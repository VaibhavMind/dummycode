package com.payasia.dao;

import com.payasia.dao.bean.LeaveApplicationAttachment;

public interface LeaveApplicationAttachmentDAO {

	void save(LeaveApplicationAttachment leaveApplicationAttachment);

	LeaveApplicationAttachment saveReturn(
			LeaveApplicationAttachment leaveApplicationAttachment);

	LeaveApplicationAttachment findById(Long LeaveApplicationAttachmentId);

	void delete(LeaveApplicationAttachment leaveApplicationAttachment);
	LeaveApplicationAttachment findAttachmentByEmployeeCompanyId(Long attachmentId, Long empId, Long companyId);
	LeaveApplicationAttachment viewAttachmentByReviewer(Long attachmentId, Long empReviewerId, Long companyId);
	LeaveApplicationAttachment findAttachmentByEmployeeCompanyId(Long attachmentId, Long companyId);
	
}
