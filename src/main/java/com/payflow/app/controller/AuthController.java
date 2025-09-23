package com.payflow.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.AuthRequest;
import com.payflow.app.dto.request.ForgotPasswordRequest;
import com.payflow.app.dto.request.ResetPasswordRequest;
import com.payflow.app.dto.request.VerifyOtpRequest;
import com.payflow.app.security.jwt.JwtService;
import com.payflow.app.service.PasswordResetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserDetailsService userDetailsService;
	private final PasswordResetService passwordResetService;

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		UserDetails user = (UserDetails) auth.getPrincipal();

		String token = jwtService.generateToken(user, 3600000); // 1 hour

		return ResponseEntity.ok(
				new TokenResponse(token, user.getUsername(), user.getAuthorities().iterator().next().getAuthority()));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest req) {
		passwordResetService.requestPasswordReset(req);
		return ResponseEntity.ok("Password reset OTP sent to email");
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest req) {
		boolean valid = passwordResetService.verifyOtp(req);
		return ResponseEntity.ok(valid ? "OTP valid" : "OTP invalid");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest req) {
		passwordResetService.resetPassword(req);
		return ResponseEntity.ok("Password successfully reset");
	}

	record TokenResponse(String token, String username, String role) {
	}
}
