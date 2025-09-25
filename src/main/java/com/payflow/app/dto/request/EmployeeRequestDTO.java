package com.payflow.app.dto.request;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDTO {

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String employeeCode;

    @NotNull
    private LocalDate dateOfJoining;

    @NotBlank
    private String jobTitle;

    @NotBlank
    private String department;

    @NotBlank
    private String status; 

  

    @Size(min = 12, max = 12)
    private String aadhaarNumber;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
    private String panNumber;

    @NotNull
    private Long organizationId; 
    
    @Valid
    private BankAccountRequestDTO bankAccount;   
}
