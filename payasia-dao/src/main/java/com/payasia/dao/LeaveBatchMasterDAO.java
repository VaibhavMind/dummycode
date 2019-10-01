package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.LeaveBatchConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveBatchMaster;

public interface LeaveBatchMasterDAO {

	List<LeaveBatchMaster> getLeaveBatchAll(PageRequest pageDTO,
			SortCondition sortDTO, Long companyID);

	List<LeaveBatchMaster> getLeaveBatchByCondition(PageRequest pageDTO,
			SortCondition sortDTO, LeaveBatchConditionDTO leaveBatchDTO,
			Long companyID);

	void save(LeaveBatchMaster leaveBatchMaster);

	LeaveBatchMaster findByID(Long leaveBatchId);

	Path<String> getSortPathForLeaveBatch(SortCondition sortDTO,
			Root<LeaveBatchMaster> leaveBatchRoot);

	void update(LeaveBatchMaster leaveBatchMaster);

	void delete(LeaveBatchMaster leaveBatchMaster);

	LeaveBatchMaster findByDescCompany(Long leaveBatchId, String leaveDesc,
			Long companyId);

	int countLeaveBatch(LeaveBatchConditionDTO leaveBatchDTO, Long companyId);

	LeaveBatchMaster findByCompID(Long leaveBatchId, Long companyId);

}