package com.rabobank.banking.validator;

import com.rabobank.banking.exception.InsufficientFundsException;
import com.rabobank.banking.model.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountValidator {

    public void validateSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds for operation");
        }
    }

}
