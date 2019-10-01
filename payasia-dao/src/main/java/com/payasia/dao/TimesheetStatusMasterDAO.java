package com.payasia.dao;

import com.payasia.dao.bean.TimesheetStatusMaster;

public interface TimesheetStatusMasterDAO {
	public TimesheetStatusMaster findById(long id);

	public TimesheetStatusMaster findByName(String statusName);
}
