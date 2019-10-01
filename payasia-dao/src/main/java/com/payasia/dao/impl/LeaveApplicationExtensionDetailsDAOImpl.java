package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveApplicationExtensionDetailsDAO;
import com.payasia.dao.bean.LeaveApplicationExtensionDetails;

@Repository
public class LeaveApplicationExtensionDetailsDAOImpl extends BaseDAO implements LeaveApplicationExtensionDetailsDAO {

	@Override
	public void save(LeaveApplicationExtensionDetails leaveApplicationReviewer) {
		super.save(leaveApplicationReviewer);
	}

	@Override
	public void update(LeaveApplicationExtensionDetails leaveApplicationReviewer) {
		super.update(leaveApplicationReviewer);
	}

	@Override
	protected Object getBaseEntity() {
		LeaveApplicationExtensionDetails leaveApplicationExtensionDetails = new LeaveApplicationExtensionDetails();
		return leaveApplicationExtensionDetails;
	}

}
