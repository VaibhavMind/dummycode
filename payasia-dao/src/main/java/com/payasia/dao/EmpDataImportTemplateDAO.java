package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.ExcelImportExportConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmpDataImportTemplate;

/**
 * The Interface EmpDataImportTemplateDAO.
 */
public interface EmpDataImportTemplateDAO {

	/**
	 * Save.
	 * 
	 * @param empDataImportTemplate
	 *            the emp data import template
	 * @return the emp data import template
	 */
	EmpDataImportTemplate save(EmpDataImportTemplate empDataImportTemplate);

	/**
	 * Find all.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 * @return the list
	 */
	List<EmpDataImportTemplate> findAll(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	/**
	 * Gets the count for all.
	 * 
	 * @param companyId
	 * 
	 * @return the count for all
	 */
	int getCountForAll(Long companyId);

	/**
	 * Delete.
	 * 
	 * @param empDataImportTemplate
	 *            the emp data import template
	 */
	void delete(EmpDataImportTemplate empDataImportTemplate);

	/**
	 * Find by id.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the emp data import template
	 */
	EmpDataImportTemplate findById(Long templateId);

	/**
	 * Update.
	 * 
	 * @param empDataImportTemplate
	 *            the emp data import template
	 */
	void update(EmpDataImportTemplate empDataImportTemplate);

	/**
	 * Find by entity.
	 * 
	 * @param entityId
	 *            the entity id
	 * @param companyId
	 * @return the list
	 */
	List<EmpDataImportTemplate> findByEntity(long entityId, Long companyId);

	Path<String> getSortPathForImportList(SortCondition sortDTO,
			Root<EmpDataImportTemplate> empImportRoot);

	List<EmpDataImportTemplate> findByName(String templateName, long companyId);

	List<EmpDataImportTemplate> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId,
			ExcelImportExportConditionDTO conditionDTO);

	int getCountForAll(Long companyId,
			ExcelImportExportConditionDTO conditionDTO);

}
