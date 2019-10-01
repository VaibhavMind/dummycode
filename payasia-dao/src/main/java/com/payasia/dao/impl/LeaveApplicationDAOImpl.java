package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AddLeaveConditionDTO;
import com.payasia.common.dto.LeaveBalanceSummaryConditionDTO;
import com.payasia.common.dto.LeaveConditionDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LeaveReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveReviewer_;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeType_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplication_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.LeaveStatusMaster_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;

@Repository
public class LeaveApplicationDAOImpl extends BaseDAO implements LeaveApplicationDAO {

	@Override
	public void save(LeaveApplication leaveApplication) {
		super.save(leaveApplication);
	}

	@Override
	public LeaveApplication saveReturn(LeaveApplication leaveApplication) {
		LeaveApplication persistObj = leaveApplication;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveApplication) getBaseEntity();
			beanUtil.copyProperties(persistObj, leaveApplication);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	protected Object getBaseEntity() {
		LeaveApplication leaveApplication = new LeaveApplication();
		return leaveApplication;
	}

	@Override
	public List<LeaveApplication> findByCondition(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		criteriaQuery.where(restriction);
		if (sortDTO == null || StringUtils.isEmpty(sortDTO.getColumnName())) {
			criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
		}

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	
	
	
	@Override
	public List<LeaveApplication> findByConditionWithDrawnCancel(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		criteriaQuery.where(restriction);
		
		if (sortDTO == null || sortDTO.getColumnName().equals("")) {
			criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
		}

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> findByConditionForRejected(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		criteriaQuery.where(restriction);
		
		if (sortDTO == null || sortDTO.getColumnName().equals("")) {
			criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
		}
		
		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> findByConditionSubmitted(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusNames().size() > 0) {
			restriction = cb.and(restriction,
					leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(conditionDTO.getLeaveStatusNames()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveApplication_.updatedDate)));

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> findByConditionCompletedForCancellation(AddLeaveConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}
		
		restriction = cb.and(restriction,
				cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> findByConditionLeaveCancel(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		criteriaQuery.where(restriction);
		
		if (sortDTO == null || sortDTO.getColumnName().equals("")) {
			criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
		}
		
		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> findByConditionSubmittedLeaveCancel(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);
		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);
		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction, cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusNames().size() > 0) {
			restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(conditionDTO.getLeaveStatusNames()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		criteriaQuery.where(restriction);
	
		if (sortDTO == null || sortDTO.getColumnName().equals("")) {
			criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
		}
		
//		if (sortDTO != null) {
//
//			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot, empLeaveJoin, leaveStatusJoin);
//			if (sortPath != null) {
//				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
//					criteriaQuery.orderBy(cb.asc(sortPath));
//				}
//				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
//					criteriaQuery.orderBy(cb.desc(sortPath));
//				}
//			}
//			 if (sortPath == null) {
//			 criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
//			 }
//		}
		
		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> findEmployeeLeaveTransactions(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long leaveApplicationId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Predicate restriction = cb.conjunction();

		if (leaveApplicationId != null) {
			restriction = cb.and(restriction,
					cb.notEqual((empRoot.get(LeaveApplication_.leaveApplicationId)), leaveApplicationId));
		}
		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class)), conditionDTO.getToDate()));
		}

		restriction = cb.and(restriction,
				cb.equal(empRoot.get(LeaveApplication_.employee).get("employeeId").as(Long.class),
						conditionDTO.getEmployeeId()));
		restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		restriction = cb.and(restriction,
				leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(conditionDTO.getLeaveStatusNames()));

		criteriaQuery.where(restriction);
		
		if (sortDTO == null || sortDTO.getColumnName().equals("")) {
			criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
		}

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public Integer getCountEmployeeLeaveTransactions(AddLeaveConditionDTO conditionDTO, Long leaveApplicationId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Predicate restriction = cb.conjunction();
		if (leaveApplicationId != null) {
			restriction = cb.and(restriction,
					cb.notEqual((empRoot.get(LeaveApplication_.leaveApplicationId)), leaveApplicationId));
		}
		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class)), conditionDTO.getToDate()));
		}

		restriction = cb.and(restriction,
				cb.equal(empRoot.get(LeaveApplication_.employee).get("employeeId").as(Long.class),
						conditionDTO.getEmployeeId()));
		restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		restriction = cb.and(restriction,
				leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(conditionDTO.getLeaveStatusNames()));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public List<LeaveApplication> findEmployeesOnLeave(AddLeaveConditionDTO conditionDTO) {
		String queryString = "select Distinct LA FROM LeaveApplication LA inner join LA.employee emp inner join emp.employeeLeaveReviewers1 ELR WHERE ELR.employee2.employeeId = :reviewerId AND LA.leaveStatusMaster.leaveStatusName in :leaveStatus and LA.leaveApplicationId <> :leaveApplicationId ";
		queryString += "and ((LA.startDate between :startDate and :endDate) or (LA.startDate <= :startDate and ISNULL(LA.endDate,'2999-12-31') >= :endDate ) or (ISNULL(LA.endDate,'2999-12-31') between :startDate and :endDate)  )";

		Query query = entityManagerFactory.createQuery(queryString);
		query.setParameter("reviewerId", conditionDTO.getEmployeeId());
		query.setParameter("leaveStatus", conditionDTO.getLeaveStatusNames());
		query.setParameter("leaveApplicationId", conditionDTO.getLeaveApplicationId());
		query.setParameter("startDate", conditionDTO.getFromDate());
		query.setParameter("endDate", conditionDTO.getToDate());

		List<LeaveApplication> leaveAppList = query.getResultList();
		if (leaveAppList != null && !leaveAppList.isEmpty()) {
			return leaveAppList;
		}
		return null;

	}

	@Override
	public Long getCountForConditionWithdrawnCancel(AddLeaveConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}
		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public Long getCountForConditionRejected(AddLeaveConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public Long getCountForConditionLeaveCancel(AddLeaveConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public Long getCountForConditionSubmittedLeaveCancel(AddLeaveConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusNames().size() > 0) {
			restriction = cb.and(restriction,
					leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(conditionDTO.getLeaveStatusNames()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public LeaveApplication findById(Long leaveApplicationId) {
		return super.findById(LeaveApplication.class, leaveApplicationId);
	}

	@Override
	public void update(LeaveApplication leaveApplication) {
		super.update(leaveApplication);
	}

	@Override
	public void delete(LeaveApplication leaveApplication) {
		super.delete(leaveApplication);
	}

	@Override
	public List<LeaveApplication> findByLeaveStatus(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			List<String> leaveStatusList, PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(leaveAppRoot);

		Predicate restriction = cb.conjunction();
		Join<LeaveApplication, Employee> employeeJoin = leaveAppRoot.join(LeaveApplication_.employee);
		Join<LeaveApplication, Company> companyJoin = leaveAppRoot.join(LeaveApplication_.company);
		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = leaveAppRoot
				.join(LeaveApplication_.leaveStatusMaster);
		restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(leaveStatusList));

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo((leaveAppRoot.get(LeaveApplication_.startDate)),
					conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((leaveAppRoot.get(LeaveApplication_.endDate)), conditionDTO.getToDate()));
		}
		if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId).in(-1));

		} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId)
					.in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}
		restriction = cb.and(restriction, cb.isNull(leaveAppRoot.get(LeaveApplication_.leaveCancelApplication)));
		criteriaQuery.where(restriction);

		if (sortDTO == null || sortDTO.getColumnName().equals("")) {
			criteriaQuery.orderBy(cb.desc(leaveAppRoot.get(LeaveTypeMaster_.createdDate)));
		}

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public Long getCountForCondLeaveStatus(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			List<String> leaveStatusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Company> companyJoin = empRoot.join(LeaveApplication_.company);
		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);
		restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(leaveStatusList));

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction,
					cb.greaterThanOrEqualTo((empRoot.get(LeaveApplication_.startDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.lessThanOrEqualTo((empRoot.get(LeaveApplication_.endDate)), conditionDTO.getToDate()));
		}
		restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public List<LeaveApplication> findByLeaveStatusAndReviewer(LeaveBalanceSummaryConditionDTO conditionDTO,
			Long companyId, List<String> leaveStatusList, Long reviewerId, Long employeeId, PageRequest pageDTO,
			SortCondition sortDTO) {
		    
		    CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
			Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
			criteriaQuery.select(leaveAppRoot);

			Predicate restriction = cb.conjunction();

			Join<LeaveApplication, Employee> employeeJoin = leaveAppRoot.join(LeaveApplication_.employee);

			Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = leaveAppRoot
					.join(LeaveApplication_.leaveStatusMaster);
			restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(leaveStatusList));

			if (conditionDTO.getFromDate() != null) {
				restriction = cb.and(restriction, cb.greaterThanOrEqualTo((leaveAppRoot.get(LeaveApplication_.startDate)),
						conditionDTO.getFromDate()));
			}
			if (conditionDTO.getToDate() != null) {
				restriction = cb.and(restriction,
						cb.lessThanOrEqualTo((leaveAppRoot.get(LeaveApplication_.endDate)), conditionDTO.getToDate()));
			}

			restriction = cb.and(restriction, cb.isNull(leaveAppRoot.get(LeaveApplication_.leaveCancelApplication)));

			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<EmployeeLeaveReviewer> empLeaveRevSubRoot = subquery.from(EmployeeLeaveReviewer.class);
			subquery.select(empLeaveRevSubRoot.get(EmployeeLeaveReviewer_.employee1).get("employeeId").as(Long.class)).distinct(true);

			Predicate subRestriction = cb.conjunction();

			subRestriction = cb.and(subRestriction, cb.or(
					cb.equal(empLeaveRevSubRoot.get(EmployeeLeaveReviewer_.employee2).get("employeeId").as(Long.class),
							reviewerId),
					cb.equal(leaveAppRoot.get(LeaveApplication_.employee).get("employeeId").as(Long.class), employeeId)));

			subquery.where(subRestriction);
			
			restriction = cb.and(restriction, cb.in(employeeJoin.get(Employee_.employeeId)).value(subquery));

			criteriaQuery.where(restriction);

			if (sortDTO != null) {

				Path<String> sortPath = getSortPathForleaveApplication(sortDTO, leaveAppRoot);
				if (sortPath != null) {
					if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
						criteriaQuery.orderBy(cb.asc(sortPath));
					}
					if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
						criteriaQuery.orderBy(cb.desc(sortPath));
					}
				}

			}

			TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
			if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
				leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
				leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
			}

			return leaveAppTypedQuery.getResultList();}

	@Override
	public Integer getCountForCondLeaveStatusAndReviewer(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			Long reviewerId, Long employeeId, List<String> leaveStatusList) {

		return findByLeaveStatusAndReviewer(conditionDTO, companyId, leaveStatusList, reviewerId, employeeId, null,
				null).size();

	}

	@Override
	public Path<String> getSortPathForleaveApplication(SortCondition sortDTO, Root<LeaveApplication> leaveAppRoot) {

		Join<LeaveApplication, Employee> reviewerEmpJoin = leaveAppRoot.join(LeaveApplication_.employee);
		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = leaveAppRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		List<String> leaveAppIsColList = new ArrayList<String>();
		leaveAppIsColList.add(SortConstants.START_DATE);
		leaveAppIsColList.add(SortConstants.END_DATE);
		leaveAppIsColList.add(SortConstants.LEAVE_APPLICATION_NO_OF_DAYS);

//		List<String> leaveReviewerIsColList = new ArrayList<String>();
//		leaveReviewerIsColList.add(SortConstants.EMPLOYEE_NAME);
//		leaveReviewerIsColList.add(SortConstants.EMPLOYEE_NUMBER);

//		List<String> leaveTypeIsColList = new ArrayList<String>();
//		leaveTypeIsColList.add(SortConstants.LEAVE_REVIEWER_LEAVE_TYPE);

		Path<String> sortPath = null;

		if (leaveAppIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveAppRoot.get(colMap.get(LeaveApplication.class + sortDTO.getColumnName()));
		}
//		if (leaveReviewerIsColList.contains(sortDTO.getColumnName())) {
//			sortPath = reviewerEmpJoin.get(colMap.get(Employee.class + sortDTO.getColumnName()));
//		}
//		if (leaveTypeIsColList.contains(sortDTO.getColumnName())) {
//			sortPath = leaveTypeJoin.get(colMap.get(LeaveTypeMaster.class + sortDTO.getColumnName()));
//		}
		return sortPath;
	}

	@Override
	public List<LeaveApplication> findByYearAndMonth(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			Long employeeId, Long reviewerId, List<String> leaveStatusList, String year, String month) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(leaveAppRoot).distinct(true);

		Predicate restriction = cb.conjunction();
		Join<LeaveApplication, Employee> employeeJoin = leaveAppRoot.join(LeaveApplication_.employee);
		Join<LeaveApplication, Company> companyJoin = leaveAppRoot.join(LeaveApplication_.company);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = leaveAppRoot
				.join(LeaveApplication_.leaveStatusMaster);
		if (employeeId != null) {
			restriction = cb.and(restriction, cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		}

		if (reviewerId != null) {
			Join<Employee, EmployeeLeaveReviewer> reviewerEmpJoin = employeeJoin
					.join(Employee_.employeeLeaveReviewers1);
			restriction = cb.and(restriction,
					cb.equal(reviewerEmpJoin.get(EmployeeLeaveReviewer_.employee2).get("employeeId").as(Long.class),
							reviewerId));
		}
		restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(leaveStatusList));

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		Predicate yearRestriction = cb.or(
				cb.equal(cb.function("year", Integer.class, leaveAppRoot.get(LeaveApplication_.startDate)),
						Integer.parseInt(year)),
				cb.equal(cb.function("year", Integer.class, leaveAppRoot.get(LeaveApplication_.endDate)),
						Integer.parseInt(year)));
		
		restriction =cb.and(restriction,yearRestriction);
		
		Predicate monthRestriction = cb.or(
				cb.equal(cb.function("month", Integer.class, leaveAppRoot.get(LeaveApplication_.startDate)),
						Integer.parseInt(month)),
				cb.equal(cb.function("month", Integer.class, leaveAppRoot.get(LeaveApplication_.endDate)),
						Integer.parseInt(month)));

		restriction = cb.and(restriction, monthRestriction);

		if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId).in(-1));

		} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId)
					.in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> findByYearAndMonthForManager(LeaveBalanceSummaryConditionDTO conditionDTO,
			Long companyId, Long reviewerId, List<String> leaveStatusList, String year, String month) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(leaveAppRoot).distinct(true);

		Predicate restriction = cb.conjunction();
		Join<LeaveApplication, Employee> employeeJoin = leaveAppRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = leaveAppRoot
				.join(LeaveApplication_.leaveStatusMaster);

		if (reviewerId != null) {
			Join<Employee, EmployeeLeaveReviewer> reviewerEmpJoin = employeeJoin
					.join(Employee_.employeeLeaveReviewers1);
			Join<EmployeeLeaveReviewer, Employee> empJoin = reviewerEmpJoin.join(EmployeeLeaveReviewer_.employee2);
			restriction = cb.and(restriction,
					cb.equal(reviewerEmpJoin.get(EmployeeLeaveReviewer_.employee2).get("employeeId").as(Long.class),
							reviewerId));
			restriction = cb.and(restriction,
					cb.equal(empJoin.get(Employee_.company).get("companyId").as(Long.class), companyId));
		}
		restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(leaveStatusList));

		restriction = cb.and(restriction,
				cb.equal(cb.function("year", Integer.class, leaveAppRoot.get(LeaveApplication_.startDate)),
						Integer.parseInt(year)));
		restriction = cb.and(restriction,
				cb.equal(cb.function("year", Integer.class, leaveAppRoot.get(LeaveApplication_.endDate)),
						Integer.parseInt(year)));

		Predicate monthRestriction = cb.or(
				cb.equal(cb.function("month", Integer.class, leaveAppRoot.get(LeaveApplication_.startDate)),
						Integer.parseInt(month)),
				cb.equal(cb.function("month", Integer.class, leaveAppRoot.get(LeaveApplication_.endDate)),
						Integer.parseInt(month)));

		restriction = cb.and(restriction, monthRestriction);

		if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() == 0) {

			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId).in(-1));

		} else if (conditionDTO.getEmployeeShortListDTO().getEmployeeShortList()
				&& conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds().size() > 0) {
			restriction = cb.and(restriction, employeeJoin.get(Employee_.employeeId)
					.in(conditionDTO.getEmployeeShortListDTO().getShortListEmployeeIds()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> findByLeaveAppIds(Long companyId, ArrayList<Long> leaveAppIdList, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(leaveAppRoot);

		Predicate restriction = cb.conjunction();
		leaveAppRoot.join(LeaveApplication_.employee);
		leaveAppRoot.join(LeaveApplication_.company);

		restriction = cb.and(restriction, leaveAppRoot.get(LeaveApplication_.leaveApplicationId).in(leaveAppIdList));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForleaveApplication(sortDTO, leaveAppRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public Long getCountLeaveAppIds(Long companyId, ArrayList<Long> leaveAppIdList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(leaveAppRoot));

		Predicate restriction = cb.conjunction();
		leaveAppRoot.join(LeaveApplication_.employee);
		leaveAppRoot.join(LeaveApplication_.company);

		restriction = cb.and(restriction, leaveAppRoot.get(LeaveApplication_.leaveApplicationId).in(leaveAppIdList));
		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDAO#findAssignCompanyToUser(com.payasia.common
	 * .form.SortCondition, com.payasia.common.form.PageRequest, java.lang.Long)
	 */
	@Override
	public List<Tuple> findLeaveCancellationIds(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<LeaveApplication> leaveApplicationRoot = criteriaQuery.from(LeaveApplication.class);

		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems.add(leaveApplicationRoot.get(LeaveApplication_.leaveCancelApplication).get("leaveApplicationId")
				.as(Long.class).alias("Cancellation_Id"));

		criteriaQuery.multiselect(selectionItems).distinct(true);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.isNotNull(leaveApplicationRoot.get(LeaveApplication_.leaveCancelApplication)));
		criteriaQuery.where(restriction);
		TypedQuery<Tuple> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<Tuple> leaveApplicationList = typedQuery.getResultList();
		return leaveApplicationList;
	}

	@Override
	public LeaveDTO validateLeaveApplication(final LeaveConditionDTO leaveConditionDTO) {

		final LeaveDTO lDTO = new LeaveDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection.prepareCall("{call Validate_Leave_Application (?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

					cstmt.setLong("@Employee_ID", leaveConditionDTO.getEmployeeId());
					cstmt.setLong("@Employee_Leave_Scheme_Type_ID", leaveConditionDTO.getEmployeeLeaveSchemeTypeId());

					if (leaveConditionDTO.getEmployeeLeaveApplicationId() == null) {
						cstmt.setNull("@Employee_Leave_Application_ID", java.sql.Types.BIGINT);
					} else {
						cstmt.setLong("@Employee_Leave_Application_ID",
								leaveConditionDTO.getEmployeeLeaveApplicationId());
					}
					if (leaveConditionDTO.getDateFormat() != null) {

						cstmt.setTimestamp("@Leave_Start_Date", DateUtils.stringToTimestamp(
								leaveConditionDTO.getStartDate(), leaveConditionDTO.getDateFormat()));
						cstmt.setTimestamp("@Leave_End_Date", DateUtils
								.stringToTimestamp(leaveConditionDTO.getEndDate(), leaveConditionDTO.getDateFormat()));

					} else {
						cstmt.setTimestamp("@Leave_Start_Date",
								DateUtils.stringToTimestamp(leaveConditionDTO.getStartDate()));
						cstmt.setTimestamp("@Leave_End_Date",
								DateUtils.stringToTimestamp(leaveConditionDTO.getEndDate()));
					}

					cstmt.setLong("@Leave_Start_Session_ID", leaveConditionDTO.getStartSession());
					cstmt.setLong("@Leave_End_Session_ID", leaveConditionDTO.getEndSession());
					cstmt.setBoolean("@Has_Attachment", leaveConditionDTO.isAttachementStatus());
					cstmt.setBoolean("@Is_Post", leaveConditionDTO.isPost());
					cstmt.setBoolean("@Is_Reviewer", leaveConditionDTO.isReviewer());
					cstmt.setBigDecimal("@Hours_Between_Dates",
							new BigDecimal(leaveConditionDTO.getTotalHoursBetweenDates()));

					cstmt.registerOutParameter("@Error_Code", java.sql.Types.INTEGER);
					cstmt.registerOutParameter("@Error_Key", java.sql.Types.VARCHAR);
					cstmt.registerOutParameter("@Error_Value", java.sql.Types.VARCHAR);

					cstmt.execute();
					Integer errorCode = cstmt.getInt("@Error_Code");
					String errorKey = cstmt.getString("@Error_Key");
					String errorValue = cstmt.getString("@Error_Value");

					StringBuilder errorKeyFinalStr = new StringBuilder();
					// Add Hours to error key to change "Leave In Hours" keys
					if (leaveConditionDTO.isLeaveUnitHours()) {
						if (StringUtils.isNotBlank(errorKey)) {
							String[] errorKeyArr = errorKey.split(";");
							for (int count = 0; count < errorKeyArr.length; count++) {
								if (StringUtils.isNotBlank(errorKeyArr[count])) {
									errorKeyFinalStr.append(errorKeyArr[count] + "_Hours;");
								}
							}
						}
					} else {
						errorKeyFinalStr.append(errorKey);
					}

					lDTO.setErrorCode(errorCode);
					lDTO.setErrorKey(errorKeyFinalStr.toString());
					lDTO.setErrorValue(errorValue);
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}
		});

		return lDTO;
	}

	@Override
	public List<LeaveApplication> findByLeaveStatus(List<String> leaveStatusNames, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, Company> companyJoin = empRoot.join(LeaveApplication_.company);

		Path<String> leaveStatusName = leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName);

		if (!leaveStatusNames.isEmpty()) {

			restriction = cb.and(restriction, leaveStatusName.in(leaveStatusNames));
		}

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<LeaveApplication> leaveApplications = leaveAppTypedQuery.getResultList();

		return leaveApplications;
	}

	@Override
	public Integer getCountPendingLeaveAppl(List<String> leaveStatusList, Long leaveTypeId, Long leaveSchemeId,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Company> companyLeaveJoin = empRoot.join(LeaveApplication_.company);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemeJoin = leaveSchemeTypeJoin.join(LeaveSchemeType_.leaveScheme);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		restriction = cb.and(restriction, cb.equal(companyLeaveJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));
		restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId), leaveSchemeId));
		restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(leaveStatusList));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public List<LeaveApplication> getPendingLeaveApplEmps(List<String> leaveStatusList, Long leaveTypeId,
			Long leaveSchemeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(leaveAppRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Company> companyLeaveJoin = leaveAppRoot.join(LeaveApplication_.company);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = leaveAppRoot
				.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = leaveAppRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemeJoin = leaveSchemeTypeJoin.join(LeaveSchemeType_.leaveScheme);
		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		restriction = cb.and(restriction, cb.equal(companyLeaveJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.equal(leaveTypeJoin.get(LeaveTypeMaster_.leaveTypeId), leaveTypeId));
		restriction = cb.and(restriction, cb.equal(leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId), leaveSchemeId));

		restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(leaveStatusList));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> getLeaveApplicationLeaveSchemeTypeId(Long companyId, Long leaveSchemeTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(leaveAppRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Company> companyLeaveJoin = leaveAppRoot.join(LeaveApplication_.company);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveschemeType = leaveAppRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeType = employeeLeaveschemeType
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		restriction = cb.and(restriction, cb.equal(companyLeaveJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction,
				cb.equal(leaveSchemeType.get(LeaveSchemeType_.leaveSchemeTypeId), leaveSchemeTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public Long getCountForCondition(AddLeaveConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}
		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}

	@Override
	public List<LeaveApplication> findCancelledLeaveApplications(Long leaveAppId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);
		Predicate restriction = cb.conjunction();
		Join<LeaveApplication, Company> companyJoin = empRoot.join(LeaveApplication_.company);
		restriction = cb.and(restriction,
				cb.equal(empRoot.get(LeaveApplication_.leaveCancelApplication).get("leaveApplicationId").as(Long.class),
						leaveAppId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<LeaveApplication> getApprovedNCancelLeaveForKeyPayInt(Long maxLeaveApplicationId, Long companyId,
			String leaveStatusName) {
		List<String> leaveStatusNameList = new ArrayList<String>();
		leaveStatusNameList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		leaveStatusNameList.add(PayAsiaConstants.LEAVE_STATUS_CANCELLED);

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> root = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<LeaveApplication, Company> companyJoin = root.join(LeaveApplication_.company);
		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = root.join(LeaveApplication_.leaveStatusMaster);

		restriction = cb.and(restriction,
				cb.greaterThan(root.get(LeaveApplication_.leaveApplicationId), maxLeaveApplicationId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		restriction = cb.and(restriction, cb.or(
				cb.and(cb.not(cb.isNull(root.get(LeaveApplication_.leaveCancelApplication).get("leaveApplicationId"))),
						cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName),
								PayAsiaConstants.LEAVE_STATUS_COMPLETED)),
				cb.and(cb.isNull(root.get(LeaveApplication_.leaveCancelApplication).get("leaveApplicationId")),
						leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(leaveStatusNameList))));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public List<Object[]> getLeaveAppForAutoApproval(Long companyId) {

		String queryString = "SELECT LA.Leave_Application_ID ,LA.Employee_Leave_Scheme_Type_ID, LAR.Work_Flow_Rule_ID, WRM.Rule_Value,LAR.Leave_Application_Reviewer_ID,LAR.Employee_Reviewer_ID, ";
		queryString += "LA.Start_Date,LA.End_Date,LA.Start_Session,LA.End_Session,LA.Total_Days FROM Leave_Application AS LA INNER JOIN Employee_Leave_Scheme_Type AS ELST ";
		queryString += "ON ELST.Employee_Leave_Scheme_Type_ID = LA.Employee_Leave_Scheme_Type_ID ";
		queryString += "INNER JOIN leave_scheme_type AS LST ON LST.Leave_Scheme_Type_ID = ELST.Leave_Scheme_Type_ID ";
		queryString += "INNER JOIN Leave_Scheme_Type_Availing_Leave AS LSTAL ON LSTAL.Leave_Scheme_Type_ID = LST.Leave_Scheme_Type_ID ";
		queryString += "INNER JOIN Leave_Application_Reviewer AS LAR ON LAR.Leave_Application_ID = LA.Leave_Application_ID AND LAR.Pending=1 ";
		queryString += "INNER JOIN Work_Flow_Rule_Master AS WRM ON WRM.Work_Flow_Rule_ID = LAR.Work_Flow_Rule_ID ";
		queryString += " where LA.Company_ID= :company and LA.Leave_Status_ID IN (2,3) "
				+ " AND (CAST(DATEADD(DAY,LSTAL.Auto_Approve_After_Days+1,la.End_Date) AS DATE) <= CAST(GETDATE() AS DATE)) "
				+ " AND LSTAL.Auto_Approve=1";

		Query query = entityManagerFactory.createNativeQuery(queryString);
		query.setParameter("company", companyId);
		List<Object[]> tuples = query.getResultList();
		return tuples;

	}

	@Override
	public LeaveApplication findByLeaveApplicationIdAndEmpId(Long leaveApplicationId, Long empId, Long companyId) {
		LeaveApplication leaveApplication=null;
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> root = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<LeaveApplication, Company> companyJoin = root.join(LeaveApplication_.company);
		Join<LeaveApplication, Employee> leaveAppJoin = root.join(LeaveApplication_.employee);
		
		restriction = cb.and(restriction, cb.equal(root.get(LeaveApplication_.leaveApplicationId), leaveApplicationId));

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(leaveAppJoin.get(Employee_.employeeId), empId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if(leaveAppTypedQuery.getResultList().size()>0)
		{
			leaveApplication=leaveAppTypedQuery.getResultList().get(0);
			return leaveApplication;
	}
		else
		{
			return leaveApplication;
		}
	}
	
	@Override
	public List<LeaveApplication> findByLeaveAppIdsEmp(Long companyId, Long employeeId, ArrayList<Long> leaveAppIdList, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> leaveAppRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(leaveAppRoot);
		Predicate restriction = cb.conjunction();
		leaveAppRoot.join(LeaveApplication_.employee);
		leaveAppRoot.join(LeaveApplication_.company);
		Join<LeaveApplication, Company> leaveCompanyJoin = leaveAppRoot.join(LeaveApplication_.company);
		Join<Company, CompanyGroup> companyGroupJoin = leaveCompanyJoin.join(Company_.companyGroup);
		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<Company> cmp = subquery.from(Company.class);
		Join<Company, CompanyGroup> sqEmp = cmp.join(Company_.companyGroup);
		subquery.select(sqEmp.get(CompanyGroup_.groupId).as(Long.class));
		Path<Long> companyID = cmp.get(Company_.companyId);
		Predicate subRestriction = cb.conjunction();
		subRestriction = cb.and(subRestriction, cb.equal(companyID, companyId));
		subquery.where(subRestriction);
		restriction = cb.and(restriction, leaveAppRoot.get(LeaveApplication_.leaveApplicationId).in(leaveAppIdList));
		restriction = cb.and(restriction, cb.and(cb.in(companyGroupJoin.get(CompanyGroup_.groupId)).value(subquery)));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForleaveApplication(sortDTO, leaveAppRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}

	@Override
	public LeaveApplication findLeaveApplicationByCompanyId(Long leaveApplicationId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> root = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<LeaveApplication, Company> companyJoin = root.join(LeaveApplication_.company);
		restriction = cb.and(restriction, cb.equal(root.get(LeaveApplication_.leaveApplicationId), leaveApplicationId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList().get(0);
	}
	
	@Override
	public LeaveApplication findByLeaveApplicationIdAndEmployeeId(Long leaveApplicationId, Long empId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> root = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<LeaveApplication, Company> companyJoin = root.join(LeaveApplication_.company);
		Join<LeaveApplication, Employee> leaveAppJoin = root.join(LeaveApplication_.employee);
		
		restriction = cb.and(restriction, cb.equal(root.get(LeaveApplication_.leaveApplicationId), leaveApplicationId));

		restriction = cb.and(restriction, cb.equal(leaveAppJoin.get(Employee_.employeeId), empId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList().get(0);
	}
	@Override
	public Long getCountForConditionAllLeave(AddLeaveConditionDTO conditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot));

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);
		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
	}
	@Override
	public List<LeaveApplication> findByConditionForAllLeaveRequest(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		Join<LeaveApplication, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(LeaveApplication_.employeeLeaveSchemeType);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		if (StringUtils.isNotBlank(conditionDTO.getLeaveTypeName())) {

			restriction = cb.and(restriction, cb.like(
					leaveSchemeTypeJoin.get(LeaveSchemeType_.leaveTypeMaster).get("leaveTypeName").as(String.class),
					conditionDTO.getLeaveTypeName()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.createdDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getCreatedDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveAppfromDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.startDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveAppfromDate())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveApptoDate())) {

			restriction = cb.and(restriction, cb.equal(empRoot.get(LeaveApplication_.endDate).as(Date.class),
					DateUtils.stringToDate(conditionDTO.getLeaveApptoDate())));
		}

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));

		}

		criteriaQuery.where(restriction);

		if (sortDTO == null || sortDTO.getColumnName().equals("")) {
			criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
		}
		
//		if (sortDTO != null) {
//
//			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, empRoot, empLeaveJoin, leaveStatusJoin);
//			if (sortPath != null) {
//				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
//					criteriaQuery.orderBy(cb.asc(sortPath));
//				}
//				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
//					criteriaQuery.orderBy(cb.desc(sortPath));
//				}
//			}
//			 if (sortPath == null) {
//			 criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
//			 }
//		}
		
		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			leaveAppTypedQuery.setFirstResult(getStartPosition(pageDTO));
			leaveAppTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return leaveAppTypedQuery.getResultList();
	}
	
	
	
	@Override
	public Long getCountForConditionCompletedForCancellation(AddLeaveConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(cb.count(empRoot));
		
		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);

		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusId() != null) {
			restriction = cb.and(restriction,
					cb.equal(leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID), conditionDTO.getLeaveStatusId()));
		}

		if (conditionDTO.getFromDate() != null && conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction,
					cb.or(cb.between(empRoot.get(LeaveApplication_.startDate).as(Timestamp.class),
							conditionDTO.getFromDate(), conditionDTO.getToDate()),
							cb.between(empRoot.get(LeaveApplication_.endDate).as(Timestamp.class),
									conditionDTO.getFromDate(), conditionDTO.getToDate())));
		}

		criteriaQuery.where(restriction);

		TypedQuery<Long> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getSingleResult();
		
		
	}
	
	/*
	 	ADDED TO APPLY SORTING IN VIEW-REQUEST-LEAVE
	 */
	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO, Root<LeaveApplication> empRoot,Join<LeaveApplication, Employee> empLeaveJoin, 
			Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin ) {

		Path<String> sortPath = null;
		
		List<String> leaveChangeReqColList = new ArrayList<>();
		leaveChangeReqColList.add(SortConstants.LEAVE_CHANGE_REQUEST_CREATED_DATE);
		leaveChangeReqColList.add(SortConstants.START_DATE);
		leaveChangeReqColList.add(SortConstants.END_DATE);
		
		List<String> leaveReviewerIsColList = new ArrayList<>();
		leaveReviewerIsColList.add(SortConstants.EMPLOYEE_NAME);
		leaveReviewerIsColList.add(SortConstants.EMPLOYEE_NUMBER);

		List<String> leaveTypeIsColList = new ArrayList<>();
		leaveTypeIsColList.add(SortConstants.LEAVE_REVIEWER_LEAVE_TYPE);
		
		List<String> leaveReviewersList = new ArrayList<>();
		leaveReviewersList.add(SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER1);
		leaveReviewersList.add(SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER2);
		leaveReviewersList.add(SortConstants.LEAVE_REVIEWER_LEAVE_REVIEWER3);
		
		if (leaveChangeReqColList.contains(sortDTO.getColumnName())) {
			sortPath = empRoot.get(colMap.get(LeaveApplication.class + sortDTO.getColumnName()));
		}
		
		if (leaveReviewerIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empLeaveJoin.get(colMap.get(Employee.class + sortDTO.getColumnName()));
		}
		
		if (leaveTypeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveStatusJoin.get(colMap.get(LeaveTypeMaster.class + sortDTO.getColumnName()));
		}
		
		if (leaveReviewersList.contains(sortDTO.getColumnName())) {
			sortPath = leaveStatusJoin.get(colMap.get(LeaveReviewerForm.class + sortDTO.getColumnName()));
		}
		
		return sortPath;
	}
	
	/*
	 	FUNCTION TO CHECK IF EMPLOYEE IS ON-LEAVE OR NOT FOR TEAM LEAVE MEMBER API
	 */
	@Override
	public boolean findIsOnLeave(AddLeaveConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplication> criteriaQuery = cb.createQuery(LeaveApplication.class);
		Root<LeaveApplication> empRoot = criteriaQuery.from(LeaveApplication.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplication, Employee> empLeaveJoin = empRoot.join(LeaveApplication_.employee);
		Join<LeaveApplication, LeaveStatusMaster> leaveStatusJoin = empRoot.join(LeaveApplication_.leaveStatusMaster);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction, cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getLeaveStatusNames()!=null && !conditionDTO.getLeaveStatusNames().isEmpty()) {
			restriction = cb.and(restriction, leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusName).in(conditionDTO.getLeaveStatusNames()));
		}

		if (conditionDTO.getLeaveType() != null && "cancel".equals(conditionDTO.getLeaveType())) {
			restriction = cb.and(restriction, cb.isNotNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		} else {
			restriction = cb.and(restriction, cb.isNull(empRoot.get(LeaveApplication_.leaveCancelApplication)));
		}
		
		ParameterExpression<java.util.Date> parameter = cb.parameter(java.util.Date.class);
		
		restriction = cb.and(restriction, (cb.between(parameter, 
				empRoot.get(LeaveApplication_.startDate).as(Timestamp.class), empRoot.get(LeaveApplication_.endDate).as(Timestamp.class))));
		
		criteriaQuery.where(restriction);
	
		criteriaQuery.orderBy(cb.desc(empRoot.get(LeaveTypeMaster_.createdDate)));
		
		Date formattedCurrentDate = DateUtils.getCurrentDate(PayAsiaConstants.DATE_FORMAT_YYYY_MM_DD);
		TypedQuery<LeaveApplication> leaveAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery)
				.setParameter(parameter, formattedCurrentDate);
		
		return !leaveAppTypedQuery.getResultList().isEmpty();
	}

}
