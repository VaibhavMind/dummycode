package com.payasia.logic.data;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.DynamicFormTableRecordPK;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.impl.DataImportUtilsImpl;

public class DataImportUtilsAbstact {

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The general dao. */
	@Resource
	GeneralDAO generalDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The dynamic form table record dao. */
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	/** The dynamic form field ref value dao. */
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;

	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	private static final Logger LOGGER = Logger
			.getLogger(DataImportUtilsImpl.class);

	/**
	 * Purpose: To gets the column value from Existing DynamicFormRecord Entity.
	 * 
	 * @param colNumber
	 *            the col number
	 * @param existingFormRecord
	 *            the existing form record
	 * @return the col value file
	 */
	public String getColValueFile(String colNumber,
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

	/**
	 * Purpose: To gets the column value from Existing DynamicFormTableRecord
	 * Entity.
	 * 
	 * @param colNumber
	 *            the col number
	 * @param existingFormRecord
	 *            the existing form record
	 * @return the col value file
	 */

	public String getTableRecordValue(String colNumber,
			DynamicFormTableRecord existingFormRecord) {

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

	/**
	 * Purpose : Sets the dynamic field value.
	 * 
	 * @param empDataImportTemplateField
	 *            the emp data import template field
	 * @param colFormMapList
	 *            the col form map list
	 * @param tableNames
	 *            the table names
	 * @param value
	 *            the value
	 * @param formIds
	 *            the form ids
	 * @param companyId
	 *            the company id
	 */

	public void setDynamicFieldValue(
			EmpDataImportTemplateField empDataImportTemplateField,
			List<HashMap<String, String>> colFormMapList,
			List<String> tableNames, String value, List<Long> formIds,
			Long companyId, HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions) {

		HashMap<String, String> colFormMap = new HashMap<String, String>();
		long entityId = empDataImportTemplateField.getDataDictionary()
				.getEntityMaster().getEntityId();
		long formId = empDataImportTemplateField.getDataDictionary()
				.getFormID();
		colFormMap.put("formId", Long.toString(formId));
		colFormMap.put("value", value);
		if (!formIds.contains(formId)) {
			formIds.add(formId);
		}
		String dataDictionaryName = empDataImportTemplateField
				.getDataDictionary().getDataDictName();
		colFormMap.put("dataDictionaryId", Long
				.toString(empDataImportTemplateField.getDataDictionary()
						.getDataDictionaryId()));
		Tab tab = dynamicFormObjects.get(formId);
		int maxVersion = 0;
		if (tab == null) {

			synchronized (this) {
				maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId,
						entityId, formId);
			}

			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
					companyId, entityId, maxVersion, formId);

			Unmarshaller unmarshaller = null;

			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
				throw new PayAsiaSystemException(saxException.getMessage(),
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
				LOGGER.error(jaxbException.getMessage(), jaxbException);
				throw new PayAsiaSystemException(jaxbException.getMessage(),
						jaxbException);
			}
		} else {
			maxVersion = dynamicFormVersions.get(formId);
		}
		colFormMap.put("maxVersion", Integer.toString(maxVersion));
		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.LABEL_FIELD_TYPE)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionaryName)) {

					colFormMap.put("colName", field.getName());
					colFormMap.put("tableName", null);
					colFormMap.put("colLabel", empDataImportTemplateField
							.getDataDictionary().getLabel());
					colFormMap.put("colType", field.getType());

				}

			}

			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {
				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (new String(Base64.decodeBase64(column
							.getDictionaryName().getBytes()))
							.equals(dataDictionaryName)) {
						colFormMap.put("colName", column.getName());
						colFormMap.put("tableName", field.getName());
						colFormMap.put("colLabel", empDataImportTemplateField
								.getDataDictionary().getLabel());
						colFormMap.put("colType", column.getType());
						if (!tableNames.contains(field.getName())) {
							tableNames.add(field.getName());
						}
					}

				}

			}
		}

		colFormMapList.add(colFormMap);

	}

	/**
	 * Purpose : Sets the dynamic form record values from excel.
	 * 
	 * @param colNumber
	 *            the col number
	 * @param dynRecordsName
	 *            the dynamic records name
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param value
	 *            the value
	 */

	public void setDynamicFormRecordValues(String colNumber,
			List<String> dynRecordsName, DynamicFormRecord dynamicFormRecord,
			String value) {

		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
		String colMehtodName = "setCol" + colNumber;
		Method dynamicFormRecordMethod;
		dynRecordsName.add(colNumber);

		try {
			dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(
					colMehtodName, String.class);
			if (StringUtils.isNotBlank(value)) {
				value = value.trim();
			}
			dynamicFormRecordMethod.invoke(dynamicFormRecord, value);
		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

	}

	/**
	 * Purpose : Sets the table values in DynamicFormTableRecord object.
	 * 
	 * @param colFormMapList
	 *            the col form map list
	 * @param formId
	 *            the form id
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param tableName
	 *            the table name
	 * @param dynamicFormTableRecord
	 *            the dynamic form table record
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @return true, if successful
	 */

	public boolean setTableValues(List<HashMap<String, String>> colFormMapList,
			Long formId, DynamicFormRecord dynamicFormRecord, String tableName,
			DynamicFormTableRecord dynamicFormTableRecord,
			DataImportParametersDTO dataImportParametersDTO, Long companyId,
			String mode) {
		boolean containsTableVal = false;
		for (HashMap<String, String> colFormMap : colFormMapList) {
			String formIDString = colFormMap.get("formId");
			long formID = Long.parseLong(formIDString);
			if (formID == formId) {
				dynamicFormRecord.setVersion(Integer.parseInt(colFormMap
						.get("maxVersion")));
				if (colFormMap.get("tableName") != null) {
					containsTableVal = true;
					String colLabel = colFormMap.get("colLabel");
					if (tableName.equals(colFormMap.get("tableName"))) {

						String tableColName = colFormMap.get("colName");
						String tableRecordMethodName = "setCol"
								+ tableColName.substring(
										tableColName.lastIndexOf('_') + 1,
										tableColName.length());
						Method dynamicFormTableRecordMethod;
						try {
							dynamicFormTableRecordMethod = DynamicFormTableRecord.class
									.getMethod(tableRecordMethodName,
											String.class);
							String coLVal = colFormMap.get("value");
							if (StringUtils.isNotBlank(coLVal)) {
								coLVal = coLVal.trim();
							}
							String defaultValue = colFormMap
									.get("defaultValue");
							if (colFormMap.get("colType").equalsIgnoreCase(
									"codedesc")) {

								coLVal = getValForCodeDesc(
										dataImportParametersDTO, colFormMap,
										coLVal);

							} else if (colFormMap
									.get("colType")
									.equalsIgnoreCase(
											PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {

								Employee employee = employeeDAO.findByNumber(
										coLVal, companyId);
								if (employee != null) {
									coLVal = String.valueOf(employee
											.getEmployeeId());
								}

							}

							if (colLabel
									.equalsIgnoreCase(PayAsiaConstants.EFFECTIVE_DATE_LABEL)) {
								coLVal = DateUtils.appendTodayTime(coLVal);

							}

							if (StringUtils.isBlank(coLVal)
									&& ("insert".equalsIgnoreCase(mode) || "Dependent Relationship"
											.equalsIgnoreCase(colLabel))) {
								if (StringUtils.isNotBlank(defaultValue)) {
									coLVal = defaultValue;
									if (colFormMap.get("colType")
											.equalsIgnoreCase("codedesc")) {

										coLVal = getValForCodeDesc(
												dataImportParametersDTO,
												colFormMap, defaultValue);

									} else if (colFormMap.get("colType")
											.equalsIgnoreCase("employeelist")) {
										Employee employee = employeeDAO
												.findById(Long
														.parseLong(defaultValue));
										coLVal = String.valueOf(employee
												.getEmployeeId());

									}

								}
							}

							dynamicFormTableRecordMethod.invoke(
									dynamicFormTableRecord, coLVal);

						} catch (SecurityException | NoSuchMethodException
								| IllegalArgumentException
								| IllegalAccessException
								| InvocationTargetException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}
					}

				}
			}
		}
		return containsTableVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataImportUtils#handleException(java.lang.String,
	 * java.util.List, com.payasia.common.exception.PayAsiaSystemException)
	 */

	public void handleException(String rowNumber,
			List<DataImportLogDTO> updateAndInsertLogs,
			PayAsiaSystemException ex) {
		Throwable th = ex;
		if (ex.getCause().getClass().isInstance(PersistenceException.class)) {
			th = ex.getCause();
		}
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setFailureType("payasia.data.import.transaction");
		dataImportLogDTO.setFromMessageSource(false);
		dataImportLogDTO.setRemarks(th.getCause().getLocalizedMessage());
		if (dataImportLogDTO.getRemarks() == null) {
			dataImportLogDTO.setRemarks(th.getLocalizedMessage());
		}
		dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber) + 1);
		updateAndInsertLogs.add(dataImportLogDTO);
	}

	/**
	 * Sets the dynamic values.
	 * 
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param tableNames
	 *            the table names
	 * @param colFormMapList
	 *            the col form map list
	 * @param dynRecordsName
	 *            the dyn records name
	 * @param formId
	 *            the form id
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param existingFormRecord
	 *            the existing form record
	 */

	public void setDynamicValues(
			DataImportParametersDTO dataImportParametersDTO,
			List<String> tableNames,
			List<HashMap<String, String>> colFormMapList,
			List<String> dynRecordsName, Long formId,
			DynamicFormRecord dynamicFormRecord,
			DynamicFormRecord existingFormRecord, Long companyId, String mode) {
		DynamicFormTableRecord exDynamicFormTableRecord = null;
		boolean containsTableVal;
		List<DataImportLogDTO> insertOnlyLogs = new ArrayList<DataImportLogDTO>();
		for (String tableName : tableNames) {
			int seqNo = 1;
			DynamicFormTableRecord dynamicFormTableRecord = new DynamicFormTableRecord();
			DynamicFormTableRecordPK dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
			containsTableVal = setTableValues(colFormMapList, formId,
					dynamicFormRecord, tableName, dynamicFormTableRecord,
					dataImportParametersDTO, companyId, mode);
			int getMaxSequence = 0;

			if (containsTableVal) {
				synchronized (this) {
					Long maxTableRecordId = dynamicFormTableRecordDAO
							.getMaxTableRecordId() + 1;

					dynamicFormTableRecordPK.setSequence(seqNo);

					if (existingFormRecord == null) {
						dynamicFormTableRecordPK
								.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO
								.saveWithFlush(dynamicFormTableRecord);
					} else {
						String colVal = getColValueFile(tableName.substring(
								tableName.lastIndexOf('_') + 1,
								tableName.length()), existingFormRecord);

						if (StringUtils.isBlank(colVal) || colVal == null) {
							dynamicFormTableRecordPK
									.setDynamicFormTableRecordId(maxTableRecordId);
						} else {
							dynamicFormTableRecordPK
									.setDynamicFormTableRecordId(Long
											.parseLong(colVal));
							maxTableRecordId = Long.parseLong(colVal);
							getMaxSequence = dynamicFormTableRecordDAO
									.getMaxSequenceNumber(Long
											.parseLong(colVal));

							dynamicFormTableRecordPK
									.setSequence(getMaxSequence + 1);
						}
						String effectiveDate = dynamicFormTableRecord.getCol1();

						if (StringUtils.isBlank(effectiveDate)) {
							exDynamicFormTableRecord = null;
						} else {
							if (!dataImportParametersDTO
									.getDependentsTypeFieldNameList().contains(
											tableName)) {
								effectiveDate = effectiveDate.substring(0, 10);
								exDynamicFormTableRecord = dynamicFormTableRecordDAO
										.findByEffectiveDate(maxTableRecordId,
												effectiveDate);
							}
						}

						if (exDynamicFormTableRecord != null) {
							DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
							dataImportLogDTO
									.setFailureType("payasia.data.import.validation");
							dataImportLogDTO.setFromMessageSource(true);
							dataImportLogDTO
									.setRemarks("payasia.data.import.same.effective.date.error");
							dataImportLogDTO.setRowNumber(Long
									.parseLong(dataImportParametersDTO
											.getRowNumber()) + 1);
							insertOnlyLogs.add(dataImportLogDTO);
							throw new PayAsiaDataException(insertOnlyLogs);
						} else if (exDynamicFormTableRecord == null
								&& getMaxSequence > 0) {

							DynamicFormTableRecord exiDynamicFormTableRecord = dynamicFormTableRecordDAO
									.findByIdAndSeq(maxTableRecordId,
											getMaxSequence);

							setTableValuesFromExisting(colFormMapList, formId,
									dynamicFormRecord, tableName,
									dynamicFormTableRecord,
									dataImportParametersDTO,
									exiDynamicFormTableRecord, companyId);

						} else {

							setTableValues(colFormMapList, formId,
									dynamicFormRecord, tableName,
									dynamicFormTableRecord,
									dataImportParametersDTO, companyId, mode);
						}

						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO
								.saveWithFlush(dynamicFormTableRecord);
					}

					// Update Leave Entitlement for 1. Child Care Leave type ,
					// 2. Extended Child Care Leave type i.e. when any
					// dependents
					// details will inserted.
					Company companyVO = companyDAO.findById(companyId);
					if (companyVO.getCountryMaster().getCountryName()
							.equalsIgnoreCase("Singapore")
							&& "insert".equalsIgnoreCase(mode)) {
						String leaveSchemeTypeIds = generalLogic
								.getChildCareLeaveTypeInfo(companyId,
										dynamicFormRecord.getEntityKey());

						leaveSchemeTypeDAO.childCareLeaveEntitlementProc(
								companyId, dynamicFormRecord.getEntityKey(),
								leaveSchemeTypeIds);
					}

					boolean isCottonOnTableNameWorkedHours = isCottonOnTableNameWorkedHours(
							colFormMapList, formId, dynamicFormRecord,
							tableName, dynamicFormTableRecord,
							dataImportParametersDTO, companyId, mode);
					if (isCottonOnTableNameWorkedHours) {
						List<Object> tupleList;
						/*
						 * if (exDynamicFormTableRecord != null) { tupleList =
						 * dynamicFormTableRecordDAO
						 * .getMaxEffectiveDate(exDynamicFormTableRecord
						 * .getId() .getDynamicFormTableRecordId()); } else {
						 */
						tupleList = dynamicFormTableRecordDAO
								.getMaxEffectiveDate(dynamicFormTableRecord
										.getId().getDynamicFormTableRecordId());
						/* } */

						saveCottonOnPaidUPTODateFieldValue(tupleList,
								existingFormRecord, dynamicFormRecord,
								companyId);

					}

					setDynamicFormRecordValues(
							tableName.substring(tableName.lastIndexOf('_') + 1,
									tableName.length()), dynRecordsName,
							dynamicFormRecord, maxTableRecordId.toString());
				}

			}
		}

		for (HashMap<String, String> colFormMap : colFormMapList) {

			String formIDString = colFormMap.get("formId");
			long formID = Long.parseLong(formIDString);
			if (formID == formId) {
				if (colFormMap.get("tableName") == null) {

					dynamicFormRecord.setVersion(Integer.parseInt(colFormMap
							.get("maxVersion")));
					String colName = colFormMap.get("colName");
					String codeVal = colFormMap.get("value");
					String defaultValue = colFormMap.get("defaultValue");
					if (colFormMap.get("colType").equalsIgnoreCase("codedesc")) {

						codeVal = getValForCodeDesc(dataImportParametersDTO,
								colFormMap, codeVal);

					} else if (colFormMap.get("colType").equalsIgnoreCase(
							"employeelist")) {

						Employee employee = employeeDAO.findByNumber(codeVal,
								companyId);
						if (employee != null) {
							codeVal = String.valueOf(employee.getEmployeeId());
						}

					}

					if (StringUtils.isBlank(codeVal)
							&& "insert".equalsIgnoreCase(mode)) {
						if (StringUtils.isNotBlank(defaultValue)) {
							codeVal = defaultValue;
							if (colFormMap.get("colType").equalsIgnoreCase(
									"codedesc")) {

								codeVal = getValForCodeDesc(
										dataImportParametersDTO, colFormMap,
										defaultValue);

							} else if (colFormMap.get("colType")
									.equalsIgnoreCase("employeelist")) {

								Employee employee = employeeDAO.findById(Long
										.parseLong(defaultValue));
								codeVal = String.valueOf(employee
										.getEmployeeId());

							}

						}
					}
					setDynamicFormRecordValues(colName.substring(
							colName.lastIndexOf('_') + 1, colName.length()),
							dynRecordsName, dynamicFormRecord, codeVal);

				}

			}

		}
	}

	/**
	 * Sets the dynamic values. Delete All Insert All
	 * 
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param tableNames
	 *            the table names
	 * @param colFormMapList
	 *            the col form map list
	 * @param dynRecordsName
	 *            the dyn records name
	 * @param formId
	 *            the form id
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param existingFormRecord
	 *            the existing form record
	 */

	public void setDynamicValuesDeleteAllInsertAll(
			DataImportParametersDTO dataImportParametersDTO,
			List<String> tableNames,
			List<HashMap<String, String>> colFormMapList,
			List<String> dynRecordsName, Long formId,
			DynamicFormRecord dynamicFormRecord,
			DynamicFormRecord existingFormRecord, Long companyId) {
		boolean containsTableVal;
		List<DataImportLogDTO> insertOnlyLogs = new ArrayList<DataImportLogDTO>();
		for (String tableName : tableNames) {
			int seqNo = 1;
			DynamicFormTableRecord dynamicFormTableRecord = new DynamicFormTableRecord();
			DynamicFormTableRecordPK dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
			containsTableVal = setTableValues(colFormMapList, formId,
					dynamicFormRecord, tableName, dynamicFormTableRecord,
					dataImportParametersDTO, companyId, "insert");

			if (containsTableVal) {
				synchronized (this) {
					Long maxTableRecordId = dynamicFormTableRecordDAO
							.getMaxTableRecordId() + 1;

					dynamicFormTableRecordPK.setSequence(seqNo);

					if (existingFormRecord == null) {
						dynamicFormTableRecordPK
								.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);
					} else {
						String colVal = getColValueFile(tableName.substring(
								tableName.lastIndexOf('_') + 1,
								tableName.length()), existingFormRecord);

						if (StringUtils.isBlank(colVal) || colVal == null) {
							dynamicFormTableRecordPK
									.setDynamicFormTableRecordId(maxTableRecordId);
						} else {
							dynamicFormTableRecordPK
									.setDynamicFormTableRecordId(Long
											.parseLong(colVal));
							maxTableRecordId = Long.parseLong(colVal);
							int getMaxSequence = dynamicFormTableRecordDAO
									.getMaxSequenceNumber(Long
											.parseLong(colVal));

							dynamicFormTableRecordPK
									.setSequence(getMaxSequence + 1);
						}
						String effectiveDate = dynamicFormTableRecord.getCol1();
						effectiveDate = effectiveDate.substring(0, 10);
						DynamicFormTableRecord exDynamicFormTableRecord = dynamicFormTableRecordDAO
								.findByEffectiveDate(maxTableRecordId,
										effectiveDate);
						if (dataImportParametersDTO
								.getDependentsTypeFieldNameList().contains(
										tableName)) {
							exDynamicFormTableRecord = null;
						}

						if (exDynamicFormTableRecord != null) {
							DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
							dataImportLogDTO
									.setFailureType("payasia.data.import.validation");
							dataImportLogDTO.setFromMessageSource(true);
							dataImportLogDTO
									.setRemarks("payasia.data.import.same.effective.date.error");
							dataImportLogDTO.setRowNumber(Long
									.parseLong(dataImportParametersDTO
											.getRowNumber()) + 1);
							insertOnlyLogs.add(dataImportLogDTO);
							throw new PayAsiaDataException(insertOnlyLogs);
						}

						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);
					}

					setDynamicFormRecordValues(
							tableName.substring(tableName.lastIndexOf('_') + 1,
									tableName.length()), dynRecordsName,
							dynamicFormRecord, maxTableRecordId.toString());
				}

			}
		}

		for (HashMap<String, String> colFormMap : colFormMapList) {

			String formIDString = colFormMap.get("formId");
			long formID = Long.parseLong(formIDString);
			if (formID == formId) {
				if (colFormMap.get("tableName") == null) {

					dynamicFormRecord.setVersion(Integer.parseInt(colFormMap
							.get("maxVersion")));
					String colName = colFormMap.get("colName");
					String codeVal = colFormMap.get("value");
					if (colFormMap.get("colType").equalsIgnoreCase("codedesc")) {

						codeVal = getValForCodeDesc(dataImportParametersDTO,
								colFormMap, codeVal);

					}
					setDynamicFormRecordValues(colName.substring(
							colName.lastIndexOf('_') + 1, colName.length()),
							dynRecordsName, dynamicFormRecord, codeVal);

				}

			}

		}
	}

	/**
	 * Gets the val for code desc.
	 * 
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param colFormMap
	 *            the col form map
	 * @param codeVal
	 *            the code val
	 * @return the val for code desc
	 */
	private String getValForCodeDesc(
			DataImportParametersDTO dataImportParametersDTO,
			HashMap<String, String> colFormMap, String codeVal) {
		Long dataDictionaryId = Long.parseLong(colFormMap
				.get("dataDictionaryId"));

		DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
				.findByCondition(dataDictionaryId, codeVal);

		if (dynamicFormFieldRefValue != null) {
			codeVal = Long.toString(dynamicFormFieldRefValue
					.getFieldRefValueId());
		} else {
			if (codeVal.trim().equalsIgnoreCase("")) {
				codeVal = "";
			} else {
				DynamicFormFieldRefValue fieldRefValue = new DynamicFormFieldRefValue();
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(dataDictionaryId);
				if (StringUtils.isNotBlank(codeVal)) {
					codeVal = codeVal.trim();
				}
				fieldRefValue.setCode(codeVal);
				fieldRefValue.setDescription(codeVal);
				fieldRefValue.setDataDictionary(dataDictionary);

				DynamicFormFieldRefValue returnedRefVal = dynamicFormFieldRefValueDAO
						.saveReturn(fieldRefValue);
				codeVal = Long.toString(returnedRefVal.getFieldRefValueId());
			}

		}
		return codeVal;
	}

	/**
	 * Sets the dynamic values.
	 * 
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param tableNames
	 *            the table names
	 * @param colFormMapList
	 *            the col form map list
	 * @param dynRecordsName
	 *            the dyn records name
	 * @param formId
	 *            the form id
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param existingFormRecord
	 *            the existing form record
	 */

	public void setDynamicValuesForUpdate(
			DataImportParametersDTO dataImportParametersDTO,
			List<String> tableNames,
			List<HashMap<String, String>> colFormMapList,
			List<String> dynRecordsName, Long formId,
			DynamicFormRecord dynamicFormRecord,
			DynamicFormRecord existingFormRecord, Long companyId, String mode) {
		boolean containsTableVal;

		for (String tableName : tableNames) {
			DynamicFormTableRecord existingRecord = null;
			int seqNo = 1;
			DynamicFormTableRecord dynamicFormTableRecord = new DynamicFormTableRecord();
			DynamicFormTableRecordPK dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
			containsTableVal = setTableValues(colFormMapList, formId,
					dynamicFormRecord, tableName, dynamicFormTableRecord,
					dataImportParametersDTO, companyId, mode);
			int getMaxSequence = 0;
			if (containsTableVal) {
				synchronized (this) {
					Long maxTableRecordId = dynamicFormTableRecordDAO
							.getMaxTableRecordId() + 1;

					dynamicFormTableRecordPK.setSequence(seqNo);

					if (existingFormRecord == null) {
						dynamicFormTableRecordPK
								.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO
								.saveWithFlush(dynamicFormTableRecord);
					} else {
						String colVal = getColValueFile(tableName.substring(
								tableName.lastIndexOf('_') + 1,
								tableName.length()), existingFormRecord);
						boolean recordExist = false;
						if (StringUtils.isBlank(colVal) || colVal == null) {
							dynamicFormTableRecordPK
									.setDynamicFormTableRecordId(maxTableRecordId);
						} else {
							String effectiveDate = dynamicFormTableRecord
									.getCol1();

							if (StringUtils.isBlank(effectiveDate)) {
								existingRecord = null;
							} else {
								effectiveDate = effectiveDate.substring(0, 10);
								existingRecord = dynamicFormTableRecordDAO
										.findByEffectiveDate(
												Long.parseLong(colVal),
												effectiveDate);

							}

							dynamicFormTableRecordPK
									.setDynamicFormTableRecordId(Long
											.parseLong(colVal));
							maxTableRecordId = Long.parseLong(colVal);

							if (existingRecord != null) {
								recordExist = true;

								setTableValues(colFormMapList, formId,
										dynamicFormRecord, tableName,
										existingRecord,
										dataImportParametersDTO, companyId,
										"update");
								dynamicFormTableRecordPK
										.setSequence(existingRecord.getId()
												.getSequence());
							} else {

								getMaxSequence = dynamicFormTableRecordDAO
										.getMaxSequenceNumber(Long
												.parseLong(colVal));

								dynamicFormTableRecordPK
										.setSequence(getMaxSequence + 1);
							}

						}

						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						if (recordExist) {
							dynamicFormTableRecordDAO.update(existingRecord);
						} else {

							if (getMaxSequence > 0 && existingRecord == null) {

								DynamicFormTableRecord exiDynamicFormTableRecord = dynamicFormTableRecordDAO
										.findByIdAndSeq(maxTableRecordId,
												getMaxSequence);

								setTableValuesFromExisting(colFormMapList,
										formId, dynamicFormRecord, tableName,
										dynamicFormTableRecord,
										dataImportParametersDTO,
										exiDynamicFormTableRecord, companyId);

							}

							dynamicFormTableRecordDAO
									.saveWithFlush(dynamicFormTableRecord);

							// Update Leave Entitlement for 1. Child Care Leave
							// type ,
							// 2. Extended Child Care Leave type i.e. when any
							// dependents
							// details will inserted.
							Company companyVO = companyDAO.findById(companyId);
							if (companyVO.getCountryMaster().getCountryName()
									.equalsIgnoreCase("Singapore")) {
								String leaveSchemeTypeIds = generalLogic
										.getChildCareLeaveTypeInfo(companyId,
												dynamicFormRecord
														.getEntityKey());

								leaveSchemeTypeDAO
										.childCareLeaveEntitlementProc(
												companyId, dynamicFormRecord
														.getEntityKey(),
												leaveSchemeTypeIds);
							}
						}
					}

					boolean isCottonOnTableNameWorkedHours = isCottonOnTableNameWorkedHours(
							colFormMapList, formId, dynamicFormRecord,
							tableName, dynamicFormTableRecord,
							dataImportParametersDTO, companyId, mode);
					if (isCottonOnTableNameWorkedHours) {
						List<Object> tupleList;
						if (existingRecord != null) {
							tupleList = dynamicFormTableRecordDAO
									.getMaxEffectiveDate(existingRecord.getId()
											.getDynamicFormTableRecordId());
						} else {
							tupleList = dynamicFormTableRecordDAO
									.getMaxEffectiveDate(dynamicFormTableRecord
											.getId()
											.getDynamicFormTableRecordId());
						}

						List<DynamicFormTableRecord> dynamicFormTableRecords22 = dynamicFormTableRecordDAO
								.getTableRecords(dynamicFormTableRecord.getId()
										.getDynamicFormTableRecordId(), null,
										null);

						saveCottonOnPaidUPTODateFieldValue(tupleList,
								existingFormRecord, dynamicFormRecord,
								companyId);

					}

					setDynamicFormRecordValues(
							tableName.substring(tableName.lastIndexOf('_') + 1,
									tableName.length()), dynRecordsName,
							dynamicFormRecord, maxTableRecordId.toString());
				}

			}
		}

		for (HashMap<String, String> colFormMap : colFormMapList) {

			String formIDString = colFormMap.get("formId");
			long formID = Long.parseLong(formIDString);
			if (formID == formId) {
				if (colFormMap.get("tableName") == null) {
					dynamicFormRecord.setVersion(Integer.parseInt(colFormMap
							.get("maxVersion")));
					String colName = colFormMap.get("colName");
					String codeVal = colFormMap.get("value");
					String defaultValue = colFormMap.get("defaultValue");
					if (colFormMap.get("colType").equalsIgnoreCase("codedesc")) {
						codeVal = getValForCodeDesc(dataImportParametersDTO,
								colFormMap, codeVal);
					} else if (colFormMap.get("colType").equalsIgnoreCase(
							PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
						Employee employee = employeeDAO.findByNumber(codeVal,
								companyId);
						if (employee != null) {
							codeVal = String.valueOf(employee.getEmployeeId());
						}

					}

					if (StringUtils.isBlank(codeVal)
							&& "insert".equalsIgnoreCase(mode)) {
						if (StringUtils.isNotBlank(defaultValue)) {
							codeVal = defaultValue;
							if (colFormMap.get("colType").equalsIgnoreCase(
									"codedesc")) {

								codeVal = getValForCodeDesc(
										dataImportParametersDTO, colFormMap,
										defaultValue);

							} else if (colFormMap.get("colType")
									.equalsIgnoreCase("employeelist")) {

								Employee employee = employeeDAO.findById(Long
										.parseLong(defaultValue));
								codeVal = String.valueOf(employee
										.getEmployeeId());

							}

						}
					}

					setDynamicFormRecordValues(colName.substring(
							colName.lastIndexOf('_') + 1, colName.length()),
							dynRecordsName, dynamicFormRecord, codeVal);
				}

			}

		}
	}

	private void saveCottonOnPaidUPTODateFieldValue(List<Object> tupleList,
			DynamicFormRecord existingFormRecord,
			DynamicFormRecord dynamicFormRecord, Long companyId) {

		DataDictionary dataDictionaryCOVO = dataDictionaryDAO
				.findByDictionaryName(companyId,
						PayAsiaConstants.EMPLOYEE_ENTITY_ID,
						PayAsiaConstants.COTTON_ON_PAID_UP_TO_DATE);
		if (dataDictionaryCOVO != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
					companyId, dataDictionaryCOVO.getEntityMaster()
							.getEntityId(), dataDictionaryCOVO.getFormID());
			if (dynamicForm != null) {
				Unmarshaller unmarshallerRef = null;
				try {
					unmarshallerRef = XMLUtil.getDocumentUnmarshaller();
				} catch (JAXBException jAXBException) {
					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				} catch (SAXException sAXException) {

					LOGGER.error(sAXException.getMessage(), sAXException);
					throw new PayAsiaSystemException(sAXException.getMessage(),
							sAXException);
				}
				final StringReader xmlRefReader = new StringReader(
						dynamicForm.getMetaData());
				Source xmlRefSource = null;
				try {
					xmlRefSource = XMLUtil.getSAXSource(xmlRefReader);
				} catch (SAXException | ParserConfigurationException e1) {
					LOGGER.error(e1.getMessage(), e1);
					throw new PayAsiaSystemException(e1.getMessage(),
							e1);
				}

				Tab tab = null;
				try {
					tab = (Tab) unmarshallerRef.unmarshal(xmlRefSource);
				} catch (JAXBException jAXBException) {
					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				}
				DynamicFormRecord dynamicFormRecordCO = null;
				if (existingFormRecord == null) {
					dynamicFormRecordCO = dynamicFormRecordDAO.getEmpRecords(
							dynamicFormRecord.getEntityKey(), null,
							dataDictionaryCOVO.getFormID(),
							PayAsiaConstants.EMPLOYEE_ENTITY_ID, companyId);

				} else {
					dynamicFormRecordCO = dynamicFormRecordDAO.getEmpRecords(
							existingFormRecord.getEntityKey(), null,
							dataDictionaryCOVO.getFormID(),
							PayAsiaConstants.EMPLOYEE_ENTITY_ID, companyId);
				}
				boolean isExistingDynamicRecord = true;
				if (dynamicFormRecordCO == null) {
					isExistingDynamicRecord = false;
					dynamicFormRecordCO = new DynamicFormRecord();

					dynamicFormRecordCO.setForm_ID(dataDictionaryCOVO
							.getFormID());
					dynamicFormRecordCO.setCompany_ID(companyId);
					dynamicFormRecordCO
							.setEntity_ID(PayAsiaConstants.EMPLOYEE_ENTITY_ID);
					dynamicFormRecordCO.setEntityKey(dynamicFormRecord
							.getEntityKey());
				}

				if (dynamicFormRecordCO != null) {
					Class<?> dynamicFormRecordClass = dynamicFormRecordCO
							.getClass();
					List<Field> listOfRefFields = tab.getField();
					for (Field fieldR : listOfRefFields) {
						if (fieldR.getDictionaryId().equals(
								dataDictionaryCOVO.getDataDictionaryId())) {
							String fieldName = fieldR.getName();

							String methodName = PayAsiaConstants.SET_COL
									+ fieldName.substring(
											fieldName.lastIndexOf('_') + 1,
											fieldName.length());
							Method dynamicFormRecordMethod = null;
							try {
								dynamicFormRecordMethod = dynamicFormRecordClass
										.getMethod(methodName, String.class);
							} catch (NoSuchMethodException e) {

								LOGGER.error(e.getMessage(), e);
							} catch (SecurityException e) {

								LOGGER.error(e.getMessage(), e);
							}
							for (Object object : tupleList) {
								String dateStr = String.valueOf(object);
								dateStr = DateUtils.convertDateFormatyyyyMMdd(
										dateStr, "yyyy-MM-dd");
								try {
									dynamicFormRecordMethod.invoke(
											dynamicFormRecordCO, dateStr);
								} catch (IllegalAccessException e) {

									LOGGER.error(e.getMessage(), e);
								} catch (IllegalArgumentException e) {

									LOGGER.error(e.getMessage(), e);
								} catch (InvocationTargetException e) {

									LOGGER.error(e.getMessage(), e);
								}
							}
							if (isExistingDynamicRecord) {
								dynamicFormRecordDAO
										.update(dynamicFormRecordCO);
							} else {
								dynamicFormRecordDAO.save(dynamicFormRecordCO);
							}

						}
					}
				}

			}
		}
	}

	public boolean setTableValuesFromExisting(
			List<HashMap<String, String>> colFormMapList, Long formId,
			DynamicFormRecord dynamicFormRecord, String tableName,
			DynamicFormTableRecord dynamicFormTableRecord,
			DataImportParametersDTO dataImportParametersDTO,
			DynamicFormTableRecord existingDynamicFormRecord, Long companyId) {
		boolean containsTableVal = false;
		for (HashMap<String, String> colFormMap : colFormMapList) {
			String formIDString = colFormMap.get("formId");
			long formID = Long.parseLong(formIDString);

			String exdynamicFormTableRecordMethodName;
			Method exdynamicFormTableRecordMethod;

			if (formID == formId) {
				dynamicFormRecord.setVersion(Integer.parseInt(colFormMap
						.get("maxVersion")));
				if (colFormMap.get("tableName") != null) {
					containsTableVal = true;
					String colLabel = colFormMap.get("colLabel");
					if (tableName.equals(colFormMap.get("tableName"))) {

						String tableColName = colFormMap.get("colName");
						String tableRecordMethodName = "setCol"
								+ tableColName.substring(
										tableColName.lastIndexOf('_') + 1,
										tableColName.length());
						Method dynamicFormTableRecordMethod;
						try {
							dynamicFormTableRecordMethod = DynamicFormTableRecord.class
									.getMethod(tableRecordMethodName,
											String.class);
							String coLVal = colFormMap.get("value");
							if (StringUtils.isNotBlank(coLVal)) {
								coLVal = coLVal.trim();
							}
							if (colFormMap.get("colType").equalsIgnoreCase(
									"codedesc")) {

								if (StringUtils.isBlank(coLVal)) {
									exdynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL
											+ tableColName
													.substring(
															tableColName
																	.lastIndexOf('_') + 1,
															tableColName
																	.length());
									exdynamicFormTableRecordMethod = DynamicFormTableRecord.class
											.getMethod(exdynamicFormTableRecordMethodName);

									coLVal = (String) exdynamicFormTableRecordMethod
											.invoke(existingDynamicFormRecord);

								} else {
									coLVal = getValForCodeDesc(
											dataImportParametersDTO,
											colFormMap, coLVal);
								}

							} else if (colFormMap
									.get("colType")
									.equalsIgnoreCase(
											PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {

								if (StringUtils.isBlank(coLVal)) {

									coLVal = "";
								} else {
									Employee employee = employeeDAO
											.findByNumber(coLVal, companyId);
									coLVal = String.valueOf(employee
											.getEmployeeId());
								}

							}

							else

							if (colLabel
									.equalsIgnoreCase(PayAsiaConstants.EFFECTIVE_DATE_LABEL)) {
								coLVal = DateUtils.appendTodayTime(coLVal);

							} else

							if ("null".equalsIgnoreCase(coLVal)) {

								coLVal = "";
							} else if (StringUtils.isBlank(coLVal)) {

								exdynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL
										+ tableColName
												.substring(tableColName
														.lastIndexOf('_') + 1,
														tableColName.length());
								exdynamicFormTableRecordMethod = DynamicFormTableRecord.class
										.getMethod(exdynamicFormTableRecordMethodName);

								coLVal = (String) exdynamicFormTableRecordMethod
										.invoke(existingDynamicFormRecord);

							}

							if (!colLabel
									.equalsIgnoreCase(PayAsiaConstants.PAYASIA_EMP_DYNFIELD_TABLE_COLNAME_DEPENDENT_RELATIONSHIP)) {
								dynamicFormTableRecordMethod.invoke(
										dynamicFormTableRecord, coLVal);
							}

						} catch (SecurityException | NoSuchMethodException
								| IllegalArgumentException
								| IllegalAccessException
								| InvocationTargetException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}
					}

				}
			}
		}
		return containsTableVal;
	}

	public boolean isATableValue(List<HashMap<String, String>> colFormMapList,
			Long formId, DynamicFormRecord dynamicFormRecord) {
		boolean containsTableVal = false;
		for (HashMap<String, String> colFormMap : colFormMapList) {
			String formIDString = colFormMap.get("formId");
			long formID = Long.parseLong(formIDString);
			if (formID == formId) {
				dynamicFormRecord.setVersion(Integer.parseInt(colFormMap
						.get("maxVersion")));
				if (colFormMap.get("tableName") != null) {
					containsTableVal = true;
				}
			}
		}
		return containsTableVal;
	}

	public boolean isCottonOnTableNameWorkedHours(
			List<HashMap<String, String>> colFormMapList, Long formId,
			DynamicFormRecord dynamicFormRecord, String tableName,
			DynamicFormTableRecord dynamicFormTableRecord,
			DataImportParametersDTO dataImportParametersDTO, Long companyId,
			String mode) {
		boolean isTableNameWorkedHours = false;
		for (HashMap<String, String> colFormMap : colFormMapList) {
			String formIDString = colFormMap.get("formId");
			long formID = Long.parseLong(formIDString);
			if (formID == formId) {
				if (colFormMap.get("tableName") != null) {
					String dataDictionaryName = colFormMap
							.get("dataDictionaryName");
					if (tableName.equals(colFormMap.get("tableName"))) {
						isTableNameWorkedHours = dataDictionaryName
								.contains(PayAsiaConstants.COTTON_ON_WORKED_HOURS_HISTORY_TABLE);
						if (isTableNameWorkedHours) {
							return isTableNameWorkedHours;
						}
					}

				}
			}
		}
		return isTableNameWorkedHours;
	}

}
