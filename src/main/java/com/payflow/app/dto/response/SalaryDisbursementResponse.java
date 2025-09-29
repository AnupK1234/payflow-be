package com.payflow.app.dto.response;

import java.time.LocalDateTime;

import com.payflow.app.enums.DisbursementStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SalaryDisbursementResponse {
	private Long id;
	private LocalDateTime requestDate;
	private DisbursementStatus status;
	private Long organizationId;
	private Long createdById;
	private Long approvedById;
	private LocalDateTime approvedAt;
}
