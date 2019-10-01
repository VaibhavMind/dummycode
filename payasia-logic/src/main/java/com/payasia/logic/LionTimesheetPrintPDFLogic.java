/**
 * @author mayankdwivedi
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

public interface LionTimesheetPrintPDFLogic {

	/**
	 * @param document
	 * @param writer
	 * @param currentPageNumber
	 * @param companyId
	 * @param claimApplicationId
	 * @return
	 */

	PdfPTable createTimesheetPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long claimApplicationId,
			boolean hasLundinTimesheetModule);

}
