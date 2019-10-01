package com.payasia.logic;

/**
 * @author vivekjain
 * 
 */
import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.CustomAdminAccessControlResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface AdminAccessControlLogic.
 */
@Transactional
public interface AdminAccessControlLogic {

	/**
	 * purpose : search employees who have enabled or disabled to access
	 * application.
	 * 
	 * @param String
	 *            searchCondition
	 * @param String
	 *            searchText
	 * @param String
	 *            employeeStatus
	 * @param PageRequest
	 *            the pageRequest
	 * @param SortCondition
	 *            the sortDTO
	 * @param Long
	 *            the companyId
	 * @return CustomAdminAccessControlResponse contains Employee List .
	 */
	CustomAdminAccessControlResponse searchEmployee(String searchCondition,
			String searchText, String employeeStatus, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long employeeId);

	/**
	 * purpose : Enable employees to access application.
	 * 
	 * @param String
	 *            [] employeeId
	 */
	void enableEmployee(String[] employeeId,Long companyId);

	/**
	 * purpose : Disable employees to access application.
	 * 
	 * @param String
	 *            [] employeeId
	 */
	void disableEmployee(String[] employeeId,Long companyId);
	

}
