package com.payasia.dao;

import com.payasia.dao.bean.EmployeeLoginDetail;

public interface EmployeeLoginDetailDAO {

	void update(EmployeeLoginDetail employeeLoginDetail);

	void save(EmployeeLoginDetail employeeLoginDetail);

	void delete(EmployeeLoginDetail employeeLoginDetail);

	EmployeeLoginDetail findByID(long employeeLoginDetailId);

	/**
	 * Find by Employee Id
	 * 
	 * @param employeeId
	 * @return EmployeeLoginDetail
	 */
	EmployeeLoginDetail findByEmployeeId(long employeeId);

}
