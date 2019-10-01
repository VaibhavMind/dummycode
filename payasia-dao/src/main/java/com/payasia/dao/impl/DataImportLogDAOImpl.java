package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.DataImportLogDAO;
import com.payasia.dao.bean.DataImportLog;

@Repository
public class DataImportLogDAOImpl extends BaseDAO implements DataImportLogDAO {

	@Override
	public void saveLog(DataImportLog dataImportLog) {
		super.save(dataImportLog);

	}

	@Override
	protected Object getBaseEntity() {
		DataImportLog dataImportLog = new DataImportLog();
		return dataImportLog;
	}

}
