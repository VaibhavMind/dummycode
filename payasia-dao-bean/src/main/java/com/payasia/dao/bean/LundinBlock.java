package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Lundin_Block")
public class LundinBlock extends BaseEntity implements Serializable {
	public long getBlockId() {
		return blockId;
	}

	public void setBlockId(long blockId) {
		this.blockId = blockId;
	}

	public String getBlockCode() {
		return blockCode;
	}

	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public boolean isEffectiveAllocation() {
		return effectiveAllocation;
	}

	public void setEffectiveAllocation(boolean effectiveAllocation) {
		this.effectiveAllocation = effectiveAllocation;
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Block_ID")
	private long blockId;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

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

	@Column(name = "Effective_Date")
	private Timestamp effectiveDate;

	@Column(name = "Status")
	private boolean status;

	@Column(name = "Block_Code")
	private String blockCode;

	@Column(name = "Block_Name")
	private String blockName;

	@Column(name = "Effective_Allocation")
	private boolean effectiveAllocation;

	// @ManyToMany
	// @JoinTable(name = "Lundin_Block_AFE_Mapping", joinColumns = {
	// @JoinColumn(name = "Block_ID", referencedColumnName = "Block_ID") },
	// inverseJoinColumns = { @JoinColumn(name = "AFE_ID") })
	@ManyToMany(mappedBy = "lundinBlocks")
	private List<LundinAFE> lundinAfes;

	public List<LundinAFE> getLundinAfes() {
		return lundinAfes;
	}

	public void setLundinAfes(List<LundinAFE> lundinAfes) {
		this.lundinAfes = lundinAfes;
	}

}
