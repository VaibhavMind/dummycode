package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.EmployeeDocumentConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeDocument;

/**
 * The Interface EmployeeDocumentDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmployeeDocumentDAO {

	/**
	 * Save EmployeeDocument Object .
	 * 
	 * @param employeeDocument
	 *            the employee document
	 */
	void save(EmployeeDocument employeeDocument);

	/**
	 * Find EmployeeDocument Object by condition employeeID and
	 * EmployeeDocumentConditionDTO.
	 * 
	 * @param employeeDocumentConditionDTO
	 *            the employee document condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param empID
	 *            the emp id
	 * @return the list
	 */
	List<EmployeeDocument> findByCondition(
			EmployeeDocumentConditionDTO employeeDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long empID);

	/**
	 * Gets the count for condition EmployeeDocumentConditionDTO and employeeId.
	 * 
	 * @param employeeDocumentConditionDTO
	 *            the employee document condition dto
	 * @param employeeId
	 *            the employee id
	 * @return the count for condition
	 */
	int getCountForCondition(
			EmployeeDocumentConditionDTO employeeDocumentConditionDTO,
			Long employeeId);

	/**
	 * Find EmployeeDocument Object by docId.
	 * 
	 * @param docId
	 *            the doc id
	 * @return the employee document
	 */
	EmployeeDocument findById(long docId);

	/**
	 * Update EmployeeDocument Object.
	 * 
	 * @param employeeDocument
	 *            the employee document
	 */
	void update(EmployeeDocument employeeDocument);

	/**
	 * Delete EmployeeDocument Object.
	 * 
	 * @param employeeDocument
	 *            the employee document
	 */
	void delete(EmployeeDocument employeeDocument);

	/**
	 * Find EmployeeDocument Object by year, employeeId,fileName and documentId.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param year
	 *            the year
	 * @param fileName
	 *            the file name
	 * @param documentId
	 *            the document id
	 * @return the employee document
	 */
	EmployeeDocument findDocumentByYearEmIDocNameDocId(Long employeeId,
			Integer year, String fileName, Long documentId);

	/**
	 * Gets the sort path for all employee document.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param employeeDocumentRoot
	 *            the employee document root
	 * @return the sort path for all employee document
	 */
	Path<String> getSortPathForAllEmployeeDocument(SortCondition sortDTO,
			Root<EmployeeDocument> employeeDocumentRoot);

}
