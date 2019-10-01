package com.payasia.logic;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.EmployeeClaimTemplateDataResponse;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.dao.bean.Employee;

@Transactional
public interface AddClaimLogic {

	void saveClaimApplicationItem(AddClaimForm addClaimForm, Long claimTemplateItemId);

	AddClaimForm getClaimTemplates(Long companyId, Long employeeId, boolean isAdmin);

	AddClaimForm getClaimAppItemForEdit(Long claimApplicationId, Long companyId, Long employeeId);

	AddClaimForm saveClaimTemplateItem(ClaimApplicationItemForm claimApplicationItemForm, Long companyId);

	AddClaimForm updateClaimTemplateItem(ClaimApplicationItemForm claimApplicationItemForm, Long companyId);

	AddClaimForm getForexRate(Date currencyDate, Long currencyId, Long companyId);

	AddClaimForm getEmployeeClaimTemplates(String employeeNumber, Long companyId, Long sessionEmployeeId);

	void uploadClaimAplicationAttachment(String fileName, byte[] imgBytes, Long claimApplicationItemId);

	List<LundinTimesheetReportsForm> lundinBlockList(Long companyId, String claimItemAccountCode);

	List<LundinTimesheetReportsForm> lundinAFEList(Long companyId, Long blockId, String claimItemAccountCode);

	Employee getDelegatedEmployee(Long claimAppEmpId, Long employeeId);

	AddClaimForm saveAsDraftClaimApp(Long claimApplicationId, Long companyId, Long employeeId);

	AddClaimForm getClaimTemplateItemList(AddClaimDTO addClaimDTO);

	AddClaimForm getClaimTemplateItemConfigData(AddClaimDTO addClaimDTO);

	AddClaimForm persistClaimApplication(AddClaimDTO addClaimDTO, AddClaimForm addClaimForm);

	AddClaimForm saveAsDraftFromWithdrawClaim(AddClaimDTO addClaimDTO, AddClaimForm addClaimForm);

	AddClaimForm copyClaimApplication(AddClaimDTO addClaimDTO, AddClaimForm addClaimForm);

	AddClaimForm persistAdminClaimApplication(AddClaimDTO addClaimDTO, AddClaimForm addClaimForm);

	AddClaimForm saveClaimApplication(AddClaimDTO addClaimDTO);

	String withdrawClaim(AddClaimDTO addClaimDTO);

	Long getEmployeeId(Long companyId, String employeeNumber);

	AddClaimForm deleteClaimApplicationAttachement(AddClaimDTO addClaimDTO);

	AddClaimForm getEmployeeItemData(AddClaimDTO addClaimDTO);

	AddClaimForm uploadClaimItemAttachement(ClaimApplicationItemAttach claimApplicationItemAttach,
			AddClaimDTO addClaimDTO);

	AddClaimForm deleteClaimApplication(AddClaimDTO addClaimDTO);

	AddClaimForm deleteClaimTemplateItem(AddClaimDTO addClaimDTO);

	AddClaimForm getClaimApplicationItemList(AddClaimDTO addClaimDTO);

	AddClaimForm deleteApprovedClaimApplication(AddClaimDTO addClaimDTO);

	EmployeeClaimTemplateDataResponse getClaimTemplatesData(Long companyId, Long employeeId);

	

}
