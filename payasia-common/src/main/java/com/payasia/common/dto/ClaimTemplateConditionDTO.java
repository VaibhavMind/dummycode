/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

public class ClaimTemplateConditionDTO {

	private String templateName;
	private String status;
	private Long claimItemId;

	private Long frontEndViewModeOnId;
	private Long frontEndViewModeOnWhenTranId;
	private Long frontEndViewModeOffId;
	private Long backEndViewModeOnId;
	private Long backEndViewModeOnWhenTranId;
	private Long backEndViewModeOffId;
	private String frontEndViewModeOn;
	private String frontEndViewModeOnWhenTran;
	private String frontEndViewModeOff;
	private String backEndViewModeOn;
	private String backEndViewModeOnWhenTran;
	private String backEndViewModeOff;

	public Long getClaimItemId() {
		return claimItemId;
	}

	public void setClaimItemId(Long claimItemId) {
		this.claimItemId = claimItemId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Long getFrontEndViewModeOnId() {
		return frontEndViewModeOnId;
	}

	public void setFrontEndViewModeOnId(Long frontEndViewModeOnId) {
		this.frontEndViewModeOnId = frontEndViewModeOnId;
	}

	public Long getFrontEndViewModeOnWhenTranId() {
		return frontEndViewModeOnWhenTranId;
	}

	public void setFrontEndViewModeOnWhenTranId(
			Long frontEndViewModeOnWhenTranId) {
		this.frontEndViewModeOnWhenTranId = frontEndViewModeOnWhenTranId;
	}

	public Long getFrontEndViewModeOffId() {
		return frontEndViewModeOffId;
	}

	public void setFrontEndViewModeOffId(Long frontEndViewModeOffId) {
		this.frontEndViewModeOffId = frontEndViewModeOffId;
	}

	public Long getBackEndViewModeOnId() {
		return backEndViewModeOnId;
	}

	public void setBackEndViewModeOnId(Long backEndViewModeOnId) {
		this.backEndViewModeOnId = backEndViewModeOnId;
	}

	public Long getBackEndViewModeOnWhenTranId() {
		return backEndViewModeOnWhenTranId;
	}

	public void setBackEndViewModeOnWhenTranId(Long backEndViewModeOnWhenTranId) {
		this.backEndViewModeOnWhenTranId = backEndViewModeOnWhenTranId;
	}

	public Long getBackEndViewModeOffId() {
		return backEndViewModeOffId;
	}

	public void setBackEndViewModeOffId(Long backEndViewModeOffId) {
		this.backEndViewModeOffId = backEndViewModeOffId;
	}

	public String getFrontEndViewModeOn() {
		return frontEndViewModeOn;
	}

	public void setFrontEndViewModeOn(String frontEndViewModeOn) {
		this.frontEndViewModeOn = frontEndViewModeOn;
	}

	public String getFrontEndViewModeOnWhenTran() {
		return frontEndViewModeOnWhenTran;
	}

	public void setFrontEndViewModeOnWhenTran(String frontEndViewModeOnWhenTran) {
		this.frontEndViewModeOnWhenTran = frontEndViewModeOnWhenTran;
	}

	public String getFrontEndViewModeOff() {
		return frontEndViewModeOff;
	}

	public void setFrontEndViewModeOff(String frontEndViewModeOff) {
		this.frontEndViewModeOff = frontEndViewModeOff;
	}

	public String getBackEndViewModeOn() {
		return backEndViewModeOn;
	}

	public void setBackEndViewModeOn(String backEndViewModeOn) {
		this.backEndViewModeOn = backEndViewModeOn;
	}

	public String getBackEndViewModeOnWhenTran() {
		return backEndViewModeOnWhenTran;
	}

	public void setBackEndViewModeOnWhenTran(String backEndViewModeOnWhenTran) {
		this.backEndViewModeOnWhenTran = backEndViewModeOnWhenTran;
	}

	public String getBackEndViewModeOff() {
		return backEndViewModeOff;
	}

	public void setBackEndViewModeOff(String backEndViewModeOff) {
		this.backEndViewModeOff = backEndViewModeOff;
	}

}
