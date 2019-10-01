package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.WorkflowDelegateConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.AppCodeMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.dao.bean.WorkflowDelegate_;

/**
 * The Class WorkflowDelegateDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class WorkflowDelegateDAOImpl extends BaseDAO implements
		WorkflowDelegateDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		WorkflowDelegate workflowDelegate = new WorkflowDelegate();
		return workflowDelegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkflowDelegateDAO#update(com.payasia.dao.bean.
	 * WorkflowDelegate)
	 */
	@Override
	public void update(WorkflowDelegate workflowDelegate) {
		super.update(workflowDelegate);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkflowDelegateDAO#save(com.payasia.dao.bean.
	 * WorkflowDelegate)
	 */
	@Override
	public void save(WorkflowDelegate workflowDelegate) {
		super.save(workflowDelegate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDAO#saveReturn(com.payasia.dao.bean.Employee)
	 */
	@Override
	public WorkflowDelegate saveReturn(WorkflowDelegate workflowDelegate) {

		WorkflowDelegate persistObj = workflowDelegate;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (WorkflowDelegate) getBaseEntity();
			beanUtil.copyProperties(persistObj, workflowDelegate);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkflowDelegateDAO#delete(com.payasia.dao.bean.
	 * WorkflowDelegate)
	 */
	@Override
	public void delete(WorkflowDelegate workflowDelegate) {
		super.delete(workflowDelegate);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkflowDelegateDAO#findByID(long)
	 */
	@Override
	public WorkflowDelegate findByID(long workflowDelegateId) {
		return super.findById(WorkflowDelegate.class, workflowDelegateId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.WorkflowDelegateDAO#findByCondition(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition,
	 * com.payasia.common.dto.WorkflowDelegateConditionDTO, java.lang.Long)
	 */
	@Override
	public List<WorkflowDelegate> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, WorkflowDelegateConditionDTO conditionDTO,
			Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkflowDelegate> criteriaQuery = cb
				.createQuery(WorkflowDelegate.class);
		Root<WorkflowDelegate> workflowRoot = criteriaQuery
				.from(WorkflowDelegate.class);
		criteriaQuery.select(workflowRoot);

		Join<WorkflowDelegate, Employee> workflowEmployeeJoin = workflowRoot
				.join(WorkflowDelegate_.employee1);
		Join<WorkflowDelegate, Company> workflowCompanyJoin = workflowRoot
				.join(WorkflowDelegate_.company);
		Join<WorkflowDelegate, AppCodeMaster> appcodeJoin = workflowRoot
				.join(WorkflowDelegate_.workflowType);

		Predicate restriction = cb.conjunction();

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction, cb.equal(
					workflowEmployeeJoin.get(Employee_.employeeId),
					conditionDTO.getEmployeeId()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(
					workflowEmployeeJoin.get(Employee_.employeeNumber),
					conditionDTO.getEmployeeNumber()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(workflowEmployeeJoin.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getWorkFlowType())) {

			restriction = cb.and(
					restriction,
					appcodeJoin.get(AppCodeMaster_.appCodeID).in(
							conditionDTO.getAppCodeIds()));
		}

		restriction = cb.and(restriction, cb.equal(
				workflowCompanyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForWorkFlowDelegate(sortDTO,
					workflowRoot, workflowEmployeeJoin, workflowCompanyJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<WorkflowDelegate> workflowDelegateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			workflowDelegateTypedQuery
					.setFirstResult(getStartPosition(pageDTO));
			workflowDelegateTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<WorkflowDelegate> workflowDelegateList = workflowDelegateTypedQuery
				.getResultList();

		return workflowDelegateList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.WorkflowDelegateDAO#getSortPathForWorkFlowDelegate(com
	 * .payasia.common.form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join, javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForWorkFlowDelegate(SortCondition sortDTO,
			Root<WorkflowDelegate> workflowRoot,
			Join<WorkflowDelegate, Employee> workflowEmployeeJoin,
			Join<WorkflowDelegate, Company> workflowCompanyJoin) {

		List<String> workFlowIsColList = new ArrayList<String>();
		workFlowIsColList.add(SortConstants.WORKFLOW_FROM_DATE);
		workFlowIsColList.add(SortConstants.WORKFLOW_TO_DATE);
		workFlowIsColList.add(SortConstants.WORKFLOW_TYPE);

		List<String> empIsColList = new ArrayList<String>();
		empIsColList.add(SortConstants.WORKFLOW_USER);
		empIsColList.add(SortConstants.WORKFLOW_DELEGATEE);

		Path<String> sortPath = null;

		if (workFlowIsColList.contains(sortDTO.getColumnName())) {
			sortPath = workflowRoot.get(colMap.get(WorkflowDelegate.class
					+ sortDTO.getColumnName()));
		}
		if (empIsColList.contains(sortDTO.getColumnName())) {
			sortPath = workflowEmployeeJoin.get(colMap.get(Employee.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.WorkflowDelegateDAO#getCountForCondition(com.payasia.
	 * common.dto.WorkflowDelegateConditionDTO, java.lang.Long)
	 */
	@Override
	public int getCountForCondition(WorkflowDelegateConditionDTO conditionDTO,
			Long companyId) {
		return findByCondition(null, null, conditionDTO, companyId).size();
	}

	@Override
	public WorkflowDelegate findEmployeeByCurrentDate(Long employeeId,
			Long appCodeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkflowDelegate> criteriaQuery = cb
				.createQuery(WorkflowDelegate.class);
		Root<WorkflowDelegate> workflowRoot = criteriaQuery
				.from(WorkflowDelegate.class);
		criteriaQuery.select(workflowRoot);

		Join<WorkflowDelegate, Employee> workflowEmployeeJoin = workflowRoot
				.join(WorkflowDelegate_.employee1);

		Join<WorkflowDelegate, AppCodeMaster> appCodeJoin = workflowRoot
				.join(WorkflowDelegate_.workflowType);

		Predicate restriction = cb.conjunction();
		java.sql.Timestamp stamp = DateUtils.getCurrentTimestamp();

		restriction = cb.and(
				restriction,
				cb.lessThanOrEqualTo(
						workflowRoot.get(WorkflowDelegate_.startDate), stamp));

		restriction = cb.and(
				restriction,
				cb.greaterThanOrEqualTo(
						workflowRoot.get(WorkflowDelegate_.endDate), stamp));

		restriction = cb.and(restriction, cb.equal(
				workflowEmployeeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(appCodeJoin.get(AppCodeMaster_.appCodeID), appCodeId));

		criteriaQuery.where(restriction);

		TypedQuery<WorkflowDelegate> workflowDelegateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkflowDelegate> workflowDelegates = workflowDelegateTypedQuery
				.getResultList();
		if ( !workflowDelegates.isEmpty()) {
			WorkflowDelegate workflowDelegate = workflowDelegates.get(0);
			return workflowDelegate;
		}

		return null;
	}

	@Override
	public WorkflowDelegate findByID(long workflowDelegateId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkflowDelegate> criteriaQuery = cb
				.createQuery(WorkflowDelegate.class);
		Root<WorkflowDelegate> workflowRoot = criteriaQuery
				.from(WorkflowDelegate.class);
		criteriaQuery.select(workflowRoot);

		Predicate restriction = cb.conjunction();
		
        restriction = cb.and(restriction, cb.equal(
        		workflowRoot.get(WorkflowDelegate_.workflowDelegateId), workflowDelegateId));

        restriction = cb.and(restriction, cb.equal(
        		workflowRoot.get(WorkflowDelegate_.company), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<WorkflowDelegate> workflowDelegateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkflowDelegate> workflowDelegates = workflowDelegateTypedQuery
				.getResultList();
		if ( !workflowDelegates.isEmpty()) {
			WorkflowDelegate workflowDelegate = workflowDelegates.get(0);
			return workflowDelegate;
		}

		return null;
	}
}
