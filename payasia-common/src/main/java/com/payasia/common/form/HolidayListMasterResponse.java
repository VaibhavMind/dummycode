package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class HolidayListMasterResponse extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2123801646002057485L;
	private List<HolidayListMasterForm> holidayListMasterForms;

	public List<HolidayListMasterForm> getHolidayListMasterForms() {
		return holidayListMasterForms;
	}

	public void setHolidayListMasterForms(List<HolidayListMasterForm> holidayListMasterForms) {
		this.holidayListMasterForms = holidayListMasterForms;
	}

}
