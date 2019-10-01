package com.payasia.common.util;



import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.mind.payasia.xml.bean.Tab;


public class XMLUtil {
	
	private static SAXParserFactory getParserFactory() throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		return spf;
	}

	public static Unmarshaller getDocumentUnmarshaller() throws JAXBException,
			SAXException {
		JAXBContext context = getDocumentContext();
		Schema schema = getDocumentSchema();

		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		return unmarshaller;
	}

	public static Marshaller getDocumentMarshaller() throws JAXBException,
			SAXException {
		JAXBContext context = getDocumentContext();
		Schema schema = getDocumentSchema();

		Marshaller marshaller = context.createMarshaller();
		marshaller.setSchema(schema);
		return marshaller;
	}

	private static Schema getDocumentSchema() throws SAXException {
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(Thread.currentThread()
				.getContextClassLoader().getResource("Dynamic-form.xsd"));
		return schema;
	}

	private static JAXBContext getDocumentContext() throws JAXBException {
		JAXBContext context = JAXBContext
				.newInstance(Tab.class);
		return context;
	}

	public static Source getSAXSource(final StringReader xmlReader) throws ParserConfigurationException, SAXException {
		SAXParserFactory spf = getParserFactory();
		SAXSource xmlSource  = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(xmlReader));
		StringReader reader = (StringReader) xmlSource.getInputSource().getCharacterStream();
		return new StreamSource(reader);
	}
}

