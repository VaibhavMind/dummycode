package com.payasia.dao;

import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.SsoConfiguration;

/**
 * The Interface SsoConfigurationDao.
 */
public interface SsoConfigurationDAO {

	/**
	 * Access control.
	 *
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	public SsoConfiguration findByCompanyId(Long companyId);

	/**
	 * Save.
	 *
	 * @param ssoConfiguration
	 *            the sso configuration
	 */
	void save(SsoConfiguration ssoConfiguration);

	/**
	 * Update.
	 *
	 * @param ssoConfiguration
	 *            the sso configuration
	 */
	void update(SsoConfiguration ssoConfiguration);

	/**
	 * Find Company by IDP issuer.
	 *
	 * @param idpIssuer
	 *            the idp issuer
	 * @return the company
	 */
	Company findCompanyByIDPIssuer(String idpIssuer);

	/**
	 * Find by company code.
	 *
	 * @param companyCode
	 *            the company code
	 * @return the sso configuration
	 */

	/**
	 *  Function added to apply Group_Id check for SSO_Configuration in case of Company Group case
	 */
	SsoConfiguration findByCompanyCodeWithGroup(String companyCode);

}
