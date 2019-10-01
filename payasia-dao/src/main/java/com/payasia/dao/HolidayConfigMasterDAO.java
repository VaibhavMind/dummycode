package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.HolidayConfigMaster;

/**
 * The Interface HolidayConfigMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface HolidayConfigMasterDAO {

	/**
	 * Gets the year list.
	 * 
	 * @return the year list
	 */
	List<Integer> getYearList();

	/**
	 * Gets the holiday list count By countryId ,stateId and year.
	 * 
	 * @param countryId
	 *            the country id
	 * @param stateId
	 *            the state id
	 * @param year
	 *            the year
	 * @return the holiday list count
	 */
	int getHolidayListCount(String countryId, String stateId, String year);

	/**
	 * Find HolidayConfigMaster Objects List by condition By countryId ,stateId
	 * and year.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param countryId
	 *            the country id
	 * @param stateId
	 *            the state id
	 * @param year
	 *            the year
	 * @return the list
	 */
	List<HolidayConfigMaster> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, String countryId, String stateId, String year);

	/**
	 * Gets the sort path for holiday.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param holidayRoot
	 *            the holiday root
	 * @return the sort path for holiday
	 */
	Path<String> getSortPathForHoliday(SortCondition sortDTO,
			Root<HolidayConfigMaster> holidayRoot);

	/**
	 * Find HolidayConfigMaster Object by holidayId.
	 * 
	 * @param holidayId
	 *            the holiday id
	 * @return the holiday config master
	 */
	HolidayConfigMaster findByID(Long holidayId);

	/**
	 * Save HolidayConfigMaster Object.
	 * 
	 * @param holidayConfigMaster
	 *            the holiday config master
	 */
	void save(HolidayConfigMaster holidayConfigMaster);

	/**
	 * Update HolidayConfigMaster Object.
	 * 
	 * @param holidayConfigMaster
	 *            the holiday config master
	 */
	void update(HolidayConfigMaster holidayConfigMaster);

	/**
	 * Delete HolidayConfigMaster Object.
	 * 
	 * @param holidayConfigMaster
	 *            the holiday config master
	 */
	void delete(HolidayConfigMaster holidayConfigMaster);

	/**
	 * Find HolidayConfigMaster Object by occasion , date,holidayId ,countryId
	 * and stateId.
	 * 
	 * @param holidayId
	 *            the holiday id
	 * @param countryId
	 *            the country id
	 * @param stateId
	 *            the state id
	 * @param occasion
	 *            the occasion
	 * @param holidayDate
	 *            the holiday date
	 * @param dateFormat
	 *            the date format
	 * @return the holiday config master
	 */
	HolidayConfigMaster findByOccasionAndDate(Long holidayId, Long countryId,
			Long stateId, String occasion, String holidayDate, String dateFormat);
	
	HolidayConfigMaster findByID(Long holidayId,Long companyId);

}
