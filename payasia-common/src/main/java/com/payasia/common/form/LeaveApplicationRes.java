package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.LeaveAttachmentDTO;

public class LeaveApplicationRes implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8688662376759120647L;
	private Long leaveApplicationId;
	private List<LeaveAttachmentDTO> leaveAttachmentDTOs;
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public List<LeaveAttachmentDTO> getLeaveAttachmentDTOs() {
		return leaveAttachmentDTOs;
	}
	public void setLeaveAttachmentDTOs(List<LeaveAttachmentDTO> leaveAttachmentDTOs) {
		this.leaveAttachmentDTOs = leaveAttachmentDTOs;
	}
	
	
	

}
