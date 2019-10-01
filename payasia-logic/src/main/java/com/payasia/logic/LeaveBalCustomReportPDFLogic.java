/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.EmployeeAsOnLeaveDTO;
import com.payasia.common.form.LeaveReportsForm;

/**
 * The Interface ClaimFormPrintPDFLogic.
 * 
 * 
 */

public interface LeaveBalCustomReportPDFLogic {

	/**
	 * @param document
	 * @param writer
	 * @param currentPageNumber
	 * @param companyId
	 * @param claimApplicationId
	 * @return
	 */

	void createLeaveBalCustomEmpPerPagePDF(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long employeeId,
			List<EmployeeAsOnLeaveDTO> empAsOnLeaveDTOs,
			LeaveReportsForm leaveReportsForm);

}
