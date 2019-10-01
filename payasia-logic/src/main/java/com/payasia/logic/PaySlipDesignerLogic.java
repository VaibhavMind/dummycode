package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.PaySlipDynamicForm;
import com.payasia.common.form.PayslipDesignerResponse;

/**
 * The Interface PaySlipPDFLogoHeaderSectionImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Transactional
public interface PaySlipDesignerLogic {

	/**
	 * purpose : get Label List For PaySlipDesigner.
	 * 
	 * @param Long
	 *            the companyId
	 * @return PayslipDesignerResponse contains Label List
	 */
	PayslipDesignerResponse getLabelList(Long companyId);

	/**
	 * purpose : save XML.
	 * 
	 * @param part
	 * @param month
	 * @param year
	 * 
	 * @param Long
	 *            companyId
	 * @param String
	 *            the metaData
	 * @param String
	 *            the sectionName
	 */
	void saveXML(Long companyId, String metaData, String sectionName, int year,
			long month, int part);

	/**
	 * purpose: Gets the XML.
	 * 
	 * @param companyId
	 *            the company id
	 * @param sectionName
	 *            the section name
	 * @return the Max Dynamic form Meta Data
	 */
	String getXML(Long companyId, String sectionName);

	/**
	 * Generate Payslip Pdf Template.
	 * 
	 * @param companyId
	 *            the company id
	 * @return pdf in Byte Format
	 */
	byte[] generatePdf(Long companyId, Long employeeId);

	PaySlipDynamicForm getEffectiveFrom(Long companyId);

}
