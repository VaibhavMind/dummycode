package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionInfoDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1963382910749630058L;
	private Long sectionId;
	private String sectionName;
	private String sectionIdGroup;
	
	public Long getSectionId() {
		return sectionId;
	}
	public String getSectionIdGroup() {
		return sectionIdGroup;
	}
	public void setSectionIdGroup(String sectionIdGroup) {
		this.sectionIdGroup = sectionIdGroup;
	}
	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	@Override
	public boolean  equals(Object obj){
		
		if(obj!=null && obj instanceof SectionInfoDTO){
			if((((SectionInfoDTO) obj).getSectionId().equals(this.sectionId))&&((SectionInfoDTO) obj).getSectionName().trim().equals(this.getSectionName().trim())) {
				
				return true;
			}
		}else{
			return false;
		}
		return false;
	}
	
	
	@Override
	   public int hashCode() {
	      return 1;
	   }
	
	
}
