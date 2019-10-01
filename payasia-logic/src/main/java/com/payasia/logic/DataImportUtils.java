/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.EmpDataImportTemplateField;

/**
 * The Interface DataImportUtils.
 */
@Transactional
public interface DataImportUtils {

	/**
	 * Purpose: To get the dynamic form record value based on Column Number
	 * 
	 * @param colNumber
	 *            the col number
	 * @param existingFormRecord
	 *            the existing form record
	 * @return the col value file
	 */
	String getColValueFile(String colNumber,
			DynamicFormRecord existingFormRecord);

	/**
	 * Purpose: To copy the changed dynamic form record data to that of the
	 * existing data.
	 * 
	 * @param colNames
	 *            the col names
	 * @param newRecord
	 *            the new record
	 * @param oldRecord
	 *            the old record
	 * @return the dynamic form record
	 */
	DynamicFormRecord copyDynamicRecordData(List<String> colNames,
			DynamicFormRecord newRecord, DynamicFormRecord oldRecord);

	/**
	 * Purpose: To set the dynamic form record value based on Column Number
	 * 
	 * @param colNumber
	 *            the col number
	 * @param dynRecordsName
	 *            the dyn records name
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param value
	 *            the value
	 */
	void setDynamicFormRecordValues(String colNumber,
			List<String> dynRecordsName, DynamicFormRecord dynamicFormRecord,
			String value);

	/**
	 * Purpose: To set the dynamic form record field properties in colMap
	 * 
	 * @param empDataImportTemplateField
	 *            the emp data import template field
	 * @param colFormMapList
	 *            the col form map list
	 * @param tableNames
	 *            the table names
	 * @param value
	 *            the value
	 * @param formIds
	 *            the form ids
	 * @param companyId
	 *            the company id
	 * @param dynamicFormVersions
	 * @param dynamicFormObjects
	 */
	void setDynamicFieldValue(
			EmpDataImportTemplateField empDataImportTemplateField,
			List<HashMap<String, String>> colFormMapList,
			List<String> tableNames, String value, List<Long> formIds,
			Long companyId, HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions,
			List<String> dependentsTypeFieldNameList);

	/**
	 * Purpose: To set the dynamic form table record value from the excel file
	 * for insert/update/delete strategy.
	 * 
	 * @param colFormMapList
	 *            the col form map list
	 * @param formId
	 *            the form id
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param tableName
	 *            the table name
	 * @param dynamicFormTableRecord
	 *            the dynamic form table record
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @return true, if successful
	 */
	boolean setTableValues(List<HashMap<String, String>> colFormMapList,
			Long formId, DynamicFormRecord dynamicFormRecord, String tableName,
			DynamicFormTableRecord dynamicFormTableRecord,
			DataImportParametersDTO dataImportParametersDTO);

	/**
	 * Purpose: To get the dynamic form table record value based on Column
	 * Number
	 * 
	 * @param colNumber
	 *            the col number
	 * @param existingFormRecord
	 *            the existing form record
	 * @return the table record value
	 */
	String getTableRecordValue(String colNumber,
			DynamicFormTableRecord existingFormRecord);

	/** The data import status map. */
	static HashMap<String, String> dataImportStatusMap = new HashMap<String, String>();

	/** The data import log map. */
	static HashMap<String, DataImportForm> dataImportLogMap = new HashMap<String, DataImportForm>();

	/** The data import last updated time map. */
	static HashMap<String, String> dataImportLastUpdatedTimeMap = new HashMap<String, String>();

	/** The leave import status map. */
	static HashMap<String, String> leaveImportStatusMap = new HashMap<String, String>();

	/** The leave import log map. */
	static HashMap<String, LeaveBalanceSummaryForm> leaveImportLogMap = new HashMap<String, LeaveBalanceSummaryForm>();

}
