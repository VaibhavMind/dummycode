package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.SectionInfoDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExcelImportToolForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8327439945440658719L;
	private Long formId;
	private Integer custTablePosition;
	private long templateId;
	private String templateName;
	private Workbook workbook;
	private long entityId;
	private long companyId;
	private String category;
	private String templateDesc;
	private String transactionType;
	private String uploadType;
	private List<DBTableInformationForm> dbTableInformationFormList;
	private List<DynamicTableDTO> tableNames;
	private List<SectionInfoDTO> sectionList;
	private long baseSectionId;
	
	
	
	public List<SectionInfoDTO> getSectionList() {
		return sectionList;
	}
	public void setSectionList(List<SectionInfoDTO> sectionList) {
		this.sectionList = sectionList;
	}
	public long getBaseSectionId() {
		return baseSectionId;
	}
	public void setBaseSectionId(long baseSectionId) {
		this.baseSectionId = baseSectionId;
	}
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getUploadType() {
		return uploadType;
	}
	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	public List<DBTableInformationForm> getDbTableInformationFormList() {
		return dbTableInformationFormList;
	}
	public void setDbTableInformationFormList(
			List<DBTableInformationForm> dbTableInformationFormList) {
		this.dbTableInformationFormList = dbTableInformationFormList;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTemplateDesc() {
		return templateDesc;
	}
	public void setTemplateDesc(String templateDesc) {
		this.templateDesc = templateDesc;
	}
	public List<DynamicTableDTO> getTableNames() {
		return tableNames;
	}
	public void setTableNames(List<DynamicTableDTO> tableNames) {
		this.tableNames = tableNames;
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
	


}
