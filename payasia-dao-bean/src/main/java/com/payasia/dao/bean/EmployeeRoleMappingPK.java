package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the Employee_Role_Mapping database table.
 * 
 */
@Embeddable
public class EmployeeRoleMappingPK implements Serializable {
	 
	private static final long serialVersionUID = 1L;

	@Column(name = "Employee_ID")
	private long employeeId;

	@Column(name = "Company_ID")
	private long companyId;

	@Column(name = "Role_ID")
	private long roleId;

	public EmployeeRoleMappingPK() {
	}

	public long getEmployeeId() {
		return this.employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public long getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EmployeeRoleMappingPK)) {
			return false;
		}
		EmployeeRoleMappingPK castOther = (EmployeeRoleMappingPK) other;
		return (this.employeeId == castOther.employeeId)
				&& (this.companyId == castOther.companyId)
				&& (this.roleId == castOther.roleId);

	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime
				+ ((int) (this.employeeId ^ (this.employeeId >>> 32)));
		hash = hash * prime
				+ ((int) (this.companyId ^ (this.companyId >>> 32)));
		hash = hash * prime + ((int) (this.roleId ^ (this.roleId >>> 32)));

		return hash;
	}
}