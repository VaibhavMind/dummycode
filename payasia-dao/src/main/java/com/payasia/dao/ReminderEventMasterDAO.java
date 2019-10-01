package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ReminderEventMaster;

public interface ReminderEventMasterDAO {

	List<ReminderEventMaster> getAllReminderEvents(
			String leaveEventReminderModuleLeave);

	ReminderEventMaster findById(long eventMasterId);

}
