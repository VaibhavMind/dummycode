package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CoherentShiftApplicationReviewer;

public interface CoherentShiftApplicationReviewerDAO {

	void save(CoherentShiftApplicationReviewer coherentShiftApplicationReviewer);

	CoherentShiftApplicationReviewer findById(long id);

	void update(
			CoherentShiftApplicationReviewer coherentShiftApplicationReviewer);

	CoherentShiftApplicationReviewer saveAndReturn(
			CoherentShiftApplicationReviewer coherentShiftApplicationReviewer);

	void delete(
			CoherentShiftApplicationReviewer coherentShiftApplicationReviewer);

	Integer findByConditionCountRecords(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentShiftApplicationReviewer> findByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	CoherentShiftApplicationReviewer findByCondition(long timesheetId,
			long reviewerId);

	Integer getOTTimesheetReviewerCount(long timesheetId);

	Integer getCountForAllByCondition(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentShiftApplicationReviewer> findAllByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO,
			Long companyId);

	List<CoherentShiftApplicationReviewer> getPendingTimesheetByIds(Long empId,
			List<Long> timesheetIdsList);

	List<CoherentShiftApplicationReviewer> findByTimesheetStatus(
			List<String> timesheetStatusNames, Long companyId);

	List<CoherentShiftApplicationReviewer> findByCondition(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO);

	List<CoherentShiftApplicationReviewer> findByCoherentShiftApplication(
			long timesheetId);

}
