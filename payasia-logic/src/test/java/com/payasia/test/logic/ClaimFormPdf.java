package com.payasia.test.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.logic.ClaimFormPrintPDFLogic;

public class ClaimFormPdf {

	@Resource
	ClaimFormPrintPDFLogic claimFormPrintPDFLogic;

	public byte[] generateClaimFormPrintPDF(Long companyId, Long employeeId) {

		try {
			 
			PDFThreadLocal.pageNumbers.set(true);
			try {
				return generateClaimFormPDF(companyId, employeeId);
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {
			 
			 
			 
			PDFThreadLocal.pageNumbers.set(true);
			try {
				return generateClaimFormPDF(companyId, employeeId);
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

	}

	private byte[] generateClaimFormPDF(Long companyId, Long employeeId)
			throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTempFile();
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PayAsiaPDFConstants.PAGE_SIZE,
					PayAsiaPDFConstants.PAGE_LEFT_MARGIN,
					PayAsiaPDFConstants.PAGE_TOP_MARGIN,
					PayAsiaPDFConstants.PAGE_RIGHT_MARGIN,
					PayAsiaPDFConstants.PAGE_BOTTOM_MARGIN);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

			document.open();

			ClaimFormPrintPDFLogicTest claimFormPrintPDFLogicTest = new ClaimFormPrintPDFLogicTest();
			PdfPTable claimReportPdfTable = claimFormPrintPDFLogicTest
					.createClaimReportPdf(document, writer, 1, companyId);
			document.add(claimReportPdfTable);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(
					PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow
					| PdfWriter.CenterWindow);

			document.close();

			pdfIn = new FileInputStream(tempFile);

			return IOUtils.toByteArray(pdfIn);
		} catch (DocumentException ex) {
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} catch (IOException ex) {
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception ex) {
					throw new PayAsiaSystemException(ex.getMessage(), ex);
				}
			}

			IOUtils.closeQuietly(pdfOut);
			IOUtils.closeQuietly(pdfIn);

			try {
				if (tempFile.exists()) {
					 
				}
			} catch (Exception ex) {
				throw new PayAsiaSystemException(ex.getMessage(), ex);
			}

		}
	}
}
