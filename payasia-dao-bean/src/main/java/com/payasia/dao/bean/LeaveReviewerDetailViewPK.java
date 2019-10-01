package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the Leave_Reviewer_Detail_View database view.
 * 
 */
@Embeddable
public class LeaveReviewerDetailViewPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4916475643958742626L;

	@Column(name = "Company_ID")
	private long companyId;

	@Column(name = "Employee_ID")
	private long employeeId;

	@Column(name = "Employee_Leave_Scheme_ID")
	private long employeeLeaveSchemeId;

	@Column(name = "Employee_Leave_Scheme_Type_ID")
	private long employeeLeaveSchemeTypeId;

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public long getEmployeeLeaveSchemeId() {
		return employeeLeaveSchemeId;
	}

	public void setEmployeeLeaveSchemeId(long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}

	public long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
