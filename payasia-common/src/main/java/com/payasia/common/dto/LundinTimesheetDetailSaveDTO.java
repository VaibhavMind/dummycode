package com.payasia.common.dto;

import java.util.List;

public class LundinTimesheetDetailSaveDTO {
	private long blockId;
	private long afeId;
	private String blockName;
	private String afeName;
	private String blockCode;
	private String afeCode;
	public long getBlockId() {
		return blockId;
	}
	public void setBlockId(long blockId) {
		this.blockId = blockId;
	}
	public long getAfeId() {
		return afeId;
	}
	public void setAfeId(long afeId) {
		this.afeId = afeId;
	}

	public List<LundinTimesheetSaveDataDTO> getSaveData() {
		return saveData;
	}
	public void setSaveData(List<LundinTimesheetSaveDataDTO> saveData) {
		this.saveData = saveData;
	}

	public String getBlockName() {
		return blockName;
	}
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	public String getAfeName() {
		return afeName;
	}
	public void setAfeName(String afeName) {
		this.afeName = afeName;
	}
	public String getBlockCode() {
		return blockCode;
	}
	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}
	public String getAfeCode() {
		return afeCode;
	}
	public void setAfeCode(String afeCode) {
		this.afeCode = afeCode;
	}

	private List<LundinTimesheetSaveDataDTO> saveData;
	
}
