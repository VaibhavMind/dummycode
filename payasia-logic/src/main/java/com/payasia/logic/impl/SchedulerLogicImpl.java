package com.payasia.logic.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.TimeZoneDTO;
import com.payasia.common.util.PayAsiaAttachmentUtils;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmailDAO;
import com.payasia.dao.EmployeeActivationCodeDAO;
import com.payasia.dao.ForgotPasswordTokenDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.SchedulerMasterDAO;
import com.payasia.dao.SchedulerStatusDAO;
import com.payasia.dao.TimeZoneMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Email;
import com.payasia.dao.bean.EmailAttachment;
import com.payasia.dao.bean.EmployeeActivationCode;
import com.payasia.dao.bean.NotificationAlert;
import com.payasia.dao.bean.SchedulerMaster;
import com.payasia.dao.bean.SchedulerStatus;
import com.payasia.dao.bean.TimeZoneMaster;
import com.payasia.logic.ExecuteSchedulerLogic;
import com.payasia.logic.SchedulerLogic;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;

@Component
public class SchedulerLogicImpl implements SchedulerLogic {

	@Resource
	private EmailDAO emailDAO;

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
	private NotificationAlertDAO notificationAlertDAO;

	@Resource
	private ForgotPasswordTokenDAO forgotPasswordTokenDAO;

	@Resource
	private ExecuteSchedulerLogic executeSchedulerLogic;

	@Resource
	private EmployeeActivationCodeDAO employeeActivationCodeDAO;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The payasia mobile ios keystore path. */
	@Value("#{payasiaptProperties['payasia.mobile.ios.keystore.path']}")
	private String PAYASIA_MOBILE_IOS_KEYSTORE_PATH;

	/** The payasia mobile ios keystore password. */
	@Value("#{payasiaptProperties['payasia.mobile.ios.keystore.password']}")
	private String PAYASIA_MOBILE_IOS_KEYSTORE_PASSWORD;

	@Value("#{payasiaptProperties['payasia.mobile.ios.production.server']}")
	private boolean PAYASIA_MOBILE_IOS_PRODUCTION_SERVER;

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(SchedulerLogicImpl.class);
	/** The recon reentrant lock. */
	final ReentrantLock reconReentrantLock = new ReentrantLock();
	
	Date lastRunDate = null;

	private Boolean checkScheduledTime(String timeZoneGMTOffset,
			SchedulerMaster scheduleMasterVO) {

		Boolean scheduledTimeStatus = false;
		Date timeZoneTime = com.payasia.common.util.DateUtils
				.convertTimeZoneToLocal(timeZoneGMTOffset);
		Calendar cal = Calendar.getInstance();
		cal.setTime(scheduleMasterVO.getSchedulerTime());
		cal.get(Calendar.HOUR);
		Calendar curentDate = Calendar.getInstance();
		curentDate.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR));
		curentDate.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		curentDate.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		curentDate.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
		Date scheduledDateTime = curentDate.getTime();
		if (timeZoneTime != null)
			LOGGER.info("TimeZone Time: " + timeZoneTime.toString());
		if (scheduledDateTime != null)
			LOGGER.info("SchedulerScheduled DateTime: "
					+ scheduledDateTime.toString());
		if (timeZoneTime.equals(scheduledDateTime)) {

			scheduledTimeStatus = true;

		} else if (timeZoneTime.compareTo(scheduledDateTime) >= 0) {

			scheduledTimeStatus = true;

		} else if (timeZoneTime.compareTo(scheduledDateTime) < 0) {

			scheduledTimeStatus = false;

		}
		return scheduledTimeStatus;

	}

	@Override
	public void sendLeaveReminderEmails() {
		boolean lockAcquired;
		try {
			lockAcquired = reconReentrantLock.tryLock(10, TimeUnit.MINUTES);
			if (lockAcquired) {
				List<Email> emailVOs = emailDAO.findAllEmailsBySentDate();
				for (Email emailVO : emailVOs) {

					PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

					if (StringUtils.isNotBlank(emailVO.getEmailCC())) {

						String[] ccEmailIds = emailVO.getEmailCC().split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}

					}
					if (StringUtils.isNotBlank(emailVO.getEmailTo())) {
						String[] toEmailIds = emailVO.getEmailTo().split(";");
						for (String toEmailId : toEmailIds) {
							payAsiaEmailTO.addMailTo(toEmailId);
						}
					}
					if (StringUtils.isNotBlank(emailVO.getEmailBCC())) {
						String[] bccEmailIds = emailVO.getEmailBCC().split(";");
						for (String bccEmailId : bccEmailIds) {
							payAsiaEmailTO.addMailBCc(bccEmailId);
						}
					}
					payAsiaEmailTO.setMailFrom(emailVO.getEmailFrom());
					payAsiaEmailTO.setMailSubject(emailVO.getSubject());
					payAsiaEmailTO.setMailText(emailVO.getBody());

					Set<EmailAttachment> emailAttachments = emailVO
							.getEmailAttachments();
					for (EmailAttachment emailAttachment : emailAttachments) {
						PayAsiaAttachmentUtils payAsiaAttachmentUtils = new PayAsiaAttachmentUtils();
						payAsiaAttachmentUtils.setFileName(emailAttachment
								.getFileName());
						File file;
						try {
							file = File.createTempFile("temp",
									Long.toString(System.nanoTime()));
							FileOutputStream output = new FileOutputStream(file);
							IOUtils.write(emailAttachment.getAttachment(),
									output);
							payAsiaAttachmentUtils.setFile(file);
						} catch (IOException e) {
							LOGGER.error(e.getMessage(), e);
						}

						payAsiaEmailTO.addAttachment(payAsiaAttachmentUtils);

					}
					try {

						payAsiaMailUtils
								.sendReminderEmail(payAsiaEmailTO, true);
						emailVO.setSentDate(com.payasia.common.util.DateUtils
								.getCurrentTimestampWithTime());
						emailDAO.update(emailVO);

					} catch (Exception e) {
						emailVO.setSentDate(com.payasia.common.util.DateUtils
								.getCurrentTimestampWithTime());
						emailDAO.update(emailVO);
						LOGGER.error(e.getMessage(), e);

					}
				}
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			reconReentrantLock.unlock();
		}
	}

	@Override
	public List<Long> getTimeZoneIds() {

		List<Long> timeZoneIds = companyDAO.getDistinctTimeZoneIds();

		return timeZoneIds;
	}

	@Override
	public TimeZoneDTO getTimeZone(Long timeZoneId) {
		TimeZoneMaster timeZoneMaster = timeZoneMasterDAO.findById(timeZoneId);
		TimeZoneDTO timeZoneDTO = new TimeZoneDTO();
		timeZoneDTO.setTimeZoneId(timeZoneId);
		timeZoneDTO.setTimeZoneName(timeZoneMaster.getTimeZoneName());
		timeZoneDTO.setTimeZoneGMTOffset(timeZoneMaster.getGmtOffset());

		return timeZoneDTO;
	}

	@Override
	public void callSchedulerByName(String scheduler) {

		SchedulerMaster scheduleMasterVO = schedulerMasterDAO
				.findByCondition(scheduler);
		if (scheduleMasterVO == null) {
			return;
		}

		List<Long> timeZoneIds = getTimeZoneIds();
		LOGGER.info("timeZoneIds list:" + timeZoneIds);

		for (Long timeZoneId : timeZoneIds) {

			Boolean runScheduler;
			final TimeZoneDTO timeZoneDTO = getTimeZone(timeZoneId);

			runScheduler = checkScheduledTime(
					timeZoneDTO.getTimeZoneGMTOffset(), scheduleMasterVO);

			if (!runScheduler) {
				LOGGER.info("Scheduler not running for time zone: "
						+ timeZoneDTO.getTimeZoneGMTOffset());
				continue;
			}
			LOGGER.info("Running for time zone: Offset"
					+ timeZoneDTO.getTimeZoneGMTOffset() + ", Timezoneid:"
					+ timeZoneId);
			List<Company> companyVOs = companyDAO
					.findCompanyByTimeZone(timeZoneId);

			for (Company company : companyVOs) {

				if (company.getCompanyId() == 1l) {
					continue;
				}
				if (company.getCompanyCode().equalsIgnoreCase("")) {
					LOGGER.info("Running for baxterhealthcaresg");
				}
				LOGGER.info("Scheduler running for Company: "
						+ company.getCompanyCode() + ", "
						+ company.getCompanyName() + ", "
						+ company.getTimeZoneMaster().getTimeZoneName());
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date currentDate = com.payasia.common.util.DateUtils
						.stringToDate(com.payasia.common.util.DateUtils
								.timeStampToString(
										com.payasia.common.util.DateUtils
												.getCurrentTimestamp(), company
												.getDateFormat()), company
								.getDateFormat());
				LOGGER.info("Current date: " + currentDate);
				Date lastRunDate = null;
				SchedulerStatus schedulerStatusVO = schedulerStatusDAO
						.findByCondition(company.getCompanyId(),
								scheduleMasterVO.getSchedulerId(), currentDate,
								true);

				if (schedulerStatusVO != null) {
					Calendar lastRunDateCal = Calendar.getInstance();
					lastRunDateCal.setTime(schedulerStatusVO.getLastRunDate());
					lastRunDateCal.set(Calendar.HOUR_OF_DAY, 0);
					lastRunDateCal.set(Calendar.MINUTE, 0);
					lastRunDateCal.set(Calendar.SECOND, 0);
					lastRunDateCal.set(Calendar.MILLISECOND, 0);
					lastRunDate = lastRunDateCal.getTime();

					if (lastRunDate.equals(currentDate)
							&& schedulerStatusVO.isLastRunStatus()) {
						LOGGER.info("Scheduler has already run on date:"
								+ lastRunDate + ", skipping the company.");
						continue;
					}

				}
				LOGGER.info("Last Run date: " + lastRunDate);
				if (lastRunDate == null) {
					lastRunDate = currentDate;
				} else {
					lastRunDate = DateUtils.addDays(lastRunDate, 1);
				}

				if (lastRunDate != null && lastRunDate.before(currentDate)) {

					Date dateCtr = null;

					for (dateCtr = lastRunDate; dateCtr.before(currentDate)
							|| dateCtr.equals(currentDate); dateCtr = DateUtils
							.addDays(dateCtr, 1)) {

						executeSchedulerLogic.executeScheduler(dateCtr,
								company, scheduler, schedulerStatusVO,
								scheduleMasterVO);
					}

				} else {
					LOGGER.info("Executing scheduler for current date: "
							+ lastRunDate);
					executeSchedulerLogic.executeScheduler(currentDate,
							company, scheduler, schedulerStatusVO,
							scheduleMasterVO);
				}

				// }
			}

		}

	}

	@Override
	public void forgotPasswordTokenSchedular() {
		forgotPasswordTokenDAO.updateByCondition();
	}

	@Override
	public void pushIOSDeviceNotifications() {
		List<NotificationAlert> notificationAlerts = notificationAlertDAO
				.getIOSDeviceNotificationsAlerts();

		for (NotificationAlert notificationAlert : notificationAlerts) {

			List<EmployeeActivationCode> employeeActivationCodes = employeeActivationCodeDAO
					.findByEmployee(notificationAlert.getEmployee()
							.getEmployeeId());

			for (EmployeeActivationCode employeeActivationCode : employeeActivationCodes) {
				try {
					Push.alert(notificationAlert.getMessage(),
							PAYASIA_MOBILE_IOS_KEYSTORE_PATH,
							PAYASIA_MOBILE_IOS_KEYSTORE_PASSWORD,
							PAYASIA_MOBILE_IOS_PRODUCTION_SERVER,
							employeeActivationCode.getEmployeeMobileDetails()
									.iterator().next().getDeviceToken());
				} catch (CommunicationException e) {
					LOGGER.error(e.getMessage(), e);

				} catch (KeystoreException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}

			notificationAlert.setShownStatus(true);
			notificationAlertDAO.update(notificationAlert);

		}

	}
	
	@Override
	public void paySlipRelease(String payslipReleaseScheduler) {

		SchedulerMaster scheduleMasterVO = schedulerMasterDAO.findByCondition(payslipReleaseScheduler);
		if (scheduleMasterVO == null) {
			return;
		}

		List<Long> timeZoneIds = getTimeZoneIds();
		LOGGER.info("timeZoneIds list:" + timeZoneIds);

		for (Long timeZoneId : timeZoneIds) {

			Boolean runScheduler;
			final TimeZoneDTO timeZoneDTO = getTimeZone(timeZoneId);

			runScheduler = checkScheduledTime(timeZoneDTO.getTimeZoneGMTOffset(), scheduleMasterVO);

			if (!runScheduler) {
				LOGGER.info("Scheduler not running for time zone: " + timeZoneDTO.getTimeZoneGMTOffset());
				continue;
			}
			LOGGER.info("Running for time zone: Offset" + timeZoneDTO.getTimeZoneGMTOffset() + ", Timezoneid:"
					+ timeZoneId);
			List<Company> companyVOs = companyDAO.findCompanyByTimeZone(timeZoneId);

			for (Company company : companyVOs) {

				if (company.getCompanyId() == 1l) {
					continue;
				}
				if (company.getCompanyCode().equalsIgnoreCase("")) {
					LOGGER.info("Running for baxterhealthcaresg");
				}
				LOGGER.info("Scheduler running for Company: " + company.getCompanyCode() + ", "
						+ company.getCompanyName() + ", " + company.getTimeZoneMaster().getTimeZoneName());
			
				Date currentDate=new Date();
				currentDate = com.payasia.common.util.DateUtils.convertDateToTimeStampWithTime(currentDate);
				
				LOGGER.info("Current date: " + currentDate);
				
				SchedulerStatus schedulerStatusVO = schedulerStatusDAO.findByCondition(company.getCompanyId(),
						scheduleMasterVO.getSchedulerId(),currentDate, true);

				if (schedulerStatusVO != null) {
					Date lastRunDateCal=schedulerStatusVO.getLastRunDate();
					lastRunDate=com.payasia.common.util.DateUtils.convertDateToTimeStampWithTime(lastRunDateCal);
					if (lastRunDate.equals(currentDate) && schedulerStatusVO.isLastRunStatus()) {
						LOGGER.info("Scheduler has already run on date:" + lastRunDate + ", skipping the company.");
						continue;
					}

				}
				executeSchedulerLogic.executeScheduler(currentDate, company, payslipReleaseScheduler, schedulerStatusVO,
						scheduleMasterVO);
			}
		}

	}
}
