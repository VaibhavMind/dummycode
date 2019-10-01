package com.payasia.web.controller;

public interface SchedulerController {

	void saveLeaveReminderEmail();

	void sendLeaveReminderEmail();

	void callLeaveGrantProc();

	void callForfeitProc();

	void scheduleLeaveSchemeTypeShortList();

	void scheduleClaimTemplateItemShortList();

	void scheduleEmploymentStatusUpdate();

	void yearEndProcess();

	void yearEndLeaveActivateScheduler();

	void pushIOSDeviceNotifications();

	void checkForgotPasswordTokenStatus();

	void lundinTimesheetReminderEmail();

	void keyPayLeaveIntegration();

	void autoApprovalLeaveSchedular();

	void lionTimesheetReminderEmail();

	void coherentTimesheetReminderEmail();

	void leaveTypeActivationScheduler();

	void paySlipReleaseScheduler();

}
