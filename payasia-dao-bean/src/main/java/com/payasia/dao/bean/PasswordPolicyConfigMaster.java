package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Password_Policy_Config_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Password_Policy_Config_Master")
public class PasswordPolicyConfigMaster extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Pwd_Policy_Config_ID")
	private long pwdPolicyConfigId;

	@Column(name = "Allowed_Invalid_Attempts")
	private int allowedInvalidAttempts;

	@Column(name = "Enable_Password_Complexity")
	private boolean enablePasswordComplexity;

	@Column(name = "Enable_Pwd_Policy")
	private boolean enablePwdPolicy;

	@Column(name = "Expiry_Reminder_Days")
	private int expiryReminderDays;

	@Column(name = "Include_Special_Character")
	private boolean includeSpecialCharacter;

	@Column(name = "Max_Expiry_Days_Limit")
	private int maxExpiryDaysLimit;

	@Column(name = "Memory_List_Size")
	private int memoryListSize;

	@Column(name = "Min_Pwd_Length")
	private int minPwdLength;

	@Column(name = "Max_Pwd_Length")
	private Integer maxPwdLength;

	@Column(name = "Include_Numeric_Character")
	private Boolean includeNumericCharacter;

	@Column(name = "Include_Upper_Lower_Case")
	private Boolean includeUpperLowerCase;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public PasswordPolicyConfigMaster() {
	}

	public long getPwdPolicyConfigId() {
		return pwdPolicyConfigId;
	}

	public void setPwdPolicyConfigId(long pwdPolicyConfigId) {
		this.pwdPolicyConfigId = pwdPolicyConfigId;
	}

	public int getAllowedInvalidAttempts() {
		return allowedInvalidAttempts;
	}

	public void setAllowedInvalidAttempts(int allowedInvalidAttempts) {
		this.allowedInvalidAttempts = allowedInvalidAttempts;
	}

	public boolean isEnablePasswordComplexity() {
		return enablePasswordComplexity;
	}

	public void setEnablePasswordComplexity(boolean enablePasswordComplexity) {
		this.enablePasswordComplexity = enablePasswordComplexity;
	}

	public boolean isEnablePwdPolicy() {
		return enablePwdPolicy;
	}

	public void setEnablePwdPolicy(boolean enablePwdPolicy) {
		this.enablePwdPolicy = enablePwdPolicy;
	}

	public int getExpiryReminderDays() {
		return expiryReminderDays;
	}

	public void setExpiryReminderDays(int expiryReminderDays) {
		this.expiryReminderDays = expiryReminderDays;
	}

	public boolean isIncludeSpecialCharacter() {
		return includeSpecialCharacter;
	}

	public void setIncludeSpecialCharacter(boolean includeSpecialCharacter) {
		this.includeSpecialCharacter = includeSpecialCharacter;
	}

	public int getMaxExpiryDaysLimit() {
		return maxExpiryDaysLimit;
	}

	public void setMaxExpiryDaysLimit(int maxExpiryDaysLimit) {
		this.maxExpiryDaysLimit = maxExpiryDaysLimit;
	}

	public int getMemoryListSize() {
		return memoryListSize;
	}

	public void setMemoryListSize(int memoryListSize) {
		this.memoryListSize = memoryListSize;
	}

	public int getMinPwdLength() {
		return minPwdLength;
	}

	public void setMinPwdLength(int minPwdLength) {
		this.minPwdLength = minPwdLength;
	}

	public Integer getMaxPwdLength() {
		return maxPwdLength;
	}

	public void setMaxPwdLength(Integer maxPwdLength) {
		this.maxPwdLength = maxPwdLength;
	}

	public Boolean getIncludeNumericCharacter() {
		return includeNumericCharacter;
	}

	public void setIncludeNumericCharacter(Boolean includeNumericCharacter) {
		this.includeNumericCharacter = includeNumericCharacter;
	}

	public Boolean getIncludeUpperLowerCase() {
		return includeUpperLowerCase;
	}

	public void setIncludeUpperLowerCase(Boolean includeUpperLowerCase) {
		this.includeUpperLowerCase = includeUpperLowerCase;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}