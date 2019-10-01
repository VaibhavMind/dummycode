package com.payasia.logic.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

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
import com.payasia.common.dto.LundinDailyPaidTimesheetDTO;
import com.payasia.common.dto.LundinTimesheetReportDTO;
import com.payasia.common.dto.LundinTimesheetStatusReportDTO;
import com.payasia.common.dto.LundinTimewritingDeptReportDTO;
import com.payasia.common.dto.LundinTimewritingReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.LeaveReportsResponse;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.LundinDepartmentDAO;
import com.payasia.dao.LundinTimesheetPreferenceDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LundinAFE;
import com.payasia.dao.bean.LundinBlock;
import com.payasia.dao.bean.LundinDepartment;
import com.payasia.dao.bean.LundinTimesheetPreference;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LundinTimesheetReportsLogic;
import com.payasia.logic.LundinTimesheetRequestLogic;

@Component
public class LundinTimesheetReportsLogicImpl extends BaseLogic implements
		LundinTimesheetReportsLogic {
	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetReportsLogicImpl.class);
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	TimesheetBatchDAO lundinTimesheetBatchDAO;
	@Resource
	LundinDepartmentDAO lundinDepartmentDAO;
	@Resource
	LundinBlockDAO lundinBlockDAO;
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
	LundinTimesheetPreferenceDAO lundinTimesheetPreferenceDAO;
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;
	@Resource
	LundinTimesheetRequestLogic lundinTimesheetRequestLogic;
	@Resource
	EmployeeTimesheetReviewerDAO lundinEmployeeReviewerDAO;

	@Override
	public LeaveReportsResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			Long companyId, Long employeeId, Boolean includeResignedEmployees,
			long departmentId) {
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();
		List<EmployeeListForm> employeeListForms = new ArrayList<>();
		List<EmployeeListForm> employeestatusListForms = new ArrayList<>();
		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber("%" + searchText.trim() + "%");
			}
		}
		if (searchCondition.equals(PayAsiaConstants.FIRST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFirstName("%" + searchText.trim() + "%");
			}
		}
		if (searchCondition.equals(PayAsiaConstants.LAST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLastName("%" + searchText.trim() + "%");
			}
		}
		conditionDTO.setStatus(includeResignedEmployees);

		List<Employee> employeeVOList = new ArrayList<>();
		Company companyVO = companyDAO.findById(companyId);
		if (departmentId != 0) {
			LundinDepartment lundinDepartment = lundinDepartmentDAO
					.findById(departmentId);
			if (lundinDepartment != null) {
				String deptCode = lundinDepartment
						.getDynamicFormFieldRefValue().getCode();
				// Get All Employees By company
				Map<String, Long> empNumEmpIdMap = new HashMap<String, Long>();
				Map<String, Boolean> empNumStatusMap = new HashMap<String, Boolean>();
				List<Employee> employeeIdVOList = employeeDAO
						.findByCompany(companyId);

				for (Employee employee : employeeIdVOList) {
					empNumEmpIdMap.put(employee.getEmployeeNumber(),
							employee.getEmployeeId());
				}

				for (Employee employee : employeeIdVOList) {
					empNumStatusMap.put(employee.getEmployeeNumber(),
							employee.isStatus());
				}

				List<Object[]> deptEmployeeObjList = getTimesheetStatusDepartmentEmpList(
						companyId, companyVO.getDateFormat(), employeeId,
						lundinDepartment.getDynamicFormFieldRefValue()
								.getDataDictionary());
				// Get Department Of Employees from Employee Information Details
				for (Object[] deptObject : deptEmployeeObjList) {
					if (deptObject != null
							&& deptObject[2] != null
							&& deptObject[0] != null
							&& String.valueOf(deptObject[0]).equalsIgnoreCase(
									deptCode)) {
						EmployeeListForm lundinTimesheetStatusReportDTO = new EmployeeListForm();
						lundinTimesheetStatusReportDTO.setEmployeeNumber(String
								.valueOf(deptObject[2]));
						String lastName = "";
						if (deptObject[4] != null) {
							lastName = String.valueOf(deptObject[4]);
						}
						lundinTimesheetStatusReportDTO
								.setEmployeeName(getEmployeeName(
										String.valueOf(deptObject[3]), lastName));
						lundinTimesheetStatusReportDTO
								.setEmployeeID(empNumEmpIdMap.get(String
										.valueOf(deptObject[2])));
						lundinTimesheetStatusReportDTO
								.setStatus(empNumStatusMap.get(String
										.valueOf(deptObject[2])));
						employeeListFormList
								.add(lundinTimesheetStatusReportDTO);
					}
				}

				if (conditionDTO.getStatus() == false) {
					for (EmployeeListForm employee : employeeListFormList) {
						if (employee.isStatus()) {
							employeestatusListForms.add(employee);
						}
					}
				} else {
					employeestatusListForms.addAll(employeeListFormList);
				}

				filterDeptEmployeeByCondition(searchCondition, searchText,
						employeeListForms, employeestatusListForms);
			}
		} else {
			employeeVOList = employeeDAO.findByCondition(conditionDTO, null,
					null, companyId);
		}

		for (Employee employee : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListForms.add(employeeForm);
		}

		LeaveReportsResponse response = new LeaveReportsResponse();
		response.setSearchEmployeeList(employeeListForms);
		return response;
	}

	private void filterDeptEmployeeByCondition(String searchCondition,
			String searchText, List<EmployeeListForm> employeeListForms,
			List<EmployeeListForm> employeestatusListForms) {
		for (EmployeeListForm employee : employeestatusListForms) {
			if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_NUMBER)) {
				if (StringUtils.isNotBlank(searchText)) {
					if (employee.getEmployeeNumber().toLowerCase()
							.contains(searchText.trim().toLowerCase())) {
						employeeListForms.add(employee);
					}
				} else {
					employeeListForms.add(employee);
				}
			} else if (searchCondition.equals(PayAsiaConstants.FIRST_NAME)) {
				if (StringUtils.isNotBlank(searchText)) {
					if (employee.getEmployeeName().toLowerCase()
							.contains(searchText.trim().toLowerCase())) {
						employeeListForms.add(employee);
					}
				} else {
					employeeListForms.add(employee);
				}
			} else if (searchCondition.equals(PayAsiaConstants.LAST_NAME)) {
				if (StringUtils.isNotBlank(searchText)) {
					if (employee.getEmployeeName().toLowerCase()
							.contains(searchText.trim().toLowerCase())) {
						employeeListForms.add(employee);
					}
				} else {
					employeeListForms.add(employee);
				}
			} else {
				employeeListForms.add(employee);
			}
		}
	}

	@Override
	public LeaveReportsResponse searchEmployeeByManager(PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			Long companyId, Long employeeId, Boolean includeResignedEmployees,
			long departmentId) {
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();
		List<EmployeeListForm> employeeListForms = new ArrayList<>();
		List<EmployeeListForm> employeestatusListForms = new ArrayList<>();
		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber("%" + searchText.trim() + "%");
			}
		}
		if (searchCondition.equals(PayAsiaConstants.FIRST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFirstName("%" + searchText.trim() + "%");
			}
		}
		if (searchCondition.equals(PayAsiaConstants.LAST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLastName("%" + searchText.trim() + "%");
			}
		}
		conditionDTO.setStatus(includeResignedEmployees);

		List<Tuple> employeeVOList = new ArrayList<>();
		Company companyVO = companyDAO.findById(companyId);
		if (departmentId != 0) {
			List<EmployeeListForm> employeeDeptListFormList = new ArrayList<EmployeeListForm>();
			LundinDepartment lundinDepartment = lundinDepartmentDAO
					.findById(departmentId);
			if (lundinDepartment != null) {
				String deptCode = lundinDepartment
						.getDynamicFormFieldRefValue().getCode();
				// Get All Employees By company
				Map<String, Long> empNumEmpIdMap = new HashMap<String, Long>();
				Map<String, Boolean> empNumStatusMap = new HashMap<String, Boolean>();
				List<Employee> employeeIdVOList = employeeDAO
						.findByCompany(companyId);

				for (Employee employee : employeeIdVOList) {
					empNumEmpIdMap.put(employee.getEmployeeNumber(),
							employee.getEmployeeId());
				}

				for (Employee employee : employeeIdVOList) {
					empNumStatusMap.put(employee.getEmployeeNumber(),
							employee.isStatus());
				}

				List<Object[]> deptEmployeeObjList = getTimesheetStatusDepartmentEmpList(
						companyId, companyVO.getDateFormat(), employeeId,
						lundinDepartment.getDynamicFormFieldRefValue()
								.getDataDictionary());
				// Get Department Of Employees from Employee Information Details
				for (Object[] deptObject : deptEmployeeObjList) {
					if (deptObject != null
							&& deptObject[2] != null
							&& deptObject[0] != null
							&& String.valueOf(deptObject[0]).equalsIgnoreCase(
									deptCode)) {
						EmployeeListForm lundinTimesheetStatusReportDTO = new EmployeeListForm();
						lundinTimesheetStatusReportDTO.setEmployeeNumber(String
								.valueOf(deptObject[2]));
						String lastName = "";
						if (deptObject[4] != null) {
							lastName = String.valueOf(deptObject[4]);
						}
						lundinTimesheetStatusReportDTO
								.setEmployeeName(getEmployeeName(
										String.valueOf(deptObject[3]), lastName));
						lundinTimesheetStatusReportDTO
								.setEmployeeID(empNumEmpIdMap.get(String
										.valueOf(deptObject[2])));
						lundinTimesheetStatusReportDTO
								.setStatus(empNumStatusMap.get(String
										.valueOf(deptObject[2])));
						employeeDeptListFormList
								.add(lundinTimesheetStatusReportDTO);
					}
				}

				if (conditionDTO.getStatus() == false) {
					for (EmployeeListForm employee : employeeDeptListFormList) {
						if (employee.isStatus()) {
							employeestatusListForms.add(employee);
						}
					}
				} else {
					employeestatusListForms.addAll(employeeDeptListFormList);
				}

				filterDeptEmployeeByCondition(searchCondition, searchText,
						employeeListForms, employeestatusListForms);
				if (!employeeListForms.isEmpty()) {
					employeeVOList = lundinEmployeeReviewerDAO
							.getEmployeeIdsTupleForTimesheetReviewer(
									conditionDTO, companyId, employeeId,
									employeeShortListDTO);
					List<Tuple> loginEmpList = new ArrayList<>();
					loginEmpList = employeeDAO.getLundinManagerInfoDetail(
							conditionDTO, employeeId);
					employeeVOList.addAll(loginEmpList);
					List<Long> employeeIdList = new ArrayList<Long>();
					for (Tuple employeeTuple : employeeVOList) {
						employeeIdList.add(employeeTuple.get(
								getAlias(Employee_.employeeId), Long.class));
					}
					for (EmployeeListForm employeeListForm : employeeListForms) {
						if (employeeIdList.contains(employeeListForm
								.getEmployeeID())) {
							employeeListFormList.add(employeeListForm);
						}
					}
				}
			}
		} else {
			// For All Subordinate Employee
			employeeVOList = lundinEmployeeReviewerDAO
					.getEmployeeIdsTupleForTimesheetReviewer(conditionDTO,
							companyId, employeeId, employeeShortListDTO);
			List<Tuple> loginEmpList = new ArrayList<>();
			loginEmpList = employeeDAO.getLundinManagerInfoDetail(conditionDTO,
					employeeId);
			employeeVOList.addAll(loginEmpList);

			for (Tuple employeeTuple : employeeVOList) {
				EmployeeListForm employeeForm = new EmployeeListForm();

				String employeeName = "";
				employeeName += employeeTuple.get(
						getAlias(Employee_.firstName), String.class) + " ";
				if (StringUtils.isNotBlank(employeeTuple.get(
						getAlias(Employee_.lastName), String.class))) {
					employeeName += employeeTuple.get(
							getAlias(Employee_.lastName), String.class);
				}
				employeeForm.setEmployeeName(employeeName);

				employeeForm.setEmployeeNumber(employeeTuple.get(
						getAlias(Employee_.employeeNumber), String.class));

				employeeForm.setEmployeeID(employeeTuple.get(
						getAlias(Employee_.employeeId), Long.class));
				employeeListFormList.add(employeeForm);
			}
		}

		LeaveReportsResponse response = new LeaveReportsResponse();
		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public List<LundinTimesheetReportsForm> otBatchList(Long companyId, int year) {
		List<LundinTimesheetReportsForm> otBatchList = new ArrayList<>();
		Calendar now = Calendar.getInstance();
		Date currentDate = now.getTime();

		List<TimesheetBatch> lundinTimesheetBatchList = lundinTimesheetBatchDAO
				.getOTBatchesByYear(companyId, year);
		for (TimesheetBatch lundinTimesheetBatch : lundinTimesheetBatchList) {
			LundinTimesheetReportsForm otTimesheetReportsForm = new LundinTimesheetReportsForm();
			otTimesheetReportsForm.setOtBatchId(lundinTimesheetBatch
					.getTimesheetBatchId());
			otTimesheetReportsForm.setBatchDesc(lundinTimesheetBatch
					.getTimesheetBatchDesc());
			Date startDate = DateUtils.stringToDate(DateUtils
					.timeStampToString(lundinTimesheetBatch.getStartDate(),
							lundinTimesheetBatch.getCompany().getDateFormat()),
					lundinTimesheetBatch.getCompany().getDateFormat());
			Date endDate = DateUtils.stringToDate(DateUtils.timeStampToString(
					lundinTimesheetBatch.getEndDate(), lundinTimesheetBatch
							.getCompany().getDateFormat()),
					lundinTimesheetBatch.getCompany().getDateFormat());
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
	public List<LundinTimesheetReportsForm> lundinDepartmentList(Long companyId) {
		List<LundinTimesheetReportsForm> lundinDepartmentList = new ArrayList<>();

		List<LundinDepartment> lundinDepartmentVOList = lundinDepartmentDAO
				.findByCondition(companyId);

		for (LundinDepartment lundinDepartment : lundinDepartmentVOList) {
			LundinTimesheetReportsForm otTimesheetReportsForm = new LundinTimesheetReportsForm();
			otTimesheetReportsForm.setDepartmentId(lundinDepartment
					.getDepartmentId());
			otTimesheetReportsForm.setDepartmentName(lundinDepartment
					.getDynamicFormFieldRefValue().getCode()
					+ " - "
					+ lundinDepartment.getDynamicFormFieldRefValue()
							.getDescription());
			lundinDepartmentList.add(otTimesheetReportsForm);
		}
		return lundinDepartmentList;

	}

	@Override
	public List<LundinTimesheetReportsForm> lundinBlockList(Long companyId) {
		List<LundinTimesheetReportsForm> lundinBlockList = new ArrayList<>();

		List<LundinBlock> lundinDepartmentVOList = lundinBlockDAO
				.findByCondition(companyId);

		for (LundinBlock lundinBlock : lundinDepartmentVOList) {
			LundinTimesheetReportsForm otTimesheetReportsForm = new LundinTimesheetReportsForm();
			otTimesheetReportsForm.setBlockId(lundinBlock.getBlockId());
			otTimesheetReportsForm.setBlockName(lundinBlock.getBlockName());
			otTimesheetReportsForm.setBlockCode(lundinBlock.getBlockCode());
			lundinBlockList.add(otTimesheetReportsForm);
		}
		return lundinBlockList;

	}

	@Override
	public List<LundinTimesheetReportsForm> lundinAFEList(Long companyId,
			Long blockId) {
		List<LundinTimesheetReportsForm> lundinBlockList = new ArrayList<>();
		LundinBlock lundinBlockVO = lundinBlockDAO.findById(blockId,companyId);
		if(lundinBlockVO==null){// Change
			return Collections.emptyList();
		}
		List<LundinAFE> lundinAFEVOList = lundinBlockVO.getLundinAfes();
		Collections.sort(lundinAFEVOList, new LundinAFEComp());
		for (LundinAFE lundinAFE : lundinAFEVOList) {
			LundinTimesheetReportsForm otTimesheetReportsForm = new LundinTimesheetReportsForm();
			otTimesheetReportsForm.setAfeId(lundinAFE.getAfeId());
			otTimesheetReportsForm.setAfeName(lundinAFE.getAfeName());
			otTimesheetReportsForm.setAfeCode(lundinAFE.getAfeCode());
			lundinBlockList.add(otTimesheetReportsForm);
		}
		return lundinBlockList;

	}

	@Override
	public LundinTimewritingReportDTO getOTBatchName(Long companyId,
			Long fromBatchId, Long toBatchId) {
		LundinTimewritingReportDTO lundinTimewritingReportDTO = new LundinTimewritingReportDTO();
		String otBatchName = "";
		TimesheetBatch fromOTBatch = lundinTimesheetBatchDAO
				.findById(fromBatchId);
		TimesheetBatch toOTBatch = lundinTimesheetBatchDAO.findById(toBatchId);
		if (fromOTBatch != null && toOTBatch != null) {
			if (fromBatchId.equals(toBatchId)) {
				otBatchName = fromOTBatch.getTimesheetBatchDesc().substring(0,
						3)
						+ "-"
						+ toOTBatch.getTimesheetBatchDesc().substring(0, 8);
			} else {
				otBatchName = fromOTBatch.getTimesheetBatchDesc().substring(0,
						3)
						+ "-"
						+ toOTBatch.getTimesheetBatchDesc().substring(0, 8);
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

	private Map<String, Integer> sortMapByValues(Map<String, Integer> map) {
		Map<String, Integer> newMap = new LinkedHashMap<String, Integer>();
		Set<Entry<String, Integer>> set = map.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
				set);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		for (Map.Entry<String, Integer> entry : list) {
			newMap.put(entry.getKey(), entry.getValue());
		}
		return newMap;
	}

	private HashMap<String, LundinTimewritingReportDTO> sortDepartmentMapByDisplayOrder(
			HashMap<String, LundinTimewritingReportDTO> deptCodeTechnicalTimewritingDTOMap) {
		HashMap<String, LundinTimewritingReportDTO> deptCodeTechTimewritingDTOFinalMap = new LinkedHashMap<String, LundinTimewritingReportDTO>();
		Map<String, Integer> keyWithValueDisplayOrderMap = new HashMap<String, Integer>();

		Set<String> keySet = deptCodeTechnicalTimewritingDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			LundinTimewritingReportDTO reportDTO = deptCodeTechnicalTimewritingDTOMap
					.get(key);
			int order = reportDTO.getDisplayOrder();
			keyWithValueDisplayOrderMap.put(key, order);
		}
		keyWithValueDisplayOrderMap = sortMapByValues(keyWithValueDisplayOrderMap);

		Set<String> keyWithValueDisplayOrderKeySet = keyWithValueDisplayOrderMap
				.keySet();
		for (Iterator<String> iterator = keyWithValueDisplayOrderKeySet
				.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			deptCodeTechTimewritingDTOFinalMap.put(key,
					deptCodeTechnicalTimewritingDTOMap.get(key));
		}
		return deptCodeTechTimewritingDTOFinalMap;
	}

	private int getTotalTimesheetManHours(Long employeeId,
			Timestamp fromStartDate, Timestamp toEndDate, String dateFormat) {
		Calendar calStart = new GregorianCalendar();
		Date startDate = DateUtils.stringToDate(
				DateUtils.timeStampToString(fromStartDate, dateFormat),
				dateFormat);
		calStart.setTime(startDate);

		Calendar calEnd = new GregorianCalendar();
		Date endDate = DateUtils.stringToDate(
				DateUtils.timeStampToString(toEndDate, dateFormat), dateFormat);
		calEnd.setTime(endDate);

		List<Date> holidaysDateList = new ArrayList<Date>();
		List<String> holidays = lundinTimesheetRequestLogic.getHolidaysFor(
				employeeId, startDate, endDate);
		for (String dateStr : holidays) {
			Date date = DateUtils.stringToDate(dateStr, "MM/dd/yyyy");
			holidaysDateList.add(date);
		}

		int manHoursCount = 0;
		while (startDate.before(endDate) || startDate.equals(endDate)) {
			int dayOfWeek = calStart.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek != 1 && dayOfWeek != 7
					&& !holidaysDateList.contains(startDate)) {
				manHoursCount++;
			}
			calStart.add(Calendar.DATE, 1);
			startDate = calStart.getTime();
		}
		return manHoursCount;
	}

	@Override
	public LundinTimesheetReportDTO genTimewritingAndCostAllocationReportExcelFile(
			Long companyId, Long employeeId,
			LundinTimesheetReportsForm lundinTimesheetReportsForm) {
		Company company = companyDAO.findById(companyId);
		LundinTimesheetReportDTO lundinTimesheetReportDTOs = new LundinTimesheetReportDTO();
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		String employeeIds = "";
		StringBuilder builder = new StringBuilder();
		for (BigInteger empId : employeeShortListDTO.getShortListEmployeeIds()) {
			builder = builder.append(String.valueOf(empId));
			builder = builder.append(",");

		}
		employeeIds = builder.toString();

		List<LundinTimewritingDeptReportDTO> reportDTOList = lundinTimesheetDAO
				.LundinTimewritingReportProc(
						companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						lundinTimesheetReportsForm.getToBatchId(),
						lundinTimesheetReportsForm.getBlockId(),
						lundinTimesheetReportsForm.getAfeId(),
						lundinTimesheetReportsForm.getEmployeeNumber(),
						lundinTimesheetReportsForm.isIncludeResignedEmployees(),
						employeeIds);

		Map<String, LundinTimewritingReportDTO> deptCodeDeptDTOMap = new LinkedHashMap<String, LundinTimewritingReportDTO>();
		Map<String, LundinTimewritingReportDTO> employeeDeptDTOMap = new LinkedHashMap<String, LundinTimewritingReportDTO>();
		HashMap<String, LundinTimewritingReportDTO> deptCodeTechnicalTimewritingDTOMap = new LinkedHashMap<String, LundinTimewritingReportDTO>();
		HashMap<String, LundinTimewritingReportDTO> deptCodeNonTechnicalTimewritingDTOMap = new LinkedHashMap<String, LundinTimewritingReportDTO>();
		HashMap<String, LinkedHashSet<String>> empListDepartmentWiseMap = new LinkedHashMap<String, LinkedHashSet<String>>();
		HashMap<String, List<String>> empListDepartmentWiseListMap = new LinkedHashMap<String, List<String>>();
		HashMap<String, Double> empTimesheetKeyValueMap = new LinkedHashMap<String, Double>();
		HashMap<String, Double> totalTechnicalManHoursValMap = new LinkedHashMap<String, Double>();
		Set<String> empColCountSet = new HashSet<String>();
		HashMap<String, Double> totalTechnicalManHoursValFinalMap = new LinkedHashMap<String, Double>();
		HashMap<String, Double> totalTechnicalManHoursWithEffecAllocationMap = new LinkedHashMap<String, Double>();
		List<LundinTimewritingDeptReportDTO> totalTechnicalManHoursWithEffecAllocDTOList = new ArrayList<LundinTimewritingDeptReportDTO>();
		List<LundinTimewritingDeptReportDTO> totalTechnicalManHoursDTOList = new ArrayList<LundinTimewritingDeptReportDTO>();
		List<LundinTimewritingReportDTO> nonTimesheetEmpDetailsList = new ArrayList<LundinTimewritingReportDTO>();
		// Set<String> allEmployeesSet = new HashSet<String>();

		// Get Lundin Departments From LundinDepartment Entity
		List<LundinDepartment> lundinDepartmentList = lundinDepartmentDAO
				.findByCondition(companyId);
		for (LundinDepartment lundinDepartmentVO : lundinDepartmentList) {
			if (lundinTimesheetReportsForm.getDepartmentId() != null
					&& !lundinTimesheetReportsForm.getDepartmentId().equals(
							lundinDepartmentVO.getDepartmentId())) {
				continue;
			}
			LundinTimewritingReportDTO lundinTimesheetReportDTO = new LundinTimewritingReportDTO();
			lundinTimesheetReportDTO.setDepartmentId(lundinDepartmentVO
					.getDepartmentId());
			lundinTimesheetReportDTO.setDisplayOrder(lundinDepartmentVO
					.getOrder());
			if (lundinDepartmentVO.getDefaultBlock() != null) {
				lundinTimesheetReportDTO.setDefaultBlockId(lundinDepartmentVO
						.getDefaultBlock().getBlockId());
				lundinTimesheetReportDTO.setDefaultBlockName(lundinDepartmentVO
						.getDefaultBlock().getBlockName());
				lundinTimesheetReportDTO.setDefaultBlockCode(lundinDepartmentVO
						.getDefaultBlock().getBlockCode());
				lundinTimesheetReportDTO
						.setDefaultBlockEffectiveAllocation(lundinDepartmentVO
								.getDefaultBlock().isEffectiveAllocation());
			}
			if (lundinDepartmentVO.getDefaultAFE() != null) {
				lundinTimesheetReportDTO.setDefaultAFEId(lundinDepartmentVO
						.getDefaultAFE().getAfeId());
				lundinTimesheetReportDTO.setDefaultAFEName(lundinDepartmentVO
						.getDefaultAFE().getAfeName());
			}

			if (lundinDepartmentVO.getDepartmentType() != null) {
				lundinTimesheetReportDTO.setDepartmentType(lundinDepartmentVO
						.getDepartmentType().getCodeDesc());
				// Add Department details as LundinTimewritingReportDTO and
				// key is Department Code In deptCodeDeptDTOMap
				deptCodeDeptDTOMap.put(lundinDepartmentVO
						.getDynamicFormFieldRefValue().getCode(),
						lundinTimesheetReportDTO);
			}
		}
		int sizeOfDepartmentList = 0;
		if (!lundinDepartmentList.isEmpty()) {
			sizeOfDepartmentList = lundinDepartmentList.size();
			if (sizeOfDepartmentList > 0) {
				LundinDepartment lundinDepartment = lundinDepartmentList
						.get(sizeOfDepartmentList - 1);
				lundinTimesheetReportDTOs
						.setNtofcDepartmentCode(lundinDepartment
								.getDynamicFormFieldRefValue().getCode());
				lundinTimesheetReportDTOs
						.setNtofcDepartmentName(lundinDepartment
								.getDynamicFormFieldRefValue().getDescription());
			}
		}

		TimesheetBatch fromTimsheetBatch = lundinTimesheetBatchDAO
				.findById(lundinTimesheetReportsForm.getFromBatchId());
		TimesheetBatch toTimsheetBatch = lundinTimesheetBatchDAO
				.findById(lundinTimesheetReportsForm.getToBatchId());

		LundinTimesheetPreference lundinOTPreferenceVO = lundinTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		String BLOCK_NAME_OF_COM_BLOCK = "";
		int empListMaxOccurLength = 0;
		if (lundinOTPreferenceVO != null
				&& lundinOTPreferenceVO.getDataDictionary() != null) {

			// Get Department Of Employees from Employee Information Details
			List<Object[]> deptObjectList = getLundinEmployeeDepartmentList(
					lundinOTPreferenceVO, companyId, company.getDateFormat(),
					employeeId);
			for (Object[] deptObject : deptObjectList) {
				if (deptObject[3] != null && deptObject[0] != null) {
					LundinTimewritingReportDTO lundinTimesheetReportDTO = deptCodeDeptDTOMap
							.get(String.valueOf(deptObject[0]));
					if (lundinTimesheetReportDTO == null) {
						continue;
					}
					lundinTimesheetReportDTO.setDepartmentName(String
							.valueOf(deptObject[1]));
					lundinTimesheetReportDTO.setDepartmentCode(String
							.valueOf(deptObject[0]));

					// Auto-Timewrite boolean Field
					if (deptObject[2] != null
							&& Boolean.valueOf(String.valueOf(deptObject[2]))) {
						// Add non-eTimesheet Employee details to
						// nonTimesheetEmpDetailsList(
						LundinTimewritingReportDTO timesheetReportDTO = new LundinTimewritingReportDTO();
						timesheetReportDTO.setAutoTimewrite(Boolean
								.valueOf(String.valueOf(deptObject[2])));
						timesheetReportDTO.setEmployeeNumber(String
								.valueOf(deptObject[3]));
						timesheetReportDTO.setFirstName(String
								.valueOf(deptObject[4]));
						timesheetReportDTO.setLastName(String
								.valueOf(deptObject[5]));
						timesheetReportDTO.setDepartmentName(String
								.valueOf(deptObject[1]));
						timesheetReportDTO.setDepartmentCode(String
								.valueOf(deptObject[0]));

						timesheetReportDTO
								.setDepartmentId(lundinTimesheetReportDTO
										.getDepartmentId());
						timesheetReportDTO
								.setDisplayOrder(lundinTimesheetReportDTO
										.getDisplayOrder());
						timesheetReportDTO
								.setDefaultBlockId(lundinTimesheetReportDTO
										.getDefaultBlockId());
						timesheetReportDTO
								.setDefaultBlockName(lundinTimesheetReportDTO
										.getDefaultBlockName());
						timesheetReportDTO
								.setDefaultBlockCode(lundinTimesheetReportDTO
										.getDefaultBlockCode());
						timesheetReportDTO
								.setDefaultBlockEffectiveAllocation(lundinTimesheetReportDTO
										.isDefaultBlockEffectiveAllocation());
						timesheetReportDTO
								.setDefaultAFEId(lundinTimesheetReportDTO
										.getDefaultAFEId());
						timesheetReportDTO
								.setDefaultAFEName(lundinTimesheetReportDTO
										.getDefaultAFEName());

						timesheetReportDTO
								.setDepartmentType(lundinTimesheetReportDTO
										.getDepartmentType());
						nonTimesheetEmpDetailsList.add(timesheetReportDTO);
					}

					// Add Department details as LundinTimewritingReportDTO and
					// key is Employee Number In employeeDeptDTOMap
					employeeDeptDTOMap.put(String.valueOf(deptObject[3]),
							lundinTimesheetReportDTO);
				}
			}

			// Get All employees of session company
			Map<String, Employee> empNumEmployeeMap = new HashMap<String, Employee>();
			List<Employee> employeeVOList = employeeDAO
					.findByCompany(companyId);
			for (Employee employee : employeeVOList) {
				// if (employee.isStatus()) {
				empNumEmployeeMap.put(employee.getEmployeeNumber(), employee);
				// }
			}

			// Add Non-ETimesheet Employee details to reportDTOList(this list is
			// filled by dbase procedure)
			List<BigInteger> shortListemployeeList = employeeShortListDTO
					.getShortListEmployeeIds();
			for (LundinTimewritingReportDTO nonTimesheetEmpDetailDTO : nonTimesheetEmpDetailsList) {
				if (nonTimesheetEmpDetailDTO.getDefaultBlockId() == null
						&& nonTimesheetEmpDetailDTO.getDefaultAFEId() == null) {
					continue;
				}
				if (!lundinTimesheetReportsForm.isIncludeResignedEmployees()
						&& !empNumEmployeeMap.get(
								nonTimesheetEmpDetailDTO.getEmployeeNumber())
								.isStatus()) {
					continue;
				}
				if (empNumEmployeeMap.get(
						nonTimesheetEmpDetailDTO.getEmployeeNumber())
						.getResignationDate() != null) {
					Date resignationDate = DateUtils.stringToDate(DateUtils
							.timeStampToString(
									empNumEmployeeMap.get(
											nonTimesheetEmpDetailDTO
													.getEmployeeNumber())
											.getResignationDate(), company
											.getDateFormat()), company
							.getDateFormat());
					Date startBatchDate = DateUtils.stringToDate(
							DateUtils.timeStampToString(
									fromTimsheetBatch.getStartDate(),
									company.getDateFormat()),
							company.getDateFormat());
					if (resignationDate.before(startBatchDate)) {
						continue;
					}
				}
				if (!shortListemployeeList.isEmpty()
						&& !shortListemployeeList.contains(new BigInteger(
								String.valueOf(empNumEmployeeMap.get(
										nonTimesheetEmpDetailDTO
												.getEmployeeNumber())
										.getEmployeeId())))) {
					continue;
				}
				LundinTimewritingDeptReportDTO lundinTimewritingDeptReportDTO = new LundinTimewritingDeptReportDTO();
				lundinTimewritingDeptReportDTO
						.setEmployeeNumber(nonTimesheetEmpDetailDTO
								.getEmployeeNumber());
				lundinTimewritingDeptReportDTO
						.setFirstName(nonTimesheetEmpDetailDTO.getFirstName());
				if (StringUtils.isNotBlank(nonTimesheetEmpDetailDTO
						.getLastName())
						&& !nonTimesheetEmpDetailDTO.getLastName()
								.equalsIgnoreCase("null")) {
					lundinTimewritingDeptReportDTO
							.setLastName(nonTimesheetEmpDetailDTO.getLastName());
				}

				lundinTimewritingDeptReportDTO
						.setAfeId(nonTimesheetEmpDetailDTO.getDefaultAFEId());
				lundinTimewritingDeptReportDTO
						.setAfeName(nonTimesheetEmpDetailDTO
								.getDefaultAFEName());
				lundinTimewritingDeptReportDTO
						.setBlockCode(nonTimesheetEmpDetailDTO
								.getDefaultBlockCode());
				lundinTimewritingDeptReportDTO
						.setBlockId(nonTimesheetEmpDetailDTO
								.getDefaultBlockId());
				lundinTimewritingDeptReportDTO
						.setBlockName(nonTimesheetEmpDetailDTO
								.getDefaultBlockName());
				lundinTimewritingDeptReportDTO
						.setBlockEffectiveAllocation(nonTimesheetEmpDetailDTO
								.isDefaultBlockEffectiveAllocation());
				// Start: Check Employee hire date and Resigned date lies
				// between batch
				// Dates
				Timestamp startBatchTimestamp = fromTimsheetBatch
						.getStartDate();
				Timestamp endBatchTimestamp = toTimsheetBatch.getEndDate();

				if (empNumEmployeeMap.get(
						nonTimesheetEmpDetailDTO.getEmployeeNumber())
						.getHireDate() != null) {
					Date hireDate = DateUtils.stringToDate(DateUtils
							.timeStampToString(
									empNumEmployeeMap.get(
											nonTimesheetEmpDetailDTO
													.getEmployeeNumber())
											.getHireDate(), company
											.getDateFormat()), company
							.getDateFormat());
					if (hireDate.after(fromTimsheetBatch.getStartDate())
							&& hireDate.before(toTimsheetBatch.getEndDate())) {
						startBatchTimestamp = empNumEmployeeMap.get(
								nonTimesheetEmpDetailDTO.getEmployeeNumber())
								.getHireDate();
					}
				}

				if (empNumEmployeeMap.get(
						nonTimesheetEmpDetailDTO.getEmployeeNumber())
						.getResignationDate() != null) {
					Date resignedDate = DateUtils.stringToDate(DateUtils
							.timeStampToString(
									empNumEmployeeMap.get(
											nonTimesheetEmpDetailDTO
													.getEmployeeNumber())
											.getResignationDate(), company
											.getDateFormat()), company
							.getDateFormat());
					if (resignedDate.after(fromTimsheetBatch.getStartDate())
							&& resignedDate
									.before(toTimsheetBatch.getEndDate())) {
						endBatchTimestamp = empNumEmployeeMap.get(
								nonTimesheetEmpDetailDTO.getEmployeeNumber())
								.getResignationDate();
					}
				}
				// End: Check Employee hire date and Resigned date lies
				// between batch
				// Dates
				lundinTimewritingDeptReportDTO.setValue(Double
						.valueOf(getTotalTimesheetManHours(
								empNumEmployeeMap.get(
										nonTimesheetEmpDetailDTO
												.getEmployeeNumber())
										.getEmployeeId(), startBatchTimestamp,
								endBatchTimestamp, company.getDateFormat())));

				reportDTOList.add(lundinTimewritingDeptReportDTO);
			}

			for (LundinTimewritingDeptReportDTO reportDTO : reportDTOList) {
				// Get LundinTimewritingReportDTO From employeeDeptDTOMap by
				// Employee Number
				LundinTimewritingReportDTO lundinTimesheetReportDTO = employeeDeptDTOMap
						.get(reportDTO.getEmployeeNumber());
				if (lundinTimesheetReportDTO == null) {
					continue;
				}
				// Get Block Name by Block Code "COM"
				if (!PayAsiaConstants.PAYASIA_LUNDIN_DEPARTMENT_TECHNICAL
						.equalsIgnoreCase(lundinTimesheetReportDTO
								.getDepartmentType())
						&& PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
								.equalsIgnoreCase(reportDTO.getBlockCode())) {
					BLOCK_NAME_OF_COM_BLOCK = reportDTO.getBlockName();
				}

				// Get and Add Block AFE Details List By Department
				List<LundinTimewritingDeptReportDTO> lundinTimewritingReportDTOList = lundinTimesheetReportDTO
						.getTimewritingDeptReportDTOList();
				if (lundinTimewritingReportDTOList == null) {
					lundinTimewritingReportDTOList = new ArrayList<LundinTimewritingDeptReportDTO>();
				}
				if (PayAsiaConstants.PAYASIA_LUNDIN_DEPARTMENT_TECHNICAL
						.equalsIgnoreCase(lundinTimesheetReportDTO
								.getDepartmentType())) {
					lundinTimewritingReportDTOList.add(reportDTO);
				}
				if (PayAsiaConstants.PAYASIA_LUNDIN_DEPARTMENT_NON_TECHNICAL
						.equalsIgnoreCase(lundinTimesheetReportDTO
								.getDepartmentType())) {
					lundinTimewritingReportDTOList.add(reportDTO);
				}

				lundinTimesheetReportDTO
						.setTimewritingDeptReportDTOList(lundinTimewritingReportDTOList);

				// Add Block AFE Details List per Department by Department type
				// i.e. Technical or Non-Technical
				if (PayAsiaConstants.PAYASIA_LUNDIN_DEPARTMENT_TECHNICAL
						.equalsIgnoreCase(lundinTimesheetReportDTO
								.getDepartmentType())) {
					// Department type i.e. Technical
					deptCodeTechnicalTimewritingDTOMap.put(
							lundinTimesheetReportDTO.getDepartmentCode(),
							lundinTimesheetReportDTO);
				}
				if (PayAsiaConstants.PAYASIA_LUNDIN_DEPARTMENT_NON_TECHNICAL
						.equalsIgnoreCase(lundinTimesheetReportDTO
								.getDepartmentType())) {
					// Department type i.e. Non-Technical
					deptCodeNonTechnicalTimewritingDTOMap.put(
							lundinTimesheetReportDTO.getDepartmentCode(),
							lundinTimesheetReportDTO);
				}

				// Add All Employees of Both technical and Non technical
				// allEmployeesSet.add(getEmployeeName(reportDTO.getFirstName(),
				// reportDTO.getLastName()));

				// Add Employee Name List per Department
				LinkedHashSet<String> employeeNameList = new LinkedHashSet<String>();
				if (empListDepartmentWiseMap
						.containsKey(lundinTimesheetReportDTO
								.getDepartmentCode())) {
					Set<String> existingEmployeeNumberList = empListDepartmentWiseMap
							.get(lundinTimesheetReportDTO.getDepartmentCode());
					employeeNameList.addAll(existingEmployeeNumberList);
					employeeNameList.add(getEmployeeName(
							reportDTO.getFirstName(), reportDTO.getLastName()));
					List<String> empNameList = new ArrayList<String>(
							employeeNameList);
					Collections.sort(empNameList, new StringBasedComparator());
					employeeNameList = new LinkedHashSet<String>(empNameList);
					empListDepartmentWiseMap.put(
							lundinTimesheetReportDTO.getDepartmentCode(),
							employeeNameList);
				} else {
					employeeNameList.add(getEmployeeName(
							reportDTO.getFirstName(), reportDTO.getLastName()));
					List<String> empNameList = new ArrayList<String>(
							employeeNameList);
					Collections.sort(empNameList, new StringBasedComparator());
					employeeNameList = new LinkedHashSet<String>(empNameList);
					empListDepartmentWiseMap.put(
							lundinTimesheetReportDTO.getDepartmentCode(),
							employeeNameList);
				}

				// Add Timesheet value per Department,Block,AFE,Employee Name
				String empTimesheetValueKey = lundinTimesheetReportDTO
						.getDepartmentCode()
						+ reportDTO.getBlockName()
						+ reportDTO.getAfeName()
						+ getEmployeeName(reportDTO.getFirstName(),
								reportDTO.getLastName());
				empTimesheetKeyValueMap.put(empTimesheetValueKey,
						reportDTO.getValue());

				// Add (Total Technical Man Hours) Timesheet Value per
				// Block,AFE,Employee Name
				if (PayAsiaConstants.PAYASIA_LUNDIN_DEPARTMENT_TECHNICAL
						.equalsIgnoreCase(lundinTimesheetReportDTO
								.getDepartmentType())) {
					String totalTechnicalManHoursKey = reportDTO.getBlockName()
							+ reportDTO.getAfeName()
							+ getEmployeeName(reportDTO.getFirstName(),
									reportDTO.getLastName());
					if (totalTechnicalManHoursValMap
							.containsKey(totalTechnicalManHoursKey)) {
						Double value = totalTechnicalManHoursValMap
								.get(totalTechnicalManHoursKey);
						totalTechnicalManHoursValMap.put(
								totalTechnicalManHoursKey,
								value + reportDTO.getValue());
					} else {
						totalTechnicalManHoursValMap
								.put(totalTechnicalManHoursKey,
										reportDTO.getValue());
					}
				}
			}

			// Segregate (Total Technical Man Hours) Employee List Per
			// Block,AFE,Employee Name Vertical Column as in Excel for technical
			// department area
			Map<String, List<String>> totalTechnicalEmpMap = new LinkedHashMap<String, List<String>>();
			Set<String> empListDeptKeySet = empListDepartmentWiseMap.keySet();
			for (Iterator<String> iterator = empListDeptKeySet.iterator(); iterator
					.hasNext();) {
				String key = iterator.next();
				Set<String> empSet = empListDepartmentWiseMap.get(key);
				int count = 1;
				for (String employee : empSet) {
					if (totalTechnicalEmpMap.containsKey("empColCount" + count)) {
						List<String> empList = totalTechnicalEmpMap
								.get("empColCount" + count);
						empList.add(employee);
						totalTechnicalEmpMap
								.put("empColCount" + count, empList);
					} else {
						List<String> empList = new ArrayList<String>();
						empList.add(employee);
						totalTechnicalEmpMap
								.put("empColCount" + count, empList);
					}
					count++;
				}
			}

			// Get maximum Employees size per Department
			Set<String> keySet = empListDepartmentWiseMap.keySet();
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = iterator.next();
				Set<String> empSet = empListDepartmentWiseMap.get(key);
				if (empListMaxOccurLength < empSet.size()) {
					empListMaxOccurLength = empSet.size();
				}
			}
			// empListMaxOccurLength = allEmployeesSet.size();
			// Add empty column to cover maximum size of employees per
			// Department
			Set<String> empListKeySet = empListDepartmentWiseMap.keySet();
			for (Iterator<String> iterator = empListKeySet.iterator(); iterator
					.hasNext();) {
				String key = iterator.next();
				List<String> empSet = new ArrayList<>(
						empListDepartmentWiseMap.get(key));
				int length = 0;
				// if (empSet.size() == 1) {
				// length = empListMaxOccurLength - empSet.size() + 1;
				// } else {
				length = empListMaxOccurLength - empSet.size();
				// }

				if (length > 0) {
					for (int count = 1; count <= length; count++) {
						empSet.add("");
					}
				}
				empListDepartmentWiseListMap.put(key, empSet);
			}
			// Remove Duplicate (Block And AFE) per Department
			Set<String> deptCodeDepartmentWiseKeySet = deptCodeTechnicalTimewritingDTOMap
					.keySet();
			for (Iterator<String> iterator = deptCodeDepartmentWiseKeySet
					.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				LundinTimewritingReportDTO timewritingDTO = deptCodeTechnicalTimewritingDTOMap
						.get(key);
				List<LundinTimewritingDeptReportDTO> deptReportDTOList = timewritingDTO
						.getTimewritingDeptReportDTOList();
				Set<LundinTimewritingDeptReportDTO> reportDTOSet = new LinkedHashSet<LundinTimewritingDeptReportDTO>(
						deptReportDTOList);
				deptReportDTOList = new ArrayList<LundinTimewritingDeptReportDTO>(
						reportDTOSet);
				timewritingDTO
						.setTimewritingDeptReportDTOList(deptReportDTOList);

			}
			// Remove Duplicate (Block And AFE) for Total Technical Man Hours
			Set<LundinTimewritingDeptReportDTO> reportDTOSet = new LinkedHashSet<LundinTimewritingDeptReportDTO>(
					reportDTOList);
			reportDTOList = new ArrayList<LundinTimewritingDeptReportDTO>(
					reportDTOSet);
			addTotalTechnicalBlockAFEList(reportDTOList,
					totalTechnicalManHoursDTOList, false,
					BLOCK_NAME_OF_COM_BLOCK);

			totalTechnicalManHoursValMap = getTotalTechnicalManHoursValMap(
					reportDTOList, totalTechnicalManHoursValMap,
					empColCountSet, totalTechnicalManHoursValFinalMap,
					totalTechnicalEmpMap, false, BLOCK_NAME_OF_COM_BLOCK);
			totalTechnicalManHoursWithEffecAllocationMap = getTotalTechnicalManHoursValMap(
					reportDTOList, totalTechnicalManHoursValMap,
					empColCountSet, totalTechnicalManHoursValFinalMap,
					totalTechnicalEmpMap, true, BLOCK_NAME_OF_COM_BLOCK);

			// (TECH TIMEWRITING TO MALAYSIAN OPERATIONS) Add Total Technical
			// Block AFE DTO list for (If Effective
			// Allocation is enabled)
			addTotalTechnicalBlockAFEList(reportDTOList,
					totalTechnicalManHoursWithEffecAllocDTOList, true,
					BLOCK_NAME_OF_COM_BLOCK);

			Set<String> deptCodeNonTechnicalKeySet = deptCodeNonTechnicalTimewritingDTOMap
					.keySet();

			// Add (TECH TIMEWRITING TO MALAYSIAN OPERATIONS) Blocks And AFEs to
			// Non-technical Departments
			for (Iterator<String> iterator = deptCodeNonTechnicalKeySet
					.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				LundinTimewritingReportDTO timewritingDTO = deptCodeNonTechnicalTimewritingDTOMap
						.get(key);
				List<LundinTimewritingDeptReportDTO> deptDTOList = new ArrayList<LundinTimewritingDeptReportDTO>();
				List<LundinTimewritingDeptReportDTO> timewritingDeptDTOList = timewritingDTO
						.getTimewritingDeptReportDTOList();
				deptDTOList.addAll(timewritingDeptDTOList);
				deptDTOList.addAll(totalTechnicalManHoursWithEffecAllocDTOList);
				Set<LundinTimewritingDeptReportDTO> deptReportDTOSet = new LinkedHashSet<LundinTimewritingDeptReportDTO>(
						deptDTOList);
				List<LundinTimewritingDeptReportDTO> deptReportDTOList = new ArrayList<LundinTimewritingDeptReportDTO>(
						deptReportDTOSet);
				Collections.sort(deptReportDTOList,
						new LundinTimewritingDeptComp());
				timewritingDTO
						.setTimewritingDeptReportDTOList(deptReportDTOList);
				deptCodeNonTechnicalTimewritingDTOMap.put(key, timewritingDTO);
			}
			// set Block = COM as first entry In Non-Technical Department
			for (Iterator<String> iterator = deptCodeNonTechnicalKeySet
					.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				LundinTimewritingReportDTO timewritingDTO = deptCodeNonTechnicalTimewritingDTOMap
						.get(key);
				List<LundinTimewritingDeptReportDTO> timewritingDeptDTOList = new ArrayList<LundinTimewritingDeptReportDTO>();
				Set<LundinTimewritingDeptReportDTO> deptReportDTOSet = new LinkedHashSet<LundinTimewritingDeptReportDTO>(
						timewritingDTO.getTimewritingDeptReportDTOList());
				List<LundinTimewritingDeptReportDTO> deptReportDTOList = new ArrayList<LundinTimewritingDeptReportDTO>(
						deptReportDTOSet);
				for (LundinTimewritingDeptReportDTO deptReportDTO : deptReportDTOList) {
					if (PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
							.equalsIgnoreCase(deptReportDTO.getBlockCode())
							&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED
									.equalsIgnoreCase(deptReportDTO
											.getAfeName()) || PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED
									.equalsIgnoreCase(deptReportDTO
											.getAfeName()))) {
						timewritingDeptDTOList.add(deptReportDTO);
					} else if (PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
							.equalsIgnoreCase(deptReportDTO.getBlockCode())
							&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM
									.equalsIgnoreCase(deptReportDTO
											.getAfeName()))) {
						timewritingDeptDTOList.add(deptReportDTO);
					}
				}
				for (LundinTimewritingDeptReportDTO deptReportDTO : deptReportDTOList) {
					if (PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
							.equalsIgnoreCase(deptReportDTO.getBlockCode())
							&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED
									.equalsIgnoreCase(deptReportDTO
											.getAfeName()) || PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED
									.equalsIgnoreCase(deptReportDTO
											.getAfeName()))) {

					} else if (PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
							.equalsIgnoreCase(deptReportDTO.getBlockCode())
							&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM
									.equalsIgnoreCase(deptReportDTO
											.getAfeName()))) {

					} else {
						timewritingDeptDTOList.add(deptReportDTO);
					}
				}
				timewritingDTO
						.setTimewritingDeptReportDTOList(timewritingDeptDTOList);
				deptCodeNonTechnicalTimewritingDTOMap.put(key, timewritingDTO);
			}
		}

		// Calculate Effective allocation for Non technical dept : Start
		Map<String, Double> malyasianOpernTechMap = new HashMap<String, Double>();
		for (LundinTimewritingDeptReportDTO lundinTimewritingDeptReportDTO : reportDTOList) {
			if (!lundinTimewritingDeptReportDTO.isBlockEffectiveAllocation()) {
				continue;
			}

			// if (BLOCK_NAME_OF_COM_BLOCK
			// .equalsIgnoreCase(lundinTimewritingDeptReportDTO
			// .getBlockName())
			// && (PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED
			// .equalsIgnoreCase(lundinTimewritingDeptReportDTO
			// .getAfeName()) ||
			// PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED
			// .equalsIgnoreCase(lundinTimewritingDeptReportDTO
			// .getAfeName()))) {
			// continue;
			// }
			Double value = 0.0d;
			Set<String> empColCounterSet = empColCountSet;
			for (String empColCount : empColCounterSet) {
				String mapKey = lundinTimewritingDeptReportDTO.getBlockName()
						+ lundinTimewritingDeptReportDTO.getAfeName()
						+ empColCount;
				if (totalTechnicalManHoursWithEffecAllocationMap.get(mapKey) == null) {
					continue;
				}
				value += totalTechnicalManHoursWithEffecAllocationMap
						.get(mapKey);
			}
			malyasianOpernTechMap.put(
					lundinTimewritingDeptReportDTO.getBlockName()
							+ lundinTimewritingDeptReportDTO.getAfeName(),
					value);
		}

		// Calculate total malaysian operation days values by Block , Afe
		Double totalMalyasianOpernValue = 0.0d;
		Set<String> malyasianOpernValTotalKeySet = malyasianOpernTechMap
				.keySet();
		for (Iterator<String> iterator = malyasianOpernValTotalKeySet
				.iterator(); iterator.hasNext();) {
			// Key is Block Name + AFE Name
			String key = iterator.next();
			if (malyasianOpernTechMap.get(key) != null) {
				totalMalyasianOpernValue += malyasianOpernTechMap.get(key);
			}
		}
		// Calculate total percentage malaysian operation days values by Block ,
		// Afe
		// divide By total days malaysian operation values by department
		Map<String, Double> malyasianOpernTechPercentMap = new HashMap<String, Double>();
		Set<String> malyasianOpernPercentKeySet = malyasianOpernTechMap
				.keySet();
		for (Iterator<String> iterator = malyasianOpernPercentKeySet.iterator(); iterator
				.hasNext();) {
			// Key is Block Name + AFE Name
			String key = iterator.next();
			if (malyasianOpernTechMap.get(key) != null) {
				Double percentage = (malyasianOpernTechMap.get(key) / totalMalyasianOpernValue) * 100;
				if (percentage.isNaN()) {
					malyasianOpernTechPercentMap.put(key, 0.0);
				} else {
					malyasianOpernTechPercentMap.put(key, percentage);
				}

			}
		}
		// Calculate Non technical Days
		Map<String, Double> nonTechnicalDaysMap = new HashMap<String, Double>();
		Map<String, Double> nonTechnicalDaysByBlckAfeMap = new HashMap<String, Double>();
		Map<String, Double> nonTechnicalBlockComPercentMap = new HashMap<String, Double>();
		Map<String, Double> nonTechTotalDaysByDeptMap = new HashMap<String, Double>();
		Set<String> deptCodeNonTechnical1KeySet = deptCodeNonTechnicalTimewritingDTOMap
				.keySet();
		for (Iterator<String> iterator = deptCodeNonTechnical1KeySet.iterator(); iterator
				.hasNext();) {
			// Key is Department Code
			String key = iterator.next();
			LundinTimewritingReportDTO timewritingDTO = deptCodeNonTechnicalTimewritingDTOMap
					.get(key);
			List<LundinTimewritingDeptReportDTO> deptReportDTOList = timewritingDTO
					.getTimewritingDeptReportDTOList();
			// Calculate Non-Technical total days values by Department ,Block ,
			// Afe
			for (LundinTimewritingDeptReportDTO deptReportDTO : deptReportDTOList) {
				Double value = 0.0d;
				Set<String> employeeNameList = empListDepartmentWiseMap
						.get(key);
				for (String empName : employeeNameList) {
					String empTimesheetKey = key + deptReportDTO.getBlockName()
							+ deptReportDTO.getAfeName() + empName;
					if (empTimesheetKeyValueMap.get(empTimesheetKey) != null) {
						value += empTimesheetKeyValueMap.get(empTimesheetKey);
					}
				}
				nonTechnicalDaysMap.put(key + deptReportDTO.getBlockName()
						+ deptReportDTO.getAfeName(), value);
				nonTechnicalDaysByBlckAfeMap.put(
						key + deptReportDTO.getBlockName()
								+ deptReportDTO.getAfeName(), value);
			}

			// calculate nonTechnical Total Days Value per department
			Double nonTechnTotalDaysByDeptValue = 0.0d;
			for (LundinTimewritingDeptReportDTO deptReportDTO : deptReportDTOList) {
				if (nonTechnicalDaysMap.get(key + deptReportDTO.getBlockName()
						+ deptReportDTO.getAfeName()) != null) {
					nonTechnTotalDaysByDeptValue += nonTechnicalDaysMap.get(key
							+ deptReportDTO.getBlockName()
							+ deptReportDTO.getAfeName());
				}
			}
			nonTechTotalDaysByDeptMap.put(key, nonTechnTotalDaysByDeptValue);
		}

		// calculate nonTechnical Total Days Value percentage by department
		// ,block,afe
		for (Iterator<String> iterator = deptCodeNonTechnical1KeySet.iterator(); iterator
				.hasNext();) {
			// Key is Department Code
			String key = iterator.next();
			Set<String> nonTechnicalDaysPercentKeySet = nonTechnicalDaysMap
					.keySet();
			for (Iterator<String> iter = nonTechnicalDaysPercentKeySet
					.iterator(); iter.hasNext();) {
				// Key is (Department Code+Block Name+AFE Name)
				String key1 = iter.next();
				if (nonTechnicalDaysMap.get(key1) != null
						&& key1.startsWith(key)) {
					Double percentage = (nonTechnicalDaysMap.get(key1) / nonTechTotalDaysByDeptMap
							.get(key)) * 100;
					nonTechnicalDaysMap.put(key1, percentage);
				}
			}
		}

		// Segregate Non -Technical block com percentage per department
		Set<String> deptCodeNonTechnical2KeySet = deptCodeNonTechnicalTimewritingDTOMap
				.keySet();
		for (Iterator<String> iterator = deptCodeNonTechnical2KeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Department Code)
			String key = iterator.next();
			Double value = nonTechnicalDaysMap.get(key
					+ BLOCK_NAME_OF_COM_BLOCK
					+ PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED);
			if (value == null) {
				value = nonTechnicalDaysMap.get(key + BLOCK_NAME_OF_COM_BLOCK
						+ PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM);
			}
			if (value != null) {
				nonTechnicalBlockComPercentMap.put(key, value);
			}
		}

		// calculate Total Non-Technical effective allocation percentage
		Map<String, String> nonTechEffecAllocByDeptMap = new HashMap<String, String>();
		Set<String> deptCodeNonTechnical3KeySet = deptCodeNonTechnicalTimewritingDTOMap
				.keySet();
		for (Iterator<String> iterator = deptCodeNonTechnical3KeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Department Code)
			String key = iterator.next();
			LundinTimewritingReportDTO timewritingDTO = deptCodeNonTechnicalTimewritingDTOMap
					.get(key);
			List<LundinTimewritingDeptReportDTO> deptReportDTOList = timewritingDTO
					.getTimewritingDeptReportDTOList();

			for (LundinTimewritingDeptReportDTO deptReportDTO : deptReportDTOList) {
				Double nontechBlockComValue = nonTechnicalBlockComPercentMap
						.get(key);
				Double malyasianOpernPerValue = malyasianOpernTechPercentMap
						.get(deptReportDTO.getBlockName()
								+ deptReportDTO.getAfeName());
				if (nontechBlockComValue != null
						&& malyasianOpernPerValue != null) {
					malyasianOpernPerValue = (malyasianOpernPerValue * nontechBlockComValue) / 100;
					if (malyasianOpernPerValue != null) {
						// nonTechEffecAllocByDeptMap
						// .put(key + deptReportDTO.getBlockName()
						// + deptReportDTO.getAfeName(),
						// String.valueOf(Math
						// .round(malyasianOpernPerValue * 100.0) / 100.0));
						nonTechEffecAllocByDeptMap.put(
								key + deptReportDTO.getBlockName()
										+ deptReportDTO.getAfeName(),
								String.valueOf(malyasianOpernPerValue));
					}
				}
				if (BLOCK_NAME_OF_COM_BLOCK.equalsIgnoreCase(deptReportDTO
						.getBlockName())
						&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED
								.equalsIgnoreCase(deptReportDTO.getAfeName()) || PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED
								.equalsIgnoreCase(deptReportDTO.getAfeName()))) {
					nonTechEffecAllocByDeptMap.put(
							key + deptReportDTO.getBlockName()
									+ deptReportDTO.getAfeName(), " ");
				} else if (BLOCK_NAME_OF_COM_BLOCK
						.equalsIgnoreCase(deptReportDTO.getBlockName())
						&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM
								.equalsIgnoreCase(deptReportDTO.getAfeName()))) {
					nonTechEffecAllocByDeptMap.put(
							key + deptReportDTO.getBlockName()
									+ deptReportDTO.getAfeName(), " ");
				}
			}
		}

		// calculate Total Non-Technical Final Effective allocation percentage
		Map<String, String> nonTechFinalEffecAllocByDeptMap = new HashMap<String, String>();
		for (Iterator<String> iterator = deptCodeNonTechnical3KeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Department Code)
			String key = iterator.next();
			LundinTimewritingReportDTO timewritingDTO = deptCodeNonTechnicalTimewritingDTOMap
					.get(key);
			List<LundinTimewritingDeptReportDTO> deptReportDTOList = timewritingDTO
					.getTimewritingDeptReportDTOList();
			for (LundinTimewritingDeptReportDTO deptReportDTO : deptReportDTOList) {
				Double nonTechDaysPercentvalue = nonTechnicalDaysMap.get(key
						+ deptReportDTO.getBlockName()
						+ deptReportDTO.getAfeName());
				Double nonTechEffecAllocvalue = 0.0d;
				if (StringUtils.isNotBlank(nonTechEffecAllocByDeptMap.get(key
						+ deptReportDTO.getBlockName()
						+ deptReportDTO.getAfeName()))) {
					nonTechEffecAllocvalue = Double
							.parseDouble(nonTechEffecAllocByDeptMap.get(key
									+ deptReportDTO.getBlockName()
									+ deptReportDTO.getAfeName()));
				}
				if (nonTechDaysPercentvalue != null
						&& nonTechEffecAllocvalue != null) {
					nonTechEffecAllocvalue = nonTechEffecAllocvalue
							+ nonTechDaysPercentvalue;
					// nonTechFinalEffecAllocByDeptMap
					// .put(key + deptReportDTO.getBlockName()
					// + deptReportDTO.getAfeName(),
					// String.valueOf(Math
					// .round(nonTechEffecAllocvalue * 100.0) / 100.0));
					nonTechFinalEffecAllocByDeptMap.put(
							key + deptReportDTO.getBlockName()
									+ deptReportDTO.getAfeName(),
							String.valueOf(nonTechEffecAllocvalue));
				}
				if (nonTechDaysPercentvalue == null) {
					nonTechFinalEffecAllocByDeptMap.put(
							key + deptReportDTO.getBlockName()
									+ deptReportDTO.getAfeName(),
							String.valueOf(nonTechEffecAllocvalue));
					// nonTechFinalEffecAllocByDeptMap
					// .put(key + deptReportDTO.getBlockName()
					// + deptReportDTO.getAfeName(),
					// String.valueOf(Math
					// .round(nonTechEffecAllocvalue * 100.0) / 100.0));
				}
				if (nonTechEffecAllocvalue == null) {
					// nonTechFinalEffecAllocByDeptMap
					// .put(key + deptReportDTO.getBlockName()
					// + deptReportDTO.getAfeName(),
					// String.valueOf(Math
					// .round(nonTechDaysPercentvalue * 100.0) / 100.0));
					nonTechFinalEffecAllocByDeptMap.put(
							key + deptReportDTO.getBlockName()
									+ deptReportDTO.getAfeName(),
							String.valueOf(nonTechDaysPercentvalue));
				}
				if (BLOCK_NAME_OF_COM_BLOCK.equalsIgnoreCase(deptReportDTO
						.getBlockName())
						&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED
								.equalsIgnoreCase(deptReportDTO.getAfeName()) || PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED
								.equalsIgnoreCase(deptReportDTO.getAfeName()))) {
					nonTechFinalEffecAllocByDeptMap.put(
							key + deptReportDTO.getBlockName()
									+ deptReportDTO.getAfeName(), " ");
				} else if (BLOCK_NAME_OF_COM_BLOCK
						.equalsIgnoreCase(deptReportDTO.getBlockName())
						&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM
								.equalsIgnoreCase(deptReportDTO.getAfeName()))) {
					nonTechFinalEffecAllocByDeptMap.put(
							key + deptReportDTO.getBlockName()
									+ deptReportDTO.getAfeName(), " ");
				}
			}
		}
		Map<String, String> nonTechTotalFinalEffecAllocMap = new HashMap<String, String>();
		for (Iterator<String> iterator = deptCodeNonTechnical3KeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Department Code)
			String key = iterator.next();
			LundinTimewritingReportDTO timewritingDTO = deptCodeNonTechnicalTimewritingDTOMap
					.get(key);
			List<LundinTimewritingDeptReportDTO> deptReportDTOList = timewritingDTO
					.getTimewritingDeptReportDTOList();
			Double nonTechTotalFinalEffecAllocvalue = null;
			for (LundinTimewritingDeptReportDTO deptReportDTO : deptReportDTOList) {
				if (nonTechTotalFinalEffecAllocvalue == null) {
					nonTechTotalFinalEffecAllocvalue = 0.0d;
				}
				if (StringUtils.isNotBlank(nonTechFinalEffecAllocByDeptMap
						.get(key + deptReportDTO.getBlockName()
								+ deptReportDTO.getAfeName()))) {
					nonTechTotalFinalEffecAllocvalue = nonTechTotalFinalEffecAllocvalue
							+ Double.parseDouble(nonTechFinalEffecAllocByDeptMap
									.get(key + deptReportDTO.getBlockName()
											+ deptReportDTO.getAfeName()));
				}
			}
			if (nonTechTotalFinalEffecAllocvalue != null) {
				// nonTechTotalFinalEffecAllocMap
				// .put(key,
				// String.valueOf(Math
				// .round(nonTechTotalFinalEffecAllocvalue * 100.0) / 100.0) +
				// '%');
				nonTechTotalFinalEffecAllocMap
						.put(key,
								String.valueOf(decimalFmt
										.format(nonTechTotalFinalEffecAllocvalue)) + '%');

			}
		}
		Set<String> nonTechFinalEffecAllocByDeptMapKeySet = nonTechFinalEffecAllocByDeptMap
				.keySet();
		for (Iterator<String> iterator = nonTechFinalEffecAllocByDeptMapKeySet
				.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String value = nonTechFinalEffecAllocByDeptMap.get(key);
			if (StringUtils.isNotBlank(value)) {
				nonTechFinalEffecAllocByDeptMap
						.put(key,
								decimalFmt.format((Math.round(Double
										.valueOf(value) * 100.0) / 100.0))
										+ "%");
			}
		}

		Set<String> nonTechEffecAllocByDeptMapKeySet = nonTechEffecAllocByDeptMap
				.keySet();
		for (Iterator<String> iterator = nonTechEffecAllocByDeptMapKeySet
				.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String value = nonTechEffecAllocByDeptMap.get(key);
			if (StringUtils.isNotBlank(value)) {
				nonTechEffecAllocByDeptMap
						.put(key,
								decimalFmt.format((Math.round(Double
										.valueOf(value) * 100.0) / 100.0))
										+ "%");
			}
		}

		// NTOFC
		// set Block = COM as first entry In NTOFC Department
		List<LundinTimewritingDeptReportDTO> timewritingNTOFCDTOList = new ArrayList<LundinTimewritingDeptReportDTO>();
		for (LundinTimewritingDeptReportDTO lundinTimewritingDeptReportDTO : reportDTOList) {

			if (PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
					.equalsIgnoreCase(lundinTimewritingDeptReportDTO
							.getBlockCode())
					&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED
							.equalsIgnoreCase(lundinTimewritingDeptReportDTO
									.getAfeName()) || PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED
							.equalsIgnoreCase(lundinTimewritingDeptReportDTO
									.getAfeName()))) {
				timewritingNTOFCDTOList.add(lundinTimewritingDeptReportDTO);
			} else if (PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
					.equalsIgnoreCase(lundinTimewritingDeptReportDTO
							.getBlockCode())
					&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM
							.equalsIgnoreCase(lundinTimewritingDeptReportDTO
									.getAfeName()))) {
				timewritingNTOFCDTOList.add(lundinTimewritingDeptReportDTO);
			}
		}
		for (LundinTimewritingDeptReportDTO deptReportDTO : reportDTOList) {
			if (PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
					.equalsIgnoreCase(deptReportDTO.getBlockCode())
					&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED
							.equalsIgnoreCase(deptReportDTO.getAfeName()) || PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED
							.equalsIgnoreCase(deptReportDTO.getAfeName()))) {

			} else if (PayAsiaConstants.PAYASIA_LUNDIN_BLOCK_COM
					.equalsIgnoreCase(deptReportDTO.getBlockCode())
					&& (PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM
							.equalsIgnoreCase(deptReportDTO.getAfeName()))) {

			} else {
				timewritingNTOFCDTOList.add(deptReportDTO);
			}
		}
		// Calculate Sum of all the units for the specific Block or AFE from
		// Technical Deparments
		Map<String, Double> ntofcTotalDaysValMap = new HashMap<String, Double>();
		Map<String, Double> ntofcTotalDaysPercentValMap = new HashMap<String, Double>();
		for (LundinTimewritingDeptReportDTO lundinTimewritingReportDTO : timewritingNTOFCDTOList) {
			for (String emp : empColCountSet) {
				Double value = totalTechnicalManHoursValMap
						.get(lundinTimewritingReportDTO.getBlockName()
								+ lundinTimewritingReportDTO.getAfeName() + emp);
				if (value == null) {
					continue;
				}
				if (ntofcTotalDaysValMap.containsKey(lundinTimewritingReportDTO
						.getBlockName()
						+ lundinTimewritingReportDTO.getAfeName())) {
					Double mapValue = ntofcTotalDaysValMap
							.get(lundinTimewritingReportDTO.getBlockName()
									+ lundinTimewritingReportDTO.getAfeName());
					if (mapValue != null) {
						ntofcTotalDaysValMap
								.put(lundinTimewritingReportDTO.getBlockName()
										+ lundinTimewritingReportDTO
												.getAfeName(), mapValue + value);
					}

				} else {
					ntofcTotalDaysValMap.put(
							lundinTimewritingReportDTO.getBlockName()
									+ lundinTimewritingReportDTO.getAfeName(),
							value);
				}
			}
		}
		// Calculate Sum of all the units for the specific Block or AFE from
		// Non-Technical Deparments
		for (Iterator<String> iterator = deptCodeNonTechnical3KeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Department Code)
			String key = iterator.next();
			for (LundinTimewritingDeptReportDTO lundinTimewritingReportDTO : timewritingNTOFCDTOList) {
				Double value = nonTechnicalDaysByBlckAfeMap.get(key
						+ lundinTimewritingReportDTO.getBlockName()
						+ lundinTimewritingReportDTO.getAfeName());
				if (value == null) {
					continue;
				}
				if (ntofcTotalDaysValMap.containsKey(lundinTimewritingReportDTO
						.getBlockName()
						+ lundinTimewritingReportDTO.getAfeName())) {
					Double mapValue = ntofcTotalDaysValMap
							.get(lundinTimewritingReportDTO.getBlockName()
									+ lundinTimewritingReportDTO.getAfeName());
					if (mapValue != null) {
						ntofcTotalDaysValMap
								.put(lundinTimewritingReportDTO.getBlockName()
										+ lundinTimewritingReportDTO
												.getAfeName(), mapValue + value);
					}

				} else {
					ntofcTotalDaysValMap.put(
							lundinTimewritingReportDTO.getBlockName()
									+ lundinTimewritingReportDTO.getAfeName(),
							value);
				}
			}
		}

		// calculate NTOFC Total Days Value per Block and AFE
		Double ntofcDaysTotal = 0.0d;
		Set<String> ntofcTotalDaysValMapKeySet = ntofcTotalDaysValMap.keySet();
		for (Iterator<String> iterator = ntofcTotalDaysValMapKeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Block Name + AFE Name)
			String key = iterator.next();
			if (ntofcTotalDaysValMap.get(key) != null) {
				ntofcDaysTotal += ntofcTotalDaysValMap.get(key);
			}
		}

		for (Iterator<String> iterator = ntofcTotalDaysValMapKeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Block Name + AFE Name)
			String key = iterator.next();
			if (ntofcTotalDaysValMap.get(key) != null) {
				Double percentVal = (ntofcTotalDaysValMap.get(key) / ntofcDaysTotal) * 100;
				ntofcTotalDaysPercentValMap.put(key, percentVal);
			}
		}
		// calculate NTOFC Total Days Percentage per Block and AFE
		Double ntofcTotalDaysBlockComPercent = 0.0d;
		Set<String> ntofcTotalDaysPercentValMapKeySet = ntofcTotalDaysPercentValMap
				.keySet();
		for (Iterator<String> iterator = ntofcTotalDaysPercentValMapKeySet
				.iterator(); iterator.hasNext();) {
			// Key is (Block Name + AFE Name)
			String key = iterator.next();
			if ((BLOCK_NAME_OF_COM_BLOCK + PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED)
					.equals(key)) {
				ntofcTotalDaysBlockComPercent = ntofcTotalDaysPercentValMap
						.get(key);
			} else if ((BLOCK_NAME_OF_COM_BLOCK + PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM)
					.equals(key)) {
				ntofcTotalDaysBlockComPercent = ntofcTotalDaysPercentValMap
						.get(key);
			}
		}
		// calculate NTOFC Effective Allocation Percentage per Block and
		// AFE
		Map<String, String> ntofcEffecAllocMap = new HashMap<String, String>();
		for (Iterator<String> iterator = ntofcTotalDaysValMapKeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Block Name + AFE Name)
			String key = iterator.next();
			Double malyasianOpernPerValue = malyasianOpernTechPercentMap
					.get(key);
			if (malyasianOpernPerValue != null) {
				malyasianOpernPerValue = (malyasianOpernPerValue * ntofcTotalDaysBlockComPercent) / 100;
				// ntofcEffecAllocMap.put(key, String.valueOf(Math
				// .round(malyasianOpernPerValue * 100.0) / 100.0));
				ntofcEffecAllocMap.put(key,
						String.valueOf(malyasianOpernPerValue));
			}
			if ((BLOCK_NAME_OF_COM_BLOCK + PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED)
					.equals(key)) {
				ntofcEffecAllocMap.put(key, "");
			} else if ((BLOCK_NAME_OF_COM_BLOCK + PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM)
					.equals(key)) {
				ntofcEffecAllocMap.put(key, "");
			}
		}
		// calculate NTOFC Final Effective Allocation Percentage per Block and
		// AFE
		Map<String, String> ntofcFinalEffecAllocMap = new HashMap<String, String>();
		for (Iterator<String> iterator = ntofcTotalDaysValMapKeySet.iterator(); iterator
				.hasNext();) {
			// Key is (Block Name + AFE Name)
			String key = iterator.next();
			Double ntofcTotalDaysPercent = ntofcTotalDaysPercentValMap.get(key);
			Double ntofcEffecAllocPercent = 0.0d;
			if (StringUtils.isNotBlank(ntofcEffecAllocMap.get(key))) {
				ntofcEffecAllocPercent = Double.valueOf(ntofcEffecAllocMap
						.get(key));
				// ntofcFinalEffecAllocMap.put(key, String.valueOf(Math
				// .round(ntofcEffecAllocPercent * 100.0) / 100.0));
				ntofcFinalEffecAllocMap.put(key,
						String.valueOf(ntofcEffecAllocPercent));
			}
			if (ntofcTotalDaysPercent != null) {
				ntofcEffecAllocPercent = ntofcEffecAllocPercent
						+ ntofcTotalDaysPercent;
				// ntofcFinalEffecAllocMap.put(key, String.valueOf(Math
				// .round(ntofcEffecAllocPercent * 100.0) / 100.0));
				ntofcFinalEffecAllocMap.put(key,
						String.valueOf(ntofcEffecAllocPercent));
			}
			if ((BLOCK_NAME_OF_COM_BLOCK + PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYSED)
					.equals(key)) {
				ntofcFinalEffecAllocMap.put(key, "");
			} else if ((BLOCK_NAME_OF_COM_BLOCK + PayAsiaConstants.PAYASIA_LUNDIN_AFE_COM)
					.equals(key)) {
				ntofcFinalEffecAllocMap.put(key, "");
			}
		}

		Set<String> ntofcFinalEffecAllocMapKeySet = ntofcFinalEffecAllocMap
				.keySet();
		Double ntofcFinalEffecAllocTotal = null;
		for (Iterator<String> iterator = ntofcFinalEffecAllocMapKeySet
				.iterator(); iterator.hasNext();) {
			// Key is (Block Name + AFE Name)
			String key = iterator.next();

			if (StringUtils.isNotBlank(ntofcFinalEffecAllocMap.get(key))) {
				if (ntofcFinalEffecAllocTotal == null) {
					ntofcFinalEffecAllocTotal = 0.0d;
				}
				ntofcFinalEffecAllocTotal += Double
						.valueOf(ntofcFinalEffecAllocMap.get(key));
			}
		}
		if (ntofcFinalEffecAllocTotal != null) {
			lundinTimesheetReportDTOs.setNtofcFinalEffecAllocTotal(String
					.valueOf(decimalFmt.format(ntofcFinalEffecAllocTotal))
					+ "%");
		}

		for (Iterator<String> iterator = ntofcFinalEffecAllocMapKeySet
				.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String value = ntofcFinalEffecAllocMap.get(key);
			if (StringUtils.isNotBlank(value)) {
				ntofcFinalEffecAllocMap
						.put(key,
								decimalFmt.format((Math.round(Double
										.valueOf(value) * 100.0) / 100.0))
										+ "%");
			}
		}

		Set<String> ntofcEffecAllocMapKeySet = ntofcEffecAllocMap.keySet();
		for (Iterator<String> iterator = ntofcEffecAllocMapKeySet.iterator(); iterator
				.hasNext();) {
			String key = iterator.next();
			String value = ntofcEffecAllocMap.get(key);
			if (StringUtils.isNotBlank(value)) {
				ntofcEffecAllocMap
						.put(key,
								decimalFmt.format((Math.round(Double
										.valueOf(value) * 100.0) / 100.0))
										+ "%");
			}
		}

		// Add Empty String for Total Technical ManHours Employee Vertical
		// Column
		List<String> empColCountList = new ArrayList<String>(empColCountSet);
		Collections.sort(empColCountList, new StringBasedComparator());
		int empColCountLength = empListMaxOccurLength - empColCountList.size();
		// if (empColCountList.size() == 1) {
		// empColCountLength = empListMaxOccurLength - empColCountList.size()
		// + 1;
		// }
		if (empColCountLength > 0) {
			for (int count = 1; count <= empColCountLength; count++) {
				empColCountList.add("");
			}
		}

		lundinTimesheetReportDTOs.setNtofcEffecAllocMap(ntofcEffecAllocMap);
		lundinTimesheetReportDTOs
				.setNtofcFinalEffecAllocMap(ntofcFinalEffecAllocMap);
		lundinTimesheetReportDTOs
				.setDeptCodeTechnicalTimewritingDTOMap(sortDepartmentMapByDisplayOrder(deptCodeTechnicalTimewritingDTOMap));
		lundinTimesheetReportDTOs
				.setDeptCodeNonTechnicalTimewritingDTOMap(sortDepartmentMapByDisplayOrder(deptCodeNonTechnicalTimewritingDTOMap));
		lundinTimesheetReportDTOs
				.setEmpListDepartmentWiseMap(empListDepartmentWiseListMap);
		lundinTimesheetReportDTOs
				.setEmpTimesheetKeyValueMap(empTimesheetKeyValueMap);
		lundinTimesheetReportDTOs
				.setTotalTechnicalManHoursValMap(totalTechnicalManHoursValFinalMap);
		lundinTimesheetReportDTOs
				.setTotalTechnicalManHoursWithEffecAllocMap(totalTechnicalManHoursWithEffecAllocationMap);
		lundinTimesheetReportDTOs.setEmployeeColCountList(empColCountList);
		lundinTimesheetReportDTOs
				.setTotalTechnicalManHoursDTOList(totalTechnicalManHoursDTOList);
		lundinTimesheetReportDTOs
				.setTotalTechnicalManHoursWithEffecAllocDTOList(totalTechnicalManHoursWithEffecAllocDTOList);
		lundinTimesheetReportDTOs
				.setNonTechEffecAllocByDeptMap(nonTechEffecAllocByDeptMap);
		lundinTimesheetReportDTOs
				.setNonTechFinalEffecAllocByDeptMap(nonTechFinalEffecAllocByDeptMap);
		lundinTimesheetReportDTOs
				.setNonTechTotalFinalEffecAllocMap(nonTechTotalFinalEffecAllocMap);
		lundinTimesheetReportDTOs
				.setMalyasianOpernTechMap(malyasianOpernTechMap);
		lundinTimesheetReportDTOs
				.setBlockAfeNTOFCDTOList(timewritingNTOFCDTOList);
		lundinTimesheetReportDTOs.setNtofcTotalDaysValMap(ntofcTotalDaysValMap);
		return lundinTimesheetReportDTOs;
	}

	// Add Total Technical Block AFE DTO list for (If Effective
	// Allocation is enabled)
	private void addTotalTechnicalBlockAFEList(
			List<LundinTimewritingDeptReportDTO> reportDTOList,
			List<LundinTimewritingDeptReportDTO> totalTechnicalManHoursDTOList,
			boolean withEffectiveAllocation, String Block_Name_Of_COM_Block) {
		for (LundinTimewritingDeptReportDTO lundinTimewritingDeptReportDTO : reportDTOList) {
			if (withEffectiveAllocation) {
				if (lundinTimewritingDeptReportDTO.isBlockEffectiveAllocation()) {
					totalTechnicalManHoursDTOList
							.add(lundinTimewritingDeptReportDTO);
				}
			} else {
				totalTechnicalManHoursDTOList
						.add(lundinTimewritingDeptReportDTO);
			}
		}
	}

	private HashMap<String, Double> getTotalTechnicalManHoursValMap(
			List<LundinTimewritingDeptReportDTO> reportDTOList,
			HashMap<String, Double> totalTechnicalManHoursValMap,
			Set<String> empColCountSet,
			HashMap<String, Double> totalTechnicalManHoursValFinalMap,
			Map<String, List<String>> totalTechnicalEmpMap,
			boolean withEffectiveAllocation, String Block_Name_Of_COM_Block) {
		for (LundinTimewritingDeptReportDTO lundinTimewritingDeptReportDTO : reportDTOList) {
			if (withEffectiveAllocation
					&& !lundinTimewritingDeptReportDTO
							.isBlockEffectiveAllocation()) {
				continue;
			}

			Set<String> totalTechnicalKeySet = totalTechnicalEmpMap.keySet();
			for (Iterator<String> iterator = totalTechnicalKeySet.iterator(); iterator
					.hasNext();) {
				String key = iterator.next();
				List<String> employeeNameList = totalTechnicalEmpMap.get(key);
				for (String empName : employeeNameList) {
					Double value = totalTechnicalManHoursValMap
							.get(lundinTimewritingDeptReportDTO.getBlockName()
									+ lundinTimewritingDeptReportDTO
											.getAfeName() + empName);
					if (value == null) {
						continue;
					}
					empColCountSet.add(key);

					if (totalTechnicalManHoursValFinalMap
							.containsKey(lundinTimewritingDeptReportDTO
									.getBlockName()
									+ lundinTimewritingDeptReportDTO
											.getAfeName() + key)) {
						Double mapValue = totalTechnicalManHoursValFinalMap
								.get(lundinTimewritingDeptReportDTO
										.getBlockName()
										+ lundinTimewritingDeptReportDTO
												.getAfeName() + key);
						totalTechnicalManHoursValFinalMap.put(
								lundinTimewritingDeptReportDTO.getBlockName()
										+ lundinTimewritingDeptReportDTO
												.getAfeName() + key, mapValue
										+ value);
					} else {
						totalTechnicalManHoursValFinalMap.put(
								lundinTimewritingDeptReportDTO.getBlockName()
										+ lundinTimewritingDeptReportDTO
												.getAfeName() + key, value);
					}

				}
			}
		}
		return totalTechnicalManHoursValFinalMap;
	}

	private List<Object[]> getLundinEmployeeDepartmentList(
			LundinTimesheetPreference lundinTimesheetPreferenceVO,
			Long companyId, String dateFormat, Long employeeId) {
		List<Long> formIds = new ArrayList<Long>();
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

		if (lundinTimesheetPreferenceVO != null) {
			// Department Field
			if (lundinTimesheetPreferenceVO.getDataDictionary() != null) {
				getColMap(lundinTimesheetPreferenceVO.getDataDictionary(),
						colMap);
				addDynamicTableKeyMap(
						lundinTimesheetPreferenceVO.getDataDictionary(),
						tableRecordInfoFrom, colMap);
				if (!formIds.contains(lundinTimesheetPreferenceVO
						.getDataDictionary().getFormID())) {
					formIds.add(lundinTimesheetPreferenceVO.getDataDictionary()
							.getFormID());
				}

			}
			// Auto Timewrite Field
			if (lundinTimesheetPreferenceVO.getAutoTimewrite() != null) {
				getColMap(lundinTimesheetPreferenceVO.getAutoTimewrite(),
						colMap);
				addDynamicTableKeyMap(
						lundinTimesheetPreferenceVO.getAutoTimewrite(),
						tableRecordInfoFrom, colMap);
				if (!formIds.contains(lundinTimesheetPreferenceVO
						.getAutoTimewrite().getFormID())) {
					formIds.add(lundinTimesheetPreferenceVO.getAutoTimewrite()
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

	public class StringBasedComparator implements Comparator<String> {
		public int compare(String obj1, String obj2) {
			if (obj1 == obj2) {
				return 0;
			}
			if (obj1 == null) {
				return -1;
			}
			if (obj2 == null) {
				return 1;
			}
			return obj1.compareTo(obj2);
		}
	}

	private class LundinTimewritingDeptComp implements
			Comparator<LundinTimewritingDeptReportDTO> {
		public int compare(LundinTimewritingDeptReportDTO templateField,
				LundinTimewritingDeptReportDTO compWithTemplateField) {
			String x1 = ((LundinTimewritingDeptReportDTO) templateField)
					.getBlockName();
			String x2 = ((LundinTimewritingDeptReportDTO) compWithTemplateField)
					.getBlockName();
			int sComp = x1.compareTo(x2);

			if (sComp != 0) {
				return sComp;
			} else {
				String x3 = ((LundinTimewritingDeptReportDTO) templateField)
						.getAfeName();
				String x4 = ((LundinTimewritingDeptReportDTO) compWithTemplateField)
						.getAfeName();
				return x3.compareTo(x4);
			}

		}

	}

	/**
	 * Comparator Class for Ordering LundinAFEComp List
	 */
	private class LundinAFEComp implements Comparator<LundinAFE> {
		public int compare(LundinAFE lundinAFE1, LundinAFE lundinAFE2) {
			return lundinAFE1.getAfeName().compareTo(lundinAFE2.getAfeName());
		}

	}

	private List<Object[]> getDailyPaidEmployeeDepartmentList(Long companyId,
			String dateFormat, Long employeeId, Timestamp bacthEndDate) {
		List<Long> formIds = new ArrayList<Long>();
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

		LundinTimesheetPreference lundinTimesheetPreferenceVO = lundinTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (lundinTimesheetPreferenceVO != null) {
			// Department Field
			if (lundinTimesheetPreferenceVO.getDataDictionary() != null) {
				getColMap(lundinTimesheetPreferenceVO.getDataDictionary(),
						colMap);
				addDynamicTableKeyMap(
						lundinTimesheetPreferenceVO.getDataDictionary(),
						tableRecordInfoFrom, colMap);
				formIds.add(lundinTimesheetPreferenceVO.getDataDictionary()
						.getFormID());
			}
			// Cost Category Field
			if (lundinTimesheetPreferenceVO.getCostCategory() != null) {
				getColMap(lundinTimesheetPreferenceVO.getCostCategory(), colMap);
				addDynamicTableKeyMap(
						lundinTimesheetPreferenceVO.getCostCategory(),
						tableRecordInfoFrom, colMap);
				if (!formIds.contains(lundinTimesheetPreferenceVO
						.getCostCategory().getFormID())) {
					formIds.add(lundinTimesheetPreferenceVO.getCostCategory()
							.getFormID());
				}

			}
			// Daily Rate Field
			if (lundinTimesheetPreferenceVO.getDailyRate() != null) {
				getColMap(lundinTimesheetPreferenceVO.getDailyRate(), colMap);
				addDynamicTableKeyMap(
						lundinTimesheetPreferenceVO.getDailyRate(),
						tableRecordInfoFrom, colMap);
				if (!formIds.contains(lundinTimesheetPreferenceVO
						.getDailyRate().getFormID())) {
					formIds.add(lundinTimesheetPreferenceVO.getDailyRate()
							.getFormID());
				}
			}
		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		employeeShortListDTO.setLundinBatchEndDate(DateUtils
				.timeStampToString(bacthEndDate));

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

	@Override
	public List<Object[]> getTimesheetStatusDepartmentEmpList(Long companyId,
			String dateFormat, Long employeeId,
			DataDictionary deptDataDictionary) {
		List<Long> formIds = new ArrayList<Long>();
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

		// Department Field
		if (deptDataDictionary != null) {
			getColMap(deptDataDictionary, colMap);
			addDynamicTableKeyMap(deptDataDictionary, tableRecordInfoFrom,
					colMap);
			formIds.add(deptDataDictionary.getFormID());
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
		if (colMap.get(dataDictionary.getLabel()+ dataDictionary.getDataDictionaryId()) !=null && colMap.get(dataDictionary.getLabel()+ dataDictionary.getDataDictionaryId()).getTablePosition() != null) {
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
	public LundinTimesheetReportDTO showDailyPaidTimesheet(Long companyId,
			Long employeeId,
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds) {
		LundinTimesheetReportDTO lundinTimesheetReportDTO = new LundinTimesheetReportDTO();

		Company companyVO = companyDAO.findById(companyId);

		String employeeIds = "";
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO
				.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (lundinTimesheetReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic
					.getShortListEmployeeIdsForReports(companyId,
							lundinTimesheetReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList
					.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds
						.retainAll(companyShortListEmployeeIds);
			}
		}

		if (lundinTimesheetReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}

		// /
		BigInteger filterEmpId = null;
		if (StringUtils.isNotBlank(lundinTimesheetReportsForm
				.getEmployeeNumber())
				&& !lundinTimesheetReportsForm.getEmployeeNumber()
						.equalsIgnoreCase("ALL")) {
			Employee employeeVO = employeeDAO.findByNumber(
					lundinTimesheetReportsForm.getEmployeeNumber(), companyId);
			filterEmpId = BigInteger.valueOf(employeeVO.getEmployeeId());
			if (generateEmpIds != null && !generateEmpIds.isEmpty()) {
				if (!generateEmpIds.contains(filterEmpId)) {
					filterEmpId = null;
				}
			}
			if (filterEmpId != null) {
				employeeIds = filterEmpId.toString();
			}

		} else {
			StringBuilder builder = new StringBuilder();
			for (BigInteger empId : generateEmpIds) {
				builder = builder.append(String.valueOf(empId));
				builder = builder.append(",");

			}

			if (!lundinTimesheetReportsForm.getIsShortList()
					&& (generateEmpIds == null || generateEmpIds.isEmpty())) {
				employeeIds = "ALL";
			} else {
				employeeIds = builder.toString();
				employeeIds = StringUtils.removeEnd(employeeIds, ",");
			}

		}

		Set<Long> empIdSet = new HashSet<Long>();
		LundinDailyPaidTimesheetDTO dailyRateDynFieldDetails = getDailyRateDynFieldDetails(companyId);
		List<LundinDailyPaidTimesheetDTO> dailyPaidTimesheetList = lundinTimesheetDAO
				.dailyPaidTimesheetReportProc(
						companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						String.valueOf(lundinTimesheetReportsForm.getYear()),
						dailyRateDynFieldDetails.getDynamicFormRecordColName(),
						dailyRateDynFieldDetails
								.getDynamicFormTableRecordColName(),
						dailyRateDynFieldDetails.getDailRateFormId(),
						lundinTimesheetReportsForm.isIncludeResignedEmployees(),
						employeeIds);
		List<String> empNumbersList = new ArrayList<>();
		for (LundinDailyPaidTimesheetDTO paidTimesheetDTO : dailyPaidTimesheetList) {
			empNumbersList.add(paidTimesheetDTO.getEmployeeNumber());
		}

		Map<String, LundinDailyPaidTimesheetDTO> empDeptCostCatDetailMap = new HashMap<String, LundinDailyPaidTimesheetDTO>();
		// Get Department Of Employees from Employee Information Details
		TimesheetBatch lundinTimesheetBatchVO = lundinTimesheetBatchDAO
				.findById(lundinTimesheetReportsForm.getFromBatchId());
		List<Object[]> deptObjectList = getDailyPaidEmployeeDepartmentList(
				companyId, companyVO.getDateFormat(), employeeId,
				lundinTimesheetBatchVO.getEndDate());
		for (Object[] deptObject : deptObjectList) {
			if (deptObject != null && deptObject[4] != null
					&& empNumbersList.contains(String.valueOf(deptObject[4]))) {
				LundinDailyPaidTimesheetDTO lundinDailyPaidTimesheetDTO = new LundinDailyPaidTimesheetDTO();
				lundinDailyPaidTimesheetDTO.setEmployeeNumber(String
						.valueOf(deptObject[4]));
				String lastName = "";
				if (deptObject[6] != null) {
					lastName = String.valueOf(deptObject[6]);
				}
				lundinDailyPaidTimesheetDTO.setEmployeeName(getEmployeeName(
						String.valueOf(deptObject[5]), lastName));
				lundinDailyPaidTimesheetDTO.setCostCategoryCode(String
						.valueOf(deptObject[2]));
				lundinDailyPaidTimesheetDTO.setDepartmentCode(String
						.valueOf(deptObject[0]));
				lundinDailyPaidTimesheetDTO.setDepartmentDesc(String
						.valueOf(deptObject[1]));
				empDeptCostCatDetailMap.put(String.valueOf(deptObject[4]),
						lundinDailyPaidTimesheetDTO);
			}
		}

		for (LundinDailyPaidTimesheetDTO paidTimesheetDTO : dailyPaidTimesheetList) {
			LundinDailyPaidTimesheetDTO empDeptCostCatDetail = empDeptCostCatDetailMap
					.get(paidTimesheetDTO.getEmployeeNumber());
			paidTimesheetDTO.setCostCategoryCode(empDeptCostCatDetail
					.getCostCategoryCode());
			paidTimesheetDTO.setCostCategoryDesc(empDeptCostCatDetail
					.getCostCategoryDesc());
			paidTimesheetDTO.setDepartmentCode(empDeptCostCatDetail
					.getDepartmentCode());
			paidTimesheetDTO.setDepartmentDesc(empDeptCostCatDetail
					.getDepartmentDesc());
			paidTimesheetDTO.setEmployeeName(empDeptCostCatDetail
					.getEmployeeName());
		}

		for (LundinDailyPaidTimesheetDTO paidTimesheetDTO : dailyPaidTimesheetList) {
			empIdSet.add(paidTimesheetDTO.getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long
						.parseLong(dataDictionaryIds[count]));
			}
		}

		List<LeaveReportHeaderDTO> leaveHeaderDTOs = new ArrayList<>();
		LeaveReportHeaderDTO employeeNo = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeName = new LeaveReportHeaderDTO();
		employeeNo.setmDataProp("employeeNumber");
		employeeNo.setsTitle("Employee ID");
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");
		leaveHeaderDTOs.add(employeeNo);
		leaveHeaderDTOs.add(employeeName);
		LeaveReportHeaderDTO department = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO annualCumManDays = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO annualCumAmount = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO costCategory = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO totalManDays = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO dailyRate = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO totalAmount = new LeaveReportHeaderDTO();
		department.setmDataProp("departmentCode");
		department.setsTitle("Department");
		annualCumManDays.setmDataProp("annualCumManDays");
		annualCumManDays.setsTitle("Annual Cum Man-Days");
		annualCumAmount.setmDataProp("annualCumAmount");
		annualCumAmount.setsTitle("Annual Cum Amount");
		costCategory.setmDataProp("costCategoryCode");
		costCategory.setsTitle("Cost Category");
		totalManDays.setmDataProp("totalManDays");
		totalManDays.setsTitle("Total Man-Days");
		dailyRate.setmDataProp("dailyRate");
		dailyRate.setsTitle("Daily Rate");
		totalAmount.setmDataProp("totalAmount");
		totalAmount.setsTitle("Total Amount");
		leaveHeaderDTOs.add(department);
		leaveHeaderDTOs.add(annualCumManDays);
		leaveHeaderDTOs.add(annualCumAmount);
		leaveHeaderDTOs.add(costCategory);
		leaveHeaderDTOs.add(totalManDays);
		leaveHeaderDTOs.add(dailyRate);
		leaveHeaderDTOs.add(totalAmount);
		lundinTimesheetReportDTO.setLundinHeaderDTOs(leaveHeaderDTOs);

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
				leaveHeaderDTOs.add(leaveHeaderDTO);
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
			for (LundinDailyPaidTimesheetDTO reportDTO : dailyPaidTimesheetList) {
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
								LundinDailyPaidTimesheetDTO.class.getMethod(
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
			lundinTimesheetReportDTO.setDataDictNameList(dataDictNameList);
			List<LeaveReportCustomDataDTO> customDataDTOs = new ArrayList<>();
			for (int countF = 1; countF <= fieldCount; countF++) {
				LeaveReportCustomDataDTO customFieldDto = new LeaveReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field "
						+ countF);
				customDataDTOs.add(customFieldDto);
			}
			lundinTimesheetReportDTO.setLeaveCustomDataDTOs(customDataDTOs);
		}

		BeanComparator beanComparator = new BeanComparator("employeeNumber");
		Collections.sort(dailyPaidTimesheetList, beanComparator);
		lundinTimesheetReportDTO.setTotalEmployeesCount(empIdSet.size());
		lundinTimesheetReportDTO
				.setLundinDailyPaidTimesheetDTOs(dailyPaidTimesheetList);

		return lundinTimesheetReportDTO;
	}

	public LundinDailyPaidTimesheetDTO getDailyRateDynFieldDetails(
			Long companyId) {
		LundinDailyPaidTimesheetDTO dailyPaidTimesheetDTO = new LundinDailyPaidTimesheetDTO();
		String dynamicFormRecordColName = "";
		String dynamicFormTableRecordColName = "";

		DataDictionary dataDictionary = null;
		LundinTimesheetPreference lundinTimesheetPreferenceVO = lundinTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (lundinTimesheetPreferenceVO != null
				&& lundinTimesheetPreferenceVO.getDailyRate() != null) {
			dataDictionary = lundinTimesheetPreferenceVO.getDailyRate();
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
					companyId, dataDictionary.getEntityMaster().getEntityId(),
					dataDictionary.getFormID());
			if (dynamicForm != null) {
				Tab tab = dataExportUtils.getTabObject(dynamicForm);
				List<Field> listOfFields = tab.getField();
				for (Field field : listOfFields) {
					if (field.getType().equalsIgnoreCase(
							PayAsiaConstants.TABLE_FIELD_TYPE)) {
						List<Column> columnList = field.getColumn();
						for (Column column : columnList) {
							if (column.getDictionaryId().equals(
									dataDictionary.getDataDictionaryId())) {
								dynamicFormTableRecordColName = "Col"
										+ column.getName().substring(
												column.getName().indexOf("_"),
												column.getName().length());
								dynamicFormRecordColName = "Col"
										+ field.getName().substring(
												field.getName().indexOf("_"),
												field.getName().length());
								dailyPaidTimesheetDTO
										.setDynamicFormRecordColName(dynamicFormRecordColName);
								dailyPaidTimesheetDTO
										.setDynamicFormTableRecordColName(dynamicFormTableRecordColName);
								dailyPaidTimesheetDTO.setDailRateFormId(String
										.valueOf(dynamicForm.getId()
												.getFormId()));
							}
						}
					}
				}
			}
		}
		return dailyPaidTimesheetDTO;

	}

	@Override
	public LundinDailyPaidTimesheetDTO getDailyPaidBatchName(Long companyId,
			Long batchId) {
		LundinDailyPaidTimesheetDTO lundinReportDTO = new LundinDailyPaidTimesheetDTO();
		String otBatchName = "";
		TimesheetBatch timesheetBatch = lundinTimesheetBatchDAO
				.findById(batchId);
		if (timesheetBatch != null) {
			otBatchName = timesheetBatch.getTimesheetBatchDesc()
					.substring(0, 3)
					+ " "
					+ timesheetBatch.getTimesheetBatchDesc().substring(4, 8);
			otBatchName = otBatchName.toUpperCase();
		}
		lundinReportDTO.setFileFromBatchDate(DateUtils.timeStampToString(
				timesheetBatch.getStartDate(), timesheetBatch.getCompany()
						.getDateFormat()));
		lundinReportDTO.setFileToBatchDate(DateUtils.timeStampToString(
				timesheetBatch.getEndDate(), timesheetBatch.getCompany()
						.getDateFormat()));
		lundinReportDTO.setFileBatchName(otBatchName);
		return lundinReportDTO;

	}

	@Override
	public List<LundinTimesheetReportsForm> lundinEmployeeList(Long employeeId,
			Long companyId) {
		List<LundinTimesheetReportsForm> employeeNumList = new ArrayList<>();

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Employee> employeeList = employeeDAO.findByCondition(conditionDTO,
				null, null, companyId);
		for (Employee employeeVO : employeeList) {
			if (employeeVO.isStatus()) {
				LundinTimesheetReportsForm otTimesheetReportsForm = new LundinTimesheetReportsForm();
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
	public LundinTimesheetReportDTO showTimesheetStatusReport(Long companyId,
			Long employeeId,
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds) {
		LundinTimesheetReportDTO lundinTimesheetReportDTO = new LundinTimesheetReportDTO();

		Company companyVO = companyDAO.findById(companyId);

		String shortListEmployeeIds = "";
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> shortlistEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO
				.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (lundinTimesheetReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic
					.getShortListEmployeeIdsForReports(companyId,
							lundinTimesheetReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList
					.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds
						.retainAll(companyShortListEmployeeIds);
			}
		}

		if (lundinTimesheetReportsForm.getIsShortList()) {
			shortlistEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			shortlistEmpIds = new ArrayList<>(companyShortListEmployeeIds);
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
		Map<String, LundinTimesheetStatusReportDTO> empDeptCostCatDetailMap = new HashMap<String, LundinTimesheetStatusReportDTO>();

		List<Object[]> deptObjectList = new ArrayList<>();
		LundinTimesheetPreference lundinTimesheetPreferenceVO = lundinTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (lundinTimesheetPreferenceVO != null) {
			// Department Field
			if (lundinTimesheetPreferenceVO.getDataDictionary() != null) {
				deptObjectList = getTimesheetStatusDepartmentEmpList(companyId,
						companyVO.getDateFormat(), employeeId,
						lundinTimesheetPreferenceVO.getDataDictionary());

			}
		}
		// Get Department Of Employees from Employee Information Details
		for (Object[] deptObject : deptObjectList) {
			if (deptObject != null && deptObject[2] != null) {
				LundinTimesheetStatusReportDTO lundinTimesheetStatusReportDTO = new LundinTimesheetStatusReportDTO();
				lundinTimesheetStatusReportDTO.setEmployeeNumber(String
						.valueOf(deptObject[2]));
				String lastName = "";
				if (deptObject[4] != null) {
					lastName = String.valueOf(deptObject[4]);
				}
				lundinTimesheetStatusReportDTO.setEmployeeName(getEmployeeName(
						String.valueOf(deptObject[3]), lastName));
				lundinTimesheetStatusReportDTO.setDepartmentCode(String
						.valueOf(deptObject[0]));
				lundinTimesheetStatusReportDTO.setDepartmentDesc(String
						.valueOf(deptObject[1]));
				lundinTimesheetStatusReportDTO.setEmployeeId(empNumEmpIdMap
						.get(String.valueOf(deptObject[2])));
				deptEmpIdsList.add(BigInteger.valueOf(empNumEmpIdMap.get(String
						.valueOf(deptObject[2]))));
				empDeptCostCatDetailMap.put(String.valueOf(deptObject[2]),
						lundinTimesheetStatusReportDTO);
			}
		}

		shortlistEmpIds.retainAll(deptEmpIdsList);
		StringBuilder builder = new StringBuilder();
		for (BigInteger empId : shortlistEmpIds) {
			builder = builder.append(String.valueOf(empId));
			builder = builder.append(",");

		}
		shortListEmployeeIds = builder.toString();
		shortListEmployeeIds = StringUtils.removeEnd(shortListEmployeeIds, ",");

		String timesheetStatus = "";
		if (StringUtils
				.isBlank(lundinTimesheetReportsForm.getTimesheetStatus())) {
			timesheetStatus += "Draft,Submitted,Approved,Completed,Rejected,Withdrawn";
		} else if (lundinTimesheetReportsForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
			timesheetStatus += "Submitted,Approved";
		} else if (lundinTimesheetReportsForm.getTimesheetStatus()
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
			timesheetStatus += "Completed";
		} else {
			timesheetStatus += lundinTimesheetReportsForm.getTimesheetStatus();
		}

		List<LundinTimesheetStatusReportDTO> lundinTimesheetStatusReportVOList = lundinTimesheetDAO
				.lundinTimesheetStatusReportProc(
						companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						lundinTimesheetReportsForm.getToBatchId(),
						timesheetStatus,
						lundinTimesheetReportsForm.isIncludeResignedEmployees(),
						shortListEmployeeIds);

		Set<Long> empIdSet = new HashSet<Long>();
		for (LundinTimesheetStatusReportDTO timesheetStatusReportDTO : lundinTimesheetStatusReportVOList) {
			empIdSet.add(timesheetStatusReportDTO.getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long
						.parseLong(dataDictionaryIds[count]));
			}
		}

		List<LeaveReportHeaderDTO> leaveHeaderDTOs = new ArrayList<>();
		LeaveReportHeaderDTO employeeNo = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeName = new LeaveReportHeaderDTO();
		employeeNo.setmDataProp("employeeNumber");
		employeeNo.setsTitle("Employee ID");
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");
		leaveHeaderDTOs.add(employeeNo);
		leaveHeaderDTOs.add(employeeName);
		LeaveReportHeaderDTO department = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO totalManDays = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO cutoff = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeReviewer1Name = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeReviewer1Status = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeReviewer2Name = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeReviewer2Status = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeReviewer3Name = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO employeeReviewer3Status = new LeaveReportHeaderDTO();
		department.setmDataProp("departmentCode");
		department.setsTitle("Department");
		totalManDays.setmDataProp("totalManDays");
		totalManDays.setsTitle("Total Man-Days");
		cutoff.setmDataProp("cutoff");
		cutoff.setsTitle("Cutoff");
		employeeReviewer1Name.setmDataProp("employeeReviewer1Name");
		employeeReviewer1Name.setsTitle("Reviewer 1 Name");
		employeeReviewer1Status.setmDataProp("employeeReviewer1Status");
		employeeReviewer1Status.setsTitle("Reviewer 1 Status");
		employeeReviewer2Name.setmDataProp("employeeReviewer2Name");
		employeeReviewer2Name.setsTitle("Reviewer 2 Name");
		employeeReviewer2Status.setmDataProp("employeeReviewer2Status");
		employeeReviewer2Status.setsTitle("Reviewer 2 Status");
		employeeReviewer3Name.setmDataProp("employeeReviewer3Name");
		employeeReviewer3Name.setsTitle("Reviewer 3 Name");
		employeeReviewer3Status.setmDataProp("employeeReviewer3Status");
		employeeReviewer3Status.setsTitle("Reviewer 3 Status");
		leaveHeaderDTOs.add(department);
		leaveHeaderDTOs.add(cutoff);
		leaveHeaderDTOs.add(totalManDays);
		leaveHeaderDTOs.add(employeeReviewer1Name);
		leaveHeaderDTOs.add(employeeReviewer1Status);
		leaveHeaderDTOs.add(employeeReviewer2Name);
		leaveHeaderDTOs.add(employeeReviewer2Status);
		leaveHeaderDTOs.add(employeeReviewer3Name);
		leaveHeaderDTOs.add(employeeReviewer3Status);
		lundinTimesheetReportDTO.setLundinHeaderDTOs(leaveHeaderDTOs);

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
				leaveHeaderDTOs.add(leaveHeaderDTO);
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
			for (LundinTimesheetStatusReportDTO reportDTO : lundinTimesheetStatusReportVOList) {
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
								LundinTimesheetStatusReportDTO.class.getMethod(
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
			lundinTimesheetReportDTO.setDataDictNameList(dataDictNameList);
			List<LeaveReportCustomDataDTO> customDataDTOs = new ArrayList<>();
			for (int countF = 1; countF <= fieldCount; countF++) {
				LeaveReportCustomDataDTO customFieldDto = new LeaveReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field "
						+ countF);
				customDataDTOs.add(customFieldDto);
			}
			lundinTimesheetReportDTO.setLeaveCustomDataDTOs(customDataDTOs);
		}
		LundinDepartment lundinDepartmentVO = null;
		if (lundinTimesheetReportsForm.getDepartmentId() != 0) {
			lundinDepartmentVO = lundinDepartmentDAO
					.findById(lundinTimesheetReportsForm.getDepartmentId());
		}

		List<LundinTimesheetStatusReportDTO> lundinTimesheetStatusReportVONewList = new ArrayList<LundinTimesheetStatusReportDTO>();
		for (LundinTimesheetStatusReportDTO timesheetStatusReportDTO : lundinTimesheetStatusReportVOList) {
			LundinTimesheetStatusReportDTO empDeptCostCatDetail = empDeptCostCatDetailMap
					.get(timesheetStatusReportDTO.getEmployeeNumber());
			timesheetStatusReportDTO.setDepartmentCode(empDeptCostCatDetail
					.getDepartmentCode());
			timesheetStatusReportDTO.setDepartmentDesc(empDeptCostCatDetail
					.getDepartmentDesc());
			timesheetStatusReportDTO.setEmployeeName(empDeptCostCatDetail
					.getEmployeeName());
			if (timesheetStatusReportDTO.getTimesheetId() == 0) {
				lundinTimesheetStatusReportVONewList
						.add(timesheetStatusReportDTO);
			} else {
				if (lundinTimesheetReportsForm.getDepartmentId() != 0) {
					if (lundinDepartmentVO != null
							&& lundinDepartmentVO
									.getDynamicFormFieldRefValue()
									.getDescription()
									.equalsIgnoreCase(
											empDeptCostCatDetail
													.getDepartmentDesc())) {
						lundinTimesheetStatusReportVONewList
								.add(timesheetStatusReportDTO);
					}
				} else {
					lundinTimesheetStatusReportVONewList
							.add(timesheetStatusReportDTO);
				}
			}
		}

		int totalApprovedTimesheet = 0;
		int totalPendingTimesheet = 0;
		int totalEmployeesCount = 0;
		for (LundinTimesheetStatusReportDTO timesheetStatusReportDTO : lundinTimesheetStatusReportVONewList) {
			if (StringUtils.isNotBlank(timesheetStatusReportDTO
					.getTimesheetStatusName())
					&& timesheetStatusReportDTO.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
				totalApprovedTimesheet++;
			} else {
				totalPendingTimesheet++;
			}
			totalEmployeesCount++;
		}

		lundinTimesheetReportDTO.setTotalEmployeesCount(totalEmployeesCount);
		lundinTimesheetReportDTO
				.setTotalEmployeesApprovedTimesheetCount(totalApprovedTimesheet);
		lundinTimesheetReportDTO
				.setTotalEmployeesPendingTimesheetCount(totalPendingTimesheet);
		lundinTimesheetReportDTO
				.setLundinTimesheetStatusReportDTOs(lundinTimesheetStatusReportVONewList);

		return lundinTimesheetReportDTO;
	}

	@Override
	public List<EmployeeListForm> findEmployeeBasedOnDepartment(Long companyId,
			Long departmentId, Long employeeId) {

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		Company companyVO = companyDAO.findById(companyId);
		if (departmentId != 0) {
			LundinDepartment lundinDepartment = lundinDepartmentDAO
					.findById(departmentId);
			if (lundinDepartment != null) {
				String deptCode = lundinDepartment
						.getDynamicFormFieldRefValue().getCode();
				// Get All Employees By company
				Map<String, Long> empNumEmpIdMap = new HashMap<String, Long>();
				List<Employee> employeeIdVOList = employeeDAO
						.findByCompany(companyId);
				for (Employee employee : employeeIdVOList) {
					// if (employee.isStatus()) {
					empNumEmpIdMap.put(employee.getEmployeeNumber(),
							employee.getEmployeeId());
					// }
				}
				List<Object[]> deptEmployeeObjList = getTimesheetStatusDepartmentEmpList(
						companyId, companyVO.getDateFormat(), employeeId,
						lundinDepartment.getDynamicFormFieldRefValue()
								.getDataDictionary());
				// Get Department Of Employees from Employee Information Details
				for (Object[] deptObject : deptEmployeeObjList) {
					if (deptObject != null
							&& deptObject[2] != null
							&& deptObject[0] != null
							&& String.valueOf(deptObject[0]).equalsIgnoreCase(
									deptCode)) {
						EmployeeListForm lundinTimesheetStatusReportDTO = new EmployeeListForm();
						lundinTimesheetStatusReportDTO.setEmployeeNumber(String
								.valueOf(deptObject[2]));
						String lastName = "";
						if (deptObject[4] != null) {
							lastName = String.valueOf(deptObject[4]);
						}
						lundinTimesheetStatusReportDTO
								.setEmployeeName(getEmployeeName(
										String.valueOf(deptObject[3]), lastName));
						// lundinTimesheetStatusReportDTO.setDepartmentCode(String
						// .valueOf(deptObject[0]));
						// lundinTimesheetStatusReportDTO.setDepartmentDesc(String
						// .valueOf(deptObject[1]));
						lundinTimesheetStatusReportDTO
								.setEmployeeID(empNumEmpIdMap.get(String
										.valueOf(deptObject[2])));
						employeeListFormList
								.add(lundinTimesheetStatusReportDTO);
					}
				}
			}
		}

		return employeeListFormList;

	}

	@Override
	public String isEmployeeRoleAsTimesheetReviewer(Long companyId,
			Long employeeId) {
		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();
		employeeShortListDTO.setEmployeeShortList(false);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		List<Tuple> employeeVOList = lundinEmployeeReviewerDAO
				.getEmployeeIdsTupleForTimesheetReviewer(conditionDTO,
						companyId, employeeId, employeeShortListDTO);
		if (employeeVOList != null && !employeeVOList.isEmpty()) {
			return PayAsiaConstants.TRUE;
		}
		return PayAsiaConstants.FALSE;
	}

}
