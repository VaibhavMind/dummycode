package com.payasia.test.logic;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.itextpdf.text.DocumentException;
import com.payasia.logic.TaxDocumentLogic;

public class TaxDocumentLogicTest extends AbstractTestCase {

	private static final Logger LOGGER = Logger
			.getLogger(TaxDocumentLogicTest.class);

	@Resource
	TaxDocumentLogic taxDocumentLogic;

	@Test
	public void getPdf() throws DocumentException, IOException {

		 
		taxDocumentLogic.getPdf();
	}

}
