package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserPrivilegeDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String moduleName;

	private Set<PrivilegeDTO> privileges = new HashSet<>();

	public UserPrivilegeDTO() {
	}

	public UserPrivilegeDTO(String moduleName, Set<PrivilegeDTO> privileges) {
		super();
		this.moduleName = moduleName;
		this.privileges = privileges;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Set<PrivilegeDTO> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<PrivilegeDTO> privileges) {
		this.privileges = privileges;
	}

}
