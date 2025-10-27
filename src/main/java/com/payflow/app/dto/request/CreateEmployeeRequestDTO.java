package com.payflow.app.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeRequestDTO {

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

	@Size(min = 12, max = 12)
	private String aadhaarNumber;

	@Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
	private String panNumber;

	private BigDecimal basicSalary;
	
	private EmployeeSalaryStructureRequestDTO salaryStructure;

	@Valid
	private BankAccountRequestDTO bankAccount;
}
