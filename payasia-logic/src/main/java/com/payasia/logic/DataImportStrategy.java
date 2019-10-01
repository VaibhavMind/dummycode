package com.payasia.logic;

import java.util.HashMap;
import java.util.List;

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.dao.bean.DynamicFormRecord;

/**
 * The Interface DataImportStrategy.
 */
public interface DataImportStrategy {

	void setDynamicValues(DataImportParametersDTO dataImportParametersDTO,
			List<String> tableNames,
			List<HashMap<String, String>> colFormMapList,
			List<String> dynRecordsName, Long formId,
			DynamicFormRecord dynamicFormRecord,
			DynamicFormRecord existingFormRecord, Long companyId, String mode);

	void handleException(String rowNumber,
			List<DataImportLogDTO> updateAndInsertLogs,
			PayAsiaSystemException ex);

	void setDynamicValuesForUpdate(
			DataImportParametersDTO dataImportParametersDTO,
			List<String> tableNames,
			List<HashMap<String, String>> colFormMapList,
			List<String> dynRecordsName, Long formId,
			DynamicFormRecord dynamicFormRecord,
			DynamicFormRecord existingFormRecord, Long companyId, String mode);

}
