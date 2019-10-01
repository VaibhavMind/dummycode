package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;

import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.dao.bean.CoherentShiftApplicationDetail;

public interface CoherentShiftApplicationDetailDAO {

	CoherentShiftApplicationDetail findById(long id);

	void save(CoherentShiftApplicationDetail coherentShiftApplicationDetail);

	void update(CoherentShiftApplicationDetail coherentShiftApplicationDetail);

	CoherentShiftApplicationDetail saveAndReturn(
			CoherentShiftApplicationDetail coherentShiftApplicationDetail);

	List<CoherentShiftApplicationDetail> findByShiftId(long id);

	List<Tuple> getEmployeeShiftReportDetail(
			LundinTsheetConditionDTO conditionDTO, Long companyId);

	List<CoherentShiftApplicationDetail> getTimesheetDetailsByTimesheetId(
			long timesheeetId);

	List<Tuple> getEmployeeShiftReportDetailWithRevAppDate(
			LundinTsheetConditionDTO conditionDTO, Long companyId);
	
	CoherentShiftApplicationDetail findById(long id,Long employeeId);

}
