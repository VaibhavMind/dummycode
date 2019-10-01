package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationPdfDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.PendingItemsFormResponse;
import com.payasia.common.form.SortCondition;

@Transactional
public interface PendingItemsLogic {

	List<AppCodeDTO> getWorkflowTypeList();

	PendingItemsFormResponse getPendingLeaves(Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String workflowTypeValue,
			String searchCondition, String searchText);

	PendingItemsForm getDataForLeaveReview(Long reviewId);

	AddLeaveForm acceptLeave(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO);

	String rejectLeave(PendingItemsForm pendingItemsForm, Long employeeId,
			LeaveSessionDTO sessionDTO);

	AddLeaveForm forwardLeave(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO);

	PendingItemsFormResponse getLeaveTransactions(Long createdById, Long empId,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long leaveApplicationId, String startDate, String endDate);

	PendingItemsFormResponse getPendingLeavesForAdmin(Long empId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String workflowTypeValue, String searchCondition, String searchText);

	AddLeaveForm rejectLeaveForAdmin(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO);

	AddLeaveForm acceptLeaveforAdmin(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO);

	PendingItemsFormResponse getEmployeesOnLeave(String fromDate,
			String toDate, Long leaveApplicationId, Long empId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	LeaveApplicationPdfDTO generateLeaveApplicationPrintPDF(Long companyId,
			Long employeeId, Long leaveApplicationReviewerId);

	PendingItemsFormResponse viewMultipleLeaveApplications(Long companyId,
			Long employeeId, String[] leaveApplicationRevIds);

	PendingItemsForm showEmpLeaveWorkflowStatus(Long companyId,Long employeeId,
			Long leaveApplicationId);
	
	PendingItemsForm showEmpLeaveWorkflowStatusForMobile(Long companyId,Long employeeId,
			Long leaveApplicationId);

	List<AddLeaveForm> reviewMultipleLeaveApp(PendingItemsForm pendingItemsFrm,
			Long employeeId, Long companyId, LeaveSessionDTO sessionDTO);
	
	List<AddLeaveForm> reviewMultipleLeaveApproveandForward(PendingItemsForm pendingItemsFrm,
			Long employeeId, Long companyId, LeaveSessionDTO sessionDTO);

	List<AddLeaveForm> reviewMultipleLeaveAppByAdmin(
			PendingItemsForm pendingItemsForm, Long employeeId, Long companyId,
			LeaveSessionDTO sessionDTO);
	PendingItemsForm getDataForLeaveReviewEmp(Long reviewId,Long employeeId);

	LeaveApplicationAttachmentDTO viewAttachmentByReviewer(Long attachmentId, Long empReviewerId, Long companyId);
	LeaveReviewerResponseForm searchEmployee(PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			String empName, String empNumber, Long companyId);

	boolean isSameCompanyGroupExist(Long leaveApplicationId, Long companyId);

}
