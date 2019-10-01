package com.payasia.logic.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.ClaimDetailsReportCustomDataDTO;
import com.payasia.common.dto.ClaimDetailsReportDTO;
import com.payasia.common.dto.ClaimDetailsReportDataDTO;
import com.payasia.common.dto.ClaimItemDTO;
import com.payasia.common.dto.ClaimReportHeaderDTO;
import com.payasia.common.dto.ClaimReviewerInfoReportDTO;
import com.payasia.common.dto.ClaimReviewerReportDataDTO;
import com.payasia.common.dto.ClaimTemplateDTO;
import com.payasia.common.dto.CustomFieldReportDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeHeadCountReportDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.EmployeeWiseConsolidatedClaimReportDTO;
import com.payasia.common.dto.EmployeeWiseConsolidatedClaimReportDataDTO;
import com.payasia.common.dto.EmployeeWiseTemplateClaimReportDTO;
import com.payasia.common.dto.EmployeeWiseTemplateClaimReportDataDTO;
import com.payasia.common.dto.MonthlyConsFinReportDTO;
import com.payasia.common.dto.MonthlyConsolidatedFinanceReportDTO;
import com.payasia.common.dto.MonthlyConsolidatedFinanceReportDataDTO;
import com.payasia.common.form.ClaimCategoryForm;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.ClaimReportsResponse;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.ClaimApplicationWorkflowDAO;
import com.payasia.dao.ClaimBatchMasterDAO;
import com.payasia.dao.ClaimCategoryDAO;
import com.payasia.dao.ClaimItemEntertainmentDAO;
import com.payasia.dao.ClaimItemMasterDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.ClaimTemplateItemDAO;
import com.payasia.dao.ClaimTemplateItemGeneralDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemCustomField;
import com.payasia.dao.bean.ClaimApplicationItemLundinDetail;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimBatchMaster;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimItemEntertainment;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.ClaimReportsLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class ClaimReportsLogicImpl implements ClaimReportsLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(ClaimReportsLogicImpl.class);
	
	@Autowired
	private MessageSource messageSource;
	
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	ClaimCategoryDAO claimCategoryDAO;
	@Resource
	ClaimTemplateDAO claimTemplateDAO;
	@Resource
	ClaimItemMasterDAO claimItemMasterDAO;
	@Resource
	ClaimBatchMasterDAO claimBatchMasterDAO;
	@Resource
	ClaimApplicationDAO claimApplicationDAO;
	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;
	@Resource
	ClaimTemplateItemDAO claimTemplateItemDAO;
	@Resource
	ClaimApplicationItemDAO claimApplicationItemDAO;
	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;
	@Resource
	ClaimApplicationWorkflowDAO claimApplicationWorkflowDAO;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	@Resource
	ClaimItemEntertainmentDAO claimItemEntertainmentDAO;
	@Resource
	ClaimTemplateItemGeneralDAO claimTemplateItemGeneralDAO;

	@Override
	public List<String> getClaimReportType() {
		List<String> claimReportsTypeList = new ArrayList<String>();
		claimReportsTypeList.add("Claim Transaction Report");
		return claimReportsTypeList;
	}

	@Override
	public List<ClaimTemplateDTO> getAllClaimTemplate(Long companyId) {
		List<ClaimTemplateDTO> claimTemplateDTOList = new ArrayList<ClaimTemplateDTO>();

		List<ClaimTemplate> claimTemplateVOList = claimTemplateDAO.getAllClaimTemplateCompany(companyId);

		if (claimTemplateVOList != null && !claimTemplateVOList.isEmpty()) {
			for (ClaimTemplate claimTemplateVO : claimTemplateVOList) {
				ClaimTemplateDTO claimTemplateDTO = new ClaimTemplateDTO();
				claimTemplateDTO.setClaimTemplateId("" + claimTemplateVO.getClaimTemplateId());
				claimTemplateDTO.setClaimTemplateName(claimTemplateVO.getTemplateName());
				claimTemplateDTOList.add(claimTemplateDTO);
			}
		}

		return claimTemplateDTOList;
	}

	@Override
	public List<ClaimItemDTO> getClaimItemListForClaimDetailsReport(Long companyId, Long[] claimTemplateIdArr) {

		List<ClaimItemDTO> claimItemDTOList = new ArrayList<ClaimItemDTO>();

		List<Long> multipleClaimTemplateIdList = Arrays.asList(claimTemplateIdArr);

		List<ClaimTemplateItem> claimTemplateItemList = claimTemplateItemDAO
				.findByCondition(multipleClaimTemplateIdList, companyId);
		if (claimTemplateItemList != null && !claimTemplateItemList.isEmpty()) {
			for (ClaimTemplateItem claimTemplateItemVO : claimTemplateItemList) {
				ClaimItemMaster claimItemMaster = claimTemplateItemVO.getClaimItemMaster();
				ClaimItemDTO claimItemDTO = new ClaimItemDTO();
				claimItemDTO.setClaimItemId(claimItemMaster.getClaimItemId());
				claimItemDTO.setClaimItemName(claimItemMaster.getClaimItemName());
				claimItemDTOList.add(claimItemDTO);
			}
		}
		Collections.sort(claimItemDTOList, new ClaimItemComp());
		return claimItemDTOList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClaimDetailsReportDTO showClaimTransactionReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds, boolean hasLundinTimesheetModule, Locale locale,
			Boolean isCheckedFromCreatedDate, MessageSource messageSource) {

		String sNoLocale = "payasia.serial.number";
		String claimNumberLocale = "payasia.add.claim.number";
		String claimTemplateLocale = "payasia.claim.item.claim.template";
		String receiptDateLocale = "payasia.add.claim.date";
		String employeeNumberLocale = "payasia.employee.number";
		String employeeNameLocale = "payasia.employee.name";
		String claimItemLocale = "payasia.claim.item.claim.items";
		String claimaintNameLocale = "payasia.claim.claimaint.name";
		String convClaimAmountLocale = "payasia.claim.conv.claim.amount";
		String amountBeforeTaxLocale = "payasia.add.claim.amount.before.tax";
		String convTaxAmountLocale = "payasia.claim.conv.tax.amount";
		String remarksLocale = "payasia.remarks";
		String blockLocale = "payasia.lundin.claim.block";
		String createdDateLocale = "payasia.add.claim.created.date";
		String claimSubmittedDateLocale = "payasia.add.claim.submitted.date";
		String claimApprovedForwardDateLocale = "payasia.add.claim.approved.forwarded.date";
		String claimApprovedForward2DateLocale = "payasia.add.claim.approved.forwarded2.date";
		String claimApprovedDateLocale = "payasia.add.claim.approved.date";
		String rejectedDateLocale = "payasia.add.claim.rejected.date";
		String companyCode = "payasia.add.claim.company.code";
		String forexRateLocale = "payasia.add.claim.forex.rate";
		String receiptAmountLocale = "payasia.add.claim.receipt.amount";

		ClaimDetailsReportDTO claimDetailsReportDTO = new ClaimDetailsReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		ClaimReportHeaderDTO serialNum = new ClaimReportHeaderDTO();
		serialNum.setmDataProp("serialNum");
		serialNum.setsTitle(messageSource.getMessage(sNoLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimNumber = new ClaimReportHeaderDTO();
		claimNumber.setmDataProp("claimNumber");
		claimNumber.setsTitle(messageSource.getMessage(claimNumberLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimTemplateName = new ClaimReportHeaderDTO();
		claimTemplateName.setmDataProp("claimTemplateName");
		claimTemplateName.setsTitle(messageSource.getMessage(claimTemplateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimDate = new ClaimReportHeaderDTO();
		claimDate.setmDataProp("claimDate");
		claimDate.setsTitle(messageSource.getMessage(receiptDateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimCreatedDate = new ClaimReportHeaderDTO();
		claimCreatedDate.setmDataProp("claimCreatedDate");
		claimCreatedDate.setsTitle(messageSource.getMessage(createdDateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimSubmittedDate = new ClaimReportHeaderDTO();
		claimSubmittedDate.setmDataProp("claimSubmittedDate");
		claimSubmittedDate.setsTitle(messageSource.getMessage(claimSubmittedDateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimApprovededForwardDate = new ClaimReportHeaderDTO();
		claimApprovededForwardDate.setmDataProp("claimApprovedForwardedDate");
		claimApprovededForwardDate
				.setsTitle(messageSource.getMessage(claimApprovedForwardDateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimApprovedForwarded2Date = new ClaimReportHeaderDTO();
		claimApprovedForwarded2Date.setmDataProp("claimApprovedForwarded2Date");
		claimApprovedForwarded2Date
				.setsTitle(messageSource.getMessage(claimApprovedForward2DateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimApprovedDate = new ClaimReportHeaderDTO();
		claimApprovedDate.setmDataProp("claimApprovedDate");
		claimApprovedDate.setsTitle(messageSource.getMessage(claimApprovedDateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO rejectedDate = new ClaimReportHeaderDTO();
		rejectedDate.setmDataProp("rejectedDate");
		rejectedDate.setsTitle(messageSource.getMessage(rejectedDateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle(messageSource.getMessage(employeeNumberLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle(messageSource.getMessage(employeeNameLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimItem = new ClaimReportHeaderDTO();
		claimItem.setmDataProp("claimItemName");
		claimItem.setsTitle(messageSource.getMessage(claimItemLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
		claimantName.setmDataProp("claimantName");
		claimantName.setsTitle(messageSource.getMessage(claimaintNameLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO convClaimAmount = new ClaimReportHeaderDTO();
		convClaimAmount.setmDataProp("convClaimAmount");
		convClaimAmount.setsTitle(messageSource.getMessage(convClaimAmountLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO convClaimAmountCurrency = new ClaimReportHeaderDTO();
		convClaimAmountCurrency.setmDataProp("convClaimAmountCurrency");
		convClaimAmountCurrency.setsTitle("Conv Curr Name");

		ClaimReportHeaderDTO amountApplicable = new ClaimReportHeaderDTO();
		amountApplicable.setmDataProp("amountApplicable");
		amountApplicable.setsTitle("Applicable Amount");

		ClaimReportHeaderDTO amountBeforeTax = new ClaimReportHeaderDTO();
		amountBeforeTax.setmDataProp("amountBeforeTax");
		amountBeforeTax.setsTitle(messageSource.getMessage(amountBeforeTaxLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO amountBeforeTaxCurrency = new ClaimReportHeaderDTO();
		amountBeforeTaxCurrency.setmDataProp("amountBeforeTaxCurrency");
		amountBeforeTaxCurrency.setsTitle("Conv Curr Name2");

		ClaimReportHeaderDTO convTaxAmount = new ClaimReportHeaderDTO();
		convTaxAmount.setmDataProp("convTaxAmount");
		convTaxAmount.setsTitle(messageSource.getMessage(convTaxAmountLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO convTaxAmountCurrency = new ClaimReportHeaderDTO();
		convTaxAmountCurrency.setmDataProp("convTaxAmountCurrency");
		convTaxAmountCurrency.setsTitle("Conv Curr Name3");

		ClaimReportHeaderDTO remarks = new ClaimReportHeaderDTO();
		remarks.setmDataProp("remarks");
		remarks.setsTitle(messageSource.getMessage(remarksLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO status = new ClaimReportHeaderDTO();
		status.setmDataProp("status");
		status.setsTitle("Claim Status");

		ClaimReportHeaderDTO block = new ClaimReportHeaderDTO();
		block.setmDataProp("block");
		block.setsTitle(messageSource.getMessage(blockLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO afe = new ClaimReportHeaderDTO();
		afe.setmDataProp("afe");
		afe.setsTitle("AFE");

		ClaimReportHeaderDTO companyCodes = new ClaimReportHeaderDTO();
		companyCodes.setmDataProp("companyCodes");
		companyCodes.setsTitle(messageSource.getMessage(companyCode, new Object[] {}, locale));

		ClaimReportHeaderDTO exchangeRate = new ClaimReportHeaderDTO();
		exchangeRate.setmDataProp("exchangeRate");
		exchangeRate.setsTitle(messageSource.getMessage(forexRateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO receiptAmount = new ClaimReportHeaderDTO();
		receiptAmount.setmDataProp("receiptAmount");
		receiptAmount.setsTitle(messageSource.getMessage(receiptAmountLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO receiptAmountCurrency = new ClaimReportHeaderDTO();
		receiptAmountCurrency.setmDataProp("receiptAmountCurrency");
		receiptAmountCurrency.setsTitle("Currency Name");

		claimHeaderDTOs.add(serialNum);
		claimHeaderDTOs.add(claimNumber);
		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(employeeName);

		claimHeaderDTOs.add(claimTemplateName);
		claimHeaderDTOs.add(claimItem);
		claimHeaderDTOs.add(status);

		if (hasLundinTimesheetModule) {
			claimHeaderDTOs.add(block);
			claimHeaderDTOs.add(afe);
		}

		claimHeaderDTOs.add(claimDate);

		claimHeaderDTOs.add(claimCreatedDate);
		claimHeaderDTOs.add(claimSubmittedDate);
		claimHeaderDTOs.add(claimApprovededForwardDate);
		claimHeaderDTOs.add(claimApprovedForwarded2Date);
		claimHeaderDTOs.add(claimApprovedDate);
		claimHeaderDTOs.add(rejectedDate);

		claimHeaderDTOs.add(convClaimAmount);
		claimHeaderDTOs.add(convClaimAmountCurrency);

		claimHeaderDTOs.add(amountApplicable);
		claimHeaderDTOs.add(amountBeforeTax);
		claimHeaderDTOs.add(amountBeforeTaxCurrency);
		claimHeaderDTOs.add(convTaxAmount);
		claimHeaderDTOs.add(convTaxAmountCurrency);
		claimHeaderDTOs.add(receiptAmount);
		claimHeaderDTOs.add(receiptAmountCurrency);
		claimHeaderDTOs.add(exchangeRate);
		claimHeaderDTOs.add(claimantName);

		claimHeaderDTOs.add(remarks);

		if (claimReportsForm.isIncludeSubordinateEmployees()) {
			claimHeaderDTOs.add(companyCodes);
		}

		List<Long> multipleClaimTemplateIdList = new ArrayList<Long>();
		List<Long> multipleClaimItemIdList = new ArrayList<Long>();

		for (int count = 0; count < claimReportsForm.getMultipleClaimTemplateId().length; count++) {
			if (claimReportsForm.getMultipleClaimTemplateId()[count] != null
					&& claimReportsForm.getMultipleClaimTemplateId()[count] != 0) {
				multipleClaimTemplateIdList.add(claimReportsForm.getMultipleClaimTemplateId()[count]);
			}
		}

		for (int count = 0; count < claimReportsForm.getMultipleClaimItemId().length; count++) {
			if (claimReportsForm.getMultipleClaimItemId()[count] != null
					&& claimReportsForm.getMultipleClaimItemId()[count] != 0) {
				multipleClaimItemIdList.add(claimReportsForm.getMultipleClaimItemId()[count]);
			}
		}

		List<ClaimApplicationItem> claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		if (claimReportsForm.getIsShortList()) {
			claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		} else {
			List<String> claimStatusList = new ArrayList<>();
			if (StringUtils.isNotBlank(claimReportsForm.getStatusName()) && claimReportsForm.getStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByClaimReviewerCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), employeeId, claimReportsForm.isIncludeResignedEmployees(),
						claimReportsForm.isIncludeSubordinateEmployees(), isCheckedFromCreatedDate);
			} else if (StringUtils.isNotBlank(claimReportsForm.getStatusName())
					&& claimReportsForm.getStatusName().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByClaimReviewerCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), employeeId, claimReportsForm.isIncludeResignedEmployees(),
						claimReportsForm.isIncludeSubordinateEmployees(), isCheckedFromCreatedDate);
			} else if (StringUtils.isNotBlank(claimReportsForm.getStatusName())
					&& claimReportsForm.getStatusName().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByClaimReviewerCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), employeeId, claimReportsForm.isIncludeResignedEmployees(),
						claimReportsForm.isIncludeSubordinateEmployees(), isCheckedFromCreatedDate);
			} else {
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);

				List<ClaimApplicationItem> claimApplicationItemVO1List = new ArrayList<ClaimApplicationItem>();
				claimApplicationItemVO1List = claimApplicationItemDAO.findByClaimReviewerCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), employeeId, claimReportsForm.isIncludeResignedEmployees(),
						claimReportsForm.isIncludeSubordinateEmployees(), isCheckedFromCreatedDate);

				claimApplicationItemVOList.addAll(claimApplicationItemVO1List);

			}

		}

		// Get Employee Custom Field Data
		Set<Long> empIdSet = new HashSet<Long>();
		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {
			empIdSet.add(ClaimAppItemsVO.getClaimApplication().getEmployee().getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();

		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}
		}

		CustomFieldReportDTO customFieldReportDTO = null;
		List<String> dataDictNameList = new ArrayList<String>();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		List<Object[]> customFieldObjList = new ArrayList<Object[]>();
		if (!employeeIdsList.isEmpty()) {
			customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList,
					companyId, true);
			dataDictNameList = customFieldReportDTO.getDataDictNameList();
			// Get Employee Custom Field Data
			Integer custFieldCount = 1;
			for (String dataDictName : dataDictNameList) {
				ClaimReportHeaderDTO claimHeaderDTO = new ClaimReportHeaderDTO();
				claimHeaderDTO.setmDataProp("custField" + custFieldCount);
				claimHeaderDTO.setsTitle(dataDictName);
				claimHeaderDTOs.add(claimHeaderDTO);
				custFieldCount++;
			}

			// Get Employee Custom Field Data
			customFieldObjList = customFieldReportDTO.getCustomFieldObjList();

			for (Object[] objArr : customFieldObjList) {
				customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
			}
		}

		int count = 1;
		int fieldCount = 0;
		List<ClaimDetailsReportDataDTO> claimDetailsDataDTOs = new ArrayList<>();
		boolean openToDependentsExists = claimApplicationItemDAO.openToDependentsExists(companyId);
		HashMap<String, String> custFieldHashMap;
		Set<String> claimCustomFieldSet = new LinkedHashSet<>();
		// 1000 Seperator
		DecimalFormat thousandSeperator = new DecimalFormat("###,###.00");

		// DecimalFormat thousandSeperator2 = new
		// DecimalFormat("###,###.00000000");

		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {

			List<ClaimApplicationWorkflow> claimApplicationWorkflowList = claimApplicationWorkflowDAO
					.findWorkFlowByClaimAppId(ClaimAppItemsVO.getClaimApplication().getClaimApplicationId());

			ClaimDetailsReportDataDTO claimReportDataDTO = new ClaimDetailsReportDataDTO();
			custFieldHashMap = new LinkedHashMap<>();
			Set<ClaimApplicationItemCustomField> claimAppCustomFieldsSet = ClaimAppItemsVO
					.getClaimApplicationItemCustomFields();
			List<ClaimDetailsReportCustomDataDTO> customFieldDtoList = new ArrayList<>();
			int tempfieldCount = 0;
			for (ClaimApplicationItemCustomField applicationItemCustomField : claimAppCustomFieldsSet) {

				ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field " + fieldCount);
				customFieldDto.setCustomFieldKeyName(
						applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				customFieldDto.setCustomFieldValueName(applicationItemCustomField.getValue());
				customFieldDtoList.add(customFieldDto);

				claimCustomFieldSet.add(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				custFieldHashMap.put(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName(),
						applicationItemCustomField.getValue());

				try {
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldValueName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(applicationItemCustomField.getValue()));
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldHeaderName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(
									applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName()));

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
				tempfieldCount++;
			}
			if (tempfieldCount > fieldCount) {
				fieldCount = tempfieldCount;
			}
			claimReportDataDTO.setReportCustomDataDTOs(customFieldDtoList);

			// Get Lundin Claims Details
			if (hasLundinTimesheetModule) {
				Set<ClaimApplicationItemLundinDetail> applicationItemLundinDetails = ClaimAppItemsVO
						.getClaimApplicationItemLundinDetails();
				if (!applicationItemLundinDetails.isEmpty()) {
					for (ClaimApplicationItemLundinDetail detail : applicationItemLundinDetails) {
						claimReportDataDTO.setBlock(detail.getLundinBlock().getBlockCode());
						claimReportDataDTO.setAfe(detail.getLundinAFE().getAfeCode());
					}
				}
			}

			claimReportDataDTO.setSerialNum(count);
			claimReportDataDTO.setClaimNumber(ClaimAppItemsVO.getClaimApplication().getClaimNumber());
			claimReportDataDTO.setClaimTemplateName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
					.getClaimTemplate().getTemplateName());

			claimReportDataDTO.setEmployeeNo(
					ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee().getEmployeeNumber());
			claimReportDataDTO.setEmployeeId(
					ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee().getEmployeeId());
			claimReportDataDTO.setEmployeeName(
					getEmployeeName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee()));
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			}
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
			}
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
			}

			if (claimApplicationWorkflowList != null) {
				int counter = 0;
				for (ClaimApplicationWorkflow claimAppWorkflow : claimApplicationWorkflowList) {
					if (claimAppWorkflow.getClaimStatusMaster() != null) {

						switch (claimAppWorkflow.getClaimStatusMaster().getClaimStatusName()) {

						case PayAsiaConstants.CLAIM_STATUS_COMPLETED:
							claimReportDataDTO.setClaimApprovedDate(
									DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
							break;
						case PayAsiaConstants.CLAIM_STATUS_REJECTED:
							claimReportDataDTO
									.setRejectedDate(DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
							break;
						case PayAsiaConstants.CLAIM_STATUS_SUBMITTED:
							claimReportDataDTO.setClaimSubmittedDate(
									DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
							break;
						case PayAsiaConstants.CLAIM_STATUS_APPROVED:

							if (counter == 0) {
								claimReportDataDTO.setClaimApprovedForwardedDate(
										DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
							} else {
								claimReportDataDTO.setClaimApprovedForwarded2Date(
										DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
							}
							counter++;
							break;
						default:
							LOGGER.error("There is no claim transaction..");
						}
					}
				}
			}

			claimReportDataDTO.setClaimCreatedDate(
					DateUtils.timeStampToString(ClaimAppItemsVO.getClaimApplication().getCreatedDate()));
			claimReportDataDTO.setClaimDate(DateUtils.timeStampToString(ClaimAppItemsVO.getClaimDate()));

			claimReportDataDTO.setClaimItemName(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getClaimItemName());
			claimReportDataDTO.setAccountCode(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getAccountCode());
			claimReportDataDTO
					.setConvClaimAmount(thousandSeperator.format(ClaimAppItemsVO.getClaimAmount().doubleValue()));
			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getAmountBeforeTax() != null) {
				claimReportDataDTO.setAmountBeforeTax(
						thousandSeperator.format(ClaimAppItemsVO.getAmountBeforeTax().doubleValue()));
			} else {
				claimReportDataDTO.setAmountBeforeTax("");
			}

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getTaxAmount() != null) {
				claimReportDataDTO
						.setConvTaxAmount(thousandSeperator.format(ClaimAppItemsVO.getTaxAmount().doubleValue()));
			} else {
				claimReportDataDTO.setConvTaxAmount("");
			}

			// Amount Applicable
			if (ClaimAppItemsVO.getApplicableClaimAmount() != null
					&& ClaimAppItemsVO.getApplicableClaimAmount().compareTo(BigDecimal.ZERO) != 0) {
				claimReportDataDTO.setAmountApplicable(
						thousandSeperator.format(ClaimAppItemsVO.getApplicableClaimAmount().doubleValue()));
			} else {
				claimReportDataDTO
						.setAmountApplicable(thousandSeperator.format(ClaimAppItemsVO.getClaimAmount().doubleValue()));
			}

			claimReportDataDTO.setClaimantName(ClaimAppItemsVO.getClaimantName());
			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			claimReportDataDTO.setRemarks(ClaimAppItemsVO.getRemarks());
			if (claimReportsForm.isIncludeSubordinateEmployees()) {
				claimReportDataDTO.setCompanyCodes(ClaimAppItemsVO.getClaimApplication().getCompany().getCompanyCode());
			}

			if (ClaimAppItemsVO.getExchangeRate() != null) {
				claimReportDataDTO.setExchangeRate(ClaimAppItemsVO.getExchangeRate().toString());

			} else {
				claimReportDataDTO.setExchangeRate("");
			}

			if (ClaimAppItemsVO.getForexReceiptAmount() != null) {
				claimReportDataDTO.setReceiptAmount(
						thousandSeperator.format(ClaimAppItemsVO.getForexReceiptAmount().doubleValue()));
				if (ClaimAppItemsVO.getCurrencyMaster() != null) {
					claimReportDataDTO.setReceiptAmountCurrency(ClaimAppItemsVO.getCurrencyMaster().getCurrencyCode());
				} else {
					claimReportDataDTO.setReceiptAmountCurrency("");
				}
			} else {
				claimReportDataDTO.setReceiptAmount("");
				claimReportDataDTO.setReceiptAmountCurrency("");
			}

			claimDetailsDataDTOs.add(claimReportDataDTO);
			count++;

			// Add Employee Custom Field
			if (!customFieldObjList.isEmpty()) {
				int counter = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(claimReportDataDTO.getEmployeeId());
				for (Object object : objArr) {
					if (counter != 0) {
						if (object != null) {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), String.valueOf(object));
						} else {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), "");
						}

						try {
							ClaimDetailsReportDataDTO.class.getMethod("setCustField" + counter, String.class)
									.invoke(claimReportDataDTO, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					counter++;
				}
			}
			claimReportDataDTO.setCustFieldMap(custFieldHashMap);

		}

		for (String claimCustField : claimCustomFieldSet) {
			dataDictNameList.add(claimCustField);
		}
		claimDetailsReportDTO.setDataDictNameList(dataDictNameList);

		if (!openToDependentsExists) {
			claimHeaderDTOs.remove(claimantName);
		}
		List<ClaimDetailsReportCustomDataDTO> customDataDTOs = new ArrayList<>();
		for (int countF = 1; countF <= fieldCount; countF++) {
			ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
			customFieldDto.setCustomFieldHeaderName("Custom Field " + countF);
			customDataDTOs.add(customFieldDto);
		}

		BeanComparator beanComparator = null;

		if (StringUtils.isNotBlank(claimReportsForm.getGroupByName())
				&& !claimReportsForm.getGroupByName().equalsIgnoreCase(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_MONTH)) {
			if (claimReportsForm.getGroupByName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_ITEM)) {
				beanComparator = new BeanComparator(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_ITEM_NAME);
			}
			if (claimReportsForm.getGroupByName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_TEMPLATE)) {
				beanComparator = new BeanComparator(PayAsiaConstants.CLAIM_REPORTS_GROUP_BY_CLAIM_TEMPLATE_NAME);
			}
		} else {
			beanComparator = new BeanComparator("employeeNo");
		}

		Collections.sort(claimDetailsDataDTOs, beanComparator);

		claimDetailsReportDTO.setClaimDetailsDataDTOs(claimDetailsDataDTOs);
		claimDetailsReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);
		claimDetailsReportDTO.setClaimDetailsCustomDataDTOs(customDataDTOs);
		if (claimReportsForm.isIncludeSubordinateEmployees()) {
			claimDetailsReportDTO.setSubordinateCompanyEmployee(claimReportsForm.isIncludeSubordinateEmployees());
		}
		return claimDetailsReportDTO;
	}

	@Override
	public List<ClaimReportsForm> getClaimCategoryList(Long companyId) {
		List<ClaimReportsForm> claimReportsFormList = new ArrayList<>();
		List<ClaimCategoryMaster> claimCategoryMasterVOList = claimCategoryDAO.getClaimCategory(null, null, companyId);

		for (ClaimCategoryMaster categoryMaster : claimCategoryMasterVOList) {
			ClaimReportsForm claimTemplateForm = new ClaimReportsForm();
			claimTemplateForm.setClaimCategoryName(categoryMaster.getClaimCategoryName());
			claimTemplateForm.setClaimCategoryId(categoryMaster.getClaimCategoryId());
			claimReportsFormList.add(claimTemplateForm);
		}
		return claimReportsFormList;
	}

	@Override
	public List<ClaimCategoryForm> getAllClaimCategory(Long companyId) {
		List<ClaimCategoryForm> claimCategoryFormList = new ArrayList<>();

		List<ClaimCategoryMaster> claimCategVOList = claimTemplateDAO.getAllClaimCategoryCompany(companyId);
		for (ClaimCategoryMaster claimCategVO : claimCategVOList) {
			ClaimCategoryForm claimCategoryForm = new ClaimCategoryForm();
			claimCategoryForm.setClaimCategoryID(claimCategVO.getClaimCategoryId());
			claimCategoryForm.setClaimCategoryName(claimCategVO.getClaimCategoryName());
			claimCategoryFormList.add(claimCategoryForm);
		}

		return claimCategoryFormList;
	}

	@Override
	public List<ClaimReportsForm> getAllClaimTemplateWithTemplateCapping(Long companyId) {
		List<ClaimReportsForm> claimReportsFormList = new ArrayList<>();

		List<ClaimTemplate> claimTemplateVOList = claimTemplateDAO.getAllClaimTemplateWithTemplateCapping(companyId);
		for (ClaimTemplate claimTemplateVO : claimTemplateVOList) {
			ClaimReportsForm claimTemplateForm = new ClaimReportsForm();
			claimTemplateForm.setClaimTemplateId(claimTemplateVO.getClaimTemplateId());
			claimTemplateForm.setClaimTemplateName(claimTemplateVO.getTemplateName());
			claimReportsFormList.add(claimTemplateForm);
		}

		return claimReportsFormList;
	}

	@Override
	public List<ClaimReportsForm> getAllClaimTemplateWithClaimItemCapping(Long companyId) {
		List<ClaimReportsForm> claimReportsFormList = new ArrayList<>();

		List<ClaimTemplate> claimTemplateVOList = claimTemplateDAO.getAllClaimTemplateWithClaimItemCapping(companyId);
		for (ClaimTemplate claimTemplateVO : claimTemplateVOList) {
			ClaimReportsForm claimTemplateForm = new ClaimReportsForm();
			claimTemplateForm.setClaimTemplateId(claimTemplateVO.getClaimTemplateId());
			claimTemplateForm.setClaimTemplateName(claimTemplateVO.getTemplateName());
			claimReportsFormList.add(claimTemplateForm);
		}

		return claimReportsFormList;
	}

	@Override
	public List<ClaimReportsForm> getClaimItemList(Long companyId, Long claimCategoryId) {
		List<ClaimReportsForm> claimReportsFormList = new ArrayList<>();
		List<ClaimItemMaster> claimItemMasterList;
		if (claimCategoryId != 0) {
			claimItemMasterList = claimItemMasterDAO.findAll(companyId, null, null, claimCategoryId);
		} else {
			claimItemMasterList = claimItemMasterDAO.findAll(companyId, null, null, null);
		}

		for (ClaimItemMaster claimItemMasterVO : claimItemMasterList) {
			ClaimReportsForm claimTemplateForm = new ClaimReportsForm();
			claimTemplateForm.setClaimItemId(claimItemMasterVO.getClaimItemId());
			claimTemplateForm.setClaimItemName(claimItemMasterVO.getClaimItemName());
			claimReportsFormList.add(claimTemplateForm);
		}
		// Collections.sort(claimReportsFormList, new ClaimItemComp());
		return claimReportsFormList;
	}

	@Override
	public List<ClaimReportsForm> getClaimItemListByTemplateId(Long companyId, Long claimTemplateId) {
		List<ClaimReportsForm> claimReportsFormList = new ArrayList<>();
		List<ClaimTemplateItem> claimTemplateItemList;
		if (claimTemplateId != 0) {
			claimTemplateItemList = claimTemplateItemDAO.findByCondition(claimTemplateId, companyId);

			for (ClaimTemplateItem claimTemplateItemVO : claimTemplateItemList) {
				ClaimReportsForm claimTemplateForm = new ClaimReportsForm();
				claimTemplateForm.setClaimItemId(claimTemplateItemVO.getClaimItemMaster().getClaimItemId());
				claimTemplateForm.setClaimItemName(claimTemplateItemVO.getClaimItemMaster().getClaimItemName());
				claimReportsFormList.add(claimTemplateForm);
			}
		}
		// Collections.sort(claimReportsFormList, new ClaimItemComp());
		return claimReportsFormList;
	}

	/**
	 * Comparator Class for Ordering LundinAFEComp List
	 */
	private class ClaimItemComp implements Comparator<ClaimItemDTO> {
		public int compare(ClaimItemDTO claimItemDTO1, ClaimItemDTO claimItemDTO2) {
			return claimItemDTO1.getClaimItemName().compareTo(claimItemDTO2.getClaimItemName());
		}
	}

	@Override
	public List<ClaimReportsForm> getClaimBatchList(Long companyId) {
		List<ClaimReportsForm> claimReportsFormList = new ArrayList<>();

		List<ClaimBatchMaster> claimBatchMasterVOList = claimBatchMasterDAO.getClaimBatchByCompany(companyId);
		for (ClaimBatchMaster claimBatchMaster : claimBatchMasterVOList) {
			ClaimReportsForm claimTemplateForm = new ClaimReportsForm();
			claimTemplateForm.setClaimBatchName(claimBatchMaster.getClaimBatchDesc());
			claimTemplateForm.setClaimBatchId(FormatPreserveCryptoUtil.encrypt(claimBatchMaster.getClaimBatchID()));
			claimReportsFormList.add(claimTemplateForm);
		}

		return claimReportsFormList;
	}

	@Override
	public ClaimReviewerInfoReportDTO showClaimReviewerReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId) {
		ClaimReviewerInfoReportDTO claimDetailsReportDTO = new ClaimReviewerInfoReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");

		ClaimReportHeaderDTO leaveSchemeName = new ClaimReportHeaderDTO();
		leaveSchemeName.setmDataProp("claimTemplateName");
		leaveSchemeName.setsTitle("Claim Template");

		ClaimReportHeaderDTO rev1EmpNo = new ClaimReportHeaderDTO();
		rev1EmpNo.setmDataProp("reviewer1EmployeeNo");
		rev1EmpNo.setsTitle("Reviewer 1 Employee Number");

		ClaimReportHeaderDTO reviewer1EmployeeName = new ClaimReportHeaderDTO();
		reviewer1EmployeeName.setmDataProp("reviewer1EmployeeName");
		reviewer1EmployeeName.setsTitle("Reviewer 1 Employee Name");

		ClaimReportHeaderDTO reviewer1Email = new ClaimReportHeaderDTO();
		reviewer1Email.setmDataProp("reviewer1Email");
		reviewer1Email.setsTitle("Reviewer 1 Email");

		ClaimReportHeaderDTO rev2EmpNo = new ClaimReportHeaderDTO();
		rev2EmpNo.setmDataProp("reviewer2EmployeeNo");
		rev2EmpNo.setsTitle("Reviewer 2 Employee Number");

		ClaimReportHeaderDTO reviewer2EmployeeName = new ClaimReportHeaderDTO();
		reviewer2EmployeeName.setmDataProp("reviewer2EmployeeName");
		reviewer2EmployeeName.setsTitle("Reviewer 2 Employee Name");

		ClaimReportHeaderDTO reviewer2Email = new ClaimReportHeaderDTO();
		reviewer2Email.setmDataProp("reviewer2Email");
		reviewer2Email.setsTitle("Reviewer 2 Email");

		ClaimReportHeaderDTO rev3EmpNo = new ClaimReportHeaderDTO();
		rev3EmpNo.setmDataProp("reviewer3EmployeeNo");
		rev3EmpNo.setsTitle("Reviewer 3 Employee Number");

		ClaimReportHeaderDTO reviewer3EmployeeName = new ClaimReportHeaderDTO();
		reviewer3EmployeeName.setmDataProp("reviewer3EmployeeName");
		reviewer3EmployeeName.setsTitle("Reviewer 3 Employee Name");

		ClaimReportHeaderDTO reviewer3Email = new ClaimReportHeaderDTO();
		reviewer3Email.setmDataProp("reviewer3Email");
		reviewer3Email.setsTitle("Reviewer 3 Email");

		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(employeeName);
		claimHeaderDTOs.add(leaveSchemeName);
		claimHeaderDTOs.add(rev1EmpNo);
		claimHeaderDTOs.add(reviewer1EmployeeName);
		claimHeaderDTOs.add(reviewer1Email);
		claimHeaderDTOs.add(rev2EmpNo);
		claimHeaderDTOs.add(reviewer2EmployeeName);
		claimHeaderDTOs.add(reviewer2Email);
		claimHeaderDTOs.add(rev3EmpNo);
		claimHeaderDTOs.add(reviewer3EmployeeName);
		claimHeaderDTOs.add(reviewer3Email);

		EmployeeShortListDTO companyShortList = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		if (companyShortList.getEmployeeShortList()) {
			claimReportsForm.setIsAllEmployees(false);
		} else {
			claimReportsForm.setIsAllEmployees(true);
		}

		List<BigInteger> companyShortListEmployeeIds = companyShortList.getShortListEmployeeIds();
		List<BigInteger> generateEmpIds = null;
		List<BigInteger> reportShortListEmployeeIds = new ArrayList<BigInteger>();
		if (StringUtils.isNotBlank(claimReportsForm.getEmployeeIds())) {
			String[] shortListEmployeeIds = claimReportsForm.getEmployeeIds().split(",");
			for (int count = 0; count < shortListEmployeeIds.length; count++) {
				if (StringUtils.isNotBlank(shortListEmployeeIds[count])) {
					reportShortListEmployeeIds.add(new BigInteger(shortListEmployeeIds[count]));
				}

			}

			if (companyShortList.getEmployeeShortList()) {
				claimReportsForm.setIsAllEmployees(false);
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		String employeeIds = "";
		if (StringUtils.isNotBlank(claimReportsForm.getEmployeeIds())) {
			claimReportsForm.setIsAllEmployees(false);
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}
		StringBuilder empIdBuilder = new StringBuilder();
		for (BigInteger empId : generateEmpIds) {
			empIdBuilder = empIdBuilder.append(String.valueOf(empId));
			empIdBuilder = empIdBuilder.append(",");
		}
		employeeIds = empIdBuilder.toString();
		List<ClaimReviewerReportDataDTO> claimRevReportDataVODTOs = employeeClaimTemplateItemDAO
				.findClaimReviewerReportData(companyId, employeeIds, claimReportsForm.getIsAllEmployees());
		List<ClaimReviewerReportDataDTO> claimReviewerDataDTOs = new ArrayList<>();
		for (ClaimReviewerReportDataDTO claimReviewerReportDataVO : claimRevReportDataVODTOs) {
			ClaimReviewerReportDataDTO claimReportDataDTO = new ClaimReviewerReportDataDTO();
			claimReportDataDTO.setEmployeeNo(claimReviewerReportDataVO.getEmployeeNumber());
			claimReportDataDTO.setEmployeeName(
					getEmployeeName(claimReviewerReportDataVO.getFirstName(), claimReviewerReportDataVO.getLastName()));
			claimReportDataDTO.setClaimTemplateName(claimReviewerReportDataVO.getClaimTemplateName());
			claimReportDataDTO.setReviewer1EmployeeNo(claimReviewerReportDataVO.getReviewer1EmployeeNo());
			claimReportDataDTO
					.setReviewer1EmployeeName(getEmployeeName(claimReviewerReportDataVO.getReviewer1FirstName(),
							claimReviewerReportDataVO.getReviewer1LastName()));
			claimReportDataDTO.setReviewer1Email(claimReviewerReportDataVO.getReviewer1Email());
			claimReportDataDTO.setReviewer2EmployeeNo(claimReviewerReportDataVO.getReviewer2EmployeeNo());
			claimReportDataDTO
					.setReviewer2EmployeeName(getEmployeeName(claimReviewerReportDataVO.getReviewer2FirstName(),
							claimReviewerReportDataVO.getReviewer2LastName()));
			claimReportDataDTO.setReviewer2Email(claimReviewerReportDataVO.getReviewer2Email());
			claimReportDataDTO.setReviewer3EmployeeNo(claimReviewerReportDataVO.getReviewer3EmployeeNo());
			claimReportDataDTO
					.setReviewer3EmployeeName(getEmployeeName(claimReviewerReportDataVO.getReviewer3FirstName(),
							claimReviewerReportDataVO.getReviewer3LastName()));
			claimReportDataDTO.setReviewer3Email(claimReviewerReportDataVO.getReviewer3Email());

			claimReviewerDataDTOs.add(claimReportDataDTO);
		}
		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(claimReviewerDataDTOs, beanComparator);
		claimDetailsReportDTO.setClaimReviewerDataDTOs(claimReviewerDataDTOs);
		claimDetailsReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);

		return claimDetailsReportDTO;
	}

	@Override
	public ClaimDetailsReportDTO showClaimDetailsReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds, boolean hasLundinTimesheetModule, Locale locale) {
		String sNoLocale = "payasia.serial.number";
		String claimNumberLocale = "payasia.add.claim.number";
		String claimTemplateLocale = "payasia.claim.item.claim.template";
		String receiptDateLocale = "payasia.add.claim.date";
		String employeeNumberLocale = "payasia.employee.number";
		String employeeNameLocale = "payasia.employee.name";
		String claimItemLocale = "payasia.claim.item.claim.items";
		String claimaintNameLocale = "payasia.claim.claimaint.name";
		String accountCodeLocale = "payasia.account.code";
		String appliedAcceptedDateLocale = "payasia.claim.applied.accepted.date";
		String convClaimAmountLocale = "payasia.claim.conv.claim.amount";
		String amountBeforeTaxLocale = "payasia.add.claim.amount.before.tax";
		String convTaxAmountLocale = "payasia.claim.conv.tax.amount";
		String remarksLocale = "payasia.remarks";
		String blockBeforeTaxLocale = "payasia.claim.block.before.tax";
		String AFELocale = "payasia.lundin.claim.afe";
		String customfield1Locale = "payasia.claim.custom.field.1";
		String blockLocale = "payasia.lundin.claim.block";

		ClaimDetailsReportDTO claimDetailsReportDTO = new ClaimDetailsReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		ClaimReportHeaderDTO serialNum = new ClaimReportHeaderDTO();
		serialNum.setmDataProp("serialNum");
		serialNum.setsTitle(messageSource.getMessage(sNoLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimNumber = new ClaimReportHeaderDTO();
		claimNumber.setmDataProp("claimNumber");
		claimNumber.setsTitle(messageSource.getMessage(claimNumberLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimTemplateName = new ClaimReportHeaderDTO();
		claimTemplateName.setmDataProp("claimTemplateName");
		claimTemplateName.setsTitle(messageSource.getMessage(claimTemplateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimDate = new ClaimReportHeaderDTO();
		claimDate.setmDataProp("claimDate");
		claimDate.setsTitle(messageSource.getMessage(receiptDateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle(messageSource.getMessage(employeeNumberLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle(messageSource.getMessage(employeeNameLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO claimItem = new ClaimReportHeaderDTO();
		claimItem.setmDataProp("claimItemName");
		claimItem.setsTitle(messageSource.getMessage(claimItemLocale, new Object[] {}, locale));
		ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
		claimantName.setmDataProp("claimantName");
		claimantName.setsTitle(messageSource.getMessage(claimaintNameLocale, new Object[] {}, locale));
		ClaimReportHeaderDTO accountCode = new ClaimReportHeaderDTO();
		accountCode.setmDataProp("accountCode");
		accountCode.setsTitle(messageSource.getMessage(accountCodeLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO appliedDate = new ClaimReportHeaderDTO();
		appliedDate.setmDataProp("appliedDate");
		appliedDate.setsTitle(messageSource.getMessage(appliedAcceptedDateLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO convClaimAmount = new ClaimReportHeaderDTO();
		convClaimAmount.setmDataProp("convClaimAmount");
		convClaimAmount.setsTitle(messageSource.getMessage(convClaimAmountLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO convClaimAmountCurrency = new ClaimReportHeaderDTO();
		convClaimAmountCurrency.setmDataProp("convClaimAmountCurrency");
		convClaimAmountCurrency.setsTitle("");

		ClaimReportHeaderDTO amountApplicable = new ClaimReportHeaderDTO();
		amountApplicable.setmDataProp("amountApplicable");
		amountApplicable.setsTitle("Applicable Amount");

		ClaimReportHeaderDTO amountBeforeTax = new ClaimReportHeaderDTO();
		amountBeforeTax.setmDataProp("amountBeforeTax");
		amountBeforeTax.setsTitle(messageSource.getMessage(amountBeforeTaxLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO amountBeforeTaxCurrency = new ClaimReportHeaderDTO();
		amountBeforeTaxCurrency.setmDataProp("amountBeforeTaxCurrency");
		amountBeforeTaxCurrency.setsTitle("");

		ClaimReportHeaderDTO convTaxAmount = new ClaimReportHeaderDTO();
		convTaxAmount.setmDataProp("convTaxAmount");
		convTaxAmount.setsTitle(messageSource.getMessage(convTaxAmountLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO convTaxAmountCurrency = new ClaimReportHeaderDTO();
		convTaxAmountCurrency.setmDataProp("convTaxAmountCurrency");
		convTaxAmountCurrency.setsTitle("");

		ClaimReportHeaderDTO remarks = new ClaimReportHeaderDTO();
		remarks.setmDataProp("remarks");
		remarks.setsTitle(messageSource.getMessage(remarksLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO status = new ClaimReportHeaderDTO();
		status.setmDataProp("status");
		status.setsTitle("");

		ClaimReportHeaderDTO block = new ClaimReportHeaderDTO();
		block.setmDataProp("block");
		block.setsTitle(messageSource.getMessage(blockLocale, new Object[] {}, locale));

		ClaimReportHeaderDTO afe = new ClaimReportHeaderDTO();
		afe.setmDataProp("afe");
		afe.setsTitle("AFE");

		ClaimReportHeaderDTO customFieldHeaderName1 = new ClaimReportHeaderDTO();
		customFieldHeaderName1.setmDataProp("customFieldHeaderName1");
		customFieldHeaderName1.setsTitle("Custom Field 1");

		ClaimReportHeaderDTO customFieldValueName1 = new ClaimReportHeaderDTO();
		customFieldValueName1.setmDataProp("customFieldValueName1");
		customFieldValueName1.setsTitle("Custom Field 1 Value");

		ClaimReportHeaderDTO customFieldHeaderName2 = new ClaimReportHeaderDTO();
		customFieldHeaderName2.setmDataProp("customFieldHeaderName2");
		customFieldHeaderName2.setsTitle("Custom Field 2");

		ClaimReportHeaderDTO customFieldValueName2 = new ClaimReportHeaderDTO();
		customFieldValueName2.setmDataProp("customFieldValueName2");
		customFieldValueName2.setsTitle("Custom Field 2 Value");

		ClaimReportHeaderDTO customFieldHeaderName3 = new ClaimReportHeaderDTO();
		customFieldHeaderName3.setmDataProp("customFieldHeaderName3");
		customFieldHeaderName3.setsTitle("Custom Field 3 ");

		ClaimReportHeaderDTO customFieldValueName3 = new ClaimReportHeaderDTO();
		customFieldValueName3.setmDataProp("customFieldValueName3");
		customFieldValueName3.setsTitle("Custom Field 3 Value");

		ClaimReportHeaderDTO customFieldHeaderName4 = new ClaimReportHeaderDTO();
		customFieldHeaderName4.setmDataProp("customFieldHeaderName4");
		customFieldHeaderName4.setsTitle("Custom Field 4 ");

		ClaimReportHeaderDTO customFieldValueName4 = new ClaimReportHeaderDTO();
		customFieldValueName4.setmDataProp("customFieldValueName4");
		customFieldValueName4.setsTitle("Custom Field 4 Value");

		ClaimReportHeaderDTO customFieldHeaderName5 = new ClaimReportHeaderDTO();
		customFieldHeaderName5.setmDataProp("customFieldHeaderName5");
		customFieldHeaderName5.setsTitle("Custom Field 5");

		ClaimReportHeaderDTO customFieldValueName5 = new ClaimReportHeaderDTO();
		customFieldValueName5.setmDataProp("customFieldValueName5");
		customFieldValueName5.setsTitle("Custom Field 5 Value");

		claimHeaderDTOs.add(serialNum);
		claimHeaderDTOs.add(claimNumber);
		claimHeaderDTOs.add(claimTemplateName);
		if (hasLundinTimesheetModule) {
			claimHeaderDTOs.add(block);
			claimHeaderDTOs.add(afe);
		}

		claimHeaderDTOs.add(claimDate);
		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(employeeName);
		claimHeaderDTOs.add(claimItem);
		claimHeaderDTOs.add(claimantName);
		claimHeaderDTOs.add(accountCode);
		claimHeaderDTOs.add(appliedDate);
		claimHeaderDTOs.add(convClaimAmount);
		claimHeaderDTOs.add(convClaimAmountCurrency);
		claimHeaderDTOs.add(amountApplicable);
		claimHeaderDTOs.add(amountBeforeTax);
		claimHeaderDTOs.add(amountBeforeTaxCurrency);
		claimHeaderDTOs.add(convTaxAmount);
		claimHeaderDTOs.add(convTaxAmountCurrency);
		claimHeaderDTOs.add(remarks);
		claimHeaderDTOs.add(status);

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (claimReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId,
					claimReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		if (claimReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}

		List<Long> multipleClaimTemplateIdList = new ArrayList<Long>();
		List<Long> multipleClaimItemIdList = new ArrayList<Long>();

		for (int count = 0; count < claimReportsForm.getMultipleClaimTemplateId().length; count++) {
			if (claimReportsForm.getMultipleClaimTemplateId()[count] != null
					&& claimReportsForm.getMultipleClaimTemplateId()[count] != 0) {
				multipleClaimTemplateIdList.add(claimReportsForm.getMultipleClaimTemplateId()[count]);
			}
		}

		for (int count = 0; count < claimReportsForm.getMultipleClaimItemId().length; count++) {
			if (claimReportsForm.getMultipleClaimItemId()[count] != null
					&& claimReportsForm.getMultipleClaimItemId()[count] != 0) {
				multipleClaimItemIdList.add(claimReportsForm.getMultipleClaimItemId()[count]);
			}
		}

		List<ClaimApplicationItem> claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		if (claimReportsForm.getIsShortList() && generateEmpIds.isEmpty()) {
			claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		} else {
			List<String> claimStatusList = new ArrayList<>();
			if (StringUtils.isNotBlank(claimReportsForm.getStatusName()) && claimReportsForm.getStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), generateEmpIds);
			} else if (StringUtils.isNotBlank(claimReportsForm.getStatusName())
					&& claimReportsForm.getStatusName().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), generateEmpIds);
			} else {
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);

				List<ClaimApplicationItem> claimApplicationItemVO1List = new ArrayList<ClaimApplicationItem>();
				claimApplicationItemVO1List = claimApplicationItemDAO.findByCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), generateEmpIds);

				List<String> claimStatusCompList = new ArrayList<>();
				claimStatusCompList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				List<ClaimApplicationItem> claimApplicationItemVO2List = new ArrayList<ClaimApplicationItem>();
				claimApplicationItemVO2List = claimApplicationItemDAO.findByCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusCompList,
						claimReportsForm.getGroupByName(), generateEmpIds);

				claimApplicationItemVOList.addAll(claimApplicationItemVO1List);
				claimApplicationItemVOList.addAll(claimApplicationItemVO2List);
			}

		}

		// Get Employee Custom Field Data
		Set<Long> empIdSet = new HashSet<Long>();
		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {
			empIdSet.add(ClaimAppItemsVO.getClaimApplication().getEmployee().getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();

		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}
		}

		CustomFieldReportDTO customFieldReportDTO = null;
		List<String> dataDictNameList = new ArrayList<String>();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		List<Object[]> customFieldObjList = new ArrayList<Object[]>();
		if (!employeeIdsList.isEmpty()) {
			customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList,
					companyId, true);
			dataDictNameList = customFieldReportDTO.getDataDictNameList();
			// Get Employee Custom Field Data
			Integer custFieldCount = 1;
			for (String dataDictName : dataDictNameList) {
				ClaimReportHeaderDTO claimHeaderDTO = new ClaimReportHeaderDTO();
				claimHeaderDTO.setmDataProp("custField" + custFieldCount);
				claimHeaderDTO.setsTitle(dataDictName);
				claimHeaderDTOs.add(claimHeaderDTO);
				custFieldCount++;
			}

			// Get Employee Custom Field Data
			customFieldObjList = customFieldReportDTO.getCustomFieldObjList();

			for (Object[] objArr : customFieldObjList) {
				customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
			}
		}

		claimHeaderDTOs.add(customFieldHeaderName1);
		claimHeaderDTOs.add(customFieldValueName1);
		claimHeaderDTOs.add(customFieldHeaderName2);
		claimHeaderDTOs.add(customFieldValueName2);
		claimHeaderDTOs.add(customFieldHeaderName3);
		claimHeaderDTOs.add(customFieldValueName3);
		claimHeaderDTOs.add(customFieldHeaderName4);
		claimHeaderDTOs.add(customFieldValueName4);
		claimHeaderDTOs.add(customFieldHeaderName5);
		claimHeaderDTOs.add(customFieldValueName5);

		int count = 1;
		int fieldCount = 0;
		List<ClaimDetailsReportDataDTO> claimDetailsDataDTOs = new ArrayList<>();
		boolean openToDependentsExists = claimApplicationItemDAO.openToDependentsExists(companyId);
		HashMap<String, String> custFieldHashMap;
		Set<String> claimCustomFieldSet = new LinkedHashSet<>();
		// 1000 Seperator
		DecimalFormat thousandSeperator = new DecimalFormat("###,###.00");

		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {

			ClaimApplicationWorkflow applicationCompletedWorkflow = claimApplicationWorkflowDAO.findByAppIdAndStatus(
					ClaimAppItemsVO.getClaimApplication().getClaimApplicationId(),
					PayAsiaConstants.CLAIM_STATUS_COMPLETED);

			if (!ClaimAppItemsVO.isActive()) {
				continue;
			}
			ClaimDetailsReportDataDTO claimReportDataDTO = new ClaimDetailsReportDataDTO();
			custFieldHashMap = new LinkedHashMap<>();
			Set<ClaimApplicationItemCustomField> claimAppCustomFieldsSet = ClaimAppItemsVO
					.getClaimApplicationItemCustomFields();
			List<ClaimDetailsReportCustomDataDTO> customFieldDtoList = new ArrayList<>();
			int tempfieldCount = 0;
			for (ClaimApplicationItemCustomField applicationItemCustomField : claimAppCustomFieldsSet) {

				ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field " + fieldCount);
				customFieldDto.setCustomFieldKeyName(
						applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				customFieldDto.setCustomFieldValueName(applicationItemCustomField.getValue());
				customFieldDtoList.add(customFieldDto);

				claimCustomFieldSet.add(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				custFieldHashMap.put(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName(),
						applicationItemCustomField.getValue());

				try {
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldValueName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(applicationItemCustomField.getValue()));
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldHeaderName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(
									applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName()));

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
				tempfieldCount++;

			}
			if (tempfieldCount > fieldCount) {
				fieldCount = tempfieldCount;
			}
			claimReportDataDTO.setReportCustomDataDTOs(customFieldDtoList);

			// Get Lundin Claims Details
			if (hasLundinTimesheetModule) {
				Set<ClaimApplicationItemLundinDetail> applicationItemLundinDetails = ClaimAppItemsVO
						.getClaimApplicationItemLundinDetails();
				if (!applicationItemLundinDetails.isEmpty()) {
					for (ClaimApplicationItemLundinDetail detail : applicationItemLundinDetails) {
						claimReportDataDTO.setBlock(detail.getLundinBlock().getBlockCode());
						claimReportDataDTO.setAfe(detail.getLundinAFE().getAfeCode());
					}

				}
			}

			claimReportDataDTO.setSerialNum(count);
			claimReportDataDTO.setClaimNumber(ClaimAppItemsVO.getClaimApplication().getClaimNumber());
			claimReportDataDTO.setClaimTemplateName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
					.getClaimTemplate().getTemplateName());

			claimReportDataDTO.setEmployeeNo(
					ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee().getEmployeeNumber());
			claimReportDataDTO.setEmployeeId(
					ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee().getEmployeeId());
			claimReportDataDTO.setEmployeeName(
					getEmployeeName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee()));
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			}
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
			}

			if (applicationCompletedWorkflow != null) {
				claimReportDataDTO
						.setAppliedDate(DateUtils.timeStampToString(applicationCompletedWorkflow.getCreatedDate()));
			} else {
				claimReportDataDTO.setAppliedDate(
						DateUtils.timeStampToString(ClaimAppItemsVO.getClaimApplication().getUpdatedDate()));
			}

			claimReportDataDTO.setClaimDate(DateUtils.timeStampToString(ClaimAppItemsVO.getClaimDate()));
			claimReportDataDTO.setClaimItemName(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getClaimItemName());
			claimReportDataDTO.setAccountCode(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getAccountCode());
			claimReportDataDTO
					.setConvClaimAmount(thousandSeperator.format(ClaimAppItemsVO.getClaimAmount().doubleValue()));
			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getAmountBeforeTax() != null) {
				claimReportDataDTO.setAmountBeforeTax(
						thousandSeperator.format(ClaimAppItemsVO.getAmountBeforeTax().doubleValue()));
			} else {
				claimReportDataDTO.setAmountBeforeTax("");
			}

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getTaxAmount() != null) {
				claimReportDataDTO
						.setConvTaxAmount(thousandSeperator.format(ClaimAppItemsVO.getTaxAmount().doubleValue()));
			} else {
				claimReportDataDTO.setConvTaxAmount("");
			}

			// Amount Applicable
			DecimalFormat df = new DecimalFormat("##.00");
			if (ClaimAppItemsVO.getApplicableClaimAmount() != null
					&& ClaimAppItemsVO.getApplicableClaimAmount().compareTo(BigDecimal.ZERO) != 0) {
				claimReportDataDTO.setAmountApplicable(
						thousandSeperator.format(ClaimAppItemsVO.getApplicableClaimAmount().doubleValue()));
			} else {
				claimReportDataDTO
						.setAmountApplicable(thousandSeperator.format(ClaimAppItemsVO.getClaimAmount().doubleValue()));
			}

			claimReportDataDTO.setClaimantName(ClaimAppItemsVO.getClaimantName());

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			claimReportDataDTO.setRemarks(ClaimAppItemsVO.getRemarks());

			claimDetailsDataDTOs.add(claimReportDataDTO);
			count++;

			// Add Employee Custom Field
			if (!customFieldObjList.isEmpty()) {
				int counter = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(claimReportDataDTO.getEmployeeId());
				for (Object object : objArr) {
					if (counter != 0) {
						if (object != null) {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), String.valueOf(object));
						} else {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), "");
						}

						try {
							ClaimDetailsReportDataDTO.class.getMethod("setCustField" + counter, String.class)
									.invoke(claimReportDataDTO, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					counter++;
				}
			}
			claimReportDataDTO.setCustFieldMap(custFieldHashMap);

		}

		for (String claimCustField : claimCustomFieldSet) {
			dataDictNameList.add(claimCustField);
		}
		claimDetailsReportDTO.setDataDictNameList(dataDictNameList);

		if (!openToDependentsExists) {
			claimHeaderDTOs.remove(claimantName);
		}
		List<ClaimDetailsReportCustomDataDTO> customDataDTOs = new ArrayList<>();
		for (int countF = 1; countF <= fieldCount; countF++) {
			ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
			customFieldDto.setCustomFieldHeaderName("Custom Field " + countF);
			customDataDTOs.add(customFieldDto);
		}
		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(claimDetailsDataDTOs, beanComparator);
		claimDetailsReportDTO.setClaimDetailsDataDTOs(claimDetailsDataDTOs);
		claimDetailsReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);
		claimDetailsReportDTO.setClaimDetailsCustomDataDTOs(customDataDTOs);
		return claimDetailsReportDTO;
	}

	@Override
	public ClaimDetailsReportDTO showCategoryWiseClaimDetailsReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds, boolean hasLundinTimesheetModule) {
		ClaimDetailsReportDTO claimDetailsReportDTO = new ClaimDetailsReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (claimReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId,
					claimReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		if (claimReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}

		List<Long> multipleClaimTemplateIdList = new ArrayList<Long>();
		List<Long> multipleClaimItemIdList = new ArrayList<Long>();
		/*
		 * for (int count = 0; count < claimReportsForm
		 * .getMultipleClaimTemplateId().length; count++) { if
		 * (claimReportsForm.getMultipleClaimTemplateId()[count] != null &&
		 * claimReportsForm.getMultipleClaimTemplateId()[count] != 0) {
		 * multipleClaimTemplateIdList.add(claimReportsForm
		 * .getMultipleClaimTemplateId()[count]); } }
		 */

		/*
		 * for (int count = 0; count <
		 * claimReportsForm.getMultipleClaimItemId().length; count++) { if
		 * (claimReportsForm.getMultipleClaimItemId()[count] != null &&
		 * claimReportsForm.getMultipleClaimItemId()[count] != 0) {
		 * multipleClaimItemIdList.add(claimReportsForm
		 * .getMultipleClaimItemId()[count]); } }
		 */

		Map<String, ClaimDetailsReportDTO> claimDetailsTranMap = new HashMap<String, ClaimDetailsReportDTO>();

		Map<Long, ClaimCategoryMaster> claimCategoryMap = new HashMap<Long, ClaimCategoryMaster>();
		List<ClaimCategoryMaster> claimCategVOList = claimTemplateDAO.getAllClaimCategoryCompany(companyId);
		for (ClaimCategoryMaster ClaimCategory : claimCategVOList) {
			claimCategoryMap.put(ClaimCategory.getClaimCategoryId(), ClaimCategory);
		}

		List<Long> categoryIdList = new ArrayList<Long>();
		List<String> categoryNameList = new ArrayList<String>();
		if (claimReportsForm.getMultipleClaimCategoryId().length == 0) {
			for (ClaimCategoryMaster claimCategVO : claimCategVOList) {
				categoryIdList.add(claimCategVO.getClaimCategoryId());
				categoryNameList.add(claimCategVO.getClaimCategoryName());
			}
		} else {
			for (int count = 0; count < claimReportsForm.getMultipleClaimCategoryId().length; count++) {
				categoryIdList.add(claimReportsForm.getMultipleClaimCategoryId()[count]);
				categoryNameList.add(claimCategoryMap.get(claimReportsForm.getMultipleClaimCategoryId()[count])
						.getClaimCategoryName());
			}
		}

		List<String> dataDictNameList = new ArrayList<String>();
		List<String> claimStatusList = new ArrayList<>();
		List<ClaimApplicationItem> claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		if (claimReportsForm.getIsShortList() && generateEmpIds.isEmpty()) {
			claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		} else {

			if (StringUtils.isNotBlank(claimReportsForm.getStatusName()) && claimReportsForm.getStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByCategoryWiseCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						categoryIdList, multipleClaimItemIdList, claimStatusList, claimReportsForm.getGroupByName(),
						generateEmpIds);

			} else if (StringUtils.isNotBlank(claimReportsForm.getStatusName())
					&& claimReportsForm.getStatusName().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByCategoryWiseCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						categoryIdList, multipleClaimItemIdList, claimStatusList, claimReportsForm.getGroupByName(),
						generateEmpIds);
			} else {
				List<ClaimApplicationItem> claimApplicationItemVO1List = new ArrayList<ClaimApplicationItem>();
				List<String> claimStatus1List = new ArrayList<>();
				claimStatus1List.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				claimApplicationItemVO1List = claimApplicationItemDAO.findByCategoryWiseCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						categoryIdList, multipleClaimItemIdList, claimStatus1List, claimReportsForm.getGroupByName(),
						generateEmpIds);

				List<ClaimApplicationItem> claimApplicationItemVO2List = new ArrayList<ClaimApplicationItem>();
				List<String> claimStatus2List = new ArrayList<>();
				claimStatus2List.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimApplicationItemVO2List = claimApplicationItemDAO.findByCategoryWiseCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						categoryIdList, multipleClaimItemIdList, claimStatus2List, claimReportsForm.getGroupByName(),
						generateEmpIds);
				claimApplicationItemVOList.addAll(claimApplicationItemVO1List);
				claimApplicationItemVOList.addAll(claimApplicationItemVO2List);
			}

		}

		/*
		 * if (claimApplicationItemVOList.size() == 0) {
		 * claimDetailsReportDTO.setClaimDetailsTranMap(claimDetailsTranMap);
		 * return claimDetailsReportDTO; }
		 */

		// Get Employee Custom Field Data
		Set<Long> empIdSet = new HashSet<Long>();
		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {

			// Set Employee IDs
			empIdSet.add(ClaimAppItemsVO.getClaimApplication().getEmployee().getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();

		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}
		}

		CustomFieldReportDTO customFieldReportDTO = null;

		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		List<Object[]> customFieldObjList = new ArrayList<Object[]>();
		if (!employeeIdsList.isEmpty()) {
			customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList,
					companyId, true);
			dataDictNameList = customFieldReportDTO.getDataDictNameList();

			// Get Employee Custom Field Data
			customFieldObjList = customFieldReportDTO.getCustomFieldObjList();

			for (Object[] objArr : customFieldObjList) {
				customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
			}
		}

		int count = 1;
		int fieldCount = 0;
		List<ClaimDetailsReportDataDTO> claimDetailsDataDTOs = new ArrayList<>();
		boolean openToDependentsExists = claimApplicationItemDAO.openToDependentsExists(companyId);
		HashMap<String, String> custFieldHashMap;
		Set<String> claimCustomFieldSet = new LinkedHashSet<>();
		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {
			ClaimApplicationWorkflow applicationCompletedWorkflow = claimApplicationWorkflowDAO.findByAppIdAndStatus(
					ClaimAppItemsVO.getClaimApplication().getClaimApplicationId(),
					PayAsiaConstants.CLAIM_STATUS_COMPLETED);
			if (!ClaimAppItemsVO.isActive()) {
				continue;
			}
			ClaimDetailsReportDataDTO claimReportDataDTO = new ClaimDetailsReportDataDTO();
			custFieldHashMap = new LinkedHashMap<>();
			Set<ClaimApplicationItemCustomField> claimAppCustomFieldsSet = ClaimAppItemsVO
					.getClaimApplicationItemCustomFields();
			List<ClaimDetailsReportCustomDataDTO> customFieldDtoList = new ArrayList<>();
			int tempfieldCount = 0;
			for (ClaimApplicationItemCustomField applicationItemCustomField : claimAppCustomFieldsSet) {

				ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field " + fieldCount);
				customFieldDto.setCustomFieldKeyName(
						applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				customFieldDto.setCustomFieldValueName(applicationItemCustomField.getValue());
				customFieldDtoList.add(customFieldDto);

				claimCustomFieldSet.add(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				custFieldHashMap.put(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName(),
						applicationItemCustomField.getValue());

				try {
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldValueName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(applicationItemCustomField.getValue()));
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldHeaderName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(
									applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName()));

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
				tempfieldCount++;

			}
			if (tempfieldCount > fieldCount) {
				fieldCount = tempfieldCount;
			}

			claimReportDataDTO.setCategoryId(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryId());
			claimReportDataDTO.setCategoryName(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
			claimReportDataDTO.setReportCustomDataDTOs(customFieldDtoList);

			claimReportDataDTO.setSerialNum(count);
			claimReportDataDTO.setClaimNumber(ClaimAppItemsVO.getClaimApplication().getClaimNumber());
			claimReportDataDTO.setClaimTemplateName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
					.getClaimTemplate().getTemplateName());

			claimReportDataDTO.setEmployeeNo(
					ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee().getEmployeeNumber());
			claimReportDataDTO.setEmployeeId(
					ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee().getEmployeeId());
			claimReportDataDTO.setEmployeeName(
					getEmployeeName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee()));
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			}
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
			}

			if (applicationCompletedWorkflow != null) {
				claimReportDataDTO
						.setAppliedDate(DateUtils.timeStampToString(applicationCompletedWorkflow.getCreatedDate()));
			} else {
				claimReportDataDTO.setAppliedDate(
						DateUtils.timeStampToString(ClaimAppItemsVO.getClaimApplication().getUpdatedDate()));
			}

			claimReportDataDTO.setClaimDate(DateUtils.timeStampToString(ClaimAppItemsVO.getClaimDate()));
			claimReportDataDTO.setClaimItemName(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getClaimItemName());
			claimReportDataDTO.setAccountCode(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getAccountCode());
			claimReportDataDTO.setConvClaimAmount(ClaimAppItemsVO.getClaimAmount().toString());
			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getAmountBeforeTax() != null) {
				claimReportDataDTO.setAmountBeforeTax(ClaimAppItemsVO.getAmountBeforeTax().toString());
			} else {
				claimReportDataDTO.setAmountBeforeTax("");
			}

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getTaxAmount() != null) {
				claimReportDataDTO.setConvTaxAmount(ClaimAppItemsVO.getTaxAmount().toString());
			} else {
				claimReportDataDTO.setConvTaxAmount("");
			}

			// Amount Applicable
			DecimalFormat df = new DecimalFormat("##.00");
			if (ClaimAppItemsVO.getApplicableClaimAmount() != null
					&& ClaimAppItemsVO.getApplicableClaimAmount().compareTo(BigDecimal.ZERO) != 0) {
				claimReportDataDTO.setAmountApplicable(df.format(ClaimAppItemsVO.getApplicableClaimAmount()));
			} else {
				claimReportDataDTO.setAmountApplicable(df.format(ClaimAppItemsVO.getClaimAmount()));
			}

			claimReportDataDTO.setClaimantName(ClaimAppItemsVO.getClaimantName());

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			claimReportDataDTO.setRemarks(ClaimAppItemsVO.getRemarks());

			claimDetailsDataDTOs.add(claimReportDataDTO);
			count++;

			// Add Employee Custom Field
			if (!customFieldObjList.isEmpty()) {
				int counter = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(claimReportDataDTO.getEmployeeId());
				for (Object object : objArr) {
					if (counter != 0) {
						if (object != null) {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), String.valueOf(object));
						} else {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), "");
						}

						try {
							ClaimDetailsReportDataDTO.class.getMethod("setCustField" + counter, String.class)
									.invoke(claimReportDataDTO, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					counter++;
				}
			}
			claimReportDataDTO.setCustFieldMap(custFieldHashMap);

		}

		for (String claimCustField : claimCustomFieldSet) {
			dataDictNameList.add(claimCustField);
		}
		claimDetailsReportDTO.setDataDictNameList(dataDictNameList);

		// Day Wise
		Map<String, List<ClaimDetailsReportDataDTO>> dayWiseLeaveTranMap = new LinkedHashMap<String, List<ClaimDetailsReportDataDTO>>();
		for (String categoryName : categoryNameList) {
			for (ClaimDetailsReportDataDTO claimDetailsReportDataDTO : claimDetailsDataDTOs) {
				if (categoryName.equals(claimDetailsReportDataDTO.getCategoryName())) {
					List<ClaimDetailsReportDataDTO> dayWiseLeaveTranList = new ArrayList<>();
					if (dayWiseLeaveTranMap.containsKey(categoryName)) {
						dayWiseLeaveTranList = dayWiseLeaveTranMap.get(categoryName);
						dayWiseLeaveTranList.add(claimDetailsReportDataDTO);
						dayWiseLeaveTranMap.put(categoryName, dayWiseLeaveTranList);
					} else {
						dayWiseLeaveTranList.add(claimDetailsReportDataDTO);
						dayWiseLeaveTranMap.put(categoryName, dayWiseLeaveTranList);
					}
				}
			}
		}

		List<ClaimDetailsReportCustomDataDTO> customDataDTOs = new ArrayList<>();
		for (int countF = 1; countF <= fieldCount; countF++) {
			ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
			customFieldDto.setCustomFieldHeaderName("Custom Field " + countF);
			customDataDTOs.add(customFieldDto);
		}
		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(claimDetailsDataDTOs, beanComparator);
		claimDetailsReportDTO.setClaimDetailsDataDTOs(claimDetailsDataDTOs);

		claimDetailsReportDTO.setClaimDetailsCustomDataDTOs(customDataDTOs);
		claimDetailsReportDTO.setClaimDetailsTranMap(dayWiseLeaveTranMap);
		claimDetailsReportDTO.setCompanyName(companyDAO.findById(companyId).getCompanyName());
		return claimDetailsReportDTO;
	}

	@Override
	public ClaimDetailsReportDTO showPaidClaimDetailsReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds, boolean hasLundinTimesheetModule) {
		ClaimDetailsReportDTO claimDetailsReportDTO = new ClaimDetailsReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		Company companyVO = companyDAO.findById(companyId);

		List<ClaimBatchMaster> claimBatchMasterList = claimBatchMasterDAO.getClaimBatchMastersByDateRange(companyId,
				claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), companyVO.getDateFormat(),
				claimReportsForm.getPaidStatus());

		ClaimReportHeaderDTO serialNum = new ClaimReportHeaderDTO();
		serialNum.setmDataProp("serialNum");
		serialNum.setsTitle("S.No");

		ClaimReportHeaderDTO claimNumber = new ClaimReportHeaderDTO();
		claimNumber.setmDataProp("claimNumber");
		claimNumber.setsTitle("Claim Number");

		ClaimReportHeaderDTO claimTemplateName = new ClaimReportHeaderDTO();
		claimTemplateName.setmDataProp("claimTemplateName");
		claimTemplateName.setsTitle("Claim Template");

		ClaimReportHeaderDTO claimPaid = new ClaimReportHeaderDTO();
		claimPaid.setmDataProp("paid");
		claimPaid.setsTitle("Paid");

		ClaimReportHeaderDTO claimDate = new ClaimReportHeaderDTO();
		claimDate.setmDataProp("claimDate");
		claimDate.setsTitle("Receipt Date");

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");

		ClaimReportHeaderDTO claimItem = new ClaimReportHeaderDTO();
		claimItem.setmDataProp("claimItemName");
		claimItem.setsTitle("Claim Item");
		ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
		claimantName.setmDataProp("claimantName");
		claimantName.setsTitle("Claimaint Name");
		ClaimReportHeaderDTO accountCode = new ClaimReportHeaderDTO();
		accountCode.setmDataProp("accountCode");
		accountCode.setsTitle("Account Code");

		ClaimReportHeaderDTO appliedDate = new ClaimReportHeaderDTO();
		appliedDate.setmDataProp("appliedDate");
		appliedDate.setsTitle("Applied/Accepted Date");

		ClaimReportHeaderDTO convClaimAmount = new ClaimReportHeaderDTO();
		convClaimAmount.setmDataProp("convClaimAmount");
		convClaimAmount.setsTitle("Conv.Claim Amount");

		ClaimReportHeaderDTO convClaimAmountCurrency = new ClaimReportHeaderDTO();
		convClaimAmountCurrency.setmDataProp("convClaimAmountCurrency");
		convClaimAmountCurrency.setsTitle("");

		ClaimReportHeaderDTO amountApplicable = new ClaimReportHeaderDTO();
		amountApplicable.setmDataProp("amountApplicable");
		amountApplicable.setsTitle("Applicable Amount");

		ClaimReportHeaderDTO amountBeforeTax = new ClaimReportHeaderDTO();
		amountBeforeTax.setmDataProp("amountBeforeTax");
		amountBeforeTax.setsTitle("Amount Before Tax");

		ClaimReportHeaderDTO amountBeforeTaxCurrency = new ClaimReportHeaderDTO();
		amountBeforeTaxCurrency.setmDataProp("amountBeforeTaxCurrency");
		amountBeforeTaxCurrency.setsTitle("");

		ClaimReportHeaderDTO convTaxAmount = new ClaimReportHeaderDTO();
		convTaxAmount.setmDataProp("convTaxAmount");
		convTaxAmount.setsTitle("Conv.Tax Amount");

		ClaimReportHeaderDTO convTaxAmountCurrency = new ClaimReportHeaderDTO();
		convTaxAmountCurrency.setmDataProp("convTaxAmountCurrency");
		convTaxAmountCurrency.setsTitle("");

		ClaimReportHeaderDTO remarks = new ClaimReportHeaderDTO();
		remarks.setmDataProp("remarks");
		remarks.setsTitle("Remarks");

		ClaimReportHeaderDTO status = new ClaimReportHeaderDTO();
		status.setmDataProp("status");
		status.setsTitle("");

		ClaimReportHeaderDTO block = new ClaimReportHeaderDTO();
		block.setmDataProp("block");
		block.setsTitle("Block");

		ClaimReportHeaderDTO afe = new ClaimReportHeaderDTO();
		afe.setmDataProp("afe");
		afe.setsTitle("AFE");

		ClaimReportHeaderDTO customFieldHeaderName1 = new ClaimReportHeaderDTO();
		customFieldHeaderName1.setmDataProp("customFieldHeaderName1");
		customFieldHeaderName1.setsTitle("Custom Field 1");

		ClaimReportHeaderDTO customFieldValueName1 = new ClaimReportHeaderDTO();
		customFieldValueName1.setmDataProp("customFieldValueName1");
		customFieldValueName1.setsTitle("Custom Field 1 Value");

		ClaimReportHeaderDTO customFieldHeaderName2 = new ClaimReportHeaderDTO();
		customFieldHeaderName2.setmDataProp("customFieldHeaderName2");
		customFieldHeaderName2.setsTitle("Custom Field 2");

		ClaimReportHeaderDTO customFieldValueName2 = new ClaimReportHeaderDTO();
		customFieldValueName2.setmDataProp("customFieldValueName2");
		customFieldValueName2.setsTitle("Custom Field 2 Value");

		ClaimReportHeaderDTO customFieldHeaderName3 = new ClaimReportHeaderDTO();
		customFieldHeaderName3.setmDataProp("customFieldHeaderName3");
		customFieldHeaderName3.setsTitle("Custom Field 3 ");

		ClaimReportHeaderDTO customFieldValueName3 = new ClaimReportHeaderDTO();
		customFieldValueName3.setmDataProp("customFieldValueName3");
		customFieldValueName3.setsTitle("Custom Field 3 Value");

		ClaimReportHeaderDTO customFieldHeaderName4 = new ClaimReportHeaderDTO();
		customFieldHeaderName4.setmDataProp("customFieldHeaderName4");
		customFieldHeaderName4.setsTitle("Custom Field 4 ");

		ClaimReportHeaderDTO customFieldValueName4 = new ClaimReportHeaderDTO();
		customFieldValueName4.setmDataProp("customFieldValueName4");
		customFieldValueName4.setsTitle("Custom Field 4 Value");

		ClaimReportHeaderDTO customFieldHeaderName5 = new ClaimReportHeaderDTO();
		customFieldHeaderName5.setmDataProp("customFieldHeaderName5");
		customFieldHeaderName5.setsTitle("Custom Field 5");

		ClaimReportHeaderDTO customFieldValueName5 = new ClaimReportHeaderDTO();
		customFieldValueName5.setmDataProp("customFieldValueName5");
		customFieldValueName5.setsTitle("Custom Field 5 Value");

		claimHeaderDTOs.add(serialNum);
		claimHeaderDTOs.add(claimNumber);
		claimHeaderDTOs.add(claimTemplateName);
		claimHeaderDTOs.add(claimPaid);

		if (hasLundinTimesheetModule) {
			claimHeaderDTOs.add(block);
			claimHeaderDTOs.add(afe);
		}

		claimHeaderDTOs.add(claimDate);
		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(employeeName);
		claimHeaderDTOs.add(claimItem);
		claimHeaderDTOs.add(claimantName);
		claimHeaderDTOs.add(accountCode);
		claimHeaderDTOs.add(appliedDate);
		claimHeaderDTOs.add(convClaimAmount);
		claimHeaderDTOs.add(convClaimAmountCurrency);
		claimHeaderDTOs.add(amountApplicable);
		claimHeaderDTOs.add(amountBeforeTax);
		claimHeaderDTOs.add(amountBeforeTaxCurrency);
		claimHeaderDTOs.add(convTaxAmount);
		claimHeaderDTOs.add(convTaxAmountCurrency);
		claimHeaderDTOs.add(remarks);
		claimHeaderDTOs.add(status);

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (claimReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId,
					claimReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		if (claimReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}

		List<Long> multipleClaimTemplateIdList = new ArrayList<Long>();
		List<Long> multipleClaimItemIdList = new ArrayList<Long>();
		for (int count = 0; count < claimReportsForm.getMultipleClaimTemplateId().length; count++) {
			if (claimReportsForm.getMultipleClaimTemplateId()[count] != null
					&& claimReportsForm.getMultipleClaimTemplateId()[count] != 0) {
				multipleClaimTemplateIdList.add(claimReportsForm.getMultipleClaimTemplateId()[count]);
			}
		}
		for (int count = 0; count < claimReportsForm.getMultipleClaimItemId().length; count++) {
			if (claimReportsForm.getMultipleClaimItemId()[count] != null
					&& claimReportsForm.getMultipleClaimItemId()[count] != 0) {
				multipleClaimItemIdList.add(claimReportsForm.getMultipleClaimItemId()[count]);
			}
		}

		List<ClaimApplicationItem> claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		if (claimReportsForm.getIsShortList() && generateEmpIds.isEmpty()) {
			claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		} else {
			List<String> claimStatusList = new ArrayList<>();
			if (claimReportsForm.getStatusName().equalsIgnoreCase(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), generateEmpIds);
			} else if (claimReportsForm.getStatusName().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), generateEmpIds);
			} else {
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				List<ClaimApplicationItem> claimApplicationItemVO1List = new ArrayList<ClaimApplicationItem>();
				claimApplicationItemVO1List = claimApplicationItemDAO.findByCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), generateEmpIds);

				List<ClaimApplicationItem> claimApplicationItemVO2List = new ArrayList<ClaimApplicationItem>();
				List<String> claimStatusCompletedList = new ArrayList<>();
				claimStatusCompletedList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimApplicationItemVO2List = claimApplicationItemDAO.findByCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusCompletedList,
						claimReportsForm.getGroupByName(), generateEmpIds);

				claimApplicationItemVOList.addAll(claimApplicationItemVO1List);
				claimApplicationItemVOList.addAll(claimApplicationItemVO2List);
			}

		}

		// Get Employee Custom Field Data
		Set<Long> empIdSet = new HashSet<Long>();
		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {
			empIdSet.add(ClaimAppItemsVO.getClaimApplication().getEmployee().getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}
		}

		CustomFieldReportDTO customFieldReportDTO = null;
		List<String> dataDictNameList = new ArrayList<String>();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		List<Object[]> customFieldObjList = new ArrayList<Object[]>();
		if (!employeeIdsList.isEmpty()) {
			customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList,
					companyId, true);
			dataDictNameList = customFieldReportDTO.getDataDictNameList();
			// Get Employee Custom Field Data
			Integer custFieldCount = 1;
			for (String dataDictName : dataDictNameList) {
				ClaimReportHeaderDTO claimHeaderDTO = new ClaimReportHeaderDTO();
				claimHeaderDTO.setmDataProp("custField" + custFieldCount);
				claimHeaderDTO.setsTitle(dataDictName);
				claimHeaderDTOs.add(claimHeaderDTO);
				custFieldCount++;
			}

			// Get Employee Custom Field Data
			customFieldObjList = customFieldReportDTO.getCustomFieldObjList();

			for (Object[] objArr : customFieldObjList) {
				customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
			}
		}

		claimHeaderDTOs.add(customFieldHeaderName1);
		claimHeaderDTOs.add(customFieldValueName1);
		claimHeaderDTOs.add(customFieldHeaderName2);
		claimHeaderDTOs.add(customFieldValueName2);
		claimHeaderDTOs.add(customFieldHeaderName3);
		claimHeaderDTOs.add(customFieldValueName3);
		claimHeaderDTOs.add(customFieldHeaderName4);
		claimHeaderDTOs.add(customFieldValueName4);
		claimHeaderDTOs.add(customFieldHeaderName5);
		claimHeaderDTOs.add(customFieldValueName5);

		int count = 1;
		int fieldCount = 0;
		List<ClaimDetailsReportDataDTO> claimDetailsDataDTOs = new ArrayList<>();
		boolean openToDependentsExists = claimApplicationItemDAO.openToDependentsExists(companyId);
		HashMap<String, String> custFieldHashMap;
		Set<String> claimCustomFieldSet = new LinkedHashSet<>();
		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {
			if (!ClaimAppItemsVO.isActive()) {
				continue;
			}

			ClaimApplicationWorkflow applicationCompletedWorkflow = claimApplicationWorkflowDAO.findByAppIdAndStatus(
					ClaimAppItemsVO.getClaimApplication().getClaimApplicationId(),
					PayAsiaConstants.CLAIM_STATUS_COMPLETED);
			String paidStatus = "";
			if (applicationCompletedWorkflow != null) {
				paidStatus = getPaidStatus(claimBatchMasterList, applicationCompletedWorkflow.getCreatedDate());
			}

			switch (claimReportsForm.getPaidStatus().toLowerCase()) {
			case PayAsiaConstants.CLAIM_PAID_STATUS_ALL:
				break;
			case PayAsiaConstants.CLAIM_PAID_STATUS_PAID:
				if (StringUtils.isBlank(paidStatus) || paidStatus.equalsIgnoreCase("NA")) {
					continue;
				}

				break;
			case PayAsiaConstants.CLAIM_PAID_STATUS_UNPAID:
				if (StringUtils.isNotBlank(paidStatus) && !paidStatus.equalsIgnoreCase("NA")) {
					continue;
				}
			}
			paidStatus = ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED) ? paidStatus : "";
			ClaimDetailsReportDataDTO claimReportDataDTO = new ClaimDetailsReportDataDTO();
			custFieldHashMap = new LinkedHashMap<>();
			Set<ClaimApplicationItemCustomField> claimAppCustomFieldsSet = ClaimAppItemsVO
					.getClaimApplicationItemCustomFields();
			List<ClaimDetailsReportCustomDataDTO> customFieldDtoList = new ArrayList<>();
			int tempfieldCount = 0;
			for (ClaimApplicationItemCustomField applicationItemCustomField : claimAppCustomFieldsSet) {

				ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field " + fieldCount);
				customFieldDto.setCustomFieldKeyName(
						applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				customFieldDto.setCustomFieldValueName(applicationItemCustomField.getValue());
				customFieldDtoList.add(customFieldDto);

				claimCustomFieldSet.add(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				custFieldHashMap.put(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName(),
						applicationItemCustomField.getValue());

				try {
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldValueName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(applicationItemCustomField.getValue()));
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldHeaderName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(
									applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName()));

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
				tempfieldCount++;

			}
			if (tempfieldCount > fieldCount) {
				fieldCount = tempfieldCount;
			}
			claimReportDataDTO.setReportCustomDataDTOs(customFieldDtoList);

			// Get Lundin Claims Details
			if (hasLundinTimesheetModule) {
				Set<ClaimApplicationItemLundinDetail> applicationItemLundinDetails = ClaimAppItemsVO
						.getClaimApplicationItemLundinDetails();
				if (!applicationItemLundinDetails.isEmpty()) {
					for (ClaimApplicationItemLundinDetail detail : applicationItemLundinDetails) {
						claimReportDataDTO.setBlock(detail.getLundinBlock().getBlockCode());
						claimReportDataDTO.setAfe(detail.getLundinAFE().getAfeCode());
					}

				}
			}

			claimReportDataDTO.setPaid(paidStatus);

			claimReportDataDTO.setSerialNum(count);
			claimReportDataDTO.setClaimNumber(ClaimAppItemsVO.getClaimApplication().getClaimNumber());

			claimReportDataDTO.setClaimTemplateName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
					.getClaimTemplate().getTemplateName());

			claimReportDataDTO.setEmployeeNo(
					ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee().getEmployeeNumber());
			claimReportDataDTO.setEmployeeId(
					ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee().getEmployeeId());
			claimReportDataDTO.setEmployeeName(
					getEmployeeName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getEmployee()));
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			}
			if (ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| ClaimAppItemsVO.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				claimReportDataDTO.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
			}

			if (applicationCompletedWorkflow != null) {
				claimReportDataDTO
						.setAppliedDate(DateUtils.timeStampToString(applicationCompletedWorkflow.getCreatedDate()));
			} else {
				claimReportDataDTO.setAppliedDate(
						DateUtils.timeStampToString(ClaimAppItemsVO.getClaimApplication().getUpdatedDate()));
			}

			claimReportDataDTO.setClaimDate(DateUtils.timeStampToString(ClaimAppItemsVO.getClaimDate()));
			claimReportDataDTO.setClaimItemName(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getClaimItemName());
			claimReportDataDTO.setAccountCode(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getAccountCode());
			claimReportDataDTO.setConvClaimAmount(ClaimAppItemsVO.getClaimAmount().toString());
			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getAmountBeforeTax() != null) {
				claimReportDataDTO.setAmountBeforeTax(ClaimAppItemsVO.getAmountBeforeTax().toString());
			} else {
				claimReportDataDTO.setAmountBeforeTax("");
			}

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getTaxAmount() != null) {
				claimReportDataDTO.setConvTaxAmount(ClaimAppItemsVO.getTaxAmount().toString());
			} else {
				claimReportDataDTO.setConvTaxAmount("");
			}

			// Amount Applicable
			DecimalFormat df = new DecimalFormat("##.00");
			if (ClaimAppItemsVO.getApplicableClaimAmount() != null
					&& ClaimAppItemsVO.getApplicableClaimAmount().compareTo(BigDecimal.ZERO) != 0) {
				claimReportDataDTO.setAmountApplicable(df.format(ClaimAppItemsVO.getApplicableClaimAmount()));
			} else {
				claimReportDataDTO.setAmountApplicable(df.format(ClaimAppItemsVO.getClaimAmount()));
			}

			claimReportDataDTO.setClaimantName(ClaimAppItemsVO.getClaimantName());

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			claimReportDataDTO.setRemarks(ClaimAppItemsVO.getRemarks());

			claimDetailsDataDTOs.add(claimReportDataDTO);
			count++;

			// Add Employee Custom Field
			if (!customFieldObjList.isEmpty()) {
				int counter = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(claimReportDataDTO.getEmployeeId());
				for (Object object : objArr) {
					if (counter != 0) {
						if (object != null) {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), String.valueOf(object));
						} else {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), "");
						}

						try {
							ClaimDetailsReportDataDTO.class.getMethod("setCustField" + counter, String.class)
									.invoke(claimReportDataDTO, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					counter++;
				}
			}
			claimReportDataDTO.setCustFieldMap(custFieldHashMap);

		}

		for (String claimCustField : claimCustomFieldSet) {
			dataDictNameList.add(claimCustField);
		}
		claimDetailsReportDTO.setDataDictNameList(dataDictNameList);

		if (!openToDependentsExists) {
			claimHeaderDTOs.remove(claimantName);
		}
		List<ClaimDetailsReportCustomDataDTO> customDataDTOs = new ArrayList<>();
		for (int countF = 1; countF <= fieldCount; countF++) {
			ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
			customFieldDto.setCustomFieldHeaderName("Custom Field " + countF);
			customDataDTOs.add(customFieldDto);
		}
		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(claimDetailsDataDTOs, beanComparator);
		claimDetailsReportDTO.setClaimDetailsDataDTOs(claimDetailsDataDTOs);
		claimDetailsReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);
		claimDetailsReportDTO.setClaimDetailsCustomDataDTOs(customDataDTOs);
		return claimDetailsReportDTO;
	}

	private String getPaidStatus(List<ClaimBatchMaster> claimMasters, Timestamp updatedDate) {
		Date updatedDateT = new Date(updatedDate.getTime());
		for (ClaimBatchMaster cbm : claimMasters) {
			Date batchStartDate = new Date(cbm.getStartDate().getTime());
			Date batchEndDate = new Date(cbm.getEndDate().getTime());

			if ((updatedDate.after(cbm.getStartDate()) && updatedDate.before(cbm.getEndDate()))
					|| org.apache.commons.lang.time.DateUtils.isSameDay(updatedDateT, batchStartDate)
					|| org.apache.commons.lang.time.DateUtils.isSameDay(updatedDateT, batchEndDate)) {
				return (cbm.getPaidDate() == null) ? "" : DateUtils.dateToString(cbm.getPaidDate());
			}

		}
		return "NA";
	}

	@Override
	public ClaimDetailsReportDTO showBatchWiseClaimDetailsReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds, boolean hasLundinTimesheetModule) {
		ClaimDetailsReportDTO claimDetailsReportDTO = new ClaimDetailsReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		ClaimReportHeaderDTO serialNum = new ClaimReportHeaderDTO();
		serialNum.setmDataProp("serialNum");
		serialNum.setsTitle("S.NO");

		ClaimReportHeaderDTO claimNumber = new ClaimReportHeaderDTO();
		claimNumber.setmDataProp("claimNumber");
		claimNumber.setsTitle("Claim Number");

		ClaimReportHeaderDTO claimTemplateName = new ClaimReportHeaderDTO();
		claimTemplateName.setmDataProp("claimTemplateName");
		claimTemplateName.setsTitle("Claim Template");

		ClaimReportHeaderDTO claimDate = new ClaimReportHeaderDTO();
		claimDate.setmDataProp("claimDate");
		claimDate.setsTitle("Receipt Date");

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");

		ClaimReportHeaderDTO claimItem = new ClaimReportHeaderDTO();
		claimItem.setmDataProp("claimItemName");
		claimItem.setsTitle("Claim Item");
		ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
		claimantName.setmDataProp("claimantName");
		claimantName.setsTitle("Claimaint Name");
		ClaimReportHeaderDTO accountCode = new ClaimReportHeaderDTO();
		accountCode.setmDataProp("accountCode");
		accountCode.setsTitle("Account Code");

		ClaimReportHeaderDTO approvedDate = new ClaimReportHeaderDTO();
		approvedDate.setmDataProp("approvedDate");
		approvedDate.setsTitle("Approved Date");

		ClaimReportHeaderDTO convClaimAmount = new ClaimReportHeaderDTO();
		convClaimAmount.setmDataProp("convClaimAmount");
		convClaimAmount.setsTitle("Conv.Claim Amount");

		ClaimReportHeaderDTO convClaimAmountCurrency = new ClaimReportHeaderDTO();
		convClaimAmountCurrency.setmDataProp("convClaimAmountCurrency");
		convClaimAmountCurrency.setsTitle("");

		ClaimReportHeaderDTO amountBeforeTax = new ClaimReportHeaderDTO();
		amountBeforeTax.setmDataProp("amountBeforeTax");
		amountBeforeTax.setsTitle("Amount Before Tax");

		ClaimReportHeaderDTO amountBeforeTaxCurrency = new ClaimReportHeaderDTO();
		amountBeforeTaxCurrency.setmDataProp("amountBeforeTaxCurrency");
		amountBeforeTaxCurrency.setsTitle("");

		ClaimReportHeaderDTO convTaxAmount = new ClaimReportHeaderDTO();
		convTaxAmount.setmDataProp("convTaxAmount");
		convTaxAmount.setsTitle("Conv.Tax Amount");

		ClaimReportHeaderDTO convTaxAmountCurrency = new ClaimReportHeaderDTO();
		convTaxAmountCurrency.setmDataProp("convTaxAmountCurrency");
		convTaxAmountCurrency.setsTitle("");

		ClaimReportHeaderDTO remarks = new ClaimReportHeaderDTO();
		remarks.setmDataProp("remarks");
		remarks.setsTitle("Remarks");

		ClaimReportHeaderDTO block = new ClaimReportHeaderDTO();
		block.setmDataProp("block");
		block.setsTitle("Block");

		ClaimReportHeaderDTO afe = new ClaimReportHeaderDTO();
		afe.setmDataProp("afe");
		afe.setsTitle("AFE");

		ClaimReportHeaderDTO customFieldHeaderName1 = new ClaimReportHeaderDTO();
		customFieldHeaderName1.setmDataProp("customFieldHeaderName1");
		customFieldHeaderName1.setsTitle("Custom Field 1");

		ClaimReportHeaderDTO customFieldValueName1 = new ClaimReportHeaderDTO();
		customFieldValueName1.setmDataProp("customFieldValueName1");
		customFieldValueName1.setsTitle("Custom Field 1 Value");

		ClaimReportHeaderDTO customFieldHeaderName2 = new ClaimReportHeaderDTO();
		customFieldHeaderName2.setmDataProp("customFieldHeaderName2");
		customFieldHeaderName2.setsTitle("Custom Field 2");

		ClaimReportHeaderDTO customFieldValueName2 = new ClaimReportHeaderDTO();
		customFieldValueName2.setmDataProp("customFieldValueName2");
		customFieldValueName2.setsTitle("Custom Field 2 Value");

		ClaimReportHeaderDTO customFieldHeaderName3 = new ClaimReportHeaderDTO();
		customFieldHeaderName3.setmDataProp("customFieldHeaderName3");
		customFieldHeaderName3.setsTitle("Custom Field 3 ");

		ClaimReportHeaderDTO customFieldValueName3 = new ClaimReportHeaderDTO();
		customFieldValueName3.setmDataProp("customFieldValueName3");
		customFieldValueName3.setsTitle("Custom Field 3 Value");

		ClaimReportHeaderDTO customFieldHeaderName4 = new ClaimReportHeaderDTO();
		customFieldHeaderName4.setmDataProp("customFieldHeaderName4");
		customFieldHeaderName4.setsTitle("Custom Field 4 ");

		ClaimReportHeaderDTO customFieldValueName4 = new ClaimReportHeaderDTO();
		customFieldValueName4.setmDataProp("customFieldValueName4");
		customFieldValueName4.setsTitle("Custom Field 4 Value");

		ClaimReportHeaderDTO customFieldHeaderName5 = new ClaimReportHeaderDTO();
		customFieldHeaderName5.setmDataProp("customFieldHeaderName5");
		customFieldHeaderName5.setsTitle("Custom Field 5");

		ClaimReportHeaderDTO customFieldValueName5 = new ClaimReportHeaderDTO();
		customFieldValueName5.setmDataProp("customFieldValueName5");
		customFieldValueName5.setsTitle("Custom Field 5 Value");

		claimHeaderDTOs.add(serialNum);
		claimHeaderDTOs.add(claimNumber);
		claimHeaderDTOs.add(claimTemplateName);
		if (hasLundinTimesheetModule) {
			claimHeaderDTOs.add(block);
			claimHeaderDTOs.add(afe);
		}

		claimHeaderDTOs.add(claimDate);
		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(employeeName);
		claimHeaderDTOs.add(claimItem);
		claimHeaderDTOs.add(claimantName);
		claimHeaderDTOs.add(accountCode);
		claimHeaderDTOs.add(approvedDate);
		claimHeaderDTOs.add(convClaimAmount);
		claimHeaderDTOs.add(convClaimAmountCurrency);
		claimHeaderDTOs.add(amountBeforeTax);
		claimHeaderDTOs.add(amountBeforeTaxCurrency);
		claimHeaderDTOs.add(convTaxAmount);
		claimHeaderDTOs.add(convTaxAmountCurrency);
		claimHeaderDTOs.add(remarks);

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (claimReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId,
					claimReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		if (claimReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}

		ClaimBatchMaster batchMaster = claimBatchMasterDAO.findByID(claimReportsForm.getClaimBatchId());

		List<Long> multipleClaimTemplateIdList = new ArrayList<Long>();
		List<Long> multipleClaimItemIdList = new ArrayList<Long>();
		for (int count = 0; count < claimReportsForm.getMultipleClaimTemplateId().length; count++) {
			if (claimReportsForm.getMultipleClaimTemplateId()[count] != 0) {
				multipleClaimTemplateIdList.add(claimReportsForm.getMultipleClaimTemplateId()[count]);
			}
		}
		for (int count = 0; count < claimReportsForm.getMultipleClaimItemId().length; count++) {
			if (claimReportsForm.getMultipleClaimItemId()[count] != 0) {
				multipleClaimItemIdList.add(claimReportsForm.getMultipleClaimItemId()[count]);
			}
		}

		List<ClaimApplicationItem> claimApplicationItemVOList = null;
		if (claimReportsForm.getIsShortList() && generateEmpIds.isEmpty()) {
			claimApplicationItemVOList = new ArrayList<ClaimApplicationItem>();
		} else {
			claimApplicationItemVOList = claimApplicationItemDAO.findApplicationByBatch(companyId,
					claimReportsForm.getClaimBatchId(), batchMaster.getStartDate(), batchMaster.getEndDate(),
					multipleClaimTemplateIdList, claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList,
					claimReportsForm.getGroupByName(), PayAsiaConstants.CLAIM_STATUS_COMPLETED, generateEmpIds);
		}

		// Get Employee Custom Field Data
		Set<Long> empIdSet = new HashSet<Long>();
		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {
			empIdSet.add(ClaimAppItemsVO.getClaimApplication().getEmployee().getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}
		}

		CustomFieldReportDTO customFieldReportDTO = null;
		List<String> dataDictNameList = new ArrayList<String>();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		List<Object[]> customFieldObjList = new ArrayList<Object[]>();
		if (!employeeIdsList.isEmpty()) {
			customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList, employeeIdsList,
					companyId, true);
			dataDictNameList = customFieldReportDTO.getDataDictNameList();
			// Get Employee Custom Field Data
			Integer custFieldCount = 1;
			for (String dataDictName : dataDictNameList) {
				ClaimReportHeaderDTO claimHeaderDTO = new ClaimReportHeaderDTO();
				claimHeaderDTO.setmDataProp("custField" + custFieldCount);
				claimHeaderDTO.setsTitle(dataDictName);
				claimHeaderDTOs.add(claimHeaderDTO);
				custFieldCount++;
			}

			// Get Employee Custom Field Data
			customFieldObjList = customFieldReportDTO.getCustomFieldObjList();
			for (Object[] objArr : customFieldObjList) {
				customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
			}
		}

		claimHeaderDTOs.add(customFieldHeaderName1);
		claimHeaderDTOs.add(customFieldValueName1);
		claimHeaderDTOs.add(customFieldHeaderName2);
		claimHeaderDTOs.add(customFieldValueName2);
		claimHeaderDTOs.add(customFieldHeaderName3);
		claimHeaderDTOs.add(customFieldValueName3);
		claimHeaderDTOs.add(customFieldHeaderName4);
		claimHeaderDTOs.add(customFieldValueName4);
		claimHeaderDTOs.add(customFieldHeaderName5);
		claimHeaderDTOs.add(customFieldValueName5);

		int count = 1;
		int fieldCount = 0;
		List<ClaimDetailsReportDataDTO> claimDetailsDataDTOs = new ArrayList<>();
		boolean openToDependentsExists = claimApplicationItemDAO.openToDependentsExists(companyId);
		HashMap<String, String> custFieldHashMap;
		DecimalFormat thousandSeperator = new DecimalFormat("###,###.00");
		Set<String> claimCustomFieldSet = new LinkedHashSet<>();
		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {
			ClaimApplicationWorkflow applicationCompletedWorkflow = claimApplicationWorkflowDAO.findByAppIdAndStatus(
					ClaimAppItemsVO.getClaimApplication().getClaimApplicationId(),
					PayAsiaConstants.CLAIM_STATUS_COMPLETED);

			if (!ClaimAppItemsVO.isActive()) {
				continue;
			}
			ClaimDetailsReportDataDTO claimReportDataDTO = new ClaimDetailsReportDataDTO();
			custFieldHashMap = new LinkedHashMap<>();
			Set<ClaimApplicationItemCustomField> claimAppCustomFieldsSet = ClaimAppItemsVO
					.getClaimApplicationItemCustomFields();
			List<ClaimDetailsReportCustomDataDTO> customFieldDtoList = new ArrayList<>();
			int tempfieldCount = 0;
			for (ClaimApplicationItemCustomField applicationItemCustomField : claimAppCustomFieldsSet) {
				ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
				customFieldDto.setCustomFieldHeaderName("Custom Field " + fieldCount);
				customFieldDto.setCustomFieldKeyName(
						applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());

				customFieldDto.setCustomFieldValueName(applicationItemCustomField.getValue());
				customFieldDtoList.add(customFieldDto);
				claimCustomFieldSet.add(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				custFieldHashMap.put(applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName(),
						applicationItemCustomField.getValue());

				try {
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldValueName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(applicationItemCustomField.getValue()));
					ClaimDetailsReportDataDTO.class
							.getMethod("setCustomFieldHeaderName" + (tempfieldCount + 1), String.class)
							.invoke(claimReportDataDTO, String.valueOf(
									applicationItemCustomField.getClaimTemplateItemCustomField().getFieldName()));

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException exception) {
					LOGGER.error(exception.getMessage(), exception);
				}

				tempfieldCount++;
			}
			if (tempfieldCount > fieldCount) {
				fieldCount = tempfieldCount;
			}
			claimReportDataDTO.setReportCustomDataDTOs(customFieldDtoList);
			// Get Lundin Claims Details
			if (hasLundinTimesheetModule) {
				Set<ClaimApplicationItemLundinDetail> applicationItemLundinDetails = ClaimAppItemsVO
						.getClaimApplicationItemLundinDetails();
				if (!applicationItemLundinDetails.isEmpty()) {
					claimReportDataDTO.setBlock(ClaimAppItemsVO.getClaimApplicationItemLundinDetails().iterator().next()
							.getLundinBlock().getBlockCode());
					claimReportDataDTO.setAfe(ClaimAppItemsVO.getClaimApplicationItemLundinDetails().iterator().next()
							.getLundinAFE().getAfeCode());
				}
			}

			claimReportDataDTO.setSerialNum(count);
			claimReportDataDTO.setClaimNumber(ClaimAppItemsVO.getClaimApplication().getClaimNumber());
			claimReportDataDTO.setClaimTemplateName(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
					.getClaimTemplate().getTemplateName());
			claimReportDataDTO.setEmployeeId(ClaimAppItemsVO.getClaimApplication().getEmployee().getEmployeeId());
			claimReportDataDTO.setEmployeeNo(ClaimAppItemsVO.getClaimApplication().getEmployee().getEmployeeNumber());
			claimReportDataDTO.setEmployeeName(getEmployeeName(ClaimAppItemsVO.getClaimApplication().getEmployee()));

			if (applicationCompletedWorkflow != null) {
				claimReportDataDTO
						.setApprovedDate(DateUtils.timeStampToString(applicationCompletedWorkflow.getCreatedDate()));
			} else {
				claimReportDataDTO.setApprovedDate(
						DateUtils.timeStampToString(ClaimAppItemsVO.getClaimApplication().getUpdatedDate()));
			}

			claimReportDataDTO.setClaimDate(DateUtils.timeStampToString(ClaimAppItemsVO.getClaimDate()));
			claimReportDataDTO.setClaimItemName(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getClaimItemName());
			claimReportDataDTO.setAccountCode(ClaimAppItemsVO.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimItemMaster().getAccountCode());
			claimReportDataDTO
					.setConvClaimAmount(thousandSeperator.format(ClaimAppItemsVO.getClaimAmount().doubleValue()));
			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvClaimAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getAmountBeforeTax() != null) {
				claimReportDataDTO.setAmountBeforeTax(
						thousandSeperator.format(ClaimAppItemsVO.getAmountBeforeTax().doubleValue()));
			} else {
				claimReportDataDTO.setAmountBeforeTax("");
			}

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setAmountBeforeTaxCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			if (ClaimAppItemsVO.getTaxAmount() != null) {
				claimReportDataDTO
						.setConvTaxAmount(thousandSeperator.format(ClaimAppItemsVO.getTaxAmount().doubleValue()));
			} else {
				claimReportDataDTO.setConvTaxAmount("");
			}

			claimReportDataDTO.setClaimantName(ClaimAppItemsVO.getClaimantName());

			if (ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
					.getDefaultCurrency() == null) {
				claimReportDataDTO
						.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication().getEmployeeClaimTemplate()
								.getClaimTemplate().getCompany().getCurrencyMaster().getCurrencyCode());
			} else {
				claimReportDataDTO.setConvTaxAmountCurrency(ClaimAppItemsVO.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			}
			claimReportDataDTO.setRemarks(ClaimAppItemsVO.getRemarks());

			claimDetailsDataDTOs.add(claimReportDataDTO);
			count++;

			// Add Employee Custom Field
			if (!customFieldObjList.isEmpty()) {
				int counter = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(claimReportDataDTO.getEmployeeId());
				for (Object object : objArr) {
					if (counter != 0) {
						if (object != null) {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), String.valueOf(object));
						} else {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), "");
						}

						try {
							ClaimDetailsReportDataDTO.class.getMethod("setCustField" + counter, String.class)
									.invoke(claimReportDataDTO, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					counter++;
				}
			}
			claimReportDataDTO.setCustFieldMap(custFieldHashMap);
		}
		for (String claimCustField : claimCustomFieldSet) {
			dataDictNameList.add(claimCustField);
		}
		claimDetailsReportDTO.setDataDictNameList(dataDictNameList);

		if (!openToDependentsExists) {
			claimHeaderDTOs.remove(claimantName);
		}
		List<ClaimDetailsReportCustomDataDTO> customDataDTOs = new ArrayList<>();
		for (int countF = 1; countF <= fieldCount; countF++) {
			ClaimDetailsReportCustomDataDTO customFieldDto = new ClaimDetailsReportCustomDataDTO();
			customFieldDto.setCustomFieldHeaderName("Custom Field " + countF);
			customDataDTOs.add(customFieldDto);
		}

		BeanComparator beanComparator = new BeanComparator("claimNumber");
		Collections.sort(claimDetailsDataDTOs, beanComparator);
		claimDetailsReportDTO.setClaimDetailsDataDTOs(claimDetailsDataDTOs);
		claimDetailsReportDTO.setClaimDetailsCustomDataDTOs(customDataDTOs);
		claimDetailsReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);

		return claimDetailsReportDTO;
	}

	@Override
	public EmployeeWiseConsolidatedClaimReportDTO showEmployeeWiseConsClaimReport(Long companyId,
			ClaimReportsForm claimReportsForm, Long employeeId, String[] dataDictionaryIds) {
		EmployeeWiseConsolidatedClaimReportDTO empWiseclaimDetailsReportDTO = new EmployeeWiseConsolidatedClaimReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		ClaimReportHeaderDTO serialNum = new ClaimReportHeaderDTO();
		serialNum.setmDataProp("serialNum");
		serialNum.setsTitle("S.NO");

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");

		ClaimReportHeaderDTO claimTemplateName = new ClaimReportHeaderDTO();
		claimTemplateName.setmDataProp("claimTemplateName");
		claimTemplateName.setsTitle("Claim Template");

		ClaimReportHeaderDTO claimItemName = new ClaimReportHeaderDTO();
		claimItemName.setmDataProp("claimItemName");
		claimItemName.setsTitle("Claim Item");

		ClaimReportHeaderDTO entitlement = new ClaimReportHeaderDTO();
		entitlement.setmDataProp("entitlement");
		entitlement.setsTitle("Entitlement");

		ClaimReportHeaderDTO itemEntitlement = new ClaimReportHeaderDTO();
		itemEntitlement.setmDataProp("itemEntitlement");
		itemEntitlement.setsTitle("ItemEntitlement");

		ClaimReportHeaderDTO claimed = new ClaimReportHeaderDTO();
		claimed.setmDataProp("claimed");
		claimed.setsTitle("Claimed");

		ClaimReportHeaderDTO entitlementBalance = new ClaimReportHeaderDTO();
		entitlementBalance.setmDataProp("entitlementBalance");
		entitlementBalance.setsTitle("Balance");

		claimHeaderDTOs.add(employeeName);
		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(claimTemplateName);
		claimHeaderDTOs.add(claimItemName);
		claimHeaderDTOs.add(entitlement);
		claimHeaderDTOs.add(itemEntitlement);
		claimHeaderDTOs.add(claimed);
		claimHeaderDTOs.add(entitlementBalance);

		Boolean sortByStatus = false;
		if ("EmployeeNumber".equalsIgnoreCase(claimReportsForm.getSortBy())) {
			sortByStatus = true;
		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (claimReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId,
					claimReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		if (claimReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}
		StringBuilder builder = new StringBuilder();
		for (BigInteger empId : generateEmpIds) {
			builder = builder.append(String.valueOf(empId));
			builder = builder.append(",");

		}
		String employeeIds = builder.toString();

		String claimItemList = "";
		StringBuilder claimItemBuilder = new StringBuilder();
		for (Long claimItemId : claimReportsForm.getMultipleClaimItemId()) {
			claimItemBuilder = claimItemBuilder.append(String.valueOf(claimItemId));
			claimItemBuilder = claimItemBuilder.append(",");
		}
		claimItemList = claimItemBuilder.toString();

		List<EmployeeWiseConsolidatedClaimReportDataDTO> claimDetailsDataDTOList = null;
		if (claimReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds)) {
			claimDetailsDataDTOList = new ArrayList<EmployeeWiseConsolidatedClaimReportDataDTO>();
		} else {
			claimDetailsDataDTOList = claimApplicationDAO.getEmloyeeWiseConsClaimReportProc(companyId,
					claimReportsForm.getReportYear(), claimReportsForm.getClaimTemplateId(), claimItemList, null,
					sortByStatus, employeeIds, claimReportsForm.isIncludeResignedEmployees());
		}

		// Get Employee Custom Field Data
		Set<Long> empIdSet = new HashSet<Long>();
		for (EmployeeWiseConsolidatedClaimReportDataDTO claimRepVO : claimDetailsDataDTOList) {
			empIdSet.add(claimRepVO.getEmployeeId());

			ClaimItemEntertainment ClaimItemEntertainment = claimItemEntertainmentDAO
					.findByCondition(claimRepVO.getEmployeeId(), claimRepVO.getClaimItemId(), companyId);
			if (ClaimItemEntertainment != null) {
				claimRepVO.setItemEntitlement(ClaimItemEntertainment.getAmount());
				if (claimRepVO.getEntitlementBalance() != null && ClaimItemEntertainment.getAmount() != null) {
					claimRepVO.setEntitlementBalance("" + (Float.valueOf(claimRepVO.getEntitlementBalance())
							+ Float.valueOf(ClaimItemEntertainment.getAmount())));
				}
			}

		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}
		}
		CustomFieldReportDTO customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList,
				employeeIdsList, companyId, true);
		List<String> dataDictNameList = customFieldReportDTO.getDataDictNameList();
		// Get Employee Custom Field Data
		Integer custFieldCount = 1;
		for (String dataDictName : dataDictNameList) {
			ClaimReportHeaderDTO claimHeaderDTO = new ClaimReportHeaderDTO();
			claimHeaderDTO.setmDataProp("custField" + custFieldCount);
			claimHeaderDTO.setsTitle(dataDictName);
			claimHeaderDTOs.add(claimHeaderDTO);
			custFieldCount++;
		}

		// Get Employee Custom Field Data
		List<Object[]> customFieldObjList = customFieldReportDTO.getCustomFieldObjList();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		for (Object[] objArr : customFieldObjList) {
			customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
		}

		HashMap<String, String> custFieldHashMap;
		for (EmployeeWiseConsolidatedClaimReportDataDTO claimDetails : claimDetailsDataDTOList) {
			custFieldHashMap = new LinkedHashMap<String, String>();
			// Add Employee Custom Field
			if (!customFieldObjList.isEmpty()) {
				int counter = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(claimDetails.getEmployeeId());
				for (Object object : objArr) {
					if (counter != 0) {
						if (object != null) {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), String.valueOf(object));
						}

						try {
							EmployeeWiseConsolidatedClaimReportDataDTO.class
									.getMethod("setCustField" + counter, String.class)
									.invoke(claimDetails, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					counter++;
				}
			}
			claimDetails.setCustFieldMap(custFieldHashMap);
		}
		empWiseclaimDetailsReportDTO.setDataDictNameList(dataDictNameList);
		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(claimDetailsDataDTOList, beanComparator);
		empWiseclaimDetailsReportDTO.setEmpWiseclaimDataDTOs(claimDetailsDataDTOList);
		empWiseclaimDetailsReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);

		return empWiseclaimDetailsReportDTO;
	}

	@Override
	public EmployeeWiseConsolidatedClaimReportDTO showEmpWiseConsClaimReportII(Long companyId,
			ClaimReportsForm claimReportsForm, Long employeeId, String[] dataDictionaryIds) {
		EmployeeWiseConsolidatedClaimReportDTO empWiseclaimDetailsReportDTO = new EmployeeWiseConsolidatedClaimReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		ClaimReportHeaderDTO serialNum = new ClaimReportHeaderDTO();
		serialNum.setmDataProp("serialNum");
		serialNum.setsTitle("S.NO");

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");

		ClaimReportHeaderDTO claimTemplateName = new ClaimReportHeaderDTO();
		claimTemplateName.setmDataProp("claimTemplateName");
		claimTemplateName.setsTitle("Claim Template");

		ClaimReportHeaderDTO claimItemName = new ClaimReportHeaderDTO();
		claimItemName.setmDataProp("claimItemName");
		claimItemName.setsTitle("Claim Item");

		ClaimReportHeaderDTO entitlement = new ClaimReportHeaderDTO();
		entitlement.setmDataProp("entitlement");
		entitlement.setsTitle("Entitlement");

		ClaimReportHeaderDTO itemEntitlement = new ClaimReportHeaderDTO();
		entitlement.setmDataProp("itemEntitlement");
		entitlement.setsTitle("ItemEntitlement");

		ClaimReportHeaderDTO claimed = new ClaimReportHeaderDTO();
		claimed.setmDataProp("claimed");
		claimed.setsTitle("Claimed");

		ClaimReportHeaderDTO entitlementBalance = new ClaimReportHeaderDTO();
		entitlementBalance.setmDataProp("entitlementBalance");
		entitlementBalance.setsTitle("Balance");

		claimHeaderDTOs.add(employeeName);
		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(claimTemplateName);
		claimHeaderDTOs.add(claimItemName);
		claimHeaderDTOs.add(entitlement);
		// claimHeaderDTOs.add(itemEntitlement);
		claimHeaderDTOs.add(claimed);
		claimHeaderDTOs.add(entitlementBalance);

		Boolean sortByStatus = false;
		if ("EmployeeNumber".equalsIgnoreCase(claimReportsForm.getSortBy())) {
			sortByStatus = true;
		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (claimReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId,
					claimReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		if (claimReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}
		StringBuilder builder = new StringBuilder();
		for (BigInteger empId : generateEmpIds) {
			builder = builder.append(String.valueOf(empId));
			builder = builder.append(",");

		}
		String employeeIds = builder.toString();

		String claimItemList = "";
		StringBuilder claimItemBuilder = new StringBuilder();
		for (Long claimItemId : claimReportsForm.getMultipleClaimItemId()) {
			claimItemBuilder = claimItemBuilder.append(String.valueOf(claimItemId));
			claimItemBuilder = claimItemBuilder.append(",");
		}
		claimItemList = claimItemBuilder.toString();

		List<EmployeeWiseConsolidatedClaimReportDataDTO> claimDetailsDataDTOList = null;
		if (claimReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds)) {
			claimDetailsDataDTOList = new ArrayList<EmployeeWiseConsolidatedClaimReportDataDTO>();
		} else {
			claimDetailsDataDTOList = claimApplicationDAO.getEmloyeeWiseConsClaimReportProc(companyId,
					claimReportsForm.getReportYear(), claimReportsForm.getClaimTemplateId(), claimItemList, true,
					sortByStatus, employeeIds, claimReportsForm.isIncludeResignedEmployees());
		}

		for (EmployeeWiseConsolidatedClaimReportDataDTO employeeWiseConsolidatedClaimReportDataDTO : claimDetailsDataDTOList) {
			ClaimItemEntertainment ClaimItemEntertainment = claimItemEntertainmentDAO.findByCondition(
					employeeWiseConsolidatedClaimReportDataDTO.getEmployeeId(),
					employeeWiseConsolidatedClaimReportDataDTO.getClaimItemId(), companyId);
			if (ClaimItemEntertainment != null) {
				employeeWiseConsolidatedClaimReportDataDTO.setItemEntitlement(ClaimItemEntertainment.getAmount());
				if (employeeWiseConsolidatedClaimReportDataDTO.getEntitlementBalance() != null
						&& ClaimItemEntertainment.getAmount() != null) {
					employeeWiseConsolidatedClaimReportDataDTO.setEntitlementBalance(
							"" + (Float.valueOf(employeeWiseConsolidatedClaimReportDataDTO.getEntitlementBalance())
									+ Float.valueOf(ClaimItemEntertainment.getAmount())));
				}
			}
		}
		// Get Employee Custom Field Data
		Set<Long> empIdSet = new HashSet<Long>();
		for (EmployeeWiseConsolidatedClaimReportDataDTO claimRepVO : claimDetailsDataDTOList) {
			empIdSet.add(claimRepVO.getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}
		}
		CustomFieldReportDTO customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList,
				employeeIdsList, companyId, true);
		List<String> dataDictNameList = customFieldReportDTO.getDataDictNameList();
		// Get Employee Custom Field Data
		Integer custFieldCount = 1;
		for (String dataDictName : dataDictNameList) {
			ClaimReportHeaderDTO claimHeaderDTO = new ClaimReportHeaderDTO();
			claimHeaderDTO.setmDataProp("custField" + custFieldCount);
			claimHeaderDTO.setsTitle(dataDictName);
			claimHeaderDTOs.add(claimHeaderDTO);
			custFieldCount++;
		}

		// Get Employee Custom Field Data
		List<Object[]> customFieldObjList = customFieldReportDTO.getCustomFieldObjList();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		for (Object[] objArr : customFieldObjList) {
			customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
		}

		HashMap<String, String> custFieldHashMap;
		for (EmployeeWiseConsolidatedClaimReportDataDTO claimDetails : claimDetailsDataDTOList) {
			custFieldHashMap = new LinkedHashMap<String, String>();
			// Add Employee Custom Field
			if (!customFieldObjList.isEmpty()) {
				int counter = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(claimDetails.getEmployeeId());
				for (Object object : objArr) {
					if (counter != 0) {
						if (object != null) {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), String.valueOf(object));
						}

						try {
							EmployeeWiseConsolidatedClaimReportDataDTO.class
									.getMethod("setCustField" + counter, String.class)
									.invoke(claimDetails, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					counter++;
				}
			}
			claimDetails.setCustFieldMap(custFieldHashMap);
		}
		empWiseclaimDetailsReportDTO.setDataDictNameList(dataDictNameList);

		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(claimDetailsDataDTOList, beanComparator);
		empWiseclaimDetailsReportDTO.setEmpWiseclaimDataDTOs(claimDetailsDataDTOList);
		empWiseclaimDetailsReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);

		return empWiseclaimDetailsReportDTO;
	}

	@Override
	public MonthlyConsolidatedFinanceReportDTO showMonthlyConsFinanceReport(Long companyId,
			ClaimReportsForm claimReportsForm, Long employeeId, String[] dataDictionaryIds) {

		MonthlyConsolidatedFinanceReportDTO monthlyConsFinanceReportDTO = new MonthlyConsolidatedFinanceReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");

		ClaimReportHeaderDTO claimTemplateName = new ClaimReportHeaderDTO();
		claimTemplateName.setmDataProp("claimTemplateName");
		claimTemplateName.setsTitle("Claim Template");

		ClaimReportHeaderDTO claimItemName = new ClaimReportHeaderDTO();
		claimItemName.setmDataProp("claimItemName");
		claimItemName.setsTitle("Claim Item");

		ClaimReportHeaderDTO currency = new ClaimReportHeaderDTO();
		currency.setmDataProp("currency");
		currency.setsTitle("Currency");

		ClaimReportHeaderDTO subTotal = new ClaimReportHeaderDTO();
		subTotal.setmDataProp("subTotal");
		subTotal.setsTitle("Sub Total");

		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(employeeName);
		claimHeaderDTOs.add(claimTemplateName);
		claimHeaderDTOs.add(claimItemName);
		claimHeaderDTOs.add(currency);
		claimHeaderDTOs.add(subTotal);

		Boolean sortByStatus = false;
		if ("EmployeeNumber".equalsIgnoreCase(claimReportsForm.getSortBy())) {
			sortByStatus = true;
		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (claimReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId,
					claimReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		if (claimReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}
		StringBuilder builder = new StringBuilder();
		for (BigInteger empId : generateEmpIds) {
			builder = builder.append(String.valueOf(empId));
			builder = builder.append(",");

		}
		String employeeIds = builder.toString();

		String claimItemList = "";
		StringBuilder claimItemBuilder = new StringBuilder();
		for (Long claimItemId : claimReportsForm.getMultipleClaimItemId()) {
			claimItemBuilder = claimItemBuilder.append(String.valueOf(claimItemId));
			claimItemBuilder = claimItemBuilder.append(",");
		}
		claimItemList = claimItemBuilder.toString();

		String claimTemplateList = "";
		StringBuilder claimTemplateBuilder = new StringBuilder();
		for (Long claimTemplateId : claimReportsForm.getMultipleClaimTemplateId()) {
			if (claimTemplateId != 0) {
				claimTemplateBuilder = claimTemplateBuilder.append(String.valueOf(claimTemplateId));
				claimTemplateBuilder = claimTemplateBuilder.append(",");
			}
		}
		claimTemplateList = claimTemplateBuilder.toString();

		List<MonthlyConsolidatedFinanceReportDataDTO> montlyConsFinDataDTOs = null;
		if (claimReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds)) {
			montlyConsFinDataDTOs = new ArrayList<MonthlyConsolidatedFinanceReportDataDTO>();
		} else {
			montlyConsFinDataDTOs = claimApplicationDAO.getMonthlyConsFinanceReportProc(companyId,
					DateUtils.stringToTimestamp(claimReportsForm.getStartDate()),
					DateUtils.stringToTimestamp(claimReportsForm.getEndDate()), claimTemplateList, claimItemList,
					sortByStatus, employeeIds, claimReportsForm.isIncludeResignedEmployees());
		}

		// Get Employee Custom Field Data
		Set<Long> empIdSet = new HashSet<Long>();
		for (MonthlyConsolidatedFinanceReportDataDTO claimRepVO : montlyConsFinDataDTOs) {
			empIdSet.add(claimRepVO.getEmployeeId());
		}
		List<Long> employeeIdsList = new ArrayList<>(empIdSet);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long.parseLong(dataDictionaryIds[count]));
			}
		}
		CustomFieldReportDTO customFieldReportDTO = generalLogic.getCustomFieldDataForLeaveReport(dataDictionaryIdsList,
				employeeIdsList, companyId, true);
		List<String> dataDictNameList = customFieldReportDTO.getDataDictNameList();
		// Get Employee Custom Field Data
		Integer custFieldCount = 1;
		for (String dataDictName : dataDictNameList) {
			ClaimReportHeaderDTO claimHeaderDTO = new ClaimReportHeaderDTO();
			claimHeaderDTO.setmDataProp("custField" + custFieldCount);
			claimHeaderDTO.setsTitle(dataDictName);
			claimHeaderDTOs.add(claimHeaderDTO);
			custFieldCount++;
		}

		// Get Employee Custom Field Data
		List<Object[]> customFieldObjList = customFieldReportDTO.getCustomFieldObjList();
		HashMap<Long, Object[]> customFieldObjByEmpIdMap = new HashMap<>();
		for (Object[] objArr : customFieldObjList) {
			customFieldObjByEmpIdMap.put(Long.valueOf(objArr[0].toString()), objArr);
		}

		HashMap<String, String> custFieldHashMap;
		for (MonthlyConsolidatedFinanceReportDataDTO claimDetails : montlyConsFinDataDTOs) {
			custFieldHashMap = new LinkedHashMap<String, String>();
			// Add Employee Custom Field
			if (!customFieldObjList.isEmpty()) {
				int counter = 0;

				Object[] objArr = customFieldObjByEmpIdMap.get(claimDetails.getEmployeeId());
				for (Object object : objArr) {
					if (counter != 0) {
						if (object != null) {
							custFieldHashMap.put(dataDictNameList.get(counter - 1), String.valueOf(object));
						}

						try {
							MonthlyConsolidatedFinanceReportDataDTO.class
									.getMethod("setCustField" + counter, String.class)
									.invoke(claimDetails, String.valueOf(object));
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					counter++;
				}
			}
			claimDetails.setCustFieldMap(custFieldHashMap);
		}
		monthlyConsFinanceReportDTO.setDataDictNameList(dataDictNameList);

		// ///
		List<MonthlyConsFinReportDTO> summarryDTOs = new ArrayList<>();
		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreferenceVO != null) {
			monthlyConsFinanceReportDTO.setShowMonthlyConsFinReportGroupingByEmp(
					claimPreferenceVO.isShowMonthlyConsFinReportGroupingByEmp());
			if (claimPreferenceVO.isShowMonthlyConsFinReportGroupingByEmp()) {

				MonthlyConsFinReportDTO monthlyConsFinReportDTO = null;
				HashMap<String, List<MonthlyConsolidatedFinanceReportDataDTO>> empTimesheetDetailMap = null;
				List<Long> empIdTraversedList = new ArrayList<Long>();
				for (MonthlyConsolidatedFinanceReportDataDTO otTimesheetVO : montlyConsFinDataDTOs) {
					if (!empIdTraversedList.contains(otTimesheetVO.getEmployeeId())) {
						empTimesheetDetailMap = new HashMap<>();
						List<MonthlyConsolidatedFinanceReportDataDTO> detailDataDTOList = new ArrayList<MonthlyConsolidatedFinanceReportDataDTO>();
						monthlyConsFinReportDTO = new MonthlyConsFinReportDTO();
						monthlyConsFinReportDTO.setEmployeeId(otTimesheetVO.getEmployeeId());
						monthlyConsFinReportDTO.setEmployeeNumber(otTimesheetVO.getEmployeeNo());
						monthlyConsFinReportDTO.setEmployeeName(
								getEmployeeName(otTimesheetVO.getFirstName(), otTimesheetVO.getLastName()));

						for (MonthlyConsolidatedFinanceReportDataDTO ingersollOTTimesheetDetail : montlyConsFinDataDTOs) {
							if (!ingersollOTTimesheetDetail.getEmployeeId().equals(otTimesheetVO.getEmployeeId())) {
								continue;
							}
							detailDataDTOList.add(ingersollOTTimesheetDetail);
						}
						empTimesheetDetailMap.put(otTimesheetVO.getEmployeeNo(), detailDataDTOList);
						monthlyConsFinReportDTO.setMonthlyConFinDetailMap(empTimesheetDetailMap);
						summarryDTOs.add(monthlyConsFinReportDTO);
					}
					empIdTraversedList.add(otTimesheetVO.getEmployeeId());
				}
			}
		}

		// ///

		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(montlyConsFinDataDTOs, beanComparator);
		monthlyConsFinanceReportDTO.setMontlyConsFinDataDTOs(montlyConsFinDataDTOs);
		monthlyConsFinanceReportDTO.setMontlyConsFinDataNewDTOs(summarryDTOs);
		monthlyConsFinanceReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);

		return monthlyConsFinanceReportDTO;
	}

	@Override
	public EmployeeWiseTemplateClaimReportDTO showEmpWiseTemplateClaimReport(Long companyId,
			ClaimReportsForm claimReportsForm, Long employeeId) {

		EmployeeWiseTemplateClaimReportDTO empWiseTemplateReportDTO = new EmployeeWiseTemplateClaimReportDTO();
		List<ClaimReportHeaderDTO> claimHeaderDTOs = new ArrayList<>();

		ClaimReportHeaderDTO serialNum = new ClaimReportHeaderDTO();
		serialNum.setmDataProp("serialNum");
		serialNum.setsTitle("S.NO");

		ClaimReportHeaderDTO employeeNumber = new ClaimReportHeaderDTO();
		employeeNumber.setmDataProp("employeeNo");
		employeeNumber.setsTitle("Employee Number");

		ClaimReportHeaderDTO employeeName = new ClaimReportHeaderDTO();
		employeeName.setmDataProp("employeeName");
		employeeName.setsTitle("Employee Name");

		ClaimReportHeaderDTO claimTemplateName = new ClaimReportHeaderDTO();
		claimTemplateName.setmDataProp("claimTemplateName");
		claimTemplateName.setsTitle("Claim Template");

		ClaimReportHeaderDTO claimItemName = new ClaimReportHeaderDTO();
		claimItemName.setmDataProp("claimItemName");
		claimItemName.setsTitle("Claim Item");

		ClaimReportHeaderDTO entitlement = new ClaimReportHeaderDTO();
		entitlement.setmDataProp("entitlement");
		entitlement.setsTitle("Entitlement");

		ClaimReportHeaderDTO itemEntitlement = new ClaimReportHeaderDTO();
		itemEntitlement.setmDataProp("itemEntitlement");
		itemEntitlement.setsTitle("ItemEntitlement");

		ClaimReportHeaderDTO claimed = new ClaimReportHeaderDTO();
		claimed.setmDataProp("claimed");
		claimed.setsTitle("Claimed");

		ClaimReportHeaderDTO entitlementBalance = new ClaimReportHeaderDTO();
		entitlementBalance.setmDataProp("entitlementBalance");
		entitlementBalance.setsTitle("Balance");

		claimHeaderDTOs.add(serialNum);
		claimHeaderDTOs.add(employeeNumber);
		claimHeaderDTOs.add(employeeName);
		claimHeaderDTOs.add(claimTemplateName);
		claimHeaderDTOs.add(claimItemName);
		claimHeaderDTOs.add(entitlement);
		claimHeaderDTOs.add(itemEntitlement);
		claimHeaderDTOs.add(claimed);
		claimHeaderDTOs.add(entitlementBalance);

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		List<BigInteger> generateEmpIds = null;
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> reportShortListEmployeeIds = null;
		if (claimReportsForm.getIsShortList()) {
			EmployeeShortListDTO reportShortList = generalLogic.getShortListEmployeeIdsForReports(companyId,
					claimReportsForm.getMetaData());

			reportShortListEmployeeIds = reportShortList.getShortListEmployeeIds();

			if (employeeShortListDTO.getEmployeeShortList()) {
				reportShortListEmployeeIds.retainAll(companyShortListEmployeeIds);
			}
		}

		if (claimReportsForm.getIsShortList()) {
			generateEmpIds = new ArrayList<>(reportShortListEmployeeIds);
		} else {
			generateEmpIds = new ArrayList<>(companyShortListEmployeeIds);
		}

		StringBuilder builder = new StringBuilder();
		for (BigInteger empId : generateEmpIds) {
			builder = builder.append(String.valueOf(empId));
			builder = builder.append(",");

		}
		String employeeIds = builder.toString();
		List<EmployeeWiseTemplateClaimReportDataDTO> empWiseTemplateDataDTOs = null;
		if (claimReportsForm.getIsShortList() && StringUtils.isBlank(employeeIds)) {
			empWiseTemplateDataDTOs = new ArrayList<EmployeeWiseTemplateClaimReportDataDTO>();
		} else {
			empWiseTemplateDataDTOs = claimApplicationDAO.getEmloyeeWiseTemplateClaimReportProc(companyId,
					claimReportsForm.getReportYear(), claimReportsForm.getClaimTemplateId(), employeeIds);
		}

		List<EmployeeWiseTemplateClaimReportDataDTO> empDataWiseDTOs = new ArrayList<>();
		String empNumber = "";
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
		Integer counter = 1;
		Integer dtoSize = empWiseTemplateDataDTOs.size();
		Double totalclaimedAmount = (Double) 0.0;
		Integer serialNumber = 1;
		String TempclaimTemplateName = null;
		for (EmployeeWiseTemplateClaimReportDataDTO employeeWiseTemplateClaimReportDataDTO : empWiseTemplateDataDTOs) {

			totalclaimedAmount = totalclaimedAmount
					+ Double.parseDouble(employeeWiseTemplateClaimReportDataDTO.getClaimed());

			if (empNumber.equals(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo())) {

				EmployeeWiseTemplateClaimReportDataDTO employeeWiseTemplateClaimReportItemData = new EmployeeWiseTemplateClaimReportDataDTO();
				employeeWiseTemplateClaimReportItemData
						.setClaimItemName(employeeWiseTemplateClaimReportDataDTO.getClaimItemName());

				if (TempclaimTemplateName != null) {
					ClaimItemEntertainment ClaimItemEntertainment = claimItemEntertainmentDAO.findByCondition(
							employeeWiseTemplateClaimReportDataDTO.getEmployeeId(),
							employeeWiseTemplateClaimReportDataDTO.getClaimItemId(), companyId);
					if (ClaimItemEntertainment != null) {
						employeeWiseTemplateClaimReportItemData.setItemEntitlement(ClaimItemEntertainment.getAmount());
					}
				}
				employeeWiseTemplateClaimReportItemData.setClaimTemplateName("");
				employeeWiseTemplateClaimReportItemData.setClaimed(employeeWiseTemplateClaimReportDataDTO.getClaimed());
				employeeWiseTemplateClaimReportItemData
						.setEmployeeNo(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo());
				empDataWiseDTOs.add(employeeWiseTemplateClaimReportItemData);

				if (counter < dtoSize) {

					EmployeeWiseTemplateClaimReportDataDTO nextRecord = empWiseTemplateDataDTOs.get(counter);
					if (!nextRecord.getEmployeeNo().equals(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo())) {

						EmployeeWiseTemplateClaimReportDataDTO employeeWiseTemplateClaimReportTotal = new EmployeeWiseTemplateClaimReportDataDTO();

						employeeWiseTemplateClaimReportTotal.setClaimed(decimalFmt.format(totalclaimedAmount));
						employeeWiseTemplateClaimReportTotal
								.setEmployeeNo(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo());
						empDataWiseDTOs.add(employeeWiseTemplateClaimReportTotal);

						Double entBalance = Double.parseDouble(employeeWiseTemplateClaimReportDataDTO.getEntitlement())
								- totalclaimedAmount;

						if (entBalance < 0
								&& employeeWiseTemplateClaimReportDataDTO.getClaimBalanceFromOtherCT() != false) {
							entBalance = (double) 0;
						}

						employeeWiseTemplateClaimReportTotal.setEntitlementBalance(decimalFmt.format(entBalance));
						totalclaimedAmount = (Double) 0.0;

					}

				} else if (counter.intValue() == dtoSize.intValue()) {

					EmployeeWiseTemplateClaimReportDataDTO employeeWiseTemplateClaimReportTotal = new EmployeeWiseTemplateClaimReportDataDTO();

					employeeWiseTemplateClaimReportTotal.setClaimed(decimalFmt.format(totalclaimedAmount));
					employeeWiseTemplateClaimReportTotal
							.setEmployeeNo(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo());
					empDataWiseDTOs.add(employeeWiseTemplateClaimReportTotal);

					Double entBalance = Double.parseDouble(employeeWiseTemplateClaimReportDataDTO.getEntitlement())
							- totalclaimedAmount;

					if (entBalance < 0
							&& employeeWiseTemplateClaimReportDataDTO.getClaimBalanceFromOtherCT() != false) {
						entBalance = (double) 0;
					}

					employeeWiseTemplateClaimReportTotal.setEntitlementBalance(decimalFmt.format(entBalance));

				}

				counter++;

			} else {

				EmployeeWiseTemplateClaimReportDataDTO employeeWiseTemplateClaimReportEmployeeData = new EmployeeWiseTemplateClaimReportDataDTO();
				employeeWiseTemplateClaimReportEmployeeData.setSerialNumber(serialNumber.toString());
				serialNumber++;
				employeeWiseTemplateClaimReportEmployeeData
						.setEmployeeName(employeeWiseTemplateClaimReportDataDTO.getEmployeeName());
				employeeWiseTemplateClaimReportEmployeeData
						.setClaimTemplateName(employeeWiseTemplateClaimReportDataDTO.getClaimTemplateName());
				TempclaimTemplateName = employeeWiseTemplateClaimReportDataDTO.getClaimTemplateName();
				employeeWiseTemplateClaimReportEmployeeData
						.setEmployeeNo(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo());
				employeeWiseTemplateClaimReportEmployeeData
						.setEntitlement(employeeWiseTemplateClaimReportDataDTO.getEntitlement());
				employeeWiseTemplateClaimReportEmployeeData
						.setExcelEmployeeNumber(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo());
				empDataWiseDTOs.add(employeeWiseTemplateClaimReportEmployeeData);
				EmployeeWiseTemplateClaimReportDataDTO employeeWiseTemplateClaimReportItemData = new EmployeeWiseTemplateClaimReportDataDTO();
				employeeWiseTemplateClaimReportItemData
						.setEmployeeNo(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo());
				employeeWiseTemplateClaimReportItemData
						.setClaimItemName(employeeWiseTemplateClaimReportDataDTO.getClaimItemName());
				if (TempclaimTemplateName != null) {
					ClaimItemEntertainment ClaimItemEntertainment = claimItemEntertainmentDAO.findByCondition(
							employeeWiseTemplateClaimReportDataDTO.getEmployeeId(),
							employeeWiseTemplateClaimReportDataDTO.getClaimItemId(), companyId);
					if (ClaimItemEntertainment != null) {
						employeeWiseTemplateClaimReportItemData.setItemEntitlement(ClaimItemEntertainment.getAmount());
					}
				}

				employeeWiseTemplateClaimReportItemData.setClaimTemplateName("");
				employeeWiseTemplateClaimReportItemData.setClaimed(employeeWiseTemplateClaimReportDataDTO.getClaimed());
				empDataWiseDTOs.add(employeeWiseTemplateClaimReportItemData);
				empNumber = employeeWiseTemplateClaimReportDataDTO.getEmployeeNo();

				if (counter < dtoSize) {

					EmployeeWiseTemplateClaimReportDataDTO nextRecord = empWiseTemplateDataDTOs.get(counter);
					if (!nextRecord.getEmployeeNo().equals(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo())) {

						EmployeeWiseTemplateClaimReportDataDTO employeeWiseTemplateClaimReportTotal = new EmployeeWiseTemplateClaimReportDataDTO();

						employeeWiseTemplateClaimReportTotal.setClaimed(decimalFmt.format(totalclaimedAmount));
						employeeWiseTemplateClaimReportTotal
								.setEmployeeNo(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo());

						Double entBalance = Double.parseDouble(employeeWiseTemplateClaimReportDataDTO.getEntitlement())
								- totalclaimedAmount;

						if (entBalance < 0
								&& employeeWiseTemplateClaimReportDataDTO.getClaimBalanceFromOtherCT() != false) {
							entBalance = (double) 0;
						}
						employeeWiseTemplateClaimReportTotal.setEntitlementBalance(decimalFmt.format(entBalance));

						empDataWiseDTOs.add(employeeWiseTemplateClaimReportTotal);
						totalclaimedAmount = (Double) 0.0;

					}
				} else if (counter.intValue() == dtoSize.intValue()) {

					EmployeeWiseTemplateClaimReportDataDTO employeeWiseTemplateClaimReportTotal = new EmployeeWiseTemplateClaimReportDataDTO();

					employeeWiseTemplateClaimReportTotal.setClaimed(decimalFmt.format(totalclaimedAmount));
					employeeWiseTemplateClaimReportTotal
							.setEmployeeNo(employeeWiseTemplateClaimReportDataDTO.getEmployeeNo());
					empDataWiseDTOs.add(employeeWiseTemplateClaimReportTotal);
					Double entBalance = Double.parseDouble(employeeWiseTemplateClaimReportDataDTO.getEntitlement())
							- totalclaimedAmount;

					if (entBalance < 0
							&& employeeWiseTemplateClaimReportDataDTO.getClaimBalanceFromOtherCT() != false) {
						entBalance = (double) 0;
					}

					employeeWiseTemplateClaimReportTotal.setEntitlementBalance(decimalFmt.format(entBalance));

				}

				counter++;
			}

		}

		empWiseTemplateReportDTO.setEmpWiseTemplateclaimDataDTOs(empDataWiseDTOs);
		empWiseTemplateReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);

		return empWiseTemplateReportDTO;

	}

	@Override
	public ClaimReportsResponse searchEmployee(PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, Long companyId, Long employeeId, boolean isShortList, String metaData) {
		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.FIRST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFirstName("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.LAST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLastName("%" + searchText.trim() + "%");
			}

		}
		conditionDTO.setEmployeeShortList(false);
		List<BigInteger> claimShortListEmployeeIds = null;
		List<BigInteger> employeeShortListEmployeeIds = null;
		if (isShortList) {
			EmployeeShortListDTO employeeShortList = generalLogic.getShortListEmployeeIdsForAdvanceFilter(companyId,
					metaData);
			claimShortListEmployeeIds = employeeShortList.getShortListEmployeeIds();

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);

		employeeShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();

		if (isShortList) {
			if (!employeeShortListEmployeeIds.isEmpty()) {
				employeeShortListEmployeeIds.retainAll(claimShortListEmployeeIds);
				conditionDTO.setShortListEmployeeIds(employeeShortListEmployeeIds);
			} else {
				conditionDTO.setShortListEmployeeIds(claimShortListEmployeeIds);
			}
			conditionDTO.setEmployeeShortList(true);

		} else {
			conditionDTO.setShortListEmployeeIds(employeeShortListEmployeeIds);
			if (!employeeShortListEmployeeIds.isEmpty()) {
				conditionDTO.setEmployeeShortList(true);
			} else {
				conditionDTO.setEmployeeShortList(false);
			}
		}

		List<Employee> finalList = employeeDAO.getEmployeeListByAdvanceFilter(conditionDTO, pageDTO, sortDTO,
				companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();
			String empNameStr = "";
			if (StringUtils.isNotBlank(employee.getFirstName())) {
				empNameStr += employee.getFirstName() + " ";
			}
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empNameStr += employee.getLastName();
			}
			employeeForm.setEmployeeName(empNameStr);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		ClaimReportsResponse response = new ClaimReportsResponse();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	private String getEmployeeName(Employee employeeVO) {
		String empName = "";
		if (employeeVO != null) {
			empName += employeeVO.getFirstName();
			if (StringUtils.isNotBlank(employeeVO.getLastName())) {
				empName += " " + employeeVO.getLastName();
			}
		}
		return empName;
	}

	private String getEmployeeName(String firstName, String lastName) {
		String employeeName = "";
		if (StringUtils.isNotBlank(firstName)) {
			employeeName = employeeName + firstName;
		}
		if (StringUtils.isNotBlank(lastName)) {
			employeeName = employeeName + " " + lastName;
		}
		return employeeName;
	}

	@Override
	public ClaimDetailsReportDTO genClaimHeadcountReport(Long employeeId, Long companyId,
			ClaimReportsForm claimReportsForm, Boolean isManager) {
		ClaimDetailsReportDTO claimReportDTO = new ClaimDetailsReportDTO();
		String companyIds = "";
		if (StringUtils.isBlank(claimReportsForm.getCompanyIds())) {
			companyIds = companyId.toString();
		} else {
			companyIds = claimReportsForm.getCompanyIds();
		}

		Employee employeeVO = employeeDAO.findById(employeeId);
		Company companyVO = companyDAO.findById(companyId);
		List<EmployeeHeadCountReportDTO> employeeHeadCountReportDTOs = employeeClaimTemplateDAO
				.getClaimHeadCountReportDetail(claimReportsForm.getStartDate(), claimReportsForm.getEndDate(),
						companyVO.getDateFormat(), companyIds);

		Set<String> companyCodeList = new TreeSet<>();
		Set<Long> empIdSet = new HashSet<Long>();

		Map<String, List<EmployeeHeadCountReportDTO>> claimHeadCountEmpDataListMap = new HashMap<String, List<EmployeeHeadCountReportDTO>>();
		Map<String, Set<Long>> empWithClaimTemplateByCompanyMap = new HashMap<String, Set<Long>>();
		Map<String, Set<Long>> empWithoutClaimTemplateByCompanyMap = new HashMap<String, Set<Long>>();
		for (EmployeeHeadCountReportDTO employeeHeadCountReportDTO : employeeHeadCountReportDTOs) {
			if (claimHeadCountEmpDataListMap.containsKey(employeeHeadCountReportDTO.getCompanyCode())) {
				List<EmployeeHeadCountReportDTO> reportList = claimHeadCountEmpDataListMap
						.get(employeeHeadCountReportDTO.getCompanyCode());
				reportList.add(employeeHeadCountReportDTO);
				claimHeadCountEmpDataListMap.put(employeeHeadCountReportDTO.getCompanyCode(), reportList);
			} else {
				List<EmployeeHeadCountReportDTO> reportList = new ArrayList<EmployeeHeadCountReportDTO>();
				reportList.add(employeeHeadCountReportDTO);
				claimHeadCountEmpDataListMap.put(employeeHeadCountReportDTO.getCompanyCode(), reportList);
			}

			// add unique employee with claim template by company Code
			if (empWithClaimTemplateByCompanyMap.containsKey(employeeHeadCountReportDTO.getCompanyCode())) {
				if (StringUtils.isNotBlank(employeeHeadCountReportDTO.getClaimTemplateName())) {
					Set<Long> employeeSet = empWithClaimTemplateByCompanyMap
							.get(employeeHeadCountReportDTO.getCompanyCode());
					employeeSet.add(employeeHeadCountReportDTO.getEmployeeId());
					empWithClaimTemplateByCompanyMap.put(employeeHeadCountReportDTO.getCompanyCode(), employeeSet);
				}
			} else {
				if (StringUtils.isNotBlank(employeeHeadCountReportDTO.getClaimTemplateName())) {
					Set<Long> employeeSet = new HashSet<Long>();
					employeeSet.add(employeeHeadCountReportDTO.getEmployeeId());
					empWithClaimTemplateByCompanyMap.put(employeeHeadCountReportDTO.getCompanyCode(), employeeSet);
				}
			}

			// add unique employee without claim template by company Code
			if (empWithoutClaimTemplateByCompanyMap.containsKey(employeeHeadCountReportDTO.getCompanyCode())) {
				if (StringUtils.isBlank(employeeHeadCountReportDTO.getClaimTemplateName())) {
					Set<Long> employeeSet = empWithoutClaimTemplateByCompanyMap
							.get(employeeHeadCountReportDTO.getCompanyCode());
					employeeSet.add(employeeHeadCountReportDTO.getEmployeeId());
					empWithoutClaimTemplateByCompanyMap.put(employeeHeadCountReportDTO.getCompanyCode(), employeeSet);
				}
			} else {
				if (StringUtils.isBlank(employeeHeadCountReportDTO.getClaimTemplateName())) {
					Set<Long> employeeSet = new HashSet<Long>();
					employeeSet.add(employeeHeadCountReportDTO.getEmployeeId());
					empWithoutClaimTemplateByCompanyMap.put(employeeHeadCountReportDTO.getCompanyCode(), employeeSet);
				}
			}

			companyCodeList.add(employeeHeadCountReportDTO.getCompanyCode());
			empIdSet.add(employeeHeadCountReportDTO.getEmployeeId());

		}
		claimReportDTO.setCompanyCodeList(new ArrayList<String>(companyCodeList));

		EmployeeHeadCountReportDTO empHeadCountReportDTO = null;
		String companyCode = "testCompany";

		Map<String, List<EmployeeHeadCountReportDTO>> claimHeadCountReportDTOMap = new HashMap<String, List<EmployeeHeadCountReportDTO>>();
		for (EmployeeHeadCountReportDTO summarryDTO : employeeHeadCountReportDTOs) {

			if (!companyCode.equals(summarryDTO.getCompanyCode())) {
				empHeadCountReportDTO = new EmployeeHeadCountReportDTO();
				empHeadCountReportDTO.setCompanyName(summarryDTO.getCompanyName());
				empHeadCountReportDTO.setCountryName(summarryDTO.getCountryName());
				empHeadCountReportDTO.setFromPeriod(claimReportsForm.getStartDate());
				empHeadCountReportDTO.setToPeriod(claimReportsForm.getEndDate());
				empHeadCountReportDTO.setGeneratedBy(getEmployeeName(employeeVO));
				if (empWithClaimTemplateByCompanyMap.get(summarryDTO.getCompanyCode()) != null) {
					empHeadCountReportDTO.setTotalEmployeesCount(
							empWithClaimTemplateByCompanyMap.get(summarryDTO.getCompanyCode()).size());
				}
				if (empWithoutClaimTemplateByCompanyMap.get(summarryDTO.getCompanyCode()) != null) {
					empHeadCountReportDTO.setEmployeesWithoutClaimTemplate(
							empWithoutClaimTemplateByCompanyMap.get(summarryDTO.getCompanyCode()).size());
				}

				List<EmployeeHeadCountReportDTO> employeeHeadCountReportDTOList = new ArrayList<EmployeeHeadCountReportDTO>();
				employeeHeadCountReportDTOList.add(empHeadCountReportDTO);
				claimHeadCountReportDTOMap.put(summarryDTO.getCompanyCode(), employeeHeadCountReportDTOList);
			}
			companyCode = summarryDTO.getCompanyCode();
		}

		BeanComparator beanComparator = new BeanComparator("employeeNumber");
		Collections.sort(employeeHeadCountReportDTOs, beanComparator);
		claimReportDTO.setClaimHeadCountEmpDataListMap(claimHeadCountEmpDataListMap);
		claimReportDTO.setClaimHeadCountHeaderDTOMap(claimHeadCountReportDTOMap);

		return claimReportDTO;
	}

	@Override
	public List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId, String searchString, Long employeeId) {

		List<EmployeeListForm> leaveBalanceSummaryFormList = new ArrayList<EmployeeListForm>();
		Company companyVO = companyDAO.findById(companyId);
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		List<Employee> employeeNumberList = employeeDAO.getEmployeeIdsForGroupCompany(searchString.trim(), companyId,
				companyVO.getCompanyGroup().getGroupId(), employeeShortListDTO);

		for (Employee employee : employeeNumberList) {
			EmployeeListForm employeeListForm = new EmployeeListForm();
			employeeListForm.setEmployeeNumber(employee.getEmployeeNumber());
			String empName = "";
			empName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empName += employee.getLastName();
			}
			employeeListForm.setEmployeeName(empName);
			employeeListForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			leaveBalanceSummaryFormList.add(employeeListForm);

		}
		return leaveBalanceSummaryFormList;
	}

}
