package com.payasia.dao;

import java.util.Date;

import com.payasia.dao.bean.SchedulerStatus;

public interface SchedulerStatusDAO {

	void update(SchedulerStatus schedulerStatus);

	void save(SchedulerStatus schedulerStatus);

	void delete(SchedulerStatus schedulerStatus);

	SchedulerStatus findByID(long schedulerStatusId);

	SchedulerStatus findByCondition(long companyId, long schedulerId,
			Date currentDate, boolean b);

	void updateSchedulerStatus(SchedulerStatus schedulerStatus);

}
