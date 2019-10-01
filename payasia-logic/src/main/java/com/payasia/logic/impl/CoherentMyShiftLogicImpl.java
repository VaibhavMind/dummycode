package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.EmailDataDTO;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.OTTimesheetWorkflowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CoherentShiftApplicationDAO;
import com.payasia.dao.CoherentShiftApplicationDetailDAO;
import com.payasia.dao.CoherentShiftApplicationReviewerDAO;
import com.payasia.dao.CoherentShiftApplicationWorkflowDAO;
import com.payasia.dao.CoherentTimesheetPreferenceDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
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
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.CoherentMyShiftLogic;
import com.payasia.logic.CoherentShiftPrintPDFLogic;
import com.payasia.logic.CoherentTimesheetMailLogic;
import com.payasia.logic.CoherentTimesheetReportsLogic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LionTimesheetReportsLogic;
import com.payasia.logic.LundinTimesheetMailLogic;

@Component
public class CoherentMyShiftLogicImpl implements CoherentMyShiftLogic {
	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetRequestLogicImpl.class);
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
	CoherentTimesheetPreferenceDAO coherentTimesheetPreferenceDAO;

	@Resource
	LionEmployeeTimesheetApplicationDetailDAO lionEmployeeTimesheetApplicationDetailDAO;

	/*
	 * @Resource CoherentOvertimeApplicationReviewerDAO
	 * coherentOvertimeApplicationReviewerDAO;
	 */

	@Override
	public AddClaimFormResponse getPendingShift(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, Long companyId) {

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

		int recordSize = (coherentShiftApplicationDAO.getCountForCondition(
				conditionDTO, companyId)).intValue();

		List<CoherentShiftApplication> pendingClaims = coherentShiftApplicationDAO
				.findByCondition(conditionDTO, pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentShiftApplication otTimesheet : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(otTimesheet.getTimesheetBatch()
					.getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editCoherentShiftApplications("
							+ FormatPreserveCryptoUtil.encrypt(otTimesheet.getShiftApplicationID())
							+ ");'>[Edit]</a></span>");
			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);

			addOTForm.setCreateDate(DateUtils.timeStampToString(otTimesheet
					.getCreatedDate()));

			addOTForm
					.setClaimApplicationId(otTimesheet.getShiftApplicationID());
			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					lundinEmpReviewerDAO.findByCondition(empId, otTimesheet
							.getCompany().getCompanyId()));

			Collections
					.sort(employeeClaimReviewers, new EmployeeReviewerComp());
			for (EmployeeTimesheetReviewer employeeOTReviewer : employeeClaimReviewers) {
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {

					addOTForm
							.setClaimReviewer1(getEmployeeNameWithNumber(employeeOTReviewer
									.getEmployeeReviewer()));

				}
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("2")) {

					addOTForm
							.setClaimReviewer2(getEmployeeNameWithNumber(employeeOTReviewer
									.getEmployeeReviewer()));

				}
				if (employeeOTReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("3")) {

					addOTForm
							.setClaimReviewer3(getEmployeeNameWithNumber(employeeOTReviewer
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
	public AddClaimFormResponse getSubmittedShift(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long companyId) {
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

		int recordSize = (coherentShiftApplicationDAO.getCountForCondition(
				conditionDTO, companyId)).intValue();

		List<CoherentShiftApplication> pendingClaims = coherentShiftApplicationDAO
				.findByCondition(conditionDTO, pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentShiftApplication claimApplication : pendingClaims) {
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
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewCoherentShiftApplications("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getShiftApplicationID())
							+ claimStatusMode + ");'>[View]</a></span>");

			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addOTForm.setCreateDate(DateUtils
					.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication
					.getShiftApplicationID());

			List<CoherentShiftApplicationReviewer> coherentApplicationReviewers = new ArrayList<>(
					claimApplication.getCoherentShiftApplicationReviewer());

			Collections.sort(coherentApplicationReviewers,
					new CoherentShiftApplicationReviewerComp());

			int revCount = 1;
			for (CoherentShiftApplicationReviewer claimAppReviewer : coherentApplicationReviewers) {
				if (revCount == 1) {

					CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(claimApplication
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());

					if (coherentShiftApplicationWorkflow == null) {

						addOTForm.setClaimReviewer1(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								claimAppReviewer.getEmployeeReviewer()));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (coherentShiftApplicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer1 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										claimAppReviewer.getEmployeeReviewer()));

						claimReviewer1
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ coherentShiftApplicationWorkflow
												.getCreatedDate() + "</span>");

						addOTForm.setClaimReviewer1(String
								.valueOf(claimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					}
				}
				if (revCount == 2) {

					CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(claimApplication
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());
					if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

						addOTForm.setClaimReviewer2(getStatusImage("NA",
								pageContextPath,
								claimAppReviewer.getEmployeeReviewer()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (coherentShiftApplicationWorkflow == null) {

							addOTForm.setClaimReviewer2(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));
							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
						} else if (coherentShiftApplicationWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder claimReviewer2 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_APPROVED,
											pageContextPath, claimAppReviewer
													.getEmployeeReviewer()));

							claimReviewer2
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ coherentShiftApplicationWorkflow
													.getCreatedDate()
											+ "</span>");

							addOTForm.setClaimReviewer2(String
									.valueOf(claimReviewer2));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

						}

					}

				}
				if (revCount == 3) {
					CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(claimApplication
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());
					if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

						addOTForm.setClaimReviewer3(getStatusImage("NA",
								pageContextPath,
								claimAppReviewer.getEmployeeReviewer()));

					} else if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (coherentShiftApplicationWorkflow == null) {
							addOTForm.setClaimReviewer3(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));
						} else if (coherentShiftApplicationWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder claimReviewer3 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_APPROVED,
											pageContextPath, claimAppReviewer
													.getEmployeeReviewer()));

							claimReviewer3
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ coherentShiftApplicationWorkflow
													.getCreatedDate()
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

	@Override
	public AddClaimFormResponse getApprovedShift(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long companyId) {
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

		int recordSize = (coherentShiftApplicationDAO.getCountForCondition(
				conditionDTO, companyId)).intValue();

		List<CoherentShiftApplication> pendingClaims = coherentShiftApplicationDAO
				.findByCondition(conditionDTO, pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentShiftApplication claimApplication : pendingClaims) {
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getTimesheetBatch()
					.getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"approved\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewCoherentShiftApplications("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getShiftApplicationID())
							+ claimStatusMode + ");'>[View]</a></span>");

			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addOTForm.setCreateDate(DateUtils
					.timeStampToString(claimApplication.getCreatedDate()));

			addOTForm.setClaimApplicationId(claimApplication
					.getShiftApplicationID());

			List<CoherentShiftApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getCoherentShiftApplicationReviewer());

			Collections.sort(claimApplicationReviewers,
					new CoherentShiftApplicationReviewerComp());
			int revCount = 1;
			for (CoherentShiftApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(claimApplication
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());
					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));

					ClaimReviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate()
									+ "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

				}
				if (revCount == 2) {

					CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(claimApplication
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder ClaimReviewer2 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));

					if (applicationWorkflow.getCreatedDate() != null) {
						ClaimReviewer2
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ applicationWorkflow.getCreatedDate()
										+ "</span>");
					}

					addOTForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

				}
				if (revCount == 3) {

					CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(claimApplication
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder ClaimReviewer3 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));

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
	public AddClaimFormResponse getRejectedShift(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long companyId) {
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

		int recordSize = (coherentShiftApplicationDAO.getCountForCondition(
				conditionDTO, companyId)).intValue();

		List<CoherentShiftApplication> pendingClaims = coherentShiftApplicationDAO
				.findByCondition(conditionDTO, pageDTO, sortDTO, companyId);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		for (CoherentShiftApplication application : pendingClaims) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddClaimForm addOTForm = new AddClaimForm();
			addOTForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(application.getTimesheetBatch()
					.getTimesheetBatchDesc());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"rejected\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewCoherentShiftApplications("
							+ FormatPreserveCryptoUtil.encrypt(application.getShiftApplicationID())
							+ claimStatusMode + ");'>[View]</a></span>");
			addOTForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addOTForm.setCreateDate(DateUtils.timeStampToString(application
					.getCreatedDate()));

			addOTForm
					.setClaimApplicationId(application.getShiftApplicationID());

			List<CoherentShiftApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					application.getCoherentShiftApplicationReviewer());

			Collections.sort(claimApplicationReviewers,
					new CoherentShiftApplicationReviewerComp());

			Boolean reviewStatus = false;
			int revCount = 1;
			for (CoherentShiftApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (reviewStatus) {
					continue;
				}

				if (revCount == 1) {

					CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(application
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());

					if (applicationWorkflow == null) {

						addOTForm.setClaimReviewer1(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								claimAppReviewer.getEmployeeReviewer()));

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
										claimAppReviewer.getEmployeeReviewer()));

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
										claimAppReviewer.getEmployeeReviewer()));

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

					CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(application
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());

					if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
							|| reviewer1Status
									.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm.setClaimReviewer2(getStatusImage("NA",
								pageContextPath,
								claimAppReviewer.getEmployeeReviewer()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							addOTForm.setClaimReviewer2(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));
							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
						} else if (applicationWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder ClaimReviewer2 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_APPROVED,
											pageContextPath, claimAppReviewer
													.getEmployeeReviewer()));

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
									claimAppReviewer.getEmployeeReviewer()));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

						}

					}

				}
				if (revCount == 3) {
					CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationDAO
							.findByCondition(application
									.getShiftApplicationID(), claimAppReviewer
									.getEmployeeReviewer().getEmployeeId());
					if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
							|| reviewer2Status
									.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm.setClaimReviewer3(getStatusImage("NA",
								pageContextPath,
								claimAppReviewer.getEmployeeReviewer()));

					} else if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							addOTForm.setClaimReviewer3(getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_PENDING,
									pageContextPath,
									claimAppReviewer.getEmployeeReviewer()));
						} else if (applicationWorkflow
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder ClaimReviewer3 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.CLAIM_STATUS_APPROVED,
											pageContextPath, claimAppReviewer
													.getEmployeeReviewer()));

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
											pageContextPath, claimAppReviewer
													.getEmployeeReviewer()));

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

	private class CoherentShiftApplicationReviewerComp implements
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
		String employeeName = getEmployeeNameWithNumber(employee);

		return "<img src='" + imagePath + "'  />  " + employeeName;

	}

	@Override
	public String getCoherentShiftJSON(long batchId, long companyId,
			long employeeId) {

		EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO
				.findByEmployeeId(employeeId);

		JSONObject jsonObject = new JSONObject();

		if (employeeTimesheetReviewer.getEmployeeReviewer() == null) {
			try {
				jsonObject.put("reviewrStatus", "false");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {

			TimesheetBatch timesheetBatch = timesheetBatchDAO.findById(batchId);

			List<CoherentShiftApplication> coherentShiftApplicationOld = coherentShiftApplicationDAO
					.findByTimesheetBatchId(batchId, companyId, employeeId);

			List<CoherentShiftApplicationDetail> coherentShiftApplicationDetails = new ArrayList<CoherentShiftApplicationDetail>();

			String companyDateFormat = companyDAO.findById(companyId)
					.getDateFormat();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					companyDateFormat);

			Long shiftId = 0l;
			CoherentShiftApplication coherentShiftApplication = null;
			if (coherentShiftApplicationOld.size() == 0) {
				shiftId = saveCherentShiftApplication(batchId, companyId,
						employeeId);
				coherentShiftApplication = coherentShiftApplicationDAO
						.findById(shiftId);

				Timestamp timesheetBatchStartDate = timesheetBatch
						.getStartDate();
				Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();
				timesheetBatchStartDate.setHours(0);
				timesheetBatchStartDate.setMinutes(0);
				Timestamp newTimestamp = timesheetBatchStartDate;
				CoherentShiftApplicationDetail coherentShiftApplicationDetail;

				int dayNum = 1;
				long oneDay = 1 * 24 * 60 * 60 * 1000;
				if (coherentShiftApplicationDetails.size() == 0) {
					do {
						newTimestamp.setHours(0);
						newTimestamp.setMinutes(0);

						coherentShiftApplicationDetail = new CoherentShiftApplicationDetail();
						coherentShiftApplicationDetail
								.setCoherentShiftApplication(coherentShiftApplication);

						coherentShiftApplicationDetail.setRemarks("");
						coherentShiftApplicationDetail.setShift(false);
						coherentShiftApplicationDetail.setShiftType(null);

						coherentShiftApplicationDetail
								.setShiftDate(newTimestamp);

						coherentShiftApplicationDetailDAO
								.save(coherentShiftApplicationDetail);

						newTimestamp = new Timestamp(
								timesheetBatchStartDate.getTime() + oneDay
										* dayNum);
						dayNum++;

					} while (newTimestamp.compareTo(timesheetBatchEndDate) <= 0);
				}
			} else {
				shiftId = coherentShiftApplicationOld.get(0)
						.getShiftApplicationID();
			}

			coherentShiftApplicationDetails = coherentShiftApplicationDAO
					.getCoherentShiftApplicationDetails(shiftId);

			JSONArray coherentShiftApplicationDetailsJson = new JSONArray();

			SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

			for (CoherentShiftApplicationDetail CoherentShiftApplicationDets : coherentShiftApplicationDetails) {
				try {
					JSONObject shiftDetailsJsonObject = new JSONObject();
					shiftDetailsJsonObject.put("date",
							CoherentShiftApplicationDets.getShiftDate());

					shiftDetailsJsonObject.put("shiftDate",
							simpleDateFormat
									.format(CoherentShiftApplicationDets
											.getShiftDate()));

					shiftDetailsJsonObject.put("shiftDay",format2.format(CoherentShiftApplicationDets
					.getShiftDate()));

					if (CoherentShiftApplicationDets.isShift()) {
						shiftDetailsJsonObject.put("shift", "Yes");
					} else {
						shiftDetailsJsonObject.put("shift", "No");
					}

					// Shift Type
					if (CoherentShiftApplicationDets.getShiftType() == null) {
						shiftDetailsJsonObject.put("shiftType", "");
					} else if (CoherentShiftApplicationDets
							.getShiftType()
							.getCodeDesc()
							.equalsIgnoreCase(
									PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND)) {
						shiftDetailsJsonObject.put("shiftType", "Second Shift");
					} else if (CoherentShiftApplicationDets
							.getShiftType()
							.getCodeDesc()
							.equalsIgnoreCase(
									PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_THIRD)) {
						shiftDetailsJsonObject.put("shiftType", "Third Shift");
					}

					shiftDetailsJsonObject.put("remarks",
							CoherentShiftApplicationDets.getRemarks());
					/*ID ENCRYPT*/
					shiftDetailsJsonObject.put("shiftApplicationDetailId",
							FormatPreserveCryptoUtil.encrypt(CoherentShiftApplicationDets
									.getShiftApplicationDetailID()));

					Date timesheetDate = new Date(CoherentShiftApplicationDets
							.getShiftDate().getTime());
					List<String> holidayList = getHolidaysFor(
							coherentShiftApplication.getEmployee()
									.getEmployeeId(), timesheetDate,
							timesheetDate);

					if (holidayList.size() > 0) {
						shiftDetailsJsonObject.put("isHoliday", "true");
					} else {
						shiftDetailsJsonObject.put("isHoliday", "false");
					}

					coherentShiftApplicationDetailsJson
							.put(shiftDetailsJsonObject);
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}

			try {

				CoherentShiftApplication cSA = coherentShiftApplicationDAO
						.findById(shiftId);

				jsonObject.put("employeeId", cSA.getEmployee()
						.getEmployeeNumber());

				jsonObject
						.put("employeeName", cSA.getEmployee().getFirstName());

				// jsonObject.put("shiftType",
				// cSA.getShiftType().getCodeValue());

				jsonObject.put("coherentShiftApplicationDetails",
						coherentShiftApplicationDetailsJson);
				jsonObject.put("reviewrStatus", "true");
				/*ID ENCRYPT*/
				jsonObject.put("ShiftApplicationId",
						FormatPreserveCryptoUtil.encrypt(cSA.getShiftApplicationID()));

				jsonObject.put("batchName", cSA.getTimesheetBatch()
						.getTimesheetBatchDesc());
				String approvalSteps = "";
				if (!cSA.getTimesheetStatusMaster().getTimesheetStatusName()
						.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
					List<CoherentShiftApplicationReviewer> applicationReviewers = new ArrayList<>(
							cSA.getCoherentShiftApplicationReviewer());
					Collections.sort(applicationReviewers,
							new ClaimReviewerComp());

					for (CoherentShiftApplicationReviewer applicationReviewer : applicationReviewers) {
						approvalSteps += getEmployeeName(applicationReviewer
								.getEmployeeReviewer()) + " -> ";
					}
					if (approvalSteps.endsWith(" -> ")) {
						approvalSteps = StringUtils.removeEnd(approvalSteps,
								" -> ");
					}
					jsonObject.put("approvalSteps", approvalSteps);
				}
				if (cSA.getTimesheetStatusMaster().getTimesheetStatusName()
						.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
					List<EmployeeTimesheetReviewer> applicationReviewers = new ArrayList<>(
							employeeTimesheetReviewerDAO.findByCondition(cSA
									.getEmployee().getEmployeeId(), cSA
									.getCompany().getCompanyId()));
					Collections.sort(applicationReviewers,
							new EmployeeReviewerComp());

					for (EmployeeTimesheetReviewer applicationReviewer : applicationReviewers) {
						approvalSteps += getEmployeeName(applicationReviewer
								.getEmployeeReviewer()) + " -> ";
					}
					if (approvalSteps.endsWith(" -> ")) {
						approvalSteps = StringUtils.removeEnd(approvalSteps,
								" -> ");
					}
					jsonObject.put("approvalSteps", approvalSteps);

				}

				Company companyVO = companyDAO.findById(companyId);

				List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic
						.getEmpDynFieldsValueList(cSA.getCompany()
								.getCompanyId(), companyVO.getDateFormat(),
								employeeId);

				for (Object[] deptObject : deptCostCentreEmpObjectList) {

					if (deptObject != null
							&& deptObject[3] != null
							&& deptObject[3].equals(cSA.getEmployee()
									.getEmployeeNumber())) {

						if (deptObject[0] == null) {
							jsonObject.put("department", "");
						} else {
							jsonObject.put("department", deptObject[0]);
						}

						if (deptObject[2] == null) {
							jsonObject.put("costCentre", "");
						} else {
							jsonObject.put("costCentre", deptObject[2]);
						}
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return jsonObject.toString();

	}

	@Override
	public String shiftApplications(long shiftApplicationId, long employeeId,
			long companyId) {

		JSONObject jsonObject = new JSONObject();


		List<CoherentShiftApplicationDetail> coherentShiftApplicationDetails = new ArrayList<CoherentShiftApplicationDetail>();

		String companyDateFormat = companyDAO.findById(companyId)
				.getDateFormat();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				companyDateFormat);

		coherentShiftApplicationDetails = coherentShiftApplicationDAO
				.getCoherentShiftApplicationDetails(shiftApplicationId);

		JSONArray coherentShiftApplicationDetailsJson = new JSONArray();

		SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

		for (CoherentShiftApplicationDetail CoherentShiftApplicationDets : coherentShiftApplicationDetails) {
			try {
				JSONObject shiftDetailsJsonObject = new JSONObject();
				shiftDetailsJsonObject.put("date",
						CoherentShiftApplicationDets.getShiftDate());

				shiftDetailsJsonObject.put("shiftDate", simpleDateFormat
						.format(CoherentShiftApplicationDets.getShiftDate()));

				shiftDetailsJsonObject.put("shiftDay", format2
						.format(CoherentShiftApplicationDets.getShiftDate()));

				if (CoherentShiftApplicationDets.isShift()) {
					shiftDetailsJsonObject.put("shift", "Yes");
				} else {
					shiftDetailsJsonObject.put("shift", "No");
				}

				// Shift Type
				if (CoherentShiftApplicationDets.getShiftType() == null) {
					shiftDetailsJsonObject.put("shiftType", "");
				} else if (CoherentShiftApplicationDets
						.getShiftType()
						.getCodeDesc()
						.equalsIgnoreCase(
								PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND)) {
					shiftDetailsJsonObject.put("shiftType", "Second Shift");
				} else if (CoherentShiftApplicationDets
						.getShiftType()
						.getCodeDesc()
						.equalsIgnoreCase(
								PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_THIRD)) {
					shiftDetailsJsonObject.put("shiftType", "Third Shift");
				}

				if (CoherentShiftApplicationDets.isShiftChanged()) {
					shiftDetailsJsonObject.put("shiftChanged", "Yes");
				}

				if (CoherentShiftApplicationDets.isShiftTypeChanged()) {
					shiftDetailsJsonObject.put("shiftTypeChanged", "Yes");
				}

				shiftDetailsJsonObject.put("remarks",
						CoherentShiftApplicationDets.getRemarks());
				/*ID ENCRYPT*/
				shiftDetailsJsonObject.put("shiftApplicationDetailId",
						FormatPreserveCryptoUtil.encrypt(CoherentShiftApplicationDets
								.getShiftApplicationDetailID()));
				Date timesheetDate = new Date(CoherentShiftApplicationDets
						.getShiftDate().getTime());
				CoherentShiftApplication coherentShiftApplication = coherentShiftApplicationDAO
						.findById(shiftApplicationId,employeeId);
				List<String> holidayList = getHolidaysFor(
						coherentShiftApplication.getEmployee().getEmployeeId(),
						timesheetDate, timesheetDate);

				if (holidayList.size() > 0) {
					shiftDetailsJsonObject.put("isHoliday", "true");
				} else {
					shiftDetailsJsonObject.put("isHoliday", "false");
				}
				coherentShiftApplicationDetailsJson.put(shiftDetailsJsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {

			CoherentShiftApplication cSA = coherentShiftApplicationDAO
					.findById(shiftApplicationId);

			jsonObject.put("employeeId", cSA.getEmployee().getEmployeeNumber());

			jsonObject.put("employeeName", cSA.getEmployee().getFirstName());

			// jsonObject.put("shiftType", cSA.getShiftType().getCodeValue());
			jsonObject.put("shiftTotal", cSA.getTotalShifts());
			jsonObject.put("coherentShiftApplicationDetails",
					coherentShiftApplicationDetailsJson);
			jsonObject.put("reviewrStatus", "true");
			jsonObject.put("ShiftApplicationId", FormatPreserveCryptoUtil.encrypt(cSA.getShiftApplicationID()));
			jsonObject.put("batchName", cSA.getTimesheetBatch()
					.getTimesheetBatchDesc());
			String approvalSteps = "";
			if (!cSA.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
				List<CoherentShiftApplicationReviewer> applicationReviewers = new ArrayList<>(
						cSA.getCoherentShiftApplicationReviewer());
				Collections.sort(applicationReviewers, new ClaimReviewerComp());

				for (CoherentShiftApplicationReviewer applicationReviewer : applicationReviewers) {
					approvalSteps += getEmployeeName(applicationReviewer
							.getEmployeeReviewer()) + " -> ";
				}
				if (approvalSteps.endsWith(" -> ")) {
					approvalSteps = StringUtils
							.removeEnd(approvalSteps, " -> ");
				}
				jsonObject.put("approvalSteps", approvalSteps);
			}
			if (cSA.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
				List<EmployeeTimesheetReviewer> applicationReviewers = new ArrayList<>(
						employeeTimesheetReviewerDAO.findByCondition(cSA
								.getEmployee().getEmployeeId(), cSA
								.getCompany().getCompanyId()));
				Collections.sort(applicationReviewers,
						new EmployeeReviewerComp());

				for (EmployeeTimesheetReviewer applicationReviewer : applicationReviewers) {
					approvalSteps += getEmployeeName(applicationReviewer
							.getEmployeeReviewer()) + " -> ";
				}
				if (approvalSteps.endsWith(" -> ")) {
					approvalSteps = StringUtils
							.removeEnd(approvalSteps, " -> ");
				}
				jsonObject.put("approvalSteps", approvalSteps);

			}

			List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic
					.getEmpDynFieldsValueList(cSA.getCompany().getCompanyId(),
							cSA.getCompany().getDateFormat(), employeeId);

			for (Object[] deptObject : deptCostCentreEmpObjectList) {

				if (deptObject != null
						&& deptObject[3] != null
						&& deptObject[3].equals(cSA.getEmployee()
								.getEmployeeNumber())) {

					if (deptObject[0] == null) {
						jsonObject.put("department", "");
					} else {
						jsonObject.put("department", deptObject[0]);
					}

					if (deptObject[2] == null) {
						jsonObject.put("costCentre", "");
					} else {
						jsonObject.put("costCentre", deptObject[2]);
					}

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// }
		return jsonObject.toString();

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
	public long saveCherentShiftApplication(long batchId, long companyId,
			long employeeId) {

		CoherentShiftApplication coherentShiftApplication = new CoherentShiftApplication();
		coherentShiftApplication.setCompany(companyDAO.findById(companyId));
		coherentShiftApplication.setEmployee(employeeDAO.findById(employeeId));
		TimesheetBatch timesheetBatch = timesheetBatchDAO.findById(batchId);
		coherentShiftApplication.setTimesheetBatch(timesheetBatch);
		coherentShiftApplication
				.setTimesheetStatusMaster(timesheetStatusMasterDAO
						.findByName("Draft"));

		// coherentShiftApplication.setShiftType(dayTypeCode);

		coherentShiftApplication.setTotalShifts(0);
		return coherentShiftApplicationDAO
				.saveAndReturn(coherentShiftApplication);

	}

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
	public Map<String, String> updateCoherentShiftApplicationDetailEmployee(
			String shiftDetailId, String totalShift, String remarks,
			String isShift, String coherentShiftType, String shiftTypePerDate) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		CoherentShiftApplicationDetail coherentShiftApplicationDetail = coherentShiftApplicationDetailDAO
				.findById(Long.valueOf(shiftDetailId),employeeId);

		coherentShiftApplicationDetail.setRemarks(remarks);

		if (isShift.equalsIgnoreCase("Yes")) {
			coherentShiftApplicationDetail.setShift(true);
		} else {
			coherentShiftApplicationDetail.setShift(false);
		}

		if (StringUtils.isNotBlank(shiftTypePerDate)) {
			if (shiftTypePerDate
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND)) {
				AppCodeMaster shiftTypeCode = appCodeMasterDAO
						.findByCategoryAndDesc(
								PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_SHIFT_TYPE,
								PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND);
				coherentShiftApplicationDetail.setShiftType(shiftTypeCode);
			} else if (shiftTypePerDate
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_THIRD)) {
				AppCodeMaster shiftTypeCode = appCodeMasterDAO
						.findByCategoryAndDesc(
								PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_SHIFT_TYPE,
								PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_THIRD);
				coherentShiftApplicationDetail.setShiftType(shiftTypeCode);
			}
		} else {
			coherentShiftApplicationDetail.setShiftType(null);
		}

		coherentShiftApplicationDetailDAO
				.update(coherentShiftApplicationDetail);

		CoherentShiftApplication coherentShiftApplication = coherentShiftApplicationDAO
				.findById(coherentShiftApplicationDetail
						.getCoherentShiftApplication().getShiftApplicationID());

		List<CoherentShiftApplicationDetail> coherentShiftApplicationDetails = coherentShiftApplicationDAO
				.getCoherentShiftApplicationDetails(coherentShiftApplicationDetail
						.getCoherentShiftApplication().getShiftApplicationID());
		int totShifts = 0;
		String isShft;
		for (CoherentShiftApplicationDetail coherentShiftApplicationDetail2 : coherentShiftApplicationDetails) {
			if (coherentShiftApplicationDetail2.isShift()) {
				totShifts = totShifts + 1;
			}
		}

		coherentShiftApplication.setTotalShifts(Integer.valueOf(totShifts));

		coherentShiftApplicationDAO.update(coherentShiftApplication);
		Map<String, String> statusMap = new HashMap<String, String>();

		CoherentShiftApplicationDetail coShiftApplicationDetail = coherentShiftApplicationDetailDAO
				.findById(Long.valueOf(shiftDetailId));

		if (coShiftApplicationDetail.isShift()) {
			isShft = "Yes";
		} else {
			isShft = "No";
		}

		statusMap.put("totShifts", String.valueOf(totShifts));

		statusMap.put("isShft", String.valueOf(isShft));

		if (coShiftApplicationDetail.getShiftType() != null) {
			statusMap.put("shiftType", coShiftApplicationDetail.getShiftType()
					.getCodeDesc());
		} else {
			statusMap.put("shiftType", "");
		}

		statusMap.put("coherentShiftType", coherentShiftType);
		statusMap.put("remarks", coShiftApplicationDetail.getRemarks());

		return statusMap;

	}

	@Override
	public Map<String, String> updateCoherentShiftApplicationDetailRevewer(
			String shiftDetailId, String totalShift, String remarks,
			String isShift, String coherentShiftType, String shiftTypePerDate) {

		CoherentShiftApplicationDetail coherentShiftApplicationDetail = coherentShiftApplicationDetailDAO
				.findById(Long.valueOf(shiftDetailId));

		coherentShiftApplicationDetail.setRemarks(remarks);

		boolean shift;

		if (isShift.equalsIgnoreCase("Yes")) {
			shift = true;

		} else {
			shift = false;

		}

		if (coherentShiftApplicationDetail.isShift() != shift) {
			coherentShiftApplicationDetail.setShiftChanged(true);
		}

		String shiftTypeChangeStatus = "";
		if (StringUtils.isBlank(shiftTypePerDate)) {
			shiftTypeChangeStatus = null;
		} else {
			shiftTypeChangeStatus = shiftTypePerDate;
		}

		if (coherentShiftApplicationDetail.getShiftType() == null
				&& shiftTypeChangeStatus != null) {
			coherentShiftApplicationDetail.setShiftTypeChanged(true);
		} else if (coherentShiftApplicationDetail.getShiftType() != null
				&& shiftTypeChangeStatus == null) {
			coherentShiftApplicationDetail.setShiftTypeChanged(true);
		} else if (coherentShiftApplicationDetail.getShiftType() != null
				&& shiftTypeChangeStatus != null) {
			if (!shiftTypeChangeStatus
					.equalsIgnoreCase(coherentShiftApplicationDetail
							.getShiftType().getCodeDesc())) {
				coherentShiftApplicationDetail.setShiftTypeChanged(true);
			}
		}

		if (shift) {
			coherentShiftApplicationDetail.setShift(true);
		} else {
			coherentShiftApplicationDetail.setShift(false);
		}

		if (StringUtils.isNotBlank(shiftTypePerDate)) {
			if (shiftTypePerDate
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND)) {
				AppCodeMaster shiftTypeCode = appCodeMasterDAO
						.findByCategoryAndDesc(
								PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_SHIFT_TYPE,
								PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_SECOND);
				coherentShiftApplicationDetail.setShiftType(shiftTypeCode);
			} else if (shiftTypePerDate
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_THIRD)) {
				AppCodeMaster shiftTypeCode = appCodeMasterDAO
						.findByCategoryAndDesc(
								PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_SHIFT_TYPE,
								PayAsiaConstants.APP_CODE_CATEGORY_SHIFT_TYPE_THIRD);
				coherentShiftApplicationDetail.setShiftType(shiftTypeCode);
			}
		} else {
			coherentShiftApplicationDetail.setShiftType(null);
		}

		coherentShiftApplicationDetailDAO
				.update(coherentShiftApplicationDetail);

		CoherentShiftApplication coherentShiftApplication = coherentShiftApplicationDAO
				.findById(coherentShiftApplicationDetail
						.getCoherentShiftApplication().getShiftApplicationID());

		List<CoherentShiftApplicationDetail> coherentShiftApplicationDetails = coherentShiftApplicationDAO
				.getCoherentShiftApplicationDetails(coherentShiftApplicationDetail
						.getCoherentShiftApplication().getShiftApplicationID());
		int totShifts = 0;
		String isShft;
		String isShiftChanged = "";
		String isShiftTypeChanged = "";
		for (CoherentShiftApplicationDetail coherentShiftApplicationDetail2 : coherentShiftApplicationDetails) {
			if (coherentShiftApplicationDetail2.isShift()) {
				totShifts = totShifts + 1;
			}
		}

		coherentShiftApplication.setTotalShifts(Integer.valueOf(totShifts));

		coherentShiftApplicationDAO.update(coherentShiftApplication);
		Map<String, String> statusMap = new HashMap<String, String>();

		CoherentShiftApplicationDetail coShiftApplicationDetail = coherentShiftApplicationDetailDAO
				.findById(Long.valueOf(shiftDetailId));

		if (coShiftApplicationDetail.isShift()) {
			isShft = "Yes";
		} else {
			isShft = "No";
		}

		if (coShiftApplicationDetail.isShiftChanged()) {
			isShiftChanged = "Yes";
		}
		if (coShiftApplicationDetail.isShiftTypeChanged()) {
			isShiftTypeChanged = "Yes";
		}

		statusMap.put("totShifts", String.valueOf(totShifts));

		statusMap.put("isShft", String.valueOf(isShft));
		statusMap.put("isShiftChanged", String.valueOf(isShiftChanged));

		if (coShiftApplicationDetail.getShiftType() != null) {
			statusMap.put("shiftType", coShiftApplicationDetail.getShiftType()
					.getCodeDesc());
		} else {
			statusMap.put("shiftType", "");
		}
		statusMap.put("isShiftTypeChanged", String.valueOf(isShiftTypeChanged));

		statusMap.put("coherentShiftType", coherentShiftType);
		statusMap.put("remarks", coShiftApplicationDetail.getRemarks());

		return statusMap;

	}

	@Override
	public void submitToWorkFlow(
			List<LundinEmployeeTimesheetReviewerDTO> employeeOTReviewerDTOs,
			long shiftApplicationId, long companyId, long employeeId) {

		CoherentShiftApplication coherentShiftApplication = coherentShiftApplicationDAO
				.findById(shiftApplicationId);

		boolean isSuccessfullyFor = false;
		Employee firstEmployee = null;
		boolean firstEntry = true;
		for (LundinEmployeeTimesheetReviewerDTO employeeOTReviewerDTO : employeeOTReviewerDTOs) {

			try {
				CoherentShiftApplicationReviewer coherentShiftApplicationReviewer = new CoherentShiftApplicationReviewer();

				coherentShiftApplicationReviewer
						.setCoherentShiftApplication(coherentShiftApplication);
				coherentShiftApplicationReviewer
						.setWorkFlowRuleMaster(workflowRuleMasterDAO
								.findByID(employeeOTReviewerDTO
										.getWorkFlowRuleMasterId()));
				if (firstEntry) {

					firstEmployee = getDelegatedEmployee(employeeOTReviewerDTO
							.getEmployeeReviewerId());

					coherentShiftApplicationReviewer
							.setEmployeeReviewer(firstEmployee);

					coherentShiftApplicationReviewer.setPending(true);
					firstEntry = false;
				} else {
					coherentShiftApplicationReviewer
							.setEmployeeReviewer(getDelegatedEmployee(employeeOTReviewerDTO
									.getEmployeeReviewerId()));
					coherentShiftApplicationReviewer.setPending(false);
				}

				coherentShiftApplicationReviewerDAO
						.save(coherentShiftApplicationReviewer);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		TimesheetStatusMaster otStatusMaster = lundinStatusMasterDAO
				.findByName(PayAsiaConstants.OT_STATUS_SUBMITTED);

		coherentShiftApplication.setTimesheetStatusMaster(otStatusMaster);

		coherentShiftApplicationDAO.update(coherentShiftApplication);

		CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow = new CoherentShiftApplicationWorkflow();
		coherentShiftApplicationWorkflow.setCompanyId(companyId);
		// lundinOtTimesheetWorkflow.setEmailCC(firstEmployee.getEmail());

		// coherentShiftApplicationWorkflow.setForwardTo(firstEmployee.getEmail());

		coherentShiftApplicationWorkflow.setRemarks("");
		coherentShiftApplicationWorkflow
				.setTimesheetStatusMaster(otStatusMaster);
		coherentShiftApplicationWorkflow
				.setCoherentShiftApplication(coherentShiftApplication);

		Employee loggedInEmp = empDAO.findById(employeeId);
		coherentShiftApplicationWorkflow.setCreatedBy(loggedInEmp);
		coherentShiftApplicationWorkflow.setCreatedDate(new Timestamp(
				new Date().getTime()));

		isSuccessfullyFor = true;

		if (isSuccessfullyFor) {
			EmailDataDTO emailDataDTO = new EmailDataDTO();
			emailDataDTO.setTimesheetId(coherentShiftApplication
					.getShiftApplicationID());
			emailDataDTO
					.setEmployeeName(getEmployeeNameWithNumber(coherentShiftApplication
							.getEmployee()));
			emailDataDTO.setEmployeeNumber(coherentShiftApplication
					.getEmployee().getEmployeeNumber());
			emailDataDTO.setBatchDesc(coherentShiftApplication
					.getTimesheetBatch().getTimesheetBatchDesc());
			emailDataDTO
					.setOvertimeShiftType(PayAsiaConstants.COHERENT_SHIFT_TYPE);
			emailDataDTO.setEmailTo(firstEmployee.getEmail());
			emailDataDTO.setEmailFrom(loggedInEmp.getEmail());
			emailDataDTO.setReviewerFirstName(firstEmployee.getFirstName());
			emailDataDTO.setReviewerLastName(firstEmployee.getLastName());
			emailDataDTO.setReviewerCompanyId(firstEmployee.getCompany()
					.getCompanyId());
			coherentTimesheetMailLogic
					.sendEMailForTimesheet(
							companyId,
							emailDataDTO,
							PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPLY);

		}
	}

	private Employee getDelegatedEmployee(Long employeeId) {
		Employee emp = empDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterOT = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_TYPE_COHERENT_TIMESHEET_DESC);

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
	public boolean withdrawShiftRequest(long shiftApplicationId,
			long employeeId, Long companyId) {
		if (getCanWithdraw(shiftApplicationId)) {
			CoherentShiftApplication coherentShiftApplication = coherentShiftApplicationDAO
					.findById(shiftApplicationId,employeeId);
			try {
				TimesheetStatusMaster otStatusMaster = lundinStatusMasterDAO
						.findByName(PayAsiaConstants.OT_STATUS_DRAFT);

				coherentShiftApplication
						.setTimesheetStatusMaster(otStatusMaster);
				coherentShiftApplicationDAO.update(coherentShiftApplication);

				for (CoherentShiftApplicationReviewer otTsheetReviewer : coherentShiftApplication
						.getCoherentShiftApplicationReviewer()) {

					coherentShiftApplicationReviewerDAO
							.delete(otTsheetReviewer);
				}

				for (CoherentShiftApplicationWorkflow workflow : coherentShiftApplication
						.getCoherentShiftApplicationWorkflows()) {

					coherentShiftApplicationWorkflowDAO.delete(workflow);
				}

				List<EmployeeTimesheetReviewer> employeeTimesheetReviewerList = employeeTimesheetReviewerDAO
						.findByCondition(employeeId, companyId);
				EmployeeTimesheetReviewer firstApplicationReviewer = null;
				Collections.sort(employeeTimesheetReviewerList,
						new EmployeeReviewerComp());
				boolean firstRevFound = false;
				for (EmployeeTimesheetReviewer applicationReviewer : employeeTimesheetReviewerList) {
					if (!firstRevFound) {
						firstApplicationReviewer = applicationReviewer;
						firstRevFound = true;
					}
				}

				EmailDataDTO emailDataDTO = new EmailDataDTO();
				emailDataDTO.setTimesheetId(coherentShiftApplication
						.getShiftApplicationID());
				emailDataDTO
						.setEmployeeName(getEmployeeName(coherentShiftApplication
								.getEmployee()));
				emailDataDTO.setEmployeeNumber(coherentShiftApplication
						.getEmployee().getEmployeeNumber());
				emailDataDTO.setBatchDesc(coherentShiftApplication
						.getTimesheetBatch().getTimesheetBatchDesc());
				emailDataDTO
						.setOvertimeShiftType(PayAsiaConstants.COHERENT_SHIFT_TYPE);
				if (firstApplicationReviewer != null) {
					emailDataDTO.setEmailTo(firstApplicationReviewer
							.getEmployeeReviewer().getEmail());
				}
				emailDataDTO.setEmailFrom(coherentShiftApplication
						.getEmployee().getEmail());

				coherentTimesheetMailLogic
						.sendWithdrawEmailForTimesheet(
								companyId,
								emailDataDTO,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_WITHDRAW);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}

	}

	@Override
	public AddClaimFormResponse getAllCoherentShifts(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String transactionType, Long companyId, String searchCondition,
			String searchText) {

		LundinTsheetConditionDTO conditionDto = new LundinTsheetConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.OT_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setBatch("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_REVIEWERS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setReviewers("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_STATUS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setStatus("%" + searchText.trim() + "%");
			}

		}

		int recordSize = coherentShiftApplicationDAO
				.getCountByConditionForEmployee(empId, fromDate, toDate,
						conditionDto, companyId);

		Boolean visibleToEmployee = true;
		List<CoherentShiftApplication> coherentShiftApplications = coherentShiftApplicationDAO
				.findByConditionForEmployee(pageDTO, sortDTO, empId, fromDate,
						toDate, visibleToEmployee, conditionDto, companyId);

		List<AddClaimForm> addOtFormList = new ArrayList<AddClaimForm>();
		for (CoherentShiftApplication otApplication : coherentShiftApplications) {
			AddClaimForm addOtForm = new AddClaimForm();

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				StringBuilder claimTemplateEdit = new StringBuilder();
				claimTemplateEdit.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateEdit.append("<br>");
				/*ID ENCRYPT*/
				claimTemplateEdit
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editCoherentShiftApplications("
								+ FormatPreserveCryptoUtil.encrypt(otApplication.getShiftApplicationID())
								+ ");'>[Edit]</a></span>");

				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateEdit));
				StringBuilder claimWithDrawLink = new StringBuilder();
				claimWithDrawLink
						.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplication("
								+ FormatPreserveCryptoUtil.encrypt(otApplication.getShiftApplicationID())
								+ ");'>Delete</a>");

				addOtForm.setAction(String.valueOf(claimWithDrawLink));
			} else {
				StringBuilder claimTemplateView = new StringBuilder();
				claimTemplateView.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateView.append("<br>");
				String claimStatusMode = ",\"submitted\"";

				if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {

					claimStatusMode = ",\"Approved\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
						|| otApplication
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
					claimStatusMode = ",\"submitted\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
					claimStatusMode = ",\"Withdrawn\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED)) {
					claimStatusMode = ",\"Rejected\"";
				}

				claimTemplateView
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewCoherentShiftApplications("
								+FormatPreserveCryptoUtil.encrypt(otApplication.getShiftApplicationID())
								+ claimStatusMode + ");'>[View]</a></span>");
				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateView));
			}

			addOtForm.setCreateDate(DateUtils.timeStampToString(otApplication
					.getCreatedDate()));
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equals(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {

				if (otApplication.getCreatedBy() != null) {
					if (otApplication.getCreatedBy().equals(
							String.valueOf(empId))) {
						StringBuilder claimWithDrawLink = new StringBuilder();
						claimWithDrawLink
								.append("<a class='alink' href='#' onClick='javascipt:withDrawClaimApplication("
										+ FormatPreserveCryptoUtil.encrypt(otApplication.getShiftApplicationID())
										+ ");'>Withdraw</a>");

						addOtForm.setAction(String.valueOf(claimWithDrawLink));
					}
				}
			}
			addOtForm.setClaimApplicationId(otApplication
					.getShiftApplicationID());
			List<CoherentShiftApplicationReviewer> claimAppReviewers = new ArrayList<>(
					otApplication.getCoherentShiftApplicationReviewer());

			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					lundinEmpReviewerDAO.findByCondition(empId, otApplication
							.getCompany().getCompanyId()));

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
				getPendingClaimReviewers(employeeClaimReviewers, addOtForm);
			}

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);

				getApprovedClaimReviewers(claimAppReviewers, addOtForm,
						pageContextPath, otApplication);

			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				getSubmittedClaimReviewers(claimAppReviewers, addOtForm,
						pageContextPath, otApplication);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
				getWithdrawClaimReviewers(claimAppReviewers, addOtForm);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
				getRejectedClaimReviewers(claimAppReviewers, addOtForm,
						pageContextPath, otApplication);
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

	public void getPendingClaimReviewers(
			List<EmployeeTimesheetReviewer> empOtReviewers,
			AddClaimForm addOTForm) {
		Collections.sort(empOtReviewers, new EmployeeOTReviewerComp());
		for (EmployeeTimesheetReviewer employeeOtReviewer : empOtReviewers) {
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {

				addOTForm
						.setClaimReviewer1(getEmployeeNameWithNumber(employeeOtReviewer
								.getEmployeeReviewer()));

			}
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {

				addOTForm
						.setClaimReviewer2(getEmployeeNameWithNumber(employeeOtReviewer
								.getEmployeeReviewer()));

			}
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {

				addOTForm
						.setClaimReviewer3(getEmployeeNameWithNumber(employeeOtReviewer
								.getEmployeeReviewer()));

			}

		}
	}

	private class IngersollOTTimesheetReviewerComp implements
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

	public void getApprovedClaimReviewers(
			List<CoherentShiftApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath,
			CoherentShiftApplication lundinTimesheet) {
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (CoherentShiftApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());

				StringBuilder ClaimReviewer1 = new StringBuilder(
						getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
								pageContextPath,
								otApplicationReviewer.getEmployeeReviewer()));

				ClaimReviewer1
						.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ applicationWorkflow.getCreatedDate()
								+ "</span>");

				addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

			}
			if (revCount == 2) {

				CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
				if (applicationWorkflow == null) {
					continue;
				}

				StringBuilder ClaimReviewer2 = new StringBuilder(
						getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
								pageContextPath,
								otApplicationReviewer.getEmployeeReviewer()));

				if (applicationWorkflow.getCreatedDate() != null) {
					ClaimReviewer2
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate()
									+ "</span>");
				}

				addOTForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

			}
			if (revCount == 3) {

				CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
				if (applicationWorkflow == null) {
					continue;
				}

				StringBuilder ClaimReviewer3 = new StringBuilder(
						getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
								pageContextPath,
								otApplicationReviewer.getEmployeeReviewer()));

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
			List<CoherentShiftApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath,
			CoherentShiftApplication lundinTimesheet) {

		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (CoherentShiftApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());

				if (applicationWorkflow == null) {

					addOTForm.setClaimReviewer1(getStatusImage(
							PayAsiaConstants.CLAIM_STATUS_PENDING,
							pageContextPath,
							otApplicationReviewer.getEmployeeReviewer()));

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
									otApplicationReviewer.getEmployeeReviewer()));

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
									otApplicationReviewer.getEmployeeReviewer()));

					ClaimReviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate()
									+ "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

				}
			}
			if (revCount == 2) {

				CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());

				if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer1Status
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addOTForm.setClaimReviewer2(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployeeReviewer()));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {

						addOTForm.setClaimReviewer2(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployeeReviewer()));
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
										otApplicationReviewer
												.getEmployeeReviewer()));

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
								otApplicationReviewer.getEmployeeReviewer()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

					}

				}

			}
			if (revCount == 3) {
				CoherentShiftApplicationWorkflow applicationWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
				if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer2Status
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addOTForm.setClaimReviewer3(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployeeReviewer()));

				} else if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {
						addOTForm.setClaimReviewer3(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployeeReviewer()));
					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer
												.getEmployeeReviewer()));

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
										otApplicationReviewer
												.getEmployeeReviewer()));

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
			List<CoherentShiftApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath,
			CoherentShiftApplication lundinTimesheet) {
		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (CoherentShiftApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				CoherentShiftApplicationWorkflow lundinWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());

				if (lundinWorkflow == null) {

					addOTForm.setClaimReviewer1(getStatusImage(
							PayAsiaConstants.CLAIM_STATUS_PENDING,
							pageContextPath,
							otApplicationReviewer.getEmployeeReviewer()));

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
									otApplicationReviewer.getEmployeeReviewer()));

					claimReviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ lundinWorkflow.getCreatedDate()
									+ "</span>");

					addOTForm.setClaimReviewer1(String.valueOf(claimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				}
			}
			if (revCount == 2) {

				CoherentShiftApplicationWorkflow lundinWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());

				if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addOTForm.setClaimReviewer2(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployeeReviewer()));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (lundinWorkflow == null) {

						addOTForm.setClaimReviewer2(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployeeReviewer()));
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
										otApplicationReviewer
												.getEmployeeReviewer()));

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
				CoherentShiftApplicationWorkflow lundinWorkflow = coherentShiftApplicationWorkflowDAO
						.findByCondition(lundinTimesheet
								.getShiftApplicationID(), otApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
				if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addOTForm.setClaimReviewer3(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployeeReviewer()));

				} else if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (lundinWorkflow == null) {
						addOTForm.setClaimReviewer3(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployeeReviewer()));
					} else if (lundinWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer3 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer
												.getEmployeeReviewer()));

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
			List<CoherentShiftApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOtForm) {
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (CoherentShiftApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				addOtForm
						.setClaimReviewer1(getEmployeeNameWithNumber(otApplicationReviewer
								.getEmployeeReviewer()));

			}
			if (revCount == 2) {

				addOtForm
						.setClaimReviewer2(getEmployeeNameWithNumber(otApplicationReviewer
								.getEmployeeReviewer()));

			}
			if (revCount == 3) {

				addOtForm
						.setClaimReviewer3(getEmployeeNameWithNumber(otApplicationReviewer
								.getEmployeeReviewer()));

			}
			revCount++;
		}
	}

	@Override
	public boolean getCanWithdraw(long timesheetId) {
		CoherentShiftApplication coherentShiftApplication = coherentShiftApplicationDAO
				.findById(timesheetId);

		Set<CoherentShiftApplicationReviewer> coherentShiftApplicationReviewers = coherentShiftApplication
				.getCoherentShiftApplicationReviewer();

		List<CoherentShiftApplicationReviewer> reviewersList = new ArrayList<CoherentShiftApplicationReviewer>(
				coherentShiftApplicationReviewers);

		if (reviewersList != null && !reviewersList.isEmpty()) {
			Collections.sort(reviewersList, new CoherentShiftReviewerComp());

			if (reviewersList.get(0).isPending())
				return true;

		}
		return false;
	}

	private class CoherentShiftReviewerComp implements
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
	public PendingOTTimesheetResponseForm getPendingEmployeeShift(Long empId,
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

		recordSize = coherentShiftApplicationReviewerDAO
				.findByConditionCountRecords(empId, otTimesheetConditionDTO,
						companyId);

		List<CoherentShiftApplicationReviewer> otReviewers = coherentShiftApplicationReviewerDAO
				.findByCondition(empId, pageDTO, sortDTO,
						otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentShiftApplicationReviewer otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			CoherentShiftApplication lundinOTTimesheet = otReviewer
					.getCoherentShiftApplication();
			TimesheetBatch lundinOtBatch = lundinOTTimesheet
					.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(lundinOTTimesheet
					.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils
					.timeStampToString(lundinOTTimesheet.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:viewCoherentPendingShiftApplications("
							+ FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getShiftApplicationID())
							+ ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc()
							+ "&apos;" + ");'>Review" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(lundinOTTimesheet
					.getShiftApplicationID());
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch
					.getTimesheetBatchDesc());
			pendingOTTimesheetForm.setOtTimesheetReviewerId(otReviewer
					.getShiftApplicationReviewerID());
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
	public PendingOTTimesheetResponseForm getAllShift(Long empId,
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

		recordSize = coherentShiftApplicationReviewerDAO
				.getCountForAllByCondition(empId, otTimesheetConditionDTO,
						companyId);
		List<CoherentShiftApplicationReviewer> otReviewers = coherentShiftApplicationReviewerDAO
				.findAllByCondition(empId, pageDTO, sortDTO,
						otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentShiftApplicationReviewer otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			CoherentShiftApplication lundinOTTimesheet = otReviewer
					.getCoherentShiftApplication();
			TimesheetBatch lundinOtBatch = lundinOTTimesheet
					.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(lundinOTTimesheet
					.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils
					.timeStampToString(lundinOTTimesheet.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();

			if (lundinOTTimesheet.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)
					|| lundinOTTimesheet
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewCoherentPendingShiftApplications("
								+ FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getShiftApplicationID())
								+ ",&apos;"
								+ lundinOtBatch.getTimesheetBatchDesc()
								+ "&apos;" + ");'>Review" + "</a>");
			} else {
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewApprovedOrRejectShiftItems("
								+ FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getShiftApplicationID())
								+ ",&apos;"
								+ lundinOtBatch.getTimesheetBatchDesc()
								+ "&apos;" + ");'>View" + "</a>");
			}
			/*ID ENCRYPT*/
			pendingOTTimesheetForm.setOtTimesheetId(FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet
					.getShiftApplicationID()));
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch
					.getTimesheetBatchDesc());
			pendingOTTimesheetForm.setOtTimesheetReviewerId(otReviewer
					.getShiftApplicationReviewerID());
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
	public PendingOTTimesheetResponseForm getShift(Long empId,
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

		// List<String> claimStatus = new ArrayList<>();
		// claimStatus.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		otTimesheetConditionDTO.setStatusNameList(StatusNameList);

		int recordSize = 0;

		recordSize = coherentShiftApplicationReviewerDAO
				.findByConditionCountRecords(empId, otTimesheetConditionDTO,
						companyId);
		List<CoherentShiftApplicationReviewer> otReviewers = coherentShiftApplicationReviewerDAO
				.findByCondition(empId, pageDTO, sortDTO,
						otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentShiftApplicationReviewer otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			CoherentShiftApplication lundinOTTimesheet = otReviewer
					.getCoherentShiftApplication();
			TimesheetBatch lundinOtBatch = lundinOTTimesheet
					.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(lundinOTTimesheet
					.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils
					.timeStampToString(lundinOTTimesheet.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:viewApprovedOrRejectShiftItems("
							+ FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getShiftApplicationID())
							+ ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc()
							+ "&apos;" + ");'>View" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(lundinOTTimesheet
					.getShiftApplicationID());
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch
					.getTimesheetBatchDesc());
			pendingOTTimesheetForm.setOtTimesheetReviewerId(otReviewer
					.getShiftApplicationReviewerID());
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
	public List<LundinOTBatchDTO> getOTBacthesByCompanyId(long companyId,
			Long employeeId) {
		List<LundinOTBatchDTO> coherentOTBatchDTOList = new ArrayList<LundinOTBatchDTO>();
		List<TimesheetBatch> allCoherentTBatches = new ArrayList<TimesheetBatch>();
		List<Long> batchIdList = new ArrayList<Long>();
		List<String> otStatusList = new ArrayList<String>();
		otStatusList.add("Rejected");
		otStatusList.add("Withdrawn");

		TimesheetBatch TimesheetBatch = timesheetBatchDAO
				.getBatchByCurrentDate(companyId, DateUtils
						.convertDateToTimeStamp(Calendar.getInstance()
								.getTime()));
		if (TimesheetBatch == null) {
			return coherentOTBatchDTOList;
		}
		allCoherentTBatches = timesheetBatchDAO
				.getCurrentAndPreviousBatchByCompany(companyId, DateUtils
						.convertDateToTimeStamp(Calendar.getInstance()
								.getTime()));
		for (TimesheetBatch batch : allCoherentTBatches) {
			batchIdList.add(batch.getTimesheetBatchId());
		}

		List<Long> overtimeApplicationBatchList = coherentShiftApplicationDAO
				.findByBatchId(employeeId, batchIdList, otStatusList);

		if (overtimeApplicationBatchList != null
				&& overtimeApplicationBatchList.size() > 0
				&& batchIdList.size() > 0) {
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
			coherentOtBatchDto.setOtBatchDesc(coherentOTBatch
					.getTimesheetBatchDesc());
			coherentOtBatchDto.setOtBatchId(coherentOTBatch
					.getTimesheetBatchId());

			coherentOTBatchDTOList.add(coherentOtBatchDto);
		}

		return coherentOTBatchDTOList;
	}

	@Override
	public Long getEmployeeIdFromShiftId(long shiftApplicationId) {

		long empid = coherentShiftApplicationDAO.findById(shiftApplicationId)
				.getEmployee().getEmployeeId();
		return empid;
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
				.findById(timesheetId);

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

				if (employeeId.equals(reviewer.getEmployeeReviewer()
						.getEmployeeId())) {
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

				if (employeeId.equals(reviewer.getEmployeeReviewer()
						.getEmployeeId())) {
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

				if (employeeId.equals(reviewer.getEmployeeReviewer()
						.getEmployeeId())) {
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
		toReturn.setEmployeeId(employeeId);
		toReturn.setTimesheetId(timesheetApplication.getShiftApplicationID());
		toReturn.setRemarks("");
		return toReturn;
	}

	@Override
	public PendingOTTimesheetForm acceptShift(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		Boolean isSuccessfullyAcc = false;
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		CoherentShiftApplication coherentShiftApplication = null;
		
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			CoherentShiftApplicationReviewer otApplicationReviewer = coherentShiftApplicationReviewerDAO
					.findByCondition(pendingOTTimesheetForm.getOtTimesheetId(),
							pendingOTTimesheetForm.getOtTimesheetReviewerId());

			CoherentShiftApplicationWorkflow applicationWorkflow = new CoherentShiftApplicationWorkflow();

			coherentShiftApplication = otApplicationReviewer
					.getCoherentShiftApplication();

			List<String> otApprovedStatusList = new ArrayList<>();
			otApprovedStatusList.add(PayAsiaConstants.OT_STATUS_COMPLETED);

			TimesheetStatusMaster otStatusCompleted = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
			coherentShiftApplication
					.setTimesheetStatusMaster(otStatusCompleted);

			coherentShiftApplication.setUpdatedDate(new Timestamp(date
					.getTime()));
			coherentShiftApplicationDAO.update(coherentShiftApplication);

			/*
			 * otApplicationReviewer.setPending(false);
			 * otApplicationReviewer.setEmployeeReviewer(employee);
			 * coherentShiftApplicationReviewerDAO
			 * .update(otApplicationReviewer);
			 */

			for (CoherentShiftApplicationReviewer appReviewer : coherentShiftApplication
					.getCoherentShiftApplicationReviewer()) {
				if (appReviewer.isPending()) {
					appReviewer.setPending(false);
					appReviewer.setEmployeeReviewer(employee);
					coherentShiftApplicationReviewerDAO.update(appReviewer);
				}
			}

			applicationWorkflow.setCreatedBy(employee);
			applicationWorkflow
					.setCoherentShiftApplication(otApplicationReviewer
							.getCoherentShiftApplication());

			applicationWorkflow.setTimesheetStatusMaster(otStatusCompleted);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));

			coherentShiftApplicationWorkflowDAO
					.save(applicationWorkflow);
			isSuccessfullyAcc = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		if (isSuccessfullyAcc) {

			EmailDataDTO emailDataDTO = new EmailDataDTO();
			emailDataDTO.setTimesheetId(coherentShiftApplication
					.getShiftApplicationID());
			emailDataDTO
					.setEmployeeName(getEmployeeName(coherentShiftApplication
							.getEmployee()));
			emailDataDTO.setEmpCompanyId(coherentShiftApplication.getEmployee()
					.getCompany().getCompanyId());
			emailDataDTO.setEmployeeNumber(coherentShiftApplication
					.getEmployee().getEmployeeNumber());
			emailDataDTO.setBatchDesc(coherentShiftApplication
					.getTimesheetBatch().getTimesheetBatchDesc());
			emailDataDTO
					.setOvertimeShiftType(PayAsiaConstants.COHERENT_SHIFT_TYPE);
			emailDataDTO.setEmailFrom(employee.getEmail());
			emailDataDTO.setEmailTo(coherentShiftApplication.getEmployee()
					.getEmail());

			coherentTimesheetMailLogic
					.sendAcceptRejectMailForTimesheet(
							companyId,
							emailDataDTO,
							PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPROVE,
							pendingOTTimesheetForm.getRemarks());
		}

		return response;

	}

	@Override
	public PendingOTTimesheetForm rejectShift(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessRejeted = false;
		CoherentShiftApplication coherentShiftApplication = null;
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			CoherentShiftApplicationReviewer otApplicationReviewer = coherentShiftApplicationReviewerDAO
					.findByCondition(pendingOTTimesheetForm.getOtTimesheetId(),
							pendingOTTimesheetForm.getOtTimesheetReviewerId());

			CoherentShiftApplicationWorkflow applicationWorkflow = new CoherentShiftApplicationWorkflow();

			TimesheetStatusMaster otStatusMaster = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_REJECTED);
			coherentShiftApplication = otApplicationReviewer
					.getCoherentShiftApplication();

			List<String> claimApprovedStatusList = new ArrayList<>();
			claimApprovedStatusList.add(PayAsiaConstants.OT_STATUS_COMPLETED);

			coherentShiftApplication.setTimesheetStatusMaster(otStatusMaster);
			coherentShiftApplication.setUpdatedDate(new Timestamp(date
					.getTime()));
			coherentShiftApplicationDAO.update(coherentShiftApplication);

			for (CoherentShiftApplicationReviewer applicationReviewer : coherentShiftApplication
					.getCoherentShiftApplicationReviewer()) {
				if (applicationReviewer.isPending()) {
					applicationReviewer.setPending(false);
					coherentShiftApplicationReviewerDAO
							.update(applicationReviewer);
				}
			}

			applicationWorkflow.setCreatedBy(employee);
			applicationWorkflow
					.setCoherentShiftApplication(otApplicationReviewer
							.getCoherentShiftApplication());
			applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			coherentShiftApplicationWorkflowDAO
					.save(applicationWorkflow);
			isSuccessRejeted = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		if (isSuccessRejeted) {
			EmailDataDTO emailDataDTO = new EmailDataDTO();
			emailDataDTO.setTimesheetId(coherentShiftApplication
					.getShiftApplicationID());
			emailDataDTO
					.setEmployeeName(getEmployeeName(coherentShiftApplication
							.getEmployee()));
			emailDataDTO.setEmpCompanyId(coherentShiftApplication.getEmployee()
					.getCompany().getCompanyId());
			emailDataDTO.setEmployeeNumber(coherentShiftApplication
					.getEmployee().getEmployeeNumber());
			emailDataDTO.setBatchDesc(coherentShiftApplication
					.getTimesheetBatch().getTimesheetBatchDesc());
			emailDataDTO
					.setOvertimeShiftType(PayAsiaConstants.COHERENT_SHIFT_TYPE);
			emailDataDTO.setEmailFrom(employee.getEmail());
			emailDataDTO.setEmailTo(coherentShiftApplication.getEmployee()
					.getEmail());

			coherentTimesheetMailLogic
					.sendAcceptRejectMailForTimesheet(
							companyId,
							emailDataDTO,
							PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_REJECT,
							pendingOTTimesheetForm.getRemarks());
		}
		return response;
	}

	@Override
	public PendingOTTimesheetForm forwardShift(
			PendingOTTimesheetForm pendingOtTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessfullyFor = false;
		CoherentShiftApplication coherentShiftApplicationVO = null;
		CoherentShiftApplicationReviewer coherentOTTimesheetReviewer2 = null;
		Employee employee = employeeDAO.findById(employeeId);
		Date date = new Date();
		CoherentShiftApplicationReviewer coherentOvertimeApplicationReviewer = coherentShiftApplicationReviewerDAO
				.findByCondition(pendingOtTimesheetForm.getOtTimesheetId(),
						pendingOtTimesheetForm.getOtTimesheetReviewerId());
		String workflowLevel = String
				.valueOf(coherentShiftApplicationReviewerDAO
						.getOTTimesheetReviewerCount(coherentOvertimeApplicationReviewer
								.getCoherentShiftApplication()
								.getShiftApplicationID()));
		if (workflowLevel != null
				&& coherentOvertimeApplicationReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equalsIgnoreCase(workflowLevel)) {
			response = acceptShift(pendingOtTimesheetForm, employeeId,
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

				coherentShiftApplicationVO = coherentOvertimeApplicationReviewer
						.getCoherentShiftApplication();

				// Validating claim application item.
				
				TimesheetStatusMaster otStatusCompleted = null;
				if (coherentOvertimeApplicationReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equalsIgnoreCase(workflowLevel)) {
					otStatusCompleted = lundinTimesheetStatusMasterDAO
							.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
					coherentShiftApplicationVO
							.setTimesheetStatusMaster(otStatusCompleted);
				}
				coherentShiftApplicationVO.setUpdatedDate(new Timestamp(date
						.getTime()));
				coherentShiftApplicationDAO.update(coherentShiftApplicationVO);

				int nextWorkFlowRuleValueLevel = Integer
						.valueOf(coherentOvertimeApplicationReviewer
								.getWorkFlowRuleMaster().getRuleValue()) + 1;
				for (CoherentShiftApplicationReviewer applicationReviewer : coherentShiftApplicationVO
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

				coherentOvertimeApplicationReviewer.setPending(false);
				coherentOvertimeApplicationReviewer
						.setEmployeeReviewer(employee);
				coherentShiftApplicationReviewerDAO
						.update(coherentOvertimeApplicationReviewer);

				applicationWorkflow.setCreatedBy(employee);
				applicationWorkflow
						.setCoherentShiftApplication(coherentOvertimeApplicationReviewer
								.getCoherentShiftApplication());
				applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
				applicationWorkflow.setRemarks(pendingOtTimesheetForm
						.getRemarks());
				applicationWorkflow
						.setCreatedDate(new Timestamp(date.getTime()));
				coherentShiftApplicationWorkflowDAO
						.save(applicationWorkflow);
				isSuccessfullyFor = true;
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
			}
			if (isSuccessfullyFor) {
				EmailDataDTO emailDataDTO = new EmailDataDTO();
				emailDataDTO.setTimesheetId(coherentShiftApplicationVO
						.getShiftApplicationID());
				emailDataDTO
						.setEmployeeName(getEmployeeName(coherentShiftApplicationVO
								.getEmployee()));
				emailDataDTO.setEmployeeNumber(coherentShiftApplicationVO
						.getEmployee().getEmployeeNumber());
				emailDataDTO.setEmpCompanyId(coherentShiftApplicationVO
						.getEmployee().getCompany().getCompanyId());
				emailDataDTO.setBatchDesc(coherentShiftApplicationVO
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

	@Override
	public boolean saveAsDraftTimesheet(long shiftApplicationId,
			long employeeId, Long companyId) {
		CoherentShiftApplication otTimesheet = coherentShiftApplicationDAO
				.findById(shiftApplicationId);
		try {
			TimesheetStatusMaster otStatusMaster = timesheetStatusMasterDAO
					.findByName(PayAsiaConstants.LUNDIN_STATUS_DRAFT);
			otTimesheet.setTimesheetStatusMaster(otStatusMaster);
			coherentShiftApplicationDAO.update(otTimesheet);

			for (CoherentShiftApplicationReviewer otTsheetReviewer : otTimesheet
					.getCoherentShiftApplicationReviewer()) {
				coherentShiftApplicationReviewerDAO.delete(otTsheetReviewer);
			}

			for (CoherentShiftApplicationWorkflow workflow : otTimesheet
					.getCoherentShiftApplicationWorkflows()) {
				coherentShiftApplicationWorkflowDAO.delete(workflow);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public OTPendingTimesheetForm getTimesheetWorkflowHistory(Long otTimesheetId) {
		OTPendingTimesheetForm otMyRequestForm = new OTPendingTimesheetForm();
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		CoherentShiftApplication coherentOvertimeApplication = coherentShiftApplicationDAO
				.findByCompanyShiftId(otTimesheetId,companyId);
		if (coherentOvertimeApplication!=null)
		{
		otMyRequestForm
				.setCreatedBy(getEmployeeNameWithNumber(coherentOvertimeApplication
						.getEmployee()));

		otMyRequestForm
				.setCreatedDate(DateUtils
						.timeStampToStringWithTime(new ArrayList<CoherentShiftApplicationReviewer>(
								coherentOvertimeApplication
										.getCoherentShiftApplicationReviewer())
								.get(0).getCreatedDate()));
		List<CoherentShiftApplicationWorkflow> applicationWorkflows = new ArrayList<>(
				coherentOvertimeApplication
						.getCoherentShiftApplicationWorkflows());
		Collections.sort(applicationWorkflows, new OTTimesheetWorkFlowComp());
		Integer workFlowCount = 0;

		if (coherentOvertimeApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_WITHDRAWN)) {
			otMyRequestForm.setUserStatus(PayAsiaConstants.OT_STATUS_WITHDRAWN);
		} else {
			otMyRequestForm.setUserStatus(PayAsiaConstants.OT_STATUS_SUBMITTED);

		}

		for (CoherentShiftApplicationReviewer coherentApplicationReviewer : coherentOvertimeApplication
				.getCoherentShiftApplicationReviewer()) {

			if (coherentApplicationReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("1")) {
				otMyRequestForm
						.setOtTimesheetReviewer1(getEmployeeNameWithNumber(coherentApplicationReviewer
								.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer1Id(coherentApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
			}

			if (coherentApplicationReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("2")) {
				otMyRequestForm
						.setOtTimesheetReviewer2(getEmployeeNameWithNumber(coherentApplicationReviewer
								.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer2Id(coherentApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
			}
			if (coherentApplicationReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("3")) {
				otMyRequestForm
						.setOtTimesheetReviewer3(getEmployeeNameWithNumber(coherentApplicationReviewer
								.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer3Id(coherentApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
			}
		}

		otMyRequestForm.setTotalNoOfReviewers(coherentOvertimeApplication
				.getCoherentShiftApplicationReviewer().size());
		List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms = new ArrayList<>();
		for (CoherentShiftApplicationWorkflow coherentApplicationWorkflow : applicationWorkflows) {

			OTTimesheetWorkflowForm otTimesheetWorkflowForm = new OTTimesheetWorkflowForm();
			otTimesheetWorkflowForm.setRemarks(coherentApplicationWorkflow
					.getRemarks());
			otTimesheetWorkflowForm.setStatus(coherentApplicationWorkflow
					.getTimesheetStatusMaster().getTimesheetStatusName());
			otTimesheetWorkflowForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(coherentApplicationWorkflow
							.getCreatedDate()));
			otTimesheetWorkflowForm.setOrder(workFlowCount);

			otTimesheetWorkflowForms.add(otTimesheetWorkflowForm);
			workFlowCount++;
		}
		otMyRequestForm.setOtTimesheetWorkflowForms(otTimesheetWorkflowForms);
		}
		return otMyRequestForm;
	}

	private class OTTimesheetWorkFlowComp implements
			Comparator<CoherentShiftApplicationWorkflow> {

		@Override
		public int compare(CoherentShiftApplicationWorkflow templateField,
				CoherentShiftApplicationWorkflow compWithTemplateField) {
			if (templateField.getShiftApplicationWorkflowID() > compWithTemplateField
					.getShiftApplicationWorkflowID()) {
				return 1;
			} else if (templateField.getShiftApplicationWorkflowID() < compWithTemplateField
					.getShiftApplicationWorkflowID()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public DataExportForm generateShiftExcel(Long timesheetId) {

		CoherentShiftApplication coherentShiftApplication = coherentShiftApplicationDAO
				.findById(timesheetId);
		TimesheetBatch timesheetBatch = timesheetBatchDAO
				.findById(coherentShiftApplication.getTimesheetBatch()
						.getTimesheetBatchId());

		String approvalSteps = "";
		if (!coherentShiftApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
			List<CoherentShiftApplicationReviewer> applicationReviewers = new ArrayList<>(
					coherentShiftApplication
							.getCoherentShiftApplicationReviewer());
			Collections.sort(applicationReviewers, new ClaimReviewerComp());

			for (CoherentShiftApplicationReviewer applicationReviewer : applicationReviewers) {
				approvalSteps += getEmployeeName(applicationReviewer
						.getEmployeeReviewer()) + " -> ";
			}
			if (approvalSteps.endsWith(" -> ")) {
				approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
			}
			// jsonObject.put("approvalSteps", approvalSteps);
		}
		if (coherentShiftApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
			List<EmployeeTimesheetReviewer> applicationReviewers = new ArrayList<>(
					employeeTimesheetReviewerDAO.findByCondition(
							coherentShiftApplication.getEmployee()
									.getEmployeeId(), coherentShiftApplication
									.getCompany().getCompanyId()));
			Collections.sort(applicationReviewers, new EmployeeReviewerComp());

			for (EmployeeTimesheetReviewer applicationReviewer : applicationReviewers) {
				approvalSteps += getEmployeeName(applicationReviewer
						.getEmployeeReviewer()) + " -> ";
			}
			if (approvalSteps.endsWith(" -> ")) {
				approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
			}
			// jsonObject.put("approvalSteps", approvalSteps);

		}

		Company companyVO = companyDAO.findById(coherentShiftApplication
				.getCompany().getCompanyId());

		List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic
				.getEmpDynFieldsValueList(coherentShiftApplication.getCompany()
						.getCompanyId(), companyVO.getDateFormat(),
						coherentShiftApplication.getEmployee().getEmployeeId());

		String department = "";
		String costCentre = "";
		for (Object[] deptObject : deptCostCentreEmpObjectList) {

			if (deptObject != null
					&& deptObject[3] != null
					&& deptObject[3].equals(coherentShiftApplication
							.getEmployee().getEmployeeNumber())) {
				department = String.valueOf(deptObject[0]);

				if (deptObject[0] != null) {
					department = String.valueOf(deptObject[0]);
				} else {
					department = "";
				}

				if (deptObject[2] != null) {
					costCentre = String.valueOf(deptObject[2]);
				} else {
					costCentre = "";
				}
			}
		}

		String companyDateFormat = companyDAO.findById(
				coherentShiftApplication.getCompany().getCompanyId())
				.getDateFormat();

		DataExportForm exportForm = new DataExportForm();
		String fileName = coherentShiftApplication.getEmployee()
				.getEmployeeNumber()
				+ "_"
				+ timesheetBatch.getTimesheetBatchDesc();

		if (StringUtils.isBlank(fileName)) {
			fileName = PayAsiaConstants.EXPORT_FILE_DEFAULT_TEMPLATE_NAME;
		}
		exportForm.setFinalFileName(fileName);
		Workbook wb = new HSSFWorkbook();

		Font font = wb.createFont();// Create font
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		HSSFFont font1 = (HSSFFont) wb.createFont();
		// Font font1 = wb.createFont();
		font1.setColor(HSSFColor.RED.index);
		// style.setFont(font);

		CellStyle bold1 = wb.createCellStyle();
		bold1.setFont(font1);

		CellStyle bold = wb.createCellStyle();
		bold.setFont(font);

		CellStyle allBorder = wb.createCellStyle();
		allBorder.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		allBorder.setBorderTop(HSSFCellStyle.BORDER_THIN);
		allBorder.setBorderRight(HSSFCellStyle.BORDER_THIN);
		allBorder.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		CellStyle allBorderRight = wb.createCellStyle();
		allBorderRight.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		allBorderRight.setBorderTop(HSSFCellStyle.BORDER_THIN);
		allBorderRight.setBorderRight(HSSFCellStyle.BORDER_THIN);
		allBorderRight.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		allBorderRight.setAlignment(CellStyle.ALIGN_RIGHT);

		CellStyle allBorderLeft = wb.createCellStyle();
		allBorderLeft.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		allBorderLeft.setBorderTop(HSSFCellStyle.BORDER_THIN);
		allBorderLeft.setBorderRight(HSSFCellStyle.BORDER_THIN);
		allBorderLeft.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		allBorderLeft.setAlignment(CellStyle.ALIGN_LEFT);

		HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
		palette.setColorAtIndex((short) 57, (byte) 83, (byte) 141, (byte) 213);

		CellStyle allBorderVoiletBackg = wb.createCellStyle();
		allBorderVoiletBackg.setFillForegroundColor(palette.getColor(57)
				.getIndex());
		allBorderVoiletBackg.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		allBorderVoiletBackg.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		allBorderVoiletBackg.setBorderTop(HSSFCellStyle.BORDER_THIN);
		allBorderVoiletBackg.setBorderRight(HSSFCellStyle.BORDER_THIN);
		allBorderVoiletBackg.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		Sheet sheet = wb.createSheet("Sheet1");

		int rowId = 0;

		Row row1 = sheet.createRow(rowId);
		Cell cell11 = row1.createCell(0);
		cell11.setCellStyle(bold);
		Cell cell12 = row1.createCell(1);
		Cell cell13 = row1.createCell(3);
		cell13.setCellStyle(bold);
		Cell cell14 = row1.createCell(4);
		Cell cell15 = row1.createCell(6);
		cell15.setCellStyle(bold);
		Cell cell16 = row1.createCell(7);

		cell11.setCellValue("Employee ID");
		cell12.setCellValue(coherentShiftApplication.getEmployee()
				.getEmployeeNumber());
		cell13.setCellValue("Claim Month");

		cell14.setCellValue(coherentShiftApplication.getTimesheetBatch()
				.getTimesheetBatchDesc());

		cell15.setCellValue("");
		cell16.setCellValue("");

		rowId = rowId + 2;
		Row row2 = sheet.createRow(rowId);
		Cell cell21 = row2.createCell(0);
		cell21.setCellStyle(bold);
		Cell cell22 = row2.createCell(1);
		Cell cell23 = row2.createCell(3);
		cell23.setCellStyle(bold);
		Cell cell24 = row2.createCell(4);

		cell21.setCellValue("Name");
		cell22.setCellValue(getEmployeeName(coherentShiftApplication
				.getEmployee()));
		cell23.setCellValue("Approval Steps");
		cell24.setCellValue(approvalSteps);

		rowId = rowId + 2;
		Row row4 = sheet.createRow(rowId);
		Cell cell41 = row4.createCell(0);
		cell41.setCellStyle(bold);
		Cell cell42 = row4.createCell(1);
		Cell cell43 = row4.createCell(3);
		cell43.setCellStyle(bold);
		Cell cell44 = row4.createCell(4);

		cell41.setCellValue("Cost Centre");
		cell42.setCellValue(costCentre);
		cell43.setCellValue("Department");
		cell44.setCellValue(department);

		rowId = rowId + 4;
		Row row3 = sheet.createRow(rowId);
		Cell cell31 = row3.createCell(0);
		cell31.setCellStyle(allBorderVoiletBackg);

		Cell cell32 = row3.createCell(1);
		cell32.setCellStyle(allBorderVoiletBackg);

		Cell cell35 = row3.createCell(2);
		cell35.setCellStyle(allBorderVoiletBackg);

		Cell cell33 = row3.createCell(3);
		cell33.setCellStyle(allBorderVoiletBackg);

		Cell cell34 = row3.createCell(4);
		cell34.setCellStyle(allBorderVoiletBackg);

		cell31.setCellValue("Date");
		cell32.setCellValue("Day");
		cell35.setCellValue("Shift Type");
		cell33.setCellValue("Shift");
		cell34.setCellValue("Remarks");

		List<CoherentShiftApplicationDetail> coherentShiftApplicationDetails = new ArrayList<CoherentShiftApplicationDetail>();
		coherentShiftApplicationDetails = coherentShiftApplicationDAO
				.getCoherentShiftApplicationDetails(timesheetId);

		rowId++;
		for (CoherentShiftApplicationDetail lionEmployeeTimesheetApplicationDetail : coherentShiftApplicationDetails) {

			Date timesheetDate1 = new Date(
					lionEmployeeTimesheetApplicationDetail.getShiftDate()
							.getTime());
			List<String> holidayList = getHolidaysFor(coherentShiftApplication
					.getEmployee().getEmployeeId(), timesheetDate1,
					timesheetDate1);

			Row rowTable = sheet.createRow(rowId);
			Cell cellT1 = rowTable.createCell(0);

			if (holidayList.size() > 0) {
				cellT1.setCellStyle(allBorderLeft);
				cellT1.setCellStyle(bold1);
			} else {
				cellT1.setCellStyle(allBorderLeft);
			}

			Cell cellT2 = rowTable.createCell(1);

			if (holidayList.size() > 0) {
				cellT2.setCellStyle(allBorderLeft);
				cellT2.setCellStyle(bold1);
			} else {
				cellT2.setCellStyle(allBorderLeft);
			}
			Cell cellT5 = rowTable.createCell(2);
			cellT5.setCellStyle(allBorderRight);

			Cell cellT3 = rowTable.createCell(3);
			cellT3.setCellStyle(allBorderRight);

			Cell cellT4 = rowTable.createCell(4);
			cellT4.setCellStyle(allBorderLeft);

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					companyDateFormat);

			Timestamp timesheetDate = lionEmployeeTimesheetApplicationDetail
					.getShiftDate();

			cellT1.setCellValue(simpleDateFormat.format(timesheetDate));

			java.util.GregorianCalendar cal = (GregorianCalendar) Calendar
					.getInstance();
			cal.setTime(timesheetDate);

			SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

			cellT2.setCellValue(format2
					.format(lionEmployeeTimesheetApplicationDetail
							.getShiftDate()));

			if (lionEmployeeTimesheetApplicationDetail.isShiftTypeChanged()) {
				if (lionEmployeeTimesheetApplicationDetail.getShiftType() != null) {
					cellT5.setCellValue(lionEmployeeTimesheetApplicationDetail
							.getShiftType().getCodeDesc());
					cellT5.setCellStyle(allBorderLeft);
					cellT5.setCellStyle(bold1);
				} else {
					cellT5.setCellValue("NULL");
					cellT5.setCellStyle(allBorderLeft);
					cellT5.setCellStyle(bold1);
				}

			} else {
				if (lionEmployeeTimesheetApplicationDetail.getShiftType() != null) {
					cellT5.setCellValue(lionEmployeeTimesheetApplicationDetail
							.getShiftType().getCodeDesc());
					cellT5.setCellStyle(allBorderLeft);
				} else {
					cellT5.setCellStyle(allBorderLeft);
					cellT5.setCellValue("NULL");
				}
			}

			if (lionEmployeeTimesheetApplicationDetail.isShiftChanged()) {

				if (lionEmployeeTimesheetApplicationDetail.isShift()) {
					cellT3.setCellValue("Yes");
					cellT3.setCellStyle(allBorderLeft);
					cellT3.setCellStyle(bold1);
				} else {
					cellT3.setCellValue("No");
					cellT3.setCellStyle(allBorderLeft);
					cellT3.setCellStyle(bold1);
				}

			} else {
				if (lionEmployeeTimesheetApplicationDetail.isShift()) {
					cellT3.setCellValue("Yes");
					cellT3.setCellStyle(allBorderLeft);
				} else {
					cellT3.setCellStyle(allBorderLeft);
					cellT3.setCellValue("No");
				}
			}

			cellT4.setCellValue(lionEmployeeTimesheetApplicationDetail
					.getRemarks());

			rowId++;

		}

		Row row2L = sheet.createRow(rowId);
		Cell cell2L1 = row2L.createCell(2);
		cell2L1.setCellStyle(allBorderRight);
		Cell cell2L2 = row2L.createCell(3);
		cell2L2.setCellStyle(allBorderRight);

		rowId++;

		cell2L1.setCellValue("TOTALS");
		cell2L2.setCellValue(coherentShiftApplication.getTotalShifts());
		if (!coherentShiftApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
			rowId = rowId + 4;

			Row row2R = sheet.createRow(rowId);
			Cell cell2R1 = row2R.createCell(0);
			cell2R1.setCellStyle(allBorderVoiletBackg);
			Cell cell2R2 = row2R.createCell(1);
			cell2R2.setCellStyle(allBorderVoiletBackg);

			Cell cell2R3 = row2R.createCell(2);
			cell2R3.setCellStyle(allBorderVoiletBackg);

			Cell cell2R4 = row2R.createCell(3);
			cell2R4.setCellStyle(allBorderVoiletBackg);

			Cell cell2R5 = row2R.createCell(4);
			cell2R5.setCellStyle(allBorderVoiletBackg);

			rowId++;
			cell2R1.setCellValue("Wokflow Role");
			cell2R2.setCellValue("Name");
			cell2R3.setCellValue("Remarks");
			cell2R4.setCellValue("Status");
			cell2R5.setCellValue("Date");

			OTPendingTimesheetForm form = getTimesheetWorkflowHistory(timesheetId);

			List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms = form
					.getOtTimesheetWorkflowForms();

			// ////////////////
			boolean isCompleted = false;
			for (OTTimesheetWorkflowForm otTimesheetWorkflowForm : otTimesheetWorkflowForms) {
				if (otTimesheetWorkflowForm.getStatus().equalsIgnoreCase(
						"Completed")) {
					isCompleted = true;
				}
			}

			for (int i = 0; i <= form.getTotalNoOfReviewers(); i++) {

				Row row2RL = sheet.createRow(rowId);
				Cell cellR1 = row2RL.createCell(0);
				cellR1.setCellStyle(allBorder);
				Cell cellR2 = row2RL.createCell(1);
				cellR2.setCellStyle(allBorder);

				Cell cellR3 = row2RL.createCell(2);
				cellR3.setCellStyle(allBorder);
				Cell cellR4 = row2RL.createCell(3);
				cellR4.setCellStyle(allBorder);
				Cell cellR5 = row2RL.createCell(4);
				cellR5.setCellStyle(allBorder);

				if (i == 0) {
					cellR1.setCellValue("User");
					cellR2.setCellValue(form.getCreatedBy());
				}

				if (i == 1) {
					cellR1.setCellValue("Reviewer 1");
					cellR2.setCellValue(form.getOtTimesheetReviewer1());
				}

				if (i == 2) {
					cellR1.setCellValue("Reviewer 2");
					cellR2.setCellValue(form.getOtTimesheetReviewer2());
				}

				if (i == 3) {
					cellR1.setCellValue("Reviewer 3");
					cellR2.setCellValue(form.getOtTimesheetReviewer3());
				}

				if (otTimesheetWorkflowForms.size() > i) {

					cellR3.setCellValue(otTimesheetWorkflowForms.get(i)
							.getRemarks());
					cellR4.setCellValue(otTimesheetWorkflowForms.get(i)
							.getStatus());
					cellR5.setCellValue(otTimesheetWorkflowForms.get(i)
							.getCreatedDate());
				} else {

					if (!isCompleted) {
						cellR4.setCellValue("Pending");
					}
				}

				rowId++;
			}
		}

		// ////////////

		for (int columnIndex = 0; columnIndex < 10; columnIndex++) {
			sheet.autoSizeColumn(columnIndex);
		}

		exportForm.setFinalFileName(fileName);

		exportForm.setWorkbook(wb);

		return exportForm;
	}

	@Override
	public TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId,
			Long employeeId, Long timesheetId,
			boolean hasCoherentTimesheetModule) {
		TimesheetFormPdfDTO timesheetFormPdfDTO = new TimesheetFormPdfDTO();

		CoherentShiftApplication coherentOvertimeApplication = coherentShiftApplicationDAO
				.findById(timesheetId);
		timesheetFormPdfDTO.setTimesheetBatchDesc(coherentOvertimeApplication
				.getTimesheetBatch().getTimesheetBatchDesc());
		timesheetFormPdfDTO.setEmployeeNumber(coherentOvertimeApplication
				.getEmployee().getEmployeeNumber());
		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				timesheetFormPdfDTO
						.setTimesheetPdfByteFile(generateTimesheetFormPDF(
								companyId, employeeId, timesheetId,
								hasCoherentTimesheetModule));
				return timesheetFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			try {
				timesheetFormPdfDTO
						.setTimesheetPdfByteFile(generateTimesheetFormPDF(
								companyId, employeeId, timesheetId,
								hasCoherentTimesheetModule));
				return timesheetFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}
	}

	private byte[] generateTimesheetFormPDF(Long companyId, Long employeeId,
			Long timesheetId, boolean hasLundinTimesheetModule)
			throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId,
				PAYASIA_TEMP_PATH, "Timesheet");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PageSize.A4.rotate(), 88f, 88f, 10f, 10f);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

			document.open();

			PdfPTable lundinTimesheetPdfTable = coherentShiftPrintPDFLogic
					.createShiftPdf(document, writer, 1, companyId,
							timesheetId, hasLundinTimesheetModule);

			document.add(lundinTimesheetPdfTable);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(
					PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow
					| PdfWriter.CenterWindow);

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

}
