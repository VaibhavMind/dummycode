package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.payasia.common.dto.CompanyDynamicFormDTO;

@JsonInclude(Include.NON_DEFAULT)
public class CompanyForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long companyId;
	private String companyName;
	private Integer countryId;
	private String companyCode;
	private Integer part;
	private String status;
	private String deleteMsg;
	private String message;
	private String companyStatus;

	private boolean hasHrisModule;
	private boolean hasClaimModule;
	private boolean hasLeaveModule;
	private boolean hasMobileModule;
	private boolean hasLundinTimesheetModule;
	private boolean hasLionTimesheetModule;
	private boolean hasCoherentTimesheetModule;

	private String payslipImportType;

	public String getPayslipImportType() {
		return payslipImportType;
	}

	public void setPayslipImportType(String payslipImportType) {
		this.payslipImportType = payslipImportType;
	}

	private String currency;
	private Long currencyId;

	private Long timeZoneId;
	private String gmtOffset;
	private String timeZoneName;

	private Long groupId;
	private String groupName;
	private String groupCode;

	private Integer finYearId;
	private String finYear;

	private String dateFormat;

	private Long financialYearId;
	private String financialYearValue;

	private Long paySlipFrequencyId;
	private String paySlipfrequnecyValue;

	private String dateFormatId;
	private String dateFormatValue;

	private Long countryID;
	private String countryValue;
	private String countryName;

	private Long entityValue;

	private Long dynamicFormRecordId;
	private Long dynamicFormTableRecordId;
	private String mode;

	private Boolean audit;
	private boolean active;
	private boolean demoCompany;
	
	private Boolean isTwoFactorAuth;


	public Boolean getAudit() {
		return audit;
	}

	public void setAudit(Boolean audit) {
		this.audit = audit;
	}

	public Long getDynamicFormRecordId() {
		return dynamicFormRecordId;
	}

	public void setDynamicFormRecordId(Long dynamicFormRecordId) {
		this.dynamicFormRecordId = dynamicFormRecordId;
	}

	public Long getDynamicFormTableRecordId() {
		return dynamicFormTableRecordId;
	}

	public void setDynamicFormTableRecordId(Long dynamicFormTableRecordId) {
		this.dynamicFormTableRecordId = dynamicFormTableRecordId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	List<CompanyForm> updatedCompanyList;

	private CompanyDynamicFormDTO companyDynamicFormDTO;

	public CompanyDynamicFormDTO getCompanyDynamicFormDTO() {
		return companyDynamicFormDTO;
	}

	public void setCompanyDynamicFormDTO(CompanyDynamicFormDTO companyDynamicFormDTO) {
		this.companyDynamicFormDTO = companyDynamicFormDTO;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeleteMsg() {
		return deleteMsg;
	}

	public void setDeleteMsg(String deleteMsg) {
		this.deleteMsg = deleteMsg;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	private int tableRecordId;
	private List<CompanyForm> tableDataList;

	public Integer getPart() {
		return part;
	}

	public void setPart(Integer part) {
		this.part = part;
	}

	public List<CompanyForm> getTableDataList() {
		return tableDataList;
	}

	public void setTableDataList(List<CompanyForm> tableDataList) {
		this.tableDataList = tableDataList;
	}

	public int getTableRecordId() {
		return tableRecordId;
	}

	public void setTableRecordId(int tableRecordId) {
		this.tableRecordId = tableRecordId;
	}

	public String getFinYear() {
		return finYear;
	}

	public void setFinYear(String finYear) {
		this.finYear = finYear;
	}

	public String getCompanyStatus() {
		return companyStatus;
	}

	public void setCompanyStatus(String companyStatus) {
		this.companyStatus = companyStatus;
	}

	public List<CompanyForm> getUpdatedCompanyList() {
		return updatedCompanyList;
	}

	public void setUpdatedCompanyList(List<CompanyForm> updatedCompanyList) {
		this.updatedCompanyList = updatedCompanyList;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Long getCountryID() {
		return countryID;
	}

	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}

	public String getCountryValue() {
		return countryValue;
	}

	public void setCountryValue(String countryValue) {
		this.countryValue = countryValue;
	}

	public String getDateFormatId() {
		return dateFormatId;
	}

	public void setDateFormatId(String dateFormatId) {
		this.dateFormatId = dateFormatId;
	}

	public String getDateFormatValue() {
		return dateFormatValue;
	}

	public void setDateFormatValue(String dateFormatValue) {
		this.dateFormatValue = dateFormatValue;
	}

	public Long getPaySlipFrequencyId() {
		return paySlipFrequencyId;
	}

	public void setPaySlipFrequencyId(Long paySlipFrequencyId) {
		this.paySlipFrequencyId = paySlipFrequencyId;
	}

	public String getPaySlipfrequnecyValue() {
		return paySlipfrequnecyValue;
	}

	public void setPaySlipfrequnecyValue(String paySlipfrequnecyValue) {
		this.paySlipfrequnecyValue = paySlipfrequnecyValue;
	}

	public Long getFinancialYearId() {
		return financialYearId;
	}

	public void setFinancialYearId(Long financialYearId) {
		this.financialYearId = financialYearId;
	}

	public String getFinancialYearValue() {
		return financialYearValue;
	}

	public void setFinancialYearValue(String financialYearValue) {
		this.financialYearValue = financialYearValue;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getFinYearId() {
		return finYearId;
	}

	public void setFinYearId(Integer finYearId) {
		this.finYearId = finYearId;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/** The page. */
	private String page;

	/** Total pages for the query. */
	private String total;

	/** Total number of records for the query. */
	private String records;

	/** An array that contains the actual objects. */
	private List<CompanyForm> rows;

	/**
	 * Gets the page.
	 * 
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	/**
	 * Sets the page.
	 * 
	 * @param page
	 *            the new page
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * Gets the total.
	 * 
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * Sets the total.
	 * 
	 * @param total
	 *            the new total
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	/**
	 * Gets the records.
	 * 
	 * @return the records
	 */
	public String getRecords() {
		return records;
	}

	/**
	 * Sets the records.
	 * 
	 * @param records
	 *            the new records
	 */
	public void setRecords(String records) {
		this.records = records;
	}

	/**
	 * Gets the rows.
	 * 
	 * @return the rows
	 */
	public List<CompanyForm> getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 * 
	 * @param rows
	 *            the new rows
	 */
	public void setRows(List<CompanyForm> rows) {
		this.rows = rows;
	}

	public String getTimeZoneName() {
		return timeZoneName;
	}

	public void setTimeZoneName(String timeZoneName) {
		this.timeZoneName = timeZoneName;
	}

	public Long getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(Long timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Long getEntityValue() {
		return entityValue;
	}

	public void setEntityValue(Long entityValue) {
		this.entityValue = entityValue;
	}

	public String getGmtOffset() {
		return gmtOffset;
	}

	public void setGmtOffset(String gmtOffset) {
		this.gmtOffset = gmtOffset;
	}

	public boolean isHasHrisModule() {
		return hasHrisModule;
	}

	public void setHasHrisModule(boolean hasHrisModule) {
		this.hasHrisModule = hasHrisModule;
	}

	public boolean isHasClaimModule() {
		return hasClaimModule;
	}

	public void setHasClaimModule(boolean hasClaimModule) {
		this.hasClaimModule = hasClaimModule;
	}

	public boolean isHasLeaveModule() {
		return hasLeaveModule;
	}

	public void setHasLeaveModule(boolean hasLeaveModule) {
		this.hasLeaveModule = hasLeaveModule;
	}

	public boolean isHasMobileModule() {
		return hasMobileModule;
	}

	public void setHasMobileModule(boolean hasMobileModule) {
		this.hasMobileModule = hasMobileModule;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDemoCompany() {
		return demoCompany;
	}

	public void setDemoCompany(boolean demoCompany) {
		this.demoCompany = demoCompany;
	}

	public boolean isHasLundinTimesheetModule() {
		return hasLundinTimesheetModule;
	}

	public void setHasLundinTimesheetModule(boolean hasLundinTimesheetModule) {
		this.hasLundinTimesheetModule = hasLundinTimesheetModule;
	}

	public boolean isHasLionTimesheetModule() {
		return hasLionTimesheetModule;
	}

	public void setHasLionTimesheetModule(boolean hasLionTimesheetModule) {
		this.hasLionTimesheetModule = hasLionTimesheetModule;
	}

	public boolean isHasCoherentTimesheetModule() {
		return hasCoherentTimesheetModule;
	}

	public void setHasCoherentTimesheetModule(boolean hasCoherentTimesheetModule) {
		this.hasCoherentTimesheetModule = hasCoherentTimesheetModule;
	}

	public Boolean getIsTwoFactorAuth() {
		return isTwoFactorAuth;
	}

	public void setIsTwoFactorAuth(Boolean isTwoFactorAuth) {
		this.isTwoFactorAuth = isTwoFactorAuth;
	}

	
}
