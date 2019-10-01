package com.payasia.common.dto;

import java.util.List;

public class LundinAFEDTO {
	private long afeId;

	public long getAfeId() {
		return afeId;
	}

	public void setAfeId(long afeId) {
		this.afeId = afeId;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
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

	public List<LundinBlockDTO> getLundinBlocks() {
		return lundinBlocks;
	}

	public void setLundinBlocks(List<LundinBlockDTO> lundinBlocks) {
		this.lundinBlocks = lundinBlocks;
	}

	public Long[] getLundinAfeBlock() {
		return lundinAfeBlock;
	}

	public void setLundinAfeBlock(Long[] lundinAfeBlock) {
		this.lundinAfeBlock = lundinAfeBlock;
	}

	private String effectiveDate;
	
	private List<LundinBlockDTO> lundinBlocks;
	
//	public Set<LundinBlockAFEMapping> getLundinBlockAFEMapping() {
//		return lundinBlockAFEMapping;
//	}
//
//	public void setLundinBlockAFEMapping(
//			Set<LundinBlockAFEMapping> lundinBlockAFEMapping) {
//		this.lundinBlockAFEMapping = lundinBlockAFEMapping;
//	}

//	private Timestamp effectiveDate;

	private boolean status;

	private String afeCode;

	private String afeName;
	
	private Long[] lundinAfeBlock;

//	private Set<LundinBlockAFEMapping> lundinBlockAFEMapping;
}
