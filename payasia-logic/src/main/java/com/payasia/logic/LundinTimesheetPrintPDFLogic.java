/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.form.LundinTimesheetReportsForm;

/**
 * The Interface ClaimFormPrintPDFLogic.
 * 
 * 
 */

public interface LundinTimesheetPrintPDFLogic {

	/**
	 * @param document
	 * @param writer
	 * @param currentPageNumber
	 * @param companyId
	 * @param claimApplicationId
	 * @return
	 */

	PdfPTable createTimesheetPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long timesheetId,
			boolean hasLundinTimesheetModule,
			LundinTimesheetDTO lundinTimesheetDTO);

	List<LundinTimesheetDTO> generateTimesheets(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			Long employeeId, Long companyId);

}
