package com.payasia.test.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.itextpdf.text.DocumentException;

public class claimFormPDFLogicTest extends AbstractTestCase {

	private static final Logger LOGGER = Logger
			.getLogger(claimFormPDFLogicTest.class);

	@Test
	public void generatePaySlipPDF() throws DocumentException, IOException {
		ClaimFormPdf claimFormPdf = new ClaimFormPdf();
		 
		Long companyId = 2l;
		Long employeeId = 1l;
		try {
			byte[] byteArray = claimFormPdf.generateClaimFormPrintPDF(
					companyId, employeeId);
			File pdfFile = new File("C:/temp/claim_form_PDF.pdf");
			if (!pdfFile.exists()) {
				pdfFile.createNewFile();
			}
			IOUtils.write(byteArray, new FileOutputStream(pdfFile));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

}
