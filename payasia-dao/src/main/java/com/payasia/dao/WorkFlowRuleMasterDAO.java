package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.WorkFlowRuleMaster;

/**
 * The Interface WorkFlowRuleMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface WorkFlowRuleMasterDAO {

	/**
	 * Find WorkFlowRuleMaster Object by workFlowRuleMasterId.
	 * 
	 * @param workFlowRuleMasterId
	 *            the work flow rule master id
	 * @return the work flow rule master
	 */
	WorkFlowRuleMaster findByID(long workFlowRuleMasterId);

	/**
	 * Delete WorkFlowRuleMaster Object.
	 * 
	 * @param workFlowRuleMaster
	 *            the work flow rule master
	 */
	void delete(WorkFlowRuleMaster workFlowRuleMaster);

	/**
	 * Save WorkFlowRuleMaster Object.
	 * 
	 * @param workFlowRuleMaster
	 *            the work flow rule master
	 */
	void save(WorkFlowRuleMaster workFlowRuleMaster);

	/**
	 * Update WorkFlowRuleMaster Object.
	 * 
	 * @param workFlowRuleMaster
	 *            the work flow rule master
	 */
	void update(WorkFlowRuleMaster workFlowRuleMaster);

	/**
	 * Find all WorkFlowRuleMaster Objects List.
	 * 
	 * @return the list
	 */
	List<WorkFlowRuleMaster> findAll();

	/**
	 * Find WorkFlowRuleMaster Objects List by rule name.
	 * 
	 * @param ruleName
	 *            the rule name
	 * @return the list
	 */
	List<WorkFlowRuleMaster> findByRuleName(String ruleName);

	/**
	 * Find WorkFlowRuleMaster Object by rule name and value.
	 * 
	 * @param ruleName
	 *            the rule name
	 * @param value
	 *            the value
	 * @return the work flow rule master
	 */
	WorkFlowRuleMaster findByRuleNameValue(String ruleName, String value);

	WorkFlowRuleMaster findByCondition(Long leaveSchemeId, String ruleName);

	Integer findMaxRuleValue(Long companyId, Long leaveSchemeId, String ruleName);

	Integer findMaxRuleValueByClaimTemplate(Long companyId,
			Long claimTemplateId, String ruleName);

	WorkFlowRuleMaster findByClaimTemplateCondition(Long claimTemplateId,
			String ruleName);

}
