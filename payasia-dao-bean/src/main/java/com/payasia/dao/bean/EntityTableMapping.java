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
 * The persistent class for the Entity_Table_Mapping database table.
 * 
 */
@Entity
@Table(name = "Entity_Table_Mapping")
public class EntityTableMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Entity_Table_Mapping_ID")
	private long entityTableMappingId;

	@Column(name = "Table_Name")
	private String tableName;

	@Column(name = "Table_Order")
	private int tableOrder;

	 
	@ManyToOne
	@JoinColumn(name = "Entity_ID")
	private EntityMaster entityMaster;

	public EntityTableMapping() {
	}

	public long getEntityTableMappingId() {
		return this.entityTableMappingId;
	}

	public void setEntityTableMappingId(long entityTableMappingId) {
		this.entityTableMappingId = entityTableMappingId;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getTableOrder() {
		return this.tableOrder;
	}

	public void setTableOrder(int tableOrder) {
		this.tableOrder = tableOrder;
	}

	public EntityMaster getEntityMaster() {
		return this.entityMaster;
	}

	public void setEntityMaster(EntityMaster entityMaster) {
		this.entityMaster = entityMaster;
	}

}