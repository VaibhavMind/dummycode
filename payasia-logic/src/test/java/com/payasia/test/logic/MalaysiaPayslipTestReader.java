/*
 * Copyright 2012 Kevin Seim
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http: 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.payasia.test.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import org.junit.Test;

import com.payasia.test.logic.beans.TextPaySlipDTO;
import com.payasia.test.logic.beans.TextPaySlipListDTO;

/**
 * JUnit test cases for testing bean objects mapped to a group.
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class MalaysiaPayslipTestReader extends ParserTest {

	private StreamFactory factory;

	@Test
	public void testMalaysiaGroup() throws ParseException,
			FileNotFoundException, IOException {
		factory = newStreamFactory("MalayasiaTextPaySlipParser.xml");
		BeanReader in = factory.createReader("mpp", new InputStreamReader(
				getClass().getResourceAsStream("MalaysiaTextPayslip.txt")));
		int count = 1;
		try {
			 
			TextPaySlipListDTO textPaySlipDTO = (TextPaySlipListDTO) in.read();

			List<TextPaySlipDTO> textPaySlipList = textPaySlipDTO
					.getTextPaySlipDTOList();
			for (TextPaySlipDTO textPaySlip : textPaySlipList) {
				 
				PDFTemplate pdfTemplate = new PDFTemplate();
				byte[] byteArray = pdfTemplate.generatePDF(textPaySlip);
				File pdfFile = new File("C:/temp/Malaysia_singlePage" + count
						+ ".pdf");
				count++;
				if (!pdfFile.exists()) {
					pdfFile.createNewFile();
				}

				IOUtils.write(byteArray, new FileOutputStream(pdfFile));
			}

		} finally {
			in.close();
		}
	}

}
