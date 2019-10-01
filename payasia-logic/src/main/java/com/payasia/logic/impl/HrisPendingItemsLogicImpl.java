package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.HRISChangeRequestWorkflowDTO;
import com.payasia.common.dto.HrisPendingItemsConditionDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.HrisPendingItemWorkflowRes;
import com.payasia.common.form.HrisPendingItemsForm;
import com.payasia.common.form.HrisPendingItemsFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.HRISChangeRequestDAO;
import com.payasia.dao.HRISChangeRequestReviewerDAO;
import com.payasia.dao.HRISChangeRequestWorkflowDAO;
import com.payasia.dao.HRISChangeWorkflowDAO;
import com.payasia.dao.HRISStatusMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequestReviewer;
import com.payasia.dao.bean.HRISChangeRequestWorkflow;
import com.payasia.dao.bean.HRISChangeWorkflow;
import com.payasia.dao.bean.HRISStatusMaster;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.HRISMailLogic;
import com.payasia.logic.HrisPendingItemsLogic;
import com.payasia.logic.MultilingualLogic;

@Component
public class HrisPendingItemsLogicImpl implements HrisPendingItemsLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HrisPendingItemsLogicImpl.class);

	@Resource
	HRISChangeRequestReviewerDAO hRISChangeRequestReviewerDAO;

	@Resource
	HRISChangeWorkflowDAO hRISChangeWorkflowDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	HRISStatusMasterDAO hRISStatusMasterDAO;

	@Resource
	HRISChangeRequestDAO hrisChangeRequestDAO;

	@Resource
	HRISChangeRequestWorkflowDAO hRISChangeRequestWorkflowDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	GeneralLogic generalLogic;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	HRISMailLogic hrisMailLogic;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	MultilingualLogic multilingualLogic;

	@Override
	public HrisPendingItemsFormResponse getPendingItems(Long employeeId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, Long languageId) {
		HrisPendingItemsConditionDTO conditionDTO = new HrisPendingItemsConditionDTO();
		conditionDTO.setEmployeeId(employeeId);

		if(!StringUtils.isEmpty(searchCondition) &&  !StringUtils.isEmpty(searchText))
		{
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_CREATED_BY)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedBy(searchText.trim());
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
		Company companyVO = companyDAO.findById(companyId);

		Integer recordSize = hRISChangeRequestReviewerDAO
				.getCountHRISChangeRequestReviewers(conditionDTO);

		List<HRISChangeRequestReviewer> hRISChangeRequestReviewers = null;
		hRISChangeRequestReviewers = hRISChangeRequestReviewerDAO
				.findHRISChangeRequestReviewers(employeeId, pageDTO, sortDTO,
						conditionDTO);

		List<HrisPendingItemsForm> pendingItemsForms = new ArrayList<HrisPendingItemsForm>();
		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();
		for (HRISChangeRequestReviewer hRISChangeRequestReviewer : hRISChangeRequestReviewers) {
			HrisPendingItemsForm hrisPendingItemsForm = new HrisPendingItemsForm();
			/*ID ENCRYPT*/
			hrisPendingItemsForm.sethRISChangeRequestReviewerId(FormatPreserveCryptoUtil.encrypt(hRISChangeRequestReviewer.getHrisChangeRequestReviewerId()));

			DynamicForm dynamicForm = null;
			if (formIdMap.containsKey(hRISChangeRequestReviewer
					.getHrisChangeRequest().getDataDictionary().getFormID())) {
				dynamicForm = formIdMap
						.get(hRISChangeRequestReviewer.getHrisChangeRequest()
								.getDataDictionary().getFormID());
			} else {
				dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
						hRISChangeRequestReviewer.getHrisChangeRequest()
								.getEmployee().getCompany().getCompanyId(),
						hRISChangeRequestReviewer.getHrisChangeRequest()
								.getDataDictionary().getEntityMaster()
								.getEntityId(), hRISChangeRequestReviewer
								.getHrisChangeRequest().getDataDictionary()
								.getFormID());
				formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
			}

			EmployeeListFormPage employeeListFormPage = generalLogic
					.getEmployeeHRISChangeRequestData(hRISChangeRequestReviewer
							.getHrisChangeRequest().getHrisChangeRequestId(),
							hRISChangeRequestReviewer.getHrisChangeRequest()
									.getEmployee().getCompany().getCompanyId(),
							dynamicForm.getMetaData(), languageId);

			hrisPendingItemsForm
					.setNewValue(employeeListFormPage.getNewValue());
			hrisPendingItemsForm
					.setOldValue(employeeListFormPage.getOldValue());

			String multilingualFieldLabel = multilingualLogic
					.convertFieldLabelToSpecificLanguage(languageId, companyId,
							hRISChangeRequestReviewer.getHrisChangeRequest()
									.getDataDictionary().getDataDictionaryId());
			if (StringUtils.isBlank(multilingualFieldLabel)) {
				multilingualFieldLabel = hRISChangeRequestReviewer
						.getHrisChangeRequest().getDataDictionary().getLabel();
			}

			hrisPendingItemsForm.setField(multilingualFieldLabel);
			hrisPendingItemsForm
					.setCreatedBy(getEmployeeNameWithNumber(hRISChangeRequestReviewer
							.getHrisChangeRequest().getEmployee()));
			hrisPendingItemsForm.setCreatedDate(DateUtils.timeStampToString(
					hRISChangeRequestReviewer.getHrisChangeRequest()
							.getCreatedDate(), companyVO.getDateFormat()));

			pendingItemsForms.add(hrisPendingItemsForm);
		}

		HrisPendingItemsFormResponse response = new HrisPendingItemsFormResponse();
		response.setRows(pendingItemsForms);
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
	//@PreAuthorize("hasRole('PRIV_HRIS_PENDING_ITEMS')")
	public HrisPendingItemsFormResponse getAllPendingItems(Long employeeId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, Long languageId) {
		HrisPendingItemsConditionDTO conditionDTO = new HrisPendingItemsConditionDTO();
		conditionDTO.setEmployeeId(employeeId);

		if(!StringUtils.isEmpty(searchCondition) &&  !StringUtils.isEmpty(searchText))
		{
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.HRIS_CHANGE_REQUEST_CREATED_BY)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCreatedBy(searchText.trim());
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

		Integer recordSize = hRISChangeRequestReviewerDAO
				.getCountHRISChangeRequestReviewers(conditionDTO);

		List<HRISChangeRequestReviewer> hRISChangeRequestReviewers = null;
		hRISChangeRequestReviewers = hRISChangeRequestReviewerDAO
				.findHRISChangeRequestReviewers(employeeId, pageDTO, sortDTO,
						conditionDTO);

		List<HrisPendingItemsForm> pendingItemsForms = new ArrayList<HrisPendingItemsForm>();
		for (HRISChangeRequestReviewer hRISChangeRequestReviewer : hRISChangeRequestReviewers) {
			HrisPendingItemsForm hrisPendingItemsForm = new HrisPendingItemsForm();
			
			Long reviewId= hRISChangeRequestReviewer.getHrisChangeRequestReviewerId();
			hrisPendingItemsForm = reviewHrisPendingItem(reviewId, companyId, languageId, employeeId);
			
			pendingItemsForms.add(hrisPendingItemsForm);
		}

		HrisPendingItemsFormResponse response = new HrisPendingItemsFormResponse();
		response.setRows(pendingItemsForms);
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
	public HrisPendingItemsForm reviewHrisPendingItem(Long reviewId,
			Long companyId, Long languageId,Long employeeId) {
		HrisPendingItemsForm hrisPendingItemsForm = new HrisPendingItemsForm();
		HRISChangeRequestReviewer hRISChangeRequestReviewer = hRISChangeRequestReviewerDAO.findById(reviewId);
		
		if(hRISChangeRequestReviewer!=null && employeeId.equals(hRISChangeRequestReviewer.getEmployeeReviewer().getEmployeeId())){ 
	
		HRISChangeRequest hrisChangeRequest = hRISChangeRequestReviewer.getHrisChangeRequest();
		/*ID ENCRYPT*/
		hrisPendingItemsForm.sethRISChangeRequestReviewerId(FormatPreserveCryptoUtil.encrypt(reviewId));
		hrisPendingItemsForm
				.setCreatedBy(getEmployeeNameWithNumber(hRISChangeRequestReviewer
						.getHrisChangeRequest().getEmployee()));

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
				hRISChangeRequestReviewer.getHrisChangeRequest().getEmployee()
						.getCompany().getCompanyId(), hRISChangeRequestReviewer
						.getHrisChangeRequest().getDataDictionary()
						.getEntityMaster().getEntityId(),
				hRISChangeRequestReviewer.getHrisChangeRequest()
						.getDataDictionary().getFormID());

		EmployeeListFormPage employeeListFormPage = generalLogic
				.getEmployeeHRISChangeRequestData(hRISChangeRequestReviewer
						.getHrisChangeRequest().getHrisChangeRequestId(),
						hRISChangeRequestReviewer.getHrisChangeRequest()
								.getEmployee().getCompany().getCompanyId(),
						dynamicForm.getMetaData(), languageId);
		hrisPendingItemsForm.setNewValue(employeeListFormPage.getNewValue());
		hrisPendingItemsForm.setOldValue(employeeListFormPage.getOldValue());
		String multilingualFieldLabel = multilingualLogic
				.convertFieldLabelToSpecificLanguage(languageId, companyId,
						hrisChangeRequest.getDataDictionary()
								.getDataDictionaryId());
		if (StringUtils.isBlank(multilingualFieldLabel)) {
			multilingualFieldLabel = hrisChangeRequest.getDataDictionary()
					.getLabel();
		}
		hrisPendingItemsForm.setField(multilingualFieldLabel);

		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";

		List<HRISChangeWorkflow> hrisChangeWorkFlows = hRISChangeWorkflowDAO
				.findByCompanyId(hRISChangeRequestReviewer
						.getHrisChangeRequest().getEmployee().getCompany()
						.getCompanyId());

		for (HRISChangeWorkflow hRISChangeWorkflow : hrisChangeWorkFlows) {
			if (hRISChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.equalsIgnoreCase(PayAsiaConstants.HRIS_DEF_ALLOW_OVERRIDE)) {
				allowOverride = hRISChangeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();

			} else if (hRISChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.equalsIgnoreCase(PayAsiaConstants.HRIS_DEF_ALLOW_REJECT)) {
				allowReject = hRISChangeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			} else if (hRISChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.equalsIgnoreCase(PayAsiaConstants.HRIS_DEF_ALLOW_FORWARD)) {
				allowForward = hRISChangeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			} else if (hRISChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.equalsIgnoreCase(PayAsiaConstants.HRIS_DEF_ALLOW_APPROVE)) {
				allowApprove = hRISChangeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			}

		}
		int reviewOrder = 0;

		for (HRISChangeRequestReviewer hangeRequestReviewer : hrisChangeRequest
				.getHrisChangeRequestReviewers()) {
			if (hangeRequestReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {

				hrisPendingItemsForm.setApplyTo(hangeRequestReviewer
						.getEmployeeReviewer().getEmail());
				hrisPendingItemsForm
						.setChangeRequestReviewer1(getEmployeeNameWithNumber(hangeRequestReviewer
								.getEmployeeReviewer()));
				hrisPendingItemsForm.setApplyToId(hangeRequestReviewer
						.getEmployeeReviewer().getEmployeeId());
				if (hRISChangeRequestReviewer.getEmployeeReviewer()
						.getEmployeeId() == hangeRequestReviewer
						.getEmployeeReviewer().getEmployeeId()) {
					reviewOrder = 1;
					if (allowOverride.length() == 3
							&& allowOverride.substring(0, 1).equals("1")) {
						hrisPendingItemsForm.setCanOverride(true);
					} else {
						hrisPendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(0, 1).equals("1")) {
						hrisPendingItemsForm.setCanReject(true);
					} else {
						hrisPendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(0, 1).equals("1")) {
						hrisPendingItemsForm.setCanApprove(true);
					} else {
						hrisPendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(0, 1).equals("1")) {
						hrisPendingItemsForm.setCanForward(true);
					} else {
						hrisPendingItemsForm.setCanForward(false);
					}
				}
			}

			if (hangeRequestReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {
				hrisPendingItemsForm
						.setChangeRequestReviewer2(getEmployeeNameWithNumber(hangeRequestReviewer
								.getEmployeeReviewer()));
				hrisPendingItemsForm
						.setChangeRequestReviewer2Id(hangeRequestReviewer
								.getEmployeeReviewer().getEmployeeId());

				if (hRISChangeRequestReviewer.getEmployeeReviewer()
						.getEmployeeId() == hangeRequestReviewer
						.getEmployeeReviewer().getEmployeeId()) {
					reviewOrder = 2;
					if (allowOverride.length() == 3
							&& allowOverride.substring(1, 2).equals("1")) {
						hrisPendingItemsForm.setCanOverride(true);
					} else {
						hrisPendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(1, 2).equals("1")) {
						hrisPendingItemsForm.setCanReject(true);
					} else {
						hrisPendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(1, 2).equals("1")) {
						hrisPendingItemsForm.setCanApprove(true);
					} else {
						hrisPendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(1, 2).equals("1")) {
						hrisPendingItemsForm.setCanForward(true);
					} else {
						hrisPendingItemsForm.setCanForward(false);
					}
				}

			}

			if (hangeRequestReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				hrisPendingItemsForm
						.setChangeRequestReviewer3(getEmployeeNameWithNumber(hangeRequestReviewer
								.getEmployeeReviewer()));
				hrisPendingItemsForm
						.setChangeRequestReviewer3Id(hangeRequestReviewer
								.getEmployeeReviewer().getEmployeeId());
				if (hRISChangeRequestReviewer.getEmployeeReviewer()
						.getEmployeeId() == hangeRequestReviewer
						.getEmployeeReviewer().getEmployeeId()) {
					reviewOrder = 2;
					if (allowOverride.length() == 3
							&& allowOverride.substring(2, 3).equals("1")) {
						hrisPendingItemsForm.setCanOverride(true);
					} else {
						hrisPendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(2, 3).equals("1")) {
						hrisPendingItemsForm.setCanReject(true);
					} else {
						hrisPendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(2, 3).equals("1")) {
						hrisPendingItemsForm.setCanApprove(true);
					} else {
						hrisPendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(2, 3).equals("1")) {
						hrisPendingItemsForm.setCanForward(true);
					} else {
						hrisPendingItemsForm.setCanForward(false);
					}
				}
			}
		}

		hrisPendingItemsForm.setTotalNoOfReviewers(hrisChangeRequest
				.getHrisChangeRequestReviewers().size());

		Integer workFlowCount = 0;
		List<HRISChangeRequestWorkflowDTO> hRISChangeRequestWorkflowDTOs = new ArrayList<>();
		List<HRISChangeRequestWorkflow> hRISChangeRequestWorkflows = new ArrayList<>(
				hrisChangeRequest.getChangeRequestWorkflows());

		hrisPendingItemsForm.setUserStatus(hrisChangeRequest
				.getHrisStatusMaster().getHrisStatusName());
		hrisPendingItemsForm.setCreatedDate(DateUtils
				.timeStampToString(hrisChangeRequest.getCreatedDate()));
		for (HRISChangeRequestWorkflow hRISChangeRequestWorkflow : hRISChangeRequestWorkflows) {

			HRISChangeRequestWorkflowDTO hRISChangeRequestWorkflowDTO = new HRISChangeRequestWorkflowDTO();
			hRISChangeRequestWorkflowDTO.setRemarks(hRISChangeRequestWorkflow
					.getRemarks());
			hRISChangeRequestWorkflowDTO.setStatus(hRISChangeRequestWorkflow
					.getHrisStatusMaster().getHrisStatusName());
			hRISChangeRequestWorkflowDTO.setCreatedDate(DateUtils
					.timeStampToString(hRISChangeRequestWorkflow
							.getCreatedDate()));
			workFlowCount++;

			if (hRISChangeRequestWorkflow.getEmployee().getEmployeeId() == hRISChangeRequestReviewer
					.getHrisChangeRequest().getEmployee().getEmployeeId()) {

				hRISChangeRequestWorkflowDTO.setOrder(0);

			}

			if (hrisPendingItemsForm.getApplyToId() != null
					&& hrisPendingItemsForm.getApplyToId() == hRISChangeRequestWorkflow
							.getEmployee().getEmployeeId()) {
				hRISChangeRequestWorkflowDTO.setOrder(1);
			}

			if (hrisPendingItemsForm.getChangeRequestReviewer2Id() != null
					&& hrisPendingItemsForm.getChangeRequestReviewer2Id() == hRISChangeRequestWorkflow
							.getEmployee().getEmployeeId()) {
				hRISChangeRequestWorkflowDTO.setOrder(2);
			}

			if (hrisPendingItemsForm.getChangeRequestReviewer3Id() != null
					&& hrisPendingItemsForm.getChangeRequestReviewer3Id() == hRISChangeRequestWorkflow
							.getEmployee().getEmployeeId()) {
				hRISChangeRequestWorkflowDTO.setOrder(3);
			}
			hRISChangeRequestWorkflowDTOs.add(hRISChangeRequestWorkflowDTO);
		}
		hrisPendingItemsForm
				.sethRISChangeRequestWorkflowDTOs(hRISChangeRequestWorkflowDTOs);
		hrisPendingItemsForm
				.setCreatedBy(getEmployeeNameWithNumber(hrisChangeRequest
						.getEmployee()));
		hrisPendingItemsForm.setCreatedById(hrisChangeRequest.getEmployee()
				.getEmployeeId());
		if (reviewOrder == 1
				&& hrisPendingItemsForm.getChangeRequestReviewer2Id() != null) {
			Employee empReviewer2 = employeeDAO.findById(hrisPendingItemsForm
					.getChangeRequestReviewer2Id());
			hrisPendingItemsForm
					.setForwardTo(getEmployeeNameWithNumber(empReviewer2));
			hrisPendingItemsForm.setForwardToId(empReviewer2.getEmployeeId());
		} else if (reviewOrder == 2
				&& hrisPendingItemsForm.getChangeRequestReviewer3Id() != null) {
			Employee empReviewer3 = employeeDAO.findById(hrisPendingItemsForm
					.getChangeRequestReviewer3Id());
			hrisPendingItemsForm
					.setForwardTo(getEmployeeNameWithNumber(empReviewer3));
			hrisPendingItemsForm.setForwardToId(empReviewer3.getEmployeeId());
		} else {
			hrisPendingItemsForm.setForwardTo("");
		}

		return hrisPendingItemsForm;
	  }
	   return null;
	}

	@Override
	public HrisPendingItemWorkflowRes forward(
			HrisPendingItemsForm hrisPendingItemsForm, Long employeeId,
			Long languageId) {
		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = new HrisPendingItemWorkflowRes();
		HRISChangeRequestReviewer hRISChangeRequestReviewer = hRISChangeRequestReviewerDAO
				.findById(hrisPendingItemsForm.gethRISChangeRequestReviewerId());
	   if(hRISChangeRequestReviewer!=null && employeeId.equals(hRISChangeRequestReviewer.getEmployeeReviewer().getEmployeeId())){ 
		
		HRISChangeRequest hrisChangeRequest = hRISChangeRequestReviewer.getHrisChangeRequest();

		HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
		HRISChangeRequestWorkflow hRISChangeRequestWorkflow = new HRISChangeRequestWorkflow();
		Employee forwardToEmployee = null;
		Employee employee = employeeDAO.findById(employeeId);

		HRISStatusMaster hRISStatusMasterApproved = hRISStatusMasterDAO
				.findByName(PayAsiaConstants.HRIS_STATUS_APPROVED);

		hrisChangeRequest.setHrisStatusMaster(hRISStatusMasterApproved);
		forwardToEmployee = employeeDAO.findById(hrisPendingItemsForm
				.getForwardToId());
		hRISChangeRequestWorkflow.setForwardTo(forwardToEmployee.getEmail());
		HRISChangeRequestReviewer hRISChangeRequestReviewer2 = hRISChangeRequestReviewerDAO
				.findById(hRISChangeRequestReviewer
						.getHrisChangeRequestReviewerId() + 1);
		hRISChangeRequestReviewer2.setPending(true);
		hRISChangeRequestReviewerDAO.update(hRISChangeRequestReviewer2);

		hrisChangeRequestDAO.update(hrisChangeRequest);

		hRISChangeRequestReviewer.setPending(false);
		hRISChangeRequestReviewer.setEmployeeReviewer(employee);
		hRISChangeRequestReviewerDAO.update(hRISChangeRequestReviewer);

		hRISChangeRequestWorkflow.setEmployee(employee);
		hRISChangeRequestWorkflow.setEmailCC(hrisPendingItemsForm.getEmailCC());
		hRISChangeRequestWorkflow
				.setHrisChangeRequest(hRISChangeRequestReviewer
						.getHrisChangeRequest());
		hRISChangeRequestWorkflow.setHrisStatusMaster(hRISStatusMasterApproved);
		String remarks = "";
		try {
			remarks = URLDecoder.decode(hrisPendingItemsForm.getReason(),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		hRISChangeRequestWorkflow.setRemarks(remarks);
		hRISChangeRequestWorkflow.setCreatedDate(DateUtils
				.getCurrentTimestampWithTime());

		hRISChangeRequestWorkflow.setNewValue(hrisChangeRequest.getNewValue());

		hrisChangeRequestWorkflowPersistObj = hRISChangeRequestWorkflowDAO
				.savePersist(hRISChangeRequestWorkflow);

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
				hrisChangeRequest.getEmployee().getCompany().getCompanyId(),
				hrisChangeRequest.getDataDictionary().getEntityMaster()
						.getEntityId(), hrisChangeRequest.getDataDictionary()
						.getFormID());

		EmployeeListFormPage employeeListFormPage = generalLogic
				.getEmployeeHRISChangeRequestData(
						hrisChangeRequest.getHrisChangeRequestId(),
						hrisChangeRequest.getEmployee().getCompany()
								.getCompanyId(), dynamicForm.getMetaData(),
						languageId);

		hrisMailLogic.sendPendingEmailForHRISDataChange(hrisChangeRequest
				.getEmployee().getCompany().getCompanyId(),
				hrisChangeRequestWorkflowPersistObj,
				PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_FORWARD,
				employee, remarks, forwardToEmployee, employeeListFormPage);

		return hrisPendingItemWorkflowRes;
	   }
	   
	  return null;
	}

	@Override
	public HrisPendingItemWorkflowRes reject(
			HrisPendingItemsForm hrisPendingItemsForm, Long employeeId,
			Long languageId) {
		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = new HrisPendingItemWorkflowRes();
		HRISChangeRequestReviewer hRISChangeRequestReviewer = hRISChangeRequestReviewerDAO.findById(hrisPendingItemsForm.gethRISChangeRequestReviewerId());
		
		if(hRISChangeRequestReviewer!=null && employeeId.equals(hRISChangeRequestReviewer.getEmployeeReviewer().getEmployeeId())){ 

		HRISChangeRequest hrisChangeRequest = hRISChangeRequestReviewer.getHrisChangeRequest();
		HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
		HRISChangeRequestWorkflow hRISChangeRequestWorkflow = new HRISChangeRequestWorkflow();
		Employee employee = employeeDAO.findById(employeeId);
		HRISStatusMaster hRISStatusMasterRejected = hRISStatusMasterDAO
				.findByName(PayAsiaConstants.HRISE_STATUS_REJECTED);
		hrisChangeRequest.setHrisStatusMaster(hRISStatusMasterRejected);
		hrisChangeRequestDAO.update(hrisChangeRequest);

		for (HRISChangeRequestReviewer applicationReviewer : hrisChangeRequest
				.getHrisChangeRequestReviewers()) {
			applicationReviewer.setPending(false);
			hRISChangeRequestReviewerDAO.update(applicationReviewer);
		}

		hRISChangeRequestWorkflow.setEmployee(employee);
		hRISChangeRequestWorkflow.setEmailCC(hrisPendingItemsForm.getEmailCC());
		hRISChangeRequestWorkflow.setHrisChangeRequest(hrisChangeRequest);
		hRISChangeRequestWorkflow.setHrisStatusMaster(hRISStatusMasterRejected);
		String remarks = "";
		try {
			remarks = URLDecoder.decode(hrisPendingItemsForm.getReason(),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		hRISChangeRequestWorkflow.setRemarks(remarks);

		hRISChangeRequestWorkflow.setCreatedDate(DateUtils
				.getCurrentTimestampWithTime());
		if (hrisPendingItemsForm.getForwardTo() != null) {
			hRISChangeRequestWorkflow.setForwardTo(hrisPendingItemsForm
					.getForwardTo());
		}
		hrisChangeRequestWorkflowPersistObj = hRISChangeRequestWorkflowDAO
				.savePersist(hRISChangeRequestWorkflow);

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
				hrisChangeRequest.getEmployee().getCompany().getCompanyId(),
				hrisChangeRequest.getDataDictionary().getEntityMaster()
						.getEntityId(), hrisChangeRequest.getDataDictionary()
						.getFormID());

		EmployeeListFormPage employeeListFormPage = generalLogic
				.getEmployeeHRISChangeRequestData(
						hrisChangeRequest.getHrisChangeRequestId(),
						hrisChangeRequest.getEmployee().getCompany()
								.getCompanyId(), dynamicForm.getMetaData(),
						languageId);

		hrisMailLogic.sendAcceptRejectMailForHRISDataChange(hrisChangeRequest
				.getEmployee().getCompany().getCompanyId(),
				hrisChangeRequestWorkflowPersistObj,
				PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_REJECT,
				employee, remarks, employeeListFormPage);
		  return hrisPendingItemWorkflowRes;
	   }
		return null;
	}

	@Override
	public HrisPendingItemWorkflowRes accept(
			HrisPendingItemsForm hrisPendingItemsForm, Long employeeId,
			Long languageId) {
		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = new HrisPendingItemWorkflowRes();
		HRISChangeRequestReviewer hRISChangeRequestReviewer = hRISChangeRequestReviewerDAO.findById(hrisPendingItemsForm.gethRISChangeRequestReviewerId());
		
	  if(hRISChangeRequestReviewer!=null && employeeId.equals(hRISChangeRequestReviewer.getEmployeeReviewer().getEmployeeId())){ 
		
		HRISChangeRequest hrisChangeRequest = hRISChangeRequestReviewer.getHrisChangeRequest();
		HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
		HRISChangeRequestWorkflow hRISChangeRequestWorkflow = new HRISChangeRequestWorkflow();
		Employee employee = employeeDAO.findById(employeeId);
		HRISStatusMaster hRISStatusMasterCompleted = hRISStatusMasterDAO
				.findByName(PayAsiaConstants.HRIS_STATUS_COMPLETED);

		hrisChangeRequest.setHrisStatusMaster(hRISStatusMasterCompleted);
		hrisChangeRequestDAO.update(hrisChangeRequest);

		hRISChangeRequestReviewer.setPending(false);
		hRISChangeRequestReviewer.setEmployeeReviewer(employee);
		hRISChangeRequestReviewerDAO.update(hRISChangeRequestReviewer);

		hRISChangeRequestWorkflow.setEmployee(employee);
		hRISChangeRequestWorkflow.setEmailCC(hrisPendingItemsForm.getEmailCC());
		hRISChangeRequestWorkflow.setHrisChangeRequest(hrisChangeRequest);
		hRISChangeRequestWorkflow.setHrisStatusMaster(hRISStatusMasterCompleted);
		String remarks = "";
//		try {
//			remarks = URLDecoder.decode(hrisPendingItemsForm.getReason(),
//					"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
		hRISChangeRequestWorkflow.setRemarks(remarks);
		hRISChangeRequestWorkflow.setCreatedDate(DateUtils
				.getCurrentTimestampWithTime());

		hRISChangeRequestWorkflow.setNewValue(hrisChangeRequest.getNewValue());

		hrisChangeRequestWorkflowPersistObj = hRISChangeRequestWorkflowDAO
				.savePersist(hRISChangeRequestWorkflow);
		employeeDetailLogic.updateEmpNewValueInExistingXML(
				hrisChangeRequest.getHrisChangeRequestId(), hrisChangeRequest
						.getEmployee().getCompany().getCompanyId());

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
				hrisChangeRequest.getEmployee().getCompany().getCompanyId(),
				hrisChangeRequest.getDataDictionary().getEntityMaster()
						.getEntityId(), hrisChangeRequest.getDataDictionary()
						.getFormID());

		EmployeeListFormPage employeeListFormPage = generalLogic
				.getEmployeeHRISChangeRequestData(
						hrisChangeRequest.getHrisChangeRequestId(),
						hrisChangeRequest.getEmployee().getCompany()
								.getCompanyId(), dynamicForm.getMetaData(),
						languageId);

		hrisMailLogic.sendAcceptRejectMailForHRISDataChange(hrisChangeRequest
				.getEmployee().getCompany().getCompanyId(),
				hrisChangeRequestWorkflowPersistObj,
				PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPROVE,
				employee, remarks, employeeListFormPage);
		  return hrisPendingItemWorkflowRes;
		}
		return null;
	}

	@Override
	public HrisPendingItemWorkflowRes searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String empName,
			String empNumber, Long companyId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		Company cmp = companyDAO.findById(companyId);
		
		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		
//		int recordSize = employeeDAO.getCountForCondition(conditionDTO,
//				companyId);
		int recordSize = employeeDAO.getCountForGroupCondition(conditionDTO,
				companyId, cmp.getCompanyGroup().getGroupId());
		
//		List<Employee> finalList = employeeDAO.findByCondition(conditionDTO,
//				pageDTO, sortDTO, companyId);
		List<Employee> finalList = employeeDAO.findByGroupCondition(conditionDTO,
				pageDTO, sortDTO, companyId, cmp.getCompanyGroup().getGroupId());
		
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
			employeeForm.setEmail(employee.getEmail());
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		HrisPendingItemWorkflowRes response = new HrisPendingItemWorkflowRes();
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

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}


}
