package com.payflow.app.dto.request;

import com.payflow.app.enums.ConcernStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateConcernStatusRequestDTO {
	@NotNull
	private ConcernStatus status;
}
