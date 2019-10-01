package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class UserRoleForm.
 */
public class UserRoleForm extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1417824818794261726L;
	/** The user role list form. */
	private List<UserRoleListForm> userRoleListForm;

	/**
	 * Gets the user role list form.
	 *
	 * @return the user role list form
	 */
	public List<UserRoleListForm> getUserRoleListForm() {
		return userRoleListForm;
	}

	/**
	 * Sets the user role list form.
	 *
	 * @param userRoleListForm the new user role list form
	 */
	public void setUserRoleListForm(List<UserRoleListForm> userRoleListForm) {
		this.userRoleListForm = userRoleListForm;
	}

	
}
