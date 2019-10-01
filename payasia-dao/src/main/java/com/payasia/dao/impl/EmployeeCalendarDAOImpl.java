package com.payasia.dao.impl;

import java.sql.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeCalendarDAO;
import com.payasia.dao.bean.EmployeeCalendar;
import com.payasia.dao.bean.EmployeeCalendarConfig;
import com.payasia.dao.bean.EmployeeCalendarConfig_;
import com.payasia.dao.bean.EmployeeCalendar_;

@Repository
public class EmployeeCalendarDAOImpl extends BaseDAO implements
		EmployeeCalendarDAO {

	@Override
	protected Object getBaseEntity() {
		EmployeeCalendar employeeCalendar = new EmployeeCalendar();
		return employeeCalendar;
	}

	@Override
	public void update(EmployeeCalendar employeeCalendar) {
		super.update(employeeCalendar);
	}

	@Override
	public void delete(EmployeeCalendar employeeCalendar) {
		super.delete(employeeCalendar);
	}

	@Override
	public void save(EmployeeCalendar employeeCalendar) {
		super.save(employeeCalendar);
	}

	@Override
	public EmployeeCalendar findByID(Long employeeCalendarId) {
		return super.findById(EmployeeCalendar.class, employeeCalendarId);
	}

	@Override
	public EmployeeCalendar findByDate(String eventDate, String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeCalendar> criteriaQuery = cb
				.createQuery(EmployeeCalendar.class);
		Root<EmployeeCalendar> calCodeRoot = criteriaQuery
				.from(EmployeeCalendar.class);
		criteriaQuery.select(calCodeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				calCodeRoot.get(EmployeeCalendar_.calendarDate).as(Date.class),
				DateUtils.stringToDate(eventDate,
						PayAsiaConstants.DEFAULT_DATE_FORMAT)));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeCalendar> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeCalendar> calCodeList = calCodeTypedQuery.getResultList();
		if (calCodeList != null &&  !calCodeList.isEmpty()) {
			return calCodeList.get(0);
		}
		return null;
	}

	@Override
	public EmployeeCalendar findByEmployeeCalConfigId(Long empCalConfigId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeCalendar> criteriaQuery = cb
				.createQuery(EmployeeCalendar.class);
		Root<EmployeeCalendar> empCalRoot = criteriaQuery
				.from(EmployeeCalendar.class);
		criteriaQuery.select(empCalRoot);
		Join<EmployeeCalendar, EmployeeCalendarConfig> empConfigJoin = empCalRoot
				.join(EmployeeCalendar_.employeeCalendarConfig);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(empConfigJoin
				.get(EmployeeCalendarConfig_.employeeCalendarConfigId),
				empCalConfigId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeCalendar> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeeCalendar> calCodeList = calCodeTypedQuery.getResultList();
		if (calCodeList != null &&  !calCodeList.isEmpty()) {
			return calCodeList.get(0);
		}
		return null;
	}

}
