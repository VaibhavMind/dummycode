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
import com.payasia.dao.CompanyCalendarDAO;
import com.payasia.dao.bean.CompanyCalendar;
import com.payasia.dao.bean.CompanyCalendarTemplate;
import com.payasia.dao.bean.CompanyCalendarTemplate_;
import com.payasia.dao.bean.CompanyCalendar_;

@Repository
public class CompanyCalendarDAOImpl extends BaseDAO implements
		CompanyCalendarDAO {

	@Override
	protected Object getBaseEntity() {
		CompanyCalendar companyCalendar = new CompanyCalendar();
		return companyCalendar;
	}

	@Override
	public void update(CompanyCalendar companyCalendar) {
		super.update(companyCalendar);
	}

	@Override
	public void delete(CompanyCalendar companyCalendar) {
		super.delete(companyCalendar);
	}

	@Override
	public void save(CompanyCalendar calTempMaster) {
		super.save(calTempMaster);
	}

	@Override
	public CompanyCalendar findByID(Long companyCalendarId) {
		return super.findById(CompanyCalendar.class, companyCalendarId);
	}

	@Override
	public CompanyCalendar findByDate(String eventDate, String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyCalendar> criteriaQuery = cb
				.createQuery(CompanyCalendar.class);
		Root<CompanyCalendar> calCodeRoot = criteriaQuery
				.from(CompanyCalendar.class);
		criteriaQuery.select(calCodeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				calCodeRoot.get(CompanyCalendar_.calendarDate).as(Date.class),
				DateUtils.stringToDate(eventDate,
						PayAsiaConstants.DEFAULT_DATE_FORMAT)));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyCalendar> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyCalendar> calCodeList = calCodeTypedQuery.getResultList();
		if (calCodeList != null && !calCodeList.isEmpty()) {
			return calCodeList.get(0);
		}
		return null;
	}

	@Override
	public CompanyCalendar findByCompanyCalTemlateId(Long CompanyCalTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyCalendar> criteriaQuery = cb
				.createQuery(CompanyCalendar.class);
		Root<CompanyCalendar> calRoot = criteriaQuery
				.from(CompanyCalendar.class);
		criteriaQuery.select(calRoot);
		Join<CompanyCalendar, CompanyCalendarTemplate> calTemJoin = calRoot
				.join(CompanyCalendar_.companyCalendarTemplate);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(calTemJoin
				.get(CompanyCalendarTemplate_.companyCalendarTemplateId),
				CompanyCalTemplateId));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyCalendar> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyCalendar> calCodeList = calCodeTypedQuery.getResultList();
		if (calCodeList != null && !calCodeList.isEmpty()) {
			return calCodeList.get(0);
		}
		return null;
	}
}
