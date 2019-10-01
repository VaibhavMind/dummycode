package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.MultiLingualData;

/**
 * The Interface MultiLingualDataDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface MultiLingualDataDAO {

	/**
	 * Find MultiLingualData Object by dictionaryId and languageId.
	 * 
	 * @param dictionaryId
	 *            the dictionary id
	 * @param languageId
	 *            the language id
	 * @return the multi lingual data
	 */
	MultiLingualData findByDictionaryIdAndLanguage(Long dictionaryId,
			Long languageId);

	/**
	 * Update MultiLingualData Object.
	 * 
	 * @param multiLingualData
	 *            the multi lingual data
	 */
	void update(MultiLingualData multiLingualData);

	/**
	 * Delete MultiLingualData Object.
	 * 
	 * @param multiLingualData
	 *            the multi lingual data
	 */
	void delete(MultiLingualData multiLingualData);

	/**
	 * Save MultiLingualData Object.
	 * 
	 * @param multiLingualData
	 *            the multi lingual data
	 */
	void save(MultiLingualData multiLingualData);

	/**
	 * Find MultiLingualData Object by entity id
	 * ,dictionaryId,languageId,companyId, and dataDictionaryName dictionary
	 * name.
	 * 
	 * @param dictionaryId
	 *            the dictionary id
	 * @param languageId
	 *            the language id
	 * @param companyId
	 *            the company id
	 * @param enityId
	 *            the enity id
	 * @param dataDictionaryName
	 *            the data dictionary name
	 * @return the multi lingual data
	 */
	MultiLingualData findByEntityIdCompanyIdAndDictionaryName(
			Long dictionaryId, Long languageId, Long companyId, Long enityId,
			String dataDictionaryName);

	/**
	 * Find MultiLingualData Object by languageId, CompanyId & entityId
	 * 
	 * @param languageId
	 * @param companyId
	 * @param enityId
	 * @return
	 */
	List<MultiLingualData> findByLanguageEntityCompany(Long languageId,
			Long companyId, Long entityId);
	
	 MultiLingualData findByDictionaryIdCompanyAndLanguage(
				Long dictionaryId, Long languageId, Long companyId);

	

}
