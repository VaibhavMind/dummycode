package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmpDataImportTemplateField;

 
/**
 * The Interface EmpDataImportTemplateFieldDAO.
 */
public interface EmpDataImportTemplateFieldDAO {

	/**
	 * Save.
	 * 
	 * @param empDataImportTemplateField
	 *            the emp data import template field
	 */
	void save(EmpDataImportTemplateField empDataImportTemplateField);

	/**
	 * Update.
	 * 
	 * @param empDataImportTemplateField
	 *            the emp data import template field
	 */
	void update(EmpDataImportTemplateField empDataImportTemplateField);

	/**
	 * Delete.
	 * 
	 * @param empDataImportTemplateField
	 *            the emp data import template field
	 */
	void delete(EmpDataImportTemplateField empDataImportTemplateField);

	/**
	 * Find by id.
	 * 
	 * @param fieldId
	 *            the field id
	 * @return the emp data import template field
	 */
	EmpDataImportTemplateField findById(long fieldId);

	/**
	 * Find by condition form id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @return the list
	 */
	List<EmpDataImportTemplateField> findByConditionFormId(Long companyId,
			Long entityId, Long formId);

	/**
	 * Find by excel field.
	 * 
	 * @param excelFieldName
	 *            the excel field name
	 * @param templateId
	 *            the template id
	 * @return the emp data import template field
	 */
	List<EmpDataImportTemplateField> findByExcelField(String excelFieldName,
			long templateId);

	void deleteByCondition(Long importTemplateId);

}
