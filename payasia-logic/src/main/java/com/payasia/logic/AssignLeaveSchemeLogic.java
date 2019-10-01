/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.AssignLeaveSchemeForm;
import com.payasia.common.form.AssignLeaveSchemeResponse;
import com.payasia.common.form.EmployeeLeaveDistributionForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface assignLeaveSchemeLogic.
 */
@Transactional
public interface AssignLeaveSchemeLogic {

	/**
	 * Search assign leave scheme.
	 * 
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param empLeaveSchemeStatus
	 *            the emp leave scheme status
	 * @param toDate
	 * @param fromDate
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the assign leave scheme response
	 */
	AssignLeaveSchemeResponse searchAssignLeaveScheme(String searchCondition,
			String searchText, String fromDate, String toDate,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	/**
	 * Search employee.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param empName
	 *            the emp name
	 * @param empNumber
	 *            the emp number
	 * @param companyId
	 *            the company id
	 * @return the leave Scheme response form
	 */
	AssignLeaveSchemeResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

	/**
	 * Gets the leave scheme list.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the leave scheme list
	 */
	List<AssignLeaveSchemeForm> getLeaveSchemeList(Long companyId);

	/**
	 * Adds the emp leave scheme.
	 * 
	 * @param companyId
	 *            the company id
	 * @param assignLeaveSchemeForm
	 *            the assign leave scheme form
	 * @return the string
	 */
	String addEmpLeaveScheme(Long companyId,
			AssignLeaveSchemeForm assignLeaveSchemeForm);

	/**
	 * Edits the emp leave scheme.
	 * 
	 * @param companyId
	 *            the company id
	 * @param assignLeaveSchemeForm
	 *            the assign leave scheme form
	 * @return the string
	 */
	String editEmpLeaveScheme(Long companyId,
			AssignLeaveSchemeForm assignLeaveSchemeForm);

	/**
	 * Delete emp leave scheme.
	 * 
	 * @param empLeaveSchemeId
	 *            the emp leave scheme id
	 */
	void deleteEmpLeaveScheme(Long empLeaveSchemeId);

	/**
	 * Gets the emp leave scheme for edit.
	 * 
	 * @param empLeaveSchemeId
	 *            the emp leave scheme id
	 * @param companyId
	 * @return the emp leave scheme for edit
	 */
	AssignLeaveSchemeForm getEmpLeaveSchemeForEdit(Long empLeaveSchemeId,
			Long companyId);

	/**
	 * Edits the emp leave distribution.
	 * 
	 * @param companyId
	 *            the company id
	 * @param employeeLeaveDistributionForm
	 *            the employee leave distribution form
	 * @return the string
	 */
	String editEmpLeaveDistribution(Long companyId,
			EmployeeLeaveDistributionForm employeeLeaveDistributionForm);

	/**
	 * View employeeleave distribution.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @param empLeaveSchemeId
	 *            the emp leave scheme id
	 * @param year
	 * @return the assign leave scheme response
	 */
	AssignLeaveSchemeResponse viewEmployeeleaveDistribution(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long empLeaveSchemeId, Integer year);

	AssignLeaveSchemeForm importAssignLeaveScheme(
			AssignLeaveSchemeForm assignLeaveSchemeForm, Long companyId);

	List<AssignLeaveSchemeForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId);

	String getEmployeeName(Long loggedInEmployeeId, String employeeNumber,
			Long companyId);

	AssignLeaveSchemeForm importLeaveDistribution(
			AssignLeaveSchemeForm assignLeaveSchemeForm, Long companyId);

	void deleteEmpLeaveScheme(Long empLeaveSchemeId, Long companyId);

}
