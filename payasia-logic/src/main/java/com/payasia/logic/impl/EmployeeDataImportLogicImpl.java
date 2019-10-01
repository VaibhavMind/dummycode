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

import org.apache.commons.lang.StringUtils;
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
import com.payasia.dao.DataImportHistoryDAO;
import com.payasia.dao.DataImportLogDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.RoleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.EmployeeDataImportLogic;
import com.payasia.logic.EmployeeDataImportStrategy;
import com.payasia.logic.SecurityLogic;

/**
 * The Class EmployeeDataImportLogicImpl.
 */
@Component
public class EmployeeDataImportLogicImpl implements EmployeeDataImportLogic {

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

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

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

	/** The role master dao. */
	@Resource
	RoleMasterDAO roleMasterDAO;

	@Resource(name = "employeeDataImportStrategyCOELogic")
	EmployeeDataImportStrategy employeeDataImportStrategyCOE;

	@Resource(name = "employeeDataImportStrategyROELogic")
	EmployeeDataImportStrategy employeeDataImportStrategyROE;

	EmployeeDataImportStrategy employeeDataImportStrategy = null;

	/** The employee role mapping dao. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	@Resource
	SecurityLogic securityLogic;

	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;

	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDataImportLogicImpl.class);

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
			employeeDataImportStrategy = employeeDataImportStrategyROE;
		} else {
			employeeDataImportStrategy = employeeDataImportStrategyCOE;
		}

		List<DataImportLogDTO> dataImportLogDTOList = new ArrayList<DataImportLogDTO>();

		for (HashMap<String, DataImportKeyValueDTO> map : keyValMapList) {
			Set<String> keySet = map.keySet();
			DataImportKeyValueDTO rowNumberDTO = map.get("rowNumber");
			String rowNumber = rowNumberDTO.getValue();
			boolean isDynamic = false;
			Employee employee = new Employee();
			Company company = companyDAO.findById(companyId);
			employee.setCompany(company);
			List<DataImportKeyValueDTO> empColName = new ArrayList<DataImportKeyValueDTO>();
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
					isDynamic = setFields(colMap, isDynamic, employee,
							empColName, colFormMapList, formIds, tableNames,
							key, valueDTO, value, companyId,
							dynamicFormObjects, dynamicFormVersions,
							dependentsTypeFieldNameList);

				}

			}

			DataImportParametersDTO dataImportParametersDTO = new DataImportParametersDTO();
			dataImportParametersDTO.setColFormMapList(colFormMapList);
			dataImportParametersDTO.setDynamic(isDynamic);
			dataImportParametersDTO.setDynRecordsName(dynRecordsName);
			dataImportParametersDTO.setEmpColName(empColName);
			dataImportParametersDTO.setEntityId(entityId);
			dataImportParametersDTO.setFormIds(formIds);
			dataImportParametersDTO.setRowNumber(rowNumber);
			dataImportParametersDTO.setTableNames(tableNames);
			dataImportParametersDTO.setTransactionType(transactionType);
			dataImportParametersDTO
					.setDependentsTypeFieldNameList(dependentsTypeFieldNameList);
			try {
				switch (uploadType) {
				case PayAsiaConstants.INSERT_NEW:
					employeeDataImportStrategy.insertNew(employee,
							dataImportParametersDTO, companyId);
					break;

				case PayAsiaConstants.UPDATE_AND_INSERT:
					employeeDataImportStrategy.updateORInsert(employee,
							dataImportParametersDTO, companyId);
					break;

				case PayAsiaConstants.DELETE_ALL_AND_INSERT:
					employeeDataImportStrategy.deleteAndInsert(employee,
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

		if (dataImportLogDTOList != null && !dataImportLogDTOList.isEmpty()) {
			throw new PayAsiaDataException(dataImportLogDTOList);
		}
	}

	/**
	 * Purpose: Sets the static/dynamic employee fields.
	 * 
	 * @param colMap
	 *            the col map
	 * @param isDynamic
	 *            the is dynamic
	 * @param employee
	 *            the employee
	 * @param empColName
	 *            the emp col name
	 * @param colFormMapList
	 *            the col form map list
	 * @param formIds
	 *            the form ids
	 * @param tableNames
	 *            the table names
	 * @param key
	 *            the key
	 * @param valueDTO
	 *            the value dto
	 * @param value
	 *            the value
	 * @param companyId
	 *            the company id
	 * @return true, if successful
	 */
	private boolean setFields(
			HashMap<String, EmpDataImportTemplateField> colMap,
			boolean isDynamic, Employee employee,
			List<DataImportKeyValueDTO> empColName,
			List<HashMap<String, String>> colFormMapList, List<Long> formIds,
			List<String> tableNames, String key,
			DataImportKeyValueDTO valueDTO, String value, Long companyId,
			HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions,
			List<String> dependentsTypeFieldNameList) {
		EmpDataImportTemplateField empDataImportTemplateField = colMap.get(key);

		if (empDataImportTemplateField.getDataDictionary().getFieldType()
				.equals(PayAsiaConstants.STATIC_TYPE)) {

			String colName = empDataImportTemplateField.getDataDictionary()
					.getColumnName();

			setStaticFields(employee, colName, valueDTO, empColName);

		} else {

			isDynamic = true;

			dataImportUtils.setDynamicFieldValue(empDataImportTemplateField,
					colFormMapList, tableNames, value, formIds, companyId,
					dynamicFormObjects, dynamicFormVersions,
					dependentsTypeFieldNameList);
		}
		return isDynamic;
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
	 */
	public void setStaticFields(Employee employee, String colName,
			DataImportKeyValueDTO valueDTO,
			List<DataImportKeyValueDTO> empColName) {
		String[] parts = colName.split("_");
		StringBuilder camelCaseColName = new StringBuilder("");

		for (String part : parts) {

			camelCaseColName.append(part.substring(0, 1).toUpperCase()).append(
					part.substring(1).toLowerCase());
		}

		Class<?> employeeClass = employee.getClass();
		String methodName = "set" + camelCaseColName;
		valueDTO.setMethodName(camelCaseColName.toString());
		valueDTO.setStatic(true);

		empColName.add(valueDTO);
		Method method;

		try {

			if (valueDTO.getFieldType().equals(
					PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {
				method = employeeClass.getMethod(methodName, Boolean.TYPE);

				method.invoke(employee,
						Boolean.parseBoolean(valueDTO.getValue()));
			} else if (valueDTO.getFieldType().equals(
					PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {
				method = employeeClass.getMethod(methodName, Timestamp.class);
				Timestamp timestamp = DateUtils.stringToTimestamp(
						valueDTO.getValue(),
						PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
				method.invoke(employee, timestamp);

			} else {
				method = employeeClass.getMethod(methodName, String.class);
				method.invoke(employee, StringUtils.trim(valueDTO.getValue()));
			}

		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

	}

}
