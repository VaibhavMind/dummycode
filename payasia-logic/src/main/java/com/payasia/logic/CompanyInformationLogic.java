package com.payasia.logic;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.CompanyCopyForm;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.CompanyFormResponse;
import com.payasia.common.form.CompanyListForm;
import com.payasia.common.form.EntityListViewFieldForm;
import com.payasia.common.form.EntityListViewForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface CompanyInformationLogic.
 */
@Transactional
public interface CompanyInformationLogic {

	/**
	 * Purpose : Gets all the companies which were assigned based on search
	 * condition and search text .
	 * 
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param empId
	 *            the emp id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the companies
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	CompanyFormResponse getCompanies(String searchCondition, String searchText,
			Long empId, PageRequest pageDTO, SortCondition sortDTO)
			throws UnsupportedEncodingException;

	/**
	 * Purpose : Creates company with company basic information.
	 * 
	 * @param companyForm
	 *            the company form
	 * @return the long
	 */
	Long addCompany(CompanyForm companyForm);

	/**
	 * Purpose : Saving company grid view.
	 * 
	 * @param companyId
	 *            the company id
	 * @param viewName
	 *            the view name
	 * @param recordsPerPage
	 *            the records per page
	 * @param dataDictionaryIdArr
	 *            the data dictionary id arr
	 * @param rowIndexs
	 *            the row indexs
	 */
	void saveCustomView(Long companyId, String viewName, int recordsPerPage,
			String[] dataDictionaryIdArr, String[] rowIndexs);

	/**
	 * Purpose : Gets the company field names.
	 * 
	 * @param viewID
	 *            the view id
	 * @return the custom column name
	 */
	List<EntityListViewFieldForm> getCustomColumnName(Long viewID);

	/**
	 * Purpose : Gets company grid view names.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the view name
	 */
	List<EntityListViewForm> getViewName(Long companyId);

	/**
	 * Purpose : Gets the company groups.
	 * 
	 * @return the company group
	 */
	List<CompanyForm> getCompanyGroup();

	/**
	 * Purpose : Gets the country list.
	 * 
	 * @return the country list
	 */
	List<CompanyForm> getCountryList();

	/**
	 * Purpose : Gets the state list.
	 * 
	 * @param countryId
	 *            the country id
	 * @return the state list
	 */
	List<CompanyListForm> getStateList(Long countryId);

	/**
	 * Purpose : Gets the financial year list.
	 * 
	 * @return the financial year list
	 */
	List<CompanyForm> getFinancialYearList();

	/**
	 * Purpose : Gets the pay slip frequency list.
	 * 
	 * @return the pay slip frequency list
	 */
	List<CompanyForm> getPaySlipFrequencyList();

	/**
	 * Purpose : Gets the date format list.
	 * 
	 * @return the date format list
	 */
	List<CompanyForm> getDateFormatList();

	/**
	 * Purpose : Saving company dynamic form information.
	 * 
	 * @param xml
	 *            the xml
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @param version
	 *            the version
	 * @param cmpId
	 *            the cmp id
	 * @param companyDateFormat
	 *            the company date format
	 * @param existingCompanyDateFormat
	 *            the existing company date format
	 * @return the long
	 */
	CompanyForm saveCompany(String xml, Long companyId, Long entityId,
			Long formId, Integer version, Long cmpId, String companyDateFormat,
			String existingCompanyDateFormat);

	/**
	 * Purpose : Checking company with same code already exists.
	 * 
	 * @param companyForm
	 *            the company form
	 * @return the company form
	 */
	CompanyForm checkCompany(CompanyForm companyForm);

	/**
	 * Purpose : Gets the company xmls with updated information .
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityKey
	 *            the entity key
	 * @param languageId
	 *            the language id
	 * @return the company updated xmls
	 */
	CompanyForm getCompanyUpdatedXmls(Long companyId, long entityKey,
			Long languageId);

	/**
	 * Pupose : Fetching dynamic form grid data based on grid id.
	 * 
	 * @param tId
	 *            the t id
	 * @param ColumnCount
	 *            the column count
	 * @param fieldNames
	 *            the field names
	 * @param fieldTypes
	 *            the field types
	 * @param companyId
	 *            the company id
	 * @param editCompanyJqueryDateFormat
	 *            the edit company jquery date format
	 * @return the company form
	 */
	CompanyForm tableRecordList(Long tId, int ColumnCount, String[] fieldNames,
			String[] fieldTypes, Long companyId,
			String editCompanyJqueryDateFormat);

	/**
	 * Purpose : Updating companies static data.
	 * 
	 * @param companyForm
	 *            the company form
	 * @param entityKey
	 *            the entity key
	 */
	void updateCompany(CompanyForm companyForm, Long entityKey);

	/**
	 * Purpose : Updating companies dynamic form data.
	 * 
	 * @param xml
	 *            the xml
	 * @param companyID
	 *            the company id
	 * @param entityID
	 *            the entity id
	 * @param formID
	 *            the form id
	 * @param version
	 *            the version
	 * @param entityKey
	 *            the entity key
	 * @param tabID
	 *            the tab id
	 * @param companyDateFormat
	 *            the company date format
	 * @param existingCompanyDateFormat
	 *            the existing company date format
	 */
	void updateCompanyDynamicFormRecord(String xml, Long companyID,
			Long entityID, Long formID, Integer version, Long entityKey,
			Long tabID, String companyDateFormat,
			String existingCompanyDateFormat);

	/**
	 * Purpose : Editing companies grid view based on view id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param viewId
	 *            the view id
	 * @return the list
	 */
	List<EntityListViewFieldForm> editView(Long companyId, Long viewId);

	/**
	 * Purpose: List edit view.
	 * 
	 * @param companyId
	 *            the company id
	 * @param viewId
	 *            the view id
	 * @return the list
	 */
	List<EntityListViewFieldForm> listEditView(Long companyId, Long viewId);

	/**
	 * Purpose : Delete company grid view.
	 * 
	 * @param viewId
	 *            the view id
	 */
	void deleteView(Long viewId);

	/**
	 * Purpose : Gets the currency name.
	 * 
	 * @return the currency name
	 */
	List<CompanyForm> getCurrencyName();

	/**
	 * Purpose : Copy company.
	 * 
	 * @param companyCopyForm
	 *            the company copy form
	 * @param companyId
	 *            the company id
	 * @param employeeId
	 * @param employeeId
	 *            the employee id
	 * @return the boolean
	 */
	Boolean copyCompany(CompanyCopyForm companyCopyForm, int companyId,
			Long employeeId);

	/**
	 * Purpose : Deleting company.
	 * 
	 * @param cmpId
	 *            the cmp id
	 * @param employeeId
	 *            the employee id
	 */
	void deleteCompany(Long cmpId, Long employeeId);

	/**
	 * Purpose : Checking company with company code already exists or not .
	 * 
	 * @param companyForm
	 *            the company form
	 * @param companyId
	 *            the company id
	 * @return the company form
	 */
	CompanyForm checkCompany(CompanyForm companyForm, Long companyId);

	/**
	 * Purpose : Cheking grid view name already exists ot not.
	 * 
	 * @param viewName
	 *            the view name
	 * @param companyId
	 *            the company id
	 * @return the company form
	 */
	CompanyForm checkView(String viewName, Long companyId);

	/**
	 * Purpose : Updating company grid view name .
	 * 
	 * @param companyId
	 *            the company id
	 * @param viewName
	 *            the view name
	 * @param recordsPerPage
	 *            the records per page
	 * @param dataDictionaryIdArr
	 *            the data dictionary id arr
	 * @param rowIndexsArr
	 *            the row indexs arr
	 * @param viewId
	 *            the view id
	 */
	void updateCustomView(Long companyId, String viewName, int recordsPerPage,
			String[] dataDictionaryIdArr, String[] rowIndexsArr, Long viewId);

	/**
	 * Pupose : Checking company grid view name while updating company grid view
	 * .
	 * 
	 * @param viewName
	 *            the view name
	 * @param companyId
	 *            the company id
	 * @param viewId
	 *            the view id
	 * @return the company form
	 */
	CompanyForm checkViewUpdate(String viewName, Long companyId, Long viewId);

	/**
	 * Purpose : Gets the time zone list.
	 * 
	 * @return the time zone list
	 */
	List<CompanyForm> getTimeZoneList();

	CompanyForm saveCompanyTableRecord(String tableXML, Long tabId,
			Long companyId, Long formId, Integer version, Long entityKey);

	CompanyForm updateCompanyTableRecord(String tableXML, Long tabId,
			Long companyId, Integer seqNo);

	CompanyForm deleteCompanyTableRecord(Long tableId, Long companyId,
			Integer seqNo);

	List<CompanyForm> getCompanyList();

	Map<Long, CompanyForm> getCompanyGroupYEP();

	String getShortCompanyCode(Long companyId);

	Long getCompanyIdByCode(String companyCode);

}
