package com.payasia.logic.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.PayDataCollectionConditionDTO;
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
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.PayCodeDAO;
import com.payasia.dao.PayDataCollectionDAO;
import com.payasia.dao.PayslipFrequencyDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.DynamicFormTableRecordPK;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.PayDataCollection;
import com.payasia.dao.bean.Paycode;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.PayCodeDataImportLogic;

@Component
public class PayCodeDataImportLogicImpl implements PayCodeDataImportLogic {
	private static final Logger LOGGER = Logger
			.getLogger(PayCodeDataImportLogicImpl.class);
	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	PayDataCollectionDAO payDataCollectionDAO;

	@Resource
	DataImportUtils dataImportUtils;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	@Resource
	DataImportHistoryDAO dataImportHistoryDAO;

	@Resource
	DataImportLogDAO dataImportLogDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	@Resource
	CompanyGroupDAO companyGroupDAO;

	@Resource
	CountryMasterDAO countryMasterDAO;

	@Resource
	PayslipFrequencyDAO payslipFrequencyDAO;

	@Resource
	PayCodeDAO payCodeDAO;

	/**
	 * Purpose : To Save the valid data and Log the transaction errors.
	 * 
	 * @param keyValMapList
	 *            the key val map list
	 * @param templateId
	 *            the template id
	 * @param l
	 *            the import history
	 * @param colMap
	 *            the col map
	 * @param uploadType
	 *            the upload type
	 * @return the list
	 */
	@Override
	public List<DataImportLogDTO> saveValidDataFile(
			List<HashMap<String, DataImportKeyValueDTO>> keyValMapList,
			long templateId, long entityId,
			HashMap<String, EmpDataImportTemplateField> colMap,
			String uploadType, String transactionType, Long companyId) {

		List<DataImportLogDTO> dataImportLogDTOList = new ArrayList<DataImportLogDTO>();
		if (uploadType.equals(PayAsiaConstants.DELETE_ALL_AND_INSERT)) {
			for (HashMap<String, DataImportKeyValueDTO> map : keyValMapList) {
				Set<String> keySet = map.keySet();
				DataImportKeyValueDTO rowNumberDTO = map.get("rowNumber");
				String rowNumber = rowNumberDTO.getValue();
				boolean isDynamic = false;

				Company company = companyDAO.findById(companyId);
				PayDataCollection payDataCollection = new PayDataCollection();
				payDataCollection.setCompany(company);

				List<DataImportKeyValueDTO> companyColName = new ArrayList<DataImportKeyValueDTO>();

				List<Long> formIds = new ArrayList<Long>();
				List<String> tableNames = new ArrayList<String>();

				for (Iterator<String> iterator = keySet.iterator(); iterator
						.hasNext();) {
					String key = (String) iterator.next();
					DataImportKeyValueDTO valueDTO = (DataImportKeyValueDTO) map
							.get(key);
					valueDTO.setKey(key);

					if (key != "rowNumber") {
						EmpDataImportTemplateField empDataImportTemplateField = colMap
								.get(key);

						if (empDataImportTemplateField.getDataDictionary()
								.getFieldType()
								.equals(PayAsiaConstants.STATIC_TYPE)) {

							String colName = empDataImportTemplateField
									.getDataDictionary().getColumnName();

							DataImportLogDTO dataImportLogDTO = setStaticFields(
									payDataCollection, colName, valueDTO,
									companyColName, rowNumber);

							if (dataImportLogDTO != null) {
								dataImportLogDTOList.add(dataImportLogDTO);
								return dataImportLogDTOList;
							}

						} else {

							isDynamic = true;

						}

					}

				}
				DataImportParametersDTO dataImportParametersDTO = new DataImportParametersDTO();
				dataImportParametersDTO.setDynamic(isDynamic);
				dataImportParametersDTO.setFormIds(formIds);
				dataImportParametersDTO.setEntityId(entityId);
				dataImportParametersDTO.setTableNames(tableNames);
				dataImportParametersDTO.setTransactionType(transactionType);
				deleteFormRecords(payDataCollection, dataImportParametersDTO,
						companyId);

			}
		}
		for (HashMap<String, DataImportKeyValueDTO> map : keyValMapList) {
			Set<String> keySet = map.keySet();
			DataImportKeyValueDTO rowNumberDTO = map.get("rowNumber");
			String rowNumber = rowNumberDTO.getValue();
			boolean isDynamic = false;
			Company company = companyDAO.findById(1L);
			PayDataCollection payDataCollection = new PayDataCollection();
			payDataCollection.setCompany(company);
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

				if (key != "rowNumber") {
					EmpDataImportTemplateField empDataImportTemplateField = colMap
							.get(key);

					if (empDataImportTemplateField.getDataDictionary()
							.getFieldType()
							.equals(PayAsiaConstants.STATIC_TYPE)) {

						String colName = empDataImportTemplateField
								.getDataDictionary().getColumnName();
						DataImportLogDTO dataImportLogDTO = setStaticFields(
								payDataCollection, colName, valueDTO,
								companyColName, rowNumber);

						if (dataImportLogDTO != null) {
							dataImportLogDTOList.add(dataImportLogDTO);
							return dataImportLogDTOList;
						}

					} else {

						isDynamic = true;

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

			if (uploadType.equals(PayAsiaConstants.DELETE_ALL_AND_INSERT)) {
				List<DataImportLogDTO> deleteAndInsertLogs = saveForDeleteAndInsert(
						payDataCollection, dataImportParametersDTO);
				dataImportLogDTOList.addAll(deleteAndInsertLogs);
			}

			if (transactionType.equals(PayAsiaConstants.ROLLBACK_ON_ERROR)) {
				if (!dataImportLogDTOList.isEmpty()) {
					return dataImportLogDTOList;
				}
			}

		}

		return dataImportLogDTOList;
	}

	/**
	 * Purpose : Sets the static fields value.
	 * 
	 * @param employee
	 *            the employee
	 * @param colName
	 *            the col name
	 * @param valueDTO
	 *            the value dto
	 * @param empColName
	 *            the emp col name
	 * @param rowNumber
	 */
	public DataImportLogDTO setStaticFields(
			PayDataCollection payDataCollection, String colName,
			DataImportKeyValueDTO valueDTO,
			List<DataImportKeyValueDTO> empColName, String rowNumber) {

		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setFailureType("payasia.data.import.transaction");
		dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber) + 1);
		dataImportLogDTO.setFromMessageSource(false);
		if (colName.equalsIgnoreCase("Employee_Number")) {

			EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
			conditionDTO.setEmployeeNumber(valueDTO.getValue());
			List<Employee> employees = employeeDAO.findByCondition(
					conditionDTO, null, null);

			if (employees.size() == 0) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO
						.setRemarks("payasia.data.import.employee.code.error");
				dataImportLogDTO.setColName(valueDTO.getKey());
				return dataImportLogDTO;
			}
			valueDTO.setMethodName("Employee_Number");
			valueDTO.setStatic(true);

			empColName.add(valueDTO);
			payDataCollection.setEmployee(employees.get(0));

		} else if ("Paycode".equalsIgnoreCase(colName)) {

			Paycode payCode = payCodeDAO.getPayCodeByName(valueDTO.getValue(),
					1l);

			if (payCode == null) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO
						.setRemarks("payasia.data.import.pay.code.error");
				dataImportLogDTO.setColName(valueDTO.getKey());
				return dataImportLogDTO;
			}
			valueDTO.setMethodName("Paycode");
			valueDTO.setStatic(true);

			empColName.add(valueDTO);
			payDataCollection.setPaycode(payCode);

		} else {

			String[] parts = colName.split("_");
			StringBuilder camelCaseColName = new StringBuilder("");

			for (String part : parts) {

				camelCaseColName.append(part.substring(0, 1).toUpperCase())
						.append(part.substring(1).toLowerCase());
			}

			Class<?> companyClass = payDataCollection.getClass();
			String methodName = "set" + camelCaseColName;
			valueDTO.setMethodName(camelCaseColName.toString());
			valueDTO.setStatic(true);

			empColName.add(valueDTO);
			Method method;

			try {

				if ("bit".equals(valueDTO.getFieldType())) {
					method = companyClass.getMethod(methodName, Boolean.TYPE);

					method.invoke(payDataCollection,
							Boolean.parseBoolean(valueDTO.getValue()));
				} else if ("datetime".equals(valueDTO.getFieldType())) {
					method = companyClass
							.getMethod(methodName, Timestamp.class);
					Timestamp timestamp = DateUtils.stringToTimestamp(valueDTO
							.getValue());
					method.invoke(payDataCollection, timestamp);

				} else if ("numeric".equals(valueDTO.getFieldType())) {
					method = companyClass.getMethod(methodName,
							BigDecimal.class);

					method.invoke(payDataCollection,
							new BigDecimal(valueDTO.getValue()));
				} else {
					method = companyClass.getMethod(methodName, String.class);
					method.invoke(payDataCollection, valueDTO.getValue());
				}

			} catch (SecurityException e) {
				dataImportLogDTO.setRemarks(e.getLocalizedMessage());
				dataImportLogDTO.setColName(valueDTO.getKey());
				LOGGER.error(e.getMessage(), e);
				return dataImportLogDTO;
			} catch (NoSuchMethodException e) {
				dataImportLogDTO.setRemarks(e.getLocalizedMessage());
				dataImportLogDTO.setColName(valueDTO.getKey());
				LOGGER.error(e.getMessage(), e);
				return dataImportLogDTO;
			} catch (IllegalArgumentException e) {
				dataImportLogDTO.setRemarks(e.getLocalizedMessage());
				dataImportLogDTO.setColName(valueDTO.getKey());
				LOGGER.error(e.getMessage(), e);
				return dataImportLogDTO;
			} catch (IllegalAccessException e) {
				dataImportLogDTO.setRemarks(e.getLocalizedMessage());
				dataImportLogDTO.setColName(valueDTO.getKey());
				LOGGER.error(e.getMessage(), e);
				return dataImportLogDTO;
			} catch (InvocationTargetException e) {
				dataImportLogDTO.setRemarks(e.getLocalizedMessage());
				dataImportLogDTO.setColName(valueDTO.getKey());
				LOGGER.error(e.getMessage(), e);
				return dataImportLogDTO;
			}
		}

		return null;
	}

	/**
	 * Purpose : Save the data for Upload Type - Insert Only (01).
	 * 
	 * @param employee
	 *            the employee
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @return the list
	 */
	public List<DataImportLogDTO> saveForInsertOnly(Company company,
			DataImportParametersDTO dataImportParametersDTO) {
		List<DataImportLogDTO> insertOnlyLogs = new ArrayList<DataImportLogDTO>();
		List<DataImportKeyValueDTO> companyColName = dataImportParametersDTO
				.getEmpColName();
		String rowNumber = dataImportParametersDTO.getRowNumber();
		boolean isDynamic = dataImportParametersDTO.isDynamic();
		List<Long> formIds = dataImportParametersDTO.getFormIds();
		long entityId = dataImportParametersDTO.getEntityId();
		List<String> tableNames = dataImportParametersDTO.getTableNames();
		List<HashMap<String, String>> colFormMapList = dataImportParametersDTO
				.getColFormMapList();
		List<String> dynRecordsName = dataImportParametersDTO
				.getDynRecordsName();
		try {

			CompanyConditionDTO companyDTO = new CompanyConditionDTO();
			companyDTO.setCompanyCode(company.getCompanyCode());
			Company existingCompanyRecord = companyDAO
					.findByCondition(companyDTO);

			if (companyColName.size() == 1
					&& companyColName.get(0).getMethodName()
							.equalsIgnoreCase("CompanyCode")) {
				if (existingCompanyRecord != null) {
					company.setCompanyId(existingCompanyRecord.getCompanyId());
				}

			} else {
				if (existingCompanyRecord == null) {

					if (dataImportParametersDTO.getTransactionType().equals(
							PayAsiaConstants.ROLLBACK_ON_ERROR)) {
						company = companyDAO.saveReturn(company);
					} else {
						company = companyDAO.newTranSaveReturn(company);
					}

				} else {
					company.setCompanyId(existingCompanyRecord.getCompanyId());
				}
			}

			if (isDynamic) {
				for (Long formId : formIds) {
					boolean containsTableVal = false;
					DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

					dynamicFormRecord.setForm_ID(formId);
					dynamicFormRecord.setCompany_ID(1L);
					dynamicFormRecord.setEntity_ID(entityId);
					dynamicFormRecord.setEntityKey(company.getCompanyId());
					DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
							.getEmpRecords(company.getCompanyId(), null,
									formId, entityId, 1L);

					for (String tableName : tableNames) {
						int seqNo = 1;
						DynamicFormTableRecord dynamicFormTableRecord = new DynamicFormTableRecord();
						DynamicFormTableRecordPK dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
						containsTableVal = dataImportUtils.setTableValues(
								colFormMapList, formId, dynamicFormRecord,
								tableName, dynamicFormTableRecord,
								dataImportParametersDTO);

						if (containsTableVal) {
							Long maxTableRecordId = dynamicFormTableRecordDAO
									.getMaxTableRecordId() + 1;

							dynamicFormTableRecordPK.setSequence(seqNo);

							if (existingFormRecord == null) {
								dynamicFormTableRecordPK
										.setDynamicFormTableRecordId(maxTableRecordId);
								dynamicFormTableRecord
										.setId(dynamicFormTableRecordPK);

								dynamicFormTableRecordDAO
										.save(dynamicFormTableRecord);
							} else {
								String colVal = dataImportUtils
										.getColValueFile(tableName.substring(
												tableName.lastIndexOf('_') + 1,
												tableName.length()),
												existingFormRecord);
								dynamicFormTableRecordPK
										.setDynamicFormTableRecordId(Long
												.parseLong(colVal

												));
								maxTableRecordId = Long.parseLong(colVal);
								int getMaxSequence = dynamicFormTableRecordDAO
										.getMaxSequenceNumber(Long
												.parseLong(colVal));

								dynamicFormTableRecordPK
										.setSequence(getMaxSequence + 1);

								dynamicFormTableRecord
										.setId(dynamicFormTableRecordPK);

								dynamicFormTableRecordDAO
										.save(dynamicFormTableRecord);

							}

							dataImportUtils.setDynamicFormRecordValues(
									tableName.substring(
											tableName.lastIndexOf('_') + 1,
											tableName.length()),
									dynRecordsName, dynamicFormRecord,
									maxTableRecordId.toString());

						}
					}

					for (HashMap<String, String> colFormMap : colFormMapList) {

						String formIDString = colFormMap.get("formId");
						long formID = Long.parseLong(formIDString);
						if (formID == formId) {
							if (colFormMap.get("tableName") == null) {
								dynamicFormRecord
										.setVersion(Integer.parseInt(colFormMap
												.get("maxVersion")));
								String colName = colFormMap.get("colName");

								dataImportUtils.setDynamicFormRecordValues(
										colName.substring(
												colName.lastIndexOf('_') + 1,
												colName.length()),
										dynRecordsName, dynamicFormRecord,
										colFormMap.get("value"));
							}

						}

					}

					if (existingFormRecord == null) {

						dynamicFormRecordDAO.save(dynamicFormRecord);
					}
				}
			}

		} catch (PayAsiaSystemException e) {
			LOGGER.error(e.getMessage(), e);
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			dataImportLogDTO.setFailureType("payasia.data.import.transaction");
			dataImportLogDTO.setRemarks(e.toString());
			dataImportLogDTO.setFromMessageSource(false);
			dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber) + 1);
			insertOnlyLogs.add(dataImportLogDTO);

			if (dataImportParametersDTO.getTransactionType().equals(
					PayAsiaConstants.ROLLBACK_ON_ERROR)) {
				return insertOnlyLogs;
			}

		}

		return insertOnlyLogs;
	}

	/**
	 * Purpose : Save the data for Upload Type - Update Existing and Insert New
	 * (02).
	 * 
	 * @param employee
	 *            the employee
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @return the list
	 */

	public List<DataImportLogDTO> saveForUpdateAndInsert(Company company,
			DataImportParametersDTO dataImportParametersDTO) {
		List<DataImportKeyValueDTO> empCompanyName = dataImportParametersDTO
				.getEmpColName();
		String rowNumber = dataImportParametersDTO.getRowNumber();
		boolean isDynamic = dataImportParametersDTO.isDynamic();
		List<Long> formIds = dataImportParametersDTO.getFormIds();
		long entityId = dataImportParametersDTO.getEntityId();
		List<String> tableNames = dataImportParametersDTO.getTableNames();
		List<HashMap<String, String>> colFormMapList = dataImportParametersDTO
				.getColFormMapList();
		List<String> dynRecordsName = dataImportParametersDTO
				.getDynRecordsName();
		List<DataImportLogDTO> updateAndInsertLogs = new ArrayList<DataImportLogDTO>();
		try {

			CompanyConditionDTO companyDTO = new CompanyConditionDTO();
			companyDTO.setCompanyCode(company.getCompanyCode());
			Company existingCompanyRecord = companyDAO
					.findByCondition(companyDTO);

			if (empCompanyName.size() == 1
					&& empCompanyName.get(0).getMethodName()
							.equalsIgnoreCase("CompanyCode")) {
				if (existingCompanyRecord != null) {
					company.setCompanyId(existingCompanyRecord.getCompanyId());
				}

			} else {
				if (existingCompanyRecord != null) {
					company.setCompanyId(existingCompanyRecord.getCompanyId());
					BeanUtils.copyProperties(
							company,
							copyCompanyData(empCompanyName, company,
									existingCompanyRecord));
					if (dataImportParametersDTO.getTransactionType().equals(
							PayAsiaConstants.ROLLBACK_ON_ERROR)) {
						companyDAO.update(company);
					} else {
						companyDAO.newTranUpdate(company);
					}
				} else {
					if (dataImportParametersDTO.getTransactionType().equals(
							PayAsiaConstants.ROLLBACK_ON_ERROR)) {
						company = companyDAO.saveReturn(company);
					} else {
						company = companyDAO.newTranSaveReturn(company);
					}
				}
			}

			if (isDynamic) {
				if (company.getCompanyId() != 0) {
					for (Long formId : formIds) {
						boolean containsTableVal = false;
						DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

						dynamicFormRecord.setForm_ID(formId);
						dynamicFormRecord.setCompany_ID(1L);
						dynamicFormRecord.setEntity_ID(entityId);
						dynamicFormRecord.setEntityKey(company.getCompanyId());
						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(company.getCompanyId(), null,
										formId, entityId, 1L);

						for (String tableName : tableNames) {
							int seqNo = 1;
							DynamicFormTableRecord dynamicFormTableRecord = new DynamicFormTableRecord();
							DynamicFormTableRecordPK dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
							containsTableVal = dataImportUtils.setTableValues(
									colFormMapList, formId, dynamicFormRecord,
									tableName, dynamicFormTableRecord,
									dataImportParametersDTO);

							if (containsTableVal) {
								Long maxTableRecordId = dynamicFormTableRecordDAO
										.getMaxTableRecordId() + 1;

								dynamicFormTableRecordPK.setSequence(seqNo);

								if (existingFormRecord == null) {
									dynamicFormTableRecordPK
											.setDynamicFormTableRecordId(maxTableRecordId);
									dynamicFormTableRecord
											.setId(dynamicFormTableRecordPK);
									dynamicFormTableRecordDAO
											.save(dynamicFormTableRecord);
								} else {
									String colVal = dataImportUtils
											.getColValueFile(
													tableName
															.substring(
																	tableName
																			.lastIndexOf('_') + 1,
																	tableName
																			.length()),
													existingFormRecord);
									dynamicFormTableRecordPK
											.setDynamicFormTableRecordId(Long
													.parseLong(colVal

													));
									maxTableRecordId = Long.parseLong(colVal);
									int getMaxSequence = dynamicFormTableRecordDAO
											.getMaxSequenceNumber(Long
													.parseLong(colVal));

									dynamicFormTableRecordPK
											.setSequence(getMaxSequence + 1);

									dynamicFormTableRecord
											.setId(dynamicFormTableRecordPK);
									dynamicFormTableRecordDAO
											.save(dynamicFormTableRecord);
								}

								dataImportUtils.setDynamicFormRecordValues(
										tableName.substring(
												tableName.lastIndexOf('_') + 1,
												tableName.length()),
										dynRecordsName, dynamicFormRecord,
										maxTableRecordId.toString());

							}
						}

						for (HashMap<String, String> colFormMap : colFormMapList) {

							String formIDString = colFormMap.get("formId");
							long formID = Long.parseLong(formIDString);
							if (formID == formId) {
								if (colFormMap.get("tableName") == null) {
									dynamicFormRecord.setVersion(Integer
											.parseInt(colFormMap
													.get("maxVersion")));
									String colName = colFormMap.get("colName");

									dataImportUtils
											.setDynamicFormRecordValues(
													colName.substring(
															colName.lastIndexOf('_') + 1,
															colName.length()),
													dynRecordsName,
													dynamicFormRecord,
													colFormMap.get("value"));
								}

							}

						}

						if (existingFormRecord == null) {

							dynamicFormRecordDAO.save(dynamicFormRecord);
						} else {

							dynamicFormRecord.setRecordId(existingFormRecord
									.getRecordId());
							BeanUtils.copyProperties(dynamicFormRecord,
									dataImportUtils.copyDynamicRecordData(
											dynRecordsName, dynamicFormRecord,
											existingFormRecord));
							dynamicFormRecordDAO.update(dynamicFormRecord);
						}

					}
				}

			}

		} catch (PayAsiaSystemException e) {
			LOGGER.error(e.getMessage(), e);
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			dataImportLogDTO.setFailureType("payasia.data.import.transaction");
			dataImportLogDTO.setFromMessageSource(false);
			dataImportLogDTO.setRemarks(e.toString());
			dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber) + 1);
			updateAndInsertLogs.add(dataImportLogDTO);

			if (dataImportParametersDTO.getTransactionType().equals(
					PayAsiaConstants.ROLLBACK_ON_ERROR)) {
				return updateAndInsertLogs;
			}

		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return updateAndInsertLogs;
	}

	/**
	 * Purpose : To Delete previous records form Dynamic Form Records and
	 * Dynamic Form Table Records for Upload Type: Delete all and Insert
	 * All(03).
	 * 
	 * @param employee
	 *            the employee
	 * @param isDynamic
	 *            the is dynamic
	 * @param formIds
	 *            the form ids
	 * @param entityId
	 *            the entity id
	 * @param tableNames
	 *            the table names
	 * @return
	 */
	public void deleteFormRecords(PayDataCollection payDataCollection,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {

		boolean isDynamic = dataImportParametersDTO.isDynamic();
		List<Long> formIds = dataImportParametersDTO.getFormIds();
		long entityId = dataImportParametersDTO.getEntityId();
		List<String> tableNames = dataImportParametersDTO.getTableNames();

		PayDataCollectionConditionDTO payDataDTO = new PayDataCollectionConditionDTO();
		payDataDTO.setEmployeeNumber(payDataCollection.getEmployee()
				.getEmployeeNumber());
		List<PayDataCollection> existingPayDataCollection = payDataCollectionDAO
				.findByCondition(payDataDTO, 1l, null, null);

		if (!existingPayDataCollection.isEmpty()) {
			for (PayDataCollection existingPayData : existingPayDataCollection) {
				if (isDynamic) {

					for (Long formId : formIds) {

						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(existingPayData
										.getPayDataCollectionId(), null,
										formId, entityId, 1L);
						if (existingFormRecord != null) {

							dynamicFormRecordDAO.delete(existingFormRecord);
						}
						for (String tableName : tableNames) {
							if (existingFormRecord != null) {
								String colVal = dataImportUtils
										.getColValueFile(tableName.substring(
												tableName.lastIndexOf('_') + 1,
												tableName.length()),
												existingFormRecord);
								dynamicFormTableRecordDAO
										.deleteByCondition(Long
												.parseLong(colVal));
							}
						}
					}

				}
			}

		}

	}

	/**
	 * Purpose : Save the data for Upload Type - Delete All and Insert All (03).
	 * 
	 * @param employee
	 *            the employee
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @return the list
	 */

	public List<DataImportLogDTO> saveForDeleteAndInsert(
			PayDataCollection payDataCollection,
			DataImportParametersDTO dataImportParametersDTO) {

		List<DataImportKeyValueDTO> empCompanyName = dataImportParametersDTO
				.getEmpColName();
		String rowNumber = dataImportParametersDTO.getRowNumber();
		boolean isDynamic = dataImportParametersDTO.isDynamic();
		List<Long> formIds = dataImportParametersDTO.getFormIds();
		long entityId = dataImportParametersDTO.getEntityId();
		List<String> tableNames = dataImportParametersDTO.getTableNames();
		List<HashMap<String, String>> colFormMapList = dataImportParametersDTO
				.getColFormMapList();
		List<String> dynRecordsName = dataImportParametersDTO
				.getDynRecordsName();
		List<DataImportLogDTO> updateAndInsertLogs = new ArrayList<DataImportLogDTO>();
		try {

			PayDataCollectionConditionDTO payDataDTO = new PayDataCollectionConditionDTO();
			payDataDTO.setEmployeeNumber(payDataCollection.getEmployee()
					.getEmployeeNumber());
			List<PayDataCollection> existingPayDataCollection = payDataCollectionDAO
					.findByCondition(payDataDTO, 1l, null, null);

			if (empCompanyName.size() == 1
					&& empCompanyName.get(0).getMethodName()
							.equalsIgnoreCase("Employee_Number")) {
				if (!existingPayDataCollection.isEmpty()) {
					payDataCollection
							.setPayDataCollectionId(existingPayDataCollection
									.get(0).getPayDataCollectionId());
				}

			} else {
				if (!existingPayDataCollection.isEmpty()) {

					if (dataImportParametersDTO.getTransactionType().equals(
							PayAsiaConstants.ROLLBACK_ON_ERROR)) {
						payDataCollectionDAO
								.deleteByCondition(payDataCollection
										.getEmployee().getEmployeeId(),
										payDataCollection.getCompany()
												.getCompanyId());
					} else {
						payDataCollectionDAO
								.deleteByEmpCondition(payDataCollection
										.getEmployee().getEmployeeId(),
										payDataCollection.getCompany()
												.getCompanyId());
						payDataCollection = payDataCollectionDAO
								.newTranSaveReturn(payDataCollection);
					}

				} else {
					if (dataImportParametersDTO.getTransactionType().equals(
							PayAsiaConstants.ROLLBACK_ON_ERROR)) {
						payDataCollection = payDataCollectionDAO
								.saveReturn(payDataCollection);
					} else {
						payDataCollection = payDataCollectionDAO
								.newTranSaveReturn(payDataCollection);
					}
				}
			}

			if (isDynamic) {
				if (payDataCollection.getPayDataCollectionId() != 0) {

					for (Long formId : formIds) {
						boolean containsTableVal = false;
						DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

						dynamicFormRecord.setForm_ID(formId);
						dynamicFormRecord.setCompany_ID(1L);
						dynamicFormRecord.setEntity_ID(entityId);
						dynamicFormRecord.setEntityKey(payDataCollection
								.getPayDataCollectionId());
						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(payDataCollection
										.getPayDataCollectionId(), null,
										formId, entityId, 1L);

						for (String tableName : tableNames) {
							int seqNo = 1;
							DynamicFormTableRecord dynamicFormTableRecord = new DynamicFormTableRecord();
							DynamicFormTableRecordPK dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
							containsTableVal = dataImportUtils.setTableValues(
									colFormMapList, formId, dynamicFormRecord,
									tableName, dynamicFormTableRecord,
									dataImportParametersDTO);

							if (containsTableVal) {
								Long maxTableRecordId = dynamicFormTableRecordDAO
										.getMaxTableRecordId() + 1;

								dynamicFormTableRecordPK.setSequence(seqNo);

								if (existingFormRecord == null) {
									dynamicFormTableRecordPK
											.setDynamicFormTableRecordId(maxTableRecordId);
									dynamicFormTableRecord
											.setId(dynamicFormTableRecordPK);
									dynamicFormTableRecordDAO
											.save(dynamicFormTableRecord);
								} else {
									String colVal = dataImportUtils
											.getColValueFile(
													tableName
															.substring(
																	tableName
																			.lastIndexOf('_') + 1,
																	tableName
																			.length()),
													existingFormRecord);
									dynamicFormTableRecordPK
											.setDynamicFormTableRecordId(Long
													.parseLong(colVal

													));
									maxTableRecordId = Long.parseLong(colVal);
									int getMaxSequence = dynamicFormTableRecordDAO
											.getMaxSequenceNumber(Long
													.parseLong(colVal));

									dynamicFormTableRecordPK
											.setSequence(getMaxSequence + 1);

									dynamicFormTableRecord
											.setId(dynamicFormTableRecordPK);
									dynamicFormTableRecordDAO
											.save(dynamicFormTableRecord);
								}

								dataImportUtils.setDynamicFormRecordValues(
										tableName.substring(
												tableName.lastIndexOf('_') + 1,
												tableName.length()),
										dynRecordsName, dynamicFormRecord,
										maxTableRecordId.toString());

							}
						}

						for (HashMap<String, String> colFormMap : colFormMapList) {

							String formIDString = colFormMap.get("formId");
							long formID = Long.parseLong(formIDString);
							if (formID == formId) {
								if (colFormMap.get("tableName") == null) {
									dynamicFormRecord.setVersion(Integer
											.parseInt(colFormMap
													.get("maxVersion")));
									String colName = colFormMap.get("colName");

									dataImportUtils
											.setDynamicFormRecordValues(
													colName.substring(
															colName.lastIndexOf('_') + 1,
															colName.length()),
													dynRecordsName,
													dynamicFormRecord,
													colFormMap.get("value"));
								}

							}

						}

						if (existingFormRecord == null) {
							dynamicFormRecordDAO.save(dynamicFormRecord);
						}

					}
				}

			}

		} catch (PayAsiaSystemException e) {
			LOGGER.error(e.getMessage(), e);
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			dataImportLogDTO.setFailureType("payasia.data.import.transaction");
			dataImportLogDTO.setFromMessageSource(false);
			dataImportLogDTO.setRemarks(e.toString());
			dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber) + 1);
			updateAndInsertLogs.add(dataImportLogDTO);

			if (dataImportParametersDTO.getTransactionType().equals(
					PayAsiaConstants.ROLLBACK_ON_ERROR)) {
				return updateAndInsertLogs;
			}

		}

		return updateAndInsertLogs;
	}

	public Company copyCompanyData(List<DataImportKeyValueDTO> empColName,
			Company company, Company existingCompanyRecord) {
		try {
			Class<?> oldEmpClass = existingCompanyRecord.getClass();
			Class<?> newEmpClass = company.getClass();

			for (DataImportKeyValueDTO colName : empColName) {
				if ("Group_Code".equalsIgnoreCase(colName.getMethodName())) {
					existingCompanyRecord.setCompanyGroup(company
							.getCompanyGroup());
				} else if (colName.getMethodName().equalsIgnoreCase(
						"Country_Name")) {
					existingCompanyRecord.setCountryMaster(company
							.getCountryMaster());
				} else if (colName.getMethodName()
						.equalsIgnoreCase("Frequency")) {
					existingCompanyRecord.setPayslipFrequency(company
							.getPayslipFrequency());
				} else {
					String getMethodName = "get" + colName.getMethodName();
					Method getColMethod;

					getColMethod = newEmpClass.getMethod(getMethodName);

					String setMethodName = "set" + colName.getMethodName();
					Method setMethod;

					if ("bit".equals(colName.getFieldType())) {

						boolean bolVal = (Boolean) getColMethod.invoke(company);
						setMethod = oldEmpClass.getMethod(setMethodName,
								Boolean.TYPE);

						setMethod.invoke(existingCompanyRecord, bolVal);

					} else if ("datetime".equals(colName.getFieldType())) {

						Timestamp dateVal = (Timestamp) getColMethod
								.invoke(company);
						setMethod = oldEmpClass.getMethod(setMethodName,
								Timestamp.class);
						setMethod.invoke(existingCompanyRecord, dateVal);
					} else {
						String stringVal = (String) getColMethod
								.invoke(company);
						setMethod = oldEmpClass.getMethod(setMethodName,
								String.class);
						setMethod.invoke(existingCompanyRecord, stringVal);
					}
				}
			}
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return existingCompanyRecord;
	}
}
