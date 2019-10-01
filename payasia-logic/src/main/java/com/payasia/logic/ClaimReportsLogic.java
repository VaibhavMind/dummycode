package com.payasia.logic;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.payasia.common.dto.ClaimDetailsReportDTO;
import com.payasia.common.dto.ClaimItemDTO;
import com.payasia.common.dto.ClaimReviewerInfoReportDTO;
import com.payasia.common.dto.ClaimTemplateDTO;
import com.payasia.common.dto.EmployeeWiseConsolidatedClaimReportDTO;
import com.payasia.common.dto.EmployeeWiseTemplateClaimReportDTO;
import com.payasia.common.dto.MonthlyConsolidatedFinanceReportDTO;
import com.payasia.common.form.ClaimCategoryForm;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.ClaimReportsResponse;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface ClaimReportsLogic {
	
	List<String> getClaimReportType();
	
	List<ClaimTemplateDTO> getAllClaimTemplate(Long companyId);
	
	List<ClaimReportsForm> getClaimCategoryList(Long companyId);
	
	List<ClaimItemDTO> getClaimItemListForClaimDetailsReport(Long companyId, Long[] claimTemplateIdArr);
	
	ClaimDetailsReportDTO showClaimTransactionReport(Long companyId, ClaimReportsForm claimReportsForm, Long employeeId,
			String[] dataDictionaryIds, boolean hasLundinTimesheetModule, Locale locale,
			Boolean isCheckedFromCreatedDate, MessageSource messageSource);

	List<ClaimReportsForm> getClaimItemList(Long companyId, Long claimCategoryId);

	List<ClaimReportsForm> getClaimBatchList(Long companyId);

	ClaimReviewerInfoReportDTO showClaimReviewerReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId);

	EmployeeWiseConsolidatedClaimReportDTO showEmployeeWiseConsClaimReport(Long companyId,
			ClaimReportsForm claimReportsForm, Long employeeId, String[] dataDictionaryIds);

	ClaimReportsResponse searchEmployee(PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, Long companyId, Long employeeId, boolean isShortList, String metaData);

	EmployeeWiseConsolidatedClaimReportDTO showEmpWiseConsClaimReportII(Long companyId,
			ClaimReportsForm claimReportsForm, Long employeeId, String[] dataDictionaryIds);

	List<ClaimReportsForm> getAllClaimTemplateWithTemplateCapping(Long companyId);

	List<ClaimReportsForm> getAllClaimTemplateWithClaimItemCapping(Long companyId);

	List<ClaimReportsForm> getClaimItemListByTemplateId(Long companyId, Long claimTemplateId);

	

	List<ClaimCategoryForm> getAllClaimCategory(Long companyId);

	EmployeeWiseTemplateClaimReportDTO showEmpWiseTemplateClaimReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId);

	MonthlyConsolidatedFinanceReportDTO showMonthlyConsFinanceReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds);

	ClaimDetailsReportDTO showClaimDetailsReport(Long companyId, ClaimReportsForm claimReportsForm, Long employeeId,
			String[] dataDictionaryIds, boolean hasLundinTimesheetModule, Locale locale);

	ClaimDetailsReportDTO showBatchWiseClaimDetailsReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds, boolean hasLundinTimesheetModule);

	ClaimDetailsReportDTO genClaimHeadcountReport(Long employeeId, Long companyId, ClaimReportsForm claimReportsForm,
			Boolean isManager);

	ClaimDetailsReportDTO showCategoryWiseClaimDetailsReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds, boolean hasLundinTimesheetModule);

	ClaimDetailsReportDTO showPaidClaimDetailsReport(Long companyId, ClaimReportsForm claimReportsForm, Long employeeId,
			String[] dataDictionaryIds, boolean hasLundinTimesheetModule);

	List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId, String searchString, Long employeeId);



	
	
}
