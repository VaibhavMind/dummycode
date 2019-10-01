package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Entity_List_View database table.
 * 
 */
@Entity
@Table(name = "Entity_List_View")
public class EntityListView extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Entity_List_View_ID")
	private long entityListViewId;

	@Column(name = "Records_Per_Page")
	private int recordsPerPage;

	@Column(name = "View_Name")
	private String viewName;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Entity_ID")
	private EntityMaster entityMaster;

	 
	@OneToMany(mappedBy = "entityListView", cascade = { CascadeType.ALL })
	private Set<EntityListViewField> entityListViewFields;

	public EntityListView() {
	}

	public long getEntityListViewId() {
		return this.entityListViewId;
	}

	public void setEntityListViewId(long entityListViewId) {
		this.entityListViewId = entityListViewId;
	}

	public int getRecordsPerPage() {
		return this.recordsPerPage;
	}

	public void setRecordsPerPage(int recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}

	public String getViewName() {
		return this.viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public EntityMaster getEntityMaster() {
		return this.entityMaster;
	}

	public void setEntityMaster(EntityMaster entityMaster) {
		this.entityMaster = entityMaster;
	}

	public Set<EntityListViewField> getEntityListViewFields() {
		return this.entityListViewFields;
	}

	public void setEntityListViewFields(
			Set<EntityListViewField> entityListViewFields) {
		this.entityListViewFields = entityListViewFields;
	}

}