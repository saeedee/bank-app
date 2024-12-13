package com.rabobank.banking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    @NotNull(message = "Sender account number is required")
    private String fromAccountNumber;

    @NotNull(message = "Receiver account number is required")
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount should be greater than 0")
    private BigDecimal amount;


}
