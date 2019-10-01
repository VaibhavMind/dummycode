package com.payasia.test.dao;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.Employee;

public class EmployeeDAOTest extends AbstractTestCase {
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDAOTest.class);

	@Resource
	EmployeeDAO employeeDAO;

	@Test
	public void getAllEmployees() {
		Employee employee = employeeDAO.findById(60);
		System.out.println("Emp Name: " + employee.getFirstName());

	}
}
