/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.CodeDesc;
import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CompanyDynamicForm;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmpDataImportTemplateFieldDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormPK;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.logic.CompanyDynamicFormLogic;
import com.payasia.logic.DataImportUtils;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * The Class CompanyDynamicFormLogicImpl.
 */
@Component
public class CompanyDynamicFormLogicImpl implements CompanyDynamicFormLogic {

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The dynamic form record dao. */
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	/** The emp data import template field dao. */
	@Resource
	EmpDataImportTemplateFieldDAO empDataImportTemplateFieldDAO;

	/** The dynamic form field ref value dao. */
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;

	/** The dynamic form table record dao. */
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	/** The data import utils. */
	@Resource
	DataImportUtils dataImportUtils;

	/** The Constant BASE_VERSION. */
	public static final int BASE_VERSION = 1;

	private static final Logger LOGGER = Logger
			.getLogger(CompanyDynamicFormLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDynamicFormLogic#saveDynamicXML(java.lang.Long,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public CompanyDynamicForm saveDynamicXML(Long companyId, String metaData,
			String tabName) {
		Long formId = null;
		Company company = companyDAO.findById(companyId);
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);

		CompanyDynamicForm companyForm = new CompanyDynamicForm();

		Integer employeeBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
				entityMaster.getEntityId(),
				PayAsiaConstants.COMPANY_BASIC_TAB_NAME);

		if (employeeBasicFormMax == null) {
			String dummyData = PayAsiaConstants.COMPANY_FORM_DESIGNER_DUMMY_XML;
			String basicFormId = saveDummyXML(companyId, dummyData,
					PayAsiaConstants.COMPANY_BASIC_TAB_NAME);
			companyForm.setAddedNewTab(true);
			companyForm.setBasicFormId(Long.parseLong(basicFormId));
		} else {
			companyForm.setAddedNewTab(false);
		}

		DynamicFormPK dynamicFormPk = new DynamicFormPK();
		DynamicForm dynamicForm = new DynamicForm();
		dynamicForm.setMetaData(metaData);
		dynamicForm.setCompany(company);
		dynamicForm.setEntityMaster(entityMaster);
		dynamicForm.setTabName(tabName);

		dynamicFormPk.setVersion(BASE_VERSION);
		synchronized (this) {
			long maxFormId = dynamicFormDAO.getMaxFormId(null, null);
			formId = maxFormId + 1;
			dynamicFormPk.setFormId(formId);
			dynamicFormPk.setCompany_ID(company.getCompanyId());
			dynamicFormPk.setEntity_ID(entityMaster.getEntityId());
			dynamicForm.setId(dynamicFormPk);
			dynamicFormDAO.save(dynamicForm);
		}
		if (!tabName.equals(PayAsiaConstants.COMPANY_BASIC_TAB_NAME)) {
			Tab tab = getTabObject(metaData);

			 
			DataDictionary dataDictionaryTab = new DataDictionary();
			dataDictionaryTab.setCompany(company);
			dataDictionaryTab.setEntityMaster(entityMaster);
			dataDictionaryTab.setDataDictName(tab.getDictionaryName());
			dataDictionaryTab.setLabel(tab.getLabel());
			dataDictionaryTab.setFormID(formId);
			dataDictionaryTab.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
			dataDictionaryTab
					.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_SECTION);
			dataDictionaryTab.setImportable(false);
			dataDictionaryDAO.save(dataDictionaryTab);
		}

		companyForm.setFormId(formId);
		return companyForm;

	}

	/**
	 * Purpose: To save the metaData and the data dictionaries for the 1st save
	 * of Basic Section.
	 * 
	 * @param companyId
	 *            the company id
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @return the string
	 */
	public String saveDummyXML(Long companyId, String metaData, String tabName) {
		Long formId = null;
		Company company = companyDAO.findById(companyId);
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);
		DynamicFormPK dynamicFormPk = new DynamicFormPK();
		DynamicForm dynamicForm = new DynamicForm();
		dynamicForm.setMetaData(metaData);
		dynamicForm.setCompany(company);
		dynamicForm.setEntityMaster(entityMaster);
		dynamicForm.setTabName(tabName);

		dynamicFormPk.setVersion(BASE_VERSION);
		synchronized (this) {
			long maxFormId = dynamicFormDAO.getMaxFormId(null, null);
			formId = maxFormId + 1;
			dynamicFormPk.setFormId(formId);
			dynamicFormPk.setCompany_ID(company.getCompanyId());
			dynamicFormPk.setEntity_ID(entityMaster.getEntityId());
			dynamicForm.setId(dynamicFormPk);
			dynamicFormDAO.save(dynamicForm);
		}

		if (dataDictionaryDAO.getCountByCondition(companyId,
				entityMaster.getEntityId(), formId) > 0) {
			dataDictionaryDAO.deleteByCondition(companyId,
					entityMaster.getEntityId(), formId);
		} else {
			LOGGER.info("No data dictionary exist.");
		}

		Tab tab = getTabObject(metaData);

		List<Field> listOfFields = tab.getField();

		for (Field field : listOfFields) {

			 
			 

			DataDictionary dataDictionary = new DataDictionary();
			dataDictionary.setCompany(company);
			dataDictionary.setEntityMaster(entityMaster);
			dataDictionary.setDataDictName(new String(Base64.decodeBase64(field
					.getDictionaryName().getBytes())));
			dataDictionary.setLabel(new String(Base64.decodeBase64(field
					.getLabel().getBytes())));
			dataDictionary.setFormID(formId);
			dataDictionary.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);

			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.LABEL_FIELD_TYPE)) {
				dataDictionary
						.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
				dataDictionary.setImportable(false);
			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {
				dataDictionary
						.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_TABLE);
				dataDictionary.setImportable(false);
			} else {
				dataDictionary
						.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
			}

			if (field.getType().equalsIgnoreCase(
					PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
				DataDictionary returnedDD = dataDictionaryDAO
						.saveReturn(dataDictionary);
				saveCodeDesc(returnedDD, field.getCodeDesc());

			} else {
				dataDictionaryDAO.save(dataDictionary);
			}

			if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {

					DataDictionary dataDictionaryVO = new DataDictionary();
					dataDictionaryVO.setCompany(company);
					dataDictionaryVO.setEntityMaster(entityMaster);
					dataDictionaryVO.setDataDictName(new String(
							Base64.decodeBase64(column.getDictionaryName()
									.getBytes())));
					dataDictionaryVO.setLabel(new String(Base64
							.decodeBase64(column.getLabel().getBytes())));
					dataDictionaryVO.setFormID(formId);
					dataDictionaryVO
							.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
					dataDictionaryVO
							.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
					if (column.getType().equalsIgnoreCase(
							PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
						DataDictionary returnedDD = dataDictionaryDAO
								.saveReturn(dataDictionaryVO);
						saveCodeDesc(returnedDD, column.getCodeDesc());

					} else {
						dataDictionaryDAO.save(dataDictionaryVO);
					}
				}
			}

		}
		return formId.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyDynamicFormLogic#saveXML(java.lang.Long,
	 * java.lang.String, java.lang.String, long)
	 */
	@Override
	public String saveXML(Long companyId, String metaData, String tabName,
			long argFormId) {

		if (argFormId == 0) {
			return saveDummyXML(companyId, metaData, tabName);
		} else {
			List<Long> existingDictionaryIds = new ArrayList<Long>();
			List<Long> tabDictionaryIds = new ArrayList<Long>();
			Company company = companyDAO.findById(companyId);
			EntityMaster entityMaster = entityMasterDAO
					.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);

			if (dataDictionaryDAO.getCountByCondition(companyId,
					entityMaster.getEntityId(), argFormId) > 0) {

				List<DataDictionary> existingDictionaries = dataDictionaryDAO
						.findByConditionFormId(companyId,
								entityMaster.getEntityId(), argFormId);

				for (DataDictionary dictionary : existingDictionaries) {
					if (!dictionary
							.getDataType()
							.equalsIgnoreCase(
									PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_SECTION)) {
						existingDictionaryIds.add(dictionary
								.getDataDictionaryId());
					}

				}

			} else {
				LOGGER.info("No data dictionary exist.");
			}

			Tab tab = getTabObject(metaData);

			 
			if (!tabName.equals(PayAsiaConstants.COMPANY_BASIC_TAB_NAME)) {
				Long dictIdTab = tab.getDictionaryId();
				if (dictIdTab > 0) {
					DataDictionary dataDictionary = dataDictionaryDAO
							.findById(tab.getDictionaryId());
					if (!dataDictionary.getDataDictName().equals(
							tab.getDictionaryName())) {

						dataDictionary.setDataDictName(tab.getDictionaryName());
						dataDictionary.setLabel(tab.getLabel());
						dataDictionary
								.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_SECTION);
						dataDictionary.setImportable(false);
						dataDictionaryDAO.update(dataDictionary);
					}
					if (dataDictionary.getLabel() == null) {
						dataDictionary
								.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_SECTION);
						dataDictionary.setImportable(false);
						dataDictionary.setLabel(tab.getLabel());
						dataDictionaryDAO.update(dataDictionary);
					}
				} else {
					DataDictionary dataDictionaryTab = new DataDictionary();
					dataDictionaryTab.setCompany(company);
					dataDictionaryTab.setEntityMaster(entityMaster);
					dataDictionaryTab.setDataDictName(tab.getDictionaryName());
					dataDictionaryTab.setLabel(tab.getLabel());
					dataDictionaryTab.setFormID(argFormId);
					dataDictionaryTab
							.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
					dataDictionaryTab
							.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_SECTION);
					dataDictionaryTab.setImportable(false);
					dataDictionaryDAO.save(dataDictionaryTab);
				}
			}

			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {

				 
				 
				 
				tabDictionaryIds.add(field.getDictionaryId());
				if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						if (column.getDictionaryId() > 0) {
							tabDictionaryIds.add(column.getDictionaryId());
						}
					}
				}
			}

			boolean firstRun = true;
			Tab oldTab = null;

			for (Long dictionaryId : existingDictionaryIds) {
				if (!(tabDictionaryIds.contains(dictionaryId))) {

					if (firstRun) {
						DynamicForm currentForm = dynamicFormDAO
								.findMaxVersionByFormId(companyId,
										entityMaster.getEntityId(), argFormId);

						oldTab = getTabObject(currentForm.getMetaData());
						firstRun = false;
					}

					DataDictionary dataDictionary = dataDictionaryDAO
							.findById(dictionaryId);

					boolean dataExist = checkForData(dataDictionary, oldTab,
							companyId, argFormId);
					if (dataExist) {
						return PayAsiaConstants.PAYASIA_DELETE;
					}

					try {
						dataDictionaryDAO.delete(dataDictionary);
					} catch (Exception exception) {
						LOGGER.error(exception.getMessage(), exception);
						throw new PayAsiaSystemException(
								PayAsiaConstants.PAYASIA_DELETE);
					}

				}
			}

			try {

				HashMap<Long, DynamicFormFieldRefValue> dynFormFieldRefHashMap = new HashMap<Long, DynamicFormFieldRefValue>();
				List<DynamicFormFieldRefValue> dynFormFieldRefValueList = dynamicFormFieldRefValueDAO
						.findByCondition(companyId, entityMaster.getEntityId(),
								argFormId);
				for (DynamicFormFieldRefValue dynamicFormFieldRefVal : dynFormFieldRefValueList) {
					dynFormFieldRefHashMap.put(
							dynamicFormFieldRefVal.getFieldRefValueId(),
							dynamicFormFieldRefVal);
				}

				for (Field field : listOfFields) {

					 
					 

					if (field.getDictionaryId() > 0) {
						DataDictionary dataDictionary = dataDictionaryDAO
								.findById(field.getDictionaryId());
						if (!dataDictionary.getDataDictName().equals(
								new String(Base64.decodeBase64(field
										.getDictionaryName().getBytes())))) {

							dataDictionary.setDataDictName(new String(Base64
									.decodeBase64(field.getDictionaryName()
											.getBytes())));
							dataDictionary
									.setLabel(new String(Base64
											.decodeBase64(field.getLabel()
													.getBytes())));
							if (StringUtils.equalsIgnoreCase(field.getType(),
									PayAsiaConstants.LABEL_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
								dataDictionary.setImportable(false);
							} else if (StringUtils.equalsIgnoreCase(
									field.getType(),
									PayAsiaConstants.TABLE_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_TABLE);
								dataDictionary.setImportable(false);
							} else {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
							}
							dataDictionaryDAO.update(dataDictionary);

						}

						if (dataDictionary.getLabel() == null) {
							if (StringUtils.equalsIgnoreCase(field.getType(),
									PayAsiaConstants.LABEL_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
								dataDictionary.setImportable(false);
							} else if (StringUtils.equalsIgnoreCase(
									field.getType(),
									PayAsiaConstants.TABLE_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_TABLE);
								dataDictionary.setImportable(false);
							} else {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
							}
							dataDictionary
									.setLabel(new String(Base64
											.decodeBase64(field.getLabel()
													.getBytes())));
							dataDictionaryDAO.update(dataDictionary);
						}

						if (field.getType().equals(
								PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
							dataDictionary
									.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
							updateCodeDesc(dynFormFieldRefHashMap,
									dataDictionary, field.getCodeDesc());
						}

					} else {
						DataDictionary dataDictionary = new DataDictionary();
						dataDictionary.setCompany(company);
						dataDictionary.setEntityMaster(entityMaster);
						dataDictionary.setDataDictName(new String(Base64
								.decodeBase64(field.getDictionaryName()
										.getBytes())));
						dataDictionary.setLabel(new String(Base64
								.decodeBase64(field.getLabel().getBytes())));

						dataDictionary.setFormID(argFormId);
						dataDictionary
								.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
						if (StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.LABEL_FIELD_TYPE)) {
							dataDictionary
									.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
							dataDictionary.setImportable(false);
						} else if (StringUtils.equalsIgnoreCase(
								field.getType(),
								PayAsiaConstants.TABLE_FIELD_TYPE)) {
							dataDictionary
									.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_TABLE);
							dataDictionary.setImportable(false);
						} else {
							dataDictionary
									.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
						}

						if (field.getType().equalsIgnoreCase(
								PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
							DataDictionary returnedDD = dataDictionaryDAO
									.saveReturn(dataDictionary);
							saveCodeDesc(returnedDD, field.getCodeDesc());

						} else {
							dataDictionaryDAO.save(dataDictionary);
						}

					}
					 
					if (field.getType().equals(
							PayAsiaConstants.TABLE_FIELD_TYPE)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (column.getDictionaryId() > 0) {

								DataDictionary dataDictionary = dataDictionaryDAO
										.findById(column.getDictionaryId());
								if (!dataDictionary.getDataDictName().equals(
										new String(Base64
												.decodeBase64(column
														.getDictionaryName()
														.getBytes())))) {

									dataDictionary.setDataDictName(new String(
											Base64.decodeBase64(column
													.getDictionaryName()
													.getBytes())));
									dataDictionary.setLabel(new String(Base64
											.decodeBase64(column.getLabel()
													.getBytes())));
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
									dataDictionaryDAO.update(dataDictionary);

								}

								if (dataDictionary.getLabel() == null) {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
									dataDictionary.setLabel(new String(Base64
											.decodeBase64(column.getLabel()
													.getBytes())));
									dataDictionaryDAO.update(dataDictionary);
								}

								if (column.getType().equals(
										PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
									updateCodeDesc(dynFormFieldRefHashMap,
											dataDictionary,
											column.getCodeDesc());
								}

							} else {
								DataDictionary dataDictionary = new DataDictionary();
								dataDictionary.setCompany(company);
								dataDictionary.setEntityMaster(entityMaster);
								dataDictionary
										.setDataDictName(new String(Base64
												.decodeBase64(column
														.getDictionaryName()
														.getBytes())));
								dataDictionary.setLabel(new String(Base64
										.decodeBase64(column.getLabel()
												.getBytes())));
								dataDictionary.setFormID(argFormId);
								dataDictionary
										.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
								if (column.getType().equalsIgnoreCase(
										PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
									DataDictionary returnedDD = dataDictionaryDAO
											.saveReturn(dataDictionary);

									saveCodeDesc(returnedDD,
											column.getCodeDesc());
								} else {
									dataDictionaryDAO.save(dataDictionary);
								}

							}

						}
					}

				}

				DynamicFormPK dynamicFormPk = new DynamicFormPK();
				DynamicForm dynamicForm = new DynamicForm();
				dynamicForm.setMetaData(metaData);
				dynamicForm.setCompany(company);
				dynamicForm.setEntityMaster(entityMaster);
				dynamicForm.setTabName(tabName);

				if (argFormId > 0) {
					synchronized (this) {
						int maxVersion = dynamicFormDAO.getMaxVersionByFormId(
								companyId, entityMaster.getEntityId(),
								argFormId);

						if (maxVersion > 0) {
							DynamicForm maxDynamicForm = dynamicFormDAO
									.findByMaxVersionByFormId(companyId,
											entityMaster.getEntityId(),
											maxVersion, argFormId);
							dynamicFormPk.setVersion(maxDynamicForm.getId()
									.getVersion() + 1);

						} else {
							dynamicFormPk.setVersion(BASE_VERSION);

						}
						dynamicFormPk.setFormId(argFormId);
						dynamicFormPk.setCompany_ID(company.getCompanyId());
						dynamicFormPk.setEntity_ID(entityMaster.getEntityId());
						dynamicForm.setId(dynamicFormPk);
						dynamicFormDAO.save(dynamicForm);
					}

				}

				else {
					dynamicFormPk.setVersion(BASE_VERSION);

					synchronized (this) {
						long maxFormId = dynamicFormDAO
								.getMaxFormId(null, null);
						dynamicFormPk.setFormId(maxFormId + 1);
						dynamicFormPk.setCompany_ID(company.getCompanyId());
						dynamicFormPk.setEntity_ID(entityMaster.getEntityId());
						dynamicForm.setId(dynamicFormPk);
						dynamicFormDAO.save(dynamicForm);
					}

				}
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_ERROR);
			}
			return PayAsiaConstants.PAYASIA_SUCCESS;
		}
	}

	private boolean checkForData(DataDictionary dataDictionary, Tab oldTab,
			Long companyId, Long formId) {

		List<Field> listOfFields = oldTab.getField();

		for (Field field : listOfFields) {

			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {

				if (dataDictionary.getDataDictName().equals(
						new String(Base64.decodeBase64(field
								.getDictionaryName().getBytes())))) {

					List<String> dataList = dynamicFormRecordDAO
							.findDataForDictionary(formId, companyId,
									PayAsiaStringUtils.getColNumber(field
											.getName()), dataDictionary
											.getEntityMaster().getEntityId());
					if ( !dataList.isEmpty()) {
						for (String data : dataList) {
							if (data != null && !data.equalsIgnoreCase("")) {
								return true;
							}
						}
					} else {
						return false;
					}
				}

			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {
				return checkForChildValue(dataDictionary, companyId, formId,
						field);
			}

		}

		return false;
	}

	private boolean checkForChildValue(DataDictionary dataDictionary,
			Long companyId, Long formId, Field field) {
		List<Column> listOfColumns = field.getColumn();
		for (Column column : listOfColumns) {
			if (dataDictionary.getDataDictName().equals(
					new String(Base64.decodeBase64(column.getDictionaryName()
							.getBytes())))) {

				List<String> dataList = dynamicFormRecordDAO
						.findDataForDictionary(
								formId,
								companyId,
								PayAsiaStringUtils.getColNumber(field.getName()),
								dataDictionary.getEntityMaster().getEntityId());

				if ( !dataList.isEmpty()) {
					for (String data : dataList) {
						if (data != null && !data.equalsIgnoreCase("")) {
							List<String> childDataList = dynamicFormTableRecordDAO
									.findDataForDictionary(PayAsiaStringUtils
											.getColNumber(column.getName()),
											data);
							if ( !childDataList.isEmpty()) {
								for (String childData : childDataList) {
									if (childData != null
											&& !childData.equalsIgnoreCase("")) {
										return true;
									}
								}
							} else {
								return false;
							}
						}
					}
				} else {
					return false;
				}
			}
		}

		return false;
	}

	private Tab getTabObject(String metaData) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
		} catch (SAXException saxException) {
			LOGGER.error(saxException.getMessage(), saxException);
		}

		final StringReader xmlReader = new StringReader(metaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
		}
		return tab;
	}

	/**
	 * Purpose: to update or save code and description options.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 * @param codeDescs
	 *            the code description List
	 */
	private void updateCodeDesc(
			HashMap<Long, DynamicFormFieldRefValue> dynFormFieldRefHashMap,
			DataDictionary dataDictionary, List<CodeDesc> codeDescs) {
		List<DynamicFormFieldRefValue> formFieldRefValues = dynamicFormFieldRefValueDAO
				.findByDataDictionayId(dataDictionary.getDataDictionaryId());
		List<Long> existingCodeDesc = new ArrayList<Long>();
		for (CodeDesc codeDesc : codeDescs) {

			DynamicFormFieldRefValue dynamicFormFieldRefValue = new DynamicFormFieldRefValue();
			dynamicFormFieldRefValue.setCode(new String(Base64
					.decodeBase64(codeDesc.getCode().getBytes())));
			dynamicFormFieldRefValue.setDescription(new String(Base64
					.decodeBase64(codeDesc.getDesc().getBytes())));
			dynamicFormFieldRefValue.setDataDictionary(dataDictionary);
			if (codeDesc.getFieldRefId() != 0) {

				dynamicFormFieldRefValue.setFieldRefValueId(codeDesc
						.getFieldRefId());
				existingCodeDesc.add(codeDesc.getFieldRefId());

				DynamicFormFieldRefValue dynFormFieldRefVal = dynFormFieldRefHashMap
						.get(codeDesc.getFieldRefId());
				if (!dynFormFieldRefVal.getCode().equalsIgnoreCase(
						new String(Base64.decodeBase64(codeDesc.getCode()
								.getBytes())))
						|| !dynFormFieldRefVal.getDescription()
								.equalsIgnoreCase(
										new String(Base64.decodeBase64(codeDesc
												.getDesc().getBytes())))) {
					dynFormFieldRefVal.setCode(dynamicFormFieldRefValue
							.getCode());
					dynFormFieldRefVal.setDescription(dynamicFormFieldRefValue
							.getDescription());
					dynamicFormFieldRefValueDAO.update(dynFormFieldRefVal);
				}

			} else {
				dynamicFormFieldRefValueDAO.save(dynamicFormFieldRefValue);
			}

		}

		for (DynamicFormFieldRefValue dynamicFormFieldRefValue : formFieldRefValues) {
			if (!existingCodeDesc.contains(dynamicFormFieldRefValue
					.getFieldRefValueId())) {
				dynamicFormFieldRefValueDAO.delete(dynamicFormFieldRefValue);
			}
		}
	}

	/**
	 * Purpose: To save code and description options.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 * @param codeDescs
	 *            the code descs
	 */
	private void saveCodeDesc(DataDictionary dataDictionary,
			List<CodeDesc> codeDescs) {
		for (CodeDesc codeDesc : codeDescs) {
			DynamicFormFieldRefValue dynamicFormFieldRefValue = new DynamicFormFieldRefValue();
			dynamicFormFieldRefValue.setCode(new String(Base64
					.decodeBase64(codeDesc.getCode().getBytes())));
			dynamicFormFieldRefValue.setDescription(new String(Base64
					.decodeBase64(codeDesc.getDesc().getBytes())));
			dynamicFormFieldRefValue.setDataDictionary(dataDictionary);
			dynamicFormFieldRefValueDAO.save(dynamicFormFieldRefValue);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyDynamicFormLogic#getXML(java.lang.Long,
	 * long)
	 */
	@Override
	public CompanyDynamicForm getXML(Long companyId, long formId) {
		CompanyDynamicForm companyDynamicForm = new CompanyDynamicForm();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);

		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId,
				entityMaster.getEntityId(), formId);
		if (maxVersion == 0) {
			return companyDynamicForm;
		} else {

			DynamicForm maxDynamicForm = dynamicFormDAO
					.findByMaxVersionByFormId(companyId,
							entityMaster.getEntityId(), maxVersion, formId);

			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
			}

			final StringReader xmlReader = new StringReader(
					maxDynamicForm.getMetaData());
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(),
						e1);
			}
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			try {
				streamWriter = outputFactory
						.createXMLStreamWriter(byteArrayOutputStream);
			} catch (XMLStreamException xmlStreamException) {
				LOGGER.error(xmlStreamException.getMessage(),
						xmlStreamException);
			}
			Tab tab = null;
			try {
				tab = (Tab) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {

				LOGGER.error(jaxbException.getMessage(), jaxbException);
			}

			List<Field> listOfFields = tab.getField();
			if (!maxDynamicForm.getTabName().equals(
					PayAsiaConstants.COMPANY_BASIC_TAB_NAME)) {
				String datadictName = tab.getDictionaryName();
				if (StringUtils.isNotBlank(datadictName)) {
					DataDictionary dataDictionaryTab = dataDictionaryDAO
							.findByDictionaryName(companyId,
									entityMaster.getEntityId(), datadictName,
									formId);
					if (dataDictionaryTab != null) {
						tab.setDictionaryId(dataDictionaryTab
								.getDataDictionaryId());
						tab.setDictionaryName(dataDictionaryTab
								.getDataDictName());
					}
				}

			}

			for (Field field : listOfFields) {

				 
				 

				DataDictionary dataDictionary;

				if (Base64.isArrayByteBase64(field.getDictionaryName()
						.getBytes())) {
					dataDictionary = dataDictionaryDAO.findByDictionaryName(
							companyId,
							entityMaster.getEntityId(),
							new String(Base64.decodeBase64(field
									.getDictionaryName().getBytes())), formId);
					if (dataDictionary != null) {
						field.setDictionaryId(dataDictionary
								.getDataDictionaryId());
					}

				} else {
					dataDictionary = dataDictionaryDAO.findByDictionaryName(
							companyId, entityMaster.getEntityId(),
							field.getDictionaryName(), formId);
					if (dataDictionary != null) {
						field.setDictionaryId(dataDictionary
								.getDataDictionaryId());
					}

				}

				if (field.getType().equalsIgnoreCase(
						PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
					if (dataDictionary != null) {
						List<CodeDesc> codeDescList = getCodeDescList(dataDictionary);
						field.getCodeDesc().clear();
						field.getCodeDesc().addAll(codeDescList);
					}

				}
				 
				if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {

						DataDictionary dataDictionaryVO = dataDictionaryDAO
								.findByDictionaryName(
										companyId,
										entityMaster.getEntityId(),
										new String(Base64
												.decodeBase64(column
														.getDictionaryName()
														.getBytes())), formId);
						if (dataDictionaryVO != null) {
							column.setDictionaryId(dataDictionaryVO
									.getDataDictionaryId());

							if (column.getType().equalsIgnoreCase(
									PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
								List<CodeDesc> codeDescList = getCodeDescList(dataDictionaryVO);
								column.getCodeDesc().clear();
								column.getCodeDesc().addAll(codeDescList);

							}
						}

					}
				}

			}

			Marshaller marshaller = null;

			try {
				marshaller = XMLUtil.getDocumentMarshaller();
				marshaller.marshal(tab, streamWriter);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
			}
			List<DynamicFormRecord> dynamicFormRecordVOList;
			if (listOfFields.size() == 0) {
				dynamicFormRecordVOList = dynamicFormRecordDAO.findByFormId(
						formId, entityMaster.getEntityId(), companyId, null);
				for (DynamicFormRecord dynamicFormRecordVO : dynamicFormRecordVOList) {
					dynamicFormRecordDAO.delete(dynamicFormRecordVO);
				}
			}
			companyDynamicForm.setMetaData(byteArrayOutputStream.toString());

			return companyDynamicForm;
		}

	}

	/**
	 * Purpose: Gets the existing code and description options from the
	 * database.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 * @return the code desc list
	 */
	private List<CodeDesc> getCodeDescList(DataDictionary dataDictionary) {
		List<DynamicFormFieldRefValue> fieldRefList = dynamicFormFieldRefValueDAO
				.findByDataDictionayId(dataDictionary.getDataDictionaryId());

		List<CodeDesc> codeDescList = new ArrayList<CodeDesc>();

		for (DynamicFormFieldRefValue dynamicFormFieldRefValue : fieldRefList) {
			CodeDesc codeDesc = new CodeDesc();
			codeDesc.setCode(new String(
					Base64.encodeBase64(dynamicFormFieldRefValue.getCode()
							.getBytes())));
			codeDesc.setDesc(new String(Base64
					.encodeBase64(dynamicFormFieldRefValue.getDescription()
							.getBytes())));
			codeDesc.setFieldRefId(dynamicFormFieldRefValue
					.getFieldRefValueId());
			codeDescList.add(codeDesc);

		}
		return codeDescList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyDynamicFormLogic#getTabList(java.lang.Long)
	 */
	@Override
	public List<CompanyDynamicForm> getTabList(Long companyId) {

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);
		List<Long> formList = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());
		List<CompanyDynamicForm> companyDynamicFormList = new ArrayList<CompanyDynamicForm>();
		CompanyDynamicForm companyForm = new CompanyDynamicForm();
		Integer companyBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
				entityMaster.getEntityId(),
				PayAsiaConstants.COMPANY_BASIC_TAB_NAME);

		if (companyBasicFormMax != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityMaster.getEntityId(), companyBasicFormMax,
					PayAsiaConstants.COMPANY_BASIC_TAB_NAME);
			companyForm.setFormId(dynamicForm.getId().getFormId());
			companyForm.setTabName(dynamicForm.getTabName());
			companyDynamicFormList.add(companyForm);
		} else {
			companyForm.setFormId(0);
			companyForm.setTabName(PayAsiaConstants.COMPANY_BASIC_TAB_NAME);
			companyDynamicFormList.add(companyForm);
		}

		for (Long formId : formList) {
			int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId,
					entityMaster.getEntityId(), formId);
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
					companyId, entityMaster.getEntityId(), maxVersion, formId);
			DataDictionary dataDictionary = dataDictionaryDAO
					.findByDictionaryName(companyId,
							entityMaster.getEntityId(),
							dynamicForm.getTabName(), formId);
			if (!dynamicForm.getTabName().equals(
					PayAsiaConstants.COMPANY_BASIC_TAB_NAME)) {
				CompanyDynamicForm companyDynamicForm = new CompanyDynamicForm();

				companyDynamicForm.setFormId(dynamicForm.getId().getFormId());
				companyDynamicForm.setTabName(dynamicForm.getTabName());
				if (dataDictionary != null) {
					companyForm.setDictionaryId(dataDictionary
							.getDataDictionaryId());
					companyForm.setDictionaryName(dataDictionary
							.getDataDictName());
				}

				companyDynamicFormList.add(companyDynamicForm);
			}
		}

		return companyDynamicFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDynamicFormLogic#getOptionsFromXL(com.payasia
	 * .common.form.CompanyDynamicForm)
	 */
	@Override
	public CompanyDynamicForm getOptionsFromXL(
			CompanyDynamicForm companyDynamicForm) {

		CompanyDynamicForm compDynamicForm = new CompanyDynamicForm();
		List<String> optionsString = new ArrayList<String>();
		String fileName = companyDynamicForm.getAttachment()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {

			optionsString = ExcelUtils.readXLS(companyDynamicForm
					.getAttachment());
		}

		else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			optionsString = ExcelUtils.readXLSX(companyDynamicForm
					.getAttachment());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(optionsString, jsonConfig);
		compDynamicForm.setOptions(jsonObject.toString());
		return compDynamicForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyDynamicFormLogic#deleteTab(java.lang.Long,
	 * long)
	 */
	@Override
	public String deleteTab(Long companyId, long formId) {

		String response = null;
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);
		Long recordSize = dynamicFormRecordDAO.getMaxFormId(companyId,
				entityMaster.getEntityId(), formId);

		if (recordSize > 0) {
			response = PayAsiaConstants.PAYASIA_ERROR;
		} else {
			List<EmpDataImportTemplateField> empDataImportList = empDataImportTemplateFieldDAO
					.findByConditionFormId(companyId,
							entityMaster.getEntityId(), formId);

			if ( !empDataImportList.isEmpty()) {
				response = PayAsiaConstants.PAYASIA_ERROR;
			} else {
				dynamicFormDAO.deleteByCondition(companyId,
						entityMaster.getEntityId(), formId);
				List<DataDictionary> dataDictionaries = dataDictionaryDAO
						.findByConditionFormId(companyId,
								entityMaster.getEntityId(), formId);

				for (DataDictionary dataDictionary : dataDictionaries) {
					if (dataDictionary.getFieldType().equalsIgnoreCase(
							PayAsiaConstants.DYNAMIC_TYPE)) {
						dataDictionaryDAO.delete(dataDictionary);
					}
				}
				response = PayAsiaConstants.PAYASIA_SUCCESS;
			}

		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDynamicFormLogic#getCodeDescFromXL(com.payasia
	 * .common.form.CompanyDynamicForm)
	 */
	@Override
	public List<CodeDescDTO> getCodeDescFromXL(
			CompanyDynamicForm companyDynamicForm) {
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
		String fileName = companyDynamicForm.getAttachment()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {

			codeDescDTOs = ExcelUtils.readCodeDescXLS(companyDynamicForm
					.getAttachment());
		}

		else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			codeDescDTOs = ExcelUtils.readCodeDescXLSX(companyDynamicForm
					.getAttachment());
		}

		return codeDescDTOs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDynamicFormLogic#checkFieldEdit(java.lang.Long,
	 * java.lang.String, boolean, java.lang.Long, java.lang.String)
	 */
	@Override
	public String checkFieldEdit(Long formId, String fieldName,
			boolean isTable, Long companyId, String tablePosition) {
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);
		String colName = PayAsiaStringUtils.getColNumber(fieldName);
		List<DynamicFormRecord> recordList = dynamicFormRecordDAO.findByFormId(
				formId, entityMaster.getEntityId(), companyId, null);
		if (!isTable) {

			if (recordList != null) {
				for (DynamicFormRecord dynamicFormRecord : recordList) {
					String value = dataImportUtils.getColValueFile(colName,
							dynamicFormRecord);
					if (value != null && !value.trim().equals("")) {
						return PayAsiaConstants.PAYASIA_ERROR;
					}
				}
			}

		} else {

			if (!tablePosition.equals("0")) {
				String tableCol = PayAsiaStringUtils
						.getColNumber(tablePosition);
				if (recordList != null) {
					for (DynamicFormRecord dynamicFormRecord : recordList) {
						String value = dataImportUtils.getColValueFile(
								tableCol, dynamicFormRecord);

						if (value != null && !value.trim().equals("")) {
							List<DynamicFormTableRecord> tableRecords = dynamicFormTableRecordDAO
									.getTableRecords(
											Long.parseLong(value),
											PayAsiaConstants.SORT_ORDER_OLDEST_TO_NEWEST,
											"");

							for (DynamicFormTableRecord dynamicFormTableRecord : tableRecords) {

								String tableVal = dataImportUtils
										.getTableRecordValue(colName,
												dynamicFormTableRecord);
								if (tableVal != null
										&& !tableVal.trim().equals("")) {
									return PayAsiaConstants.PAYASIA_ERROR;
								}

							}

						}
					}
				}

			}

		}

		return PayAsiaConstants.PAYASIA_SUCCESS;
	}
}
