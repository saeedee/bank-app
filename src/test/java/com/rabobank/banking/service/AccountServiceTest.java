package com.rabobank.banking.service;

import com.rabobank.banking.dto.AccountBalanceDTO;
import com.rabobank.banking.dto.TransferRequestDTO;
import com.rabobank.banking.dto.TransferResponseDTO;
import com.rabobank.banking.dto.WithdrawRequestDTO;
import com.rabobank.banking.dto.WithdrawalResponseDTO;
import com.rabobank.banking.exception.AccountNotFoundException;
import com.rabobank.banking.mapper.AccountMapper;
import com.rabobank.banking.model.Account;
import com.rabobank.banking.model.Card;
import com.rabobank.banking.model.CardType;
import com.rabobank.banking.model.Transaction;
import com.rabobank.banking.model.TransactionStatus;
import com.rabobank.banking.model.TransactionType;
import com.rabobank.banking.repository.AccountRepository;
import com.rabobank.banking.validator.AccountValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionAuditService transactionAuditService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private AccountValidator accountValidator;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testGetAllBalances() {
        Account account1 = new Account();
        account1.setBalance(BigDecimal.valueOf(1000));
        Account account2 = new Account();
        account2.setBalance(BigDecimal.valueOf(2000));

        when(accountRepository.findAll()).thenReturn(List.of(account1, account2));

        AccountBalanceDTO dto1 = new AccountBalanceDTO();
        AccountBalanceDTO dto2 = new AccountBalanceDTO();
        when(accountMapper.accountToAccountBalanceDTO(account1)).thenReturn(dto1);
        when(accountMapper.accountToAccountBalanceDTO(account2)).thenReturn(dto2);

        List<AccountBalanceDTO> result = accountService.getAllBalances();

        assertEquals(2, result.size());
        verify(accountMapper, times(1)).accountToAccountBalanceDTO(account1);
        verify(accountMapper, times(1)).accountToAccountBalanceDTO(account2);
    }

    @Test
    void testTransferMoney() {
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setFromAccountNumber("ACC1");
        requestDTO.setToAccountNumber("ACC2");
        requestDTO.setAmount(BigDecimal.valueOf(500));

        Card card = new Card();
        card.setId(1L);
        card.setCardType(CardType.CREDIT);

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setAccountNumber("ACC1");
        fromAccount.setBalance(BigDecimal.valueOf(1000));
        fromAccount.setCard(card);

        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setAccountNumber("ACC2");
        toAccount.setBalance(BigDecimal.valueOf(500));

        when(accountRepository.findByAccountNumber("ACC1")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC2")).thenReturn(Optional.of(toAccount));
        when(paymentService.calculateFee(BigDecimal.valueOf(500), fromAccount.getCard())).thenReturn(BigDecimal.valueOf(5));

        doNothing().when(accountValidator).validateSufficientFunds(fromAccount, BigDecimal.valueOf(500));

        // Capture the created transaction using ArgumentCaptor
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Mock mapper response
        when(accountMapper.toTransferResponseDTO(any(Transaction.class), eq(fromAccount), eq(toAccount), eq(BigDecimal.valueOf(505))))
                .thenReturn(new TransferResponseDTO());

        TransferResponseDTO result = accountService.transferMoney(requestDTO);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(495), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(1000), toAccount.getBalance());
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionAuditService, times(1)).auditTransaction(any(Transaction.class));
    }

    @Test
    void testWithdrawMoney() {
        // Set up request data
        WithdrawRequestDTO requestDTO = new WithdrawRequestDTO();
        requestDTO.setAccountNumber("ACC1");
        requestDTO.setAmount(BigDecimal.valueOf(300));

        // Set up card and account
        Card card = new Card();
        card.setId(1L);
        card.setCardType(CardType.CREDIT);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC1");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setCard(card);

        // Mock repository and services
        when(accountRepository.findByAccountNumber("ACC1")).thenReturn(Optional.of(account));
        when(paymentService.calculateFee(BigDecimal.valueOf(300), card)).thenReturn(BigDecimal.valueOf(3));
        doNothing().when(accountValidator).validateSufficientFunds(account, BigDecimal.valueOf(300));

        // Capture the created transaction using ArgumentCaptor
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Mock mapper response
        when(accountMapper.toWithdrawalResponseDTO(any(Transaction.class), eq(account), eq(BigDecimal.valueOf(303))))
                .thenReturn(new WithdrawalResponseDTO());

        // Call the method under test
        WithdrawalResponseDTO result = accountService.withdrawMoney(requestDTO);

        // Assert response and side effects
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(697), account.getBalance()); // 1000 - 300 - 3

        // Verify interactions
        verify(accountRepository, times(1)).save(account);
        verify(transactionAuditService, times(1)).auditTransaction(transactionCaptor.capture());

        // Validate the transaction
        Transaction auditedTransaction = transactionCaptor.getValue();
        assertNotNull(auditedTransaction);
        assertEquals(account, auditedTransaction.getSourceAccount());
        assertEquals(BigDecimal.valueOf(303), auditedTransaction.getAmount());
        assertEquals(TransactionType.WITHDRAWAL, auditedTransaction.getType());
        assertEquals(TransactionStatus.SUCCESS, auditedTransaction.getStatus());
    }

    @Test
    void testTransferMoneyThrowsAccountNotFoundException() {
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setFromAccountNumber("ACC1");
        requestDTO.setToAccountNumber("ACC2");

        when(accountRepository.findByAccountNumber("ACC1")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.transferMoney(requestDTO));
    }

    @Test
    void testWithdrawMoneyThrowsAccountNotFoundException() {
        WithdrawRequestDTO requestDTO = new WithdrawRequestDTO();
        requestDTO.setAccountNumber("ACC1");

        when(accountRepository.findByAccountNumber("ACC1")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.withdrawMoney(requestDTO));
    }
}