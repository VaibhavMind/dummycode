package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.payasia.common.form.CompanyGroupForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.logic.CompanyGroupLogic;

@Component
public class CompanyGroupLogicImpl implements CompanyGroupLogic {

	@Resource
	CompanyGroupDAO companyGroupDAO;

	@Override
	public CompanyGroupForm viewGroup(PageRequest pageDTO,
			SortCondition sortDTO, Long companyID) {
		List<CompanyGroupForm> companyGroupFormFormList = new ArrayList<CompanyGroupForm>();

		int recordSize = companyGroupDAO.getCompanyGroups(null, null).size();

		List<CompanyGroup> companyGroupVOList = companyGroupDAO
				.getCompanyGroups(pageDTO, sortDTO);

		for (CompanyGroup companyGroupVO : companyGroupVOList) {
			CompanyGroupForm companyGroupForm = new CompanyGroupForm();
			companyGroupForm.setGroupId(companyGroupVO.getGroupId());
			companyGroupForm.setGroupName(companyGroupVO.getGroupName());
			companyGroupForm.setGroupDesc(companyGroupVO.getGroupDesc());
			companyGroupForm.setGroupCode(companyGroupVO.getGroupCode());
			companyGroupFormFormList.add(companyGroupForm);

		}

		CompanyGroupForm response = new CompanyGroupForm();
		response.setRows(companyGroupFormFormList);

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
	public void addCompanyGroup(CompanyGroupForm companyGroupForm) {
		CompanyGroup companyGroup = new CompanyGroup();
		companyGroup.setGroupCode(companyGroupForm.getGroupCode());
		companyGroup.setGroupDesc(companyGroupForm.getGroupDesc());
		companyGroup.setGroupName(companyGroupForm.getGroupName());
		companyGroupDAO.save(companyGroup);

	}

	@Override
	public CompanyGroupForm getCompanyGroupById(Long groupId) {
		CompanyGroup companyGroup = companyGroupDAO.findById(groupId);
		CompanyGroupForm companyGroupForm = new CompanyGroupForm();
		companyGroupForm.setGroupId(companyGroup.getGroupId());
		companyGroupForm.setGroupDesc(companyGroup.getGroupDesc());
		companyGroupForm.setGroupName(companyGroup.getGroupName());
		companyGroupForm.setGroupCode(companyGroup.getGroupCode());
		return companyGroupForm;

	}

	@Override
	public void upadateCompanyGroup(CompanyGroupForm companyGroupForm) {

		CompanyGroup companyGroup = companyGroupDAO.findById(companyGroupForm
				.getGroupId());
		companyGroup.setGroupCode(companyGroupForm.getGroupCode());
		companyGroup.setGroupDesc(companyGroupForm.getGroupDesc());
		companyGroup.setGroupName(companyGroupForm.getGroupName());
		companyGroupDAO.update(companyGroup);
	}

	@Override
	public void deleteClaimCategory(Long groupId) {
		CompanyGroup companyGroup = companyGroupDAO.findById(groupId);
		companyGroupDAO.delete(companyGroup);

	}

	@Override
	public CompanyGroupForm checkGroup(CompanyGroupForm companyGroupForm) {
		CompanyGroupForm companyGroupFormDTO = new CompanyGroupForm();
		CompanyGroup companyGroupVO = companyGroupDAO.findByGroupCodeGroupId(
				companyGroupForm.getGroupCode(), null);

		if (companyGroupVO != null) {
			companyGroupFormDTO.setStatus("available");

		} else {
			companyGroupFormDTO.setStatus("notavailable");

		}

		return companyGroupFormDTO;
	}

	@Override
	public CompanyGroupForm checkGroupUpdate(CompanyGroupForm companyGroupForm) {
		CompanyGroupForm companyGroupFormDTO = new CompanyGroupForm();
		CompanyGroup companyGroupVO = companyGroupDAO.findByGroupCodeGroupId(
				companyGroupForm.getGroupCode(), companyGroupForm.getGroupId());

		if (companyGroupVO != null) {
			companyGroupFormDTO.setStatus("available");

		} else {
			companyGroupFormDTO.setStatus("notavailable");

		}

		return companyGroupFormDTO;
	}
}
