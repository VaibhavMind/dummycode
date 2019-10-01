package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationDetail;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow;

public interface CoherentShiftApplicationDAO {

	CoherentShiftApplication findById(long id);

	void save(CoherentShiftApplication coherentShiftApplication);

	void update(CoherentShiftApplication coherentShiftApplication);

	long saveAndReturn(CoherentShiftApplication coherentShiftApplication);

	Long getCountForCondition(AddClaimConditionDTO conditionDTO, Long companyId);

	List<CoherentShiftApplication> findByCondition(
			AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	CoherentShiftApplicationWorkflow findByCondition(Long otTimesheetId,
			Long createdById);

	List<CoherentShiftApplication> findByTimesheetBatchId(
			long timesheetBatchId, long companyId, long employeeId);

	List<CoherentShiftApplicationDetail> getCoherentShiftApplicationDetails(
			long shiftId);

	Integer getCountByConditionForEmployee(Long employeeId, String fromDate,
			String toDate, LundinTsheetConditionDTO conditionDTO, Long companyId);

	List<CoherentShiftApplication> findByConditionForEmployee(
			PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			String fromDate, String toDate, Boolean visibleToEmployee,
			LundinTsheetConditionDTO conditionDTO, Long companyId);

	List<Long> findByBatchId(Long employeeId, List<Long> batchIdList,
			List<String> statusList);

	Integer getCountForFindByCondition(
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentShiftApplication> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	Integer getCountForFindAllByCondition(
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentShiftApplication> findAllByCondition(PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentShiftApplication> findSubmitTimesheetEmp(Long batchId,
			Long companyId);

	List<CoherentShiftApplication> findByConditionSubmitted(
			LundinPendingTsheetConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	CoherentShiftApplication saveAndReturnWithFlush(
			CoherentShiftApplication shift);
	
	CoherentShiftApplication findById(long id,Long employeeId);
	
	CoherentShiftApplication findByCompanyShiftId(long id,Long companyId);

}
