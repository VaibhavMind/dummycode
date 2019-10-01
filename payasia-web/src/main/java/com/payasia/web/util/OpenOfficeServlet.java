package com.payasia.web.util;

import java.util.Hashtable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.PayAsiaConstants;

/**
 * Servlet implementation class StartUpServlet
 */
public class OpenOfficeServlet implements ServletContextListener {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger(OpenOfficeServlet.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			ApplicationContext ac = WebApplicationContextUtils
					.getWebApplicationContext(sce.getServletContext());
			Hashtable<String, String> prop = (Hashtable<String, String>) ac
					.getBean("payasiaptProperties");
			String openOfficeAutoStart = prop.get("open.office.auto.start");
			if (openOfficeAutoStart.equalsIgnoreCase(PayAsiaConstants.TRUE)) {
				LOGGER.info("Starting Open Office Services");

				String sOoficePath = prop.get("sOoficePath");
				LOGGER.info("Open Office Path" + sOoficePath);

				Runtime.getRuntime().exec(sOoficePath);
				 
				LOGGER.info("Open Office Started");
			}
		} catch (Exception exception) {

			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
	}
}
