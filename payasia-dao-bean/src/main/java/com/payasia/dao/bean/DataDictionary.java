package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Data_Dictionary database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Data_Dictionary")
public class DataDictionary extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Data_Dictionary_ID")
	private long dataDictionaryId;

	@Column(name = "Column_Name")
	private String columnName;

	@Column(name = "Data_Dict_Name")
	private String dataDictName;

	@Column(name = "Description")
	private String description;

	@Column(name = "Field_Type")
	private String fieldType;

	@Column(name = "Form_ID")
	private Long formID;

	@Column(name = "Importable")
	private boolean importable = true;

	@Column(name = "Label")
	private String label;

	@Column(name = "Table_Name")
	private String tableName;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<CompanyDocumentShortList> companyDocumentShortLists;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<CompanyEmployeeShortList> companyEmployeeShortLists;

	 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Entity_ID")
	private EntityMaster entityMaster;

	 
	@OneToMany(mappedBy = "dataDictionary", cascade = CascadeType.REMOVE)
	private Set<DynamicFormFieldRefValue> dynamicFormFieldRefValues;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<EmpDataExportTemplateField> empDataExportTemplateFields;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<EmpDataExportTemplateFilter> empDataExportTemplateFilters;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<EmpDataImportTemplateField> empDataImportTemplateFields;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<EntityListViewField> entityListViewFields;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<MultiLingualData> multiLingualData;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists;

	 
	@OneToMany(mappedBy = "dataDictionary")
	private Set<ClaimTemplateItemShortlist> claimTemplateItemShortlists;

	@Column(name = "Data_Type")
	private String dataType;

	public DataDictionary() {
	}

	public long getDataDictionaryId() {
		return this.dataDictionaryId;
	}

	public void setDataDictionaryId(long dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataDictName() {
		return this.dataDictName;
	}

	public void setDataDictName(String dataDictName) {
		this.dataDictName = dataDictName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public Long getFormID() {
		return this.formID;
	}

	public void setFormID(Long formID) {
		this.formID = formID;
	}

	public boolean getImportable() {
		return this.importable;
	}

	public void setImportable(boolean importable) {
		this.importable = importable;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Set<CompanyDocumentShortList> getCompanyDocumentShortLists() {
		return this.companyDocumentShortLists;
	}

	public void setCompanyDocumentShortLists(
			Set<CompanyDocumentShortList> companyDocumentShortLists) {
		this.companyDocumentShortLists = companyDocumentShortLists;
	}

	public Set<CompanyEmployeeShortList> getCompanyEmployeeShortLists() {
		return this.companyEmployeeShortLists;
	}

	public void setCompanyEmployeeShortLists(
			Set<CompanyEmployeeShortList> companyEmployeeShortLists) {
		this.companyEmployeeShortLists = companyEmployeeShortLists;
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

	public Set<DynamicFormFieldRefValue> getDynamicFormFieldRefValues() {
		return this.dynamicFormFieldRefValues;
	}

	public void setDynamicFormFieldRefValues(
			Set<DynamicFormFieldRefValue> dynamicFormFieldRefValues) {
		this.dynamicFormFieldRefValues = dynamicFormFieldRefValues;
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

	public Set<EmpDataImportTemplateField> getEmpDataImportTemplateFields() {
		return this.empDataImportTemplateFields;
	}

	public void setEmpDataImportTemplateFields(
			Set<EmpDataImportTemplateField> empDataImportTemplateFields) {
		this.empDataImportTemplateFields = empDataImportTemplateFields;
	}

	public Set<EntityListViewField> getEntityListViewFields() {
		return this.entityListViewFields;
	}

	public void setEntityListViewFields(
			Set<EntityListViewField> entityListViewFields) {
		this.entityListViewFields = entityListViewFields;
	}

	public Set<MultiLingualData> getMultiLingualData() {
		return this.multiLingualData;
	}

	public void setMultiLingualData(Set<MultiLingualData> multiLingualData) {
		this.multiLingualData = multiLingualData;
	}

	public Set<LeaveSchemeTypeShortList> getLeaveSchemeTypeShortLists() {
		return this.leaveSchemeTypeShortLists;
	}

	public void setLeaveSchemeTypeShortLists(
			Set<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists) {
		this.leaveSchemeTypeShortLists = leaveSchemeTypeShortLists;
	}

	public Set<ClaimTemplateItemShortlist> getClaimTemplateItemShortlists() {
		return claimTemplateItemShortlists;
	}

	public void setClaimTemplateItemShortlists(
			Set<ClaimTemplateItemShortlist> claimTemplateItemShortlists) {
		this.claimTemplateItemShortlists = claimTemplateItemShortlists;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}