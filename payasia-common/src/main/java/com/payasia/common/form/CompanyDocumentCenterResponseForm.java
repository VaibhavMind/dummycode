package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CompanyDocumentCenterResponseForm extends PageResponse implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

private List<CompanyDocumentCenterForm> rows;

private List<CompanyDocumentCenterForm> xmlList;

public List<CompanyDocumentCenterForm> getXmlList() {
	return xmlList;
}

public void setXmlList(List<CompanyDocumentCenterForm> xmlList) {
	this.xmlList = xmlList;
}

public List<CompanyDocumentCenterForm> getRows() {
	return rows;
}

public void setRows(List<CompanyDocumentCenterForm> rows) {
	this.rows = rows;
}

}
