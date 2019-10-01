package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Privilege_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Privilege_Master")
public class PrivilegeMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "Privilege_ID")
	private long privilegeId;

	@Column(name = "Privilege_Desc")
	private String privilegeDesc;

	@Column(name = "Privilege_Name")
	private String privilegeName;

	 
	@ManyToMany(mappedBy = "privilegeMasters")
	private Set<RoleMaster> roleMasters;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Module_ID")
	private ModuleMaster moduleMaster;

	@Column(name = "Privilege_Role")
	private String privilegeRole;

	@Column(name = "Active")
	private Boolean active;

	public PrivilegeMaster() {
	}

	public long getPrivilegeId() {
		return this.privilegeId;
	}

	public void setPrivilegeId(long privilegeId) {
		this.privilegeId = privilegeId;
	}

	public String getPrivilegeDesc() {
		return this.privilegeDesc;
	}

	public void setPrivilegeDesc(String privilegeDesc) {
		this.privilegeDesc = privilegeDesc;
	}

	public String getPrivilegeName() {
		return this.privilegeName;
	}

	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}

	public Set<RoleMaster> getRoleMasters() {
		return this.roleMasters;
	}

	public void setRoleMasters(Set<RoleMaster> roleMasters) {
		this.roleMasters = roleMasters;
	}

	public ModuleMaster getModuleMaster() {
		return moduleMaster;
	}

	public void setModuleMaster(ModuleMaster moduleMaster) {
		this.moduleMaster = moduleMaster;
	}

	public String getPrivilegeRole() {
		return privilegeRole;
	}

	public void setPrivilegeRole(String privilegeRole) {
		this.privilegeRole = privilegeRole;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}