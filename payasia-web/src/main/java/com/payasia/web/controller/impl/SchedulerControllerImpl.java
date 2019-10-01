package com.payasia.web.controller.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.SchedulerLogic;
import com.payasia.web.controller.SchedulerController;

public class SchedulerControllerImpl implements SchedulerController {

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.scheduled.time']}")
	private static String PAYASIA_SCHEDULED_TIME;

	/** The job transfer scheduler enabled. */
	private boolean leaveSchedulerSaveReminderNotificationEnabled;
	private boolean leaveSchedulerSendReminderNotificationEnabled;
	private boolean leaveSchedulerGrantEmployeeLeaveEnabled;
	private boolean leaveSchedulerForfeitLeaveTimeEnabled;
	private boolean leaveSchedulerLeaveTypeShortlistTimeEnabled;
	private boolean claimSchedulerClaimItemShortlistTimeEnabled;
	private boolean hrisSchedulerEmploymentStatusUpdateTimeEnabled;
	private boolean yearEndProcessTimeEnabled;
	private boolean yearEndLeaveActivateSchedulerTimeEnabled;
	private boolean mobileSchedulerIOSPushServiceTimeEnabled;
	private boolean hrisSchedulerForgotPasswordTokenEnabled;
	private boolean lundinTimesheetReminderNotificationEnabled;
	private boolean keypayLeaveIntegrationProcessEnabled;
	private boolean autoApprovalLeaveSchedularEnabled;
	private boolean lionTimesheetReminderNotificationEnabled;
	private boolean coherentTimesheetReminderNotificationEnabled;
	private boolean leaveTypeActivationSchedulerEnabled;
	private boolean paySlipReleaseSchedulerEnabled;

	/**
	 * Instantiates a new job transfer scheduler impl.
	 * 
	 * @param jobTransferSchedulerEnabled
	 *            the job transfer scheduler enabled
	 */
	public SchedulerControllerImpl(
			boolean leaveSchedulerSaveReminderNotificationEnabled,
			boolean leaveSchedulerSendReminderNotificationEnabled,
			boolean leaveSchedulerGrantEmployeeLeaveEnabled,
			boolean leaveSchedulerForfeitLeaveTimeEnabled,
			boolean leaveSchedulerLeaveTypeShortlistTimeEnabled,
			boolean claimSchedulerClaimItemShortlistTimeEnabled,
			boolean hrisSchedulerEmploymentStatusUpdateTimeEnabled,
			boolean yearEndProcessTimeEnabled,
			boolean yearEndLeaveActivateSchedulerTimeEnabled,
			boolean mobileSchedulerIOSPushServiceTimeEnabled,
			boolean hrisSchedulerForgotPasswordTokenEnabled,
			boolean lundinTimesheetReminderNotificationEnabled,
			boolean keypayLeaveIntegrationProcessEnabled,
			boolean autoApprovalLeaveSchedularEnabled,
			boolean lionTimesheetReminderNotificationEnabled,
			boolean coherentTimesheetReminderNotificationEnabled,
			boolean leaveTypeActivationSchedulerEnabled, boolean paySlipReleaseSchedulerEnabled) {
		this.leaveSchedulerSaveReminderNotificationEnabled = leaveSchedulerSaveReminderNotificationEnabled;
		this.leaveSchedulerSendReminderNotificationEnabled = leaveSchedulerSendReminderNotificationEnabled;
		this.leaveSchedulerGrantEmployeeLeaveEnabled = leaveSchedulerGrantEmployeeLeaveEnabled;
		this.leaveSchedulerForfeitLeaveTimeEnabled = leaveSchedulerForfeitLeaveTimeEnabled;
		this.leaveSchedulerLeaveTypeShortlistTimeEnabled = leaveSchedulerLeaveTypeShortlistTimeEnabled;
		this.claimSchedulerClaimItemShortlistTimeEnabled = claimSchedulerClaimItemShortlistTimeEnabled;
		this.hrisSchedulerEmploymentStatusUpdateTimeEnabled = hrisSchedulerEmploymentStatusUpdateTimeEnabled;
		this.yearEndProcessTimeEnabled = yearEndProcessTimeEnabled;
		this.yearEndLeaveActivateSchedulerTimeEnabled = yearEndLeaveActivateSchedulerTimeEnabled;
		this.mobileSchedulerIOSPushServiceTimeEnabled = mobileSchedulerIOSPushServiceTimeEnabled;
		this.hrisSchedulerForgotPasswordTokenEnabled = hrisSchedulerForgotPasswordTokenEnabled;
		this.lundinTimesheetReminderNotificationEnabled = lundinTimesheetReminderNotificationEnabled;
		this.keypayLeaveIntegrationProcessEnabled = keypayLeaveIntegrationProcessEnabled;
		this.autoApprovalLeaveSchedularEnabled = autoApprovalLeaveSchedularEnabled;
		this.lionTimesheetReminderNotificationEnabled = lionTimesheetReminderNotificationEnabled;
		this.coherentTimesheetReminderNotificationEnabled = coherentTimesheetReminderNotificationEnabled;
		this.leaveTypeActivationSchedulerEnabled = leaveTypeActivationSchedulerEnabled;
		this.paySlipReleaseSchedulerEnabled=paySlipReleaseSchedulerEnabled;
	}

	@Resource
	SchedulerLogic schedulerLogic;

	@Override
	public void checkForgotPasswordTokenStatus() {

		if (hrisSchedulerForgotPasswordTokenEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic.forgotPasswordTokenSchedular();
				}
			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void saveLeaveReminderEmail() {

		if (leaveSchedulerSaveReminderNotificationEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.SCHEDULE_MASTER_REMINDER_EVENT_MAIL_SCHEDULAR);
				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void sendLeaveReminderEmail() {

		if (leaveSchedulerSendReminderNotificationEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic.sendLeaveReminderEmails();

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void callLeaveGrantProc() {

		if (leaveSchedulerGrantEmployeeLeaveEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.SCHEDULE_MASTER_LEAVE_GRANT_SCHEDULAR);

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void callForfeitProc() {

		if (leaveSchedulerForfeitLeaveTimeEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {

					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.SCHEDULE_MASTER_FORFEIT_LEAVE_SCHEDULAR);
				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void scheduleLeaveSchemeTypeShortList() {

		if (leaveSchedulerLeaveTypeShortlistTimeEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.LEAVE_SCHEME_TYPE_SHORTLIST_SCHEDULAR);

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void scheduleClaimTemplateItemShortList() {

		if (claimSchedulerClaimItemShortlistTimeEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.CLAIM_TEMPLATE_ITEM_SHORTLIST_SCHEDULAR);

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void scheduleEmploymentStatusUpdate() {

		if (hrisSchedulerEmploymentStatusUpdateTimeEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.EMPLOYMENT_STATUS_UPDATE_SCHEDULAR);

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void yearEndProcess() {

		if (yearEndProcessTimeEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.YEAR_END_PROCESS_SCHEDULER);

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void yearEndLeaveActivateScheduler() {

		if (yearEndLeaveActivateSchedulerTimeEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.YEAR_END_LEAVE_ACTIVATE_SCHEDULER);

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void pushIOSDeviceNotifications() {

		if (mobileSchedulerIOSPushServiceTimeEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic.pushIOSDeviceNotifications();

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void lundinTimesheetReminderEmail() {

		if (lundinTimesheetReminderNotificationEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.LUNDIN_TIMESHEET_REMINDER_EVENT_MAIL_SCHEDULAR);

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void coherentTimesheetReminderEmail() {
		if (coherentTimesheetReminderNotificationEnabled) {
			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.COHERENT_TIMESHEET_REMINDER_EVENT_MAIL_SCHEDULAR);
				}
			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();
		}
	}

	@Override
	public void lionTimesheetReminderEmail() {
		if (lionTimesheetReminderNotificationEnabled) {
			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.LION_TIMESHEET_REMINDER_EVENT_MAIL_SCHEDULAR);
				}
			};
			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();
		}
	}

	@Override
	public void keyPayLeaveIntegration() {

		if (keypayLeaveIntegrationProcessEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.KEYPAY_INTEGRATION_PROCESS_SCHEDULER);

				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	@Override
	public void autoApprovalLeaveSchedular() {
		if (autoApprovalLeaveSchedularEnabled) {
			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.LEAVE_AUTO_APPROVAL_SCHEDULER);
				}
			};
			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();
		}
	}

	@Override
	public void leaveTypeActivationScheduler() {
		if (leaveTypeActivationSchedulerEnabled) {
			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic
							.callSchedulerByName(PayAsiaConstants.LEAVE_TYPE_ACTIVATION_SCHEDULER);
				}
			};
			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();
		}
	}
	
	@Override
	public void paySlipReleaseScheduler() {
		
		if (paySlipReleaseSchedulerEnabled) {
			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					schedulerLogic.paySlipRelease(PayAsiaConstants.PAYSLIP_RELEASE_SCHEDULER);
				}
			};
			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();
		}
	}

	public boolean isLeaveSchedulerSaveReminderNotificationEnabled() {
		return leaveSchedulerSaveReminderNotificationEnabled;
	}

	public void setLeaveSchedulerSaveReminderNotificationEnabled(
			boolean leaveSchedulerSaveReminderNotificationEnabled) {
		this.leaveSchedulerSaveReminderNotificationEnabled = leaveSchedulerSaveReminderNotificationEnabled;
	}

	public boolean isLeaveSchedulerSendReminderNotificationEnabled() {
		return leaveSchedulerSendReminderNotificationEnabled;
	}

	public void setLeaveSchedulerSendReminderNotificationEnabled(
			boolean leaveSchedulerSendReminderNotificationEnabled) {
		this.leaveSchedulerSendReminderNotificationEnabled = leaveSchedulerSendReminderNotificationEnabled;
	}

	public boolean isLeaveSchedulerGrantEmployeeLeaveEnabled() {
		return leaveSchedulerGrantEmployeeLeaveEnabled;
	}

	public void setLeaveSchedulerGrantEmployeeLeaveEnabled(
			boolean leaveSchedulerGrantEmployeeLeaveEnabled) {
		this.leaveSchedulerGrantEmployeeLeaveEnabled = leaveSchedulerGrantEmployeeLeaveEnabled;
	}

	public boolean isLeaveSchedulerForfeitLeaveTimeEnabled() {
		return leaveSchedulerForfeitLeaveTimeEnabled;
	}

	public void setLeaveSchedulerForfeitLeaveTimeEnabled(
			boolean leaveSchedulerForfeitLeaveTimeEnabled) {
		this.leaveSchedulerForfeitLeaveTimeEnabled = leaveSchedulerForfeitLeaveTimeEnabled;
	}

	public boolean isLeaveSchedulerLeaveTypeShortlistTimeEnabled() {
		return leaveSchedulerLeaveTypeShortlistTimeEnabled;
	}

	public void setLeaveSchedulerLeaveTypeShortlistTimeEnabled(
			boolean leaveSchedulerLeaveTypeShortlistTimeEnabled) {
		this.leaveSchedulerLeaveTypeShortlistTimeEnabled = leaveSchedulerLeaveTypeShortlistTimeEnabled;
	}

	public boolean isClaimSchedulerClaimItemShortlistTimeEnabled() {
		return claimSchedulerClaimItemShortlistTimeEnabled;
	}

	public void setClaimSchedulerClaimItemShortlistTimeEnabled(
			boolean claimSchedulerClaimItemShortlistTimeEnabled) {
		this.claimSchedulerClaimItemShortlistTimeEnabled = claimSchedulerClaimItemShortlistTimeEnabled;
	}

	public boolean isHrisSchedulerEmploymentStatusUpdateTimeEnabled() {
		return hrisSchedulerEmploymentStatusUpdateTimeEnabled;
	}

	public void setHrisSchedulerEmploymentStatusUpdateTimeEnabled(
			boolean hrisSchedulerEmploymentStatusUpdateTimeEnabled) {
		this.hrisSchedulerEmploymentStatusUpdateTimeEnabled = hrisSchedulerEmploymentStatusUpdateTimeEnabled;
	}

	public boolean isYearEndProcessTimeEnabled() {
		return yearEndProcessTimeEnabled;
	}

	public void setYearEndProcessTimeEnabled(boolean yearEndProcessTimeEnabled) {
		this.yearEndProcessTimeEnabled = yearEndProcessTimeEnabled;
	}

	public boolean isYearEndLeaveActivateSchedulerTimeEnabled() {
		return yearEndLeaveActivateSchedulerTimeEnabled;
	}

	public void setYearEndLeaveActivateSchedulerTimeEnabled(
			boolean yearEndLeaveActivateSchedulerTimeEnabled) {
		this.yearEndLeaveActivateSchedulerTimeEnabled = yearEndLeaveActivateSchedulerTimeEnabled;
	}

	public boolean isMobileSchedulerIOSPushServiceTimeEnabled() {
		return mobileSchedulerIOSPushServiceTimeEnabled;
	}

	public void setMobileSchedulerIOSPushServiceTimeEnabled(
			boolean mobileSchedulerIOSPushServiceTimeEnabled) {
		this.mobileSchedulerIOSPushServiceTimeEnabled = mobileSchedulerIOSPushServiceTimeEnabled;
	}

	public boolean isHrisSchedulerForgotPasswordTokenEnabled() {
		return hrisSchedulerForgotPasswordTokenEnabled;
	}

	public void setHrisSchedulerForgotPasswordTokenEnabled(
			boolean hrisSchedulerForgotPasswordTokenEnabled) {
		this.hrisSchedulerForgotPasswordTokenEnabled = hrisSchedulerForgotPasswordTokenEnabled;
	}

	public boolean isLundinTimesheetReminderNotificationEnabled() {
		return lundinTimesheetReminderNotificationEnabled;
	}

	public void setLundinTimesheetReminderNotificationEnabled(
			boolean lundinTimesheetReminderNotificationEnabled) {
		this.lundinTimesheetReminderNotificationEnabled = lundinTimesheetReminderNotificationEnabled;
	}

	public boolean isKeypayLeaveIntegrationProcessEnabled() {
		return keypayLeaveIntegrationProcessEnabled;
	}

	public void setKeypayLeaveIntegrationProcessEnabled(
			boolean keypayLeaveIntegrationProcessEnabled) {
		this.keypayLeaveIntegrationProcessEnabled = keypayLeaveIntegrationProcessEnabled;
	}

	public boolean isAutoApprovalLeaveSchedularEnabled() {
		return autoApprovalLeaveSchedularEnabled;
	}

	public void setAutoApprovalLeaveSchedularEnabled(
			boolean autoApprovalLeaveSchedularEnabled) {
		this.autoApprovalLeaveSchedularEnabled = autoApprovalLeaveSchedularEnabled;
	}
	

	public boolean isPaySlipReleaseSchedulerEnabled() {
		return paySlipReleaseSchedulerEnabled;
	}

	public void setPaySlipReleaseSchedulerEnabled(boolean paySlipReleaseSchedulerEnabled) {
		this.paySlipReleaseSchedulerEnabled = paySlipReleaseSchedulerEnabled;
	}



}
