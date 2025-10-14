package com.payflow.app.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryAccountUpdateRequestResponseDTO {
	private Long id;
	private Long requestId;
	private Long employeeId;
	private String employeeName;
	private String additionalInfo;
	private String bankName;
	private String accountNumber;
	private String ifscCode;

	private String status; // PENDING, APPROVED, REJECTED
	private LocalDateTime requestedAt;
	private LocalDateTime processedAt;
	private Long approvedBy;
	private Long orgId;
	private String orgName;
}
