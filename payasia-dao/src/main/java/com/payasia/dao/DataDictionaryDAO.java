/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.DataDictionary;

/**
 * The Interface DataDictionaryDAO.
 */
public interface DataDictionaryDAO {

	/**
	 * Purpose: To find Data Dictionary Object by id.
	 * 
	 * @param dataDictionaryId
	 *            the data dictionary id
	 * @return the data dictionary
	 */
	DataDictionary findById(long dataDictionaryId);

	/**
	 * Purpose: To save Data Dictionary Object.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 */
	void save(DataDictionary dataDictionary);

	/**
	 * Purpose: To find Data Dictionary Objects based on form Id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @return the list
	 */
	List<DataDictionary> findByConditionFormId(Long companyId, Long entityId,
			Long formId);

	/**
	 * Purpose: To get the count of Data Dictionary Objects based on form Id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @return the count by condition
	 */
	int getCountByCondition(Long companyId, Long entityId, Long formId);

	/**
	 * Purpose: To Delete Data Dictionary Objects based on form Id.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 */
	void deleteByCondition(Long companyId, Long entityId, Long formId);

	/**
	 * Purpose: To update a Data Dictionary Object.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 */
	void update(DataDictionary dataDictionary);

	/**
	 * Purpose: To Delete a Data Dictionary Object.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 */
	void delete(DataDictionary dataDictionary);

	/**
	 * Purpose: To find Data Dictionary Objects based on company Id, entity Id
	 * and Field type(Static or Dynamic).
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param fieldType
	 *            the field type
	 * @return the list
	 */
	List<DataDictionary> findByConditionEntityAndCompanyId(Long companyId,
			Long entityId, String fieldType);

	/**
	 * Purpose: To find Data Dictionary Objects based on entity Id and Field
	 * type(Static or Dynamic).
	 * 
	 * @param entityId
	 *            the entity id
	 * @param fieldType
	 *            the field type
	 * @return the list
	 */
	List<DataDictionary> findByConditionEntity(Long entityId, String fieldType);

	/**
	 * Purpose: To find a Data Dictionary Object based on Data Dictionary Name
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param dictionaryName
	 *            the dictionary name
	 * @param formId
	 *            the form id
	 * @return the data dictionary
	 */
	DataDictionary findByDictionaryName(Long companyId, Long entityId,
			String dictionaryName, Long formId);

	/**
	 * Purpose: To find Data Dictionary Objects based on Language Id.
	 * 
	 * @param languageId
	 *            the language id
	 * @param entityId
	 *            the entity id
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<DataDictionary> findByConditionLanguageIdEntityIdCompanyId(
			Long languageId, Long entityId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Purpose: To get the count of Data Dictionary Objects based on Language
	 * Id.
	 * 
	 * @param languageId
	 *            the language id
	 * @param entityId
	 *            the entity id
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the count bycondition language id entity id company id
	 */
	int getCountByconditionLanguageIdEntityIdCompanyId(Long languageId,
			Long entityId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Purpose: To get Gets the sort path for multilingual labels.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param dataDictionaryRoot
	 *            the data dictionary root
	 * @return the sort path for multilingual labels
	 */
	Path<String> getSortPathForMultilingualLabels(SortCondition sortDTO,
			Root<DataDictionary> dataDictionaryRoot);

	/**
	 * Purpose: To find Data Dictionary Objects based on importable.
	 * 
	 * @param companyId
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param fieldType
	 *            the field type
	 * @param importable
	 *            the importable
	 * @return the list
	 */
	List<DataDictionary> findByConditionImportable(Long companyId,
			Long entityId, String fieldType, Boolean importable);

	/**
	 * Purpose: To save a Data Dictionary Objects and return a the persisted
	 * object containing the generated Identity.
	 * 
	 * @param saveObj
	 *            the save obj
	 * @return the data dictionary
	 */
	DataDictionary saveReturn(DataDictionary saveObj);

	/**
	 * Purpose: To find Data Dictionary Objects based on CompanyId, EntityId &
	 * FormId
	 * 
	 * @param companyId
	 * @param entityId
	 * @param formId
	 * @return
	 */
	List<DataDictionary> findByCompanyEntityForm(Long companyId, Long entityId,
			Long formId);

	List<Object[]> findAllTabFields(long entityId,
			List<String> distinctTabList, long groupId);

	DataDictionary findByDictionaryNameGroup(Long companyId, Long entityId,
			String dictionaryName, Long formId, String fieldType);

	List<DataDictionary> findByCondition(Long companyId, Long entityId);

	/**
	 * Find all Datadictionary for a company for caching
	 * 
	 * @param companyId
	 * @return
	 */
	List<DataDictionary> findByCompanyId(Long companyId);

	List<DataDictionary> findByEntityIdFieldType(Long entityId, String fieldType);

	List<DataDictionary> findByConditionEntityAndCompanyIdAndFormula(
			Long companyId, Long entityId);

	DataDictionary findByDictionaryName(Long companyId, Long entityId,
			String dictionaryName);

	/**
	 * Purpose: get Static and Custom Data Dictionary Fields based on CompanyId,
	 * EntityId
	 * 
	 * @param companyId
	 * @param entityId
	 * @return List
	 */
	List<DataDictionary> getStaticAndCustomFieldList(Long companyId,
			Long entityIdForStaticField);

	DataDictionary findByCondition(Long companyId, Long entityId, Long formId,
			String dataDictName, String dataType);
	
	DataDictionary findById(long dataDictionaryId,Long companyId);

	List<DataDictionary> findAllSection(Long companyId);

}
