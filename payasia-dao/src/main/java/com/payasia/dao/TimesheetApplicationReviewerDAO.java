package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.TimesheetApplicationReviewer;

public interface TimesheetApplicationReviewerDAO {
	public void save(TimesheetApplicationReviewer lundinOTTimesheetReviewer);

	public TimesheetApplicationReviewer findById(long id);

	void update(TimesheetApplicationReviewer lundinOTTimesheetReviewer);

	List<TimesheetApplicationReviewer> findByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO);

	Integer findByConditionCountRecords(Long empId,
			PendingOTTimesheetConditionDTO otTimesheetConditionDTO);

	Integer getOTTimesheetReviewerCount(long otTimesheetId);

	List<TimesheetApplicationReviewer> checkOTEmployeeReviewer(long employeeId,
			List<String> otStatusList);

	List<TimesheetApplicationReviewer> findByCondition(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO);

	TimesheetApplicationReviewer findByCondition(long timesheetId,
			long reviewerId);

	void delete(TimesheetApplicationReviewer otTsheetReviewer);

	List<TimesheetApplicationReviewer> getPendingTSApplicationByIds(Long empId,
			List<Long> tsApplicationRevIdsList);

	List<TimesheetApplicationReviewer> findByTimesheetCondition(Long companyId,
			LundinPendingTsheetConditionDTO otTimesheetConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO);

	Long getCountForTimesheetCondition(Long companyId,
			LundinPendingTsheetConditionDTO otTimesheetConditionDTO);
}
