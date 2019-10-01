package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Entity_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Entity_Master")
public class EntityMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Entity_ID")
	private long entityId;

	@Column(name = "Entity_Desc")
	private String entityDesc;

	@Column(name = "Entity_Name")
	private String entityName;

	@Column(name = "Label_Key")
	private String labelKey;

	 
	@OneToMany(mappedBy = "entityMaster")
	private Set<DataDictionary> dataDictionaries;

	 
	@OneToMany(mappedBy = "entityMaster")
	private Set<DataImportHistory> dataImportHistories;

	 
	@OneToMany(mappedBy = "entityMaster")
	private Set<DynamicForm> dynamicForms;

	 
	@OneToMany(mappedBy = "entityMaster")
	private Set<EmpDataExportTemplate> empDataExportTemplates;

	 
	@OneToMany(mappedBy = "entityMaster")
	private Set<EmpDataImportTemplate> empDataImportTemplates;

	 
	@OneToMany(mappedBy = "entityMaster")
	private Set<EntityListView> entityListViews;

	 
	@OneToMany(mappedBy = "entityMaster")
	private Set<EntityTableMapping> entityTableMappings;

	 
	@OneToMany(mappedBy = "entityMaster")
	private Set<EmpDataExportTemplateField> empDataExportTemplateFields;

	public EntityMaster() {
	}

	public long getEntityId() {
		return this.entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public String getEntityDesc() {
		return this.entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Set<DataDictionary> getDataDictionaries() {
		return this.dataDictionaries;
	}

	public void setDataDictionaries(Set<DataDictionary> dataDictionaries) {
		this.dataDictionaries = dataDictionaries;
	}

	public Set<DataImportHistory> getDataImportHistories() {
		return this.dataImportHistories;
	}

	public void setDataImportHistories(
			Set<DataImportHistory> dataImportHistories) {
		this.dataImportHistories = dataImportHistories;
	}

	public Set<DynamicForm> getDynamicForms() {
		return this.dynamicForms;
	}

	public void setDynamicForms(Set<DynamicForm> dynamicForms) {
		this.dynamicForms = dynamicForms;
	}

	public Set<EmpDataExportTemplate> getEmpDataExportTemplates() {
		return this.empDataExportTemplates;
	}

	public void setEmpDataExportTemplates(
			Set<EmpDataExportTemplate> empDataExportTemplates) {
		this.empDataExportTemplates = empDataExportTemplates;
	}

	public Set<EmpDataImportTemplate> getEmpDataImportTemplates() {
		return this.empDataImportTemplates;
	}

	public void setEmpDataImportTemplates(
			Set<EmpDataImportTemplate> empDataImportTemplates) {
		this.empDataImportTemplates = empDataImportTemplates;
	}

	public Set<EntityListView> getEntityListViews() {
		return this.entityListViews;
	}

	public void setEntityListViews(Set<EntityListView> entityListViews) {
		this.entityListViews = entityListViews;
	}

	public Set<EntityTableMapping> getEntityTableMappings() {
		return this.entityTableMappings;
	}

	public void setEntityTableMappings(
			Set<EntityTableMapping> entityTableMappings) {
		this.entityTableMappings = entityTableMappings;
	}

	public Set<EmpDataExportTemplateField> getEmpDataExportTemplateFields() {
		return empDataExportTemplateFields;
	}

	public void setEmpDataExportTemplateFields(
			Set<EmpDataExportTemplateField> empDataExportTemplateFields) {
		this.empDataExportTemplateFields = empDataExportTemplateFields;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

}