/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.DataDictionaryDTO;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.form.ExcelExportToolForm;
import com.payasia.common.form.ExcelExportToolFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface ExcelExportToolLogic.
 */
@Transactional
public interface ExcelExportToolLogic {

	/**
	 * Purpose: To get the existing template definition list
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
	 * @return the exist import temp def
	 */
	ExcelExportToolFormResponse getExistImportTempDef(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, String searchCondition,
			String searchText, Long entityId, String scope);

	/**
	 * Purpose: Gets the existing non table data dictionary list and table
	 * names.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param languageId
	 *            the language id
	 * @return the exist mapping
	 */
	ExcelExportToolForm getExistMapping(Long companyId, long entityId,
			Long languageId);

	/**
	 * Purpose: To Delete a template.
	 * 
	 * @param templateId
	 *            the template id
	 */
	void deleteTemplate(long templateId);

	/**
	 * Purpose: to get the entity list.
	 * 
	 * @return the entity list
	 */
	List<EntityMasterDTO> getEntityList();

	/**
	 * Purpose: To get the data for a saved Template.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the data for template
	 */
	ExcelExportToolForm getDataForTemplate(long templateId);

	/**
	 * Purpose: To delete a filter of a saved Template.
	 * 
	 * @param filterId
	 *            the filter id
	 */
	void deleteFilter(long filterId);

	/**
	 * Purpose: To get the Data Dictionaries of selected table.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @param tablePosition
	 *            the table position
	 * @param languageId
	 *            the language id
	 * @return the exist table mapping
	 */
	ExcelExportToolForm getExistTableMapping(Long companyId, long entityId,
			long formId, int tablePosition, Long languageId);

	/**
	 * Purpose: To save a new Excel Export template.
	 * 
	 * @param companyId
	 *            the company id
	 * @param metaData
	 *            the meta data
	 * @param languageId
	 *            the language id
	 * @return the string
	 */
	String saveTemplate(Long companyId, String metaData, Long languageId);

	/**
	 * Purpose: To edit a already saved template.
	 * 
	 * @param templateId
	 *            the template id
	 * @param metaData
	 *            the meta data
	 * @param companyId
	 *            the company id
	 * @param languageId
	 *            the language id
	 * @return the string
	 */
	String editTemplate(long templateId, String metaData, Long companyId,
			Long languageId);

	ExcelExportToolForm getExistMappingForGroup(Long companyId, long entityId,
			Long languageId);

	List<DataDictionaryDTO> getStaticEmployeeFieldList();
	boolean isAdminAuthorizedForComTemplate(Long templateId,Long companyId);

}
