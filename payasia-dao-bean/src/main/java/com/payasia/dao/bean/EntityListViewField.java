package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Entity_List_View_Field database table.
 * 
 */
@Entity
@Table(name = "Entity_List_View_Field")
public class EntityListViewField extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Entity_List_View_Field_ID")
	private long entityListViewFieldId;

	@Column(name = "Sequence")
	private int sequence;

	 
	@ManyToOne
	@JoinColumn(name = "Data_Dictionary_ID")
	private DataDictionary dataDictionary;

	 
	@ManyToOne
	@JoinColumn(name = "Entity_List_View_ID")
	private EntityListView entityListView;

	public EntityListViewField() {
	}

	public long getEntityListViewFieldId() {
		return this.entityListViewFieldId;
	}

	public void setEntityListViewFieldId(long entityListViewFieldId) {
		this.entityListViewFieldId = entityListViewFieldId;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public DataDictionary getDataDictionary() {
		return this.dataDictionary;
	}

	public void setDataDictionary(DataDictionary dataDictionary) {
		this.dataDictionary = dataDictionary;
	}

	public EntityListView getEntityListView() {
		return this.entityListView;
	}

	public void setEntityListView(EntityListView entityListView) {
		this.entityListView = entityListView;
	}

}