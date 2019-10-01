package com.payasia.logic.impl;

import javax.annotation.Resource;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

import com.payasia.common.exception.PayAsiaBusinessException;
import com.payasia.logic.SecurityLogic;
import com.payasia.logic.util.PayAsiaPaswordEncoder;

@Component
public class SecurityLogicImpl implements SecurityLogic {

	@Resource
	PasswordEncoder passwordEncoder;

	public SecurityLogicImpl() {
	}

	@Override
	public String generateSalt() {
		String salt = KeyGenerators.string().generateKey();
		return salt;
	}

	@Override
	public String generatePassword() {
		String password = KeyGenerators.string().generateKey();
		return password;
	}

	@Override
	public String encrypt(String password, String salt) {
		String encodedPassword = passwordEncoder.encodePassword(password, salt);
		return encodedPassword;
	}

	@Override
	public boolean isPasswordAllowed(String password, String userId) {
		 
		return true;
	}

	@Override
	public String decrypt(String encodedPassword, String salt) {
		if (passwordEncoder instanceof PayAsiaPaswordEncoder) {
			PayAsiaPaswordEncoder pape = (PayAsiaPaswordEncoder) passwordEncoder;
			return pape.decodePassword(encodedPassword, salt);
		} else {
			throw new PayAsiaBusinessException(
					"password.decryption.not.supported");
		}
	}

	@Override
	public boolean isPasswordValid(String encodedPwd, String rawPwd, Object salt) {
		return passwordEncoder.isPasswordValid(encodedPwd, rawPwd, salt);
	}
}
