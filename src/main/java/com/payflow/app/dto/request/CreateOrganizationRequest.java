package com.payflow.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrganizationRequest {
	@NotBlank
	private String name;
	private String registrationNumber;
	private String address;

	@NotBlank
	private String adminUsername;

	@NotBlank
	@Email
	private String adminEmail;

	@NotBlank
	private String tempPassword;
}