package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.OTTemplateWorkflow;

/**
 * The Interface OTTemplateWorkflowDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface OTTemplateWorkflowDAO {

	/**
	 * Find OTTemplateWorkflow Object by otTemplateId and rule name.
	 * 
	 * @param otTemplateId
	 *            the ot template id
	 * @param otReviewerRule
	 *            the ot reviewer rule
	 * @return the oT template workflow
	 */
	OTTemplateWorkflow findByTemplateIdRuleName(Long otTemplateId,
			String otReviewerRule);

	/**
	 * Delete OTTemplateWorkflow Object by condition otTemplateId.
	 * 
	 * @param otTemplateId
	 *            the ot template id
	 */
	void deleteByCondition(Long otTemplateId);

	/**
	 * Find OTTemplateWorkflow Object List by condition otTemplateId.
	 * 
	 * @param otTemplateId
	 *            the ot template id
	 * @return the list
	 */
	List<OTTemplateWorkflow> findByCondition(Long otTemplateId);

	/**
	 * Save OTTemplateWorkflow Object.
	 * 
	 * @param otTemplateWorkflow
	 *            the ot template workflow
	 */
	void save(OTTemplateWorkflow otTemplateWorkflow);

}
