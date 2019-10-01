package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.LundinTimesheetDelDTO;
import com.payasia.common.dto.LundinTimesheetDetailSaveDTO;
import com.payasia.common.dto.LundinTimesheetSaveDTO;
import com.payasia.common.dto.LundinTimesheetSaveDataDTO;
import com.payasia.common.dto.LundinTimewritingReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.LundinMyRequestForm;
import com.payasia.common.form.OTTimesheetWorkflowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LundinAFEDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.LundinDepartmentDAO;
import com.payasia.dao.LundinTimesheetDetailDAO;
import com.payasia.dao.LundinTimesheetPreferenceDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.TimesheetApplicationWorkflowDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.LundinDepartment;
import com.payasia.dao.bean.LundinTimesheetDetail;
import com.payasia.dao.bean.LundinTimesheetPreference;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LionTimesheetRequestLogic;
import com.payasia.logic.LundinTimesheetMailLogic;

@Component
public class LionTimesheetRequestLogicImpl implements LionTimesheetRequestLogic {
	private static final Logger LOGGER = Logger
			.getLogger(LionTimesheetRequestLogicImpl.class);
	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	EmployeeDAO empDAO;
	@Resource
	WorkFlowRuleMasterDAO workflowRuleMasterDAO;
	@Resource
	TimesheetApplicationReviewerDAO lundinTimesheetReviewerDAO;
	@Resource
	LundinTimesheetDetailDAO lundinTimesheetDetailDAO;
	@Resource
	EmployeeTimesheetReviewerDAO lundinEmployeeReviewerDAO;
	@Resource
	TimesheetStatusMasterDAO lundinStatusMasterDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	LundinAFEDAO lundinAFEDAO;
	@Resource
	LundinBlockDAO lundinBlockDAO;
	@Resource
	EmployeeTimesheetApplicationDAO lundinTimesheetDAO;
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
	TimesheetApplicationWorkflowDAO lundinTimesheetWorkflowDAO;

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
	LundinTimesheetPreferenceDAO lundinTimesheetPreferenceDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	LundinDepartmentDAO lundinDepartmentDAO;

	@Override
	public AddClaimFormResponse getPendingTimesheet(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText) {

		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setClaimGroup("%" + searchText.trim() + "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {

				conditionDTO.setClaimNumber(Long.parseLong(searchText.trim()));
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

		int recordSize = (lundinTimesheetDAO.getCountForCondition(conditionDTO))
				.intValue();

		List<EmployeeTimesheetApplication> pendingClaims = lundinTimesheetDAO
				.findByConditionLion(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication otTimesheet : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(otTimesheet.getTimesheetBatch()
					.getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editLundinApplication("
							+ otTimesheet.getTimesheetId()
							+ ");'>[Edit]</a></span>");
			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);

			addOTForm.setCreateDate(DateUtils.timeStampToString(otTimesheet
					.getCreatedDate()));

			addOTForm.setClaimApplicationId(otTimesheet.getTimesheetId());
			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					lundinEmpReviewerDAO.findByCondition(empId, otTimesheet
							.getCompany().getCompanyId()));

			Collections
					.sort(employeeClaimReviewers, new EmployeeReviewerComp());
			for (EmployeeTimesheetReviewer employeeOTReviewer : employeeClaimReviewers) {
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {

					addOTForm
							.setClaimReviewer1(getEmployeeName(employeeOTReviewer
									.getEmployeeReviewer()));

				}
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("2")) {

					addOTForm
							.setClaimReviewer2(getEmployeeName(employeeOTReviewer
									.getEmployeeReviewer()));

				}
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("3")) {

					addOTForm
							.setClaimReviewer3(getEmployeeName(employeeOTReviewer
									.getEmployeeReviewer()));

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
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}

	private class ClaimReviewerComp implements
			Comparator<TimesheetApplicationReviewer> {
		public int compare(TimesheetApplicationReviewer templateField,
				TimesheetApplicationReviewer compWithTemplateField) {
			if (templateField.getTimesheetReviewerId() > compWithTemplateField
					.getTimesheetReviewerId()) {
				return 1;
			} else if (templateField.getTimesheetReviewerId() < compWithTemplateField
					.getTimesheetReviewerId()) {
				return -1;
			}
			return 0;

		}

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

	@Override
	public AddClaimFormResponse getSubmittedTimesheet(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setClaimGroup("%" + searchText.trim() + "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {

				conditionDTO.setClaimNumber(Long.parseLong(searchText.trim()));
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

		int recordSize = (lundinTimesheetDAO.getCountForCondition(conditionDTO))
				.intValue();

		List<EmployeeTimesheetApplication> pendingClaims = lundinTimesheetDAO
				.findByConditionLion(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication claimApplication : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
			String reviewer1Status = "";
			String reviewer2Status = "";

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch()
					.getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"submitted\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewlionEmployeeApplication("
							+ claimApplication.getTimesheetId()
							+ claimStatusMode + ");'>[Edit]</a></span>");

			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addOTForm.setCreateDate(DateUtils
					.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication.getTimesheetId());

			List<TimesheetApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getTimesheetApplicationReviewers());

			Collections
					.sort(claimApplicationReviewers, new ClaimReviewerComp());

			int revCount = 1;
			for (TimesheetApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());

					if (lundinWorkflow == null) {

						addOTForm
								.setClaimReviewer1(getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_PENDING,
										pageContextPath,
										claimAppReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (lundinWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer1 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										claimAppReviewer.getEmployee()));

						claimReviewer1
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ lundinWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer1(String
								.valueOf(claimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					}
				}
				if (revCount == 2) {

					TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());
					if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

						addOTForm
								.setClaimReviewer2(getStatusImage("NA",
										pageContextPath,
										claimAppReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (lundinWorkflow == null) {

							addOTForm.setClaimReviewer2(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath,
									claimAppReviewer.getEmployee()));
							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
						} else if (lundinWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder claimReviewer2 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_APPROVED,
											pageContextPath,
											claimAppReviewer.getEmployee()));

							claimReviewer2
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ lundinWorkflow.getCreatedDate()
											+ "</span>");

							addOTForm.setClaimReviewer2(String
									.valueOf(claimReviewer2));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

						}

					}

				}
				if (revCount == 3) {
					TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());
					if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

						addOTForm
								.setClaimReviewer3(getStatusImage("NA",
										pageContextPath,
										claimAppReviewer.getEmployee()));

					} else if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (lundinWorkflow == null) {
							addOTForm.setClaimReviewer3(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath,
									claimAppReviewer.getEmployee()));
						} else if (lundinWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder claimReviewer3 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_APPROVED,
											pageContextPath,
											claimAppReviewer.getEmployee()));

							claimReviewer3
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ lundinWorkflow.getCreatedDate()
											+ "</span>");

							addOTForm.setClaimReviewer3(String
									.valueOf(claimReviewer3));

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

	private String getStatusImage(String status, String contextPath,
			Employee employee) {
		String imagePath = contextPath + "/resources/images/icon/16/";
		if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {
			imagePath = imagePath + "pending.png";
		} else if (status
				.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
			imagePath = imagePath + "approved.png";
		} else if ("NA".equalsIgnoreCase(status)) {
			imagePath = imagePath + "pending-next-level.png";
		} else if (status
				.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
			imagePath = imagePath + "rejected.png";
		}
		String employeeName = getEmployeeName(employee);

		return "<img src='" + imagePath + "'  />  " + employeeName;

	}

	@Override
	public AddClaimFormResponse getApprovedTimesheet(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setClaimGroup("%" + searchText.trim() + "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {

				conditionDTO.setClaimNumber(Long.parseLong(searchText.trim()));
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

		int recordSize = (lundinTimesheetDAO.getCountForCondition(conditionDTO))
				.intValue();

		List<EmployeeTimesheetApplication> pendingClaims = lundinTimesheetDAO
				.findByConditionLion(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication claimApplication : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch()
					.getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"approved\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewLundinApplication("
							+ claimApplication.getTimesheetId()
							+ claimStatusMode + ");'>[View]</a></span>");

			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addOTForm.setCreateDate(DateUtils
					.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication.getTimesheetId());

			List<TimesheetApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getTimesheetApplicationReviewers());

			Collections
					.sort(claimApplicationReviewers, new ClaimReviewerComp());
			int revCount = 1;
			for (TimesheetApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());
					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									claimAppReviewer.getEmployee()));

					ClaimReviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate()
									+ "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

				}
				if (revCount == 2) {

					TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder ClaimReviewer2 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									claimAppReviewer.getEmployee()));

					if (applicationWorkflow.getCreatedDate() != null) {
						ClaimReviewer2
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ applicationWorkflow.getCreatedDate()
										+ "</span>");
					}

					addOTForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

				}
				if (revCount == 3) {

					TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder ClaimReviewer3 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									claimAppReviewer.getEmployee()));

					ClaimReviewer3
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate()
									+ "</span>");

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
	public AddClaimFormResponse getRejectedTimesheet(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setClaimGroup("%" + searchText.trim() + "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {

				conditionDTO.setClaimNumber(Long.parseLong(searchText.trim()));
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

		int recordSize = (lundinTimesheetDAO.getCountForCondition(conditionDTO))
				.intValue();

		List<EmployeeTimesheetApplication> pendingClaims = lundinTimesheetDAO
				.findByCondition(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication claimApplication : pendingClaims) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch()
					.getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"rejected\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewLundinApplication("
							+ claimApplication.getTimesheetId()
							+ claimStatusMode + ");'>[View]</a></span>");
			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addOTForm.setCreateDate(DateUtils
					.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication.getTimesheetId());

			List<TimesheetApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getTimesheetApplicationReviewers());

			Collections
					.sort(claimApplicationReviewers, new ClaimReviewerComp());

			Boolean reviewStatus = false;
			int revCount = 1;
			for (TimesheetApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (reviewStatus) {
					continue;
				}

				if (revCount == 1) {

					TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());

					if (applicationWorkflow == null) {

						addOTForm
								.setClaimReviewer1(getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_PENDING,
										pageContextPath,
										claimAppReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer1 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										claimAppReviewer.getEmployee()));

						ClaimReviewer1
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ applicationWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer1(String
								.valueOf(ClaimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
						reviewStatus = true;
						StringBuilder ClaimReviewer1 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_REJECTED,
										pageContextPath,
										claimAppReviewer.getEmployee()));

						ClaimReviewer1
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ applicationWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer1(String
								.valueOf(ClaimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

					}
				}
				if (revCount == 2) {

					TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());

					if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
							|| reviewer1Status
									.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm
								.setClaimReviewer2(getStatusImage("NA",
										pageContextPath,
										claimAppReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							addOTForm.setClaimReviewer2(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath,
									claimAppReviewer.getEmployee()));
							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
						} else if (applicationWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder ClaimReviewer2 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_APPROVED,
											pageContextPath,
											claimAppReviewer.getEmployee()));

							ClaimReviewer2
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ applicationWorkflow
													.getCreatedDate()
											+ "</span>");

							addOTForm.setClaimReviewer2(String
									.valueOf(ClaimReviewer2));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

						} else if (applicationWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
							reviewStatus = true;
							addOTForm.setClaimReviewer2(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_REJECTED,
									pageContextPath,
									claimAppReviewer.getEmployee()));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

						}

					}

				}
				if (revCount == 3) {
					TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
							.findByCondition(claimApplication.getTimesheetId(),
									claimAppReviewer.getEmployee()
											.getEmployeeId());
					if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
							|| reviewer2Status
									.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm
								.setClaimReviewer3(getStatusImage("NA",
										pageContextPath,
										claimAppReviewer.getEmployee()));

					} else if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							addOTForm.setClaimReviewer3(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath,
									claimAppReviewer.getEmployee()));
						} else if (applicationWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder ClaimReviewer3 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_APPROVED,
											pageContextPath,
											claimAppReviewer.getEmployee()));

							ClaimReviewer3
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ applicationWorkflow
													.getCreatedDate()
											+ "</span>");

							addOTForm.setClaimReviewer3(String
									.valueOf(ClaimReviewer3));

						} else if (applicationWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

							StringBuilder ClaimReviewer3 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_REJECTED,
											pageContextPath,
											claimAppReviewer.getEmployee()));

							ClaimReviewer3
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ applicationWorkflow
													.getCreatedDate()
											+ "</span>");

							addOTForm.setClaimReviewer3(String
									.valueOf(ClaimReviewer3));

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
	public AddClaimFormResponse getWithdrawnTimesheet(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setClaimGroup("%" + searchText.trim() + "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {

				conditionDTO.setClaimNumber(Long.parseLong(searchText.trim()));
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

		int recordSize = (lundinTimesheetDAO.getCountForCondition(conditionDTO))
				.intValue();

		List<EmployeeTimesheetApplication> pendingClaims = lundinTimesheetDAO
				.findByCondition(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication claimApplication : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch()
					.getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"withdrawn\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewLundinApplication("
							+ claimApplication.getTimesheetId()
							+ claimStatusMode + ");'>[View]</a></span>");
			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addOTForm.setCreateDate(DateUtils
					.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication.getTimesheetId());

			List<TimesheetApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getTimesheetApplicationReviewers());

			Collections
					.sort(claimApplicationReviewers, new ClaimReviewerComp());
			int revCount = 1;
			for (TimesheetApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					addOTForm
							.setClaimReviewer1(getEmployeeName(claimAppReviewer
									.getEmployee()));

				}
				if (revCount == 2) {

					addOTForm
							.setClaimReviewer2(getEmployeeName(claimAppReviewer
									.getEmployee()));

				}
				if (revCount == 3) {

					addOTForm
							.setClaimReviewer3(getEmployeeName(claimAppReviewer
									.getEmployee()));

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
	public String getSubmittedOnDate(Long claimApplicationId) {
		String claimAppSubmittedDate = "";
		// ClaimApplication claimApplication = lundinTimesheetDAO
		// .findByID(claimApplicationId);
		// LundinTimesheetWorkflow empClaimApplicationWorkflow =
		// lundinTimesheetWorkflowDAO
		// .findByCondition(claimApplication.getClaimApplicationId(),
		// claimApplication.getEmployee().getEmployeeId());
		// claimAppSubmittedDate = empClaimApplicationWorkflow == null ? ""
		// : DateUtils.timeStampToString(empClaimApplicationWorkflow
		// .getCreatedDate());
		return claimAppSubmittedDate;
	}

	private class EmployeeOTReviewerComp implements
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

	/**
	 * Comparator Class for Ordering lundinWorkflow List
	 */
	private class IngersollOTTimesheetReviewerComp implements
			Comparator<TimesheetApplicationReviewer> {
		public int compare(TimesheetApplicationReviewer templateField,
				TimesheetApplicationReviewer compWithTemplateField) {
			if (templateField.getTimesheetReviewerId() > compWithTemplateField
					.getTimesheetReviewerId()) {
				return 1;
			} else if (templateField.getTimesheetReviewerId() < compWithTemplateField
					.getTimesheetReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	public void getPendingClaimReviewers(
			List<EmployeeTimesheetReviewer> empOtReviewers,
			AddClaimForm addOTForm) {
		Collections.sort(empOtReviewers, new EmployeeOTReviewerComp());
		for (EmployeeTimesheetReviewer employeeOtReviewer : empOtReviewers) {
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {

				addOTForm.setClaimReviewer1(getEmployeeName(employeeOtReviewer
						.getEmployeeReviewer()));

			}
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {

				addOTForm.setClaimReviewer2(getEmployeeName(employeeOtReviewer
						.getEmployeeReviewer()));

			}
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {

				addOTForm.setClaimReviewer3(getEmployeeName(employeeOtReviewer
						.getEmployeeReviewer()));

			}

		}
	}

	public void getApprovedClaimReviewers(
			List<TimesheetApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath,
			EmployeeTimesheetApplication lundinTimesheet) {
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (TimesheetApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());
				StringBuilder ClaimReviewer1 = new StringBuilder(
						getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
								pageContextPath,
								otApplicationReviewer.getEmployee()));

				ClaimReviewer1
						.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate()
								+ "</span>");

				addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

			}
			if (revCount == 2) {

				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());
				if (applicationWorkflow == null) {
					continue;
				}

				StringBuilder ClaimReviewer2 = new StringBuilder(
						getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
								pageContextPath,
								otApplicationReviewer.getEmployee()));

				if (applicationWorkflow.getCreatedDate() != null) {
					ClaimReviewer2
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate()
									+ "</span>");
				}

				addOTForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

			}
			if (revCount == 3) {

				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());
				if (applicationWorkflow == null) {
					continue;
				}

				StringBuilder ClaimReviewer3 = new StringBuilder(
						getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
								pageContextPath,
								otApplicationReviewer.getEmployee()));

				ClaimReviewer3
						.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate()
								+ "</span>");

				addOTForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

			}
			revCount++;
		}
	}

	public void getRejectedClaimReviewers(
			List<TimesheetApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath,
			EmployeeTimesheetApplication lundinTimesheet) {

		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (TimesheetApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());

				if (applicationWorkflow == null) {

					addOTForm.setClaimReviewer1(getStatusImage(
							PayAsiaConstants.CLAIM_STATUS_PENDING,
							pageContextPath,
							otApplicationReviewer.getEmployee()));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (applicationWorkflow
						.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equalsIgnoreCase(
								PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									otApplicationReviewer.getEmployee()));

					ClaimReviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate()
									+ "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				} else if (applicationWorkflow
						.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equalsIgnoreCase(
								PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_REJECTED,
									pageContextPath,
									otApplicationReviewer.getEmployee()));

					ClaimReviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate()
									+ "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

				}
			}
			if (revCount == 2) {

				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());

				if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer1Status
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addOTForm.setClaimReviewer2(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployee()));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {

						addOTForm.setClaimReviewer2(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployee()));
						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer2 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer.getEmployee()));

						ClaimReviewer2
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ applicationWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer2(String
								.valueOf(ClaimReviewer2));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm.setClaimReviewer2(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_REJECTED,
								pageContextPath,
								otApplicationReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

					}

				}

			}
			if (revCount == 3) {
				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());
				if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer2Status
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addOTForm.setClaimReviewer3(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployee()));

				} else if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {
						addOTForm.setClaimReviewer3(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployee()));
					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer.getEmployee()));

						ClaimReviewer3
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ applicationWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer3(String
								.valueOf(ClaimReviewer3));

					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_REJECTED,
										pageContextPath,
										otApplicationReviewer.getEmployee()));

						ClaimReviewer3
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ applicationWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer3(String
								.valueOf(ClaimReviewer3));

					}

				}

			}
			revCount++;
		}
	}

	public void getSubmittedClaimReviewers(
			List<TimesheetApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath,
			EmployeeTimesheetApplication lundinTimesheet) {
		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (TimesheetApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());

				if (lundinWorkflow == null) {

					addOTForm.setClaimReviewer1(getStatusImage(
							PayAsiaConstants.CLAIM_STATUS_PENDING,
							pageContextPath,
							otApplicationReviewer.getEmployee()));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (lundinWorkflow
						.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equalsIgnoreCase(
								PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					StringBuilder claimReviewer1 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									otApplicationReviewer.getEmployee()));

					claimReviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ lundinWorkflow.getCreatedDate()
									+ "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(claimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				}
			}
			if (revCount == 2) {

				TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());

				if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addOTForm.setClaimReviewer2(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployee()));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (lundinWorkflow == null) {

						addOTForm.setClaimReviewer2(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployee()));
						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (lundinWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer2 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer.getEmployee()));

						claimReviewer2
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ lundinWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer2(String
								.valueOf(claimReviewer2));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					}

				}

			}
			if (revCount == 3) {
				TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());
				if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addOTForm.setClaimReviewer3(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployee()));

				} else if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (lundinWorkflow == null) {
						addOTForm.setClaimReviewer3(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployee()));
					} else if (lundinWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer3 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer.getEmployee()));

						claimReviewer3
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ lundinWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer3(String
								.valueOf(claimReviewer3));

					}

				}

			}
			revCount++;
		}
	}

	public void getWithdrawClaimReviewers(
			List<TimesheetApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOtForm) {
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (TimesheetApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				addOtForm
						.setClaimReviewer1(getEmployeeName(otApplicationReviewer
								.getEmployee()));

			}
			if (revCount == 2) {

				addOtForm
						.setClaimReviewer2(getEmployeeName(otApplicationReviewer
								.getEmployee()));

			}
			if (revCount == 3) {

				addOtForm
						.setClaimReviewer3(getEmployeeName(otApplicationReviewer
								.getEmployee()));

			}
			revCount++;
		}
	}

	@Override
	public LundinMyRequestForm getotTimesheetReviewWorkflow(Long otTimesheetId,
			Long empId) {
		LundinMyRequestForm otMyRequestForm = new LundinMyRequestForm();

		EmployeeTimesheetApplication lundinTimesheet = lundinTimesheetDAO
				.findById(otTimesheetId);

		otMyRequestForm.setCreatedBy(getEmployeeNameWithNumber(lundinTimesheet
				.getEmployee()));

		otMyRequestForm
				.setCreatedDate(DateUtils
						.timeStampToStringWithTime(new ArrayList<TimesheetApplicationReviewer>(
								lundinTimesheet
										.getTimesheetApplicationReviewers())
								.get(0).getCreatedDate()));
		List<TimesheetApplicationWorkflow> applicationWorkflows = new ArrayList<>(
				lundinTimesheet.getTimesheetApplicationWorkflows());
		Collections.sort(applicationWorkflows, new OTTimesheetWorkFlowComp());
		Integer workFlowCount = 0;

		if (lundinTimesheet.getTimesheetStatusMaster().getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_WITHDRAWN)) {
			otMyRequestForm.setUserStatus(PayAsiaConstants.OT_STATUS_WITHDRAWN);
		} else {
			otMyRequestForm.setUserStatus(PayAsiaConstants.OT_STATUS_SUBMITTED);

		}

		for (TimesheetApplicationReviewer lundinTimesheetReviewer : lundinTimesheet
				.getTimesheetApplicationReviewers()) {

			if (lundinTimesheetReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {
				otMyRequestForm
						.setOtTimesheetReviewer1(getEmployeeNameWithNumber(lundinTimesheetReviewer
								.getEmployee()));
				otMyRequestForm
						.setOtTimesheetReviewer1Id(lundinTimesheetReviewer
								.getEmployee().getEmployeeId());
			}

			if (lundinTimesheetReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {
				otMyRequestForm
						.setOtTimesheetReviewer2(getEmployeeNameWithNumber(lundinTimesheetReviewer
								.getEmployee()));
				otMyRequestForm
						.setOtTimesheetReviewer2Id(lundinTimesheetReviewer
								.getEmployee().getEmployeeId());
			}
			if (lundinTimesheetReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				otMyRequestForm
						.setOtTimesheetReviewer3(getEmployeeNameWithNumber(lundinTimesheetReviewer
								.getEmployee()));
				otMyRequestForm
						.setOtTimesheetReviewer3Id(lundinTimesheetReviewer
								.getEmployee().getEmployeeId());
			}
		}

		otMyRequestForm.setTotalNoOfReviewers(lundinTimesheet
				.getTimesheetApplicationReviewers().size());
		List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms = new ArrayList<>();
		for (TimesheetApplicationWorkflow lundinWorkflow : applicationWorkflows) {

			OTTimesheetWorkflowForm otTimesheetWorkflowForm = new OTTimesheetWorkflowForm();
			otTimesheetWorkflowForm.setRemarks(lundinWorkflow.getRemarks());
			otTimesheetWorkflowForm.setStatus(lundinWorkflow
					.getTimesheetStatusMaster().getTimesheetStatusName());
			otTimesheetWorkflowForm
					.setCreatedDate(DateUtils
							.timeStampToStringWithTime(lundinWorkflow
									.getCreatedDate()));
			otTimesheetWorkflowForm.setOrder(workFlowCount);

			otTimesheetWorkflowForms.add(otTimesheetWorkflowForm);
			workFlowCount++;
		}
		otMyRequestForm.setOtTimesheetWorkflowForms(otTimesheetWorkflowForms);
		return otMyRequestForm;
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

	private class OTTimesheetWorkFlowComp implements
			Comparator<TimesheetApplicationWorkflow> {

		@Override
		public int compare(TimesheetApplicationWorkflow templateField,
				TimesheetApplicationWorkflow compWithTemplateField) {
			if (templateField.getTimesheetWorkflowId() > compWithTemplateField
					.getTimesheetWorkflowId()) {
				return 1;
			} else if (templateField.getTimesheetWorkflowId() < compWithTemplateField
					.getTimesheetWorkflowId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public void saveTimeSheet(LundinTimesheetDTO timesheetDto) {
		EmployeeTimesheetApplication timesheet = new EmployeeTimesheetApplication();
		timesheet.setCompany(companyDAO.findById(timesheetDto.getCompanyId()));
		timesheet.setEmployee(empDAO.findById(timesheetDto.getEmployeeId()));
		timesheet.setTimesheetBatch(lundinTimesheetBatchDAO
				.findById(timesheetDto.getLundinBatchId()));
		timesheet.setTimesheetStatusMaster(lundinStatusMasterDAO
				.findByName(PayAsiaConstants.OT_STATUS_DRAFT));
		lundinTimesheetDAO.save(timesheet);
	}

	@Override
	public long saveTimeSheetAndReturn(LundinTimesheetDTO timesheetDto) {
		EmployeeTimesheetApplication timesheet = new EmployeeTimesheetApplication();
		timesheet.setCompany(companyDAO.findById(timesheetDto.getCompanyId()));
		timesheet.setEmployee(empDAO.findById(timesheetDto.getEmployeeId()));
		timesheet.setTimesheetBatch(lundinTimesheetBatchDAO
				.findById(timesheetDto.getLundinBatchId()));
		timesheet.setTimesheetStatusMaster(lundinStatusMasterDAO
				.findByName(PayAsiaConstants.OT_STATUS_DRAFT));
		return lundinTimesheetDAO.saveAndReturn(timesheet);
	}

	@Override
	public long saveTimeSheetAndReturnId(LundinOTBatchDTO otbatchDto) {
		TimesheetBatch otbatch = new TimesheetBatch();
		otbatch.setCompany(companyDAO.findById(otbatchDto.getCompanyId()));
		otbatch.setStartDate(otbatchDto.getStartDate());
		otbatch.setEndDate(otbatchDto.getEndDate());
		otbatch.setTimesheetBatchDesc(otbatchDto.getOtBatchDesc());

		return lundinTimesheetBatchDAO.saveOTBatchAndReturnId(otbatch);
	}

	@Override
	public void saveTimeSheetDetail(LundinTimesheetSaveDTO timesheetDetailDto,
			long companyId) {
		long tsheetId = timesheetDetailDto.getTimesheetId();

		List<LundinTimesheetDetailSaveDTO> lundinTimesheetDetailDTOs = timesheetDetailDto
				.getTimesheetDetails();
		try {
			for (LundinTimesheetDetailSaveDTO lundinTimesheetDetailDTO : lundinTimesheetDetailDTOs) {
				List<LundinTimesheetSaveDataDTO> lundinTimesheetSaveDataDTOs = lundinTimesheetDetailDTO
						.getSaveData();
				for (LundinTimesheetSaveDataDTO lsdata : lundinTimesheetSaveDataDTOs) {

					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MMM-yyyy");
					Date date = dateFormat.parse(lsdata.getDt());
					Timestamp tdate = new Timestamp(date.getTime());
					LundinTimesheetDetail timesheetDetail = lundinTimesheetDetailDAO
							.findIfExists(tsheetId, tdate,
									lundinTimesheetDetailDTO.getAfeId(),
									lundinTimesheetDetailDTO.getBlockId());
					if (lsdata.getVal().equals("1")) {
						lsdata.setVal("1.0");
					}
					if (timesheetDetail != null) {
						try {
							if (StringEscapeUtils.escapeHtml(lsdata.getVal())
									.equals("&nbsp;")) {
								lundinTimesheetDetailDAO.delete(timesheetDetail
										.getTimesheetDetailID());
							} else {
								timesheetDetail
										.setValue(appCodeMasterDAO
												.findByCondition(
														PayAsiaConstants.Lundin_Timesheet_Values,
														"" + lsdata.getVal()));
								timesheetDetail
										.setEmployeeTimesheetApplication(lundinTimesheetDAO
												.findById(tsheetId));
								timesheetDetail.setCompanyId(companyId);
								lundinTimesheetDetailDAO
										.update(timesheetDetail);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						if (!StringEscapeUtils.escapeHtml(lsdata.getVal())
								.equals("&nbsp;")) {
							timesheetDetail = new LundinTimesheetDetail();
							try {
								timesheetDetail.setLundinAFE(lundinAFEDAO
										.findById(lundinTimesheetDetailDTO
												.getAfeId()));
								timesheetDetail.setLundinBlock(lundinBlockDAO
										.findById(lundinTimesheetDetailDTO
												.getBlockId()));
								timesheetDetail.setTimesheetDate(tdate);
								timesheetDetail
										.setValue(appCodeMasterDAO
												.findByCondition(
														PayAsiaConstants.Lundin_Timesheet_Values,
														"" + lsdata.getVal()));
								timesheetDetail
										.setEmployeeTimesheetApplication(lundinTimesheetDAO
												.findById(tsheetId));
								timesheetDetail.setTimesheetDetailID(0);
								lundinTimesheetDetailDAO.save(timesheetDetail);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

			}

			// if all set, update the timesheet
			EmployeeTimesheetApplication timesheet = lundinTimesheetDAO
					.findById(tsheetId);
			timesheet.setRemarks(timesheetDetailDto.getRemarks());
			lundinTimesheetDAO.update(timesheet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void submitToWorkFlow(
			List<LundinEmployeeTimesheetReviewerDTO> lundinEmployeeOTReviewerDTOs,
			long timesheetId, long companyId, long employeeId) {
		EmployeeTimesheetApplication lundinOTTimesheet = lundinTimesheetDAO
				.findById(timesheetId);
		boolean isSuccessfullyFor = false;
		Employee firstEmployee = null;
		boolean firstEntry = true;
		for (LundinEmployeeTimesheetReviewerDTO lundinEmployeeOTReviewerDTO : lundinEmployeeOTReviewerDTOs) {

			try {
				TimesheetApplicationReviewer lundinOTTimesheetReviewer = new TimesheetApplicationReviewer();

				lundinOTTimesheetReviewer
						.setEmployeeTimesheetApplication(lundinOTTimesheet);
				lundinOTTimesheetReviewer
						.setWorkFlowRuleMaster(workflowRuleMasterDAO
								.findByID(lundinEmployeeOTReviewerDTO
										.getWorkFlowRuleMasterId()));
				if (firstEntry) {
					// firstEmployee = empDAO
					// .findById(lundinEmployeeOTReviewerDTO
					// .getEmployeeReviewerId());
					firstEmployee = getDelegatedEmployee(lundinEmployeeOTReviewerDTO
							.getEmployeeReviewerId());
					lundinOTTimesheetReviewer.setEmployee(firstEmployee);

					lundinOTTimesheetReviewer.setPending(true);
					firstEntry = false;
				} else {
					lundinOTTimesheetReviewer
							.setEmployee(getDelegatedEmployee(lundinEmployeeOTReviewerDTO
									.getEmployeeReviewerId()));
					lundinOTTimesheetReviewer.setPending(false);
				}

				lundinTimesheetReviewerDAO.save(lundinOTTimesheetReviewer);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		TimesheetStatusMaster otStatusMaster = lundinStatusMasterDAO
				.findByName(PayAsiaConstants.OT_STATUS_SUBMITTED);
		lundinOTTimesheet.setTimesheetStatusMaster(otStatusMaster);
		lundinTimesheetDAO.update(lundinOTTimesheet);
		TimesheetApplicationWorkflow lundinOtTimesheetWorkflow = new TimesheetApplicationWorkflow();
		lundinOtTimesheetWorkflow.setCompanyId(companyId);
		// lundinOtTimesheetWorkflow.setEmailCC(firstEmployee.getEmail());

		lundinOtTimesheetWorkflow.setForwardTo(firstEmployee.getEmail());

		lundinOtTimesheetWorkflow.setRemarks("");
		lundinOtTimesheetWorkflow.setTimesheetStatusMaster(otStatusMaster);
		lundinOtTimesheetWorkflow
				.setEmployeeTimesheetApplication(lundinOTTimesheet);
		Employee loggedInEmp = empDAO.findById(employeeId);
		lundinOtTimesheetWorkflow.setCreatedBy(loggedInEmp);
		lundinOtTimesheetWorkflow.setCreatedDate(new Timestamp(new Date()
				.getTime()));
		TimesheetApplicationWorkflow requestWorkflow = lundinTimesheetWorkflowDAO
				.saveReturn(lundinOtTimesheetWorkflow);
		isSuccessfullyFor = true;
		if (isSuccessfullyFor) {
			lundinTimesheetMailLogic
					.sendEMailForTimesheet(
							companyId,
							lundinOTTimesheet,
							requestWorkflow,
							PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPLY,
							loggedInEmp, firstEmployee);

		}
	}

	@Override
	public LundinTimesheetSaveDTO getTimesheetResponseForm(long id) {
		LundinTimesheetSaveDTO toReturn = new LundinTimesheetSaveDTO();

		EmployeeTimesheetApplication tsheet = lundinTimesheetDAO.findById(id);
		if (tsheet != null) {
			toReturn.setRemarks(tsheet.getRemarks());
			toReturn.setTimesheetStatus(tsheet.getTimesheetStatusMaster()
					.getTimesheetStatusName());
			toReturn.setTimesheetId(id);

			List<LundinTimesheetDetail> lundinTsheetDetails = lundinTimesheetDetailDAO
					.findByTimesheetId(id);

			if (lundinTsheetDetails.isEmpty()) {
				toReturn.setEmptyTimesheetData(true);
			}

			List<LundinTimesheetDetailSaveDTO> saveDtos = new ArrayList<LundinTimesheetDetailSaveDTO>();
			List<Long> alreadyAttached = new ArrayList<Long>();
			for (int i = 0; i < lundinTsheetDetails.size(); i++) {
				LundinTimesheetDetail lundinTimesheetDetail = lundinTsheetDetails
						.get(i);
				Long afeId = lundinTimesheetDetail.getLundinAFE().getAfeId();
				Long blockId = lundinTimesheetDetail.getLundinBlock()
						.getBlockId();
				if (alreadyAttached.contains(lundinTimesheetDetail
						.getTimesheetDetailID())) {
					continue;
				}
				LundinTimesheetDetailSaveDTO timesheetDto = new LundinTimesheetDetailSaveDTO();

				timesheetDto.setAfeId(afeId);
				timesheetDto.setBlockId(blockId);

				List<LundinTimesheetSaveDataDTO> toAttach = new ArrayList<LundinTimesheetSaveDataDTO>();
				List<LundinTimesheetDetail> internalAttachments = lundinTimesheetDetailDAO
						.findByBlockAndAfe(blockId, afeId, id);
				for (LundinTimesheetDetail temp : internalAttachments) {

					LundinTimesheetSaveDataDTO dto = new LundinTimesheetSaveDataDTO();

					DateTime dt = new DateTime(temp.getTimesheetDate());
					DateTimeFormatter dtfOut = DateTimeFormat
							.forPattern("MM/dd/yyyy");

					dto.setDt(dtfOut.print(dt));
					dto.setVal(temp.getValue().getCodeValue());
					dto.setTimesheetDetailId(temp.getTimesheetDetailID());
					toAttach.add(dto);

					alreadyAttached.add(temp.getTimesheetDetailID());

				}

				timesheetDto.setSaveData(toAttach);
				saveDtos.add(timesheetDto);
			}
			toReturn.setTimesheetDetails(saveDtos);

			return toReturn;
		} else
			return null;
	}

	private Employee getDelegatedEmployee(Long employeeId) {
		Employee emp = empDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterOT = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_TYPE_LUNDIN_TIMESHEET_DESC);

		WorkflowDelegate workflowDelegateLeave = workflowDelegateDAO
				.findEmployeeByCurrentDate(employeeId,
						appCodeMasterOT.getAppCodeID());

		if (workflowDelegateLeave != null) {
			workflowDelegate = workflowDelegateLeave;
		} else {

			AppCodeMaster appCodeMasterALL = appCodeMasterDAO
					.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
							PayAsiaConstants.WORKFLOW_CATEGORY_ALL);

			WorkflowDelegate workflowDelegateALL = workflowDelegateDAO
					.findEmployeeByCurrentDate(employeeId,
							appCodeMasterALL.getAppCodeID());

			workflowDelegate = workflowDelegateALL;
		}

		if (workflowDelegate != null) {
			return workflowDelegate.getEmployee2();
		}
		return emp;
	}

	@Override
	public List<LundinEmployeeTimesheetReviewerDTO> getReviewersFor(
			long employeeId, long companyId) {
		List<LundinEmployeeTimesheetReviewerDTO> toReturn = new ArrayList<LundinEmployeeTimesheetReviewerDTO>();
		List<EmployeeTimesheetReviewer> beanList = lundinEmployeeReviewerDAO
				.findByCondition(employeeId, companyId);
		for (EmployeeTimesheetReviewer bean : beanList) {
			LundinEmployeeTimesheetReviewerDTO dto = new LundinEmployeeTimesheetReviewerDTO();
			Employee emp = bean.getEmployee();
			Employee empRev = bean.getEmployeeReviewer();
			dto.setEmployeeId(emp.getEmployeeId());
			dto.setEmployeeReviewerId(getDelegatedEmployee(
					empRev.getEmployeeId()).getEmployeeId());
			dto.setEmployeeReviewerId(bean.getEmployeeTimesheetReviewerId());
			dto.setWorkFlowRuleMasterId(bean.getWorkFlowRuleMaster()
					.getWorkFlowRuleId());
			StringBuilder empFullName = new StringBuilder(emp.getFirstName());

			if (emp.getMiddleName() != null) {
				empFullName.append(" ");
				empFullName.append(emp.getMiddleName());
			}
			if (emp.getLastName() != null) {
				empFullName.append(" ");
				empFullName.append(emp.getLastName());
			}
			dto.setEmpName(empFullName.toString());

			StringBuilder empRevFullName = new StringBuilder(
					empRev.getFirstName());

			if (empRev.getMiddleName() != null) {
				empRevFullName.append(" ");
				empRevFullName.append(empRev.getMiddleName());
			}
			if (empRev.getLastName() != null) {
				empRevFullName.append(" ");
				empRevFullName.append(empRev.getLastName());
			}
			empRevFullName.append(" (" + empRev.getEmployeeNumber() + ") ");

			dto.setEmpReviewerName(empRevFullName.toString());

			dto.setEmpReviewerEmail(empRev.getEmail());
			toReturn.add(dto);
		}
		return toReturn;
	}

	@Override
	public LundinTimesheetDTO findById(long id) {
		EmployeeTimesheetApplication otTimesheet = lundinTimesheetDAO
				.findById(id);
		LundinTimesheetDTO toReturn = new LundinTimesheetDTO();
		toReturn.setCompanyId(otTimesheet.getCompany().getCompanyId());
		toReturn.setEmployeeId(otTimesheet.getEmployee().getEmployeeId());
		toReturn.setLundinBatchId(otTimesheet.getTimesheetBatch()
				.getTimesheetBatchId());
		toReturn.setTimesheetId(otTimesheet.getTimesheetId());
		return toReturn;

	}

	@Override
	public LundinOTBatchDTO getOTBatch(long timesheetId) {
		TimesheetBatch lundinOTBatch = lundinTimesheetDAO.findById(timesheetId)
				.getTimesheetBatch();
		LundinOTBatchDTO toReturn = new LundinOTBatchDTO();
		toReturn.setCompanyId(lundinOTBatch.getCompany().getCompanyId());
		toReturn.setEndDate(lundinOTBatch.getEndDate());
		toReturn.setOtBatchDesc(lundinOTBatch.getTimesheetBatchDesc());
		toReturn.setOtBatchId(lundinOTBatch.getTimesheetBatchId());
		toReturn.setStartDate(lundinOTBatch.getStartDate());
		return toReturn;
	}

	private class LundinTimesheetReviewerComp implements
			Comparator<TimesheetApplicationReviewer> {
		public int compare(TimesheetApplicationReviewer templateField,
				TimesheetApplicationReviewer compWithTemplateField) {
			if (templateField.getTimesheetReviewerId() > compWithTemplateField
					.getTimesheetReviewerId()) {
				return 1;
			} else if (templateField.getTimesheetReviewerId() < compWithTemplateField
					.getTimesheetReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public boolean withdrawTimesheetRequest(long timesheetId, long employeeId,
			Long companyId) {
		if (getCanWithdraw(timesheetId)) {
			EmployeeTimesheetApplication otTimesheet = lundinTimesheetDAO
					.findById(timesheetId);
			try {
				TimesheetStatusMaster otStatusMaster = lundinStatusMasterDAO
						.findByName(PayAsiaConstants.OT_STATUS_DRAFT);
				otTimesheet.setTimesheetStatusMaster(otStatusMaster);
				lundinTimesheetDAO.update(otTimesheet);

				for (TimesheetApplicationReviewer otTsheetReviewer : otTimesheet
						.getTimesheetApplicationReviewers()) {
					lundinTimesheetReviewerDAO.delete(otTsheetReviewer);
				}
				TimesheetApplicationWorkflow deletedWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(otTimesheet.getTimesheetId(),
								otTimesheet.getEmployee().getEmployeeId());

				for (TimesheetApplicationWorkflow workflow : otTimesheet
						.getTimesheetApplicationWorkflows()) {
					lundinTimesheetWorkflowDAO.delete(workflow);
				}
				lundinTimesheetMailLogic
						.sendWithdrawEmailForTimesheet(
								companyId,
								otTimesheet,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_WITHDRAW,
								otTimesheet.getEmployee(), deletedWorkflow);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}

	}

	@Override
	public boolean getCanWithdraw(long timesheetId) {
		EmployeeTimesheetApplication lundinOTTimesheet = lundinTimesheetDAO
				.findById(timesheetId);
		Set<TimesheetApplicationReviewer> lundinOtTsheetRevieweres = lundinOTTimesheet
				.getTimesheetApplicationReviewers();

		List<TimesheetApplicationReviewer> reviewersList = new ArrayList<TimesheetApplicationReviewer>(
				lundinOtTsheetRevieweres);

		if (reviewersList != null && !reviewersList.isEmpty()) {
			Collections.sort(reviewersList,
					new IngersollOTTimesheetReviewerComp());

			if (reviewersList.get(0).isPending())
				return true;

		}
		return false;
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
	public List<AppCodeDTO> getValueAndCodes() {
		List<AppCodeDTO> codes = new ArrayList<AppCodeDTO>();
		List<AppCodeMaster> appCodes = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.Lundin_Timesheet_Values);
		for (AppCodeMaster appCodeMaster : appCodes) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setCodeValue(appCodeMaster.getCodeValue());
			appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			codes.add(appCodeDTO);
		}

		return codes;
	}

	@Override
	public void deleteTimesheetRecords(
			List<LundinTimesheetDelDTO> lundinTimesheetDelDTOs) {
		for (LundinTimesheetDelDTO lundinTimesheetDelDTO : lundinTimesheetDelDTOs) {
			lundinTimesheetDAO.deleteByCondition(
					lundinTimesheetDelDTO.getBlockId(),
					lundinTimesheetDelDTO.getAfeId(),
					lundinTimesheetDelDTO.getTimesheetId());
		}

	}

	@Override
	public boolean saveAsDraftTimesheet(long timesheetId, long employeeId,
			Long companyId) {
		EmployeeTimesheetApplication otTimesheet = lundinTimesheetDAO
				.findById(timesheetId);
		try {
			TimesheetStatusMaster otStatusMaster = lundinStatusMasterDAO
					.findByName(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
			otTimesheet.setTimesheetStatusMaster(otStatusMaster);
			lundinTimesheetDAO.update(otTimesheet);

			for (TimesheetApplicationReviewer otTsheetReviewer : otTimesheet
					.getTimesheetApplicationReviewers()) {
				lundinTimesheetReviewerDAO.delete(otTsheetReviewer);
			}

			for (TimesheetApplicationWorkflow workflow : otTimesheet
					.getTimesheetApplicationWorkflows()) {
				lundinTimesheetWorkflowDAO.delete(workflow);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<String> getEmpResignedAndNewHiresDates(Long employeeId,
			Date startDate, Date endDate) {
		Employee empployeeVO = employeeDAO.findById(employeeId);

		DateTime startdt = new DateTime(new Timestamp(startDate.getTime()));
		DateTime endDt = new DateTime(new Timestamp(endDate.getTime()));
		DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
		List<String> toReturn = new ArrayList<String>();
		if (empployeeVO.getResignationDate() != null) {
			DateTime resignationDate = new DateTime(
					empployeeVO.getResignationDate()).plusDays(1);
			if (startdt.compareTo(resignationDate) < 0
					|| endDt.compareTo(resignationDate) > 0) {
				while (resignationDate.compareTo(endDt) <= 0) {
					toReturn.add(dtfOut.print(resignationDate));
					resignationDate = resignationDate.plusDays(1);
				}
			}
		}

		if (empployeeVO.getHireDate() != null) {
			DateTime hireDate = new DateTime(empployeeVO.getHireDate());
			if (startdt.compareTo(hireDate) < 0
					|| endDt.compareTo(hireDate) > 0) {
				while (startdt.compareTo(hireDate) < 0
						|| startdt.compareTo(hireDate) == 0) {
					toReturn.add(dtfOut.print(startdt));
					startdt = startdt.plusDays(1);
				}
			}
		}
		return toReturn;
	}

	@Override
	public LundinTimewritingReportDTO getDefaultBlockAfeDetails(Long companyId,
			Long employeeId, Timestamp bacthEndDate) {
		LundinTimesheetPreference lundinOTPreferenceVO = lundinTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		Employee employeeVO = employeeDAO.findById(employeeId);
		Company company = companyDAO.findById(companyId);

		Map<String, LundinTimewritingReportDTO> deptCodeDeptDTOMap = new LinkedHashMap<String, LundinTimewritingReportDTO>();
		// Get Lundin Departments From LundinDepartment Entity
		List<LundinDepartment> lundinDepartmentList = lundinDepartmentDAO
				.findByCondition(companyId);
		for (LundinDepartment lundinDepartmentVO : lundinDepartmentList) {
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
			}
			if (lundinDepartmentVO.getDefaultAFE() != null) {
				lundinTimesheetReportDTO.setDefaultAFEId(lundinDepartmentVO
						.getDefaultAFE().getAfeId());
				lundinTimesheetReportDTO.setDefaultAFEName(lundinDepartmentVO
						.getDefaultAFE().getAfeName());
			}

			deptCodeDeptDTOMap.put(lundinDepartmentVO
					.getDynamicFormFieldRefValue().getCode(),
					lundinTimesheetReportDTO);
		}

		LundinTimewritingReportDTO timesheetReportDTO = new LundinTimewritingReportDTO();
		// Get Department Of Employees from Employee Information Details
		List<Object[]> deptObjectList = getLundinEmployeeDepartmentList(
				lundinOTPreferenceVO, companyId, company.getDateFormat(),
				employeeId, bacthEndDate);
		for (Object[] deptObject : deptObjectList) {
			if (deptObject[3] != null
					&& deptObject[0] != null
					&& employeeVO.getEmployeeNumber().equalsIgnoreCase(
							String.valueOf(deptObject[3]))) {
				LundinTimewritingReportDTO lundinTimesheetReportDTO = deptCodeDeptDTOMap
						.get(String.valueOf(deptObject[0]));
				if (lundinTimesheetReportDTO == null) {
					continue;
				}
				lundinTimesheetReportDTO.setDepartmentName(String
						.valueOf(deptObject[1]));
				lundinTimesheetReportDTO.setDepartmentCode(String
						.valueOf(deptObject[0]));

				// Add non-eTimesheet Employee details to
				// nonTimesheetEmpDetailsList(
				if (deptObject[2] == null || deptObject[2].equals("")
						|| deptObject[2].equals(" ")) {
					timesheetReportDTO.setDailyPaidEmployee(false);
				} else {
					timesheetReportDTO.setDailyPaidEmployee(true);
				}

				timesheetReportDTO.setEmployeeNumber(String
						.valueOf(deptObject[3]));
				timesheetReportDTO.setFirstName(String.valueOf(deptObject[4]));
				timesheetReportDTO.setLastName(String.valueOf(deptObject[5]));
				timesheetReportDTO.setDepartmentName(String
						.valueOf(deptObject[1]));
				timesheetReportDTO.setDepartmentCode(String
						.valueOf(deptObject[0]));

				timesheetReportDTO.setDepartmentId(lundinTimesheetReportDTO
						.getDepartmentId());
				timesheetReportDTO.setDisplayOrder(lundinTimesheetReportDTO
						.getDisplayOrder());
				timesheetReportDTO.setDefaultBlockId(lundinTimesheetReportDTO
						.getDefaultBlockId());
				timesheetReportDTO.setDefaultBlockName(lundinTimesheetReportDTO
						.getDefaultBlockName());
				timesheetReportDTO.setDefaultBlockCode(lundinTimesheetReportDTO
						.getDefaultBlockCode());
				timesheetReportDTO.setDefaultAFEId(lundinTimesheetReportDTO
						.getDefaultAFEId());
				timesheetReportDTO.setDefaultAFEName(lundinTimesheetReportDTO
						.getDefaultAFEName());

				timesheetReportDTO.setDepartmentType(lundinTimesheetReportDTO
						.getDepartmentType());
			}
		}
		return timesheetReportDTO;
	}

	private List<Object[]> getLundinEmployeeDepartmentList(
			LundinTimesheetPreference lundinTimesheetPreferenceVO,
			Long companyId, String dateFormat, Long employeeId,
			Timestamp bacthEndDate) {
		List<Long> formIds = new ArrayList<Long>();
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

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

	private void addDynamicTableKeyMap(DataDictionary dataDictionary,
			Map<String, DataImportKeyValueDTO> tableRecordInfoFrom,
			Map<String, DataImportKeyValueDTO> colMap) {
		if (colMap.get(
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

}
