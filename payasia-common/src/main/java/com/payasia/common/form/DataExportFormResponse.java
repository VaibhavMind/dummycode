package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class DataExportFormResponse extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -349388736343086723L;
	private List<DataExportForm> dataExportFormList;

	public List<DataExportForm> getDataExportFormList() {
		return dataExportFormList;
	}

	public void setDataExportFormList(List<DataExportForm> dataExportFormList) {
		this.dataExportFormList = dataExportFormList;
	}
	
}
