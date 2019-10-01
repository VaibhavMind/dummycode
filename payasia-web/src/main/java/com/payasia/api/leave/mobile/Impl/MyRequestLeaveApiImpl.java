package com.payasia.api.leave.mobile.Impl;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.payasia.api.leave.mobile.MyRequestLeaveApi;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CancelLeaveApplication;
import com.payasia.common.dto.CancelLeavesReponse;
import com.payasia.common.dto.ComboValueDTO;
import com.payasia.common.dto.EditLeaveResponse;
import com.payasia.common.dto.EmployeeDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveAttachmentDTO;
import com.payasia.common.dto.LeaveCustomFieldDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.PendingLeaveRes;
import com.payasia.common.dto.ViewLeaveResponse;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.ApplyLeaveResponse;
import com.payasia.common.form.EmployeeHomePageForm;
import com.payasia.common.form.EmployeeLeaveSchemeTypeResponse;
import com.payasia.common.form.LeaveApplicationRes;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaMobileConstants;
import com.payasia.common.util.PropReader;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.EmployeeHomePageLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.web.util.URLUtils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE + "/leave/")
public class MyRequestLeaveApiImpl implements MyRequestLeaveApi {
	Logger LOGGER = LoggerFactory.getLogger(MyRequestLeaveApiImpl.class);
	@Resource
	private AddLeaveLogic addLeaveLogic;
	@Resource
	private LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	@Resource
	private EmployeeHomePageLogic employeeHomePageLogic;
	@Autowired
	private MessageSource messageSource;
	@Resource
	private URLUtils urlUtils;
	@Resource
	private CompanyInformationLogic companyInformationLogic;
	@Resource
	private GeneralLogic generalLogic;
	
	@Override
	@PostMapping(value="view-leave-request-mobile")
	public ResponseEntity<?> doShowLeaveRequest(@RequestBody String jsonDataStr) {
		Long employeeId = Long.valueOf(UserContext.getUserId());
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonDataStr, jsonConfig);
		String requestType = jsonObj.getString(PayAsiaMobileConstants.REQUEST_TYPE);
		AddLeaveFormResponse addLeaveFormResponse = null;
		switch (requestType) {
		case PayAsiaMobileConstants.MY_REQUEST_DRAFT:
			addLeaveFormResponse = addLeaveLogic.getPendingLeaves(null, null, employeeId, null, null, "", "",companyId);
			break;
		case PayAsiaMobileConstants.MY_REQUEST_SUBMITTED:
			addLeaveFormResponse = addLeaveLogic.getSubmittedLeaves(null, null, employeeId, null, null, "", "", "",companyId);
			break;
		case PayAsiaMobileConstants.MY_REQUEST_SUBMITTED_CANCEL:
			addLeaveFormResponse = addLeaveLogic.getSubmittedCancelledLeaves(null, null, employeeId, null, null, "", "","",companyId);
			break;
		case PayAsiaMobileConstants.MY_REQUEST_APPROVED:
			addLeaveFormResponse = addLeaveLogic.getCompletedLeaves(null, null, employeeId, null, null, "", "", "",companyId);
			break;
		case PayAsiaMobileConstants.MY_REQUEST_APPROVED_CANCEL:
			addLeaveFormResponse = addLeaveLogic.getCompletedCancelLeaves(null, null, employeeId, null, null, "", "",
					"",companyId);
			break;
		case PayAsiaMobileConstants.MY_REQUEST_WITHDRAWN:
			addLeaveFormResponse = addLeaveLogic.getWithDrawnLeaves(null, null, employeeId, null, null, "", "", "",companyId);
			break;
		case PayAsiaMobileConstants.MY_REQUEST_REJECTED:
			addLeaveFormResponse = addLeaveLogic.getRejectedLeaves(null, null, employeeId, null, null, "", "", "",companyId);
			break;
		case PayAsiaMobileConstants.MY_REQUEST_ALL:
			addLeaveFormResponse = addLeaveLogic.getAllLeaveRequestData(null, null, employeeId, null, null, "", "", "",companyId);
			break;

		default:
			requestType = "NONE";
		}

		if (addLeaveFormResponse == null || requestType.equals("NONE")) {
			return new ResponseEntity<>(
					new ApiMessageHandler(Type.INFO, "409", PropReader.getMessage("leave.request.type.data")),
					HttpStatus.CONFLICT);
		} else {
			Map<String, AddLeaveFormResponse> leaveReuestMap = new HashMap<>();
			leaveReuestMap.put(requestType, addLeaveFormResponse);
			return new ResponseEntity<>(addLeaveFormResponse, HttpStatus.OK);
		}
	}
	
	@Override
	@PostMapping(value="apply-leave-mobile")
	public ResponseEntity<?> applyLeave() {
		ApplyLeaveResponse applyLeaveResponse = new ApplyLeaveResponse();
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		Long employeeId = Long.valueOf(UserContext.getUserId());
		UserContext.setWorkingCompanyDateFormat(UserContext.getWorkingCompanyDateFormat());
		setApplyLeaveData(companyId, employeeId, applyLeaveResponse);
		Map<String, ApplyLeaveResponse> map = new HashMap<>();
		map.put("applyLeave", applyLeaveResponse);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@Override
	@PostMapping(value="pending-leave-mobile")
	public ResponseEntity<?> getPendingLeaves() {
		Long employeeId = Long.valueOf(UserContext.getUserId());
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		EmployeeHomePageForm employeeHomePageForm = employeeHomePageLogic.getLeaveDetails(companyId, employeeId);
		PendingLeaveRes pendingLeaveRes = new PendingLeaveRes();
		pendingLeaveRes.setReviewLeaveList(employeeHomePageForm.getReviewLeaveList());
		for (PendingItemsForm pendingItemsForm : pendingLeaveRes.getReviewLeaveList()) {
			if (pendingItemsForm != null) {
				pendingItemsForm.setCreatedDate(DateUtils.timeStampToStringWithTimeM(pendingItemsForm.getCreatedDateM(),
						UserContext.getWorkingCompanyTimeZoneGMTOffset()));
			}
		}
		final Map<String, PendingLeaveRes> map = new HashMap<>();
		map.put("pendingLeave", pendingLeaveRes);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	private void setApplyLeaveData(Long companyId, Long employeeId, ApplyLeaveResponse applyLeaveResponse) {
		LeaveSchemeForm employeeLeaveScheme = addLeaveLogic.getLeaveSchemes(companyId, employeeId);
		List<ComboValueDTO> sessionList = leaveBalanceSummaryLogic.getLeaveSessionList();
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveTypes(employeeLeaveScheme.getEmployeeLeaveSchemeId(),
				companyId, employeeId);
		applyLeaveResponse.setLeaveSchemName(employeeLeaveScheme.getLeaveSchemeName());
		applyLeaveResponse.setEmployeeLeaveSchemeId(employeeLeaveScheme.getEmployeeLeaveSchemeId());
		applyLeaveResponse.setEmployeeLeaveSchemeTypes(addLeaveForm.getLeaveTypeFormList());
		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);
		applyLeaveResponse.setLeaveUnitDays(isLeaveUnitDays);
		getLocalBasedSession(companyId, sessionList);
		applyLeaveResponse.setSessionList(sessionList);

	}

	private void getLocalBasedSession(Long companyId, List<ComboValueDTO> sessionList) {
		String shortCompanyCode = companyInformationLogic.getShortCompanyCode(companyId);
		String localeParam = "en_US";
		Locale newLocale = urlUtils.getNewLocale(localeParam, shortCompanyCode);
		UserContext.setLocale(newLocale);
		for (ComboValueDTO comboValueDTO : sessionList) {
			comboValueDTO.setLabel(messageSource.getMessage(comboValueDTO.getLabelKey(), new Object[] {}, newLocale));
		}
	}

	@Override
	@PostMapping(value="save-leave-mobile")
	public ResponseEntity<?> saveLeave(@RequestParam("leaveData") String jsonDataStr,@RequestParam("attachedFiles") MultipartFile files []) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		final Map<String,Object> map= new HashMap<>();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonDataStr, jsonConfig);
		JSONObject jsonObjectToBeReturn = new JSONObject();
		Integer noOfAttachements = jsonObj.getInt(PayAsiaMobileConstants.NO_OF_ATTACHMENTS);
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		Long employeeId = Long.valueOf(UserContext.getUserId());
		Boolean isFromDateGreater = DateUtils.checkDates(jsonObj.getString(PayAsiaMobileConstants.FROM_DATE),
				jsonObj.getString(PayAsiaMobileConstants.TO_DATE));
		if (isFromDateGreater) {
			map.put(PayAsiaMobileConstants.JSON_STATUS, PayAsiaMobileConstants.JSON_ERROR);
			map.put(PayAsiaMobileConstants.MESSAGE,
					messageSource.getMessage(PayAsiaMobileConstants.FROM_DATE_CANT_BE_GREATER_THAN_TO_DATE,
							new Object[] {}, new Locale(companyId.toString())));
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
		setLeaveApplicationData(addLeaveForm,jsonObj,companyId,employeeId,PayAsiaConstants.LEAVE_STATUS_DRAFT,PayAsiaMobileConstants.SAVE);
		String shortCompanyCode = companyInformationLogic.getShortCompanyCode(companyId);
		String localeParam = "en_US";
		Locale newLocale = urlUtils.getNewLocale(localeParam, shortCompanyCode);
		UserContext.setLocale(newLocale);
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, newLocale));
		sessionDTO.setToSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, newLocale));
		AddLeaveForm addLeaveFormRes = addLeaveLogic.addLeaveMobile(companyId, employeeId, addLeaveForm,
				noOfAttachements, sessionDTO);
		LeaveApplicationRes leaveApplicationRes = new LeaveApplicationRes();
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
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
						errorKeyFinalStr.append(
								messageSource.getMessage(errorKeyArr[count], errorVal, new Locale(companyId.toString())));
					}

				}

			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
		}

		jsonObjectToBeReturn=JSONObject.fromObject(addLeaveFormRes, jsonConfig);
		if (addLeaveFormRes.getLeaveDTO() != null && addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			map.put(PayAsiaMobileConstants.JSON_STATUS, PayAsiaMobileConstants.JSON_ERROR);
			map.put(PayAsiaMobileConstants.MESSAGE, addLeaveFormRes.getLeaveDTO().getErrorKey());
		} else {
			leaveApplicationRes.setLeaveApplicationId(addLeaveFormRes.getLeaveApplicationId());
			List<LeaveAttachmentDTO> leaveAttachmentDTOs = new ArrayList<>();
			int fileId=1;
			for (int attachementCounter = 0; attachementCounter <noOfAttachements; attachementCounter++) {
				fileId=fileId+attachementCounter;
				LeaveAttachmentDTO leaveAttachmentDTO = new LeaveAttachmentDTO();
				leaveAttachmentDTO.setFileId(fileId);
				leaveAttachmentDTOs.add(leaveAttachmentDTO);
		uploadLeaveApplication( Long.valueOf(attachementCounter),leaveApplicationRes.getLeaveApplicationId(),
					  files[attachementCounter].getOriginalFilename(),files[attachementCounter]);
			}
			leaveApplicationRes.setLeaveAttachmentDTOs(leaveAttachmentDTOs);
			jsonObjectToBeReturn = net.sf.json.JSONObject.fromObject(leaveApplicationRes, jsonConfig);

			map.put(PayAsiaMobileConstants.MESSAGE,
					messageSource.getMessage(PayAsiaMobileConstants.LEAVETRANSACTION_SUCCESSFULLY_SAVED,
							new Object[] {}, new Locale(companyId.toString())));
			map.put(PayAsiaMobileConstants.JSON_STATUS, PayAsiaMobileConstants.JSON_SUCCESS);

		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	private void setLeaveApplicationData(AddLeaveForm addLeaveForm, JSONObject jsonObject, Long companyId,
			Long employeeId, String status, String mode) {
		if ("update".equalsIgnoreCase(mode)) {
			Long leaveApplicationId = jsonObject.getLong("leaveApplicationId");
			addLeaveForm.setLeaveApplicationId(leaveApplicationId);
		}
		Long employeeLeaveSchemeTypeId = jsonObject.getLong("employeeLeaveSchemeTypeId");
		addLeaveForm.setLeaveTypeId(employeeLeaveSchemeTypeId);
		addLeaveForm.setFromDate(jsonObject.getString(PayAsiaMobileConstants.FROM_DATE));
		addLeaveForm.setToDate(jsonObject.getString(PayAsiaMobileConstants.TO_DATE));
		addLeaveForm.setFromSessionId(jsonObject.getLong(PayAsiaMobileConstants.FROM_SESSION_ID));
		addLeaveForm.setToSessionId(jsonObject.getLong(PayAsiaMobileConstants.TO_SESSION_ID));

		addLeaveForm.setReason(jsonObject.getString(PayAsiaMobileConstants.REASON));
		addLeaveForm.setApplyToId(jsonObject.getLong(PayAsiaMobileConstants.LEAVE_REVIEWER_ID));
		addLeaveForm.setEmailCC(jsonObject.getString(PayAsiaMobileConstants.EMAIL_CC).replace(",", ";"));
		addLeaveForm.setStatus(status);
		if (jsonObject.has("noOfDays")) {
			if (jsonObject.getString("noOfDays") != null) {
				addLeaveForm.setNoOfDays(new BigDecimal(jsonObject.getString("noOfDays")));
			}
		}
		AddLeaveForm addLeaveFormCustomFields = addLeaveLogic.getLeaveCustomFields(null, null, companyId, employeeId,
				employeeLeaveSchemeTypeId);

		List<LeaveCustomFieldDTO> customFieldDTOVOs = null;

		if (addLeaveFormCustomFields.getLeaveCustomFieldDTO() == null) {
			customFieldDTOVOs = new ArrayList<>();
		} else {
			customFieldDTOVOs = addLeaveFormCustomFields.getLeaveCustomFieldDTO();
		}

		List<LeaveCustomFieldDTO> customFieldDTOs = new ArrayList<>();
		for (LeaveCustomFieldDTO leaveCustomFieldDTO : customFieldDTOVOs) {

			leaveCustomFieldDTO.setValue(jsonObject
					.getString(PayAsiaMobileConstants.CUSTOM_FIELD_ + leaveCustomFieldDTO.getCustomFieldTypeId()));

			if (mode.equalsIgnoreCase(PayAsiaMobileConstants.UPDATE)) {
				leaveCustomFieldDTO.setCustomFieldId(leaveCustomFieldDTO.getCustomFieldTypeId());
			}
			customFieldDTOs.add(leaveCustomFieldDTO);
		}
		addLeaveForm.setCustomFieldDTOList(customFieldDTOs);
		if (!status.equals(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			addLeaveForm.setLeaveReviewer2Id(jsonObject.getLong(PayAsiaMobileConstants.LEAVE_REVIEWER_ID_2));
			addLeaveForm.setLeaveReviewer3Id(jsonObject.getLong(PayAsiaMobileConstants.LEAVE_REVIEWER_ID_3));

		}
	}
	private  void uploadLeaveApplication(Long fileId, Long leaveApplicationId,String fileName,MultipartFile file) {
	final Map<String,Object> map= new HashMap<>();
		try {
			byte[] imgBytes = IOUtils.toByteArray(file.getInputStream());

			addLeaveLogic.uploadLeaveApllication(
					URLDecoder.decode(fileName, "UTF-8"), imgBytes,
					leaveApplicationId);
			map.put(PayAsiaMobileConstants.JSON_STATUS,
					PayAsiaMobileConstants.JSON_SUCCESS);
			map.put(PayAsiaMobileConstants.MESSAGE, "");
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			map.put(PayAsiaMobileConstants.JSON_STATUS,
					PayAsiaMobileConstants.JSON_ERROR);
			map.put(PayAsiaMobileConstants.MESSAGE,
					exception.getMessage());
		}
	}
	@Override
	@PostMapping(value="submited-leave-mobile")
	public ResponseEntity<?> submitedLeave(@RequestBody String jsonDataStr) {
		final Map<String,Object> map=new HashMap<>();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonDataStr, jsonConfig);
		JSONObject jsonObjectToBeReturn = new JSONObject();
		Integer noOfAttachements = jsonObj.getInt(PayAsiaMobileConstants.NO_OF_ATTACHMENTS);
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		Long employeeId = Long.valueOf(UserContext.getUserId());
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		Boolean isFromDateGreater = DateUtils.checkDates(jsonObj.getString(PayAsiaMobileConstants.FROM_DATE),
				jsonObj.getString(PayAsiaMobileConstants.TO_DATE));
		if (isFromDateGreater) {
			map.put(PayAsiaMobileConstants.JSON_STATUS, PayAsiaMobileConstants.JSON_ERROR);
			map.put(PayAsiaMobileConstants.MESSAGE,
					messageSource.getMessage(PayAsiaMobileConstants.FROM_DATE_CANT_BE_GREATER_THAN_TO_DATE,
							new Object[] {}, new Locale(companyId.toString())));
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
		setLeaveApplicationData(addLeaveForm, jsonObj, companyId, employeeId, PayAsiaConstants.LEAVE_STATUS_SUBMITTED,
				PayAsiaMobileConstants.UPDATE);
		String shortCompanyCode = companyInformationLogic.getShortCompanyCode(companyId);
		String localeParam = "en_US";
		Locale newLocale = urlUtils.getNewLocale(localeParam, shortCompanyCode);
		UserContext.setLocale(newLocale);
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, newLocale));
		sessionDTO.setToSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, newLocale));

		AddLeaveForm addLeaveFormRes = addLeaveLogic.updateLeaveMobile(companyId, employeeId, addLeaveForm,
				noOfAttachements, sessionDTO);
		LeaveApplicationRes leaveApplicationRes = new LeaveApplicationRes();
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
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
						errorKeyFinalStr.append(
								messageSource.getMessage(errorKeyArr[count], errorVal, new Locale(companyId.toString()))
										+ " \n ");
					}

				}

			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
		}

		jsonObjectToBeReturn = net.sf.json.JSONObject.fromObject(addLeaveFormRes, jsonConfig);
		if (addLeaveFormRes.getLeaveDTO() != null && addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			map.put(PayAsiaMobileConstants.JSON_STATUS, PayAsiaMobileConstants.JSON_ERROR);
			map.put(PayAsiaMobileConstants.MESSAGE, addLeaveFormRes.getLeaveDTO().getErrorKey());
		} else {
			leaveApplicationRes.setLeaveApplicationId(addLeaveFormRes.getLeaveApplicationId());
			List<LeaveAttachmentDTO> leaveAttachmentDTOs = new ArrayList<>();
			for (int attachementCounter = 1; attachementCounter <= noOfAttachements; attachementCounter++) {
				LeaveAttachmentDTO leaveAttachmentDTO = new LeaveAttachmentDTO();
				leaveAttachmentDTO.setFileId(attachementCounter);
				leaveAttachmentDTO.setFileURL("/service/leaveWS/upload/leaveApplication/" + attachementCounter);
				leaveAttachmentDTOs.add(leaveAttachmentDTO);
			}
			leaveApplicationRes.setLeaveAttachmentDTOs(leaveAttachmentDTOs);
			jsonObjectToBeReturn = net.sf.json.JSONObject.fromObject(leaveApplicationRes, jsonConfig);
			map.put(PayAsiaMobileConstants.MESSAGE,
					messageSource.getMessage(PayAsiaMobileConstants.LEAVE_APPLICATION_SUCCESFULLY_SUBMITTED_TO_WORKFLOW,
							new Object[] {}, new Locale(companyId.toString())));
			map.put(PayAsiaMobileConstants.JSON_STATUS, PayAsiaMobileConstants.JSON_SUCCESS);
		}

		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@Override
	@PostMapping(value="delete-leave-mobile")
	public ResponseEntity<?> deleteLeave(@RequestBody String jsonDataStr) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonDataStr, jsonConfig);
		Long leaveApplicationId = jsonObj.getLong(PayAsiaMobileConstants.LEAVE_APPLICATION_ID);
		EmployeeDTO employeeDTO = new EmployeeDTO();
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		Long employeeId = Long.valueOf(UserContext.getUserId());
		employeeDTO.setEmployeeId(employeeId);
		employeeDTO.setCompanyId(companyId);
		addLeaveLogic.deleteLeave(leaveApplicationId, employeeDTO);
		return null;
	}

	@Override
	@PostMapping(value="get-days-mobile")
	public ResponseEntity<?> getDays(@RequestBody String jsonDataStr) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonDataStr, jsonConfig);
		final Map<String,Object> map= new HashMap<>();
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		Long employeeId = Long.valueOf(UserContext.getUserId());
		Long employeeLeaveSchemeTypeId = jsonObj.getLong(PayAsiaMobileConstants.EMPLOYEE_LEAVE_SCHEME_TYPE_ID);
		Long session1= jsonObj.getLong(PayAsiaMobileConstants.SESSION1);
		Long session2= jsonObj.getLong(PayAsiaMobileConstants.SESSION2);
		String fromDate=jsonObj.getString(PayAsiaMobileConstants.FROM_DATE);
		String toDate = jsonObj.getString(PayAsiaMobileConstants.TO_DATE);
		Boolean isFromDateGreater = DateUtils.checkDates(fromDate, toDate);
		if (isFromDateGreater) {
			map.put(PayAsiaMobileConstants.JSON_STATUS,
					PayAsiaMobileConstants.JSON_ERROR);
			map.put(PayAsiaMobileConstants.MESSAGE,
					messageSource
					.getMessage(PayAsiaMobileConstants.FROM_DATE_CANT_BE_GREATER_THAN_TO_DATE,
								   new Object[] {},
									new Locale(companyId.toString())));
			return new ResponseEntity<>(map,HttpStatus.OK);
		}
		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(fromDate);
		leaveDTO.setToDate(toDate);
		leaveDTO.setSession1(session1);
		leaveDTO.setSession2(session2);
		leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeTypeId);
		AddLeaveForm addLeaveForm = addLeaveLogic.getNoOfDays(companyId,
				employeeId, leaveDTO);
		if(addLeaveForm!=null)
		{
		map.put("days",addLeaveForm.getNoOfDays());
		return new ResponseEntity<>(addLeaveForm,HttpStatus.OK);
		}
		else
		{
		return new ResponseEntity<>(
					new ApiMessageHandler(Type.INFO, "500", PropReader.getMessage("leave.request.days.data")),
					HttpStatus.CONFLICT);
		}
		
	}
	@Override
	@PostMapping("leaveScheme-employee-info")
	public ResponseEntity<?> employeeLeaveSchemeTypeInfo(@RequestBody String jsonDataStr) {
		final Map<String,Object> map=new HashMap<>();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(jsonDataStr, jsonConfig);
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		Long employeeId = Long.valueOf(UserContext.getUserId());
		Long employeeLeaveSchemeTypeId = jsonObject.getLong(PayAsiaMobileConstants.EMPLOYEE_LEAVE_SCHEME_TYPE_ID);
		EmployeeLeaveSchemeTypeResponse employeeLeaveSchemeTypeResponse = new EmployeeLeaveSchemeTypeResponse();
		setEmployeeLeaveSchemeTypeInfo(companyId, employeeId,
					employeeLeaveSchemeTypeResponse, employeeLeaveSchemeTypeId);
		map.put("showEmployeeInfo",employeeLeaveSchemeTypeResponse);
		return new ResponseEntity<>(map,HttpStatus.OK);

	}
		private void setEmployeeLeaveSchemeTypeInfo(Long companyId,
				Long employeeId,
				EmployeeLeaveSchemeTypeResponse employeeLeaveSchemeTypeResponse,
				Long employeeLeaveSchemeTypeId) {
			AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveCustomFields(null,
					null, companyId, employeeId, employeeLeaveSchemeTypeId);
			Map<Long,String> map=new HashMap<>();
			employeeLeaveSchemeTypeResponse.setInstruction(addLeaveForm
					.getRemarks()==null?"":addLeaveForm.getRemarks());
			employeeLeaveSchemeTypeResponse.setApplyTo(addLeaveForm.getApplyToEmail());

			employeeLeaveSchemeTypeResponse.setLeaveReviewer1Name(addLeaveForm
					.getLeaveReviewer1() == null ? "" : addLeaveForm
							.getLeaveReviewer1());

			employeeLeaveSchemeTypeResponse.setLeaveReviewer2Name(addLeaveForm
					.getLeaveReviewer2() == null ? "" : addLeaveForm
							.getLeaveReviewer2());
			employeeLeaveSchemeTypeResponse.setLeaveReviewer3Name(addLeaveForm
					.getLeaveReviewer3() == null ? "" : addLeaveForm
							.getLeaveReviewer3());
			employeeLeaveSchemeTypeResponse.setLeaveReviewerId1(addLeaveForm
					.getApplyToId());
			employeeLeaveSchemeTypeResponse.setLeaveReviewerId2(addLeaveForm
					.getLeaveReviewer2Id());
			employeeLeaveSchemeTypeResponse.setLeaveReviewerId3(addLeaveForm
					.getLeaveReviewer3Id());
			AddLeaveForm leaveBalanceDetails = addLeaveLogic.getLeaveBalance(
					companyId, employeeId, employeeLeaveSchemeTypeId);
			map.put(addLeaveForm.getApplyToId(), addLeaveForm.getLeaveReviewer1());
			if(addLeaveForm.getLeaveReviewer2Id()!=null)
				map.put(addLeaveForm.getLeaveReviewer2Id(), addLeaveForm.getLeaveReviewer2());
			if(addLeaveForm.getLeaveReviewer3Id()!=null)
				map.put(employeeLeaveSchemeTypeResponse.getLeaveReviewerId3(),employeeLeaveSchemeTypeResponse.getLeaveReviewer3Name());
			employeeLeaveSchemeTypeResponse.setLeaveBalance(leaveBalanceDetails
					.getLeaveBalance());
			employeeLeaveSchemeTypeResponse.setNoOfCustomFields(addLeaveForm
					.getLeaveCustomFieldDTO() == null ? 0 : addLeaveForm
							.getLeaveCustomFieldDTO().size());
			employeeLeaveSchemeTypeResponse.setLeaveCustomFieldDTO(addLeaveForm
					.getLeaveCustomFieldDTO());
			employeeLeaveSchemeTypeResponse.setEmployeeLeaveReviewerMap(map);
		}

		@Override
		@PostMapping("view-Leave-mobile")
		public ResponseEntity<?> viewLeave(@RequestBody String jsonDataStr) {
			JsonConfig jsonConfig=new JsonConfig();
			JSONObject jsonObject=JSONObject.fromObject(jsonDataStr, jsonConfig);
			Long employeeId=Long.valueOf(UserContext.getUserId());
			Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
			Long leaveApplicationId = jsonObject.getLong("leaveApplicationId");
			EmployeeDTO employeeDTO= new EmployeeDTO();
			employeeDTO.setEmployeeId(employeeId);
			employeeDTO.setCompanyId(companyId);
			employeeDTO.setWorkingCompanyTimezoneOffset(UserContext.getWorkingCompanyTimeZoneGMTOffset());
			AddLeaveForm leaveApplicationRes = addLeaveLogic.viewLeave(leaveApplicationId, employeeDTO);
			if(leaveApplicationRes!=null){
			ViewLeaveResponse viewLeaveResponse = new ViewLeaveResponse();
			viewLeaveResponse.setLeaveApplicationId(leaveApplicationId);
			viewLeaveResponse.setEmployeeName(leaveApplicationRes.getLeaveAppEmp());
			viewLeaveResponse.setTotalNoOfReviewers(leaveApplicationRes.getTotalNoOfReviewers());
			Long session1 = leaveApplicationRes.getFromSessionId();
			Long session2 = leaveApplicationRes.getToSessionId();
			String fromDate = leaveApplicationRes.getFromDate();
			String toDate = leaveApplicationRes.getToDate();
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(fromDate);
			leaveDTO.setToDate(toDate);
			leaveDTO.setSession1(session1);
			leaveDTO.setSession2(session2);
			leaveDTO.setEmployeeLeaveSchemeTypeId(leaveApplicationRes.getLeaveTypeId());
			BigDecimal totalLeaveDaysHours = null;
			boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);
			viewLeaveResponse.setLeaveUnitDays(isLeaveUnitDays);
			if (isLeaveUnitDays) {
				AddLeaveForm nOfDays = addLeaveLogic.getNoOfDays(companyId,
						employeeId, leaveDTO);
				totalLeaveDaysHours = nOfDays.getNoOfDays();
			} else {
				// Hours between dates
				totalLeaveDaysHours = leaveApplicationRes.getNoOfDays();
			}
			viewLeaveResponse.setDays(totalLeaveDaysHours);
			viewLeaveResponse.setFromDate(leaveApplicationRes.getFromDate());
			String shortCompanyCode = companyInformationLogic.getShortCompanyCode(companyId);
			String localeParam = "en_US";
			Locale newLocale = urlUtils.getNewLocale(localeParam,
					shortCompanyCode);
			UserContext.setLocale(newLocale);
			if (leaveApplicationRes.getFromSession().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_SESSION_1)) {
				viewLeaveResponse.setSession1(messageSource.getMessage(
						PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
						new Object[] {}, newLocale));
			}
			if (leaveApplicationRes.getFromSession().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_SESSION_2)) {
				viewLeaveResponse.setSession1(messageSource.getMessage(
						PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
						new Object[] {}, newLocale));
			}
			if (leaveApplicationRes.getToSession().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_SESSION_1)) {
				viewLeaveResponse.setSession2(messageSource.getMessage(
						PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
						new Object[] {}, newLocale));
			}
			if (leaveApplicationRes.getToSession().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_SESSION_2)) {
				viewLeaveResponse.setSession2(messageSource.getMessage(
						PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
						new Object[] {}, newLocale));
			}
			viewLeaveResponse.setLeaveScheme(leaveApplicationRes.getLeaveScheme());
			viewLeaveResponse.setLeaveType(leaveApplicationRes.getLeaveType());
			viewLeaveResponse.setReason(leaveApplicationRes.getReason());
			viewLeaveResponse.setRemarks(leaveApplicationRes.getRemarks());
			viewLeaveResponse.setToDate(leaveApplicationRes.getToDate());
			viewLeaveResponse.setAttachmentList(leaveApplicationRes.getAttachmentList());
			viewLeaveResponse.setWorkflowList(leaveApplicationRes.getWorkflowList());
			viewLeaveResponse.setLeaveReviewer1(leaveApplicationRes.getLeaveReviewer1());
			viewLeaveResponse.setLeaveReviewer2(leaveApplicationRes.getLeaveReviewer2());
			viewLeaveResponse.setLeaveReviewer3(leaveApplicationRes.getLeaveReviewer3());
			viewLeaveResponse.setIsWithdrawn(leaveApplicationRes.getIsWithdrawn());
			viewLeaveResponse.setLeaveApplicationStatus(messageSource
					.getMessage(leaveApplicationRes.getLeaveAppStatus(),
							new Object[] {},
							new Locale(companyId.toString())));
			viewLeaveResponse.setLeaveApplicationCreatedDate(DateUtils
					.timeStampToStringWithTimeM(
							leaveApplicationRes.getCreateDateM(),
							employeeDTO.getWorkingCompanyTimezoneOffset()));
			List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = leaveApplicationRes
					.getCustomFieldDTOList();
			HashMap<Long, String> customFieldHashMap = new HashMap<>();
			for (LeaveCustomFieldDTO leaveCustomFieldDTO : leaveCustomFieldDTOs) {
				customFieldHashMap.put(
						leaveCustomFieldDTO.getCustomFieldTypeId(),
						leaveCustomFieldDTO.getValue());
			}
			EmployeeLeaveSchemeTypeResponse employeeLeaveSchemeTypeResponse = new EmployeeLeaveSchemeTypeResponse();
			setEmployeeLeaveSchemeTypeInfo(companyId, employeeId,
					employeeLeaveSchemeTypeResponse,
					leaveApplicationRes.getLeaveTypeId());
			for (LeaveCustomFieldDTO leaveCustomFieldDTO : employeeLeaveSchemeTypeResponse
					.getLeaveCustomFieldDTO()) {
				leaveCustomFieldDTO.setValue(customFieldHashMap
						.get(leaveCustomFieldDTO.getCustomFieldTypeId()));
			}
			viewLeaveResponse.setEmployeeLeaveSchemeTypeInfo(employeeLeaveSchemeTypeResponse);
			for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : viewLeaveResponse
					.getWorkflowList()) {
				if (leaveApplicationWorkflowDTO.getCreatedDate() != null) {
					leaveApplicationWorkflowDTO
							.setCreatedDate(DateUtils.timeStampToStringWithTimeM(
									leaveApplicationWorkflowDTO
											.getCreatedDateM(), employeeDTO
											.getWorkingCompanyTimezoneOffset()));
				}
			}
			return new ResponseEntity<>(viewLeaveResponse,HttpStatus.OK);
		}
			else
			{
				return new ResponseEntity<>(
						new ApiMessageHandler(Type.INFO, "409", PropReader.getMessage("leave.request.type.data")),
						HttpStatus.CONFLICT);
			}
		}
		@Override
		@PostMapping("withdraw-leave-mobile")
		public ResponseEntity<?> withdrawLeave(@RequestBody String jsonDataStr) {
			final Map<String,Object> map= new HashMap<>();
			JsonConfig jsonConfig=new JsonConfig();
			JSONObject jsonObject=JSONObject.fromObject(jsonDataStr,jsonConfig);
			Long employeeId=Long.valueOf(UserContext.getUserId());
			Long companyId=Long.valueOf(UserContext.getWorkingCompanyId());
			Long leaveApplicationId=jsonObject.getLong("leaveApplicationId");
			LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
			String shortCompanyCode = companyInformationLogic.getShortCompanyCode(companyId);
			String localeParam = "en_US";
			Locale newLocale = urlUtils.getNewLocale(localeParam,
					shortCompanyCode);
			UserContext.setLocale(newLocale);
			sessionDTO.setFromSessionName(messageSource.getMessage(
					PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
					new Object[] {}, newLocale));
			sessionDTO.setToSessionName(messageSource.getMessage(
					PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
					new Object[] {}, newLocale));
			addLeaveLogic.withdrawLeaveMobile(leaveApplicationId, employeeId,
					sessionDTO, companyId);
			map.put(PayAsiaMobileConstants.MESSAGE,messageSource
					.getMessage(
							PayAsiaMobileConstants.LEAVE_APPLICATION_WITHDRAWN_SUCCESSFULLY,
							new Object[] {},
							new Locale(companyId.toString())));
			map.put(PayAsiaMobileConstants.JSON_STATUS,
					PayAsiaMobileConstants.JSON_SUCCESS);
			return new ResponseEntity<>(map,HttpStatus.OK);
		}

		@Override
		@GetMapping("downloadAttachment-leave-mobile")
		public ResponseEntity<?> downloadAttachment(@RequestParam("attachementId") Long attachmentId) {
			 HttpHeaders headers = new HttpHeaders();
		Long employeeId=Long.valueOf(UserContext.getUserId());
		Long CompanyId=Long.valueOf(UserContext.getWorkingCompanyId());
		LeaveApplicationAttachmentDTO attachment = addLeaveLogic
				.viewAttachment(attachmentId);
		byte[] byteFile = attachment.getAttachmentBytes();
			return new ResponseEntity<>(byteFile,headers,HttpStatus.OK);
		}
		
		@Override
		@PostMapping("removeAttachment-leave-mobile")
		public ResponseEntity<?> removeAttachement(@RequestBody String jsonDataStr) {
			final Map<String,Object> map=new HashMap<>();
		JsonConfig jsonConfig=new JsonConfig();
		JSONObject jsonObject=JSONObject.fromObject(jsonDataStr, jsonConfig);
		Long attachmentId = jsonObject.getLong(PayAsiaMobileConstants.ATTACHEMENT_ID);
		addLeaveLogic.deleteAttachment(attachmentId);
		map.put(PayAsiaMobileConstants.MESSAGE,"Attchement Deleted Successfully");
			return new ResponseEntity<>(map,HttpStatus.OK);
		}

		@Override
		@PostMapping("completed-leaves-mobile")
		public ResponseEntity<?> getCompletedLeaves(@RequestBody String jsonDataStr) {
			final Map<String,Object> map=new HashMap<>();
			JsonConfig jsonConfig=new JsonConfig();
			JSONObject jsonObject=JSONObject.fromObject(jsonDataStr,jsonConfig);
			Long employeeId=Long.valueOf(UserContext.getUserId());
			Long companyId=Long.valueOf(UserContext.getWorkingCompanyId());
			AddLeaveFormResponse addLeaveResponse = null;
			addLeaveResponse = leaveBalanceSummaryLogic.getCompletedLeaves(
					employeeId, null, null, "", companyId);
			CancelLeavesReponse cancelLeaveReponse = new CancelLeavesReponse();
			setCompletedLeaves(cancelLeaveReponse,
					addLeaveResponse.getRows());		
			return new ResponseEntity<>(cancelLeaveReponse,HttpStatus.OK);
		}
		private void setCompletedLeaves(CancelLeavesReponse cancelLeaveReponse,
				List<AddLeaveForm> addLeaveFormList) {

			List<CancelLeaveApplication> cancelLeaveApplications = new ArrayList<>();
			for (AddLeaveForm addLeaveForm : addLeaveFormList) {
				CancelLeaveApplication cancelLeaveApplication = new CancelLeaveApplication();
				cancelLeaveApplication.setFromDate(addLeaveForm.getFromDate());
				cancelLeaveApplication.setLeaveApplicationId(addLeaveForm
						.getLeaveApplicationId());
				cancelLeaveApplication
						.setLeaveScheme(addLeaveForm.getLeaveScheme());
				cancelLeaveApplication.setLeaveType(addLeaveForm.getLeaveType());
				cancelLeaveApplication.setToDate(addLeaveForm.getToDate());
				cancelLeaveApplications.add(cancelLeaveApplication);
			}
			cancelLeaveReponse.setCancelLeaveApplications(cancelLeaveApplications);
		}

		@Override
		public ResponseEntity<?> viewCancelLeave(@RequestBody String jsonDataStr) {
			JsonConfig jsonConfig=new JsonConfig();
			JSONObject jsonObject=JSONObject.fromObject(jsonDataStr, jsonConfig);
			Long employeeId=Long.valueOf(UserContext.getUserId());
			Long comapnyId=Long.valueOf(UserContext.getWorkingCompanyId());
			Long leaveApplicationId = jsonObject
					.getLong(PayAsiaMobileConstants.LEAVE_APPLICATION_ID);
			return null;
		}

		@Override
		@PostMapping(value="edit-leave-mobile")
		public ResponseEntity<?> editLeaveApplication(@RequestBody String jsonDataStr) {
			final Map<String,Object> map = new HashMap<String,Object>();
			JsonConfig jsonConfig=new JsonConfig();
			JSONObject jsonObject=JSONObject.fromObject(jsonDataStr,jsonConfig);
			Long employeeId=Long.valueOf(UserContext.getUserId());
			Long companyId=Long.valueOf(UserContext.getWorkingCompanyId());
			Long leaveApplicationId=jsonObject.getLong(PayAsiaMobileConstants.LEAVE_APPLICATION_ID);
			AddLeaveForm persistedLeaveAppData = addLeaveLogic
					.getDataForPendingLeave(leaveApplicationId, employeeId,
							companyId);
			AddLeaveForm employeeLeaveSchemeTypes = addLeaveLogic
					.getLeaveTypes(
							persistedLeaveAppData.getEmployeeLeaveSchemeId(),
							companyId, employeeId);
			EditLeaveResponse editLeaveResponse = new EditLeaveResponse();
			setPersistedLeaveApplicationData(persistedLeaveAppData,
					employeeLeaveSchemeTypes, editLeaveResponse, companyId,
					employeeId);
			Long session1 = persistedLeaveAppData.getFromSessionId();
			Long session2 = persistedLeaveAppData.getToSessionId();
			String fromDate = persistedLeaveAppData.getFromDate();
			String toDate = persistedLeaveAppData.getToDate();
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(fromDate);
			leaveDTO.setToDate(toDate);
			leaveDTO.setSession1(session1);
			leaveDTO.setSession2(session2);
			leaveDTO.setEmployeeLeaveSchemeTypeId(persistedLeaveAppData
					.getLeaveTypeId());
			BigDecimal totalLeaveDaysHours = null;
			boolean isLeaveUnitDays = leaveBalanceSummaryLogic
					.isLeaveUnitDays(companyId);
			if (isLeaveUnitDays) {
				AddLeaveForm nOfDays = addLeaveLogic.getNoOfDays(companyId,
						employeeId, leaveDTO);
				totalLeaveDaysHours = nOfDays.getNoOfDays();
			} else {
				// Hours between dates
				totalLeaveDaysHours = persistedLeaveAppData.getNoOfDays();
			}
			map.put("days", totalLeaveDaysHours);
			map.put(PayAsiaMobileConstants.JSON_STATUS,
					PayAsiaMobileConstants.JSON_SUCCESS);
			return new ResponseEntity<>(map,HttpStatus.OK);
		}
		private void setPersistedLeaveApplicationData(
				AddLeaveForm persistedLeaveAppData,
				AddLeaveForm employeeLeaveSchemeTypes,
			    EditLeaveResponse editLeaveResponse, Long companyId, Long employeeId){
			
			editLeaveResponse.setEmployeeLeaveSchemeTypes(employeeLeaveSchemeTypes
					.getLeaveTypeFormList());
			List<ComboValueDTO> sessionList = leaveBalanceSummaryLogic
					.getLeaveSessionList();
			getLocalBasedSession(companyId, sessionList);
			editLeaveResponse.setSessionList(sessionList);
			editLeaveResponse.setInstruction(persistedLeaveAppData.getRemarks());
			editLeaveResponse.setApplyTo(persistedLeaveAppData.getApplyTo());
			editLeaveResponse.setApplyToEmail(persistedLeaveAppData
					.getApplyToEmail());
			editLeaveResponse.setApplyToId(persistedLeaveAppData.getApplyToId());
			editLeaveResponse.setAttachmentList(persistedLeaveAppData
					.getAttachmentList());
			editLeaveResponse.setEmployeeLeaveSchemeId(persistedLeaveAppData
					.getEmployeeLeaveSchemeId());
			editLeaveResponse.setFromDate(persistedLeaveAppData.getFromDate());
			editLeaveResponse.setFromSession(persistedLeaveAppData.getFromSession());
	        editLeaveResponse.setFromSessionId(persistedLeaveAppData.getFromSessionId());
	        editLeaveResponse.setLeaveBalance(persistedLeaveAppData.getLeaveBalance());
	        editLeaveResponse.setLeaveReviewer1(persistedLeaveAppData.getLeaveReviewer1());
	        editLeaveResponse.setLeaveReviewer2(persistedLeaveAppData.getLeaveReviewer2());
			editLeaveResponse.setLeaveReviewer2Id(persistedLeaveAppData.getLeaveReviewer2Id());
			editLeaveResponse.setLeaveReviewer3(persistedLeaveAppData.getLeaveReviewer3());
			editLeaveResponse.setLeaveReviewer3Id(persistedLeaveAppData.getLeaveReviewer3Id());
			editLeaveResponse.setLeaveSchemName(persistedLeaveAppData.getLeaveScheme());
			editLeaveResponse.setNoOfDays(persistedLeaveAppData.getNoOfDays());
			editLeaveResponse.setReason(persistedLeaveAppData.getReason());
			editLeaveResponse.setRemarks(persistedLeaveAppData.getRemarks());
			editLeaveResponse.setToDate(persistedLeaveAppData.getToDate());
			editLeaveResponse.setToSession(persistedLeaveAppData.getToSession());
			editLeaveResponse.setToSessionId(persistedLeaveAppData.getToSessionId());
			editLeaveResponse.setWorkflowList(persistedLeaveAppData.getWorkflowList());
			editLeaveResponse.setEmployeeLeaveSchemeTypeId(persistedLeaveAppData.getLeaveTypeId());
			editLeaveResponse.setCustomFieldDTOList(persistedLeaveAppData.getCustomFieldDTOList());
			editLeaveResponse.setEmailCC(persistedLeaveAppData.getEmailCC());
			List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = persistedLeaveAppData.getCustomFieldDTOList();
			HashMap<Long, String> customFieldHashMap = new HashMap<>();
			for (LeaveCustomFieldDTO leaveCustomFieldDTO : leaveCustomFieldDTOs) {
				customFieldHashMap.put(leaveCustomFieldDTO.getCustomFieldTypeId(),
						leaveCustomFieldDTO.getValue());
			}
			EmployeeLeaveSchemeTypeResponse employeeLeaveSchemeTypeResponse = new EmployeeLeaveSchemeTypeResponse();
			setEmployeeLeaveSchemeTypeInfo(companyId, employeeId,employeeLeaveSchemeTypeResponse,persistedLeaveAppData.getLeaveTypeId());
			for (LeaveCustomFieldDTO leaveCustomFieldDTO : employeeLeaveSchemeTypeResponse
					.getLeaveCustomFieldDTO()) {
				leaveCustomFieldDTO.setValue(customFieldHashMap
						.get(leaveCustomFieldDTO.getCustomFieldTypeId()));
			}
			editLeaveResponse
			.setEmployeeLeaveSchemeTypeInfo(employeeLeaveSchemeTypeResponse);
	        boolean isLeaveUnitDays = leaveBalanceSummaryLogic
			.isLeaveUnitDays(companyId);
	        editLeaveResponse.setLeaveUnitDays(isLeaveUnitDays);
		}

		@Override
		@PostMapping(value="config-leave-mobile")
		public ResponseEntity<?> configLeave() {
			final Map<String,Object> map = new HashMap<>();
			Long companyId=Long.valueOf(UserContext.getWorkingCompanyId());
			Long employeeId=Long.valueOf(UserContext.getUserId());
			Boolean isLeaveManager = generalLogic
					.checkIsLeaveManager(employeeId);
			EmployeeHomePageForm employeeHomePageForm = employeeHomePageLogic
					.getLeaveDetails(companyId, employeeId);
			map.put("isLeaveManager", isLeaveManager);
			map.put("noOfPendingItems", employeeHomePageForm
					.getReviewLeaveList().size());
			map.put(PayAsiaMobileConstants.JSON_STATUS,
					PayAsiaMobileConstants.JSON_SUCCESS);
			return new ResponseEntity<>(map,HttpStatus.OK);
		}
}