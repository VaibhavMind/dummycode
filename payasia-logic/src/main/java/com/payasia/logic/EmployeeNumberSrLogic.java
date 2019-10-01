/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.EmployeeNumberSrForm;
import com.payasia.common.form.EmployeeNumberSrFormResponse;
import com.payasia.common.form.EmployeeTypeForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface EmployeeNumberSrLogic.
 */

@Transactional
public interface EmployeeNumberSrLogic {

	/**
	 * purpose : View Employee Number Series List.
	 * 
	 * @param Long
	 *            the companyId
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 * @return EmployeeNumberSrFormResponse contains Employee Number Series List
	 */
	EmployeeNumberSrFormResponse viewEmpNoSr(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * purpose : Save Employee Number Series of Employee.
	 * 
	 * @param EmployeeNumberSrForm
	 *            the employeeNumberSrForm
	 * @param Long
	 *            the companyId
	 * @return response
	 */
	String saveNewSeries(EmployeeNumberSrForm employeeNumberSrForm,
			Long companyId);

	/**
	 * purpose : Update Employee Number Series of Employee.
	 * 
	 * @param EmployeeNumberSrForm
	 *            the employeeNumberSrForm
	 * @param Long
	 *            the companyId
	 * @return
	 */
	String editSeries(Long companyId, EmployeeNumberSrForm employeeNumberSrForm);

	/**
	 * purpose : Delete Employee Number Series of Employee.
	 * 
	 * @param long the empNoSeriesId
	 * @return
	 */
	String deleteSeries(long empNoSeriesId);

	/**
	 * purpose : Get Employee Number Series of Employee for Edit.
	 * 
	 * @param long the empNoSeriesId
	 * @return EmployeeNumberSrForm
	 */
	EmployeeNumberSrForm getDataForSeries(long empNoSeriesId);

	/**
	 * purpose : Get Employee Type List for Employee.
	 * 
	 * @param Long
	 *            the companyId
	 * @return EmployeeTypeForm contains List
	 */
	List<EmployeeTypeForm> getEmployeeTypeList(Long companyId);

}
