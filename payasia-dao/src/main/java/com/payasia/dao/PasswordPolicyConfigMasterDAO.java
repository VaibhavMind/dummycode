package com.payasia.dao;

import com.payasia.dao.bean.PasswordPolicyConfigMaster;

/**
 * The Interface PasswordPolicyConfigMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface PasswordPolicyConfigMasterDAO {

	/**
	 * Save PasswordPolicyConfigMaster Object.
	 * 
	 * @param passwordPolicyConfigMaster
	 *            the password policy config master
	 */
	void save(PasswordPolicyConfigMaster passwordPolicyConfigMaster);

	/**
	 * Update PasswordPolicyConfigMaster Object.
	 * 
	 * @param passwordPolicyConfigMaster
	 *            the password policy config master
	 */
	void update(PasswordPolicyConfigMaster passwordPolicyConfigMaster);

	/**
	 * Find PasswordPolicyConfigMaster Object by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the password policy config master
	 */
	PasswordPolicyConfigMaster findByConditionCompany(Long companyId);

	PasswordPolicyConfigMaster findByConditionCompanyCode(String companyCode);
}
