package com.payasia.common.util;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.CalendarTemplate;

public class CalendarTemplateXMLUtil {

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
				.getContextClassLoader().getResource("Calendar-Template.xsd"));
		return schema;
	}
	
	private static JAXBContext getDocumentContext() throws JAXBException{
		JAXBContext context = JAXBContext
				.newInstance(CalendarTemplate.class);
		return context;
	}
}
