package com.payflow.app.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.payflow.app.dto.request.ForgotPasswordRequest;
import com.payflow.app.dto.request.ResetPasswordRequest;
import com.payflow.app.dto.request.VerifyOtpRequest;
import com.payflow.app.entity.PasswordResetToken;
import com.payflow.app.entity.User;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.PasswordResetTokenRepository;
import com.payflow.app.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

	private final UserRepository userRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final EmailService emailService;
	private final BCryptPasswordEncoder encoder;

	@Override
	@Transactional
	public void requestPasswordReset(ForgotPasswordRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NotFoundException("User not found"));

		// Remove old tokens
		tokenRepository.deleteByUser(user);

		// Generate OTP (6-digit)
		String otp = String.format("%06d", new Random().nextInt(999999));
		PasswordResetToken token = PasswordResetToken.builder().user(user).otp(otp)
				.expiryTime(LocalDateTime.now().plusMinutes(10)).used(false).build();
		tokenRepository.save(token);

		// Send email
		Context ctx = new Context();
		ctx.setVariable("username", user.getUsername());
		ctx.setVariable("otp", otp);
		ctx.setVariable("expiry", "10 minutes");

		emailService.sendEmailWithTemplate(user.getEmail(), "Password Reset Request", "forgot-password-template.html",
				ctx);
	}

	@Override
	public boolean verifyOtp(VerifyOtpRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NotFoundException("User not found"));

		PasswordResetToken token = tokenRepository.findByUserAndOtpAndUsedFalse(user, request.getOtp())
				.orElseThrow(() -> new NotFoundException("Invalid or expired OTP"));

		if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP expired");
		}
		return true;
	}

	@Override
	public void resetPassword(ResetPasswordRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NotFoundException("User not found"));

		PasswordResetToken token = tokenRepository.findByUserAndOtpAndUsedFalse(user, request.getOtp())
				.orElseThrow(() -> new NotFoundException("Invalid or expired OTP"));

		if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP expired");
		}

		// Update password
		user.setPasswordHash(encoder.encode(request.getNewPassword()));
		userRepository.save(user);

		// Mark OTP as used
		token.setUsed(true);
		tokenRepository.save(token);
	}
}
