package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.WorkDayGenerateReportDTO;
import com.payasia.common.dto.WorkdayPaygroupBatchDTO;
import com.payasia.common.dto.WorkdayReportMasterDTO;
import com.payasia.common.form.WorkdayGenerateReportForm;

public interface WorkdayReportLogic {

	List<WorkdayPaygroupBatchDTO> getBatchPeriod(Long companyId, Long batchYear, Long workDayReportVal, Long batchTypeVal);

	WorkDayGenerateReportDTO generateWorkDayReport(WorkdayGenerateReportForm workdayGererateReportDTO);

	List<WorkdayReportMasterDTO> getWorkDayReportMaster(Long companyId);

	List<WorkdayPaygroupBatchDTO> getBatchYear(Long companyId);

}
