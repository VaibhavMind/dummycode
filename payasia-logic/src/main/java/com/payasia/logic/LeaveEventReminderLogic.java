package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LeaveTypeDTO;
import com.payasia.common.dto.MailTemplateDTO;
import com.payasia.common.dto.ReminderEventDTO;
import com.payasia.common.form.LeaveEventReminderForm;
import com.payasia.common.form.PageRequest;

public interface LeaveEventReminderLogic {

	List<ReminderEventDTO> getLeaveReminderEvents();

	List<AppCodeDTO> getRecepientTypes();

	List<LeaveTypeDTO> getLeaveTypes(Long leaveSchemeId, Long companyId);

	List<MailTemplateDTO> getMailTemplates(Long companyId);

	void saveLeaveEventReminder(LeaveEventReminderForm leaveEventReminderForm,
			Long companyId);

	LeaveEventReminderForm getEventReminders(Long companyId,
			PageRequest pageDTO, String searchType, Long searchValue);

	LeaveEventReminderForm getLeaveEventReminderData(Long companyId,
			Long eventReminderConfigId);

	void updateLeaveEventReminder(
			LeaveEventReminderForm leaveEventReminderForm,
			Long eventReminderConfigId, Long companyId);

	void deleteLeaveEventReminder(Long eventReminderConfigId, Long companyId);

}
