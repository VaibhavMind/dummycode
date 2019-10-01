package com.payasia.common.form;

import java.io.Serializable;

public class OTItemDefinitionForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1449852034755296620L;
	private Long otTemplateItemId;
	private Long otItemId; 
	private String name;
	private String code;
	private String accountCode;
	private Boolean visibleOrHidden;
	private String instruction;
	private String type;
	private String deletedMsg;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public Boolean getVisibleOrHidden() {
		return visibleOrHidden;
	}

	public void setVisibleOrHidden(Boolean visibleOrHidden) {
		this.visibleOrHidden = visibleOrHidden;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public Long getOtItemId() {
		return otItemId;
	}

	public void setOtItemId(Long otItemId) {
		this.otItemId = otItemId;
	}

	public String getDeletedMsg() {
		return deletedMsg;
	}

	public void setDeletedMsg(String deletedMsg) {
		this.deletedMsg = deletedMsg;
	}

	public Long getOtTemplateItemId() {
		return otTemplateItemId;
	}

	public void setOtTemplateItemId(Long otTemplateItemId) {
		this.otTemplateItemId = otTemplateItemId;
	}

}
