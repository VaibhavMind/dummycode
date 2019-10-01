package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.Date;
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

import com.payasia.common.dto.HolidayCalendarConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail_;
import com.payasia.dao.bean.CompanyHolidayCalendar_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeHolidayCalendar_;
import com.payasia.dao.bean.Employee_;

@Repository
public class EmployeeHolidayCalendarDAOImpl extends BaseDAO implements
		EmployeeHolidayCalendarDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeHolidayCalendar employeeHolidayCalendar = new EmployeeHolidayCalendar();
		return employeeHolidayCalendar;
	}

	@Override
	public EmployeeHolidayCalendar findByID(Long employeeHolidayCalendarId) {
		return super.findById(EmployeeHolidayCalendar.class,
				employeeHolidayCalendarId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#save(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void save(EmployeeHolidayCalendar employeeHolidayCalendar) {
		super.save(employeeHolidayCalendar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#update(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void update(EmployeeHolidayCalendar employeeHolidayCalendar) {
		super.update(employeeHolidayCalendar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#delete(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void delete(EmployeeHolidayCalendar employeeHolidayCalendar) {
		super.delete(employeeHolidayCalendar);

	}

	@Override
	public List<EmployeeHolidayCalendar> getEmployeeHolidayCalendars(
			HolidayCalendarConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHolidayCalendar> criteriaQuery = cb
				.createQuery(EmployeeHolidayCalendar.class);
		Root<EmployeeHolidayCalendar> calendarRoot = criteriaQuery
				.from(EmployeeHolidayCalendar.class);
		criteriaQuery.select(calendarRoot);

		Join<EmployeeHolidayCalendar, CompanyHolidayCalendar> cmpHolidayJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.companyHolidayCalendar);
		Join<CompanyHolidayCalendar, Company> companyJoin = cmpHolidayJoin
				.join(CompanyHolidayCalendar_.company);
		Join<EmployeeHolidayCalendar, Employee> employeeJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.employee);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCalName())) {
			restriction = cb.and(restriction, cb.like(cb.upper(cmpHolidayJoin
					.get(CompanyHolidayCalendar_.calendarName)), conditionDTO
					.getCalName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(
					employeeJoin.get(Employee_.employeeNumber),
					conditionDTO.getEmployeeNumber() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(cb.like(
					cb.upper(employeeJoin.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase()), cb.like(
					cb.upper(employeeJoin.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase())));
		}

		restriction = cb.and(
				restriction,
				cb.equal(companyJoin.get(Company_.companyId),
						conditionDTO.getCompanyId()));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForHolidayCal(sortDTO,
					cmpHolidayJoin, employeeJoin);
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

		TypedQuery<EmployeeHolidayCalendar> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<EmployeeHolidayCalendar> empCalendarList = typedQuery
				.getResultList();

		return empCalendarList;
	}

	public Path<String> getSortPathForHolidayCal(
			SortCondition sortDTO,
			Join<EmployeeHolidayCalendar, CompanyHolidayCalendar> cmpHolidayJoin,
			Join<EmployeeHolidayCalendar, Employee> employeeJoin) {

		List<String> holIsColList = new ArrayList<String>();
		holIsColList.add(SortConstants.ASSIGN_LEAVE_SCHEME_EMPLOYEE_NAME);
		List<String> holCalIsColList = new ArrayList<String>();
		holCalIsColList.add(SortConstants.EMP_HOLIDAY_CALENDAR_NAME);

		Path<String> sortPath = null;

		if (holIsColList.contains(sortDTO.getColumnName())) {
			sortPath = employeeJoin.get(colMap.get(Employee.class
					+ sortDTO.getColumnName()));
		}
		if (holCalIsColList.contains(sortDTO.getColumnName())) {
			sortPath = cmpHolidayJoin
					.get(colMap.get(CompanyHolidayCalendar.class
							+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Integer getCountForCondition(
			HolidayCalendarConditionDTO conditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<EmployeeHolidayCalendar> calendarRoot = criteriaQuery
				.from(EmployeeHolidayCalendar.class);
		criteriaQuery.select(cb.count(calendarRoot).as(Integer.class));

		Join<EmployeeHolidayCalendar, CompanyHolidayCalendar> cmpHolidayJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.companyHolidayCalendar);
		Join<CompanyHolidayCalendar, Company> companyJoin = cmpHolidayJoin
				.join(CompanyHolidayCalendar_.company);
		Join<EmployeeHolidayCalendar, Employee> employeeJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.employee);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCalName())) {
			restriction = cb.and(restriction, cb.like(cb.upper(cmpHolidayJoin
					.get(CompanyHolidayCalendar_.calendarName)), conditionDTO
					.getCalName().toUpperCase() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeNumber())) {
			restriction = cb.and(restriction, cb.like(
					employeeJoin.get(Employee_.employeeNumber),
					conditionDTO.getEmployeeNumber() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getEmployeeName())) {

			restriction = cb.and(restriction, cb.or(cb.like(
					cb.upper(employeeJoin.get(Employee_.firstName)),
					conditionDTO.getEmployeeName().toUpperCase()), cb.like(
					cb.upper(employeeJoin.get(Employee_.lastName)),
					conditionDTO.getEmployeeName().toUpperCase())));
		}

		restriction = cb.and(
				restriction,
				cb.equal(companyJoin.get(Company_.companyId),
						conditionDTO.getCompanyId()));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return typedQuery.getSingleResult();
	}

	@Override
	public EmployeeHolidayCalendar isAssignedEmpExist(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHolidayCalendar> criteriaQuery = cb
				.createQuery(EmployeeHolidayCalendar.class);
		Root<EmployeeHolidayCalendar> calendarRoot = criteriaQuery
				.from(EmployeeHolidayCalendar.class);
		criteriaQuery.select(calendarRoot);

		Join<EmployeeHolidayCalendar, Employee> employeeJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeHolidayCalendar> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList().get(0);
		}
		return null;
	}

	@Override
	public EmployeeHolidayCalendar findByEmpId(Long employeeId, Long companyId,
			int year) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHolidayCalendar> criteriaQuery = cb
				.createQuery(EmployeeHolidayCalendar.class);
		Root<EmployeeHolidayCalendar> calendarRoot = criteriaQuery
				.from(EmployeeHolidayCalendar.class);
		criteriaQuery.select(calendarRoot);

		Join<EmployeeHolidayCalendar, Employee> employeeJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.employee);
		Join<EmployeeHolidayCalendar, CompanyHolidayCalendar> calJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.companyHolidayCalendar);
		Join<CompanyHolidayCalendar, Company> companyJoin = calJoin
				.join(CompanyHolidayCalendar_.company);
		Join<CompanyHolidayCalendar, CompanyHolidayCalendarDetail> calDetailsJoin = calJoin
				.join(CompanyHolidayCalendar_.companyHolidayCalendarDetails);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(cb.function("year",
				Integer.class,
				calDetailsJoin.get(CompanyHolidayCalendarDetail_.holidayDate)),
				year));
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeHolidayCalendar> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList().get(0);
		}
		return null;
	}

	@Override
	public EmployeeHolidayCalendar getCalendarDetail(Long employeeId,
			Date startDate, Date endDate) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHolidayCalendar> criteriaQuery = cb
				.createQuery(EmployeeHolidayCalendar.class);
		Root<EmployeeHolidayCalendar> calendarRoot = criteriaQuery
				.from(EmployeeHolidayCalendar.class);
		criteriaQuery.select(calendarRoot);

		Join<EmployeeHolidayCalendar, Employee> employeeJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.employee);
		Join<EmployeeHolidayCalendar, CompanyHolidayCalendar> calJoin = calendarRoot
				.join(EmployeeHolidayCalendar_.companyHolidayCalendar);
		Join<CompanyHolidayCalendar, Company> companyJoin = calJoin
				.join(CompanyHolidayCalendar_.company);
		Join<CompanyHolidayCalendar, CompanyHolidayCalendarDetail> calDetailsJoin = calJoin
				.join(CompanyHolidayCalendar_.companyHolidayCalendarDetails);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(
				restriction,
				cb.greaterThanOrEqualTo(
						calDetailsJoin.get(
								CompanyHolidayCalendarDetail_.holidayDate).as(
								Date.class), startDate));
		restriction = cb.and(
				restriction,
				cb.lessThanOrEqualTo(
						calDetailsJoin.get(
								CompanyHolidayCalendarDetail_.holidayDate).as(
								Date.class), endDate));
		criteriaQuery.where(restriction).orderBy(
				cb.asc(calDetailsJoin
						.get(CompanyHolidayCalendarDetail_.holidayDate)));

		TypedQuery<EmployeeHolidayCalendar> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList().get(0);
		}
		return null;
	}

	@Override
	public EmployeeHolidayCalendar findByID(Long employeeHolidayCalendarId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeHolidayCalendar> criteriaQuery = cb.createQuery(EmployeeHolidayCalendar.class);
		Root<EmployeeHolidayCalendar> employeeHolidayCalendarRoot = criteriaQuery
				.from(EmployeeHolidayCalendar.class);

		/*criteriaQuery.select(cb.count(companyHolidayCalendarRoot).as(
				CompanyHolidayCalendarDetail.class));*/
		criteriaQuery.select(employeeHolidayCalendarRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(employeeHolidayCalendarRoot
				.get(EmployeeHolidayCalendar_.employeeHolidayCalendarId),
				employeeHolidayCalendarId));
		
		restriction = cb.and(restriction, cb.equal(employeeHolidayCalendarRoot
				.get(EmployeeHolidayCalendar_.companyId),
				companyId));
		
		criteriaQuery.where(restriction);

		TypedQuery<EmployeeHolidayCalendar> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		
		List<EmployeeHolidayCalendar> calendarList = holidayCalTypedQuery
				.getResultList();

		return calendarList.get(0);
	}
}
