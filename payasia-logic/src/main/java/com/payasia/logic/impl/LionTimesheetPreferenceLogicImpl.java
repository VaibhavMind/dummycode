package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.LionTimesheetPreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.LionTimesheetPreferenceDAO;
import com.payasia.dao.TimesheetApplicationWorkflowDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;
import com.payasia.dao.bean.LionTimesheetPreference;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.logic.LionTimesheetPreferenceLogic;

@Component
public class LionTimesheetPreferenceLogicImpl implements
		LionTimesheetPreferenceLogic {

	@Resource
	LionTimesheetPreferenceDAO lionTimesheetPreferenceDAO;
	@Resource
	CompanyDAO companyDAO;

	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	TimesheetBatchDAO lundinTimesheetBatchDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	TimesheetBatchDAO timesheetBatchDAO;

	@Resource
	TimesheetStatusMasterDAO timesheetStatusMasterDAO;

	@Resource
	EmployeeTimesheetApplicationDAO employeeTimesheetApplicationDAO;

	@Resource
	LionEmployeeTimesheetApplicationDetailDAO lionEmployeeTimesheetApplicationDetailDAO;

	@Resource
	EmployeeTimesheetApplicationDAO lundinTimesheetDAO;

	@Resource
	EmployeeTimesheetReviewerDAO lundinEmpReviewerDAO;

	@Resource
	TimesheetApplicationWorkflowDAO lundinTimesheetWorkflowDAO;

	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetApplicationReviewerDAO;

	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;

	@Override
	public void saveLionTimesheetPreference(
			LionTimesheetPreferenceForm lionTimesheetPreferenceForm,
			Long companyId) {

		LionTimesheetPreference lionOTPreferenceVO = lionTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (lionOTPreferenceVO == null) {
			LionTimesheetPreference lionTimesheetPreference = new LionTimesheetPreference();

			Company company = companyDAO.findById(companyId);

			lionTimesheetPreference.setCompany(company);

			lionTimesheetPreference.setCutoffCycle(appCodeMasterDAO
					.findById(lionTimesheetPreferenceForm.getCutoffCycleId()));
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(lionTimesheetPreferenceForm.getLocationId());
			lionTimesheetPreference.setLocation(dataDictionary);

			Timestamp timestamp = DateUtils.stringToTimestamp(
					lionTimesheetPreferenceForm.getPeriodStart(),
					company.getDateFormat());

			lionTimesheetPreference.setPeriodStart(timestamp);

			lionTimesheetPreference
					.setUseSystemMailAsFromAddress(lionTimesheetPreferenceForm
							.getUseSystemMailAsFromAddress());

			lionTimesheetPreferenceDAO.save(lionTimesheetPreference);
		} else {
			Company company = companyDAO.findById(companyId);

			lionOTPreferenceVO.setCompany(company);

			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(lionTimesheetPreferenceForm.getLocationId());
			lionOTPreferenceVO.setLocation(dataDictionary);

			List<TimesheetBatch> lionTimesheetBatches = timesheetBatchDAO
					.getOTBacthesByCompanyId(companyId);

			if (lionTimesheetBatches.size() == 0) {
				Timestamp timestamp = DateUtils.stringToTimestamp(
						lionTimesheetPreferenceForm.getPeriodStart(),
						company.getDateFormat());
				lionOTPreferenceVO.setPeriodStart(timestamp);
			}

			lionOTPreferenceVO
					.setUseSystemMailAsFromAddress(lionTimesheetPreferenceForm
							.getUseSystemMailAsFromAddress());

			lionTimesheetPreferenceDAO.update(lionOTPreferenceVO);

		}
	}

	@Override
	public LionTimesheetPreferenceForm getLionTimesheetPreference(Long companyId) {
		LionTimesheetPreference lionOTPreference = lionTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		LionTimesheetPreferenceForm lionTimesheetPreferenceFormObj = new LionTimesheetPreferenceForm();
		if (lionOTPreference != null) {
			try {
				BeanUtils.copyProperties(lionTimesheetPreferenceFormObj,
						lionOTPreference);

				Timestamp timestamp = lionOTPreference.getPeriodStart();

				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"dd MMM YYYY");

				List<TimesheetBatch> lionTimesheetBatches = timesheetBatchDAO
						.getOTBacthesByCompanyId(companyId);

				if (lionTimesheetBatches.size() > 0) {
					lionTimesheetPreferenceFormObj.setOverridePeriodStart(true);
				} else {
					lionTimesheetPreferenceFormObj
							.setOverridePeriodStart(false);
				}

				lionTimesheetPreferenceFormObj.setPeriodStart(dateFormat
						.format(timestamp));

				if (lionOTPreference.getCutoffCycle() != null) {
					lionTimesheetPreferenceFormObj
							.setCutoffCycleId(lionOTPreference.getCutoffCycle()
									.getAppCodeID());
				} else {
					lionTimesheetPreferenceFormObj.setCutoffCycleId(-1);
				}

				if (lionOTPreference.getLocation() != null) {
					lionTimesheetPreferenceFormObj
							.setLocationId((lionOTPreference.getLocation()
									.getDataDictionaryId()));
				} else {
					lionTimesheetPreferenceFormObj.setLocationId(-1);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lionTimesheetPreferenceFormObj;

	}

	@Override
	public List<LundinOTBatchDTO> getOTBacthesByCompanyId(long companyId,
			Long employeeId) {
		List<TimesheetBatch> allLionTBatches = new ArrayList<TimesheetBatch>();
		List<String> otStatusList = new ArrayList<String>();
		otStatusList.add("Rejected");
		otStatusList.add("Withdrawn");

		allLionTBatches = lundinTimesheetBatchDAO.getLionHKOTBacthes(companyId,
				employeeId, otStatusList);

		List<TimesheetBatch> lionOTBatchList = new ArrayList<TimesheetBatch>();
		if (allLionTBatches.size() != 0) {
			lionOTBatchList = getValidTimesheetBatches(allLionTBatches,
					companyId, employeeId);
		}

		List<LundinOTBatchDTO> lundinOTBatchDTOList = new ArrayList<LundinOTBatchDTO>();
		for (TimesheetBatch lundinOTBatch : lionOTBatchList) {
			if (lundinOTBatch == null) {
				continue;
			}
			LundinOTBatchDTO lundinOtBatchDto = new LundinOTBatchDTO();
			lundinOtBatchDto.setStartDate(lundinOTBatch.getStartDate());
			lundinOtBatchDto.setEndDate(lundinOTBatch.getEndDate());
			lundinOtBatchDto.setOtBatchDesc(lundinOTBatch
					.getTimesheetBatchDesc());
			lundinOtBatchDto.setOtBatchId(lundinOTBatch.getTimesheetBatchId());

			lundinOTBatchDTOList.add(lundinOtBatchDto);
		}

		return lundinOTBatchDTOList;
	}

	@Override
	public void createOTBatches(
			LionTimesheetPreference lionTimesheetPreference, int yearOfBatch,
			long companyId) {

		TimesheetBatch oTBatchResult = lundinTimesheetBatchDAO
				.findLatestBatchLion(companyId);

		int numberOfBatches = 0;
		int iterationNo;
		int incrementalDays;

		Timestamp periodStart = lionTimesheetPreference.getPeriodStart();
		String codeDesc = lionTimesheetPreference.getCutoffCycle()
				.getCodeDesc();

		Map<String, Integer> iterationIncrementalMap = new HashMap<String, Integer>();
		iterationIncrementalMap = getNumberofBatches(codeDesc);

		iterationNo = iterationIncrementalMap.get("iterationNo");
		incrementalDays = iterationIncrementalMap.get("incrementalDays");

		Timestamp startDate = new Timestamp((new Date().getTime()));
		Timestamp endDate = new Timestamp((new Date().getTime()));
		String description;
		String startDesc, endDesc;

		// List<TimesheetBatch> timesheetBatches = new
		// ArrayList<TimesheetBatch>();

		if (oTBatchResult == null) {
			// create batch on the basis of period start date
			startDate = periodStart;

		} else {

			Date currentDate = new Date();

			Calendar currentDateCalender = Calendar.getInstance();
			Calendar endDateCalender = Calendar.getInstance();

			currentDateCalender.setTime(currentDate);
			currentDateCalender.set(Calendar.MILLISECOND, 0);
			currentDateCalender.set(Calendar.SECOND, 0);
			currentDateCalender.set(Calendar.MINUTE, 0);
			currentDateCalender.set(Calendar.HOUR_OF_DAY, 0);

			endDateCalender.setTime(new Date(oTBatchResult.getStartDate()
					.getTime()));
			endDateCalender.set(Calendar.MILLISECOND, 0);
			endDateCalender.set(Calendar.SECOND, 0);
			endDateCalender.set(Calendar.MINUTE, 0);
			endDateCalender.set(Calendar.HOUR_OF_DAY, 0);

			if ((endDateCalender.getTime()).compareTo(currentDateCalender
					.getTime()) >= 0) {
				iterationNo = 0;
			}

			// create batches on the basis of last batch
			endDate = oTBatchResult.getEndDate();
			startDate = addDays(new Date(endDate.getTime()), 1);

		}

		// Date periodStartDate = new Date(periodStart.getTime());

		SimpleDateFormat smp = new SimpleDateFormat("dd MMM yyyy");

		for (int i = 0; i < iterationNo; i++) {

			TimesheetBatch timesheetBatch = new TimesheetBatch();

			timesheetBatch.setCompany(companyDAO.findById(companyId));
			timesheetBatch.setStartDate(startDate);

			if (incrementalDays == -1) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(startDate.getTime()));
				calendar.add(Calendar.MONTH, 1);

				endDate = new Timestamp(calendar.getTimeInMillis());
				endDate = addDays(new Date(endDate.getTime()), -1);
			} else {
				endDate = addDays(new Date(startDate.getTime()),
						incrementalDays);
			}
			timesheetBatch.setEndDate(endDate);
			startDesc = smp.format(new Date(startDate.getTime()));
			endDesc = smp.format(new Date(endDate.getTime()));

			description = startDesc + " - " + endDesc;

			timesheetBatch.setTimesheetBatchDesc(description);

			startDate = addDays(new Date(endDate.getTime()), 1);

			lundinTimesheetBatchDAO.save(timesheetBatch);
			// timesheetBatches.add(timesheetBatch);

		}

	}

	@Override
	public AddClaimFormResponse getAllTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText) {

		LundinTsheetConditionDTO conditionDto = new LundinTsheetConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.OT_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setBatch("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_REVIEWERS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setReviewers("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_STATUS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setStatus("%" + searchText.trim() + "%");
			}

		}

		int recordSize = lundinTimesheetDAO.getCountByConditionForEmployee(
				empId, fromDate, toDate, conditionDto);

		Boolean visibleToEmployee = true;
		List<EmployeeTimesheetApplication> otTimesheetList = lundinTimesheetDAO
				.findByConditionForEmployeeLion(pageDTO, sortDTO, empId,
						fromDate, toDate, visibleToEmployee, conditionDto);

		List<AddClaimForm> addOtFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication otApplication : otTimesheetList) {
			AddClaimForm addOtForm = new AddClaimForm();

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				StringBuilder claimTemplateEdit = new StringBuilder();
				claimTemplateEdit.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateEdit.append("<br>");
				/*ID ENCRYPT*/
				claimTemplateEdit
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editlionEmployeeApplication("
								+ FormatPreserveCryptoUtil.encrypt(otApplication.getTimesheetId())
								+ ");'>[Edit]</a></span>");

				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateEdit));
				StringBuilder claimWithDrawLink = new StringBuilder();
				claimWithDrawLink
						.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplication("
								+ otApplication.getTimesheetId()
								+ ");'>Delete</a>");

				addOtForm.setAction(String.valueOf(claimWithDrawLink));
			} else {
				StringBuilder claimTemplateView = new StringBuilder();
				claimTemplateView.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateView.append("<br>");
				String claimStatusMode = ",\"submitted\"";

				if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {

					claimStatusMode = ",\"Approved\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
						|| otApplication
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
					claimStatusMode = ",\"submitted\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
					claimStatusMode = ",\"Withdrawn\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED)) {
					claimStatusMode = ",\"Rejected\"";
				}
				/*ID ENCRYPT */
				claimTemplateView
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewlionEmployeeApplication("
								+FormatPreserveCryptoUtil.encrypt( otApplication.getTimesheetId())
								+ claimStatusMode + ");'>[View]</a></span>");
				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateView));
			}

			addOtForm.setCreateDate(DateUtils.timeStampToString(otApplication
					.getCreatedDate()));
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equals(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {

				if (otApplication.getCreatedBy() != null) {
					if (otApplication.getCreatedBy().equals(
							String.valueOf(empId))) {
						StringBuilder claimWithDrawLink = new StringBuilder();
						claimWithDrawLink
								.append("<a class='alink' href='#' onClick='javascipt:withDrawClaimApplication("
										+ otApplication.getTimesheetId()
										+ ");'>Withdraw</a>");

						addOtForm.setAction(String.valueOf(claimWithDrawLink));
					}
				}
			}
			addOtForm.setClaimApplicationId(otApplication.getTimesheetId());
			List<TimesheetApplicationReviewer> claimAppReviewers = new ArrayList<>(
					otApplication.getTimesheetApplicationReviewers());

			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					lundinEmpReviewerDAO.findByCondition(empId, otApplication
							.getCompany().getCompanyId()));

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}

			addOtFormList.add(addOtForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addOtFormList);

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
	public AddClaimFormResponse getSubmittedTimesheet(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String transactionType, Long companyId, String searchCondition,
			String searchText) {

		LundinTsheetConditionDTO conditionDto = new LundinTsheetConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.OT_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setBatch("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_REVIEWERS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setReviewers("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_STATUS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setStatus("%" + searchText.trim() + "%");
			}

		}

		int recordSize = lundinTimesheetDAO.getCountByConditionForEmployee(
				empId, fromDate, toDate, conditionDto);

		conditionDto.setStatus(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);

		Boolean visibleToEmployee = true;
		List<EmployeeTimesheetApplication> otTimesheetList = lundinTimesheetDAO
				.findByConditionForEmployeeLion(pageDTO, sortDTO, empId,
						fromDate, toDate, visibleToEmployee, conditionDto);

		List<AddClaimForm> addOtFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication otApplication : otTimesheetList) {
			AddClaimForm addOtForm = new AddClaimForm();

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				StringBuilder claimTemplateEdit = new StringBuilder();
				claimTemplateEdit.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateEdit.append("<br>");
				/*ID ENCRYPT */
				claimTemplateEdit
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editlionEmployeeApplication("
								+ FormatPreserveCryptoUtil.encrypt(otApplication.getTimesheetId())
								+ ");'>[Edit]</a></span>");

				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateEdit));
				StringBuilder claimWithDrawLink = new StringBuilder();
				claimWithDrawLink
						.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplication("
								+ otApplication.getTimesheetId()
								+ ");'>Delete</a>");

				addOtForm.setAction(String.valueOf(claimWithDrawLink));
			} else {
				StringBuilder claimTemplateView = new StringBuilder();
				claimTemplateView.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateView.append("<br>");
				String claimStatusMode = ",\"submitted\"";

				if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {

					claimStatusMode = ",\"Approved\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
						|| otApplication
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
					claimStatusMode = ",\"submitted\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
					claimStatusMode = ",\"Withdrawn\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED)) {
					claimStatusMode = ",\"Rejected\"";
				}
				/*ID ENCRYPT */
				claimTemplateView
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewlionEmployeeApplication("
								+FormatPreserveCryptoUtil.encrypt( otApplication.getTimesheetId())
								+ claimStatusMode + ");'>[View]</a></span>");
				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateView));
			}

			addOtForm.setCreateDate(DateUtils.timeStampToString(otApplication
					.getCreatedDate()));
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equals(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {

				if (otApplication.getCreatedBy() != null) {
					if (otApplication.getCreatedBy().equals(
							String.valueOf(empId))) {
						StringBuilder claimWithDrawLink = new StringBuilder();
						claimWithDrawLink
								.append("<a class='alink' href='#' onClick='javascipt:withDrawClaimApplication("
										+ otApplication.getTimesheetId()
										+ ");'>Withdraw</a>");

						addOtForm.setAction(String.valueOf(claimWithDrawLink));
					}
				}
			}
			addOtForm.setClaimApplicationId(otApplication.getTimesheetId());
			List<TimesheetApplicationReviewer> claimAppReviewers = new ArrayList<>(
					otApplication.getTimesheetApplicationReviewers());

			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					lundinEmpReviewerDAO.findByCondition(empId, otApplication
							.getCompany().getCompanyId()));

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}

			addOtFormList.add(addOtForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addOtFormList);

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
	public AddClaimFormResponse getPendingTimesheet(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String transactionType, Long companyId, String searchCondition,
			String searchText) {

		LundinTsheetConditionDTO conditionDto = new LundinTsheetConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.OT_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setBatch("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_REVIEWERS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setReviewers("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_STATUS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setStatus("%" + searchText.trim() + "%");
			}

		}

		int recordSize = lundinTimesheetDAO.getCountByConditionForEmployee(
				empId, fromDate, toDate, conditionDto);

		conditionDto.setStatus(PayAsiaConstants.CLAIM_STATUS_DRAFT);

		Boolean visibleToEmployee = true;
		List<EmployeeTimesheetApplication> otTimesheetList = lundinTimesheetDAO
				.findByConditionForEmployeeLion(pageDTO, sortDTO, empId,
						fromDate, toDate, visibleToEmployee, conditionDto);

		List<AddClaimForm> addOtFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication otApplication : otTimesheetList) {
			AddClaimForm addOtForm = new AddClaimForm();

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				StringBuilder claimTemplateEdit = new StringBuilder();
				claimTemplateEdit.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				/*ID ENCRYPT */
				claimTemplateEdit.append("<br>");
				claimTemplateEdit
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editlionEmployeeApplication("
								+ FormatPreserveCryptoUtil.encrypt(otApplication.getTimesheetId())
								+ ");'>[Edit]</a></span>");

				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateEdit));
				StringBuilder claimWithDrawLink = new StringBuilder();
				claimWithDrawLink
						.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplication("
								+ otApplication.getTimesheetId()
								+ ");'>Delete</a>");

				addOtForm.setAction(String.valueOf(claimWithDrawLink));
			} else {
				StringBuilder claimTemplateView = new StringBuilder();
				claimTemplateView.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateView.append("<br>");
				String claimStatusMode = ",\"submitted\"";

				if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {

					claimStatusMode = ",\"Approved\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
						|| otApplication
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
					claimStatusMode = ",\"submitted\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
					claimStatusMode = ",\"Withdrawn\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED)) {
					claimStatusMode = ",\"Rejected\"";
				}
				/*ID ENCRYPT */
				claimTemplateView
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewlionEmployeeApplication("
								+ FormatPreserveCryptoUtil.encrypt(otApplication.getTimesheetId())
								+ claimStatusMode + ");'>[View]</a></span>");
				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateView));
			}

			addOtForm.setCreateDate(DateUtils.timeStampToString(otApplication
					.getCreatedDate()));
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equals(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {

				if (otApplication.getCreatedBy() != null) {
					if (otApplication.getCreatedBy().equals(
							String.valueOf(empId))) {
						StringBuilder claimWithDrawLink = new StringBuilder();
						claimWithDrawLink
								.append("<a class='alink' href='#' onClick='javascipt:withDrawClaimApplication("
										+ otApplication.getTimesheetId()
										+ ");'>Withdraw</a>");

						addOtForm.setAction(String.valueOf(claimWithDrawLink));
					}
				}
			}
			addOtForm.setClaimApplicationId(otApplication.getTimesheetId());
			List<TimesheetApplicationReviewer> claimAppReviewers = new ArrayList<>(
					otApplication.getTimesheetApplicationReviewers());

			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					lundinEmpReviewerDAO.findByCondition(empId, otApplication
							.getCompany().getCompanyId()));

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}

			addOtFormList.add(addOtForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addOtFormList);

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
	public AddClaimFormResponse getApprovedTimesheet(String fromDate,
			String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath,
			String transactionType, Long companyId, String searchCondition,
			String searchText) {

		LundinTsheetConditionDTO conditionDto = new LundinTsheetConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.OT_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setBatch("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_REVIEWERS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setReviewers("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.OT_STATUS)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDto.setStatus("%" + searchText.trim() + "%");
			}

		}

		int recordSize = lundinTimesheetDAO.getCountByConditionForEmployee(
				empId, fromDate, toDate, conditionDto);

		conditionDto.setStatus(PayAsiaConstants.CLAIM_STATUS_COMPLETED);

		Boolean visibleToEmployee = true;
		List<EmployeeTimesheetApplication> otTimesheetList = lundinTimesheetDAO
				.findByConditionForEmployeeLion(pageDTO, sortDTO, empId,
						fromDate, toDate, visibleToEmployee, conditionDto);

		List<AddClaimForm> addOtFormList = new ArrayList<AddClaimForm>();
		for (EmployeeTimesheetApplication otApplication : otTimesheetList) {
			AddClaimForm addOtForm = new AddClaimForm();

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				StringBuilder claimTemplateEdit = new StringBuilder();
				claimTemplateEdit.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateEdit.append("<br>");
				/*ID ENCRYPT */
				claimTemplateEdit
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editlionEmployeeApplication("
								+FormatPreserveCryptoUtil.encrypt( otApplication.getTimesheetId())
								+ ");'>[Edit]</a></span>");

				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateEdit));
				StringBuilder claimWithDrawLink = new StringBuilder();
				claimWithDrawLink
						.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplication("
								+ otApplication.getTimesheetId()
								+ ");'>Delete</a>");

				addOtForm.setAction(String.valueOf(claimWithDrawLink));
			} else {
				StringBuilder claimTemplateView = new StringBuilder();
				claimTemplateView.append(otApplication.getTimesheetBatch()
						.getTimesheetBatchDesc());
				claimTemplateView.append("<br>");
				String claimStatusMode = ",\"submitted\"";

				if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {

					claimStatusMode = ",\"Approved\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
						|| otApplication
								.getTimesheetStatusMaster()
								.getTimesheetStatusName()
								.equalsIgnoreCase(
										PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
					claimStatusMode = ",\"submitted\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
					claimStatusMode = ",\"Withdrawn\"";
				} else if (otApplication.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED)) {
					claimStatusMode = ",\"Rejected\"";
				}
				/*ID ENCRYPT */
				claimTemplateView
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewlionEmployeeApplication("
								+FormatPreserveCryptoUtil.encrypt(  otApplication.getTimesheetId())
								+ claimStatusMode + ");'>[View]</a></span>");
				addOtForm.setClaimTemplateName(String
						.valueOf(claimTemplateView));
			}

			addOtForm.setCreateDate(DateUtils.timeStampToString(otApplication
					.getCreatedDate()));
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equals(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {

				if (otApplication.getCreatedBy() != null) {
					if (otApplication.getCreatedBy().equals(
							String.valueOf(empId))) {
						StringBuilder claimWithDrawLink = new StringBuilder();
						claimWithDrawLink
								.append("<a class='alink' href='#' onClick='javascipt:withDrawClaimApplication("
										+ otApplication.getTimesheetId()
										+ ");'>Withdraw</a>");

						addOtForm.setAction(String.valueOf(claimWithDrawLink));
					}
				}
			}
			addOtForm.setClaimApplicationId(otApplication.getTimesheetId());
			List<TimesheetApplicationReviewer> claimAppReviewers = new ArrayList<>(
					otApplication.getTimesheetApplicationReviewers());

			List<EmployeeTimesheetReviewer> employeeClaimReviewers = new ArrayList<>(
					lundinEmpReviewerDAO.findByCondition(empId, otApplication
							.getCompany().getCompanyId()));

			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				addOtForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| otApplication
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}
			if (otApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				addOtForm
						.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
				getClaimReviewer(addOtForm, pageContextPath, otApplication,
						empId);
			}

			addOtFormList.add(addOtForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addOtFormList);

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
	public LionTimesheetPreference getByCompanyId(long companyId)
			throws Exception {
		LionTimesheetPreference lionTimesheetPreference = lionTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		return lionTimesheetPreference;
	}

	private class EmployeeOTReviewerComp implements
			Comparator<EmployeeTimesheetReviewer> {
		public int compare(EmployeeTimesheetReviewer templateField,
				EmployeeTimesheetReviewer compWithTemplateField) {
			if (templateField.getEmployeeTimesheetReviewerId() > compWithTemplateField
					.getEmployeeTimesheetReviewerId()) {
				return 1;
			} else if (templateField.getEmployeeTimesheetReviewerId() < compWithTemplateField
					.getEmployeeTimesheetReviewerId()) {
				return -1;
			}
			return 0;

		}
	}

	/**
	 * Comparator Class for Ordering lundinWorkflow List
	 */
	private class IngersollOTTimesheetReviewerComp implements
			Comparator<TimesheetApplicationReviewer> {
		public int compare(TimesheetApplicationReviewer templateField,
				TimesheetApplicationReviewer compWithTemplateField) {
			if (templateField.getTimesheetReviewerId() > compWithTemplateField
					.getTimesheetReviewerId()) {
				return 1;
			} else if (templateField.getTimesheetReviewerId() < compWithTemplateField
					.getTimesheetReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	public void getPendingClaimReviewers(
			List<EmployeeTimesheetReviewer> empOtReviewers,
			AddClaimForm addOTForm) {
		Collections.sort(empOtReviewers, new EmployeeOTReviewerComp());
		for (EmployeeTimesheetReviewer employeeOtReviewer : empOtReviewers) {
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {

				addOTForm.setClaimReviewer1(getEmployeeName(employeeOtReviewer
						.getEmployeeReviewer()));

			}
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {

				addOTForm.setClaimReviewer2(getEmployeeName(employeeOtReviewer
						.getEmployeeReviewer()));

			}
			if (employeeOtReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {

				addOTForm.setClaimReviewer3(getEmployeeName(employeeOtReviewer
						.getEmployeeReviewer()));

			}

		}
	}

	public void getClaimReviewer(AddClaimForm addOTForm,
			String pageContextPath, EmployeeTimesheetApplication lionTimesheet,
			long empId) {

		String timesheetStatus = lionTimesheet.getTimesheetStatusMaster()
				.getTimesheetStatusName();
		Employee reviewer = null;
		if (timesheetStatus
				.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
			EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO
					.findByEmployeeId(empId);
			reviewer = employeeTimesheetReviewer.getEmployeeReviewer();

		} else {
			List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
					.getLionEmployeeTimesheetApplicationDetails(lionTimesheet
							.getTimesheetId());

			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetailReviewer = null;

			for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {
				if ((lionEmployeeTimesheetApplicationDetail
						.getTimesheetStatusMaster().getTimesheetStatusName())
						.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_APPROVED)
						|| (lionEmployeeTimesheetApplicationDetail
								.getTimesheetStatusMaster()
								.getTimesheetStatusName())
								.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_COMPLETED)
						|| (lionEmployeeTimesheetApplicationDetail
								.getTimesheetStatusMaster()
								.getTimesheetStatusName())
								.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_SUBMITTED)) {
					lionEmployeeTimesheetApplicationDetailReviewer = lionEmployeeTimesheetApplicationDetail;
				}
			}
			if (lionEmployeeTimesheetApplicationDetailReviewer == null) {
				reviewer = null;
			} else {
				LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
						.findByEmployeeTimesheetDetailId(lionEmployeeTimesheetApplicationDetailReviewer
								.getEmployeeTimesheetDetailID());
				reviewer = lionTimesheetApplicationReviewer
						.getEmployeeReviewer();
			}

		}

		StringBuilder ClaimReviewer1 = new StringBuilder(getStatusImage(
				timesheetStatus, pageContextPath, reviewer, lionTimesheet));

		addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

	}

	public void getSubmittedClaimReviewers(
			List<TimesheetApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath,
			EmployeeTimesheetApplication lundinTimesheet) {
		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (TimesheetApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());

				if (lundinWorkflow == null) {

					addOTForm.setClaimReviewer1(getStatusImage(
							PayAsiaConstants.CLAIM_STATUS_PENDING,
							pageContextPath,
							otApplicationReviewer.getEmployee(),
							lundinTimesheet));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (lundinWorkflow
						.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equalsIgnoreCase(
								PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					StringBuilder claimReviewer1 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									otApplicationReviewer.getEmployee(),
									lundinTimesheet));

					addOTForm.setClaimReviewer1(String.valueOf(claimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				}
			}
			if (revCount == 2) {

				TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());

				if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addOTForm.setClaimReviewer2(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployee(),
							lundinTimesheet));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (lundinWorkflow == null) {

						addOTForm.setClaimReviewer2(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployee(),
								lundinTimesheet));
						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (lundinWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer2 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer.getEmployee(),
										lundinTimesheet));

						claimReviewer2
								.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ lundinWorkflow.getCreatedDate()
										+ "</span>");

						addOTForm.setClaimReviewer2(String
								.valueOf(claimReviewer2));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					}

				}

			}
			if (revCount == 3) {
				TimesheetApplicationWorkflow lundinWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());
				if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addOTForm.setClaimReviewer3(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployee(),
							lundinTimesheet));

				} else if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (lundinWorkflow == null) {
						addOTForm.setClaimReviewer3(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployee(),
								lundinTimesheet));
					} else if (lundinWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer3 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer.getEmployee(),
										lundinTimesheet));

						addOTForm.setClaimReviewer3(String
								.valueOf(claimReviewer3));

					}

				}

			}
			revCount++;
		}
	}

	public void getWithdrawClaimReviewers(
			List<TimesheetApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOtForm) {
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (TimesheetApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				addOtForm
						.setClaimReviewer1(getEmployeeName(otApplicationReviewer
								.getEmployee()));

			}
			if (revCount == 2) {

				addOtForm
						.setClaimReviewer2(getEmployeeName(otApplicationReviewer
								.getEmployee()));

			}
			if (revCount == 3) {

				addOtForm
						.setClaimReviewer3(getEmployeeName(otApplicationReviewer
								.getEmployee()));

			}
			revCount++;
		}
	}

	public void getRejectedClaimReviewers(
			List<TimesheetApplicationReviewer> otApplicationReviewers,
			AddClaimForm addOTForm, String pageContextPath,
			EmployeeTimesheetApplication lundinTimesheet) {

		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(otApplicationReviewers,
				new IngersollOTTimesheetReviewerComp());
		int revCount = 1;
		for (TimesheetApplicationReviewer otApplicationReviewer : otApplicationReviewers) {
			if (revCount == 1) {

				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());

				if (applicationWorkflow == null) {

					addOTForm.setClaimReviewer1(getStatusImage(
							PayAsiaConstants.CLAIM_STATUS_PENDING,
							pageContextPath,
							otApplicationReviewer.getEmployee(),
							lundinTimesheet));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (applicationWorkflow
						.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equalsIgnoreCase(
								PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_APPROVED,
									pageContextPath,
									otApplicationReviewer.getEmployee(),
									lundinTimesheet));

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				} else if (applicationWorkflow
						.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equalsIgnoreCase(
								PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(
									PayAsiaConstants.CLAIM_STATUS_REJECTED,
									pageContextPath,
									otApplicationReviewer.getEmployee(),
									lundinTimesheet));

					addOTForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

				}
			}
			if (revCount == 2) {

				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());

				if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer1Status
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addOTForm.setClaimReviewer2(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployee(),
							lundinTimesheet));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {

						addOTForm.setClaimReviewer2(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployee(),
								lundinTimesheet));
						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer2 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer.getEmployee(),
										lundinTimesheet));

						addOTForm.setClaimReviewer2(String
								.valueOf(ClaimReviewer2));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addOTForm.setClaimReviewer2(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_REJECTED,
								pageContextPath,
								otApplicationReviewer.getEmployee(),
								lundinTimesheet));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

					}

				}

			}
			if (revCount == 3) {
				TimesheetApplicationWorkflow applicationWorkflow = lundinTimesheetWorkflowDAO
						.findByCondition(lundinTimesheet.getTimesheetId(),
								otApplicationReviewer.getEmployee()
										.getEmployeeId());
				if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer2Status
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addOTForm.setClaimReviewer3(getStatusImage("NA",
							pageContextPath,
							otApplicationReviewer.getEmployee(),
							lundinTimesheet));

				} else if (reviewer2Status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {
						addOTForm.setClaimReviewer3(getStatusImage(
								PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath,
								otApplicationReviewer.getEmployee(),
								lundinTimesheet));
					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_APPROVED,
										pageContextPath,
										otApplicationReviewer.getEmployee(),
										lundinTimesheet));

						addOTForm.setClaimReviewer3(String
								.valueOf(ClaimReviewer3));

					} else if (applicationWorkflow
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equalsIgnoreCase(
									PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(
										PayAsiaConstants.CLAIM_STATUS_REJECTED,
										pageContextPath,
										otApplicationReviewer.getEmployee(),
										lundinTimesheet));

						addOTForm.setClaimReviewer3(String
								.valueOf(ClaimReviewer3));

					}

				}

			}
			revCount++;
		}
	}

	private String getStatusImage(String status, String contextPath,
			Employee employee,
			EmployeeTimesheetApplication employeeTimesheetApplication) {
		String imagePath = contextPath + "/resources/images/icon/16/";

		if (status
				.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_SUBMITTED)) {
			imagePath = imagePath + "pending.png";
		} else if (status
				.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_COMPLETED)) {
			imagePath = imagePath + "approved.png";
		} else if ("NA".equalsIgnoreCase(status)) {
			imagePath = imagePath + "pending-next-level.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
			imagePath = "";
		}
		String employeeName = null;
		if (employee == null) {
			employeeName = "";
		} else {
			employeeName = getEmployeeName(employee);
		}

		String timesheetDate = getStatusDate(employeeTimesheetApplication);

		return "<img src='" + imagePath + "'  />  " + employeeName
				+ timesheetDate;

	}

	private String getStatusDate(
			EmployeeTimesheetApplication employeeTimesheetApplication) {

		if (employeeTimesheetApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
			TimesheetBatch timesheetBatch = timesheetBatchDAO
					.findById(employeeTimesheetApplication.getTimesheetBatch()
							.getTimesheetBatchId());
			return "<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
					+ timesheetBatch.getCreatedDate() + "</span>";
		} else {
			return "<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
					+ employeeTimesheetApplication.getUpdatedDate() + "</span>";
		}

	}

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

	@Override
	public Map<String, Integer> getNumberofBatches(String codeDesc) {

		int iterationNo;
		int incrementalDays;

		switch (codeDesc) {
		case "Weekly":
			iterationNo = 53;
			incrementalDays = 6;
			break;
		case "Fortnightly":
			iterationNo = 27;
			incrementalDays = 13;
			break;
		case "Monthly":
			iterationNo = 12;
			incrementalDays = -1;
			break;
		default:
			iterationNo = 0;
			incrementalDays = 0;
			break;
		}

		Map<String, Integer> iterationIncrementalMap = new HashMap<String, Integer>();
		iterationIncrementalMap.put("incrementalDays", incrementalDays);
		iterationIncrementalMap.put("iterationNo", iterationNo);

		return iterationIncrementalMap;
	}

	public static Timestamp addDays(Date d, int days) {
		Date newDate = new Date();
		newDate.setTime(d.getTime() + days * 1000 * 60 * 60 * 24);

		Timestamp timestamp = new Timestamp(newDate.getTime());

		return timestamp;
	}

	List<TimesheetBatch> getValidTimesheetBatches(
			List<TimesheetBatch> timesheetBatches, long companyId,
			Long employeeId) {

		Date currentDate = new Date();
		Timestamp batchStartTimestamp = new Timestamp(currentDate.getTime());

		Calendar currentCalendar = Calendar.getInstance();
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();

		currentCalendar.setTime(currentDate);
		currentCalendar.set(Calendar.MILLISECOND, 0);
		currentCalendar.set(Calendar.SECOND, 0);
		currentCalendar.set(Calendar.MINUTE, 0);
		currentCalendar.set(Calendar.HOUR_OF_DAY, 0);

		List<TimesheetBatch> validTimesheetBatches = new ArrayList<TimesheetBatch>();

		for (TimesheetBatch timesheetBatch : timesheetBatches) {

			startCalendar.setTime(new Date(timesheetBatch.getStartDate()
					.getTime()));
			endCalendar
					.setTime(new Date(timesheetBatch.getEndDate().getTime()));

			startCalendar.set(Calendar.MILLISECOND, 0);
			startCalendar.set(Calendar.SECOND, 0);
			startCalendar.set(Calendar.MINUTE, 0);
			startCalendar.set(Calendar.HOUR_OF_DAY, 0);

			endCalendar.set(Calendar.MILLISECOND, 0);
			endCalendar.set(Calendar.SECOND, 0);
			endCalendar.set(Calendar.MINUTE, 0);
			endCalendar.set(Calendar.HOUR_OF_DAY, 0);

			if ((currentCalendar.getTime()).compareTo(startCalendar.getTime()) >= 0
					&& (endCalendar.getTime()).compareTo(currentCalendar
							.getTime()) >= 0) {
				validTimesheetBatches.add(timesheetBatch);
				batchStartTimestamp = timesheetBatch.getStartDate();
			}
		}

		TimesheetBatch secondBatch = null;

		for (TimesheetBatch timesheetBatch : timesheetBatches) {

			if (timesheetBatch.getEndDate().before(batchStartTimestamp)) {
				secondBatch = timesheetBatch;
			}
		}

		if (secondBatch != null) {
			validTimesheetBatches.add(secondBatch);
		}

		List<TimesheetBatch> finalValidTimesheetBatches = new ArrayList<TimesheetBatch>();

		for (TimesheetBatch timesheetBatch : validTimesheetBatches) {
			List<EmployeeTimesheetApplication> isemployeeTimesheetApplication = employeeTimesheetApplicationDAO
					.findByTimesheetBatchId(
							timesheetBatch.getTimesheetBatchId(), companyId,
							employeeId);
			if (isemployeeTimesheetApplication.size() == 0) {
				finalValidTimesheetBatches.add(timesheetBatch);
			}
		}

		Collections.reverse(finalValidTimesheetBatches);

		return finalValidTimesheetBatches;

	}

	@Override
	public Long getEmployeeIdByNumber(String employeeNumber, Long companyId) {
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber,
				companyId);
		if (employeeVO != null) {
			return employeeVO.getEmployeeId();
		} else {
			return 0l;
		}
	}

}
