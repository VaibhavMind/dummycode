package com.payasia.logic;

/**
 * @author vivekjain
 *
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.PaySlipPDFTemplateDTO;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.Payslip;

 
/**
 * The Interface PaySlipPDFOtherSection.
 */

@Transactional
public interface PaySlipPDFOtherSection {

	/**
	 * Prepare the Other section PdfCell.
	 * 
	 * @param document
	 *            the document
	 * @param xmlString
	 *            the xml string
	 * @param payslip
	 *            the payslip
	 * @return the other section
	 * @throws BadElementException
	 *             the BadElement Exception
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	PdfPCell getOtherSection(Document document, String otherSectionXML,
			Payslip payslip, List<DynamicFormRecord> dynamicFormRecordList,
			String sectionName) throws BadElementException,
			MalformedURLException, IOException;

	/**
	 * Get the PaySlipPDFTemplateDTO.
	 * 
	 * @return the pay slip pdf template dto
	 */
	PaySlipPDFTemplateDTO getPaySlipPDFTemplateDTO();

	/**
	 * set the PaySlipPDFTemplateDTO.
	 * 
	 * @param paySlipPDFTemplateDTO
	 *            the new pay slip pdf template dto
	 */
	void setPaySlipPDFTemplateDTO(PaySlipPDFTemplateDTO paySlipPDFTemplateDTO);

	/**
	 * set the Company Id.
	 * 
	 * @param companyId
	 *            the new company id
	 */
	void setCompanyId(Long companyId);

	/**
	 * Gets the XML.
	 * 
	 * @param companyId
	 *            the company id
	 * @param sectionName
	 *            the section name
	 * @param payslip
	 *            the payslip
	 * @return the Max Dynamic form Meta Data
	 */
	String getXML(Long companyId, String sectionName, Payslip payslip);

	/**
	 * Prepare the Payslip Other section .
	 * 
	 * @param document
	 *            the document
	 * @param writer
	 *            the writer
	 * @param paySlipPDFTemplateDTO
	 *            the pay slip pdf template dto
	 * @param payslip
	 *            the payslip
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the document exception
	 */

	void preparePaySlipPDFOtherSection(Document document, PdfWriter writer,
			PaySlipPDFTemplateDTO paySlipPDFTemplateDTO, Payslip payslip,
			List<DynamicFormRecord> dynamicFormRecordList)
			throws MalformedURLException, IOException, DocumentException;

	/**
	 * Sets the payslip.
	 * 
	 * @param payslip
	 *            the new payslip
	 */
	void setPayslip(Payslip payslip);

}
