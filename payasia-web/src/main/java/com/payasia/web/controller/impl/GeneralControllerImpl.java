package com.payasia.web.controller.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.logic.GeneralLogic;
import com.payasia.web.controller.GeneralController;

@Controller
@RequestMapping(value = { "/employee/generalDetail", "/admin/generalDetail" })
public class GeneralControllerImpl implements GeneralController {

	@Resource
	GeneralLogic generalLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.GeneralController#generateEncryptedPassword
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/generateEncryptedPassword", method = RequestMethod.POST)
	public @ResponseBody
	void generateEncryptedPassword(HttpServletRequest request,
			HttpServletResponse response) {
		generalLogic.generateEncryptedPasswords();
	}

}
