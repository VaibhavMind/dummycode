package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimItemDTO;
import com.payasia.common.dto.ClaimTemplateConditionDTO;
import com.payasia.common.form.ClaimItemForm;
import com.payasia.common.form.ClaimItemResponse;
import com.payasia.common.form.ClaimItemsCategoryForm;
import com.payasia.common.form.ClaimItemsCategoryResponse;
import com.payasia.common.form.ClaimTemplateForm;
import com.payasia.common.form.ClaimTemplateResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimCategoryDAO;
import com.payasia.dao.ClaimItemMasterDAO;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.ClaimTemplateItemDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.logic.ClaimItemDefLogic;

@Component
public class ClaimItemDefLogicImpl implements ClaimItemDefLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(ClaimItemDefLogicImpl.class);

	@Resource
	ClaimItemMasterDAO claimItemMasterDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	ClaimCategoryDAO claimCategoryDAO;

	@Resource
	ClaimTemplateDAO claimTemplateDAO;

	@Resource
	ClaimTemplateItemDAO claimTemplateItemDAO;

	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Autowired
	private MessageSource messageSource;

	@Override
	public ClaimItemResponse viewClaimItems(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {
		String edit = "payasia.edit";
		String template = "payasia.template";
		ClaimItemDTO claimItemDTO = new ClaimItemDTO();

		if (PayAsiaConstants.NAME.equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				claimItemDTO.setName("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (PayAsiaConstants.CODE.equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				claimItemDTO.setCode("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (PayAsiaConstants.DESCRIPTION.equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				claimItemDTO.setDescription("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (PayAsiaConstants.CLAIM_CATEGORY.equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				claimItemDTO.setClaimCategory("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (PayAsiaConstants.ACCOUNT_CODE.equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				claimItemDTO.setAccountCode("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (PayAsiaConstants.VISIBILITY.equals(claimDTO.getSearchCondition())) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				claimItemDTO.setVisibility(claimDTO.getSearchText());
			}
		}

		List<ClaimItemMaster> claimItemMasterVOList;
		
		claimItemMasterVOList = claimItemMasterDAO.getClaimItemByCondition(pageDTO, sortDTO, claimItemDTO,
				claimDTO.getCompanyId());

		List<ClaimItemForm> claimItemFormList = new ArrayList<ClaimItemForm>();

		for (ClaimItemMaster claimItemMasterVO : claimItemMasterVOList) {
			ClaimItemForm claimItemForm = new ClaimItemForm();
			long claimItemId =FormatPreserveCryptoUtil.encrypt(claimItemMasterVO.getClaimItemId());
			claimItemForm.setAccountCode(claimItemMasterVO.getAccountCode());
			claimItemForm.setClaimCategory(claimItemMasterVO.getClaimCategoryMaster().getClaimCategoryName());
			claimItemForm.setCode(claimItemMasterVO.getCode());
			claimItemForm.setDescription(claimItemMasterVO.getClaimItemDesc());
			claimItemForm.setItemId(claimItemId);

			String claimItemName = "<span class='jqGridColumnHighlight'>";
			claimItemName += claimItemMasterVO.getClaimItemName();
			claimItemName += "</span>";
			claimItemForm.setName(claimItemName);

			if (claimItemMasterVO.getVisibility() == true) {
				claimItemForm.setVisibility("Yes");
			} else {
				claimItemForm.setVisibility("No");
			}

			String claimTemplateCount = "<span class='Text'><h2>"
					+ String.valueOf(claimItemMasterVO.getClaimTemplateItems().size()) + "</h2></span>";
			claimTemplateCount += "<span class='Textsmall' style='padding-top: 5px;'>&nbsp;"
					+ messageSource.getMessage(template, new Object[] {}, claimDTO.getLocale()) + "</span>";
			claimTemplateCount += "<br><br>	";
			claimTemplateCount += "<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'viewAssignedClaimTemplates("
					+ claimItemId + ")'>["
					+ messageSource.getMessage(edit, new Object[] {}, claimDTO.getLocale()) + "]</a></span>";

			claimItemForm.setClaimTemplateCount(claimTemplateCount);

			claimItemFormList.add(claimItemForm);
		}

		ClaimItemResponse response = new ClaimItemResponse();
		response.setRows(claimItemFormList);

		return response;

	}

	@Override
	public List<ClaimItemForm> getCategoryList(Long companyId) {
		List<ClaimItemForm> claimItemFormList = new ArrayList<ClaimItemForm>();

		List<ClaimCategoryMaster> claimCatMasterVOList = claimCategoryDAO.getCategoryAll(companyId);

		for (ClaimCategoryMaster claimCatMasterVO : claimCatMasterVOList) {
			ClaimItemForm ClaimItemForm = new ClaimItemForm();
			ClaimItemForm.setCategoryId(FormatPreserveCryptoUtil.encrypt(claimCatMasterVO.getClaimCategoryId()));
			ClaimItemForm.setClaimCategory(claimCatMasterVO.getClaimCategoryName());
			claimItemFormList.add(ClaimItemForm);
		}
		return claimItemFormList;
	}

	@Override
	public synchronized String addClaimItem(ClaimItemForm claimItemForm, Long companyId) {
		ClaimItemMaster claimItemMasterVO = new ClaimItemMaster();

		Company company = companyDAO.findById(companyId);
		claimItemMasterVO.setCompany(company);

		ClaimCategoryMaster claimCatMaster = claimCategoryDAO.findByClaimCategoryId(FormatPreserveCryptoUtil.decrypt(claimItemForm.getCategoryId()),companyId);
		claimItemMasterVO.setClaimCategoryMaster(claimCatMaster);
		claimItemMasterVO.setAccountCode(claimItemForm.getAccountCode());
		try {
			claimItemMasterVO.setClaimItemDesc(URLDecoder.decode(claimItemForm.getDescription(), "UTF-8"));
			claimItemMasterVO.setClaimItemName(URLDecoder.decode(claimItemForm.getName(), "UTF-8"));
			claimItemMasterVO.setCode(URLDecoder.decode(claimItemForm.getCode(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		claimItemMasterVO.setSortOrder(claimItemMasterDAO.getMaxClaimTypeSortOrder(companyId) + 1);
		claimItemMasterVO.setVisibility(Boolean.parseBoolean(claimItemForm.getVisibility()));

		String duplicateFieldInDB = checkExistInDBForItem(null, FormatPreserveCryptoUtil.decrypt(claimItemForm.getCategoryId()), claimItemForm.getName(),
				claimItemForm.getCode(), companyId);

		if (duplicateFieldInDB == null) {
			claimItemMasterDAO.saveClaimItem(claimItemMasterVO);
			return "Unique entry".toLowerCase();
		} else {
			return duplicateFieldInDB.toLowerCase();
		}

	}

	String checkExistInDBForItem(Long claimItemId, Long categoryId, String itemName, String itemCode, Long companyId) {
		ClaimItemMaster claimItemMasterVO = claimItemMasterDAO.findByNameCategoryCompany(claimItemId, itemName,
				categoryId, companyId);
		if (claimItemMasterVO != null) {
			return "name";
		} else {
			claimItemMasterVO = claimItemMasterDAO.findByCodeCategoryCompany(claimItemId, itemCode, categoryId,
					companyId);
			if (claimItemMasterVO != null) {
				return "code";
			} else
				return null;
		}
	}

	@Override
	public ClaimItemsCategoryResponse viewClaimCategory(PageRequest pageDTO, SortCondition sortDTO, Long companyID) {
		List<ClaimItemsCategoryForm> claimCatFormList = new ArrayList<ClaimItemsCategoryForm>();

		int recordSize = claimCategoryDAO.getClaimCategory(null, null, companyID).size();

		List<ClaimCategoryMaster> CategoryMasterVOList = claimCategoryDAO.getClaimCategory(pageDTO, sortDTO, companyID);

		for (ClaimCategoryMaster claimCatMasterVO : CategoryMasterVOList) {
			ClaimItemsCategoryForm claimCatForm = new ClaimItemsCategoryForm();

			claimCatForm.setName(claimCatMasterVO.getClaimCategoryName());
			claimCatForm.setCategoryId(FormatPreserveCryptoUtil.encrypt(claimCatMasterVO.getClaimCategoryId()));
			claimCatForm.setDescription(claimCatMasterVO.getClaimCategoryDesc());
			claimCatForm.setCode(claimCatMasterVO.getCode());

			claimCatFormList.add(claimCatForm);
		}

		ClaimItemsCategoryResponse response = new ClaimItemsCategoryResponse();
		response.setRows(claimCatFormList);

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
	public String addClaimCategory(ClaimItemsCategoryForm claimCatForm, Long companyId) {

		ClaimCategoryMaster claimCatMasterVO = new ClaimCategoryMaster();

		Company company = companyDAO.findById(companyId);
		claimCatMasterVO.setCompany(company);

		try {
			claimCatMasterVO.setClaimCategoryName(URLDecoder.decode(claimCatForm.getName(), "UTF-8"));
			claimCatMasterVO.setClaimCategoryDesc(URLDecoder.decode(claimCatForm.getDescription(), "UTF-8"));
			claimCatMasterVO.setCode(URLDecoder.decode(claimCatForm.getCode(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		String duplicateFieldInDB = checkExistInDBForCategory(null, claimCatForm.getName(), claimCatForm.getCode(),
				companyId);

		if (duplicateFieldInDB == null) {
			claimCategoryDAO.saveClaimCat(claimCatMasterVO);
			return "unique entry".toLowerCase();
		} else
			return duplicateFieldInDB.toLowerCase();
	}

	String checkExistInDBForCategory(Long categoryId, String categoryName, String categoryCode, Long companyId) {
		ClaimCategoryMaster claimCatMasterVO = claimCategoryDAO.findByNameCompany(categoryId, categoryName, companyId);
		if (claimCatMasterVO != null) {
			return "name";
		} else {
			claimCatMasterVO = claimCategoryDAO.findByCodeCompany(categoryId, categoryCode, companyId);
			if (claimCatMasterVO != null) {
				return "code";
			} else
				return null;
		}
	}

	@Override
	public ClaimItemForm getClaimItemById(Long claimItemId,Long companyId) {
		
		ClaimItemMaster claimItemMasterVO = claimItemMasterDAO.findByClaimItemMasterId(claimItemId,companyId);

		ClaimItemForm claimItemform = new ClaimItemForm();

		claimItemform.setAccountCode(claimItemMasterVO.getAccountCode());
		claimItemform.setCode(claimItemMasterVO.getCode());
		claimItemform.setDescription(claimItemMasterVO.getClaimItemDesc());
		claimItemform.setItemId(FormatPreserveCryptoUtil.encrypt(claimItemMasterVO.getClaimItemId()));
		claimItemform.setName(claimItemMasterVO.getClaimItemName());
		claimItemform.setCategoryId(FormatPreserveCryptoUtil.encrypt(claimItemMasterVO.getClaimCategoryMaster().getClaimCategoryId()));

		if (claimItemMasterVO.getVisibility() == true) {
			claimItemform.setVisibility("true");
		} else {
			claimItemform.setVisibility("false");
		}
		return claimItemform;
	}

	@Override
	public String editClaimItem(ClaimItemForm claimItemForm, Long companyId) {
		ClaimItemMaster claimItemMasterVO = new ClaimItemMaster();

		ClaimCategoryMaster claimCatMaster = claimCategoryDAO.findByClaimCategoryId(FormatPreserveCryptoUtil.decrypt(claimItemForm.getCategoryId()),companyId);

		Company company = companyDAO.findById(companyId);

		claimItemMasterVO.setClaimItemId(FormatPreserveCryptoUtil.decrypt(claimItemForm.getItemId()));
		claimItemMasterVO.setAccountCode(claimItemForm.getAccountCode());
		claimItemMasterVO.setClaimCategoryMaster(claimCatMaster);

		try {

			claimItemMasterVO.setClaimItemName(URLDecoder.decode(claimItemForm.getName(), "UTF-8"));
			claimItemMasterVO.setClaimItemDesc(URLDecoder.decode(claimItemForm.getDescription(), "UTF-8"));
			claimItemMasterVO.setCode(URLDecoder.decode(claimItemForm.getCode(), "UTF-8"));

		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		claimItemMasterVO.setVisibility(Boolean.parseBoolean(claimItemForm.getVisibility()));

		claimItemMasterVO.setCompany(company);

		String duplicateFieldInDB = checkExistInDBForItem(FormatPreserveCryptoUtil.decrypt(claimItemForm.getItemId()), FormatPreserveCryptoUtil.decrypt(claimItemForm.getCategoryId()),
				claimItemForm.getName(), claimItemForm.getCode(), companyId);

		if (duplicateFieldInDB == null) {
			claimItemMasterDAO.update(claimItemMasterVO);
			return "Unique entry".toLowerCase();
		} else {
			return duplicateFieldInDB.toLowerCase();
		}

	}

	@Override
	public ClaimItemsCategoryForm getClaimCatById(Long categoryId,Long companyId) {
		
		ClaimCategoryMaster claimCatMasterVO = claimCategoryDAO.findByClaimCategoryId(categoryId,companyId);

		ClaimItemsCategoryForm claimItemCatForm = new ClaimItemsCategoryForm();

		claimItemCatForm.setCategoryId(claimCatMasterVO.getClaimCategoryId());
		claimItemCatForm.setCode(claimCatMasterVO.getCode());
		claimItemCatForm.setDescription(claimCatMasterVO.getClaimCategoryDesc());
		claimItemCatForm.setName(claimCatMasterVO.getClaimCategoryName());

		return claimItemCatForm;
	}

	@Override
	public String editClaimCategory(ClaimItemsCategoryForm claimItemCatForm, Long companyId) {
		ClaimCategoryMaster claimCatMasterVO = new ClaimCategoryMaster();

		Company company = companyDAO.findById(companyId);

		claimCatMasterVO.setClaimCategoryId(FormatPreserveCryptoUtil.decrypt(claimItemCatForm.getCategoryId()));

		try {
			claimCatMasterVO.setClaimCategoryDesc(URLDecoder.decode(claimItemCatForm.getDescription(), "UTF-8"));
			claimCatMasterVO.setClaimCategoryName(URLDecoder.decode(claimItemCatForm.getName(), "UTF-8"));
			claimCatMasterVO.setCode(URLDecoder.decode(claimItemCatForm.getCode(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		claimCatMasterVO.setCompany(company);

		String duplicateFieldInDB = checkExistInDBForCategory(FormatPreserveCryptoUtil.decrypt(claimItemCatForm.getCategoryId()),
				claimItemCatForm.getName(), claimItemCatForm.getCode(), companyId);

		if (duplicateFieldInDB == null) {
			claimCategoryDAO.update(claimCatMasterVO);
			return "unique entry".toLowerCase();
		} else
			return duplicateFieldInDB.toLowerCase();
	}

	@Override
	public void deleteClaimItem(Long claimItemId,Long companyId) {
		ClaimItemMaster claimItemMaster = claimItemMasterDAO.findByClaimItemMasterId(claimItemId,companyId);
		claimItemMasterDAO.delete(claimItemMaster);
	}

	@Override
	public String deleteClaimCategory(Long categoryId,Long companyId) {
			ClaimCategoryMaster claimCatMaster = claimCategoryDAO.findByClaimCategoryId(categoryId,companyId);
			claimCategoryDAO.delete(claimCatMaster);
			return "deleted".toLowerCase();
	}

	@Override
	public ClaimTemplateResponse assignedClaimTemplates(Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			Long claimItemId, Locale locale) {
		ClaimTemplateConditionDTO conditionDTO = new ClaimTemplateConditionDTO();

		ClaimItemMaster claimItemMaster = claimItemMasterDAO.findByClaimItemMasterId(claimItemId,companyId);
		Set<ClaimTemplateItem> claimTemplateItemes = claimItemMaster.getClaimTemplateItems();
		List<ClaimTemplateForm> claimTemplateFormList = new ArrayList<ClaimTemplateForm>();
		for (ClaimTemplateItem claimTemplateItem : claimTemplateItemes) {

			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
			claimTemplateForm.setTemplateName(claimTemplateItem.getClaimTemplate().getTemplateName());
			claimTemplateForm.setClaimTemplateItemId(FormatPreserveCryptoUtil.encrypt(claimTemplateItem.getClaimTemplateItemId()));
			claimTemplateFormList.add(claimTemplateForm);

		}

		conditionDTO.setClaimItemId(FormatPreserveCryptoUtil.decrypt(claimItemId));

		ClaimTemplateResponse response = new ClaimTemplateResponse();
		int recordSize = claimTemplateDAO.getCountForAllClaimTemplate(companyId, conditionDTO);
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
		response.setRows(claimTemplateFormList);

		return response;
	}

	@Override
	public ClaimTemplateResponse unAssignedClaimTemplates(Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			Long claimItemId) {

		ClaimItemMaster claimItemMaster = claimItemMasterDAO.findByClaimItemMasterId(claimItemId,companyId);
		Set<ClaimTemplateItem> claimTemplateItemeVOs = claimItemMaster.getClaimTemplateItems();

		List<ClaimTemplate> assignedClaimTemplates = new ArrayList<>();
		for (ClaimTemplateItem claimTemplateItemVO : claimTemplateItemeVOs) {
			assignedClaimTemplates.add(claimTemplateItemVO.getClaimTemplate());
		}

		List<ClaimTemplate> unassignedClaimTemplates = claimTemplateDAO.getAllClaimTemplateCompany(companyId);

		unassignedClaimTemplates.removeAll(assignedClaimTemplates);

		List<ClaimTemplateForm> claimTemplateFormList = new ArrayList<ClaimTemplateForm>();
		for (ClaimTemplate claimTemplate : unassignedClaimTemplates) {

			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
			claimTemplateForm.setTemplateName(claimTemplate.getTemplateName());
			claimTemplateForm.setClaimTemplateId(FormatPreserveCryptoUtil.encrypt(claimTemplate.getClaimTemplateId()));
			claimTemplateFormList.add(claimTemplateForm);

		}

		ClaimTemplateResponse response = new ClaimTemplateResponse();

		int recordSize = unassignedClaimTemplates.size();
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
		response.setRows(claimTemplateFormList);

		return response;
	}

	@Override
	public void assignClaimTemplate(String[] claimTemplateId, Long claimItemId,Long companyId) {

		ClaimItemMaster claimItemMaster = claimItemMasterDAO.findByClaimItemMasterId(claimItemId,companyId);

		for (int count = 0; count < claimTemplateId.length; count++) {
			ClaimTemplateItem claimTemplateItem = new ClaimTemplateItem();
			ClaimTemplate claimTemplate = claimTemplateDAO.findByClaimTemplateID(FormatPreserveCryptoUtil.decrypt(Long.parseLong(claimTemplateId[count])),companyId);
			claimTemplateItem.setClaimItemMaster(claimItemMaster);
			claimTemplateItem.setClaimTemplate(claimTemplate);
			claimTemplateItem.setVisibility(true);
			ClaimTemplateItem persistClaimTemplateItem = claimTemplateItemDAO.saveReturn(claimTemplateItem);

			if (claimTemplate.getEmployeeClaimTemplates().size() > 0) {
				for (EmployeeClaimTemplate employeeClaimTemplate : claimTemplate.getEmployeeClaimTemplates()) {
					EmployeeClaimTemplateItem employeeClaimTemplateItem = new EmployeeClaimTemplateItem();
					employeeClaimTemplateItem.setClaimTemplateItem(persistClaimTemplateItem);
					employeeClaimTemplateItem.setEmployeeClaimTemplate(employeeClaimTemplate);
					employeeClaimTemplateItemDAO.save(employeeClaimTemplateItem);
				}
			}
		}
	}

	@Override
	public void deleteClaimTemplateItem(Long claimTemplateItemId,Long companyId) {

		ClaimTemplateItem claimTemplateItem = claimTemplateItemDAO.findByClaimTemplateItemId(claimTemplateItemId,companyId);
		claimTemplateItemDAO.delete(claimTemplateItem);

	}

	@Override
	public void updateClaimItemSortOrder(String[] sortOrder, Long companyId) {
		for (String sortOrd : sortOrder) {

			String[] sortValues = sortOrd.split("-");

			Long claimItemId = Long.parseLong(sortValues[0]);
			Integer pos = Integer.parseInt(sortValues[1]);

			ClaimItemMaster claimItemMaster = claimItemMasterDAO.findByClaimItemMasterId(claimItemId, companyId);
			claimItemMaster.setSortOrder(pos);
			claimItemMasterDAO.update(claimItemMaster);

		}

	}

}
