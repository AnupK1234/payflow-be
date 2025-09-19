package com.payflow.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
	@NotBlank
	private String username;
	
	@NotBlank
	private String currentPassword;
	
	@NotBlank
	private String newPassword;
}