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

import com.payasia.common.dto.OTReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeOTReviewerDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeOTReviewer;
import com.payasia.dao.bean.EmployeeOTReviewer_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.OTTemplate;
import com.payasia.dao.bean.OTTemplate_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class EmployeeOTReviewerDAOImpl extends BaseDAO implements
		EmployeeOTReviewerDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeOTReviewer employeeOTReviewer = new EmployeeOTReviewer();

		return employeeOTReviewer;
	}

	@Override
	public void update(EmployeeOTReviewer employeeOTReviewer) {
		super.update(employeeOTReviewer);
	}

	@Override
	public void save(EmployeeOTReviewer employeeOTReviewer) {
		super.save(employeeOTReviewer);

	}

	@Override
	public void delete(EmployeeOTReviewer employeeOTReviewer) {
		super.delete(employeeOTReviewer);
	}

	@Override
	public EmployeeOTReviewer findById(long employeeOTReviewerId) {

		EmployeeOTReviewer employeeOTReviewer = super.findById(
				EmployeeOTReviewer.class, employeeOTReviewerId);
		return employeeOTReviewer;
	}

	@Override
	public void deleteByCondition(Long employeeId) {

		String queryString = "DELETE FROM EmployeeOTReviewer e WHERE e.employee1.employeeId = :employee ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);

		q.executeUpdate();
	}

	@Override
	public List<EmployeeOTReviewer> findByCondition(
			OTReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeOTReviewer> criteriaQuery = cb
				.createQuery(EmployeeOTReviewer.class);
		Root<EmployeeOTReviewer> employeeOTReviewerRoot = criteriaQuery
				.from(EmployeeOTReviewer.class);
		criteriaQuery.select(employeeOTReviewerRoot);

		employeeOTReviewerRoot.join(EmployeeOTReviewer_.employee1);

		employeeOTReviewerRoot.join(EmployeeOTReviewer_.employee2);

		Join<EmployeeOTReviewer, OTTemplate> otTemplateJoin = employeeOTReviewerRoot
				.join(EmployeeOTReviewer_.otTemplate);

		employeeOTReviewerRoot.join(EmployeeOTReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						otTemplateJoin.get(OTTemplate_.company)
								.get("companyId").as(Long.class), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeOTReviewer> employeeOTReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			employeeOTReviewerTypedQuery
					.setFirstResult(getStartPosition(pageDTO));
			employeeOTReviewerTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return employeeOTReviewerTypedQuery.getResultList();
	}

	@Override
	public int getCountByCondition(OTReviewerConditionDTO conditionDTO,
			Long companyId) {
		return findByCondition(conditionDTO, null, null, companyId).size();
	}

	@Override
	public List<EmployeeOTReviewer> findByEmployeeIdCompanyId(Long employeeId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeOTReviewer> criteriaQuery = cb
				.createQuery(EmployeeOTReviewer.class);
		Root<EmployeeOTReviewer> employeeOTReviewerRoot = criteriaQuery
				.from(EmployeeOTReviewer.class);
		criteriaQuery.select(employeeOTReviewerRoot);

		Join<EmployeeOTReviewer, Employee> employeeJoin1 = employeeOTReviewerRoot
				.join(EmployeeOTReviewer_.employee1);

		employeeOTReviewerRoot.join(EmployeeOTReviewer_.employee2);

		Join<EmployeeOTReviewer, OTTemplate> otTemplateJoin = employeeOTReviewerRoot
				.join(EmployeeOTReviewer_.otTemplate);

		employeeOTReviewerRoot.join(EmployeeOTReviewer_.workFlowRuleMaster);

		Path<Long> employeeId1 = employeeJoin1.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(
						otTemplateJoin.get(OTTemplate_.company)
								.get("companyId").as(Long.class), companyId));

		restriction = cb.and(restriction, cb.equal(employeeId1, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeOTReviewer> employeeOTReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (employeeOTReviewerTypedQuery.getResultList().size() > 0) {
			return employeeOTReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeOTReviewer> findByOTTemplateIdAndWorkFlowId(
			Long otTemplateId, Long workFlowRuleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeOTReviewer> criteriaQuery = cb
				.createQuery(EmployeeOTReviewer.class);
		Root<EmployeeOTReviewer> employeeOTReviewerRoot = criteriaQuery
				.from(EmployeeOTReviewer.class);
		criteriaQuery.select(employeeOTReviewerRoot);

		Join<EmployeeOTReviewer, OTTemplate> ottemplateJoin = employeeOTReviewerRoot
				.join(EmployeeOTReviewer_.otTemplate);

		Join<EmployeeOTReviewer, WorkFlowRuleMaster> empWorkflowJoin = employeeOTReviewerRoot
				.join(EmployeeOTReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				ottemplateJoin.get(OTTemplate_.otTemplateId), otTemplateId));
		restriction = cb.and(restriction, cb.equal(
				empWorkflowJoin.get(WorkFlowRuleMaster_.workFlowRuleId),
				workFlowRuleId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeOTReviewer> employeeOTReviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeOTReviewer> employeeOTReviewerList = employeeOTReviewerTypedQuery
				.getResultList();
		return employeeOTReviewerList;

	}

}
