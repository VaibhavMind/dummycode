/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.MultiLingualData;

/**
 * The Interface DataExportUtils.
 */
@Transactional
public interface DataExportUtils {

	/**
	 * Purpose: To get the dynamic record fields value.
	 * 
	 * @param empDynamicMap
	 *            the emp dynamic map
	 * @param empDynamicTableMap
	 *            the emp dynamic table map
	 * @param employeeId
	 *            the employee id
	 * @param totalRecords
	 *            the total records
	 * @param finalMap
	 *            the final map
	 * @param key
	 *            the key
	 * @param valueDTO
	 *            the value dto
	 * @return the dynamic records
	 */
	int getDynamicRecords(Map<Long, List<DynamicFormRecord>> empDynamicMap,
			Map<Long, List<DynamicFormTableRecord>> empDynamicTableMap,
			Long employeeId, int totalRecords,
			Map<String, List<String>> finalMap, String key,
			DataImportKeyValueDTO valueDTO);

	/**
	 * Purpose: To get the JAXB Tab field object
	 * 
	 * @param dynamicForm
	 *            the dynamic form
	 * @return the tab object
	 */
	Tab getTabObject(DynamicForm dynamicForm);

	/**
	 * Purpose: To get the Dynamic Form Record value based on Column number
	 * 
	 * @param colNumber
	 *            the col number
	 * @param existingFormRecord
	 *            the existing form record
	 * @return the col value file
	 */
	String getColValueFile(String colNumber,
			DynamicFormRecord existingFormRecord);

	Tab getTabObjectDataExportGroup(DynamicForm dynamicForm,
			List<DataDictionary> dataDictionaryList,
			List<MultiLingualData> multiLingualDataList);

}
