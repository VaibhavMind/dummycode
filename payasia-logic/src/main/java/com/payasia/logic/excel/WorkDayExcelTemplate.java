package com.payasia.logic.excel;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.payasia.common.dto.WorkDayReportDTO;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;

public abstract class WorkDayExcelTemplate {

	public abstract XSSFWorkbook generateReport(List<WorkDayReportDTO> workDayReportDTOList);

	public abstract List<WorkDayReportDTO> getReportDataMappedObject(JAXBElement employeeTypeElement,WorkdayPaygroupBatchData workdayPaygroupBatch);

}
