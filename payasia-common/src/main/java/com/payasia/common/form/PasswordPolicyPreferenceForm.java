package com.payasia.common.form;

/**
 * The Class PasswordPolicyPreferenceForm.
 */
public class PasswordPolicyPreferenceForm {

	private Long passwordPolicyConfigId;

	private Integer companyId;

	private boolean passwordPolicyEnabled;

	private Integer maxExpiry;

	private Integer minPasswordLength;
	private Integer maxPasswordLength;

	private Integer canNOtSameAsLastPwd;

	private Integer expiryReminder;

	private Integer minPasswordChange;

	private boolean changePasswordOnExpiry;

	private boolean passwordComplexity;

	private boolean specialCharacters;
	private boolean numericCharacters;
	private boolean combinationLowerUpperCase;
	private Integer invalidAttemptsAllowed;

	


	public Integer getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the company id.
	 * 
	 * @param companyId
	 *            the new company id
	 */
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * Checks if is password policy enabled.
	 * 
	 * @return true, if is password policy enabled
	 */
	public boolean isPasswordPolicyEnabled() {
		return passwordPolicyEnabled;
	}

	/**
	 * Sets the password policy enabled.
	 * 
	 * @param passwordPolicyEnabled
	 *            the new password policy enabled
	 */
	public void setPasswordPolicyEnabled(boolean passwordPolicyEnabled) {
		this.passwordPolicyEnabled = passwordPolicyEnabled;
	}

	/**
	 * Gets the max expiry.
	 * 
	 * @return the max expiry
	 */
	public Integer getMaxExpiry() {
		return maxExpiry;
	}

	/**
	 * Sets the max expiry.
	 * 
	 * @param maxExpiry
	 *            the new max expiry
	 */
	public void setMaxExpiry(Integer maxExpiry) {
		this.maxExpiry = maxExpiry;
	}

	/**
	 * Gets the min password length.
	 * 
	 * @return the min password length
	 */
	public Integer getMinPasswordLength() {
		return minPasswordLength;
	}

	/**
	 * Sets the min password length.
	 * 
	 * @param minPasswordLength
	 *            the new min password length
	 */
	public void setMinPasswordLength(Integer minPasswordLength) {
		this.minPasswordLength = minPasswordLength;
	}

	

	

	/**
	 * Gets the expiry reminder.
	 * 
	 * @return the expiry reminder
	 */
	public Integer getExpiryReminder() {
		return expiryReminder;
	}

	/**
	 * Sets the expiry reminder.
	 * 
	 * @param expiryReminder
	 *            the new expiry reminder
	 */
	public void setExpiryReminder(Integer expiryReminder) {
		this.expiryReminder = expiryReminder;
	}

	/**
	 * Gets the min password change.
	 * 
	 * @return the min password change
	 */
	public Integer getMinPasswordChange() {
		return minPasswordChange;
	}

	/**
	 * Sets the min password change.
	 * 
	 * @param minPasswordChange
	 *            the new min password change
	 */
	public void setMinPasswordChange(Integer minPasswordChange) {
		this.minPasswordChange = minPasswordChange;
	}

	/**
	 * Checks if is change password on expiry.
	 * 
	 * @return true, if is change password on expiry
	 */
	public boolean isChangePasswordOnExpiry() {
		return changePasswordOnExpiry;
	}

	/**
	 * Sets the change password on expiry.
	 * 
	 * @param changePasswordOnExpiry
	 *            the new change password on expiry
	 */
	public void setChangePasswordOnExpiry(boolean changePasswordOnExpiry) {
		this.changePasswordOnExpiry = changePasswordOnExpiry;
	}

	/**
	 * Checks if is password complexity.
	 * 
	 * @return true, if is password complexity
	 */
	public boolean isPasswordComplexity() {
		return passwordComplexity;
	}

	/**
	 * Sets the password complexity.
	 * 
	 * @param passwordComplexity
	 *            the new password complexity
	 */
	public void setPasswordComplexity(boolean passwordComplexity) {
		this.passwordComplexity = passwordComplexity;
	}

	

	
	/**
	 * Gets the invalid attempts allowed.
	 * 
	 * @return the invalid attempts allowed
	 */
	public Integer getInvalidAttemptsAllowed() {
		return invalidAttemptsAllowed;
	}

	/**
	 * Sets the invalid attempts allowed.
	 * 
	 * @param invalidAttemptsAllowed
	 *            the new invalid attempts allowed
	 */
	public void setInvalidAttemptsAllowed(Integer invalidAttemptsAllowed) {
		this.invalidAttemptsAllowed = invalidAttemptsAllowed;
	}

	
	
	
	
	public boolean isSpecialCharacters() {
		return specialCharacters;
	}

	public void setSpecialCharacters(boolean specialCharacters) {
		this.specialCharacters = specialCharacters;
	}

	public Integer getMaxPasswordLength() {
		return maxPasswordLength;
	}

	public void setMaxPasswordLength(Integer maxPasswordLength) {
		this.maxPasswordLength = maxPasswordLength;
	}

	public boolean isNumericCharacters() {
		return numericCharacters;
	}

	public void setNumericCharacters(boolean numericCharacters) {
		this.numericCharacters = numericCharacters;
	}

	public boolean isCombinationLowerUpperCase() {
		return combinationLowerUpperCase;
	}

	public void setCombinationLowerUpperCase(boolean combinationLowerUpperCase) {
		this.combinationLowerUpperCase = combinationLowerUpperCase;
	}

	public Integer getCanNOtSameAsLastPwd() {
		return canNOtSameAsLastPwd;
	}

	public void setCanNOtSameAsLastPwd(Integer canNOtSameAsLastPwd) {
		this.canNOtSameAsLastPwd = canNOtSameAsLastPwd;
	}

	public Long getPasswordPolicyConfigId() {
		return passwordPolicyConfigId;
	}

	public void setPasswordPolicyConfigId(Long passwordPolicyConfigId) {
		this.passwordPolicyConfigId = passwordPolicyConfigId;
	}

}
