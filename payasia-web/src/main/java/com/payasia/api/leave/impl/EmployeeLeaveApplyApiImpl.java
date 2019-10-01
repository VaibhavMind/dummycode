package com.payasia.api.leave.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.leave.EmployeeLeaveApplyApi;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveCustomFieldDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE + "/leave/")
public class EmployeeLeaveApplyApiImpl implements EmployeeLeaveApplyApi {

	@Resource
	private LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;

	@Resource
	private AddLeaveLogic addLeaveLogic;

	@Resource
	private MessageSource messageSource;

	@Override
	@PostMapping(value = "view-scheme-info")
	public ResponseEntity<?> doShowLeaveSchemeInfo() {
		Long loginEmployeeID = Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSchemeForm leaveSchemeForm = addLeaveLogic.getLeaveSchemes(companyID, loginEmployeeID);
		if (leaveSchemeForm.getEmployeeLeaveSchemeId() != null && leaveSchemeForm.getEmployeeLeaveSchemeId() != 0l) {
			return new ResponseEntity<>(leaveSchemeForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
				.getMessage("payasia.leave.scheme.info.data", new Object[] {}, UserContext.getLocale()).toString()),
				HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "view-leave-type")
	public ResponseEntity<?> doShowLeaveTypes(@RequestBody String jsonStr) {
		Long loginEmployeeID = Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long employeeLeaveSchemeId = jsonObj.getLong("employeeLeaveSchemeId");
		if (employeeLeaveSchemeId != null && employeeLeaveSchemeId != 0l) {
			AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveTypes(employeeLeaveSchemeId, companyID, loginEmployeeID);
			return new ResponseEntity<>(addLeaveForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(
				new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
						.getMessage("payasia.leave.type.data", new Object[] {}, UserContext.getLocale()).toString()),
				HttpStatus.NOT_FOUND);

	}

	@Override
	@PostMapping(value = "view-reviewers-email-customfields")
	public ResponseEntity<?> doShowLeaveReviewersEmailAndCustomFields(@RequestBody String jsonStr) {
		Long loginEmployeeID = Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long employeeLeaveSchemeId = jsonObj.getLong("employeeLeaveSchemeId");
		Long employeeLeaveSchemeTypeId = jsonObj.getLong("employeeLeaveSchemeTypeId");
		Long leaveTypeId = jsonObj.getLong("leaveTypeId");
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveCustomFields(employeeLeaveSchemeId, leaveTypeId, companyID,
				loginEmployeeID, employeeLeaveSchemeTypeId);
		if (addLeaveForm != null) {
			return new ResponseEntity<>(addLeaveForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
				.getMessage("payasia.leave.reviewers.data", new Object[] {}, UserContext.getLocale()).toString()),
				HttpStatus.NOT_FOUND);

	}

	@Override
	@PostMapping(value = "view-leave-balance")
	public ResponseEntity<?> doShowLeaveBalance(@RequestBody String jsonStr) {
		Long loginEmployeeID = Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long employeeLeaveSchemeTypeId = jsonObj.getLong("employeeLeaveSchemeTypeId");
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveBalance(companyID, loginEmployeeID,
				employeeLeaveSchemeTypeId);

		if (addLeaveForm.getLeaveBalance() == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
					.getMessage("payasia.leave.balance.data", new Object[] {}, UserContext.getLocale()).toString()),
					HttpStatus.NOT_FOUND);

		}
		return new ResponseEntity<>(addLeaveForm, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "view-leave-days")
	public ResponseEntity<?> doShowLeaveDays(@RequestBody String jsonStr) {
		Long loginEmployeeID = Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		String fromDate = jsonObj.getString("fromDate");
		String toDate = jsonObj.getString("toDate");
		Long session1 = jsonObj.getLong("session1");
		Long session2 = jsonObj.getLong("session2");
		Long employeeLeaveSchemeTypeId = jsonObj.getLong("employeeLeaveSchemeTypeId");
		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(fromDate);
		leaveDTO.setToDate(toDate);
		leaveDTO.setSession1(session1);
		leaveDTO.setSession2(session2);
		leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeTypeId);
		AddLeaveForm addLeaveForm = addLeaveLogic.getNoOfDays(companyID, loginEmployeeID, leaveDTO);
		if (addLeaveForm.getLeaveDTO() == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
					.getMessage("payasia.leave.days.data", new Object[] {}, UserContext.getLocale()).toString()),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(addLeaveForm, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "apply-leave")
	public ResponseEntity<?> doApplyLeave(@RequestParam("applyLeaveModel") String jsonStr,
			@RequestParam("files") MultipartFile files[]) {
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		Long loginEmployeeID = Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		/*sessionDTO.setFromSessionName(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY);
		sessionDTO.setToSessionName(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY);*/
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,new Object[] {}, UserContext.getLocale()));
		sessionDTO.setToSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,new Object[] {}, UserContext.getLocale()));

		int invalidFileCount = 0;
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		String leaveMode = jsonObj.getString("leaveMode");
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		addLeaveForm.setStatus(jsonObj.getString("status"));
		addLeaveForm.setApplyToEmail(jsonObj.getString("applyToEmail"));
		addLeaveForm.setEmployeeLeaveSchemeId(jsonObj.getLong("employeeLeaveSchemeId"));
		addLeaveForm.setLeaveTypeId(jsonObj.getLong("employeeLeaveSchemeTypeId"));
		addLeaveForm.setFromDate(jsonObj.getString("fromDate"));
		addLeaveForm.setFromSessionId(jsonObj.getLong("s1"));
		addLeaveForm.setToSessionId(jsonObj.getLong("s4"));
		addLeaveForm.setLeaveBalance(new BigDecimal(jsonObj.getLong("leaveBalance")));
		addLeaveForm.setToDate(jsonObj.getString("toDate"));
		addLeaveForm.setNoOfDays(new BigDecimal(jsonObj.getDouble("leaveDays")));
		addLeaveForm.setApplyTo(jsonObj.getString("applyTo"));
		addLeaveForm.setApplyToId(jsonObj.getLong("applyToId"));
		addLeaveForm.setReason(jsonObj.getString("reason"));
		int totalNumberofReviewer = jsonObj.getInt("totalNoOfReviewer");
		if (totalNumberofReviewer == 1) {
			addLeaveForm.setLeaveApplicationReviewerId(
					FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveReviewerId1")));
		}
		if (totalNumberofReviewer == 2) {
			addLeaveForm.setLeaveReviewer2Id(FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveReviewerId2")));
			addLeaveForm.setLeaveApplicationReviewerId(
					FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveReviewerId1")));

		}
		if (totalNumberofReviewer == 3) {
			addLeaveForm.setLeaveReviewer2Id(FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveReviewerId2")));
			addLeaveForm.setLeaveApplicationReviewerId(
					FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveReviewerId1")));
			addLeaveForm.setLeaveReviewer3Id(FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveReviewerId3")));
		}

		JSONArray jsonArray = jsonObj.getJSONArray("emailCC");
		String emailCCStr = "";
		if (jsonArray != null && jsonArray.size() > 0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				emailCCStr = StringUtils.trim(emailCCStr) + jsonArray.getString(i);
			}
		}

		addLeaveForm.setEmailCC(emailCCStr);
		List<LeaveCustomFieldDTO> customFieldDTOList = new ArrayList<>();
		JSONArray jsonArrayCustomFields = jsonObj.getJSONArray("customFieldDTOList");
		System.out.println(jsonArrayCustomFields);
		if (!jsonArrayCustomFields.isEmpty()) {
			for (int i = 0; i < jsonArrayCustomFields.size(); i++) {

				LeaveCustomFieldDTO leaveCustomFieldDTO = new LeaveCustomFieldDTO();
				leaveCustomFieldDTO.setCustomFieldTypeId(
						jsonObj.getJSONArray("customFieldDTOList").getJSONObject(i).getLong("customFieldTypeId"));
				leaveCustomFieldDTO.setCustomFieldName(
						jsonObj.getJSONArray("customFieldDTOList").getJSONObject(i).getString("customFieldName"));
				leaveCustomFieldDTO
						.setValue(jsonObj.getJSONArray("customFieldDTOList").getJSONObject(i).getString("value"));
				if (leaveMode.equals("modify")) {
                    Long strCustomFieldId = jsonObj.getJSONArray("customFieldDTOList").getJSONObject(i)
                            .getLong("customFieldId");
                  
                    if(strCustomFieldId!=0){
                        leaveCustomFieldDTO.setCustomFieldId(Long.valueOf(strCustomFieldId));
                    }
                } 
				customFieldDTOList.add(leaveCustomFieldDTO);

			}
		}

		addLeaveForm.setCustomFieldDTOList(customFieldDTOList);

		if (addLeaveForm.getLeaveTypeId() == null || addLeaveForm.getLeaveTypeId() == 0L) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.CONFLICT.toString(), messageSource
					.getMessage("payasia.leave.type.required", new Object[] {}, UserContext.getLocale()).toString()),
					HttpStatus.CONFLICT);
		} else if (StringUtils.isEmpty(addLeaveForm.getFromDate())) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.CONFLICT.toString(),
					messageSource
							.getMessage("payasia.leave.fromdate.required", new Object[] {}, UserContext.getLocale())
							.toString()),
					HttpStatus.CONFLICT);
		} else if (StringUtils.isEmpty(addLeaveForm.getToDate())) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.CONFLICT.toString(), messageSource
					.getMessage("payasia.leave.todate.required", new Object[] {}, UserContext.getLocale()).toString()),
					HttpStatus.CONFLICT);
		}

		if (leaveMode.equals("modify")) {
			addLeaveForm.setLeaveApplicationId(jsonObj.getLong("leaveApplicationId"));
			addLeaveForm.setPreApprovalReq(jsonObj.getBoolean("preApprovalReq"));
		} else {
			addLeaveForm.setPreApprovalReq(jsonObj.getBoolean("preApprovalReq"));
		}
		List<LeaveApplicationAttachmentDTO> LeaveApplicationAttachmentDTOList = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			LeaveApplicationAttachmentDTO leaveApplicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
			leaveApplicationAttachmentDTO.setAttachment((CommonsMultipartFile) files[i]);
			leaveApplicationAttachmentDTO.setFileName(files[i].getOriginalFilename());
			LeaveApplicationAttachmentDTOList.add(leaveApplicationAttachmentDTO);
		}
		addLeaveForm.setAttachmentList(LeaveApplicationAttachmentDTOList);
		if (addLeaveForm.getAttachmentList() != null) {
			for (LeaveApplicationAttachmentDTO applicationAttachmentDTO : addLeaveForm.getAttachmentList()) {
				if (applicationAttachmentDTO.getAttachment() != null
						&& applicationAttachmentDTO.getAttachment().getSize() > 0) {

					boolean isFileValid = FileUtils.isValidFile(applicationAttachmentDTO.getAttachment(),
							applicationAttachmentDTO.getAttachment().getOriginalFilename(),
							PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT,
							PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);

					if (!isFileValid) {
						invalidFileCount++;
					}
				}
				if (applicationAttachmentDTO.getAttachment() != null
						&& applicationAttachmentDTO.getAttachment().getSize() == 0) {
					invalidFileCount++;
				}
			}
		}
		if (invalidFileCount != 0) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_INVALID_FILE);
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		} else {

			if ("apply".equalsIgnoreCase(leaveMode)) {
				addLeaveFormRes = addLeaveLogic.addLeave(companyID, loginEmployeeID, addLeaveForm, sessionDTO);
			} else if ("modify".equalsIgnoreCase(leaveMode)) {
				addLeaveFormRes = addLeaveLogic.editLeave(companyID, loginEmployeeID, addLeaveForm, sessionDTO);
			}
		}
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
								messageSource.getMessage(errorKeyArr[count], errorVal, UserContext.getLocale()));

					}

				}

			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());

			return new ResponseEntity<>(
					new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), errorKeyFinalStr.toString()),
					HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource
					.getMessage("payasia.leave.apply.success", new Object[] {}, UserContext.getLocale()).toString()),
					HttpStatus.OK);
		}

	}

	@Override
	@PostMapping(value = "save-default-email")
	public ResponseEntity<?> doSaveDefaultEmailCC(@RequestBody String jsonStr) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);

		String moduleName = jsonObj.getString("moduleNameStr");
		JSONArray jsonArray = jsonObj.getJSONArray("emailCC");
		String emailCCStr = "";
		if (jsonArray != null && jsonArray.size() > 0) {
			for (int i = 0; i < jsonArray.size(); i++) {
				emailCCStr = StringUtils.trim(emailCCStr) + jsonArray.getString(i);
			}
		}
		if (emailCCStr != null || !emailCCStr.isEmpty()) {
			leaveBalanceSummaryLogic.saveDefaultEmailCCByEmployee(companyId, employeeId, emailCCStr, moduleName, false);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource
				.getMessage("payasia.leave.defaultEmail.success", new Object[] {}, UserContext.getLocale()).toString()),
				HttpStatus.OK);

	}

	@Override
	@PostMapping(value = "show-default-email")
	public ResponseEntity<?> doShowDefaultEmailCC(@RequestBody String jsonStr) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);

		String moduleName = jsonObj.getString("moduleNameStr");
		List<EmployeeFilterListForm> filterList = leaveBalanceSummaryLogic.getDefaultEmailCCListByEmployee(companyId,
				employeeId, moduleName, false);
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);

		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);

	}

	@Override
	@PostMapping(value = "remove-leave-attachment")
	public ResponseEntity<?> deleteAttachment(@RequestBody String jsonStr) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long attachmentIdEnc = jsonObj.getLong("attachmentId");

		Long attachmentId = FormatPreserveCryptoUtil.decrypt(attachmentIdEnc);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String message = "";
		if (attachmentId != 0L) {
			addLeaveLogic.deleteAttachment(attachmentId, employeeId, companyId);
			message = "Delete Successfully";
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),message),HttpStatus.OK);
	}

}
