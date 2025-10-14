package com.payflow.app.dto.request;
import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UpdateEmployeeRequestDTO {
    private String fullName;

    @Email
    private String email;
    private String employeeCode;
    private LocalDate dateOfJoining;
    private String jobTitle;
    private String department;
    private String status;
    
    @Size(min = 12, max = 12)
    private String aadhaarNumber;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
    private String panNumber;
    
    @Valid
    private BankAccountRequestDTO bankAccount;   
}
