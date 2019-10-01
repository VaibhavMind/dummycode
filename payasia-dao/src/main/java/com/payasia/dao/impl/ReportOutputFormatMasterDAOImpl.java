package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ReportOutputFormatMasterDAO;
import com.payasia.dao.bean.ReportOutputFormatMaster;

@Repository
public class ReportOutputFormatMasterDAOImpl extends BaseDAO implements
		ReportOutputFormatMasterDAO {

	@Override
	protected Object getBaseEntity() {
		ReportOutputFormatMaster reportOutputFormatMaster = new ReportOutputFormatMaster();
		return reportOutputFormatMaster;
	}

}
