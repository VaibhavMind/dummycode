package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimCustomFieldDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.dto.ClaimMailDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.PendingClaimConditionDTO;
import com.payasia.common.dto.ValidationClaimItemDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ClaimApplicationForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimApplicationItemWorkflowForm;
import com.payasia.common.form.ClaimApplicationWorkflowForm;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingClaimsForm;
import com.payasia.common.form.PendingClaimsResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimApplicationItemAttachmentDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.ClaimApplicationItemWorkflowDAO;
import com.payasia.dao.ClaimApplicationReviewerDAO;
import com.payasia.dao.ClaimApplicationWorkflowDAO;
import com.payasia.dao.ClaimItemMasterDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.ClaimTemplateItemGeneralDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeDefaultEmailCCDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.bean.AppCodeMaster;
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
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemClaimType;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.ClaimTemplateWorkflow;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeDefaultEmailCC;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.NotificationAlert;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.ClaimFormPrintPDFLogic;
import com.payasia.logic.ClaimMailLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.PendingClaimsLogic;

@Component
public class PendingClaimsLogicImpl implements PendingClaimsLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(PendingClaimsLogicImpl.class);

	@Resource
	ClaimApplicationReviewerDAO claimApplicationReviewerDAO;

	@Resource
	ClaimApplicationItemAttachmentDAO claimApplicationItemAttachmentDAO;

	@Resource
	ClaimApplicationItemDAO claimApplicationItemDAO;

	@Resource
	ClaimApplicationItemWorkflowDAO claimApplicationItemWorkflowDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	ClaimApplicationDAO claimApplicationDAO;

	@Resource
	ClaimStatusMasterDAO claimStatusMasterDAO;

	@Resource
	ClaimApplicationWorkflowDAO claimApplicationWorkflowDAO;

	@Resource
	ClaimMailLogic claimMailLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

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

	@Resource
	ClaimFormPrintPDFLogic claimFormPrintPDFLogic;

	@Resource
	ModuleMasterDAO moduleMasterDAO;

	@Resource
	NotificationAlertDAO notificationAlertDAO;
	
	@Resource
	EmployeeDefaultEmailCCDAO employeeDefaultEmailCCDAO;
	
	@Resource
	GeneralLogic generalLogic;

	@Autowired
	private MessageSource messageSource;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	
	@Resource
	private EmployeeDetailLogic employeeDetailLogic;
	
	@Resource
	ClaimStatusMasterDAO claimstatusMasterDAO;

	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;
	
	@Resource
	ClaimItemMasterDAO claimItemMasterDAO;
	
	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;
	
	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;

	@Resource
	ClaimTemplateItemGeneralDAO claimTemplateItemGeneralDAO;

	@Override
	public PendingClaimsResponseForm getPendingClaims(AddClaimDTO addClaimDTO, PageRequest pageDTO,
			SortCondition sortDTO) {
		PendingClaimsResponseForm pendingClaimsResponseForm = new PendingClaimsResponseForm();

		PendingClaimConditionDTO claimConditionDTO = new PendingClaimConditionDTO();

		if(StringUtils.isNotBlank(addClaimDTO.getSearchCondition()) && addClaimDTO.getSearchCondition()!=null){
			if (addClaimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
				if (StringUtils.isNotBlank(addClaimDTO.getSearchText())) {
					claimConditionDTO.setCreatedDate(addClaimDTO.getSearchText().trim());
				}
			}

			if (addClaimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
				if (StringUtils.isNotBlank(addClaimDTO.getSearchText())) {
					claimConditionDTO.setClaimGroup("%" + addClaimDTO.getSearchText().trim() + "%");
				}
			}

			if (addClaimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
				if (StringUtils.isNotBlank(addClaimDTO.getSearchText()) && addClaimDTO.getSearchText().matches("^[0-9]+$")) {
					claimConditionDTO.setClaimNumber(Long.parseLong(addClaimDTO.getSearchText().trim()));
				}
				else{
					return null;
				}
			}
		}
		int recordSize = 0;

		recordSize = claimApplicationReviewerDAO.findByConditionCountRecords(addClaimDTO.getEmployeeId(),
				claimConditionDTO);

		Employee employeeVO = employeeDAO.findById(addClaimDTO.getEmployeeId());
		Long companyId = employeeVO.getCompany().getCompanyId();
		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		claimConditionDTO.setCreatedDateSortOrder(claimPreferenceForm.getCreatedDateSortOrder());
		claimConditionDTO.setClaimNumberSortOrder(claimPreferenceForm.getClaimNumberSortOrder());
		List<ClaimApplicationReviewer> claimApplicationReviewers = claimApplicationReviewerDAO
				.findByCondition(addClaimDTO.getEmployeeId(), pageDTO, sortDTO, claimConditionDTO);

		claimConditionDTO.setClaimStatusName(PayAsiaConstants.CLAIM_STATUS_PENDING);

		List<PendingClaimsForm> pendingClaimsForms = new ArrayList<PendingClaimsForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			PendingClaimsForm pendingClaimsForm = new PendingClaimsForm();

			ClaimApplication claimApplication = claimApplicationReviewer.getClaimApplication();

			pendingClaimsForm.setCreatedBy(generalLogic.getEmployeeNameWithNumber(claimApplication.getEmployee()));
			pendingClaimsForm.setCreatedDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount.append(claimApplication.getTotalItems());

			pendingClaimsForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));

			pendingClaimsForm.setTotalItems(String.valueOf(claimTemplateItemCount));

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			pendingClaimsForm.setTotalAmount(new BigDecimal(df.format(totalApplicableAmount)));
			pendingClaimsForm.setClaimNumber(claimApplication.getClaimNumber());
			pendingClaimsForm.setClaimTemplateName(
					claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			pendingClaimsForm.setClaimApplicationReviewerId(FormatPreserveCryptoUtil.encrypt(claimApplicationReviewer.getClaimApplicationReviewerId()));
			pendingClaimsForms.add(pendingClaimsForm);
		}

		pendingClaimsResponseForm.setPendingClaims(pendingClaimsForms);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			pendingClaimsResponseForm.setPage(pageDTO.getPageNumber());
			pendingClaimsResponseForm.setTotal(totalPages);
			pendingClaimsResponseForm.setRecords(recordSize);
		}
		return pendingClaimsResponseForm;

	}

	private ClaimPreferenceForm getClaimGridSortOrder(Long companyId) {
		String claimNumberSortOrder = "";
		String createdDateSortOrder = "";
		ClaimPreferenceForm claimPreferenceForm = new ClaimPreferenceForm();
		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreferenceVO != null) {
			if (StringUtils.isNotBlank(claimPreferenceVO.getGridClaimNumberSortOrder())) {
				claimNumberSortOrder = claimPreferenceVO.getGridClaimNumberSortOrder();
			}
			if (StringUtils.isNotBlank(claimPreferenceVO.getGridCreatedDateSortOrder())) {
				createdDateSortOrder = claimPreferenceVO.getGridCreatedDateSortOrder();
			}
		}
		claimPreferenceForm.setClaimNumberSortOrder(claimNumberSortOrder);
		claimPreferenceForm.setCreatedDateSortOrder(createdDateSortOrder);
		return claimPreferenceForm;
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
	public PendingClaimsForm getDataForClaimReview(Long claimApplicationReviewerId, Long empId, Locale locale, MessageSource messageSource) {
		PendingClaimsForm pendingClaimsForm = new PendingClaimsForm();

		ClaimApplicationReviewer claimApplicationReviewer = claimApplicationReviewerDAO.getClaimApplicationReviewerDetail(claimApplicationReviewerId, empId);

		if (claimApplicationReviewer == null) {
			return null;
		}
		ClaimApplication claimApplication = claimApplicationReviewer.getClaimApplication();
		pendingClaimsForm.setCreatedBy(generalLogic.getEmployeeNameWithNumber(claimApplication.getEmployee()));
		ClaimApplicationForm claimApplicationForm = new ClaimApplicationForm();
		setClaimApplicationData(claimApplicationForm, claimApplication, empId, locale, messageSource);
		pendingClaimsForm.setClaimApplicationForm(claimApplicationForm);
		return pendingClaimsForm;
	}

	private void setClaimApplicationData(ClaimApplicationForm claimApplicationForm, ClaimApplication claimApplication,
			Long employeeId, Locale locale,MessageSource messageSource) {
		DecimalFormat df = new DecimalFormat("##.00");
		String user = "payasia.user";
		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		Timestamp claimAppSubmittedDate = null;
		for (ClaimTemplateWorkflow claimTemplateWorkflow : claimApplication.getEmployeeClaimTemplate()
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

		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplication.getClaimApplicationReviewers()) {
			if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

				if (employeeId.equals(claimApplicationReviewer.getEmployee().getEmployeeId())) {
					if (allowOverride.length() == 3 && allowOverride.substring(0, 1).equals("1")) {
						claimApplicationForm.setCanOverride(true);
					} else {
						claimApplicationForm.setCanOverride(false);
					}
					if (allowReject.length() == 3 && allowReject.substring(0, 1).equals("1")) {
						claimApplicationForm.setCanReject(true);
					} else {
						claimApplicationForm.setCanReject(false);
					}
					if (allowApprove.length() == 3 && allowApprove.substring(0, 1).equals("1")) {
						claimApplicationForm.setCanApprove(true);
					} else {
						claimApplicationForm.setCanApprove(false);
					}
					if (allowForward.length() == 3 && allowForward.substring(0, 1).equals("1")) {
						claimApplicationForm.setCanForward(true);
					} else {
						claimApplicationForm.setCanForward(false);
					}
				}

			}

			if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

				if (employeeId.equals(claimApplicationReviewer.getEmployee().getEmployeeId())) {
					if (allowOverride.length() == 3 && allowOverride.substring(1, 2).equals("1")) {
						claimApplicationForm.setCanOverride(true);
					} else {
						claimApplicationForm.setCanOverride(false);
					}
					if (allowReject.length() == 3 && allowReject.substring(1, 2).equals("1")) {
						claimApplicationForm.setCanReject(true);
					} else {
						claimApplicationForm.setCanReject(false);
					}
					if (allowApprove.length() == 3 && allowApprove.substring(1, 2).equals("1")) {
						claimApplicationForm.setCanApprove(true);
					} else {
						claimApplicationForm.setCanApprove(false);
					}
					if (allowForward.length() == 3 && allowForward.substring(1, 2).equals("1")) {
						claimApplicationForm.setCanForward(true);
					} else {
						claimApplicationForm.setCanForward(false);
					}
				}

			}

			if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

				if (employeeId.equals(claimApplicationReviewer.getEmployee().getEmployeeId())) {
					if (allowOverride.length() == 3 && allowOverride.substring(2, 3).equals("1")) {
						claimApplicationForm.setCanOverride(true);
					} else {
						claimApplicationForm.setCanOverride(false);
					}
					if (allowReject.length() == 3 && allowReject.substring(2, 3).equals("1")) {
						claimApplicationForm.setCanReject(true);
					} else {
						claimApplicationForm.setCanReject(false);
					}
					if (allowApprove.length() == 3 && allowApprove.substring(2, 3).equals("1")) {
						claimApplicationForm.setCanApprove(true);
					} else {
						claimApplicationForm.setCanApprove(false);
					}
					if (allowForward.length() == 3 && allowForward.substring(2, 3).equals("1")) {
						claimApplicationForm.setCanForward(true);
					} else {
						claimApplicationForm.setCanForward(false);
					}
				}

			}
		}

		claimApplicationForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));
		claimApplicationForm.setClaimNumber(claimApplication.getClaimNumber());

		BigDecimal totalApplicableAmount = BigDecimal.ZERO;
		totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);

		claimApplicationForm.setTotalAmount(new BigDecimal(df.format(totalApplicableAmount)));
		claimApplicationForm.setTotalItems(claimApplication.getTotalItems());
		claimApplicationForm.setCreatedDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));
		claimApplicationForm
				.setClaimFormName(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());

		List<ClaimApplicationItem> claimApplicationItems = new ArrayList<>(claimApplication.getClaimApplicationItems());
		Collections.sort(claimApplicationItems, new ClaimItemTypeComp());

		List<ClaimApplicationItemForm> claimApplicationItemForms = new ArrayList<>();
		for (ClaimApplicationItem claimApplicationItem : claimApplicationItems) {

			ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();

			ClaimApplicationItemWorkflow claimApplicationItemWorkflowStatus = claimApplicationItemWorkflowDAO
					.findClaimItemStatus(claimApplicationItem, employeeId);
			claimApplicationItemForm.setCurrencyCode(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimTemplate().getDefaultCurrency() == null ? ""
							: claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
									.getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			if (claimApplicationItemWorkflowStatus != null
					&& claimApplicationItemWorkflowStatus.getClaimItemWorkflowStatus().getCodeDesc()
							.equals(PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS_REJECTED)) {

				claimApplicationItemForm.setRejected(true);

			} else {
				claimApplicationItemForm.setRejected(false);
			}

			if (claimApplicationItem.getCurrencyMaster() != null) {
				claimApplicationItemForm.setCurrencyName(claimApplicationItem.getCurrencyMaster().getCurrencyName());
			}
			claimApplicationItemForm.setForexRate(claimApplicationItem.getExchangeRate());
			claimApplicationItemForm.setTaxAmount(claimApplicationItem.getTaxAmount());
			claimApplicationItemForm.setForexAmount(claimApplicationItem.getForexReceiptAmount());
			claimApplicationItemForm.setActive(claimApplicationItem.isActive());
			claimApplicationItemForm.setEmployeeClaimTemplateItemId(
					FormatPreserveCryptoUtil.encrypt(claimApplicationItem.getEmployeeClaimTemplateItem().getEmployeeClaimTemplateItemId()));
			claimApplicationItemForm.setReceiptNumber(claimApplicationItem.getReceiptNumber());
			claimApplicationItemForm.setClaimApplicationItemID(FormatPreserveCryptoUtil.encrypt(claimApplicationItem.getClaimApplicationItemId()));
			claimApplicationItemForm.setClaimApplicationClaimItemName(claimApplicationItem
					.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
			claimApplicationItemForm.setClaimDate(DateUtils.timeStampToString(claimApplicationItem.getClaimDate()));
			claimApplicationItemForm.setClaimAmount(claimApplicationItem.getClaimAmount());
			claimApplicationItemForm.setCategory(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
			claimApplicationItemForm.setAmountBeforeTax(claimApplicationItem.getAmountBeforeTax());
			claimApplicationItemForm.setRemarks(claimApplicationItem.getRemarks());
			claimApplicationItemForm.setQuantity(claimApplicationItem.getQuantity());
			claimApplicationItemForm.setUnitPrice(claimApplicationItem.getUnitPrice());
			claimApplicationItemForm.setApplicableClaimAmount(claimApplicationItem.getApplicableClaimAmount());
			claimApplicationItemForm.setTaxAmount(claimApplicationItem.getTaxAmount());

			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemClaimTypes().size() > 0) {
				if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
						.getClaimTemplateItemGenerals().size() > 0) {
					ClaimTemplateItemGeneral claimTemplateItemGeneral = claimApplicationItem
							.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemGenerals()
							.iterator().next();
					claimApplicationItemForm.setTaxAmountPer(claimTemplateItemGeneral.getTaxPercentage());
					claimApplicationItemForm.setAllowOverrideTaxAmt(claimTemplateItemGeneral.getAllowOverrideTaxAmt());
					if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
							.getClaimTemplateItemGenerals().iterator().next().getOpenToDependents()) {
						if (StringUtils.isNotBlank(claimApplicationItem.getClaimantName())) {
							claimApplicationItemForm.setClaimantName(claimApplicationItem.getClaimantName());
						}
					}
				}
			}
			
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
					claimApplicationItemForm
							.setApplicableClaimAmountPer(claimTemplateItemClaimType.getReceiptAmtPercentApplicable());
					Integer amtApplicablePercent = claimTemplateItemClaimType.getReceiptAmtPercentApplicable();
					if (amtApplicablePercent == null || amtApplicablePercent == 0) {
						claimApplicationItemForm.setApplicableClaimAmountPer(
								PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT);
					}
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
			List<ClaimCustomFieldDTO> customFields = new ArrayList<>();
			for (ClaimApplicationItemCustomField claimApplicationItemCustomField : claimApplicationItem
					.getClaimApplicationItemCustomFields()) {
				ClaimCustomFieldDTO claimCustomFieldDTO = new ClaimCustomFieldDTO();
				claimCustomFieldDTO.setCustomFieldName(
						claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				if (claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldType().getCodeValue()
						.equalsIgnoreCase("Date")) {
					if (StringUtils.isNotBlank(claimApplicationItemCustomField.getValue())) {
						claimCustomFieldDTO
								.setValue(DateUtils.convertDateFormat(claimApplicationItemCustomField.getValue(),
										claimApplication.getCompany().getDateFormat()));
					}

				} else {
					claimCustomFieldDTO.setValue(claimApplicationItemCustomField.getValue());
				}

				customFields.add(claimCustomFieldDTO);

			}
			List<ClaimApplicationItemAttach> attachements = new ArrayList<>();
			for (ClaimApplicationItemAttachment claimApplicationItemAttachment : claimApplicationItem
					.getClaimApplicationItemAttachments()) {
				ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
				claimApplicationItemAttach.setClaimApplicationItemAttachementId(
						FormatPreserveCryptoUtil.encrypt(claimApplicationItemAttachment.getClaimApplicationItemAttachmentId()));
				claimApplicationItemAttach.setFileName(claimApplicationItemAttachment.getFileName());
				attachements.add(claimApplicationItemAttach);
			}
			List<ClaimApplicationItemWorkflow> claimApplicationItemWorkflows = new ArrayList<>(
					claimApplicationItem.getClaimApplicationItemWorkflows());
			Collections.sort(claimApplicationItemWorkflows, new ClaimItemWorkComp());
			List<ClaimApplicationItemWorkflowForm> claimApplicationItemWorkflowForms = new ArrayList<>();
			
			byte[] empImage = null;
			for (ClaimApplicationItemWorkflow claimApplicationItemWorkflow : claimApplicationItemWorkflows) {
				ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm = new ClaimApplicationItemWorkflowForm();
				claimApplicationItemWorkflowForm.setClaimApplicationItemWorkflowID(
						FormatPreserveCryptoUtil.encrypt(claimApplicationItemWorkflow.getClaimApplicationItemWorkflowId()));
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
				
				try {
					empImage = employeeDetailLogic.getEmployeeImage(claimApplicationItemWorkflow.getCreatedBy().getEmployeeId(), null, employeeImageWidth, employeeImageHeight);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				claimApplicationItemWorkflowForm.setImage(empImage);
				claimApplicationItemWorkflowForms.add(claimApplicationItemWorkflowForm);
			}

			claimApplicationItemForm.setClaimApplicationItemWorkflowForms(claimApplicationItemWorkflowForms);
			claimApplicationItemForm.setAttachements(attachements);
			claimApplicationItemForm.setCustomFields(customFields);
			claimApplicationItemForm.setClaimApplicationID(FormatPreserveCryptoUtil.encrypt(claimApplicationItem.getClaimApplication().getClaimApplicationId()));

			// Get Lundin Claims Details
			Set<ClaimApplicationItemLundinDetail> applicationItemLundinDetails = claimApplicationItem
					.getClaimApplicationItemLundinDetails();
			if (!applicationItemLundinDetails.isEmpty()) {
				claimApplicationItemForm.setBlockName(claimApplicationItem.getClaimApplicationItemLundinDetails()
						.iterator().next().getLundinBlock().getBlockName());
				claimApplicationItemForm.setAfeName(claimApplicationItem.getClaimApplicationItemLundinDetails()
						.iterator().next().getLundinAFE().getAfeName());
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
			
			claimApplicationItemForm.setLundinTimesheetModule(UserContext.isLundinTimesheetModule());

			claimApplicationItemForms.add(claimApplicationItemForm);

		}

		Set<ClaimApplicationWorkflow> claimApplicationWorkflows = claimApplication.getClaimApplicationWorkflows();
		HashMap<Long, ClaimApplicationWorkflow> workFlow = new HashMap<>();
		for (ClaimApplicationWorkflow claimApplicationWorkflow : claimApplicationWorkflows) {
			if (claimApplicationWorkflow.getEmployee().getEmployeeId() == claimApplication.getEmployee()
					.getEmployeeId()) {

				claimAppSubmittedDate = claimApplicationWorkflow.getCreatedDate();

			}
			workFlow.put(claimApplicationWorkflow.getEmployee().getEmployeeId(), claimApplicationWorkflow);

		}

		Set<ClaimApplicationReviewer> claimApplicationReviewerVOs = claimApplication.getClaimApplicationReviewers();
		List<ClaimApplicationWorkflowForm> claimWorkflows = new ArrayList<>();

		byte[] empImg = null; 
		ClaimApplicationWorkflowForm appClaimWorkFlowForm = new ClaimApplicationWorkflowForm();

		appClaimWorkFlowForm.setRemarks(claimApplication.getRemarks());

		appClaimWorkFlowForm.setStatusName(claimApplication.getClaimStatusMaster().getClaimStatusName());
		if (StringUtils.isNotBlank(claimApplication.getClaimStatusMaster().getLabelKey())) {
			appClaimWorkFlowForm.setStatusNameLocale(messageSource
					.getMessage(claimApplication.getClaimStatusMaster().getLabelKey(), new Object[] {}, locale));

		} else {
			appClaimWorkFlowForm.setStatusNameLocale(claimApplication.getClaimStatusMaster().getClaimStatusName());
		}

		appClaimWorkFlowForm.setCreatedDate(
				claimAppSubmittedDate == null ? DateUtils.timeStampToString(claimApplication.getCreatedDate())
						: DateUtils.timeStampToString(claimAppSubmittedDate));

		appClaimWorkFlowForm.setWorkflowRule(messageSource.getMessage(user, new Object[] {}, locale));
		appClaimWorkFlowForm.setEmpName(getEmployeeName(claimApplication.getEmployee()));
		
		try {
			empImg = employeeDetailLogic.getEmployeeImage(claimApplication.getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		appClaimWorkFlowForm.setEmpImage(empImg);
		
		
		claimWorkflows.add(appClaimWorkFlowForm);
		List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(claimApplicationReviewerVOs);
		Collections.sort(claimApplicationReviewers, new ClaimTypeComp());
		Integer revCount = 1;
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			ClaimApplicationWorkflowForm claimApplicationWorkflowForm = new ClaimApplicationWorkflowForm();
			ClaimApplicationWorkflow claimApplicationWorkflow = workFlow
					.get(claimApplicationReviewer.getEmployee().getEmployeeId());

			if (claimApplicationWorkflow != null) {
				claimApplicationWorkflowForm.setRemarks(claimApplicationWorkflow.getRemarks());
				claimApplicationWorkflowForm
						.setStatusName(claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName());
				if (StringUtils.isNotBlank(claimApplicationWorkflow.getClaimStatusMaster().getLabelKey())) {
					claimApplicationWorkflowForm.setStatusNameLocale(messageSource.getMessage(
							claimApplicationWorkflow.getClaimStatusMaster().getLabelKey(), new Object[] {}, locale));

				} else {
					claimApplicationWorkflowForm
							.setStatusNameLocale(claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName());
				}
				claimApplicationWorkflowForm
						.setCreatedDate(DateUtils.timeStampToStringWithTime(claimApplicationWorkflow.getCreatedDate()));
			}
			if (StringUtils.isNotBlank(claimApplicationReviewer.getWorkFlowRuleMaster().getLabelKey())) {
				claimApplicationWorkflowForm.setWorkflowRule(
						messageSource.getMessage(claimApplicationReviewer.getWorkFlowRuleMaster().getLabelKey(),
								new Object[] {}, locale) + revCount);
			} else {
				claimApplicationWorkflowForm
						.setWorkflowRule(claimApplicationReviewer.getWorkFlowRuleMaster().getRuleName() + revCount);
			}

			claimApplicationWorkflowForm.setEmpName(getEmployeeName(claimApplicationReviewer.getEmployee()));
			
			try {
				empImg = employeeDetailLogic.getEmployeeImage(claimApplicationReviewer.getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			claimApplicationWorkflowForm.setEmpImage(empImg);
			
			claimWorkflows.add(claimApplicationWorkflowForm);
			revCount++;

		}

		ClaimPreferenceForm claimPreference = getClaimDateAndItemSortOrder(
				claimApplication.getCompany().getCompanyId());

		if ((StringUtils.isNotBlank(claimPreference.getClaimItemNameSortOrder()))
				&& (StringUtils.isNotBlank(claimPreference.getClaimItemDateSortOrder()))) {
			Collections.sort(claimApplicationItemForms, new ClaimItemCompAndClaimDateComp(
					claimPreference.getClaimItemDateSortOrder(), claimPreference.getClaimItemDateSortOrder()));

		}
		claimApplicationForm.setClaimWorkflows(claimWorkflows);
		Collections.reverse(claimApplicationItemForms);
		claimApplicationForm.setClaimapplicationItems(claimApplicationItemForms);

	}

	/**
	 * Comparator Class for Ordering ClaimApplicationReviewer List
	 */
	private class ClaimTypeComp implements Comparator<ClaimApplicationReviewer> {
		public int compare(ClaimApplicationReviewer templateField, ClaimApplicationReviewer compWithTemplateField) {
			if (templateField.getClaimApplicationReviewerId() > compWithTemplateField.getClaimApplicationReviewerId()) {
				return 1;
			} else if (templateField.getClaimApplicationReviewerId() < compWithTemplateField
					.getClaimApplicationReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering ClaimApplicationReviewer List
	 */
	private class ClaimItemTypeComp implements Comparator<ClaimApplicationItem> {
		public int compare(ClaimApplicationItem templateField, ClaimApplicationItem compWithTemplateField) {
			if (templateField.getClaimApplicationItemId() > compWithTemplateField.getClaimApplicationItemId()) {
				return 1;
			} else if (templateField.getClaimApplicationItemId() < compWithTemplateField.getClaimApplicationItemId()) {
				return -1;
			}
			return 0;

		}

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

			if (sortByDate.equals(PayAsiaConstants.ASC) && (sortItem.equals(PayAsiaConstants.ASC))) {
				if (date.before(date1)) {
					comparableVariable = 1;
				} else if (date.after(date1)) {
					comparableVariable = -1;
				} else {
					comparableVariable = compWithTemplateField.getClaimApplicationClaimItemName()
							.compareTo(templateField.getClaimApplicationClaimItemName());
				}
			} else if ((sortByDate.equals(PayAsiaConstants.DESC) && (sortItem.equals(PayAsiaConstants.DESC)))) {
				if (date.before(date1)) {
					comparableVariable = -1;
				} else if (date.after(date1)) {
					comparableVariable = 1;
				} else {
					comparableVariable = templateField.getClaimApplicationClaimItemName()
							.compareTo(compWithTemplateField.getClaimApplicationClaimItemName());
				}
			} else if ((sortByDate.equals(PayAsiaConstants.ASC) && (sortItem.equals(PayAsiaConstants.DESC)))) {
				if (date.before(date1)) {
					comparableVariable = 1;
				} else if (date.after(date1)) {
					comparableVariable = -1;
				} else {
					comparableVariable = templateField.getClaimApplicationClaimItemName()
							.compareTo(compWithTemplateField.getClaimApplicationClaimItemName());
				}
			} else if ((sortByDate.equals(PayAsiaConstants.DESC) && (sortItem.equals(PayAsiaConstants.ASC)))) {
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
	public ClaimApplicationItemForm saveOverrideItemInfo(AddClaimDTO addClaimDTO,
			ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm) {
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();

			ClaimApplicationItem claimApplicationItemVO = claimApplicationItemDAO.findByClaimApplicationItemIdReview(addClaimDTO);
			ClaimApplicationItem claimApplicationItemValidate = new ClaimApplicationItem();
			 claimApplicationItemValidate.setAmountBeforeTax(claimApplicationItemVO.getAmountBeforeTax());
			 claimApplicationItemValidate.setApplicableClaimAmount(claimApplicationItemVO.getApplicableClaimAmount());
			 claimApplicationItemValidate.setClaimAmount(claimApplicationItemVO.getClaimAmount());
			 claimApplicationItemValidate.setTaxAmount(claimApplicationItemVO.getTaxAmount());
			 claimApplicationItemValidate.setEmployeeClaimTemplateItem(claimApplicationItemVO.getEmployeeClaimTemplateItem());
			 claimApplicationItemValidate.setClaimApplication(claimApplicationItemVO.getClaimApplication());
			 claimApplicationItemValidate.setClaimApplicationItemId(claimApplicationItemVO.getClaimApplicationItemId());
			 claimApplicationItemValidate.setCompanyId(claimApplicationItemVO.getCompanyId());
			 claimApplicationItemValidate.setReceiptNumber(claimApplicationItemVO.getReceiptNumber());
			 claimApplicationItemValidate.setClaimDate(claimApplicationItemVO.getClaimDate());
			 claimApplicationItemValidate.setRemarks(claimApplicationItemVO.getRemarks());
			 claimApplicationItemValidate.setForexReceiptAmount(claimApplicationItemVO.getForexReceiptAmount());
			 claimApplicationItemValidate.setUnitPrice(claimApplicationItemVO.getUnitPrice());
			 claimApplicationItemValidate.setQuantity(claimApplicationItemVO.getQuantity());
			 claimApplicationItemValidate.setExchangeRate(claimApplicationItemVO.getExchangeRate());
				
				setClaimItemDataOnOverride(claimApplicationItemWorkflowForm ,claimApplicationItemValidate);
				
				  ValidationClaimItemDTO validateClaimItemDTO = claimApplicationItemDAO.validateClaimItem(claimApplicationItemValidate, false, true); 
				  if (validateClaimItemDTO.getErrorCode() == 1) {
					  claimApplicationItemForm.setValidationClaimItemDTO(validateClaimItemDTO); 
					  return claimApplicationItemForm;
				  }
				 
			Employee loggedInEmployee = employeeDAO.findById(addClaimDTO.getEmployeeId());

			ClaimApplicationItemWorkflow claimApplicationItemWorkflow = new ClaimApplicationItemWorkflow();
			claimApplicationItemWorkflow.setClaimApplicationItem(claimApplicationItemVO);
			AppCodeMaster pendingClaimAction = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_ACTION,
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_ACTION_OVERRIDE);
			claimApplicationItemWorkflow.setClaimItemWorkflowAction(pendingClaimAction);

			AppCodeMaster pendingClaimStatus = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS,
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS_OVERRIDDEN);
			claimApplicationItemWorkflow.setClaimItemWorkflowStatus(pendingClaimStatus);

			claimApplicationItemWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
			claimApplicationItemWorkflow.setCreatedBy(loggedInEmployee);
			claimApplicationItemWorkflow.setOverriddenAmount(claimApplicationItemVO.getClaimAmount());
			claimApplicationItemWorkflow.setOverriddenTaxAmount(claimApplicationItemVO.getTaxAmount());

			claimApplicationItemWorkflow.setRemarks(claimApplicationItemWorkflowForm.getRemarks());
			
			claimApplicationItemWorkflowDAO.save(claimApplicationItemWorkflow);

			ClaimApplication claimApplicationVO = claimApplicationItemVO.getClaimApplication();
			BigDecimal claimAppAmount = claimApplicationItemVO.getClaimAmount()
					.subtract(claimApplicationItemWorkflowForm.getOverriddenAmount());
			claimApplicationVO.setTotalAmount(claimApplicationVO.getTotalAmount().subtract(claimAppAmount));
			claimApplicationDAO.update(claimApplicationVO);

			setClaimItemDataOnOverride(claimApplicationItemWorkflowForm ,claimApplicationItemVO);

			claimApplicationItemDAO.update(claimApplicationItemVO);
			updateClaimApplication(claimApplicationVO);
		
		return claimApplicationItemForm;
	}

	@Override
	public ClaimApplicationItemForm saveRejectItemInfo(AddClaimDTO addClaimDTO,
			ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm) {

		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		try {
			ClaimApplicationItem claimApplicationItemVO = claimApplicationItemDAO.findByClaimApplicationItemIdReview(addClaimDTO);
			
			if(claimApplicationItemVO==null){
				return null;
			}
			
			Employee loggedInEmployee = employeeDAO.findById(addClaimDTO.getEmployeeId());

			claimApplicationItemForm.setClaimApplicationItemID(claimApplicationItemVO.getClaimApplicationItemId());
			ClaimApplicationItemWorkflow claimApplicationItemWorkflow = new ClaimApplicationItemWorkflow();
			claimApplicationItemWorkflow.setClaimApplicationItem(claimApplicationItemVO);
			AppCodeMaster pendingClaimAction = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_ACTION,
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_ACTION_REJECT);
			claimApplicationItemWorkflow.setClaimItemWorkflowAction(pendingClaimAction);
			AppCodeMaster pendingClaimStatus;
			if (claimApplicationItemWorkflowForm.getStatusMode()
					.equals(PayAsiaConstants.APP_CODE_ClAIM_ITEM_REJECT_MODE_REJECT)) {
				pendingClaimStatus = appCodeMasterDAO.findByCondition(
						PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS,
						PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS_REJECTED);
			} else {
				pendingClaimStatus = appCodeMasterDAO.findByCondition(
						PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS,
						PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS_UNDO_REJECTED);
			}

			claimApplicationItemWorkflow.setClaimItemWorkflowStatus(pendingClaimStatus);
			claimApplicationItemWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
			claimApplicationItemWorkflow.setCreatedBy(loggedInEmployee);
/*
			if (StringUtils.isNotBlank(claimApplicationItemWorkflowForm.getRemarks())) {
				try {
					claimApplicationItemWorkflow
							.setRemarks(URLDecoder.decode(claimApplicationItemWorkflowForm.getRemarks(), "UTF-8"));
				} catch (UnsupportedEncodingException exception) {
					claimApplicationItemWorkflow.setRemarks("");
					LOGGER.error(exception.getMessage(), exception);
				}
			}
*/
			claimApplicationItemWorkflow.setRemarks(claimApplicationItemWorkflowForm.getRemarks());
			
			claimApplicationItemWorkflowDAO.save(claimApplicationItemWorkflow);

			ClaimApplication claimApplicationVO = claimApplicationItemVO.getClaimApplication();
			if (claimApplicationItemWorkflowForm.getStatusMode()
					.equals(PayAsiaConstants.APP_CODE_ClAIM_ITEM_REJECT_MODE_REJECT)) {
				claimApplicationVO.setTotalAmount(
						claimApplicationVO.getTotalAmount().subtract(claimApplicationItemVO.getClaimAmount()));
				claimApplicationItemForm.setRejectStatus(true);

				claimApplicationItemVO.setActive(false);
				claimApplicationItemDAO.update(claimApplicationItemVO);

			} else {
				claimApplicationVO.setTotalAmount(
						claimApplicationVO.getTotalAmount().add(claimApplicationItemVO.getClaimAmount()));
				claimApplicationItemForm.setRejectStatus(false);
				claimApplicationItemVO.setActive(true);
				claimApplicationItemDAO.update(claimApplicationItemVO);
			}

			claimApplicationDAO.update(claimApplicationVO);
		}

		catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return claimApplicationItemForm;
	}

	@Override
	public PendingClaimsForm forwardClaim(PendingClaimsForm pendingClaimsForm, AddClaimDTO addClaimDTO) {
		PendingClaimsForm response = new PendingClaimsForm();
		Boolean isSuccessfullyFor = false;
		ClaimApplication claimApplicationVO = null;
		String reviewerRemarks = "";
		ClaimApplicationReviewer claimApplicationReviewer2 = null;
		ClaimApplicationWorkflow claimApplicationWorkflow = null;

		Date date = new Date();
		ClaimApplicationReviewer claimApplicationReviewer = claimApplicationReviewerDAO
				.getClaimApplicationReviewerDetail(FormatPreserveCryptoUtil.decrypt(pendingClaimsForm.getClaimApplicationReviewerId()),
						addClaimDTO.getEmployeeId());
		String workflowLevel = null;
		if(claimApplicationReviewer!=null){
			workflowLevel = String.valueOf(claimApplicationReviewerDAO
					.getClaimReviewerCount(claimApplicationReviewer.getClaimApplication().getClaimApplicationId()));
		}
		else{
			claimApplicationReviewer= new ClaimApplicationReviewer();
		}
		if (workflowLevel != null
				&& claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equalsIgnoreCase(workflowLevel)) {
			response = acceptClaim(pendingClaimsForm, addClaimDTO.getEmployeeId(), addClaimDTO.getCompanyId());
		} else {
			try {
				ClaimApplicationWorkflow applicationWorkflow = new ClaimApplicationWorkflow();
				Employee employee = employeeDAO.findById(addClaimDTO.getEmployeeId());

				for (ClaimTemplateWorkflow claimTemplateWorkflow : claimApplicationReviewer.getClaimApplication()
						.getEmployeeClaimTemplate().getClaimTemplate().getClaimTemplateWorkflows()) {
					if (claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE__DEF_WORKFLOW_LEVEL)) {
						workflowLevel = claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue();

					}
				}
				ClaimStatusMaster claimStatusMaster = claimStatusMasterDAO
						.findByCondition(PayAsiaConstants.CLAIM_STATUS_APPROVED);

				claimApplicationVO = claimApplicationReviewer.getClaimApplication();

				// Validating claim application item.
				Boolean isAdmin = false;
				Boolean isManager = true;
				for (ClaimApplicationItem claimApplicationItem : claimApplicationVO.getClaimApplicationItems()) {
					ValidationClaimItemDTO validateClaimItemDTO = claimApplicationItemDAO
							.validateClaimItem(claimApplicationItem, isAdmin, isManager);
					if (validateClaimItemDTO.getErrorCode() == 1) {
						response.setValidationClaimItemDTO(validateClaimItemDTO);
						response.setMessageCode("500");
						return response;

					}
				}

				ClaimStatusMaster claimStatusCompleted = null;
				if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equalsIgnoreCase(workflowLevel)) {
					claimStatusCompleted = claimStatusMasterDAO
							.findByCondition(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
					claimApplicationVO.setClaimStatusMaster(claimStatusCompleted);
				} else {

					claimApplicationReviewer2 = claimApplicationReviewerDAO
							.findByID(claimApplicationReviewer.getClaimApplicationReviewerId() + 1);
					claimApplicationReviewer2.setPending(true);
					applicationWorkflow.setForwardTo(claimApplicationReviewer2.getEmployee().getEmail());

					claimApplicationReviewerDAO.update(claimApplicationReviewer2);
				}
				claimApplicationVO.setUpdatedDate(new Timestamp(date.getTime()));
				claimApplicationDAO.update(claimApplicationVO);

				claimApplicationReviewer.setPending(false);
				claimApplicationReviewer.setEmployee(employee);
				claimApplicationReviewerDAO.update(claimApplicationReviewer);

				applicationWorkflow.setEmployee(employee);
				applicationWorkflow.setClaimApplication(claimApplicationReviewer.getClaimApplication());

				String emailCC = StringUtils.removeEnd(pendingClaimsForm.getEmailCC(), ";");
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
				EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(
						claimApplicationVO.getCompany().getCompanyId(),
						claimApplicationVO.getEmployee().getEmployeeId(), moduleMaster.getModuleId());
				if (employeeDefaultEmailCCVO != null) {
					if (StringUtils.isNotBlank(emailCC)) {
						emailCC += ";";
					}
					emailCC += employeeDefaultEmailCCVO.getEmailCC();
				}
				applicationWorkflow.setEmailCC(emailCC);
				applicationWorkflow.setClaimStatusMaster(claimStatusMaster);
				applicationWorkflow.setTotalAmount(claimApplicationVO.getTotalAmount());
/*				
				if (StringUtils.isNotBlank(pendingClaimsForm.getRemarks())) {
					try {
						reviewerRemarks = URLDecoder.decode(pendingClaimsForm.getRemarks(), "UTF-8");
					} catch (UnsupportedEncodingException exception) {
						reviewerRemarks = "";
						LOGGER.error(exception.getMessage(), exception);
					}
				}
*/
				reviewerRemarks = pendingClaimsForm.getRemarks();
				applicationWorkflow.setRemarks(reviewerRemarks);
				applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
				claimApplicationWorkflow = claimApplicationWorkflowDAO.saveReturn(applicationWorkflow);
				isSuccessfullyFor = true;
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
			}
			if (isSuccessfullyFor) {

				response.setMessageCode("200");
				ClaimMailDTO claimMailDTORev = new ClaimMailDTO();
				claimMailDTORev.setClaimApplication(claimApplicationVO);
				claimMailDTORev.setLoggedInCmpId(addClaimDTO.getCompanyId());
				claimMailDTORev.setLoggedInEmpId(addClaimDTO.getEmployeeId());
				claimMailDTORev.setReviewerRemarks(reviewerRemarks);
				claimMailDTORev.setSubcategoryName(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_FORWARD_REVIEWER);
				claimMailDTORev.setClaimApplicationWorkflow(claimApplicationWorkflow);
				claimMailDTORev.setAttachmentRequired(true);
				claimMailLogic.sendClaimMail(claimMailDTORev);

				ClaimMailDTO claimMailDTOEmp = new ClaimMailDTO();
				claimMailDTOEmp.setClaimApplication(claimApplicationVO);
				claimMailDTOEmp.setLoggedInCmpId(addClaimDTO.getCompanyId());
				claimMailDTOEmp.setLoggedInEmpId(addClaimDTO.getEmployeeId());
				claimMailDTOEmp.setReviewerRemarks(reviewerRemarks);
				claimMailDTOEmp.setSubcategoryName(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_FORWARD_EMPLOYEE);
				claimMailLogic.sendClaimMail(claimMailDTOEmp);

				NotificationAlert notificationAlert = new NotificationAlert();
				notificationAlert.setEmployee(claimApplicationReviewer2.getEmployee());
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);

				notificationAlert.setModuleMaster(moduleMaster);
				notificationAlert.setMessage(getEmployeeName(claimApplicationVO.getEmployee()) + " "
						+ PayAsiaConstants.CLAIMAPPLICATION_NOTIFICATION_ALERT);
				notificationAlert.setShownStatus(false);
				notificationAlertDAO.save(notificationAlert);

			}
		}

		return response;

	}

	@Override
	public PendingClaimsForm acceptClaim(PendingClaimsForm pendingClaimsForm, Long employeeId, Long companyId) {
		Boolean isSuccessfullyAcc = false;
		PendingClaimsForm response = new PendingClaimsForm();
		ClaimApplication claimApplicationVO = null;
		String reviewerRemarks = "";
		ClaimApplicationWorkflow claimApplicationWorkflow = null;
		try {
			Date date = new Date();
			ClaimApplicationReviewer claimApplicationReviewer = claimApplicationReviewerDAO
					.getClaimApplicationReviewerDetail(FormatPreserveCryptoUtil.decrypt(pendingClaimsForm.getClaimApplicationReviewerId()), employeeId);
			ClaimApplicationWorkflow applicationWorkflow = new ClaimApplicationWorkflow();
			Employee employee = employeeDAO.findById(employeeId);

			claimApplicationVO = claimApplicationReviewer.getClaimApplication();

			List<String> claimApprovedStatusList = new ArrayList<>();
			claimApprovedStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
			if (claimApprovedStatusList.contains(claimApplicationVO.getClaimStatusMaster().getClaimStatusName())) {
				response.setStatus(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				response.setMessageCode("201");
				return response;
			}

			// Validating claim application item.
			Boolean isAdmin = false;
			Boolean isManager = true;
			for (ClaimApplicationItem claimApplicationItem : claimApplicationVO.getClaimApplicationItems()) {
				ValidationClaimItemDTO validateClaimItemDTO = claimApplicationItemDAO
						.validateClaimItem(claimApplicationItem, isAdmin, isManager);
				if (validateClaimItemDTO.getErrorCode() == 1) {
					response.setValidationClaimItemDTO(validateClaimItemDTO);
					response.setMessageCode("500");
					return response;

				}
			}

			ClaimStatusMaster claimStatusCompleted = null;

			claimStatusCompleted = claimStatusMasterDAO.findByCondition(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
			claimApplicationVO.setClaimStatusMaster(claimStatusCompleted);

			claimApplicationVO.setUpdatedDate(new Timestamp(date.getTime()));
			claimApplicationDAO.update(claimApplicationVO);

			claimApplicationReviewer.setPending(false);
			claimApplicationReviewer.setEmployee(employee);
			claimApplicationReviewerDAO.update(claimApplicationReviewer);

			applicationWorkflow.setEmployee(employee);
			applicationWorkflow.setClaimApplication(claimApplicationReviewer.getClaimApplication());

			String emailCC = StringUtils.removeEnd(pendingClaimsForm.getEmailCC(), ";");
			ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(
					claimApplicationVO.getCompany().getCompanyId(), claimApplicationVO.getEmployee().getEmployeeId(),
					moduleMaster.getModuleId());
			if (employeeDefaultEmailCCVO != null) {
				if (StringUtils.isNotBlank(emailCC)) {
					emailCC += ";";
				}
				emailCC += employeeDefaultEmailCCVO.getEmailCC();
			}
			applicationWorkflow.setEmailCC(emailCC);
			applicationWorkflow.setClaimStatusMaster(claimStatusCompleted);
			applicationWorkflow.setTotalAmount(claimApplicationVO.getTotalAmount());
/*			
			if (StringUtils.isNotBlank(pendingClaimsForm.getRemarks())) {
				try {
					reviewerRemarks = URLDecoder.decode(pendingClaimsForm.getRemarks(), "UTF-8");
				} catch (UnsupportedEncodingException exception) {
					reviewerRemarks = "";
					LOGGER.error(exception.getMessage(), exception);
				}
			}
*/
			reviewerRemarks = pendingClaimsForm.getRemarks();
			applicationWorkflow.setRemarks(reviewerRemarks);

			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			claimApplicationWorkflow = claimApplicationWorkflowDAO.saveReturn(applicationWorkflow);
			isSuccessfullyAcc = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		if (isSuccessfullyAcc) {

			response.setMessageCode("200");
			ClaimMailDTO claimMailDTO = new ClaimMailDTO();
			claimMailDTO.setClaimApplication(claimApplicationVO);
			claimMailDTO.setLoggedInCmpId(companyId);
			claimMailDTO.setLoggedInEmpId(employeeId);
			claimMailDTO.setReviewerRemarks(reviewerRemarks);
			claimMailDTO.setSubcategoryName(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_ACCEPTED);
			claimMailDTO.setClaimApplicationWorkflow(claimApplicationWorkflow);
			claimMailLogic.sendClaimMail(claimMailDTO);
		}

		return response;

	}

	@Override
	public PendingClaimsForm rejectClaim(PendingClaimsForm pendingClaimsForm, Long employeeId, Long companyId) {
		PendingClaimsForm response = new PendingClaimsForm();
		Boolean isSuccessRejeted = false;
		ClaimApplication claimApplicationVO = null;
		String reviewerRemarks = "";
		ClaimApplicationWorkflow workFlow = null;
		try {
			Date date = new Date();
			ClaimApplicationReviewer claimApplicationReviewer = claimApplicationReviewerDAO
					.getClaimApplicationReviewerDetail(FormatPreserveCryptoUtil.decrypt(pendingClaimsForm.getClaimApplicationReviewerId()), employeeId);
			ClaimApplicationWorkflow applicationWorkflow = new ClaimApplicationWorkflow();
			Employee employee = employeeDAO.findById(employeeId);
			ClaimStatusMaster claimStatusMaster = claimStatusMasterDAO
					.findByCondition(PayAsiaConstants.CLAIM_STATUS_REJECTED);
			claimApplicationVO = claimApplicationReviewer.getClaimApplication();

			List<String> claimApprovedStatusList = new ArrayList<>();
			claimApprovedStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
			if (claimApprovedStatusList.contains(claimApplicationVO.getClaimStatusMaster().getClaimStatusName())) {
				response.setStatus(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				response.setMessageCode("201");
				return response;
			}
			claimApplicationVO.setClaimStatusMaster(claimStatusMaster);
			claimApplicationVO.setUpdatedDate(new Timestamp(date.getTime()));
			claimApplicationDAO.update(claimApplicationVO);

			Set<ClaimApplicationItem> claimApplicationItems = claimApplicationReviewer.getClaimApplication()
					.getClaimApplicationItems();
			for (ClaimApplicationItem claimApplicationItem : claimApplicationItems) {
				ClaimApplicationItem applicationItem = claimApplicationItem;
				applicationItem.setActive(false);
				claimApplicationItemDAO.update(applicationItem);
			}

			for (ClaimApplicationReviewer applicationReviewer : claimApplicationVO.getClaimApplicationReviewers()) {
				applicationReviewer.setPending(false);
				claimApplicationReviewerDAO.update(applicationReviewer);
			}

			applicationWorkflow.setEmployee(employee);
			applicationWorkflow.setClaimApplication(claimApplicationReviewer.getClaimApplication());
			applicationWorkflow.setClaimStatusMaster(claimStatusMaster);

			String emailCC = StringUtils.removeEnd(pendingClaimsForm.getEmailCC(), ";");
			ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.COMPANY_MODULE_CLAIM);
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(
					claimApplicationVO.getCompany().getCompanyId(), claimApplicationVO.getEmployee().getEmployeeId(),
					moduleMaster.getModuleId());
			if (employeeDefaultEmailCCVO != null) {
				if (StringUtils.isNotBlank(emailCC)) {
					emailCC += ";";
				}
				emailCC += employeeDefaultEmailCCVO.getEmailCC();
			}
			applicationWorkflow.setEmailCC(emailCC);
/*			
			if (StringUtils.isNotBlank(pendingClaimsForm.getRemarks())) {
				try {
					reviewerRemarks = URLDecoder.decode(pendingClaimsForm.getRemarks(), "UTF-8");
				} catch (UnsupportedEncodingException exception) {
					reviewerRemarks = "";
					LOGGER.error(exception.getMessage(), exception);
				}
			}
*/
			reviewerRemarks = pendingClaimsForm.getRemarks();
			applicationWorkflow.setRemarks(reviewerRemarks);
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			workFlow = claimApplicationWorkflowDAO.saveReturn(applicationWorkflow);
			isSuccessRejeted = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		if (isSuccessRejeted) {

			response.setMessageCode("200");
			ClaimMailDTO claimMailDTO = new ClaimMailDTO();
			claimMailDTO.setClaimApplication(claimApplicationVO);
			claimMailDTO.setLoggedInCmpId(companyId);
			claimMailDTO.setLoggedInEmpId(employeeId);
			claimMailDTO.setReviewerRemarks(reviewerRemarks);
			claimMailDTO.setSubcategoryName(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_REJECTED);
			claimMailDTO.setClaimApplicationWorkflow(workFlow);
			claimMailLogic.sendClaimMail(claimMailDTO);
		}
		return response;
	}

	@Override
	public ClaimFormPdfDTO generateClaimFormPrintPDF(Long companyId, Long employeeId, Long claimApplicationReviewerId,
			boolean hasLundinTimesheetModule) {

		ClaimFormPdfDTO claimFormPdfDTO = new ClaimFormPdfDTO();
		ClaimApplicationReviewer claimApplicationReviewerVO = claimApplicationReviewerDAO
				.getClaimApplicationReviewerDetail(claimApplicationReviewerId, employeeId);
		
		if(claimApplicationReviewerVO==null){
			return null;
		}
		claimFormPdfDTO.setClaimTemplateName(claimApplicationReviewerVO.getClaimApplication().getEmployeeClaimTemplate()
				.getClaimTemplate().getTemplateName());
		claimFormPdfDTO
				.setEmployeeNumber(claimApplicationReviewerVO.getClaimApplication().getEmployee().getEmployeeNumber());
		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				claimFormPdfDTO.setClaimFormPdfByteFile(generateClaimFormPDF(companyId, employeeId,
						claimApplicationReviewerVO.getClaimApplication().getClaimApplicationId(),
						hasLundinTimesheetModule));
				return claimFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			try {
				claimFormPdfDTO.setClaimFormPdfByteFile(generateClaimFormPDF(companyId, employeeId,
						claimApplicationReviewerVO.getClaimApplication().getClaimApplicationId(),
						hasLundinTimesheetModule));
				return claimFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

	}

	private byte[] generateClaimFormPDF(Long companyId, Long employeeId, Long claimApplicationId,
			boolean hasLundinTimesheetModule) throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId, PAYASIA_TEMP_PATH, "ClaimForm");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PayAsiaPDFConstants.PAGE_SIZE, PayAsiaPDFConstants.PAGE_LEFT_MARGIN,
					PayAsiaPDFConstants.PAGE_TOP_MARGIN, PayAsiaPDFConstants.PAGE_RIGHT_MARGIN,
					PayAsiaPDFConstants.PAGE_BOTTOM_MARGIN);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

			document.open();

			PdfPTable claimReportPdfTable = claimFormPrintPDFLogic.createClaimReportPdf(document, writer, 1, companyId,
					claimApplicationId, hasLundinTimesheetModule);

			document.add(claimReportPdfTable);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow | PdfWriter.CenterWindow);

			document.close();

			pdfIn = new FileInputStream(tempFile);

			return IOUtils.toByteArray(pdfIn);
		} catch (DocumentException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					throw new PayAsiaSystemException(ex.getMessage(), ex);
				}
			}

			IOUtils.closeQuietly(pdfOut);
			IOUtils.closeQuietly(pdfIn);

			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new PayAsiaSystemException(ex.getMessage(), ex);
			}

		}
	}

	@Override
	public ClaimApplicationItemAttach viewAttachment(AddClaimDTO addClaimDTO) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		ClaimApplicationItemAttachment claimApplicationItemAttachment = claimApplicationItemAttachmentDAO
				.findByID(addClaimDTO.getClaimApplicationItemAttachmentId());
		
		if(claimApplicationItemAttachment==null){
			return null;
		}
		
		byte[] byteFile = null;
		String fileExt = claimApplicationItemAttachment.getFileType();

		filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				claimApplicationItemAttachment.getClaimApplicationItem().getClaimApplication().getCompany()
						.getCompanyId(),
				PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME,
				String.valueOf(claimApplicationItemAttachment.getClaimApplicationItemAttachmentId()), null, null,
				fileExt, PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);

		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		File file = new File(filePath);
		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				byteFile = org.apache.commons.io.IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
			} else {
				byteFile = Files.readAllBytes(file.toPath());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		claimApplicationItemAttach.setAttachmentBytes(byteFile);
		claimApplicationItemAttach.setFileName(claimApplicationItemAttachment.getFileName());
		claimApplicationItemAttach.setFileType(claimApplicationItemAttachment.getFileType());
		return claimApplicationItemAttach;
	}
	
	private void setClaimItemDataOnOverride(ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm,ClaimApplicationItem claimApplicationItem)
	{
		claimApplicationItem.setClaimAmount(claimApplicationItemWorkflowForm.getOverriddenAmount());
		claimApplicationItem.setAmountBeforeTax(claimApplicationItemWorkflowForm.getAmountBeforeTax());
		claimApplicationItem.setTaxAmount(claimApplicationItemWorkflowForm.getOverriddenTaxAmount());

		ClaimTemplateItem claimTemplateItem = claimApplicationItem.getEmployeeClaimTemplateItem()
				.getClaimTemplateItem();
		if (claimTemplateItem.getClaimTemplateItemClaimTypes().size() > 0) {
			ClaimTemplateItemClaimType claimTemplateItemClaimType = claimTemplateItem
					.getClaimTemplateItemClaimTypes().iterator().next();
			if (claimTemplateItemClaimType.getClaimType().getCodeDesc()
					.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED)
					|| claimTemplateItemClaimType.getClaimType().getCodeDesc()
							.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED) || claimTemplateItemClaimType.getClaimType().getCodeDesc()
							.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_QUANTITY_BASED)) {
				Integer amtApplicablePercent = claimTemplateItemClaimType.getReceiptAmtPercentApplicable();
				if (amtApplicablePercent == null || amtApplicablePercent == 0) {
					claimApplicationItem
							.setApplicableClaimAmount(claimApplicationItemWorkflowForm.getOverriddenAmount());
				} else {
					claimApplicationItem
							.setApplicableClaimAmount(claimApplicationItemWorkflowForm.getOverriddenAmount()
									.multiply(new BigDecimal(amtApplicablePercent)).divide(new BigDecimal(100)));
				}
				
				if (claimTemplateItemClaimType.getClaimType().getCodeDesc().equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED))
				{
					claimApplicationItem.setClaimAmount(claimApplicationItemWorkflowForm.getOverriddenAmount().multiply(claimApplicationItem.getExchangeRate()));
					claimApplicationItem.setForexReceiptAmount(claimApplicationItemWorkflowForm.getOverriddenAmount());
					claimApplicationItem.setApplicableClaimAmount(claimApplicationItemWorkflowForm.getOverriddenAmount().multiply(claimApplicationItem.getExchangeRate()));
				}
				
				else if (claimTemplateItemClaimType.getClaimType().getCodeDesc().equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_QUANTITY_BASED))
				{
					claimApplicationItem.setUnitPrice(claimApplicationItem.getClaimAmount().divide(new BigDecimal(claimApplicationItem.getQuantity()), 4, RoundingMode.HALF_DOWN));
				}
			}
		}	
	}
	private void updateClaimApplication(ClaimApplication claimApplicationVO) {

		Integer noOfItems = claimApplicationVO.getClaimApplicationItems().size();
		claimApplicationVO.setTotalItems(noOfItems);
		BigDecimal totalAmount = new BigDecimal(0);
		for (ClaimApplicationItem existingClaimApplicationItem : claimApplicationVO.getClaimApplicationItems()) {
			if(existingClaimApplicationItem.getApplicableClaimAmount().equals(null)|| (existingClaimApplicationItem.getApplicableClaimAmount().compareTo(BigDecimal.ZERO)==0))
			{
			totalAmount = totalAmount.add(existingClaimApplicationItem.getClaimAmount());
			}
			
			else
			{
				totalAmount = totalAmount.add(existingClaimApplicationItem.getApplicableClaimAmount());
			}
		}
		claimApplicationVO.setTotalAmount(totalAmount);
		claimApplicationDAO.update(claimApplicationVO);
	}
	
	/*
	 * NEW METHOD ADDED FOR OVERRIDING CLAIM ITEM : DISCARD
	 */
	@Override
	public Map<String, Object> saveOverrideClaimItemInfo(AddClaimDTO addClaimDTO, ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm) {
		
		Map<String, Object> saveOverrideMap = new HashMap<>();
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();

			ClaimApplicationItem claimApplicationItemVO = claimApplicationItemDAO.findByClaimApplicationItemIdReview(addClaimDTO);
			ClaimApplicationItem claimApplicationItemValidate = new ClaimApplicationItem();
			 claimApplicationItemValidate.setAmountBeforeTax(claimApplicationItemVO.getAmountBeforeTax());
			 claimApplicationItemValidate.setApplicableClaimAmount(claimApplicationItemVO.getApplicableClaimAmount());
			 claimApplicationItemValidate.setClaimAmount(claimApplicationItemVO.getClaimAmount());
			 claimApplicationItemValidate.setTaxAmount(claimApplicationItemVO.getTaxAmount());
			 claimApplicationItemValidate.setEmployeeClaimTemplateItem(claimApplicationItemVO.getEmployeeClaimTemplateItem());
			 claimApplicationItemValidate.setClaimApplication(claimApplicationItemVO.getClaimApplication());
			 claimApplicationItemValidate.setClaimApplicationItemId(claimApplicationItemVO.getClaimApplicationItemId());
			 claimApplicationItemValidate.setCompanyId(claimApplicationItemVO.getCompanyId());
			 claimApplicationItemValidate.setReceiptNumber(claimApplicationItemVO.getReceiptNumber());
			 claimApplicationItemValidate.setClaimDate(claimApplicationItemVO.getClaimDate());
			 claimApplicationItemValidate.setRemarks(claimApplicationItemVO.getRemarks());
			 claimApplicationItemValidate.setForexReceiptAmount(claimApplicationItemVO.getForexReceiptAmount());
			 claimApplicationItemValidate.setUnitPrice(claimApplicationItemVO.getUnitPrice());
			 claimApplicationItemValidate.setQuantity(claimApplicationItemVO.getQuantity());
			 claimApplicationItemValidate.setExchangeRate(claimApplicationItemVO.getExchangeRate());
				
				setClaimItemDataOnOverride(claimApplicationItemWorkflowForm ,claimApplicationItemValidate);
				
				  ValidationClaimItemDTO validateClaimItemDTO = claimApplicationItemDAO.validateClaimItem(claimApplicationItemValidate, false, true); 
				  if (validateClaimItemDTO.getErrorCode() == 1) {
					  claimApplicationItemForm.setValidationClaimItemDTO(validateClaimItemDTO); 
					  saveOverrideMap.put("code", "404");
					  saveOverrideMap.put("status", validateClaimItemDTO.getErrorKey());
					  return saveOverrideMap;
				  }
				 
			Employee loggedInEmployee = employeeDAO.findById(addClaimDTO.getEmployeeId());

			ClaimApplicationItemWorkflow claimApplicationItemWorkflow = new ClaimApplicationItemWorkflow();
			claimApplicationItemWorkflow.setClaimApplicationItem(claimApplicationItemVO);
			AppCodeMaster pendingClaimAction = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_ACTION,
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_ACTION_OVERRIDE);
			claimApplicationItemWorkflow.setClaimItemWorkflowAction(pendingClaimAction);

			AppCodeMaster pendingClaimStatus = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS,
					PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS_OVERRIDDEN);
			claimApplicationItemWorkflow.setClaimItemWorkflowStatus(pendingClaimStatus);

			claimApplicationItemWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
			claimApplicationItemWorkflow.setCreatedBy(loggedInEmployee);
			claimApplicationItemWorkflow.setOverriddenAmount(claimApplicationItemVO.getClaimAmount());
			claimApplicationItemWorkflow.setOverriddenTaxAmount(claimApplicationItemVO.getTaxAmount());
			if (StringUtils.isNotBlank(claimApplicationItemWorkflowForm.getRemarks())) {
				try {
					claimApplicationItemWorkflow
							.setRemarks(URLDecoder.decode(claimApplicationItemWorkflowForm.getRemarks(), "UTF-8"));
				} catch (UnsupportedEncodingException exception) {
					claimApplicationItemWorkflow.setRemarks("");
					LOGGER.error(exception.getMessage(), exception);
				}
			}

			claimApplicationItemWorkflowDAO.save(claimApplicationItemWorkflow);

			ClaimApplication claimApplicationVO = claimApplicationItemVO.getClaimApplication();
			BigDecimal claimAppAmount = claimApplicationItemVO.getClaimAmount()
					.subtract(claimApplicationItemWorkflowForm.getOverriddenAmount());
			claimApplicationVO.setTotalAmount(claimApplicationVO.getTotalAmount().subtract(claimAppAmount));
			claimApplicationDAO.update(claimApplicationVO);

			setClaimItemDataOnOverride(claimApplicationItemWorkflowForm ,claimApplicationItemVO);

			claimApplicationItemDAO.update(claimApplicationItemVO);
			updateClaimApplication(claimApplicationVO);
			
			saveOverrideMap.put("code", "200");
			saveOverrideMap.put("status", claimApplicationItemForm);
		return saveOverrideMap;
	}

	@Override
	public AddClaimForm getClaimReviewersData(AddClaimDTO claimDTO) {
		AddClaimForm response = new AddClaimForm();
		ClaimApplication claimApplicationVO = claimApplicationDAO.findByClaimApplicationID(claimDTO);
		if(claimApplicationVO==null){
			return null;
		}
		Boolean isApproved = false;
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationVO.getClaimApplicationReviewers()) {
			if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")	&& !claimApplicationReviewer.getPending()) {
				isApproved = true;
			}
		}
		try {
			setClaimTemplateItemListMsgSource(response, claimApplicationVO, claimDTO, messageSource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setClaimTemplateItemDTOList(null);
		response.setLundinTimesheetModule(UserContext.isLeaveModule());
		return response;
	}

	/*
	 *  NEW METHOD CREATED FOR REVIEWER LIST("claim-reviewers") IN MY-CLAIM API.
	 */
	private void setClaimTemplateItemListMsgSource(AddClaimForm response, ClaimApplication claimApplication, AddClaimDTO claimDTO, MessageSource messageSource) throws IOException {
		byte [] empImage= null;

		List<EmployeeClaimReviewer> claimReviewers = new ArrayList<>(claimApplication.getEmployeeClaimTemplate().getEmployeeClaimReviewers());

		int totalNoOfReviewers = 0;
		if (claimApplication.getClaimApplicationReviewers().size() == 0) {

			for (EmployeeClaimReviewer employeeClaimReviewer : claimReviewers) {

				totalNoOfReviewers++;
				if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					response.setClaimReviewer1(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setClaimReviewer1Img(employeeDetailLogic.getEmployeeImage(employeeClaimReviewer.getEmployee2().getEmployeeId(), null, employeeImageWidth, employeeImageHeight));

				} else if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
					response.setClaimReviewer2(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setClaimReviewer2Img(employeeDetailLogic.getEmployeeImage(employeeClaimReviewer.getEmployee2().getEmployeeId(), null, employeeImageWidth, employeeImageHeight));

				} else if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					response.setClaimReviewer3(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setClaimReviewer3Img(employeeDetailLogic.getEmployeeImage(employeeClaimReviewer.getEmployee2().getEmployeeId(), null, employeeImageWidth, employeeImageHeight));

				}

			}
		} else {

			for (ClaimApplicationReviewer claimApplicationReviewer : claimApplication.getClaimApplicationReviewers()) {

				totalNoOfReviewers++;
				if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					response.setClaimReviewer1(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setClaimReviewer1Img(employeeDetailLogic.getEmployeeImage(claimApplicationReviewer.getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight));
				} else if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					response.setClaimReviewer2(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setClaimReviewer2Img(employeeDetailLogic.getEmployeeImage(claimApplicationReviewer.getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight));
				} else if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					response.setClaimReviewer3(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setClaimReviewer3Img(employeeDetailLogic.getEmployeeImage(claimApplicationReviewer.getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight));
				}

			}
		}

		Timestamp claimAppSubmittedDate = null;
		HashMap<Long, ClaimApplicationWorkflow> workFlow = new HashMap<>();
		for (ClaimApplicationWorkflow claimApplicationWorkflow : claimApplication.getClaimApplicationWorkflows()) {

			if (claimApplicationWorkflow.getEmployee().getEmployeeId() == claimApplication.getEmployee().getEmployeeId()) {
				claimAppSubmittedDate = claimApplicationWorkflow.getCreatedDate();
			}

			workFlow.put(claimApplicationWorkflow.getEmployee().getEmployeeId(), claimApplicationWorkflow);
		}
		Set<ClaimApplicationReviewer> claimApplicationReviewerVOs = claimApplication.getClaimApplicationReviewers();
		List<ClaimApplicationWorkflowForm> claimWorkflows = new ArrayList<>();

		ClaimApplicationWorkflowForm appClaimWorkFlowForm = new ClaimApplicationWorkflowForm();

		if (claimApplication.getClaimStatusMaster().getClaimStatusName()
				.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
			appClaimWorkFlowForm.setStatusName(claimApplication.getClaimStatusMaster().getClaimStatusName());
			if (StringUtils.isNotBlank(claimApplication.getClaimStatusMaster().getLabelKey())) {
				appClaimWorkFlowForm.setStatusNameLocale(messageSource.getMessage(
						claimApplication.getClaimStatusMaster().getLabelKey(), new Object[] {}, claimDTO.getLocale()));
			} else {

				appClaimWorkFlowForm.setStatusNameLocale(claimApplication.getClaimStatusMaster().getClaimStatusName());
			}
		} else {
			appClaimWorkFlowForm.setStatusName(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
			if (StringUtils.isNotBlank(claimApplication.getClaimStatusMaster().getLabelKey())) {
				ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO.findByCondition(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				appClaimWorkFlowForm.setStatusNameLocale(claimStatusMaster.getClaimStatusName());
			} else {
				appClaimWorkFlowForm.setStatusNameLocale(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
			}
		}
	
		appClaimWorkFlowForm.setEmpName(getEmployeeName(claimApplication.getEmployee()));
		claimWorkflows.add(appClaimWorkFlowForm);
		List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(claimApplicationReviewerVOs);
		Collections.sort(claimApplicationReviewers, new ClaimTypeComp());
		Integer revCount = 1;
		
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			ClaimApplicationWorkflowForm claimApplicationWorkflowForm = new ClaimApplicationWorkflowForm();
			ClaimApplicationWorkflow claimApplicationWorkflow = workFlow
					.get(claimApplicationReviewer.getEmployee().getEmployeeId());

			claimApplicationWorkflowForm.setStatusNameLocale("");
			claimApplicationWorkflowForm.setStatusName("");
			
			if (claimApplicationWorkflow != null) {
				claimApplicationWorkflowForm.setRemarks(claimApplicationWorkflow.getRemarks());
				claimApplicationWorkflowForm.setStatusName(claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName());

				if (StringUtils.isNotBlank(claimApplicationWorkflow.getClaimStatusMaster().getLabelKey())) {
					claimApplicationWorkflowForm.setStatusNameLocale(
							messageSource.getMessage(claimApplicationWorkflow.getClaimStatusMaster().getLabelKey(),
									new Object[] {}, claimDTO.getLocale()));
				} else {
					claimApplicationWorkflowForm
							.setStatusNameLocale(claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName());
				}

				claimApplicationWorkflowForm.setCreatedDate(DateUtils.timeStampToStringWithTime(claimApplicationWorkflow.getCreatedDate()));
				claimApplicationWorkflowForm.setCreateDateM(claimApplicationWorkflow.getCreatedDate());
			}
			if (StringUtils.isNotBlank(claimApplicationReviewer.getWorkFlowRuleMaster().getLabelKey())) {
				claimApplicationWorkflowForm.setWorkflowRule(
						messageSource.getMessage(claimApplicationReviewer.getWorkFlowRuleMaster().getLabelKey(),
								new Object[] {}, claimDTO.getLocale()) + revCount);
			} else {
				claimApplicationWorkflowForm.setWorkflowRule(claimApplicationReviewer.getWorkFlowRuleMaster().getRuleName() + revCount);
			}
			claimApplicationWorkflowForm.setEmpName(getEmployeeName(claimApplicationReviewer.getEmployee()));
			if(StringUtils.isNotBlank(UserContext.getDevice()) && !UserContext.getDevice().equalsIgnoreCase("WEB BROWSER")){
			try {
				empImage = employeeDetailLogic.getEmployeeImage(claimApplicationReviewer.getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
			claimApplicationWorkflowForm.setEmpImage(empImage);
			}
			claimWorkflows.add(claimApplicationWorkflowForm);
			revCount++;
		}
		response.setClaimWorkflows(claimWorkflows);
		response.setTotalNoOfReviewers(totalNoOfReviewers);
	}

	/*
	 *  NEW METHOD FOR CLAIM ITEM DETAIL
	 */
	@Override
	public ClaimApplicationItemForm getClaimItemDetail(Long claimItemId, Long empId, Long companyId) {
		ClaimApplicationItem claimItemDetail = claimApplicationItemDAO.findById(claimItemId);
		if(claimItemDetail!=null){
			return setClaimItemDataAsItemWise(claimItemDetail, empId);
		}
		return null;
	}
	
	private ClaimApplicationItemForm setClaimItemDataAsItemWise(ClaimApplicationItem claimApplicationItem, Long employeeId) {

		   ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
			ClaimApplicationItemWorkflow claimApplicationItemWorkflowStatus = claimApplicationItemWorkflowDAO
					.findClaimItemStatus(claimApplicationItem, employeeId);
			claimApplicationItemForm.setCurrencyCode(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimTemplate().getDefaultCurrency() == null ? ""
							: claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
									.getClaimTemplate().getDefaultCurrency().getCurrencyCode());
			if (claimApplicationItemWorkflowStatus != null
					&& claimApplicationItemWorkflowStatus.getClaimItemWorkflowStatus().getCodeDesc()
							.equals(PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_STATUS_REJECTED)) {

				claimApplicationItemForm.setRejected(true);

			} else {
				claimApplicationItemForm.setRejected(false);
			}

			if (claimApplicationItem.getCurrencyMaster() != null) {
				claimApplicationItemForm.setCurrencyName(claimApplicationItem.getCurrencyMaster().getCurrencyName());
			}
			claimApplicationItemForm.setForexRate(claimApplicationItem.getExchangeRate());
			claimApplicationItemForm.setTaxAmount(claimApplicationItem.getTaxAmount());
			claimApplicationItemForm.setForexAmount(claimApplicationItem.getForexReceiptAmount());
			claimApplicationItemForm.setActive(claimApplicationItem.isActive());
			claimApplicationItemForm.setEmployeeClaimTemplateItemId(
					claimApplicationItem.getEmployeeClaimTemplateItem().getEmployeeClaimTemplateItemId());
			claimApplicationItemForm.setReceiptNumber(claimApplicationItem.getReceiptNumber());
			claimApplicationItemForm.setClaimApplicationItemID(claimApplicationItem.getClaimApplicationItemId());
			claimApplicationItemForm.setClaimApplicationClaimItemName(claimApplicationItem
					.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
			claimApplicationItemForm.setClaimDate(DateUtils.timeStampToString(claimApplicationItem.getClaimDate()));
			claimApplicationItemForm.setClaimAmount(claimApplicationItem.getClaimAmount());
			claimApplicationItemForm.setCategory(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
			claimApplicationItemForm.setAmountBeforeTax(claimApplicationItem.getAmountBeforeTax());
			claimApplicationItemForm.setRemarks(claimApplicationItem.getRemarks());
			claimApplicationItemForm.setQuantity(claimApplicationItem.getQuantity());
			claimApplicationItemForm.setUnitPrice(claimApplicationItem.getUnitPrice());
			claimApplicationItemForm.setApplicableClaimAmount(claimApplicationItem.getApplicableClaimAmount());
			claimApplicationItemForm.setTaxAmount(claimApplicationItem.getTaxAmount());

			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
					.getClaimTemplateItemClaimTypes().size() > 0) {
				if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
						.getClaimTemplateItemGenerals().size() > 0) {
					ClaimTemplateItemGeneral claimTemplateItemGeneral = claimApplicationItem
							.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemGenerals()
							.iterator().next();
					claimApplicationItemForm.setTaxAmountPer(claimTemplateItemGeneral.getTaxPercentage());
					claimApplicationItemForm.setAllowOverrideTaxAmt(claimTemplateItemGeneral.getAllowOverrideTaxAmt());
					if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
							.getClaimTemplateItemGenerals().iterator().next().getOpenToDependents()) {
						if (StringUtils.isNotBlank(claimApplicationItem.getClaimantName())) {
							claimApplicationItemForm.setClaimantName(claimApplicationItem.getClaimantName());
						}
					}
				}
			}

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
					claimApplicationItemForm
							.setApplicableClaimAmountPer(claimTemplateItemClaimType.getReceiptAmtPercentApplicable());
					Integer amtApplicablePercent = claimTemplateItemClaimType.getReceiptAmtPercentApplicable();
					if (amtApplicablePercent == null || amtApplicablePercent == 0) {
						claimApplicationItemForm.setApplicableClaimAmountPer(
								PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT);
					}
				} else {
					claimApplicationItemForm.setIsAmountBased(false);
				}

			}
			List<ClaimCustomFieldDTO> customFields = new ArrayList<>();
			for (ClaimApplicationItemCustomField claimApplicationItemCustomField : claimApplicationItem
					.getClaimApplicationItemCustomFields()) {
				ClaimCustomFieldDTO claimCustomFieldDTO = new ClaimCustomFieldDTO();
				claimCustomFieldDTO.setCustomFieldName(
						claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldName());
				if (claimApplicationItemCustomField.getClaimTemplateItemCustomField().getFieldType().getCodeValue()
						.equalsIgnoreCase("Date")) {
					if (StringUtils.isNotBlank(claimApplicationItemCustomField.getValue())) {
						claimCustomFieldDTO
								.setValue(DateUtils.convertDateFormat(claimApplicationItemCustomField.getValue(),
										UserContext.getWorkingCompanyDateFormat()));
					}

				} else {
					claimCustomFieldDTO.setValue(claimApplicationItemCustomField.getValue());
				}

				customFields.add(claimCustomFieldDTO);

			}
			List<ClaimApplicationItemAttach> attachements = new ArrayList<>();
			for (ClaimApplicationItemAttachment claimApplicationItemAttachment : claimApplicationItem
					.getClaimApplicationItemAttachments()) {
				ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
				claimApplicationItemAttach.setClaimApplicationItemAttachementId(
						claimApplicationItemAttachment.getClaimApplicationItemAttachmentId());
				claimApplicationItemAttach.setFileName(claimApplicationItemAttachment.getFileName());
				attachements.add(claimApplicationItemAttach);
			}
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
			claimApplicationItemForm.setAttachements(attachements);
			claimApplicationItemForm.setCustomFields(customFields);
			claimApplicationItemForm
					.setClaimApplicationID(claimApplicationItem.getClaimApplication().getClaimApplicationId());
			
			if (claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemGenerals().size() > 0) {
				boolean openToDepConfig = claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemGenerals().iterator().next().getOpenToDependents();
				if (openToDepConfig) {
					claimApplicationItemForm.setOpenToDependents(openToDepConfig);
				}
			}
			
			claimApplicationItemForm.setLundinTimesheetModule(UserContext.isLundinTimesheetModule());

			// Get Lundin Claims Details
			Set<ClaimApplicationItemLundinDetail> applicationItemLundinDetails = claimApplicationItem
					.getClaimApplicationItemLundinDetails();
			if (!applicationItemLundinDetails.isEmpty()) {
				claimApplicationItemForm.setBlockName(claimApplicationItem.getClaimApplicationItemLundinDetails()
						.iterator().next().getLundinBlock().getBlockName());
				claimApplicationItemForm.setAfeName(claimApplicationItem.getClaimApplicationItemLundinDetails()
						.iterator().next().getLundinAFE().getAfeName());
			}
			return claimApplicationItemForm;
	}
	
}
