package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class UserRoleListForm.
 */
public class UserRoleListForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 217636704468770676L;

	/** The Copy role id. */
	private Long CopyRoleId;
	
	/** The role id. */
	private Long roleId;
	
	/** The role desc. */
	private String roleDesc;
	
	/** The read write access. */
	private String readWriteAccess;
	
	private Boolean deletable;
	
	/** The role name. */
	private String roleName;
	
	/**
	 * Gets the role desc.
	 *
	 * @return the role desc
	 */
	public String getRoleDesc() {
		return roleDesc;
	}
	
	/**
	 * Sets the role desc.
	 *
	 * @param roleDesc the new role desc
	 */
	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}
	
	/**
	 * Gets the read write access.
	 *
	 * @return the read write access
	 */
	public String getReadWriteAccess() {
		return readWriteAccess;
	}
	
	/**
	 * Sets the read write access.
	 *
	 * @param readWriteAccess the new read write access
	 */
	public void setReadWriteAccess(String readWriteAccess) {
		this.readWriteAccess = readWriteAccess;
	}
	
	/**
	 * Gets the role name.
	 *
	 * @return the role name
	 */
	public String getRoleName() {
		return roleName;
	}
	
	/**
	 * Sets the role name.
	 *
	 * @param roleName the new role name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	/**
	 * Gets the copy role id.
	 *
	 * @return the copy role id
	 */
	public Long getCopyRoleId() {
		return CopyRoleId;
	}
	
	/**
	 * Sets the copy role id.
	 *
	 * @param copyRoleId the new copy role id
	 */
	public void setCopyRoleId(Long copyRoleId) {
		CopyRoleId = copyRoleId;
	}
	
	/**
	 * Gets the role id.
	 *
	 * @return the role id
	 */
	public Long getRoleId() {
		return roleId;
	}
	
	/**
	 * Sets the role id.
	 *
	 * @param roleId the new role id
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Boolean getDeletable() {
		return deletable;
	}

	public void setDeletable(Boolean deletable) {
		this.deletable = deletable;
	}
	
}
