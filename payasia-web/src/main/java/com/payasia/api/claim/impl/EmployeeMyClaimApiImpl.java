package com.payasia.api.claim.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.claim.EmployeeMyClaimApi;
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
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimBatchForm;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.EmployeeClaimTemplateDataResponse;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.ClaimBatchLogic;
import com.payasia.logic.LundinTimesheetReportsLogic;
import com.payasia.logic.MyClaimLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param : This class used for My Claim APIs
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_CLAIM+ApiUtils.MY_CLAIM)
public class EmployeeMyClaimApiImpl implements EmployeeMyClaimApi{

	@Resource
	private MyClaimLogic myClaimLogic;
	
	@Resource
	private AddClaimLogic addClaimLogic;

	@Autowired
	private MessageSource messageSource;
	
	@Resource
	private LundinTimesheetReportsLogic lundinTimesheetReportsLogic;
	
	@Resource
	private ClaimBatchLogic claimBatchLogic;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	public void intialize(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@Override
	@PostMapping(value = "search-my-claims")
	public ResponseEntity<?> searchMyClaims(@RequestBody SearchParam searchParamObj, @RequestParam(value = "requestType", required = true) String requestType) {
		
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		AddClaimDTO claimDTO = setClaimData(searchParamObj);
		requestType = requestType.toUpperCase();
		AddClaimFormResponse addClaimFormResponse = null;
		switch (requestType) {
		case "ALL":
			addClaimFormResponse = myClaimLogic.getAllClaims(claimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
			break;
		
		case "DRAFT":
			addClaimFormResponse = myClaimLogic.getPendingClaims(claimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
			break;

		case "SUBMITTED":
			addClaimFormResponse = myClaimLogic.getSubmittedClaims(claimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
			break;

		case "APPROVED":
			addClaimFormResponse = myClaimLogic.getApprovedClaims(claimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
			break;

		case "REJECTED":
			addClaimFormResponse = myClaimLogic.getRejectedClaims(claimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
			break;
			
		case "WITHDRAWN":
			addClaimFormResponse = myClaimLogic.getWithdrawnClaims(claimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
			break;
		}

		if(addClaimFormResponse!=null) {
			return new ResponseEntity<>(addClaimFormResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "apply-my-claim")
	public ResponseEntity<?> applyMyClaim() {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		EmployeeClaimTemplateDataResponse employeeClaimTemplateDataResponse = addClaimLogic.getClaimTemplatesData(companyId, employeeId);
		if(employeeClaimTemplateDataResponse.getRows().size()!=0){
			return new ResponseEntity<>(employeeClaimTemplateDataResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "claim-template-items")
	public ResponseEntity<?> myClaimTemplateItems(@RequestParam(value = "employeeClaimTemplateId", required = true) Long employeeClaimTemplateId) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);

		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplateItemList(addClaimDTO);
		if(addClaimForm!=null){
			return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "claim-template-item-data")
	public ResponseEntity<?> viewClaimTemplateItemData(@RequestParam(value = "employeeClaimTemplateItemId", required = true) Long employeeClaimTemplateItemId,
			@RequestParam(value = "claimApplicationId") Long claimApplicationId, @RequestParam(value = "claimApplicationItemId") Long claimApplicationItemId) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		boolean hasLundinTimesheetModule = UserContext.isLundinTimesheetModule();

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		if(claimApplicationItemId == 0){
			addClaimDTO.setClaimApplicationItemId(claimApplicationItemId);
		}else{
			addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));
		}
		
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeClaimTemplateItemId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateItemId));
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setHasLundinTimesheetModule(hasLundinTimesheetModule);

		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplateItemConfigData(addClaimDTO);
		addClaimForm.setLundinTimesheetModule(hasLundinTimesheetModule);

		setError(addClaimForm, UserContext.getLocale());
		return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
	}
	
	@Override
	@DeleteMapping(value = "delete-claim-template-item")
	public ResponseEntity<?> deleteMyClaimTemplateItem(@RequestParam(value = "claimApplicationItemId") Long claimApplicationItemId) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);

		AddClaimForm addClaimForm = addClaimLogic.deleteClaimTemplateItem(addClaimDTO);
		
		if(addClaimForm != null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.my.claim.request.operation.success", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "employee-claim-item-data")
	public ResponseEntity<?> getEmployeeClaimItemData(@RequestParam(value = "claimApplicationItemId") Long claimApplicationItemId) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);

		AddClaimForm addClaimForm = addClaimLogic.getEmployeeItemData(addClaimDTO);

		if(addClaimForm!=null){
			return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PutMapping(value = "update-claim-template-item")
	public ResponseEntity<?> updateClaimTemplateItem(@RequestBody ClaimApplicationItemForm claimApplicationItemForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		boolean hasLundinTimesheetModule = UserContext.isLundinTimesheetModule();
		claimApplicationItemForm.setLundinTimesheetModule(hasLundinTimesheetModule);

		claimApplicationItemForm.setCustomFieldDTOList(claimApplicationItemForm.getCustomFields());
		AddClaimForm addClaimForm = addClaimLogic.updateClaimTemplateItem(claimApplicationItemForm, companyId);
		
		setError(addClaimForm, UserContext.getLocale());
		return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
	}
	
	@Override
	@PostMapping("save-claim-template-item")
	public ResponseEntity<?> saveClaimTemplateItem(@RequestBody ClaimApplicationItemForm claimApplicationItemForm) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);
		claimApplicationItemForm.setCustomFieldDTOList(claimApplicationItemForm.getCustomFields());
		boolean hasLundinTimesheetModule = UserContext.isLundinTimesheetModule();
		claimApplicationItemForm.setLundinTimesheetModule(hasLundinTimesheetModule);
		claimApplicationItemForm.setReceiptNumber(claimApplicationItemForm.getReceiptNumber()!=null?claimApplicationItemForm.getReceiptNumber():"");
		AddClaimForm addClaimForm = addClaimLogic.saveClaimTemplateItem(claimApplicationItemForm, companyId);

		if(addClaimForm==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
		setError(addClaimForm, UserContext.getLocale());
		return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "claim-application-item-list")
	public ResponseEntity<?> getClaimApplicationItemList(@RequestParam(value = "claimApplicationId") Long claimApplicationId) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));

		AddClaimForm addClaimForm = addClaimLogic.getClaimApplicationItemList(addClaimDTO);
		addClaimForm.setLundinTimesheetModule(UserContext.isLundinTimesheetModule());
		
		return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "lundin-block-list")
	public ResponseEntity<?> lundinEmpBlockListDropDown() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic.lundinBlockList(companyId);
		return new ResponseEntity<>(otBatchList, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "lundin-afe-list")
	public ResponseEntity<?> lundinAFEEmpListDropDown(@RequestParam(value = "blockId", required = false) Long blockId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic.lundinAFEList(companyId, blockId);
		if(!otBatchList.isEmpty() && otBatchList!=null){
			return new ResponseEntity<>(otBatchList, HttpStatus.OK);
		}
		else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
	}
	
	@Override
	@PostMapping("view-claim-application-data")
	public ResponseEntity<?> viewClaimApplicationData(@RequestParam(value = "claimApplicationId") Long claimApplicationId){
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		claimDTO.setLocale(UserContext.getLocale());
		claimDTO.setAdmin(false);
	
		AddClaimForm addClaimForm = myClaimLogic.getClaimAppDataMsgSource(claimDTO, messageSource);
		if (addClaimForm != null) {
			return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * @param This API is used to print the pending claim data.
	 */
	@Override
	@GetMapping("print-my-claim-application")
	public ResponseEntity<?> doPrintMyClaimData(@RequestParam(value="claimApplicationId", required = true) Long claimApplicationId){
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Map<String, Object> printClaimMap = new HashMap<String, Object>();
		
		boolean hasLundinTimesheetModule =UserContext.isLundinTimesheetModule();
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		claimDTO.setAdmin(false);
		claimDTO.setHasLundinTimesheetModule(hasLundinTimesheetModule);
	
		ClaimFormPdfDTO claimFormPdfDTO = myClaimLogic.generateClaimFormPrintPDF(claimDTO);
		
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
	
	@Override
	@PostMapping("save-as-draft")
	public ResponseEntity<?> saveAsDraftFromWithdraw(@RequestBody AddClaimForm addClaimForm) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));

		AddClaimForm addClaimFormRes = addClaimLogic.saveAsDraftFromWithdrawClaim(addClaimDTO, addClaimForm);

		if(addClaimFormRes==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
		
		setErrorClaimApplication(addClaimFormRes, UserContext.getLocale());
		return new ResponseEntity<>(addClaimFormRes, HttpStatus.OK);
	}
	
	@Override
	@PostMapping("save-claim-application")
	public ResponseEntity<?> getSaveOrCreateClaimApplication(@RequestParam(value = "employeeClaimTemplateId", required = true) Long employeeClaimTemplateId) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);

		AddClaimForm addClaimForm = addClaimLogic.saveClaimApplication(addClaimDTO);
		if(StringUtils.isNotBlank(addClaimForm.getMesssage()) && StringUtils.equalsIgnoreCase(addClaimForm.getMesssage(), "No such Employee Claim Template")){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.my.claim.employee.claim.template.no.data", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		if (addClaimForm.getClaimReviewerNotDefined()) {
			addClaimForm.setClaimApplicationStatus(false);
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.claim.reviewer.cannot.be.blank", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		} 
		else {
			addClaimForm.setClaimApplicationStatus(true);
		}
		
		setErrorClaimApplication(addClaimForm, UserContext.getLocale());
		
		if(addClaimForm.getValidationClaimItemDTO() != null && addClaimForm.getValidationClaimItemDTO().getErrorCode() == 1){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(addClaimForm.getValidateClaimApplicationDTO().getErrorValue(), new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
	}
	
	@Override
	@PostMapping("copy-claim-application")
	public ResponseEntity<?> copyClaimApplication(@RequestBody AddClaimForm addClaimForm) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));
		
		AddClaimForm addClaimFormRes = addClaimLogic.copyClaimApplication(addClaimDTO, addClaimForm);
		
		if(addClaimFormRes==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}

		setError(addClaimFormRes, UserContext.getLocale());

		return new ResponseEntity<>(addClaimFormRes, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value ="upload-claim-item-attachment", produces = "text/html; charset=utf-8")
	public ResponseEntity<?> uploadClaimItemAttachement(@RequestParam(value = "claimApplicationItemId") Long claimApplicationItemId, 
			@RequestBody ClaimApplicationItemAttach claimApplicationItemAttach) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		String fileName = claimApplicationItemAttach.getAttachment().getOriginalFilename();
		
		boolean isFileTypeValid  = FileUtils.isValidFile(claimApplicationItemAttach.getAttachment(), fileName, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);

		AddClaimForm addClaimForm = new AddClaimForm();
		addClaimForm.setDataValid(false);
		
		if (claimApplicationItemAttach.getAttachment() != null && claimApplicationItemAttach.getAttachment().getSize() == 0) {
			isFileTypeValid = false;
		}
		
		if (isFileTypeValid) {
			AddClaimDTO addClaimDTO = new AddClaimDTO();
			addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));			
			addClaimDTO.setCompanyId(companyId);
			addClaimDTO.setEmployeeId(employeeId);

			addClaimForm = addClaimLogic.uploadClaimItemAttachement(claimApplicationItemAttach, addClaimDTO);
			addClaimForm.setDataValid(true);
		}
		return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value ="upload-multiple-claim-item-attachment")
	public ResponseEntity<?> uploadMultipleClaimItemAttachement(@RequestParam(value = "claimApplicationItemId") Long claimApplicationItemId, 
			@RequestParam (value = "claimApplicationItemAttach") CommonsMultipartFile[] claimAppItemAttachments) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<ClaimApplicationItemAttach> listOfFile = new ArrayList<>();
		
		for(CommonsMultipartFile claimAppItemAttachment : claimAppItemAttachments) {
			ClaimApplicationItemAttach claimApplicationItemAttachObj = new ClaimApplicationItemAttach();
			claimApplicationItemAttachObj.setAttachment(claimAppItemAttachment);
			claimApplicationItemAttachObj.setFileName(claimAppItemAttachment.getOriginalFilename());
			boolean isFileTypeValid = FileUtils.isValidFile(claimAppItemAttachment, claimAppItemAttachment.getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			if(!isFileTypeValid){
				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.my.claim.invalid.file", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
			}
			listOfFile.add(claimApplicationItemAttachObj);
		}
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));			
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		
		for(ClaimApplicationItemAttach claimFileUpload : listOfFile) {
			AddClaimForm addClaimForm = addClaimLogic.uploadClaimItemAttachement(claimFileUpload, addClaimDTO);
			if(StringUtils.equalsIgnoreCase(addClaimForm.getStatus(), "FAILURE")){
				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.my.claim.application.item.file.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
			}
		}

		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.my.claim.files.uploaded.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
	}
	
	@Override
	@DeleteMapping("delete-claim-application-attachment")
	public ResponseEntity<?> deleteClaimApplicationAttachement(@RequestParam(value = "attachementId") Long attachementId) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationAttachementId(FormatPreserveCryptoUtil.decrypt(attachementId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);

		AddClaimForm addClaimForm = addClaimLogic.deleteClaimApplicationAttachement(addClaimDTO);

		if(addClaimForm != null){
			return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@DeleteMapping("delete-claim-application")
	public ResponseEntity<?> deleteClaimApplication(@RequestParam(value = "claimApplicationId", required = true) Long claimApplicationId) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);

		AddClaimForm addClaimForm = addClaimLogic.deleteClaimApplication(addClaimDTO);

		if(addClaimForm != null){
			return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
		}	
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping("persist-my-claim-application")
	public ResponseEntity<?> persistMyClaimApplication(@RequestBody AddClaimForm addClaimForm) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimForm addClaimFormRes = null;

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));
		addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));
		addClaimFormRes = addClaimLogic.persistClaimApplication(addClaimDTO, addClaimForm);

		if(addClaimFormRes==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		
		setErrorClaimApplication(addClaimFormRes, UserContext.getLocale());
		return new ResponseEntity<>(addClaimFormRes, HttpStatus.OK);
	}
	
	@Override
	@PostMapping("withdraw-claim")
	public ResponseEntity<?> withdrawClaim(@RequestBody AddClaimForm addClaimForm) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);

		String status = addClaimLogic.withdrawClaim(addClaimDTO);
		
		if(StringUtils.equalsIgnoreCase(status, "SUCCESS")){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.my.claim.request.operation.success", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		else if(StringUtils.equalsIgnoreCase(status, "NO CLAIM APPLICATION")){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.my.claim.request.operation.fail", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping("claim-batch-configuration")
	public ResponseEntity<?> getClaimBatchConf() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		ClaimBatchForm claimBatchForm = claimBatchLogic.getClaimBatchConf(companyId);
		return new ResponseEntity<>(claimBatchForm, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "claim-preferences")
	public ResponseEntity<?> getClaimPreferences() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		ClaimPreferenceForm claimPreferenceForm = myClaimLogic.getClaimPreferences(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(claimPreferenceForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@PostMapping("forex-rate")
	public ResponseEntity<?> getForexRate(@RequestParam(value = "currencyDate", required = true) String currencyDate, @RequestParam(value = "currencyId", required = true) Long currencyId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimForm addClaimForm = addClaimLogic.getForexRate(DateUtils.stringToDate(currencyDate), currencyId, companyId);
		return new ResponseEntity<>(addClaimForm, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "claim-template-config")
	public ResponseEntity<?> getLoggedinEmpTemplates() {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplates(companyId, employeeId, false);

		return new ResponseEntity<>(addClaimForm, HttpStatus.OK); 
	}
	
	private AddClaimDTO setClaimData(SearchParam searchParamObj) {
		int size = 0;
		int idx;
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setLocale(UserContext.getLocale());
		claimDTO.setSearchCondition("");
		final List<Filters> filterlist = Arrays.asList(searchParamObj.getFilters());
		if (filterlist != null && !filterlist.isEmpty()) {
			List<String> fieldList = new ArrayList<>();
			for(int i=0; i<filterlist.size(); i++){
				fieldList.add(filterlist.get(i).getField());
			}
			if (fieldList.contains("fromDate")) {
				size = size + 1;
				idx = fieldList.indexOf("fromDate");
				claimDTO.setFromDate(filterlist.get(idx).getValue());
			}
			if (fieldList.contains("toDate")) {
				size = size + 1;
				idx = fieldList.indexOf("toDate");
				claimDTO.setToDate(filterlist.get(idx).getValue());
			}
			if (filterlist.size() > size) {
				if (fieldList.contains("claimGroup")) {
					idx = fieldList.indexOf("claimGroup");
					claimDTO.setSearchCondition(filterlist.get(idx).getField());
					claimDTO.setSearchText(filterlist.get(idx).getValue());
				} else if (fieldList.contains("claimNumber")) {
					idx = fieldList.indexOf("claimNumber");
					claimDTO.setSearchCondition(filterlist.get(idx).getField());
					claimDTO.setSearchText(filterlist.get(idx).getValue());
				} else if (fieldList.contains("createdDate")) {
					idx = fieldList.indexOf("createdDate");
					claimDTO.setSearchCondition(filterlist.get(idx).getField());
					claimDTO.setSearchText(filterlist.get(idx).getValue());
				}
			}
		}
		return claimDTO;
	}
	
	private void setError(AddClaimForm addClaimForm, Locale locale) {

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addClaimForm.getValidationClaimItemDTO() != null
				&& addClaimForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addClaimForm.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = addClaimForm.getValidationClaimItemDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(addClaimForm.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = addClaimForm.getValidationClaimItemDTO().getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if(count>0) {
						errorKeyFinalStr.append("#");
					}
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0 && StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
						}
						errorKeyFinalStr.append(messageSource.getMessage(errorKeyArr[count], errorVal, locale));
					}
				}
			}
			addClaimForm.getValidationClaimItemDTO().setErrorValue(errorKeyFinalStr.toString());
		}
		else if (addClaimForm.getClaimItemBalanceDTO() != null) {
			if(StringUtils.isNotBlank(addClaimForm.getClaimItemBalanceDTO().getEntitlementType())) {
				addClaimForm.getClaimItemBalanceDTO().setEntitlementType(messageSource.getMessage(addClaimForm.getClaimItemBalanceDTO().getEntitlementType(), new Object[] {}, UserContext.getLocale()));
			}
			if(StringUtils.isNotBlank(addClaimForm.getClaimItemBalanceDTO().getErrorKey())) {
				addClaimForm.getClaimItemBalanceDTO().setErrorValue(messageSource.getMessage(addClaimForm.getClaimItemBalanceDTO().getErrorKey(), new Object[] {}, UserContext.getLocale()));
			}
		}
	}
	
	private void setErrorClaimApplication(AddClaimForm addClaimForm, Locale locale) {

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		
		if (addClaimForm.getValidateClaimApplicationDTO() != null && addClaimForm.getValidateClaimApplicationDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addClaimForm.getValidateClaimApplicationDTO().getErrorKey())) {
				errorKeyArr = addClaimForm.getValidateClaimApplicationDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(addClaimForm.getValidateClaimApplicationDTO().getErrorValue())) {
					errorValArr = addClaimForm.getValidateClaimApplicationDTO().getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if(count>0) {
						errorKeyFinalStr.append("#");
					}
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0 && StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
						}
						errorKeyFinalStr.append(messageSource.getMessage(errorKeyArr[count], errorVal, locale));
					}
				}
			}
			addClaimForm.getValidateClaimApplicationDTO().setErrorKey(errorKeyFinalStr.toString());
		}
		else if (addClaimForm.getClaimItemBalanceDTO() != null && addClaimForm.getClaimItemBalanceDTO().getEntitlementType() != null) {
			if(StringUtils.isNotBlank(addClaimForm.getClaimItemBalanceDTO().getEntitlementType())) {
				addClaimForm.getClaimItemBalanceDTO().setEntitlementType(messageSource.getMessage(addClaimForm.getClaimItemBalanceDTO().getEntitlementType(), new Object[] {}, UserContext.getLocale()));
			}
			if(StringUtils.isNotBlank(addClaimForm.getClaimItemBalanceDTO().getErrorKey())) {
				addClaimForm.getClaimItemBalanceDTO().setErrorValue(messageSource.getMessage(addClaimForm.getClaimItemBalanceDTO().getErrorKey(), new Object[] {}, UserContext.getLocale()));
			}
		}
	}
	
}
