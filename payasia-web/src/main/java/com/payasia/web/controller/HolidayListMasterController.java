package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.HolidayListMasterForm;

 
/**
 * The Interface HolidayListMasterController.
 *
 * @author vivekjain
 */
/**
 * The Interface HolidayListMasterController.
 */
public interface HolidayListMasterController {

	/**
	 * purpose : get State List.
	 * 
	 * @param countryId
	 *            the country id
	 * @return HolidayListMasterForm contains state list
	 */
	String getStateList(Long countryId);

	/**
	 * purpose : delete Holiday .
	 * 
	 * @param holidayId
	 *            the holiday id
	 * @return the string
	 */
	String deleteHolidayMaster(Long holidayId);

	/**
	 * purpose : get HolidayList From Excel File and Save to database.
	 * 
	 * @param holidayListMasterForm
	 *            the holiday list master form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the holiday list from xl
	 * @throws Exception
	 *             the exception
	 */
	String getHolidayListFromXL(HolidayListMasterForm holidayListMasterForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception;

	/**
	 * purpose : get Country List.
	 * 
	 * @return HolidayListMasterForm contains Country list
	 */
	String getCountryList();

	/**
	 * purpose : view Holiday List.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param countryId
	 *            the country id
	 * @param stateId
	 *            the state id
	 * @param year
	 *            the year
	 * @param request
	 *            the request
	 * @return HolidayListMasterResponse contains Holiday List
	 */
	String viewHolidayList(String columnName, String sortingType, int page,
			int rows, String countryId, String stateId, String year,
			HttpServletRequest request);

	/**
	 * purpose : Get Holiday Data For Edit.
	 * 
	 * @param holidayId
	 *            the holiday id
	 * @param request
	 *            the request
	 * @return HolidayListMasterForm contains Holiday data
	 */
	String getHolidayData(Long holidayId, HttpServletRequest request);

	/**
	 * purpose : Add Holiday Data.
	 * 
	 * @param holidayListMasterForm
	 *            the holiday list master form
	 * @param request
	 *            the request
	 * @return Response
	 */
	String addHolidayMaster(HolidayListMasterForm holidayListMasterForm,
			HttpServletRequest request);

	/**
	 * purpose : Edit Holiday Master Data.
	 * 
	 * @param holidayListMasterForm
	 *            the holiday list master form
	 * @param holidayId
	 *            the holiday id
	 * @param request
	 *            the request
	 * @return Response
	 */
	String editHolidayMaster(HolidayListMasterForm holidayListMasterForm,
			Long holidayId, HttpServletRequest request);

	/**
	 * Gets the year list.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the year list
	 */
	String getYearList(HttpServletRequest request, HttpServletResponse response);

}
