package com.payasia.logic;

import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.dao.bean.Employee;

/**
 * The Interface EmployeeDataImportStrategy.
 */
public interface EmployeeDataImportStrategy extends DataImportStrategy {

	/**
	 * Purpose : Save the data for Upload Type - Insert Only (01).
	 * 
	 * @param employee
	 *            the employee
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void insertNew(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);

	/**
	 * Purpose : Save the data for Upload Type - Update Existing and Insert New
	 * (02).
	 * 
	 * @param employee
	 *            the employee
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void updateORInsert(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);

	/**
	 * Purpose : Save the data for Upload Type - Delete All and Insert All (03).
	 * 
	 * @param employee
	 *            the employee
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void deleteAndInsert(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);

}
