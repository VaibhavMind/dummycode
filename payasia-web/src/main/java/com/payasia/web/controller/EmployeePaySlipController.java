package com.payasia.web.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.springframework.ui.ModelMap;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.common.form.EmployeePaySlipForm;

 
/**
 * The Interface EmployeePaySlipController.
 *
 * @author vivekjain
 */
/**
 * The Interface EmployeePaySlipController.
 */
public interface EmployeePaySlipController {

	/**
	 * purpose : Get Payslip of Employee.
	 * 
	 * @param model
	 *            the model
	 * @param employeePaySlipForm
	 *            the employee pay slip form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return Employee Payslip in Byte array with response
	 * @throws DocumentException
	 *             the document exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the jAXB exception
	 * @throws SAXException
	 *             the sAX exception
	 */

	byte[] getPaySlip(ModelMap model, EmployeePaySlipForm employeePaySlipForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws DocumentException, IOException,
			JAXBException, SAXException;

	String getPaySlipStatus(ModelMap model,
			EmployeePaySlipForm employeePaySlipForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	byte[] downloadPaySlip(ModelMap model,
			EmployeePaySlipForm employeePaySlipForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws DocumentException, IOException,
			JAXBException, SAXException;

}
