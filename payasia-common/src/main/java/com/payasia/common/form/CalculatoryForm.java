package com.payasia.common.form;

public class CalculatoryForm {
	
	private Long companyId;
	private Long employeeId;
	private String formulaType;
	private String formula;
	private String dataDictionaryId;
	
	public String getDataDictionaryId() {
		return dataDictionaryId;
	}
	public void setDataDictionaryId(String dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getFormulaType() {
		return formulaType;
	}
	public void setFormulaType(String formulaType) {
		this.formulaType = formulaType;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	

}
