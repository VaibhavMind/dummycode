/*
 * <h4>Description</h4>
 * Used for handling success and failure messages.
 *
 * @author tarungupta
 */
package com.payasia.web.util;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.support.RequestContext;

import com.payasia.common.exception.ErrorType;
import com.payasia.common.exception.PayAsiaBusinessException;
import com.payasia.common.exception.PayAsiaException;

/**
 * The Class MessageUtils. Provides method for handling exceptions and
 * displaying error messages at controller level.
 */
@Component
public class MessageUtils {

	private static final Logger LOGGER = Logger.getLogger(MessageUtils.class);

	private static final String GLOBAL_SUCCESS_MSG_FIELD = "payAsia_global_success";

	@Resource
	MessageSource messageSource;

	/**
	 * Gets error message corresponding to exception.
	 * 
	 * @param ex
	 *            the Exception
	 * @param request
	 *            the HttpServletRequest
	 * @param model
	 *            the ModelMap
	 */
	public String handleException(Exception ex, HttpServletRequest httpRequest) {

		String errorMsg = this.getMessage(ex, httpRequest);
		if (ex instanceof PayAsiaBusinessException) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(errorMsg, ex);
			} else {
				LOGGER.info(errorMsg);
			}
		} else {
			LOGGER.error(errorMsg, ex);
		}

		return errorMsg;
	}

	private String getMessage(Exception ex, HttpServletRequest httpRequest) {
		String errorKey = "error.generic";
		String[] errorArgs = null;
		String defaultMessage = "Exception occurred. Please contact system administrator or try again later.";

		if (PayAsiaException.class.equals(ex.getClass().getSuperclass())) {

			PayAsiaException payAsiaEx = (PayAsiaException) ex;

			if (payAsiaEx.getKey() != null
					&& !"".equals(payAsiaEx.getKey().trim())) {
				errorKey = payAsiaEx.getKey();
			}

			if (payAsiaEx.getErrorArgs() != null) {
				errorArgs = payAsiaEx.getErrorArgs();
			}

			if (payAsiaEx.getDefaultMessage() != null
					&& !"".equals(payAsiaEx.getDefaultMessage().trim())) {
				defaultMessage = payAsiaEx.getDefaultMessage();
			}

			if (payAsiaEx.getErrType().equals(ErrorType.SYSTEM)) {
				LOGGER.error(ex.getMessage(), ex);
			}

		} else {
			errorArgs = new String[] { ex.getClass().getName(), ex.getMessage() };
		}
		String errorMsg = messageSource.getMessage(errorKey, errorArgs,
				defaultMessage, httpRequest.getLocale());

		return errorMsg;
	}

	/**
	 * Handle success messages and add these to model map.
	 * 
	 * @param request
	 *            the HttpServletRequest
	 * @param model
	 *            the Model
	 * @param msg
	 *            Message object containing message key.
	 */
	public void handleSuccess(HttpServletRequest request, ModelMap model,
			Message msg) {
		RequestContext ctx = new RequestContext(request);
		String successMsg = ctx.getMessage(msg.getMsgKey(), msg.getMsgArgs(),
				msg.getDefaultMsg());
		model.put(GLOBAL_SUCCESS_MSG_FIELD, successMsg);
	}

	/**
	 * Handle success messages and add these to model map.
	 * 
	 * @param request
	 *            the HttpServletRequest
	 * @param model
	 *            the Model
	 * @param msgKey
	 *            the success message key
	 */
	public void handleSuccess(HttpServletRequest request, ModelMap model,
			String msgKey) {

		Message msg = new Message(msgKey);
		this.handleSuccess(request, model, msg);
	}

	/**
	 * Handle success messages and add these to model map.
	 * 
	 * @param request
	 *            the HttpServletRequest
	 * @param model
	 *            the Model
	 * @param msgKey
	 *            the success message key
	 */
	public void handleSuccess(HttpServletRequest request, ModelMap model,
			String msgKey, String[] msgArgs) {

		Message msg = new Message(msgKey, msgArgs, "key.undefined");
		this.handleSuccess(request, model, msg);
	}
}
