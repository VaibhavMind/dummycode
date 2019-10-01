/**
 * @author ragulapraveen
 *
 */
package com.payasia.dao;

import com.payasia.dao.bean.PayslipUploadHistory;

/**
 * The Interface PayslipUploadHistoryDAO.
 */

public interface PayslipUploadHistoryDAO {

	/**
	 * Save PayslipUploadHistory Object.
	 * 
	 * @param payslipUploadHistory
	 *            the payslip upload history
	 */
	void save(PayslipUploadHistory payslipUploadHistory);

	/**
	 * New tran save PayslipUploadHistory Object.
	 * 
	 * @param payslipUploadHistory
	 *            the payslip upload history
	 */
	void newTranSave(PayslipUploadHistory payslipUploadHistory);

	/**
	 * Find PayslipUploadHistory Object by companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the payslip upload history
	 */
	PayslipUploadHistory findByCompany(Long companyId);

	PayslipUploadHistory findByCondition(Long companyId, Long monthId,
			Integer year);

}
