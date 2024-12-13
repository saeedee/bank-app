package com.rabobank.banking.payment;

import java.math.BigDecimal;

public interface PaymentHandler {
    BigDecimal calculateFee(BigDecimal amount);
}
