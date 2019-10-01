package com.payasia.logic.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.payasia.logic.TaxDocumentLogic;

@Component
public class TaxDocumentLogicImpl implements TaxDocumentLogic {
	private static final Logger LOGGER = Logger
			.getLogger(TaxDocumentLogicImpl.class);

	@Override
	public PdfDocument getPdf() {
		Document document = new Document();

		PdfWriter writer = null;
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(
					"C:/temp/PApdftest.pdf"));
		} catch (FileNotFoundException e1) {
			LOGGER.error(e1.getMessage(), e1);
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		document.open();
		PdfReader reader;
		try {

			reader = new PdfReader(
					"D:/DevWorkspaces/PayasiaPortalNew/payasia/payasia-parent/payasia-logic/src/main/resources/pdf/PApdf.pdf");
			int n = reader.getNumberOfPages();
			int fromPage = 1;
			int toPage = 4;
			boolean status = false;
			String textToSearch = "Warehouse";

			PdfContentByte cb = writer.getDirectContent();  
			PdfImportedPage page;

			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			TextExtractionStrategy strategy;
			for (int count = 1; count <= reader.getNumberOfPages(); count++) {
				strategy = parser.processContent(count,
						new SimpleTextExtractionStrategy());
				Scanner scanner = new Scanner(strategy.getResultantText());

				while (scanner.hasNextLine()) {
					String result = scanner.findInLine(textToSearch);
					if (result != null) {
						fromPage = count;
						status = true;
					}
					scanner.nextLine();
				}
				if (status) {
					document.newPage();
					page = writer.getImportedPage(reader, fromPage);
					cb.addTemplate(page, 0, 0);
				}
				status = false;

			}

			document.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return null;
	}
}
