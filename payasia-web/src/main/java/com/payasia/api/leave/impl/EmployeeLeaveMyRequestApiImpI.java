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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.payasia.api.leave.EmployeeLeaveMyRequestApi;
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
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value=ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE+"/leave/")
public class EmployeeLeaveMyRequestApiImpI implements EmployeeLeaveMyRequestApi {

	static final Log log = LogFactory.getLog("API_LOG");
	
	@Autowired
	private MessageSource messageSource;
	
	@Resource
	private AddLeaveLogic addLeaveLogic;
	
	@Resource
	private EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	
	@Autowired
	private LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	
	@Override
	@PostMapping(value="view-pending-leave")
	public ResponseEntity<?> getDataForPendingLeave(@RequestBody String strJson) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(strJson, jsonConfig);
        Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long leaveApplicationId=FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveApplicationId"));
		AddLeaveForm addLeaveForm = null;
		if(leaveApplicationId != null){
		   addLeaveForm = addLeaveLogic.getDataForPendingLeave(leaveApplicationId, empId, companyId);
		   return new ResponseEntity<>(addLeaveForm , HttpStatus.OK);
		}else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(),messageSource.getMessage("payasia.leave.view.request", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		
	}
	
	@Override
	@PostMapping(value = "delete-leave")
	public ResponseEntity<?> deleteLeave(@RequestBody String strJson) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(strJson, jsonConfig);
		long leaveApplicationId=FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveApplicationId"));
		
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		addLeaveLogic.deleteLeave(leaveApplicationId,empId,companyId);
		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),messageSource.getMessage("payasia.leave.delete.success", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.OK);
		
	}

	@Override
    @PostMapping(value="view-leave")
	public ResponseEntity<?> viewLeave(@RequestBody String strJson) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(strJson, jsonConfig);
		long leaveApplicationId= FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveApplicationId"));
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveForm addLeaveForm = null;
		addLeaveForm = addLeaveLogic.viewLeave(leaveApplicationId,empId,companyId);
		if (StringUtils.isNotBlank(addLeaveForm.getLeaveAppStatus())) {
			addLeaveForm.setLeaveAppStatus(messageSource.getMessage(
					addLeaveForm.getLeaveAppStatus(), new Object[] {}, UserContext.getLocale()));
		}
		List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm.getWorkflowList();
		for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
				leaveApplicationWorkflowDTO.setStatus(messageSource.getMessage(
						leaveApplicationWorkflowDTO.getStatus(),
						new Object[] {}, UserContext.getLocale()));
			}
		}
		addLeaveForm.setLeaveDTO(addLeaveForm.getLeaveDTO()!=null ? addLeaveForm.getLeaveDTO() : new LeaveDTO());
		return new ResponseEntity<>(addLeaveForm , HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "withdraw-leave")
	public ResponseEntity<?> withdrawLeave(@RequestBody String strJson) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(strJson, jsonConfig);
		long leaveApplicationId=FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveApplicationId"));
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, UserContext.getLocale()));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, UserContext.getLocale()));
		String response=addLeaveLogic.withdrawLeave(leaveApplicationId,empId, sessionDTO, companyId);
		if(response.equalsIgnoreCase("SUCCESS")){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),messageSource.getMessage("payasia.leave.withdraw.success", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.OK);
		}
		else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.CONFLICT.toString(),messageSource.getMessage("payasia.leave.withdraw.error", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.CONFLICT);
		}
	}
	@Override
	@PostMapping(value="extension-leave")
	public ResponseEntity<?> extensionLeave(@RequestBody String strJson, Locale locale) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(strJson, jsonConfig);
		long leaveApplicationId=FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveApplicationId"));
		Long empId =Long.parseLong(UserContext.getUserId());
		Long companyId =Long.parseLong(UserContext.getWorkingCompanyId());
		String response = addLeaveLogic.extensionLeaveViewAdmin(leaveApplicationId, empId, companyId);
		if(StringUtils.isEmpty(response)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.CONFLICT.toString(),messageSource.getMessage("payasia.leave.extension.error", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(response , HttpStatus.OK);
	}

	@Override
    @PostMapping(value="view-request-leave")
    public ResponseEntity<?> viewLeave(@RequestBody SearchParam searchParamObj,@RequestParam(value="requestType",required = true) String requestType) {

           String filterStr1="";
           String filterStr2="";
           String requestTypeStr=requestType.toUpperCase();
           Long employeeId = Long.parseLong(UserContext.getUserId());
           Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
           final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
           final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
           SortCondition sortDTO = new SortCondition();
   			sortDTO.setColumnName(searchParamObj.getSortField());
   			sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
   			PageRequest pageDTO = new PageRequest();
   			pageDTO.setPageNumber(searchParamObj.getPage());
   			pageDTO.setPageSize(searchParamObj.getRows());
           String fromDate="";
           String toDate="";
           String pageContext="";
              if(multisortlist != null && !multisortlist.isEmpty()) {
                        sortDTO.setColumnName(multisortlist.get(0).getField());
                        sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
                  }
                  if(filterllist != null && !filterllist.isEmpty())
                  {
                        filterStr1=filterllist.get(0).getField();
                        filterStr2=filterllist.get(0).getValue();
                  }
                  AddLeaveFormResponse addLeaveFormResponse = null;
                  switch (requestTypeStr) {
                  case "DRAFT":
                        addLeaveFormResponse = addLeaveLogic.getPendingLeaves(fromDate,toDate,employeeId,pageDTO,sortDTO,filterStr1,filterStr2,companyId);
                        break;
                  case "SUBMITTED":
                        addLeaveFormResponse = addLeaveLogic.getSubmittedLeaves(fromDate,toDate,
                                      employeeId, pageDTO, sortDTO, pageContext,filterStr1,filterStr2,companyId);
                        break;
                  case "SUBMITTEDCANCEL":
                        addLeaveFormResponse = addLeaveLogic.getSubmittedCancelledLeaves(fromDate,toDate,
                                      employeeId, pageDTO, sortDTO, pageContext,filterStr1,filterStr2,companyId);
                        break;
                  case "APPROVED":
                        addLeaveFormResponse = addLeaveLogic.getCompletedLeaves(fromDate,toDate,
                                      employeeId, pageDTO, sortDTO, pageContext,filterStr1,filterStr2, companyId);
                        break;
                  case "APPROVEDCANCEL":
                        addLeaveFormResponse = addLeaveLogic.getCompletedCancelLeaves(fromDate,toDate,
                                      employeeId, pageDTO, sortDTO, pageContext,filterStr1,filterStr2,companyId);
                        break;
                  case "WITHDRAWN":
                        addLeaveFormResponse = addLeaveLogic.getWithDrawnLeaves(fromDate,toDate,
                                      employeeId, pageDTO, sortDTO, pageContext,filterStr1,filterStr2,companyId);
                        break;
                  case "REJECTED":
                        addLeaveFormResponse = addLeaveLogic.getRejectedLeaves(fromDate,toDate,
                                      employeeId, pageDTO, sortDTO, pageContext,filterStr1,filterStr2,companyId);
                        break;
                  case "ALL":
                      addLeaveFormResponse = addLeaveLogic.getAllLeaveRequestData(fromDate, toDate, employeeId, pageDTO, sortDTO, pageContext, filterStr1, filterStr2,companyId);
                        break;
                  default:
                        requestType = "NONE";
                  }
                  if (addLeaveFormResponse == null || requestType.equals("NONE")) {
                         return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.view.request", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
                  } else {
                        Map<String, AddLeaveFormResponse> leaveReuestMap = new HashMap<>();
                        leaveReuestMap.put(requestType, addLeaveFormResponse);
                        return new ResponseEntity<>(addLeaveFormResponse, HttpStatus.OK);
                 }
    }
	
	
	@Override
	@PostMapping(value = "print-leave-application")
	public ResponseEntity<?> doPrintLeave(@RequestBody String strJson)
			throws DocumentException, IOException, JAXBException, SAXException {

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(strJson, jsonConfig);
		long leaveApplicationId=FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveApplicationId"));
		LeaveApplicationPdfDTO leaveAppPdfDTO = null;
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		Map<String, Object> printDataMap = new HashMap<>();
			leaveAppPdfDTO = addLeaveLogic
					.generateLeaveApplicationPrintPDF(companyId, employeeId,
							leaveApplicationId);
		if(leaveAppPdfDTO.getLeaveAppPdfByteFile()==null){
				 return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.app.print.error", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		printDataMap.put("printLeavebody", leaveAppPdfDTO.getLeaveAppPdfByteFile());
		printDataMap.put("pdfname", leaveAppPdfDTO.getEmployeeNumber() + "_" + leaveAppPdfDTO.getLeaveSchemeName() + ".pdf");
		return new ResponseEntity<>(printDataMap, HttpStatus.OK);

	}
	
	@Override
	@PostMapping(value = "view-attachment")
	public ResponseEntity<?> viewAttachment(@RequestBody String strJson) throws DocumentException, IOException, JAXBException, SAXException {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig=new JsonConfig();
		JSONObject jsonObject=JSONObject.fromObject(strJson,jsonConfig);
		Long attachmentId = FormatPreserveCryptoUtil.decrypt(jsonObject.getLong("attachmentId"));
		LeaveApplicationAttachmentDTO attachmentDTO = addLeaveLogic.viewAttachment(attachmentId,employeeId, companyId);
		Map<String, Object> printDataMap = new HashMap<>();
		if(attachmentDTO!=null){
		  printDataMap.put("downloadLeavebody", attachmentDTO.getAttachmentBytes());
		  printDataMap.put("pdfname", attachmentDTO.getFileName());
		}
	    return new ResponseEntity<>(printDataMap, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value="view-custom-fields")
	public ResponseEntity<?> getLeaveCustomFields(@RequestBody String strJson) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(strJson, jsonConfig);
		Long leaveSchemeId=jsonObj.getLong("leaveSchemeId");
		Long leaveTypeId=jsonObj.getLong("leaveTypeId");
		Long employeeLeaveSchemeId=jsonObj.getLong("employeeLeaveSchemeId");
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveForm addLeaveForm =addLeaveLogic.getLeaveCustomFields(leaveSchemeId, leaveTypeId, companyId, employeeId,employeeLeaveSchemeId);
		if(addLeaveForm==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(),messageSource.getMessage("payasia.leave.customfields.leave.data", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
			return new ResponseEntity<>(addLeaveForm , HttpStatus.OK);
	}
	
	/*
	 * LEAVE EXTENSION APIs
	 */
	
	@Override
	@PostMapping(value = "extension-leave-view")
	public ResponseEntity<?> extendLeaveEmployee(@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		
		String response = null;
		if(leaveApplicationId != null)
		{
			response = addLeaveLogic.extensionLeaveView(leaveApplicationId, employeeId, companyId);
//				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "leave-balance")
	public ResponseEntity<?> getLeaveBalance(@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		employeeLeaveSchemeTypeId= FormatPreserveCryptoUtil.decrypt(employeeLeaveSchemeTypeId);
		
		AddLeaveForm addLeaveForm = leaveBalanceSummaryLogic.getLeaveBalance(employeeLeaveSchemeTypeId, employeeId, companyId);

//		if(StringUtils.equalsIgnoreCase(addLeaveForm.getStatus(), "failure")){
//			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
//		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "leave-days")
	public ResponseEntity<?> getEmployeeLeaveDays(@RequestBody SearchParam searchParamObj, @RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveDTO leaveDTO = new LeaveDTO();
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		
		if ((filterllist != null && !filterllist.isEmpty() & filterllist.size() > 3)) {
			for (Filters filter : filterllist) {
				switch (filter.getField().toUpperCase()) {
				case "FROMDATE":
					leaveDTO.setFromDate(filter.getValue());
					break;

				case "TODATE":
					leaveDTO.setToDate(filter.getValue());
					break;

				case "SESSION1":
					leaveDTO.setSession1(Long.parseLong(filter.getValue()));
					break;

				case "SESSION2":
					leaveDTO.setSession2(Long.parseLong(filter.getValue()));
					break;
				}
			}
		}
		
		leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeTypeId);
		AddLeaveForm addLeaveForm = addLeaveLogic.getNoOfDays(companyId, employeeId, leaveDTO);

		if (addLeaveForm.getLeaveDTO() != null) {
			if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO().getErrorKey())) {
				addLeaveForm.getLeaveDTO().setErrorKey(messageSource.getMessage(addLeaveForm.getLeaveDTO().getErrorKey(), new Object[] {}, UserContext.getLocale()));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "extend-leave")
	public ResponseEntity<?> extensionLeave(@RequestBody AddLeaveForm addLeaveForm) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(addLeaveForm.getLeaveApplicationId()));
		
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY, new Object[] {}, UserContext.getLocale()));
		sessionDTO.setToSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY, new Object[] {}, UserContext.getLocale()));

		addLeaveFormRes = addLeaveLogic.extensionLeave(companyId, employeeId, addLeaveForm, sessionDTO);

		if(addLeaveFormRes == null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr = null;
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
						errorKeyFinalStr.append(messageSource.getMessage(errorKeyArr[count], errorVal, UserContext.getLocale()));
					}
				}
			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
}
