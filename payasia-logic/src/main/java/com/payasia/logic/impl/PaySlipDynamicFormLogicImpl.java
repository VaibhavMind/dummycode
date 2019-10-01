/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CalculatoryFieldForm;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.PaySlipDynamicForm;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyPayslipReleaseDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.EmpDataImportTemplateFieldDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyPayslipRelease;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormPK;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.PaySlipDynamicFormLogic;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * The Class PaySlipDynamicFormLogicImpl.
 */
@Component
public class PaySlipDynamicFormLogicImpl implements PaySlipDynamicFormLogic {

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

	/** The data import utils. */
	@Resource
	DataImportUtils dataImportUtils;

	@Resource
	MonthMasterDAO monthMasterDAO;

	@Resource
	PayslipDAO payslipDAO;

	@Resource
	CompanyPayslipReleaseDAO companyPayslipReleaseDAO;

	/** The Constant BASE_VERSION. */
	public static final int BASE_VERSION = 1;

	private static final Logger LOGGER = Logger
			.getLogger(PaySlipDynamicFormLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PaySlipDynamicFormLogic#saveDynamicXML(java.lang.Long,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public PaySlipDynamicForm saveDynamicXML(Long companyId, String metaData,
			String tabName, int year, long month, int part) {
		if (year == 0) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		if (month == 0) {
			month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		}
		if (part == 0) {
			part = 1;
		}

		Long formId = null;
		Company company = companyDAO.findById(companyId);
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);

		PaySlipDynamicForm payslipForm = new PaySlipDynamicForm();

		boolean isPayslipReleased = getPayslipReleasedStatus(companyId, month,
				year, part);
		if (isPayslipReleased) {
			payslipForm.setAddedNewTab(false);
			payslipForm.setSaveResponse(PayAsiaConstants.PAYSLIP_RELEASED);
			return payslipForm;
		}

		Integer employeeBasicFormMax = null;
		synchronized (this) {
			employeeBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
					entityMaster.getEntityId(),
					PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME);
		}

		if (employeeBasicFormMax == null) {
			String dummyData = PayAsiaConstants.PAYSLIP_FORM_DESIGNER_DUMMY_XML;
			String basicFormId = saveDummyXML(companyId, dummyData,
					PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME, year, month, part);
			payslipForm.setAddedNewTab(true);
			payslipForm.setBasicFormId(Long.parseLong(basicFormId));
		} else {
			payslipForm.setAddedNewTab(false);
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
			dynamicForm.setId(dynamicFormPk);
			dynamicForm.setEffectiveYear(year);
			MonthMaster monthMaster = monthMasterDAO.findById(month);
			dynamicForm.setMonthMaster(monthMaster);

			if (company
					.getPayslipFrequency()
					.getFrequency()
					.equalsIgnoreCase(
							PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
				dynamicForm.setEffectivePart(1);
			} else {
				dynamicForm.setEffectivePart(part);
			}
			dynamicFormDAO.save(dynamicForm);

		}

		if (!tabName.equals(PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME)) {
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

		payslipForm.setFormId(formId);
		return payslipForm;

	}

	/**
	 * Save dummy xml.
	 * 
	 * @param companyId
	 *            the company id
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @param part
	 * @param month
	 * @param year
	 * @return the string
	 */
	public String saveDummyXML(Long companyId, String metaData, String tabName,
			int year, long month, int part) {
		Long formId = null;
		Company company = companyDAO.findById(companyId);
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);
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
			dynamicForm.setEffectiveYear(year);
			MonthMaster monthMaster = monthMasterDAO.findById(month);
			dynamicForm.setMonthMaster(monthMaster);

			if (company
					.getPayslipFrequency()
					.getFrequency()
					.equalsIgnoreCase(
							PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
				dynamicForm.setEffectivePart(1);
			} else {
				dynamicForm.setEffectivePart(part);
			}
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

			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {

				DataDictionary dataDictionary = new DataDictionary();
				dataDictionary.setCompany(company);
				dataDictionary.setEntityMaster(entityMaster);
				dataDictionary.setDataDictName(new String(Base64
						.decodeBase64(field.getDictionaryName().getBytes())));
				dataDictionary.setLabel(new String(Base64.decodeBase64(field
						.getLabel().getBytes())));
				dataDictionary.setFormID(formId);
				dataDictionary.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
				if (StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.LABEL_FIELD_TYPE)) {
					dataDictionary
							.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
					dataDictionary.setImportable(false);
				} else {
					dataDictionary
							.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
				}
				dataDictionaryDAO.save(dataDictionary);

			}
			if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {

					DataDictionary dataDictionary = new DataDictionary();
					dataDictionary.setCompany(company);
					dataDictionary.setEntityMaster(entityMaster);
					dataDictionary.setDataDictName(new String(
							Base64.decodeBase64(column.getDictionaryName()
									.getBytes())));
					dataDictionary.setLabel(new String(Base64
							.decodeBase64(column.getLabel().getBytes())));
					dataDictionary.setFormID(formId);
					dataDictionary.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
					dataDictionary
							.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
					dataDictionaryDAO.save(dataDictionary);

				}
			}

		}
		return formId.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PaySlipDynamicFormLogic#saveXML(java.lang.Long,
	 * java.lang.String, java.lang.String, long)
	 */
	@Override
	public String saveXML(Long companyId, String metaData, String tabName,
			long argFormId, int year, long month, int part,
			boolean effectiveDateChanged) {

		if (argFormId == 0) {
			return saveDummyXML(companyId, metaData, tabName, year, month, part);
		} else {

			boolean isPayslipReleased = getPayslipReleasedStatus(companyId,
					month, year, part);
			if (isPayslipReleased) {
				return PayAsiaConstants.PAYSLIP_RELEASED;
			}

			List<Long> existingDictionaryIds = new ArrayList<Long>();
			List<Long> tabDictionaryIds = new ArrayList<Long>();
			Company company = companyDAO.findById(companyId);
			EntityMaster entityMaster = entityMasterDAO
					.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);

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

			if (!tabName.equals(PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME)) {
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

				if (!StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.TABLE_FIELD_TYPE)) {
					tabDictionaryIds.add(field.getDictionaryId());
				} else if (field.getType().equals(
						PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						if (column.getDictionaryId() > 0) {
							tabDictionaryIds.add(column.getDictionaryId());
						}
					}
				}
			}

			List<String> payslipMetaDatas = new ArrayList<String>();
			String logoSection = getPayslipDesignerXML(companyId,
					PayAsiaConstants.LOGO_SECTION);
			if (logoSection != null) {
				payslipMetaDatas.add(logoSection);
			}

			String headerSection = getPayslipDesignerXML(companyId,
					PayAsiaConstants.HEADER_SECTION);
			if (headerSection != null) {
				payslipMetaDatas.add(headerSection);
			}
			String statutorySection = getPayslipDesignerXML(companyId,
					PayAsiaConstants.STATUTORY_SECTION);
			if (statutorySection != null) {
				payslipMetaDatas.add(statutorySection);
			}
			String totalIncomeSection = getPayslipDesignerXML(companyId,
					PayAsiaConstants.TOTAL_INCOME_SECTION);
			if (totalIncomeSection != null) {
				payslipMetaDatas.add(totalIncomeSection);
			}
			String summarySection = getPayslipDesignerXML(companyId,
					PayAsiaConstants.SUMMARY_SECTION);
			if (summarySection != null) {
				payslipMetaDatas.add(summarySection);
			}
			String footerSection = getPayslipDesignerXML(companyId,
					PayAsiaConstants.FOOTER_SECTION);
			if (footerSection != null) {
				payslipMetaDatas.add(footerSection);
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

					if (verifyPayslipFieldDelete(payslipMetaDatas,
							dataDictionary.getDataDictionaryId())) {
						return PayAsiaConstants.PAYASIA_FIELD_EXIST;
					}

					try {
						dataDictionaryDAO.delete(dataDictionary);
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(
								PayAsiaConstants.PAYASIA_DELETE);
					}

				}
			}

			try {

				for (Field field : listOfFields) {

					if (!StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.TABLE_FIELD_TYPE)) {

						if (field.getDictionaryId() > 0) {
							DataDictionary dataDictionary = dataDictionaryDAO
									.findById(field.getDictionaryId());
							if (dataDictionary == null) {
								continue;
							}
							if (!dataDictionary.getDataDictName().equals(
									new String(Base64.decodeBase64(field
											.getDictionaryName().getBytes())))) {

								dataDictionary
										.setDataDictName(new String(Base64
												.decodeBase64(field
														.getDictionaryName()
														.getBytes())));
								dataDictionary.setLabel(new String(Base64
										.decodeBase64(field.getLabel()
												.getBytes())));

								if (StringUtils.equalsIgnoreCase(
										field.getType(),
										PayAsiaConstants.LABEL_FIELD_TYPE)) {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
									dataDictionary.setImportable(false);
								} else {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
								}
								dataDictionaryDAO.update(dataDictionary);

							}

							if (dataDictionary.getLabel() == null) {
								if (StringUtils.equalsIgnoreCase(
										field.getType(),
										PayAsiaConstants.LABEL_FIELD_TYPE)) {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
									dataDictionary.setImportable(false);
								} else {
									dataDictionary
											.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
								}
								dataDictionary.setLabel(new String(Base64
										.decodeBase64(field.getLabel()
												.getBytes())));
								dataDictionaryDAO.update(dataDictionary);
							}

						} else {
							DataDictionary dataDictionary = new DataDictionary();
							dataDictionary.setCompany(company);
							dataDictionary.setEntityMaster(entityMaster);
							dataDictionary.setDataDictName(new String(Base64
									.decodeBase64(field.getDictionaryName()
											.getBytes())));
							dataDictionary
									.setLabel(new String(Base64
											.decodeBase64(field.getLabel()
													.getBytes())));
							dataDictionary.setFormID(argFormId);
							dataDictionary
									.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);
							if (StringUtils.equalsIgnoreCase(field.getType(),
									PayAsiaConstants.LABEL_FIELD_TYPE)) {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_LABEL);
								dataDictionary.setImportable(false);
							} else {
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
							}
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
								if (dataDictionary == null) {
									continue;
								}
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
								dataDictionary
										.setDataType(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
								dataDictionary.setFormID(argFormId);
								dataDictionary
										.setFieldType(PayAsiaConstants.DYNAMIC_TYPE);

								dataDictionaryDAO.save(dataDictionary);

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
				dynamicForm.setEffectiveYear(year);
				MonthMaster monthMaster = monthMasterDAO.findById(month);
				dynamicForm.setMonthMaster(monthMaster);

				if (company
						.getPayslipFrequency()
						.getFrequency()
						.equalsIgnoreCase(
								PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
					dynamicForm.setEffectivePart(1);
				} else {
					dynamicForm.setEffectivePart(part);
				}

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

				} else {
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

				if (effectiveDateChanged) {
					updateRestSections(companyId, argFormId, year, part,
							company, entityMaster, dynamicForm, monthMaster);

				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
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
					PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {

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

	private void updateRestSections(Long companyId, long argFormId, int year,
			int part, Company company, EntityMaster entityMaster,
			DynamicForm dynamicForm, MonthMaster monthMaster) {
		List<Object[]> tuples = dynamicFormDAO.getSectionsForEffectiveDate(
				companyId, entityMaster.getEntityId(), argFormId);
		for (Object[] tuple : tuples) {
			int count = 0;
			Integer version = 0;
			Long formID = 0l;
			for (Object object : tuple) {
				if (count == 0) {
					formID = (Long) object;
				} else {
					version = (Integer) object;
				}
				count++;

			}
			DynamicForm maxDynamicForm = dynamicFormDAO
					.findByMaxVersionByFormIdReadWrite(companyId,
							entityMaster.getEntityId(), version, formID);
			dynamicFormDAO.getDetachedEntity(maxDynamicForm);
			maxDynamicForm.setEffectiveYear(year);
			maxDynamicForm.setMonthMaster(monthMaster);
			if (company
					.getPayslipFrequency()
					.getFrequency()
					.equalsIgnoreCase(
							PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
				dynamicForm.setEffectivePart(1);
			} else {
				dynamicForm.setEffectivePart(part);
			}

			DynamicFormPK formPK = maxDynamicForm.getId();
			formPK.setVersion(version + 1);

			dynamicFormDAO.save(maxDynamicForm);

		}
	}

	/**
	 * Purpose: To Verify that the payslip field can be deleted or not.
	 * 
	 * @param payslipMetaDatas
	 *            the payslip meta datas
	 * @param dataDictionaryId
	 *            the data dictionary id
	 * @return true, if successful
	 */
	public boolean verifyPayslipFieldDelete(List<String> payslipMetaDatas,
			long dataDictionaryId) {

		for (String metaData : payslipMetaDatas) {
			Tab tab = getTabObject(metaData);

			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {

				if (!StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.FIELD_TYPE_LOGO)) {
					if (field.getDictionaryId() != null
							&& field.getDictionaryId() == dataDictionaryId) {
						LOGGER.info("Field with DataDictionary Id: "
								+ dataDictionaryId + " exist.");
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Purpose: To get the payslip designer metadata based on Section Name.
	 * 
	 * @param companyId
	 *            the company id
	 * @param sectionName
	 *            the section name
	 * @return the payslip designer xml
	 */
	public String getPayslipDesignerXML(Long companyId, String sectionName) {

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);

		Integer maxVersion = dynamicFormDAO.getMaxVersion(companyId,
				entityMaster.getEntityId(), sectionName);
		if (maxVersion != null) {
			DynamicForm maxDynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityMaster.getEntityId(), maxVersion,
					sectionName);

			return maxDynamicForm.getMetaData();
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PaySlipDynamicFormLogic#getXML(java.lang.Long,
	 * long)
	 */
	@Override
	public PaySlipDynamicForm getXML(Long companyId, long formId) {
		PaySlipDynamicForm paySlipDynamicForm = new PaySlipDynamicForm();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);

		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId,
				entityMaster.getEntityId(), formId);
		if (maxVersion == 0) {

			return paySlipDynamicForm;
		} else {
			DynamicForm maxDynamicForm = dynamicFormDAO
					.findByMaxVersionByFormId(companyId,
							entityMaster.getEntityId(), maxVersion, formId);

			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException | SAXException ume) {
				LOGGER.error(ume);
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
			} catch (XMLStreamException e2) {
				LOGGER.error(e2);
			}
			Tab tab = null;
			try {
				tab = (Tab) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			}

			List<Field> listOfFields = tab.getField();
			if (!maxDynamicForm.getTabName().equals(
					PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME)) {
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

				if (!StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.TABLE_FIELD_TYPE)) {

					DataDictionary dataDictionary;

					if (Base64.isArrayByteBase64(field.getDictionaryName()
							.getBytes())) {
						dataDictionary = dataDictionaryDAO
								.findByDictionaryName(
										companyId,
										entityMaster.getEntityId(),
										new String(Base64
												.decodeBase64(field
														.getDictionaryName()
														.getBytes())), formId);

						if (dataDictionary == null) {
							continue;
						}

						field.setDictionaryId(dataDictionary
								.getDataDictionaryId());
					} else {
						dataDictionary = dataDictionaryDAO
								.findByDictionaryName(companyId,
										entityMaster.getEntityId(),
										field.getDictionaryName(), formId);

						if (dataDictionary == null) {
							continue;
						}
						field.setDictionaryId(dataDictionary
								.getDataDictionaryId());
					}

				}
				if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {

						DataDictionary dataDictionary = dataDictionaryDAO
								.findByDictionaryName(
										companyId,
										entityMaster.getEntityId(),
										new String(Base64
												.decodeBase64(column
														.getDictionaryName()
														.getBytes())), formId);
						if (dataDictionary == null) {
							continue;
						}

						column.setDictionaryId(dataDictionary
								.getDataDictionaryId());

					}
				}

			}

			Marshaller marshaller = null;

			try {
				marshaller = XMLUtil.getDocumentMarshaller();
				marshaller.marshal(tab, streamWriter);
			} catch (JAXBException | SAXException ume) {
				LOGGER.error(ume);
			}

			paySlipDynamicForm.setMetaData(byteArrayOutputStream.toString());

			return paySlipDynamicForm;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PaySlipDynamicFormLogic#getTabList(java.lang.Long)
	 */
	@Override
	public List<PaySlipDynamicForm> getTabList(Long companyId) {

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);
		List<Long> formList = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());
		List<PaySlipDynamicForm> paySlipDynamicFormList = new ArrayList<PaySlipDynamicForm>();
		PaySlipDynamicForm paySlipForm = new PaySlipDynamicForm();
		Integer payslipBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
				entityMaster.getEntityId(),
				PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME);

		if (payslipBasicFormMax != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityMaster.getEntityId(), payslipBasicFormMax,
					PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME);
			paySlipForm.setFormId(dynamicForm.getId().getFormId());
			paySlipForm.setTabName(dynamicForm.getTabName());
			paySlipDynamicFormList.add(paySlipForm);
		} else {
			paySlipForm.setFormId(0);

			paySlipForm.setTabName(PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME);
			paySlipDynamicFormList.add(paySlipForm);
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
					PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME)) {
				PaySlipDynamicForm PaySlipDynamicForm = new PaySlipDynamicForm();

				PaySlipDynamicForm.setFormId(dynamicForm.getId().getFormId());
				PaySlipDynamicForm.setTabName(dynamicForm.getTabName());
				if (dataDictionary != null) {
					PaySlipDynamicForm.setDictionaryId(dataDictionary
							.getDataDictionaryId());
					PaySlipDynamicForm.setDictionaryName(dataDictionary
							.getDataDictName());

				}

				paySlipDynamicFormList.add(PaySlipDynamicForm);
			}
		}

		return paySlipDynamicFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PaySlipDynamicFormLogic#getOptionsFromXL(com.payasia
	 * .common.form.PaySlipDynamicForm)
	 */
	@Override
	public PaySlipDynamicForm getOptionsFromXL(
			PaySlipDynamicForm paySlipDynamicForm) {

		PaySlipDynamicForm payslipDynamicForm = new PaySlipDynamicForm();
		List<String> optionsString = new ArrayList<String>();
		String fileName = paySlipDynamicForm.getAttachment()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {

			optionsString = ExcelUtils.readXLS(paySlipDynamicForm
					.getAttachment());
		}

		else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			optionsString = ExcelUtils.readXLSX(paySlipDynamicForm
					.getAttachment());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(optionsString, jsonConfig);
		payslipDynamicForm.setOptions(jsonObject.toString());
		return payslipDynamicForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PaySlipDynamicFormLogic#deleteTab(java.lang.Long,
	 * long)
	 */
	@Override
	public String deleteTab(Long companyId, long formId) {

		String response = null;
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);
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
				dataDictionaryDAO.deleteByCondition(companyId,
						entityMaster.getEntityId(), formId);
				response = PayAsiaConstants.PAYASIA_SUCCESS;
			}

		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PaySlipDynamicFormLogic#getCalculatioryFields(java.
	 * lang.Long)
	 */
	@Override
	public CalculatoryFieldFormResponse getCalculatioryFields(Long companyId) {

		EntityMaster payslipDesignerEntityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId,
						payslipDesignerEntityMaster.getEntityId(), null);
		List<CalculatoryFieldForm> calculatoryFields = new ArrayList<CalculatoryFieldForm>();
		for (DataDictionary dataDictionary : dataDictionaryList) {
			CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
			calculatoryField.setDictionaryId(dataDictionary
					.getDataDictionaryId());
			if (dataDictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {
				calculatoryField.setFieldName(dataDictionary.getDataDictName());
			} else {
				calculatoryField.setFieldName(dataDictionary.getLabel());
			}

			calculatoryFields.add(calculatoryField);
		}

		CalculatoryFieldFormResponse calculatoryFieldFormResponse = new CalculatoryFieldFormResponse();
		calculatoryFieldFormResponse.setCalculatoryFieldList(calculatoryFields);
		return calculatoryFieldFormResponse;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PaySlipDynamicFormLogic#getDictionaryLabel(java.lang
	 * .Long, java.lang.String[])
	 */
	@Override
	public Map<Long, String> getDictionaryLabel(Long companyId,
			String[] dictionaryIds) {

		EntityMaster payslipDesignerEntityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);
		Map<Long, String> dictionaryNames = new HashMap<Long, String>();
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId,
						payslipDesignerEntityMaster.getEntityId(), null);
		for (String argDictionaryId : dictionaryIds) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PaySlipDynamicFormLogic#checkFieldEdit(java.lang.Long,
	 * java.lang.String, boolean, java.lang.Long, java.lang.String)
	 */
	@Override
	public String checkFieldEdit(Long formId, String fieldName,
			boolean isTable, Long companyId, String tablePosition) {
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);
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

		}

		return PayAsiaConstants.PAYASIA_SUCCESS;
	}

	@Override
	public List<MonthMasterDTO> getMonthList() {
		List<MonthMaster> monthMasters = monthMasterDAO.findAll();
		List<MonthMasterDTO> masterDTOs = new ArrayList<>();
		for (MonthMaster monthMaster : monthMasters) {
			MonthMasterDTO monthMasterDTO = new MonthMasterDTO();
			monthMasterDTO.setMonthId(monthMaster.getMonthId());
			monthMasterDTO.setMonthName(monthMaster.getMonthName());
			masterDTOs.add(monthMasterDTO);
		}
		return masterDTOs;
	}

	@Override
	public PaySlipDynamicForm getPaySlipFrequencyDetails(Long companyId) {
		PaySlipDynamicForm paySlipDynamicForm = new PaySlipDynamicForm();

		Company company = companyDAO.findById(companyId);
		PayslipFrequency paySlipFrequency = company.getPayslipFrequency();
		paySlipDynamicForm.setPaySlipFrequency(paySlipFrequency.getFrequency());
		paySlipDynamicForm.setPayslipPart(company.getPart());

		return paySlipDynamicForm;

	}

	@Override
	public PaySlipDynamicForm getEffectiveFrom(Long companyId) {
		PaySlipDynamicForm paySlipDynamicForm = new PaySlipDynamicForm();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);
		Integer employeeBasicFormMax = null;
		synchronized (this) {
			employeeBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
					entityMaster.getEntityId(),
					PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME);
		}

		if (employeeBasicFormMax == null) {
			paySlipDynamicForm.setEffectiveMonth(0);
			paySlipDynamicForm.setEffectiveYear(0);
			paySlipDynamicForm.setEffectivePart(0);
		} else {
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityMaster.getEntityId(),
					employeeBasicFormMax,
					PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME);
			paySlipDynamicForm.setEffectiveMonth(dynamicForm.getMonthMaster()
					.getMonthId());
			paySlipDynamicForm.setEffectiveYear(dynamicForm.getEffectiveYear());
			paySlipDynamicForm.setEffectivePart(dynamicForm.getEffectivePart());
		}

		return paySlipDynamicForm;

	}

	@Override
	public PaySlipDynamicForm getCurrentPayslipInfo(Long companyId) {
		PaySlipDynamicForm paySlipDynamicForm = new PaySlipDynamicForm();
		List<Object[]> tuples = payslipDAO
				.getLatestPayslipEffectiveDate(companyId);

		for (Object[] tuple : tuples) {
			paySlipDynamicForm.setEffectiveYear((Integer) tuple[0]);
			paySlipDynamicForm.setEffectiveMonth((Long) tuple[1]);
			paySlipDynamicForm.setEffectivePart((Integer) tuple[2]);
		}

		if (tuples.size() == 0) {
			paySlipDynamicForm.setEffectiveMonth(0);
			paySlipDynamicForm.setEffectiveYear(0);
			paySlipDynamicForm.setEffectivePart(0);
		}
		return paySlipDynamicForm;
	}

	@Override
	public boolean getPayslipReleasedStatus(Long companyId, Long monthId,
			int year, Integer part) {
		boolean isPayslipReleased = false;
		CompanyPayslipRelease companyPayslipReleaseVO = companyPayslipReleaseDAO
				.findByCondition(null, monthId, year, part, companyId);
		if (companyPayslipReleaseVO != null) {
			if (companyPayslipReleaseVO.isReleased()) {
				isPayslipReleased = true;
			}

		}
		return isPayslipReleased;
	}
}
