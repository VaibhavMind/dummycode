package com.payasia.dao;

import com.payasia.dao.bean.EmailPreferenceMaster;

/**
 * The Interface EmailPreferenceMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmailPreferenceMasterDAO {

	/**
	 * Find EmailPreferenceMaster Object by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the email preference master
	 */
	EmailPreferenceMaster findByConditionCompany(Long companyId);

	/**
	 * Save EmailPreferenceMaster Object.
	 * 
	 * @param emailPreferenceMaster
	 *            the email preference master
	 */
	void save(EmailPreferenceMaster emailPreferenceMaster);

	/**
	 * Update EmailPreferenceMaster Object.
	 * 
	 * @param emailPreferenceMaster
	 *            the email preference master
	 */
	void update(EmailPreferenceMaster emailPreferenceMaster);
}
