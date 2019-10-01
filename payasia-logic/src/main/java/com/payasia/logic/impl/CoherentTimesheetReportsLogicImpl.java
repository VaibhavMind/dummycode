package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.CodeDesc;
import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.CoherentOvertimeDetailReportDTO;
import com.payasia.common.dto.CoherentOvertimeReportDataDTO;
import com.payasia.common.dto.CoherentShiftDetailReportDTO;
import com.payasia.common.dto.CoherentShiftReportDataDTO;
import com.payasia.common.dto.CoherentTimesheetReportDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.CustomFieldReportDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveReportCustomDataDTO;
import com.payasia.common.dto.LeaveReportHeaderDTO;
import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CoherentTimesheetReportsForm;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.dao.CoherentOvertimeApplicationDetailDAO;
import com.payasia.dao.CoherentShiftApplicationDetailDAO;
import com.payasia.dao.CoherentTimesheetPreferenceDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.AppCodeMaster_;
import com.payasia.dao.bean.CoherentOvertimeApplicationDetail_;
import com.payasia.dao.bean.CoherentOvertimeApplication_;
import com.payasia.dao.bean.CoherentShiftApplication_;
import com.payasia.dao.bean.CoherentTimesheetPreference;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.CoherentTimesheetReportsLogic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.MultilingualLogic;

@Component
public class CoherentTimesheetReportsLogicImpl extends BaseLogic implements
		CoherentTimesheetReportsLogic {
	private static final Logger LOGGER = Logger
			.getLogger(CoherentTimesheetReportsLogicImpl.class);
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;

	@Resource
	TimesheetBatchDAO timesheetBatchDAO;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	DataExportUtils dataExportUtils;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	GeneralDAO generalDAO;
	@Resource
	CoherentTimesheetPreferenceDAO coherentTimesheetPreferenceDAO;
	@Resource
	MultilingualLogic multilingualLogic;
	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;
	@Resource
	CoherentOvertimeApplicationDetailDAO coherentOvertimeApplicationDetailDAO;

	@Resource
	CoherentShiftApplicationDetailDAO coherentShiftApplicationDetailDAO;

	@Override
	public List<CoherentTimesheetReportsForm> otBatchList(Long companyId,
			int year) {
		List<CoherentTimesheetReportsForm> otBatchList = new ArrayList<>();
		Calendar now = Calendar.getInstance();
		Date currentDate = now.getTime();

		List<TimesheetBatch> timesheetBatchList = timesheetBatchDAO
				.getOTBatchesByYear(companyId, year);
		for (TimesheetBatch timesheetBatch : timesheetBatchList) {
			CoherentTimesheetReportsForm otTimesheetReportsForm = new CoherentTimesheetReportsForm();
			otTimesheetReportsForm.setOtBatchId(timesheetBatch
					.getTimesheetBatchId());
			otTimesheetReportsForm.setBatchDesc(timesheetBatch
					.getTimesheetBatchDesc());
			Date startDate = DateUtils.stringToDate(DateUtils
					.timeStampToString(timesheetBatch.getStartDate(),
							timesheetBatch.getCompany().getDateFormat()),
					timesheetBatch.getCompany().getDateFormat());
			Date endDate = DateUtils.stringToDate(DateUtils.timeStampToString(
					timesheetBatch.getEndDate(), timesheetBatch.getCompany()
							.getDateFormat()), timesheetBatch.getCompany()
					.getDateFormat());
			if (currentDate.after(startDate) && currentDate.before(endDate)) {
				otTimesheetReportsForm.setDefaultBatch(1);
			}
			if (currentDate.equals(startDate) || currentDate.equals(endDate)) {
				otTimesheetReportsForm.setDefaultBatch(1);
			}
			otBatchList.add(otTimesheetReportsForm);
		}
		return otBatchList;

	}

	@Override
	public CoherentTimesheetReportDTO getOTBatchName(Long companyId,
			Long fromBatchId, Long toBatchId) {
		CoherentTimesheetReportDTO lundinTimewritingReportDTO = new CoherentTimesheetReportDTO();
		String otBatchName = "";
		TimesheetBatch fromOTBatch = timesheetBatchDAO.findById(fromBatchId);
		TimesheetBatch toOTBatch = timesheetBatchDAO.findById(toBatchId);
		if (fromOTBatch != null && toOTBatch != null) {
			if (fromBatchId.equals(toBatchId)) {
				otBatchName = fromOTBatch.getTimesheetBatchDesc().substring(0,
						8);
				otBatchName = otBatchName.replaceAll(" ", "");
			} else {
				otBatchName = fromOTBatch.getTimesheetBatchDesc().substring(0,
						8)
						+ "_TO_"
						+ toOTBatch.getTimesheetBatchDesc().substring(0, 8);
				otBatchName = otBatchName.replaceAll(" ", "");
			}
			otBatchName = otBatchName.toUpperCase();
			lundinTimewritingReportDTO.setFromBatchDate(DateUtils
					.timeStampToString(fromOTBatch.getStartDate(), fromOTBatch
							.getCompany().getDateFormat()));
			lundinTimewritingReportDTO.setToBatchDate(DateUtils
					.timeStampToString(toOTBatch.getEndDate(), toOTBatch
							.getCompany().getDateFormat()));
		}
		lundinTimewritingReportDTO.setFileBatchName(otBatchName);
		return lundinTimewritingReportDTO;

	}

	@Override
	public List<CoherentTimesheetReportsForm> coherentEmployeeList(
			Long employeeId, Long companyId) {
		List<CoherentTimesheetReportsForm> employeeNumList = new ArrayList<>();

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Employee> employeeList = employeeDAO.findByCondition(conditionDTO,
				null, null, companyId);
		for (Employee employeeVO : employeeList) {
			if (employeeVO.isStatus()) {
				CoherentTimesheetReportsForm otTimesheetReportsForm = new CoherentTimesheetReportsForm();
				otTimesheetReportsForm
						.setEmployeeId(employeeVO.getEmployeeId());
				otTimesheetReportsForm.setEmployeeName(employeeVO
						.getEmployeeNumber()
						+ " - "
						+ getEmployeeName(employeeVO.getFirstName(),
								employeeVO.getLastName()));
				otTimesheetReportsForm.setEmployeeNumber(employeeVO
						.getEmployeeNumber());
				employeeNumList.add(otTimesheetReportsForm);
			}
		}
		return employeeNumList;
	}

	@Override
	public List<CoherentTimesheetReportsForm> coherentTimesheetReviewerList(
			Long employeeId, Long companyId) {
		List<CoherentTimesheetReportsForm> employeeNumList = new ArrayList<>();

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Tuple> employeeReviewerList = employeeTimesheetReviewerDAO
				.getEmployeeReviewerList(companyId);
		for (Tuple tuple : employeeReviewerList) {
			CoherentTimesheetReportsForm otTimesheetReportsForm = new CoherentTimesheetReportsForm();
			otTimesheetReportsForm.setEmployeeId((Long) tuple.get(
					getAlias(Employee_.employeeId), Long.class));
			otTimesheetReportsForm.setEmployeeName((String) tuple.get(
					getAlias(Employee_.employeeNumber), String.class)
					+ " - "
					+ getEmployeeName((String) tuple.get(
							getAlias(Employee_.firstName), String.class),
							(String) tuple.get(getAlias(Employee_.lastName),
									String.class)));
			otTimesheetReportsForm.setEmployeeNumber((String) tuple.get(
					getAlias(Employee_.employeeNumber), String.class));
			employeeNumList.add(otTimesheetReportsForm);
		}
		return employeeNumList;
	}

	@Override
	public List<CoherentTimesheetReportsForm> coherentTimesheetEmpListForManager(
			Long employeeId, Long companyId) {
		List<CoherentTimesheetReportsForm> employeeNumList = new ArrayList<>();

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Tuple> employeeReviewerList = employeeTimesheetReviewerDAO
				.getEmployeeListByManager(companyId, employeeId);
		for (Tuple tuple : employeeReviewerList) {
			CoherentTimesheetReportsForm otTimesheetReportsForm = new CoherentTimesheetReportsForm();
			otTimesheetReportsForm.setEmployeeId((Long) tuple.get(
					getAlias(Employee_.employeeId), Long.class));
			otTimesheetReportsForm.setEmployeeName((String) tuple.get(
					getAlias(Employee_.employeeNumber), String.class)
					+ " - "
					+ getEmployeeName((String) tuple.get(
							getAlias(Employee_.firstName), String.class),
							(String) tuple.get(getAlias(Employee_.lastName),
									String.class)));
			otTimesheetReportsForm.setEmployeeNumber((String) tuple.get(
					getAlias(Employee_.employeeNumber), String.class));
			employeeNumList.add(otTimesheetReportsForm);
		}
		return employeeNumList;
	}

	private void getColMap(DataDictionary dataDictionary,
			Map<String, DataImportKeyValueDTO> colMap) {
		Long formId = dataDictionary.getFormID();
		Tab tab = null;
		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(dataDictionary
				.getCompany().getCompanyId(), dataDictionary.getEntityMaster()
				.getEntityId(), dataDictionary.getFormID());

		DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
				dataDictionary.getCompany().getCompanyId(), dataDictionary
						.getEntityMaster().getEntityId(), maxVersion,
				dataDictionary.getFormID());

		tab = dataExportUtils.getTabObject(dynamicForm);
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (column.getDictionaryId().equals(
							dataDictionary.getDataDictionaryId())) {
						dataImportKeyValueDTO.setStatic(false);
						dataImportKeyValueDTO.setChild(true);
						dataImportKeyValueDTO.setFieldType(column.getType());
						String colNumber = PayAsiaStringUtils
								.getColNumber(column.getName());
						dataImportKeyValueDTO.setMethodName(colNumber);
						dataImportKeyValueDTO.setFormId(formId);
						dataImportKeyValueDTO
								.setActualColName(new String(Base64
										.decodeBase64(field.getLabel()
												.getBytes())));
						String tablePosition = PayAsiaStringUtils
								.getColNumber(field.getName());
						dataImportKeyValueDTO.setTablePosition(tablePosition);
						if (dataDictionary
								.getEntityMaster()
								.getEntityName()
								.equalsIgnoreCase(
										PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
							colMap.put(dataDictionary.getLabel()
									+ dataDictionary.getDataDictionaryId(),
									dataImportKeyValueDTO);
						}

					}
				}
			}
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {
				if (field.getDictionaryId().equals(
						dataDictionary.getDataDictionaryId())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(true);
					dataImportKeyValueDTO.setFieldType(field.getType());
					String colNumber = PayAsiaStringUtils.getColNumber(field
							.getName());
					dataImportKeyValueDTO.setMethodName(colNumber);
					dataImportKeyValueDTO.setFormId(formId);
					dataImportKeyValueDTO.setActualColName(new String(Base64
							.decodeBase64(field.getLabel().getBytes())));
					String tablePosition = PayAsiaStringUtils
							.getColNumber(field.getName());
					dataImportKeyValueDTO.setTablePosition(tablePosition);
					if (dataDictionary
							.getEntityMaster()
							.getEntityName()
							.equalsIgnoreCase(
									PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
						colMap.put(
								dataDictionary.getLabel()
										+ dataDictionary.getDataDictionaryId(),
								dataImportKeyValueDTO);
					}

				}
			}
		}
	}

	private void setStaticDictionary(Map<String, DataImportKeyValueDTO> colMap,
			String colKey, DataDictionary dataDictionary) {
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		try {
			ColumnPropertyDTO colProp = generalDAO.getColumnProperties(
					dataDictionary.getTableName(),
					dataDictionary.getColumnName());
			dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
			dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
					.getCamelCase(dataDictionary.getColumnName()));
			dataImportKeyValueDTO.setStatic(true);
			dataImportKeyValueDTO.setActualColName(dataDictionary
					.getColumnName());
			colMap.put(colKey, dataImportKeyValueDTO);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
	}

	private String getEmployeeName(String firstName, String lastName) {
		String employeeName = firstName;
		if (StringUtils.isNotBlank(lastName)) {
			employeeName = employeeName + " " + lastName;
		}
		return employeeName;
	}

	@Override
	public List<Object[]> getEmpDynFieldsValueList(Long companyId,
			String dateFormat, Long employeeId) {
		List<Long> formIds = new ArrayList<Long>();
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

		CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (coherentTimesheetPreferenceVO != null) {
			// Department Field
			if (coherentTimesheetPreferenceVO.getDepartment() != null) {
				getColMap(coherentTimesheetPreferenceVO.getDepartment(), colMap);
				addDynamicTableKeyMap(
						coherentTimesheetPreferenceVO.getDepartment(),
						tableRecordInfoFrom, colMap);
				formIds.add(coherentTimesheetPreferenceVO.getDepartment()
						.getFormID());
			}
			// Location Field
			if (coherentTimesheetPreferenceVO.getLocation() != null) {
				getColMap(coherentTimesheetPreferenceVO.getLocation(), colMap);
				addDynamicTableKeyMap(
						coherentTimesheetPreferenceVO.getLocation(),
						tableRecordInfoFrom, colMap);
				if (!formIds.contains(coherentTimesheetPreferenceVO
						.getLocation().getFormID())) {
					formIds.add(coherentTimesheetPreferenceVO.getLocation()
							.getFormID());
				}
			}
			// Cost Centre Field
			if (coherentTimesheetPreferenceVO.getCostCenter() != null) {
				getColMap(coherentTimesheetPreferenceVO.getCostCenter(), colMap);
				addDynamicTableKeyMap(
						coherentTimesheetPreferenceVO.getCostCenter(),
						tableRecordInfoFrom, colMap);
				if (!formIds.contains(coherentTimesheetPreferenceVO
						.getCostCenter().getFormID())) {
					formIds.add(coherentTimesheetPreferenceVO.getCostCenter()
							.getFormID());
				}
			}
		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		// Get Static Dictionary
		DataDictionary dataDictionaryEmp = dataDictionaryDAO
				.findByDictionaryNameGroup(null, 1l, "Employee Number", null,
						null);
		setStaticDictionary(colMap, dataDictionaryEmp.getLabel()
				+ dataDictionaryEmp.getDataDictionaryId(), dataDictionaryEmp);
		DataDictionary dataDictionaryEmpFirstName = dataDictionaryDAO
				.findByDictionaryNameGroup(null, 1l, "First Name", null, null);
		setStaticDictionary(colMap, dataDictionaryEmpFirstName.getLabel()
				+ dataDictionaryEmpFirstName.getDataDictionaryId(),
				dataDictionaryEmpFirstName);
		DataDictionary dataDictionaryEmpLastName = dataDictionaryDAO
				.findByDictionaryNameGroup(null, 1l, "Last Name", null, null);
		setStaticDictionary(colMap, dataDictionaryEmpLastName.getLabel()
				+ dataDictionaryEmpLastName.getDataDictionaryId(),
				dataDictionaryEmpLastName);

		// Show by effective date table data if one custom table record is
		// required, here it is true i.e. show only by effective date table data
		boolean showByEffectiveDateTableData = true;

		List<Object[]> objectList = employeeDAO.findByCondition(colMap,
				formIds, companyId, finalFilterList, dateFormat,
				tableRecordInfoFrom, tableElements, employeeShortListDTO,
				showByEffectiveDateTableData);
		return objectList;
	}

	private void addDynamicTableKeyMap(DataDictionary dataDictionary,
			Map<String, DataImportKeyValueDTO> tableRecordInfoFrom,
			Map<String, DataImportKeyValueDTO> colMap) {
		if (colMap.get(dataDictionary.getLabel()
				+ dataDictionary.getDataDictionaryId()) != null
				&& colMap.get(
						dataDictionary.getLabel()
								+ dataDictionary.getDataDictionaryId())
						.getTablePosition() != null) {
			String tableKey;
			tableKey = colMap.get(
					dataDictionary.getLabel()
							+ dataDictionary.getDataDictionaryId()).getFormId()
					+ colMap.get(
							dataDictionary.getLabel()
									+ dataDictionary.getDataDictionaryId())
							.getTablePosition();
			tableRecordInfoFrom.put(
					tableKey,
					colMap.get(dataDictionary.getLabel()
							+ dataDictionary.getDataDictionaryId()));
		}
	}

	@Override
	public CoherentTimesheetReportDTO genEmpOvertimeDetailRepExcelFile(
			Long companyId, Long employeeId,
			CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds) {
		CoherentTimesheetReportDTO coherentTimesheetReportDTO = new CoherentTimesheetReportDTO();

		Company companyVO = companyDAO.findById(companyId);
		coherentTimesheetReportDTO.setCompanyName(companyVO.getCompanyName());

		List<BigInteger> shortlistEmpIds = null;
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		if (isManager) {
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setStatus(coherentTimesheetReportsForm
					.isIncludeResignedEmployees());
			List<Tuple> employeeIdsList = employeeTimesheetReviewerDAO
					.getEmployeeIdsTupleForManager(companyId, employeeId,
							employeeShortListDTO);
			shortlistEmpIds = new ArrayList<BigInteger>();
			for (Tuple employeeTuple : employeeIdsList) {
				shortlistEmpIds.add(new BigInteger(String
						.valueOf((Long) employeeTuple.get(
								getAlias(Employee_.employeeId), Long.class))));
			}
		} else {
			List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO
					.getShortListEmployeeIds();
			List<BigInteger> reportShortListEmployeeIds = null;
			if (coherentTimesheetReportsForm.getIsShortList()) {
				EmployeeShortListDTO reportShortList = generalLogic
						.getShortListEmployeeIdsForReports(companyId,
								coherentTimesheetReportsForm.getMetaData());

				reportShortListEmployeeIds = reportShortList
						.getShortListEmployeeIds();

				if (employeeShortListDTO.getEmployeeShortList()) {
					reportShortListEmployeeIds
							.retainAll(companyShortListEmployeeIds);
				}
			}
			if (coherentTimesheetReportsForm.getIsShortList()) {
				shortlistEmpIds = new ArrayList<>(reportShortListEmployeeIds);
			} else {
				shortlistEmpIds = new ArrayList<>(companyShortListEmployeeIds);
			}
		}

		// Get All Employees By company
		Map<String, Long> empNumEmpIdMap = new HashMap<String, Long>();
		List<Employee> employeeVOList = employeeDAO.findByCompany(companyId);
		for (Employee employee : employeeVOList) {
			// if (employee.isStatus()) {
			empNumEmpIdMap.put(employee.getEmployeeNumber(),
					employee.getEmployeeId());
			// }
		}

		List<BigInteger> costCentreEmpIdsList = new ArrayList<BigInteger>();
		Map<String, CoherentOvertimeDetailReportDTO> empDeptCostCatDetailMap = new HashMap<String, CoherentOvertimeDetailReportDTO>();
		// Get Department Of Employees from Employee Information Details
		List<Object[]> locationEmpObjectList = getEmpDynFieldsValueList(
				companyId, companyVO.getDateFormat(), employeeId);
		for (Object[] deptObject : locationEmpObjectList) {
			if (deptObject != null && deptObject[3] != null) {
				CoherentOvertimeDetailReportDTO coherentOvertimeDetailReportDTO = new CoherentOvertimeDetailReportDTO();
				coherentOvertimeDetailReportDTO.setEmployeeNumber(String
						.valueOf(deptObject[3]));
				String lastName = "";
				if (deptObject[5] != null) {
					lastName = String.valueOf(deptObject[5]);
				}
				coherentOvertimeDetailReportDTO
						.setEmployeeName(getEmployeeName(
								String.valueOf(deptObject[4]), lastName));
				coherentOvertimeDetailReportDTO.setDepartmentCode(String
						.valueOf(deptObject[0]));
				coherentOvertimeDetailReportDTO.setLocation(String
						.valueOf(deptObject[1]));
				coherentOvertimeDetailReportDTO.setCostCentre(String
						.valueOf(deptObject[2]));
				coherentOvertimeDetailReportDTO.setEmployeeId(empNumEmpIdMap
						.get(String.valueOf(deptObject[3])));
				costCentreEmpIdsList.add(BigInteger.valueOf(empNumEmpIdMap
						.get(String.valueOf(deptObject[3]))));
				empDeptCostCatDetailMap.put(String.valueOf(deptObject[3]),
						coherentOvertimeDetailReportDTO);
			}
		}

		shortlistEmpIds.retainAll(costCentreEmpIdsList);

		// Populate conditionDTO
		LundinTsheetConditionDTO conditionDTO = new LundinTsheetConditionDTO();
		EmployeeShortListDTO shortListEmpDTO = new EmployeeShortListDTO();
		shortListEmpDTO.setShortListEmployeeIds(shortlistEmpIds);
		if (coherentTimesheetReportsForm.getIsShortList()
				|| (shortlistEmpIds != null && shortlistEmpIds.size() > 0)) {
			shortListEmpDTO.setEmployeeShortList(true);
		} else {
			shortListEmpDTO.setEmployeeShortList(false);
		}
		conditionDTO.setEmployeeShortListDTO(shortListEmpDTO);
		conditionDTO
				.setEmployeeId(coherentTimesheetReportsForm.getEmployeeId());
		if (isManager) {
			conditionDTO.setEmployeeReviewerId(employeeId);
		} else {
			conditionDTO.setEmployeeReviewerId(coherentTimesheetReportsForm
					.getReviewerEmpId());
		}

		if (coherentTimesheetReportsForm.getFromBatchId() != null) {
			TimesheetBatch fromTimesheetBatch = timesheetBatchDAO
					.findById(coherentTimesheetReportsForm.getFromBatchId());
			conditionDTO.setFromBatchDate(fromTimesheetBatch.getStartDate());
		}
		if (coherentTimesheetReportsForm.getToBatchId() != null) {
			TimesheetBatch toTimesheetBatch = timesheetBatchDAO
					.findById(coherentTimesheetReportsForm.getToBatchId());
			conditionDTO.setToBatchDate(toTimesheetBatch.getEndDate());
		}

		if (StringUtils.isNotBlank(coherentTimesheetReportsForm
				.getApprovedFromDate())) {
			conditionDTO.setApprovedFromDate(DateUtils
					.stringToTimestamp(coherentTimesheetReportsForm
							.getApprovedFromDate()));
		}
		if (StringUtils.isNotBlank(coherentTimesheetReportsForm
				.getApprovedToDate())) {
			conditionDTO.setApprovedToDate(DateUtils
					.stringToTimestamp(coherentTimesheetReportsForm
							.getApprovedToDate()));
		}

		boolean istimesheetStatusAll = false;
		List<String> timesheetStatusList = new ArrayList<>();
		if (coherentTimesheetReportsForm.getTimesheetStatus().equalsIgnoreCase(
				PayAsiaConstants.LUNDIN_STATUS_DRAFT)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
		} else if (coherentTimesheetReportsForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
		} else if (coherentTimesheetReportsForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_APPROVED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_COMPLETED);
		} else if (coherentTimesheetReportsForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_REJECTED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_REJECTED);
		} else {
			istimesheetStatusAll = true;
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_REJECTED);
		}
		conditionDTO.setStatusNameList(timesheetStatusList);
		conditionDTO.setEmpStatus(coherentTimesheetReportsForm
				.isIncludeResignedEmployees());

		List<Tuple> lionTimesheetSummaryReportVOList = new ArrayList<Tuple>();

		// Fetch Timesheet Data
		if (coherentTimesheetReportsForm.getTimesheetStatus().equalsIgnoreCase(
				PayAsiaConstants.LUNDIN_STATUS_APPROVED)) {
			lionTimesheetSummaryReportVOList = coherentOvertimeApplicationDetailDAO
					.getEmployeeOvertimeReportDetailWithRevAppDate(
							conditionDTO, companyId);
		} else {
			if (istimesheetStatusAll) {
				List<Tuple> lionTimesheetSummaryReportVO1List = new ArrayList<Tuple>();
				lionTimesheetSummaryReportVO1List = coherentOvertimeApplicationDetailDAO
						.getEmployeeOvertimeReportDetailWithRevAppDate(
								conditionDTO, companyId);
				List<Tuple> lionTimesheetSummaryReportVO2List = new ArrayList<Tuple>();
				lionTimesheetSummaryReportVO2List = coherentOvertimeApplicationDetailDAO
						.getEmployeeOvertimeReportDetail(conditionDTO,
								companyId);
				lionTimesheetSummaryReportVOList
						.addAll(lionTimesheetSummaryReportVO1List);
				lionTimesheetSummaryReportVOList
						.addAll(lionTimesheetSummaryReportVO2List);
			} else {
				lionTimesheetSummaryReportVOList = coherentOvertimeApplicationDetailDAO
						.getEmployeeOvertimeReportDetail(conditionDTO,
								companyId);
			}
		}

		Set<Long> empIdSet = new HashSet<Long>();
		Set<Timestamp> timesheetBatchIdSet = new HashSet<Timestamp>();
		List<CoherentOvertimeDetailReportDTO> coherentOvertimeDetailReportDTOVOList = new ArrayList<CoherentOvertimeDetailReportDTO>();
		for (Tuple applicationDetail : lionTimesheetSummaryReportVOList) {
			CoherentOvertimeDetailReportDTO summaryReportDTO = new CoherentOvertimeDetailReportDTO();
			summaryReportDTO
					.setBatchDesc((String) applicationDetail.get(
							getAlias(TimesheetBatch_.timesheetBatchDesc),
							String.class));
			summaryReportDTO
					.setTimesheetId((Long) applicationDetail
							.get(getAlias(CoherentOvertimeApplication_.overtimeApplicationID),
									Long.class));
			summaryReportDTO.setEmployeeId((Long) applicationDetail.get(
					getAlias(Employee_.employeeId), Long.class));
			summaryReportDTO.setUpdatedDate(DateUtils.timeStampToString(
					(Timestamp) applicationDetail.get(
							getAlias(CoherentOvertimeApplication_.updatedDate),
							Timestamp.class), companyVO.getDateFormat()));
			summaryReportDTO.setEmployeeNumber((String) applicationDetail.get(
					getAlias(Employee_.employeeNumber), String.class));

			summaryReportDTO.setEmployeeName(getEmployeeName(
					(String) applicationDetail.get(
							getAlias(Employee_.firstName), String.class),
					(String) applicationDetail.get(
							getAlias(Employee_.lastName), String.class)));

			summaryReportDTO.setDayType((String) applicationDetail.get(
					getAlias(AppCodeMaster_.codeDesc), String.class));
			summaryReportDTO.setTotalOTHours((Double) applicationDetail.get(
					getAlias(CoherentOvertimeApplicationDetail_.otHours),
					Double.class));
			summaryReportDTO.setTotalOT15Hours((Double) applicationDetail.get(
					getAlias(CoherentOvertimeApplicationDetail_.ot15Hours),
					Double.class));
			summaryReportDTO.setTotalOT10Day((Double) applicationDetail.get(
					getAlias(CoherentOvertimeApplicationDetail_.ot10Day),
					Double.class));
			summaryReportDTO.setTotalOT20Day((Double) applicationDetail.get(
					getAlias(CoherentOvertimeApplicationDetail_.ot20Day),
					Double.class));
			summaryReportDTO.setCompanyName(companyVO.getCompanyName());
			timesheetBatchIdSet.add((Timestamp) applicationDetail.get(
					getAlias(TimesheetBatch_.startDate), Timestamp.class));
			coherentOvertimeDetailReportDTOVOList.add(summaryReportDTO);
			empIdSet.add((Long) applicationDetail.get(
					getAlias(Employee_.employeeId), Long.class));
		}

		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long
						.parseLong(dataDictionaryIds[count]));
			}
		}

		List<LeaveReportHeaderDTO> reportHeaderDTOs = new ArrayList<>();

		CustomFieldReportDTO customFieldReportDTO = null;
		if (!employeeIdsList.isEmpty()) {
			customFieldReportDTO = generalLogic
					.getCustomFieldDataForLeaveReport(dataDictionaryIdsList,
							employeeIdsList, companyId, false);
			Integer custFieldCount = 1;
			List<String> dataDictNameList = customFieldReportDTO
					.getDataDictNameList();
			for (String dataDictName : dataDictNameList) {
				LeaveReportHeaderDTO leaveHeaderDTO = new LeaveReportHeaderDTO();
				leaveHeaderDTO.setmDataProp("custField" + custFieldCount);
				leaveHeaderDTO.setsTitle(dataDictName);
				reportHeaderDTOs.add(leaveHeaderDTO);
				custFieldCount++;
			}

			List<Object[]> customFieldObjList = customFieldReportDTO
					.getCustomFieldObjList();
			HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
			for (Object[] objArr : customFieldObjList) {
				customFieldObjByEmpIdMap.put(
						Long.valueOf(objArr[0].toString()), objArr);
			}

			Set<String> coherentCustomFieldSet = new LinkedHashSet<>();

			HashMap<String, String> custFieldHashMap;
			int fieldCount = 0;
			for (CoherentOvertimeDetailReportDTO reportDTO : coherentOvertimeDetailReportDTOVOList) {
				custFieldHashMap = new LinkedHashMap<>();
				if (!customFieldObjList.isEmpty()) {
					int count = 0;

					Object[] objArr = customFieldObjByEmpIdMap.get(reportDTO
							.getEmployeeId());
					for (Object object : objArr) {
						if (count != 0) {
							custFieldHashMap.put(
									dataDictNameList.get(count - 1),
									String.valueOf(object));

							try {
								CoherentOvertimeDetailReportDTO.class
										.getMethod("setCustField" + count,
												String.class).invoke(reportDTO,
												String.valueOf(object));
							} catch (NoSuchMethodException | SecurityException
									| IllegalAccessException
									| IllegalArgumentException
									| InvocationTargetException exception) {
								LOGGER.error(exception.getMessage(), exception);
							}
						}
						count++;
					}
				}

				reportDTO.setCustFieldMap(custFieldHashMap);
			}
			for (String leaveCustField : coherentCustomFieldSet) {
				dataDictNameList.add(leaveCustField);
			}
			coherentTimesheetReportDTO.setDataDictNameList(dataDictNameList);
			List<LeaveReportCustomDataDTO> customDataDTOs = new ArrayList<>();
			for (int countF = 1; countF <= fieldCount; countF++) {
				LeaveReportCustomDataDTO customFieldDto = new LeaveReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field "
						+ countF);
				customDataDTOs.add(customFieldDto);
			}
			coherentTimesheetReportDTO.setLeaveCustomDataDTOs(customDataDTOs);
		}

		List<CoherentOvertimeDetailReportDTO> coherentOvertimeDetailReportDTOVONewList = new ArrayList<CoherentOvertimeDetailReportDTO>();
		for (CoherentOvertimeDetailReportDTO coherentOvertimeDetailReportDTO : coherentOvertimeDetailReportDTOVOList) {
			CoherentOvertimeDetailReportDTO empDeptCostCatDetail = empDeptCostCatDetailMap
					.get(coherentOvertimeDetailReportDTO.getEmployeeNumber());
			if (StringUtils.isNotBlank(empDeptCostCatDetail.getCostCentre())
					&& !empDeptCostCatDetail.getCostCentre().equalsIgnoreCase(
							"null")) {
				coherentOvertimeDetailReportDTO
						.setCostCentre(empDeptCostCatDetail.getCostCentre());
			}

			coherentOvertimeDetailReportDTO
					.setEmployeeName(empDeptCostCatDetail.getEmployeeName());
			if (StringUtils.isNotBlank(empDeptCostCatDetail.getLocation())
					&& !empDeptCostCatDetail.getLocation().equalsIgnoreCase(
							"null")) {
				coherentOvertimeDetailReportDTO
						.setLocation(empDeptCostCatDetail.getLocation());
			}
			if (StringUtils
					.isNotBlank(empDeptCostCatDetail.getDepartmentCode())
					&& !empDeptCostCatDetail.getDepartmentCode()
							.equalsIgnoreCase("null")) {
				coherentOvertimeDetailReportDTO
						.setDepartmentCode(empDeptCostCatDetail
								.getDepartmentCode());
			}
			if (coherentOvertimeDetailReportDTO.getTimesheetId() == 0) {
				coherentOvertimeDetailReportDTOVONewList
						.add(coherentOvertimeDetailReportDTO);
			} else {
				if (StringUtils.isNotBlank(coherentTimesheetReportsForm
						.getDepartmentDesc())) {
					if (coherentTimesheetReportsForm.getDepartmentDesc()
							.equalsIgnoreCase(
									empDeptCostCatDetail.getCostCentre())) {
						coherentOvertimeDetailReportDTOVONewList
								.add(coherentOvertimeDetailReportDTO);
					}
				} else {
					coherentOvertimeDetailReportDTOVONewList
							.add(coherentOvertimeDetailReportDTO);
				}
			}
		}

		List<CoherentOvertimeReportDataDTO> summarryDTOs = new ArrayList<>();
		CoherentOvertimeReportDataDTO yearWiseSummarryDTO = null;
		HashMap<String, List<CoherentOvertimeDetailReportDTO>> empTimesheetDetailMap = null;
		List<Long> empIdTraversedList = new ArrayList<Long>();
		for (CoherentOvertimeDetailReportDTO otTimesheetVO : coherentOvertimeDetailReportDTOVONewList) {
			if (!empIdTraversedList.contains(otTimesheetVO.getEmployeeId())) {
				empTimesheetDetailMap = new HashMap<>();
				List<CoherentOvertimeDetailReportDTO> detailDataDTOList = new ArrayList<CoherentOvertimeDetailReportDTO>();
				yearWiseSummarryDTO = new CoherentOvertimeReportDataDTO();
				yearWiseSummarryDTO
						.setEmployeeId(otTimesheetVO.getEmployeeId());
				yearWiseSummarryDTO.setEmployeeNumber(otTimesheetVO
						.getEmployeeNumber());
				yearWiseSummarryDTO.setEmployeeName(getEmployeeName(
						otTimesheetVO.getFirstName(),
						otTimesheetVO.getLastName()));

				for (CoherentOvertimeDetailReportDTO ingersollOTTimesheetDetail : coherentOvertimeDetailReportDTOVOList) {
					if (!ingersollOTTimesheetDetail.getEmployeeId().equals(
							otTimesheetVO.getEmployeeId())) {
						continue;
					}
					detailDataDTOList.add(ingersollOTTimesheetDetail);
				}
				empTimesheetDetailMap.put(otTimesheetVO.getEmployeeNumber(),
						detailDataDTOList);
				yearWiseSummarryDTO
						.setEmpTimesheetDetailMap(empTimesheetDetailMap);
				summarryDTOs.add(yearWiseSummarryDTO);
			}
			empIdTraversedList.add(otTimesheetVO.getEmployeeId());
		}

		int totalEmployeesCount = empIdSet.size();

		TreeSet timesheetBatchIdTreeSet = new TreeSet();
		timesheetBatchIdTreeSet.addAll(timesheetBatchIdSet);

		if (!timesheetBatchIdTreeSet.isEmpty()) {
			TimesheetBatch startBatch = timesheetBatchDAO.getBatchByStartDate(
					companyId, Timestamp.valueOf(timesheetBatchIdTreeSet
							.first().toString()));
			TimesheetBatch endBatch = timesheetBatchDAO.getBatchByStartDate(
					companyId, Timestamp.valueOf(timesheetBatchIdTreeSet.last()
							.toString()));

			coherentTimesheetReportDTO.setFromBatchId(startBatch
					.getTimesheetBatchId());
			coherentTimesheetReportDTO.setToBatchId(endBatch
					.getTimesheetBatchId());
		}

		coherentTimesheetReportDTO.setTotalEmployeesCount(totalEmployeesCount);
		coherentTimesheetReportDTO.setCoherentOvertimeReportDTOs(summarryDTOs);

		return coherentTimesheetReportDTO;
	}

	public List<String> getEmpDynFieldListByDictId(Long companyId,
			Long dataDictionaryId) {
		List<String> locationFieldList = new ArrayList<>();

		DataDictionary dataDictionary = dataDictionaryDAO
				.findById(dataDictionaryId);

		if (dataDictionary != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
					companyId, dataDictionary.getEntityMaster().getEntityId(),
					dataDictionary.getFormID());
			if (dynamicForm != null) {
				Tab tab = dataExportUtils.getTabObject(dynamicForm);

				List<Field> listOfFields = tab.getField();
				for (Field field : listOfFields) {
					if (field.getType().equalsIgnoreCase(
							PayAsiaConstants.TABLE_FIELD_TYPE)
							|| field.getType().equalsIgnoreCase(
									PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
						List<Column> columnList = field.getColumn();
						for (Column column : columnList) {
							if (column.getType().equalsIgnoreCase(
									PayAsiaConstants.CODEDESC_FIELD_TYPE)
									&& column.getDictionaryId().equals(
											dataDictionary
													.getDataDictionaryId())) {
								List<CodeDesc> CodeDescList = multilingualLogic
										.getCodeDescList(dataDictionary
												.getDataDictionaryId());
								if (CodeDescList != null) {
									for (CodeDesc codeDesc : CodeDescList) {
										try {
											locationFieldList.add(URLDecoder
													.decode(codeDesc.getCode(),
															"UTF-8"));
										} catch (UnsupportedEncodingException e) {
											LOGGER.error(e.getMessage(), e);
										}
									}
								}
							}
							if (column.getType().equalsIgnoreCase(
									PayAsiaConstants.FIELD_TYPE_DROPDOWN)
									&& column.getDictionaryId().equals(
											dataDictionary
													.getDataDictionaryId())) {
								List<String> comboList = column.getOption();
								for (String comboVal : comboList) {
									try {
										locationFieldList
												.add(URLDecoder.decode(
														StringEscapeUtils
																.unescapeHtml(new String(
																		Base64.decodeBase64(comboVal
																				.getBytes()))),
														"UTF-8"));
									} catch (UnsupportedEncodingException e) {
										LOGGER.error(e.getMessage(), e);
									}
								}
							}
						}
					} else {
						if (field.getType().equalsIgnoreCase(
								PayAsiaConstants.PAYASIA_FIELD_DROPDOWN)
								&& field.getDictionaryId().equals(
										dataDictionary.getDataDictionaryId())) {
							List<String> comboList = field.getOption();
							for (String comboVal : comboList) {
								try {
									locationFieldList
											.add(URLDecoder.decode(
													StringEscapeUtils
															.unescapeHtml(new String(
																	Base64.decodeBase64(comboVal
																			.getBytes()))),
													"UTF-8"));
								} catch (UnsupportedEncodingException e) {
									LOGGER.error(e.getMessage(), e);
								}
							}
						}
						if (field.getType().equalsIgnoreCase(
								PayAsiaConstants.CODEDESC_FIELD_TYPE)
								&& field.getDictionaryId().equals(
										dataDictionary.getDataDictionaryId())) {
							List<CodeDesc> CodeDescList = multilingualLogic
									.getCodeDescList(dataDictionary
											.getDataDictionaryId());
							if (CodeDescList != null) {
								for (CodeDesc codeDesc : CodeDescList) {
									try {
										locationFieldList.add(URLDecoder
												.decode(codeDesc.getCode(),
														"UTF-8"));
									} catch (UnsupportedEncodingException e) {
										LOGGER.error(e.getMessage(), e);
									}
								}
							}
						}
					}
				}
			}
		}
		return locationFieldList;
	}

	@Override
	public List<CoherentTimesheetReportsForm> getEmpDynCostCentreFieldList(
			Long companyId) {
		List<CoherentTimesheetReportsForm> coherentTimesheetReportsFormList = new ArrayList<>();

		CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (coherentTimesheetPreferenceVO != null
				&& coherentTimesheetPreferenceVO.getCostCenter() != null) {
			List<String> costCentreFieldList = getEmpDynFieldListByDictId(
					companyId, coherentTimesheetPreferenceVO.getCostCenter()
							.getDataDictionaryId());
			for (String locationField : costCentreFieldList) {
				CoherentTimesheetReportsForm otTimesheetReportsForm = new CoherentTimesheetReportsForm();
				otTimesheetReportsForm.setDepartmentName(locationField);
				coherentTimesheetReportsFormList.add(otTimesheetReportsForm);
			}
		}
		return coherentTimesheetReportsFormList;
	}

	@Override
	public CoherentTimesheetReportDTO genEmpShiftDetailRepExcelFile(
			Long companyId, Long employeeId,
			CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds) {
		CoherentTimesheetReportDTO coherentTimesheetReportDTO = new CoherentTimesheetReportDTO();

		Company companyVO = companyDAO.findById(companyId);
		coherentTimesheetReportDTO.setCompanyName(companyVO.getCompanyName());

		List<BigInteger> shortlistEmpIds = null;
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		if (isManager) {
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setStatus(coherentTimesheetReportsForm
					.isIncludeResignedEmployees());
			List<Tuple> employeeIdsList = employeeTimesheetReviewerDAO
					.getEmployeeIdsTupleForManager(companyId, employeeId,
							employeeShortListDTO);
			shortlistEmpIds = new ArrayList<BigInteger>();
			for (Tuple employeeTuple : employeeIdsList) {
				shortlistEmpIds.add(new BigInteger(String
						.valueOf((Long) employeeTuple.get(
								getAlias(Employee_.employeeId), Long.class))));
			}
		} else {
			List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO
					.getShortListEmployeeIds();
			List<BigInteger> reportShortListEmployeeIds = null;
			if (coherentTimesheetReportsForm.getIsShortList()) {
				EmployeeShortListDTO reportShortList = generalLogic
						.getShortListEmployeeIdsForReports(companyId,
								coherentTimesheetReportsForm.getMetaData());

				reportShortListEmployeeIds = reportShortList
						.getShortListEmployeeIds();

				if (employeeShortListDTO.getEmployeeShortList()) {
					reportShortListEmployeeIds
							.retainAll(companyShortListEmployeeIds);
				}
			}
			if (coherentTimesheetReportsForm.getIsShortList()) {
				shortlistEmpIds = new ArrayList<>(reportShortListEmployeeIds);
			} else {
				shortlistEmpIds = new ArrayList<>(companyShortListEmployeeIds);
			}
		}

		// Get All Employees By company
		Map<String, Long> empNumEmpIdMap = new HashMap<String, Long>();
		List<Employee> employeeVOList = employeeDAO.findByCompany(companyId);
		for (Employee employee : employeeVOList) {
			// if (employee.isStatus()) {
			empNumEmpIdMap.put(employee.getEmployeeNumber(),
					employee.getEmployeeId());
			// }
		}

		List<BigInteger> costCentreEmpIdsList = new ArrayList<BigInteger>();
		Map<String, CoherentShiftDetailReportDTO> empDeptCostCatDetailMap = new HashMap<String, CoherentShiftDetailReportDTO>();
		// Get Department Of Employees from Employee Information Details
		List<Object[]> deptCostCentreEmpObjectList = getEmpDynFieldsValueList(
				companyId, companyVO.getDateFormat(), employeeId);

		for (Object[] deptObject : deptCostCentreEmpObjectList) {

			if (deptObject != null && deptObject[3] != null) {
				CoherentShiftDetailReportDTO coherentOvertimeDetailReportDTO = new CoherentShiftDetailReportDTO();
				coherentOvertimeDetailReportDTO.setEmployeeNumber(String
						.valueOf(deptObject[3]));
				String lastName = "";
				if (deptObject[5] != null) {
					lastName = String.valueOf(deptObject[5]);
				}
				coherentOvertimeDetailReportDTO
						.setEmployeeName(getEmployeeName(
								String.valueOf(deptObject[4]), lastName));
				coherentOvertimeDetailReportDTO.setDepartmentCode(String
						.valueOf(deptObject[0]));
				coherentOvertimeDetailReportDTO.setCostCentre(String
						.valueOf(deptObject[2]));
				coherentOvertimeDetailReportDTO.setEmployeeId(empNumEmpIdMap
						.get(String.valueOf(deptObject[3])));
				costCentreEmpIdsList.add(BigInteger.valueOf(empNumEmpIdMap
						.get(String.valueOf(deptObject[3]))));
				empDeptCostCatDetailMap.put(String.valueOf(deptObject[3]),
						coherentOvertimeDetailReportDTO);
			}

			// mayur
			/*
			 * CoherentShiftDetailReportDTO coherentOvertimeDetailReportDTO =
			 * new CoherentShiftDetailReportDTO();
			 * coherentOvertimeDetailReportDTO.setEmployeeId(empNumEmpIdMap
			 * .get(String.valueOf(deptObject[0])));
			 * costCentreEmpIdsList.add(BigInteger.valueOf(empNumEmpIdMap
			 * .get(String.valueOf(deptObject[0]))));
			 * empDeptCostCatDetailMap.put(String.valueOf(deptObject[0]),
			 * coherentOvertimeDetailReportDTO);
			 * 
			 * String lastName = ""; if (deptObject[1] != null) { lastName =
			 * String.valueOf(deptObject[1]); }
			 * coherentOvertimeDetailReportDTO.setEmployeeName(getEmployeeName(
			 * String.valueOf(deptObject[2]), lastName));
			 * empDeptCostCatDetailMap.put(String.valueOf(deptObject[0]),
			 * coherentOvertimeDetailReportDTO);
			 */
		}

		shortlistEmpIds.retainAll(costCentreEmpIdsList);

		// Populate conditionDTO
		LundinTsheetConditionDTO conditionDTO = new LundinTsheetConditionDTO();
		EmployeeShortListDTO shortListEmpDTO = new EmployeeShortListDTO();
		shortListEmpDTO.setShortListEmployeeIds(shortlistEmpIds);
		if (coherentTimesheetReportsForm.getIsShortList()
				|| (shortlistEmpIds != null && shortlistEmpIds.size() > 0)) {
			shortListEmpDTO.setEmployeeShortList(true);
		} else {
			shortListEmpDTO.setEmployeeShortList(false);
		}
		conditionDTO.setEmployeeShortListDTO(shortListEmpDTO);
		conditionDTO
				.setEmployeeId(coherentTimesheetReportsForm.getEmployeeId());
		if (isManager) {
			conditionDTO.setEmployeeReviewerId(employeeId);
		} else {
			conditionDTO.setEmployeeReviewerId(coherentTimesheetReportsForm
					.getReviewerEmpId());
		}

		if (coherentTimesheetReportsForm.getFromBatchId() != null) {
			TimesheetBatch fromTimesheetBatch = timesheetBatchDAO
					.findById(coherentTimesheetReportsForm.getFromBatchId());
			conditionDTO.setFromBatchDate(fromTimesheetBatch.getStartDate());
		}
		if (coherentTimesheetReportsForm.getToBatchId() != null) {
			TimesheetBatch toTimesheetBatch = timesheetBatchDAO
					.findById(coherentTimesheetReportsForm.getToBatchId());
			conditionDTO.setToBatchDate(toTimesheetBatch.getEndDate());
		}

		boolean istimesheetStatusAll = false;
		List<String> timesheetStatusList = new ArrayList<>();
		if (coherentTimesheetReportsForm.getTimesheetStatus().equalsIgnoreCase(
				PayAsiaConstants.LUNDIN_STATUS_DRAFT)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
		} else if (coherentTimesheetReportsForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
		} else if (coherentTimesheetReportsForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_APPROVED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_COMPLETED);
		} else if (coherentTimesheetReportsForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_REJECTED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_REJECTED);
		} else {
			istimesheetStatusAll = true;
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_REJECTED);
		}
		conditionDTO.setStatusNameList(timesheetStatusList);
		conditionDTO.setEmpStatus(coherentTimesheetReportsForm
				.isIncludeResignedEmployees());

		if (coherentTimesheetReportsForm.getShiftType().equalsIgnoreCase(
				PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND)) {
			conditionDTO
					.setShiftType(PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND);
		}

		else if (coherentTimesheetReportsForm.getShiftType().equalsIgnoreCase(
				PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_THIRD)) {
			conditionDTO
					.setShiftType(PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_THIRD);
		} else {
			conditionDTO.setShiftType(coherentTimesheetReportsForm
					.getShiftType());
		}

		if (StringUtils.isNotBlank(coherentTimesheetReportsForm
				.getApprovedFromDate())) {
			conditionDTO.setApprovedFromDate(DateUtils
					.stringToTimestamp(coherentTimesheetReportsForm
							.getApprovedFromDate()));
		}
		if (StringUtils.isNotBlank(coherentTimesheetReportsForm
				.getApprovedToDate())) {
			conditionDTO.setApprovedToDate(DateUtils
					.stringToTimestamp(coherentTimesheetReportsForm
							.getApprovedToDate()));
		}

		List<Tuple> lionTimesheetSummaryReportVOList = new ArrayList<Tuple>();

		// Fetch Timesheet Data
		// List<Tuple> lionTimesheetSummaryReportVOList =
		// coherentShiftApplicationDetailDAO
		// .getEmployeeShiftReportDetail(conditionDTO, companyId);

		// Fetch Timesheet Data
		if (coherentTimesheetReportsForm.getTimesheetStatus().equalsIgnoreCase(
				PayAsiaConstants.LUNDIN_STATUS_APPROVED)) {
			lionTimesheetSummaryReportVOList = coherentShiftApplicationDetailDAO
					.getEmployeeShiftReportDetailWithRevAppDate(conditionDTO,
							companyId);
		} else {
			if (istimesheetStatusAll) {
				List<Tuple> lionTimesheetSummaryReportVO1List = new ArrayList<Tuple>();
				lionTimesheetSummaryReportVO1List = coherentShiftApplicationDetailDAO
						.getEmployeeShiftReportDetailWithRevAppDate(
								conditionDTO, companyId);

				List<Tuple> lionTimesheetSummaryReportVO2List = new ArrayList<Tuple>();
				lionTimesheetSummaryReportVO2List = coherentShiftApplicationDetailDAO
						.getEmployeeShiftReportDetail(conditionDTO, companyId);
				lionTimesheetSummaryReportVOList
						.addAll(lionTimesheetSummaryReportVO1List);
				lionTimesheetSummaryReportVOList
						.addAll(lionTimesheetSummaryReportVO2List);
			} else {
				lionTimesheetSummaryReportVOList = coherentShiftApplicationDetailDAO
						.getEmployeeShiftReportDetail(conditionDTO, companyId);
			}
		}

		Set<Long> empIdSet = new HashSet<Long>();
		Set<Timestamp> timesheetBatchIdSet = new HashSet<Timestamp>();
		List<CoherentShiftDetailReportDTO> coherentOvertimeDetailReportDTOVOList = new ArrayList<CoherentShiftDetailReportDTO>();
		for (Tuple applicationDetail : lionTimesheetSummaryReportVOList) {

			CoherentShiftDetailReportDTO summaryReportDTO = new CoherentShiftDetailReportDTO();
			summaryReportDTO
					.setShiftApplicationId((Long) applicationDetail
							.get(getAlias(CoherentShiftApplication_.shiftApplicationID),
									Long.class));

			summaryReportDTO.setEmployeeId((Long) applicationDetail.get(
					getAlias(Employee_.employeeId), Long.class));

			summaryReportDTO.setUpdatedDate(DateUtils.timeStampToString(
					(Timestamp) applicationDetail.get(
							getAlias(CoherentShiftApplication_.updatedDate),
							Timestamp.class), companyVO.getDateFormat()));

			summaryReportDTO.setEmployeeNumber((String) applicationDetail.get(
					getAlias(Employee_.employeeNumber), String.class));

			summaryReportDTO.setEmployeeName(getEmployeeName(
					(String) applicationDetail.get(
							getAlias(Employee_.firstName), String.class),
					(String) applicationDetail.get(
							getAlias(Employee_.lastName), String.class)));

			summaryReportDTO.setShiftType((String) applicationDetail.get(
					getAlias(AppCodeMaster_.codeDesc), String.class));
			summaryReportDTO
					.setMonth((String) applicationDetail.get(
							getAlias(TimesheetBatch_.timesheetBatchDesc),
							String.class));

			Long totalshift = applicationDetail
					.get(getAlias(CoherentShiftApplication_.totalShifts),
							Long.class);
			timesheetBatchIdSet.add((Timestamp) applicationDetail.get(
					getAlias(TimesheetBatch_.startDate), Timestamp.class));

			if (totalshift != null) {
				summaryReportDTO.setNumberOfShift(totalshift.intValue());
			} else {
				summaryReportDTO.setNumberOfShift(0);
			}

			summaryReportDTO.setCompanyName(companyVO.getCompanyName());
			coherentOvertimeDetailReportDTOVOList.add(summaryReportDTO);
			empIdSet.add((Long) applicationDetail.get(
					getAlias(Employee_.employeeId), Long.class));
		}

		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long
						.parseLong(dataDictionaryIds[count]));
			}
		}

		List<LeaveReportHeaderDTO> reportHeaderDTOs = new ArrayList<>();

		CustomFieldReportDTO customFieldReportDTO = null;
		if (!employeeIdsList.isEmpty()) {
			customFieldReportDTO = generalLogic
					.getCustomFieldDataForLeaveReport(dataDictionaryIdsList,
							employeeIdsList, companyId, false);
			Integer custFieldCount = 1;
			List<String> dataDictNameList = customFieldReportDTO
					.getDataDictNameList();
			for (String dataDictName : dataDictNameList) {
				LeaveReportHeaderDTO leaveHeaderDTO = new LeaveReportHeaderDTO();
				leaveHeaderDTO.setmDataProp("custField" + custFieldCount);
				leaveHeaderDTO.setsTitle(dataDictName);
				reportHeaderDTOs.add(leaveHeaderDTO);
				custFieldCount++;
			}

			List<Object[]> customFieldObjList = customFieldReportDTO
					.getCustomFieldObjList();
			HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
			for (Object[] objArr : customFieldObjList) {
				customFieldObjByEmpIdMap.put(
						Long.valueOf(objArr[0].toString()), objArr);
			}

			Set<String> coherentCustomFieldSet = new LinkedHashSet<>();

			HashMap<String, String> custFieldHashMap;
			int fieldCount = 0;
			for (CoherentShiftDetailReportDTO reportDTO : coherentOvertimeDetailReportDTOVOList) {
				custFieldHashMap = new LinkedHashMap<>();
				if (!customFieldObjList.isEmpty()) {
					int count = 0;

					Object[] objArr = customFieldObjByEmpIdMap.get(reportDTO
							.getEmployeeId());
					for (Object object : objArr) {
						if (count != 0) {
							custFieldHashMap.put(
									dataDictNameList.get(count - 1),
									String.valueOf(object));

							try {
								CoherentShiftDetailReportDTO.class.getMethod(
										"setCustField" + count, String.class)
										.invoke(reportDTO,
												String.valueOf(object));
							} catch (NoSuchMethodException | SecurityException
									| IllegalAccessException
									| IllegalArgumentException
									| InvocationTargetException exception) {
								LOGGER.error(exception.getMessage(), exception);
							}
						}
						count++;
					}
				}

				reportDTO.setCustFieldMap(custFieldHashMap);
			}
			for (String leaveCustField : coherentCustomFieldSet) {
				dataDictNameList.add(leaveCustField);
			}
			coherentTimesheetReportDTO.setDataDictNameList(dataDictNameList);
			List<LeaveReportCustomDataDTO> customDataDTOs = new ArrayList<>();
			for (int countF = 1; countF <= fieldCount; countF++) {
				LeaveReportCustomDataDTO customFieldDto = new LeaveReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field "
						+ countF);
				customDataDTOs.add(customFieldDto);
			}
			coherentTimesheetReportDTO.setLeaveCustomDataDTOs(customDataDTOs);
		}

		List<CoherentShiftDetailReportDTO> coherentOvertimeDetailReportDTOVONewList = new ArrayList<CoherentShiftDetailReportDTO>();
		for (CoherentShiftDetailReportDTO coherentOvertimeDetailReportDTO : coherentOvertimeDetailReportDTOVOList) {
			CoherentShiftDetailReportDTO empDeptCostCatDetail = empDeptCostCatDetailMap
					.get(coherentOvertimeDetailReportDTO.getEmployeeNumber());

			if (StringUtils.isNotBlank(empDeptCostCatDetail.getCostCentre())
					&& !empDeptCostCatDetail.getCostCentre().equalsIgnoreCase(
							"null")) {
				coherentOvertimeDetailReportDTO
						.setCostCentre(empDeptCostCatDetail.getCostCentre());
			}

			coherentOvertimeDetailReportDTO
					.setEmployeeName(empDeptCostCatDetail.getEmployeeName());

			if (StringUtils
					.isNotBlank(empDeptCostCatDetail.getDepartmentCode())
					&& !empDeptCostCatDetail.getDepartmentCode()
							.equalsIgnoreCase("null")) {
				coherentOvertimeDetailReportDTO
						.setDepartmentCode(empDeptCostCatDetail
								.getDepartmentCode());
			}

			if (coherentOvertimeDetailReportDTO.getShiftApplicationId() == 0) {
				coherentOvertimeDetailReportDTOVONewList
						.add(coherentOvertimeDetailReportDTO);
			} else {
				if (StringUtils.isNotBlank(coherentTimesheetReportsForm
						.getDepartmentDesc())) {
					if (coherentTimesheetReportsForm.getDepartmentDesc()
							.equalsIgnoreCase(
									empDeptCostCatDetail.getCostCentre())) {
						coherentOvertimeDetailReportDTOVONewList
								.add(coherentOvertimeDetailReportDTO);
					}
				} else {
					coherentOvertimeDetailReportDTOVONewList
							.add(coherentOvertimeDetailReportDTO);
				}
			}
		}

		List<CoherentShiftReportDataDTO> summarryDTOs = new ArrayList<>();
		CoherentShiftReportDataDTO yearWiseSummarryDTO = null;
		HashMap<String, List<CoherentShiftDetailReportDTO>> empTimesheetDetailMap = null;
		List<Long> empIdTraversedList = new ArrayList<Long>();
		for (CoherentShiftDetailReportDTO otTimesheetVO : coherentOvertimeDetailReportDTOVONewList) {
			if (!empIdTraversedList.contains(otTimesheetVO.getEmployeeId())) {
				empTimesheetDetailMap = new HashMap<>();
				List<CoherentShiftDetailReportDTO> detailDataDTOList = new ArrayList<CoherentShiftDetailReportDTO>();
				yearWiseSummarryDTO = new CoherentShiftReportDataDTO();
				yearWiseSummarryDTO
						.setEmployeeId(otTimesheetVO.getEmployeeId());
				yearWiseSummarryDTO.setEmployeeNumber(otTimesheetVO
						.getEmployeeNumber());
				yearWiseSummarryDTO.setEmployeeName(getEmployeeName(
						otTimesheetVO.getFirstName(),
						otTimesheetVO.getLastName()));

				for (CoherentShiftDetailReportDTO ingersollOTTimesheetDetail : coherentOvertimeDetailReportDTOVOList) {
					if (!ingersollOTTimesheetDetail.getEmployeeId().equals(
							otTimesheetVO.getEmployeeId())) {
						continue;
					}
					detailDataDTOList.add(ingersollOTTimesheetDetail);
				}
				empTimesheetDetailMap.put(otTimesheetVO.getEmployeeNumber(),
						detailDataDTOList);
				yearWiseSummarryDTO
						.setEmpTimesheetDetailMap(empTimesheetDetailMap);
				summarryDTOs.add(yearWiseSummarryDTO);
			}
			empIdTraversedList.add(otTimesheetVO.getEmployeeId());
		}

		TreeSet timesheetBatchIdTreeSet = new TreeSet();
		timesheetBatchIdTreeSet.addAll(timesheetBatchIdSet);

		if (!timesheetBatchIdTreeSet.isEmpty()) {
			TimesheetBatch startBatch = timesheetBatchDAO.getBatchByStartDate(
					companyId, Timestamp.valueOf(timesheetBatchIdTreeSet
							.first().toString()));
			TimesheetBatch endBatch = timesheetBatchDAO.getBatchByStartDate(
					companyId, Timestamp.valueOf(timesheetBatchIdTreeSet.last()
							.toString()));

			coherentTimesheetReportDTO.setFromBatchId(startBatch
					.getTimesheetBatchId());
			coherentTimesheetReportDTO.setToBatchId(endBatch
					.getTimesheetBatchId());
		}

		int totalEmployeesCount = empIdSet.size();
		coherentTimesheetReportDTO.setTotalEmployeesCount(totalEmployeesCount);
		coherentTimesheetReportDTO.setCoherentShjiftReportDTOs(summarryDTOs);

		return coherentTimesheetReportDTO;
	}

}
