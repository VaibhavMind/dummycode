/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.PaySlipDynamicForm;

/**
 * The Interface PaySlipDynamicFormLogic.
 */
@Transactional
public interface PaySlipDynamicFormLogic {

	/**
	 * Purpose: Edit and Save a section metadata.
	 * 
	 * @param companyId
	 *            the company id
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @param argFormId
	 *            the arg form id
	 * @param part
	 * @param month
	 * @param year
	 * @param effectiveDateChanged
	 * @return the string
	 */
	String saveXML(Long companyId, String metaData, String tabName,
			long argFormId, int year, long month, int part,
			boolean effectiveDateChanged);

	/**
	 * Purpose: To get the Metadata for selected Section.
	 * 
	 * @param companyId
	 *            the company id
	 * @param formId
	 *            the form id
	 * @return the xml
	 */
	PaySlipDynamicForm getXML(Long companyId, long formId);

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
	 * Purpose: To save the a dummy metadata for the basic tab if no record for
	 * basic tab exist.
	 * 
	 * @param companyId
	 *            the company id
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @param part
	 * @param month
	 * @param year
	 * @return the pay slip dynamic form
	 */
	PaySlipDynamicForm saveDynamicXML(Long companyId, String metaData,
			String tabName, int year, long month, int part);

	/**
	 * Purpose: To extract the DropDown options from imported XLs.
	 * 
	 * @param paySlipDynamicForm
	 *            the pay slip dynamic form
	 * @return the options from xl
	 */
	PaySlipDynamicForm getOptionsFromXL(PaySlipDynamicForm paySlipDynamicForm);

	/**
	 * Purpose: To get the Section list.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the tab list
	 */
	List<PaySlipDynamicForm> getTabList(Long companyId);

	/**
	 * Purpose: to get the calculatiory fields.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the calculatiory fields
	 */
	CalculatoryFieldFormResponse getCalculatioryFields(Long companyId);

	/**
	 * Purpose: to Get the dictionary names.
	 * 
	 * @param companyId
	 *            the company id
	 * @param dictionaryIds
	 *            the dictionary ids
	 * @return the dictionary label
	 */
	Map<Long, String> getDictionaryLabel(Long companyId, String[] dictionaryIds);

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

	List<MonthMasterDTO> getMonthList();

	PaySlipDynamicForm getPaySlipFrequencyDetails(Long companyId);

	PaySlipDynamicForm getEffectiveFrom(Long companyId);

	PaySlipDynamicForm getCurrentPayslipInfo(Long companyId);

	boolean getPayslipReleasedStatus(Long companyId, Long monthId, int year,
			Integer part);

}
