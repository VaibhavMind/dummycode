package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveStatusMaster;

public interface LeaveStatusMasterDAO {

	LeaveStatusMaster findByCondition(String leaveStatusName);

	List<LeaveStatusMaster> findAll();

}
