/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * The Interface ClaimFormPrintPDFLogic.
 * 
 * 
 */

public interface LeaveApplicationPrintPDFLogic {

	/**
	 * @param document
	 * @param writer
	 * @param currentPageNumber
	 * @param companyId
	 * @param claimApplicationId
	 * @return
	 */
	PdfPTable createLeaveReportPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long leaveApplicationId);

}
