package com.payasia.common.util;

import java.io.StringReader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import com.payasia.common.exception.PayAsiaSystemException;

public class HRISUtils {
	
	public static Object unmarshal(String data) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jaxbException) {
			throw new PayAsiaSystemException(jaxbException.getMessage(), jaxbException);
		} catch (SAXException saxException) {
			throw new PayAsiaSystemException(saxException.getMessage(), saxException);
		}
		
		
		final StringReader xmlReader = new StringReader(data);
		final StreamSource xmlSource = new StreamSource(xmlReader);

		try {
			return (Object) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			throw new PayAsiaSystemException(jaxbException.getMessage(), jaxbException);
		} 
	}

}
