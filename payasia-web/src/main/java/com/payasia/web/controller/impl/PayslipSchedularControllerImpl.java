package com.payasia.web.controller.impl;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.payasia.common.dto.PrintTokenDTO;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.PayslipSchedularLogic;
import com.payasia.web.controller.PayslipSchedularController;

public class PayslipSchedularControllerImpl implements
		PayslipSchedularController {

	@Resource
	PayslipSchedularLogic payslipSchedularLogic;

	@Autowired
	ServletContext context;

	@Value("#{payasiaptProperties['print.token.delete.time']}")
	private Integer printTokenDeleteTime;

	/** The payslip schedular enabled. */
	private boolean payslipSchedularEnabled;

	/**
	 * Instantiates a new payslip schedular controller impl.
	 * 
	 * @param payslipSchedularEnabled
	 *            the payslip schedular enabled
	 */
	PayslipSchedularControllerImpl(boolean payslipSchedularEnabled) {
		this.payslipSchedularEnabled = payslipSchedularEnabled;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PayslipSchedularController#checkPayslipTokens
	 * ()
	 */
	@Override
	public void checkPayslipTokens() {

		if (payslipSchedularEnabled) {

			Runnable synchronizeRunnable = new Runnable() {
				@Override
				public void run() {
					HashMap<String, PrintTokenDTO> printTokenMap = (HashMap<String, PrintTokenDTO>) context
							.getAttribute(PayAsiaConstants.PAYSLIP_TOKEN);
					payslipSchedularLogic.checkPayslipTokens(printTokenMap,
							printTokenDeleteTime);
				}

			};

			Thread schedulerThread = new Thread(synchronizeRunnable);
			schedulerThread.start();

		}

	}

	/**
	 * Checks if is payslip schedular enabled.
	 * 
	 * @return true, if is payslip schedular enabled
	 */
	public boolean isPayslipSchedularEnabled() {
		return payslipSchedularEnabled;
	}

	/**
	 * Sets the payslip schedular enabled.
	 * 
	 * @param payslipSchedularEnabled
	 *            the new payslip schedular enabled
	 */
	public void setPayslipSchedularEnabled(boolean payslipSchedularEnabled) {
		this.payslipSchedularEnabled = payslipSchedularEnabled;
	}

}
