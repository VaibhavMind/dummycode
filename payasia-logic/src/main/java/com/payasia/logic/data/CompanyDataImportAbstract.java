package com.payasia.logic.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaSystemException;
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
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.logic.DataImportUtils;

public class CompanyDataImportAbstract extends DataImportUtilsAbstact {
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

	private static final Logger LOGGER = Logger
			.getLogger(CompanyDataImportAbstract.class);

	/**
	 * Purpose : Save the data for Upload Type - Insert Only (01).
	 * 
	 * @param payslip
	 *            the payslip
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return
	 * @return the list
	 */
	public void insertNew(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {

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
					&& companyColName
							.get(0)
							.getMethodName()
							.equalsIgnoreCase(
									PayAsiaConstants.CAMEL_CASE_COMPANY_CODE)) {
				if (existingCompanyRecord != null) {
					company.setCompanyId(existingCompanyRecord.getCompanyId());
				}

			} else {
				if (existingCompanyRecord == null) {
					company = companyDAO.saveReturn(company);
				} else {
					company.setCompanyId(existingCompanyRecord.getCompanyId());
				}
			}

			if (isDynamic) {
				for (Long formId : formIds) {
					DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

					dynamicFormRecord.setForm_ID(formId);
					dynamicFormRecord.setCompany_ID(companyId);
					dynamicFormRecord.setEntity_ID(entityId);
					dynamicFormRecord.setEntityKey(company.getCompanyId());
					DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
							.getEmpRecords(company.getCompanyId(), null,
									formId, entityId, companyId);

					super.setDynamicValues(dataImportParametersDTO, tableNames,
							colFormMapList, dynRecordsName, formId,
							dynamicFormRecord, existingFormRecord, companyId,
							"update");

					if (existingFormRecord == null) {

						dynamicFormRecordDAO.save(dynamicFormRecord);
					}
				}
			}

		} catch (PayAsiaSystemException ex) {
			super.handleException(rowNumber, insertOnlyLogs, ex);
		}

		if (insertOnlyLogs != null &&  !insertOnlyLogs.isEmpty()) {
			throw new PayAsiaDataException(insertOnlyLogs);
		}
	}

	/**
	 * Purpose : Save the data for Upload Type - Update Existing and Insert New
	 * (02).
	 * 
	 * @param payslip
	 *            the payslip
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return
	 * @return the list
	 */

	public void updateORInsert(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
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
					&& empCompanyName
							.get(0)
							.getMethodName()
							.equalsIgnoreCase(
									PayAsiaConstants.CAMEL_CASE_COMPANY_CODE)) {
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
					companyDAO.update(company);
				} else {
					company = companyDAO.saveReturn(company);
				}
			}

			if (isDynamic) {
				if (company.getCompanyId() != 0) {
					for (Long formId : formIds) {
						DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

						dynamicFormRecord.setForm_ID(formId);
						dynamicFormRecord.setCompany_ID(companyId);
						dynamicFormRecord.setEntity_ID(entityId);
						dynamicFormRecord.setEntityKey(company.getCompanyId());
						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(company.getCompanyId(), null,
										formId, entityId, companyId);

						super.setDynamicValuesForUpdate(
								dataImportParametersDTO, tableNames,
								colFormMapList, dynRecordsName, formId,
								dynamicFormRecord, existingFormRecord,
								companyId, "update");

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

		} catch (PayAsiaSystemException ex) {
			super.handleException(rowNumber, updateAndInsertLogs, ex);

		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		if (updateAndInsertLogs != null &&  !updateAndInsertLogs.isEmpty()) {
			throw new PayAsiaDataException(updateAndInsertLogs);
		}

	}

	/**
	 * Purpose : To Delete previous records form Dynamic Form Records and
	 * Dynamic Form Table Records for Upload Type: Delete all and Insert
	 * All(03).
	 * 
	 * @param company
	 *            the company
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 */
	public void deleteFormRecords(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId,
			Set<String> companyTableRecordDeletedSet) {

		boolean isDynamic = dataImportParametersDTO.isDynamic();
		List<Long> formIds = dataImportParametersDTO.getFormIds();
		long entityId = dataImportParametersDTO.getEntityId();
		List<String> tableNames = dataImportParametersDTO.getTableNames();

		CompanyConditionDTO companyDTO = new CompanyConditionDTO();
		companyDTO.setCompanyCode(company.getCompanyCode());
		Company existingCompanyRecord = companyDAO.findByCondition(companyDTO);

		if (existingCompanyRecord != null) {
			if (isDynamic) {

				for (Long formId : formIds) {

					DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
							.getEmpRecords(
									existingCompanyRecord.getCompanyId(), null,
									formId, entityId, companyId);
					if (existingFormRecord != null) {

						dynamicFormRecordDAO.delete(existingFormRecord);

					}
					for (String tableName : tableNames) {
						if (existingFormRecord != null) {
							if (companyTableRecordDeletedSet == null
									|| !companyTableRecordDeletedSet
											.contains(company.getCompanyCode())) {
								String colVal = dataImportUtils
										.getColValueFile(tableName.substring(
												tableName.lastIndexOf("_") + 1,
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
	 * @param company
	 *            the company
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return
	 * @return the list
	 */

	public void deleteAndInsert(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId,
			Set<String> companyTableRecordDeletedSet) {
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
					&& empCompanyName
							.get(0)
							.getMethodName()
							.equalsIgnoreCase(
									PayAsiaConstants.CAMEL_CASE_COMPANY_CODE)) {
				if (existingCompanyRecord != null) {
					company.setCompanyId(existingCompanyRecord.getCompanyId());
				}

			} else {
				if (existingCompanyRecord != null) {

					companyDAO.delete(existingCompanyRecord);
					BeanUtils.copyProperties(
							company,
							copyCompanyData(empCompanyName, company,
									existingCompanyRecord));
					company.setCompanyId(0);
					company = companyDAO.saveReturn(company);

				} else {
					company = companyDAO.saveReturn(company);
				}
			}

			if (isDynamic) {
				if (company.getCompanyId() != 0) {

					for (Long formId : formIds) {
						DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

						dynamicFormRecord.setForm_ID(formId);
						dynamicFormRecord.setCompany_ID(companyId);
						dynamicFormRecord.setEntity_ID(entityId);
						dynamicFormRecord.setEntityKey(company.getCompanyId());
						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(
										existingCompanyRecord.getCompanyId(),
										null, formId, entityId, companyId);

						if (existingFormRecord != null) {
							deleteFormRecords(company, dataImportParametersDTO,
									companyId, companyTableRecordDeletedSet);
						}
						super.setDynamicValues(dataImportParametersDTO,
								tableNames, colFormMapList, dynRecordsName,
								formId, dynamicFormRecord, existingFormRecord,
								companyId, "update");

						dynamicFormRecordDAO.save(dynamicFormRecord);

					}
				}

			}

		} catch (PayAsiaSystemException ex) {
			super.handleException(rowNumber, updateAndInsertLogs, ex);

		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		if (updateAndInsertLogs != null &&  !updateAndInsertLogs.isEmpty()) {
			throw new PayAsiaDataException(updateAndInsertLogs);
		}

	}

	/**
	 * Copy company data.
	 * 
	 * @param empColName
	 *            the emp col name
	 * @param company
	 *            the company
	 * @param existingCompanyRecord
	 *            the existing company record
	 * @return the company
	 */
	public Company copyCompanyData(List<DataImportKeyValueDTO> empColName,
			Company company, Company existingCompanyRecord) {
		try {
			Class<?> oldEmpClass = existingCompanyRecord.getClass();
			Class<?> newEmpClass = company.getClass();

			for (DataImportKeyValueDTO colName : empColName) {
				if (colName.getMethodName().equalsIgnoreCase(
						PayAsiaConstants.COLUMN_NAME_GROUP_CODE)) {
					existingCompanyRecord.setCompanyGroup(company
							.getCompanyGroup());
				} else if (colName.getMethodName().equalsIgnoreCase(
						PayAsiaConstants.COLUMN_NAME_COUNTRY_NAME)) {
					existingCompanyRecord.setCountryMaster(company
							.getCountryMaster());
				} else if (colName.getMethodName().equalsIgnoreCase(
						PayAsiaConstants.COLUMN_NAME_FREQUENCY)) {
					existingCompanyRecord.setPayslipFrequency(company
							.getPayslipFrequency());
				} else if (colName.getMethodName().equalsIgnoreCase(
						PayAsiaConstants.COLUMN_NAME_TIME_ZONE_NAME)) {
					existingCompanyRecord.setTimeZoneMaster(company
							.getTimeZoneMaster());
				} else {
					String getMethodName = "get" + colName.getMethodName();
					Method getColMethod;

					getColMethod = newEmpClass.getMethod(getMethodName);

					String setMethodName = "set" + colName.getMethodName();
					Method setMethod;

					if (colName.getFieldType().equals(
							PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {

						boolean bolVal = (Boolean) getColMethod.invoke(company);
						setMethod = oldEmpClass.getMethod(setMethodName,
								Boolean.TYPE);

						setMethod.invoke(existingCompanyRecord, bolVal);

					} else if (colName.getFieldType().equals(
							PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {

						Timestamp dateVal = (Timestamp) getColMethod
								.invoke(company);
						setMethod = oldEmpClass.getMethod(setMethodName,
								Timestamp.class);
						setMethod.invoke(existingCompanyRecord, dateVal);
					} else if (colName.getFieldType().equals(
							PayAsiaConstants.PAYASIA_DATATYPE_INT)) {
						Integer intVal = (Integer) getColMethod.invoke(company);
						setMethod = oldEmpClass.getMethod(setMethodName,
								Integer.TYPE);
						setMethod.invoke(existingCompanyRecord, intVal);
					} else {
						String stringVal = (String) getColMethod
								.invoke(company);
						setMethod = oldEmpClass.getMethod(setMethodName,
								String.class);
						setMethod.invoke(existingCompanyRecord, stringVal);
					}
				}
			}
		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return existingCompanyRecord;
	}

}
