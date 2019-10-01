package com.payasia.test.logic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.test.logic.beans.BankInfoTextPayslipDTO;
import com.payasia.test.logic.beans.EmpInfoHeaderTextPayslipDTO;
import com.payasia.test.logic.beans.SummarySectionTextPayslipDTO;
import com.payasia.test.logic.beans.TextPaySlipDTO;
import com.payasia.test.logic.beans.TotalIncomeSectionTextPayslipDTO;

public class PayAsiaTextPaySlipToPdf extends PdfPageEventHelper {
	private static final Logger LOGGER = Logger
			.getLogger(PayAsiaTextPaySlipToPdf.class);

	private static Image getLogoImage() throws MalformedURLException {

		URL imageURL = Thread.currentThread().getContextClassLoader()
				.getResource("images/Malaysia.PNG");

		Image logoImg = null;
		try {
			logoImg = Image.getInstance(imageURL);
		} catch (BadElementException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		logoImg.scaleToFit(102, 50);
		logoImg.setRotationDegrees(0);
		return logoImg;
	}

	public PdfPTable createTextPaySlipPdf(Document document,
			int currentPageNumber, TextPaySlipDTO textPaySlip)
			throws MalformedURLException {

		PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
		float tableWidth = document.right() - document.left() - 3
				* PayAsiaPDFConstants.X_PADDING;
		mainSectionTable.setTotalWidth(tableWidth);

		mainSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);
		mainCell.setPaddingTop(5f);
		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);

		PdfPTable logoSectionTable = getLogoSection(document, textPaySlip);

		PdfPTable payslipDateHeaderSectionTable = getPayslipDateHeaderSection(
				document, textPaySlip);

		PdfPTable employeeInfoSectionTable = getEmployeeInfoSection(document,
				textPaySlip);

		PdfPTable totalIncomeSectionTable = getTotalIncomeSection(document,
				textPaySlip);

		PdfPTable bankInfoSectionTable = getBankInfoSection(document,
				textPaySlip);

		PdfPTable summarySectionTable = getSummarySection(document, textPaySlip);

		mainCell.addElement(logoSectionTable);
		mainCell.addElement(payslipDateHeaderSectionTable);
		mainCell.addElement(employeeInfoSectionTable);
		mainCell.addElement(totalIncomeSectionTable);
		mainCell.addElement(bankInfoSectionTable);
		mainCell.addElement(summarySectionTable);

		mainSectionTable.addCell(mainCell);
		return mainSectionTable;

	}

	public PdfPTable getLogoSection(Document document,
			TextPaySlipDTO textPaySlip) throws MalformedURLException {
		PdfPTable logoSectionTable = new PdfPTable(new float[] { 0.8f, 1.9f,
				0.3f });
		logoSectionTable.setWidthPercentage(110f);

		Image logoImg = getLogoImage();
		PdfPCell nestedLeftCell = new PdfPCell(logoImg);
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPaddingTop(5);
		nestedLeftCell.setBorder(0);

		StringBuilder CompNameStr = new StringBuilder(
				"Malaysia-Standard & Poors Malaysia Sdn Bhd");
		StringBuilder addressStr = new StringBuilder(
				"17-7, The Boulevard, Mid Valley City Lingkaran Syed Putra, Kuala Lumpur, Malaysia - 59200");

		PdfPCell RightCell = new PdfPCell();
		RightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		RightCell.setPaddingLeft(0);
		RightCell.setBorder(0);

		PdfPTable nestedRightCellTable = new PdfPTable(new float[] { 1f });
		logoSectionTable.setWidthPercentage(110f);

		PdfPCell nestedCompNameCell = new PdfPCell(new Phrase(
				CompNameStr.toString(), getBaseFont12Bold()));
		nestedCompNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);

		nestedCompNameCell.setBorder(0);

		PdfPCell nestedCompAddressCell = new PdfPCell(new Phrase(
				addressStr.toString(), getBaseFontNormal()));
		nestedCompAddressCell.setHorizontalAlignment(Element.ALIGN_CENTER);

		nestedCompAddressCell.setBorder(0);

		nestedRightCellTable.addCell(nestedCompNameCell);
		nestedRightCellTable.addCell(nestedCompAddressCell);
		RightCell.addElement(nestedRightCellTable);

		PdfPCell tempCell = new PdfPCell();
		tempCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		tempCell.setPadding(0);
		tempCell.setBorder(0);

		logoSectionTable.addCell(nestedLeftCell);
		logoSectionTable.addCell(RightCell);
		logoSectionTable.addCell(tempCell);
		return logoSectionTable;
	}

	public PdfPTable getPayslipDateHeaderSection(Document document,
			TextPaySlipDTO textPaySlip) throws MalformedURLException {
		PdfPTable SectionTable = new PdfPTable(new float[] { 1 });
		SectionTable.setWidthPercentage(110f);
		SectionTable.setSpacingBefore(5f);

		List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSectionDTOList = textPaySlip
				.getEmpInfoHeaderSection1DTOList();
		int count = 1;
		String dateStr = "";
		for (EmpInfoHeaderTextPayslipDTO value : empInfoHeaderSectionDTOList) {
			if (count == 1) {
				dateStr = value.getKeyConstant3();
			}
			count++;
		}
		dateStr = dateStr.trim();
		dateStr = dateStr.substring(0, 4) + "20"
				+ dateStr.substring(5, dateStr.length());
		StringBuilder tempStr = new StringBuilder("PAYSLIP FOR "
				+ dateStr.toUpperCase());
		PdfPCell nestedCell = new PdfPCell(new Phrase(tempStr.toString(),
				getBaseFont12Bold()));
		nestedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		nestedCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedCell.setBorder(0);

		SectionTable.addCell(nestedCell);
		return SectionTable;
	}

	public PdfPTable getEmployeeInfoSection(Document document,
			TextPaySlipDTO textPaySlip) {
		PdfPTable headerSectionTable = new PdfPTable(new float[] { 1.2f, 0.9f,
				0.9f });
		headerSectionTable.setWidthPercentage(110f);
		headerSectionTable.setSpacingBefore(10f);

		List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSectionDTOList1 = textPaySlip
				.getEmpInfoHeaderSection1DTOList();
		for (EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO : empInfoHeaderSectionDTOList1) {
			getEmployeeInfoPdfCell(document, headerSectionTable,
					empInfoHeaderSectionDTO);
		}

		List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSectionDTOList2 = textPaySlip
				.getEmpInfoHeaderSection2DTOList();
		if (empInfoHeaderSectionDTOList2 != null
				&& !empInfoHeaderSectionDTOList2.isEmpty()) {
			for (EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO : empInfoHeaderSectionDTOList2) {
				getEmployeeInfoPdfFourCell(document, headerSectionTable,
						empInfoHeaderSectionDTO, false);
				getEmployeeInfoPdfFourCell(document, headerSectionTable,
						empInfoHeaderSectionDTO, true);
			}
		}

		return headerSectionTable;

	}

	public void getEmployeeInfoPdfFourCell(Document document,
			PdfPTable headerSectionTable,
			EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO, boolean status) {

		if (status) {
			PdfPCell nestedMiddleCell = new PdfPCell(new Phrase(
					empInfoHeaderSectionDTO.getKeyConstant4(),
					getBaseFontNormal()));
			nestedMiddleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			nestedMiddleCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

			PdfPCell nestedTempCell = new PdfPCell(new Phrase("",
					getBaseFontNormal()));
			nestedTempCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			nestedTempCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

			headerSectionTable.addCell(nestedMiddleCell);
			headerSectionTable.addCell(nestedTempCell);
			headerSectionTable.addCell(nestedTempCell);
		} else {
			PdfPCell nestedLeftCell = new PdfPCell(new Phrase(
					empInfoHeaderSectionDTO.getKeyConstant1(),
					getBaseFontNormal()));
			nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			nestedLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

			PdfPCell nestedMiddleCell = new PdfPCell(new Phrase(
					empInfoHeaderSectionDTO.getKeyConstant2(),
					getBaseFontNormal()));
			nestedMiddleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			nestedMiddleCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

			PdfPCell nestedRightCell = new PdfPCell(new Phrase(
					empInfoHeaderSectionDTO.getKeyConstant3(),
					getBaseFontNormal()));
			nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			nestedRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

			headerSectionTable.addCell(nestedLeftCell);
			headerSectionTable.addCell(nestedMiddleCell);
			headerSectionTable.addCell(nestedRightCell);
		}

	}

	public void getEmployeeInfoPdfCell(Document document,
			PdfPTable headerSectionTable,
			EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO) {
		PdfPCell nestedLeftCell = new PdfPCell(new Phrase(
				empInfoHeaderSectionDTO.getKeyConstant1().trim(),
				getBaseFontNormal()));
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedMiddleCell = new PdfPCell(new Phrase(
				empInfoHeaderSectionDTO.getKeyConstant2(), getBaseFontNormal()));
		nestedMiddleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedMiddleCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedRightCell = new PdfPCell(new Phrase(
				empInfoHeaderSectionDTO.getKeyConstant3(), getBaseFontNormal()));
		nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		headerSectionTable.addCell(nestedLeftCell);
		headerSectionTable.addCell(nestedMiddleCell);
		headerSectionTable.addCell(nestedRightCell);

	}

	public PdfPTable getTotalIncomeSection(Document document,
			TextPaySlipDTO textPaySlip) {
		PdfPTable totalIncomeSectionTable = new PdfPTable(new float[] { 1.3f,
				0.7f, 1.3f, 0.7f });
		totalIncomeSectionTable.setWidthPercentage(110f);
		totalIncomeSectionTable.setSpacingBefore(20f);
		int count = 1;
		List<TotalIncomeSectionTextPayslipDTO> totalIncomeSectionDTOList = textPaySlip
				.getTotalIncomeSectionDTOList();
		for (TotalIncomeSectionTextPayslipDTO totalIncomeSectionDTO : totalIncomeSectionDTOList) {
			if (count == 1) {
				getTotalIncomeSectionHeaderCell(document,
						totalIncomeSectionTable, totalIncomeSectionDTO);
			}
			count++;
			getTotalIncomeSectionPdfCell(document, totalIncomeSectionTable,
					totalIncomeSectionDTO);
		}
		return totalIncomeSectionTable;

	}

	public void getTotalIncomeSectionHeaderCell(Document document,
			PdfPTable totalIncomeSectionTable,
			TotalIncomeSectionTextPayslipDTO totalIncomeSectionDTO) {

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(
				totalIncomeSectionDTO.getKey2().substring(0,
						totalIncomeSectionDTO.getKey2().trim().length() - 1),
				getBaseFont10Bold()));
		nestedLeftKeyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(
				totalIncomeSectionDTO.getKey1(), getBaseFontNormal()));
		nestedLeftValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedRightKeyCell = new PdfPCell(new Phrase(
				totalIncomeSectionDTO.getKey4().substring(0,
						totalIncomeSectionDTO.getKey4().trim().length() - 1),
				getBaseFont10Bold()));
		nestedRightKeyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedRightKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(
				totalIncomeSectionDTO.getKey3(), getBaseFontNormal()));
		nestedRightValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedRightValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		totalIncomeSectionTable.addCell(nestedLeftKeyCell);
		totalIncomeSectionTable.addCell(nestedLeftValueCell);
		totalIncomeSectionTable.addCell(nestedRightKeyCell);
		totalIncomeSectionTable.addCell(nestedRightValueCell);

	}

	public void getTotalIncomeSectionPdfCell(Document document,
			PdfPTable totalIncomeSectionTable,
			TotalIncomeSectionTextPayslipDTO totalIncomeSectionDTO) {
		PdfPCell nestedLeftKeyCell;
		if (totalIncomeSectionDTO.getKey1().trim()
				.equalsIgnoreCase("TOTAL EARNINGS")) {
			nestedLeftKeyCell = new PdfPCell(new Phrase(totalIncomeSectionDTO
					.getKey1().trim(), getBaseFont10Bold()));
		} else {
			nestedLeftKeyCell = new PdfPCell(new Phrase(totalIncomeSectionDTO
					.getKey1().trim(), getBaseFontNormal()));
		}

		nestedLeftKeyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedLeftValueCell = new PdfPCell(new Phrase(
				totalIncomeSectionDTO.getKey2(), getBaseFontNormal()));
		nestedLeftValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedRightKeyCell;
		if (totalIncomeSectionDTO.getKey3().trim()
				.equalsIgnoreCase("TOTAL DEDUCTIONS")) {
			nestedRightKeyCell = new PdfPCell(new Phrase(totalIncomeSectionDTO
					.getKey3().trim(), getBaseFont10Bold()));
		} else {
			nestedRightKeyCell = new PdfPCell(new Phrase(totalIncomeSectionDTO
					.getKey3().trim(), getBaseFontNormal()));
		}

		nestedRightKeyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedRightKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(
				totalIncomeSectionDTO.getKey4(), getBaseFontNormal()));
		nestedRightValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedRightValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		if (StringUtils.isNotBlank(totalIncomeSectionDTO.getKey1())
				|| StringUtils.isNotBlank(totalIncomeSectionDTO.getKey3())) {
			totalIncomeSectionTable.addCell(nestedLeftKeyCell);
			totalIncomeSectionTable.addCell(nestedLeftValueCell);
			totalIncomeSectionTable.addCell(nestedRightKeyCell);
			totalIncomeSectionTable.addCell(nestedRightValueCell);
		}

	}

	public PdfPTable getBankInfoSection(Document document,
			TextPaySlipDTO textPaySlip) {
		PdfPTable bankInfoSectionTable = new PdfPTable(
				new float[] { 1.3f, 0.7f });
		bankInfoSectionTable.setSpacingBefore(20f);
		bankInfoSectionTable.setWidthPercentage(110f);

		List<BankInfoTextPayslipDTO> bankInfoSectionDTOList = textPaySlip
				.getBankInfoSectionDTOList();
		for (BankInfoTextPayslipDTO bankInfoSectionDTO : bankInfoSectionDTOList) {
			PdfPCell nestedLeftCell = new PdfPCell(new Phrase(
					bankInfoSectionDTO.getBankInfo(), getBaseFontNormal()));
			nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			nestedLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

			PdfPCell tempCell = new PdfPCell(
					new Phrase("", getBaseFontNormal()));
			tempCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			tempCell.setPadding(2);
			tempCell.setBorder(0);

			if (StringUtils.isNotBlank(bankInfoSectionDTO.getBankInfo())) {
				bankInfoSectionTable.addCell(nestedLeftCell);
				bankInfoSectionTable.addCell(tempCell);
			}
		}

		return bankInfoSectionTable;

	}

	public PdfPTable getSummarySection(Document document,
			TextPaySlipDTO textPaySlip) {
		PdfPTable summarySectionTable = new PdfPTable(new float[] { 1, 0.8f,
				0.8f, 1.4f });
		summarySectionTable.setWidthPercentage(110f);
		summarySectionTable.setSpacingBefore(20f);

		int count = 1;
		List<BankInfoTextPayslipDTO> bankInfoSectionDTOList = textPaySlip
				.getBankInfoSectionDTOList();
		for (BankInfoTextPayslipDTO bankInfoSectionDTO : bankInfoSectionDTOList) {
			if (count == 1) {
				if (bankInfoSectionDTOList.size() == 1) {
					getFooterInfoHeaderPdfCell(document, summarySectionTable,
							bankInfoSectionDTO);
				}
			} else {
				getFooterInfoHeaderPdfCell(document, summarySectionTable,
						bankInfoSectionDTO);
			}
			count++;
		}

		List<SummarySectionTextPayslipDTO> summarySectionDTOList = textPaySlip
				.getSummarySectionDTOList();
		for (SummarySectionTextPayslipDTO summarySectionDTO : summarySectionDTOList) {
			getFooterInfoPdfCell(document, summarySectionTable,
					summarySectionDTO);
		}

		return summarySectionTable;

	}

	public void getFooterInfoPdfCell(Document document,
			PdfPTable summarySectionTable,
			SummarySectionTextPayslipDTO summarySectionDTO) {
		PdfPCell nestedLeftCell = new PdfPCell(new Phrase(
				summarySectionDTO.getKey(), getBaseFont10Bold()));
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedMiddleCell = new PdfPCell(new Phrase(
				summarySectionDTO.getCurrentValue(), getBaseFontNormal()));
		nestedMiddleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedMiddleCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedRightCell = new PdfPCell(new Phrase(
				summarySectionDTO.getYtdValue(), getBaseFontNormal()));
		nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell tempCell = new PdfPCell(new Phrase("", getBaseFontNormal()));
		tempCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		tempCell.setPadding(2);
		tempCell.setBorder(0);
		if (StringUtils.isNotBlank(summarySectionDTO.getKey())) {
			summarySectionTable.addCell(nestedLeftCell);
			summarySectionTable.addCell(nestedMiddleCell);
			summarySectionTable.addCell(nestedRightCell);
			summarySectionTable.addCell(tempCell);
		}

	}

	public void getFooterInfoHeaderPdfCell(Document document,
			PdfPTable summarySectionTable,
			BankInfoTextPayslipDTO bankInfoSectionDTO) {
		PdfPCell nestedLeftCell = new PdfPCell(new Phrase("",
				getBaseFont10Bold()));
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedMiddleCell = new PdfPCell(new Phrase(
				bankInfoSectionDTO.getCurrentLabel(), getBaseFont10Bold()));
		nestedMiddleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedMiddleCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedRightCell = new PdfPCell(new Phrase(
				bankInfoSectionDTO.getYtdLabel(), getBaseFont10Bold()));
		nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell tempCell = new PdfPCell(new Phrase("", getBaseFontNormal()));
		tempCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		tempCell.setPadding(2);
		tempCell.setBorder(0);

		summarySectionTable.addCell(nestedLeftCell);
		summarySectionTable.addCell(nestedMiddleCell);
		summarySectionTable.addCell(nestedRightCell);
		summarySectionTable.addCell(tempCell);
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
		Font unicodeFont = new Font(bf, 9, Font.NORMAL, BaseColor.DARK_GRAY);
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
