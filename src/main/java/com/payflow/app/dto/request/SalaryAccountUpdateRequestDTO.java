package com.payflow.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SalaryAccountUpdateRequestDTO {

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    private String ifscCode;

    private String additionalInfo; // optional
}
