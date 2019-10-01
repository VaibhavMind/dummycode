package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CalendarCodeMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.CalendarCodeMaster;
import com.payasia.dao.bean.CalendarCodeMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;

@Repository
public class CalendarCodeMasterDAOImpl extends BaseDAO implements
		CalendarCodeMasterDAO {

	@Override
	protected Object getBaseEntity() {
		CalendarCodeMaster calendarCodeMaster = new CalendarCodeMaster();
		return calendarCodeMaster;
	}

	@Override
	public List<CalendarCodeMaster> findByConditionCompany(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CalendarCodeMaster> criteriaQuery = cb
				.createQuery(CalendarCodeMaster.class);
		Root<CalendarCodeMaster> calCodeRoot = criteriaQuery
				.from(CalendarCodeMaster.class);
		criteriaQuery.select(calCodeRoot);

		Join<CalendarCodeMaster, Company> companyJoin = calCodeRoot
				.join(CalendarCodeMaster_.company);

		Join<CalendarCodeMaster, AppCodeMaster> appCodeJoin = calCodeRoot
				.join(CalendarCodeMaster_.appCodeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForCalendarCode(sortDTO,
					calCodeRoot, appCodeJoin);
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

		TypedQuery<CalendarCodeMaster> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			calCodeTypedQuery.setFirstResult(getStartPosition(pageDTO));
			calCodeTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return calCodeTypedQuery.getResultList();
	}

	private Path<String> getSortPathForCalendarCode(SortCondition sortDTO,
			Root<CalendarCodeMaster> calCodeRoot,
			Join<CalendarCodeMaster, AppCodeMaster> appCodeJoin) {

		List<String> calCodeIsColList = new ArrayList<String>();
		calCodeIsColList.add(SortConstants.CALENDAR_CODE);

		List<String> calCodeValIsColList = new ArrayList<String>();
		calCodeValIsColList.add(SortConstants.CALENDAR_CODE_VALUE);

		Path<String> sortPath = null;

		if (calCodeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = calCodeRoot.get(colMap.get(CalendarCodeMaster.class
					+ sortDTO.getColumnName()));
		}
		if (calCodeValIsColList.contains(sortDTO.getColumnName())) {
			sortPath = appCodeJoin.get(colMap.get(AppCodeMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Long getCountForConditionCompany(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<CalendarCodeMaster> calCodeRoot = criteriaQuery
				.from(CalendarCodeMaster.class);
		criteriaQuery.select(cb.count(calCodeRoot));

		Join<CalendarCodeMaster, Company> companyJoin = calCodeRoot
				.join(CalendarCodeMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return calCodeTypedQuery.getSingleResult();
	}

	@Override
	public void save(CalendarCodeMaster calendarCodeMaster) {
		super.save(calendarCodeMaster);
	}

	@Override
	public CalendarCodeMaster findById(Long calendarCodeMasterId) {
		return super.findById(CalendarCodeMaster.class, calendarCodeMasterId);
	}

	@Override
	public void update(CalendarCodeMaster calendarCodeMaster) {
		super.update(calendarCodeMaster);
	}

	@Override
	public void delete(CalendarCodeMaster calendarCodeMaster) {
		super.delete(calendarCodeMaster);
	}

	@Override
	public CalendarCodeMaster findByCodeName(Long companyId, String calCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CalendarCodeMaster> criteriaQuery = cb
				.createQuery(CalendarCodeMaster.class);
		Root<CalendarCodeMaster> calCodeRoot = criteriaQuery
				.from(CalendarCodeMaster.class);
		criteriaQuery.select(calCodeRoot);

		Join<CalendarCodeMaster, Company> companyJoin = calCodeRoot
				.join(CalendarCodeMaster_.company);

		calCodeRoot.join(CalendarCodeMaster_.appCodeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				cb.upper(calCodeRoot.get(CalendarCodeMaster_.code)),
				calCode.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<CalendarCodeMaster> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return calCodeTypedQuery.getSingleResult();
	}

	@Override
	public CalendarCodeMaster findByCodeNameAndCalCodeMasterId(Long companyId,
			String calCode, Long calendarCodeMasterId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CalendarCodeMaster> criteriaQuery = cb
				.createQuery(CalendarCodeMaster.class);
		Root<CalendarCodeMaster> calCodeRoot = criteriaQuery
				.from(CalendarCodeMaster.class);
		criteriaQuery.select(calCodeRoot);

		Join<CalendarCodeMaster, Company> companyJoin = calCodeRoot
				.join(CalendarCodeMaster_.company);

		calCodeRoot.join(CalendarCodeMaster_.appCodeMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(
				cb.upper(calCodeRoot.get(CalendarCodeMaster_.code)),
				calCode.toUpperCase()));
		if (calendarCodeMasterId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					calCodeRoot.get(CalendarCodeMaster_.calendarCodeId),
					calendarCodeMasterId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<CalendarCodeMaster> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CalendarCodeMaster> calCodeList = calCodeTypedQuery
				.getResultList();
		if (calCodeList != null && !calCodeList.isEmpty()) {
			return calCodeList.get(0);
		}
		return null;
	}

}
