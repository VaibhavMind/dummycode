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
 * The persistent class for the Emp_Data_Export_Template_Field database table.
 * 
 */
@Entity
@Table(name = "Emp_Data_Export_Template_Field")
public class EmpDataExportTemplateField extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Export_Field_ID")
	private long exportFieldId;

	@Column(name = "Excel_Field_Name")
	private String excelFieldName;

	@Column(name = "Data_Dict_Name")
	private String dataDictName;

	 
	@ManyToOne
	@JoinColumn(name = "Data_Dictionary_ID")
	private DataDictionary dataDictionary;

	 
	@ManyToOne
	@JoinColumn(name = "Export_Template_ID")
	private EmpDataExportTemplate empDataExportTemplate;

	 
	@ManyToOne
	@JoinColumn(name = "Entity_ID")
	private EntityMaster entityMaster;

	public EmpDataExportTemplateField() {
	}

	public long getExportFieldId() {
		return this.exportFieldId;
	}

	public void setExportFieldId(long exportFieldId) {
		this.exportFieldId = exportFieldId;
	}

	public String getExcelFieldName() {
		return this.excelFieldName;
	}

	public void setExcelFieldName(String excelFieldName) {
		this.excelFieldName = excelFieldName;
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

	public String getDataDictName() {
		return dataDictName;
	}

	public void setDataDictName(String dataDictName) {
		this.dataDictName = dataDictName;
	}

	public EntityMaster getEntityMaster() {
		return entityMaster;
	}

	public void setEntityMaster(EntityMaster entityMaster) {
		this.entityMaster = entityMaster;
	}

}