package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ExcelExportToolFormResponse extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9017060678884593625L;
	private List<ExcelExportToolForm> excelExportToolFormList;

	public List<ExcelExportToolForm> getExcelExportToolFormList() {
		return excelExportToolFormList;
	}

	public void setExcelExportToolFormList(
			List<ExcelExportToolForm> excelExportToolFormList) {
		this.excelExportToolFormList = excelExportToolFormList;
	}
	
	
}
