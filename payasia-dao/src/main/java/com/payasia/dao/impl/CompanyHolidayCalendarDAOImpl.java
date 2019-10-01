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

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.HolidayCalendarConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyHolidayCalendarDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendar_;
import com.payasia.dao.bean.Company_;

@Repository
public class CompanyHolidayCalendarDAOImpl extends BaseDAO implements
		CompanyHolidayCalendarDAO {

	@Override
	protected Object getBaseEntity() {
		CompanyHolidayCalendar companyHolidayCalendar = new CompanyHolidayCalendar();
		return companyHolidayCalendar;
	}

	@Override
	public CompanyHolidayCalendar findByID(Long companyHolidayCalendarId) {
		return super.findById(CompanyHolidayCalendar.class,
				companyHolidayCalendarId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#save(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void save(CompanyHolidayCalendar companyHolidayCalendar) {
		super.save(companyHolidayCalendar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#update(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void update(CompanyHolidayCalendar companyHolidayCalendar) {
		super.update(companyHolidayCalendar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#delete(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void delete(CompanyHolidayCalendar companyHolidayCalendar) {
		super.delete(companyHolidayCalendar);

	}

	@Override
	public List<CompanyHolidayCalendar> findByCondition(
			HolidayCalendarConditionDTO conditionDTO, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyHolidayCalendar> criteriaQuery = cb
				.createQuery(CompanyHolidayCalendar.class);
		Root<CompanyHolidayCalendar> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendar.class);

		criteriaQuery.select(companyHolidayCalendarRoot);

		Join<CompanyHolidayCalendar, Company> companyJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendar_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCalName())) {

			restriction = cb.and(restriction, cb.like(cb
					.upper(companyHolidayCalendarRoot
							.get(CompanyHolidayCalendar_.calendarName)),
					conditionDTO.getCalName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCalDesc())) {

			restriction = cb.and(restriction, cb.like(cb
					.upper(companyHolidayCalendarRoot
							.get(CompanyHolidayCalendar_.calendarDesc)),
					conditionDTO.getCalDesc().toUpperCase() + '%'));
		}

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
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

		TypedQuery<CompanyHolidayCalendar> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			holidayCalTypedQuery.setFirstResult(getStartPosition(pageDTO));
			holidayCalTypedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<CompanyHolidayCalendar> calendarList = holidayCalTypedQuery
				.getResultList();

		return calendarList;
	}

	public Path<String> getSortPathForHolidayCal(SortCondition sortDTO,
			Root<CompanyHolidayCalendar> companyHolidayCalendarRoot) {

		List<String> holIsColList = new ArrayList<String>();
		holIsColList.add(SortConstants.HOLIDAY_CALENDAR_NAME);
		holIsColList.add(SortConstants.HOLIDAY_CALENDAR_DESC);

		Path<String> sortPath = null;

		if (holIsColList.contains(sortDTO.getColumnName())) {
			sortPath = companyHolidayCalendarRoot
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
		Root<CompanyHolidayCalendar> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendar.class);

		criteriaQuery.select(cb.count(companyHolidayCalendarRoot).as(
				Integer.class));

		Join<CompanyHolidayCalendar, Company> companyJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendar_.company);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getCalName())) {

			restriction = cb.and(restriction, cb.like(cb
					.upper(companyHolidayCalendarRoot
							.get(CompanyHolidayCalendar_.calendarName)),
					conditionDTO.getCalName().toUpperCase() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCalDesc())) {

			restriction = cb.and(restriction, cb.like(cb
					.upper(companyHolidayCalendarRoot
							.get(CompanyHolidayCalendar_.calendarDesc)),
					conditionDTO.getCalDesc().toUpperCase() + '%'));
		}

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		Integer count = holidayCalTypedQuery.getSingleResult();

		return count;
	}

	@Override
	public CompanyHolidayCalendar checkDuplicateCalendar(Long companyId,
			String holidayCalName, Long holidayCalId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyHolidayCalendar> criteriaQuery = cb
				.createQuery(CompanyHolidayCalendar.class);
		Root<CompanyHolidayCalendar> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendar.class);

		criteriaQuery.select(companyHolidayCalendarRoot);

		Join<CompanyHolidayCalendar, Company> companyJoin = companyHolidayCalendarRoot
				.join(CompanyHolidayCalendar_.company);

		Predicate restriction = cb.conjunction();
		if (holidayCalId != null) {
			restriction = cb
					.and(restriction,
							cb.notEqual(
									companyHolidayCalendarRoot
											.get(CompanyHolidayCalendar_.companyHolidayCalendarId),
									holidayCalId));
		}

		restriction = cb.and(restriction, cb.equal(cb
				.upper(companyHolidayCalendarRoot
						.get(CompanyHolidayCalendar_.calendarName)),
				holidayCalName.toUpperCase()));

		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<CompanyHolidayCalendar> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyHolidayCalendar> calendarList = holidayCalTypedQuery
				.getResultList();
		if (!calendarList.isEmpty()) {
			return calendarList.get(0);
		}
		return null;
	}

	@Override
	public CompanyHolidayCalendar findByID(Long companyHolidayCalendarId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyHolidayCalendar> criteriaQuery = cb.createQuery(CompanyHolidayCalendar.class);
		Root<CompanyHolidayCalendar> companyHolidayCalendarRoot = criteriaQuery
				.from(CompanyHolidayCalendar.class);

		criteriaQuery.select(companyHolidayCalendarRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyHolidayCalendarRoot
				.get(CompanyHolidayCalendar_.companyHolidayCalendarId),
				companyHolidayCalendarId));
		
		restriction = cb.and(restriction, cb.equal(companyHolidayCalendarRoot
				.get(CompanyHolidayCalendar_.company),
				companyId));
		
		criteriaQuery.where(restriction);

		TypedQuery<CompanyHolidayCalendar> holidayCalTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		
		List<CompanyHolidayCalendar> calendarList = holidayCalTypedQuery
				.getResultList();

		return calendarList.get(0);
	}
}
