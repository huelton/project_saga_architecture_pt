#!/usr/bin/env python3
"""
Commit Scheduler — Envio incremental de commits com intervalo configurável.

Envia commits definidos em commits.json para o remoto (ex.: origin/main),
respeitando intervalo de 2h + jitter (10–30 min) no modo agendado.
Disparos apenas em horário comercial: 08:00 às 19:00, segunda a sábado (horário local).
Usa horário local. Não adiciona co-author. Pode ser executado manualmente
ou em loop (--schedule).

Uso:
  python commit_scheduler.py              # próximo commit agora (manual)
  python commit_scheduler.py --manual      # idem
  python commit_scheduler.py --schedule    # loop: commit -> aguardar 2h+jitter -> repetir
  python commit_scheduler.py --status     # mostra último commit enviado e próximo
"""

from __future__ import annotations

import argparse
import json
import os
import random
import subprocess
import sys
from datetime import datetime, timedelta
from pathlib import Path

# --- Configuração ---
REPO_ROOT = Path(__file__).resolve().parent.parent
COMMITS_FILE = REPO_ROOT / "commits.json"
STATE_FILE = REPO_ROOT / ".strategy_state.json"
INTERVAL_BASE_MINUTES = 1.5 * 60  # 1.5 horas
JITTER_MIN_MINUTES = 10
JITTER_MAX_MINUTES = 30

# Horário comercial: disparos apenas neste intervalo (horário local)
BUSINESS_HOUR_START = 8   # 08:00
BUSINESS_HOUR_END = 19    # até 19:00 (exclusive: 18:59 é o último minuto)
BUSINESS_DAYS = (0, 1, 2, 3, 4, 5)  # segunda=0 a sábado=5 (domingo=6 fora)
CHECK_BUSINESS_INTERVAL_WEEKEND_SECONDS = 60 * 60  # fora do horário comercial: recheca a cada 60 min


def load_commits() -> list[dict]:
    """Carrega a lista de commits a partir de commits.json."""
    if not COMMITS_FILE.exists():
        print(f"Erro: {COMMITS_FILE} não encontrado.", file=sys.stderr)
        sys.exit(1)
    with open(COMMITS_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)
    if not isinstance(data, list):
        print("Erro: commits.json deve ser uma lista de objetos.", file=sys.stderr)
        sys.exit(1)
    return data


def load_state() -> dict:
    """Carrega o estado do último commit enviado."""
    if not STATE_FILE.exists():
        return {"last_pushed_index": -1, "last_push_timestamp": None, "last_pushed_message": None}
    try:
        with open(STATE_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    except (json.JSONDecodeError, OSError):
        return {"last_pushed_index": -1, "last_push_timestamp": None, "last_pushed_message": None}


def save_state(index: int, message: str) -> None:
    """Persiste o índice e a mensagem do último commit enviado (horário local)."""
    now = datetime.now().isoformat()
    state = {
        "last_pushed_index": index,
        "last_push_timestamp": now,
        "last_pushed_message": message,
    }
    with open(STATE_FILE, "w", encoding="utf-8") as f:
        json.dump(state, f, indent=2, ensure_ascii=False)


def run_git(args: list[str], cwd: Path | None = None) -> subprocess.CompletedProcess:
    """Executa um comando git no repositório."""
    cmd = ["git"] + args
    return subprocess.run(
        cmd,
        cwd=cwd or REPO_ROOT,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )


def get_next_commit_index(commits: list[dict], state: dict) -> int | None:
    """Retorna o índice do próximo commit a enviar ou None se não houver mais."""
    last = state.get("last_pushed_index", -1)
    next_index = last + 1
    if next_index >= len(commits):
        return None
    return next_index


def push_one_commit(commits: list[dict], index: int) -> tuple[bool, bool]:
    """
    Adiciona os arquivos do commit, faz o commit (sem co-author) e envia.
    Retorna (sucesso, skipped): skipped=True quando não havia nada a commitar (já commitado).
    """
    spec = commits[index]
    message = spec.get("message", "").strip()
    files = spec.get("files") or []

    if not message:
        print("Erro: mensagem do commit vazia.", file=sys.stderr)
        return (False, False)

    # Resolve caminhos relativos ao repositório
    paths = []
    for p in files:
        path = (REPO_ROOT / p).resolve()
        if not path.exists():
            print(f"Aviso: arquivo não encontrado (será ignorado): {path}", file=sys.stderr)
            continue
        try:
            rel = path.relative_to(REPO_ROOT)
        except ValueError:
            continue
        paths.append(str(rel).replace("\\", "/"))

    if not paths:
        print("Erro: nenhum arquivo válido para este commit.", file=sys.stderr)
        return (False, False)

    # git add <files>
    add_result = run_git(["add"] + paths, cwd=REPO_ROOT)
    if add_result.returncode != 0:
        print(f"Erro ao adicionar arquivos: {add_result.stderr}", file=sys.stderr)
        return (False, False)

    # git commit -m "message" (sem co-author)
    commit_result = run_git(["commit", "-m", message], cwd=REPO_ROOT)
    if commit_result.returncode != 0:
        out_and_err = (commit_result.stdout + " " + commit_result.stderr).lower()
        if "nothing to commit" in out_and_err or "working tree clean" in out_and_err:
            # Arquivos já commitados: avança o estado para não travar ao reexecutar
            print("Nada a commitar (arquivos já commitados). Avançando estado.", file=sys.stderr)
            return (True, True)
        print(f"Erro ao commitar: {commit_result.stderr or commit_result.stdout}", file=sys.stderr)
        return (False, False)

    # git push (usa branch atual; remoto padrão)
    push_result = run_git(["push"], cwd=REPO_ROOT)
    if push_result.returncode != 0:
        print(f"Erro ao enviar: {push_result.stderr or push_result.stdout}", file=sys.stderr)
        return (False, False)

    return (True, False)


def is_business_hours(dt: datetime | None = None) -> bool:
    """True se estiver em horário comercial: 08:00–19:00, segunda a sábado (horário local)."""
    t = dt or datetime.now()
    if t.weekday() not in BUSINESS_DAYS:
        return False
    return BUSINESS_HOUR_START <= t.hour < BUSINESS_HOUR_END


def wait_until_business_hours() -> None:
    """Aguarda em loop até entrar no horário comercial (recheca a cada CHECK_BUSINESS_INTERVAL_WEEKEND_SECONDS)."""
    import time
    interval = CHECK_BUSINESS_INTERVAL_WEEKEND_SECONDS
    while not is_business_hours():
        now = datetime.now()
        print(f"[{now.strftime('%Y-%m-%d %H:%M:%S')}] Fora do horário comercial (08:00–19:00, seg–sáb). Próxima checagem em {interval // 60} min...")
        time.sleep(interval)


def next_interval_seconds() -> int:
    """Intervalo até o próximo envio: 2h + aleatório entre 10 e 30 minutos (em segundos)."""
    jitter = random.randint(JITTER_MIN_MINUTES, JITTER_MAX_MINUTES)
    return (INTERVAL_BASE_MINUTES + jitter) * 60


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Envia commits incrementais (manual ou agendado). Horário local, sem co-author."
    )
    parser.add_argument(
        "--manual",
        action="store_true",
        help="Envia apenas o próximo commit e encerra (padrão).",
    )
    parser.add_argument(
        "--schedule",
        action="store_true",
        help="Modo agendado: envia um commit, aguarda 2h+jitter, repete até acabar.",
    )
    parser.add_argument(
        "--status",
        action="store_true",
        help="Mostra último commit enviado e próximo da lista.",
    )
    args = parser.parse_args()

    if args.status:
        commits = load_commits()
        state = load_state()
        last = state.get("last_pushed_index", -1)
        next_idx = get_next_commit_index(commits, state)
        print(f"Total de commits na lista: {len(commits)}")
        print(f"Último enviado (índice): {last}")
        if last >= 0 and last < len(commits):
            print(f"Última mensagem: {commits[last].get('message', '')}")
        if next_idx is not None:
            print(f"Próximo (índice): {next_idx}")
            print(f"Próxima mensagem: {commits[next_idx].get('message', '')}")
        else:
            print("Não há próximo commit (todos já enviados).")
        return

    use_schedule = args.schedule
    commits = load_commits()
    state = load_state()

    while True:
        next_index = get_next_commit_index(commits, state)
        if next_index is None:
            print("Todos os commits já foram enviados.")
            break

        # Em modo agendado, disparar apenas em horário comercial (08:00–19:00, seg–sáb)
        if use_schedule:
            wait_until_business_hours()

        spec = commits[next_index]
        message = spec.get("message", "")
        print(f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] Enviando commit {next_index + 1}/{len(commits)}: {message[:60]}...")

        success, skipped = push_one_commit(commits, next_index)
        if success:
            save_state(next_index, message)
            state = load_state()  # recarrega para a próxima iteração (--schedule) usar o índice correto
            if skipped:
                print(f"Commit já existente; estado atualizado. Próximo: {next_index + 2}/{len(commits)}")
            else:
                print(f"Commit enviado: {message}")
        else:
            print("Falha no envio. Encerrando.", file=sys.stderr)
            sys.exit(1)

        if not use_schedule:
            break

        delay = next_interval_seconds()
        next_run = datetime.now() + timedelta(seconds=delay)
        print(f"Próximo envio em {delay // 60} min (por volta de {next_run.strftime('%H:%M')}). Aguardando...")
        try:
            import time
            time.sleep(delay)
        except KeyboardInterrupt:
            print("\nInterrompido pelo usuário.")
            break

    print("Concluído.")


if __name__ == "__main__":
    main()
