package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class DynamicTabPrivilgeFormResponse implements Serializable{

	private static final long serialVersionUID = -8715740630750674394L;
	
	private List<DynamicTabPrivilgeForm> dynamicTabPrivilgeForms;
	public List<DynamicTabPrivilgeForm> getDynamicTabPrivilgeForms() {
		return dynamicTabPrivilgeForms;
	}
	public void setDynamicTabPrivilgeForms(List<DynamicTabPrivilgeForm> dynamicTabPrivilgeForms) {
		this.dynamicTabPrivilgeForms = dynamicTabPrivilgeForms;
	}
}
