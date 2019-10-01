package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Lundin_AFE")
public class LundinAFE extends CompanyBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "AFE_ID")
	private long afeId;

	@Column(name = "Effective_Date")
	private Timestamp effectiveDate;

	public long getAfeId() {
		return afeId;
	}

	public void setAfeId(long afeId) {
		this.afeId = afeId;
	}

	public Timestamp getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getAfeCode() {
		return afeCode;
	}

	public void setAfeCode(String afeCode) {
		this.afeCode = afeCode;
	}

	public String getAfeName() {
		return afeName;
	}

	public void setAfeName(String afeName) {
		this.afeName = afeName;
	}

	@Column(name = "Status")
	private boolean status;

	@Column(name = "AFE_Code")
	private String afeCode;

	@Column(name = "AFE_Name")
	private String afeName;

	@ManyToMany
	@JoinTable(name = "Lundin_Block_AFE_Mapping", joinColumns = { @JoinColumn(name = "AFE_ID", referencedColumnName = "AFE_ID") }, inverseJoinColumns = { @JoinColumn(name = "Block_ID") })
	private List<LundinBlock> lundinBlocks;

	public List<LundinBlock> getLundinBlocks() {
		return lundinBlocks;
	}

	public void setLundinBlocks(List<LundinBlock> lundinBlocks) {
		this.lundinBlocks = lundinBlocks;
	}

}
