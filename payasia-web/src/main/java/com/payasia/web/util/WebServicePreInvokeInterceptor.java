package com.payasia.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.payasia.logic.impl.AddLeaveLogicImpl;

public class WebServicePreInvokeInterceptor extends
		AbstractPhaseInterceptor<Message> {

	private static final Logger LOGGER = Logger
			.getLogger(AddLeaveLogicImpl.class);
	
	@Autowired
	private ServletContext servletContext;

	public WebServicePreInvokeInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	private static Integer sessionExpiraryTime = 0;

	@Override
	public void handleMessage(Message message) {/*
		HttpServletRequest request = (HttpServletRequest) message
				.get(AbstractHTTPDestination.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) message
				.get(AbstractHTTPDestination.HTTP_RESPONSE);
		String reqParams = "";

		CORS headers 
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE,HEAD");
		response.setHeader("Access-Control-Max-Age", "1209600");
		
		
		if (message.get(message.HTTP_REQUEST_METHOD).equals("POST")) {

			try {

				InputStream is = message.getContent(InputStream.class);
				JSONObject jsonObj = new JSONObject(is.toString());
				String loginToken = (String) jsonObj.get("loginToken");
				String uuid = (String) jsonObj.get("uuid");

				if (StringUtils.isNotBlank(loginToken)) {
					HashMap<String, EmployeeDTO> employeeInfo = (HashMap<String, EmployeeDTO>) request
							.getServletContext().getAttribute(
									PayAsiaMobileConstants.MOBILE_TOKEN_KEY);
					EmployeeDTO employeeDTO = employeeInfo.get(loginToken);
					UserContext.setUserId(String.valueOf(employeeDTO.getEmployeeId()));
					UserContext.setWorkingCompanyId(String.valueOf(employeeDTO.getCompanyId()));

					Boolean isSessionVaild = checkSession(loginToken, uuid,
							request, employeeInfo.get(loginToken)
									.getLastAccessedTime());
					if (isSessionVaild
							&& uuid.equals(employeeInfo.get(loginToken)
									.getUuid())) {
						employeeInfo.get(loginToken).setLastAccessedTime(
								new Date());
					} else {
						response.sendRedirect(request.getContextPath()
								+ "/service/loginWS/" + "sessionExpired.html");
					}

				} else {

					response.sendRedirect(request.getContextPath()
							+ "/service/loginWS/" + "sessionExpired.html");
				}

			} catch (Exception exception) {
				// LOGGER.error(exception.getMessage(), exception);
			}
		}
		// System.out.println(reqParams);

	*/}

	private Boolean checkSession(String loginToken, String uuid,
			HttpServletRequest request, Date loginTokenDateTime) {
		String path = servletContext.getRealPath(
				"/WEB-INF/classes/payasia-mobile-app.properties");
		FileInputStream fileInputStream = null;
		try {
			File file = new File(path);
			Boolean fileExists = file.exists();

			if (fileExists) {
				fileInputStream = new FileInputStream(file);
				java.util.Properties properties = new java.util.Properties();
				properties.load(fileInputStream);

				sessionExpiraryTime = Integer.parseInt(properties
						.getProperty("payasia.mobile.sessionExpiryTime"));
				Calendar cal = Calendar.getInstance();
				cal.setTime(loginTokenDateTime);
				cal.add(Calendar.MINUTE, sessionExpiraryTime);
				Date lastAccessedDateTime = cal.getTime();
				Date currentDateTime = new Date();
				Integer comparedValue = lastAccessedDateTime
						.compareTo(currentDateTime);
				if (comparedValue >= 1) {
					return true;
				} else {
					return false;
				}

			}
		} catch (Exception exception) {
			// LOGGER.error(exception.getMessage(), exception);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					// LOGGER.error(e.getMessage(), e);
				}
			}
		}

		return null;
	}

	public void handleFault(Message messageParam) {
	}

	

}
