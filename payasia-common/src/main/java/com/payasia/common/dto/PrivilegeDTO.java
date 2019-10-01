package com.payasia.common.dto;

import java.io.Serializable;

public class PrivilegeDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String privilegeName;
	private String privilegeDesc;
	
	public PrivilegeDTO() {
	}
	
	
	public PrivilegeDTO(String privilegeName, String privilegeDesc) {
		super();
		this.privilegeName = privilegeName;
		this.privilegeDesc = privilegeDesc;
	}

	public String getPrivilegeName() {
		return privilegeName;
	}

	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}

	public String getPrivilegeDesc() {
		return privilegeDesc;
	}

	public void setPrivilegeDesc(String privilegeDesc) {
		this.privilegeDesc = privilegeDesc;
	}
	
}
