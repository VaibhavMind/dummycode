package com.payasia.web.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;

import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmployeeListForm;

/**
 * The Interface EmployeeDetailController.
 * 
 * @author ragulapraveen
 */
/**
 * @author ragulapraveen
 * 
 */
public interface EmployeeDetailController {

	/**
	 * Purpose : Gets the meta data of employee from dynamic form .
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the xML data
	 */
	String getXMLData(HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale);

	/**
	 * Purpose : Edits the selected employee grid view.
	 * 
	 * @param viewId
	 *            the view id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String editViewGrid(Long viewId, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Delete employee grid view .
	 * 
	 * @param viewId
	 *            the view id
	 * @return the string
	 */
	String deleteView(Long viewId);

	/**
	 * Purpose : Creates employee grid view .
	 * 
	 * @param model
	 *            the model
	 * @param viewName
	 *            the view name
	 * @param recordsPerPage
	 *            the records per page
	 * @param dataDictionaryIdArr
	 *            the data dictionary id arr
	 * @param rowIndexsArr
	 *            the row indexs arr
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String saveCustomView(ModelMap model, String viewName, int recordsPerPage, String[] dataDictionaryIdArr,
			String[] rowIndexsArr, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Gets employee grid view list
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the view list
	 */
	String getViewList(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Gets Employee grid views data dictionary names of employee.
	 * 
	 * @param viewID
	 *            the view id
	 * @return the custom column name
	 */
	String getCustomColumnName(Long viewID);

	/**
	 * Purpose : Uploading employee image .
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param empID
	 *            the emp id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String uploadEmployeeImage(EmployeeListForm employeeListForm, Long empID, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Purpose : Gets the employee image size.
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the employee image size
	 */
	String getEmployeeImageSize(EmployeeListForm employeeListForm, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Purpose : Gets the password policy.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the password policy
	 */
	String getPasswordPolicy(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Deleting employee if employee doesn't have any other roles
	 * other than default role.
	 * 
	 * @param empID
	 *            the emp id
	 * @return the string
	 */
	String deleteEmployee(Long empID, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Deleting employee default role.
	 * 
	 * @param empID
	 *            the emp id
	 * @return the string
	 */
	String deleteRoleAndEmployee(Long empID);

	/**
	 * Purpose : Updating employee grid view .
	 * 
	 * @param model
	 *            the model
	 * @param viewName
	 *            the view name
	 * @param recordsPerPage
	 *            the records per page
	 * @param dataDictionaryIdArr
	 *            the data dictionary id arr
	 * @param rowIndexsArr
	 *            the row indexs arr
	 * @param viewId
	 *            the view id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String updateCustomView(ModelMap model, String viewName, int recordsPerPage, String[] dataDictionaryIdArr,
			String[] rowIndexsArr, Long viewId, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Adds the employee static data.
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String saveEmployeeStaticData(EmployeeListForm employeeListForm, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	/**
	 * Purpose : Save employee static and dynamic data.
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param xml
	 *            the xml
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @param version
	 *            the version
	 * @param tabNumber
	 *            the tab number
	 * @param employeeID
	 *            the employee id
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String saveEmployeeStaticAndDynamicData(EmployeeListForm employeeListForm, String xml, Long companyId,
			Long entityId, Long formId, Integer version, Integer tabNumber, String employeeID, Locale locale);

	/**
	 * Purpose : Gets the emp password and send mail.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param password
	 *            the password
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return the emp password and send mail
	 */
	String getEmpPasswordAndSendMail(long employeeId, String password, HttpServletResponse response,
			HttpServletRequest request, Locale locale);

	/**
	 * Purpose : Search employee pwd.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @return the string
	 */
	String searchEmployeePwd(String columnName, String sortingType, String fromDate, String toDate, int page, int rows,
			HttpServletRequest request, HttpServletResponse response, HttpSession session);

	/**
	 * Purpose : Updates employee static and dynamic data.
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param xml
	 *            the xml
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @param version
	 *            the version
	 * @param tabNumber
	 *            the tab number
	 * @param employeeID
	 *            the employee id
	 * @param tabID
	 *            the tab id
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String updateEmployeeStaticAndDynamicData(EmployeeListForm employeeListForm, String xml, Long companyId,
			Long entityId, Long formId, Integer version, Integer tabNumber, Long employeeID, Long tabID, Locale locale);

	/**
	 * Purpsoe : Update employees static data.
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param employeeID
	 *            the employee id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String updateEmployeeStaticData(EmployeeListForm employeeListForm, Long employeeID, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	/**
	 * Purpose : Filter employee list.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String filterEmployeeList(String columnName, String sortingType, String searchCondition, String searchText,
			int page, int rows, HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Locale locale);

	/**
	 * Purpose : Gets the reset password.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the reset password
	 */
	String getResetPassword(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Gets the employee image.
	 * 
	 * @param empID
	 *            the emp id
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @return the employee image
	 * @throws IOException
	 */
	byte[] getEmployeeImage(long empID, HttpServletResponse response, HttpServletRequest request) throws IOException;

	/**
	 * Gets the employee xml.
	 * 
	 * @param empID
	 *            the emp id
	 * @param formId
	 *            the form id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the employee xml
	 */
	String getEmployeeXML(Long empID, Long formId, HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String editEmployee(EmployeeListForm employeeListForm, HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale, Long empID, String mode);

	String updateTableRecord(String tableXML, Long tabId, Integer seqNo, HttpServletRequest request,
			HttpServletResponse response);

	String saveTableRecord(String tableXML, Long tabId, Integer seqNo, Long formId, Integer version, Long entityKey,
			HttpServletRequest request, HttpServletResponse response);

	void getHomePageEmployeeImage(HttpServletResponse response, HttpServletRequest request);

	String editHomePageEmployee(EmployeeListForm employeeListForm, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale, String mode);

	String deleteEmployeeImage(Long empID, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Gets the employee name.
	 * 
	 * @param empID
	 *            the emp id
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @return the employee name
	 */
	String getEmployeeName(HttpServletResponse response, HttpServletRequest request);

	String updateEmpStaticAndDynamicViewProfileData(EmployeeListForm employeeListForm, String xml, Long companyId,
			Long entityId, Long formId, Integer version, Integer tabNumber, Long employeeID, Long tabID, Locale locale);

	String saveTableDocRecord(DynamicFormTableDocumentDTO dynamicFormDocumentDTO, HttpServletRequest request,
			HttpServletResponse response);

	byte[] downloadEmpDoc(Long recordId, int seqNo, Long entityKey, HttpServletRequest request,
			HttpServletResponse response);

	String updateDocTableRecord(DynamicFormTableDocumentDTO dynamicFormDocumentDTO, HttpServletRequest request,
			HttpServletResponse response);

	String empTakeOwnership(Long tableId, Integer seqNo, String ownership, HttpServletRequest request,
			HttpServletResponse response);

	String saveAddEmployee(EmployeeListForm employeeListForm, String xml, Long companyId, Long entityId, Long formId,
			Integer version, Integer tabNumber, String employeeID, Locale locale);

	String isEnableEmployeeChangeWorkflow(HttpServletRequest request, HttpServletResponse response);

	String isAllowEmployeetouploaddocument(HttpServletRequest request, HttpServletResponse response);

	String empGetPassword(Long empID, HttpServletRequest request, HttpServletResponse response);

	String isHideGetPassword(HttpServletRequest request, HttpServletResponse response);

	String getClientAdminEditDeleteEmpStatus(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose : Edits employee Grid View information
	 * 
	 * @param viewId
	 *            the view id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String editView(Long viewId, HttpServletRequest request, HttpServletResponse response, Locale locale);

	String editEmployeeViewProfile(EmployeeListForm employeeListForm, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale, String mode);

	String getViewProfileEmployeeXML(Long formId, HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String showHistory(Long tableId, Integer seqNo, String tableType, HttpServletRequest request,
			HttpServletResponse response);

	byte[] downloadViewProfileEmpDoc(Long recordId, int seqNo, HttpServletRequest request,
			HttpServletResponse response);

	String updateEmpDocTableRecord(DynamicFormTableDocumentDTO dynamicFormDocumentDTO, HttpServletRequest request,
			HttpServletResponse response);

	String saveEmpDocTableRecord(DynamicFormTableDocumentDTO dynamicFormDocumentDTO, HttpServletRequest request,
			HttpServletResponse response);

	byte[] employeeViewProfileImage(HttpServletResponse response, HttpServletRequest request) throws IOException;

	String loadgrid(HttpServletRequest request, HttpServletResponse response, Long tid, int columnCount,
			String[] fieldNames, String[] fieldTypes, String[] fieldDictIds, String tableType, Long empID,
			String sortOrder, String sortBy, Locale locale);

	String updateViewProfileTableRecord(String tableXML, Long tabId, Integer seqNo, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String saveViewProfileTableRecord(String tableXML, Long tabId, Integer seqNo, Long formId, Integer version,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	String getEmpChangeRequestData(Long HRISChangeRequestId, HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String getViewProfileTableEmpChangeRequestData(Long tableFieldDictionaryId, int seqNum, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String getTableEmpChangeRequestData(Long tableFieldDictionaryId, int seqNum, Long empID, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String empLoadGrid(HttpServletRequest request, HttpServletResponse response, Long tid, Long formId, int columnCount,
			String[] fieldNames, String[] fieldTypes, String[] fieldDictIds, String tableType, String sortOrder,
			String sortBy, Locale locale);

	String deleteTableRecord(Long tableId, Integer seqNo, String tableType, Long entityKey, String[] fieldDictIds,
			String fieldLabel, HttpServletRequest request, HttpServletResponse response);

	String deleteEmpTableRecord(Long tableId, Integer seqNo, String tableType, String[] fieldDictIds, String fieldLabel,
			String formId, HttpServletRequest request, HttpServletResponse response);
	String showHistory(Long tableId, Integer seqNo, String tableType,String formId,
			HttpServletRequest request, HttpServletResponse response);

	byte[] getAdminEmployeeImage(long empID, HttpServletResponse response, HttpServletRequest request)
			throws IOException;
}
