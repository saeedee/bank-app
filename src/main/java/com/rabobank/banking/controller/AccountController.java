package com.rabobank.banking.controller;

import com.rabobank.banking.dto.AccountBalanceDTO;
import com.rabobank.banking.dto.TransferRequestDTO;
import com.rabobank.banking.dto.TransferResponseDTO;
import com.rabobank.banking.dto.WithdrawRequestDTO;
import com.rabobank.banking.dto.WithdrawalResponseDTO;
import com.rabobank.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account API", description = "Account operations")
public class AccountController {


    @Autowired
    private AccountService accountService;

    @GetMapping("/balance")
    @Operation(summary = "Get all balances", operationId = "getAllBalances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the balances"),
            @ApiResponse(responseCode = "404", description = "The balance does not exist"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AccountBalanceDTO>> getAllBalances() {
        List<AccountBalanceDTO> balances = accountService.getAllBalances();
        return ResponseEntity.ok(balances);
    }


    @PostMapping("/transfer")
    @Operation(summary = "Transfer Money from one account to another account", operationId = "transferMoney")
    public ResponseEntity<TransferResponseDTO> transferMoney(@Valid @RequestBody TransferRequestDTO requestTDO) {
        TransferResponseDTO transferResponseDTO = accountService.transferMoney(requestTDO);
        return ResponseEntity.ok(transferResponseDTO);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "withdraw Money from a account", operationId = "withdrawMoney")
    public ResponseEntity<WithdrawalResponseDTO> withdrawMoney(@Valid @RequestBody WithdrawRequestDTO requestTDO) {
        WithdrawalResponseDTO withdrawalResponseDTO = accountService.withdrawMoney(requestTDO);
        return ResponseEntity.ok(withdrawalResponseDTO);
    }
}
