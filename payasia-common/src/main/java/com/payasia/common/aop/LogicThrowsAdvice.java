/*
 * <h4>Description</h4>
 * This class handles exceptions from DAO layer and convert them to LATSystemException
 * 
 * @author tarungupta
 */
package com.payasia.common.aop;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.dao.DataAccessException;

import com.payasia.common.exception.PayAsiaBusinessException;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaPageNotFoundException;
import com.payasia.common.exception.PayAsiaPasswordPolicyException;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;

/**
 * The Class LogicThrowsAdvice.
 */
public class LogicThrowsAdvice implements ThrowsAdvice {

	private static final Logger LOGGER = Logger
			.getLogger(LogicThrowsAdvice.class);

	/**
	 * After throwing.
	 * 
	 * @param ex
	 *            the ex
	 */
	public void afterThrowing(PayAsiaSystemException ex) {
		throw ex;
	}

	/**
	 * After throwing.
	 * 
	 * @param ex
	 *            the ex
	 */
	public void afterThrowing(PayAsiaBusinessException ex) {
		throw ex;
	}

	public void afterThrowing(PayAsiaDataException ex) {
		throw ex;
	}

	public void afterThrowing(PayAsiaPageNotFoundException ex) {
		throw ex;
	}

	public void afterThrowing(PayAsiaRollBackDataException ex) {
		throw ex;
	}

	public void afterThrowing(PayAsiaPasswordPolicyException ex) {
		throw ex;
	}

	/**
	 * After throwing.
	 * 
	 * @param ex
	 *            the ex
	 */
	public void afterThrowing(DataAccessException ex) {
		LOGGER.error(ex.getMessage(), ex);
		String[] errorArgs = { ex.getClass().getSimpleName(), ex.getMessage() };
		PayAsiaSystemException sdfpEx = new PayAsiaSystemException(
				"errors.system.dataAccess", errorArgs, ex, ex.getMessage());
		throw sdfpEx;
	}

	/**
	 * After throwing.
	 * 
	 * @param method
	 *            the method
	 * @param args
	 *            the args
	 * @param target
	 *            the target
	 * @param ex
	 *            the ex
	 */
	public void afterThrowing(Method method, Object[] args, Object target,
			RuntimeException ex) {
		LOGGER.error(ex.getMessage(), ex);
		String[] errorArgs = { ex.getClass().getSimpleName(), ex.getMessage() };
		PayAsiaSystemException sdfpEx = new PayAsiaSystemException(
				"errors.system.dao", errorArgs, ex, ex.getMessage());
		throw sdfpEx;
	}

}
