package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.LeaveBatchConditionDTO;
import com.payasia.common.form.ClaimBatchForm;
import com.payasia.common.form.ClaimBatchResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimBatchMasterDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimBatchMaster;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.Company;
import com.payasia.logic.ClaimBatchLogic;

@Component
public class ClaimBatchLogicImpl implements ClaimBatchLogic {

	@Resource
	ClaimBatchMasterDAO claimBatchMasterDAO;
	@Autowired
	private MessageSource messageSource;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	ClaimStatusMasterDAO claimStatusMasterDAO;
	@Resource
	ClaimApplicationDAO ClaimApplicationDAO;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	private static final Logger LOGGER = Logger.getLogger(ClaimBatchLogicImpl.class);

	@Override
	public ClaimBatchResponse viewClaimBatch(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {

		LeaveBatchConditionDTO leaveBatchDTO = new LeaveBatchConditionDTO();
		String view = "payasia.view";
		String claims = "payasia.claims";

		if ("description".equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				leaveBatchDTO.setDescription("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if ("startDate".equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				leaveBatchDTO.setStartDate(DateUtils.stringToTimestamp(claimDTO.getSearchText()));
			}
		}

		if ("endDate".equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				leaveBatchDTO.setEndDate(DateUtils.stringToTimestamp(claimDTO.getSearchText()));
			}
		}

		Company companyVO = companyDAO.findById(claimDTO.getCompanyId());

		List<ClaimBatchMaster> claimBatchMasterVOList;
		int recordSize = claimBatchMasterDAO.getClaimBatchCountByCondition(leaveBatchDTO, claimDTO.getCompanyId(),
				claimDTO.getYear());

		claimBatchMasterVOList = claimBatchMasterDAO.getClaimBatchByCondition(pageDTO, sortDTO, leaveBatchDTO,
				claimDTO.getCompanyId(), claimDTO.getYear());

		List<ClaimBatchForm> claimBatchFormList = new ArrayList<ClaimBatchForm>();

		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(claimDTO.getCompanyId());
		boolean showPaidStatusForClaimBatch = false;
		if (claimPreferenceVO != null) {
			showPaidStatusForClaimBatch = claimPreferenceVO.isShowPaidStatusForClaimBatch();
		}

		for (ClaimBatchMaster claimBatchMasterVO : claimBatchMasterVOList) {
			boolean status = true;
			ClaimBatchForm claimBatchForm = new ClaimBatchForm();

			claimBatchForm.setClaimBatchID(FormatPreserveCryptoUtil.encrypt(claimBatchMasterVO.getClaimBatchID()));
			claimBatchForm.setDescription(claimBatchMasterVO.getClaimBatchDesc());
			claimBatchForm.setStartDate(DateUtils.timeStampToString(claimBatchMasterVO.getStartDate()));
			claimBatchForm.setEndDate(DateUtils.timeStampToString(claimBatchMasterVO.getEndDate()));

			claimBatchForm.setPaid(claimBatchMasterVO.getPaid());
			if (claimBatchMasterVO.getPaidDate() != null) {
				claimBatchForm.setPaidStatus(DateUtils.dateToString(claimBatchMasterVO.getPaidDate()));
			}

			List<ClaimApplication> claimApplicationList = ClaimApplicationDAO.findClaimsByBatchPeriod(null, null,
					PayAsiaConstants.CLAIM_STATUS_COMPLETED, claimBatchMasterVO.getStartDate(),
					claimBatchMasterVO.getEndDate(), claimDTO.getCompanyId());

			String claimBatchDetailCount = "<span class='Text'><h2>" + String.valueOf(claimApplicationList.size())
					+ "</h2></span>";
			claimBatchDetailCount += "<span class='Textsmall' style='padding-top: 5px;'>&nbsp;"
					+ messageSource.getMessage(claims, new Object[] {}, claimDTO.getLocale()) + "</span>";
			claimBatchDetailCount += "<br><br>	";
			claimBatchDetailCount += "<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'showClaimBatchEmpDetail("
					+ FormatPreserveCryptoUtil.encrypt(claimBatchMasterVO.getClaimBatchID()) + ")'>["
					+ messageSource.getMessage(view, new Object[] {}, claimDTO.getLocale()) + "]</a></span>";

			claimBatchForm.setClaims(claimBatchDetailCount);

			Date todayDate = DateUtils.stringToDate(
					DateUtils.timeStampToString(DateUtils.getCurrentTimestamp(), companyVO.getDateFormat()),
					companyVO.getDateFormat());
			Date endDate = DateUtils.stringToDate(
					DateUtils.timeStampToString(claimBatchMasterVO.getEndDate(), companyVO.getDateFormat()),
					companyVO.getDateFormat());

			if (endDate.before(todayDate)) {
				status = false;
			}
			if (status) {
				claimBatchForm.setEditable("true");
			} else {
				claimBatchForm.setEditable("false");
			}
			claimBatchForm.setFieldEditable("false");
			if (showPaidStatusForClaimBatch) {
				claimBatchForm.setFieldEditable("true");
			}

			claimBatchFormList.add(claimBatchForm);

		}

		ClaimBatchResponse response = new ClaimBatchResponse();

		response.setRows(claimBatchFormList);
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

	public String addClaimBatch(ClaimBatchForm claimBatchForm, Long companyId) {
		boolean status = true;
		ClaimBatchMaster claimBatchMasterVO = new ClaimBatchMaster();

		Company companyVO = companyDAO.findById(companyId);

		ClaimBatchMaster empRecordByFromDateVO = claimBatchMasterDAO.checkClaimBatchByDate(null, companyId,
				claimBatchForm.getStartDate(), companyVO.getDateFormat());
		if (empRecordByFromDateVO != null) {
			status = false;
		}
		if (StringUtils.isNotBlank(claimBatchForm.getEndDate())) {
			ClaimBatchMaster empRecordByToDateVO = claimBatchMasterDAO.checkClaimBatchByDate(null, companyId,
					claimBatchForm.getEndDate(), companyVO.getDateFormat());
			if (empRecordByToDateVO != null) {
				status = false;
			}
		}

		claimBatchMasterVO.setPaid(false);
		if (StringUtils.isNotBlank(claimBatchForm.getPaidDate())) {
			claimBatchMasterVO.setPaidDate(DateUtils.stringToDate(claimBatchForm.getPaidDate()));
		}
		String descriptiosDecode = "";
		if (status) {
			try {
				descriptiosDecode = URLDecoder.decode(claimBatchForm.getDescription(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			String duplicateFieldInDB = checkExistInDB(null, descriptiosDecode, companyId);
			if (duplicateFieldInDB == null) {
				claimBatchMasterVO.setStartDate(DateUtils.stringToTimestamp(claimBatchForm.getStartDate()));
				claimBatchMasterVO.setClaimBatchDesc(descriptiosDecode);
				claimBatchMasterVO.setEndDate(DateUtils.stringToTimestamp(claimBatchForm.getEndDate()));

				claimBatchMasterVO.setCompany(companyVO);
				claimBatchMasterDAO.save(claimBatchMasterVO);
				return "success";
			} else {
				return "duplicate";
			}
		} else {
			return "overlap";
		}
	}

	String checkExistInDB(Long claimBatchId, String claimDesc, Long companyId) {
		ClaimBatchMaster claimMasterVO = claimBatchMasterDAO.findByDescCompany(claimBatchId, claimDesc, companyId);
		if (claimMasterVO == null) {
			return null;
		} else
			return "description";
	}

	@Override
	public ClaimBatchForm getClaimBatchData(Long claimBatchId, Long companyId) {

		ClaimBatchForm claimBatchForm = new ClaimBatchForm();
		Company companyVO = companyDAO.findById(companyId);
		ClaimBatchMaster claimBatchMasterVO = claimBatchMasterDAO
				.findByClaimBatchMasterId(claimBatchId,companyId);
		Date todayDate = new Date();
		Date startDate = DateUtils.stringToDate(
				DateUtils.timeStampToString(claimBatchMasterVO.getStartDate(), companyVO.getDateFormat()),
				companyVO.getDateFormat());
		if (startDate.before(todayDate)) {
			claimBatchForm.setEditable("false");
		} else {
			claimBatchForm.setEditable("true");
		}

		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		boolean showPaidStatusForClaimBatch = false;
		if (claimPreferenceVO != null) {
			showPaidStatusForClaimBatch = claimPreferenceVO.isShowPaidStatusForClaimBatch();
		}
		claimBatchForm.setFieldEditable("false");
		if (showPaidStatusForClaimBatch) {
			claimBatchForm.setFieldEditable("true");
		}
		claimBatchForm.setDescription(claimBatchMasterVO.getClaimBatchDesc());
		claimBatchForm.setStartDate(DateUtils.timeStampToString(claimBatchMasterVO.getStartDate()));
		claimBatchForm.setEndDate(DateUtils.timeStampToString(claimBatchMasterVO.getEndDate()));
		claimBatchForm.setPaid(claimBatchMasterVO.getPaid());
		claimBatchForm.setPaidDate(DateUtils.dateToString(claimBatchMasterVO.getPaidDate()));

		return claimBatchForm;
	}

	@Override
	public String editClaimBatch(ClaimBatchForm claimBatchForm, Long companyId) {
		boolean status = true;
		claimBatchForm.setPaid(StringUtils.isNotBlank(claimBatchForm.getPaidDate()) ? true : false);
		ClaimBatchMaster claimBatchMasterVO = new ClaimBatchMaster();

		Company companyVO = companyDAO.findById(companyId);

		ClaimBatchMaster empRecordByFromDateVO = claimBatchMasterDAO.checkClaimBatchByDate(
				claimBatchForm.getClaimBatchID(), companyId, claimBatchForm.getStartDate(), companyVO.getDateFormat());
		if (empRecordByFromDateVO != null) {
			status = false;
		}
		if (StringUtils.isNotBlank(claimBatchForm.getEndDate())) {
			ClaimBatchMaster empRecordByToDateVO = claimBatchMasterDAO.checkClaimBatchByDate(
					claimBatchForm.getClaimBatchID(), companyId, claimBatchForm.getEndDate(),
					companyVO.getDateFormat());
			if (empRecordByToDateVO != null) {
				status = false;
			}
		}

		claimBatchMasterVO.setPaid(false);
		if (StringUtils.isNotBlank(claimBatchForm.getPaidDate())) {
			claimBatchMasterVO.setPaidDate(DateUtils.stringToDate(claimBatchForm.getPaidDate()));
		}
		String description = "";
		try {
			description = URLDecoder.decode(claimBatchForm.getDescription(), "UTF-8");
		} catch (UnsupportedEncodingException e) {

		}

		if (status) {
			String duplicateFieldInDB = checkExistInDB(claimBatchForm.getClaimBatchID(), description, companyId);

			if (duplicateFieldInDB == null) {

				claimBatchMasterVO.setCompany(companyVO);

				claimBatchMasterVO.setClaimBatchID(claimBatchForm.getClaimBatchID());
				claimBatchMasterVO.setClaimBatchDesc(description);
				claimBatchMasterVO.setStartDate(DateUtils.stringToTimestamp(claimBatchForm.getStartDate()));
				claimBatchMasterVO.setEndDate(DateUtils.stringToTimestamp(claimBatchForm.getEndDate()));
				claimBatchMasterDAO.update(claimBatchMasterVO);
				return "success";
			} else {
				return "duplicate";
			}

		} else {
			return "overlap";
		}

	}

	@Override
	public void deleteClaimBatch(Long claimBatchId, Long companyId) {
		ClaimBatchMaster claimBatchMaster = claimBatchMasterDAO
				.findByClaimBatchMasterId(claimBatchId, companyId);
		claimBatchMasterDAO.delete(claimBatchMaster);
	}

	@Override
	public ClaimBatchResponse viewClaimBatchDetail(PageRequest pageDTO, SortCondition sortDTO, Long claimBatchId,
			Long companyID) {

		ClaimBatchMaster claimBatchMasterVO = claimBatchMasterDAO.findByClaimBatchMasterId(claimBatchId,companyID);

		List<ClaimBatchForm> claimBatchFormList = new ArrayList<ClaimBatchForm>();

		List<ClaimApplication> claimApplicationList = ClaimApplicationDAO.findClaimsByBatchPeriod(pageDTO, sortDTO,
				PayAsiaConstants.CLAIM_STATUS_COMPLETED, claimBatchMasterVO.getStartDate(),
				claimBatchMasterVO.getEndDate(), companyID);
		int recordSize = ClaimApplicationDAO.getClaimsByBatchPeriodCount(PayAsiaConstants.CLAIM_STATUS_COMPLETED,
				claimBatchMasterVO.getStartDate(), claimBatchMasterVO.getEndDate(), companyID);
		for (ClaimApplication claimApplicationVO : claimApplicationList) {

			ClaimBatchForm claimBatchForm = new ClaimBatchForm();

			claimBatchForm.setEmployeeNumber(claimApplicationVO.getEmployee().getEmployeeNumber());
			String empName = "";
			empName += claimApplicationVO.getEmployee().getFirstName();
			if (StringUtils.isNotBlank(claimApplicationVO.getEmployee().getLastName())) {
				empName += claimApplicationVO.getEmployee().getLastName();
			}
			claimBatchForm.setClaimTemplate(
					claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			claimBatchForm.setCreatedDate(DateUtils.timeStampToString(claimApplicationVO.getCreatedDate()));
			claimBatchForm.setTotalAmount(claimApplicationVO.getTotalAmount());

			claimBatchForm.setEmployeeName(empName);

			claimBatchForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplicationVO.getClaimApplicationId()));

			claimBatchFormList.add(claimBatchForm);

		}

		ClaimBatchResponse response = new ClaimBatchResponse();

		response.setRows(claimBatchFormList);
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
	public ClaimBatchForm getClaimBatchConf(Long companyId) {
		ClaimBatchForm claimPreferenceForm = new ClaimBatchForm();
		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreferenceVO != null) {
			claimPreferenceForm.setShowPaidStatusForClaimBatch(claimPreferenceVO.isShowPaidStatusForClaimBatch());
		}

		return claimPreferenceForm;
	}
}
