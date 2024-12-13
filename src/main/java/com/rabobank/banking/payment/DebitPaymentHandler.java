package com.rabobank.banking.payment;

import java.math.BigDecimal;

public class DebitPaymentHandler implements PaymentHandler {
    @Override
    public BigDecimal calculateFee(BigDecimal amount) {
        return BigDecimal.ZERO;
    }
}
