package com.payasia.dao;

import java.util.List;

import com.payasia.common.form.AdminAccessControlForm;

/**
 * The Interface AdminAccessControlDAO.
 */
public interface AdminAccessControlDAO {

	/**
	 * Access control.
	 * 
	 * @return the list
	 */
	List<AdminAccessControlForm> accessControl();

	/**
	 * Search employee.
	 * 
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param employeeStatus
	 *            the employee status
	 * @return the list
	 */
	List<AdminAccessControlForm> searchEmployee(String searchCondition,
			String searchText, String employeeStatus);

	/**
	 * Enable employee.
	 * 
	 * @param employeeId
	 *            the employee id
	 */
	void enableEmployee(String[] employeeId);

	/**
	 * Disable employee.
	 * 
	 * @param employeeId
	 *            the employee id
	 */
	void disableEmployee(String[] employeeId);
}
