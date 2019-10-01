package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class SwitchCompanyResponse extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3550643983911931464L;
	private List<SwitchCompanyForm> switchCompanyFormList;

	public List<SwitchCompanyForm> getSwitchCompanyFormList() {
		return switchCompanyFormList;
	}

	public void setSwitchCompanyFormList(List<SwitchCompanyForm> switchCompanyFormList) {
		this.switchCompanyFormList = switchCompanyFormList;
	}

}
