/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.ExportDataField;
import com.mind.payasia.xml.bean.ExportFilter;
import com.mind.payasia.xml.bean.ExportTemplate;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataDictionaryDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.dto.ExcelImportExportConditionDTO;
import com.payasia.common.dto.SectionInfoDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DBTableInformationForm;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.ExcelExportToolForm;
import com.payasia.common.form.ExcelExportToolFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.ExportDataXMLUtil;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.ShortlistOperatorEnum;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmpDataExportTemplateDAO;
import com.payasia.dao.EmpDataExportTemplateFieldDAO;
import com.payasia.dao.EmpDataExportTemplateFilterDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.MultiLingualDataDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.EmpDataExportTemplate;
import com.payasia.dao.bean.EmpDataExportTemplateField;
import com.payasia.dao.bean.EmpDataExportTemplateFilter;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.MultiLingualData;
import com.payasia.logic.ExcelExportToolLogic;
import com.payasia.logic.GeneralFilterLogic;

/**
 * The Class ExcelExportToolLogicImpl.
 */
/**
 * @author ragulapraveen
 * 
 */
@Component
public class ExcelExportToolLogicImpl implements ExcelExportToolLogic {

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The emp data export template filter dao. */
	@Resource
	EmpDataExportTemplateFilterDAO empDataExportTemplateFilterDAO;

	/** The emp data export template dao. */
	@Resource
	EmpDataExportTemplateDAO empDataExportTemplateDAO;

	/** The emp data export template field dao. */
	@Resource
	EmpDataExportTemplateFieldDAO empDataExportTemplateFieldDAO;

	/** The general dao. */
	@Resource
	GeneralDAO generalDAO;

	/** The multi lingual data dao. */
	@Resource
	MultiLingualDataDAO multiLingualDataDAO;

	@Resource
	GeneralFilterLogic generalFilterLogic;

	@Resource
	MessageSource messageSource;
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(ExcelExportToolLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelExportToolLogic#saveTemplate(java.lang.Long,
	 * java.lang.String, java.lang.Long)
	 */
	@Override
	public String saveTemplate(Long companyId, String metaData, Long languageId) {

		try {
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = ExportDataXMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {

				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			} catch (SAXException saxException) {

				throw new PayAsiaSystemException(saxException.getMessage(),
						saxException);
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

			ExportTemplate template = null;
			try {
				template = (ExportTemplate) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {

				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			}

			String templateName = new String(Base64.decodeBase64(template
					.getTemplateName().getBytes()));
			try {
				templateName = URLDecoder.decode(templateName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			String templateDesc = new String(Base64.decodeBase64(template
					.getTemplateDesc().getBytes()));
			try {
				templateDesc = URLDecoder.decode(templateDesc, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			String templateScope = template.getScope();
			long entityId = template.getCategory();
			String prefix = new String(Base64.decodeBase64(template.getPrefix()
					.getBytes()));
			String suffix = new String(Base64.decodeBase64(template.getSuffix()
					.getBytes()));
			boolean prefixCheck = template.isPrefixCheck();
			boolean suffixCheck = template.isSuffixCheck();
			boolean tsPrefixCheck = template.isTsPrefixCheck();
			boolean tsSuffixCheck = template.isTsSuffixCheck();
			boolean multipleSectionCheck = template.isMultipleSectionCheck();

			List<EmpDataExportTemplate> returnedTemplates = empDataExportTemplateDAO
					.findByName(templateName, companyId);

			if (returnedTemplates != null && !returnedTemplates.isEmpty()) {
				return "payasia.excel.export.duplicate.template.name";
			}

			EntityMaster entityMaster = entityMasterDAO.findById(entityId);
			Company company = companyDAO.findById(companyId);

			EmpDataExportTemplate empDataExportTemplate = new EmpDataExportTemplate();
			empDataExportTemplate.setMultipleSection(multipleSectionCheck);
			empDataExportTemplate.setCategory(entityMaster.getEntityName());
			empDataExportTemplate.setCompany(company);
			empDataExportTemplate.setDescription(templateDesc);
			empDataExportTemplate.setEntityMaster(entityMaster);
			empDataExportTemplate.setIncludePrefix(prefixCheck);
			empDataExportTemplate.setIncludeSuffix(suffixCheck);
			empDataExportTemplate.setIncludeTemplateNameAsPrefix(tsPrefixCheck);
			empDataExportTemplate.setIncludeTimestampAsSuffix(tsSuffixCheck);
			empDataExportTemplate.setPrefix(prefix);
			empDataExportTemplate.setSuffix(suffix);
			empDataExportTemplate.setTemplateName(templateName);
			empDataExportTemplate.setScope(templateScope);
			if (templateScope.equals(PayAsiaConstants.PAYASIA_SCOPE_G)) {
				empDataExportTemplate
						.setCompanyGroup(company.getCompanyGroup());
			}

			if (template.getFormId() != 0) {
				empDataExportTemplate.setFormID(template.getFormId());
			}

			if (template.getCustTableName() != 0) {
				empDataExportTemplate.setCustom_Table_Position(template
						.getCustTableName());
			}
			EmpDataExportTemplate exportTemplateReturned = empDataExportTemplateDAO
					.save(empDataExportTemplate);

			List<ExportDataField> listOfDataFields = template
					.getExportDataField();
			List<ExportFilter> listOfFilters = template.getExportFilter();

			Map<Long, EntityMaster> entityMasterMap = new HashMap<>();
			List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
			for (EntityMaster entity : entityMasterList) {
				entityMasterMap.put(entity.getEntityId(), entity);
			}

			for (ExportDataField exportDataField : listOfDataFields) {
				if (exportDataField.isSelect()) {
					EmpDataExportTemplateField empDataExportTemplateField = new EmpDataExportTemplateField();

					if (templateScope.equals(PayAsiaConstants.PAYASIA_SCOPE_C)) {

						DataDictionary dataDictionary = dataDictionaryDAO
								.findById(exportDataField.getDictionaryId());
						empDataExportTemplateField
								.setDataDictionary(dataDictionary);

						MultiLingualData multiLingualData = multiLingualDataDAO
								.findByDictionaryIdAndLanguage(
										dataDictionary.getDataDictionaryId(),
										languageId);

						if (StringUtils
								.isBlank(exportDataField.getExcelField())) {
							if (dataDictionary.getFieldType().equalsIgnoreCase(
									PayAsiaConstants.STATIC_TYPE)) {
								String fieldName = dataDictionary
										.getDataDictName();
								if (fieldName.indexOf(':') != -1) {
									fieldName = fieldName.replace(":", "");
								}
								empDataExportTemplateField
										.setExcelFieldName(fieldName);
							} else {
								String fieldName = dataDictionary.getLabel();
								if (fieldName.indexOf(':') != -1) {
									fieldName = fieldName.replace(":", "");
								}
								empDataExportTemplateField
										.setExcelFieldName(fieldName);
							}
							if (multiLingualData != null) {
								empDataExportTemplateField
										.setExcelFieldName(multiLingualData
												.getLabel());
							}
						} else {
							String fieldName = new String(
									Base64.decodeBase64(exportDataField
											.getExcelField().getBytes()));
							if (fieldName.indexOf(':') != -1) {
								fieldName = fieldName.replace(":", "");
							}
							empDataExportTemplateField
									.setExcelFieldName(fieldName);
						}

					} else {
						Long fieldEntityId = exportDataField.getEntityId();
						empDataExportTemplateField
								.setEntityMaster(entityMasterMap
										.get(fieldEntityId));
						empDataExportTemplateField.setDataDictName(new String(
								Base64.decodeBase64(exportDataField
										.getDataDictionaryName().getBytes())));

						if (StringUtils
								.isBlank(exportDataField.getExcelField())) {
							String fieldName = exportDataField
									.getDataDictionaryName();
							if (fieldName.indexOf(':') != -1) {
								fieldName = fieldName.replace(":", "");
							}

							empDataExportTemplateField
									.setExcelFieldName(getFieldNameExcelField(fieldName));

						} else {
							String fieldName = new String(
									Base64.decodeBase64(exportDataField
											.getExcelField().getBytes()));
							if (fieldName.indexOf(':') != -1) {
								fieldName = fieldName.replace(":", "");
							}

							empDataExportTemplateField
									.setExcelFieldName(fieldName);

						}

					}

					empDataExportTemplateField
							.setEmpDataExportTemplate(exportTemplateReturned);
					empDataExportTemplateFieldDAO
							.save(empDataExportTemplateField);
				}

			}

			for (ExportFilter exportFilter : listOfFilters) {
				EmpDataExportTemplateFilter empDataExportTemplateFilter = new EmpDataExportTemplateFilter();
				String equalityOperator = URLDecoder.decode(
						exportFilter.getEqualityOperator(), "UTF-8");
				// Check Valid ShortList Operator
				ShortlistOperatorEnum shortlistOperatorEnum = ShortlistOperatorEnum
						.getFromOperator(equalityOperator);
				if (shortlistOperatorEnum == null) {
					throw new PayAsiaSystemException(
							PayAsiaConstants.PAYASIA_SHORTLIST_OPERATOR_NOT_VALID);
				}
				empDataExportTemplateFilter
						.setEqualityOperator(equalityOperator);
				empDataExportTemplateFilter.setLogicalOperator(exportFilter
						.getLogicalOperator());
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(exportFilter.getDictionaryId());
				empDataExportTemplateFilter.setDataDictionary(dataDictionary);
				empDataExportTemplateFilter.setValue(URLDecoder.decode(
						exportFilter.getValue(), "UTF-8"));
				empDataExportTemplateFilter.setOpenBracket(exportFilter
						.getOpenBracket());
				empDataExportTemplateFilter.setCloseBracket(exportFilter
						.getCloseBracket());
				empDataExportTemplateFilter
						.setEmpDataExportTemplate(exportTemplateReturned);

				empDataExportTemplateFilterDAO
						.save(empDataExportTemplateFilter);
			}

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			return "payasia.excel.export.error.in.saving.export.template";
		}

		return "payasia.excel.export.record.successfully.inserted";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelExportToolLogic#deleteTemplate(long)
	 */
	@Override
	public void deleteTemplate(long templateId) {

		EmpDataExportTemplate empDataExportTemplate = empDataExportTemplateDAO
				.findById(templateId);

		empDataExportTemplateDAO.delete(empDataExportTemplate);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelExportToolLogic#editTemplate(long,
	 * java.lang.String, java.lang.Long, java.lang.Long)
	 */
	@Override
	public String editTemplate(long templateId, String metaData,
			Long companyId, Long languageId) {

		Map<Long, EntityMaster> entityMasterMap = new HashMap<>();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entity : entityMasterList) {
			entityMasterMap.put(entity.getEntityId(), entity);
		}

		try {
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = ExportDataXMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {

				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			} catch (SAXException saxException) {

				throw new PayAsiaSystemException(saxException.getMessage(),
						saxException);
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

			ExportTemplate template = null;
			try {
				template = (ExportTemplate) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {

				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			}

			String templateName = new String(Base64.decodeBase64(template
					.getTemplateName().getBytes()));
			try {
				templateName = URLDecoder.decode(templateName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			String templateDesc = new String(Base64.decodeBase64(template
					.getTemplateDesc().getBytes()));
			try {
				templateDesc = URLDecoder.decode(templateDesc, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			String scope = template.getScope();
			long entityId = template.getCategory();
			String prefix = template.getPrefix();
			String suffix = template.getSuffix();
			boolean prefixCheck = template.isPrefixCheck();
			boolean suffixCheck = template.isSuffixCheck();
			boolean tsPrefixCheck = template.isTsPrefixCheck();
			boolean tsSuffixCheck = template.isTsSuffixCheck();

			EmpDataExportTemplate returnedTemplate = empDataExportTemplateDAO
					.findById(templateId);

			if (!returnedTemplate.getTemplateName().equalsIgnoreCase(
					templateName)) {
				List<EmpDataExportTemplate> returnedTemplates = empDataExportTemplateDAO
						.findByName(templateName, companyId);

				if (returnedTemplates != null && !returnedTemplates.isEmpty()) {
					return "payasia.excel.export.duplicate.template.name";
				}
			}

			EntityMaster entityMaster = entityMasterDAO.findById(entityId);
			Company company = companyDAO.findById(companyId);

			empDataExportTemplateFieldDAO.deleteByCondition(templateId);
			empDataExportTemplateFilterDAO.deleteByCondition(templateId);

			EmpDataExportTemplate empDataExportTemplate = returnedTemplate;
			empDataExportTemplate.setMultipleSection(returnedTemplate
					.isMultipleSection());
			empDataExportTemplate.setCategory(entityMaster.getEntityName());
			empDataExportTemplate.setCompany(company);
			empDataExportTemplate.setDescription(templateDesc);
			empDataExportTemplate.setEntityMaster(entityMaster);
			empDataExportTemplate.setIncludePrefix(prefixCheck);
			empDataExportTemplate.setIncludeSuffix(suffixCheck);
			empDataExportTemplate.setIncludeTemplateNameAsPrefix(tsPrefixCheck);
			empDataExportTemplate.setIncludeTimestampAsSuffix(tsSuffixCheck);
			empDataExportTemplate.setPrefix(prefix);
			empDataExportTemplate.setSuffix(suffix);
			empDataExportTemplate.setTemplateName(templateName);
			empDataExportTemplate.setExportTemplateId(templateId);
			empDataExportTemplate.setScope(scope);
			if (scope.equals(PayAsiaConstants.PAYASIA_SCOPE_C)) {
				empDataExportTemplate.setCompanyGroup(null);
			} else {
				empDataExportTemplate
						.setCompanyGroup(company.getCompanyGroup());
			}

			if (template.getFormId() != 0) {
				empDataExportTemplate.setFormID(template.getFormId());
			} else {
				empDataExportTemplate.setFormID(null);
			}

			if (template.getCustTableName() != 0) {
				empDataExportTemplate.setCustom_Table_Position(template
						.getCustTableName());
			} else {
				empDataExportTemplate.setCustom_Table_Position(null);
			}

			empDataExportTemplateDAO.update(empDataExportTemplate);

			List<ExportDataField> listOfDataFields = template
					.getExportDataField();
			List<ExportFilter> listOfFilters = template.getExportFilter();
			for (ExportDataField exportDataField : listOfDataFields) {
				if (exportDataField.isSelect()) {
					EmpDataExportTemplateField empDataExportTemplateField = new EmpDataExportTemplateField();
					if (scope.equals(PayAsiaConstants.PAYASIA_SCOPE_C)) {
						DataDictionary dataDictionary = dataDictionaryDAO
								.findById(exportDataField.getDictionaryId());

						empDataExportTemplateField
								.setDataDictionary(dataDictionary);

						MultiLingualData multiLingualData = multiLingualDataDAO
								.findByDictionaryIdAndLanguage(
										dataDictionary.getDataDictionaryId(),
										languageId);

						if (StringUtils
								.isBlank(exportDataField.getExcelField())) {
							if (dataDictionary.getFieldType().equalsIgnoreCase(
									PayAsiaConstants.STATIC_TYPE)) {
								String fieldName = dataDictionary
										.getDataDictName();
								if (fieldName.indexOf(':') != -1) {
									fieldName = fieldName.replace(":", "");
								}
								empDataExportTemplateField
										.setExcelFieldName(fieldName);
							} else {
								String fieldName = dataDictionary.getLabel();
								if (fieldName.indexOf(':') != -1) {
									fieldName = fieldName.replace(":", "");
								}
								empDataExportTemplateField
										.setExcelFieldName(fieldName);
							}
							if (multiLingualData != null) {
								empDataExportTemplateField
										.setExcelFieldName(multiLingualData
												.getLabel());
							}
						} else {
							String fieldName = new String(
									Base64.decodeBase64(exportDataField
											.getExcelField().getBytes()));
							if (fieldName.indexOf(':') != -1) {
								fieldName = fieldName.replace(":", "");
							}
							empDataExportTemplateField
									.setExcelFieldName(fieldName);
						}

					} else {

						EntityMaster fieldEntity = entityMasterMap
								.get(exportDataField.getEntityId());
						empDataExportTemplateField.setEntityMaster(fieldEntity);
						empDataExportTemplateField.setDataDictName(new String(
								Base64.decodeBase64(exportDataField
										.getDataDictionaryName().getBytes())));

						if (StringUtils
								.isBlank(exportDataField.getExcelField())) {
							String fieldName = exportDataField
									.getDataDictionaryName();
							if (fieldName.indexOf(':') != -1) {
								fieldName = fieldName.replace(":", "");
							}
							empDataExportTemplateField
									.setExcelFieldName(getFieldNameExcelField(fieldName));

						} else {
							String fieldName = new String(
									Base64.decodeBase64(exportDataField
											.getExcelField().getBytes()));
							if (fieldName.indexOf(':') != -1) {
								fieldName = fieldName.replace(":", "");
							}
							empDataExportTemplateField
									.setExcelFieldName(fieldName);

						}

					}
					empDataExportTemplateField
							.setEmpDataExportTemplate(empDataExportTemplate);
					empDataExportTemplateFieldDAO
							.save(empDataExportTemplateField);

				}
			}

			for (ExportFilter exportFilter : listOfFilters) {
				EmpDataExportTemplateFilter empDataExportTemplateFilter = new EmpDataExportTemplateFilter();
				String equalityOperator = URLDecoder.decode(
						exportFilter.getEqualityOperator(), "UTF-8");
				// Check Valid ShortList Operator
				ShortlistOperatorEnum shortlistOperatorEnum = ShortlistOperatorEnum
						.getFromOperator(equalityOperator);
				if (shortlistOperatorEnum == null) {
					throw new PayAsiaSystemException(
							PayAsiaConstants.PAYASIA_SHORTLIST_OPERATOR_NOT_VALID);
				}

				empDataExportTemplateFilter
						.setEqualityOperator(equalityOperator);
				empDataExportTemplateFilter.setLogicalOperator(exportFilter
						.getLogicalOperator());
				empDataExportTemplateFilter.setValue(URLDecoder.decode(
						exportFilter.getValue(), "UTF-8"));
				empDataExportTemplateFilter
						.setEmpDataExportTemplate(empDataExportTemplate);
				empDataExportTemplateFilter.setOpenBracket(exportFilter
						.getOpenBracket());
				empDataExportTemplateFilter.setCloseBracket(exportFilter
						.getCloseBracket());
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(exportFilter.getDictionaryId());
				empDataExportTemplateFilter.setDataDictionary(dataDictionary);
				empDataExportTemplateFilterDAO
						.save(empDataExportTemplateFilter);

			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			return "payasia.excel.export.error.in.saving.export.template";
		}

		return "payasia.excel.export.record.successfully.updated";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelExportToolLogic#getEntityList()
	 */
	@Override
	public List<EntityMasterDTO> getEntityList() {
		Locale locale = UserContext.getLocale();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		List<EntityMasterDTO> entityList = new ArrayList<EntityMasterDTO>();

		for (EntityMaster entityMaster : entityMasterList) {
			if (!entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.PAY_SLIP_ENTITY_NAME)) {
				EntityMasterDTO entityMasterDTO = new EntityMasterDTO();
				entityMasterDTO.setEntityId(entityMaster.getEntityId());
				if (StringUtils.isNotBlank(entityMaster.getLabelKey())) {
					String labelMsg = messageSource
							.getMessage(entityMaster.getLabelKey(),
									new Object[] {}, locale);
					entityMasterDTO.setEntityName(labelMsg);
				} else {
					entityMasterDTO.setEntityName(entityMaster.getEntityName());
					if (entityMaster.getEntityName().equalsIgnoreCase(
							PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {
						entityMasterDTO
								.setEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
					}
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
	 * @see
	 * com.payasia.logic.ExcelExportToolLogic#getExistMapping(java.lang.Long,
	 * long, java.lang.Long)
	 */
	@Override
	public ExcelExportToolForm getExistMapping(Long companyId, long entityId,
			Long languageId) {
		ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntityAndCompanyIdAndFormula(companyId,
						entityId);
		Set<SectionInfoDTO> sectionList = new HashSet<SectionInfoDTO>();
		Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
		List<DBTableInformationForm> dbTablelist = new ArrayList<DBTableInformationForm>();
		EntityMaster entityMaster = entityMasterDAO.findById(entityId);
		String baseSectionName = "";
		long baseSectionId = 100000l + entityId;

		if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
			baseSectionName = PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME;
		} else if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.COMPANY_ENTITY_NAME)) {
			baseSectionName = PayAsiaConstants.COMPANY_BASIC_TAB_NAME;

		} else {
			baseSectionName = PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME;
		}

		try {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				if (dataDictionary.getFormID() != null
						&& dataDictionary.getFormID() != 0) {
					DynamicForm dynamicForm = dynamicFormDAO
							.findMaxVersionByFormId(companyId, entityId,
									dataDictionary.getFormID());
					String tabName = dynamicForm.getTabName();
					if (tabName.equalsIgnoreCase(baseSectionName)) {
						baseSectionId = dataDictionary.getFormID();
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		HashMap<Long, Tab> dynamicFormObjects = new HashMap<>();
		HashMap<Long, String> dynamicFormSectionName = new HashMap<>();
		SectionInfoDTO basicSectionInfoDTO = new SectionInfoDTO();
		basicSectionInfoDTO.setSectionId(baseSectionId);
		basicSectionInfoDTO.setSectionName(baseSectionName);
		sectionList.add(basicSectionInfoDTO);

		try {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				boolean isTableVal = false;
				DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
				dbTableInformationForm.setDataDictionaryId(dataDictionary
						.getDataDictionaryId());
				MultiLingualData multiLingualData = multiLingualDataDAO
						.findByDictionaryIdAndLanguage(
								dataDictionary.getDataDictionaryId(),
								languageId);

				if (dataDictionary.getFieldType().equalsIgnoreCase(
						PayAsiaConstants.STATIC_TYPE)) {
					dbTableInformationForm.setDbFields(dataDictionary
							.getDataDictName());
					dbTableInformationForm.setDbFields(new String(Base64
							.encodeBase64(dbTableInformationForm.getDbFields()
									.getBytes())));
					ColumnPropertyDTO columnPropertyDTO = generalDAO
							.getColumnProperties(dataDictionary.getTableName(),
									dataDictionary.getColumnName());
					dbTableInformationForm.setSectionName(baseSectionName);

					dbTableInformationForm.setDataType(columnPropertyDTO
							.getColumnType());
					dbTableInformationForm.setSectionId(baseSectionId);
				} else {

					dbTableInformationForm.setDbFields(dataDictionary
							.getLabel());
					dbTableInformationForm.setSectionId(dataDictionary
							.getFormID());
					dbTableInformationForm.setDbFields(new String(Base64
							.encodeBase64(dbTableInformationForm.getDbFields()
									.getBytes())));

					Tab tab = dynamicFormObjects
							.get(dataDictionary.getFormID());

					String tabName = null;
					if (tab == null) {
						DynamicForm dynamicForm = dynamicFormDAO
								.findMaxVersionByFormId(companyId, entityId,
										dataDictionary.getFormID());
						Unmarshaller unmarshaller = null;
						try {
							unmarshaller = XMLUtil.getDocumentUnmarshaller();
						} catch (JAXBException jaxbException) {
							LOGGER.error(jaxbException.getMessage(),
									jaxbException);
						} catch (SAXException saxException) {
							LOGGER.error(saxException.getMessage(),
									saxException);
						}

						final StringReader xmlReader = new StringReader(
								dynamicForm.getMetaData());
						Source xmlSource = null;
						try {
							xmlSource = XMLUtil.getSAXSource(xmlReader);
						} catch (SAXException | ParserConfigurationException e1) {
							LOGGER.error(e1.getMessage(), e1);
							throw new PayAsiaSystemException(e1.getMessage(),
									e1);
						}

						try {
							tab = (Tab) unmarshaller.unmarshal(xmlSource);
						} catch (JAXBException jaxbException) {
							LOGGER.error(jaxbException.getMessage(),
									jaxbException);
						}
						tabName = dynamicForm.getTabName();
						dynamicFormObjects.put(dataDictionary.getFormID(), tab);
						dynamicFormSectionName.put(dataDictionary.getFormID(),
								tabName);
					} else {
						tabName = dynamicFormSectionName.get(dataDictionary
								.getFormID());
					}
					List<Field> listOfFields = tab.getField();

					for (Field field : listOfFields) {
						if (!StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.TABLE_FIELD_TYPE)
								&& !StringUtils.equalsIgnoreCase(
										field.getType(),
										PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
								&& !StringUtils.equalsIgnoreCase(
										field.getType(),
										PayAsiaConstants.LABEL_FIELD_TYPE)) {
							if (new String(Base64.decodeBase64(field
									.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								dbTableInformationForm.setDataType(field
										.getType());
								dbTableInformationForm
										.setSectionId(dataDictionary
												.getFormID());
								dbTableInformationForm.setSectionName(tabName);
								SectionInfoDTO sectionInfoDTO = new SectionInfoDTO();
								sectionInfoDTO.setSectionId(dataDictionary
										.getFormID());
								sectionInfoDTO.setSectionName(tabName);
								if (tabName.equalsIgnoreCase(baseSectionName)) {
									baseSectionId = dataDictionary.getFormID();
									sectionList.add(sectionInfoDTO);
								} else {
									sectionList.add(sectionInfoDTO);
								}

							}

						} else if (field.getType().equalsIgnoreCase(
								PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
							if (new String(Base64.decodeBase64(field
									.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								isTableVal = true;
							}

						} else if (field.getType().equals(
								PayAsiaConstants.TABLE_FIELD_TYPE)
								|| field.getType().equals(
										PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
							List<Column> listOfColumns = field.getColumn();
							for (Column column : listOfColumns) {
								if (new String(Base64.decodeBase64(column
										.getDictionaryName().getBytes()))
										.equals(dataDictionary
												.getDataDictName())) {
									isTableVal = true;
								}
							}

							DynamicTableDTO dynamicTableDTO = new DynamicTableDTO();
							dynamicTableDTO.setFormId(dataDictionary
									.getFormID());
							dynamicTableDTO.setTableName(field.getLabel());

							dynamicTableDTO.setTablePosition(PayAsiaStringUtils
									.getColNumber(field.getName()));
							tableNames.add(dynamicTableDTO);

						}
					}

					dbTableInformationForm.setDbFields(dataDictionary
							.getLabel());
					dbTableInformationForm.setDbFields(new String(Base64
							.encodeBase64(dbTableInformationForm.getDbFields()
									.getBytes())));
				}
				dbTableInformationForm.setSelect("<input type='checkBox' />");
				dbTableInformationForm
						.setXlFeild("<input class='txtFLd' type='text' title='Excel Field' />");

				if (multiLingualData != null) {
					try {
						dbTableInformationForm.setDbFields(new String(
								Base64.encodeBase64(StringEscapeUtils
										.escapeXml(multiLingualData.getLabel())
										.getBytes())));
						dbTableInformationForm.setDbFields(URLEncoder.encode(
								dbTableInformationForm.getDbFields(), "UTF-8"));
					} catch (UnsupportedEncodingException unsupportedEncodingException) {
						LOGGER.error(unsupportedEncodingException.getMessage(),
								unsupportedEncodingException);
					}
				}

				if (!isTableVal) {

					dbTablelist.add(dbTableInformationForm);
				}

			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		List<DynamicTableDTO> tableDTOs = new ArrayList<DynamicTableDTO>(
				tableNames);
		List<SectionInfoDTO> sectionInfoList = new ArrayList<>(sectionList);
		Collections.sort(sectionInfoList, new SectionInfoDTOComp());
		excelExportToolForm.setDbTableInformationFormList(dbTablelist);
		excelExportToolForm.setTableNames(tableDTOs);
		excelExportToolForm.setSectionList(sectionInfoList);
		excelExportToolForm.setBasicSectionId(baseSectionId);
		return excelExportToolForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ExcelExportToolLogic#getExistImportTempDef(com.payasia
	 * .common.form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.Long, java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public ExcelExportToolFormResponse getExistImportTempDef(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			String searchCondition, String searchText, Long entityId,
			String scope) {

		ExcelImportExportConditionDTO conditionDTO = new ExcelImportExportConditionDTO();
		if (searchCondition
				.equalsIgnoreCase(PayAsiaConstants.SEARCH_CONDITION_TEMPLATE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setSearchString("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
			}
		} else if (searchCondition
				.equalsIgnoreCase(PayAsiaConstants.SEARCH_CONDITION_CATEGORY)) {
			if (entityId != 0) {
				conditionDTO.setEntityId(entityId);
			}
		} else if (searchCondition
				.equalsIgnoreCase(PayAsiaConstants.SEARCH_CONDITION_SCOPE)) {
			if (!scope.equals("0")) {
				conditionDTO.setScopeSearchString("%" + scope + "%");
			}
		} else if (searchCondition
				.equalsIgnoreCase(PayAsiaConstants.SEARCH_CONDITION_DESCRIPTION)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setSearchString("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
			}
		}

		int recordSize = empDataExportTemplateDAO.getCountForAll(companyId,
				conditionDTO);

		List<EmpDataExportTemplate> empDataExportTemplateList = empDataExportTemplateDAO
				.findByCondition(pageDTO, sortDTO, companyId, conditionDTO);

		List<ExcelExportToolForm> excelExportToolFormList = new ArrayList<ExcelExportToolForm>();

		for (EmpDataExportTemplate empDataExportTemplate : empDataExportTemplateList) {

			ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
			excelExportToolForm.setCategory(empDataExportTemplate
					.getEntityMaster().getEntityName());
			excelExportToolForm.setDescription(empDataExportTemplate
					.getDescription());
			excelExportToolForm.setEntityId(empDataExportTemplate
					.getEntityMaster().getEntityId());
			/*ID ENCRYPT */
			excelExportToolForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(empDataExportTemplate
					.getExportTemplateId()));
			excelExportToolForm.setTemplateName(empDataExportTemplate
					.getTemplateName());
			if (empDataExportTemplate.getScope() != null) {
				if (empDataExportTemplate.getScope().equals(
						PayAsiaConstants.PAYASIA_SCOPE_C)) {
					excelExportToolForm
							.setScope(PayAsiaConstants.PAYASIA_SCOPE_COMPANY);
				} else {
					excelExportToolForm
							.setScope(PayAsiaConstants.PAYASIA_SCOPE_GROUP);

				}
			}

			excelExportToolFormList.add(excelExportToolForm);

		}

		ExcelExportToolFormResponse response = new ExcelExportToolFormResponse();
		response.setExcelExportToolFormList(excelExportToolFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelExportToolLogic#getDataForTemplate(long)
	 */
	@Override
	public ExcelExportToolForm getDataForTemplate(long templateId) {
		EmpDataExportTemplate empDataExportTemplate = empDataExportTemplateDAO
				.findById(templateId);

		ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
		excelExportToolForm.setCategory(empDataExportTemplate.getCategory());
		excelExportToolForm.setDescription(empDataExportTemplate
				.getDescription());
		excelExportToolForm.setEntityId(empDataExportTemplate.getEntityMaster()
				.getEntityId());
		
		/*ID ENCRYPT*/
		excelExportToolForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(empDataExportTemplate.getExportTemplateId()));
		
		excelExportToolForm.setTemplateName(empDataExportTemplate
				.getTemplateName());
		excelExportToolForm.setMultipleSectionEdit(empDataExportTemplate
				.isMultipleSection());
		excelExportToolForm.setScopeEdit(empDataExportTemplate.getScope());
		excelExportToolForm.setPrefix(empDataExportTemplate.getPrefix());
		excelExportToolForm.setSuffix(empDataExportTemplate.getSuffix());
		excelExportToolForm.setPrefixCheck(empDataExportTemplate
				.getIncludePrefix());
		excelExportToolForm.setSuffixCheck(empDataExportTemplate
				.getIncludeSuffix());
		excelExportToolForm.setTemplateNamePrefix(empDataExportTemplate
				.getIncludeTemplateNameAsPrefix());
		excelExportToolForm.setTimeStampSuffix(empDataExportTemplate
				.getIncludeTimestampAsSuffix());
		if (empDataExportTemplate.getFormID() != null) {
			excelExportToolForm.setFormId(empDataExportTemplate.getFormID());
		} else {
			excelExportToolForm.setFormId(0l);
		}

		if (empDataExportTemplate.getCustom_Table_Position() != null) {
			excelExportToolForm.setCustTablePosition(empDataExportTemplate
					.getCustom_Table_Position());
		} else {
			excelExportToolForm.setCustTablePosition(0);
		}

		List<DBTableInformationForm> dbTableInformationFormList = new ArrayList<DBTableInformationForm>();
		List<ExcelExportFiltersForm> excelExportFiltersFormList = new ArrayList<ExcelExportFiltersForm>();

		List<EmpDataExportTemplateField> empDataExportTemplateFieldList = new ArrayList<EmpDataExportTemplateField>(
				empDataExportTemplate.getEmpDataExportTemplateFields());
		Collections.sort(empDataExportTemplateFieldList,
				new EmpDataExportTemplateFieldComp());

		for (EmpDataExportTemplateField empDataExportTemplateField : empDataExportTemplateFieldList) {

			DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
			dbTableInformationForm.setDbId(empDataExportTemplateField
					.getExportFieldId());
			if (empDataExportTemplate.getScope().equals(
					PayAsiaConstants.PAYASIA_SCOPE_C)) {
				dbTableInformationForm
						.setDataDictionaryId(empDataExportTemplateField
								.getDataDictionary().getDataDictionaryId());

				if (empDataExportTemplateField.getDataDictionary()
						.getFieldType()
						.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
					dbTableInformationForm.setSectionId(0l);
				} else {
					dbTableInformationForm
							.setSectionId(empDataExportTemplateField
									.getDataDictionary().getFormID());
				}

			} else {

				dbTableInformationForm
						.setDataDictionaryName(empDataExportTemplateField
								.getDataDictName());
				dbTableInformationForm.setEntityId(empDataExportTemplateField
						.getEntityMaster().getEntityId());
			}

			dbTableInformationForm.setXlFeild(empDataExportTemplateField
					.getExcelFieldName());

			dbTableInformationFormList.add(dbTableInformationForm);

		}

		List<EmpDataExportTemplateFilter> empDataExportTemplateFilterList = new ArrayList<EmpDataExportTemplateFilter>(
				empDataExportTemplate.getEmpDataExportTemplateFilters());
		Collections.sort(empDataExportTemplateFilterList,
				new EmpDataExportTemplateFilterComp());

		for (EmpDataExportTemplateFilter empDataExportTemplateFilter : empDataExportTemplateFilterList) {

			ExcelExportFiltersForm excelExportFiltersForm = new ExcelExportFiltersForm();
			excelExportFiltersForm
					.setEqualityOperator(empDataExportTemplateFilter
							.getEqualityOperator());

			excelExportFiltersForm.setDictionaryId(empDataExportTemplateFilter
					.getDataDictionary().getDataDictionaryId());
			
			/*ID ENCRYPT*/
			excelExportFiltersForm
					.setExportFilterId(FormatPreserveCryptoUtil.encrypt(empDataExportTemplateFilter
							.getExportFilterId()));
			excelExportFiltersForm
					.setLogicalOperator(empDataExportTemplateFilter
							.getLogicalOperator());

			excelExportFiltersForm.setValue(empDataExportTemplateFilter
					.getValue());

			excelExportFiltersForm.setOpenBracket(empDataExportTemplateFilter
					.getOpenBracket());
			excelExportFiltersForm.setCloseBracket(empDataExportTemplateFilter
					.getCloseBracket());
			excelExportFiltersForm.setDataType(generalFilterLogic
					.getFieldDataType(empDataExportTemplate.getCompany()
							.getCompanyId(), empDataExportTemplateFilter
							.getDataDictionary()));
			excelExportFiltersFormList.add(excelExportFiltersForm);
		}

		excelExportToolForm
				.setDbTableInformationFormList(dbTableInformationFormList);
		excelExportToolForm
				.setExcelExportFiltersFormList(excelExportFiltersFormList);

		return excelExportToolForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelExportToolLogic#deleteFilter(long)
	 */
	@Override
	public void deleteFilter(long filterId) {
		EmpDataExportTemplateFilter dataExportTemplateFilter = empDataExportTemplateFilterDAO
				.findById(filterId);
		empDataExportTemplateFilterDAO.delete(dataExportTemplateFilter);

	}

	/**
	 * Comparator Class for Ordering EmpDataExportTemplateFilter List.
	 */
	private class EmpDataExportTemplateFilterComp implements
			Comparator<EmpDataExportTemplateFilter> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(EmpDataExportTemplateFilter templateFilter,
				EmpDataExportTemplateFilter compWithTemplateFilter) {
			if (templateFilter.getExportFilterId() > compWithTemplateFilter
					.getExportFilterId()) {
				return 1;
			} else if (templateFilter.getExportFilterId() < compWithTemplateFilter
					.getExportFilterId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering EmpDataImportTemplateField List.
	 */
	private class EmpDataExportTemplateFieldComp implements
			Comparator<EmpDataExportTemplateField> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(EmpDataExportTemplateField templateField,
				EmpDataExportTemplateField compWithTemplateField) {
			if (templateField.getExportFieldId() < compWithTemplateField
					.getExportFieldId()) {
				return 1;
			} else if (templateField.getExportFieldId() > compWithTemplateField
					.getExportFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering SectionIntoDTOComp List.
	 */
	private class SectionInfoDTOComp implements Comparator<SectionInfoDTO> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(SectionInfoDTO templateFilter,
				SectionInfoDTO compWithTemplateFilter) {
			if (templateFilter.getSectionId() > compWithTemplateFilter
					.getSectionId()) {
				return 1;
			} else if (templateFilter.getSectionId() < compWithTemplateFilter
					.getSectionId()) {
				return -1;
			}
			return 0;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ExcelExportToolLogic#getExistTableMapping(java.lang
	 * .Long, long, long, int, java.lang.Long)
	 */
	@Override
	public ExcelExportToolForm getExistTableMapping(Long companyId,
			long entityId, long formId, int tablePosition, Long languageId) {

		ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
		EntityMaster entityMaster = entityMasterDAO.findById(entityId);
		Set<SectionInfoDTO> sectionList = new HashSet<SectionInfoDTO>();
		String baseSectionName = "";
		long baseSectionId = 0l;

		if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
			baseSectionName = PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME;
		} else if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
			baseSectionName = PayAsiaConstants.COMPANY_BASIC_TAB_NAME;

		} else {
			baseSectionName = PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME;
		}

		HashMap<Long, Tab> dynamicFormObjects = new HashMap<>();
		HashMap<Long, String> dynamicFormSectionName = new HashMap<>();

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionFormId(companyId, entityId, formId);

		List<DBTableInformationForm> dbTablelist = new ArrayList<DBTableInformationForm>();
		try {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				if (!dataDictionary
						.getDataType()
						.equals(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD)
						&& !dataDictionary
								.getDataType()
								.equals(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA)) {
					continue;
				}
				if (!dataDictionary.getFieldType().equalsIgnoreCase(
						PayAsiaConstants.STATIC_TYPE)) {

					Tab tab = dynamicFormObjects
							.get(dataDictionary.getFormID());

					String tabName = null;

					if (tab == null) {
						DynamicForm dynamicForm = dynamicFormDAO
								.findMaxVersionByFormId(companyId, entityId,
										dataDictionary.getFormID());
						Unmarshaller unmarshaller = null;
						try {
							unmarshaller = XMLUtil.getDocumentUnmarshaller();
						} catch (JAXBException jaxbException) {
							LOGGER.error(jaxbException.getMessage(),
									jaxbException);
						} catch (SAXException saxException) {
							LOGGER.error(saxException.getMessage(),
									saxException);
						}

						final StringReader xmlReader = new StringReader(
								dynamicForm.getMetaData());
						Source xmlSource = null;
						try {
							xmlSource = XMLUtil.getSAXSource(xmlReader);
						} catch (SAXException | ParserConfigurationException e1) {
							LOGGER.error(e1.getMessage(), e1);
							throw new PayAsiaSystemException(e1.getMessage(),
									e1);
						}
						try {
							tab = (Tab) unmarshaller.unmarshal(xmlSource);
						} catch (JAXBException jaxbException) {
							LOGGER.error(jaxbException.getMessage(),
									jaxbException);
						}
						tabName = dynamicForm.getTabName();
						dynamicFormObjects.put(dataDictionary.getFormID(), tab);
						dynamicFormSectionName.put(dataDictionary.getFormID(),
								tabName);
					} else {
						tabName = dynamicFormSectionName.get(dataDictionary
								.getFormID());
					}

					List<Field> listOfFields = tab.getField();
					for (Field field : listOfFields) {
						if (field.getType().equals(
								PayAsiaConstants.TABLE_FIELD_TYPE)
								|| field.getType().equals(
										PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
							if (field.getName().equalsIgnoreCase(
									"custfield_" + tablePosition)) {

								List<Column> listOfColumns = field.getColumn();
								for (Column column : listOfColumns) {
									if (new String(Base64.decodeBase64(column
											.getDictionaryName().getBytes()))
											.equals(dataDictionary
													.getDataDictName())) {

										MultiLingualData multiLingualData = multiLingualDataDAO
												.findByDictionaryIdAndLanguage(
														dataDictionary
																.getDataDictionaryId(),
														languageId);

										DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
										dbTableInformationForm
												.setSectionWithTableName(new String(
														Base64.decodeBase64(field
																.getLabel()
																.getBytes())));
										dbTableInformationForm
												.setDataDictionaryId(dataDictionary
														.getDataDictionaryId());
										dbTableInformationForm
												.setSelect("<input type='checkBox' />");
										dbTableInformationForm
												.setXlFeild("<input class='txtFLd' type='text' title='Excel Field' />");
										dbTableInformationForm
												.setDbFields(dataDictionary
														.getLabel());
										dbTableInformationForm
												.setDataType(column.getType());
										dbTableInformationForm
												.setDbFields(dataDictionary
														.getLabel());
										dbTableInformationForm
												.setDbFields(new String(
														Base64.encodeBase64(dbTableInformationForm
																.getDbFields()
																.getBytes())));
										if (multiLingualData != null) {
											try {
												dbTableInformationForm
														.setDbFields(new String(
																Base64.encodeBase64(StringEscapeUtils
																		.escapeXml(
																				multiLingualData
																						.getLabel())
																		.getBytes())));
												dbTableInformationForm
														.setDbFields(URLEncoder
																.encode(dbTableInformationForm
																		.getDbFields(),
																		"UTF-8"));
											} catch (UnsupportedEncodingException unsupportedEncodingException) {
												LOGGER.error(
														unsupportedEncodingException
																.getMessage(),
														unsupportedEncodingException);
											}
										}

										SectionInfoDTO sectionInfoDTO = new SectionInfoDTO();
										sectionInfoDTO
												.setSectionId(dataDictionary
														.getFormID());
										sectionInfoDTO.setSectionName(tabName);
										if (tabName
												.equalsIgnoreCase(baseSectionName)) {
											baseSectionId = dataDictionary
													.getFormID();
											sectionList.add(sectionInfoDTO);
										} else {
											sectionList.add(sectionInfoDTO);
										}
										dbTableInformationForm
												.setSectionId(dataDictionary
														.getFormID());
										dbTableInformationForm
												.setSectionName(tabName);
										dbTablelist.add(dbTableInformationForm);
									}
								}

							}
						}
					}
				}

			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		List<SectionInfoDTO> sectionInfoList = new ArrayList<>(sectionList);
		Collections.sort(sectionInfoList, new SectionInfoDTOComp());
		excelExportToolForm.setSectionList(sectionInfoList);
		excelExportToolForm.setBasicSectionId(baseSectionId);
		excelExportToolForm.setDbTableInformationFormList(dbTablelist);
		return excelExportToolForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ExcelExportToolLogic#getExistMapping(java.lang.Long,
	 * long, java.lang.Long)
	 */
	@Override
	public ExcelExportToolForm getExistMappingForGroup(Long companyId,
			long entityId, Long languageId) {

		ExcelExportToolForm excelExportToolForm = new ExcelExportToolForm();
		Company company = companyDAO.findById(companyId);
		List<String> distinctTabList = dynamicFormDAO.getDistinctTabNames(
				company.getCompanyGroup().getGroupId(), entityId);

		Set<SectionInfoDTO> sectionList = new HashSet<SectionInfoDTO>();
		Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
		List<DBTableInformationForm> dbTablelist = new ArrayList<DBTableInformationForm>();
		EntityMaster entityMaster = entityMasterDAO.findById(entityId);
		String baseSectionName = "";
		long baseSectionId = 0l;

		if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
			baseSectionName = PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME;
		} else if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
			baseSectionName = PayAsiaConstants.COMPANY_BASIC_TAB_NAME;

		} else {
			baseSectionName = PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME;
		}

		String entityName = entityMaster.getEntityName().toLowerCase();
		SectionInfoDTO basicSectionInfoDTO = new SectionInfoDTO();
		basicSectionInfoDTO.setSectionIdGroup(entityName
				+ String.valueOf(baseSectionId));
		basicSectionInfoDTO.setSectionId(baseSectionId);
		basicSectionInfoDTO.setSectionName(baseSectionName);

		sectionList.add(basicSectionInfoDTO);

		List<Object[]> tupleList = new ArrayList<Object[]>();
		/* String tabName = returnTabsStr(distinctTabList); */

		tupleList.addAll(dataDictionaryDAO.findAllTabFields(entityId,
				distinctTabList, company.getCompanyGroup().getGroupId()));

		Long sectionId = 0l;

		Map<String, String> sectionMap = new HashMap<>();

		for (Object[] tuple : tupleList) {
			DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
			if (String.valueOf(tuple[0]).lastIndexOf('.') != -1) {
				dbTableInformationForm.setDataDictionaryNameConst(String
						.valueOf(tuple[0]).substring(0,
								String.valueOf(tuple[0]).lastIndexOf('.')));
			} else {
				dbTableInformationForm.setDataDictionaryNameConst(String
						.valueOf(tuple[0]));
			}
			dbTableInformationForm.setDataDictionaryName(String
					.valueOf(tuple[0]));
			dbTableInformationForm.setDbFields(getFieldName(
					String.valueOf(tuple[0]), String.valueOf(tuple[1])));
			dbTableInformationForm.setDbFields(new String(Base64
					.encodeBase64(dbTableInformationForm.getDbFields()
							.getBytes())));
			dbTableInformationForm.setSelect("<input type='checkBox' />");
			dbTableInformationForm
					.setXlFeild("<input class='txtFLd' type='text' title='Excel Field' />");
			dbTableInformationForm.setFieldCount(String.valueOf(tuple[3]));
			dbTableInformationForm.setLabel(String.valueOf(tuple[1]));
			dbTableInformationForm.setTabName(String.valueOf(tuple[2]));
			dbTableInformationForm.setDataType(String.valueOf(tuple[4]));
			if (String.valueOf(tuple[2]).equals("null")
					|| String.valueOf(tuple[2]) == null
					|| String.valueOf(tuple[2]).equals(baseSectionName)) {

				dbTableInformationForm.setSectionId(baseSectionId);
				dbTableInformationForm.setSectionIdGroup(entityName
						+ String.valueOf(baseSectionId));
				dbTableInformationForm.setSectionName(baseSectionName);
				SectionInfoDTO section = new SectionInfoDTO();
				section.setSectionIdGroup(entityName
						+ String.valueOf(baseSectionId));
				section.setSectionId(baseSectionId);
				section.setSectionName(baseSectionName);
				sectionList.add(section);

			} else if (sectionMap.get(String.valueOf(tuple[2])) == null) {
				sectionId++;
				sectionMap.put(String.valueOf(tuple[2]),
						entityName + String.valueOf(sectionId));
				dbTableInformationForm.setSectionId(sectionId);
				dbTableInformationForm.setSectionIdGroup(entityName
						+ String.valueOf(sectionId));
				dbTableInformationForm
						.setSectionName((String.valueOf(tuple[2])));
				SectionInfoDTO section = new SectionInfoDTO();
				section.setSectionIdGroup(entityName
						+ String.valueOf(sectionId));
				section.setSectionId(sectionId);
				section.setSectionName((String.valueOf(tuple[2])));
				sectionList.add(section);

			} else {

				dbTableInformationForm.setSectionIdGroup(sectionMap.get(String
						.valueOf(tuple[2])));
				dbTableInformationForm
						.setSectionName((String.valueOf(tuple[2])));
				SectionInfoDTO section = new SectionInfoDTO();
				section.setSectionIdGroup(entityName
						+ String.valueOf(sectionId));
				section.setSectionId(sectionId);
				section.setSectionName((String.valueOf(tuple[2])));
				sectionList.add(section);
			}

			dbTablelist.add(dbTableInformationForm);

		}

		List<DynamicTableDTO> tableDTOs = new ArrayList<DynamicTableDTO>(
				tableNames);
		List<SectionInfoDTO> sectionInfoList = new ArrayList<>(sectionList);
		Collections.sort(sectionInfoList, new SectionInfoDTOComp());
		excelExportToolForm.setDbTableInformationFormList(dbTablelist);
		excelExportToolForm.setTableNames(tableDTOs);
		excelExportToolForm.setSectionList(sectionInfoList);
		excelExportToolForm.setBasicSectionId(baseSectionId);
		return excelExportToolForm;
	}

	private String getFieldName(String fieldName, String label) {

		String[] str = fieldName.split("\\.");
		if (str.length == 3) {

			fieldName = fieldName.substring(fieldName.indexOf('.') + 1,
					fieldName.lastIndexOf(".")) + " " + label;
			return fieldName;
		} else if (str.length == 2) {
			return label;
		} else {

			return label;
		}

	}

	private String getFieldNameExcelField(String fieldName) {

		String[] str = fieldName.split("\\.");
		if (str.length == 3) {

			fieldName = fieldName.substring(fieldName.indexOf('.') + 1,
					fieldName.lastIndexOf("."));
			return fieldName;
		} else if (str.length == 2) {
			return fieldName.substring(fieldName.indexOf('.') + 1);
		} else {

			return fieldName;
		}

	}

	@Override
	public List<DataDictionaryDTO> getStaticEmployeeFieldList() {

		List<DataDictionaryDTO> dataDictionaryDTOs = new ArrayList<>();
		EntityMaster employeeEntity = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		List<DataDictionary> dataDictionarieVOs = dataDictionaryDAO
				.findByEntityIdFieldType(employeeEntity.getEntityId(),
						PayAsiaConstants.STATIC_TYPE);
		for (DataDictionary dataDictionary : dataDictionarieVOs) {
			DataDictionaryDTO dataDictionaryDTO = new DataDictionaryDTO();
			dataDictionaryDTO.setDataDictionaryId(dataDictionary
					.getDataDictionaryId());
			dataDictionaryDTO.setDataDictionaryName(dataDictionary
					.getDataDictName());
			if (dataDictionary.getDataDictName().equals(
					PayAsiaConstants.EMPLOYEE_STATIC_HIRE_DATE)
					|| dataDictionary.getDataDictName().equals(
							PayAsiaConstants.EMPLOYEE_STATIC_CONFIRMATION_DATE)
					|| dataDictionary.getDataDictName().equals(
							PayAsiaConstants.EMPLOYEE_STATIC_RESIGNATION_DATE)
					|| dataDictionary
							.getDataDictName()
							.equals(PayAsiaConstants.EMPLOYEE_STATIC_ORIGINAL_HIRE_DATE)
					|| dataDictionary.getDataDictName().equals(
							PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
				dataDictionaryDTO.setDataType("datetime");
			} else {
				ColumnPropertyDTO columnPropertyDTO = generalDAO
						.getColumnProperties(dataDictionary.getTableName(),
								dataDictionary.getColumnName());

				dataDictionaryDTO
						.setDataType(columnPropertyDTO.getColumnType());
			}
			dataDictionaryDTOs.add(dataDictionaryDTO);
		}
		return dataDictionaryDTOs;
	}

	/*
	 * private String returnTabsStr(List<String> tabs) { String tabStr = ""; int
	 * tabLength = tabs.size(); int listCount = 1; for (String str : tabs) { if
	 * (listCount == tabLength) { tabStr += "'" + str + "'"; } else { tabStr +=
	 * "'" + str + "',"; }
	 * 
	 * listCount++; } return tabStr; }
	 */
	@Override
	public boolean isAdminAuthorizedForComTemplate(Long templateId,Long companyId)
	{
		if(templateId == null || companyId == null)
			return false;
		return generalDAO.isTemplateExistForCom(templateId,companyId);
		
		
	}

}
