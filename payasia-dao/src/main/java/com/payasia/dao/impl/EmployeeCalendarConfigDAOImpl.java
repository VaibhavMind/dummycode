package com.payasia.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

import com.payasia.common.dto.EmployeeCalendarConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeCalendarConfigDAO;
import com.payasia.dao.bean.CalendarPatternMaster;
import com.payasia.dao.bean.CalendarPatternMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyCalendarTemplate;
import com.payasia.dao.bean.CompanyCalendarTemplate_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeCalendarConfig;
import com.payasia.dao.bean.EmployeeCalendarConfig_;
import com.payasia.dao.bean.Employee_;

@Repository
public class EmployeeCalendarConfigDAOImpl extends BaseDAO implements
		EmployeeCalendarConfigDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeCalendarConfig employeeCalendarConfig = new EmployeeCalendarConfig();
		return employeeCalendarConfig;
	}

	@Override
	public void update(EmployeeCalendarConfig employeeCalendarConfig) {
		super.update(employeeCalendarConfig);
	}

	@Override
	public void delete(EmployeeCalendarConfig employeeCalendarConfig) {
		super.delete(employeeCalendarConfig);
	}

	@Override
	public void save(EmployeeCalendarConfig employeeCalendarConfig) {
		super.save(employeeCalendarConfig);
	}

	@Override
	public EmployeeCalendarConfig findByID(Long employeeCalendarConfigId) {
		return super.findById(EmployeeCalendarConfig.class,
				employeeCalendarConfigId);
	}

	@Override
	public List<EmployeeCalendarConfig> findByCondition(
			EmployeeCalendarConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeCalendarConfig> criteriaQuery = cb
				.createQuery(EmployeeCalendarConfig.class);

		Root<EmployeeCalendarConfig> calTempRoot = criteriaQuery
				.from(EmployeeCalendarConfig.class);

		criteriaQuery.select(calTempRoot);

		Join<EmployeeCalendarConfig, CompanyCalendarTemplate> companyTempJoin = calTempRoot
				.join(EmployeeCalendarConfig_.companyCalendarTemplate);
		Join<EmployeeCalendarConfig, Employee> employeeJoin = calTempRoot
				.join(EmployeeCalendarConfig_.employee);
		Join<CompanyCalendarTemplate, Company> companyJoin = companyTempJoin
				.join(CompanyCalendarTemplate_.company);
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getTemplateName())) {
			restriction = cb.and(restriction, cb.like(cb.upper((companyTempJoin
					.get(CompanyCalendarTemplate_.templateName))), conditionDTO
					.getTemplateName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((employeeJoin.get(Employee_.employeeNumber))),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.lastName)),
					conditionDTO.getLastName().toUpperCase()));
		}

		restriction = cb.and(restriction,
				cb.equal((companyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForCalTemplate(sortDTO,
					calTempRoot, companyTempJoin, employeeJoin);

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

		TypedQuery<EmployeeCalendarConfig> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			calTempTypedQuery.setFirstResult(getStartPosition(pageDTO));
			calTempTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<EmployeeCalendarConfig> calTempList = calTempTypedQuery
				.getResultList();

		return calTempList;
	}

	@Override
	public List<EmployeeCalendarConfig> findByCalTempId(Long companyCalTempId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeCalendarConfig> criteriaQuery = cb
				.createQuery(EmployeeCalendarConfig.class);

		Root<EmployeeCalendarConfig> calTempRoot = criteriaQuery
				.from(EmployeeCalendarConfig.class);

		criteriaQuery.select(calTempRoot);

		Join<EmployeeCalendarConfig, CompanyCalendarTemplate> companyTempJoin = calTempRoot
				.join(EmployeeCalendarConfig_.companyCalendarTemplate);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyTempJoin
				.get(CompanyCalendarTemplate_.companyCalendarTemplateId),
				companyCalTempId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeCalendarConfig> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeCalendarConfig> calTempList = calTempTypedQuery
				.getResultList();
		return calTempList;
	}

	public Path<String> getSortPathForCalTemplate(
			SortCondition sortDTO,
			Root<EmployeeCalendarConfig> calTempRoot,
			Join<EmployeeCalendarConfig, CompanyCalendarTemplate> companyTempJoin,
			Join<EmployeeCalendarConfig, Employee> employeeJoin) {

		List<String> calTemplateColList = new ArrayList<String>();
		calTemplateColList.add(SortConstants.EMPLOYEE_CALENDAR_START_DATE);
		calTemplateColList.add(SortConstants.EMPLOYEE_CALENDAR_END_DATE);

		List<String> empColList = new ArrayList<String>();
		empColList.add(SortConstants.EMPLOYEE_CALENDAR_EMPLOYEE_NAME);
		empColList.add(SortConstants.EMPLOYEE_CALENDAR_EMPLOYEE_NUMBER);

		List<String> calTempColList = new ArrayList<String>();
		calTempColList.add(SortConstants.EMPLOYEE_CALENDAR_TEMPLATE_NAME);

		Path<String> sortPath = null;

		if (calTemplateColList.contains(sortDTO.getColumnName())) {
			sortPath = calTempRoot.get(colMap.get(EmployeeCalendarConfig.class
					+ sortDTO.getColumnName()));
		}
		if (empColList.contains(sortDTO.getColumnName())) {
			sortPath = employeeJoin.get(colMap.get(Employee.class
					+ sortDTO.getColumnName()));
		}
		if (calTempColList.contains(sortDTO.getColumnName())) {
			sortPath = companyTempJoin.get(colMap
					.get(CompanyCalendarTemplate.class
							+ sortDTO.getColumnName()));
		}
		return sortPath;

	}

	@Override
	public int getCountForCondition(EmployeeCalendarConditionDTO conditionDTO,
			Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);

		Root<EmployeeCalendarConfig> calTempRoot = criteriaQuery
				.from(EmployeeCalendarConfig.class);

		criteriaQuery.select(cb.count(calTempRoot).as(Integer.class));

		Join<EmployeeCalendarConfig, CompanyCalendarTemplate> companyTempJoin = calTempRoot
				.join(EmployeeCalendarConfig_.companyCalendarTemplate);
		Join<EmployeeCalendarConfig, Employee> employeeJoin = calTempRoot
				.join(EmployeeCalendarConfig_.employee);
		Join<CompanyCalendarTemplate, Company> companyJoin = companyTempJoin
				.join(CompanyCalendarTemplate_.company);
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getTemplateName())) {
			restriction = cb.and(restriction, cb.like(cb.upper((companyTempJoin
					.get(CompanyCalendarTemplate_.templateName))), conditionDTO
					.getTemplateName().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(
					cb.upper((employeeJoin.get(Employee_.employeeNumber))),
					conditionDTO.getEmployeeNumber().toUpperCase()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getFirstName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.firstName)),
					conditionDTO.getFirstName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getLastName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeJoin.get(Employee_.lastName)),
					conditionDTO.getLastName().toUpperCase()));
		}

		restriction = cb.and(restriction,
				cb.equal((companyJoin.get(Company_.companyId)), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return calTempTypedQuery.getSingleResult();
	}

	@Override
	public List<EmployeeCalendarConfig> findByCalPatternId(Long calPatternId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeCalendarConfig> criteriaQuery = cb
				.createQuery(EmployeeCalendarConfig.class);

		Root<EmployeeCalendarConfig> calTempRoot = criteriaQuery
				.from(EmployeeCalendarConfig.class);

		criteriaQuery.select(calTempRoot);

		Join<EmployeeCalendarConfig, CompanyCalendarTemplate> companyTempJoin = calTempRoot
				.join(EmployeeCalendarConfig_.companyCalendarTemplate);
		Join<CompanyCalendarTemplate, CalendarPatternMaster> calPatternJoin = companyTempJoin
				.join(CompanyCalendarTemplate_.calendarPatternMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				calPatternJoin.get(CalendarPatternMaster_.calendarPatternId),
				calPatternId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeCalendarConfig> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeCalendarConfig> calTempList = calTempTypedQuery
				.getResultList();
		return calTempList;
	}

	@Override
	public EmployeeCalendarConfig checkEmpCalTempByDate(Long empCalConfigId,
			Long employeeId, String date, String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeCalendarConfig> criteriaQuery = cb
				.createQuery(EmployeeCalendarConfig.class);
		Root<EmployeeCalendarConfig> empRoot = criteriaQuery
				.from(EmployeeCalendarConfig.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeCalendarConfig, Employee> empJoin = empRoot
				.join(EmployeeCalendarConfig_.employee);

		Predicate restriction = cb.conjunction();
		if (empCalConfigId != null) {
			restriction = cb.and(restriction, cb.notEqual(empRoot
					.get(EmployeeCalendarConfig_.employeeCalendarConfigId),
					empCalConfigId));
		}

		Calendar dateForNull = new GregorianCalendar(
				PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
				PayAsiaConstants.LAST_DAY);
		Timestamp timeStampForNull = new Timestamp(
				dateForNull.getTimeInMillis());

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(
				restriction,

				restriction = cb.and(cb.and(restriction, cb.lessThanOrEqualTo(
						empRoot.get(EmployeeCalendarConfig_.startDate),
						DateUtils.stringToTimestamp(date, dateFormat))), cb
						.and(restriction, cb.greaterThanOrEqualTo(cb.coalesce(
								empRoot.get(EmployeeCalendarConfig_.endDate),
								timeStampForNull), DateUtils.stringToTimestamp(
								date, dateFormat)))));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeCalendarConfig> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeCalendarConfig> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

	@Override
	public EmployeeCalendarConfig findByEmpIdAndEndDate(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeCalendarConfig> criteriaQuery = cb
				.createQuery(EmployeeCalendarConfig.class);
		Root<EmployeeCalendarConfig> empRoot = criteriaQuery
				.from(EmployeeCalendarConfig.class);
		criteriaQuery.select(empRoot);

		Join<EmployeeCalendarConfig, Employee> empJoin = empRoot
				.join(EmployeeCalendarConfig_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeCalendarConfig> empTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeCalendarConfig> empList = empTypedQuery.getResultList();
		if (empList != null && !empList.isEmpty()) {
			return empList.get(0);
		}
		return null;

	}

}
