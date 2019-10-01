/**
 * @author ragulapraveen
 *
 */
package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.CompanyDocumentConditionDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CompanyDocument;

/**
 * The Interface CompanyDocumentDAO.
 */
public interface CompanyDocumentDAO {

	/**
	 * Save CompanyDocumentDAO Object.
	 * 
	 * @param companyDocument
	 *            the company document
	 * @return the company document
	 */
	CompanyDocument save(CompanyDocument companyDocument);

	/**
	 * Find CompanyDocumentDAO Object list by condition.
	 * 
	 * @param companyDocumentConditionDTO
	 *            the company document condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<CompanyDocument> findByCondition(
			CompanyDocumentConditionDTO companyDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Gets the sort path for all company document.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param empRoot
	 *            the emp root
	 * @return the sort path for all company document
	 */
	Path<String> getSortPathForAllCompanyDocument(SortCondition sortDTO,
			Root<CompanyDocument> empRoot);

	/**
	 * Find CompanyDocumentDAO Object by id.
	 * 
	 * @param docId
	 *            the doc id
	 * @return the company document
	 */
	CompanyDocument findByID(long docId);

	/**
	 * Delete CompanyDocumentDAO Object.
	 * 
	 * @param companyDocument
	 *            the company document
	 */
	void delete(CompanyDocument companyDocument);

	/**
	 * Update CompanyDocumentDAO Object.
	 * 
	 * @param companyDocument
	 *            the company document
	 */
	void update(CompanyDocument companyDocument);

	/**
	 * Gets the count for all.
	 * 
	 * @param companyDocumentConditionDTO
	 *            the company document condition dto
	 * @return the count for all
	 */
	int getCountForAll(CompanyDocumentConditionDTO companyDocumentConditionDTO);

	/**
	 * Find CompanyDocumentDAO Object by category company and document name.
	 * 
	 * @param categoryId
	 *            the category id
	 * @param documentName
	 *            the document name
	 * @param companyId
	 *            the company id
	 * @return the company document
	 */
	CompanyDocument findByCategoryCompanyAndDocumentName(Long categoryId,
			String documentName, Long companyId);

	/**
	 * Find max company document id.
	 * 
	 * @return the long
	 */
	Long findMaxCompanyDocumentId();

	/**
	 * Gets the distinct year list.
	 * 
	 * @param companyId
	 *            the company id
	 * @param categoryId
	 *            the category id
	 * @return the distinct year list
	 */
	List<Integer> getDistinctYearList(Long companyId, Long categoryId);

	CompanyDocument findByConditionSourceTextAndDesc(Long companyId,
			Long documentCategoryId, int year, int part, Long monthId,
			String source, String employeeNumber);

	void updatePayslipReleaseStatus(Long companyId, Long monthId, int year,
			int part, Long documentCategoryId, Boolean released);

	List<CompanyDocument> getPayslips(PayslipConditionDTO payslipConditionDTO);

	List<Integer> getParts(PayslipConditionDTO payslipConditionDTO);

	List<Integer> findByConditionSourceTextAndDescWithoutPart(Long companyId,
			Long documentCategoryId, int year, Long monthId, String source,
			String employeeNumber);

	List<CompanyDocument> findByConditionSourceTextAndDesc(Long companyId,
			Long documentCategoryId, int year, int part, Long monthId,
			String source);

}
