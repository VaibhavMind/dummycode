package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmployeeActivationCode;

public interface EmployeeActivationCodeDAO {

	void update(EmployeeActivationCode employeeActivationCode);

	void save(EmployeeActivationCode employeeActivationCode);

	void delete(EmployeeActivationCode employeeActivationCode);

	EmployeeActivationCode findByID(long employeeActivationCodeId);

	EmployeeActivationCode findByActivationCode(String activationCode,
			String userName);

	List<EmployeeActivationCode> findByEmployee(Long employeeId);

}
