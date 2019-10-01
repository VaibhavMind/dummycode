package com.payasia.common.dto;

public class ModulePrivileges {

	private String privilegeName;

	private String privilegeDesc;
	
	public ModulePrivileges() {
	}
	

	public ModulePrivileges(String privilegeName, String privilegeDesc) {
		super();
		this.privilegeName = privilegeName;
		this.privilegeDesc = privilegeDesc;
	}

	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}

	public String getPrivilegeName() {
		return this.privilegeName;
	}

	public void setPrivilegeDesc(String privilegeDesc) {
		this.privilegeDesc = privilegeDesc;
	}

	public String getPrivilegeDesc() {
		return this.privilegeDesc;
	}
}
