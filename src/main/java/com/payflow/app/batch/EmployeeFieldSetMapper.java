package com.payflow.app.batch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.payflow.app.dto.request.BankAccountRequestDTO;
import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;

public class EmployeeFieldSetMapper implements FieldSetMapper<CreateEmployeeRequestDTO> {

	// 💡 IMPORTANT: Use the formatter that matches your CSV data (e.g.,
	// "2024-01-15")
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

	@Override
	public CreateEmployeeRequestDTO mapFieldSet(FieldSet fieldSet) throws BindException {
		
		if (fieldSet == null || fieldSet.readString(0).trim().isEmpty()) {
	        return null; // Spring Batch will skip null items
	    }
		
		System.out.println("DATAAAA : " + fieldSet);

		// 1. Handle Nested DTO: Bank Account
		// Note: Field names must match the names set in the DelimitedLineTokenizer.
		BankAccountRequestDTO bankAccountDTO = BankAccountRequestDTO.builder()
				.accountNumber(fieldSet.readString("bankAccount.accountNumber"))
				.ifsc(fieldSet.readString("bankAccount.ifsc")).build();

		// 2. Handle Date Conversion
		// Use the explicit formatter to convert the date string to LocalDate
		String dateStr = fieldSet.readString("dateOfJoining");
	    LocalDate dateOfJoining = parseFlexibleDate(dateStr);

	    LocalDate effectiveFrom = parseFlexibleDate(dateStr);
		
		EmployeeSalaryStructureRequestDTO salaryDTO = EmployeeSalaryStructureRequestDTO.builder()
		        .effectiveFrom(effectiveFrom)
		        .basic(fieldSet.readBigDecimal("basicSalary"))
		        .isCurrent(true)
		        .build();
	
		String basicStr = fieldSet.readString("basicSalary");
		
		// 3. Build the Main DTO
		return CreateEmployeeRequestDTO.builder().fullName(fieldSet.readString("fullName"))
				.email(fieldSet.readString("email")).employeeCode(fieldSet.readString("employeeCode"))
				.dateOfJoining(dateOfJoining) // The correctly parsed LocalDate
				.jobTitle(fieldSet.readString("jobTitle")).department(fieldSet.readString("department"))
				.basicSalary(fieldSet.readBigDecimal("basicSalary"))
				.aadhaarNumber(fieldSet.readString("aadhaarNumber"))
				.panNumber(fieldSet.readString("panNumber"))

				.bankAccount(bankAccountDTO)
				.salaryStructure(salaryDTO)
				.basicSalary(fieldSet.readBigDecimal("basicSalary"))
				.build();
	}
	
	private static final List<DateTimeFormatter> SUPPORTED_FORMATS = List.of(
	        DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH),
	        DateTimeFormatter.ofPattern("MM-dd-yy", Locale.ENGLISH),
	        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
	        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH),
	        DateTimeFormatter.ofPattern("d/M/yy", Locale.ENGLISH)
	);
	
	private LocalDate parseFlexibleDate(String dateStr) {
		
		if (dateStr == null || dateStr.trim().isEmpty()) {
	        return null; 
	    }
		
	    for (DateTimeFormatter formatter : SUPPORTED_FORMATS) {
	        try {
	            return LocalDate.parse(dateStr.trim(), formatter);
	        } catch (DateTimeParseException ignored) {}
	    }
	    throw new IllegalArgumentException("Unrecognized date format: " + dateStr);
	}
	
}