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
import com.payasia.common.form.DBTableInformationForm;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.DataExportFormResponse;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.MultiLingualData;

/**
 * The Interface DataExportLogic.
 */
@Transactional
public interface DataExportLogic {

	/**
	 * Purpose: To get the list of Export templates
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param entityId
	 *            the entity id
	 * @param scope
	 * @return the export templates
	 */
	DataExportFormResponse getExportTemplates(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, String searchCondition,
			String searchText, Long entityId, String scope);

	/**
	 * Purpose: To generate Excel sheet containing exported data.
	 * 
	 * @param templateId
	 *            the template id
	 * @param companyId
	 *            the company id
	 * @param employeeId
	 *            the employee id
	 * @param exportFilterlist
	 *            the export filterlist
	 * @param languageId
	 *            the language id
	 * @return the data export form
	 */
	DataExportForm generateExcel(long templateId, Long companyId,
			Long employeeId, List<ExcelExportFiltersForm> exportFilterlist,
			Long languageId);

	DataExportForm generateExcelGroup(long templateId, Long companyId,
			Long employeeId,
			List<ExcelExportFiltersForm> excelExportFilterList,
			Long languageId, String[] selectedIds);

	/**
	 * Purpose: To set the properties of Dynamic Data Dictionaries
	 * 
	 * @param colMap
	 *            the col map
	 * @param dBTableInformationForm
	 *            the d b table information form
	 * @param dataDictionary
	 *            the data dictionary
	 * @param tabCacheMap
	 *            the Tab cache Map
	 */

	void setDynamicDictionary(Map<String, DataImportKeyValueDTO> colMap,
			DBTableInformationForm dBTableInformationForm,
			DataDictionary dataDictionary, Map<Long, Tab> tabCacheMap,
			List<Long> authorizedFormIds, String entityName);

	void setDynamicDictionaryGroup(Map<String, DataImportKeyValueDTO> colMap,
			DBTableInformationForm dBTableInformationForm,
			DataDictionary dataDictionary,
			Map<Long, DynamicForm> dynamicFormMap,
			List<DataDictionary> dataDictionaryList,
			List<MultiLingualData> multiLingualDataList,
			List<Long> authorizedFormIds, String entityName);

	/**
	 * Purpose: To get the data of Export template
	 * 
	 * @param templateId
	 *            the template id
	 * @param languageId
	 *            the language id
	 * @return the data for template
	 */

	DataExportForm getDataForTemplate(long templateId, Long languageId,
			Long loggedInEmployeeId, String mode);

	/**
	 * Purpose: To get the data of Export template Group
	 * 
	 * @param templateId
	 *            the template id
	 * @param languageId
	 *            the language id
	 * @return the data for template
	 */
	DataExportForm getDataForTemplateGroup(long templateId, Long languageId,
			Long companyId, Long loggedInEmployeeId, String mode);

	boolean isAdminAuthorizedForComTemplate(Long templateId,Long companyId);
}
