package com.payflow.app.dto.response;

import lombok.Data;

@Data
public class UserResponse {
	private Long id;
	private String username;
	private String email;
	private String role;
	private Long organizationId;
	private Boolean mustResetPassword;
}