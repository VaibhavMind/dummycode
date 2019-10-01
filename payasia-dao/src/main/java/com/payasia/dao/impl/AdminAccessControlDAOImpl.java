package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.payasia.common.form.AdminAccessControlForm;
import com.payasia.dao.AdminAccessControlDAO;

@Repository
public class AdminAccessControlDAOImpl implements AdminAccessControlDAO {
	@Override
	public List<AdminAccessControlForm> accessControl() {

		List<AdminAccessControlForm> adminAccessControlForm = new ArrayList<AdminAccessControlForm>();

		return adminAccessControlForm;
	}

	@Override
	public List<AdminAccessControlForm> searchEmployee(String searchCondition,
			String searchText, String employeeStatus) {
		List<AdminAccessControlForm> adminAccessControlForm = new ArrayList<AdminAccessControlForm>();

		return adminAccessControlForm;
	}

	@Override
	public void enableEmployee(String[] employeeId) {

	}

	@Override
	public void disableEmployee(String[] employeeId) {

	}
}
