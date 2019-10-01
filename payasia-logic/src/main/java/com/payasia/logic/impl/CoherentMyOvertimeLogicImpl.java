package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CoherentConditionDTO;
import com.payasia.common.dto.EmailDataDTO;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.CoherentTimesheetDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CoherentOTEmployeeListDAO;
import com.payasia.dao.CoherentOvertimeApplicationDAO;
import com.payasia.dao.CoherentOvertimeApplicationDetailDAO;
import com.payasia.dao.CoherentOvertimeApplicationReviewerDAO;
import com.payasia.dao.CoherentOvertimeApplicationWorkflowDAO;
import com.payasia.dao.CoherentTimesheetPreferenceDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.CoherentOTEmployeeList;
import com.payasia.dao.bean.CoherentOvertimeApplication;
import com.payasia.dao.bean.CoherentOvertimeApplicationDetail;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer;
import com.payasia.dao.bean.CoherentOvertimeApplicationWorkflow;
import com.payasia.dao.bean.CoherentTimesheetPreference;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.CoherentMyOvertimeLogic;
import com.payasia.logic.CoherentTimesheetMailLogic;
import com.payasia.logic.CoherentTimesheetReportsLogic;

@Component
public class CoherentMyOvertimeLogicImpl implements CoherentMyOvertimeLogic {
	private static final Logger LOGGER = Logger.getLogger(CoherentMyOvertimeLogicImpl.class);
	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	WorkFlowRuleMasterDAO workflowRuleMasterDAO;
	@Resource
	TimesheetStatusMasterDAO timesheetStatusMasterDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeHolidayCalendarDAO employeeHolidayCalendarDAO;
	@Resource
	CoherentTimesheetMailLogic coherentTimesheetMailLogic;
	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	TimesheetBatchDAO timesheetBatchDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;
	@Resource
	CoherentOvertimeApplicationDAO coherentOvertimeApplicationDAO;
	@Resource
	CoherentTimesheetPreferenceDAO coherentTimesheetPreferenceDAO;
	@Resource
	CoherentOvertimeApplicationDetailDAO coherentOvertimeApplicationDetailDAO;
	@Resource
	CoherentOvertimeApplicationWorkflowDAO coherentOvertimeApplicationWorkflowDAO;
	@Resource
	CoherentOvertimeApplicationReviewerDAO coherentOvertimeApplicationReviewerDAO;
	@Resource
	CoherentTimesheetReportsLogic coherentTimesheetReportsLogic;
	@Resource
	CoherentOTEmployeeListDAO coherentOTEmployeeListDAO;

	@Override
	public AddClaimFormResponse getPendingTimesheet(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText, Long companyId) {

		CoherentConditionDTO conditionDTO = new CoherentConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setBatch(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.LEAVE_REVIEWER_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setReviewer("%" + searchText.trim() + "%");
			}
		}

		conditionDTO.setEmployeeId(empId);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_DRAFT);
		conditionDTO.setClaimStatus(claimStatus);
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		int recordSize = (coherentOvertimeApplicationDAO.getCountForCondition(conditionDTO, companyId)).intValue();

		List<CoherentOvertimeApplication> pendingClaims = coherentOvertimeApplicationDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentOvertimeApplication otTimesheet : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(otTimesheet.getTimesheetBatch().getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editcoherentEmployeeApplication("
							+ otTimesheet.getOvertimeApplicationID() + ");'>[Edit]</a></span>");
			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);

			addOTForm.setCreateDate(DateUtils.timeStampToString(otTimesheet.getCreatedDate()));

			addOTForm.setClaimApplicationId(otTimesheet.getOvertimeApplicationID());
			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					employeeTimesheetReviewerDAO.findByCondition(empId, otTimesheet.getCompany().getCompanyId()));

			Collections.sort(employeeClaimReviewers, new EmployeeReviewerComp());
			for (EmployeeTimesheetReviewer employeeOTReviewer : employeeClaimReviewers) {
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					addOTForm.setClaimReviewer1(getEmployeeNameWithNumber(employeeOTReviewer.getEmployeeReviewer()));

				}
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					addOTForm.setClaimReviewer2(getEmployeeNameWithNumber(employeeOTReviewer.getEmployeeReviewer()));

				}
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					addOTForm.setClaimReviewer3(getEmployeeNameWithNumber(employeeOTReviewer.getEmployeeReviewer()));

				}

			}

			addClaimFormList.add(addOTForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
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

	private class ClaimReviewerComp implements Comparator<CoherentOvertimeApplicationReviewer> {
		public int compare(CoherentOvertimeApplicationReviewer templateField,
				CoherentOvertimeApplicationReviewer compWithTemplateField) {
			if (templateField.getOvertimeApplciationReviewerID() > compWithTemplateField
					.getOvertimeApplciationReviewerID()) {
				return 1;
			} else if (templateField.getOvertimeApplciationReviewerID() < compWithTemplateField
					.getOvertimeApplciationReviewerID()) {
				return -1;
			}
			return 0;
		}
	}

	private class EmployeeReviewerComp implements Comparator<EmployeeTimesheetReviewer> {
		public int compare(EmployeeTimesheetReviewer templateField, EmployeeTimesheetReviewer compWithTemplateField) {
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

	@Override
	public AddClaimFormResponse getSubmittedTimesheet(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText, Long companyId) {
		CoherentConditionDTO conditionDTO = new CoherentConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setBatch(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.LEAVE_REVIEWER_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setReviewer("%" + searchText.trim() + "%");
			}
		}
		conditionDTO.setEmployeeId(empId);
		conditionDTO.setVisibleToEmployee(true);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
		conditionDTO.setClaimStatus(claimStatus);

		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		int recordSize = (coherentOvertimeApplicationDAO.getCountForCondition(conditionDTO, companyId)).intValue();

		List<CoherentOvertimeApplication> pendingClaims = coherentOvertimeApplicationDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentOvertimeApplication claimApplication : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
			String reviewer1Status = "";
			String reviewer2Status = "";

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch().getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			/*ID ENCRYPT*/
			String claimStatusMode = ",\"submitted\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewcoherentEmployeeApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getOvertimeApplicationID()) + claimStatusMode + ");'>[View]</a></span>");

			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addOTForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication.getOvertimeApplicationID());

			List<CoherentOvertimeApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getCoherentOvertimeApplicationReviewers());

			Collections.sort(claimApplicationReviewers, new ClaimReviewerComp());

			int revCount = 1;
			for (CoherentOvertimeApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					CoherentOvertimeApplicationWorkflow coherentWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());

					if (coherentWorkflow == null) {

						addOTForm.setClaimReviewer1(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, claimAppReviewer.getEmployeeReviewer()));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (coherentWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										claimAppReviewer.getEmployeeReviewer()));

						claimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ coherentWorkflow.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer1(String.valueOf(claimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					}
				}
				if (revCount == 2) {

					CoherentOvertimeApplicationWorkflow coherentWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());
					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

						addOTForm.setClaimReviewer2(
								getStatusImage("NA", pageContextPath, claimAppReviewer.getEmployeeReviewer()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (coherentWorkflow == null) {

							addOTForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath, claimAppReviewer.getEmployeeReviewer()));
							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
						} else if (coherentWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder claimReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
											claimAppReviewer.getEmployeeReviewer()));

							claimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ coherentWorkflow.getCreatedDate() + "</span>");

							addOTForm.setClaimReviewer2(String.valueOf(claimReviewer2));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

						}

					}

				}
				if (revCount == 3) {
					CoherentOvertimeApplicationWorkflow coherentWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());
					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

						addOTForm.setClaimReviewer3(
								getStatusImage("NA", pageContextPath, claimAppReviewer.getEmployeeReviewer()));

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (coherentWorkflow == null) {
							addOTForm.setClaimReviewer3(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath, claimAppReviewer.getEmployeeReviewer()));
						} else if (coherentWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder claimReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
											claimAppReviewer.getEmployeeReviewer()));

							claimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ coherentWorkflow.getCreatedDate() + "</span>");

							addOTForm.setClaimReviewer3(String.valueOf(claimReviewer3));

						}

					}

				}
				revCount++;
			}

			addClaimFormList.add(addOTForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	private String getStatusImage(String status, String contextPath, Employee employee) {
		String imagePath = contextPath + "/resources/images/icon/16/";
		if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {
			imagePath = imagePath + "pending.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
			imagePath = imagePath + "approved.png";
		} else if ("NA".equalsIgnoreCase(status)) {
			imagePath = imagePath + "pending-next-level.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
			imagePath = imagePath + "rejected.png";
		}
		String employeeName = getEmployeeNameWithNumber(employee);

		return "<img src='" + imagePath + "'  />  " + employeeName;

	}

	@Override
	public AddClaimFormResponse getApprovedTimesheet(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText, Long companyId) {
		CoherentConditionDTO conditionDTO = new CoherentConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setBatch(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.LEAVE_REVIEWER_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setReviewer("%" + searchText.trim() + "%");
			}
		}
		conditionDTO.setEmployeeId(empId);
		conditionDTO.setVisibleToEmployee(true);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		conditionDTO.setClaimStatus(claimStatus);

		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		int recordSize = (coherentOvertimeApplicationDAO.getCountForCondition(conditionDTO, companyId)).intValue();

		List<CoherentOvertimeApplication> pendingClaims = coherentOvertimeApplicationDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentOvertimeApplication claimApplication : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch().getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			/*ID ENCRYPT*/
			String claimStatusMode = ",\"approved\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewcoherentEmployeeApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getOvertimeApplicationID()) + claimStatusMode + ");'>[View]</a></span>");

			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addOTForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication.getOvertimeApplicationID());

			List<CoherentOvertimeApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getCoherentOvertimeApplicationReviewers());

			Collections.sort(claimApplicationReviewers, new ClaimReviewerComp());
			int revCount = 1;
			for (CoherentOvertimeApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());
					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));

					ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
							+ applicationWorkflow.getCreatedDate() + "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

				}
				if (revCount == 2) {

					CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder ClaimReviewer2 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));

					if (applicationWorkflow.getCreatedDate() != null) {
						ClaimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate() + "</span>");
					}

					addOTForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

				}
				if (revCount == 3) {

					CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder ClaimReviewer3 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));

					ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
							+ applicationWorkflow.getCreatedDate() + "</span>");

					addOTForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

				}
				revCount++;
			}

			addClaimFormList.add(addOTForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public AddClaimFormResponse getRejectedTimesheet(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText, Long companyId) {
		CoherentConditionDTO conditionDTO = new CoherentConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setBatch(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.LEAVE_REVIEWER_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setReviewer("%" + searchText.trim() + "%");
			}
		}
		conditionDTO.setEmployeeId(empId);
		conditionDTO.setVisibleToEmployee(true);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
		conditionDTO.setClaimStatus(claimStatus);

		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		int recordSize = (coherentOvertimeApplicationDAO.getCountForCondition(conditionDTO, companyId)).intValue();

		List<CoherentOvertimeApplication> pendingClaims = coherentOvertimeApplicationDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentOvertimeApplication claimApplication : pendingClaims) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch().getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			/*ID ENCRYPT*/
			String claimStatusMode = ",\"rejected\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewcoherentEmployeeApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getOvertimeApplicationID()) + claimStatusMode + ");'>[View]</a></span>");
			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addOTForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication.getOvertimeApplicationID());

			List<CoherentOvertimeApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getCoherentOvertimeApplicationReviewers());

			Collections.sort(claimApplicationReviewers, new ClaimReviewerComp());

			Boolean reviewStatus = false;
			int revCount = 1;
			for (CoherentOvertimeApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (reviewStatus) {
					continue;
				}

				if (revCount == 1) {

					CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());

					if (applicationWorkflow == null) {

						addOTForm.setClaimReviewer1(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, claimAppReviewer.getEmployeeReviewer()));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										claimAppReviewer.getEmployeeReviewer()));

						ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
						reviewStatus = true;
						StringBuilder ClaimReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED, pageContextPath,
										claimAppReviewer.getEmployeeReviewer()));

						ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

					}
				}
				if (revCount == 2) {

					CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());

					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
							|| reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm.setClaimReviewer2(
								getStatusImage("NA", pageContextPath, claimAppReviewer.getEmployeeReviewer()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							addOTForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath, claimAppReviewer.getEmployeeReviewer()));
							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
						} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder ClaimReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
											claimAppReviewer.getEmployeeReviewer()));

							ClaimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addOTForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

						} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
							reviewStatus = true;
							addOTForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED,
									pageContextPath, claimAppReviewer.getEmployeeReviewer()));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

						}

					}

				}
				if (revCount == 3) {
					CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
							.findByCondition(claimApplication.getOvertimeApplicationID(),
									claimAppReviewer.getEmployeeReviewer().getEmployeeId());
					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
							|| reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm.setClaimReviewer3(
								getStatusImage("NA", pageContextPath, claimAppReviewer.getEmployeeReviewer()));

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							addOTForm.setClaimReviewer3(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath, claimAppReviewer.getEmployeeReviewer()));
						} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder ClaimReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
											claimAppReviewer.getEmployeeReviewer()));

							ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addOTForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

						} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

							StringBuilder ClaimReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED, pageContextPath,
											claimAppReviewer.getEmployeeReviewer()));

							ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addOTForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

						}

					}

				}
				revCount++;
			}

			addClaimFormList.add(addOTForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public AddClaimFormResponse getWithdrawnTimesheet(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText, Long companyId) {
		CoherentConditionDTO conditionDTO = new CoherentConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setBatch(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.LEAVE_REVIEWER_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setReviewer("%" + searchText.trim() + "%");
			}
		}
		conditionDTO.setEmployeeId(empId);
		conditionDTO.setVisibleToEmployee(true);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN);
		conditionDTO.setClaimStatus(claimStatus);

		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		int recordSize = (coherentOvertimeApplicationDAO.getCountForCondition(conditionDTO, companyId)).intValue();

		List<CoherentOvertimeApplication> pendingClaims = coherentOvertimeApplicationDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentOvertimeApplication claimApplication : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch().getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			/*ID ENCRYPT*/
			String claimStatusMode = ",\"withdrawn\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewcoherentEmployeeApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getOvertimeApplicationID()) + claimStatusMode + ");'>[View]</a></span>");
			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addOTForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication.getOvertimeApplicationID());

			List<CoherentOvertimeApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getCoherentOvertimeApplicationReviewers());

			Collections.sort(claimApplicationReviewers, new ClaimReviewerComp());
			int revCount = 1;
			for (CoherentOvertimeApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					addOTForm.setClaimReviewer1(getEmployeeNameWithNumber(claimAppReviewer.getEmployeeReviewer()));

				}
				if (revCount == 2) {

					addOTForm.setClaimReviewer2(getEmployeeNameWithNumber(claimAppReviewer.getEmployeeReviewer()));

				}
				if (revCount == 3) {

					addOTForm.setClaimReviewer3(getEmployeeNameWithNumber(claimAppReviewer.getEmployeeReviewer()));

				}
				revCount++;
			}

			addClaimFormList.add(addOTForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	private class EmployeeOTReviewerComp implements Comparator<EmployeeTimesheetReviewer> {
		public int compare(EmployeeTimesheetReviewer templateField, EmployeeTimesheetReviewer compWithTemplateField) {
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

	/**
	 * Comparator Class for Ordering coherentWorkflow List
	 */
	private class CoherentOTTimesheetReviewerComp implements Comparator<CoherentOvertimeApplicationReviewer> {
		public int compare(CoherentOvertimeApplicationReviewer templateField,
				CoherentOvertimeApplicationReviewer compWithTemplateField) {
			if (templateField.getOvertimeApplciationReviewerID() > compWithTemplateField
					.getOvertimeApplciationReviewerID()) {
				return 1;
			} else if (templateField.getOvertimeApplciationReviewerID() < compWithTemplateField
					.getOvertimeApplciationReviewerID()) {
				return -1;
			}
			return 0;

		}

	}

	private Employee getDelegatedEmployee(Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterOT = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_TYPE_COHERENT_TIMESHEET_DESC);

		WorkflowDelegate workflowDelegateLeave = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
				appCodeMasterOT.getAppCodeID());

		if (workflowDelegateLeave != null) {
			workflowDelegate = workflowDelegateLeave;
		} else {

			AppCodeMaster appCodeMasterALL = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
					PayAsiaConstants.WORKFLOW_CATEGORY_ALL);

			WorkflowDelegate workflowDelegateALL = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
					appCodeMasterALL.getAppCodeID());

			workflowDelegate = workflowDelegateALL;
		}

		if (workflowDelegate != null) {
			return workflowDelegate.getEmployee2();
		}
		return emp;
	}

	@Override
	public boolean getCanWithdraw(long timesheetId) {
		CoherentOvertimeApplication coherentOvertimeApplication = coherentOvertimeApplicationDAO.findById(timesheetId);
		Set<CoherentOvertimeApplicationReviewer> otTsheetRevieweres = coherentOvertimeApplication
				.getCoherentOvertimeApplicationReviewers();

		List<CoherentOvertimeApplicationReviewer> reviewersList = new ArrayList<CoherentOvertimeApplicationReviewer>(
				otTsheetRevieweres);

		if (reviewersList != null && !reviewersList.isEmpty()) {
			Collections.sort(reviewersList, new CoherentOTTimesheetReviewerComp());

			if (reviewersList.get(0).isPending())
				return true;
		}
		return false;
	}

	@Override
	public List<String> getHolidaysFor(Long employeeId, Date startDate, Date endDate) {
		EmployeeHolidayCalendar empHolidaycalendar = employeeHolidayCalendarDAO.getCalendarDetail(employeeId, startDate,
				endDate);
		List<String> toReturn = new ArrayList<String>();
		if (empHolidaycalendar != null) {
			CompanyHolidayCalendar compHolidayCalendar = empHolidaycalendar.getCompanyHolidayCalendar();
			Set<CompanyHolidayCalendarDetail> calDetails = compHolidayCalendar.getCompanyHolidayCalendarDetails();
			DateTime holidayDt = new DateTime();
			DateTime startdt = new DateTime(new Timestamp(startDate.getTime()));
			DateTime endDt = new DateTime(new Timestamp(endDate.getTime()));
			DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
			for (CompanyHolidayCalendarDetail compHolidayDetail : calDetails) {
				holidayDt = new DateTime(compHolidayDetail.getHolidayDate());
				if (startdt.compareTo(holidayDt) < 0 && endDt.compareTo(holidayDt) > 0) {
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
	public boolean saveAsDraftTimesheet(long timesheetId, long employeeId, Long companyId) {
		CoherentOvertimeApplication otTimesheet = coherentOvertimeApplicationDAO.findById(timesheetId);
		try {
			TimesheetStatusMaster otStatusMaster = timesheetStatusMasterDAO
					.findByName(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
			otTimesheet.setTimesheetStatusMaster(otStatusMaster);
			coherentOvertimeApplicationDAO.update(otTimesheet);

			for (CoherentOvertimeApplicationReviewer otTsheetReviewer : otTimesheet
					.getCoherentOvertimeApplicationReviewers()) {
				coherentOvertimeApplicationReviewerDAO.delete(otTsheetReviewer);
			}

			for (CoherentOvertimeApplicationWorkflow workflow : otTimesheet.getCoherentOvertimeApplicationWorkflows()) {
				coherentOvertimeApplicationWorkflowDAO.delete(workflow);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public AddClaimFormResponse getAllTimesheet(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText) {

		CoherentConditionDTO conditionDto = new CoherentConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setBatch(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.LEAVE_REVIEWER_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setReviewer("%" + searchText.trim() + "%");
			}
		}

		if (StringUtils.isNotBlank(fromDate)) {
			conditionDto.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDto.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		int recordSize = coherentOvertimeApplicationDAO.getCountByConditionForEmployee(empId, fromDate, toDate,
				conditionDto);

		Boolean visibleToEmployee = true;
		List<CoherentOvertimeApplication> otTimesheetList = coherentOvertimeApplicationDAO
				.findByConditionForEmployee(pageDTO, sortDTO, empId, fromDate, toDate, visibleToEmployee, conditionDto);

		List<AddClaimForm> addOtFormList = new ArrayList<AddClaimForm>();
		for (CoherentOvertimeApplication otApplication : otTimesheetList) {
			AddClaimForm addOtForm = new AddClaimForm();

			if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				StringBuilder claimTemplateEdit = new StringBuilder();
				claimTemplateEdit.append(otApplication.getTimesheetBatch().getTimesheetBatchDesc());
				claimTemplateEdit.append("<br>");
				/*ID ENCRYPT*/
				claimTemplateEdit
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editcoherentEmployeeApplication("
								+ 	FormatPreserveCryptoUtil.encrypt(otApplication.getOvertimeApplicationID()) + ");'>[Edit]</a></span>");

				addOtForm.setClaimTemplateName(String.valueOf(claimTemplateEdit));
			} else {
				StringBuilder claimTemplateView = new StringBuilder();
				claimTemplateView.append(otApplication.getTimesheetBatch().getTimesheetBatchDesc());
				claimTemplateView.append("<br>");
				String claimStatusMode = ",\"submitted\"";

				if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {

					claimStatusMode = ",\"Approved\"";
				} else if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
						|| otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
					claimStatusMode = ",\"submitted\"";
				} else if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
					claimStatusMode = ",\"Withdrawn\"";
				} else if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED)) {
					claimStatusMode = ",\"Rejected\"";
				}

				claimTemplateView
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewcoherentEmployeeApplication("
								+ FormatPreserveCryptoUtil.encrypt(otApplication.getOvertimeApplicationID()) + claimStatusMode + ");'>[View]</a></span>");
				addOtForm.setClaimTemplateName(String.valueOf(claimTemplateView));
			}

			addOtForm.setCreateDate(DateUtils.timeStampToString(otApplication.getCreatedDate()));
			if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
							.equals(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {

				if (otApplication.getCreatedBy() != null) {
					if (otApplication.getCreatedBy().equals(String.valueOf(empId))) {
						StringBuilder claimWithDrawLink = new StringBuilder();
						claimWithDrawLink
								.append("<a class='alink' href='#' onClick='javascipt:withDrawClaimApplication("
										+ FormatPreserveCryptoUtil.encrypt(otApplication.getOvertimeApplicationID()) + ");'>Withdraw</a>");

						addOtForm.setAction(String.valueOf(claimWithDrawLink));
					}
				}
			}
			addOtForm.setClaimApplicationId(otApplication.getOvertimeApplicationID());
			List<CoherentOvertimeApplicationReviewer> claimAppReviewers = new ArrayList<>(
					otApplication.getCoherentOvertimeApplicationReviewers());

			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					employeeTimesheetReviewerDAO.findByCondition(empId, otApplication.getCompany().getCompanyId()));

			if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
				getPendingClaimReviewers(employeeClaimReviewers, addOtForm);
			}
			if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
				getApprovedClaimReviewers(claimAppReviewers, addOtForm, pageContextPath, otApplication);
			}
			if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				getSubmittedClaimReviewers(claimAppReviewers, addOtForm, pageContextPath, otApplication);
			}
			if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
				getWithdrawClaimReviewers(claimAppReviewers, addOtForm);
			}
			if (otApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
				getRejectedClaimReviewers(claimAppReviewers, addOtForm, pageContextPath, otApplication);
			}

			addOtFormList.add(addOtForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addOtFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	private class IngersollOTTimesheetReviewerComp implements Comparator<CoherentOvertimeApplicationReviewer> {
		public int compare(CoherentOvertimeApplicationReviewer templateField,
				CoherentOvertimeApplicationReviewer compWithTemplateField) {
			if (templateField.getOvertimeApplciationReviewerID() > compWithTemplateField
					.getOvertimeApplciationReviewerID()) {
				return 1;
			} else if (templateField.getOvertimeApplciationReviewerID() < compWithTemplateField
					.getOvertimeApplciationReviewerID()) {
				return -1;
			}
			return 0;

		}

	}

	public void getRejectedClaimReviewers(List<CoherentOvertimeApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath, CoherentOvertimeApplication coherentTimesheet) {

		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(otApplicationReviewers, new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (CoherentOvertimeApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());

				if (applicationWorkflow == null) {

					addOTForm.setClaimReviewer1(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING, pageContextPath,
							otApplicationReviewer.getEmployeeReviewer()));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
									otApplicationReviewer.getEmployeeReviewer()));

					ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
							+ applicationWorkflow.getCreatedDate() + "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED, pageContextPath,
									otApplicationReviewer.getEmployeeReviewer()));

					ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
							+ applicationWorkflow.getCreatedDate() + "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

				}
			}
			if (revCount == 2) {

				CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());

				if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addOTForm.setClaimReviewer2(
							getStatusImage("NA", pageContextPath, otApplicationReviewer.getEmployeeReviewer()));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {

						addOTForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, otApplicationReviewer.getEmployeeReviewer()));
						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer2 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										otApplicationReviewer.getEmployeeReviewer()));

						ClaimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED,
								pageContextPath, otApplicationReviewer.getEmployeeReviewer()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

					}

				}

			}
			if (revCount == 3) {
				CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());
				if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addOTForm.setClaimReviewer3(
							getStatusImage("NA", pageContextPath, otApplicationReviewer.getEmployeeReviewer()));

				} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {
						addOTForm.setClaimReviewer3(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, otApplicationReviewer.getEmployeeReviewer()));
					} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										otApplicationReviewer.getEmployeeReviewer()));

						ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

					} else if (applicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED, pageContextPath,
										otApplicationReviewer.getEmployeeReviewer()));

						ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

					}

				}

			}
			revCount++;
		}
	}

	public void getSubmittedClaimReviewers(List<CoherentOvertimeApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath, CoherentOvertimeApplication coherentTimesheet) {
		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(otApplicationReviewers, new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (CoherentOvertimeApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				CoherentOvertimeApplicationWorkflow coherentWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());

				if (coherentWorkflow == null) {

					addOTForm.setClaimReviewer1(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING, pageContextPath,
							otApplicationReviewer.getEmployeeReviewer()));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (coherentWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					StringBuilder claimReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
									otApplicationReviewer.getEmployeeReviewer()));

					claimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
							+ coherentWorkflow.getCreatedDate() + "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(claimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				}
			}
			if (revCount == 2) {

				CoherentOvertimeApplicationWorkflow coherentWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());

				if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addOTForm.setClaimReviewer2(
							getStatusImage("NA", pageContextPath, otApplicationReviewer.getEmployeeReviewer()));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (coherentWorkflow == null) {

						addOTForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, otApplicationReviewer.getEmployeeReviewer()));
						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (coherentWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer2 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										otApplicationReviewer.getEmployeeReviewer()));

						claimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ coherentWorkflow.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer2(String.valueOf(claimReviewer2));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					}

				}

			}
			if (revCount == 3) {
				CoherentOvertimeApplicationWorkflow coherentWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());
				if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addOTForm.setClaimReviewer3(
							getStatusImage("NA", pageContextPath, otApplicationReviewer.getEmployeeReviewer()));

				} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (coherentWorkflow == null) {
						addOTForm.setClaimReviewer3(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, otApplicationReviewer.getEmployeeReviewer()));
					} else if (coherentWorkflow.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer3 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										otApplicationReviewer.getEmployeeReviewer()));

						claimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ coherentWorkflow.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer3(String.valueOf(claimReviewer3));

					}

				}

			}
			revCount++;
		}
	}

	public void getWithdrawClaimReviewers(List<CoherentOvertimeApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOtForm) {
		Collections.sort(otApplicationReviewers, new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (CoherentOvertimeApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				addOtForm.setClaimReviewer1(getEmployeeNameWithNumber(otApplicationReviewer.getEmployeeReviewer()));

			}
			if (revCount == 2) {

				addOtForm.setClaimReviewer2(getEmployeeNameWithNumber(otApplicationReviewer.getEmployeeReviewer()));

			}
			if (revCount == 3) {

				addOtForm.setClaimReviewer3(getEmployeeNameWithNumber(otApplicationReviewer.getEmployeeReviewer()));

			}
			revCount++;
		}
	}

	public void getPendingClaimReviewers(List<EmployeeTimesheetReviewer> empOtReviewers, AddClaimForm addOTForm) {
		Collections.sort(empOtReviewers, new EmployeeOTReviewerComp());
		for (EmployeeTimesheetReviewer employeeOtReviewer : empOtReviewers) {
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

				addOTForm.setClaimReviewer1(getEmployeeNameWithNumber(employeeOtReviewer.getEmployeeReviewer()));

			}
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

				addOTForm.setClaimReviewer2(getEmployeeNameWithNumber(employeeOtReviewer.getEmployeeReviewer()));

			}
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

				addOTForm.setClaimReviewer3(getEmployeeNameWithNumber(employeeOtReviewer.getEmployeeReviewer()));

			}

		}
	}

	public void getApprovedClaimReviewers(List<CoherentOvertimeApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath, CoherentOvertimeApplication coherentTimesheet) {
		Collections.sort(otApplicationReviewers, new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (CoherentOvertimeApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());
				StringBuilder ClaimReviewer1 = new StringBuilder(getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
						pageContextPath, otApplicationReviewer.getEmployeeReviewer()));

				ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
						+ applicationWorkflow.getCreatedDate() + "</span>");

				addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

			}
			if (revCount == 2) {

				CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());
				if (applicationWorkflow == null) {
					continue;
				}

				StringBuilder ClaimReviewer2 = new StringBuilder(getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
						pageContextPath, otApplicationReviewer.getEmployeeReviewer()));

				if (applicationWorkflow.getCreatedDate() != null) {
					ClaimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
							+ applicationWorkflow.getCreatedDate() + "</span>");
				}

				addOTForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

			}
			if (revCount == 3) {

				CoherentOvertimeApplicationWorkflow applicationWorkflow = coherentOvertimeApplicationWorkflowDAO
						.findByCondition(coherentTimesheet.getOvertimeApplicationID(),
								otApplicationReviewer.getEmployeeReviewer().getEmployeeId());
				if (applicationWorkflow == null) {
					continue;
				}

				StringBuilder ClaimReviewer3 = new StringBuilder(getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
						pageContextPath, otApplicationReviewer.getEmployeeReviewer()));

				ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
						+ applicationWorkflow.getCreatedDate() + "</span>");

				addOTForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

			}
			revCount++;
		}
	}

	@Override
	public List<LundinOTBatchDTO> getOTBacthesByCompanyId(long companyId, Long employeeId) {
		List<LundinOTBatchDTO> coherentOTBatchDTOList = new ArrayList<LundinOTBatchDTO>();
		List<TimesheetBatch> allCoherentTBatches = new ArrayList<TimesheetBatch>();
		List<Long> batchIdList = new ArrayList<Long>();
		List<String> otStatusList = new ArrayList<String>();
		// otStatusList.add("Rejected");
		otStatusList.add("Withdrawn");

		TimesheetBatch TimesheetBatch = timesheetBatchDAO.getBatchByCurrentDate(companyId,
				DateUtils.convertDateToTimeStamp(Calendar.getInstance().getTime()));
		if (TimesheetBatch == null) {
			return coherentOTBatchDTOList;
		}
		allCoherentTBatches = timesheetBatchDAO.getCurrentAndPreviousBatchByCompany(companyId,
				DateUtils.convertDateToTimeStamp(Calendar.getInstance().getTime()));
		for (TimesheetBatch batch : allCoherentTBatches) {
			batchIdList.add(batch.getTimesheetBatchId());
		}

		List<Long> overtimeApplicationBatchList = coherentOvertimeApplicationDAO.findByBatchId(employeeId, batchIdList,
				otStatusList);
		if (overtimeApplicationBatchList != null && overtimeApplicationBatchList.size() > 0 && batchIdList.size() > 0) {
			batchIdList.removeAll(overtimeApplicationBatchList);
		}

		for (TimesheetBatch coherentOTBatch : allCoherentTBatches) {
			if (coherentOTBatch == null) {
				continue;
			}
			if (!batchIdList.contains(coherentOTBatch.getTimesheetBatchId())) {
				continue;
			}
			LundinOTBatchDTO coherentOtBatchDto = new LundinOTBatchDTO();
			coherentOtBatchDto.setStartDate(coherentOTBatch.getStartDate());
			coherentOtBatchDto.setEndDate(coherentOTBatch.getEndDate());
			coherentOtBatchDto.setOtBatchDesc(coherentOTBatch.getTimesheetBatchDesc());
			coherentOtBatchDto.setOtBatchId(coherentOTBatch.getTimesheetBatchId());

			coherentOTBatchDTOList.add(coherentOtBatchDto);
		}

		return coherentOTBatchDTOList;
	}

	@Override
	public void createOTBatches(int yearOfBatch, long companyId) {
		CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		int cutOffDay = 0;
		if (coherentTimesheetPreferenceVO != null) {
			cutOffDay = coherentTimesheetPreferenceVO.getCutoffDay();
		} else {
			return;
		}
		Company company = companyDAO.findById(companyId);
		TimesheetBatch timesheetBatchVO = timesheetBatchDAO.findLatestBatchLion(companyId);

		int iterationNo = 12;
		if (timesheetBatchVO == null) {
			int allowedCutOffLimit = 27;
			boolean isLeapYear = ((yearOfBatch % 4 == 0) && (yearOfBatch % 100 != 0) || (yearOfBatch % 400 == 0));
			// boolean isLeapYear = true;
			if (isLeapYear) {
				allowedCutOffLimit = allowedCutOffLimit + 1;
			}
			if (cutOffDay <= allowedCutOffLimit) {
				smallerDateBatch(cutOffDay, yearOfBatch, company);
			} else {
				largerDateBatch(cutOffDay, yearOfBatch, isLeapYear, company);
			}

		} else {
			Date currentDate = new Date();

			Calendar currentDateCalender = Calendar.getInstance();
			currentDateCalender.setTime(currentDate);
			currentDateCalender.set(Calendar.MILLISECOND, 0);
			currentDateCalender.set(Calendar.SECOND, 0);
			currentDateCalender.set(Calendar.MINUTE, 0);
			currentDateCalender.set(Calendar.HOUR_OF_DAY, 0);

			Calendar endDateCalender = Calendar.getInstance();
			endDateCalender.setTime(new Date(timesheetBatchVO.getStartDate().getTime()));
			endDateCalender.set(Calendar.MILLISECOND, 0);
			endDateCalender.set(Calendar.SECOND, 0);
			endDateCalender.set(Calendar.MINUTE, 0);
			endDateCalender.set(Calendar.HOUR_OF_DAY, 0);

			if ((endDateCalender.getTime()).compareTo(currentDateCalender.getTime()) >= 0) {
				iterationNo = 0;
			}

			// create batches on the basis of last batch
			Timestamp startDate = new Timestamp((new Date().getTime()));
			Timestamp endDate = new Timestamp((new Date().getTime()));
			endDate = timesheetBatchVO.getEndDate();
			startDate = addDays(new Date(endDate.getTime()), 1);

			Calendar sDateCalender = Calendar.getInstance();
			sDateCalender.setTime(new Date(timesheetBatchVO.getEndDate().getTime()));
			sDateCalender.set(Calendar.MILLISECOND, 0);
			sDateCalender.set(Calendar.SECOND, 0);
			sDateCalender.set(Calendar.MINUTE, 0);
			sDateCalender.set(Calendar.HOUR_OF_DAY, 0);
			sDateCalender.add(Calendar.DAY_OF_MONTH, 1);

			if (iterationNo > 0) {
				createBatchWOLeapYear(companyId, startDate, cutOffDay, iterationNo);
			}
		}

	}

	private void smallerDateBatch(int cutDay, int year, Company company) {
		Calendar startDate = GregorianCalendar.getInstance();
		Calendar endDate = GregorianCalendar.getInstance();

		startDate.set(year - 1, 11, cutDay + 1);
		endDate.set(year, 0, cutDay);
		saveBatch(startDate, endDate, company);

		for (int count = 0; count < 11; count++) {
			startDate.set(year, count, cutDay + 1);
			endDate.set(year, count + 1, cutDay);
			saveBatch(startDate, endDate, company);

		}
	}

	private void largerDateBatch(int cutDay, int year, boolean leapYear, Company company) {
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		int daysInMonth = 0;
		int useMaxLimit = 11;
		if (cutDay == 31) {
			useMaxLimit = 12;
		} else {

			startDate.set(Calendar.DAY_OF_MONTH, cutDay + 1);
			startDate.set(Calendar.YEAR, year - 1);
			startDate.set(Calendar.MONTH, 11);

			endDate.set(Calendar.DAY_OF_MONTH, cutDay);
			endDate.set(Calendar.YEAR, year);
			endDate.set(Calendar.MONTH, 0);
			saveBatch(startDate, endDate, company);
		}

		for (int month = 0; month < useMaxLimit; month++) {

			if (cutDay == 31) {
				startDate.set(Calendar.DAY_OF_MONTH, 1);
				startDate.set(Calendar.YEAR, year);
				startDate.set(Calendar.MONTH, month);
				int days = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				endDate.set(Calendar.DAY_OF_MONTH, days);
				endDate.set(Calendar.YEAR, year);
				endDate.set(Calendar.MONTH, month);
			} else {
				if (month == 3 || month == 5 || month == 8 || month == 10)
					daysInMonth = 30;
				else if (month == 1)
					daysInMonth = (leapYear) ? 29 : 28;
				else
					daysInMonth = 31;
				if (month == 0) {
					startDate.set(Calendar.DAY_OF_MONTH, cutDay + 1);
					startDate.set(Calendar.YEAR, year);
					startDate.set(Calendar.MONTH, month);
					if (leapYear) {
						endDate.set(Calendar.DAY_OF_MONTH, 29);
					} else {
						endDate.set(Calendar.DAY_OF_MONTH, 28);
					}
					endDate.set(Calendar.YEAR, year);
					endDate.set(Calendar.MONTH, month + 1);
				} else if (month == 1) {
					startDate.set(Calendar.DAY_OF_MONTH, 1);
					startDate.set(Calendar.YEAR, year);
					startDate.set(Calendar.MONTH, month + 1);

					endDate.set(Calendar.DAY_OF_MONTH, cutDay);
					endDate.set(Calendar.YEAR, year);
					endDate.set(Calendar.MONTH, month + 1);
				} else {
					startDate.add(Calendar.DAY_OF_MONTH, daysInMonth);
					startDate.set(Calendar.DAY_OF_MONTH, cutDay + 1);
					startDate.set(Calendar.YEAR, year);
					startDate.set(Calendar.MONTH, month);

					endDate.add(Calendar.DAY_OF_MONTH, daysInMonth);
					endDate.set(Calendar.DAY_OF_MONTH, cutDay);
					endDate.set(Calendar.YEAR, year);
					endDate.set(Calendar.MONTH, month + 1);
				}
			}
			saveBatch(startDate, endDate, company);

		}
	}

	private void saveBatch(Calendar startDate, Calendar endDate, Company company) {
		TimesheetBatch coherentOtBatch = new TimesheetBatch();
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.HOUR, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.HOUR, 0);
		endDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.MILLISECOND, 0);
		DateTime fromDt = new DateTime(startDate.getTimeInMillis());
		MutableDateTime toDt = new MutableDateTime(endDate.getTimeInMillis());

		Timestamp startDateTmp = new Timestamp(fromDt.getMillis());
		Timestamp endDateTmp = new Timestamp(toDt.getMillis());

		String batchDescr = toDt.toString("MMM YYYY");
		// + PayAsiaConstants.OT_BATCH_DESCRIPTION;

		coherentOtBatch.setStartDate(startDateTmp);
		coherentOtBatch.setEndDate(endDateTmp);
		coherentOtBatch.setTimesheetBatchDesc(batchDescr);
		coherentOtBatch.setCompany(company);

		timesheetBatchDAO.save(coherentOtBatch);
	}

	private void createBatchWOLeapYear(long companyId, Timestamp startDate, int cutOffDay, int iterationNo) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM YYYY");
		Timestamp endDate;
		boolean traversed = true;
		for (int i = 0; i < iterationNo; i++) {
			TimesheetBatch timesheetBatch = new TimesheetBatch();

			timesheetBatch.setCompany(companyDAO.findById(companyId));
			timesheetBatch.setStartDate(startDate);

			Calendar scalendar = Calendar.getInstance();
			scalendar.setTime(new Date(startDate.getTime()));
			int smonth = scalendar.get(Calendar.MONTH);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(startDate.getTime()));
			calendar.add(Calendar.MONTH, 1);
			int month = calendar.get(Calendar.MONTH);
			endDate = new Timestamp(calendar.getTimeInMillis());

			int allowedCutOffLimit = 27;
			int yearOfBatch = calendar.get(Calendar.YEAR);
			boolean isLeapYear = ((yearOfBatch % 4 == 0) && (yearOfBatch % 100 != 0) || (yearOfBatch % 400 == 0));
			if (isLeapYear) {
				allowedCutOffLimit = allowedCutOffLimit + 1;
			}
			if (month == 1 && (cutOffDay == 29 || cutOffDay == 30)) {
				// endDate = addDays(new Date(endDate.getTime()), 1);
			}
			if (traversed && smonth == 2 && month == 3 && (cutOffDay == 29 || cutOffDay == 30)) {
				traversed = false;
				calendar.add(Calendar.MONTH, -1);
				calendar.set(Calendar.DAY_OF_MONTH, cutOffDay + 1);
				endDate = new Timestamp(calendar.getTimeInMillis());
			}
			if (!traversed && smonth == 2 && month == 3 && (cutOffDay == 29 || cutOffDay == 30)) {
				endDate = addDays(new Date(endDate.getTime()), -1);
			}
			if (month != 1 && month != 3 && (cutOffDay == 29 || cutOffDay == 30)) {
				endDate = addDays(new Date(endDate.getTime()), -1);
			}
			//
			if (cutOffDay <= 27 || cutOffDay == 31) {
				endDate = addDays(new Date(endDate.getTime()), -1);
			}

			if (cutOffDay == 28 && isLeapYear) {
				endDate = addDays(new Date(endDate.getTime()), -1);
			}

			timesheetBatch.setEndDate(endDate);

			String endDesc = simpleDateFormat.format(new Date(endDate.getTime()));
			String description = endDesc;
			timesheetBatch.setTimesheetBatchDesc(description);

			startDate = addDays(new Date(endDate.getTime()), 1);
			timesheetBatchDAO.save(timesheetBatch);
		}
	}

	public static Timestamp addDays(Date d, int days) {
		Date newDate = new Date();
		newDate.setTime(d.getTime() + days * 1000 * 60 * 60 * 24);

		Timestamp timestamp = new Timestamp(newDate.getTime());

		return timestamp;
	}

	@Override
	public String getCoherentTimesheetJSON(long batchId, long companyId, long employeeId) {
		JSONObject jsonObject = new JSONObject();
		List<EmployeeTimesheetReviewer> employeeTimesheetReviewerList = employeeTimesheetReviewerDAO
				.findByCondition(employeeId, companyId);

		try {
			if (employeeTimesheetReviewerList != null && employeeTimesheetReviewerList.size() > 0) {
				jsonObject.put("reviewerStatus", true);
			} else {
				jsonObject.put("reviewerStatus", false);
				return jsonObject.toString();
			}
		} catch (JSONException e) {
			LOGGER.error(e.getMessage(), e);
		}

		TimesheetBatch timesheetBatch = timesheetBatchDAO.findById(batchId);
		Company companyVO = companyDAO.findById(companyId);

		String companyDateFormat = companyDAO.findById(companyId).getDateFormat();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(companyDateFormat);

		Employee employeeVO = employeeDAO.findById(employeeId);

		List<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetails = new ArrayList<CoherentOvertimeApplicationDetail>();
		List<CoherentOvertimeApplication> employeeTimesheetApplicationOld = coherentOvertimeApplicationDAO
				.findByTimesheetBatchId(batchId, companyId, employeeId);

		Long timesheetId = 0l;
		if (employeeTimesheetApplicationOld.size() == 0) {

			CoherentOvertimeApplication employeeTimesheetApplication = new CoherentOvertimeApplication();
			employeeTimesheetApplication.setCompany(companyVO);
			employeeTimesheetApplication.setEmployee(employeeVO);
			employeeTimesheetApplication.setTimesheetBatch(timesheetBatch);
			employeeTimesheetApplication.setRemarks("");
			employeeTimesheetApplication.setTimesheetStatusMaster(timesheetStatusMasterDAO.findByName("Draft"));
			employeeTimesheetApplication.setTotalOT10Day(0.0);
			employeeTimesheetApplication.setTotalOT20Day(0.0);
			employeeTimesheetApplication.setTotalOTHours(0.0);
			employeeTimesheetApplication.setTotalOT15Hours(0.0);
			CoherentOvertimeApplication coherentOvertimeApplicationVO = coherentOvertimeApplicationDAO
					.saveAndReturn(employeeTimesheetApplication);
 /*ID ENCRYPT*/
			coherentOvertimeApplicationDetails = coherentOvertimeApplicationDetailDAO
					.getTimesheetDetailsByTimesheetId(FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplicationVO.getOvertimeApplicationID()));
			timesheetId = coherentOvertimeApplicationVO.getOvertimeApplicationID();
			Timestamp timesheetBatchStartDate = timesheetBatch.getStartDate();
			Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();
			timesheetBatchStartDate.setHours(0);
			timesheetBatchStartDate.setMinutes(0);
			Timestamp newTimestamp = timesheetBatchStartDate;
			CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail;

			AppCodeMaster dayTypeCode = appCodeMasterDAO.findByCategoryAndDesc(
					PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE, PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL);

			int dayNum = 1;
			long oneDay = 1 * 24 * 60 * 60 * 1000;
			if (coherentOvertimeApplicationDetails.size() == 0) {
				do {
					newTimestamp.setHours(0);
					newTimestamp.setMinutes(0);

					coherentOvertimeApplicationDetail = new CoherentOvertimeApplicationDetail();
					coherentOvertimeApplicationDetail.setCoherentOvertimeApplication(coherentOvertimeApplicationVO);
					coherentOvertimeApplicationDetail.setOvertimeDate(newTimestamp);
					coherentOvertimeApplicationDetail.setStartTime(newTimestamp);
					coherentOvertimeApplicationDetail.setEndTime(newTimestamp);
					coherentOvertimeApplicationDetail.setMealDuration(newTimestamp);
					coherentOvertimeApplicationDetail.setOtHours(0.0);
					coherentOvertimeApplicationDetail.setOt15Hours(0.0);
					coherentOvertimeApplicationDetail.setOt10Day(0.0);
					coherentOvertimeApplicationDetail.setOt20Day(0.0);
					coherentOvertimeApplicationDetail.setRemarks("");
					coherentOvertimeApplicationDetail.setStartTimeChanged(false);
					coherentOvertimeApplicationDetail.setEndTimeChanged(false);
					coherentOvertimeApplicationDetail.setMealDurationChanged(false);
					coherentOvertimeApplicationDetail.setDayTypeChanged(false);
					coherentOvertimeApplicationDetail.setDayType(dayTypeCode);
					coherentOvertimeApplicationDetailDAO.save(coherentOvertimeApplicationDetail);

					newTimestamp = new Timestamp(timesheetBatchStartDate.getTime() + oneDay * dayNum);
					dayNum++;

				} while (newTimestamp.compareTo(timesheetBatchEndDate) <= 0);
			}
		} else {
			timesheetId = employeeTimesheetApplicationOld.get(0).getOvertimeApplicationID();
		}
		coherentOvertimeApplicationDetails = coherentOvertimeApplicationDetailDAO
				.getTimesheetDetailsByTimesheetId(timesheetId);

		JSONArray coherentEmployeeTimesheetApplicationDetailsJson = new JSONArray();

		CoherentOTEmployeeList coherentOTEmployeeList = coherentOTEmployeeListDAO.findById(employeeId);

		SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

		for (CoherentOvertimeApplicationDetail applicationDetail : coherentOvertimeApplicationDetails) {
			try {
				JSONObject timesheetJsonObject = new JSONObject();
				timesheetJsonObject.put("employeeTimesheetDetailID",
						applicationDetail.getOvertimeApplicationDetailID());
				timesheetJsonObject.put("timesheetDate", simpleDateFormat.format(applicationDetail.getOvertimeDate()));
				timesheetJsonObject.put("timesheetDay", format2.format(applicationDetail.getOvertimeDate()));

				Date date = DateUtils.stringToDate(simpleDateFormat.format(applicationDetail.getOvertimeDate()),
						companyDateFormat);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				timesheetJsonObject.put("month", cal.get(Calendar.MONTH));
				timesheetJsonObject.put("day", cal.get(Calendar.DAY_OF_MONTH));
				timesheetJsonObject.put("year", cal.get(Calendar.YEAR));

				Date timesheetDate = new Date(applicationDetail.getOvertimeDate().getTime());
				List<String> holidayList = getHolidaysFor(employeeId, timesheetDate, timesheetDate);
				if (holidayList.size() > 0) {
					timesheetJsonObject.put("isHoliday", "true");
				} else {
					timesheetJsonObject.put("isHoliday", "false");
				}

				timesheetJsonObject.put("inTime",
						convertTimestampToString(applicationDetail.getStartTime(), "yyyy-MM-dd"));
				timesheetJsonObject.put("outTime",
						convertTimestampToString(applicationDetail.getEndTime(), "yyyy-MM-dd"));
				timesheetJsonObject.put("breakTimeHours", applicationDetail.getMealDuration());
				timesheetJsonObject.put("totalHoursWorked", applicationDetail.getOtHours());
				timesheetJsonObject.put("ot15hours", applicationDetail.getOt15Hours());
				timesheetJsonObject.put("ot10day", applicationDetail.getOt10Day());
				timesheetJsonObject.put("ot20day", applicationDetail.getOt20Day());
				timesheetJsonObject.put("remarks", applicationDetail.getRemarks());
				timesheetJsonObject.put("dayType", applicationDetail.getDayType().getCodeDesc().toLowerCase());

				coherentEmployeeTimesheetApplicationDetailsJson.put(timesheetJsonObject);
			} catch (JSONException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		try {

			Collections.sort(employeeTimesheetReviewerList, new EmployeeReviewerComp());
			String approvalSteps = "";
			for (EmployeeTimesheetReviewer applicationReviewer : employeeTimesheetReviewerList) {
				approvalSteps += getEmployeeName(applicationReviewer.getEmployeeReviewer()) + " -> ";
			}
			if (approvalSteps.endsWith(" -> ")) {
				approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
			}
			jsonObject.put("approvalSteps", approvalSteps);

			jsonObject.put("startDate", simpleDateFormat.format(timesheetBatch.getStartDate()));
			jsonObject.put("endDate", simpleDateFormat.format(timesheetBatch.getEndDate()));
			jsonObject.put("timesheetBatchId", timesheetBatch.getTimesheetBatchId());
			 /*ID ENCRYPT*/
			jsonObject.put("timesheetId", FormatPreserveCryptoUtil.encrypt(timesheetId));
			jsonObject.put("coherentEmployeeTimesheetApplicationDetails",
					coherentEmployeeTimesheetApplicationDetailsJson);

			jsonObject.put("employeeId", employeeVO.getEmployeeNumber());

			jsonObject.put("employeeName", getEmployeeName(employeeVO));
			jsonObject.put("batchName", timesheetBatch.getTimesheetBatchDesc());

			CoherentTimesheetPreference coherentTimesheetPreference = coherentTimesheetPreferenceDAO
					.findByCompanyId(companyId);
			if (coherentTimesheetPreference != null && coherentTimesheetPreference.getWorkingHoursInADay() > 0.0) {
				jsonObject.put("workingHoursInADay", coherentTimesheetPreference.getWorkingHoursInADay());
			} else {
				jsonObject.put("workingHoursInADay", 0.0);
			}
			if (coherentOTEmployeeList != null) {
				jsonObject.put("iScoherentOTEmployee", true);
			} else {
				jsonObject.put("iScoherentOTEmployee", false);
			}
			setDeptCostCentre(employeeId, companyId, employeeVO.getEmployeeNumber(), jsonObject,
					companyVO.getDateFormat());
		} catch (JSONException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return jsonObject.toString();
	}

	@Override
	public Map<String, String> updateCoherentEmployeeTimesheetApplicationDetailEmployee(
			CoherentTimesheetDTO coherentTimesheetDTO) {
		Map<String, String> statusMap = new HashMap<String, String>();
		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;
		Long employeeId = Long.parseLong(UserContext.getUserId());
		CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail = coherentOvertimeApplicationDetailDAO
				.findById(coherentTimesheetDTO.getEmployeeTimesheetDetailId(),employeeId);

		CoherentOvertimeApplication coherentEmployeeTimesheet = coherentOvertimeApplicationDAO.findById(
				coherentOvertimeApplicationDetail.getCoherentOvertimeApplication().getOvertimeApplicationID());

		// Overtime Limit 72 Hours
		Double totalOT15Hours = 0.0;
		Set<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetailVOList = coherentEmployeeTimesheet
				.getCoherentOvertimeApplicationDetails();
		for (CoherentOvertimeApplicationDetail applicationDetail : coherentOvertimeApplicationDetailVOList) {
			if (coherentOvertimeApplicationDetail.getOvertimeApplicationDetailID() == applicationDetail
					.getOvertimeApplicationDetailID()) {
				continue;
			}
			Date timesheetDate = new Date(applicationDetail.getOvertimeDate().getTime());
			List<String> holidayList = getHolidaysFor(coherentEmployeeTimesheet.getEmployee().getEmployeeId(),
					timesheetDate, timesheetDate);
			boolean isHoliday = false;

			if (holidayList.size() > 0) {
				isHoliday = true;
			}
			if (applicationDetail.getDayType().getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL) && !isHoliday) {
				totalOT15Hours += applicationDetail.getOt15Hours();
			}
		}

		Date timesheetDt = new Date(coherentOvertimeApplicationDetail.getOvertimeDate().getTime());
		List<String> holidayListForDt = getHolidaysFor(coherentEmployeeTimesheet.getEmployee().getEmployeeId(),
				timesheetDt, timesheetDt);
		boolean isHolidayDt = false;

		if (holidayListForDt.size() > 0) {
			isHolidayDt = true;
		}
		if (coherentTimesheetDTO.getDayType().equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL)
				&& !isHolidayDt) {
			totalOT15Hours += Double.parseDouble(coherentTimesheetDTO.getOt15hours());
		}
		CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
				.findByCompanyId(coherentOvertimeApplicationDetail.getCompanyId());
		if (totalOT15Hours > PayAsiaConstants.COHERENT_TOTAL_NUM_OF_HOURS_LIMIT
				&& coherentTimesheetPreferenceVO.getIs_validation_72_Hours()) {
			statusMap.put(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS,
					PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS);

			return statusMap;
		} else {
			statusMap.put(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS, "");
		}

		String[] oldInTime = coherentOvertimeApplicationDetail.getStartTime().toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = coherentOvertimeApplicationDetail.getEndTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = coherentOvertimeApplicationDetail.getMealDuration().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!coherentTimesheetDTO.getInTime().equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + coherentTimesheetDTO.getInTime() + ":" + oldInTime2[2];
			coherentOvertimeApplicationDetail.setStartTime(Timestamp.valueOf(newInTime));
			inTimeStatus = 1;
		}
		if (!coherentTimesheetDTO.getOutTime().equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {
			String newOutTime = oldOutTime[0] + " " + coherentTimesheetDTO.getOutTime() + ":" + oldOutTime2[2];
			coherentOvertimeApplicationDetail.setEndTime(Timestamp.valueOf(newOutTime));
			outTimeStatus = 1;
		}
		if (!coherentTimesheetDTO.getBreakTime().equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + coherentTimesheetDTO.getBreakTime() + ":" + oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			coherentOvertimeApplicationDetail.setMealDuration(newBreakTimeTimestamp);
			breakTimeStatus = 1;
		}

		coherentOvertimeApplicationDetail.setOtHours(Double.parseDouble(coherentTimesheetDTO.getTotalHours()));
		coherentOvertimeApplicationDetail.setOt10Day(Double.parseDouble(coherentTimesheetDTO.getOt10day()));
		coherentOvertimeApplicationDetail.setOt15Hours(Double.parseDouble(coherentTimesheetDTO.getOt15hours()));
		coherentOvertimeApplicationDetail.setOt20Day(Double.parseDouble(coherentTimesheetDTO.getOt20day()));

		if (coherentTimesheetDTO.getDayType().equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL)) {
			coherentOvertimeApplicationDetail.setDayType(appCodeMasterDAO.findByCategoryAndDesc(
					PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE, PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL));
		}
		if (coherentTimesheetDTO.getDayType().equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_REST_DAY)) {
			coherentOvertimeApplicationDetail.setDayType(appCodeMasterDAO.findByCategoryAndDesc(
					PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE, PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_REST_DAY));
		}
		coherentOvertimeApplicationDetail.setRemarks(coherentTimesheetDTO.getRemarks());
		coherentOvertimeApplicationDetailDAO.update(coherentOvertimeApplicationDetail);

		coherentEmployeeTimesheet.setTotalOT10Day(Double.parseDouble(coherentTimesheetDTO.getGrandot10day()));
		coherentEmployeeTimesheet.setTotalOT15Hours(Double.parseDouble(coherentTimesheetDTO.getGrandot15hours()));
		coherentEmployeeTimesheet.setTotalOT20Day(Double.parseDouble(coherentTimesheetDTO.getGrandot20day()));
		coherentEmployeeTimesheet.setTotalOTHours(Double.parseDouble(coherentTimesheetDTO.getGrandtotalhours()));
		coherentOvertimeApplicationDAO.update(coherentEmployeeTimesheet);

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));

		return statusMap;
	}

	private String convertTimestampToString(Timestamp timestamp, String dateFormat) {
		Date date = new Date(timestamp.getTime());
		dateFormat += " HH:mm";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}

	@Override
	public String getTimesheetApplications(long timesheetId, long employeeId, long companyId) {
		CoherentOTEmployeeList coherentOTEmployeeList = coherentOTEmployeeListDAO.findById(employeeId);
		CoherentOvertimeApplication coherentTimesheetApplication = coherentOvertimeApplicationDAO.findById(timesheetId,employeeId,companyId);
		if(coherentTimesheetApplication!=null)
		{
		List<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetails = coherentOvertimeApplicationDetailDAO
				.getTimesheetDetailsByTimesheetId(timesheetId);

		JSONObject jsonObject = new JSONObject();
		JSONArray coherentEmployeeTimesheetApplicationDetailsJson = new JSONArray();

		SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

		String companyDateFormat = companyDAO.findById(companyId).getDateFormat();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(companyDateFormat);

		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Double grantOTTotalHours = 0.0;
		Double grantOT15TotalHours = 0.0;
		Double grantOT10day = 0.0;
		Double grantOT20day = 0.0;
		for (CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail : coherentOvertimeApplicationDetails) {
			try {
				JSONObject timesheetJsonObject = new JSONObject();
				grantOTTotalHours += coherentOvertimeApplicationDetail.getOtHours();
				grantOT15TotalHours += coherentOvertimeApplicationDetail.getOt15Hours();
				grantOT10day += coherentOvertimeApplicationDetail.getOt10Day();
				grantOT20day += coherentOvertimeApplicationDetail.getOt20Day();

				timesheetJsonObject.put("employeeTimesheetDetailID",
						coherentOvertimeApplicationDetail.getOvertimeApplicationDetailID());
				timesheetJsonObject.put("timesheetDate",
						simpleDateFormat.format(coherentOvertimeApplicationDetail.getOvertimeDate()));

				Date date = DateUtils.stringToDate(
						simpleDateFormat.format(coherentOvertimeApplicationDetail.getOvertimeDate()),
						companyDateFormat);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				timesheetJsonObject.put("month", cal.get(Calendar.MONTH));
				timesheetJsonObject.put("day", cal.get(Calendar.DAY_OF_MONTH));
				timesheetJsonObject.put("year", cal.get(Calendar.YEAR));

				timesheetJsonObject.put("timesheetDay",
						format2.format(coherentOvertimeApplicationDetail.getOvertimeDate()));
				timesheetJsonObject.put("dayType", coherentOvertimeApplicationDetail.getDayType().getCodeDesc());

				Date timesheetDate = new Date(coherentOvertimeApplicationDetail.getOvertimeDate().getTime());

				List<String> holidayList = getHolidaysFor(employeeId, timesheetDate, timesheetDate);

				if (holidayList.size() > 0) {
					timesheetJsonObject.put("isHoliday", "true");
				} else {
					timesheetJsonObject.put("isHoliday", "false");
				}

				/*
				 * timesheetJsonObject.put( "inTime", convertTimestampToString(
				 * coherentOvertimeApplicationDetail .getStartTime(),
				 * "yyyy-MM-dd"));
				 */

				timesheetJsonObject.put("inTime",
						convertTimestampToString(coherentOvertimeApplicationDetail.getStartTime(), "HH:mm"));
				timesheetJsonObject.put("inTimeHoursChanged",
						String.valueOf(coherentOvertimeApplicationDetail.isStartTimeChanged()));
				/*
				 * timesheetJsonObject.put( "outTime", convertTimestampToString(
				 * coherentOvertimeApplicationDetail.getEndTime(),
				 * "yyyy-MM-dd"));
				 */
				timesheetJsonObject.put("outTime",
						convertTimestampToString(coherentOvertimeApplicationDetail.getEndTime(), "HH:mm"));
				timesheetJsonObject.put("outTimeHoursChanged",
						String.valueOf(coherentOvertimeApplicationDetail.isEndTimeChanged()));
				timesheetJsonObject.put("breakTimeHours", coherentOvertimeApplicationDetail.getMealDuration());
				timesheetJsonObject.put("breakTimeHoursChanged",
						String.valueOf(coherentOvertimeApplicationDetail.isMealDurationChanged()));

				timesheetJsonObject.put("totalHoursWorked", coherentOvertimeApplicationDetail.getOtHours());
				timesheetJsonObject.put("ot15hours", coherentOvertimeApplicationDetail.getOt15Hours());
				timesheetJsonObject.put("ot10day", coherentOvertimeApplicationDetail.getOt10Day());
				timesheetJsonObject.put("ot20day", coherentOvertimeApplicationDetail.getOt20Day());
				timesheetJsonObject.put("remarks", coherentOvertimeApplicationDetail.getRemarks());

				coherentEmployeeTimesheetApplicationDetailsJson.put(timesheetJsonObject);
			} catch (JSONException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

		TimesheetBatch timesheetBatch = timesheetBatchDAO
				.findById(coherentTimesheetApplication.getTimesheetBatch().getTimesheetBatchId());

		Timestamp timesheetBatchStartDate = timesheetBatch.getStartDate();
		Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();

		try {
			jsonObject.put("canWithdraw", getCanWithdraw(timesheetId));
			jsonObject.put("startDate", simpleDateFormat.format(timesheetBatchStartDate));
			jsonObject.put("endDate", simpleDateFormat.format(timesheetBatchEndDate));
			jsonObject.put("timesheetBatchId", timesheetBatch.getTimesheetBatchId());
			jsonObject.put("coherentEmployeeTimesheetApplicationDetails",
					coherentEmployeeTimesheetApplicationDetailsJson);
			Employee employee = employeeDAO.findById(employeeId);
			jsonObject.put("employeeId", employee.getEmployeeNumber());
			jsonObject.put("grandTotalHours", grantOTTotalHours.toString());
			jsonObject.put("grandot15hours", grantOT15TotalHours.toString());
			jsonObject.put("grandot20day", grantOT20day.toString());
			jsonObject.put("grandot10day", grantOT10day.toString());
			if (coherentOTEmployeeList != null) {
				jsonObject.put("iScoherentOTEmployee", true);
			} else {
				jsonObject.put("iScoherentOTEmployee", false);
			}
			jsonObject.put("employeeName", getEmployeeName(coherentTimesheetApplication.getEmployee()));
			jsonObject.put("timesheetStatus",
					coherentTimesheetApplication.getTimesheetStatusMaster().getTimesheetStatusName());
			jsonObject.put("remarks", coherentTimesheetApplication.getRemarks());
			jsonObject.put("batchName", coherentTimesheetApplication.getTimesheetBatch().getTimesheetBatchDesc());
			String approvalSteps = "";
			if (!coherentTimesheetApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
				List<CoherentOvertimeApplicationReviewer> applicationReviewers = new ArrayList<>(
						coherentTimesheetApplication.getCoherentOvertimeApplicationReviewers());
				Collections.sort(applicationReviewers, new ClaimReviewerComp());

				for (CoherentOvertimeApplicationReviewer applicationReviewer : applicationReviewers) {
					approvalSteps += getEmployeeName(applicationReviewer.getEmployeeReviewer()) + " -> ";
				}
				if (approvalSteps.endsWith(" -> ")) {
					approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
				}
				jsonObject.put("approvalSteps", approvalSteps);
			}
			if (coherentTimesheetApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
				List<EmployeeTimesheetReviewer> applicationReviewers = new ArrayList<>(employeeTimesheetReviewerDAO
						.findByCondition(coherentTimesheetApplication.getEmployee().getEmployeeId(),
								coherentTimesheetApplication.getCompany().getCompanyId()));
				Collections.sort(applicationReviewers, new EmployeeReviewerComp());

				for (EmployeeTimesheetReviewer applicationReviewer : applicationReviewers) {
					approvalSteps += getEmployeeName(applicationReviewer.getEmployeeReviewer()) + " -> ";
				}
				if (approvalSteps.endsWith(" -> ")) {
					approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
				}
				jsonObject.put("approvalSteps", approvalSteps);
			}

			CoherentTimesheetPreference coherentTimesheetPreference = coherentTimesheetPreferenceDAO
					.findByCompanyId(companyId);
			if (coherentTimesheetPreference != null && coherentTimesheetPreference.getWorkingHoursInADay() > 0.0) {
				jsonObject.put("workingHoursInADay", coherentTimesheetPreference.getWorkingHoursInADay());
			} else {
				jsonObject.put("workingHoursInADay", 0.0);
			}

			setDeptCostCentre(employeeId, companyId, coherentTimesheetApplication.getEmployee().getEmployeeNumber(),
					jsonObject, companyDateFormat);

		} catch (JSONException exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

		return jsonObject.toString();
		}
		return "";
	}

	private void setDeptCostCentre(long employeeId, long companyId, String employeeNumber, JSONObject jsonObject,
			String companyDateFormat) throws JSONException {
		List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic.getEmpDynFieldsValueList(companyId,
				companyDateFormat, employeeId);

		for (Object[] deptObject : deptCostCentreEmpObjectList) {
			if (deptObject != null && deptObject[3] != null && deptObject[3].equals(employeeNumber)) {
				if (StringUtils.isNotBlank(String.valueOf(deptObject[0]))
						&& !String.valueOf(deptObject[0]).equalsIgnoreCase("null")) {
					jsonObject.put("department", String.valueOf(deptObject[0]));
				} else {
					jsonObject.put("department", "");
				}
				if (StringUtils.isNotBlank(String.valueOf(deptObject[2]))
						&& !String.valueOf(deptObject[2]).equalsIgnoreCase("null")) {
					jsonObject.put("costCentre", String.valueOf(deptObject[2]));
				} else {
					jsonObject.put("costCentre", "");
				}

			}
		}
	}

	@Override
	public String submitToWorkFlow(List<LundinEmployeeTimesheetReviewerDTO> coherentEmployeeOTReviewerDTOs,
			long timesheetId, String remarks, long companyId, long employeeId) {
		CoherentOvertimeApplication coherentOvertimeApplication = coherentOvertimeApplicationDAO.findById(timesheetId);

		Double totalOT15Hours = 0.0;
		Set<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetailVOList = coherentOvertimeApplication
				.getCoherentOvertimeApplicationDetails();
		for (CoherentOvertimeApplicationDetail applicationDetail : coherentOvertimeApplicationDetailVOList) {

			Date timesheetDate = new Date(applicationDetail.getOvertimeDate().getTime());
			List<String> holidayList = getHolidaysFor(coherentOvertimeApplication.getEmployee().getEmployeeId(),
					timesheetDate, timesheetDate);
			boolean isHoliday = false;

			if (holidayList.size() > 0) {
				isHoliday = true;
			}
			if (applicationDetail.getDayType().getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL) && !isHoliday) {
				totalOT15Hours += applicationDetail.getOt15Hours();
			}
		}
		CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (totalOT15Hours > PayAsiaConstants.COHERENT_TOTAL_NUM_OF_HOURS_LIMIT
				&& coherentTimesheetPreferenceVO.getIs_validation_72_Hours()) {
			return PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS;
		}
		if (coherentOvertimeApplication.getTotalOTHours() <= 0) {
			return PayAsiaConstants.COHERENT_ZERO_TOTAL_HOURS;
		}

		boolean isSuccessfullyFor = false;
		Employee firstEmployee = null;
		boolean firstEntry = true;
		for (LundinEmployeeTimesheetReviewerDTO coherentEmployeeOTReviewerDTO : coherentEmployeeOTReviewerDTOs) {

			try {
				CoherentOvertimeApplicationReviewer coherentOTTimesheetReviewer = new CoherentOvertimeApplicationReviewer();

				coherentOTTimesheetReviewer.setCoherentOvertimeApplication(coherentOvertimeApplication);
				coherentOTTimesheetReviewer.setWorkFlowRuleMaster(
						workflowRuleMasterDAO.findByID(coherentEmployeeOTReviewerDTO.getWorkFlowRuleMasterId()));
				if (firstEntry) {
					// firstEmployee = empDAO
					// .findById(coherentEmployeeOTReviewerDTO
					// .getEmployeeReviewerId());
					firstEmployee = getDelegatedEmployee(coherentEmployeeOTReviewerDTO.getEmployeeReviewerId());
					coherentOTTimesheetReviewer.setEmployeeReviewer(firstEmployee);

					coherentOTTimesheetReviewer.setPending(true);
					firstEntry = false;
				} else {
					coherentOTTimesheetReviewer.setEmployeeReviewer(
							getDelegatedEmployee(coherentEmployeeOTReviewerDTO.getEmployeeReviewerId()));
					coherentOTTimesheetReviewer.setPending(false);
				}

				coherentOvertimeApplicationReviewerDAO.save(coherentOTTimesheetReviewer);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}

		}
		TimesheetStatusMaster otStatusMaster = timesheetStatusMasterDAO
				.findByName(PayAsiaConstants.OT_STATUS_SUBMITTED);
		coherentOvertimeApplication.setTimesheetStatusMaster(otStatusMaster);
		coherentOvertimeApplication.setRemarks(remarks);
		coherentOvertimeApplicationDAO.update(coherentOvertimeApplication);
		CoherentOvertimeApplicationWorkflow coherentOtTimesheetWorkflow = new CoherentOvertimeApplicationWorkflow();
		coherentOtTimesheetWorkflow.setCompanyId(companyId);

		coherentOtTimesheetWorkflow.setRemarks("");
		coherentOtTimesheetWorkflow.setTimesheetStatusMaster(otStatusMaster);
		coherentOtTimesheetWorkflow.setCoherentOvertimeApplication(coherentOvertimeApplication);
		Employee loggedInEmp = employeeDAO.findById(employeeId);
		coherentOtTimesheetWorkflow.setCreatedBy(loggedInEmp);
		coherentOtTimesheetWorkflow.setCreatedDate(new Timestamp(new Date().getTime()));
		coherentOvertimeApplicationWorkflowDAO.save(coherentOtTimesheetWorkflow);
		isSuccessfullyFor = true;

		if (isSuccessfullyFor) {
			EmailDataDTO emailDataDTO = new EmailDataDTO();
			emailDataDTO.setTimesheetId(coherentOvertimeApplication.getOvertimeApplicationID());
			emailDataDTO.setEmployeeName(getEmployeeName(coherentOvertimeApplication.getEmployee()));
			emailDataDTO.setEmployeeNumber(coherentOvertimeApplication.getEmployee().getEmployeeNumber());
			emailDataDTO.setBatchDesc(coherentOvertimeApplication.getTimesheetBatch().getTimesheetBatchDesc());
			emailDataDTO.setOvertimeShiftType(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
			emailDataDTO.setEmailTo(firstEmployee.getEmail());
			emailDataDTO.setEmailFrom(loggedInEmp.getEmail());
			emailDataDTO.setReviewerFirstName(firstEmployee.getFirstName());
			emailDataDTO.setReviewerLastName(firstEmployee.getLastName());
			emailDataDTO.setReviewerCompanyId(firstEmployee.getCompany().getCompanyId());
			coherentTimesheetMailLogic.sendEMailForTimesheet(companyId, emailDataDTO,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPLY);

		}
		return "";
	}

	@Override
	public boolean withdrawTimesheetRequest(long timesheetId, long employeeId, Long companyId) {
		if (getCanWithdraw(timesheetId)) {
			CoherentOvertimeApplication otTimesheet = coherentOvertimeApplicationDAO.findById(timesheetId,employeeId,companyId);
			try {
				TimesheetStatusMaster otStatusMaster = timesheetStatusMasterDAO
						.findByName(PayAsiaConstants.OT_STATUS_DRAFT);
				otTimesheet.setTimesheetStatusMaster(otStatusMaster);
				coherentOvertimeApplicationDAO.update(otTimesheet);

				for (CoherentOvertimeApplicationReviewer otTsheetReviewer : otTimesheet
						.getCoherentOvertimeApplicationReviewers()) {
					coherentOvertimeApplicationReviewerDAO.delete(otTsheetReviewer);
				}

				for (CoherentOvertimeApplicationWorkflow workflow : otTimesheet
						.getCoherentOvertimeApplicationWorkflows()) {
					coherentOvertimeApplicationWorkflowDAO.delete(workflow);
				}

				List<EmployeeTimesheetReviewer> employeeTimesheetReviewerList = employeeTimesheetReviewerDAO
						.findByCondition(employeeId, companyId);
				EmployeeTimesheetReviewer firstApplicationReviewer = null;
				Collections.sort(employeeTimesheetReviewerList, new EmployeeReviewerComp());
				boolean firstRevFound = false;
				for (EmployeeTimesheetReviewer applicationReviewer : employeeTimesheetReviewerList) {
					if (!firstRevFound) {
						firstApplicationReviewer = applicationReviewer;
						firstRevFound = true;
					}
				}

				EmailDataDTO emailDataDTO = new EmailDataDTO();
				emailDataDTO.setTimesheetId(otTimesheet.getOvertimeApplicationID());
				emailDataDTO.setEmployeeName(getEmployeeName(otTimesheet.getEmployee()));
				emailDataDTO.setEmployeeNumber(otTimesheet.getEmployee().getEmployeeNumber());
				emailDataDTO.setBatchDesc(otTimesheet.getTimesheetBatch().getTimesheetBatchDesc());
				emailDataDTO.setOvertimeShiftType(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
				if (firstApplicationReviewer != null) {
					emailDataDTO.setEmailTo(firstApplicationReviewer.getEmployeeReviewer().getEmail());
				}
				emailDataDTO.setEmailFrom(otTimesheet.getEmployee().getEmail());

				coherentTimesheetMailLogic.sendWithdrawEmailForTimesheet(companyId, emailDataDTO,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_WITHDRAW);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}

	}

}
