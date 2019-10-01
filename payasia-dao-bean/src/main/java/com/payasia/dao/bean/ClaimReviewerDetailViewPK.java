package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the Claim_Reviewer_Detail_View database view.
 * 
 */
@Embeddable
public class ClaimReviewerDetailViewPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8271558499546638760L;

	@Column(name = "Company_ID")
	private long companyId;

	@Column(name = "Employee_ID")
	private long employeeId;

	@Column(name = "Employee_Claim_Template_ID")
	private long employeeClaimTemplateId;

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}

	public void setEmployeeClaimTemplateId(long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
