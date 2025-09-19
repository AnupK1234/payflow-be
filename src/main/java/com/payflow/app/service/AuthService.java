package com.payflow.app.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.payflow.app.dto.response.UserResponse;
import com.payflow.app.entity.User;
import com.payflow.app.exception.ApiException;
import com.payflow.app.repository.UserRepository;
import com.payflow.app.security.jwt.JwtUtil;

@Service
public class AuthService {
	private final UserRepository userRepo;
	private final BCryptPasswordEncoder encoder;
	// private final JwtUtil jwtUtil;

	public AuthService(UserRepository userRepo, BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
		this.userRepo = userRepo;
		this.encoder = encoder;
		this.jwtUtil = jwtUtil;
	}

	public User authenticate(String username, String password) {
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
		if (!encoder.matches(password, user.getPasswordHash()))
			throw new BadCredentialsException("Invalid credentials");
		if (!user.getEnabled())
			throw new ApiException.Forbidden("User disabled");
		return user;
	}

	public String issueToken(User user) {
		return jwtUtil.generateToken(user);
	}

	public void resetPassword(String username, String currentPassword, String newPassword) {
		User user = userRepo.findByUsername(username).orElseThrow(() -> new ApiException.NotFound("User not found"));
		if (!encoder.matches(currentPassword, user.getPasswordHash()))
			throw new ApiException.BadRequest("Current password incorrect");
		user.setPasswordHash(encoder.encode(newPassword));
		user.setMustResetPassword(false);
		userRepo.save(user);
	}

	public static UserResponse toUserResponse(User u) {
		UserResponse res = new UserResponse();
		res.setId(u.getId());
		res.setUsername(u.getUsername());
		res.setEmail(u.getEmail());
		res.setRole(u.getRole());
		res.setOrganizationId(u.getOrganization() != null ? u.getOrganization().getId() : null);
		res.setMustResetPassword(u.getMustResetPassword());
		return res;
	}
}
