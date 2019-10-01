package com.payasia.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
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

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyHolidayCalendarDetailDAO;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail_;
import com.payasia.dao.bean.CompanyHolidayCalendar_;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.StateMaster;

@Repository
public class CompanyHolidayCalendarDetailDAOImpl extends BaseDAO implements
		CompanyHolidayCalendarDetailDAO {

	@Override
	protected Object getBaseEntity() {
		CompanyHolidayCalendarDetail companyHolidayCalendarDetail = new CompanyHolidayCalendarDetail();
		return companyHolidayCalendarDetail;
	}

	@Override
	public CompanyHolidayCalendarDetail findByID(
			Long companyHolidayCalendarDetailId) {
		return super.findById(CompanyHolidayCalendarDetail.class,
				companyHolidayCalendarDetailId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#save(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void save(CompanyHolidayCalendarDetail companyHolidayCalendarDetail) {
		super.save(companyHolidayCalendarDetail);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#update(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void update(CompanyHolidayCalendarDetail companyHolidayCalendarDetail) {
		super.update(companyHolidayCalendarDetail);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#delete(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void delete(CompanyHolidayCalendarDetail companyHolidayCalendarDetail) {
		super.delete(companyHolidayCalendarDetail);

	}

	@Override
	public List<CompanyHolidayCalendarDetail> findByCondition(
			Long holidayCalId, Long companyId, int year, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyHolidayCalendarDetail> criteriaQuery = cb
				.createQuery(CompanyHolidayCalendarDetail.class);
		Root<CompanyHolidayCalendarDetail> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendarDetail.class);

		criteriaQuery.select(companyHolidayCalendarRoot);

		Join<CompanyHolidayCalendarDetail, CompanyHolidayCalendar> companyHolidayCalJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendarDetail_.companyHolidayCalendar);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyHolidayCalJoin
				.get(CompanyHolidayCalendar_.companyHolidayCalendarId),
				holidayCalId));

		restriction = cb
				.and(restriction,
						cb.equal(
								cb.function(
										"year",
										Integer.class,
										companyHolidayCalendarRoot
												.get(CompanyHolidayCalendarDetail_.holidayDate)),
								year));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForHolidayCal(sortDTO,
					companyHolidayCalendarRoot);
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

		TypedQuery<CompanyHolidayCalendarDetail> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			holidayCalTypedQuery.setFirstResult(getStartPosition(pageDTO));
			holidayCalTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<CompanyHolidayCalendarDetail> calendarList = holidayCalTypedQuery
				.getResultList();

		return calendarList;
	}

	public Path<String> getSortPathForHolidayCal(SortCondition sortDTO,
			Root<CompanyHolidayCalendarDetail> companyHolidayCalendarRoot) {
		Join<CompanyHolidayCalendarDetail, CountryMaster> countryJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendarDetail_.countryMaster);
		Join<CompanyHolidayCalendarDetail, StateMaster> stateJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendarDetail_.stateMaster);
		List<String> countryIsColList = new ArrayList<String>();
		countryIsColList.add(SortConstants.HOLIDAY_CALENDAR_COUNTRY_NAME);
		List<String> stateIsColList = new ArrayList<String>();
		stateIsColList.add(SortConstants.HOLIDAY_CALENDAR_STATE_NAME);
		List<String> holIsColList = new ArrayList<String>();
		holIsColList.add(SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DATE);
		holIsColList.add(SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DESC);

		Path<String> sortPath = null;

		if (countryIsColList.contains(sortDTO.getColumnName())) {
			sortPath = countryJoin.get(colMap.get(CountryMaster.class
					+ sortDTO.getColumnName()));
		}
		if (stateIsColList.contains(sortDTO.getColumnName())) {
			sortPath = stateJoin.get(colMap.get(StateMaster.class
					+ sortDTO.getColumnName()));
		}
		if (holIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyHolidayCalendarRoot.get(colMap
					.get(CompanyHolidayCalendarDetail.class
							+ sortDTO.getColumnName()));
		}
		if (StringUtils.isBlank(sortDTO.getColumnName())) {
			sortPath = companyHolidayCalendarRoot.get(colMap
					.get(CompanyHolidayCalendarDetail.class
							+ SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DATE));
		}
		return sortPath;
	}

	@Override
	public int getCountForCondition(Long holidayCalId, Long companyId, int year) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyHolidayCalendarDetail> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendarDetail.class);

		criteriaQuery.select(cb.count(companyHolidayCalendarRoot).as(
				Integer.class));

		Join<CompanyHolidayCalendarDetail, CompanyHolidayCalendar> companyHolidayCalJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendarDetail_.companyHolidayCalendar);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyHolidayCalJoin
				.get(CompanyHolidayCalendar_.companyHolidayCalendarId),
				holidayCalId));
		
		restriction = cb.and(restriction, cb.equal(companyHolidayCalJoin
				.get(CompanyHolidayCalendar_.company),
				companyId));

		restriction = cb
				.and(restriction,
						cb.equal(
								cb.function(
										"year",
										Integer.class,
										companyHolidayCalendarRoot
												.get(CompanyHolidayCalendarDetail_.holidayDate)),
								year));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return holidayCalTypedQuery.getSingleResult();
	}

	@Override
	public CompanyHolidayCalendarDetail findByDateAndCalId(
			Long compaHoliCalDetailId, Long holidayCalId, Timestamp holidayDate) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyHolidayCalendarDetail> criteriaQuery = cb
				.createQuery(CompanyHolidayCalendarDetail.class);
		Root<CompanyHolidayCalendarDetail> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendarDetail.class);

		criteriaQuery.select(companyHolidayCalendarRoot);

		Join<CompanyHolidayCalendarDetail, CompanyHolidayCalendar> companyHolidayCalJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendarDetail_.companyHolidayCalendar);
		Predicate restriction = cb.conjunction();

		if (compaHoliCalDetailId != null) {
			restriction = cb
					.and(restriction,
							cb.notEqual(
									companyHolidayCalendarRoot
											.get(CompanyHolidayCalendarDetail_.companyHolidayCalendarDetailId),
									compaHoliCalDetailId));
		}
		if (holidayCalId != null) {
			restriction = cb.and(restriction, cb.equal(companyHolidayCalJoin
					.get(CompanyHolidayCalendar_.companyHolidayCalendarId),
					holidayCalId));
		}

		restriction = cb.and(restriction, cb.equal(companyHolidayCalendarRoot
				.get(CompanyHolidayCalendarDetail_.holidayDate), holidayDate));
		criteriaQuery.where(restriction);

		TypedQuery<CompanyHolidayCalendarDetail> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyHolidayCalendarDetail> calendarList = holidayCalTypedQuery
				.getResultList();
		if (!calendarList.isEmpty()) {
			return calendarList.get(0);
		}
		return null;
	}

	@Override
	public Integer getHolidayCountByYear(Long holidayCalId, int year) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyHolidayCalendarDetail> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendarDetail.class);

		criteriaQuery.select(cb.count(companyHolidayCalendarRoot).as(
				Integer.class));

		Join<CompanyHolidayCalendarDetail, CompanyHolidayCalendar> companyHolidayCalJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendarDetail_.companyHolidayCalendar);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyHolidayCalJoin
				.get(CompanyHolidayCalendar_.companyHolidayCalendarId),
				holidayCalId));
		restriction = cb
				.and(restriction,
						cb.equal(
								cb.function(
										"year",
										Integer.class,
										companyHolidayCalendarRoot
												.get(CompanyHolidayCalendarDetail_.holidayDate)),
								year));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return holidayCalTypedQuery.getSingleResult();
	}

	@Override
	public CompanyHolidayCalendarDetail findByID(Long companyHolidayCalendarDetailId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyHolidayCalendarDetail> criteriaQuery = cb.createQuery(CompanyHolidayCalendarDetail.class);
		Root<CompanyHolidayCalendarDetail> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendarDetail.class);

		/*criteriaQuery.select(cb.count(companyHolidayCalendarRoot).as(
				CompanyHolidayCalendarDetail.class));*/
		criteriaQuery.select(companyHolidayCalendarRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyHolidayCalendarRoot
				.get(CompanyHolidayCalendarDetail_.companyHolidayCalendarDetailId),
				companyHolidayCalendarDetailId));
		
		restriction = cb.and(restriction, cb.equal(companyHolidayCalendarRoot
				.get(CompanyHolidayCalendarDetail_.companyId),
				companyId));
		
		criteriaQuery.where(restriction);

		TypedQuery<CompanyHolidayCalendarDetail> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		
		List<CompanyHolidayCalendarDetail> calendarList = holidayCalTypedQuery
				.getResultList();

		return calendarList.get(0);
	}
}
