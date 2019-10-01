/*
 * <h4>Description</h4>
 * Records method entry and exit along with execution time.
 *
 * @author tarungupta
 */
package com.payasia.common.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;

/**
 * Purpose: The Class PerformanceInterceptor is used for calculating the
 * execution time of a particular method.
 * 
 * @author tarungupta
 */

public class PerformanceInterceptor extends PerformanceMonitorInterceptor {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static final Logger LOGGER = Logger
			.getLogger(PerformanceInterceptor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.springframework.aop.interceptor.PerformanceMonitorInterceptor#
	 * invokeUnderTrace(org.aopalliance.intercept.MethodInvocation,
	 * org.apache.commons.logging.Log)
	 */

	@Override
	public Object invokeUnderTrace(MethodInvocation invocation, Log log)
			throws Throwable {
		String methodName = invocation.getThis().getClass().getSimpleName()
				+ "#" + invocation.getMethod().getName();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(methodName + " : start");
		}

		long startTime = System.currentTimeMillis();
		try {

			return invocation.proceed();

		} finally {
			long stopTime = System.currentTimeMillis();
			long respTime = (stopTime - startTime);

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace(" Method Name: [" + methodName
						+ "] Execution Time [" + respTime + " ms]");
			}
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace(methodName + " : end");
			}
		}

	}
}
