package com.payflow.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AuthResponse {
	private String token;
	private Long userId;
	private String username;
	private String email;
	private String role;
}
