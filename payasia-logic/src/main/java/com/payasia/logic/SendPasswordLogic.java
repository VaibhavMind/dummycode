package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SendPasswordResponse;
import com.payasia.common.form.SortCondition;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface SendPasswordLogic.
 */
@Transactional
public interface SendPasswordLogic {

	/**
	 * purpose : get EmployeeList By search Condition.
	 * 
	 * @param employeeId
	 * 
	 * @param String
	 *            the searchCondition
	 * @param String
	 *            the searchText
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 * @param Long
	 *            the companyId
	 * @return SendPasswordResponse contains Employees list
	 */
	SendPasswordResponse getEmployeeList(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId);

	/**
	 * purpose : send Email with Password to employees.
	 * 
	 * @param String
	 *            [] the employeeId
	 * @param Long
	 *            the companyId
	 * @return Response
	 */
	String sendPwdEmail(Long companyId, String[] employeeId);
}
