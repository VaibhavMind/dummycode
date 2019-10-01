/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.CountryMasterDAO;
import com.payasia.dao.DataImportHistoryDAO;
import com.payasia.dao.DataImportLogDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.PayslipFrequencyDAO;
import com.payasia.dao.TimeZoneMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.dao.bean.TimeZoneMaster;
import com.payasia.logic.CompanyDataImportLogic;
import com.payasia.logic.CompanyDataImportStrategy;
import com.payasia.logic.DataImportUtils;

/**
 * The Class CompanyDataImportLogicImpl.
 */
@Component
public class CompanyDataImportLogicImpl implements CompanyDataImportLogic {

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The data import utils. */
	@Resource
	DataImportUtils dataImportUtils;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The dynamic form record dao. */
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	/** The data import history dao. */
	@Resource
	DataImportHistoryDAO dataImportHistoryDAO;

	/** The data import log dao. */
	@Resource
	DataImportLogDAO dataImportLogDAO;

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The dynamic form table record dao. */
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	/** The company group dao. */
	@Resource
	CompanyGroupDAO companyGroupDAO;

	/** The country master dao. */
	@Resource
	CountryMasterDAO countryMasterDAO;

	/** The payslip frequency dao. */
	@Resource
	PayslipFrequencyDAO payslipFrequencyDAO;

	/** The time zone master dao. */
	@Resource
	TimeZoneMasterDAO timeZoneMasterDAO;

	@Resource(name = "companyDataImportStrategyCOELogic")
	CompanyDataImportStrategy companyDataImportStrategyCOE;

	@Resource(name = "companyDataImportStrategyROELogic")
	CompanyDataImportStrategy companyDataImportStrategyROE;

	CompanyDataImportStrategy companyDataImportStrategy = null;

	private static final Logger LOGGER = Logger
			.getLogger(CompanyDataImportLogicImpl.class);

	/**
	 * Purpose : To Save the valid data and Log the transaction errors.
	 * 
	 * @param keyValMapList
	 *            the key val map list
	 * @param templateId
	 *            the template id
	 * @param entityId
	 *            the entity id
	 * @param colMap
	 *            the col map
	 * @param uploadType
	 *            the upload type
	 * @param transactionType
	 *            the transaction type
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	@Override
	public void saveValidDataFile(
			List<HashMap<String, DataImportKeyValueDTO>> keyValMapList,
			long templateId, long entityId,
			HashMap<String, EmpDataImportTemplateField> colMap,
			String uploadType, String transactionType, Long companyId,
			HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions) {
		List<String> dependentsTypeFieldNameList = new ArrayList<String>();
		if (transactionType.equals(PayAsiaConstants.ROLLBACK_ON_ERROR)) {
			companyDataImportStrategy = companyDataImportStrategyROE;
		} else {
			companyDataImportStrategy = companyDataImportStrategyCOE;
		}

		List<DataImportLogDTO> dataImportLogDTOList = new ArrayList<DataImportLogDTO>();

		for (HashMap<String, DataImportKeyValueDTO> map : keyValMapList) {
			Set<String> keySet = map.keySet();
			DataImportKeyValueDTO rowNumberDTO = map.get("rowNumber");
			String rowNumber = rowNumberDTO.getValue();
			boolean isDynamic = false;
			boolean isFieldValid = true;
			Company company = new Company();
			List<DataImportKeyValueDTO> companyColName = new ArrayList<DataImportKeyValueDTO>();
			List<String> dynRecordsName = new ArrayList<String>();
			List<HashMap<String, String>> colFormMapList = new ArrayList<HashMap<String, String>>();
			List<Long> formIds = new ArrayList<Long>();
			List<String> tableNames = new ArrayList<String>();

			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) map
						.get(key);

				String value = valueDTO.getValue();

				if (key != "rowNumber") {
					EmpDataImportTemplateField empDataImportTemplateField = colMap
							.get(key);

					if (empDataImportTemplateField.getDataDictionary()
							.getFieldType()
							.equals(PayAsiaConstants.STATIC_TYPE)) {

						String colName = empDataImportTemplateField
								.getDataDictionary().getColumnName();
						try {
							setStaticFields(company, colName, valueDTO,
									companyColName, rowNumber);

						} catch (PayAsiaDataException pde) {
							LOGGER.error(pde.getMessage(), pde);
							isFieldValid = false;
							dataImportLogDTOList.addAll(pde.getErrors());
						}

					} else {

						isDynamic = true;

						dataImportUtils.setDynamicFieldValue(
								empDataImportTemplateField, colFormMapList,
								tableNames, value, formIds, companyId,
								dynamicFormObjects, dynamicFormVersions,
								dependentsTypeFieldNameList);
					}

				}

			}

			DataImportParametersDTO dataImportParametersDTO = new DataImportParametersDTO();
			dataImportParametersDTO.setColFormMapList(colFormMapList);
			dataImportParametersDTO.setDynamic(isDynamic);
			dataImportParametersDTO.setDynRecordsName(dynRecordsName);
			dataImportParametersDTO.setEmpColName(companyColName);
			dataImportParametersDTO.setEntityId(entityId);
			dataImportParametersDTO.setFormIds(formIds);
			dataImportParametersDTO.setRowNumber(rowNumber);
			dataImportParametersDTO.setTableNames(tableNames);
			dataImportParametersDTO.setTransactionType(transactionType);
			dataImportParametersDTO
					.setDependentsTypeFieldNameList(dependentsTypeFieldNameList);

			if (isFieldValid) {
				try {
					switch (uploadType) {
					case PayAsiaConstants.INSERT_NEW:
						companyDataImportStrategy.insertNew(company,
								dataImportParametersDTO, companyId);
						break;

					case PayAsiaConstants.UPDATE_AND_INSERT:
						companyDataImportStrategy.updateORInsert(company,
								dataImportParametersDTO, companyId);
						break;

					case PayAsiaConstants.DELETE_ALL_AND_INSERT:
						companyDataImportStrategy.deleteAndInsert(company,
								dataImportParametersDTO, companyId);
						break;
					}
				} catch (PayAsiaDataException pde) {
					LOGGER.error(pde.getMessage(), pde);
					dataImportLogDTOList.addAll(pde.getErrors());
				}
				if (transactionType.equals(PayAsiaConstants.ROLLBACK_ON_ERROR)) {
					if (dataImportLogDTOList != null
							&& !dataImportLogDTOList.isEmpty()) {
						throw new PayAsiaDataException(dataImportLogDTOList);
					}
				}
			}

		}

		if (dataImportLogDTOList != null && !dataImportLogDTOList.isEmpty()) {
			throw new PayAsiaDataException(dataImportLogDTOList);
		}
	}

	/**
	 * Purpose : Sets the static fields value.
	 * 
	 * @param company
	 *            the company
	 * @param colName
	 *            the col name
	 * @param valueDTO
	 *            the value dto
	 * @param empColName
	 *            the emp col name
	 * @param rowNumber
	 *            the row number
	 */
	public void setStaticFields(Company company, String colName,
			DataImportKeyValueDTO valueDTO,
			List<DataImportKeyValueDTO> empColName, String rowNumber) {

		List<DataImportLogDTO> dataImportLogDTOList = new ArrayList<DataImportLogDTO>();
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setFromMessageSource(false);
		dataImportLogDTO.setFailureType("payasia.data.import.transaction");
		dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber) + 1);

		if (colName.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_GROUP_CODE)) {

			CompanyGroup companyGroup = companyGroupDAO.findByCode(valueDTO
					.getValue());

			if (companyGroup == null) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO
						.setRemarks("payasia.data.import.group.code.error");
				dataImportLogDTO.setColName(valueDTO.getKey());

				dataImportLogDTOList.add(dataImportLogDTO);

			} else {
				valueDTO.setMethodName(PayAsiaConstants.COLUMN_NAME_GROUP_CODE);
				valueDTO.setStatic(true);

				empColName.add(valueDTO);
				company.setCompanyGroup(companyGroup);
			}

		} else if (colName
				.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_COUNTRY_NAME)) {

			CountryMaster country = countryMasterDAO.findByName(valueDTO
					.getValue());

			if (country == null) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO
						.setRemarks("payasia.data.import.country.error");
				dataImportLogDTO.setColName(valueDTO.getKey());
				dataImportLogDTOList.add(dataImportLogDTO);
			} else {
				valueDTO.setMethodName(PayAsiaConstants.COLUMN_NAME_COUNTRY_NAME);
				valueDTO.setStatic(true);

				empColName.add(valueDTO);
				company.setCountryMaster(country);
			}

		} else if (colName
				.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_FREQUENCY)) {

			PayslipFrequency payslipFrequency = payslipFrequencyDAO
					.findByFrequency(valueDTO.getValue());

			if (payslipFrequency == null) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO
						.setRemarks("payasia.data.import.frequency.error");
				dataImportLogDTO.setColName(valueDTO.getKey());
				dataImportLogDTOList.add(dataImportLogDTO);
			} else {
				valueDTO.setMethodName(PayAsiaConstants.COLUMN_NAME_FREQUENCY);
				valueDTO.setStatic(true);

				empColName.add(valueDTO);
				company.setPayslipFrequency(payslipFrequency);
			}

		} else if (colName
				.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_TIME_ZONE_NAME)) {

			TimeZoneMaster timeZoneMaster = timeZoneMasterDAO
					.findByName(valueDTO.getValue());

			if (timeZoneMaster == null) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO
						.setRemarks("payasia.data.import.time.zone.error");
				dataImportLogDTO.setColName(valueDTO.getKey());
				dataImportLogDTOList.add(dataImportLogDTO);
			} else {
				valueDTO.setMethodName(PayAsiaConstants.COLUMN_NAME_TIME_ZONE_NAME);
				valueDTO.setStatic(true);

				empColName.add(valueDTO);
				company.setTimeZoneMaster(timeZoneMaster);
			}

		} else {

			String[] parts = colName.split("_");
			StringBuilder camelCaseBuilder = new StringBuilder("");
			for (String part : parts) {

				camelCaseBuilder.append(part.substring(0, 1).toUpperCase())
						.append(part.substring(1).toLowerCase());
			}

			Class<?> companyClass = company.getClass();
			String methodName = "set" + camelCaseBuilder.toString();
			valueDTO.setMethodName(camelCaseBuilder.toString());
			valueDTO.setStatic(true);

			empColName.add(valueDTO);
			Method method;

			try {

				if (valueDTO.getFieldType().equals(
						PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {
					method = companyClass.getMethod(methodName, Boolean.TYPE);

					method.invoke(company,
							Boolean.parseBoolean(valueDTO.getValue()));
				} else if (valueDTO.getFieldType().equals(
						PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {
					method = companyClass
							.getMethod(methodName, Timestamp.class);
					Timestamp timestamp = DateUtils.stringToTimestamp(
							valueDTO.getValue(),
							PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
					method.invoke(company, timestamp);

				} else if (valueDTO.getFieldType().equals(
						PayAsiaConstants.PAYASIA_DATATYPE_INT)) {
					method = companyClass.getMethod(methodName, Integer.TYPE);
					method.invoke(company,
							Integer.parseInt(valueDTO.getValue()));
				} else {
					method = companyClass.getMethod(methodName, String.class);
					method.invoke(company, valueDTO.getValue());
				}

			} catch (SecurityException | NoSuchMethodException
					| IllegalArgumentException | IllegalAccessException
					| InvocationTargetException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

		if (dataImportLogDTOList != null && !dataImportLogDTOList.isEmpty()) {
			throw new PayAsiaDataException(dataImportLogDTOList);
		}

	}

}