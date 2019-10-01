package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.TimeZoneDTO;

public interface SchedulerLogic {

	void sendLeaveReminderEmails();

	List<Long> getTimeZoneIds();

	TimeZoneDTO getTimeZone(Long timeZoneId);

	void callSchedulerByName(String Scheduler);

	void pushIOSDeviceNotifications();

	void forgotPasswordTokenSchedular();

	void paySlipRelease(String payslipReleaseScheduler);

}
