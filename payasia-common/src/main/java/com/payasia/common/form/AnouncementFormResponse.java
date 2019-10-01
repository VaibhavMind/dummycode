package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class AnouncementFormResponse extends PageResponse implements Serializable {
		
	private List<AnouncementForm> rows;

	public List<AnouncementForm> getRows() {
		return rows;
	}

	public void setRows(List<AnouncementForm> rows) {
		this.rows = rows;
	}

	
	
}
