package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.EmployeeTimesheetReviewer_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class EmployeeTimesheetReviewerDAOImpl extends BaseDAO implements
		EmployeeTimesheetReviewerDAO {

	@Override
	protected Object getBaseEntity() {

		EmployeeTimesheetReviewer lundinEmployeeTimesheetReviewer = new EmployeeTimesheetReviewer();
		return lundinEmployeeTimesheetReviewer;
	}

	@Override
	public void update(EmployeeTimesheetReviewer lundinEmployeeTimesheetReviewer) {
		super.update(lundinEmployeeTimesheetReviewer);
	}

	@Override
	public void delete(EmployeeTimesheetReviewer lundinEmployeeTimesheetReviewer) {
		super.delete(lundinEmployeeTimesheetReviewer);
	}

	@Override
	public void save(EmployeeTimesheetReviewer lundinEmployeeTimesheetReviewer) {
		super.save(lundinEmployeeTimesheetReviewer);
	}

	@Override
	public EmployeeTimesheetReviewer findById(Long lundinReviewerId) {
		return super
				.findById(EmployeeTimesheetReviewer.class, lundinReviewerId);

	}

	@Override
	public List<EmployeeTimesheetReviewer> findByCondition(Long employeeId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetReviewer> criteriaQuery = cb
				.createQuery(EmployeeTimesheetReviewer.class);
		Root<EmployeeTimesheetReviewer> reviewerRoot = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);
		criteriaQuery.select(reviewerRoot);

		Join<EmployeeTimesheetReviewer, Employee> empJoin = reviewerRoot
				.join(EmployeeTimesheetReviewer_.employee);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));
			
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetReviewer> reviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeTimesheetReviewer> employeeReviewerList = reviewerTypedQuery
				.getResultList();
		return employeeReviewerList;
	}

	@Override
	public void deleteByCondition(Long employeeId) {

		String queryString = "DELETE FROM EmployeeTimesheetReviewer e WHERE e.employee.employeeId = :employee ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);

		q.executeUpdate();
	}

	@Override
	public List<EmployeeTimesheetReviewer> checkEmployeeOTReviewer(
			Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetReviewer> criteriaQuery = cb
				.createQuery(EmployeeTimesheetReviewer.class);
		Root<EmployeeTimesheetReviewer> root = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);
		criteriaQuery.select(root);

		Join<EmployeeTimesheetReviewer, Employee> employeeJoin2 = root
				.join(EmployeeTimesheetReviewer_.employeeReviewer);

		Path<Long> employeeId2 = employeeJoin2.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeId2, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public EmployeeTimesheetReviewer findByWorkFlowCondition(Long employeeId,
			Long workFlowRuleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetReviewer> criteriaQuery = cb
				.createQuery(EmployeeTimesheetReviewer.class);
		Root<EmployeeTimesheetReviewer> root = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);
		criteriaQuery.select(root);

		Join<EmployeeTimesheetReviewer, Employee> employeeJoin = root
				.join(EmployeeTimesheetReviewer_.employee);
		Join<EmployeeTimesheetReviewer, WorkFlowRuleMaster> workFlowRuleJoin = root
				.join(EmployeeTimesheetReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(
				workFlowRuleJoin.get(WorkFlowRuleMaster_.workFlowRuleId),
				workFlowRuleId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeTimesheetReviewer> empOTRevList = typedQuery
				.getResultList();
		if (empOTRevList != null && !empOTRevList.isEmpty()) {
			return empOTRevList.get(0);
		}
		return null;
	}

	@Override
	public EmployeeTimesheetReviewer findByEmployeeId(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetReviewer> criteriaQuery = cb
				.createQuery(EmployeeTimesheetReviewer.class);
		Root<EmployeeTimesheetReviewer> reviewerRoot = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);
		criteriaQuery.select(reviewerRoot);

		Join<EmployeeTimesheetReviewer, Employee> empJoin = reviewerRoot
				.join(EmployeeTimesheetReviewer_.employee);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetReviewer> reviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeTimesheetReviewer> employeeReviewerList = reviewerTypedQuery
				.getResultList();

		if (employeeReviewerList == null || employeeReviewerList.size() == 0) {
			return new EmployeeTimesheetReviewer();
		} else {
			return employeeReviewerList.get(0);
		}

	}

	@Override
	public EmployeeTimesheetReviewer findByReviewerId(Long reviewerId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeTimesheetReviewer> criteriaQuery = cb
				.createQuery(EmployeeTimesheetReviewer.class);
		Root<EmployeeTimesheetReviewer> reviewerRoot = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);
		criteriaQuery.select(reviewerRoot);

		Join<EmployeeTimesheetReviewer, Employee> empJoin = reviewerRoot
				.join(EmployeeTimesheetReviewer_.employeeReviewer);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), reviewerId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeTimesheetReviewer> reviewerTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeTimesheetReviewer> employeeReviewerList = reviewerTypedQuery
				.getResultList();
		return employeeReviewerList.get(0);
	}

	@Override
	public List<Tuple> getEmployeeReviewerList(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeTimesheetReviewer> root = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);

		Join<EmployeeTimesheetReviewer, Employee> employeeJoin2 = root
				.join(EmployeeTimesheetReviewer_.employeeReviewer);
		Join<Employee, Company> companyJoin2 = employeeJoin2
				.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(employeeJoin2.get(Employee_.employeeId).alias(
				getAlias(Employee_.employeeId)));
		selectionItems.add(employeeJoin2.get(Employee_.firstName).alias(
				getAlias(Employee_.firstName)));
		selectionItems.add(employeeJoin2.get(Employee_.lastName).alias(
				getAlias(Employee_.lastName)));
		selectionItems.add(employeeJoin2.get(Employee_.employeeNumber).alias(
				getAlias(Employee_.employeeNumber)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin2.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<Tuple> getEmployeeListByManager(Long companyId, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeTimesheetReviewer> root = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);

		Join<EmployeeTimesheetReviewer, Employee> employeeJoin = root
				.join(EmployeeTimesheetReviewer_.employee);
		Join<Employee, Company> companyJoin2 = employeeJoin
				.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(employeeJoin.get(Employee_.employeeId).alias(
				getAlias(Employee_.employeeId)));
		selectionItems.add(employeeJoin.get(Employee_.firstName).alias(
				getAlias(Employee_.firstName)));
		selectionItems.add(employeeJoin.get(Employee_.lastName).alias(
				getAlias(Employee_.lastName)));
		selectionItems.add(employeeJoin.get(Employee_.employeeNumber).alias(
				getAlias(Employee_.employeeNumber)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin2.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(
				root.get(EmployeeTimesheetReviewer_.employeeReviewer).get(
						Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<Tuple> getEmployeeIdsTupleForManager(Long companyId,
			Long employeeId, EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeTimesheetReviewer> empRevRoot = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);

		Join<EmployeeTimesheetReviewer, Employee> empRevEmp1Join = empRevRoot
				.join(EmployeeTimesheetReviewer_.employee);

		Join<EmployeeTimesheetReviewer, Employee> empRevEmp2Join = empRevRoot
				.join(EmployeeTimesheetReviewer_.employeeReviewer);

		Join<Employee, Company> emp2CompanyJoin = empRevEmp2Join
				.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeId).alias(
				getAlias(Employee_.employeeId)));

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeNumber).alias(
				getAlias(Employee_.employeeNumber)));
		selectionItems.add(empRevEmp1Join.get(Employee_.firstName).alias(
				getAlias(Employee_.firstName)));
		selectionItems.add(empRevEmp1Join.get(Employee_.lastName).alias(
				getAlias(Employee_.lastName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(empRevEmp2Join.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(emp2CompanyJoin.get(Company_.companyId), companyId));
		if (employeeShortListDTO.getStatus() == null
				|| employeeShortListDTO.getStatus() == false) {
			restriction = cb.and(restriction,
					cb.equal(empRevEmp1Join.get(Employee_.status), true));
		}

		if (employeeShortListDTO.getEmployeeShortList()
				&& employeeShortListDTO.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction,
					empRevEmp2Join.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList()
				&& employeeShortListDTO.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(
					restriction,
					empRevEmp2Join.get(Employee_.employeeId).in(
							employeeShortListDTO.getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Tuple> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public List<Tuple> getEmployeeIdsTupleForTimesheetReviewer(
			EmployeeConditionDTO conditionDTO, Long companyId, Long employeeId,
			EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeTimesheetReviewer> empRevRoot = criteriaQuery
				.from(EmployeeTimesheetReviewer.class);

		Join<EmployeeTimesheetReviewer, Employee> empRevEmp1Join = empRevRoot
				.join(EmployeeTimesheetReviewer_.employee);

		Join<EmployeeTimesheetReviewer, Employee> empRevEmp2Join = empRevRoot
				.join(EmployeeTimesheetReviewer_.employeeReviewer);

		Join<Employee, Company> emp2CompanyJoin = empRevEmp2Join
				.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeId).alias(
				getAlias(Employee_.employeeId)));

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeNumber).alias(
				getAlias(Employee_.employeeNumber)));
		selectionItems.add(empRevEmp1Join.get(Employee_.firstName).alias(
				getAlias(Employee_.firstName)));
		selectionItems.add(empRevEmp1Join.get(Employee_.lastName).alias(
				getAlias(Employee_.lastName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		// restriction = cb.or(
		// cb.equal(empRevEmp2Join.get(Employee_.employeeId), employeeId),
		// cb.equal(empRevEmp1Join.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(empRevEmp2Join.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(emp2CompanyJoin.get(Company_.companyId), companyId));

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(empRevEmp1Join.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(empRevEmp1Join.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(empRevEmp1Join.get(Employee_.lastName)),
					conditionDTO.getLastName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(empRevEmp1Join.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction, cb.like(
					cb.upper(empRevEmp1Join.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO() != null) {
			if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& conditionDTO.getEmployeeShortListDTO()
							.getShortListEmployeeIds().isEmpty()) {

				restriction = cb.and(restriction,
						empRevEmp1Join.get("employeeId").in(-1));

			} else if (conditionDTO.getEmployeeShortListDTO()
					.getEmployeeShortList()
					&& !conditionDTO.getEmployeeShortListDTO()
							.getShortListEmployeeIds().isEmpty()) {
				restriction = cb.and(
						restriction,
						empRevEmp1Join.get("employeeId").in(
								conditionDTO.getEmployeeShortListDTO()
										.getShortListEmployeeIds()));
			}
		}

		if (conditionDTO.getStatus() == null
				|| conditionDTO.getStatus() == false) {
			restriction = cb.and(restriction,
					cb.equal(empRevEmp1Join.get(Employee_.status), true));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Tuple> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	/**
	 * @author sheetalagarwal
	 * @param : This method is used for deleteByCondition
	*/
	@Override
	public void deleteByCondition(Long employeeId, Long companyId) {
		String queryString = "DELETE FROM EmployeeTimesheetReviewer e WHERE e.employee.employeeId = :employee and  e.employee.companyId = :company";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);
		q.setParameter("company", companyId);
		q.executeUpdate();
		
	}

	

}
