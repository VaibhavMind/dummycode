package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;

import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;

public interface LionTimesheetApplicationReviewerDAO {
	List<LionTimesheetApplicationReviewer> findByCondition(Long employeeId,
			Long companyId);

	void deleteByCondition(Long employeeId);

	List<LionTimesheetApplicationReviewer> checkEmployeeOTReviewer(
			Long employeeId);

	LionTimesheetApplicationReviewer findByWorkFlowCondition(Long employeeId,
			Long workFlowRuleId);

	void update(LionTimesheetApplicationReviewer lionEmployeeReviewer);

	void delete(LionTimesheetApplicationReviewer lionEmployeeReviewer);

	void save(LionTimesheetApplicationReviewer lionEmployeeReviewer);

	LionTimesheetApplicationReviewer findById(Long ingersollEmployeeOTReviewerId);

	List<Tuple> findReviewListByCondition(Long empId, PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO);

	LionTimesheetApplicationReviewer findByEmployeeTimesheetDetailId(
			long employeeTimesheetDetailId);

	List<Tuple> findAllReviewListByCondition(Long empId, PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO);

	List<LionTimesheetApplicationReviewer> findByTimesheetStatus(
			List<String> timesheetStatusNames, Long companyId);

	long saveReviewerAndReturnId(
			LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer);

	List<LionTimesheetApplicationReviewer> checkOTEmployeeReviewer(
			long employeeId, List<String> otStatusList);

	List<Tuple> getCountByCondition(Long companyId, PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO);

	List<Tuple> getCountByConditionReviewer(Long empId, PageRequest pageDTO,
			SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO);

	LionTimesheetApplicationReviewer saveAndReturn(
			LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer);
	
	//##############################################

	List<LionTimesheetApplicationReviewer> findByCondition(Long employeeId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO);
}
