package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.CompanyDocumentCenterResponseForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface CompanyDocumentCenterLogic.
 */
/**
 * @author ragulapraveen
 * 
 */
@Transactional
public interface CompanyDocumentCenterLogic {

	/**
	 * Purpose : Search company document.
	 * 
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @param categoryId
	 *            the category id
	 * @param employeeId
	 * @return the company document center form
	 */
	CompanyDocumentCenterResponseForm searchDocument(String searchCondition,                //chnage return type
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long categoryId, Long employeeId);

	/**
	 * Purpose : Delete company document
	 * 
	 * @param docId
	 *            the doc id
	 * @param companyId
	 *            the company id
	 */
	String deleteCompanyDocument(Long docId, Long companyId);

	/**
	 * Purpose : Get company category list
	 * 
	 * @return the category list
	 */
	List<CompanyDocumentCenterForm> getCategoryList();

	/**
	 * Purpose : gets uploaded company document list
	 * 
	 * @param docId
	 *            the doc id
	 * @return the uploaded doc
	 */
	CompanyDocumentCenterForm getUploadedDoc(long docId);

	/**
	 * Purpose : Update the company document
	 * 
	 * @param CompanyDocumentCenterForm
	 *            the company document center form
	 * @param companyId
	 *            the company id
	 */
	String updateDocument(CompanyDocumentCenterForm CompanyDocumentCenterForm,
			Long companyId);

	/**
	 * Purpose : Checking whether company document exists or not .
	 * 
	 * @param catergoryId
	 *            the catergory id
	 * @param documentNamem
	 *            the document namem
	 * @param companyId
	 *            the company id
	 * @return the company document center form
	 */
	CompanyDocumentCenterForm checkCompanyDocument(Long catergoryId,
			String documentNamem, Long companyId);

	/**
	 * Purpose : Upload company document
	 * 
	 * @param companyDocumentCenterForm
	 *            the company document center form
	 * @param companyId
	 *            the company id
	 * @return
	 */
	CompanyDocumentCenterForm uploadDocument(
			CompanyDocumentCenterForm companyDocumentCenterForm, Long companyId);

	/**
	 * Purpose : Searching employee documents.
	 * 
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @param categoryId
	 *            the category id
	 * @param employeeId
	 *            the employee id
	 * @return the company document center form
	 */
	CompanyDocumentCenterResponseForm searchDocumentEmployeeDocumentCenter(                                         //change return type
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long categoryId,
			Long employeeId);

	/**
	 * Purpose : Gets the employee filter list.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the employee filter list
	 */
	List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId);

	/**
	 * Purpose : Gets the edits the employee filter list.
	 * 
	 * @param documentId
	 *            the document id
	 * @param companyId
	 * @return the edits the employee filter list
	 */
	List<EmployeeFilterListForm> getEditEmployeeFilterList(Long documentId,
			Long companyId);

	/**
	 * Purpose :Delete filter.
	 * 
	 * @param filterId
	 *            the filter id
	 * @return 
	 */
	String deleteFilter(Long filterId);

	/**
	 * Purpose: Save employee filter list.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param documentId
	 *            the document id
	 * @return the string
	 */
	String saveEmployeeFilterList(String metaData, Long documentId);

	String getDocumentCategoryName(Long categoryId);

	/**
	 * Purpose : Returns path of the document to be downloaded.
	 * 
	 * @param docId
	 *            the doc id
	 * @return the string
	 */
	String viewDocument(long docId, Long companyId, Long loggedInEmployeeId);

	String getValidEmployeeNumber(String fileName,
			List<CompanyDocumentLogDTO> documentLogs, Long companyId,
			List<String> invalidZipFileNames);

	String deleteTaxDocuments(Long[] docIds, Long companyId);

}
