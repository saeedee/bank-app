package com.rabobank.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponseDTO {
    private Long transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal remainingBalance;
    private String statusMessage;
}
