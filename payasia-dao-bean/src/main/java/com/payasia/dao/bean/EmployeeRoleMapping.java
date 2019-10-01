package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Role_Mapping database table.
 * 
 */
@Entity
@Table(name = "Employee_Role_Mapping")
public class EmployeeRoleMapping extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@EmbeddedId
	private EmployeeRoleMappingPK id;

	 
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "Company_ID", insertable = false, updatable = false)
	private Company company;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Employee_ID", insertable = false, updatable = false)
	private Employee employee;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Role_ID", insertable = false, updatable = false)
	private RoleMaster roleMaster;

	 
	@OneToMany(mappedBy = "employeeRoleMapping")
	private Set<CompanyEmployeeShortList> companyEmployeeShortLists;

	public EmployeeRoleMapping() {
	}

	public EmployeeRoleMappingPK getId() {
		return this.id;
	}

	public void setId(EmployeeRoleMappingPK id) {
		this.id = id;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public RoleMaster getRoleMaster() {
		return this.roleMaster;
	}

	public void setRoleMaster(RoleMaster roleMaster) {
		this.roleMaster = roleMaster;
	}

	public Set<CompanyEmployeeShortList> getCompanyEmployeeShortLists() {
		return this.companyEmployeeShortLists;
	}

	public void setCompanyEmployeeShortLists(
			Set<CompanyEmployeeShortList> companyEmployeeShortLists) {
		this.companyEmployeeShortLists = companyEmployeeShortLists;
	}

}