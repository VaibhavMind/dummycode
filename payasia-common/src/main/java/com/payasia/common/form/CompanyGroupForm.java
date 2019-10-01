package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CompanyGroupForm extends PageResponse implements Serializable {

	private List<CompanyGroupForm> rows;

	private Long groupId;
	private String groupName;
	private String groupDesc;
	private String groupCode;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public List<CompanyGroupForm> getRows() {
		return rows;
	}

	public void setRows(List<CompanyGroupForm> rows) {
		this.rows = rows;
	}

}
