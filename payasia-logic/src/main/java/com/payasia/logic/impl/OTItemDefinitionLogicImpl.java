package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.OTItemMasterConditionDTO;
import com.payasia.common.form.OTItemDefinitionForm;
import com.payasia.common.form.OTItemDefinitionResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.OTItemMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.OTItemMaster;
import com.payasia.logic.OTItemDefinitionLogic;

@Component
public class OTItemDefinitionLogicImpl implements OTItemDefinitionLogic {

	@Resource
	OTItemMasterDAO oTItemMasterDAO;

	@Resource
	CompanyDAO companyDAO;

	@Override
	public OTItemDefinitionResponse getOTItemList(String searchCriteria,
			String keyword, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId) {

		List<OTItemMaster> oTItemMasters;

		OTItemMasterConditionDTO conditionDTO = new OTItemMasterConditionDTO();

		if ("name".equals(searchCriteria)) {
			if (StringUtils.isNotBlank(keyword)) {
				conditionDTO.setName("%" + keyword.trim() + "%");
			}

		}

		if ("code".equals(searchCriteria)) {
			if (StringUtils.isNotBlank(keyword)) {
				conditionDTO.setCode("%" + keyword.trim() + "%");
			}

		}

		if ("description".equals(searchCriteria)) {
			if (StringUtils.isNotBlank(keyword)) {
				conditionDTO.setDescription("%" + keyword.trim() + "%");
			}

		}

		if ("visibleOrHidden".equals(searchCriteria)) {
			if (StringUtils.isNotBlank(keyword)) {
				conditionDTO.setVisibleOrHidden(keyword.trim());
			}

		}

		int recordSize = 0;

		recordSize = oTItemMasterDAO.getCountByCondition(conditionDTO,
				companyId);
		oTItemMasters = oTItemMasterDAO.findByCondition(conditionDTO, pageDTO,
				sortDTO, companyId);

		List<OTItemDefinitionForm> oTItemDefinitionFormList = new ArrayList<OTItemDefinitionForm>();
		for (OTItemMaster otItemMaster : oTItemMasters) {
			OTItemDefinitionForm oTItemDefinitionForm = new OTItemDefinitionForm();
			oTItemDefinitionForm.setOtItemId(otItemMaster.getOtItemId());
			oTItemDefinitionForm.setName(otItemMaster.getOtItemName());
			oTItemDefinitionForm.setCode(otItemMaster.getCode());
			oTItemDefinitionForm.setInstruction(otItemMaster.getOtItemDesc());
			oTItemDefinitionForm.setVisibleOrHidden(otItemMaster
					.getVisibility());
			oTItemDefinitionFormList.add(oTItemDefinitionForm);
		}

		OTItemDefinitionResponse otItemDefinitionResponse = new OTItemDefinitionResponse();

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(1);
			}

			otItemDefinitionResponse.setPage(pageDTO.getPageNumber());
			otItemDefinitionResponse.setTotal(totalPages);

			otItemDefinitionResponse.setRecords(recordSize);
		}
		otItemDefinitionResponse.setRows(oTItemDefinitionFormList);
		return otItemDefinitionResponse;

	}

	public void updateOTItem(OTItemDefinitionForm otItemDefinitionForm,
			Long otItemId) {
		OTItemMaster otItemMaster = oTItemMasterDAO.findById(otItemId);
		otItemMaster.setOtItemName(otItemDefinitionForm.getName());
		otItemMaster.setCode(otItemDefinitionForm.getCode());
		otItemMaster.setOtItemDesc(otItemDefinitionForm.getInstruction());
		otItemMaster.setVisibility(otItemDefinitionForm.getVisibleOrHidden());
		oTItemMasterDAO.update(otItemMaster);

	}

	public void saveOTItem(OTItemDefinitionForm otItemDefinitionForm,
			Long companyId) {
		OTItemMaster otItemMaster = new OTItemMaster();
		otItemMaster.setOtItemName(otItemDefinitionForm.getName());
		otItemMaster.setCode(otItemDefinitionForm.getCode());
		otItemMaster.setOtItemDesc(otItemDefinitionForm.getInstruction());
		otItemMaster.setVisibility(otItemDefinitionForm.getVisibleOrHidden());
		Company company = companyDAO.findById(companyId);
		otItemMaster.setCompany(company);
		oTItemMasterDAO.save(otItemMaster);
	}

	public void deleteOTItem(Long otItemId) {
		OTItemMaster otItemMaster = oTItemMasterDAO.findById(otItemId);
		oTItemMasterDAO.delete(otItemMaster);
	}

	@Override
	public OTItemDefinitionForm checkOTItem(Long companyId,
			OTItemDefinitionForm otItemDefinitionForm, Long itemId) {
		OTItemDefinitionForm oTItemDefinitionForm = new OTItemDefinitionForm();
		OTItemMaster oTItemMaster = oTItemMasterDAO
				.findByCompanyIdItemNameItemId(companyId,
						otItemDefinitionForm.getName(), itemId);

		if (oTItemMaster != null) {
			oTItemDefinitionForm.setStatus("failure");

		} else {
			oTItemDefinitionForm.setStatus("success");

		}

		return oTItemDefinitionForm;
	}
}
