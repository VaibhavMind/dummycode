package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.DataImportHistory;

public interface DataImportHistoryDAO {

	DataImportHistory saveHistory(DataImportHistory dataImportHistory);

	List<DataImportHistory> findAll(Long companyId);
}
