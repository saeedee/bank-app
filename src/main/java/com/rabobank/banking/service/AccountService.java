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
import com.rabobank.banking.model.Transaction;
import com.rabobank.banking.model.TransactionStatus;
import com.rabobank.banking.model.TransactionType;
import com.rabobank.banking.repository.AccountRepository;
import com.rabobank.banking.validator.AccountValidator;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionAuditService transactionAuditService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AccountValidator accountValidator;

    @Autowired
    private AccountMapper accountMapper;

    public List<AccountBalanceDTO> getAllBalances(){
        List<Account> accounts = accountRepository.findAll();
        log.debug("Found {} accounts.", accounts.size());
        return accounts.stream()
                .map(accountMapper::accountToAccountBalanceDTO)
                .toList();
    }

    @Transactional
    public TransferResponseDTO transferMoney(TransferRequestDTO requestTDO) {
        log.info("Starting money transfer: From account - {} To account - {} Amount - {}",
                requestTDO.getFromAccountNumber(), requestTDO.getToAccountNumber(), requestTDO.getAmount());

        Account fromAccount = accountRepository.findByAccountNumber(requestTDO.getFromAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("From account not found"));
        Account toAccount = accountRepository.findByAccountNumber(requestTDO.getToAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("To account not found"));

        log.debug("From Account Balance before transfer: {}", fromAccount.getBalance());
        log.debug("To Account Balance before transfer: {}", toAccount.getBalance());

        Card card = fromAccount.getCard();
        //get the fee
        BigDecimal fee = paymentService.calculateFee(requestTDO.getAmount(), card);
        BigDecimal totalAmount = requestTDO.getAmount().add(fee);

        //validate amount
        accountValidator.validateSufficientFunds(fromAccount, requestTDO.getAmount());

        fromAccount.setBalance(fromAccount.getBalance().subtract(totalAmount));
        toAccount.setBalance(toAccount.getBalance().add(requestTDO.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        log.info("Transfer successful: {} transferred from {} to {}", requestTDO.getAmount(),
                requestTDO.getFromAccountNumber(), requestTDO.getToAccountNumber());

        // Audit the transaction
        Transaction transaction = createTransaction(fromAccount, toAccount, totalAmount,
                TransactionType.WITHDRAWAL, TransactionStatus.SUCCESS);
        transactionAuditService.auditTransaction(transaction);

        return accountMapper.toTransferResponseDTO(transaction, fromAccount, toAccount, totalAmount);
    }

    @Transactional
    public WithdrawalResponseDTO withdrawMoney(WithdrawRequestDTO requestTDO) {
        log.info("Starting withdrawal for account: {} Amount: {}",
                requestTDO.getAccountNumber(), requestTDO.getAmount());
        Account account = accountRepository.findByAccountNumber(requestTDO.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        log.debug("Account Balance before withdrawal: {}", account.getBalance());

        Card card = account.getCard();
        // Get the fee
        BigDecimal fee = paymentService.calculateFee(requestTDO.getAmount(), card);
        BigDecimal totalAmount = requestTDO.getAmount().add(fee);

        //validate amount
        accountValidator.validateSufficientFunds(account, requestTDO.getAmount());

        account.setBalance(account.getBalance().subtract(totalAmount));
        accountRepository.save(account);

        log.info("Withdrawal successful: {} withdrawn from account {}",
                requestTDO.getAmount(), requestTDO.getAccountNumber());

        // Audit the transaction
        Transaction transaction = createTransaction(account, null, totalAmount,
                TransactionType.WITHDRAWAL, TransactionStatus.SUCCESS);
        transactionAuditService.auditTransaction(transaction);

        return accountMapper.toWithdrawalResponseDTO(transaction, account, totalAmount);
    }

    private Transaction createTransaction(Account sourceAccount, Account destinationAccount,
                                          BigDecimal amount, TransactionType type, TransactionStatus status) {
        return Transaction.builder()
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .amount(amount)
                .type(type)
                .status(status)
                .timestamp(LocalDateTime.now())
                .description(type + " transaction of " + amount)
                .build();
    }
}
