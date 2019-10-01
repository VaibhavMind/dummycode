package com.payasia.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.FieldDataTransformationForm;
import com.payasia.common.form.WorkdayFtpConfigForm;

public interface WorkdayIntegrationController {

	String deleteHROField(Long hroFieldId);

	String getDataTransformationData(Long fieldMappingId);

	void viewImportLog(Long logFileId, Boolean isEmployeeData, HttpServletRequest request, HttpServletResponse response) throws IOException;

	String saveFTPConfigData(WorkdayFtpConfigForm ftpConfigForm);

	String getDataDictionaryFields(boolean isTableField);

	String saveUpdateHROField(Long workdayFieldId, Long hroDictionaryId);

	String saveUpdateDataTransformationData(FieldDataTransformationForm dataTransformationFieldForm);

	String runFtpImport(boolean isEmployeeData);

	String getImportHistory(int page, int rows, String columnName, String sortingType, String searchCondition,
			String searchText, String fromDate, String toDate, String importType);

	void hroWorkdayGererateReport(String batchType, String batchYear, String batchPeriod, String batchPeriod2,
			HttpServletRequest request, HttpServletResponse response);

	String getWorkDayReportMaster();

	String getBatchPeriod(String batchYear, String workDayReportId, String batchTypeId);

	String getBatchYear();

	
	
	
}
