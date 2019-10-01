package com.payasia.common.dto;

import java.io.Serializable;

public class LundinTimewritingDeptReportDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4217120984052116720L;
	
	private Long departmentId;
	private String departmentCode;
	private String departmentName;
	private long displayOrder;
	private String departmentType;
	
	private Long employeeId;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private Long blockId;
	private String blockName;
	private String blockCode;
	private boolean blockEffectiveAllocation;
	private Long afeId;
	private String afeName;
	private Double value;
	
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public Long getBlockId() {
		return blockId;
	}
	public void setBlockId(Long blockId) {
		this.blockId = blockId;
	}
	public String getBlockName() {
		return blockName;
	}
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	public Long getAfeId() {
		return afeId;
	}
	public void setAfeId(Long afeId) {
		this.afeId = afeId;
	}
	public String getAfeName() {
		return afeName;
	}
	public void setAfeName(String afeName) {
		this.afeName = afeName;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getDepartmentType() {
		return departmentType;
	}
	public void setDepartmentType(String departmentType) {
		this.departmentType = departmentType;
	}
	public long getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(long displayOrder) {
		this.displayOrder = displayOrder;
	}

	
	public boolean isBlockEffectiveAllocation() {
		return blockEffectiveAllocation;
	}
	public void setBlockEffectiveAllocation(boolean blockEffectiveAllocation) {
		this.blockEffectiveAllocation = blockEffectiveAllocation;
	}
	
	
	
	public String getBlockCode() {
		return blockCode;
	}
	public void setBlockCode(String blockCode) {
		this.blockCode = blockCode;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj !=null && obj instanceof LundinTimewritingDeptReportDTO){
			LundinTimewritingDeptReportDTO ltdr=(LundinTimewritingDeptReportDTO)obj;
			if( ltdr.blockName.equals(this.blockName) && ltdr.afeName.equals(this.afeName)){
					return true;
			}
			
		}
		return false;
	}
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.blockName.length() ^ (this.blockName.length() >>> 29)));
		hash = hash * prime + ((int) (this.afeName.length() ^ (this.afeName.length() >>> 29)));
		
		
		return hash;
    }
	
}
