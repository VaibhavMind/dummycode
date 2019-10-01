package com.payasia.logic;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.CalendarDefForm;
import com.payasia.common.form.EmployeeCalendarDefForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface EmployeeCalendarDefLogic {

	List<EmployeeCalendarDefForm> getCalTempYear(Long companyId);

	 
	 
	List<CalendarDefForm> getEmployeeCalendarTemplates(Integer year,
			Long companyId);

	 
	 
	 
	 

	String assignCalendarTemplates(
			EmployeeCalendarDefForm employeeCalendarDefForm, Long companyId,
			String[] selectedIds);

	EmployeeCalendarDefForm getEmpCalendarTemplateList(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long languageId)
			throws UnsupportedEncodingException;

	List<EmployeeCalendarDefForm> getCalTemConfigYearList(Long empCalConfigId,
			Long companyId);

	CalendarDefForm getEmpCalTempData(Long empCalendarConfigId, int year,
			Long companyId);

	void saveCalEventByDate(Long calTempId, String calCode, String eventDate,
			Long companyId);

	EmployeeCalendarDefForm getDataForEmpCalConfig(Long empCalConfigId);

	String editEmpCalConfig(EmployeeCalendarDefForm employeeCalendarDefForm,
			Long companyId);

	void deleteEmpCalTemplate(Long empCalTempId);

	EmployeeListFormPage fetchEmpsForCalendarTemplate(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId, Long calendartemplateId,
			String startDate);

}
