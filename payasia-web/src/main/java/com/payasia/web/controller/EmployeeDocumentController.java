/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.web.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.common.form.AdminPaySlipForm;

/**
 * The Interface EmployeeDocumentController.
 */
public interface EmployeeDocumentController {

	/**
	 * Purpose: To get the employee id for auto fill search string.
	 * 
	 * @param searchString
	 *            the search string
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the employee id
	 */
	String getEmployeeId(String searchString, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Purpose: To get the tax document.
	 * 
	 * @param documentId
	 *            the document id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @return the tax documents
	 */
	void getTaxDocuments(Long documentId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	/**
	 * Purpose: To get the payslip of an Employee.
	 * 
	 * @param adminPaySlipForm
	 *            the admin pay slip form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the pay slip
	 * @throws SAXException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws DocumentException
	 */
	byte[] getPaySlip(AdminPaySlipForm adminPaySlipForm, BindingResult result,
			ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale)
			throws DocumentException, IOException, JAXBException, SAXException;

	/**
	 * Purpose: To get the tax document list.
	 * 
	 * @param employeeNumber
	 *            the employee number
	 * @param year
	 *            the year
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @return the tax document list
	 */
	String getTaxDocumentList(String employeeNumber, int year,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String getPaySlipStatus(AdminPaySlipForm adminPaySlipForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	/**
	 * Gets the pay slip status for print.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param part
	 *            the part
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * 
	 * @return the pay slip status for print
	 */
	String getPaySlipStatusForPrint(Long employeeId, Integer year, Long month,
			Integer part, Long companyId, Long sessionEmployeeId,
			HttpServletRequest request, Locale locale);

	byte[] generatePrintTokenPaySlips(String employeeIndex,
			String printTokenKey, String loggedInCompanyId,
			String loggedInEmployeeId, HttpServletRequest request,
			HttpServletResponse response, Locale locale)
			throws NumberFormatException, IOException;

	String getEmployeeFilterList(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String searchEmployeeListForPrint(String columnName, String sortingType,
			int page, int rows, String empName, String empNumber,
			boolean isShortList, String metaData, HttpServletRequest request,
			HttpServletResponse response);

	String generateParts(Integer year, Long month, HttpServletRequest request,
			HttpServletResponse response);

	String sendPayslips(String[] employeeList, Integer year, Integer month,
			Integer part, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	byte[] downloadPaySlip(ModelMap model, AdminPaySlipForm adminPaySlipForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws DocumentException, IOException,
			JAXBException, SAXException;

	/**
	 * Generate payslip for print.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param part
	 *            the part
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the byte[]
	 * @throws DocumentException
	 *             the document exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the jAXB exception
	 * @throws SAXException
	 *             the sAX exception
	 */
	byte[] generatePayslipForPrint(Long employeeId, Integer year, Long month,
			Integer part, Long companyId, Long sessionEmployeeId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws DocumentException, IOException,
			JAXBException, SAXException;

	
	String getPayslipPartDetailsForAdmin(String employeeNumber, Integer year,
			Long monthId, HttpServletRequest request,
			HttpServletResponse response);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

	byte[] downloadTaxDocuments(Long documentId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws DocumentException, IOException, JAXBException, SAXException;

}
