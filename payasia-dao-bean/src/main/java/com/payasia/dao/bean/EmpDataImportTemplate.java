package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Emp_Data_Import_Template database table.
 * 
 */
@Entity
@Table(name = "Emp_Data_Import_Template")
public class EmpDataImportTemplate extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Import_Template_ID")
	private long importTemplateId;

	@Column(name = "Custom_Table_Position")
	private Integer custom_Table_Position;

	@Column(name = "Description")
	private String description;

	@Column(name = "Form_ID")
	private Long formID;

	@Column(name = "Template_Name")
	private String templateName;

	@Column(name = "Transaction_Type")
	private String transaction_Type;

	@Column(name = "Upload_Type")
	private String upload_Type;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Entity_ID")
	private EntityMaster entityMaster;

	 
	@OneToMany(mappedBy = "empDataImportTemplate", cascade = CascadeType.REMOVE)
	private Set<EmpDataImportTemplateField> empDataImportTemplateFields;

	public EmpDataImportTemplate() {
	}

	public long getImportTemplateId() {
		return this.importTemplateId;
	}

	public void setImportTemplateId(long importTemplateId) {
		this.importTemplateId = importTemplateId;
	}

	public Integer getCustom_Table_Position() {
		return this.custom_Table_Position;
	}

	public void setCustom_Table_Position(Integer custom_Table_Position) {
		this.custom_Table_Position = custom_Table_Position;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getFormID() {
		return this.formID;
	}

	public void setFormID(Long formID) {
		this.formID = formID;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTransaction_Type() {
		return this.transaction_Type;
	}

	public void setTransaction_Type(String transaction_Type) {
		this.transaction_Type = transaction_Type;
	}

	public String getUpload_Type() {
		return this.upload_Type;
	}

	public void setUpload_Type(String upload_Type) {
		this.upload_Type = upload_Type;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public EntityMaster getEntityMaster() {
		return this.entityMaster;
	}

	public void setEntityMaster(EntityMaster entityMaster) {
		this.entityMaster = entityMaster;
	}

	public Set<EmpDataImportTemplateField> getEmpDataImportTemplateFields() {
		return this.empDataImportTemplateFields;
	}

	public void setEmpDataImportTemplateFields(
			Set<EmpDataImportTemplateField> empDataImportTemplateFields) {
		this.empDataImportTemplateFields = empDataImportTemplateFields;
	}

}