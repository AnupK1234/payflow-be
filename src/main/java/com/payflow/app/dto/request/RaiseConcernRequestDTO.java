package com.payflow.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaiseConcernRequestDTO {

	@NotNull
	private Long employeeId;

	@NotNull
	private Long organizationId;

	@NotBlank
	private String description;

	private String attachmentUrl; // Optional
}
