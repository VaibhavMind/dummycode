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

import com.payasia.common.dto.ClaimReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeClaimReviewerDAO;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeClaimReviewer_;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplate_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class EmployeeClaimReviewerDAOImpl extends BaseDAO implements EmployeeClaimReviewerDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeClaimReviewer employeeClaimReviewer = new EmployeeClaimReviewer();

		return employeeClaimReviewer;
	}

	@Override
	public void update(EmployeeClaimReviewer employeeClaimReviewer) {
		super.update(employeeClaimReviewer);
	}

	@Override
	public void save(EmployeeClaimReviewer employeeClaimReviewer) {
		super.save(employeeClaimReviewer);

	}

	@Override
	public void delete(EmployeeClaimReviewer employeeClaimReviewer) {
		super.delete(employeeClaimReviewer);
	}

	@Override
	public EmployeeClaimReviewer findById(long employeeClaimReviewerId) {

		EmployeeClaimReviewer employeeClaimReviewer = super.findById(EmployeeClaimReviewer.class,
				employeeClaimReviewerId);
		return employeeClaimReviewer;
	}

	@Override
	public void deleteByCondition(Long employeeId, Long employeeClaimTemplateId) {

		String queryString = "DELETE FROM EmployeeClaimReviewer e WHERE e.employee1.employeeId = :employee AND e.employeeClaimTemplate.employeeClaimTemplateId = :employeeClaimTemplateId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);
		q.setParameter("employeeClaimTemplateId", employeeClaimTemplateId);

		q.executeUpdate();
	}

	@Override
	public List<EmployeeClaimReviewer> findByCondition(ClaimReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimReviewer> criteriaQuery = cb.createQuery(EmployeeClaimReviewer.class);
		Root<EmployeeClaimReviewer> employeeClaimReviewerRoot = criteriaQuery.from(EmployeeClaimReviewer.class);
		criteriaQuery.select(employeeClaimReviewerRoot);

		employeeClaimReviewerRoot.join(EmployeeClaimReviewer_.employee1);

		employeeClaimReviewerRoot.join(EmployeeClaimReviewer_.employee2);

		Join<EmployeeClaimReviewer, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = employeeClaimTemplateJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		employeeClaimReviewerRoot.join(EmployeeClaimReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTemplateJoin.get(ClaimTemplate_.company).get("companyId").as(Long.class), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimReviewer> employeeClaimReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			employeeClaimReviewerTypedQuery.setFirstResult(getStartPosition(pageDTO));
			employeeClaimReviewerTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return employeeClaimReviewerTypedQuery.getResultList();
	}

	@Override
	public int getCountByCondition(ClaimReviewerConditionDTO conditionDTO, Long companyId) {
		return findByCondition(conditionDTO, null, null, companyId).size();
	}

	@Override
	public List<EmployeeClaimReviewer> findByEmployeeIdCompanyId(Long employeeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimReviewer> criteriaQuery = cb.createQuery(EmployeeClaimReviewer.class);
		Root<EmployeeClaimReviewer> employeeClaimReviewerRoot = criteriaQuery.from(EmployeeClaimReviewer.class);
		criteriaQuery.select(employeeClaimReviewerRoot);

		Join<EmployeeClaimReviewer, Employee> employeeJoin1 = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employee1);

		employeeClaimReviewerRoot.join(EmployeeClaimReviewer_.employee2);

		Join<EmployeeClaimReviewer, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = employeeClaimTemplateJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		employeeClaimReviewerRoot.join(EmployeeClaimReviewer_.workFlowRuleMaster);

		Path<Long> employeeId1 = employeeJoin1.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTemplateJoin.get(ClaimTemplate_.company).get("companyId").as(Long.class), companyId));

		restriction = cb.and(restriction, cb.equal(employeeId1, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimReviewer> employeeClaimReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (employeeClaimReviewerTypedQuery.getResultList().size() > 0) {
			return employeeClaimReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeClaimReviewer> checkExistingClaimReviewer(Long employeeId, Long companyId,
			Long employeeClaimTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimReviewer> criteriaQuery = cb.createQuery(EmployeeClaimReviewer.class);
		Root<EmployeeClaimReviewer> employeeClaimReviewerRoot = criteriaQuery.from(EmployeeClaimReviewer.class);
		criteriaQuery.select(employeeClaimReviewerRoot);

		Join<EmployeeClaimReviewer, Employee> employeeJoin1 = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employee1);

		Join<EmployeeClaimReviewer, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = employeeClaimTemplateJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Path<Long> employeeId1 = employeeJoin1.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTemplateJoin.get(ClaimTemplate_.company).get("companyId").as(Long.class), companyId));
		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateJoin.get(EmployeeClaimTemplate_.employeeClaimTemplateId),
						employeeClaimTemplateId));

		restriction = cb.and(restriction, cb.equal(employeeId1, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimReviewer> employeeClaimReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (employeeClaimReviewerTypedQuery.getResultList().size() > 0) {
			return employeeClaimReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeClaimReviewer> findByClaimTemplateIdAndWorkFlowId(Long claimTemplateId, Long workFlowRuleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimReviewer> criteriaQuery = cb.createQuery(EmployeeClaimReviewer.class);
		Root<EmployeeClaimReviewer> employeeClaimReviewerRoot = criteriaQuery.from(EmployeeClaimReviewer.class);
		criteriaQuery.select(employeeClaimReviewerRoot);

		Join<EmployeeClaimReviewer, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = employeeClaimTemplateJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Join<EmployeeClaimReviewer, WorkFlowRuleMaster> empWorkflowJoin = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTemplateJoin.get(ClaimTemplate_.claimTemplateId), claimTemplateId));
		restriction = cb.and(restriction,
				cb.equal(empWorkflowJoin.get(WorkFlowRuleMaster_.workFlowRuleId), workFlowRuleId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimReviewer> employeeClaimReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeClaimReviewer> employeeClaimReviewerList = employeeClaimReviewerTypedQuery.getResultList();
		return employeeClaimReviewerList;

	}

	@Override
	public List<EmployeeClaimReviewer> findByClaimTemplateIdAndEmpId(Long employeeClaimTemplateId, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimReviewer> criteriaQuery = cb.createQuery(EmployeeClaimReviewer.class);
		Root<EmployeeClaimReviewer> employeeClaimReviewerRoot = criteriaQuery.from(EmployeeClaimReviewer.class);
		criteriaQuery.select(employeeClaimReviewerRoot);

		Join<EmployeeClaimReviewer, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employeeClaimTemplate);

		Join<EmployeeClaimReviewer, Employee> employeeJoin = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employee1);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateJoin.get(EmployeeClaimTemplate_.employeeClaimTemplateId),
						employeeClaimTemplateId));
		restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateJoin.get(EmployeeClaimTemplate_.employeeClaimTemplateId),
						employeeClaimTemplateId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimReviewer> employeeClaimReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeClaimReviewer> employeeClaimReviewerList = employeeClaimReviewerTypedQuery.getResultList();
		return employeeClaimReviewerList;

	}

	@Override
	public List<EmployeeClaimReviewer> checkEmployeeClaimReviewer(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimReviewer> criteriaQuery = cb.createQuery(EmployeeClaimReviewer.class);
		Root<EmployeeClaimReviewer> employeeClaimReviewerRoot = criteriaQuery.from(EmployeeClaimReviewer.class);
		criteriaQuery.select(employeeClaimReviewerRoot);

		Join<EmployeeClaimReviewer, Employee> employeeJoin2 = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employee2);

		Path<Long> employeeId2 = employeeJoin2.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeId2, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimReviewer> employeeClaimReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (employeeClaimReviewerTypedQuery.getResultList().size() > 0) {
			return employeeClaimReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeClaimReviewer> findByCondition(Long employeeId, Long employeeClaimTemplateId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeClaimReviewer> criteriaQuery = cb.createQuery(EmployeeClaimReviewer.class);
		Root<EmployeeClaimReviewer> employeeClaimReviewerRoot = criteriaQuery.from(EmployeeClaimReviewer.class);
		criteriaQuery.select(employeeClaimReviewerRoot);

		Join<EmployeeClaimReviewer, Employee> employeeJoin1 = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employee1);

		Join<EmployeeClaimReviewer, EmployeeClaimTemplate> employeeClaimTemplateJoin = employeeClaimReviewerRoot
				.join(EmployeeClaimReviewer_.employeeClaimTemplate);

		Join<EmployeeClaimTemplate, ClaimTemplate> claimTemplateJoin = employeeClaimTemplateJoin
				.join(EmployeeClaimTemplate_.claimTemplate);

		Path<Long> employeeId1 = employeeJoin1.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(claimTemplateJoin.get(ClaimTemplate_.company).get("companyId").as(Long.class), companyId));
		restriction = cb.and(restriction,
				cb.equal(employeeClaimTemplateJoin.get(EmployeeClaimTemplate_.employeeClaimTemplateId),
						employeeClaimTemplateId));

		restriction = cb.and(restriction, cb.equal(employeeId1, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeClaimReviewer> employeeClaimReviewerQueryJoin = entityManagerFactory
				.createQuery(criteriaQuery);

		if (employeeClaimReviewerQueryJoin.getResultList().size() > 0) {
			return employeeClaimReviewerQueryJoin.getResultList();
		} else {
			return null;
		}
	}
}
