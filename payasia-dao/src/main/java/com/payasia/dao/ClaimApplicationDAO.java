package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Tuple;

import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.EmployeeWiseConsolidatedClaimReportDataDTO;
import com.payasia.common.dto.EmployeeWiseTemplateClaimReportDataDTO;
import com.payasia.common.dto.MonthlyConsolidatedFinanceReportDataDTO;
import com.payasia.common.dto.ValidateClaimApplicationDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimApplication;

public interface ClaimApplicationDAO {

	ClaimApplication saveReturn(ClaimApplication claimApplication);

	Long getCountForCondition(AddClaimConditionDTO conditionDTO);

	List<ClaimApplication> findByCondition(AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	ClaimApplication findByID(long claimApplicationId);

	void update(ClaimApplication claimApplication);

	Long getMaxClaimNumber();

	void delete(ClaimApplication claimApplication);

	Integer getCountByConditionForAdmin(Long employeeId, List<String> claimStatus, String fromDate, String toDate);

	ValidateClaimApplicationDTO validateClaimApplication(Long employeeClaimAppId, Long employeeClaimTemplateId,
			boolean isAdmin);

	List<ClaimApplication> findByConditionForAdmin(PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			List<String> claimStatus, String fromDate, String toDate, Boolean visibleToEmployee,
			String claimNumberSortOrder, String createdDateSortOrder);

	List<ClaimApplication> findClaimsByBatchPeriod(PageRequest pageDTO, SortCondition sortDTO,
			String claimStatusCompleted, Timestamp startDate, Timestamp endDate, Long companyID);

	Integer getClaimsByBatchPeriodCount(String claimStatusCompleted, Timestamp startDate, Timestamp endDate,
			Long companyID);

	List<Tuple> getCategoryWiseTotalCount(Long claimApplicationId);

	Integer getCountByConditionForEmployee(Long employeeId, List<String> claimStatus, String fromDate, String toDate,
			AddClaimConditionDTO conditionDTO);

	List<EmployeeWiseTemplateClaimReportDataDTO> getEmloyeeWiseTemplateClaimReportProc(Long companyId, int year,
			Long claimTemplateId, String employeeIdList);

	List<ClaimApplication> findByConditionForEmployee(PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			List<String> claimStatus, String fromDate, String toDate, Boolean visibleToEmployee,
			AddClaimConditionDTO conditionDTO, String claimNumberSortOrder, String createdDateSortOrder);

	List<EmployeeWiseConsolidatedClaimReportDataDTO> getEmloyeeWiseConsClaimReportProc(Long companyId, int year,
			Long claimTemplateId, String claimItemIdList, Boolean visibleToEmployee, Boolean orderBy,
			String employeeIdList, boolean isIncludeResignedEmployees);

	List<MonthlyConsolidatedFinanceReportDataDTO> getMonthlyConsFinanceReportProc(Long companyId, Timestamp fromDate,
			Timestamp toDate, String claimTemplateIdList, String claimItemIdList, Boolean orderBy,
			String employeeIdList, boolean isIncludeResignedEmployees);

	ClaimApplication findByClaimApplicationID(AddClaimDTO addClaimDTO);

	List<ClaimApplication> findByCondition2(AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, int position);

}
