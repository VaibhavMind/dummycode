package com.payasia.logic;

import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.dao.bean.Company;

/**
 * The Interface CompanyDataImportStrategy.
 */
public interface CompanyDataImportStrategy extends DataImportStrategy {

	/**
	 * Purpose : Save the data for Upload Type - Insert Only (01).
	 * 
	 * @param company
	 *            the company
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void insertNew(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);

	/**
	 * Purpose : Save the data for Upload Type - Update Existing and Insert New
	 * (02).
	 * 
	 * @param company
	 *            the company
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void updateORInsert(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);

	/**
	 * Purpose : Save the data for Upload Type - Delete All and Insert All (03).
	 * 
	 * @param company
	 *            the company
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void deleteAndInsert(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);
}
