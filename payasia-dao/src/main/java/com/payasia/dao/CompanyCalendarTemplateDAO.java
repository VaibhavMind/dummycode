package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.CalendarTemplateConditionDTO;
import com.payasia.common.dto.LeaveCalendarEventDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CompanyCalendarTemplate;

public interface CompanyCalendarTemplateDAO {

	void update(CompanyCalendarTemplate companyCalendarTemplate);

	void delete(CompanyCalendarTemplate companyCalendarTemplate);

	void save(CompanyCalendarTemplate companyCalendarTemplate);

	CompanyCalendarTemplate findByID(Long companyCalendarTemplateId);

	Integer getCountforfindByCompanyId(
			CalendarTemplateConditionDTO conditionDTO, Long companyId);

	List<CompanyCalendarTemplate> findByCompanyId(
			CalendarTemplateConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	List<Integer> getYearList(Long companyId);

	List<CompanyCalendarTemplate> findByYearCompany(Integer year, Long companyId);

	List<LeaveCalendarEventDTO> getCompanyCalendarTemplate(Long companyId,
			Long companyCalendarTemplateId, int year,
			Boolean isCompanyCalendar, String dateFormat, Long employeeId);

	CompanyCalendarTemplate findByTemplateNameAndId(Long companyId,
			String calendarTemplateName, Long companyCalTemplateId);

}
