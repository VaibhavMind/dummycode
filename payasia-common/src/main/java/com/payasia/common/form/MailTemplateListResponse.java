package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class MailTemplateListResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3835445932441783509L;
	private List<MailTemplateListForm> rows;


	public List<MailTemplateListForm> getRows() {
		return rows;
	}

	public void setRows(List<MailTemplateListForm> rows) {
		this.rows = rows;
	}

}
