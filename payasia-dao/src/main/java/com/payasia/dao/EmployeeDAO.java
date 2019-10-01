/**
 * @author ragulapraveen
 *
 */
package com.payasia.dao;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.AccessControlConditionDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.ManageRolesConditionDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;

/**
 * The Interface EmployeeDAO.
 */

/**
 * @author ragulapraveen
 *
 */
/**
 * @author ragulapraveen
 *
 */
/**
 * @author ragulapraveen
 * 
 */
public interface EmployeeDAO {

	/**
	 * Find all Employee Object list.
	 * 
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<Employee> findAll(long companyId, PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Gets the sort path for all employee.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param empRoot
	 *            the emp root
	 * @param empCompanyJoin
	 *            the emp company join
	 * @return the sort path for all employee
	 */
	Path<String> getSortPathForAllEmployee(SortCondition sortDTO, Root<Employee> empRoot,
			Join<Employee, Company> empCompanyJoin);

	/**
	 * Find Employee Object list by role.
	 * 
	 * @param roleId
	 *            the role id
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Employee> findByRole(long roleId, long companyId);

	/**
	 * Find Employee Object by id.
	 * 
	 * @param userId
	 *            the user id
	 * @return the employee
	 */
	Employee findByID(long userId);

	/**
	 * Find Employee Object list by condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Employee> findByCondition(AccessControlConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);

	/**
	 * Gets the sort path for search employee.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param empRoot
	 *            the emp root
	 * @return the sort path for search employee
	 */
	Path<String> getSortPathForSearchEmployee(SortCondition sortDTO, Root<Employee> empRoot);

	/**
	 * Update Employee Object.
	 * 
	 * @param employee
	 *            the employee
	 */
	void update(Employee employee);

	/**
	 * Save Employee Object.
	 * 
	 * @param employee
	 *            the employee
	 * @return the employee
	 */
	Employee save(Employee employee);

	/**
	 * Find Employee Object by number.
	 * 
	 * @param empNumber
	 *            the emp number
	 * @param companyId
	 *            the company id
	 * @return the employee
	 */
	Employee findByNumber(String empNumber, Long companyId);

	/**
	 * Find Employee Object by condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Employee> findByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);

	/**
	 * Find All Employee Object(Enabled & Disabled) by condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Employee> findAllByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);

	/**
	 * Find Employee Object by id.
	 * 
	 * @param empID
	 *            the emp id
	 * @return the employee
	 */
	Employee findById(long empID);

	/**
	 * Delete Employee Object.
	 * 
	 * @param employee
	 *            the employee
	 */
	void delete(Employee employee);

	/**
	 * Gets the count for all.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the count for all
	 */
	int getCountForAll(long companyId);

	/**
	 * Gets the count for condition.
	 * 
	 * @param accessControlconditionDTO
	 *            the access controlcondition dto
	 * @param companyId
	 *            the company id
	 * @return the count for condition
	 */
	Integer getCountForCondition(AccessControlConditionDTO accessControlconditionDTO, Long companyId);

	/**
	 * Gets the count for role.
	 * 
	 * @param roleId
	 *            the role id
	 * @param companyId
	 *            the company id
	 * @return the count for role
	 */
	int getCountForRole(Long roleId, Long companyId);

	/**
	 * Gets the max employee id.
	 * 
	 * @return the max employee id
	 */
	Long getMaxEmployeeId();

	/**
	 * Find Employee Object list by roleID and companyid.
	 * 
	 * @param roleId
	 *            the role id
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Employee> findByRoleForPayAsiaUsers(long roleId, long companyId);

	/**
	 * Gets Employee Object list by searchString and companyId.
	 * 
	 * @param searchString
	 *            the search string
	 * @param companyId
	 *            the company id
	 * @param employeeShortListDTO
	 *            the employee short list dto
	 * @return the employee ids
	 */
	List<Employee> getEmployeeIds(String searchString, Long companyId, EmployeeShortListDTO employeeShortListDTO);

	/**
	 * Save return Employee Object.
	 * 
	 * @param employee
	 *            the employee
	 * @return the employee
	 */
	Employee saveReturn(Employee employee);

	/**
	 * New transaction save Employee Object.
	 * 
	 * @param employee
	 *            the employee
	 */
	void newTransactionSave(Employee employee);

	/**
	 * New tran save return Employee Object.
	 * 
	 * @param employee
	 *            the employee
	 * @return the employee
	 */
	Employee newTranSaveReturn(Employee employee);

	/**
	 * New tran update Employee Object.
	 * 
	 * @param employee
	 *            the employee
	 */
	void newTranUpdate(Employee employee);

	/**
	 * New tran delete Employee Object.
	 * 
	 * @param employee
	 *            the employee
	 */
	void newTranDelete(Employee employee);

	/**
	 * Authenticate employee by loginname,password and companycode.
	 * 
	 * @param loginName
	 *            the login name
	 * @param password
	 *            the password
	 * @param companyCode
	 *            the company code
	 * @return the boolean
	 */
	Boolean authenticateEmployee(String loginName, String password, String companyCode);

	/**
	 * Gets the employee by login name.
	 * 
	 * @param loginName
	 *            the login name
	 * @param companyCode
	 *            the company code
	 * @return the employee by login name
	 */
	Employee getEmployeeByLoginName(String loginName, String companyCode);

	/**
	 * Gets the employee by login name.
	 * 
	 * @param loginName
	 *            the login name
	 * @return the employee by login name
	 */
	Employee getEmployeeByLoginName(String loginName);

	/**
	 * Find Employee Object by colmap,formIds,companyid,finalfilterlist and
	 * dataformat .
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param companyId
	 *            the company id
	 * @param finalFilterList
	 *            the final filter list
	 * @param dataFormat
	 *            the data format
	 * @param tableRecordInfo
	 *            the table record info
	 * @param tableElements
	 *            the table elements
	 * @param employeeShortListDTO
	 *            the employee short list dto
	 * @return the list
	 */
	List<Object[]> findByCondition(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds, Long companyId,
			List<ExcelExportFiltersForm> finalFilterList, String dataFormat,
			Map<String, DataImportKeyValueDTO> tableRecordInfo, List<DataImportKeyValueDTO> tableElements,
			EmployeeShortListDTO employeeShortListDTO, boolean showEffectiveTableData);

	/**
	 * Gets the employee by email.
	 * 
	 * @param EmailId
	 *            the email id
	 * @return the employee by email
	 */
	Employee getEmployeeByEmail(String EmailId);

	/**
	 * Find employee object by employeeNUmber,companyid and email.
	 * 
	 * @param employeeNumber
	 *            the employee number
	 * @param companyId
	 *            the company id
	 * @param email
	 *            the email
	 * @return the employee
	 */
	Employee findEmployee(String employeeNumber, Long companyId, String email);

	/**
	 * Find Employee object by employeeId,email.
	 * 
	 * @param employeeID
	 *            the employee id
	 * @param email
	 *            the email
	 * @return the employee
	 */
	Employee findByEmailId(Long employeeID, String email);

	/**
	 * Find Employee object by condition companyid,conditonDTO.
	 * 
	 * @param companyId
	 *            the company id
	 * @param conditionDTO
	 *            the condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<Employee> findByConditionCompany(Long companyId, EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Gets the count for condition company.
	 * 
	 * @param companyId
	 *            the company id
	 * @param conditionDTO
	 *            the condition dto
	 * @return the count for condition company
	 */
	int getCountForConditionCompany(Long companyId, EmployeeConditionDTO conditionDTO);

	/**
	 * Gets the count for condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the count for condition
	 */
	int getCountForCondition(EmployeeConditionDTO conditionDTO, Long companyId);

	/**
	 * Find Employee Object list by condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<Employee> findByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Gets the count for condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @return the count for condition
	 */
	int getCountForCondition(EmployeeConditionDTO conditionDTO);

	/**
	 * Find Employees for send password.
	 * 
	 * @param accessControlconditionDTO
	 *            the access controlcondition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Employee> findEmpForSendPassword(AccessControlConditionDTO accessControlconditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	/**
	 * Gets the employee by emp num and comp code.
	 * 
	 * @param employeeNum
	 *            the employee num
	 * @param companyCode
	 *            the company code
	 * @return the employee by emp num and comp code
	 */
	Employee getEmployeeByEmpNumAndCompCode(String employeeNum, String companyCode);

	/**
	 * Gets the emp count for condition.
	 * 
	 * @param accessControlconditionDTO
	 *            the access controlcondition dto
	 * @param companyId
	 *            the company id
	 * @return the emp count for condition
	 */
	int getEmpCountForCondition(AccessControlConditionDTO accessControlconditionDTO, Long companyId);

	/**
	 * Gets the emp count for send pwd.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the emp count for send pwd
	 */
	Long getEmpCountForSendPwd(AccessControlConditionDTO conditionDTO, Long companyId);

	/**
	 * Gets the count for work flow employee.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the count for work flow employee
	 */
	Long getCountForWorkFlowEmployee(EmployeeConditionDTO conditionDTO, Long companyId);

	/**
	 * Find employees calendar template.
	 * 
	 * @param accessControlconditionDTO
	 *            the access controlcondition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Employee> findEmployeesCalendarTemplate(AccessControlConditionDTO accessControlconditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	/**
	 * Find by condition group.
	 * 
	 * @param colMap
	 *            the col map
	 * @param formIds
	 *            the form ids
	 * @param companyId
	 *            the company id
	 * @param finalFilterList
	 *            the final filter list
	 * @param tableRecordInfo
	 *            the table record info
	 * @param tableElements
	 *            the table elements
	 * @param employeeShortListDTO
	 *            the employee short list dto
	 * @return the list
	 */
	List<Object[]> findByConditionGroup(Map<String, DataImportKeyValueDTO> colMap, List<Long> formIds, Long companyId,
			List<ExcelExportFiltersForm> finalFilterList, Map<String, DataImportKeyValueDTO> tableRecordInfo,
			List<DataImportKeyValueDTO> tableElements, EmployeeShortListDTO employeeShortListDTO);

	/**
	 * Find granter employees.
	 * 
	 * @param companyId
	 *            the company id
	 * @param leaveSchemeId
	 *            the leave scheme id
	 * @param leaveTypeId
	 *            the leave type id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<Employee> findGranterEmployees(Long companyId, Long leaveSchemeId, Long leaveTypeId, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Gets the count for granter employees.
	 * 
	 * @param companyId
	 *            the company id
	 * @param leaveSchemeId
	 *            the leave scheme id
	 * @param leaveTypeId
	 *            the leave type id
	 * @return the count for granter employees
	 */
	Integer getCountForGranterEmployees(Long companyId, Long leaveSchemeId, Long leaveTypeId);

	/**
	 * Find annual rollback emps.
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
	 * @return the list
	 */
	List<Employee> findAnnualRollbackEmps(Long companyId, String fromDate, String toDate, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Gets the count annual rollback emps.
	 * 
	 * @param companyId
	 *            the company id
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @return the count annual rollback emps
	 */
	Integer getCountAnnualRollbackEmps(Long companyId, String fromDate, String toDate);

	/**
	 * Find employees of group companies.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Employee> findEmployeesOfGroupCompanies(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	/**
	 * Gets the group company employee count.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the group company employee count
	 */
	int getGroupCompanyEmployeeCount(EmployeeConditionDTO conditionDTO, Long companyId);

	List<Employee> getEmployeeIdsForGroupCompany(String searchString, Long companyId, Long companyGroupId,
			EmployeeShortListDTO employeeShortListDTO);

	List<Employee> findUnAssignEmployeesCalendarTemplate(AccessControlConditionDTO accessControlconditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long calendartemplateId, String startDate,
			String dateFormat);

	List<Employee> findByShortlist(long companyId, Long employeeId, EmployeeShortListDTO employeeShortListDTO,
			PageRequest pageDTO, SortCondition sortDTO, ManageRolesConditionDTO conditionDTO);

	Integer getCountForPrivilegeUser(long companyId, Long employeeId, EmployeeShortListDTO employeeShortListDTO);

	List<Tuple> getEmployeeNameTupleList(Long companyId);

	Integer getEmployeeListByAdvanceFilterCount(EmployeeConditionDTO conditionDTO, Long companyId);

	List<Employee> getEmployeeListByAdvanceFilter(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	void updateEmploymentStatus(Long companyId, Date dateCtr);

	void updateEmployeeStatus(Long companyId, Date dateCtr);

	Collection<? extends Object[]> createQueryForCustomFieldReport(Map<String, DataImportKeyValueDTO> colMap,
			List<Long> formIds, Long companyId, String dateFormat,
			Map<String, DataImportKeyValueDTO> tableRecordInfoFrom, List<Long> employeeIdsList,
			boolean showOnlyEmployeeDynFieldCode);

	List<Employee> findByCompany(Long companyId);

	List<Employee> findEmployeesByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	EmployeeListForm deleteEmployeeProc(Long employeeId);

	int findEmployeesOfGroupCompaniesCount(EmployeeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	List<String> findAllEmpNumberByCompany(Long companyId);

	List<Employee> findResignedEmpForClaim(Long companyId, String fromDate, String toDate, PageRequest pageDTO,
			SortCondition sortDTO);

	List<BigInteger> findApplicableEmployeeIds(String queryString, Long companyId, Map<String, String> paramValueMap);

	List<String> findShortListedEmployeeIds(String queryString, Map<String, String> paramValueMap, Long companyId);

	/**
	 * Check for employee documents.
	 * 
	 * @param queryString
	 *            the query string
	 * @return the list
	 */

	List<BigInteger> checkForEmployeeDocuments(String queryString, Map<String, String> paramValueMap, Long employeeId,
			Long companyId);

	/**
	 * Gets the employee by Email.
	 * 
	 * @param loginName
	 *            the login name
	 * @param companyCode
	 *            the Company Code
	 * @return the List of employees by Email
	 */
	List<Employee> getEmployeeByEmail(String email, String companyCode);

	List<Employee> findByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId);

	List<Employee> getEmployeeListByEmpNum(Long companyId, List<String> employeeNumberList);

	List<Employee> findByEmpStaticInfo(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, Long companyId);

	Employee getEmpByLoginNameOrEmail(String loginNameOrEmail, String companyCode);

	List<Employee> getEmpByUsernameOrEmailOrFullName(String emailOrUsernameOrFullName, String companyCode);

	List<Employee> getEmpByEmailOrFullName(String emailOrFullName, String companyCode);

	List<Tuple> getLundinManagerInfoDetail(EmployeeConditionDTO conditionDTO, Long employeeId);

	List<Employee> getEmpByPrivilageName(Long companyId, String PrivilageName);

	Employee findByNumber(String string);

	Object getEmpByValueCustomField(Long companyId, Long employeeId, String custonField);

	boolean isEmployeeExistInCompany(Long employee, Long companyId);

	List<Long> findAllEmpIdByCompany(Long companyId);

	Employee findByNumberEmp(String employeeNumber, Long companyId, Long loggedInUser);

	Employee findEmpByIdinReviewers(Long employeeId, Long leaveApplicationID);

	Employee findById(long userId, Long companyId);

	Employee findByGroupCompanyId(Long reviewerId, Long groupId);

	List<Employee> findByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId, Long groupCompanyId);

	List<Employee> getGroupCompanyEmployee(Long groupId);

	List<Employee> getEmployeeByEmailWithCompanyCode(String email, String companyCode);

	int getCountForGroupCondition(EmployeeConditionDTO conditionDTO, Long companyId, Long companyGroupId);

	List<Employee> findByGroupCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long companyGroupId);

}
