package com.payasia.logic;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.ComboValueDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.TeamLeaveDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.LeaveBalanceSummaryResponse;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveApplication;

@Transactional
public interface LeaveBalanceSummaryLogic {

	LeaveBalanceSummaryForm getEmployeeSchemeDetail(String empNumber,
			Long companyId);

	EmployeeLeaveSchemeTypeDTO getDataForSchemeType(Long empLeaveSchemeTypeId);

	LeaveBalanceSummaryResponse savePostTransactionLeaveType(
			EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistory,
			Long companyId, Long employeeId,  boolean isAdmin);

	List<LeaveBalanceSummaryForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId);

	LeaveBalanceSummaryResponse getDashBoardEmpOnLeaveList(String fromDate,
			String toDate, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId);

	LeaveBalanceSummaryForm myLeaveSchemeDetail(Long employeeId, Long companyId);

	LeaveBalanceSummaryForm getLeaveCalMonthList(String year, String month,
			Long companyId, Long employeeId);

	LeaveBalanceSummaryResponse getEmpOnLeaveByDate(String[] leaveAppIds,
			Long companyId, Long employeeId, PageRequest pageDTO,
			SortCondition sortDTO, boolean isAdmin);

	List<EmployeeLeaveSchemeTypeDTO> getDistinctYears(Long companyId);

	List<LeaveBalanceSummaryForm> getLeaveScheme(String empNumber, String year,
			Long companyId);

	LeaveBalanceSummaryResponse getPostLeaveSchemeData(String empNumber,
			String year, Long companyId);

	List<LeaveBalanceSummaryForm> getMyLeaveScheme(Long employeeId,
			String year, Long companyId);

	AddLeaveFormResponse getCompletedLeaves(Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, Long companyId);

	void canCelLeave(LeaveBalanceSummaryForm leaveBalanceSummaryForm,
			Long employeeId, Long companyId, LeaveSessionDTO sessionDTO);

	LeaveBalanceSummaryResponse getDashBoardByManagerEmpOnLeaveList(
			String fromDate, String toDate, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long employeeId,List<String> roleList,MessageSource messageSource,int year,int fromYear, int toYear);

	LeaveBalanceSummaryForm getLeaveCalMonthListByManager(String year,
			String month, Long companyId, Long employeeId,List<String> roleList );

	List<ComboValueDTO> getLeaveSessionList();

	AddLeaveForm getLeaveBalance(Long employeeLeaveSchemeTypeId);

	AddLeaveForm getNoOfDays(Long companyId, Long employeeId, LeaveDTO leaveDTO);

	LeaveBalanceSummaryResponse getMyLeaveSchemeType(int year,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long employeeId);

	AddLeaveFormResponse getLeaveReviewers(Long leaveApplicationId);

	List<LeaveBalanceSummaryForm> getEmployeeIdForManager(Long companyId,
			String searchString, Long employeeId);

	String getEmployeeName(Long loggedInEmployeeId, String employeeNumber,
			Long companyId);

	LeaveBalanceSummaryResponse getEmpLeaveSchemeTypeHistoryList(
			Long leaveSchemeTypeId, String postLeaveTypeFilter, int year,
			String employeeNumber, Long companyId, Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, boolean isAdmin);

	String deleteLeaveTransaction(Long companyId, Long leaveTranId);

	LeaveBalanceSummaryResponse getLeavetransactionData(Long leaveTranId,
			Long companyId);

	LeaveBalanceSummaryResponse updatePostTransactionLeaveType(
			EmployeeLeaveSchemeTypeHistoryDTO employeeLeaveSchemeTypeHistory,
			Long companyId, Long employeeId, Long histroryId);

	void cancelLeaveTransaction(Long companyId, Long leaveTranId,
			Long employeeId);

	List<LeaveBalanceSummaryForm> getHolidaycalendar(Long companyId,
			Long employeeId, int year);

	LeaveBalanceSummaryForm importPostLeaveTran(
			LeaveBalanceSummaryForm leaveBalanceSummaryForm, Long companyId,
			Long employeeId);

	LeaveBalanceSummaryResponse getEmployeeLeaveSchemeType(int year,
			String employeeNumber, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId);

	LeaveBalanceSummaryResponse getTeamEmployeeLeaveSchemeType(int year,
			String employeeNumber, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId, String searchStringEmpId);

	LeaveBalanceSummaryResponse getTeamEmpLeaveSchemeTypeHistoryList(
			Long leaveSchemeTypeId, String postLeaveTypeFilter, int year,
			String employeeNumber, Long companyId, Long employeeId,
			String searchStringEmpId, PageRequest pageDTO, SortCondition sortDTO);

	LeavePreferenceForm isEncashedVisible(Long companyId);

	List<AppCodeDTO> getLeaveTransactionType(Long companyId);

	List<String> getLeaveTransactionHistoryType(Long companyId);

	LeaveBalanceSummaryResponse getLeaveTransactionHistory(
			Long leaveApplicationId, String transactionType, Long companyId);

	String getEmployeeNameForManager(Long loggedInEmployeeId,
			String employeeNumber, Long companyId);

	LeaveApplicationAttachmentDTO viewAttachment(long attachmentId);

	void createLeaveCalendarDate(int year, Long companyId,
			EmployeeLeaveSchemeTypeDTO employeeLeaveSchemeTypeDTO);

	void forfeitFromOtherLeaveType(LeaveApplication leaveApplication,
			Long leaveSchemeTypeId);

	void deleteForfeitFromOtherLeaveType(LeaveApplication leaveApplication,
			Long leaveSchemeTypeId);

	boolean isLeaveUnitDays(Long companyId);

	void addLeaveAppToKeyPayInt(LeaveApplication leaveApplication);

	String getDefaultEmailCCByEmp(Long companyId, Long employeeId, String moduleName, boolean moduleEnabled);

	List<EmployeeFilterListForm> getDefaultEmailCCListByEmployee(Long companyId, Long employeeId, String moduleName,
			boolean moduleEnabled);

	void saveDefaultEmailCCByEmployee(Long companyId, Long employeeId, String ccEmailIds, String moduleName,
			boolean moduleEnabled);

	AddLeaveForm getLeaveBalance(Long employeeLeaveSchemeTypeId, Long empId, Long companyId);
	
	Long getEmployeeIdByCode(String employeeNumber, Long companyID);

	LeaveApplication findByLeaveApplicationIdAndEmpId(Long leaveApplicationId, Long loginEmployeeID, Long companyID);

	EmployeeShortListDTO getEmployeeNameForManagerDup(Long loggedInEmployeeId, String employeeNumber, Long companyId);

	List<TeamLeaveDTO> getTeamMemberInfo(Long companyId, Long employeeId);


}
