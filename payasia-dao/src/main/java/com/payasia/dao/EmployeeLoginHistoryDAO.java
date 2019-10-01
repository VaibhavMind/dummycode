package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmployeeLoginHistory;

 
/**
 * The Interface EmployeeLoginHistoryDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmployeeLoginHistoryDAO {

	/**
	 * purpose : Save EmployeeLoginHistory Object.
	 * 
	 * @param employeeLoginHistory
	 *            the employee login history
	 */
	void save(EmployeeLoginHistory employeeLoginHistory);

	/**
	 * purpose : Find EmployeeLoginHistory Objects List By companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<Object[]> findEmployeeLoginHistory(Long companyId);

	/**
	 * Find by id.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return the employee login history
	 */
	EmployeeLoginHistory findById(Long employeeId);

	/**
	 * Find by employee id.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return the list
	 */
	List<EmployeeLoginHistory> findByEmployeeId(Long employeeId);

	/**
	 * 
	 * @param employeeId
	 * @return
	 */
	boolean isEmployeeLoginHistoryExist(long employeeId);

	List<EmployeeLoginHistory> findByEmployeeIdAndDate(List<Long> employeeIds,
			String fromDate, String toDate, String dateFormat);

}
