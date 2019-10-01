package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.AssignLeaveSchemeDTO;
import com.payasia.common.dto.EmployeeHeadCountReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeType_;
import com.payasia.dao.bean.EmployeeLeaveScheme_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;

@Repository
public class EmployeeLeaveSchemeDAOImpl extends BaseDAO implements
		EmployeeLeaveSchemeDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeLeaveScheme employeeLeaveScheme = new EmployeeLeaveScheme();
		return employeeLeaveScheme;
	}

	@Override
	public void save(EmployeeLeaveScheme employeeLeaveScheme) {
		super.save(employeeLeaveScheme);
	}

	@Override
	public EmployeeLeaveScheme saveReturn(
			EmployeeLeaveScheme employeeLeaveScheme) {
		EmployeeLeaveScheme persistObj = employeeLeaveScheme;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmployeeLeaveScheme) getBaseEntity();
			beanUtil.copyProperties(persistObj, employeeLeaveScheme);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();

		return persistObj;
	}

	@Override
	public EmployeeLeaveScheme findById(Long employeeLeaveSchemeId) {
		return super.findById(EmployeeLeaveScheme.class, employeeLeaveSchemeId);
	}

	@Override
	public void update(EmployeeLeaveScheme employeeLeaveScheme) {
		super.update(employeeLeaveScheme);
	}

	@Override
	public void delete(EmployeeLeaveScheme employeeLeaveScheme) {
		super.delete(employeeLeaveScheme);
	}

	@Override
	public List<EmployeeLeaveScheme> findByCondition(
			AssignLeaveSchemeDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);
		Join<Employee, Company> empCompanyJoin = empJoin
				.join(Employee_.company);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(empJoin.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(cb.like(cb.upper(empJoin
					.get(Employee_.firstName)), conditionDTO.getEmployeeName()
					.toUpperCase()), cb.like(cb.upper(empJoin
					.get(Employee_.lastName)), conditionDTO.getEmployeeName()
					.toUpperCase())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveSchemeName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(leaveSchemeJoin.get(LeaveScheme_.schemeName)),
					conditionDTO.getLeaveSchemeName().toUpperCase()));
		}

		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(empRoot.get(EmployeeLeaveScheme_.startDate)),
					conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(empRoot.get(EmployeeLeaveScheme_.endDate)),
					conditionDTO.getToDate()));
		}

		restriction = cb.and(restriction,
				cb.equal((empCompanyJoin.get(Company_.companyId)), companyId));
		restriction = cb.and(restriction, cb.equal(
				(leaveSchemeCompanyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO,
					empRoot, empJoin, leaveSchemeJoin);
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

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			empTypedQuery.setFirstResult(getStartPosition(pageDTO));
			empTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<EmployeeLeaveScheme> empLeaveSchemeList = empTypedQuery
				.getResultList();
		return empLeaveSchemeList;

	}

	@Override
	public EmployeeLeaveScheme findByActive(Long employeeId, boolean active) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveScheme findByEmpIdAndEndDate(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.isNull(empRoot.get(EmployeeLeaveScheme_.endDate)));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveScheme checkEmpLeaveSchemeByDate(Long empLeaveSchemeId,
			Long employeeId, String date, String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Join<EmployeeLeaveScheme, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(EmployeeLeaveScheme_.employeeLeaveSchemeTypes);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeMasterJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();
		if (empLeaveSchemeId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					empRoot.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
					empLeaveSchemeId));
		}

		Calendar dateForNull = new GregorianCalendar(
				PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(
				dateForNull.getTimeInMillis());

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				empRoot.get(EmployeeLeaveScheme_.startDate),
				DateUtils.stringToTimestamp(date, dateFormat)), cb
				.greaterThanOrEqualTo(cb.coalesce(
						empRoot.get(EmployeeLeaveScheme_.endDate),
						timeStampForNull), DateUtils.stringToTimestamp(date,
						dateFormat)));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(leaveTypeMasterJoin
				.get(LeaveTypeMaster_.sortOrder)));
		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveScheme getEmpLeaveSchemeGreaterThanCurrDate(
			Long empLeaveSchemeId, Long employeeId, String date,
			String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Predicate restriction = cb.conjunction();
		if (empLeaveSchemeId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					empRoot.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
					empLeaveSchemeId));
		}

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(
				restriction,
				cb.greaterThanOrEqualTo(
						empRoot.get(EmployeeLeaveScheme_.startDate),
						DateUtils.stringToTimestamp(date, dateFormat)));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveScheme checkEmpLeaveSchemeByStartDate(
			Long empLeaveSchemeId, Long employeeId, String date,
			String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Join<EmployeeLeaveScheme, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(EmployeeLeaveScheme_.employeeLeaveSchemeTypes);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeMasterJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();
		if (empLeaveSchemeId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					empRoot.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
					empLeaveSchemeId));
		}

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(
				restriction,
				cb.greaterThanOrEqualTo(
						empRoot.get(EmployeeLeaveScheme_.endDate),
						DateUtils.stringToTimestamp(date, dateFormat)));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(leaveTypeMasterJoin
				.get(LeaveTypeMaster_.sortOrder)));
		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveScheme checkEmpLeaveSchemeByEndDate(
			Long empLeaveSchemeId, Long employeeId, String date,
			String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Join<EmployeeLeaveScheme, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(EmployeeLeaveScheme_.employeeLeaveSchemeTypes);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeMasterJoin = leaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();
		if (empLeaveSchemeId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					empRoot.get(EmployeeLeaveScheme_.employeeLeaveSchemeId),
					empLeaveSchemeId));
		}

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(
				restriction,
				cb.lessThanOrEqualTo(
						empRoot.get(EmployeeLeaveScheme_.startDate),
						DateUtils.stringToTimestamp(date, dateFormat)));
		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(leaveTypeMasterJoin
				.get(LeaveTypeMaster_.sortOrder)));
		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveScheme checkEmpLeaveSchemeByDateAndEmpNum(
			String employeeNumber, int year, String date, String dateFormat,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Join<Employee, Company> employeeCompanyJoin = empLeaveSchemeJoin
				.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeJoin.get(Employee_.employeeNumber),
				employeeNumber));
		restriction = cb.and(restriction, cb.equal(
				employeeCompanyJoin.get(Company_.companyId), companyId));
		Calendar dateForNull = new GregorianCalendar(
				PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(
				dateForNull.getTimeInMillis());

		restriction = cb.and(
				restriction,

				restriction = cb.and(cb.and(restriction, cb.lessThanOrEqualTo(
						empRoot.get(EmployeeLeaveScheme_.startDate),
						DateUtils.stringToTimestamp(date, dateFormat))), cb
						.and(restriction, cb.greaterThanOrEqualTo(cb.coalesce(
								empRoot.get(EmployeeLeaveScheme_.endDate),
								timeStampForNull), DateUtils.stringToTimestamp(
								date, dateFormat)))));
		criteriaQuery.where(restriction);
		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<EmployeeLeaveScheme> empRoot,
			Join<EmployeeLeaveScheme, Employee> empJoin,
			Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin) {

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.ASSIGN_LEAVE_SCHEME_EMPLOYEE_NAME);
		employeeIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_EMPLOYEE_NUMBER);

		List<String> empLeaveSchemeColList = new ArrayList<String>();
		empLeaveSchemeColList.add(SortConstants.ASSIGN_LEAVE_SCHEME_ACTIVE);
		empLeaveSchemeColList.add(SortConstants.ASSIGN_LEAVE_SCHEME_FROM_DATE);
		empLeaveSchemeColList.add(SortConstants.ASSIGN_LEAVE_SCHEME_TO_DATE);

		List<String> leaveSchemeIsColList = new ArrayList<String>();
		leaveSchemeIsColList
				.add(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_SCHEME);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empJoin.get(colMap.get(Employee.class
					+ sortDTO.getColumnName()));
		}
		if (empLeaveSchemeColList.contains(sortDTO.getColumnName())) {
			sortPath = empRoot.get(colMap.get(EmployeeLeaveScheme.class
					+ sortDTO.getColumnName()));
		}
		if (leaveSchemeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = leaveSchemeJoin.get(colMap.get(LeaveScheme.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Long getCountForCondition(AssignLeaveSchemeDTO conditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(cb.count(empRoot));

		Join<EmployeeLeaveScheme, Employee> empJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);
		Join<Employee, Company> empCompanyJoin = empJoin
				.join(Employee_.company);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(empJoin.get(Employee_.employeeNumber)),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(cb.like(cb.upper(empJoin
					.get(Employee_.firstName)), conditionDTO.getEmployeeName()
					.toUpperCase()), cb.like(cb.upper(empJoin
					.get(Employee_.lastName)), conditionDTO.getEmployeeName()
					.toUpperCase())));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLeaveSchemeName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(leaveSchemeJoin.get(LeaveScheme_.schemeName)),
					conditionDTO.getLeaveSchemeName().toUpperCase()));
		}

		restriction = cb.and(restriction,
				cb.equal((empCompanyJoin.get(Company_.companyId)), companyId));
		restriction = cb.and(restriction, cb.equal(
				(leaveSchemeCompanyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return empTypedQuery.getSingleResult();
	}

	@Override
	public List<EmployeeLeaveScheme> findByConditionCompany(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);
		Join<Employee, Company> empCompanyJoin = empJoin
				.join(Employee_.company);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal((empCompanyJoin.get(Company_.companyId)), companyId));
		restriction = cb.and(restriction, cb.equal(
				(leaveSchemeCompanyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeLeaveScheme> empLeaveSchemeList = empTypedQuery
				.getResultList();
		return empLeaveSchemeList;

	}

	@Override
	public EmployeeLeaveScheme findByEmployeeScheme(Long employeeId,
			Long leaveSchemeId, String date, String dateFormat) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, Employee> empJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.leaveScheme);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal((empJoin.get(Employee_.employeeId)), employeeId));
		restriction = cb.and(restriction, cb.equal(
				(leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId)),
				leaveSchemeId));

		restriction = cb
				.and(restriction,

						restriction = cb.and(cb.and(restriction, cb
								.lessThanOrEqualTo(empRoot
										.get(EmployeeLeaveScheme_.startDate),
										DateUtils.stringToTimestamp(date,
												dateFormat))), cb.and(
								restriction, cb.greaterThanOrEqualTo(empRoot
										.get(EmployeeLeaveScheme_.endDate),
										DateUtils.stringToTimestamp(date,
												dateFormat)))));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empLeaveSchemeList = empTypedQuery
				.getResultList();
		if (empLeaveSchemeList != null && !empLeaveSchemeList.isEmpty()) {
			return empLeaveSchemeList.get(0);
		}
		return null;

	}

	@Override
	public List<Integer> getYearFromStartDate(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeLeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(
				cb.function("year", Integer.class,
						leaveSchemeRoot.get(EmployeeLeaveScheme_.startDate)))
				.distinct(true);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				(leaveSchemeCompanyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> yearTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Integer> yearList = yearTypedQuery.getResultList();
		return yearList;
	}

	@Override
	public List<Integer> getYearFromEndDate(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeLeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(
				cb.function("year", Integer.class,
						leaveSchemeRoot.get(EmployeeLeaveScheme_.endDate)))
				.distinct(true);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				(leaveSchemeCompanyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);
		TypedQuery<Integer> yearTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Integer> yearList = yearTypedQuery.getResultList();
		return yearList;
	}

	@Override
	public List<EmployeeLeaveScheme> findByCompanyAndEmployee(
			String employeeNumber, int year, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(leaveSchemeRoot);

		Join<EmployeeLeaveScheme, Employee> employeeJoin = leaveSchemeRoot
				.join(EmployeeLeaveScheme_.employee);
		Join<Employee, Company> companyJoin = employeeJoin
				.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				(employeeJoin.get(Employee_.employeeNumber)), employeeNumber));

		restriction = cb.and(restriction,
				cb.equal((companyJoin.get(Company_.companyId)), companyId));
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				cb.function("year", Integer.class,
						leaveSchemeRoot.get(EmployeeLeaveScheme_.startDate)),
				year));
		Calendar dateForNull = new GregorianCalendar(
				PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(
				dateForNull.getTimeInMillis());
		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(cb.function(
				"year", Integer.class, cb.coalesce(
						leaveSchemeRoot.get(EmployeeLeaveScheme_.endDate),
						timeStampForNull)), year));

		criteriaQuery.where(restriction);
		TypedQuery<EmployeeLeaveScheme> yearTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> leaveSchemeList = yearTypedQuery
				.getResultList();
		return leaveSchemeList;
	}

	@Override
	public List<EmployeeLeaveScheme> findByCompanyAndEmployeeId(
			Long employeeId, int year, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(leaveSchemeRoot);

		Join<EmployeeLeaveScheme, Employee> employeeJoin = leaveSchemeRoot
				.join(EmployeeLeaveScheme_.employee);
		Join<Employee, Company> companyJoin = employeeJoin
				.join(Employee_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal((employeeJoin.get(Employee_.employeeId)), employeeId));

		restriction = cb.and(restriction,
				cb.equal((companyJoin.get(Company_.companyId)), companyId));

		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				cb.function("year", Integer.class,
						leaveSchemeRoot.get(EmployeeLeaveScheme_.startDate)),
				year));

		Calendar dateForNull = new GregorianCalendar(
				PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(
				dateForNull.getTimeInMillis());

		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(cb.function(
				"year", Integer.class, cb.coalesce(
						leaveSchemeRoot.get(EmployeeLeaveScheme_.endDate),
						timeStampForNull)), year));

		criteriaQuery.where(restriction);
		TypedQuery<EmployeeLeaveScheme> yearTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> leaveSchemeList = yearTypedQuery
				.getResultList();
		return leaveSchemeList;
	}

	@Override
	public EmployeeLeaveScheme findByEmpNumAndLeaveSchemeName(
			String employeeNumber, String leaveSchemeName, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> leaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(leaveSchemeRoot);

		Join<EmployeeLeaveScheme, Employee> employeeJoin = leaveSchemeRoot
				.join(EmployeeLeaveScheme_.employee);
		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = leaveSchemeRoot
				.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, Company> leaveSchemeCompanyJoin = leaveSchemeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				cb.upper((employeeJoin.get(Employee_.employeeNumber))),
				employeeNumber.toUpperCase()));

		restriction = cb.and(restriction, cb.equal(
				(leaveSchemeCompanyJoin.get(Company_.companyId)), companyId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper((leaveSchemeJoin.get(LeaveScheme_.schemeName))),
				leaveSchemeName.toUpperCase()));

		criteriaQuery.where(restriction);
		TypedQuery<EmployeeLeaveScheme> yearTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> leaveSchemeList = yearTypedQuery
				.getResultList();
		if (!leaveSchemeList.isEmpty()) {
			return leaveSchemeList.get(0);
		}
		return null;
	}

	@Override
	public List<Long> getEmployeesOfLeaveSheme(Long leaveSchemeId,
			Date currentDate) {
		String queryString = "select ELS.employee.employeeId FROM EmployeeLeaveScheme ELS  WHERE ELS.leaveScheme.leaveSchemeId = :leaveSchemeId AND   cast(:currentDate as date) between ELS.startDate and isnull(ELS.endDate,'2999-12-31') ";
		Query query = entityManagerFactory.createQuery(queryString);
		query.setParameter("leaveSchemeId", leaveSchemeId);
		query.setParameter("currentDate",
				DateUtils.convertDateToTimeStamp(currentDate));
		List<Long> empList = query.getResultList();
		return empList;
	}

	@Override
	public EmployeeLeaveScheme findByEmpIdAndLeaveSchemeId(Long employeeId,
			Long leaveSchemeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empleaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empleaveSchemeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(
				restriction,
				cb.equal(empleaveSchemeRoot.get(EmployeeLeaveScheme_.employee)
						.get("employeeId").as(Long.class), employeeId));

		restriction = cb.and(restriction, cb.equal(
				empleaveSchemeRoot.get(EmployeeLeaveScheme_.leaveScheme)
						.get("leaveSchemeId").as(Long.class), leaveSchemeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public List<EmployeeLeaveScheme> findbyLeaveScheme(long leaveSchemeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeLeaveScheme, LeaveScheme> leaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.leaveScheme);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				(leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId)),
				leaveSchemeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empLeaveSchQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeLeaveScheme> empLeaveSchemeList = empLeaveSchQuery
				.getResultList();
		return empLeaveSchemeList;
	}

	@Override
	public List<EmployeeLeaveScheme> getActiveWithFutureLeaveScheme(
			Long employeeId, String date, String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot).distinct(true);

		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Join<EmployeeLeaveScheme, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(EmployeeLeaveScheme_.employeeLeaveSchemeTypes);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		leaveSchemeTypeJoin.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();

		Calendar dateForNull = new GregorianCalendar(
				PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(
				dateForNull.getTimeInMillis());

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(cb.coalesce(
				empRoot.get(EmployeeLeaveScheme_.endDate), timeStampForNull),
				DateUtils.stringToTimestamp(date, dateFormat)));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList;
		}
		return null;

	}

	@Override
	public EmployeeLeaveScheme getLastAssignedEmpLeaveScheme(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empLeaveSchemeRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empLeaveSchemeRoot);

		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeJoin = empLeaveSchemeRoot
				.join(EmployeeLeaveScheme_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(empLeaveSchemeRoot));
		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeLeaveScheme getActiveLeaveSchemeByDate(Long employeeId,
			String date, String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot).distinct(true);

		Join<EmployeeLeaveScheme, Employee> empLeaveSchemeJoin = empRoot
				.join(EmployeeLeaveScheme_.employee);

		Join<EmployeeLeaveScheme, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeJoin = empRoot
				.join(EmployeeLeaveScheme_.employeeLeaveSchemeTypes);

		Join<EmployeeLeaveSchemeType, LeaveSchemeType> leaveSchemeTypeJoin = employeeLeaveSchemeTypeJoin
				.join(EmployeeLeaveSchemeType_.leaveSchemeType);

		leaveSchemeTypeJoin.join(LeaveSchemeType_.leaveTypeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeJoin.get(Employee_.employeeId), employeeId));

		Calendar dateForNull = new GregorianCalendar(
				PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(
				dateForNull.getTimeInMillis());
		restriction = cb.and(
				restriction,

				restriction = cb.and(cb.and(restriction, cb.lessThanOrEqualTo(
						empRoot.get(EmployeeLeaveScheme_.startDate),
						DateUtils.stringToTimestamp(date, dateFormat))), cb
						.and(restriction, cb.greaterThanOrEqualTo(cb.coalesce(
								empRoot.get(EmployeeLeaveScheme_.endDate),
								timeStampForNull), DateUtils.stringToTimestamp(
								date, dateFormat)))));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public List<EmployeeHeadCountReportDTO> getLeaveHeadCountReportDetail(
			final String startDate, final String endDate,
			final String dateFormat, final String companyIdList) {
		final List<EmployeeHeadCountReportDTO> employeeHeadCountReportDTOs = new ArrayList<>();
		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Leave_Head_Count_Report (?,?,?)}");

					cstmt.setTimestamp("@From_Date",
							DateUtils.stringToTimestamp(startDate, dateFormat));
					cstmt.setTimestamp("@To_Date",
							DateUtils.stringToTimestamp(endDate, dateFormat));
					cstmt.setString("@Company_ID_List", companyIdList);

					ResultSet rs = cstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							EmployeeHeadCountReportDTO empHeadCountDto = new EmployeeHeadCountReportDTO();
							empHeadCountDto.setEmployeeId(rs
									.getLong("Employee_ID"));
							empHeadCountDto.setEmployeeNumber(rs
									.getString("Employee_Number"));
							empHeadCountDto.setFirstName(rs
									.getString("First_Name"));
							empHeadCountDto.setLastName(rs
									.getString("Last_Name"));
							empHeadCountDto.setLeaveSchemeName(rs
									.getString("Leave_Scheme_Name"));
							empHeadCountDto.setIsReviewer(rs
									.getString("Is_Leave_Reviewer"));
							if (rs.getTimestamp("Original_Hire_Date") != null) {
								empHeadCountDto.setOriginalHireDate(DateUtils.timeStampToString(
										rs.getTimestamp("Original_Hire_Date"),
										dateFormat));
							}
							if (rs.getTimestamp("Resignation_Date") != null) {
								empHeadCountDto.setResignationDate(DateUtils.timeStampToString(
										rs.getTimestamp("Resignation_Date"),
										dateFormat));
							}
							empHeadCountDto.setCompanyCode(rs
									.getString("Company_Code"));
							empHeadCountDto.setCompanyName(rs
									.getString("Company_Name"));
							empHeadCountDto.setCompanyId(rs
									.getLong("Company_ID"));
							empHeadCountDto.setCountryName(rs
									.getString("Country_Name"));

							employeeHeadCountReportDTOs.add(empHeadCountDto);
						}
					}
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		});
		return employeeHeadCountReportDTOs;
	}

	@Override
	public EmployeeLeaveScheme findSchemeByCompanyId(Long empLeaveSchemeId, Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeLeaveScheme> criteriaQuery = cb
				.createQuery(EmployeeLeaveScheme.class);
		Root<EmployeeLeaveScheme> empRoot = criteriaQuery
				.from(EmployeeLeaveScheme.class);
		criteriaQuery.select(empRoot).distinct(true);
		Join<EmployeeLeaveScheme, LeaveScheme> empLeaveSchemeJoin = empRoot.join(EmployeeLeaveScheme_.leaveScheme);
		Join<LeaveScheme, Company> empLeaveSchemeCompJoin = empLeaveSchemeJoin.join(LeaveScheme_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				empLeaveSchemeCompJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				empRoot.get(EmployeeLeaveScheme_.employeeLeaveSchemeId), empLeaveSchemeId));
		criteriaQuery.where(restriction);
		TypedQuery<EmployeeLeaveScheme> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeLeaveScheme> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;
	}

}
