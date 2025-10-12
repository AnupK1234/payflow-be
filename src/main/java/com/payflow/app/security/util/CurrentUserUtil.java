package com.payflow.app.security.util;

import org.springframework.stereotype.Component;

import com.payflow.app.dto.response.UserResponse;
import com.payflow.app.security.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserUtil {

	private final JwtService jwtService;

	public UserResponse getCurrentUser(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				return jwtService.extractUser(token);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
