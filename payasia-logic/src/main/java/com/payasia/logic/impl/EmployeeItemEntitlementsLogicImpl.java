package com.payasia.logic.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.payasia.common.dto.EmployeeItemEntitlementDetailsDTO;
import com.payasia.common.dto.ImportItemEntitlementTemplateDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.EmployeeItemEntitlementForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.ClaimItemEntertainmentDAO;
import com.payasia.dao.ClaimItemMasterDAO;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.ClaimTemplateItemDAO;
import com.payasia.dao.ClaimTemplateItemGeneralDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.ClaimItemEntertainment;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.Company;
import com.payasia.logic.EmployeeUtemEntitlementsLogic;

@Service
public class EmployeeItemEntitlementsLogicImpl implements EmployeeUtemEntitlementsLogic {
	@Resource
	ClaimTemplateDAO claimTemplateDAO;
	@Resource
	ClaimItemEntertainmentDAO claimItemEntertainmentDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	ClaimItemMasterDAO claimItemMasterDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	ClaimTemplateItemDAO claimTemplateItemDAO;
	@Resource
	ClaimTemplateItemGeneralDAO claimTemplateItemGeneralDAO;

	private static final Logger LOGGER = Logger.getLogger(EmployeeItemEntitlementsLogicImpl.class);

	@Override
	public EmployeeItemEntitlementDetailsDTO getEmpItemEntitlementDetails(String templateName, Long companyId) {
		Map<Long, String> itemsList = new HashMap<Long, String>();
		EmployeeItemEntitlementDetailsDTO employeeItemEntitlementDetailsDTO = new EmployeeItemEntitlementDetailsDTO();
		ClaimTemplate claimTemplate = claimTemplateDAO.findByName(templateName, companyId);
		for (ClaimTemplateItem claimTemplateItem : claimTemplate.getClaimTemplateItems()) {
			itemsList.put(FormatPreserveCryptoUtil.encrypt(claimTemplateItem.getClaimTemplateItemId()),
					claimTemplateItem.getClaimItemMaster().getClaimItemName());
		}
		employeeItemEntitlementDetailsDTO.setItemsList(itemsList);
		return employeeItemEntitlementDetailsDTO;
	}

	@Override
	public void saveItemEntitlementDetails(Long companyId, EmployeeItemEntitlementForm employeeItemEntitlementForm) {

		ClaimItemEntertainment claimItemEntertainment = claimItemEntertainmentDAO.findByCondition(""+
				FormatPreserveCryptoUtil.decrypt(Long.parseLong(employeeItemEntitlementForm.getLitemEntitlementClaimTemplateTypeId())),
				employeeDAO.findByNumber(employeeItemEntitlementForm.getEmployeeNumber(), companyId), companyId);
		if (claimItemEntertainment == null) {
			claimItemEntertainment = new ClaimItemEntertainment();
			setClaimItemEntertainmentDetails(claimItemEntertainment, employeeItemEntitlementForm, companyId);
			claimItemEntertainmentDAO.save(claimItemEntertainment);
		} else {
			setClaimItemEntertainmentDetails(claimItemEntertainment, employeeItemEntitlementForm, companyId);
			claimItemEntertainmentDAO.update(claimItemEntertainment);
		}

	}

	private ClaimItemEntertainment setClaimItemEntertainmentDetails(ClaimItemEntertainment claimItemEntertainment,
			EmployeeItemEntitlementForm employeeItemEntitlementForm, Long companyId) {
		claimItemEntertainment.setAmount(employeeItemEntitlementForm.getItemEntitlementAmount());
		claimItemEntertainment
				.setEmployee(employeeDAO.findByNumber(employeeItemEntitlementForm.getEmployeeNumber(), companyId));
		claimItemEntertainment.setCompany(companyDAO.findById(companyId));
		claimItemEntertainment.setClaimTemplateItem(claimTemplateItemDAO
				.findById(FormatPreserveCryptoUtil.decrypt(Long.valueOf(employeeItemEntitlementForm.getLitemEntitlementClaimTemplateTypeId()))));
		claimItemEntertainment
				.setStartDate(DateUtils.stringToTimestamp(employeeItemEntitlementForm.getItemEntitlementFromDate()));
		claimItemEntertainment
				.setEndDate(DateUtils.stringToTimestamp(employeeItemEntitlementForm.getItemEntitlementToDate()));
		claimItemEntertainment.setReason(employeeItemEntitlementForm.getItemEntitlementReason());
		claimItemEntertainment.setForfeitAtEndDate(employeeItemEntitlementForm.isItemEntitlementforfeitAtEndDate());
		return claimItemEntertainment;
	}

	@Override
	public EmployeeItemEntitlementForm getItemDetails(String itemId, String employeeNo,
			String itemEntitlementlaimTemplate, Long companyId) {
		ClaimTemplateItemGeneral claimTemplateItemGeneral = claimTemplateItemGeneralDAO.findByItemId(itemId);
		ClaimItemEntertainment claimItemEntertainment = claimItemEntertainmentDAO.findByCondition(itemId,
				employeeDAO.findByNumber(employeeNo, companyId), companyId);
		EmployeeItemEntitlementForm itemEntitlementForm = new EmployeeItemEntitlementForm();
		if (claimItemEntertainment != null) {

			if (claimItemEntertainment.getAmount() != null) {
				itemEntitlementForm.setItemEntitlementAmount(claimItemEntertainment.getAmount());
			}
			if (claimItemEntertainment.getReason() != null) {
				itemEntitlementForm.setItemEntitlementReason(claimItemEntertainment.getReason());
			}

			itemEntitlementForm.setItemEntitlementforfeitAtEndDate(claimItemEntertainment.isForfeitAtEndDate());
			;

			if (claimItemEntertainment.getStartDate() != null) {
				itemEntitlementForm
						.setItemEntitlementFromDate(DateUtils.dateToString(claimItemEntertainment.getStartDate()));
			}
			if (claimItemEntertainment.getEndDate() != null) {
				itemEntitlementForm
						.setItemEntitlementToDate(DateUtils.dateToString(claimItemEntertainment.getEndDate()));
			}

		}
		if (claimTemplateItemGeneral != null) {
			if (claimTemplateItemGeneral.getEntitlementPerDay() != null
					&& ((claimTemplateItemGeneral.getEntitlementPerMonth()).compareTo(BigDecimal.ZERO) > 0)) {
				itemEntitlementForm.setItemEntitlementClaimBalances(
						"" + claimTemplateItemGeneral.getEntitlementPerDay().setScale(2, BigDecimal.ROUND_UP));
			} else if (claimTemplateItemGeneral.getEntitlementPerMonth() != null
					&& !claimTemplateItemGeneral.getEntitlementPerMonth().equals(0)
					&& ((claimTemplateItemGeneral.getEntitlementPerMonth()).compareTo(BigDecimal.ZERO) > 0)) {

				itemEntitlementForm.setItemEntitlementClaimBalances(
						"" + claimTemplateItemGeneral.getEntitlementPerMonth().setScale(2, BigDecimal.ROUND_UP));
			} else if (claimTemplateItemGeneral.getEntitlementPerYear() != null
					&& !claimTemplateItemGeneral.getEntitlementPerYear().equals(0)
					&& ((claimTemplateItemGeneral.getEntitlementPerYear()).compareTo(BigDecimal.ZERO) > 0)) {
				itemEntitlementForm.setItemEntitlementClaimBalances(
						"" + claimTemplateItemGeneral.getEntitlementPerYear().setScale(2, BigDecimal.ROUND_UP));
			}
		}
		return itemEntitlementForm;

	}

	@Override
	public void importItemEntitlementTemplate(Long companyId, AssignClaimTemplateForm importAssignClaimTemplateForm) {

		boolean isItemSaved = false;
		try {
			Company companyVO = companyDAO.findById(companyId);
			String fileName = importAssignClaimTemplateForm.getFileUpload().getOriginalFilename();
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			List<HashMap<String, ImportItemEntitlementTemplateDTO>> empClaimTemplateMapList = null;
			if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
				empClaimTemplateMapList = ExcelUtils.getimportItemEntitlementTemplateFromXLS(
						importAssignClaimTemplateForm.getFileUpload(), companyVO.getDateFormat());
			} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
				empClaimTemplateMapList = ExcelUtils.getimportItemEntitlementTemplateFromXLSX(
						importAssignClaimTemplateForm.getFileUpload(), companyVO.getDateFormat());
			}
			for (HashMap<String, ImportItemEntitlementTemplateDTO> empClaimTemplate : empClaimTemplateMapList) {

				Set<String> keySet = empClaimTemplate.keySet();
				for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					ImportItemEntitlementTemplateDTO importItemEntitlementTemplateDTO = empClaimTemplate.get(key);
					ClaimTemplate claimTemplate = null;
					if (importItemEntitlementTemplateDTO.getClaimTemplateName() != null) {
						claimTemplate = claimTemplateDAO
								.findByName(importItemEntitlementTemplateDTO.getClaimTemplateName(), companyId);
					}
					if (claimTemplate == null) {
						continue;
					}
					ClaimItemEntertainment claimItemEntertainment = new ClaimItemEntertainment();
					for (ClaimTemplateItem claimTemplateItem : claimTemplate.getClaimTemplateItems()) {
						if (claimTemplateItem.getClaimItemMaster().getClaimItemName()
								.equalsIgnoreCase(importItemEntitlementTemplateDTO.getClaimTemplateitemName())) {
							claimItemEntertainment.setClaimTemplateItem(claimTemplateItemDAO
									.findById(Long.valueOf(claimTemplateItem.getClaimTemplateItemId())));
						}
					}
					if (claimItemEntertainment.getClaimTemplateItem() == null) {
						continue;
					}
					claimItemEntertainment.setAmount(importItemEntitlementTemplateDTO.getAmount());
					claimItemEntertainment.setEmployee(employeeDAO
							.findByNumber(importItemEntitlementTemplateDTO.getEmployeeNumber().trim(), companyId));
					claimItemEntertainment.setCompany(companyDAO.findById(companyId));
					claimItemEntertainment.setStartDate(DateUtils.stringToTimestamp(
							importItemEntitlementTemplateDTO.getFromDateString(), companyVO.getDateFormat()));
					claimItemEntertainment.setEndDate(DateUtils.stringToTimestamp(
							importItemEntitlementTemplateDTO.getToDateString(), companyVO.getDateFormat()));

					claimItemEntertainment.setReason(importItemEntitlementTemplateDTO.getReason());
					claimItemEntertainment.setForfeitAtEndDate(importItemEntitlementTemplateDTO.isForfeitAtEndDate());

					ClaimItemEntertainment claimItemEntertainmentVO = claimItemEntertainmentDAO.findByCondition(
							claimItemEntertainment.getClaimTemplateItem().getClaimTemplateItemId(),
							claimItemEntertainment.getEmployee().getEmployeeId(), companyId);

					if (claimItemEntertainmentVO == null) {

						claimItemEntertainmentDAO.save(claimItemEntertainment);
					} else {
						claimItemEntertainment
								.setClaimItemEntertainmentId(claimItemEntertainmentVO.getClaimItemEntertainmentId());
						claimItemEntertainmentDAO.update(claimItemEntertainment);
					}
					isItemSaved = true;
				}

			}
			if (!isItemSaved) {
				LOGGER.error("No Item Saved. May be template mismatch.");
				throw new PayAsiaSystemException("Template_Mismatch", "No Item Saved.");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteItemEntitlementDetails(Long companyId, EmployeeItemEntitlementForm itemEntitlementForm) {
		claimItemEntertainmentDAO.delete(
				employeeDAO.findByNumber(itemEntitlementForm.getEmployeeNumber(), companyId).getEmployeeId(),
				FormatPreserveCryptoUtil.decrypt(Long.valueOf(itemEntitlementForm.getLitemEntitlementClaimTemplateTypeId())));

	}
}
