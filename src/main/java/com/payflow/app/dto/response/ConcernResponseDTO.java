package com.payflow.app.dto.response;

import java.time.LocalDateTime;

import com.payflow.app.enums.ConcernStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcernResponseDTO {
	private Long id;
	private Long employeeId;
	private Long organizationId;
	private String description;
	private String attachmentUrl;
	private ConcernStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
