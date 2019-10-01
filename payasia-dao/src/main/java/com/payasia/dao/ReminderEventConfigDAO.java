package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.LeaveEventReminderConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.dao.bean.ReminderEventConfig;

public interface ReminderEventConfigDAO {

	ReminderEventConfig save(ReminderEventConfig reminderEventConfig);

	List<ReminderEventConfig> findByCondition(
			LeaveEventReminderConditionDTO conditionDTO, PageRequest pageDTO);

	Integer getCountForCondition(
			LeaveEventReminderConditionDTO leaveEventReminderConditionDTO);

	ReminderEventConfig findById(long reminderEventConfigId);

	void update(ReminderEventConfig reminderEventConfig);

	void delete(ReminderEventConfig reminderEventConfig);

	ReminderEventConfig findBySchemeEvent(Long leaveSchemeId, String eventName,
			Long companyId);

	ReminderEventConfig findByTypeEvent(Long leaveTypeId, String eventName,
			Long companyId);

	ReminderEventConfig findByEvent(String eventName, Long companyId);

	ReminderEventConfig findByTypeSchemeEvent(Long leaveTypeId,
			Long leaveSchemeId, String eventName, Long companyId);

	ReminderEventConfig findEventReminderConfigByCompanyId(Long eventReminderConfigId, Long companyId);

}
