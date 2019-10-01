package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveReviewerConditionDTO;
import com.payasia.common.dto.LeaveReviewersDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveReviewerDetailViewDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveSchemeTypeWorkflowDAO;
import com.payasia.dao.LeaveSchemeWorkflowDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveReviewerDetailView;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSchemeWorkflow;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LeaveReviewerLogic;

@Component
public class LeaveReviewerLogicImpl extends BaseLogic implements
		LeaveReviewerLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LeaveReviewerLogicImpl.class);

	@Resource
	LeaveSchemeDAO leaveSchemeDAO;
	@Resource
	LeaveSchemeWorkflowDAO leaveSchemeWorkFlowDAO;
	@Resource
	LeaveSchemeWorkflowDAO leaveSchemeWorkflowDAO;
	@Resource
	LeavePreferenceDAO leavePreferenceDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;
	@Resource
	LeaveReviewerDetailViewDAO leaveReviewerDetailViewDAO;
	@Resource
	EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;
	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	LeaveSchemeTypeWorkflowDAO leaveSchemeTypeWorkflowDAO;

	@Resource
	GeneralLogic generalLogic;
	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;

	@Override
	public List<LeaveReviewerForm> getLeaveSchemeList(Long companyId) {

		List<LeaveReviewerForm> leaveSchemeList = new ArrayList<LeaveReviewerForm>();
		List<LeaveScheme> leaveSchemeListVO = leaveSchemeDAO
				.getAllLeaveScheme(companyId);
		for (LeaveScheme leaveSchemeVO : leaveSchemeListVO) {
			LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
			leaveReviewerForm
					.setLeaveSchemeId(leaveSchemeVO.getLeaveSchemeId());
			leaveReviewerForm.setLeaveSchemeName(leaveSchemeVO.getSchemeName());
			leaveSchemeList.add(leaveReviewerForm);

		}

		return leaveSchemeList;
	}

	@Override
	public LeaveReviewerForm getLeaveTypeWorkFlow(
			Long employeeleaveSchemeTypeId, Long employeeLeaveSchemeId) {
		int leaveSchemeRuleVal = 0;
		if (employeeleaveSchemeTypeId == 0) {
			return getWorkFlowRuleLeaveReviewerValue(employeeLeaveSchemeId,
					leaveSchemeRuleVal);
		} else {
			EmployeeLeaveSchemeType empLeaveSchemeTypeVO = employeeLeaveSchemeTypeDAO
					.findById(employeeleaveSchemeTypeId);
			LeaveSchemeType leaveSchemeTypeVO = empLeaveSchemeTypeVO
					.getLeaveSchemeType();

			LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = leaveSchemeTypeWorkflowDAO
					.findByLeaveSchemeIdRuleName(
							leaveSchemeTypeVO.getLeaveSchemeTypeId(),
							PayAsiaConstants.LEAVE_REVIEWER_RULE);

			LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
			if (leaveSchemeTypeWorkflow != null) {
				// leaveSchemeTypeRuleVal = Integer
				// .parseInt(leaveSchemeTypeWorkflow
				// .getWorkFlowRuleMaster().getRuleValue());
				leaveReviewerForm.setRuleName(leaveSchemeTypeWorkflow
						.getWorkFlowRuleMaster().getRuleName());

				leaveReviewerForm.setRuleValue(leaveSchemeTypeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());

				leaveReviewerForm.setLeaveSchemeTypeId(leaveSchemeTypeVO
						.getLeaveSchemeTypeId());

			}
			return leaveReviewerForm;
		}

	}

	private LeaveReviewerForm getWorkFlowRuleLeaveReviewerValue(
			Long employeeLeaveSchemeId, int leaveSchemeRuleVal) {
		int leaveSchemeTypeRuleVal;
		String finalRuleValue;
		EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO
				.findById(employeeLeaveSchemeId);
		LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
		if (employeeLeaveScheme != null) {
			LeaveSchemeWorkflow leaveSchemeWorkflow = leaveSchemeWorkFlowDAO
					.findByLeaveSchemeIdRuleName(employeeLeaveScheme
							.getLeaveScheme().getLeaveSchemeId(),
							PayAsiaConstants.LEAVE_REVIEWER_RULE);
			if (leaveSchemeWorkflow != null) {
				leaveSchemeRuleVal = Integer.parseInt(leaveSchemeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());
			}
			LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = leaveSchemeTypeWorkflowDAO
					.findMaxWorkFlowRuleValByLeaveScheme(employeeLeaveScheme
							.getLeaveScheme().getLeaveSchemeId(),
							PayAsiaConstants.LEAVE_REVIEWER_RULE);

			if (leaveSchemeTypeWorkflow != null) {
				leaveSchemeTypeRuleVal = Integer
						.parseInt(leaveSchemeTypeWorkflow
								.getWorkFlowRuleMaster().getRuleValue());
				if (leaveSchemeTypeRuleVal > leaveSchemeRuleVal) {
					finalRuleValue = String.valueOf(leaveSchemeTypeRuleVal);
				} else {
					finalRuleValue = String.valueOf(leaveSchemeRuleVal);
				}

			} else {
				finalRuleValue = String.valueOf(leaveSchemeRuleVal);
			}
		} else {
			finalRuleValue = String.valueOf(leaveSchemeRuleVal);
		}

		leaveReviewerForm.setRuleValue(finalRuleValue);
		return leaveReviewerForm;
	}

	@Override
	public void saveLeaveReviewer(LeaveReviewerForm leaveReviewerForm,
			Long companyId, Long loggedInEmployeeId) {
		if (leaveReviewerForm.getEmployeeLeaveSchemeTypeId() == 0) {
			saveLeaveReviewerForAllLeaveType(leaveReviewerForm, companyId,
					loggedInEmployeeId);
		} else {
			int ruleValue = Integer.parseInt(leaveReviewerForm.getRuleValue());
			Class<?> c = leaveReviewerForm.getClass();
			Company companyVO = companyDAO.findById(companyId);
			try {

				Employee employee1 = employeeDAO.findById(leaveReviewerForm
						.getEmployeeId());
				EmployeeLeaveScheme empLeaveScheme = employeeLeaveSchemeDAO
						.findById(leaveReviewerForm.getEmployeeLeaveSchemeId());
				EmployeeLeaveSchemeType empLeaveSchemeType = employeeLeaveSchemeTypeDAO
						.findById(leaveReviewerForm
								.getEmployeeLeaveSchemeTypeId());

				EmployeeLeaveReviewer employeeLeaveReviewer = null;
				WorkFlowRuleMaster workFlowRuleReviewer1 = null;
				Employee employee2 = null;
				String methodNameForReviewer;
				String methodNameForWorkFlow;
				String methodNameForLeaveReviewerNum;

				// Check and validate
				for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
					methodNameForReviewer = "getLeaveReviewerId"
							+ leaveReviewerCount;
					methodNameForLeaveReviewerNum = "getLeaveReviewer"
							+ leaveReviewerCount;
					Method methodReviewer = c.getMethod(methodNameForReviewer);
					Method methodForLeaveReviewerNum = c
							.getMethod(methodNameForLeaveReviewerNum);
					Long employeeReviewerId;
					String leaveReviewerNum;

					employeeReviewerId = (Long) methodReviewer
							.invoke(leaveReviewerForm);
					leaveReviewerNum = (String) methodForLeaveReviewerNum
							.invoke(leaveReviewerForm);

					if (employeeReviewerId == null) {
						if (StringUtils.isNotBlank(leaveReviewerNum)) {
							EmployeeShortListDTO employeeShortListDTO = generalLogic
									.getShortListEmployeeIds(
											loggedInEmployeeId, companyId);
							List<Employee> employeeNumberList = employeeDAO
									.getEmployeeIdsForGroupCompany(
											leaveReviewerNum.trim(), companyId,
											companyVO.getCompanyGroup()
													.getGroupId(),
											employeeShortListDTO);
							int count = 1;
							for (Employee employee : employeeNumberList) {
								if (count == 1) {
									if (leaveReviewerCount == 1) {
										leaveReviewerForm
												.setLeaveReviewerId1(employee
														.getEmployeeId());
									}
									if (leaveReviewerCount == 2) {
										leaveReviewerForm
												.setLeaveReviewerId2(employee
														.getEmployeeId());
									}
									if (leaveReviewerCount == 3) {
										leaveReviewerForm
												.setLeaveReviewerId3(employee
														.getEmployeeId());
									}
								}
								count++;
							}
						}
					}
				}

				employeeLeaveReviewer = null;
				workFlowRuleReviewer1 = null;
				employee2 = null;
				methodNameForReviewer = "";
				methodNameForWorkFlow = "";
				methodNameForLeaveReviewerNum = "";

				// After Check, process for update reviewers
				for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
					methodNameForReviewer = "getLeaveReviewerId"
							+ leaveReviewerCount;
					Method methodReviewer = c.getMethod(methodNameForReviewer);
					Long employeeReviewerId;

					employeeReviewerId = (Long) methodReviewer
							.invoke(leaveReviewerForm);

					methodNameForWorkFlow = "getLeaveReviewerRuleId"
							+ leaveReviewerCount;
					Method methodWorkflow = c.getMethod(methodNameForWorkFlow);
					Long workFlowId = (Long) methodWorkflow
							.invoke(leaveReviewerForm);
					boolean status = true;
					if (employeeReviewerId != null) {
						if (ruleValue == 2) {
							if (leaveReviewerForm.getLeaveReviewerId1() == null) {
								status = false;
							}
						}
						if (ruleValue == 3) {
							if (leaveReviewerForm.getLeaveReviewerId1() == null
									&& leaveReviewerForm.getLeaveReviewerId2() != null) {
								status = false;
							}
							if (leaveReviewerForm.getLeaveReviewerId2() == null
									&& leaveReviewerForm.getLeaveReviewerId3() != null) {
								status = false;
							}
						}
						if (status) {
							employee2 = employeeDAO
									.findById(employeeReviewerId);
							employeeLeaveReviewer = new EmployeeLeaveReviewer();
							employeeLeaveReviewer.setEmployee1(employee1);
							employeeLeaveReviewer.setEmployee2(employee2);
							workFlowRuleReviewer1 = workFlowRuleMasterDAO
									.findByID(workFlowId);
							employeeLeaveReviewer
									.setWorkFlowRuleMaster(workFlowRuleReviewer1);
							employeeLeaveReviewer
									.setEmployeeLeaveSchemeType(empLeaveSchemeType);
							employeeLeaveReviewer
									.setEmployeeLeaveScheme(empLeaveScheme);
							employeeLeaveReviewerDAO
									.save(employeeLeaveReviewer);
						}
					}

				}
			} catch (IllegalArgumentException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (SecurityException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

	}

	public void saveLeaveReviewerForAllLeaveType(
			LeaveReviewerForm leaveReviewerForm, Long companyId,
			Long loggedInEmployeeId) {
		Company companyVO = companyDAO.findById(companyId);
		int ruleValue;
		try {

			Employee employee1 = employeeDAO.findById(leaveReviewerForm
					.getEmployeeId());
			EmployeeLeaveScheme empLeaveScheme = employeeLeaveSchemeDAO
					.findById(leaveReviewerForm.getEmployeeLeaveSchemeId());
			employeeLeaveReviewerDAO.deleteByConditionEmpId(
					leaveReviewerForm.getEmployeeId(),
					empLeaveScheme.getEmployeeLeaveSchemeId());
			Set<EmployeeLeaveSchemeType> employeeLeaveSchemeTypeSet = empLeaveScheme
					.getEmployeeLeaveSchemeTypes();

			for (EmployeeLeaveSchemeType empLeaveSchemeType : employeeLeaveSchemeTypeSet) {
				LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = leaveSchemeTypeWorkflowDAO
						.findByLeaveSchemeIdRuleName(empLeaveSchemeType
								.getLeaveSchemeType().getLeaveSchemeTypeId(),
								PayAsiaConstants.LEAVE_REVIEWER_RULE);
				ruleValue = Integer.parseInt(leaveSchemeTypeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());
				Class<?> c = leaveReviewerForm.getClass();
				EmployeeLeaveReviewer employeeLeaveReviewer = null;
				WorkFlowRuleMaster workFlowRuleReviewer1 = null;
				Employee employee2 = null;
				String methodNameForReviewer;
				String methodNameForWorkFlow;
				String methodNameForLeaveReviewerNum;

				// Check and validate
				for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
					methodNameForReviewer = "getLeaveReviewerId"
							+ leaveReviewerCount;
					methodNameForLeaveReviewerNum = "getLeaveReviewer"
							+ leaveReviewerCount;
					Method methodReviewer = c.getMethod(methodNameForReviewer);
					Method methodForLeaveReviewerNum = c
							.getMethod(methodNameForLeaveReviewerNum);
					Long employeeReviewerId;
					String leaveReviewerNum;

					employeeReviewerId = (Long) methodReviewer
							.invoke(leaveReviewerForm);
					leaveReviewerNum = (String) methodForLeaveReviewerNum
							.invoke(leaveReviewerForm);

					if (employeeReviewerId == null) {
						if (StringUtils.isNotBlank(leaveReviewerNum)) {
							EmployeeShortListDTO employeeShortListDTO = generalLogic
									.getShortListEmployeeIds(
											loggedInEmployeeId, companyId);
							List<Employee> employeeNumberList = employeeDAO
									.getEmployeeIdsForGroupCompany(
											leaveReviewerNum.trim(), companyId,
											companyVO.getCompanyGroup()
													.getGroupId(),
											employeeShortListDTO);
							int count = 1;
							for (Employee employee : employeeNumberList) {
								if (count == 1) {
									if (leaveReviewerCount == 1) {
										leaveReviewerForm
												.setLeaveReviewerId1(employee
														.getEmployeeId());
									}
									if (leaveReviewerCount == 2) {
										leaveReviewerForm
												.setLeaveReviewerId2(employee
														.getEmployeeId());
									}
									if (leaveReviewerCount == 3) {
										leaveReviewerForm
												.setLeaveReviewerId3(employee
														.getEmployeeId());
									}
								}
								count++;
							}
						}
					}
				}

				employeeLeaveReviewer = null;
				workFlowRuleReviewer1 = null;
				employee2 = null;
				methodNameForReviewer = "";
				methodNameForWorkFlow = "";
				methodNameForLeaveReviewerNum = "";

				// After Check, process for update reviewers

				for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
					methodNameForReviewer = "getLeaveReviewerId"
							+ leaveReviewerCount;
					Method methodReviewer = c.getMethod(methodNameForReviewer);
					Long employeeReviewerId;

					employeeReviewerId = (Long) methodReviewer
							.invoke(leaveReviewerForm);

					methodNameForWorkFlow = "getLeaveReviewerRuleId"
							+ leaveReviewerCount;
					Method methodWorkflow = c.getMethod(methodNameForWorkFlow);
					Long workFlowId = (Long) methodWorkflow
							.invoke(leaveReviewerForm);
					boolean status = true;
					if (employeeReviewerId != null) {
						if (ruleValue == 2) {
							if (leaveReviewerForm.getLeaveReviewerId1() == null) {
								status = false;
							}
						}
						if (ruleValue == 3) {
							if (leaveReviewerForm.getLeaveReviewerId1() == null
									&& leaveReviewerForm.getLeaveReviewerId2() != null) {
								status = false;
							}
							if (leaveReviewerForm.getLeaveReviewerId2() == null
									&& leaveReviewerForm.getLeaveReviewerId3() != null) {
								status = false;
							}
						}
						if (status) {
							employee2 = employeeDAO
									.findById(employeeReviewerId);
							employeeLeaveReviewer = new EmployeeLeaveReviewer();
							employeeLeaveReviewer.setEmployee1(employee1);
							employeeLeaveReviewer.setEmployee2(employee2);
							workFlowRuleReviewer1 = workFlowRuleMasterDAO
									.findByID(workFlowId);
							employeeLeaveReviewer
									.setWorkFlowRuleMaster(workFlowRuleReviewer1);
							employeeLeaveReviewer
									.setEmployeeLeaveSchemeType(empLeaveSchemeType);
							employeeLeaveReviewer
									.setEmployeeLeaveScheme(empLeaveScheme);
							employeeLeaveReviewerDAO
									.save(employeeLeaveReviewer);
						}

					}

				}

			}

		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public List<LeaveReviewerForm> getWorkFlowRuleList() {

		List<LeaveReviewerForm> workFlowRuleList = new ArrayList<LeaveReviewerForm>();
		List<WorkFlowRuleMaster> workFlowRuleListVO = workFlowRuleMasterDAO
				.findByRuleName(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER);

		for (WorkFlowRuleMaster workFlowRuleMasterVO : workFlowRuleListVO) {
			LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
			leaveReviewerForm.setLeaveReviewerRuleId(workFlowRuleMasterVO
					.getWorkFlowRuleId());
			workFlowRuleList.add(leaveReviewerForm);

		}

		return workFlowRuleList;

	}

	@Override
	public LeaveReviewerResponseForm getLeaveReviewers(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId) {

		if (sortDTO.getColumnName().equals("")) {
			sortDTO.setColumnName(SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME);
			sortDTO.setOrderType(PayAsiaConstants.ASC);
		}

		List<LeaveReviewerDetailView> leaveReviewerListVO;

		LeaveReviewerConditionDTO conditionDTO = new LeaveReviewerConditionDTO();
		if (searchCondition
				.equals(PayAsiaConstants.LEAVE_REVIEWER_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setEmployeeName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		if (searchCondition
				.equals(PayAsiaConstants.LEAVE_REVIEWER_EMPLOYEE_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber(searchText);
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.LEAVE_REVIEWER_LEAVE_SCHEME)) {
			if (StringUtils.isNotBlank(searchText)) {

				conditionDTO.setLeaveSchemeName("%" + searchText + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.LEAVE_REVIEWER_LEAVE_TYPE)) {
			if (StringUtils.isNotBlank(searchText)) {

				conditionDTO.setLeaveType("%" + searchText + "%");
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.LEAVE_REVIEWER_LEAVE_REVIEWER1)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLeaveReviewer1("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.LEAVE_REVIEWER_LEAVE_REVIEWER2)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLeaveReviewer2("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.LEAVE_REVIEWER_LEAVE_REVIEWER3)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLeaveReviewer3("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		int recordSize = 0;

		leaveReviewerListVO = leaveReviewerDetailViewDAO.findByCondition(
				conditionDTO, pageDTO, sortDTO, companyId);
		List<LeaveReviewerForm> leaveReviewerFormList = new ArrayList<LeaveReviewerForm>();
		for (LeaveReviewerDetailView leaveReviewerVO : leaveReviewerListVO) {
			String filterIds = "";
			LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
			/* ID ENCRYPT*/
			filterIds += FormatPreserveCryptoUtil.encrypt(leaveReviewerVO.getLeaveReviewerDetailViewPK()
					.getEmployeeId())
					+ ","
					+ FormatPreserveCryptoUtil.encrypt(leaveReviewerVO.getLeaveReviewerDetailViewPK()
							.getEmployeeLeaveSchemeId())
					+ ","
					+FormatPreserveCryptoUtil.encrypt(leaveReviewerVO.getLeaveReviewerDetailViewPK()
							.getEmployeeLeaveSchemeTypeId());
			leaveReviewerForm.setFilterIds(filterIds);

			String empName = leaveReviewerVO.getEmpFirstName() + " ";
			if (StringUtils.isNotBlank(leaveReviewerVO.getEmpLastName())) {
				empName += leaveReviewerVO.getEmpLastName();
			}
			leaveReviewerForm.setEmployeeName(empName + " ("
					+ leaveReviewerVO.getEmpEmployeeNumber() + ")");

			leaveReviewerForm.setLeaveReviewerId1(leaveReviewerVO
					.getReviewer1Id());
			String LeaveReviewer1Name = leaveReviewerVO.getReviewer1FirstName()
					+ " ";
			if (StringUtils.isNotBlank(leaveReviewerVO.getReviewer1LastName())) {
				LeaveReviewer1Name += leaveReviewerVO.getReviewer1LastName();
			}
			if (StringUtils.isNotBlank(leaveReviewerVO.getReviewer1FirstName())) {
				leaveReviewerForm.setLeaveReviewer1(LeaveReviewer1Name);
			} else {
				leaveReviewerForm.setLeaveReviewer1("");
			}

			leaveReviewerForm.setLeaveReviewerId2(leaveReviewerVO
					.getReviewer2Id());

			String LeaveReviewer2Name = leaveReviewerVO.getReviewer2FirstName()
					+ " ";
			if (StringUtils.isNotBlank(leaveReviewerVO.getReviewer2LastName())) {
				LeaveReviewer2Name += leaveReviewerVO.getReviewer2LastName();
			}
			if (StringUtils.isNotBlank(leaveReviewerVO.getReviewer2FirstName())) {
				leaveReviewerForm.setLeaveReviewer2(LeaveReviewer2Name);
			} else {
				leaveReviewerForm.setLeaveReviewer2("");
			}

			leaveReviewerForm.setLeaveReviewerId3(leaveReviewerVO
					.getReviewer3Id());
			String LeaveReviewer3Name = leaveReviewerVO.getReviewer3FirstName()
					+ " ";
			if (StringUtils.isNotBlank(leaveReviewerVO.getReviewer3LastName())) {
				LeaveReviewer3Name += leaveReviewerVO.getReviewer3LastName();
			}
			if (StringUtils.isNotBlank(leaveReviewerVO.getReviewer3FirstName())) {
				leaveReviewerForm.setLeaveReviewer3(LeaveReviewer3Name);
			} else {
				leaveReviewerForm.setLeaveReviewer3("");
			}

			leaveReviewerForm.setLeaveSchemeName(leaveReviewerVO
					.getLeaveSchemeName());
			leaveReviewerForm.setLeaveType(leaveReviewerVO.getLeaveTypeName());
			leaveReviewerFormList.add(leaveReviewerForm);
		}
		recordSize = (leaveReviewerDetailViewDAO.getCountByCondition(
				conditionDTO, companyId)).intValue();

		LeaveReviewerResponseForm response = new LeaveReviewerResponseForm();
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

		}
		response.setRows(leaveReviewerFormList);
		response.setRecords(recordSize);
		return response;

	}

	@Override
	public LeaveReviewerResponseForm getLeaveReviewerData(Long[] filterIds,
			Long companyId) {
		Long leaveSchemeTypeId = null;
		LeaveReviewerResponseForm leaveReviewerFormResponse = new LeaveReviewerResponseForm();
		List<LeaveReviewerForm> employeeLeaveReviewerList = new ArrayList<LeaveReviewerForm>();

		List<EmployeeLeaveReviewer> employeeLeaveReviewerListVO;
			employeeLeaveReviewerListVO = employeeLeaveReviewerDAO.findByCondition(
				filterIds[0], filterIds[1], filterIds[2], companyId);
		
		for (EmployeeLeaveReviewer employeeLeaveReviewerVO : employeeLeaveReviewerListVO) {

			LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();

			leaveReviewerForm.setEmployeeId(employeeLeaveReviewerVO
					.getEmployee1().getEmployeeId());

			String employeeName1 = employeeLeaveReviewerVO.getEmployee1()
					.getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeLeaveReviewerVO.getEmployee1()
					.getLastName())) {
				employeeName1 += employeeLeaveReviewerVO.getEmployee1()
						.getLastName();
			}
			employeeName1 += "["
					+ employeeLeaveReviewerVO.getEmployee1()
							.getEmployeeNumber() + "]";
			leaveReviewerForm.setEmployeeName(employeeName1);
			leaveReviewerForm.setLeaveSchemeId(employeeLeaveReviewerVO
					.getEmployeeLeaveScheme().getLeaveScheme()
					.getLeaveSchemeId());
			leaveReviewerForm.setLeaveSchemeName(employeeLeaveReviewerVO
					.getEmployeeLeaveScheme().getLeaveScheme().getSchemeName());
			leaveReviewerForm.setEmployeeLeaveSchemeId(employeeLeaveReviewerVO
					.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId());
			leaveSchemeTypeId = employeeLeaveReviewerVO
					.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveSchemeTypeId();
			leaveReviewerForm
					.setEmployeeLeaveSchemeTypeId(employeeLeaveReviewerVO
							.getEmployeeLeaveSchemeType()
							.getEmployeeLeaveSchemeTypeId());
			leaveReviewerForm.setLeaveSchemeTypeId(leaveSchemeTypeId);
			leaveReviewerForm.setLeaveTypeId(employeeLeaveReviewerVO
					.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveTypeMaster().getLeaveTypeId());
			leaveReviewerForm.setLeaveType(employeeLeaveReviewerVO
					.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveTypeMaster().getLeaveTypeName());
			
			/* ID ENCRYPT*/
			leaveReviewerForm.setLeaveReviewerId(FormatPreserveCryptoUtil.encrypt(employeeLeaveReviewerVO
					.getEmployee2().getEmployeeId()));

			String employeeName2 = employeeLeaveReviewerVO.getEmployee2()
					.getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeLeaveReviewerVO.getEmployee2()
					.getLastName())) {
				employeeName2 += employeeLeaveReviewerVO.getEmployee2()
						.getLastName();
			}
			employeeName2 += "["
					+ employeeLeaveReviewerVO.getEmployee2()
							.getEmployeeNumber() + "]";
			leaveReviewerForm.setLeaveReviewerName(employeeName2);
			leaveReviewerForm
					.setEmployeeLeaveReviewerId(employeeLeaveReviewerVO
							.getEmployeeLeaveReviewerID());
			employeeLeaveReviewerList.add(leaveReviewerForm);

		}
		WorkFlowRuleMaster workFlowRuleMasterVO = workFlowRuleMasterDAO
				.findByCondition(leaveSchemeTypeId,
						PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL);
		leaveReviewerFormResponse.setRuleValue(workFlowRuleMasterVO
				.getRuleValue());
		leaveReviewerFormResponse.setRows(employeeLeaveReviewerList);

		return leaveReviewerFormResponse;
	}

	@Override
	public void updateLeaveReviewer(LeaveReviewerForm leaveReviewerForm,
			Long companyId, Long loggedInEmployeeId) {
		Company companyVO = companyDAO.findById(companyId);
		int ruleValue = Integer.parseInt(leaveReviewerForm.getRuleValue());
		Class<?> c = leaveReviewerForm.getClass();

		try {

			EmployeeLeaveReviewer employeeLeaveReviewer = null;
			Employee employee1 = null;
			Employee employee2 = null;

			String methodNameForReviewer;
			String methodNameForLeaveReviewer;
			String methodNameForLeaveReviewerRule;
			String methodNameForLeaveReviewerNum;

			// Check and validate
			for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
				methodNameForReviewer = "getLeaveReviewerId"
						+ leaveReviewerCount;
				methodNameForLeaveReviewerNum = "getLeaveReviewer"
						+ leaveReviewerCount;

				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Method methodEmployeeLeaveReviewerNum = c
						.getMethod(methodNameForLeaveReviewerNum);

				Long employeeReviewerId;
				String employeeLeaveReviewerNum;

				employeeReviewerId = (Long) methodReviewer
						.invoke(leaveReviewerForm);

				employeeLeaveReviewerNum = (String) methodEmployeeLeaveReviewerNum
						.invoke(leaveReviewerForm);

				if (employeeReviewerId == null) {
					if (StringUtils.isNotBlank(employeeLeaveReviewerNum)) {
						EmployeeShortListDTO employeeShortListDTO = generalLogic
								.getShortListEmployeeIds(loggedInEmployeeId,
										companyId);
						List<Employee> employeeNumberList = employeeDAO
								.getEmployeeIdsForGroupCompany(
										employeeLeaveReviewerNum.trim(),
										companyId, companyVO.getCompanyGroup()
												.getGroupId(),
										employeeShortListDTO);
						int count = 1;
						for (Employee employee : employeeNumberList) {
							if (count == 1) {
								if (leaveReviewerCount == 1) {
									leaveReviewerForm
											.setLeaveReviewerId1(employee
													.getEmployeeId());
								}
								if (leaveReviewerCount == 2) {
									leaveReviewerForm
											.setLeaveReviewerId2(employee
													.getEmployeeId());
								}
								if (leaveReviewerCount == 3) {
									leaveReviewerForm
											.setLeaveReviewerId3(employee
													.getEmployeeId());
								}
							}
							count++;
						}
					}
				}
			}

			employeeLeaveReviewer = null;
			employee1 = null;
			employee2 = null;

			methodNameForReviewer = "";
			methodNameForLeaveReviewer = "";
			methodNameForLeaveReviewerRule = "";
			methodNameForLeaveReviewerNum = "";
			// After Check, process for update reviewers
			for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {

				methodNameForReviewer = "getLeaveReviewerId"
						+ leaveReviewerCount;
				methodNameForLeaveReviewer = "getEmployeeLeaveReviewerId"
						+ leaveReviewerCount;
				methodNameForLeaveReviewerRule = "getLeaveReviewerRuleId"
						+ leaveReviewerCount;

				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Method methodEmployeeLeaveReviewer = c
						.getMethod(methodNameForLeaveReviewer);
				Method methodEmployeeLeaveReviewerRule = c
						.getMethod(methodNameForLeaveReviewerRule);

				Long employeeReviewerId;
				Long employeeLeaveReviewerId;
				Long employeeLeaveReviewerRuleId;

				employeeLeaveReviewerId = (Long) methodEmployeeLeaveReviewer
						.invoke(leaveReviewerForm);

				employeeReviewerId = (Long) methodReviewer
						.invoke(leaveReviewerForm);

				employeeLeaveReviewerRuleId = (Long) methodEmployeeLeaveReviewerRule
						.invoke(leaveReviewerForm);

				if (employeeLeaveReviewerId != null) {
					employeeLeaveReviewer = employeeLeaveReviewerDAO
							.findById(employeeLeaveReviewerId);
					if (employeeReviewerId != null) {
						employee2 = employeeDAO.findById(employeeReviewerId);

						employeeLeaveReviewer.setEmployee2(employee2);

						employeeLeaveReviewerDAO.update(employeeLeaveReviewer);
					}
					if (employeeReviewerId == null
							&& employeeLeaveReviewer != null) {
						if (ruleValue == 2) {
							if (leaveReviewerCount == 2) {
								employeeLeaveReviewerDAO
										.delete(employeeLeaveReviewer);
							}
						}
						if (ruleValue == 3) {
							if (leaveReviewerCount == 2
									&& leaveReviewerForm.getLeaveReviewerId3() == null) {
								employeeLeaveReviewerDAO
										.delete(employeeLeaveReviewer);
							}
							if (leaveReviewerCount == 3) {
								employeeLeaveReviewerDAO
										.delete(employeeLeaveReviewer);
							}
						}
					}
				} else {

					if (employeeReviewerId != null) {
						String[] empIds = leaveReviewerForm.getEmployeeName()
								.split(",");
						EmployeeLeaveReviewer emplLeaveReviewer = new EmployeeLeaveReviewer();
						employee1 = employeeDAO.findById(Long
								.parseLong(empIds[0]));
						employee2 = employeeDAO.findById(employeeReviewerId);
						emplLeaveReviewer.setEmployee1(employee1);
						emplLeaveReviewer.setEmployee2(employee2);
						WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
								.findByID(employeeLeaveReviewerRuleId);
						emplLeaveReviewer
								.setWorkFlowRuleMaster(workFlowRuleMaster);
						EmployeeLeaveScheme empleaveScheme = employeeLeaveSchemeDAO
								.findById(Long.parseLong(empIds[1]));
						emplLeaveReviewer
								.setEmployeeLeaveScheme(empleaveScheme);
						EmployeeLeaveSchemeType empLeaveSchemeType = employeeLeaveSchemeTypeDAO
								.findById(Long.parseLong(empIds[2]));
						emplLeaveReviewer
								.setEmployeeLeaveSchemeType(empLeaveSchemeType);
						employeeLeaveReviewerDAO.save(emplLeaveReviewer);
					}
				}

			}
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	@Override
	public void deleteLeaveReviewer(Long[] filterIds) {
		/* ID DECRYPT */
		filterIds[0]= FormatPreserveCryptoUtil.decrypt(filterIds[0]);
		filterIds[1]= FormatPreserveCryptoUtil.decrypt(filterIds[1]);
		filterIds[2]= FormatPreserveCryptoUtil.decrypt(filterIds[2]);
		employeeLeaveReviewerDAO.deleteByCondition(filterIds[0], filterIds[1],
				filterIds[2]);

	}

	@Override
	public LeaveReviewerForm checkLeaveReviewer(Long employeeId,
			Long empLeaveSchemeId, Long employeeLeaveSchemeTypeId,
			Long companyId) {
		LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
		List<EmployeeLeaveReviewer> employeeLeaveReviewersList = employeeLeaveReviewerDAO
				.findByEmpLeaveSchemeAndLeaveTypeId(employeeId,
						empLeaveSchemeId, employeeLeaveSchemeTypeId, companyId);
		if (employeeLeaveReviewersList != null) {
			leaveReviewerForm.setEmployeeStatus("exists");

		} else {
			leaveReviewerForm.setEmployeeStatus("notexists");
		}

		return leaveReviewerForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.WorkFlowDelegateLogic#searchEmployee(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public LeaveReviewerResponseForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		Company company = companyDAO.findById(companyId);
		List<Long> companyIds = companyDAO.getDistinctAssignedGroupCompanies(
				employeeId, company.getCompanyGroup().getGroupId());

		for (Long assignedCompanyId : companyIds) {

			EmployeeShortListDTO employeeShortListDTO = generalLogic
					.getShortListEmployeeIds(employeeId, assignedCompanyId);
			conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

			if (StringUtils.isNotBlank(empName)) {
				conditionDTO.setEmployeeName("%" + empName.trim() + "%");
			}

			if (StringUtils.isNotBlank(empNumber)) {
				conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

			}
			/*
			 * int recordSize = employeeDAO.getCountForCondition(conditionDTO,
			 * assignedCompanyId);
			 */

			List<Employee> finalList = employeeDAO.findByCondition(
					conditionDTO, pageDTO, sortDTO, assignedCompanyId);

			for (Employee employee : finalList) {
				EmployeeListForm employeeForm = new EmployeeListForm();

				String employeeName = "";
				employeeName += employee.getFirstName() + " ";
				if (StringUtils.isNotBlank(employee.getLastName())) {
					employeeName += employee.getLastName();
				}
				employeeForm.setEmployeeName(employeeName);

				employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
				/* ID ENCRYPT*/
				employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
				employeeForm.setCompanyName(employee.getCompany()
						.getCompanyName());
				employeeListFormList.add(employeeForm);
			}

		}

		LeaveReviewerResponseForm response = new LeaveReviewerResponseForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public LeaveReviewerResponseForm searchEmployeeBySessionCompany(
			PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}

		List<Employee> finalList = employeeDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
			/* ID ENCRYPT*/
			//employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeListFormList.add(employeeForm);
		}

		LeaveReviewerResponseForm response = new LeaveReviewerResponseForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public LeaveReviewerResponseForm getLeaveType(Long employeeId,
			Long companyId) {

		LeaveReviewerResponseForm leaveReviewerResponseForm = new LeaveReviewerResponseForm();

		Company companyVO = companyDAO.findById(companyId);
		List<LeaveReviewerForm> leaveReviewerFormList = new ArrayList<LeaveReviewerForm>();
		EmployeeLeaveScheme empLeaveSchemeVO;
		empLeaveSchemeVO = employeeLeaveSchemeDAO.checkEmpLeaveSchemeByDate(
				null, employeeId, DateUtils.timeStampToString(DateUtils
						.getCurrentTimestampWithTime()), companyVO
						.getDateFormat());
		if (empLeaveSchemeVO == null) {
			empLeaveSchemeVO = employeeLeaveSchemeDAO
					.getEmpLeaveSchemeGreaterThanCurrDate(null, employeeId,
							DateUtils.timeStampToString(DateUtils
									.getCurrentTimestampWithTime()), companyVO
									.getDateFormat());
			leaveReviewerResponseForm
					.setErrorMsg("payasia.leave.reviewer.no.leave.scheme.assign");
		}
		if (empLeaveSchemeVO != null) {
			/*
			 * Date toDate = null; if (empLeaveSchemeVO.getEndDate() != null) {
			 * toDate = DateUtils.stringToDate( DateUtils.timeStampToString(
			 * empLeaveSchemeVO.getEndDate(), companyVO.getDateFormat()),
			 * companyVO.getDateFormat()); }
			 */
			ArrayList<EmployeeLeaveSchemeType> leaveSchemeArrayList = new ArrayList<>();

			List<EmployeeLeaveSchemeType> empLeaveSchemeTypeList = employeeLeaveSchemeTypeDAO
					.findByEmpLeaveSchemeId(
							empLeaveSchemeVO.getEmployeeLeaveSchemeId(), null,
							null);
			List<EmployeeLeaveReviewer> employeeLeaveReviewerList = employeeLeaveReviewerDAO
					.findByEmpLeaveSchemeId(empLeaveSchemeVO
							.getEmployeeLeaveSchemeId());
			if (employeeLeaveReviewerList != null) {
				for (EmployeeLeaveReviewer leaveReviewer : employeeLeaveReviewerList) {
					leaveSchemeArrayList.add(leaveReviewer
							.getEmployeeLeaveSchemeType());
				}
				empLeaveSchemeTypeList.removeAll(leaveSchemeArrayList);
			}

			if (empLeaveSchemeTypeList.size() == 0) {
				leaveReviewerResponseForm
						.setErrorMsg("payasia.leave.reviewers.already.assigned");
			}

			for (EmployeeLeaveSchemeType leaveSchemeType : empLeaveSchemeTypeList) {
				LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
				try {
					leaveReviewerForm.setLeaveType(URLEncoder.encode(
							leaveSchemeType.getLeaveSchemeType()
									.getLeaveTypeMaster().getLeaveTypeName(),
							"UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
				leaveReviewerForm.setLeaveTypeId(leaveSchemeType
						.getEmployeeLeaveSchemeTypeId());
				leaveReviewerForm.setLeaveSchemeId(leaveSchemeType
						.getLeaveSchemeType().getLeaveScheme()
						.getLeaveSchemeId());
				try {
					leaveReviewerForm
							.setLeaveSchemeName(URLEncoder.encode(
									leaveSchemeType.getLeaveSchemeType()
											.getLeaveScheme().getSchemeName(),
									"UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}

				leaveReviewerForm.setEmployeeLeaveSchemeId(empLeaveSchemeVO
						.getEmployeeLeaveSchemeId());
				leaveReviewerFormList.add(leaveReviewerForm);
			}
			leaveReviewerResponseForm.setRows(leaveReviewerFormList);

		}
		leaveReviewerResponseForm.setRows(leaveReviewerFormList);

		return leaveReviewerResponseForm;

	}

	@Override
	public LeaveReviewerResponseForm getActiveWithFutureLeaveScheme(
			Long employeeId, Long companyId) {

		LeaveReviewerResponseForm leaveReviewerResponseForm = new LeaveReviewerResponseForm();

		Company companyVO = companyDAO.findById(companyId);
		List<LeaveReviewerForm> leaveReviewerFormList = new ArrayList<LeaveReviewerForm>();
		List<EmployeeLeaveScheme> empLeaveSchemeListVO = employeeLeaveSchemeDAO
				.getActiveWithFutureLeaveScheme(employeeId, DateUtils
						.timeStampToString(
								DateUtils.getCurrentTimestampWithTime(),
								companyVO.getDateFormat()), companyVO
						.getDateFormat());
		if (empLeaveSchemeListVO == null) {
			leaveReviewerResponseForm
					.setErrorMsg("payasia.leave.reviewer.no.leave.scheme.assign");
		}
		if (empLeaveSchemeListVO != null) {

			for (EmployeeLeaveScheme empLeaveScheme : empLeaveSchemeListVO) {
				LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
				leaveReviewerForm.setLeaveSchemeId(empLeaveScheme
						.getLeaveScheme().getLeaveSchemeId());
				try {
					leaveReviewerForm.setLeaveSchemeName(URLEncoder.encode(
							empLeaveScheme.getLeaveScheme().getSchemeName(),
							"UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}

				leaveReviewerForm.setEmployeeLeaveSchemeId(empLeaveScheme
						.getEmployeeLeaveSchemeId());
				leaveReviewerFormList.add(leaveReviewerForm);
			}

		}
		leaveReviewerResponseForm.setRows(leaveReviewerFormList);

		return leaveReviewerResponseForm;

	}

	public boolean checkValidLeaveScheme(Date fromDate, Date toDate,
			String compDateFormat) {
		boolean status = true;
		Date currentDate = DateUtils.stringToDate(DateUtils.timeStampToString(
				DateUtils.getCurrentTimestampWithTime(), compDateFormat));

		if (currentDate.before(fromDate)) {
			status = false;
		}
		if (toDate != null) {
			if (currentDate.after(toDate)) {
				status = false;
			}
		}

		if (status) {
			return true;
		} else
			return false;

	}

	@Override
	public String isAllowManagerSelfApproveLeave(Long companyId) {
		Boolean status = false;
		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			status = leavePreferenceVO.isAllowManagerSelfApproveLeave();
		}
		return status.toString();
	}

	@Override
	public List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId,
			String searchString, Long employeeId) {
		List<EmployeeListForm> leaveBalanceSummaryFormList = new ArrayList<EmployeeListForm>();
		Company companyVO = companyDAO.findById(companyId);
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		List<Employee> employeeNumberList = employeeDAO
				.getEmployeeIdsForGroupCompany(searchString.trim(), companyId,
						companyVO.getCompanyGroup().getGroupId(),
						employeeShortListDTO);

		for (Employee employee : employeeNumberList) {
			EmployeeListForm employeeListForm = new EmployeeListForm();
			employeeListForm.setEmployeeNumber(employee.getEmployeeNumber());
			String empName = "";
			empName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empName += employee.getLastName();
			}
			employeeListForm.setEmployeeName(empName);
			/* ID ENCRYPT*/
			employeeListForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			leaveBalanceSummaryFormList.add(employeeListForm);

		}
		return leaveBalanceSummaryFormList;
	}

	@Override
	public List<EmployeeListForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId) {
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(
				searchString.trim(), companyId, employeeShortListDTO);
		for (Employee employee : employeeNumberList) {
			EmployeeListForm employeeListForm = new EmployeeListForm();

			try {
				employeeListForm.setEmployeeNumber(URLEncoder.encode(
						employee.getEmployeeNumber(), "UTF-8"));

				String employeeName = URLEncoder.encode(
						employee.getFirstName(), "UTF-8");

				if (employee.getLastName() != null) {
					employeeName = employeeName
							+ " "
							+ URLEncoder
									.encode(employee.getLastName(), "UTF-8");
				}
				employeeListForm.setEmployeeName(employeeName);

			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}

			employeeListForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeListForm);

		}
		return employeeListFormList;
	}

	@Override
	public LeaveReviewerResponseForm getLeaveTypeListForleaveScheme(
			Long employeeLeaveSchemeId, Long companyId) {

		LeaveReviewerResponseForm leaveReviewerResponseForm = new LeaveReviewerResponseForm();

		List<LeaveReviewerForm> leaveReviewerFormList = new ArrayList<LeaveReviewerForm>();
		EmployeeLeaveScheme empLeaveSchemeVO = employeeLeaveSchemeDAO
				.findSchemeByCompanyId(employeeLeaveSchemeId, companyId);

		if (empLeaveSchemeVO != null) {

			ArrayList<EmployeeLeaveSchemeType> leaveSchemeArrayList = new ArrayList<>();

			List<EmployeeLeaveSchemeType> empLeaveSchemeTypeList = employeeLeaveSchemeTypeDAO
					.findByEmpLeaveSchemeId(
							empLeaveSchemeVO.getEmployeeLeaveSchemeId(), null,
							null);
			List<EmployeeLeaveReviewer> employeeLeaveReviewerList = employeeLeaveReviewerDAO
					.findByEmpLeaveSchemeId(empLeaveSchemeVO
							.getEmployeeLeaveSchemeId());
			if (employeeLeaveReviewerList != null) {
				for (EmployeeLeaveReviewer leaveReviewer : employeeLeaveReviewerList) {
					leaveSchemeArrayList.add(leaveReviewer
							.getEmployeeLeaveSchemeType());
				}
				empLeaveSchemeTypeList.removeAll(leaveSchemeArrayList);
			}

			if (empLeaveSchemeTypeList.size() == 0
					&& employeeLeaveReviewerList == null) {
				leaveReviewerResponseForm
						.setErrorMsg("payasia.leave.reviewers.no.leave.type.assigned");
			}

			else if (empLeaveSchemeTypeList.size() == 0) {
				leaveReviewerResponseForm
						.setErrorMsg("payasia.leave.reviewers.already.assigned");
			}

			for (EmployeeLeaveSchemeType leaveSchemeType : empLeaveSchemeTypeList) {
				LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();
				try {
					leaveReviewerForm.setLeaveType(URLEncoder.encode(
							leaveSchemeType.getLeaveSchemeType()
									.getLeaveTypeMaster().getLeaveTypeName(),
							"UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
				leaveReviewerForm.setLeaveTypeId(leaveSchemeType
						.getEmployeeLeaveSchemeTypeId());
				leaveReviewerForm.setLeaveSchemeId(leaveSchemeType
						.getLeaveSchemeType().getLeaveScheme()
						.getLeaveSchemeId());
				try {
					leaveReviewerForm
							.setLeaveSchemeName(URLEncoder.encode(
									leaveSchemeType.getLeaveSchemeType()
											.getLeaveScheme().getSchemeName(),
									"UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}

				leaveReviewerForm.setEmployeeLeaveSchemeId(empLeaveSchemeVO
						.getEmployeeLeaveSchemeId());
				leaveReviewerFormList.add(leaveReviewerForm);
			}
			leaveReviewerResponseForm.setRows(leaveReviewerFormList);

		}
		leaveReviewerResponseForm.setRows(leaveReviewerFormList);

		return leaveReviewerResponseForm;

	}

	public void saveLeaveReviewerForAllLeaveTypeImport(
			LeaveReviewersDTO leaveReviewerDTO, Long companyId) {
		int ruleValue;
		try {
			Employee employee1 = employeeDAO.findById(leaveReviewerDTO
					.getEmployeeId());
			EmployeeLeaveScheme empLeaveScheme = employeeLeaveSchemeDAO
					.findById(leaveReviewerDTO.getEmployeeLeaveSchemeId());
			employeeLeaveReviewerDAO.deleteByConditionEmpId(
					leaveReviewerDTO.getEmployeeId(),
					empLeaveScheme.getEmployeeLeaveSchemeId());
			Set<EmployeeLeaveSchemeType> employeeLeaveSchemeTypeSet = empLeaveScheme
					.getEmployeeLeaveSchemeTypes();

			for (EmployeeLeaveSchemeType empLeaveSchemeType : employeeLeaveSchemeTypeSet) {
				LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = leaveSchemeTypeWorkflowDAO
						.findByLeaveSchemeIdRuleName(empLeaveSchemeType
								.getLeaveSchemeType().getLeaveSchemeTypeId(),
								PayAsiaConstants.LEAVE_REVIEWER_RULE);
				ruleValue = Integer.parseInt(leaveSchemeTypeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());
				Class<?> c = leaveReviewerDTO.getClass();
				EmployeeLeaveReviewer employeeLeaveReviewer = null;
				WorkFlowRuleMaster workFlowRuleReviewer1 = null;
				Employee employee2 = null;
				String methodNameForReviewer;
				for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
					methodNameForReviewer = "getReviewer" + leaveReviewerCount
							+ "Id";
					Method methodReviewer = c.getMethod(methodNameForReviewer);
					Long employeeReviewerId;

					employeeReviewerId = (Long) methodReviewer
							.invoke(leaveReviewerDTO);
					if (employeeReviewerId != null) {
						employee2 = employeeDAO.findById(employeeReviewerId);
						employeeLeaveReviewer = new EmployeeLeaveReviewer();
						employeeLeaveReviewer.setEmployee1(employee1);
						employeeLeaveReviewer.setEmployee2(employee2);
						workFlowRuleReviewer1 = workFlowRuleMasterDAO
								.findByRuleNameValue(
										PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER,
										String.valueOf(leaveReviewerCount));
						employeeLeaveReviewer
								.setWorkFlowRuleMaster(workFlowRuleReviewer1);
						employeeLeaveReviewer
								.setEmployeeLeaveSchemeType(empLeaveSchemeType);
						employeeLeaveReviewer
								.setEmployeeLeaveScheme(empLeaveScheme);
						employeeLeaveReviewerDAO.save(employeeLeaveReviewer);
					}

				}

			}

		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public LeaveReviewerForm importLeaveReviewer(
			LeaveReviewerForm leaveReviewerForm, Long companyId) {
		String fileName = leaveReviewerForm.getFileUpload()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		LeaveReviewerForm leaveReviewerExcelDataForm = new LeaveReviewerForm();
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			leaveReviewerExcelDataForm = ExcelUtils
					.getLeaveReviewersFromXLS(leaveReviewerForm.getFileUpload());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			leaveReviewerExcelDataForm = ExcelUtils
					.getLeaveReviewersFromXLSX(leaveReviewerForm
							.getFileUpload());
		}

		HashMap<String, LeaveReviewersDTO> leaveReviewersDTOMap = new HashMap<>();
		LeaveReviewerForm reviewerForm = new LeaveReviewerForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();

		setLeaveReviewersImportDataDTO(dataImportLogDTOs,
				leaveReviewerExcelDataForm, leaveReviewersDTOMap, companyId);

		validateImpotedData(dataImportLogDTOs, leaveReviewersDTOMap, companyId);

		if (!dataImportLogDTOs.isEmpty()) {
			reviewerForm.setDataValid(false);
			reviewerForm.setDataImportLogDTOs(dataImportLogDTOs);
			return reviewerForm;
		}

		Set<String> keySet = leaveReviewersDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			LeaveReviewersDTO leaveReviewersDTO = leaveReviewersDTOMap.get(key);
			if (leaveReviewersDTO.getLeaveType().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_TYPE_SEARCH_ALL)) {
				saveLeaveReviewerForAllLeaveTypeImport(leaveReviewersDTO,
						companyId);
			} else {
				int ruleValue = Integer.parseInt(leaveReviewersDTO
						.getRuleValue());
				Class<?> c = leaveReviewersDTO.getClass();
				try {
					Employee employee1 = employeeDAO.findById(leaveReviewersDTO
							.getEmployeeId());
					EmployeeLeaveScheme empLeaveScheme = employeeLeaveSchemeDAO
							.findById(leaveReviewersDTO
									.getEmployeeLeaveSchemeId());
					EmployeeLeaveSchemeType empLeaveSchemeType = employeeLeaveSchemeTypeDAO
							.findByEmpLeaveSchemeAndLeaveType(leaveReviewersDTO
									.getEmployeeLeaveSchemeId(),
									leaveReviewersDTO.getLeaveType());

					// Check Existing Leave Reviewers
					List<EmployeeLeaveReviewer> employeeLeaveReviewerVOList = employeeLeaveReviewerDAO
							.findByCondition(leaveReviewersDTO.getEmployeeId(),
									leaveReviewersDTO
											.getEmployeeLeaveSchemeId(),
									empLeaveSchemeType
											.getEmployeeLeaveSchemeTypeId(),
									companyId);
					if (employeeLeaveReviewerVOList != null) {
						employeeLeaveReviewerDAO.deleteByCondition(
								leaveReviewersDTO.getEmployeeId(),
								leaveReviewersDTO.getEmployeeLeaveSchemeId(),
								empLeaveSchemeType
										.getEmployeeLeaveSchemeTypeId());
					}

					EmployeeLeaveReviewer employeeLeaveReviewer = null;
					WorkFlowRuleMaster workFlowRuleReviewer = null;
					Employee employee2 = null;
					String methodNameForReviewer;
					for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
						methodNameForReviewer = "getReviewer"
								+ leaveReviewerCount + "Id";
						Method methodReviewer = c
								.getMethod(methodNameForReviewer);
						Long employeeReviewerId;

						employeeReviewerId = (Long) methodReviewer
								.invoke(leaveReviewersDTO);

						if (employeeReviewerId != null) {
							employee2 = employeeDAO
									.findById(employeeReviewerId);
							employeeLeaveReviewer = new EmployeeLeaveReviewer();
							employeeLeaveReviewer.setEmployee1(employee1);
							employeeLeaveReviewer.setEmployee2(employee2);
							workFlowRuleReviewer = workFlowRuleMasterDAO
									.findByRuleNameValue(
											PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER,
											String.valueOf(leaveReviewerCount));
							employeeLeaveReviewer
									.setWorkFlowRuleMaster(workFlowRuleReviewer);
							employeeLeaveReviewer
									.setEmployeeLeaveSchemeType(empLeaveSchemeType);
							employeeLeaveReviewer
									.setEmployeeLeaveScheme(empLeaveScheme);
							employeeLeaveReviewerDAO
									.save(employeeLeaveReviewer);
						}
					}
				} catch (IllegalArgumentException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (SecurityException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		reviewerForm.setDataValid(true);
		return reviewerForm;
	}

	public void setLeaveReviewersImportDataDTO(
			List<DataImportLogDTO> dataImportLogDTOs,
			LeaveReviewerForm leaveReviewerExcelFieldForm,
			HashMap<String, LeaveReviewersDTO> leaveReviewersDTOMap,
			Long companyId) {
		for (HashMap<String, String> map : leaveReviewerExcelFieldForm
				.getImportedData()) {
			LeaveReviewersDTO leaveReviewersDTO = new LeaveReviewersDTO();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get(PayAsiaConstants.HASH_KEY_ROW_NUMBER);
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				if (key != PayAsiaConstants.HASH_KEY_ROW_NUMBER) {
					if (key.equals(PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY)) {
						leaveReviewersDTO.setEmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME)) {
						leaveReviewersDTO.setLeaveType(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME)) {
						leaveReviewersDTO.setLeaveScheme(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER1_EMPLOYEE_NUMBER)) {
						leaveReviewersDTO.setReviewer1EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER1_COMPANY_CODE)) {
						leaveReviewersDTO.setReviewer1CompanyCode(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER2_EMPLOYEE_NUMBER)) {
						leaveReviewersDTO.setReviewer2EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER2_COMPANY_CODE)) {
						leaveReviewersDTO.setReviewer2CompanyCode(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER3_EMPLOYEE_NUMBER)) {
						leaveReviewersDTO.setReviewer3EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER3_COMPANY_CODE)) {
						leaveReviewersDTO.setReviewer3CompanyCode(value);
					}
					leaveReviewersDTOMap.put(rowNumber, leaveReviewersDTO);
				}
			}
		}
	}

	/**
	 * Purpose: To Validate imported Leave Reviewer.
	 * 
	 * @param leaveReviewersDTOMap
	 *            the leaveReviewersDataDTOMap
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImpotedData(List<DataImportLogDTO> dataImportLogDTOs,
			HashMap<String, LeaveReviewersDTO> leaveReviewersDTOMap,
			Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		List<String> leaveSchemeNameList = new ArrayList<>();
		List<Tuple> leaveSchemeListVO = leaveSchemeDAO
				.getLeaveSchemeNameTupleList(companyId);
		for (Tuple leaveSchemeTuple : leaveSchemeListVO) {
			String leaveScheme = (String) leaveSchemeTuple.get(
					getAlias(LeaveScheme_.schemeName), String.class);
			leaveSchemeNameList.add(leaveScheme.toUpperCase());
		}

		HashMap<String, Long> employeeNumberMap = new HashMap<String, Long>();
		List<Tuple> employeeNameListVO = employeeDAO
				.getEmployeeNameTupleList(companyId);
		for (Tuple empTuple : employeeNameListVO) {
			String employeeName = (String) empTuple.get(
					getAlias(Employee_.employeeNumber), String.class);
			Long employeeId = (Long) empTuple.get(
					getAlias(Employee_.employeeId), Long.class);
			employeeNumberMap.put(employeeName.toUpperCase(), employeeId);
		}
		HashMap<String, List<String>> leaveSchemeLeaveTypeListMap = new HashMap<String, List<String>>();

		Set<String> keySet = leaveReviewersDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			LeaveReviewersDTO postLeavetransDTO = leaveReviewersDTOMap.get(key);

			String rowNumber = key;
			if (key.equalsIgnoreCase(PayAsiaConstants.HASH_KEY_ROW_NUMBER)) {
				continue;
			}
			if (StringUtils.isBlank(postLeavetransDTO.getEmployeeNumber())) {
				setLeaveReviewersDataImportLogs(dataImportLogDTOs,
						PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY,
						"payasia.empty", Long.parseLong(rowNumber));
				continue;
			}
			if (StringUtils.isNotBlank(postLeavetransDTO.getEmployeeNumber())) {
				if (!employeeNumberMap.containsKey(postLeavetransDTO
						.getEmployeeNumber().toUpperCase())) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY,
							"payasia.invalid.employee.number",
							Long.parseLong(rowNumber));
					continue;
				}
				postLeavetransDTO.setEmployeeId(employeeNumberMap
						.get(postLeavetransDTO.getEmployeeNumber()
								.toUpperCase()));
				// Leave Scheme Name
				if (StringUtils.isBlank(postLeavetransDTO.getLeaveScheme())) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME,
							"payasia.empty", Long.parseLong(rowNumber));
					continue;
				}
				if (!leaveSchemeNameList.contains(postLeavetransDTO
						.getLeaveScheme().toUpperCase())) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME,
							"payasia.invalid.leave.scheme",
							Long.parseLong(rowNumber));
					continue;
				}
				List<EmployeeLeaveScheme> empLeaveSchemeListVO = employeeLeaveSchemeDAO
						.getActiveWithFutureLeaveScheme(
								employeeNumberMap.get(postLeavetransDTO
										.getEmployeeNumber().toUpperCase()),
								DateUtils.timeStampToString(
										DateUtils.getCurrentTimestampWithTime(),
										companyVO.getDateFormat()), companyVO
										.getDateFormat());
				if (empLeaveSchemeListVO.isEmpty()) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME,
							"payasia.leave.reviewer.no.leave.scheme.assign",
							Long.parseLong(rowNumber));
					continue;
				}
				Long validEmpleaveSchemeId = null;
				validEmpleaveSchemeId = getValidLeaveScheme(companyId,
						leaveSchemeLeaveTypeListMap, postLeavetransDTO,
						empLeaveSchemeListVO, validEmpleaveSchemeId);
				if (validEmpleaveSchemeId == null) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME,
							"payasia.leave.reviewer.no.leave.scheme.assign",
							Long.parseLong(rowNumber));
					continue;
				}
				postLeavetransDTO
						.setEmployeeLeaveSchemeId(validEmpleaveSchemeId);
				if (StringUtils.isBlank(postLeavetransDTO.getLeaveType())) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME,
							"payasia.empty", Long.parseLong(rowNumber));
					continue;
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getLeaveType())) {
					List<String> leaveTypeList = leaveSchemeLeaveTypeListMap
							.get(postLeavetransDTO.getLeaveScheme()
									.toUpperCase());

					if (!leaveTypeList.contains(postLeavetransDTO
							.getLeaveType().toUpperCase())) {
						setLeaveReviewersDataImportLogs(dataImportLogDTOs,
								PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME,
								"payasia.invalid.leave.type",
								Long.parseLong(rowNumber));
						continue;
					}
					int leaveReviewerWorkflowRuleVal = 0;
					LeaveReviewerForm leaveReviewerForm = getWorkFlowRuleLeaveReviewerValue(
							validEmpleaveSchemeId, leaveReviewerWorkflowRuleVal);
					if (StringUtils
							.isNotBlank(leaveReviewerForm.getRuleValue())) {
						leaveReviewerWorkflowRuleVal = Integer
								.valueOf(leaveReviewerForm.getRuleValue());
						postLeavetransDTO.setRuleValue(leaveReviewerForm
								.getRuleValue());
					}
					if (leaveReviewerWorkflowRuleVal == 0) {
						setLeaveReviewersDataImportLogs(
								dataImportLogDTOs,
								PayAsiaConstants.PAYASIA_LEAVE_REVIEWER,
								"payasia.hris.reviewer.workflow.level.not.configured",
								Long.parseLong(rowNumber));
					}
					getValidLeaveReviewers(dataImportLogDTOs, rowNumber,
							postLeavetransDTO, leaveReviewerWorkflowRuleVal);
				}
			}
			leaveReviewersDTOMap.put(key, postLeavetransDTO);
		}
	}

	private void getValidLeaveReviewers(
			List<DataImportLogDTO> dataImportLogDTOs, String key,
			LeaveReviewersDTO postLeavetransDTO,
			int leaveReviewerWorkflowRuleVal) {
		if (leaveReviewerWorkflowRuleVal == 1
				|| leaveReviewerWorkflowRuleVal > 1) {
			if (StringUtils.isNotBlank(postLeavetransDTO
					.getReviewer1EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO
							.getReviewer1CompanyCode())) {
				Employee reviewer1 = employeeDAO
						.getEmployeeByEmpNumAndCompCode(
								postLeavetransDTO.getReviewer1EmployeeNumber(),
								postLeavetransDTO.getReviewer1CompanyCode());
				if (reviewer1 == null) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.REVIEWER1,
							"payasia.invalid.hris.reviewer.1",
							Long.parseLong(key));
				} else {
					postLeavetransDTO.setReviewer1Id(reviewer1.getEmployeeId());
					if (postLeavetransDTO.getEmployeeNumber().equalsIgnoreCase(
							postLeavetransDTO.getReviewer1EmployeeNumber())) {
						setLeaveReviewersDataImportLogs(dataImportLogDTOs,
								PayAsiaConstants.REVIEWER1,
								"payasia.leave.reviewer.names.cannot.be.same",
								Long.parseLong(key));
					}
				}
			} else {
				setLeaveReviewersDataImportLogs(dataImportLogDTOs,
						PayAsiaConstants.REVIEWER1,
						"payasia.invalid.hris.reviewer.1", Long.parseLong(key));
			}
		}
		if (leaveReviewerWorkflowRuleVal == 2
				|| leaveReviewerWorkflowRuleVal > 2) {
			if (StringUtils.isNotBlank(postLeavetransDTO
					.getReviewer2EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO
							.getReviewer2CompanyCode())) {
				Employee reviewer2 = employeeDAO
						.getEmployeeByEmpNumAndCompCode(
								postLeavetransDTO.getReviewer2EmployeeNumber(),
								postLeavetransDTO.getReviewer2CompanyCode());
				if (reviewer2 == null) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.REVIEWER2,
							"payasia.invalid.hris.reviewer.2",
							Long.parseLong(key));
				} else {
					postLeavetransDTO.setReviewer2Id(reviewer2.getEmployeeId());
				}
			} else {
				// setLeaveReviewersDataImportLogs(dataImportLogDTOs,
				// PayAsiaConstants.REVIEWER2,
				// "payasia.invalid.hris.reviewer.2", Long.parseLong(key));
			}
		}
		if (leaveReviewerWorkflowRuleVal == 3
				|| leaveReviewerWorkflowRuleVal > 3) {
			if (StringUtils.isNotBlank(postLeavetransDTO
					.getReviewer3EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO
							.getReviewer3CompanyCode())) {
				Employee reviewer3 = employeeDAO
						.getEmployeeByEmpNumAndCompCode(
								postLeavetransDTO.getReviewer3EmployeeNumber(),
								postLeavetransDTO.getReviewer3CompanyCode());
				if (reviewer3 == null) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.REVIEWER3,
							"payasia.invalid.hris.reviewer.3",
							Long.parseLong(key));
				} else {
					postLeavetransDTO.setReviewer3Id(reviewer3.getEmployeeId());
				}
			} else {
				// setLeaveReviewersDataImportLogs(dataImportLogDTOs,
				// PayAsiaConstants.REVIEWER3,
				// "payasia.invalid.hris.reviewer.3", Long.parseLong(key));
			}
		}
		if (leaveReviewerWorkflowRuleVal == 2
				|| leaveReviewerWorkflowRuleVal > 2) {
			if (StringUtils.isNotBlank(postLeavetransDTO
					.getReviewer2EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO
							.getReviewer2CompanyCode())) {
				if (postLeavetransDTO.getEmployeeNumber().equalsIgnoreCase(
						postLeavetransDTO.getReviewer2EmployeeNumber())) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.REVIEWER2,
							"payasia.leave.reviewer.names.cannot.be.same",
							Long.parseLong(key));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO
						.getReviewer1EmployeeNumber())
						&& StringUtils.isNotBlank(postLeavetransDTO
								.getReviewer1CompanyCode())) {
					if (postLeavetransDTO.getReviewer1EmployeeNumber()
							.equalsIgnoreCase(
									postLeavetransDTO
											.getReviewer2EmployeeNumber())) {
						setLeaveReviewersDataImportLogs(dataImportLogDTOs,
								PayAsiaConstants.REVIEWER1,
								"payasia.leave.reviewer.names.cannot.be.same",
								Long.parseLong(key));
					}
				}
			}
		}
		if (leaveReviewerWorkflowRuleVal == 3
				|| leaveReviewerWorkflowRuleVal > 3) {
			if (StringUtils.isNotBlank(postLeavetransDTO
					.getReviewer3EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO
							.getReviewer3CompanyCode())) {
				if (postLeavetransDTO.getEmployeeNumber().equalsIgnoreCase(
						postLeavetransDTO.getReviewer3EmployeeNumber())) {
					setLeaveReviewersDataImportLogs(dataImportLogDTOs,
							PayAsiaConstants.REVIEWER3,
							"payasia.leave.reviewer.names.cannot.be.same",
							Long.parseLong(key));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO
						.getReviewer1EmployeeNumber())
						&& StringUtils.isNotBlank(postLeavetransDTO
								.getReviewer1CompanyCode())) {
					if (postLeavetransDTO.getReviewer1EmployeeNumber()
							.equalsIgnoreCase(
									postLeavetransDTO
											.getReviewer3EmployeeNumber())) {
						setLeaveReviewersDataImportLogs(dataImportLogDTOs,
								PayAsiaConstants.REVIEWER1,
								"payasia.leave.reviewer.names.cannot.be.same",
								Long.parseLong(key));
					}
				}
				if (StringUtils.isNotBlank(postLeavetransDTO
						.getReviewer2EmployeeNumber())
						&& StringUtils.isNotBlank(postLeavetransDTO
								.getReviewer2CompanyCode())) {
					if (postLeavetransDTO.getReviewer2EmployeeNumber()
							.equalsIgnoreCase(
									postLeavetransDTO
											.getReviewer3EmployeeNumber())) {
						setLeaveReviewersDataImportLogs(dataImportLogDTOs,
								PayAsiaConstants.REVIEWER2,
								"payasia.leave.reviewer.names.cannot.be.same",
								Long.parseLong(key));
					}
				}
			}
		}
	}

	private Long getValidLeaveScheme(Long companyId,
			HashMap<String, List<String>> leaveSchemeLeaveTypeListMap,
			LeaveReviewersDTO postLeavetransDTO,
			List<EmployeeLeaveScheme> empLeaveSchemeListVO,
			Long validEmpleaveSchemeId) {
		for (EmployeeLeaveScheme empLeaveScheme : empLeaveSchemeListVO) {
			if (empLeaveScheme.getLeaveScheme().getSchemeName()
					.equalsIgnoreCase(postLeavetransDTO.getLeaveScheme())) {
				validEmpleaveSchemeId = empLeaveScheme
						.getEmployeeLeaveSchemeId();
				if (!leaveSchemeLeaveTypeListMap.containsKey(empLeaveScheme
						.getLeaveScheme().getSchemeName().toUpperCase())) {
					List<LeaveTypeMaster> leaveTypeMasterList = leaveTypeMasterDAO
							.findByConditionAndVisibility(empLeaveScheme
									.getLeaveScheme().getLeaveSchemeId(),
									companyId);
					List<String> leaveTypeList = new ArrayList<String>();
					leaveTypeList.add(PayAsiaConstants.LEAVE_TYPE_SEARCH_ALL
							.toUpperCase());
					for (LeaveTypeMaster leaveTypeMaster : leaveTypeMasterList) {
						leaveTypeList.add(leaveTypeMaster.getLeaveTypeName()
								.toUpperCase());
					}
					leaveSchemeLeaveTypeListMap.put(empLeaveScheme
							.getLeaveScheme().getSchemeName().toUpperCase(),
							leaveTypeList);
				}

			}

		}
		return validEmpleaveSchemeId;
	}

	public void setLeaveReviewersDataImportLogs(
			List<DataImportLogDTO> dataImportLogDTOs, String key,
			String remarks, Long rowNumber) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setColName(key);
		dataImportLogDTO.setRemarks(remarks);
		dataImportLogDTO.setRowNumber(rowNumber + 1);
		dataImportLogDTOs.add(dataImportLogDTO);
	}

}
