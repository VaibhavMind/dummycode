package com.payasia.api.leave.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.leave.EmployeeLeavePendingApi;
import com.payasia.api.leave.model.Filters;
import com.payasia.api.leave.model.MultiSortMeta;
import com.payasia.api.leave.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationPdfDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.PendingItemsFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmployeeDAO;
import com.payasia.logic.PendingItemsLogic;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
@RequestMapping(value=ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE+"/leave/")
@RestController
public class EmployeeLeavePendingApiImpl implements EmployeeLeavePendingApi {
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeLeavePendingApiImpl.class);
	@Resource
	private PendingItemsLogic pendingItemsLogic;
	@Resource
	private EmployeeDAO employeeDAO;
	@Autowired
	private MessageSource messageSource;
	
	@Override
	@PostMapping(value = "view-pending-items")
	public ResponseEntity<?> getPendingLeaves(@RequestBody SearchParam searchParamObj,@RequestParam(value="requestType",required = true) String requestType) { 		
		Long loginEmployeeID=Long.valueOf(UserContext.getUserId());
		 String filterStr1="";
         String filterStr2="";
         
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParamObj.getSortField());
		sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParamObj.getPage());
		pageDTO.setPageSize(searchParamObj.getRows());	
		PendingItemsFormResponse pendingItemsResponse = null;
		 if (multisortlist != null && !multisortlist.isEmpty()) {
				sortDTO.setColumnName(multisortlist.get(0).getField());
				sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
			 }
			if(filterllist != null && !filterllist.isEmpty()){
			     filterStr1=filterllist.get(0).getField();
                 filterStr2=filterllist.get(0).getValue();
             }
				
				pendingItemsResponse = pendingItemsLogic.getPendingLeaves(loginEmployeeID,
						pageDTO, sortDTO, requestType, filterStr1,filterStr2);
				if(pendingItemsResponse.getRows().isEmpty()){
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
				}
				List<PendingItemsForm> pendingItemsFormList = pendingItemsResponse
						.getRows();
				for (PendingItemsForm pendingItemsForm : pendingItemsFormList) {
					if (StringUtils.isNotBlank(pendingItemsForm.getStatus())) {
						pendingItemsForm.setStatus(messageSource.getMessage(
								pendingItemsForm.getStatus(), new Object[] {}, UserContext.getLocale()));
					}
				}
			
			return new ResponseEntity<>(pendingItemsResponse , HttpStatus.OK);
	}
	@Override
	@PostMapping(value="view-pending-data-leave-review")
	public ResponseEntity<?> getDataForLeaveReviewEmp(@RequestBody String jsonStr){
		
		JsonConfig jsonConfig= new JsonConfig();
		JSONObject jsonObject=JSONObject.fromObject(jsonStr, jsonConfig);
		Long reviewId=FormatPreserveCryptoUtil.decrypt(jsonObject.getLong("reviewId"));
		Long employeeId = Long.parseLong(UserContext.getUserId());
		PendingItemsForm pendingItemsForm = pendingItemsLogic.getDataForLeaveReviewEmp(reviewId, employeeId);
		AddLeaveForm addLeaveForm = pendingItemsForm.getAddLeaveForm();
		if (StringUtils.isNotBlank(addLeaveForm.getLeaveAppStatus())) {
			addLeaveForm.setLeaveAppStatus(messageSource.getMessage(
					addLeaveForm.getLeaveAppStatus(), new Object[] {}, UserContext.getLocale()));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getStatus())) {
			addLeaveForm.setStatus(messageSource.getMessage(
					addLeaveForm.getStatus(), new Object[] {}, UserContext.getLocale()));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getFromSessionLabelKey())) {
			addLeaveForm.setFromSession(messageSource.getMessage(
					addLeaveForm.getFromSessionLabelKey(), new Object[] {},
					UserContext.getLocale()));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getToSessionLabelKey())) {
			addLeaveForm.setToSession(messageSource.getMessage(
					addLeaveForm.getToSessionLabelKey(), new Object[] {},
					UserContext.getLocale()));
		}
		List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm
				.getWorkflowList();
		for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
				leaveApplicationWorkflowDTO.setStatus(messageSource.getMessage(
						leaveApplicationWorkflowDTO.getStatus(),
						new Object[] {}, UserContext.getLocale()));
			}
		}
		return  new ResponseEntity<>(pendingItemsForm , HttpStatus.OK);
	}
	@Override
	@PostMapping(value = "view-pending-item-view-multiple-leave")
	public ResponseEntity<?> viewMultipleLeaveApplicationsEmp(@RequestBody String jsonStr, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		UserContext.setLocale(locale);
		JsonConfig jsonConfig= new JsonConfig();
		JSONObject jsonObject=JSONObject.fromObject(jsonStr, jsonConfig);
		JSONArray values =jsonObject.getJSONArray("leaveApplicationRevIds");
		String leaveApplicationRevId[] = new String[values.size()];
		for(int i=0;i<values.size();i++)
		{
			 JSONObject jsonObj = values.getJSONObject(i);
		   Long leaveApplicationId= Long.valueOf(jsonObj.getString("leaveApplicationRevId"));
			String leaveApplicationIdStr =String.valueOf(FormatPreserveCryptoUtil.decrypt(leaveApplicationId));
			 leaveApplicationRevId[i]=leaveApplicationIdStr;
		}
		PendingItemsFormResponse pendingItemsFormResponse = pendingItemsLogic.viewMultipleLeaveApplications(companyId,
				employeeId, leaveApplicationRevId);
		if(pendingItemsFormResponse.getRows().isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item.multiple.leave", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(pendingItemsFormResponse,HttpStatus.OK);
	}
	@Override
	@PostMapping("view-pending-item-review-multiple-leave")
	public ResponseEntity<?> reviewMultipleLeaveApp(@RequestBody PendingItemsForm pendingItemsFrm, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,new Object[] {}, locale));
		sessionDTO.setToSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,new Object[] {}, locale));
		List<AddLeaveForm> addLeaveFormRes = pendingItemsLogic.reviewMultipleLeaveApp(pendingItemsFrm, employeeId, companyId,sessionDTO);
		if(addLeaveFormRes.isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item.review.multiple.leave", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		for (AddLeaveForm addLeaveForm : addLeaveFormRes) {
			String[] errorValArr = null;
			String[] errorVal = null;
			StringBuilder errorKeyFinalStr = new StringBuilder();
			String[] errorKeyArr;
			if (addLeaveForm.getLeaveDTO() != null
					&& addLeaveForm.getLeaveDTO().getErrorCode() == 1) {
				if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO()
						.getErrorKey())) {
					errorKeyArr = addLeaveForm.getLeaveDTO().getErrorKey()
							.split(";");
					if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO()
							.getErrorValue())) {
						errorValArr = addLeaveForm.getLeaveDTO()
								.getErrorValue().split(";");
					}

					for (int count = 0; count < errorKeyArr.length; count++) {
						if (StringUtils.isNotBlank(errorKeyArr[count])) {
							if (errorValArr.length > 0) {
								if (StringUtils.isNotBlank(errorValArr[count])) {
									errorVal = errorValArr[count].split(",");
								}
							}
							errorKeyFinalStr.append(messageSource.getMessage(
									errorKeyArr[count], errorVal, locale)
									);
						}

					}

				}
				addLeaveForm.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
			}
		}
		return new ResponseEntity<>(addLeaveFormRes,HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "view-pending-item-employees-on-leave")
	public ResponseEntity<?> getEmployeesOnLeavesEmp(@RequestBody SearchParam searchParamObj) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
				
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		String arg1 = null;
		String arg2 = null;
		
		Long leaveApplicationId=FormatPreserveCryptoUtil.decrypt(Long.parseLong(filterllist.get(0).getValue()));
		
		PendingItemsFormResponse pendingItemsResponse = null;
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParamObj.getPage());
		pageDTO.setPageSize(searchParamObj.getRows());
		
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParamObj.getSortField());
		sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		
		if (multisortlist != null && !multisortlist.isEmpty()) {
			sortDTO.setColumnName(multisortlist.get(0).getField());
			sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}
		
		if ((filterllist.size()>2) && (StringUtils.equalsIgnoreCase(filterllist.get(1).getField(), "fromDate"))) {
			arg1 = filterllist.get(1).getValue();
			arg2 = filterllist.get(2).getValue();
		}
		else {
			arg1 = filterllist.get(2).getValue();
			arg2 = filterllist.get(1).getValue();
		}
		
		pendingItemsResponse = pendingItemsLogic.getEmployeesOnLeave(arg1, arg2, leaveApplicationId, empId, companyId, pageDTO, sortDTO);
		if (pendingItemsResponse != null) {
			return new ResponseEntity<>(pendingItemsResponse, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item.employees.on.leave", new Object[] {}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "view-employee-pending-items-transaction")
	public ResponseEntity<?> viewLeaveTransactionsEmp(@RequestBody SearchParam searchParamObj) {
		
		
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		
		Long leaveApplicationId=FormatPreserveCryptoUtil.decrypt(Long.parseLong(filterllist.get(0).getValue()));
		Long createdById=FormatPreserveCryptoUtil.decrypt(Long.parseLong(filterllist.get(1).getValue()));
		
		PendingItemsFormResponse pendingItemsResponse = null;
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParamObj.getPage());
		pageDTO.setPageSize(searchParamObj.getRows());
		
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParamObj.getSortField());
		sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		
		if (multisortlist != null && !multisortlist.isEmpty()) {
			sortDTO.setColumnName(multisortlist.get(0).getField());
			sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}
		
		if ((filterllist.size()>2)) {
			if( filterllist.get(2).getValue()!= null  && filterllist.get(3).getValue() != null ){
				pendingItemsResponse = pendingItemsLogic.getLeaveTransactions(createdById, empId, pageDTO, sortDTO, companyId,leaveApplicationId, filterllist.get(2).getValue(), filterllist.get(3).getValue());
			}
			else if(filterllist.get(2).getValue()!= null){
				pendingItemsResponse = pendingItemsLogic.getLeaveTransactions(createdById, empId, pageDTO, sortDTO, companyId,leaveApplicationId, filterllist.get(2).getValue(), null);
			}
			else if(filterllist.get(3).getValue()!= null){
				pendingItemsResponse = pendingItemsLogic.getLeaveTransactions(createdById, empId, pageDTO, sortDTO, companyId,leaveApplicationId, null, filterllist.get(3).getValue());
			}
			else {
				pendingItemsResponse = pendingItemsLogic.getLeaveTransactions(createdById, empId, pageDTO, sortDTO, companyId,leaveApplicationId, null, null);
			}
		} 
		
		if(pendingItemsResponse.getEmployeeLeaveDTOs().isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item.employees.on.leave", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(pendingItemsResponse,HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "view-accept-pending-item")
	public ResponseEntity<?> acceptLeave(@RequestBody PendingItemsForm pendingItemsForm, Locale locale) {
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getForwardToId()));
		Long employeeId = Long.parseLong(UserContext.getUserId());
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));
		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));
		try {
			addLeaveFormRes = pendingItemsLogic.acceptLeave(pendingItemsForm,employeeId, sessionDTO);
			
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.LEAVE_APPLICATION_ALREADY_APPROVED);
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
			
		}
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null
				&& addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue()
							.split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr.append(messageSource.getMessage(
								errorKeyArr[count], errorVal, locale)
								);
					}

				}

			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(addLeaveFormRes.getLeaveDTO().getErrorKey());
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
			return new ResponseEntity<>(addLeaveFormRes,HttpStatus.OK);
				
		}
	
		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setErrorCode(0);
		leaveDTO.setErrorKey(messageSource.getMessage(PayAsiaConstants.PAYASIA_APPROVED, errorVal, locale));
		leaveDTO.setErrorValue(" ;");
		addLeaveFormRes.setLeaveDTO(leaveDTO);
		return new ResponseEntity<>(addLeaveFormRes,HttpStatus.OK);
	}
	@Override
	@PostMapping(value = "view-reject-pending-items")
	public ResponseEntity<?> rejectLeave(@RequestBody PendingItemsForm pendingItemsForm, Locale locale) {
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getForwardToId()));
		Long employeeId = Long.parseLong(UserContext.getUserId());
		String status = "true";
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));
		
		
		status = pendingItemsLogic.rejectLeave(pendingItemsForm,
				employeeId, sessionDTO);
		if(status.equalsIgnoreCase("true")){
			return new ResponseEntity<>(status,HttpStatus.OK);
		}
		
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
	}
	@Override
	@PostMapping(value = "view-pending-items-forward-leave")
	public ResponseEntity<?> forwardLeaveEmp(@RequestBody PendingItemsForm pendingItemsForm, Locale locale) {
	
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,new Object[] {}, locale));
		sessionDTO.setToSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY, new Object[] {}, locale));
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		try {
			addLeaveFormRes = pendingItemsLogic.forwardLeave(pendingItemsForm, employeeId, sessionDTO);
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(0);
			leaveDTO.setErrorKey(messageSource.getMessage(PayAsiaConstants.PAYASIA_FORWARDED, errorVal, locale));
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey("payasia.leave.accept.error");
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		}
		
		if (addLeaveFormRes.getLeaveDTO() != null && addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO().getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO().getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr
								.append(messageSource.getMessage(errorKeyArr[count], errorVal, locale));
					}

				}

			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(errorKeyFinalStr.toString());
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
			return new ResponseEntity<>(addLeaveFormRes,HttpStatus.OK);
		}
	
		return new ResponseEntity<>(addLeaveFormRes,HttpStatus.OK);
	}
	@Override
	@PostMapping(value = "view-pending-items-print-leaveapplication")
	public ResponseEntity<?> printLeaveApplicationFormEmp(@RequestBody String strJson) throws DocumentException, IOException, JAXBException, SAXException {
	
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(strJson, jsonConfig);
		long leaveApplicationId=FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveApplicationReviewerIds"));
		LeaveApplicationPdfDTO leaveAppPdfDTO = pendingItemsLogic.generateLeaveApplicationPrintPDF(companyId,
				empId, leaveApplicationId);
		Map<String, Object> printDataMap = new HashMap<>();
		
			if(leaveAppPdfDTO.getLeaveAppPdfByteFile()==null){
					 return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.app.print.error", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
			}
			printDataMap.put("printLeavebody", leaveAppPdfDTO.getLeaveAppPdfByteFile());
			printDataMap.put("pdfname", leaveAppPdfDTO.getEmployeeNumber() + "_" + leaveAppPdfDTO.getLeaveSchemeName() + ".pdf");
		return new ResponseEntity<>(printDataMap, HttpStatus.OK);
	}
	@Override
	@PostMapping(value = "pending-items-search-employee")
	public ResponseEntity<?> searchEmployee(@RequestBody String strJson) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig=new JsonConfig();
		JSONObject jsonObject=JSONObject.fromObject(strJson,jsonConfig);
		String columnName=jsonObject.getString("sidx");
		int page=jsonObject.getInt("page");
		int row=jsonObject.getInt("row");
		String sortingType=jsonObject.getString("sord");
		String empName=jsonObject.getString("empName");
		String empNumber=jsonObject.getString("empNumber");
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(row);
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveReviewerResponseForm leaveReviewerResponse = pendingItemsLogic.searchEmployee(pageDTO, sortDTO, employeeId,
				empName, empNumber, companyId);
		if(leaveReviewerResponse.getSearchEmployeeList().isEmpty())
		{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item.search.employee", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(leaveReviewerResponse,HttpStatus.OK);
	}
	@Override
	@PostMapping(value = "view-pending-items-employee-workflowstatus")
	public ResponseEntity<?> showEmpLeaveWorkflowStatusEmp(@RequestBody String strJson,Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig=new JsonConfig();
		JSONObject jsonObject=JSONObject.fromObject(strJson,jsonConfig);
		Long leaveApplicationId =FormatPreserveCryptoUtil.decrypt(jsonObject.getLong("leaveApplicationId"));
		PendingItemsForm pendingItemResponse = pendingItemsLogic.showEmpLeaveWorkflowStatus(companyId, employeeId,
				leaveApplicationId);
		AddLeaveForm addLeaveForm = pendingItemResponse.getAddLeaveForm();
		if (StringUtils.isNotBlank(addLeaveForm.getLeaveAppStatus())) {
			addLeaveForm.setLeaveAppStatus(
					messageSource.getMessage(addLeaveForm.getLeaveAppStatus(), new Object[] {}, locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getStatus())) {
			addLeaveForm.setStatus(messageSource.getMessage(addLeaveForm.getStatus(), new Object[] {}, locale));
		}
		List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm.getWorkflowList();
		for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
				leaveApplicationWorkflowDTO.setStatus(
						messageSource.getMessage(leaveApplicationWorkflowDTO.getStatus(), new Object[] {}, locale));
			}
		}
		return new ResponseEntity<>(pendingItemResponse,HttpStatus.OK);
	}
	@Override
	@PostMapping(value = "pending-leave-reviewer")
	public ResponseEntity<?> showEmpLeaveWorkflowStatusEmpForMobile(@RequestBody String strJson,Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig=new JsonConfig();
		JSONObject jsonObject=JSONObject.fromObject(strJson,jsonConfig);
		Long leaveApplicationId =FormatPreserveCryptoUtil.decrypt(jsonObject.getLong("leaveApplicationId"));
		PendingItemsForm pendingItemResponse = pendingItemsLogic.showEmpLeaveWorkflowStatusForMobile(companyId, employeeId,leaveApplicationId);
		AddLeaveForm addLeaveForm = pendingItemResponse.getAddLeaveForm();
	    if(addLeaveForm!=null){
			List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm.getWorkflowList();
			for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
				if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
					leaveApplicationWorkflowDTO.setStatus(
							messageSource.getMessage(leaveApplicationWorkflowDTO.getStatus(), new Object[] {}, locale));
				}
			}
		}
		return new ResponseEntity<>(pendingItemResponse,HttpStatus.OK);
	}
	@Override
	@PostMapping(value = "view-pending-Item-attachmentrev")
	public ResponseEntity<?> viewAttachmentRev(@RequestBody String strJson) throws DocumentException, IOException, JAXBException, SAXException {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig=new JsonConfig();
		JSONObject jsonObject=JSONObject.fromObject(strJson,jsonConfig);
		Long attachmentId =jsonObject.getLong("attachmentId");
		LeaveApplicationAttachmentDTO attachmentDTO = pendingItemsLogic.viewAttachmentByReviewer(attachmentId,
				employeeId, companyId);
		if(attachmentDTO.getAttachmentBytes()==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item.attachmentrev", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		
		Map<String, Object> printDataMap = new HashMap<>();
		printDataMap.put("downloadLeavebody", attachmentDTO.getAttachmentBytes());
		printDataMap.put("pdfname", attachmentDTO.getFileName() + ".pdf");
	    return new ResponseEntity<>(printDataMap, HttpStatus.OK);
			
	}
	
	@Override
	@PostMapping("view-pending-item-review-multiple-leave-approved-forwarded")
	public ResponseEntity<?> reviewMultipleLeaveApproveandForward(@RequestBody PendingItemsForm pendingItemsFrm, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,new Object[] {}, locale));
		sessionDTO.setToSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,new Object[] {}, locale));
		List<AddLeaveForm> addLeaveFormRes = pendingItemsLogic.reviewMultipleLeaveApproveandForward(pendingItemsFrm, employeeId, companyId,sessionDTO);
		if(addLeaveFormRes.isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.pending.item.review.multiple.leave", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		for (AddLeaveForm addLeaveForm : addLeaveFormRes) {
			String[] errorValArr = null;
			String[] errorVal = null;
			StringBuilder errorKeyFinalStr = new StringBuilder();
			String[] errorKeyArr;
			if (addLeaveForm.getLeaveDTO() != null
					&& addLeaveForm.getLeaveDTO().getErrorCode() == 1) {
				if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO()
						.getErrorKey())) {
					errorKeyArr = addLeaveForm.getLeaveDTO().getErrorKey()
							.split(";");
					if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO()
							.getErrorValue())) {
						errorValArr = addLeaveForm.getLeaveDTO()
								.getErrorValue().split(";");
					}

					for (int count = 0; count < errorKeyArr.length; count++) {
						if (StringUtils.isNotBlank(errorKeyArr[count])) {
							if (errorValArr.length > 0) {
								if (StringUtils.isNotBlank(errorValArr[count])) {
									errorVal = errorValArr[count].split(",");
								}
							}
							errorKeyFinalStr.append(messageSource.getMessage(
									errorKeyArr[count], errorVal, locale)
									);
						}

					}

				}
				addLeaveForm.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
			}
		}
		return new ResponseEntity<>(addLeaveFormRes,HttpStatus.OK);
	}
}