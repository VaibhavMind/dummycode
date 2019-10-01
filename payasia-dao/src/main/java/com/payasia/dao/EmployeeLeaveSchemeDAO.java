/**
 * @author vivekjain
 *
 */
package com.payasia.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.AssignLeaveSchemeDTO;
import com.payasia.common.dto.EmployeeHeadCountReportDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.LeaveScheme;

/**
 * The Interface EmployeeLeaveSchemeDAO.
 */

public interface EmployeeLeaveSchemeDAO {

	/**
	 * Delete.
	 * 
	 * @param employeeLeaveScheme
	 *            the employee leave scheme
	 */
	void delete(EmployeeLeaveScheme employeeLeaveScheme);

	/**
	 * Find by id.
	 * 
	 * @param employeeLeaveSchemeId
	 *            the employee leave scheme id
	 * @return the employee leave scheme
	 */
	EmployeeLeaveScheme findById(Long employeeLeaveSchemeId);

	/**
	 * Update.
	 * 
	 * @param employeeLeaveScheme
	 *            the employee leave scheme
	 */
	void update(EmployeeLeaveScheme employeeLeaveScheme);

	/**
	 * Save.
	 * 
	 * @param employeeLeaveScheme
	 *            the employee leave scheme
	 */
	void save(EmployeeLeaveScheme employeeLeaveScheme);

	/**
	 * Find by condition.
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
	List<EmployeeLeaveScheme> findByCondition(
			AssignLeaveSchemeDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	/**
	 * Gets the count for condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the count for condition
	 */
	Long getCountForCondition(AssignLeaveSchemeDTO conditionDTO, Long companyId);

	/**
	 * Gets the sort path for search employee.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param empRoot
	 *            the emp root
	 * @param empJoin
	 *            the emp join
	 * @param leaveSchemeJoin
	 *            the leave scheme join
	 * @return the sort path for search employee
	 */
	Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<EmployeeLeaveScheme> empRoot,
			Join<EmployeeLeaveScheme, Employee> empJoin,
			Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin);

	/**
	 * Find by condition company.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<EmployeeLeaveScheme> findByConditionCompany(Long companyId);

	EmployeeLeaveScheme saveReturn(EmployeeLeaveScheme employeeLeaveScheme);

	EmployeeLeaveScheme findByEmployeeScheme(Long employeeId,
			Long leaveSchemeId, String date, String dateFormat);

	EmployeeLeaveScheme findByEmpIdAndEndDate(Long employeeId);

	EmployeeLeaveScheme checkEmpLeaveSchemeByDate(Long empLeaveSchemeId,
			Long employeeId, String date, String dateFormat);

	EmployeeLeaveScheme findByActive(Long employeeId, boolean active);

	List<Integer> getYearFromStartDate(Long companyId);

	List<Integer> getYearFromEndDate(Long companyId);

	List<EmployeeLeaveScheme> findByCompanyAndEmployee(String employeeNumber,
			int year, Long companyId);

	EmployeeLeaveScheme checkEmpLeaveSchemeByDateAndEmpNum(
			String employeeNumber, int year, String date, String dateFormat,
			Long companyId);

	List<EmployeeLeaveScheme> findByCompanyAndEmployeeId(Long employeeId,
			int parseInt, Long companyId);

	EmployeeLeaveScheme checkEmpLeaveSchemeByStartDate(Long empLeaveSchemeId,
			Long employeeId, String date, String dateFormat);

	EmployeeLeaveScheme checkEmpLeaveSchemeByEndDate(Long empLeaveSchemeId,
			Long employeeId, String date, String dateFormat);

	EmployeeLeaveScheme getEmpLeaveSchemeGreaterThanCurrDate(
			Long empLeaveSchemeId, Long employeeId, String date,
			String dateFormat);

	EmployeeLeaveScheme findByEmpNumAndLeaveSchemeName(String employeeNumber,
			String leaveSchemeName, Long companyId);

	List<Long> getEmployeesOfLeaveSheme(Long leaveSchemeId, Date date);

	EmployeeLeaveScheme findByEmpIdAndLeaveSchemeId(Long employeeId,
			Long leaveSchemeId);

	List<EmployeeLeaveScheme> findbyLeaveScheme(long leaveSchemeId);

	List<EmployeeLeaveScheme> getActiveWithFutureLeaveScheme(Long employeeId,
			String date, String dateFormat);

	EmployeeLeaveScheme getLastAssignedEmpLeaveScheme(Long employeeId);

	EmployeeLeaveScheme getActiveLeaveSchemeByDate(Long employeeId,
			String date, String dateFormat);

	List<EmployeeHeadCountReportDTO> getLeaveHeadCountReportDetail(
			String startDate, String endDate, String dateFormat,
			String companyIdList);

	EmployeeLeaveScheme findSchemeByCompanyId(Long empLeaveSchemeId, Long companyId);
}
