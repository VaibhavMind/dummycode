package com.payasia.web.controller.impl;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PayslipDesignerResponse;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.PaySlipDesignerLogic;
import com.payasia.logic.PaySlipDynamicFormLogic;
import com.payasia.web.controller.PaySlipDesignerController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class PaySlipDesignerControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/paySlipDesigner")
public class PaySlipDesignerControllerImpl implements PaySlipDesignerController {

	/** The pay slip designer logic. */
	@Resource
	PaySlipDesignerLogic paySlipDesignerLogic;
	@Resource
	PaySlipDynamicFormLogic paySlipDynamicFormLogic;

	private static final Logger LOGGER = Logger
			.getLogger(PaySlipDesignerControllerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.PaySlipDesignerController#
	 * getLabelListForPaySlipDesigner(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getLabelList.html", method = RequestMethod.POST)
	@ResponseBody public String getLabelListForPaySlipDesigner(
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PayslipDesignerResponse payslipDesignerResponse = paySlipDesignerLogic
				.getLabelList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(payslipDesignerResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDesignerController#saveXML(java.lang
	 * .String[], javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("deprecation")
	@Override
	@RequestMapping(value = "/saveXML.html", method = RequestMethod.POST)
	@ResponseBody public String saveXML(
			@RequestParam(value = "metaData", required = true) String[] metaData,
			@RequestParam(value = "year") int year,
			@RequestParam(value = "month") long month,
			@RequestParam(value = "part") int part, HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		 
		boolean payslipReleasedStatus = paySlipDynamicFormLogic
				.getPayslipReleasedStatus(companyId, month, year, part);
		if (payslipReleasedStatus) {
			return PayAsiaConstants.PAYSLIP_RELEASED;
		}
			
      try{
	   
    	  paySlipDesignerLogic.saveXML(companyId,  URLDecoder.decode(metaData[0]),
				PayAsiaConstants.LOGO_SECTION, year, month, part);
		paySlipDesignerLogic.saveXML(companyId,  URLDecoder.decode(metaData[1]),
				PayAsiaConstants.HEADER_SECTION, year, month, part);
		paySlipDesignerLogic.saveXML(companyId,  URLDecoder.decode(metaData[2]),
				PayAsiaConstants.TOTAL_INCOME_SECTION, year, month, part);

		paySlipDesignerLogic.saveXML(companyId,  URLDecoder.decode(metaData[3]),
				PayAsiaConstants.STATUTORY_SECTION, year, month, part);
		paySlipDesignerLogic.saveXML(companyId,  URLDecoder.decode(metaData[4]),
				PayAsiaConstants.SUMMARY_SECTION, year, month, part);
		paySlipDesignerLogic.saveXML(companyId,  URLDecoder.decode(metaData[5]),
				PayAsiaConstants.FOOTER_SECTION, year, month, part);
		return PayAsiaConstants.PAYASIA_SUCCESS;
      }catch(Exception exception){
    	  LOGGER.error(exception.getMessage(),exception);
    	  return PayAsiaConstants.PAYASIA_ERROR;
	   }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDesignerController#getXML(java.lang
	 * .String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getXML.html", method = RequestMethod.POST)
	@ResponseBody public String getXML(
			@RequestParam(value = "sectionName", required = true) String sectionName,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String xmlFile = paySlipDesignerLogic.getXML(companyId, sectionName);
		Map<String, String> file = new HashMap<String, String>();
		file.put(sectionName, xmlFile);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(file, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.PaySlipDesignerController#generatePdf(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/generatePdf", method = RequestMethod.GET)
	public @ResponseBody void generatePdf(HttpServletRequest request,
			HttpServletResponse response) {
		ServletOutputStream outStream = null;
		try {
			Long companyId = (Long) request.getSession().getAttribute(
					PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
			Long employeeId = (Long) request.getSession().getAttribute(
					PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
			byte[] byteFile = paySlipDesignerLogic.generatePdf(companyId,
					employeeId);
			response.reset();
			response.setContentType("application/pdf");
			response.setContentLength(byteFile.length);
			String pdfName = "PA_PaySlipPdf";

			response.setHeader("Content-Disposition", "inline;filename="
					+ pdfName);

			outStream = response.getOutputStream();
			outStream.write(byteFile);

		} catch (IOException exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		} finally {
			try {

				if (outStream != null) {

					outStream.flush();
					outStream.close();
				}

			} catch (IOException exception) {
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(),
						exception);
			}
		}
	}
}
