package com.payasia.logic.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.CompanyDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveCustomFieldDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSchemeDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.LeaveTypeDTO;
import com.payasia.common.dto.YearEndProcessDTO;
import com.payasia.common.dto.YearEndProcessingConditionDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.LeaveEventReminderForm;
import com.payasia.common.form.LeaveGranterForm;
import com.payasia.common.form.LeaveYearEndEmployeeDetailForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.PendingItemsFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.YearEndProcessFilterForm;
import com.payasia.common.form.YearEndProcessForm;
import com.payasia.common.form.YearEndProcessingForm;
import com.payasia.common.form.YearEndProcessingFormResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeHistoryDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.LeaveApplicationWorkflowDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveSessionMasterDAO;
import com.payasia.dao.LeaveStatusMasterDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.LeaveYearEndBatchDAO;
import com.payasia.dao.LeaveYearEndEmployeeDetailDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.YearEndProcessScheduleDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeTypeHistory;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.dao.bean.LeaveApplicationCustomField;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSessionMaster;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveYearEndBatch;
import com.payasia.dao.bean.LeaveYearEndEmployeeDetail;
import com.payasia.dao.bean.YearEndProcessSchedule;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.LeaveSchemeLogic;
import com.payasia.logic.LeaveTypeLogic;
import com.payasia.logic.YearEndProcessingLogic;

@Component
public class YearEndProcessingLogicImpl implements YearEndProcessingLogic {
	private static final Logger LOGGER = Logger
			.getLogger(YearEndProcessingLogicImpl.class);
	@Resource
	LeaveSchemeDAO leaveSchemeDAO;
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;
	@Resource
	LeaveStatusMasterDAO leaveStatusMasterDAO;
	@Resource
	LeaveApplicationDAO leaveApplicationDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	AddLeaveLogic addLeaveLogic;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewerDAO;
	@Resource
	LeaveSessionMasterDAO leaveSessionMasterDAO;
	@Resource
	LeaveApplicationWorkflowDAO applicationWorkflowDAO;
	@Resource
	EmployeeLeaveSchemeTypeHistoryDAO employeeLeaveSchemeTypeHistoryDAO;
	@Resource
	GeneralMailLogic generalMailLogic;
	@Resource
	LeaveYearEndEmployeeDetailDAO leaveYearEndEmployeeDetailDAO;
	@Resource
	LeaveYearEndBatchDAO leaveYearEndBatchDAO;

	@Resource
	CompanyGroupDAO companyGroupDAO;

	@Resource
	YearEndProcessScheduleDAO yearEndProcessScheduleDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;

	@Resource
	LeaveSchemeLogic leaveSchemeLogic;

	@Resource
	LeaveTypeLogic leaveTypeLogic;

	@Resource
	CompanyInformationLogic companyInformationLogic;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;

	@Override
	public List<LeaveGranterForm> getLeaveType(Long companyId) {
		List<LeaveGranterForm> leaveGranterFormList = new ArrayList<>();
		List<LeaveSchemeType> leaveTypeList = leaveSchemeTypeDAO
				.findByCompany(companyId);

		Set<LeaveTypeMaster> leaveTypeMasterSet = new LinkedHashSet<>();
		for (LeaveSchemeType leaveType : leaveTypeList) {
			leaveTypeMasterSet.add(leaveType.getLeaveTypeMaster());
		}
		for (LeaveTypeMaster leaveType : leaveTypeMasterSet) {
			LeaveGranterForm leaveGranterForm = new LeaveGranterForm();
			leaveGranterForm.setLeaveType(leaveType.getLeaveTypeName());
			leaveGranterForm.setLeaveTypeId(leaveType.getLeaveTypeId());
			leaveGranterFormList.add(leaveGranterForm);
		}

		return leaveGranterFormList;
	}

	@Override
	public YearEndProcessingFormResponse getYEPLeaveTypeList(
			SortCondition sortDTO, Long companyId, Long leaveSchemeId) {
		YearEndProcessingFormResponse response = new YearEndProcessingFormResponse();
		List<LeaveSchemeType> leaveSchemeTypes = leaveSchemeTypeDAO
				.findByConditionLeaveSchemeIdCompanyId(leaveSchemeId, companyId);

		List<String> leaveStatusList = new ArrayList<>();
		leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

		List<YearEndProcessingForm> leaveTypesList = new ArrayList<>();
		for (LeaveSchemeType leaveSchemeType : leaveSchemeTypes) {
			YearEndProcessingForm leaveTypeForm = new YearEndProcessingForm();
			leaveTypeForm.setLeaveType(leaveSchemeType.getLeaveTypeMaster()
					.getLeaveTypeName());
			leaveTypeForm.setLeaveTypeId(leaveSchemeType.getLeaveTypeMaster()
					.getLeaveTypeId());

			Integer empCount = leaveApplicationDAO.getCountPendingLeaveAppl(
					leaveStatusList, leaveSchemeType.getLeaveTypeMaster()
							.getLeaveTypeId(), leaveSchemeType.getLeaveScheme()
							.getLeaveSchemeId(), companyId);

			StringBuilder empCountBuilder = new StringBuilder();
			empCountBuilder
					.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'reloadPendingLeavesGrid("
							+ leaveSchemeType.getLeaveTypeMaster()
									.getLeaveTypeId()
							+ ")'>"
							+ empCount
							+ "</a>");

			leaveTypeForm.setLeavesCount(String.valueOf(empCountBuilder));
			leaveTypesList.add(leaveTypeForm);
		}
		response.setYearEndProcessList(leaveTypesList);
		return response;
	}

	@Override
	public PendingItemsFormResponse getPendingLeaves(PageRequest pageDTO,
			SortCondition sortDTO, Long leaveTypeId, Long leaveSchemeId,
			Long companyId) {
		List<String> leaveStatusList = new ArrayList<>();
		leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

		List<LeaveApplication> leaveApplicationList = leaveApplicationDAO
				.getPendingLeaveApplEmps(leaveStatusList, leaveTypeId,
						leaveSchemeId, companyId);

		List<PendingItemsForm> pendingItemsForms = new ArrayList<PendingItemsForm>();
		for (LeaveApplication leaveApplication : leaveApplicationList) {
			PendingItemsForm pendingItemsForm = new PendingItemsForm();
			pendingItemsForm.setLeaveApplicationId(leaveApplication
					.getLeaveApplicationId());
			pendingItemsForm
					.setCreatedBy(getEmployeeNameWithNumber(leaveApplication
							.getEmployee()));
			pendingItemsForm.setDays(new BigDecimal(leaveApplication
					.getTotalDays()));

			Set<LeaveApplicationReviewer> LeaveApplicationReviewerSet = leaveApplication
					.getLeaveApplicationReviewers();
			for (LeaveApplicationReviewer LeaveApplicationReviewer : LeaveApplicationReviewerSet) {

				if (LeaveApplicationReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equals("1")) {
					pendingItemsForm
							.setLeaveAppReviewer1(getEmployeeNameWithNumber(LeaveApplicationReviewer
									.getEmployee()));
				}

				if (LeaveApplicationReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equals("2")) {
					pendingItemsForm
							.setLeaveAppReviewer2(getEmployeeNameWithNumber(LeaveApplicationReviewer
									.getEmployee()));
				}

				if (LeaveApplicationReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equals("3")) {
					pendingItemsForm
							.setLeaveAppReviewer3(getEmployeeNameWithNumber(LeaveApplicationReviewer
									.getEmployee()));
				}
			}

			pendingItemsForm.setCreatedDate(DateUtils
					.timeStampToString(leaveApplication.getCreatedDate()));
			pendingItemsForm.setFromDate(DateUtils
					.timeStampToString(leaveApplication.getStartDate()));
			pendingItemsForm.setToDate(DateUtils
					.timeStampToString(leaveApplication.getEndDate()));
			pendingItemsForms.add(pendingItemsForm);
		}

		PendingItemsFormResponse response = new PendingItemsFormResponse();
		response.setRows(pendingItemsForms);
		Integer recordSize = leaveApplicationDAO.getCountPendingLeaveAppl(
				leaveStatusList, leaveTypeId, leaveSchemeId, companyId);
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
	public List<AppCodeDTO> getWorkflowTypeList() {
		List<AppCodeDTO> workflowTypes = new ArrayList<AppCodeDTO>();
		List<AppCodeMaster> appCodes = appCodeMasterDAO
				.findByConditionPendingItems(PayAsiaConstants.APP_CODE_WORKFLOW_TYPE);
		for (AppCodeMaster appCodeMaster : appCodes) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setCodeValue(appCodeMaster.getCodeValue());
			appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			workflowTypes.add(appCodeDTO);
		}

		return workflowTypes;
	}

	@Override
	public PendingItemsForm getDataForLeaveReview(Long companyId,
			Long leaveApplicationId) {

		PendingItemsForm pendingItemsForm = new PendingItemsForm();
		LeaveApplication leaveApplication = leaveApplicationDAO
				.findById(leaveApplicationId);

		AddLeaveForm addLeaveForm = new AddLeaveForm();

		Set<LeaveApplicationCustomField> leaveApplicationCustomFields = leaveApplication
				.getLeaveApplicationCustomFields();
		List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = new ArrayList<>();
		for (LeaveApplicationCustomField leaveApplicationCustomField : leaveApplicationCustomFields) {

			LeaveCustomFieldDTO customField = new LeaveCustomFieldDTO();
			customField.setValue(leaveApplicationCustomField.getValue());
			customField.setCustomFieldId(leaveApplicationCustomField
					.getLeaveApplicationCustomFieldId());
			customField.setCustomFieldTypeId(leaveApplicationCustomField
					.getLeaveSchemeTypeCustomField().getCustomFieldId());
			customField.setCustomFieldName(leaveApplicationCustomField
					.getLeaveSchemeTypeCustomField().getFieldName());
			leaveCustomFieldDTOs.add(customField);
		}
		addLeaveForm.setCustomFieldDTOList(leaveCustomFieldDTOs);

		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication
				.getStartDate()));
		leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication
				.getEndDate()));
		leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1()
				.getLeaveSessionId());
		leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2()
				.getLeaveSessionId());
		leaveDTO.setEmployeeLeaveSchemeTypeId(leaveApplication
				.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
		AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null, null, leaveDTO);
		AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());

		addLeaveForm.setLeaveBalance(leaveBalance.getLeaveBalance());
		addLeaveForm.setNoOfDays(noOfDays.getNoOfDays());

		addLeaveForm.setLeaveScheme(leaveApplication
				.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveScheme().getSchemeName());
		addLeaveForm.setLeaveType(leaveApplication.getEmployeeLeaveSchemeType()
				.getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
		addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication
				.getStartDate()));
		addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication
				.getEndDate()));
		addLeaveForm.setLeaveApplicationId(leaveApplication
				.getLeaveApplicationId());
		addLeaveForm.setReason(leaveApplication.getReason());
		addLeaveForm.setStatus(leaveApplication.getLeaveStatusMaster()
				.getLeaveStatusName());
		addLeaveForm.setStatusId(leaveApplication.getLeaveStatusMaster()
				.getLeaveStatusID());

		if (leaveApplication.getLeaveSessionMaster1() != null) {
			addLeaveForm.setFromSession(leaveApplication
					.getLeaveSessionMaster1().getSession());
			addLeaveForm.setFromSessionId(leaveApplication
					.getLeaveSessionMaster1().getLeaveSessionId());
		}

		if (leaveApplication.getLeaveSessionMaster2() != null) {
			addLeaveForm.setToSession(leaveApplication.getLeaveSessionMaster2()
					.getSession());
			addLeaveForm.setToSessionId(leaveApplication
					.getLeaveSessionMaster2().getLeaveSessionId());
		}

		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : leaveApplication
				.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveSchemeTypeWorkflows()) {
			if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
				allowOverride = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();

			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
				allowReject = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD)) {
				allowForward = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
				allowApprove = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			}

		}
		int reviewOrder = 0;
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplication
				.getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {
				addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee()
						.getEmail());
				addLeaveForm
						.setLeaveReviewer1(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setApplyToId(leaveApplicationReviewer
						.getEmployee().getEmployeeId());

				reviewOrder = 1;

				pendingItemsForm.setCanReject(true);

				pendingItemsForm.setCanApprove(true);

			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {
				addLeaveForm
						.setLeaveReviewer2(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());

				reviewOrder = 2;

				pendingItemsForm.setCanReject(true);

				pendingItemsForm.setCanApprove(true);

			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				addLeaveForm
						.setLeaveReviewer3(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());

				reviewOrder = 2;

				pendingItemsForm.setCanReject(true);

				pendingItemsForm.setCanApprove(true);

			}
		}

		List<LeaveApplicationAttachmentDTO> leaveApplicationAttachmentDTOs = new ArrayList<LeaveApplicationAttachmentDTO>();

		for (LeaveApplicationAttachment leaveApplicationAttachment : leaveApplication
				.getLeaveApplicationAttachments()) {
			LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
			applicationAttachmentDTO.setFileName(leaveApplicationAttachment
					.getFileName());
			applicationAttachmentDTO
					.setLeaveApplicationId(leaveApplicationAttachment
							.getLeaveApplicationAttachmentId());
			leaveApplicationAttachmentDTOs.add(applicationAttachmentDTO);
		}

		addLeaveForm.setAttachmentList(leaveApplicationAttachmentDTOs);
		addLeaveForm.setTotalNoOfReviewers(leaveApplication
				.getLeaveApplicationReviewers().size());

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<LeaveApplicationWorkflowDTO>();
		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplication
				.getLeaveApplicationWorkflows()) {

			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();
			applicationWorkflowDTO.setCreatedDate(DateUtils
					.timeStampToString(leaveApplicationWorkflow
							.getCreatedDate()));
			applicationWorkflowDTO
					.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationWorkflow
							.getEmployee()));
			applicationWorkflowDTO.setRemarks(leaveApplicationWorkflow
					.getRemarks());
			applicationWorkflowDTO.setStatus(leaveApplicationWorkflow
					.getLeaveStatusMaster().getLeaveStatusName());
			applicationWorkflowDTO.setCreatedDate(DateUtils
					.timeStampToString(leaveApplicationWorkflow
							.getCreatedDate()));
			if (leaveApplicationWorkflow.getEmployee().getEmployeeId() == leaveApplication
					.getEmployee().getEmployeeId()) {

				applicationWorkflowDTO.setOrder(0);

			}

			if (addLeaveForm.getApplyToId() != null
					&& addLeaveForm.getApplyToId() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(1);
			}

			if (addLeaveForm.getLeaveReviewer2Id() != null
					&& addLeaveForm.getLeaveReviewer2Id() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(2);
			}

			if (addLeaveForm.getLeaveReviewer3Id() != null
					&& addLeaveForm.getLeaveReviewer3Id() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(3);
			}
			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		pendingItemsForm
				.setCreatedBy(getEmployeeNameWithNumber(leaveApplication
						.getEmployee()));
		pendingItemsForm.setCreatedById(leaveApplication.getEmployee()
				.getEmployeeId());
		if (reviewOrder == 1 && addLeaveForm.getLeaveReviewer2Id() != null) {
			Employee empReviewer2 = employeeDAO.findById(addLeaveForm
					.getLeaveReviewer2Id());
			pendingItemsForm
					.setForwardTo(getEmployeeNameWithNumber(empReviewer2));
			pendingItemsForm.setForwardToId(empReviewer2.getEmployeeId());
		} else if (reviewOrder == 2
				&& addLeaveForm.getLeaveReviewer3Id() != null) {
			Employee empReviewer3 = employeeDAO.findById(addLeaveForm
					.getLeaveReviewer3Id());
			pendingItemsForm
					.setForwardTo(getEmployeeNameWithNumber(empReviewer3));
			pendingItemsForm.setForwardToId(empReviewer3.getEmployeeId());
		} else {
			pendingItemsForm.setForwardTo("");
		}

		pendingItemsForm.setTypeOfApplication(generalLogic
				.getTypeOfApplication(leaveApplication));

		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		pendingItemsForm.setAddLeaveForm(addLeaveForm);
		return pendingItemsForm;
	}

	@Override
	public void acceptLeave(PendingItemsForm pendingItemsForm, Long employeeId,
			LeaveSessionDTO sessionDTO) {
		Date date = new Date();

		LeaveApplication leaveApplicationVO = leaveApplicationDAO
				.findById(pendingItemsForm.getLeaveApplicationReviewerId());
		Set<LeaveApplicationReviewer> leaveApplicationReviewerVOs = leaveApplicationVO
				.getLeaveApplicationReviewers();
		LeaveApplicationReviewer pendingLeaveApplicationreveiwer = null;
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationReviewerVOs) {
			if (leaveApplicationReviewer.getPending()) {
				pendingLeaveApplicationreveiwer = leaveApplicationReviewer;
			}

		}

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplicationVO.getCompany().getCompanyId());

		LeaveApplicationWorkflow applicationWorkflow = new LeaveApplicationWorkflow();
		Employee employee = employeeDAO.findById(employeeId);
		String workflowLevel = "";

		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		if (pendingItemsForm.isCanOverride()) {
			leaveApplicationVO.setStartDate(DateUtils
					.stringToTimestamp(pendingItemsForm.getFromDate()));
			leaveApplicationVO.setEndDate(DateUtils
					.stringToTimestamp(pendingItemsForm.getToDate()));

			if (pendingItemsForm.getFromSessionId() != null) {
				LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO
						.findById(pendingItemsForm.getFromSessionId());
				leaveApplicationVO.setLeaveSessionMaster1(leaveSessionMaster);
			}
			if (pendingItemsForm.getToSessionId() != null) {
				LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO
						.findById(pendingItemsForm.getToSessionId());
				leaveApplicationVO.setLeaveSessionMaster2(leaveSessionMaster);
			}

		}
		leaveApplicationVO.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplicationVO.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplicationDAO.update(leaveApplicationVO);

		pendingLeaveApplicationreveiwer.setPending(false);
		pendingLeaveApplicationreveiwer.setEmployee(employee);
		leaveApplicationReviewerDAO.update(pendingLeaveApplicationreveiwer);

		applicationWorkflow.setEmployee(employee);
		applicationWorkflow.setEmailCC(pendingItemsForm.getEmailCC());
		applicationWorkflow.setLeaveApplication(pendingLeaveApplicationreveiwer
				.getLeaveApplication());
		applicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
		applicationWorkflow.setRemarks(pendingItemsForm.getRemarks());

		if (pendingItemsForm.isCanOverride()) {
			applicationWorkflow.setStartDate(DateUtils
					.stringToTimestamp(pendingItemsForm.getFromDate()));
			applicationWorkflow.setEndDate(DateUtils
					.stringToTimestamp(pendingItemsForm.getToDate()));
		} else {
			applicationWorkflow.setStartDate(pendingLeaveApplicationreveiwer
					.getLeaveApplication().getStartDate());
			applicationWorkflow.setEndDate(pendingLeaveApplicationreveiwer
					.getLeaveApplication().getEndDate());
		}

		applicationWorkflow
				.setStartSessionMaster(pendingLeaveApplicationreveiwer
						.getLeaveApplication().getLeaveSessionMaster1());
		applicationWorkflow.setEndSessionMaster(pendingLeaveApplicationreveiwer
				.getLeaveApplication().getLeaveSessionMaster2());
		applicationWorkflow.setTotalDays(pendingLeaveApplicationreveiwer
				.getLeaveApplication().getTotalDays());

		applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		LeaveApplicationWorkflow leaveApplicationWorkflow = applicationWorkflowDAO
				.saveReturn(applicationWorkflow);

		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplicationVO
				.getEmployeeLeaveSchemeType();

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplicationVO
					.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplicationVO
					.getEndDate()));
			leaveDTO.setSession1(leaveApplicationVO.getLeaveSessionMaster1()
					.getLeaveSessionId());
			leaveDTO.setSession2(leaveApplicationVO.getLeaveSessionMaster2()
					.getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType
					.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null, employeeId,
					leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(leaveApplicationVO.getTotalDays())
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null, null,
				leaveApplicationVO.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());

		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
		employeeLeaveSchemeTypeHistory
				.setEmployeeLeaveSchemeType(leaveApplicationVO
						.getEmployeeLeaveSchemeType());
		AppCodeMaster appcodeTaken = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
				PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED);
		employeeLeaveSchemeTypeHistory.setAppCodeMaster(appcodeTaken);
		employeeLeaveSchemeTypeHistory.setLeaveApplication(leaveApplicationVO);
		LeaveStatusMaster leaveStatusForHistory = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		if (leaveApplicationVO.getLeaveCancelApplication() != null) {
			leaveStatusForHistory = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
		} else {
			leaveStatusForHistory = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}
		employeeLeaveSchemeTypeHistory
				.setLeaveStatusMaster(leaveStatusForHistory);
		employeeLeaveSchemeTypeHistory.setStartDate(leaveApplicationVO
				.getStartDate());
		employeeLeaveSchemeTypeHistory.setEndDate(leaveApplicationVO
				.getEndDate());
		employeeLeaveSchemeTypeHistory.setDays(BigDecimal
				.valueOf(leaveApplicationVO.getTotalDays()));
		employeeLeaveSchemeTypeHistory
				.setReason(leaveApplicationVO.getReason());
		employeeLeaveSchemeTypeHistory.setStartSessionMaster(leaveApplicationVO
				.getLeaveSessionMaster1());
		employeeLeaveSchemeTypeHistory.setEndSessionMaster(leaveApplicationVO
				.getLeaveSessionMaster2());
		employeeLeaveSchemeTypeHistoryDAO.save(employeeLeaveSchemeTypeHistory);

		if (leaveApplicationVO.getLeaveCancelApplication() != null) {

			LeaveApplication leaveApplicationToBeCancelled = leaveApplicationVO
					.getLeaveCancelApplication();
			LeaveStatusMaster cancelLeaveStatusMaster = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
			leaveApplicationToBeCancelled
					.setLeaveStatusMaster(cancelLeaveStatusMaster);
			leaveApplicationDAO.update(leaveApplicationToBeCancelled);

			generalMailLogic.sendAcceptRejectMailForLeave(
					leaveApplicationWorkflow.getLeaveApplication()
							.getEmployee().getCompany().getCompanyId(),
					leaveApplicationWorkflow,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_CANCEL_APPROVE,
					totalLeaveDays, leaveBalance.getLeaveBalance(), employee,
					pendingItemsForm.getRemarks(), sessionDTO, isLeaveUnitDays);

		} else {
			generalMailLogic.sendAcceptRejectMailForLeave(
					leaveApplicationWorkflow.getLeaveApplication()
							.getEmployee().getCompany().getCompanyId(),
					leaveApplicationWorkflow,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPROVE,
					totalLeaveDays, leaveBalance.getLeaveBalance(), employee,
					pendingItemsForm.getRemarks(), sessionDTO, isLeaveUnitDays);
		}

	}

	@Override
	public void rejectLeave(PendingItemsForm pendingItemsForm, Long employeeId,
			LeaveSessionDTO sessionDTO) {

		Date date = new Date();
		LeaveApplication leaveApplicationVO = leaveApplicationDAO
				.findById(pendingItemsForm.getLeaveApplicationReviewerId());
		Set<LeaveApplicationReviewer> leaveApplicationReviewerVOs = leaveApplicationVO
				.getLeaveApplicationReviewers();
		LeaveApplicationReviewer pendingLeaveApplicationreveiwer = null;
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationReviewerVOs) {
			if (leaveApplicationReviewer.getPending()) {
				pendingLeaveApplicationreveiwer = leaveApplicationReviewer;
			}

		}

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplicationVO.getCompany().getCompanyId());

		LeaveApplicationWorkflow applicationWorkflow = new LeaveApplicationWorkflow();
		Employee employee = employeeDAO.findById(employeeId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_REJECTED);
		LeaveApplication leaveApplication = new LeaveApplication();
		try {
			BeanUtils.copyProperties(leaveApplication,
					pendingLeaveApplicationreveiwer.getLeaveApplication());
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}
		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplicationDAO.update(leaveApplication);

		for (LeaveApplicationReviewer applicationReviewer : pendingLeaveApplicationreveiwer
				.getLeaveApplication().getLeaveApplicationReviewers()) {
			applicationReviewer.setPending(false);
			leaveApplicationReviewerDAO.update(applicationReviewer);
		}

		applicationWorkflow.setEmployee(employee);
		applicationWorkflow.setEmailCC(pendingItemsForm.getEmailCC());
		applicationWorkflow.setLeaveApplication(pendingLeaveApplicationreveiwer
				.getLeaveApplication());
		applicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
		applicationWorkflow.setRemarks(pendingItemsForm.getRemarks());
		if (pendingItemsForm.getForwardTo() != null) {
			applicationWorkflow.setForwardTo(pendingItemsForm.getForwardTo());
		}
		applicationWorkflow.setStartDate(pendingLeaveApplicationreveiwer
				.getLeaveApplication().getStartDate());
		applicationWorkflow.setEndDate(pendingLeaveApplicationreveiwer
				.getLeaveApplication().getEndDate());
		applicationWorkflow
				.setStartSessionMaster(pendingLeaveApplicationreveiwer
						.getLeaveApplication().getLeaveSessionMaster1());
		applicationWorkflow.setEndSessionMaster(pendingLeaveApplicationreveiwer
				.getLeaveApplication().getLeaveSessionMaster2());

		applicationWorkflow.setTotalDays(pendingLeaveApplicationreveiwer
				.getLeaveApplication().getTotalDays());
		applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		LeaveApplicationWorkflow leaveApplicationWorkflow = applicationWorkflowDAO
				.saveReturn(applicationWorkflow);

		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication
				.getEmployeeLeaveSchemeType();

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication
					.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication
					.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1()
					.getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2()
					.getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType
					.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null, employeeId,
					leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays())
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());

		if (leaveApplication.getLeaveCancelApplication() != null) {
			generalMailLogic.sendAcceptRejectMailForLeave(
					leaveApplicationWorkflow.getLeaveApplication()
							.getEmployee().getCompany().getCompanyId(),
					leaveApplicationWorkflow,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_CANCEL_REJECT,
					totalLeaveDays, leaveBalance.getLeaveBalance(), employee,
					pendingItemsForm.getRemarks(), sessionDTO, isLeaveUnitDays);
		} else {
			generalMailLogic.sendAcceptRejectMailForLeave(
					leaveApplicationWorkflow.getLeaveApplication()
							.getEmployee().getCompany().getCompanyId(),
					leaveApplicationWorkflow,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_REJECT,
					totalLeaveDays, leaveBalance.getLeaveBalance(), employee,
					pendingItemsForm.getRemarks(), sessionDTO, isLeaveUnitDays);
		}

	}

	@Override
	public YearEndProcessingFormResponse getLeaveYearEndBatchList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			int year, Long leaveTypeId, Long groupId, Long leaveSchemeId) {

		YearEndProcessingFormResponse response = new YearEndProcessingFormResponse();
		YearEndProcessingConditionDTO yearEndProcessingConditionDTO = new YearEndProcessingConditionDTO();
		yearEndProcessingConditionDTO.setCompanyId(companyId);
		yearEndProcessingConditionDTO.setGroupId(groupId);
		yearEndProcessingConditionDTO.setLeaveSchemeId(leaveSchemeId);
		yearEndProcessingConditionDTO.setLeaveTypeId(leaveTypeId);
		yearEndProcessingConditionDTO.setYear(year);

		List<YearEndProcessingForm> leaveYearEndFormList = new ArrayList<>();
		List<LeaveYearEndBatch> leaveYearEndBatchList = leaveYearEndBatchDAO
				.findByCondition(yearEndProcessingConditionDTO, year,
						leaveTypeId, pageDTO, sortDTO);
		for (LeaveYearEndBatch leaveYearEndBatch : leaveYearEndBatchList) {
			YearEndProcessingForm leaveYearEndForm = new YearEndProcessingForm();

			leaveYearEndForm.setLeaveYearEndBatchId(leaveYearEndBatch
					.getLeaveYearEndBatchId());
			leaveYearEndForm.setCompanyId(leaveYearEndBatch.getCompany()
					.getCompanyId());
			leaveYearEndForm.setLeaveScheme(leaveYearEndBatch
					.getLeaveSchemeType().getLeaveScheme().getSchemeName());
			leaveYearEndForm.setLeaveType(leaveYearEndBatch
					.getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			leaveYearEndForm.setEmployeesCount(String.valueOf(leaveYearEndBatch
					.getEmployeeCount()));
			leaveYearEndForm.setProcessedDate(DateUtils
					.timeStampToString(leaveYearEndBatch.getProcessedDate()));
			leaveYearEndFormList.add(leaveYearEndForm);
		}

		int recordSize = leaveYearEndBatchDAO
				.getCountByCondition(yearEndProcessingConditionDTO);
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

		}
		response.setRecords(recordSize);
		response.setYearEndProcessList(leaveYearEndFormList);
		return response;
	}

	@Override
	public YearEndProcessingFormResponse getLeaveYearEndEmpDetailList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long leaveYearEndBatchId, String employeeNumber) {
		YearEndProcessingFormResponse response = new YearEndProcessingFormResponse();

		List<YearEndProcessingForm> leaveYearEndFormList = new ArrayList<>();
		List<LeaveYearEndEmployeeDetail> leaveYearEndEmpList = leaveYearEndEmployeeDetailDAO
				.findByCondition(leaveYearEndBatchId, employeeNumber, pageDTO,
						sortDTO);
		for (LeaveYearEndEmployeeDetail batchEmployeeDetail : leaveYearEndEmpList) {
			YearEndProcessingForm leaveYearEndForm = new YearEndProcessingForm();
			leaveYearEndForm
					.setLeaveYearEndEmployeeDetailId(batchEmployeeDetail
							.getLeaveYearEndEmployeeDetailId());
			leaveYearEndForm.setEmployeeId(batchEmployeeDetail.getEmployee()
					.getEmployeeId());
			leaveYearEndForm
					.setEmployeeName(getEmployeeNameWithNumber(batchEmployeeDetail
							.getEmployee()));
			leaveYearEndForm.setBalance(batchEmployeeDetail.getBalance());
			leaveYearEndForm.setLapsed(batchEmployeeDetail.getLapsed());
			leaveYearEndForm.setEncashed(batchEmployeeDetail.getEncashed());
			leaveYearEndForm.setClosingBalance(batchEmployeeDetail
					.getClosingBalance());
			leaveYearEndFormList.add(leaveYearEndForm);

		}
		int recordSize = leaveYearEndEmployeeDetailDAO.getCountByCondition(
				leaveYearEndBatchId, employeeNumber);
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

		}
		response.setRecords(recordSize);
		response.setYearEndProcessList(leaveYearEndFormList);
		return response;
	}

	@Override
	public List<Integer> getDistinctYears(Long companyId) {

		List<Integer> yearList = new ArrayList<>();
		yearList.addAll(leaveYearEndBatchDAO
				.getYearFromProcessedDate(companyId));

		return yearList;
	}

	@Override
	public String deleteLeaveYearEndBatch(Long leaveYearBatchId) {

		return null;
	}

	@Override
	public String deleteLeaveYearEndEmpDetail(Long leaveGrantBatchEmpDetailId) {

		return null;
	}

	@Override
	public YearEndProcessForm getGroupCompanies() {
		YearEndProcessForm yearEndProcessForm = new YearEndProcessForm();
		Map<String, List<CompanyDTO>> companyGroupMap = new LinkedHashMap<>();
		List<CompanyGroup> companyGroups = companyGroupDAO.findAll();
		List<YearEndProcessSchedule> yearEndProcessSchedules = yearEndProcessScheduleDAO
				.findAll();
		HashMap<Long, YearEndProcessSchedule> yearEndProcessScheduleMap = new HashMap<>();
		for (YearEndProcessSchedule yearEndProcessSchedule : yearEndProcessSchedules) {

			yearEndProcessScheduleMap.put(yearEndProcessSchedule.getCompany()
					.getCompanyId(), yearEndProcessSchedule);
		}

		for (CompanyGroup companyGroup : companyGroups) {
			List<CompanyDTO> companyDTOs = new ArrayList<>();
			Set<Company> companies = companyGroup.getCompanies();
			List<Company> companyList = new ArrayList<>(companies);
			Collections.sort(companyList, new CompanyComp());

			for (Company company : companyList) {
				CompanyDTO companyDTO = new CompanyDTO();
				companyDTO.setId(company.getCompanyId());
				companyDTO.setName(company.getCompanyName());
				YearEndProcessSchedule yearEndProcessSchedule = yearEndProcessScheduleMap
						.get(company.getCompanyId());
				if (yearEndProcessSchedule == null) {
					companyDTO.setLeaveActivateValue("01 Jan");
					companyDTO.setLeaveRollOverValue("31 Dec");
				} else {

					String leaveActivateDate = DateUtils
							.timeStampToStringWOTimezone(
									yearEndProcessSchedule.getLeave_Activate(),
									PayAsiaConstants.DEFAULT_DATE_FORMAT);
					leaveActivateDate = leaveActivateDate.substring(0,
							leaveActivateDate.length() - 5);
					companyDTO.setLeaveActivateValue(leaveActivateDate);

					String leaveRollOverDate = DateUtils
							.timeStampToStringWOTimezone(
									yearEndProcessSchedule.getLeaveRollOver(),
									PayAsiaConstants.DEFAULT_DATE_FORMAT);
					leaveRollOverDate = leaveRollOverDate.substring(0,
							leaveRollOverDate.length() - 5);

					companyDTO.setLeaveRollOverValue(leaveRollOverDate);

				}

				companyDTOs.add(companyDTO);
			}
			if (!companyDTOs.isEmpty()) {
				companyGroupMap.put(companyGroup.getGroupName(), companyDTOs);
			}

		}
		yearEndProcessForm.setCompanyGroupList(companyGroupMap);
		return yearEndProcessForm;
	}

	@Override
	public void performYearEndProcessSave(YearEndProcessForm yearEndProcessForm) {
		List<YearEndProcessDTO> yearEndProcessList = yearEndProcessForm
				.getYearEndProcessList();
		for (YearEndProcessDTO yearEndProcessDTO : yearEndProcessList) {
			if (yearEndProcessDTO == null) {
				continue;
			}
			if (yearEndProcessDTO.getCompanyId() == null) {
				continue;
			}
			String year = " 2013";
			yearEndProcessDTO.setLeaveActivateDate(yearEndProcessDTO
					.getLeaveActivateDate() + year);
			yearEndProcessDTO.setLeaveRollOverDate(yearEndProcessDTO
					.getLeaveRollOverDate() + year);
			YearEndProcessSchedule yearEndProcessScheduleVO = yearEndProcessScheduleDAO
					.findByCompanyId(yearEndProcessDTO.getCompanyId());
			if (yearEndProcessScheduleVO == null) {
				yearEndProcessScheduleVO = new YearEndProcessSchedule();
				yearEndProcessScheduleVO.setLeave_Activate(DateUtils
						.stringToTimestamp(
								yearEndProcessDTO.getLeaveActivateDate(),
								PayAsiaConstants.DEFAULT_DATE_FORMAT));
				yearEndProcessScheduleVO.setLeaveRollOver(DateUtils
						.stringToTimestamp(
								yearEndProcessDTO.getLeaveRollOverDate(),
								PayAsiaConstants.DEFAULT_DATE_FORMAT));
				Company company = companyDAO.findById(yearEndProcessDTO
						.getCompanyId());
				yearEndProcessScheduleVO.setCompany(company);
				yearEndProcessScheduleDAO.save(yearEndProcessScheduleVO);

			} else {

				yearEndProcessScheduleVO.setLeave_Activate(DateUtils
						.stringToTimestamp(
								yearEndProcessDTO.getLeaveActivateDate(),
								PayAsiaConstants.DEFAULT_DATE_FORMAT));
				yearEndProcessScheduleVO.setLeaveRollOver(DateUtils
						.stringToTimestamp(
								yearEndProcessDTO.getLeaveRollOverDate(),
								PayAsiaConstants.DEFAULT_DATE_FORMAT));
				yearEndProcessScheduleDAO.update(yearEndProcessScheduleVO);

			}

		}

	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class CompanyComp implements Comparator<Company> {
		public int compare(Company templateField, Company compWithTemplateField) {
			if (templateField.getCompanyId() > compWithTemplateField
					.getCompanyId()) {
				return 1;
			} else if (templateField.getCompanyId() < compWithTemplateField
					.getCompanyId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public YearEndProcessFilterForm getYearEndProcessFilterData(Long groupId) {
		YearEndProcessFilterForm yearEndProcessFilterForm = new YearEndProcessFilterForm();

		if (groupId == null) {
			SortCondition sort = null;
			List<Company> companyListVO = companyDAO.findAll(sort);

			List<CompanyDTO> companyList = new ArrayList<>();
			for (Company company : companyListVO) {
				CompanyDTO companyDTO = new CompanyDTO();
				companyDTO.setId(company.getCompanyId());
				companyDTO.setName(company.getCompanyName());

				companyList.add(companyDTO);
			}
			yearEndProcessFilterForm.setCompanyList(companyList);
			yearEndProcessFilterForm.setLeaveSchemeDTOs(leaveSchemeLogic
					.getAllLeaveSchemes(null));
			yearEndProcessFilterForm.setLeaveTypeDTOs(leaveTypeLogic
					.getAllLeaveTypes(null));
		} else {
			LeaveEventReminderForm leaveEventReminderForm = new LeaveEventReminderForm();
			leaveEventReminderForm.setLeaveSchemeDTOs(leaveSchemeLogic
					.getAllLeaveSchemes(null));

			SortCondition sortCondition = null;
			List<Company> companies = companyDAO.findByGroupId(sortCondition,
					groupId);
			List<CompanyDTO> companyList = new ArrayList<>();
			for (Company company : companies) {
				CompanyDTO companyDTO = new CompanyDTO();
				companyDTO.setId(company.getCompanyId());
				companyDTO.setName(company.getCompanyName());

				companyList.add(companyDTO);
			}
			yearEndProcessFilterForm.setCompanyList(companyList);

		}

		return yearEndProcessFilterForm;
	}

	@Override
	public YearEndProcessFilterForm getYearEndProcessFilterDataOnCmpChange(
			Long companyId) {
		YearEndProcessFilterForm yearEndProcessFilterForm = new YearEndProcessFilterForm();

		List<LeaveSchemeDTO> leaveSchemeDTOs = new ArrayList<>();
		List<LeaveScheme> leaveSchemeVOs = leaveSchemeDAO
				.getAllLeaveScheme(companyId);
		for (LeaveScheme leaveScheme : leaveSchemeVOs) {
			LeaveSchemeDTO leaveSchemeDTO = new LeaveSchemeDTO();
			leaveSchemeDTO.setLeaveSchemeId(leaveScheme.getLeaveSchemeId());
			leaveSchemeDTO.setLeaveSchemeName(leaveScheme.getSchemeName());
			leaveSchemeDTOs.add(leaveSchemeDTO);
		}
		yearEndProcessFilterForm.setLeaveSchemeDTOs(leaveSchemeDTOs);

		List<LeaveTypeDTO> leaveTypeDTOs = new ArrayList<>();
		List<LeaveTypeMaster> leaveTypeMasterVOs = leaveTypeMasterDAO
				.getAllLeaveTypes(companyId);
		for (LeaveTypeMaster leaveTypeMaster : leaveTypeMasterVOs) {
			LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
			leaveTypeDTO.setLeaveTypeId(leaveTypeMaster.getLeaveTypeId());
			leaveTypeDTO.setLeaveTypeName(leaveTypeMaster.getLeaveTypeName());
			leaveTypeDTOs.add(leaveTypeDTO);
		}
		yearEndProcessFilterForm.setLeaveTypeDTOs(leaveTypeDTOs);
		return yearEndProcessFilterForm;
	}

	@Override
	public LeaveYearEndEmployeeDetailForm editLeaveYearEndEmpDetail(
			Long leaveYearEndEmployeeDetailId) {
		LeaveYearEndEmployeeDetailForm leaveYearEndEmployeeDetailForm = new LeaveYearEndEmployeeDetailForm();
		LeaveYearEndEmployeeDetail leaveYearEndEmployeeDetail = leaveYearEndEmployeeDetailDAO
				.findByID(leaveYearEndEmployeeDetailId);
		leaveYearEndEmployeeDetailForm.setBalance(leaveYearEndEmployeeDetail
				.getBalance());
		leaveYearEndEmployeeDetailForm
				.setClosingBalance(leaveYearEndEmployeeDetail
						.getClosingBalance());
		leaveYearEndEmployeeDetailForm.setEmployee(employeeDetailLogic
				.getEmployeeName(leaveYearEndEmployeeDetail.getEmployee()));
		leaveYearEndEmployeeDetailForm.setEncashed(leaveYearEndEmployeeDetail
				.getEncashed());
		leaveYearEndEmployeeDetailForm.setLapsed(leaveYearEndEmployeeDetail
				.getLapsed());
		leaveYearEndEmployeeDetailForm
				.setLeaveYearEndEmployeeDetailId(leaveYearEndEmployeeDetail
						.getLeaveYearEndEmployeeDetailId());

		return leaveYearEndEmployeeDetailForm;
	}

	@Override
	public void updateEmployeeYearEndSummaryDetail(
			LeaveYearEndEmployeeDetailForm leaveYearEndEmployeeDetailForm) {
		LeaveYearEndEmployeeDetail leaveYearEndEmployeeDetailVO = leaveYearEndEmployeeDetailDAO
				.findByID(leaveYearEndEmployeeDetailForm
						.getLeaveYearEndEmployeeDetailId());

		leaveYearEndEmployeeDetailVO.setBalance(leaveYearEndEmployeeDetailForm
				.getBalance());
		leaveYearEndEmployeeDetailVO
				.setClosingBalance(leaveYearEndEmployeeDetailForm
						.getClosingBalance());
		leaveYearEndEmployeeDetailVO.setEncashed(leaveYearEndEmployeeDetailForm
				.getEncashed());
		leaveYearEndEmployeeDetailVO.setLapsed(leaveYearEndEmployeeDetailForm
				.getLapsed());
		leaveYearEndEmployeeDetailDAO.update(leaveYearEndEmployeeDetailVO);

	}
}
