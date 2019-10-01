package com.payasia.logic.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataImportHistoryDAO;
import com.payasia.dao.DataImportLogDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.PayslipFrequencyDAO;
import com.payasia.dao.PayslipUploadHistoryDAO;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.DynamicFormTableRecordPK;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.DataImportUtils;

public abstract class PayslipDataImportAbstract extends DataImportUtilsAbstact {

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The payslip dao. */
	@Resource
	PayslipDAO payslipDAO;

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

	/** The month master dao. */
	@Resource
	MonthMasterDAO monthMasterDAO;

	/** The payslip frequency dao. */
	@Resource
	PayslipFrequencyDAO payslipFrequencyDAO;

	/** The payslip upload history dao. */
	@Resource
	PayslipUploadHistoryDAO payslipUploadHistoryDAO;

	private static final Logger LOGGER = Logger
			.getLogger(PayslipDataImportAbstract.class);

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
	public void insertNew(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		List<DataImportLogDTO> insertOnlyLogs = new ArrayList<DataImportLogDTO>();
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
			PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
			payslipConditionDTO.setEmployeeId(payslip.getEmployee()
					.getEmployeeId());
			payslipConditionDTO.setCompanyId(payslip.getCompany()
					.getCompanyId());
			payslipConditionDTO.setMonthMasterId(payslip.getMonthMaster()
					.getMonthId());
			payslipConditionDTO.setPart(payslip.getPart());
			payslipConditionDTO.setPayslipFrequencyId(payslip
					.getPayslipFrequency().getPayslipFrequencyID());
			payslipConditionDTO.setYear(payslip.getYear());
			List<Payslip> existingPayslip = payslipDAO
					.findByCondition(payslipConditionDTO);

			if (existingPayslip == null || existingPayslip.size() == 0) {
				payslip.setCreatedDate(DateUtils.getCurrentTimestamp());
				payslip = payslipDAO.saveReturn(payslip);

			} else {
				payslip.setPayslipId(existingPayslip.get(0).getPayslipId());
			}

			if (isDynamic) {
				for (Long formId : formIds) {
					DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

					dynamicFormRecord.setForm_ID(formId);
					dynamicFormRecord.setCompany_ID(companyId);
					dynamicFormRecord.setEntity_ID(entityId);
					dynamicFormRecord.setEntityKey(payslip.getPayslipId());
					DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
							.getEmpRecords(payslip.getPayslipId(), null,
									formId, entityId, companyId);

					setDynamicFields(dataImportParametersDTO, tableNames,
							colFormMapList, dynRecordsName, formId,
							dynamicFormRecord, existingFormRecord);

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
	 * Sets the dynamic fields.
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
	private void setDynamicFields(
			DataImportParametersDTO dataImportParametersDTO,
			List<String> tableNames,
			List<HashMap<String, String>> colFormMapList,
			List<String> dynRecordsName, Long formId,
			DynamicFormRecord dynamicFormRecord,
			DynamicFormRecord existingFormRecord) {
		boolean containsTableVal;
		for (String tableName : tableNames) {
			int seqNo = 1;
			DynamicFormTableRecord dynamicFormTableRecord = new DynamicFormTableRecord();
			DynamicFormTableRecordPK dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
			containsTableVal = dataImportUtils.setTableValues(colFormMapList,
					formId, dynamicFormRecord, tableName,
					dynamicFormTableRecord, dataImportParametersDTO);

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
						String colVal = dataImportUtils
								.getColValueFile(tableName.substring(
										tableName.lastIndexOf("_") + 1,
										tableName.length()), existingFormRecord);

						if (StringUtils.isBlank(colVal) || colVal == null) {
							dynamicFormTableRecordPK
									.setDynamicFormTableRecordId(maxTableRecordId);
						} else {
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
						}

						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);

					}

					dataImportUtils.setDynamicFormRecordValues(tableName
							.substring(tableName.lastIndexOf("_") + 1,
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

					dataImportUtils.setDynamicFormRecordValues(colName
							.substring(colName.lastIndexOf("_") + 1,
									colName.length()), dynRecordsName,
							dynamicFormRecord, colFormMap.get("value"));
				}

			}

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

	public void updateORInsert(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		List<DataImportKeyValueDTO> payslipColName = dataImportParametersDTO
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

			PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
			payslipConditionDTO.setEmployeeId(payslip.getEmployee()
					.getEmployeeId());
			payslipConditionDTO.setCompanyId(payslip.getCompany()
					.getCompanyId());
			payslipConditionDTO.setMonthMasterId(payslip.getMonthMaster()
					.getMonthId());
			payslipConditionDTO.setPart(payslip.getPart());
			payslipConditionDTO.setPayslipFrequencyId(payslip
					.getPayslipFrequency().getPayslipFrequencyID());
			payslipConditionDTO.setYear(payslip.getYear());
			List<Payslip> existingPayslip = payslipDAO
					.findByCondition(payslipConditionDTO);

			if (existingPayslip != null &&  !existingPayslip.isEmpty()) {
				payslip.setPayslipId(existingPayslip.get(0).getPayslipId());
				BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
				beanUtilsBean
						.getConvertUtils()
						.register(
								new org.apache.commons.beanutils.converters.BigDecimalConverter(
										null), BigDecimal.class);
				beanUtilsBean
						.getConvertUtils()
						.register(
								new org.apache.commons.beanutils.converters.SqlTimestampConverter(
										null), Timestamp.class);
				beanUtilsBean.copyProperties(
						payslip,
						copyPayslipData(payslipColName, payslip,
								existingPayslip.get(0)));
				payslip.setUpdatedDate(DateUtils.getCurrentTimestamp());
				payslipDAO.update(payslip);
			} else {
				payslip.setCreatedDate(DateUtils.getCurrentTimestamp());
				payslip = payslipDAO.saveReturn(payslip);
			}

			if (isDynamic) {
				if (payslip.getPayslipId() != 0) {
					for (Long formId : formIds) {
						DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

						dynamicFormRecord.setForm_ID(formId);
						dynamicFormRecord.setCompany_ID(companyId);
						dynamicFormRecord.setEntity_ID(entityId);
						dynamicFormRecord.setEntityKey(payslip.getPayslipId());
						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(payslip.getPayslipId(), null,
										formId, entityId, companyId);

						setDynamicFields(dataImportParametersDTO, tableNames,
								colFormMapList, dynRecordsName, formId,
								dynamicFormRecord, existingFormRecord);

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
	 * @param payslip
	 *            the payslip
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 */
	private void deleteFormRecords(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {

		boolean isDynamic = dataImportParametersDTO.isDynamic();
		List<Long> formIds = dataImportParametersDTO.getFormIds();
		long entityId = dataImportParametersDTO.getEntityId();
		List<String> tableNames = dataImportParametersDTO.getTableNames();
		PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
		payslipConditionDTO
				.setEmployeeId(payslip.getEmployee().getEmployeeId());
		payslipConditionDTO.setCompanyId(payslip.getCompany().getCompanyId());
		payslipConditionDTO.setMonthMasterId(payslip.getMonthMaster()
				.getMonthId());
		payslipConditionDTO.setPart(payslip.getPart());
		payslipConditionDTO.setPayslipFrequencyId(payslip.getPayslipFrequency()
				.getPayslipFrequencyID());
		payslipConditionDTO.setYear(payslip.getYear());
		List<Payslip> existingPayslips = payslipDAO
				.findByCondition(payslipConditionDTO);

		if (existingPayslips != null) {
			for (Payslip existingPayslip : existingPayslips) {
				if (isDynamic) {

					for (Long formId : formIds) {

						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(existingPayslip.getPayslipId(),
										null, formId, entityId, companyId);
						if (existingFormRecord != null) {

							dynamicFormRecordDAO.delete(existingFormRecord);

						}
						for (String tableName : tableNames) {
							if (existingFormRecord != null) {
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
	 * @param payslip
	 *            the payslip
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 * @return
	 * @return the list
	 */

	public void deleteAndInsert(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {

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
			PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
			payslipConditionDTO.setEmployeeId(payslip.getEmployee()
					.getEmployeeId());
			payslipConditionDTO.setCompanyId(payslip.getCompany()
					.getCompanyId());
			payslipConditionDTO.setMonthMasterId(payslip.getMonthMaster()
					.getMonthId());
			payslipConditionDTO.setPart(payslip.getPart());
			payslipConditionDTO.setPayslipFrequencyId(payslip
					.getPayslipFrequency().getPayslipFrequencyID());
			payslipConditionDTO.setYear(payslip.getYear());
			List<Payslip> existingPayslip = payslipDAO
					.findByCondition(payslipConditionDTO);

			if (existingPayslip != null) {

				payslipDAO.deleteByCondition(payslipConditionDTO);
				payslip.setPayslipId(0);
				payslip.setCreatedDate(DateUtils.getCurrentTimestamp());
				payslip = payslipDAO.saveReturn(payslip);

			} else {
				payslip.setCreatedDate(DateUtils.getCurrentTimestamp());
				payslip = payslipDAO.saveReturn(payslip);
			}

			if (isDynamic) {
				if (payslip.getPayslipId() != 0) {

					for (Long formId : formIds) {
						DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

						dynamicFormRecord.setForm_ID(formId);
						dynamicFormRecord.setCompany_ID(companyId);
						dynamicFormRecord.setEntity_ID(entityId);
						dynamicFormRecord.setEntityKey(payslip.getPayslipId());
						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(payslip.getPayslipId(), null,
										formId, entityId, companyId);

						if (existingFormRecord != null) {
							deleteFormRecords(payslip, dataImportParametersDTO,
									companyId);
						}

						setDynamicFields(dataImportParametersDTO, tableNames,
								colFormMapList, dynRecordsName, formId,
								dynamicFormRecord, existingFormRecord);

						dynamicFormRecordDAO.save(dynamicFormRecord);
					}
				}

			}

		} catch (PayAsiaSystemException ex) {

			super.handleException(rowNumber, updateAndInsertLogs, ex);
		}

		if (updateAndInsertLogs != null &&  !updateAndInsertLogs.isEmpty()) {
			throw new PayAsiaDataException(updateAndInsertLogs);
		}

	}

	/**
	 * Copy payslip data.
	 * 
	 * @param empColName
	 *            the emp col name
	 * @param payslip
	 *            the payslip
	 * @param existingPayslip
	 *            the existing payslip
	 * @return the payslip
	 */
	private Payslip copyPayslipData(List<DataImportKeyValueDTO> empColName,
			Payslip payslip, Payslip existingPayslip) {
		try {
			Class<?> oldEmpClass = existingPayslip.getClass();
			Class<?> newEmpClass = payslip.getClass();

			for (DataImportKeyValueDTO colName : empColName) {
				if (colName.getMethodName().equalsIgnoreCase(
						PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {
					existingPayslip.setEmployee(payslip.getEmployee());
				} else if (colName.getMethodName().equalsIgnoreCase(
						PayAsiaConstants.COLUMN_NAME_MONTH_NAME)) {
					existingPayslip.setMonthMaster(payslip.getMonthMaster());
				} else if (colName.getMethodName().equalsIgnoreCase(
						PayAsiaConstants.COLUMN_NAME_FREQUENCY)) {
					existingPayslip.setPayslipFrequency(payslip
							.getPayslipFrequency());
				} else {
					String getMethodName = "get" + colName.getMethodName();
					Method getColMethod;

					getColMethod = newEmpClass.getMethod(getMethodName);

					String setMethodName = "set" + colName.getMethodName();
					Method setMethod;

					if (colName.getFieldType().equals(
							PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {

						boolean bolVal = (Boolean) getColMethod.invoke(payslip);
						setMethod = oldEmpClass.getMethod(setMethodName,
								Boolean.TYPE);

						setMethod.invoke(existingPayslip, bolVal);

					} else if (colName.getFieldType().equals(
							PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {

						Timestamp dateVal = (Timestamp) getColMethod
								.invoke(payslip);
						setMethod = oldEmpClass.getMethod(setMethodName,
								Timestamp.class);
						setMethod.invoke(existingPayslip, dateVal);
					} else if (colName.getFieldType().equals(
							PayAsiaConstants.PAYASIA_DATATYPE_INT)) {

						int intVal = (Integer) getColMethod.invoke(payslip);
						setMethod = oldEmpClass.getMethod(setMethodName,
								Integer.TYPE);

						setMethod.invoke(existingPayslip, intVal);
					} else {
						String stringVal = (String) getColMethod
								.invoke(payslip);
						setMethod = oldEmpClass.getMethod(setMethodName,
								String.class);
						setMethod.invoke(existingPayslip, stringVal);
					}
				}
			}
		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return existingPayslip;
	}
}
