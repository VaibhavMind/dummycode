package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.OTBatchMasterConditionDTO;
import com.payasia.common.form.OTBatchForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.OTBatchMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.OTBatchMaster;
import com.payasia.logic.OTBatchDefinitionLogic;

@Component
public class OTBatchDefinitionLogicImpl implements OTBatchDefinitionLogic {

	@Resource
	OTBatchMasterDAO oTBatchMasterDAO;

	@Resource
	CompanyDAO companyDAO;

	@Override
	public OTBatchForm checkOTItem(Long companyId, OTBatchForm oTBatchForm,
			Long OtBatchId) {
		OTBatchForm oTBatchFormResponse = new OTBatchForm();
		OTBatchMaster oTBatchMaster = oTBatchMasterDAO
				.findByCompanyIdItemNameItemId(companyId,
						oTBatchForm.getDescription(), OtBatchId);

		if (oTBatchMaster != null) {
			oTBatchFormResponse.setStatus("failure");

		} else {
			oTBatchFormResponse.setStatus("success");

		}

		return oTBatchFormResponse;
	}

	@Override
	public void saveOTItem(OTBatchForm oTBatchForm, Long companyId) {
		OTBatchMaster oTBatchMaster = new OTBatchMaster();
		Company company = companyDAO.findById(companyId);
		oTBatchMaster.setCompany(company);
		oTBatchMaster.setEndDate(DateUtils.stringToTimestamp(oTBatchForm
				.getToDate()));
		oTBatchMaster.setStartDate(DateUtils.stringToTimestamp(oTBatchForm
				.getFromDate()));
		oTBatchMaster.setOTBatchDesc(oTBatchForm.getDescription());
		oTBatchMasterDAO.save(oTBatchMaster);

	}

	@Override
	public OTBatchForm getOTBatchList(String searchCriteria,
			String searchKeyword, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId) {

		List<OTBatchMaster> oTBatchMasterListVO;

		OTBatchMasterConditionDTO conditionDTO = new OTBatchMasterConditionDTO();

		int recordSize = 0;

		if ("description".equals(searchCriteria)) {
			if (StringUtils.isNotBlank(searchKeyword)) {
				conditionDTO.setDescription("%" + searchKeyword.trim() + "%");
			}

		}

		if ("startDate".equals(searchCriteria)) {
			if (StringUtils.isNotBlank(searchKeyword)) {
				conditionDTO.setFromDate(DateUtils
						.stringToTimestamp(searchKeyword.trim()));
			}

		}

		if ("endDate".equals(searchCriteria)) {
			if (StringUtils.isNotBlank(searchKeyword)) {
				conditionDTO.setToDate(DateUtils
						.stringToTimestamp(searchKeyword.trim()));
			}

		}

		recordSize = oTBatchMasterDAO.getCountByCondition(conditionDTO,
				companyId);
		oTBatchMasterListVO = oTBatchMasterDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		List<OTBatchForm> oTBatchMasterList = new ArrayList<OTBatchForm>();
		for (OTBatchMaster oTBatchFormVO : oTBatchMasterListVO) {
			OTBatchForm oTBatchForm = new OTBatchForm();
			oTBatchForm.setOtBatchId(oTBatchFormVO.getOTBatchId());
			oTBatchForm.setDescription(oTBatchFormVO.getOTBatchDesc());
			oTBatchForm.setFromDate(DateUtils.timeStampToString(oTBatchFormVO
					.getStartDate()));
			oTBatchForm.setToDate(DateUtils.timeStampToString(oTBatchFormVO
					.getEndDate()));

			oTBatchMasterList.add(oTBatchForm);

		}

		OTBatchForm oTBatchFormResponse = new OTBatchForm();

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(1);
			}

			oTBatchFormResponse.setPage(pageDTO.getPageNumber());
			oTBatchFormResponse.setTotal(totalPages);

			oTBatchFormResponse.setRecords(recordSize);
		}
		oTBatchFormResponse.setRows(oTBatchMasterList);
		return oTBatchFormResponse;

	}

	@Override
	public void updateOTItem(OTBatchForm oTBatchForm, Long companyId) {
		OTBatchMaster oTBatchMaster = oTBatchMasterDAO.findById(oTBatchForm
				.getOtBatchId());

		oTBatchMaster.setEndDate(DateUtils.stringToTimestamp(oTBatchForm
				.getToDate()));
		oTBatchMaster.setStartDate(DateUtils.stringToTimestamp(oTBatchForm
				.getFromDate()));
		oTBatchMaster.setOTBatchDesc(oTBatchForm.getDescription());
		oTBatchMasterDAO.update(oTBatchMaster);

	}

	@Override
	public void deleteOTBatch(Long otBatchId) {
		OTBatchMaster oTBatchMaster = oTBatchMasterDAO.findById(otBatchId);
		oTBatchMasterDAO.delete(oTBatchMaster);

	}

}
