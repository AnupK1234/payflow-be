package com.payflow.app.batch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.payflow.app.dto.request.BankAccountRequestDTO;
import com.payflow.app.dto.request.CreateEmployeeRequestDTO;

public class EmployeeFieldSetMapper implements FieldSetMapper<CreateEmployeeRequestDTO> {

	// 💡 IMPORTANT: Use the formatter that matches your CSV data (e.g.,
	// "2024-01-15")
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public CreateEmployeeRequestDTO mapFieldSet(FieldSet fieldSet) throws BindException {

		// 1. Handle Nested DTO: Bank Account
		// Note: Field names must match the names set in the DelimitedLineTokenizer.
		BankAccountRequestDTO bankAccountDTO = BankAccountRequestDTO.builder()
				.accountNumber(fieldSet.readString("bankAccount.accountNumber"))
				.ifsc(fieldSet.readString("bankAccount.ifsc")).build();

		// 2. Handle Date Conversion
		// Use the explicit formatter to convert the date string to LocalDate
		LocalDate dateOfJoining = LocalDate.parse(fieldSet.readString("dateOfJoining"), DATE_FORMATTER);

		// 3. Build the Main DTO
		return CreateEmployeeRequestDTO.builder().fullName(fieldSet.readString("fullName"))
				.email(fieldSet.readString("email")).employeeCode(fieldSet.readString("employeeCode"))
				.dateOfJoining(dateOfJoining) // The correctly parsed LocalDate
				.jobTitle(fieldSet.readString("jobTitle")).department(fieldSet.readString("department"))

				// NOTE: The next two fields show scientific notation in your raw data
				// We use readString() to maintain their full value and prevent loss of
				// precision.
				.aadhaarNumber(fieldSet.readString("aadhaarNumber")).panNumber(fieldSet.readString("panNumber"))

				.organizationId(fieldSet.readLong("organizationId")).bankAccount(bankAccountDTO).build();
	}
}