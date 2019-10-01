package com.payasia.dao;

import com.payasia.dao.bean.CompanyLogo;

/**
 * The Interface CompanyLogoDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface CompanyLogoDAO {

	/**
	 * purpose : Find CompanyLogo Object by companyLogoId.
	 * 
	 * @param long the company logo id
	 * @return the company logo
	 */
	CompanyLogo findById(long companyLogoId);

	/**
	 * purpose : Save CompanyLogo Object.
	 * 
	 * @param companyLogo
	 *            the company logo
	 */
	void save(CompanyLogo companyLogo);

	/**
	 * purpose : Update CompanyLogo Object.
	 * 
	 * @param companyLogo
	 *            the company logo
	 */
	void update(CompanyLogo companyLogo);

	/**
	 * purpose : Find CompanyLogo Object by condition company Id.
	 * 
	 * @param Long
	 *            the company id
	 * @return the company logo
	 */
	CompanyLogo findByConditionCompany(Long companyId);

	/**
	 * purpose : Find CompanyLogo Object by condition company code.
	 * 
	 * @param String
	 *            the company code
	 * @return the company logo
	 */
	CompanyLogo findByConditionCompanyCode(String companyCode);

	CompanyLogo saveReturn(CompanyLogo companyLogo);
}
