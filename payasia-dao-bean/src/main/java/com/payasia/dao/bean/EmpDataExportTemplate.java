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
 * The persistent class for the Emp_Data_Export_Template database table.
 * 
 */
@Entity
@Table(name = "Emp_Data_Export_Template")
public class EmpDataExportTemplate extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Export_Template_ID")
	private long exportTemplateId;

	@Column(name = "Category")
	private String category;

	@Column(name = "Custom_Table_Position")
	private Integer custom_Table_Position;

	@Column(name = "Description")
	private String description;

	@Column(name = "Form_ID")
	private Long formID;

	@Column(name = "Scope")
	private String scope;

	@Column(name = "Include_Prefix")
	private boolean includePrefix;

	@Column(name = "Include_Suffix")
	private boolean includeSuffix;

	@Column(name = "Include_Template_Name_As_Prefix")
	private boolean includeTemplateNameAsPrefix;

	@Column(name = "Include_Timestamp_As_Suffix")
	private boolean includeTimestampAsSuffix;

	@Column(name = "Prefix")
	private String prefix;

	@Column(name = "Suffix")
	private String suffix;

	@Column(name = "Template_Name")
	private String templateName;

	@Column(name = "Multiple_Section")
	private boolean multipleSection;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Group_ID")
	private CompanyGroup companyGroup;

	 
	@ManyToOne
	@JoinColumn(name = "Entity_ID")
	private EntityMaster entityMaster;

	 
	@OneToMany(mappedBy = "empDataExportTemplate", cascade = CascadeType.REMOVE)
	private Set<EmpDataExportTemplateField> empDataExportTemplateFields;

	 
	@OneToMany(mappedBy = "empDataExportTemplate", cascade = CascadeType.REMOVE)
	private Set<EmpDataExportTemplateFilter> empDataExportTemplateFilters;

	public EmpDataExportTemplate() {
	}

	public long getExportTemplateId() {
		return this.exportTemplateId;
	}

	public void setExportTemplateId(long exportTemplateId) {
		this.exportTemplateId = exportTemplateId;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public boolean getIncludePrefix() {
		return this.includePrefix;
	}

	public void setIncludePrefix(boolean includePrefix) {
		this.includePrefix = includePrefix;
	}

	public boolean getIncludeSuffix() {
		return this.includeSuffix;
	}

	public void setIncludeSuffix(boolean includeSuffix) {
		this.includeSuffix = includeSuffix;
	}

	public boolean getIncludeTemplateNameAsPrefix() {
		return this.includeTemplateNameAsPrefix;
	}

	public void setIncludeTemplateNameAsPrefix(
			boolean includeTemplateNameAsPrefix) {
		this.includeTemplateNameAsPrefix = includeTemplateNameAsPrefix;
	}

	public boolean getIncludeTimestampAsSuffix() {
		return this.includeTimestampAsSuffix;
	}

	public void setIncludeTimestampAsSuffix(boolean includeTimestampAsSuffix) {
		this.includeTimestampAsSuffix = includeTimestampAsSuffix;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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

	public Set<EmpDataExportTemplateField> getEmpDataExportTemplateFields() {
		return this.empDataExportTemplateFields;
	}

	public void setEmpDataExportTemplateFields(
			Set<EmpDataExportTemplateField> empDataExportTemplateFields) {
		this.empDataExportTemplateFields = empDataExportTemplateFields;
	}

	public Set<EmpDataExportTemplateFilter> getEmpDataExportTemplateFilters() {
		return this.empDataExportTemplateFilters;
	}

	public void setEmpDataExportTemplateFilters(
			Set<EmpDataExportTemplateFilter> empDataExportTemplateFilters) {
		this.empDataExportTemplateFilters = empDataExportTemplateFilters;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public CompanyGroup getCompanyGroup() {
		return companyGroup;
	}

	public void setCompanyGroup(CompanyGroup companyGroup) {
		this.companyGroup = companyGroup;
	}

	public boolean isMultipleSection() {
		return multipleSection;
	}

	public void setMultipleSection(boolean multipleSection) {
		this.multipleSection = multipleSection;
	}

}