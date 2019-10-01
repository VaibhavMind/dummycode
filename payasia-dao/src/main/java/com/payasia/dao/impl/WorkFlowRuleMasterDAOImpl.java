package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateWorkflow;
import com.payasia.dao.bean.ClaimTemplateWorkflow_;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeClaimReviewer_;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplate_;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveReviewer_;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveScheme_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow_;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

/**
 * The Class WorkFlowRuleMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class WorkFlowRuleMasterDAOImpl extends BaseDAO implements
		WorkFlowRuleMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		WorkFlowRuleMaster workFlowRuleMaster = new WorkFlowRuleMaster();
		return workFlowRuleMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkFlowRuleMasterDAO#update(com.payasia.dao.bean.
	 * WorkFlowRuleMaster)
	 */
	@Override
	public void update(WorkFlowRuleMaster workFlowRuleMaster) {
		super.update(workFlowRuleMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkFlowRuleMasterDAO#save(com.payasia.dao.bean.
	 * WorkFlowRuleMaster)
	 */
	@Override
	public void save(WorkFlowRuleMaster workFlowRuleMaster) {
		super.save(workFlowRuleMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkFlowRuleMasterDAO#delete(com.payasia.dao.bean.
	 * WorkFlowRuleMaster)
	 */
	@Override
	public void delete(WorkFlowRuleMaster workFlowRuleMaster) {
		super.delete(workFlowRuleMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkFlowRuleMasterDAO#findByID(long)
	 */
	@Override
	public WorkFlowRuleMaster findByID(long workFlowRuleMasterId) {
		return super.findById(WorkFlowRuleMaster.class, workFlowRuleMasterId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkFlowRuleMasterDAO#findAll()
	 */
	@Override
	public List<WorkFlowRuleMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkFlowRuleMaster> criteriaQuery = cb
				.createQuery(WorkFlowRuleMaster.class);
		Root<WorkFlowRuleMaster> workFlowRoot = criteriaQuery
				.from(WorkFlowRuleMaster.class);

		criteriaQuery.select(workFlowRoot);

		TypedQuery<WorkFlowRuleMaster> workFlowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<WorkFlowRuleMaster> workFlowList = workFlowTypedQuery
				.getResultList();
		return workFlowList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.WorkFlowRuleMasterDAO#findByRuleName(java.lang.String)
	 */
	@Override
	public List<WorkFlowRuleMaster> findByRuleName(String ruleName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkFlowRuleMaster> criteriaQuery = cb
				.createQuery(WorkFlowRuleMaster.class);
		Root<WorkFlowRuleMaster> workFlowRoot = criteriaQuery
				.from(WorkFlowRuleMaster.class);

		criteriaQuery.select(workFlowRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				workFlowRoot.get(WorkFlowRuleMaster_.ruleName), ruleName));

		criteriaQuery.orderBy(cb.asc(workFlowRoot
				.get(WorkFlowRuleMaster_.workFlowRuleId)));

		criteriaQuery.where(restriction);
		TypedQuery<WorkFlowRuleMaster> workFlowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkFlowRuleMaster> workFlowList = workFlowTypedQuery
				.getResultList();
		return workFlowList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.WorkFlowRuleMasterDAO#findByRuleNameValue(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public WorkFlowRuleMaster findByRuleNameValue(String ruleName, String value) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkFlowRuleMaster> criteriaQuery = cb
				.createQuery(WorkFlowRuleMaster.class);
		Root<WorkFlowRuleMaster> workFlowRoot = criteriaQuery
				.from(WorkFlowRuleMaster.class);

		criteriaQuery.select(workFlowRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				workFlowRoot.get(WorkFlowRuleMaster_.ruleName), ruleName));

		restriction = cb.and(restriction, cb.equal(
				workFlowRoot.get(WorkFlowRuleMaster_.ruleValue), value));

		criteriaQuery.where(restriction);
		TypedQuery<WorkFlowRuleMaster> workFlowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkFlowRuleMaster> workFlowList = workFlowTypedQuery
				.getResultList();
		if (workFlowList != null &&  !workFlowList.isEmpty()) {
			return workFlowList.get(0);
		}
		return null;
	}

	@Override
	public WorkFlowRuleMaster findByCondition(Long leaveSchemeTypeId,
			String ruleName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkFlowRuleMaster> criteriaQuery = cb
				.createQuery(WorkFlowRuleMaster.class);
		Root<WorkFlowRuleMaster> workFlowRoot = criteriaQuery
				.from(WorkFlowRuleMaster.class);

		criteriaQuery.select(workFlowRoot);

		Join<WorkFlowRuleMaster, LeaveSchemeTypeWorkflow> leaveSchemeWorkflowJoin = workFlowRoot
				.join(WorkFlowRuleMaster_.leaveSchemeTypeWorkflows);

		Join<LeaveSchemeTypeWorkflow, LeaveSchemeType> leaveSchemeTypeJoin = leaveSchemeWorkflowJoin
				.join(LeaveSchemeTypeWorkflow_.leaveSchemeType);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId),
				leaveSchemeTypeId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(workFlowRoot.get(WorkFlowRuleMaster_.ruleName)),
				ruleName.toUpperCase()));

		criteriaQuery.where(restriction);
		TypedQuery<WorkFlowRuleMaster> workFlowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkFlowRuleMaster> workFlowList = workFlowTypedQuery
				.getResultList();
		if (workFlowList != null &&  !workFlowList.isEmpty()) {
			return workFlowList.get(0);
		}
		return null;
	}

	@Override
	public WorkFlowRuleMaster findByClaimTemplateCondition(
			Long claimTemplateId, String ruleName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkFlowRuleMaster> criteriaQuery = cb
				.createQuery(WorkFlowRuleMaster.class);
		Root<WorkFlowRuleMaster> workFlowRoot = criteriaQuery
				.from(WorkFlowRuleMaster.class);

		criteriaQuery.select(workFlowRoot);

		Join<WorkFlowRuleMaster, ClaimTemplateWorkflow> claimSchemeWorkflowJoin = workFlowRoot
				.join(WorkFlowRuleMaster_.claimTemplateWorkflows);

		Join<ClaimTemplateWorkflow, ClaimTemplate> claimTemplateJoin = claimSchemeWorkflowJoin
				.join(ClaimTemplateWorkflow_.claimTemplate);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				claimTemplateJoin.get(ClaimTemplate_.claimTemplateId),
				claimTemplateId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(workFlowRoot.get(WorkFlowRuleMaster_.ruleName)),
				ruleName.toUpperCase()));

		criteriaQuery.where(restriction);
		TypedQuery<WorkFlowRuleMaster> workFlowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkFlowRuleMaster> workFlowList = workFlowTypedQuery
				.getResultList();
		if (workFlowList != null &&  !workFlowList.isEmpty()) {
			return workFlowList.get(0);
		}
		return null;
	}

	@Override
	public Integer findMaxRuleValue(Long companyId, Long leaveSchemeId,
			String ruleName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<WorkFlowRuleMaster> workFlowRoot = criteriaQuery
				.from(WorkFlowRuleMaster.class);

		criteriaQuery.select(cb.max(workFlowRoot.get(
				WorkFlowRuleMaster_.ruleValue).as(Integer.class)));

		Join<WorkFlowRuleMaster, EmployeeLeaveReviewer> empLeaveReviewerJoin = workFlowRoot
				.join(WorkFlowRuleMaster_.employeeLeaveReviewers);

		Join<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveSchemaJoin = empLeaveReviewerJoin
				.join(EmployeeLeaveReviewer_.employeeLeaveScheme);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = employeeLeaveSchemaJoin
				.join(EmployeeLeaveScheme_.leaveScheme);

		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction, cb.equal(
						leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId),
						leaveSchemeId));
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				workFlowRoot.get(WorkFlowRuleMaster_.ruleName), ruleName));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> workFlowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return workFlowTypedQuery.getSingleResult();
	}

	@Override
	public Integer findMaxRuleValueByClaimTemplate(Long companyId,
			Long claimTemplateId, String ruleName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<WorkFlowRuleMaster> workFlowRoot = criteriaQuery
				.from(WorkFlowRuleMaster.class);

		criteriaQuery.select(cb.max(workFlowRoot.get(
				WorkFlowRuleMaster_.ruleValue).as(Integer.class)));

		Join<WorkFlowRuleMaster, EmployeeClaimReviewer> empClaimReviewerJoin = workFlowRoot
				.join(WorkFlowRuleMaster_.employeeClaimReviewers);
		empClaimReviewerJoin.join(EmployeeClaimReviewer_.employee1);

		Join<EmployeeClaimReviewer, EmployeeClaimTemplate> employeeClaimTemplateJoin = empClaimReviewerJoin
				.join(EmployeeClaimReviewer_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = employeeClaimTemplateJoin
				.join(EmployeeClaimTemplate_.claimTemplate);
		Join<ClaimTemplate, Company> claimTemplateCompanyJoin = claimTemplateJoin
				.join(ClaimTemplate_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				claimTemplateJoin.get(ClaimTemplate_.claimTemplateId),
				claimTemplateId));
		restriction = cb.and(restriction, cb.equal(
				claimTemplateCompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				workFlowRoot.get(WorkFlowRuleMaster_.ruleName), ruleName));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> workFlowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return workFlowTypedQuery.getSingleResult();
	}

}
