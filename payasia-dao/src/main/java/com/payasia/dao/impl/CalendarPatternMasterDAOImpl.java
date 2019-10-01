package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
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

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CalendarPatternMasterDAO;
import com.payasia.dao.bean.CalendarPatternMaster;
import com.payasia.dao.bean.CalendarPatternMaster_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;

@Repository
public class CalendarPatternMasterDAOImpl extends BaseDAO implements
		CalendarPatternMasterDAO {

	@Override
	protected Object getBaseEntity() {
		CalendarPatternMaster calendarPatternMaster = new CalendarPatternMaster();
		return calendarPatternMaster;
	}

	@Override
	public List<CalendarPatternMaster> findByConditionCompany(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CalendarPatternMaster> criteriaQuery = cb
				.createQuery(CalendarPatternMaster.class);
		Root<CalendarPatternMaster> calPatternRoot = criteriaQuery
				.from(CalendarPatternMaster.class);
		criteriaQuery.select(calPatternRoot);

		Join<CalendarPatternMaster, Company> companyJoin = calPatternRoot
				.join(CalendarPatternMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForCalendarPattern(sortDTO,
					calPatternRoot);
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

		TypedQuery<CalendarPatternMaster> calPatternTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			calPatternTypedQuery.setFirstResult(getStartPosition(pageDTO));
			calPatternTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return calPatternTypedQuery.getResultList();
	}

	private Path<String> getSortPathForCalendarPattern(SortCondition sortDTO,
			Root<CalendarPatternMaster> calPatternRoot) {
		List<String> calPatternIsColList = new ArrayList<String>();
		calPatternIsColList.add(SortConstants.CALENDAR_PATTERN_NAME);
		calPatternIsColList.add(SortConstants.CALENDAR_PATTERN_DESC);

		Path<String> sortPath = null;

		if (calPatternIsColList.contains(sortDTO.getColumnName())) {
			sortPath = calPatternRoot
					.get(colMap.get(CalendarPatternMaster.class
							+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Long getCountForConditionCompany(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<CalendarPatternMaster> calPatternRoot = criteriaQuery
				.from(CalendarPatternMaster.class);
		criteriaQuery.select(cb.count(calPatternRoot));

		Join<CalendarPatternMaster, Company> companyJoin = calPatternRoot
				.join(CalendarPatternMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return calCodeTypedQuery.getSingleResult();
	}

	@Override
	public void save(CalendarPatternMaster calendarPatternMaster) {
		super.save(calendarPatternMaster);
	}

	@Override
	public CalendarPatternMaster findById(Long calendarPatternMasterId) {
		return super.findById(CalendarPatternMaster.class,
				calendarPatternMasterId);
	}

	@Override
	public void update(CalendarPatternMaster calendarPatternMaster) {
		super.update(calendarPatternMaster);
	}

	@Override
	public void delete(CalendarPatternMaster calendarPatternMaster) {
		super.delete(calendarPatternMaster);
	}

	@Override
	public CalendarPatternMaster saveCalPatternMaster(
			CalendarPatternMaster calendarPatternMaster) {
		CalendarPatternMaster persistObj = calendarPatternMaster;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CalendarPatternMaster) getBaseEntity();
			beanUtil.copyProperties(persistObj, calendarPatternMaster);
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
	public CalendarPatternMaster findByPatternNameAndId(Long companyId,
			String calendarPattern, Long patternMasterId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CalendarPatternMaster> criteriaQuery = cb
				.createQuery(CalendarPatternMaster.class);
		Root<CalendarPatternMaster> calPatternRoot = criteriaQuery
				.from(CalendarPatternMaster.class);
		criteriaQuery.select(calPatternRoot);

		Join<CalendarPatternMaster, Company> companyJoin = calPatternRoot
				.join(CalendarPatternMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(cb.upper(calPatternRoot
				.get(CalendarPatternMaster_.patternName)), calendarPattern
				.toUpperCase()));
		if (patternMasterId != null) {
			restriction = cb.and(restriction, cb.notEqual(calPatternRoot
					.get(CalendarPatternMaster_.calendarPatternId),
					patternMasterId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<CalendarPatternMaster> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CalendarPatternMaster> calCodeList = calCodeTypedQuery
				.getResultList();
		if (calCodeList != null && !calCodeList.isEmpty()) {
			return calCodeList.get(0);
		}
		return null;
	}

	@Override
	public CalendarPatternMaster findByPatternName(Long companyId,
			String calendarPattern) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CalendarPatternMaster> criteriaQuery = cb
				.createQuery(CalendarPatternMaster.class);
		Root<CalendarPatternMaster> calPatternRoot = criteriaQuery
				.from(CalendarPatternMaster.class);
		criteriaQuery.select(calPatternRoot);

		Join<CalendarPatternMaster, Company> companyJoin = calPatternRoot
				.join(CalendarPatternMaster_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(cb.upper(calPatternRoot
				.get(CalendarPatternMaster_.patternName)), calendarPattern
				.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<CalendarPatternMaster> calCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return calCodeTypedQuery.getSingleResult();
	}

}
