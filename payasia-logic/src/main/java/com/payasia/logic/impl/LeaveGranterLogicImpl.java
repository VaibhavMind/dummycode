package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.payasia.common.dto.LeaveGrantProcDTO;
import com.payasia.common.dto.RollBackProcDTO;
import com.payasia.common.form.LeaveGranterForm;
import com.payasia.common.form.LeaveGranterFormResponse;
import com.payasia.common.form.LeaveTypeForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.YearEndProcessingForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.LeaveGrantBatchDAO;
import com.payasia.dao.LeaveGrantBatchDetailDAO;
import com.payasia.dao.LeaveGrantBatchEmployeeDetailDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.LeaveGrantBatch;
import com.payasia.dao.bean.LeaveGrantBatchDetail;
import com.payasia.dao.bean.LeaveGrantBatchEmployeeDetail;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.logic.LeaveGranterLogic;

@Component
public class LeaveGranterLogicImpl implements LeaveGranterLogic {
	@Resource
	LeaveGrantBatchDAO leaveGrantBatchDAO;

	@Resource
	LeaveGrantBatchDetailDAO leaveGrantBatchDetailDAO;

	@Resource
	LeaveGrantBatchEmployeeDetailDAO leaveGrantBatchEmployeeDetailDAO;

	@Resource
	CompanyDAO companyDAO;
	@Resource
	LeaveSchemeDAO leaveSchemeDAO;
	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;
	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	@Override
	public LeaveGranterFormResponse getLeaveGrantBatchDetailList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			String fromDate, String toDate, Long leaveType) {

		LeaveGranterFormResponse response = new LeaveGranterFormResponse();

		List<LeaveGranterForm> leaveGranterFormList = new ArrayList<>();
		List<LeaveGrantBatch> leaveGrantBatchList = leaveGrantBatchDAO
				.findByCondition(companyId, fromDate, toDate, leaveType,
						pageDTO, sortDTO);
		for (LeaveGrantBatch leaveGrantBatch : leaveGrantBatchList) {
			LeaveGranterForm leaveGranterForm = new LeaveGranterForm();

			leaveGranterForm.setBatchNumber(String.valueOf(leaveGrantBatch
					.getBatchNumber()));
			leaveGranterForm.setBatchDate(DateUtils
					.timeStampToString(leaveGrantBatch.getBatchDate()));

			Set<LeaveGrantBatchDetail> leaveGrantBatchDetailSet = leaveGrantBatch
					.getLeaveGrantBatchDetails();
			for (LeaveGrantBatchDetail leaveGrantBatchDetail : leaveGrantBatchDetailSet) {
				/* ID ENCRYPT*/
				leaveGranterForm
						.setLeaveGrantBatchDetailId(FormatPreserveCryptoUtil.encrypt(leaveGrantBatchDetail
								.getLeaveGrantBatchDetailId()));
				leaveGranterForm.setEmployeesCount(String
						.valueOf(leaveGrantBatchDetail.getEmployeesCount()));
				leaveGranterForm.setFromPeriod(DateUtils
						.timeStampToString(leaveGrantBatchDetail
								.getFromPeriod()));
				leaveGranterForm
						.setToPeriod(DateUtils
								.timeStampToString(leaveGrantBatchDetail
										.getToPeriod()));
				leaveGranterForm.setLeaveSchemeId(leaveGrantBatchDetail
						.getLeaveSchemeType().getLeaveScheme()
						.getLeaveSchemeId());
				leaveGranterForm.setLeaveScheme(leaveGrantBatchDetail
						.getLeaveSchemeType().getLeaveScheme().getSchemeName());
				leaveGranterForm.setLeaveTypeId(leaveGrantBatchDetail
						.getLeaveSchemeType().getLeaveTypeMaster()
						.getLeaveTypeId());
				leaveGranterForm.setLeaveType(leaveGrantBatchDetail
						.getLeaveSchemeType().getLeaveTypeMaster()
						.getLeaveTypeName());
				leaveGranterFormList.add(leaveGranterForm);
			}

		}
		int recordSize = leaveGrantBatchDAO.getCountByCondition(companyId,
				fromDate, toDate, leaveType);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

		}
		response.setRecords(recordSize);
		response.setLeaveGranterFormList(leaveGranterFormList);
		return response;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}

		return employeeName;
	}

	@Override
	public LeaveGranterFormResponse getLeaveGrantBatchEmpDetailList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long leaveGrantBatchDetailId, String employeeNumber) {
		LeaveGranterFormResponse response = new LeaveGranterFormResponse();

		List<LeaveGranterForm> leaveGranterFormList = new ArrayList<>();
		List<LeaveGrantBatchEmployeeDetail> leaveGrantBatchEmpList = leaveGrantBatchEmployeeDetailDAO
				.findByCondition(leaveGrantBatchDetailId, employeeNumber,
						pageDTO, sortDTO);
		for (LeaveGrantBatchEmployeeDetail batchEmployeeDetail : leaveGrantBatchEmpList) {
			LeaveGranterForm leaveGranterForm = new LeaveGranterForm();
			/* ID ENCRYPT*/
			
			leaveGranterForm
					.setLeaveGrantBatchEmployeeDetailId(FormatPreserveCryptoUtil.encrypt(batchEmployeeDetail
							.getLeaveGrantBatchEmployeeDetailId()));
			leaveGranterForm.setGrantedDays(String.valueOf(batchEmployeeDetail
					.getGrantedDays()));
			leaveGranterForm.setEmployeeId(batchEmployeeDetail.getEmployee()
					.getEmployeeId());
			leaveGranterForm
					.setEmployeeName(getEmployeeName(batchEmployeeDetail
							.getEmployee()));
			leaveGranterForm.setEmployeeNumber(batchEmployeeDetail
					.getEmployee().getEmployeeNumber());
			if (batchEmployeeDetail.getEmployee().getHireDate() != null) {
				leaveGranterForm.setHireDate(DateUtils
						.timeStampToString(batchEmployeeDetail.getEmployee()
								.getHireDate()));
			}

			leaveGranterFormList.add(leaveGranterForm);

		}
		int recordSize = leaveGrantBatchEmployeeDetailDAO
				.getCountByCondition(leaveGrantBatchDetailId);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

		}
		response.setRecords(recordSize);
		response.setLeaveGranterFormList(leaveGranterFormList);
		return response;
	}

	@Override
	public List<YearEndProcessingForm> getLeaveScheme(Long companyId) {
		List<YearEndProcessingForm> yearEndProcessingFormList = new ArrayList<>();
		List<LeaveScheme> leaveSchemeList = leaveSchemeDAO
				.getAllLeaveScheme(companyId);
		for (LeaveScheme leaveScheme : leaveSchemeList) {
			YearEndProcessingForm yearEndProcessingForm = new YearEndProcessingForm();
			yearEndProcessingForm.setLeaveSchemeId(leaveScheme
					.getLeaveSchemeId());
			yearEndProcessingForm.setLeaveScheme(leaveScheme.getSchemeName());
			yearEndProcessingFormList.add(yearEndProcessingForm);
		}
		return yearEndProcessingFormList;
	}

	@Override
	public List<LeaveGranterForm> getLeaveType(Long companyId) {
		List<LeaveGranterForm> leaveGranterFormList = new ArrayList<>();
		List<LeaveTypeMaster> leaveTypeList = leaveTypeMasterDAO
				.findByCompanyAndVisibility(companyId);
		for (LeaveTypeMaster leaveTypeMaster : leaveTypeList) {
			LeaveGranterForm leaveGranterForm = new LeaveGranterForm();
			leaveGranterForm.setLeaveTypeId(leaveTypeMaster.getLeaveTypeId());
			leaveGranterForm.setLeaveType(leaveTypeMaster.getLeaveTypeName());
			leaveGranterFormList.add(leaveGranterForm);
		}

		return leaveGranterFormList;
	}

	@Override
	public LeaveGranterFormResponse getLeaveGranterEmpList(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long leaveSchemeId,
			Long leaveTypeId) {
		LeaveGranterFormResponse response = new LeaveGranterFormResponse();

		Set<LeaveGranterForm> leaveGranterFormSet = new LinkedHashSet<>();
		Set<Employee> employeeSet = new LinkedHashSet<>();
		List<Employee> granterEmployeesList = employeeDAO.findGranterEmployees(
				companyId, leaveSchemeId, leaveTypeId, pageDTO, sortDTO);
		for (Employee employeeVO : granterEmployeesList) {
			employeeSet.add(employeeVO);
		}
		for (Employee employeeVO : employeeSet) {
			LeaveGranterForm leaveGranterForm = new LeaveGranterForm();
			leaveGranterForm.setEmployeeId(employeeVO.getEmployeeId());
			leaveGranterForm.setEmployeeName(getEmployeeName(employeeVO));
			if (employeeVO.getHireDate() != null) {
				leaveGranterForm.setHireDate(DateUtils
						.timeStampToString(employeeVO.getHireDate()));
			}
			leaveGranterForm.setEmployeeNumber(employeeVO.getEmployeeNumber());
			leaveGranterFormSet.add(leaveGranterForm);
		}

		int recordSize = employeeDAO.getCountForGranterEmployees(companyId,
				leaveSchemeId, leaveTypeId);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

		}
		response.setRecords(recordSize);
		response.setLeaveGranterFormSet(leaveGranterFormSet);
		return response;
	}

	@Override
	public String deleteLeaveGrantBatchDetail(Long leaveGrantBatchDetailId) {

		Boolean status = leaveGrantBatchDAO.callDeleteLeaveGrantBatchProc(
				leaveGrantBatchDetailId, null);
		return status.toString();

	}

	@Override
	public String deleteLeaveGrantBatchEmpDetail(Long leaveGrantBatchEmpDetailId) {
		Boolean status = leaveGrantBatchDAO.callDeleteLeaveGrantBatchProc(null,
				leaveGrantBatchEmpDetailId);
		return status.toString();
	}

	@Override
	public LeaveGranterFormResponse getAnnualRollbackEmpList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			String fromDate, String toDate) {
		LeaveGranterFormResponse response = new LeaveGranterFormResponse();

		Set<LeaveGranterForm> leaveGranterFormSet = new LinkedHashSet<>();
		List<Employee> granterEmployeesList = employeeDAO
				.findAnnualRollbackEmps(companyId, fromDate, toDate, pageDTO,
						sortDTO);
		for (Employee employeeVO : granterEmployeesList) {
			LeaveGranterForm leaveGranterForm = new LeaveGranterForm();
			leaveGranterForm.setEmployeeId(employeeVO.getEmployeeId());
			leaveGranterForm.setEmployeeName(getEmployeeName(employeeVO));
			if (employeeVO.getResignationDate() != null) {
				leaveGranterForm.setResignationDate(DateUtils
						.timeStampToString(employeeVO.getResignationDate()));
			}
			leaveGranterForm.setEmployeeNumber(employeeVO.getEmployeeNumber());
			leaveGranterFormSet.add(leaveGranterForm);
		}

		int recordSize = employeeDAO.getCountAnnualRollbackEmps(companyId,
				fromDate, toDate);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

		}
		response.setRecords(recordSize);
		response.setLeaveGranterFormSet(leaveGranterFormSet);
		return response;
	}

	@Override
	public LeaveGranterFormResponse getLeaveGranterLeaveTypeList(
			Long companyId, Long leaveSchemeId) {
		LeaveGranterFormResponse response = new LeaveGranterFormResponse();
		List<LeaveSchemeType> leaveSchemeTypes = leaveSchemeTypeDAO
				.findByConditionLeaveSchemeIdCompanyId(leaveSchemeId, companyId);
		List<LeaveTypeForm> leaveTypes = new ArrayList<>();
		if(leaveSchemeTypes != null && !leaveSchemeTypes.isEmpty())
		{
			for (LeaveSchemeType leaveSchemeType : leaveSchemeTypes) {
				LeaveTypeForm leaveTypeForm = new LeaveTypeForm();
				leaveTypeForm.setLeaveSchemeTypeId(leaveSchemeType
						.getLeaveSchemeTypeId());
				leaveTypeForm.setLeaveType(leaveSchemeType.getLeaveScheme()
						.getSchemeName());
				leaveTypeForm.setLeaveScheme(leaveSchemeType.getLeaveTypeMaster()
						.getLeaveTypeName());
				leaveTypes.add(leaveTypeForm);
			}
		}
		response.setLeaveTypes(leaveTypes);
		return response;
	}

	@Override
	public Boolean callLeaveGrantProc(Long companyId,
			String leaveSchemeTypeIds, Boolean isNewHires, String fromDate,
			String toDate, String employeeIdsList) {
		LeaveGrantProcDTO leaveGrantProcDTO = new LeaveGrantProcDTO();
		leaveGrantProcDTO.setCompanyId(companyId);
		leaveGrantProcDTO.setCurrentDate(DateUtils.getCurrentTimestamp());
		leaveGrantProcDTO.setIsYearEndProcess(true);
		leaveGrantProcDTO.setLeaveSchemeTypeIds(leaveSchemeTypeIds);
		leaveGrantProcDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		leaveGrantProcDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		leaveGrantProcDTO.setIsNewHires(isNewHires);
		leaveGrantProcDTO.setEmployeeIds(employeeIdsList);
		Boolean status = leaveSchemeTypeDAO
				.callLeaveGrantProc(leaveGrantProcDTO);
		return status;
	}

	@Override
	public Boolean rollbackResignedEmployeeLeaveProc(Long companyId,
			String employeeIds, Long loggedInEmployeeId) {
		String[] empIds = employeeIds.split(",");
		for (String employeeId : empIds) {
			RollBackProcDTO rollBackProcDTO = leaveSchemeTypeDAO
					.rollbackResignedEmployeeLeaveProc(
							Long.parseLong(employeeId), loggedInEmployeeId);
		}

		return true;
	}
}
