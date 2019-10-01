package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveDTO implements Serializable{
	
	private BigDecimal leaveBalance;
	private String fromDate;
	private String toDate;
	private Long session1;
	private Long session2;
	private Long employeeLeaveSchemeTypeId;
	private BigDecimal days;
	private Integer errorCode;
	private String errorKey;
	private String errorValue;
	private Boolean status;
	

	public String getErrorValue() {
		return errorValue;
	}

	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}

	public BigDecimal getDays() {
		return days;
	}

	public void setDays(BigDecimal days) {
		this.days = days;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	public Long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(Long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Long getSession1() {
		return session1;
	}

	public void setSession1(Long session1) {
		this.session1 = session1;
	}

	public Long getSession2() {
		return session2;
	}

	public void setSession2(Long session2) {
		this.session2 = session2;
	}

	public BigDecimal getLeaveBalance() {
		return leaveBalance;
	}

	public void setLeaveBalance(BigDecimal leaveBalance) {
		this.leaveBalance = leaveBalance;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((days == null) ? 0 : days.hashCode());
		result = prime * result + ((employeeLeaveSchemeTypeId == null) ? 0 : employeeLeaveSchemeTypeId.hashCode());
		result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result + ((errorKey == null) ? 0 : errorKey.hashCode());
		result = prime * result + ((errorValue == null) ? 0 : errorValue.hashCode());
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((leaveBalance == null) ? 0 : leaveBalance.hashCode());
		result = prime * result + ((session1 == null) ? 0 : session1.hashCode());
		result = prime * result + ((session2 == null) ? 0 : session2.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LeaveDTO other = (LeaveDTO) obj;
		if (days == null) {
			if (other.days != null)
				return false;
		} else if (!days.equals(other.days))
			return false;
		if (employeeLeaveSchemeTypeId == null) {
			if (other.employeeLeaveSchemeTypeId != null)
				return false;
		} else if (!employeeLeaveSchemeTypeId.equals(other.employeeLeaveSchemeTypeId))
			return false;
		if (errorCode == null) {
			if (other.errorCode != null)
				return false;
		} else if (!errorCode.equals(other.errorCode))
			return false;
		if (errorKey == null) {
			if (other.errorKey != null)
				return false;
		} else if (!errorKey.equals(other.errorKey))
			return false;
		if (errorValue == null) {
			if (other.errorValue != null)
				return false;
		} else if (!errorValue.equals(other.errorValue))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (leaveBalance == null) {
			if (other.leaveBalance != null)
				return false;
		} else if (!leaveBalance.equals(other.leaveBalance))
			return false;
		if (session1 == null) {
			if (other.session1 != null)
				return false;
		} else if (!session1.equals(other.session1))
			return false;
		if (session2 == null) {
			if (other.session2 != null)
				return false;
		} else if (!session2.equals(other.session2))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (toDate == null) {
			if (other.toDate != null)
				return false;
		} else if (!toDate.equals(other.toDate))
			return false;
		return true;
	}

	

	
	
	


	

}
