/**
 * @author vivekjain
 *
 */
package com.payasia.logic.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.EmployeeAsOnLeaveDTO;
import com.payasia.common.form.LeaveReportsForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.logic.LeaveBalCustomReportPDFLogic;

@Component
public class LeaveBalCustomReportPDFLogicImpl implements
		LeaveBalCustomReportPDFLogic {
	/** The total. */

	private static final Logger LOGGER = Logger
			.getLogger(LeaveBalCustomReportPDFLogicImpl.class);

	@Override
	public void createLeaveBalCustomEmpPerPagePDF(Document document,
			PdfWriter writer, int currentPageNumber, Long companyId,
			Long employeeId, List<EmployeeAsOnLeaveDTO> empAsOnLeaveDTOs,
			LeaveReportsForm leaveReportsForm) {

		Map<Long, List<EmployeeAsOnLeaveDTO>> empAsOnLeaveDTOMap = new HashMap<Long, List<EmployeeAsOnLeaveDTO>>();

		for (EmployeeAsOnLeaveDTO employeeAsOnLeaveDTO : empAsOnLeaveDTOs) {
			List<EmployeeAsOnLeaveDTO> empAsOnLeaveDTOList = new ArrayList<>();
			if (!empAsOnLeaveDTOMap.containsKey(employeeAsOnLeaveDTO
					.getEmployeeId())) {
				empAsOnLeaveDTOList.add(employeeAsOnLeaveDTO);
				empAsOnLeaveDTOMap.put(employeeAsOnLeaveDTO.getEmployeeId(),
						empAsOnLeaveDTOList);
			} else {
				List<EmployeeAsOnLeaveDTO> existingEmpAsOnLeaveDTOListList = empAsOnLeaveDTOMap
						.get(employeeAsOnLeaveDTO.getEmployeeId());
				empAsOnLeaveDTOList.add(employeeAsOnLeaveDTO);
				existingEmpAsOnLeaveDTOListList.addAll(empAsOnLeaveDTOList);
			}
		}

		Set<Long> keySet = empAsOnLeaveDTOMap.keySet();
		if (empAsOnLeaveDTOs.isEmpty()) {
			PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
			PdfPCell mainCell = new PdfPCell();
			mainCell.setBorder(0);
			mainSectionTable.addCell(mainCell);
			try {
				document.add(mainSectionTable);
			} catch (DocumentException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return;
		}
		for (Iterator<Long> iterator = keySet.iterator(); iterator.hasNext();) {
			Long key = (Long) iterator.next();
			List<EmployeeAsOnLeaveDTO> employeeAsOnLeaveDTOList = empAsOnLeaveDTOMap
					.get(key);

			PdfPCell mainCell = null;
			PdfPTable mainSectionTable = null;
			PdfPTable headerSectionTable = null;
			PdfPTable summarySectionTable = null;
			PdfPTable leaveDetailHeaderSectionTable = null;
			if (!employeeAsOnLeaveDTOList.isEmpty()) {
				boolean firstResult = true;
				for (EmployeeAsOnLeaveDTO employeeAsOnLeaveDTO : employeeAsOnLeaveDTOList) {
					if (firstResult) {
						firstResult = false;
						document.newPage();
						mainSectionTable = new PdfPTable(new float[] { 1 });
						mainSectionTable.setWidthPercentage(95f);
						mainSectionTable
								.setHorizontalAlignment(Element.ALIGN_CENTER);

						mainCell = new PdfPCell();
						mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						mainCell.setPadding(0);
						mainCell.setPaddingTop(5f);
						mainCell.setBorder(0);
						PDFUtils.setDefaultCellLayout(mainCell);

						headerSectionTable = getHeaderSection(document);
						headerSectionTable.setWidthPercentage(100f);

						summarySectionTable = getEmployeeHeaderDetailSection(
								document, leaveReportsForm,
								employeeAsOnLeaveDTO.getEmployeeNumber());
						leaveDetailHeaderSectionTable = getLeaveDetailHeaderSection(
								document, leaveReportsForm,
								employeeAsOnLeaveDTO, employeeId);
						mainCell.addElement(headerSectionTable);
						mainCell.addElement(summarySectionTable);
						mainCell.addElement(leaveDetailHeaderSectionTable);
					}

					PdfPTable categoryWiseTotalSectionTable = getLeaveDetailSection(
							document, leaveReportsForm, employeeAsOnLeaveDTO,
							employeeId);

					mainCell.addElement(categoryWiseTotalSectionTable);

				}
				mainSectionTable.addCell(mainCell);
				try {
					document.add(mainSectionTable);
				} catch (DocumentException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}

	}

	private PdfPTable getHeaderSection(Document document) {
		PdfPTable headerSectionTable = new PdfPTable(new float[] { 1f });
		headerSectionTable.setWidthPercentage(100f);
		headerSectionTable.setSpacingAfter(5f);

		PdfPCell headerCell = new PdfPCell(new Phrase("LEAVE ENTITLEMENT",
				getBaseFont10Bold()));
		headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerCell.setBorder(0);

		headerSectionTable.addCell(headerCell);
		return headerSectionTable;
	}

	private PdfPTable getEmployeeHeaderDetailSection(Document document,
			LeaveReportsForm leaveReportsForm, String employeeNumber) {
		PdfPTable headerSectionTable = new PdfPTable(new float[] { 0.2f, 1f });
		headerSectionTable.setWidthPercentage(100f);
		headerSectionTable.setSpacingAfter(15f);

		PdfPCell employeeIdLabelCell = new PdfPCell(new Phrase("Employee ID:",
				getBaseFont10Bold()));
		employeeIdLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		employeeIdLabelCell.setBorder(0);

		PdfPCell employeeIdValCell = new PdfPCell(new Phrase(employeeNumber,
				getBaseFontNormal()));
		employeeIdValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		employeeIdValCell.setBorder(0);
		headerSectionTable.addCell(employeeIdLabelCell);
		headerSectionTable.addCell(employeeIdValCell);

		PdfPCell uniqueIdLabelCell = new PdfPCell(new Phrase("Unique ID:",
				getBaseFont10Bold()));
		uniqueIdLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		uniqueIdLabelCell.setBorder(0);

		PdfPCell uniqueIdValCell = new PdfPCell(
				new Phrase(
						"#"
								+ employeeNumber
								+ DateUtils
										.convertCurrentDateTimeWithMilliSecToString(PayAsiaConstants.TIMESTAMP_WITH_MILLISEC_FORMAT),
						getBaseFontNormal()));
		uniqueIdValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		uniqueIdValCell.setBorder(0);
		headerSectionTable.addCell(uniqueIdLabelCell);
		headerSectionTable.addCell(uniqueIdValCell);

		PdfPCell asOfDateLabelCell = new PdfPCell(new Phrase("As Of Date:",
				getBaseFont10Bold()));
		asOfDateLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		asOfDateLabelCell.setBorder(0);

		PdfPCell asOfDateValCell = new PdfPCell(new Phrase(
				leaveReportsForm.getLeaveBalAsOnDateCustomRep(),
				getBaseFontNormal()));
		asOfDateValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		asOfDateValCell.setBorder(0);
		headerSectionTable.addCell(asOfDateLabelCell);
		headerSectionTable.addCell(asOfDateValCell);

		return headerSectionTable;
	}

	private PdfPTable getLeaveDetailHeaderSection(Document document,
			LeaveReportsForm leaveReportsForm,
			EmployeeAsOnLeaveDTO empAsOnLeaveDTO, Long employeeId) {
		PdfPTable detailSectionTable = new PdfPTable(new float[] { 1f, 1f, 1f,
				1f });
		detailSectionTable.setWidthPercentage(100f);
		// detailSectionTable.setSpacingAfter(1f);

		PdfPCell detailHeader1Cell = new PdfPCell(new Phrase("",
				getBaseFont10Bold()));
		detailHeader1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		detailHeader1Cell.setBorder(0);

		PdfPCell detailHeader2Cell = new PdfPCell(new Phrase("Available",
				getBaseFont10Bold()));
		detailHeader2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		// detailHeader2Cell.setBorder(0);

		PdfPCell detailHeader3Cell = new PdfPCell(new Phrase("Taken",
				getBaseFont10Bold()));
		detailHeader3Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		// detailHeader3Cell.setBorder(0);

		PdfPCell detailHeader4Cell = new PdfPCell(new Phrase("Balance",
				getBaseFont10Bold()));
		detailHeader4Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		// detailHeader4Cell.setBorder(0);
		detailSectionTable.addCell(detailHeader1Cell);
		detailSectionTable.addCell(detailHeader2Cell);
		detailSectionTable.addCell(detailHeader3Cell);
		detailSectionTable.addCell(detailHeader4Cell);

		return detailSectionTable;
	}

	private PdfPTable getLeaveDetailSection(Document document,
			LeaveReportsForm leaveReportsForm,
			EmployeeAsOnLeaveDTO empAsOnLeaveDTO, Long employeeId) {
		PdfPTable detailSectionTable = new PdfPTable(new float[] { 1f, 1f, 1f,
				1f });
		detailSectionTable.setWidthPercentage(100f);
		// detailSectionTable.setSpacingAfter(1f);

		PdfPCell detail1Cell = new PdfPCell(new Phrase(
				empAsOnLeaveDTO.getLeaveTypeName(), getBaseFont10Bold()));
		detail1Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		// detail1Cell.setBorder(0);

		PdfPCell detail2Cell = new PdfPCell(new Phrase(
				String.valueOf(empAsOnLeaveDTO.getAvailable()),
				getBaseFontNormal()));
		detail2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		// detail2Cell.setBorder(0);

		PdfPCell detail3Cell = new PdfPCell(
				new Phrase(String.valueOf(empAsOnLeaveDTO.getTaken()),
						getBaseFontNormal()));
		detail3Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		// detail3Cell.setBorder(0);

		PdfPCell detail4Cell = new PdfPCell(new Phrase(
				String.valueOf(empAsOnLeaveDTO.getBalance()),
				getBaseFontNormal()));
		detail4Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		// detail4Cell.setBorder(0);
		detailSectionTable.addCell(detail1Cell);
		detailSectionTable.addCell(detail2Cell);
		detailSectionTable.addCell(detail3Cell);
		detailSectionTable.addCell(detail4Cell);

		return detailSectionTable;
	}

	private Font getBaseFontNormal() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 8, Font.NORMAL, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont10Bold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 8, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont12Bold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 11, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}
}
