package com.payflow.app.service;

import com.payflow.app.dto.request.ForgotPasswordRequest;
import com.payflow.app.dto.request.ResetPasswordRequest;
import com.payflow.app.dto.request.VerifyOtpRequest;

public interface PasswordResetService {
	void requestPasswordReset(ForgotPasswordRequest request);

	boolean verifyOtp(VerifyOtpRequest request);

	void resetPassword(ResetPasswordRequest request);
}
