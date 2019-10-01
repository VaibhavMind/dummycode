package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.FinancialYearMaster;

/**
 * The Interface FinancialYearMasterDAO.
 */
public interface FinancialYearMasterDAO {

	/**
	 * Save FinancialYearMaster Object.
	 * 
	 * @param financialYearMaster
	 *            the financial year master
	 */
	void saveRole(FinancialYearMaster financialYearMaster);

	/**
	 * Find FinancialYearMaster Object by financialYearId.
	 * 
	 * @param financialYearId
	 *            the financial year id
	 * @return the financial year master
	 */
	FinancialYearMaster findById(long financialYearId);

	/**
	 * Find all FinancialYearMaster Objects List.
	 * 
	 * @return the list
	 */
	List<FinancialYearMaster> findAll();

	/**
	 * Find FinancialYearMaster Object by companyId.
	 * 
	 * @param companyId
	 *            the companyId
	 * @return the financial year master
	 */
	FinancialYearMaster findByCompany(Long companyId);
}
