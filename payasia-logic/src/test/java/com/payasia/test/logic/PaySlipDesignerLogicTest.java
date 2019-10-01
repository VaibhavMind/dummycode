package com.payasia.test.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.itextpdf.text.DocumentException;
import com.payasia.logic.PaySlipDesignerLogic;

public class PaySlipDesignerLogicTest extends AbstractTestCase {

	private static final Logger LOGGER = Logger
			.getLogger(PaySlipDesignerLogicTest.class);

	@Resource
	PaySlipDesignerLogic paySlipDesignerLogic;

	@Test
	public void generatePaySlipPDF() throws DocumentException, IOException {

		Long companyId = 2l;
		Long employeeId = 1l;
		try {
			byte[] byteArray = paySlipDesignerLogic.generatePdf(companyId,
					employeeId);
			File pdfFile = new File("C:/temp/PA_PDF.pdf");
			if (!pdfFile.exists()) {
				pdfFile.createNewFile();
			}
			IOUtils.write(byteArray, new FileOutputStream(pdfFile));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

}
