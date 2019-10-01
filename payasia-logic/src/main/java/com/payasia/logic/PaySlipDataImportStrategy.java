/**
 * 
 */
package com.payasia.logic;

import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.dao.bean.Payslip;

/**
 * @author tarungupta
 * 
 */
public interface PaySlipDataImportStrategy extends DataImportStrategy {
	/**
	 * Purpose : Save the data for Upload Type - Insert Only (01).
	 * 
	 * @param payslip
	 *            the payslip
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void insertNew(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);

	/**
	 * Purpose : Save the data for Upload Type - Update Existing and Insert New
	 * (02).
	 * 
	 * @param payslip
	 *            the payslip
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void updateORInsert(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);

	/**
	 * Purpose : Save the data for Upload Type - Delete All and Insert All (03).
	 * 
	 * @param payslip
	 *            the payslip
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */

	void deleteAndInsert(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId);

}
