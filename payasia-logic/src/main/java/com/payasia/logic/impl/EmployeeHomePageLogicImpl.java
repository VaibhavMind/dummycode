package com.payasia.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.AddLeaveConditionDTO;
import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.dto.HrisPendingItemsConditionDTO;
import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.PendingClaimConditionDTO;
import com.payasia.common.dto.RecentActivityDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.EmployeeHomePageForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingClaimsForm;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.RecentActivityComparison;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimApplicationReviewerDAO;
import com.payasia.dao.ClaimApplicationWorkflowDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.CoherentOvertimeApplicationDAO;
import com.payasia.dao.CoherentOvertimeApplicationReviewerDAO;
import com.payasia.dao.CoherentShiftApplicationDAO;
import com.payasia.dao.CoherentShiftApplicationReviewerDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyPayslipReleaseDAO;
import com.payasia.dao.EmployeeActivationCodeDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeDefaultEmailCCDAO;
import com.payasia.dao.EmployeePasswordChangeHistoryDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.HRISChangeRequestDAO;
import com.payasia.dao.HRISChangeRequestReviewerDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveStatusMasterDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.PayslipUploadHistoryDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.CoherentOvertimeApplication;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationReviewer;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyPayslipRelease;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeActivationCode;
import com.payasia.dao.bean.EmployeeDefaultEmailCC;
import com.payasia.dao.bean.EmployeePasswordChangeHistory;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequestReviewer;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.dao.bean.PayslipUploadHistory;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.EmployeeHomePageLogic;
import com.payasia.logic.MultilingualLogic;

@Component
public class EmployeeHomePageLogicImpl implements EmployeeHomePageLogic {

	@Resource
	PayslipUploadHistoryDAO payslipUploadHistoryDAO;
	@Resource
	EmployeePasswordChangeHistoryDAO employeePasswordChangeHistoryDAO;
	@Resource
	PasswordPolicyConfigMasterDAO passwordPolicyConfigMasterDAO;
	@Resource
	LeaveStatusMasterDAO leaveStatusMasterDAO;
	@Resource
	TimesheetApplicationReviewerDAO lundinTimesheetReviewerDAO;
	@Resource
	EmployeeTimesheetApplicationDAO lundinTimesheetDAO;
	@Resource
	LeaveApplicationDAO leaveApplicationDAO;
	@Resource
	CompanyPayslipReleaseDAO companyPayslipReleaseDAO;
	@Resource
	MonthMasterDAO monthMasterDAO;

	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewerDAO;

	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;

	@Resource
	ClaimApplicationReviewerDAO claimApplicationReviewerDAO;

	@Resource
	ClaimApplicationDAO claimApplicationDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	EmployeeActivationCodeDAO employeeActivationCodeDAO;

	@Resource
	HRISChangeRequestDAO hrisChangeRequestDAO;

	@Resource
	HRISChangeRequestReviewerDAO hrisChangeRequestReviewerDAO;

	@Resource
	MultilingualLogic multilingualLogic;

	@Resource
	ClaimApplicationWorkflowDAO claimApplicationWorkflowDAO;
	@Resource
	EmployeeDefaultEmailCCDAO employeeDefaultEmailCCDAO;
	@Resource
	ModuleMasterDAO moduleMasterDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	@Resource
	LeavePreferenceDAO leavePreferenceDAO;

	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetApplicationReviewerDAO;

	@Resource
	LionEmployeeTimesheetApplicationDetailDAO lionEmployeeTimesheetApplicationDetailDAO;
	@Resource
	CoherentOvertimeApplicationDAO coherentOvertimeApplicationDAO;
	@Resource
	CoherentOvertimeApplicationReviewerDAO coherentOvertimeApplicationReviewerDAO;

	@Resource
	CoherentShiftApplicationDAO coherentShiftApplicationDAO;

	@Resource
	CoherentShiftApplicationReviewerDAO coherentShiftApplicationReviewerDAO;

	/** The employee detail logic. */
	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;

	@Override
	public EmployeeHomePageForm getPayslipDetails(Long companyId) {
		int count = 0;
		EmployeeHomePageForm employeeHomePageForm = new EmployeeHomePageForm();
		List<CompanyPayslipRelease> companyPayslipReleaseList = companyPayslipReleaseDAO.findByCompany(companyId);
		if (!companyPayslipReleaseList.isEmpty()) {
			for (CompanyPayslipRelease payslipRelease : companyPayslipReleaseList) {
				if (payslipRelease.isReleased()) {
					count++;
				}
			}
			CompanyPayslipRelease companyPayslipRelease = null;
			if (count > 0) {
				companyPayslipRelease = companyPayslipReleaseDAO.findLastPayslipReleased(companyId);
				employeeHomePageForm.setPayslipUploadId(0l);
				if (companyPayslipRelease != null) {
					employeeHomePageForm.setCompanyName(companyPayslipRelease.getCompany().getCompanyName());
					employeeHomePageForm.setPayslipUploadId(companyPayslipRelease.getCompanyPayslipReleaseId());
					employeeHomePageForm.setMonth(companyPayslipRelease.getMonthMaster().getMonthName());
					employeeHomePageForm.setYear(companyPayslipRelease.getYear());
				}
			} else {
				companyPayslipRelease = companyPayslipReleaseDAO.findMaxPayslipRelease(companyId);
				if (companyPayslipRelease != null) {
					Long monthId = null;
					if (companyPayslipRelease.getMonthMaster().getMonthId() == 1) {
						monthId = 13l;
					} else {
						monthId = companyPayslipRelease.getMonthMaster().getMonthId();
					}
					PayslipUploadHistory payslipUploadHistory = payslipUploadHistoryDAO.findByCondition(companyId,
							monthId, companyPayslipRelease.getYear());
					employeeHomePageForm.setPayslipUploadId(0l);
					if (payslipUploadHistory != null) {
						employeeHomePageForm.setCompanyName(payslipUploadHistory.getCompany().getCompanyName());
						employeeHomePageForm.setPayslipUploadDate(
								DateUtils.timeStampToString(payslipUploadHistory.getPayslip_Upload_Date(),
										payslipUploadHistory.getCompany().getDateFormat()));
						employeeHomePageForm.setPayslipUploadId(payslipUploadHistory.getPayslipUploadHistoryId());
						employeeHomePageForm.setMonth(payslipUploadHistory.getMonthMaster().getMonthName());
						employeeHomePageForm.setYear(payslipUploadHistory.getYear());
					}
				}
			}

		} else {
			PayslipUploadHistory payslipUploadHistory = payslipUploadHistoryDAO.findByCompany(companyId);
			employeeHomePageForm.setPayslipUploadId(0l);
			if (payslipUploadHistory != null) {
				employeeHomePageForm.setCompanyName(payslipUploadHistory.getCompany().getCompanyName());
				employeeHomePageForm
						.setPayslipUploadDate(DateUtils.timeStampToString(payslipUploadHistory.getPayslip_Upload_Date(),
								payslipUploadHistory.getCompany().getDateFormat()));
				employeeHomePageForm.setPayslipUploadId(payslipUploadHistory.getPayslipUploadHistoryId());
				employeeHomePageForm.setMonth(payslipUploadHistory.getMonthMaster().getMonthName());
				employeeHomePageForm.setYear(payslipUploadHistory.getYear());
			}
		}

		return employeeHomePageForm;
	}

	@Override
	public EmployeeHomePageForm getLeaveDetails(Long companyId, Long employeeId) {

		EmployeeHomePageForm employeeHomePageForm = new EmployeeHomePageForm();
		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(employeeId);

		List<String> leaveStatusNames = new ArrayList<>();
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
		conditionDTO.setLeaveStatusNames(leaveStatusNames);
		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionSubmitted(conditionDTO, null, null);
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setApplyTo(getEmployeeName(leaveApplication.getEmployee()));
			addLeaveForm.setLeaveApplicationId(leaveApplication.getLeaveApplicationId());
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());
			addLeaveForm.setLeaveType(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveTypeMaster().getLeaveTypeName());
			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			double totalDays=leaveApplication.getTotalDays();
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(totalDays));
			addLeaveFormList.add(addLeaveForm);

		}
		employeeHomePageForm.setSubmittedLeaveList(addLeaveFormList);
		List<LeaveApplicationReviewer> leaveReviewerList = leaveApplicationReviewerDAO.findByCondition(employeeId, null,
				null);

		List<PendingItemsForm> pendingItemsForms = new ArrayList<PendingItemsForm>();

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveReviewerList) {
			PendingItemsForm pendingItemsForm = new PendingItemsForm();

			if (leaveApplicationReviewer.getLeaveApplication().getLeaveCancelApplication() != null) {
			
				pendingItemsForm.setIsCancelApplication(true);
			} else {
				pendingItemsForm.setIsCancelApplication(false);
			}
			pendingItemsForm.setDays(BigDecimal.
					valueOf(leaveApplicationReviewer.getLeaveApplication().getTotalDays()));
			pendingItemsForm.setLeaveApplicationId(leaveApplicationReviewer
					.getLeaveApplication().getLeaveApplicationId());
			pendingItemsForm
					.setLeaveApplicationId(leaveApplicationReviewer.getLeaveApplication().getLeaveApplicationId());
			pendingItemsForm
					.setCreatedBy(getEmployeeName(leaveApplicationReviewer.getLeaveApplication().getEmployee()));
			pendingItemsForm.setWorkflowTypeName(leaveApplicationReviewer.getLeaveApplication()
					.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
			pendingItemsForm
					.setCreatedById(leaveApplicationReviewer.getLeaveApplication().getEmployee().getEmployeeId());
			pendingItemsForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(leaveApplicationReviewer.getLeaveApplication().getCreatedDate()));
			pendingItemsForm.setCreatedDateM(leaveApplicationReviewer.getLeaveApplication().getCreatedDate());
			pendingItemsForm.setStatus(
					leaveApplicationReviewer.getLeaveApplication().getLeaveStatusMaster().getLeaveStatusName());
			pendingItemsForm.setFromDate(
					DateUtils.timeStampToString(leaveApplicationReviewer.getLeaveApplication().getStartDate()));
			pendingItemsForm.setToDate(
					DateUtils.timeStampToString(leaveApplicationReviewer.getLeaveApplication().getEndDate()));
			pendingItemsForm.setLeaveApplicationReviewerId(leaveApplicationReviewer.getLeaveApplicationReviewerId());

			pendingItemsForms.add(pendingItemsForm);

		}
		employeeHomePageForm.setReviewLeaveList(pendingItemsForms);
		return employeeHomePageForm;
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

	@Override
	public String getPasswordExpiryReminder(Long employeeId, Long companyId) {
		EmployeePasswordChangeHistory empPasswordChangeHistory = employeePasswordChangeHistoryDAO
				.getPreviousPasswords(employeeId);

		PasswordPolicyConfigMaster passwordPolicyConfigMasterVO = passwordPolicyConfigMasterDAO
				.findByConditionCompany(companyId);
		if (passwordPolicyConfigMasterVO != null) {
			if (passwordPolicyConfigMasterVO.getMaxExpiryDaysLimit() != 0) {
				if (passwordPolicyConfigMasterVO.getExpiryReminderDays() != 0) {
					int maxPwdAge = passwordPolicyConfigMasterVO.getMaxExpiryDaysLimit();
					int expiryDays = passwordPolicyConfigMasterVO.getExpiryReminderDays();
					int startReminderDays = maxPwdAge - expiryDays;
					if (empPasswordChangeHistory != null) {
						Date changedPwdDate = DateUtils
								.stringToDate(DateUtils.timeStampToString(empPasswordChangeHistory.getChangeDate()));

						Date startReminderDate = changedPwdDate;
						Calendar cal = Calendar.getInstance();
						cal.setTime(startReminderDate);
						cal.add(Calendar.DATE, startReminderDays);
						startReminderDate = cal.getTime();

						Date expiredDate = changedPwdDate;

						cal.setTime(expiredDate);
						cal.add(Calendar.DATE, maxPwdAge);
						expiredDate = cal.getTime();

						Date currentDate = DateUtils
								.stringToDate(DateUtils.timeStampToString(DateUtils.getCurrentTimestamp()));
						if ((currentDate.after(startReminderDate)) && (!currentDate.equals(expiredDate))) {
							int diffInDays = (int) ((expiredDate.getTime() - currentDate.getTime())
									/ (1000 * 60 * 60 * 24));
							return String.valueOf(diffInDays);
						}
						if (currentDate.equals(startReminderDate)) {
							int diffInDays = (int) ((expiredDate.getTime() - currentDate.getTime())
									/ (1000 * 60 * 60 * 24));
							return String.valueOf(diffInDays);
						}
						if (currentDate.equals(expiredDate)) {
							return PayAsiaConstants.TODAY;
						}

					}
				}
			}

		}

		return "";
	}

	@Override
	public EmployeeHomePageForm getClaimDetails(Long companyId, Long employeeId) {

		EmployeeHomePageForm employeeHomePageRes = new EmployeeHomePageForm();
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();
		conditionDTO.setEmployeeId(employeeId);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
		conditionDTO.setClaimStatus(claimStatus);
		conditionDTO.setVisibleToEmployee(true);
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByCondition(conditionDTO, null, null);

		List<AddClaimForm> pedClaims = new ArrayList<AddClaimForm>();
		for (ClaimApplication claimApplication : pendingClaims) {
			AddClaimForm addClaimForm = new AddClaimForm();

			addClaimForm.setClaimTemplateName(
					claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			addClaimForm.setApplyTo(getEmployeeName(claimApplication.getEmployee()));

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(claimApplication.getClaimApplicationId());
			addClaimForm.setClaimAmount(claimApplication.getTotalAmount());
			addClaimForm.setTotalItems(String.valueOf(claimApplication.getTotalItems()));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			pedClaims.add(addClaimForm);
		}

		employeeHomePageRes.setPendingClaims(pedClaims);
		PendingClaimConditionDTO claimConditionDTO = new PendingClaimConditionDTO();

		List<ClaimApplicationReviewer> claimApplicationReviewers = claimApplicationReviewerDAO
				.findByCondition(employeeId, null, null, claimConditionDTO);

		claimConditionDTO.setClaimStatusName(PayAsiaConstants.CLAIM_STATUS_PENDING);

		List<PendingClaimsForm> pendingClaimsForms = new ArrayList<PendingClaimsForm>();

		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			PendingClaimsForm pendingClaimsForm = new PendingClaimsForm();

			ClaimApplication claimApplication = claimApplicationReviewer.getClaimApplication();

			pendingClaimsForm.setCreatedBy(claimApplication.getEmployee().getFirstName());
			pendingClaimsForm.setCreatedById(claimApplication.getEmployee().getEmployeeId());
			pendingClaimsForm.setCreatedDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));
			pendingClaimsForm.setTotalAmount(claimApplication.getTotalAmount());
			pendingClaimsForm.setClaimNumber(claimApplication.getClaimNumber());
			pendingClaimsForm.setClaimTemplateName(
					claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			pendingClaimsForm.setClaimApplicationReviewerId(claimApplicationReviewer.getClaimApplicationReviewerId());
			pendingClaimsForms.add(pendingClaimsForm);
		}
		employeeHomePageRes.setPendingClaimRequests(pendingClaimsForms);

		return employeeHomePageRes;
	}

	@Override
	public EmployeeHomePageForm getAllRecentActivityList(Long companyId, Long employeeId,
			CompanyModuleDTO companyModuleDTO, Long languageId) {

		EmployeeHomePageForm employeeHomePageRes = new EmployeeHomePageForm();
		List<RecentActivityDTO> recentActivityDTOList = new ArrayList<RecentActivityDTO>();

		if (companyModuleDTO.isHasClaimModule()) {

			AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();
			conditionDTO.setEmployeeId(employeeId);
			List<String> claimStatus = new ArrayList<>();
			claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
			claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
			conditionDTO.setClaimStatus(claimStatus);
			conditionDTO.setVisibleToEmployee(true);
			List<ClaimApplication> pendingClaims = claimApplicationDAO.findByCondition(conditionDTO, null, null);

			for (ClaimApplication claimApplication : pendingClaims) {
				RecentActivityDTO addClaimForm = new RecentActivityDTO();

				addClaimForm.setClaimTemplateName(
						claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
				ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(), claimApplication.getEmployee().getEmployeeId());
				addClaimForm.setCreatedDate(claimApplicationWorkflow == null
						? DateUtils.timeStampToString(claimApplication.getCreatedDate())
						: DateUtils.timeStampToString(claimApplicationWorkflow.getCreatedDate()));

				addClaimForm.setClaimApplicationId(
						FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));
				addClaimForm.setActivityType(PayAsiaConstants.PAYASIA_CLAIM_SUBMITTED_CLAIM);
				addClaimForm.setUpdatedDate(claimApplication.getUpdatedDate());
				recentActivityDTOList.add(addClaimForm);
			}

			PendingClaimConditionDTO claimConditionDTO = new PendingClaimConditionDTO();
			List<ClaimApplicationReviewer> claimApplicationReviewers = claimApplicationReviewerDAO
					.findByCondition(employeeId, null, null, claimConditionDTO);

			for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
				RecentActivityDTO pendingClaimsForm = new RecentActivityDTO();

				ClaimApplication claimApplication = claimApplicationReviewer.getClaimApplication();
				ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(), claimApplication.getEmployee().getEmployeeId());
				pendingClaimsForm.setCreatedBy(getEmployeeName(claimApplication.getEmployee()));
				pendingClaimsForm.setCreatedById(claimApplication.getEmployee().getEmployeeId());
				pendingClaimsForm
						.setCreatedDate(DateUtils.timeStampToString(claimApplicationWorkflow.getCreatedDate()));
				pendingClaimsForm.setClaimTemplateName(
						claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
				pendingClaimsForm.setClaimApplicationReviewerId(
						FormatPreserveCryptoUtil.encrypt(claimApplicationReviewer.getClaimApplicationReviewerId()));
				pendingClaimsForm.setActivityType(PayAsiaConstants.PAYASIA_CLAIM_PENDING_CLAIM);
				pendingClaimsForm.setUpdatedDate(claimApplication.getUpdatedDate());
				recentActivityDTOList.add(pendingClaimsForm);
			}
		}
		if (companyModuleDTO.isHasLeaveModule()) {

			AddLeaveConditionDTO addLeaveConditionDTO = new AddLeaveConditionDTO();
			addLeaveConditionDTO.setEmployeeId(employeeId);
			List<String> leaveStatusNames = new ArrayList<>();
			leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
			leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			addLeaveConditionDTO.setLeaveStatusNames(leaveStatusNames);
			List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionSubmitted(addLeaveConditionDTO,
					null, null);
			for (LeaveApplication leaveApplication : pendingLeaves) {
				RecentActivityDTO addLeaveForm = new RecentActivityDTO();
				/* ID ENCRYPT */
				addLeaveForm.setLeaveApplicationId(
						FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
				addLeaveForm.setLeaveType(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
						.getLeaveTypeMaster().getLeaveTypeName());
				addLeaveForm.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
				addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
				addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
				addLeaveForm.setActivityType(PayAsiaConstants.PAYASIA_LEAVE_SUBMITTED_LEAVE);
				addLeaveForm.setUpdatedDate(leaveApplication.getUpdatedDate());
				recentActivityDTOList.add(addLeaveForm);

			}

			List<LeaveApplicationReviewer> leaveReviewerList = leaveApplicationReviewerDAO.findByCondition(employeeId,
					null, null);

			for (LeaveApplicationReviewer leaveApplicationReviewer : leaveReviewerList) {
				RecentActivityDTO pendingItemsForm = new RecentActivityDTO();

				if (leaveApplicationReviewer.getLeaveApplication().getLeaveCancelApplication() != null) {
					pendingItemsForm.setIsCancelApplication(true);
				} else {
					pendingItemsForm.setIsCancelApplication(false);
				}
				pendingItemsForm
						.setLeaveApplicationId(leaveApplicationReviewer.getLeaveApplication().getLeaveApplicationId());
				pendingItemsForm
						.setCreatedBy(getEmployeeName(leaveApplicationReviewer.getLeaveApplication().getEmployee()));
				pendingItemsForm.setWorkflowTypeName(leaveApplicationReviewer.getLeaveApplication()
						.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				pendingItemsForm
						.setCreatedById(leaveApplicationReviewer.getLeaveApplication().getEmployee().getEmployeeId());
				pendingItemsForm.setCreatedDate(DateUtils
						.timeStampToStringWithTime(leaveApplicationReviewer.getLeaveApplication().getCreatedDate()));
				pendingItemsForm.setFromDate(
						DateUtils.timeStampToString(leaveApplicationReviewer.getLeaveApplication().getStartDate()));
				pendingItemsForm.setToDate(
						DateUtils.timeStampToString(leaveApplicationReviewer.getLeaveApplication().getEndDate()));
				pendingItemsForm.setLeaveApplicationReviewerId(
						FormatPreserveCryptoUtil.encrypt(leaveApplicationReviewer.getLeaveApplicationReviewerId()));
				pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_LEAVE_PENDING_LEAVE);
				pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(
						DateUtils.timeStampToString(leaveApplicationReviewer.getLeaveApplication().getUpdatedDate())));
				recentActivityDTOList.add(pendingItemsForm);

			}
		}
		if (companyModuleDTO.isHasLundinTimesheetModule()) {
			LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
			otTimesheetConditionDTO.setEmployeeId(employeeId);
			List<String> otStatusNames = new ArrayList<>();
			otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
			otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			otTimesheetConditionDTO.setStatusNameList(otStatusNames);
			List<EmployeeTimesheetApplication> pendingOTTimesheet = lundinTimesheetDAO
					.findByConditionSubmitted(otTimesheetConditionDTO, null, null);

			for (EmployeeTimesheetApplication lundinTimesheet : pendingOTTimesheet) {
				RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
				/* ID ENCRYPT */
				recentActivityDTO
						.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(lundinTimesheet.getTimesheetId()));
				recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(lundinTimesheet.getCreatedDate()));

				recentActivityDTO.setWorkflowTypeName(lundinTimesheet.getTimesheetBatch().getTimesheetBatchDesc());
				recentActivityDTO.setActivityType(PayAsiaConstants.PAYASIA_LUNDIN_TIMESHEET_SUBMITTED);
				recentActivityDTO.setUpdatedDate(lundinTimesheet.getUpdatedDate());
				recentActivityDTOList.add(recentActivityDTO);

			}

			List<TimesheetApplicationReviewer> otTimesheetReviewerList = lundinTimesheetReviewerDAO
					.findByCondition(employeeId, null, null);

			for (TimesheetApplicationReviewer otTimesheetReviewer : otTimesheetReviewerList) {
				RecentActivityDTO pendingItemsForm = new RecentActivityDTO();
				/* ID ENCRYPT */
				pendingItemsForm.setLundinTimesheetId(FormatPreserveCryptoUtil
						.encrypt(otTimesheetReviewer.getEmployeeTimesheetApplication().getTimesheetId()));

				pendingItemsForm.setCreatedBy(
						getEmployeeName(otTimesheetReviewer.getEmployeeTimesheetApplication().getEmployee()));

				pendingItemsForm.setCreatedById(
						otTimesheetReviewer.getEmployeeTimesheetApplication().getEmployee().getEmployeeId());
				pendingItemsForm.setCreatedDate(DateUtils.timeStampToStringWithTime(
						otTimesheetReviewer.getEmployeeTimesheetApplication().getCreatedDate()));
				pendingItemsForm.setLundinTimesheetReviewerId(otTimesheetReviewer.getTimesheetReviewerId());
				pendingItemsForm.setWorkflowTypeName(otTimesheetReviewer.getEmployeeTimesheetApplication()
						.getTimesheetBatch().getTimesheetBatchDesc());
				pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_LUNDIN_TIMESHEET_PENDING);
				pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(DateUtils
						.timeStampToString(otTimesheetReviewer.getEmployeeTimesheetApplication().getUpdatedDate())));
				recentActivityDTOList.add(pendingItemsForm);

			}
		}

		if (companyModuleDTO.isHasLionTimesheetModule()) {
			LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
			otTimesheetConditionDTO.setEmployeeId(employeeId);
			List<String> otStatusNames = new ArrayList<>();
			otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
			otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			otTimesheetConditionDTO.setStatusNameList(otStatusNames);
			List<EmployeeTimesheetApplication> pendingOTTimesheet = lundinTimesheetDAO
					.findByConditionSubmitted(otTimesheetConditionDTO, null, null);

			for (EmployeeTimesheetApplication lundinTimesheet : pendingOTTimesheet) {
				RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
				/* ID ENCRYPT */
				recentActivityDTO
						.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(lundinTimesheet.getTimesheetId()));
				recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(lundinTimesheet.getCreatedDate()));

				recentActivityDTO.setWorkflowTypeName(lundinTimesheet.getTimesheetBatch().getTimesheetBatchDesc());

				recentActivityDTO.setActivityType(PayAsiaConstants.PAYASIA_LION_TIMESHEET_SUBMITTED);

				recentActivityDTO.setUpdatedDate(lundinTimesheet.getUpdatedDate());
				recentActivityDTOList.add(recentActivityDTO);

			}

			List<LionTimesheetApplicationReviewer> otTimesheetReviewerList = lionTimesheetApplicationReviewerDAO
					.findByCondition(employeeId, companyId);
			List<String> employeeNBatchList = new ArrayList<String>();
			for (LionTimesheetApplicationReviewer otTimesheetReviewer : otTimesheetReviewerList) {
				RecentActivityDTO pendingItemsForm = new RecentActivityDTO();

				Employee createdBy = employeeDAO.findById(Long.parseLong(otTimesheetReviewer.getCreatedBy()));

				EmployeeTimesheetApplication employeeTimesheetApplication = lionEmployeeTimesheetApplicationDetailDAO
						.findById(otTimesheetReviewer.getLionEmployeeTimesheetApplicationDetail()
								.getEmployeeTimesheetDetailID())
						.getEmployeeTimesheetApplication();

				String employeeNBatch = employeeTimesheetApplication.getTimesheetBatch().getTimesheetBatchDesc()
						+ createdBy.getEmployeeId();
				if (!employeeNBatchList.isEmpty() && employeeNBatchList.contains(employeeNBatch)) {
					continue;
				} else {
					employeeNBatchList.add(employeeNBatch);
				}
				/* ID ENCRYPT */
				pendingItemsForm.setLundinTimesheetId(
						FormatPreserveCryptoUtil.encrypt(employeeTimesheetApplication.getTimesheetId()));

				pendingItemsForm.setCreatedBy(getEmployeeName(createdBy));

				pendingItemsForm.setCreatedById(createdBy.getEmployeeId());
				pendingItemsForm
						.setCreatedDate(DateUtils.timeStampToStringWithTime(otTimesheetReviewer.getCreatedDate()));
				pendingItemsForm
						.setLundinTimesheetReviewerId(otTimesheetReviewer.getEmployeeReviewer().getEmployeeId());
				pendingItemsForm
						.setWorkflowTypeName(employeeTimesheetApplication.getTimesheetBatch().getTimesheetBatchDesc());
				pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_LION_TIMESHEET_PENDING);
				pendingItemsForm.setUpdatedDate(
						DateUtils.stringToDate(DateUtils.timeStampToString(otTimesheetReviewer.getUpdatedDate())));
				recentActivityDTOList.add(pendingItemsForm);

			}
		}
		if (companyModuleDTO.isHasCoherentTimesheetModule()) {
			setCoherentTimesheetNotification(employeeId, recentActivityDTOList);
		}
		HrisPendingItemsConditionDTO hrisPendingItemsConditionDTO = new HrisPendingItemsConditionDTO();
		hrisPendingItemsConditionDTO.setEmployeeId(employeeId);
		List<String> hrisStatusNames = new ArrayList<>();
		hrisStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		hrisStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
		hrisPendingItemsConditionDTO.setHrisStatusNames(hrisStatusNames);
		List<HRISChangeRequest> pendingChangeRequest = hrisChangeRequestDAO
				.findByConditionSubmitted(hrisPendingItemsConditionDTO, null, null);
		for (HRISChangeRequest hrisChangeRequest : pendingChangeRequest) {
			RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
			/* ID ENCRYPT */
			recentActivityDTO.setHrisChangeRequestId(
					FormatPreserveCryptoUtil.encrypt(hrisChangeRequest.getHrisChangeRequestId()));
			recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(hrisChangeRequest.getCreatedDate()));
			String multilingualFieldLabel = multilingualLogic.convertFieldLabelToSpecificLanguage(languageId,
					hrisChangeRequest.getDataDictionary().getCompany().getCompanyId(),
					hrisChangeRequest.getDataDictionary().getDataDictionaryId());
			if (StringUtils.isBlank(multilingualFieldLabel)) {
				multilingualFieldLabel = hrisChangeRequest.getDataDictionary().getLabel();
			}

			recentActivityDTO.setWorkflowTypeName(multilingualFieldLabel);
			recentActivityDTO.setActivityType(PayAsiaConstants.PAYASIA_HRIS_SUBMITTED_DATA_CHANGE_REQUEST);
			recentActivityDTO.setUpdatedDate(hrisChangeRequest.getUpdatedDate());
			recentActivityDTOList.add(recentActivityDTO);

		}

		List<HRISChangeRequestReviewer> hrisReviewerList = hrisChangeRequestReviewerDAO.findByCondition(employeeId,
				null, null);

		for (HRISChangeRequestReviewer hrisChangeRequestReviewer : hrisReviewerList) {
			RecentActivityDTO pendingItemsForm = new RecentActivityDTO();

			pendingItemsForm
					.setHrisChangeRequestId(hrisChangeRequestReviewer.getHrisChangeRequest().getHrisChangeRequestId());
			pendingItemsForm
					.setCreatedBy(getEmployeeName(hrisChangeRequestReviewer.getHrisChangeRequest().getEmployee()));

			pendingItemsForm
					.setCreatedById(hrisChangeRequestReviewer.getHrisChangeRequest().getEmployee().getEmployeeId());
			pendingItemsForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(hrisChangeRequestReviewer.getHrisChangeRequest().getCreatedDate()));
			/* ID ENCRYPT */
			pendingItemsForm.setHrisChangeRequestReviewerId(
					FormatPreserveCryptoUtil.encrypt(hrisChangeRequestReviewer.getHrisChangeRequestReviewerId()));
			String multilingualFieldLabel = multilingualLogic.convertFieldLabelToSpecificLanguage(languageId,
					hrisChangeRequestReviewer.getHrisChangeRequest().getDataDictionary().getCompany().getCompanyId(),
					hrisChangeRequestReviewer.getHrisChangeRequest().getDataDictionary().getDataDictionaryId());
			if (StringUtils.isBlank(multilingualFieldLabel)) {
				multilingualFieldLabel = hrisChangeRequestReviewer.getHrisChangeRequest().getDataDictionary()
						.getLabel();
			}
			pendingItemsForm.setWorkflowTypeName(multilingualFieldLabel);
			pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_HRIS_PENDING_DATA_CHANGE_REQUEST);
			pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(
					DateUtils.timeStampToString(hrisChangeRequestReviewer.getHrisChangeRequest().getUpdatedDate())));
			recentActivityDTOList.add(pendingItemsForm);

		}

		Collections.sort(recentActivityDTOList, Collections.reverseOrder());
		employeeHomePageRes.setRecentActivityDTOList(recentActivityDTOList);

		return employeeHomePageRes;
	}

	private void setCoherentTimesheetNotification(Long employeeId, List<RecentActivityDTO> recentActivityDTOList) {
		LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
		otTimesheetConditionDTO.setEmployeeId(employeeId);

		List<String> otStatusNames = new ArrayList<>();
		otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
		otTimesheetConditionDTO.setStatusNameList(otStatusNames);

		List<CoherentOvertimeApplication> pendingOvertimeTimesheet = coherentOvertimeApplicationDAO
				.findByConditionSubmitted(otTimesheetConditionDTO, null, null);

		for (CoherentOvertimeApplication overtimeTimesheet : pendingOvertimeTimesheet) {
			RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
			/* ID ENCRYPT */
			recentActivityDTO.setLundinTimesheetId(
					FormatPreserveCryptoUtil.encrypt(overtimeTimesheet.getOvertimeApplicationID()));
			recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(overtimeTimesheet.getCreatedDate()));

			recentActivityDTO.setWorkflowTypeName(
					overtimeTimesheet.getTimesheetBatch().getTimesheetBatchDesc() + " " + "Timesheet");
			recentActivityDTO.setField(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
			recentActivityDTO.setActivityType(PayAsiaConstants.PAYASIA_COHERENT_TIMESHEET_SUBMITTED);
			recentActivityDTO.setUpdatedDate(overtimeTimesheet.getUpdatedDate());
			recentActivityDTOList.add(recentActivityDTO);

		}

		List<CoherentShiftApplication> pendingOvertimeTimesheets = coherentShiftApplicationDAO
				.findByConditionSubmitted(otTimesheetConditionDTO, null, null);

		for (CoherentShiftApplication overtimeTimesheet : pendingOvertimeTimesheets) {
			RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
			/* ID ENCRYPT */
			recentActivityDTO
					.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(overtimeTimesheet.getShiftApplicationID()));
			recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(overtimeTimesheet.getCreatedDate()));

			recentActivityDTO.setWorkflowTypeName(
					overtimeTimesheet.getTimesheetBatch().getTimesheetBatchDesc() + " " + "Timesheet");
			recentActivityDTO.setField(PayAsiaConstants.COHERENT_SHIFT_TYPE);
			recentActivityDTO.setActivityType("SubmittedCoherenShiftTimesheet");
			recentActivityDTO.setUpdatedDate(overtimeTimesheet.getUpdatedDate());
			recentActivityDTOList.add(recentActivityDTO);

		}

		List<CoherentOvertimeApplicationReviewer> overtimeReviewerList = coherentOvertimeApplicationReviewerDAO
				.findByCondition(employeeId, null, null);

		for (CoherentOvertimeApplicationReviewer overtimeReviewerVO : overtimeReviewerList) {
			RecentActivityDTO pendingItemsForm = new RecentActivityDTO();
			/* ID ENCRYPT */
			pendingItemsForm.setLundinTimesheetId(FormatPreserveCryptoUtil
					.encrypt(overtimeReviewerVO.getCoherentOvertimeApplication().getOvertimeApplicationID()));

			pendingItemsForm
					.setCreatedBy(getEmployeeName(overtimeReviewerVO.getCoherentOvertimeApplication().getEmployee()));

			pendingItemsForm
					.setCreatedById(overtimeReviewerVO.getCoherentOvertimeApplication().getEmployee().getEmployeeId());
			pendingItemsForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(overtimeReviewerVO.getCoherentOvertimeApplication().getCreatedDate()));
			pendingItemsForm.setLundinTimesheetReviewerId(overtimeReviewerVO.getOvertimeApplciationReviewerID());
			pendingItemsForm.setWorkflowTypeName(
					overtimeReviewerVO.getCoherentOvertimeApplication().getTimesheetBatch().getTimesheetBatchDesc());
			pendingItemsForm.setField(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
			pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_COHERENT_TIMESHEET_PENDING);
			pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(
					DateUtils.timeStampToString(overtimeReviewerVO.getCoherentOvertimeApplication().getUpdatedDate())));
			recentActivityDTOList.add(pendingItemsForm);

		}

		List<CoherentShiftApplicationReviewer> overtimeReviewerLists = coherentShiftApplicationReviewerDAO
				.findByCondition(employeeId, null, null);

		for (CoherentShiftApplicationReviewer overtimeReviewerVO : overtimeReviewerLists) {
			RecentActivityDTO pendingItemsForm = new RecentActivityDTO();
			/* ID ENCRYPT */
			pendingItemsForm.setLundinTimesheetId(FormatPreserveCryptoUtil
					.encrypt(overtimeReviewerVO.getCoherentShiftApplication().getShiftApplicationID()));

			pendingItemsForm
					.setCreatedBy(getEmployeeName(overtimeReviewerVO.getCoherentShiftApplication().getEmployee()));

			pendingItemsForm
					.setCreatedById(overtimeReviewerVO.getCoherentShiftApplication().getEmployee().getEmployeeId());
			pendingItemsForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(overtimeReviewerVO.getCoherentShiftApplication().getCreatedDate()));
			pendingItemsForm.setLundinTimesheetReviewerId(overtimeReviewerVO.getShiftApplicationReviewerID());
			pendingItemsForm.setWorkflowTypeName(
					overtimeReviewerVO.getCoherentShiftApplication().getTimesheetBatch().getTimesheetBatchDesc());
			pendingItemsForm.setField(PayAsiaConstants.COHERENT_SHIFT_TYPE);
			pendingItemsForm.setActivityType("PendingCoherenShiftTimesheet");
			pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(
					DateUtils.timeStampToString(overtimeReviewerVO.getCoherentShiftApplication().getUpdatedDate())));
			recentActivityDTOList.add(pendingItemsForm);

		}

	}

	@Override
	public EmployeeHomePageForm generateEmployeeActivationCode(Long employeeId) {
		EmployeeHomePageForm employeeHomePageForm = new EmployeeHomePageForm();
		Employee employee = employeeDAO.findById(employeeId);
		EmployeeActivationCode employeeActivationCode = new EmployeeActivationCode();
		String activationCode = String.valueOf(employee.getEmployeeId()) + PasswordUtils.getRandomPassword(3);
		employeeActivationCode.setActivationCode(activationCode);
		employeeHomePageForm.setActivationCode(activationCode);
		employeeActivationCode.setEmployee(employee);
		employeeActivationCodeDAO.save(employeeActivationCode);
		return employeeHomePageForm;
	}

	@Override
	public List<EmployeeFilterListForm> getDefaultEmailCCListByEmployee(Long companyId, Long employeeId,
			String moduleName, boolean moduleEnabled) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		ModuleMaster moduleMaster = null;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
			if (claimPreferenceVO == null) {
				return employeeFilterList;
			}
			if (claimPreferenceVO != null && !claimPreferenceVO.isDefaultEmailCC()) {
				return employeeFilterList;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
			if (leavePreferenceVO == null) {
				return employeeFilterList;
			}
			if (leavePreferenceVO != null && !leavePreferenceVO.isDefaultEmailCC()) {
				return employeeFilterList;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
		}
		if (moduleMaster != null) {
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(companyId,
					employeeId, moduleMaster.getModuleId());
			if (employeeDefaultEmailCCVO != null) {
				String[] emailCCArray = employeeDefaultEmailCCVO.getEmailCC().split(";");
				for (int count = 0; count < emailCCArray.length; count++) {
					EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
					employeeFilterListForm.setValue(emailCCArray[count]);
					employeeFilterList.add(employeeFilterListForm);
				}
			}

		}
		return employeeFilterList;
	}

	@Override
	public void saveDefaultEmailCCByEmployee(Long companyId, Long employeeId, String ccEmailIds, String moduleName,
			boolean moduleEnabled) {
		Company company = companyDAO.findById(companyId);
		boolean isDefaultEmailCC = true;
		ModuleMaster moduleMaster = null;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
			if (claimPreferenceVO == null) {
				isDefaultEmailCC = false;
			}
			if (claimPreferenceVO != null && !claimPreferenceVO.isDefaultEmailCC()) {
				isDefaultEmailCC = false;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
			if (leavePreferenceVO == null) {
				isDefaultEmailCC = false;
			}
			if (leavePreferenceVO != null && !leavePreferenceVO.isDefaultEmailCC()) {
				isDefaultEmailCC = false;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
		}
		if (moduleMaster != null && isDefaultEmailCC) {
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(companyId,
					employeeId, moduleMaster.getModuleId());
			String emailCC = "";
			String[] emailCCArray = ccEmailIds.split(";");
			for (int count = 0; count < emailCCArray.length; count++) {
				if (StringUtils.isNotBlank(emailCCArray[count])) {
					emailCC += emailCCArray[count] + ";";
				}
			}

			if (employeeDefaultEmailCCVO == null) {
				if (StringUtils.isNotBlank(emailCC)) {
					employeeDefaultEmailCCVO = new EmployeeDefaultEmailCC();
					employeeDefaultEmailCCVO.setCompany(company);
					employeeDefaultEmailCCVO.setModuleMaster(moduleMaster);
					Employee employee = employeeDAO.findById(employeeId);
					employeeDefaultEmailCCVO.setEmployee(employee);
					employeeDefaultEmailCCVO.setEmailCC(emailCC);
					employeeDefaultEmailCCDAO.save(employeeDefaultEmailCCVO);
				}
			} else {
				if (StringUtils.isNotBlank(emailCC)) {
					employeeDefaultEmailCCVO.setEmailCC(emailCC);
					employeeDefaultEmailCCDAO.update(employeeDefaultEmailCCVO);
				} else {
					employeeDefaultEmailCCDAO.delete(employeeDefaultEmailCCVO);
				}
			}

		}
	}

	@Override
	public String getDefaultEmailCCByEmp(Long companyId, Long employeeId, String moduleName, boolean moduleEnabled) {
		String emailCC = "";
		ModuleMaster moduleMaster = null;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
			if (claimPreferenceVO == null) {
				return emailCC;
			}
			if (claimPreferenceVO != null && !claimPreferenceVO.isDefaultEmailCC()) {
				return emailCC;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
			if (leavePreferenceVO == null) {
				return emailCC;
			}
			if (leavePreferenceVO != null && !leavePreferenceVO.isDefaultEmailCC()) {
				return emailCC;
			}
			moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
		}
		if (moduleMaster != null) {
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(companyId,
					employeeId, moduleMaster.getModuleId());
			if (employeeDefaultEmailCCVO != null) {
				emailCC = employeeDefaultEmailCCVO.getEmailCC();
			}
		}
		return emailCC;
	}

	/*
	 * ADDED FOR RECENT ACTIVITIES COUNT
	 */

	@Override
	public Map<String, Object> getAllRecentActivityListCount(Long companyId, Long employeeId,
			CompanyModuleDTO companyModuleDTO, Long languageId, String requestType,List<String> listOfPrivilege, List<String> listOfRoles) {

		Map<String, Object> finalRecentActivityMap = new HashMap<String, Object>();
		Map<String, Object> finalSubmittedMap = new HashMap<String, Object>();
		Integer submitTotal = 0;
		Integer total = 0;
		Integer all;

		// 1
		if (companyModuleDTO.isHasClaimModule()) {

			PendingClaimConditionDTO claimConditionDTO = new PendingClaimConditionDTO();

			List<ClaimApplicationReviewer> claimApplicationReviewers = claimApplicationReviewerDAO
					.findByCondition(employeeId, null, null, claimConditionDTO);
			total = total + claimApplicationReviewers.size();

			if (requestType != null && requestType.equalsIgnoreCase("pending")) {

				finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_CLAIM_PENDING_CLAIM,
						claimApplicationReviewers.size());
			} else {
				if(listOfRoles.contains("ROLE_CLAIM_MANAGER") && listOfPrivilege.contains("PRIV_PENDING_CLAIM")) {		
					finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_CLAIM_PENDING_CLAIM,
							"Pending-Claim Apps - [" + claimApplicationReviewers.size() + "]");
				}

				AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();
				conditionDTO.setEmployeeId(employeeId);
				List<String> claimStatus = new ArrayList<>();
				claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
				claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
				conditionDTO.setClaimStatus(claimStatus);
				conditionDTO.setVisibleToEmployee(true);

				List<ClaimApplication> submittedClaims = claimApplicationDAO.findByCondition(conditionDTO, null, null);
				finalSubmittedMap.put(PayAsiaConstants.PAYASIA_CLAIM_SUBMITTED_CLAIM, submittedClaims.size());

				submitTotal = submitTotal + submittedClaims.size();
			}

		}

		// 2
		if (companyModuleDTO.isHasLeaveModule()) {

			List<LeaveApplicationReviewer> leaveReviewerList = leaveApplicationReviewerDAO.findByCondition(employeeId,
					null, null);
			total = total + leaveReviewerList.size();

			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("pending")) {

				finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_LEAVE_PENDING_LEAVE, leaveReviewerList.size());

			} else {
				if (listOfRoles.contains("ROLE_LEAVE_MANAGER") && listOfPrivilege.contains("PRIV_PENDING_ITEMS")) {
					finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_LEAVE_PENDING_LEAVE,
							"Pending-Leave Apps - [" + leaveReviewerList.size() + "]");
				}

				AddLeaveConditionDTO addLeaveConditionDTO = new AddLeaveConditionDTO();
				addLeaveConditionDTO.setEmployeeId(employeeId);
				List<String> leaveStatusNames = new ArrayList<>();
				leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				addLeaveConditionDTO.setLeaveStatusNames(leaveStatusNames);

				List<LeaveApplication> submittedLeaves = leaveApplicationDAO
						.findByConditionSubmitted(addLeaveConditionDTO, null, null);
				finalSubmittedMap.put(PayAsiaConstants.PAYASIA_LEAVE_SUBMITTED_LEAVE, submittedLeaves.size());

				submitTotal = submitTotal + submittedLeaves.size();
			}
		}

		// 3
		if (companyModuleDTO.isHasLundinTimesheetModule()) {

			List<TimesheetApplicationReviewer> otTimesheetReviewerList = lundinTimesheetReviewerDAO
					.findByCondition(employeeId, null, null);
			total = total + otTimesheetReviewerList.size();

			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("pending")) {

				finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_LUNDIN_TIMESHEET_PENDING,
						otTimesheetReviewerList.size());
			} else {
				if (listOfRoles.contains("ROLE_LUNDIN_MANAGER") && listOfPrivilege.contains("PRIV_LUNDIN_PENDING_TIMESHEET")) {
					finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_LUNDIN_TIMESHEET_PENDING,
							"Pending-Lundin Timesheet Apps - [" + otTimesheetReviewerList.size() + "]");
				}

				LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
				otTimesheetConditionDTO.setEmployeeId(employeeId);
				List<String> otStatusNames = new ArrayList<>();
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				otTimesheetConditionDTO.setStatusNameList(otStatusNames);

				List<EmployeeTimesheetApplication> pendingOTTimesheet = lundinTimesheetDAO
						.findByConditionSubmitted(otTimesheetConditionDTO, null, null);
				finalSubmittedMap.put(PayAsiaConstants.PAYASIA_LUNDIN_TIMESHEET_SUBMITTED, pendingOTTimesheet.size());

				submitTotal = submitTotal + pendingOTTimesheet.size();
			}
		}

		// 4
		if (companyModuleDTO.isHasLionTimesheetModule()) {

			List<LionTimesheetApplicationReviewer> otTimesheetReviewerList = lionTimesheetApplicationReviewerDAO
					.findByCondition(employeeId, companyId);
			total = total + otTimesheetReviewerList.size();

			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("pending")) {

				finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_LION_TIMESHEET_PENDING,
						otTimesheetReviewerList.size());
			} else {
				if (listOfRoles.contains("ROLE_LION_MANAGER") && listOfPrivilege.contains("PRIV_LION_EMPLOYEE_TIMESHEET")) {
					finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_LION_TIMESHEET_PENDING,
							"Pending-Lion Timesheet Apps - [" + otTimesheetReviewerList.size() + "]");
				}

				LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
				otTimesheetConditionDTO.setEmployeeId(employeeId);
				List<String> otStatusNames = new ArrayList<>();
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				otTimesheetConditionDTO.setStatusNameList(otStatusNames);

				List<EmployeeTimesheetApplication> pendingOTTimesheet = lundinTimesheetDAO
						.findByConditionSubmitted(otTimesheetConditionDTO, null, null);
				finalSubmittedMap.put(PayAsiaConstants.PAYASIA_LION_TIMESHEET_SUBMITTED, pendingOTTimesheet.size());

				submitTotal = submitTotal + pendingOTTimesheet.size();
			}
		}

		// 5
		if (companyModuleDTO.isHasHrisModule()) {

			List<HRISChangeRequestReviewer> hrisReviewerList = hrisChangeRequestReviewerDAO.findByCondition(employeeId,
					null, null);
			total = total + hrisReviewerList.size();

			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("pending")) {

				finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_HRIS_PENDING_DATA_CHANGE_REQUEST,
						hrisReviewerList.size());
			} else {
				if (listOfRoles.contains("ROLE_HRIS_MANAGER") && listOfPrivilege.contains("PRIV_HRIS_PENDING_ITEMS")) {
					finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_HRIS_PENDING_DATA_CHANGE_REQUEST,
							"Pending-HRIS Apps - [" + hrisReviewerList.size() + "]");
				}

				HrisPendingItemsConditionDTO hrisPendingItemsConditionDTO = new HrisPendingItemsConditionDTO();
				hrisPendingItemsConditionDTO.setEmployeeId(employeeId);
				List<String> hrisStatusNames = new ArrayList<>();
				hrisStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				hrisStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				hrisPendingItemsConditionDTO.setHrisStatusNames(hrisStatusNames);

				List<HRISChangeRequest> pendingChangeRequest = hrisChangeRequestDAO
						.findByConditionSubmitted(hrisPendingItemsConditionDTO, null, null);
				finalSubmittedMap.put(PayAsiaConstants.PAYASIA_HRIS_SUBMITTED_DATA_CHANGE_REQUEST,
						pendingChangeRequest.size());

				submitTotal = submitTotal + pendingChangeRequest.size();
			}
		}

		// 6
		if (companyModuleDTO.isHasCoherentTimesheetModule()) {

			List<CoherentOvertimeApplicationReviewer> overtimeReviewerList = coherentOvertimeApplicationReviewerDAO
					.findByCondition(employeeId, null, null);
			total = total + overtimeReviewerList.size();

			List<CoherentShiftApplicationReviewer> overtimeReviewerLists = coherentShiftApplicationReviewerDAO
					.findByCondition(employeeId, null, null);
			total = total + overtimeReviewerLists.size();

			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("pending")) {

				finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_COHERENT_TIMESHEET_PENDING,
						overtimeReviewerList.size() + overtimeReviewerLists.size());
			} else {
				if (listOfRoles.contains("ROLE_COHERENT_MANAGER") && listOfPrivilege.contains("PRIV_COHERENT_EMPLOYEE_OVERTIME")) {
					finalRecentActivityMap.put(PayAsiaConstants.PAYASIA_COHERENT_TIMESHEET_PENDING,
							"Pending Coherent Timesheet Apps - [" + overtimeReviewerList.size()
									+ overtimeReviewerLists.size() + "]");
				}

				LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
				otTimesheetConditionDTO.setEmployeeId(employeeId);

				List<String> otStatusNames = new ArrayList<>();
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				otTimesheetConditionDTO.setStatusNameList(otStatusNames);

				List<CoherentOvertimeApplication> pendingOvertimeTimesheet = coherentOvertimeApplicationDAO
						.findByConditionSubmitted(otTimesheetConditionDTO, null, null);
				submitTotal = submitTotal + pendingOvertimeTimesheet.size();

				List<CoherentShiftApplication> pendingOvertimeTimesheets = coherentShiftApplicationDAO
						.findByConditionSubmitted(otTimesheetConditionDTO, null, null);

				finalSubmittedMap.put(PayAsiaConstants.PAYASIA_COHERENT_TIMESHEET_SUBMITTED,
						pendingOvertimeTimesheet.size() + pendingOvertimeTimesheets.size());

				submitTotal = submitTotal + pendingOvertimeTimesheets.size();
			}
		}

		if ((requestType == null || requestType.isEmpty())) {
			all = total + submitTotal;
			finalRecentActivityMap.put("Submitted", "My Request - [" + submitTotal + "]");
			finalRecentActivityMap.put("All", "All - [" + all + "]");
		} else if ((requestType != null && !requestType.isEmpty() && !requestType.equalsIgnoreCase("pending")
				&& !requestType.equalsIgnoreCase("submitted"))) {
			return null;
		} else if ((requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("submitted"))) {
			return finalSubmittedMap;
		}
		return finalRecentActivityMap;
	}

	/*
	 * ADDED FOR RECENT ACTIVITIES DATA
	 */

	@Override
	public EmployeeHomePageForm getRecentActivityList(Long companyId, Long employeeId,
			CompanyModuleDTO companyModuleDTO, Long languageId, String recentActivityType, PageRequest pageDTO,
			SortCondition sortDTO,List<String> listOfPrivilege, List<String> listOfRole) {

//		List<RecentActivityDTO> recentSubmittedActivityDTOList = new ArrayList<RecentActivityDTO>();
//		List<RecentActivityDTO> recentPendingActivityDTOList = new ArrayList<RecentActivityDTO>();
		List<RecentActivityDTO> recentTotalActivityDTOList = new ArrayList<RecentActivityDTO>();

		// 1
		if (companyModuleDTO.isHasClaimModule()) {

			if ((listOfPrivilege.contains("PRIV_MY_CLAIM")) && (recentActivityType.equalsIgnoreCase("Submitted") || recentActivityType.equalsIgnoreCase("All"))) {

				AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();
				conditionDTO.setEmployeeId(employeeId);
				List<String> claimStatus = new ArrayList<>();
				claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
				claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
				conditionDTO.setClaimStatus(claimStatus);
				conditionDTO.setVisibleToEmployee(true);

				List<ClaimApplication> pendingClaims = claimApplicationDAO.findByCondition(conditionDTO, null, null);

				for (ClaimApplication claimApplication : pendingClaims) {
					RecentActivityDTO addClaimForm = new RecentActivityDTO();

					addClaimForm.setClaimTemplateName(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
					ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimApplication.getEmployee().getEmployeeId());
					addClaimForm.setCreatedDate(claimApplicationWorkflow == null
							? DateUtils.timeStampToString(claimApplication.getCreatedDate())
							: DateUtils.timeStampToString(claimApplicationWorkflow.getCreatedDate()));

					addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));
					addClaimForm.setActivityType(PayAsiaConstants.PAYASIA_CLAIM_SUBMITTED_CLAIM);
					addClaimForm.setUpdatedDate(claimApplication.getUpdatedDate());

//					recentSubmittedActivityDTOList.add(addClaimForm);
					recentTotalActivityDTOList.add(addClaimForm);
				}
			}

			if ((listOfRole.contains("ROLE_CLAIM_MANAGER")) && (listOfPrivilege.contains("PRIV_PENDING_CLAIM")) && (recentActivityType.equalsIgnoreCase("PendingClaim") || recentActivityType.equalsIgnoreCase("All"))) {

				PendingClaimConditionDTO claimConditionDTO = new PendingClaimConditionDTO();

				List<ClaimApplicationReviewer> claimApplicationReviewers = claimApplicationReviewerDAO.findByCondition(employeeId, null, null, claimConditionDTO);

				for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
					RecentActivityDTO pendingClaimsForm = new RecentActivityDTO();

					ClaimApplication claimApplication = claimApplicationReviewer.getClaimApplication();
					ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(claimApplication.getClaimApplicationId(), claimApplication.getEmployee().getEmployeeId());
					
					pendingClaimsForm.setCreatedBy(getEmployeeName(claimApplication.getEmployee()));
					pendingClaimsForm.setCreatedById(claimApplication.getEmployee().getEmployeeId());
					pendingClaimsForm.setCreatedDate(DateUtils.timeStampToString(claimApplicationWorkflow.getCreatedDate()));
					pendingClaimsForm.setClaimTemplateName(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
					pendingClaimsForm.setClaimApplicationReviewerId(FormatPreserveCryptoUtil.encrypt(claimApplicationReviewer.getClaimApplicationReviewerId()));
					pendingClaimsForm.setActivityType(PayAsiaConstants.PAYASIA_CLAIM_PENDING_CLAIM);
					pendingClaimsForm.setUpdatedDate(claimApplication.getUpdatedDate());

					try {
						byte[] byteFile = employeeDetailLogic.getEmployeeImage(claimApplicationReviewer.getClaimApplication().getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight);

						if (byteFile != null) {
							pendingClaimsForm.setEmployeeImage(byteFile);
						} 
					} catch (IOException e) {
					}
					
//					recentPendingActivityDTOList.add(pendingClaimsForm);
					recentTotalActivityDTOList.add(pendingClaimsForm);
				}
			}
		}
		
		// 2
		if (companyModuleDTO.isHasLeaveModule()) {

			if ((listOfPrivilege.contains("PRIV_MY_REQUESTS")) && (recentActivityType.equalsIgnoreCase("Submitted") || recentActivityType.equalsIgnoreCase("All"))) {

				AddLeaveConditionDTO addLeaveConditionDTO = new AddLeaveConditionDTO();
				addLeaveConditionDTO.setEmployeeId(employeeId);
				List<String> leaveStatusNames = new ArrayList<>();
				leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				addLeaveConditionDTO.setLeaveStatusNames(leaveStatusNames);
				
				List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionSubmitted(addLeaveConditionDTO, null, null);

				for (LeaveApplication leaveApplication : pendingLeaves) {
					RecentActivityDTO addLeaveForm = new RecentActivityDTO();
					/* ID ENCRYPT */
					addLeaveForm.setLeaveApplicationId(	FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
					addLeaveForm.setLeaveType(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
					addLeaveForm.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
					addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
					addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
					addLeaveForm.setActivityType(PayAsiaConstants.PAYASIA_LEAVE_SUBMITTED_LEAVE);
					addLeaveForm.setUpdatedDate(leaveApplication.getUpdatedDate());

					recentTotalActivityDTOList.add(addLeaveForm);
				}
			}
			
			if ((listOfRole.contains("ROLE_LEAVE_MANAGER")) && (listOfPrivilege.contains("PRIV_PENDING_ITEMS")) && (recentActivityType.equalsIgnoreCase("PendingLeave") || recentActivityType.equalsIgnoreCase("All"))) {
				
				List<LeaveApplicationReviewer> leaveReviewerList = leaveApplicationReviewerDAO.findByCondition(employeeId, null, null);

				for (LeaveApplicationReviewer leaveApplicationReviewer : leaveReviewerList) {
					RecentActivityDTO pendingItemsForm = new RecentActivityDTO();

					if (leaveApplicationReviewer.getLeaveApplication().getLeaveCancelApplication() != null) {
						pendingItemsForm.setIsCancelApplication(true);
					} else {
						pendingItemsForm.setIsCancelApplication(false);
					}
					pendingItemsForm.setLeaveApplicationId(leaveApplicationReviewer.getLeaveApplication().getLeaveApplicationId());
					pendingItemsForm.setCreatedBy(getEmployeeName(leaveApplicationReviewer.getLeaveApplication().getEmployee()));
					pendingItemsForm.setWorkflowTypeName(leaveApplicationReviewer.getLeaveApplication().getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
					pendingItemsForm.setCreatedById(leaveApplicationReviewer.getLeaveApplication().getEmployee().getEmployeeId());
					pendingItemsForm.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplicationReviewer.getLeaveApplication().getCreatedDate()));
					pendingItemsForm.setFromDate(DateUtils.timeStampToString(leaveApplicationReviewer.getLeaveApplication().getStartDate()));
					pendingItemsForm.setToDate(DateUtils.timeStampToString(leaveApplicationReviewer.getLeaveApplication().getEndDate()));
					pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.encrypt(leaveApplicationReviewer.getLeaveApplicationReviewerId()));
					pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_LEAVE_PENDING_LEAVE);
					pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(DateUtils.timeStampToString(leaveApplicationReviewer.getLeaveApplication().getUpdatedDate())));

					try {
						byte[] byteFile = employeeDetailLogic.getEmployeeImage(leaveApplicationReviewer.getLeaveApplication().getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight);

						if (byteFile != null) {
							pendingItemsForm.setEmployeeImage(byteFile);
						} 
					} catch (IOException e) {
					}
					
					recentTotalActivityDTOList.add(pendingItemsForm);
				}
			}
		}

		// 3
		if (companyModuleDTO.isHasLundinTimesheetModule()) {

			if ((listOfPrivilege.contains("PRIV_LUNDIN_TIMESHEET")) && (recentActivityType.equalsIgnoreCase("Submitted") || recentActivityType.equalsIgnoreCase("All"))) {

				LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
				otTimesheetConditionDTO.setEmployeeId(employeeId);
				List<String> otStatusNames = new ArrayList<>();
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				otTimesheetConditionDTO.setStatusNameList(otStatusNames);

				List<EmployeeTimesheetApplication> pendingOTTimesheet = lundinTimesheetDAO.findByConditionSubmitted(otTimesheetConditionDTO, null, null);

				for (EmployeeTimesheetApplication lundinTimesheet : pendingOTTimesheet) {
					RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
					// ID ENCRYPT
					recentActivityDTO.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(lundinTimesheet.getTimesheetId()));
					recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(lundinTimesheet.getCreatedDate()));
					recentActivityDTO.setWorkflowTypeName(lundinTimesheet.getTimesheetBatch().getTimesheetBatchDesc());
					recentActivityDTO.setActivityType(PayAsiaConstants.PAYASIA_LUNDIN_TIMESHEET_SUBMITTED);
					recentActivityDTO.setUpdatedDate(lundinTimesheet.getUpdatedDate());

//					recentSubmittedActivityDTOList.add(recentActivityDTO);
					recentTotalActivityDTOList.add(recentActivityDTO);
				}
			}

			if ((listOfRole.contains("ROLE_LUNDIN_MANAGER")) && (listOfPrivilege.contains("PRIV_LUNDIN_PENDING_TIMESHEET")) && (recentActivityType.equalsIgnoreCase("PendingLundinTimesheet") || recentActivityType.equalsIgnoreCase("All"))) {

				List<TimesheetApplicationReviewer> otTimesheetReviewerList = lundinTimesheetReviewerDAO.findByCondition(employeeId, null, null);

				for (TimesheetApplicationReviewer otTimesheetReviewer : otTimesheetReviewerList) {
					RecentActivityDTO pendingItemsForm = new RecentActivityDTO();
					// ID ENCRYPT
					pendingItemsForm.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(otTimesheetReviewer.getEmployeeTimesheetApplication().getTimesheetId()));
					pendingItemsForm.setCreatedBy(getEmployeeName(otTimesheetReviewer.getEmployeeTimesheetApplication().getEmployee()));
					pendingItemsForm.setCreatedById(otTimesheetReviewer.getEmployeeTimesheetApplication().getEmployee().getEmployeeId());
					pendingItemsForm.setCreatedDate(DateUtils.timeStampToStringWithTime(otTimesheetReviewer.getEmployeeTimesheetApplication().getCreatedDate()));
					pendingItemsForm.setLundinTimesheetReviewerId(otTimesheetReviewer.getTimesheetReviewerId());
					pendingItemsForm.setWorkflowTypeName(otTimesheetReviewer.getEmployeeTimesheetApplication().getTimesheetBatch().getTimesheetBatchDesc());
					pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_LUNDIN_TIMESHEET_PENDING);
					pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(DateUtils.timeStampToString(otTimesheetReviewer.getEmployeeTimesheetApplication().getUpdatedDate())));

//					recentPendingActivityDTOList.add(pendingItemsForm);
					recentTotalActivityDTOList.add(pendingItemsForm);
				}
			}
		}

		// 4
		if ((listOfPrivilege.contains("PRIV_LION_TIMESHEET")) && (companyModuleDTO.isHasLionTimesheetModule())) {

			if (recentActivityType.equalsIgnoreCase("Submitted") || recentActivityType.equalsIgnoreCase("All")) {

				LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
				otTimesheetConditionDTO.setEmployeeId(employeeId);
				List<String> otStatusNames = new ArrayList<>();
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				otTimesheetConditionDTO.setStatusNameList(otStatusNames);

				List<EmployeeTimesheetApplication> pendingOTTimesheet = lundinTimesheetDAO.findByConditionSubmitted(otTimesheetConditionDTO, null, null);
				
				for (EmployeeTimesheetApplication lundinTimesheet : pendingOTTimesheet) {
					RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
					/* ID ENCRYPT */
					recentActivityDTO.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(lundinTimesheet.getTimesheetId()));
					recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(lundinTimesheet.getCreatedDate()));
					recentActivityDTO.setWorkflowTypeName(lundinTimesheet.getTimesheetBatch().getTimesheetBatchDesc());
					recentActivityDTO.setActivityType(PayAsiaConstants.PAYASIA_LION_TIMESHEET_SUBMITTED);
					recentActivityDTO.setUpdatedDate(lundinTimesheet.getUpdatedDate());

					recentTotalActivityDTOList.add(recentActivityDTO);
				}
			}

			if ((listOfRole.contains("ROLE_LION_MANAGER")) && (listOfPrivilege.contains("PRIV_LION_EMPLOYEE_TIMESHEET")) && (recentActivityType.equalsIgnoreCase("PendingLionTimesheet") || recentActivityType.equalsIgnoreCase("All"))) {

				List<LionTimesheetApplicationReviewer> otTimesheetReviewerList = lionTimesheetApplicationReviewerDAO.findByCondition(employeeId, companyId);

				for (LionTimesheetApplicationReviewer otTimesheetReviewer : otTimesheetReviewerList) {
					RecentActivityDTO pendingItemsForm = new RecentActivityDTO();

					List<String> employeeNBatchList = new ArrayList<String>();
					
					Employee createdBy = employeeDAO.findById(Long.parseLong(otTimesheetReviewer.getCreatedBy()));

					EmployeeTimesheetApplication employeeTimesheetApplication = lionEmployeeTimesheetApplicationDetailDAO
							.findById(otTimesheetReviewer.getLionEmployeeTimesheetApplicationDetail()
									.getEmployeeTimesheetDetailID())
							.getEmployeeTimesheetApplication();

					String employeeNBatch = employeeTimesheetApplication.getTimesheetBatch().getTimesheetBatchDesc() + createdBy.getEmployeeId();
					
					if (!employeeNBatchList.isEmpty() && employeeNBatchList.contains(employeeNBatch)) {
						continue;
					} else {
						employeeNBatchList.add(employeeNBatch);
					}
					
					/* ID ENCRYPT */
					pendingItemsForm.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(employeeTimesheetApplication.getTimesheetId()));
					pendingItemsForm.setCreatedBy(getEmployeeName(createdBy));
					pendingItemsForm.setCreatedById(createdBy.getEmployeeId());
					pendingItemsForm.setCreatedDate(DateUtils.timeStampToStringWithTime(otTimesheetReviewer.getCreatedDate()));
					pendingItemsForm.setLundinTimesheetReviewerId(otTimesheetReviewer.getEmployeeReviewer().getEmployeeId());
					pendingItemsForm.setWorkflowTypeName(employeeTimesheetApplication.getTimesheetBatch().getTimesheetBatchDesc());
					pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_LION_TIMESHEET_PENDING);
					pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(DateUtils.timeStampToString(otTimesheetReviewer.getUpdatedDate())));

					recentTotalActivityDTOList.add(pendingItemsForm);
				}
			}
		}
				
		// 5
		if (companyModuleDTO.isHasHrisModule()) {

			if ((listOfPrivilege.contains("PRIV_HRIS_MY_REQUEST")) && (recentActivityType.equalsIgnoreCase("Submitted") || recentActivityType.equalsIgnoreCase("All"))) {

				HrisPendingItemsConditionDTO hrisPendingItemsConditionDTO = new HrisPendingItemsConditionDTO();
				hrisPendingItemsConditionDTO.setEmployeeId(employeeId);
				List<String> hrisStatusNames = new ArrayList<>();
				hrisStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				hrisStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				hrisPendingItemsConditionDTO.setHrisStatusNames(hrisStatusNames);

				List<HRISChangeRequest> pendingChangeRequest = hrisChangeRequestDAO.findByConditionSubmitted(hrisPendingItemsConditionDTO, null, null);

				for (HRISChangeRequest hrisChangeRequest : pendingChangeRequest) {
					RecentActivityDTO recentActivityDTO = new RecentActivityDTO();

					// ID ENCRYPT
					recentActivityDTO.setHrisChangeRequestId(FormatPreserveCryptoUtil.encrypt(hrisChangeRequest.getHrisChangeRequestId()));
					recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(hrisChangeRequest.getCreatedDate()));
					String multilingualFieldLabel = multilingualLogic.convertFieldLabelToSpecificLanguage(languageId,
							hrisChangeRequest.getDataDictionary().getCompany().getCompanyId(),
							hrisChangeRequest.getDataDictionary().getDataDictionaryId());
					if (StringUtils.isBlank(multilingualFieldLabel)) {
						multilingualFieldLabel = hrisChangeRequest.getDataDictionary().getLabel();
					}

					recentActivityDTO.setWorkflowTypeName(multilingualFieldLabel);
					recentActivityDTO.setActivityType(PayAsiaConstants.PAYASIA_HRIS_SUBMITTED_DATA_CHANGE_REQUEST);
					recentActivityDTO.setUpdatedDate(hrisChangeRequest.getUpdatedDate());

					recentTotalActivityDTOList.add(recentActivityDTO);
				}
			}

			if ((listOfRole.contains("ROLE_HRIS_MANAGER")) && (listOfPrivilege.contains("PRIV_HRIS_PENDING_ITEMS")) &&(recentActivityType.equalsIgnoreCase("PendingHRISChangeRequest") || recentActivityType.equalsIgnoreCase("All"))) {

				List<HRISChangeRequestReviewer> hrisReviewerList = hrisChangeRequestReviewerDAO.findByCondition(employeeId, null, null);

				for (HRISChangeRequestReviewer hrisChangeRequestReviewer : hrisReviewerList) {
					RecentActivityDTO pendingItemsForm = new RecentActivityDTO();

					pendingItemsForm.setHrisChangeRequestId(hrisChangeRequestReviewer.getHrisChangeRequest().getHrisChangeRequestId());
					pendingItemsForm.setCreatedBy(getEmployeeName(hrisChangeRequestReviewer.getHrisChangeRequest().getEmployee()));
					pendingItemsForm.setCreatedById(hrisChangeRequestReviewer.getHrisChangeRequest().getEmployee().getEmployeeId());

					pendingItemsForm.setCreatedDate(DateUtils.timeStampToStringWithTime(hrisChangeRequestReviewer.getHrisChangeRequest().getCreatedDate()));

					// ID ENCRYPT
					pendingItemsForm.setHrisChangeRequestReviewerId(FormatPreserveCryptoUtil.encrypt(hrisChangeRequestReviewer.getHrisChangeRequestReviewerId()));
					String multilingualFieldLabel = multilingualLogic.convertFieldLabelToSpecificLanguage(languageId, hrisChangeRequestReviewer.getHrisChangeRequest().getDataDictionary().getCompany().getCompanyId(),
							hrisChangeRequestReviewer.getHrisChangeRequest().getDataDictionary().getDataDictionaryId());
					
					if (StringUtils.isBlank(multilingualFieldLabel)) {
						multilingualFieldLabel = hrisChangeRequestReviewer.getHrisChangeRequest().getDataDictionary().getLabel();
					}
					pendingItemsForm.setWorkflowTypeName(multilingualFieldLabel);
					pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_HRIS_PENDING_DATA_CHANGE_REQUEST);

					pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(DateUtils.timeStampToString(hrisChangeRequestReviewer.getHrisChangeRequest().getUpdatedDate())));

					try {
						byte[] byteFile = employeeDetailLogic.getEmployeeImage(hrisChangeRequestReviewer.getHrisChangeRequest().getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight);

						if (byteFile != null) {
							pendingItemsForm.setEmployeeImage(byteFile);
						} 
					} catch (IOException e) {
					}
					recentTotalActivityDTOList.add(pendingItemsForm);
				}
			}
		}
		
		// 6
		if (companyModuleDTO.isHasCoherentTimesheetModule()) {

			if ((listOfPrivilege.contains("PRIV_COHERENT_MY_OVERTIME_TIMESHEET")) && (recentActivityType.equalsIgnoreCase("Submitted") || recentActivityType.equalsIgnoreCase("All"))) {
				LundinPendingTsheetConditionDTO otTimesheetConditionDTO = new LundinPendingTsheetConditionDTO();
				otTimesheetConditionDTO.setEmployeeId(employeeId);

				List<String> otStatusNames = new ArrayList<>();
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				otStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				otTimesheetConditionDTO.setStatusNameList(otStatusNames);

				// i)
				List<CoherentOvertimeApplication> pendingOvertimeTimesheet = coherentOvertimeApplicationDAO.findByConditionSubmitted(otTimesheetConditionDTO, null, null);

				for (CoherentOvertimeApplication overtimeTimesheet : pendingOvertimeTimesheet) {
					RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
					/* ID ENCRYPT */
					recentActivityDTO.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(overtimeTimesheet.getOvertimeApplicationID()));
					recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(overtimeTimesheet.getCreatedDate()));
					recentActivityDTO.setWorkflowTypeName(overtimeTimesheet.getTimesheetBatch().getTimesheetBatchDesc() + " " + "Timesheet");
					recentActivityDTO.setField(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
					recentActivityDTO.setActivityType(PayAsiaConstants.PAYASIA_COHERENT_TIMESHEET_SUBMITTED);
					recentActivityDTO.setUpdatedDate(overtimeTimesheet.getUpdatedDate());

					recentTotalActivityDTOList.add(recentActivityDTO);
				}

				// ii)
				List<CoherentShiftApplication> pendingOvertimeTimesheets = coherentShiftApplicationDAO.findByConditionSubmitted(otTimesheetConditionDTO, null, null);

				for (CoherentShiftApplication overtimeTimesheet : pendingOvertimeTimesheets) {
					RecentActivityDTO recentActivityDTO = new RecentActivityDTO();
					/* ID ENCRYPT */
					recentActivityDTO.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(overtimeTimesheet.getShiftApplicationID()));
					recentActivityDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(overtimeTimesheet.getCreatedDate()));
					recentActivityDTO.setWorkflowTypeName(overtimeTimesheet.getTimesheetBatch().getTimesheetBatchDesc() + " " + "Timesheet");
					recentActivityDTO.setField(PayAsiaConstants.COHERENT_SHIFT_TYPE);
					recentActivityDTO.setActivityType("SubmittedCoherenShiftTimesheet");
					recentActivityDTO.setUpdatedDate(overtimeTimesheet.getUpdatedDate());

					recentTotalActivityDTOList.add(recentActivityDTO);
				}

			}

			if ((listOfRole.contains("ROLE_COHERENT_MANAGER")) && (listOfPrivilege.contains("PRIV_COHERENT_EMPLOYEE_OVERTIME")) && (recentActivityType.equalsIgnoreCase("PendingCoherentTimesheet") || recentActivityType.equalsIgnoreCase("All"))) {

				// i)
				List<CoherentOvertimeApplicationReviewer> overtimeReviewerList = coherentOvertimeApplicationReviewerDAO.findByCondition(employeeId, null, null);

				for (CoherentOvertimeApplicationReviewer overtimeReviewerVO : overtimeReviewerList) {
					RecentActivityDTO pendingItemsForm = new RecentActivityDTO();
					/* ID ENCRYPT */
					pendingItemsForm.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(overtimeReviewerVO.getCoherentOvertimeApplication().getOvertimeApplicationID()));
					pendingItemsForm.setCreatedBy(getEmployeeName(overtimeReviewerVO.getCoherentOvertimeApplication().getEmployee()));
					pendingItemsForm.setCreatedById(overtimeReviewerVO.getCoherentOvertimeApplication().getEmployee().getEmployeeId());
					pendingItemsForm.setCreatedDate(DateUtils.timeStampToStringWithTime(overtimeReviewerVO.getCoherentOvertimeApplication().getCreatedDate()));
					pendingItemsForm.setLundinTimesheetReviewerId(overtimeReviewerVO.getOvertimeApplciationReviewerID());
					pendingItemsForm.setWorkflowTypeName(overtimeReviewerVO.getCoherentOvertimeApplication().getTimesheetBatch().getTimesheetBatchDesc());
					pendingItemsForm.setField(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
					pendingItemsForm.setActivityType(PayAsiaConstants.PAYASIA_COHERENT_TIMESHEET_PENDING);
					pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(DateUtils.timeStampToString(overtimeReviewerVO.getCoherentOvertimeApplication().getUpdatedDate())));

					recentTotalActivityDTOList.add(pendingItemsForm);
				}

				// ii)
				List<CoherentShiftApplicationReviewer> overtimeReviewerLists = coherentShiftApplicationReviewerDAO.findByCondition(employeeId, null, null);

				for (CoherentShiftApplicationReviewer overtimeReviewerVO : overtimeReviewerLists) {
					RecentActivityDTO pendingItemsForm = new RecentActivityDTO();
					/* ID ENCRYPT */
					pendingItemsForm.setLundinTimesheetId(FormatPreserveCryptoUtil.encrypt(overtimeReviewerVO.getCoherentShiftApplication().getShiftApplicationID()));
					pendingItemsForm.setCreatedBy(getEmployeeName(overtimeReviewerVO.getCoherentShiftApplication().getEmployee()));
					pendingItemsForm.setCreatedById(overtimeReviewerVO.getCoherentShiftApplication().getEmployee().getEmployeeId());
					pendingItemsForm.setCreatedDate(DateUtils.timeStampToStringWithTime(overtimeReviewerVO.getCoherentShiftApplication().getCreatedDate()));
					pendingItemsForm.setLundinTimesheetReviewerId(overtimeReviewerVO.getShiftApplicationReviewerID());
					pendingItemsForm.setWorkflowTypeName(overtimeReviewerVO.getCoherentShiftApplication().getTimesheetBatch().getTimesheetBatchDesc());
					pendingItemsForm.setField(PayAsiaConstants.COHERENT_SHIFT_TYPE);
					pendingItemsForm.setActivityType("PendingCoherenShiftTimesheet");
					pendingItemsForm.setUpdatedDate(DateUtils.stringToDate(DateUtils.timeStampToString(overtimeReviewerVO.getCoherentShiftApplication().getUpdatedDate())));

					recentTotalActivityDTOList.add(pendingItemsForm);
				}
			}
		}

//		Collections.sort(recentTotalActivityDTOList, Collections.reverseOrder());
		EmployeeHomePageForm response = new EmployeeHomePageForm();
		
		if (StringUtils.equalsIgnoreCase(recentActivityType, "Submitted")) {
			sortRecentActivityData(response,sortDTO,pageDTO,recentTotalActivityDTOList);
			//response.setRecentActivityDTOList(recentTotalActivityDTOList);
			return response;
		}

		else if (StringUtils.equalsIgnoreCase(recentActivityType, "All")) {
			sortRecentActivityData(response,sortDTO,pageDTO,recentTotalActivityDTOList);
			//response.setRecentActivityDTOList(recentTotalActivityDTOList);
			return response;
		}

		else if (StringUtils.startsWithIgnoreCase(recentActivityType, "Pending")) {
			sortRecentActivityData(response,sortDTO,pageDTO,recentTotalActivityDTOList);
			//response.setRecentActivityDTOList(recentTotalActivityDTOList);
			return response;
		}

		return null;
	}
	
	private void sortRecentActivityData(EmployeeHomePageForm response, SortCondition sortDTO, PageRequest pageDTO, List<RecentActivityDTO> recentTotalActivityDTOList) {

		if (sortDTO != null && !StringUtils.isEmpty(sortDTO.getColumnName()) && !recentTotalActivityDTOList.isEmpty()) {
			Collections.sort(recentTotalActivityDTOList, new RecentActivityComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		List<RecentActivityDTO> finalRecentTotalActivityDTOList = new ArrayList<RecentActivityDTO>();

		if (pageDTO != null && !recentTotalActivityDTOList.isEmpty()) {
			int recordSizeFinal = recentTotalActivityDTOList.size();
			int pageSize = pageDTO.getPageSize();
			int startPos = (pageSize * ((pageDTO.getPageNumber()) - 1));

			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal){
				finalRecentTotalActivityDTOList = recentTotalActivityDTOList.subList(startPos, (startPos + pageSize));
			} else if (startPos <= recordSizeFinal) {
				finalRecentTotalActivityDTOList = recentTotalActivityDTOList.subList(startPos, recordSizeFinal);
			}
			
			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSizeFinal);
		}
		response.setRecentActivityDTOList(finalRecentTotalActivityDTOList);
	}
}
