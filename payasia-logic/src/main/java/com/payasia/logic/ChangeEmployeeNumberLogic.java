/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.AssignLeaveSchemeResponse;
import com.payasia.common.form.ChangeEmployeeNameListForm;
import com.payasia.common.form.ChangeEmployeeNumberFromResponse;
import com.payasia.common.form.EmployeeNumberSrForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface ChangeEmployeeNumberLogic.
 */
/**
 * @author abhisheksachdeva
 * 
 */
@Transactional
public interface ChangeEmployeeNumberLogic {

	/**
	 * purpose : Gets the change employee name list.
	 * 
	 * @param companyId
	 * 
	 * @return the change employee name list
	 */
	ChangeEmployeeNumberFromResponse getChangeEmployeeNameList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	/**
	 * purpose : Gets the search result list.
	 * 
	 * @param companyId
	 * 
	 * @param keyword
	 *            the keyword
	 * @param searchBy
	 *            the search by
	 * @return the search result list
	 */
	ChangeEmployeeNumberFromResponse getSearchResultList(Long companyId,
			String keyword, String searchBy, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * purpose : Change employee number.
	 * 
	 * @param changeEmployeeNameListForm
	 *            the change employee name list form
	 * @param employeeId
	 * @param companyId
	 * @param empNumSeriesId
	 * @return
	 */
	String changeEmployeeNumber(
			ChangeEmployeeNameListForm changeEmployeeNameListForm,
			Long companyId, Long employeeId, Long empNumSeriesId);

	/**
	 * purpose : Get Employee Number Series.
	 * 
	 * @param Long
	 *            companyId
	 * @return the EmployeeNumberSrForm list
	 */
	List<EmployeeNumberSrForm> getEmployeeNumSeries(Long companyId);

	AssignLeaveSchemeResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);
}
