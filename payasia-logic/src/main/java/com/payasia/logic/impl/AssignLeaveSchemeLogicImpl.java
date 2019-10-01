/**
 * @author vivekjain
 * 
 */
package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.AssignLeaveSchemeDTO;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AssignLeaveSchemeForm;
import com.payasia.common.form.AssignLeaveSchemeResponse;
import com.payasia.common.form.EmployeeLeaveDistributionForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeCalendarConfigDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveDistributionDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeCCLAndECCLEffectDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeHistoryDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveTypeMasterDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveDistribution;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeShortList;
import com.payasia.dao.bean.LeaveSchemeWorkflow;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.LeaveTypeMaster;
import com.payasia.dao.bean.LeaveTypeMaster_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.AssignLeaveSchemeLogic;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralLogic;

/**
 * The Class assignLeaveSchemeLogicImpl.
 */

@Component
public class AssignLeaveSchemeLogicImpl extends BaseLogic implements
		AssignLeaveSchemeLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AssignLeaveSchemeLogicImpl.class);

	@Resource
	EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	LeaveSchemeDAO leaveSchemeDAO;	
	@Resource
	EmployeeLeaveSchemeTypeCCLAndECCLEffectDAO employeeLeaveSchemeTypeCCLAndECCLEffectDAO;
	@Resource
	EmployeeLeaveDistributionDAO employeeLeaveDistributionDAO;
	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	EmployeeLeaveSchemeTypeHistoryDAO employeeLeaveSchemeTypeHistoryDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	FiltersInfoUtilsLogic filtersInfoUtilsLogic;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	EntityMasterDAO entityMasterDAO;
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	EmployeeCalendarConfigDAO employeeCalendarConfigDAO;
	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	LeaveTypeMasterDAO leaveTypeMasterDAO;

	@Override
	public AssignLeaveSchemeResponse searchAssignLeaveScheme(
			String searchCondition, String searchText, String fromDate,
			String toDate, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		List<AssignLeaveSchemeForm> assignLeaveSchemeFormList = new ArrayList<AssignLeaveSchemeForm>();
		AssignLeaveSchemeDTO conditionDTO = new AssignLeaveSchemeDTO();
		if (searchCondition
				.equals(PayAsiaConstants.PAYDATA_COLLECTION_EMPLOYEE_NUM)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setEmployeeNumber("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition
				.equals(PayAsiaConstants.PAYDATA_COLLECTION_EMP_NAME)) {
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
				.equals(PayAsiaConstants.LEAVE_REVIEWER_LEAVE_SCHEME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLeaveSchemeName("%" + searchText + "%");
			}

		}

		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate,
					companyVO.getDateFormat()));

		}
		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate,
					companyVO.getDateFormat()));

		}

		List<EmployeeLeaveScheme> searchList = employeeLeaveSchemeDAO
				.findByCondition(conditionDTO, pageDTO, sortDTO, companyId);
		for (EmployeeLeaveScheme empLeaveSchemeVO : searchList) {
			AssignLeaveSchemeForm assignLeaveSchemeForm = new AssignLeaveSchemeForm();
			assignLeaveSchemeForm.setEmployeeNumber(empLeaveSchemeVO
					.getEmployee().getEmployeeNumber());
			String empName = "";
			empName += empLeaveSchemeVO.getEmployee().getFirstName() + " ";
			if (StringUtils.isNotBlank(empLeaveSchemeVO.getEmployee()
					.getLastName())) {
				empName += empLeaveSchemeVO.getEmployee().getLastName();
			}
			assignLeaveSchemeForm.setEmployeeName(empName);

			assignLeaveSchemeForm.setLeaveSchemeName(empLeaveSchemeVO
					.getLeaveScheme().getSchemeName());
			assignLeaveSchemeForm
					.setFromDate(DateUtils.timeStampToString(
							empLeaveSchemeVO.getStartDate(),
							companyVO.getDateFormat()));
			if (empLeaveSchemeVO.getEndDate() != null) {
				assignLeaveSchemeForm.setToDate(DateUtils.timeStampToString(
						empLeaveSchemeVO.getEndDate(),
						companyVO.getDateFormat()));
			}
			/* ID ENCRYPT*/
			assignLeaveSchemeForm.setEmployeeLeaveSchemeId(FormatPreserveCryptoUtil.encrypt(empLeaveSchemeVO
					.getEmployeeLeaveSchemeId()));
			assignLeaveSchemeFormList.add(assignLeaveSchemeForm);
		}
		int recordSize = (employeeLeaveSchemeDAO.getCountForCondition(
				conditionDTO, companyId)).intValue();
		AssignLeaveSchemeResponse response = new AssignLeaveSchemeResponse();
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
		response.setAssignLeaveSchemeList(assignLeaveSchemeFormList);
		response.setRecords(recordSize);
		return response;
	}

	@Override
	public AssignLeaveSchemeResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}
		int recordSize = employeeDAO.getCountForCondition(conditionDTO,
				companyId);

		List<Employee> employeeVOList = employeeDAO.findEmployeesByCondition(
				conditionDTO, pageDTO, sortDTO, companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		AssignLeaveSchemeResponse response = new AssignLeaveSchemeResponse();
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

	@Override
	public List<AssignLeaveSchemeForm> getLeaveSchemeList(Long companyId) {

		List<AssignLeaveSchemeForm> leaveSchemeList = new ArrayList<AssignLeaveSchemeForm>();
		Set<LeaveScheme> leaveSchemeListSet = new LinkedHashSet<>();
		List<LeaveSchemeType> leaveSchemeListVO = leaveSchemeTypeDAO
				.findByCompany(companyId);
		for (LeaveSchemeType leaveSchemeTypeVO : leaveSchemeListVO) {
			leaveSchemeListSet.add(leaveSchemeTypeVO.getLeaveScheme());
		}

		for (LeaveScheme leaveSchemeTypeVO : leaveSchemeListSet) {
			AssignLeaveSchemeForm assignLeaveSchemeForm = new AssignLeaveSchemeForm();
			assignLeaveSchemeForm.setLeaveSchemeId(leaveSchemeTypeVO
					.getLeaveSchemeId());
			try {
				assignLeaveSchemeForm.setLeaveSchemeName(URLEncoder.encode(
						leaveSchemeTypeVO.getSchemeName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			leaveSchemeList.add(assignLeaveSchemeForm);

		}

		return leaveSchemeList;
	}

	@Override
	public String addEmpLeaveScheme(Long companyId,
			AssignLeaveSchemeForm assignLeaveSchemeForm) {
		boolean status = true;
		int count = 0;

		Company companyVO = companyDAO.findById(companyId);

		Employee employeeVO = employeeDAO.findById(assignLeaveSchemeForm
				.getEmployeeId());
		if (employeeVO.getEmployeeCalendarConfigs().size() == 0) {
			count++;
		}
		if (employeeVO.getEmployeeHolidayCalendars().size() == 0) {
			count++;

		}
		if (count > 0) {
			return "calNotDefinedErr";
		}
		EmployeeLeaveScheme employeeLeaveScheme = new EmployeeLeaveScheme();
		employeeLeaveScheme.setEmployee(employeeVO);

		//LeaveScheme leaveSchemeVO = leaveSchemeDAO.findByID(assignLeaveSchemeForm.getLeaveSchemeId());
		LeaveScheme leaveSchemeVO = leaveSchemeDAO.findSchemeByCompanyID(assignLeaveSchemeForm.getLeaveSchemeId(), companyId);
		if(leaveSchemeVO == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		employeeLeaveScheme.setLeaveScheme(leaveSchemeVO);
		employeeLeaveScheme
				.setStartDate(DateUtils.stringToTimestamp(
						assignLeaveSchemeForm.getFromDate(),
						companyVO.getDateFormat()));
		if (StringUtils.isNotBlank(assignLeaveSchemeForm.getToDate())) {
			employeeLeaveScheme.setEndDate(DateUtils.stringToTimestamp(
					assignLeaveSchemeForm.getToDate(),
					companyVO.getDateFormat()));
		}
		EmployeeLeaveScheme employeeLeaveSchemeVO = employeeLeaveSchemeDAO
				.findByEmpIdAndEndDate(assignLeaveSchemeForm.getEmployeeId());
		if (employeeLeaveSchemeVO != null) {
			if (employeeLeaveSchemeVO.getEndDate() == null) {
				return "enddate";
			}
		}

		EmployeeLeaveScheme empRecordByFromDateVO = employeeLeaveSchemeDAO
				.checkEmpLeaveSchemeByDate(null,
						assignLeaveSchemeForm.getEmployeeId(),
						assignLeaveSchemeForm.getFromDate(),
						companyVO.getDateFormat());
		if (empRecordByFromDateVO != null) {
			status = false;
		}
		if (StringUtils.isNotBlank(assignLeaveSchemeForm.getToDate())) {
			EmployeeLeaveScheme empRecordByToDateVO = employeeLeaveSchemeDAO
					.checkEmpLeaveSchemeByDate(null,
							assignLeaveSchemeForm.getEmployeeId(),
							assignLeaveSchemeForm.getToDate(),
							companyVO.getDateFormat());
			if (empRecordByToDateVO != null) {
				status = false;
			}
		}
		if (status) {
			EmployeeLeaveScheme lastAssignedEmpLeaveSchemeVO = employeeLeaveSchemeDAO
					.getLastAssignedEmpLeaveScheme(employeeVO.getEmployeeId());
			EmployeeLeaveScheme employeeLeaveSchemePersist = employeeLeaveSchemeDAO
					.saveReturn(employeeLeaveScheme);

			saveEmpLeaveSchemeType(companyId, employeeLeaveSchemePersist,
					employeeVO, leaveSchemeVO);
			if (lastAssignedEmpLeaveSchemeVO != null) {
				Set<LeaveSchemeWorkflow> leaveSchemeWorkflowSet = employeeLeaveSchemePersist
						.getLeaveScheme().getLeaveSchemeWorkflows();
				List<EmployeeLeaveSchemeType> employeeLeaveSchemeTypeList = employeeLeaveSchemeTypeDAO
						.findAllByEmpLeaveSchemeId(employeeLeaveSchemePersist
								.getEmployeeLeaveSchemeId());
				for (EmployeeLeaveSchemeType employeeLeaveSchemeType : employeeLeaveSchemeTypeList) {
					saveEmployeeLeaveReviewerOnAssignLeaveScheme(
							leaveSchemeWorkflowSet, employeeVO.getEmployeeId(),
							lastAssignedEmpLeaveSchemeVO,
							employeeLeaveSchemePersist,
							employeeLeaveSchemeType, companyId);
				}
			}

			return "success";
		} else {
			return "overlap";
		}

	}

	private void saveEmpLeaveSchemeType(Long companyId,
			EmployeeLeaveScheme employeeLeaveScheme, Employee employeeVO,
			LeaveScheme leaveSchemeVO) {
		List<LeaveSchemeType> validSchemeTypes = new ArrayList<>();
		List<LeaveSchemeType> notValidSchemeTypes = new ArrayList<>();
		HashMap<Long, Tab> tabMap = new HashMap<>();
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());
		for (LeaveSchemeType leaveSchemeType : leaveSchemeVO
				.getLeaveSchemeTypes()) {
			Map<String, String> paramValueMap = new HashMap<String, String>();
			Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
			List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
			List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
			List<LeaveSchemeTypeShortList> leaveSchemeTypeShortList = new ArrayList<>(
					leaveSchemeType.getLeaveSchemeTypeShortLists());
			setFilterInfo(leaveSchemeTypeShortList, finalFilterList,
					tableNames, codeDescDTOs, tabMap);

			String query = filtersInfoUtilsLogic.createQuery(
					employeeVO.getEmployeeId(), formIds, finalFilterList,
					tableNames, codeDescDTOs, companyId, paramValueMap);

			List<BigInteger> resultList = employeeDAO
					.checkForEmployeeDocuments(query, paramValueMap,
							employeeVO.getEmployeeId(), null);

			if (!resultList.isEmpty()) {
				validSchemeTypes.add(leaveSchemeType);
			} else {
				notValidSchemeTypes.add(leaveSchemeType);
			}

		}

		for (LeaveSchemeType leaveSchemeType : validSchemeTypes) {
			EmployeeLeaveSchemeType employeeLeaveSchemeType = new EmployeeLeaveSchemeType();
			employeeLeaveSchemeType.setEmployeeLeaveScheme(employeeLeaveScheme);
			employeeLeaveSchemeType.setLeaveSchemeType(leaveSchemeType);
			employeeLeaveSchemeType.setBalance(new BigDecimal(0));
			employeeLeaveSchemeType.setCarriedForward(new BigDecimal(0));
			employeeLeaveSchemeType.setCredited(new BigDecimal(0));
			employeeLeaveSchemeType.setEncashed(new BigDecimal(0));
			employeeLeaveSchemeType.setForfeited(new BigDecimal(0));
			employeeLeaveSchemeType.setPending(new BigDecimal(0));
			employeeLeaveSchemeType.setTaken(new BigDecimal(0));

			EmployeeLeaveSchemeType persistObj = employeeLeaveSchemeTypeDAO
					.saveReturn(employeeLeaveSchemeType);

		}

		for (LeaveSchemeType leaveSchemeType : notValidSchemeTypes) {
			EmployeeLeaveSchemeType employeeLeaveSchemeType = new EmployeeLeaveSchemeType();
			employeeLeaveSchemeType.setEmployeeLeaveScheme(employeeLeaveScheme);
			employeeLeaveSchemeType.setLeaveSchemeType(leaveSchemeType);
			employeeLeaveSchemeType.setBalance(new BigDecimal(0));
			employeeLeaveSchemeType.setCarriedForward(new BigDecimal(0));
			employeeLeaveSchemeType.setCredited(new BigDecimal(0));
			employeeLeaveSchemeType.setEncashed(new BigDecimal(0));
			employeeLeaveSchemeType.setForfeited(new BigDecimal(0));
			employeeLeaveSchemeType.setPending(new BigDecimal(0));
			employeeLeaveSchemeType.setTaken(new BigDecimal(0));
			employeeLeaveSchemeType.setActive(false);

			employeeLeaveSchemeTypeDAO.save(employeeLeaveSchemeType);
		}
	}

	private void saveEmployeeLeaveReviewerOnAssignLeaveScheme(
			Set<LeaveSchemeWorkflow> leaveSchemeWorkflowSet, Long employeeId,
			EmployeeLeaveScheme employeeLeaveSchemeOld,
			EmployeeLeaveScheme employeeLeaveSchemeNew,
			EmployeeLeaveSchemeType employeeLeaveSchemeTypeNew, Long companyId) {
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

					List<Object[]> tuplelist = employeeLeaveReviewerDAO
							.getEmployeeReviewersCountByCondition(employeeId,
									employeeLeaveSchemeOld
											.getEmployeeLeaveSchemeId(),
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
								.setEmployeeLeaveScheme(employeeLeaveSchemeNew);
						empLeaveRev
								.setEmployeeLeaveSchemeType(employeeLeaveSchemeTypeNew);
						empLeaveRev.setWorkFlowRuleMaster(workFlowRuleListVO);
						employeeLeaveReviewerDAO.save(empLeaveRev);
					}

				}

			}

		}
	}

	private void setFilterInfo(
			List<LeaveSchemeTypeShortList> leaveSchemeTypeShortList,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			HashMap<Long, Tab> tabMap) {
		for (LeaveSchemeTypeShortList schemeTypeShortList : leaveSchemeTypeShortList) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (schemeTypeShortList.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(
						schemeTypeShortList.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic
						.getDynamicDictionaryInfo(
								schemeTypeShortList.getDataDictionary(),
								dataImportKeyValueDTO, tableNames,
								codeDescDTOs, tabMap);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (schemeTypeShortList.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(schemeTypeShortList
						.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (schemeTypeShortList.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(schemeTypeShortList
						.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(schemeTypeShortList
					.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(schemeTypeShortList
					.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(schemeTypeShortList
					.getEqualityOperator());
			generalFilterDTO.setFilterId(schemeTypeShortList.getShortListId());
			generalFilterDTO.setLogicalOperator(schemeTypeShortList
					.getLogicalOperator());
			generalFilterDTO.setValue(schemeTypeShortList.getValue());

			finalFilterList.add(generalFilterDTO);
		}
	}

	@Override
	public String editEmpLeaveScheme(Long companyId,
			AssignLeaveSchemeForm assignLeaveSchemeForm) {

		boolean status = true;
		Company companyVO = companyDAO.findById(companyId);

		EmployeeLeaveScheme empRecordByFromDateVO = employeeLeaveSchemeDAO
				.checkEmpLeaveSchemeByDate(
						assignLeaveSchemeForm.getEmployeeLeaveSchemeId(),
						assignLeaveSchemeForm.getEmployeeId(),
						assignLeaveSchemeForm.getFromDate(),
						companyVO.getDateFormat());
		if (empRecordByFromDateVO != null) {
			status = false;
		}
		if (StringUtils.isNotBlank(assignLeaveSchemeForm.getToDate())) {
			EmployeeLeaveScheme empRecordByToDateVO = employeeLeaveSchemeDAO
					.checkEmpLeaveSchemeByDate(
							assignLeaveSchemeForm.getEmployeeLeaveSchemeId(),
							assignLeaveSchemeForm.getEmployeeId(),
							assignLeaveSchemeForm.getToDate(),
							companyVO.getDateFormat());
			if (empRecordByToDateVO != null) {
				status = false;
			}
		}
		if (status) {
			/*EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO
					.findById(assignLeaveSchemeForm.getEmployeeLeaveSchemeId());*/

			EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO
					.findSchemeByCompanyId(assignLeaveSchemeForm.getEmployeeLeaveSchemeId(), companyId);

			Employee employeeVO = employeeDAO.findById(assignLeaveSchemeForm
					.getEmployeeId());
			employeeLeaveScheme.setEmployee(employeeVO);
			LeaveScheme leaveSchemeVO = leaveSchemeDAO
					.findByID(assignLeaveSchemeForm.getLeaveSchemeId());
			employeeLeaveScheme.setLeaveScheme(leaveSchemeVO);
			employeeLeaveScheme.setStartDate(DateUtils.stringToTimestamp(
					assignLeaveSchemeForm.getFromDate(),
					companyVO.getDateFormat()));
			if (StringUtils.isNotBlank(assignLeaveSchemeForm.getToDate())) {
				employeeLeaveScheme.setEndDate(DateUtils.stringToTimestamp(
						assignLeaveSchemeForm.getToDate(),
						companyVO.getDateFormat()));
			} else {
				employeeLeaveScheme.setEndDate(null);
			}
			employeeLeaveSchemeDAO.update(employeeLeaveScheme);
			return "payasia.assign.leave.scheme.updated.successfully";
		} else {
			return "payasia.assign.leave.scheme.leave.scheme.period.not.overlap";
		}

	}

	@Override
	public void deleteEmpLeaveScheme(Long empLeaveSchemeId) {
		EmployeeLeaveScheme employeeLeaveSchemeVO = employeeLeaveSchemeDAO
				.findById(empLeaveSchemeId);
		List<EmployeeLeaveSchemeType> employeeLeaveSchemeTypeList = employeeLeaveSchemeTypeDAO
				.findAllByEmpLeaveSchemeId(empLeaveSchemeId);
		for (EmployeeLeaveSchemeType employeeLeaveSchemeType : employeeLeaveSchemeTypeList) {
			employeeLeaveDistributionDAO
					.deleteByCondition(employeeLeaveSchemeType
							.getEmployeeLeaveSchemeTypeId());
		}
		employeeLeaveSchemeDAO.delete(employeeLeaveSchemeVO);

	}

	@Override
	public AssignLeaveSchemeForm getEmpLeaveSchemeForEdit(
			Long empLeaveSchemeId, Long companyId) {
		/*EmployeeLeaveScheme employeeLeaveSchemeVO = employeeLeaveSchemeDAO
				.findById(empLeaveSchemeId);*/
		EmployeeLeaveScheme employeeLeaveSchemeVO = employeeLeaveSchemeDAO.findSchemeByCompanyId(empLeaveSchemeId, companyId);
		Company companyVO = companyDAO.findById(companyId);
		AssignLeaveSchemeForm assignLeaveSchemeForm = new AssignLeaveSchemeForm();
		assignLeaveSchemeForm.setEmployeeId(employeeLeaveSchemeVO.getEmployee()
				.getEmployeeId());
		assignLeaveSchemeForm.setEmployeeNumber(employeeLeaveSchemeVO
				.getEmployee().getEmployeeNumber());
		String employeeName = "";
		employeeName += employeeLeaveSchemeVO.getEmployee().getFirstName()
				+ " ";
		if (StringUtils.isNotBlank(employeeLeaveSchemeVO.getEmployee()
				.getLastName())) {
			employeeName += employeeLeaveSchemeVO.getEmployee().getLastName();
		}

		assignLeaveSchemeForm.setEmployeeName(employeeName);
		if (employeeLeaveSchemeVO.getEndDate() != null) {
			assignLeaveSchemeForm.setToDate(DateUtils.timeStampToString(
					employeeLeaveSchemeVO.getEndDate(),
					companyVO.getDateFormat()));
		}

		assignLeaveSchemeForm
				.setFromDate(DateUtils.timeStampToString(
						employeeLeaveSchemeVO.getStartDate(),
						companyVO.getDateFormat()));
		assignLeaveSchemeForm.setLeaveSchemeId(employeeLeaveSchemeVO
				.getLeaveScheme().getLeaveSchemeId());
		assignLeaveSchemeForm.setLeaveSchemeName(employeeLeaveSchemeVO
				.getLeaveScheme().getSchemeName());
		assignLeaveSchemeForm.setEmployeeLeaveSchemeId(employeeLeaveSchemeVO
				.getEmployeeLeaveSchemeId());
		return assignLeaveSchemeForm;
	}

	@Override
	public AssignLeaveSchemeResponse viewEmployeeleaveDistribution(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long empLeaveSchemeId, Integer year) {

		if (sortDTO.getColumnName().equals("")) {
			sortDTO.setColumnName(SortConstants.ASSIGN_LEAVE_SCHEME_LEAVE_TYPE);
			sortDTO.setOrderType(PayAsiaConstants.ASC);
		}
		List<EmployeeLeaveDistributionForm> empLeaveDistributionFormList = new ArrayList<EmployeeLeaveDistributionForm>();

		List<EmployeeLeaveDistribution> empLeaveDistVOList = employeeLeaveDistributionDAO
				.findByCondition(pageDTO, sortDTO, empLeaveSchemeId, year);
		for (EmployeeLeaveDistribution empLeaveDistVO : empLeaveDistVOList) {
			EmployeeLeaveDistributionForm employeeLeaveDistributionForm = new EmployeeLeaveDistributionForm();
			Boolean status = true;
			if (empLeaveDistVO.getEmployeeLeaveSchemeType()
					.getEmployeeLeaveScheme().getEndDate() != null) {
				status = checkValidLeaveScheme(
						DateUtils.timeStampToString(empLeaveDistVO
								.getEmployeeLeaveSchemeType()
								.getEmployeeLeaveScheme().getStartDate()),
						DateUtils.timeStampToString(empLeaveDistVO
								.getEmployeeLeaveSchemeType()
								.getEmployeeLeaveScheme().getEndDate()));
			} else {
				status = checkValidLeaveScheme(
						DateUtils.timeStampToString(empLeaveDistVO
								.getEmployeeLeaveSchemeType()
								.getEmployeeLeaveScheme().getStartDate()), null);
			}

			if (status) {
				employeeLeaveDistributionForm.setEmpLeaveSchemeStatus("active");
			} else {
				employeeLeaveDistributionForm
						.setEmpLeaveSchemeStatus("notactive");
			}
			employeeLeaveDistributionForm
					.setEmployeeLeaveDistributionId(empLeaveDistVO
							.getEmployeeLeaveDistributionId());
			employeeLeaveDistributionForm
					.setEmployeeLeaveSchemeId(empLeaveDistVO
							.getEmployeeLeaveSchemeType()
							.getEmployeeLeaveScheme()
							.getEmployeeLeaveSchemeId());
			employeeLeaveDistributionForm
					.setEmployeeLeaveSchemeTypeId(empLeaveDistVO
							.getEmployeeLeaveSchemeType()
							.getEmployeeLeaveSchemeTypeId());
			employeeLeaveDistributionForm.setLeaveTypeName(empLeaveDistVO
					.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveTypeMaster().getLeaveTypeName());
			employeeLeaveDistributionForm.setLeaveTypeId(empLeaveDistVO
					.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveTypeMaster().getLeaveTypeId());
			employeeLeaveDistributionForm.setYear(empLeaveDistVO.getYear());
			employeeLeaveDistributionForm.setJan(empLeaveDistVO.getJan());
			employeeLeaveDistributionForm.setFeb(empLeaveDistVO.getFeb());
			employeeLeaveDistributionForm.setMar(empLeaveDistVO.getMar());
			employeeLeaveDistributionForm.setApr(empLeaveDistVO.getApr());
			employeeLeaveDistributionForm.setMay(empLeaveDistVO.getMay());
			employeeLeaveDistributionForm.setJune(empLeaveDistVO.getJun());
			employeeLeaveDistributionForm.setJuly(empLeaveDistVO.getJul());
			employeeLeaveDistributionForm.setAug(empLeaveDistVO.getAug());
			employeeLeaveDistributionForm.setSept(empLeaveDistVO.getSep());
			employeeLeaveDistributionForm.setOct(empLeaveDistVO.getOct());
			employeeLeaveDistributionForm.setNov(empLeaveDistVO.getNov());
			employeeLeaveDistributionForm.setDec(empLeaveDistVO.getDec());

			empLeaveDistributionFormList.add(employeeLeaveDistributionForm);
		}
		AssignLeaveSchemeResponse response = new AssignLeaveSchemeResponse();

		int recordSize = employeeLeaveDistributionDAO.getCountForCondition(
				null, null, empLeaveSchemeId, year);

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
		response.setRecords(recordSize);
		response.setEmpLeaveDistriList(empLeaveDistributionFormList);
		return response;
	}

	private Boolean checkValidLeaveScheme(String startDateStr, String endDateStr) {
		Boolean status = true;
		Date endDate = null;
		Date startDate = DateUtils.stringToDate(startDateStr);
		if (endDateStr != null) {
			endDate = DateUtils.stringToDate(endDateStr);
		}

		Date currentDate = DateUtils.stringToDate(DateUtils
				.timeStampToString(DateUtils.getCurrentTimestampWithTime()));

		if (currentDate.before(startDate)) {
			status = false;
		}
		if (endDateStr != null) {
			if (currentDate.after(endDate)) {
				status = false;
			}
		}

		return status;

	}

	@Override
	public String editEmpLeaveDistribution(Long companyId,
			EmployeeLeaveDistributionForm employeeLeaveDistributionForm) {
		EmployeeLeaveDistribution employeeLeaveDistributionVO = employeeLeaveDistributionDAO
				.findById(employeeLeaveDistributionForm
						.getEmployeeLeaveDistributionId());
		employeeLeaveDistributionVO.setJan(employeeLeaveDistributionForm
				.getJan());
		employeeLeaveDistributionVO.setFeb(employeeLeaveDistributionForm
				.getFeb());
		employeeLeaveDistributionVO.setMar(employeeLeaveDistributionForm
				.getMar());
		employeeLeaveDistributionVO.setApr(employeeLeaveDistributionForm
				.getApr());
		employeeLeaveDistributionVO.setMay(employeeLeaveDistributionForm
				.getMay());
		employeeLeaveDistributionVO.setJun(employeeLeaveDistributionForm
				.getJune());
		employeeLeaveDistributionVO.setJul(employeeLeaveDistributionForm
				.getJuly());
		employeeLeaveDistributionVO.setAug(employeeLeaveDistributionForm
				.getAug());
		employeeLeaveDistributionVO.setSep(employeeLeaveDistributionForm
				.getSept());
		employeeLeaveDistributionVO.setOct(employeeLeaveDistributionForm
				.getOct());
		employeeLeaveDistributionVO.setNov(employeeLeaveDistributionForm
				.getNov());
		employeeLeaveDistributionVO.setDec(employeeLeaveDistributionForm
				.getDec());
		employeeLeaveDistributionDAO.update(employeeLeaveDistributionVO);
		return null;
	}

	@Override
	public AssignLeaveSchemeForm importAssignLeaveScheme(
			AssignLeaveSchemeForm assignLeaveSchemeForm, Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		String fileName = assignLeaveSchemeForm.getFileUpload()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		List<HashMap<String, AssignLeaveSchemeDTO>> empLeaveSchemeList = null;
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			empLeaveSchemeList = ExcelUtils.getAssignLeaveSchemeFromXLS(
					assignLeaveSchemeForm.getFileUpload(),
					companyVO.getDateFormat());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			empLeaveSchemeList = ExcelUtils.getAssignLeaveSchemeFromXLSX(
					assignLeaveSchemeForm.getFileUpload(),
					companyVO.getDateFormat());
		}
		AssignLeaveSchemeForm assignLeaveSchForm = new AssignLeaveSchemeForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();
		if (empLeaveSchemeList != null) {
			validateImpotedData(empLeaveSchemeList, dataImportLogDTOs,
					companyId);
		}
		if (!dataImportLogDTOs.isEmpty()) {
			assignLeaveSchForm.setDataValid(false);
			assignLeaveSchForm.setDataImportLogDTOs(dataImportLogDTOs);
			return assignLeaveSchForm;
		}

		for (HashMap<String, AssignLeaveSchemeDTO> empLeaveScheme : empLeaveSchemeList) {
			Set<String> keySet = empLeaveScheme.keySet();
			EmployeeLeaveScheme employeeLeaveScheme = new EmployeeLeaveScheme();
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				AssignLeaveSchemeDTO assignLeaveSchemeDTO = empLeaveScheme
						.get(key);
				if (!key.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)
						&& !key.equalsIgnoreCase(PayAsiaConstants.LEAVE_SCHEME_NAME)) {
					Employee employeeVO = employeeDAO
							.findByNumber(
									assignLeaveSchemeDTO.getEmployeeNumber(),
									companyId);

					employeeLeaveScheme.setEmployee(employeeVO);

					LeaveScheme leaveScheme = leaveSchemeDAO
							.findByLeaveSchemeAndCompany(null,
									assignLeaveSchemeDTO.getLeaveSchemeName(),
									companyId);
					employeeLeaveScheme.setLeaveScheme(leaveScheme);
					employeeLeaveScheme.setStartDate(DateUtils
							.stringToTimestamp(
									assignLeaveSchemeDTO.getFromDateString(),
									companyVO.getDateFormat()));
					if (StringUtils.isNotBlank(assignLeaveSchemeDTO
							.getToDateString())) {
						employeeLeaveScheme.setEndDate(DateUtils
								.stringToTimestamp(
										assignLeaveSchemeDTO.getToDateString(),
										companyVO.getDateFormat()));
					}

					EmployeeLeaveScheme returnedleaveScheme = employeeLeaveSchemeDAO
							.saveReturn(employeeLeaveScheme);
					saveEmpLeaveSchemeType(companyId, returnedleaveScheme,
							employeeVO, leaveScheme);

				}

			}

		}
		assignLeaveSchForm.setDataValid(true);
		return assignLeaveSchForm;

	}

	/**
	 * Purpose: To Validate impoted employee leave scheme.
	 * 
	 * @param empLeaveSchemeList
	 *            the employee LeaveScheme List
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImpotedData(
			List<HashMap<String, AssignLeaveSchemeDTO>> empLeaveSchemeList,
			List<DataImportLogDTO> dataImportLogDTOs, Long companyId) {
		boolean status = true;
		for (HashMap<String, AssignLeaveSchemeDTO> empLeaveScheme : empLeaveSchemeList) {
			Set<String> keySet = empLeaveScheme.keySet();
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				AssignLeaveSchemeDTO assignLeaveSchemeDTO = empLeaveScheme
						.get(key);
				if (!key.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)
						&& !key.equalsIgnoreCase(PayAsiaConstants.LEAVE_SCHEME_NAME)) {
					Employee employeeVO = employeeDAO
							.findByNumber(
									assignLeaveSchemeDTO.getEmployeeNumber(),
									companyId);
					if (employeeVO == null) {
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO
								.setColName(PayAsiaConstants.CAMEL_CASE_EMPLOYEE_NUMBER);
						dataImportLogDTO
								.setRemarks("payasia.assign.leave.scheme.not.valid.emp.num.error");
						dataImportLogDTO.setRowNumber(Long.parseLong(key));
						dataImportLogDTOs.add(dataImportLogDTO);
					} else {
						int count = 0;
						if (employeeVO.getEmployeeCalendarConfigs().size() == 0) {
							count++;
						}
						if (employeeVO.getEmployeeHolidayCalendars().size() == 0) {
							count++;

						}
						if (count > 0) {
							DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
							dataImportLogDTO.setColName("");
							dataImportLogDTO
									.setRemarks("payasia.assign.leave.scheme.calendar.not.defined");
							dataImportLogDTO.setRowNumber(Long.parseLong(key));
							dataImportLogDTOs.add(dataImportLogDTO);
						}
					}
					LeaveScheme leaveScheme = leaveSchemeDAO
							.findByLeaveSchemeAndCompany(null,
									assignLeaveSchemeDTO.getLeaveSchemeName(),
									companyId);
					if (leaveScheme == null) {
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO
								.setColName(PayAsiaConstants.LEAVE_SCHEME_NAME);
						dataImportLogDTO
								.setRemarks("payasia.assign.leave.scheme.not.valid.leave.scheme.name.error");
						dataImportLogDTO.setRowNumber(Long.parseLong(key));
						dataImportLogDTOs.add(dataImportLogDTO);
					}
					if (!StringUtils.isNotBlank(assignLeaveSchemeDTO
							.getFromDateString())) {
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO
								.setColName(PayAsiaConstants.ASSIGN_LEAVE_SCHEME_START_DATE);
						dataImportLogDTO
								.setRemarks("payasia.assign.leave.scheme.from.date.can.not.empty");
						dataImportLogDTO.setRowNumber(Long.parseLong(key));
						dataImportLogDTOs.add(dataImportLogDTO);
					}
					if (employeeVO != null) {
						EmployeeLeaveScheme employeeLeaveSchemeVO = employeeLeaveSchemeDAO
								.findByEmpIdAndEndDate(employeeVO
										.getEmployeeId());
						if (employeeLeaveSchemeVO != null) {
							if (employeeLeaveSchemeVO.getEndDate() == null) {
								DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
								dataImportLogDTO
										.setColName(PayAsiaConstants.ASSIGN_LEAVE_SCHEME_END_DATE);
								dataImportLogDTO
										.setRemarks("payasia.assign.leave.scheme.add.end.date.to.previous.leave.scheme");
								dataImportLogDTO.setRowNumber(Long
										.parseLong(key));
								dataImportLogDTOs.add(dataImportLogDTO);
							}
						}
						if (employeeLeaveSchemeVO == null) {
							EmployeeLeaveScheme empRecordByFromDateVO = employeeLeaveSchemeDAO
									.checkEmpLeaveSchemeByDate(null, employeeVO
											.getEmployeeId(),
											assignLeaveSchemeDTO
													.getFromDateString(),
											employeeVO.getCompany()
													.getDateFormat());
							if (empRecordByFromDateVO != null) {
								status = false;
							}
							if (StringUtils.isNotBlank(assignLeaveSchemeDTO
									.getToDateString())) {
								EmployeeLeaveScheme empRecordByToDateVO = employeeLeaveSchemeDAO
										.checkEmpLeaveSchemeByDate(null,
												employeeVO.getEmployeeId(),
												assignLeaveSchemeDTO
														.getToDateString(),
												employeeVO.getCompany()
														.getDateFormat());
								if (empRecordByToDateVO != null) {
									status = false;
								}
							}
							if (!status) {
								DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
								dataImportLogDTO
										.setColName(PayAsiaConstants.ASSIGN_LEAVE_SCHEME_END_DATE);
								dataImportLogDTO
										.setRemarks("payasia.assign.leave.scheme.leave.scheme.period.not.overlap");
								dataImportLogDTO.setRowNumber(Long
										.parseLong(key));
								dataImportLogDTOs.add(dataImportLogDTO);
							}
						}

					}

				}

			}

		}
	}

	@Override
	public AssignLeaveSchemeForm importLeaveDistribution(
			AssignLeaveSchemeForm assignLeaveSchemeForm, Long companyId) {
		String fileName = assignLeaveSchemeForm.getFileUpload()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		LeaveBalanceSummaryForm leaveDistributionForm = new LeaveBalanceSummaryForm();
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			leaveDistributionForm = ExcelUtils
					.getPostLeaveTranFromXLS(assignLeaveSchemeForm
							.getFileUpload());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			leaveDistributionForm = ExcelUtils
					.getPostLeaveTranFromXLSX(assignLeaveSchemeForm
							.getFileUpload());
		}
		AssignLeaveSchemeForm assignLeaveSchForm = new AssignLeaveSchemeForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();
		HashMap<String, EmployeeLeaveDistributionForm> empLeaveDistributionMap = new HashMap<>();

		setEmpLeaveDistributionForm(leaveDistributionForm,
				empLeaveDistributionMap);
		if (leaveDistributionForm != null) {
			validateLeaveDistributeImpotedData(dataImportLogDTOs,
					empLeaveDistributionMap, companyId);
		}

		if (!dataImportLogDTOs.isEmpty()) {
			assignLeaveSchForm.setDataValid(false);
			assignLeaveSchForm.setDataImportLogDTOs(dataImportLogDTOs);
			return assignLeaveSchForm;
		}

		Set<String> keySet = empLeaveDistributionMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			EmployeeLeaveDistributionForm employeeLeaveDistributionForm = empLeaveDistributionMap
					.get(key);

			EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
					.findByEmpLeaveSchemeAndLeaveType(
							employeeLeaveDistributionForm
									.getEmployeeLeaveSchemeId(),
							employeeLeaveDistributionForm.getLeaveTypeName());
			boolean isExist = true;
			EmployeeLeaveDistribution employeeLeaveDistributionVO = employeeLeaveDistributionDAO
					.findByCondition(employeeLeaveSchemeType
							.getEmployeeLeaveSchemeTypeId(),
							employeeLeaveDistributionForm.getYear());
			if (employeeLeaveDistributionVO == null) {
				employeeLeaveDistributionVO = new EmployeeLeaveDistribution();
				isExist = false;
			}
			employeeLeaveDistributionVO.setYear(employeeLeaveDistributionForm
					.getYear());

			employeeLeaveDistributionVO
					.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
			employeeLeaveDistributionVO.setJan(employeeLeaveDistributionForm
					.getJan());
			employeeLeaveDistributionVO.setFeb(employeeLeaveDistributionForm
					.getFeb());
			employeeLeaveDistributionVO.setMar(employeeLeaveDistributionForm
					.getMar());
			employeeLeaveDistributionVO.setApr(employeeLeaveDistributionForm
					.getApr());
			employeeLeaveDistributionVO.setMay(employeeLeaveDistributionForm
					.getMay());
			employeeLeaveDistributionVO.setJun(employeeLeaveDistributionForm
					.getJune());
			employeeLeaveDistributionVO.setJul(employeeLeaveDistributionForm
					.getJuly());
			employeeLeaveDistributionVO.setAug(employeeLeaveDistributionForm
					.getAug());
			employeeLeaveDistributionVO.setSep(employeeLeaveDistributionForm
					.getSept());
			employeeLeaveDistributionVO.setOct(employeeLeaveDistributionForm
					.getOct());
			employeeLeaveDistributionVO.setNov(employeeLeaveDistributionForm
					.getNov());
			employeeLeaveDistributionVO.setDec(employeeLeaveDistributionForm
					.getDec());
			if (isExist) {
				employeeLeaveDistributionDAO
						.update(employeeLeaveDistributionVO);
			} else {
				employeeLeaveDistributionDAO.save(employeeLeaveDistributionVO);
			}
		}
		assignLeaveSchForm.setDataValid(true);
		return assignLeaveSchForm;
	}

	private BigDecimal checkBlankAndReturnValue(String value) {
		if (StringUtils.isNotBlank(value)) {
			return new BigDecimal(value);
		}
		return BigDecimal.ZERO;
	}

	public void setEmpLeaveDistributionForm(
			LeaveBalanceSummaryForm leaveBalSummExcelFieldForm,
			HashMap<String, EmployeeLeaveDistributionForm> empLeaveDistributionMap) {
		for (HashMap<String, String> map : leaveBalSummExcelFieldForm
				.getImportedData()) {
			EmployeeLeaveDistributionForm empLeaveDistribution = new EmployeeLeaveDistributionForm();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get("rowNumber");
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				if (key != "rowNumber") {
					if (key.equals("Employee Number")) {
						empLeaveDistribution.setEmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME)) {
						empLeaveDistribution.setLeaveTypeName(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME)) {
						empLeaveDistribution.setLeaveSchemeName(value);
					}
					if (key.equals("Year") && StringUtils.isNotBlank(value)) {
						empLeaveDistribution.setYear(Integer.parseInt(value));
					}
					if (key.equals("Year") && StringUtils.isBlank(value)) {
						empLeaveDistribution.setYear(0);
					}
					if (key.equals("Jan")) {
						empLeaveDistribution
								.setJan(checkBlankAndReturnValue(value));
					}
					if (key.equals("Feb")) {
						empLeaveDistribution
								.setFeb(checkBlankAndReturnValue(value));
					}
					if (key.equals("Mar")) {
						empLeaveDistribution
								.setMar(checkBlankAndReturnValue(value));
					}
					if (key.equals("Apr")) {
						empLeaveDistribution
								.setApr(checkBlankAndReturnValue(value));
					}
					if (key.equals("May")) {
						empLeaveDistribution
								.setMay(checkBlankAndReturnValue(value));
					}
					if (key.equals("June")) {
						empLeaveDistribution
								.setJune(checkBlankAndReturnValue(value));
					}
					if (key.equals("July")) {
						empLeaveDistribution
								.setJuly(checkBlankAndReturnValue(value));
					}
					if (key.equals("Aug")) {
						empLeaveDistribution
								.setAug(checkBlankAndReturnValue(value));
					}
					if (key.equals("Sept")) {
						empLeaveDistribution
								.setSept(checkBlankAndReturnValue(value));
					}
					if (key.equals("Oct")) {
						empLeaveDistribution
								.setOct(checkBlankAndReturnValue(value));
					}
					if (key.equals("Nov")) {
						empLeaveDistribution
								.setNov(checkBlankAndReturnValue(value));
					}
					if (key.equals("Dec")) {
						empLeaveDistribution
								.setDec(checkBlankAndReturnValue(value));
					}
					empLeaveDistributionMap
							.put(rowNumber, empLeaveDistribution);
				}
			}
		}
	}

	public void setEmpLeaveDistributionImportLogs(
			List<DataImportLogDTO> dataImportLogDTOs, String key,
			String remarks, Long rowNumber) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setColName(key);
		dataImportLogDTO.setRemarks(remarks);
		dataImportLogDTO.setRowNumber(rowNumber + 1);
		dataImportLogDTOs.add(dataImportLogDTO);
	}

	/**
	 * Purpose: To Validate impoted employee leave scheme.
	 * 
	 * @param empLeaveSchemeList
	 *            the employee LeaveScheme List
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateLeaveDistributeImpotedData(
			List<DataImportLogDTO> dataImportLogDTOs,
			HashMap<String, EmployeeLeaveDistributionForm> empLeaveDistributionMap,
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

		List<String> leaveTypeNameList = new ArrayList<>();
		List<Tuple> leaveTypeListVO = leaveTypeMasterDAO
				.getLeaveTypeNameTupleList(companyId);
		for (Tuple leaveTypeTuple : leaveTypeListVO) {
			String leaveType = (String) leaveTypeTuple.get(
					getAlias(LeaveTypeMaster_.leaveTypeName), String.class);
			leaveTypeNameList.add(leaveType.toUpperCase());
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

		Set<String> keySet = empLeaveDistributionMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			EmployeeLeaveDistributionForm leaveDistributionForm = empLeaveDistributionMap
					.get(key);
			String rowNumber = key;
			if (key != "rowNumber") {
				if (StringUtils.isBlank(leaveDistributionForm
						.getEmployeeNumber())) {
					setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
							PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY,
							"payasia.empty", Long.parseLong(rowNumber));
				}
				if (StringUtils.isNotBlank(leaveDistributionForm
						.getEmployeeNumber())) {
					if (!employeeNumberMap.containsKey(leaveDistributionForm
							.getEmployeeNumber().toUpperCase())) {
						setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
								PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY,
								"payasia.invalid.employee.number",
								Long.parseLong(rowNumber));
					}
				}
				// Leave Scheme Name
				if (StringUtils.isBlank(leaveDistributionForm
						.getLeaveSchemeName())) {
					setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME,
							"payasia.empty", Long.parseLong(rowNumber));
					continue;
				}
				if (!leaveSchemeNameList.contains(leaveDistributionForm
						.getLeaveSchemeName().toUpperCase())) {
					setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME,
							"payasia.invalid.leave.scheme",
							Long.parseLong(rowNumber));
					continue;
				}
				List<EmployeeLeaveScheme> empLeaveSchemeListVO = employeeLeaveSchemeDAO
						.getActiveWithFutureLeaveScheme(
								employeeNumberMap.get(leaveDistributionForm
										.getEmployeeNumber().toUpperCase()),
								DateUtils.timeStampToString(
										DateUtils.getCurrentTimestampWithTime(),
										companyVO.getDateFormat()), companyVO
										.getDateFormat());
				if (empLeaveSchemeListVO == null) {
					setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME,
							"payasia.leave.reviewer.no.leave.scheme.assign",
							Long.parseLong(rowNumber));
					continue;
				}
				Long validEmpleaveSchemeId = null;
				validEmpleaveSchemeId = getValidLeaveScheme(companyId,
						leaveSchemeLeaveTypeListMap, leaveDistributionForm,
						empLeaveSchemeListVO, validEmpleaveSchemeId);
				if (validEmpleaveSchemeId == null) {
					setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_SCHEME_NAME,
							"payasia.leave.reviewer.no.leave.scheme.assign",
							Long.parseLong(rowNumber));
					continue;
				}
				leaveDistributionForm
						.setEmployeeLeaveSchemeId(validEmpleaveSchemeId);
				if (StringUtils.isBlank(leaveDistributionForm
						.getLeaveTypeName())) {
					setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
							PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME,
							"payasia.empty", Long.parseLong(rowNumber));
					continue;
				}
				if (StringUtils.isNotBlank(leaveDistributionForm
						.getLeaveTypeName())) {
					List<String> leaveTypeList = leaveSchemeLeaveTypeListMap
							.get(leaveDistributionForm.getLeaveSchemeName()
									.toUpperCase());
					if (!leaveTypeList.contains(leaveDistributionForm
							.getLeaveTypeName().toUpperCase())) {
						setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
								PayAsiaConstants.PAYASIA_LEAVE_TYPE_NAME,
								"payasia.invalid.leave.type",
								Long.parseLong(rowNumber));
						continue;
					}
				}
				if (leaveDistributionForm.getYear() == 0) {
					setEmpLeaveDistributionImportLogs(dataImportLogDTOs,
							"Year", "payasia.empty", Long.parseLong(rowNumber));
					continue;
				}
				empLeaveDistributionMap.put(key, leaveDistributionForm);
			}
		}
	}

	private Long getValidLeaveScheme(Long companyId,
			HashMap<String, List<String>> leaveSchemeLeaveTypeListMap,
			EmployeeLeaveDistributionForm employeeLeaveDistributionForm,
			List<EmployeeLeaveScheme> empLeaveSchemeListVO,
			Long validEmpleaveSchemeId) {
		for (EmployeeLeaveScheme empLeaveScheme : empLeaveSchemeListVO) {
			if (empLeaveScheme
					.getLeaveScheme()
					.getSchemeName()
					.equalsIgnoreCase(
							employeeLeaveDistributionForm.getLeaveSchemeName())) {
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

	@Override
	public List<AssignLeaveSchemeForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId) {
		List<AssignLeaveSchemeForm> assignLeaveSchemeFormList = new ArrayList<AssignLeaveSchemeForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(
				searchString.trim(), companyId, employeeShortListDTO);

		for (Employee employee : employeeNumberList) {
			AssignLeaveSchemeForm assignLeaveSchemeForm = new AssignLeaveSchemeForm();
			String empName = "";
			empName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empName += employee.getLastName();
			}
/*
			try {
				assignLeaveSchemeForm.setEmployeeName(URLEncoder.encode(
						empName, "UTF-8"));
				assignLeaveSchemeForm.setEmployeeNumber(URLEncoder.encode(
						employee.getEmployeeNumber(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
*/
			assignLeaveSchemeForm.setEmployeeName(empName);
			assignLeaveSchemeForm.setEmployeeNumber(employee.getEmployeeNumber());
			
			assignLeaveSchemeForm.setEmployeeId(employee.getEmployeeId());
			assignLeaveSchemeFormList.add(assignLeaveSchemeForm);

		}
		return assignLeaveSchemeFormList;
	}

	@Override
	public String getEmployeeName(Long loggedInEmployeeId,
			String employeeNumber, Long companyId) {
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber,
				companyId);
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(loggedInEmployeeId, companyId);
		if (employeeVO != null) {
			if ((employeeShortListDTO.getEmployeeShortList())
					&& (employeeShortListDTO.getShortListEmployeeIds()
							.contains(BigInteger.valueOf(employeeVO
									.getEmployeeId())))) {
				String employeeName = employeeVO.getFirstName();
				if (StringUtils.isNotBlank(employeeVO.getLastName())) {
					employeeName += " " + employeeVO.getLastName();
				}
				employeeName += "#" + employeeVO.getEmployeeId();
				return employeeName;
			} else {
				String employeeName = employeeVO.getFirstName();
				if (StringUtils.isNotBlank(employeeVO.getLastName())) {
					employeeName += " " + employeeVO.getLastName();
				}
				employeeName += "#" + employeeVO.getEmployeeId();
				return employeeName;
			}
		} else {
			return "";
		}
	}

	@Override
	public void deleteEmpLeaveScheme(Long empLeaveSchemeId, Long companyId) {
		EmployeeLeaveScheme employeeLeaveSchemeVO = employeeLeaveSchemeDAO
				.findSchemeByCompanyId(empLeaveSchemeId, companyId);
		List<EmployeeLeaveSchemeType> employeeLeaveSchemeTypeList = employeeLeaveSchemeTypeDAO
				.findAllByEmpLeaveSchemeId(empLeaveSchemeId);
		for (EmployeeLeaveSchemeType employeeLeaveSchemeType : employeeLeaveSchemeTypeList) {
			
		String leaveType =  employeeLeaveSchemeType.getLeaveSchemeType()!=null && employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster()!=null
				&& employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getLeaveType() !=null ? employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getLeaveType().getCodeDesc() : "";
			
			if (PayAsiaConstants.APP_CODE_LEAVE_TYPE_EXTENDED_CHILD_CARE_LEAVE.equalsIgnoreCase(leaveType)||
				PayAsiaConstants.APP_CODE_LEAVE_TYPE_CHILD_CARE_LEAVE.equalsIgnoreCase(leaveType))
			{
				employeeLeaveSchemeTypeCCLAndECCLEffectDAO.deleteByCondition(employeeLeaveSchemeType
						.getEmployeeLeaveSchemeTypeId(),companyId);
			}
			
			employeeLeaveDistributionDAO
					.deleteByCondition(employeeLeaveSchemeType
							.getEmployeeLeaveSchemeTypeId());
		}
		employeeLeaveSchemeDAO.delete(employeeLeaveSchemeVO);
	}
}
