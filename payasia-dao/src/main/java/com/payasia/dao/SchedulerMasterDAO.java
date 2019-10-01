package com.payasia.dao;

import com.payasia.dao.bean.SchedulerMaster;

public interface SchedulerMasterDAO {

	SchedulerMaster findByCondition(String schedulerName);

}
