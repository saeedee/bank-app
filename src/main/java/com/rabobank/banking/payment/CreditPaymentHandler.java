package com.rabobank.banking.payment;

import java.math.BigDecimal;

public class CreditPaymentHandler implements PaymentHandler {

    private static final BigDecimal CREDIT_CARD_FEE_PERCENTAGE = BigDecimal.valueOf(0.01);

    @Override
    public BigDecimal calculateFee(BigDecimal amount) {
        // Apply 1% fee for credit card transactions
        return amount.multiply(CREDIT_CARD_FEE_PERCENTAGE);
    }
}
