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
public class TransferResponseDTO {
    private Long transactionId;   // Unique identifier for the transaction
    private String fromAccountNumber;   // Account number of the sender
    private String toAccountNumber;     // Account number of the receiver
    private BigDecimal amount;      // Amount transferred
    private BigDecimal fromAccountBalance;  // Sender's remaining balance
    private BigDecimal toAccountBalance;    // Receiver's balance after transfer
    private String statusMessage;   // Message indicating the status of the transaction (e.g., "Transfer successful")

}
