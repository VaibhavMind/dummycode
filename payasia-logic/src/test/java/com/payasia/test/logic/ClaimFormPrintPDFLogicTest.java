/**
 * @author vivekjain
 *
 */
package com.payasia.test.logic;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaPDFConstants;

public class ClaimFormPrintPDFLogicTest extends PdfPageEventHelper {
	/** The total. */

	private static final Logger LOGGER = Logger
			.getLogger(ClaimFormPrintPDFLogicTest.class);

	public PdfPTable createClaimReportPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId) {
		PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
		float tableWidth = document.right() - document.left() - 3
				* PayAsiaPDFConstants.X_PADDING;
		 
		mainSectionTable.setWidthPercentage(95f);
		 
		mainSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);
		mainCell.setPaddingTop(5f);
		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);

		 
		PdfPTable headerSectionTable = getHeaderSection(document);
		headerSectionTable.setWidthPercentage(110f);

		 
		PdfPTable pdfTableSummaryHeaderTable = getPdfTableHeader(document,
				"Summary");
		pdfTableSummaryHeaderTable.setWidthPercentage(110f);
		PdfPTable summarySectionTable = getSummarySection(document, companyId);
		 

		 
		PdfPTable pdfTableWorkflowHeaderTable = getPdfTableHeader(document,
				"Workflow History");
		pdfTableWorkflowHeaderTable.setWidthPercentage(110f);
		PdfPTable workflowSectionTable = getWorkFlowHistorySection(document,
				companyId);
		 

		 
		PdfPTable pdfTableCategoryWiseHeaderTable = getPdfTableHeader(document,
				"Category Wise Total");
		pdfTableCategoryWiseHeaderTable.setWidthPercentage(110f);
		PdfPTable categoryWiseTotalSectionTable = getCategoryWiseTotalSection(document);
		 

		 
		PdfPTable pdfTableItemsSectionHeaderTable = getPdfTableHeader(document,
				"Items");
		pdfTableItemsSectionHeaderTable.setWidthPercentage(110f);
		PdfPTable itemsSectiontable = getItemsSection(document, writer);

		mainCell.addElement(headerSectionTable);
		 
		mainCell.addElement(summarySectionTable);
		 
		mainCell.addElement(workflowSectionTable);

		 
		mainCell.addElement(categoryWiseTotalSectionTable);
		mainCell.addElement(pdfTableItemsSectionHeaderTable);
		mainCell.addElement(itemsSectiontable);

		mainSectionTable.addCell(mainCell);
		return mainSectionTable;
	}

	private PdfPTable getPdfTableHeader(Document document,
			String tableHeaderName) {
		PdfPTable pdfTableHeaderTable = new PdfPTable(new float[] { 1f });
		pdfTableHeaderTable.setWidthPercentage(110f);
		pdfTableHeaderTable.getTotalWidth();
		pdfTableHeaderTable.setSpacingBefore(5f);

		PdfPCell headerCell = new PdfPCell(new Phrase(tableHeaderName,
				getBaseFont10Bold()));
		headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerCell.setPadding(2);
		headerCell
				.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		 

		pdfTableHeaderTable.addCell(headerCell);
		return pdfTableHeaderTable;
	}

	private PdfPTable getHeaderSection(Document document) {
		PdfPTable headerSectionTable = new PdfPTable(new float[] { 1f });
		headerSectionTable.setWidthPercentage(110f);
		headerSectionTable.setSpacingAfter(5f);

		PdfPCell headerCell = new PdfPCell(new Phrase("PayAsia",
				getBaseFont12Bold()));
		headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 
		headerCell.setBorder(0);

		headerSectionTable.addCell(headerCell);
		return headerSectionTable;
	}

	private PdfPTable getSummarySection(Document document, Long companyId) {
		PdfPTable summarySectionTable = new PdfPTable(new float[] { 1f });
		summarySectionTable.setWidthPercentage(110f);
		summarySectionTable.getTotalWidth();
		summarySectionTable.setSpacingBefore(5f);

		PdfPCell SummaryTableHeaderCell = new PdfPCell(new Phrase("Summary",
				getBaseFont10Bold()));
		SummaryTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		SummaryTableHeaderCell
				.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		 
		SummaryTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		 
		 

		PdfPCell pdfCell1 = createSummarySectionTableData("Claim Number:",
				"180", "", "");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		pdfCell1.enableBorderSide(Rectangle.LEFT);
		pdfCell1.enableBorderSide(Rectangle.RIGHT);
		 

		PdfPCell pdfCell2 = createSummarySectionTableData("Employee Name:",
				"Jack Thomas", "Employee No:", "125");
		PDFUtils.setDefaultCellLayout(pdfCell2);
		pdfCell2.enableBorderSide(Rectangle.LEFT);
		pdfCell2.enableBorderSide(Rectangle.RIGHT);
		 

		PdfPCell pdfCell3 = createSummarySectionTableData("Claim Form:",
				"Local Expenses", "Created Date:", "14 Jun 2013");
		PDFUtils.setDefaultCellLayout(pdfCell3);
		pdfCell3.enableBorderSide(Rectangle.LEFT);
		pdfCell3.enableBorderSide(Rectangle.RIGHT);
		 

		PdfPCell pdfCell4 = createSummarySectionTableData(
				"No of items claimed:", "1", "Total Amount:", "100.00 SGD");
		PDFUtils.setDefaultCellLayout(pdfCell4);
		pdfCell4.enableBorderSide(Rectangle.LEFT);
		pdfCell4.enableBorderSide(Rectangle.RIGHT);
		pdfCell4.enableBorderSide(Rectangle.BOTTOM);
		 
		 

		summarySectionTable.addCell(SummaryTableHeaderCell);
		summarySectionTable.addCell(pdfCell1);
		summarySectionTable.addCell(pdfCell2);
		summarySectionTable.addCell(pdfCell3);
		summarySectionTable.addCell(pdfCell4);
		return summarySectionTable;
	}

	private PdfPCell createSummarySectionTableData(String leftColKey,
			String leftColVal, String rightColKey, String rightColVal) {
		PdfPCell pdfPCell = new PdfPCell();

		PdfPTable summarySectionTable = new PdfPTable(new float[] { 1.5f, 1.5f,
				1.5f, 1.5f });
		summarySectionTable.setWidthPercentage(100f);

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(leftColKey,
				getBaseFontNormal()));
		 
		 
		nestedLeftKeyCell.setBorder(0);
		 

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(leftColVal,
				getBaseFontNormal()));
		 
		 
		nestedLeftValueCell.setBorder(0);
		 

		PdfPCell nestedRightKeyCell = new PdfPCell(new Phrase(rightColKey,
				getBaseFontNormal()));
		 
		 
		nestedRightKeyCell.setBorder(0);
		 

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(rightColVal,
				getBaseFontNormal()));
		 
		 
		nestedRightValueCell.setBorder(0);
		 

		summarySectionTable.addCell(nestedLeftKeyCell);
		summarySectionTable.addCell(nestedLeftValueCell);
		summarySectionTable.addCell(nestedRightKeyCell);
		summarySectionTable.addCell(nestedRightValueCell);

		pdfPCell.addElement(summarySectionTable);
		return pdfPCell;
	}

	private PdfPTable getWorkFlowHistorySection(Document document,
			Long companyId) {
		PdfPTable workflowySectionTable = new PdfPTable(new float[] { 1f });
		workflowySectionTable.setWidthPercentage(110f);
		workflowySectionTable.getTotalWidth();
		workflowySectionTable.setSpacingBefore(5f);

		PdfPCell workflowyTableHeaderCell = new PdfPCell(new Phrase(
				"WorkFlow History", getBaseFont10Bold()));
		workflowyTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		workflowyTableHeaderCell
				.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		 
		workflowyTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		 
		 

		PdfPCell pdfCell1 = createWorkFlowSectionTableData("s.no",
				"WorkFlow Rule", "Name", "Remarks", "Status", "Date");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		pdfCell1.enableBorderSide(Rectangle.LEFT);
		pdfCell1.enableBorderSide(Rectangle.RIGHT);
		 

		PdfPCell pdfCell2 = createWorkFlowSectionTableData("1",
				"ClaimReviewer1", "Employee Name", "Test", "Submitted",
				"23 Aug 2013");
		PDFUtils.setDefaultCellLayout(pdfCell2);
		pdfCell2.enableBorderSide(Rectangle.LEFT);
		pdfCell2.enableBorderSide(Rectangle.RIGHT);
		 

		PdfPCell pdfCell3 = createWorkFlowSectionTableData("2",
				"ClaimReviewer2", "Employee Name", "Test", "Accepted",
				"23 Aug 2013");
		PDFUtils.setDefaultCellLayout(pdfCell3);
		pdfCell3.enableBorderSide(Rectangle.LEFT);
		pdfCell3.enableBorderSide(Rectangle.RIGHT);
		 

		PdfPCell pdfCell4 = createWorkFlowSectionTableData("3",
				"ClaimReviewer3", "Employee Name", "Test", "pending",
				"23 Aug 2013");
		PDFUtils.setDefaultCellLayout(pdfCell4);
		pdfCell4.enableBorderSide(Rectangle.LEFT);
		pdfCell4.enableBorderSide(Rectangle.RIGHT);
		pdfCell4.enableBorderSide(Rectangle.BOTTOM);
		 
		 

		workflowySectionTable.addCell(workflowyTableHeaderCell);
		workflowySectionTable.addCell(pdfCell1);
		workflowySectionTable.addCell(pdfCell2);
		workflowySectionTable.addCell(pdfCell3);
		workflowySectionTable.addCell(pdfCell4);
		return workflowySectionTable;

	}

	private PdfPCell createWorkFlowSectionTableData(String leftColKey,
			String leftColVal, String middleColKey, String middleColVal,
			String rightColKey, String rightColVal) {
		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorder(0);

		PdfPTable amountBasedTable = new PdfPTable(new float[] { 0.3f, 0.8f,
				1.5f, 2f, 0.7f, 0.7f });
		amountBasedTable.setWidthPercentage(100f);

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(leftColKey,
				getBaseFontNormal()));
		 
		nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftKeyCell.setBorder(0);
		 

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(leftColVal,
				getBaseFontNormal()));
		 
		nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftValueCell.setBorder(0);
		 

		PdfPCell nestedMiddleKeyCell = new PdfPCell(new Phrase(middleColKey,
				getBaseFontNormal()));
		 
		nestedMiddleKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleKeyCell.setBorder(0);
		 

		PdfPCell nestedMiddleValueCell = new PdfPCell(new Phrase(middleColVal,
				getBaseFontNormal()));
		 
		nestedMiddleValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleValueCell.setBorder(0);
		 

		PdfPCell nestedRightKeyCell = new PdfPCell(new Phrase(rightColKey,
				getBaseFontNormal()));
		 
		nestedRightKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightKeyCell.setBorder(0);
		 

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(rightColVal,
				getBaseFontNormal()));
		 
		nestedRightValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightValueCell.setBorder(0);
		 

		amountBasedTable.addCell(nestedLeftKeyCell);
		amountBasedTable.addCell(nestedLeftValueCell);
		amountBasedTable.addCell(nestedMiddleKeyCell);
		amountBasedTable.addCell(nestedMiddleValueCell);
		amountBasedTable.addCell(nestedRightKeyCell);
		amountBasedTable.addCell(nestedRightValueCell);

		pdfPCell.addElement(amountBasedTable);
		return pdfPCell;
	}

	private PdfPTable getCategoryWiseTotalSection(Document document) {
		PdfPTable categoryWiseTotalSectionTable = new PdfPTable(new float[] {
				2f, 2f });
		categoryWiseTotalSectionTable.setWidthPercentage(110f);
		categoryWiseTotalSectionTable.getTotalWidth();
		categoryWiseTotalSectionTable.setSpacingBefore(5f);

		PdfPCell categoryWiseTableHeaderCell = new PdfPCell(new Phrase(
				"Category Wise Total", getBaseFont10Bold()));
		categoryWiseTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		categoryWiseTableHeaderCell
				.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		 
		categoryWiseTableHeaderCell
				.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		categoryWiseTableHeaderCell.setColspan(2);
		 
		 

		PdfPCell pdfLeftCell = new PdfPCell(new Phrase("Entertainmnet:",
				getBaseFontNormal()));
		pdfLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		pdfLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PDFUtils.setDefaultCellLayout(pdfLeftCell);
		pdfLeftCell.enableBorderSide(Rectangle.LEFT);
		pdfLeftCell.enableBorderSide(Rectangle.BOTTOM);
		 

		PdfPCell pdfRightCell = new PdfPCell(new Phrase("100.00 SGD",
				getBaseFontNormal()));
		pdfRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		pdfRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		PDFUtils.setDefaultCellLayout(pdfRightCell);
		pdfRightCell.enableBorderSide(Rectangle.RIGHT);
		pdfRightCell.enableBorderSide(Rectangle.BOTTOM);
		 

		categoryWiseTotalSectionTable.addCell(categoryWiseTableHeaderCell);
		categoryWiseTotalSectionTable.addCell(pdfLeftCell);
		categoryWiseTotalSectionTable.addCell(pdfRightCell);
		return categoryWiseTotalSectionTable;
	}

	private PdfPTable getItemsSection(Document document, PdfWriter writer) {

		PdfPTable itemSectionTable = new PdfPTable(new float[] { 1f });
		itemSectionTable.setWidthPercentage(110f);
		itemSectionTable.getTotalHeight();

		PdfPCell pdfPCell = new PdfPCell();
		 
		PDFUtils.setDefaultCellLayout(pdfPCell);
		pdfPCell.enableBorderSide(Rectangle.TOP);
		pdfPCell.enableBorderSide(Rectangle.LEFT);
		pdfPCell.enableBorderSide(Rectangle.RIGHT);
		pdfPCell.enableBorderSide(Rectangle.BOTTOM);
		pdfPCell.setPadding(0);

		PdfPTable amountBasedTable = createAmountBasedItemsSection(document);
		PdfPTable quantityBasedTable = createQuantityBasedItemsSection(document);
		PdfPTable forexBasedTable = createForexBasedItemsSection(document);

		pdfPCell.addElement(quantityBasedTable);
		pdfPCell.addElement(amountBasedTable);
		pdfPCell.addElement(quantityBasedTable);
		pdfPCell.addElement(forexBasedTable);
		pdfPCell.addElement(quantityBasedTable);
		pdfPCell.addElement(amountBasedTable);
		pdfPCell.addElement(quantityBasedTable);

		itemSectionTable.addCell(pdfPCell);

		return itemSectionTable;

	}

	private PdfPTable createAmountBasedItemsSection(Document document) {
		PdfPTable amountBasedTable = new PdfPTable(new float[] { 1f });
		amountBasedTable.setWidthPercentage(100f);

		PdfPCell itemTableHeaderCell = new PdfPCell(new Phrase(
				"Client Entertainment", getBaseFont10Bold()));
		itemTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		itemTableHeaderCell
				.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		PDFUtils.setDefaultCellLayout(itemTableHeaderCell);
		itemTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		 
		 

		PdfPCell pdfCell1 = createItemsSectionTableData("Category:",
				"Entertainmnet", "Claim Date:", "Jun 12, 2013", "Receipt No:",
				"");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		 

		PdfPCell pdfCell2 = createItemsSectionTableData("Amount:",
				"100.00 SGD", "Tax amount:", "0.00 SGD", "", "");
		PDFUtils.setDefaultCellLayout(pdfCell2);
		 

		PdfPCell remarksCell = createRemarksCell("Remarks:", "Test");
		PDFUtils.setDefaultCellLayout(remarksCell);
		 

		amountBasedTable.addCell(itemTableHeaderCell);
		amountBasedTable.addCell(pdfCell1);
		amountBasedTable.addCell(pdfCell2);
		amountBasedTable.addCell(remarksCell);
		return amountBasedTable;
	}

	private PdfPTable createQuantityBasedItemsSection(Document document) {
		PdfPTable quantityBasedTable = new PdfPTable(new float[] { 1f });
		quantityBasedTable.setWidthPercentage(100f);

		PdfPCell itemTableHeaderCell = new PdfPCell(new Phrase("Car",
				getBaseFont10Bold()));
		itemTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		itemTableHeaderCell
				.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		PDFUtils.setDefaultCellLayout(itemTableHeaderCell);
		itemTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		 
		 

		PdfPCell pdfCell1 = createItemsSectionTableData("Category:",
				"Local Travel", "Claim Date:", "Jun 14, 2013", "Receipt No:",
				"");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		 

		PdfPCell pdfCell2 = createItemsSectionTableData("Amount:",
				"150.00 SGD", "Tax amount:", "0.00 SGD", "", "");
		PDFUtils.setDefaultCellLayout(pdfCell2);
		 

		PdfPCell pdfCell3 = createItemsSectionTableData("Quantity::", "100",
				"Unit Price:", "1.75 SGD", "Actual Unit Price:", "1.75 SGD");
		PDFUtils.setDefaultCellLayout(pdfCell3);
		 

		PdfPCell pdfCell4 = createItemsSectionTableData("From:", "A", "To:",
				"B", "", "");
		PDFUtils.setDefaultCellLayout(pdfCell4);
		 

		PdfPCell remarksCell = createRemarksCell("Remarks:", "Test");
		PDFUtils.setDefaultCellLayout(remarksCell);
		 

		PdfPCell attachmentCell = createRemarksCell("Attachment:", "sample.JPG");
		PDFUtils.setDefaultCellLayout(attachmentCell);
		 

		quantityBasedTable.addCell(itemTableHeaderCell);
		quantityBasedTable.addCell(pdfCell1);
		quantityBasedTable.addCell(pdfCell2);
		quantityBasedTable.addCell(pdfCell3);
		quantityBasedTable.addCell(pdfCell4);
		quantityBasedTable.addCell(remarksCell);
		quantityBasedTable.addCell(attachmentCell);
		return quantityBasedTable;
	}

	private PdfPTable createForexBasedItemsSection(Document document) {
		PdfPTable forexBasedTable = new PdfPTable(new float[] { 1f });
		forexBasedTable.setWidthPercentage(100f);

		PdfPCell itemTableHeaderCell = new PdfPCell(new Phrase("Travel",
				getBaseFont10Bold()));
		itemTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		itemTableHeaderCell
				.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);
		PDFUtils.setDefaultCellLayout(itemTableHeaderCell);
		itemTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		 
		 

		PdfPCell pdfCell1 = createItemsSectionTableData("Category:",
				"Foreign Travel", "Claim Date:", "Jun 14, 2013", "Receipt No:",
				"");
		PDFUtils.setDefaultCellLayout(pdfCell1);
		 

		PdfPCell pdfCell2 = createItemsSectionTableData("Amount:",
				"150.00 SGD", "Tax amount:", "0.00 SGD", "", "");
		PDFUtils.setDefaultCellLayout(pdfCell2);
		 

		PdfPCell pdfCell3 = createItemsSectionTableData("Currency::", "INR",
				"Forex Rate:", "1.75 SGD", "Receipt Amount:", "1.75 SGD");
		PDFUtils.setDefaultCellLayout(pdfCell3);
		 

		PdfPCell remarksCell = createRemarksCell("Remarks:", "Test");
		PDFUtils.setDefaultCellLayout(remarksCell);
		 

		PdfPCell attachmentCell = createRemarksCell("Attachment:", "sample.JPG");
		PDFUtils.setDefaultCellLayout(attachmentCell);
		 

		forexBasedTable.addCell(itemTableHeaderCell);
		forexBasedTable.addCell(pdfCell1);
		forexBasedTable.addCell(pdfCell2);
		forexBasedTable.addCell(pdfCell3);
		forexBasedTable.addCell(remarksCell);
		forexBasedTable.addCell(attachmentCell);
		return forexBasedTable;
	}

	private PdfPCell createItemsSectionTableData(String leftColKey,
			String leftColVal, String middleColKey, String middleColVal,
			String rightColKey, String rightColVal) {
		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorder(0);

		PdfPTable amountBasedTable = new PdfPTable(new float[] { 1f, 1f, 1f,
				1f, 1f, 1f });
		amountBasedTable.setWidthPercentage(100f);

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(leftColKey,
				getBaseFontNormal()));
		 
		nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftKeyCell.setBorder(0);
		 

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(leftColVal,
				getBaseFontNormal()));
		 
		nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftValueCell.setBorder(0);
		 

		PdfPCell nestedMiddleKeyCell = new PdfPCell(new Phrase(middleColKey,
				getBaseFontNormal()));
		 
		nestedMiddleKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleKeyCell.setBorder(0);
		 

		PdfPCell nestedMiddleValueCell = new PdfPCell(new Phrase(middleColVal,
				getBaseFontNormal()));
		 
		nestedMiddleValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleValueCell.setBorder(0);
		 

		PdfPCell nestedRightKeyCell = new PdfPCell(new Phrase(rightColKey,
				getBaseFontNormal()));
		 
		nestedRightKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightKeyCell.setBorder(0);
		 

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(rightColVal,
				getBaseFontNormal()));
		 
		nestedRightValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightValueCell.setBorder(0);
		 

		amountBasedTable.addCell(nestedLeftKeyCell);
		amountBasedTable.addCell(nestedLeftValueCell);
		amountBasedTable.addCell(nestedMiddleKeyCell);
		amountBasedTable.addCell(nestedMiddleValueCell);
		amountBasedTable.addCell(nestedRightKeyCell);
		amountBasedTable.addCell(nestedRightValueCell);

		pdfPCell.addElement(amountBasedTable);
		return pdfPCell;
	}

	private PdfPCell createRemarksCell(String remarksColKey,
			String remarksColVal) {
		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorder(0);

		PdfPTable remarksTable = new PdfPTable(new float[] { 0.3f, 1.7f });
		remarksTable.setWidthPercentage(100f);

		PdfPCell pdfRemarksCell = new PdfPCell(new Phrase(remarksColKey,
				getBaseFontNormal()));
		pdfRemarksCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		pdfRemarksCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		pdfRemarksCell.setBorder(0);
		 

		PdfPCell pdfRemarksValCell = new PdfPCell(new Phrase(remarksColVal,
				getBaseFontNormal()));
		pdfRemarksValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		pdfRemarksValCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		pdfRemarksValCell.setBorder(0);
		 

		remarksTable.addCell(pdfRemarksCell);
		remarksTable.addCell(pdfRemarksValCell);

		pdfPCell.addElement(remarksTable);

		return pdfPCell;

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
