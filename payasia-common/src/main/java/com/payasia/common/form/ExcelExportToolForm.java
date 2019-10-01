package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.SectionInfoDTO;


public class ExcelExportToolForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 721677846009756045L;
	private long basicSectionId;
	private long templateId;
	private long entityId;
	private String entityName;
	private long companyId;
	private Long formId;
	private String category;
	private String templateName;
	private String templateRadio;
	private String prefix;
	private String suffix;
	private Integer custTablePosition;
	private boolean prefixCheck;
	private boolean suffixCheck;
	private boolean templateNamePrefix;
	private boolean timeStampSuffix;
	private String description;
	private List<DBTableInformationForm> dbTableInformationFormList;
	private List<DynamicTableDTO> tableNames;
	private List<ExcelExportFiltersForm> excelExportFiltersFormList;
	private List<SectionInfoDTO> sectionList;
	private String scope;
	private String scopeEdit;
    private Boolean multipleSection;
	private Boolean multipleSectionEdit;

	public Boolean getMultipleSection() {
		return multipleSection;
	}

	public void setMultipleSection(Boolean multipleSection) {
		this.multipleSection = multipleSection;
	}

	public Boolean getMultipleSectionEdit() {
		return multipleSectionEdit;
	}

	public void setMultipleSectionEdit(Boolean multipleSectionEdit) {
		this.multipleSectionEdit = multipleSectionEdit;
	}

	public String getScopeEdit() {
		return scopeEdit;
	}

	public void setScopeEdit(String scopeEdit) {
		this.scopeEdit = scopeEdit;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public Integer getCustTablePosition() {
		return custTablePosition;
	}

	public void setCustTablePosition(Integer custTablePosition) {
		this.custTablePosition = custTablePosition;
	}

	public List<DynamicTableDTO> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<DynamicTableDTO> tableNames) {
		this.tableNames = tableNames;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateRadio() {
		return templateRadio;
	}

	public void setTemplateRadio(String templateRadio) {
		this.templateRadio = templateRadio;
	}

	public boolean isPrefixCheck() {
		return prefixCheck;
	}

	public void setPrefixCheck(boolean prefixCheck) {
		this.prefixCheck = prefixCheck;
	}

	public boolean isSuffixCheck() {
		return suffixCheck;
	}

	public void setSuffixCheck(boolean suffixCheck) {
		this.suffixCheck = suffixCheck;
	}

	public boolean isTemplateNamePrefix() {
		return templateNamePrefix;
	}

	public void setTemplateNamePrefix(boolean templateNamePrefix) {
		this.templateNamePrefix = templateNamePrefix;
	}

	public boolean isTimeStampSuffix() {
		return timeStampSuffix;
	}

	public void setTimeStampSuffix(boolean timeStampSuffix) {
		this.timeStampSuffix = timeStampSuffix;
	}

	public List<DBTableInformationForm> getDbTableInformationFormList() {
		return dbTableInformationFormList;
	}

	public void setDbTableInformationFormList(
			List<DBTableInformationForm> dbTableInformationFormList) {
		this.dbTableInformationFormList = dbTableInformationFormList;
	}

	public List<ExcelExportFiltersForm> getExcelExportFiltersFormList() {
		return excelExportFiltersFormList;
	}

	public void setExcelExportFiltersFormList(
			List<ExcelExportFiltersForm> excelExportFiltersFormList) {
		this.excelExportFiltersFormList = excelExportFiltersFormList;
	}

	public List<SectionInfoDTO> getSectionList() {
		return sectionList;
	}

	public void setSectionList(List<SectionInfoDTO> sectionList) {
		this.sectionList = sectionList;
	}

	public long getBasicSectionId() {
		return basicSectionId;
	}

	public void setBasicSectionId(long basicSectionId) {
		this.basicSectionId = basicSectionId;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	

	
	

}
