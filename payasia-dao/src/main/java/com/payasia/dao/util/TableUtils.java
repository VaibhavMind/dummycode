package com.payasia.dao.util;

import java.util.HashMap;

import com.payasia.common.util.SortConstants;
import com.payasia.dao.bean.*;

/**
 * The Class TableUtils.
 */
public class TableUtils {

	/** The col map. */
	public static HashMap<String, String> colMap = new HashMap<String, String>();

	static {
		colMapEmployeeNumberHistory();
		colMapCompanyExchangeRate();
		colMapPasswordSecretQuestion();
		colMapManageRoles();
		colMapManagePrivilege();
		colMapManageUsers();
		colMapEmployee();
		colMapCompanyDocument();
		colMapPayDataCollection();
		colMapHRLetter();
		colMapEmpNumberHistory();
		colMapEmpNumberSeries();
		colMapHolidayDefinition();
		colMapHolidayMaster();
		colMapWorkFlowDefinition();
		colMapEmailTemplate();
		colMapSwitchUser();
		colMapLeaveTypeDefinition();
		colMapLeaveBatchDefinition();
		colMapClaimBatchDefinition();
		colMapEmpDataImportTemplate();
		colMapEmpDataExportTemplate();
		colMapClaimItemDefinition();
		colMapClaimCategory();
		colMapDataDictionary();
		colMapCalendarTemplate();
		colMapCompanyDocumentDetail();
		colMapCompany();
		colMapEmployeeDocumentCenter();
		colMapLeaveScheme();
		colMapLeaveBalanceSummary();
		colMapClaimTemplate();
		colMapOTTemplate();
		colMapAssignCompany();
		colMapLeaveReviewer();
		colMapAssignLeaveScheme();
		colMapEmployeeLeaveDistribution();
		colMapClaimReviewer();
		colMapDynamicForm();
		colMapEmployeeCalendar();
		colMapLeaveGrant();
		colMapHolidayCalendar();
		colMapPayslipRelease();
		colMapHrisReviewer();
		colMapHrisPendingItems();
		colMapLundinOTReviewer();
		colMapDiscussionBoard();
		colMapWorkdayFTPImportHistory();
		colMapMyClaim();
	}

	/**
	 * Col map employee number history.
	 */
	private static void colMapEmployeeNumberHistory() {
		colMap.put(EmpNumberHistory.class
				+ SortConstants.CHANGE_EMPLOYEE_NUMBER_CHANGED_BY,
				EmpNumberHistory_.employee2.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.CHANGE_EMPLOYEE_NUMBER_CHANGE_ON,
				EmpNumberHistory_.changedDate.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.CHANGE_EMPLOYEE_NUMBER_CHANGE_TO,
				EmpNumberHistory_.changedEmpNo.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.CHANGE_EMPLOYEE_NUMBER_CHANGED_REASON,
				EmpNumberHistory_.reason.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.CHANGE_EMPLOYEE_NUMBER_OLD_EMPLOYEE_NO,
				EmpNumberHistory_.prevEmpNo.getName());
	}

	private static void colMapLeaveGrant() {
		colMap.put(LeaveGrantBatch.class
				+ SortConstants.LEAVE_GRANTER_BATCH_DATE,
				LeaveGrantBatch_.batchDate.getName());
		colMap.put(LeaveGrantBatch.class
				+ SortConstants.LEAVE_GRANTER_BATCH_NUMBER,
				LeaveGrantBatch_.batchNumber.getName());
		colMap.put(LeaveGrantBatchDetail.class
				+ SortConstants.LEAVE_GRANTER_EMPLOYEE_COUNT,
				LeaveGrantBatchDetail_.employeesCount.getName());
		colMap.put(LeaveGrantBatchDetail.class
				+ SortConstants.LEAVE_GRANTER_FROM_PERIOD,
				LeaveGrantBatchDetail_.fromPeriod.getName());
		colMap.put(LeaveGrantBatchDetail.class
				+ SortConstants.LEAVE_GRANTER_TO_PERIOD,
				LeaveGrantBatchDetail_.toPeriod.getName());
		colMap.put(
				LeaveScheme.class + SortConstants.LEAVE_GRANTER_LEAVE_SCHEME,
				LeaveScheme_.schemeName.getName());
		colMap.put(LeaveTypeMaster.class
				+ SortConstants.LEAVE_GRANTER_LEAVE_TYPE,
				LeaveTypeMaster_.leaveTypeName.getName());
		colMap.put(LeaveGrantBatchEmployeeDetail.class
				+ SortConstants.LEAVE_GRANTER_GRANTED_DAYS,
				LeaveGrantBatchEmployeeDetail_.grantedDays.getName());
		colMap.put(
				Employee.class + SortConstants.LEAVE_GRANTER_EMPLOYEE_NUMBER,
				Employee_.employeeNumber.getName());
		colMap.put(Employee.class + SortConstants.LEAVE_GRANTER_EMPLOYEE_NAME,
				Employee_.firstName.getName());
		colMap.put(Employee.class + SortConstants.LEAVE_GRANTER_HIRE_DATE,
				Employee_.hireDate.getName());

	}

	private static void colMapEmployeeCalendar() {
		colMap.put(CompanyCalendarTemplate.class
				+ SortConstants.EMPLOYEE_CALENDAR_TEMPLATE_NAME,
				CompanyCalendarTemplate_.templateName.getName());
		colMap.put(EmployeeCalendarConfig.class
				+ SortConstants.EMPLOYEE_CALENDAR_START_DATE,
				EmployeeCalendarConfig_.startDate.getName());
		colMap.put(EmployeeCalendarConfig.class
				+ SortConstants.EMPLOYEE_CALENDAR_END_DATE,
				EmployeeCalendarConfig_.endDate.getName());
		colMap.put(Employee.class
				+ SortConstants.EMPLOYEE_CALENDAR_EMPLOYEE_NAME,
				Employee_.firstName.getName());
		colMap.put(Employee.class
				+ SortConstants.EMPLOYEE_CALENDAR_EMPLOYEE_NUMBER,
				Employee_.employeeNumber.getName());

	}

	private static void colMapLeaveBalanceSummary() {
		colMap.put(LeaveApplication.class + SortConstants.START_DATE,
				LeaveApplication_.startDate.getName());
		colMap.put(LeaveApplication.class + SortConstants.END_DATE,
				LeaveApplication_.endDate.getName());
		colMap.put(LeaveApplication.class
				+ SortConstants.LEAVE_APPLICATION_NO_OF_DAYS,
				LeaveApplication_.totalDays.getName());
		colMap.put(LeaveTypeMaster.class
				+ SortConstants.LEAVE_REVIEWER_LEAVE_TYPE,
				LeaveTypeMaster_.leaveTypeName.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_NUMBER,
				Employee_.employeeNumber.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_NAME,
				Employee_.firstName.getName());
		colMap.put(EmployeeLeaveSchemeType.class
				+ SortConstants.EMPLOYEE_LEAVE_SCHEME_CREDITED,
				EmployeeLeaveSchemeType_.credited.getName());
		colMap.put(EmployeeLeaveSchemeType.class
				+ SortConstants.EMPLOYEE_LEAVE_SCHEME_BALANCE,
				EmployeeLeaveSchemeType_.balance.getName());
		colMap.put(EmployeeLeaveSchemeType.class
				+ SortConstants.EMPLOYEE_LEAVE_SCHEME_CARRIED_FORWARD,
				EmployeeLeaveSchemeType_.carriedForward.getName());
		colMap.put(EmployeeLeaveSchemeType.class
				+ SortConstants.EMPLOYEE_LEAVE_SCHEME_ENCASHED,
				EmployeeLeaveSchemeType_.encashed.getName());
		colMap.put(EmployeeLeaveSchemeType.class
				+ SortConstants.EMPLOYEE_LEAVE_SCHEME_FORTFEITED,
				EmployeeLeaveSchemeType_.forfeited.getName());
		colMap.put(EmployeeLeaveSchemeType.class
				+ SortConstants.EMPLOYEE_LEAVE_SCHEME_PENDING,
				EmployeeLeaveSchemeType_.pending.getName());
		colMap.put(EmployeeLeaveSchemeType.class
				+ SortConstants.EMPLOYEE_LEAVE_SCHEME_TAKEN,
				EmployeeLeaveSchemeType_.taken.getName());

	}

	private static void colMapDynamicForm() {
		colMap.put(DynamicForm.class + SortConstants.MANAGE_ROLES_TAB_NAME,
				DynamicForm_.tabName.getName());
	}

	/**
	 * Col map company exchange rate.
	 */
	private static void colMapCompanyExchangeRate() {
		colMap.put(CurrencyMaster.class + SortConstants.CURRENCY_NAME,
				CurrencyMaster_.currencyName.getName());
		colMap.put(CurrencyMaster.class + SortConstants.CUREENCY_ID,
				CurrencyMaster_.currencyId.getName());
		colMap.put(CompanyExchangeRate.class
				+ SortConstants.CUREENCY_START_DATE,
				CompanyExchangeRate_.startDate.getName());
		colMap.put(CompanyExchangeRate.class + SortConstants.CUREENCY_END_DATE,
				CompanyExchangeRate_.endDate.getName());
		colMap.put(CompanyExchangeRate.class
				+ SortConstants.CUREENCY_EXCHANGE_RATE,
				CompanyExchangeRate_.exchangeRate.getName());
	}

	/**
	 * Col map password secret question.
	 */
	private static void colMapPasswordSecretQuestion() {
		colMap.put(PasswordSecurityQuestionMaster.class
				+ SortConstants.SECURITY_QUESTION,
				PasswordSecurityQuestionMaster_.secretQuestion.getName());
		colMap.put(PasswordSecurityQuestionMaster.class
				+ SortConstants.PASS_SECURITY_QUESTION_ID,
				PasswordSecurityQuestionMaster_.pwdSecurityQuestionId.getName());
	}

	/**
	 * Col map manage roles.
	 */
	private static void colMapManageRoles() {
		colMap.put(RoleMaster.class + SortConstants.MANAGE_ROLES_ROLE_NAME,
				RoleMaster_.roleName.getName());
		colMap.put(RoleMaster.class + SortConstants.MANAGE_ROLES_ROLE_ID,
				RoleMaster_.roleId.getName());
	}

	/**
	 * Col map manage privilege.
	 */
	private static void colMapManagePrivilege() {
		colMap.put(PrivilegeMaster.class
				+ SortConstants.MANAGE_PRIVILEGE_ROLE_NAME,
				PrivilegeMaster_.privilegeName.getName());
		colMap.put(PrivilegeMaster.class
				+ SortConstants.MANAGE_ROLES_PRIVILEGE_ID,
				PrivilegeMaster_.privilegeId.getName());
	}

	/**
	 * Col map manage users.
	 */
	private static void colMapManageUsers() {
		colMap.put(Employee.class + SortConstants.EMPLOYEE_ID,
				Employee_.employeeId.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_FIRST_NAME,
				Employee_.firstName.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_LAST_NAME,
				Employee_.lastName.getName());
		 
		 
		colMap.put(Employee.class + SortConstants.EMPLOYEE_NUMBER,
				Employee_.employeeNumber.getName());
		colMap.put(Company.class + SortConstants.EMPLOYEE_COMPANY_ID,
				Company_.companyId.getName());
		colMap.put(Company.class + SortConstants.EMPLOYEE_COMPANY_NAME,
				Company_.companyName.getName());

	}

	/**
	 * Col map employee.
	 */
	private static void colMapEmployee() {
		colMap.put(Employee.class + SortConstants.EMPLOYEE_ID,
				Employee_.employeeId.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_NUMBER,
				Employee_.employeeNumber.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_NAME,
				Employee_.firstName.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_LAST_NAME,
				Employee_.lastName.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_FIRST_NAME,
				Employee_.firstName.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_STATUS,
				Employee_.status.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_STATUS_MSG,
				Employee_.status.getName());
		colMap.put(Employee.class + SortConstants.EMPLOYEE_EMAIL,
				Employee_.email.getName());
		colMap.put(Employee.class
				+ SortConstants.LEAVE_GRANTER_RESIGNATION_DATE,
				Employee_.resignationDate.getName());

		 
		 

		 
		 

	}

	/**
	 * Col map company document.
	 */
	private static void colMapCompanyDocument() {
		 
		 
		 
		 
		 
		 
		 
		colMap.put(CompanyDocument.class
				+ SortConstants.COMPANY_DOCUMENT_UPLOADED_DATE,
				CompanyDocument_.uploadedDate.getName());
		 
		 
		 

	}

	/**
	 * Col map pay code.
	 */
	private static void colMapPayDataCollection() {
		colMap.put(PayDataCollection.class
				+ SortConstants.PAYDATA_COLLECTION_AMOUNT,
				PayDataCollection_.amount.getName());
		colMap.put(PayDataCollection.class
				+ SortConstants.PAYDATA_COLLECTION_FROM_DATE,
				PayDataCollection_.startDate.getName());
		colMap.put(PayDataCollection.class
				+ SortConstants.PAYDATA_COLLECTION_TO_DATE,
				PayDataCollection_.endDate.getName());
		colMap.put(Paycode.class + SortConstants.PAYDATA_COLLECTION_PAYCODE,
				Paycode_.paycode.getName());
		colMap.put(Employee.class
				+ SortConstants.PAYDATA_COLLECTION_EMPLOYEE_NAME,
				Employee_.firstName.getName());
		colMap.put(Employee.class
				+ SortConstants.PAYDATA_COLLECTION_EMPLOYEE_NUMBER,
				Employee_.employeeNumber.getName());
	}

	/**
	 * Col map hr letter.
	 */
	private static void colMapHRLetter() {
		colMap.put(HRLetter.class + SortConstants.HR_LETTER_ID,
				HRLetter_.hrLetterId.getName());
		colMap.put(HRLetter.class + SortConstants.HR_LETTER_NAME,
				HRLetter_.letterName.getName());
		colMap.put(HRLetter.class + SortConstants.HR_LETTER_DESCRIPTION,
				HRLetter_.letterDesc.getName());
		colMap.put(Company.class + SortConstants.HR_LETTER_COMPANY_ID,
				Company_.companyId.getName());
		colMap.put(HRLetter.class + SortConstants.HR_LETTER_SUBJECT,
				HRLetter_.subject.getName());
		colMap.put(HRLetter.class + SortConstants.HR_LETTER_ACTIVE,
				HRLetter_.active.getName());
	}

	/**
	 * Col map emp number history.
	 */
	private static void colMapEmpNumberHistory() {
		colMap.put(EmpNumberHistory.class
				+ SortConstants.EMPLOYEE_NUMBER_HISTORY_ID,
				EmpNumberHistory_.empNoHistoryId.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.EMPLOYEE_NUMBER_HISTORY_OLD_EMP_NUMBER,
				EmpNumberHistory_.prevEmpNo.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGE_TO,
				EmpNumberHistory_.changedEmpNo.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGE_ON,
				EmpNumberHistory_.changedDate.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGED_BY,
				EmpNumberHistory_.employee2.getName());
		colMap.put(EmpNumberHistory.class
				+ SortConstants.EMPLOYEE_NUMBER_HISTORY_CHANGE_REASON,
				EmpNumberHistory_.reason.getName());

	}

	/**
	 * Col map emp number series.
	 */
	private static void colMapEmpNumberSeries() {

		colMap.put(EmpNoSeriesMaster.class
				+ SortConstants.EMPLOYEE_SERIES_NUMBER_DESCRIPTION,
				EmpNoSeriesMaster_.seriesDesc.getName());

		colMap.put(EmpNoSeriesMaster.class
				+ SortConstants.EMPLOYEE_SERIES_NUMBER_ACTIVE,
				EmpNoSeriesMaster_.active.getName());
		colMap.put(EmpNoSeriesMaster.class
				+ SortConstants.EMPLOYEE_SERIES_PREFIX,
				EmpNoSeriesMaster_.prefix.getName());
		colMap.put(EmpNoSeriesMaster.class
				+ SortConstants.EMPLOYEE_SERIES_SUFFIX,
				EmpNoSeriesMaster_.suffix.getName());

	}

	/**
	 * Col map holiday definition.
	 */
	private static void colMapHolidayDefinition() {
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 

	}

	/**
	 * Col map holiday Master.
	 */
	private static void colMapHolidayMaster() {
		colMap.put(
				HolidayConfigMaster.class + SortConstants.HOLIDAY_MASTER_DAY,
				HolidayConfigMaster_.holidayDate.getName());
		colMap.put(HolidayConfigMaster.class
				+ SortConstants.HOLIDAY_MASTER_DATE,
				HolidayConfigMaster_.holidayDate.getName());
		colMap.put(HolidayConfigMaster.class
				+ SortConstants.HOLIDAY_MASTER_OCCASSION,
				HolidayConfigMaster_.holidayDesc.getName());
		colMap.put(HolidayConfigMaster.class
				+ SortConstants.HOLIDAY_MASTER_COUNTRY_NAME,
				HolidayConfigMaster_.countryMaster.getName());
		colMap.put(HolidayConfigMaster.class
				+ SortConstants.HOLIDAY_MASTER_STATE_NAME,
				HolidayConfigMaster_.stateMaster.getName());
		colMap.put(HolidayConfigMaster.class
				+ SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DATE,
				HolidayConfigMaster_.holidayDate.getName());
		colMap.put(HolidayConfigMaster.class
				+ SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DESC,
				HolidayConfigMaster_.holidayDesc.getName());
	}

	/**
	 * Col map work flow definition.
	 */
	private static void colMapWorkFlowDefinition() {
		colMap.put(WorkflowDelegate.class + SortConstants.WORKFLOW_TYPE,
				WorkflowDelegate_.workflowType.getName());
		colMap.put(WorkflowDelegate.class + SortConstants.WORKFLOW_FROM_DATE,
				WorkflowDelegate_.startDate.getName());
		colMap.put(WorkflowDelegate.class + SortConstants.WORKFLOW_TO_DATE,
				WorkflowDelegate_.endDate.getName());
		colMap.put(Employee.class + SortConstants.WORKFLOW_USER,
				Employee_.firstName.getName());
		colMap.put(Employee.class + SortConstants.WORKFLOW_DELEGATEE,
				Employee_.firstName.getName());
	}

	/**
	 * Col map email template.
	 */
	private static void colMapEmailTemplate() {
		colMap.put(EmailTemplate.class + SortConstants.EMAIL_TEMPLATE_CATEGORY,
				EmailTemplate_.emailTemplateSubCategoryMaster.getName());
		colMap.put(EmailTemplate.class + SortConstants.EMAIL_TEMPLATE_NAME,
				EmailTemplate_.name.getName());
		colMap.put(EmailTemplate.class + SortConstants.EMAIL_TEMPLATE_SUBJECT,
				EmailTemplate_.subject.getName());
	}

	/**
	 * Col map switch user.
	 */
	private static void colMapSwitchUser() {
		colMap.put(Employee.class + SortConstants.SWITCH_USER,
				Employee_.firstName.getName());
		colMap.put(Employee.class + SortConstants.SWITCH_USER_SWITCHED,
				Employee_.firstName.getName());
		 
		 
		 
		 
		 
		 
	}

	/**
	 * Col map leave type definition
	 */

	private static void colMapLeaveTypeDefinition() {
		colMap.put(LeaveTypeMaster.class + SortConstants.LEAVE_TYPE_NAME,
				LeaveTypeMaster_.leaveTypeName.getName());
		colMap.put(LeaveTypeMaster.class + SortConstants.LEAVE_TYPE_CODE,
				LeaveTypeMaster_.code.getName());
		colMap.put(LeaveTypeMaster.class
				+ SortConstants.LEAVE_TYPE_ACCOUNT_CODE,
				LeaveTypeMaster_.accountCode.getName());
		colMap.put(
				LeaveTypeMaster.class + SortConstants.LEAVE_TYPE_DESCRIPTION,
				LeaveTypeMaster_.leaveTypeDesc.getName());
		colMap.put(LeaveTypeMaster.class + SortConstants.LEAVE_TYPE_ID,
				LeaveTypeMaster_.leaveTypeId.getName());
		colMap.put(LeaveTypeMaster.class
				+ SortConstants.LEAVE_TYPE_VISIBLE_OR_HIDDEN,
				LeaveTypeMaster_.visibility.getName());
	}

	/**
	 * Col map leave batch definition.
	 */
	private static void colMapLeaveBatchDefinition() {
		colMap.put(LeaveBatchMaster.class
				+ SortConstants.LEAVE_BATCH_DESCRIPTION,
				LeaveBatchMaster_.leaveBatchDesc.getName());
		colMap.put(LeaveBatchMaster.class
				+ SortConstants.LEAVE_BATCH_START_DATE,
				LeaveBatchMaster_.startDate.getName());
		colMap.put(LeaveBatchMaster.class + SortConstants.LEAVE_BATCH_END_DATE,
				LeaveBatchMaster_.endDate.getName());
		colMap.put(LeaveBatchMaster.class + SortConstants.LEAVE_BATCH_ID,
				LeaveBatchMaster_.leaveBatchId.getName());

	}

	/**
	 * Col map leave batch definition.
	 */
	private static void colMapDataDictionary() {
		colMap.put(DataDictionary.class + SortConstants.DATA_DICTIONARY_ID,
				DataDictionary_.dataDictionaryId.getName());
		colMap.put(DataDictionary.class + SortConstants.DATA_DICTIONARY_LABEL,
				DataDictionary_.label.getName());
		colMap.put(DataDictionary.class
				+ SortConstants.DATA_DICTIONARY_LABEL_VALUE,
				DataDictionary_.multiLingualData.getName());
	}

	/**
	 * Col map claim batch definition.
	 */
	private static void colMapClaimBatchDefinition() {
		colMap.put(ClaimBatchMaster.class
				+ SortConstants.CLAIM_BATCH_DESCRIPTION,
				ClaimBatchMaster_.claimBatchDesc.getName());
		colMap.put(ClaimBatchMaster.class
				+ SortConstants.CLAIM_BATCH_START_DATE,
				ClaimBatchMaster_.startDate.getName());
		colMap.put(ClaimBatchMaster.class + SortConstants.CLAIM_BATCH_END_DATE,
				ClaimBatchMaster_.endDate.getName());
		colMap.put(ClaimBatchMaster.class + SortConstants.CLAIM_BATCH_ID,
				ClaimBatchMaster_.claimBatchID.getName());

	}

	/**
	 * Col map Excel Import.
	 */

	private static void colMapEmpDataImportTemplate() {
		colMap.put(EmpDataImportTemplate.class
				+ SortConstants.EXCEL_IMPORT_CATEGORY,
				EmpDataImportTemplate_.entityMaster.getName());
		colMap.put(EmpDataImportTemplate.class
				+ SortConstants.EXCEL_IMPORT_TEMPLATE_DESCRIPTION,
				EmpDataImportTemplate_.description.getName());
		colMap.put(EmpDataImportTemplate.class
				+ SortConstants.EXCEL_IMPORT_TEMPLATE_ID,
				EmpDataImportTemplate_.importTemplateId.getName());
		colMap.put(EmpDataImportTemplate.class
				+ SortConstants.EXCEL_IMPORT_TEMPLATE_NAME,
				EmpDataImportTemplate_.templateName.getName());

	}

	/**
	 * Col map Excel Export.
	 */

	private static void colMapEmpDataExportTemplate() {
		colMap.put(EmpDataExportTemplate.class
				+ SortConstants.EXCEL_EXPORT_CATEGORY,
				EmpDataExportTemplate_.entityMaster.getName());
		colMap.put(EmpDataExportTemplate.class
				+ SortConstants.EXCEL_EXPORT_TEMPLATE_DESCRIPTION,
				EmpDataExportTemplate_.description.getName());
		colMap.put(EmpDataExportTemplate.class
				+ SortConstants.EXCEL_EXPORT_TEMPLATE_ID,
				EmpDataExportTemplate_.exportTemplateId.getName());
		colMap.put(EmpDataExportTemplate.class
				+ SortConstants.EXCEL_EXPORT_TEMPLATE_NAME,
				EmpDataExportTemplate_.templateName.getName());

	}

	/**
	 * Col map claim item definition.
	 */
	private static void colMapClaimItemDefinition() {
		colMap.put(ClaimItemMaster.class + SortConstants.CLAIM_ITEM_NAME,
				ClaimItemMaster_.claimItemName.getName());
		colMap.put(ClaimCategoryMaster.class
				+ SortConstants.CLAIM_ITEM_CLAIM_CATEGORY,
				ClaimCategoryMaster_.claimCategoryName.getName());
		colMap.put(ClaimItemMaster.class + SortConstants.CLAIM_ITEM_CODE,
				ClaimItemMaster_.code.getName());
		colMap.put(ClaimItemMaster.class
				+ SortConstants.CLAIM_ITEM_ACCOUNT_CODE,
				ClaimItemMaster_.accountCode.getName());
		colMap.put(ClaimItemMaster.class
				+ SortConstants.CLAIM_ITEM_VISIBLE_OR_HIDDEN,
				ClaimItemMaster_.visibility.getName());
		colMap.put(
				ClaimItemMaster.class + SortConstants.CLAIM_ITEM_DESCRIPTION,
				ClaimItemMaster_.claimItemDesc.getName());

	}

	/**
	 * Col map claim category definition.
	 */
	private static void colMapClaimCategory() {
		colMap.put(ClaimCategoryMaster.class
				+ SortConstants.CLAIM_CATEGORY_NAME,
				ClaimCategoryMaster_.claimCategoryName.getName());
		colMap.put(ClaimCategoryMaster.class
				+ SortConstants.CLAIM_CATEGORY_CODE,
				ClaimCategoryMaster_.code.getName());
		colMap.put(ClaimCategoryMaster.class
				+ SortConstants.CLAIM_CATEGORY_DESCRIPTION,
				ClaimCategoryMaster_.claimCategoryDesc.getName());
	}

	/**
	 * Col map calendar template definition.
	 */
	private static void colMapCalendarTemplate() {
		colMap.put(CompanyCalendarTemplate.class
				+ SortConstants.CALENDAR_TEMPLATE_NAME,
				CompanyCalendarTemplate_.templateName.getName());
		colMap.put(CompanyCalendarTemplate.class
				+ SortConstants.CALENDAR_TEMPLATE_DESCRIPTION,
				CompanyCalendarTemplate_.templateDesc.getName());
		colMap.put(CompanyCalendarTemplate.class + SortConstants.CALENDAR_YEAR,
				CompanyCalendarTemplate_.Start_Year.getName());
		colMap.put(CalendarCodeMaster.class + SortConstants.CALENDAR_CODE,
				CalendarCodeMaster_.code.getName());
		colMap.put(AppCodeMaster.class + SortConstants.CALENDAR_CODE_VALUE,
				AppCodeMaster_.appCodeID.getName());
		colMap.put(CalendarPatternMaster.class
				+ SortConstants.CALENDAR_PATTERN_NAME,
				CalendarPatternMaster_.patternName.getName());
		colMap.put(CalendarPatternMaster.class
				+ SortConstants.CALENDAR_PATTERN_DESC,
				CalendarPatternMaster_.patternDesc.getName());

	}

	/**
	 * Col map claim company Document Detail
	 */
	private static void colMapCompanyDocumentDetail() {
		colMap.put(CompanyDocumentDetail.class
				+ SortConstants.COMPANY_DOCUMENT_DETAIL_DOCUMENT_NAME,
				CompanyDocumentDetail_.fileName.getName());
		colMap.put(CompanyDocumentDetail.class
				+ SortConstants.COMPANY_DOCUMENT_DETAIL_DOCTYPE,
				CompanyDocumentDetail_.fileType.getName());
		colMap.put(CompanyDocument.class
				+ SortConstants.COMPANY_DOCUMENT_DETAIL_YEAR,
				CompanyDocument_.year.getName());
		colMap.put(CompanyDocument.class
				+ SortConstants.COMPANY_DOCUMENT_DETAIL_DESCRIPTION,
				CompanyDocument_.description.getName());
		colMap.put(DocumentCategoryMaster.class
				+ SortConstants.COMPANY_DOCUMENT_DETAIL_CATEGORY,
				DocumentCategoryMaster_.categoryName.getName());

	}

	/**
	 * Col map company
	 */
	private static void colMapCompany() {
		colMap.put(Company.class
				+ SortConstants.COMPANY_INFORMTAION_COMPANY_NAME,
				Company_.companyName.getName());
		colMap.put(Company.class
				+ SortConstants.COMPANY_INFORMTAION_COMPANY_CODE,
				Company_.companyCode.getName());
		colMap.put(
				Company.class + SortConstants.COMPANY_INFORMTAION_COMPANY_ID,
				Company_.companyId.getName());
		colMap.put(CompanyGroup.class
				+ SortConstants.COMPANY_INFORMTAION_GROUP_CODE,
				CompanyGroup_.groupCode.getName());
		colMap.put(CompanyGroup.class
				+ SortConstants.COMPANY_INFORMTAION_GROUP_NAME,
				CompanyGroup_.groupName.getName());
		colMap.put(CountryMaster.class
				+ SortConstants.COMPANY_INFORMTAION_COUNTRY,
				CountryMaster_.countryName.getName());
		colMap.put(PayslipFrequency.class
				+ SortConstants.COMPANY_INFORMTAION_PAYSLIP_FREQUENCY,
				PayslipFrequency_.frequency.getName());

	}

	/**
	 * Col map Employee Document Center
	 */
	private static void colMapEmployeeDocumentCenter() {
		colMap.put(EmployeeDocument.class
				+ SortConstants.EMPLOYEE_DOCUMENT_CENTER_DOCNAME,
				EmployeeDocument_.fileName.getName());
		colMap.put(EmployeeDocument.class
				+ SortConstants.EMPLOYEE_DOCUMENT_CENTER_DESCRIPTION,
				EmployeeDocument_.description.getName());
		colMap.put(EmployeeDocument.class
				+ SortConstants.EMPLOYEE_DOCUMENT_CENTER_DOCTYPE,
				EmployeeDocument_.fileType.getName());
		colMap.put(EmployeeDocument.class
				+ SortConstants.EMPLOYEE_DOCUMENT_CENTER_YEAR,
				EmployeeDocument_.year.getName());

	}

	private static void colMapLeaveScheme() {
		colMap.put(LeaveScheme.class + SortConstants.LEAVE_SCHEME_NAME,
				LeaveScheme_.schemeName.getName());
		colMap.put(LeaveScheme.class + SortConstants.LEAVE_SCHEME_VISIBILITY,
				LeaveScheme_.visibility.getName());

	}

	private static void colMapClaimTemplate() {
		colMap.put(ClaimTemplate.class + SortConstants.CLAIM_TEMPLATE_NAME,
				ClaimTemplate_.templateName.getName());
		colMap.put(ClaimTemplate.class
				+ SortConstants.CLAIM_TEMPLATE_VISIBILITY,
				ClaimTemplate_.visibility.getName());

	}

	private static void colMapOTTemplate() {
		colMap.put(OTTemplate.class + SortConstants.OT_TEMPLATE_NAME,
				OTTemplate_.templateName.getName());
		colMap.put(OTTemplate.class + SortConstants.OT_TEMPLATE_VISIBILITY,
				OTTemplate_.visibility.getName());

	}

	private static void colMapAssignCompany() {
		colMap.put(Company.class + SortConstants.MANAGE_ROLES_COMPANY_NAME,
				Company_.companyName.getName());
		colMap.put(Company.class + SortConstants.MANAGE_ROLES_COMPANY_CODE,
				Company_.companyCode.getName());
		colMap.put(CompanyGroup.class + SortConstants.MANAGE_ROLES_GROUP_CODE,
				CompanyGroup_.groupCode.getName());
		colMap.put(CompanyGroup.class + SortConstants.MANAGE_ROLES_GROUP_NAME,
				CompanyGroup_.groupName.getName());

	}

	private static void colMapLeaveReviewer() {
		colMap.put(LeaveReviewerDetailView.class
				+ SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME,
				LeaveReviewerDetailView_.empFirstName.getName());
		colMap.put(LeaveReviewerDetailView.class
				+ SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER1,
				LeaveReviewerDetailView_.reviewer1FirstName.getName());
		colMap.put(LeaveReviewerDetailView.class
				+ SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER2,
				LeaveReviewerDetailView_.reviewer2FirstName.getName());
		colMap.put(LeaveReviewerDetailView.class
				+ SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER3,
				LeaveReviewerDetailView_.reviewer3FirstName.getName());
		colMap.put(LeaveReviewerDetailView.class
				+ SortConstants.LEAVE_REVIEWER_LEAVE_SCHEME,
				LeaveReviewerDetailView_.leaveSchemeName.getName());
		colMap.put(LeaveReviewerDetailView.class
				+ SortConstants.LEAVE_REVIEWER_LEAVE_TYPE,
				LeaveReviewerDetailView_.leaveTypeName.getName());

	}

	private static void colMapClaimReviewer() {
		colMap.put(ClaimReviewerDetailView.class
				+ SortConstants.CLAIM_REVIEWER_EMPLOYEE_NAME,
				ClaimReviewerDetailView_.empFirstName.getName());
		colMap.put(ClaimReviewerDetailView.class
				+ SortConstants.CLAIM_REVIEWER_LEAVE_REVIEWER1,
				ClaimReviewerDetailView_.reviewer1FirstName.getName());
		colMap.put(ClaimReviewerDetailView.class
				+ SortConstants.CLAIM_REVIEWER_LEAVE_REVIEWER2,
				ClaimReviewerDetailView_.reviewer2FirstName.getName());
		colMap.put(ClaimReviewerDetailView.class
				+ SortConstants.CLAIM_REVIEWER_LEAVE_REVIEWER3,
				ClaimReviewerDetailView_.reviewer3FirstName.getName());
		colMap.put(ClaimReviewerDetailView.class
				+ SortConstants.CLAIM_REVIEWER_LEAVE_SCHEME,
				ClaimReviewerDetailView_.claimTemplateName.getName());

	}

	private static void colMapAssignLeaveScheme() {
		colMap.put(Employee.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_EMPLOYEE_NAME,
				Employee_.firstName.getName());
		colMap.put(Employee.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_EMPLOYEE_NUMBER,
				Employee_.employeeNumber.getName());
		colMap.put(EmployeeLeaveScheme.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_FROM_DATE,
				EmployeeLeaveScheme_.startDate.getName());
		colMap.put(EmployeeLeaveScheme.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_TO_DATE,
				EmployeeLeaveScheme_.endDate.getName());
		colMap.put(LeaveScheme.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_SCHEME,
				LeaveScheme_.schemeName.getName());
	}

	private static void colMapEmployeeLeaveDistribution() {
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_YEAR,
				EmployeeLeaveDistribution_.year.getName());
		colMap.put(LeaveTypeMaster.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_TYPE,
				LeaveTypeMaster_.leaveTypeName.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_JAN,
				EmployeeLeaveDistribution_.jan.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_FEB,
				EmployeeLeaveDistribution_.feb.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_MAR,
				EmployeeLeaveDistribution_.mar.getName());

		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_APR,
				EmployeeLeaveDistribution_.apr.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_MAY,
				EmployeeLeaveDistribution_.may.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_JUNE,
				EmployeeLeaveDistribution_.jun.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_JULY,
				EmployeeLeaveDistribution_.jul.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_AUG,
				EmployeeLeaveDistribution_.aug.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_SEPT,
				EmployeeLeaveDistribution_.sep.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_OCT,
				EmployeeLeaveDistribution_.oct.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_NOV,
				EmployeeLeaveDistribution_.nov.getName());
		colMap.put(EmployeeLeaveDistribution.class
				+ SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_DISTRIBUTION_DEC,
				EmployeeLeaveDistribution_.dec.getName());
	}

	private static void colMapHolidayCalendar() {
		colMap.put(CompanyHolidayCalendar.class
				+ SortConstants.HOLIDAY_CALENDAR_NAME,
				CompanyHolidayCalendar_.calendarName.getName());
		colMap.put(CompanyHolidayCalendar.class
				+ SortConstants.EMP_HOLIDAY_CALENDAR_NAME,
				CompanyHolidayCalendar_.calendarName.getName());
		colMap.put(CompanyHolidayCalendar.class
				+ SortConstants.HOLIDAY_CALENDAR_DESC,
				CompanyHolidayCalendar_.calendarDesc.getName());

		colMap.put(CountryMaster.class
				+ SortConstants.HOLIDAY_CALENDAR_COUNTRY_NAME,
				CountryMaster_.countryName.getName());
		colMap.put(StateMaster.class
				+ SortConstants.HOLIDAY_CALENDAR_STATE_NAME,
				StateMaster_.stateName.getName());
		colMap.put(CompanyHolidayCalendarDetail.class
				+ SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DATE,
				CompanyHolidayCalendarDetail_.holidayDate.getName());
		colMap.put(CompanyHolidayCalendarDetail.class
				+ SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DESC,
				CompanyHolidayCalendarDetail_.holidayDesc.getName());
	}

	private static void colMapPayslipRelease() {
		colMap.put(CompanyPayslipRelease.class
				+ SortConstants.PAYSLIP_RELEASE_NAME,
				CompanyPayslipRelease_.name.getName());
		colMap.put(CompanyPayslipRelease.class
				+ SortConstants.PAYSLIP_RELEASE_PART,
				CompanyPayslipRelease_.part.getName());
		colMap.put(CompanyPayslipRelease.class
				+ SortConstants.PAYSLIP_RELEASE_RELEASE,
				CompanyPayslipRelease_.released.getName());
		colMap.put(CompanyPayslipRelease.class
				+ SortConstants.PAYSLIP_RELEASE_YEAR,
				CompanyPayslipRelease_.year.getName());
		colMap.put(MonthMaster.class + SortConstants.PAYSLIP_RELEASE_MONTH,
				MonthMaster_.monthId.getName());
	}

	private static void colMapHrisReviewer() {
		colMap.put(HRISReviewerDetailView.class
				+ SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME,
				HRISReviewerDetailView_.empFirstName.getName());
		colMap.put(HRISReviewerDetailView.class + SortConstants.HRIS_REVIEWER1,
				HRISReviewerDetailView_.reviewer1FirstName.getName());
		colMap.put(HRISReviewerDetailView.class + SortConstants.HRIS_REVIEWER2,
				HRISReviewerDetailView_.reviewer2FirstName.getName());
		colMap.put(HRISReviewerDetailView.class + SortConstants.HRIS_REVIEWER3,
				HRISReviewerDetailView_.reviewer3FirstName.getName());
		colMap.put(
				HRISReviewerDetailView.class + SortConstants.EMPLOYEE_NUMBER,
				HRISReviewerDetailView_.empEmployeeNumber.getName());

	}

	private static void colMapLundinOTReviewer() {
		colMap.put(LundinOTReviewerDetailView.class
				+ SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME,
				LundinOTReviewerDetailView_.empFirstName.getName());
		colMap.put(LundinOTReviewerDetailView.class
				+ SortConstants.LUNDIN_REVIEWER1,
				LundinOTReviewerDetailView_.reviewer1FirstName.getName());
		colMap.put(LundinOTReviewerDetailView.class
				+ SortConstants.LUNDIN_REVIEWER2,
				LundinOTReviewerDetailView_.reviewer2FirstName.getName());
		colMap.put(LundinOTReviewerDetailView.class
				+ SortConstants.LUNDIN_REVIEWER3,
				LundinOTReviewerDetailView_.reviewer3FirstName.getName());
		colMap.put(LundinOTReviewerDetailView.class
				+ SortConstants.EMPLOYEE_NUMBER,
				LundinOTReviewerDetailView_.empEmployeeNumber.getName());
	}

	private static void colMapHrisPendingItems() {
		colMap.put(
				Employee.class + SortConstants.HRIS_CHANGE_REQUEST_CREATEDBY,
				Employee_.firstName.getName());
		colMap.put(DataDictionary.class
				+ SortConstants.HRIS_CHANGE_REQUEST_FIELD,
				DataDictionary_.label.getName());
		colMap.put(HRISChangeRequest.class
				+ SortConstants.HRIS_CHANGE_REQUEST_NEW_VALUE,
				HRISChangeRequest_.newValue.getName());
		colMap.put(HRISChangeRequest.class
				+ SortConstants.HRIS_CHANGE_REQUEST_OLD_VALUE,
				HRISChangeRequest_.oldValue.getName());
		colMap.put(HRISChangeRequest.class
				+ SortConstants.HRIS_CHANGE_REQUEST_CREATED_DATE,
				HRISChangeRequest_.createdDate.getName());
	}

	private static void colMapDiscussionBoard() {
		colMap.put(ForumTopic.class + SortConstants.FORUM_TOPIC_NAME,
				ForumTopic_.topicName.getName());
		colMap.put(Employee.class + SortConstants.FORUM_TOPIC_AUTHOR,
				Employee_.firstName.getName());
		colMap.put(ForumTopic.class + SortConstants.FORUM_TOPIC_CREATION_DATE,
				ForumTopic_.createdDate.getName());
		colMap.put(ForumTopic.class + SortConstants.FORUM_TOPIC_STATUS,
				ForumTopic_.status.getName());
		/*
		 * colMap.put(HRISChangeRequest.class +
		 * SortConstants.HRIS_CHANGE_REQUEST_OLD_VALUE,
		 * HRISChangeRequest_.oldValue.getName());
		 * colMap.put(HRISChangeRequest.class +
		 * SortConstants.HRIS_CHANGE_REQUEST_CREATED_DATE,
		 * HRISChangeRequest_.createdDate.getName());
		 */
	}
	
	private static void colMapWorkdayFTPImportHistory() {
		colMap.put(WorkdayFtpImportHistory.class
				+ SortConstants.FTP_IMPORT_HISTORY_CREATED_DATE,
				WorkdayFtpImportHistory_.createdDate.getName());

		colMap.put(WorkdayFtpImportHistory.class
				+ SortConstants.FTP_IMPORT_HISTORY_FILE_NAME,
				WorkdayFtpImportHistory_.importFileName.getName());

		colMap.put(WorkdayFtpImportHistory.class
				+ SortConstants.FTP_IMPORT_HISTORY_STATUS,
				WorkdayFtpImportHistory_.importStatus.getName());

		colMap.put(WorkdayFtpImportHistory.class
				+ SortConstants.FTP_IMPORT_HISTORY_TO_NO_RECORDS,
				WorkdayFtpImportHistory_.totalEmpRecords.getName());

	}
	
	private static void colMapMyClaim() {
		colMap.put(ClaimApplication.class + "claimNumber",
				ClaimApplication_.claimNumber.getName());
		
		colMap.put(ClaimApplication.class + "createDate",
				ClaimApplication_.createdDate.getName());
		
		colMap.put(ClaimApplication.class + "totalItems",
				ClaimApplication_.totalItems.getName());

		colMap.put(ClaimTemplate.class + "claimTemplateName",
				ClaimTemplate_.templateName.getName());

		colMap.put(ClaimStatusMaster.class + "status",
				ClaimStatusMaster_.claimStatusName.getName());
		
		colMap.put(ClaimApplication.class + "createdBy",
				ClaimApplication_.createdBy.getName());
		
		colMap.put(ClaimApplicationItem.class + "receiptNumber",
				ClaimApplicationItem_.receiptNumber.getName());
		colMap.put(ClaimApplicationItem.class + "claimAmount",
				ClaimApplicationItem_.claimAmount.getName());
		colMap.put(ClaimApplicationItem.class + "claimDate",
				ClaimApplicationItem_.claimDate.getName());
		
	}
}
