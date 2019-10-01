/**
 * @author Ragula Praveen
 *
 */
package com.payasia.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmployeeForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.EntityListViewFieldForm;
import com.payasia.common.form.EntityListViewForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;

import net.sf.json.JSONObject;

/**
 * The Interface EmployeeDetailLogic.
 * 
 * @author ragulapraveen
 */
/**
 * @author ragulapraveen
 * 
 */
@Transactional
public interface EmployeeDetailLogic {

	/**
	 * Purpose : Gets all the employees based on company id and search
	 * condition.
	 * 
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyIds
	 *            the company ids
	 * @param employeeId
	 *            the employee id
	 * @param languageId
	 *            the language id
	 * @return the employee list
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	EmployeeListFormPage getEmployeeList(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyIds, Long employeeId, Long languageId)
			throws UnsupportedEncodingException;

	/**
	 * Purpose : Filtering employees based on filter condition.
	 */
	void filterEmployeeList();

	/**
	 * Purpose : Adding employee static information.
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param CompanyId
	 *            the company id
	 * @return the long
	 */
	Long addEmployee(EmployeeListForm employeeListForm, Long CompanyId);

	/**
	 * Update employee dynamic form record.
	 * 
	 * @param xml
	 *            the xml
	 * @param companyID
	 *            the company id
	 * @param entityID
	 *            the entity id
	 * @param formID
	 *            the form id
	 * @param version
	 *            the version
	 * @param empid
	 *            the empid
	 * @param tabID
	 *            the tab id
	 */
	void updateEmployeeDynamicFormRecord(String xml, Long companyID,
			Long entityID, Long formID, Integer version, Long empid, Long tabID);

	/**
	 * Purpose : Gets employee dynamic form information.
	 * 
	 * @param companyId
	 *            the company id
	 * @param languageId
	 *            the language id
	 * @return the uploaded doc
	 */
	EmployeeListFormPage getUploadedDoc(Long companyId, Long languageId);

	/**
	 * Purpose : save employee static and dynamic data.
	 * 
	 * @param xml
	 *            the xml
	 * @param cmpID
	 *            the cmp id
	 * @param entityID
	 *            the entity id
	 * @param formID
	 *            the form id
	 * @param version
	 *            the version
	 * @param empid
	 *            the empid
	 * @return the long
	 */
	EmployeeListFormPage saveEmployee(String xml, Long cmpID, Long entityID,
			Long formID, Integer version, Long empid);

	/**
	 * Purpose : Edits employee grid view .
	 * 
	 * @param companyId
	 *            the company id
	 * @param viewId
	 *            the view id
	 * @return the list
	 */
	List<EntityListViewFieldForm> editView(Long companyId, Long viewId);

	/**
	 * Purpose : Save employee grid view .
	 * 
	 * @param companyId
	 *            the company id
	 * @param viewName
	 *            the view name
	 * @param recordsPerPage
	 *            the records per page
	 * @param dataDictionaryIdArr
	 *            the data dictionary id arr
	 * @param rowIndexs
	 *            the row indexs
	 */
	void saveCustomView(Long companyId, String viewName, int recordsPerPage,
			String[] dataDictionaryIdArr, String[] rowIndexs);

	/**
	 * Purpose : Gets employee grid view names .
	 * 
	 * @param companyId
	 *            the company id
	 * @return the view name
	 */
	List<EntityListViewForm> getViewName(Long companyId);

	/**
	 * Purpose : Delete employee grid view .
	 * 
	 * @param viewId
	 *            the view id
	 */
	void deleteView(Long viewId);

	/**
	 * Purpose : Gets the custom column name.
	 * 
	 * @param viewID
	 *            the view id
	 * @return the custom column name
	 */
	List<EntityListViewFieldForm> getCustomColumnName(Long viewID);

	/**
	 * Purpose : List edit view.
	 * 
	 * @param companyId
	 *            the company id
	 * @param viewId
	 *            the view id
	 * @return the list
	 */
	List<EntityListViewFieldForm> listEditView(Long companyId, Long viewId);

	/**
	 * Purpose : Updating employee image.
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param companyId
	 *            the company id
	 * @param empID
	 *            the emp id
	 */
	void updateEmployeeImage(EmployeeListForm employeeListForm, Long companyId,
			Long empID);

	/**
	 * Purpose : Gets the employee name.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return the employee name
	 */
	String getEmployeeName(Long employeeId);

	/**
	 * Purpose : Gets the employee image size.
	 * 
	 * @return the employee image size
	 */
	int getEmployeeImageSize();

	/**
	 * Purpose : Gets the emp password and send mail.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param password
	 *            the password
	 * @return the emp password and send mail
	 */
	String getEmpPasswordAndSendMail(long employeeId, String password);

	/**
	 * Purpose : Gets the password policy.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the password policy
	 */
	PasswordPolicyPreferenceForm getPasswordPolicy(Long companyId);

	/**
	 * Purpose : Deleting employee ,if employee hasn't assigned any role .
	 * 
	 * @param empID
	 *            the emp id
	 * @return true, if successful
	 */
	boolean deleteEmployee(Long empID);

	/**
	 * Purpose : Delete role and employee.
	 * 
	 * @param empID
	 *            the emp id
	 */
	void deleteRoleAndEmployee(Long empID);

	/**
	 * Purpose : Checking employee grid view name .
	 * 
	 * @param viewName
	 *            the view name
	 * @param companyId
	 *            the company id
	 * @return the employee list form page
	 */
	EmployeeListFormPage checkView(String viewName, Long companyId);

	/**
	 * Purpose : Checking employee grid view name while updating employee grid
	 * view .
	 * 
	 * @param viewName
	 *            the view name
	 * @param companyId
	 *            the company id
	 * @param viewId
	 *            the view id
	 * @return the employee list form page
	 */
	EmployeeListFormPage checkViewUpdate(String viewName, Long companyId,
			Long viewId);

	/**
	 * Purpose : updating employee grid view .
	 * 
	 * @param companyId
	 *            the company id
	 * @param viewName
	 *            the view name
	 * @param recordsPerPage
	 *            the records per page
	 * @param dataDictionaryIdArr
	 *            the data dictionary id arr
	 * @param rowIndexs
	 *            the row indexs
	 * @param viewId
	 *            the view id
	 */
	void updateCustomView(Long companyId, String viewName, int recordsPerPage,
			String[] dataDictionaryIdArr, String[] rowIndexs, Long viewId);

	/**
	 * Purpose : Check employee number email.
	 * 
	 * @param employeeNumber
	 *            the employee number
	 * @param companyId
	 *            the company id
	 * @param emailId
	 *            the email id
	 * @return the employee list form page
	 */
	EmployeeListFormPage checkEmployeeNumberEmail(String employeeNumber,
			Long companyId, String emailId);

	/**
	 * Purpose : Check employee email.
	 * 
	 * @param email
	 *            the email
	 * @param employeeID
	 *            the employee id
	 * @return the employee list form page
	 */
	EmployeeListFormPage checkEmployeeEmail(String email, Long employeeID);

	/**
	 * Purpose : Gets the employee list pwd.
	 * 
	 * @param companyId
	 *            the company id
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the employee list pwd
	 */
	EmployeeListFormPage getEmployeeListPwd(Long companyId, String fromDate,
			String toDate, PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Purpose : Update employee static information.
	 * 
	 * @param employeeListForm
	 *            the employee list form
	 * @param empId
	 *            the emp id
	 * @param companyId
	 *            the company id
	 */
	void updateEmployee(EmployeeListForm employeeListForm, Long empId,
			Long companyId);

	/**
	 * Purpose : Gets the reset password.
	 * 
	 * @param companyId
	 *            the company id
	 * @param employeeId
	 * @return the reset password
	 */
	String getResetPassword(Long companyId, Long employeeId);

	/**
	 * Purpose : Gets the employee image.
	 * 
	 * @param empID
	 *            the emp id
	 * @param imagePath
	 *            the image path
	 * @param imageWidth
	 *            the image width
	 * @param imageHeight
	 *            the image height
	 * @return the employee image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	byte[] getEmployeeImage(long empID, String imagePath, int imageWidth,
			int imageHeight) throws IOException;

	/**
	 * Purpose Gets the employee login history based on company id.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the employee login history
	 */
	String getEmployeeLoginHistory(Long companyId);

	/**
	 * Gets the xml.
	 * 
	 * @param empID
	 *            the emp id
	 * @param companyId
	 *            the company id
	 * @param languageId
	 *            the language id
	 * @param formId
	 *            the form id
	 * @return the xml
	 */
	EmployeeListFormPage getXML(Long empID, Long companyId, Long languageId,
			Long formId);

	EmployeeListFormPage getUpdatedXmls(Long loggedInEmployee, long empID,
			Long companyId, Long languageId, String mode);

	EmployeeListFormPage getViewProfileXMLS(long empID, Long companyId,
			Long languageId, String mode);

	EmployeeListFormPage fetchEmpsForCalendarTemplate(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId);

	String getEmployeeName(Employee employee);

	String deleteEmployeeImage(Long companyId, Long empID);

	EmployeeForm getUserDetails(String userId, String companyCode,
			Long employeeActivationCodeId);

	EmployeeListFormPage getViewProfileUpdatedXmls(Long loggedInEmployeeId,
			long empID, Long companyId, Long languageId, String mode);

	EmployeeListFormPage getViewProfileXML(Long empID, Long companyId,
			Long languageId, Long formId);

	void updateEmpNewValueInExistingXML(Long hrisChangeRequestId, Long companyId);

	EmployeeListFormPage saveDocTableRecord(
			DynamicFormTableDocumentDTO dynamicFormDocumentDTO, Long companyId,
			Long loggedInEmpId);

	DynamicFormTableDocumentDTO downloadEmpDoc(Long recordId, int seqNo,
			Long companyId, Long entityKey);

	EmployeeListFormPage updateDocTableRecord(
			DynamicFormTableDocumentDTO dynamicFormDocumentDTO, Long companyId,
			Long loggedInEmpId);

	EmployeeListFormPage empTakeOwnership(Long tableId, Long companyId,
			Integer seqNo, String ownership);

	EmployeeListFormPage getEmployeeDocHistory(Long companyId,Long entityKey, Long tableId, Integer seqNo, String tableType,Long formId,boolean isAdmin);

	EmployeeListFormPage updateTableRecord(String tableXML, Long tabId,
			Long companyId, Long employeeId, Integer seqNo);

	EmployeeListFormPage saveTableRecord(String tableXML, Long tabId,
			Long companyId, Long employeeId, Long formId, Integer version,
			Long entityKey);

	EmployeeListFormPage saveAddEmployee(String xml, Long companyID,
			Long entityID, Long formID, Integer version, Long empid);

	String isEnableEmployeeChangeWorkflow(Long companyId);

	String getEmployeeNameWithNumber(Long employeeId);

	String isAllowEmployeetouploaddocument(Long companyId);

	String isHideGetPassword(Long companyId);

	String empGetPassword(Long companyId, Long loggedInEmployeeId,
			Long employeeId);

	String getClientAdminEditDeleteEmpStatus(Long companyId);

	String getFormulaFieldCalculatedValue(
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<Long, Tab> dataTabMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap, String formula,
			String formulaType, DataDictionary dataDictionary, Long employeeId,
			Long companyId, Long tableId, Integer seqNo)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException;

	/**
	 * Purpose : Gets data for employee dynamic form grids.
	 * 
	 * @param tid
	 *            the tid
	 * @param columnCount
	 *            the column count
	 * @param fieldNames
	 *            the field names
	 * @param fieldTypes
	 *            the field types
	 * @param fieldDictIds
	 * @param companyId
	 *            the company id
	 * @param employeeId
	 * @param empID
	 * @param tableType
	 * @param sortOrder
	 * @param sortBy
	 * @return the employee list form page
	 */
	EmployeeListFormPage tableRecordList(Long tid, int columnCount,
			String[] fieldNames, String[] fieldTypes, String[] fieldDictIds,
			Long companyId, Long loggedInemployeeId, Long empID,
			String tableType, String sortOrder, String sortBy, Long languageId);

	EmployeeListFormPage updateEmplViewProfileDynamicFormRecord(String xml,
			Long companyID, Long entityID, Long formID, Integer version,
			Long empid, Long tabId, Long languageId);

	EmployeeListFormPage saveEmployeeViewProfile(String xml, Long companyID,
			Long entityID, Long formID, Integer version, Long empid,
			Long languageId);

	EmployeeListFormPage saveViewProfileTableRecord(String tableXML,
			Long tabId, Long companyId, Long employeeId, Long formId,
			Integer version, Long entityKey, Long languageId);

	EmployeeListFormPage updateViewProfileTableRecord(String tableXML,
			Long tabId, Long companyId, Long employeeId, Integer seqNo,
			Long languageId);

	EmployeeListFormPage getEmpChangeRequestData(Long loggedInEmployeeId,
			Long companyId, Long hrisChangeRequestId, Long languageId);

	EmployeeListFormPage empTableRecordList(Long tid, Long formId, int ColumnCount,
			String[] fieldNames, String[] fieldTypes, String[] fieldDictIds,
			Long companyId, Long employeeId, String tableType,
			String sortOrder, String sortBy, Long languageId);

	EmployeeListFormPage getTableEmpChangeRequestData(Long loggedInEmployeeId,
			Long companyId, Long employeeId, Long tableFieldDictionaryId,
			int seqNum, Long languageId);

	boolean areEmployeesOfSameCompanyGroup(String workingCompanyId, Long employeeId1, Long employeeId2);
	
	boolean isAdminAuthorizedForEmployee(Long empId,Long companyId,Long adminId);
	Long getEmpIdByEmpNoAndComId(String empNumber,Long companyId);

	boolean isAdminAuthorizedForViewGrid(Long viewId, Long companyId);
	List<String> getAuthorizedEmployeeList(List<String> empNoList,Long companyId,Long adminId);

	EmployeeListFormPage deleteTableRecord(Long tableId, Long companyId, Integer seqNo, String tableType,
			Long entityKey, String[] fieldDictIds, String fieldLabel, Long formId, Boolean fromAdmin);

	EmployeeListFormPage checkEmployeeNumberIsSame(String employeeNumber, Long companyId, String emailId,long empID);

	Map<String, Object> saveUserDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			DynamicForm dynamicForm);

	Map<String,Object> updateUserDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			DynamicForm dynamicForm);

	Map<String, Object> saveTableDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			DynamicForm dynamicForm);

	Map<String, Object> saveDocumentDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			DynamicForm dynamicForm, MultipartFile files);

	Map<String, Object> deleteTableRecord(DynamicForm dynamicForm, DynamicFormDataForm dynamicFormDataForm);

	DynamicFormTableDocumentDTO downloadEmpDoc(Long companyId, Long employeeId, String fileName);

}
