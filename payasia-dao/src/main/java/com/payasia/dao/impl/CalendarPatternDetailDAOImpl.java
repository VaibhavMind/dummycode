package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.CalendarPatternDetailDAO;
import com.payasia.dao.bean.CalendarPatternDetail;
import com.payasia.dao.bean.CalendarPatternDetail_;
import com.payasia.dao.bean.CalendarPatternMaster;
import com.payasia.dao.bean.CalendarPatternMaster_;

@Repository
public class CalendarPatternDetailDAOImpl extends BaseDAO implements
		CalendarPatternDetailDAO {

	@Override
	protected Object getBaseEntity() {
		CalendarPatternDetail calendarPatternDetail = new CalendarPatternDetail();
		return calendarPatternDetail;
	}

	@Override
	public void update(CalendarPatternDetail calendarPatternDetail) {
		super.update(calendarPatternDetail);
	}

	@Override
	public void delete(CalendarPatternDetail calendarPatternDetail) {
		super.delete(calendarPatternDetail);
	}

	@Override
	public void save(CalendarPatternDetail calendarPatternDetail) {
		super.save(calendarPatternDetail);
	}

	@Override
	public CalendarPatternDetail findByID(Long calendarPatternDetailId) {
		return super.findById(CalendarPatternDetail.class,
				calendarPatternDetailId);
	}

	@Override
	public CalendarPatternDetail findByCalPatternMasterId(
			Long calendarPatternMasterId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CalendarPatternDetail> criteriaQuery = cb
				.createQuery(CalendarPatternDetail.class);

		Root<CalendarPatternDetail> calTempRoot = criteriaQuery
				.from(CalendarPatternDetail.class);

		criteriaQuery.select(calTempRoot);

		Join<CalendarPatternDetail, CalendarPatternMaster> calPatternMasterJoin = calTempRoot
				.join(CalendarPatternDetail_.calendarPatternMaster);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((calPatternMasterJoin
				.get(CalendarPatternMaster_.calendarPatternId)),
				calendarPatternMasterId));

		criteriaQuery.where(restriction);

		TypedQuery<CalendarPatternDetail> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CalendarPatternDetail> calTempList = calTempTypedQuery
				.getResultList();
		if (calTempList != null && !calTempList.isEmpty()) {
			return calTempList.get(0);
		}
		return null;
	}

	@Override
	public List<CalendarPatternDetail> findListByCalPatternMasterId(
			Long calendarPatternMasterId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CalendarPatternDetail> criteriaQuery = cb
				.createQuery(CalendarPatternDetail.class);

		Root<CalendarPatternDetail> calTempRoot = criteriaQuery
				.from(CalendarPatternDetail.class);

		criteriaQuery.select(calTempRoot);

		Join<CalendarPatternDetail, CalendarPatternMaster> calPatternMasterJoin = calTempRoot
				.join(CalendarPatternDetail_.calendarPatternMaster);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal((calPatternMasterJoin
				.get(CalendarPatternMaster_.calendarPatternId)),
				calendarPatternMasterId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(calTempRoot
				.get(CalendarPatternDetail_.patternIndex)));

		TypedQuery<CalendarPatternDetail> calTempTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CalendarPatternDetail> calTempList = calTempTypedQuery
				.getResultList();
		return calTempList;
	}
}
