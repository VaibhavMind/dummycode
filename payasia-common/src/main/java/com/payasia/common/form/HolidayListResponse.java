package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class HolidayListResponse.
 */
public class HolidayListResponse extends PageResponse implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2890478451723294304L;
	/** The holiday list form. */
	private List<HolidayListForm> holidayListForm;
	private List<CompanyHolidayCalendarForm> companyHolidayCalendarForm;

	/**
	 * Gets the holiday list form.
	 *
	 * @return the holiday list form
	 */
	public List<HolidayListForm> getHolidayListForm() {
		return holidayListForm;
	}

	/**
	 * Sets the holiday list form.
	 *
	 * @param holidayListForm the new holiday list form
	 */
	public void setHolidayListForm(List<HolidayListForm> holidayListForm) {
		this.holidayListForm = holidayListForm;
	}

	public List<CompanyHolidayCalendarForm> getCompanyHolidayCalendarForm() {
		return companyHolidayCalendarForm;
	}

	public void setCompanyHolidayCalendarForm(
			List<CompanyHolidayCalendarForm> companyHolidayCalendarForm) {
		this.companyHolidayCalendarForm = companyHolidayCalendarForm;
	}

}
