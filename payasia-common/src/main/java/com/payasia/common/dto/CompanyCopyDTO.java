package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;

public class CompanyCopyDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int companyId;
	private Long employeeId;
	private String copyCompanyName;
	private String copyCompanyCode;
	private Boolean copyCompanyFormDesigner;
	private Boolean copyCompanyImportTemplate;
	private Boolean copyCompanyExportTemplate;
	
	private Boolean copyEmployeeFormDesigner;
	private Boolean copyEmployeeImportTemplate;
	private Boolean copyEmployeeExportTemplate;
	
	private Boolean copyPayslipFormDesigner;
	private Boolean copyPayslipImportTemplate;
	private Boolean copyPayslipExportTemplate;
	private Boolean copyPayslipDesigner;
	
	private HashMap<Long, Long> dataDictionaryMap;
	private Boolean status;
	private Long newCompanyID;
	
	private Boolean copyLeavePreference;
	private Boolean copyClaimPreference;
	private Boolean copyLeaveType;
	private Boolean copyClaimItem;
	private Boolean copyLeaveScheme;
	private Boolean copyClaimTemplate;
	private Boolean copyCompanyCalendar;
	private Boolean copyClaimBatch;
	private Boolean copyHolidayCalendar;
	private Boolean copyCompanyRole;
	private Boolean copyCompanyPrivilege;
	
	
	public Boolean getCopyCompanyImportTemplate() {
		return copyCompanyImportTemplate;
	}
	public void setCopyCompanyImportTemplate(Boolean copyCompanyImportTemplate) {
		this.copyCompanyImportTemplate = copyCompanyImportTemplate;
	}
	public Boolean getCopyCompanyExportTemplate() {
		return copyCompanyExportTemplate;
	}
	public void setCopyCompanyExportTemplate(Boolean copyCompanyExportTemplate) {
		this.copyCompanyExportTemplate = copyCompanyExportTemplate;
	}
	public Boolean getCopyEmployeeImportTemplate() {
		return copyEmployeeImportTemplate;
	}
	public void setCopyEmployeeImportTemplate(Boolean copyEmployeeImportTemplate) {
		this.copyEmployeeImportTemplate = copyEmployeeImportTemplate;
	}
	public Boolean getCopyEmployeeExportTemplate() {
		return copyEmployeeExportTemplate;
	}
	public void setCopyEmployeeExportTemplate(Boolean copyEmployeeExportTemplate) {
		this.copyEmployeeExportTemplate = copyEmployeeExportTemplate;
	}
	public Boolean getCopyPayslipImportTemplate() {
		return copyPayslipImportTemplate;
	}
	public void setCopyPayslipImportTemplate(Boolean copyPayslipImportTemplate) {
		this.copyPayslipImportTemplate = copyPayslipImportTemplate;
	}
	public Boolean getCopyPayslipExportTemplate() {
		return copyPayslipExportTemplate;
	}
	public void setCopyPayslipExportTemplate(Boolean copyPayslipExportTemplate) {
		this.copyPayslipExportTemplate = copyPayslipExportTemplate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public HashMap<Long, Long> getDataDictionaryMap() {
		return dataDictionaryMap;
	}
	public void setDataDictionaryMap(HashMap<Long, Long> dataDictionaryMap) {
		this.dataDictionaryMap = dataDictionaryMap;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Long getNewCompanyID() {
		return newCompanyID;
	}
	public void setNewCompanyID(Long newCompanyID) {
		this.newCompanyID = newCompanyID;
	}
	public String getCopyCompanyName() {
		return copyCompanyName;
	}
	public void setCopyCompanyName(String copyCompanyName) {
		this.copyCompanyName = copyCompanyName;
	}
	public String getCopyCompanyCode() {
		return copyCompanyCode;
	}
	public void setCopyCompanyCode(String copyCompanyCode) {
		this.copyCompanyCode = copyCompanyCode;
	}
	public Boolean getCopyCompanyFormDesigner() {
		return copyCompanyFormDesigner;
	}
	public void setCopyCompanyFormDesigner(Boolean copyCompanyFormDesigner) {
		this.copyCompanyFormDesigner = copyCompanyFormDesigner;
	}
	public Boolean getCopyEmployeeFormDesigner() {
		return copyEmployeeFormDesigner;
	}
	public void setCopyEmployeeFormDesigner(Boolean copyEmployeeFormDesigner) {
		this.copyEmployeeFormDesigner = copyEmployeeFormDesigner;
	}
	public Boolean getCopyPayslipFormDesigner() {
		return copyPayslipFormDesigner;
	}
	public void setCopyPayslipFormDesigner(Boolean copyPayslipFormDesigner) {
		this.copyPayslipFormDesigner = copyPayslipFormDesigner;
	}
	public Boolean getCopyPayslipDesigner() {
		return copyPayslipDesigner;
	}
	public void setCopyPayslipDesigner(Boolean copyPayslipDesigner) {
		this.copyPayslipDesigner = copyPayslipDesigner;
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Boolean getCopyLeavePreference() {
		return copyLeavePreference;
	}
	public void setCopyLeavePreference(Boolean copyLeavePreference) {
		this.copyLeavePreference = copyLeavePreference;
	}
	public Boolean getCopyClaimPreference() {
		return copyClaimPreference;
	}
	public void setCopyClaimPreference(Boolean copyClaimPreference) {
		this.copyClaimPreference = copyClaimPreference;
	}
	public Boolean getCopyLeaveType() {
		return copyLeaveType;
	}
	public void setCopyLeaveType(Boolean copyLeaveType) {
		this.copyLeaveType = copyLeaveType;
	}
	public Boolean getCopyClaimItem() {
		return copyClaimItem;
	}
	public void setCopyClaimItem(Boolean copyClaimItem) {
		this.copyClaimItem = copyClaimItem;
	}
	public Boolean getCopyLeaveScheme() {
		return copyLeaveScheme;
	}
	public void setCopyLeaveScheme(Boolean copyLeaveScheme) {
		this.copyLeaveScheme = copyLeaveScheme;
	}
	public Boolean getCopyClaimTemplate() {
		return copyClaimTemplate;
	}
	public void setCopyClaimTemplate(Boolean copyClaimTemplate) {
		this.copyClaimTemplate = copyClaimTemplate;
	}
	public Boolean getCopyCompanyCalendar() {
		return copyCompanyCalendar;
	}
	public void setCopyCompanyCalendar(Boolean copyCompanyCalendar) {
		this.copyCompanyCalendar = copyCompanyCalendar;
	}
	public Boolean getCopyClaimBatch() {
		return copyClaimBatch;
	}
	public void setCopyClaimBatch(Boolean copyClaimBatch) {
		this.copyClaimBatch = copyClaimBatch;
	}
	public Boolean getCopyHolidayCalendar() {
		return copyHolidayCalendar;
	}
	public void setCopyHolidayCalendar(Boolean copyHolidayCalendar) {
		this.copyHolidayCalendar = copyHolidayCalendar;
	}
	public Boolean getCopyCompanyRole() {
		return copyCompanyRole;
	}
	public void setCopyCompanyRole(Boolean copyCompanyRole) {
		this.copyCompanyRole = copyCompanyRole;
	}
	public Boolean getCopyCompanyPrivilege() {
		return copyCompanyPrivilege;
	}
	public void setCopyCompanyPrivilege(Boolean copyCompanyPrivilege) {
		this.copyCompanyPrivilege = copyCompanyPrivilege;
	}

}
