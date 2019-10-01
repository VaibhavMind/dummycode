/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.form.CompanyDynamicForm;

/**
 * The Interface CompanyDynamicFormLogic.
 */
@Transactional
public interface CompanyDynamicFormLogic {

	/**
	 * Purpose: To save the a dummy metadata for the basic tab if no record for
	 * basic tab exist.
	 * 
	 * @param companyId
	 *            the company id
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @return the company dynamic form
	 */
	CompanyDynamicForm saveDynamicXML(Long companyId, String metaData,
			String tabName);

	/**
	 * Purpose: Edit and Save a section metadata
	 * 
	 * @param companyId
	 *            the company id
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @param argFormId
	 *            the arg form id
	 * @return the string
	 */
	String saveXML(Long companyId, String metaData, String tabName,
			long argFormId);

	/**
	 * Purpose: To get the Metadata for selected Section.
	 * 
	 * @param companyId
	 *            the company id
	 * @param formId
	 *            the tab name
	 * @return the xML
	 */
	CompanyDynamicForm getXML(Long companyId, long formId);

	/**
	 * Purpose: To get the Section list.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the tab list
	 */
	List<CompanyDynamicForm> getTabList(Long companyId);

	/**
	 * Purpose: To extract the DropDown options from imported XLs.
	 * 
	 * @param companyDynamicForm
	 *            the company dynamic form
	 * @return the options from xl
	 */
	CompanyDynamicForm getOptionsFromXL(CompanyDynamicForm companyDynamicForm);

	/**
	 * Purpose: To Delete a section.
	 * 
	 * @param companyId
	 *            the company id
	 * @param formId
	 *            the form id
	 * @return the string
	 */

	String deleteTab(Long companyId, long formId);

	/**
	 * Purpose: To extract the code and description from the Imported xls.
	 * 
	 * @param companyDynamicForm
	 *            the company dynamic form
	 * @return the code desc from xl
	 */
	List<CodeDescDTO> getCodeDescFromXL(CompanyDynamicForm companyDynamicForm);

	/**
	 * Purpose: To check if the String or Numeric type field is eligible for
	 * Property edit.
	 * 
	 * @param formId
	 *            the form id
	 * @param fieldName
	 *            the field name
	 * @param isTable
	 *            the is table
	 * @param companyId
	 *            the company id
	 * @param tablePosition
	 *            the table position
	 * @return the string
	 */
	String checkFieldEdit(Long formId, String fieldName, boolean isTable,
			Long companyId, String tablePosition);

}
