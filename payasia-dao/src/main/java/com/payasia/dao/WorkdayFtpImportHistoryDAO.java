package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.WorkdayFtpImportHistoryDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.WorkdayFtpImportHistory;

public interface WorkdayFtpImportHistoryDAO {

	List<WorkdayFtpImportHistory> findByCondition(Long companyId, WorkdayFtpImportHistoryDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, String importType);

	int getCountForImportHistory(Long companyId, WorkdayFtpImportHistoryDTO conditionDTO, String importType);

	void save(WorkdayFtpImportHistory ftpImportHistory);

	WorkdayFtpImportHistory saveHistory(WorkdayFtpImportHistory ftpImportHistory);

	WorkdayFtpImportHistory findById(Long importHistoryId);

}
