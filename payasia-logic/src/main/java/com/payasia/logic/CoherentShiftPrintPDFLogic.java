package com.payasia.logic;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public interface CoherentShiftPrintPDFLogic {

	PdfPTable createShiftPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long timesheetId,
			boolean hasLundinTimesheetModule);

}
