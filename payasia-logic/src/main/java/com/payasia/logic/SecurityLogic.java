package com.payasia.logic;

/**
 * Methods in this interface are for related to password generation and password
 * validation against password policy
 */
public interface SecurityLogic {

	/**
	 * Generates random salt string for encryption.
	 * 
	 * @return the salt
	 */
	String generateSalt();

	/**
	 * Generates a random password.
	 * 
	 * @return the password
	 */
	String generatePassword();

	/**
	 * Encrypt.
	 * 
	 * @param password
	 *            the password
	 * @param salt
	 *            the salt
	 * @return the string
	 */
	String encrypt(String password, String salt);

	/**
	 * Checks if is password allowed after validating it against password
	 * policy.
	 * 
	 * @param password
	 *            the password
	 * @param userId
	 *            the user id
	 * @return true, if is password allowed
	 */
	boolean isPasswordAllowed(String password, String userId);

	/**
	 * Decrypts user password.
	 * 
	 * @param encodedPassword
	 *            the encoded password
	 * @param salt
	 *            the salt
	 * @return plain text, if successful
	 */
	String decrypt(String encodedPassword, String salt);

	/**
	 * Checks if is password valid.
	 * 
	 * @param encodedPwd
	 *            the encoded pwd
	 * @param rawPwd
	 *            the raw pwd
	 * @param salt
	 *            the salt
	 * @return true, if is password valid
	 */
	boolean isPasswordValid(String encodedPwd, String rawPwd, Object salt);

}
