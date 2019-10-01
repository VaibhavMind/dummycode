package com.payasia.common.form;

import java.io.Serializable;

public class AnouncementForm implements Serializable{

	private static final long serialVersionUID = -11906035314906210L;
	
	private Long announcementId;
	private String title;
	private String description;
	private String scope;
	private String postDateTime;
	private String postTime;
	private String removeDateTime;
	private String removeTime;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getPostTime() {
		return postTime;
	}
	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}
	public String getRemoveTime() {
		return removeTime;
	}
	public void setRemoveTime(String removeTime) {
		this.removeTime = removeTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Long getAnnouncementId() {
		return announcementId;
	}
	public void setAnnouncementId(Long announcementId) {
		this.announcementId = announcementId;
	}
	public String getPostDateTime() {
		return postDateTime;
	}
	public void setPostDateTime(String postDateTime) {
		this.postDateTime = postDateTime;
	}
	public String getRemoveDateTime() {
		return removeDateTime;
	}
	public void setRemoveDateTime(String removeDateTime) {
		this.removeDateTime = removeDateTime;
	}

}
