package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimApplicationItemDTO;
import com.payasia.common.dto.ClaimCustomFieldDTO;
import com.payasia.common.dto.ClaimItemBalanceDTO;
import com.payasia.common.dto.ClaimItemDTO;
import com.payasia.common.dto.ClaimMailDTO;
import com.payasia.common.dto.CustomFieldsDTO;
import com.payasia.common.dto.EmployeeClaimTemplateDataDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.ValidateClaimApplicationDTO;
import com.payasia.common.dto.ValidationClaimItemDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ClaimApplicationForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimApplicationItemWorkflowForm;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.ClaimTempItemConfig;
import com.payasia.common.form.ClaimTemplateForm;
import com.payasia.common.form.EmployeeClaimTemplateDataResponse;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimApplicationItemAttachmentDAO;
import com.payasia.dao.ClaimApplicationItemCustomFieldDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.ClaimApplicationItemLundinDetailDAO;
import com.payasia.dao.ClaimApplicationItemWorkflowDAO;
import com.payasia.dao.ClaimApplicationReviewerDAO;
import com.payasia.dao.ClaimApplicationWorkflowDAO;
import com.payasia.dao.ClaimItemEntertainmentDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.ClaimTemplateItemClaimTypeDAO;
import com.payasia.dao.ClaimTemplateItemCustomFieldDAO;
import com.payasia.dao.ClaimTemplateItemDAO;
import com.payasia.dao.ClaimTemplateItemGeneralDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyExchangeRateDAO;
import com.payasia.dao.CurrencyMasterDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmployeeClaimReviewerDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.LundinAFEDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.ClaimAmountReviewerTemplate;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemAttachment;
import com.payasia.dao.bean.ClaimApplicationItemCustomField;
import com.payasia.dao.bean.ClaimApplicationItemLundinDetail;
import com.payasia.dao.bean.ClaimApplicationItemWorkflow;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.ClaimStatusMaster;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemClaimType;
import com.payasia.dao.bean.ClaimTemplateItemCustomField;
import com.payasia.dao.bean.ClaimTemplateItemCustomFieldDropDown;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.ClaimTemplateWorkflow;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyExchangeRate;
import com.payasia.dao.bean.CurrencyMaster;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.dao.bean.LundinAFE;
import com.payasia.dao.bean.LundinBlock;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.NotificationAlert;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.ClaimMailLogic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class AddClaimLogicImpl implements AddClaimLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(AddClaimLogicImpl.class);

	@Resource
	ClaimTemplateDAO claimTemplateDAO;

	@Resource
	ClaimTemplateItemDAO claimTemplateItemDAO;

	@Resource
	ClaimApplicationItemDAO claimApplicationItemDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	ClaimStatusMasterDAO claimstatusMasterDAO;

	@Resource
	ClaimApplicationDAO claimApplicationDAO;

	@Resource
	ClaimApplicationItemAttachmentDAO claimApplicationItemAttachmentDAO;

	@Resource
	ClaimApplicationWorkflowDAO claimApplicationWorkflowDAO;

	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

	@Resource
	ClaimApplicationReviewerDAO claimApplicationReviewerDAO;

	@Resource
	ClaimApplicationItemWorkflowDAO claimApplicationItemWorkflowDAO;

	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;

	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;

	@Resource
	ClaimTemplateItemClaimTypeDAO claimTemplateItemClaimTypeDAO;

	@Resource
	ClaimApplicationItemCustomFieldDAO claimApplicationItemCustomFieldDAO;

	@Resource
	ClaimTemplateItemCustomFieldDAO claimTemplateItemCustomFieldDAO;

	@Resource
	EmployeeClaimReviewerDAO employeeClaimReviewerDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	CompanyExchangeRateDAO companyExchangeRateDAO;

	@Resource
	CurrencyMasterDAO currencyMasterDAO;

	@Resource
	ClaimMailLogic claimMailLogic;

	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	ModuleMasterDAO moduleMasterDAO;

	@Resource
	NotificationAlertDAO notificationAlertDAO;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	DataExportUtils dataExportUtils;
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	LundinBlockDAO lundinBlockDAO;
	@Resource
	LundinAFEDAO lundinAFEDAO;
	@Resource
	ClaimApplicationItemLundinDetailDAO claimApplicationItemLundinDetailDAO;
	@Resource
	ClaimItemEntertainmentDAO claimItemEntertainmentDAO;
	@Resource
	ClaimTemplateItemGeneralDAO claimTemplateItemGeneralDAO;
	@Resource
	FileUtils fileUtils;

	@Resource
	AWSS3Logic awss3LogicImpl;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	private final String CLAIM_PREV_YEAR_INDENTIFIER = " PY";

	@Override
	public AddClaimForm getClaimTemplateItemList(AddClaimDTO addClaimDTO) {

		List<AddClaimForm> claimTemplateItemList = new ArrayList<AddClaimForm>();

		EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO
				.findByEmployeeClaimTemplateID(addClaimDTO);

		Set<EmployeeClaimTemplateItem> employeeClaimTemplateItems = employeeClaimTemplate
				.getEmployeeClaimTemplateItems();

		AddClaimForm addClaimFormResponse = new AddClaimForm();
		addClaimFormResponse.setEmployeeClaimTemplateItemId(addClaimDTO.getEmployeeClaimTemplateId());
		addClaimFormResponse.setClaimTemplateName(employeeClaimTemplate.getClaimTemplate().getTemplateName());

		HashMap<Long, ClaimApplicationItemDTO> claimApplicationItems = new HashMap<>();

		for (EmployeeClaimTemplateItem employeeClaimTemplateItem : employeeClaimTemplateItems) {
			if (!employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getVisibility()) {
				continue;
			}
			if (!employeeClaimTemplateItem.getClaimTemplateItem().getVisibility()) {
				continue;
			}

			Long claimItemCategoryId = employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster()
					.getClaimCategoryMaster().getClaimCategoryId();

			if (claimApplicationItems.get(claimItemCategoryId) == null) {
				ClaimApplicationItemDTO claimApplicationItemDTO = new ClaimApplicationItemDTO();
				List<ClaimItemDTO> claimItems = new ArrayList<>();
				ClaimItemDTO claimItemDTO = new ClaimItemDTO();
				claimItemDTO.setEmployeeClaimTemplateItemId(
						FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateItem.getEmployeeClaimTemplateItemId()));
				claimItemDTO.setClaimItemId(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemId());
				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());

				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO
						.findByItemId("" + employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());

				if (claimTemplateItemGeneralVO != null
						&& StringUtils.isNotBlank(claimTemplateItemGeneralVO.getHelpText())) {
					claimItemDTO.setHelpText(claimTemplateItemGeneralVO.getHelpText());
				}

				claimItems.add(claimItemDTO);
				claimApplicationItemDTO.setClaimItems(claimItems);
				claimApplicationItemDTO.setCategoryId(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryId());
				claimApplicationItemDTO.setCategoryName(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
				claimApplicationItems.put(claimItemCategoryId, claimApplicationItemDTO);

			} else {
				ClaimItemDTO claimItemDTO = new ClaimItemDTO();
				claimItemDTO.setEmployeeClaimTemplateItemId(
						FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateItem.getEmployeeClaimTemplateItemId()));
				claimItemDTO.setClaimItemId(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemId());
				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());

				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO
						.findByItemId("" + employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());

				if (claimTemplateItemGeneralVO != null
						&& StringUtils.isNotBlank(claimTemplateItemGeneralVO.getHelpText())) {
					claimItemDTO.setHelpText(claimTemplateItemGeneralVO.getHelpText());
				}

				claimApplicationItems.get(claimItemCategoryId).getClaimItems().add(claimItemDTO);

			}

		}
		addClaimFormResponse.setClaimApplicationItems(claimApplicationItems);
		addClaimFormResponse.setClaimTemplateItemList(claimTemplateItemList);
		List<EmployeeClaimReviewer> claimReviewers = new ArrayList<>(employeeClaimTemplate.getEmployeeClaimReviewers());

		int totalNoOfReviewers = 0;
		for (EmployeeClaimReviewer employeeClaimReviewer : claimReviewers) {
			if (addClaimDTO.getEmployeeId() == employeeClaimReviewer.getEmployee1().getEmployeeId()) {
				totalNoOfReviewers++;
				if ("1".equals(employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue())) {
					addClaimFormResponse.setClaimReviewer1(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					addClaimFormResponse
							.setApplyTo(employeeDetailLogic.getEmployeeName(employeeClaimReviewer.getEmployee2()));
					addClaimFormResponse.setApplyToId(employeeClaimReviewer.getEmployee2().getEmployeeId());

				} else if ("2".equals(employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue())) {

					addClaimFormResponse.setClaimReviewer2(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					addClaimFormResponse.setClaimReviewer2Id(employeeClaimReviewer.getEmployee2().getEmployeeId());

				} else if ("3".equals(employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue())) {

					addClaimFormResponse.setClaimReviewer3(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					addClaimFormResponse.setClaimReviewer3Id(employeeClaimReviewer.getEmployee2().getEmployeeId());
				}
			}

		}

		addClaimFormResponse.setTotalNoOfReviewers(totalNoOfReviewers);

		return addClaimFormResponse;
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
	public void saveClaimApplicationItem(AddClaimForm addClaimForm, Long claimTemplateItemId) {

		ClaimApplicationItem claimApplicationItemVO = new ClaimApplicationItem();

		claimApplicationItemVO.setReceiptNumber(addClaimForm.getReceiptNumber());
		claimApplicationItemVO.setClaimDate(DateUtils.stringToTimestampWithTime(addClaimForm.getClaimDate()));
		claimApplicationItemVO.setClaimAmount(addClaimForm.getClaimAmount());
		claimApplicationItemVO.setAmountBeforeTax(addClaimForm.getAmountBeforeTax());
		claimApplicationItemVO.setTaxAmount(addClaimForm.getTaxAmount());
		claimApplicationItemVO.setRemarks(addClaimForm.getRemarks());
		claimApplicationItemDAO.save(claimApplicationItemVO);

	}

	@Override
	public AddClaimForm persistClaimApplication(AddClaimDTO addClaimDTO, AddClaimForm addClaimForm) {

		AddClaimForm addClaimFormRes = new AddClaimForm();
		Boolean claimStatus = false;
		ClaimApplication claimApplicationVO = null;
		Employee reviewer = null;
		Employee reviewerEmp = null;
		try {

			claimApplicationVO = claimApplicationDAO.findByClaimApplicationID(addClaimDTO);

			ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO
					.findByCondition(addClaimForm.getAddClaimStatus());
/*
			if (StringUtils.isNotBlank(addClaimForm.getRemarks())) {
				claimApplicationVO.setRemarks(URLDecoder.decode(addClaimForm.getRemarks(), "UTF-8"));
			} else {
				claimApplicationVO.setRemarks("");
			}
*/
			claimApplicationVO.setRemarks(addClaimForm.getRemarks());
			
			claimApplicationVO.setEmailCC(addClaimForm.getEmailCC());

			if (!addClaimForm.getAddClaimStatus().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {

				ValidateClaimApplicationDTO validateClaimApplicationDTO = claimApplicationDAO
						.validateClaimApplication(addClaimForm.getClaimApplicationId(), null, false);
				addClaimFormRes.setValidateClaimApplicationDTO(validateClaimApplicationDTO);

				if (validateClaimApplicationDTO.getErrorCode() == 1) {
					return addClaimFormRes;
				}

				claimApplicationVO.setClaimStatusMaster(claimStatusMaster);
				ClaimApplicationWorkflow claimApplicationWorkflow = new ClaimApplicationWorkflow();
				claimApplicationWorkflow.setClaimApplication(claimApplicationVO);
				claimApplicationWorkflow.setClaimStatusMaster(claimStatusMaster);
/*
				if (StringUtils.isNotBlank(addClaimForm.getRemarks())) {
					claimApplicationWorkflow.setRemarks(URLDecoder.decode(addClaimForm.getRemarks(), "UTF-8"));
				} else {
					claimApplicationWorkflow.setRemarks("");
				}
*/				
				claimApplicationWorkflow.setRemarks(addClaimForm.getRemarks());
				
				claimApplicationWorkflow.setForwardTo(addClaimForm.getApplyTo());
				claimApplicationWorkflow.setEmailCC(addClaimForm.getEmailCC());
				claimApplicationWorkflow.setEmployee(claimApplicationVO.getEmployee());
				claimApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestamp());
				claimApplicationWorkflow.setTotalAmount(claimApplicationVO.getTotalAmount());
				claimApplicationWorkflow.setTotalAmount(addClaimForm.getTaxAmount());

				claimApplicationWorkflow = claimApplicationWorkflowDAO.saveReturn(claimApplicationWorkflow);

				String allowOverride = "";
				String allowReject = "";
				String allowApprove = "";
				String allowForward = "";

				for (ClaimTemplateWorkflow claimTemplateWorkflow : claimApplicationVO.getEmployeeClaimTemplate()
						.getClaimTemplate().getClaimTemplateWorkflows()) {
					if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_OVERRIDE)) {
						allowOverride = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();
					} else if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_REJECT)) {
						allowReject = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();
					} else if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_FORWARD)) {
						allowForward = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();
					} else if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_APPROVE)) {
						allowApprove = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();
					}
				}

				boolean level1Reviewer = false;
				boolean level2Reviewer = false;
				boolean level3Reviewer = false;
				WorkFlowRuleMaster level1WorkFlowMaster = null;
				WorkFlowRuleMaster level2WorkFlowMaster = null;
				WorkFlowRuleMaster level3WorkFlowMaster = null;
				ClaimTemplate claimTemplateVO = claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate();

				if (claimTemplateVO != null && claimTemplateVO.isClaimReviewersBasedOnClaimAmount()) {
					Set<ClaimAmountReviewerTemplate> claimAmountReviewerTemplates = claimTemplateVO
							.getClaimAmountReviewerTemplates();
					if (!claimAmountReviewerTemplates.isEmpty()) {
						BigDecimal totalClaimAmount = claimApplicationVO.getTotalAmount();
						for (ClaimAmountReviewerTemplate amountReviewerTemplate : claimAmountReviewerTemplates) {
							if (totalClaimAmount.compareTo(amountReviewerTemplate.getFromClaimAmount()) >= 0
									&& totalClaimAmount.compareTo(amountReviewerTemplate.getToClaimAmount()) <= 0) {
								if (amountReviewerTemplate.getLevel1() != null) {
									level1Reviewer = true;
									level1WorkFlowMaster = amountReviewerTemplate.getLevel1();
								}
								if (amountReviewerTemplate.getLevel2() != null) {
									level2Reviewer = true;
									level2WorkFlowMaster = amountReviewerTemplate.getLevel2();
								}
								if (amountReviewerTemplate.getLevel3() != null
										&& amountReviewerTemplate.getLevel3() != null) {
									level3Reviewer = true;
									level3WorkFlowMaster = amountReviewerTemplate.getLevel3();
								}
							}
						}
					}
				}

				if (level1Reviewer && !level2Reviewer && !level3Reviewer) {
					if (allowOverride.length() == 3 && "0".equals(allowOverride.substring(2, 3))
							&& "0".equals(allowReject.substring(2, 3)) && "0".equals(allowApprove.substring(2, 3))
							&& "0".equals(allowForward.substring(2, 3))) {
						if (allowOverride.length() == 3 && "0".equals(allowOverride.substring(1, 2))
								&& "0".equals(allowReject.substring(1, 2)) && "0".equals(allowApprove.substring(1, 2))
								&& "0".equals(allowForward.substring(1, 2))) {
							if (addClaimForm.getApplyToId() != null && addClaimForm.getApplyToId() != 0) {
								reviewerEmp = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
										level1WorkFlowMaster, "1", true);
							}
						} else {
							reviewerEmp = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
									level1WorkFlowMaster, "2", true);
						}
					} else {
						reviewerEmp = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
								level1WorkFlowMaster, "3", true);
					}
				}

				if (level1Reviewer && level2Reviewer && !level3Reviewer) {
					reviewerEmp = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
							level1WorkFlowMaster, "1", true);
					if (allowOverride.length() == 3 && "0".equals(allowOverride.substring(2, 3))
							&& "0".equals(allowReject.substring(2, 3)) && "0".equals(allowApprove.substring(2, 3))
							&& "0".equals(allowForward.substring(2, 3))) {
						reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
								level2WorkFlowMaster, "2", false);
					} else {
						reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
								level2WorkFlowMaster, "3", false);
					}
				}

				if (level1Reviewer && level2Reviewer && level3Reviewer) {
					reviewerEmp = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
							level1WorkFlowMaster, "1", true);
					reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
							level2WorkFlowMaster, "2", false);
					reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
							level3WorkFlowMaster, "3", false);
				}

				if (!level1Reviewer && !level2Reviewer && !level3Reviewer) {
					if (addClaimForm.getApplyToId() != null && addClaimForm.getApplyToId() != 0) {
						WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
								.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, "1");
						reviewerEmp = saveClaimApplicationReviewer1(addClaimForm, claimApplicationVO,
								workflowRuleMaster, true);
					}

					if (addClaimForm.getClaimReviewer2Id() != null && addClaimForm.getClaimReviewer2Id() != 0) {
						WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
								.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, "2");
						reviewer = saveClaimApplicationReviewer2(addClaimForm, claimApplicationVO, workflowRuleMaster,
								false);
					}

					if (addClaimForm.getClaimReviewer3Id() != null && addClaimForm.getClaimReviewer3Id() != 0) {
						WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
								.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, "3");
						reviewer = saveClaimApplicationReviewer3(addClaimForm, claimApplicationVO, workflowRuleMaster,
								false);
					}
				}
			}
			claimApplicationDAO.update(claimApplicationVO);
			claimStatus = true;

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		} finally {

			if (claimStatus
					&& addClaimForm.getAddClaimStatus().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				ClaimMailDTO claimMailDTO = new ClaimMailDTO();
				claimMailDTO.setClaimApplication(claimApplicationVO);
				claimMailDTO.setLoggedInCmpId(addClaimDTO.getCompanyId());
				claimMailDTO.setLoggedInEmpId(addClaimDTO.getEmployeeId());
				// claimMailDTO.setAttachmentRequired(false);
				claimMailDTO.setAttachmentRequired(true);
				claimMailDTO.setSubcategoryName(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_APPLY);

				claimMailLogic.sendClaimMail(claimMailDTO);

				NotificationAlert notificationAlert = new NotificationAlert();
				notificationAlert.setEmployee(reviewerEmp);
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);

				notificationAlert.setModuleMaster(moduleMaster);
				notificationAlert.setMessage(getEmployeeName(claimApplicationVO.getEmployee()) + " "
						+ PayAsiaConstants.CLAIMAPPLICATION_NOTIFICATION_ALERT);
				notificationAlert.setShownStatus(false);
				notificationAlertDAO.save(notificationAlert);

			}

		}

		return addClaimFormRes;
	}

	/**
	 * @param addClaimForm
	 * @param claimApplicationVO
	 * @param reviewer
	 * @param level1WorkFlowMaster
	 * @return
	 */
	private Employee addClaimReviewerToWorkflow(AddClaimForm addClaimForm, ClaimApplication claimApplicationVO,
			Employee reviewer, WorkFlowRuleMaster workFlowMaster, String ruleValue, boolean isClaimPending) {
		if ("1".equalsIgnoreCase(workFlowMaster.getRuleValue())) {
			if (addClaimForm.getApplyToId() != null && addClaimForm.getApplyToId() != 0) {
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, ruleValue);
				reviewer = saveClaimApplicationReviewer1(addClaimForm, claimApplicationVO, workflowRuleMaster,
						isClaimPending);
			}
		}
		if ("2".equalsIgnoreCase(workFlowMaster.getRuleValue())) {
			if (addClaimForm.getClaimReviewer2Id() != null && addClaimForm.getClaimReviewer2Id() != 0) {
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, ruleValue);
				reviewer = saveClaimApplicationReviewer2(addClaimForm, claimApplicationVO, workflowRuleMaster,
						isClaimPending);
			}
		}
		if ("3".equalsIgnoreCase(workFlowMaster.getRuleValue())) {
			if (addClaimForm.getClaimReviewer3Id() != null && addClaimForm.getClaimReviewer3Id() != 0) {
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, ruleValue);
				reviewer = saveClaimApplicationReviewer3(addClaimForm, claimApplicationVO, workflowRuleMaster,
						isClaimPending);
			}
		}
		return reviewer;
	}

	/**
	 * @param addClaimForm
	 * @param claimApplicationVO
	 * @return
	 */
	private Employee saveClaimApplicationReviewer3(AddClaimForm addClaimForm, ClaimApplication claimApplicationVO,
			WorkFlowRuleMaster workflowRuleMaster, boolean isClaimPending) {
		Employee reviewer;
		ClaimApplicationReviewer claimApplicationReviewer = new ClaimApplicationReviewer();
		reviewer = getDelegatedEmployee(claimApplicationVO.getEmployee().getEmployeeId(),
				addClaimForm.getClaimReviewer3Id());
		claimApplicationReviewer.setEmployee(reviewer);
		claimApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
		claimApplicationReviewer.setClaimApplication(claimApplicationVO);
		claimApplicationReviewer.setPending(isClaimPending);
		claimApplicationReviewerDAO.save(claimApplicationReviewer);
		return reviewer;

	}

	/**
	 * @param addClaimForm
	 * @param claimApplicationVO
	 * @return
	 */
	private Employee saveClaimApplicationReviewer2(AddClaimForm addClaimForm, ClaimApplication claimApplicationVO,
			WorkFlowRuleMaster workflowRuleMaster, boolean isClaimPending) {
		Employee reviewer;
		ClaimApplicationReviewer claimApplicationReviewer = new ClaimApplicationReviewer();
		reviewer = getDelegatedEmployee(claimApplicationVO.getEmployee().getEmployeeId(),
				addClaimForm.getClaimReviewer2Id());
		claimApplicationReviewer.setEmployee(reviewer);
		claimApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
		claimApplicationReviewer.setClaimApplication(claimApplicationVO);
		claimApplicationReviewer.setPending(isClaimPending);
		claimApplicationReviewerDAO.save(claimApplicationReviewer);
		return reviewer;
	}

	/**
	 * @param addClaimForm
	 * @param claimApplicationVO
	 * @return
	 */
	private Employee saveClaimApplicationReviewer1(AddClaimForm addClaimForm, ClaimApplication claimApplicationVO,
			WorkFlowRuleMaster workflowRuleMasterVO, boolean isClaimPending) {
		Employee reviewer;
		ClaimApplicationReviewer claimApplicationReviewer = new ClaimApplicationReviewer();
		reviewer = getDelegatedEmployee(claimApplicationVO.getEmployee().getEmployeeId(), addClaimForm.getApplyToId());
		claimApplicationReviewer.setEmployee(reviewer);
		claimApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMasterVO);
		claimApplicationReviewer.setClaimApplication(claimApplicationVO);
		claimApplicationReviewer.setPending(isClaimPending);
		claimApplicationReviewerDAO.save(claimApplicationReviewer);
		return reviewer;
	}

	@Override
	public AddClaimForm persistAdminClaimApplication(AddClaimDTO addClaimDTO, AddClaimForm addClaimForm) {
		AddClaimForm addClaimFormRes = new AddClaimForm();
		Boolean claimStatus = false;
		ClaimApplication claimApplicationVO = null;

		try {
			claimApplicationVO = claimApplicationDAO
					.findByID(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));

			/*
			 * Set<EmployeeClaimReviewer>
			 * employeeClaimReviewerSet=claimApplicationVO.
			 * getEmployeeClaimTemplate().getEmployeeClaimReviewers();
			 * if(employeeClaimReviewerSet!=null &&
			 * !employeeClaimReviewerSet.isEmpty()){ for(EmployeeClaimReviewer
			 * employeeClaimReviewer:employeeClaimReviewerSet){
			 * employeeClaimReviewer.getWorkFlowRuleMaster().
			 * 
			 * } }
			 */

			ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO
					.findByCondition(addClaimForm.getAddClaimStatus());
			if (StringUtils.isNotBlank(addClaimForm.getRemarks())) {
				try {
					claimApplicationVO.setRemarks(URLDecoder.decode(addClaimForm.getRemarks(), "UTF-8"));
				} catch (UnsupportedEncodingException exception) {
					claimApplicationVO.setRemarks("");
					LOGGER.error(exception.getMessage(), exception);
				}
			} else {
				claimApplicationVO.setRemarks("");
			}
			claimApplicationVO.setEmailCC(addClaimForm.getEmailCC());
			claimApplicationVO.setVisibleToEmployee(addClaimForm.getVisibleToEmployee());

			if (!addClaimForm.getAddClaimStatus().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {

				ValidateClaimApplicationDTO validateClaimApplicationDTO = claimApplicationDAO.validateClaimApplication(
						FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()), null, true);
				addClaimFormRes.setValidateClaimApplicationDTO(validateClaimApplicationDTO);

				if (validateClaimApplicationDTO.getErrorCode() == 1) {

					return addClaimFormRes;

				}

				claimApplicationVO.setClaimStatusMaster(claimStatusMaster);
				ClaimApplicationWorkflow claimApplicationWorkflow = new ClaimApplicationWorkflow();

				claimApplicationWorkflow.setClaimApplication(claimApplicationVO);
				claimApplicationWorkflow.setClaimStatusMaster(claimStatusMaster);
				if (StringUtils.isNotBlank(addClaimForm.getRemarks())) {
					try {
						claimApplicationWorkflow.setRemarks(URLDecoder.decode(addClaimForm.getRemarks(), "UTF-8"));
					} catch (UnsupportedEncodingException exception) {
						claimApplicationWorkflow.setRemarks("");
						LOGGER.error(exception.getMessage(), exception);
					}

				} else {
					claimApplicationWorkflow.setRemarks("");
				}
				claimApplicationWorkflow.setForwardTo(addClaimForm.getApplyTo());
				claimApplicationWorkflow.setEmailCC(addClaimForm.getEmailCC());
				claimApplicationWorkflow.setEmployee(claimApplicationVO.getEmployee());
				claimApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestamp());
				claimApplicationWorkflow.setTotalAmount(claimApplicationVO.getTotalAmount());
				claimApplicationWorkflow.setTotalAmount(addClaimForm.getTaxAmount());
				claimApplicationWorkflowDAO.saveReturn(claimApplicationWorkflow);
				String allowOverride = "";
				String allowReject = "";
				String allowApprove = "";
				String allowForward = "";

				for (ClaimTemplateWorkflow claimTemplateWorkflow : claimApplicationVO.getEmployeeClaimTemplate()
						.getClaimTemplate().getClaimTemplateWorkflows()) {
					if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_OVERRIDE)) {
						allowOverride = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();
					} else if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_REJECT)) {
						allowReject = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();
					} else if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_FORWARD)) {
						allowForward = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();
					} else if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_APPROVE)) {
						allowApprove = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();
					}
				}
				boolean level1Reviewer = false;
				boolean level2Reviewer = false;
				boolean level3Reviewer = false;
				WorkFlowRuleMaster level1WorkFlowMaster = null;
				WorkFlowRuleMaster level2WorkFlowMaster = null;
				WorkFlowRuleMaster level3WorkFlowMaster = null;
				Employee reviewer = null;

				ClaimTemplate claimTemplateVO = claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate();
				if (claimTemplateVO != null && claimTemplateVO.isClaimReviewersBasedOnClaimAmount()) {
					Set<ClaimAmountReviewerTemplate> claimAmountReviewerTemplates = claimTemplateVO
							.getClaimAmountReviewerTemplates();
					if (!claimAmountReviewerTemplates.isEmpty()) {
						BigDecimal totalClaimAmount = claimApplicationVO.getTotalAmount();
						for (ClaimAmountReviewerTemplate amountReviewerTemplate : claimAmountReviewerTemplates) {
							if (totalClaimAmount.compareTo(amountReviewerTemplate.getFromClaimAmount()) >= 0
									&& totalClaimAmount.compareTo(amountReviewerTemplate.getToClaimAmount()) <= 0) {
								if (amountReviewerTemplate.getLevel1() != null) {
									level1Reviewer = true;
									level1WorkFlowMaster = amountReviewerTemplate.getLevel1();
								}
								if (amountReviewerTemplate.getLevel2() != null) {
									level2Reviewer = true;
									level2WorkFlowMaster = amountReviewerTemplate.getLevel2();
								}
								if (amountReviewerTemplate.getLevel3() != null
										&& amountReviewerTemplate.getLevel3() != null) {
									level3Reviewer = true;
									level3WorkFlowMaster = amountReviewerTemplate.getLevel3();
								}
							}
						}
					}
				}

				if (level1Reviewer && !level2Reviewer && !level3Reviewer) {
					if (allowOverride.length() == 3 && "0".equals(allowOverride.substring(2, 3))
							&& "0".equals(allowReject.substring(2, 3)) && "0".equals(allowApprove.substring(2, 3))
							&& "0".equals(allowForward.substring(2, 3))) {
						if (allowOverride.length() == 3 && "0".equals(allowOverride.substring(1, 2))
								&& "0".equals(allowReject.substring(1, 2)) && "0".equals(allowApprove.substring(1, 2))
								&& "0".equals(allowForward.substring(1, 2))) {
							if (addClaimForm.getApplyToId() != null && addClaimForm.getApplyToId() != 0) {
								reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
										level1WorkFlowMaster, "1", true);
							}
						} else {
							reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
									level1WorkFlowMaster, "2", true);
						}
					} else {
						reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
								level1WorkFlowMaster, "3", true);
					}
				}

				if (level1Reviewer && level2Reviewer && !level3Reviewer) {
					reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
							level1WorkFlowMaster, "1", true);

					if (allowOverride.length() == 3 && "0".equals(allowOverride.substring(2, 3))
							&& "0".equals(allowReject.substring(2, 3)) && "0".equals(allowApprove.substring(2, 3))
							&& "0".equals(allowForward.substring(2, 3))) {
						reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
								level2WorkFlowMaster, "2", false);
					} else {
						reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
								level2WorkFlowMaster, "3", false);
					}
				}

				if (level1Reviewer && level2Reviewer && level3Reviewer) {
					reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
							level1WorkFlowMaster, "1", true);
					reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
							level2WorkFlowMaster, "2", false);
					reviewer = addClaimReviewerToWorkflow(addClaimForm, claimApplicationVO, reviewer,
							level3WorkFlowMaster, "3", false);
				}

				if (!level1Reviewer && !level2Reviewer && !level3Reviewer) {
					if (addClaimForm.getApplyToId() != null && addClaimForm.getApplyToId() != 0) {
						WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
								.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, "1");

						reviewer = saveClaimApplicationReviewer1(addClaimForm, claimApplicationVO, workflowRuleMaster,
								true);
					}

					if (addClaimForm.getClaimReviewer2Id() != null && addClaimForm.getClaimReviewer2Id() != 0) {
						WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
								.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, "2");

						reviewer = saveClaimApplicationReviewer2(addClaimForm, claimApplicationVO, workflowRuleMaster,
								false);
					}

					if (addClaimForm.getClaimReviewer3Id() != null && addClaimForm.getClaimReviewer3Id() != 0) {
						WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
								.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, "3");

						reviewer = saveClaimApplicationReviewer3(addClaimForm, claimApplicationVO, workflowRuleMaster,
								false);
					}
				}
			}
			claimApplicationDAO.update(claimApplicationVO);
			claimStatus = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		} finally {
			if (claimStatus
					&& addClaimForm.getAddClaimStatus().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {

				ClaimMailDTO claimMailDTO = new ClaimMailDTO();
				claimMailDTO.setClaimApplication(claimApplicationVO);
				claimMailDTO.setLoggedInCmpId(addClaimDTO.getCompanyId());
				claimMailDTO.setLoggedInEmpId(addClaimDTO.getEmployeeId());
				claimMailDTO.setAttachmentRequired(true);
				claimMailDTO.setSubcategoryName(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_APPLY);

				claimMailLogic.sendClaimMail(claimMailDTO);
			}
		}
		return addClaimFormRes;
	}

	@Override
	public synchronized AddClaimForm saveClaimApplication(AddClaimDTO addClaimDTO) {
		AddClaimForm addClaimForm = new AddClaimForm();

		ClaimApplication claimApplication = new ClaimApplication();
		Company cmp = companyDAO.findById(addClaimDTO.getCompanyId());
		claimApplication.setCompany(cmp);
		Employee emp = employeeDAO.findById(addClaimDTO.getEmployeeId());
		claimApplication.setEmployee(emp);

		EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO
				.findByEmployeeClaimTemplateID(addClaimDTO);

		if (employeeClaimTemplate == null) {
			addClaimForm.setMesssage("No such Employee Claim Template");
			return addClaimForm;
		}
		if (employeeClaimTemplate.getEmployeeClaimReviewers().size() == 0) {
			addClaimForm.setClaimReviewerNotDefined(true);
			addClaimForm.setMesssage("Reviewers not defined for this template");
			return addClaimForm;
		} else {
			addClaimForm.setClaimReviewerNotDefined(false);
		}

		ValidateClaimApplicationDTO validateClaimApplicationDTO = claimApplicationDAO.validateClaimApplication(null,
				employeeClaimTemplate.getEmployeeClaimTemplateId(), addClaimDTO.getAdmin());
		addClaimForm.setValidateClaimApplicationDTO(validateClaimApplicationDTO);

		if (validateClaimApplicationDTO.getErrorCode() == 1) {

			return addClaimForm;

		}

		claimApplication.setEmployeeClaimTemplate(employeeClaimTemplate);
		claimApplication.setCreatedDate(DateUtils.getCurrentTimestamp());
		claimApplication.setUpdatedDate(DateUtils.getCurrentTimestamp());
		claimApplication.setTotalItems(0);
		claimApplication.setTotalAmount(new BigDecimal(0));
		ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO.findByCondition(PayAsiaConstants.CLAIM_STATUS_DRAFT);
		claimApplication.setVisibleToEmployee(true);
		claimApplication.setClaimStatusMaster(claimStatusMaster);
		claimApplication.setRemarks("");
		claimApplication.setClaimNumber(claimApplicationDAO.getMaxClaimNumber() + 1);

		ClaimApplication persistClaimApplication = claimApplicationDAO.saveReturn(claimApplication);
		ClaimApplicationForm claimApplicationForm = new ClaimApplicationForm();

		claimApplicationForm.setClaimApplicationId(
				FormatPreserveCryptoUtil.encrypt(persistClaimApplication.getClaimApplicationId()));

		claimApplicationForm.setCreatedDate(DateUtils.timeStampToString(persistClaimApplication.getCreatedDate()));
		claimApplicationForm.setUpdatedDate(DateUtils.timeStampToString(persistClaimApplication.getUpdatedDate()));
		claimApplicationForm.setTotalItems(persistClaimApplication.getTotalItems());
		claimApplicationForm.setTotalAmount(persistClaimApplication.getTotalAmount());
		claimApplicationForm.setClaimStatusId(persistClaimApplication.getClaimStatusMaster().getClaimStatusId());
		claimApplicationForm.setRemarks(persistClaimApplication.getRemarks());
		claimApplicationForm.setClaimFormName(
				persistClaimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
		claimApplicationForm.setClaimNumber(persistClaimApplication.getClaimNumber());
		addClaimForm.setClaimApplicationForm(claimApplicationForm);
		return addClaimForm;
	}

	@Override
	public AddClaimForm getClaimTemplates(Long companyId, Long employeeId, boolean isAdmin) {

		AddClaimForm addClaimForm = new AddClaimForm();
		List<ClaimTemplateForm> claimTemplates = new ArrayList<>();
		Company companyVO = companyDAO.findById(companyId);

		ArrayList<String> roleList = new ArrayList<>();
		ArrayList<String> claimTemplatePrivilegeList = new ArrayList<>();

		// For Glencoresg Company
		if (companyVO.getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
				if (grantedAuthority.getAuthority()
						.equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_MEDICAL_AND_AD_HOC)) {
					roleList.add(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_MEDICAL_AND_AD_HOC);
				}
				if (grantedAuthority.getAuthority().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_AD_HOC)) {
					roleList.add(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_AD_HOC);
				}
				if (grantedAuthority.getAuthority()
						.equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_SECRETARY)) {
					roleList.add(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_SECRETARY);
				}
			}
			// ADD Claim Template Privilege
			if (isAdmin && roleList.contains(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_MEDICAL_AND_AD_HOC)) {
				claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_MEDICAL_CLAIMS);
				claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_Ad_Hoc);
			}
			if (isAdmin && roleList.contains(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_AD_HOC)) {
				claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_Ad_Hoc);
			}
			if (isAdmin && roleList.contains(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_SECRETARY)) {
				claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_LOCAL_EXPENSES);
				claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_OVERSEAS_EXPENSES);
				claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_LOCAL_EXPENSES_2LR);
				claimTemplatePrivilegeList
						.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_OVERSEAS_EXPENSES_2LR);
				claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_LOCAL_EXPENSES_3LR);
				claimTemplatePrivilegeList
						.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_OVERSEAS_EXPENSES_3LR);
			}

		}

		List<EmployeeClaimTemplate> employeeClaimTemplates = employeeClaimTemplateDAO.checkEmpClaimTemplateByDate(null,
				employeeId,
				DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime(), companyVO.getDateFormat()),
				companyVO.getDateFormat());
		for (EmployeeClaimTemplate employeeClaimTemplate : employeeClaimTemplates) {

			if (!isAdmin && !employeeClaimTemplate.getClaimTemplate().getFrontEndApplicationMode()) {
				continue;
			}
			if (isAdmin && !employeeClaimTemplate.getClaimTemplate().getBackEndApplicationMode()) {
				continue;
			}

			// For Glencoresg Company
			if (companyVO.getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
				if (isAdmin && !claimTemplatePrivilegeList.isEmpty() && !claimTemplatePrivilegeList
						.contains(employeeClaimTemplate.getClaimTemplate().getTemplateName())) {
					continue;
				}
			}

			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
			claimTemplateForm.setClaimTemplateId(employeeClaimTemplate.getClaimTemplate().getClaimTemplateId());
			claimTemplateForm.setClaimTemplateName(employeeClaimTemplate.getClaimTemplate().getTemplateName());

			claimTemplateForm.setPerYear(employeeClaimTemplate.getClaimTemplate().getEntitlementPerYear());

			claimTemplateForm.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.encrypt(employeeClaimTemplate.getEmployeeClaimTemplateId()));
			
			claimTemplateForm.setBackEndAppModeClaim(employeeClaimTemplate.getClaimTemplate().getBackEndApplicationMode());
			claimTemplateForm.setFrontEndAppModeClaim(employeeClaimTemplate.getClaimTemplate().getFrontEndApplicationMode());
			
			claimTemplateForm.setCurrencyId(employeeClaimTemplate.getClaimTemplate().getDefaultCurrency()==null?null:employeeClaimTemplate.getClaimTemplate().getDefaultCurrency().getCurrencyId());
			claimTemplateForm.setCurrencyCode(employeeClaimTemplate.getClaimTemplate().getDefaultCurrency()==null?null:employeeClaimTemplate.getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			
			claimTemplates.add(claimTemplateForm);
		}
		addClaimForm.setClaimTemplates(claimTemplates);

		return addClaimForm;
	}

	public String getEmpMaritalStatus(Long companyId, Long employeeId) {
		String fieldValue = "";
		DataDictionary dataDictionary = dataDictionaryDAO.findByDictionaryName(companyId,
				PayAsiaConstants.EMPLOYEE_ENTITY_ID, PayAsiaConstants.PAYASIA_EMP_DYNFIELD_MARITAL_STATUS);
		if (dataDictionary != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
					dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());
			if (dynamicForm != null) {
				Tab tab = dataExportUtils.getTabObject(dynamicForm);
				List<Field> listOfFields = tab.getField();
				for (Field field : listOfFields) {
					if (field.getDictionaryId().equals(dataDictionary.getDataDictionaryId())) {
						DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO.getEmpRecords(employeeId,
								dynamicForm.getId().getVersion(), dynamicForm.getId().getFormId(),
								dynamicForm.getEntityMaster().getEntityId(), companyId);
						if (dynamicFormRecord != null) {
							Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
							String getMethodName = "getCol" + field.getName()
									.substring(field.getName().lastIndexOf("_") + 1, field.getName().length());
							Method dynamicFormRecordMethod = null;
							try {
								dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(getMethodName);
							} catch (NoSuchMethodException | SecurityException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(e.getMessage(), e);
							}

							try {
								fieldValue = (String) dynamicFormRecordMethod.invoke(dynamicFormRecord);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(e.getMessage(), e);
							}
							if (StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
								DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
										.findById(Long.parseLong(fieldValue));
								fieldValue = dynamicFormFieldRefValue.getCode();
							}
						}

					}
				}
			}
		}

		return fieldValue;
	}

	public List<ClaimApplicationItemDTO> getClaimantNameList(Long companyId, Long employeeId, String EmployeeName) {
		List<ClaimApplicationItemDTO> claimantNameList = new ArrayList<>();
		ClaimApplicationItemDTO claimantSelfNameDTO = new ClaimApplicationItemDTO();
		claimantSelfNameDTO.setClaimantName(EmployeeName);
		claimantNameList.add(claimantSelfNameDTO);

		DataDictionary dataDictionary = dataDictionaryDAO.findByDictionaryName(companyId,
				PayAsiaConstants.EMPLOYEE_ENTITY_ID, PayAsiaConstants.PAYASIA_EMP_DYNFIELD_TABLE_NAME_DEPENDENT);

		if (dataDictionary != null) {
			String companyDateFormat = dataDictionary.getCompany().getDateFormat();
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
					dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());
			if (dynamicForm != null) {
				String dynamicFormTableRecordValue = null;

				Tab tab = dataExportUtils.getTabObject(dynamicForm);

				String maritalStatus = getEmpMaritalStatus(companyId, employeeId);
				if (StringUtils.isBlank(maritalStatus)) {
					maritalStatus = PayAsiaConstants.PAYASIA_EMP_MARITAL_STATUS_SINGLE;
				}

				List<Field> listOfFields = tab.getField();
				for (Field field : listOfFields) {
					if (field.getType().equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
						if (field.getDictionaryId().equals(dataDictionary.getDataDictionaryId())) {
							String recordId = "";

							DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO.getEmpRecords(employeeId,
									dynamicForm.getId().getVersion(), dynamicForm.getId().getFormId(),
									dynamicForm.getEntityMaster().getEntityId(), companyId);
							if (dynamicFormRecord != null) {
								Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
								String getMethodName = "getCol" + field.getName()
										.substring(field.getName().lastIndexOf("_") + 1, field.getName().length());
								Method dynamicFormRecordMethod = null;
								try {
									dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(getMethodName);
								} catch (NoSuchMethodException | SecurityException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}

								try {
									recordId = (String) dynamicFormRecordMethod.invoke(dynamicFormRecord);
								} catch (IllegalAccessException | IllegalArgumentException
										| InvocationTargetException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}
								if (recordId != null) {
									List<DynamicFormTableRecord> dynamicFormTableRecordList = dynamicFormTableRecordDAO
											.getTableRecords(Long.parseLong(recordId), null, null);
									for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecordList) {
										String dependentName = "";
										String dependentRelationship = "";
										String deactivationDate = "";
										List<Column> columnList = field.getColumn();
										for (Column column : columnList) {
											String colLabel = new String(
													Base64.decodeBase64(column.getLabel().getBytes()));
											String dynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL
													+ column.getName().substring(column.getName().lastIndexOf("_") + 1,
															column.getName().length());
											Class<?> dynamicTableClass = dynamicFormTableRecord.getClass();
											Method dynamicFormTableRecordMethod;
											try {
												dynamicFormTableRecordMethod = dynamicTableClass
														.getMethod(dynamicFormTableRecordMethodName);
											} catch (NoSuchMethodException e) {
												LOGGER.error(e.getMessage(), e);
												throw new PayAsiaSystemException(e.getMessage(), e);
											} catch (SecurityException e) {
												LOGGER.error(e.getMessage(), e);
												throw new PayAsiaSystemException(e.getMessage(), e);
											}

											try {
												dynamicFormTableRecordValue = (String) dynamicFormTableRecordMethod
														.invoke(dynamicFormTableRecord);
											} catch (IllegalAccessException e) {
												LOGGER.error(e.getMessage(), e);
												throw new PayAsiaSystemException(e.getMessage(), e);
											} catch (IllegalArgumentException e) {
												LOGGER.error(e.getMessage(), e);
												throw new PayAsiaSystemException(e.getMessage(), e);
											} catch (InvocationTargetException e) {
												LOGGER.error(e.getMessage(), e);
												throw new PayAsiaSystemException(e.getMessage(), e);
											}

											if (colLabel.equalsIgnoreCase(
													PayAsiaConstants.PAYASIA_EMP_DYNFIELD_TABLE_COLNAME_DEPENDENT_NAME)) {
												dependentName = dynamicFormTableRecordValue;
											}
											if (colLabel.equalsIgnoreCase(
													PayAsiaConstants.PAYASIA_EMP_DYNFIELD_TABLE_COLNAME_DEPENDENT_RELATIONSHIP)) {
												dependentRelationship = dynamicFormTableRecordValue;
											}
											if (colLabel.equalsIgnoreCase(
													PayAsiaConstants.PAYASIA_EMP_DYNFIELD_TABLE_COLNAME_DEACTIVATION_DATE)) {
												deactivationDate = DateUtils.convertDateToSpecificFormat(
														dynamicFormTableRecordValue, companyDateFormat);

												if (StringUtils.isNotBlank(dependentRelationship)) {
													if (maritalStatus.equalsIgnoreCase(
															PayAsiaConstants.PAYASIA_EMP_MARITAL_STATUS_MARRIED)) {
														if (dependentRelationship.equalsIgnoreCase(
																PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_SPOUSE)
																|| dependentRelationship.equalsIgnoreCase(
																		PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_SON)
																|| dependentRelationship.equalsIgnoreCase(
																		PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_DAUGHTER)
																|| dependentRelationship.equalsIgnoreCase(
																		PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_CHILDREN)) {
															ClaimApplicationItemDTO claimApplicationItemDTO = new ClaimApplicationItemDTO();
															claimApplicationItemDTO.setClaimantName(dependentName);
															claimApplicationItemDTO
																	.setDeactivationDate(deactivationDate);
															claimantNameList.add(claimApplicationItemDTO);
														}
													}
													if (maritalStatus.equalsIgnoreCase(
															PayAsiaConstants.PAYASIA_EMP_MARITAL_STATUS_SINGLE)) {
														if (dependentRelationship.equalsIgnoreCase(
																PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_FATHER)
																|| dependentRelationship.equalsIgnoreCase(
																		PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_MOTHER)
																|| dependentRelationship.equalsIgnoreCase(
																		PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_SIBLING)) {
															ClaimApplicationItemDTO claimApplicationItemDTO = new ClaimApplicationItemDTO();
															claimApplicationItemDTO.setClaimantName(dependentName);
															claimApplicationItemDTO
																	.setDeactivationDate(deactivationDate);
															claimantNameList.add(claimApplicationItemDTO);
														}
													}
													if (maritalStatus.equalsIgnoreCase(
															PayAsiaConstants.PAYASIA_EMP_MARITAL_STATUS_WITH_CHILDREN)) {
														if (dependentRelationship.equalsIgnoreCase(
																PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_SON)
																|| dependentRelationship.equalsIgnoreCase(
																		PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_DAUGHTER)
																|| dependentRelationship.equalsIgnoreCase(
																		PayAsiaConstants.PAYASIA_EMP_DEPENDENT_RELATIONSHIP_CHILDREN)) {
															ClaimApplicationItemDTO claimApplicationItemDTO = new ClaimApplicationItemDTO();
															claimApplicationItemDTO.setClaimantName(dependentName);
															claimApplicationItemDTO
																	.setDeactivationDate(deactivationDate);
															claimantNameList.add(claimApplicationItemDTO);
														}
													}
													if (maritalStatus.equalsIgnoreCase(
															PayAsiaConstants.PAYASIA_EMP_MARITAL_STATUS_WITHOUT_CHILDREN)) {

													}

												}

											}
										}
									}
								}

							}

						}

					}
				}
			}
		}

		return claimantNameList;
	}

	@Override
	public AddClaimForm getClaimTemplateItemConfigData(AddClaimDTO addClaimDTO) {

		AddClaimForm addClaimForm = new AddClaimForm();
		EmployeeClaimTemplateItem employeeClaimTemplateItem = employeeClaimTemplateItemDAO
				.findByEmployeeClaimTemplateItemID(addClaimDTO);

		boolean isPreviousYearClaim = false;
		if (employeeClaimTemplateItem!=null && employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName()
				.endsWith(CLAIM_PREV_YEAR_INDENTIFIER)) {
			isPreviousYearClaim = true;
		}

		ClaimItemBalanceDTO claimItemBalanceDTO = claimApplicationItemDAO.getClaimItemBal(
				addClaimDTO.getEmployeeClaimTemplateItemId(), addClaimDTO.getCompanyId(),
				addClaimDTO.getClaimApplicationId(), addClaimDTO.getClaimApplicationItemId(), isPreviousYearClaim);
		addClaimForm.setClaimItemBalanceDTO(claimItemBalanceDTO);

		if (claimItemBalanceDTO.getErrorCode() == 1) {

			return addClaimForm;

		}

		ClaimTemplateItem claimTemplateItem = employeeClaimTemplateItem.getClaimTemplateItem();
		ClaimTempItemConfig claimTempItemConfig = new ClaimTempItemConfig();
		boolean isOpenToDependents = false;
		if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals().size() > 0) {
			claimTempItemConfig.setGuideLines(employeeClaimTemplateItem.getClaimTemplateItem()
					.getClaimTemplateItemGenerals().iterator().next().getRemarks());

			isOpenToDependents = employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals()
					.iterator().next().getOpenToDependents();
		}

		claimTempItemConfig.setOpenToDependent(isOpenToDependents);
		CurrencyMaster currencyMaster = employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplate()
				.getDefaultCurrency();
		if (currencyMaster != null) {
			claimTempItemConfig.setCurrencyCode(currencyMaster.getCurrencyCode());
			claimTempItemConfig.setCurrencyId(currencyMaster.getCurrencyId());
		} else {
			claimTempItemConfig.setCurrencyCode("");
			claimTempItemConfig.setCurrencyId(0L);
		}

		if (isOpenToDependents) {
			String employeeName = employeeClaimTemplateItem.getEmployeeClaimTemplate().getEmployee().getFirstName();
			if (StringUtils
					.isNotBlank(employeeClaimTemplateItem.getEmployeeClaimTemplate().getEmployee().getLastName())) {
				employeeName += " " + employeeClaimTemplateItem.getEmployeeClaimTemplate().getEmployee().getLastName();
			}
			UserContext.setWorkingCompanyId(String.valueOf(
					employeeClaimTemplateItem.getEmployeeClaimTemplate().getEmployee().getCompany().getCompanyId()));

			List<ClaimApplicationItemDTO> claimantNameList = getClaimantNameList(addClaimDTO.getCompanyId(),
					employeeClaimTemplateItem.getEmployeeClaimTemplate().getEmployee().getEmployeeId(), employeeName);
			claimTempItemConfig.setClaimantDTOList(claimantNameList);
		}

		// Save Lundin Claim Details
		if (addClaimDTO.isHasLundinTimesheetModule()) {
			if (StringUtils.isNotBlank(
					employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getAccountCode())) {
				claimTempItemConfig.setAccountCodeStartWith(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getAccountCode().substring(0, 1));
			} else {
				claimTempItemConfig.setAccountCodeStartWith("0");
			}
		}

		if (claimTemplateItem.getClaimTemplateItemClaimTypes().size() > 0) {

			ClaimTemplateItemClaimType claimTemplateItemClaimType = claimTemplateItem.getClaimTemplateItemClaimTypes()
					.iterator().next();

			if (claimTemplateItemClaimType.getClaimType().getCodeDesc()
					.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_QUANTITY_BASED)) {
				claimTempItemConfig.setIsQuantityBasedItem(true);
				claimTempItemConfig.setDefaultUnit(claimTemplateItemClaimType.getDefaultUnit());
				claimTempItemConfig.setShowDefaultUnit(claimTemplateItemClaimType.getShowDefaultUnit());
				claimTempItemConfig.setDefaultUnitPrice(claimTemplateItemClaimType.getDefaultUnitPrice());
				claimTempItemConfig.setAllowChangeDefaultPrice(claimTemplateItemClaimType.getAllowChangeDefaultPrice());

			} else {
				claimTempItemConfig.setIsQuantityBasedItem(false);
			}
			if (claimTemplateItemClaimType.getClaimType().getCodeDesc()
					.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED)) {
				claimTempItemConfig.setIsAmountBased(true);
				if (claimTemplateItemClaimType.getReceiptAmtPercentApplicable() != null && claimTemplateItemClaimType
						.getReceiptAmtPercentApplicable() <= PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT) {
					claimTempItemConfig.setIsClaimAmountApplicable(true);
					claimTempItemConfig
							.setAmountApplicable(claimTemplateItemClaimType.getReceiptAmtPercentApplicable());
					Integer amtApplicablePercent = claimTemplateItemClaimType.getReceiptAmtPercentApplicable();
					if (amtApplicablePercent == null || amtApplicablePercent == 0) {
						claimTempItemConfig.setAmountApplicable(
								PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT);
					}
				}

			} else {
				claimTempItemConfig.setIsAmountBased(false);
			}
			if (claimTemplateItemClaimType.getClaimType().getCodeDesc()
					.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED)) {
				claimTempItemConfig.setIsForexBased(true);
				claimTempItemConfig.setAllowChangeForexRate(claimTemplateItemClaimType.getAllowChangeForexRate());
				claimTempItemConfig
						.setAllowOverrideConvertedAmt(claimTemplateItemClaimType.getAllowOverrideConvertedAmt());

			} else {
				claimTempItemConfig.setIsForexBased(false);
			}

		}
		/* Find Claim_Auto_Populate_Data */
		List<ClaimApplicationItem> claimApplicationItemList = claimApplicationItemDAO.findByConditions(
				addClaimDTO.getClaimApplicationId(), new ArrayList<SortCondition>(), "updatedDate",
				employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimCategoryMaster()
						.getClaimCategoryId());

		if (claimTemplateItem.getClaimTemplateItemClaimTypes().size() > 0) {

			ClaimTemplateItemGeneral claimTemplateItemGeneral = claimTemplateItem.getClaimTemplateItemGenerals()
					.iterator().next();
			claimTempItemConfig.setTaxAmountPercentage(claimTemplateItemGeneral.getTaxPercentage());
			claimTempItemConfig.setAmountBeforeTaxVisible(claimTemplateItemGeneral.getAmountBeforeTaxVisible());
			claimTempItemConfig.setReceiptNoMandatory(claimTemplateItemGeneral.getReceiptNoMandatory() == null ? false
					: claimTemplateItemGeneral.getReceiptNoMandatory());
			claimTempItemConfig.setRemarksMandatory(claimTemplateItemGeneral.getRemarksMandatory() == null ? false
					: claimTemplateItemGeneral.getRemarksMandatory());
			claimTempItemConfig.setClaimDateMandatory(claimTemplateItemGeneral.getClaimDateMandatory() == null ? false
					: claimTemplateItemGeneral.getClaimDateMandatory());
			claimTempItemConfig.setAllowOverrideTaxAmt(claimTemplateItemGeneral.getAllowOverrideTaxAmt() == null ? false
					: claimTemplateItemGeneral.getAllowOverrideTaxAmt());
			claimTempItemConfig.setOverrideReceiptAmountForQuantityBased(
					claimTemplateItemGeneral.isOverrideReceiptAmountForQuantityBased());

			if (claimTempItemConfig.getRemarks() == null || claimTempItemConfig.getRemarks().trim().equals("")) {
				for (ClaimApplicationItem claimApplicationItem : claimApplicationItemList) {
//					if (!(claimApplicationItem.getRemarks() == null) || claimApplicationItem.getRemarks().trim().equals("")) {
					if ((claimApplicationItem.getRemarks() == null)? false: claimApplicationItem.getRemarks().trim().equals("")) {
						claimTempItemConfig.setRemarks(claimApplicationItem.getRemarks());
						break;
					}
				}

			}

		}

		List<ClaimTemplateItemCustomField> sortedCustomFieldList = new ArrayList<ClaimTemplateItemCustomField>(
				claimTemplateItem.getClaimTemplateItemCustomFields());
		Collections.sort(sortedCustomFieldList, new ClaimTempItemCusFieComp());
		List<CustomFieldsDTO> customFields = new ArrayList<>();
		for (ClaimTemplateItemCustomField claimTemplateItemCustomField : sortedCustomFieldList) {
			CustomFieldsDTO customFieldsDTO = new CustomFieldsDTO();
			customFieldsDTO.setCustomFieldId(claimTemplateItemCustomField.getCustomFieldId());
			customFieldsDTO.setFieldName(claimTemplateItemCustomField.getFieldName());
			customFieldsDTO.setMandatory(claimTemplateItemCustomField.isMandatory());
			customFieldsDTO.setCustomFieldTypeName(claimTemplateItemCustomField.getFieldType().getCodeDesc());

			for (ClaimApplicationItem claimApplicationItem : claimApplicationItemList) {
				List<ClaimApplicationItemCustomField> ClaimApplicationItemCustomFieldList = claimApplicationItemCustomFieldDAO
						.findByClaimApplicationItemID(claimApplicationItem.getClaimApplicationItemId());
				for (ClaimApplicationItemCustomField claimApplicationItemCustomField : ClaimApplicationItemCustomFieldList) {
					String fieldName = claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldName();
					if (fieldName.equalsIgnoreCase(claimTemplateItemCustomField.getFieldName())
							&& !fieldName.trim().equals("")) {
						if (claimTemplateItemCustomField.getFieldType().getCodeValue().equalsIgnoreCase("Date")) {
							customFieldsDTO.setFieldValue(
									DateUtils.convertDateFormat(claimApplicationItemCustomField.getValue(), companyDAO
											.findById(claimApplicationItemCustomField.getCompanyId()).getDateFormat()));
						} else {
							customFieldsDTO.setFieldValue(claimApplicationItemCustomField.getValue());
						}
						break;
					}
				}
				break;
			}

			if (claimTemplateItemCustomField.getFieldType().getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CLAIM_CUSTOM_FIELD_TYPE_DROPDOWN)) {
				List<ClaimTemplateItemCustomFieldDropDown> customFieldDDLists = new ArrayList<>(
						claimTemplateItemCustomField.getClaimTemplateItemCustomFieldDropDowns());
				Collections.sort(customFieldDDLists, new ClaimTemplateItemCustomFieldDropDownComp());
				String customFieldDropDownValues = "";
				for (ClaimTemplateItemCustomFieldDropDown customFieldDropDown : customFieldDDLists) {
					if (StringUtils.isNotBlank(customFieldDropDown.getFieldValue())) {
						customFieldDropDownValues += customFieldDropDown.getFieldValue() + ';';
					}
				}
				if (customFieldDropDownValues.endsWith(";")) {
					customFieldDropDownValues = StringUtils.removeEnd(customFieldDropDownValues, ";");
				}
				customFieldsDTO.setFieldDropdownValues(customFieldDropDownValues);
			}

			customFields.add(customFieldsDTO);
		}
		claimTempItemConfig.setCustomFieldDTO(customFields);
		addClaimForm.setClaimTempItemConfig(claimTempItemConfig);
		return addClaimForm;
	}

	private class ClaimTemplateItemCustomFieldDropDownComp implements Comparator<ClaimTemplateItemCustomFieldDropDown> {
		public int compare(ClaimTemplateItemCustomFieldDropDown templateField,
				ClaimTemplateItemCustomFieldDropDown compWithTemplateField) {
			if (templateField.getCustomFieldDropDownId() > compWithTemplateField.getCustomFieldDropDownId()) {
				return 1;
			} else if (templateField.getCustomFieldDropDownId() < compWithTemplateField.getCustomFieldDropDownId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class ClaimTempItemCusFieComp implements Comparator<ClaimTemplateItemCustomField> {
		public int compare(ClaimTemplateItemCustomField templateField,
				ClaimTemplateItemCustomField compWithTemplateField) {
			if (templateField.getCustomFieldId() > compWithTemplateField.getCustomFieldId()) {
				return 1;
			} else if (templateField.getCustomFieldId() < compWithTemplateField.getCustomFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class ClaimAppTempItemCusFieComp implements Comparator<ClaimApplicationItemCustomField> {
		public int compare(ClaimApplicationItemCustomField templateField,
				ClaimApplicationItemCustomField compWithTemplateField) {
			if (templateField.getClaimTemplateItemCustomField().getCustomFieldId() > compWithTemplateField
					.getClaimTemplateItemCustomField().getCustomFieldId()) {
				return 1;
			} else if (templateField.getClaimTemplateItemCustomField().getCustomFieldId() < compWithTemplateField
					.getClaimTemplateItemCustomField().getCustomFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public AddClaimForm saveClaimTemplateItem(ClaimApplicationItemForm claimApplicationItemForm, Long companyId) {

		Company company = companyDAO.findById(companyId);
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItem claimApplicationItem = new ClaimApplicationItem();
		ClaimApplication claimApplicationVO = claimApplicationDAO
				.findByID(FormatPreserveCryptoUtil.decrypt(claimApplicationItemForm.getClaimApplicationID()));
/*		try {
			if(claimApplicationItemForm.getReceiptNumber()!=null){
			claimApplicationItem
					.setReceiptNumber(URLDecoder.decode(claimApplicationItemForm.getReceiptNumber(), "UTF-8"));
			}
			if (StringUtils.isNotBlank(claimApplicationItemForm.getRemarks())) {
				claimApplicationItem.setRemarks(URLDecoder.decode(claimApplicationItemForm.getRemarks(), "UTF-8"));
			} else {
				claimApplicationItem.setRemarks("");
			}
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		
		claimApplicationItem.setReceiptNumber(claimApplicationItemForm.getReceiptNumber());
		claimApplicationItem.setRemarks(claimApplicationItemForm.getRemarks());
		
		claimApplicationItem.setClaimApplication(claimApplicationVO);
		claimApplicationItem.setClaimDate(DateUtils.stringToTimestampWithTime(claimApplicationItemForm.getClaimDate()));

		setClaimApplicationItemOnClaimType(claimApplicationItemForm, claimApplicationItem);
		boolean isManager = false;
		ValidationClaimItemDTO validateClaimItemDTO = claimApplicationItemDAO.validateClaimItem(claimApplicationItem,
				claimApplicationItemForm.getIsAdmin(), isManager);
		if (validateClaimItemDTO.getErrorCode() == 1) {
			addClaimForm.setValidationClaimItemDTO(validateClaimItemDTO);
			return addClaimForm;

		}

		ClaimApplicationItem persistClaimApplicationItem = claimApplicationItemDAO.saveReturn(claimApplicationItem);

		// Save Lundin Claim Details
		if (claimApplicationItemForm.isLundinTimesheetModule()) {
			ClaimApplicationItemLundinDetail claimApplicationItemLundinDetail = new ClaimApplicationItemLundinDetail();
			if (claimApplicationItemForm.getBlockId() != 0) {
				LundinBlock lundinBlock = lundinBlockDAO.findById(claimApplicationItemForm.getBlockId());
				claimApplicationItemLundinDetail.setLundinBlock(lundinBlock);
			}
			if (claimApplicationItemForm.getAfeId() != 0) {
				LundinAFE lundinAFE = lundinAFEDAO.findById(claimApplicationItemForm.getAfeId());
				claimApplicationItemLundinDetail.setLundinAFE(lundinAFE);
			}
			claimApplicationItemLundinDetail.setClaimApplicationItem(persistClaimApplicationItem);
			claimApplicationItemLundinDetailDAO.save(claimApplicationItemLundinDetail);
		}

		for (ClaimCustomFieldDTO claimCustomFieldDTO : claimApplicationItemForm.getCustomFieldDTOList()) {
			ClaimApplicationItemCustomField claimApplicationItemCustomField = new ClaimApplicationItemCustomField();

			ClaimTemplateItemCustomField claimTemplateItemCustomField = claimTemplateItemCustomFieldDAO
					.findByID(claimCustomFieldDTO.getCustomFieldId());
			claimApplicationItemCustomField.setClaimTemplateItemCustomField(claimTemplateItemCustomField);
			claimApplicationItemCustomField.setClaimApplicationItem(persistClaimApplicationItem);

			String customFieldValue = claimCustomFieldDTO.getValue();
			if (StringUtils.isNotBlank(customFieldValue)) {
/*				
				if (claimTemplateItemCustomField!=null && claimTemplateItemCustomField.getFieldType()!=null && claimTemplateItemCustomField.getFieldType().getCodeDesc()
						.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CLAIM_CUSTOM_FIELD_TYPE_DROPDOWN)) {

				} 
				else {
					try {
						customFieldValue = URLDecoder.decode(customFieldValue, "UTF-8");
					} catch (IllegalArgumentException | UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
*/				
			} else {
				customFieldValue = "";
			}

			if (claimTemplateItemCustomField!=null && claimTemplateItemCustomField.getFieldType()!=null && claimTemplateItemCustomField.getFieldType().getCodeValue().equals(PayAsiaConstants.PAYASIA_DATE)) {
				if (StringUtils.isNotBlank(customFieldValue)) {
					claimApplicationItemCustomField.setValue(DateUtils.appendTodayTime(
							DateUtils.convertDateFormatyyyyMMdd(customFieldValue, company.getDateFormat())));
				}
			} else {
				claimApplicationItemCustomField.setValue(customFieldValue);
			}

			if(claimApplicationItemCustomField.getClaimTemplateItemCustomField()!=null){
			   claimApplicationItemCustomFieldDAO.save(claimApplicationItemCustomField);
			}
		}
		addClaimForm
				.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplicationVO.getClaimApplicationId()));
		addClaimForm.setClaimApplicationItemId(FormatPreserveCryptoUtil.encrypt(persistClaimApplicationItem.getClaimApplicationItemId()));
		updateClaimApplication(claimApplicationVO);
		return addClaimForm;
	}

	private void updateClaimApplication(ClaimApplication claimApplicationVO) {

		Integer noOfItems = claimApplicationVO.getClaimApplicationItems().size();
		claimApplicationVO.setTotalItems(noOfItems);
		BigDecimal totalAmount = new BigDecimal(0);
		for (ClaimApplicationItem existingClaimApplicationItem : claimApplicationVO.getClaimApplicationItems()) {
			if (existingClaimApplicationItem.getApplicableClaimAmount() == null
					|| (existingClaimApplicationItem.getApplicableClaimAmount().compareTo(BigDecimal.ZERO) == 0)) {
				totalAmount = totalAmount.add(existingClaimApplicationItem.getClaimAmount());
			}

			else {
				totalAmount = totalAmount.add(existingClaimApplicationItem.getApplicableClaimAmount());
			}
		}
		claimApplicationVO.setTotalAmount(totalAmount);
		claimApplicationDAO.update(claimApplicationVO);
	}

	private void updateClaimApplicationForDelete(ClaimApplication claimApplicationVO,
			ClaimApplicationItem claimApplicationItem) {

		Integer noOfItems = claimApplicationVO.getClaimApplicationItems().size();
		claimApplicationVO.setTotalItems(noOfItems - 1);
		BigDecimal totalAmount = new BigDecimal(0);
		for (ClaimApplicationItem existingClaimApplicationItem : claimApplicationVO.getClaimApplicationItems()) {
			if (existingClaimApplicationItem != claimApplicationItem) {
				if (existingClaimApplicationItem.getApplicableClaimAmount() == null
						|| (existingClaimApplicationItem.getApplicableClaimAmount().compareTo(BigDecimal.ZERO) == 0)) {
					totalAmount = totalAmount.add(existingClaimApplicationItem.getClaimAmount());
				}

				else {
					totalAmount = totalAmount.add(existingClaimApplicationItem.getApplicableClaimAmount());
				}
			}
		}
		claimApplicationVO.setTotalAmount(totalAmount);
		claimApplicationDAO.update(claimApplicationVO);
	}

	@Override
	public AddClaimForm updateClaimTemplateItem(ClaimApplicationItemForm claimApplicationItemForm, Long companyId) {
		Company company = companyDAO.findById(companyId);
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItem claimApplicationItem = claimApplicationItemDAO
				.findById(FormatPreserveCryptoUtil.decrypt(claimApplicationItemForm.getClaimApplicationItemID()));
		ClaimApplication claimApplicationVO = claimApplicationDAO
				.findByID(FormatPreserveCryptoUtil.decrypt(claimApplicationItemForm.getClaimApplicationID()));

		ClaimApplicationItem validClaimAppItem = new ClaimApplicationItem();
		/*try {
			if(claimApplicationItemForm.getReceiptNumber()!=null){
			 validClaimAppItem.setReceiptNumber(URLDecoder.decode(claimApplicationItemForm.getReceiptNumber(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		validClaimAppItem.setReceiptNumber(claimApplicationItemForm.getReceiptNumber());
		validClaimAppItem.setClaimApplication(claimApplicationVO);
		validClaimAppItem.setClaimDate(DateUtils.stringToTimestampWithTime(claimApplicationItemForm.getClaimDate()));
		/*if (StringUtils.isNotBlank(claimApplicationItemForm.getRemarks())) {
			try {
				validClaimAppItem.setRemarks(URLDecoder.decode(claimApplicationItemForm.getRemarks(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else {
			validClaimAppItem.setRemarks("");
		}*/
		validClaimAppItem.setRemarks(claimApplicationItemForm.getRemarks());
		setClaimApplicationItemOnClaimType(claimApplicationItemForm, validClaimAppItem);
		validClaimAppItem.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemForm.getClaimApplicationItemID()));
		boolean isManager = false;
		ValidationClaimItemDTO validateClaimItemDTO = claimApplicationItemDAO.validateClaimItem(validClaimAppItem,
				claimApplicationItemForm.getIsAdmin(), isManager);
		if (validateClaimItemDTO.getErrorCode() == 1) {
			addClaimForm.setValidationClaimItemDTO(validateClaimItemDTO);
			return addClaimForm;

		}

		/*try {
			if(claimApplicationItemForm.getReceiptNumber()!=null){
			claimApplicationItem
					.setReceiptNumber(URLDecoder.decode(claimApplicationItemForm.getReceiptNumber(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}*/
		claimApplicationItem.setReceiptNumber(claimApplicationItemForm.getReceiptNumber());
		
		claimApplicationItem.setClaimApplication(claimApplicationVO);
		claimApplicationItem.setClaimDate(DateUtils.stringToTimestampWithTime(claimApplicationItemForm.getClaimDate()));

		setClaimApplicationItemOnClaimType(claimApplicationItemForm, claimApplicationItem);
		/*if (StringUtils.isNotBlank(claimApplicationItemForm.getRemarks())) {
			try {
				claimApplicationItem.setRemarks(URLDecoder.decode(claimApplicationItemForm.getRemarks(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else {
			claimApplicationItem.setRemarks("");
		}*/
		claimApplicationItem.setRemarks(claimApplicationItemForm.getRemarks());
		
		claimApplicationItemDAO.update(claimApplicationItem);

		// Save Lundin Claim Details
		if (claimApplicationItemForm.isLundinTimesheetModule()) {
			claimApplicationItemLundinDetailDAO.deleteByCondition(claimApplicationItem.getClaimApplicationItemId());

			ClaimApplicationItemLundinDetail claimApplicationItemLundinDetail = new ClaimApplicationItemLundinDetail();
			if (claimApplicationItemForm.getBlockId() != 0) {
				LundinBlock lundinBlock = lundinBlockDAO.findById(claimApplicationItemForm.getBlockId());
				claimApplicationItemLundinDetail.setLundinBlock(lundinBlock);
			}
			if (claimApplicationItemForm.getAfeId() != 0) {
				LundinAFE lundinAFE = lundinAFEDAO.findById(claimApplicationItemForm.getAfeId());
				claimApplicationItemLundinDetail.setLundinAFE(lundinAFE);
			}
			claimApplicationItemLundinDetail.setClaimApplicationItem(claimApplicationItem);
			claimApplicationItemLundinDetailDAO.save(claimApplicationItemLundinDetail);
		}

		for (ClaimCustomFieldDTO claimCustomFieldDTO : claimApplicationItemForm.getCustomFieldDTOList()) {
			ClaimApplicationItemCustomField claimApplicationItemCustomField;
			if (claimCustomFieldDTO.getClaimApplicationItemCustomFieldId() != null
					&& claimCustomFieldDTO.getClaimApplicationItemCustomFieldId() != 0) {
				claimApplicationItemCustomField = claimApplicationItemCustomFieldDAO
						.findByID(claimCustomFieldDTO.getClaimApplicationItemCustomFieldId());
			} else {
				claimApplicationItemCustomField = new ClaimApplicationItemCustomField();
			}
			ClaimTemplateItemCustomField claimTemplateItemCustomField = claimTemplateItemCustomFieldDAO
					.findByID(claimCustomFieldDTO.getCustomFieldId());
			claimApplicationItemCustomField.setClaimTemplateItemCustomField(claimTemplateItemCustomField);
			claimApplicationItemCustomField.setClaimApplicationItem(claimApplicationItem);

			String customFieldValue = claimCustomFieldDTO.getValue();
			if (StringUtils.isNotBlank(customFieldValue)) {
/*				
				if (claimTemplateItemCustomField.getFieldType().getCodeDesc()
						.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CLAIM_CUSTOM_FIELD_TYPE_DROPDOWN)) {

				} else {
					try {
						if(customFieldValue!=null){
						 customFieldValue = URLDecoder.decode(customFieldValue, "UTF-8");
						}else{
							customFieldValue = "";
						}
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
*/				
			} else {
				customFieldValue = "";
			}

			if (claimTemplateItemCustomField.getFieldType().getCodeValue().equals(PayAsiaConstants.PAYASIA_DATE)) {
				if (StringUtils.isNotBlank(customFieldValue)) {
					claimApplicationItemCustomField.setValue(DateUtils.appendTodayTime(
							DateUtils.convertDateFormatyyyyMMdd(customFieldValue, company.getDateFormat())));
				}

			} else {
				claimApplicationItemCustomField.setValue(customFieldValue);
			}

			if (claimCustomFieldDTO.getClaimApplicationItemCustomFieldId() != null
					&& claimCustomFieldDTO.getClaimApplicationItemCustomFieldId() != 0) {
				claimApplicationItemCustomFieldDAO.update(claimApplicationItemCustomField);
			} else {
				claimApplicationItemCustomFieldDAO.save(claimApplicationItemCustomField);
			}

		}
		addClaimForm
				.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplicationVO.getClaimApplicationId()));
		updateClaimApplication(claimApplicationVO);
		return addClaimForm;
	}

	/**
	 * getClaimDateAndItemSortOrder
	 * 
	 * @param companyId
	 * @return claimPreferenceForm
	 */

	private ClaimPreferenceForm getClaimDateAndItemSortOrder(final Long companyId) {
		String claimItemNameSortOrder = "";
		String createdDateSortOrder = "";
		ClaimPreferenceForm claimPreferenceForm = new ClaimPreferenceForm();
		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreferenceVO != null) {
			if (StringUtils.isNotBlank(claimPreferenceVO.getClaimItemDateSortOrder())) {
				createdDateSortOrder = claimPreferenceVO.getClaimItemDateSortOrder();
			}
			if (StringUtils.isNotBlank(claimPreferenceVO.getClaimItemNameSortOrder())) {
				claimItemNameSortOrder = claimPreferenceVO.getClaimItemNameSortOrder();
			}
		}
		claimPreferenceForm.setClaimItemNameSortOrder(claimItemNameSortOrder);
		claimPreferenceForm.setClaimItemDateSortOrder(createdDateSortOrder);
		return claimPreferenceForm;
	}

	private BigDecimal getTotalAmountApplicable(ClaimApplication claimApplication, BigDecimal totalApplicableAmount) {
		Set<ClaimApplicationItem> itemsList = claimApplication.getClaimApplicationItems();
		for (ClaimApplicationItem applicationItem : itemsList) {
			if (applicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemClaimTypes()
					.isEmpty() || !applicationItem.isActive()) {
				continue;
			}
			ClaimTemplateItemClaimType claimTemplateItemClaimType = applicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimTemplateItemClaimTypes().iterator().next();
			if (claimTemplateItemClaimType.getClaimType().getCodeDesc()
					.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED)) {
				if (claimTemplateItemClaimType.getReceiptAmtPercentApplicable() != null && claimTemplateItemClaimType
						.getReceiptAmtPercentApplicable() <= PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT) {

					Integer amtApplicablePercent = claimTemplateItemClaimType.getReceiptAmtPercentApplicable();
					if (amtApplicablePercent == null || amtApplicablePercent == 0) {
						amtApplicablePercent = PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT;
					}
					BigDecimal totalAmt = (applicationItem.getClaimAmount()
							.multiply(new BigDecimal(amtApplicablePercent))).divide(new BigDecimal(100));
					totalApplicableAmount = totalApplicableAmount.add(totalAmt);
				} else {
					totalApplicableAmount = totalApplicableAmount.add(applicationItem.getClaimAmount());
				}
			} else {
				totalApplicableAmount = totalApplicableAmount.add(applicationItem.getClaimAmount());
			}
		}
		return totalApplicableAmount;
	}

	@Override
	public AddClaimForm getClaimApplicationItemList(AddClaimDTO addClaimDTO) {
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplication claimApplication = claimApplicationDAO.findByClaimApplicationID(addClaimDTO);
		DecimalFormat df = new DecimalFormat("##.00");

		BigDecimal totalApplicableAmount = BigDecimal.ZERO;
		totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
		addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
		addClaimForm.setTotalItems(String.valueOf(claimApplication.getTotalItems()));
		addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
		addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

		addClaimForm
				.setClaimTemplateName(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
		addClaimForm.setRemarks(claimApplication.getRemarks());

		ClaimPreference claimPreference = claimPreferenceDAO
				.findByCompanyId(claimApplication.getCompany().getCompanyId());
		List<SortCondition> sortConditions = new ArrayList<SortCondition>();

		if (claimPreference != null) {
			if (StringUtils.isNotBlank(claimPreference.getClaimItemDateSortOrder())) {
				SortCondition sortCondition = new SortCondition();
				sortCondition.setColumnName(PayAsiaConstants.CLAIMITEMDATESORTORDER);
				if (claimPreference.getClaimItemDateSortOrder().equalsIgnoreCase(PayAsiaConstants.ASC)) {
					sortCondition.setOrderType(PayAsiaConstants.ASC);
				} else {
					sortCondition.setOrderType(PayAsiaConstants.DESC);
				}
				sortConditions.add(sortCondition);
			}

			if (StringUtils.isNotBlank(claimPreference.getClaimItemNameSortOrder())) {
				SortCondition sortCondition = new SortCondition();
				sortCondition.setColumnName(PayAsiaConstants.ITEMNAMESORTORDER);
				if (claimPreference.getClaimItemNameSortOrder().equalsIgnoreCase(PayAsiaConstants.ASC)) {
					sortCondition.setOrderType(PayAsiaConstants.ASC);
				} else {
					sortCondition.setOrderType(PayAsiaConstants.DESC);
				}
				sortConditions.add(sortCondition);
			}
		}

		List<ClaimApplicationItem> claimApplicationItems = claimApplicationItemDAO
				.findByCondition(claimApplication.getClaimApplicationId(), sortConditions, PayAsiaConstants.ASC);

		List<ClaimApplicationItemForm> claimApplicationITemList = new ArrayList<>();
		for (ClaimApplicationItem claimApplicationItem : claimApplicationItems) {
			ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
			
			claimApplicationItemForm.setLundinTimesheetModule(UserContext.isLundinTimesheetModule());
			claimApplicationItemForm.setEmployeeClaimTemplateItemId(FormatPreserveCryptoUtil
					.encrypt(claimApplicationItem.getEmployeeClaimTemplateItem().getEmployeeClaimTemplateItemId()));
			claimApplicationItemForm.setReceiptNumber(claimApplicationItem.getReceiptNumber());
			claimApplicationItemForm.setClaimApplicationItemID(
					FormatPreserveCryptoUtil.encrypt(claimApplicationItem.getClaimApplicationItemId()));
			claimApplicationItemForm.setClaimApplicationClaimItemName(claimApplicationItem
					.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
			claimApplicationItemForm.setCurrencyCode(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getEmployeeClaimTemplate().getClaimTemplate().getDefaultCurrency() == null ? ""
							: claimApplicationItem.getEmployeeClaimTemplateItem().getEmployeeClaimTemplate()
									.getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			if (claimApplicationItem.getCurrencyMaster() != null) {
				claimApplicationItemForm.setCurrencyName(claimApplicationItem.getCurrencyMaster().getCurrencyName());
			}
			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemClaimTypes().size() > 0) {

				ClaimTemplateItemClaimType claimTemplateItemClaimType = claimApplicationItem
						.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemClaimTypes()
						.iterator().next();
				addClaimForm.setDefaultUnitPrice(claimTemplateItemClaimType.getDefaultUnitPrice());
			}
			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemGenerals().size() > 0) {
				if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
						.getClaimTemplateItemGenerals().iterator().next().getOpenToDependents()) {
					claimApplicationItemForm.setOpenToDependents(
							claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
									.getClaimTemplateItemGenerals().iterator().next().getOpenToDependents());
				}
			}

			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemClaimTypes().size() > 0) {

				ClaimTemplateItemClaimType claimTemplateItemClaimType = claimApplicationItem
						.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemClaimTypes()
						.iterator().next();
				addClaimForm.setDefaultUnitPrice(claimTemplateItemClaimType.getDefaultUnitPrice());
			}
			if (StringUtils.isNotBlank(claimApplicationItem.getClaimantName())) {
				claimApplicationItemForm.setClaimantName(claimApplicationItem.getClaimantName());
			}

			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemGenerals().size() > 0) {
				if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
						.getClaimTemplateItemGenerals().iterator().next().getOpenToDependents()) {
					claimApplicationItemForm.setOpenToDependents(
							claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
									.getClaimTemplateItemGenerals().iterator().next().getOpenToDependents());
				}
			}

			claimApplicationItemForm.setForexRate(claimApplicationItem.getExchangeRate());
			if (claimApplicationItem.getClaimDate() != null) {
				claimApplicationItemForm.setClaimDate(DateUtils.timeStampToString(claimApplicationItem.getClaimDate()));
			}

			claimApplicationItemForm.setClaimAmount(new BigDecimal(df.format(claimApplicationItem.getClaimAmount())));
			claimApplicationItemForm.setCategory(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
			if (claimApplicationItem.getAmountBeforeTax() != null) {
				claimApplicationItemForm
						.setAmountBeforeTax(new BigDecimal(df.format(claimApplicationItem.getAmountBeforeTax())));
			}
			if (claimApplicationItem.getTaxAmount() != null) {
				claimApplicationItemForm.setTaxAmount(new BigDecimal(df.format(claimApplicationItem.getTaxAmount())));
			}

			claimApplicationItemForm.setForexAmount(claimApplicationItem.getForexReceiptAmount());
			claimApplicationItemForm.setRemarks(claimApplicationItem.getRemarks());
			claimApplicationItemForm.setQuantity(claimApplicationItem.getQuantity());
			claimApplicationItemForm.setUnitPrice(claimApplicationItem.getUnitPrice());
			claimApplicationItemForm.setApplicableClaimAmount(claimApplicationItem.getApplicableClaimAmount());

			// ADDED AS DEFAULT CLAIM_TEMPLATE_ITEM_TYPE IS AMOUNT_BASED
			claimApplicationItemForm.setIsAmountBased(true);
			claimApplicationItemForm.setIsQuantityBased(false);
			claimApplicationItemForm.setIsForexBased(false);
			
			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemClaimTypes().size() > 0) {
				ClaimTemplateItemClaimType claimTemplateItemClaimType = claimApplicationItem
						.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemClaimTypes()
						.iterator().next();
				if (claimTemplateItemClaimType.getClaimType().getCodeValue()
						.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_QUANTITY_BASED)) {
					claimApplicationItemForm.setIsQuantityBased(true);
					claimApplicationItemForm.setDefaultUnitPrice(claimTemplateItemClaimType.getDefaultUnitPrice());
				} else {
					claimApplicationItemForm.setIsQuantityBased(false);
				}
				if (claimTemplateItemClaimType.getClaimType().getCodeValue()
						.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED)) {
					claimApplicationItemForm.setIsAmountBased(true);

				} else {
					claimApplicationItemForm.setIsAmountBased(false);
				}
				
				// ADDED FOR FOREX_BASED CHECK
				if (claimTemplateItemClaimType.getClaimType().getCodeValue()
						.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED)) {
					claimApplicationItemForm.setIsForexBased(true);

				} else {
					claimApplicationItemForm.setIsForexBased(false);
				}

			}

			// Get Lundin Claims Details
			Set<ClaimApplicationItemLundinDetail> applicationItemLundinDetails = claimApplicationItem
					.getClaimApplicationItemLundinDetails();
			if (!applicationItemLundinDetails.isEmpty()) {
				claimApplicationItemForm.setBlockName(claimApplicationItem.getClaimApplicationItemLundinDetails()
						.iterator().next().getLundinBlock().getBlockName());
				claimApplicationItemForm.setAfeName(claimApplicationItem.getClaimApplicationItemLundinDetails()
						.iterator().next().getLundinAFE().getAfeName());
			}

			List<ClaimCustomFieldDTO> customFields = new ArrayList<>();
			List<ClaimApplicationItemCustomField> claimApplicationItemCustomFieldList = new ArrayList<ClaimApplicationItemCustomField>(
					claimApplicationItem.getClaimApplicationItemCustomFields());

			Collections.sort(claimApplicationItemCustomFieldList, new ClaimAppTempItemCusFieComp());
			for (ClaimApplicationItemCustomField claimApplicationItemCustomField : claimApplicationItemCustomFieldList) {
				ClaimCustomFieldDTO claimCustomFieldDTO = new ClaimCustomFieldDTO();
				claimCustomFieldDTO.setCustomFieldName(
						claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				if (StringUtils.isNotBlank(claimApplicationItemCustomField.getValue())) {
					if ("date".equalsIgnoreCase(claimApplicationItemCustomField.getClaimTemplateItemCustomField()
							.getFieldType().getCodeDesc())) {

						claimCustomFieldDTO
								.setValue(DateUtils.convertDateFormat(claimApplicationItemCustomField.getValue(),
										claimApplication.getCompany().getDateFormat()));
					} else {
						claimCustomFieldDTO.setValue(claimApplicationItemCustomField.getValue());
					}
				} else {
					claimCustomFieldDTO.setValue("");
				}

				customFields.add(claimCustomFieldDTO);

			}
			List<ClaimApplicationItemAttach> attachements = new ArrayList<>();
			for (ClaimApplicationItemAttachment claimApplicationItemAttachment : claimApplicationItem
					.getClaimApplicationItemAttachments()) {
				ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
				/* ID ENCRYPT */
				claimApplicationItemAttach.setClaimApplicationItemAttachementId(FormatPreserveCryptoUtil
						.encrypt(claimApplicationItemAttachment.getClaimApplicationItemAttachmentId()));
				claimApplicationItemAttach.setFileName(claimApplicationItemAttachment.getFileName());

				attachements.add(claimApplicationItemAttach);
			}
			claimApplicationItemForm.setAttachements(attachements);

			List<ClaimApplicationItemWorkflow> claimApplicationItemWorkflows = new ArrayList<>(
					claimApplicationItem.getClaimApplicationItemWorkflows());
			Collections.sort(claimApplicationItemWorkflows, new ClaimItemWorkComp());
			List<ClaimApplicationItemWorkflowForm> claimApplicationItemWorkflowForms = new ArrayList<>();
			for (ClaimApplicationItemWorkflow claimApplicationItemWorkflow : claimApplicationItemWorkflows) {
				ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm = new ClaimApplicationItemWorkflowForm();
				claimApplicationItemWorkflowForm.setClaimApplicationItemWorkflowID(
						claimApplicationItemWorkflow.getClaimApplicationItemWorkflowId());
				claimApplicationItemWorkflowForm.setCreatedDate(
						DateUtils.timeStampToStringWithTime(claimApplicationItemWorkflow.getCreatedDate()));
				claimApplicationItemWorkflowForm
						.setOverriddenAmount(claimApplicationItemWorkflow.getOverriddenAmount() == null
								? new BigDecimal(0) : claimApplicationItemWorkflow.getOverriddenAmount());
				claimApplicationItemWorkflowForm
						.setOverriddenTaxAmount(claimApplicationItemWorkflow.getOverriddenTaxAmount() == null
								? new BigDecimal(0) : claimApplicationItemWorkflow.getOverriddenTaxAmount());
				claimApplicationItemWorkflowForm.setRemarks(claimApplicationItemWorkflow.getRemarks());
				claimApplicationItemWorkflowForm
						.setCreatedByName(getEmployeeName(claimApplicationItemWorkflow.getCreatedBy()));
				claimApplicationItemWorkflowForm
						.setStatusName(claimApplicationItemWorkflow.getClaimItemWorkflowStatus() == null ? ""
								: claimApplicationItemWorkflow.getClaimItemWorkflowStatus().getCodeDesc());
				claimApplicationItemWorkflowForms.add(claimApplicationItemWorkflowForm);
			}

			claimApplicationItemForm.setClaimApplicationItemWorkflowForms(claimApplicationItemWorkflowForms);
			claimApplicationItemForm.setCustomFields(customFields);
			claimApplicationITemList.add(claimApplicationItemForm);
		}

		ClaimPreferenceForm claimPreferences = getClaimDateAndItemSortOrder(
				claimApplication.getCompany().getCompanyId());

		if ((StringUtils.isNotBlank(claimPreferences.getClaimItemNameSortOrder()))
				&& (StringUtils.isNotBlank(claimPreferences.getClaimItemDateSortOrder()))) {
			Collections.sort(claimApplicationITemList, new ClaimItemCompAndClaimDateComp(
					claimPreference.getClaimItemDateSortOrder(), claimPreference.getClaimItemNameSortOrder()));

		}
		Collections.reverse(claimApplicationITemList);
		addClaimForm.setClaimItems(claimApplicationITemList);
		return addClaimForm;
	}

	/**
	 * Comparator Class for Ordering ClaimApplicationItemWorkflow List
	 */
	private class ClaimItemWorkComp implements Comparator<ClaimApplicationItemWorkflow> {
		public int compare(ClaimApplicationItemWorkflow templateField,
				ClaimApplicationItemWorkflow compWithTemplateField) {
			if (templateField.getClaimApplicationItemWorkflowId() > compWithTemplateField
					.getClaimApplicationItemWorkflowId()) {
				return 1;
			} else if (templateField.getClaimApplicationItemWorkflowId() < compWithTemplateField
					.getClaimApplicationItemWorkflowId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering ASC ClaimApplicationItem List
	 */

	private class ClaimItemCompAndClaimDateComp implements Comparator<ClaimApplicationItemForm> {
		String sortItem, sortByDate;

		public ClaimItemCompAndClaimDateComp(final String sortByDate, final String sortByItem) {
			this.sortByDate = sortByDate;
			this.sortItem = sortByItem;
		}

		@Override
		public int compare(ClaimApplicationItemForm templateField, ClaimApplicationItemForm compWithTemplateField) {

			int comparableVariable = 0;
			Date date = null, date1 = null;

			date = (Date) DateUtils.stringToTimestampWOTime(templateField.getClaimDate());
			date1 = (Date) DateUtils.stringToTimestampWOTime(compWithTemplateField.getClaimDate());

			if (sortByDate.equalsIgnoreCase(PayAsiaConstants.ASC) && (sortItem.equalsIgnoreCase(PayAsiaConstants.ASC)))

			{
				if (date.before(date1)) {
					comparableVariable = 1;
				} else if (date.after(date1)) {
					comparableVariable = -1;
				} else {

					comparableVariable = compWithTemplateField.getClaimApplicationClaimItemName()
							.compareTo(templateField.getClaimApplicationClaimItemName());
				}

			}

			else if ((sortByDate.equalsIgnoreCase(PayAsiaConstants.DESC)
					&& (sortItem.equalsIgnoreCase(PayAsiaConstants.DESC)))) {

				if (date.before(date1)) {
					comparableVariable = -1;
				} else if (date.after(date1)) {
					comparableVariable = 1;
				} else {

					comparableVariable = templateField.getClaimApplicationClaimItemName()
							.compareTo(compWithTemplateField.getClaimApplicationClaimItemName());

				}

			}

			else if ((sortByDate.equalsIgnoreCase(PayAsiaConstants.ASC)
					&& (sortItem.equalsIgnoreCase(PayAsiaConstants.DESC)))) {
				if (date.before(date1)) {
					comparableVariable = 1;
				} else if (date.after(date1)) {
					comparableVariable = -1;
				} else {

					comparableVariable = templateField.getClaimApplicationClaimItemName()
							.compareTo(compWithTemplateField.getClaimApplicationClaimItemName());

				}
			}

			else if ((sortByDate.equalsIgnoreCase(PayAsiaConstants.DESC)
					&& (sortItem.equalsIgnoreCase(PayAsiaConstants.ASC)))) {
				if (date.before(date1)) {
					comparableVariable = -1;
				} else if (date.after(date1)) {
					comparableVariable = 1;
				} else {

					comparableVariable = compWithTemplateField.getClaimApplicationClaimItemName()
							.compareTo(templateField.getClaimApplicationClaimItemName());
				}
			}
			return comparableVariable;
		}
	}

	@Override
	public AddClaimForm deleteClaimTemplateItem(AddClaimDTO addClaimDTO) {
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItem claimApplicationItem = claimApplicationItemDAO.findByClaimApplicationItemId(addClaimDTO);
		if (claimApplicationItem == null) {
			return null;
		}
		claimApplicationItemDAO.delete(claimApplicationItem);
		updateClaimApplicationForDelete(claimApplicationItem.getClaimApplication(), claimApplicationItem);
		return addClaimForm;
	}

	@Override
	public AddClaimForm getEmployeeItemData(AddClaimDTO addClaimDTO) {

		DecimalFormat df = new DecimalFormat("##.00");
		Company company = companyDAO.findById(addClaimDTO.getCompanyId());
		AddClaimForm addClaimForm = new AddClaimForm();

		ClaimApplicationItem claimApplicationItem = claimApplicationItemDAO.findByClaimApplicationItemId(addClaimDTO);

		if (claimApplicationItem == null) {
			return null;
		}

		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		claimApplicationItemForm.setClaimApplicationItemID(claimApplicationItem.getClaimApplicationItemId());
		claimApplicationItemForm
				.setClaimApplicationID(claimApplicationItem.getClaimApplication().getClaimApplicationId());
		claimApplicationItemForm.setClaimAmount(new BigDecimal(df.format(claimApplicationItem.getClaimAmount())));
		claimApplicationItemForm.setReceiptNumber(claimApplicationItem.getReceiptNumber());
		if (claimApplicationItem.getClaimDate() != null) {
			claimApplicationItemForm.setClaimDate(DateUtils.timeStampToString(claimApplicationItem.getClaimDate()));
		}
		if (claimApplicationItem.getAmountBeforeTax() != null) {
			claimApplicationItemForm
					.setAmountBeforeTax(new BigDecimal(df.format(claimApplicationItem.getAmountBeforeTax())));
		}
		if (claimApplicationItem.getTaxAmount() != null) {
			claimApplicationItemForm.setTaxAmount(new BigDecimal(df.format(claimApplicationItem.getTaxAmount())));
		}

		if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemGenerals()
				.size() > 0) {
			claimApplicationItemForm.setOpenToDependents(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimTemplateItemGenerals().iterator().next().getOpenToDependents());
			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemGenerals().iterator().next().getOpenToDependents()) {
				claimApplicationItemForm.setClaimantName(claimApplicationItem.getClaimantName());
			}
		}

		claimApplicationItemForm.setRemarks(claimApplicationItem.getRemarks());
		claimApplicationItemForm.setEmployeeClaimTemplateItemId(
				claimApplicationItem.getEmployeeClaimTemplateItem().getEmployeeClaimTemplateItemId());
		claimApplicationItemForm.setQuantity(claimApplicationItem.getQuantity());
		claimApplicationItemForm.setUnitPrice(claimApplicationItem.getUnitPrice());
		claimApplicationItemForm.setApplicableClaimAmount(claimApplicationItem.getApplicableClaimAmount());

		if (claimApplicationItem.getCurrencyMaster() != null) {
			claimApplicationItemForm.setCurrencyId(claimApplicationItem.getCurrencyMaster().getCurrencyId());
			claimApplicationItemForm.setCurrencyCode(claimApplicationItem.getCurrencyMaster().getCurrencyCode());
			claimApplicationItemForm.setCurrencyName(claimApplicationItem.getCurrencyMaster().getCurrencyName());
		} else {
			CurrencyMaster currencyMaster = claimApplicationItem.getClaimApplication().getEmployeeClaimTemplate()
					.getClaimTemplate().getDefaultCurrency();
			if (currencyMaster != null) {
				claimApplicationItemForm.setCurrencyId(currencyMaster.getCurrencyId());
				claimApplicationItemForm.setCurrencyCode(currencyMaster.getCurrencyCode());
				claimApplicationItemForm.setCurrencyName(currencyMaster.getCurrencyName());
			}
		}

		if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemClaimTypes()
				.size() > 0
				&& claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
						.getClaimTemplateItemClaimTypes().iterator().next().getClaimType().getCodeValue()
						.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(claimApplicationItem.getClaimDate());
			Integer year = cal.get(Calendar.YEAR);
			Integer month = cal.get(Calendar.MONTH);

			BigDecimal receiptAmount = null;
			BigDecimal forexRate = claimApplicationItem.getExchangeRate();
			if (forexRate != null) {
				if (forexRate.compareTo(BigDecimal.ZERO) == 0) {
					receiptAmount = claimApplicationItem.getClaimAmount();

				} else {
					if (claimApplicationItem.getClaimAmount().compareTo(BigDecimal.ZERO) != 0) {
						receiptAmount = new BigDecimal(
								claimApplicationItem.getClaimAmount().floatValue() / forexRate.floatValue());
						receiptAmount = new BigDecimal(df.format(receiptAmount.doubleValue()));
						claimApplicationItemForm.setCurrencyMonth(month);
						claimApplicationItemForm.setCurrencyYear(year);
					}

				}
			}

			claimApplicationItemForm.setForexRate(forexRate);
			claimApplicationItemForm.setReceiptAmount(claimApplicationItem.getForexReceiptAmount());
			claimApplicationItemForm.setIsForexBased(true);

		}

		// Get claim Lundin Details
		Set<ClaimApplicationItemLundinDetail> itemLundinDetailsSet = claimApplicationItem
				.getClaimApplicationItemLundinDetails();
		for (ClaimApplicationItemLundinDetail itemLundinDetail : itemLundinDetailsSet) {
			claimApplicationItemForm.setBlockId(itemLundinDetail.getLundinBlock().getBlockId());
			claimApplicationItemForm.setAfeId(itemLundinDetail.getLundinAFE().getAfeId());
		}
		if (StringUtils.isNotBlank(claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
				.getClaimItemMaster().getAccountCode())) {
			claimApplicationItemForm.setAccountCodeStartWith(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimItemMaster().getAccountCode().substring(0, 1));
		} else {
			claimApplicationItemForm.setAccountCodeStartWith("0");
		}

		List<ClaimCustomFieldDTO> customFieldDTO = new ArrayList<>();
		for (ClaimApplicationItemCustomField claimApplicationItemCustomField : claimApplicationItem
				.getClaimApplicationItemCustomFields()) {
			ClaimCustomFieldDTO claimCustomFieldDTO = new ClaimCustomFieldDTO();
			claimCustomFieldDTO
					.setClaimApplicationItemCustomFieldId(claimApplicationItemCustomField.getClaimItemCustomFieldId());
			claimCustomFieldDTO.setCustomFieldId(
					claimApplicationItemCustomField.getClaimTemplateItemCustomField().getCustomFieldId());
			if (claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldType().getCodeValue()
					.equals(PayAsiaConstants.PAYASIA_DATE)) {

				if (StringUtils.isNotBlank(claimApplicationItemCustomField.getValue())) {
					claimCustomFieldDTO.setValue(DateUtils.convertDateToSpecificFormat(
							claimApplicationItemCustomField.getValue(), company.getDateFormat()));
				}

			} else {
				claimCustomFieldDTO.setValue(claimApplicationItemCustomField.getValue());
			}

			customFieldDTO.add(claimCustomFieldDTO);
		}
		claimApplicationItemForm.setCustomFields(customFieldDTO);

		List<ClaimApplicationItemAttach> attachements = new ArrayList<>();
		for (ClaimApplicationItemAttachment claimApplicationItemAttachment : claimApplicationItem
				.getClaimApplicationItemAttachments()) {
			ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
			claimApplicationItemAttach.setClaimApplicationItemAttachementId(
					FormatPreserveCryptoUtil.encrypt(claimApplicationItemAttachment.getClaimApplicationItemAttachmentId()));
			claimApplicationItemAttach.setFileName(claimApplicationItemAttachment.getFileName());

			attachements.add(claimApplicationItemAttach);
		}
		claimApplicationItemForm.setAttachements(attachements);
		addClaimForm.setClaimApplicationItemForm(claimApplicationItemForm);

		return addClaimForm;
	}

	@Override
	public void uploadClaimAplicationAttachment(String fileName, byte[] imgBytes, Long claimApplicationItemId) {
		ClaimApplicationItemAttachment claimApplicationItemAttachment = new ClaimApplicationItemAttachment();
		ClaimApplicationItem claimApplicationItem = claimApplicationItemDAO.findById(claimApplicationItemId);
		claimApplicationItemAttachment.setFileName(fileName);
		claimApplicationItemAttachment
				.setFileType(fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length()));
		claimApplicationItemAttachment.setUploadedDate(DateUtils.getCurrentTimestamp());
		claimApplicationItemAttachment.setClaimApplicationItem(claimApplicationItem);
		ClaimApplicationItemAttachment saveReturn = claimApplicationItemAttachmentDAO
				.saveReturn(claimApplicationItemAttachment);

		// save Leave attachment to file directory
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				claimApplicationItem.getClaimApplication().getCompany().getCompanyId(),
				PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME, null, null, null, null,
				PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		/*
		 * downloadPath + "/company/" + claimApplicationItem .
		 * getClaimApplication ( ) . getCompany ( ) . getCompanyId ( ) + "/" +
		 * PayAsiaConstants . CLAIM_ATTACHMENT_DIRECTORY_NAME + "/" ;
		 */

		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
			String fileNameNew = String.valueOf(saveReturn.getClaimApplicationItemAttachmentId());
			if (!("").equals(fileNameNew)) {
				fileNameNew = saveReturn.getClaimApplicationItemAttachmentId() + "." + ext;
			}
			awss3LogicImpl.uploadByteArrayFile(imgBytes, filePath + fileNameNew);
		} else {
			FileUtils.uploadFile(imgBytes, fileName, filePath, fileNameSeperator,
					saveReturn.getClaimApplicationItemAttachmentId());
		}

	}

	@Override
	public AddClaimForm uploadClaimItemAttachement(ClaimApplicationItemAttach claimApplicationItemAttach,
			AddClaimDTO addClaimDTO) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItem claimApplicationItem = claimApplicationItemDAO.findByClaimApplicationItemId(addClaimDTO);
		if(claimApplicationItem == null){
			addClaimForm.setStatus("failure");
			return addClaimForm;
		}
		
		ClaimApplicationItemAttachment claimApplicationItemAttachment;

		claimApplicationItemAttachment = new ClaimApplicationItemAttachment();

		String fileName = claimApplicationItemAttach.getAttachment().getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

		claimApplicationItemAttachment.setFileName(fileName);
		claimApplicationItemAttachment.setFileType(fileExt);
		claimApplicationItemAttachment.setUploadedDate(DateUtils.getCurrentTimestamp());
		claimApplicationItemAttachment.setClaimApplicationItem(claimApplicationItem);
		ClaimApplicationItemAttachment saveReturn = claimApplicationItemAttachmentDAO
				.saveReturn(claimApplicationItemAttachment);
		// save Leave attachment to file directory
		filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				claimApplicationItem.getClaimApplication().getCompany().getCompanyId(),
				PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME, null, null, null, null,
				PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		/*
		 * downloadPath + "/company/" +
		 * claimApplicationItem.getClaimApplication().getCompany()
		 * .getCompanyId() + "/" +
		 * PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME + "/";
		 */
		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			String fileNameNew = claimApplicationItemAttach.getAttachment().getOriginalFilename();
			String ext = fileNameNew.substring(fileNameNew.lastIndexOf('.') + 1);

			if (!("").equals(fileNameNew)) {
				fileNameNew = saveReturn.getClaimApplicationItemAttachmentId() + "." + ext;
			}
			awss3LogicImpl.uploadCommonMultipartFile(claimApplicationItemAttach.getAttachment(),
					filePath + fileNameNew);
		} else {
			FileUtils.uploadFile(claimApplicationItemAttach.getAttachment(), filePath, fileNameSeperator,
					saveReturn.getClaimApplicationItemAttachmentId());
		}
		addClaimForm.setStatus("success");
		return addClaimForm;
	}

	@Override
	public AddClaimForm getClaimAppItemForEdit(Long claimApplicationId, Long companyId, Long employeeId) {
		ClaimApplication claimApplicationVO = claimApplicationDAO.findByID(claimApplicationId);
		AddClaimForm addClaimFormResponse = new AddClaimForm();
		EmployeeClaimTemplate employeeClaimTemplate = claimApplicationVO.getEmployeeClaimTemplate();
		Set<EmployeeClaimTemplateItem> employeeClaimTemplateItems = employeeClaimTemplate
				.getEmployeeClaimTemplateItems();
		addClaimFormResponse.setClaimTemplateName(
				claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());

		HashMap<Long, ClaimApplicationItemDTO> claimApplicationItems = new HashMap<>();
		for (EmployeeClaimTemplateItem employeeClaimTemplateItem : employeeClaimTemplateItems) {
			Long claimItemCategoryId = employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster()
					.getClaimCategoryMaster().getClaimCategoryId();

			if (claimApplicationItems.get(claimItemCategoryId) == null) {
				ClaimApplicationItemDTO claimApplicationItemDTO = new ClaimApplicationItemDTO();
				List<ClaimItemDTO> claimItems = new ArrayList<>();
				ClaimItemDTO claimItemDTO = new ClaimItemDTO();
				// mahendra
				claimItemDTO.setEmployeeClaimTemplateItemId(
						FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateItem.getEmployeeClaimTemplateItemId()));
				claimItemDTO.setClaimItemId(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemId());
				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());

				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO
						.findByItemId("" + employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());
				if (claimTemplateItemGeneralVO != null
						&& StringUtils.isNotBlank(claimTemplateItemGeneralVO.getHelpText())) {
					claimItemDTO.setHelpText(claimTemplateItemGeneralVO.getHelpText());
				}
				claimItems.add(claimItemDTO);
				claimApplicationItemDTO.setClaimItems(claimItems);
				claimApplicationItemDTO.setCategoryId(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryId());
				claimApplicationItemDTO.setCategoryName(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
				claimApplicationItems.put(claimItemCategoryId, claimApplicationItemDTO);

			} else {
				ClaimItemDTO claimItemDTO = new ClaimItemDTO();

				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO
						.findByItemId("" + employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());
				if (claimTemplateItemGeneralVO != null
						&& StringUtils.isNotBlank(claimTemplateItemGeneralVO.getHelpText())) {
					claimItemDTO.setHelpText(claimTemplateItemGeneralVO.getHelpText());
				}
				claimItemDTO.setEmployeeClaimTemplateItemId(
						FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateItem.getEmployeeClaimTemplateItemId()));
				claimItemDTO.setClaimItemId(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemId());
				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
				claimApplicationItems.get(claimItemCategoryId).getClaimItems().add(claimItemDTO);

			}

		}
		addClaimFormResponse.setClaimApplicationItems(claimApplicationItems);

		List<EmployeeClaimReviewer> employeeClaimReviewers = employeeClaimReviewerDAO
				.findByClaimTemplateIdAndEmpId(employeeClaimTemplate.getEmployeeClaimTemplateId(), employeeId);

		int totalNoOfReviewers = 0;
		for (EmployeeClaimReviewer employeeClaimReviewer : employeeClaimReviewers) {
			if (employeeId == employeeClaimReviewer.getEmployee1().getEmployeeId()) {
				totalNoOfReviewers++;
				if ("1".equals(employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue())) {
					addClaimFormResponse.setClaimReviewer1(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					addClaimFormResponse
							.setApplyTo(employeeDetailLogic.getEmployeeName(employeeClaimReviewer.getEmployee2()));
					addClaimFormResponse.setApplyToId(employeeClaimReviewer.getEmployee2().getEmployeeId());

				} else if ("2".equals(employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue())) {

					addClaimFormResponse.setClaimReviewer2(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					addClaimFormResponse.setClaimReviewer2Id(employeeClaimReviewer.getEmployee2().getEmployeeId());

				} else if ("3".equals(employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue())) {

					addClaimFormResponse.setClaimReviewer3(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					addClaimFormResponse.setClaimReviewer3Id(employeeClaimReviewer.getEmployee2().getEmployeeId());
				}
			}

		}

		addClaimFormResponse.setTotalNoOfReviewers(totalNoOfReviewers);

		return addClaimFormResponse;
	}

	@Override
	public AddClaimForm deleteClaimApplication(AddClaimDTO addClaimDTO) {
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplication claimApplication = claimApplicationDAO.findByClaimApplicationID(addClaimDTO);
		claimApplicationDAO.delete(claimApplication);
		return addClaimForm;
	}

	@Override
	public String withdrawClaim(AddClaimDTO addClaimDTO) {

		ClaimApplication claimApplication = claimApplicationDAO.findByClaimApplicationID(addClaimDTO);
		Boolean isSuccessfullyWithdrawn = false;

		if (claimApplication == null) {
			return "NO CLAIM APPLICATION";
		}
		try {
			List<ClaimApplicationWorkflow> claimApplicationWorkflows = new ArrayList<>(
					claimApplication.getClaimApplicationWorkflows());

			for (ClaimApplicationWorkflow claimApplicationWorkflow : claimApplicationWorkflows) {
				if (claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName()
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
					return "ERROR";
				}
			}

			ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO
					.findByCondition(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN);

			Date date = new Date();
			claimApplication.setUpdatedDate(new Timestamp(date.getTime()));
			claimApplication.setClaimStatusMaster(claimStatusMaster);
			claimApplicationDAO.update(claimApplication);

			ClaimApplicationWorkflow claimApplicationWorkflow = new ClaimApplicationWorkflow();

			claimApplicationWorkflow.setClaimApplication(claimApplication);
			claimApplicationWorkflow.setClaimStatusMaster(claimStatusMaster);
			claimApplicationWorkflow.setRemarks(claimApplication.getRemarks());
			claimApplicationWorkflow.setForwardTo("");
			claimApplicationWorkflow.setEmailCC("");
			claimApplicationWorkflow.setEmployee(claimApplication.getEmployee());
			claimApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestamp());
			claimApplicationWorkflow.setTotalAmount(claimApplication.getTotalAmount());

			claimApplicationWorkflowDAO.saveReturn(claimApplicationWorkflow);
			isSuccessfullyWithdrawn = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		if (isSuccessfullyWithdrawn) {

			ClaimMailDTO claimMailDTO = new ClaimMailDTO();
			claimMailDTO.setClaimApplication(claimApplication);
			claimMailDTO.setLoggedInCmpId(addClaimDTO.getCompanyId());
			claimMailDTO.setLoggedInEmpId(addClaimDTO.getEmployeeId());
			claimMailDTO.setSubcategoryName(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_WITHDRAWN_REVIEWER);

			claimMailLogic.sendClaimMail(claimMailDTO);
		}

		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplication.getClaimApplicationReviewers()) {
			claimApplicationReviewer.setPending(false);
			claimApplicationReviewerDAO.update(claimApplicationReviewer);
		}

		return "SUCCESS";
	}

	@Override
	public AddClaimForm getForexRate(Date currencyDate, Long currencyId, Long companyId) {
		AddClaimForm addClaimForm = new AddClaimForm();

		CompanyExchangeRate companyExchangeRate = companyExchangeRateDAO.findExchangeRate(currencyDate, currencyId,
				companyId);

		if (companyExchangeRate == null) {
			addClaimForm.setForexRate(new BigDecimal(0));
		} else {
			addClaimForm.setForexRate(companyExchangeRate.getExchangeRate());

		}

		return addClaimForm;
	}

	@Override
	public Employee getDelegatedEmployee(Long claimAppEmpId, Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterClaim = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_CATEGORY_CLAIM);

		WorkflowDelegate workflowDelegateClaim = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
				appCodeMasterClaim.getAppCodeID());

		if (workflowDelegateClaim != null) {
			workflowDelegate = workflowDelegateClaim;
		} else {
			AppCodeMaster appCodeMasterALL = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
					PayAsiaConstants.WORKFLOW_CATEGORY_ALL);

			WorkflowDelegate workflowDelegateALL = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
					appCodeMasterALL.getAppCodeID());

			workflowDelegate = workflowDelegateALL;
		}

		if (workflowDelegate != null) {
			if (claimAppEmpId.equals(workflowDelegate.getEmployee2().getEmployeeId())) {
				return emp;
			}
			return workflowDelegate.getEmployee2();
		}
		return emp;
	}

	@Override
	public AddClaimForm getEmployeeClaimTemplates(String employeeNumber, Long companyId, Long sessionEmployeeId) {
		AddClaimForm addClaimForm = new AddClaimForm();

		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		if (employeeVO == null) {
			return addClaimForm;
		}
		/* Check employee shortlist */
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmployeeId, companyId);
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		if (employeeShortListDTO.getEmployeeShortList()
				&& !companyShortListEmployeeIds.contains(BigInteger.valueOf(employeeVO.getEmployeeId()))) {
			return addClaimForm;
		}

		addClaimForm = getClaimTemplates(companyId, employeeVO.getEmployeeId(), true);
		return addClaimForm;
	}

	@Override
	public AddClaimForm deleteClaimApplicationAttachement(AddClaimDTO addClaimDTO) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItemAttachment claimApplicationItemAttachment = claimApplicationItemAttachmentDAO
				.findByClaimApplicationItemAttachmentID(addClaimDTO);

		boolean success = true;
		try {
			String fileExt = claimApplicationItemAttachment.getFileType();

			filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					claimApplicationItemAttachment.getClaimApplicationItem().getClaimApplication().getCompany()
							.getCompanyId(),
					PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME,
					String.valueOf(claimApplicationItemAttachment.getClaimApplicationItemAttachmentId()), null, null,
					fileExt, PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);

			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				List<String> fileList = new ArrayList<String>();
				fileList.add(filePath);
				awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
			} else {
				FileUtils.deletefile(filePath);
			}
		} catch (Exception exception) {
			success = false;
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

		if (success == true) {
			// delete attachment
			claimApplicationItemAttachmentDAO.delete(claimApplicationItemAttachment);
		}
		return addClaimForm;
	}

	@Override
	public Long getEmployeeId(Long companyId, String employeeNumber) {
		Employee employee = employeeDAO.findByNumber(employeeNumber, companyId);
		return employee.getEmployeeId();
	}

	@Override
	public AddClaimForm deleteApprovedClaimApplication(AddClaimDTO addClaimDTO) {
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplication claimApplication = claimApplicationDAO.findByClaimApplicationID(addClaimDTO);

		claimApplicationItemDAO.deleteByCondition(claimApplication.getClaimApplicationId());

		claimApplicationDAO.delete(claimApplication);
		return addClaimForm;
	}

	@Override
	public AddClaimForm saveAsDraftFromWithdrawClaim(AddClaimDTO addClaimDTO, AddClaimForm addClaimForm) {

		AddClaimForm addClaimFormRes = new AddClaimForm();
		ClaimApplication claimApplicationVO = null;

		try {
			claimApplicationVO = claimApplicationDAO.findByClaimApplicationID(addClaimDTO);

			if (claimApplicationVO == null) {
				return null;
			}

			ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO
					.findByCondition(addClaimForm.getAddClaimStatus());
			if (StringUtils.isNotBlank(addClaimForm.getRemarks())) {
				claimApplicationVO.setRemarks(URLDecoder.decode(addClaimForm.getRemarks(), "UTF-8"));
			} else {
				claimApplicationVO.setRemarks("");
			}

			claimApplicationVO.setEmailCC(addClaimForm.getEmailCC());
			claimApplicationVO.setClaimStatusMaster(claimStatusMaster);

			claimApplicationDAO.update(claimApplicationVO);
			claimApplicationWorkflowDAO.deleteByCondition(claimApplicationVO.getClaimApplicationId());
			claimApplicationReviewerDAO.deleteByCondition(claimApplicationVO.getClaimApplicationId());

			Set<ClaimApplicationItem> claimApplicationItemSet = new HashSet<ClaimApplicationItem>(
					claimApplicationVO.getClaimApplicationItems());
			for (ClaimApplicationItem applicationItem : claimApplicationItemSet) {
				claimApplicationItemWorkflowDAO.deleteByCondition(applicationItem.getClaimApplicationItemId());
				ClaimApplicationItem claimApplicationItem = applicationItem;
				claimApplicationItem.setActive(true);
				claimApplicationItemDAO.update(claimApplicationItem);
			}

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);

		}

		return addClaimFormRes;

	}

	@Override
	public AddClaimForm saveAsDraftClaimApp(Long claimApplicationId, Long companyId, Long employeeId) {

		AddClaimForm addClaimFormRes = new AddClaimForm();
		ClaimApplication claimApplicationVO = null;

		try {
			claimApplicationVO = claimApplicationDAO.findByID(claimApplicationId);
			ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO
					.findByCondition(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);

			claimApplicationVO.setClaimStatusMaster(claimStatusMaster);

			claimApplicationDAO.update(claimApplicationVO);
			claimApplicationWorkflowDAO.deleteByCondition(claimApplicationVO.getClaimApplicationId());
			claimApplicationReviewerDAO.deleteByCondition(claimApplicationVO.getClaimApplicationId());

			Set<ClaimApplicationItem> claimApplicationItemSet = new HashSet<ClaimApplicationItem>(
					claimApplicationVO.getClaimApplicationItems());
			for (ClaimApplicationItem applicationItem : claimApplicationItemSet) {
				claimApplicationItemWorkflowDAO.deleteByCondition(applicationItem.getClaimApplicationItemId());
				ClaimApplicationItem claimApplicationItem = applicationItem;
				claimApplicationItem.setActive(true);
				claimApplicationItemDAO.update(claimApplicationItem);
			}

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return addClaimFormRes;
	}

	@Override
	public AddClaimForm copyClaimApplication(AddClaimDTO addClaimDTO, AddClaimForm addClaimForm) {

		AddClaimForm addClaimFormRes = new AddClaimForm();
		ClaimApplication claimApplicationVO = null;
		ValidateClaimApplicationDTO validateClaimApplicationDTO = new ValidateClaimApplicationDTO();

		boolean isClaimTemplateActive = false;
		try {
			claimApplicationVO = claimApplicationDAO.findByClaimApplicationID(addClaimDTO);

			Date startDate = DateUtils.stringToDate(
					DateUtils.timeStampToString(claimApplicationVO.getEmployeeClaimTemplate().getStartDate(),
							claimApplicationVO.getCompany().getDateFormat()),
					claimApplicationVO.getCompany().getDateFormat());

			Date endDate = null;
			if (claimApplicationVO.getEmployeeClaimTemplate().getEndDate() != null) {
				endDate = DateUtils.stringToDate(
						DateUtils.timeStampToString(claimApplicationVO.getEmployeeClaimTemplate().getEndDate(),
								claimApplicationVO.getCompany().getDateFormat()),
						claimApplicationVO.getCompany().getDateFormat());
			} else {
				Calendar dateForNull = new GregorianCalendar(PayAsiaConstants.LAST_YEAR, PayAsiaConstants.LAST_MONTH,
						PayAsiaConstants.LAST_DAY);
				endDate = dateForNull.getTime();
			}

			Date currDate = DateUtils.stringToDate(
					DateUtils.timeStampToString(DateUtils.getCurrentTimestamp(),
							claimApplicationVO.getCompany().getDateFormat()),
					claimApplicationVO.getCompany().getDateFormat());
			if ((currDate.after(startDate) || currDate.equals(startDate))
					&& (currDate.before(endDate) || currDate.equals(endDate))) {
				isClaimTemplateActive = true;
			}

			if (claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getVisibility()
					&& isClaimTemplateActive) {
				ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO
						.findByCondition(PayAsiaConstants.CLAIM_STATUS_DRAFT);

				ClaimApplication claimApplicationCopy = new ClaimApplication();
				claimApplicationCopy.setClaimNumber(claimApplicationDAO.getMaxClaimNumber() + 1);
				claimApplicationCopy.setClaimStatusMaster(claimStatusMaster);
				claimApplicationCopy.setCompany(claimApplicationVO.getCompany());
				claimApplicationCopy.setEmailCC(claimApplicationVO.getEmailCC());
				claimApplicationCopy.setEmployee(claimApplicationVO.getEmployee());
				claimApplicationCopy.setRemarks(claimApplicationVO.getRemarks());
				claimApplicationCopy.setTotalAmount(claimApplicationVO.getTotalAmount());
				claimApplicationCopy.setTotalItems(claimApplicationVO.getTotalItems());
				claimApplicationCopy.setVisibleToEmployee(claimApplicationVO.getVisibleToEmployee());
				claimApplicationCopy.setEmployeeClaimTemplate(claimApplicationVO.getEmployeeClaimTemplate());

				ClaimApplication persistClaimApplicationCopy = claimApplicationDAO.saveReturn(claimApplicationCopy);

				Set<ClaimApplicationItem> claimApplicationItemSet = new HashSet<ClaimApplicationItem>(
						claimApplicationVO.getClaimApplicationItems());
				for (ClaimApplicationItem applicationItem : claimApplicationItemSet) {
					if (applicationItem.getEmployeeClaimTemplateItem().getActive() == null
							|| !applicationItem.getEmployeeClaimTemplateItem().getActive()) {
						continue;
					}
					if (!applicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getVisibility()) {
						continue;
					}
					if (!applicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimItemMaster()
							.getVisibility()) {
						continue;
					}
					ClaimApplicationItem claimApplicationItemCopy = new ClaimApplicationItem();
					claimApplicationItemCopy.setAmountBeforeTax(applicationItem.getAmountBeforeTax());
					claimApplicationItemCopy.setClaimAmount(applicationItem.getClaimAmount());
					claimApplicationItemCopy.setApplicableClaimAmount(applicationItem.getApplicableClaimAmount());
					claimApplicationItemCopy.setClaimDate(applicationItem.getClaimDate());
					claimApplicationItemCopy.setQuantity(applicationItem.getQuantity());
					claimApplicationItemCopy.setUnitPrice(applicationItem.getUnitPrice());
					claimApplicationItemCopy.setReceiptNumber(applicationItem.getReceiptNumber());
					claimApplicationItemCopy.setRemarks(applicationItem.getRemarks());
					claimApplicationItemCopy.setTaxAmount(applicationItem.getTaxAmount());
					claimApplicationItemCopy.setActive(applicationItem.isActive());
					claimApplicationItemCopy.setClaimantName(applicationItem.getClaimantName());
					claimApplicationItemCopy.setClaimApplication(persistClaimApplicationCopy);
					claimApplicationItemCopy
							.setEmployeeClaimTemplateItem(applicationItem.getEmployeeClaimTemplateItem());
					claimApplicationItemCopy.setCurrencyMaster(applicationItem.getCurrencyMaster());
					claimApplicationItemCopy.setExchangeRate(applicationItem.getExchangeRate());
					claimApplicationItemCopy.setForexReceiptAmount(applicationItem.getForexReceiptAmount());

					ClaimApplicationItem persistClaimApplicationItemCopy = claimApplicationItemDAO
							.saveReturn(claimApplicationItemCopy);

					// Copy Custom Fields
					Set<ClaimApplicationItemCustomField> claimAppItemCustomFieldSet = new HashSet<ClaimApplicationItemCustomField>(
							applicationItem.getClaimApplicationItemCustomFields());
					for (ClaimApplicationItemCustomField applicationItemCustomField : claimAppItemCustomFieldSet) {
						ClaimApplicationItemCustomField customFieldCopy = new ClaimApplicationItemCustomField();
						customFieldCopy.setClaimApplicationItem(persistClaimApplicationItemCopy);
						customFieldCopy.setClaimTemplateItemCustomField(
								applicationItemCustomField.getClaimTemplateItemCustomField());
						customFieldCopy.setValue(applicationItemCustomField.getValue());
						claimApplicationItemCustomFieldDAO.save(customFieldCopy);
					}

					// Copy Lundin Details
					Set<ClaimApplicationItemLundinDetail> claimAppItemLundinDetailSet = new HashSet<ClaimApplicationItemLundinDetail>(
							applicationItem.getClaimApplicationItemLundinDetails());
					for (ClaimApplicationItemLundinDetail claimAppItemLundinDetail : claimAppItemLundinDetailSet) {
						ClaimApplicationItemLundinDetail lundinDetailCopy = new ClaimApplicationItemLundinDetail();
						lundinDetailCopy.setClaimApplicationItem(persistClaimApplicationItemCopy);
						lundinDetailCopy.setLundinBlock(claimAppItemLundinDetail.getLundinBlock());
						lundinDetailCopy.setLundinAFE(claimAppItemLundinDetail.getLundinAFE());

						claimApplicationItemLundinDetailDAO.save(lundinDetailCopy);
					}
				}
			} else {
				validateClaimApplicationDTO.setErrorCode(1);
				validateClaimApplicationDTO.setErrorKey("payasia.claim.assigned.status");
				addClaimFormRes.setValidateClaimApplicationDTO(validateClaimApplicationDTO);
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return addClaimFormRes;
	}

	@Override
	public List<LundinTimesheetReportsForm> lundinBlockList(Long companyId, String claimItemAccountCode) {
		List<LundinTimesheetReportsForm> lundinBlockList = new ArrayList<>();

		List<LundinBlock> lundinBlockVOList = lundinBlockDAO.findByCondition(companyId);

		for (LundinBlock lundinBlock : lundinBlockVOList) {
			if (claimItemAccountCode.equalsIgnoreCase("3") && (lundinBlock.getBlockCode().startsWith("C")
					|| lundinBlock.getBlockCode().startsWith("c") || lundinBlock.getBlockCode().equalsIgnoreCase("COM")
					|| lundinBlock.getBlockCode().startsWith("NV") || lundinBlock.getBlockCode().startsWith("nv"))) {
			} else {
				LundinTimesheetReportsForm otTimesheetReportsForm = new LundinTimesheetReportsForm();
				otTimesheetReportsForm.setBlockId(lundinBlock.getBlockId());
				otTimesheetReportsForm.setBlockName(lundinBlock.getBlockName());
				if (lundinBlock.getBlockCode().equalsIgnoreCase("COM")) {
					otTimesheetReportsForm.setDefaultBlockCodeId(lundinBlock.getBlockId());
				}
				lundinBlockList.add(otTimesheetReportsForm);
			}

		}
		return lundinBlockList;

	}

	@Override
	public List<LundinTimesheetReportsForm> lundinAFEList(Long companyId, Long blockId, String claimItemAccountCode) {
		List<LundinTimesheetReportsForm> lundinBlockList = new ArrayList<>();
		LundinBlock lundinBlockVO = lundinBlockDAO.findById(blockId);
		if (lundinBlockVO != null) {
			List<LundinAFE> lundinAFEVOList = lundinAFEDAO.findByCondition(blockId, companyId);

			for (LundinAFE lundinAFE : lundinAFEVOList) {
				if (claimItemAccountCode.equalsIgnoreCase("3") && lundinAFE.getAfeCode()
						.equalsIgnoreCase(PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED_CODE)) {
				} else {
					LundinTimesheetReportsForm otTimesheetReportsForm = new LundinTimesheetReportsForm();
					otTimesheetReportsForm.setAfeId(lundinAFE.getAfeId());
					otTimesheetReportsForm.setAfeName(lundinAFE.getAfeName());
					if (lundinBlockVO.getBlockCode().equalsIgnoreCase("COM") && lundinAFE.getAfeCode()
							.equalsIgnoreCase(PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED_CODE)) {
						otTimesheetReportsForm.setDefaultAfeId(lundinAFE.getAfeId());
					}
					lundinBlockList.add(otTimesheetReportsForm);
				}
			}
		}
		return lundinBlockList;
	}

	private void setClaimApplicationItemOnClaimType(ClaimApplicationItemForm claimApplicationItemForm,
			ClaimApplicationItem claimApplicationItem) {
		Integer taxPercentage = 0;
		Boolean allowOverrideTaxAmt = null;
		EmployeeClaimTemplateItem employeeClaimTemplateItem = employeeClaimTemplateItemDAO
				.findByID(FormatPreserveCryptoUtil.decrypt(claimApplicationItemForm.getEmployeeClaimTemplateItemId()));
		claimApplicationItem.setEmployeeClaimTemplateItem(employeeClaimTemplateItem);
		claimApplicationItem.setQuantity(
				claimApplicationItemForm.getQuantity() != null ? claimApplicationItemForm.getQuantity() : 0);
		claimApplicationItemForm.setUnitPrice(claimApplicationItemForm.getUnitPrice() != null
				? claimApplicationItemForm.getUnitPrice() : BigDecimal.ZERO);
		claimApplicationItem.setUnitPrice(claimApplicationItemForm.getUnitPrice().compareTo(BigDecimal.ZERO) > 0
				? claimApplicationItemForm.getUnitPrice() : BigDecimal.ZERO);
		claimApplicationItemForm.setTaxAmount(claimApplicationItemForm.getTaxAmount() != null
				? claimApplicationItemForm.getTaxAmount() : BigDecimal.ZERO);
		claimApplicationItemForm.setForexAmount(claimApplicationItemForm.getForexAmount() != null
				? claimApplicationItemForm.getForexAmount() : BigDecimal.ZERO);
		claimApplicationItemForm.setClaimAmount(claimApplicationItemForm.getClaimAmount() != null
				? claimApplicationItemForm.getClaimAmount() : BigDecimal.ZERO);
		claimApplicationItemForm.setForexRate(claimApplicationItemForm.getForexRate() != null
				? claimApplicationItemForm.getForexRate() : BigDecimal.ZERO);
		claimApplicationItem.setExchangeRate(claimApplicationItemForm.getForexRate().compareTo(BigDecimal.ZERO) > 0
				? claimApplicationItemForm.getForexRate() : BigDecimal.ZERO);
		if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals().size() > 0) {
			if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals().iterator().next()
					.getOpenToDependents()) {
				claimApplicationItem.setClaimantName(claimApplicationItemForm.getClaimantName());
			}

			taxPercentage = employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals().iterator()
					.next().getTaxPercentage();
			taxPercentage = taxPercentage != null ? taxPercentage : 0;

			allowOverrideTaxAmt = employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals()
					.iterator().next().getAllowOverrideTaxAmt();

		}

		if (claimApplicationItemForm.getCurrencyId() != null) {
			CurrencyMaster currencyMaster = currencyMasterDAO.findById(claimApplicationItemForm.getCurrencyId());
			claimApplicationItem.setCurrencyMaster(currencyMaster);
		}
		claimApplicationItemForm.setTaxAmount(claimApplicationItemForm.getTaxAmount() != null
				? claimApplicationItemForm.getTaxAmount() : BigDecimal.ZERO);
		if (claimApplicationItemForm.isRequestFromWebService()
				&& StringUtils.isNotBlank(claimApplicationItemForm.getClaimType())
				&& "forex".equalsIgnoreCase(claimApplicationItemForm.getClaimType())) {
			claimApplicationItem.setClaimAmount(claimApplicationItemForm.getClaimAmount());
			claimApplicationItem.setForexReceiptAmount(claimApplicationItemForm.getReceiptAmount());
			claimApplicationItem.setApplicableClaimAmount(claimApplicationItemForm.getForexAmount());
			claimApplicationItem.setAmountBeforeTax(Boolean.TRUE.equals(allowOverrideTaxAmt)
					? calAmountBeforeTaxOnAllowOverTaxAmount(claimApplicationItemForm.getForexAmount(),
							claimApplicationItemForm.getTaxAmount())
					: calAmountBeforeTax(claimApplicationItemForm.getForexAmount(), taxPercentage));
			claimApplicationItem
					.setTaxAmount(Boolean.TRUE.equals(allowOverrideTaxAmt) ? claimApplicationItemForm.getTaxAmount()
							: calTaxAmount(claimApplicationItemForm.getForexAmount(),
									claimApplicationItem.getAmountBeforeTax()));

		} else {

			claimApplicationItem.setClaimAmount(claimApplicationItemForm.getClaimAmount());
			switch (claimApplicationItemForm.getClaimType()) {
			case PayAsiaConstants.PAYASIA_CLAIM_TYPE_FORREX_BASED:
				claimApplicationItem.setClaimAmount(claimApplicationItemForm.getForexAmount());
				claimApplicationItem.setForexReceiptAmount(claimApplicationItemForm.getClaimAmount());
				claimApplicationItem.setApplicableClaimAmount(claimApplicationItemForm.getForexAmount());
				claimApplicationItem.setAmountBeforeTax(Boolean.TRUE.equals(allowOverrideTaxAmt)
						? calAmountBeforeTaxOnAllowOverTaxAmount(claimApplicationItemForm.getForexAmount(),
								claimApplicationItemForm.getTaxAmount())
						: calAmountBeforeTax(claimApplicationItemForm.getForexAmount(), taxPercentage));

				claimApplicationItem
						.setTaxAmount(Boolean.TRUE.equals(allowOverrideTaxAmt) ? claimApplicationItemForm.getTaxAmount()
								: calTaxAmount(claimApplicationItemForm.getForexAmount(),
										claimApplicationItem.getAmountBeforeTax()));
				break;
			case PayAsiaConstants.PAYASIA_CLAIM_TYPE_QUANTITY_BASED:

				claimApplicationItem.setApplicableClaimAmount(new BigDecimal(0));
				claimApplicationItem.setAmountBeforeTax(Boolean.TRUE.equals(allowOverrideTaxAmt)
						? calAmountBeforeTaxOnAllowOverTaxAmount(claimApplicationItemForm.getClaimAmount(),
								claimApplicationItemForm.getTaxAmount())
						: calAmountBeforeTax(claimApplicationItemForm.getClaimAmount(), taxPercentage));
				claimApplicationItem
						.setTaxAmount(Boolean.TRUE.equals(allowOverrideTaxAmt) ? claimApplicationItemForm.getTaxAmount()
								: calTaxAmount(claimApplicationItemForm.getClaimAmount(),
										claimApplicationItem.getAmountBeforeTax()));
				break;
			case PayAsiaConstants.PAYASIA_CLAIM_TYPE_AMOUNT_BASED:
				Integer receiptAmountPercentage = employeeClaimTemplateItemDAO.findReceiptAmountPercentageForAmountBase(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());
				receiptAmountPercentage = receiptAmountPercentage != null ? receiptAmountPercentage : 0;
				if (receiptAmountPercentage.equals(100) || receiptAmountPercentage.equals(0)) {
					claimApplicationItem.setApplicableClaimAmount(claimApplicationItemForm.getClaimAmount());
					claimApplicationItem.setAmountBeforeTax(Boolean.TRUE.equals(allowOverrideTaxAmt)
							? calAmountBeforeTaxOnAllowOverTaxAmount(claimApplicationItemForm.getClaimAmount(),
									claimApplicationItemForm.getTaxAmount())
							: calAmountBeforeTax(claimApplicationItemForm.getClaimAmount(), taxPercentage));
					claimApplicationItem.setTaxAmount(
							Boolean.TRUE.equals(allowOverrideTaxAmt) ? claimApplicationItemForm.getTaxAmount()
									: calTaxAmount(claimApplicationItemForm.getClaimAmount(),
											claimApplicationItem.getAmountBeforeTax()));
				}

				else {
					claimApplicationItem.setApplicableClaimAmount(claimApplicationItemForm.getClaimAmount()
							.multiply(BigDecimal.valueOf((receiptAmountPercentage)).divide(BigDecimal.valueOf(100), 4,
									RoundingMode.HALF_DOWN)));
					claimApplicationItem.setAmountBeforeTax(Boolean.TRUE.equals(allowOverrideTaxAmt)
							? calAmountBeforeTaxOnAllowOverTaxAmount(claimApplicationItem.getApplicableClaimAmount(),
									claimApplicationItemForm.getTaxAmount())
							: calAmountBeforeTax(claimApplicationItem.getApplicableClaimAmount(), taxPercentage));
					claimApplicationItem.setTaxAmount(
							Boolean.TRUE.equals(allowOverrideTaxAmt) ? claimApplicationItemForm.getTaxAmount()
									: calTaxAmount(claimApplicationItem.getApplicableClaimAmount(),
											claimApplicationItem.getAmountBeforeTax()));

				}
				break;
			}
		}
	}

	private BigDecimal calAmountBeforeTax(BigDecimal claimAmount, Integer taxPercentage) {
		return claimAmount.divide(BigDecimal.valueOf((100 + taxPercentage)), 4, RoundingMode.HALF_DOWN)
				.multiply(BigDecimal.valueOf(100));

	}

	private BigDecimal calTaxAmount(BigDecimal claimAmount, BigDecimal amountBeforeTax) {
		return claimAmount.subtract(amountBeforeTax);
	}

	private BigDecimal calAmountBeforeTaxOnAllowOverTaxAmount(BigDecimal claimAmount, BigDecimal taxAmount) {
		return claimAmount.subtract(taxAmount);
	}

	/*
	 * NEW METHOD FOR CLAIM TEMPLATE DATA
	 */
	@Override
	public EmployeeClaimTemplateDataResponse getClaimTemplatesData(Long companyId, Long employeeId) {
		EmployeeClaimTemplateDataResponse employeeClaimTemplateDataResponse = new EmployeeClaimTemplateDataResponse();
		boolean isAdmin = false;
		
		AddClaimForm addClaimForm = getClaimTemplates(companyId, employeeId, isAdmin);
		
		List<EmployeeClaimTemplateDataDTO> employeeClaimTemplateDataDTOList = employeeClaimTemplateDAO
				.getEmployeeTemplateData(companyId, employeeId);

		for (ClaimTemplateForm claimTempForm : addClaimForm.getClaimTemplates()) {
			for (EmployeeClaimTemplateDataDTO empClaimTempDataDTO : employeeClaimTemplateDataDTOList) {
				if((claimTempForm.getClaimTemplateId().longValue()==empClaimTempDataDTO.getClaimTemplateId().longValue()) && (claimTempForm.getEmployeeClaimTemplateId().longValue()==empClaimTempDataDTO.getEmployeeClaimTemplateId().longValue())){
					empClaimTempDataDTO.setClaimTemplateConfig(claimTempForm);
					break;
				}
			}
		}
		
		employeeClaimTemplateDataResponse.setRows(employeeClaimTemplateDataDTOList);
		return employeeClaimTemplateDataResponse;
	}
}
