package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Menu implements Serializable {

	private String moduleName;

	private Set<ModulePrivileges> modulePrivileges =  new HashSet<ModulePrivileges>();

	public Menu() {	}
	
	
	
	public Menu(String moduleName, Set<ModulePrivileges> modulePrivileges) {
		super();
		this.moduleName = moduleName;
		this.modulePrivileges = modulePrivileges;
	}



	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Set<ModulePrivileges> getModulePrivileges() {
		return modulePrivileges;
	}

	public void setModulePrivileges(Set<ModulePrivileges> modulePrivileges) {
		this.modulePrivileges = modulePrivileges;
	}

	
	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + (moduleName == null ? 0 : moduleName.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
	        return true;
	    }
	    if (obj == null) {
	        return false;
	    }
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    Menu other = (Menu) obj;
	    if (moduleName == null) {
	        if (other.moduleName != null) {
	            return false;
	        }
	    } else if (!moduleName.equals(other.moduleName)) {
	        return false;
	    }
	    return true;
	}

	
	
}
