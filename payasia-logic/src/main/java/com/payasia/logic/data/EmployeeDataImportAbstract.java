package com.payasia.logic.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.PasswordUtils;
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
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EmployeeRoleMappingPK;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.SecurityLogic;

public class EmployeeDataImportAbstract extends DataImportUtilsAbstact {

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

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

	@Resource
	SecurityLogic securityLogic;

	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;

	@Resource
	DataImportUtils dataImportUtils;

	/** The employee role mapping dao. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDataImportAbstract.class);

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
	public void insertNew(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		List<DataImportLogDTO> insertOnlyLogs = new ArrayList<DataImportLogDTO>();

		List<DataImportKeyValueDTO> empColName = dataImportParametersDTO
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

			EmployeeConditionDTO empDTO = new EmployeeConditionDTO();

			EmployeeShortListDTO empShortListDTO = new EmployeeShortListDTO();
			empShortListDTO.setEmployeeShortList(false);
			empDTO.setEmployeeShortListDTO(empShortListDTO);

			empDTO.setEmployeeNumber(employee.getEmployeeNumber());
			int empRecordSize = 0;
			List<Employee> existingEmpRecords = new ArrayList<Employee>();
			empRecordSize = employeeDAO.findAllByCondition(empDTO, null, null,
					employee.getCompany().getCompanyId()).size();

			if (empRecordSize > 0) {
				existingEmpRecords = employeeDAO.findAllByCondition(empDTO,
						null, null, employee.getCompany().getCompanyId());
			}

			if (empColName.size() == 1
					&& empColName
							.get(0)
							.getMethodName()
							.equalsIgnoreCase(
									PayAsiaConstants.CAMEL_CASE_EMPLOYEE_NUMBER)) {
				if (empRecordSize > 0) {
					employee.setEmployeeId(existingEmpRecords.get(0)
							.getEmployeeId());
				} else {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setFailureType("payasia.data.import.transaction");
					dataImportLogDTO.setFromMessageSource(true);
					dataImportLogDTO
							.setRemarks("payasia.data.import.employee.code.error");
					dataImportLogDTO
							.setRowNumber(Long.parseLong(rowNumber) + 1);
					insertOnlyLogs.add(dataImportLogDTO);
					throw new PayAsiaDataException(insertOnlyLogs);
				}

			} else {
				if (empRecordSize == 0) {

					employee = employeeDAO.saveReturn(employee);
					EmployeeLoginDetail employeeLoginDetail = new EmployeeLoginDetail();
					String salt = securityLogic.generateSalt();
					String encryptPassWord = securityLogic.encrypt(
							PasswordUtils.getRandomPassword(), salt);
					employeeLoginDetail.setLoginName(employee
							.getEmployeeNumber().trim());
					employeeLoginDetail.setPassword(encryptPassWord);
					employeeLoginDetail.setSalt(salt);
					employeeLoginDetail.setEmployee(employee);
					employeeLoginDetailDAO.save(employeeLoginDetail);

					setDefaultRoleMapping(employee.getEmployeeId(), companyId,
							PayAsiaConstants.TRANSACTION_TYPE_OLD);
				} else {
					employee.setEmployeeId(existingEmpRecords.get(0)
							.getEmployeeId());
				}
			}

			if (isDynamic) {
				for (Long formId : formIds) {
					DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

					dynamicFormRecord.setForm_ID(formId);
					dynamicFormRecord.setCompany_ID(companyId);
					dynamicFormRecord.setEntity_ID(entityId);
					dynamicFormRecord.setEntityKey(employee.getEmployeeId());

					DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
							.getEmpRecords(employee.getEmployeeId(), null,
									formId, entityId, companyId);
					super.setDynamicValues(dataImportParametersDTO, tableNames,
							colFormMapList, dynRecordsName, formId,
							dynamicFormRecord, existingFormRecord, companyId,
							"insert");

					if (existingFormRecord == null) {
						dynamicFormRecordDAO.save(dynamicFormRecord);
					}
				}
			}

		} catch (PayAsiaSystemException ex) {
			super.handleException(rowNumber, insertOnlyLogs, ex);

		}
		if (insertOnlyLogs != null && !insertOnlyLogs.isEmpty()) {
			throw new PayAsiaDataException(insertOnlyLogs);
		}

	}

	/**
	 * Purpose: Sets the default role to the newly added Employees.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 * @param transaction
	 *            the transaction
	 */
	private void setDefaultRoleMapping(long employeeId, long companyId,
			String transaction) {
		RoleMaster roleMaster = roleMasterDAO.findByRoleName(
				PayAsiaConstants.EMPLOYEE_DEFAULT_ROLE, companyId);

		EmployeeRoleMapping empRoleMapping = new EmployeeRoleMapping();
		EmployeeRoleMappingPK employeeRoleMappingPK = new EmployeeRoleMappingPK();
		employeeRoleMappingPK.setCompanyId(companyId);
		employeeRoleMappingPK.setEmployeeId(employeeId);
		employeeRoleMappingPK.setRoleId(roleMaster.getRoleId());
		empRoleMapping.setId(employeeRoleMappingPK);
		if (transaction.equalsIgnoreCase(PayAsiaConstants.TRANSACTION_TYPE_NEW)) {
			employeeRoleMappingDAO.newTranSave(empRoleMapping);
		} else {
			employeeRoleMappingDAO.save(empRoleMapping);
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

	public void updateORInsert(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		List<DataImportKeyValueDTO> empColName = dataImportParametersDTO
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

			EmployeeConditionDTO empDTO = new EmployeeConditionDTO();

			EmployeeShortListDTO empShortListDTO = new EmployeeShortListDTO();
			empShortListDTO.setEmployeeShortList(false);
			empDTO.setEmployeeShortListDTO(empShortListDTO);

			empDTO.setEmployeeNumber(employee.getEmployeeNumber());
			int empRecordSize = 0;
			List<Employee> existingEmpRecords = new ArrayList<Employee>();
			empRecordSize = employeeDAO.findAllByCondition(empDTO, null, null,
					employee.getCompany().getCompanyId()).size();

			if (empRecordSize > 0) {
				existingEmpRecords = employeeDAO.findAllByCondition(empDTO,
						null, null, employee.getCompany().getCompanyId());
			}

			if (empColName.size() == 1
					&& empColName
							.get(0)
							.getMethodName()
							.equalsIgnoreCase(
									PayAsiaConstants.CAMEL_CASE_EMPLOYEE_NUMBER)) {
				if (empRecordSize > 0) {
					employee.setEmployeeId(existingEmpRecords.get(0)
							.getEmployeeId());
				} else {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setFailureType("payasia.data.import.transaction");
					dataImportLogDTO.setFromMessageSource(true);
					dataImportLogDTO
							.setRemarks("payasia.data.import.employee.code.error");
					dataImportLogDTO
							.setRowNumber(Long.parseLong(rowNumber) + 1);
					updateAndInsertLogs.add(dataImportLogDTO);
					throw new PayAsiaDataException(updateAndInsertLogs);
				}

			} else {
				if (empRecordSize > 0) {
					employee.setEmployeeId(existingEmpRecords.get(0)
							.getEmployeeId());
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
							employee,
							copyEmployeeData(empColName, employee,
									existingEmpRecords.get(0)));

					employeeDAO.update(employee);

				} else {

					employee = employeeDAO.saveReturn(employee);

					EmployeeLoginDetail employeeLoginDetail = new EmployeeLoginDetail();
					String salt = securityLogic.generateSalt();
					String encryptPassWord = securityLogic.encrypt(
							PasswordUtils.getRandomPassword(), salt);
					employeeLoginDetail.setLoginName(employee
							.getEmployeeNumber().trim());
					employeeLoginDetail.setPassword(encryptPassWord);
					employeeLoginDetail.setSalt(salt);
					employeeLoginDetail.setEmployee(employee);
					employeeLoginDetailDAO.save(employeeLoginDetail);

					setDefaultRoleMapping(employee.getEmployeeId(), companyId,
							PayAsiaConstants.TRANSACTION_TYPE_OLD);
				}
			}

			if (isDynamic) {
				if (employee.getEmployeeId() != 0) {
					for (Long formId : formIds) {
						DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

						dynamicFormRecord.setForm_ID(formId);
						dynamicFormRecord.setCompany_ID(companyId);
						dynamicFormRecord.setEntity_ID(entityId);
						dynamicFormRecord
								.setEntityKey(employee.getEmployeeId());

						DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(employee.getEmployeeId(), null,
										formId, entityId, companyId);
						String mode = "";
						if (existingFormRecord == null) {
							mode = "insert";
						} else {
							mode = "update";
						}

						super.setDynamicValuesForUpdate(
								dataImportParametersDTO, tableNames,
								colFormMapList, dynRecordsName, formId,
								dynamicFormRecord, existingFormRecord,
								companyId, mode);

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

		if (updateAndInsertLogs != null && !updateAndInsertLogs.isEmpty()) {
			throw new PayAsiaDataException(updateAndInsertLogs);
		}
	}

	/**
	 * Purpose : To Delete previous records form Dynamic Form Records and
	 * Dynamic Form Table Records for Upload Type: Delete all and Insert
	 * All(03).
	 * 
	 * @param employee
	 *            the employee
	 * @param dataImportParametersDTO
	 *            the data import parameters dto
	 * @param companyId
	 *            the company id
	 */
	public void deleteFormRecords(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId,
			Set<String> employeeTableRecordDeletedSet) {

		boolean isDynamic = dataImportParametersDTO.isDynamic();
		List<Long> formIds = dataImportParametersDTO.getFormIds();
		long entityId = dataImportParametersDTO.getEntityId();
		List<String> tableNames = dataImportParametersDTO.getTableNames();

		EmployeeConditionDTO empDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO empShortListDTO = new EmployeeShortListDTO();
		empShortListDTO.setEmployeeShortList(false);
		empDTO.setEmployeeShortListDTO(empShortListDTO);

		empDTO.setEmployeeNumber(employee.getEmployeeNumber());
		int empRecordSize = 0;

		empRecordSize = employeeDAO.findAllByCondition(empDTO, null, null,
				employee.getCompany().getCompanyId()).size();

		if (empRecordSize > 0) {
			List<Employee> existingEmpRecords = employeeDAO.findAllByCondition(
					empDTO, null, null, employee.getCompany().getCompanyId());
			if (isDynamic) {

				for (Long formId : formIds) {

					DynamicFormRecord existingFormRecord = dynamicFormRecordDAO
							.getEmpRecords(existingEmpRecords.get(0)
									.getEmployeeId(), null, formId, entityId,
									companyId);

					for (String tableName : tableNames) {
						if (existingFormRecord != null) {
							String colVal = dataImportUtils.getColValueFile(
									tableName.substring(
											tableName.lastIndexOf("_") + 1,
											tableName.length()),
									existingFormRecord);
							if (StringUtils.isNotBlank(colVal)) {
								if (employeeTableRecordDeletedSet == null
										|| !employeeTableRecordDeletedSet
												.contains(employee
														.getEmployeeNumber())) {
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

	public void deleteAndInsert(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId,
			Set<String> employeeTableRecordDeletedSet) {

		List<DataImportKeyValueDTO> empColName = dataImportParametersDTO
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

			EmployeeConditionDTO empDTO = new EmployeeConditionDTO();

			EmployeeShortListDTO empShortListDTO = new EmployeeShortListDTO();
			empShortListDTO.setEmployeeShortList(false);
			empDTO.setEmployeeShortListDTO(empShortListDTO);

			empDTO.setEmployeeNumber(employee.getEmployeeNumber());
			int empRecordSize = 0;
			List<Employee> existingEmpRecords = new ArrayList<Employee>();
			empRecordSize = employeeDAO.findAllByCondition(empDTO, null, null,
					employee.getCompany().getCompanyId()).size();

			if (empRecordSize > 0) {
				existingEmpRecords = employeeDAO.findAllByCondition(empDTO,
						null, null, employee.getCompany().getCompanyId());
			}

			if (empColName.size() == 1
					&& empColName
							.get(0)
							.getMethodName()
							.equalsIgnoreCase(
									PayAsiaConstants.CAMEL_CASE_EMPLOYEE_NUMBER)) {
				if (empRecordSize > 0) {
					employee.setEmployeeId(existingEmpRecords.get(0)
							.getEmployeeId());
				} else {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setFailureType("payasia.data.import.transaction");
					dataImportLogDTO.setFromMessageSource(true);
					dataImportLogDTO
							.setRemarks("payasia.data.import.employee.code.error");
					dataImportLogDTO
							.setRowNumber(Long.parseLong(rowNumber) + 1);
					updateAndInsertLogs.add(dataImportLogDTO);
					throw new PayAsiaDataException(updateAndInsertLogs);
				}

			} else {
				if (empRecordSize > 0) {

					employeeDAO.delete(existingEmpRecords.get(0));
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
							employee,
							copyEmployeeData(empColName, employee,
									existingEmpRecords.get(0)));
					employee.setEmployeeId(0);
					employee = employeeDAO.saveReturn(employee);
					EmployeeLoginDetail employeeLoginDetail = new EmployeeLoginDetail();
					String salt = securityLogic.generateSalt();
					String encryptPassWord = securityLogic.encrypt(
							PasswordUtils.getRandomPassword(), salt);
					employeeLoginDetail.setLoginName(employee
							.getEmployeeNumber().trim());
					employeeLoginDetail.setPassword(encryptPassWord);
					employeeLoginDetail.setSalt(salt);
					employeeLoginDetail.setEmployee(employee);
					employeeLoginDetailDAO.save(employeeLoginDetail);
					setDefaultRoleMapping(employee.getEmployeeId(), companyId,
							PayAsiaConstants.TRANSACTION_TYPE_OLD);

				} else {

					employee = employeeDAO.saveReturn(employee);
					EmployeeLoginDetail employeeLoginDetail = new EmployeeLoginDetail();
					String salt = securityLogic.generateSalt();
					String encryptPassWord = securityLogic.encrypt(
							PasswordUtils.getRandomPassword(), salt);
					employeeLoginDetail.setLoginName(employee
							.getEmployeeNumber());
					employeeLoginDetail.setPassword(encryptPassWord);
					employeeLoginDetail.setSalt(salt);
					employeeLoginDetail.setEmployee(employee);
					employeeLoginDetailDAO.save(employeeLoginDetail);

					setDefaultRoleMapping(employee.getEmployeeId(), companyId,
							PayAsiaConstants.TRANSACTION_TYPE_OLD);

				}
			}

			if (isDynamic) {
				if (employee.getEmployeeId() != 0) {

					for (Long formId : formIds) {
						DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

						dynamicFormRecord.setForm_ID(formId);
						dynamicFormRecord.setCompany_ID(companyId);
						dynamicFormRecord.setEntity_ID(entityId);
						dynamicFormRecord
								.setEntityKey(employee.getEmployeeId());
						DynamicFormRecord existingFormRecord = new DynamicFormRecord();

						existingFormRecord = dynamicFormRecordDAO
								.getEmpRecords(existingEmpRecords.get(0)
										.getEmployeeId(), null, formId,
										entityId, companyId);

						if (existingFormRecord != null) {

							deleteFormRecords(employee,
									dataImportParametersDTO, companyId,
									employeeTableRecordDeletedSet);
							super.setDynamicValues(dataImportParametersDTO,
									tableNames, colFormMapList, dynRecordsName,
									formId, existingFormRecord,
									existingFormRecord, companyId, "update");

							dynamicFormRecordDAO.update(existingFormRecord);
						} else {

							existingFormRecord = new DynamicFormRecord();
							super.setDynamicValues(dataImportParametersDTO,
									tableNames, colFormMapList, dynRecordsName,
									formId, dynamicFormRecord,
									existingFormRecord, companyId, "insert");

							dynamicFormRecordDAO.save(dynamicFormRecord);
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

		if (updateAndInsertLogs != null && !updateAndInsertLogs.isEmpty()) {
			throw new PayAsiaDataException(updateAndInsertLogs);
		}
	}

	/**
	 * Purpose: To copy one employee object to other.
	 * 
	 * @param empColName
	 *            the emp col name
	 * @param newEmp
	 *            the new emp
	 * @param oldEmp
	 *            the old emp
	 * @return the employee
	 */
	public Employee copyEmployeeData(List<DataImportKeyValueDTO> empColName,
			Employee newEmp, Employee oldEmp) {
		try {
			Class<?> oldEmpClass = oldEmp.getClass();
			Class<?> newEmpClass = newEmp.getClass();

			for (DataImportKeyValueDTO colName : empColName) {

				String getMethodName = "get" + colName.getMethodName();
				Method getColMethod;

				getColMethod = newEmpClass.getMethod(getMethodName);

				String setMethodName = "set" + colName.getMethodName();
				Method setMethod;

				if (colName.getFieldType().equals(
						PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {

					boolean bolVal = (Boolean) getColMethod.invoke(newEmp);
					setMethod = oldEmpClass.getMethod(setMethodName,
							Boolean.TYPE);

					setMethod.invoke(oldEmp, bolVal);

				} else if (colName.getFieldType().equals(
						PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {

					Timestamp dateVal = (Timestamp) getColMethod.invoke(newEmp);
					setMethod = oldEmpClass.getMethod(setMethodName,
							Timestamp.class);
					setMethod.invoke(oldEmp, dateVal);
				} else {
					String stringVal = (String) getColMethod.invoke(newEmp);
					setMethod = oldEmpClass.getMethod(setMethodName,
							String.class);
					setMethod.invoke(oldEmp, stringVal);
				}

			}
		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return oldEmp;
	}

}
