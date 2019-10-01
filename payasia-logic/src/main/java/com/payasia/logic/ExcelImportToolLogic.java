/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.form.ExcelImportToolFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface ExcelImportToolLogic.
 */
@Transactional
public interface ExcelImportToolLogic {

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
	 * @return the exist import temp def
	 */
	ExcelImportToolFormResponse getExistImportTempDef(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, String searchCondition,
			String searchText, Long entityId);

	/**
	 * Purpose: To save a new Excel Import template.
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
	 * Purpose: To Delete a template.
	 * 
	 * @param templateId
	 *            the template id
	 */
	String deleteTemplate(Long templateId);

	/**
	 * Purpose: To edit a already saved template.
	 * 
	 * @param companyId
	 *            the company id
	 * @param metaData
	 *            the meta data
	 * @param templateId
	 *            the template id
	 * @param languageId
	 *            the language id
	 * @return the string
	 */
	String editTemplate(Long companyId, String metaData, long templateId,
			Long languageId);

	/**
	 * Purpose: To get the list of the entities.
	 * 
	 * @return the entity list
	 */
	List<EntityMasterDTO> getEntityList();

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
	ExcelImportToolForm getExistMapping(Long companyId, long entityId,
			Long languageId);

	/**
	 * Purpose: To get the data for a saved Template.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the data for template
	 */
	ExcelImportToolForm getDataForTemplate(long templateId);

	/**
	 * Purpose: To get the list of Transaction and upload type appcodes
	 * 
	 * @param category
	 *            the category
	 * @return the app code list
	 */
	List<AppCodeDTO> getAppCodeList(String category);

	/**
	 * Purpose: To generate an Excel for the template
	 * 
	 * @param templateId
	 *            the template id
	 * @return the excel import tool form
	 */
	ExcelImportToolForm generateExcel(long templateId);

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
	ExcelImportToolForm getExistTableMapping(Long companyId, long entityId,
			long formId, int tablePosition, Long languageId);

	ExcelImportToolFormResponse getExistImportTempDef(PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			String searchCondition, String searchText);
}
