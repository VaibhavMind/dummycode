package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.CodeDesc;
import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.dto.LanguageMasterDTO;
import com.payasia.common.dto.MultilingualConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.MultilingualForm;
import com.payasia.common.form.MultilingualResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CountryMasterDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.LanguageMasterDAO;
import com.payasia.dao.MultiLingualDataDAO;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.LanguageMaster;
import com.payasia.dao.bean.MultiLingualData;
import com.payasia.dao.bean.MultiLingualDataPK;
import com.payasia.logic.MultilingualLogic;

/**
 * The Class MultilingualLogicImpl.
 */
@Component
public class MultilingualLogicImpl implements MultilingualLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(MultilingualLogicImpl.class);

	/** The multi lingual data dao. */
	@Resource
	MultiLingualDataDAO multiLingualDataDAO;

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The language master dao. */
	@Resource
	LanguageMasterDAO languageMasterDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The dynamic form field ref value dao. */
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	MessageSource messageSource;
	@Resource
	CountryMasterDAO countryMasterDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.MultilingualLogic#convertLabelsToSpecificLanguage(java
	 * .lang.String, java.lang.Long, java.lang.Long, java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public String convertLabelsToSpecificLanguage(String metaData, Long languageId, Long companyID, Long entityId,
			Long formId) {
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {

			Unmarshaller unmarshaller = null;
			unmarshaller = XMLUtil.getDocumentUnmarshaller();

			final StringReader xmlReader = new StringReader(metaData);
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(), e1);
			}
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);
			Tab tab = null;
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
			List<Field> listOfFields = tab.getField();
			String label = "";
			Long dataDictionaryId = null;
			String dataDictionaryName = "";
			DataDictionary dataDictionary;

			List<DataDictionary> dataDictionaryList = dataDictionaryDAO.findByCompanyEntityForm(companyID, entityId,
					formId);
			Map<String, DataDictionary> dataDictionaryMap = new HashMap<>();
			for (DataDictionary dictionary : dataDictionaryList) {
				dataDictionaryMap.put(dictionary.getDataDictName(), dictionary);
			}

			List<MultiLingualData> multiLingualDataList = multiLingualDataDAO.findByLanguageEntityCompany(languageId,
					companyID, entityId);
			Map<String, MultiLingualData> multiLingualDataMap = new HashMap<>();
			for (MultiLingualData multiLingualData : multiLingualDataList) {
				multiLingualDataMap.put(multiLingualData.getDataDictionary().getDataDictName(), multiLingualData);
			}

			for (Field field : listOfFields) {

				if (field.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {

					dataDictionaryId = field.getDictionaryId();

					dataDictionaryName = new String(Base64.decodeBase64(field.getDictionaryName().getBytes()));

					dataDictionary = dataDictionaryMap.get(dataDictionaryName);

					if (field.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& dataDictionary != null) {
						List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

						field.getCodeDesc().clear();
						field.getCodeDesc().addAll(codeDescList);

					}

					MultiLingualData multiLingual1Data = multiLingualDataMap.get(dataDictionaryName);

					if (multiLingual1Data != null) {
						label = URLEncoder.encode(multiLingual1Data.getLabel(), "UTF-8");
						field.setLabel(new String(Base64
								.encodeBase64(StringEscapeUtils.escapeXml(multiLingual1Data.getLabel()).getBytes())));
					}

					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						dataDictionaryId = column.getDictionaryId();
						dataDictionaryName = new String(Base64.decodeBase64(column.getDictionaryName().getBytes()));

						dataDictionary = dataDictionaryMap.get(dataDictionaryName);
						if (column.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
								&& dataDictionary != null) {

							List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

							column.getCodeDesc().clear();
							column.getCodeDesc().addAll(codeDescList);

						}

						MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryName);
						if (multiLingualData != null) {
							label = URLEncoder.encode(multiLingualData.getLabel(), "UTF-8");
							column.setLabel(new String(Base64.encodeBase64(
									StringEscapeUtils.escapeXml(multiLingualData.getLabel()).getBytes())));
						}
					}
				} else if (field.getType().equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						dataDictionaryId = column.getDictionaryId();
						dataDictionaryName = new String(Base64.decodeBase64(column.getDictionaryName().getBytes()));

						dataDictionary = dataDictionaryMap.get(dataDictionaryName);
						if (column.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DROPDOWN)
								&& dataDictionary != null && dataDictionary.getLabel().equalsIgnoreCase(
										PayAsiaConstants.PAYASIA_EMP_DYNFIELD_TABLE_COLNAME_DEPENDENT_NATIONALITY)) {
							List<String> nationalityList = getComboDependentNationalityList();
							column.getOption().clear();
							column.getOption().addAll(nationalityList);
						}
					}
				} else {
					dataDictionaryId = field.getDictionaryId();

					dataDictionaryName = new String(Base64.decodeBase64(field.getDictionaryName().getBytes()));

					dataDictionary = dataDictionaryMap.get(dataDictionaryName);

					if (field.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& dataDictionary != null) {
						List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

						field.getCodeDesc().clear();
						field.getCodeDesc().addAll(codeDescList);

					}

					MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryName);

					if (multiLingualData != null) {
						label = URLEncoder.encode(multiLingualData.getLabel(), "UTF-8");
						field.setLabel(new String(Base64
								.encodeBase64(StringEscapeUtils.escapeXml(multiLingualData.getLabel()).getBytes())));
					}
				}

			}

			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);

		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		} catch (XMLStreamException xMLStreamException) {
			LOGGER.error(xMLStreamException.getMessage(), xMLStreamException);
			throw new PayAsiaSystemException(xMLStreamException.getMessage(), xMLStreamException);
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
			throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
			throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		}

		return byteArrayOutputStream.toString();
	}

	@Override
	public void convertLabelsToSpecificLanguage(Tab tab, Long languageId, Long companyID, Long entityId, Long formId) {
		try {

			List<Field> listOfFields = tab.getField();
			String dataDictionaryName = "";
			DataDictionary dataDictionary;

			List<DataDictionary> dataDictionaryList = dataDictionaryDAO.findByCompanyEntityForm(companyID, entityId,
					formId);
			Map<String, DataDictionary> dataDictionaryMap = new HashMap<>();
			for (DataDictionary dictionary : dataDictionaryList) {
				dataDictionaryMap.put(dictionary.getDataDictName(), dictionary);
			}

			List<MultiLingualData> multiLingualDataList = multiLingualDataDAO.findByLanguageEntityCompany(languageId,
					companyID, entityId);
			Map<String, MultiLingualData> multiLingualDataMap = new HashMap<>();
			for (MultiLingualData multiLingualData : multiLingualDataList) {
				multiLingualDataMap.put(multiLingualData.getDataDictionary().getDataDictName(), multiLingualData);
			}

			for (Field field : listOfFields) {

				if (field.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {

					dataDictionaryName = new String(Base64.decodeBase64(field.getDictionaryName().getBytes()));

					dataDictionary = dataDictionaryMap.get(dataDictionaryName);

					if (field.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& dataDictionary != null) {
						List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());
						field.getCodeDesc().clear();
						field.getCodeDesc().addAll(codeDescList);
					}

					MultiLingualData multiLingual1Data = multiLingualDataMap.get(dataDictionaryName);

					if (multiLingual1Data != null) {
						field.setLabel(new String(Base64
								.encodeBase64(StringEscapeUtils.escapeXml(multiLingual1Data.getLabel()).getBytes())));
					}

					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						dataDictionaryName = new String(Base64.decodeBase64(column.getDictionaryName().getBytes()));

						dataDictionary = dataDictionaryMap.get(dataDictionaryName);
						if (column.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
								&& dataDictionary != null) {
							List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

							column.getCodeDesc().clear();
							column.getCodeDesc().addAll(codeDescList);
						}

						MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryName);
						if (multiLingualData != null) {
							column.setLabel(new String(Base64.encodeBase64(
									StringEscapeUtils.escapeXml(multiLingualData.getLabel()).getBytes())));
						}
					}
				} else if (field.getType().equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						dataDictionaryName = new String(Base64.decodeBase64(column.getDictionaryName().getBytes()));

						dataDictionary = dataDictionaryMap.get(dataDictionaryName);
						if (column.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DROPDOWN)
								&& dataDictionary != null && dataDictionary.getLabel().equalsIgnoreCase(
										PayAsiaConstants.PAYASIA_EMP_DYNFIELD_TABLE_COLNAME_DEPENDENT_NATIONALITY)) {
							List<String> nationalityList = getComboDependentNationalityList();
							column.getOption().clear();
							column.getOption().addAll(nationalityList);
						}
					}
				} else {
					dataDictionaryName = new String(Base64.decodeBase64(field.getDictionaryName().getBytes()));

					dataDictionary = dataDictionaryMap.get(dataDictionaryName);

					if (field.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& dataDictionary != null) {
						List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

						field.getCodeDesc().clear();
						field.getCodeDesc().addAll(codeDescList);

					}

					MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryName);

					if (multiLingualData != null) {
						field.setLabel(new String(Base64
								.encodeBase64(StringEscapeUtils.escapeXml(multiLingualData.getLabel()).getBytes())));
					}
				}

			}
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
			throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
		}
	}

	@Override
	public String convertSectionNameToSpecificLanguage(String sectionName, Long languageId, Long companyID,
			Long entityId, Long formId) {

		String label = sectionName;

		DataDictionary dataDictionaryVO = dataDictionaryDAO.findByCondition(companyID, entityId, formId, sectionName,
				PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_SECTION);
		if (dataDictionaryVO != null) {
			List<MultiLingualData> multiLingualDataList = multiLingualDataDAO.findByLanguageEntityCompany(languageId,
					companyID, entityId);
			Map<String, MultiLingualData> multiLingualDataMap = new HashMap<>();
			for (MultiLingualData multiLingualData : multiLingualDataList) {
				multiLingualDataMap.put(multiLingualData.getDataDictionary().getDataDictName(), multiLingualData);
			}

			MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryVO.getDataDictName());

			if (multiLingualData != null) {
				try {
					label = URLEncoder.encode(multiLingualData.getLabel(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		}

		return label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.MultilingualLogic#getEntityList()
	 */
	@Override
	public List<EntityMasterDTO> getEntityList() {
		Locale locale = UserContext.getLocale();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		List<EntityMasterDTO> entityList = new ArrayList<EntityMasterDTO>();

		for (EntityMaster entityMaster : entityMasterList) {

			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)
					|| entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.COMPANY_ENTITY_NAME)
					|| entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)
					|| entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.PAY_SLIP_ENTITY_NAME)) {
				EntityMasterDTO entityMasterDTO = new EntityMasterDTO();
				entityMasterDTO.setEntityId(entityMaster.getEntityId());
				if (StringUtils.isNotBlank(entityMaster.getLabelKey())) {
					String labelMsg = messageSource.getMessage(entityMaster.getLabelKey(), new Object[] {}, locale);

					if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {
						labelMsg = messageSource.getMessage(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME_MSG_KEY,
								new Object[] {}, locale);
					}
					entityMasterDTO.setEntityName(labelMsg);
				} else {
					entityMasterDTO.setEntityName(entityMaster.getEntityName());
				}

				entityMasterDTO.setEntityDesc(entityMaster.getEntityDesc());

				entityList.add(entityMasterDTO);
			}
		}

		return entityList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.MultilingualLogic#getLanguageList()
	 */
	@Override
	public List<LanguageMasterDTO> getLanguageList() {
		List<LanguageMaster> languageMasterList = languageMasterDAO.getLanguages();
		List<LanguageMasterDTO> languageList = new ArrayList<LanguageMasterDTO>();

		for (LanguageMaster languageMaster : languageMasterList) {
			LanguageMasterDTO languageMasterDTO = new LanguageMasterDTO();
			languageMasterDTO.setLanguageId(languageMaster.getLanguageId());
			languageMasterDTO.setLanguage(languageMaster.getLanguage());
			languageMasterDTO.setLanguageDesc(languageMaster.getLanguageDesc());
			languageList.add(languageMasterDTO);
		}

		return languageList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.MultilingualLogic#getMultilingualLabelsList(java.lang
	 * .Long, java.lang.Long, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public MultilingualResponse getMultilingualLabelsList(Long entityId, Long languageId, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		MultilingualConditionDTO conditionDTO = new MultilingualConditionDTO();

		int recordSize = 0;
		conditionDTO.setCompanyId(companyId);
		conditionDTO.setLanguageId(languageId);
		conditionDTO.setEntityId(entityId);

		List<DataDictionary> dataDictionaries;
		dataDictionaries = dataDictionaryDAO.findByConditionLanguageIdEntityIdCompanyId(languageId, entityId, companyId,
				pageDTO, sortDTO);
		recordSize = dataDictionaryDAO.getCountByconditionLanguageIdEntityIdCompanyId(languageId, entityId, companyId,
				null, null);
		List<MultilingualForm> multilingualFormList = new ArrayList<MultilingualForm>();
		for (DataDictionary dataDictionary : dataDictionaries) {
			MultilingualForm multilingualForm = new MultilingualForm();
			/*
			 * ID ENCRYPT
			 */
			multilingualForm
					.setDataDictionaryId(FormatPreserveCryptoUtil.encrypt(dataDictionary.getDataDictionaryId()));

			if (dataDictionary.getFieldType().equals(PayAsiaConstants.STATIC_TYPE)) {
				multilingualForm.setLabel(dataDictionary.getDataDictName());
			} else {
				multilingualForm.setLabel(dataDictionary.getLabel());
			}

			for (MultiLingualData lingualData : dataDictionary.getMultiLingualData()) {
				if (lingualData.getLanguageMaster().getLanguageId() == languageId) {
					multilingualForm.setLabelValue(lingualData.getLabel());
				}
			}
			if (dataDictionary.getCompany() != null) {
				multilingualForm.setCompanyId(dataDictionary.getCompany().getCompanyId());
			}

			multilingualForm.setLanguageId(languageId);
			multilingualForm.setEntityId(dataDictionary.getEntityMaster().getEntityId());
			multilingualForm.setColumnName(dataDictionary.getColumnName());
			multilingualForm.setDataDictName(dataDictionary.getDataDictName());
			multilingualForm.setDescription(dataDictionary.getDescription());
			multilingualFormList.add(multilingualForm);
		}
		MultilingualResponse multilingualResponse = new MultilingualResponse();

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			multilingualResponse.setPage(pageDTO.getPageNumber());
			multilingualResponse.setTotal(totalPages);
			multilingualResponse.setRecords(recordSize);
		}
		multilingualResponse.setRows(multilingualFormList);
		return multilingualResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.MultilingualLogic#updateMultilingualLabel(java.lang
	 * .Long, java.lang.Long, java.lang.String)
	 */
	public void updateMultilingualLabel(Long dataDictionaryId, Long languageId, String labelValue, Long companyId) {
		MultiLingualData multiLingualData = multiLingualDataDAO.findByDictionaryIdCompanyAndLanguage(dataDictionaryId,
				languageId, companyId);

		try {
			labelValue = URLDecoder.decode(labelValue, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (multiLingualData != null) {
			multiLingualData.setLabel(labelValue);
			multiLingualDataDAO.update(multiLingualData);
		} else {
			multiLingualData = new MultiLingualData();

			multiLingualData.setLabel(labelValue);

			LanguageMaster languageMaster = languageMasterDAO.findById(languageId);
			multiLingualData.setLanguageMaster(languageMaster);

			DataDictionary dataDictionary = dataDictionaryDAO.findById(dataDictionaryId);
			multiLingualData.setDataDictionary(dataDictionary);

			MultiLingualDataPK multiLingualDataPK = new MultiLingualDataPK();
			multiLingualDataPK.setData_Dictionary_ID(dataDictionaryId);
			multiLingualDataPK.setLanguage_ID(languageId);

			multiLingualData.setId(multiLingualDataPK);

			multiLingualDataDAO.save(multiLingualData);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.MultilingualLogic#deleteMultilingualRecord(java.lang
	 * .Long, java.lang.Long)
	 */
	public void deleteMultilingualRecord(Long dataDictionaryId, Long languageId, Long companyId) {
		MultiLingualData multiLingualData = multiLingualDataDAO.findByDictionaryIdCompanyAndLanguage(dataDictionaryId,
				languageId, companyId);
		if (multiLingualData != null) {
			multiLingualDataDAO.delete(multiLingualData);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.MultilingualLogic#getLanguageId(java.lang.String)
	 */
	@Override
	public Long getLanguageId(String languageCode) {
		/* Remove Variant(as #Cbaa) from Locale(as Locale = zh_US_#Cbaa) */
		if (StringUtils.isNotBlank(languageCode)) {
			if (languageCode.toString().indexOf("#") > 0) {
				languageCode = languageCode.substring(0, (languageCode.indexOf("#") - 1));
			}
		}

		LanguageMaster languageMaster = languageMasterDAO.findByLanguageCode(languageCode);
		Long languageId = 0L;
		if (languageMaster != null) {
			languageId = languageMaster.getLanguageId();
		}
		return languageId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.MultilingualLogic#getCodeDescList(java.lang.Long)
	 */
	@Override
	public List<CodeDesc> getCodeDescList(Long dataDictionaryId) {
		List<DynamicFormFieldRefValue> fieldRefList = dynamicFormFieldRefValueDAO
				.findByDataDictionayId(dataDictionaryId);

		List<CodeDesc> codeDescList = new ArrayList<CodeDesc>();

		for (DynamicFormFieldRefValue dynamicFormFieldRefValue : fieldRefList) {
			CodeDesc codeDesc = new CodeDesc();
			try {
				codeDesc.setCode(URLEncoder.encode(dynamicFormFieldRefValue.getCode(), "UTF-8"));
				codeDesc.setDesc(URLEncoder.encode(dynamicFormFieldRefValue.getDescription(), "UTF-8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
				throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);

			}

			codeDesc.setFieldRefId(dynamicFormFieldRefValue.getFieldRefValueId());
			codeDescList.add(codeDesc);

		}
		return codeDescList;
	}

	@Override
	public List<String> getComboDependentNationalityList() {
		List<CountryMaster> countryMasterList = countryMasterDAO.findAll();

		List<String> nationalityList = new ArrayList<String>();

		for (CountryMaster countryMaster : countryMasterList) {
			if (StringUtils.isNotBlank(countryMaster.getNationality())) {
				try {

					String encodedValue = new String(
							Base64.encodeBase64(URLEncoder.encode(countryMaster.getNationality(), "UTF-8").getBytes()));

					nationalityList.add(URLEncoder.encode(encodedValue, "UTF-8"));
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
					throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);

				}
			}
		}
		return nationalityList;
	}

	@Override
	public String convertLabelsToSpecificLanguageWithoutEntity(String metaData, Long languageId, Long companyID,
			Long formId) {
		Locale locale = UserContext.getLocale();

		ByteArrayOutputStream byteArrayOutputStream = null;
		try {

			Unmarshaller unmarshaller = null;
			unmarshaller = XMLUtil.getDocumentUnmarshaller();

			final StringReader xmlReader = new StringReader(metaData);
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(), e1);
			}
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);
			Tab tab = null;
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
			List<Field> listOfFields = tab.getField();
			String label = "";
			Long dataDictionaryId = null;
			String dataDictionaryName = "";
			DataDictionary dataDictionary;

			List<DataDictionary> dataDictionaryList = dataDictionaryDAO.getStaticAndCustomFieldList(companyID,
					PayAsiaConstants.PAYSLIP_FORM_ENTITY_ID);
			Map<String, DataDictionary> dataDictionaryMap = new HashMap<>();
			for (DataDictionary dictionary : dataDictionaryList) {
				dataDictionaryMap.put(dictionary.getLabel(), dictionary);
			}

			List<MultiLingualData> multiLingualDataList = multiLingualDataDAO.findByLanguageEntityCompany(languageId,
					companyID, null);
			Map<String, MultiLingualData> multiLingualDataMap = new HashMap<>();
			for (MultiLingualData multiLingualData : multiLingualDataList) {
				multiLingualDataMap.put(multiLingualData.getDataDictionary().getLabel(), multiLingualData);
			}

			for (Field field : listOfFields) {

				if (field.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						dataDictionaryId = column.getDictionaryId();
						dataDictionaryName = new String(Base64.decodeBase64(column.getDictionaryName().getBytes()));

						dataDictionary = dataDictionaryMap.get(dataDictionaryName);
						if (column.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
								&& dataDictionary != null) {

							List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

							column.getCodeDesc().clear();
							column.getCodeDesc().addAll(codeDescList);

						}

						MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryName);
						if (multiLingualData != null) {
							label = URLEncoder.encode(multiLingualData.getLabel(), "UTF-8");
							column.setDictionaryName(new String(Base64.encodeBase64(label.getBytes())));

						}
					}
				} else {
					dataDictionaryId = field.getDictionaryId();

					dataDictionaryName = new String(Base64.decodeBase64(field.getDictionaryName().getBytes()));

					dataDictionary = dataDictionaryMap.get(dataDictionaryName);
					if (dataDictionary != null
							&& dataDictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

						if (StringUtils.isNotBlank(dataDictionary.getDescription())) {
							/*
							 * String staticLabelMsg = messageSource.getMessage(
							 * dataDictionary.getDescription(), new Object[] {},
							 * locale); if
							 * (StringUtils.isNotBlank(staticLabelMsg)) {
							 * staticLabelMsg = URLEncoder.encode(
							 * staticLabelMsg, "UTF-8");
							 * field.setDictionaryName(new String(
							 * Base64.encodeBase64(staticLabelMsg
							 * .getBytes()))); }
							 */
						}

					}
					if (dataDictionary != null
							&& dataDictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.DYNAMIC_TYPE)) {
						if (field.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
							List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

							field.getCodeDesc().clear();
							field.getCodeDesc().addAll(codeDescList);

						}

						MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryName);

						if (multiLingualData != null) {
							label = URLEncoder.encode(multiLingualData.getLabel(), "UTF-8");
							field.setDictionaryName(new String(Base64.encodeBase64(label.getBytes())));

						}
					}

				}

			}

			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);

		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		} catch (XMLStreamException xMLStreamException) {
			LOGGER.error(xMLStreamException.getMessage(), xMLStreamException);
			throw new PayAsiaSystemException(xMLStreamException.getMessage(), xMLStreamException);
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
			throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return byteArrayOutputStream.toString();
	}

	@Override
	public String convertLabelsToSpecificLanguageWithoutEntityDataExportGroup(String metaData, Long languageId,
			long company_ID, long formId, List<DataDictionary> dataDictionaryList,
			List<MultiLingualData> multiLingualDataList) {
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {

			Unmarshaller unmarshaller = null;
			unmarshaller = XMLUtil.getDocumentUnmarshaller();

			final StringReader xmlReader = new StringReader(metaData);
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(), e1);
			}
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);
			Tab tab = null;
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
			List<Field> listOfFields = tab.getField();
			String label = "";
			Long dataDictionaryId = null;
			String dataDictionaryName = "";
			DataDictionary dataDictionary;

			Map<String, DataDictionary> dataDictionaryMap = new HashMap<>();
			for (DataDictionary dictionary : dataDictionaryList) {
				dataDictionaryMap.put(dictionary.getLabel(), dictionary);
			}

			Map<String, MultiLingualData> multiLingualDataMap = new HashMap<>();
			for (MultiLingualData multiLingualData : multiLingualDataList) {
				multiLingualDataMap.put(multiLingualData.getDataDictionary().getLabel(), multiLingualData);
			}

			for (Field field : listOfFields) {

				if (field.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						dataDictionaryId = column.getDictionaryId();
						dataDictionaryName = new String(Base64.decodeBase64(column.getDictionaryName().getBytes()));

						dataDictionary = dataDictionaryMap.get(dataDictionaryName);
						if (column.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
								&& dataDictionary != null) {

							List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

							column.getCodeDesc().clear();
							column.getCodeDesc().addAll(codeDescList);

						}

						MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryName);
						if (multiLingualData != null) {
							label = URLEncoder.encode(multiLingualData.getLabel(), "UTF-8");
							column.setDictionaryName(new String(Base64.encodeBase64(label.getBytes())));

						}
					}
				} else {
					dataDictionaryId = field.getDictionaryId();

					dataDictionaryName = new String(Base64.decodeBase64(field.getDictionaryName().getBytes()));

					dataDictionary = dataDictionaryMap.get(dataDictionaryName);

					if (field.getType().equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& dataDictionary != null) {
						List<CodeDesc> codeDescList = getCodeDescList(dataDictionary.getDataDictionaryId());

						field.getCodeDesc().clear();
						field.getCodeDesc().addAll(codeDescList);

					}

					MultiLingualData multiLingualData = multiLingualDataMap.get(dataDictionaryName);

					if (multiLingualData != null) {
						label = URLEncoder.encode(multiLingualData.getLabel(), "UTF-8");
						field.setDictionaryName(new String(Base64.encodeBase64(label.getBytes())));

					}
				}

			}

			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);

		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		} catch (XMLStreamException xMLStreamException) {
			LOGGER.error(xMLStreamException.getMessage(), xMLStreamException);
			throw new PayAsiaSystemException(xMLStreamException.getMessage(), xMLStreamException);
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
			throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return byteArrayOutputStream.toString();
	}

	@Override
	public String convertFieldLabelToSpecificLanguage(Long languageId, Long companyId, Long dataDictId) {
		String label = "";
		try {
			MultiLingualData multiLingualDataVO = multiLingualDataDAO.findByDictionaryIdCompanyAndLanguage(dataDictId,
					languageId, companyId);

			MultiLingualData multiLingualData = multiLingualDataVO;
			if (multiLingualData != null) {
				label = multiLingualData.getLabel();
			}
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
			throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
		}
		return label;
	}
}
