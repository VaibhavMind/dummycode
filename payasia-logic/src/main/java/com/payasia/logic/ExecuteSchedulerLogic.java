/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import java.util.Date;

import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.SchedulerMaster;
import com.payasia.dao.bean.SchedulerStatus;

public interface ExecuteSchedulerLogic {

	/**
	 * Purpose : Execute Scheduler for company
	 * 
	 * @param Date
	 *            the currentDate
	 * @param Company
	 *            the company
	 * @param String
	 *            the scheduler
	 * @param SchedulerStatus
	 *            the schedulerStatusVO
	 * @param SchedulerMaster
	 *            the scheduleMasterVO
	 */
	void executeScheduler(Date currentDate, Company company, String scheduler,
			SchedulerStatus schedulerStatusVO, SchedulerMaster scheduleMasterVO);

	void yearEndProcessSchedular(Company company, Date currentDate);

	void executePaySlipScheduler(Company company, Date currentDate, SchedulerMaster scheduleMasterVO);

}
