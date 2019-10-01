package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.HolidayListMasterForm;
import com.payasia.common.form.HolidayListMasterResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface HolidayListMasterLogic.
 */
@Transactional
public interface HolidayListMasterLogic {
	/**
	 * purpose : get Year List for Search.
	 * 
	 * @return yearList
	 */
	List<Integer> getYearList();

	/**
	 * purpose : get Country List.
	 * 
	 * @return HolidayListMasterForm contains Country list
	 */
	List<HolidayListMasterForm> getCountryList();

	/**
	 * purpose : get State List.
	 * 
	 * @param Long
	 *            the countryId
	 * @return HolidayListMasterForm contains state list
	 */
	List<HolidayListMasterForm> getStateList(Long countryId);

	/**
	 * purpose : delete Holiday .
	 * 
	 * @param Long
	 *            the holidayId
	 * @return
	 */
	List<Integer> deleteHolidayMaster(Long holidayId,Long companyId);

	/**
	 * purpose : get HolidayList From Excel File and Save to database.
	 * 
	 * @param holidayListMasterForm
	 *            the holiday list master form
	 * @param companyId
	 *            the company id
	 * @return the holiday list from xl
	 */
	HolidayListMasterForm getHolidayListFromXL(
			HolidayListMasterForm holidayListMasterForm, Long companyId);

	/**
	 * purpose : Get Holiday List.
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
	 * @param companyId
	 *            the company id
	 * @return HolidayListMasterResponse contains Holiday List
	 */
	HolidayListMasterResponse getHolidayList(PageRequest pageDTO,
			SortCondition sortDTO, String countryId, String stateId,
			String year, Long companyId);

	/**
	 * purpose : Get Holiday Data For Edit.
	 * 
	 * @param Long
	 *            the holidayId
	 * @param Long
	 *            the companyId
	 * @return HolidayListMasterForm contains Holiday data
	 */
	HolidayListMasterForm getHolidayData(Long holidayId, Long companyId);

	/**
	 * purpose : Add Holiday Data.
	 * 
	 * @param HolidayListMasterForm
	 *            the holidayListMasterForm
	 * @param Long
	 *            the companyId
	 * @return Response
	 */
	HolidayListMasterForm addHolidayMaster(
			HolidayListMasterForm holidayListMasterForm, Long companyId);

	/**
	 * purpose : Edit Holiday Master Data.
	 * 
	 * @param HolidayListMasterForm
	 *            the holidayListMasterForm
	 * @param Long
	 *            the holidayId
	 * @param Long
	 *            the companyId
	 * @return Response
	 */
	HolidayListMasterForm editHolidayMaster(
			HolidayListMasterForm holidayListMasterForm, Long holidayId,
			Long companyId);

}
