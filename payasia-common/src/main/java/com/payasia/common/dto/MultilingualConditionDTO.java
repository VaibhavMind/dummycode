package com.payasia.common.dto;

import java.io.Serializable;

public class MultilingualConditionDTO implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 8852823372957853181L;
private Long languageId;
private Long entityId;
private Long companyId;
public Long getLanguageId() {
	return languageId;
}
public void setLanguageId(Long languageId) {
	this.languageId = languageId;
}
public Long getEntityId() {
	return entityId;
}
public void setEntityId(Long entityId) {
	this.entityId = entityId;
}
public Long getCompanyId() {
	return companyId;
}
public void setCompanyId(Long companyId) {
	this.companyId = companyId;
}

}
