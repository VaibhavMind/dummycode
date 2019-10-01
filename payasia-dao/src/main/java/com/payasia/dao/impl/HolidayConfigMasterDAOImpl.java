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

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.HolidayConfigMasterDAO;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.CountryMaster_;
import com.payasia.dao.bean.HolidayConfigMaster;
import com.payasia.dao.bean.HolidayConfigMaster_;
import com.payasia.dao.bean.StateMaster;
import com.payasia.dao.bean.StateMaster_;

/**
 * The Class HolidayConfigMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class HolidayConfigMasterDAOImpl extends BaseDAO implements
		HolidayConfigMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		HolidayConfigMaster holidayConfigMaster = new HolidayConfigMaster();
		return holidayConfigMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#getYearList()
	 */
	@Override
	public List<Integer> getYearList() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<HolidayConfigMaster> holidayRoot = criteriaQuery
				.from(HolidayConfigMaster.class);
		criteriaQuery.select(
				cb.function("year", Integer.class,
						holidayRoot.get(HolidayConfigMaster_.holidayDate)))
				.distinct(true);
		criteriaQuery.orderBy(cb.asc(cb.function("year", Integer.class,
				holidayRoot.get(HolidayConfigMaster_.holidayDate))));

		TypedQuery<Integer> yearTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<Integer> yearList = yearTypedQuery.getResultList();
		return yearList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.HolidayConfigMasterDAO#getHolidayListCount(java.lang.
	 * Long, java.lang.Long, int)
	 */
	@Override
	public int getHolidayListCount(String countryId, String stateId, String year) {
		return findByCondition(null, null, countryId, stateId, year).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.HolidayConfigMasterDAO#findByCondition(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition, java.lang.Long,
	 * java.lang.Long, int)
	 */
	@Override
	public List<HolidayConfigMaster> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, String countryId, String stateId, String year) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HolidayConfigMaster> criteriaQuery = cb
				.createQuery(HolidayConfigMaster.class);
		Root<HolidayConfigMaster> holidayRoot = criteriaQuery
				.from(HolidayConfigMaster.class);

		criteriaQuery.select(holidayRoot);

		Join<HolidayConfigMaster, CountryMaster> holidayRootCountryJoin = holidayRoot
				.join(HolidayConfigMaster_.countryMaster);
		Join<HolidayConfigMaster, StateMaster> holidayRootStateJoin = holidayRoot
				.join(HolidayConfigMaster_.stateMaster);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(countryId)) {
			restriction = cb.and(restriction, cb.equal(
					holidayRootCountryJoin.get(CountryMaster_.countryId),
					Long.parseLong(countryId)));
		}

		if (StringUtils.isNotBlank(stateId)) {
			restriction = cb.and(restriction, cb.equal(
					holidayRootStateJoin.get(StateMaster_.stateId),
					Long.parseLong(stateId)));
		}
		if (StringUtils.isNotBlank(year)) {
			restriction = cb.and(restriction, cb.equal(
					cb.function("year", Integer.class,
							holidayRoot.get(HolidayConfigMaster_.holidayDate)),
					Integer.parseInt(year)));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForHoliday(sortDTO, holidayRoot);
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

		TypedQuery<HolidayConfigMaster> holidayTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			holidayTypedQuery.setFirstResult(getStartPosition(pageDTO));
			holidayTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<HolidayConfigMaster> allHolidayList = holidayTypedQuery
				.getResultList();

		return allHolidayList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.HolidayConfigMasterDAO#getSortPathForHoliday(com.payasia
	 * .common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForHoliday(SortCondition sortDTO,
			Root<HolidayConfigMaster> holidayRoot) {

		List<String> holidayIsColList = new ArrayList<String>();
		holidayIsColList.add(SortConstants.HOLIDAY_MASTER_DAY);
		holidayIsColList.add(SortConstants.HOLIDAY_MASTER_DATE);
		holidayIsColList.add(SortConstants.HOLIDAY_MASTER_OCCASSION);
		holidayIsColList.add(SortConstants.HOLIDAY_MASTER_COUNTRY_NAME);
		holidayIsColList.add(SortConstants.HOLIDAY_MASTER_STATE_NAME);

		holidayIsColList.add(SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DATE);
		holidayIsColList.add(SortConstants.HOLIDAY_CALENDAR_HOLIDAY_DESC);
		Path<String> sortPath = null;

		if (holidayIsColList.contains(sortDTO.getColumnName())) {
			sortPath = holidayRoot.get(colMap.get(HolidayConfigMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#findByID(java.lang.Long)
	 */
	@Override
	public HolidayConfigMaster findByID(Long holidayId) {
		return super.findById(HolidayConfigMaster.class, holidayId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#save(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void save(HolidayConfigMaster holidayConfigMaster) {
		super.save(holidayConfigMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#update(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void update(HolidayConfigMaster holidayConfigMaster) {
		super.update(holidayConfigMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.HolidayConfigMasterDAO#delete(com.payasia.dao.bean.
	 * HolidayConfigMaster)
	 */
	@Override
	public void delete(HolidayConfigMaster holidayConfigMaster) {
		super.delete(holidayConfigMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.HolidayConfigMasterDAO#findByOccasionAndDate(java.lang
	 * .Long, java.lang.Long, java.lang.Long, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public HolidayConfigMaster findByOccasionAndDate(Long holidayId,
			Long countryId, Long stateId, String occasion, String holidayDate,
			String dateFormat) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HolidayConfigMaster> criteriaQuery = cb
				.createQuery(HolidayConfigMaster.class);
		Root<HolidayConfigMaster> holidayRoot = criteriaQuery
				.from(HolidayConfigMaster.class);

		criteriaQuery.select(holidayRoot);

		Join<HolidayConfigMaster, CountryMaster> holidayRootCountryJoin = holidayRoot
				.join(HolidayConfigMaster_.countryMaster);
		Join<HolidayConfigMaster, StateMaster> holidayRootStateJoin = holidayRoot
				.join(HolidayConfigMaster_.stateMaster);

		Predicate restriction = cb.conjunction();

		if (holidayId != null) {
			restriction = cb.and(restriction,
					cb.notEqual(holidayRoot
							.get(HolidayConfigMaster_.holidayConfigMasterId),
							holidayId));
		}

		restriction = cb.and(restriction,
				cb.equal(holidayRootCountryJoin.get(CountryMaster_.countryId),
						countryId));

		restriction = cb.and(restriction, cb.equal(
				holidayRootStateJoin.get(StateMaster_.stateId), stateId));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(holidayRoot.get(HolidayConfigMaster_.holidayDesc)),
				occasion.toUpperCase()));
		restriction = cb.and(restriction, cb.equal(
				holidayRoot.get(HolidayConfigMaster_.holidayDate),
				DateUtils.stringToTimestamp(holidayDate, dateFormat)));

		criteriaQuery.where(restriction);

		TypedQuery<HolidayConfigMaster> holidayTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<HolidayConfigMaster> holidayList = holidayTypedQuery
				.getResultList();
		if (holidayList != null &&  !holidayList.isEmpty()) {
			return holidayList.get(0);
		}
		return null;

	}
	@Override
	public HolidayConfigMaster findByID(Long holidayId,Long companyId)
	{
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HolidayConfigMaster> criteriaQuery = cb
				.createQuery(HolidayConfigMaster.class);
		Root<HolidayConfigMaster> holidayRoot = criteriaQuery
				.from(HolidayConfigMaster.class);

		criteriaQuery.select(holidayRoot);
		
		Predicate restriction = cb.conjunction();

		if (holidayId != null) {
			restriction = cb.and(restriction,
					cb.equal(holidayRoot
							.get(HolidayConfigMaster_.holidayConfigMasterId),
							holidayId));
		}
  
	    /**
	     * This code & check comment as per discussion with Mr.Surya  
	     * */		
		/*restriction = cb.and(restriction,
				cb.equal(holidayRoot.get(HolidayConfigMaster_.companyId),
				companyId));
		*/

		criteriaQuery.where(restriction);

		TypedQuery<HolidayConfigMaster> holidayTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<HolidayConfigMaster> holidayList = holidayTypedQuery
				.getResultList();
		if (holidayList != null &&  !holidayList.isEmpty()) {
			return holidayList.get(0);
		}
		return null;
		
	}

}
