package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
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
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.CustomFieldReportDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveReportCustomDataDTO;
import com.payasia.common.dto.LeaveReportHeaderDTO;
import com.payasia.common.dto.LionTimesheetReportDTO;
import com.payasia.common.dto.LionTimesheetSummaryReportDTO;
import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.LionTimesheetReportsForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
import com.payasia.dao.LionTimesheetPreferenceDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionTimesheetPreference;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LionTimesheetReportsLogic;
import com.payasia.logic.MultilingualLogic;

@Component
public class LionTimesheetReportsLogicImpl extends BaseLogic implements
		LionTimesheetReportsLogic {
	private static final Logger LOGGER = Logger
			.getLogger(LionTimesheetReportsLogicImpl.class);
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;

	@Resource
	TimesheetBatchDAO lundinTimesheetBatchDAO;
	@Resource
	EmployeeTimesheetApplicationDAO lundinTimesheetDAO;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	DataExportUtils dataExportUtils;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	GeneralDAO generalDAO;
	@Resource
	LionTimesheetPreferenceDAO lionTimesheetPreferenceDAO;
	@Resource
	MultilingualLogic multilingualLogic;
	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;

	@Resource
	LionEmployeeTimesheetApplicationDetailDAO lionEmployeeTimesheetApplicationDetailDAO;

	@Override
	public List<LionTimesheetReportsForm> otBatchList(Long companyId, int year) {
		List<LionTimesheetReportsForm> otBatchList = new ArrayList<>();
		Calendar now = Calendar.getInstance();
		Date currentDate = now.getTime();

		List<TimesheetBatch> timesheetBatchList = lundinTimesheetBatchDAO
				.getOTBatchesByYear(companyId, year);
		for (TimesheetBatch timesheetBatch : timesheetBatchList) {
			LionTimesheetReportsForm otTimesheetReportsForm = new LionTimesheetReportsForm();
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
	public LionTimesheetSummaryReportDTO getTimesheetBatchName(Long companyId,
			Long batchId) {
		LionTimesheetSummaryReportDTO lionReportDTO = new LionTimesheetSummaryReportDTO();
		TimesheetBatch timesheetBatch = lundinTimesheetBatchDAO
				.findById(batchId);
		lionReportDTO.setFileFromBatchDate(DateUtils.timeStampToString(
				timesheetBatch.getStartDate(), timesheetBatch.getCompany()
						.getDateFormat()));
		lionReportDTO.setFileToBatchDate(DateUtils.timeStampToString(
				timesheetBatch.getEndDate(), timesheetBatch.getCompany()
						.getDateFormat()));
		lionReportDTO.setFileBatchName(timesheetBatch.getTimesheetBatchDesc());
		return lionReportDTO;

	}

	@Override
	public List<LionTimesheetReportsForm> lionTimesheetEmployeeList(
			Long employeeId, Long companyId) {
		List<LionTimesheetReportsForm> employeeNumList = new ArrayList<>();

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Employee> employeeList = employeeDAO.findByCondition(conditionDTO,
				null, null, companyId);
		for (Employee employeeVO : employeeList) {
			if (employeeVO.isStatus()) {
				LionTimesheetReportsForm otTimesheetReportsForm = new LionTimesheetReportsForm();
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
	public List<LionTimesheetReportsForm> lionTimesheetReviewerList(
			Long employeeId, Long companyId) {
		List<LionTimesheetReportsForm> employeeNumList = new ArrayList<>();

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Tuple> employeeReviewerList = employeeTimesheetReviewerDAO
				.getEmployeeReviewerList(companyId);
		for (Tuple tuple : employeeReviewerList) {
			LionTimesheetReportsForm otTimesheetReportsForm = new LionTimesheetReportsForm();
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
	public List<LionTimesheetReportsForm> lionTimesheetEmpListForManager(
			Long employeeId, Long companyId) {
		List<LionTimesheetReportsForm> employeeNumList = new ArrayList<>();

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Tuple> employeeReviewerList = employeeTimesheetReviewerDAO
				.getEmployeeListByManager(companyId, employeeId);
		for (Tuple tuple : employeeReviewerList) {
			LionTimesheetReportsForm otTimesheetReportsForm = new LionTimesheetReportsForm();
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
	public List<Object[]> getTimesheetLocationEmpList(Long companyId,
			String dateFormat, Long employeeId) {
		List<Long> formIds = new ArrayList<Long>();
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

		LionTimesheetPreference lionTimesheetPreferenceVO = lionTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (lionTimesheetPreferenceVO != null) {
			// Department Field
			if (lionTimesheetPreferenceVO.getLocation() != null) {
				getColMap(lionTimesheetPreferenceVO.getLocation(), colMap);
				addDynamicTableKeyMap(lionTimesheetPreferenceVO.getLocation(),
						tableRecordInfoFrom, colMap);
				formIds.add(lionTimesheetPreferenceVO.getLocation().getFormID());
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
	public LionTimesheetReportDTO showTimesheetSummaryReport(Long companyId,
			Long employeeId, LionTimesheetReportsForm lionTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds) {
		LionTimesheetReportDTO lionTimesheetReportDTO = new LionTimesheetReportDTO();

		Company companyVO = companyDAO.findById(companyId);

		List<BigInteger> shortlistEmpIds = null;
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		if (isManager) {
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setStatus(lionTimesheetReportsForm
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
			if (lionTimesheetReportsForm.getIsShortList()) {
				EmployeeShortListDTO reportShortList = generalLogic
						.getShortListEmployeeIdsForReports(companyId,
								lionTimesheetReportsForm.getMetaData());

				reportShortListEmployeeIds = reportShortList
						.getShortListEmployeeIds();

				if (employeeShortListDTO.getEmployeeShortList()) {
					reportShortListEmployeeIds
							.retainAll(companyShortListEmployeeIds);
				}
			}
			if (lionTimesheetReportsForm.getIsShortList()) {
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

		List<BigInteger> deptEmpIdsList = new ArrayList<BigInteger>();
		Map<String, LionTimesheetSummaryReportDTO> empDeptCostCatDetailMap = new HashMap<String, LionTimesheetSummaryReportDTO>();
		// Get Department Of Employees from Employee Information Details
		List<Object[]> locationEmpObjectList = getTimesheetLocationEmpList(
				companyId, companyVO.getDateFormat(), employeeId);
		for (Object[] deptObject : locationEmpObjectList) {
			if (deptObject != null && deptObject[1] != null) {
				LionTimesheetSummaryReportDTO lionTimesheetStatusReportDTO = new LionTimesheetSummaryReportDTO();
				lionTimesheetStatusReportDTO.setEmployeeNumber(String
						.valueOf(deptObject[1]));
				String lastName = "";
  
				if (deptObject.length > 3) {
					if (deptObject[3] != null) {
						lastName = String.valueOf(deptObject[3]);
					}
				} else {
					lastName = String.valueOf(deptObject[2]);
				}
				lionTimesheetStatusReportDTO.setEmployeeName(getEmployeeName(
						String.valueOf(deptObject[2]), lastName));
				lionTimesheetStatusReportDTO.setDepartmentCode(String
						.valueOf(deptObject[0]));
				lionTimesheetStatusReportDTO.setEmployeeId(empNumEmpIdMap
						.get(String.valueOf(deptObject[1])));

				deptEmpIdsList.add(BigInteger.valueOf(empNumEmpIdMap.get(String
						.valueOf(deptObject[1]))));

				empDeptCostCatDetailMap.put(String.valueOf(deptObject[1]),
						lionTimesheetStatusReportDTO);
			}
		}

		shortlistEmpIds.retainAll(deptEmpIdsList);

		// Populate conditionDTO
		LundinTsheetConditionDTO conditionDTO = new LundinTsheetConditionDTO();
		EmployeeShortListDTO shortListEmpDTO = new EmployeeShortListDTO();
		shortListEmpDTO.setShortListEmployeeIds(shortlistEmpIds);
		if (lionTimesheetReportsForm.getIsShortList()
				|| (shortlistEmpIds != null && shortlistEmpIds.size() > 0)) {
			shortListEmpDTO.setEmployeeShortList(true);
		} else {
			shortListEmpDTO.setEmployeeShortList(false);
		}
		conditionDTO.setEmployeeShortListDTO(shortListEmpDTO);
		conditionDTO.setEmployeeId(lionTimesheetReportsForm.getEmployeeId());
		if (isManager) {
			conditionDTO.setEmployeeReviewerId(employeeId);
		} else {
			conditionDTO.setEmployeeReviewerId(lionTimesheetReportsForm
					.getReviewerEmpId());
		}

		conditionDTO.setBatchId(lionTimesheetReportsForm.getFromBatchId());

		List<String> timesheetStatusList = new ArrayList<>();
		if (lionTimesheetReportsForm.getTimesheetStatus().equalsIgnoreCase(
				PayAsiaConstants.LUNDIN_STATUS_DRAFT)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
		}
		if (lionTimesheetReportsForm.getTimesheetStatus().equalsIgnoreCase(
				PayAsiaConstants.LUNDIN_STATUS_SUBMITTED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
		}
		if (lionTimesheetReportsForm.getTimesheetStatus().equalsIgnoreCase(
				PayAsiaConstants.LUNDIN_STATUS_APPROVED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_COMPLETED);
		}
		conditionDTO.setStatusNameList(timesheetStatusList);
		conditionDTO.setEmpStatus(lionTimesheetReportsForm
				.isIncludeResignedEmployees());

		// Fetch Timesheet Data
		List<EmployeeTimesheetApplication> lionTimesheetSummaryReportVOList = lundinTimesheetDAO
				.findLionTimsheetByCondition(companyId, conditionDTO);

		Set<Long> empIdSet = new HashSet<Long>();
		List<LionTimesheetSummaryReportDTO> lionTimesheetSummaryDTOList = new ArrayList<LionTimesheetSummaryReportDTO>();
		for (EmployeeTimesheetApplication employeeTimesheetApplicationVO : lionTimesheetSummaryReportVOList) {
			LionTimesheetSummaryReportDTO summaryReportDTO = new LionTimesheetSummaryReportDTO();
			summaryReportDTO.setTimesheetId(employeeTimesheetApplicationVO
					.getTimesheetId());
			summaryReportDTO.setEmployeeId(employeeTimesheetApplicationVO
					.getEmployee().getEmployeeId());
			summaryReportDTO
					.setEmployeeName(getEmployeeName(
							employeeTimesheetApplicationVO.getEmployee()
									.getFirstName(),
							employeeTimesheetApplicationVO.getEmployee()
									.getLastName()));
			summaryReportDTO.setEmployeeNumber(employeeTimesheetApplicationVO
					.getEmployee().getEmployeeNumber());
			summaryReportDTO
					.setTimesheetStatusName(employeeTimesheetApplicationVO
							.getTimesheetStatusMaster()
							.getTimesheetStatusName());
			summaryReportDTO.setTotalHoursWorked(employeeTimesheetApplicationVO
					.getLionEmployeeTimesheets().iterator().next()
					.getTimesheetTotalHours());
			summaryReportDTO
					.setExcessHoursWorked(employeeTimesheetApplicationVO
							.getLionEmployeeTimesheets().iterator().next()
							.getExcessHoursWorked());

			// Get Employee Reviewer
			List<EmployeeTimesheetReviewer> employeeTimesheetReviewerList = employeeTimesheetReviewerDAO
					.findByCondition(employeeTimesheetApplicationVO
							.getEmployee().getEmployeeId(), companyId);
			for (EmployeeTimesheetReviewer timesheetReviewer : employeeTimesheetReviewerList) {
				summaryReportDTO.setEmployeeReviewer1Number(timesheetReviewer
						.getEmployeeReviewer().getEmployeeNumber());
				summaryReportDTO.setEmployeeReviewer1Name(getEmployeeName(
						timesheetReviewer.getEmployeeReviewer().getFirstName(),
						timesheetReviewer.getEmployeeReviewer().getLastName()));
			}

			lionTimesheetSummaryDTOList.add(summaryReportDTO);

			empIdSet.add(employeeTimesheetApplicationVO.getEmployee()
					.getEmployeeId());
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
		LeaveReportHeaderDTO employeeNo = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeName = new LeaveReportHeaderDTO();
		employeeNo.setmDataProp("employeeNumber");
		employeeNo.setsTitle("Employee ID");
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");
		reportHeaderDTOs.add(employeeNo);
		reportHeaderDTOs.add(employeeName);
		LeaveReportHeaderDTO location = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO totalHoursWorked = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO excessHoursWorked = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO timesheetStatus = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO reviewer1EmployeeId = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO reviewer1EmployeeName = new LeaveReportHeaderDTO();
		location.setmDataProp("departmentCode");
		location.setsTitle("Location");
		timesheetStatus.setmDataProp("timesheetStatusName");
		timesheetStatus.setsTitle("Status");
		totalHoursWorked.setmDataProp("totalHoursWorked");
		totalHoursWorked.setsTitle("Total Hours Worked");
		excessHoursWorked.setmDataProp("excessHoursWorked");
		excessHoursWorked.setsTitle("Excess Hours Worked");
		reviewer1EmployeeId.setmDataProp("employeeReviewer1Number");
		reviewer1EmployeeId.setsTitle("Reviewer1 Employee ID");
		reviewer1EmployeeName.setmDataProp("employeeReviewer1Name");
		reviewer1EmployeeName.setsTitle("Reviewer1 Employee Name");
		reportHeaderDTOs.add(location);
		reportHeaderDTOs.add(timesheetStatus);
		reportHeaderDTOs.add(totalHoursWorked);
		reportHeaderDTOs.add(excessHoursWorked);
		reportHeaderDTOs.add(reviewer1EmployeeId);
		reportHeaderDTOs.add(reviewer1EmployeeName);
		lionTimesheetReportDTO.setTimesheetHeaderDTOs(reportHeaderDTOs);

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

			Set<String> leaveCustomFieldSet = new LinkedHashSet<>();

			HashMap<String, String> custFieldHashMap;
			int fieldCount = 0;
			for (LionTimesheetSummaryReportDTO reportDTO : lionTimesheetSummaryDTOList) {
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
								LionTimesheetSummaryReportDTO.class.getMethod(
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
			for (String leaveCustField : leaveCustomFieldSet) {
				dataDictNameList.add(leaveCustField);
			}
			lionTimesheetReportDTO.setDataDictNameList(dataDictNameList);
			List<LeaveReportCustomDataDTO> customDataDTOs = new ArrayList<>();
			for (int countF = 1; countF <= fieldCount; countF++) {
				LeaveReportCustomDataDTO customFieldDto = new LeaveReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field "
						+ countF);
				customDataDTOs.add(customFieldDto);
			}
			lionTimesheetReportDTO.setLeaveCustomDataDTOs(customDataDTOs);
		}

		List<LionTimesheetSummaryReportDTO> lionTimesheetStatusReportVONewList = new ArrayList<LionTimesheetSummaryReportDTO>();
		for (LionTimesheetSummaryReportDTO timesheetStatusReportDTO : lionTimesheetSummaryDTOList) {
			LionTimesheetSummaryReportDTO empDeptCostCatDetail = empDeptCostCatDetailMap
					.get(timesheetStatusReportDTO.getEmployeeNumber());
			if (StringUtils
					.isNotBlank(empDeptCostCatDetail.getDepartmentCode())
					&& !empDeptCostCatDetail.getDepartmentCode()
							.equalsIgnoreCase("null")) {
				timesheetStatusReportDTO.setDepartmentCode(empDeptCostCatDetail
						.getDepartmentCode());
			}

			timesheetStatusReportDTO.setEmployeeName(empDeptCostCatDetail
					.getEmployeeName());
			if (timesheetStatusReportDTO.getTimesheetId() == 0) {
				lionTimesheetStatusReportVONewList
						.add(timesheetStatusReportDTO);
			} else {
				if (StringUtils.isNotBlank(lionTimesheetReportsForm
						.getDepartmentDesc())) {
					if (lionTimesheetReportsForm.getDepartmentDesc()
							.equalsIgnoreCase(
									empDeptCostCatDetail.getDepartmentCode())) {
						lionTimesheetStatusReportVONewList
								.add(timesheetStatusReportDTO);
					}
				} else {
					lionTimesheetStatusReportVONewList
							.add(timesheetStatusReportDTO);
				}
			}
		}

		int totalEmployeesCount = empIdSet.size();
		lionTimesheetReportDTO.setTotalEmployeesCount(totalEmployeesCount);
		lionTimesheetReportDTO
				.setLionTimesheetStatusReportDTOs(lionTimesheetStatusReportVONewList);

		return lionTimesheetReportDTO;
	}

	public List<String> getLocationFieldList(Long companyId,
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
									PayAsiaConstants.PAYASIA_FIELD_DROPDOWN)
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
	public List<LionTimesheetReportsForm> getEmpDynlocationFieldList(
			Long companyId) {
		List<LionTimesheetReportsForm> lionDepartmentList = new ArrayList<>();

		LionTimesheetPreference lionTimesheetPreferenceVO = lionTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (lionTimesheetPreferenceVO != null
				&& lionTimesheetPreferenceVO.getLocation() != null) {
			List<String> locationFieldList = getLocationFieldList(companyId,
					lionTimesheetPreferenceVO.getLocation()
							.getDataDictionaryId());
			for (String locationField : locationFieldList) {
				LionTimesheetReportsForm otTimesheetReportsForm = new LionTimesheetReportsForm();
				otTimesheetReportsForm.setDepartmentName(locationField);
				lionDepartmentList.add(otTimesheetReportsForm);
			}
		}
		return lionDepartmentList;
	}

	@Override
	public LionTimesheetReportDTO showTimesheetSummaryReportDetails(
			Long companyId, Long employeeId,
			LionTimesheetReportsForm lionTimesheetDetailListingReportForm,
			Boolean isManager, String[] dataDictionaryIds) {
		LionTimesheetReportDTO lionTimesheetReportDTO = new LionTimesheetReportDTO();

		Company companyVO = companyDAO.findById(companyId);

		List<BigInteger> shortlistEmpIds = null;
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		if (isManager) {
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setStatus(lionTimesheetDetailListingReportForm
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
			if (lionTimesheetDetailListingReportForm.getIsShortList()) {
				EmployeeShortListDTO reportShortList = generalLogic
						.getShortListEmployeeIdsForReports(companyId,
								lionTimesheetDetailListingReportForm
										.getMetaData());

				reportShortListEmployeeIds = reportShortList
						.getShortListEmployeeIds();

				if (employeeShortListDTO.getEmployeeShortList()) {
					reportShortListEmployeeIds
							.retainAll(companyShortListEmployeeIds);
				}
			}
			if (lionTimesheetDetailListingReportForm.getIsShortList()) {
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

		List<BigInteger> deptEmpIdsList = new ArrayList<BigInteger>();
		Map<String, LionTimesheetSummaryReportDTO> empDeptCostCatDetailMap = new HashMap<String, LionTimesheetSummaryReportDTO>();
		// Get Department Of Employees from Employee Information Details
		List<Object[]> locationEmpObjectList = getTimesheetLocationEmpList(
				companyId, companyVO.getDateFormat(), employeeId);
		for (Object[] deptObject : locationEmpObjectList) {
			if (deptObject != null && deptObject[1] != null) {
				LionTimesheetSummaryReportDTO lionTimesheetStatusReportDTO = new LionTimesheetSummaryReportDTO();
				lionTimesheetStatusReportDTO.setEmployeeNumber(String
						.valueOf(deptObject[1]));
				String lastName = "";  
				if (deptObject.length > 3) {
					if (deptObject[3] != null) {
						lastName = String.valueOf(deptObject[3]);
					}
				} else {
					lastName = String.valueOf(deptObject[2]);
				}
				lionTimesheetStatusReportDTO.setEmployeeName(getEmployeeName(
						String.valueOf(deptObject[2]), lastName));
				lionTimesheetStatusReportDTO.setDepartmentCode(String
						.valueOf(deptObject[0]));
				lionTimesheetStatusReportDTO.setEmployeeId(empNumEmpIdMap
						.get(String.valueOf(deptObject[1])));

				deptEmpIdsList.add(BigInteger.valueOf(empNumEmpIdMap.get(String
						.valueOf(deptObject[1]))));

				empDeptCostCatDetailMap.put(String.valueOf(deptObject[1]),
						lionTimesheetStatusReportDTO);
			}
		}

		shortlistEmpIds.retainAll(deptEmpIdsList);

		LundinTsheetConditionDTO conditionDTO = new LundinTsheetConditionDTO();
		EmployeeShortListDTO shortListEmpDTO = new EmployeeShortListDTO();
		shortListEmpDTO.setShortListEmployeeIds(shortlistEmpIds);
		if (lionTimesheetDetailListingReportForm.getIsShortList()
				|| (shortlistEmpIds != null && shortlistEmpIds.size() > 0)) {
			shortListEmpDTO.setEmployeeShortList(true);
		} else {
			shortListEmpDTO.setEmployeeShortList(false);
		}
		conditionDTO.setEmployeeShortListDTO(shortListEmpDTO);
		conditionDTO.setEmployeeId(lionTimesheetDetailListingReportForm
				.getEmployeeId());
		if (isManager) {
			conditionDTO.setEmployeeReviewerId(employeeId);
		} else {
			conditionDTO
					.setEmployeeReviewerId(lionTimesheetDetailListingReportForm
							.getReviewerEmpId());
		}

		List<String> timesheetStatusList = new ArrayList<>();
		if (lionTimesheetDetailListingReportForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.CLAIM_PAID_STATUS_ALL)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_REJECTED);
			timesheetStatusList.add(PayAsiaConstants.LEAVE_STATUS_PENDING);
		}
		if (lionTimesheetDetailListingReportForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_DRAFT)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
		}
		if (lionTimesheetDetailListingReportForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);

		}
		if (lionTimesheetDetailListingReportForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_APPROVED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
		}
		if (lionTimesheetDetailListingReportForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_REJECTED)) {
			timesheetStatusList.add(PayAsiaConstants.LUNDIN_STATUS_REJECTED);
		}
		if (lionTimesheetDetailListingReportForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {
			timesheetStatusList.add(PayAsiaConstants.LEAVE_STATUS_PENDING);
		}
		conditionDTO.setStatusNameList(timesheetStatusList);

		if (lionTimesheetDetailListingReportForm.getFromBatchId() != null) {
			TimesheetBatch fromTimesheetBatch = lundinTimesheetBatchDAO
					.findById(lionTimesheetDetailListingReportForm.getFromBatchId());
			conditionDTO.setBatchId(fromTimesheetBatch.getTimesheetBatchId());
		}
		
		if (StringUtils.isNotBlank(lionTimesheetDetailListingReportForm
				.getApprovedFromDate())) {
			conditionDTO.setApprovedFromDate(DateUtils
					.stringToTimestamp(lionTimesheetDetailListingReportForm
							.getApprovedFromDate()));
		}
		if (StringUtils.isNotBlank(lionTimesheetDetailListingReportForm
				.getApprovedToDate())) {
			conditionDTO.setApprovedToDate(DateUtils
					.stringToTimestamp(lionTimesheetDetailListingReportForm
							.getApprovedToDate()));
		}

		conditionDTO.setEmpStatus(lionTimesheetDetailListingReportForm
				.isIncludeResignedEmployees());

		// Fetch Timesheet Data

		List<LionEmployeeTimesheetApplicationDetail> lionTimesheetSummaryReportDetailsVOList = lionEmployeeTimesheetApplicationDetailDAO
				.findLionTimsheetDetailsByCondition(companyId, conditionDTO);

		Set<Long> empIdSet = new HashSet<Long>();
		List<LionTimesheetSummaryReportDTO> lionTimesheetSummaryDTOList = new ArrayList<LionTimesheetSummaryReportDTO>();
		for (LionEmployeeTimesheetApplicationDetail lionTimesheetApplicationDetailsVO : lionTimesheetSummaryReportDetailsVOList) {
			LionTimesheetSummaryReportDTO summaryReportDTO = new LionTimesheetSummaryReportDTO();

			summaryReportDTO
					.setEmployeeNumber(lionTimesheetApplicationDetailsVO
							.getEmployeeTimesheetApplication().getEmployee()
							.getEmployeeNumber());
			summaryReportDTO.setEmployeeName(getEmployeeName(
					lionTimesheetApplicationDetailsVO
							.getEmployeeTimesheetApplication().getEmployee()
							.getFirstName(), lionTimesheetApplicationDetailsVO
							.getEmployeeTimesheetApplication().getEmployee()
							.getLastName()));

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					companyVO.getDateFormat());
			summaryReportDTO.setDate(simpleDateFormat
					.format(lionTimesheetApplicationDetailsVO
							.getTimesheetDate()));

			Date date = DateUtils.stringToDate(simpleDateFormat
					.format(lionTimesheetApplicationDetailsVO
							.getTimesheetDate()), companyVO.getDateFormat());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			summaryReportDTO.setDay(getWeekday(cal.get(Calendar.DAY_OF_WEEK)));

			summaryReportDTO.setInTime(DateUtils.timeStampToStringWithTimeM(
					lionTimesheetApplicationDetailsVO.getInTime(),
					companyVO.getDateFormat()).substring(12, 17));
			summaryReportDTO.setOutTime(DateUtils.timeStampToStringWithTimeM(
					lionTimesheetApplicationDetailsVO.getOutTime(),
					companyVO.getDateFormat()).substring(12, 17));
			summaryReportDTO.setBreakTime(DateUtils.timeStampToStringWithTimeM(
					lionTimesheetApplicationDetailsVO.getBreakTimeHours(),
					companyVO.getDateFormat()).substring(12, 17));

			summaryReportDTO
					.setTotalHoursWorked(lionTimesheetApplicationDetailsVO
							.getTotalHoursWorked());
			summaryReportDTO.setRemark(lionTimesheetApplicationDetailsVO
					.getRemarks());

			lionTimesheetSummaryDTOList.add(summaryReportDTO);

			empIdSet.add(lionTimesheetApplicationDetailsVO
					.getEmployeeTimesheetApplication().getEmployee()
					.getEmployeeId());
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
		LeaveReportHeaderDTO employeeNo = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeName = new LeaveReportHeaderDTO();
		employeeNo.setmDataProp("employeeNumber");
		employeeNo.setsTitle("Employee Number");
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");
		reportHeaderDTOs.add(employeeNo);
		reportHeaderDTOs.add(employeeName);
		LeaveReportHeaderDTO date = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO day = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO inTime = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO outTime = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO breakTimeHours = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO totalHoursWorked = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO remarks = new LeaveReportHeaderDTO();
		date.setmDataProp("date");
		date.setsTitle("Date");
		day.setmDataProp("day");
		day.setsTitle("Day");
		inTime.setmDataProp("inTime");
		inTime.setsTitle("In Time");
		outTime.setmDataProp("outTime");
		outTime.setsTitle("Out Time");
		breakTimeHours.setmDataProp("breakTime");
		breakTimeHours.setsTitle("Break Time Hours");
		totalHoursWorked.setmDataProp("totalHoursWorked");
		totalHoursWorked.setsTitle("Total Hours Worked");
		remarks.setmDataProp("remark");
		remarks.setsTitle("Remarks");
		reportHeaderDTOs.add(date);
		reportHeaderDTOs.add(day);
		// reportHeaderDTOs.add(totalHoursWorked);
		reportHeaderDTOs.add(inTime);
		reportHeaderDTOs.add(outTime);
		reportHeaderDTOs.add(breakTimeHours);
		reportHeaderDTOs.add(totalHoursWorked);
		reportHeaderDTOs.add(remarks);
		lionTimesheetReportDTO.setTimesheetHeaderDTOs(reportHeaderDTOs);

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

			Set<String> leaveCustomFieldSet = new LinkedHashSet<>();

			HashMap<String, String> custFieldHashMap;
			int fieldCount = 0;
			for (LionTimesheetSummaryReportDTO reportDTO : lionTimesheetSummaryDTOList) {
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
								LionTimesheetSummaryReportDTO.class.getMethod(
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
			for (String leaveCustField : leaveCustomFieldSet) {
				dataDictNameList.add(leaveCustField);
			}
			lionTimesheetReportDTO.setDataDictNameList(dataDictNameList);
			List<LeaveReportCustomDataDTO> customDataDTOs = new ArrayList<>();
			for (int countF = 1; countF <= fieldCount; countF++) {
				LeaveReportCustomDataDTO customFieldDto = new LeaveReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field "
						+ countF);
				customDataDTOs.add(customFieldDto);
			}
			lionTimesheetReportDTO.setLeaveCustomDataDTOs(customDataDTOs);
		}

		List<LionTimesheetSummaryReportDTO> lionTimesheetStatusReportVONewList = new ArrayList<LionTimesheetSummaryReportDTO>();
		for (LionTimesheetSummaryReportDTO timesheetStatusReportDTO : lionTimesheetSummaryDTOList) {
			LionTimesheetSummaryReportDTO empDeptCostCatDetail = empDeptCostCatDetailMap
					.get(timesheetStatusReportDTO.getEmployeeNumber());
			if (StringUtils
					.isNotBlank(empDeptCostCatDetail.getDepartmentCode())
					&& !empDeptCostCatDetail.getDepartmentCode()
							.equalsIgnoreCase("null")) {
				timesheetStatusReportDTO.setDepartmentCode(empDeptCostCatDetail
						.getDepartmentCode());
			}

			timesheetStatusReportDTO.setEmployeeName(empDeptCostCatDetail
					.getEmployeeName());

			lionTimesheetStatusReportVONewList.add(timesheetStatusReportDTO);

		}

		int totalEmployeesCount = empIdSet.size();
		lionTimesheetReportDTO.setTotalEmployeesCount(totalEmployeesCount);
		lionTimesheetReportDTO
				.setLionTimesheetStatusReportDTOs(lionTimesheetStatusReportVONewList);

		return lionTimesheetReportDTO;
	}

	private String getWeekday(int weekdayNo) {
		String weekday = null;

		switch (weekdayNo) {
		case 1:
			weekday = "Sunday";
			break;
		case 2:
			weekday = "Monday";
			break;
		case 3:
			weekday = "Tuesday";
			break;
		case 4:
			weekday = "Wednesday";
			break;
		case 5:
			weekday = "Thursday";
			break;
		case 6:
			weekday = "Friday";
			break;
		case 7:
			weekday = "Saturday";
		}

		return weekday;
	}
}
