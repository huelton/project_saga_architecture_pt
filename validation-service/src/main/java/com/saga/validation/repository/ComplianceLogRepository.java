package com.saga.validation.repository;

import com.saga.validation.document.ComplianceLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ComplianceLogRepository extends MongoRepository<ComplianceLog, String> {

    List<ComplianceLog> findByTransferId(String transferId);
}
