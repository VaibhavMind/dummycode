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
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.DataField;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.mind.payasia.xml.bean.Template;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.dto.ExcelImportExportConditionDTO;
import com.payasia.common.dto.SectionInfoDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DBTableInformationForm;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.form.ExcelImportToolFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DataXMLUtil;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmpDataImportTemplateDAO;
import com.payasia.dao.EmpDataImportTemplateFieldDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.MultiLingualDataDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.EmpDataImportTemplate;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.MultiLingualData;
import com.payasia.logic.ExcelImportToolLogic;

/**
 * The Class ExcelImportToolLogicImpl.
 */
@Component
public class ExcelImportToolLogicImpl implements ExcelImportToolLogic {

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The general dao. */
	@Resource
	GeneralDAO generalDAO;

	/** The emp data import template dao. */
	@Resource
	EmpDataImportTemplateDAO empDataImportTemplateDAO;

	/** The emp data import template field dao. */
	@Resource
	EmpDataImportTemplateFieldDAO empDataImportTemplateFieldDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The app code master dao. */
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	/** The multi lingual data dao. */
	@Resource
	MultiLingualDataDAO multiLingualDataDAO;
	@Resource
	MessageSource messageSource;

	private static final Logger LOGGER = Logger
			.getLogger(ExcelImportToolLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ExcelImportToolLogic#getExistImportTempDef(com.payasia
	 * .common.form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.Long, java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public ExcelImportToolFormResponse getExistImportTempDef(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			String searchCondition, String searchText, Long entityId) {

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

		int recordSize = empDataImportTemplateDAO.getCountForAll(companyId,
				conditionDTO);

		List<EmpDataImportTemplate> empDataImportTemplateList = empDataImportTemplateDAO
				.findByCondition(pageDTO, sortDTO, companyId, conditionDTO);
		List<ExcelImportToolForm> excelImportToolFormList = new ArrayList<ExcelImportToolForm>();

		for (EmpDataImportTemplate empDataImportTemplate : empDataImportTemplateList) {
			ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
			excelImportToolForm.setCompanyId(empDataImportTemplate.getCompany()
					.getCompanyId());
			excelImportToolForm.setCategory(empDataImportTemplate
					.getEntityMaster().getEntityName());
			excelImportToolForm.setEntityId(empDataImportTemplate
					.getEntityMaster().getEntityId());
			excelImportToolForm.setTemplateDesc(empDataImportTemplate
					.getDescription());
			
			/*ID ENCRYPT */
			excelImportToolForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(empDataImportTemplate.getImportTemplateId()));
		
			excelImportToolForm.setTemplateName(empDataImportTemplate
					.getTemplateName());
			excelImportToolFormList.add(excelImportToolForm);
		}
		ExcelImportToolFormResponse response = new ExcelImportToolFormResponse();
		response.setExcelImportToolFormList(excelImportToolFormList);

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
	 * @see
	 * com.payasia.logic.ExcelImportToolLogic#getExistMapping(java.lang.Long,
	 * long, java.lang.Long)
	 */
	@Override
	public ExcelImportToolForm getExistMapping(Long companyId, long entityId,
			Long languageId) {
		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionImportable(companyId, entityId, "", true);
		Company company = companyDAO.findById(companyId);
		Set<SectionInfoDTO> sectionList = new HashSet<SectionInfoDTO>();
		Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
		List<DBTableInformationForm> dbTablelist = new ArrayList<DBTableInformationForm>();
		HashMap<Long, Tab> dynamicFormObjects = new HashMap<>();
		HashMap<Long, String> dynamicFormSectionName = new HashMap<>();
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

		SectionInfoDTO basicSectionInfoDTO = new SectionInfoDTO();
		basicSectionInfoDTO.setSectionId(baseSectionId);
		basicSectionInfoDTO.setSectionName(baseSectionName);
		sectionList.add(basicSectionInfoDTO);

		for (DataDictionary dataDictionary : dataDictionaryList) {
			boolean isTableVal = false;
			DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();

			MultiLingualData multiLingualData = multiLingualDataDAO
					.findByDictionaryIdAndLanguage(
							dataDictionary.getDataDictionaryId(), languageId);

			dbTableInformationForm.setDataDictionaryId(dataDictionary
					.getDataDictionaryId());
			dbTableInformationForm
					.setSelect("<input class='checkClass' type='checkBox' />");
			dbTableInformationForm
					.setXlFeild("<input class='txtFLd' disabled='disabled' type='text' title='Excel Field' />");
			dbTableInformationForm
					.setDbDefaultValue("<input class='txtFLd' disabled='disabled' title='Default Value' type='text' />");
			dbTableInformationForm
					.setDescription("<input class='txtFLd' disabled='disabled' type='text' title='Descption' />");
			try {

				if (dataDictionary.getFieldType().equals(
						PayAsiaConstants.STATIC_TYPE)) {

					ColumnPropertyDTO columnPropertyDTO = generalDAO
							.getColumnProperties(dataDictionary.getTableName(),
									dataDictionary.getColumnName());
					dbTableInformationForm.setDbFields(dataDictionary
							.getDataDictName());
					dbTableInformationForm.setDbFields(new String(Base64
							.encodeBase64(dbTableInformationForm.getDbFields()
									.getBytes())));
					dbTableInformationForm.setDataType(columnPropertyDTO
							.getColumnType());
					dbTableInformationForm.setLength(columnPropertyDTO
							.getColumnLength());
					dbTableInformationForm.setSectionId(0l);

					int isNullable = columnPropertyDTO.getColumnNullable();

					if (isNullable == 1) {
						dbTableInformationForm
								.setRequired(PayAsiaConstants.PAYASIA_REQUIRED_NO);
					} else {
						dbTableInformationForm
								.setRequired(PayAsiaConstants.PAYASIA_REQUIRED_YES);
					}

				} else {

					dbTableInformationForm.setDbFields(dataDictionary
							.getLabel());
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
							throw new PayAsiaSystemException(
									jaxbException.getMessage(), jaxbException);
						} catch (SAXException saxException) {
							LOGGER.error(saxException.getMessage(),
									saxException);
							throw new PayAsiaSystemException(
									saxException.getMessage(), saxException);
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

						tab = null;
						try {
							tab = (Tab) unmarshaller.unmarshal(xmlSource);
						} catch (JAXBException jaxbException) {
							LOGGER.error(jaxbException.getMessage(),
									jaxbException);
							throw new PayAsiaSystemException(
									jaxbException.getMessage(), jaxbException);
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
										PayAsiaConstants.LABEL_FIELD_TYPE)
								&& !StringUtils
										.equalsIgnoreCase(
												field.getType(),
												PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
								&& !StringUtils.equalsIgnoreCase(
										field.getType(),
										PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {

							if (new String(Base64.decodeBase64(field
									.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {

								if (field.getType().equals(
										PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
									dbTableInformationForm.setPrecision(field
											.getPrecision());
									dbTableInformationForm.setScale(field
											.getScale());
								}
								if (field.getType().equals(
										PayAsiaConstants.FIELD_TYPE_TEXT)) {
									dbTableInformationForm.setMaxLength(field
											.getMaxLength());
									dbTableInformationForm.setMinLength(field
											.getMinLength());
								}

								if (field.isOptional()) {
									dbTableInformationForm
											.setRequired(PayAsiaConstants.PAYASIA_REQUIRED_YES);
								} else {
									dbTableInformationForm
											.setRequired(PayAsiaConstants.PAYASIA_REQUIRED_NO);
								}
								dbTableInformationForm.setDataType(field
										.getType());

								dbTableInformationForm
										.setSectionId(dataDictionary
												.getFormID());

								SectionInfoDTO sectionInfoDTO = new SectionInfoDTO();
								sectionInfoDTO.setSectionId(dataDictionary
										.getFormID());
								sectionInfoDTO.setSectionName(tabName);
								if (tabName.equalsIgnoreCase(baseSectionName)) {
									baseSectionId = dataDictionary.getFormID();
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
				}

			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(),
						exception);
			}

			if (multiLingualData != null) {
				try {
					dbTableInformationForm.setDbFields(new String(Base64
							.encodeBase64(StringEscapeUtils.escapeXml(
									multiLingualData.getLabel()).getBytes())));
					dbTableInformationForm.setDbFields(URLEncoder.encode(
							dbTableInformationForm.getDbFields(), "UTF-8"));

				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
			}

			if (!isTableVal) {
				if (dbTableInformationForm.getDataType().equalsIgnoreCase(
						PayAsiaConstants.FIELD_TYPE_DATE)
						|| dbTableInformationForm
								.getDataType()
								.equalsIgnoreCase(
										PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {
					dbTableInformationForm
							.setSample("<input class='txtFLd' placeholder='"
									+ company.getDateFormat()
									+ "' disabled='disabled' type='text' title='Sample' />");
				} else if (dbTableInformationForm.getDataType()
						.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CHECK)
						|| dbTableInformationForm.getDataType()
								.equalsIgnoreCase(
										PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {
					dbTableInformationForm
							.setSample("<input class='txtFLd' placeholder='TRUE/FALSE' disabled='disabled' type='text' title='Sample' />");
				} else {
					dbTableInformationForm
							.setSample("<input class='txtFLd' disabled='disabled' type='text' title='Sample' />");
				}
				dbTablelist.add(dbTableInformationForm);
			}

		}
		List<DynamicTableDTO> tableDTOs = new ArrayList<DynamicTableDTO>(
				tableNames);
		List<SectionInfoDTO> sectionInfoList = new ArrayList<>(sectionList);
		Collections.sort(sectionInfoList, new SectionInfoDTOComp());
		excelImportToolForm.setDbTableInformationFormList(dbTablelist);
		excelImportToolForm.setTableNames(tableDTOs);
		excelImportToolForm.setSectionList(sectionInfoList);
		excelImportToolForm.setBaseSectionId(baseSectionId);
		return excelImportToolForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelImportToolLogic#saveTemplate(java.lang.Long,
	 * java.lang.String, java.lang.Long)
	 */
	@Override
	public String saveTemplate(Long companyId, String metaData, Long languageId) {

		try {
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = DataXMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
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

			Template template = null;
			try {
				template = (Template) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			}

			String templateName = new String(Base64.decodeBase64(template
					.getTemplateName().getBytes()));
		/*	try {
				templateName = URLDecoder.decode(templateName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}*/
			String templateDesc = new String(Base64.decodeBase64(template
					.getTemplateDesc().getBytes()));
			String transactionType = template.getTransactionType();
			String uploadType = template.getUploadType();
			long entityId = template.getCategory();

			EntityMaster entityMaster = entityMasterDAO.findById(entityId);
			Company company = companyDAO.findById(companyId);

			EmpDataImportTemplate empDataImportTemplate = new EmpDataImportTemplate();
			empDataImportTemplate.setTemplateName(templateName);
			empDataImportTemplate.setDescription(templateDesc);
			empDataImportTemplate.setEntityMaster(entityMaster);
			empDataImportTemplate.setCompany(company);
			empDataImportTemplate.setTransaction_Type(transactionType);
			empDataImportTemplate.setUpload_Type(uploadType);

			if (template.getFormId() != 0) {
				empDataImportTemplate.setFormID(template.getFormId());
			}

			if (template.getCustTableName() != 0) {
				empDataImportTemplate.setCustom_Table_Position(template
						.getCustTableName());
			}

			List<EmpDataImportTemplate> returnedTemplates = empDataImportTemplateDAO
					.findByName(templateName, companyId);

			if (returnedTemplates != null &&  !returnedTemplates.isEmpty()) {
				return "payasia.excel.export.duplicate.template.name";
			}

			EmpDataImportTemplate empDataImportTemplateReturned = empDataImportTemplateDAO
					.save(empDataImportTemplate);

			List<DataField> listOfDataFields = template.getDataField();

			for (DataField dataField : listOfDataFields) {

				if (dataField.isSelect()) {
					DataDictionary dataDictionary = dataDictionaryDAO
							.findById(dataField.getDictionaryId());
					EmpDataImportTemplateField empDataImportTemplateField = new EmpDataImportTemplateField();
					empDataImportTemplateField
							.setEmpDataImportTemplate(empDataImportTemplateReturned);
					empDataImportTemplateField
							.setDataDictionary(dataDictionary);
					empDataImportTemplateField.setDefaultValue(new String(
							Base64.decodeBase64(dataField.getDefaultValue()
									.getBytes())));
					empDataImportTemplateField.setDescription(new String(
							Base64.decodeBase64(dataField.getDescription()
									.getBytes())));

					MultiLingualData multiLingualData = multiLingualDataDAO
							.findByDictionaryIdAndLanguage(
									dataDictionary.getDataDictionaryId(),
									languageId);

					if (StringUtils.isBlank(dataField.getExcelField())) {
						if (dataDictionary.getFieldType().equals(
								PayAsiaConstants.STATIC_TYPE)) {
							empDataImportTemplateField
									.setExcelFieldName(dataDictionary
											.getDataDictName());
						} else {
							empDataImportTemplateField
									.setExcelFieldName(dataDictionary
											.getLabel());
						}

						if (multiLingualData != null) {
							empDataImportTemplateField
									.setExcelFieldName(multiLingualData
											.getLabel());
						}

					} else {
						empDataImportTemplateField
								.setExcelFieldName(new String(Base64
										.decodeBase64(dataField.getExcelField()
												.getBytes())));
					}

					empDataImportTemplateField
							.setSampleData(new String(Base64
									.decodeBase64(dataField.getSampleData()
											.getBytes())));
					empDataImportTemplateFieldDAO
							.save(empDataImportTemplateField);

				}
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			return "payasia.excel.export.error.in.saving.import.template";
		}

		return "payasia.excel.export.record.successfully.inserted";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelImportToolLogic#deleteTemplate(long)
	 */
	@Override
	public String deleteTemplate(Long templateId) {
		EmpDataImportTemplate empDataImportTemplate = empDataImportTemplateDAO
				.findById(templateId);
		if(empDataImportTemplate != null)
			empDataImportTemplateDAO.delete(empDataImportTemplate);
		 	return "Success";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelImportToolLogic#editTemplate(java.lang.Long,
	 * java.lang.String, long, java.lang.Long)
	 */
	@Override
	public String editTemplate(Long companyId, String metaData,
			long templateId, Long languageId) {

		try {
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = DataXMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
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

			Template template = null;
			try {
				template = (Template) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			}
			String templateName = new String(Base64.decodeBase64(template
					.getTemplateName().getBytes()));
			/*try {
				templateName = URLDecoder.decode(templateName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}*/
			String templateDesc = new String(Base64.decodeBase64(template
					.getTemplateDesc().getBytes()));
			String transactionType = template.getTransactionType();
			String uploadType = template.getUploadType();
			long entityId = template.getCategory();

			EntityMaster entityMaster = entityMasterDAO.findById(entityId);
			Company company = companyDAO.findById(companyId);

			EmpDataImportTemplate returnedTemplate = empDataImportTemplateDAO
					.findById(templateId);

			if (!returnedTemplate.getTemplateName().equalsIgnoreCase(
					templateName)) {
				List<EmpDataImportTemplate> returnedTemplates = empDataImportTemplateDAO
						.findByName(templateName, companyId);

				if (returnedTemplates != null &&  !returnedTemplates.isEmpty()) {
					return "payasia.excel.export.duplicate.template.name";
				}

			}

			EmpDataImportTemplate empDataImportTemplate = returnedTemplate;
			empDataImportTemplate.setTemplateName(templateName);
			empDataImportTemplate.setDescription(templateDesc);
			empDataImportTemplate.setEntityMaster(entityMaster);
			empDataImportTemplate.setCompany(company);
			empDataImportTemplate.setTransaction_Type(transactionType);
			empDataImportTemplate.setUpload_Type(uploadType);
			if (template.getFormId() != 0) {
				empDataImportTemplate.setFormID(template.getFormId());
			} else {
				empDataImportTemplate.setFormID(null);
			}

			if (template.getCustTableName() != 0) {
				empDataImportTemplate.setCustom_Table_Position(template
						.getCustTableName());
			} else {
				empDataImportTemplate.setCustom_Table_Position(null);
			}

			empDataImportTemplateDAO.update(empDataImportTemplate);
			empDataImportTemplateFieldDAO.deleteByCondition(templateId);

			List<DataField> listOfDataFields = template.getDataField();
			for (DataField dataField : listOfDataFields) {

				if (dataField.isSelect()) {
					DataDictionary dataDictionary = dataDictionaryDAO
							.findById(dataField.getDictionaryId());
					EmpDataImportTemplateField empDataImportTemplateField = new EmpDataImportTemplateField();
					empDataImportTemplateField
							.setEmpDataImportTemplate(empDataImportTemplate);
					empDataImportTemplateField
							.setDataDictionary(dataDictionary);
					empDataImportTemplateField.setDefaultValue(new String(
							Base64.decodeBase64(dataField.getDefaultValue()
									.getBytes())));
					empDataImportTemplateField.setDescription(new String(
							Base64.decodeBase64(dataField.getDescription()
									.getBytes())));

					MultiLingualData multiLingualData = multiLingualDataDAO
							.findByDictionaryIdAndLanguage(
									dataDictionary.getDataDictionaryId(),
									languageId);

					if (StringUtils.isBlank(dataField.getExcelField())) {
						if (dataDictionary.getFieldType().equals(
								PayAsiaConstants.STATIC_TYPE)) {
							empDataImportTemplateField
									.setExcelFieldName(dataDictionary
											.getDataDictName());
						} else {
							empDataImportTemplateField
									.setExcelFieldName(dataDictionary
											.getLabel());
						}

						if (multiLingualData != null) {
							empDataImportTemplateField
									.setExcelFieldName(multiLingualData
											.getLabel());
						}

					} else {
						empDataImportTemplateField
								.setExcelFieldName(new String(Base64
										.decodeBase64(dataField.getExcelField()
												.getBytes())));
					}
					empDataImportTemplateField
							.setSampleData(new String(Base64
									.decodeBase64(dataField.getSampleData()
											.getBytes())));
					empDataImportTemplateFieldDAO
							.save(empDataImportTemplateField);

				}
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			return "payasia.excel.export.error.in.saving.import.template";
		}

		return "payasia.excel.export.record.successfully.updated";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelImportToolLogic#getEntityList()
	 */
	@Override
	public List<EntityMasterDTO> getEntityList() {
		Locale locale = UserContext.getLocale();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		List<EntityMasterDTO> entityList = new ArrayList<EntityMasterDTO>();

		for (EntityMaster entityMaster : entityMasterList) {
			if (!entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.PAY_SLIP_ENTITY_NAME)
					&& !entityMaster.getEntityName().equalsIgnoreCase(
							PayAsiaConstants.PAY_DATA_COLLECTION_ENTITY_NAME)) {
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
	 * @see com.payasia.logic.ExcelImportToolLogic#getDataForTemplate(long)
	 */
	@Override
	public ExcelImportToolForm getDataForTemplate(long templateId) {

		EmpDataImportTemplate empDataImportTemplate = empDataImportTemplateDAO
				.findById(templateId);
		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
		excelImportToolForm.setCompanyId(empDataImportTemplate.getCompany()
				.getCompanyId());
		excelImportToolForm.setCategory(empDataImportTemplate.getEntityMaster()
				.getEntityName());
		excelImportToolForm.setEntityId(empDataImportTemplate.getEntityMaster()
				.getEntityId());
		excelImportToolForm.setTemplateDesc(empDataImportTemplate
				.getDescription());
		
		/*ID ENCRYPT*/
		excelImportToolForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(empDataImportTemplate.getImportTemplateId()));
		
		excelImportToolForm.setTemplateName(empDataImportTemplate
				.getTemplateName());
		excelImportToolForm.setTemplateId(empDataImportTemplate
				.getImportTemplateId());
		excelImportToolForm.setTransactionType(empDataImportTemplate
				.getTransaction_Type());
		excelImportToolForm.setUploadType(empDataImportTemplate
				.getUpload_Type());
		if (empDataImportTemplate.getFormID() != null) {
			excelImportToolForm.setFormId(empDataImportTemplate.getFormID());
		} else {
			excelImportToolForm.setFormId(0l);
		}

		if (empDataImportTemplate.getCustom_Table_Position() != null) {
			excelImportToolForm.setCustTablePosition(empDataImportTemplate
					.getCustom_Table_Position());
		} else {
			excelImportToolForm.setCustTablePosition(0);
		}

		List<DBTableInformationForm> dbTableInformationFormList = new ArrayList<DBTableInformationForm>();
		List<EmpDataImportTemplateField> empDataImportTemplateFieldList = new ArrayList<EmpDataImportTemplateField>(
				empDataImportTemplate.getEmpDataImportTemplateFields());
		Collections.sort(empDataImportTemplateFieldList,
				new EmpDataImportTemplateFieldComp());
		for (EmpDataImportTemplateField empDataImportTemplateField : empDataImportTemplateFieldList) {

			DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
			dbTableInformationForm.setDbId(empDataImportTemplateField
					.getImportFieldId());
			dbTableInformationForm
					.setDataDictionaryId(empDataImportTemplateField
							.getDataDictionary().getDataDictionaryId());
			dbTableInformationForm.setXlFeild(empDataImportTemplateField
					.getExcelFieldName());
			dbTableInformationForm.setDescription(empDataImportTemplateField
					.getDescription());
			dbTableInformationForm.setDbDefaultValue(empDataImportTemplateField
					.getDefaultValue());
			dbTableInformationForm.setSample(empDataImportTemplateField
					.getSampleData());

			if (empDataImportTemplateField.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
				dbTableInformationForm.setSectionId(0l);
			} else {
				dbTableInformationForm.setSectionId(empDataImportTemplateField
						.getDataDictionary().getFormID());
			}
			dbTableInformationFormList.add(dbTableInformationForm);

		}

		excelImportToolForm
				.setDbTableInformationFormList(dbTableInformationFormList);

		return excelImportToolForm;
	}

	/**
	 * Purpose: Gets the data for template in reverse order for edit view.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the data for template reverse
	 */
	public ExcelImportToolForm getDataForTemplateReverse(long templateId) {

		EmpDataImportTemplate empDataImportTemplate = empDataImportTemplateDAO
				.findById(templateId);
		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
		excelImportToolForm.setCompanyId(empDataImportTemplate.getCompany()
				.getCompanyId());
		excelImportToolForm.setCategory(empDataImportTemplate.getEntityMaster()
				.getEntityName());
		excelImportToolForm.setEntityId(empDataImportTemplate.getEntityMaster()
				.getEntityId());
		excelImportToolForm.setTemplateDesc(empDataImportTemplate
				.getDescription());
		excelImportToolForm.setTemplateId(empDataImportTemplate
				.getImportTemplateId());
		excelImportToolForm.setTemplateName(empDataImportTemplate
				.getTemplateName());
		excelImportToolForm.setTemplateId(empDataImportTemplate
				.getImportTemplateId());
		excelImportToolForm.setTransactionType(empDataImportTemplate
				.getTransaction_Type());
		excelImportToolForm.setUploadType(empDataImportTemplate
				.getUpload_Type());
		if (empDataImportTemplate.getFormID() != null) {
			excelImportToolForm.setFormId(empDataImportTemplate.getFormID());
		} else {
			excelImportToolForm.setFormId(0l);
		}

		if (empDataImportTemplate.getCustom_Table_Position() != null) {
			excelImportToolForm.setCustTablePosition(empDataImportTemplate
					.getCustom_Table_Position());
		} else {
			excelImportToolForm.setCustTablePosition(0);
		}

		List<DBTableInformationForm> dbTableInformationFormList = new ArrayList<DBTableInformationForm>();
		List<EmpDataImportTemplateField> empDataImportTemplateFieldList = new ArrayList<EmpDataImportTemplateField>(
				empDataImportTemplate.getEmpDataImportTemplateFields());
		Collections.sort(empDataImportTemplateFieldList,
				new EmpDataImportTemplateFieldCompReverse());
		for (EmpDataImportTemplateField empDataImportTemplateField : empDataImportTemplateFieldList) {

			DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
			dbTableInformationForm.setDbId(empDataImportTemplateField
					.getImportFieldId());
			dbTableInformationForm
					.setDataDictionaryId(empDataImportTemplateField
							.getDataDictionary().getDataDictionaryId());
			dbTableInformationForm.setXlFeild(empDataImportTemplateField
					.getExcelFieldName());
			dbTableInformationForm.setDescription(empDataImportTemplateField
					.getDescription());
			dbTableInformationForm.setDbDefaultValue(empDataImportTemplateField
					.getDefaultValue());
			dbTableInformationForm.setSample(empDataImportTemplateField
					.getSampleData());

			if (empDataImportTemplateField.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
				dbTableInformationForm.setSectionId(0l);
			} else {
				dbTableInformationForm.setSectionId(empDataImportTemplateField
						.getDataDictionary().getFormID());
			}
			dbTableInformationFormList.add(dbTableInformationForm);

		}

		excelImportToolForm
				.setDbTableInformationFormList(dbTableInformationFormList);

		return excelImportToolForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ExcelImportToolLogic#getAppCodeList(java.lang.String)
	 */
	@Override
	public List<AppCodeDTO> getAppCodeList(String category) {

		List<AppCodeMaster> appCodeMasters = appCodeMasterDAO
				.findByCondition(category);
		List<AppCodeDTO> appCodeList = new ArrayList<AppCodeDTO>();
		for (AppCodeMaster appCodeMaster : appCodeMasters) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setAppCodeID(appCodeMaster.getAppCodeID());
			appCodeDTO.setCategory(appCodeMaster.getCategory());
			appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			appCodeDTO.setCodeValue(appCodeMaster.getCodeValue());
			appCodeList.add(appCodeDTO);

		}
		return appCodeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.ExcelImportToolLogic#generateExcel(long)
	 */
	@Override
	public ExcelImportToolForm generateExcel(long templateId) {
		ExcelImportToolForm excelImportToolForm = getDataForTemplateReverse(templateId);

		List<DBTableInformationForm> dbTableInformationFormList = excelImportToolForm
				.getDbTableInformationFormList();

		Workbook wb = new HSSFWorkbook();

		Sheet sheet = wb.createSheet("Sheet1");

		CellStyle headerCellStyleForNonRequiredField = wb.createCellStyle();
		CellStyle headerCellStyleForRequiredField = wb.createCellStyle();
		setcellHeaderStyle(headerCellStyleForNonRequiredField);
		setcellHeaderStyle(headerCellStyleForRequiredField);

		Font headerFontForNonRequiredField = wb.createFont();
		headerFontForNonRequiredField.setFontName(HSSFFont.FONT_ARIAL);
		headerFontForNonRequiredField.setFontHeightInPoints((short) 10);
		headerFontForNonRequiredField.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFontForNonRequiredField.setColor(HSSFColor.BLACK.index);
		headerCellStyleForNonRequiredField
				.setFont(headerFontForNonRequiredField);

		Font headerFontForRequiredField = wb.createFont();
		headerFontForRequiredField.setFontName(HSSFFont.FONT_ARIAL);
		headerFontForRequiredField.setFontHeightInPoints((short) 10);
		headerFontForRequiredField.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFontForRequiredField.setColor(HSSFColor.RED.index);
		headerCellStyleForRequiredField.setFont(headerFontForRequiredField);

		CellStyle sampleDataCellStyle = wb.createCellStyle();
		sampleDataCellStyle.setAlignment(CellStyle.ALIGN_CENTER);

		Row headerRow = sheet.createRow(0);
		Row sampleDataRow = sheet.createRow(1);
		int columnCount = 0;
		for (DBTableInformationForm dBTableInformationForm : dbTableInformationFormList) {

			Cell headerCell = headerRow.createCell(columnCount);
			Cell sampleDataCell = sampleDataRow.createCell(columnCount);
			sampleDataCell.setCellValue(dBTableInformationForm.getSample());
			headerCell.setCellValue(dBTableInformationForm.getXlFeild());

			 
			StringBuilder comment = new StringBuilder();
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(dBTableInformationForm.getDataDictionaryId());

			try {

				if (dataDictionary.getFieldType().equals(
						PayAsiaConstants.STATIC_TYPE)) {

					ColumnPropertyDTO columnPropertyDTO = generalDAO
							.getColumnProperties(dataDictionary.getTableName(),
									dataDictionary.getColumnName());
					if (StringUtils.isNotEmpty(columnPropertyDTO
							.getColumnType())) {
						comment.append("Data Type: "
								+ columnPropertyDTO.getColumnType() + "\n");
					}

					comment.append("Length: "
							+ columnPropertyDTO.getColumnLength() + "\n");

					int isNullable = columnPropertyDTO.getColumnNullable();

					if (isNullable == 1) {
						comment.append("Required : No" + "\n");
						headerCell
								.setCellStyle(headerCellStyleForNonRequiredField);

					} else {
						comment.append("Required : Yes" + "\n");
						headerCell
								.setCellStyle(headerCellStyleForRequiredField);
					}

				} else {

					DynamicForm dynamicForm = dynamicFormDAO
							.findMaxVersionByFormId(
									excelImportToolForm.getCompanyId(),
									excelImportToolForm.getEntityId(),
									dataDictionary.getFormID());

					Unmarshaller unmarshaller = null;
					try {
						unmarshaller = XMLUtil.getDocumentUnmarshaller();
					} catch (JAXBException jaxbException) {
						LOGGER.error(jaxbException.getMessage(), jaxbException);
						throw new PayAsiaSystemException(
								jaxbException.getMessage(), jaxbException);
					} catch (SAXException saxException) {
						LOGGER.error(saxException.getMessage(), saxException);
						throw new PayAsiaSystemException(
								saxException.getMessage(), saxException);
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

					Tab tab = null;
					try {
						tab = (Tab) unmarshaller.unmarshal(xmlSource);
					} catch (JAXBException jaxbException) {
						LOGGER.error(jaxbException.getMessage(), jaxbException);
						throw new PayAsiaSystemException(
								jaxbException.getMessage(), jaxbException);
					}

					List<Field> listOfFields = tab.getField();

					for (Field field : listOfFields) {
						if (!StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.TABLE_FIELD_TYPE)
								&& !StringUtils.equalsIgnoreCase(
										field.getType(),
										PayAsiaConstants.LABEL_FIELD_TYPE)) {

							if (new String(Base64.decodeBase64(field
									.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {

								if (field.getType().equals(
										PayAsiaConstants.FIELD_TYPE_NUMERIC)) {

									comment.append("Data Type: "
											+ field.getType() + "\n");
									if (field.getPrecision() != null) {
										comment.append("Precision : "
												+ field.getPrecision() + "\n");
									}
									if (field.getScale() != null) {
										comment.append("Scale : "
												+ field.getScale() + "\n");
									}
								}
								if (field.getType().equals(
										PayAsiaConstants.FIELD_TYPE_TEXT)) {
									comment.append("Data Type: "
											+ field.getType() + "\n");
									if (field.getMaxLength() != null) {
										comment.append("Max Length : "
												+ field.getMaxLength() + "\n");
									}
									if (field.getMinLength() != null) {
										comment.append("Min Length : "
												+ field.getMinLength() + "\n");
									}
								}

								if (field.isOptional()) {
									comment.append("Required : Yes" + "\n");
									headerCell
											.setCellStyle(headerCellStyleForRequiredField);
								} else {
									comment.append("Required : No" + "\n");
									headerCell
											.setCellStyle(headerCellStyleForNonRequiredField);
								}

							}
						}
						if (field.getType().equals(
								PayAsiaConstants.TABLE_FIELD_TYPE)) {
							List<Column> listOfColumns = field.getColumn();
							for (Column column : listOfColumns) {
								if (new String(Base64.decodeBase64(column
										.getDictionaryName().getBytes()))
										.equals(dataDictionary
												.getDataDictName())) {

									if (column
											.getType()
											.equals(PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
										comment.append("Data Type: "
												+ column.getType() + "\n");
										if (field.getPrecision() != null) {
											comment.append("Precision : "
													+ field.getPrecision()
													+ "\n");
										}
										if (field.getScale() != null) {
											comment.append("Scale : "
													+ field.getScale() + "\n");
										}
									}
									if (column.getType().equals(
											PayAsiaConstants.FIELD_TYPE_TEXT)) {
										comment.append("Data Type: "
												+ column.getType() + "\n");
										if (field.getMaxLength() != null) {
											comment.append("Max Length : "
													+ field.getMaxLength()
													+ "\n");
										}
										if (field.getMinLength() != null) {
											comment.append("Min Length : "
													+ field.getMinLength()
													+ "\n");
										}
									}

									if (column.isOptional()) {
										comment.append("Required : Yes" + "\n");
										headerCell
												.setCellStyle(headerCellStyleForRequiredField);
									} else {
										comment.append("Required : No" + "\n");
										headerCell
												.setCellStyle(headerCellStyleForNonRequiredField);
									}

								}
							}
						}

					}
				}

			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(),
						exception);
			}

			setCellComment(headerCell, comment.toString());
			sampleDataCell.setCellStyle(sampleDataCellStyle);
			sheet.autoSizeColumn(columnCount);
			columnCount++;

		}

		EmpDataImportTemplate empDataImportTemplateVO = empDataImportTemplateDAO
				.findById(templateId);
		ExcelImportToolForm importToolForm = new ExcelImportToolForm();
		importToolForm.setWorkbook(wb);
		importToolForm.setTemplateName(empDataImportTemplateVO
				.getTemplateName());

		return importToolForm;

	}

	/**
	 * Purpose : Sets the cell comment.
	 * 
	 * @param cell
	 *            the cell
	 * @param message
	 *            the message
	 */
	public void setCellComment(Cell cell, String message) {
		Drawing drawing = cell.getSheet().createDrawingPatriarch();
		CreationHelper factory = cell.getSheet().getWorkbook()
				.getCreationHelper();
		ClientAnchor anchor = factory.createClientAnchor();
		anchor.setCol1(cell.getColumnIndex());
		anchor.setCol2(cell.getColumnIndex() + 4);
		anchor.setRow1(cell.getRowIndex());
		anchor.setRow2(cell.getRowIndex() + 5);
		anchor.setDx1(200);
		anchor.setDx2(200);
		anchor.setDy1(200);
		anchor.setDy2(200);

		Comment comment = drawing.createCellComment(anchor);
		RichTextString str = factory.createRichTextString(message);
		comment.setString(str);
		cell.setCellComment(comment);
	}

	/**
	 * Purpose : Set the Header Style of the Excel Sheet.
	 * 
	 * @param styleHeader
	 *            the new cell header style
	 */
	public void setcellHeaderStyle(CellStyle styleHeader) {
		 
		styleHeader.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		styleHeader.setAlignment(CellStyle.ALIGN_CENTER);
		styleHeader.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styleHeader.setBorderBottom(CellStyle.BORDER_THIN);
		styleHeader.setBottomBorderColor(HSSFColor.WHITE.index);
		styleHeader.setBorderRight(CellStyle.BORDER_THIN);
		styleHeader.setRightBorderColor(HSSFColor.WHITE.index);
		styleHeader.setHidden(false);
	}

	/**
	 * Comparator Class for Ordering EmpDataImportTemplateField List.
	 */
	private class EmpDataImportTemplateFieldComp implements
			Comparator<EmpDataImportTemplateField> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(EmpDataImportTemplateField templateField,
				EmpDataImportTemplateField compWithTemplateField) {
			if (templateField.getImportFieldId() < compWithTemplateField
					.getImportFieldId()) {
				return 1;
			} else if (templateField.getImportFieldId() > compWithTemplateField
					.getImportFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering EmpDataImportTemplateField List.
	 */
	private class EmpDataImportTemplateFieldCompReverse implements
			Comparator<EmpDataImportTemplateField> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(EmpDataImportTemplateField templateField,
				EmpDataImportTemplateField compWithTemplateField) {
			if (templateField.getImportFieldId() > compWithTemplateField
					.getImportFieldId()) {
				return 1;
			} else if (templateField.getImportFieldId() < compWithTemplateField
					.getImportFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.ExcelImportToolLogic#getExistTableMapping(java.lang
	 * .Long, long, long, int, java.lang.Long)
	 */
	@Override
	public ExcelImportToolForm getExistTableMapping(Long companyId,
			long entityId, long formId, int tablePosition, Long languageId) {

		ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
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
		SectionInfoDTO basicSectionInfoDTO = new SectionInfoDTO();
		basicSectionInfoDTO.setSectionId(baseSectionId);
		basicSectionInfoDTO.setSectionName(baseSectionName);
		sectionList.add(basicSectionInfoDTO);
		Company company = companyDAO.findById(companyId);
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionFormId(companyId, entityId, formId);

		List<DBTableInformationForm> dbTablelist = new ArrayList<DBTableInformationForm>();
		try {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				if (!dataDictionary
						.getDataType()
						.equals(PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD)) {
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
							throw new PayAsiaSystemException(
									jaxbException.getMessage(), jaxbException);
						} catch (SAXException saxException) {
							LOGGER.error(saxException.getMessage(),
									saxException);
							throw new PayAsiaSystemException(
									saxException.getMessage(), saxException);
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
							throw new PayAsiaSystemException(
									jaxbException.getMessage(), jaxbException);
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
										DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
										MultiLingualData multiLingualData = multiLingualDataDAO
												.findByDictionaryIdAndLanguage(
														dataDictionary
																.getDataDictionaryId(),
														languageId);
										dbTableInformationForm
												.setDataDictionaryId(dataDictionary
														.getDataDictionaryId());
										dbTableInformationForm
												.setSelect("<input class='checkClass' type='checkBox' />");
										dbTableInformationForm
												.setXlFeild("<input class='txtFLd' disabled='disabled' type='text' title='Excel Field' />");
										dbTableInformationForm
												.setDbDefaultValue("<input class='txtFLd' disabled='disabled' title='Default Value' type='text' />");
										dbTableInformationForm
												.setDescription("<input class='txtFLd' disabled='disabled' type='text' title='Descption' />");
										dbTableInformationForm
												.setSample("<input class='txtFLd' disabled='disabled' type='text' title='Sample' />");
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
												throw new PayAsiaSystemException(
														unsupportedEncodingException
																.getMessage(),
														unsupportedEncodingException);
											}
										}

										if (column
												.getType()
												.equals(PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
											dbTableInformationForm
													.setPrecision(column
															.getPrecision());
											dbTableInformationForm
													.setScale(column.getScale());
										}
										if (column
												.getType()
												.equals(PayAsiaConstants.FIELD_TYPE_TEXT)) {
											dbTableInformationForm
													.setMaxLength(column
															.getMaxLength());
											dbTableInformationForm
													.setMinLength(column
															.getMinLength());
										}

										if (column
												.getType()
												.equals(PayAsiaConstants.FIELD_TYPE_DATE)) {
											dbTableInformationForm
													.setSample("<input class='txtFLd' placeholder='"
															+ company
																	.getDateFormat()
															+ "' disabled='disabled' type='text' title='Sample' />");
										}

										if (column
												.getType()
												.equals(PayAsiaConstants.FIELD_TYPE_CHECK)) {
											dbTableInformationForm
													.setSample("<input class='txtFLd' placeholder='TRUE/FALSE' disabled='disabled' type='text' title='Sample' />");
										}

										if (column.isOptional()) {
											dbTableInformationForm
													.setRequired(PayAsiaConstants.PAYASIA_REQUIRED_YES);
										} else {
											dbTableInformationForm
													.setRequired(PayAsiaConstants.PAYASIA_REQUIRED_NO);
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
										} else {
											sectionList.add(sectionInfoDTO);
										}
										dbTableInformationForm
												.setSectionId(dataDictionary
														.getFormID());
										dbTableInformationForm
												.setDataType(column.getType());
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
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
		List<SectionInfoDTO> sectionInfoList = new ArrayList<>(sectionList);
		Collections.sort(sectionInfoList, new SectionInfoDTOComp());
		excelImportToolForm.setSectionList(sectionInfoList);
		excelImportToolForm.setBaseSectionId(baseSectionId);
		excelImportToolForm.setDbTableInformationFormList(dbTablelist);
		return excelImportToolForm;
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
	
	@Override
	public ExcelImportToolFormResponse getExistImportTempDef(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			String searchCondition, String searchText) {

		ExcelImportExportConditionDTO conditionDTO = new ExcelImportExportConditionDTO();
		if (searchCondition.equalsIgnoreCase(PayAsiaConstants.SEARCH_CONDITION_TEMPLATE_NAME)) {
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
			if (Long.parseLong(searchText) != 0) {
				conditionDTO.setEntityId(Long.parseLong(searchText));
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

		int recordSize = empDataImportTemplateDAO.getCountForAll(companyId,
				conditionDTO);

		List<EmpDataImportTemplate> empDataImportTemplateList = empDataImportTemplateDAO
				.findByCondition(pageDTO, sortDTO, companyId, conditionDTO);
		List<ExcelImportToolForm> excelImportToolFormList = new ArrayList<ExcelImportToolForm>();

		for (EmpDataImportTemplate empDataImportTemplate : empDataImportTemplateList) {
			ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
			excelImportToolForm.setCompanyId(empDataImportTemplate.getCompany()
					.getCompanyId());
			excelImportToolForm.setCategory(empDataImportTemplate
					.getEntityMaster().getEntityName());
			excelImportToolForm.setEntityId(empDataImportTemplate
					.getEntityMaster().getEntityId());
			excelImportToolForm.setTemplateDesc(empDataImportTemplate
					.getDescription());
			
			/*ID ENCRYPT */
			excelImportToolForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(empDataImportTemplate.getImportTemplateId()));
		
			excelImportToolForm.setTemplateName(empDataImportTemplate
					.getTemplateName());
			excelImportToolFormList.add(excelImportToolForm);
		}
		ExcelImportToolFormResponse response = new ExcelImportToolFormResponse();
		response.setExcelImportToolFormList(excelImportToolFormList);

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
}
