package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the OT_Reviewer_Detail_View database view.
 * 
 */
@Embeddable
public class OTReviewerDetailViewPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7528049282316313469L;

	@Column(name = "Company_ID")
	private long companyId;

	@Column(name = "Employee_ID")
	private long employeeId;

	@Column(name = "OT_Template_ID")
	private long otTemplateId;

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public long getOtTemplateId() {
		return otTemplateId;
	}

	public void setOtTemplateId(long otTemplateId) {
		this.otTemplateId = otTemplateId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
