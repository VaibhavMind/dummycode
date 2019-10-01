package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.xml.bind.JAXBException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.CustomFieldReportDTO;
import com.payasia.common.dto.DayWiseLeaveTranReportDTO;
import com.payasia.common.dto.EmployeeAsOnLeaveDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeHeadCountReportDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveBalAsOnDayDTO;
import com.payasia.common.dto.LeaveReportCustomDataDTO;
import com.payasia.common.dto.LeaveReportDTO;
import com.payasia.common.dto.LeaveReportHeaderDTO;
import com.payasia.common.dto.LeaveReviewerReportDTO;
import com.payasia.common.dto.LeaveTranReportDTO;
import com.payasia.common.dto.SelectOptionDTO;
import com.payasia.common.dto.YearWiseSummarryDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.LeaveReportsForm;
import com.payasia.common.form.LeaveReportsResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyForm;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeHistoryDAO;
import com.payasia.dao.LeaveApplicationCustomFieldDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveStatusMasterDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveApplicationCustomField;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LeaveBalCustomReportPDFLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.LeaveReportsLogic;

@Component
public class LeaveReportsLogicImpl extends BaseLogic implements LeaveReportsLogic {
	private static final Logger LOGGER = Logger.getLogger(LeaveReportsLogicImpl.class);
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	EmployeeLeaveSchemeTypeHistoryDAO employeeLeaveSchemeTypeHistoryDAO;

	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	@Resource
	LeavePreferenceDAO leavePreferenceDAO;
	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;
	@Resource
	LeaveStatusMasterDAO leaveStatusMasterDAO;
	@Resource
	private LeaveApplicationCustomFieldDAO leaveApplicationCustomFieldDAO;
	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	LeaveBalCustomReportPDFLogic leaveBalCustomEmpPerPagePDFLogic;
	@Resource
	EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;

	@Override
	public LeaveReportsResponse viewLeaveReports(PageRequest pageDTO, SortCondition sortDTO, Long companyId) {
		LeaveReportsResponse leaveReportsResponse = new LeaveReportsResponse();
		List<LeaveReportsForm> leaveReportsFormList = new ArrayList<>();

		LeaveReportsForm reportsForm1 = new LeaveReportsForm();
		reportsForm1.setReportName("Leave Balance As On A Day");
		leaveReportsFormList.add(reportsForm1);

		LeaveReportsForm reportsForm2 = new LeaveReportsForm();
		reportsForm2.setReportName("Year Wise Summary report");
		leaveReportsFormList.add(reportsForm2);

		LeaveReportsForm reportsForm3 = new LeaveReportsForm();
		reportsForm3.setReportName("Leave Transaction Report");
		leaveReportsFormList.add(reportsForm3);

		leaveReportsResponse.setLeaveReportsFormList(leaveReportsFormList);
		return leaveReportsResponse;
	}

	@Override
	public LeaveReportsResponse searchEmployee(PageRequest pageDTO, SortCondition sortDTO, String searchCondition, String searchText, Long companyId,
			Long employeeId, String metaData, Boolean includeResignedEmployees) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
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
		List<Employee> employeeVOList = employeeDAO.findByCondition(conditionDTO, null, null, companyId);
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		LeaveReportsResponse response = new LeaveReportsResponse();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public LeaveReportsResponse searchEmployeeForManager(PageRequest pageDTO, SortCondition sortDTO, String searchCondition, String searchText,
			Long companyId, Long employeeId, String metaData, Boolean includeResignedEmployees) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		employeeShortListDTO.setEmployeeShortList(false);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		
		if (StringUtils.isNotEmpty(searchCondition)) {
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
		}
		conditionDTO.setStatus(includeResignedEmployees);
		List<Tuple> employeeVOList = employeeLeaveReviewerDAO.findEmpsByCondition(conditionDTO, null, null, companyId, employeeId);
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Tuple employee : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.get(getAlias(Employee_.firstName), String.class) + " ";
			if (StringUtils.isNotBlank(employee.get(getAlias(Employee_.lastName), String.class))) {
				employeeName += employee.get(getAlias(Employee_.lastName), String.class);
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.get(getAlias(Employee_.employeeNumber), String.class));

			employeeForm.setEmployeeID(employee.get(getAlias(Employee_.employeeId), Long.class));
			employeeForm.setAction(true);
			employeeListFormList.add(employeeForm);
		}

		LeaveReportsResponse response = new LeaveReportsResponse();
		
		List<EmployeeListForm> employeeListFormListFinal = new ArrayList<EmployeeListForm>();
		
		if (pageDTO != null && !employeeListFormList.isEmpty()) {
			int recordSizeFinal = employeeListFormList.size();
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;

			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal){
				endPos = startPos + pageSize;
			} else if (startPos <= recordSizeFinal) {
				endPos = recordSizeFinal;
			}

			employeeListFormListFinal = employeeListFormList.subList(startPos, endPos);
			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSizeFinal);
		}
		
		response.setSearchEmployeeList(employeeListFormListFinal);
		return response;
	}

	@Override
	public List<LeaveReportsForm> getLeaveTypeList(Long companyId) {
		List<LeaveReportsForm> leaveReportsFormList = new ArrayList<>();

		List<LeaveTypeMaster> leaveTypeMasterList = leaveTypeMasterDAO.findByCompanyAndVisibility(companyId);
		for (LeaveTypeMaster leaveTypeMaster : leaveTypeMasterList) {
			LeaveReportsForm leaveReportsForm = new LeaveReportsForm();
			leaveReportsForm.setLeaveType(leaveTypeMaster.getLeaveTypeName());
			leaveReportsForm.setLeaveTypeId(leaveTypeMaster.getLeaveTypeId());
			leaveReportsFormList.add(leaveReportsForm);
		}
		return leaveReportsFormList;

	}

	@Override
	public List<LeaveReportsForm> getLeaveTransactionList(Long companyId) {
		List<LeaveReportsForm> leaveReportsFormList = new ArrayList<>();

		boolean status = true;
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			if (leavePreferenceVO.getShowEncashed() != null) {
				if (!leavePreferenceVO.getShowEncashed()) {
					status = false;
				}
			}
		}
		List<AppCodeMaster> appCodeMasterList = appCodeMasterDAO.findByCondition(PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE);

		for (AppCodeMaster appCodeMaster : appCodeMasterList) {
			LeaveReportsForm leaveReportsForm = new LeaveReportsForm();
			if (!status) {
				if (!"Encashed".equalsIgnoreCase(appCodeMaster.getCodeDesc())) {
					leaveReportsForm.setLeaveTransactionName(appCodeMaster.getCodeValue());
					leaveReportsForm.setLeaveTransactionId(appCodeMaster.getAppCodeID());
					leaveReportsFormList.add(leaveReportsForm);
				}
			} else {
				leaveReportsForm.setLeaveTransactionName(appCodeMaster.getCodeValue());
				leaveReportsForm.setLeaveTransactionId(appCodeMaster.getAppCodeID());
				leaveReportsFormList.add(leaveReportsForm);
			}

		}
		return leaveReportsFormList;

	}

	@Override
	public LeaveReportDTO showLeaveTranReport(Long companyId, Long employeeId, LeaveReportsForm leaveReportsForm, Boolean isManager,
			String[] dataDictionaryIds) {

		LeaveReportDTO leaveReportDTO = new LeaveReportDTO();

		Company companyVO = companyDAO.findById(companyId);

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);

		boolean leavePreferencePreApproval = leavePreference.isPreApprovalRequired();

		boolean leaveExtension = leavePreference.isLeaveExtensionRequired();

		leaveReportDTO.setLeavePreferencePreApproval(leavePreferencePreApproval);
		leaveReportDTO.setLeaveExtensionPreference(leaveExtension);

		List<Long> empIdsList = new ArrayList<>();
		String employeeIds = "";
		if (isManager) {
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setStatus(leaveReportsForm.isIncludeResignedEmployees());
			List<Tuple> employeeIdsList = employeeLeaveReviewerDAO.getEmployeeIdsTupleForManager(companyId, employeeId, employeeShortListDTO);
			for (Tuple employeeTuple : employeeIdsList) {
				empIdsList.add(employeeTuple.get(getAlias(Employee_.employeeId), Long.class));
			}

			StringBuilder builder = new StringBuilder();
			for (Long empId : empIdsList) {
				builder = builder.append(String.valueOf(empId));
				builder = builder.append(",");

			}
			employeeIds = builder.toString();
		} else {
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

			List<BigInteger> generateEmpIds = null;
			List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
			List<BigInteger> reportShortListEmployeeIds = null;
			if (leaveReportsForm.getIsShortList()) {
				EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId, leaveReportsForm.getMetaData());

				reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

				if (employeeShortListDTO.getEmployeeShortList()) {
					reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
				}
			}

			if (leaveReportsForm.getIsShortList()) {
				generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
			} else {
				generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
			}
			StringBuilder builder = new StringBuilder();
			for (BigInteger empId : generateEmpIds) {
				builder = builder.append(String.valueOf(empId));
				builder = builder.append(",");

			}
			employeeIds = builder.toString();

		}
		Set<Long> empIdSet = new HashSet<Long>();

		String leaveTypeList = "";
		StringBuilder leaveTypeBuilder = new StringBuilder();
		for (Long leaveTypeId : leaveReportsForm.getMultipleLeaveTypeId()) {
			if (leaveTypeId != null) {
				leaveTypeBuilder = leaveTypeBuilder.append(String.valueOf(leaveTypeId));
				leaveTypeBuilder = leaveTypeBuilder.append(",");
			}

		}
		leaveTypeList = leaveTypeBuilder.toString();

		String leaveTransactionList = "";
		List<String> leaveTranList = new ArrayList<String>();
		StringBuilder leaveTransactionBuilder = new StringBuilder();
		for (String leaveTransaction : leaveReportsForm.getMultipleLeaveTransactionName()) {
			if (!leaveTransaction.equalsIgnoreCase("0")) {
				leaveTransactionBuilder = leaveTransactionBuilder.append(leaveTransaction);
				leaveTranList.add(leaveTransaction);
				leaveTransactionBuilder = leaveTransactionBuilder.append(",");
			}

		}
		leaveTransactionList = leaveTransactionBuilder.toString();
		leaveTransactionList = StringUtils.removeEnd(leaveTransactionBuilder.toString(), ",");

		List<LeaveTranReportDTO> leaveReportDataDTOs = null;
		if (leaveReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds) && !isManager) {
			leaveReportDataDTOs = new ArrayList<LeaveTranReportDTO>();
		} else {
			leaveReportDataDTOs = employeeLeaveSchemeTypeHistoryDAO.getLeaveTransactionReportProc(companyId, employeeIds, leaveReportsForm.getStartDate(),
					leaveReportsForm.getEndDate(), leaveTypeList, leaveTransactionList, leaveReportsForm.isMultipleRecord(),
					leaveReportsForm.isIncludeApprovalCancel(), leaveReportsForm.isIncludeResignedEmployees(), companyVO.getDateFormat(), isManager,
					leaveReportsForm.getLeaveReviewerId(), leavePreferencePreApproval, leaveExtension);
		}

		for (LeaveTranReportDTO leaveTranReportDTO : leaveReportDataDTOs) {
			empIdSet.add(leaveTranReportDTO.getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}

		}

		List<LeaveReportHeaderDTO> leaveHeaderDTOs = new ArrayList<>();
		if (leaveReportsForm.isMultipleRecord()) {
			LeaveReportHeaderDTO employeeNo = new LeaveReportHeaderDTO();
			employeeNo.setmDataProp("employeeNo");
			employeeNo.setsTitle("Employee Id");
			leaveHeaderDTOs.add(employeeNo);
		} else {
			LeaveReportHeaderDTO employeeNo = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO employeeName = new LeaveReportHeaderDTO();
			employeeNo.setmDataProp("employeeNo");
			employeeNo.setsTitle("Employee Number");
			employeeName.setmDataProp("employeeName");
			employeeName.setsTitle("Employee Name");
			leaveHeaderDTOs.add(employeeNo);
			leaveHeaderDTOs.add(employeeName);
		}

		CustomFieldReportDTO customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList, companyId,
				false);

		List<String> dataDictNameList = customFieldReportDTO.getDataDictNameList();

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);

		if (leaveReportsForm.isMultipleRecord()) {
			LeaveReportHeaderDTO leaveTypeName = new LeaveReportHeaderDTO();
			leaveTypeName.setmDataProp("leaveTypeName");
			leaveTypeName.setsTitle("Leave Type");
			LeaveReportHeaderDTO leaveDate = new LeaveReportHeaderDTO();
			leaveDate.setmDataProp("leaveDate");
			leaveDate.setsTitle("Leave Date");
			LeaveReportHeaderDTO leaveDuration = new LeaveReportHeaderDTO();
			leaveDuration.setmDataProp("leaveDuration");
			leaveDuration.setsTitle("Leave Duration");
			LeaveReportHeaderDTO leaveApplyType = new LeaveReportHeaderDTO();
			leaveApplyType.setmDataProp("leaveApplyType");
			leaveApplyType.setsTitle("(N)ew/(C)ancel");
			
			LeaveReportHeaderDTO leaveTransStatus = new LeaveReportHeaderDTO();
			leaveTransStatus.setmDataProp("leaveTransactionType");
			leaveTransStatus.setsTitle("Leave Transaction Type");
			
			leaveHeaderDTOs.add(leaveTypeName);
			leaveHeaderDTOs.add(leaveDate);
			leaveHeaderDTOs.add(leaveDuration);
			leaveHeaderDTOs.add(leaveApplyType);
			leaveHeaderDTOs.add(leaveTransStatus);
		} else {
			LeaveReportHeaderDTO leaveTypeName = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO leaveSchemeName = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO postedDate = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO approvedDate = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO fromDate = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO session1 = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO toDate = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO session2 = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO days = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO remarks = new LeaveReportHeaderDTO();
			LeaveReportHeaderDTO leaveTransStatus = new LeaveReportHeaderDTO();
			leaveTypeName.setmDataProp("leaveTypeName");
			leaveTypeName.setsTitle("Leave Type");
			leaveSchemeName.setmDataProp("leaveSchemeName");
			leaveSchemeName.setsTitle("Leave Scheme");
			postedDate.setmDataProp("postedDate");
			postedDate.setsTitle("Posted Date");
			approvedDate.setmDataProp("approvedDate");
			approvedDate.setsTitle("Approved Date");
			fromDate.setmDataProp("fromDate");
			fromDate.setsTitle("From Date");
			session1.setmDataProp("session1");
			session1.setsTitle("From Session");
			toDate.setmDataProp("toDate");
			toDate.setsTitle("To Date");
			session2.setmDataProp("session2");
			session2.setsTitle("To Session");
			days.setmDataProp("days");
			if (!isLeaveUnitDays) {
				days.setsTitle("Hours");
			} else {
				days.setsTitle("Days");
			}
			remarks.setmDataProp("remarks");
			remarks.setsTitle("Remarks");
			
			leaveTransStatus.setmDataProp("leaveTransactionType");
			leaveTransStatus.setsTitle("Leave Transaction Type");
	
			
			leaveHeaderDTOs.add(leaveSchemeName);
			leaveHeaderDTOs.add(leaveTypeName);
			leaveHeaderDTOs.add(postedDate);
			if (leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)
					|| leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				leaveHeaderDTOs.add(approvedDate);
			}
			leaveHeaderDTOs.add(fromDate);
			leaveHeaderDTOs.add(session1);
			leaveHeaderDTOs.add(toDate);
			leaveHeaderDTOs.add(session2);
			leaveHeaderDTOs.add(days);
			leaveHeaderDTOs.add(remarks);
			leaveHeaderDTOs.add(leaveTransStatus);
		}
		if (leavePreferencePreApproval) {
			LeaveReportHeaderDTO preApproval = new LeaveReportHeaderDTO();
			preApproval.setmDataProp("preApproval");
			preApproval.setsTitle("Is Pre-Approved Leave");
			leaveHeaderDTOs.add(preApproval);
		}
		if (leaveExtension) {
			LeaveReportHeaderDTO preApproval = new LeaveReportHeaderDTO();
			preApproval.setmDataProp("leaveExtension");
			preApproval.setsTitle("Is Leave Extended");
			leaveHeaderDTOs.add(preApproval);
		}
		Integer custFieldCount = 1;
		for (String dataDictName : dataDictNameList) {
			LeaveReportHeaderDTO leaveHeaderDTO = new LeaveReportHeaderDTO();
			leaveHeaderDTO.setmDataProp("custField" + custFieldCount);
			leaveHeaderDTO.setsTitle(dataDictName);
			leaveHeaderDTOs.add(leaveHeaderDTO);
			custFieldCount++;
		}
		if (!leaveReportsForm.isMultipleRecord()) {
			LeaveReportHeaderDTO customFieldHeaderName1 = new LeaveReportHeaderDTO();
			customFieldHeaderName1.setmDataProp("customFieldHeaderName1");
			customFieldHeaderName1.setsTitle("Custom Field 1");
			leaveHeaderDTOs.add(customFieldHeaderName1);

			LeaveReportHeaderDTO customFieldValueName1 = new LeaveReportHeaderDTO();
			customFieldValueName1.setmDataProp("customFieldValueName1");
			customFieldValueName1.setsTitle("Custom Field 1 Value");
			leaveHeaderDTOs.add(customFieldValueName1);

			LeaveReportHeaderDTO customFieldHeaderName2 = new LeaveReportHeaderDTO();
			customFieldHeaderName2.setmDataProp("customFieldHeaderName2");
			customFieldHeaderName2.setsTitle("Custom Field 2");
			leaveHeaderDTOs.add(customFieldHeaderName2);

			LeaveReportHeaderDTO customFieldValueName2 = new LeaveReportHeaderDTO();
			customFieldValueName2.setmDataProp("customFieldValueName2");
			customFieldValueName2.setsTitle("Custom Field 2 Value");
			leaveHeaderDTOs.add(customFieldValueName2);

			LeaveReportHeaderDTO customFieldHeaderName3 = new LeaveReportHeaderDTO();
			customFieldHeaderName3.setmDataProp("customFieldHeaderName3");
			customFieldHeaderName3.setsTitle("Custom Field 3 ");
			leaveHeaderDTOs.add(customFieldHeaderName3);

			LeaveReportHeaderDTO customFieldValueName3 = new LeaveReportHeaderDTO();
			customFieldValueName3.setmDataProp("customFieldValueName3");
			customFieldValueName3.setsTitle("Custom Field 3 Value");
			leaveHeaderDTOs.add(customFieldValueName3);

			LeaveReportHeaderDTO customFieldHeaderName4 = new LeaveReportHeaderDTO();
			customFieldHeaderName4.setmDataProp("customFieldHeaderName4");
			customFieldHeaderName4.setsTitle("Custom Field 4 ");
			leaveHeaderDTOs.add(customFieldHeaderName4);

			LeaveReportHeaderDTO customFieldValueName4 = new LeaveReportHeaderDTO();
			customFieldValueName4.setmDataProp("customFieldValueName4");
			customFieldValueName4.setsTitle("Custom Field 4 Value");
			leaveHeaderDTOs.add(customFieldValueName4);

			LeaveReportHeaderDTO customFieldHeaderName5 = new LeaveReportHeaderDTO();
			customFieldHeaderName5.setmDataProp("customFieldHeaderName5");
			customFieldHeaderName5.setsTitle("Custom Field 5");
			leaveHeaderDTOs.add(customFieldHeaderName5);

			LeaveReportHeaderDTO customFieldValueName5 = new LeaveReportHeaderDTO();
			customFieldValueName5.setmDataProp("customFieldValueName5");
			customFieldValueName5.setsTitle("Custom Field 5 Value");
			leaveHeaderDTOs.add(customFieldValueName5);
		}
		leaveReportDTO.setLeaveHeaderDTOs(leaveHeaderDTOs);

		List<Object[]> customFieldObjList = customFieldReportDTO.getCustomFieldObjList();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		for (Object[] objArr : customFieldObjList) {
			customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
		}

		Set<String> leaveCustomFieldSet = new LinkedHashSet<>();

		HashMap<String, String> custFieldHashMap;
		int fieldCount = 0;
		for (LeaveTranReportDTO reportDTO : leaveReportDataDTOs) {
			custFieldHashMap = new LinkedHashMap<>();
			if (!customFieldObjList.isEmpty()) {
				int count = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(reportDTO.getEmployeeId());
				for (Object object : objArr) {
					if (count != 0) {
						custFieldHashMap.put(dataDictNameList.get(count - 1), String.valueOf(object));

						try {
							LeaveTranReportDTO.class.getMethod("setCustField" + count, String.class).invoke(reportDTO, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					count++;
				}
			}
			// Leave Custom Field
			if (reportDTO.getLeaveApplicationId() != null && !leaveReportsForm.isMultipleRecord()) {
				List<LeaveApplicationCustomField> leaveApplicationCustomFieldVOList = leaveApplicationCustomFieldDAO
						.findByCondition(reportDTO.getLeaveApplicationId());
				int tempfieldCount = 0;
				List<LeaveReportCustomDataDTO> customFieldDtoList = new ArrayList<>();
				for (LeaveApplicationCustomField customField : leaveApplicationCustomFieldVOList) {
					leaveCustomFieldSet.add(customField.getLeaveSchemeTypeCustomField().getFieldName());
					custFieldHashMap.put(customField.getLeaveSchemeTypeCustomField().getFieldName(), customField.getValue());
					try {
						LeaveTranReportDTO.class.getMethod("setCustomFieldValueName" + (tempfieldCount + 1), String.class).invoke(reportDTO,
								String.valueOf(customField.getValue()));
						LeaveTranReportDTO.class.getMethod("setCustomFieldHeaderName" + (tempfieldCount + 1), String.class).invoke(reportDTO,
								String.valueOf(customField.getLeaveSchemeTypeCustomField().getFieldName()));

					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException exception) {
						LOGGER.error(exception.getMessage(), exception);
					}
					tempfieldCount++;

				}
				reportDTO.setReportCustomDataDTOs(customFieldDtoList);
				if (tempfieldCount > fieldCount) {
					fieldCount = tempfieldCount;
				}
			}

			reportDTO.setCustFieldMap(custFieldHashMap);
		}
		if (!leaveReportsForm.isMultipleRecord()) {
			for (String leaveCustField : leaveCustomFieldSet) {
				dataDictNameList.add(leaveCustField);
			}
		}

		leaveReportDTO.setDataDictNameList(dataDictNameList);

		List<LeaveReportCustomDataDTO> customDataDTOs = new ArrayList<>();
		for (int countF = 1; countF <= fieldCount; countF++) {
			LeaveReportCustomDataDTO customFieldDto = new LeaveReportCustomDataDTO();
			customFieldDto.setCustomFieldHeaderName("Custom Field " + countF);
			customDataDTOs.add(customFieldDto);
		}
		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(leaveReportDataDTOs, beanComparator);
		leaveReportDTO.setLeaveTranReportDTOs(leaveReportDataDTOs);
		leaveReportDTO.setLeaveCustomDataDTOs(customDataDTOs);
		return leaveReportDTO;
	}

	@Override
	public LeaveReportDTO showDayWiseLeaveTranReport(Long companyId, Long employeeId, LeaveReportsForm leaveReportsForm, Boolean isManager,
			String[] dataDictionaryIds) {

		LeaveReportDTO leaveReportDTO = new LeaveReportDTO();

		Company companyVO = companyDAO.findById(companyId);

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);

		boolean leavePreferencePreApproval = leavePreference.isPreApprovalRequired();
		boolean leaveExtension = leavePreference.isLeaveExtensionRequired();

		leaveReportDTO.setLeavePreferencePreApproval(leavePreferencePreApproval);
		leaveReportDTO.setLeaveExtensionPreference(leaveExtension);

		List<Long> empIdsList = new ArrayList<>();
		String employeeIds = "";
		if (isManager) {
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setStatus(leaveReportsForm.isIncludeResignedEmployees());
			List<Tuple> employeeIdsList = employeeLeaveReviewerDAO.getEmployeeIdsTupleForManager(companyId, employeeId, employeeShortListDTO);
			for (Tuple employeeTuple : employeeIdsList) {
				empIdsList.add(employeeTuple.get(getAlias(Employee_.employeeId), Long.class));
			}

			StringBuilder builder = new StringBuilder();
			for (Long empId : empIdsList) {
				builder = builder.append(String.valueOf(empId));
				builder = builder.append(",");

			}
			employeeIds = builder.toString();
		} else {
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

			List<BigInteger> generateEmpIds = null;
			List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
			List<BigInteger> reportShortListEmployeeIds = null;
			if (leaveReportsForm.getIsShortList()) {
				EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId, leaveReportsForm.getMetaData());

				reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

				if (employeeShortListDTO.getEmployeeShortList()) {
					reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
				}
			}

			if (leaveReportsForm.getIsShortList()) {
				generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
			} else {
				generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
			}
			StringBuilder builder = new StringBuilder();
			for (BigInteger empId : generateEmpIds) {
				builder = builder.append(String.valueOf(empId));
				builder = builder.append(",");

			}
			employeeIds = builder.toString();

		}
		Set<Long> empIdSet = new HashSet<Long>();

		String leaveTypeList = "";
		StringBuilder leaveTypeBuilder = new StringBuilder();
		for (Long leaveTypeId : leaveReportsForm.getMultipleLeaveTypeId()) {
			if (leaveTypeId != null) {
				leaveTypeBuilder = leaveTypeBuilder.append(String.valueOf(leaveTypeId));
				leaveTypeBuilder = leaveTypeBuilder.append(",");
			}

		}
		leaveTypeList = leaveTypeBuilder.toString();

		String leaveTransactionList = "";
		List<String> leaveTranList = new ArrayList<String>();
		StringBuilder leaveTransactionBuilder = new StringBuilder();
		for (String leaveTransaction : leaveReportsForm.getMultipleLeaveTransactionName()) {
			if (!leaveTransaction.equalsIgnoreCase("0")) {
				leaveTransactionBuilder = leaveTransactionBuilder.append(leaveTransaction);
				leaveTranList.add(leaveTransaction);
				leaveTransactionBuilder = leaveTransactionBuilder.append(",");
			}

		}
		leaveTransactionList = leaveTransactionBuilder.toString();
		leaveTransactionList = StringUtils.removeEnd(leaveTransactionBuilder.toString(), ",");

		List<DayWiseLeaveTranReportDTO> leaveReportDataDTOs = null;
		if (leaveReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds) && !isManager) {
			leaveReportDataDTOs = new ArrayList<DayWiseLeaveTranReportDTO>();
		} else {
			leaveReportDataDTOs = employeeLeaveSchemeTypeHistoryDAO.getDayWiseLeaveTransactionReportProc(companyId, employeeIds,
					leaveReportsForm.getStartDate(), leaveReportsForm.getEndDate(), leaveTypeList, leaveTransactionList, true, false,
					leaveReportsForm.isIncludeResignedEmployees(), companyVO.getDateFormat(), isManager, leaveReportsForm.getLeaveReviewerId(),
					leavePreferencePreApproval, leaveExtension);
		}

		for (DayWiseLeaveTranReportDTO leaveTranReportDTO : leaveReportDataDTOs) {
			empIdSet.add(leaveTranReportDTO.getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}

		}

		CustomFieldReportDTO customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList, companyId,
				false);

		List<String> dataDictNameList = customFieldReportDTO.getDataDictNameList();

		List<Object[]> customFieldObjList = customFieldReportDTO.getCustomFieldObjList();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		for (Object[] objArr : customFieldObjList) {
			customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
		}

		Set<String> leaveCustomFieldSet = new LinkedHashSet<>();

		HashMap<String, String> custFieldHashMap;
		int fieldCount = 0;
		for (DayWiseLeaveTranReportDTO reportDTO : leaveReportDataDTOs) {
			custFieldHashMap = new LinkedHashMap<>();
			if (!customFieldObjList.isEmpty()) {
				int count = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(reportDTO.getEmployeeId());
				for (Object object : objArr) {
					if (count != 0) {
						custFieldHashMap.put(dataDictNameList.get(count - 1), String.valueOf(object));

						try {
							DayWiseLeaveTranReportDTO.class.getMethod("setCustField" + count, String.class).invoke(reportDTO, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					count++;
				}
			}
			// Leave Custom Field
			if (reportDTO.getLeaveApplicationId() != null && !leaveReportsForm.isMultipleRecord()) {
				List<LeaveApplicationCustomField> leaveApplicationCustomFieldVOList = leaveApplicationCustomFieldDAO
						.findByCondition(reportDTO.getLeaveApplicationId());
				int tempfieldCount = 0;
				List<LeaveReportCustomDataDTO> customFieldDtoList = new ArrayList<>();
				for (LeaveApplicationCustomField customField : leaveApplicationCustomFieldVOList) {
					leaveCustomFieldSet.add(customField.getLeaveSchemeTypeCustomField().getFieldName());
					custFieldHashMap.put(customField.getLeaveSchemeTypeCustomField().getFieldName(), customField.getValue());
					try {
						DayWiseLeaveTranReportDTO.class.getMethod("setCustomFieldValueName" + (tempfieldCount + 1), String.class).invoke(reportDTO,
								String.valueOf(customField.getValue()));
						DayWiseLeaveTranReportDTO.class.getMethod("setCustomFieldHeaderName" + (tempfieldCount + 1), String.class).invoke(reportDTO,
								String.valueOf(customField.getLeaveSchemeTypeCustomField().getFieldName()));

					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException exception) {
						LOGGER.error(exception.getMessage(), exception);
					}
					tempfieldCount++;

				}
				reportDTO.setReportCustomDataDTOs(customFieldDtoList);
				if (tempfieldCount > fieldCount) {
					fieldCount = tempfieldCount;
				}
			}

			reportDTO.setCustFieldMap(custFieldHashMap);
		}
		if (!leaveReportsForm.isMultipleRecord()) {
			for (String leaveCustField : leaveCustomFieldSet) {
				dataDictNameList.add(leaveCustField);
			}
		}

		leaveReportDTO.setDataDictNameList(dataDictNameList);

		List<LeaveReportCustomDataDTO> customDataDTOs = new ArrayList<>();
		for (int countF = 1; countF <= fieldCount; countF++) {
			LeaveReportCustomDataDTO customFieldDto = new LeaveReportCustomDataDTO();
			customFieldDto.setCustomFieldHeaderName("Custom Field " + countF);
			customDataDTOs.add(customFieldDto);
		}
		Map<Date, List<DayWiseLeaveTranReportDTO>> dayWiseLeaveTranMap = new LinkedHashMap<Date, List<DayWiseLeaveTranReportDTO>>();
		Calendar startDateCal = new GregorianCalendar();
		Date startDate = DateUtils.stringToDate(leaveReportsForm.getStartDate(), companyVO.getDateFormat());
		Date endDate = DateUtils.stringToDate(leaveReportsForm.getEndDate(), companyVO.getDateFormat());

		// Day Wise
		while (!startDate.equals(endDate)) {
			for (DayWiseLeaveTranReportDTO reportDTO : leaveReportDataDTOs) {
				if (startDate.equals(DateUtils.stringToDate(reportDTO.getLeaveDate(), companyVO.getDateFormat()))) {
					List<DayWiseLeaveTranReportDTO> dayWiseLeaveTranList = new ArrayList<>();
					if (dayWiseLeaveTranMap.containsKey(startDate)) {
						dayWiseLeaveTranList = dayWiseLeaveTranMap.get(startDate);
						dayWiseLeaveTranList.add(reportDTO);
						dayWiseLeaveTranMap.put(startDate, dayWiseLeaveTranList);
					} else {
						dayWiseLeaveTranList.add(reportDTO);
						dayWiseLeaveTranMap.put(startDate, dayWiseLeaveTranList);
					}
				}
			}
			// Increment Date by one day
			startDateCal.setTime(startDate);
			startDateCal.add(Calendar.DATE, 1);
			startDate = startDateCal.getTime();
		}
		if (startDate.equals(endDate)) {
			for (DayWiseLeaveTranReportDTO reportDTO : leaveReportDataDTOs) {
				if (startDate.equals(DateUtils.stringToDate(reportDTO.getLeaveDate(), companyVO.getDateFormat()))) {
					List<DayWiseLeaveTranReportDTO> dayWiseLeaveTranList = new ArrayList<>();
					if (dayWiseLeaveTranMap.containsKey(startDate)) {
						dayWiseLeaveTranList = dayWiseLeaveTranMap.get(startDate);
						dayWiseLeaveTranList.add(reportDTO);
						dayWiseLeaveTranMap.put(startDate, dayWiseLeaveTranList);
					} else {
						dayWiseLeaveTranList.add(reportDTO);
						dayWiseLeaveTranMap.put(startDate, dayWiseLeaveTranList);
					}
				}
			}
			// Increment Date by one day
			startDateCal.setTime(startDate);
			startDateCal.add(Calendar.DATE, 1);
			startDate = startDateCal.getTime();
		}

		Map<Date, List<DayWiseLeaveTranReportDTO>> sortedMap = new TreeMap<Date, List<DayWiseLeaveTranReportDTO>>(dayWiseLeaveTranMap);
		dayWiseLeaveTranMap = new LinkedHashMap<Date, List<DayWiseLeaveTranReportDTO>>(sortedMap);
		Map<String, List<DayWiseLeaveTranReportDTO>> dayWiseLeaveTranFinalMap = new LinkedHashMap<>();
		SimpleDateFormat sdfSource = new SimpleDateFormat(companyVO.getDateFormat());
		int counter = 1;
		Set<Date> empListDeptKeySet = dayWiseLeaveTranMap.keySet();
		Iterator<Date> iterator = empListDeptKeySet.iterator();
		while (iterator.hasNext()) {
			Date key = iterator.next();
			List<DayWiseLeaveTranReportDTO> dayWiseLeaveTran1List = new ArrayList<>();
			List<DayWiseLeaveTranReportDTO> dayWiseLeaveTranList = dayWiseLeaveTranMap.get(key);
			DayWiseLeaveTranReportDTO leaveTranReportDTO = null;
			for (int i = 0; i < dayWiseLeaveTranList.size(); i++) {
				leaveTranReportDTO = dayWiseLeaveTranList.get(i);
				leaveTranReportDTO.setCounter(String.valueOf(counter));
				dayWiseLeaveTran1List.add(leaveTranReportDTO);
				counter++;
				leaveTranReportDTO = null;

			}
			dayWiseLeaveTranFinalMap.put(sdfSource.format(key), dayWiseLeaveTran1List);
		}

		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(leaveReportDataDTOs, beanComparator);
		leaveReportDTO.setDayWiseLeaveTranMap(dayWiseLeaveTranFinalMap);
		leaveReportDTO.setLeaveCustomDataDTOs(customDataDTOs);
		return leaveReportDTO;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();

		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}

		return employeeName;
	}

	private String getEmployeeName(String firstName, String lastName) {
		String employeeName = "";
		if (StringUtils.isNotBlank(firstName)) {
			employeeName = employeeName + firstName;
		}
		if (StringUtils.isNotBlank(lastName)) {
			employeeName = employeeName + " " + lastName;
		}
		return employeeName;
	}

	@Override
	public LeaveReportDTO showLeaveBalanceAsOnDateReport(Long companyId, LeaveReportsForm leaveReportsForm, Long employeeId, String[] dataDictionaryIds) {
		LeaveReportDTO leaveReportDTO = new LeaveReportDTO();
		List<LeaveReportHeaderDTO> leaveHeaderDTOs = new ArrayList<>();
		LeaveReportHeaderDTO employeeNumber = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO firstName = new LeaveReportHeaderDTO();
		LeaveReportHeaderDTO lastName = new LeaveReportHeaderDTO();

		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");
		leaveHeaderDTOs.add(employeeNumber);
		firstName.setmDataProp("firstName");
		firstName.setsTitle("First Name");
		leaveHeaderDTOs.add(firstName);
		lastName.setmDataProp("lastName");
		lastName.setsTitle("Last Name");
		leaveHeaderDTOs.add(lastName);

		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}

		}

		List<BigInteger> generateEmpIds = null;
		EmployeeShortListDTO companyShortList = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		if (companyShortList.getEmployeeShortList()) {
			leaveReportsForm.setIsAllEmployees(false);
		} else {
			leaveReportsForm.setIsAllEmployees(true);
		}

		List<BigInteger> companyShortListEmployeeIds = companyShortList.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (leaveReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId, leaveReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (companyShortList.getEmployeeShortList()) {
				leaveReportsForm.setIsAllEmployees(false);
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		String employeeIds = "";
		if (leaveReportsForm.getIsShortList()) {
			leaveReportsForm.setIsAllEmployees(false);
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}
		StringBuilder builder = new StringBuilder();
		for (BigInteger empId : generateEmpIds) {
			builder = builder.append(String.valueOf(empId));
			builder = builder.append(",");

		}
		employeeIds = builder.toString();

		if (StringUtils.isNotBlank(leaveReportsForm.getEmployeeIds())) {
			leaveReportsForm.setIsAllEmployees(false);
			employeeIds = leaveReportsForm.getEmployeeIds();
		}

		List<EmployeeAsOnLeaveDTO> empAsOnLeaveDTOs = null;
		if (leaveReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds)) {
			empAsOnLeaveDTOs = new ArrayList<EmployeeAsOnLeaveDTO>();
		} else {
			empAsOnLeaveDTOs = employeeLeaveSchemeTypeDAO.findEmpLeavesAsOnDate(companyId, employeeIds, leaveReportsForm.getIsAllEmployees(),
					leaveReportsForm.getLeaveBalAsOnDate());
		}

		Set<String> leaveTypeNames = new TreeSet<>();
		HashMap<String, Integer> leaveTypeHashMap = new HashMap<>();
		Set<Long> empIdSet = new HashSet<Long>();
		for (EmployeeAsOnLeaveDTO empAsOnLeaveDTO : empAsOnLeaveDTOs) {
			leaveTypeNames.add(empAsOnLeaveDTO.getLeaveTypeName());
			empIdSet.add(empAsOnLeaveDTO.getEmployeeId());
		}

		List<Long> employeeIdsList = new ArrayList<>(empIdSet);

		leaveReportDTO.setLeaveTypeNames(new ArrayList<String>(leaveTypeNames));

		CustomFieldReportDTO customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList, companyId,
				false);

		List<String> dataDictNameList = customFieldReportDTO.getDataDictNameList();
		leaveReportDTO.setDataDictNameList(dataDictNameList);
		Integer custFieldCount = 1;
		for (String dataDictName : dataDictNameList) {
			LeaveReportHeaderDTO leaveHeaderDTO = new LeaveReportHeaderDTO();
			leaveHeaderDTO.setmDataProp("custField" + custFieldCount);
			leaveHeaderDTO.setsTitle(dataDictName);
			leaveHeaderDTOs.add(leaveHeaderDTO);
			custFieldCount++;
		}

		Integer leaveTypeCount = 1;
		for (String leaveTypeName : leaveTypeNames) {
			LeaveReportHeaderDTO leaveHeaderDTO = new LeaveReportHeaderDTO();
			leaveHeaderDTO.setmDataProp("leaveType" + String.valueOf(leaveTypeCount));
			leaveHeaderDTO.setsTitle(leaveTypeName);
			leaveHeaderDTOs.add(leaveHeaderDTO);
			leaveTypeHashMap.put(leaveTypeName, leaveTypeCount);
			leaveTypeCount++;
		}
		List<LeaveBalAsOnDayDTO> leaveBalAsOnDayDTOs = new ArrayList<>();
		LeaveBalAsOnDayDTO leaveBalAsOnDayDTO = null;
		HashMap<String, String> leaveTypeBalance = null;
		Long empId = 0L;
		for (EmployeeAsOnLeaveDTO employeeAsOnLeaveDTO : empAsOnLeaveDTOs) {

			if (!empId.equals(employeeAsOnLeaveDTO.getEmployeeId())) {
				leaveBalAsOnDayDTO = new LeaveBalAsOnDayDTO();
				leaveTypeBalance = new HashMap<>();
				leaveBalAsOnDayDTO.setEmployeeId(employeeAsOnLeaveDTO.getEmployeeId());
				leaveBalAsOnDayDTO.setEmployeeNo(employeeAsOnLeaveDTO.getEmployeeNumber());
				leaveBalAsOnDayDTO.setFirstName(employeeAsOnLeaveDTO.getFirstName());
				leaveBalAsOnDayDTO.setLastName(employeeAsOnLeaveDTO.getLastName());
				leaveTypeBalance.put(employeeAsOnLeaveDTO.getLeaveTypeName(), String.valueOf(employeeAsOnLeaveDTO.getBalance()));

				leaveBalAsOnDayDTO.setLeaveTypeName(employeeAsOnLeaveDTO.getLeaveTypeName());

				try {
					LeaveBalAsOnDayDTO.class.getMethod("setLeaveType" + leaveTypeHashMap.get(employeeAsOnLeaveDTO.getLeaveTypeName()), String.class)
							.invoke(leaveBalAsOnDayDTO, String.valueOf(employeeAsOnLeaveDTO.getBalance()));
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
				leaveBalAsOnDayDTO.setLeaveTypeBalance(leaveTypeBalance);

				leaveBalAsOnDayDTOs.add(leaveBalAsOnDayDTO);
			} else {

				leaveBalAsOnDayDTO.setLeaveTypeName(employeeAsOnLeaveDTO.getLeaveTypeName());

				leaveBalAsOnDayDTO.getLeaveTypeBalance().put(employeeAsOnLeaveDTO.getLeaveTypeName(), String.valueOf(employeeAsOnLeaveDTO.getBalance()));

				try {
					LeaveBalAsOnDayDTO.class.getMethod("setLeaveType" + leaveTypeHashMap.get(employeeAsOnLeaveDTO.getLeaveTypeName()), String.class)
							.invoke(leaveBalAsOnDayDTO, String.valueOf(employeeAsOnLeaveDTO.getBalance()));
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException exception) {
					LOGGER.error(exception.getMessage(), exception);
				}

			}

			empId = employeeAsOnLeaveDTO.getEmployeeId();

		}

		List<Object[]> customFieldObjList = customFieldReportDTO.getCustomFieldObjList();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		for (Object[] objArr : customFieldObjList) {
			customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
		}
		HashMap<String, String> custFieldHashMap;
		if (!customFieldObjList.isEmpty()) {
			for (LeaveBalAsOnDayDTO reportDTO : leaveBalAsOnDayDTOs) {
				int count = 0;
				custFieldHashMap = new HashMap<>();
				Object[] objArr = customFieldObjByEmpIdMap.get(reportDTO.getEmployeeId());
				for (Object object : objArr) {
					if (count != 0) {
						custFieldHashMap.put(dataDictNameList.get(count - 1), String.valueOf(object));

						try {
							LeaveBalAsOnDayDTO.class.getMethod("setCustField" + count, String.class).invoke(reportDTO, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					count++;
				}
				reportDTO.setCustFieldMap(custFieldHashMap);
			}
		}

		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(leaveBalAsOnDayDTOs, beanComparator);
		leaveReportDTO.setLeaveBalAsOnDayDTOs(leaveBalAsOnDayDTOs);
		leaveReportDTO.setLeaveHeaderDTOs(leaveHeaderDTOs);

		return leaveReportDTO;
	}

	@Override
	public LeaveReportDTO showLeaveReviewerReport(Long companyId, LeaveReportsForm leaveReportsForm, Long employeeId) {
		LeaveReportDTO leaveReportDTO = new LeaveReportDTO();
		List<LeaveReportHeaderDTO> leaveHeaderDTOs = new ArrayList<>();

		LeaveReportHeaderDTO employeeNumber = new LeaveReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");

		LeaveReportHeaderDTO employeeName = new LeaveReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");

		LeaveReportHeaderDTO leaveSchemeName = new LeaveReportHeaderDTO();
		leaveSchemeName.setmDataProp("leaveSchemeName");
		leaveSchemeName.setsTitle("Leave Scheme");

		LeaveReportHeaderDTO leaveTypeName = new LeaveReportHeaderDTO();
		leaveTypeName.setmDataProp("leaveTypeName");
		leaveTypeName.setsTitle("Leave Type");

		LeaveReportHeaderDTO rev1EmpNo = new LeaveReportHeaderDTO();
		rev1EmpNo.setmDataProp("reviewer1EmployeeNo");
		rev1EmpNo.setsTitle("Reviewer 1 Employee Number");

		LeaveReportHeaderDTO reviewer1EmployeeName = new LeaveReportHeaderDTO();
		reviewer1EmployeeName.setmDataProp("reviewer1EmployeeName");
		reviewer1EmployeeName.setsTitle("Reviewer 1 Employee Name");

		LeaveReportHeaderDTO reviewer1Email = new LeaveReportHeaderDTO();
		reviewer1Email.setmDataProp("reviewer1Email");
		reviewer1Email.setsTitle("Reviewer 1 Email");

		LeaveReportHeaderDTO rev2EmpNo = new LeaveReportHeaderDTO();
		rev2EmpNo.setmDataProp("reviewer2EmployeeNo");
		rev2EmpNo.setsTitle("Reviewer 2 Employee Number");

		LeaveReportHeaderDTO reviewer2EmployeeName = new LeaveReportHeaderDTO();
		reviewer2EmployeeName.setmDataProp("reviewer2EmployeeName");
		reviewer2EmployeeName.setsTitle("Reviewer 2 Employee Name");

		LeaveReportHeaderDTO reviewer2Email = new LeaveReportHeaderDTO();
		reviewer2Email.setmDataProp("reviewer2Email");
		reviewer2Email.setsTitle("Reviewer 2 Email");

		LeaveReportHeaderDTO rev3EmpNo = new LeaveReportHeaderDTO();
		rev3EmpNo.setmDataProp("reviewer3EmployeeNo");
		rev3EmpNo.setsTitle("Reviewer 3 Employee Number");

		LeaveReportHeaderDTO reviewer3EmployeeName = new LeaveReportHeaderDTO();
		reviewer3EmployeeName.setmDataProp("reviewer3EmployeeName");
		reviewer3EmployeeName.setsTitle("Reviewer 3 Employee Name");

		LeaveReportHeaderDTO reviewer3Email = new LeaveReportHeaderDTO();
		reviewer3Email.setmDataProp("reviewer3Email");
		reviewer3Email.setsTitle("Reviewer 3 Email");

		leaveHeaderDTOs.add(employeeNumber);
		leaveHeaderDTOs.add(employeeName);
		leaveHeaderDTOs.add(leaveSchemeName);
		leaveHeaderDTOs.add(leaveTypeName);
		leaveHeaderDTOs.add(rev1EmpNo);
		leaveHeaderDTOs.add(reviewer1EmployeeName);
		leaveHeaderDTOs.add(reviewer1Email);
		leaveHeaderDTOs.add(rev2EmpNo);
		leaveHeaderDTOs.add(reviewer2EmployeeName);
		leaveHeaderDTOs.add(reviewer2Email);
		leaveHeaderDTOs.add(rev3EmpNo);
		leaveHeaderDTOs.add(reviewer3EmployeeName);
		leaveHeaderDTOs.add(reviewer3Email);

		List<BigInteger> generateEmpIds = null;
		EmployeeShortListDTO companyShortList = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		if (companyShortList.getEmployeeShortList()) {
			leaveReportsForm.setIsAllEmployees(false);
		} else {
			leaveReportsForm.setIsAllEmployees(true);
		}
		List<BigInteger> companyShortListEmployeeIds = companyShortList.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (leaveReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId, leaveReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (companyShortList.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
				leaveReportsForm.setIsAllEmployees(false);
			}

		}

		String employeeIds = "";

		if (leaveReportsForm.getIsShortList()) {
			leaveReportsForm.setIsAllEmployees(false);
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);

		}
		StringBuilder builder = new StringBuilder();
		for (BigInteger empId : generateEmpIds) {
			builder = builder.append(String.valueOf(empId));
			builder = builder.append(",");

		}
		employeeIds = builder.toString();

		if (StringUtils.isNotBlank(leaveReportsForm.getEmployeeIds())) {

			leaveReportsForm.setIsAllEmployees(false);
			employeeIds = leaveReportsForm.getEmployeeIds();

		}

		List<EmployeeAsOnLeaveDTO> empAsOnLeaveDTOs = null;
		if (leaveReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds)) {
			empAsOnLeaveDTOs = new ArrayList<EmployeeAsOnLeaveDTO>();
		} else {
			empAsOnLeaveDTOs = employeeLeaveSchemeTypeDAO.findLeaveReviewerReportData(companyId, employeeIds, leaveReportsForm.getIsAllEmployees());
		}

		List<LeaveReviewerReportDTO> leaveReviewerReportDTOs = new ArrayList<>();
		for (EmployeeAsOnLeaveDTO employeeAsOnLeaveDTO : empAsOnLeaveDTOs) {
			LeaveReviewerReportDTO leaveReviewerReportDTO = new LeaveReviewerReportDTO();
			leaveReviewerReportDTO.setEmployeeNo(employeeAsOnLeaveDTO.getEmployeeNumber());
			leaveReviewerReportDTO.setEmployeeName(getEmployeeName(employeeAsOnLeaveDTO.getFirstName(), employeeAsOnLeaveDTO.getLastName()));
			leaveReviewerReportDTO.setLastName(employeeAsOnLeaveDTO.getLastName() == null ? "" : employeeAsOnLeaveDTO.getLastName());
			leaveReviewerReportDTO.setLeaveSchemeName(employeeAsOnLeaveDTO.getLeaveSchemeName());
			leaveReviewerReportDTO.setLeaveTypeName(employeeAsOnLeaveDTO.getLeaveTypeName());
			leaveReviewerReportDTO
					.setReviewer1EmployeeNo(employeeAsOnLeaveDTO.getReviewer1EmployeeNo() == null ? "" : employeeAsOnLeaveDTO.getReviewer1EmployeeNo());
			leaveReviewerReportDTO
					.setReviewer1EmployeeName(getEmployeeName(employeeAsOnLeaveDTO.getReviewer1FirstName(), employeeAsOnLeaveDTO.getReviewer1LastName()));
			leaveReviewerReportDTO.setReviewer1Email(employeeAsOnLeaveDTO.getReviewer1Email() == null ? "" : employeeAsOnLeaveDTO.getReviewer1Email());
			leaveReviewerReportDTO
					.setReviewer2EmployeeNo(employeeAsOnLeaveDTO.getReviewer2EmployeeNo() == null ? "" : employeeAsOnLeaveDTO.getReviewer2EmployeeNo());
			leaveReviewerReportDTO
					.setReviewer2EmployeeName(getEmployeeName(employeeAsOnLeaveDTO.getReviewer2FirstName(), employeeAsOnLeaveDTO.getReviewer2LastName()));
			leaveReviewerReportDTO.setReviewer2Email(employeeAsOnLeaveDTO.getReviewer2Email() == null ? "" : employeeAsOnLeaveDTO.getReviewer2Email());
			leaveReviewerReportDTO
					.setReviewer3EmployeeNo(employeeAsOnLeaveDTO.getReviewer3EmployeeNo() == null ? "" : employeeAsOnLeaveDTO.getReviewer3EmployeeNo());
			leaveReviewerReportDTO
					.setReviewer3EmployeeName(getEmployeeName(employeeAsOnLeaveDTO.getReviewer3FirstName(), employeeAsOnLeaveDTO.getReviewer3LastName()));
			leaveReviewerReportDTO.setReviewer3Email(employeeAsOnLeaveDTO.getReviewer3Email() == null ? "" : employeeAsOnLeaveDTO.getReviewer3Email());

			leaveReviewerReportDTOs.add(leaveReviewerReportDTO);

		}
		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(leaveReviewerReportDTOs, beanComparator);
		leaveReportDTO.setLeaveReviewerReportDTOs(leaveReviewerReportDTOs);
		leaveReportDTO.setLeaveHeaderDTOs(leaveHeaderDTOs);

		return leaveReportDTO;
	}

	@Override
	public LeaveReportDTO showYearWiseSummaryReport(Long employeeId, Long companyId, LeaveReportsForm leaveReportsForm, Boolean isManager,
			String[] dataDictionaryIds) {
		LeaveReportDTO leaveReportDTO = new LeaveReportDTO();
		List<LeaveReportHeaderDTO> leaveHeaderDTOs = new ArrayList<>();

		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}

		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		String employeeIds = "";

		if (isManager) {
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setStatus(leaveReportsForm.isIncludeResignedEmployees());
			List<Tuple> employeeIdsList = employeeLeaveReviewerDAO.getEmployeeIdsTupleForManager(companyId, employeeId, employeeShortListDTO);

			StringBuilder builder = new StringBuilder();
			for (Tuple employeeTuple : employeeIdsList) {
				builder = builder.append(employeeTuple.get(getAlias(Employee_.employeeId), Long.class));
				builder = builder.append(",");

			}
			employeeIds = builder.toString();

		} else {

			List<BigInteger> generateEmpIds = null;
			List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
			List<BigInteger> reportShortListEmployeeIds = null;
			if (leaveReportsForm.getIsShortList()) {
				EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId, leaveReportsForm.getMetaData());

				reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

				if (employeeShortListDTO.getEmployeeShortList()) {
					reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
				}
			}

			if (leaveReportsForm.getIsShortList()) {
				generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
			} else {
				generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
			}
			StringBuilder builder = new StringBuilder();
			for (BigInteger empId : generateEmpIds) {
				builder = builder.append(String.valueOf(empId));
				builder = builder.append(",");

			}
			employeeIds = builder.toString();

		}
		Company companyVO = companyDAO.findById(companyId);
		EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO = new EmployeeLeaveSchemeTypeDTO();
		if (StringUtils.isBlank(leaveReportsForm.getStartDate())) {
			leaveBalanceSummaryLogic.createLeaveCalendarDate(leaveReportsForm.getReportYear(), companyId, employeeLeaveSchemeTypeDTO);
			leaveReportsForm
					.setStartDate(DateUtils.timeStampToString(employeeLeaveSchemeTypeDTO.getStartDateTime(), PayAsiaConstants.DEFAULT_DATE_FORMAT));
			leaveReportsForm.setEndDate(DateUtils.timeStampToString(employeeLeaveSchemeTypeDTO.getEndDateTime(), PayAsiaConstants.DEFAULT_DATE_FORMAT));
		} else {
			leaveReportsForm
					.setStartDate(DateUtils.convertDate(leaveReportsForm.getStartDate(), companyVO.getDateFormat(), PayAsiaConstants.DEFAULT_DATE_FORMAT));
			leaveReportsForm
					.setEndDate(DateUtils.convertDate(leaveReportsForm.getEndDate(), companyVO.getDateFormat(), PayAsiaConstants.DEFAULT_DATE_FORMAT));
		}

		String leaveTypeList = "";
		StringBuilder leaveTypeBuilder = new StringBuilder();
		for (Long leaveTypeId : leaveReportsForm.getMultipleLeaveTypeId()) {
			if (leaveTypeId != null) {
				leaveTypeBuilder = leaveTypeBuilder.append(String.valueOf(leaveTypeId));
				leaveTypeBuilder = leaveTypeBuilder.append(",");
			}
		}
		leaveTypeList = StringUtils.removeEnd(leaveTypeBuilder.toString(), ",");

		List<YearWiseSummarryDTO> yearWiseSummaryDTOs = null;
		if (leaveReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds) && !isManager) {
			yearWiseSummaryDTOs = new ArrayList<YearWiseSummarryDTO>();
		} else {
			yearWiseSummaryDTOs = employeeLeaveSchemeTypeDAO.findYearWiseSummaryReport(companyId, leaveReportsForm.getReportYear(),
					leaveReportsForm.getStartDate(), leaveReportsForm.getEndDate(), leaveTypeList, employeeIds, isManager,
					leaveReportsForm.isIncludeResignedEmployees(), PayAsiaConstants.DEFAULT_DATE_FORMAT, leaveReportsForm.getLeaveReviewerId());
		}

		Set<String> leaveTypeNames = new TreeSet<>();
		Set<Long> empIdSet = new HashSet<Long>();

		for (YearWiseSummarryDTO yearWiseSummarryDTO : yearWiseSummaryDTOs) {
			leaveTypeNames.add(yearWiseSummarryDTO.getLeaveTypeName());
			empIdSet.add(yearWiseSummarryDTO.getEmployeeId());

		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		leaveReportDTO.setLeaveTypeNames(new ArrayList<String>(leaveTypeNames));

		CustomFieldReportDTO customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList, companyId,
				false);

		List<String> dataDictNameList = customFieldReportDTO.getDataDictNameList();
		leaveReportDTO.setDataDictNameList(dataDictNameList);

		List<YearWiseSummarryDTO> summarryDTOs = new ArrayList<>();
		YearWiseSummarryDTO yearWiseSummarryDTO = null;
		Long empId = 0L;

		HashMap<String, String> transactions = null;
		for (YearWiseSummarryDTO summarryDTO : yearWiseSummaryDTOs) {

			if (!empId.equals(summarryDTO.getEmployeeId())) {

				transactions = new HashMap<>();
				yearWiseSummarryDTO = new YearWiseSummarryDTO();
				yearWiseSummarryDTO.setEmployeeId(summarryDTO.getEmployeeId());
				yearWiseSummarryDTO.setEmployeeNumber(summarryDTO.getEmployeeNumber());
				yearWiseSummarryDTO.setFirstName(summarryDTO.getFirstName());
				yearWiseSummarryDTO.setLastName(summarryDTO.getLastName() == null ? "" : summarryDTO.getLastName());

				transactions.put(summarryDTO.getLeaveTypeName() + "carryForward", summarryDTO.getCarryForward());

				transactions.put(summarryDTO.getLeaveTypeName() + "credited", summarryDTO.getCredited());

				transactions.put(summarryDTO.getLeaveTypeName() + "encashed", summarryDTO.getEncashed());

				transactions.put(summarryDTO.getLeaveTypeName() + "forfeited", summarryDTO.getForfeited());

				transactions.put(summarryDTO.getLeaveTypeName() + "taken", summarryDTO.getTaken());

				transactions.put(summarryDTO.getLeaveTypeName() + "closingBalance", summarryDTO.getClosingBalance());

				yearWiseSummarryDTO.setTransactions(transactions);

				summarryDTOs.add(yearWiseSummarryDTO);
			} else {

				yearWiseSummarryDTO.getTransactions().put(summarryDTO.getLeaveTypeName() + "carryForward", summarryDTO.getCarryForward());

				yearWiseSummarryDTO.getTransactions().put(summarryDTO.getLeaveTypeName() + "credited", summarryDTO.getCredited());

				yearWiseSummarryDTO.getTransactions().put(summarryDTO.getLeaveTypeName() + "taken", summarryDTO.getTaken());

				yearWiseSummarryDTO.getTransactions().put(summarryDTO.getLeaveTypeName() + "closingBalance", summarryDTO.getClosingBalance());

				yearWiseSummarryDTO.getTransactions().put(summarryDTO.getLeaveTypeName() + "encashed", summarryDTO.getEncashed());

				yearWiseSummarryDTO.getTransactions().put(summarryDTO.getLeaveTypeName() + "forfeited", summarryDTO.getForfeited());

			}

			empId = yearWiseSummarryDTO.getEmployeeId();

		}

		List<Object[]> customFieldObjList = customFieldReportDTO.getCustomFieldObjList();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		for (Object[] objArr : customFieldObjList) {
			customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
		}
		HashMap<String, String> custFieldHashMap;
		if (!customFieldObjList.isEmpty()) {
			for (YearWiseSummarryDTO reportDTO : summarryDTOs) {
				int count = 0;
				custFieldHashMap = new HashMap<>();
				Object[] objArr = customFieldObjByEmpIdMap.get(reportDTO.getEmployeeId());
				for (Object object : objArr) {
					if (count != 0) {
						custFieldHashMap.put(dataDictNameList.get(count - 1), String.valueOf(object));
					}
					count++;
				}
				reportDTO.setCustFieldMap(custFieldHashMap);
			}
		}

		BeanComparator beanComparator = new BeanComparator("employeeNumber");
		Collections.sort(summarryDTOs, beanComparator);
		leaveReportDTO.setSummarryDTOs(summarryDTOs);
		leaveReportDTO.setLeaveHeaderDTOs(leaveHeaderDTOs);

		return leaveReportDTO;
	}

	@Override
	public LeaveReportDTO genLeaveBalAsOnDayCustReportPDF(Long companyId, Long employeeId, LeaveReportsForm leaveReportsForm, boolean isManager) {
		LeaveReportDTO leaveReportDTO = new LeaveReportDTO();

		List<BigInteger> generateEmpIds = null;
		List<Long> empIdsList = new ArrayList<>();
		String employeeIds = "";
		if (isManager) {
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setStatus(leaveReportsForm.isIncludeResignedEmployees());
			List<Tuple> employeeIdsList = employeeLeaveReviewerDAO.getEmployeeIdsTupleForManager(companyId, employeeId, employeeShortListDTO);
			for (Tuple employeeTuple : employeeIdsList) {
				empIdsList.add(employeeTuple.get(getAlias(Employee_.employeeId), Long.class));
			}

			StringBuilder builder = new StringBuilder();
			for (Long empId : empIdsList) {
				builder = builder.append(String.valueOf(empId));
				builder = builder.append(",");

			}
			leaveReportsForm.setIsAllEmployees(false);
			employeeIds = builder.toString();
		} else {
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
			if (employeeShortListDTO.getEmployeeShortList()) {
				leaveReportsForm.setIsAllEmployees(false);
			} else {
				leaveReportsForm.setIsAllEmployees(true);
			}
			List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
			List<BigInteger> reportShortListEmployeeIds = null;
			if (leaveReportsForm.getIsShortList()) {
				EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId, leaveReportsForm.getMetaData());

				reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

				if (employeeShortListDTO.getEmployeeShortList()) {
					leaveReportsForm.setIsAllEmployees(false);
					reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
				}
			}

			if (leaveReportsForm.getIsShortList()) {
				leaveReportsForm.setIsAllEmployees(false);
				generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
			} else {
				generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
			}
			StringBuilder builder = new StringBuilder();
			for (BigInteger empId : generateEmpIds) {
				builder = builder.append(String.valueOf(empId));
				builder = builder.append(",");

			}
			employeeIds = builder.toString();

		}
		List<EmployeeAsOnLeaveDTO> empAsOnLeaveDTOs = null;
		if (leaveReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds) && !isManager) {
			empAsOnLeaveDTOs = new ArrayList<EmployeeAsOnLeaveDTO>();
		} else {
			empAsOnLeaveDTOs = employeeLeaveSchemeTypeDAO.findLeaveBalAsOnDateCustomEmpReport(companyId, employeeIds, leaveReportsForm.getIsAllEmployees(),
					leaveReportsForm.getLeaveBalAsOnDateCustomRep());
		}

		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				leaveReportDTO
						.setLeaveBalAsOnDayCustReportByteFile(genPDFLeaveBalAsOnDayCustReport(companyId, employeeId, empAsOnLeaveDTOs, leaveReportsForm));
				return leaveReportDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			try {
				leaveReportDTO
						.setLeaveBalAsOnDayCustReportByteFile(genPDFLeaveBalAsOnDayCustReport(companyId, employeeId, empAsOnLeaveDTOs, leaveReportsForm));
				return leaveReportDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

	}

	private byte[] genPDFLeaveBalAsOnDayCustReport(Long companyId, Long employeeId, List<EmployeeAsOnLeaveDTO> empAsOnLeaveDTOs,
			LeaveReportsForm leaveReportsForm) throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId, PAYASIA_TEMP_PATH, "LeaveBalCustReport");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PayAsiaPDFConstants.PAGE_SIZE, PayAsiaPDFConstants.PAGE_LEFT_MARGIN, PayAsiaPDFConstants.PAGE_TOP_MARGIN,
					PayAsiaPDFConstants.PAGE_RIGHT_MARGIN, PayAsiaPDFConstants.PAGE_BOTTOM_MARGIN);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

			document.open();

			leaveBalCustomEmpPerPagePDFLogic.createLeaveBalCustomEmpPerPagePDF(document, writer, 1, companyId, employeeId, empAsOnLeaveDTOs,
					leaveReportsForm);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow | PdfWriter.CenterWindow);

			document.close();

			pdfIn = new FileInputStream(tempFile);

			return IOUtils.toByteArray(pdfIn);
		} catch (DocumentException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					throw new PayAsiaSystemException(ex.getMessage(), ex);
				}
			}

			IOUtils.closeQuietly(pdfOut);
			IOUtils.closeQuietly(pdfIn);

			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new PayAsiaSystemException(ex.getMessage(), ex);
			}

		}
	}

	@Override
	public LeavePreferenceForm getLeavePreferenceDetail(Long companyId, String companyCode, boolean isAdmin) {
		LeavePreferenceForm leavePreferenceForm = new LeavePreferenceForm();
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO == null) {
			leavePreferenceForm.setShowLeaveBalanceCustomReport(false);
			leavePreferenceForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			return leavePreferenceForm;
		}

		leavePreferenceForm.setShowLeaveBalanceCustomReport(leavePreferenceVO.isShowLeaveBalanceCustomReport());
		if (leavePreferenceVO.getLeaveUnit() != null) {
			leavePreferenceForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
		}

		// Check if session company code is "angloeastern"
		// then hide "Add custom field" options for employees(Manager) to add
		// custom fields
		if (!isAdmin && companyCode.equalsIgnoreCase(PayAsiaConstants.ANGLOEASTERN_COMPANY_CODE)) {
			leavePreferenceForm.setHideCustomFieldsForAngloEasternCompany(true);
		}
		leavePreferenceForm.setLeavehideAddColumn(leavePreferenceVO.isLeaveHideAddColumn());
		return leavePreferenceForm;
	}

	@Override
	public List<EmployeeLeaveSchemeTypeDTO> getDistinctYears(Long companyId, boolean isManager) {

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		int startMonth = 0;
		int endMonth = 0;
		if (leavePreference != null) {
			startMonth = Integer.parseInt(String.valueOf(leavePreference.getStartMonth().getMonthId())) - 1;
			endMonth = Integer.parseInt(String.valueOf(leavePreference.getEndMonth().getMonthId()));

		}

		List<Integer> yearList = new ArrayList<>();

		Calendar now = Calendar.getInstance();
		Integer currentYear = now.get(Calendar.YEAR);
		if (startMonth != 1 && (now.get(Calendar.MONTH) + 1) >= startMonth) {
			currentYear = currentYear + 1;
		}

		if (isManager) {
			for (int count = 6; count >= 1; count--) {
				yearList.add(currentYear - count);
			}
		} else {
			for (int count = 5; count >= 1; count--) {
				yearList.add(currentYear - count);
			}
			for (int count = 1; count <= 5; count++) {
				yearList.add(currentYear + count);
			}
		}

		yearList.add(currentYear);
		Collections.sort(yearList);
		Set<Integer> yearSet = new LinkedHashSet<>(yearList);

		Date date = now.getTime();

		List<EmployeeLeaveSchemeTypeDTO> typeDTOList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		for (Integer year : yearSet) {
			boolean isCurrentDateInLeaveCal = false;
			EmployeeLeaveSchemeTypeDTO typeDTO = new EmployeeLeaveSchemeTypeDTO();
			Calendar startCalendar;
			if (startMonth == 0) {
				startCalendar = new GregorianCalendar(year, startMonth, 1, 0, 0, 0);
			} else {
				startCalendar = new GregorianCalendar(year - 1, startMonth, 1, 0, 0, 0);
			}

			String startDate = sdf.format(startCalendar.getTime());
			Calendar endCalendar = new GregorianCalendar(year, endMonth, 1, 0, 0, 0);
			endCalendar.add(Calendar.DAY_OF_MONTH, -1);
			String endDate = sdf.format(endCalendar.getTime());
			if (date.after(startCalendar.getTime()) && date.before(endCalendar.getTime())) {
				isCurrentDateInLeaveCal = true;
			}

			typeDTO.setYearKey(startDate + " - " + endDate);
			typeDTO.setYearValue(year);
			typeDTO.setCurrentDateInLeaveCal(isCurrentDateInLeaveCal);
			typeDTOList.add(typeDTO);
		}

		return typeDTOList;
	}

	@Override
	public List<EmployeeListForm> getLeaveReviewerList(Long employeeId, Long companyId) {
		List<EmployeeListForm> employeeListFormList = new ArrayList<>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<Tuple> employeeList = employeeLeaveReviewerDAO.getEmployeeReviewersList(companyId, employeeShortListDTO);
		for (Tuple employee : employeeList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = employee.get(getAlias(Employee_.employeeNumber), String.class) + " - ";
			employeeName += employee.get(getAlias(Employee_.firstName), String.class) + " ";
			if (StringUtils.isNotBlank(employee.get(getAlias(Employee_.lastName), String.class))) {
				employeeName += employee.get(getAlias(Employee_.lastName), String.class);
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.get(getAlias(Employee_.employeeNumber), String.class));

			employeeForm.setEmployeeID(employee.get(getAlias(Employee_.employeeId), Long.class));
			employeeListFormList.add(employeeForm);
		}
		return employeeListFormList;

	}

	@Override
	public LeaveReportDTO genLeaveHeadcountReport(Long employeeId, Long companyId, LeaveReportsForm leaveReportsForm, Boolean isManager) {
		LeaveReportDTO leaveReportDTO = new LeaveReportDTO();
		String companyIds = "";
		if (StringUtils.isBlank(leaveReportsForm.getCompanyIds())) {
			companyIds = companyId.toString();
		} else {
			companyIds = leaveReportsForm.getCompanyIds();
		}

		Employee employeeVO = employeeDAO.findById(employeeId);
		Company companyVO = companyDAO.findById(companyId);
		List<EmployeeHeadCountReportDTO> employeeHeadCountReportDTOs = employeeLeaveSchemeDAO
				.getLeaveHeadCountReportDetail(leaveReportsForm.getStartDate(), leaveReportsForm.getEndDate(), companyVO.getDateFormat(), companyIds);

		Set<String> companyCodeList = new TreeSet<>();
		Set<Long> empIdSet = new HashSet<Long>();

		Map<String, List<EmployeeHeadCountReportDTO>> leaveHeadCountEmpDataListMap = new HashMap<String, List<EmployeeHeadCountReportDTO>>();
		Map<String, Set<Long>> empWithLeaveSchemeByCompanyMap = new HashMap<String, Set<Long>>();
		Map<String, Set<Long>> empWithoutLeaveSchemeByCompanyMap = new HashMap<String, Set<Long>>();
		for (EmployeeHeadCountReportDTO employeeHeadCountReportDTO : employeeHeadCountReportDTOs) {
			if (leaveHeadCountEmpDataListMap.containsKey(employeeHeadCountReportDTO.getCompanyCode())) {
				List<EmployeeHeadCountReportDTO> reportList = leaveHeadCountEmpDataListMap.get(employeeHeadCountReportDTO.getCompanyCode());
				reportList.add(employeeHeadCountReportDTO);
				leaveHeadCountEmpDataListMap.put(employeeHeadCountReportDTO.getCompanyCode(), reportList);
			} else {
				List<EmployeeHeadCountReportDTO> reportList = new ArrayList<EmployeeHeadCountReportDTO>();
				reportList.add(employeeHeadCountReportDTO);
				leaveHeadCountEmpDataListMap.put(employeeHeadCountReportDTO.getCompanyCode(), reportList);
			}

			// add unique employee with leave scheme by company Code
			if (empWithLeaveSchemeByCompanyMap.containsKey(employeeHeadCountReportDTO.getCompanyCode())) {
				if (StringUtils.isNotBlank(employeeHeadCountReportDTO.getLeaveSchemeName())) {
					Set<Long> employeeSet = empWithLeaveSchemeByCompanyMap.get(employeeHeadCountReportDTO.getCompanyCode());
					employeeSet.add(employeeHeadCountReportDTO.getEmployeeId());
					empWithLeaveSchemeByCompanyMap.put(employeeHeadCountReportDTO.getCompanyCode(), employeeSet);
				}
			} else {
				if (StringUtils.isNotBlank(employeeHeadCountReportDTO.getLeaveSchemeName())) {
					Set<Long> employeeSet = new HashSet<Long>();
					employeeSet.add(employeeHeadCountReportDTO.getEmployeeId());
					empWithLeaveSchemeByCompanyMap.put(employeeHeadCountReportDTO.getCompanyCode(), employeeSet);
				}
			}

			// add unique employee without leave scheme by company Code
			if (empWithoutLeaveSchemeByCompanyMap.containsKey(employeeHeadCountReportDTO.getCompanyCode())) {
				if (StringUtils.isBlank(employeeHeadCountReportDTO.getLeaveSchemeName())) {
					Set<Long> employeeSet = empWithoutLeaveSchemeByCompanyMap.get(employeeHeadCountReportDTO.getCompanyCode());
					employeeSet.add(employeeHeadCountReportDTO.getEmployeeId());
					empWithoutLeaveSchemeByCompanyMap.put(employeeHeadCountReportDTO.getCompanyCode(), employeeSet);
				}
			} else {
				if (StringUtils.isBlank(employeeHeadCountReportDTO.getLeaveSchemeName())) {
					Set<Long> employeeSet = new HashSet<Long>();
					employeeSet.add(employeeHeadCountReportDTO.getEmployeeId());
					empWithoutLeaveSchemeByCompanyMap.put(employeeHeadCountReportDTO.getCompanyCode(), employeeSet);
				}
			}

			companyCodeList.add(employeeHeadCountReportDTO.getCompanyCode());
			empIdSet.add(employeeHeadCountReportDTO.getEmployeeId());

		}
		leaveReportDTO.setCompanyCodeList(new ArrayList<String>(companyCodeList));

		EmployeeHeadCountReportDTO empHeadCountReportDTO = null;
		String companyCode = "testCompany";

		Map<String, List<EmployeeHeadCountReportDTO>> leaveHeadCountReportDTOMap = new HashMap<String, List<EmployeeHeadCountReportDTO>>();
		for (EmployeeHeadCountReportDTO summarryDTO : employeeHeadCountReportDTOs) {

			if (!companyCode.equals(summarryDTO.getCompanyCode())) {
				empHeadCountReportDTO = new EmployeeHeadCountReportDTO();
				empHeadCountReportDTO.setCompanyName(summarryDTO.getCompanyName());
				empHeadCountReportDTO.setCountryName(summarryDTO.getCountryName());
				empHeadCountReportDTO.setFromPeriod(leaveReportsForm.getStartDate());
				empHeadCountReportDTO.setToPeriod(leaveReportsForm.getEndDate());
				empHeadCountReportDTO.setGeneratedBy(getEmployeeName(employeeVO));
				if (empWithLeaveSchemeByCompanyMap.get(summarryDTO.getCompanyCode()) != null) {
					empHeadCountReportDTO.setTotalEmployeesCount(empWithLeaveSchemeByCompanyMap.get(summarryDTO.getCompanyCode()).size());
				}
				if (empWithoutLeaveSchemeByCompanyMap.get(summarryDTO.getCompanyCode()) != null) {
					empHeadCountReportDTO.setEmployeeWithoutLeaveScheme(empWithoutLeaveSchemeByCompanyMap.get(summarryDTO.getCompanyCode()).size());
				}

				List<EmployeeHeadCountReportDTO> employeeHeadCountReportDTOList = new ArrayList<EmployeeHeadCountReportDTO>();
				employeeHeadCountReportDTOList.add(empHeadCountReportDTO);
				leaveHeadCountReportDTOMap.put(summarryDTO.getCompanyCode(), employeeHeadCountReportDTOList);
			} else {

			}
			companyCode = summarryDTO.getCompanyCode();
		}

		BeanComparator beanComparator = new BeanComparator("employeeNumber");
		Collections.sort(employeeHeadCountReportDTOs, beanComparator);
		leaveReportDTO.setLeaveHeadCountEmpDataListMap(leaveHeadCountEmpDataListMap);
		leaveReportDTO.setLeaveHeadCountHeaderDTOMap(leaveHeadCountReportDTOMap);

		return leaveReportDTO;
	}

	@Override
	public SwitchCompanyResponse getSwitchCompanyList(PageRequest pageDTO, SortCondition sortDTO, Long employeeId, String searchCondition,
			String searchText, String groupName) {
		CompanyConditionDTO conditionDTO = new CompanyConditionDTO();
		if (StringUtils.isNotBlank(searchCondition)) {
			if (searchCondition.equals(PayAsiaConstants.COMPANY_NAME)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCompanyName(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.COMPANY_CODE)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCompanyCode(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.GROUP_CODE)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setGroupCode(searchText.trim());
				}

			}
		}
		if (StringUtils.isNotBlank(groupName)) {
			conditionDTO.setGroupName(groupName.trim());
		}

		List<SwitchCompanyForm> switchCompanyFormList = new ArrayList<SwitchCompanyForm>();

		List<Tuple> companyListVO = companyDAO.findAssignCompanyToUser(null, null, employeeId, conditionDTO);
		for (Tuple companyTuple : companyListVO) {
			SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
			switchCompanyForm.setCompanyId((Long) companyTuple.get(getAlias(Company_.companyId), Long.class));
			switchCompanyForm.setCompanyName((String) companyTuple.get(getAlias(Company_.companyName), String.class));
			switchCompanyForm.setCompanyCode((String) companyTuple.get(getAlias(Company_.companyCode), String.class));
			switchCompanyForm.setGroupId((Long) companyTuple.get(getAlias(CompanyGroup_.groupId), Long.class));
			switchCompanyForm.setGroupName((String) companyTuple.get(getAlias(CompanyGroup_.groupName), String.class));
			switchCompanyForm.setGroupCode((String) companyTuple.get(getAlias(CompanyGroup_.groupCode), String.class));
			switchCompanyFormList.add(switchCompanyForm);

		}

		SwitchCompanyResponse response = new SwitchCompanyResponse();
		response.setSwitchCompanyFormList(switchCompanyFormList);

		return response;

	}

	@Override
	public SwitchCompanyResponse getOrderedCompanyList(PageRequest pageDTO, SortCondition sortDTO, Long employeeId, String searchCondition,
			String searchText, String groupName) {
		CompanyConditionDTO conditionDTO = new CompanyConditionDTO();
		if (StringUtils.isNotBlank(searchCondition)) {
			if (searchCondition.equals(PayAsiaConstants.COMPANY_NAME)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCompanyName(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.COMPANY_CODE)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCompanyCode(searchText.trim());
				}

			}
			if (searchCondition.equals(PayAsiaConstants.GROUP_CODE)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setGroupCode(searchText.trim());
				}

			}
		}
		if (StringUtils.isNotBlank(groupName)) {
			conditionDTO.setGroupName(groupName.trim());
		}

		List<SwitchCompanyForm> switchCompanyFormList = new ArrayList<SwitchCompanyForm>();
		SortCondition sortDTO1 = new SortCondition();
		sortDTO1.setColumnName(SortConstants.MANAGE_ROLES_GROUP_NAME);
		sortDTO1.setOrderType(SortConstants.DB_ORDER_BY_ASC);
		List<Tuple> companyListVO = companyDAO.findAssignCompanyToUser(sortDTO1, null, employeeId, conditionDTO);
		for (Tuple companyTuple : companyListVO) {
			SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
			switchCompanyForm.setCompanyId((Long) companyTuple.get(getAlias(Company_.companyId), Long.class));
			switchCompanyForm.setCompanyName((String) companyTuple.get(getAlias(Company_.companyName), String.class));
			switchCompanyForm.setCompanyCode((String) companyTuple.get(getAlias(Company_.companyCode), String.class));
			switchCompanyForm.setGroupId((Long) companyTuple.get(getAlias(CompanyGroup_.groupId), Long.class));
			switchCompanyForm.setGroupName((String) companyTuple.get(getAlias(CompanyGroup_.groupName), String.class));
			switchCompanyForm.setGroupCode((String) companyTuple.get(getAlias(CompanyGroup_.groupCode), String.class));
			switchCompanyFormList.add(switchCompanyForm);

		}

		SwitchCompanyResponse response = new SwitchCompanyResponse();
		response.setSwitchCompanyFormList(switchCompanyFormList);

		return response;

	}
	
	@Override
	public List<SelectOptionDTO> getLeaveReportList(Long companyId, String companyCode, boolean isAdmin) {
		List<SelectOptionDTO> leaveReportNames = new ArrayList<>();
		leaveReportNames.add(new SelectOptionDTO("LTR","Leave Transaction Report"));
		leaveReportNames.add(new SelectOptionDTO("DLTR","Day Wise Leave Transaction Report"));
		leaveReportNames.add(new SelectOptionDTO("LBD","Leave Balance As On Day"));
		leaveReportNames.add(new SelectOptionDTO("YSR","Year Wise Summary Report"));
		
		LeavePreferenceForm leavePreferenceForm = getLeavePreferenceDetail(companyId, companyCode, isAdmin);
		
		if(leavePreferenceForm.isShowLeaveBalanceCustomReport()==true){
			leaveReportNames.add(new SelectOptionDTO("LBDC1","Leave Balance As On Day(custom 1 employee/page)"));
		}
		
		return leaveReportNames;
	}
	
}
