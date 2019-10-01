package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.LeaveBatchConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimBatchMaster;

public interface ClaimBatchMasterDAO {

	void save(ClaimBatchMaster claimBatchMaster);

	ClaimBatchMaster findByID(Long claimBatchId);

	void update(ClaimBatchMaster claimBatchMaster);

	void delete(ClaimBatchMaster claimBatchMaster);

	Path<String> getSortPathForclaimBatch(SortCondition sortDTO,
			Root<ClaimBatchMaster> claimBatchRoot);

	ClaimBatchMaster findByDescCompany(Long claimBatchId, String claimDesc,
			Long companyId);

	List<ClaimBatchMaster> getClaimBatchByCondition(PageRequest pageDTO,
			SortCondition sortDTO, LeaveBatchConditionDTO leaveBatchDTO,
			Long companyID, int year);

	Integer getClaimBatchCountByCondition(LeaveBatchConditionDTO leaveBatchDTO,
			Long companyID, int year);

	ClaimBatchMaster checkClaimBatchByDate(Long claimBatchId, Long companyId,
			String date, String dateFormat);

	List<ClaimBatchMaster> getClaimBatchByCompany(Long companyID);

	/**
	 * This method fetch information from ClaimBatchMaster within a date range
	 * and paid or unpaid or all types.
	 * 
	 * @param companyId
	 * @param startDate
	 * @param endDate
	 * @param dateFormat
	 * @param paidStatus
	 * @return List<ClaimBatchMaster> JPA bean
	 */

	List<ClaimBatchMaster> getClaimBatchMastersByDateRange(Long companyId,
			String startDate, String endDate, String dateFormat,
			String paidStatus);

	ClaimBatchMaster findByClaimBatchMasterId(Long claimBatchId, Long companyId);

}