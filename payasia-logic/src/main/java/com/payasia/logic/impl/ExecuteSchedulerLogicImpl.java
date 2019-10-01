/**
 * @author vivekjain
 *
 */
package com.payasia.logic.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeFieldDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.GrantEmployeeLeaveVO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.LionTimesheetDetailDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.dto.PayslipDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.LeaveReminderDTO;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.AnnouncementDAO;
import com.payasia.dao.ClaimTemplateItemDAO;
import com.payasia.dao.ClaimTemplateItemShortlistDAO;
import com.payasia.dao.CoherentOvertimeApplicationDAO;
import com.payasia.dao.CoherentOvertimeApplicationReviewerDAO;
import com.payasia.dao.CoherentShiftApplicationDAO;
import com.payasia.dao.CoherentShiftApplicationReviewerDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.CompanyPayslipReleaseDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmailAttachmentDAO;
import com.payasia.dao.EmailDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmployeeActivationCodeDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.ForgotPasswordTokenDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.IntegrationMasterDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.LeaveApplicationWorkflowDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveSchemeTypeAvailingLeaveDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveSchemeTypeShortListDAO;
import com.payasia.dao.LeaveSchemeTypeWorkflowDAO;
import com.payasia.dao.LeaveSessionMasterDAO;
import com.payasia.dao.LeaveStatusMasterDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.PrivilegeMasterDAO;
import com.payasia.dao.ReminderEventConfigDAO;
import com.payasia.dao.SchedulerMasterDAO;
import com.payasia.dao.SchedulerStatusDAO;
import com.payasia.dao.TimeZoneMasterDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.YearEndProcessScheduleDAO;
import com.payasia.dao.bean.Announcement;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemShortlist;
import com.payasia.dao.bean.CoherentOvertimeApplication;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationReviewer;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.CompanyModuleMapping;
import com.payasia.dao.bean.CompanyPayslipRelease;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.Email;
import com.payasia.dao.bean.EmailAttachment;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.IntegrationMaster;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave;
import com.payasia.dao.bean.LeaveSchemeTypeShortList;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.Payslip;
import com.payasia.dao.bean.ReminderEventConfig;
import com.payasia.dao.bean.RoleMaster_;
import com.payasia.dao.bean.SchedulerMaster;
import com.payasia.dao.bean.SchedulerStatus;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.YearEndProcessSchedule;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.ClaimTemplateLogic;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.ExecuteSchedulerLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.KeyPayIntLogic;
import com.payasia.logic.LeaveSchemeLogic;
import com.payasia.logic.PaySlipReleaseLogic;
import com.payasia.logic.PendingItemsLogic;

@Component
public class ExecuteSchedulerLogicImpl extends BaseLogic implements
		ExecuteSchedulerLogic {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger(ExecuteSchedulerLogicImpl.class);

	@Autowired
	private VelocityEngine velocityEngine;

	@Resource
	private LeaveApplicationDAO leaveApplicationDAO;

	@Resource
	private LeaveStatusMasterDAO leaveStatusMasterDAO;

	@Resource
	private ReminderEventConfigDAO reminderEventConfigDAO;

	@Resource
	private EmployeeDAO employeeDAO;

	@Resource
	private EmailDAO emailDAO;

	@Resource
	private EmailAttachmentDAO emailAttachementDAO;

	@Resource
	private EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	@Resource
	private PayAsiaMailUtils payAsiaMailUtils;

	@Resource
	private CompanyDAO companyDAO;

	@Resource
	private TimeZoneMasterDAO timeZoneMasterDAO;

	@Resource
	private SchedulerMasterDAO schedulerMasterDAO;

	@Resource
	private SchedulerStatusDAO schedulerStatusDAO;

	@Resource
	private LeavePreferenceDAO leavePreferenceDAO;

	@Resource
	private AddLeaveLogic addLeaveLogic;

	@Resource
	private EmployeeDetailLogic employeeDetailLogic;

	@Resource
	private LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	@Resource
	private EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;

	@Resource
	private LeaveSchemeLogic leaveSchemeLogic;

	@Resource
	private ClaimTemplateItemDAO claimTemplateItemDAO;

	@Resource
	private EmployeeClaimTemplateDAO employeeClaimTemplateDAO;

	@Resource
	private ClaimTemplateLogic claimTemplateLogic;

	@Resource
	private EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;

	@Resource
	private LeaveSchemeTypeShortListDAO leaveSchemeTypeShortListDAO;

	@Resource
	private ClaimTemplateItemShortlistDAO claimTemplateItemShortlistDAO;

	@Resource
	private DynamicFormDAO dynamicFormDAO;

	@Resource
	private EntityMasterDAO entityMasterDAO;

	@Resource
	private EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;

	@Resource
	private YearEndProcessScheduleDAO yearEndProcessScheduleDAO;

	@Resource
	private NotificationAlertDAO notificationAlertDAO;

	@Resource
	private EmployeeActivationCodeDAO employeeActivationCodeDAO;
	@Resource
	private ForgotPasswordTokenDAO forgotPasswordTokenDAO;

	@Resource
	private EmployeeTimesheetApplicationDAO lundinTimesheetDAO;

	@Resource
	private EmployeeRoleMappingDAO empployeeRoleMappindDAO;

	@Resource
	private PrivilegeMasterDAO privilegeMasterDAO;

	@Resource
	private EmployeeHolidayCalendarDAO employeeHolidayCalendarDAO;
	@Resource
	private KeyPayIntLogic keyPayIntLogic;
	@Resource
	IntegrationMasterDAO integrationMasterDAO;
	@Resource
	LeaveSchemeTypeWorkflowDAO leaveSchemeTypeWorkflowDAO;
	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewerDAO;
	@Resource
	LeaveSessionMasterDAO leaveSessionMasterDAO;
	@Resource
	LeaveApplicationWorkflowDAO leaveApplicationWorkflowDAO;
	@Resource
	GeneralMailLogic generalMailLogic;
	@Resource
	PendingItemsLogic pendingItemsLogic;
	@Resource
	CompanyInformationLogic companyInformationLogic;
	@Autowired
	private MessageSource messageSource;
	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetApplicationReviewerDAO;
	@Resource
	EmployeeTimesheetApplicationDAO employeeTimesheetApplicationDAO;
	@Resource
	TimesheetBatchDAO timesheetBatchDAO;
	@Resource
	CoherentOvertimeApplicationReviewerDAO coherentOvertimeApplicationReviewerDAO;
	@Resource
	CoherentShiftApplicationReviewerDAO coherentShiftApplicationReviewerDAO;
	@Resource
	CoherentOvertimeApplicationDAO coherentOvertimeApplicationDAO;
	@Resource
	CoherentShiftApplicationDAO coherentShiftApplicationDAO;
	@Resource
	LeaveSchemeTypeAvailingLeaveDAO leaveSchemeTypeAvailingLeaveDAO;

	@Resource
	FileUtils fileUtils;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Resource
	AWSS3Logic awss3LogicImpl;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;
	
	@Resource
	CompanyPayslipReleaseDAO companyPayslipReleaseDAO;
	
	@Resource
	PaySlipReleaseLogic paySlipReleaseLogic;
	
	@Resource
	AnnouncementDAO announcementDAO;
	
	@Resource
	DocumentCategoryMasterDAO documentCategoryMasterDAO;
	
	@Resource
	CompanyDocumentDAO companyDocumentDAO;
	
	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;
	
	@Resource
	PayslipDAO payslipDAO;
	
	@Resource
	MonthMasterDAO monthMasterDAO;
	
	@Resource
	DataImportLogic dataImportLogic;

	@Override
	public void executeScheduler(Date currentDate, Company company,
			String scheduler, SchedulerStatus schedulerStatusVO,
			SchedulerMaster scheduleMasterVO) {
		try {
			LOGGER.info("Scheduler By Name ::" + scheduler
					+ " has started running for company ::"
					+ company.getCompanyName() + "Dated :" + currentDate);
			switch (scheduler) {
			case PayAsiaConstants.SCHEDULE_MASTER_REMINDER_EVENT_MAIL_SCHEDULAR:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
					leaveEventReminderScheduler(currentDate, company);
				}
				break;

			case PayAsiaConstants.SCHEDULE_MASTER_LEAVE_GRANT_SCHEDULAR:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
					leaveGrantScheduler(currentDate, company);
				}
				break;

			case PayAsiaConstants.SCHEDULE_MASTER_FORFEIT_LEAVE_SCHEDULAR:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
					forfeitLeaveScheduler(currentDate, company);
				}
				break;

			case PayAsiaConstants.LEAVE_SCHEME_TYPE_SHORTLIST_SCHEDULAR:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
				
						leaveSchemeTypeShortListScheduler(currentDate, company);
					
				}
				break;

			case PayAsiaConstants.CLAIM_TEMPLATE_ITEM_SHORTLIST_SCHEDULAR:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
					claimTemplateItemShortList(currentDate, company);
				}
				break;

			case PayAsiaConstants.EMPLOYMENT_STATUS_UPDATE_SCHEDULAR:

				employmentStatusUpdateScheduler(company, currentDate);

				break;

			case PayAsiaConstants.YEAR_END_PROCESS_SCHEDULER:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
					yearEndProcessSchedular(company, currentDate);
				}
				break;

			case PayAsiaConstants.YEAR_END_LEAVE_ACTIVATE_SCHEDULER:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
					yearEndLeaveActivateSchedular(company, currentDate);
				}
				break;

			case PayAsiaConstants.LUNDIN_TIMESHEET_REMINDER_EVENT_MAIL_SCHEDULAR:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET)) {
					lundinTimesheetReminderScheduler(currentDate, company,
							PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET);
				}
				break;

			case PayAsiaConstants.KEYPAY_INTEGRATION_PROCESS_SCHEDULER:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LEAVE)
						&& hasCompanyLeaveModuleInHours(company)) {
					keyPayIntegrationProcessSchedular(company, currentDate);
				}
				break;

			case PayAsiaConstants.LEAVE_AUTO_APPROVAL_SCHEDULER:
				autoApprovalLeaveSchedular(currentDate, company);
				break;
			case PayAsiaConstants.LION_TIMESHEET_REMINDER_EVENT_MAIL_SCHEDULAR:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_LION_TIMESHEET)) {
					lionTimesheetReminderScheduler(currentDate, company);
				}
				break;
			case PayAsiaConstants.COHERENT_TIMESHEET_REMINDER_EVENT_MAIL_SCHEDULAR:
				if (hasCompanyModule(company,
						PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET)) {
					coherentTimesheetReminderScheduler(currentDate, company,
							PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET);
				}
				break;
			case PayAsiaConstants.LEAVE_TYPE_ACTIVATION_SCHEDULER:
				leaveTypeActivationScheduler(currentDate, company);
				break;
			case PayAsiaConstants.PAYSLIP_RELEASE_SCHEDULER:
				executePaySlipScheduler(company, currentDate, scheduleMasterVO);
				break;	
			default:
				break;
			}
			executeSchedulerStatus(company, scheduleMasterVO,
					schedulerStatusVO, scheduler, currentDate, null);

		} catch (Exception exception) {
			executeSchedulerStatus(company, scheduleMasterVO,
					schedulerStatusVO, scheduler, currentDate, exception);

		}
		LOGGER.info("Scheduler By Name ::" + scheduler
				+ " has complete for company ::" + company.getCompanyName()
				+ "Dated :" + currentDate);
	}

	private boolean hasCompanyModule(Company company, String moduleName) {
		boolean hasCompanyModule = false;

		Set<CompanyModuleMapping> companyModuleList = company
				.getCompanyModuleMappings();

		if (companyModuleList == null || companyModuleList.isEmpty()) {
			return hasCompanyModule;
		}
		for (CompanyModuleMapping companyModuleMapping : companyModuleList) {
			if (companyModuleMapping.getModuleMaster().getModuleName()
					.equalsIgnoreCase(moduleName)) {
				return true;
			}
		}
		return hasCompanyModule;
	}

	private boolean hasCompanyLeaveModuleInHours(Company company) {
		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(company.getCompanyId());
		if (leavePreferenceVO == null) {
			return false;
		}
		if (leavePreferenceVO.getLeaveUnit() != null
				&& leavePreferenceVO
						.getLeaveUnit()
						.getCodeDesc()
						.equalsIgnoreCase(
								PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_HOURS)) {
			return true;
		} else {
			return false;
		}
	}

	private void executeSchedulerStatus(Company company,
			SchedulerMaster scheduleMasterVO,
			SchedulerStatus schedulerStatusVO, String scheduler,
			Date currentDate, Exception exception) {

		if (exception == null) {
			SchedulerStatus schedulerStatus = new SchedulerStatus();
			schedulerStatus.setCompany(company);
			schedulerStatus.setSchedulerMaster(scheduleMasterVO);
			schedulerStatus.setLastRunDate(com.payasia.common.util.DateUtils
					.getCurrentTimestampWithTime());
			schedulerStatus.setLastRunStatus(true);

			if (schedulerStatusVO != null) {
				schedulerStatus.setSchedulerStatusId(schedulerStatusVO
						.getSchedulerStatusId());
				schedulerStatus
						.setLastRunDate(com.payasia.common.util.DateUtils
								.convertDateToTimeStampWithTime(currentDate));
				schedulerStatusDAO.updateSchedulerStatus(schedulerStatus);
			} else {
				schedulerStatusDAO.save(schedulerStatus);
			}
			LOGGER.info("Schedular By Name ::" + scheduler
					+ " has been run for company ::" + company.getCompanyName());

		} else {
			SchedulerStatus schedulerStatus = new SchedulerStatus();
			schedulerStatus.setCompany(company);
			schedulerStatus.setSchedulerMaster(scheduleMasterVO);
			schedulerStatus.setLastRunDate(com.payasia.common.util.DateUtils
					.getCurrentTimestampWithTime());
			schedulerStatus.setLastRunStatus(false);
			schedulerStatus.setFailureMessage(exception.getMessage());
			if (schedulerStatusVO != null) {
				schedulerStatus.setSchedulerStatusId(schedulerStatusVO
						.getSchedulerStatusId());
				schedulerStatus.setFailureMessage(exception.getMessage());
				schedulerStatus
						.setLastRunDate(com.payasia.common.util.DateUtils
								.convertDateToTimeStampWithTime(currentDate));
				schedulerStatusDAO.updateSchedulerStatus(schedulerStatus);
			} else {
				schedulerStatusDAO.save(schedulerStatus);
			}
			LOGGER.error(
					"Schedular By Name ::" + scheduler
							+ " has been run for company ::"
							+ company.getCompanyName(), exception);
		}

	}

	@Override
	public void yearEndProcessSchedular(Company company, Date currentDate) {
		try {
			LOGGER.info("### Executing yearEndProcessSchedular for company###:"
					+ company.getCompanyCode());
			YearEndProcessSchedule yearEndProcessSchedule = yearEndProcessScheduleDAO
					.findByCompanyId(company.getCompanyId());
			if (yearEndProcessSchedule == null) {
				LOGGER.info("yearEndProcessSchedule is not defined:");
			}
			if (yearEndProcessSchedule != null) {
				Calendar leaveRollOverDayMonth = GregorianCalendar
						.getInstance();
				leaveRollOverDayMonth.setTimeInMillis(yearEndProcessSchedule
						.getLeaveRollOver().getTime());
				Calendar leaveRollOverCal = Calendar.getInstance();
				leaveRollOverCal.set(
						GregorianCalendar.getInstance().get(Calendar.YEAR),
						leaveRollOverDayMonth.get(Calendar.MONTH),
						leaveRollOverDayMonth.get(Calendar.DAY_OF_MONTH));

				Date leaveRollOverDate = leaveRollOverCal.getTime();
				LOGGER.info("SP: leaveRollOverDate: " + leaveRollOverDate);
				LOGGER.info("SP: currentDate: " + currentDate);
				if (DateUtils.isSameDay(leaveRollOverDate, currentDate)) {
					LOGGER.info("SP: Started for company:"
							+ company.getCompanyCode());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(currentDate);
					yearEndProcessScheduleDAO
							.callProcessYearEndRollOverProc(
									company.getCompanyId(),
									calendar.get(Calendar.YEAR));
					LOGGER.info("SP: Completed for company:"
							+ company.getCompanyCode());
				} else {
					LOGGER.info("Current Date and YearRollOver Date doesnt match.");
				}

			}
		} catch (Exception ex) {
			LOGGER.error(
					"Error occured while executing year end rollover for company:"
							+ company.getCompanyCode(), ex);
		}

	}

	private void yearEndLeaveActivateSchedular(Company company, Date currentDate) {

		YearEndProcessSchedule yearEndProcessSchedule = yearEndProcessScheduleDAO
				.findByCompanyId(company.getCompanyId());
		if (yearEndProcessSchedule != null) {
			Calendar leaveActivateDayMonth = GregorianCalendar.getInstance();
			leaveActivateDayMonth.setTimeInMillis(yearEndProcessSchedule
					.getLeave_Activate().getTime());
			Calendar leaveActivateCal = Calendar.getInstance();
			leaveActivateCal.set(
					GregorianCalendar.getInstance().get(Calendar.YEAR),
					leaveActivateDayMonth.get(Calendar.MONTH),
					leaveActivateDayMonth.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			Date leaveActivateDate = leaveActivateCal.getTime();

			if (DateUtils.isSameDay(leaveActivateDate, currentDate)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(currentDate);
				yearEndProcessScheduleDAO.callyearEndLeaveActivateProc(
						company.getCompanyId(), calendar.get(Calendar.YEAR));

			}

		}

	}

	private void employmentStatusUpdateScheduler(Company company,
			Date currentDate) {
		updateEmploymentStatus(company, currentDate);

	}

	private void claimTemplateItemShortList(Date currentDate, Company company) {
		grantEmployeeClaimTemplateItemCmp(company, currentDate);

	}

	private void leaveSchemeTypeShortListScheduler(Date currentDate,
			Company company) {
		grantEmployeeLeaveSchemeTypeCmp(company, currentDate);

	}

	private void forfeitLeaveScheduler(Date currentDate, Company company) {
		leavePreferenceDAO.callForfeitProc(company.getCompanyId(), currentDate);

	}

	private void leaveGrantScheduler(Date currentDate, Company company) {

		Boolean yearEndProcess;
		LeavePreference leavePreference = leavePreferenceDAO
				.findByCompanyId(company.getCompanyId());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date scheduledDate;
		if (leavePreference != null) {

			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, Integer.parseInt(String
					.valueOf(leavePreference.getStartMonth().getMonthId())) - 1);

		} else {

			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, 0);

		}
		scheduledDate = cal.getTime();

		Calendar calCurrentDate = Calendar.getInstance();
		calCurrentDate.set(Calendar.HOUR_OF_DAY, 0);
		calCurrentDate.set(Calendar.MINUTE, 0);
		calCurrentDate.set(Calendar.SECOND, 0);
		calCurrentDate.set(Calendar.MILLISECOND, 0);
		Date currDate = com.payasia.common.util.DateUtils
				.stringToDate(
						com.payasia.common.util.DateUtils.timeStampToString(
								com.payasia.common.util.DateUtils
										.getCurrentTimestamp(), company
										.getDateFormat()), company
								.getDateFormat());
		if (scheduledDate.equals(currDate)) {
			yearEndProcess = true;
		} else {
			yearEndProcess = false;

		}

		GrantEmployeeLeaveVO grantEmployeeLeaveVO = new GrantEmployeeLeaveVO();
		grantEmployeeLeaveVO.setCompanyId(company.getCompanyId());
		grantEmployeeLeaveVO.setIsYearEndProcess(yearEndProcess);
		grantEmployeeLeaveVO.setCurrentDate(com.payasia.common.util.DateUtils
				.convertDateToTimeStamp(currentDate));
		leavePreferenceDAO.callGrantEmployeeLeaveProc(grantEmployeeLeaveVO);

	}

	private void leaveEventReminderScheduler(Date currentDate, Company company) {
		List<String> leaveStatusNames = new ArrayList<>();
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

		List<LeaveApplication> leaveApplicationVOs = leaveApplicationDAO
				.findByLeaveStatus(leaveStatusNames, company.getCompanyId());

		if (!leaveApplicationVOs.isEmpty()) {
			saveReminderEmailForLeave(leaveApplicationVOs, currentDate);
		}

	}

	private void grantEmployeeLeaveSchemeTypeCmp(Company company,
			Date currentDate) {
		List<LeaveSchemeType> leaveSchemeTypes = leaveSchemeTypeDAO
				.findLeaveSchTypeByCmpId(company.getCompanyId());

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(
				company.getCompanyId(), entityMaster.getEntityId());

		List<EmployeeLeaveSchemeType> cmpEmployeeLeaveSchemeTypes = employeeLeaveSchemeTypeDAO
				.findByCompany(company.getCompanyId(), currentDate);

		if (cmpEmployeeLeaveSchemeTypes == null) {
			return;
		}

		HashMap<String, EmployeeLeaveSchemeType> employeeLeaveSchemeMap = new HashMap<>();
		for (EmployeeLeaveSchemeType employeeLeaveSchemeType : cmpEmployeeLeaveSchemeTypes) {

			String mapkey = String.valueOf(employeeLeaveSchemeType
					.getLeaveSchemeType().getLeaveSchemeTypeId())
					+ "_"
					+ String.valueOf(employeeLeaveSchemeType
							.getEmployeeLeaveScheme().getEmployee()
							.getEmployeeId());
			employeeLeaveSchemeMap.put(mapkey, employeeLeaveSchemeType);
		}

		for (LeaveSchemeType leaveSchemeType : leaveSchemeTypes) {

			List<Long> leaveSchemeEmployeeIds = employeeLeaveSchemeDAO
					.getEmployeesOfLeaveSheme(leaveSchemeType.getLeaveScheme()
							.getLeaveSchemeId(), currentDate);

			List<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists = leaveSchemeTypeShortListDAO
					.findByCondition(leaveSchemeType.getLeaveSchemeTypeId());
			leaveSchemeLogic.setEmployeeLeaveSchemeTypes(
					leaveSchemeTypeShortLists, company.getCompanyId(),
					leaveSchemeEmployeeIds, leaveSchemeType,
					employeeLeaveSchemeMap, formIds, currentDate);

		}

	}

	private void grantEmployeeClaimTemplateItemCmp(Company company,
			Date currentDate) {
		List<ClaimTemplateItem> claimTemplateItems = claimTemplateItemDAO
				.findByCompanyId(company.getCompanyId());

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(
				company.getCompanyId(), entityMaster.getEntityId());

		for (ClaimTemplateItem claimTemplateItem : claimTemplateItems) {

			List<EmployeeClaimTemplateItem> cmpEmployeeClaimTemplateItems = employeeClaimTemplateItemDAO
					.findByCompanyCondition(company.getCompanyId(),
							currentDate, claimTemplateItem.getClaimTemplate()
									.getClaimTemplateId());

			HashMap<String, EmployeeClaimTemplateItem> employeeClaimTemplateItemMap = new HashMap<>();
			for (EmployeeClaimTemplateItem employeeClaimTemplateItem : cmpEmployeeClaimTemplateItems) {

				String mapkey = String.valueOf(employeeClaimTemplateItem
						.getClaimTemplateItem().getClaimTemplateItemId())
						+ "_"
						+ String.valueOf(employeeClaimTemplateItem
								.getEmployeeClaimTemplate().getEmployee()
								.getEmployeeId());
				employeeClaimTemplateItemMap.put(mapkey,
						employeeClaimTemplateItem);
			}

			List<Long> claimTemplateEmpIds = employeeClaimTemplateDAO
					.getEmployeesOfClaimTemplate(claimTemplateItem
							.getClaimTemplate().getClaimTemplateId());
			List<ClaimTemplateItemShortlist> claimTemplateItemShortlists = claimTemplateItemShortlistDAO
					.findByCondition(claimTemplateItem.getClaimTemplateItemId(),company.getCompanyId());

			claimTemplateLogic.setEmployeeClaimTemplateItem(
					claimTemplateItemShortlists, company.getCompanyId(),
					claimTemplateEmpIds, claimTemplateItem, currentDate,
					formIds, employeeClaimTemplateItemMap);

		}

	}

	private void updateEmploymentStatus(Company company, Date dateCtr) {
		employeeDAO.updateEmploymentStatus(company.getCompanyId(), dateCtr);
		employeeDAO.updateEmployeeStatus(company.getCompanyId(), dateCtr);

	}

	private void saveReminderEmailForLeave(
			List<LeaveApplication> leaveApplications, Date lastRunDate) {

		for (LeaveApplication leaveApplication : leaveApplications) {
			String eventName;
			if (leaveApplication.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

				if (leaveApplication.getLeaveCancelApplication() != null) {
					eventName = PayAsiaConstants.LEAVE_EVENT_REMINDER_LEAVE_CANCEL_APPLY;
				} else {
					eventName = PayAsiaConstants.LEAVE_EVENT_REMINDER_LEAVE_APPLY;
				}

			} else {

				if (leaveApplication.getLeaveCancelApplication() != null) {
					eventName = PayAsiaConstants.LEAVE_EVENT_REMINDER_LEAVE_CANCEL_FORWARD;
				} else {
					eventName = PayAsiaConstants.LEAVE_EVENT_REMINDER_LEAVE_FORWARD;
				}

			}

			Long companyId = leaveApplication.getCompany().getCompanyId();

			ReminderEventConfig reminDerEventConfig = null;
			LeaveReminderDTO leaveReminderDTO = new LeaveReminderDTO();

			reminDerEventConfig = reminderEventConfigDAO.findByTypeSchemeEvent(
					leaveApplication.getEmployeeLeaveSchemeType()
							.getLeaveSchemeType().getLeaveTypeMaster()
							.getLeaveTypeId(), leaveApplication
							.getEmployeeLeaveSchemeType().getLeaveSchemeType()
							.getLeaveScheme().getLeaveSchemeId(), eventName,
					companyId);

			if (reminDerEventConfig == null) {

				reminDerEventConfig = reminderEventConfigDAO.findByTypeEvent(
						leaveApplication.getEmployeeLeaveSchemeType()
								.getLeaveSchemeType().getLeaveTypeMaster()
								.getLeaveTypeId(),

						eventName, companyId);

			}
			if (reminDerEventConfig == null) {

				reminDerEventConfig = reminderEventConfigDAO.findBySchemeEvent(
						leaveApplication.getEmployeeLeaveSchemeType()
								.getLeaveSchemeType().getLeaveScheme()
								.getLeaveSchemeId(), eventName, companyId);
			}
			if (reminDerEventConfig == null) {

				reminDerEventConfig = reminderEventConfigDAO.findByEvent(
						eventName, companyId);
			}

			if (reminDerEventConfig != null) {

				String emailTo = getEmailId(leaveApplication,
						reminDerEventConfig, leaveReminderDTO);
				Email email = new Email();

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date leaveApplicationCreatedDate = new Date(leaveApplication
						.getCreatedDate().getTime());

				EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
						.findByConditionCompany(leaveApplication.getCompany()
								.getCompanyId());

				if (emailPreferenceMasterVO != null) {

					email.setCompany(leaveApplication.getCompany());
					email.setEmailFrom(emailPreferenceMasterVO
							.getContactEmail());
					email.setEmailTo(emailTo);
					email.setEmailCC(leaveApplication.getEmailCC());

					if (DateUtils.isSameDay(lastRunDate,
							leaveApplicationCreatedDate)) {

						saveEmailOnEvent(reminDerEventConfig, email,
								leaveApplication, leaveReminderDTO);

					} else if (lastRunDate
							.compareTo(leaveApplicationCreatedDate) > 0) {

						saveEmailAfterEvent(lastRunDate, reminDerEventConfig,
								email, leaveApplicationCreatedDate,
								leaveApplication, leaveReminderDTO);

					} else if (lastRunDate
							.compareTo(leaveApplicationCreatedDate) < 0) {

						saveEmailBeforeEvent(lastRunDate, reminDerEventConfig,
								email, leaveApplicationCreatedDate,
								leaveApplication, leaveReminderDTO);

					}
				}

			}
		}

	}

	private void saveEmailBeforeEvent(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Email email,
			Date leaveApplicationCreatedDate,
			LeaveApplication leaveApplication, LeaveReminderDTO leaveReminderDTO) {
		if (!reminDerEventConfig.isAllowSendMailBeforeEventDay()) {
			return;
		}
		if (reminDerEventConfig.getSendMailBeforeMailTemplate() == null) {
			return;

		} else {

			setEmailObject(email,
					reminDerEventConfig.getSendMailBeforeMailTemplate());

		}
		if (reminDerEventConfig.getSendMailBeforeDays() == null) {

			return;
		}
		Date sendMailBeforeDaysDate = DateUtils.addDays(
				leaveApplicationCreatedDate,
				reminDerEventConfig.getSendMailBeforeDays() * -1);

		Date dateCtr;
		for (dateCtr = sendMailBeforeDaysDate; dateCtr
				.before(leaveApplicationCreatedDate)
				|| dateCtr.equals(leaveApplicationCreatedDate); dateCtr = DateUtils
				.addDays(dateCtr,
						reminDerEventConfig.getSendMailBeforeRepeatDays())) {

			if (DateUtils.isSameDay(dateCtr, currentDate)) {
				saveEmailObject(email, leaveApplication, leaveReminderDTO);
			}

		}

	}

	private void saveEmailAfterEvent(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Email email,
			Date leaveApplicationCreatedDate,
			LeaveApplication leaveApplication, LeaveReminderDTO leaveReminderDTO) {

		if (!reminDerEventConfig.isAllowSendMailAfterEventDay()) {

			return;
		}

		if (reminDerEventConfig.getSendMailAfterMailTemplate() == null) {
			return;
		} else {
			setEmailObject(email,
					reminDerEventConfig.getSendMailAfterMailTemplate());

		}

		Date sendMailAfterTillDaysDate = DateUtils.addDays(
				leaveApplicationCreatedDate,
				reminDerEventConfig.getSendMailAfterDays()
						+ reminDerEventConfig.getSendMailAfterTillDays());
		if (sendMailAfterTillDaysDate.compareTo(currentDate) > 0
				|| sendMailAfterTillDaysDate.equals(currentDate)) {

			Date dateCtr;
			Date dateAfterDays = DateUtils.addDays(leaveApplicationCreatedDate,
					reminDerEventConfig.getSendMailAfterDays());

			if (reminDerEventConfig.getSendMailAfterRepeatDays() > 0) {

				for (dateCtr = dateAfterDays; dateCtr
						.before(sendMailAfterTillDaysDate)
						|| dateCtr.equals(sendMailAfterTillDaysDate); dateCtr = DateUtils
						.addDays(dateCtr, reminDerEventConfig
								.getSendMailAfterRepeatDays())) {

					if (DateUtils.isSameDay(dateCtr, currentDate)) {
						saveEmailObject(email, leaveApplication,
								leaveReminderDTO);
					}
				}
			} else if (reminDerEventConfig.getSendMailAfterRepeatDays() == 0) {
				dateCtr = dateAfterDays;
				if (dateCtr.before(sendMailAfterTillDaysDate)
						|| dateCtr.equals(sendMailAfterTillDaysDate)
						&& DateUtils.isSameDay(dateCtr, currentDate)) {

					saveEmailObject(email, leaveApplication, leaveReminderDTO);
				}
			}

		}

	}

	private void saveEmailOnEvent(ReminderEventConfig reminDerEventConfig,
			Email email, LeaveApplication leaveApplication,
			LeaveReminderDTO leaveReminderDTO) {

		if (reminDerEventConfig.isAllowSendMailOnEventDay()) {

			if (reminDerEventConfig.getSendMailOnEventMailTemplate() != null) {

				setEmailObject(email,
						reminDerEventConfig.getSendMailOnEventMailTemplate());
				saveEmailObject(email, leaveApplication, leaveReminderDTO);

			}

		}
	}

	private void setEmailObject(Email email, EmailTemplate emailTemplateOn) {

		if (StringUtils.isNotBlank(emailTemplateOn.getBody())) {
			email.setBody(emailTemplateOn.getBody());
		} else {
			email.setBody("");
		}
		if (StringUtils.isNotBlank(emailTemplateOn.getSubject())) {
			email.setSubject(emailTemplateOn.getSubject());
		} else {
			email.setSubject("");
		}

	}

	private String getEmailId(LeaveApplication leaveApplication,
			ReminderEventConfig reminDerEventConfig,
			LeaveReminderDTO leaveReminderDTO) {

		String emailId = "";

		String recepientType = reminDerEventConfig.getRecipientType()
				.getCodeDesc();

		switch (recepientType) {
		case PayAsiaConstants.LEAVE_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE_USER:

			Employee employeeUser = employeeDAO.findByNumber(
					reminDerEventConfig.getRecipientValue(), leaveApplication
							.getCompany().getCompanyId());
			if (employeeUser != null) {

				emailId = employeeUser.getEmail();
				if (employeeUser.getCompany().getEmailPreferenceMasters() != null) {
					leaveReminderDTO.setURL(employeeUser.getCompany()
							.getEmailPreferenceMasters().iterator().next()
							.getCompanyUrl());
				}

			}

			break;
		case PayAsiaConstants.LEAVE_Event_Reminder_RECEPIENT_TYPE_EXTERNAL_ENTITY:
			emailId = reminDerEventConfig.getRecipientValue();

			break;
		case PayAsiaConstants.LEAVE_Event_Reminder_RECEPIENT_TYPE_CURRENT_OWNER:

			Set<LeaveApplicationReviewer> leaveApplicationReviewers = leaveApplication
					.getLeaveApplicationReviewers();

			for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationReviewers) {
				if (leaveApplicationReviewer.getPending()) {
					emailId = leaveApplicationReviewer.getEmployee().getEmail();
					if (leaveApplicationReviewer.getEmployee().getCompany()
							.getEmailPreferenceMasters() != null) {
						leaveReminderDTO.setURL(leaveApplicationReviewer
								.getEmployee().getCompany()
								.getEmailPreferenceMasters().iterator().next()
								.getCompanyUrl());
					}
				}

			}

			break;
		case PayAsiaConstants.LEAVE_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE:
			emailId = leaveApplication.getEmployee().getEmail();
			break;

		}
		return emailId;
	}

	private void saveEmailObject(Email email,
			LeaveApplication leaveApplication, LeaveReminderDTO leaveReminderDTO) {

		setEmailBodySubject(email, leaveApplication, leaveReminderDTO);

		email.setCreatedDate(com.payasia.common.util.DateUtils
				.getCurrentTimestamp());

		if (StringUtils.isNotBlank(email.getEmailTo())
				&& StringUtils.isNotBlank(email.getEmailFrom())) {
			Email persistEmail = emailDAO.saveReturn(email);

			Set<LeaveApplicationAttachment> leaveApplicationAttachmentVOs = leaveApplication
					.getLeaveApplicationAttachments();
			for (LeaveApplicationAttachment leaveApplicationAttachmentVO : leaveApplicationAttachmentVOs) {

				EmailAttachment emailAttachment = new EmailAttachment();

				emailAttachment.setEmail(persistEmail);
				emailAttachment.setFileName(leaveApplicationAttachmentVO
						.getFileName());

				// Get Leave Attachment to save new object for cancel leave
				String fileExt = leaveApplicationAttachmentVO.getFileType();
				/*
				 * String filePath = "/company/" +
				 * leaveApplicationAttachmentVO.getLeaveApplication()
				 * .getCompany().getCompanyId() + "/" +
				 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/" +
				 * leaveApplicationAttachmentVO
				 * .getLeaveApplicationAttachmentId() + "." + fileExt;
				 */
				FilePathGeneratorDTO filePathGenerator = fileUtils
						.getFileCommonPath(
								downloadPath,
								rootDirectoryName,
								leaveApplicationAttachmentVO
										.getLeaveApplication().getCompany()
										.getCompanyId(),
								PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME,
								String.valueOf(leaveApplicationAttachmentVO
										.getLeaveApplicationAttachmentId()),
								null, null, null,
								PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
				String filePath = fileUtils
						.getGeneratedFilePath(filePathGenerator);
				File file = new File(filePath);

				try {
					if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
							.equalsIgnoreCase(appDeployLocation)) {
						byte[] byteFile = org.apache.commons.io.IOUtils
								.toByteArray(awss3LogicImpl
										.readS3ObjectAsStream(filePath));
						emailAttachment.setAttachment(byteFile);
					} else {
						emailAttachment.setAttachment(Files.readAllBytes(file
								.toPath()));
					}
					emailAttachementDAO.saveReturn(emailAttachment);
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}

			}
		}
	}

	private void setEmailBodySubject(Email email,
			LeaveApplication leaveApplication, LeaveReminderDTO leaveReminderDTO) {
		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(com.payasia.common.util.DateUtils
				.timeStampToString(leaveApplication.getStartDate(),
						leaveApplication.getCompany().getDateFormat()));
		leaveDTO.setToDate(com.payasia.common.util.DateUtils.timeStampToString(
				leaveApplication.getEndDate(), leaveApplication.getCompany()
						.getDateFormat()));
		leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1()
				.getLeaveSessionId());
		leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2()
				.getLeaveSessionId());
		leaveDTO.setEmployeeLeaveSchemeTypeId(leaveApplication
				.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
		AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put(PayAsiaConstants.LEAVE_FROM_DATE,
				com.payasia.common.util.DateUtils.timeStampToString(
						leaveApplication.getStartDate(), leaveApplication
								.getCompany().getDateFormat()));
		modelMap.put(PayAsiaConstants.LEAVE_TO_DATE,
				com.payasia.common.util.DateUtils.timeStampToString(
						leaveApplication.getEndDate(), leaveApplication
								.getCompany().getDateFormat()));
		modelMap.put(PayAsiaConstants.LEAVE_LEAVE_TYPE_DESC, leaveApplication
				.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveTypeMaster().getLeaveTypeName());
		modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NAME,
				getEmployeeName(leaveApplication.getEmployee()));
		modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NUMBER, leaveApplication
				.getEmployee().getEmployeeNumber());

		modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, leaveApplication
				.getLeaveSessionMaster1().getSessionDesc());
		modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, leaveApplication
				.getLeaveSessionMaster2().getSessionDesc());
		modelMap.put(PayAsiaConstants.LEAVE_REASON,
				leaveApplication.getReason());
		modelMap.put(PayAsiaConstants.LEAVE_LEAVE_BALANCE, decimalFmt
				.format(Double.parseDouble(String.valueOf(leaveBalance
						.getLeaveBalance()))));
		modelMap.put(PayAsiaConstants.LEAVE_DAYS, decimalFmt.format(Double
				.parseDouble(String.valueOf(leaveApplication.getTotalDays()))));

		if (leaveReminderDTO.getURL() != null) {
			String link = "<a href='" + leaveReminderDTO.getURL() + "'>"
					+ leaveReminderDTO.getURL() + "</a>";
			modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);

		} else {
			modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
		}

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;
		try {
			byte[] mailBodyBytes = email.getBody().getBytes();
			byte[] mailSubjectBytes = email.getSubject().getBytes();

			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);

			emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendLeaveTemplate"
					+ leaveApplication.getLeaveApplicationId() + "_" + uniqueId
					+ ".vm");
			emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendLeaveTemplateSubject"
					+ leaveApplication.getLeaveApplicationId() + "_" + uniqueId
					+ ".vm");
			emailSubjectTemplate.deleteOnExit();
			emailTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
				fosSubject = new FileOutputStream(emailSubjectTemplate);
				fosSubject.write(mailSubjectBytes);
				fosSubject.flush();
				fosSubject.close();

			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			String templateBodyString = emailTemplate.getPath().substring(
					emailTemplate.getParent().length() + 1);
			String templateSubjectString = emailSubjectTemplate.getPath()
					.substring(emailSubjectTemplate.getParent().length() + 1);

			StringBuilder subjectText = new StringBuilder("");

			subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateSubjectString, modelMap));

			StringBuilder bodyText = new StringBuilder("");

			bodyText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateBodyString, modelMap));

			email.setBody(bodyText.toString());
			email.setSubject(subjectText.toString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
				emailSubjectTemplate.delete();
			}
		}

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

	private void lundinTimesheetReminderScheduler(Date currentDate,
			Company company, String moduleName) {
		List<String> timesheetStatusNames = new ArrayList<>();
		timesheetStatusNames
				.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED);

		try {
			List<TimesheetApplicationReviewer> lundinTimesheetVOs = lundinTimesheetDAO
					.findByTimesheetStatus(timesheetStatusNames,
							company.getCompanyId());

			if (!lundinTimesheetVOs.isEmpty()) {

				saveReminderEmailForLundinTimesheet(lundinTimesheetVOs,
						currentDate, company.getCompanyId());
			}

			List<EmployeeFieldDTO> empListToSendReminderMail = getEmpListToSendReminderMail(
					company, moduleName);
			if (!empListToSendReminderMail.isEmpty()) {
				saveReminderEmailForEmployee(empListToSendReminderMail,
						currentDate, company, "");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private List<EmployeeFieldDTO> getEmpListToSendReminderMail(
			Company company, String moduleName) {

		List<EmployeeFieldDTO> empListToReturn = new ArrayList<EmployeeFieldDTO>();
		try {
			List<Tuple> employeeRoleMapList = empployeeRoleMappindDAO
					.findByConditionCompany(company.getCompanyId());
			Set<EmployeeFieldDTO> employeeSet = new HashSet<EmployeeFieldDTO>();
			Boolean isAssigned = false;
			if (!employeeRoleMapList.isEmpty()) {
				for (Tuple tupleVOObj : employeeRoleMapList) {
					isAssigned = privilegeMasterDAO
							.getTimesheetPrivilegesByRole((Long) tupleVOObj
									.get(getAlias(RoleMaster_.roleId),
											Long.class));
					if (isAssigned) {
						EmployeeFieldDTO employeeDto = new EmployeeFieldDTO();

						String email = tupleVOObj
								.get(getAlias(Employee_.email)) != null ? ""
								+ tupleVOObj.get(getAlias(Employee_.email))
								: "";

						Long empId = (Long) tupleVOObj.get(
								getAlias(Employee_.employeeId), Long.class);

						String empNumber = tupleVOObj
								.get(getAlias(Employee_.employeeNumber)) != null ? ""
								+ tupleVOObj
										.get(getAlias(Employee_.employeeNumber))
								: "";

						String empFirstName = tupleVOObj
								.get(getAlias(Employee_.firstName)) != null ? ""
								+ tupleVOObj.get(getAlias(Employee_.firstName))
								: "";
						String empMiddleName = tupleVOObj
								.get(getAlias(Employee_.middleName)) != null ? ""
								+ tupleVOObj
										.get(getAlias(Employee_.middleName))
								: "";
						String empLastName = tupleVOObj
								.get(getAlias(Employee_.lastName)) != null ? ""
								+ tupleVOObj.get(getAlias(Employee_.lastName))
								: "";

						employeeDto.setEmployeeEmail(email);
						employeeDto.setEmployeeId(empId);
						employeeDto.setEmployeeNumber(empNumber);
						employeeDto.setEmployeeName(empFirstName + " "
								+ empMiddleName + " " + empLastName);

						employeeSet.add(employeeDto);
					}
				}
			}
			if (!employeeSet.isEmpty()) {

				List<EmployeeFieldDTO> employeeList = new ArrayList<EmployeeFieldDTO>(
						employeeSet);
				empListToReturn = saveReminderMailForEmployee(employeeList,
						company.getCompanyId());

			}
			return empListToReturn;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return empListToReturn;
		}
	}

	private List<EmployeeFieldDTO> getEmpListToSendCoherentReminderMail(
			Company company) {

		List<EmployeeFieldDTO> empListToReturn = new ArrayList<EmployeeFieldDTO>();
		try {
			List<Tuple> employeeRoleMapList = empployeeRoleMappindDAO
					.findByConditionCompany(company.getCompanyId());
			Set<EmployeeFieldDTO> employeeSet = new HashSet<EmployeeFieldDTO>();
			if (!employeeRoleMapList.isEmpty()) {
				for (Tuple tupleVOObj : employeeRoleMapList) {
					EmployeeFieldDTO employeeDto = new EmployeeFieldDTO();

					String email = tupleVOObj.get(getAlias(Employee_.email)) != null ? ""
							+ tupleVOObj.get(getAlias(Employee_.email))
							: "";

					Long empId = (Long) tupleVOObj.get(
							getAlias(Employee_.employeeId), Long.class);

					String empNumber = tupleVOObj
							.get(getAlias(Employee_.employeeNumber)) != null ? ""
							+ tupleVOObj
									.get(getAlias(Employee_.employeeNumber))
							: "";

					String empFirstName = tupleVOObj
							.get(getAlias(Employee_.firstName)) != null ? ""
							+ tupleVOObj.get(getAlias(Employee_.firstName))
							: "";
					String empMiddleName = tupleVOObj
							.get(getAlias(Employee_.middleName)) != null ? ""
							+ tupleVOObj.get(getAlias(Employee_.middleName))
							: "";
					String empLastName = tupleVOObj
							.get(getAlias(Employee_.lastName)) != null ? ""
							+ tupleVOObj.get(getAlias(Employee_.lastName)) : "";

					employeeDto.setEmployeeEmail(email);
					employeeDto.setEmployeeId(empId);
					employeeDto.setEmployeeNumber(empNumber);
					employeeDto.setEmployeeName(empFirstName + " "
							+ empMiddleName + " " + empLastName);

					employeeSet.add(employeeDto);
				}
			}
			if (!employeeSet.isEmpty()) {

				List<EmployeeFieldDTO> employeeList = new ArrayList<EmployeeFieldDTO>(
						employeeSet);
				empListToReturn.addAll(employeeList);

			}
			return empListToReturn;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return empListToReturn;
		}
	}

	private List<EmployeeFieldDTO> saveReminderMailForEmployee(
			List<EmployeeFieldDTO> employeeFieldDtoList, Long companyId) {

		try {
			if (!employeeFieldDtoList.isEmpty()) {

				TimesheetBatch batch = timesheetBatchDAO.findBatchByDate(
						new Timestamp(new Date().getTime()), companyId);
				List<EmployeeTimesheetApplication> submittedTimesheet = new ArrayList<EmployeeTimesheetApplication>();
				submittedTimesheet = lundinTimesheetDAO.findSubmitTimesheetEmp(
						batch.getTimesheetBatchId(), companyId);
				List<EmployeeFieldDTO> empIdToRemove = new ArrayList<EmployeeFieldDTO>();
				for (EmployeeTimesheetApplication timesheet : submittedTimesheet) {
					EmployeeFieldDTO empDto = new EmployeeFieldDTO();
					Employee employee = timesheet.getEmployee();
					empDto.setEmployeeId(employee.getEmployeeId());
					empIdToRemove.add(empDto);
				}
				;
				employeeFieldDtoList.removeAll(empIdToRemove);
			}
			return employeeFieldDtoList;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return employeeFieldDtoList;
		}
	}

	private String getTimesheetEmailId(
			EmployeeTimesheetApplication lundinTimesheet,
			ReminderEventConfig reminDerEventConfig,
			LeaveReminderDTO leaveReminderDTO) {

		String emailId = "";

		String recepientType = reminDerEventConfig.getRecipientType()
				.getCodeDesc();

		switch (recepientType) {
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE_USER:

			Employee employeeUser = employeeDAO.findByNumber(
					reminDerEventConfig.getRecipientValue(), lundinTimesheet
							.getCompany().getCompanyId());
			if (employeeUser != null) {

				emailId = employeeUser.getEmail();
				if (employeeUser.getCompany().getEmailPreferenceMasters() != null) {
					leaveReminderDTO.setURL(employeeUser.getCompany()
							.getEmailPreferenceMasters().iterator().next()
							.getCompanyUrl());
				}

			}

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EXTERNAL_ENTITY:
			emailId = reminDerEventConfig.getRecipientValue();

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_CURRENT_OWNER:

			Set<TimesheetApplicationReviewer> lundinTimesheetReviewers = lundinTimesheet
					.getTimesheetApplicationReviewers();

			for (TimesheetApplicationReviewer lundinTimesheetReviewer : lundinTimesheetReviewers) {
				if (lundinTimesheetReviewer.isPending()) {
					emailId = lundinTimesheetReviewer.getEmployee().getEmail();
					if (lundinTimesheetReviewer.getEmployee().getCompany()
							.getEmailPreferenceMasters() != null) {
						leaveReminderDTO.setURL(lundinTimesheetReviewer
								.getEmployee().getCompany()
								.getEmailPreferenceMasters().iterator().next()
								.getCompanyUrl());
					}
				}

			}

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE:
			emailId = lundinTimesheet.getEmployee().getEmail();
			break;
		}
		return emailId;
	}

	private void saveReminderEmailForLundinTimesheet(
			List<TimesheetApplicationReviewer> lundinTimesheetVOs,
			Date lastRunDate, Long companyId) {

		TimesheetBatch batch = lundinTimesheetDAO.findCurrentBatchForDate(
				new Timestamp(new Date().getTime()), companyId);

		for (TimesheetApplicationReviewer lundinTimesheetReviewer : lundinTimesheetVOs) {
			String eventName = null;
			if (lundinTimesheetReviewer
					.getEmployeeTimesheetApplication()
					.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED)) {
				eventName = PayAsiaConstants.LUNDIN_TIMESHEET_EVENT_REMINDER_APPROVE;
			}

			ReminderEventConfig reminDerEventConfig = null;

			reminDerEventConfig = reminderEventConfigDAO.findByEvent(eventName,
					companyId);
			LeaveReminderDTO leaveReminderDTO = new LeaveReminderDTO();
			if (reminDerEventConfig != null) {

				String emailTo = getTimesheetEmailId(
						lundinTimesheetReviewer
								.getEmployeeTimesheetApplication(),
						reminDerEventConfig, leaveReminderDTO);

				if (StringUtils.isBlank(emailTo)) {
					emailTo = lundinTimesheetReviewer.getEmployee().getEmail();
				}

				Email email = new Email();

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				// Date timesheetCreatedDate = new Date(lundinTimesheetReviewer
				// .getLundinTimesheet().getUpdatedDate().getTime());

				EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
						.findByConditionCompany(lundinTimesheetReviewer
								.getEmployeeTimesheetApplication().getCompany()
								.getCompanyId());

				if (emailPreferenceMasterVO != null) {

					email.setCompany(lundinTimesheetReviewer
							.getEmployeeTimesheetApplication().getCompany());
					email.setEmailFrom(emailPreferenceMasterVO
							.getContactEmail());
					email.setEmailTo(emailTo);

					Long employeeId = lundinTimesheetReviewer.getEmployee()
							.getEmployeeId();
					Date sDate = new Date(batch.getStartDate().getTime());
					cal.setTime(sDate);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					Date startDate = cal.getTime();
					Date eDate = new Date(batch.getEndDate().getTime());
					cal.setTime(eDate);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					Date endDate = cal.getTime();
					List<Date> holidayDates = getHolidaysFor(employeeId,
							startDate, endDate);

					if (lastRunDate.compareTo(endDate) < 0) {

						saveTimesheetEmailBeforeEvent(lastRunDate,
								reminDerEventConfig, email, endDate,
								lundinTimesheetReviewer
										.getEmployeeTimesheetApplication(),
								leaveReminderDTO, holidayDates);

						// saveTimesheetEmailOnEvent(reminDerEventConfig, email,
						// lundinTimesheetReviewer.getLundinTimesheet(),
						// leaveReminderDTO);
						// } else if
						// (lastRunDate.compareTo(timesheetCreatedDate) > 0) {
						// saveTimesheetEmailAfterEvent(lastRunDate,
						// reminDerEventConfig, email,
						// timesheetCreatedDate,
						// lundinTimesheetReviewer.getLundinTimesheet(),
						// leaveReminderDTO);
						// } else if
						// (lastRunDate.compareTo(timesheetCreatedDate) < 0) {
						// saveTimesheetEmailBeforeEvent(lastRunDate,
						// reminDerEventConfig, email,
						// timesheetCreatedDate,
						// lundinTimesheetReviewer.getLundinTimesheet(),
						// leaveReminderDTO);
					}
				}

			}
		}
	}

	private void saveTimesheetEmailBeforeEvent(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Email email,
			Date cuttOffDate, EmployeeTimesheetApplication lundinTimesheet,
			LeaveReminderDTO leaveReminderDTO, List<Date> holidayDates) {

		if (!reminDerEventConfig.isAllowSendMailBeforeEventDay()) {
			return;
		}
		if (reminDerEventConfig.getSendMailBeforeMailTemplate() == null) {
			return;

		} else {

			setEmailObject(email,
					reminDerEventConfig.getSendMailBeforeMailTemplate());

		}
		if (reminDerEventConfig.getSendMailBeforeDays() == null) {

			return;
		}
		// Date sendMailBeforeDaysDate = DateUtils.addDays(cuttOffDate,
		// reminDerEventConfig.getSendMailBeforeDays() * -1);
		int ctr = 0;
		Date sendMailBeforeDate = cuttOffDate;
		List<Date> sendMailOnDaysList = new ArrayList<Date>();
		while (ctr < reminDerEventConfig.getSendMailBeforeDays()) {
			sendMailBeforeDate = DateUtils.addDays(sendMailBeforeDate, -1);
			Calendar cal = Calendar.getInstance();
			cal.setTime(sendMailBeforeDate);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			// Check For Non Working (Public Holidays) & (Sat,Sun), If Not Exist
			if (!holidayDates.contains(sendMailBeforeDate)
					&& (dayOfWeek != 1 && dayOfWeek != 7)) {
				ctr++;
				sendMailOnDaysList.add(sendMailBeforeDate);
			}
		}
		Date dateCtr;
		if (reminDerEventConfig.getSendMailBeforeRepeatDays() > 0) {

			for (dateCtr = sendMailBeforeDate; dateCtr.before(cuttOffDate)
					|| dateCtr.equals(cuttOffDate); dateCtr = DateUtils
					.addDays(dateCtr,
							reminDerEventConfig.getSendMailBeforeRepeatDays())) {

				if (DateUtils.isSameDay(dateCtr, currentDate)
						&& sendMailOnDaysList.contains(dateCtr)) {
					saveTimesheetEmailObject(email, lundinTimesheet,
							leaveReminderDTO);
					email.setCreatedDate(com.payasia.common.util.DateUtils
							.getCurrentTimestamp());
					if (StringUtils.isNotBlank(email.getEmailTo())
							&& StringUtils.isNotBlank(email.getEmailFrom())) {
						emailDAO.saveReturn(email);
					}
				}
			}
		} else if (reminDerEventConfig.getSendMailBeforeRepeatDays() == 0) {
			dateCtr = sendMailBeforeDate;
			if (dateCtr.before(cuttOffDate) || dateCtr.equals(cuttOffDate)) {
				if (DateUtils.isSameDay(dateCtr, currentDate)
						&& sendMailOnDaysList.contains(dateCtr)) {
					saveTimesheetEmailObject(email, lundinTimesheet,
							leaveReminderDTO);
					email.setCreatedDate(com.payasia.common.util.DateUtils
							.getCurrentTimestamp());
					if (StringUtils.isNotBlank(email.getEmailTo())
							&& StringUtils.isNotBlank(email.getEmailFrom())) {
						emailDAO.saveReturn(email);
					}
				}
			}
		}
	}

	private void saveTimesheetEmailAfterEvent(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Email email,
			Date timesheetCreatedDate,
			EmployeeTimesheetApplication lundinTimesheet,
			LeaveReminderDTO leaveReminderDTO) {

		if (!reminDerEventConfig.isAllowSendMailAfterEventDay()) {

			return;
		}

		if (reminDerEventConfig.getSendMailAfterMailTemplate() == null) {
			return;
		} else {
			setEmailObject(email,
					reminDerEventConfig.getSendMailAfterMailTemplate());

		}

		Date sendMailAfterTillDaysDate = DateUtils.addDays(
				timesheetCreatedDate,
				reminDerEventConfig.getSendMailAfterDays()
						+ reminDerEventConfig.getSendMailAfterTillDays());
		if (sendMailAfterTillDaysDate.compareTo(currentDate) > 0
				|| sendMailAfterTillDaysDate.equals(currentDate)) {

			Date dateCtr;
			Date dateAfterDays = DateUtils.addDays(timesheetCreatedDate,
					reminDerEventConfig.getSendMailAfterDays());
			for (dateCtr = dateAfterDays; dateCtr
					.before(sendMailAfterTillDaysDate)
					|| dateCtr.equals(sendMailAfterTillDaysDate); dateCtr = DateUtils
					.addDays(dateCtr,
							reminDerEventConfig.getSendMailAfterRepeatDays())) {

				if (DateUtils.isSameDay(dateCtr, currentDate)) {

					saveTimesheetEmailObject(email, lundinTimesheet,
							leaveReminderDTO);
					email.setCreatedDate(com.payasia.common.util.DateUtils
							.getCurrentTimestamp());
					if (StringUtils.isNotBlank(email.getEmailTo())
							&& StringUtils.isNotBlank(email.getEmailFrom())) {
						emailDAO.saveReturn(email);
					}
				}
			}

		}

	}

	private void saveTimesheetEmailOnEvent(
			ReminderEventConfig reminDerEventConfig, Email email,
			EmployeeTimesheetApplication lundinTimesheet,
			LeaveReminderDTO leaveReminderDTO) {

		if (reminDerEventConfig.isAllowSendMailOnEventDay()) {

			if (reminDerEventConfig.getSendMailOnEventMailTemplate() != null) {

				setEmailObject(email,
						reminDerEventConfig.getSendMailOnEventMailTemplate());

				saveTimesheetEmailObject(email, lundinTimesheet,
						leaveReminderDTO);

				email.setCreatedDate(com.payasia.common.util.DateUtils
						.getCurrentTimestamp());
				if (StringUtils.isNotBlank(email.getEmailTo())
						&& StringUtils.isNotBlank(email.getEmailFrom())) {
					emailDAO.saveReturn(email);
				}

			}

		}
	}

	private void saveTimesheetEmailObject(Email email,
			EmployeeTimesheetApplication lundinTimesheet,
			LeaveReminderDTO leaveReminderDTO) {

		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NAME,
				getEmployeeName(lundinTimesheet.getEmployee()));
		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NUMBER,
				lundinTimesheet.getEmployee().getEmployeeNumber());
		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_BATCH, lundinTimesheet
				.getTimesheetBatch().getTimesheetBatchDesc());

		if (leaveReminderDTO.getURL() != null) {
			String link = "<a href='" + leaveReminderDTO.getURL() + "'>"
					+ leaveReminderDTO.getURL() + "</a>";
			modelMap.put(PayAsiaConstants.URL, link);
		} else {
			modelMap.put(PayAsiaConstants.URL, "");
		}

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;
		try {
			byte[] mailBodyBytes = email.getBody().getBytes();
			byte[] mailSubjectBytes = email.getSubject().getBytes();

			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);

			emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendMailTimesheetTemplate"
					+ lundinTimesheet.getTimesheetId() + "_" + uniqueId + ".vm");
			emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendMailTimesheetTemplateSubject"
					+ lundinTimesheet.getTimesheetId() + "_" + uniqueId + ".vm");
			emailSubjectTemplate.deleteOnExit();
			emailTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
				fosSubject = new FileOutputStream(emailSubjectTemplate);
				fosSubject.write(mailSubjectBytes);
				fosSubject.flush();
				fosSubject.close();

			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			String templateBodyString = emailTemplate.getPath().substring(
					emailTemplate.getParent().length() + 1);
			String templateSubjectString = emailSubjectTemplate.getPath()
					.substring(emailSubjectTemplate.getParent().length() + 1);

			StringBuilder subjectText = new StringBuilder("");

			subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateSubjectString, modelMap));

			StringBuilder bodyText = new StringBuilder("");

			bodyText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateBodyString, modelMap));

			email.setBody(bodyText.toString());
			email.setSubject(subjectText.toString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
				emailSubjectTemplate.delete();
			}
		}

	}

	private void saveReminderEmailForEmployee(
			List<EmployeeFieldDTO> empListToSendReminderMail, Date lastRunDate,
			Company company, String overtimeShiftType) {

		Long companyId = company.getCompanyId();
		TimesheetBatch batch = timesheetBatchDAO.findBatchByDate(new Timestamp(
				new Date().getTime()), companyId);

		for (EmployeeFieldDTO employeeFieldDto : empListToSendReminderMail) {

			String eventName = PayAsiaConstants.LUNDIN_TIMESHEET_EVENT_REMINDER_APPLY;

			ReminderEventConfig reminDerEventConfig = null;

			reminDerEventConfig = reminderEventConfigDAO.findByEvent(eventName,
					companyId);
			LeaveReminderDTO leaveReminderDTO = new LeaveReminderDTO();
			leaveReminderDTO.setOvertimeShiftType(overtimeShiftType);
			if (reminDerEventConfig != null) {

				String emailTo = employeeFieldDto.getEmployeeEmail();
				Email email = new Email();

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date batchEndDate = new Date(batch.getEndDate().getTime());

				Date sDate = new Date(batch.getStartDate().getTime());
				cal.setTime(sDate);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date startDate = cal.getTime();
				Date eDate = new Date(batch.getEndDate().getTime());
				cal.setTime(eDate);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date endDate = cal.getTime();
				List<Date> holidayDates = getHolidaysFor(
						employeeFieldDto.getEmployeeId(), startDate, endDate);

				EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
						.findByConditionCompany(companyId);

				if (emailPreferenceMasterVO != null) {

					email.setCompany(company);
					email.setEmailFrom(emailPreferenceMasterVO
							.getContactEmail());
					email.setEmailTo(emailTo);
					leaveReminderDTO.setURL(emailPreferenceMasterVO
							.getCompanyUrl());

					if (lastRunDate.compareTo(batchEndDate) > 0) {
						saveRemEmailAfterEventForEmp(lastRunDate,
								reminDerEventConfig, email, batchEndDate,
								employeeFieldDto, batch, leaveReminderDTO,
								holidayDates);
					}
				}

			}
		}
	}

	private void saveRemEmailBeforeEventForEmp(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Email email,
			Date timesheetCreatedDate, EmployeeFieldDTO employeeFieldDto,
			TimesheetBatch batch, LeaveReminderDTO leaveReminderDTO) {

		if (!reminDerEventConfig.isAllowSendMailBeforeEventDay()) {
			return;
		}
		if (reminDerEventConfig.getSendMailBeforeMailTemplate() == null) {
			return;

		} else {

			setEmailObject(email,
					reminDerEventConfig.getSendMailBeforeMailTemplate());

		}
		if (reminDerEventConfig.getSendMailBeforeDays() == null) {

			return;
		}
		Date sendMailBeforeDaysDate = DateUtils.addDays(timesheetCreatedDate,
				reminDerEventConfig.getSendMailBeforeDays() * -1);

		Date dateCtr;
		for (dateCtr = sendMailBeforeDaysDate; dateCtr
				.before(timesheetCreatedDate)
				|| dateCtr.equals(timesheetCreatedDate); dateCtr = DateUtils
				.addDays(dateCtr,
						reminDerEventConfig.getSendMailBeforeRepeatDays())) {

			if (DateUtils.isSameDay(dateCtr, currentDate)) {
				saveTimesheetEmailObject(email, employeeFieldDto, batch,
						leaveReminderDTO);
				email.setCreatedDate(com.payasia.common.util.DateUtils
						.getCurrentTimestamp());
				if (StringUtils.isNotBlank(email.getEmailTo())
						&& StringUtils.isNotBlank(email.getEmailFrom())) {
					emailDAO.saveReturn(email);
				}
			}

		}

	}

	private void saveRemEmailAfterEventForEmp(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Email email,
			Date batchEndDate, EmployeeFieldDTO employeeFieldDto,
			TimesheetBatch batch, LeaveReminderDTO leaveReminderDTO,
			List<Date> holidayDates) {

		if (!reminDerEventConfig.isAllowSendMailAfterEventDay()) {

			return;
		}

		if (reminDerEventConfig.getSendMailAfterMailTemplate() == null) {
			return;
		} else {
			setEmailObject(email,
					reminDerEventConfig.getSendMailAfterMailTemplate());

		}
		Date sendMailAfterTillDaysDate = null;
		if (reminDerEventConfig.getSendMailAfterTillDays() > 0) {

			sendMailAfterTillDaysDate = DateUtils.addDays(batchEndDate,
					reminDerEventConfig.getSendMailAfterDays()
							+ reminDerEventConfig.getSendMailAfterTillDays());
		} else {

			TimesheetBatch nextBatch = timesheetBatchDAO
					.findCurrentBatchForDate(
							new Timestamp(new Date().getTime()), batch
									.getCompany().getCompanyId());
			sendMailAfterTillDaysDate = new Date(nextBatch.getEndDate()
					.getTime());
		}

		if (sendMailAfterTillDaysDate.compareTo(currentDate) > 0
				&& currentDate.compareTo(batchEndDate) > 0) {

			Date dateCtr;
			Date dateAfterDays = DateUtils.addDays(batchEndDate,
					reminDerEventConfig.getSendMailAfterDays());

			if (reminDerEventConfig.getSendMailAfterRepeatDays() > 0) {

				for (dateCtr = dateAfterDays; dateCtr
						.before(sendMailAfterTillDaysDate)
						|| dateCtr.equals(sendMailAfterTillDaysDate); dateCtr = DateUtils
						.addDays(dateCtr, reminDerEventConfig
								.getSendMailAfterRepeatDays())) {

					if (DateUtils.isSameDay(dateCtr, currentDate)) {

						Calendar cal = Calendar.getInstance();
						cal.setTime(dateCtr);
						int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

						if (!holidayDates.contains(dateCtr)
								&& (dayOfWeek != 1 && dayOfWeek != 7)) {
							saveTimesheetEmailObject(email, employeeFieldDto,
									batch, leaveReminderDTO);
							email.setCreatedDate(com.payasia.common.util.DateUtils
									.getCurrentTimestamp());
							if (StringUtils.isNotBlank(email.getEmailTo())
									&& StringUtils.isNotBlank(email
											.getEmailFrom())) {
								emailDAO.saveReturn(email);
							}
						}
					}
				}
			} else if (reminDerEventConfig.getSendMailAfterRepeatDays() == 0) {

				dateCtr = dateAfterDays;
				if (DateUtils.isSameDay(dateCtr, currentDate)) {

					Calendar cal = Calendar.getInstance();
					cal.setTime(dateCtr);
					int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

					if (!holidayDates.contains(dateCtr)
							&& (dayOfWeek != 1 && dayOfWeek != 7)) {
						saveTimesheetEmailObject(email, employeeFieldDto,
								batch, leaveReminderDTO);
						email.setCreatedDate(com.payasia.common.util.DateUtils
								.getCurrentTimestamp());
						if (StringUtils.isNotBlank(email.getEmailTo())
								&& StringUtils.isNotBlank(email.getEmailFrom())) {
							emailDAO.saveReturn(email);
						}
					}
				}
			}

		}
	}

	private void saveRemEmailOnEventForEmp(
			ReminderEventConfig reminDerEventConfig, Email email,
			EmployeeFieldDTO employeeFieldDto, TimesheetBatch batch,
			LeaveReminderDTO leaveReminderDTO) {

		if (reminDerEventConfig.isAllowSendMailOnEventDay()) {

			if (reminDerEventConfig.getSendMailOnEventMailTemplate() != null) {

				setEmailObject(email,
						reminDerEventConfig.getSendMailOnEventMailTemplate());

				saveTimesheetEmailObject(email, employeeFieldDto, batch,
						leaveReminderDTO);

				email.setCreatedDate(com.payasia.common.util.DateUtils
						.getCurrentTimestamp());
				if (StringUtils.isNotBlank(email.getEmailTo())
						&& StringUtils.isNotBlank(email.getEmailFrom())) {
					emailDAO.saveReturn(email);
				}

			}

		}

	}

	private void saveTimesheetEmailObject(Email email,
			EmployeeFieldDTO employeeFieldDto, TimesheetBatch batch,
			LeaveReminderDTO leaveReminderDTO) {

		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NAME,
				employeeFieldDto.getEmployeeName());
		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NUMBER,
				employeeFieldDto.getEmployeeNumber());
		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_BATCH,
				batch.getTimesheetBatchDesc());
		modelMap.put(PayAsiaConstants.COHERENT_OVERTIME_SHIFT_TYPE_PLACEHOLDER,
				leaveReminderDTO.getOvertimeShiftType());

		if (leaveReminderDTO.getURL() != null) {
			String link = "<a href='" + leaveReminderDTO.getURL() + "'>"
					+ leaveReminderDTO.getURL() + "</a>";
			modelMap.put(PayAsiaConstants.URL, link);
		} else {
			modelMap.put(PayAsiaConstants.URL, "");
		}

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;
		try {
			byte[] mailBodyBytes = email.getBody().getBytes();
			byte[] mailSubjectBytes = email.getSubject().getBytes();

			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);

			emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendRemMailTemplate" + employeeFieldDto.getEmployeeId()
					+ "_" + uniqueId + ".vm");
			emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendRemTemplateSubject"
					+ employeeFieldDto.getEmployeeId() + "_" + uniqueId + ".vm");
			emailSubjectTemplate.deleteOnExit();
			emailTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
				fosSubject = new FileOutputStream(emailSubjectTemplate);
				fosSubject.write(mailSubjectBytes);
				fosSubject.flush();
				fosSubject.close();

			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			String templateBodyString = emailTemplate.getPath().substring(
					emailTemplate.getParent().length() + 1);
			String templateSubjectString = emailSubjectTemplate.getPath()
					.substring(emailSubjectTemplate.getParent().length() + 1);

			StringBuilder subjectText = new StringBuilder("");

			subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateSubjectString, modelMap));

			StringBuilder bodyText = new StringBuilder("");

			bodyText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateBodyString, modelMap));

			email.setBody(bodyText.toString());
			email.setSubject(subjectText.toString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
				emailSubjectTemplate.delete();
			}
		}

	}

	private List<Date> getHolidaysFor(Long employeeId, Date startDate,
			Date endDate) {
		EmployeeHolidayCalendar empHolidaycalendar = employeeHolidayCalendarDAO
				.getCalendarDetail(employeeId, startDate, endDate);
		List<Date> toReturn = new ArrayList<Date>();
		if (empHolidaycalendar != null) {
			CompanyHolidayCalendar compHolidayCalendar = empHolidaycalendar
					.getCompanyHolidayCalendar();
			Set<CompanyHolidayCalendarDetail> calDetails = compHolidayCalendar
					.getCompanyHolidayCalendarDetails();
			Date holidayDt = new Date();
			for (CompanyHolidayCalendarDetail compHolidayDetail : calDetails) {
				holidayDt = new Date(compHolidayDetail.getHolidayDate()
						.getTime());
				if (startDate.compareTo(holidayDt) < 0
						&& endDate.compareTo(holidayDt) > 0) {
					toReturn.add(holidayDt);
				}

			}
		}
		return toReturn;
	}

	private void keyPayIntegrationProcessSchedular(Company company,
			Date currentDate) {
		try {
			IntegrationMaster integrationMasterVO = integrationMasterDAO
					.findByCondition(company.getCompanyId());
			if (integrationMasterVO != null) {
				String baseURL = integrationMasterVO.getBaseURL()
						+ integrationMasterVO.getExternalCompanyId();

				// Update employee external Id
				keyPayIntLogic.updateEmployeeExternalId(company.getCompanyId(),
						baseURL, integrationMasterVO.getApiKey());

				// Get Leave Category Details
				Map<String, Long> leaveCategoryMap = keyPayIntLogic
						.getLeaveCategoryDetails(baseURL,
								integrationMasterVO.getApiKey());

				// Send Approved Leave
				keyPayIntLogic.sendApprovedLeaveApp(company.getCompanyId(),
						leaveCategoryMap, baseURL,
						integrationMasterVO.getApiKey());
				// Send Cancelled Leave
				keyPayIntLogic.sendCancelledLeaveApp(company.getCompanyId(),
						leaveCategoryMap, baseURL,
						integrationMasterVO.getApiKey());

				// Get PayRun And sync with Payasia
				keyPayIntLogic.getPayRunDetails(company.getCompanyId(),
						baseURL, integrationMasterVO.getApiKey());
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
	}

	/**
	 * @param currentDate
	 * @param company
	 */
	private void autoApprovalLeaveSchedular(Date currentDate, Company company) {
		UserContext.setWorkingCompanyDateFormat(company.getDateFormat());
		UserContext.setWorkingCompanyId(String.valueOf(company.getCompanyId()));

		// Get Pending Leaves
		List<Object[]> leaveApplicationList = leaveApplicationDAO
				.getLeaveAppForAutoApproval(company.getCompanyId());
		// Get System Email
		EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO
				.findByConditionCompany(company.getCompanyId());
		if (emailPreferenceMaster != null
				&& StringUtils.isNotBlank(emailPreferenceMaster
						.getSystemEmailForSendingEmails())) {
			String systemEmail = "";
			systemEmail = emailPreferenceMaster
					.getSystemEmailForSendingEmails();
			// Get System Employee
			Employee systemEmployee = employeeDAO.findByNumber(
					PayAsiaConstants.PAYASIA_SYSTEM_EMPLOYEE_NUMBER,
					PayAsiaConstants.PAYASIA_COMPANY_ID);

			if (leaveApplicationList != null && !leaveApplicationList.isEmpty()) {
				for (Object[] object : leaveApplicationList) {

					PendingItemsForm pendingItemsForm = new PendingItemsForm();
					pendingItemsForm.setLeaveApplicationReviewerId(Long
							.valueOf(object[4].toString()));
					pendingItemsForm
							.setRemarks(PayAsiaConstants.LEAVE_AUTO_APPROVAL_BY_THE_SYSTEM);
					pendingItemsForm.setSystemEmail(systemEmail);

					// Get Locale
					String shortCompanyCode = companyInformationLogic
							.getShortCompanyCode(company.getCompanyId());
					String localeParam = "en_US";
					Locale newLocale = getNewLocale(localeParam,
							shortCompanyCode);
					UserContext.setLocale(newLocale);

					LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
					sessionDTO
							.setFromSessionName(messageSource
									.getMessage(
											PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
											new Object[] {}, newLocale));
					sessionDTO.setToSessionName(messageSource.getMessage(
							PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
							new Object[] {}, newLocale));

					UserContext.setUserId(String.valueOf(systemEmployee
							.getEmployeeId()));

					// Approve Leaves
					pendingItemsLogic.acceptLeaveforAdmin(pendingItemsForm,
							systemEmployee.getEmployeeId(), sessionDTO);

				}
			}
		}
	}

	/**
	 * Purpose : Get New Locale with specified Script(As short company Code).
	 * 
	 * @param localeParam
	 *            the locale Parameter
	 * @param shortcompanyCode
	 *            the short company Code
	 * @return the New Locale
	 */
	public Locale getNewLocale(String localeParam, String shortCompanyCode) {
		Builder localeBuilder = new Locale.Builder();
		localeBuilder.setLocale(org.springframework.util.StringUtils
				.parseLocaleString(localeParam));

		if (StringUtils.isNotBlank(shortCompanyCode)) {
			localeBuilder.setScript(shortCompanyCode);
		}
		Locale newLocale = localeBuilder.build();

		return newLocale;
	}

	/**
	 * @param currentDate
	 * @param company
	 */
	private void leaveTypeActivationScheduler(Date currentDate, Company company) {

		List<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves = leaveSchemeTypeAvailingLeaveDAO
				.findByCompanyId(company.getCompanyId());
		if (leaveSchemeTypeAvailingLeaves != null
				&& !leaveSchemeTypeAvailingLeaves.isEmpty()) {
			for (LeaveSchemeTypeAvailingLeave schemeTypeAvailingLeave : leaveSchemeTypeAvailingLeaves) {
				if (schemeTypeAvailingLeave.getLeaveVisibilityStartDate() == null
						&& schemeTypeAvailingLeave.getLeaveVisibilityEndDate() == null) {
					LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO
							.findById(schemeTypeAvailingLeave
									.getLeaveSchemeType()
									.getLeaveSchemeTypeId());
					leaveSchemeTypeVO.setVisibility(true);
					leaveSchemeTypeDAO.update(leaveSchemeTypeVO);
					continue;
				}

				Date startDate = new Date(schemeTypeAvailingLeave
						.getLeaveVisibilityStartDate().getTime());
				Date endDate = new Date(schemeTypeAvailingLeave
						.getLeaveVisibilityEndDate().getTime());

				if ((currentDate.after(startDate) && currentDate
						.before(endDate))
						|| org.apache.commons.lang.time.DateUtils.isSameDay(
								currentDate, startDate)
						|| org.apache.commons.lang.time.DateUtils.isSameDay(
								currentDate, endDate)) {
					LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO
							.findById(schemeTypeAvailingLeave
									.getLeaveSchemeType()
									.getLeaveSchemeTypeId());
					leaveSchemeTypeVO.setVisibility(true);
					leaveSchemeTypeDAO.update(leaveSchemeTypeVO);

				} else {
					LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO
							.findById(schemeTypeAvailingLeave
									.getLeaveSchemeType()
									.getLeaveSchemeTypeId());
					leaveSchemeTypeVO.setVisibility(false);
					leaveSchemeTypeDAO.update(leaveSchemeTypeVO);
				}
			}
		}
	}

	private void lionTimesheetReminderScheduler(Date currentDate,
			Company company) {
		List<String> timesheetStatusNames = new ArrayList<>();
		timesheetStatusNames
				.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED);

		try {
			// For Reviewers
			List<LionTimesheetApplicationReviewer> lionTimesheetReviewerVOs = lionTimesheetApplicationReviewerDAO
					.findByTimesheetStatus(timesheetStatusNames,
							company.getCompanyId());
			if (!lionTimesheetReviewerVOs.isEmpty()) {
				saveReminderEmailForLionTimesheetForRev(
						lionTimesheetReviewerVOs, currentDate,
						company.getCompanyId());
			}

			// For Employee
			saveLionTSReminderEmailForEmployee(currentDate, company);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void saveReminderEmailForLionTimesheetForRev(
			List<LionTimesheetApplicationReviewer> lionTimesheetReviewerVOs,
			Date lastRunDate, Long companyId) {

		TimesheetBatch batch = lundinTimesheetDAO.findCurrentBatchForDate(
				new Timestamp(new Date().getTime()), companyId);

		for (LionTimesheetApplicationReviewer lionTimesheetReviewer : lionTimesheetReviewerVOs) {
			String eventName = null;
			if (lionTimesheetReviewer
					.getLionEmployeeTimesheetApplicationDetail()
					.getEmployeeTimesheetApplication()
					.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED)) {
				eventName = PayAsiaConstants.LUNDIN_TIMESHEET_EVENT_REMINDER_APPROVE;
			}

			ReminderEventConfig reminDerEventConfig = null;

			reminDerEventConfig = reminderEventConfigDAO.findByEvent(eventName,
					companyId);
			LeaveReminderDTO leaveReminderDTO = new LeaveReminderDTO();
			if (reminDerEventConfig != null) {

				String emailTo = getLionTimesheetEmailId(lionTimesheetReviewer
						.getLionEmployeeTimesheetApplicationDetail()
						.getEmployeeTimesheetApplication(),
						lionTimesheetReviewer, reminDerEventConfig,
						leaveReminderDTO);
				if (StringUtils.isBlank(emailTo)) {
					emailTo = lionTimesheetReviewer.getReviewerEmail();
				}

				Email email = new Email();

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				// Date timesheetCreatedDate = new Date(lundinTimesheetReviewer
				// .getLundinTimesheet().getUpdatedDate().getTime());

				EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
						.findByConditionCompany(lionTimesheetReviewer
								.getLionEmployeeTimesheetApplicationDetail()
								.getEmployeeTimesheetApplication().getCompany()
								.getCompanyId());
				if (emailPreferenceMasterVO != null) {
					email.setCompany(lionTimesheetReviewer
							.getLionEmployeeTimesheetApplicationDetail()
							.getEmployeeTimesheetApplication().getCompany());
					email.setEmailFrom(emailPreferenceMasterVO
							.getContactEmail());
					email.setEmailTo(emailTo);

					Long employeeId = lionTimesheetReviewer
							.getEmployeeReviewer().getEmployeeId();
					Date sDate = new Date(batch.getStartDate().getTime());
					cal.setTime(sDate);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					Date startDate = cal.getTime();

					// Lion Timesheet Per Day Submitted Date
					Date eDate = new Date(lionTimesheetReviewer
							.getLionEmployeeTimesheetApplicationDetail()
							.getUpdatedDate().getTime());
					cal.setTime(eDate);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					Date endDate = cal.getTime();
					List<Date> holidayDates = getHolidaysFor(employeeId,
							startDate, endDate);

					if (lastRunDate.compareTo(endDate) > 0) {
						saveLionTSRemEmailAfterEventForRev(
								lastRunDate,
								reminDerEventConfig,
								email,
								endDate,
								lionTimesheetReviewer
										.getLionEmployeeTimesheetApplicationDetail()
										.getEmployeeTimesheetApplication(),
								leaveReminderDTO, holidayDates);
					}
				}

			}
		}
	}

	private void saveLionTSRemEmailAfterEventForRev(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Email email,
			Date timesheetDetailPerDaySubmittedDate,
			EmployeeTimesheetApplication employeeTimesheetApplication,
			LeaveReminderDTO leaveReminderDTO, List<Date> holidayDates) {

		if (!reminDerEventConfig.isAllowSendMailAfterEventDay()) {

			return;
		}

		if (reminDerEventConfig.getSendMailAfterMailTemplate() == null) {
			return;
		} else {
			setEmailObject(email,
					reminDerEventConfig.getSendMailAfterMailTemplate());

		}
		Date sendMailAfterTillDaysDate = null;
		if (reminDerEventConfig.getSendMailAfterTillDays() > 0) {

			sendMailAfterTillDaysDate = DateUtils.addDays(
					timesheetDetailPerDaySubmittedDate,
					reminDerEventConfig.getSendMailAfterDays()
							+ reminDerEventConfig.getSendMailAfterTillDays());
		} else {
			TimesheetBatch nextBatch = lundinTimesheetDAO
					.findCurrentBatchForDate(
							new Timestamp(new Date().getTime()),
							employeeTimesheetApplication.getCompany()
									.getCompanyId());
			sendMailAfterTillDaysDate = new Date(nextBatch.getEndDate()
					.getTime());
		}

		if (sendMailAfterTillDaysDate.compareTo(currentDate) > 0
				&& currentDate.compareTo(timesheetDetailPerDaySubmittedDate) > 0) {
			Date dateCtr;
			Date dateAfterDays = DateUtils.addDays(
					timesheetDetailPerDaySubmittedDate,
					reminDerEventConfig.getSendMailAfterDays());

			if (reminDerEventConfig.getSendMailAfterRepeatDays() > 0) {
				for (dateCtr = dateAfterDays; dateCtr
						.before(sendMailAfterTillDaysDate)
						|| dateCtr.equals(sendMailAfterTillDaysDate); dateCtr = DateUtils
						.addDays(dateCtr, reminDerEventConfig
								.getSendMailAfterRepeatDays())) {
					if (DateUtils.isSameDay(dateCtr, currentDate)) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(dateCtr);
						int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

						if (!holidayDates.contains(dateCtr)
								&& (dayOfWeek != 1 && dayOfWeek != 7)) {
							saveTimesheetEmailObject(email,
									employeeTimesheetApplication,
									leaveReminderDTO);
							email.setCreatedDate(com.payasia.common.util.DateUtils
									.getCurrentTimestamp());
							emailDAO.saveReturn(email);
						}
					}
				}
			} else if (reminDerEventConfig.getSendMailAfterRepeatDays() == 0) {
				dateCtr = dateAfterDays;
				if (DateUtils.isSameDay(dateCtr, currentDate)) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dateCtr);
					int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

					if (!holidayDates.contains(dateCtr)
							&& (dayOfWeek != 1 && dayOfWeek != 7)) {
						saveTimesheetEmailObject(email,
								employeeTimesheetApplication, leaveReminderDTO);
						email.setCreatedDate(com.payasia.common.util.DateUtils
								.getCurrentTimestamp());
						emailDAO.saveReturn(email);
					}
				}
			}
		}
	}

	private void saveLionTSReminderEmailForEmployee(Date lastRunDate,
			Company company) {

		Long companyId = company.getCompanyId();
		TimesheetBatch batch = lundinTimesheetDAO.findCurrentBatchForDate(
				new Timestamp(new Date().getTime()), companyId);

		Date batchStartDate = new Date(batch.getStartDate().getTime());

		String eventName = PayAsiaConstants.LUNDIN_TIMESHEET_EVENT_REMINDER_APPLY;
		ReminderEventConfig reminDerEventConfig = reminderEventConfigDAO
				.findByEvent(eventName, companyId);

		SimpleDateFormat sdf = new SimpleDateFormat(
				PayAsiaConstants.DEFAULT_DATE_FORMAT);

		Date dateAfterDays = DateUtils.addDays(
				new Timestamp(new Date().getTime()),
				-reminDerEventConfig.getSendMailAfterDays());
		List<LionTimesheetDetailDTO> LionTimesheetDetailDTOList = employeeTimesheetApplicationDAO
				.getlionTimesheetNotFilledDetailProc(companyId,
						com.payasia.common.util.DateUtils.stringToTimestamp(
								sdf.format(batchStartDate),
								PayAsiaConstants.DEFAULT_DATE_FORMAT),
						com.payasia.common.util.DateUtils.stringToTimestamp(
								sdf.format(dateAfterDays),
								PayAsiaConstants.DEFAULT_DATE_FORMAT));

		// Get All Employees By company
		Map<Long, Employee> employeeIdMap = new HashMap<Long, Employee>();
		List<Employee> employeeVOList = employeeDAO.findByCompany(companyId);
		for (Employee employee : employeeVOList) {
			if (employee.isStatus()) {
				employeeIdMap.put(employee.getEmployeeId(), employee);
			}
		}

		for (LionTimesheetDetailDTO lionTimesheetDetailDTO : LionTimesheetDetailDTOList) {
			LeaveReminderDTO leaveReminderDTO = new LeaveReminderDTO();
			if (reminDerEventConfig != null) {
				Employee employeeVO = employeeIdMap.get(lionTimesheetDetailDTO
						.getEmployeeId());
				String emailTo = employeeVO.getEmail();

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date sDate = new Date(batch.getStartDate().getTime());
				cal.setTime(sDate);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date startDate = cal.getTime();
				Date eDate = new Date(batch.getEndDate().getTime());
				cal.setTime(eDate);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date endDate = cal.getTime();
				List<Date> holidayDates = getHolidaysFor(
						lionTimesheetDetailDTO.getEmployeeId(), startDate,
						endDate);

				EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
						.findByConditionCompany(companyId);

				if (emailPreferenceMasterVO != null) {

					leaveReminderDTO.setEmailFrom(emailPreferenceMasterVO
							.getContactEmail());
					leaveReminderDTO.setEmailTo(emailTo);
					leaveReminderDTO.setURL(emailPreferenceMasterVO
							.getCompanyUrl());

					if (lastRunDate.compareTo(batchStartDate) > 0) {

						saveLionTSRemEmailAfterEventForEmp(lastRunDate,
								reminDerEventConfig, batchStartDate,
								employeeVO, batch, leaveReminderDTO,
								holidayDates, lionTimesheetDetailDTO);
					}
				}

			}
		}
	}

	private void saveLionTSRemEmailAfterEventForEmp(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Date batchStartDate,
			Employee employeeVO, TimesheetBatch batch,
			LeaveReminderDTO leaveReminderDTO, List<Date> holidayDates,
			LionTimesheetDetailDTO lionTimesheetDetailDTO) {

		if (!reminDerEventConfig.isAllowSendMailAfterEventDay()) {

			return;
		}

		if (reminDerEventConfig.getSendMailAfterMailTemplate() == null) {
			return;
		}
		Date sendMailAfterTillDaysDate = null;
		List<Date> timesheetDateList = new ArrayList<Date>();
		HashMap<Date, Date> timesheetDateTillDateList = new HashMap<Date, Date>();
		int sendMailAfterTillDays = 0;
		if (reminDerEventConfig.getSendMailAfterTillDays() > 0) {
			sendMailAfterTillDays = reminDerEventConfig
					.getSendMailAfterTillDays();
		} else {
			TimesheetBatch currentBatch = lundinTimesheetDAO
					.findCurrentBatchForDate(
							new Timestamp(new Date().getTime()), batch
									.getCompany().getCompanyId());
			sendMailAfterTillDaysDate = new Date(currentBatch.getEndDate()
					.getTime());
		}

		String finalTimesheetDates = "";
		if (StringUtils.isNotBlank(lionTimesheetDetailDTO.getTimesheetDate())) {
			String[] timesheetDates = lionTimesheetDetailDTO.getTimesheetDate()
					.split(",");
			if (timesheetDates.length > 0
					&& StringUtils.isNotBlank(timesheetDates[0])) {
				for (String timesheetDate : timesheetDates) {
					if (StringUtils.isBlank(timesheetDate)) {
						continue;
					}
					Date date = com.payasia.common.util.DateUtils.stringToDate(
							timesheetDate, employeeVO.getCompany()
									.getDateFormat());
					if (date == null) {
						date = com.payasia.common.util.DateUtils.stringToDate(
								timesheetDate,
								PayAsiaConstants.DEFAULT_DATE_FORMAT);
					}
					if (date != null) {
						Date sendMailTillDaysDate = DateUtils.addDays(date,
								sendMailAfterTillDays);
						if (sendMailTillDaysDate.compareTo(currentDate) > 0
								|| sendMailTillDaysDate.equals(currentDate)) {
							finalTimesheetDates = finalTimesheetDates
									+ timesheetDate + ',';
							timesheetDateList.add(date);
							timesheetDateTillDateList.put(date,
									sendMailTillDaysDate);
						}
					}
				}
			}
		}
		finalTimesheetDates = StringUtils.removeEnd(finalTimesheetDates, ",");

		for (Date timesheetDate : timesheetDateList) {
			Date dateCtr;
			if (reminDerEventConfig.getSendMailAfterRepeatDays() > 0) {
				if (reminDerEventConfig.getSendMailAfterTillDays() > 0) {
					sendMailAfterTillDaysDate = timesheetDateTillDateList
							.get(timesheetDate);
				}
				for (dateCtr = timesheetDate; dateCtr
						.before(sendMailAfterTillDaysDate)
						|| dateCtr.equals(sendMailAfterTillDaysDate); dateCtr = DateUtils
						.addDays(dateCtr, reminDerEventConfig
								.getSendMailAfterRepeatDays())) {
					if (DateUtils.isSameDay(dateCtr, currentDate)) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(dateCtr);
						int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

						if (!holidayDates.contains(dateCtr)
								&& (dayOfWeek != 1 && dayOfWeek != 7)) {
							Email email = new Email();
							email.setCompany(employeeVO.getCompany());
							email.setEmailTo(leaveReminderDTO.getEmailTo());
							email.setEmailFrom(leaveReminderDTO.getEmailFrom());
							if (reminDerEventConfig
									.getSendMailAfterMailTemplate() != null) {
								setEmailObject(email,
										reminDerEventConfig
												.getSendMailAfterMailTemplate());
							}
							saveLionTimesheetEmailObject(email, employeeVO,
									batch, leaveReminderDTO,
									lionTimesheetDetailDTO, timesheetDate);
							email.setCreatedDate(com.payasia.common.util.DateUtils
									.getCurrentTimestamp());
							emailDAO.saveReturn(email);
						}
					}
				}
			} else if (reminDerEventConfig.getSendMailAfterRepeatDays() == 0) {
				dateCtr = timesheetDate;
				if (DateUtils.isSameDay(dateCtr, currentDate)) {

					Calendar cal = Calendar.getInstance();
					cal.setTime(dateCtr);
					int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

					if (!holidayDates.contains(dateCtr)
							&& (dayOfWeek != 1 && dayOfWeek != 7)) {
						Email email = new Email();
						email.setCompany(employeeVO.getCompany());
						email.setEmailTo(leaveReminderDTO.getEmailTo());
						email.setEmailFrom(leaveReminderDTO.getEmailFrom());
						if (reminDerEventConfig.getSendMailAfterMailTemplate() != null) {
							setEmailObject(email,
									reminDerEventConfig
											.getSendMailAfterMailTemplate());
						}
						saveLionTimesheetEmailObject(email, employeeVO, batch,
								leaveReminderDTO, lionTimesheetDetailDTO,
								timesheetDate);
						email.setCreatedDate(com.payasia.common.util.DateUtils
								.getCurrentTimestamp());
						emailDAO.saveReturn(email);
					}
				}
			}
		}

	}

	private void saveLionTimesheetEmailObject(Email email, Employee employeeVO,
			TimesheetBatch batch, LeaveReminderDTO leaveReminderDTO,
			LionTimesheetDetailDTO lionTimesheetDetailDTO, Date timesheetDate) {

		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NAME,
				getEmployeeName(employeeVO));
		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NUMBER,
				employeeVO.getEmployeeNumber());
		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_BATCH,
				batch.getTimesheetBatchDesc());
		modelMap.put(PayAsiaConstants.LION_TIMESHEET_EMAIL_BATCH_PER_DAY_DATE,
				com.payasia.common.util.DateUtils.timeStampToString(
						com.payasia.common.util.DateUtils
								.convertDateToTimeStamp(timesheetDate),
						employeeVO.getCompany().getDateFormat()));

		if (leaveReminderDTO.getURL() != null) {
			String link = "<a href='" + leaveReminderDTO.getURL() + "'>"
					+ leaveReminderDTO.getURL() + "</a>";
			modelMap.put(PayAsiaConstants.URL, link);
		} else {
			modelMap.put(PayAsiaConstants.URL, "");
		}

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;
		try {
			byte[] mailBodyBytes = email.getBody().getBytes();
			byte[] mailSubjectBytes = email.getSubject().getBytes();

			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);

			emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendRemMailTemplate" + employeeVO.getEmployeeId() + "_"
					+ uniqueId + ".vm");
			emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendRemTemplateSubject" + employeeVO.getEmployeeId()
					+ "_" + uniqueId + ".vm");
			emailSubjectTemplate.deleteOnExit();
			emailTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
				fosSubject = new FileOutputStream(emailSubjectTemplate);
				fosSubject.write(mailSubjectBytes);
				fosSubject.flush();
				fosSubject.close();

			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			String templateBodyString = emailTemplate.getPath().substring(
					emailTemplate.getParent().length() + 1);
			String templateSubjectString = emailSubjectTemplate.getPath()
					.substring(emailSubjectTemplate.getParent().length() + 1);

			StringBuilder subjectText = new StringBuilder("");

			subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateSubjectString, modelMap));

			StringBuilder bodyText = new StringBuilder("");

			bodyText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateBodyString, modelMap));

			email.setBody(bodyText.toString());
			email.setSubject(subjectText.toString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
				emailSubjectTemplate.delete();
			}
		}

	}

	private String getLionTimesheetEmailId(
			EmployeeTimesheetApplication lionTimesheet,
			LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer,
			ReminderEventConfig reminDerEventConfig,
			LeaveReminderDTO leaveReminderDTO) {

		String emailId = "";

		String recepientType = reminDerEventConfig.getRecipientType()
				.getCodeDesc();

		switch (recepientType) {
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE_USER:

			Employee employeeUser = employeeDAO.findByNumber(
					reminDerEventConfig.getRecipientValue(), lionTimesheet
							.getCompany().getCompanyId());
			if (employeeUser != null) {

				emailId = employeeUser.getEmail();
				if (employeeUser.getCompany().getEmailPreferenceMasters() != null) {
					leaveReminderDTO.setURL(employeeUser.getCompany()
							.getEmailPreferenceMasters().iterator().next()
							.getCompanyUrl());
				}

			}

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EXTERNAL_ENTITY:
			emailId = reminDerEventConfig.getRecipientValue();

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_CURRENT_OWNER:
			LionTimesheetApplicationReviewer lundinTimesheetReviewers = lionTimesheetApplicationReviewer;
			if (lundinTimesheetReviewers.isPending()) {
				emailId = lundinTimesheetReviewers.getEmployeeReviewer()
						.getEmail();
				if (lundinTimesheetReviewers.getEmployeeReviewer().getCompany()
						.getEmailPreferenceMasters() != null) {
					leaveReminderDTO.setURL(lundinTimesheetReviewers
							.getEmployeeReviewer().getCompany()
							.getEmailPreferenceMasters().iterator().next()
							.getCompanyUrl());
				}
			}

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE:
			emailId = lionTimesheet.getEmployee().getEmail();
			break;
		}
		return emailId;
	}

	private void coherentTimesheetReminderScheduler(Date currentDate,
			Company company, String moduleName) {
		List<String> timesheetStatusNames = new ArrayList<>();
		timesheetStatusNames
				.add(PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED);

		try {
			// For Overtime Reviewers
			List<CoherentOvertimeApplicationReviewer> coherentOvertimeApplicationReviewerVOs = coherentOvertimeApplicationReviewerDAO
					.findByTimesheetStatus(timesheetStatusNames,
							company.getCompanyId());
			if (!coherentOvertimeApplicationReviewerVOs.isEmpty()) {
				saveCoherentOvertimeTimesheetRevReminderEmail(
						coherentOvertimeApplicationReviewerVOs, currentDate,
						company.getCompanyId());
			}

			// For Shift Reviewers
			List<CoherentShiftApplicationReviewer> coherentShiftApplicationReviewerVOs = coherentShiftApplicationReviewerDAO
					.findByTimesheetStatus(timesheetStatusNames,
							company.getCompanyId());
			if (!coherentShiftApplicationReviewerVOs.isEmpty()) {
				saveCoherentShiftTimesheetRevReminderEmail(
						coherentShiftApplicationReviewerVOs, currentDate,
						company.getCompanyId());
			}

			List<EmployeeFieldDTO> empListForReminderMail = getEmpListToSendCoherentReminderMail(company);

			// For Overtime Employees
			List<EmployeeFieldDTO> empListForReminderMailForCoherent = new ArrayList<EmployeeFieldDTO>(
					empListForReminderMail);
			List<EmployeeFieldDTO> empListToSendOvertimeReminderMail = getNotFilledOvertimeTSEmpList(
					empListForReminderMailForCoherent, company.getCompanyId());
			if (!empListToSendOvertimeReminderMail.isEmpty()) {
				saveReminderEmailForEmployee(empListToSendOvertimeReminderMail,
						currentDate, company,
						PayAsiaConstants.COHERENT_OVERTIME_TYPE);
			}

			// For Shift Employees
			List<EmployeeFieldDTO> empListForReminderMailForShift = new ArrayList<EmployeeFieldDTO>(
					empListForReminderMail);
			List<EmployeeFieldDTO> empListToSendShiftReminderMail = getNotFilledShiftTSEmpList(
					empListForReminderMailForShift, company.getCompanyId());
			if (!empListToSendShiftReminderMail.isEmpty()) {
				saveReminderEmailForEmployee(empListToSendShiftReminderMail,
						currentDate, company,
						PayAsiaConstants.COHERENT_SHIFT_TYPE);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private List<EmployeeFieldDTO> getNotFilledOvertimeTSEmpList(
			List<EmployeeFieldDTO> employeeFieldDtoList, Long companyId) {

		try {
			if (!employeeFieldDtoList.isEmpty()) {

				TimesheetBatch batch = timesheetBatchDAO.findBatchByDate(
						new Timestamp(new Date().getTime()), companyId);
				List<CoherentOvertimeApplication> submittedTimesheet = new ArrayList<CoherentOvertimeApplication>();
				submittedTimesheet = coherentOvertimeApplicationDAO
						.findSubmitTimesheetEmp(batch.getTimesheetBatchId(),
								companyId);
				List<EmployeeFieldDTO> empIdToRemove = new ArrayList<EmployeeFieldDTO>();
				for (CoherentOvertimeApplication timesheet : submittedTimesheet) {
					EmployeeFieldDTO empDto = new EmployeeFieldDTO();
					Employee employee = timesheet.getEmployee();
					empDto.setEmployeeId(employee.getEmployeeId());
					empIdToRemove.add(empDto);
				}
				;
				employeeFieldDtoList.removeAll(empIdToRemove);
			}
			return employeeFieldDtoList;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return employeeFieldDtoList;
		}
	}

	private List<EmployeeFieldDTO> getNotFilledShiftTSEmpList(
			List<EmployeeFieldDTO> employeeFieldDtoList, Long companyId) {

		try {
			if (!employeeFieldDtoList.isEmpty()) {

				TimesheetBatch batch = timesheetBatchDAO.findBatchByDate(
						new Timestamp(new Date().getTime()), companyId);
				List<CoherentShiftApplication> submittedTimesheet = new ArrayList<CoherentShiftApplication>();
				submittedTimesheet = coherentShiftApplicationDAO
						.findSubmitTimesheetEmp(batch.getTimesheetBatchId(),
								companyId);
				List<EmployeeFieldDTO> empIdToRemove = new ArrayList<EmployeeFieldDTO>();
				for (CoherentShiftApplication timesheet : submittedTimesheet) {
					EmployeeFieldDTO empDto = new EmployeeFieldDTO();
					Employee employee = timesheet.getEmployee();
					empDto.setEmployeeId(employee.getEmployeeId());
					empIdToRemove.add(empDto);
				}
				;
				employeeFieldDtoList.removeAll(empIdToRemove);
			}
			return employeeFieldDtoList;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return employeeFieldDtoList;
		}
	}

	private void saveCoherentShiftTimesheetRevReminderEmail(
			List<CoherentShiftApplicationReviewer> coherentTimesheetReviewerVOs,
			Date lastRunDate, Long companyId) {

		TimesheetBatch batch = lundinTimesheetDAO.findCurrentBatchForDate(
				new Timestamp(new Date().getTime()), companyId);

		for (CoherentShiftApplicationReviewer coherentTimesheetReviewer : coherentTimesheetReviewerVOs) {
			String eventName = null;
			if (coherentTimesheetReviewer
					.getCoherentShiftApplication()
					.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED)) {
				eventName = PayAsiaConstants.LUNDIN_TIMESHEET_EVENT_REMINDER_APPROVE;
			}

			ReminderEventConfig reminDerEventConfig = null;

			reminDerEventConfig = reminderEventConfigDAO.findByEvent(eventName,
					companyId);
			LeaveReminderDTO leaveReminderDTO = new LeaveReminderDTO();
			if (reminDerEventConfig == null) {
				continue;
			}
			if (!coherentTimesheetReviewer.isPending()) {
				continue;
			}

			leaveReminderDTO
					.setEmployeeName(getEmployeeName(coherentTimesheetReviewer
							.getCoherentShiftApplication().getEmployee()));
			leaveReminderDTO.setTimesheetBatch(coherentTimesheetReviewer
					.getCoherentShiftApplication().getTimesheetBatch()
					.getTimesheetBatchDesc());
			leaveReminderDTO.setTimesheetId(coherentTimesheetReviewer
					.getCoherentShiftApplication().getShiftApplicationID());
			leaveReminderDTO.setEmployeeNumber(coherentTimesheetReviewer
					.getCoherentShiftApplication().getEmployee()
					.getEmployeeNumber());
			leaveReminderDTO
					.setOvertimeShiftType(PayAsiaConstants.COHERENT_SHIFT_TYPE);

			String emailTo = getCoherentShiftTimesheetEmailId(
					coherentTimesheetReviewer.getCoherentShiftApplication(),
					reminDerEventConfig, leaveReminderDTO);

			if (StringUtils.isBlank(emailTo)) {
				emailTo = coherentTimesheetReviewer.getEmployeeReviewer()
						.getEmail();
			}

			Email email = new Email();

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
					.findByConditionCompany(coherentTimesheetReviewer
							.getCoherentShiftApplication().getCompany()
							.getCompanyId());

			if (emailPreferenceMasterVO == null) {
				continue;
			}

			email.setCompany(coherentTimesheetReviewer
					.getCoherentShiftApplication().getCompany());
			email.setEmailFrom(emailPreferenceMasterVO.getContactEmail());
			email.setEmailTo(emailTo);

			Long employeeId = coherentTimesheetReviewer.getEmployeeReviewer()
					.getEmployeeId();
			Date sDate = new Date(batch.getStartDate().getTime());
			cal.setTime(sDate);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date startDate = cal.getTime();
			Date eDate = new Date(batch.getEndDate().getTime());
			cal.setTime(eDate);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date endDate = cal.getTime();
			List<Date> holidayDates = getHolidaysFor(employeeId, startDate,
					endDate);

			if (lastRunDate.compareTo(endDate) < 0) {
				saveTimesheetEmailBeforeEvent(lastRunDate, reminDerEventConfig,
						email, endDate, leaveReminderDTO, holidayDates);
			}
		}
	}

	private void saveCoherentOvertimeTimesheetRevReminderEmail(
			List<CoherentOvertimeApplicationReviewer> coherentTimesheetReviewerVOs,
			Date lastRunDate, Long companyId) {

		TimesheetBatch batch = lundinTimesheetDAO.findCurrentBatchForDate(
				new Timestamp(new Date().getTime()), companyId);

		for (CoherentOvertimeApplicationReviewer coherentTimesheetReviewer : coherentTimesheetReviewerVOs) {
			String eventName = null;
			if (coherentTimesheetReviewer
					.getCoherentOvertimeApplication()
					.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LUNDIN_TIMESHEET_STATUS_SUBMITTED)) {
				eventName = PayAsiaConstants.LUNDIN_TIMESHEET_EVENT_REMINDER_APPROVE;
			}

			ReminderEventConfig reminDerEventConfig = null;

			reminDerEventConfig = reminderEventConfigDAO.findByEvent(eventName,
					companyId);
			LeaveReminderDTO leaveReminderDTO = new LeaveReminderDTO();
			if (reminDerEventConfig == null) {
				continue;
			}
			if (!coherentTimesheetReviewer.isPending()) {
				continue;
			}

			leaveReminderDTO
					.setEmployeeName(getEmployeeName(coherentTimesheetReviewer
							.getCoherentOvertimeApplication().getEmployee()));
			leaveReminderDTO.setTimesheetBatch(coherentTimesheetReviewer
					.getCoherentOvertimeApplication().getTimesheetBatch()
					.getTimesheetBatchDesc());
			leaveReminderDTO.setTimesheetId(coherentTimesheetReviewer
					.getCoherentOvertimeApplication()
					.getOvertimeApplicationID());
			leaveReminderDTO.setEmployeeNumber(coherentTimesheetReviewer
					.getCoherentOvertimeApplication().getEmployee()
					.getEmployeeNumber());
			leaveReminderDTO
					.setOvertimeShiftType(PayAsiaConstants.COHERENT_OVERTIME_TYPE);

			String emailTo = getCoherentOvertimeTimesheetEmailId(
					coherentTimesheetReviewer.getCoherentOvertimeApplication(),
					reminDerEventConfig, leaveReminderDTO);

			if (StringUtils.isBlank(emailTo)) {
				emailTo = coherentTimesheetReviewer.getEmployeeReviewer()
						.getEmail();
			}

			Email email = new Email();

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
					.findByConditionCompany(coherentTimesheetReviewer
							.getCoherentOvertimeApplication().getCompany()
							.getCompanyId());

			if (emailPreferenceMasterVO == null) {
				continue;
			}

			email.setCompany(coherentTimesheetReviewer
					.getCoherentOvertimeApplication().getCompany());
			email.setEmailFrom(emailPreferenceMasterVO.getContactEmail());
			email.setEmailTo(emailTo);

			Long employeeId = coherentTimesheetReviewer.getEmployeeReviewer()
					.getEmployeeId();
			Date sDate = new Date(batch.getStartDate().getTime());
			cal.setTime(sDate);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date startDate = cal.getTime();
			Date eDate = new Date(batch.getEndDate().getTime());
			cal.setTime(eDate);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date endDate = cal.getTime();
			List<Date> holidayDates = getHolidaysFor(employeeId, startDate,
					endDate);

			if (lastRunDate.compareTo(endDate) < 0) {
				saveTimesheetEmailBeforeEvent(lastRunDate, reminDerEventConfig,
						email, endDate, leaveReminderDTO, holidayDates);
			}
		}
	}

	private void saveTimesheetEmailBeforeEvent(Date currentDate,
			ReminderEventConfig reminDerEventConfig, Email email,
			Date cuttOffDate, LeaveReminderDTO leaveReminderDTO,
			List<Date> holidayDates) {

		if (!reminDerEventConfig.isAllowSendMailBeforeEventDay()) {
			return;
		}
		if (reminDerEventConfig.getSendMailBeforeMailTemplate() == null) {
			return;

		} else {

			setEmailObject(email,
					reminDerEventConfig.getSendMailBeforeMailTemplate());

		}
		if (reminDerEventConfig.getSendMailBeforeDays() == null) {

			return;
		}
		// Date sendMailBeforeDaysDate = DateUtils.addDays(cuttOffDate,
		// reminDerEventConfig.getSendMailBeforeDays() * -1);
		int ctr = 0;
		Date sendMailBeforeDate = cuttOffDate;
		List<Date> sendMailOnDaysList = new ArrayList<Date>();
		while (ctr < reminDerEventConfig.getSendMailBeforeDays()) {
			sendMailBeforeDate = DateUtils.addDays(sendMailBeforeDate, -1);
			Calendar cal = Calendar.getInstance();
			cal.setTime(sendMailBeforeDate);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			// Check For Non Working (Public Holidays) & (Sat,Sun), If Not Exist
			if (!holidayDates.contains(sendMailBeforeDate)
					&& (dayOfWeek != 1 && dayOfWeek != 7)) {
				ctr++;
				sendMailOnDaysList.add(sendMailBeforeDate);
			}
		}
		Date dateCtr;
		if (reminDerEventConfig.getSendMailBeforeRepeatDays() > 0) {

			for (dateCtr = sendMailBeforeDate; dateCtr.before(cuttOffDate)
					|| dateCtr.equals(cuttOffDate); dateCtr = DateUtils
					.addDays(dateCtr,
							reminDerEventConfig.getSendMailBeforeRepeatDays())) {

				if (DateUtils.isSameDay(dateCtr, currentDate)
						&& sendMailOnDaysList.contains(dateCtr)) {
					saveTimesheetEmailObject(email, leaveReminderDTO);
					email.setCreatedDate(com.payasia.common.util.DateUtils
							.getCurrentTimestamp());
					emailDAO.saveReturn(email);
				}
			}
		} else if (reminDerEventConfig.getSendMailBeforeRepeatDays() == 0) {
			dateCtr = sendMailBeforeDate;
			if (dateCtr.before(cuttOffDate) || dateCtr.equals(cuttOffDate)) {
				if (DateUtils.isSameDay(dateCtr, currentDate)
						&& sendMailOnDaysList.contains(dateCtr)) {
					saveTimesheetEmailObject(email, leaveReminderDTO);
					email.setCreatedDate(com.payasia.common.util.DateUtils
							.getCurrentTimestamp());
					emailDAO.saveReturn(email);
				}
			}
		}
	}

	private void saveTimesheetEmailObject(Email email,
			LeaveReminderDTO leaveReminderDTO) {

		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NAME,
				leaveReminderDTO.getEmployeeName());
		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NUMBER,
				leaveReminderDTO.getEmployeeNumber());
		modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_BATCH,
				leaveReminderDTO.getTimesheetBatch());
		modelMap.put(PayAsiaConstants.COHERENT_OVERTIME_SHIFT_TYPE_PLACEHOLDER,
				leaveReminderDTO.getOvertimeShiftType());

		if (leaveReminderDTO.getURL() != null) {
			String link = "<a href='" + leaveReminderDTO.getURL() + "'>"
					+ leaveReminderDTO.getURL() + "</a>";
			modelMap.put(PayAsiaConstants.URL, link);
		} else {
			modelMap.put(PayAsiaConstants.URL, "");
		}

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;
		try {
			byte[] mailBodyBytes = email.getBody().getBytes();
			byte[] mailSubjectBytes = email.getSubject().getBytes();

			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);

			emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendMailTimesheetTemplate"
					+ leaveReminderDTO.getTimesheetId() + "_" + uniqueId
					+ ".vm");
			emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
					+ "sendMailTimesheetTemplateSubject"
					+ leaveReminderDTO.getTimesheetId() + "_" + uniqueId
					+ ".vm");
			emailSubjectTemplate.deleteOnExit();
			emailTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
				fosSubject = new FileOutputStream(emailSubjectTemplate);
				fosSubject.write(mailSubjectBytes);
				fosSubject.flush();
				fosSubject.close();

			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			String templateBodyString = emailTemplate.getPath().substring(
					emailTemplate.getParent().length() + 1);
			String templateSubjectString = emailSubjectTemplate.getPath()
					.substring(emailSubjectTemplate.getParent().length() + 1);

			StringBuilder subjectText = new StringBuilder("");

			subjectText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateSubjectString, modelMap));

			StringBuilder bodyText = new StringBuilder("");

			bodyText.append(VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, templateBodyString, modelMap));

			email.setBody(bodyText.toString());
			email.setSubject(subjectText.toString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
				emailSubjectTemplate.delete();
			}
		}
	}

	private String getCoherentOvertimeTimesheetEmailId(
			CoherentOvertimeApplication coherentOvertimeApplicationVO,
			ReminderEventConfig reminDerEventConfig,
			LeaveReminderDTO leaveReminderDTO) {

		String emailId = "";

		String recepientType = reminDerEventConfig.getRecipientType()
				.getCodeDesc();

		switch (recepientType) {
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE_USER:

			Employee employeeUser = employeeDAO.findByNumber(
					reminDerEventConfig.getRecipientValue(),
					coherentOvertimeApplicationVO.getCompany().getCompanyId());
			if (employeeUser != null) {

				emailId = employeeUser.getEmail();
				if (employeeUser.getCompany().getEmailPreferenceMasters() != null) {
					leaveReminderDTO.setURL(employeeUser.getCompany()
							.getEmailPreferenceMasters().iterator().next()
							.getCompanyUrl());
				}

			}

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EXTERNAL_ENTITY:
			emailId = reminDerEventConfig.getRecipientValue();

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_CURRENT_OWNER:

			Set<CoherentOvertimeApplicationReviewer> timesheetReviewers = coherentOvertimeApplicationVO
					.getCoherentOvertimeApplicationReviewers();

			for (CoherentOvertimeApplicationReviewer timesheetReviewer : timesheetReviewers) {
				if (timesheetReviewer.isPending()) {
					emailId = timesheetReviewer.getEmployeeReviewer()
							.getEmail();
					if (timesheetReviewer.getEmployeeReviewer().getCompany()
							.getEmailPreferenceMasters() != null) {
						leaveReminderDTO.setURL(timesheetReviewer
								.getEmployeeReviewer().getCompany()
								.getEmailPreferenceMasters().iterator().next()
								.getCompanyUrl());
					}
				}
			}

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE:
			emailId = coherentOvertimeApplicationVO.getEmployee().getEmail();
			break;
		}
		return emailId;
	}

	private String getCoherentShiftTimesheetEmailId(
			CoherentShiftApplication coherentShiftApplicationVO,
			ReminderEventConfig reminDerEventConfig,
			LeaveReminderDTO leaveReminderDTO) {

		String emailId = "";

		String recepientType = reminDerEventConfig.getRecipientType()
				.getCodeDesc();

		switch (recepientType) {
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE_USER:

			Employee employeeUser = employeeDAO.findByNumber(
					reminDerEventConfig.getRecipientValue(),
					coherentShiftApplicationVO.getCompany().getCompanyId());
			if (employeeUser != null) {

				emailId = employeeUser.getEmail();
				if (employeeUser.getCompany().getEmailPreferenceMasters() != null) {
					leaveReminderDTO.setURL(employeeUser.getCompany()
							.getEmailPreferenceMasters().iterator().next()
							.getCompanyUrl());
				}

			}

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EXTERNAL_ENTITY:
			emailId = reminDerEventConfig.getRecipientValue();

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_CURRENT_OWNER:

			Set<CoherentShiftApplicationReviewer> timesheetReviewers = coherentShiftApplicationVO
					.getCoherentShiftApplicationReviewer();

			for (CoherentShiftApplicationReviewer timesheetReviewer : timesheetReviewers) {
				if (timesheetReviewer.isPending()) {
					emailId = timesheetReviewer.getEmployeeReviewer()
							.getEmail();
					if (timesheetReviewer.getEmployeeReviewer().getCompany()
							.getEmailPreferenceMasters() != null) {
						leaveReminderDTO.setURL(timesheetReviewer
								.getEmployeeReviewer().getCompany()
								.getEmailPreferenceMasters().iterator().next()
								.getCompanyUrl());
					}
				}
			}

			break;
		case PayAsiaConstants.TIMESHEET_Event_Reminder_RECEPIENT_TYPE_EMPLOYEE:
			emailId = coherentShiftApplicationVO.getEmployee().getEmail();
			break;
		}
		return emailId;
	}
	
	@Override
	  public void executePaySlipScheduler(Company company,Date currentDate,SchedulerMaster scheduleMasterVO) {

			List<CompanyPayslipRelease> companypaySlipReleases = companyPayslipReleaseDAO
					.findByCondition(company.getCompanyId(), false, currentDate);

			boolean isFuturePaySlipReleased = false;
			if (companypaySlipReleases != null) {

				for (CompanyPayslipRelease companypaySlipRelease : companypaySlipReleases) {
					Long month =companypaySlipRelease.getMonthMaster().getMonthId();
					int year = companypaySlipRelease.getYear();
					String announcementNDesc = PayAsiaConstants.PAYASIA_PAYSLIP_ANNOUNCEMENT_START_TEXT + month + " " + year
							+ PayAsiaConstants.PAYASIA_PAYSLIP_ANNOUNCEMENT_END_TEXT;
					isFuturePaySlipReleased = companyPayslipReleaseDAO.isFuturePaySlipReleased(month, year,
							company.getCompanyId(), companypaySlipRelease.getPart());

					if (!isFuturePaySlipReleased) {

						announcementDAO.updatePayslipEndate(company.getCompanyId());

						Announcement announcement = new Announcement();

						announcement.setCompany(company);
						announcement.setCompanyGroup(company.getCompanyGroup());
						announcement.setTitle(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
						announcement.setDescription(announcementNDesc);
						announcement.setScope("C");
						announcement.setPostDateTime(com.payasia.common.util.DateUtils.getCurrentTimestamp());
						announcementDAO.save(announcement);
					} else {

						announcementDAO.deleteByCondition(announcementNDesc, PayAsiaConstants.PAY_SLIP_ENTITY_NAME,
								company.getCompanyId());

					}
					companypaySlipRelease.setReleased(true);
					companypaySlipRelease.setSendEmail(true);
					UserContext.setUserId("System");
					
					
					DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
							.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
					companyDocumentDAO.updatePayslipReleaseStatus(company.getCompanyId(), month, year,
							companypaySlipRelease.getPart(), categoryMaster.getDocumentCategoryId(),
							companypaySlipRelease.isReleased());
					companyPayslipReleaseDAO.update(companypaySlipRelease);
					saveAndSendPayslipReleaseEmail(company.getCompanyId(), companypaySlipRelease);
					
				}
			}
		}
		
		
		private void saveAndSendPayslipReleaseEmail(Long companyId,
				CompanyPayslipRelease paySlipReleaseForm) {
			List<PayslipDTO> payslipDTOList = new ArrayList<PayslipDTO>();
			HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
			Long month=Long.valueOf(paySlipReleaseForm.getMonthMaster().getMonthId());
			int year =paySlipReleaseForm.getYear();
			if (hrisPreferenceVO != null
					&& hrisPreferenceVO.isSendPayslipReleaseMail()) {
				DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
						.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
				List<Payslip> payslipVOList = null;
				List<CompanyDocument> companyDocumentListVO = companyDocumentDAO
						.findByConditionSourceTextAndDesc(
								companyId,
								categoryMaster.getDocumentCategoryId(),
								year,
								paySlipReleaseForm.getPart(),
								month,
								PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT);
				if (companyDocumentListVO.isEmpty()) {
					companyDocumentListVO = companyDocumentDAO
							.findByConditionSourceTextAndDesc(
									companyId,
									categoryMaster.getDocumentCategoryId(),
									year,
									paySlipReleaseForm.getPart(),
									month,
									PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF);
					if (companyDocumentListVO.isEmpty()) {
						PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
						payslipConditionDTO.setCompanyId(companyId);
						payslipConditionDTO.setMonthMasterId(month);
						payslipConditionDTO.setYear(year);
						payslipConditionDTO.setPart(paySlipReleaseForm.getPart());
						payslipVOList = payslipDAO.getReleasedPayslipDetails(
								payslipConditionDTO, companyId);
					}

				}
				if (!companyDocumentListVO.isEmpty()) {
					MonthMaster monthMasterVO = monthMasterDAO
							.findById(month);
					Map<String, Employee> employeeMap = new HashMap<String, Employee>();
					EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
					List<BigInteger> employeeShortListEmployeeIds = new ArrayList<>();
					conditionDTO
							.setShortListEmployeeIds(employeeShortListEmployeeIds);
					conditionDTO.setEmployeeShortList(false);

					List<Employee> employeeVOList = employeeDAO
							.getEmployeeListByAdvanceFilter(conditionDTO, null,
									null, companyId);
					for (Employee employeeVO : employeeVOList) {
						employeeMap.put(employeeVO.getEmployeeNumber(), employeeVO);
					}
					for (CompanyDocument companyDocument : companyDocumentListVO) {
						PayslipDTO payslipDTO = new PayslipDTO();
						payslipDTO.setYear(companyDocument.getYear());
						payslipDTO.setMonthName(monthMasterVO.getMonthAbbr());
						payslipDTO.setPart(companyDocument.getPart());
						payslipDTO.setEmployeeNumber(companyDocument
								.getDescription().substring(
										0,
										companyDocument.getDescription().indexOf(
												'_')));
						Employee employeeVO = employeeMap.get(companyDocument
								.getDescription().substring(
										0,
										companyDocument.getDescription().indexOf(
												'_')));
						if (employeeVO != null) {
							payslipDTO.setEmployeeId(employeeVO.getEmployeeId());
							payslipDTO.setEmployeeNumber(employeeVO
									.getEmployeeNumber());
							payslipDTO.setEmail(employeeVO.getEmail());
							payslipDTO.setFirstName(employeeVO.getFirstName());
							payslipDTO.setLastName(employeeVO.getLastName());
							payslipDTO.setMiddleName(employeeVO.getMiddleName());
							payslipDTO.setLoginName(employeeVO
									.getEmployeeLoginDetail().getLoginName());
							payslipDTO.setCompanyName(employeeVO.getCompany()
									.getCompanyName());
							payslipDTOList.add(payslipDTO);
						}
					}
				}
				if (payslipVOList != null) {
					for (Payslip payslipVO : payslipVOList) {
						PayslipDTO payslipDTO = new PayslipDTO();
						payslipDTO.setYear(payslipVO.getYear());
						payslipDTO.setMonthName(payslipVO.getMonthMaster()
								.getMonthAbbr());
						payslipDTO.setPart(payslipVO.getPart());
						payslipDTO.setEmployeeId(payslipVO.getEmployee()
								.getEmployeeId());
						payslipDTO.setEmployeeNumber(payslipVO.getEmployee()
								.getEmployeeNumber());
						payslipDTO.setEmail(payslipVO.getEmployee().getEmail());
						payslipDTO.setFirstName(payslipVO.getEmployee()
								.getFirstName());
						payslipDTO.setLastName(payslipVO.getEmployee()
								.getLastName());
						payslipDTO.setMiddleName(payslipVO.getEmployee()
								.getMiddleName());
						payslipDTO.setLoginName(payslipVO.getEmployee()
								.getEmployeeLoginDetail().getLoginName());
						payslipDTO.setCompanyName(payslipVO.getEmployee()
								.getCompany().getCompanyName());
						payslipDTOList.add(payslipDTO);
					}
				}
				dataImportLogic.savePayslipReleaseEmail(companyId, payslipDTOList);
			}
			paySlipReleaseLogic.sendPayslipReleaseEmailTO(companyId, paySlipReleaseForm);
		  }

}