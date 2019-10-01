package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;

public class LoginForm implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1460782827421790764L;
	private String loginName;
	private String password;
	private String role;
	private Long companyId;
	private String companyName;
	private byte[] byteFile;
	private String fileType;
	
	
	
	public byte[] getByteFile() {
		return byteFile;
	}
	public void setByteFile(byte[] byteFile) {
		if (byteFile != null) {
			this.byteFile = Arrays.copyOf(byteFile, byteFile.length);
		}
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
	
	
	
}
