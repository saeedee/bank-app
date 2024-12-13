package com.rabobank.banking.service;

import com.rabobank.banking.model.Transaction;
import com.rabobank.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TransactionAuditService {

    @Autowired
    private TransactionRepository transactionRepository;

    public void auditTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }


}
