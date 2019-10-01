/**
 * @author ragulapraveen
 *
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mind.payasia.xml.bean.CodeDesc;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.dto.LanguageMasterDTO;
import com.payasia.common.form.MultilingualResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.MultiLingualData;

/**
 * The Interface MultilingualLogic.
 */

@Transactional
public interface MultilingualLogic {

	/**
	 * Gets the language id based on languagecode.
	 * 
	 * @param languageCode
	 *            the language code
	 * @return the language id
	 */
	Long getLanguageId(String languageCode);

	/**
	 * Gets EntityMasterDTO Object list .
	 * 
	 * @return the entity list
	 */
	List<EntityMasterDTO> getEntityList();

	/**
	 * Gets LanguageMasterDTO object list.
	 * 
	 * @return the language list
	 */
	List<LanguageMasterDTO> getLanguageList();

	/**
	 * Gets the multilingual labels list by entityid,languageid,companyid.
	 * 
	 * @param entityId
	 *            the entity id
	 * @param languageId
	 *            the language id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the multilingual labels list
	 */
	MultilingualResponse getMultilingualLabelsList(Long entityId, Long languageId, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	/**
	 * Update multilingual label by datadictionaryid,languageid.
	 * 
	 * @param dataDictionaryId
	 *            the data dictionary id
	 * @param languageId
	 *            the language id
	 * @param labelValue
	 *            the label value
	 * @param companyId
	 */
	void updateMultilingualLabel(Long dataDictionaryId, Long languageId, String labelValue, Long companyId);

	/**
	 * Delete multilingual record by datadictionaryid,languageid.
	 * 
	 * @param dataDictionaryId
	 *            the data dictionary id
	 * @param languageId
	 *            the language id
	 */
	void deleteMultilingualRecord(Long dataDictionaryId, Long languageId, Long companyId);

	/**
	 * Gets the code desc list by datadictionaryid.
	 * 
	 * @param dataDictionaryId
	 *            the data dictionary id
	 * @return the code desc list
	 */
	List<CodeDesc> getCodeDescList(Long dataDictionaryId);

	/**
	 * Convert labels to specific language by languageid,metadata,companyid and
	 * entityid.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param languageId
	 *            the language id
	 * @param companyID
	 *            the company id
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @return the string
	 */
	String convertLabelsToSpecificLanguage(String metaData, Long languageId, Long companyID, Long entityId,
			Long formId);

	String convertLabelsToSpecificLanguageWithoutEntityDataExportGroup(String metaData, Long languageId,
			long company_ID, long formId, List<DataDictionary> dataDictionaryList,
			List<MultiLingualData> multiLingualDataList);

	String convertLabelsToSpecificLanguageWithoutEntity(String metaData, Long languageId, Long companyID, Long formId);

	String convertSectionNameToSpecificLanguage(String metaData, Long languageId, Long companyID, Long entityId,
			Long formId);

	String convertFieldLabelToSpecificLanguage(Long languageId, Long companyId, Long dataDictId);

	List<String> getComboDependentNationalityList();

	void convertLabelsToSpecificLanguage(Tab tab, Long languageId, Long companyID, Long entityId, Long formId);

}
