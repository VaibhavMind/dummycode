package com.payasia.logic.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.TextPaySlipListDTO;
import com.payasia.logic.TextPayslipUtils;

@Component
public class TextPayslipUtilsImpl implements TextPayslipUtils {
	private static final Logger LOGGER = Logger
			.getLogger(TextPayslipUtilsImpl.class);

	private StreamFactory factory;

	protected StreamFactory newStreamFactory(String config) throws IOException {
		StreamFactory factory = StreamFactory.newInstance();
		loadMappingFile(factory, config);
		return factory;
	}

	protected void loadMappingFile(StreamFactory factory, String config)
			throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(config);
		try {
			factory.load(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@Override
	public TextPaySlipListDTO readMalaysiaTextPayslip(
			CommonsMultipartFile txtFile) throws ParseException,
			FileNotFoundException, IOException {
		factory = newStreamFactory("textPayslipXml/MalayasiaTextPaySlipParser.xml");
		BeanReader in = factory.createReader("mpp", new InputStreamReader(
				txtFile.getInputStream()));
		TextPaySlipListDTO textPaySlipDTO;
		try {

			textPaySlipDTO = (TextPaySlipListDTO) in.read();

		} finally {
			in.close();
		}
		return textPaySlipDTO;
	}

	@Override
	public TextPaySlipListDTO readHongKongTextPayslip(
			CommonsMultipartFile txtFile) throws ParseException,
			FileNotFoundException, IOException {
		factory = newStreamFactory("textPayslipXml/HongKongTextPaySlipParser.xml");
		BeanReader in = factory.createReader("hpp", new InputStreamReader(
				txtFile.getInputStream()));
		TextPaySlipListDTO textPaySlipDTO;
		try {

			textPaySlipDTO = (TextPaySlipListDTO) in.read();

		} finally {
			in.close();
		}
		return textPaySlipDTO;
	}

	@Override
	public TextPaySlipListDTO readSingaporeTextPayslip(
			CommonsMultipartFile txtFile) throws ParseException,
			FileNotFoundException, IOException {
		factory = newStreamFactory("textPayslipXml/SingaporeTextPaySlipParser.xml");
		BeanReader in = factory.createReader("spp", new InputStreamReader(
				txtFile.getInputStream()));
		TextPaySlipListDTO textPaySlipDTO;
		try {

			textPaySlipDTO = (TextPaySlipListDTO) in.read();

		} finally {
			in.close();
		}
		return textPaySlipDTO;
	}

	@Override
	public TextPaySlipListDTO readIndonesiaTextPayslip(
			CommonsMultipartFile txtFile) throws ParseException,
			FileNotFoundException, IOException {
		factory = newStreamFactory("textPayslipXml/IndonesianTextPaySlipParser.xml");
		BeanReader in = factory.createReader("ipp", new InputStreamReader(
				txtFile.getInputStream()));
		TextPaySlipListDTO textPaySlipDTO;
		try {

			textPaySlipDTO = (TextPaySlipListDTO) in.read();

		} finally {
			in.close();
		}
		return textPaySlipDTO;
	}

	@Override
	public TextPaySlipListDTO readSingaporeTextPayslipPaymentModeCash(
			CommonsMultipartFile txtFile) throws ParseException,
			FileNotFoundException, IOException {
		factory = newStreamFactory("textPayslipXml/SingaporeTextPaySlipPaymentModeCashParser.xml");
		BeanReader in = factory.createReader("spppc", new InputStreamReader(
				txtFile.getInputStream()));
		TextPaySlipListDTO textPaySlipDTO;
		try {

			textPaySlipDTO = (TextPaySlipListDTO) in.read();

		} finally {
			in.close();
		}
		return textPaySlipDTO;
	}
}
