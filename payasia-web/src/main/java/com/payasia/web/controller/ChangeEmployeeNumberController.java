/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.ChangeEmployeeNameListForm;

/**
 * The Interface ChangeEmployeeNumberController.
 */
/**
 * @author abhisheksachdeva
 * 
 */
public interface ChangeEmployeeNumberController {

	/**
	 * purpose : Search employee.
	 * 
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param keyword
	 *            the keyword
	 * @param searchBy
	 *            the search by
	 * @param model
	 *            the model
	 * @return the string
	 */
	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String keyword, String searchBy,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : Change employee number.
	 * 
	 * @param chgByEmpID
	 *            the chg by emp id
	 * @param empID
	 *            the emp id
	 * @param oldEmployeeNumber
	 *            the old employee number
	 * @param changeTo
	 *            the change to
	 * @param changeOn
	 *            the change on
	 * @param changedBy
	 *            the changed by
	 * @param changeReason
	 *            the change reason
	 * @param model
	 *            the model
	 * @return
	 */

	String changeEmployeeNumber(
			ChangeEmployeeNameListForm changeEmployeeNameListForm,
			Long empNumSeriesId, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Gets the all.
	 * 
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @return the all
	 */
	String getAll(String columnName, String sortingType, int page, int rows,
			HttpServletRequest request);

	String searchEmployeeList(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

}
