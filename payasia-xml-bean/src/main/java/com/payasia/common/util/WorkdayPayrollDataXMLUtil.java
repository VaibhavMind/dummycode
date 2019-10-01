package com.payasia.common.util;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.workday.paydata.EmployeeType;
import com.mind.payasia.xml.bean.workday.paydata.PayrollExtractEmployees;



public class WorkdayPayrollDataXMLUtil {
	
	private static String NAMESPACE_URI = "http://payasia.mind.com/xml/bean/workday/paydata";

	public static Unmarshaller getDocumentUnmarshaller() throws JAXBException,
			SAXException {
		JAXBContext context = getDocumentContext();
		Schema schema = getDocumentSchema();
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		unmarshaller.setEventHandler(new IgnoringValidationEventHandler());
		return unmarshaller;
	}

	public static Marshaller getDocumentMarshaller() throws JAXBException,
			SAXException {
		JAXBContext context = getDocumentContext();
		Schema schema = getDocumentSchema();
		Marshaller marshaller = context.createMarshaller();
		marshaller.setSchema(schema);
		marshaller.setEventHandler(new IgnoringValidationEventHandler());
		return marshaller;
	}

	private static JAXBContext getDocumentContext() throws JAXBException {
		JAXBContext context = JAXBContext
				.newInstance(PayrollExtractEmployees.class);
		return context;
	}
	
	private static Schema getDocumentSchema() throws SAXException {
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(Thread.currentThread()
				.getContextClassLoader().getResource("Workday-PTRX_Data.xsd"));
		return schema;
	}
	
	public static Unmarshaller getEmployeeUnmarshaller() throws JAXBException, SAXException {
		JAXBContext context = JAXBContext.newInstance(EmployeeType.class);
		Schema schema = getDocumentSchema();
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		unmarshaller.setEventHandler(new IgnoringValidationEventHandler());
		return unmarshaller;
	}
	
	private static class XsiTypeReader extends StreamReaderDelegate {
        public XsiTypeReader(XMLStreamReader reader) {
            super(reader);
        }
        @Override
        public String getNamespaceURI() {
        	return NAMESPACE_URI;
        }
    }
	
	private static class XsiTypeReaderWithoutNamespace extends StreamReaderDelegate {
        public XsiTypeReaderWithoutNamespace(XMLStreamReader reader) {
            super(reader);
        }
        @Override
        public String getNamespaceURI() {
        	return "";
        }
    }
	
	private static class IgnoringValidationEventHandler implements ValidationEventHandler {
		@Override
	    public boolean handleEvent(ValidationEvent event) {
	        return true;
	    }
	}
	
	public static StreamReaderDelegate getStreamReaderDelegate(XMLStreamReader reader, boolean isNSRequired) {
		return isNSRequired ? new XsiTypeReader(reader) : new XsiTypeReaderWithoutNamespace(reader);
	}
	
	public static PayrollExtractEmployees getXMLData(byte[] fileByteArray) {
		
		PayrollExtractEmployees xmlData = null;
		try {
			Unmarshaller unmarshaller = getDocumentUnmarshaller();
			XMLInputFactory xif = XMLInputFactory.newInstance();
			XMLStreamReader xsr = xif.createXMLStreamReader(new ByteArrayInputStream(fileByteArray));
			xsr = getStreamReaderDelegate(xsr, true);
			xmlData = (PayrollExtractEmployees)unmarshaller.unmarshal(xsr);
		} catch (JAXBException | SAXException | XMLStreamException e1) {
			e1.printStackTrace();
		}
		return xmlData;
	}
	
	public static String convertEmployeeToXmlString(EmployeeType employeeObj) {
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);
			streamWriter.setPrefix("pi", NAMESPACE_URI);
			
			Marshaller marshaller = WorkdayPayrollDataXMLUtil.getDocumentMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<EmployeeType> emp1 = new JAXBElement<EmployeeType>(new QName(NAMESPACE_URI, "Employee"), EmployeeType.class, employeeObj);
			marshaller.marshal(emp1, streamWriter);
			
		} catch (JAXBException | SAXException | XMLStreamException e) {
			e.printStackTrace();
		}
		return byteArrayOutputStream.toString();
	}
	
	public static JAXBElement<EmployeeType> convertXmlStringToEmployee(String xmlString) {
		
		JAXBElement<EmployeeType> emp = null;
		try {
			Unmarshaller unmarshaller = WorkdayPayrollDataXMLUtil.getEmployeeUnmarshaller();
			XMLInputFactory xif = XMLInputFactory.newInstance();
		//	xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
			XMLStreamReader xsr = xif.createXMLStreamReader(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
			xsr = getStreamReaderDelegate(xsr, true);
			emp = unmarshaller.unmarshal(xsr, EmployeeType.class);
			System.out.println(emp.getValue().getSummary());
			
		} catch (JAXBException | SAXException | XMLStreamException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return emp;
	}	
}

