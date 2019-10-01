package com.payasia.dao;

import java.util.Date;
import java.util.List;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimReviewerReportDataDTO;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;

public interface EmployeeClaimTemplateItemDAO {

	void update(EmployeeClaimTemplateItem employeeClaimTemplateItem);

	void save(EmployeeClaimTemplateItem employeeClaimTemplateItem);

	void delete(EmployeeClaimTemplateItem employeeClaimTemplateItem);

	EmployeeClaimTemplateItem findByID(long employeeClaimTemplateItemId);

	List<ClaimReviewerReportDataDTO> findClaimReviewerReportData(Long companyId, String employeeIds,
			Boolean isAllEmployees);

	EmployeeClaimTemplateItem findByEmployeeIdAndClaimTemplateItemId(Long employeeId, Long claimTemplateItemId,Long companyId);

	List<EmployeeClaimTemplateItem> findByCompanyCondition(Long companyId, Date currentDate, long claimTemplateId);

	EmployeeClaimTemplateItem findItemByTemplateIdandItemName(long employeeClaimTemplateId, String claimItemName,
			String claimCategory);
	
	Integer findReceiptAmountPercentageForAmountBase(Long claimTemplateItemId);

	EmployeeClaimTemplateItem findByEmployeeClaimTemplateItemID(AddClaimDTO addClaimDTO);

	List<EmployeeClaimTemplateItem> findByEmployeeClaimTemplateId(AddClaimDTO addClaimDTO);

}
