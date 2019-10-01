package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the LundinOTReviewerDetailViewPK database view.
 * 
 */
@Embeddable
public class LundinOTReviewerDetailViewPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4916475643958742626L;

	@Column(name = "Company_ID")
	private long companyId;

	@Column(name = "Employee_ID")
	private long employeeId;

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
