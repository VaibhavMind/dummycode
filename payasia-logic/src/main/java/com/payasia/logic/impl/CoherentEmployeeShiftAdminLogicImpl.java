package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.CoherentOvertimeDetailReportDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmailDataDTO;
import com.payasia.common.dto.LeaveReviewFormDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.CoherentShiftDetailForm;
import com.payasia.common.form.CoherentShiftDetailFormResponse;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CoherentShiftApplicationDAO;
import com.payasia.dao.CoherentShiftApplicationDetailDAO;
import com.payasia.dao.CoherentShiftApplicationReviewerDAO;
import com.payasia.dao.CoherentShiftApplicationWorkflowDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LundinAFEDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.TimesheetWorkflowDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationDetail;
import com.payasia.dao.bean.CoherentShiftApplicationReviewer;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.CoherentEmployeeShiftAdminLogic;
import com.payasia.logic.CoherentShiftPrintPDFLogic;
import com.payasia.logic.CoherentTimesheetMailLogic;
import com.payasia.logic.CoherentTimesheetReportsLogic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LionTimesheetReportsLogic;
import com.payasia.logic.LundinTimesheetMailLogic;

@Component
public class CoherentEmployeeShiftAdminLogicImpl extends BaseLogic implements
		CoherentEmployeeShiftAdminLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CoherentEmployeeShiftAdminLogicImpl.class);
	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	EmployeeDAO empDAO;
	@Resource
	WorkFlowRuleMasterDAO workflowRuleMasterDAO;
	@Resource
	EmployeeTimesheetReviewerDAO lundinEmployeeReviewerDAO;
	@Resource
	TimesheetStatusMasterDAO lundinStatusMasterDAO;
	@Resource
	TimesheetStatusMasterDAO lundinTimesheetStatusMasterDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	LundinAFEDAO lundinAFEDAO;
	@Resource
	LundinBlockDAO lundinBlockDAO;
	@Resource
	EmployeeTimesheetReviewerDAO lundinEmpReviewerDAO;
	@Resource
	EmployeeHolidayCalendarDAO employeeHolidayCalendarDAO;
	@Resource
	LundinTimesheetMailLogic lundinTimesheetMailLogic;
	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	TimesheetBatchDAO lundinTimesheetBatchDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	DataExportUtils dataExportUtils;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	GeneralDAO generalDAO;
	@Resource
	GeneralLogic generalLogic;

	@Resource
	CoherentShiftApplicationDAO coherentShiftApplicationDAO;

	@Resource
	TimesheetBatchDAO timesheetBatchDAO;
	@Resource
	TimesheetStatusMasterDAO timesheetStatusMasterDAO;
	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;

	@Resource
	LionTimesheetReportsLogic lionTimesheetReportsLogic;

	@Resource
	CoherentShiftApplicationDetailDAO coherentShiftApplicationDetailDAO;

	@Resource
	CoherentShiftApplicationReviewerDAO coherentShiftApplicationReviewerDAO;

	@Resource
	CoherentShiftApplicationWorkflowDAO coherentShiftApplicationWorkflowDAO;

	@Resource
	CoherentShiftPrintPDFLogic coherentShiftPrintPDFLogic;

	@Resource
	TimesheetWorkflowDAO lundinWorkflowDAO;

	@Resource
	CoherentTimesheetReportsLogic coherentTimesheetReportsLogic;

	@Resource
	CoherentTimesheetMailLogic coherentTimesheetMailLogic;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

	@Override
	public PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, Long companyId) {
		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = new PendingOTTimesheetResponseForm();

		PendingOTTimesheetConditionDTO otTimesheetConditionDTO = new PendingOTTimesheetConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setCreatedDate(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setEmployeeName("%" + searchText.trim()
						+ "%");
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {

				otTimesheetConditionDTO.setBatch("%" + searchText.trim() + "%");
			}
		}

		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
		otTimesheetConditionDTO.setStatusNameList(claimStatus);
		otTimesheetConditionDTO.setPendingStatus(true);
		int recordSize = 0;

		recordSize = coherentShiftApplicationDAO.getCountForFindByCondition(
				otTimesheetConditionDTO, companyId);
		List<CoherentShiftApplication> otReviewers = coherentShiftApplicationDAO
				.findByCondition(pageDTO, sortDTO, otTimesheetConditionDTO,
						companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentShiftApplication coherentOvertimeApplication : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			TimesheetBatch lundinOtBatch = coherentOvertimeApplication
					.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(coherentOvertimeApplication
					.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils
					.timeStampToString(coherentOvertimeApplication
							.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			/*ID ENCRYPT*/
			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:viewCoherentPendingShiftApplications("
							+ FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication
									.getShiftApplicationID())
							+ ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc()
							+ "&apos;"
							+ ");'>Review" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication
					.getShiftApplicationID()));
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch
					.getTimesheetBatchDesc());
			pendingOTTimesheetForms.add(pendingOTTimesheetForm);
		}

		pendingOTTimesheetResponseForm
				.setPendingOTTimesheets(pendingOTTimesheetForms);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			pendingOTTimesheetResponseForm.setPage(pageDTO.getPageNumber());
			pendingOTTimesheetResponseForm.setTotal(totalPages);
			pendingOTTimesheetResponseForm.setRecords(recordSize);
		}
		return pendingOTTimesheetResponseForm;
	}

	@Override
	public PendingOTTimesheetResponseForm getTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, List<String> StatusNameList, Long companyId) {
		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = new PendingOTTimesheetResponseForm();

		PendingOTTimesheetConditionDTO otTimesheetConditionDTO = new PendingOTTimesheetConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setCreatedDate(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setEmployeeName("%" + searchText.trim()
						+ "%");
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {

				otTimesheetConditionDTO.setBatch("%" + searchText.trim() + "%");
			}
		}

		otTimesheetConditionDTO.setStatusNameList(StatusNameList);

		int recordSize = 0;

		recordSize = coherentShiftApplicationDAO.getCountForFindByCondition(
				otTimesheetConditionDTO, companyId);
		List<CoherentShiftApplication> otReviewers = coherentShiftApplicationDAO
				.findByCondition(pageDTO, sortDTO, otTimesheetConditionDTO,
						companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentShiftApplication coherentOvertimeApplication : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			TimesheetBatch lundinOtBatch = coherentOvertimeApplication
					.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(coherentOvertimeApplication
					.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils
					.timeStampToString(coherentOvertimeApplication
							.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();

			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:viewApprovedOrRejectShiftItems("
							+ FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication
									.getShiftApplicationID())
							+ ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc()
							+ "&apos;"
							+ ");'>View" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(coherentOvertimeApplication
					.getShiftApplicationID());
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch
					.getTimesheetBatchDesc());
			pendingOTTimesheetForms.add(pendingOTTimesheetForm);
		}

		pendingOTTimesheetResponseForm
				.setPendingOTTimesheets(pendingOTTimesheetForms);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			pendingOTTimesheetResponseForm.setPage(pageDTO.getPageNumber());
			pendingOTTimesheetResponseForm.setTotal(totalPages);
			pendingOTTimesheetResponseForm.setRecords(recordSize);
		}
		return pendingOTTimesheetResponseForm;
	}

	@Override
	public PendingOTTimesheetResponseForm getAllTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, List<String> StatusNameList, Long companyId) {
		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = new PendingOTTimesheetResponseForm();

		PendingOTTimesheetConditionDTO otTimesheetConditionDTO = new PendingOTTimesheetConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setCreatedDate(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setEmployeeName("%" + searchText.trim()
						+ "%");
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {

				otTimesheetConditionDTO.setBatch("%" + searchText.trim() + "%");
			}
		}

		otTimesheetConditionDTO.setStatusNameList(StatusNameList);

		int recordSize = 0;

		recordSize = coherentShiftApplicationDAO.getCountForFindAllByCondition(
				otTimesheetConditionDTO, companyId);
		List<CoherentShiftApplication> otReviewers = coherentShiftApplicationDAO
				.findAllByCondition(pageDTO, sortDTO, otTimesheetConditionDTO,
						companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentShiftApplication coherentOvertimeApplication : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			TimesheetBatch lundinOtBatch = coherentOvertimeApplication
					.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(coherentOvertimeApplication
					.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils
					.timeStampToString(coherentOvertimeApplication
							.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();

			if (coherentOvertimeApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)
					|| coherentOvertimeApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewCoherentPendingShiftApplications("
								+ FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication
										.getShiftApplicationID())
								+ ",&apos;"
								+ lundinOtBatch.getTimesheetBatchDesc()
								+ "&apos;" + ");'>Review" + "</a>");
			} else {
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewApprovedOrRejectShiftItems("
								+ FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication
										.getShiftApplicationID())
								+ ",&apos;"
								+ lundinOtBatch.getTimesheetBatchDesc()
								+ "&apos;" + ");'>View" + "</a>");
			}
   /*ID ENCRYPT*/
			pendingOTTimesheetForm.setOtTimesheetId(FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication
					.getShiftApplicationID()));
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch
					.getTimesheetBatchDesc());
			pendingOTTimesheetForms.add(pendingOTTimesheetForm);
		}

		pendingOTTimesheetResponseForm
				.setPendingOTTimesheets(pendingOTTimesheetForms);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			pendingOTTimesheetResponseForm.setPage(pageDTO.getPageNumber());
			pendingOTTimesheetResponseForm.setTotal(totalPages);
			pendingOTTimesheetResponseForm.setRecords(recordSize);
		}
		return pendingOTTimesheetResponseForm;
	}

	private String getEmployeeNameWithNumber(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		return employeeName;
	}

	private class EmployeeReviewerComp implements
			Comparator<EmployeeTimesheetReviewer> {
		public int compare(EmployeeTimesheetReviewer templateField,
				EmployeeTimesheetReviewer compWithTemplateField) {
			if (templateField.getEmployeeTimesheetReviewerId() > compWithTemplateField
					.getEmployeeTimesheetReviewerId()) {
				return 1;
			} else if (templateField.getEmployeeTimesheetReviewerId() < compWithTemplateField
					.getEmployeeTimesheetReviewerId()) {
				return -1;
			}
			return 0;
		}
	}

	private class ClaimReviewerComp implements
			Comparator<CoherentShiftApplicationReviewer> {
		public int compare(CoherentShiftApplicationReviewer templateField,
				CoherentShiftApplicationReviewer compWithTemplateField) {
			if (templateField.getShiftApplicationReviewerID() > compWithTemplateField
					.getShiftApplicationReviewerID()) {
				return 1;
			} else if (templateField.getShiftApplicationReviewerID() < compWithTemplateField
					.getShiftApplicationReviewerID()) {
				return -1;
			}
			return 0;
		}
	}

	@Override
	public List<String> getHolidaysFor(Long employeeId, Date startDate,
			Date endDate) {
		EmployeeHolidayCalendar empHolidaycalendar = employeeHolidayCalendarDAO
				.getCalendarDetail(employeeId, startDate, endDate);
		List<String> toReturn = new ArrayList<String>();
		if (empHolidaycalendar != null) {
			CompanyHolidayCalendar compHolidayCalendar = empHolidaycalendar
					.getCompanyHolidayCalendar();
			Set<CompanyHolidayCalendarDetail> calDetails = compHolidayCalendar
					.getCompanyHolidayCalendarDetails();
			DateTime holidayDt = new DateTime();
			DateTime startdt = new DateTime(new Timestamp(startDate.getTime()));
			DateTime endDt = new DateTime(new Timestamp(endDate.getTime()));
			DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
			for (CompanyHolidayCalendarDetail compHolidayDetail : calDetails) {
				holidayDt = new DateTime(compHolidayDetail.getHolidayDate());
				if (startdt.compareTo(holidayDt) < 0
						&& endDt.compareTo(holidayDt) > 0) {
					toReturn.add(dtfOut.print(holidayDt));
				}
				if (startdt.toLocalDate().equals(holidayDt.toLocalDate())
						|| endDt.toLocalDate().equals(holidayDt.toLocalDate())) {
					toReturn.add(dtfOut.print(holidayDt));
				}

			}
		}
		return toReturn;
	}

	@Override
	public LundinPendingItemsForm getPendingItemForReview(long timesheetId,
			Long employeeId, long companyId) {
		LundinPendingItemsForm toReturn = new LundinPendingItemsForm();
		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		CoherentShiftApplication timesheetApplication = coherentShiftApplicationDAO
				.findByCompanyShiftId(timesheetId,companyId);
         if(timesheetApplication!=null)
         {
		toReturn.setEmployeeName(getEmployeeNameWithNumber(timesheetApplication
				.getEmployee()));

		List<TimesheetWorkflow> otWorkflows = lundinWorkflowDAO
				.findByCompanyId(timesheetApplication.getCompany()
						.getCompanyId());
		for (TimesheetWorkflow otWorkflow : otWorkflows) {
			WorkFlowRuleMaster ruleMaster = otWorkflow.getWorkFlowRuleMaster();
			if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_OVERRIDE)) {
				allowOverride = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_REJECT)) {
				allowReject = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_FORWARD)) {
				allowForward = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_APPROVE)) {
				allowApprove = ruleMaster.getRuleValue();
			}
		}
		for (CoherentShiftApplicationReviewer reviewer : timesheetApplication
				.getCoherentShiftApplicationReviewer()) {
			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

				if (reviewer.isPending()) {
					if (allowOverride.length() == 3
							&& allowOverride.substring(0, 1).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(0, 1).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(0, 1).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(0, 1).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}

			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

				if (reviewer.isPending()) {
					if (allowOverride.length() == 3
							&& allowOverride.substring(1, 2).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(1, 2).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(1, 2).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(1, 2).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}

			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

				if (reviewer.isPending()) {
					if (allowOverride.length() == 3
							&& allowOverride.substring(2, 3).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(2, 3).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(2, 3).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(2, 3).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}
		}
		toReturn.setCompanyId(timesheetApplication.getCompany().getCompanyId());
		toReturn.setTimesheetId(timesheetApplication.getShiftApplicationID());
		toReturn.setRemarks("");
         }
		return toReturn;
	}

	@Override
	public CoherentShiftDetailFormResponse viewMultipleTimesheetApps(
			Long companyId, Long loggedInEmpId, String[] timesheetIds) {
		List<Long> timesheetIdsList = new ArrayList<Long>();
		for (int count = 0; count < timesheetIds.length; count++) {
			if (StringUtils.isNotBlank(timesheetIds[count])) {
				/*ID DECRYPT*/
				timesheetIdsList.add(FormatPreserveCryptoUtil.decrypt(Long.parseLong(timesheetIds[count])));
			}
		}
		List<CoherentShiftApplicationReviewer> coherentOvertimeApplicationList = coherentShiftApplicationReviewerDAO
				.getPendingTimesheetByIds(null, timesheetIdsList);
		CoherentShiftDetailFormResponse response = new CoherentShiftDetailFormResponse();

		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		List<TimesheetWorkflow> otWorkflows = lundinWorkflowDAO
				.findByCompanyId(companyId);
		for (TimesheetWorkflow otWorkflow : otWorkflows) {
			WorkFlowRuleMaster ruleMaster = otWorkflow.getWorkFlowRuleMaster();
			if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_REJECT)) {
				allowReject = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_FORWARD)) {
				allowForward = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_APPROVE)) {
				allowApprove = ruleMaster.getRuleValue();
			}
		}

		List<CoherentShiftDetailForm> coherentOvertimeDetailFormList = new ArrayList<CoherentShiftDetailForm>();
		for (CoherentShiftApplicationReviewer coherentOvertimeApplicationReviewerVO : coherentOvertimeApplicationList) {
			CoherentShiftDetailForm coherentOvertimeDetailForm = new CoherentShiftDetailForm();
			AddLeaveForm addLeaveForm = new AddLeaveForm();

			int reviewOrder = 0;
			for (CoherentShiftApplicationReviewer applicationReviewer : coherentOvertimeApplicationReviewerVO
					.getCoherentShiftApplication()
					.getCoherentShiftApplicationReviewer()) {
				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {
					addLeaveForm.setApplyTo(applicationReviewer
							.getEmployeeReviewer().getEmail());
					addLeaveForm
							.setLeaveReviewer1(getEmployeeNameWithNumber(applicationReviewer
									.getEmployeeReviewer()));
					addLeaveForm.setApplyToId(applicationReviewer
							.getEmployeeReviewer().getEmployeeId());
					if (coherentOvertimeApplicationReviewerVO
							.getEmployeeReviewer().getEmployeeId() == applicationReviewer
							.getEmployeeReviewer().getEmployeeId()) {
						reviewOrder = 1;
						if (allowReject.length() == 3
								&& allowReject.substring(0, 1).equals("1")) {
							coherentOvertimeDetailForm.setCanReject(true);
						} else {
							coherentOvertimeDetailForm.setCanReject(false);
						}
						if (allowApprove.length() == 3
								&& allowApprove.substring(0, 1).equals("1")) {
							coherentOvertimeDetailForm.setCanApprove(true);
						} else {
							coherentOvertimeDetailForm.setCanApprove(false);
						}
						if (allowForward.length() == 3
								&& allowForward.substring(0, 1).equals("1")) {
							coherentOvertimeDetailForm.setCanForward(true);
						} else {
							coherentOvertimeDetailForm.setCanForward(false);
						}
					}
				}

				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("2")) {
					addLeaveForm
							.setLeaveReviewer2(getEmployeeNameWithNumber(applicationReviewer
									.getEmployeeReviewer()));
					addLeaveForm.setLeaveReviewer2Id(applicationReviewer
							.getEmployeeReviewer().getEmployeeId());
					if (coherentOvertimeApplicationReviewerVO
							.getEmployeeReviewer().getEmployeeId() == applicationReviewer
							.getEmployeeReviewer().getEmployeeId()) {
						reviewOrder = 2;
						if (allowReject.length() == 3
								&& allowReject.substring(1, 2).equals("1")) {
							coherentOvertimeDetailForm.setCanReject(true);
						} else {
							coherentOvertimeDetailForm.setCanReject(false);
						}
						if (allowApprove.length() == 3
								&& allowApprove.substring(1, 2).equals("1")) {
							coherentOvertimeDetailForm.setCanApprove(true);
						} else {
							coherentOvertimeDetailForm.setCanApprove(false);
						}
						if (allowForward.length() == 3
								&& allowForward.substring(1, 2).equals("1")) {
							coherentOvertimeDetailForm.setCanForward(true);
						} else {
							coherentOvertimeDetailForm.setCanForward(false);
						}
					}

				}

				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("3")) {
					addLeaveForm
							.setLeaveReviewer3(getEmployeeNameWithNumber(applicationReviewer
									.getEmployeeReviewer()));
					addLeaveForm.setLeaveReviewer3Id(applicationReviewer
							.getEmployeeReviewer().getEmployeeId());
					if (coherentOvertimeApplicationReviewerVO
							.getEmployeeReviewer().getEmployeeId() == applicationReviewer
							.getEmployeeReviewer().getEmployeeId()) {
						reviewOrder = 2;
						if (allowReject.length() == 3
								&& allowReject.substring(2, 3).equals("1")) {
							coherentOvertimeDetailForm.setCanReject(true);
						} else {
							coherentOvertimeDetailForm.setCanReject(false);
						}
						if (allowApprove.length() == 3
								&& allowApprove.substring(2, 3).equals("1")) {
							coherentOvertimeDetailForm.setCanApprove(true);
						} else {
							coherentOvertimeDetailForm.setCanApprove(false);
						}
						if (allowForward.length() == 3
								&& allowForward.substring(2, 3).equals("1")) {
							coherentOvertimeDetailForm.setCanForward(true);
						} else {
							coherentOvertimeDetailForm.setCanForward(false);
						}
					}
				}
			}

			if (reviewOrder == 1 && addLeaveForm.getLeaveReviewer2Id() != null) {
				Employee empReviewer2 = employeeDAO.findById(addLeaveForm
						.getLeaveReviewer2Id());
				coherentOvertimeDetailForm
						.setForwardTo(getEmployeeNameWithNumber(empReviewer2));
				coherentOvertimeDetailForm.setForwardToId(empReviewer2
						.getEmployeeId());
			} else if (reviewOrder == 2
					&& addLeaveForm.getLeaveReviewer3Id() != null) {
				Employee empReviewer3 = employeeDAO.findById(addLeaveForm
						.getLeaveReviewer3Id());
				coherentOvertimeDetailForm
						.setForwardTo(getEmployeeNameWithNumber(empReviewer3));
				coherentOvertimeDetailForm.setForwardToId(empReviewer3
						.getEmployeeId());
			} else {
				coherentOvertimeDetailForm.setForwardTo("");
			}

			coherentOvertimeDetailForm
					.setCoherentShiftApplicationId(coherentOvertimeApplicationReviewerVO
							.getShiftApplicationReviewerID());
			coherentOvertimeDetailForm
					.setCoherentShiftApplicationId(FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplicationReviewerVO
							.getCoherentShiftApplication()
							.getShiftApplicationID()));
			coherentOvertimeDetailForm
					.setClaimMonth(coherentOvertimeApplicationReviewerVO
							.getCoherentShiftApplication().getTimesheetBatch()
							.getTimesheetBatchDesc());

			coherentOvertimeDetailForm.setShiftType("");
			coherentOvertimeDetailForm
					.setTotalShift(coherentOvertimeApplicationReviewerVO
							.getCoherentShiftApplication().getTotalShifts());

			Company companyVO = companyDAO.findById(companyId);

			List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic
					.getEmpDynFieldsValueList(
							coherentOvertimeApplicationReviewerVO
									.getCoherentShiftApplication().getCompany()
									.getCompanyId(), companyVO.getDateFormat(),
							coherentOvertimeApplicationReviewerVO
									.getCoherentShiftApplication()
									.getEmployee().getEmployeeId());

			for (Object[] deptObject : deptCostCentreEmpObjectList) {

				if (deptObject != null
						&& deptObject[3] != null
						&& deptObject[3]
								.equals(coherentOvertimeApplicationReviewerVO
										.getCoherentShiftApplication()
										.getEmployee().getEmployeeNumber())) {
					// jsonObject.put("department", deptObject[0]);
					// jsonObject.put("costCentre", deptObject[2]);

					if (deptObject[2] != null) {
						coherentOvertimeDetailForm.setCostCentre(String
								.valueOf(deptObject[2]));
					} else {
						coherentOvertimeDetailForm.setCostCentre("");
					}
					if (deptObject[0] != null) {
						coherentOvertimeDetailForm.setDepartment(String
								.valueOf(deptObject[0]));
					} else {
						coherentOvertimeDetailForm.setDepartment("");
					}
				}
			}

			coherentOvertimeDetailForm
					.setEmployee(getEmployeeNameWithNumber(coherentOvertimeApplicationReviewerVO
							.getCoherentShiftApplication().getEmployee()));
			coherentOvertimeDetailFormList.add(coherentOvertimeDetailForm);
		}
		response.setPendingItemsList(coherentOvertimeDetailFormList);
		return response;
	}

	@Override
	public List<PendingOTTimesheetForm> reviewMultipleAppByAdmin(
			CoherentShiftDetailForm pendingItemsForm, Long employeeId,
			Long companyId, LeaveSessionDTO sessionDTO) {
		List<PendingOTTimesheetForm> addLeaveFormList = new ArrayList<PendingOTTimesheetForm>();
		List<LeaveReviewFormDTO> leaveReviewFormDTOList = pendingItemsForm
				.getCoherentReviewFormDTOList();
		for (LeaveReviewFormDTO leaveReviewFormDTO : leaveReviewFormDTOList) {
			pendingItemsForm.setOtTimesheetId(null);
			pendingItemsForm.setRemarks("");

			if (StringUtils.isBlank(leaveReviewFormDTO.getLeaveReviewAction())) {
				continue;
			}

			if (leaveReviewFormDTO.getLeaveApplicationId() != null) {
				pendingItemsForm.setOtTimesheetId(leaveReviewFormDTO
						.getLeaveApplicationId());
				pendingItemsForm.setRemarks(leaveReviewFormDTO.getRemarks());
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Approve")) {
					PendingOTTimesheetForm addLeaveForm = acceptTimesheet(
							pendingItemsForm, employeeId, companyId);
					addLeaveForm.setOtTimesheetId(leaveReviewFormDTO
							.getLeaveApplicationId());
					addLeaveForm.setRemarks("Approved.");
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Reject")) {
					PendingOTTimesheetForm addLeaveForm = rejectTimesheet(
							pendingItemsForm, employeeId, companyId);
					addLeaveForm.setOtTimesheetId(leaveReviewFormDTO
							.getLeaveApplicationId());
					addLeaveForm.setRemarks("Rejected.");
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Forward")) {
					PendingOTTimesheetForm addLeaveForm = forwardTimesheet(
							pendingItemsForm, employeeId, companyId);
					addLeaveForm.setOtTimesheetId(leaveReviewFormDTO
							.getLeaveApplicationId());
					addLeaveForm.setRemarks("Forward to next reviewer.");
					addLeaveFormList.add(addLeaveForm);
				}

			}
		}
		return addLeaveFormList;
	}

	@Override
	public PendingOTTimesheetForm forwardTimesheet(
			CoherentShiftDetailForm pendingOtTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessfullyFor = false;
		CoherentShiftApplicationReviewer coherentOvertimeApplicationReviewerVO = null;
		CoherentShiftApplicationWorkflow coherentOvertimeApplicationWorkflow = null;
		CoherentShiftApplicationReviewer coherentOTTimesheetReviewer2 = null;
		Employee employee = employeeDAO.findById(employeeId);
		Date date = new Date();
		CoherentShiftApplication coherentOvertimeApplicationVO = coherentShiftApplicationDAO
				.findById(pendingOtTimesheetForm.getOtTimesheetId());

		for (CoherentShiftApplicationReviewer appReviewer : coherentOvertimeApplicationVO
				.getCoherentShiftApplicationReviewer()) {
			if (appReviewer.isPending()) {
				coherentOvertimeApplicationReviewerVO = appReviewer;
			}
		}

		String workflowLevel = String
				.valueOf(coherentShiftApplicationReviewerDAO
						.getOTTimesheetReviewerCount(coherentOvertimeApplicationVO
								.getShiftApplicationID()));
		if (workflowLevel != null
				&& coherentOvertimeApplicationReviewerVO
						.getWorkFlowRuleMaster().getRuleValue()
						.equalsIgnoreCase(workflowLevel)) {
			response = acceptTimesheet(pendingOtTimesheetForm, employeeId,
					companyId);
		} else {
			try {
				CoherentShiftApplicationWorkflow applicationWorkflow = new CoherentShiftApplicationWorkflow();

				for (TimesheetWorkflow otWorkflow : lundinWorkflowDAO
						.findByCompanyId(companyId)) {
					if (otWorkflow
							.getWorkFlowRuleMaster()
							.getRuleName()
							.equalsIgnoreCase(
									PayAsiaConstants.OT_DEF_WORKFLOW_LEVEL)) {
						workflowLevel = otWorkflow.getWorkFlowRuleMaster()
								.getRuleValue();

					}
				}
				TimesheetStatusMaster otStatusMaster = lundinTimesheetStatusMasterDAO
						.findByName(PayAsiaConstants.OT_STATUS_APPROVED);

				TimesheetStatusMaster otStatusCompleted = null;
				if (coherentOvertimeApplicationReviewerVO
						.getWorkFlowRuleMaster().getRuleValue()
						.equalsIgnoreCase(workflowLevel)) {
					otStatusCompleted = lundinTimesheetStatusMasterDAO
							.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
					coherentOvertimeApplicationVO
							.setTimesheetStatusMaster(otStatusCompleted);
				}
				coherentOvertimeApplicationVO.setUpdatedDate(new Timestamp(date
						.getTime()));
				coherentShiftApplicationDAO
						.update(coherentOvertimeApplicationVO);

				// Update Timesheet Approval Pending Status to next level
				// reviewer
				int nextWorkFlowRuleValueLevel = Integer
						.valueOf(coherentOvertimeApplicationReviewerVO
								.getWorkFlowRuleMaster().getRuleValue()) + 1;
				for (CoherentShiftApplicationReviewer applicationReviewer : coherentOvertimeApplicationVO
						.getCoherentShiftApplicationReviewer()) {
					int workFlowRuleValueLevel = Integer
							.valueOf(applicationReviewer
									.getWorkFlowRuleMaster().getRuleValue());
					if (nextWorkFlowRuleValueLevel == workFlowRuleValueLevel) {
						coherentOTTimesheetReviewer2 = applicationReviewer;
						applicationReviewer.setPending(true);
						coherentShiftApplicationReviewerDAO
								.update(applicationReviewer);
					}
				}

				coherentOvertimeApplicationReviewerVO.setPending(false);
				coherentOvertimeApplicationReviewerVO
						.setEmployeeReviewer(employee);
				coherentShiftApplicationReviewerDAO
						.update(coherentOvertimeApplicationReviewerVO);

				applicationWorkflow.setCreatedBy(employee);
				applicationWorkflow
						.setCoherentShiftApplication(coherentOvertimeApplicationVO);
				applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
				applicationWorkflow.setRemarks(pendingOtTimesheetForm
						.getRemarks());
				applicationWorkflow
						.setCreatedDate(new Timestamp(date.getTime()));
				coherentOvertimeApplicationWorkflow = coherentShiftApplicationWorkflowDAO
						.saveAndReturn(applicationWorkflow);
				isSuccessfullyFor = true;
			} catch (Exception exception) {
				// LOGGER.error(exception.getMessage(), exception);
			}
			if (isSuccessfullyFor) {
				EmailDataDTO emailDataDTO = new EmailDataDTO();
				emailDataDTO.setTimesheetId(coherentOvertimeApplicationVO
						.getShiftApplicationID());
				emailDataDTO
						.setEmployeeName(getEmployeeName(coherentOvertimeApplicationVO
								.getEmployee()));
				emailDataDTO.setEmployeeNumber(coherentOvertimeApplicationVO
						.getEmployee().getEmployeeNumber());
				emailDataDTO.setEmpCompanyId(coherentOvertimeApplicationVO
						.getEmployee().getCompany().getCompanyId());
				emailDataDTO.setBatchDesc(coherentOvertimeApplicationVO
						.getTimesheetBatch().getTimesheetBatchDesc());
				emailDataDTO
						.setOvertimeShiftType(PayAsiaConstants.COHERENT_SHIFT_TYPE);
				emailDataDTO.setCurrentEmployeeName(getEmployeeName(employee));
				emailDataDTO.setEmailFrom(employee.getEmail());
				emailDataDTO.setEmailTo(coherentOTTimesheetReviewer2
						.getEmployeeReviewer().getEmail());
				emailDataDTO.setReviewerCompanyId(coherentOTTimesheetReviewer2
						.getEmployeeReviewer().getCompany().getCompanyId());
				emailDataDTO.setReviewerFirstName(coherentOTTimesheetReviewer2
						.getEmployeeReviewer().getFirstName());
				coherentTimesheetMailLogic
						.sendPendingEmailForTimesheet(
								companyId,
								emailDataDTO,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_FORWARD,
								pendingOtTimesheetForm.getRemarks());
			}

		}

		return response;

	}

	@Override
	public PendingOTTimesheetForm acceptTimesheet(
			CoherentShiftDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		Boolean isSuccessfullyAcc = false;
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		CoherentShiftApplication coherentOvertimeApplication = null;
		CoherentShiftApplicationWorkflow coherentOvertimeApplicationWorkflow = null;
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			CoherentShiftApplicationWorkflow applicationWorkflow = new CoherentShiftApplicationWorkflow();

			coherentOvertimeApplication = coherentShiftApplicationDAO
					.findByCompanyShiftId(pendingOTTimesheetForm.getOtTimesheetId(),companyId);
              if(coherentOvertimeApplication!=null){
			List<String> otApprovedStatusList = new ArrayList<>();
			otApprovedStatusList.add(PayAsiaConstants.OT_STATUS_COMPLETED);

			TimesheetStatusMaster otStatusCompleted = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
			coherentOvertimeApplication
					.setTimesheetStatusMaster(otStatusCompleted);

			coherentOvertimeApplication.setUpdatedDate(new Timestamp(date
					.getTime()));
			coherentShiftApplicationDAO.update(coherentOvertimeApplication);

			for (CoherentShiftApplicationReviewer appReviewer : coherentOvertimeApplication
					.getCoherentShiftApplicationReviewer()) {
				if (appReviewer.isPending()) {
					appReviewer.setPending(false);
					appReviewer.setEmployeeReviewer(employee);
					coherentShiftApplicationReviewerDAO.update(appReviewer);
				}
			}

			applicationWorkflow.setCreatedBy(employee);
			applicationWorkflow
					.setCoherentShiftApplication(coherentOvertimeApplication);
			applicationWorkflow.setTimesheetStatusMaster(otStatusCompleted);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			coherentOvertimeApplicationWorkflow = coherentShiftApplicationWorkflowDAO
					.saveAndReturn(applicationWorkflow);
			isSuccessfullyAcc = true;
		if (isSuccessfullyAcc) {
			EmailDataDTO emailDataDTO = new EmailDataDTO();
			emailDataDTO.setTimesheetId(coherentOvertimeApplication
					.getShiftApplicationID());
			emailDataDTO
					.setEmployeeName(getEmployeeName(coherentOvertimeApplication
							.getEmployee()));
			emailDataDTO.setEmpCompanyId(coherentOvertimeApplication
					.getEmployee().getCompany().getCompanyId());
			emailDataDTO.setEmployeeNumber(coherentOvertimeApplication
					.getEmployee().getEmployeeNumber());
			emailDataDTO.setBatchDesc(coherentOvertimeApplication
					.getTimesheetBatch().getTimesheetBatchDesc());
			emailDataDTO
					.setOvertimeShiftType(PayAsiaConstants.COHERENT_SHIFT_TYPE);
			emailDataDTO.setEmailFrom(employee.getEmail());
			emailDataDTO.setEmailTo(coherentOvertimeApplication.getEmployee()
					.getEmail());

			coherentTimesheetMailLogic
					.sendAcceptRejectMailForTimesheet(
							companyId,
							emailDataDTO,
							PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPROVE,
							pendingOTTimesheetForm.getRemarks());
		}
		}
		} catch (Exception exception) {
			// LOGGER.error(exception.getMessage(), exception);
		}
		return response;

	}

	@Override
	public PendingOTTimesheetForm rejectTimesheet(
			CoherentShiftDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessRejeted = false;
		CoherentShiftApplication coherentOvertimeApplication = null;
		CoherentShiftApplicationWorkflow workFlow = null;
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			CoherentShiftApplicationWorkflow applicationWorkflow = new CoherentShiftApplicationWorkflow();

			TimesheetStatusMaster otStatusMaster = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_REJECTED);
			coherentOvertimeApplication = coherentShiftApplicationDAO
					.findByCompanyShiftId(pendingOTTimesheetForm.getOtTimesheetId(),companyId);
            if(coherentOvertimeApplication!=null){
			List<String> claimApprovedStatusList = new ArrayList<>();
			claimApprovedStatusList.add(PayAsiaConstants.OT_STATUS_COMPLETED);

			coherentOvertimeApplication
					.setTimesheetStatusMaster(otStatusMaster);
			coherentOvertimeApplication.setUpdatedDate(new Timestamp(date
					.getTime()));
			coherentShiftApplicationDAO.update(coherentOvertimeApplication);

			for (CoherentShiftApplicationReviewer appReviewer : coherentOvertimeApplication
					.getCoherentShiftApplicationReviewer()) {
				if (appReviewer.isPending()) {
					appReviewer.setPending(false);
					appReviewer.setEmployeeReviewer(employee);
					coherentShiftApplicationReviewerDAO.update(appReviewer);
				}
			}

			applicationWorkflow.setCreatedBy(employee);
			applicationWorkflow
					.setCoherentShiftApplication(coherentOvertimeApplication);
			applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			workFlow = coherentShiftApplicationWorkflowDAO
					.saveAndReturn(applicationWorkflow);
			isSuccessRejeted = true;
		if (isSuccessRejeted) {
			EmailDataDTO emailDataDTO = new EmailDataDTO();
			emailDataDTO.setTimesheetId(coherentOvertimeApplication
					.getShiftApplicationID());
			emailDataDTO
					.setEmployeeName(getEmployeeName(coherentOvertimeApplication
							.getEmployee()));
			emailDataDTO.setEmpCompanyId(coherentOvertimeApplication
					.getEmployee().getCompany().getCompanyId());
			emailDataDTO.setEmployeeNumber(coherentOvertimeApplication
					.getEmployee().getEmployeeNumber());
			emailDataDTO.setBatchDesc(coherentOvertimeApplication
					.getTimesheetBatch().getTimesheetBatchDesc());
			emailDataDTO
					.setOvertimeShiftType(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
			emailDataDTO.setEmailFrom(employee.getEmail());
			emailDataDTO.setEmailTo(coherentOvertimeApplication.getEmployee()
					.getEmail());

			coherentTimesheetMailLogic
					.sendAcceptRejectMailForTimesheet(
							companyId,
							emailDataDTO,
							PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_REJECT,
							pendingOTTimesheetForm.getRemarks());
		}
	}
		}catch (Exception exception) {
		// LOGGER.error(exception.getMessage(), exception);
	}
		return response;
	}

	@Override
	public ImportEmployeeOvertimeShiftForm importEmployeeShift(
			ImportEmployeeOvertimeShiftForm importEmployeeClaimForm,
			Long companyId, Long employeeId) {
		ImportEmployeeOvertimeShiftForm response = new ImportEmployeeOvertimeShiftForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();
		try {

			importEmployeeClaimForm = readEmployeeOvertimeImportData(
					importEmployeeClaimForm.getFileUpload(), companyId,
					dataImportLogDTOs);

			validateAndInsertEmployeeShift(importEmployeeClaimForm, companyId,
					dataImportLogDTOs, employeeId);

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaRollBackDataException(dataImportLogDTOs);

		}
		response.setDataImportLogDTOs(dataImportLogDTOs);
		return response;

	}

	private ImportEmployeeOvertimeShiftForm readEmployeeOvertimeImportData(
			CommonsMultipartFile fileUpload, Long companyId,
			List<DataImportLogDTO> dataImportLogDTOs) {
		ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShift = new ImportEmployeeOvertimeShiftForm();
		String fileName = fileUpload.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());

		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			importEmployeeOvertimeShift = ExcelUtils
					.getCoherentOvertimeShiftFromXLS(fileUpload,
							dataImportLogDTOs);
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			importEmployeeOvertimeShift = ExcelUtils
					.getCoherentOvertimeShiftFromXLSX(fileUpload,
							dataImportLogDTOs);
		}
		return importEmployeeOvertimeShift;
	}

	private ImportEmployeeOvertimeShiftForm validateAndInsertEmployeeShift(
			ImportEmployeeOvertimeShiftForm importEmployeeClaimForm,
			Long companyId, List<DataImportLogDTO> dataImportLogDTOs,
			Long loggedInEmployeeId) {
		ImportEmployeeOvertimeShiftForm importEmployeeClaimFrm = new ImportEmployeeOvertimeShiftForm();
		HashMap<String, CoherentOvertimeDetailReportDTO> overtimeDetailDTOMap = new HashMap<>();

		setOvertimeDetailImportDataDTO(dataImportLogDTOs,
				importEmployeeClaimForm, overtimeDetailDTOMap, companyId);
		validateImpotedData(dataImportLogDTOs, overtimeDetailDTOMap, companyId);

		if (!dataImportLogDTOs.isEmpty()) {
			importEmployeeClaimFrm.setDataValid(false);
			importEmployeeClaimFrm.setDataImportLogDTOs(dataImportLogDTOs);
			return importEmployeeClaimFrm;
		}

		Company companyVO = companyDAO.findById(companyId);
		Employee loggedInEmployeeVO = employeeDAO.findById(loggedInEmployeeId);

		TimesheetStatusMaster timesheetStatusMasterVO = timesheetStatusMasterDAO
				.findByName(PayAsiaConstants.LION_TIMESHEET_STATUS_COMPLETED);

		HashMap<String, AppCodeMaster> appCodeMasterMap = new HashMap<>();
		List<AppCodeMaster> appCodeMasterList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_SHIFT_TYPE);
		for (AppCodeMaster appCodeMaster : appCodeMasterList) {
			appCodeMasterMap.put(appCodeMaster.getCodeDesc(), appCodeMaster);
		}

		Set<String> keySet = overtimeDetailDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			CoherentOvertimeDetailReportDTO overtimeDetailDTO = overtimeDetailDTOMap
					.get(key);

			if (overtimeDetailDTO.getEmployeeId() == null) {
				continue;
			}

			Employee employeeVO = employeeDAO.findById(overtimeDetailDTO
					.getEmployeeId());
			if (employeeVO == null) {
				continue;
			}
			TimesheetBatch timesheetBatch = timesheetBatchDAO
					.getBatchByCurrentDate(companyId, DateUtils
							.stringToTimestamp(
									overtimeDetailDTO.getMonthDate(),
									PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
			if (timesheetBatch == null) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(Long.parseLong(key));
				dataImportLogDTO
						.setErrorKey("payasia.coherent.batch.not.found");
				dataImportLogDTO.setColName("Month");
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);
			}
			Timestamp newTimestamp = timesheetBatch.getStartDate();

			CoherentShiftApplication coherentShiftApplicationReturn = null;

			CoherentShiftApplication coherentShiftApplication = new CoherentShiftApplication();
			coherentShiftApplication.setCompany(companyVO);
			coherentShiftApplication.setEmployee(employeeVO);
			coherentShiftApplication
					.setTimesheetStatusMaster(timesheetStatusMasterVO);
			coherentShiftApplication.setTimesheetBatch(timesheetBatch);

			// coherentShiftApplication.setShiftType(appCodeMasterMap
			// .get(PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND));
			if (StringUtils.isNotBlank(overtimeDetailDTO.getNoOfShift())) {
				coherentShiftApplication.setTotalShifts(Integer
						.valueOf(overtimeDetailDTO.getNoOfShift()));
			}
			if (StringUtils.isBlank(overtimeDetailDTO.getUpdatedDate())) {
				coherentShiftApplication.setCreatedDate(DateUtils
						.getCurrentTimestampWithTime());
				coherentShiftApplication.setUpdatedDate(DateUtils
						.getCurrentTimestampWithTime());
			} else {
				coherentShiftApplication.setCreatedDate(DateUtils
						.stringToTimestamp(overtimeDetailDTO.getUpdatedDate(),
								PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
				coherentShiftApplication.setUpdatedDate(DateUtils
						.stringToTimestamp(overtimeDetailDTO.getUpdatedDate(),
								PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
			}

			coherentShiftApplicationReturn = coherentShiftApplicationDAO
					.saveAndReturnWithFlush(coherentShiftApplication);

			// Insert Into CoherentOvertimeApplicationDetail
			CoherentShiftApplicationDetail coherentShiftApplicationDetail = new CoherentShiftApplicationDetail();
			coherentShiftApplicationDetail
					.setCoherentShiftApplication(coherentShiftApplicationReturn);
			coherentShiftApplicationDetail.setShift(true);
			coherentShiftApplicationDetail.setShiftDate(newTimestamp);
			coherentShiftApplicationDetail.setRemarks("");
			coherentShiftApplicationDetailDAO
					.save(coherentShiftApplicationDetail);

			// Set Reviewers
			CoherentShiftApplicationReviewer coherentShiftApplicationReviewer = new CoherentShiftApplicationReviewer();
			WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
					.findByRuleNameValue(
							PayAsiaConstants.WORK_FLOW_RULE_NAME_OT_REVIEWER,
							"1");
			coherentShiftApplicationReviewer
					.setWorkFlowRuleMaster(workflowRuleMaster);
			coherentShiftApplicationReviewer
					.setCoherentShiftApplication(coherentShiftApplicationReturn);
			coherentShiftApplicationReviewer.setPending(false);
			coherentShiftApplicationReviewer
					.setEmployeeReviewer(loggedInEmployeeVO);
			if (StringUtils.isBlank(overtimeDetailDTO.getUpdatedDate())) {
				coherentShiftApplicationReviewer.setCreatedDate(DateUtils
						.getCurrentTimestampWithTime());
			} else {
				coherentShiftApplicationReviewer.setCreatedDate(DateUtils
						.stringToTimestamp(overtimeDetailDTO.getUpdatedDate(),
								PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
			}
			coherentShiftApplicationReviewerDAO
					.save(coherentShiftApplicationReviewer);

			// Set Workflow
			CoherentShiftApplicationWorkflow shiftApplicationSubmittedWorkflow = new CoherentShiftApplicationWorkflow();
			shiftApplicationSubmittedWorkflow
					.setCoherentShiftApplication(coherentShiftApplicationReturn);
			shiftApplicationSubmittedWorkflow.setCreatedBy(loggedInEmployeeVO);
			shiftApplicationSubmittedWorkflow.setRemarks("");
			shiftApplicationSubmittedWorkflow
					.setTimesheetStatusMaster(timesheetStatusMasterDAO
							.findByName(PayAsiaConstants.LION_TIMESHEET_STATUS_SUBMITTED));
			if (StringUtils.isBlank(overtimeDetailDTO.getUpdatedDate())) {
				shiftApplicationSubmittedWorkflow.setCreatedDate(DateUtils
						.getCurrentTimestampWithTime());
			} else {
				shiftApplicationSubmittedWorkflow.setCreatedDate(DateUtils
						.stringToTimestamp(overtimeDetailDTO.getUpdatedDate(),
								PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
			}
			coherentShiftApplicationWorkflowDAO
					.save(shiftApplicationSubmittedWorkflow);

			CoherentShiftApplicationWorkflow shiftApplicationApprovedWorkflow = new CoherentShiftApplicationWorkflow();
			shiftApplicationApprovedWorkflow
					.setCoherentShiftApplication(coherentShiftApplicationReturn);
			shiftApplicationApprovedWorkflow.setCreatedBy(loggedInEmployeeVO);
			shiftApplicationApprovedWorkflow.setRemarks("Import By "
					+ getEmployeeNameWithNumber(loggedInEmployeeVO));
			shiftApplicationApprovedWorkflow
					.setTimesheetStatusMaster(timesheetStatusMasterVO);
			if (StringUtils.isBlank(overtimeDetailDTO.getUpdatedDate())) {
				shiftApplicationApprovedWorkflow.setCreatedDate(DateUtils
						.getCurrentTimestampWithTime());
			} else {
				shiftApplicationApprovedWorkflow.setCreatedDate(DateUtils
						.stringToTimestamp(overtimeDetailDTO.getUpdatedDate(),
								PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
			}
			coherentShiftApplicationWorkflowDAO
					.save(shiftApplicationApprovedWorkflow);

		}

		return importEmployeeClaimFrm;

	}

	public void setOvertimeDetailImportDataDTO(
			List<DataImportLogDTO> dataImportLogDTOs,
			ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShiftForm,
			HashMap<String, CoherentOvertimeDetailReportDTO> overtimeDetailDTOMap,
			Long companyId) {
		for (HashMap<String, String> map : importEmployeeOvertimeShiftForm
				.getImportedData()) {
			CoherentOvertimeDetailReportDTO overtimeDetailDTO = new CoherentOvertimeDetailReportDTO();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get(PayAsiaConstants.HASH_KEY_ROW_NUMBER);
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				key = key.trim();

				if (key != PayAsiaConstants.HASH_KEY_ROW_NUMBER) {
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_EMPLOYEENO)) {
						overtimeDetailDTO.setEmployeeNumber(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_APPROVED_DATE)) {
						overtimeDetailDTO.setUpdatedDate(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_NO_OF_SHIFT)) {
						overtimeDetailDTO.setNoOfShift(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_MONTH)
							&& StringUtils.isNotBlank(value)) {
						overtimeDetailDTO.setMonthDate(value);
						overtimeDetailDTOMap.put(rowNumber, overtimeDetailDTO);
					}
				}
			}
		}
	}

	/**
	 * Purpose: To Validate imported Overtime Data.
	 * 
	 * @param overtimeDetailDTOMap
	 *            the overtimeDetailDTOMap
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImpotedData(
			List<DataImportLogDTO> dataImportLogDTOs,
			HashMap<String, CoherentOvertimeDetailReportDTO> overtimeDetailDTOMap,
			Long companyId) {
		HashMap<String, Long> employeeNumberMap = new HashMap<String, Long>();
		List<Tuple> employeeNameListVO = employeeDAO
				.getEmployeeNameTupleList(companyId);
		for (Tuple empTuple : employeeNameListVO) {
			String employeeName = (String) empTuple.get(
					getAlias(Employee_.employeeNumber), String.class);
			Long employeeId = (Long) empTuple.get(
					getAlias(Employee_.employeeId), Long.class);
			employeeNumberMap.put(employeeName.toUpperCase(), employeeId);
		}

		Set<String> keySet = overtimeDetailDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			CoherentOvertimeDetailReportDTO overtimeDetailDTO = overtimeDetailDTOMap
					.get(key);

			String rowNumber = key;
			if (key.equalsIgnoreCase(PayAsiaConstants.HASH_KEY_ROW_NUMBER)) {
				continue;
			}
			if (StringUtils.isNotBlank(overtimeDetailDTO.getEmployeeNumber())) {
				if (!employeeNumberMap.containsKey(overtimeDetailDTO
						.getEmployeeNumber().toUpperCase())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.COHERENT_REPORT_EMPLOYEENO,
							"payasia.invalid.employee.number",
							Long.parseLong(rowNumber));
					continue;
				}
				overtimeDetailDTO.setEmployeeId(employeeNumberMap
						.get(overtimeDetailDTO.getEmployeeNumber()
								.toUpperCase()));
				// Month
				if (StringUtils.isBlank(overtimeDetailDTO.getMonthDate())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.COHERENT_REPORT_MONTH,
							"payasia.empty", Long.parseLong(rowNumber));
					continue;
				}
			}
			overtimeDetailDTOMap.put(key, overtimeDetailDTO);
		}
	}

	public void setClaimReviewersDataImportLogs(
			List<DataImportLogDTO> dataImportLogDTOs, String key,
			String remarks, Long rowNumber) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setColName(key);
		dataImportLogDTO.setRemarks(remarks);
		dataImportLogDTO.setRowNumber(rowNumber + 1);
		dataImportLogDTOs.add(dataImportLogDTO);
	}

}
