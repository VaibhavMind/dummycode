/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * The Interface ClaimFormPrintPDFLogic.
 * 
 * 
 */

public interface ClaimFormPrintPDFLogic {

	/**
	 * @param document
	 * @param writer
	 * @param currentPageNumber
	 * @param companyId
	 * @param claimApplicationId
	 * @return
	 */

	PdfPTable createClaimReportPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long claimApplicationId,
			boolean hasLundinTimesheetModule);

	List<Object[]> getEmpBankDetailFieldsValueList(Long companyId,
			String dateFormat, Long employeeId);

}
