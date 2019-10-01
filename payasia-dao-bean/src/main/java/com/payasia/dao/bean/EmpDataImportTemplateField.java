package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Emp_Data_Import_Template_Field database table.
 * 
 */
@Entity
@Table(name = "Emp_Data_Import_Template_Field")
public class EmpDataImportTemplateField extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Import_Field_ID")
	private long importFieldId;

	@Column(name = "Data_Type")
	private String dataType;

	@Column(name = "Default_Value")
	private String defaultValue;

	@Column(name = "Description")
	private String description;

	@Column(name = "Excel_Field_Name")
	private String excelFieldName;

	@Column(name = "Length")
	private Integer length;

	@Column(name = "Required")
	private Boolean required;

	@Column(name = "Sample_Data")
	private String sampleData;

	 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Data_Dictionary_ID")
	private DataDictionary dataDictionary;

	 
	@ManyToOne
	@JoinColumn(name = "Import_Template_ID")
	private EmpDataImportTemplate empDataImportTemplate;

	public EmpDataImportTemplateField() {
	}

	public long getImportFieldId() {
		return this.importFieldId;
	}

	public void setImportFieldId(long importFieldId) {
		this.importFieldId = importFieldId;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExcelFieldName() {
		return this.excelFieldName;
	}

	public void setExcelFieldName(String excelFieldName) {
		this.excelFieldName = excelFieldName;
	}

	public Integer getLength() {
		return this.length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Boolean getRequired() {
		return this.required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public String getSampleData() {
		return this.sampleData;
	}

	public void setSampleData(String sampleData) {
		this.sampleData = sampleData;
	}

	public DataDictionary getDataDictionary() {
		return this.dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}

	public EmpDataImportTemplate getEmpDataImportTemplate() {
		return this.empDataImportTemplate;
	}

	public void setEmpDataImportTemplate(
			EmpDataImportTemplate empDataImportTemplate) {
		this.empDataImportTemplate = empDataImportTemplate;
	}

}