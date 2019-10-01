package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CoherentShiftDetailFormResponse extends PageResponse implements Serializable{
	private static final long serialVersionUID = 3351329795327700764L;
	private List<CoherentShiftDetailForm> pendingItemsList;
	public List<CoherentShiftDetailForm> getPendingItemsList() {
		return pendingItemsList;
	}
	public void setPendingItemsList(List<CoherentShiftDetailForm> pendingItemsList) {
		this.pendingItemsList = pendingItemsList;
	}
}
