package com.payasia.common.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.payasia.common.dto.CalendarCodeValueDTO;
import com.payasia.common.dto.CalendarEventDTO;
import com.payasia.common.dto.EmployeeCalendarDTO;

public class EmployeeCalendarDefForm extends PageResponse implements Serializable {
	
	
	
	private static final long serialVersionUID = 1L;
	private Long calendarTemplateId;
	private String calendarTemplateName;
	private String description;
	private Integer year;
	private String startDate;
	private String endDate;
	private String employeeNumber;
	
	private String employeeName;
	private List<CalendarDefForm> calendarTemplateList;
	private List<EmployeeCalendarDTO> employeeCalendarDTOList;
	private List<CalendarEventDTO> eventsJanuary;
	private List<CalendarEventDTO> eventsFebruary;
	private List<CalendarEventDTO> eventsMarch;
	private List<CalendarEventDTO> eventsApril;
	private List<CalendarEventDTO> eventsMay;
	private List<CalendarEventDTO> eventsJune;
	private List<CalendarEventDTO> eventsJuly;
	private List<CalendarEventDTO> eventsAugust;
	private List<CalendarEventDTO> eventsSeptember;
	private List<CalendarEventDTO> eventsOctober;
	private List<CalendarEventDTO> eventsNovember;
	private List<CalendarEventDTO> eventsDecember;
	private List<CalendarCodeValueDTO> calendarCodeValueDTOList = LazyList
			.decorate(new ArrayList<CalendarCodeValueDTO>(),
					FactoryUtils.instantiateFactory(CalendarCodeValueDTO.class));
	
	
	
	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public List<CalendarCodeValueDTO> getCalendarCodeValueDTOList() {
		return calendarCodeValueDTOList;
	}

	public void setCalendarCodeValueDTOList(
			List<CalendarCodeValueDTO> calendarCodeValueDTOList) {
		this.calendarCodeValueDTOList = calendarCodeValueDTOList;
	}

	public List<CalendarEventDTO> getEventsJanuary() {
		return eventsJanuary;
	}

	public void setEventsJanuary(List<CalendarEventDTO> eventsJanuary) {
		this.eventsJanuary = eventsJanuary;
	}

	public List<CalendarEventDTO> getEventsFebruary() {
		return eventsFebruary;
	}

	public void setEventsFebruary(List<CalendarEventDTO> eventsFebruary) {
		this.eventsFebruary = eventsFebruary;
	}

	public List<CalendarEventDTO> getEventsMarch() {
		return eventsMarch;
	}

	public void setEventsMarch(List<CalendarEventDTO> eventsMarch) {
		this.eventsMarch = eventsMarch;
	}

	public List<CalendarEventDTO> getEventsApril() {
		return eventsApril;
	}

	public void setEventsApril(List<CalendarEventDTO> eventsApril) {
		this.eventsApril = eventsApril;
	}

	public List<CalendarEventDTO> getEventsMay() {
		return eventsMay;
	}

	public void setEventsMay(List<CalendarEventDTO> eventsMay) {
		this.eventsMay = eventsMay;
	}

	public List<CalendarEventDTO> getEventsJune() {
		return eventsJune;
	}

	public void setEventsJune(List<CalendarEventDTO> eventsJune) {
		this.eventsJune = eventsJune;
	}

	public List<CalendarEventDTO> getEventsJuly() {
		return eventsJuly;
	}

	public void setEventsJuly(List<CalendarEventDTO> eventsJuly) {
		this.eventsJuly = eventsJuly;
	}

	public List<CalendarEventDTO> getEventsAugust() {
		return eventsAugust;
	}

	public void setEventsAugust(List<CalendarEventDTO> eventsAugust) {
		this.eventsAugust = eventsAugust;
	}

	public List<CalendarEventDTO> getEventsSeptember() {
		return eventsSeptember;
	}

	public void setEventsSeptember(List<CalendarEventDTO> eventsSeptember) {
		this.eventsSeptember = eventsSeptember;
	}

	public List<CalendarEventDTO> getEventsOctober() {
		return eventsOctober;
	}

	public void setEventsOctober(List<CalendarEventDTO> eventsOctober) {
		this.eventsOctober = eventsOctober;
	}

	public List<CalendarEventDTO> getEventsNovember() {
		return eventsNovember;
	}

	public void setEventsNovember(List<CalendarEventDTO> eventsNovember) {
		this.eventsNovember = eventsNovember;
	}

	public List<CalendarEventDTO> getEventsDecember() {
		return eventsDecember;
	}

	public void setEventsDecember(List<CalendarEventDTO> eventsDecember) {
		this.eventsDecember = eventsDecember;
	}

	public String getCalendarTemplateName() {
		return calendarTemplateName;
	}

	public void setCalendarTemplateName(String calendarTemplateName) {
		this.calendarTemplateName = calendarTemplateName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
	
	
	
	public List<EmployeeCalendarDTO> getEmployeeCalendarDTOList() {
		return employeeCalendarDTOList;
	}

	public void setEmployeeCalendarDTOList(
			List<EmployeeCalendarDTO> employeeCalendarDTOList) {
		this.employeeCalendarDTOList = employeeCalendarDTOList;
	}

	public Long getCalendarTemplateId() {
		return calendarTemplateId;
	}

	public void setCalendarTemplateId(Long calendarTemplateId) {
		this.calendarTemplateId = calendarTemplateId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	

	public List<CalendarDefForm> getCalendarTemplateList() {
		return calendarTemplateList;
	}

	public void setCalendarTemplateList(List<CalendarDefForm> calendarTemplateList) {
		this.calendarTemplateList = calendarTemplateList;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
