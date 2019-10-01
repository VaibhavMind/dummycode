package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.form.CalendarDefForm;
import com.payasia.common.form.CalendarDefResponse;
import com.payasia.common.form.CalendarTempDataForm;
import com.payasia.common.form.CalendarTempShortListResponse;
import com.payasia.common.form.CalendarTemplateMonthForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface CalendarDefLogic.
 */
@Transactional
public interface CalendarDefLogic {

	/**
	 * Gets the cal templates.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param calTempFilter
	 *            the cal temp filter
	 * @param filterText
	 *            the filter text
	 * @param companyId
	 *            the company id
	 * @return the cal templates
	 */
	CalendarDefResponse getCalTemplates(PageRequest pageDTO,
			SortCondition sortDTO, String calTempFilter, String filterText,
			Long companyId);

	/**
	 * Gets the initial ids.
	 * 
	 * @return the initial ids
	 */
	CalendarTempDataForm getInitialIds();

	/**
	 * Adds the calendar month detail.
	 * 
	 * @param calTempMonthForm
	 *            the cal temp month form
	 */
	void addCalendarMonthDetail(CalendarTemplateMonthForm calTempMonthForm);

	/**
	 * Edits the cal template.
	 * 
	 * @param calDefForm
	 *            the cal def form
	 * @param companyId
	 *            the company id
	 * @return the string
	 */
	String editCalTemplate(CalendarDefForm calDefForm, Long companyId);

	/**
	 * Gets the year list.
	 * 
	 * @param calTempId
	 *            the cal temp id
	 * @return the year list
	 */
	List<Integer> getYearList(Long calTempId);

	/**
	 * Gets the month detail.
	 * 
	 * @param year
	 *            the year
	 * @param calTempId
	 *            the cal temp id
	 * @return the month detail
	 */
	CalendarTemplateMonthForm getMonthDetail(Integer year, Long calTempId);

	/**
	 * Edits the cal month template.
	 * 
	 * @param calTempMonthForm
	 *            the cal temp month form
	 */
	void editCalMonthTemplate(CalendarTemplateMonthForm calTempMonthForm);

	/**
	 * Delete cal template.
	 * 
	 * @param calTempId
	 *            the cal temp id
	 * @return
	 */
	void deleteCalTemplate(Long companyCalTemplateId);

	/**
	 * Gets the entity data.
	 * 
	 * @return the entity data
	 */
	CalendarTempDataForm getEntityData();

	/**
	 * Gets the fields data.
	 * 
	 * @param entityId
	 *            the entity id
	 * @param companyId
	 *            the company id
	 * @return the fields data
	 */
	CalendarTempDataForm getFieldsData(Long entityId, Long companyId);

	/**
	 * Adds the short list data.
	 * 
	 * @param shortListData
	 *            the short list data
	 * @return the string
	 */
	String addShortListData(String shortListData);

	/**
	 * Gets the short list for edit.
	 * 
	 * @param calTempId
	 *            the cal temp id
	 * @return the short list for edit
	 */
	CalendarTempShortListResponse getShortListForEdit(Long calTempId);

	/**
	 * Edits the short list data.
	 * 
	 * @param shortListData
	 *            the short list data
	 * @return the string
	 */
	String editShortListData(String shortListData);

	/**
	 * Purpose: To get the code value list for Calendar Defination.
	 * 
	 * @return the code value list
	 */
	List<AppCodeDTO> getCodeValueList();

	String saveYearlyCalendar(Long calTempId, String yearlyMap);

	CalendarDefResponse viewCalendarCodeValue(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	CalendarDefResponse viewCalendarPatternCode(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	String addCalendarCodeValue(CalendarDefForm calendarDefForm, Long companyId);

	String addCalendarPatternValue(CalendarDefForm calendarDefForm,
			Long companyId);

	String updateCalendarCodeValue(CalendarDefForm calendarDefForm,
			Long companyId);

	String updateCalendarPatternValue(CalendarDefForm calendarDefForm,
			Long companyId);

	CalendarDefForm getCalCodeForEdit(Long calCodeId);

	CalendarDefForm getCalPatterneForEdit(Long calPatternId);

	void deleteCalCode(Long calCodeId);

	void deleteCalPattern(Long calPatternId);

	List<CalendarDefForm> getCalendarCodeList(Long companyId);

	String saveCalTemplate(CalendarDefForm calDefForm, Long companyId);

	List<CalendarDefForm> getPatternCodeList(Long companyId);

	CalendarDefForm getDataForCompanyCalTemplate(Long companyCalTemplateId);

	CalendarDefForm getCalTempData(Long companyCalendarTemplateId, int year,
			Long companyId);

	void saveCalEventByDate(Long calTempId, String calCode, String eventDate,
			Long companyId);

	List<CalendarDefForm> getCalTemConfigYearList(Long calConfigId,
			Long companyId);
}
