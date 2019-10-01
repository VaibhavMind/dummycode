/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.ExcelImportExportConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmpDataExportTemplate;

/**
 * The Interface EmpDataExportTemplateDAO.
 */
public interface EmpDataExportTemplateDAO {

	/**
	 * Purpose: To save a EmpDataExportTemplate object.
	 * 
	 * @param empDataExportTemplate
	 *            the emp data export template
	 * @return the emp data export template
	 */
	EmpDataExportTemplate save(EmpDataExportTemplate empDataExportTemplate);

	/**
	 * Purpose: To find all EmpDataExportTemplate objects based on companyId.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<EmpDataExportTemplate> findAll(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	/**
	 * Purpose: To get count for EmpDataExportTemplate objects based on
	 * companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the count for all
	 */
	int getCountForAll(Long companyId);

	/**
	 * Purpose: To Delete a EmpDataExportTemplate object.
	 * 
	 * @param empDataExportTemplate
	 *            the emp data export template
	 */
	void delete(EmpDataExportTemplate empDataExportTemplate);

	/**
	 * Purpose: To Find a EmpDataExportTemplate object based on template id.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the emp data export template
	 */
	EmpDataExportTemplate findById(Long templateId);

	/**
	 * Purpose: To Update a EmpDataExportTemplate object.
	 * 
	 * @param empDataExportTemplate
	 *            the emp data export template
	 */
	void update(EmpDataExportTemplate empDataExportTemplate);

	/**
	 * Purpose: To Get the sort path for export list.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param empExportRoot
	 *            the emp export root
	 * @return the sort path for export list
	 */
	Path<String> getSortPathForExportList(SortCondition sortDTO,
			Root<EmpDataExportTemplate> empExportRoot);

	/**
	 * Purpose: To Find a EmpDataExportTemplate object based on template Name.
	 * 
	 * @param templateName
	 *            the template name
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<EmpDataExportTemplate> findByName(String templateName, long companyId);

	/**
	 * Purpose: To get count for EmpDataExportTemplate objects based on
	 * conditionDTO.
	 * 
	 * @param companyId
	 *            the company id
	 * @param conditionDTO
	 *            the condition dto
	 * @return the count for all
	 */
	int getCountForAll(Long companyId,
			ExcelImportExportConditionDTO conditionDTO);

	/**
	 * Purpose: To Find EmpDataExportTemplate objects based on conditionDTO.
	 * 
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @param conditionDTO
	 *            the condition dto
	 * @return the list
	 */
	List<EmpDataExportTemplate> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId,
			ExcelImportExportConditionDTO conditionDTO);

	Long getCountForDataExport(Long companyId,
			ExcelImportExportConditionDTO conditionDTO);

	Long getCountForExcelExport(Long companyId,
			ExcelImportExportConditionDTO conditionDTO);

}
