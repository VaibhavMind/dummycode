package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.CoherentConditionDTO;
import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CoherentOvertimeApplication;

public interface CoherentOvertimeApplicationDAO {

	CoherentOvertimeApplication findById(long id);

	void save(CoherentOvertimeApplication coherentOvertimeApplication);

	void update(CoherentOvertimeApplication coherentOvertimeApplication);

	CoherentOvertimeApplication saveAndReturn(
			CoherentOvertimeApplication coherentOvertimeApplication);

	List<CoherentOvertimeApplication> findByTimesheetBatchId(
			long timesheetBatchId, long companyId, long employeeId);

	List<Long> findByBatchId(Long employeeId, List<Long> batchIdList,
			List<String> statusList);

	List<CoherentOvertimeApplication> findByConditionForEmployee(
			PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			String fromDate, String toDate, Boolean visibleToEmployee,
			CoherentConditionDTO conditionDTO);

	Integer getCountByConditionForEmployee(Long employeeId, String fromDate,
			String toDate, CoherentConditionDTO conditionDTO);

	Integer getCountForFindByCondition(
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentOvertimeApplication> findAllByCondition(PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	Integer getCountForFindAllByCondition(
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentOvertimeApplication> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	Long getCountForCondition(CoherentConditionDTO conditionDTO, Long companyId);

	List<CoherentOvertimeApplication> findByCondition(
			CoherentConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	List<CoherentOvertimeApplication> findSubmitTimesheetEmp(Long batchId,
			Long companyId);

	List<CoherentOvertimeApplication> findByConditionSubmitted(
			LundinPendingTsheetConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	CoherentOvertimeApplication findByCondition(Long companyId,
			Long employeeId, Long batchId, String remarks);
	
	CoherentOvertimeApplication findById(long id,Long employeeId,Long companyId);

	CoherentOvertimeApplication findById(long id, Long employeeId);
	
	CoherentOvertimeApplication findByCompanyId(Long id,Long companyId);
	
	

}
