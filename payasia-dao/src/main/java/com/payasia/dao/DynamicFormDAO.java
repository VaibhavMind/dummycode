/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.Tuple;

import com.payasia.common.dto.ManageRolesConditionDTO;
import com.payasia.dao.bean.DynamicForm;

/**
 * The Interface DynamicFormDAO.
 */
public interface DynamicFormDAO {

	/**
	 * Purpose: Save a dynamicForm object.
	 * 
	 * @param dynamicForm
	 *            the dynamic form
	 */
	void save(DynamicForm dynamicForm);

	/**
	 * Purpose: Update a dynamicForm object.
	 * 
	 * @param dynamicForm
	 *            the dynamic form
	 */
	void update(DynamicForm dynamicForm);

	/**
	 * Purpose: Find DynamicForm object based on Maximum version and given
	 * FormId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @return the integer
	 */

	DynamicForm findMaxVersionByFormId(long companyId, long entityId,
			Long formId);

	/**
	 * Purpose: Find List of DynamicForm object based on Section Name
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param tabName
	 *            the tab name
	 * @return the list
	 */
	List<DynamicForm> findByCondition(long companyId, long entityId,
			String tabName);

	/**
	 * Purpose: To Get the count of DynamicForm object based on Section Name.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param tabName
	 *            the tab name
	 * @return the count by condition
	 */
	int getCountByCondition(long companyId, long entityId, String tabName);

	/**
	 * Purpose: To Get the max form id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @return the max form id
	 */
	Long getMaxFormId(Long companyId, Long entityId);

	/**
	 * Purpose: To Get the max version based on Section Name.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param tabName
	 *            the tab name
	 * @return the max version
	 */
	Integer getMaxVersion(long companyId, long entityId, String tabName);

	/**
	 * Purpose: Find DynamicForm object based on given Maximum version and given
	 * Section Name.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param version
	 *            the version
	 * @param tabName
	 *            the tab name
	 * @return the dynamic form
	 */
	DynamicForm findByMaxVersion(long companyId, long entityId, int version,
			String tabName);

	/**
	 * Purpose: To Get the distinct Form Id List.
	 * 
	 * @param CompanyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @return the distinct form id
	 */
	List<Long> getDistinctFormId(Long CompanyId, Long entityId);

	/**
	 * Purpose: To Get the max version based on form id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @return the max version by form id
	 */
	int getMaxVersionByFormId(Long companyId, long entityId, long formId);

	/**
	 * Purpose: Find DynamicForm object based on given Maximum version and given
	 * form id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param maxVersion
	 *            the max version
	 * @param formId
	 *            the form id
	 * @return the dynamic form
	 */
	DynamicForm findByMaxVersionByFormId(Long companyId, long entityId,
			int maxVersion, long formId);

	/**
	 * Purpose: To Delete dynamic form objects based form id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 */
	void deleteByCondition(long companyId, long entityId, long formId);

	List<Object[]> getSectionsForEffectiveDate(Long companyId, Long entityId,
			Long formId);

	DynamicForm findByMaxVersionByFormIdReadWrite(Long companyId,
			long entityId, int maxVersion, long formId);

	void getDetachedEntity(DynamicForm dynamicForm);

	List<Object[]> getEffectiveDateByCondition(Long companyId, Long entityId,
			String tabName, int effectiveYear, Long monthId, int effectivePart);

	List<Object[]> getEffectiveDateByCondition(Long companyId, Long entityId,
			Long formId, int effectiveYear, Long monthId, int effectivePart);

	DynamicForm getDynamicFormBasedOnEffectiveDate(Long companyId,
			Long entityId, Long formId, int effectiveYear, Long monthId,
			int effectivePart);

	DynamicForm getDynamicFormBasedOnEffectiveDate(Long companyId,
			Long entityId, String tabName, int effectiveYear, Long monthId,
			int effectivePart);

	DynamicForm findMaxVersionBySection(long companyId, long entityId,
			String sectionName);

	List<String> getDistinctTabNames(Long companyGroupId, Long entityId);

	DynamicForm findByCondition(long companyId, long entityId, Integer year,
			Long month, Integer part);

	DynamicForm saveAndReturn(DynamicForm dynamicForm);

	Integer getDynamicFormCount(Long companyId, String entityName);

	/**
	 * Purpose: Find Distinct Form IDs by given EntityId,CompanyId For Company
	 * Group (IRF)
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param tabListForCompanyIRFMY
	 * @param isCompany
	 *            IFRMY the is Company IFRMY
	 * @return the integer 
	 */

	List<Long> getDistinctFormIdForCompanyGroupIRF(Long companyId,
			Long entityId, boolean isCompanyGroupIFR,
			List<String> tabListForCompanyGroupIRF);

	List<Tuple> getDistinctSectionNameAndFormId(Long companyId, Long entityId);

	List<Object[]> getTabNameAndFormIdByMaxVersion(long companyId,
			Long entityId, ManageRolesConditionDTO conditionDTO);

	List<Long> getAuthorizedDistinctFormIds(Long companyId, Long entityId,
			Set<Long> sectionIds);
}
