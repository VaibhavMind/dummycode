package com.payasia.logic.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.LeaveTypeDTO;
import com.payasia.common.dto.LeaveTypeMasterConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LeaveTypeForm;
import com.payasia.common.form.LeaveTypeResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveSchemeTypeWorkflowDAO;
import com.payasia.dao.LeaveSchemeWorkflowDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSchemeWorkflow;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.LeaveTypeLogic;

@Component
public class LeaveTypeLogicImpl implements LeaveTypeLogic {
	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	LeaveSchemeDAO leaveSchemeDAO;

	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;

	@Resource
	LeaveSchemeWorkflowDAO leaveSchemeWorkflowDAO;

	@Resource
	LeaveSchemeTypeWorkflowDAO leaveSchemeTypeWorkflowDAO;
	@Resource
	EmployeeLeaveReviewerDAO employeeleaveReviewerDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	EmployeeDAO employeeDAO;

	public @ResponseBody @Override LeaveTypeResponse getLeaveTypeFormList(
			String searchCriteria, String keyword, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {
		List<LeaveTypeMaster> leaveTypeMasters;

		LeaveTypeMasterConditionDTO conditionDTO = new LeaveTypeMasterConditionDTO();
		conditionDTO.setCompanyId(companyId);

		if (PayAsiaConstants.LEAVE_TYPE_NAME.equals(searchCriteria)) {
			if (StringUtils.isNotBlank(keyword)) {
				conditionDTO.setName("%" + keyword.trim() + "%");
			}
		} else if (PayAsiaConstants.LEAVE_TYPE_CODE.equals(searchCriteria)) {
			if (StringUtils.isNotBlank(keyword)) {
				conditionDTO.setCode("%" + keyword.trim() + "%");
			}
		} else if (PayAsiaConstants.LEAVE_TYPE_ACCOUNT_CODE
				.equals(searchCriteria)) {
			if (StringUtils.isNotBlank(keyword)) {
				conditionDTO.setAccountCode("%" + keyword.trim() + "%");
			}
		} else if (PayAsiaConstants.LEAVE_TYPE_VISIBLEORHIDDEN
				.equals(searchCriteria)) {
			if (StringUtils.isNotBlank(keyword)) {
				if ("VISIBLE".equals(keyword.toUpperCase()))
					conditionDTO.setVisibleOrHidden("visible");
				else
					conditionDTO.setVisibleOrHidden("hidden");
			}

		}
		leaveTypeMasters = leaveTypeMasterDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO);
		List<LeaveTypeForm> leaveTypeFormList = new ArrayList<LeaveTypeForm>();
		if (leaveTypeMasters != null) {
			for (LeaveTypeMaster leaveTypeMaster : leaveTypeMasters) {
				LeaveTypeForm leaveTypeForm = new LeaveTypeForm();
				/* ID ENCRYPT*/
				leaveTypeForm.setLeaveTypeId(FormatPreserveCryptoUtil.encrypt(leaveTypeMaster.getLeaveTypeId()));
				String leaveTypeName = "<span class='jqGridColumnHighlight'>";
				leaveTypeName += leaveTypeMaster.getLeaveTypeName();
				leaveTypeName += "</span>";
				leaveTypeForm.setName(leaveTypeName);
				leaveTypeForm.setCode(leaveTypeMaster.getCode());

				if (leaveTypeMaster.getFrontEndViewMode() != null) {
					leaveTypeForm.setFrontEndViewModeId(leaveTypeMaster
							.getFrontEndViewMode().getAppCodeID());
				}
				if (leaveTypeMaster.getBackEndViewMode() != null) {
					leaveTypeForm.setBackEndViewModeId(leaveTypeMaster
							.getBackEndViewMode().getAppCodeID());
				}

				leaveTypeForm.setFrontEndAppMode(leaveTypeMaster
						.isFrontEndApplicationMode());
				leaveTypeForm.setBackEndAppMode(leaveTypeMaster
						.isBackEndApplicationMode());
				leaveTypeForm.setAccountCode(leaveTypeMaster.getAccountCode());
				if (leaveTypeMaster.getVisibility() == true) {
					leaveTypeForm.setStatus("Yes");
				}
				if (leaveTypeMaster.getVisibility() == false) {
					leaveTypeForm.setStatus("No");
				}

				leaveTypeForm
						.setInstruction(leaveTypeMaster.getLeaveTypeDesc());
				String leaveShemeCount = "<span class='Text'><h2>"
						+ String.valueOf(leaveTypeMaster.getLeaveSchemeTypes()
								.size()) + "</h2></span>";
				leaveShemeCount += "<span class='Textsmall' style='padding-top: 5px;'>&nbsp;Schemes</span>";
				leaveShemeCount += "<br><br>	";
				leaveShemeCount += "<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'editLeaveSheme("
						+ FormatPreserveCryptoUtil.encrypt(leaveTypeMaster.getLeaveTypeId())
						+ ")'>[Edit]</a></span>";

				leaveTypeForm.setLeaveShemeCount(leaveShemeCount);

				leaveTypeFormList.add(leaveTypeForm);
			}
		}

		LeaveTypeResponse leaveTypeResponse = new LeaveTypeResponse();

		leaveTypeResponse.setRows(leaveTypeFormList);
		return leaveTypeResponse;
	}

	@Override
	public String updateLeaveType(LeaveTypeForm leaveTypeForm,
			Long leaveTypeId, Long companyId) {
		/* ID DECRYPT */
		leaveTypeId= FormatPreserveCryptoUtil.decrypt(leaveTypeId);
		boolean status = true;
		/*LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO
				.findById(leaveTypeId);*/
		LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO
				.findLeaveTypeByCompId(leaveTypeId, companyId);
		if(leaveTypeMaster == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		leaveTypeMaster.setLeaveTypeName(leaveTypeForm.getName());
		leaveTypeMaster.setAccountCode(leaveTypeForm.getAccountCode());
		leaveTypeMaster.setVisibility(leaveTypeForm.getVisibleOrHidden());

		AppCodeMaster frontEndViewMode = appCodeMasterDAO
				.findById(leaveTypeForm.getFrontEndViewModeIdEdit());
		leaveTypeMaster.setFrontEndViewMode(frontEndViewMode);

		AppCodeMaster backEndViewMode = appCodeMasterDAO.findById(leaveTypeForm
				.getBackEndViewModeIdEdit());
		leaveTypeMaster.setBackEndViewMode(backEndViewMode);
		leaveTypeMaster.setFrontEndApplicationMode(leaveTypeForm
				.getFrontEndAppModeEdit());
		leaveTypeMaster.setBackEndApplicationMode(leaveTypeForm
				.getBackEndAppModeEdit());


		AppCodeMaster leaveTypeSingaporeanAppCode = null;
		if (leaveTypeForm.getLeaveTypeSingaporean().equalsIgnoreCase(
				PayAsiaConstants.APP_CODE_LEAVE_TYPE_CHILD_CARE_LEAVE)) {
			leaveTypeSingaporeanAppCode = appCodeMasterDAO
					.findByCategoryAndDesc(
							PayAsiaConstants.APP_CODE_LEAVE_TYPE_CATEGORY,
							PayAsiaConstants.APP_CODE_LEAVE_TYPE_CHILD_CARE_LEAVE);
		}
		if (leaveTypeForm.getLeaveTypeSingaporean().equalsIgnoreCase(
				PayAsiaConstants.APP_CODE_LEAVE_TYPE_EXTENDED_CHILD_CARE_LEAVE)) {
			leaveTypeSingaporeanAppCode = appCodeMasterDAO
					.findByCategoryAndDesc(
							PayAsiaConstants.APP_CODE_LEAVE_TYPE_CATEGORY,
							PayAsiaConstants.APP_CODE_LEAVE_TYPE_EXTENDED_CHILD_CARE_LEAVE);
		}
		leaveTypeMaster.setLeaveType(leaveTypeSingaporeanAppCode);

		status = checkExistInDB(leaveTypeId, leaveTypeForm.getName(),
				companyId, leaveTypeForm.getCode());

		if (!status) {
			leaveTypeMaster.setCode(leaveTypeForm.getCode());
			leaveTypeMasterDAO.update(leaveTypeMaster);
			return "notavailable";
		} else {
			return "available";
		}

	}

	@Override
	public void deleteLeaveType(Long leaveTypeId) {
		LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO
				.findById(leaveTypeId);
		leaveTypeMasterDAO.delete(leaveTypeMaster);
	}

	@Override
	public String saveLeaveType(LeaveTypeForm leaveTypeForm, Long companyId) {
		boolean status = true;
		LeaveTypeMaster leaveTypeMaster = new LeaveTypeMaster();
		leaveTypeMaster.setLeaveTypeName(leaveTypeForm.getName());
		leaveTypeMaster.setCode(leaveTypeForm.getCode());
		leaveTypeMaster.setAccountCode(leaveTypeForm.getAccountCode());
		if (leaveTypeForm.getVisibleOrHidden() == null) {
			leaveTypeMaster.setVisibility(true);
		} else {
			leaveTypeMaster.setVisibility(leaveTypeForm.getVisibleOrHidden());
		}

		leaveTypeMaster.setSortOrder(leaveTypeMasterDAO
				.getMaxLeaveTypeSortOrder(companyId) + 1);
		AppCodeMaster frontEndViewMode = appCodeMasterDAO
				.findById(leaveTypeForm.getFrontEndViewModeId());
		leaveTypeMaster.setFrontEndViewMode(frontEndViewMode);

		AppCodeMaster backEndViewMode = appCodeMasterDAO.findById(leaveTypeForm
				.getBackEndViewModeId());
		leaveTypeMaster.setBackEndViewMode(backEndViewMode);
		leaveTypeMaster.setFrontEndApplicationMode(leaveTypeForm
				.getFrontEndAppMode());
		leaveTypeMaster.setBackEndApplicationMode(leaveTypeForm
				.getBackEndAppMode());

		AppCodeMaster leaveTypeSingaporeanAppCode = null;
		if (leaveTypeForm.getLeaveTypeSingaporean().equalsIgnoreCase(
				PayAsiaConstants.APP_CODE_LEAVE_TYPE_CHILD_CARE_LEAVE)) {
			leaveTypeSingaporeanAppCode = appCodeMasterDAO
					.findByCategoryAndDesc(
							PayAsiaConstants.APP_CODE_LEAVE_TYPE_CATEGORY,
							PayAsiaConstants.APP_CODE_LEAVE_TYPE_CHILD_CARE_LEAVE);
		}
		if (leaveTypeForm.getLeaveTypeSingaporean().equalsIgnoreCase(
				PayAsiaConstants.APP_CODE_LEAVE_TYPE_EXTENDED_CHILD_CARE_LEAVE)) {
			leaveTypeSingaporeanAppCode = appCodeMasterDAO
					.findByCategoryAndDesc(
							PayAsiaConstants.APP_CODE_LEAVE_TYPE_CATEGORY,
							PayAsiaConstants.APP_CODE_LEAVE_TYPE_EXTENDED_CHILD_CARE_LEAVE);
		}
		leaveTypeMaster.setLeaveType(leaveTypeSingaporeanAppCode);

		Company company = companyDAO.findById(companyId);
		leaveTypeMaster.setCompany(company);

		status = checkExistInDB(null, leaveTypeForm.getName(), companyId,
				leaveTypeForm.getCode());

		if (!status) {
			leaveTypeMasterDAO.save(leaveTypeMaster);
			return "notavailable";
		} else {
			return "available";
		}

	}

	public boolean checkExistInDB(Long leaveTypeId, String leaveName,
			Long companyId, String leaveCode) {
		// LeaveTypeMaster leaveTypeMasterVO = leaveTypeMasterDAO
		// .findByLeaveIdAndName(leaveTypeId, leaveName, companyId, null);

		LeaveTypeMaster leaveTypeMasterCodeVO = leaveTypeMasterDAO
				.findByLeaveIdAndName(leaveTypeId, null, companyId, leaveCode);

		// if (leaveTypeMasterVO == null) {
		if (leaveTypeMasterCodeVO == null) {
			return false;
		}

		// }
		return true;
	}

	@Override
	public void addLeaveScheme(String[] leaveSchemeId, Long leaveTypeId,
			Long companyId) {
		leaveTypeId = FormatPreserveCryptoUtil.decrypt(leaveTypeId);
		LeaveSchemeType leaveSchemeType = new LeaveSchemeType();

		LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO
				.findLeaveTypeByCompId(leaveTypeId, companyId);
		if(leaveTypeMaster == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		for (int count = 0; count < leaveSchemeId.length; count++) {

			LeaveScheme leaveSchemeVO = leaveSchemeDAO.findByID(Long
					.parseLong(leaveSchemeId[count]));

			leaveSchemeType.setLeaveScheme(leaveSchemeVO);
			leaveSchemeType.setLeaveTypeMaster(leaveTypeMaster);
			leaveSchemeType.setVisibility(true);
			LeaveSchemeType leaveSchemeTypeVO = leaveSchemeTypeDAO
					.saveReturn(leaveSchemeType);

			if (leaveSchemeVO.getEmployeeLeaveSchemes().size() > 0) {

				Set<EmployeeLeaveScheme> employeeLeaveSchemes = leaveSchemeVO
						.getEmployeeLeaveSchemes();
				for (EmployeeLeaveScheme employeeLeaveScheme : employeeLeaveSchemes) {

					EmployeeLeaveSchemeType employeeLeaveSchemeType = new EmployeeLeaveSchemeType();
					employeeLeaveSchemeType
							.setEmployeeLeaveScheme(employeeLeaveScheme);
					employeeLeaveSchemeType
							.setLeaveSchemeType(leaveSchemeTypeVO);
					employeeLeaveSchemeType.setBalance(new BigDecimal(0));
					employeeLeaveSchemeType
							.setCarriedForward(new BigDecimal(0));
					employeeLeaveSchemeType.setCredited(new BigDecimal(0));
					employeeLeaveSchemeType.setEncashed(new BigDecimal(0));
					employeeLeaveSchemeType.setForfeited(new BigDecimal(0));
					employeeLeaveSchemeType.setPending(new BigDecimal(0));
					employeeLeaveSchemeType.setTaken(new BigDecimal(0));
					employeeLeaveSchemeType.setActive(true);

					EmployeeLeaveSchemeType persistObj = employeeLeaveSchemeTypeDAO
							.saveReturn(employeeLeaveSchemeType);

					Set<LeaveSchemeWorkflow> leaveSchemeTypeWorkflowSet = employeeLeaveScheme
							.getLeaveScheme().getLeaveSchemeWorkflows();
					saveEmployeeLeaveReviewerOnAssignLeaveType(
							leaveSchemeTypeWorkflowSet, employeeLeaveScheme
									.getEmployee().getEmployeeId(), persistObj,
							companyId);

				}

			}

			List<LeaveSchemeWorkflow> leaveSchemeWorkflows = leaveSchemeWorkflowDAO
					.findByCondition(leaveSchemeTypeVO.getLeaveScheme()
							.getLeaveSchemeId());

			for (LeaveSchemeWorkflow leaveSchemeWorkflow : leaveSchemeWorkflows) {
				LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflowVO = new LeaveSchemeTypeWorkflow();
				leaveSchemeTypeWorkflowVO.setLeaveSchemeType(leaveSchemeTypeVO);
				leaveSchemeTypeWorkflowVO
						.setWorkFlowRuleMaster(leaveSchemeWorkflow
								.getWorkFlowRuleMaster());

				leaveSchemeTypeWorkflowDAO.save(leaveSchemeTypeWorkflowVO);
			}

		}
	}

	private void saveEmployeeLeaveReviewerOnAssignLeaveType(
			Set<LeaveSchemeWorkflow> leaveSchemeWorkflowSet, Long employeeId,
			EmployeeLeaveSchemeType employeeLeaveSchemeType, Long companyId) {
		Integer workFlowRuleValue;
		Long employeeId1 = null;
		Long employeeId2 = null;
		Long employeeId3 = null;
		for (LeaveSchemeWorkflow leaveSchemeWorkflow : leaveSchemeWorkflowSet) {

			if (leaveSchemeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL
							.toUpperCase())) {
				workFlowRuleValue = Integer.parseInt(leaveSchemeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());
				Employee employee1Vo = employeeDAO.findByID(employeeId);
				for (int count = 1; count <= workFlowRuleValue; count++) {
					WorkFlowRuleMaster workFlowRuleListVO = workFlowRuleMasterDAO
							.findByRuleNameValue(
									PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER,
									String.valueOf(count));

					List<Object[]> tuplelist = employeeleaveReviewerDAO
							.getEmployeeReviewersCountByCondition(employeeId,
									employeeLeaveSchemeType
											.getEmployeeLeaveScheme()
											.getEmployeeLeaveSchemeId(),
									workFlowRuleListVO.getWorkFlowRuleId());

					List<Long> revCountList = new ArrayList<>();
					if ( !tuplelist.isEmpty()) {
						for (Object[] tuple : tuplelist) {
							revCountList.add((Long) tuple[1]);
						}
						Long maxRevCount = null;
						if ( !revCountList.isEmpty()) {
							maxRevCount = Collections.max(revCountList);
						}

						for (Object[] tuple : tuplelist) {
							if (maxRevCount != null
									&& maxRevCount.longValue() == ((Long) tuple[1])
											.longValue()) {
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
						empLeaveRev
								.setEmployeeLeaveScheme(employeeLeaveSchemeType
										.getEmployeeLeaveScheme());
						empLeaveRev
								.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
						empLeaveRev.setWorkFlowRuleMaster(workFlowRuleListVO);
						employeeleaveReviewerDAO.save(empLeaveRev);
					}

				}

			}

		}
	}

	@Override
	public void deleteLeaveScheme(Long leaveSchemeId, Long leaveSchemeTypeId,
			Long companyId) {
		LeaveSchemeType leaveSchemeType = leaveSchemeTypeDAO.findBySchemeType(
				leaveSchemeId, leaveSchemeTypeId);

		leaveSchemeTypeDAO.delete(leaveSchemeType);
	}

	@Override
	public LeaveTypeForm editLeaveType(Long leaveTypeId) {
		LeaveTypeForm leaveTypeFormResponse = new LeaveTypeForm();
		LeaveTypeMaster leaveTypeMasterVO = leaveTypeMasterDAO.findById(leaveTypeId);
		leaveTypeFormResponse.setName(leaveTypeMasterVO.getLeaveTypeName());
		leaveTypeFormResponse.setCode(leaveTypeMasterVO.getCode());
		leaveTypeFormResponse
				.setAccountCode(leaveTypeMasterVO.getAccountCode());
		leaveTypeFormResponse.setVisibleOrHidden(leaveTypeMasterVO
				.getVisibility());
		if (leaveTypeMasterVO.getFrontEndViewMode() != null) {
			leaveTypeFormResponse.setFrontEndViewModeId(leaveTypeMasterVO
					.getFrontEndViewMode().getAppCodeID());
		}
		if (leaveTypeMasterVO.getBackEndViewMode() != null) {
			leaveTypeFormResponse.setBackEndViewModeId(leaveTypeMasterVO
					.getBackEndViewMode().getAppCodeID());
		}

		leaveTypeFormResponse.setFrontEndAppMode(leaveTypeMasterVO
				.isFrontEndApplicationMode());
		leaveTypeFormResponse.setBackEndAppMode(leaveTypeMasterVO
				.isBackEndApplicationMode());

		if (leaveTypeMasterVO.getLeaveType() != null) {
			leaveTypeFormResponse.setLeaveTypeSingaporean(leaveTypeMasterVO
					.getLeaveType().getCodeDesc());
		} else {
			leaveTypeFormResponse.setLeaveTypeSingaporean("");
		}

		return leaveTypeFormResponse;
	}

	@Override
	public void updateLeaveTypeSortOrder(String[] sortOrder) {

		for (String sortOrd : sortOrder) {

			String[] sortValues = sortOrd.split("-");

			Long leaveTypeId = Long.parseLong(sortValues[0]);
			Integer pos = Integer.parseInt(sortValues[1]);
			LeaveTypeMaster leaveTypeMaster = leaveTypeMasterDAO
					.findById(leaveTypeId);
			leaveTypeMaster.setSortOrder(pos);
			leaveTypeMasterDAO.update(leaveTypeMaster);

		}

	}

	@Override
	public List<LeaveTypeDTO> getAllLeaveTypes(Long companyId) {
		List<LeaveTypeDTO> leaveTypeDTOs = new ArrayList<>();
		List<LeaveTypeMaster> leaveTypeMasterVOs = leaveTypeMasterDAO
				.getAllLeaveTypes(companyId);
		for (LeaveTypeMaster leaveTypeMaster : leaveTypeMasterVOs) {
			LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();
			leaveTypeDTO.setLeaveTypeId(leaveTypeMaster.getLeaveTypeId());
			leaveTypeDTO.setLeaveTypeName(leaveTypeMaster.getLeaveTypeName());
			leaveTypeDTOs.add(leaveTypeDTO);
		}
		return leaveTypeDTOs;
	}

	@Override
	public LeaveTypeForm getLeaveTypeAppcodeList() {

		LeaveTypeForm leaveTypeForm = new LeaveTypeForm();
		List<AppCodeMaster> appCodeFrontEndList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_TYPE_FRONT_END_VIEW_MODE);
		List<AppCodeMaster> appCodeBackEndList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_TYPE_BACK_END_VIEW_MODE);
		LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO();

		for (AppCodeMaster appCodeMaster : appCodeFrontEndList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_TYPE_VIEW_MODE_ON)) {

				leaveTypeDTO.setFrontEndViewModeOnId(appCodeMaster
						.getAppCodeID());
				leaveTypeDTO.setFrontEndViewModeOn(appCodeMaster.getCodeDesc());
			} else if (appCodeMaster.getCodeDesc().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_TYPE_VIEW_MODE_ON_WHEN_TRAN_EXIST)) {
				leaveTypeDTO.setFrontEndViewModeOnWhenTranId(appCodeMaster
						.getAppCodeID());
				leaveTypeDTO.setFrontEndViewModeOnWhenTran(appCodeMaster
						.getCodeDesc());
			} else if (appCodeMaster.getCodeDesc().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_TYPE_VIEW_MODE_OFF)) {
				leaveTypeDTO.setFrontEndViewModeOffId(appCodeMaster
						.getAppCodeID());
				leaveTypeDTO
						.setFrontEndViewModeOff(appCodeMaster.getCodeDesc());
			}
		}
		for (AppCodeMaster appCodeMaster : appCodeBackEndList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_TYPE_VIEW_MODE_ON)) {

				leaveTypeDTO.setBackEndViewModeOnId(appCodeMaster
						.getAppCodeID());
				leaveTypeDTO.setBackEndViewModeOn(appCodeMaster.getCodeDesc());
			} else if (appCodeMaster.getCodeDesc().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_TYPE_VIEW_MODE_ON_WHEN_TRAN_EXIST)) {
				leaveTypeDTO.setBackEndViewModeOnWhenTranId(appCodeMaster
						.getAppCodeID());
				leaveTypeDTO.setBackEndViewModeOnWhenTran(appCodeMaster
						.getCodeDesc());
			} else if (appCodeMaster.getCodeDesc().equalsIgnoreCase(
					PayAsiaConstants.LEAVE_TYPE_VIEW_MODE_OFF)) {
				leaveTypeDTO.setBackEndViewModeOffId(appCodeMaster
						.getAppCodeID());
				leaveTypeDTO.setBackEndViewModeOff(appCodeMaster.getCodeDesc());
			}
		}
		leaveTypeForm.setLeaveTypeDTO(leaveTypeDTO);
		return leaveTypeForm;
	}

	@Override
	public LeaveTypeForm editLeaveType(Long leaveTypeId, Long companyId) {
		LeaveTypeForm leaveTypeFormResponse = new LeaveTypeForm();
		LeaveTypeMaster leaveTypeMasterVO = leaveTypeMasterDAO.findLeaveTypeByCompId(leaveTypeId, companyId);
		leaveTypeFormResponse.setName(leaveTypeMasterVO.getLeaveTypeName());
		leaveTypeFormResponse.setCode(leaveTypeMasterVO.getCode());
		leaveTypeFormResponse
				.setAccountCode(leaveTypeMasterVO.getAccountCode());
		leaveTypeFormResponse.setVisibleOrHidden(leaveTypeMasterVO
				.getVisibility());
		if (leaveTypeMasterVO.getFrontEndViewMode() != null) {
			leaveTypeFormResponse.setFrontEndViewModeId(leaveTypeMasterVO
					.getFrontEndViewMode().getAppCodeID());
		}
		if (leaveTypeMasterVO.getBackEndViewMode() != null) {
			leaveTypeFormResponse.setBackEndViewModeId(leaveTypeMasterVO
					.getBackEndViewMode().getAppCodeID());
		}

		leaveTypeFormResponse.setFrontEndAppMode(leaveTypeMasterVO
				.isFrontEndApplicationMode());
		leaveTypeFormResponse.setBackEndAppMode(leaveTypeMasterVO
				.isBackEndApplicationMode());

		if (leaveTypeMasterVO.getLeaveType() != null) {
			leaveTypeFormResponse.setLeaveTypeSingaporean(leaveTypeMasterVO
					.getLeaveType().getCodeDesc());
		} else {
			leaveTypeFormResponse.setLeaveTypeSingaporean("");
		}

		return leaveTypeFormResponse;
	}
}
