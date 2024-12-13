package com.rabobank.banking.mapper;

import com.rabobank.banking.dto.AccountBalanceDTO;
import com.rabobank.banking.dto.TransferResponseDTO;
import com.rabobank.banking.dto.WithdrawalResponseDTO;
import com.rabobank.banking.model.Account;
import com.rabobank.banking.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    //AccountMapper INSTANCE = Mappers.getMapper( AccountMapper.class );

    // Map Account entity to AccountBalanceDTO
    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "balance", target = "balance")
    AccountBalanceDTO accountToAccountBalanceDTO(Account account);

    // Map Transaction and Account entities to WithdrawalResponseDTO
    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "accountNumber", source = "account.accountNumber")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "remainingBalance", source = "account.balance")
    @Mapping(target = "statusMessage", constant = "Withdrawal successful")
    WithdrawalResponseDTO toWithdrawalResponseDTO(Transaction transaction, Account account, BigDecimal amount);

    // Map Transaction and Account entities to TransferResponseDTO
    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "fromAccountNumber", source = "fromAccount.accountNumber")
    @Mapping(target = "toAccountNumber", source = "toAccount.accountNumber")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "fromAccountBalance", source = "fromAccount.balance")
    @Mapping(target = "toAccountBalance", source = "toAccount.balance")
    @Mapping(target = "statusMessage", constant = "Transfer successful")
    TransferResponseDTO toTransferResponseDTO(Transaction transaction, Account fromAccount, Account toAccount, BigDecimal amount);

}
