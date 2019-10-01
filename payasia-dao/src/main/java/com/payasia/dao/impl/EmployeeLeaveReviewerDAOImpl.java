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
import com.payasia.common.dto.LeaveReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveReviewer_;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeType_;
import com.payasia.dao.bean.EmployeeLeaveScheme_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplication_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveSchemeWorkflow;
import com.payasia.dao.bean.LeaveSchemeWorkflow_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class EmployeeLeaveReviewerDAOImpl extends BaseDAO implements EmployeeLeaveReviewerDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeLeaveReviewer employeeLeaveReviewer = new EmployeeLeaveReviewer();
		return employeeLeaveReviewer;

	}

	@Override
	public void update(EmployeeLeaveReviewer employeeLeaveReviewer) {
		super.update(employeeLeaveReviewer);
	}

	@Override
	public void save(EmployeeLeaveReviewer employeeLeaveReviewer) {
		super.save(employeeLeaveReviewer);

	}

	@Override
	public void delete(EmployeeLeaveReviewer employeeLeaveReviewer) {
		super.delete(employeeLeaveReviewer);
	}

	@Override
	public EmployeeLeaveReviewer findById(long employeeLeaveReviewerId) {

		EmployeeLeaveReviewer employeeLeaveReviewer = super.findById(EmployeeLeaveReviewer.class, employeeLeaveReviewerId);
		return employeeLeaveReviewer;
	}

	@Override
	public List<EmployeeLeaveReviewer> findByCondition(LeaveReviewerConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee1);

		employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee2);

		Join<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveSchemaJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveScheme);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = employeeLeaveSchemaJoin.join(EmployeeLeaveScheme_.leaveScheme);

		employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.company).get("companyId").as(Long.class), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			employeeLeaveReviewerTypedQuery.setFirstResult(getStartPosition(pageDTO));
			employeeLeaveReviewerTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return employeeLeaveReviewerTypedQuery.getResultList();
	}

	@Override
	public Long getCountByCondition(LeaveReviewerConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(cb.count(employeeLeaveReviewerRoot));

		employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee1);

		employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee2);

		Join<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveSchemaJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveScheme);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = employeeLeaveSchemaJoin.join(EmployeeLeaveScheme_.leaveScheme);

		employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.company).get("companyId").as(Long.class), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return employeeLeaveReviewerTypedQuery.getSingleResult();
	}

	@Override
	public List<EmployeeLeaveReviewer> findByCondition(Long employeeId, Long employeeLeaveSchemeId, Long employeeLeaveSchemeTypeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, Employee> employeeJoin1 = employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee1);

		Join<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveSchemaJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveScheme);

		Join<EmployeeLeaveReviewer, EmployeeLeaveSchemeType> employeeLeaveSchemTypeJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveSchemeType);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = employeeLeaveSchemaJoin.join(EmployeeLeaveScheme_.leaveScheme);

		Path<Long> employeeId1 = employeeJoin1.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.company).get("companyId").as(Long.class), companyId));
		restriction = cb.and(restriction, cb.equal(employeeLeaveSchemaJoin.get(EmployeeLeaveScheme_.employeeLeaveSchemeId), employeeLeaveSchemeId));
		restriction = cb.and(restriction,
				cb.equal(employeeLeaveSchemTypeJoin.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId), employeeLeaveSchemeTypeId));
		restriction = cb.and(restriction, cb.equal(employeeId1, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (employeeLeaveReviewerTypedQuery.getResultList().size() > 0) {
			return employeeLeaveReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeLeaveReviewer> findByLeaveSchemeId(Long leaveSchemeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveSchemaJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveScheme);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = employeeLeaveSchemaJoin.join(EmployeeLeaveScheme_.leaveScheme);

		Join<LeaveScheme, LeaveSchemeWorkflow> leaveSchemeWorkflowJoin = leaveSchemeJoin.join(LeaveScheme_.leaveSchemeWorkflows);

		Join<LeaveSchemeWorkflow, LeaveScheme> leaveSchemeWorkflowLeaveSchmJoin = leaveSchemeWorkflowJoin.join(LeaveSchemeWorkflow_.leaveScheme);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId), leaveSchemeId));
		restriction = cb.and(restriction, cb.equal(leaveSchemeWorkflowLeaveSchmJoin.get(LeaveScheme_.leaveSchemeId), leaveSchemeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeLeaveReviewer> empLeaveReviewerList = employeeLeaveReviewerTypedQuery.getResultList();
		return empLeaveReviewerList;
	}

	@Override
	public List<EmployeeLeaveReviewer> findByLeaveSchemeTypeId(Long leaveSchemeTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId), leaveSchemeTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeLeaveReviewer> empLeaveReviewerList = employeeLeaveReviewerTypedQuery.getResultList();
		return empLeaveReviewerList;
	}

	@Override
	public List<EmployeeLeaveReviewer> findByEmployeeLeaveSchemeID(Long employeeLeaveSchemeTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, EmployeeLeaveSchemeType> employeeLeaveJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveSchemeType);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeLeaveJoin.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId), employeeLeaveSchemeTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeLeaveReviewer> empLeaveReviewerList = employeeLeaveReviewerTypedQuery.getResultList();
		return empLeaveReviewerList;
	}

	@Override
	public List<EmployeeLeaveReviewer> findByLeaveSchemeIdAndWorkFlowId(Long leaveSchemeId, Long workFlowRuleId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveSchemaJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveScheme);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = employeeLeaveSchemaJoin.join(EmployeeLeaveScheme_.leaveScheme);

		Join<EmployeeLeaveReviewer, WorkFlowRuleMaster> empWorkflowJoin = employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId), leaveSchemeId));
		restriction = cb.and(restriction, cb.equal(empWorkflowJoin.get(WorkFlowRuleMaster_.workFlowRuleId), workFlowRuleId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeLeaveReviewer> employeeLeaveReviewerList = employeeLeaveReviewerTypedQuery.getResultList();
		return employeeLeaveReviewerList;

	}

	@Override
	public void deleteByCondition(Long employeeId, Long employeeLeaveSchemeId, Long employeeLeaveSchemeTypeId) {

		String queryString = "DELETE FROM EmployeeLeaveReviewer e WHERE e.employee1.employeeId = :employee AND e.employeeLeaveScheme.employeeLeaveSchemeId = :employeeLeaveSchemeId AND e.employeeLeaveSchemeType.employeeLeaveSchemeTypeId = :employeeLeaveSchemeTypeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);
		q.setParameter("employeeLeaveSchemeId", employeeLeaveSchemeId);
		q.setParameter("employeeLeaveSchemeTypeId", employeeLeaveSchemeTypeId);

		q.executeUpdate();
	}

	@Override
	public void deleteByConditionEmpId(Long employeeId, Long employeeLeaveSchemeId) {

		String queryString = "DELETE FROM EmployeeLeaveReviewer e WHERE e.employee1.employeeId = :employee AND e.employeeLeaveScheme.employeeLeaveSchemeId = :employeeLeaveSchemeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);
		q.setParameter("employeeLeaveSchemeId", employeeLeaveSchemeId);

		q.executeUpdate();
	}

	@Override
	public List<EmployeeLeaveReviewer> findByEmpLeaveSchemeAndLeaveTypeId(Long employeeId, Long empLeaveSchemeId, Long empLeaveSchemeTypeId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, Employee> employeeJoin1 = employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee1);

		employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee2);

		Join<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveSchemaJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveScheme);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = employeeLeaveSchemaJoin.join(EmployeeLeaveScheme_.leaveScheme);

		Join<EmployeeLeaveReviewer, EmployeeLeaveSchemeType> empLeaveSchemeTypeJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveSchemeType);

		employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.workFlowRuleMaster);

		Path<Long> employeeId1 = employeeJoin1.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.company).get("companyId").as(Long.class), companyId));

		restriction = cb.and(restriction, cb.equal(employeeId1, employeeId));
		restriction = cb.and(restriction, cb.equal(employeeLeaveSchemaJoin.get(EmployeeLeaveScheme_.employeeLeaveSchemeId), empLeaveSchemeId));
		restriction = cb.and(restriction, cb.equal(empLeaveSchemeTypeJoin.get(EmployeeLeaveSchemeType_.employeeLeaveSchemeTypeId), empLeaveSchemeTypeId));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (employeeLeaveReviewerTypedQuery.getResultList().size() > 0) {
			return employeeLeaveReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeLeaveReviewer> checkEmployeeReviewer(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, Employee> employeeJoin2 = employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee2);

		Path<Long> employeeId2 = employeeJoin2.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeId2, employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (employeeLeaveReviewerTypedQuery.getResultList().size() > 0) {
			return employeeLeaveReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeLeaveReviewer> findByEmpLeaveSchemeId(Long employeeLeaveSchemeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, EmployeeLeaveScheme> employeeLeaveSchemaJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveScheme);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeLeaveSchemaJoin.get(EmployeeLeaveScheme_.employeeLeaveSchemeId), employeeLeaveSchemeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> employeeLeaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (employeeLeaveReviewerTypedQuery.getResultList().size() > 0) {
			return employeeLeaveReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeLeaveReviewer> getEmployeeIdsForLeaveReviewer(String searchString, Long companyId, Long employeeId,
			EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> empRevRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(empRevRoot);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp1Join = empRevRoot.join(EmployeeLeaveReviewer_.employee1);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp2Join = empRevRoot.join(EmployeeLeaveReviewer_.employee2);

		Join<Employee, Company> emp2CompanyJoin = empRevEmp2Join.join(Employee_.company);
		empRevEmp1Join.join(Employee_.company);
		Predicate restriction = cb.conjunction();
		if (StringUtils.isNotBlank(searchString.trim())) {

			restriction = cb.and(restriction, cb.like(empRevEmp1Join.get(Employee_.employeeNumber), searchString.trim() + "%"));

		}
		restriction = cb.and(restriction, cb.equal(empRevEmp2Join.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(emp2CompanyJoin.get(Company_.companyId), companyId));

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, empRevEmp2Join.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, empRevEmp2Join.get(Employee_.employeeId).in(employeeShortListDTO.getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeLeaveReviewer> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public List<Tuple> getEmployeeIdsTupleForLeaveReviewer(String searchString, Long companyId, Long employeeId,
			EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeLeaveReviewer> empRevRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp1Join = empRevRoot.join(EmployeeLeaveReviewer_.employee1);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp2Join = empRevRoot.join(EmployeeLeaveReviewer_.employee2);

		Join<Employee, Company> emp2CompanyJoin = empRevEmp2Join.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeId).alias(getAlias(Employee_.employeeId)));

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeNumber).alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empRevEmp1Join.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empRevEmp1Join.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		if (StringUtils.isNotEmpty(searchString)) {

			restriction = cb.and(restriction, cb.like(empRevEmp1Join.get(Employee_.employeeNumber), searchString.trim() + "%"));

		}
		restriction = cb.and(restriction, cb.equal(empRevEmp2Join.get(Employee_.employeeId), employeeId));
		if(companyId != null) {
			restriction = cb.and(restriction, cb.equal(emp2CompanyJoin.get(Company_.companyId), companyId));
		}
		restriction = cb.and(restriction, cb.equal(empRevEmp1Join.get(Employee_.status), true));
		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, empRevEmp2Join.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, empRevEmp2Join.get(Employee_.employeeId).in(employeeShortListDTO.getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Tuple> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public Tuple getEmployeeIdTupleForManager(String employeeNumber, Long companyId, Long employeeId, EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeLeaveReviewer> empRevRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp1Join = empRevRoot.join(EmployeeLeaveReviewer_.employee1);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp2Join = empRevRoot.join(EmployeeLeaveReviewer_.employee2);

		Join<Employee, Company> emp2CompanyJoin = empRevEmp2Join.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeId).alias(getAlias(Employee_.employeeId)));

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeNumber).alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empRevEmp1Join.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empRevEmp1Join.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		if (StringUtils.isNotBlank(employeeNumber)) {

			restriction = cb.and(restriction, cb.equal(empRevEmp1Join.get(Employee_.employeeNumber), employeeNumber));

		}
		restriction = cb.and(restriction, cb.equal(empRevEmp2Join.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(emp2CompanyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(empRevEmp1Join.get(Employee_.status), true));
		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, empRevEmp2Join.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, empRevEmp2Join.get(Employee_.employeeId).in(employeeShortListDTO.getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Tuple> employeeList = empTypedQuery.getResultList();
		if (!employeeList.isEmpty()) {
			return employeeList.get(0);
		}

		return null;
	}

	@Override
	public List<Tuple> findEmpsByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long employeeId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeLeaveReviewer> empRevRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp1Join = empRevRoot.join(EmployeeLeaveReviewer_.employee1);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp2Join = empRevRoot.join(EmployeeLeaveReviewer_.employee2);

		Join<Employee, Company> emp2CompanyJoin = empRevEmp2Join.join(Employee_.company);
		empRevEmp1Join.join(Employee_.company);
		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeId).alias(getAlias(Employee_.employeeId)));

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeNumber).alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empRevEmp1Join.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empRevEmp1Join.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRevEmp1Join.get(Employee_.employeeNumber)), conditionDTO.getEmployeeNumber().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRevEmp1Join.get(Employee_.firstName)), conditionDTO.getFirstName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(cb.upper(empRevEmp1Join.get(Employee_.lastName)), conditionDTO.getLastName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(empRevEmp1Join.get(Employee_.firstName)), conditionDTO.getEmployeeName().toUpperCase() + '%'));

			restriction = cb.or(restriction,
					cb.like(cb.upper(empRevEmp1Join.get(Employee_.lastName)), conditionDTO.getEmployeeName().toUpperCase() + '%'));
		}

		if (conditionDTO.getEmployeeShortListDTO() != null) {
			if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() == 0) {

				restriction = cb.and(restriction, empRevEmp2Join.get("employeeId").in(-1));

			} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
					&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() > 0) {
				restriction = cb.and(restriction, empRevEmp2Join.get("employeeId").in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));
			}
		}

		restriction = cb.and(restriction, cb.equal(empRevEmp2Join.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(emp2CompanyJoin.get(Company_.companyId), companyId));
		if (conditionDTO.getStatus() == null || conditionDTO.getStatus() == false) {
			restriction = cb.and(restriction, cb.equal(empRevEmp1Join.get(Employee_.status), true));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Tuple> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public List<Tuple> getEmployeeIdsTupleForManager(Long companyId, Long employeeId, EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeLeaveReviewer> empRevRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp1Join = empRevRoot.join(EmployeeLeaveReviewer_.employee1);

		Join<EmployeeLeaveReviewer, Employee> empRevEmp2Join = empRevRoot.join(EmployeeLeaveReviewer_.employee2);

		Join<Employee, Company> emp2CompanyJoin = empRevEmp2Join.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeId).alias(getAlias(Employee_.employeeId)));

		selectionItems.add(empRevEmp1Join.get(Employee_.employeeNumber).alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(empRevEmp1Join.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(empRevEmp1Join.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(empRevEmp2Join.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(emp2CompanyJoin.get(Company_.companyId), companyId));
		if (employeeShortListDTO.getStatus() == null || employeeShortListDTO.getStatus() == false) {
			restriction = cb.and(restriction, cb.equal(empRevEmp1Join.get(Employee_.status), true));
		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, empRevEmp2Join.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, empRevEmp2Join.get(Employee_.employeeId).in(employeeShortListDTO.getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Tuple> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Tuple> employeeList = empTypedQuery.getResultList();

		return employeeList;
	}

	@Override
	public List<Object[]> getEmployeeReviewersCountByCondition(Long employeeId, Long employeeLeaveSchemeId, Long workFlowRuleId) {

		String queryString = "SELECT elr.employee2.employeeId , count(elr.employee2.employeeId) FROM EmployeeLeaveReviewer elr WHERE elr.employee1.employeeId = :employee AND elr.employeeLeaveScheme.employeeLeaveSchemeId = :employeeLeaveSchemeId AND elr.workFlowRuleMaster.workFlowRuleId = :workFlowRuleId GROUP BY elr.employee2.employeeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employee", employeeId);
		q.setParameter("employeeLeaveSchemeId", employeeLeaveSchemeId);
		q.setParameter("workFlowRuleId", workFlowRuleId);

		return q.getResultList();
	}

	@Override
	public List<Tuple> getEmployeeReviewersList(Long companyId, EmployeeShortListDTO employeeShortListDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);

		Join<EmployeeLeaveReviewer, Employee> employeeJoin1 = employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee1);
		Join<EmployeeLeaveReviewer, Employee> employeeJoin2 = employeeLeaveReviewerRoot.join(EmployeeLeaveReviewer_.employee2);
		Join<Employee, Company> companyJoin = employeeJoin1.join(Employee_.company);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(employeeJoin2.get(Employee_.employeeId).alias(getAlias(Employee_.employeeId)));

		selectionItems.add(employeeJoin2.get(Employee_.employeeNumber).alias(getAlias(Employee_.employeeNumber)));
		selectionItems.add(employeeJoin2.get(Employee_.firstName).alias(getAlias(Employee_.firstName)));
		selectionItems.add(employeeJoin2.get(Employee_.lastName).alias(getAlias(Employee_.lastName)));
		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, employeeJoin2.get(Employee_.employeeId).in(-1));

		} else if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, employeeJoin2.get(Employee_.employeeId).in(employeeShortListDTO.getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(employeeJoin2.get(Employee_.firstName)), cb.asc(employeeJoin2.get(Employee_.lastName)));

		TypedQuery<Tuple> leaveReviewerTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (leaveReviewerTypedQuery.getResultList().size() > 0) {
			return leaveReviewerTypedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<EmployeeLeaveReviewer> findByLeaveApplicationId(Long leaveApplicationId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveReviewer> criteriaQuery = cb.createQuery(EmployeeLeaveReviewer.class);
		Root<EmployeeLeaveReviewer> employeeLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		criteriaQuery.select(employeeLeaveReviewerRoot);

		Join<EmployeeLeaveReviewer, EmployeeLeaveSchemeType> employeeLeaveReviewerJoin = employeeLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveApplication> employeeLeaveSchemeTypeJoin = employeeLeaveReviewerJoin
				.join(EmployeeLeaveSchemeType_.leaveApplications);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeLeaveSchemeTypeJoin.get(LeaveApplication_.leaveApplicationId), leaveApplicationId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveReviewer> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<EmployeeLeaveReviewer> EmployeeLeaveSchemeList = typedQuery.getResultList();
		if (EmployeeLeaveSchemeList != null && !EmployeeLeaveSchemeList.isEmpty()) {
			return EmployeeLeaveSchemeList;
		}
		return null;

	}
	
	/*
		FUNCTION ADDED TO RETRIEVE ALL THE REVIEWERS OF LEAVE MODULE
	 */
	@Override
	public List<Long> getLeaveReviewerList(Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeLeaveReviewer> empLeaveReviewerRoot = criteriaQuery.from(EmployeeLeaveReviewer.class);
		
		Join<EmployeeLeaveReviewer, Employee> employeeLeaveReviewerJoin = empLeaveReviewerRoot
				.join(EmployeeLeaveReviewer_.employee2);
		
		criteriaQuery.multiselect(employeeLeaveReviewerJoin.get(Employee_.employeeId).alias(getAlias(Employee_.employeeId))).distinct(true);

		Predicate restriction = cb.conjunction();
		if(companyId!=null) {
			restriction = cb.and(restriction, cb.equal(empLeaveReviewerRoot.get(EmployeeLeaveReviewer_.companyId), companyId));
		}
		
		criteriaQuery.where(restriction);
		TypedQuery<Long> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Long> employeeReviewerList = typedQuery.getResultList();
		if (employeeReviewerList != null && !employeeReviewerList.isEmpty()) {
			return employeeReviewerList;
		}
		return null;
	}

}
