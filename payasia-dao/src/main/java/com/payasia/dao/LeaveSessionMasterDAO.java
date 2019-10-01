package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSessionMaster;

public interface LeaveSessionMasterDAO {

	LeaveSessionMaster findById(Long leaveSessionMasterID);

	List<LeaveSessionMaster> findAll();

	LeaveSessionMaster findByName(String SessionName);

}
