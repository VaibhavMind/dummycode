package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DynamicTableDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4318098050466872905L;
	private Long formId;
	private String tableName;
	private String tablePosition;
	private String fieldType;
	
	
	

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
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	@Override
	public boolean  equals(Object obj){
		
		if(obj!=null && obj instanceof DynamicTableDTO){
			if((((DynamicTableDTO) obj).getFormId().equals(this.formId))&&((DynamicTableDTO) obj).getTableName().trim().equals(this.getTableName().trim())) {
				
				return true;
			}
		}else{
			return false;
		}
		return false;
		
		
		
	}
	
	
	@Override
	   public int hashCode() {
	      return 1;
	   }
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	
}
