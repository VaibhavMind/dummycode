package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class ExcelExportQueryDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4447558524112220125L;

	private String selectQuery;
	
	private String tablePosition;
	
	private Long formId;
	
	private List<CodeDescDTO> codeDescList;

	public String getSelectQuery() {
		return selectQuery;
	}

	public void setSelectQuery(String selectQuery) {
		this.selectQuery = selectQuery;
	}

	public String getTablePosition() {
		return tablePosition;
	}

	public void setTablePosition(String tablePosition) {
		this.tablePosition = tablePosition;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public List<CodeDescDTO> getCodeDescList() {
		return codeDescList;
	}

	public void setCodeDescList(List<CodeDescDTO> codeDescList) {
		this.codeDescList = codeDescList;
	}
	
	
}
