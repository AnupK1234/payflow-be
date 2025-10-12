package com.payflow.app.dto.response;

import lombok.Data;

@Data
public class UserResponse {
	private Long id;
	private String username;
	private String email;
	private Boolean enabled;
	private String role;
	private Long clientId;
	private Long employeeId;
	private Long organizationId;
	private Long vendorId;
	private Boolean mustResetPassword;
}