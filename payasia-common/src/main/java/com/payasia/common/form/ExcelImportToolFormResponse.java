package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ExcelImportToolFormResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3488852574812281152L;
	private List<ExcelImportToolForm> excelImportToolFormList;

	public List<ExcelImportToolForm> getExcelImportToolFormList() {
		return excelImportToolFormList;
	}

	public void setExcelImportToolFormList(
			List<ExcelImportToolForm> excelImportToolFormList) {
		this.excelImportToolFormList = excelImportToolFormList;
	}
	
	
}
