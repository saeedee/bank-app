package com.rabobank.banking.service;

import com.rabobank.banking.model.Transaction;
import com.rabobank.banking.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionAuditServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionAuditService transactionAuditService;

    @Test
    void testAuditTransaction() {
        Transaction transaction = new Transaction();

        transactionAuditService.auditTransaction(transaction);

        verify(transactionRepository, times(1)).save(transaction);
    }
}
