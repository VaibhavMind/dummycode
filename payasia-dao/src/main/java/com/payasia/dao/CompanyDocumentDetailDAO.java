package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;

import com.payasia.common.dto.CompanyDocumentConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyDocumentDetail;

/**
 * The Interface CompanyDocumentDetailDAO.
 */
public interface CompanyDocumentDetailDAO {

	/**
	 * Save CompanyDocumentDetail Object.
	 * 
	 * @param companyDocumentDetail
	 *            the company document detail
	 */
	void save(CompanyDocumentDetail companyDocumentDetail);

	/**
	 * Update CompanyDocumentDetail Object.
	 * 
	 * @param companyDocumentDetail
	 *            the company document detail
	 */
	void update(CompanyDocumentDetail companyDocumentDetail);

	/**
	 * Find CompanyDocumentDetail Objects List by condition
	 * CompanyDocumentConditionDTO and companyId.
	 * 
	 * @param companyDocumentConditionDTO
	 *            the company document condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<CompanyDocumentDetail> findByCondition(
			CompanyDocumentConditionDTO companyDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	/**
	 * Find CompanyDocumentDetail Object by docId.
	 * 
	 * @param docId
	 *            the doc id
	 * @return the company document detail
	 */
	CompanyDocumentDetail findById(long docId);

	/**
	 * Delete CompanyDocumentDetail Object.
	 * 
	 * @param companyDocumentDetail
	 *            the company document detail
	 */
	void delete(CompanyDocumentDetail companyDocumentDetail);

	/**
	 * Gets the count for condition companyId and CompanyDocumentConditionDTO.
	 * 
	 * @param companyDocumentConditionDTO
	 *            the company document condition dto
	 * @param companyId
	 *            the company id
	 * @return the count for condition
	 */
	Integer getCountForCondition(
			CompanyDocumentConditionDTO companyDocumentConditionDTO,
			Long companyId);

	/**
	 * Find CompanyDocumentDetail Object by companyId ,fileName ,categoryName
	 * and categoryId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param fileName
	 *            the file name
	 * @param year
	 *            the year
	 * @param categoryName
	 *            the category name
	 * @param categoryId
	 *            the category id
	 * @return the company document detail
	 */
	CompanyDocumentDetail findByCompanyDocumentId(Long companyId,
			String fileName, Integer year, String categoryName, Long categoryId);

	int getCountForConditionCmp(
			CompanyDocumentConditionDTO companyDocumentConditionDTO,
			Long companyId);

	List<CompanyDocumentDetail> findByConditionCmp(
			CompanyDocumentConditionDTO companyDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	CompanyDocumentDetail findByFileNameAndCondition(Long companyId,
			String fileName, int year, long monthId, int part,
			String companyDocumentUploadSourcePdf);

	List<Tuple> findTuplesByConditionCmp(
			CompanyDocumentConditionDTO companyDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	int getCountForTuplesbyConditionCmp(
			CompanyDocumentConditionDTO companyDocumentConditionDTO,
			Long companyId);

	List<CompanyDocument> findByConditionPdf(CompanyDocumentConditionDTO companyDocumentConditionDTO, Long monthId,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	Integer getCountForConditionPdf(CompanyDocumentConditionDTO companyDocumentConditionDTO, Long companyId,
			Long monthId);

}
