package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class PrivilageResponseForm.
 */
public class PrivilageResponseForm extends PageResponse implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7005370105083357182L;
	/** The previlage list form. */
	private List<PrevilegeForm> previlageListForm;
	

	/**
	 * Gets the previlage list form.
	 *
	 * @return the previlage list form
	 */
	public List<PrevilegeForm> getPrevilageListForm() {
		return previlageListForm;
	}

	/**
	 * Sets the previlage list form.
	 *
	 * @param previlageListForm the new previlage list form
	 */
	public void setPrevilageListForm(List<PrevilegeForm> previlageListForm) {
		this.previlageListForm = previlageListForm;
	}

	

	

}
