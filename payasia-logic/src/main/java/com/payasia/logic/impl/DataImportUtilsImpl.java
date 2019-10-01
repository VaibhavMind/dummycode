/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic.impl;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.logic.DataImportUtils;

/**
 * The Class DataImportUtilsImpl.
 */
@Component
public class DataImportUtilsImpl implements DataImportUtils {

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

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
	@Override
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
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.DataImportUtils#copyDynamicRecordData(java.util.List,
	 * com.payasia.dao.bean.DynamicFormRecord,
	 * com.payasia.dao.bean.DynamicFormRecord)
	 */
	@Override
	public DynamicFormRecord copyDynamicRecordData(List<String> colNames,
			DynamicFormRecord newRecord, DynamicFormRecord oldRecord) {
		try {
			Class<?> oldDyRecordClass = oldRecord.getClass();
			Class<?> newDyRecordClass = newRecord.getClass();

			for (String colName : colNames) {

				String getMethodName = "getCol" + colName;
				Method getColMethod;

				getColMethod = newDyRecordClass.getMethod(getMethodName);

				String setMethodName = "setCol" + colName;
				Method setMethod;

				String stringVal = (String) getColMethod.invoke(newRecord);
				setMethod = oldDyRecordClass.getMethod(setMethodName,
						String.class);
				setMethod.invoke(oldRecord, stringVal);

			}
		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return oldRecord;
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
	@Override
	public void setDynamicFieldValue(
			EmpDataImportTemplateField empDataImportTemplateField,
			List<HashMap<String, String>> colFormMapList,
			List<String> tableNames, String value, List<Long> formIds,
			Long companyId, HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions,
			List<String> dependentsTypeFieldNameList) {

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
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.LABEL_FIELD_TYPE)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionaryName)) {

					colFormMap.put("colName", field.getName());
					colFormMap.put("tableName", null);
					colFormMap.put("colLabel", empDataImportTemplateField
							.getDataDictionary().getLabel());
					colFormMap.put("colType", field.getType());
					String companyDateFormat = empDataImportTemplateField
							.getDataDictionary().getCompany().getDateFormat();

					colFormMap.put("dataDictionaryName", dataDictionaryName);

					 
					if (field.getType().equalsIgnoreCase(
							PayAsiaConstants.FIELD_TYPE_DATE)) {
						if (StringUtils.isNotBlank(field.getDefaultValue())) {
							String defaultVal = DateUtils
									.convertDateFormatyyyyMMdd(
											field.getDefaultValue(),
											companyDateFormat);
							colFormMap.put("defaultValue", defaultVal);
						}
					} else {
						colFormMap.put("defaultValue", field.getDefaultValue());
					}

				}

			}

			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					|| StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				if (StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
					if (!dependentsTypeFieldNameList.contains(field.getName())) {
						dependentsTypeFieldNameList.add(field.getName());
					}

				}
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
						String companyDateFormat = empDataImportTemplateField
								.getDataDictionary().getCompany()
								.getDateFormat();
						colFormMap
								.put("dataDictionaryName", dataDictionaryName);
						 
						if (column.getType().equalsIgnoreCase(
								PayAsiaConstants.FIELD_TYPE_DATE)) {
							if (StringUtils
									.isNotBlank(column.getDefaultValue())) {
								String defaultVal = DateUtils
										.convertDateFormatyyyyMMdd(
												column.getDefaultValue(),
												companyDateFormat);
								colFormMap.put("defaultValue", defaultVal);
							}

						} else {
							colFormMap.put("defaultValue",
									column.getDefaultValue());
						}

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
	@Override
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
	@Override
	public boolean setTableValues(List<HashMap<String, String>> colFormMapList,
			Long formId, DynamicFormRecord dynamicFormRecord, String tableName,
			DynamicFormTableRecord dynamicFormTableRecord,
			DataImportParametersDTO dataImportParametersDTO) {
		Class<?> dynamicFormTableRecordClass = dynamicFormTableRecord
				.getClass();
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
										tableColName.lastIndexOf("_") + 1,
										tableColName.length());
						Method dynamicFormTableRecordMethod;
						try {
							dynamicFormTableRecordMethod = dynamicFormTableRecordClass
									.getMethod(tableRecordMethodName,
											String.class);
							String coLVal = colFormMap.get("value");

							if (colLabel
									.equalsIgnoreCase(PayAsiaConstants.EFFECTIVE_DATE_LABEL)) {
								coLVal = DateUtils.appendTodayTime(coLVal);

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

}
