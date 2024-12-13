package com.rabobank.banking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequestDTO {
    private String username;
    private String password;

}
