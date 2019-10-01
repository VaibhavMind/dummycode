package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.LeaveBatchConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LeaveBatchForm;
import com.payasia.common.form.LeaveBatchResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.LeaveBatchMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.LeaveBatchMaster;
import com.payasia.logic.LeaveBatchLogic;

@Component
public class LeaveBatchLogicImpl implements LeaveBatchLogic {

	@Resource
	LeaveBatchMasterDAO leaveBatchMasterDAO;

	@Resource
	CompanyDAO companyDAO;

	@Override
	public LeaveBatchResponse viewLeaveBatch(PageRequest pageDTO,
			SortCondition sortDTO, String leaveBatchFilter, String filterText,
			Long companyID) {

		LeaveBatchConditionDTO leaveBatchDTO = new LeaveBatchConditionDTO();

		if ("description".equals(leaveBatchFilter)) {
			if (StringUtils.isNotBlank(filterText)) {
				leaveBatchDTO.setDescription("%" + filterText.trim() + "%");
			}

		}

		if ("startDate".equals(leaveBatchFilter)) {
			if (StringUtils.isNotBlank(filterText)) {
				leaveBatchDTO.setStartDate(DateUtils
						.stringToTimestamp(filterText));
			}

		}

		if ("endDate".equals(leaveBatchFilter)) {
			if (StringUtils.isNotBlank(filterText)) {
				leaveBatchDTO.setEndDate(DateUtils
						.stringToTimestamp(filterText));
			}

		}

		List<LeaveBatchMaster> leaveBatchMasterList;
		int recordSize = 0;

		recordSize = leaveBatchMasterDAO.countLeaveBatch(leaveBatchDTO,
				companyID);
		leaveBatchMasterList = leaveBatchMasterDAO.getLeaveBatchByCondition(
				pageDTO, sortDTO, leaveBatchDTO, companyID);

		List<LeaveBatchForm> leaveBatchFormList = new ArrayList<LeaveBatchForm>();

		for (LeaveBatchMaster leaveBatchMaster : leaveBatchMasterList) {
			LeaveBatchForm leaveBatchForm = new LeaveBatchForm();
			leaveBatchForm.setLeaveBatchID(leaveBatchMaster.getLeaveBatchId());
			leaveBatchForm.setDescription(leaveBatchMaster.getLeaveBatchDesc());
			leaveBatchForm.setStartDate(DateUtils
					.timeStampToString(leaveBatchMaster.getStartDate()));
			leaveBatchForm.setEndDate(DateUtils
					.timeStampToString(leaveBatchMaster.getEndDate()));
			leaveBatchFormList.add(leaveBatchForm);
		}

		LeaveBatchResponse response = new LeaveBatchResponse();

		response.setRows(leaveBatchFormList);
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
	public String addLeaveBatch(LeaveBatchForm leaveBatchForm, Long companyId) {
		LeaveBatchMaster leaveBatchMaster = new LeaveBatchMaster();

		leaveBatchMaster.setLeaveBatchDesc(leaveBatchForm.getDescription());
		leaveBatchMaster.setStartDate(DateUtils
				.stringToTimestamp(leaveBatchForm.getStartDate()));
		leaveBatchMaster.setEndDate(DateUtils.stringToTimestamp(leaveBatchForm
				.getEndDate()));
		Company company = companyDAO.findById(companyId);
		leaveBatchMaster.setCompany(company);

		String duplicateFieldInDB = checkExistInDB(null,
				leaveBatchForm.getDescription(), companyId);

		if (duplicateFieldInDB == null) {
			leaveBatchMasterDAO.save(leaveBatchMaster);
			return "unique entry".toLowerCase();
		} else
			return duplicateFieldInDB.toLowerCase();
	}

	private String checkExistInDB(Long leaveBatchId, String leaveDesc,
			Long companyId) {
		LeaveBatchMaster leaveMasterVO = leaveBatchMasterDAO.findByDescCompany(
				leaveBatchId, leaveDesc, companyId);
		if (leaveMasterVO == null) {
			return null;
		} else
			return "description".toLowerCase();
	}

	@Override
	public LeaveBatchForm getLeaveBatchData(Long leaveBatchId) {
		LeaveBatchForm leaveBatchForm = new LeaveBatchForm();
		LeaveBatchMaster leaveBatchMaster = leaveBatchMasterDAO
				.findByID(leaveBatchId);
		leaveBatchForm.setDescription(leaveBatchMaster.getLeaveBatchDesc());
		leaveBatchForm.setStartDate(DateUtils
				.timeStampToString(leaveBatchMaster.getStartDate()));
		leaveBatchForm.setEndDate(DateUtils.timeStampToString(leaveBatchMaster
				.getEndDate()));
		return leaveBatchForm;
	}

	@Override
	public String editLeaveBatch(LeaveBatchForm leaveBatchForm,
			Long leaveBatchId, Long companyId) {
		LeaveBatchMaster leaveBatchMaster = new LeaveBatchMaster();
		LeaveBatchMaster leaveBatchMasterCheck = leaveBatchMasterDAO
				.findByCompID(leaveBatchId, companyId);
		if(leaveBatchMasterCheck == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		Company company = companyDAO.findById(companyId);
		leaveBatchMaster.setCompany(company);
		leaveBatchMaster.setLeaveBatchId(leaveBatchId);
		leaveBatchMaster.setLeaveBatchDesc(leaveBatchForm.getDescription());
		leaveBatchMaster.setStartDate(DateUtils
				.stringToTimestamp(leaveBatchForm.getStartDate()));
		leaveBatchMaster.setEndDate(DateUtils.stringToTimestamp(leaveBatchForm
				.getEndDate()));
		String duplicateFieldInDB = checkExistInDB(
				leaveBatchForm.getLeaveBatchID(),
				leaveBatchForm.getDescription(), companyId);
		if (duplicateFieldInDB == null) {
			leaveBatchMasterDAO.update(leaveBatchMaster);
			return "unique entry".toLowerCase();
		} else
			return duplicateFieldInDB.toLowerCase();
	}

	@Override
	public void deleteLeaveBatch(Long leaveBatchId) {
		LeaveBatchMaster leaveBatchMaster = leaveBatchMasterDAO
				.findByID(leaveBatchId);
		leaveBatchMasterDAO.delete(leaveBatchMaster);
	}

	@Override
	public LeaveBatchForm getLeaveBatchDataByCompany(Long leaveBatchId, Long companyId) {
		LeaveBatchForm leaveBatchForm = new LeaveBatchForm();
		LeaveBatchMaster leaveBatchMaster = leaveBatchMasterDAO
				.findByCompID(leaveBatchId, companyId);
		leaveBatchForm.setDescription(leaveBatchMaster.getLeaveBatchDesc());
		leaveBatchForm.setStartDate(DateUtils
				.timeStampToString(leaveBatchMaster.getStartDate()));
		leaveBatchForm.setEndDate(DateUtils.timeStampToString(leaveBatchMaster
				.getEndDate()));
		return leaveBatchForm;
	}

}
