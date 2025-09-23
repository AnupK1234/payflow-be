package com.payflow.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.PasswordResetToken;
import com.payflow.app.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	Optional<PasswordResetToken> findByUserAndOtpAndUsedFalse(User user, String otp);

	void deleteByUser(User user);
}
