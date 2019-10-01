package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the Employee_OT_Reviewer database table.
 * 
 */
@Entity
@Table(name="Employee_OT_Reviewer")
public class EmployeeOTReviewer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="Employee_OT_Reviewer_ID")
	private long employeeOTReviewerId;

	 
    @ManyToOne
	@JoinColumn(name="Employee_ID")
	private Employee employee1;

	 
    @ManyToOne
	@JoinColumn(name="Employee_Reviewer_ID")
	private Employee employee2;

	 
    @ManyToOne
	@JoinColumn(name="OT_Template_ID")
	private OTTemplate otTemplate;

	 
    @ManyToOne
	@JoinColumn(name="Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

    public EmployeeOTReviewer() {
    }

	public long getEmployeeOTReviewerId() {
		return this.employeeOTReviewerId;
	}

	public void setEmployeeOTReviewerId(long employeeOTReviewerId) {
		this.employeeOTReviewerId = employeeOTReviewerId;
	}

	public Employee getEmployee1() {
		return this.employee1;
	}

	public void setEmployee1(Employee employee1) {
		this.employee1 = employee1;
	}
	
	public Employee getEmployee2() {
		return this.employee2;
	}

	public void setEmployee2(Employee employee2) {
		this.employee2 = employee2;
	}
	
	public OTTemplate getOtTemplate() {
		return this.otTemplate;
	}

	public void setOtTemplate(OTTemplate otTemplate) {
		this.otTemplate = otTemplate;
	}
	
	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}
	
}