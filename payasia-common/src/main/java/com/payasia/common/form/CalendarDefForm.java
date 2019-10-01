package com.payasia.common.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.CalendarCodeValueDTO;
import com.payasia.common.dto.CalendarPatternDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.LeaveCalendarEventDTO;

public class CalendarDefForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2900449372688884987L;
	private long calDefId;
	private Long companyCalendarTemplateId;
	private String calDefName;
	private String description;
	private String codeVal;
	private Long calendarCodeMasterId;
	private String codeValue;
	private String code;
	private Long calendarPatternMasterId;
	private Long calendarPatternMasterIdEdit;
	private String patternName;
	private String patternDesc;
	private int patternIndex;
	private String patternCode;
	private String configureCalendar;
	private Boolean isCalendarAssigned;
	
	private int year;
	private boolean dataValid;
	private CommonsMultipartFile importFile;
	private List<DataImportLogDTO> dataImportLogDTOs;
	private List<CalendarPatternDTO> calendarPatternDTOList ;
	
	private List<LeaveCalendarEventDTO> leaveCalendarEventDTOList;
	private List<LeaveCalendarEventDTO> eventsJanuary;
	private List<LeaveCalendarEventDTO> eventsFebruary;
	private List<LeaveCalendarEventDTO> eventsMarch;
	private List<LeaveCalendarEventDTO> eventsApril;
	private List<LeaveCalendarEventDTO> eventsMay;
	private List<LeaveCalendarEventDTO> eventsJune;
	private List<LeaveCalendarEventDTO> eventsJuly;
	private List<LeaveCalendarEventDTO> eventsAugust;
	private List<LeaveCalendarEventDTO> eventsSeptember;
	private List<LeaveCalendarEventDTO> eventsOctober;
	private List<LeaveCalendarEventDTO> eventsNovember;
	private List<LeaveCalendarEventDTO> eventsDecember;
	
	private List<CalendarCodeValueDTO> calendarCodeValueDTOList = LazyList
			.decorate(new ArrayList<CalendarCodeValueDTO>(),
					FactoryUtils.instantiateFactory(CalendarCodeValueDTO.class));

	private CalendarTemplateWeekForm[] weekShifts = new CalendarTemplateWeekForm[7];

	public String getPatternName() {
		return patternName;
	}

	public List<LeaveCalendarEventDTO> getLeaveCalendarEventDTOList() {
		return leaveCalendarEventDTOList;
	}

	public void setLeaveCalendarEventDTOList(
			List<LeaveCalendarEventDTO> leaveCalendarEventDTOList) {
		this.leaveCalendarEventDTOList = leaveCalendarEventDTOList;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public String getConfigureCalendar() {
		return configureCalendar;
	}

	public void setConfigureCalendar(String configureCalendar) {
		this.configureCalendar = configureCalendar;
	}

	public String getPatternDesc() {
		return patternDesc;
	}

	public void setPatternDesc(String patternDesc) {
		this.patternDesc = patternDesc;
	}

	public String getCalDefName() {
		return calDefName;
	}

	public void setCalDefName(String calDefName) {
		this.calDefName = calDefName;
	}

	public String getDescription() {
		return description;
	}

	public Long getCompanyCalendarTemplateId() {
		return companyCalendarTemplateId;
	}

	public void setCompanyCalendarTemplateId(Long companyCalendarTemplateId) {
		this.companyCalendarTemplateId = companyCalendarTemplateId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCalDefId() {
		return calDefId;
	}

	public void setCalDefId(long calDefId) {
		this.calDefId = calDefId;
	}

	public CalendarTemplateWeekForm[] getWeekShifts() {
		return weekShifts;
	}

	public void setWeekShifts(CalendarTemplateWeekForm[] weekShifts) {
		if (weekShifts != null) {
			this.weekShifts = Arrays.copyOf(weekShifts, weekShifts.length);
		}
	}

	public String getCodeVal() {
		return codeVal;
	}

	public void setCodeVal(String codeVal) {
		this.codeVal = codeVal;
	}

	public List<CalendarCodeValueDTO> getCalendarCodeValueDTOList() {
		return calendarCodeValueDTOList;
	}

	public void setCalendarCodeValueDTOList(
			List<CalendarCodeValueDTO> calendarCodeValueDTOList) {
		this.calendarCodeValueDTOList = calendarCodeValueDTOList;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public CommonsMultipartFile getImportFile() {
		return importFile;
	}

	public void setImportFile(CommonsMultipartFile importFile) {
		this.importFile = importFile;
	}

	public List<DataImportLogDTO> getDataImportLogDTOs() {
		return dataImportLogDTOs;
	}

	public void setDataImportLogDTOs(List<DataImportLogDTO> dataImportLogDTOs) {
		this.dataImportLogDTOs = dataImportLogDTOs;
	}

	public boolean isDataValid() {
		return dataValid;
	}

	public void setDataValid(boolean dataValid) {
		this.dataValid = dataValid;
	}


	public List<LeaveCalendarEventDTO> getEventsJanuary() {
		return eventsJanuary;
	}

	public void setEventsJanuary(List<LeaveCalendarEventDTO> eventsJanuary) {
		this.eventsJanuary = eventsJanuary;
	}

	public List<LeaveCalendarEventDTO> getEventsFebruary() {
		return eventsFebruary;
	}

	public void setEventsFebruary(List<LeaveCalendarEventDTO> eventsFebruary) {
		this.eventsFebruary = eventsFebruary;
	}

	public List<LeaveCalendarEventDTO> getEventsMarch() {
		return eventsMarch;
	}

	public void setEventsMarch(List<LeaveCalendarEventDTO> eventsMarch) {
		this.eventsMarch = eventsMarch;
	}

	public List<LeaveCalendarEventDTO> getEventsApril() {
		return eventsApril;
	}

	public void setEventsApril(List<LeaveCalendarEventDTO> eventsApril) {
		this.eventsApril = eventsApril;
	}

	public List<LeaveCalendarEventDTO> getEventsMay() {
		return eventsMay;
	}

	public void setEventsMay(List<LeaveCalendarEventDTO> eventsMay) {
		this.eventsMay = eventsMay;
	}

	public List<LeaveCalendarEventDTO> getEventsJune() {
		return eventsJune;
	}

	public void setEventsJune(List<LeaveCalendarEventDTO> eventsJune) {
		this.eventsJune = eventsJune;
	}

	public List<LeaveCalendarEventDTO> getEventsJuly() {
		return eventsJuly;
	}

	public void setEventsJuly(List<LeaveCalendarEventDTO> eventsJuly) {
		this.eventsJuly = eventsJuly;
	}

	public List<LeaveCalendarEventDTO> getEventsAugust() {
		return eventsAugust;
	}

	public void setEventsAugust(List<LeaveCalendarEventDTO> eventsAugust) {
		this.eventsAugust = eventsAugust;
	}

	public List<LeaveCalendarEventDTO> getEventsSeptember() {
		return eventsSeptember;
	}

	public void setEventsSeptember(List<LeaveCalendarEventDTO> eventsSeptember) {
		this.eventsSeptember = eventsSeptember;
	}

	public List<LeaveCalendarEventDTO> getEventsOctober() {
		return eventsOctober;
	}

	public void setEventsOctober(List<LeaveCalendarEventDTO> eventsOctober) {
		this.eventsOctober = eventsOctober;
	}

	public List<LeaveCalendarEventDTO> getEventsNovember() {
		return eventsNovember;
	}

	public void setEventsNovember(List<LeaveCalendarEventDTO> eventsNovember) {
		this.eventsNovember = eventsNovember;
	}

	public List<LeaveCalendarEventDTO> getEventsDecember() {
		return eventsDecember;
	}

	public void setEventsDecember(List<LeaveCalendarEventDTO> eventsDecember) {
		this.eventsDecember = eventsDecember;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


	public int getPatternIndex() {
		return patternIndex;
	}

	public void setPatternIndex(int patternIndex) {
		this.patternIndex = patternIndex;
	}

	public String getPatternCode() {
		return patternCode;
	}

	public void setPatternCode(String patternCode) {
		this.patternCode = patternCode;
	}
	public Long getCalendarCodeMasterId() {
		return calendarCodeMasterId;
	}

	public void setCalendarCodeMasterId(Long calendarCodeMasterId) {
		this.calendarCodeMasterId = calendarCodeMasterId;
	}

	public Long getCalendarPatternMasterId() {
		return calendarPatternMasterId;
	}

	public void setCalendarPatternMasterId(Long calendarPatternMasterId) {
		this.calendarPatternMasterId = calendarPatternMasterId;
	}

	public List<CalendarPatternDTO> getCalendarPatternDTOList() {
		return calendarPatternDTOList;
	}

	public void setCalendarPatternDTOList(List<CalendarPatternDTO> calendarPatternDTOList) {
		this.calendarPatternDTOList = calendarPatternDTOList;
	}

	public Boolean getIsCalendarAssigned() {
		return isCalendarAssigned;
	}

	public void setIsCalendarAssigned(Boolean isCalendarAssigned) {
		this.isCalendarAssigned = isCalendarAssigned;
	}

	public Long getCalendarPatternMasterIdEdit() {
		return calendarPatternMasterIdEdit;
	}

	public void setCalendarPatternMasterIdEdit(
			Long calendarPatternMasterIdEdit) {
		this.calendarPatternMasterIdEdit = calendarPatternMasterIdEdit;
	}

}
