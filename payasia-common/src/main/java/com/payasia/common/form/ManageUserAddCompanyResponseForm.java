package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ManageUserAddCompanyResponseForm extends PageResponse implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6766710545262033253L;
	private List<ManageUserAddCompanyForm> manageUserAddCompanyForm;

	public List<ManageUserAddCompanyForm> getManageUserAddCompanyForm() {
		return manageUserAddCompanyForm;
	}

	public void setManageUserAddCompanyForm(List<ManageUserAddCompanyForm> manageUserAddCompanyForm) {
		this.manageUserAddCompanyForm = manageUserAddCompanyForm;
	}

	

}
