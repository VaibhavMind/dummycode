package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class UserResponseForm.
 */
public class UserResponseForm extends PageResponse implements Serializable{

	

	/** The users list form. */
	private List<UsersForm> usersListForm;


	/**
	 * Gets the users list form.
	 *
	 * @return the users list form
	 */
	public List<UsersForm> getUsersListForm() {
		return usersListForm;
	}

	/**
	 * Sets the users list form.
	 *
	 * @param usersListForm the new users list form
	 */
	public void setUsersListForm(List<UsersForm> usersListForm) {
		this.usersListForm = usersListForm;
	}

	

}
