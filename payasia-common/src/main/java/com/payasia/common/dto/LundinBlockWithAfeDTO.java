package com.payasia.common.dto;

import java.util.List;


public class LundinBlockWithAfeDTO {
	
	
	private long blockId;
	
	private String effectiveDate;

	private boolean status;

	private String blockCode;

	private String blockName;

	private boolean effectiveAllocation;
	
	private List<LundinAFEWithoutBlockDTO> lundinAfes;
	
	public long getBlockId() {
		return blockId;
	}

	public void setBlockId(long blockId) {
		this.blockId = blockId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
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

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public List<LundinAFEWithoutBlockDTO> getLundinAfes() {
		return lundinAfes;
	}

	public void setLundinAfes(List<LundinAFEWithoutBlockDTO> lundinAfes) {
		this.lundinAfes = lundinAfes;
	}



}
