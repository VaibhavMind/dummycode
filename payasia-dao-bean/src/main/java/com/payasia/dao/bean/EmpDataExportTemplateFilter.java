package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Emp_Data_Export_Template_Filter database table.
 * 
 */
@Entity
@Table(name = "Emp_Data_Export_Template_Filter")
public class EmpDataExportTemplateFilter extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Export_Filter_ID")
	private long exportFilterId;

	@Column(name = "Close_Bracket")
	private String closeBracket;

	@Column(name = "Equality_Operator")
	private String equalityOperator;

	@Column(name = "Logical_Operator")
	private String logicalOperator;

	@Column(name = "Open_Bracket")
	private String openBracket;

	@Column(name = "Value")
	private String value;

	 
	@ManyToOne
	@JoinColumn(name = "Data_Dictionary_ID")
	private DataDictionary dataDictionary;

	 
	@ManyToOne
	@JoinColumn(name = "Export_Template_ID")
	private EmpDataExportTemplate empDataExportTemplate;

	public EmpDataExportTemplateFilter() {
	}

	public long getExportFilterId() {
		return this.exportFilterId;
	}

	public void setExportFilterId(long exportFilterId) {
		this.exportFilterId = exportFilterId;
	}

	public String getCloseBracket() {
		return this.closeBracket;
	}

	public void setCloseBracket(String closeBracket) {
		this.closeBracket = closeBracket;
	}

	public String getEqualityOperator() {
		return this.equalityOperator;
	}

	public void setEqualityOperator(String equalityOperator) {
		this.equalityOperator = equalityOperator;
	}

	public String getLogicalOperator() {
		return this.logicalOperator;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public String getOpenBracket() {
		return this.openBracket;
	}

	public void setOpenBracket(String openBracket) {
		this.openBracket = openBracket;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DataDictionary getDataDictionary() {
		return this.dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}

	public EmpDataExportTemplate getEmpDataExportTemplate() {
		return this.empDataExportTemplate;
	}

	public void setEmpDataExportTemplate(
			EmpDataExportTemplate empDataExportTemplate) {
		this.empDataExportTemplate = empDataExportTemplate;
	}

}