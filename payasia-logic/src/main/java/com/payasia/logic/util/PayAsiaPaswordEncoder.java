package com.payasia.logic.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * Custom class to implement weak password logic to enable decryption as per
 * client requirement.
 */
public class PayAsiaPaswordEncoder implements PasswordEncoder {

	/** The base. */
	Base64 base = new Base64();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.authentication.encoding.PasswordEncoder#
	 * encodePassword(java.lang.String, java.lang.Object)
	 */
	@Override
	public String encodePassword(String rawPwd, Object salt) {
		String mergedPwd = rawPwd + salt;
		return new String(base.encode(mergedPwd.getBytes()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.authentication.encoding.PasswordEncoder#
	 * isPasswordValid(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isPasswordValid(String encodedPwd, String rawPwd, Object salt) {
		if (encodedPwd.equals(this.encodePassword(rawPwd, salt))) {
			return true;
		}
		return false;
	}

	/**
	 * Decode user password password.
	 * 
	 * @param encodedPwd
	 *            the encoded password
	 * @param salt
	 *            the salt
	 * @return the plain text password
	 */
	public String decodePassword(String encodedPwd, String salt) {
		String mergedPwd = new String(base.decode(encodedPwd.getBytes()));
		String pwd = mergedPwd.substring(0, mergedPwd.lastIndexOf(salt));

		return pwd;
	}
}
