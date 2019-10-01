package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.payasia.common.bean.util.UserContext;

@MappedSuperclass
public class UpdatedBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "Updated_Date")
	private Timestamp updatedDate;

	@Column(name = "Updated_By")
	private String updatedBy;

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@PrePersist
	public void onPreInsert() {
		this.setUpdatedBy(UserContext.getUserId());
		this.setUpdatedDate(getTimeStamp());
	}

	@PreUpdate
	public void onPreUpdate() {
		this.setUpdatedBy(UserContext.getUserId());
		this.setUpdatedDate(getTimeStamp());

	}

	@PreRemove
	public void onPreDelete() {
		this.setUpdatedBy(UserContext.getUserId());
		this.setUpdatedDate(getTimeStamp());

	}

	private Timestamp getTimeStamp() {
		Timestamp currentTimestamp = new Timestamp(new Date().getTime());
		return currentTimestamp;
	}

}
