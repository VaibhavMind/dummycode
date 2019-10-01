package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.payasia.dao.bean.DataDictionary;

public class DataExportForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -308378432052884527L;
	private String finalFileName;
	private long templateId;
	private String scope;
	private String outputType;
	private String category;
	private long entityId;
	private String description;
	private String templateName;
	private List<DBTableInformationForm> dbTableInformationFormList;
	private List<ExcelExportFiltersForm> excelExportFilterList;
	private boolean includePrefix;
	private boolean includeSuffix;
	private boolean includeTemplateNameAsPrefix;
	private boolean includeTimestampAsSuffix;
	private String prefix;
	private String suffix;
	private Workbook workbook;
	private String[] selectedComapnyIds;
	private Map<String, DataDictionary> employeeDataMap;
	private Map<String, DataDictionary> companyDataMap;
	private String exportType;
	private char csvSeparator;
	
	
	private Map<Integer,List<String[]>> csvRecordMap ;
	
	
	public String getExportType() {
		return exportType;
	}
	public void setExportType(String exportType) {
		this.exportType = exportType;
	}
	public Map<String, DataDictionary> getEmployeeDataMap() {
		return employeeDataMap;
	}
	public void setEmployeeDataMap(Map<String, DataDictionary> employeeDataMap) {
		this.employeeDataMap = employeeDataMap;
	}
	public Map<String, DataDictionary> getCompanyDataMap() {
		return companyDataMap;
	}
	public void setCompanyDataMap(Map<String, DataDictionary> companyDataMap) {
		this.companyDataMap = companyDataMap;
	}
	public String[] getSelectedComapnyIds() {
		return selectedComapnyIds;
	}
	public void setSelectedComapnyIds(String[] selectedComapnyIds) {
		if (selectedComapnyIds != null) {
			this.selectedComapnyIds = Arrays.copyOf(selectedComapnyIds, selectedComapnyIds.length);
		}
	}
	public String getFinalFileName() {
		return finalFileName;
	}
	public void setFinalFileName(String finalFileName) {
		this.finalFileName = finalFileName;
	}
	public boolean isIncludePrefix() {
		return includePrefix;
	}
	public void setIncludePrefix(boolean includePrefix) {
		this.includePrefix = includePrefix;
	}
	public boolean isIncludeSuffix() {
		return includeSuffix;
	}
	public void setIncludeSuffix(boolean includeSuffix) {
		this.includeSuffix = includeSuffix;
	}
	public boolean isIncludeTemplateNameAsPrefix() {
		return includeTemplateNameAsPrefix;
	}
	public void setIncludeTemplateNameAsPrefix(boolean includeTemplateNameAsPrefix) {
		this.includeTemplateNameAsPrefix = includeTemplateNameAsPrefix;
	}
	public boolean isIncludeTimestampAsSuffix() {
		return includeTimestampAsSuffix;
	}
	public void setIncludeTimestampAsSuffix(boolean includeTimestampAsSuffix) {
		this.includeTimestampAsSuffix = includeTimestampAsSuffix;
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
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public List<DBTableInformationForm> getDbTableInformationFormList() {
		return dbTableInformationFormList;
	}
	public void setDbTableInformationFormList(
			List<DBTableInformationForm> dbTableInformationFormList) {
		this.dbTableInformationFormList = dbTableInformationFormList;
	}
	public List<ExcelExportFiltersForm> getExcelExportFilterList() {
		return excelExportFilterList;
	}
	public void setExcelExportFilterList(
			List<ExcelExportFiltersForm> excelExportFilterList) {
		this.excelExportFilterList = excelExportFilterList;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	public Map<Integer,List<String[]>> getCsvRecordMap() {
		return csvRecordMap;
	}
	public void setCsvRecordMap(Map<Integer,List<String[]>> csvRecordMap) {
		this.csvRecordMap = csvRecordMap;
	}
	public char getCsvSeparator() {
		return csvSeparator;
	}
	public void setCsvSeparator(char csvSeparator) {
		this.csvSeparator = csvSeparator;
	}
	
	
}
