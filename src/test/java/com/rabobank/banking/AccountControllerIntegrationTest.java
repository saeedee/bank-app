package com.rabobank.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.banking.controller.AccountController;
import com.rabobank.banking.dto.AccountBalanceDTO;
import com.rabobank.banking.dto.TransferRequestDTO;
import com.rabobank.banking.dto.TransferResponseDTO;
import com.rabobank.banking.dto.WithdrawRequestDTO;
import com.rabobank.banking.dto.WithdrawalResponseDTO;
import com.rabobank.banking.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks  // Inject the mock into the AccountController
    private AccountController accountController;

    @Test
    @WithMockUser(roles = "USER")  // Mock a user with the 'USER' role
    void testGetAllBalances() throws Exception {
        AccountBalanceDTO balanceDTO1 = new AccountBalanceDTO("324324", BigDecimal.valueOf(500));
        AccountBalanceDTO balanceDTO2 = new AccountBalanceDTO("2", BigDecimal.valueOf(1000));
        when(accountService.getAllBalances()).thenReturn(List.of(balanceDTO1, balanceDTO2));

        mockMvc.perform(get("/accounts/balance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountNumber").value("324324"))
                .andExpect(jsonPath("$[0].balance").value(500))
                .andExpect(jsonPath("$[1].accountNumber").value("2"))
                .andExpect(jsonPath("$[1].balance").value(1000));
    }

    @Test
    @WithMockUser(roles = "USER")  // Mock a user with the 'USER' role
    void testTransferMoney() throws Exception {
        TransferRequestDTO requestDTO = new TransferRequestDTO("ACC1", "ACC2", BigDecimal.valueOf(200));
        TransferResponseDTO responseDTO = new TransferResponseDTO(1L, "ACC1", "ACC2", BigDecimal.valueOf(200),
                BigDecimal.valueOf(500), BigDecimal.valueOf(1200), "Transfer successful");
        when(accountService.transferMoney(any(TransferRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusMessage").value("Transfer successful"))
                .andExpect(jsonPath("$.fromAccountNumber").value("ACC1"))
                .andExpect(jsonPath("$.toAccountNumber").value("ACC2"))
                .andExpect(jsonPath("$.amount").value(200))
                .andExpect(jsonPath("$.fromAccountBalance").value(500))
                .andExpect(jsonPath("$.toAccountBalance").value(1200));
    }

    @Test
    @WithMockUser(roles = "USER")  // Mock a user with the 'USER' role
    void testWithdrawMoney() throws Exception {
        WithdrawRequestDTO requestDTO = new WithdrawRequestDTO("ACC1", BigDecimal.valueOf(300));
        WithdrawalResponseDTO responseDTO = new WithdrawalResponseDTO(1L, "ACC1", BigDecimal.valueOf(300),
                BigDecimal.valueOf(200), "Withdrawal successful");
        when(accountService.withdrawMoney(any(WithdrawRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusMessage").value("Withdrawal successful"))
                .andExpect(jsonPath("$.accountNumber").value("ACC1"))
                .andExpect(jsonPath("$.amount").value(300))
                .andExpect(jsonPath("$.remainingBalance").value(200));
    }
}
