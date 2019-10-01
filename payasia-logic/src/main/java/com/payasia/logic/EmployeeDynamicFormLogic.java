/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.EmployeeDynamicForm;

/**
 * The Interface EmployeeDynamicFormLogic.
 */
@Transactional
public interface EmployeeDynamicFormLogic {

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
	 * @return the employee dynamic form
	 */
	EmployeeDynamicForm saveDynamicXML(Long companyId, String metaData,
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
	 * Purpose: To get the Section list.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the tab list
	 */
	List<EmployeeDynamicForm> getTabList(Long companyId);

	/**
	 * Purpose: To extract the DropDown options from imported XLs.
	 * 
	 * @param employeeDynamicForm
	 *            the employee dynamic form
	 * @return the options from xl
	 */
	EmployeeDynamicForm getOptionsFromXL(EmployeeDynamicForm employeeDynamicForm);

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
	 * @param employeeDynamicForm
	 *            the employee dynamic form
	 * @return the code desc from xl
	 */
	List<CodeDescDTO> getCodeDescFromXL(EmployeeDynamicForm employeeDynamicForm);

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

	String saveDynCodeDesc(Long companyId, String metaData, String tabName,
			long formId);

	List<CodeDescDTO> getDynCodeDescList(Long dataDictionaryId, Long companyId);

	Map<Long, String> getDictionaryLabel(Long companyId, String[] dictionaryIds);

	CalculatoryFieldFormResponse getReferenceDataDictionaryFields(
			Long companyId, boolean isTableField, long tableDicId);

	/**
	 * Purpose: To get the Metadata for selected Section.
	 * 
	 * @param companyId
	 *            the company id
	 * @param formId
	 *            the tab name
	 * @return the xML
	 */
	EmployeeDynamicForm getXML(Long companyId, long formId);

	CalculatoryFieldFormResponse getStringTypeDataDictionaryFields(
			Long companyId, boolean isTableField, long tableDicId);

	CalculatoryFieldFormResponse getNumericOrDateTypeDataDictionaryFields(
			Long companyId, boolean isTableField, long tableDicId,
			String fieldType);
	
	Tab getTabObject(String metaData);

}
