package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer;

public interface CoherentOvertimeApplicationReviewerDAO {

	void save(
			CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer);

	CoherentOvertimeApplicationReviewer findById(long id);

	void update(
			CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer);

	CoherentOvertimeApplicationReviewer saveAndReturn(
			CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer);

	void delete(
			CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer);

	List<CoherentOvertimeApplicationReviewer> checkOTEmployeeReviewer(
			long employeeId, List<String> otStatusList);

	CoherentOvertimeApplicationReviewer findByCondition(long timesheetId,
			long reviewerId);

	Integer getOTTimesheetReviewerCount(long timesheetId);

	Integer getCountForAllByCondition(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentOvertimeApplicationReviewer> getPendingTimesheetByIds(
			Long empId, List<Long> timesheetIdsList, Long companyId);

	List<CoherentOvertimeApplicationReviewer> findAllByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentOvertimeApplicationReviewer> findByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	Integer findByConditionCountRecords(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentOvertimeApplicationReviewer> findByTimesheetStatus(
			List<String> timesheetStatusNames, Long companyId);

	List<CoherentOvertimeApplicationReviewer> findByCondition(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO);

	
	List<CoherentOvertimeApplicationReviewer> findByCoherentOvertimeApplication(
			long timesheetId);

}
