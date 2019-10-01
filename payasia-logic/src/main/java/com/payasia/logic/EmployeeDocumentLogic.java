/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.EmployeeTaxDocumentForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateResponse;

/**
 * The Interface EmployeeDocumentLogic.
 */
@Transactional
public interface EmployeeDocumentLogic {

	/**
	 * Purpose: To Generate tax document.
	 * 
	 * @param documentId
	 *            the document id
	 * @return the string
	 */
	String generateTaxDocument(Long documentId);

	/**
	 * Purpose: To Get the pay slip frequency details.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the pay slip frequency details
	 */
	AdminPaySlipForm getPaySlipFrequencyDetails(Long companyId);

	/**
	 * Purpose: to Get the employee id for auto fill based on search string.
	 * 
	 * @param companyId
	 *            the company id
	 * @param searchString
	 *            the search string
	 * @param employeeId
	 * @return the employee id
	 */
	List<AdminPaySlipForm> getEmployeeId(Long companyId, String searchString,
			Long employeeId);

	/**
	 * Purpose: Gets the year list.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the year list
	 */
	List<Integer> getYearList(Long companyId);

	/**
	 * Purpose: Gets the tax document list.
	 * 
	 * @param employeeNumber
	 *            the employee number
	 * @param year
	 *            the year
	 * @param companyId
	 *            the company id
	 * @param employeeId
	 * @return the tax document list
	 */
	EmployeeTaxDocumentForm getTaxDocumentList(String employeeNumber, int year,
			Long companyId, Long employeeId);

	WorkFlowDelegateResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

}
