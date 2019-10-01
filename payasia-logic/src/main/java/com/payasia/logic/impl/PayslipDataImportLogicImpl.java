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
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DataImportForm;
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
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.Payslip;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.dao.bean.PayslipUploadHistory;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.PaySlipDataImportStrategy;
import com.payasia.logic.PayslipDataImportLogic;

/**
 * The Class PayslipDataImportLogicImpl.
 */
@Component
public class PayslipDataImportLogicImpl implements PayslipDataImportLogic {

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

	@Resource(name = "paySlipDataImportStrategyCOELogic")
	PaySlipDataImportStrategy paySlipDataImportStrategyCOE;

	@Resource(name = "paySlipDataImportStrategyROELogic")
	PaySlipDataImportStrategy paySlipDataImportStrategyROE;

	PaySlipDataImportStrategy paySlipDataImportStrategy = null;

	private static final Logger LOGGER = Logger
			.getLogger(PayslipDataImportLogicImpl.class);

	/**
	 * Purpose : To Save the valid data and Log the transaction errors.
	 * 
	 * @param keyValMapList
	 *            the key val map list
	 * @param dataImportForm
	 *            the data import form
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
			DataImportForm dataImportForm, long entityId,
			HashMap<String, EmpDataImportTemplateField> colMap,
			String uploadType, String transactionType, Long companyId,
			HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions) {
		List<String> dependentsTypeFieldNameList = new ArrayList<String>();
		if (transactionType.equals(PayAsiaConstants.ROLLBACK_ON_ERROR)) {
			paySlipDataImportStrategy = paySlipDataImportStrategyROE;
		} else {
			paySlipDataImportStrategy = paySlipDataImportStrategyCOE;
		}

		Company company = companyDAO.findById(companyId);
		MonthMaster monthMaster = monthMasterDAO.findById(dataImportForm
				.getMonthId());
		List<DataImportLogDTO> dataImportLogDTOList = new ArrayList<DataImportLogDTO>();
		List<String> employeeNumberList = new ArrayList<String>();
		for (HashMap<String, DataImportKeyValueDTO> map : keyValMapList) {
			Set<String> keySet = map.keySet();
			DataImportKeyValueDTO rowNumberDTO = map.get("rowNumber");
			String rowNumber = rowNumberDTO.getValue();
			boolean isDynamic = false;
			boolean isFieldValid = true;
			Payslip payslip = new Payslip();
			payslip.setCompany(company);
			payslip.setMonthMaster(monthMaster);
			payslip.setYear(dataImportForm.getYear());
			payslip.setPayslipFrequency(company.getPayslipFrequency());

			if (company
					.getPayslipFrequency()
					.getFrequency()
					.equalsIgnoreCase(
							PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
				payslip.setPart(1);
			} else {
				payslip.setPart(dataImportForm.getPart());
			}
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
							setStaticFields(payslip, colName, valueDTO,
									companyColName, rowNumber,
									employeeNumberList);
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
						paySlipDataImportStrategy.insertNew(payslip,
								dataImportParametersDTO, companyId);
						break;

					case PayAsiaConstants.UPDATE_AND_INSERT:
						paySlipDataImportStrategy.updateORInsert(payslip,
								dataImportParametersDTO, companyId);
						break;

					case PayAsiaConstants.DELETE_ALL_AND_INSERT:
						paySlipDataImportStrategy.deleteAndInsert(payslip,
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
		} else {
			PayslipUploadHistory payslipUploadHistory = new PayslipUploadHistory();
			payslipUploadHistory.setCompany(company);
			payslipUploadHistory.setPayslip_Upload_Date(DateUtils
					.getCurrentTimestampWithTime());
			payslipUploadHistory.setMonthMaster(monthMaster);
			payslipUploadHistory.setYear(dataImportForm.getYear());
			payslipUploadHistoryDAO.save(payslipUploadHistory);
		}
		dataImportForm.setEmployeeNumberList(employeeNumberList);
	}

	/**
	 * Purpose : Sets the static fields value.
	 * 
	 * @param payslip
	 *            the payslip
	 * @param colName
	 *            the col name
	 * @param valueDTO
	 *            the value dto
	 * @param empColName
	 *            the emp col name
	 * @param rowNumber
	 *            the row number
	 */
	private void setStaticFields(Payslip payslip, String colName,
			DataImportKeyValueDTO valueDTO,
			List<DataImportKeyValueDTO> empColName, String rowNumber,
			List<String> employeeNumberList) {

		List<DataImportLogDTO> dataImportLogDTOList = new ArrayList<DataImportLogDTO>();
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setFailureType("payasia.data.import.transaction");
		dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber) + 1);
		dataImportLogDTO.setFromMessageSource(false);
		if (colName
				.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {

			EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
			conditionDTO.setEmployeeNumber(valueDTO.getValue());

			List<Employee> employees = employeeDAO.findAllByCondition(
					conditionDTO, null, null, payslip.getCompany()
							.getCompanyId());

			if (employees.size() == 0) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO
						.setRemarks("payasia.data.import.employee.code.error");
				dataImportLogDTO.setColName(valueDTO.getKey());
				dataImportLogDTOList.add(dataImportLogDTO);
			} else {
				valueDTO.setMethodName(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER);
				valueDTO.setStatic(true);

				empColName.add(valueDTO);
				payslip.setEmployee(employees.get(0));
				employeeNumberList.add(valueDTO.getValue());
			}

		} else if (colName
				.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_MONTH_NAME)) {

			MonthMaster month = monthMasterDAO.findByMonthName(valueDTO
					.getValue());

			if (month == null) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO.setRemarks("payasia.data.import.month.error");
				dataImportLogDTO.setColName(valueDTO.getKey());
				dataImportLogDTOList.add(dataImportLogDTO);
			} else {
				valueDTO.setMethodName(PayAsiaConstants.COLUMN_NAME_MONTH_NAME);
				valueDTO.setStatic(true);

				empColName.add(valueDTO);
				payslip.setMonthMaster(month);
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
				payslip.setPayslipFrequency(payslipFrequency);
			}

		} else {

			String[] parts = colName.split("_");
			StringBuilder camelCaseColName = new StringBuilder("");

			for (String part : parts) {

				camelCaseColName.append(part.substring(0, 1).toUpperCase())
						.append(part.substring(1).toLowerCase());
			}

			Class<?> companyClass = payslip.getClass();
			String methodName = "set" + camelCaseColName;
			valueDTO.setMethodName(camelCaseColName.toString());
			valueDTO.setStatic(true);

			empColName.add(valueDTO);
			Method method;

			try {

				if (valueDTO.getFieldType().equals(
						PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {
					method = companyClass.getMethod(methodName, Boolean.TYPE);

					method.invoke(payslip,
							Boolean.parseBoolean(valueDTO.getValue()));
				} else if (valueDTO.getFieldType().equals(
						PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {
					method = companyClass
							.getMethod(methodName, Timestamp.class);
					Timestamp timestamp = DateUtils.stringToTimestamp(
							valueDTO.getValue(),
							PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
					method.invoke(payslip, timestamp);

				} else if (valueDTO.getFieldType().equals(
						PayAsiaConstants.PAYASIA_DATATYPE_INT)) {

					method = companyClass.getMethod(methodName, Integer.TYPE);

					method.invoke(payslip,
							Integer.parseInt(valueDTO.getValue()));
				} else {
					method = companyClass.getMethod(methodName, String.class);
					method.invoke(payslip, valueDTO.getValue());
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
