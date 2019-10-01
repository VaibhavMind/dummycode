/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CalculatoryFieldForm;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.EmployeeDynamicForm;
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
import com.payasia.dao.EmployeeRoleSectionMappingDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.RoleSectionMappingDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormPK;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.EmployeeDynamicFormLogic;
import com.payasia.logic.GeneralLogic;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * The Class EmployeeDynamicFormLogicImpl.
 */
@Component
public class EmployeeDynamicFormLogicImpl implements EmployeeDynamicFormLogic {

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

	/** The dynamic form table record dao. */
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	/** The emp data import template field dao. */
	@Resource
	EmpDataImportTemplateFieldDAO empDataImportTemplateFieldDAO;

	/** The dynamic form field ref value dao. */
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;

	/** The data import utils. */
	@Resource
	DataImportUtils dataImportUtils;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	GeneralDAO generalDAO;
	@Resource
	RoleSectionMappingDAO roleSectionMappingDAO;
	@Resource
	EmployeeRoleSectionMappingDAO employeeRoleSectionMappingDAO;

	/** The Constant BASE_VERSION. */
	public static final int BASE_VERSION = 1;

	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDynamicFormLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDynamicFormLogic#saveDynamicXML(java.lang.Long,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public EmployeeDynamicForm saveDynamicXML(Long companyId, String metaData,
			String tabName) {

		Long formId = null;
		Company company = companyDAO.findById(companyId);
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		EmployeeDynamicForm employeeForm = new EmployeeDynamicForm();

		Integer employeeBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
				entityMaster.getEntityId(),
				PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);

		if (employeeBasicFormMax == null) {
			String dummyData = PayAsiaConstants.EMPLOYEE_FORM_DESIGNER_DUMMY_XML;
			String basicFormId = saveDummyXML(companyId, dummyData,
					PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
			employeeForm.setAddedNewTab(true);
			employeeForm.setBasicFormId(Long.parseLong(basicFormId));
		} else {
			employeeForm.setAddedNewTab(false);
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

		if (!tabName.equals(PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME)) {
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

		employeeForm.setFormId(formId);
		return employeeForm;

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
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
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
			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				dataDictionary
						.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DEPENDENTS);
				dataDictionary.setImportable(false);
			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
				dataDictionary
						.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_REFERENCE);
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

			if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)
					|| field.getType().equals(
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
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
					if (StringUtils.equalsIgnoreCase(column.getType(),
							PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
						dataDictionary
								.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_REFERENCE);
						dataDictionary.setImportable(false);
					} else {
						dataDictionaryVO
								.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
					}

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
	 * @see com.payasia.logic.EmployeeDynamicFormLogic#saveXML(java.lang.Long,
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
					.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

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

			if (!tabName.equals(PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME)) {
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
				if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)
						|| field.getType().equals(
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						if (column.getDictionaryId() > 0) {
							tabDictionaryIds.add(column.getDictionaryId());
						}
					}
				}
				/**
				 * if (field.getType()
				 * .equals(PayAsiaConstants.DOCUMENT_FIELD_TYPE)) { List<Column>
				 * listOfColumns = field.getColumn(); for (Column column :
				 * listOfColumns) { if (column.getDictionaryId() > 0) {
				 * 
				 * } } }
				 */
			}

			boolean firstRun = true;
			Tab oldTab = null;

			for (Long dictionaryId : existingDictionaryIds) {
				if (!(tabDictionaryIds.contains(dictionaryId))) {
					DataDictionary dataDictionary = dataDictionaryDAO
							.findById(dictionaryId);
					if (firstRun) {
						DynamicForm currentForm = dynamicFormDAO
								.findMaxVersionByFormId(companyId,
										entityMaster.getEntityId(), argFormId);

						oldTab = getTabObject(currentForm.getMetaData());
						firstRun = false;
					}

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
							try {
								checkDependentCustomFieldExist(companyId,
										entityMaster, field);
							} catch (PayAsiaSystemException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										PayAsiaConstants.EXISTS);
							}
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
							} else if (StringUtils.equalsIgnoreCase(
									field.getType(),
									PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DEPENDENTS);
								dataDictionary.setImportable(false);
							} else if (StringUtils.equalsIgnoreCase(
									field.getType(),
									PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DOCUMENT);
								dataDictionary.setImportable(false);
							} else if (StringUtils
									.equalsIgnoreCase(
											field.getType(),
											PayAsiaConstants.CALCULATORY_FIELD_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA);
								dataDictionary.setImportable(false);
							} else if (StringUtils
									.equalsIgnoreCase(
											field.getType(),
											PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_REFERENCE);
								dataDictionary.setImportable(false);
							} else {
								if (dataDictionary
										.getLabel()
										.equalsIgnoreCase(
												PayAsiaConstants.COTTON_ON_PAID_UP_TO_DATE)
										&& StringUtils
												.equalsIgnoreCase(
														field.getType(),
														PayAsiaConstants.FIELD_TYPE_DATE)) {
									dataDictionary.setImportable(false);
								}
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
							}
							dataDictionaryDAO.update(dataDictionary);

						}

						if (dataDictionary.getLabel() == null) {
							try {
								checkDependentCustomFieldExist(companyId,
										entityMaster, field);
							} catch (PayAsiaSystemException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										PayAsiaConstants.EXISTS);
							}

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
							} else if (StringUtils.equalsIgnoreCase(
									field.getType(),
									PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DEPENDENTS);
								dataDictionary.setImportable(false);
							} else if (StringUtils.equalsIgnoreCase(
									field.getType(),
									PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DOCUMENT);
								dataDictionary.setImportable(false);
							} else if (StringUtils
									.equalsIgnoreCase(
											field.getType(),
											PayAsiaConstants.CALCULATORY_FIELD_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA);
								dataDictionary.setImportable(false);
							} else if (StringUtils
									.equalsIgnoreCase(
											field.getType(),
											PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_REFERENCE);
								dataDictionary.setImportable(false);
							} else {
								if (dataDictionary
										.getLabel()
										.equalsIgnoreCase(
												PayAsiaConstants.COTTON_ON_PAID_UP_TO_DATE)
										&& StringUtils
												.equalsIgnoreCase(
														field.getType(),
														PayAsiaConstants.FIELD_TYPE_DATE)) {
									dataDictionary.setImportable(false);
								}
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
							}
							dataDictionary
									.setLabel(new String(Base64
											.decodeBase64(field.getLabel()
													.getBytes())));
							dataDictionaryDAO.update(dataDictionary);
						}

					} else {

						try {
							checkDependentCustomFieldExist(companyId,
									entityMaster, field);
						} catch (PayAsiaSystemException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(
									PayAsiaConstants.EXISTS);
						}

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
						} else if (StringUtils.equalsIgnoreCase(
								field.getType(),
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
							dataDictionary
									.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DEPENDENTS);
							dataDictionary.setImportable(false);
						} else if (StringUtils.equalsIgnoreCase(
								field.getType(),
								PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {
							dataDictionary
									.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DOCUMENT);
							dataDictionary.setImportable(false);
						} else if (StringUtils.equalsIgnoreCase(
								field.getType(),
								PayAsiaConstants.CALCULATORY_FIELD_FIELD_TYPE)) {
							dataDictionary
									.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA);
							dataDictionary.setImportable(false);
						} else if (StringUtils.equalsIgnoreCase(
								field.getType(),
								PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
							dataDictionary
									.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_REFERENCE);
							dataDictionary.setImportable(false);
						} else {
							if (StringUtils.equalsIgnoreCase(
									new String(Base64.decodeBase64(field
											.getLabel().getBytes())),
									PayAsiaConstants.COTTON_ON_PAID_UP_TO_DATE)
									&& StringUtils.equalsIgnoreCase(
											field.getType(),
											PayAsiaConstants.FIELD_TYPE_DATE)) {
								dataDictionary.setImportable(false);
							}
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
							PayAsiaConstants.TABLE_FIELD_TYPE)
							|| field.getType().equals(
									PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
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
									if (StringUtils
											.equalsIgnoreCase(
													column.getType(),
													PayAsiaConstants.CALCULATORY_FIELD_FIELD_TYPE)) {
										dataDictionary
												.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA);
									} else if (StringUtils
											.equalsIgnoreCase(
													column.getType(),
													PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
										dataDictionary
												.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_REFERENCE);
										dataDictionary.setImportable(false);
									} else {
										dataDictionary
												.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
									}

									dataDictionaryDAO.update(dataDictionary);

								}

								if (dataDictionary.getLabel() == null) {
									if (StringUtils
											.equalsIgnoreCase(
													column.getType(),
													PayAsiaConstants.CALCULATORY_FIELD_FIELD_TYPE)) {
										dataDictionary
												.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA);
									} else if (StringUtils
											.equalsIgnoreCase(
													column.getType(),
													PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
										dataDictionary
												.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_REFERENCE);
										dataDictionary.setImportable(false);
									} else {
										dataDictionary
												.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
									}

									dataDictionary.setLabel(new String(Base64
											.decodeBase64(column.getLabel()
													.getBytes())));
									dataDictionaryDAO.update(dataDictionary);
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

								if (StringUtils
										.equalsIgnoreCase(
												column.getType(),
												PayAsiaConstants.CALCULATORY_FIELD_FIELD_TYPE)) {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA);
								} else if (StringUtils
										.equalsIgnoreCase(
												column.getType(),
												PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_REFERENCE);
									dataDictionary.setImportable(false);
								} else {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
								}

								if (column.getType().equalsIgnoreCase(
										PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
									DataDictionary returnedDD = dataDictionaryDAO
											.saveReturn(dataDictionary);

									saveCodeDesc(returnedDD,
											column.getCodeDesc());
								} else {
									if ("custcol_1".equals(column.getName())) {
										if (column.isEffectiveDate()) {
											dataDictionaryDAO
													.save(dataDictionary);
										}
									} else {
										dataDictionaryDAO.save(dataDictionary);
									}

								}

							}

						}
					}

				}

				String modifiedMetaData = addNewDataDictionaryIdInXML(tab,
						companyId, argFormId, entityMaster, tabName);

				DynamicFormPK dynamicFormPk = new DynamicFormPK();
				DynamicForm dynamicForm = new DynamicForm();
				dynamicForm.setMetaData(modifiedMetaData);
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
			} catch (Exception ex) {
				ex.printStackTrace();
				LOGGER.error(ex.getMessage(), ex);
				if (ex.getMessage()!=null && ex.getMessage().equalsIgnoreCase(PayAsiaConstants.EXISTS)) {
					throw new PayAsiaSystemException(PayAsiaConstants.EXISTS);
				}
				throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_ERROR);
			}
			return PayAsiaConstants.PAYASIA_SUCCESS;
		}

	}


	/**
	 * @param companyId
	 * @param entityMaster
	 * @param field
	 */
	private void checkDependentCustomFieldExist(Long companyId,
			EntityMaster entityMaster, Field field) {
		if (PayAsiaConstants.PAYASIA_EMP_DYNFIELD_TABLE_NAME_DEPENDENT
				.equalsIgnoreCase(new String(Base64.decodeBase64(field
						.getLabel().getBytes())))) {
			DataDictionary checkDependentsNameTableExists = dataDictionaryDAO
					.findByDictionaryName(
							companyId,
							entityMaster.getEntityId(),
							new String(Base64.decodeBase64(field.getLabel()
									.getBytes())));
			if (checkDependentsNameTableExists != null) {
				throw new PayAsiaSystemException(PayAsiaConstants.EXISTS);
			}
		}
	}

	private String addNewDataDictionaryIdInXML(Tab tab, Long companyId,
			long formId, EntityMaster entityMaster, String tabName) {

		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLStreamWriter streamWriter = null;
		try {
			streamWriter = outputFactory
					.createXMLStreamWriter(byteArrayOutputStream);
		} catch (XMLStreamException xmlStreamException) {
			LOGGER.error(xmlStreamException.getMessage(), xmlStreamException);
		}

		if (!tabName.equals(PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME)) {
			String datadictName = tab.getDictionaryName();
			if (StringUtils.isNotBlank(datadictName)) {
				DataDictionary dataDictionaryTab = dataDictionaryDAO
						.findByDictionaryName(companyId,
								entityMaster.getEntityId(), datadictName,
								formId);
				if (dataDictionaryTab != null) {
					tab.setDictionaryId(dataDictionaryTab.getDataDictionaryId());
					tab.setDictionaryName(dataDictionaryTab.getDataDictName());
				}
			}
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {

			DataDictionary dataDictionary;

			if (Base64.isArrayByteBase64(field.getDictionaryName().getBytes())) {
				dataDictionary = dataDictionaryDAO.findByDictionaryName(
						companyId,
						entityMaster.getEntityId(),
						new String(Base64.decodeBase64(field
								.getDictionaryName().getBytes())), formId);
				if (dataDictionary != null) {
					field.setDictionaryId(dataDictionary.getDataDictionaryId());
				}

			} else {
				dataDictionary = dataDictionaryDAO.findByDictionaryName(
						companyId, entityMaster.getEntityId(),
						field.getDictionaryName(), formId);
				if (dataDictionary != null) {
					field.setDictionaryId(dataDictionary.getDataDictionaryId());
				}

			}

			if (field.getType().equalsIgnoreCase(
					PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
				if (dataDictionary != null) {

					field.getCodeDesc().clear();

				}

			}

			if (field.getType().equals(PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {
			}

			if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)
					|| field.getType().equals(
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {

					if ("custcol_1".equals(column.getName())
							&& !column.isEffectiveDate()) {
						continue;
					}

					DataDictionary dataDictionaryVO = dataDictionaryDAO
							.findByDictionaryName(
									companyId,
									entityMaster.getEntityId(),
									new String(Base64.decodeBase64(column
											.getDictionaryName().getBytes())),
									formId);
					if (dataDictionaryVO != null) {
						column.setDictionaryId(dataDictionaryVO
								.getDataDictionaryId());

						if (column.getType().equalsIgnoreCase(
								PayAsiaConstants.CODEDESC_FIELD_TYPE)) {

							column.getCodeDesc().clear();

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
			dynamicFormRecordVOList = dynamicFormRecordDAO.findByFormId(formId,
					entityMaster.getEntityId(), companyId, null);
			for (DynamicFormRecord dynamicFormRecordVO : dynamicFormRecordVOList) {
				dynamicFormRecordDAO.delete(dynamicFormRecordVO);
			}
		}
		return byteArrayOutputStream.toString();
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
					if (!dataList.isEmpty()) {
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

				if (!dataList.isEmpty()) {
					for (String data : dataList) {
						if (data != null && !data.equalsIgnoreCase("")) {
							List<String> childDataList = dynamicFormTableRecordDAO
									.findDataForDictionary(PayAsiaStringUtils
											.getColNumber(column.getName()),
											data);
							if (!childDataList.isEmpty()) {
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

	@Override
	public Tab getTabObject(String metaData) {
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
	 *            the code descs
	 */
	private void updateCodeDesc(
			HashMap<Long, DynamicFormFieldRefValue> dynFormFieldRefHashMap,
			DataDictionary dataDictionary, List<CodeDesc> codeDescs) {
		List<DynamicFormFieldRefValue> formFieldRefValues = dynamicFormFieldRefValueDAO
				.findByDataDictionayId(dataDictionary.getDataDictionaryId());
		List<Long> existingCodeDesc = new ArrayList<Long>();
		for (CodeDesc codeDesc : codeDescs) {
			DynamicFormFieldRefValue dynamicFormFieldRefValue = new DynamicFormFieldRefValue();
			try {
				dynamicFormFieldRefValue.setCode(URLDecoder.decode(
						codeDesc.getCode(), "UTF8"));
				dynamicFormFieldRefValue.setDescription(URLDecoder.decode(
						codeDesc.getDesc(), "UTF8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}

			dynamicFormFieldRefValue.setDataDictionary(dataDictionary);
			if (codeDesc.getFieldRefId() != 0) {

				dynamicFormFieldRefValue.setFieldRefValueId(codeDesc
						.getFieldRefId());
				existingCodeDesc.add(codeDesc.getFieldRefId());

				DynamicFormFieldRefValue dynFormFieldRefVal = dynFormFieldRefHashMap
						.get(codeDesc.getFieldRefId());
				if (!dynFormFieldRefVal.getCode().equalsIgnoreCase(
						dynamicFormFieldRefValue.getCode())
						|| !dynFormFieldRefVal.getDescription()
								.equalsIgnoreCase(
										dynamicFormFieldRefValue
												.getDescription())) {
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

			try {
				dynamicFormFieldRefValue.setCode(URLDecoder.decode(
						codeDesc.getCode(), "UTF8"));
				dynamicFormFieldRefValue.setDescription(URLDecoder.decode(
						codeDesc.getDesc(), "UTF8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}

			dynamicFormFieldRefValue.setDataDictionary(dataDictionary);
			dynamicFormFieldRefValueDAO.save(dynamicFormFieldRefValue);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDynamicFormLogic#getXML(java.lang.Long,
	 * long)
	 */
	@Override
	public EmployeeDynamicForm getXML(Long companyId, long formId) {
		EmployeeDynamicForm employeeDynamicForm = new EmployeeDynamicForm();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId,
				entityMaster.getEntityId(), formId);
		if (maxVersion == 0) {
			return employeeDynamicForm;
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
					PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME)) {
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

				if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)
						|| field.getType().equals(
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {

						if ("custcol_1".equals(column.getName())
								&& !column.isEffectiveDate()) {
							continue;
						}

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
			employeeDynamicForm.setMetaData(byteArrayOutputStream.toString());
			employeeDynamicForm.setEmployeesList(generalLogic
					.returnEmployeesList(0l, companyId));

			return employeeDynamicForm;
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

			try {
				codeDesc.setCode(URLEncoder.encode(
						dynamicFormFieldRefValue.getCode(), "UTF8"));
				codeDesc.setDesc(URLEncoder.encode(
						dynamicFormFieldRefValue.getDescription(), "UTF8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}

			codeDesc.setFieldRefId(dynamicFormFieldRefValue
					.getFieldRefValueId());
			codeDescList.add(codeDesc);

		}
		return codeDescList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDynamicFormLogic#getTabList(java.lang.Long)
	 */
	@Override
	public List<EmployeeDynamicForm> getTabList(Long companyId) {

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		List<Long> formList = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());
		List<EmployeeDynamicForm> employeeDynamicFormList = new ArrayList<EmployeeDynamicForm>();
		EmployeeDynamicForm employeeForm = new EmployeeDynamicForm();
		Integer employeeBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
				entityMaster.getEntityId(),
				PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);

		if (employeeBasicFormMax != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityMaster.getEntityId(),
					employeeBasicFormMax,
					PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
			employeeForm.setFormId(dynamicForm.getId().getFormId());
			employeeForm.setTabName(dynamicForm.getTabName());
			employeeDynamicFormList.add(employeeForm);
		} else {
			employeeForm.setFormId(0);
			employeeForm.setTabName(PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
			employeeDynamicFormList.add(employeeForm);
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
					PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME)) {
				EmployeeDynamicForm employeeDynamicForm = new EmployeeDynamicForm();

				employeeDynamicForm.setFormId(dynamicForm.getId().getFormId());
				employeeDynamicForm.setTabName(dynamicForm.getTabName());
				if (dataDictionary != null) {
					employeeDynamicForm.setDictionaryId(dataDictionary
							.getDataDictionaryId());
					employeeDynamicForm.setDictionaryName(dataDictionary
							.getDataDictName());
				}

				employeeDynamicFormList.add(employeeDynamicForm);
			}
		}

		return employeeDynamicFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDynamicFormLogic#getOptionsFromXL(com.payasia
	 * .common.form.EmployeeDynamicForm)
	 */
	@Override
	public EmployeeDynamicForm getOptionsFromXL(
			EmployeeDynamicForm employeeDynamicForm) {

		EmployeeDynamicForm empDynamicForm = new EmployeeDynamicForm();
		List<String> optionsString = new ArrayList<String>();
		String fileName = employeeDynamicForm.getAttachment()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {

			optionsString = ExcelUtils.readXLS(employeeDynamicForm
					.getAttachment());
		}

		else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			optionsString = ExcelUtils.readXLSX(employeeDynamicForm
					.getAttachment());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(optionsString, jsonConfig);
		empDynamicForm.setOptions(jsonObject.toString());
		return empDynamicForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDynamicFormLogic#deleteTab(java.lang.Long,
	 * long)
	 */
	@Override
	public String deleteTab(Long companyId, long formId) {

		String response = null;
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		Long recordSize = dynamicFormRecordDAO.getMaxFormId(companyId,
				entityMaster.getEntityId(), formId);

		if (recordSize > 0) {
			response = PayAsiaConstants.PAYASIA_ERROR;
		} else {
			List<EmpDataImportTemplateField> empDataImportList = empDataImportTemplateFieldDAO
					.findByConditionFormId(companyId,
							entityMaster.getEntityId(), formId);

			if (!empDataImportList.isEmpty()) {
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

				roleSectionMappingDAO.deleteByFormIdAndCompanyId(formId,
						companyId);

				employeeRoleSectionMappingDAO.deleteByFormIdAndCompanyId(
						formId, companyId);

				response = PayAsiaConstants.PAYASIA_SUCCESS;
			}

		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDynamicFormLogic#getCodeDescFromXL(com.payasia
	 * .common.form.EmployeeDynamicForm)
	 */
	@Override
	public List<CodeDescDTO> getCodeDescFromXL(
			EmployeeDynamicForm employeeDynamicForm) {
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
		String fileName = employeeDynamicForm.getAttachment()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {

			codeDescDTOs = ExcelUtils.readCodeDescXLS(employeeDynamicForm
					.getAttachment());
		}

		else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			codeDescDTOs = ExcelUtils.readCodeDescXLSX(employeeDynamicForm
					.getAttachment());
		}

		return codeDescDTOs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDynamicFormLogic#checkFieldEdit(java.lang.Long,
	 * java.lang.String, boolean, java.lang.Long, java.lang.String)
	 */
	@Override
	public String checkFieldEdit(Long formId, String fieldName,
			boolean isTable, Long companyId, String tablePosition) {
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
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
											PayAsiaConstants.SORT_ORDER_NEWEST_TO_OLDEST,
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

	@Override
	public List<CodeDescDTO> getDynCodeDescList(Long dataDictionaryId,
			Long companyId) {
		List<CodeDescDTO> codeDescDTOList = new ArrayList<>();
		List<DynamicFormFieldRefValue> dynamicFormFieldRefValueList = dynamicFormFieldRefValueDAO
				.findByDataDictionayId(dataDictionaryId,companyId);
		for (DynamicFormFieldRefValue dynamicFormFieldRefValue : dynamicFormFieldRefValueList) {
			CodeDescDTO codeDescDTO = new CodeDescDTO();
			try {
				codeDescDTO.setCode(URLEncoder.encode(
						dynamicFormFieldRefValue.getCode(), "UTF-8"));
				codeDescDTO.setDescription(URLEncoder.encode(
						dynamicFormFieldRefValue.getDescription(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			codeDescDTO.setCodeDescId(dynamicFormFieldRefValue
					.getFieldRefValueId());
			codeDescDTOList.add(codeDescDTO);
		}

		return codeDescDTOList;
	}

	@Override
	public String saveDynCodeDesc(Long companyId, String metaData,
			String tabName, long formId) {
		HashMap<Long, DynamicFormFieldRefValue> dynFormFieldRefHashMap = new HashMap<Long, DynamicFormFieldRefValue>();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		List<DynamicFormFieldRefValue> dynFormFieldRefValueList = dynamicFormFieldRefValueDAO
				.findByCondition(companyId, entityMaster.getEntityId(), formId);
		for (DynamicFormFieldRefValue dynamicFormFieldRefVal : dynFormFieldRefValueList) {
			dynFormFieldRefHashMap.put(
					dynamicFormFieldRefVal.getFieldRefValueId(),
					dynamicFormFieldRefVal);
		}
		List<String> existingCodeList = new ArrayList<String>();
		Tab tab = getTabObject(metaData);
		List<Field> fields = tab.getField();
		for (Field field : fields) {
			String fieldType = field.getType();
			Long dataDictionaryId = field.getDictionaryId();
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(dataDictionaryId);
			List<Column> columnList = field.getColumn();
			for (Column column : columnList) {
				List<CodeDesc> codeDescList = column.getCodeDesc();

				List<DynamicFormFieldRefValue> formFieldRefValues = dynamicFormFieldRefValueDAO
						.findByDataDictionayId(dataDictionary
								.getDataDictionaryId());
				List<Long> existingCodeDesc = new ArrayList<Long>();
				for (CodeDesc codeDesc : codeDescList) {
					DynamicFormFieldRefValue dynamicFormFieldRefValue = new DynamicFormFieldRefValue();
					try {
						dynamicFormFieldRefValue.setCode(URLDecoder.decode(
								codeDesc.getCode(), "UTF8"));
						dynamicFormFieldRefValue.setDescription(URLDecoder
								.decode(codeDesc.getDesc(), "UTF8"));
					} catch (UnsupportedEncodingException unsupportedEncodingException) {
						LOGGER.error(unsupportedEncodingException.getMessage(),
								unsupportedEncodingException);
						throw new PayAsiaSystemException(
								unsupportedEncodingException.getMessage(),
								unsupportedEncodingException);
					}

					dynamicFormFieldRefValue.setDataDictionary(dataDictionary);
					if (codeDesc.getFieldRefId() != 0) {

						dynamicFormFieldRefValue.setFieldRefValueId(codeDesc
								.getFieldRefId());
						existingCodeDesc.add(codeDesc.getFieldRefId());

						DynamicFormFieldRefValue dynFormFieldRefVal = dynFormFieldRefHashMap
								.get(codeDesc.getFieldRefId());
						if (!dynFormFieldRefVal.getCode().equalsIgnoreCase(
								dynamicFormFieldRefValue.getCode())
								|| !dynFormFieldRefVal.getDescription()
										.equalsIgnoreCase(
												dynamicFormFieldRefValue
														.getDescription())) {
							dynFormFieldRefVal.setCode(dynamicFormFieldRefValue
									.getCode());
							dynFormFieldRefVal
									.setDescription(dynamicFormFieldRefValue
											.getDescription());
							dynamicFormFieldRefValueDAO
									.update(dynFormFieldRefVal);
						}

					} else {
						dynamicFormFieldRefValueDAO
								.save(dynamicFormFieldRefValue);
					}

				}

				for (DynamicFormFieldRefValue dynamicFormFieldRefValue : formFieldRefValues) {
					if (!existingCodeDesc.contains(dynamicFormFieldRefValue
							.getFieldRefValueId())) {
						if (fieldType
								.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
							List<String> codeDescDynFieldList = dynamicFormTableRecordDAO
									.getExistanceOfCodeDescDynField(
											PayAsiaStringUtils
													.getColNumber(field
															.getName()),
											String.valueOf(dynamicFormFieldRefValue
													.getFieldRefValueId()));
							if (codeDescDynFieldList.isEmpty()) {
								dynamicFormFieldRefValueDAO
										.delete(dynamicFormFieldRefValue);
							} else {
								existingCodeList.add(dynamicFormFieldRefValue
										.getCode());
							}

						} else {
							List<String> codeDescDynFieldList = dynamicFormRecordDAO
									.getExistanceOfCodeDescDynField(
											PayAsiaStringUtils
													.getColNumber(field
															.getName()),
											String.valueOf(dynamicFormFieldRefValue
													.getFieldRefValueId()));
							if (codeDescDynFieldList.isEmpty()) {
								dynamicFormFieldRefValueDAO
										.delete(dynamicFormFieldRefValue);
							} else {
								existingCodeList.add(dynamicFormFieldRefValue
										.getCode());
							}
						}

					}
				}

			}
		}

		if (!existingCodeList.isEmpty()) {
			throw new PayAsiaSystemException(
					PayAsiaConstants.PAYASIA_CODE_DESC_DELETE_MSG
							+ existingCodeList,
					PayAsiaConstants.PAYASIA_CODE_DESC_DELETE_MSG
							+ existingCodeList);
		} else {
			return PayAsiaConstants.PAYASIA_SUCCESS;
		}

	}

	@Override
	public CalculatoryFieldFormResponse getNumericOrDateTypeDataDictionaryFields(
			Long companyId, boolean isTableField, long tableDicId,
			String fieldType) {
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		List<CalculatoryFieldForm> calculatoryFields = new ArrayList<CalculatoryFieldForm>();

		if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)) {
			List<DataDictionary> dataDictionaryList = dataDictionaryDAO
					.findByConditionEntity(entityMaster.getEntityId(),
							PayAsiaConstants.STATIC_TYPE);
			if (!dataDictionaryList.isEmpty()) {
				for (DataDictionary dataDictionary : dataDictionaryList) {
					CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
					if (!dataDictionary.getDataDictName().equalsIgnoreCase(
							PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
						ColumnPropertyDTO colProp = generalDAO
								.getColumnProperties(
										dataDictionary.getTableName(),
										dataDictionary.getColumnName());
						if (colProp.getColumnType().equalsIgnoreCase(
								PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {
							calculatoryField.setDictionaryId(dataDictionary
									.getDataDictionaryId());
							calculatoryField.setFieldName(dataDictionary
									.getDataDictName());
							calculatoryFields.add(calculatoryField);
						}

					}

				}
			}
		}

		List<Long> formList = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());

		for (Long formId : formList) {
			int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId,
					entityMaster.getEntityId(), formId);
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
					companyId, entityMaster.getEntityId(), maxVersion, formId);

			Tab tab = getTabObject(dynamicForm.getMetaData());
			List<Field> listOfFields = tab.getField();

			for (Field field : listOfFields) {
				if (field.getType().equals(PayAsiaConstants.FIELD_TYPE_NUMERIC)
						&& fieldType
								.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
					if (field.getDictionaryId() != null) {
						DataDictionary dataDictionary = dataDictionaryDAO
								.findById(field.getDictionaryId());
						if (dataDictionary != null) {
							CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
							calculatoryField.setDictionaryId(dataDictionary
									.getDataDictionaryId());
							if (dataDictionary.getFieldType().equalsIgnoreCase(
									PayAsiaConstants.STATIC_TYPE)) {
								calculatoryField.setFieldName(dataDictionary
										.getDataDictName());
							} else {
								calculatoryField.setFieldName(dataDictionary
										.getLabel());
							}

							calculatoryFields.add(calculatoryField);
						}
					}

				}
				if (field.getType().equals(PayAsiaConstants.FIELD_TYPE_DATE)
						&& fieldType
								.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)) {
					if (field.getDictionaryId() != null) {
						DataDictionary dataDictionary = dataDictionaryDAO
								.findById(field.getDictionaryId());
						if (dataDictionary != null) {
							CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
							calculatoryField.setDictionaryId(dataDictionary
									.getDataDictionaryId());
							if (dataDictionary.getFieldType().equalsIgnoreCase(
									PayAsiaConstants.STATIC_TYPE)) {
								calculatoryField.setFieldName(dataDictionary
										.getDataDictName());
							} else {
								calculatoryField.setFieldName(dataDictionary
										.getLabel());
							}

							calculatoryFields.add(calculatoryField);
						}
					}

				}
				if ((field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE) || field
						.getType().equals(
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE))
						&& isTableField) {
					if (field.getDictionaryId().equals(tableDicId)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (column.getType().equals(
									PayAsiaConstants.FIELD_TYPE_NUMERIC)
									&& fieldType
											.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_NUMERIC)) {

								if (column.getDictionaryId() != null) {
									DataDictionary dataDictionary = dataDictionaryDAO
											.findById(column.getDictionaryId());
									if (dataDictionary != null) {
										CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
										calculatoryField
												.setDictionaryId(dataDictionary
														.getDataDictionaryId());
										if (dataDictionary
												.getFieldType()
												.equalsIgnoreCase(
														PayAsiaConstants.STATIC_TYPE)) {
											calculatoryField
													.setFieldName(dataDictionary
															.getDataDictName());
										} else {
											calculatoryField
													.setFieldName(dataDictionary
															.getLabel());
										}
										calculatoryFields.add(calculatoryField);
									}
								}
							}
							if (column.getType().equals(
									PayAsiaConstants.FIELD_TYPE_DATE)
									&& fieldType
											.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)) {

								if (column.getDictionaryId() != null) {
									DataDictionary dataDictionary = dataDictionaryDAO
											.findById(column.getDictionaryId());
									if (dataDictionary != null) {
										CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
										calculatoryField
												.setDictionaryId(dataDictionary
														.getDataDictionaryId());
										if (dataDictionary
												.getFieldType()
												.equalsIgnoreCase(
														PayAsiaConstants.STATIC_TYPE)) {
											calculatoryField
													.setFieldName(dataDictionary
															.getDataDictName());
										} else {
											calculatoryField
													.setFieldName(dataDictionary
															.getLabel());
										}
										calculatoryFields.add(calculatoryField);
									}
								}
							}
						}
					}
				}
			}
		}

		CalculatoryFieldFormResponse calculatoryFieldFormResponse = new CalculatoryFieldFormResponse();
		calculatoryFieldFormResponse.setCalculatoryFieldList(calculatoryFields);
		return calculatoryFieldFormResponse;

	}

	@Override
	public Map<Long, String> getDictionaryLabel(Long companyId,
			String[] dictionaryIds) {

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		Map<Long, String> dictionaryNames = new HashMap<Long, String>();
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId,
						entityMaster.getEntityId(), null);
		Set<String> uniqueDictionaryIds = new HashSet<>();
		for (String argDictionaryId : dictionaryIds) {
			uniqueDictionaryIds.add(argDictionaryId);
		}

		for (String argDictionaryId : uniqueDictionaryIds) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				if (dataDictionary.getDataDictionaryId() == Long
						.valueOf(argDictionaryId)) {
					String label = dataDictionary.getLabel();
					Long dictionaryId = dataDictionary.getDataDictionaryId();
					dictionaryNames.put(dictionaryId, label);
					break;
				}
			}
		}
		return dictionaryNames;

	}

	@Override
	public CalculatoryFieldFormResponse getReferenceDataDictionaryFields(
			Long companyId, boolean isTableField, long tableDicId) {
		List<CalculatoryFieldForm> calculatoryFields = new ArrayList<CalculatoryFieldForm>();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntity(entityMaster.getEntityId(),
						PayAsiaConstants.STATIC_TYPE);
		if (dataDictionaryList != null) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
				if (dataDictionary.getFieldType().equalsIgnoreCase(
						PayAsiaConstants.STATIC_TYPE)) {
					if (!dataDictionary.getDataDictName().equalsIgnoreCase(
							PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
						calculatoryField.setDictionaryId(dataDictionary
								.getDataDictionaryId());
						calculatoryField.setFieldName(dataDictionary
								.getDataDictName());
						calculatoryFields.add(calculatoryField);
					}

				}

			}
		}

		List<Long> formList = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());
		for (Long formId : formList) {
			int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId,
					entityMaster.getEntityId(), formId);
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
					companyId, entityMaster.getEntityId(), maxVersion, formId);

			Tab tab = getTabObject(dynamicForm.getMetaData());
			List<Field> listOfFields = tab.getField();

			for (Field field : listOfFields) {
				if (!field.getType().equals(
						PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
						&& !field.getType().equals(
								PayAsiaConstants.TABLE_FIELD_TYPE)
						&& !field.getType().equals(
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
						&& !field.getType().equals(
								PayAsiaConstants.DOCUMENT_FIELD_TYPE)
						&& !field.getType().equals(
								PayAsiaConstants.FIELD_TYPE_LABEL)
						&& !field.getType().equals(
								PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
					if (field.getDictionaryId() != null) {
						DataDictionary dataDictionary = dataDictionaryDAO
								.findById(field.getDictionaryId());
						if (dataDictionary != null) {
							CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
							if (!dataDictionary
									.getDataDictName()
									.equalsIgnoreCase(
											PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
								calculatoryField.setDictionaryId(dataDictionary
										.getDataDictionaryId());
							}

							if (dataDictionary.getFieldType().equalsIgnoreCase(
									PayAsiaConstants.STATIC_TYPE)) {
								if (!dataDictionary
										.getDataDictName()
										.equalsIgnoreCase(
												PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
									calculatoryField
											.setFieldName(dataDictionary
													.getDataDictName());
								}

							} else {
								calculatoryField.setFieldName(dataDictionary
										.getLabel());
							}

							calculatoryFields.add(calculatoryField);
						}
					}

				}
				if ((field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE) || field
						.getType().equals(
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE))
						&& isTableField) {
					if (field.getDictionaryId().equals(tableDicId)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (!column
									.getType()
									.equals(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
									&& !column
											.getType()
											.equals(PayAsiaConstants.DOCUMENT_FIELD_TYPE)
									&& !column.getType().equals(
											PayAsiaConstants.FIELD_TYPE_LABEL)
									&& !column
											.getType()
											.equals(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {

								if (column.getDictionaryId() != null) {
									DataDictionary dataDictionary = dataDictionaryDAO
											.findById(column.getDictionaryId());
									if (dataDictionary != null) {
										CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
										if (!dataDictionary
												.getDataDictName()
												.equalsIgnoreCase(
														PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
											calculatoryField
													.setDictionaryId(dataDictionary
															.getDataDictionaryId());
										}

										if (dataDictionary
												.getFieldType()
												.equalsIgnoreCase(
														PayAsiaConstants.STATIC_TYPE)) {
											if (!dataDictionary
													.getDataDictName()
													.equalsIgnoreCase(
															PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
												calculatoryField
														.setFieldName(dataDictionary
																.getDataDictName());
											}

										} else {
											calculatoryField
													.setFieldName(dataDictionary
															.getLabel());
										}
										calculatoryFields.add(calculatoryField);
									}
								}
							}
						}
					}
				}
			}
		}

		CalculatoryFieldFormResponse calculatoryFieldFormResponse = new CalculatoryFieldFormResponse();
		calculatoryFieldFormResponse.setCalculatoryFieldList(calculatoryFields);
		return calculatoryFieldFormResponse;

	}

	@Override
	public CalculatoryFieldFormResponse getStringTypeDataDictionaryFields(
			Long companyId, boolean isTableField, long tableDicId) {
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		List<CalculatoryFieldForm> calculatoryFields = new ArrayList<CalculatoryFieldForm>();

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntity(entityMaster.getEntityId(),
						PayAsiaConstants.STATIC_TYPE);
		if (!dataDictionaryList.isEmpty()) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
				ColumnPropertyDTO colProp = generalDAO.getColumnProperties(
						dataDictionary.getTableName(),
						dataDictionary.getColumnName());
				if (colProp.getColumnType().equalsIgnoreCase(
						PayAsiaConstants.PAYASIA_DATATYPE_NVARCHAR)) {
					calculatoryField.setDictionaryId(dataDictionary
							.getDataDictionaryId());
					calculatoryField.setFieldName(dataDictionary
							.getDataDictName());
					calculatoryFields.add(calculatoryField);
				}
			}
		}

		List<Long> formList = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());

		for (Long formId : formList) {
			int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId,
					entityMaster.getEntityId(), formId);
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
					companyId, entityMaster.getEntityId(), maxVersion, formId);

			Tab tab = getTabObject(dynamicForm.getMetaData());
			List<Field> listOfFields = tab.getField();

			for (Field field : listOfFields) {
				if (field.getType().equals(PayAsiaConstants.FIELD_TYPE_TEXT)
						|| field.getType().equals(
								PayAsiaConstants.FIELD_TYPE_COMBO)
						|| field.getType().equals(
								PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
					if (field.getDictionaryId() != null) {
						DataDictionary dataDictionary = dataDictionaryDAO
								.findById(field.getDictionaryId());
						if (dataDictionary != null) {
							CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
							calculatoryField.setDictionaryId(dataDictionary
									.getDataDictionaryId());
							if (dataDictionary.getFieldType().equalsIgnoreCase(
									PayAsiaConstants.STATIC_TYPE)) {
								calculatoryField.setFieldName(dataDictionary
										.getDataDictName());
							} else {
								calculatoryField.setFieldName(dataDictionary
										.getLabel());
							}

							calculatoryFields.add(calculatoryField);
						}
					}

				}

				if ((field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE) || field
						.getType().equals(
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE))
						&& isTableField) {
					if (field.getDictionaryId().equals(tableDicId)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (column.getType().equals(
									PayAsiaConstants.FIELD_TYPE_TEXT)
									|| column
											.getType()
											.equals(PayAsiaConstants.FIELD_TYPE_DROPDOWN)
									|| column
											.getType()
											.equals(PayAsiaConstants.CODEDESC_FIELD_TYPE)) {

								if (column.getDictionaryId() != null) {
									DataDictionary dataDictionary = dataDictionaryDAO
											.findById(column.getDictionaryId());
									if (dataDictionary != null) {
										CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
										calculatoryField
												.setDictionaryId(dataDictionary
														.getDataDictionaryId());
										if (dataDictionary
												.getFieldType()
												.equalsIgnoreCase(
														PayAsiaConstants.STATIC_TYPE)) {
											calculatoryField
													.setFieldName(dataDictionary
															.getDataDictName());
										} else {
											calculatoryField
													.setFieldName(dataDictionary
															.getLabel());
										}
										calculatoryFields.add(calculatoryField);
									}
								}
							}

						}
					}
				}
			}
		}

		CalculatoryFieldFormResponse calculatoryFieldFormResponse = new CalculatoryFieldFormResponse();
		calculatoryFieldFormResponse.setCalculatoryFieldList(calculatoryFields);
		return calculatoryFieldFormResponse;

	}
}
