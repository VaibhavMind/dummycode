package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the Help_Desk database table.
 * 
 */
@Entity
@Table(name = "Help_Desk")
public class HelpDesk implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Help_Desk_ID")
	private long helpDeskId;

	@Column(name = "Key_Name")
	private String keyName;
	
	@Column(name = "Screen")
	private String screenName;

	@Column(name = "Topic")
	private String topicName;

	@Column(name = "URL")
	private String url;

	@Column(name = "Role")
	private String role;
	
	@Column(name = "Module")
	private String module;
	
	@Column(name = "Company_ID")
	private Long companyId;
	
	@Column(name = "Active")
	private Boolean active;

	public long getHelpDeskId() {
		return helpDeskId;
	}

	public void setHelpDeskId(long helpDeskId) {
		this.helpDeskId = helpDeskId;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	
}