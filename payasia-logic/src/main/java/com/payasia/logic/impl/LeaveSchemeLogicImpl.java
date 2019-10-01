package com.payasia.logic.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.EmployeeFilter;
import com.mind.payasia.xml.bean.EmployeeFilterTemplate;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.CustomFieldsDTO;
import com.payasia.common.dto.CustomRoundingDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.FirstMonthCustomDTO;
import com.payasia.common.dto.LeaveEntitlementDTO;
import com.payasia.common.dto.LeaveSchemeAppCodeDTO;
import com.payasia.common.dto.LeaveSchemeConditionDTO;
import com.payasia.common.dto.LeaveSchemeDTO;
import com.payasia.common.dto.LeaveSchemeProcDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.LeaveSchemeResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeFilterXMLUtil;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.ShortlistOperatorEnum;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveSchemeTypeAvailingLeaveDAO;
import com.payasia.dao.LeaveSchemeTypeCustomFieldDAO;
import com.payasia.dao.LeaveSchemeTypeCustomProrationDAO;
import com.payasia.dao.LeaveSchemeTypeCustomRoundingDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveSchemeTypeEntitlementDAO;
import com.payasia.dao.LeaveSchemeTypeGrantConditionDAO;
import com.payasia.dao.LeaveSchemeTypeGrantingDAO;
import com.payasia.dao.LeaveSchemeTypeProrationDAO;
import com.payasia.dao.LeaveSchemeTypeShortListDAO;
import com.payasia.dao.LeaveSchemeTypeWorkflowDAO;
import com.payasia.dao.LeaveSchemeTypeYearEndDAO;
import com.payasia.dao.LeaveSchemeWorkflowDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave;
import com.payasia.dao.bean.LeaveSchemeTypeCustomField;
import com.payasia.dao.bean.LeaveSchemeTypeCustomProration;
import com.payasia.dao.bean.LeaveSchemeTypeCustomRounding;
import com.payasia.dao.bean.LeaveSchemeTypeEntitlement;
import com.payasia.dao.bean.LeaveSchemeTypeGrantCondition;
import com.payasia.dao.bean.LeaveSchemeTypeGranting;
import com.payasia.dao.bean.LeaveSchemeTypeProration;
import com.payasia.dao.bean.LeaveSchemeTypeShortList;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSchemeTypeYearEnd;
import com.payasia.dao.bean.LeaveSchemeWorkflow;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralFilterLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LeaveSchemeLogic;

@Component
public class LeaveSchemeLogicImpl implements LeaveSchemeLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(LeaveSchemeLogicImpl.class);

	@Resource
	LeaveSchemeDAO leaveSchemeDAO;
	@Resource
	LeaveSchemeTypeGrantConditionDAO leaveSchemeTypeGrantConditionDAO;
	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	LeaveSchemeWorkflowDAO leaveSchemeWorkflowDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;
	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	LeaveSchemeTypeEntitlementDAO leaveSchemeTypeEntitlementDAO;

	@Resource
	LeaveSchemeTypeCustomRoundingDAO leaveSchemeTypeCustomRoundingDAO;

	@Resource
	LeaveSchemeTypeCustomFieldDAO leaveSchemeTypeCustomFieldDAO;

	@Resource
	LeaveSchemeTypeWorkflowDAO leaveSchemeTypeWorkflowDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The general dao. */
	@Resource
	GeneralDAO generalDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	LeaveSchemeTypeShortListDAO leaveSchemeTypeShortListDAO;
	@Resource
	LeaveSchemeTypeAvailingLeaveDAO leaveSchemeTypeAvailingLeaveDAO;

	@Resource
	LeaveSchemeTypeGrantingDAO leaveSchemeTypeGrantingDAO;

	@Resource
	LeaveSchemeTypeProrationDAO leaveSchemeTypeProrationDAO;

	@Resource
	LeaveSchemeTypeYearEndDAO leaveSchemeTypeYearEndDAO;

	@Resource
	LeaveSchemeTypeCustomProrationDAO leaveSchemeTypeCustomProrationDAO;

	@Resource
	FiltersInfoUtilsLogic filtersInfoUtilsLogic;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	LeaveApplicationDAO leaveApplicationDAO;

	@Resource
	LeavePreferenceDAO leavePreferenceDAO;

	@Resource
	EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;

	@Resource
	GeneralFilterLogic generalFilterLogic;
	@Resource
	EmployeeLeaveReviewerDAO employeeleaveReviewerDAO;
	@Resource
	GeneralLogic generalLogic;

	@Override
	public LeaveSchemeResponse viewLeaveScheme(Long companyId, String searchCondition, String searchText,
			PageRequest pageDTO, SortCondition sortDTO) {
		LeaveSchemeConditionDTO conditionDTO = new LeaveSchemeConditionDTO();

		if ("schemeName".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setSchemeName("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}

		if ("status".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setStatus(URLDecoder.decode(searchText, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}

			}
		}

		List<LeaveSchemeForm> leaveSchemeFormList = new ArrayList<LeaveSchemeForm>();

		List<LeaveScheme> LeaveSchemeVOList = leaveSchemeDAO.getAllLeaveSchemeByConditionCompany(companyId,
				conditionDTO, pageDTO, sortDTO);

		for (LeaveScheme LeaveSchemeVO : LeaveSchemeVOList) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();

			Long leaveSchemeTypeCount = leaveSchemeTypeDAO.findByConditionCount(LeaveSchemeVO.getLeaveSchemeId(),
					companyId);
			StringBuilder leaveTypeCount = new StringBuilder();

			leaveTypeCount.append("<span class='Text'><h2>" + String.valueOf(leaveSchemeTypeCount) + "</h2></span>");
			leaveTypeCount.append("<span class='Textsmall' style='padding-top: 5px;'>&nbsp;Types</span>");
			leaveTypeCount.append("<br><br>");
			/* ID ENCRYPT*/
			leaveTypeCount
					.append("<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'configureLeaveTypes("
							+ FormatPreserveCryptoUtil.encrypt(LeaveSchemeVO.getLeaveSchemeId()) + ")'>[Edit]</a></span>");
			leaveSchemeForm.setNoOfItems(String.valueOf(leaveTypeCount));

			StringBuilder employeeLeaveSchemeCount = new StringBuilder();

			employeeLeaveSchemeCount.append("<span class='Text'><h2>"
					+ String.valueOf(LeaveSchemeVO.getEmployeeLeaveSchemes().size()) + "</h2></span>");
			employeeLeaveSchemeCount.append("<span class='Textsmall' style='padding-top: 5px;'>&nbsp;Employees</span>");
			employeeLeaveSchemeCount.append("<br><br>");
			/* ID ENCRYPT*/
			employeeLeaveSchemeCount
					.append("<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'viewAssignedEmployees("
							+ FormatPreserveCryptoUtil.encrypt(LeaveSchemeVO.getLeaveSchemeId()) + ")'>[View]</a></span>");
			leaveSchemeForm.setEmployeesAssigned(String.valueOf(employeeLeaveSchemeCount));

			StringBuilder leaveSchemeName = new StringBuilder();
			leaveSchemeName.append("<span class='jqGridColumnHighlight'>");
			leaveSchemeName.append(LeaveSchemeVO.getSchemeName());
			leaveSchemeName.append("</span>");

			leaveSchemeForm.setTemplateName(String.valueOf(leaveSchemeName));
			if (LeaveSchemeVO.getVisibility() == true) {
				leaveSchemeForm.setActive("Active");
			}
			if (LeaveSchemeVO.getVisibility() == false) {
				leaveSchemeForm.setActive("InActive");
			}
			leaveSchemeForm.setLeaveSchemeId(FormatPreserveCryptoUtil.encrypt(LeaveSchemeVO.getLeaveSchemeId()));
			leaveSchemeFormList.add(leaveSchemeForm);
		}
		LeaveSchemeResponse response = new LeaveSchemeResponse();
		int recordSize = (leaveSchemeDAO.getCountForAllLeaveScheme(companyId, conditionDTO)).intValue();
		;
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
		response.setRows(leaveSchemeFormList);

		return response;
	}

	@Override
	public LeaveSchemeResponse assignedLeaveSchemes(Long companyId, String searchCondition, String searchText,
			PageRequest pageDTO, SortCondition sortDTO, Long leaveTypeId) {

		List<LeaveSchemeForm> leaveSchemeFormList = new ArrayList<LeaveSchemeForm>();
		LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO.findLeaveTypeByCompId(leaveTypeId, companyId);
		if(leaveTypeMaster == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		Set<LeaveSchemeType> leaveSchemeTypes = leaveTypeMaster.getLeaveSchemeTypes();

		for (LeaveSchemeType leaveSchemeType : leaveSchemeTypes) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();

			leaveSchemeForm.setTemplateName(leaveSchemeType.getLeaveScheme().getSchemeName());
			if (leaveSchemeType.getLeaveScheme().getVisibility() == true) {
				leaveSchemeForm.setActive("Active");
			}
			if (leaveSchemeType.getLeaveScheme().getVisibility() == false) {
				leaveSchemeForm.setActive("InActive");
			}
			/* ID ENCRYPT*/
			leaveSchemeForm.setLeaveSchemeId(FormatPreserveCryptoUtil.encrypt(leaveSchemeType.getLeaveScheme().getLeaveSchemeId()));
			leaveSchemeFormList.add(leaveSchemeForm);
		}
		LeaveSchemeResponse response = new LeaveSchemeResponse();
		int recordSize = leaveSchemeTypes.size();

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
		response.setRows(leaveSchemeFormList);

		return response;
	}

	@Override
	public String addLeaveScheme(Long companyId, LeaveSchemeForm leaveSchemeForm) {
		boolean status = true;
		LeaveScheme leaveSchemeVO = new LeaveScheme();

		Company company = companyDAO.findById(companyId);
		leaveSchemeVO.setCompany(company);
		leaveSchemeVO.setSchemeName(leaveSchemeForm.getTemplateName());
		leaveSchemeVO.setVisibility(true);

		status = checkLeaveSchemeName(null, leaveSchemeForm.getTemplateName(), companyId);

		if (!status) {
			return "available";
		} else {
			leaveSchemeVO = leaveSchemeDAO.saveReturn(leaveSchemeVO);
		}

		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO.findAll();

		LeaveSchemeWorkflow leaveSchemeWorkflowVO = leaveSchemeWorkflowDAO.findByLeaveSchemeIdRuleName(
				leaveSchemeVO.getLeaveSchemeId(), PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL);
		if (leaveSchemeWorkflowVO != null) {
			leaveSchemeWorkflowDAO.deleteByCondition(leaveSchemeForm.getLeaveSchemeId());
		}

		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL1)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "1",
					workFlowRuleMasterList);
			LeaveSchemeWorkflow LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
			leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);
		}
		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL2)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "2",
					workFlowRuleMasterList);
			LeaveSchemeWorkflow LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
			leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);
		}
		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL3)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "3",
					workFlowRuleMasterList);
			LeaveSchemeWorkflow LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
			leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);
		}

		String allowOverrideStr = "";
		if (leaveSchemeForm.getAllowOverrideL1() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (leaveSchemeForm.getAllowOverrideL2() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (leaveSchemeForm.getAllowOverrideL3() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		Long allowOverrideId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE,
				allowOverrideStr.trim(), workFlowRuleMasterList);
		LeaveSchemeWorkflow LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

		WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(allowOverrideId);
		LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
		LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
		leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);

		String allowRejectStr = "";
		if (leaveSchemeForm.getAllowRejectL1() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (leaveSchemeForm.getAllowRejectL2() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (leaveSchemeForm.getAllowRejectL3() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		Long allowRejectId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT, allowRejectStr.trim(),
				workFlowRuleMasterList);
		LeaveSchemeWorkflow leaveSchemeWorkflowAllowReject = new LeaveSchemeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowRejectMaster = workFlowRuleMasterDAO.findByID(allowRejectId);
		leaveSchemeWorkflowAllowReject.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
		leaveSchemeWorkflowAllowReject.setLeaveScheme(leaveSchemeVO);
		leaveSchemeWorkflowDAO.save(leaveSchemeWorkflowAllowReject);

		String allowForwardStr = "";
		if (leaveSchemeForm.getAllowForward1() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (leaveSchemeForm.getAllowForward2() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (leaveSchemeForm.getAllowForward3() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		Long allowForwardId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD,
				allowForwardStr.trim(), workFlowRuleMasterList);
		LeaveSchemeWorkflow leaveSchemeWorkflowAllowForward = new LeaveSchemeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowForward = workFlowRuleMasterDAO.findByID(allowForwardId);
		leaveSchemeWorkflowAllowForward.setWorkFlowRuleMaster(workFlowRuleAllowForward);
		leaveSchemeWorkflowAllowForward.setLeaveScheme(leaveSchemeVO);
		leaveSchemeWorkflowDAO.save(leaveSchemeWorkflowAllowForward);

		String allowApproveStr = "";
		if (leaveSchemeForm.getAllowApprove1() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (leaveSchemeForm.getAllowApprove2() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (leaveSchemeForm.getAllowApprove3() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		Long allowApproveId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE,
				allowApproveStr.trim(), workFlowRuleMasterList);
		LeaveSchemeWorkflow leaveSchemeWorkflowAllowApprove = new LeaveSchemeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowApprove = workFlowRuleMasterDAO.findByID(allowApproveId);
		leaveSchemeWorkflowAllowApprove.setWorkFlowRuleMaster(workFlowRuleAllowApprove);
		leaveSchemeWorkflowAllowApprove.setLeaveScheme(leaveSchemeVO);
		leaveSchemeWorkflowDAO.save(leaveSchemeWorkflowAllowApprove);

		return "notavailable";

	}

	@Override
	public void deleteLeaveScheme(Long companyId, Long leaveSchemeId) {
		
		LeaveScheme leaveSchemeVO = leaveSchemeDAO.findSchemeByCompanyID(leaveSchemeId, companyId);
		if(leaveSchemeVO == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		leaveSchemeWorkflowDAO.deleteByCondition(leaveSchemeId);
		leaveSchemeDAO.delete(leaveSchemeVO);
	}

	@Override
	public LeaveSchemeForm getLeaveScheme(Long companyId, Long leaveSchemeId) {
		List<LeaveSchemeWorkflow> leaveSchemeWorkflowVOList = leaveSchemeWorkflowDAO.findByCondition(leaveSchemeId);
		LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();

		LeaveScheme leaveSchemeVO = leaveSchemeDAO.findSchemeByCompanyID(leaveSchemeId, companyId);
		if(leaveSchemeVO == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		leaveSchemeForm.setTemplateName(leaveSchemeVO.getSchemeName());
		if (leaveSchemeVO.getVisibility() == true) {
			leaveSchemeForm.setVisibility(true);
		}
		if (leaveSchemeVO.getVisibility() == false) {
			leaveSchemeForm.setVisibility(false);
		}

		for (LeaveSchemeWorkflow leaveSchemeWorkflowVO : leaveSchemeWorkflowVOList) {

			if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
				leaveSchemeForm.setWorkFlowLevel(leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue());
			}

			if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
				String allowOverRideVal = leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowOverRideVal.charAt(0);
				char atlevelTwo = allowOverRideVal.charAt(1);
				char atlevelThree = allowOverRideVal.charAt(2);

				if (atlevelOne == '0') {
					leaveSchemeForm.setAllowOverrideL1(false);
				}
				if (atlevelOne == '1') {
					leaveSchemeForm.setAllowOverrideL1(true);
				}
				if (atlevelTwo == '0') {
					leaveSchemeForm.setAllowOverrideL2(false);
				}
				if (atlevelTwo == '1') {
					leaveSchemeForm.setAllowOverrideL2(true);
				}
				if (atlevelThree == '0') {
					leaveSchemeForm.setAllowOverrideL3(false);
				}
				if (atlevelThree == '1') {
					leaveSchemeForm.setAllowOverrideL3(true);
				}
			}

			if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
				String allowRejectVal = leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowRejectVal.charAt(0);
				char atlevelTwo = allowRejectVal.charAt(1);
				char atlevelThree = allowRejectVal.charAt(2);

				if (atlevelOne == '0') {
					leaveSchemeForm.setAllowRejectL1(false);
				}
				if (atlevelOne == '1') {
					leaveSchemeForm.setAllowRejectL1(true);
				}
				if (atlevelTwo == '0') {
					leaveSchemeForm.setAllowRejectL2(false);
				}
				if (atlevelTwo == '1') {
					leaveSchemeForm.setAllowRejectL2(true);
				}
				if (atlevelThree == '0') {
					leaveSchemeForm.setAllowRejectL3(false);
				}
				if (atlevelThree == '1') {
					leaveSchemeForm.setAllowRejectL3(true);
				}
			}
			if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD)) {
				String allowForwardVal = leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowForwardVal.charAt(0);
				char atlevelTwo = allowForwardVal.charAt(1);
				char atlevelThree = allowForwardVal.charAt(2);

				if (atlevelOne == '0') {
					leaveSchemeForm.setAllowForward1(false);
				}
				if (atlevelOne == '1') {
					leaveSchemeForm.setAllowForward1(true);
				}
				if (atlevelTwo == '0') {
					leaveSchemeForm.setAllowForward2(false);
				}
				if (atlevelTwo == '1') {
					leaveSchemeForm.setAllowForward2(true);
				}
				if (atlevelThree == '0') {
					leaveSchemeForm.setAllowForward3(false);
				}
				if (atlevelThree == '1') {
					leaveSchemeForm.setAllowForward3(true);
				}
			}
			if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
				String allowApproveVal = leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowApproveVal.charAt(0);
				char atlevelTwo = allowApproveVal.charAt(1);
				char atlevelThree = allowApproveVal.charAt(2);

				if (atlevelOne == '0') {
					leaveSchemeForm.setAllowApprove1(false);
				}
				if (atlevelOne == '1') {
					leaveSchemeForm.setAllowApprove1(true);
				}
				if (atlevelTwo == '0') {
					leaveSchemeForm.setAllowApprove2(false);
				}
				if (atlevelTwo == '1') {
					leaveSchemeForm.setAllowApprove2(true);
				}
				if (atlevelThree == '0') {
					leaveSchemeForm.setAllowApprove3(false);
				}
				if (atlevelThree == '1') {
					leaveSchemeForm.setAllowApprove3(true);
				}
			}
		}
		return leaveSchemeForm;

	}

	@Override
	public LeaveSchemeForm getLeaveTypeLeaveScheme(Long companyId, Long leaveSchemeId, Long leaveTypeId) {

		List<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows = leaveSchemeTypeWorkflowDAO
				.findByCondition(leaveTypeId, companyId);
		if(leaveSchemeTypeWorkflows == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}

		List<LeaveSchemeWorkflow> leaveSchemeWorkflowVOList = leaveSchemeWorkflowDAO.findByCondition(leaveSchemeId);
		LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();

		if (!leaveSchemeTypeWorkflows.isEmpty()) {

			for (LeaveSchemeTypeWorkflow leaveTypeWorkFlow : leaveSchemeTypeWorkflows) {

				if (leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
					leaveSchemeForm.setWorkFlowLevel(leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleValue());
				}

				if (leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
					String allowOverRideVal = leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleValue();
					char atlevelOne = allowOverRideVal.charAt(0);
					char atlevelTwo = allowOverRideVal.charAt(1);
					char atlevelThree = allowOverRideVal.charAt(2);

					if (atlevelOne == '0') {
						leaveSchemeForm.setAllowOverrideL1(false);
					}
					if (atlevelOne == '1') {
						leaveSchemeForm.setAllowOverrideL1(true);
					}
					if (atlevelTwo == '0') {
						leaveSchemeForm.setAllowOverrideL2(false);
					}
					if (atlevelTwo == '1') {
						leaveSchemeForm.setAllowOverrideL2(true);
					}
					if (atlevelThree == '0') {
						leaveSchemeForm.setAllowOverrideL3(false);
					}
					if (atlevelThree == '1') {
						leaveSchemeForm.setAllowOverrideL3(true);
					}
				}

				if (leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
					String allowRejectVal = leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleValue();
					char atlevelOne = allowRejectVal.charAt(0);
					char atlevelTwo = allowRejectVal.charAt(1);
					char atlevelThree = allowRejectVal.charAt(2);

					if (atlevelOne == '0') {
						leaveSchemeForm.setAllowRejectL1(false);
					}
					if (atlevelOne == '1') {
						leaveSchemeForm.setAllowRejectL1(true);
					}
					if (atlevelTwo == '0') {
						leaveSchemeForm.setAllowRejectL2(false);
					}
					if (atlevelTwo == '1') {
						leaveSchemeForm.setAllowRejectL2(true);
					}
					if (atlevelThree == '0') {
						leaveSchemeForm.setAllowRejectL3(false);
					}
					if (atlevelThree == '1') {
						leaveSchemeForm.setAllowRejectL3(true);
					}
				}
				if (leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD)) {
					String allowForwardVal = leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleValue();
					char atlevelOne = allowForwardVal.charAt(0);
					char atlevelTwo = allowForwardVal.charAt(1);
					char atlevelThree = allowForwardVal.charAt(2);

					if (atlevelOne == '0') {
						leaveSchemeForm.setAllowForward1(false);
					}
					if (atlevelOne == '1') {
						leaveSchemeForm.setAllowForward1(true);
					}
					if (atlevelTwo == '0') {
						leaveSchemeForm.setAllowForward2(false);
					}
					if (atlevelTwo == '1') {
						leaveSchemeForm.setAllowForward2(true);
					}
					if (atlevelThree == '0') {
						leaveSchemeForm.setAllowForward3(false);
					}
					if (atlevelThree == '1') {
						leaveSchemeForm.setAllowForward3(true);
					}
				}
				if (leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
					String allowApproveVal = leaveTypeWorkFlow.getWorkFlowRuleMaster().getRuleValue();
					char atlevelOne = allowApproveVal.charAt(0);
					char atlevelTwo = allowApproveVal.charAt(1);
					char atlevelThree = allowApproveVal.charAt(2);

					if (atlevelOne == '0') {
						leaveSchemeForm.setAllowApprove1(false);
					}
					if (atlevelOne == '1') {
						leaveSchemeForm.setAllowApprove1(true);
					}
					if (atlevelTwo == '0') {
						leaveSchemeForm.setAllowApprove2(false);
					}
					if (atlevelTwo == '1') {
						leaveSchemeForm.setAllowApprove2(true);
					}
					if (atlevelThree == '0') {
						leaveSchemeForm.setAllowApprove3(false);
					}
					if (atlevelThree == '1') {
						leaveSchemeForm.setAllowApprove3(true);
					}
				}
			}

		} else {

			for (LeaveSchemeWorkflow leaveSchemeWorkflowVO : leaveSchemeWorkflowVOList) {

				if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
					leaveSchemeForm.setWorkFlowLevel(leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue());
				}

				if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
					String allowOverRideVal = leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
					char atlevelOne = allowOverRideVal.charAt(0);
					char atlevelTwo = allowOverRideVal.charAt(1);
					char atlevelThree = allowOverRideVal.charAt(2);

					if (atlevelOne == '0') {
						leaveSchemeForm.setAllowOverrideL1(false);
					}
					if (atlevelOne == '1') {
						leaveSchemeForm.setAllowOverrideL1(true);
					}
					if (atlevelTwo == '0') {
						leaveSchemeForm.setAllowOverrideL2(false);
					}
					if (atlevelTwo == '1') {
						leaveSchemeForm.setAllowOverrideL2(true);
					}
					if (atlevelThree == '0') {
						leaveSchemeForm.setAllowOverrideL3(false);
					}
					if (atlevelThree == '1') {
						leaveSchemeForm.setAllowOverrideL3(true);
					}
				}

				if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
					String allowRejectVal = leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
					char atlevelOne = allowRejectVal.charAt(0);
					char atlevelTwo = allowRejectVal.charAt(1);
					char atlevelThree = allowRejectVal.charAt(2);

					if (atlevelOne == '0') {
						leaveSchemeForm.setAllowRejectL1(false);
					}
					if (atlevelOne == '1') {
						leaveSchemeForm.setAllowRejectL1(true);
					}
					if (atlevelTwo == '0') {
						leaveSchemeForm.setAllowRejectL2(false);
					}
					if (atlevelTwo == '1') {
						leaveSchemeForm.setAllowRejectL2(true);
					}
					if (atlevelThree == '0') {
						leaveSchemeForm.setAllowRejectL3(false);
					}
					if (atlevelThree == '1') {
						leaveSchemeForm.setAllowRejectL3(true);
					}
				}
				if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD)) {
					String allowForwardVal = leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
					char atlevelOne = allowForwardVal.charAt(0);
					char atlevelTwo = allowForwardVal.charAt(1);
					char atlevelThree = allowForwardVal.charAt(2);

					if (atlevelOne == '0') {
						leaveSchemeForm.setAllowForward1(false);
					}
					if (atlevelOne == '1') {
						leaveSchemeForm.setAllowForward1(true);
					}
					if (atlevelTwo == '0') {
						leaveSchemeForm.setAllowForward2(false);
					}
					if (atlevelTwo == '1') {
						leaveSchemeForm.setAllowForward2(true);
					}
					if (atlevelThree == '0') {
						leaveSchemeForm.setAllowForward3(false);
					}
					if (atlevelThree == '1') {
						leaveSchemeForm.setAllowForward3(true);
					}
				}
				if (leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
					String allowApproveVal = leaveSchemeWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
					char atlevelOne = allowApproveVal.charAt(0);
					char atlevelTwo = allowApproveVal.charAt(1);
					char atlevelThree = allowApproveVal.charAt(2);

					if (atlevelOne == '0') {
						leaveSchemeForm.setAllowApprove1(false);
					}
					if (atlevelOne == '1') {
						leaveSchemeForm.setAllowApprove1(true);
					}
					if (atlevelTwo == '0') {
						leaveSchemeForm.setAllowApprove2(false);
					}
					if (atlevelTwo == '1') {
						leaveSchemeForm.setAllowApprove2(true);
					}
					if (atlevelThree == '0') {
						leaveSchemeForm.setAllowApprove3(false);
					}
					if (atlevelThree == '1') {
						leaveSchemeForm.setAllowApprove3(true);
					}
				}
			}

		}

		return leaveSchemeForm;

	}

	@Override
	public String configureLeaveScheme(LeaveSchemeForm leaveSchemeForm, Long companyId) {
		boolean status = true;
		
		/* ID DECRYPT */
		leaveSchemeForm.setLeaveSchemeId(FormatPreserveCryptoUtil.decrypt(leaveSchemeForm.getLeaveSchemeId()));
		status = checkLeaveSchemeName(leaveSchemeForm.getLeaveSchemeId(), leaveSchemeForm.getTemplateName(), companyId);
		LeaveScheme leaveSchemeVO = leaveSchemeDAO.findByID(leaveSchemeForm.getLeaveSchemeId());

		if (!status) {
			return "available";
		}
		if (status) {

			Company company = companyDAO.findById(companyId);
			leaveSchemeVO.setCompany(company);
			leaveSchemeVO.setSchemeName(leaveSchemeForm.getTemplateName());
			leaveSchemeVO.setVisibility(leaveSchemeForm.getVisibility());
			leaveSchemeDAO.update(leaveSchemeVO);
		}

		Integer workFlowRuleValue = Integer.valueOf(leaveSchemeForm.getWorkFlowLevel().substring(9));

		Set<LeaveSchemeType> leaveSchemeTypes = leaveSchemeVO.getLeaveSchemeTypes();
		Integer tempRuleValue = 0;
		for (LeaveSchemeType leaveSchemeType : leaveSchemeTypes) {

			if (!leaveSchemeType.isWorkflowChanged()) {
				Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows = leaveSchemeType.getLeaveSchemeTypeWorkflows();

				for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : leaveSchemeTypeWorkflows) {

					if (leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
							.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL.toUpperCase())) {
						tempRuleValue = workFlowRuleValue;
						if (Integer.parseInt(
								leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue()) > tempRuleValue) {
							tempRuleValue = Integer
									.parseInt(leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue());
						}

					}

				}
			}

		}

		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO.findAll();

		LeaveSchemeWorkflow leaveSchemeWorkflowVO = leaveSchemeWorkflowDAO.findByLeaveSchemeIdRuleName(
				leaveSchemeForm.getLeaveSchemeId(), PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL);
		if (leaveSchemeWorkflowVO != null) {
			leaveSchemeWorkflowDAO.deleteByCondition(leaveSchemeForm.getLeaveSchemeId());
		}

		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL1)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "1",
					workFlowRuleMasterList);
			LeaveSchemeWorkflow LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
			leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);
		}
		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL2)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "2",
					workFlowRuleMasterList);
			LeaveSchemeWorkflow LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
			leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);
		}
		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL3)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "3",
					workFlowRuleMasterList);
			LeaveSchemeWorkflow LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
			leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);
		}

		String allowOverrideStr = "";
		if (leaveSchemeForm.getAllowOverrideL1() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (leaveSchemeForm.getAllowOverrideL2() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (leaveSchemeForm.getAllowOverrideL3() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		Long allowOverrideId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE,
				allowOverrideStr.trim(), workFlowRuleMasterList);
		LeaveSchemeWorkflow LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

		WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(allowOverrideId);
		LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
		LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
		leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);

		String allowRejectStr = "";
		if (leaveSchemeForm.getAllowRejectL1() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (leaveSchemeForm.getAllowRejectL2() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (leaveSchemeForm.getAllowRejectL3() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		Long allowRejectId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT, allowRejectStr.trim(),
				workFlowRuleMasterList);
		LeaveSchemeWorkflow = new LeaveSchemeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowRejectMaster = workFlowRuleMasterDAO.findByID(allowRejectId);
		LeaveSchemeWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
		LeaveSchemeWorkflow.setLeaveScheme(leaveSchemeVO);
		leaveSchemeWorkflowDAO.save(LeaveSchemeWorkflow);

		String allowForwardStr = "";
		if (leaveSchemeForm.getAllowForward1() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (leaveSchemeForm.getAllowForward2() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (leaveSchemeForm.getAllowForward3() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		Long allowForwardId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD,
				allowForwardStr.trim(), workFlowRuleMasterList);
		LeaveSchemeWorkflow leaveSchemeWorkflowAllowForward = new LeaveSchemeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowForward = workFlowRuleMasterDAO.findByID(allowForwardId);
		leaveSchemeWorkflowAllowForward.setWorkFlowRuleMaster(workFlowRuleAllowForward);
		leaveSchemeWorkflowAllowForward.setLeaveScheme(leaveSchemeVO);
		leaveSchemeWorkflowDAO.save(leaveSchemeWorkflowAllowForward);

		String allowApproveStr = "";
		if (leaveSchemeForm.getAllowApprove1() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (leaveSchemeForm.getAllowApprove2() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (leaveSchemeForm.getAllowApprove3() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		Long allowApproveId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE,
				allowApproveStr.trim(), workFlowRuleMasterList);
		LeaveSchemeWorkflow leaveSchemeWorkflowAllowApprove = new LeaveSchemeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowApprove = workFlowRuleMasterDAO.findByID(allowApproveId);
		leaveSchemeWorkflowAllowApprove.setWorkFlowRuleMaster(workFlowRuleAllowApprove);
		leaveSchemeWorkflowAllowApprove.setLeaveScheme(leaveSchemeVO);
		leaveSchemeWorkflowDAO.save(leaveSchemeWorkflowAllowApprove);

		for (LeaveSchemeType leaveSchemeType : leaveSchemeTypes) {

			if (!leaveSchemeType.isWorkflowChanged()) {

				leaveSchemeTypeWorkflowDAO.deleteByCondition(leaveSchemeType.getLeaveSchemeTypeId());
				Set<LeaveSchemeWorkflow> leaveSchemeWorkflowVos = leaveSchemeType.getLeaveScheme()
						.getLeaveSchemeWorkflows();
				for (LeaveSchemeWorkflow changedLeaveSchemeWorkflowVo : leaveSchemeWorkflowVos) {
					LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflowVO = new LeaveSchemeTypeWorkflow();
					leaveSchemeTypeWorkflowVO.setLeaveSchemeType(leaveSchemeType);
					leaveSchemeTypeWorkflowVO
							.setWorkFlowRuleMaster(changedLeaveSchemeWorkflowVo.getWorkFlowRuleMaster());

					leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflowVO);
				}

			}

		}

		return "notavailable";

	}

	@Override
	public String configureLeaveSchemeTypeWorkFlow(LeaveSchemeForm leaveSchemeForm, Long companyId) {

		LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO.findById(leaveSchemeForm.getLeaveSchemeTypeId());

		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO.findAll();

		LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflowVO = leaveSchemeTypeWorkflowDAO.findByLeaveSchemeIdRuleName(
				leaveSchemeForm.getLeaveSchemeTypeId(), PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL);
		if (leaveSchemeTypeWorkflowVO != null) {
			leaveSchemeTypeWorkflowDAO.deleteByCondition(leaveSchemeForm.getLeaveSchemeTypeId());
		}

		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL1)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "1",
					workFlowRuleMasterList);
			LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = new LeaveSchemeTypeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			leaveSchemeTypeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			leaveSchemeTypeWorkflow.setLeaveSchemeType(leaveSchemeTypeVO);
			leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflow);
		}
		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL2)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "2",
					workFlowRuleMasterList);
			LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = new LeaveSchemeTypeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			leaveSchemeTypeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			leaveSchemeTypeWorkflow.setLeaveSchemeType(leaveSchemeTypeVO);
			leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflow);
		}
		if (leaveSchemeForm.getWorkFlowLevel().toUpperCase().equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOWL3)) {
			Long workFlowId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "3",
					workFlowRuleMasterList);
			LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = new LeaveSchemeTypeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(workFlowId);
			leaveSchemeTypeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			leaveSchemeTypeWorkflow.setLeaveSchemeType(leaveSchemeTypeVO);
			leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflow);
		}

		String allowOverrideStr = "";
		if (leaveSchemeForm.getAllowOverrideL1() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (leaveSchemeForm.getAllowOverrideL2() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (leaveSchemeForm.getAllowOverrideL3() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		Long allowOverrideId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE,
				allowOverrideStr.trim(), workFlowRuleMasterList);
		LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = new LeaveSchemeTypeWorkflow();

		WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO.findByID(allowOverrideId);
		leaveSchemeTypeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
		leaveSchemeTypeWorkflow.setLeaveSchemeType(leaveSchemeTypeVO);
		leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflow);

		String allowRejectStr = "";
		if (leaveSchemeForm.getAllowRejectL1() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (leaveSchemeForm.getAllowRejectL2() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (leaveSchemeForm.getAllowRejectL3() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		Long allowRejectId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT, allowRejectStr.trim(),
				workFlowRuleMasterList);
		LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflowReject = new LeaveSchemeTypeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowRejectMaster = workFlowRuleMasterDAO.findByID(allowRejectId);
		leaveSchemeTypeWorkflowReject.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
		leaveSchemeTypeWorkflowReject.setLeaveSchemeType(leaveSchemeTypeVO);
		leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflowReject);

		String allowForwardStr = "";
		if (leaveSchemeForm.getAllowForward1() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (leaveSchemeForm.getAllowForward2() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (leaveSchemeForm.getAllowForward3() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		Long allowForwardId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD,
				allowForwardStr.trim(), workFlowRuleMasterList);
		LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflowForward = new LeaveSchemeTypeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowForward = workFlowRuleMasterDAO.findByID(allowForwardId);
		leaveSchemeTypeWorkflowForward.setWorkFlowRuleMaster(workFlowRuleAllowForward);
		leaveSchemeTypeWorkflowForward.setLeaveSchemeType(leaveSchemeTypeVO);
		leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflowForward);

		String allowApproveStr = "";
		if (leaveSchemeForm.getAllowApprove1() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (leaveSchemeForm.getAllowApprove2() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (leaveSchemeForm.getAllowApprove3() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		Long allowApproveId = getWorkFlowMasterId(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE,
				allowApproveStr.trim(), workFlowRuleMasterList);
		LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflowApprove = new LeaveSchemeTypeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowApprove = workFlowRuleMasterDAO.findByID(allowApproveId);
		leaveSchemeTypeWorkflowApprove.setWorkFlowRuleMaster(workFlowRuleAllowApprove);
		leaveSchemeTypeWorkflowApprove.setLeaveSchemeType(leaveSchemeTypeVO);
		leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflowApprove);

		isWorkFlowChanged(leaveSchemeTypeVO.getLeaveSchemeTypeId(),
				leaveSchemeTypeVO.getLeaveScheme().getLeaveSchemeId());

		return "notavailable";

	}

	private void isWorkFlowChanged(Long leaveSchemeTypeId, Long leaveSchemeId) {
		Boolean workFlowChanged = false;

		List<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows = leaveSchemeTypeWorkflowDAO
				.findByCondition(leaveSchemeTypeId);

		List<LeaveSchemeWorkflow> leaveSchemeWorkflows = leaveSchemeWorkflowDAO.findByCondition(leaveSchemeId);

		if (leaveSchemeWorkflows.size() == leaveSchemeTypeWorkflows.size()) {

			int workFlowCount = 0;
			for (LeaveSchemeWorkflow leaveSchemeWorkflow : leaveSchemeWorkflows) {

				if (leaveSchemeWorkflow.getWorkFlowRuleMaster().getWorkFlowRuleId() != leaveSchemeTypeWorkflows
						.get(workFlowCount).getWorkFlowRuleMaster().getWorkFlowRuleId()) {
					workFlowChanged = true;
				}
				workFlowCount++;
			}

		} else {
			workFlowChanged = true;

		}

		LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO.findById(leaveSchemeTypeId);

		if (workFlowChanged) {
			leaveSchemeTypeVO.setWorkflowChanged(true);

		} else {
			leaveSchemeTypeVO.setWorkflowChanged(false);

		}
		leaveSchemeTypeDAO.update(leaveSchemeTypeVO);

	}

	public Long getWorkFlowMasterId(String ruleName, String ruleValue,
			List<WorkFlowRuleMaster> workFlowRuleMasterList) {
		for (WorkFlowRuleMaster workFlowRuleMaster : workFlowRuleMasterList) {
			if (ruleName.toUpperCase().equalsIgnoreCase(workFlowRuleMaster.getRuleName())
					&& ruleValue.equals(workFlowRuleMaster.getRuleValue())) {
				return workFlowRuleMaster.getWorkFlowRuleId();
			}
		}
		return null;
	}

	public boolean checkLeaveSchemeName(Long leaveSchemeId, String leaveSchemeName, Long companyId) {
		LeaveScheme leaveSchemeVO = leaveSchemeDAO.findByLeaveSchemeAndCompany(leaveSchemeId, leaveSchemeName,
				companyId);
		if (leaveSchemeVO == null) {
			return true;
		}
		return false;

	}

	@Override
	public Set<LeaveSchemeForm> getLeaveTypeList(Long companyId, Long leaveSchemeId) {
		Set<LeaveSchemeForm> leaveSchemeFormSet = new HashSet<LeaveSchemeForm>();

		List<LeaveTypeMaster> leaveSchemeTypeList = leaveTypeMasterDAO.findByConditionAndVisibility(leaveSchemeId,
				companyId);

		List<LeaveTypeMaster> leaveTypeMasterVOList = leaveTypeMasterDAO.findByCompanyAndVisibility(companyId);
		leaveTypeMasterVOList.removeAll(leaveSchemeTypeList);

		for (LeaveTypeMaster leaveTypeMasterVO : leaveTypeMasterVOList) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();

			leaveSchemeForm.setLeaveTypeId(leaveTypeMasterVO.getLeaveTypeId());
			leaveSchemeForm.setLeaveType(leaveTypeMasterVO.getLeaveTypeName());
			leaveSchemeFormSet.add(leaveSchemeForm);

		}

		return leaveSchemeFormSet;

	}

	@Override
	public Set<LeaveSchemeForm> getLeaveTypeListForCombo(Long companyId, Long leaveSchemeId, Long leaveSchemeTypeId) {
		Set<LeaveSchemeForm> leaveSchemeFormSet = new LinkedHashSet<LeaveSchemeForm>();

		Set<LeaveSchemeType> leaveSchemeTypeSetVO = new LinkedHashSet<LeaveSchemeType>(
				leaveSchemeTypeDAO.findByConditionLeaveScheme(leaveSchemeId, leaveSchemeTypeId, companyId));

		for (LeaveSchemeType leaveSchemeTypeVO : leaveSchemeTypeSetVO) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();

			leaveSchemeForm.setLeaveTypeId(leaveSchemeTypeVO.getLeaveSchemeTypeId());
			leaveSchemeForm.setLeaveType(leaveSchemeTypeVO.getLeaveTypeMaster().getLeaveTypeName());
			leaveSchemeFormSet.add(leaveSchemeForm);

		}

		return leaveSchemeFormSet;

	}

	@Override
	public LeaveSchemeResponse addLeaveType(String[] leaveTypeId, Long leaveSchemeId, Long companyId) {
		/* ID DECRYPT */
		leaveSchemeId = FormatPreserveCryptoUtil.decrypt(leaveSchemeId);
		LeaveSchemeResponse leaveSchemeResponse = new LeaveSchemeResponse();
		LeaveSchemeType leaveSchemeType = new LeaveSchemeType();

		/*LeaveScheme leaveSchemeVO = leaveSchemeDAO.findByID(leaveSchemeId);*/
		LeaveScheme leaveSchemeVO = leaveSchemeDAO.findSchemeByCompanyID(leaveSchemeId, companyId);
		if(leaveSchemeVO == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}

		for (int count = 0; count < leaveTypeId.length; count++) {
			LeaveTypeMaster leaveTypeMasterVO = leaveTypeMasterDAO.findById(Long.parseLong(leaveTypeId[count]));

			leaveSchemeType.setLeaveScheme(leaveSchemeVO);
			leaveSchemeType.setLeaveTypeMaster(leaveTypeMasterVO);
			leaveSchemeType.setVisibility(true);
			leaveSchemeType.setWorkflowChanged(false);
			LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO.saveReturn(leaveSchemeType);

			if (leaveSchemeVO.getEmployeeLeaveSchemes().size() > 0) {

				Set<EmployeeLeaveScheme> employeeLeaveSchemes = leaveSchemeVO.getEmployeeLeaveSchemes();
				for (EmployeeLeaveScheme employeeLeaveScheme : employeeLeaveSchemes) {

					EmployeeLeaveSchemeType employeeLeaveSchemeType = new EmployeeLeaveSchemeType();
					employeeLeaveSchemeType.setEmployeeLeaveScheme(employeeLeaveScheme);
					employeeLeaveSchemeType.setLeaveSchemeType(leaveSchemeTypeVO);
					employeeLeaveSchemeType.setBalance(new BigDecimal(0));
					employeeLeaveSchemeType.setCarriedForward(new BigDecimal(0));
					employeeLeaveSchemeType.setCredited(new BigDecimal(0));
					employeeLeaveSchemeType.setEncashed(new BigDecimal(0));
					employeeLeaveSchemeType.setForfeited(new BigDecimal(0));
					employeeLeaveSchemeType.setPending(new BigDecimal(0));
					employeeLeaveSchemeType.setTaken(new BigDecimal(0));
					employeeLeaveSchemeType.setActive(true);

					EmployeeLeaveSchemeType persistObj = employeeLeaveSchemeTypeDAO.saveReturn(employeeLeaveSchemeType);

					Set<LeaveSchemeWorkflow> leaveSchemeTypeWorkflowSet = employeeLeaveScheme.getLeaveScheme()
							.getLeaveSchemeWorkflows();

					saveEmployeeLeaveReviewerOnAssignLeaveType(leaveSchemeTypeWorkflowSet,
							employeeLeaveScheme.getEmployee().getEmployeeId(), persistObj, companyId);
				}

			}

			List<LeaveSchemeWorkflow> leaveSchemeWorkflows = leaveSchemeWorkflowDAO.findByCondition(leaveSchemeId);

			for (LeaveSchemeWorkflow leaveSchemeWorkflow : leaveSchemeWorkflows) {
				LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflowVO = new LeaveSchemeTypeWorkflow();
				leaveSchemeTypeWorkflowVO.setLeaveSchemeType(leaveSchemeTypeVO);
				leaveSchemeTypeWorkflowVO.setWorkFlowRuleMaster(leaveSchemeWorkflow.getWorkFlowRuleMaster());

				leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflowVO);
			}

		}

		leaveSchemeResponse.setMessageKey("payasia.leave.scheme.definition.leave.type.assigned.successfully");
		return leaveSchemeResponse;
	}

	private void saveEmployeeLeaveReviewerOnAssignLeaveType(Set<LeaveSchemeWorkflow> leaveSchemeWorkflowSet,
			Long employeeId, EmployeeLeaveSchemeType employeeLeaveSchemeType, Long companyId) {
		Integer workFlowRuleValue;
		Long employeeId1 = null;
		Long employeeId2 = null;
		Long employeeId3 = null;
		for (LeaveSchemeWorkflow leaveSchemeWorkflow : leaveSchemeWorkflowSet) {

			if (leaveSchemeWorkflow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL.toUpperCase())) {
				workFlowRuleValue = Integer.parseInt(leaveSchemeWorkflow.getWorkFlowRuleMaster().getRuleValue());
				Employee employee1Vo = employeeDAO.findByID(employeeId);
				for (int count = 1; count <= workFlowRuleValue; count++) {
					WorkFlowRuleMaster workFlowRuleListVO = workFlowRuleMasterDAO.findByRuleNameValue(
							PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, String.valueOf(count));

					List<Object[]> tuplelist = employeeleaveReviewerDAO.getEmployeeReviewersCountByCondition(employeeId,
							employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId(),
							workFlowRuleListVO.getWorkFlowRuleId());

					List<Long> revCountList = new ArrayList<>();
					if (!tuplelist.isEmpty()) {
						for (Object[] tuple : tuplelist) {
							revCountList.add((Long) tuple[1]);
						}
						Long maxRevCount = null;
						if (!revCountList.isEmpty()) {
							maxRevCount = Collections.max(revCountList);
						}

						for (Object[] tuple : tuplelist) {
							if (maxRevCount != null && maxRevCount.longValue() == ((Long) tuple[1]).longValue()) {
								if (count == 1) {
									employeeId1 = (Long) tuple[0];
								}
								if (count == 2) {
									employeeId2 = (Long) tuple[0];
								}
								if (count == 3) {
									employeeId3 = (Long) tuple[0];
								}
							}
						}
						Employee employeeRevVo = null;
						if (count == 1) {
							employeeRevVo = employeeDAO.findByID(employeeId1);
						}
						if (count == 2) {
							employeeRevVo = employeeDAO.findByID(employeeId2);
						}
						if (count == 3) {
							employeeRevVo = employeeDAO.findByID(employeeId3);
						}

						EmployeeLeaveReviewer empLeaveRev = new EmployeeLeaveReviewer();
						empLeaveRev.setCompanyId(companyId);
						empLeaveRev.setEmployee1(employee1Vo);
						empLeaveRev.setEmployee2(employeeRevVo);
						empLeaveRev.setEmployeeLeaveScheme(employeeLeaveSchemeType.getEmployeeLeaveScheme());
						empLeaveRev.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
						empLeaveRev.setWorkFlowRuleMaster(workFlowRuleListVO);
						employeeleaveReviewerDAO.save(empLeaveRev);
					}

				}

			}

		}
	}

	@Override
	public String configureClaimItem() {
		return null;
	}

	@Override
	public String addCustomField() {
		return null;
	}

	@Override
	public LeaveSchemeResponse viewLeaveType(Long leaveSchemeId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO) {
		List<LeaveSchemeForm> leaveSchemeFormList = new ArrayList<LeaveSchemeForm>();

		List<LeaveSchemeType> leaveSchemeTypeVOList = leaveSchemeTypeDAO.findByCondition(leaveSchemeId, companyId);
		if(leaveSchemeTypeVOList.size()>0){
			for (LeaveSchemeType leaveSchemeTypeVO : leaveSchemeTypeVOList) {
	
				LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
	
				Set<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailings = leaveSchemeTypeVO
						.getLeaveSchemeTypeAvailingLeaves();
				if (leaveSchemeTypeAvailings.iterator().hasNext()) {
	
					leaveSchemeForm.setMaxLeaves(leaveSchemeTypeAvailings.iterator().next().getMaxBlockLeave());
					leaveSchemeForm.setMinLeaves(leaveSchemeTypeAvailings.iterator().next().getMinBlockLeave());
	
				}
	
				Set<LeaveSchemeTypeGranting> leaveSchemeTypeGrantings = leaveSchemeTypeVO.getLeaveSchemeTypeGrantings();
	
				if (leaveSchemeTypeGrantings.iterator().hasNext()) {
					leaveSchemeForm.setDays(leaveSchemeTypeEntitlementDAO
							.findMaxDays(leaveSchemeTypeGrantings.iterator().next().getLeaveSchemeTypeGrantingId()));
				}
	
				leaveSchemeForm.setLeaveTypeId(leaveSchemeTypeVO.getLeaveTypeMaster().getLeaveTypeId());
				leaveSchemeForm.setLeaveType(leaveSchemeTypeVO.getLeaveTypeMaster().getLeaveTypeName());
				leaveSchemeForm.setLeaveSchemeTypeId(leaveSchemeTypeVO.getLeaveSchemeTypeId());
				StringBuilder leaveTypeYesNo = new StringBuilder();
	
				if (leaveSchemeTypeVO.getVisibility() == true) {
					/* ID ENCRYPT*/
					leaveTypeYesNo
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'editLSDLeaveType("
									+FormatPreserveCryptoUtil.encrypt(leaveSchemeTypeVO.getLeaveSchemeTypeId())
									+ ")'><span class='ctextgreen'>Yes</span></a>");
	
				}
				if (leaveSchemeTypeVO.getVisibility() == false) {
					/* ID ENCRYPT*/
					leaveTypeYesNo
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'editLSDLeaveType("
									+ FormatPreserveCryptoUtil.encrypt(leaveSchemeTypeVO.getLeaveSchemeTypeId())
									+ ")'><span class='ctextgray'>No</span></a>");
	
				}
				leaveSchemeForm.setActive(String.valueOf(leaveTypeYesNo));
	
				List<LeaveSchemeTypeShortList> leaveSchemeTypeShortList = leaveSchemeTypeShortListDAO
						.findByCondition(leaveSchemeTypeVO.getLeaveSchemeTypeId());
	
				List<LeaveSchemeTypeGranting> leaveSchemeConfigureList = leaveSchemeTypeGrantingDAO
						.findByCondition(leaveSchemeTypeVO.getLeaveSchemeTypeId());
	
				leaveSchemeTypeWorkflowDAO.findByCondition(leaveSchemeTypeVO.getLeaveSchemeTypeId());
	
				leaveSchemeWorkflowDAO.findByCondition(leaveSchemeId);
	
				StringBuilder leaveTypeShortList = new StringBuilder();
	
				if (!leaveSchemeTypeShortList.isEmpty()) {
					/* ID ENCRYPT*/
					leaveTypeShortList
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'openShortList("
									/*+ FormatPreserveCryptoUtil.encrypt(leaveSchemeTypeVO.getLeaveSchemeTypeId())*/
									+ leaveSchemeTypeVO.getLeaveSchemeTypeId()
									+ ")'><span class='ctextgreen'>Set</span></a>");
	
					leaveSchemeForm.setShortList(String.valueOf(leaveTypeShortList));
				} else {
					/* ID ENCRYPT*/
					leaveTypeShortList
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'openShortList("
									/*+ FormatPreserveCryptoUtil.encrypt(leaveSchemeTypeVO.getLeaveSchemeTypeId())*/
									+ leaveSchemeTypeVO.getLeaveSchemeTypeId()
									+ ")'><span class='ctextgray'>Not Set</span></a>");
					leaveSchemeForm.setShortList(String.valueOf(leaveTypeShortList));
				}
				StringBuilder leaveTypeConfigure = new StringBuilder();
				if (!leaveSchemeConfigureList.isEmpty()) {
					/* ID ENCRYPT*/
					leaveTypeConfigure
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'configureLeaveType("
									/*+ FormatPreserveCryptoUtil.encrypt(leaveSchemeTypeVO.getLeaveSchemeTypeId())*/
									+ leaveSchemeTypeVO.getLeaveSchemeTypeId()
									+ ")'><span class='ctextgreen'>Set</span></a>");
	
					leaveSchemeForm.setConfigureLeaveType(String.valueOf(leaveTypeConfigure));
				} else {
					/* ID ENCRYPT*/
					leaveTypeConfigure
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'configureLeaveType("
									/*+ FormatPreserveCryptoUtil.encrypt(leaveSchemeTypeVO.getLeaveSchemeTypeId())*/
									+ leaveSchemeTypeVO.getLeaveSchemeTypeId()
									+ ")'><span class='ctextgray'>Not Set</span></a>");
	
					leaveSchemeForm.setConfigureLeaveType(String.valueOf(leaveTypeConfigure));
				}
	
				StringBuilder leaveTypeWorkFlow = new StringBuilder();
	
				if (leaveSchemeTypeVO.isWorkflowChanged()) {
					/* ID ENCRYPT*/
					leaveTypeWorkFlow
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'configureLeaveTypeWorkFlow("
									+ FormatPreserveCryptoUtil.encrypt(leaveSchemeTypeVO.getLeaveSchemeTypeId())
									+ ")'><span class='ctextgreen'>Changed</span></a>");
	
					leaveSchemeForm.setWorkflowLeaveType(String.valueOf(leaveTypeWorkFlow));
				} else {
					/* ID ENCRYPT*/
					leaveTypeWorkFlow
							.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'configureLeaveTypeWorkFlow("
									+ FormatPreserveCryptoUtil.encrypt(leaveSchemeTypeVO.getLeaveSchemeTypeId())
									+ ")'><span class='ctextgray'>No Change</span></a>");
					leaveSchemeForm.setWorkflowLeaveType(String.valueOf(leaveTypeWorkFlow));
				}
	
				leaveSchemeFormList.add(leaveSchemeForm);
			}
	   }
		LeaveSchemeResponse response = new LeaveSchemeResponse();
		response.setRows(leaveSchemeFormList);

		return response;
	}

	@Override
	public LeaveSchemeResponse viewAssignedEmployees(Long leaveSchemeId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO) {
		List<LeaveSchemeForm> assignedEmployeesList = new ArrayList<LeaveSchemeForm>();

		LeaveScheme leaveSchemeVO = leaveSchemeDAO.findByID(leaveSchemeId);
		Set<EmployeeLeaveScheme> employeeLeaveSchemes = leaveSchemeVO.getEmployeeLeaveSchemes();
		for (EmployeeLeaveScheme employeeLeaveScheme : employeeLeaveSchemes) {

			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
			String employeeName = "";
			employeeName += employeeLeaveScheme.getEmployee().getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeLeaveScheme.getEmployee().getLastName())) {
				employeeName += employeeLeaveScheme.getEmployee().getLastName();
			}
			leaveSchemeForm.setEmployeeName(employeeName);
			leaveSchemeForm.setEmployeeNumber(employeeLeaveScheme.getEmployee().getEmployeeNumber());
			assignedEmployeesList.add(leaveSchemeForm);

		}

		LeaveSchemeResponse response = new LeaveSchemeResponse();
		response.setRows(assignedEmployeesList);

		return response;
	}

	@Override
	public void editLeaveType(Long leaveSchemeTypeId, LeaveSchemeForm leaveSchemeForm) {
		LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findById(leaveSchemeTypeId);
		leaveSchemeType.setVisibility(leaveSchemeForm.getVisibility());
		leaveSchemeTypeDAO.update(leaveSchemeType);
	}

	@Override
	public LeaveSchemeResponse deleteLeaveType(Long leaveSchemeTypeId, Long leaveSchemeId, Long companyId) {

		LeaveSchemeResponse leaveSchemeResponse = new LeaveSchemeResponse();
		LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findLeaveSchemeTypeByCompId(leaveSchemeTypeId, companyId);
		Boolean canBeDeleted = false;
		List<LeaveApplication> leaveApplications = leaveApplicationDAO.getLeaveApplicationLeaveSchemeTypeId(companyId,
				leaveSchemeTypeId);
		if (leaveApplications.size() == 0) {
			canBeDeleted = true;
		}
		if (canBeDeleted) {
			leaveSchemeTypeDAO.delete(leaveSchemeType);
			leaveSchemeResponse.setMessageKey("payasia.leave.type.deleted");
		} else {
			leaveSchemeResponse.setMessageKey("payasia.leave.type.cannot.be.deleted");
		}

		return leaveSchemeResponse;
	}

	@Override
	public LeaveSchemeForm getLeaveTypeForEdit(Long leaveSchemeTypeId) {
		LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO.findById(leaveSchemeTypeId);
		LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
		leaveSchemeForm.setLeaveType(leaveSchemeTypeVO.getLeaveTypeMaster().getLeaveTypeName());
		leaveSchemeForm.setVisibility(leaveSchemeTypeVO.getVisibility());
		return leaveSchemeForm;
	}

	@Override
	public List<LeaveSchemeForm> getAppcodeListForLeaveDistMeth() {
		List<LeaveSchemeForm> list = new ArrayList<>();
		List<AppCodeMaster> appCodeLeaveDistributionMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_DISTRIBUTION_METHOD);
		for (AppCodeMaster appCodeMaster : appCodeLeaveDistributionMethodList) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
			leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
			leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
			list.add(leaveSchemeForm);
		}
		return list;
	}

	@Override
	public List<LeaveSchemeForm> getAppcodeListForLeaveRoundingMeth() {
		List<LeaveSchemeForm> list = new ArrayList<>();
		List<AppCodeMaster> appCodeLeaveRoundingMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_ROUNDING_METHOD);
		for (AppCodeMaster appCodeMaster : appCodeLeaveRoundingMethodList) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
			leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
			leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
			list.add(leaveSchemeForm);
		}
		return list;
	}

	@Override
	public List<LeaveSchemeForm> getAppcodeListForApplyAfterFrom() {
		List<LeaveSchemeForm> list = new ArrayList<>();
		List<AppCodeMaster> appCodeLeaveRoundingMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_AVAILING_APPLY_AFTER_FROM);
		for (AppCodeMaster appCodeMaster : appCodeLeaveRoundingMethodList) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
			leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
			leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
			list.add(leaveSchemeForm);
		}
		return list;
	}

	@Override
	public List<LeaveSchemeForm> getAppcodeListForEarnedProrationMeth() {
		List<LeaveSchemeForm> list = new ArrayList<>();
		List<AppCodeMaster> appCodeLeaveProrationMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_PRORATION_METHOD);
		for (AppCodeMaster appCodeMaster : appCodeLeaveProrationMethodList) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
			leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
			leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
			if (!appCodeMaster.getCodeDesc().equals(PayAsiaConstants.LEAVE_PRORATION_METHOD_CALENDAR_DAYS)
					&& !appCodeMaster.getCodeDesc().equals(PayAsiaConstants.LEAVE_PRORATION_METHOD_WORKING_DAYS)) {
				list.add(leaveSchemeForm);
			}

		}
		return list;
	}

	@Override
	public List<LeaveSchemeForm> getAppcodeListForProrationMeth() {
		List<LeaveSchemeForm> list = new ArrayList<>();
		List<AppCodeMaster> appCodeLeaveProrationMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_PRORATION_METHOD);
		for (AppCodeMaster appCodeMaster : appCodeLeaveProrationMethodList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.LEAVE_PRORATION_METHOD_MONTH)) {
				LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
				leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
				leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
				list.add(leaveSchemeForm);
			}
			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.LEAVE_PRORATION_METHOD_WORKING_DAYS)) {
				LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
				leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
				leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
				list.add(leaveSchemeForm);
			}
			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.LEAVE_PRORATION_METHOD_CALENDAR_DAYS)) {
				LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
				leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
				leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
				list.add(leaveSchemeForm);
			}
		}
		return list;
	}

	@Override
	public List<LeaveSchemeForm> getAppcodeListForProrationBasedOnMeth() {
		List<LeaveSchemeForm> list = new ArrayList<>();
		List<AppCodeMaster> appCodeLeaveProrationBasedOnList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_TYPE_PRORATION_BASED_ON);
		for (AppCodeMaster appCodeMaster : appCodeLeaveProrationBasedOnList) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
			leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
			leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
			list.add(leaveSchemeForm);
		}
		return list;
	}

	@Override
	public List<LeaveSchemeForm> getAppcodeListForRoundOffRuleMeth() {
		List<LeaveSchemeForm> list = new ArrayList<>();
		List<AppCodeMaster> appCodeLeaveProrationBasedOnList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_TYPE_ROUND_OFF_RULE);
		for (AppCodeMaster appCodeMaster : appCodeLeaveProrationBasedOnList) {
			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
			leaveSchemeForm.setOptionId(appCodeMaster.getAppCodeID());
			leaveSchemeForm.setOptionValue(appCodeMaster.getCodeDesc());
			list.add(leaveSchemeForm);
		}
		return list;
	}

	@Override
	public LeaveSchemeForm getLeaveSchemeAppcodeList() {

		LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
		List<AppCodeMaster> appCodeLeaveCalendarList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_CALENDAR);
		List<AppCodeMaster> appCodeLeaveDistributionMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_DISTRIBUTION_METHOD);
		List<AppCodeMaster> appCodeLeaveRoundingMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_ROUNDING_METHOD);
		List<AppCodeMaster> appCodeLeaveProrationMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_PRORATION_METHOD);
		List<AppCodeMaster> appCodeLeaveProrationBasedOnList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_TYPE_PRORATION_BASED_ON);

		LeaveSchemeAppCodeDTO leaveSchemeAppCodeDTO = new LeaveSchemeAppCodeDTO();

		for (AppCodeMaster appCodeMaster : appCodeLeaveCalendarList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.LEAVE_ANNIVERSARY)) {

				leaveSchemeAppCodeDTO.setAnniversaryID(appCodeMaster.getAppCodeID());
			} else if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.CALENDAR)) {
				leaveSchemeAppCodeDTO.setCalendarID(appCodeMaster.getAppCodeID());
			}
		}

		for (AppCodeMaster appCodeMaster : appCodeLeaveDistributionMethodList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.LEAVE_DISTRIBUTION_METHOD_FIRST_PERIOD)) {

				leaveSchemeAppCodeDTO.setFirstPeriodID(appCodeMaster.getAppCodeID());
			} else if (appCodeMaster.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_DISTRIBUTION_METHOD_EARNED)) {
				leaveSchemeAppCodeDTO.setEarnedID(appCodeMaster.getAppCodeID());
			} else if (appCodeMaster.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_DISTRIBUTION_METHOD_LAST_PERIOD)) {
				leaveSchemeAppCodeDTO.setLastPeriodID(appCodeMaster.getAppCodeID());
			}
		}

		for (AppCodeMaster appCodeMaster : appCodeLeaveRoundingMethodList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.LEAVE_ROUNDING_METHOD_CUSTOM_ROUNDING)) {

				leaveSchemeAppCodeDTO.setCustomRoundingID(appCodeMaster.getAppCodeID());
			} else if (appCodeMaster.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_ROUNDING_METHOD_NO_ROUNDING)) {
				leaveSchemeAppCodeDTO.setNoRoundingID(appCodeMaster.getAppCodeID());
			}
		}

		for (AppCodeMaster appCodeMaster : appCodeLeaveProrationMethodList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.LEAVE_PRORATION_METHOD_MONTH)) {

				leaveSchemeAppCodeDTO.setMonthID(appCodeMaster.getAppCodeID());
			} else if (appCodeMaster.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_PRORATION_METHOD_WORKING_DAYS)) {
				leaveSchemeAppCodeDTO.setWeekdayID(appCodeMaster.getAppCodeID());
			}

			else if (appCodeMaster.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_PRORATION_METHOD_CALENDAR_DAYS)) {
				leaveSchemeAppCodeDTO.setCalendarDayID(appCodeMaster.getAppCodeID());
			}
		}

		for (AppCodeMaster appCodeMaster : appCodeLeaveProrationBasedOnList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase("Hire Date")) {

				leaveSchemeAppCodeDTO.setHireDateId(appCodeMaster.getAppCodeID());
			} else if (appCodeMaster.getCodeDesc().equalsIgnoreCase("Original Hire Date")) {
				leaveSchemeAppCodeDTO.setOriginalHireDateId(appCodeMaster.getAppCodeID());
			}

			else if (appCodeMaster.getCodeDesc().equalsIgnoreCase("Leave Scheme Date")) {
				leaveSchemeAppCodeDTO.setLeaveSchemeDateId(appCodeMaster.getAppCodeID());
			}
		}

		leaveSchemeForm.setLeaveSchemeAppCodeDTO(leaveSchemeAppCodeDTO);

		return leaveSchemeForm;
	}

	@Override
	public LeaveSchemeResponse saveLeaveSchemeDetailTypes(LeaveSchemeForm leaveSchemeForm) {

		LeaveSchemeResponse leaveSchemeResponse = new LeaveSchemeResponse();
		Boolean isEntitlementChanged = false;
		LeaveSchemeTypeAvailingLeave availingLeave = null;
		LeaveSchemeTypeGranting schemeTypeGranting = null;
		LeaveSchemeTypeProration schemeTypeProration = null;
		LeaveSchemeTypeYearEnd schemeTypeYearEnd = null;

		if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() != null
				&& leaveSchemeForm.getLeaveSchemeTypeAvailingId() != 0) {
			availingLeave = leaveSchemeTypeAvailingLeaveDAO.findById(leaveSchemeForm.getLeaveSchemeTypeAvailingId());
		} else {
			availingLeave = new LeaveSchemeTypeAvailingLeave();
		}

		if (leaveSchemeForm.getLeaveSchemeTypeGrantingId() != null
				&& leaveSchemeForm.getLeaveSchemeTypeGrantingId() != 0) {
			schemeTypeGranting = leaveSchemeTypeGrantingDAO.findById(leaveSchemeForm.getLeaveSchemeTypeGrantingId());
		} else {
			schemeTypeGranting = new LeaveSchemeTypeGranting();
		}

		if (leaveSchemeForm.getLeaveSchemeTypeProrationId() != null
				&& leaveSchemeForm.getLeaveSchemeTypeProrationId() != 0) {
			schemeTypeProration = leaveSchemeTypeProrationDAO.findById(leaveSchemeForm.getLeaveSchemeTypeProrationId());
		} else {
			schemeTypeProration = new LeaveSchemeTypeProration();
		}

		if (leaveSchemeForm.getLeaveSchemeTypeYearEndId() != null
				&& leaveSchemeForm.getLeaveSchemeTypeYearEndId() != 0) {
			schemeTypeYearEnd = leaveSchemeTypeYearEndDAO.findById(leaveSchemeForm.getLeaveSchemeTypeYearEndId());
		} else {
			schemeTypeYearEnd = new LeaveSchemeTypeYearEnd();
		}

		LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findById(leaveSchemeForm.getLeaveSchemeTypeId());
		leaveSchemeResponse.setLeaveSchemetypeId(leaveSchemeForm.getLeaveSchemeTypeId());

		schemeTypeGranting.setLeaveSchemeType(leaveSchemeType);
		AppCodeMaster appCodeMaster1 = appCodeMasterDAO.findById(leaveSchemeForm.getLeaveCalendar());
		schemeTypeGranting.setLeaveCalendar(appCodeMaster1);

		AppCodeMaster appCodeMaster2 = appCodeMasterDAO.findById(leaveSchemeForm.getDistributionMethod());
		schemeTypeGranting.setDistributionMethod(appCodeMaster2);
		if (StringUtils.isNotBlank(leaveSchemeForm.getLeaveGrantDay())) {
			schemeTypeGranting.setGrantDay(Integer.valueOf(leaveSchemeForm.getLeaveGrantDay()));
		}
		schemeTypeGranting.setExpireEntitlement(leaveSchemeForm.getExpireEntitlement());

		LeaveSchemeTypeGranting leaveSchemeTypeGrantingVO = null;
		if (leaveSchemeForm.getLeaveSchemeTypeGrantingId() != 0) {
			schemeTypeGranting.setLeaveSchemeTypeGrantingId(leaveSchemeForm.getLeaveSchemeTypeGrantingId());
			leaveSchemeTypeGrantingDAO.update(schemeTypeGranting);
		}
		if (leaveSchemeForm.getLeaveSchemeTypeGrantingId() == 0) {
			leaveSchemeTypeGrantingVO = leaveSchemeTypeGrantingDAO.saveObj(schemeTypeGranting);
		}

		schemeTypeProration.setLeaveSchemeType(leaveSchemeType);
		AppCodeMaster appCodeMaster4 = null;
		if (leaveSchemeForm.getProbationMethod() != null) {
			appCodeMaster4 = appCodeMasterDAO.findById(leaveSchemeForm.getProbationMethod());
		}
		schemeTypeProration.setNoProration(leaveSchemeForm.getNoProration());
		schemeTypeProration.setProrationMethod(appCodeMaster4);
		schemeTypeProration.setProrationFirstYearOnly(leaveSchemeForm.getProrationFirstYearOnly());
		AppCodeMaster appCodeProBasedOn = null;
		if (leaveSchemeForm.getBasedOn() != null) {
			appCodeProBasedOn = appCodeMasterDAO.findById(leaveSchemeForm.getBasedOn());
		}
		if (StringUtils.isNotBlank(leaveSchemeForm.getLeaveCutOffDay())) {
			schemeTypeProration.setCutOffDay(Integer.valueOf(leaveSchemeForm.getLeaveCutOffDay()));
		}

		schemeTypeProration.setProrationBasedOn(appCodeProBasedOn);
		AppCodeMaster appCodeProRoundingMeth = appCodeMasterDAO.findById(leaveSchemeForm.getRounding());
		schemeTypeProration.setRoundingMethod(appCodeProRoundingMeth);
		LeaveSchemeTypeProration leaveSchemeTypeProrationVO = null;
		if (leaveSchemeForm.getLeaveSchemeTypeProrationId() != 0) {
			schemeTypeProration.setLeaveSchemeTypeProrationId(leaveSchemeForm.getLeaveSchemeTypeProrationId());
			leaveSchemeTypeProrationDAO.update(schemeTypeProration);
		}
		if (leaveSchemeForm.getLeaveSchemeTypeProrationId() == 0) {
			leaveSchemeTypeProrationVO = leaveSchemeTypeProrationDAO.saveObj(schemeTypeProration);
		}

		schemeTypeYearEnd.setLeaveSchemeType(leaveSchemeType);
		schemeTypeYearEnd.setAllowCarryForward(leaveSchemeForm.getAllowCarryForward());
		schemeTypeYearEnd.setAnnualCarryForwardPercentage(leaveSchemeForm.getAnnualCarryFwdLimit());
		schemeTypeYearEnd.setMaxCarryForwardLimit(leaveSchemeForm.getMaximumNumberCarryForwarded());
		schemeTypeYearEnd.setLeaveExpiryDays(leaveSchemeForm.getExpiryDate());

		if (leaveSchemeForm.getLeaveSchemeTypeYearEndId() != 0) {
			schemeTypeYearEnd.setLeaveSchemeTypeYearEndID(leaveSchemeForm.getLeaveSchemeTypeYearEndId());
			leaveSchemeTypeYearEndDAO.update(schemeTypeYearEnd);
		}
		if (leaveSchemeForm.getLeaveSchemeTypeYearEndId() == 0) {
			leaveSchemeTypeYearEndDAO.saveObj(schemeTypeYearEnd);
		}

		availingLeave.setLeaveSchemeType(leaveSchemeType);
		availingLeave.setAdvanceLeaveApplyBeforeDays(leaveSchemeForm.getAdvLeaveApplyBeforeDays());
		availingLeave.setAdvanceLeavePostBeforeDays(leaveSchemeForm.getAdvLeavePostBeforeDays());
		availingLeave.setAllowAdvancePosting(leaveSchemeForm.getAllowAdvanceLeavePosting());
		availingLeave.setAllowBackdatePosting(leaveSchemeForm.getAllowBackDateLeavePosting());
		availingLeave.setAllowExcessLeave(leaveSchemeForm.getAllowExcessLeave());
		if (leaveSchemeForm.getAllowExcessLeave() != null && leaveSchemeForm.getAllowExcessLeave()) {
			if (StringUtils.isNotBlank(leaveSchemeForm.getExcessLeaveAllowAs()) && leaveSchemeForm
					.getExcessLeaveAllowAs().equalsIgnoreCase(PayAsiaConstants.PAYASIA_LEAVE_FULL_ENTITLEMENT)) {
				availingLeave.setExcessLeaveAllowFullEntitlement(true);
				availingLeave.setExcessLeaveMaxDays(null);
				availingLeave.setExcessLeaveMaxFrequency(null);
			} else {
				availingLeave.setExcessLeaveAllowFullEntitlement(false);
				availingLeave.setExcessLeaveMaxDays(leaveSchemeForm.getMaximumDays());
				availingLeave.setExcessLeaveMaxFrequency(leaveSchemeForm.getMaximumFrequency());
			}
		} else {
			availingLeave.setExcessLeaveAllowFullEntitlement(false);
			availingLeave.setExcessLeaveMaxDays(null);
			availingLeave.setExcessLeaveMaxFrequency(null);
		}

		if (StringUtils.isNotBlank(leaveSchemeForm.getLeaveVisibilityStartDate())) {
			availingLeave.setLeaveVisibilityStartDate(
					DateUtils.stringToTimestamp(leaveSchemeForm.getLeaveVisibilityStartDate()));
		} else {
			availingLeave.setLeaveVisibilityStartDate(null);
		}
		if (StringUtils.isNotBlank(leaveSchemeForm.getLeaveVisibilityEndDate())) {
			availingLeave.setLeaveVisibilityEndDate(
					DateUtils.stringToTimestamp(leaveSchemeForm.getLeaveVisibilityEndDate()));
		} else {
			availingLeave.setLeaveVisibilityEndDate(null);
		}

		availingLeave.setAttachmentMandatory(leaveSchemeForm.getAttachementMandatory());
		availingLeave.setAttachmentExemptionDays(leaveSchemeForm.getNumOfDaysAttachmentBeExempted());
		availingLeave.setApprovalNotRequired(leaveSchemeForm.getApprovalNotRequired());
		availingLeave.setBackdatePostingAfterDays(leaveSchemeForm.getBackDatePostingAfterDays());
		LeaveSchemeType leaveSchemeType2 = null;
		if (leaveSchemeForm.getConsiderLeaveBalFrom() != null) {
			leaveSchemeType2 = leaveSchemeTypeDAO.findById(leaveSchemeForm.getConsiderLeaveBalFrom());
		}
		availingLeave.setConsiderLeaveBalanceFrom(leaveSchemeType2);
		availingLeave.setDefaultCCEmail(leaveSchemeForm.getDefaultCCEmail());

		availingLeave.setHolidaysInclusive(leaveSchemeForm.getHolidays());
		availingLeave.setLeaveExtension(leaveSchemeForm.getLeaveExtension());

		availingLeave.setMaxBlockLeave(leaveSchemeForm.getMaximumBlockLeave());
		availingLeave.setMinBlockLeave(leaveSchemeForm.getMinimumBlockLeave());
		availingLeave.setMaxDaysAllowPerYear(leaveSchemeForm.getMaxDaysAllowPerYear());
		availingLeave.setMinDaysGapBetweenLeave(leaveSchemeForm.getMinDaysGapBetweenLeave());
		availingLeave.setNextYearPostingBeforeDays(leaveSchemeForm.getNextYearPostingBeforeDays());
		availingLeave.setOffInclusive(leaveSchemeForm.getOffInclusive());
		availingLeave.setSendMailToDefaultCC(leaveSchemeForm.getSendMailToDefaultCC());
		availingLeave.setRemarks(leaveSchemeForm.getRemarks());

		availingLeave.setApplyAfterDays(leaveSchemeForm.getLeaveCanBeAppOnlyAfterDays());
		if (leaveSchemeForm.getApplyAfterFromId() != 0) {
			AppCodeMaster appCodeApplyAfterFrom = appCodeMasterDAO.findById(leaveSchemeForm.getApplyAfterFromId());
			availingLeave.setApplyAfterFrom(appCodeApplyAfterFrom);
		}

		availingLeave.setAutoApprove(leaveSchemeForm.isAutoApproveAfter());
		if (leaveSchemeForm.isAutoApproveAfter() && leaveSchemeForm.getAutoApproveAfterDays() > 0) {
			availingLeave.setAutoApproveAfterDays(leaveSchemeForm.getAutoApproveAfterDays());
		} else {
			availingLeave.setAutoApproveAfterDays(0);
		}

		LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeaveVO = null;
		if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() != 0) {
			availingLeave.setLeaveSchemeTypeAvailingLeaveId(leaveSchemeForm.getLeaveSchemeTypeAvailingId());
			leaveSchemeTypeAvailingLeaveDAO.update(availingLeave);
		}
		if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() == 0) {
			leaveSchemeTypeAvailingLeaveVO = leaveSchemeTypeAvailingLeaveDAO.saveObj(availingLeave);
		}
		AppCodeMaster appCodeAllowOnlyIfBalIsZero = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.PAYASIA_LEAVE_TYPE_AVAILING_GRANT_CONDITION,
				PayAsiaConstants.PAYASIA_LEAVE_ALLOW_ONLY_IF_BALANCE_IS_ZERO);
		LeaveSchemeType leaveSchemeType1 = null;
		if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() != 0) {
			leaveSchemeTypeGrantConditionDAO.deleteByAvailingId(leaveSchemeForm.getLeaveSchemeTypeAvailingId());
		}
		for (int count = 0; count < leaveSchemeForm.getAllowOnlyIfBalIsZero().length; count++) {
			leaveSchemeType1 = leaveSchemeTypeDAO.findById(leaveSchemeForm.getAllowOnlyIfBalIsZero()[count]);
			LeaveSchemeTypeGrantCondition leaveSchemeTypeGrantCondition = new LeaveSchemeTypeGrantCondition();
			leaveSchemeTypeGrantCondition.setGrantConditionValue(leaveSchemeType1);
			if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() != 0) {
				LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeaveDAO
						.findById(leaveSchemeForm.getLeaveSchemeTypeAvailingId());
				leaveSchemeTypeGrantCondition.setLeaveSchemeTypeAvailingLeave(leaveSchemeTypeAvailingLeave);
			} else {
				leaveSchemeTypeGrantCondition.setLeaveSchemeTypeAvailingLeave(leaveSchemeTypeAvailingLeaveVO);
			}

			leaveSchemeTypeGrantCondition.setGrantCondition(appCodeAllowOnlyIfBalIsZero);
			leaveSchemeTypeGrantConditionDAO.save(leaveSchemeTypeGrantCondition);
		}
		AppCodeMaster appCodeLeaveCanNotBeCombined = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.PAYASIA_LEAVE_TYPE_AVAILING_GRANT_CONDITION,
				PayAsiaConstants.PAYASIA_LEAVE_CAN_NOT_BE_COMBINED_WITH);
		LeaveSchemeType leaveSchemeType3 = null;
		for (int count = 0; count < leaveSchemeForm.getLeaveCanNotCombined().length; count++) {
			leaveSchemeType3 = leaveSchemeTypeDAO.findById(leaveSchemeForm.getLeaveCanNotCombined()[count]);
			LeaveSchemeTypeGrantCondition leaveSchemeTypeGrantCondition = new LeaveSchemeTypeGrantCondition();
			leaveSchemeTypeGrantCondition.setGrantConditionValue(leaveSchemeType3);
			if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() != 0) {
				LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeaveDAO
						.findById(leaveSchemeForm.getLeaveSchemeTypeAvailingId());
				leaveSchemeTypeGrantCondition.setLeaveSchemeTypeAvailingLeave(leaveSchemeTypeAvailingLeave);
			} else {
				leaveSchemeTypeGrantCondition.setLeaveSchemeTypeAvailingLeave(leaveSchemeTypeAvailingLeaveVO);
			}

			leaveSchemeTypeGrantCondition.setGrantCondition(appCodeLeaveCanNotBeCombined);
			leaveSchemeTypeGrantConditionDAO.save(leaveSchemeTypeGrantCondition);
		}

		if (leaveSchemeForm.getLeaveSchemeTypeGrantingId() != 0) {
			List<LeaveSchemeTypeEntitlement> leaveSchemeTypeEntitlementList = leaveSchemeTypeEntitlementDAO
					.findByLeaveSchemeTypeGrantingId(leaveSchemeForm.getLeaveSchemeTypeGrantingId());

			isEntitlementChanged = isEntitlementChanged(leaveSchemeTypeEntitlementList,
					leaveSchemeForm.getLeaveEntitlementDTOList());
			if (!leaveSchemeTypeEntitlementList.isEmpty()) {
				leaveSchemeTypeEntitlementDAO.deleteByCondition(leaveSchemeForm.getLeaveSchemeTypeGrantingId());
			}
		} else if (leaveSchemeForm.getLeaveEntitlementDTOList() != null
				&& leaveSchemeForm.getLeaveEntitlementDTOList().size() > 0) {
			isEntitlementChanged = true;
		}

		for (LeaveEntitlementDTO leaveEntitlementDTO : leaveSchemeForm.getLeaveEntitlementDTOList()) {

			if (leaveEntitlementDTO != null) {
				if (leaveEntitlementDTO.getValue() != null) {
					LeaveSchemeTypeEntitlement leaveSchemeTypeEntitlement = new LeaveSchemeTypeEntitlement();

					leaveSchemeTypeEntitlement.setValue(leaveEntitlementDTO.getValue());
					leaveSchemeTypeEntitlement.setYear(leaveEntitlementDTO.getYear());
					if (leaveSchemeForm.getLeaveSchemeTypeGrantingId() != 0) {
						LeaveSchemeTypeGranting schemeTypeGrantingData = leaveSchemeTypeGrantingDAO
								.findById(leaveSchemeForm.getLeaveSchemeTypeGrantingId());
						leaveSchemeTypeEntitlement.setLeaveSchemeTypeGranting(schemeTypeGrantingData);
						leaveSchemeTypeEntitlementDAO.save(leaveSchemeTypeEntitlement);
					}
					if (leaveSchemeForm.getLeaveSchemeTypeGrantingId() == 0) {
						leaveSchemeTypeEntitlement.setLeaveSchemeTypeGranting(leaveSchemeTypeGrantingVO);
						leaveSchemeTypeEntitlementDAO.save(leaveSchemeTypeEntitlement);
					}
				}
			}

		}

		if (leaveSchemeForm.getLeaveSchemeTypeProrationId() != 0) {
			List<LeaveSchemeTypeCustomRounding> leaveSchemeTypeCustomRoundingList = leaveSchemeTypeCustomRoundingDAO
					.findByLeaveSchemeTypeProrationId(leaveSchemeForm.getLeaveSchemeTypeProrationId());
			if (!leaveSchemeTypeCustomRoundingList.isEmpty()) {
				leaveSchemeTypeCustomRoundingDAO.deleteByCondition(leaveSchemeForm.getLeaveSchemeTypeProrationId());
			}
			List<LeaveSchemeTypeCustomProration> customProrationList = leaveSchemeTypeCustomProrationDAO
					.findByLeaveSchemeTypeProrationId(leaveSchemeForm.getLeaveSchemeTypeProrationId());
			if (!customProrationList.isEmpty()) {
				leaveSchemeTypeCustomProrationDAO.deleteByCondition(leaveSchemeForm.getLeaveSchemeTypeProrationId());
			}

		}

		for (CustomRoundingDTO customRoundingDTO : leaveSchemeForm.getCustomRoundingDTOList()) {

			if (customRoundingDTO != null) {
				if (customRoundingDTO.getValue() != null) {
					LeaveSchemeTypeCustomRounding leaveSchemeTypeCustomRounding = new LeaveSchemeTypeCustomRounding();
					leaveSchemeTypeCustomRounding.setFromRange(customRoundingDTO.getFromRange());
					leaveSchemeTypeCustomRounding.setToRange(customRoundingDTO.getToRange());
					leaveSchemeTypeCustomRounding.setValue(customRoundingDTO.getValue());
					if (leaveSchemeForm.getLeaveSchemeTypeProrationId() != 0) {
						LeaveSchemeTypeProration leaveSchemeTypeProration = leaveSchemeTypeProrationDAO
								.findById(leaveSchemeForm.getLeaveSchemeTypeProrationId());
						leaveSchemeTypeCustomRounding.setLeaveSchemeTypeProration(leaveSchemeTypeProration);
						leaveSchemeTypeCustomRoundingDAO.save(leaveSchemeTypeCustomRounding);
					}
					if (leaveSchemeForm.getLeaveSchemeTypeProrationId() == 0) {
						leaveSchemeTypeCustomRounding.setLeaveSchemeTypeProration(leaveSchemeTypeProrationVO);
						leaveSchemeTypeCustomRoundingDAO.save(leaveSchemeTypeCustomRounding);
					}
				}
			}

		}

		for (FirstMonthCustomDTO customRoundingDTO : leaveSchemeForm.getFirstMonthCustomList()) {

			if (customRoundingDTO != null) {
				if (customRoundingDTO.getValue() != null) {

					LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProration = new LeaveSchemeTypeCustomProration();
					leaveSchemeTypeCustomProration.setFromRange(customRoundingDTO.getFromRange());
					leaveSchemeTypeCustomProration.setToRange(customRoundingDTO.getToRange());
					leaveSchemeTypeCustomProration.setValue(customRoundingDTO.getValue());
					if (leaveSchemeForm.getLeaveSchemeTypeProrationId() != 0) {
						LeaveSchemeTypeProration leaveSchemeTypeProration = leaveSchemeTypeProrationDAO
								.findById(leaveSchemeForm.getLeaveSchemeTypeProrationId());
						leaveSchemeTypeCustomProration.setLeaveSchemeTypeProration(leaveSchemeTypeProration);
						leaveSchemeTypeCustomProrationDAO.save(leaveSchemeTypeCustomProration);
					}
					if (leaveSchemeForm.getLeaveSchemeTypeProrationId() == 0) {
						leaveSchemeTypeCustomProration.setLeaveSchemeTypeProration(leaveSchemeTypeProrationVO);
						leaveSchemeTypeCustomProrationDAO.save(leaveSchemeTypeCustomProration);
					}
				}
			}

		}

		List<String> custRoundDBIdList = new ArrayList<String>();
		List<String> custRoundInsertIdList = new ArrayList<String>();
		if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() != 0) {
			List<LeaveSchemeTypeCustomField> leaveSchemeTypeCustomFieldList = leaveSchemeTypeCustomFieldDAO
					.findByLeaveSchemeTypeAvailingLeaveId(leaveSchemeForm.getLeaveSchemeTypeAvailingId());
			if (!leaveSchemeTypeCustomFieldList.isEmpty()) {
				for (LeaveSchemeTypeCustomField leaveSchemeTypeCustomField : leaveSchemeTypeCustomFieldList) {
					custRoundDBIdList.add(String.valueOf(leaveSchemeTypeCustomField.getCustomFieldId()));
				}
			}
		}

		for (CustomFieldsDTO CustomFieldsDTO : leaveSchemeForm.getCustomFieldsDTOList()) {
			if (CustomFieldsDTO.getFieldName() != null) {
				if (CustomFieldsDTO.getCustomFieldId() != 0) {
					custRoundInsertIdList.add(String.valueOf(CustomFieldsDTO.getCustomFieldId()));
				}
			}

		}

		custRoundDBIdList.removeAll(custRoundInsertIdList);
		if (!custRoundDBIdList.isEmpty()) {
			for (String custRoundDeleteId : custRoundDBIdList) {
				LeaveSchemeTypeCustomField customField = leaveSchemeTypeCustomFieldDAO
						.findById(Long.parseLong(custRoundDeleteId));
				leaveSchemeTypeCustomFieldDAO.delete(customField);
			}
		}

		for (CustomFieldsDTO CustomFieldsDTO : leaveSchemeForm.getCustomFieldsDTOList()) {

			if (CustomFieldsDTO != null) {
				if (CustomFieldsDTO.getFieldName() != null) {
					if (CustomFieldsDTO.getCustomFieldId() == 0) {
						LeaveSchemeTypeCustomField leaveSchemeTypeCustomField = new LeaveSchemeTypeCustomField();
						leaveSchemeTypeCustomField.setFieldName(CustomFieldsDTO.getFieldName());
						leaveSchemeTypeCustomField.setMandatory(CustomFieldsDTO.isMandatory());
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeaveData = leaveSchemeTypeAvailingLeaveDAO
								.findById(leaveSchemeForm.getLeaveSchemeTypeAvailingId());
						if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() != 0) {
							leaveSchemeTypeCustomField
									.setLeaveSchemeTypeAvailingLeave(leaveSchemeTypeAvailingLeaveData);
						} else {
							leaveSchemeTypeCustomField.setLeaveSchemeTypeAvailingLeave(leaveSchemeTypeAvailingLeaveVO);
						}

						leaveSchemeTypeCustomFieldDAO.save(leaveSchemeTypeCustomField);
					}
					if (CustomFieldsDTO.getCustomFieldId() > 0) {
						LeaveSchemeTypeCustomField leaveSchemeTypeCustomField = new LeaveSchemeTypeCustomField();
						leaveSchemeTypeCustomField.setCustomFieldId(CustomFieldsDTO.getCustomFieldId());
						leaveSchemeTypeCustomField.setFieldName(CustomFieldsDTO.getFieldName());
						leaveSchemeTypeCustomField.setMandatory(CustomFieldsDTO.isMandatory());
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeaveData = leaveSchemeTypeAvailingLeaveDAO
								.findById(leaveSchemeForm.getLeaveSchemeTypeAvailingId());
						if (leaveSchemeForm.getLeaveSchemeTypeAvailingId() != 0) {
							leaveSchemeTypeCustomField
									.setLeaveSchemeTypeAvailingLeave(leaveSchemeTypeAvailingLeaveData);
						} else {
							leaveSchemeTypeCustomField.setLeaveSchemeTypeAvailingLeave(leaveSchemeTypeAvailingLeaveVO);
						}
						leaveSchemeTypeCustomFieldDAO.update(leaveSchemeTypeCustomField);
					}
				}

			}

		}
		leaveSchemeResponse.setIsEntitlementChanged(isEntitlementChanged);

		// leave Type Activation/DeActivation
		if (availingLeave != null) {
			if (availingLeave.getLeaveVisibilityStartDate() != null
					&& availingLeave.getLeaveVisibilityEndDate() != null) {
				Date startDate = new Date(availingLeave.getLeaveVisibilityStartDate().getTime());
				Date endDate = new Date(availingLeave.getLeaveVisibilityEndDate().getTime());
				Date currentDate = com.payasia.common.util.DateUtils.stringToDate(
						com.payasia.common.util.DateUtils.timeStampToString(
								com.payasia.common.util.DateUtils.getCurrentTimestamp(),
								leaveSchemeType.getLeaveScheme().getCompany().getDateFormat()),
						leaveSchemeType.getLeaveScheme().getCompany().getDateFormat());

				if ((currentDate.after(startDate) && currentDate.before(endDate))
						|| org.apache.commons.lang.time.DateUtils.isSameDay(currentDate, startDate)
						|| org.apache.commons.lang.time.DateUtils.isSameDay(currentDate, endDate)) {
					leaveSchemeType.setVisibility(true);
					leaveSchemeTypeDAO.update(leaveSchemeType);

				} else {
					leaveSchemeType.setVisibility(false);
					leaveSchemeTypeDAO.update(leaveSchemeType);
				}
			} else {
				leaveSchemeType.setVisibility(true);
				leaveSchemeTypeDAO.update(leaveSchemeType);
			}
		}

		return leaveSchemeResponse;
	}

	private Boolean isEntitlementChanged(List<LeaveSchemeTypeEntitlement> leaveSchemeTypeEntitlementList,
			List<LeaveEntitlementDTO> leaveEntitlementDTOList) {
		DecimalFormat decimalFmt = new DecimalFormat("#,####0.00");
		Set<LeaveSchemeTypeEntitlement> oldEntitlementList = new HashSet<>(leaveSchemeTypeEntitlementList);
		Map<Integer, BigDecimal> oldMap = new HashMap<>();
		for (LeaveSchemeTypeEntitlement leaveSchemeTypeEntitlement : oldEntitlementList) {
			oldMap.put(leaveSchemeTypeEntitlement.getYear(), leaveSchemeTypeEntitlement.getValue());
		}
		Set<LeaveEntitlementDTO> newEntitlementList = new HashSet<>(leaveEntitlementDTOList);
		Map<Integer, BigDecimal> newMap = new HashMap<>();
		for (LeaveEntitlementDTO leaveEntitlementDTO : newEntitlementList) {
			if (leaveEntitlementDTO.getValue() == null) {
				continue;
			}
			newMap.put(leaveEntitlementDTO.getYear(),
					new BigDecimal(decimalFmt.format(leaveEntitlementDTO.getValue())));
		}

		if (leaveSchemeTypeEntitlementList.size() == 0 && !newMap.isEmpty()) {
			return true;
		}

		if (oldMap.entrySet().equals(newMap.entrySet())) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public LeaveSchemeForm getLeaveSchemeTypeForEdit(Long companyId, Long leaveSchemeTypeId) {
		LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();
		/* ID DECRYPT */
		/*leaveSchemeTypeId= FormatPreserveCryptoUtil.decrypt(leaveSchemeTypeId);*/
		
		LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO.findLeaveSchemeTypeByCompId(leaveSchemeTypeId, companyId);
		if(leaveSchemeTypeVO == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);

		leaveSchemeForm.setLeaveExtensionPreference(leavePreference.isLeaveExtensionRequired());

		leaveSchemeForm.setLeaveSchemeTypeId(leaveSchemeTypeVO.getLeaveSchemeTypeId());

		Set<LeaveSchemeTypeGranting> leaveSchemeTypeGranting = leaveSchemeTypeVO.getLeaveSchemeTypeGrantings();
		if (!leaveSchemeTypeGranting.isEmpty()) {
			for (LeaveSchemeTypeGranting schemeTypeGranting : leaveSchemeTypeGranting) {

				AppCodeMaster appCodeMasterLeaveCalendar = schemeTypeGranting.getLeaveCalendar();
				leaveSchemeForm.setLeaveCalendar(appCodeMasterLeaveCalendar.getAppCodeID());

				AppCodeMaster appCodeMasterDistMethod = schemeTypeGranting.getDistributionMethod();
				leaveSchemeForm.setDistributionMethod(appCodeMasterDistMethod.getAppCodeID());
				leaveSchemeForm.setDistMethName(appCodeMasterDistMethod.getCodeDesc());
				leaveSchemeForm.setLeaveSchemeTypeGrantingId(schemeTypeGranting.getLeaveSchemeTypeGrantingId());
				leaveSchemeForm.setLeaveGrantDay(String.valueOf(schemeTypeGranting.getGrantDay()));
				if (schemeTypeGranting.getExpireEntitlement() != null) {
					leaveSchemeForm.setExpireEntitlement(schemeTypeGranting.getExpireEntitlement());
				}

				List<LeaveSchemeTypeEntitlement> leaveSchemeTypeEntitlements = new ArrayList<>();
				Set<LeaveSchemeTypeEntitlement> leaveSchemeTypeEntitlementList = schemeTypeGranting
						.getLeaveSchemeTypeEntitlements();
				for (LeaveSchemeTypeEntitlement entitlement : leaveSchemeTypeEntitlementList) {
					leaveSchemeTypeEntitlements.add(entitlement);
				}
				Collections.sort(leaveSchemeTypeEntitlements, new LeaveSchemeComp());
				if (!leaveSchemeTypeEntitlementList.isEmpty()) {
					List<LeaveEntitlementDTO> leaveEntitlementDTOList = new ArrayList<LeaveEntitlementDTO>();
					for (LeaveSchemeTypeEntitlement leaveSchemeTypeEntitlementVO : leaveSchemeTypeEntitlements) {
						LeaveEntitlementDTO leaveEntitlementDTO = new LeaveEntitlementDTO();
						leaveEntitlementDTO.setLeaveEntitlementId(leaveSchemeTypeEntitlementVO.getEntitlementId());
						leaveEntitlementDTO.setYear(leaveSchemeTypeEntitlementVO.getYear());
						leaveEntitlementDTO.setValue(leaveSchemeTypeEntitlementVO.getValue());
						leaveEntitlementDTOList.add(leaveEntitlementDTO);
					}
					leaveSchemeForm.setLeaveEntitlementList(leaveEntitlementDTOList);
				}
			}
		}

		Set<LeaveSchemeTypeProration> leaveSchemeTypeProration = leaveSchemeTypeVO.getLeaveSchemeTypeProrations();
		if (leaveSchemeTypeProration != null) {
			for (LeaveSchemeTypeProration schemeTypeProration : leaveSchemeTypeProration) {
				leaveSchemeForm.setLeaveSchemeTypeProrationId(schemeTypeProration.getLeaveSchemeTypeProrationId());
				leaveSchemeForm.setNoProration(schemeTypeProration.getNoProration());

				AppCodeMaster appCodeMasterProbMethod = schemeTypeProration.getProrationMethod();
				leaveSchemeForm.setProbationMethod(
						appCodeMasterProbMethod == null ? null : appCodeMasterProbMethod.getAppCodeID());
				AppCodeMaster appCodeMasterBasedOn = schemeTypeProration.getProrationBasedOn();
				leaveSchemeForm.setBasedOn(appCodeMasterBasedOn == null ? null : appCodeMasterBasedOn.getAppCodeID());
				leaveSchemeForm.setLeaveCutOffDay(String.valueOf(schemeTypeProration.getCutOffDay()));
				leaveSchemeForm.setProrationFirstYearOnly(schemeTypeProration.isProrationFirstYearOnly());

				AppCodeMaster appCodeMasterRoundingMeth = schemeTypeProration.getRoundingMethod();
				leaveSchemeForm.setRounding(appCodeMasterRoundingMeth.getAppCodeID());

				List<LeaveSchemeTypeCustomRounding> leaveSchemeTypeCustomRoundings = new ArrayList<>();
				Set<LeaveSchemeTypeCustomRounding> leaveSchemeTypeCustomRoundingList = schemeTypeProration
						.getLeaveSchemeTypeCustomRoundings();
				for (LeaveSchemeTypeCustomRounding custRounding : leaveSchemeTypeCustomRoundingList) {
					leaveSchemeTypeCustomRoundings.add(custRounding);
				}
				Collections.sort(leaveSchemeTypeCustomRoundings, new LeaveSchemeCustRoundComp());

				if (!leaveSchemeTypeCustomRoundingList.isEmpty()) {
					List<CustomRoundingDTO> customRoundingDTOList = new ArrayList<CustomRoundingDTO>();
					for (LeaveSchemeTypeCustomRounding leaveSchemeTypeCustRoundVO : leaveSchemeTypeCustomRoundings) {
						CustomRoundingDTO customRoundingDTO = new CustomRoundingDTO();
						customRoundingDTO.setCustomRoundingId(leaveSchemeTypeCustRoundVO.getCustomRoundingId());
						customRoundingDTO.setFromRange(leaveSchemeTypeCustRoundVO.getFromRange());
						customRoundingDTO.setToRange(leaveSchemeTypeCustRoundVO.getToRange());
						customRoundingDTO.setValue(leaveSchemeTypeCustRoundVO.getValue());
						customRoundingDTOList.add(customRoundingDTO);
					}
					leaveSchemeForm.setCustomRoundingList(customRoundingDTOList);
				}

				Set<LeaveSchemeTypeCustomProration> leaveSchemeTypeCustomProrations = schemeTypeProration
						.getLeaveSchemeTypeCustomProrations();
				List<LeaveSchemeTypeCustomProration> leaveSchemeTypeCustomProrationList = new ArrayList<>(
						leaveSchemeTypeCustomProrations);
				Collections.sort(leaveSchemeTypeCustomProrationList, new LeaveSchemeFirstMonthCustomComp());

				if (!leaveSchemeTypeCustomProrationList.isEmpty()) {
					List<FirstMonthCustomDTO> firstMonthCustomDTOList = new ArrayList<>();
					for (LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProrationVO : leaveSchemeTypeCustomProrationList) {
						FirstMonthCustomDTO firstMonthCustomDTO = new FirstMonthCustomDTO();
						firstMonthCustomDTO
								.setCustomProrationId(leaveSchemeTypeCustomProrationVO.getCustomProrationId());
						firstMonthCustomDTO.setFromRange(leaveSchemeTypeCustomProrationVO.getFromRange());
						firstMonthCustomDTO.setToRange(leaveSchemeTypeCustomProrationVO.getToRange());
						firstMonthCustomDTO.setValue(leaveSchemeTypeCustomProrationVO.getValue());
						firstMonthCustomDTOList.add(firstMonthCustomDTO);
					}
					leaveSchemeForm.setFirstMonthCustomList(firstMonthCustomDTOList);
				}

			}
		}

		Set<LeaveSchemeTypeYearEnd> leaveSchemeTypeYearEnds = leaveSchemeTypeVO.getLeaveSchemeTypeYearEnds();
		if (leaveSchemeTypeYearEnds != null) {
			for (LeaveSchemeTypeYearEnd schemeTypeYearEnd : leaveSchemeTypeYearEnds) {
				leaveSchemeForm.setAllowCarryForward(schemeTypeYearEnd.getAllowCarryForward());
				leaveSchemeForm.setLeaveSchemeTypeYearEndId(schemeTypeYearEnd.getLeaveSchemeTypeYearEndID());
				leaveSchemeForm.setMaximumNumberCarryForwarded(schemeTypeYearEnd.getMaxCarryForwardLimit());
				leaveSchemeForm.setExpiryDate(schemeTypeYearEnd.getLeaveExpiryDays());
				leaveSchemeForm.setAnnualCarryFwdLimit(schemeTypeYearEnd.getAnnualCarryForwardPercentage());
			}
		}

		Set<LeaveSchemeTypeAvailingLeave> schemeTypeAvailingLeaves = leaveSchemeTypeVO
				.getLeaveSchemeTypeAvailingLeaves();
		if (schemeTypeAvailingLeaves != null) {
			for (LeaveSchemeTypeAvailingLeave schemeTypeAvailingLeave : schemeTypeAvailingLeaves) {
				leaveSchemeForm
						.setLeaveSchemeTypeAvailingId(schemeTypeAvailingLeave.getLeaveSchemeTypeAvailingLeaveId());
				leaveSchemeForm.setHolidays(schemeTypeAvailingLeave.isHolidaysInclusive());
				leaveSchemeForm.setLeaveExtension(schemeTypeAvailingLeave.isLeaveExtension());
				leaveSchemeForm.setRemarks(schemeTypeAvailingLeave.getRemarks());
				leaveSchemeForm.setOffInclusive(schemeTypeAvailingLeave.isOffInclusive());
				leaveSchemeForm.setMinimumBlockLeave(schemeTypeAvailingLeave.getMinBlockLeave());
				leaveSchemeForm.setMaximumBlockLeave(schemeTypeAvailingLeave.getMaxBlockLeave());
				leaveSchemeForm.setMaxDaysAllowPerYear(schemeTypeAvailingLeave.getMaxDaysAllowPerYear());
				leaveSchemeForm.setMinDaysGapBetweenLeave(schemeTypeAvailingLeave.getMinDaysGapBetweenLeave());
				leaveSchemeForm.setAllowExcessLeave(schemeTypeAvailingLeave.isAllowExcessLeave());
				leaveSchemeForm.setMaximumDays(schemeTypeAvailingLeave.getExcessLeaveMaxDays());
				leaveSchemeForm.setMaximumFrequency(schemeTypeAvailingLeave.getExcessLeaveMaxFrequency());
				leaveSchemeForm.setAllowAdvanceLeavePosting(schemeTypeAvailingLeave.getAllowAdvancePosting());
				leaveSchemeForm.setAdvLeaveApplyBeforeDays(schemeTypeAvailingLeave.getAdvanceLeaveApplyBeforeDays());
				leaveSchemeForm.setAdvLeavePostBeforeDays(schemeTypeAvailingLeave.getAdvanceLeavePostBeforeDays());
				leaveSchemeForm.setAllowBackDateLeavePosting(schemeTypeAvailingLeave.getAllowBackdatePosting());
				leaveSchemeForm.setBackDatePostingAfterDays(schemeTypeAvailingLeave.getBackdatePostingAfterDays());
				leaveSchemeForm.setNextYearPostingBeforeDays(schemeTypeAvailingLeave.getNextYearPostingBeforeDays());
				leaveSchemeForm.setConsiderLeaveBalFrom(schemeTypeAvailingLeave.getConsiderLeaveBalanceFrom() == null
						? null : schemeTypeAvailingLeave.getConsiderLeaveBalanceFrom().getLeaveSchemeTypeId());
				leaveSchemeForm.setDefaultCCEmail(schemeTypeAvailingLeave.getDefaultCCEmail());
				leaveSchemeForm.setSendMailToDefaultCC(schemeTypeAvailingLeave.getSendMailToDefaultCC());
				leaveSchemeForm.setAttachementMandatory(schemeTypeAvailingLeave.isAttachmentMandatory());
				leaveSchemeForm.setNumOfDaysAttachmentBeExempted(schemeTypeAvailingLeave.getAttachmentExemptionDays());
				leaveSchemeForm.setApprovalNotRequired(schemeTypeAvailingLeave.getApprovalNotRequired());
				if (schemeTypeAvailingLeave.getApplyAfterFrom() != null) {
					leaveSchemeForm.setApplyAfterFromId(schemeTypeAvailingLeave.getApplyAfterFrom().getAppCodeID());
				}

				leaveSchemeForm.setAutoApproveAfter(schemeTypeAvailingLeave.isAutoApprove());
				if (schemeTypeAvailingLeave.isAutoApprove() && schemeTypeAvailingLeave.getAutoApproveAfterDays() > 0) {
					leaveSchemeForm.setAutoApproveAfterDays(schemeTypeAvailingLeave.getAutoApproveAfterDays());
				} else {
					leaveSchemeForm.setAutoApproveAfterDays(0);
				}

				leaveSchemeForm.setLeaveCanBeAppOnlyAfterDays(schemeTypeAvailingLeave.getApplyAfterDays());
				if (schemeTypeAvailingLeave.isExcessLeaveAllowFullEntitlement()) {
					leaveSchemeForm.setExcessLeaveAllowFullEntitlement(true);
					leaveSchemeForm.setExcessLeaveAllowMaximumDays(false);
				} else {
					leaveSchemeForm.setExcessLeaveAllowFullEntitlement(false);
					leaveSchemeForm.setExcessLeaveAllowMaximumDays(true);
				}
				if (schemeTypeAvailingLeave.getLeaveVisibilityStartDate() != null) {
					leaveSchemeForm.setLeaveVisibilityStartDate(
							DateUtils.timeStampToString(schemeTypeAvailingLeave.getLeaveVisibilityStartDate()));
				}
				if (schemeTypeAvailingLeave.getLeaveVisibilityEndDate() != null) {
					leaveSchemeForm.setLeaveVisibilityEndDate(
							DateUtils.timeStampToString(schemeTypeAvailingLeave.getLeaveVisibilityEndDate()));
				}

				Set<LeaveSchemeTypeGrantCondition> leaveSchemeTypeGrantCondSet = schemeTypeAvailingLeave
						.getLeaveSchemeTypeGrantConditions();
				int allowLeaveTypeArrCount = 0;
				int leaveCanNotBeCombineCount = 0;
				if (!leaveSchemeTypeGrantCondSet.isEmpty()) {
					Long[] allowLeaveTypeArr = new Long[leaveSchemeTypeGrantCondSet.size()];
					Long[] leaveCanNotBeCombineArr = new Long[leaveSchemeTypeGrantCondSet.size()];

					for (LeaveSchemeTypeGrantCondition leaveSchemeTypeGrantConditionVO : leaveSchemeTypeGrantCondSet) {
						if (leaveSchemeTypeGrantConditionVO.getGrantCondition().getCodeDesc()
								.equalsIgnoreCase(PayAsiaConstants.PAYASIA_LEAVE_ALLOW_ONLY_IF_BALANCE_IS_ZERO)) {
							allowLeaveTypeArr[allowLeaveTypeArrCount] = leaveSchemeTypeGrantConditionVO
									.getGrantConditionValue().getLeaveSchemeTypeId();
							allowLeaveTypeArrCount++;
						}
						leaveSchemeForm.setAllowOnlyIfBalIsZero(allowLeaveTypeArr);
						if (leaveSchemeTypeGrantConditionVO.getGrantCondition().getCodeDesc()
								.equalsIgnoreCase(PayAsiaConstants.PAYASIA_LEAVE_CAN_NOT_BE_COMBINED_WITH)) {
							leaveCanNotBeCombineArr[leaveCanNotBeCombineCount] = leaveSchemeTypeGrantConditionVO
									.getGrantConditionValue().getLeaveSchemeTypeId();
							leaveCanNotBeCombineCount++;
						}

					}
					leaveSchemeForm.setLeaveCanNotCombined(leaveCanNotBeCombineArr);
				}

				List<LeaveSchemeTypeCustomField> leaveSchemeTypeCustomFieldList = new ArrayList<LeaveSchemeTypeCustomField>(
						schemeTypeAvailingLeave.getLeaveSchemeTypeCustomFields());
				Collections.sort(leaveSchemeTypeCustomFieldList, new LeaveSchemeTypeCusFieldComp());
				if (!leaveSchemeTypeCustomFieldList.isEmpty()) {
					List<CustomFieldsDTO> customFieldsDTOList = new ArrayList<CustomFieldsDTO>();
					for (LeaveSchemeTypeCustomField leaveSchemeTypeCustFieldVO : leaveSchemeTypeCustomFieldList) {
						CustomFieldsDTO customFieldsDTO = new CustomFieldsDTO();
						customFieldsDTO.setCustomFieldId(leaveSchemeTypeCustFieldVO.getCustomFieldId());
						customFieldsDTO.setFieldName(leaveSchemeTypeCustFieldVO.getFieldName());
						customFieldsDTO.setMandatory(leaveSchemeTypeCustFieldVO.getMandatory());
						customFieldsDTOList.add(customFieldsDTO);
					}
					leaveSchemeForm.setCustomFieldsList(customFieldsDTOList);
				}
			}
		}

		return leaveSchemeForm;

	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class LeaveSchemeTypeCusFieldComp implements Comparator<LeaveSchemeTypeCustomField> {
		public int compare(LeaveSchemeTypeCustomField templateField, LeaveSchemeTypeCustomField compWithTemplateField) {
			if (templateField.getCustomFieldId() > compWithTemplateField.getCustomFieldId()) {
				return 1;
			} else if (templateField.getCustomFieldId() < compWithTemplateField.getCustomFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class LeaveSchemeComp implements Comparator<LeaveSchemeTypeEntitlement> {
		public int compare(LeaveSchemeTypeEntitlement templateField, LeaveSchemeTypeEntitlement compWithTemplateField) {
			if (templateField.getYear() > compWithTemplateField.getYear()) {
				return 1;
			} else if (templateField.getYear() < compWithTemplateField.getYear()) {
				return -1;
			}
			return 0;

		}

	}

	private class LeaveSchemeCustRoundComp implements Comparator<LeaveSchemeTypeCustomRounding> {
		public int compare(LeaveSchemeTypeCustomRounding customRoundingField,
				LeaveSchemeTypeCustomRounding compCustomRoundingFieldField) {
			return customRoundingField.getFromRange().compareTo(compCustomRoundingFieldField.getFromRange());
		}

	}

	private class LeaveSchemeFirstMonthCustomComp implements Comparator<LeaveSchemeTypeCustomProration> {
		public int compare(LeaveSchemeTypeCustomProration customProration,
				LeaveSchemeTypeCustomProration compcustomProration) {
			if (customProration.getFromRange() > compcustomProration.getFromRange()) {
				return 1;
			} else if (customProration.getFromRange() < compcustomProration.getFromRange()) {
				return -1;
			}
			return 0;
		}

	}

	/**
	 * get Employee FilterList.
	 * 
	 * @param companyId
	 *            the company id
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	@Override
	public List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId, Long employeeId, boolean isAdmin) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		Long entityId = getEntityId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
		HashMap<Long, Tab> tabMap = new HashMap<>();
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO.findByConditionEntity(entityId,
				PayAsiaConstants.STATIC_TYPE);
		if (dataDictionaryList != null) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				try {
					ColumnPropertyDTO columnPropertyDTO = generalDAO.getColumnProperties(dataDictionary.getTableName(),
							dataDictionary.getColumnName());
					employeeFilterListForm.setDataType(columnPropertyDTO.getColumnType());
				} catch (Exception exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
				employeeFilterList.add(employeeFilterListForm);
			}
		}
		List<Long> formIdList = null;
		if (isAdmin) {
			formIdList = generalLogic.getAdminAuthorizedSectionIdList(employeeId, companyId, entityId);
		}

		List<DataDictionary> dataDictionaryCompanyList = dataDictionaryDAO.findByConditionEntityAndCompanyId(companyId,
				entityId, PayAsiaConstants.DYNAMIC_TYPE);
		if (dataDictionaryCompanyList != null) {
			for (DataDictionary dataDictionary : dataDictionaryCompanyList) {
				if (isAdmin && !formIdList.isEmpty() && !formIdList.contains(dataDictionary.getFormID())) {
					continue;
				}
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				Tab tab = tabMap.get(dataDictionary.getFormID());

				if (tab == null) {
					DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId,
							dataDictionary.getFormID());
					tab = getTab(dynamicForm);
					tabMap.put(dataDictionary.getFormID(), tab);
				}

				List<Field> listOfFields = tab.getField();

				for (Field field : listOfFields) {
					if (!field.getType().equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
							&& !StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.TABLE_FIELD_TYPE)
							&& !StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.LABEL_FIELD_TYPE)
							&& !StringUtils.equalsIgnoreCase(field.getType(),
									PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						if (new String(Base64.decodeBase64(field.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())) {
							employeeFilterListForm.setDataType(field.getType());

						}

					} else if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (new String(Base64.decodeBase64(column.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								employeeFilterListForm.setDataType(column.getType());
							}
						}
					} else if (field.getType().equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (new String(Base64.decodeBase64(column.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								employeeFilterListForm.setDataType(column.getType());
							}
						}
					}
				}

				employeeFilterList.add(employeeFilterListForm);

			}
		}
		return employeeFilterList;

	}

	private Tab getTab(DynamicForm dynamicForm) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (SAXException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		final StringReader xmlReader = new StringReader(dynamicForm.getMetaData());
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}
		return tab;
	}

	/**
	 * Gets the entity id.
	 * 
	 * @param entityName
	 *            the entity name
	 * @param entityMasterList
	 *            the entity master list
	 * @return the entity id
	 */
	private Long getEntityId(String entityName, List<EntityMaster> entityMasterList) {
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityName.equalsIgnoreCase(entityMaster.getEntityName())) {
				return entityMaster.getEntityId();
			}
		}
		return null;
	}

	@Override
	public String saveEmployeeFilterList(String metaData, Long leaveSchemeTypeId, Long companyId) {

		Calendar cal = Calendar.getInstance();

		List<EmployeeLeaveSchemeType> cmpEmployeeLeaveSchemeTypes = employeeLeaveSchemeTypeDAO.findByCompany(companyId,
				cal.getTime());

		HashMap<String, EmployeeLeaveSchemeType> employeeLeaveSchemeMap = new HashMap<>();
		for (EmployeeLeaveSchemeType employeeLeaveSchemeType : cmpEmployeeLeaveSchemeTypes) {

			String mapkey = String.valueOf(employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeId()) + "_"
					+ String.valueOf(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee().getEmployeeId());
			employeeLeaveSchemeMap.put(mapkey, employeeLeaveSchemeType);
		}

		EntityMaster entityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId, entityMaster.getEntityId());

		LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findById(leaveSchemeTypeId);

		List<Long> leaveSchemeEmployeeIds = employeeLeaveSchemeDAO
				.getEmployeesOfLeaveSheme(leaveSchemeType.getLeaveScheme().getLeaveSchemeId(), cal.getTime());

		leaveSchemeTypeShortListDAO.deleteByCondition(leaveSchemeTypeId);
		EmployeeFilterTemplate empFilterTemplate = getEmpFilterTab(metaData);
		List<EmployeeFilter> listOfFields = empFilterTemplate.getEmployeeFilter();

		for (EmployeeFilter field : listOfFields) {
			LeaveSchemeTypeShortList leaveSchemeTypeShortList = new LeaveSchemeTypeShortList();

			leaveSchemeTypeShortList.setLeaveSchemeType(leaveSchemeType);

			if (field.getCloseBracket() != "" && field.getCloseBracket() != null) {
				leaveSchemeTypeShortList.setCloseBracket(field.getCloseBracket());
			}
			if (field.getOpenBracket() != "" && field.getOpenBracket() != null) {
				leaveSchemeTypeShortList.setOpenBracket(field.getOpenBracket());

			}
			if (field.getDictionaryId() != 0) {
				DataDictionary dataDictionary = dataDictionaryDAO.findById(field.getDictionaryId());
				leaveSchemeTypeShortList.setDataDictionary(dataDictionary);
			}

			if (field.getEqualityOperator() != "" && field.getEqualityOperator() != null) {
				try {
					String equalityOperator = URLDecoder.decode(field.getEqualityOperator(), "UTF-8");

					// Check Valid ShortList Operator
					ShortlistOperatorEnum shortlistOperatorEnum = ShortlistOperatorEnum
							.getFromOperator(equalityOperator);
					if (shortlistOperatorEnum == null) {
						throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_SHORTLIST_OPERATOR_NOT_VALID);
					}
					leaveSchemeTypeShortList.setEqualityOperator(equalityOperator);
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}

			}
			if (field.getLogicalOperator() != "" && field.getLogicalOperator() != null) {
				leaveSchemeTypeShortList.setLogicalOperator(field.getLogicalOperator());
			}
			if (field.getValue() != "" && field.getValue() != null) {
				String fieldValue = "";
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
				leaveSchemeTypeShortList.setValue(fieldValue);
			}

			leaveSchemeTypeShortListDAO.save(leaveSchemeTypeShortList);

		}
		List<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists = leaveSchemeTypeShortListDAO
				.findByCondition(leaveSchemeType.getLeaveSchemeTypeId());

		setEmployeeLeaveSchemeTypes(leaveSchemeTypeShortLists, companyId, leaveSchemeEmployeeIds, leaveSchemeType,
				employeeLeaveSchemeMap, formIds, null);

		return null;
	}

	@Override
	public void setEmployeeLeaveSchemeTypes(List<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists, Long companyId,
			List<Long> leaveSchemeEmployeeIds, LeaveSchemeType leaveSchType,
			HashMap<String, EmployeeLeaveSchemeType> employeeLeaveSchemeTypeMap, List<Long> formIds, Date currentDate) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		HashMap<Long, Tab> tabMap = new HashMap<>();
		Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
		List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();

		setFilterInfo(leaveSchemeTypeShortLists, finalFilterList, tableNames, codeDescDTOs, tabMap);

		String query = filtersInfoUtilsLogic.createQueryFilters(null, formIds, finalFilterList, tableNames,
				codeDescDTOs, companyId, currentDate, paramValueMap);

		List<BigInteger> resultSet = employeeDAO.findApplicableEmployeeIds(query, companyId, paramValueMap);

		List<Long> shortListedEmployeeIds = new ArrayList<>();

		for (BigInteger employeeId : resultSet) {
			shortListedEmployeeIds.add(Long.parseLong(String.valueOf(employeeId)));

		}

		List<Long> leaveSchemeEmployeeIdsCopy = new ArrayList<>(leaveSchemeEmployeeIds);

		List<EmployeeLeaveScheme> employeeLeaveSchemes = employeeLeaveSchemeDAO
				.findbyLeaveScheme(leaveSchType.getLeaveScheme().getLeaveSchemeId());
		HashMap<String, EmployeeLeaveScheme> employeeLeaveSchemeMap = new HashMap<>();
		for (EmployeeLeaveScheme employeeLeaveScheme : employeeLeaveSchemes) {

			String empLeaMapKey = String.valueOf(leaveSchType.getLeaveScheme().getLeaveSchemeId()) + "_"
					+ String.valueOf(employeeLeaveScheme.getEmployee().getEmployeeId());
			employeeLeaveSchemeMap.put(empLeaMapKey, employeeLeaveScheme);

		}

		leaveSchemeEmployeeIds.retainAll(shortListedEmployeeIds);

		leaveSchemeEmployeeIdsCopy.removeAll(leaveSchemeEmployeeIds);

		for (Long employeeId : leaveSchemeEmployeeIds) {

			EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeMap
					.get(String.valueOf(leaveSchType.getLeaveSchemeTypeId()) + "_" + String.valueOf(employeeId));

			if (employeeLeaveSchemeType == null) {
				EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeMap
						.get(String.valueOf(leaveSchType.getLeaveScheme().getLeaveSchemeId()) + "_"
								+ String.valueOf(employeeId));

				employeeLeaveSchemeType = new EmployeeLeaveSchemeType();
				employeeLeaveSchemeType.setEmployeeLeaveScheme(employeeLeaveScheme);
				employeeLeaveSchemeType.setLeaveSchemeType(leaveSchType);
				employeeLeaveSchemeType.setBalance(new BigDecimal(0));
				employeeLeaveSchemeType.setCarriedForward(new BigDecimal(0));
				employeeLeaveSchemeType.setCredited(new BigDecimal(0));
				employeeLeaveSchemeType.setEncashed(new BigDecimal(0));
				employeeLeaveSchemeType.setForfeited(new BigDecimal(0));
				employeeLeaveSchemeType.setPending(new BigDecimal(0));
				employeeLeaveSchemeType.setTaken(new BigDecimal(0));
				// vivek
				employeeLeaveSchemeType.setActive(true);
				EmployeeLeaveSchemeType persistEmpLeaveSchemeTypeObj = employeeLeaveSchemeTypeDAO
						.saveReturn(employeeLeaveSchemeType);

				Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflowSet = persistEmpLeaveSchemeTypeObj
						.getLeaveSchemeType().getLeaveSchemeTypeWorkflows();
				saveEmployeeLeaveReviewer(leaveSchemeTypeWorkflowSet, employeeId, persistEmpLeaveSchemeTypeObj,
						companyId);
			} else {

				if (!employeeLeaveSchemeType.getActive()) {
					// vivek
					employeeLeaveSchemeType.setActive(true);
					employeeLeaveSchemeTypeDAO.update(employeeLeaveSchemeType);

					Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflowSet = employeeLeaveSchemeType
							.getLeaveSchemeType().getLeaveSchemeTypeWorkflows();
					saveEmployeeLeaveReviewer(leaveSchemeTypeWorkflowSet, employeeId, employeeLeaveSchemeType,
							companyId);
				}

			}

		}

		for (Long employeeId : leaveSchemeEmployeeIdsCopy) {

			EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeMap
					.get(String.valueOf(leaveSchType.getLeaveSchemeTypeId()) + "_" + String.valueOf(employeeId));
			if (employeeLeaveSchemeType != null) {
				employeeLeaveSchemeType.setActive(false);
				employeeLeaveSchemeTypeDAO.update(employeeLeaveSchemeType);
			}

		}

	}

	private void saveEmployeeLeaveReviewer(Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflowSet, Long employeeId,
			EmployeeLeaveSchemeType employeeLeaveSchemeType, Long companyId) {
		Integer workFlowRuleValue;
		Long employeeId1 = null;
		Long employeeId2 = null;
		Long employeeId3 = null;
		for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : leaveSchemeTypeWorkflowSet) {

			if (leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL.toUpperCase())) {
				workFlowRuleValue = Integer.parseInt(leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue());
				Employee employee1Vo = employeeDAO.findByID(employeeId);
				for (int count = 1; count <= workFlowRuleValue; count++) {
					WorkFlowRuleMaster workFlowRuleListVO = workFlowRuleMasterDAO.findByRuleNameValue(
							PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, String.valueOf(count));

					List<Object[]> tuplelist = employeeleaveReviewerDAO.getEmployeeReviewersCountByCondition(employeeId,
							employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId(),
							workFlowRuleListVO.getWorkFlowRuleId());

					List<Long> revCountList = new ArrayList<>();
					if (!tuplelist.isEmpty()) {
						for (Object[] tuple : tuplelist) {
							revCountList.add((Long) tuple[1]);
						}
						Long maxRevCount = null;
						if (!revCountList.isEmpty()) {
							maxRevCount = Collections.max(revCountList);
						}

						for (Object[] tuple : tuplelist) {
							if (maxRevCount != null && maxRevCount.longValue() == ((Long) tuple[1]).longValue()) {
								if (count == 1) {
									employeeId1 = (Long) tuple[0];
								}
								if (count == 2) {
									employeeId2 = (Long) tuple[0];
								}
								if (count == 3) {
									employeeId3 = (Long) tuple[0];
								}
							}
						}
						Employee employeeRevVo = null;
						if (count == 1) {
							employeeRevVo = employeeDAO.findByID(employeeId1);
						}
						if (count == 2) {
							employeeRevVo = employeeDAO.findByID(employeeId2);
						}
						if (count == 3) {
							employeeRevVo = employeeDAO.findByID(employeeId3);
						}

						List<EmployeeLeaveReviewer> employeeLeaveReviewers = employeeleaveReviewerDAO
								.findByEmpLeaveSchemeAndLeaveTypeId(employeeId,
										employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId(),
										employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId(), companyId);
						if (employeeLeaveReviewers != null) {

						} else {
							EmployeeLeaveReviewer empLeaveRev = new EmployeeLeaveReviewer();
							empLeaveRev.setCompanyId(companyId);
							empLeaveRev.setEmployee1(employee1Vo);
							empLeaveRev.setEmployee2(employeeRevVo);
							empLeaveRev.setEmployeeLeaveScheme(employeeLeaveSchemeType.getEmployeeLeaveScheme());
							empLeaveRev.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
							empLeaveRev.setWorkFlowRuleMaster(workFlowRuleListVO);
							employeeleaveReviewerDAO.save(empLeaveRev);
						}

					}

				}

			}

		}
	}

	private void setFilterInfo(List<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists,
			List<GeneralFilterDTO> finalFilterList, Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			HashMap<Long, Tab> tabMap) {
		for (LeaveSchemeTypeShortList schemeTypeShortList : leaveSchemeTypeShortLists) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (schemeTypeShortList.getDataDictionary().getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(schemeTypeShortList.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic.getDynamicDictionaryInfo(schemeTypeShortList.getDataDictionary(),
						dataImportKeyValueDTO, tableNames, codeDescDTOs, tabMap);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (schemeTypeShortList.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(schemeTypeShortList.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (schemeTypeShortList.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(schemeTypeShortList.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(schemeTypeShortList.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(schemeTypeShortList.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(schemeTypeShortList.getEqualityOperator());
			generalFilterDTO.setFilterId(schemeTypeShortList.getShortListId());
			generalFilterDTO.setLogicalOperator(schemeTypeShortList.getLogicalOperator());
			generalFilterDTO.setValue(schemeTypeShortList.getValue());

			finalFilterList.add(generalFilterDTO);
		}

	}

	private EmployeeFilterTemplate getEmpFilterTab(String metaData) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = EmployeeFilterXMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (SAXException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		final StringReader xmlReader = new StringReader(metaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		EmployeeFilterTemplate empFilterTemplate = null;
		try {
			empFilterTemplate = (EmployeeFilterTemplate) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}
		return empFilterTemplate;
	}

	@Override
	public List<EmployeeFilterListForm> getEditEmployeeFilterList(Long leaveSchemeTypeId, Long companyId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();
		List<LeaveSchemeTypeShortList> leaveTypeShortList = leaveSchemeTypeShortListDAO
				.findSchemeTypeByCompany(leaveSchemeTypeId, companyId);
		if(leaveTypeShortList != null && !leaveTypeShortList.isEmpty())
		{
			for (LeaveSchemeTypeShortList employeeShortList : leaveTypeShortList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setCloseBracket(employeeShortList.getCloseBracket());
				employeeFilterListForm.setDataDictionaryId(employeeShortList.getDataDictionary().getDataDictionaryId());
				employeeFilterListForm.setEqualityOperator(employeeShortList.getEqualityOperator());
				employeeFilterListForm.setFilterId(employeeShortList.getShortListId());
				employeeFilterListForm.setLogicalOperator(employeeShortList.getLogicalOperator());
				employeeFilterListForm.setOpenBracket(employeeShortList.getOpenBracket());
				employeeFilterListForm
						.setDataType(generalFilterLogic.getFieldDataType(companyId, employeeShortList.getDataDictionary()));
				employeeFilterListForm.setValue(employeeShortList.getValue());
				employeeFilterList.add(employeeFilterListForm);
			}
		}
		return employeeFilterList;
	}

	@Override
	public void deleteFilter(Long filterId, Long companyId) {
		LeaveSchemeTypeShortList leaveSchemeTypeShortList = leaveSchemeTypeShortListDAO.findById(filterId);

		Calendar cal = Calendar.getInstance();
		List<EmployeeLeaveSchemeType> cmpEmployeeLeaveSchemeTypes = employeeLeaveSchemeTypeDAO.findByCompany(companyId,
				cal.getTime());

		HashMap<String, EmployeeLeaveSchemeType> employeeLeaveSchemeMap = new HashMap<>();
		for (EmployeeLeaveSchemeType employeeLeaveSchemeType : cmpEmployeeLeaveSchemeTypes) {

			String mapkey = String.valueOf(employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeId()) + "_"
					+ String.valueOf(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee().getEmployeeId());
			employeeLeaveSchemeMap.put(mapkey, employeeLeaveSchemeType);
		}

		EntityMaster entityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId, entityMaster.getEntityId());

		LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeShortList.getLeaveSchemeType();

		List<Long> leaveSchemeEmployeeIds = employeeLeaveSchemeDAO
				.getEmployeesOfLeaveSheme(leaveSchemeTypeVO.getLeaveScheme().getLeaveSchemeId(), cal.getTime());

		leaveSchemeTypeShortListDAO.delete(leaveSchemeTypeShortList);

		List<LeaveSchemeTypeShortList> leaveSchemeTypeShortLists = leaveSchemeTypeShortListDAO
				.findByCondition(leaveSchemeTypeVO.getLeaveSchemeTypeId());

		setEmployeeLeaveSchemeTypes(leaveSchemeTypeShortLists, companyId, leaveSchemeEmployeeIds, leaveSchemeTypeVO,
				employeeLeaveSchemeMap, formIds, null);

	}

	@Override
	public Set<LeaveSchemeForm> getLeaveSchemeList(Long companyId, Long leaveTypeId) {
		Set<LeaveSchemeForm> leaveSchemeFormSet = new HashSet<LeaveSchemeForm>();

		LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO.findLeaveTypeByCompId(leaveTypeId, companyId);

		Set<LeaveSchemeType> leaveSchemeTypes = leaveTypeMaster.getLeaveSchemeTypes();

		List<LeaveScheme> assignedLeaveSchemes = new ArrayList<>();

		for (LeaveSchemeType leaveSchemeType : leaveSchemeTypes) {
			assignedLeaveSchemes.add(leaveSchemeType.getLeaveScheme());

		}

		List<LeaveScheme> unassignedLeaveSchemes = leaveSchemeDAO.getAllLeaveScheme(companyId);

		unassignedLeaveSchemes.removeAll(assignedLeaveSchemes);

		for (LeaveScheme leaveScheme : unassignedLeaveSchemes) {

			LeaveSchemeForm leaveSchemeForm = new LeaveSchemeForm();

			leaveSchemeForm.setTemplateName(leaveScheme.getSchemeName());

			leaveSchemeForm.setLeaveSchemeId(leaveScheme.getLeaveSchemeId());
			leaveSchemeFormSet.add(leaveSchemeForm);

		}

		return leaveSchemeFormSet;

	}

	@Override
	public List<LeaveSchemeDTO> getAllLeaveSchemes(Long companyId) {
		List<LeaveSchemeDTO> leaveSchemeDTOs = new ArrayList<>();
		List<LeaveScheme> leaveSchemeVOs = leaveSchemeDAO.getAllLeaveScheme(companyId);
		for (LeaveScheme leaveScheme : leaveSchemeVOs) {
			LeaveSchemeDTO leaveSchemeDTO = new LeaveSchemeDTO();
			leaveSchemeDTO.setLeaveSchemeId(leaveScheme.getLeaveSchemeId());
			leaveSchemeDTO.setLeaveSchemeName(leaveScheme.getSchemeName());
			leaveSchemeDTOs.add(leaveSchemeDTO);
		}

		return leaveSchemeDTOs;
	}

	@Override
	public List<LeaveSchemeDTO> getAllLeaveSchemesGroup(Long companyId) {
		List<LeaveSchemeDTO> leaveSchemeDTOs = new ArrayList<>();
		List<LeaveScheme> leaveSchemeVOs = leaveSchemeDAO.getAllLeaveSchemes(companyId);
		for (LeaveScheme leaveScheme : leaveSchemeVOs) {
			LeaveSchemeDTO leaveSchemeDTO = new LeaveSchemeDTO();
			leaveSchemeDTO.setLeaveSchemeId(leaveScheme.getLeaveSchemeId());
			leaveSchemeDTO.setLeaveSchemeName(leaveScheme.getSchemeName());
			leaveSchemeDTOs.add(leaveSchemeDTO);
		}

		return leaveSchemeDTOs;
	}

	@Override
	public LeaveSchemeResponse callRedistributeProc(Long leaveSchemeTypeId) {
		LeaveSchemeResponse leaveSchemeResponse = new LeaveSchemeResponse();
		LeaveSchemeProcDTO leaveSchemeProcDTO = leaveSchemeDAO.callRedistributeProc(leaveSchemeTypeId);
		leaveSchemeResponse.setLeaveSchemeProc(leaveSchemeProcDTO);
		return leaveSchemeResponse;
	}

}
