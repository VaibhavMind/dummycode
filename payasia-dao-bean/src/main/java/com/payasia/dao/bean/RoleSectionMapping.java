package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Role_Section_Mapping database table.
 * 
 */
@Entity
@Table(name = "Role_Section_Mapping")
public class RoleSectionMapping extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Role_Section_Mapping_ID")
	private long roleSectionMappingId;

	@Column(name = "Form_ID")
	private long formId;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Role_ID")
	private RoleMaster roleMaster;

	public RoleSectionMapping() {
	}

	public long getRoleSectionMappingId() {
		return roleSectionMappingId;
	}

	public void setRoleSectionMappingId(long roleSectionMappingId) {
		this.roleSectionMappingId = roleSectionMappingId;
	}

	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}

	public RoleMaster getRoleMaster() {
		return roleMaster;
	}

	public void setRoleMaster(RoleMaster roleMaster) {
		this.roleMaster = roleMaster;
	}

}