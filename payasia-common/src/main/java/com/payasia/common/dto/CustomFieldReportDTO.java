package com.payasia.common.dto;

import java.util.List;

public class CustomFieldReportDTO {
	
	
	List<Object[]> customFieldObjList ; 
	List<String> dataDictNameList;
	
	
	
	
	public List<Object[]> getCustomFieldObjList() {
		return customFieldObjList;
	}
	public void setCustomFieldObjList(List<Object[]> customFieldObjList) {
		this.customFieldObjList = customFieldObjList;
	}
	public List<String> getDataDictNameList() {
		return dataDictNameList;
	}
	public void setDataDictNameList(List<String> dataDictNameList) {
		this.dataDictNameList = dataDictNameList;
	}
	
	
	
	
	
	
	
	
	

}
