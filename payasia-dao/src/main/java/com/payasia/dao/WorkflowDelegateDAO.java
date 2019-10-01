package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.WorkflowDelegateConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.WorkflowDelegate;

/**
 * The Interface WorkflowDelegateDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface WorkflowDelegateDAO {

	/**
	 * Update WorkflowDelegate Object.
	 * 
	 * @param workflowDelegate
	 *            the workflow delegate
	 */
	void update(WorkflowDelegate workflowDelegate);

	/**
	 * Save WorkflowDelegate Object.
	 * 
	 * @param workflowDelegate
	 *            the workflow delegate
	 */
	void save(WorkflowDelegate workflowDelegate);

	/**
	 * Delete WorkflowDelegate Object.
	 * 
	 * @param workflowDelegate
	 *            the workflow delegate
	 */
	void delete(WorkflowDelegate workflowDelegate);

	/**
	 * Find WorkflowDelegate Object by workflowDelegateId.
	 * 
	 * @param workflowDelegateId
	 *            the workflow delegate id
	 * @return the workflow delegate
	 */
	WorkflowDelegate findByID(long workflowDelegateId);

	/**
	 * Find WorkflowDelegate Objects List by condition
	 * WorkflowDelegateConditionDTO and companyId.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<WorkflowDelegate> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, WorkflowDelegateConditionDTO conditionDTO,
			Long companyId);

	/**
	 * Gets the sort path for work flow delegate.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param workflowRoot
	 *            the workflow root
	 * @param workflowEmployeeJoin
	 *            the workflow employee join
	 * @param workflowCompanyJoin
	 *            the workflow company join
	 * @return the sort path for work flow delegate
	 */
	Path<String> getSortPathForWorkFlowDelegate(SortCondition sortDTO,
			Root<WorkflowDelegate> workflowRoot,
			Join<WorkflowDelegate, Employee> workflowEmployeeJoin,
			Join<WorkflowDelegate, Company> workflowCompanyJoin);

	/**
	 * Gets the count for condition WorkflowDelegateConditionDTO and companyId.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the count for condition
	 */
	int getCountForCondition(WorkflowDelegateConditionDTO conditionDTO,
			Long companyId);

	WorkflowDelegate findEmployeeByCurrentDate(Long employeeId, Long appCodeId);

	WorkflowDelegate saveReturn(WorkflowDelegate workflowDelegate);
	
	WorkflowDelegate findByID(long workflowDelegateId,Long companyId);

}
