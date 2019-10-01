package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CoherentOvertimeDetailFormResponse extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3351329795327700764L;
	private List<CoherentOvertimeDetailForm> pendingItemsList;
	public List<CoherentOvertimeDetailForm> getPendingItemsList() {
		return pendingItemsList;
	}
	public void setPendingItemsList(
			List<CoherentOvertimeDetailForm> pendingItemsList) {
		this.pendingItemsList = pendingItemsList;
	}
	
	
	
	
	
}
