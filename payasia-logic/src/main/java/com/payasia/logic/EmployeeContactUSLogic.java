package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.EmployeeContactUSForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface EmployeeContactUSLogic.
 */
@Transactional
public interface EmployeeContactUSLogic {
	/**
	 * purpose : get Contact email id.
	 * 
	 * @param Long
	 *            the companyId
	 * @return contact email id
	 */
	String getContactEmail(Long companyId);

	/**
	 * purpose : Send Mail by Logged In employee.
	 * 
	 * @param EmployeeContactUSForm
	 *            the employeeContactUSForm
	 * @param Long
	 *            the employeeId
	 * @return
	 */
	String sendMail(EmployeeContactUSForm employeeContactUSForm, Long employeeId);
}
