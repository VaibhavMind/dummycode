package com.payasia.test.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.test.logic.beans.TextPaySlipDTO;

public class PDFTemplate {
	private static final Logger LOGGER = Logger.getLogger(PDFTemplate.class);

	public byte[] generatePDF(TextPaySlipDTO textPaySlip) {
		try {
			 
			PDFThreadLocal.pageNumbers.set(false);
			return generateTextPaySlipPDF(textPaySlip);
		} catch (PDFMultiplePageException mpe) {
			 
			 
			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(false);
			return generateTextPaySlipPDF(textPaySlip);
		}
	}

	private byte[] generateTextPaySlipPDF(TextPaySlipDTO textPaySlip) {
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

			PayAsiaTextPaySlipToPdf textPaySlipToPdf = new PayAsiaTextPaySlipToPdf();
			PdfPTable textPayslipTable = textPaySlipToPdf.createTextPaySlipPdf(
					document, 1, textPaySlip);
			document.add(textPayslipTable);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(
					PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow
					| PdfWriter.CenterWindow);

			document.close();

			pdfIn = new FileInputStream(tempFile);

			return IOUtils.toByteArray(pdfIn);
		} catch (DocumentException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {

			LOGGER.error(e.getMessage(), e);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}

			IOUtils.closeQuietly(pdfOut);
			IOUtils.closeQuietly(pdfIn);

			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}

		}
		return null;
	}
}
