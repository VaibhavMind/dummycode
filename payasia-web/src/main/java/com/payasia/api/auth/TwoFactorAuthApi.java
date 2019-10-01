package com.payasia.api.auth;

import org.springframework.http.ResponseEntity;

/**
 * @author manojkumar
 * @param: Two Factor Authentication
*/
public interface TwoFactorAuthApi {

	ResponseEntity<?> sendOtp();

	ResponseEntity<?> validateOtp(String otpValue);

	ResponseEntity<?> changeTwoFactorStatus();

	
}
