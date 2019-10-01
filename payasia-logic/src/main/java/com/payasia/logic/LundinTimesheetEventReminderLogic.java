package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.MailTemplateDTO;
import com.payasia.common.dto.ReminderEventDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.LundinTimesheetEventReminderForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface LundinTimesheetEventReminderLogic {

	List<ReminderEventDTO> getTimesheetReminderEvents();

	List<AppCodeDTO> getTimesheetRecepientTypes();

	List<MailTemplateDTO> getMailTemplates(Long companyId);

	ResponseObjectDTO saveTimesheetEventReminder(
			LundinTimesheetEventReminderForm lundinTimesheetEventReminderForm,
			Long companyId);

	LundinTimesheetEventReminderForm getTimesheetEventReminders(Long companyId,
			PageRequest pageDTO, String searchType, Long searchValue);

	LundinTimesheetEventReminderForm getTimesheetEventReminderData(
			Long companyId, Long eventReminderConfigId);

	ResponseObjectDTO updateTimesheetEventReminder(
			LundinTimesheetEventReminderForm lundinTimesheetEventReminderForm,
			Long eventReminderConfigId, Long companyId);

	ResponseObjectDTO deleteTimesheetEventReminder(Long eventReminderConfigId,
			Long companyId);

	LeaveReviewerResponseForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, long employeeId, String empName,
			String empNumber, long companyId);

}
