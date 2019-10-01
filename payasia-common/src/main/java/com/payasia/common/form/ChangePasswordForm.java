package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.PasswordPolicyDTO;

public class ChangePasswordForm implements Serializable{
	
	private List<PasswordPolicyDTO> passwordPolicyDTO;
	private Boolean isPasswordPolicyEnabled ;

	public List<PasswordPolicyDTO> getPasswordPolicyDTO() {
		return passwordPolicyDTO;
	}

	public void setPasswordPolicyDTO(List<PasswordPolicyDTO> passwordPolicyDTO) {
		this.passwordPolicyDTO = passwordPolicyDTO;
	}

	public Boolean getIsPasswordPolicyEnabled() {
		return isPasswordPolicyEnabled;
	}

	public void setIsPasswordPolicyEnabled(Boolean isPasswordPolicyEnabled) {
		this.isPasswordPolicyEnabled = isPasswordPolicyEnabled;
	}
	
	

}
