package com.payasia.logic.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
import com.payasia.common.dto.BankInfoTextPayslipDTO;
import com.payasia.common.dto.EmpInfoHeaderTextPayslipDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.SummarySectionTextPayslipDTO;
import com.payasia.common.dto.TextPaySlipDTO;
import com.payasia.common.dto.TotalIncomeSectionTextPayslipDTO;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyLogoDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyLogo;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.PayAsiaTextPaySlipToPdf;

@Component
public class PayAsiaTextPaySlipToPdfImpl extends PdfPageEventHelper implements
		PayAsiaTextPaySlipToPdf {
	private static final Logger LOGGER = Logger
			.getLogger(PayAsiaTextPaySlipToPdfImpl.class);
	@Resource
	CompanyLogoDAO companyLogoDAO;
	@Resource
	CompanyDAO companyDAO;

	@Resource
	FileUtils fileUtils;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Resource
	AWSS3Logic awss3LogicImpl;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Override
	public PdfPTable createTextPaySlipPdf(Document document,
			int currentPageNumber, TextPaySlipDTO textPaySlip,
			DataImportForm dataImportForm, Long companyId,
			CompanyLogo companyLogo) throws MalformedURLException {

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

		PdfPTable logoSectionTable = null;
		PdfPTable payslipDateHeaderSectionTable = null;
		if (dataImportForm.isCompanyAddressBelowLogo()) {
			logoSectionTable = getLogoSectionWithDateHeader(document,
					textPaySlip, dataImportForm, companyId, companyLogo);
			payslipDateHeaderSectionTable = getCompanyAddressBelowLogoSection(
					document, dataImportForm, textPaySlip, companyId);
		} else {
			logoSectionTable = getLogoSection(document, textPaySlip,
					dataImportForm, companyId, companyLogo);

			payslipDateHeaderSectionTable = getPayslipDateHeaderSection(
					document, textPaySlip);
		}

		PdfPTable employeeInfoSectionTable = getEmployeeInfoSection(document,
				textPaySlip, dataImportForm);

		PdfPTable totalIncomeSectionTable = getTotalIncomeSection(document,
				textPaySlip);

		PdfPTable bankInfoSectionTable = getBankInfoSection(document,
				textPaySlip);

		PdfPTable summarySectionTable;
		if (dataImportForm.getPayslipTextFormat().equalsIgnoreCase(
				PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_INDONESIA)) {
			summarySectionTable = getSummarySectionForIndonesia(document,
					textPaySlip);
		} else {
			summarySectionTable = getSummarySection(document, textPaySlip);
		}

		mainCell.addElement(logoSectionTable);
		mainCell.addElement(payslipDateHeaderSectionTable);
		mainCell.addElement(employeeInfoSectionTable);
		mainCell.addElement(totalIncomeSectionTable);
		mainCell.addElement(bankInfoSectionTable);
		mainCell.addElement(summarySectionTable);

		mainSectionTable.addCell(mainCell);
		return mainSectionTable;

	}

	public Image getLogoImage(Long companyId, CompanyLogo companyLogo)
			throws BadElementException, MalformedURLException, IOException {
		Image img;

		if (companyLogo != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogo.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogo.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyLogo.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
							String.valueOf(companyLogo.getCompanyLogoId()),
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);
			byte[] byteFile;
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
					.equalsIgnoreCase(appDeployLocation)) {
				byteFile = IOUtils.toByteArray(awss3LogicImpl
						.readS3ObjectAsStream(filePath));
			} else {
				byteFile = Files.readAllBytes(file.toPath());
			}
			img = Image.getInstance(byteFile);
			img.scaleToFit(102, 50);
			img.setRotationDegrees(0);
		} else {
			URL imageURL = Thread.currentThread().getContextClassLoader()
					.getResource("images/logo.png");

			img = Image.getInstance(imageURL);
			img.scaleToFit(102, 50);
			img.setRotationDegrees(0);
		}

		return img;
	}

	public PdfPTable getLogoSectionWithDateHeader(Document document,
			TextPaySlipDTO textPaySlip, DataImportForm dataImportForm,
			Long companyId, CompanyLogo companyLogo)
			throws MalformedURLException {
		PdfPTable logoSectionTable = new PdfPTable(new float[] { 0.8f, 1.9f,
				0.3f });
		logoSectionTable.setWidthPercentage(110f);

		Image logoImg = null;
		try {
			logoImg = getLogoImage(companyId, companyLogo);
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
		}
		PdfPCell nestedLeftCell = new PdfPCell(logoImg);
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPaddingTop(5);
		nestedLeftCell.setBorder(0);

		PdfPCell RightCell = new PdfPCell();
		RightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		RightCell.setPaddingLeft(0);
		RightCell.setBorder(0);

		PdfPTable nestedRightCellTable = getPayslipDateHeaderSection(document,
				textPaySlip);
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

	public PdfPTable getCompanyAddressBelowLogoSection(Document document,
			DataImportForm dataImportForm, TextPaySlipDTO textPaySlip,
			long companyId) throws MalformedURLException {
		PdfPTable SectionTable = new PdfPTable(new float[] { 1 });
		SectionTable.setWidthPercentage(110f);
		// SectionTable.setSpacingBefore(5f);

		Company companyVO = companyDAO.findById(companyId);
		StringBuilder CompNameStr = new StringBuilder(
				companyVO.getCompanyName());
		StringBuilder addressStr = null;
		try {
			addressStr = new StringBuilder(new String(URLDecoder.decode(
					new String(Base64.decodeBase64(dataImportForm
							.getCompanyAddress().getBytes())), "UTF-8")));
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}

		PdfPCell RightCell = new PdfPCell();
		RightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		RightCell.setPaddingLeft(0);
		RightCell.setBorder(0);

		PdfPTable nestedRightCellTable = new PdfPTable(new float[] { 1f });
		nestedRightCellTable.setWidthPercentage(100f);

		PdfPCell nestedCompNameCell = new PdfPCell(new Phrase(
				CompNameStr.toString(), getBaseFont12Bold()));
		nestedCompNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedCompNameCell.setPaddingLeft(0);
		nestedCompNameCell.setBorder(0);

		PdfPCell nestedCompAddressCell = new PdfPCell(new Phrase(
				addressStr.toString(), getBaseFontNormal()));
		nestedCompAddressCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedCompAddressCell.setPaddingLeft(0);
		nestedCompAddressCell.setBorder(0);

		nestedRightCellTable.addCell(nestedCompNameCell);
		nestedRightCellTable.addCell(nestedCompAddressCell);
		RightCell.addElement(nestedRightCellTable);

		SectionTable.addCell(RightCell);
		return SectionTable;
	}

	public PdfPTable getLogoSection(Document document,
			TextPaySlipDTO textPaySlip, DataImportForm dataImportForm,
			Long companyId, CompanyLogo companyLogo)
			throws MalformedURLException {
		PdfPTable logoSectionTable = new PdfPTable(new float[] { 0.4f, 2.3f,
				0.4f });
		logoSectionTable.setWidthPercentage(110f);

		Image logoImg = null;
		try {
			logoImg = getLogoImage(companyId, companyLogo);
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
		}
		PdfPCell nestedLeftCell = new PdfPCell(logoImg);
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPaddingTop(5);
		nestedLeftCell.setBorder(0);

		Company companyVO = companyDAO.findById(companyId);
		StringBuilder CompNameStr = new StringBuilder(
				companyVO.getCompanyName());
		StringBuilder addressStr = null;
		try {
			addressStr = new StringBuilder(new String(URLDecoder.decode(
					new String(Base64.decodeBase64(dataImportForm
							.getCompanyAddress().getBytes())), "UTF-8")));
		} catch (UnsupportedEncodingException e) {

			LOGGER.error(e.getMessage(), e);
		}

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
		// dateStr = dateStr.substring(0, 4) + "20" + dateStr.substring(4, 6);

		String[] strArr = dateStr.split(" ");
		String dateStrNew = "";
		int yearCount = 0;
		for (int counter = 0; counter < strArr.length; counter++) {
			if (counter == 0) {
				dateStrNew += strArr[counter].trim();
			} else {
				if (StringUtils.isNotBlank(strArr[counter])) {
					if (yearCount == 0) {
						dateStrNew += " 20" + strArr[counter].trim();
						yearCount++;
					}
				}
			}

		}
		dateStr = dateStrNew;
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
			TextPaySlipDTO textPaySlip, DataImportForm dataImportForm) {
		PdfPTable headerSectionTable = new PdfPTable(new float[] { 1.2f, 0.9f,
				0.9f });
		headerSectionTable.setWidthPercentage(110f);
		headerSectionTable.setSpacingBefore(10f);

		if (dataImportForm.getPayslipTextFormat().equalsIgnoreCase(
				PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_INDONESIA)) {
			List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSectionDTOList1 = textPaySlip
					.getEmpInfoHeaderSection1DTOList();
			int tcount = 1;
			for (EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO : empInfoHeaderSectionDTOList1) {
				getEmployeeInfoPdfCell(document, headerSectionTable,
						empInfoHeaderSectionDTO, tcount);
				tcount++;
			}

			List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSectionDTOList2 = textPaySlip
					.getEmpInfoHeaderSection2DTOList();
			if (empInfoHeaderSectionDTOList2 != null
					&& !empInfoHeaderSectionDTOList2.isEmpty()) {
				for (EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO : empInfoHeaderSectionDTOList2) {
					getEmployeeInfoPdfCell(document, headerSectionTable,
							empInfoHeaderSectionDTO, tcount);
				}
			}
		} else {
			List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSectionDTOList1 = textPaySlip
					.getEmpInfoHeaderSection1DTOList();
			int tcount = 1;
			for (EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO : empInfoHeaderSectionDTOList1) {
				getEmployeeInfoPdfCell(document, headerSectionTable,
						empInfoHeaderSectionDTO, tcount);
				tcount++;
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
			if (StringUtils.isNotBlank(empInfoHeaderSectionDTO
					.getKeyConstant4())) {
				headerSectionTable.addCell(nestedMiddleCell);
				headerSectionTable.addCell(nestedTempCell);
				headerSectionTable.addCell(nestedTempCell);
			}

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
			EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO, int tcount) {
		PdfPCell nestedLeftCell = new PdfPCell(new Phrase(
				empInfoHeaderSectionDTO.getKeyConstant1().trim(),
				getBaseFontNormal()));
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		String keyConst2 = "";
		if (StringUtils.isNotBlank(empInfoHeaderSectionDTO.getKeyConstant2())) {
			keyConst2 = empInfoHeaderSectionDTO.getKeyConstant2();
		}
		PdfPCell nestedMiddleCell = new PdfPCell(new Phrase(keyConst2,
				getBaseFontNormal()));
		nestedMiddleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedMiddleCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		String col3ValStr = empInfoHeaderSectionDTO.getKeyConstant3();

		PdfPCell nestedRightCell = new PdfPCell(new Phrase(col3ValStr,
				getBaseFontNormal()));
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
		if (totalIncomeSectionDTO.getKey1().trim().contains("TOTAL EARNINGS")) {
			nestedLeftKeyCell = new PdfPCell(new Phrase("TOTAL EARNINGS",
					getBaseFont10Bold()));
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
		PdfPTable bankInfoSectionTable = new PdfPTable(new float[] { 1f, 1f });
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

			// Add Net Pay of Indonasian Payslip
			PdfPCell nestedRightCell;

			if (StringUtils.isNotBlank(bankInfoSectionDTO.getBankInfo())) {
				bankInfoSectionTable.addCell(nestedLeftCell);
				if (StringUtils.isNotBlank(bankInfoSectionDTO.getNetPay())) {
					nestedRightCell = new PdfPCell(new Phrase(
							bankInfoSectionDTO.getNetPay().trim(),
							getBaseFontNormal()));
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell
							.setPadding(PayAsiaPDFConstants.CELL_PADDING);
					bankInfoSectionTable.addCell(nestedRightCell);
				} else {
					bankInfoSectionTable.addCell(tempCell);
				}
			} else {
				if (StringUtils.isNotBlank(bankInfoSectionDTO.getNetPay())) {
					nestedRightCell = new PdfPCell(new Phrase(
							bankInfoSectionDTO.getNetPay().trim(),
							getBaseFontNormal()));
					nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					nestedRightCell
							.setPadding(PayAsiaPDFConstants.CELL_PADDING);
					bankInfoSectionTable.addCell(nestedRightCell);
				} else {
					bankInfoSectionTable.addCell(tempCell);
				}
				bankInfoSectionTable.addCell(tempCell);
			}

			// Add Net Pay of Indonasian Payslip
			// PdfPCell nestedRightCell;
			// if (StringUtils.isNotBlank(bankInfoSectionDTO.getNetPay())) {
			// nestedRightCell = new PdfPCell(new Phrase(bankInfoSectionDTO
			// .getNetPay().trim(), getBaseFontNormal()));
			// nestedRightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			// nestedRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
			//
			// if (StringUtils.isNotBlank(bankInfoSectionDTO.getNetPay())) {
			// bankInfoSectionTable.addCell(nestedRightCell);
			// bankInfoSectionTable.addCell(tempCell);
			// }
			// }
		}

		return bankInfoSectionTable;

	}

	public PdfPTable getSummarySection(Document document,
			TextPaySlipDTO textPaySlip) {
		PdfPTable summarySectionTable = new PdfPTable(new float[] { 1, 0.8f,
				0.8f, 1.4f });
		summarySectionTable.setWidthPercentage(110f);
		summarySectionTable.setSpacingBefore(20f);

		// int count = 1;
		List<BankInfoTextPayslipDTO> bankInfoSectionDTOList = textPaySlip
				.getBankInfoSectionDTOList();
		for (BankInfoTextPayslipDTO bankInfoSectionDTO : bankInfoSectionDTOList) {
			// if (count == 1) {
			// if (bankInfoSectionDTOList.size() == 1) {
			// getFooterInfoHeaderPdfCell(document, summarySectionTable,
			// bankInfoSectionDTO);
			// }
			// } else {
			SummarySectionTextPayslipDTO summarySectionDTO = new SummarySectionTextPayslipDTO();
			summarySectionDTO.setKey(bankInfoSectionDTO.getKey());
			summarySectionDTO.setCurrentValue(bankInfoSectionDTO
					.getCurrentLabel());
			summarySectionDTO.setYtdValue(bankInfoSectionDTO.getYtdLabel());
			if (StringUtils.isBlank(bankInfoSectionDTO.getKey())
					&& StringUtils.isNotBlank(bankInfoSectionDTO
							.getCurrentLabel())
					&& StringUtils.isNotBlank(bankInfoSectionDTO.getYtdLabel())) {
				getFooterInfoHeaderPdfCell(document, summarySectionTable,
						bankInfoSectionDTO);
			}
			if (StringUtils.isNotBlank(bankInfoSectionDTO.getKey())
					&& StringUtils.isNotBlank(bankInfoSectionDTO
							.getCurrentLabel())
					&& StringUtils.isNotBlank(bankInfoSectionDTO.getYtdLabel())) {
				getFooterInfoPdfCell(document, summarySectionTable,
						summarySectionDTO);
			}

			// }
			// count++;
		}

		List<SummarySectionTextPayslipDTO> summarySectionDTOList = textPaySlip
				.getSummarySectionDTOList();
		for (SummarySectionTextPayslipDTO summarySectionDTO : summarySectionDTOList) {
			getFooterInfoPdfCell(document, summarySectionTable,
					summarySectionDTO);
		}

		return summarySectionTable;

	}

	public PdfPTable getSummarySectionForIndonesia(Document document,
			TextPaySlipDTO textPaySlip) {
		PdfPTable summarySectionTable = new PdfPTable(new float[] { 1, 0.8f,
				0.8f, 0.8f });
		summarySectionTable.setWidthPercentage(110f);
		summarySectionTable.setSpacingBefore(20f);

		int count = 1;
		List<SummarySectionTextPayslipDTO> summarySectionDTO1List = textPaySlip
				.getSummarySectionDTOList();
		for (SummarySectionTextPayslipDTO summarySectionDTO : summarySectionDTO1List) {
			if (count == 1) {
				BankInfoTextPayslipDTO currentYTDDTO = new BankInfoTextPayslipDTO();
				currentYTDDTO.setCurrentLabel(summarySectionDTO
						.getCurrentValue());
				currentYTDDTO.setYtdLabel(summarySectionDTO.getYtdValue());

				getFooterInfoHeaderPdfCell(document, summarySectionTable,
						currentYTDDTO);
			} else {
				getFooterInfoPdfCell(document, summarySectionTable,
						summarySectionDTO);
			}
			count++;
		}
		// Blank Column
		getFooterInfoBlankFourPdfCell(document, summarySectionTable);
		getFooterInfoBlankFourPdfCell(document, summarySectionTable);
		int countS = 1;
		List<SummarySectionTextPayslipDTO> summarySectionDTO4ColList = textPaySlip
				.getSummaryLeftFourColSectionDTOList();
		for (SummarySectionTextPayslipDTO summarySectionDTO : summarySectionDTO4ColList) {
			if (countS == 1) {
				getFooterInfoThreeColHeaderPdfCell(document,
						summarySectionTable, summarySectionDTO);
			} else {
				getFooterInfoPdfFourCell(document, summarySectionTable,
						summarySectionDTO);
			}
			countS++;
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

	public void getFooterInfoPdfFourCell(Document document,
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

		PdfPCell tempCell = new PdfPCell(new Phrase(
				summarySectionDTO.getCol4(), getBaseFontNormal()));
		tempCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		tempCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		tempCell.setPadding(2);
		// tempCell.setBorder(0);
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

		if (StringUtils.isNotBlank(bankInfoSectionDTO.getCurrentLabel())
				&& StringUtils.isNotBlank(bankInfoSectionDTO.getYtdLabel())) {
			summarySectionTable.addCell(nestedLeftCell);
			summarySectionTable.addCell(nestedMiddleCell);
			summarySectionTable.addCell(nestedRightCell);
			summarySectionTable.addCell(tempCell);
		}

	}

	public void getFooterInfoThreeColHeaderPdfCell(Document document,
			PdfPTable summarySectionTable,
			SummarySectionTextPayslipDTO summarySectionDTO) {
		PdfPCell nestedLeftCell = new PdfPCell(new Phrase("",
				getBaseFont10Bold()));
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedMiddleCell = new PdfPCell(new Phrase(
				summarySectionDTO.getCurrentValue(), getBaseFont10Bold()));
		nestedMiddleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedMiddleCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell nestedRightCell = new PdfPCell(new Phrase(
				summarySectionDTO.getYtdValue(), getBaseFont10Bold()));
		nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell col4Cell = new PdfPCell(new Phrase(
				summarySectionDTO.getCol4(), getBaseFont10Bold()));
		col4Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		col4Cell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		col4Cell.setPadding(2);
		// col4Cell.setBorder(0);

		if (StringUtils.isNotBlank(summarySectionDTO.getCurrentValue())
				&& StringUtils.isNotBlank(summarySectionDTO.getYtdValue())
				&& StringUtils.isNotBlank(summarySectionDTO.getCol4())) {
			summarySectionTable.addCell(nestedLeftCell);
			summarySectionTable.addCell(nestedMiddleCell);
			summarySectionTable.addCell(nestedRightCell);
			summarySectionTable.addCell(col4Cell);
		}

	}

	public void getFooterInfoBlankFourPdfCell(Document document,
			PdfPTable summarySectionTable) {
		PdfPCell nestedLeftCell = new PdfPCell(new Phrase("",
				getBaseFont10Bold()));
		nestedLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		nestedLeftCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftCell.setBorder(0);

		PdfPCell nestedMiddleCell = new PdfPCell(new Phrase("",
				getBaseFont10Bold()));
		nestedMiddleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedMiddleCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleCell.setBorder(0);

		PdfPCell nestedRightCell = new PdfPCell(new Phrase("",
				getBaseFont10Bold()));
		nestedRightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		nestedRightCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightCell.setBorder(0);

		PdfPCell col4Cell = new PdfPCell(new Phrase("", getBaseFont10Bold()));
		col4Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		col4Cell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		col4Cell.setPadding(2);
		col4Cell.setBorder(0);

		summarySectionTable.addCell(nestedLeftCell);
		summarySectionTable.addCell(nestedMiddleCell);
		summarySectionTable.addCell(nestedRightCell);
		summarySectionTable.addCell(col4Cell);
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
