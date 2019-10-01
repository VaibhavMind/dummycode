package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class PrevilageForm.
 */
public class PrevilegeForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6363915448730647736L;

	/** The privilege id. */
	private Long privilegeId;
	
	/** The privilege desc. */
	private String privilegeDesc;
	
	/** The privilege name. */
	private String privilegeName;
	
	/** The role assigned. */
	private boolean roleAssigned;
	private String moduleName;
	private String privilegeRole;
	
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getPrivilegeRole() {
		return privilegeRole;
	}

	public void setPrivilegeRole(String privilegeRole) {
		this.privilegeRole = privilegeRole;
	}

	/**
	 * Gets the privilege desc.
	 *
	 * @return the privilege desc
	 */
	public String getPrivilegeDesc() {
		return privilegeDesc;
	}
	
	/**
	 * Sets the privilege desc.
	 *
	 * @param privilegeDesc the new privilege desc
	 */
	public void setPrivilegeDesc(String privilegeDesc) {
		this.privilegeDesc = privilegeDesc;
	}
	
	/**
	 * Gets the privilege name.
	 *
	 * @return the privilege name
	 */
	public String getPrivilegeName() {
		return privilegeName;
	}
	
	/**
	 * Sets the privilege name.
	 *
	 * @param privilegeName the new privilege name
	 */
	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}
	
	/**
	 * Checks if is role assigned.
	 *
	 * @return true, if is role assigned
	 */
	public boolean isRoleAssigned() {
		return roleAssigned;
	}
	
	/**
	 * Sets the role assigned.
	 *
	 * @param roleAssigned the new role assigned
	 */
	public void setRoleAssigned(boolean roleAssigned) {
		this.roleAssigned = roleAssigned;
	}
	
	/**
	 * Gets the privilege id.
	 *
	 * @return the privilege id
	 */
	public Long getPrivilegeId() {
		return privilegeId;
	}
	
	/**
	 * Sets the privilege id.
	 *
	 * @param privilegeId the new privilege id
	 */
	public void setPrivilegeId(Long privilegeId) {
		this.privilegeId = privilegeId;
	}

	

}
