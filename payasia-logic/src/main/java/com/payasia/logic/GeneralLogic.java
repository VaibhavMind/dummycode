package com.payasia.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mind.payasia.xml.bean.Field;
import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.CustomFieldReportDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DeviceDTO;
import com.payasia.common.dto.EmployeeDTO;
import com.payasia.common.dto.EmployeeFieldDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.NotificationForm;
import com.payasia.dao.bean.CompanyEmployeeShortList;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.LeaveApplication;

/**
 * The Interface GeneralLogic.
 */
/**
 * @author vivekjain
 *
 */
public interface GeneralLogic {

	/**
	 * Gets the month list.
	 * 
	 * @return the month list
	 */
	List<MonthMasterDTO> getMonthList();

	/**
	 * Gets the short list employee ids.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 * @return the short list employee ids
	 */
	EmployeeShortListDTO getShortListEmployeeIds(Long employeeId, Long companyId);

	/**
	 * Generate encrypted passwords.
	 */
	void generateEncryptedPasswords();

	/**
	 * Gets the type of application.
	 * 
	 * @param leaveApplication
	 *            the leave application
	 * @return the type of application
	 */
	String getTypeOfApplication(LeaveApplication leaveApplication);

	EmployeeShortListDTO getShortListEmployeeIdsForReports(Long companyId,
			String metaData);

	HashMap<String, String> getEmployeeFilterComboList(Long companyId);

	EmployeeShortListDTO getShortListEmployeeIdsForAdvanceFilter(
			Long companyId, String metaData);

	EmployeeShortListDTO getShortListEmployeeNumbers(Long employeeId,
			Long companyId);

	void saveDeviceDetails(DeviceDTO deviceDTO, EmployeeDTO employeeDTO);

	Field getExistDynamicFormFieldInfo(String XML, Long dataDictionaryId);

	Boolean checkIsLeaveManager(Long employeeId);

	/**
	 * Purpose : Get Admin Authorized Section List.
	 * 
	 * @param EmployeeId
	 *            the employee Id
	 * @param CompanyId
	 *            the company id
	 * @param EntityId
	 *            the entityId
	 * @return the List<Long> List of Section Ids
	 */
	List<Long> getAdminAuthorizedSectionIdList(Long employeeId, Long companyId,
			Long entityId);

	/**
	 * Purpose : Get Employee Authorized Section List.
	 * 
	 * @param EmployeeId
	 *            the employee Id
	 * @param CompanyId
	 *            the company id
	 * @param EntityId
	 *            the entityId
	 * @return the List<Long> List of Section Ids
	 */
	List<Long> getEmployeeAuthorizedSectionIdList(Long employeeId,
			Long companyId, Long entityId);

	Boolean checkIsClaimManager(Long employeeId);

	NotificationForm getPendingNotifications(String mobileSerialId);

	EmployeeShortListDTO getExcelExportShortListEmployeeIds(Long templateId,
			Long companyId);

	List<EmployeeFieldDTO> returnEmployeesList(Long employeeId, Long companyId);

	String getValidEmployeeNumberFromFileName(String fileName,
			List<CompanyDocumentLogDTO> documentLogs, Long companyId,
			boolean isFileNameContainsCompNameForPdf,
			List<String> invalidZipFileNames);

	boolean isValidFileType(String fileExt);

	EmployeeListFormPage getEmployeeHRISChangeRequestData(
			Long hrisChangeRequestId, Long companyId, String metadata,
			Long languageId);

	/**
	 * Purpose : Get Employee Authorized Privilege Name:Employee Claim Summary.
	 * 
	 * @param EmployeeId
	 *            the employee Id
	 * @return Boolean
	 */
	Boolean isEmployeeClaimSummaryEnabled(Long employeeId);

	/**
	 * Purpose : Get Employee Dynamic Form Field value based on dataDictionary
	 * 
	 * @param DataDictionary
	 *            the Data Dictionary
	 * @param EmployeeId
	 *            the employee Id
	 * @param companyId
	 *            the company Id
	 * @return fieldValue
	 */
	String getEmpoyeeDynamicFormFieldValue(DataDictionary dataDictionary,
			Long employeeId, Long companyId);

	/**
	 * Gets the short list employee ids By DataDiction and dynamicFieldValue.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 * @return the short list employee ids
	 */
	EmployeeShortListDTO getShortListEmployeeIdsByCondition(
			CompanyEmployeeShortList companyEmployeeShortList, Long companyId);

	CustomFieldReportDTO getCustomFieldDataForLeaveReport(
			List<Long> dataDictionaryIdsList, List<Long> employeeIdsList,
			Long companyId, boolean showOnlyEmployeeDynFieldCode);

	CustomFieldReportDTO getCustomFieldDataForCompanyEntity(
			List<Long> dataDictionaryIdsList, Long companyId,
			boolean showOnlyCompanyDynFieldCode);

	String getEmployeeNameWithNumber(Employee employee);

	/**
	 * Update Leave Entitlement for 1. Child Care Leave type , 2. Extended Child
	 * Care Leave type i.e. when any dependents details will inserted.
	 * 
	 * @param companyId
	 * @param employeeId
	 * @return
	 */
	String getChildCareLeaveTypeInfo(Long companyId, Long employeeId);

	void setStaticDictionary(Map<String, DataImportKeyValueDTO> colMap,
			String colKey, DataDictionary dataDictionary);

	void getColMap(DataDictionary dataDictionary,
			Map<String, DataImportKeyValueDTO> colMap);

}
