package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataImportKeyValueDTO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8258372525010610649L;
	private String value;
	private String key;
	private String fieldType;
	private String methodName;
	private long formId;
	private String tablePosition;
	private boolean isStatic;
	private boolean fieldValid;
	private boolean isChild;
	private boolean isCodeDescField;
	private boolean isFormula;
	private boolean nonTemplateVal;
	private DataImportLogDTO dataImportLogDTO;
	private List<String> colNames;
	private String formula;
	private String actualColName;
	private boolean isEmployeeEntity;
	private String empLstSelectField;
	private String empLstFromEmpField;
	private String empLstFromDynField;
	
	
	
	
	public String getActualColName() {
		return actualColName;
	}
	public void setActualColName(String actualColName) {
		this.actualColName = actualColName;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public boolean isFormula() {
		return isFormula;
	}
	public void setFormula(boolean isFormula) {
		this.isFormula = isFormula;
	}
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	public String getTablePosition() {
		return tablePosition;
	}
	public void setTablePosition(String tablePosition) {
		this.tablePosition = tablePosition;
	}
	public boolean isChild() {
		return isChild;
	}
	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<String> getColNames() {
		return colNames;
	}
	public void setColNames(List<String> colNames) {
		this.colNames = colNames;
	}
	public DataImportLogDTO getDataImportLogDTO() {
		return dataImportLogDTO;
	}
	public void setDataImportLogDTO(DataImportLogDTO dataImportLogDTO) {
		this.dataImportLogDTO = dataImportLogDTO;
	}
	public boolean isFieldValid() {
		return fieldValid;
	}
	public void setFieldValid(boolean fieldValid) {
		this.fieldValid = fieldValid;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public boolean isNonTemplateVal() {
		return nonTemplateVal;
	}
	public void setNonTemplateVal(boolean nonTemplateVal) {
		this.nonTemplateVal = nonTemplateVal;
	}
	public boolean isEmployeeEntity() {
		return isEmployeeEntity;
	}
	public void setEmployeeEntity(boolean isEmployeeEntity) {
		this.isEmployeeEntity = isEmployeeEntity;
	}
	public String getEmpLstSelectField() {
		return empLstSelectField;
	}
	public void setEmpLstSelectField(String empLstSelectField) {
		this.empLstSelectField = empLstSelectField;
	}
	public String getEmpLstFromEmpField() {
		return empLstFromEmpField;
	}
	public void setEmpLstFromEmpField(String empLstFromEmpField) {
		this.empLstFromEmpField = empLstFromEmpField;
	}
	public String getEmpLstFromDynField() {
		return empLstFromDynField;
	}
	public void setEmpLstFromDynField(String empLstFromDynField) {
		this.empLstFromDynField = empLstFromDynField;
	}
	public boolean isCodeDescField() {
		return isCodeDescField;
	}
	public void setCodeDescField(boolean isCodeDescField) {
		this.isCodeDescField = isCodeDescField;
	}

	
	

}
