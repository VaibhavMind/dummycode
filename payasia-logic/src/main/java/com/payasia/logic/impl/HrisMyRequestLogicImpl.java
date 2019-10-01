package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.HRISChangeRequestWorkflowDTO;
import com.payasia.common.dto.HrisMyRequestConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.HrisChangeRequestForm;
import com.payasia.common.form.HrisMyRequestFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHRISReviewerDAO;
import com.payasia.dao.HRISChangeRequestDAO;
import com.payasia.dao.HRISChangeRequestReviewerDAO;
import com.payasia.dao.HRISChangeRequestWorkflowDAO;
import com.payasia.dao.HRISStatusMasterDAO;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequestReviewer;
import com.payasia.dao.bean.HRISChangeRequestWorkflow;
import com.payasia.dao.bean.HRISStatusMaster;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.HRISMailLogic;
import com.payasia.logic.HrisMyRequestLogic;
import com.payasia.logic.MultilingualLogic;

@Component
public class HrisMyRequestLogicImpl implements HrisMyRequestLogic {

	@Resource
	HRISChangeRequestDAO hRISChangeRequestDAO;

	@Resource
	HRISStatusMasterDAO hrisStatusMasterDAO;
	@Resource
	EmployeeHRISReviewerDAO employeeHRISReviewerDAO;

	@Resource
	HRISChangeRequestReviewerDAO hrisChangeRequestReviewerDAO;
	@Resource
	HRISChangeRequestWorkflowDAO hrisChangeRequestWorkflowDAO;
	@Resource
	HRISMailLogic hrisMailLogic;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	MultilingualLogic multilingualLogic;

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}
	
// Changes by Gaurav
	private String getStatusImage(String status, String contextPath,
			Employee employee) {
//		String imagePath = contextPath + "/resources/images/icon/16/";
//		if (status.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_PENDING)) {
//			imagePath = imagePath + "pending.png";
//		} else if (status
//				.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_APPROVED)) {
//			imagePath = imagePath + "approved.png";
//		} else if ("NA".equalsIgnoreCase(status)) {
//			imagePath = imagePath + "pending-next-level.png";
//		} else if (status
//				.equalsIgnoreCase(PayAsiaConstants.HRISE_STATUS_REJECTED)) {
//			imagePath = imagePath + "rejected.png";
//		}
		String employeeName = getEmployeeName(employee);
//
//		return "<img src='" + imagePath + "'  />  " + employeeName;
//
		return  status+"##"+employeeName;
	}

	private class HRISReviewerComp implements
			Comparator<HRISChangeRequestReviewer> {
		public int compare(HRISChangeRequestReviewer templateField,
				HRISChangeRequestReviewer compWithTemplateField) {
			if (templateField.getHrisChangeRequestReviewerId() > compWithTemplateField
					.getHrisChangeRequestReviewerId()) {
				return 1;
			} else if (templateField.getHrisChangeRequestReviewerId() < compWithTemplateField
					.getHrisChangeRequestReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	private class HRISWorkFlowComp implements
			Comparator<HRISChangeRequestWorkflow> {
		public int compare(HRISChangeRequestWorkflow templateField,
				HRISChangeRequestWorkflow compWithTemplateField) {
			if (templateField.getHrisChangeRequestWorkflowId() > compWithTemplateField
					.getHrisChangeRequestWorkflowId()) {
				return 1;
			} else if (templateField.getHrisChangeRequestWorkflowId() < compWithTemplateField
					.getHrisChangeRequestWorkflowId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public HrisMyRequestFormResponse getSubmittedRequest(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long languageId) {
		HrisMyRequestConditionDTO conditionDTO = new HrisMyRequestConditionDTO();
		conditionDTO.setEmployeeId(employeeId);

		List<String> hrisStatusName = new ArrayList<>();
		hrisStatusName.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusName.add(PayAsiaConstants.HRIS_STATUS_APPROVED);
		conditionDTO.setHrisStatusNames(hrisStatusName);

		
		/*if(((searchCondition!=null)&&(searchText!=null))||((searchCondition!="")&&(searchText!="")))*/

	  if(!StringUtils.isEmpty(searchCondition) &&  !StringUtils.isEmpty(searchText))
		{
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_OLDVALUE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setOldValue(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_NEWVALUE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setNewValue(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_FIELD)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setField(searchText.trim());
			}

		}
		}

		Integer recordSize = hRISChangeRequestDAO
				.getCountChangeRequests(conditionDTO);

		List<HRISChangeRequest> submittedChangeRequest = hRISChangeRequestDAO
				.findByConditionChangeRequests(conditionDTO, pageDTO, sortDTO);

		List<HrisChangeRequestForm> hrisChangeRequestForms = new ArrayList<HrisChangeRequestForm>();
		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();
		for (HRISChangeRequest hRISChangeRequest : submittedChangeRequest) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			HrisChangeRequestForm hrisChangeRequestForm = new HrisChangeRequestForm();

			String multilingualFieldLabel = multilingualLogic
					.convertFieldLabelToSpecificLanguage(languageId,
							hRISChangeRequest.getDataDictionary().getCompany()
									.getCompanyId(), hRISChangeRequest
									.getDataDictionary().getDataDictionaryId());
			if (StringUtils.isBlank(multilingualFieldLabel)) {
				multilingualFieldLabel = hRISChangeRequest.getDataDictionary()
						.getLabel();
			}

			StringBuilder field = new StringBuilder();
			field.append(multilingualFieldLabel);
//			field.append("<br>");
			/* ID ENCRYPT */
//			field.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewHrisChangeRequest("
//					+ FormatPreserveCryptoUtil.encrypt(hRISChangeRequest.getHrisChangeRequestId())
//					+ ");'>[View]</a></span>");

			hrisChangeRequestForm.setField(String.valueOf(field));
			
			hrisChangeRequestForm.setAction(true);
			hrisChangeRequestForm.setHrisChangeRequestId(FormatPreserveCryptoUtil.encrypt(hRISChangeRequest.getHrisChangeRequestId()));

			DynamicForm dynamicForm = null;
			if (formIdMap.containsKey(hRISChangeRequest.getDataDictionary()
					.getFormID())) {
				dynamicForm = formIdMap.get(hRISChangeRequest
						.getDataDictionary().getFormID());
			} else {
				dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
						hRISChangeRequest.getEmployee().getCompany()
								.getCompanyId(), hRISChangeRequest
								.getDataDictionary().getEntityMaster()
								.getEntityId(), hRISChangeRequest
								.getDataDictionary().getFormID());
				formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
			}

			EmployeeListFormPage employeeListFormPage = generalLogic
					.getEmployeeHRISChangeRequestData(
							hRISChangeRequest.getHrisChangeRequestId(),
							hRISChangeRequest.getEmployee().getCompany()
									.getCompanyId(), dynamicForm.getMetaData(),
							languageId);
			hrisChangeRequestForm.setNewValue(employeeListFormPage
					.getNewValue());
			hrisChangeRequestForm.setOldValue(employeeListFormPage
					.getOldValue());

			hrisChangeRequestForm.setCreatedDate(DateUtils.timeStampToStringWOTimezone(
					hRISChangeRequest.getCreatedDate(), hRISChangeRequest
							.getEmployee().getCompany().getDateFormat()));

			List<HRISChangeRequestReviewer> applicationReviewers = new ArrayList<>(
					hRISChangeRequest.getHrisChangeRequestReviewers());
			Collections.sort(applicationReviewers, new HRISReviewerComp());

			for (HRISChangeRequestReviewer changeReqReviewer : applicationReviewers) {
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByCondition(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId(), hrisStatusName);

					if (applicationWorkflow == null
							|| changeReqReviewer.getPending()) {

						hrisChangeRequestForm.setReviewer1(getStatusImage(
								PayAsiaConstants.HRIS_STATUS_PENDING,
								pageContextPath,
								changeReqReviewer.getEmployeeReviewer()));
								
						reviewer1Status = PayAsiaConstants.HRIS_STATUS_PENDING;
					} else if (applicationWorkflow
							.getHrisStatusMaster()
							.getHrisStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.HRIS_STATUS_APPROVED)) {

						StringBuilder reviewer1 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.HRIS_STATUS_APPROVED,
										pageContextPath,
										changeReqReviewer.getEmployeeReviewer()));

						reviewer1
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ DateUtils
												.timeStampToStringWOTimezone(applicationWorkflow
														.getCreatedDate(), hRISChangeRequest
														.getEmployee().getCompany().getDateFormat())
										+ "</span>");

						hrisChangeRequestForm.setReviewer1(String
								.valueOf(reviewer1));

						reviewer1Status = PayAsiaConstants.HRIS_STATUS_APPROVED;

					}
				}
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("2")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByCondition(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId(), hrisStatusName);

					if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_PENDING)) {

						hrisChangeRequestForm.setReviewer2(getStatusImage("NA",
								pageContextPath,
								changeReqReviewer.getEmployeeReviewer()));

						reviewer2Status = PayAsiaConstants.HRIS_STATUS_PENDING;
					} else if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_APPROVED)) {

						if (applicationWorkflow == null
								|| changeReqReviewer.getPending()) {

							hrisChangeRequestForm.setReviewer2(getStatusImage(
									PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath,
									changeReqReviewer.getEmployeeReviewer()));
							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						} else if (applicationWorkflow
								.getHrisStatusMaster()
								.getHrisStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.HRIS_STATUS_APPROVED)) {

							StringBuilder reviewer2 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.HRIS_STATUS_APPROVED,
											pageContextPath, changeReqReviewer
													.getEmployeeReviewer()));

							reviewer2
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ DateUtils
											.timeStampToStringWOTimezone(applicationWorkflow
													.getCreatedDate(), hRISChangeRequest
													.getEmployee().getCompany().getDateFormat())
											+ "</span>");

							hrisChangeRequestForm.setReviewer2(String
									.valueOf(reviewer2));

							reviewer2Status = PayAsiaConstants.HRIS_STATUS_APPROVED;

						}

					}

				}
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("3")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByCondition(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId(), hrisStatusName);

					if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {

						hrisChangeRequestForm.setReviewer3(getStatusImage("NA",
								pageContextPath,
								changeReqReviewer.getEmployeeReviewer()));

					} else if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null
								|| changeReqReviewer.getPending()) {
							hrisChangeRequestForm.setReviewer3(getStatusImage(
									PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath,
									changeReqReviewer.getEmployeeReviewer()));
						} else if (applicationWorkflow
								.getHrisStatusMaster()
								.getHrisStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.LEAVE_STATUS_APPROVED,
											pageContextPath, changeReqReviewer
													.getEmployeeReviewer()));

							leaveReviewer3
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ DateUtils
											.timeStampToStringWOTimezone(applicationWorkflow
													.getCreatedDate(), hRISChangeRequest
													.getEmployee().getCompany().getDateFormat())
											+ "</span>");

							hrisChangeRequestForm.setReviewer3(String
									.valueOf(leaveReviewer3));

						}

					}

				}

			}

			hrisChangeRequestForms.add(hrisChangeRequestForm);

		}

		HrisMyRequestFormResponse response = new HrisMyRequestFormResponse();
		response.setRows(hrisChangeRequestForms);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public HrisMyRequestFormResponse getApprovedRequest(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long languageId) {
		HrisMyRequestConditionDTO conditionDTO = new HrisMyRequestConditionDTO();
		conditionDTO.setEmployeeId(employeeId);

		List<String> hrisStatusName = new ArrayList<>();
		hrisStatusName.add(PayAsiaConstants.HRIS_STATUS_COMPLETED);
		conditionDTO.setHrisStatusNames(hrisStatusName);

		 if(!StringUtils.isEmpty(searchCondition) &&  !StringUtils.isEmpty(searchText))
		 {
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_OLDVALUE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setOldValue(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_NEWVALUE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setNewValue(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_FIELD)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setField(searchText.trim());
			}

		}
		 }
		Integer recordSize = hRISChangeRequestDAO
				.getCountChangeRequests(conditionDTO);

		List<HRISChangeRequest> submittedChangeRequest = hRISChangeRequestDAO
				.findByConditionChangeRequests(conditionDTO, pageDTO, sortDTO);

		List<HrisChangeRequestForm> hrisChangeRequestForms = new ArrayList<HrisChangeRequestForm>();
		for (HRISChangeRequest hRISChangeRequest : submittedChangeRequest) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			HrisChangeRequestForm hrisChangeRequestForm = new HrisChangeRequestForm();

			String multilingualFieldLabel = multilingualLogic
					.convertFieldLabelToSpecificLanguage(languageId,
							hRISChangeRequest.getDataDictionary().getCompany()
									.getCompanyId(), hRISChangeRequest
									.getDataDictionary().getDataDictionaryId());
			if (StringUtils.isBlank(multilingualFieldLabel)) {
				multilingualFieldLabel = hRISChangeRequest.getDataDictionary()
						.getLabel();
			}

			StringBuilder field = new StringBuilder();
			field.append(multilingualFieldLabel);
//			field.append("<br>");
			/* ID ENCRYPT */
//			field.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewHrisChangeRequest("
//					+ FormatPreserveCryptoUtil.encrypt(hRISChangeRequest.getHrisChangeRequestId())
//					+ ");'>[View]</a></span>");

			hrisChangeRequestForm.setField(String.valueOf(field));
			hrisChangeRequestForm.setAction(true);
			hrisChangeRequestForm.setHrisChangeRequestId(FormatPreserveCryptoUtil.encrypt(hRISChangeRequest.getHrisChangeRequestId()));

			DynamicForm dynamicForm = dynamicFormDAO
					.findMaxVersionByFormId(hRISChangeRequest.getEmployee()
							.getCompany().getCompanyId(), hRISChangeRequest
							.getDataDictionary().getEntityMaster()
							.getEntityId(), hRISChangeRequest
							.getDataDictionary().getFormID());

			EmployeeListFormPage employeeListFormPage = generalLogic
					.getEmployeeHRISChangeRequestData(
							hRISChangeRequest.getHrisChangeRequestId(),
							hRISChangeRequest.getEmployee().getCompany()
									.getCompanyId(), dynamicForm.getMetaData(),
							languageId);
			hrisChangeRequestForm.setNewValue(employeeListFormPage
					.getNewValue());
			hrisChangeRequestForm.setOldValue(employeeListFormPage
					.getOldValue());

			hrisChangeRequestForm.setCreatedDate(DateUtils.timeStampToStringWOTimezone(
					hRISChangeRequest.getCreatedDate(), hRISChangeRequest
							.getEmployee().getCompany().getDateFormat()));

			List<HRISChangeRequestReviewer> applicationReviewers = new ArrayList<>(
					hRISChangeRequest.getHrisChangeRequestReviewers());
			Collections.sort(applicationReviewers, new HRISReviewerComp());

			for (HRISChangeRequestReviewer changeReqReviewer : applicationReviewers) {
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByCondition(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId(),
									new ArrayList<String>());

					if (applicationWorkflow == null) {
						continue;
					}
					StringBuilder reviewer1 = new StringBuilder(getStatusImage(
							PayAsiaConstants.HRIS_STATUS_APPROVED,
							pageContextPath,
							changeReqReviewer.getEmployeeReviewer()));

					reviewer1
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+  DateUtils
									.timeStampToStringWOTimezone(applicationWorkflow
											.getCreatedDate(), hRISChangeRequest
											.getEmployee().getCompany().getDateFormat())
									+ "</span>");

					hrisChangeRequestForm.setReviewer1(String
							.valueOf(reviewer1));
				}
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("2")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByCondition(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId(),
									new ArrayList<String>());

					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder reviewer2 = new StringBuilder(getStatusImage(
							PayAsiaConstants.HRIS_STATUS_APPROVED,
							pageContextPath,
							changeReqReviewer.getEmployeeReviewer()));

					if (applicationWorkflow.getCreatedDate() != null) {
						reviewer2
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+  DateUtils
										.timeStampToStringWOTimezone(applicationWorkflow
												.getCreatedDate(), hRISChangeRequest
												.getEmployee().getCompany().getDateFormat())
										+ "</span>");
					}

					hrisChangeRequestForm.setReviewer2(String
							.valueOf(reviewer2));

				}
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("3")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByCondition(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId(),
									new ArrayList<String>());

					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder reviewer3 = new StringBuilder(getStatusImage(
							PayAsiaConstants.LEAVE_STATUS_APPROVED,
							pageContextPath,
							changeReqReviewer.getEmployeeReviewer()));

					reviewer3
							.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+  DateUtils
									.timeStampToStringWOTimezone(applicationWorkflow
											.getCreatedDate(), hRISChangeRequest
											.getEmployee().getCompany().getDateFormat())
									+ "</span>");

					hrisChangeRequestForm.setReviewer3(String
							.valueOf(reviewer3));

				}

			}
			hrisChangeRequestForms.add(hrisChangeRequestForm);

		}

		HrisMyRequestFormResponse response = new HrisMyRequestFormResponse();
		response.setRows(hrisChangeRequestForms);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public HrisMyRequestFormResponse getRejectedRequest(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long languageId) {
		HrisMyRequestConditionDTO conditionDTO = new HrisMyRequestConditionDTO();
		conditionDTO.setEmployeeId(employeeId);

		List<String> hrisStatusName = new ArrayList<>();
		hrisStatusName.add(PayAsiaConstants.HRISE_STATUS_REJECTED);
		conditionDTO.setHrisStatusNames(hrisStatusName);

		 if(!StringUtils.isEmpty(searchCondition) &&  !StringUtils.isEmpty(searchText))
		 {
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_OLDVALUE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setOldValue(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_NEWVALUE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setNewValue(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_FIELD)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setField(searchText.trim());
			}

		}
		 }
		
		Integer recordSize = hRISChangeRequestDAO
				.getCountChangeRequests(conditionDTO);

		List<HRISChangeRequest> submittedChangeRequest = hRISChangeRequestDAO
				.findByConditionChangeRequests(conditionDTO, pageDTO, sortDTO);

		List<HrisChangeRequestForm> hrisChangeRequestForms = new ArrayList<HrisChangeRequestForm>();
		for (HRISChangeRequest hRISChangeRequest : submittedChangeRequest) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			HrisChangeRequestForm hrisChangeRequestForm = new HrisChangeRequestForm();

			String multilingualFieldLabel = multilingualLogic
					.convertFieldLabelToSpecificLanguage(languageId,
							hRISChangeRequest.getDataDictionary().getCompany()
									.getCompanyId(), hRISChangeRequest
									.getDataDictionary().getDataDictionaryId());
			if (StringUtils.isBlank(multilingualFieldLabel)) {
				multilingualFieldLabel = hRISChangeRequest.getDataDictionary()
						.getLabel();
			}

			StringBuilder field = new StringBuilder();
			field.append(multilingualFieldLabel);
//			field.append("<br>");
//			/* ID ENCRYPT */
//			field.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewHrisChangeRequest("
//					+ FormatPreserveCryptoUtil.encrypt(hRISChangeRequest.getHrisChangeRequestId())
//					+ ");'>[View]</a></span>");

			hrisChangeRequestForm.setAction(true);
			hrisChangeRequestForm.setHrisChangeRequestId(FormatPreserveCryptoUtil.encrypt(hRISChangeRequest.getHrisChangeRequestId()));
						
			hrisChangeRequestForm.setField(String.valueOf(field));

			DynamicForm dynamicForm = dynamicFormDAO
					.findMaxVersionByFormId(hRISChangeRequest.getEmployee()
							.getCompany().getCompanyId(), hRISChangeRequest
							.getDataDictionary().getEntityMaster()
							.getEntityId(), hRISChangeRequest
							.getDataDictionary().getFormID());

			EmployeeListFormPage employeeListFormPage = generalLogic
					.getEmployeeHRISChangeRequestData(
							hRISChangeRequest.getHrisChangeRequestId(),
							hRISChangeRequest.getEmployee().getCompany()
									.getCompanyId(), dynamicForm.getMetaData(),
							languageId);
			hrisChangeRequestForm.setNewValue(employeeListFormPage
					.getNewValue());
			hrisChangeRequestForm.setOldValue(employeeListFormPage
					.getOldValue());

			hrisChangeRequestForm.setCreatedDate(DateUtils.timeStampToStringWOTimezone(
					hRISChangeRequest.getCreatedDate(), hRISChangeRequest
							.getEmployee().getCompany().getDateFormat()));
			List<HRISChangeRequestReviewer> applicationReviewers = new ArrayList<>(
					hRISChangeRequest.getHrisChangeRequestReviewers());
			Collections.sort(applicationReviewers, new HRISReviewerComp());

			for (HRISChangeRequestReviewer changeReqReviewer : applicationReviewers) {
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByConditionChangeRequest(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId());

					if (applicationWorkflow == null) {

						hrisChangeRequestForm.setReviewer1(getStatusImage(
								PayAsiaConstants.HRIS_STATUS_PENDING,
								pageContextPath,
								changeReqReviewer.getEmployeeReviewer()));

						reviewer1Status = PayAsiaConstants.HRIS_STATUS_PENDING;
					} else if (applicationWorkflow
							.getHrisStatusMaster()
							.getHrisStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.HRIS_STATUS_APPROVED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.LEAVE_STATUS_APPROVED,
										pageContextPath,
										changeReqReviewer.getEmployeeReviewer()));

						leaveReviewer1
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+  DateUtils
										.timeStampToStringWOTimezone(applicationWorkflow
												.getCreatedDate(), hRISChangeRequest
												.getEmployee().getCompany().getDateFormat())
										+ "</span>");

						hrisChangeRequestForm.setReviewer1(String
								.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.HRIS_STATUS_APPROVED;

					} else if (applicationWorkflow
							.getHrisStatusMaster()
							.getHrisStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.HRISE_STATUS_REJECTED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.LEAVE_STATUS_REJECTED,
										pageContextPath,
										changeReqReviewer.getEmployeeReviewer()));

						leaveReviewer1
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										
										// Changes by Gaurav
//										+ DateUtils
//												.timeStampToStringWithTime(applicationWorkflow
//														.getCreatedDate())
//										+" Date-2 -->> "
										
										+  DateUtils
										.timeStampToStringWOTimezone(applicationWorkflow
												.getCreatedDate(), hRISChangeRequest
												.getEmployee().getCompany().getDateFormat())
										+ "</span>");

						hrisChangeRequestForm.setReviewer1(String
								.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_REJECTED;

					}
				}
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("2")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByConditionChangeRequest(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId());

					if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_PENDING)
							|| reviewer1Status
									.equalsIgnoreCase(PayAsiaConstants.HRISE_STATUS_REJECTED)) {

						hrisChangeRequestForm.setReviewer2(getStatusImage("NA",
								pageContextPath,
								changeReqReviewer.getEmployeeReviewer()));

						reviewer2Status = PayAsiaConstants.HRIS_STATUS_PENDING;
					} else if (reviewer1Status
							.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							hrisChangeRequestForm.setReviewer2(getStatusImage(
									PayAsiaConstants.HRIS_STATUS_PENDING,
									pageContextPath,
									changeReqReviewer.getEmployeeReviewer()));
							reviewer2Status = PayAsiaConstants.HRIS_STATUS_PENDING;
						} else if (applicationWorkflow
								.getHrisStatusMaster()
								.getHrisStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.HRIS_STATUS_APPROVED)) {

							StringBuilder leaveReviewer2 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.HRIS_STATUS_APPROVED,
											pageContextPath, changeReqReviewer
													.getEmployeeReviewer()));

							leaveReviewer2
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+  DateUtils
											.timeStampToStringWOTimezone(applicationWorkflow
													.getCreatedDate(), hRISChangeRequest
													.getEmployee().getCompany().getDateFormat())
											+ "</span>");

							hrisChangeRequestForm.setReviewer2(String
									.valueOf(leaveReviewer2));

							reviewer2Status = PayAsiaConstants.HRIS_STATUS_APPROVED;

						} else if (applicationWorkflow
								.getHrisStatusMaster()
								.getHrisStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.HRISE_STATUS_REJECTED)) {

							hrisChangeRequestForm.setReviewer2(getStatusImage(
									PayAsiaConstants.HRISE_STATUS_REJECTED,
									pageContextPath,
									changeReqReviewer.getEmployeeReviewer()));

							reviewer2Status = PayAsiaConstants.HRISE_STATUS_REJECTED;

						}

					}

				}
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("3")) {

					HRISChangeRequestWorkflow applicationWorkflow = hrisChangeRequestWorkflowDAO
							.findByConditionChangeRequest(hRISChangeRequest
									.getHrisChangeRequestId(),
									changeReqReviewer.getEmployeeReviewer()
											.getEmployeeId());

					if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_PENDING)
							|| reviewer2Status
									.equalsIgnoreCase(PayAsiaConstants.HRISE_STATUS_REJECTED)) {

						hrisChangeRequestForm.setReviewer3(getStatusImage("NA",
								pageContextPath,
								changeReqReviewer.getEmployeeReviewer()));

					} else if (reviewer2Status
							.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							hrisChangeRequestForm.setReviewer3(getStatusImage(
									PayAsiaConstants.HRIS_STATUS_PENDING,
									pageContextPath,
									changeReqReviewer.getEmployeeReviewer()));
						} else if (applicationWorkflow
								.getHrisStatusMaster()
								.getHrisStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.HRIS_STATUS_APPROVED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.HRIS_STATUS_APPROVED,
											pageContextPath, changeReqReviewer
													.getEmployeeReviewer()));

							leaveReviewer3
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+  DateUtils
											.timeStampToStringWOTimezone(applicationWorkflow
													.getCreatedDate(), hRISChangeRequest
													.getEmployee().getCompany().getDateFormat())
											+ "</span>");

							hrisChangeRequestForm.setReviewer3(String
									.valueOf(leaveReviewer3));

						} else if (applicationWorkflow
								.getHrisStatusMaster()
								.getHrisStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.HRISE_STATUS_REJECTED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(
											PayAsiaConstants.HRISE_STATUS_REJECTED,
											pageContextPath, changeReqReviewer
													.getEmployeeReviewer()));

							leaveReviewer3
									.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+ DateUtils
											.timeStampToStringWOTimezone(applicationWorkflow
													.getCreatedDate(), hRISChangeRequest
													.getEmployee().getCompany().getDateFormat())
											+ "</span>");

							hrisChangeRequestForm.setReviewer3(String
									.valueOf(leaveReviewer3));

						}

					}

				}

			}
			hrisChangeRequestForms.add(hrisChangeRequestForm);

		}

		HrisMyRequestFormResponse response = new HrisMyRequestFormResponse();
		response.setRows(hrisChangeRequestForms);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public HrisMyRequestFormResponse getWithdrawnRequest(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long languageId) {
		HrisMyRequestConditionDTO conditionDTO = new HrisMyRequestConditionDTO();
		conditionDTO.setEmployeeId(employeeId);

		List<String> hrisStatusName = new ArrayList<>();
		hrisStatusName.add(PayAsiaConstants.HRIS_STATUS_WITHDRAWN);
		conditionDTO.setHrisStatusNames(hrisStatusName);

		 if(!StringUtils.isEmpty(searchCondition) &&  !StringUtils.isEmpty(searchText))
		 {
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_OLDVALUE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setOldValue(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_NEWVALUE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setNewValue(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_FIELD)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setField(searchText.trim());
			}

		}
		 }

		Integer recordSize = hRISChangeRequestDAO
				.getCountChangeRequests(conditionDTO);

		List<HRISChangeRequest> submittedChangeRequest = hRISChangeRequestDAO
				.findByConditionChangeRequests(conditionDTO, pageDTO, sortDTO);

		List<HrisChangeRequestForm> hrisChangeRequestForms = new ArrayList<HrisChangeRequestForm>();
		for (HRISChangeRequest hRISChangeRequest : submittedChangeRequest) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			HrisChangeRequestForm hrisChangeRequestForm = new HrisChangeRequestForm();

			String multilingualFieldLabel = multilingualLogic
					.convertFieldLabelToSpecificLanguage(languageId,
							hRISChangeRequest.getDataDictionary().getCompany()
									.getCompanyId(), hRISChangeRequest
									.getDataDictionary().getDataDictionaryId());
			if (StringUtils.isBlank(multilingualFieldLabel)) {
				multilingualFieldLabel = hRISChangeRequest.getDataDictionary()
						.getLabel();
			}

			StringBuilder field = new StringBuilder();
			field.append(multilingualFieldLabel);
//			field.append("<br>");
//			/* ID ENCRYPT */
//			field.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewHrisChangeRequest("
//					+ FormatPreserveCryptoUtil.encrypt(hRISChangeRequest.getHrisChangeRequestId())
//					+ ");'>[View]</a></span>");

			hrisChangeRequestForm.setAction(true);
			hrisChangeRequestForm.setHrisChangeRequestId(FormatPreserveCryptoUtil.encrypt(hRISChangeRequest.getHrisChangeRequestId()));
						
			hrisChangeRequestForm.setField(String.valueOf(field));

			DynamicForm dynamicForm = dynamicFormDAO
					.findMaxVersionByFormId(hRISChangeRequest.getEmployee()
							.getCompany().getCompanyId(), hRISChangeRequest
							.getDataDictionary().getEntityMaster()
							.getEntityId(), hRISChangeRequest
							.getDataDictionary().getFormID());

			EmployeeListFormPage employeeListFormPage = generalLogic
					.getEmployeeHRISChangeRequestData(
							hRISChangeRequest.getHrisChangeRequestId(),
							hRISChangeRequest.getEmployee().getCompany()
									.getCompanyId(), dynamicForm.getMetaData(),
							languageId);
			hrisChangeRequestForm.setNewValue(employeeListFormPage
					.getNewValue());
			hrisChangeRequestForm.setOldValue(employeeListFormPage
					.getOldValue());

			hrisChangeRequestForm.setCreatedDate(DateUtils.timeStampToStringWOTimezone(
					hRISChangeRequest.getCreatedDate(), hRISChangeRequest
							.getEmployee().getCompany().getDateFormat()));
			List<HRISChangeRequestReviewer> applicationReviewers = new ArrayList<>(
					hRISChangeRequest.getHrisChangeRequestReviewers());
			Collections.sort(applicationReviewers, new HRISReviewerComp());

			for (HRISChangeRequestReviewer changeReqReviewer : applicationReviewers) {
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {

					hrisChangeRequestForm
							.setReviewer1(getEmployeeName(changeReqReviewer
									.getEmployeeReviewer()));

				}
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("2")) {

					hrisChangeRequestForm
							.setReviewer2(getEmployeeName(changeReqReviewer
									.getEmployeeReviewer()));

				}
				if (changeReqReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("3")) {

					hrisChangeRequestForm
							.setReviewer3(getEmployeeName(changeReqReviewer
									.getEmployeeReviewer()));

				}

			}
			hrisChangeRequestForms.add(hrisChangeRequestForm);

		}

		HrisMyRequestFormResponse response = new HrisMyRequestFormResponse();
		response.setRows(hrisChangeRequestForms);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public HrisChangeRequestForm viewChangeRequest(Long hrisChangeRequestId, Long employeeId,
			Long languageId) {
		HrisChangeRequestForm hrisChangeRequestForm = new HrisChangeRequestForm();

		HRISChangeRequest hRISChangeRequest = hRISChangeRequestDAO
				.findById(hrisChangeRequestId);
		if(!Long.valueOf(hRISChangeRequest.getEmployee().getEmployeeId()).equals(employeeId)) {
			throw new PayAsiaSystemException("Unauthorized_Access", "Unauthorized Access");
		}
		String multilingualFieldLabel = multilingualLogic
				.convertFieldLabelToSpecificLanguage(languageId,
						hRISChangeRequest.getDataDictionary().getCompany()
								.getCompanyId(), hRISChangeRequest
								.getDataDictionary().getDataDictionaryId());
		if (StringUtils.isBlank(multilingualFieldLabel)) {
			multilingualFieldLabel = hRISChangeRequest.getDataDictionary()
					.getLabel();
		}

		hrisChangeRequestForm.setField(multilingualFieldLabel);
		hrisChangeRequestForm
				.setCreatedBy(getEmployeeNameWithNumber(hRISChangeRequest
						.getEmployee()));

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
				hRISChangeRequest.getEmployee().getCompany().getCompanyId(),
				hRISChangeRequest.getDataDictionary().getEntityMaster()
						.getEntityId(), hRISChangeRequest.getDataDictionary()
						.getFormID());

		EmployeeListFormPage employeeListFormPage = generalLogic
				.getEmployeeHRISChangeRequestData(
						hRISChangeRequest.getHrisChangeRequestId(),
						hRISChangeRequest.getEmployee().getCompany()
								.getCompanyId(), dynamicForm.getMetaData(),
						languageId);
		hrisChangeRequestForm.setNewValue(employeeListFormPage.getNewValue());
		hrisChangeRequestForm.setOldValue(employeeListFormPage.getOldValue());
		/*ID ENCRYPT*/
		hrisChangeRequestForm.setHrisChangeRequestId(FormatPreserveCryptoUtil.encrypt(hRISChangeRequest
				.getHrisChangeRequestId()));

		 
		 
		if (hRISChangeRequest.getHrisStatusMaster().getHrisStatusName()
				.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_WITHDRAWN)) {
			hrisChangeRequestForm
					.setUserStatus(PayAsiaConstants.HRIS_STATUS_WITHDRAWN);
		} else {
			hrisChangeRequestForm
					.setUserStatus(PayAsiaConstants.HRIS_STATUS_SUBMITTED);

		}

		hrisChangeRequestForm.setCreatedDate(DateUtils
				.timeStampToString(hRISChangeRequest.getCreatedDate()));
		List<HRISChangeRequestWorkflow> applicationWorkflows = new ArrayList<>(
				hRISChangeRequest.getChangeRequestWorkflows());
		Collections.sort(applicationWorkflows, new HRISWorkFlowComp());
		Integer workFlowCount = 0;

		for (HRISChangeRequestReviewer hangeRequestReviewer : hRISChangeRequest
				.getHrisChangeRequestReviewers()) {
			if (hangeRequestReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {
				hrisChangeRequestForm
						.setChangeRequestReviewer1(getEmployeeNameWithNumber(hangeRequestReviewer
								.getEmployeeReviewer()));
				hrisChangeRequestForm
						.setChangeRequestReviewer1Id(hangeRequestReviewer
								.getEmployeeReviewer().getEmployeeId());

			}

			if (hangeRequestReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {
				hrisChangeRequestForm
						.setChangeRequestReviewer2(getEmployeeNameWithNumber(hangeRequestReviewer
								.getEmployeeReviewer()));
				hrisChangeRequestForm
						.setChangeRequestReviewer2Id(hangeRequestReviewer
								.getEmployeeReviewer().getEmployeeId());

			}

			if (hangeRequestReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				hrisChangeRequestForm
						.setChangeRequestReviewer3(getEmployeeNameWithNumber(hangeRequestReviewer
								.getEmployeeReviewer()));
				hrisChangeRequestForm
						.setChangeRequestReviewer3Id(hangeRequestReviewer
								.getEmployeeReviewer().getEmployeeId());

			}
		}

		hrisChangeRequestForm.setTotalNoOfReviewers(hRISChangeRequest
				.getHrisChangeRequestReviewers().size());
		List<HRISChangeRequestWorkflowDTO> hRISChangeRequestWorkflowDTOs = new ArrayList<>();
		for (HRISChangeRequestWorkflow hRISChangeRequestWorkflow : applicationWorkflows) {

			HRISChangeRequestWorkflowDTO hRISChangeRequestWorkflowDTO = new HRISChangeRequestWorkflowDTO();
			hRISChangeRequestWorkflowDTO.setRemarks(hRISChangeRequestWorkflow
					.getRemarks());
			hRISChangeRequestWorkflowDTO.setStatus(hRISChangeRequestWorkflow
					.getHrisStatusMaster().getHrisStatusName());
			hRISChangeRequestWorkflowDTO.setCreatedDate(DateUtils
					.timeStampToString(hRISChangeRequestWorkflow
							.getCreatedDate()));
			hRISChangeRequestWorkflowDTO.setOrder(workFlowCount);

			hRISChangeRequestWorkflowDTOs.add(hRISChangeRequestWorkflowDTO);
			workFlowCount++;
		}
		hrisChangeRequestForm
				.sethRISChangeRequestWorkflowDTOs(hRISChangeRequestWorkflowDTOs);
		return hrisChangeRequestForm;
	}

	@Override
	public HrisChangeRequestForm withdrawChangeRequest(
			Long hrisChangeRequestId, Long employeeId, Long languageId) {
		HrisChangeRequestForm hrisChangeRequestForm = new HrisChangeRequestForm();

		Employee employeeVO = employeeDAO.findById(employeeId);

		HRISChangeRequest hRISChangeRequest = hRISChangeRequestDAO
				.findByIdAndEmployeeId(hrisChangeRequestId, employeeId);

		HRISStatusMaster hRISStatusMaster = hrisStatusMasterDAO
				.findByName(PayAsiaConstants.HRIS_STATUS_WITHDRAWN);
		hRISChangeRequest.setHrisStatusMaster(hRISStatusMaster);

		hRISChangeRequestDAO.update(hRISChangeRequest);

		for (HRISChangeRequestReviewer hrisChangeRequestReviewer : hRISChangeRequest
				.getHrisChangeRequestReviewers()) {
			hrisChangeRequestReviewer.setPending(false);
			hrisChangeRequestReviewerDAO.update(hrisChangeRequestReviewer);
		}

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
				hRISChangeRequest.getEmployee().getCompany().getCompanyId(),
				hRISChangeRequest.getDataDictionary().getEntityMaster()
						.getEntityId(), hRISChangeRequest.getDataDictionary()
						.getFormID());

		EmployeeListFormPage employeeListFormPage = generalLogic
				.getEmployeeHRISChangeRequestData(
						hRISChangeRequest.getHrisChangeRequestId(),
						hRISChangeRequest.getEmployee().getCompany()
								.getCompanyId(), dynamicForm.getMetaData(),
						languageId);

		hrisMailLogic
				.sendWithdrawEmailForHRISDataChange(
						hRISChangeRequest.getEmployee().getCompany()
								.getCompanyId(),
						hRISChangeRequest,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_WITHDRAW,
						employeeVO, employeeListFormPage);
		return hrisChangeRequestForm;
	}

	private String getEmployeeNameWithNumber(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}

	
	@Override
	public Long getEmployeeIdByCode(String employeeNumber,Long companyID){
		return employeeDAO.findByNumber(employeeNumber, companyID).getEmployeeId();
		
	}
}
