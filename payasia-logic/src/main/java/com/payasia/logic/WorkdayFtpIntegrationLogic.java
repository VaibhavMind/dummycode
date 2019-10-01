package com.payasia.logic;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.payasia.common.dto.DateFormatDTO;
import com.payasia.common.dto.WorkdayFieldMappingDTO;
import com.payasia.common.dto.WorkdayFieldMappingDataTransformationDTO;
import com.payasia.common.dto.WorkdayFtpImportHistoryDTO;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.FTPImportHistoryFormResponse;
import com.payasia.common.form.FieldDataTransformationForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkdayFtpConfigForm;

public interface WorkdayFtpIntegrationLogic {

	List<DateFormatDTO> getdateFormatList();
	
	WorkdayFtpConfigForm getFTPConfigData(Long companyId);
	
	WorkdayFtpConfigForm getFTPConfigFormData(Long companyId);
	
	boolean checkPpk(WorkdayFtpConfigForm ftpIntegrationForm, Long companyId)
			throws IOException;

	void saveftpConfigData(WorkdayFtpConfigForm ftpIntegrationForm,
			Long companyId);
	
	List<WorkdayFieldMappingDTO> getWorkdayFieldMappingList(Long companyId, String entityName);

	CalculatoryFieldFormResponse getDataDictionaryFields(Long companyId, boolean isTableField, String entityName);

	Long saveUpdateFieldMapping(Long workdayFieldId, Long hroDictionaryId, Long companyId);

	void deleteHROField(Long fieldMappingId);

	List<WorkdayFieldMappingDataTransformationDTO> getTransformationData(Long fieldMappingId);

	void saveFieldDataTransformation(FieldDataTransformationForm dataTransformationFieldForm);

	String runFtpImport(Long companyId, boolean isEmployeeData, Long loggedInEmployeeId);

	FTPImportHistoryFormResponse getImportHistory(String fromDate, String toDate, Long companyId,
			WorkdayFtpImportHistoryDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO, String importType);

	Workbook getEmfImportLog(Long logFileId, Long companyId);
	
	Workbook getPtrxImportLog(Long logFileId, Long companyId);

}
