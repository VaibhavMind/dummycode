package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmployeePasswordChangeHistory;

/**
 * The Interface EmployeePasswordChangeHistoryDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmployeePasswordChangeHistoryDAO {

	/**
	 * purpose : Save EmployeePasswordChangeHistory Object.
	 * 
	 * @param employeePasswordChangeHistory
	 *            the employee password change history
	 */
	void save(EmployeePasswordChangeHistory employeePasswordChangeHistory);

	List<EmployeePasswordChangeHistory> getPreviousPasswords(Long employeeId,
			int maxResults);

	EmployeePasswordChangeHistory getPreviousPasswords(Long employeeId);

}
