package com.payflow.app.controller;

import org.modelmapper.ModelMapper;
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
import com.payflow.app.dto.response.UserResponse;
import com.payflow.app.entity.User;
import com.payflow.app.enums.Status;
import com.payflow.app.exception.AccountAccessException;
import com.payflow.app.security.jwt.JwtService;
import com.payflow.app.service.PasswordResetService;
import com.payflow.app.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Handles user authentication, password reset, and OTP verification processes. This includes login, password reset, OTP verification, and password resetting functionalities.")
public class AuthController {

	private final UserDetailsService userDetailsService;
	private final ModelMapper modelMapper;
	private final PasswordResetService passwordResetService;
	private final UserServiceImpl userService;

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@PostMapping("/login")
	@Operation(summary = "Login user", description = "Authenticates a user with username and password and returns a JWT token for future requests.")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {
	    Authentication auth = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

	    UserDetails userDetails = (UserDetails) auth.getPrincipal();
	    User userEntity = userService.findByUsername(userDetails.getUsername());

	    
	    switch (userEntity.getRole()) {

	        
	        case ORG_ADMIN -> {
	            if (userEntity.getOrganization() == null 
	                || userEntity.getOrganization().getStatus() != Status.VERIFIED) {
	                throw new AccountAccessException("YOUR ACCOUNT IS NOT YET VERIFIED");
	            }
	        }

	       
	        case EMPLOYEE -> {
	            if (userEntity.getEmployee() == null 
	                || !"ACTIVE".equalsIgnoreCase(userEntity.getEmployee().getStatus())) {
	                throw new AccountAccessException("YOUR ACCOUNT IS NOT ACTIVE");
	            }
	        }

	       
	        case CLIENT -> {
	            if (userEntity.getClient() == null 
	                || !"ACTIVE".equalsIgnoreCase(userEntity.getClient().getStatus())) {
	                throw new AccountAccessException("YOUR ACCOUNT IS NOT ACTIVE");
	            }
	        }

	        
	        default -> {
	            
	        }
	    }

	    
	    String token = jwtService.generateToken(userDetails, 3600000); 
	    UserResponse userDto = modelMapper.map(userEntity, UserResponse.class);

	    return ResponseEntity.ok(new LoginResponse(token, userDto));
	}



	@PostMapping("/forgot-password")
	@Operation(summary = "Forgot password", description = "Requests a password reset by sending a One-Time Password (OTP) to the user's email address.")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest req) {
		passwordResetService.requestPasswordReset(req);
		return ResponseEntity.ok("Password reset OTP sent to email");
	}

	@PostMapping("/verify-otp")
	@Operation(summary = "Verify OTP", description = "Verifies the OTP sent to the user's email address for password reset.")
	public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest req) {
		boolean valid = passwordResetService.verifyOtp(req);
		return ResponseEntity.ok(valid ? "OTP valid" : "OTP invalid");
	}

	@PostMapping("/reset-password")
	@Operation(summary = "Reset password", description = "Resets the user's password after verifying the OTP.")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest req) {
		passwordResetService.resetPassword(req);
		return ResponseEntity.ok("Password successfully reset");
	}

	record TokenResponse(String token, String username, String role) {
	}

	record LoginResponse(String token, UserResponse user) {
	}
}
