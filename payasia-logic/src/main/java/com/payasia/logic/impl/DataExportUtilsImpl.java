/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic.impl;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.MultiLingualData;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.MultilingualLogic;

/**
 * The Class DataExportUtilsImpl.
 */
@Component
public class DataExportUtilsImpl implements DataExportUtils {

	private static final Logger LOGGER = Logger
			.getLogger(DataExportUtilsImpl.class);

	@Resource
	MultilingualLogic multilingualLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataExportUtils#getDynamicRecords(java.util.Map,
	 * java.util.Map, java.lang.Long, int, java.util.Map, java.lang.String,
	 * com.payasia.common.dto.DataImportKeyValueDTO)
	 */
	@Override
	public int getDynamicRecords(
			Map<Long, List<DynamicFormRecord>> empDynamicMap,
			Map<Long, List<DynamicFormTableRecord>> empDynamicTableMap,
			Long employeeId, int totalRecords,
			Map<String, List<String>> finalMap, String key,
			DataImportKeyValueDTO valueDTO) {
		List<DynamicFormRecord> dynamicFormRecordList = empDynamicMap
				.get(employeeId);
		boolean noRecordFound = true;
		if (dynamicFormRecordList != null) {

			for (DynamicFormRecord dynamicFormRecord : dynamicFormRecordList) {
				if (valueDTO.getFormId() == dynamicFormRecord.getForm_ID()) {
					noRecordFound = false;
					if (!valueDTO.isChild()) {
						String cellVal = getColValueFile(
								valueDTO.getMethodName(), dynamicFormRecord);

						if (cellVal != null) {
							List<String> stringList = new ArrayList<String>();
							stringList.add(cellVal);
							finalMap.put(key, stringList);
						} else {
							List<String> stringList = new ArrayList<String>();
							stringList.add("");
							finalMap.put(key, stringList);
						}
					} else {

						String tableCell = getColValueFile(
								valueDTO.getTablePosition(), dynamicFormRecord);

						if (tableCell != null && !tableCell.equals("")) {
							List<DynamicFormTableRecord> dynamicFormTableRecordList = empDynamicTableMap
									.get(employeeId);
							if (dynamicFormTableRecordList != null) {
								List<String> stringList = new ArrayList<String>();

								int maxRecords = 0;
								for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecordList) {
									if (dynamicFormTableRecord.getId()
											.getDynamicFormTableRecordId() == Long
											.parseLong(tableCell)) {

										String cellVal = getTableColValueFile(
												valueDTO.getMethodName(),
												dynamicFormTableRecord);
										maxRecords++;
										if (cellVal != null) {

											stringList.add(cellVal);
										} else {
											stringList.add("");
										}

									}
								}

								if (maxRecords > totalRecords) {
									totalRecords = maxRecords;
								}

								finalMap.put(key, stringList);
							} else {
								List<String> stringList = new ArrayList<String>();
								stringList.add("");
								finalMap.put(key, stringList);
							}
						} else {
							List<String> stringList = new ArrayList<String>();
							stringList.add("");
							finalMap.put(key, stringList);
						}
					}
				}
			}
		}

		if (noRecordFound) {
			List<String> stringList = new ArrayList<String>();
			stringList.add("");
			finalMap.put(key, stringList);
		}
		return totalRecords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataExportUtils#getTabObject(com.payasia.dao.bean.
	 * DynamicForm)
	 */
	@Override
	public Tab getTabObject(DynamicForm dynamicForm) {
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

		String xmlString = multilingualLogic
				.convertLabelsToSpecificLanguageWithoutEntity(dynamicForm
						.getMetaData(), UserContext.getLanguageId(),
						dynamicForm.getId().getCompany_ID(), dynamicForm
								.getId().getFormId());

		 
		 
		final StringReader xmlReader = new StringReader(xmlString);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataExportUtils#getColValueFile(java.lang.String,
	 * com.payasia.dao.bean.DynamicFormRecord)
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
		} catch (SecurityException securityException) {
			LOGGER.error(securityException.getMessage(), securityException);
			throw new PayAsiaSystemException(securityException.getMessage(),
					securityException);
		} catch (NoSuchMethodException noSuchMethodException) {
			LOGGER.error(noSuchMethodException.getMessage(),
					noSuchMethodException);
			throw new PayAsiaSystemException(
					noSuchMethodException.getMessage(), noSuchMethodException);
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage(),
					illegalArgumentException);
			throw new PayAsiaSystemException(
					illegalArgumentException.getMessage(),
					illegalArgumentException);
		} catch (IllegalAccessException illegalAccessException) {
			LOGGER.error(illegalAccessException.getMessage(),
					illegalAccessException);
			throw new PayAsiaSystemException(
					illegalAccessException.getMessage(), illegalAccessException);
		} catch (InvocationTargetException invocationTargetException) {
			LOGGER.error(invocationTargetException.getMessage(),
					invocationTargetException);
			throw new PayAsiaSystemException(
					invocationTargetException.getMessage(),
					invocationTargetException);
		}

		return tableRecordId;

	}

	/**
	 * Purpose: To get the Dynamic Form Table Record value based on Column
	 * number
	 * 
	 * @param colNumber
	 *            the col number
	 * @param existingFormTableRecord
	 *            the existing form table record
	 * @return the table col value file
	 */
	public String getTableColValueFile(String colNumber,
			DynamicFormTableRecord existingFormTableRecord) {

		String tableRecordId = null;
		Class<?> dynamicFormTableRecordClass = existingFormTableRecord
				.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormRecordMethod;
		try {

			dynamicFormRecordMethod = dynamicFormTableRecordClass
					.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormRecordMethod
					.invoke(existingFormTableRecord);
		} catch (SecurityException securityException) {
			LOGGER.error(securityException.getMessage(), securityException);
			throw new PayAsiaSystemException(securityException.getMessage(),
					securityException);
		} catch (NoSuchMethodException noSuchMethodException) {
			LOGGER.error(noSuchMethodException.getMessage(),
					noSuchMethodException);
			throw new PayAsiaSystemException(
					noSuchMethodException.getMessage(), noSuchMethodException);
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage(),
					illegalArgumentException);
			throw new PayAsiaSystemException(
					illegalArgumentException.getMessage(),
					illegalArgumentException);
		} catch (IllegalAccessException illegalAccessException) {
			LOGGER.error(illegalAccessException.getMessage(),
					illegalAccessException);
			throw new PayAsiaSystemException(
					illegalAccessException.getMessage(), illegalAccessException);
		} catch (InvocationTargetException invocationTargetException) {
			LOGGER.error(invocationTargetException.getMessage(),
					invocationTargetException);
			throw new PayAsiaSystemException(
					invocationTargetException.getMessage(),
					invocationTargetException);
		}

		return tableRecordId;

	}

	@Override
	public Tab getTabObjectDataExportGroup(DynamicForm dynamicForm,
			List<DataDictionary> dataDictionaryList,
			List<MultiLingualData> multiLingualDataList) {
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

		String xmlString = multilingualLogic
				.convertLabelsToSpecificLanguageWithoutEntityDataExportGroup(
						dynamicForm.getMetaData(), UserContext.getLanguageId(),
						dynamicForm.getId().getCompany_ID(), dynamicForm
								.getId().getFormId(), dataDictionaryList,
						multiLingualDataList);

		 
		 
		final StringReader xmlReader = new StringReader(xmlString);
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
}
