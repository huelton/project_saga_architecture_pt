package com.saga.audit.repository;

import com.saga.audit.document.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    List<AuditLog> findByTransferId(String transferId);
}
