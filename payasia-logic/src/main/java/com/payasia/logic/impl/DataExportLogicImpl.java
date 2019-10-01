/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmployeeCompanyTupleDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.ExcelImportExportConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DBTableInformationForm;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.DataExportFormResponse;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.FormulaUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmpDataExportTemplateDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.MultiLingualDataDAO;
import com.payasia.dao.PayDataCollectionDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.EmpDataExportTemplate;
import com.payasia.dao.bean.EmpDataExportTemplateField;
import com.payasia.dao.bean.EmpDataExportTemplateFilter;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.MultiLingualData;
import com.payasia.logic.DataExportLogic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;

/**
 * The Class DataExportLogicImpl.
 */
@Component
public class DataExportLogicImpl implements DataExportLogic {

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The payslip dao. */
	@Resource
	PayslipDAO payslipDAO;

	/** The pay data collection dao. */
	@Resource
	PayDataCollectionDAO payDataCollectionDAO;

	/** The emp data export template dao. */
	@Resource
	EmpDataExportTemplateDAO empDataExportTemplateDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The general dao. */
	@Resource
	GeneralDAO generalDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The dynamic form record dao. */
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	/** The dynamic form table record dao. */
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The data export utils. */
	@Resource
	DataExportUtils dataExportUtils;

	/** The multi lingual data dao. */
	@Resource
	MultiLingualDataDAO multiLingualDataDAO;

	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	GeneralLogic generalLogic;

	private static final Logger LOGGER = Logger
			.getLogger(DataExportLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.DataExportLogic#getExportTemplates(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition, java.lang.Long,
	 * java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public DataExportFormResponse getExportTemplates(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, String searchCondition,
			String searchText, Long entityId, String scope) {

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
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
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
				conditionDTO.setScopeSearchString(scope);
			}
		}

		else if (searchCondition
				.equalsIgnoreCase(PayAsiaConstants.SEARCH_CONDITION_DESCRIPTION)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setSearchString("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
			}
		}

		int recordSize = (empDataExportTemplateDAO.getCountForDataExport(
				companyId, conditionDTO)).intValue();

		List<EmpDataExportTemplate> empDataExportTemplateList = empDataExportTemplateDAO
				.findByCondition(pageDTO, sortDTO, companyId, conditionDTO);

		List<DataExportForm> dataExportFormList = new ArrayList<DataExportForm>();

		for (EmpDataExportTemplate empDataExportTemplate : empDataExportTemplateList) {

			DataExportForm dataExportForm = new DataExportForm();
			dataExportForm.setCategory(empDataExportTemplate.getEntityMaster()
					.getEntityName());
			dataExportForm.setDescription(empDataExportTemplate
					.getDescription());
			dataExportForm.setEntityId(empDataExportTemplate.getEntityMaster()
					.getEntityId());
			/*ID ENCRYPT */
			dataExportForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(empDataExportTemplate
					.getExportTemplateId()));
			dataExportForm.setTemplateName(empDataExportTemplate
					.getTemplateName());

			if (empDataExportTemplate.getScope() != null) {
				if (empDataExportTemplate.getScope().equals(
						PayAsiaConstants.PAYASIA_SCOPE_C)) {
					dataExportForm
							.setScope(PayAsiaConstants.PAYASIA_SCOPE_COMPANY);
				} else {
					dataExportForm
							.setScope(PayAsiaConstants.PAYASIA_SCOPE_GROUP);

				}
			}

			dataExportFormList.add(dataExportForm);

		}
		DataExportFormResponse response = new DataExportFormResponse();
		response.setDataExportFormList(dataExportFormList);

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

	private Tab getTabObject(String xml) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		} catch (SAXException sAXException) {
			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(),
					sAXException);
		}
		final StringReader xmlReader = new StringReader(xml);
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
			throw new PayAsiaSystemException(jaxbException.getMessage(),
					jaxbException);
		}
		return tab;
	}

	private void insertFormulaFieldValueToEmpRecord(
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<Long, Tab> dataTabMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap,
			Long fieldDataDictionaryId, Long companyId,
			Set<BigInteger> shortListEmpIds) {
		DataDictionary dataDictionary = dataDictionaryDAO
				.findById(fieldDataDictionaryId);
		DynamicForm dynamicForm = null;
		if (dynamicFormMap.containsKey(dataDictionary.getFormID())) {
			dynamicForm = dynamicFormMap.get(dataDictionary.getFormID());
		} else {
			dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
					dataDictionary.getEntityMaster().getEntityId(),
					dataDictionary.getFormID());
			dynamicFormMap.put(dynamicForm.getId().getFormId(), dynamicForm);
		}
		if (dynamicForm != null) {
			try {
				for (BigInteger empId : shortListEmpIds) {
					addFormulaValueInExistingDynXML(dynamicFormMap, dataTabMap,
							staticPropMap, dynamicForm.getMetaData(),
							empId.longValue(), companyId, dataDictionary,
							dynamicForm.getId().getFormId(), dynamicForm
									.getId().getVersion(), dynamicForm.getId()
									.getEntity_ID());
				}
			} catch (XMLStreamException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}
	}

	private void addFormulaValueInExistingDynXML(
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<Long, Tab> dataTabMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap, String XML,
			Long employeeId, Long companyId,
			DataDictionary fieldDataDictionary, Long formId, int version,
			Long entityId) throws XMLStreamException {

		DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
				.getEmpRecords(employeeId, version, formId, entityId, companyId);
		if (dynamicFormRecord != null) {
			Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

			Tab tab = dataTabMap.get(fieldDataDictionary.getFormID());
			if (tab == null) {
				tab = getTabObject(XML);
				dataTabMap.put(fieldDataDictionary.getFormID(), tab);
			}

			String calculatedValue = "";
			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {
				String fieldName = field.getName();
				if (field.getType().equalsIgnoreCase(
						PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
					if (fieldDataDictionary.getDataDictionaryId() == field
							.getDictionaryId()) {
						try {
							try {
								calculatedValue = getFormulaFieldCalculatedValue(
										dynamicFormMap, dataTabMap,
										staticPropMap, field.getFormula(),
										field.getFormulaType(),
										fieldDataDictionary, employeeId,
										companyId, null, 0);
								String methodName = PayAsiaConstants.SET_COL
										+ fieldName.substring(
												fieldName.lastIndexOf('_') + 1,
												fieldName.length());
								Method dynamicFormRecordMethod = dynamicFormRecordClass
										.getMethod(methodName, String.class);

								if (StringUtils.isNotBlank(calculatedValue)) {
									dynamicFormRecordMethod.invoke(
											dynamicFormRecord, calculatedValue);
								}

							} catch (IllegalArgumentException illegalArgumentException) {
								LOGGER.error(
										illegalArgumentException.getMessage(),
										illegalArgumentException);
								throw new PayAsiaSystemException(
										illegalArgumentException.getMessage(),
										illegalArgumentException);
							} catch (IllegalAccessException illegalAccessException) {
								LOGGER.error(
										illegalAccessException.getMessage(),
										illegalAccessException);
								throw new PayAsiaSystemException(
										illegalAccessException.getMessage(),
										illegalAccessException);
							} catch (InvocationTargetException invocationTargetException) {
								LOGGER.error(
										invocationTargetException.getMessage(),
										invocationTargetException);
								throw new PayAsiaSystemException(
										invocationTargetException.getMessage(),
										invocationTargetException);
							}
						} catch (SecurityException securityException) {
							LOGGER.error(securityException.getMessage(),
									securityException);
							throw new PayAsiaSystemException(
									securityException.getMessage(),
									securityException);
						} catch (NoSuchMethodException noSuchMethodException) {

							LOGGER.error(noSuchMethodException.getMessage(),
									noSuchMethodException);
							throw new PayAsiaSystemException(
									noSuchMethodException.getMessage(),
									noSuchMethodException);
						}
						dynamicFormRecordDAO.update(dynamicFormRecord);
					}
				}

				if (field.getType().equalsIgnoreCase(
						PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> columnList = field.getColumn();
					for (Column column : columnList) {
						String colName = column.getName();
						if (column.getType().equalsIgnoreCase(
								PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
							if (fieldDataDictionary.getDataDictionaryId() == column
									.getDictionaryId()) {
								try {

									String getMethodName = "getCol"
											+ fieldName
													.substring(
															fieldName
																	.lastIndexOf('_') + 1,
															fieldName.length());
									String tableValue;
									try {
										Method dynamicFormRecordMethod = dynamicFormRecordClass
												.getMethod(getMethodName);
										try {
											tableValue = (String) dynamicFormRecordMethod
													.invoke(dynamicFormRecord);
										} catch (IllegalAccessException e) {
											LOGGER.error(e.getMessage(), e);
											throw new PayAsiaSystemException(
													e.getMessage(), e);
										} catch (IllegalArgumentException e) {
											LOGGER.error(e.getMessage(), e);
											throw new PayAsiaSystemException(
													e.getMessage(), e);
										} catch (InvocationTargetException e) {
											LOGGER.error(e.getMessage(), e);
											throw new PayAsiaSystemException(
													e.getMessage(), e);
										}
									} catch (NoSuchMethodException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									} catch (SecurityException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									}
									if (StringUtils.isNotBlank(tableValue)) {
										List<DynamicFormTableRecord> dynamicFormTableRecordList = dynamicFormTableRecordDAO
												.getTableRecords(Long
														.parseLong(tableValue),
														"", "");
										for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecordList) {
											calculatedValue = getFormulaFieldCalculatedValue(
													dynamicFormMap,
													dataTabMap,
													staticPropMap,
													column.getFormula(),
													column.getFormulaType(),
													fieldDataDictionary,
													employeeId,
													companyId,
													dynamicFormTableRecord
															.getId()
															.getDynamicFormTableRecordId(),
													dynamicFormTableRecord
															.getId()
															.getSequence());

											String tableRecordMethodName = PayAsiaConstants.SET_COL
													+ colName
															.substring(
																	colName.lastIndexOf('_') + 1,
																	colName.length());
											Method dynamicFormTableRecordMethod;
											try {
												dynamicFormTableRecordMethod = DynamicFormTableRecord.class
														.getMethod(
																tableRecordMethodName,
																String.class);

												dynamicFormTableRecordMethod
														.invoke(dynamicFormTableRecord,
																calculatedValue);

											} catch (SecurityException
													| NoSuchMethodException
													| IllegalArgumentException
													| IllegalAccessException
													| InvocationTargetException e) {
												LOGGER.error(e.getMessage(), e);
												throw new PayAsiaSystemException(
														e.getMessage(), e);
											}

											dynamicFormTableRecordDAO
													.update(dynamicFormTableRecord);
										}

									}

								} catch (NoSuchMethodException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								} catch (IllegalAccessException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								} catch (InvocationTargetException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(
											e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		}
	}

	public String getFormulaFieldCalculatedValue(
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<Long, Tab> dataTabMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap, String formula,
			String formulaType, DataDictionary dataDictionary, Long employeeId,
			Long companyId, Long tableId, Integer seqNo)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		List<Long> dataDictionaries = new ArrayList<Long>();
		String formulaEx = formula;

		if (StringUtils.isBlank(formulaType)
				|| "undefined".equalsIgnoreCase(formulaType)) {
			formulaType = PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC;
		}
		if (formulaType
				.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC)) {
			while (!formulaEx.equals("")) {
				if (formulaEx.indexOf('{') != -1) {
					String fex = formulaEx.substring(
							formulaEx.indexOf('{') + 1, formulaEx.indexOf('}'));
					dataDictionaries.add(Long.parseLong(fex));
					formulaEx = formulaEx.substring(formulaEx.indexOf('}') + 1);
				} else {
					break;
				}
			}
		} else if (formulaType
				.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_STRING)
				|| formulaType
						.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_DATE)) {
			while (!formulaEx.equals("")) {
				if (formulaEx.indexOf('{') != -1) {
					String fex = formulaEx.substring(
							formulaEx.indexOf('(') + 1, formulaEx.indexOf(')'));
					dataDictionaries.add(Long.parseLong(fex.substring(
							fex.indexOf('{') + 1, fex.indexOf('}'))));
					formulaEx = formulaEx.substring(formulaEx.indexOf(')') + 1);
				} else {
					break;
				}
			}
		}
		String calculatedValue = "";
		List<String> valueList = getValueForDictionary(employeeId,
				dataDictionary, dataDictionaries, dataTabMap, dynamicFormMap,
				staticPropMap, companyId, tableId, seqNo);

		if (formulaType
				.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_STRING)) {
			calculatedValue = FormulaUtils.getStringTypeFormulaCalulatedValue(
					formula, valueList);
		} else if (formulaType
				.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC)) {
			calculatedValue = FormulaUtils
					.getNumericTypeFormulaCalculatedValue(formula, valueList);
		} else if (formulaType
				.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_DATE)) {
			Company companyVO = companyDAO.findById(companyId);
			calculatedValue = FormulaUtils.getDateTypeFormulaCalulatedValue(
					formula, valueList, companyVO.getDateFormat());
		}
		return calculatedValue;
	}

	private List<String> getValueForDictionary(Long employeeId,
			DataDictionary dataDictionary, List<Long> dataDictionaries,
			HashMap<Long, Tab> dataTabMap,
			HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap, Long companyId,
			Long tableId, Integer seqNo) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		Map<Long, DataImportKeyValueDTO> formulaMap = new LinkedHashMap<Long, DataImportKeyValueDTO>();
		for (Long dataDicId : dataDictionaries) {
			DataDictionary dictionary = dataDictionaryDAO.findById(dataDicId);
			if (dictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {
			} else {
				setDynamicDictionary(formulaMap, dictionary, dataTabMap,
						dynamicFormMap);
			}
		}

		List<String> valueList = new ArrayList<String>();
		for (Long dicID : dataDictionaries) {
			DataImportKeyValueDTO formulaValueDTO = formulaMap.get(dicID);
			if (formulaValueDTO.isStatic()) {
			} else {
				if (formulaValueDTO.isChild()) {
					DynamicFormTableRecord formulaDynFormTableRecord = dynamicFormTableRecordDAO
							.findByIdAndSeq(tableId, seqNo);
					boolean isRecordFound = false;
					if (formulaDynFormTableRecord != null) {
						isRecordFound = true;
						String value = getColValueOfDynamicTableRecord(
								formulaValueDTO.getMethodName(),
								formulaDynFormTableRecord);
						if (value != null) {
							valueList.add(value);
						} else {
							valueList.add("");
						}

					} else {
						valueList.add("");
					}

					if (!isRecordFound) {
						valueList.add("");
					}
				} else {
					List<DynamicFormRecord> formulaDynFormRecordList = dynamicFormRecordDAO
							.findByEntityKey(employeeId, dataDictionary
									.getEntityMaster().getEntityId(), companyId);
					boolean isRecordFound = false;
					if (formulaDynFormRecordList != null
							&& !formulaDynFormRecordList.isEmpty()) {
						for (DynamicFormRecord formulaDynFormRecord : formulaDynFormRecordList) {
							if (formulaValueDTO.getFormId() == formulaDynFormRecord
									.getForm_ID()) {
								isRecordFound = true;
								String value = getColValueFile(
										formulaValueDTO.getMethodName(),
										formulaDynFormRecord);
								if (value != null) {
									valueList.add(value);
								} else {
									valueList.add("");
								}
							}
						}
					} else {
						valueList.add("");
					}

					if (!isRecordFound) {
						valueList.add("");
					}
				}
			}
		}
		return valueList;
	}

	private String getColValueFile(String colNumber,
			DynamicFormRecord existingFormRecord) {
		String tableRecordId = null;
		Class<?> dynamicFormRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormRecordMethod;
		try {

			dynamicFormRecordMethod = dynamicFormRecordClass
					.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormRecordMethod
					.invoke(existingFormRecord);
		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return tableRecordId;

	}

	private String getColValueOfDynamicTableRecord(String colNumber,
			DynamicFormTableRecord existingFormRecord) {
		String tableRecordId = null;
		Class<?> dynamicFormTableRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormTableRecordMethod;
		try {

			dynamicFormTableRecordMethod = dynamicFormTableRecordClass
					.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormTableRecordMethod
					.invoke(existingFormRecord);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
		return tableRecordId;
	}

	private void setDynamicDictionary(Map<Long, DataImportKeyValueDTO> colMap,
			DataDictionary dataDictionary, HashMap<Long, Tab> dataTabMap,
			HashMap<Long, DynamicForm> dynamicFormMap) {
		DynamicForm dynamicForm = null;
		Tab tab = dataTabMap.get(dataDictionary.getFormID());
		if (tab == null) {
			if (dataDictionary.getEntityMaster().getEntityName()
					.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				int maxVersion = dynamicFormDAO.getMaxVersionByFormId(
						dataDictionary.getCompany().getCompanyId(),
						dataDictionary.getEntityMaster().getEntityId(),
						dataDictionary.getFormID());

				dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
						dataDictionary.getCompany().getCompanyId(),
						dataDictionary.getEntityMaster().getEntityId(),
						maxVersion, dataDictionary.getFormID());
			}

			tab = dataExportUtils.getTabObject(dynamicForm);
			dataTabMap.put(dataDictionary.getFormID(), tab);
			dynamicFormMap.put(dataDictionary.getFormID(), dynamicForm);
		} else {
			dynamicForm = dynamicFormMap.get(dataDictionary.getFormID());
		}

		List<Field> listOfFields = tab.getField();
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		for (Field field : listOfFields) {
			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())
						|| field.getDictionaryId().longValue() == dataDictionary
								.getDataDictionaryId()) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
							.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormula(true);
					dataImportKeyValueDTO.setFormId(dynamicForm.getId()
							.getFormId());
					dataImportKeyValueDTO.setFormula(field.getFormula());

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(), "table")) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (StringUtils.equalsIgnoreCase(column.getType(),
							PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
						if (new String(Base64.decodeBase64(column
								.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())
								|| column.getDictionaryId().longValue() == dataDictionary
										.getDataDictionaryId()) {
							dataImportKeyValueDTO.setStatic(false);
							dataImportKeyValueDTO.setChild(true);
							dataImportKeyValueDTO
									.setFieldType(column.getType());
							dataImportKeyValueDTO
									.setMethodName(PayAsiaStringUtils
											.getColNumber(column.getName()));
							dataImportKeyValueDTO.setFormId(dynamicForm.getId()
									.getFormId());
							dataImportKeyValueDTO
									.setTablePosition(PayAsiaStringUtils
											.getColNumber(field.getName()));
						}
					}

				}
			}

			colMap.put(dataDictionary.getDataDictionaryId(),
					dataImportKeyValueDTO);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataExportLogic#getDataForTemplate(long,
	 * java.lang.Long)
	 */
	@Override
	public DataExportForm getDataForTemplate(long templateId, Long languageId,
			Long loggedInEmployeeId, String mode) {

		EmpDataExportTemplate empDataExportTemplate = empDataExportTemplateDAO
				.findById(templateId);

		DataExportForm dataExportForm = new DataExportForm();
		dataExportForm.setCategory(empDataExportTemplate.getEntityMaster()
				.getEntityName());
		dataExportForm.setDescription(empDataExportTemplate.getDescription());
		dataExportForm.setEntityId(empDataExportTemplate.getEntityMaster()
				.getEntityId());
		/*ID ENCRYPT*/
		dataExportForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(empDataExportTemplate
				.getExportTemplateId()));
		dataExportForm.setTemplateName(empDataExportTemplate.getTemplateName());
		dataExportForm.setIncludePrefix(empDataExportTemplate
				.getIncludePrefix());
		dataExportForm.setIncludeSuffix(empDataExportTemplate
				.getIncludeSuffix());
		dataExportForm.setIncludeTemplateNameAsPrefix(empDataExportTemplate
				.getIncludeTemplateNameAsPrefix());
		dataExportForm.setIncludeTimestampAsSuffix(empDataExportTemplate
				.getIncludeTimestampAsSuffix());
		dataExportForm.setPrefix(empDataExportTemplate.getPrefix());
		dataExportForm.setSuffix(empDataExportTemplate.getSuffix());

		List<DBTableInformationForm> dbTableInformationFormList = new ArrayList<DBTableInformationForm>();

		List<EmpDataExportTemplateField> empDataExportTemplateFieldList = new ArrayList<EmpDataExportTemplateField>(
				empDataExportTemplate.getEmpDataExportTemplateFields());

		Collections.sort(empDataExportTemplateFieldList,
				new EmpDataExportTemplateFieldComp());
		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();
		EmployeeShortListDTO employeeShortListDTO;
		EmployeeShortListDTO excelExportShortListDTO;
		Set<BigInteger> shortListEmpIds = new HashSet<>();
		int countT = 1;
		for (EmpDataExportTemplateField empDataExportTemplateField : empDataExportTemplateFieldList) {
			DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
			dbTableInformationForm.setDbId(empDataExportTemplateField
					.getExportFieldId());
			dbTableInformationForm
					.setDataDictionaryId(empDataExportTemplateField
							.getDataDictionary().getDataDictionaryId());
			dbTableInformationForm
					.setXlFeild(replaceSpecialChar(empDataExportTemplateField
							.getExcelFieldName()));
			dbTableInformationForm.setLabel(empDataExportTemplateField
					.getDataDictionary().getLabel());
			dbTableInformationForm
					.setExcelExpFieldEntityName(empDataExportTemplateField
							.getDataDictionary().getEntityMaster()
							.getEntityName());
			dbTableInformationForm.setFormId(empDataExportTemplateField
					.getDataDictionary().getFormID());
			dbTableInformationFormList.add(dbTableInformationForm);

			if (empDataExportTemplate.getEntityMaster().getEntityName()
					.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)
					|| empDataExportTemplate
							.getEntityMaster()
							.getEntityName()
							.equalsIgnoreCase(
									PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {
				if (empDataExportTemplateField
						.getDataDictionary()
						.getDataType()
						.equalsIgnoreCase(
								PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA)
						&& "generate".equalsIgnoreCase(mode)) {
					if (countT == 1) {
						employeeShortListDTO = generalLogic
								.getShortListEmployeeIds(loggedInEmployeeId,
										empDataExportTemplate.getCompany()
												.getCompanyId());
						excelExportShortListDTO = generalLogic
								.getExcelExportShortListEmployeeIds(templateId,
										empDataExportTemplate.getCompany()
												.getCompanyId());
						if ((employeeShortListDTO.getShortListEmployeeIds()
								.size() > 0)
								&& (excelExportShortListDTO
										.getShortListEmployeeIds().size() > 0)) {
							employeeShortListDTO.getShortListEmployeeIds()
									.retainAll(
											excelExportShortListDTO
													.getShortListEmployeeIds());
							shortListEmpIds.addAll(employeeShortListDTO
									.getShortListEmployeeIds());
						} else if ((employeeShortListDTO
								.getShortListEmployeeIds().size() > 0)
								&& (excelExportShortListDTO
										.getShortListEmployeeIds().isEmpty())) {
							shortListEmpIds.addAll(employeeShortListDTO
									.getShortListEmployeeIds());
						} else if ((employeeShortListDTO
								.getShortListEmployeeIds().isEmpty())
								&& (excelExportShortListDTO
										.getShortListEmployeeIds().size() > 0)) {
							shortListEmpIds.addAll(excelExportShortListDTO
									.getShortListEmployeeIds());
						} else {
							List<Employee> employeeVOList = employeeDAO
									.findAll(empDataExportTemplate.getCompany()
											.getCompanyId(), null, null);
							for (Employee employee : employeeVOList) {
								shortListEmpIds.add(BigInteger.valueOf(employee
										.getEmployeeId()));
							}

						}
						countT++;
					}

					insertFormulaFieldValueToEmpRecord(dynamicFormMap,
							dataTabMap, staticPropMap,
							empDataExportTemplateField.getDataDictionary()
									.getDataDictionaryId(),
							empDataExportTemplate.getCompany().getCompanyId(),
							shortListEmpIds);
				}
			}

		}

		List<ExcelExportFiltersForm> excelExportFilterList = new ArrayList<ExcelExportFiltersForm>();

		List<EmpDataExportTemplateFilter> empDataExportTemplateFilterList = new ArrayList<EmpDataExportTemplateFilter>(
				empDataExportTemplate.getEmpDataExportTemplateFilters());
		Collections.sort(empDataExportTemplateFilterList,
				new EmpDataExportTemplateFilterComp());

		for (EmpDataExportTemplateFilter empDataExportTemplateFilter : empDataExportTemplateFilterList) {
			ExcelExportFiltersForm excelExportFiltersForm = new ExcelExportFiltersForm();
			excelExportFiltersForm
					.setExportFilterId(empDataExportTemplateFilter
							.getExportFilterId());
			excelExportFiltersForm
					.setEqualityOperator(empDataExportTemplateFilter
							.getEqualityOperator());
			excelExportFiltersForm
					.setLogicalOperator(empDataExportTemplateFilter
							.getLogicalOperator());
			excelExportFiltersForm.setValue(empDataExportTemplateFilter
					.getValue());
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
			if (empDataExportTemplateFilter.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
				getStaticDictionaryInfo(
						empDataExportTemplateFilter.getDataDictionary(),
						dataImportKeyValueDTO);
				excelExportFiltersForm
						.setDataDictionaryName(empDataExportTemplateFilter
								.getDataDictionary().getDataDictName());
				excelExportFiltersForm
						.setDictionaryId(empDataExportTemplateFilter
								.getDataDictionary().getDataDictionaryId());
			} else {
				getDynamicDictionaryInfo(
						empDataExportTemplateFilter.getDataDictionary(),
						dataImportKeyValueDTO);
				excelExportFiltersForm
						.setDataDictionaryName(empDataExportTemplateFilter
								.getDataDictionary().getLabel());
			}

			MultiLingualData multiLingualData = multiLingualDataDAO
					.findByDictionaryIdAndLanguage(empDataExportTemplateFilter
							.getDataDictionary().getDataDictionaryId(),
							languageId);

			if (multiLingualData != null) {
				try {
					excelExportFiltersForm.setDataDictionaryName(URLEncoder
							.encode(multiLingualData.getLabel(), "UTF-8"));
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
			}

			excelExportFiltersForm
					.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			excelExportFiltersForm.setDictionaryId(empDataExportTemplateFilter
					.getDataDictionary().getDataDictionaryId());
			excelExportFiltersForm.setOpenBracket(empDataExportTemplateFilter
					.getOpenBracket());
			excelExportFiltersForm.setCloseBracket(empDataExportTemplateFilter
					.getCloseBracket());
			excelExportFilterList.add(excelExportFiltersForm);
		}

		dataExportForm
				.setDbTableInformationFormList(dbTableInformationFormList);

		dataExportForm.setExcelExportFilterList(excelExportFilterList);

		return dataExportForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataExportLogic#getDataForTemplate(long,
	 * java.lang.Long)
	 */
	@Override
	public DataExportForm getDataForTemplateGroup(long templateId,
			Long languageId, Long companyId, Long loggedInEmployeeId,
			String mode) {

		EntityMaster employeeEntity = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		EntityMaster companyEntity = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);
		List<DataDictionary> companyDataDictionary = dataDictionaryDAO
				.findByCondition(companyId, companyEntity.getEntityId());

		Map<String, DataDictionary> companyDataMap = new LinkedHashMap<String, DataDictionary>();
		for (DataDictionary dataDictionary : companyDataDictionary) {
			companyDataMap
					.put(dataDictionary.getDataDictName(), dataDictionary);

		}
		List<DataDictionary> employeeDataDictionary = dataDictionaryDAO
				.findByCondition(companyId, employeeEntity.getEntityId());
		Map<String, DataDictionary> employeeDataMap = new LinkedHashMap<String, DataDictionary>();
		for (DataDictionary dataDictionary : employeeDataDictionary) {
			employeeDataMap.put(dataDictionary.getDataDictName().toLowerCase(),
					dataDictionary);

		}

		EmpDataExportTemplate empDataExportTemplate = empDataExportTemplateDAO
				.findById(templateId);

		DataExportForm dataExportForm = new DataExportForm();
		dataExportForm.setEmployeeDataMap(employeeDataMap);
		dataExportForm.setCompanyDataMap(companyDataMap);
		dataExportForm.setCategory(empDataExportTemplate.getEntityMaster()
				.getEntityName());
		dataExportForm.setDescription(empDataExportTemplate.getDescription());
		dataExportForm.setEntityId(empDataExportTemplate.getEntityMaster()
				.getEntityId());
		
		/*ID ENCRYPT*/
		dataExportForm.setTemplateId(FormatPreserveCryptoUtil.encrypt(empDataExportTemplate.getExportTemplateId()));
		
		dataExportForm.setTemplateName(empDataExportTemplate.getTemplateName());
		dataExportForm.setIncludePrefix(empDataExportTemplate
				.getIncludePrefix());
		dataExportForm.setIncludeSuffix(empDataExportTemplate
				.getIncludeSuffix());
		dataExportForm.setIncludeTemplateNameAsPrefix(empDataExportTemplate
				.getIncludeTemplateNameAsPrefix());
		dataExportForm.setIncludeTimestampAsSuffix(empDataExportTemplate
				.getIncludeTimestampAsSuffix());
		dataExportForm.setPrefix(empDataExportTemplate.getPrefix());
		dataExportForm.setSuffix(empDataExportTemplate.getSuffix());

		List<DBTableInformationForm> dbTableInformationFormList = new ArrayList<DBTableInformationForm>();

		List<EmpDataExportTemplateField> empDataExportTemplateFieldList = new ArrayList<EmpDataExportTemplateField>(
				empDataExportTemplate.getEmpDataExportTemplateFields());

		Collections.sort(empDataExportTemplateFieldList,
				new EmpDataExportTemplateFieldComp());
		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();
		EmployeeShortListDTO employeeShortListDTO;
		EmployeeShortListDTO excelExportShortListDTO;
		Set<BigInteger> shortListEmpIds = new HashSet<>();
		int countT = 1;

		for (EmpDataExportTemplateField empDataExportTemplateField : empDataExportTemplateFieldList) {

			DBTableInformationForm dbTableInformationForm = new DBTableInformationForm();
			dbTableInformationForm.setDbId(empDataExportTemplateField
					.getExportFieldId());

			if (companyId == null) {
				companyId = empDataExportTemplate.getCompany().getCompanyId();

			}
			DataDictionary dataDictionary;

			if (empDataExportTemplateField.getEntityMaster()!=null && empDataExportTemplateField.getEntityMaster().getEntityName()
					.equals(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {

				dataDictionary = employeeDataMap.get(empDataExportTemplateField
						.getDataDictName().toLowerCase());

			} else {

				dataDictionary = companyDataMap.get(empDataExportTemplateField
						.getDataDictName());

			}

			if (dataDictionary != null) {

				dbTableInformationForm.setDataDictionaryId(dataDictionary
						.getDataDictionaryId());
				dbTableInformationForm.setFormId(dataDictionary.getFormID());
			}
			dbTableInformationForm
					.setXlFeild(replaceSpecialChar(empDataExportTemplateField
							.getExcelFieldName()));
			dbTableInformationForm
					.setExcelExpFieldEntityName(empDataExportTemplateField.getEntityMaster()!=null?empDataExportTemplateField.getEntityMaster().getEntityName():null);
			dbTableInformationForm
					.setDataDictionaryName(empDataExportTemplateField
							.getDataDictName());

			dbTableInformationFormList.add(dbTableInformationForm);

			if (empDataExportTemplate.getEntityMaster().getEntityName()
					.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)
					|| empDataExportTemplate
							.getEntityMaster()
							.getEntityName()
							.equalsIgnoreCase(
									PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {
				DataDictionary fieldDataDictionary = employeeDataMap
						.get(empDataExportTemplateField.getDataDictName()!=null?empDataExportTemplateField.getDataDictName().toLowerCase():null);
				if (fieldDataDictionary != null
						&& fieldDataDictionary
								.getDataType()
								.equalsIgnoreCase(
										PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FORMULA)
						&& "generate".equalsIgnoreCase(mode)) {

					if (countT == 1) {
						employeeShortListDTO = generalLogic
								.getShortListEmployeeIds(loggedInEmployeeId,
										companyId);
						excelExportShortListDTO = generalLogic
								.getExcelExportShortListEmployeeIds(templateId,
										companyId);
						if ((employeeShortListDTO.getShortListEmployeeIds()
								.size() > 0)
								&& (excelExportShortListDTO
										.getShortListEmployeeIds().size() > 0)) {
							employeeShortListDTO.getShortListEmployeeIds()
									.retainAll(
											excelExportShortListDTO
													.getShortListEmployeeIds());
							shortListEmpIds.addAll(employeeShortListDTO
									.getShortListEmployeeIds());
						} else if ((employeeShortListDTO
								.getShortListEmployeeIds().size() > 0)
								&& (excelExportShortListDTO
										.getShortListEmployeeIds().isEmpty())) {
							shortListEmpIds.addAll(employeeShortListDTO
									.getShortListEmployeeIds());
						} else if ((employeeShortListDTO
								.getShortListEmployeeIds().isEmpty())
								&& (excelExportShortListDTO
										.getShortListEmployeeIds().size() > 0)) {
							shortListEmpIds.addAll(excelExportShortListDTO
									.getShortListEmployeeIds());
						} else {
							List<Employee> employeeVOList = employeeDAO
									.findAll(empDataExportTemplate.getCompany()
											.getCompanyId(), null, null);
							for (Employee employee : employeeVOList) {
								shortListEmpIds.add(BigInteger.valueOf(employee
										.getEmployeeId()));
							}

						}
						countT++;
					}

					insertFormulaFieldValueToEmpRecord(dynamicFormMap,
							dataTabMap, staticPropMap,
							fieldDataDictionary.getDataDictionaryId(),
							fieldDataDictionary.getCompany().getCompanyId(),
							shortListEmpIds);

				}
			}

		}

		List<ExcelExportFiltersForm> excelExportFilterList = new ArrayList<ExcelExportFiltersForm>();

		List<EmpDataExportTemplateFilter> empDataExportTemplateFilterList = new ArrayList<EmpDataExportTemplateFilter>(
				empDataExportTemplate.getEmpDataExportTemplateFilters());
		Collections.sort(empDataExportTemplateFilterList,
				new EmpDataExportTemplateFilterComp());

		for (EmpDataExportTemplateFilter empDataExportTemplateFilter : empDataExportTemplateFilterList) {
			ExcelExportFiltersForm excelExportFiltersForm = new ExcelExportFiltersForm();
			excelExportFiltersForm
					.setExportFilterId(empDataExportTemplateFilter
							.getExportFilterId());
			excelExportFiltersForm
					.setEqualityOperator(empDataExportTemplateFilter
							.getEqualityOperator());
			excelExportFiltersForm
					.setLogicalOperator(empDataExportTemplateFilter
							.getLogicalOperator());
			excelExportFiltersForm.setValue(empDataExportTemplateFilter
					.getValue());
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
			if (empDataExportTemplateFilter.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
				getStaticDictionaryInfo(
						empDataExportTemplateFilter.getDataDictionary(),
						dataImportKeyValueDTO);
				excelExportFiltersForm
						.setDataDictionaryName(empDataExportTemplateFilter
								.getDataDictionary().getDataDictName());
			} else {
				getDynamicDictionaryInfo(
						empDataExportTemplateFilter.getDataDictionary(),
						dataImportKeyValueDTO);
				excelExportFiltersForm
						.setDataDictionaryName(empDataExportTemplateFilter
								.getDataDictionary().getLabel());
			}

			MultiLingualData multiLingualData = multiLingualDataDAO
					.findByDictionaryIdAndLanguage(empDataExportTemplateFilter
							.getDataDictionary().getDataDictionaryId(),
							languageId);

			if (multiLingualData != null) {
				excelExportFiltersForm.setDataDictionaryName(multiLingualData.getLabel());
				/*try {
					
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}*/
			}

			excelExportFiltersForm
					.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			excelExportFiltersForm.setDictionaryId(empDataExportTemplateFilter
					.getDataDictionary().getDataDictionaryId());
			excelExportFiltersForm.setOpenBracket(empDataExportTemplateFilter
					.getOpenBracket());
			excelExportFiltersForm.setCloseBracket(empDataExportTemplateFilter
					.getCloseBracket());
			excelExportFilterList.add(excelExportFiltersForm);
		}

		dataExportForm
				.setDbTableInformationFormList(dbTableInformationFormList);

		dataExportForm.setExcelExportFilterList(excelExportFilterList);

		return dataExportForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataExportLogic#generateExcel(long,
	 * java.lang.Long, java.lang.Long, java.util.List, java.lang.Long)
	 */
	@Override
	public DataExportForm generateExcel(long templateId, Long companyId,
			Long employeeId, List<ExcelExportFiltersForm> exportFilterlist,
			Long languageId) {
		DataExportForm dataExportForm = getDataForTemplate(templateId,
				languageId, employeeId, "generate");

		Company company = companyDAO.findById(companyId);
		List<DBTableInformationForm> dbTableInformationFormList = dataExportForm
				.getDbTableInformationFormList();

		Workbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();

		Sheet sheet = wb.createSheet("Sheet1");

		CellStyle headerCellStyle = wb.createCellStyle();
		setcellHeaderStyle(headerCellStyle);

		Font headerFont = wb.createFont();
		headerFont.setFontName(HSSFFont.FONT_ARIAL);
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setColor(HSSFColor.BLACK.index);
		headerCellStyle.setFont(headerFont);

		CellStyle sampleDataCellStyle = wb.createCellStyle();
		sampleDataCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		Map<String, DataImportKeyValueDTO> colMapForEmployee = new LinkedHashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMapForCompany = new LinkedHashMap<String, DataImportKeyValueDTO>();

		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();

		Map<String, DataImportKeyValueDTO> tableRecordInfoFromForEmployee = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> tableRecordInfoFromForCompany = new HashMap<String, DataImportKeyValueDTO>();
		Map<Long, Tab> tabCacheMap = new HashMap<Long, Tab>();
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();

		Row headerRow = sheet.createRow(0);
		int columnCount = 0;

		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

		List<DBTableInformationForm> dbTableInformationCompanyFormList = new ArrayList<DBTableInformationForm>();
		List<DBTableInformationForm> dbTableInformationEmployeeFormList = new ArrayList<DBTableInformationForm>();

		long entityId = dataExportForm.getEntityId();
		EntityMaster entityMaster = entityMasterDAO.findById(entityId);
		List<Long> formIds = null;
		List<Long> companyFormIds = null;
		List<Long> employeeFormIds = null;
		long employeeEntityId = 0;
		long companyEntityId = 0;
		Integer employeeNumberColumnCnt = null;

		if (dataExportForm.getCategory().equals(
				PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {
			List<EntityMaster> entityMasterList = entityMasterDAO.findAll();

			for (EntityMaster entityMasterVO : entityMasterList) {
				if (entityMasterVO.getEntityName().equalsIgnoreCase(
						PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
					employeeEntityId = entityMasterVO.getEntityId();
				}
				if (entityMasterVO.getEntityName().equalsIgnoreCase(
						PayAsiaConstants.COMPANY_ENTITY_NAME)) {
					companyEntityId = entityMasterVO.getEntityId();
				}
			}
			companyFormIds = dynamicFormDAO.getDistinctFormId(companyId,
					companyEntityId);

			employeeFormIds = generalLogic.getAdminAuthorizedSectionIdList(
					employeeId, companyId, employeeEntityId);

			for (DBTableInformationForm dBTableInformationForm : dbTableInformationFormList) {
				DBTableInformationForm dbTableInfoForm = new DBTableInformationForm();
				dbTableInfoForm.setDbId(dBTableInformationForm.getDbId());
				dbTableInfoForm.setDataDictionaryId(dBTableInformationForm
						.getDataDictionaryId());
				dbTableInfoForm.setXlFeild(dBTableInformationForm.getXlFeild());
				dbTableInfoForm
						.setExcelExpFieldEntityName(dBTableInformationForm
								.getExcelExpFieldEntityName());
				if (dBTableInformationForm
						.getExcelExpFieldEntityName()
						.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
					if (dBTableInformationForm.getFormId() == null) {
						dbTableInformationEmployeeFormList.add(dbTableInfoForm);
					}
					if (employeeFormIds.contains(dBTableInformationForm
							.getFormId())) {
						dbTableInformationEmployeeFormList.add(dbTableInfoForm);
					}

				}
				if (dBTableInformationForm.getExcelExpFieldEntityName()
						.equalsIgnoreCase(PayAsiaConstants.COMPANY_ENTITY_NAME)) {

					dbTableInformationCompanyFormList.add(dbTableInfoForm);

				}
			}

			for (DBTableInformationForm dBTableInformationForm : dbTableInformationCompanyFormList) {

				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(dBTableInformationForm.getDataDictionaryId());

				if (dataDictionary.getFieldType().equalsIgnoreCase(
						PayAsiaConstants.STATIC_TYPE)) {
					setStaticDictionary(colMapForCompany,
							dBTableInformationForm, dataDictionary);
				} else {
					setDynamicDictionary(colMapForCompany,
							dBTableInformationForm, dataDictionary,
							tabCacheMap, companyFormIds,
							dataExportForm.getCategory());

				}
				if (colMapForCompany.get(
						dBTableInformationForm.getXlFeild()
								+ dBTableInformationForm.getDataDictionaryId())
						.getTablePosition() != null) {
					String tableKey;
					tableKey = colMapForCompany.get(
							dBTableInformationForm.getXlFeild()
									+ dBTableInformationForm
											.getDataDictionaryId()).getFormId()
							+ colMapForCompany.get(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId())
									.getTablePosition();
					tableRecordInfoFromForCompany.put(tableKey,
							colMapForCompany.get(dBTableInformationForm
									.getXlFeild()
									+ dBTableInformationForm
											.getDataDictionaryId()));

				}

				Cell headerCell = headerRow.createCell(columnCount);
				headerCell.setCellStyle(headerCellStyle);
				if (colMapForCompany
						.get(dBTableInformationForm.getXlFeild()
								+ dBTableInformationForm.getDataDictionaryId())
						.getFieldType()
						.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
					headerCell.setCellValue(dBTableInformationForm.getXlFeild()
							+ " Code");
					columnCount++;
					Cell headerCell1 = headerRow.createCell(columnCount);
					headerCell1.setCellValue(dBTableInformationForm
							.getXlFeild() + " Description");
					headerCell1.setCellStyle(headerCellStyle);
					sheet.autoSizeColumn(columnCount);

				} else {
					headerCell
							.setCellValue(dBTableInformationForm.getXlFeild());
				}

				sheet.autoSizeColumn(columnCount);
				columnCount++;

			}

			for (DBTableInformationForm dBTableInformationForm : dbTableInformationEmployeeFormList) {

				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(dBTableInformationForm.getDataDictionaryId());

				if (dataDictionary.getFieldType().equalsIgnoreCase(
						PayAsiaConstants.STATIC_TYPE)) {
					setStaticDictionary(colMapForEmployee,
							dBTableInformationForm, dataDictionary);
					if (dataDictionary.getColumnName().equals(
							PayAsiaConstants.PAYASIA_EMPLOYEE_NUMBER)) {
						employeeNumberColumnCnt = columnCount;
					}

				} else {
					setDynamicDictionary(colMapForEmployee,
							dBTableInformationForm, dataDictionary,
							tabCacheMap, employeeFormIds,
							dataExportForm.getCategory());

				}
				if (colMapForEmployee.get(
						dBTableInformationForm.getXlFeild()
								+ dBTableInformationForm.getDataDictionaryId())
						.getTablePosition() != null) {
					String tableKey;
					tableKey = colMapForEmployee.get(
							dBTableInformationForm.getXlFeild()
									+ dBTableInformationForm
											.getDataDictionaryId()).getFormId()
							+ colMapForEmployee.get(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId())
									.getTablePosition();
					tableRecordInfoFromForEmployee.put(tableKey,
							colMapForEmployee.get(dBTableInformationForm
									.getXlFeild()
									+ dBTableInformationForm
											.getDataDictionaryId()));

				}

				Cell headerCell = headerRow.createCell(columnCount);
				headerCell.setCellStyle(headerCellStyle);
				if (colMapForEmployee
						.get(dBTableInformationForm.getXlFeild()
								+ dBTableInformationForm.getDataDictionaryId())
						.getFieldType()
						.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
					headerCell.setCellValue(dBTableInformationForm.getXlFeild()
							+ " Code");
					columnCount++;
					Cell headerCell1 = headerRow.createCell(columnCount);
					headerCell1.setCellValue(dBTableInformationForm
							.getXlFeild() + " Description");
					headerCell1.setCellStyle(headerCellStyle);
					sheet.autoSizeColumn(columnCount);

				} else {
					headerCell
							.setCellValue(dBTableInformationForm.getXlFeild());
				}

				sheet.autoSizeColumn(columnCount);
				columnCount++;

			}

		} else {

			if (dataExportForm.getCategory().equals(
					PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {

				formIds = generalLogic.getAdminAuthorizedSectionIdList(
						employeeId, companyId, dataExportForm.getEntityId());
			} else {
				formIds = dynamicFormDAO.getDistinctFormId(companyId,
						dataExportForm.getEntityId());
			}

			List<DBTableInformationForm> dbTableInformationFormEmpList = new ArrayList<>();
			for (DBTableInformationForm dBTableInformationForm : dbTableInformationFormList) {
				if (dataExportForm.getCategory().equals(
						PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
					if (dBTableInformationForm.getFormId() == null) {
						dbTableInformationFormEmpList
								.add(dBTableInformationForm);
					}

					if (formIds.contains(dBTableInformationForm.getFormId())) {
						dbTableInformationFormEmpList
								.add(dBTableInformationForm);
					}
				} else {
					dbTableInformationFormEmpList.add(dBTableInformationForm);
				}

			}

			for (DBTableInformationForm dBTableInformationForm : dbTableInformationFormEmpList) {

				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(dBTableInformationForm.getDataDictionaryId());

				if (dataDictionary.getFieldType().equalsIgnoreCase(
						PayAsiaConstants.STATIC_TYPE)) {
					setStaticDictionary(colMap, dBTableInformationForm,
							dataDictionary);

					if (dataDictionary.getColumnName().equals(
							PayAsiaConstants.PAYASIA_EMPLOYEE_NUMBER)) {
						employeeNumberColumnCnt = columnCount;
					}
				} else {
					setDynamicDictionary(colMap, dBTableInformationForm,
							dataDictionary, tabCacheMap, formIds,
							dataExportForm.getCategory());

				}
				if (colMap.get(
						dBTableInformationForm.getXlFeild()
								+ dBTableInformationForm.getDataDictionaryId())
						.getTablePosition() != null) {
					String tableKey;
					tableKey = colMap.get(
							dBTableInformationForm.getXlFeild()
									+ dBTableInformationForm
											.getDataDictionaryId()).getFormId()
							+ colMap.get(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId())
									.getTablePosition();
					tableRecordInfoFrom.put(
							tableKey,
							colMap.get(dBTableInformationForm.getXlFeild()
									+ dBTableInformationForm
											.getDataDictionaryId()));

				}

				Cell headerCell = headerRow.createCell(columnCount);
				headerCell.setCellStyle(headerCellStyle);
				if (colMap
						.get(dBTableInformationForm.getXlFeild()
								+ dBTableInformationForm.getDataDictionaryId())
						.getFieldType()
						.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
					headerCell.setCellValue(dBTableInformationForm.getXlFeild()
							+ " Code");
					columnCount++;
					Cell headerCell1 = headerRow.createCell(columnCount);
					headerCell1.setCellValue(dBTableInformationForm
							.getXlFeild() + " Description");
					headerCell1.setCellStyle(headerCellStyle);
					sheet.autoSizeColumn(columnCount);

				} else {
					headerCell
							.setCellValue(dBTableInformationForm.getXlFeild());
				}

				sheet.autoSizeColumn(columnCount);
				columnCount++;

			}
		}

		List<ExcelExportFiltersForm> excelExportFilterList = dataExportForm
				.getExcelExportFilterList();

		for (ExcelExportFiltersForm excelExportFiltersForm : excelExportFilterList) {

			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(excelExportFiltersForm.getDictionaryId());
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
			if (dataDictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {

				getStaticDictionaryInfo(dataDictionary, dataImportKeyValueDTO);

			} else {

				getDynamicDictionaryInfo(dataDictionary, dataImportKeyValueDTO);

			}

			if(exportFilterlist != null && !exportFilterlist.isEmpty())
			{
				for (ExcelExportFiltersForm exportFilter : exportFilterlist) {
					if (exportFilter.getExportFilterId() == excelExportFiltersForm
							.getExportFilterId()) {
						try {
							excelExportFiltersForm.setValue(URLDecoder.decode(
									exportFilter.getValue(), "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							LOGGER.error(e.getMessage(), e);
						}
					}
				}
			}

			excelExportFiltersForm
					.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			finalFilterList.add(excelExportFiltersForm);
		}

		List<Object[]> tupleList = new ArrayList<Object[]>();

		List<Object[]> tupleListForEmployee = new ArrayList<Object[]>();
		List<Object[]> tupleListForCompany = new ArrayList<Object[]>();
		// Show by effective date table data if one custom table record is
		// required, here it is false i.e. show all table data
		boolean showByEffectiveDateTableData = false;
		if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {

			EmployeeShortListDTO employeeShortListDTO = generalLogic
					.getShortListEmployeeIds(employeeId, companyId);

			tupleList.addAll(employeeDAO.findByCondition(colMap, formIds,
					companyId, finalFilterList, company.getDateFormat(),
					tableRecordInfoFrom, tableElements, employeeShortListDTO,
					showByEffectiveDateTableData));
		} else if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.COMPANY_ENTITY_NAME)) {

			tupleList.addAll(companyDAO.findByCondition(colMap, formIds,
					finalFilterList, employeeId, tableRecordInfoFrom));
		} else if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {

			tupleList.addAll(payslipDAO.findByCondition(colMap, formIds,
					finalFilterList, companyId));
		} else if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.PAY_DATA_COLLECTION_ENTITY_NAME)) {
			tupleList.addAll(payDataCollectionDAO.findByCondition(colMap,
					finalFilterList, companyId, company.getDateFormat()));
		} else if (entityMaster.getEntityName().equalsIgnoreCase(
				PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {
			if (colMapForCompany.size() != 0) {
				tupleListForCompany.addAll(companyDAO.findByConditionCompanyId(
						colMapForCompany, companyFormIds,
						new ArrayList<ExcelExportFiltersForm>(), companyId,
						tableRecordInfoFrom));
			}
			if (colMapForEmployee.size() != 0) {

				EmployeeShortListDTO employeeShortListDTO = generalLogic
						.getShortListEmployeeIds(employeeId, companyId);

				tupleListForEmployee.addAll(employeeDAO.findByCondition(
						colMapForEmployee, employeeFormIds, companyId,
						finalFilterList, company.getDateFormat(),
						tableRecordInfoFromForEmployee, tableElements,
						employeeShortListDTO, showByEffectiveDateTableData));
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat(company.getDateFormat());
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
				company.getDateFormat()));
		SimpleDateFormat sdfSource = new SimpleDateFormat(
				PayAsiaConstants.EFFECTIVE_DATE_FORMAT);
		SimpleDateFormat serverSDF = new SimpleDateFormat(
				PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
		Date effectiveFormatDate;
		Date serverSideDate;
		String strObject;
		int colCount = 0;
		int rowCount = 1;
		if (dataExportForm.getCategory().equals(
				PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {

			if (tupleListForEmployee != null && !tupleListForEmployee.isEmpty()) {
				if (!(tupleListForEmployee.get(0) instanceof Object[])) {
					List<Object[]> tupleListEmpMod = new ArrayList<>();
					for (Object obj : tupleListForEmployee) {
						Object[] objArr = new Object[1];
						objArr[0] = obj;
						tupleListEmpMod.add(objArr);
					}

					tupleListForEmployee = tupleListEmpMod;
				}
			}
			if (tupleListForCompany != null && !tupleListForCompany.isEmpty()) {
				if (!(tupleListForCompany.get(0) instanceof Object[])) {
					List<Object[]> tupleListCmpMod = new ArrayList<>();
					for (Object obj : tupleListForCompany) {
						Object[] objArr = new Object[1];
						objArr[0] = obj;
						tupleListCmpMod.add(objArr);
					}

					tupleListForCompany = tupleListCmpMod;
				}
			}

			for (Object[] tuple : tupleListForEmployee) {
				Row newRow = sheet.createRow(rowCount);
				LOGGER.debug("Start -----> Current TimeStamp----"
						+ DateUtils.getCurrentTimestampWithTime()
						+ ">>RowCount >>" + rowCount);
				colCount = 0;

				for (Object[] tupleCompany : tupleListForCompany) {
					for (Object object : tupleCompany) {

						Cell headerCell = newRow.createCell(colCount);
						if (object != null) {

							strObject = String.valueOf(object);

							if (object instanceof Boolean) {

								if (object.equals(true)) {
									headerCell
											.setCellValue(PayAsiaConstants.PAYASIA_ENABLE);
								} else {
									headerCell
											.setCellValue(PayAsiaConstants.PAYASIA_DISABLE);
								}

							} else if (object instanceof Date) {
								headerCell.setCellStyle(cellStyle);
								headerCell.setCellValue(DateUtils.stringToDate(
										sdf.format(object),
										company.getDateFormat()));

							} else if (NumberUtils.isNumber(strObject)) {
								double cellValue = Double.valueOf(strObject);
								if (String.valueOf(cellValue).equals(strObject)) {
									headerCell
											.setCellType(Cell.CELL_TYPE_NUMERIC);
									headerCell.setCellValue(cellValue);
								} else {
									headerCell
											.setCellType(Cell.CELL_TYPE_STRING);
									headerCell.setCellValue(strObject);
								}
							} else {
								headerCell.setCellValue(strObject);

								try {
									effectiveFormatDate = sdfSource
											.parse(strObject);
									headerCell.setCellValue(sdf
											.format(effectiveFormatDate));
									headerCell.setCellStyle(cellStyle);
								} catch (ParseException parseException) {
									headerCell.setCellValue(strObject);
								}

								try {
									serverSideDate = serverSDF.parse(strObject);
									headerCell.setCellValue(DateUtils
											.stringToDate(
													sdf.format(serverSideDate),
													company.getDateFormat()));
									headerCell.setCellStyle(cellStyle);
								} catch (ParseException parseException) {
									headerCell.setCellValue(strObject);
								}

							}

						} else {
							headerCell.setCellValue("");
						}

						colCount++;
					}
				}

				for (Object object : tuple) {

					Cell headerCell = newRow.createCell(colCount);
					if (object != null) {

						strObject = String.valueOf(object);

						if (object instanceof Boolean) {

							if (object.equals(true)) {
								headerCell
										.setCellValue(PayAsiaConstants.PAYASIA_ENABLE);
							} else {
								headerCell
										.setCellValue(PayAsiaConstants.PAYASIA_DISABLE);
							}

						} else if (object instanceof Date) {
							headerCell.setCellStyle(cellStyle);

							headerCell
									.setCellValue(DateUtils.stringToDate(
											sdf.format(object),
											company.getDateFormat()));

						} else if (employeeNumberColumnCnt != null
								&& employeeNumberColumnCnt == colCount) {
							headerCell.setCellValue(strObject);
						}

						else if (NumberUtils.isNumber(strObject)) {
							double cellValue = Double.valueOf(strObject);
							if (String.valueOf(cellValue).equals(strObject)) {
								headerCell.setCellType(Cell.CELL_TYPE_NUMERIC);
								headerCell.setCellValue(cellValue);
							} else {
								headerCell.setCellType(Cell.CELL_TYPE_STRING);
								headerCell.setCellValue(strObject);
							}
						}

						else {
							headerCell.setCellValue(strObject);

							try {
								effectiveFormatDate = sdfSource
										.parse(strObject);
								headerCell.setCellValue(sdf
										.format(effectiveFormatDate));
								headerCell.setCellStyle(cellStyle);
							} catch (ParseException parseException) {
								headerCell.setCellValue(strObject);
							}

							try {
								serverSideDate = serverSDF.parse(strObject);
								headerCell.setCellValue(DateUtils.stringToDate(
										sdf.format(serverSideDate),
										company.getDateFormat()));
								headerCell.setCellStyle(cellStyle);
							} catch (ParseException parseException) {
								headerCell.setCellValue(strObject);
							}

						}

					} else {
						headerCell.setCellValue("");
					}

					colCount++;
				}
				LOGGER.debug("End -----> Current TimeStamp>>"
						+ DateUtils.getCurrentTimestamp() + ">>RowCount >>"
						+ rowCount);
				rowCount++;
			}
		} else {
			for (Object[] tuple : tupleList) {
				Row newRow = sheet.createRow(rowCount);
				LOGGER.debug("Start -----> Current TimeStamp----"
						+ DateUtils.getCurrentTimestampWithTime()
						+ ">>RowCount >>" + rowCount);
				colCount = 0;
				for (Object object : tuple) {

					Cell headerCell = newRow.createCell(colCount);
					if (object != null) {

						strObject = String.valueOf(object);

						if (object instanceof Boolean) {

							if (object.equals(true)) {
								headerCell
										.setCellValue(PayAsiaConstants.PAYASIA_ENABLE);
							} else {
								headerCell
										.setCellValue(PayAsiaConstants.PAYASIA_DISABLE);
							}

						} else if (object instanceof Date) {
							headerCell.setCellStyle(cellStyle);
							headerCell
									.setCellValue(DateUtils.stringToDate(
											sdf.format(object),
											company.getDateFormat()));

						} else if (employeeNumberColumnCnt != null
								&& employeeNumberColumnCnt == colCount) {
							headerCell.setCellValue(strObject);
						}

						else if (NumberUtils.isNumber(strObject)) {
							double cellValue = Double.valueOf(strObject);
							if (String.valueOf(cellValue).equals(strObject)) {
								headerCell.setCellType(Cell.CELL_TYPE_NUMERIC);
								headerCell.setCellValue(cellValue);
							} else {
								headerCell.setCellType(Cell.CELL_TYPE_STRING);
								headerCell.setCellValue(strObject);
							}
						} else {
							headerCell.setCellValue(strObject);

							try {
								effectiveFormatDate = sdfSource
										.parse(strObject);
								headerCell.setCellValue(sdf
										.format(effectiveFormatDate));
								headerCell.setCellStyle(cellStyle);
							} catch (ParseException parseException) {
								headerCell.setCellValue(strObject);
							}

							try {
								serverSideDate = serverSDF.parse(strObject);
								headerCell.setCellValue(DateUtils.stringToDate(
										sdf.format(serverSideDate),
										company.getDateFormat()));
								headerCell.setCellStyle(cellStyle);
							} catch (ParseException parseException) {
								headerCell.setCellValue(strObject);
							}

						}

					} else {
						headerCell.setCellValue("");
					}

					colCount++;
				}
				LOGGER.debug("End -----> Current TimeStamp>>"
						+ DateUtils.getCurrentTimestamp() + ">>RowCount >>"
						+ rowCount);
				rowCount++;
			}
		}

		for (int count = 0; count < colCount; count++) {
			sheet.autoSizeColumn(count);
		}

		DataExportForm exportForm = new DataExportForm();
		String fileName = "";
		if (dataExportForm.isIncludePrefix()) {
			fileName = fileName + dataExportForm.getPrefix();
		} else if (dataExportForm.isIncludeTemplateNameAsPrefix()) {
			fileName = fileName + dataExportForm.getTemplateName();
		}

		if (dataExportForm.isIncludeSuffix()) {
			fileName = fileName + dataExportForm.getSuffix();
		} else if (dataExportForm.isIncludeTimestampAsSuffix()) {
			fileName = fileName
					+ new SimpleDateFormat(
							PayAsiaConstants.FILE_NAME_TIMESTAMP_FORMAT)
							.format(new Date());
		}

		if (StringUtils.isBlank(fileName)) {
			fileName = PayAsiaConstants.EXPORT_FILE_DEFAULT_TEMPLATE_NAME;
		}
		exportForm.setFinalFileName(fileName);
		exportForm.setWorkbook(wb);

		return exportForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataExportLogic#generateExcel(long,
	 * java.lang.Long, java.lang.Long, java.util.List, java.lang.Long)
	 */
	@Override
	public synchronized DataExportForm generateExcelGroup(long templateId,
			Long companyId, Long employeeId,
			List<ExcelExportFiltersForm> exportFilterlist, Long languageId,
			String[] selectedIds) {

		Company company = companyDAO.findById(companyId);
		Workbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();

		Sheet sheet = wb.createSheet("Sheet1");

		CellStyle headerCellStyle = wb.createCellStyle();
		setcellHeaderStyle(headerCellStyle);

		Font headerFont = wb.createFont();
		headerFont.setFontName(HSSFFont.FONT_ARIAL);
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setColor(HSSFColor.BLACK.index);
		headerCellStyle.setFont(headerFont);

		CellStyle sampleDataCellStyle = wb.createCellStyle();
		sampleDataCellStyle.setAlignment(CellStyle.ALIGN_CENTER);

		Row headerRow = sheet.createRow(0);

		DataExportForm dataExportForm = new DataExportForm();
		Map<String, DataImportKeyValueDTO> colMap = null;
		Map<String, DataImportKeyValueDTO> colMapForCompany = null;
		Map<String, DataImportKeyValueDTO> colMapForEmployee = null;
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = null;
		Map<String, DataImportKeyValueDTO> tableRecordInfoCompany = null;
		Map<String, DataImportKeyValueDTO> tableRecordInfoEmployee = null;
		Map<Long, EmployeeCompanyTupleDTO> tupleListColMap = new LinkedHashMap<Long, EmployeeCompanyTupleDTO>();

		List<DBTableInformationForm> dbTableInformationFormList = new ArrayList<DBTableInformationForm>();
		List<DataImportKeyValueDTO> tableElements = null;
		List<ExcelExportFiltersForm> finalFilterList = null;
		List<ExcelExportFiltersForm> excelExportFilterList = null;
		String tableKey;
		EntityMaster entityMaster = null;
		List<Long> formIds = null;
		List<Long> companyFormIds = null;
		List<Long> employeeFormIds = null;
		Long employeeEntityId = 0l;
		Long companyEntityId = 0l;
		List<DBTableInformationForm> dbTableInformationCompanyFormList = null;
		List<DBTableInformationForm> dbTableInformationEmployeeFormList = null;
		Integer employeeNumberColumnCnt = null;
		List<Integer> companyFieldPositon = null;
		List<Integer> employeeFieldPositon = null;
		HashMap<String, Integer> fieldPosition = null;
		List<Object[]> tupleList = new ArrayList<Object[]>();

		long entityId;
		for (String cmpId : selectedIds) {
			Map<Long, Tab> tabCacheMap = new HashMap<Long, Tab>();
			Map<Long, DynamicForm> dynamicFormMapCompany = new LinkedHashMap<>();
			Map<Long, DynamicForm> dynamicFormMapEmployee = new LinkedHashMap<>();

			dataExportForm = getDataForTemplateGroup(templateId, languageId,
					Long.parseLong(cmpId), employeeId, "generate");

			entityId = dataExportForm.getEntityId();
			entityMaster = entityMasterDAO.findById(entityId);

			Map<String, DataDictionary> employeeDataDictionary = dataExportForm
					.getEmployeeDataMap();
			Map<String, DataDictionary> companyDataDictionary = dataExportForm
					.getCompanyDataMap();

			colMapForEmployee = new LinkedHashMap<String, DataImportKeyValueDTO>();
			tableRecordInfoEmployee = new HashMap<String, DataImportKeyValueDTO>();
			colMapForCompany = new LinkedHashMap<String, DataImportKeyValueDTO>();
			tableRecordInfoCompany = new HashMap<String, DataImportKeyValueDTO>();
			dbTableInformationCompanyFormList = new ArrayList<DBTableInformationForm>();
			dbTableInformationEmployeeFormList = new ArrayList<DBTableInformationForm>();
			fieldPosition = new HashMap<>();
			companyFieldPositon = new ArrayList<>();
			employeeFieldPositon = new ArrayList<>();

			if (dataExportForm.getCategory().equals(
					PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {

				List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
				for (EntityMaster entityMasterVO : entityMasterList) {
					if (entityMasterVO.getEntityName().equalsIgnoreCase(
							PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
						employeeEntityId = entityMasterVO.getEntityId();

					}
					if (entityMasterVO.getEntityName().equalsIgnoreCase(
							PayAsiaConstants.COMPANY_ENTITY_NAME)) {
						companyEntityId = entityMasterVO.getEntityId();
					}
				}
				companyFormIds = dynamicFormDAO.getDistinctFormId(
						Long.parseLong(cmpId), companyEntityId);

				for (Long companyFormId : companyFormIds) {

					DynamicForm dynamicForm = dynamicFormDAO
							.findMaxVersionByFormId(Long.parseLong(cmpId),
									companyEntityId, companyFormId);

					dynamicFormMapCompany.put(companyFormId, dynamicForm);

				}

				employeeFormIds = generalLogic.getAdminAuthorizedSectionIdList(
						employeeId, Long.parseLong(cmpId), employeeEntityId);

				for (Long employeeFormId : employeeFormIds) {

					DynamicForm dynamicForm = dynamicFormDAO
							.findMaxVersionByFormId(Long.parseLong(cmpId),
									employeeEntityId, employeeFormId);

					dynamicFormMapEmployee.put(employeeFormId, dynamicForm);

				}
				Integer positionCount = 0;
				for (DBTableInformationForm dBTableInformationForm : dataExportForm
						.getDbTableInformationFormList()) {
					fieldPosition.put(dBTableInformationForm.getXlFeild(),
							positionCount);

					if (dBTableInformationForm.getExcelExpFieldEntityName()
							.equalsIgnoreCase(
									PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
						employeeFieldPositon.add(positionCount);
						if (dBTableInformationForm.getFormId() == null) {
							dbTableInformationEmployeeFormList
									.add(dBTableInformationForm);
						}
						if (employeeFormIds.contains(dBTableInformationForm
								.getFormId())) {
							dbTableInformationEmployeeFormList
									.add(dBTableInformationForm);
						}

					}
					if (dBTableInformationForm.getExcelExpFieldEntityName()
							.equalsIgnoreCase(
									PayAsiaConstants.COMPANY_ENTITY_NAME)) {
						dbTableInformationCompanyFormList
								.add(dBTableInformationForm);
						companyFieldPositon.add(positionCount);
					}
					positionCount++;
				}
				int employeeCompanyColumnCount = 0;

				List<DataDictionary> dataDictionaryList = dataDictionaryDAO
						.findByCompanyEntityForm(Long.parseLong(cmpId), null,
								null);

				List<MultiLingualData> multiLingualDataList = multiLingualDataDAO
						.findByLanguageEntityCompany(languageId,
								Long.parseLong(cmpId), null);

				for (DBTableInformationForm dBTableInformationForm : dbTableInformationCompanyFormList) {

					DataDictionary dataDictionary = companyDataDictionary
							.get(dBTableInformationForm.getDataDictionaryName());

					Cell headerCell = headerRow.createCell(fieldPosition
							.get(dBTableInformationForm.getXlFeild()));
					headerCell.setCellStyle(headerCellStyle);

					if (dataDictionary == null) {

						setNonExistingField(colMapForCompany,
								dBTableInformationForm,
								String.valueOf(employeeCompanyColumnCount));
						headerCell.setCellValue(dBTableInformationForm
								.getXlFeild());

					} else {

						if (dataDictionary.getFieldType().equalsIgnoreCase(
								PayAsiaConstants.STATIC_TYPE)) {
							setStaticDictionary(colMapForCompany,
									dBTableInformationForm, dataDictionary);
						} else {
							setDynamicDictionaryGroup(colMapForCompany,
									dBTableInformationForm, dataDictionary,
									dynamicFormMapCompany, dataDictionaryList,
									multiLingualDataList, companyFormIds,
									dataExportForm.getCategory());

						}
						if (colMapForCompany.get(
								dBTableInformationForm.getXlFeild()
										+ dBTableInformationForm
												.getDataDictionaryId())
								.getTablePosition() != null) {

							tableKey = colMapForCompany.get(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId())
									.getFormId()
									+ colMapForCompany
											.get(dBTableInformationForm
													.getXlFeild()
													+ dBTableInformationForm
															.getDataDictionaryId())
											.getTablePosition();
							tableRecordInfoCompany.put(tableKey,
									colMapForCompany.get(dBTableInformationForm
											.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId()));

						}

						if (colMapForCompany
								.get(dBTableInformationForm.getXlFeild()
										+ dBTableInformationForm
												.getDataDictionaryId())
								.getFieldType()
								.equalsIgnoreCase(
										PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
							headerCell.setCellValue(dBTableInformationForm
									.getXlFeild() + " Code Description");

						} else {
							headerCell.setCellValue(dBTableInformationForm
									.getXlFeild());
						}

					}

					sheet.autoSizeColumn(employeeCompanyColumnCount);
					employeeCompanyColumnCount++;

				}

				for (DBTableInformationForm dBTableInformationForm : dbTableInformationEmployeeFormList) {

					DataDictionary dataDictionary = employeeDataDictionary
							.get(dBTableInformationForm.getDataDictionaryName()
									.toLowerCase());

					Cell headerCell = headerRow.createCell(fieldPosition
							.get(dBTableInformationForm.getXlFeild()));
					headerCell.setCellStyle(headerCellStyle);

					if (dataDictionary == null) {

						setNonExistingField(colMapForEmployee,
								dBTableInformationForm,
								String.valueOf(employeeCompanyColumnCount));
						headerCell.setCellValue(dBTableInformationForm
								.getXlFeild());

					} else {

						if (dataDictionary.getFieldType().equalsIgnoreCase(
								PayAsiaConstants.STATIC_TYPE)) {
							setStaticDictionary(colMapForEmployee,
									dBTableInformationForm, dataDictionary);
							if (dataDictionary.getColumnName().equals(
									PayAsiaConstants.PAYASIA_EMPLOYEE_NUMBER)) {
								employeeNumberColumnCnt = employeeCompanyColumnCount;
							}

						} else {
							setDynamicDictionaryGroup(colMapForEmployee,
									dBTableInformationForm, dataDictionary,
									dynamicFormMapEmployee, dataDictionaryList,
									multiLingualDataList, employeeFormIds,
									dataExportForm.getCategory());

						}
						if (colMapForEmployee.get(
								dBTableInformationForm.getXlFeild()
										+ dBTableInformationForm
												.getDataDictionaryId())
								.getTablePosition() != null) {

							tableKey = colMapForEmployee.get(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId())
									.getFormId()
									+ colMapForEmployee
											.get(dBTableInformationForm
													.getXlFeild()
													+ dBTableInformationForm
															.getDataDictionaryId())
											.getTablePosition();
							tableRecordInfoEmployee
									.put(tableKey,
											colMapForEmployee.get(dBTableInformationForm
													.getXlFeild()
													+ dBTableInformationForm
															.getDataDictionaryId()));

						}

						if (colMapForEmployee
								.get(dBTableInformationForm.getXlFeild()
										+ dBTableInformationForm
												.getDataDictionaryId())
								.getFieldType()
								.equalsIgnoreCase(
										PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
							headerCell.setCellValue(dBTableInformationForm
									.getXlFeild() + " Code Description");

						} else {
							headerCell.setCellValue(dBTableInformationForm
									.getXlFeild());
						}

					}

					sheet.autoSizeColumn(employeeCompanyColumnCount);
					employeeCompanyColumnCount++;

				}

			} else {

				if (dataExportForm.getCategory().equals(
						PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {

					formIds = generalLogic.getAdminAuthorizedSectionIdList(
							employeeId, Long.parseLong(cmpId),
							dataExportForm.getEntityId());
				} else {
					formIds = dynamicFormDAO
							.getDistinctFormId(Long.parseLong(cmpId),
									dataExportForm.getEntityId());
				}

				colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

				tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
				company = companyDAO.findById(Long.parseLong(cmpId));

				dbTableInformationFormList = dataExportForm
						.getDbTableInformationFormList();

				List<DBTableInformationForm> dbTableInformationFormEmpList = new ArrayList<>();
				for (DBTableInformationForm dBTableInformationForm : dbTableInformationFormList) {
					if (dataExportForm.getCategory().equals(
							PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
						if (dBTableInformationForm.getFormId() == null) {
							dbTableInformationFormEmpList
									.add(dBTableInformationForm);
						}

						if (formIds
								.contains(dBTableInformationForm.getFormId())) {
							dbTableInformationFormEmpList
									.add(dBTableInformationForm);
						}
					} else {
						dbTableInformationFormEmpList
								.add(dBTableInformationForm);
					}

				}

				tableElements = new ArrayList<DataImportKeyValueDTO>();
				int columnCount = 0;
				for (DBTableInformationForm dBTableInformationForm : dbTableInformationFormEmpList) {

					DataDictionary dataDictionary = dataDictionaryDAO
							.findById(dBTableInformationForm
									.getDataDictionaryId());

					Cell headerCell = headerRow.createCell(columnCount);
					headerCell.setCellStyle(headerCellStyle);

					if (dataDictionary == null) {
						setNonExistingField(colMap, dBTableInformationForm,
								String.valueOf(columnCount));
						headerCell.setCellValue(dBTableInformationForm
								.getXlFeild());

					} else {

						if (dataDictionary.getFieldType().equalsIgnoreCase(
								PayAsiaConstants.STATIC_TYPE)) {
							setStaticDictionary(colMap, dBTableInformationForm,
									dataDictionary);
							if (dataDictionary.getColumnName().equals(
									PayAsiaConstants.PAYASIA_EMPLOYEE_NUMBER)) {
								employeeNumberColumnCnt = columnCount;
							}
						} else {
							setDynamicDictionary(colMap,
									dBTableInformationForm, dataDictionary,
									tabCacheMap, formIds,
									dataExportForm.getCategory());

						}
						if (colMap.get(
								dBTableInformationForm.getXlFeild()
										+ dBTableInformationForm
												.getDataDictionaryId())
								.getTablePosition() != null) {

							tableKey = colMap.get(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId())
									.getFormId()
									+ colMap.get(
											dBTableInformationForm.getXlFeild()
													+ dBTableInformationForm
															.getDataDictionaryId())
											.getTablePosition();
							tableRecordInfoFrom.put(tableKey, colMap
									.get(dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId()));

						}

						if (colMap
								.get(dBTableInformationForm.getXlFeild()
										+ dBTableInformationForm
												.getDataDictionaryId())
								.getFieldType()
								.equalsIgnoreCase(
										PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
							headerCell.setCellValue(dBTableInformationForm
									.getXlFeild() + " Code Description");

						} else {
							headerCell.setCellValue(dBTableInformationForm
									.getXlFeild());
						}

					}

					sheet.autoSizeColumn(columnCount);
					columnCount++;

				}

			}

			finalFilterList = new ArrayList<ExcelExportFiltersForm>();

			excelExportFilterList = dataExportForm.getExcelExportFilterList();

			for (ExcelExportFiltersForm excelExportFiltersForm : excelExportFilterList) {

				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(excelExportFiltersForm.getDictionaryId());
				DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
				if (dataDictionary.getFieldType().equalsIgnoreCase(
						PayAsiaConstants.STATIC_TYPE)) {

					getStaticDictionaryInfo(dataDictionary,
							dataImportKeyValueDTO);

				} else {

					getDynamicDictionaryInfo(dataDictionary,
							dataImportKeyValueDTO);

				}

				for (ExcelExportFiltersForm exportFilter : exportFilterlist) {
					if (exportFilter.getExportFilterId() == excelExportFiltersForm
							.getExportFilterId()) {
						try {
							excelExportFiltersForm.setValue(URLDecoder.decode(
									exportFilter.getValue(), "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							LOGGER.error(e.getMessage(), e);
						}
					}
				}

				excelExportFiltersForm
						.setDataImportKeyValueDTO(dataImportKeyValueDTO);
				finalFilterList.add(excelExportFiltersForm);
			}

			if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				EmployeeShortListDTO employeeShortListDTO = generalLogic
						.getShortListEmployeeIds(employeeId,
								Long.valueOf(cmpId));

				tupleList.addAll(employeeDAO.findByConditionGroup(colMap,
						formIds, Long.parseLong(cmpId), finalFilterList,
						tableRecordInfoFrom, tableElements,
						employeeShortListDTO));
			} else if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.COMPANY_ENTITY_NAME)) {

				tupleList.addAll(companyDAO.findByConditionGroupCompanyId(
						colMap, formIds, finalFilterList,
						Long.parseLong(cmpId), tableRecordInfoFrom));
			} else if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {

				tupleList.addAll(payslipDAO.findByCondition(colMap, formIds,
						finalFilterList, companyId));
			} else if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.PAY_DATA_COLLECTION_ENTITY_NAME)) {
				tupleList.addAll(payDataCollectionDAO.findByCondition(colMap,
						finalFilterList, companyId, company.getDateFormat()));
			} else if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {

				EmployeeCompanyTupleDTO employeeCompanyTupleDTO = new EmployeeCompanyTupleDTO();
				if (colMapForCompany.size() != 0) {
					employeeCompanyTupleDTO.setTupleListCompany(companyDAO
							.findByConditionGroupCompanyId(colMapForCompany,
									companyFormIds,
									new ArrayList<ExcelExportFiltersForm>(),
									Long.parseLong(cmpId),
									tableRecordInfoCompany));
				}
				if (colMapForEmployee.size() != 0) {
					EmployeeShortListDTO employeeShortListDTO = generalLogic
							.getShortListEmployeeIds(employeeId,
									Long.valueOf(cmpId));
					employeeCompanyTupleDTO.setTupleListEmployee(employeeDAO
							.findByConditionGroup(colMapForEmployee,
									employeeFormIds, Long.parseLong(cmpId),
									finalFilterList, tableRecordInfoEmployee,
									tableElements, employeeShortListDTO));
				}

				tupleListColMap.put(Long.parseLong(cmpId),
						employeeCompanyTupleDTO);

			}

		}

		SimpleDateFormat sdf = new SimpleDateFormat(company.getDateFormat());
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
				company.getDateFormat()));
		SimpleDateFormat sdfSource = new SimpleDateFormat(
				PayAsiaConstants.EFFECTIVE_DATE_FORMAT);
		SimpleDateFormat serverSDF = new SimpleDateFormat(
				PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
		Date effectiveFormatDate;
		Date serverSideDate;
		String strObject;
		int colCount = 0;
		int rowCount = 1;
		if (dataExportForm.getCategory().equals(
				PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {

			for (Map.Entry<Long, EmployeeCompanyTupleDTO> entry : tupleListColMap
					.entrySet()) {

				EmployeeCompanyTupleDTO employeeCompanyTupleDTO = entry
						.getValue();
				List<Object[]> tupleListEmp = employeeCompanyTupleDTO
						.getTupleListEmployee();
				List<Object[]> tupleListCmp = employeeCompanyTupleDTO
						.getTupleListCompany();
				if (tupleListEmp != null && !tupleListEmp.isEmpty()) {
					if (!(tupleListEmp.get(0) instanceof Object[])) {
						List<Object[]> tupleListEmpMod = new ArrayList<>();
						for (Object obj : tupleListEmp) {
							Object[] objArr = new Object[1];
							objArr[0] = obj;
							tupleListEmpMod.add(objArr);
						}

						tupleListEmp = tupleListEmpMod;
					}
				}
				if (tupleListCmp != null && !tupleListCmp.isEmpty()) {
					if (!(tupleListCmp.get(0) instanceof Object[])) {
						List<Object[]> tupleListCmpMod = new ArrayList<>();
						for (Object obj : tupleListCmp) {
							Object[] objArr = new Object[1];
							objArr[0] = obj;
							tupleListCmpMod.add(objArr);
						}

						tupleListCmp = tupleListCmpMod;
					}
				}
				for (Object[] tuple : tupleListEmp) {
					Integer empFieldPositionCount = 0;
					Row newRow = sheet.createRow(rowCount);
					colCount = 0;

					for (Object[] tupleCompany : tupleListCmp) {
						Integer cmpFieldPositionCount = 0;
						for (Object object : tupleCompany) {

							Cell headerCell = newRow
									.createCell(companyFieldPositon
											.get(cmpFieldPositionCount));
							if (object != null) {

								strObject = String.valueOf(object);

								if (object instanceof Boolean) {

									if (object.equals(true)) {
										headerCell
												.setCellValue(PayAsiaConstants.PAYASIA_ENABLE);
									} else {
										headerCell
												.setCellValue(PayAsiaConstants.PAYASIA_DISABLE);
									}

								} else if (object instanceof Date) {
									headerCell.setCellStyle(cellStyle);
									headerCell.setCellValue(DateUtils
											.stringToDate(sdf.format(object),
													company.getDateFormat()));

								} else if (NumberUtils.isNumber(strObject)) {
									double cellValue = Double
											.valueOf(strObject);
									if (String.valueOf(cellValue).equals(
											strObject)) {
										headerCell
												.setCellType(Cell.CELL_TYPE_NUMERIC);
										headerCell.setCellValue(cellValue);
									} else {
										headerCell
												.setCellType(Cell.CELL_TYPE_STRING);
										headerCell.setCellValue(strObject);
									}

								} else {
									headerCell.setCellValue(strObject);

									try {
										effectiveFormatDate = sdfSource
												.parse(strObject);
										headerCell.setCellValue(sdf
												.format(effectiveFormatDate));
										headerCell.setCellStyle(cellStyle);
									} catch (ParseException parseException) {
										headerCell.setCellValue(strObject);
									}

									try {
										serverSideDate = serverSDF
												.parse(strObject);
										headerCell
												.setCellValue(DateUtils.stringToDate(
														sdf.format(serverSideDate),
														company.getDateFormat()));
										headerCell.setCellStyle(cellStyle);
									} catch (ParseException parseException) {
										headerCell.setCellValue(strObject);
									}

								}

							} else {
								headerCell.setCellValue("");
							}

							colCount++;
							cmpFieldPositionCount++;
						}
					}

					for (Object object : tuple) {

						Cell headerCell = newRow
								.createCell(employeeFieldPositon
										.get(empFieldPositionCount));
						if (object != null) {

							strObject = String.valueOf(object);

							if (object instanceof Boolean) {

								if (object.equals(true)) {
									headerCell
											.setCellValue(PayAsiaConstants.PAYASIA_ENABLE);
								} else {
									headerCell
											.setCellValue(PayAsiaConstants.PAYASIA_DISABLE);
								}

							} else if (object instanceof Date) {
								headerCell.setCellStyle(cellStyle);
								headerCell.setCellValue(DateUtils.stringToDate(
										sdf.format(object),
										company.getDateFormat()));

							} else if (employeeNumberColumnCnt != null
									&& employeeNumberColumnCnt == colCount) {
								headerCell.setCellValue(strObject);
							}

							else if (NumberUtils.isNumber(strObject)) {
								double cellValue = Double.valueOf(strObject);
								if (String.valueOf(cellValue).equals(strObject)) {
									headerCell
											.setCellType(Cell.CELL_TYPE_NUMERIC);
									headerCell.setCellValue(cellValue);
								} else {
									headerCell
											.setCellType(Cell.CELL_TYPE_STRING);
									headerCell.setCellValue(strObject);
								}
							} else {
								headerCell.setCellValue(strObject);

								try {
									effectiveFormatDate = sdfSource
											.parse(strObject);
									headerCell.setCellValue(sdf
											.format(effectiveFormatDate));
									headerCell.setCellStyle(cellStyle);
								} catch (ParseException parseException) {
									headerCell.setCellValue(strObject);
								}

								try {
									serverSideDate = serverSDF.parse(strObject);
									headerCell.setCellValue(DateUtils
											.stringToDate(
													sdf.format(serverSideDate),
													company.getDateFormat()));
									headerCell.setCellStyle(cellStyle);
								} catch (ParseException parseException) {
									headerCell.setCellValue(strObject);
								}

							}

						} else {
							headerCell.setCellValue("");
						}

						colCount++;
						empFieldPositionCount++;
					}
					rowCount++;
				}

				for (int count = 0; count < colCount; count++) {
					sheet.autoSizeColumn(count);
				}

			}

		} else {

			if (tupleList != null && !tupleList.isEmpty()) {
				if (!(tupleList.get(0) instanceof Object[])) {
					List<Object[]> tupleListMod = new ArrayList<>();
					for (Object obj : tupleList) {
						Object[] objArr = new Object[1];
						objArr[0] = obj;
						tupleListMod.add(objArr);
					}

					tupleList = tupleListMod;
				}
			}

			for (Object[] tuple : tupleList) {
				Row newRow = sheet.createRow(rowCount);

				colCount = 0;
				for (Object object : tuple) {

					Cell headerCell = newRow.createCell(colCount);
					if (object != null) {

						strObject = String.valueOf(object);

						if (object instanceof Boolean) {

							if (object.equals(true)) {
								headerCell
										.setCellValue(PayAsiaConstants.PAYASIA_ENABLE);
							} else {
								headerCell
										.setCellValue(PayAsiaConstants.PAYASIA_DISABLE);
							}

						} else if (employeeNumberColumnCnt != null
								&& employeeNumberColumnCnt == colCount) {
							headerCell.setCellValue(strObject);
						}

						else if (NumberUtils.isNumber(strObject)) {
							double cellValue = Double.valueOf(strObject);
							if (String.valueOf(cellValue).equals(strObject)) {
								headerCell.setCellType(Cell.CELL_TYPE_NUMERIC);
								headerCell.setCellValue(cellValue);
							} else {
								headerCell.setCellType(Cell.CELL_TYPE_STRING);
								headerCell.setCellValue(strObject);
							}
						} else if (object instanceof Date) {
							headerCell.setCellStyle(cellStyle);
							headerCell
									.setCellValue(DateUtils.stringToDate(
											sdf.format(object),
											company.getDateFormat()));

						} else {
							headerCell.setCellValue(strObject);

							try {
								effectiveFormatDate = sdfSource
										.parse(strObject);
								headerCell.setCellValue(sdf
										.format(effectiveFormatDate));
								headerCell.setCellStyle(cellStyle);
							} catch (ParseException parseException) {
								headerCell.setCellValue(strObject);
							}

							try {
								serverSideDate = serverSDF.parse(strObject);
								headerCell.setCellValue(DateUtils.stringToDate(
										sdf.format(serverSideDate),
										company.getDateFormat()));
								headerCell.setCellStyle(cellStyle);
							} catch (ParseException parseException) {
								headerCell.setCellValue(strObject);
							}

						}

					} else {
						headerCell.setCellValue("");
					}

					colCount++;
				}

				rowCount++;
			}

			for (int count = 0; count < colCount; count++) {
				sheet.autoSizeColumn(count);
			}

		}

		DataExportForm exportForm = new DataExportForm();
		String fileName = "";
		if (dataExportForm.isIncludePrefix()) {
			fileName = fileName + dataExportForm.getPrefix();
		} else if (dataExportForm.isIncludeTemplateNameAsPrefix()) {
			fileName = fileName + dataExportForm.getTemplateName();
		}

		if (dataExportForm.isIncludeSuffix()) {
			fileName = fileName + dataExportForm.getSuffix();
		} else if (dataExportForm.isIncludeTimestampAsSuffix()) {
			fileName = fileName
					+ new SimpleDateFormat(
							PayAsiaConstants.FILE_NAME_TIMESTAMP_FORMAT)
							.format(new Date());
		}

		if (StringUtils.isBlank(fileName)) {
			fileName = PayAsiaConstants.EXPORT_FILE_DEFAULT_TEMPLATE_NAME;
		}
		exportForm.setFinalFileName(fileName);
		exportForm.setWorkbook(wb);

		return exportForm;
	}

	private String replaceSpecialChar(String keyName) {
		keyName = keyName.replace("?", "");
		keyName = keyName.replace("%", "");
		keyName = keyName.replace("'", "");
		return keyName;
	}

	private void setNonExistingField(Map<String, DataImportKeyValueDTO> colMap,
			DBTableInformationForm dBTableInformationForm, String coulumnCount) {

		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		try {

			dataImportKeyValueDTO
					.setFieldType(PayAsiaConstants.FIELD_NOT_EXISTS);
			dataImportKeyValueDTO
					.setMethodName(PayAsiaConstants.FIELD_NOT_EXISTS);
			dataImportKeyValueDTO.setStatic(false);
			dataImportKeyValueDTO.setActualColName(dBTableInformationForm
					.getXlFeild());
			colMap.put(dBTableInformationForm.getXlFeild() + coulumnCount,
					dataImportKeyValueDTO);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

	}

	/**
	 * Gets the dynamic dictionary info.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 * @param dataImportKeyValueDTO
	 *            the data import key value dto
	 * @return the dynamic dictionary info
	 */
	private void getDynamicDictionaryInfo(DataDictionary dataDictionary,
			DataImportKeyValueDTO dataImportKeyValueDTO) {
		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(dataDictionary
				.getCompany().getCompanyId(), dataDictionary.getEntityMaster()
				.getEntityId(), dataDictionary.getFormID());

		DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
				dataDictionary.getCompany().getCompanyId(), dataDictionary
						.getEntityMaster().getEntityId(), maxVersion,
				dataDictionary.getFormID());

		Tab tab = dataExportUtils.getTabObject(dynamicForm);

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.LABEL_FIELD_TYPE)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
							.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormId(dynamicForm.getId()
							.getFormId());

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					|| StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (new String(Base64.decodeBase64(column
							.getDictionaryName().getBytes()))
							.equals(dataDictionary.getDataDictName())) {
						dataImportKeyValueDTO.setStatic(false);
						dataImportKeyValueDTO.setChild(true);
						dataImportKeyValueDTO.setFieldType(column.getType());
						dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
								.getColNumber(column.getName()));
						dataImportKeyValueDTO.setFormId(dynamicForm.getId()
								.getFormId());
						dataImportKeyValueDTO
								.setTablePosition(PayAsiaStringUtils
										.getColNumber(field.getName()));
					}
				}
			}
		}
	}

	/**
	 * Purpose: To get the static dictionary information.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 * @param dataImportKeyValueDTO
	 *            the data import key value dto
	 * @return the static dictionary info
	 */
	private void getStaticDictionaryInfo(DataDictionary dataDictionary,
			DataImportKeyValueDTO dataImportKeyValueDTO) {
		try {
			ColumnPropertyDTO colProp = generalDAO.getColumnProperties(
					dataDictionary.getTableName(),
					dataDictionary.getColumnName());
			dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
			dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
					.getCamelCase(dataDictionary.getColumnName()));
			dataImportKeyValueDTO.setStatic(true);
			dataImportKeyValueDTO.setActualColName(dataDictionary
					.getColumnName());
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.DataExportLogic#setDynamicDictionary(java.util.Map,
	 * com.payasia.common.form.DBTableInformationForm,
	 * com.payasia.dao.bean.DataDictionary)
	 */
	@Override
	public void setDynamicDictionary(Map<String, DataImportKeyValueDTO> colMap,
			DBTableInformationForm dBTableInformationForm,
			DataDictionary dataDictionary, Map<Long, Tab> tabCacheMap,
			List<Long> authorizedFormIds, String entityName) {

		Long formId = dataDictionary.getFormID();
		Tab tab = null;

		if (tabCacheMap.containsKey(formId)) {
			tab = tabCacheMap.get(formId);
		} else {
			int maxVersion = dynamicFormDAO.getMaxVersionByFormId(
					dataDictionary.getCompany().getCompanyId(), dataDictionary
							.getEntityMaster().getEntityId(), dataDictionary
							.getFormID());

			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
					dataDictionary.getCompany().getCompanyId(), dataDictionary
							.getEntityMaster().getEntityId(), maxVersion,
					dataDictionary.getFormID());

			tab = dataExportUtils.getTabObject(dynamicForm);
			tabCacheMap.put(formId, tab);
		}

		List<Field> listOfFields = tab.getField();
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.LABEL_FIELD_TYPE)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					String colNumber = PayAsiaStringUtils.getColNumber(field
							.getName());
					dataImportKeyValueDTO.setMethodName(colNumber);
					dataImportKeyValueDTO.setActualColName(new String(Base64
							.decodeBase64(field.getLabel().getBytes())));
					dataImportKeyValueDTO.setFormId(formId);
					if (field.getType().equalsIgnoreCase(
							PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {

						StringBuilder empLstField = new StringBuilder();
						empLstField.append("employee_");
						empLstField.append(formId);
						empLstField.append(".first_name + ");
						empLstField.append("employee_");
						empLstField.append(formId);
						empLstField.append(".last_name + ");
						empLstField.append(" '[' + ");
						empLstField.append("employee_");
						empLstField.append(formId);
						empLstField.append(".employee_number + ");
						empLstField.append("']'");
						dataImportKeyValueDTO.setEmpLstSelectField(String
								.valueOf(empLstField));

						StringBuilder empLstFromDynField = new StringBuilder();
						empLstFromDynField.append("dynamicFormRecord")
								.append(formId).append(".COL_")
								.append(colNumber);

						StringBuilder empLstFromEmpField = new StringBuilder();
						empLstFromEmpField.append("employee_").append(formId);
						dataImportKeyValueDTO.setEmpLstFromDynField(String
								.valueOf(empLstFromDynField));
						dataImportKeyValueDTO.setEmpLstFromEmpField(String
								.valueOf(empLstFromEmpField));

					}

					if (entityName
							.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)
							|| entityName
									.equalsIgnoreCase(PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {
						if (authorizedFormIds.contains(formId)) {
							colMap.put(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId(),
									dataImportKeyValueDTO);
						}
					} else {
						colMap.put(dBTableInformationForm.getXlFeild()
								+ dBTableInformationForm.getDataDictionaryId(),
								dataImportKeyValueDTO);
					}

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					|| StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (new String(Base64.decodeBase64(column
							.getDictionaryName().getBytes()))
							.equals(dataDictionary.getDataDictName())) {
						dataImportKeyValueDTO.setStatic(false);
						dataImportKeyValueDTO.setChild(true);
						dataImportKeyValueDTO.setFieldType(column.getType());
						String colNumber = PayAsiaStringUtils
								.getColNumber(column.getName());
						dataImportKeyValueDTO.setMethodName(colNumber);
						dataImportKeyValueDTO.setFormId(formId);
						dataImportKeyValueDTO
								.setActualColName(new String(Base64
										.decodeBase64(field.getLabel()
												.getBytes())));
						String tablePosition = PayAsiaStringUtils
								.getColNumber(field.getName());
						dataImportKeyValueDTO.setTablePosition(tablePosition);
						if (column.getType().equalsIgnoreCase(
								PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {

							StringBuilder empLstField = new StringBuilder();
							empLstField.append("employee_");
							empLstField.append(formId);
							empLstField.append(tablePosition);

							empLstField.append(".first_name + ");
							empLstField.append("employee_");
							empLstField.append(formId);
							empLstField.append(tablePosition);
							empLstField.append(".last_name + ");
							empLstField.append(" '[' + ");
							empLstField.append("employee_");
							empLstField.append(formId);
							empLstField.append(tablePosition);
							empLstField.append(".employee_number + ");
							empLstField.append(" ']' ");
							dataImportKeyValueDTO.setEmpLstSelectField(String
									.valueOf(empLstField));
							StringBuilder empLstFromDynField = new StringBuilder();
							empLstFromDynField
									.append("dynamicFormTableRecord_")
									.append(formId).append(tablePosition)
									.append(".COL_").append(colNumber);
							StringBuilder empLstFromEmpField = new StringBuilder();
							empLstFromEmpField.append("employee_")
									.append(formId).append(tablePosition);
							dataImportKeyValueDTO.setEmpLstFromDynField(String
									.valueOf(empLstFromDynField));
							dataImportKeyValueDTO.setEmpLstFromEmpField(String
									.valueOf(empLstFromEmpField));

						}
						if (entityName
								.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)
								|| entityName
										.equalsIgnoreCase(PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {
							if (authorizedFormIds.contains(formId)) {
								colMap.put(
										dBTableInformationForm.getXlFeild()
												+ dBTableInformationForm
														.getDataDictionaryId(),
										dataImportKeyValueDTO);
							}
						} else {
							colMap.put(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId(),
									dataImportKeyValueDTO);
						}

					}
				}
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.DataExportLogic#setDynamicDictionary(java.util.Map,
	 * com.payasia.common.form.DBTableInformationForm,
	 * com.payasia.dao.bean.DataDictionary)
	 */
	@Override
	public void setDynamicDictionaryGroup(
			Map<String, DataImportKeyValueDTO> colMap,
			DBTableInformationForm dBTableInformationForm,
			DataDictionary dataDictionary,
			Map<Long, DynamicForm> dynamicFormMap,
			List<DataDictionary> dataDictionaryList,
			List<MultiLingualData> multiLingualDataList,
			List<Long> authorizedFormIds, String entityName) {

		DynamicForm dynamicForm = dynamicFormMap
				.get(dataDictionary.getFormID());
		Tab tab = dataExportUtils.getTabObjectDataExportGroup(dynamicForm,
				dataDictionaryList, multiLingualDataList);

		List<Field> listOfFields = tab.getField();
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.LABEL_FIELD_TYPE)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					String colNumber = PayAsiaStringUtils.getColNumber(field
							.getName());
					dataImportKeyValueDTO.setMethodName(colNumber);
					dataImportKeyValueDTO.setActualColName(new String(Base64
							.decodeBase64(field.getLabel().getBytes())));
					Long formId = dynamicForm.getId().getFormId();
					dataImportKeyValueDTO.setFormId(formId);

					if (field.getType().equalsIgnoreCase(
							PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {

						StringBuilder empLstField = new StringBuilder();
						empLstField.append("employee_");
						empLstField.append(formId);
						empLstField.append(".first_name + ");
						empLstField.append("employee_");
						empLstField.append(formId);
						empLstField.append(".last_name + ");
						empLstField.append(" '[' + ");
						empLstField.append("employee_");
						empLstField.append(formId);
						empLstField.append(".employee_number + ");
						empLstField.append("']'");
						dataImportKeyValueDTO.setEmpLstSelectField(String
								.valueOf(empLstField));

						StringBuilder empLstFromDynField = new StringBuilder();
						empLstFromDynField.append("dynamicFormRecord")
								.append(formId).append(".COL_")
								.append(colNumber);

						StringBuilder empLstFromEmpField = new StringBuilder();
						empLstFromEmpField.append("employee_").append(formId);
						dataImportKeyValueDTO.setEmpLstFromDynField(String
								.valueOf(empLstFromDynField));
						dataImportKeyValueDTO.setEmpLstFromEmpField(String
								.valueOf(empLstFromEmpField));

					}

					if (entityName
							.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)
							|| entityName
									.equalsIgnoreCase(PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {
						if (authorizedFormIds.contains(dynamicForm.getId()
								.getFormId())) {
							colMap.put(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId(),
									dataImportKeyValueDTO);
						}
					} else {
						colMap.put(dBTableInformationForm.getXlFeild()
								+ dBTableInformationForm.getDataDictionaryId(),
								dataImportKeyValueDTO);
					}

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					|| StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (new String(Base64.decodeBase64(column
							.getDictionaryName().getBytes()))
							.equals(dataDictionary.getDataDictName())) {
						dataImportKeyValueDTO.setStatic(false);
						dataImportKeyValueDTO.setChild(true);
						dataImportKeyValueDTO.setFieldType(column.getType());
						String colNumber = PayAsiaStringUtils
								.getColNumber(column.getName());
						dataImportKeyValueDTO.setMethodName(colNumber);
						dataImportKeyValueDTO
								.setActualColName(new String(Base64
										.decodeBase64(field.getLabel()
												.getBytes())));
						Long formId = dynamicForm.getId().getFormId();
						dataImportKeyValueDTO.setFormId(formId);

						String tablePosition = PayAsiaStringUtils
								.getColNumber(field.getName());
						dataImportKeyValueDTO.setTablePosition(tablePosition);

						if (column.getType().equalsIgnoreCase(
								PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {

							StringBuilder empLstField = new StringBuilder();
							empLstField.append("employee_");
							empLstField.append(formId);
							empLstField.append(tablePosition);

							empLstField.append(".first_name + ");
							empLstField.append("employee_");
							empLstField.append(formId);
							empLstField.append(tablePosition);
							empLstField.append(".last_name + ");
							empLstField.append(" '[' + ");
							empLstField.append("employee_");
							empLstField.append(formId);
							empLstField.append(tablePosition);
							empLstField.append(".employee_number + ");
							empLstField.append(" ']' ");
							dataImportKeyValueDTO.setEmpLstSelectField(String
									.valueOf(empLstField));
							StringBuilder empLstFromDynField = new StringBuilder();
							empLstFromDynField
									.append("dynamicFormTableRecord_")
									.append(formId).append(tablePosition)
									.append(".COL_").append(colNumber);
							StringBuilder empLstFromEmpField = new StringBuilder();
							empLstFromEmpField.append("employee_")
									.append(formId).append(tablePosition);
							dataImportKeyValueDTO.setEmpLstFromDynField(String
									.valueOf(empLstFromDynField));
							dataImportKeyValueDTO.setEmpLstFromEmpField(String
									.valueOf(empLstFromEmpField));

						}

						if (entityName
								.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)
								|| entityName
										.equalsIgnoreCase(PayAsiaConstants.COMPANY_AND_EMPLOYEE_ENTITY_NAME)) {
							if (authorizedFormIds.contains(dynamicForm.getId()
									.getFormId())) {
								colMap.put(
										dBTableInformationForm.getXlFeild()
												+ dBTableInformationForm
														.getDataDictionaryId(),
										dataImportKeyValueDTO);
							}
						} else {
							colMap.put(
									dBTableInformationForm.getXlFeild()
											+ dBTableInformationForm
													.getDataDictionaryId(),
									dataImportKeyValueDTO);
						}

					}
				}
			}
		}
	}

	/**
	 * Purpose: To set the static dictionary information.
	 * 
	 * @param colMap
	 *            the col map
	 * @param dBTableInformationForm
	 *            the d b table information form
	 * @param dataDictionary
	 *            the data dictionary
	 */
	private void setStaticDictionary(Map<String, DataImportKeyValueDTO> colMap,
			DBTableInformationForm dBTableInformationForm,
			DataDictionary dataDictionary) {
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		try {
			ColumnPropertyDTO colProp = generalDAO.getColumnProperties(
					dataDictionary.getTableName(),
					dataDictionary.getColumnName());
			dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
			dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
					.getCamelCase(dataDictionary.getColumnName()));
			dataImportKeyValueDTO.setStatic(true);
			dataImportKeyValueDTO.setActualColName(dataDictionary
					.getColumnName());
			colMap.put(dBTableInformationForm.getXlFeild()
					+ dBTableInformationForm.getDataDictionaryId(),
					dataImportKeyValueDTO);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
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
	 * Comparator Class for Ordering EmpDataExportTemplateField List.
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
			if (templateField.getExportFieldId() > compWithTemplateField
					.getExportFieldId()) {
				return 1;
			} else if (templateField.getExportFieldId() < compWithTemplateField
					.getExportFieldId()) {
				return -1;
			}
			return 0;

		}

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
	
	@Override
	public boolean isAdminAuthorizedForComTemplate(Long templateId,Long companyId)
	{
		if(templateId == null || companyId == null)
			return false;
		return generalDAO.isTemplateExistForCom(templateId,companyId);
		
		
	}

}
