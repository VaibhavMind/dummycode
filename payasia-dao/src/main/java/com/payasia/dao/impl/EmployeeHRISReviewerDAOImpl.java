package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeHRISReviewerDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHRISReviewer;
import com.payasia.dao.bean.EmployeeHRISReviewer_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class EmployeeHRISReviewerDAOImpl extends BaseDAO implements
		EmployeeHRISReviewerDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeHRISReviewer employeeHRISReviewer = new EmployeeHRISReviewer();
		return employeeHRISReviewer;
	}

	@Override
	public void update(EmployeeHRISReviewer employeeHRISReviewer) {
		super.update(employeeHRISReviewer);
	}

	@Override
	public void delete(EmployeeHRISReviewer employeeHRISReviewer) {
		super.delete(employeeHRISReviewer);
	}

	@Override
	public void save(EmployeeHRISReviewer employeeHRISReviewer) {
		super.save(employeeHRISReviewer);
	}

	@Override
	public EmployeeHRISReviewer findById(Long employeeHRISReviewerId) {
		return super.findById(EmployeeHRISReviewer.class,
				employeeHRISReviewerId);

	}

	@Override
	public List<EmployeeHRISReviewer> findByCondition(Long employeeId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHRISReviewer> criteriaQuery = cb
				.createQuery(EmployeeHRISReviewer.class);
		Root<EmployeeHRISReviewer> reviewerRoot = criteriaQuery
				.from(EmployeeHRISReviewer.class);
		criteriaQuery.select(reviewerRoot);

		Join<EmployeeHRISReviewer, Employee> empJoin = reviewerRoot
				.join(EmployeeHRISReviewer_.employee);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeHRISReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeLeaveReviewerTypedQuery
				.getResultList();
		return employeeHRISReviewerList;
	}

	@Override
	public void deleteByCondition(Long employeeId) {

		String queryString = "DELETE FROM EmployeeHRISReviewer e WHERE e.employee.employeeId = :employee ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);

		q.executeUpdate();
	}

	@Override
	public List<EmployeeHRISReviewer> checkEmployeeHRISReviewer(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHRISReviewer> criteriaQuery = cb
				.createQuery(EmployeeHRISReviewer.class);
		Root<EmployeeHRISReviewer> root = criteriaQuery
				.from(EmployeeHRISReviewer.class);
		criteriaQuery.select(root);

		Join<EmployeeHRISReviewer, Employee> employeeJoin2 = root
				.join(EmployeeHRISReviewer_.employeeReviewer);

		Path<Long> employeeId2 = employeeJoin2.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeId2, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeHRISReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public EmployeeHRISReviewer findByWorkFlowCondition(Long employeeId,
			Long workFlowRuleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHRISReviewer> criteriaQuery = cb
				.createQuery(EmployeeHRISReviewer.class);
		Root<EmployeeHRISReviewer> root = criteriaQuery
				.from(EmployeeHRISReviewer.class);
		criteriaQuery.select(root);

		Join<EmployeeHRISReviewer, Employee> employeeJoin = root
				.join(EmployeeHRISReviewer_.employee);
		Join<EmployeeHRISReviewer, WorkFlowRuleMaster> workFlowRuleJoin = root
				.join(EmployeeHRISReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(
				workFlowRuleJoin.get(WorkFlowRuleMaster_.workFlowRuleId),
				workFlowRuleId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeHRISReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeHRISReviewer> empHrisRevList = typedQuery.getResultList();
		if (empHrisRevList != null &&  !empHrisRevList.isEmpty()) {
			return empHrisRevList.get(0);
		}
		return null;
	}
	
	@Override
	public EmployeeHRISReviewer findById(Long employeeHRISReviewerId,Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHRISReviewer> criteriaQuery = cb
				.createQuery(EmployeeHRISReviewer.class);
		Root<EmployeeHRISReviewer> reviewerRoot = criteriaQuery
				.from(EmployeeHRISReviewer.class);
		criteriaQuery.select(reviewerRoot);

		Join<EmployeeHRISReviewer, Employee> empJoin = reviewerRoot
				.join(EmployeeHRISReviewer_.employee);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeHRISReviewerId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeHRISReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeLeaveReviewerTypedQuery
				.getResultList();
		return employeeHRISReviewerList.get(0);
	}

	@Override
	public void deleteByCondition(Long employeeId, Long companyId) {
		String queryString = "DELETE FROM EmployeeHRISReviewer e WHERE e.employee.employeeId = :employee and e.companyId = :company ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);
		q.setParameter("company", companyId);
        q.executeUpdate();
		
	}

	

}
