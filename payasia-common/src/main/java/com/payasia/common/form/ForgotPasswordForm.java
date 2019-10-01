package com.payasia.common.form;

import java.io.Serializable;

public class ForgotPasswordForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7834052286968157848L;
	private boolean dontKnowMyPassword;
	private String emailOrUsernameOrFullName;
	private boolean dontKnowMyUsername;
	private String emailOrFullName;
	private String companycode;
	private String turing;
	private String captchaId;
	
	public boolean isDontKnowMyPassword() {
		return dontKnowMyPassword;
	}
	public void setDontKnowMyPassword(boolean dontKnowMyPassword) {
		this.dontKnowMyPassword = dontKnowMyPassword;
	}
	public String getEmailOrUsernameOrFullName() {
		return emailOrUsernameOrFullName;
	}
	public void setEmailOrUsernameOrFullName(String emailOrUsernameOrFullName) {
		this.emailOrUsernameOrFullName = emailOrUsernameOrFullName;
	}
	public boolean isDontKnowMyUsername() {
		return dontKnowMyUsername;
	}
	public void setDontKnowMyUsername(boolean dontKnowMyUsername) {
		this.dontKnowMyUsername = dontKnowMyUsername;
	}
	public String getEmailOrFullName() {
		return emailOrFullName;
	}
	public void setEmailOrFullName(String emailOrFullName) {
		this.emailOrFullName = emailOrFullName;
	}
	public String getCompanycode() {
		return companycode;
	}
	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}
	public String getTuring() {
		return turing;
	}
	public void setTuring(String turing) {
		this.turing = turing;
	}
	public String getCaptchaId() {
		return captchaId;
	}
	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}
	
	
	
	
}
