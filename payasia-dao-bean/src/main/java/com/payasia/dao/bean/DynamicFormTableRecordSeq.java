package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the Dynamic_Form_Table_Record_Seq database table.
 * 
 */
@Entity
@Table(name = "Dynamic_Form_Table_Record_Seq")
public class DynamicFormTableRecordSeq implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@Column(name = "Next_Val")
	private long nextVal;

	public DynamicFormTableRecordSeq() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getNextVal() {
		return nextVal;
	}

	public void setNextVal(long nextVal) {
		this.nextVal = nextVal;
	}

}