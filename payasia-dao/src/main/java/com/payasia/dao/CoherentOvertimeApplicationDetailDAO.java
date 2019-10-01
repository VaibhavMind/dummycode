package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;

import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.dao.bean.CoherentOvertimeApplicationDetail;

public interface CoherentOvertimeApplicationDetailDAO {

	CoherentOvertimeApplicationDetail findById(long id);

	void save(
			CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail);

	void update(
			CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail);

	CoherentOvertimeApplicationDetail saveAndReturn(
			CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail);

	List<CoherentOvertimeApplicationDetail> getTimesheetDetailsByTimesheetId(
			long timesheeetId);

	List<Tuple> getEmployeeOvertimeReportDetail(
			LundinTsheetConditionDTO conditionDTO, Long companyId);

	List<Tuple> getEmployeeOvertimeReportDetailWithRevAppDate(
			LundinTsheetConditionDTO conditionDTO, Long companyId);

	CoherentOvertimeApplicationDetail findById(long id, Long employeeId);

}
