package com.payasia.common.form;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
 
/**
 * The Class WorkFlowDelegateForm.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WorkFlowDelegateForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3715174777864430733L;

	/** The config id. */
	private long workFlowDelegateId;

	/** The user. */
	private String user;

	private long userId;

	/** The work flow. */
	private long workFlowTypeId;
	private String workFlowType;
	private Long workFlowTypeValue;

	/** The from date. */
	private String fromDate;

	/** The to date. */
	private String toDate;
	/** The description. */
	private String description;

	/** The delegate to. */
	private String delegateTo;
	private String employeeNumber;
	
	private String employeeName;

	private long delegateToId;
	
	private boolean userCheckForMail ;

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getWorkFlowType() {
		return workFlowType;
	}

	public void setWorkFlowType(String workFlowType) {
		this.workFlowType = workFlowType;
	}

	public Long getWorkFlowTypeValue() {
		return workFlowTypeValue;
	}

	public void setWorkFlowTypeValue(Long workFlowTypeValue) {
		this.workFlowTypeValue = workFlowTypeValue;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getDelegateToId() {
		return delegateToId;
	}

	public void setDelegateToId(long delegateToId) {
		this.delegateToId = delegateToId;
	}

	/**
	 * Gets the delegate to.
	 * 
	 * @return the delegate to
	 */
	public String getDelegateTo() {
		return delegateTo;
	}

	/**
	 * Sets the delegate to.
	 * 
	 * @param delegateTo
	 *            the new delegate to
	 */
	public void setDelegateTo(String delegateTo) {
		this.delegateTo = delegateTo;
	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * 
	 * @param user
	 *            the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the from date.
	 * 
	 * @return the from date
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 * 
	 * @param fromDate
	 *            the new from date
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * Gets the to date.
	 * 
	 * @return the to date
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 * 
	 * @param toDate
	 *            the new to date
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public long getWorkFlowDelegateId() {
		return workFlowDelegateId;
	}

	public void setWorkFlowDelegateId(long workFlowDelegateId) {
		this.workFlowDelegateId = workFlowDelegateId;
	}

	public long getWorkFlowTypeId() {
		return workFlowTypeId;
	}

	public void setWorkFlowTypeId(long workFlowTypeId) {
		this.workFlowTypeId = workFlowTypeId;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public boolean isUserCheckForMail() {
		return userCheckForMail;
	}

	public void setUserCheckForMail(boolean userCheckForMail) {
		this.userCheckForMail = userCheckForMail;
	}

	
}
