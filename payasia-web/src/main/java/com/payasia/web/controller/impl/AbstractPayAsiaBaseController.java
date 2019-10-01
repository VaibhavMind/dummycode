package com.payasia.web.controller.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.web.util.MessageUtils;

public class AbstractPayAsiaBaseController {
	private static final String GLOBAL_ERROR_MSG_FIELD = "ilex_global_error";

	private String viewName;
	private ModelMap model;
	private String sysExKey = "key.undefined";

	@Resource
	MessageUtils messageUtils;

	/**
	 * Inits the exception handler.
	 * 
	 * @param viewName
	 *            the view name
	 * @param model
	 *            the model
	 */
	protected void initExceptionHandler(String viewName, ModelMap model) {
		this.viewName = viewName;
		this.model = model;
	}

	/**
	 * Inits the exception handler.
	 * 
	 * @param viewName
	 *            the view name
	 * @param model
	 *            the model
	 * @param sysExKey
	 *            custom system exception key
	 */
	protected void initExceptionHandler(String viewName, ModelMap model,
			String sysExKey) {
		this.viewName = viewName;
		this.model = model;
		this.sysExKey = sysExKey;
	}

	/**
	 * Inits the exception handler.
	 * 
	 * @param viewName
	 *            the view name
	 * @param modelName
	 *            the model name
	 * @param modelObject
	 *            the model object
	 * @param sysExKey
	 *            custom system exception key
	 */
	protected void initExceptionHandler(String viewName, String modelName,
			Object modelObject, String sysExKey) {
		this.viewName = viewName;
		this.model = new ModelMap();
		this.model.put(modelName, modelObject);
		this.sysExKey = sysExKey;
	}

	/**
	 * Inits the exception handler.
	 * 
	 * @param viewName
	 *            the view name
	 * @param modelName
	 *            the model name
	 * @param modelObject
	 *            the model object
	 */
	protected void initExceptionHandler(String viewName, String modelName,
			Object modelObject) {
		this.viewName = viewName;
		this.model = new ModelMap();
		this.model.put(modelName, modelObject);
	}

	/**
	 * Handle exception.
	 * 
	 * @param request
	 *            the request
	 * @param ex
	 *            the exception
	 * @return the model and view
	 */
	@ExceptionHandler
	public ModelAndView handleException(HttpServletRequest request,
			RuntimeException ex) {
		if (viewName == null || "".equals(viewName.trim())) {
			throw ex;
		}

		if (ex instanceof PayAsiaSystemException) {
			((PayAsiaSystemException) ex).setKey(this.sysExKey);
		}

		String errorMsg = messageUtils.handleException(ex, request);
		model.put(GLOBAL_ERROR_MSG_FIELD, errorMsg);
		return new ModelAndView(viewName, model);
	}
}
