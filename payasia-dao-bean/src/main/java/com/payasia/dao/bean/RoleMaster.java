package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Role_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Role_Master")
public class RoleMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Role_ID")
	private long roleId;

	@Column(name = "Deletable")
	private boolean deletable = true;

	@Column(name = "Role_Desc")
	private String roleDesc;

	@Column(name = "Role_Name")
	private String roleName;

	 
	@OneToMany(mappedBy = "roleMaster")
	private Set<EmployeeRoleMapping> employeeRoleMappings;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "Role_Privilege_Mapping", joinColumns = { @JoinColumn(name = "Role_ID") }, inverseJoinColumns = { @JoinColumn(name = "Privilege_ID") })
	private Set<PrivilegeMaster> privilegeMasters;

	 
	@OneToMany(mappedBy = "roleMaster")
	private Set<RoleSectionMapping> roleSectionMappings;

	 
	@OneToMany(mappedBy = "roleMaster")
	private Set<EmployeeRoleSectionMapping> employeeRoleSectionMappings;

	public RoleMaster() {
	}

	public long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public boolean getDeletable() {
		return this.deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public String getRoleDesc() {
		return this.roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set<EmployeeRoleMapping> getEmployeeRoleMappings() {
		return this.employeeRoleMappings;
	}

	public void setEmployeeRoleMappings(
			Set<EmployeeRoleMapping> employeeRoleMappings) {
		this.employeeRoleMappings = employeeRoleMappings;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<PrivilegeMaster> getPrivilegeMasters() {
		return this.privilegeMasters;
	}

	public void setPrivilegeMasters(Set<PrivilegeMaster> privilegeMasters) {
		this.privilegeMasters = privilegeMasters;
	}

	public Set<RoleSectionMapping> getRoleSectionMappings() {
		return roleSectionMappings;
	}

	public void setRoleSectionMappings(
			Set<RoleSectionMapping> roleSectionMappings) {
		this.roleSectionMappings = roleSectionMappings;
	}

	public Set<EmployeeRoleSectionMapping> getEmployeeRoleSectionMappings() {
		return employeeRoleSectionMappings;
	}

	public void setEmployeeRoleSectionMappings(
			Set<EmployeeRoleSectionMapping> employeeRoleSectionMappings) {
		this.employeeRoleSectionMappings = employeeRoleSectionMappings;
	}

}