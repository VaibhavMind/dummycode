package com.payasia.logic;

/**
 * @author vivekjain
 *
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.PaySlipPDFTemplateDTO;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.Payslip;

/**
 * The Interface PaySlipPDFLogoHeaderSection.
 */
@Transactional
public interface PaySlipPDFLogoHeaderSection {
	/**
	 * Gets the page header height.
	 * 
	 * @param writer
	 *            the writer
	 * @param document
	 *            the document
	 * @param pageNumber
	 *            the page number
	 * @return the page header height
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SAXException
	 * @throws JAXBException
	 */
	float getPageHeaderHeight(PdfWriter writer, Document document,
			int pageNumber, List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException,
			JAXBException, SAXException;

	/**
	 * set the PaySlipPDFTemplateDTO.
	 * 
	 * @param PaySlipPDFTemplateDTO
	 *            the paySlipPDFTemplateDTO
	 */
	void setPaySlipPDFTemplateDTO(PaySlipPDFTemplateDTO paySlipPDFTemplateDTO);

	/**
	 * Get the PaySlipPDFTemplateDTO.
	 * 
	 */
	PaySlipPDFTemplateDTO getPaySlipPDFTemplateDTO();

	/**
	 * Gets the XML.
	 * 
	 * @param Long
	 *            the companyId
	 * @param String
	 *            the sectionName
	 * @return the Max Dynamic form Meta Data
	 */
	String getXML(Long companyId, String sectionName, Payslip payslip);

	/**
	 * set the Company Id.
	 * 
	 * @param Long
	 *            the companyId
	 */
	void setCompanyId(Long companyId);

	/**
	 * Gets the Logo Section Pdf Table.
	 * 
	 * @param PdfWriter
	 *            the PdfWriter
	 * @param Document
	 *            the Document
	 * @param Payslip
	 *            the Payslip
	 * @param int the currentPageNumber
	 * @return PdfPTable with Logo and Header Section
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SAXException
	 * @throws JAXBException
	 */
	PdfPTable getLogoSection(PdfWriter writer, Document document,
			Payslip payslip, int currentPageNumber,
			List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException,
			JAXBException, SAXException;

	/**
	 * Sets the payslip.
	 * 
	 */
	void setPayslip(Payslip payslip);

	/**
	 * Gets the Footer Section PdfTable.
	 * 
	 * @param Document
	 *            the Document
	 * @param Payslip
	 *            the Payslip
	 * @param int the currentPageNumber
	 * @return the Footer Section Pdf table
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SAXException
	 * @throws JAXBException
	 */
	PdfPTable getFooterSection(Document document, Payslip payslip,
			int currentPageNumber, List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException,
			JAXBException, SAXException;

	/**
	 * Gets the page Footer height.
	 * 
	 * @param writer
	 *            the writer
	 * @param document
	 *            the document
	 * @return the page Footer height
	 * @throws BadElementException
	 *             the bad element exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SAXException
	 * @throws JAXBException
	 */
	float getFooterHeight(PdfWriter writer, Document document,
			List<DynamicFormRecord> dynamicFormRecordList)
			throws BadElementException, MalformedURLException, IOException,
			JAXBException, SAXException;

}
