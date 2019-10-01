package com.payasia.api.claim.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.claim.EmployeePendingClaimApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimApplicationItemWorkflowForm;
import com.payasia.common.form.PendingClaimsForm;
import com.payasia.common.form.PendingClaimsResponseForm;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.MyClaimLogic;
import com.payasia.logic.PendingClaimsLogic;

/**
 * @author gauravkumar
 * @param : This class used for Pending Claim APIs
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_CLAIM+ApiUtils.PENDING_CLAIM, produces={"application/json"}, consumes={"application/json"})
public class EmployeePendingClaimApiImpl implements EmployeePendingClaimApi {

	@Resource
	private PendingClaimsLogic pendingClaimsLogic;

	@Autowired
	private MessageSource messageSource;
	
	@Resource
	private MyClaimLogic myClaimLogic;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Override
	@PostMapping(value = "search-pending-claim")
	public ResponseEntity<?> viewPendingClaims(@RequestBody SearchParam searchParamObj) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		if (filterllist != null && !filterllist.isEmpty()) {
			addClaimDTO.setSearchCondition(String.valueOf(filterllist.get(0).getField()));
			addClaimDTO.setSearchText(String.valueOf(filterllist.get(0).getValue()));
		}
		PendingClaimsResponseForm pendingClaimsResponseForm = pendingClaimsLogic.getPendingClaims( addClaimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
		
		if(pendingClaimsResponseForm!=null) {
			return new ResponseEntity<>(pendingClaimsResponseForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "review-claim")
	public ResponseEntity<?> doReviewPendingClaim(@RequestParam(value = "claimApplicationreviewerId") Long claimApplicationreviewerId) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		boolean hasLundinTimesheetModule = UserContext.isLundinTimesheetModule();
		
		PendingClaimsForm claimsForm = pendingClaimsLogic.getDataForClaimReview(FormatPreserveCryptoUtil.decrypt(claimApplicationreviewerId), employeeId, UserContext.getLocale(), messageSource);
		
		if (claimsForm != null) {
			claimsForm.setLundinTimesheetModule(hasLundinTimesheetModule);
			return new ResponseEntity<>(claimsForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	
	@Override
	@PostMapping(value = "save-override-review-claim")
	public ResponseEntity<?> doSaveOverrideReviewClaim(@RequestBody ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemWorkflowForm.getClaimApplicationItemID()));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);
		
		ClaimApplicationItemForm claimApplicationItemForm = pendingClaimsLogic.saveOverrideItemInfo(addClaimDTO,claimApplicationItemWorkflowForm);
		processValidations(claimApplicationItemForm);
		
		if(claimApplicationItemForm.getValidationClaimItemDTO()==null){
			return new ResponseEntity<>(claimApplicationItemForm, HttpStatus.OK);
		}
		else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), claimApplicationItemForm.getValidationClaimItemDTO().getErrorValue()), HttpStatus.NOT_FOUND);
		}
/*		
		Map<String, Object> claimApplicationItemForm = pendingClaimsLogic.saveOverrideClaimItemInfo(addClaimDTO,claimApplicationItemWorkflowForm);
		if(claimApplicationItemForm.get("code").toString().equals("200")){
			return new ResponseEntity<>(claimApplicationItemForm.get("status"), HttpStatus.OK);
		}
		else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(claimApplicationItemForm.get("status").toString(), new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
*/		
	}
	
	public void processValidations(ClaimApplicationItemForm claimApplicationItemForm){
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (claimApplicationItemForm.getValidationClaimItemDTO() != null && claimApplicationItemForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(claimApplicationItemForm.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = claimApplicationItemForm.getValidationClaimItemDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(claimApplicationItemForm.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = claimApplicationItemForm.getValidationClaimItemDTO().getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if(count>0) {
						errorKeyFinalStr.append("#");
					}
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0 && StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
						}
						errorKeyFinalStr.append(messageSource.getMessage(errorKeyArr[count], errorVal, UserContext.getLocale()));
					}
				}
			}
			claimApplicationItemForm.getValidationClaimItemDTO().setErrorValue(errorKeyFinalStr.toString());
		}
	}
	
	@Override
	@PostMapping(value = "save-reject-review-claim")
	public ResponseEntity<?> doSaveRejectReviewClaim(@RequestBody ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemWorkflowForm.getClaimApplicationItemID()));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
	
		ClaimApplicationItemForm claimApplicationItemForm = pendingClaimsLogic.saveRejectItemInfo(addClaimDTO, claimApplicationItemWorkflowForm);
		if(claimApplicationItemForm!=null){
			return new ResponseEntity<>(claimApplicationItemForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "claim-item-data")
	public ResponseEntity<?> claimItemDataDetail(@RequestParam(value = "claimItemId") Long claimItemId){
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimItemId = FormatPreserveCryptoUtil.decrypt(claimItemId);
		ClaimApplicationItemForm claimItemDetail = pendingClaimsLogic.getClaimItemDetail(claimItemId, employeeId, companyId);
		if(claimItemDetail!=null){
			claimItemDetail.setClaimApplicationItemID(FormatPreserveCryptoUtil.encrypt(claimItemDetail.getClaimApplicationItemID()));
			return new ResponseEntity<>(claimItemDetail, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * @param This API is used to process the request of pending claim data.
	 * @return ResponseEntity
	 * @param String claimRequestType,PendingClaimsForm pendingClaimsForm
	 */
	// TODO : Remove all the commented sending mail lines
	@Override
	@PostMapping("process-pending-claim-request")
	public ResponseEntity<?> processPendingClaimData(@RequestParam(value="claimRequestType", required = true) String claimRequestType, @RequestBody PendingClaimsForm pendingClaimsForm){
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		
		PendingClaimsForm claimsForm = new PendingClaimsForm();;

		switch(claimRequestType.toUpperCase()){
		case "FORWARD":
			claimsForm = pendingClaimsLogic.forwardClaim(pendingClaimsForm, addClaimDTO);
			process(claimsForm);
			break;
		
		case "ACCEPT":
			claimsForm = pendingClaimsLogic.acceptClaim(pendingClaimsForm, employeeId, companyId);
			process(claimsForm);
			break;
			
		case "REJECT":
			claimsForm = pendingClaimsLogic.rejectClaim(pendingClaimsForm, employeeId, companyId);
			break;
			
		default :
			claimsForm.setMessageCode("404");
			break;
			
		}
		
		if(claimsForm.getValidationClaimItemDTO()!=null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(),claimsForm.getValidationClaimItemDTO().getErrorValue()), HttpStatus.NOT_FOUND);
		}
    	if(StringUtils.isNotEmpty(claimsForm.getMessageCode()) && StringUtils.isNotBlank(claimsForm.getMessageCode())){
			if(claimsForm.getMessageCode().equals("200")){
				return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.pending.claim.request.operation.success", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.OK);
			}
			else if(claimsForm.getMessageCode().equals("404")){
				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.pending.claim.request.invalid", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
			}
			else if(claimsForm.getMessageCode().equals("201")){
				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.pending.claim.request.operation.fail", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
			}
			
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	public void process(PendingClaimsForm claimsForm){
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (claimsForm.getValidationClaimItemDTO() != null && claimsForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(claimsForm.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = claimsForm.getValidationClaimItemDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(claimsForm.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = claimsForm.getValidationClaimItemDTO().getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if(count>0) {
						errorKeyFinalStr.append("#");
					}
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0 && StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
						}
						errorKeyFinalStr.append(messageSource.getMessage(errorKeyArr[count], errorVal, UserContext.getLocale()));
					}
					
				}
			}
			claimsForm.getValidationClaimItemDTO().setErrorValue(errorKeyFinalStr.toString());
		}
	}
	
	/**
	 * @param This API is used to print the pending claim data.
	 */
	@Override
	@GetMapping("print-application")
	public ResponseEntity<?> doPrintPendingClaimData(@RequestParam(value="claimApplicationReviewerId", required = true) Long claimApplicationReviewerId){
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Map<String, Object> printClaimMap = new HashMap<String, Object>();
		
		boolean hasLundinTimesheetModule =UserContext.isLundinTimesheetModule();
		
		ClaimFormPdfDTO claimFormPdfDTO = pendingClaimsLogic.generateClaimFormPrintPDF(companyId, employeeId,
				FormatPreserveCryptoUtil.decrypt(claimApplicationReviewerId), hasLundinTimesheetModule);
		
		if(claimFormPdfDTO==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
		
		String filename = claimFormPdfDTO.getEmployeeNumber() + "_" + claimFormPdfDTO.getClaimTemplateName() + ".pdf";
		
		printClaimMap.put("data", claimFormPdfDTO.getClaimFormPdfByteFile());
		printClaimMap.put("employeeNumber", claimFormPdfDTO.getEmployeeNumber());
		printClaimMap.put("claimTemplateName", claimFormPdfDTO.getClaimTemplateName());
		printClaimMap.put("filename", filename);
		return new ResponseEntity<>(printClaimMap, HttpStatus.OK);
	}
	
	/**
	 * @param This API is used to download attachment
	 */
	@Override
	@GetMapping("download-attachment")
	public ResponseEntity<?> viewClaimAttachment(@RequestParam (value="claimAttachmentId", required = true) Long claimApplicationItemAttachmentId){
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemAttachmentId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemAttachmentId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(true);

		ClaimApplicationItemAttach claimApplicationItemAttach = pendingClaimsLogic.viewAttachment(addClaimDTO);
		
		if (claimApplicationItemAttach != null && claimApplicationItemAttach.getAttachmentBytes()!=null) {
			// byte[] byteFile = claimApplicationItemAttach.getAttachmentBytes();
			return new ResponseEntity<>(claimApplicationItemAttach, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
}
