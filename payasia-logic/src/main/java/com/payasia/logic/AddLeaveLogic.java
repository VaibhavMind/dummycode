package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.ComboValueDTO;
import com.payasia.common.dto.EmployeeDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationPdfDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface AddLeaveLogic {



	LeaveSchemeForm getLeaveSchemes(Long companyId, Long employeeId);

	AddLeaveForm getLeaveTypes(Long leaveSchemeId, Long companyId, Long employeeId);

	AddLeaveForm addLeave(Long companyId, Long employeeId, AddLeaveForm addLeaveForm, LeaveSessionDTO sessionDTO);

	AddLeaveFormResponse getPendingLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, Long companyId);

	LeaveApplicationAttachmentDTO viewAttachment(long attachmentId);

	void deleteAttachment(long attachmentId);

	AddLeaveForm editLeave(Long companyId, Long employeeId, AddLeaveForm addLeaveForm, LeaveSessionDTO sessionDTO);

	void deleteLeave(long leaveApplicationId);

	AddLeaveFormResponse getSubmittedLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText,Long companyId);

	String withdrawLeave(Long leaveApplicationId, Long empId, LeaveSessionDTO sessionDTO);

	AddLeaveFormResponse getCompletedLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText,Long companyId);

	AddLeaveFormResponse getRejectedLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText,Long companyId);

	AddLeaveForm viewLeave(Long leaveApplicationId);

	AddLeaveForm getDataForPendingLeave(Long leaveApplicationId, Long employeeId, Long companyId);

	List<ComboValueDTO> getLeaveSessionList();

	AddLeaveForm getLeaveBalance(Long companyId, Long employeeId, Long employeeLeaveSchemeTypeId);

	AddLeaveForm getLeaveCustomFields(Long leaveSchemeId, Long leaveTypeId, Long companyId, Long employeeId, Long employeeLeaveSchemeId);

	AddLeaveForm getNoOfDays(Long companyId, Long employeeId, LeaveDTO leaveDTO);

	AddLeaveFormResponse getWithDrawnLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText,Long companyId);

	AddLeaveFormResponse getSubmittedCancelledLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,Long companyId);

	AddLeaveFormResponse getCompletedCancelLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,Long companyId);

	LeaveReviewerResponseForm searchEmployee(PageRequest pageDTO, SortCondition sortDTO, Long employeeId, String empName, String empNumber,
			Long companyId);

	AddLeaveFormResponse getRejectedCancelLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText);

	AddLeaveFormResponse getWithDrawnCancelLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText);

	LeaveApplicationPdfDTO generateLeaveApplicationPrintPDF(Long companyId, Long employeeId, Long leaveApplicationId);

	void uploadLeaveApllication(String fileName, byte[] imgBytes, Long leaveApplicationId);

	AddLeaveForm addLeaveMobile(Long companyId, Long employeeId, AddLeaveForm addLeaveForm, Integer noOfAttachements, LeaveSessionDTO sessionDTO);

	AddLeaveForm updateLeaveMobile(Long companyId, Long employeeId, AddLeaveForm addLeaveForm, Integer noOfAttachments, LeaveSessionDTO leaveSessionDTO);

	AddLeaveForm getLeaveCustomFieldAndReviewerForAdmin(Long employeeLeaveSchemeId, Long leaveTypeId, Long companyId, Long employeeId,
			Long employeeLeaveSchemeTypeId);

	AddLeaveForm addLeaveByAdmin(Long companyId, Long employeeId, AddLeaveForm addLeaveForm, LeaveSessionDTO sessionDTO);

	AddLeaveForm extensionLeave(Long companyId, Long employeeId, AddLeaveForm addLeaveForm, LeaveSessionDTO sessionDTO);

	String extensionLeaveView(Long leaveApplicationId, Long empId, Long companyId);

	AddLeaveForm viewLeave(Long leaveApplicationId, Long employeeId, Long companyId);
	void deleteAttachment(Long attachmentId, Long empId, Long companyId);
	
	String withdrawLeave(Long leaveApplicationId, Long empId, LeaveSessionDTO sessionDTO, Long companyId);
	void deleteLeave(long leaveApplicationId, long empId, long companyId);
	LeaveApplicationAttachmentDTO viewAttachment(Long attachmentId, Long empId, Long companyId);

	String extensionLeaveViewAdmin(Long leaveApplicationId, Long empId, Long companyId);

	void deleteLeave(Long leaveApplicationId, EmployeeDTO employeeDTO);

	LeaveApplicationAttachmentDTO viewAttachmentMob(Long attachmentId, Long companyId, Long employeeId);

	String withdrawLeaveMobile(Long leaveApplicationId, Long employeeId, LeaveSessionDTO sessionDTO, Long companyId);

	AddLeaveForm viewLeave(Long leaveApplicationId, EmployeeDTO employeeDTO);

	AddLeaveFormResponse getAllLeaveRequestData(String fromDate, String toDate, Long empId, PageRequest pageDTO, SortCondition sortDTO, String pageContext,String searchCondition,
			String searchText,Long companyId);

}
