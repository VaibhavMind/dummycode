package com.payasia.dao;

import com.payasia.dao.bean.CompanyExternalLink;

/**
 * @author vivekjain
 */
/**
 * The Interface CompanyExternalLinkDAO.
 */
public interface CompanyExternalLinkDAO {

	/**
	 * purpose : save CompanyExternalLink Object.
	 * 
	 * @param companyExternalLink
	 *            the company external link
	 * @return AppCodeMaster List
	 */
	void save(CompanyExternalLink companyExternalLink);

	/**
	 * Update CompanyExternalLink Object.
	 * 
	 * @param companyExternalLink
	 *            the company external link
	 */
	void update(CompanyExternalLink companyExternalLink);

	/**
	 * Find CompanyExternalLink Object by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the company external link
	 */
	CompanyExternalLink findByConditionCompany(Long companyId);

}
