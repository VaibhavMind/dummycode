package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimApplicationItemDTO;
import com.payasia.common.dto.ClaimDetailsReportCustomDataDTO;
import com.payasia.common.dto.ClaimDetailsReportDTO;
import com.payasia.common.dto.ClaimDetailsReportDataDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.dto.ClaimItemDTO;
import com.payasia.common.dto.ClaimReportHeaderDTO;
import com.payasia.common.dto.CustomFieldReportDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimApplicationWorkflowForm;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimApplicationItemAttachmentDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.ClaimApplicationWorkflowDAO;
import com.payasia.dao.ClaimBatchMasterDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.ClaimTemplateItemGeneralDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemAttachment;
import com.payasia.dao.bean.ClaimApplicationItemCustomField;
import com.payasia.dao.bean.ClaimApplicationItemLundinDetail;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimBatchMaster;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.ClaimStatusMaster;
import com.payasia.dao.bean.ClaimTemplateItemClaimType;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.ClaimFormPrintPDFLogic;
import com.payasia.logic.EmployeeClaimsLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.MyClaimLogic;

@Component
public class MyClaimLogicImpl implements MyClaimLogic {

	private static final Logger LOGGER = Logger.getLogger(MyClaimLogicImpl.class);

	@Resource
	ClaimStatusMasterDAO claimstatusMasterDAO;

	@Resource
	ClaimApplicationDAO claimApplicationDAO;

	@Resource
	ClaimApplicationItemAttachmentDAO claimApplicationItemAttachmentDAO;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;

	@Resource
	ClaimApplicationWorkflowDAO claimApplicationWorkflowDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	EmployeeClaimsLogic employeeClaimsLogic;

	@Resource
	ClaimApplicationItemDAO claimApplicationItemDAO;

	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;

	@Resource
	ClaimFormPrintPDFLogic claimFormPrintPDFLogic;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;

	@Resource
	private ClaimBatchMasterDAO claimBatchMasterDAO;

	@Resource
	private CompanyDAO companyDAO;

	@Resource
	private EmployeeDAO employeeDAO;
	@Resource
	ClaimTemplateItemGeneralDAO claimTemplateItemGeneralDAO;

	@Resource
	private MessageSource messageSource;

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
	FileUtils fileUtils;
	
	@Resource
	GeneralLogic generalLogic;
	
	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;

	@Override
	public AddClaimFormResponse getPendingClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();
		//String edit = "payasia.edit";
		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setCreatedDate(claimDTO.getSearchText().trim());
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setClaimGroup("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isBlank(claimDTO.getSearchText())){
			}
			else if(claimDTO.getSearchText().matches("^[0-9]+$")) {
				conditionDTO.setClaimNumber(Long.parseLong(claimDTO.getSearchText().trim()));
			}
			else{
				return null;
			}
		}

		conditionDTO.setVisibleToEmployee(true);
		conditionDTO.setEmployeeId(claimDTO.getEmployeeId());
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_DRAFT);
		conditionDTO.setClaimStatus(claimStatus);
		if (StringUtils.isNotBlank(claimDTO.getFromDate())) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(claimDTO.getFromDate()));
		}

		if (StringUtils.isNotBlank(claimDTO.getToDate())) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(claimDTO.getToDate()));
		}

		int recordSize = (claimApplicationDAO.getCountForCondition(conditionDTO)).intValue();

		Employee employeeVO = employeeDAO.findById(claimDTO.getEmployeeId());
		Long companyId = employeeVO.getCompany().getCompanyId();
		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		conditionDTO.setCreatedDateSortOrder(claimPreferenceForm.getCreatedDateSortOrder());
		conditionDTO.setClaimNumberSortOrder(claimPreferenceForm.getClaimNumberSortOrder());

		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByCondition(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {
			AddClaimForm addClaimForm = new AddClaimForm();

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
//			claimTemplate.append("<br>");
//			claimTemplate
//					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editClaimApplication("
//							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>["
//							+ messageSource.getMessage(edit, new Object[] {}, claimDTO.getLocale()) + "]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
//			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
//					+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>" + claimApplication.getTotalItems() + "</a>");
			claimTemplateItemCount.append(claimApplication.getTotalItems());

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			
			addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));
			
			List<EmployeeClaimReviewer> employeeClaimReviewers = new ArrayList<>(
					claimApplication.getEmployeeClaimTemplate().getEmployeeClaimReviewers());

			Collections.sort(employeeClaimReviewers, new EmployeeReviewerComp());
			for (EmployeeClaimReviewer employeeClaimReviewer : employeeClaimReviewers) {
				if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					addClaimForm.setClaimReviewer1(getEmployeeName(employeeClaimReviewer.getEmployee2()));

				}
				if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					addClaimForm.setClaimReviewer2(getEmployeeName(employeeClaimReviewer.getEmployee2()));

				}
				if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					addClaimForm.setClaimReviewer3(getEmployeeName(employeeClaimReviewer.getEmployee2()));

				}

			}

			addClaimFormList.add(addClaimForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

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

	private BigDecimal getTotalAmountApplicable(ClaimApplication claimApplication, BigDecimal totalApplicableAmount) {
		Set<ClaimApplicationItem> itemsList = claimApplication.getClaimApplicationItems();
		for (ClaimApplicationItem applicationItem : itemsList) {
			if (applicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem().getClaimTemplateItemClaimTypes()
					.isEmpty()) {
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

	/**
	 * Comparator Class for Ordering claimApplicationWorkflow List
	 */
	private class ClaimReviewerComp implements Comparator<ClaimApplicationReviewer> {
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

	private class EmployeeReviewerComp implements Comparator<EmployeeClaimReviewer> {
		public int compare(EmployeeClaimReviewer templateField, EmployeeClaimReviewer compWithTemplateField) {
			if (templateField.getEmployeeClaimReviewerId() > compWithTemplateField.getEmployeeClaimReviewerId()) {
				return 1;
			} else if (templateField.getEmployeeClaimReviewerId() < compWithTemplateField
					.getEmployeeClaimReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public AddClaimForm getClaimApplicationData(AddClaimDTO claimDTO) {
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
		response.setIsApproved(isApproved);
		response.setVisibleToEmployee(claimApplicationVO.getVisibleToEmployee());
		response.setEmployeeClaimTemplateId(claimApplicationVO.getEmployeeClaimTemplate().getEmployeeClaimTemplateId());
		setClaimTemplateItemList(response, claimApplicationVO, claimDTO);
		response.setClaimTemplateName(claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
		response.setEmailCC(claimApplicationVO.getEmailCC());

		return response;
	}

	private void setClaimTemplateItemList(AddClaimForm response, ClaimApplication claimApplication,
			AddClaimDTO claimDTO) {
		String user = "payasia.user";
		List<AddClaimForm> claimTemplateItemList = new ArrayList<AddClaimForm>();
		EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO
				.findByID(claimApplication.getEmployeeClaimTemplate().getEmployeeClaimTemplateId());

		claimDTO.setEmployeeClaimTemplateId(employeeClaimTemplate.getEmployeeClaimTemplateId());
		claimDTO.setAdmin(false);

		List<EmployeeClaimTemplateItem> employeeClaimTemplateItems = employeeClaimTemplateItemDAO
				.findByEmployeeClaimTemplateId(claimDTO);

		response.setEmployeeClaimTemplateItemId(employeeClaimTemplate.getEmployeeClaimTemplateId());
		response.setClaimTemplateName(employeeClaimTemplate.getClaimTemplate().getTemplateName());
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
				claimItemDTO.setEmployeeClaimTemplateItemId(FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateItem.getEmployeeClaimTemplateItemId()));
				claimItemDTO.setClaimItemId(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemId());
				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO
						.findByItemId("" + employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());
				if (claimTemplateItemGeneralVO != null
						&& StringUtils.isNotEmpty(claimTemplateItemGeneralVO.getHelpText())) {

					claimItemDTO.setHelpText(claimTemplateItemGeneralVO.getHelpText());
				}

				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
				claimItems.add(claimItemDTO);
				claimApplicationItemDTO.setClaimItems(claimItems);
				claimApplicationItemDTO.setCategoryId(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryId());
				claimApplicationItemDTO.setCategoryName(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
				claimApplicationItems.put(claimItemCategoryId, claimApplicationItemDTO);

			} else {
				ClaimItemDTO claimItemDTO = new ClaimItemDTO();
				claimItemDTO.setEmployeeClaimTemplateItemId(FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateItem.getEmployeeClaimTemplateItemId()));
				claimItemDTO.setClaimItemId(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemId());
				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO
						.findByItemId("" + employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());
				if (claimTemplateItemGeneralVO != null
						&& StringUtils.isNotEmpty(claimTemplateItemGeneralVO.getHelpText())) {

					claimItemDTO.setHelpText(claimTemplateItemGeneralVO.getHelpText());
				}
				claimApplicationItems.get(claimItemCategoryId).getClaimItems().add(claimItemDTO);

			}

		}
		response.setClaimApplicationItems(claimApplicationItems);
		response.setClaimTemplateItemList(claimTemplateItemList);
		List<EmployeeClaimReviewer> claimReviewers = new ArrayList<>(
				claimApplication.getEmployeeClaimTemplate().getEmployeeClaimReviewers());

		int totalNoOfReviewers = 0;
		if (claimApplication.getClaimApplicationReviewers().size() == 0) {

			for (EmployeeClaimReviewer employeeClaimReviewer : claimReviewers) {

				totalNoOfReviewers++;
				if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					response.setClaimReviewer1(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setApplyTo(employeeDetailLogic.getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setApplyToId(employeeClaimReviewer.getEmployee2().getEmployeeId());

				} else if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					response.setClaimReviewer2(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setClaimReviewer2Id(employeeClaimReviewer.getEmployee2().getEmployeeId());

				} else if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					response.setClaimReviewer3(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setClaimReviewer3Id(employeeClaimReviewer.getEmployee2().getEmployeeId());
				}

			}
		} else {

			for (ClaimApplicationReviewer claimApplicationReviewer : claimApplication.getClaimApplicationReviewers()) {

				totalNoOfReviewers++;
				if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					response.setClaimReviewer1(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setApplyTo(employeeDetailLogic.getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setApplyToId(claimApplicationReviewer.getEmployee().getEmployeeId());

				} else if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					response.setClaimReviewer2(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setClaimReviewer2Id(claimApplicationReviewer.getEmployee().getEmployeeId());

				} else if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					response.setClaimReviewer3(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setClaimReviewer3Id(claimApplicationReviewer.getEmployee().getEmployeeId());
				}

			}
		}

		Timestamp claimAppSubmittedDate = null;
		HashMap<Long, ClaimApplicationWorkflow> workFlow = new HashMap<>();
		for (ClaimApplicationWorkflow claimApplicationWorkflow : claimApplication.getClaimApplicationWorkflows()) {

			if (claimApplicationWorkflow.getEmployee().getEmployeeId() == claimApplication.getEmployee()
					.getEmployeeId()) {
				claimAppSubmittedDate = claimApplicationWorkflow.getCreatedDate();
			}

			workFlow.put(claimApplicationWorkflow.getEmployee().getEmployeeId(), claimApplicationWorkflow);

		}
		Set<ClaimApplicationReviewer> claimApplicationReviewerVOs = claimApplication.getClaimApplicationReviewers();
		List<ClaimApplicationWorkflowForm> claimWorkflows = new ArrayList<>();

		ClaimApplicationWorkflowForm appClaimWorkFlowForm = new ClaimApplicationWorkflowForm();

		appClaimWorkFlowForm.setRemarks(claimApplication.getRemarks());

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
				ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO
						.findByCondition(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				
				//String msg = messageSource.getMessage(claimStatusMaster.getLabelKey(),new Object[] {}, UserContext.getLocale());
				appClaimWorkFlowForm.setStatusNameLocale("h");
			} else {
				appClaimWorkFlowForm.setStatusNameLocale(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
			}

		}

		appClaimWorkFlowForm.setCreatedDate(
				claimAppSubmittedDate == null ? DateUtils.timeStampToString(claimApplication.getCreatedDate())
						: DateUtils.timeStampToString(claimAppSubmittedDate));
		appClaimWorkFlowForm.setCreateDateM(claimAppSubmittedDate);
		appClaimWorkFlowForm.setWorkflowRule(messageSource.getMessage(user, new Object[] {}, claimDTO.getLocale()));
		appClaimWorkFlowForm.setRemarks(claimApplication.getRemarks());
		appClaimWorkFlowForm.setEmpName(getEmployeeName(claimApplication.getEmployee()));

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
					claimApplicationWorkflowForm.setStatusNameLocale(
							messageSource.getMessage(claimApplicationWorkflow.getClaimStatusMaster().getLabelKey(),
									new Object[] {}, claimDTO.getLocale()));
				} else {
					claimApplicationWorkflowForm
							.setStatusNameLocale(claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName());
				}

				claimApplicationWorkflowForm
						.setCreatedDate(DateUtils.timeStampToStringWithTime(claimApplicationWorkflow.getCreatedDate()));
				claimApplicationWorkflowForm.setCreateDateM(claimApplicationWorkflow.getCreatedDate());
			}
			if (StringUtils.isNotBlank(claimApplicationReviewer.getWorkFlowRuleMaster().getLabelKey())) {
				claimApplicationWorkflowForm.setWorkflowRule(
						messageSource.getMessage(claimApplicationReviewer.getWorkFlowRuleMaster().getLabelKey(),
								new Object[] {}, claimDTO.getLocale()) + revCount);
			} else {
				claimApplicationWorkflowForm
						.setWorkflowRule(claimApplicationReviewer.getWorkFlowRuleMaster().getRuleName() + revCount);
			}
			claimApplicationWorkflowForm.setEmpName(getEmployeeName(claimApplicationReviewer.getEmployee()));
			claimWorkflows.add(claimApplicationWorkflowForm);
			revCount++;

		}
		response.setClaimWorkflows(claimWorkflows);

		response.setTotalNoOfReviewers(totalNoOfReviewers);

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

	@Override
	public AddClaimFormResponse getSubmittedClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setCreatedDate(claimDTO.getSearchText().trim());
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setClaimGroup("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isBlank(claimDTO.getSearchText())){
			}
			else if(claimDTO.getSearchText().matches("^[0-9]+$")) {
				conditionDTO.setClaimNumber(Long.parseLong(claimDTO.getSearchText().trim()));
			}
			else{
				return null;
			}
		}

		conditionDTO.setEmployeeId(claimDTO.getEmployeeId());
		conditionDTO.setVisibleToEmployee(true);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
		conditionDTO.setClaimStatus(claimStatus);

		if (StringUtils.isNotBlank(claimDTO.getFromDate())) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(claimDTO.getFromDate()));
		}

		if (StringUtils.isNotBlank(claimDTO.getToDate())) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(claimDTO.getToDate()));
		}

		int recordSize = (claimApplicationDAO.getCountForCondition(conditionDTO)).intValue();

		Employee employeeVO = employeeDAO.findById(claimDTO.getEmployeeId());
		Long companyId = employeeVO.getCompany().getCompanyId();
		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		conditionDTO.setCreatedDateSortOrder(claimPreferenceForm.getCreatedDateSortOrder());
		conditionDTO.setClaimNumberSortOrder(claimPreferenceForm.getClaimNumberSortOrder());
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByCondition(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {
			AddClaimForm addClaimForm = new AddClaimForm();

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
//			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
//					+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>" + claimApplication.getTotalItems() + "</a>");
			claimTemplateItemCount.append(claimApplication.getTotalItems());

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
			String reviewer1Status = "";
			String reviewer2Status = "";

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
//			claimTemplate.append("<br>");
//			String claimStatusMode = ",\"submitted\"";
//			claimTemplate
//					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
//							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>[View]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));

			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());

			Collections.sort(claimApplicationReviewers, new ClaimReviewerComp());

			int revCount = 1;
			for (ClaimApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());

					if (claimApplicationWorkflow == null) {

						addClaimForm.setClaimReviewer1(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, claimDTO.getPageContextPath(),
										claimAppReviewer.getEmployee()));

//						claimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ claimApplicationWorkflow.getCreatedDate() + "</span>");
//						claimReviewer1.append(claimApplicationWorkflow.getCreatedDate());
						
						addClaimForm.setClaimReviewer1(String.valueOf(claimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					}
				}
				if (revCount == 2) {

					ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());
					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

						addClaimForm.setClaimReviewer2(
								getStatusImage("NA", claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (claimApplicationWorkflow == null) {

							addClaimForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
									claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));
							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
						} else if (claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder claimReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
											claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

//							claimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//									+ claimApplicationWorkflow.getCreatedDate() + "</span>");
//							claimReviewer2.append(claimApplicationWorkflow.getCreatedDate());

							addClaimForm.setClaimReviewer2(String.valueOf(claimReviewer2));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

						}

					}

				}
				if (revCount == 3) {
					ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());
					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

						addClaimForm.setClaimReviewer3(
								getStatusImage("NA", claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (claimApplicationWorkflow == null) {
							addClaimForm.setClaimReviewer3(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
									claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));
						} else if (claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder claimReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
											claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

//							claimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//									+ claimApplicationWorkflow.getCreatedDate() + "</span>");
//							claimReviewer3.append(claimApplicationWorkflow.getCreatedDate());

							addClaimForm.setClaimReviewer3(String.valueOf(claimReviewer3));

						}

					}

				}
				revCount++;
			}

			addClaimFormList.add(addClaimForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

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

	private String getStatusImage(String status, String contextPath, Employee employee) {
		String imagePath = contextPath + "/resources/images/icon/16/";
		if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {
			imagePath = "PENDING";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
			imagePath = "APPROVED";
		} else if ("NA".equalsIgnoreCase(status)) {
			imagePath = "PENDING-NEXT-LEVEL";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
			imagePath = "REJECTED";
		}
		String employeeName = getEmployeeName(employee);
		return imagePath + "##" + employeeName;
	}
	
	// ORIGINAL FUNCTION
	/*	
	 	private String getStatusImage(String status, String contextPath, Employee employee) {
		String imagePath = contextPath + "/resources/images/icon/16/";
		if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {
			imagePath = imagePath + "pending.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
			imagePath = imagePath + "approved.png";
		} else if ("NA".equalsIgnoreCase(status)) {
			imagePath = imagePath + "pending-next-level.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
			imagePath = imagePath + "rejected.png";
		}
		String employeeName = getEmployeeName(employee);

		return "<img src='" + imagePath + "'  />  " + employeeName;
	}
	 */

	@Override
	public AddClaimFormResponse getApprovedClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setCreatedDate(claimDTO.getSearchText().trim());
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setClaimGroup("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isBlank(claimDTO.getSearchText())){
			}
			else if(claimDTO.getSearchText().matches("^[0-9]+$")) {
				conditionDTO.setClaimNumber(Long.parseLong(claimDTO.getSearchText().trim()));
			}
			else{
				return null;
			}
		}

		Employee employeeVO = employeeDAO.findById(claimDTO.getEmployeeId());
		Long companyId = employeeVO.getCompany().getCompanyId();
		List<ClaimBatchMaster> claimBatchMasterList = claimBatchMasterDAO.getClaimBatchMastersByDateRange(companyId,
				null, null, null, PayAsiaConstants.CLAIM_PAID_STATUS_ALL);

		conditionDTO.setEmployeeId(claimDTO.getEmployeeId());
		conditionDTO.setVisibleToEmployee(true);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		conditionDTO.setClaimStatus(claimStatus);

		if (StringUtils.isNotBlank(claimDTO.getFromDate())) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(claimDTO.getFromDate()));
		}

		if (StringUtils.isNotBlank(claimDTO.getToDate())) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(claimDTO.getToDate()));
		}

		int recordSize = (claimApplicationDAO.getCountForCondition(conditionDTO)).intValue();

		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		conditionDTO.setCreatedDateSortOrder(claimPreferenceForm.getCreatedDateSortOrder());
		conditionDTO.setClaimNumberSortOrder(claimPreferenceForm.getClaimNumberSortOrder());
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByCondition(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {
			AddClaimForm addClaimForm = new AddClaimForm();

			ClaimApplicationWorkflow applicationCompletedWorkflow = claimApplicationWorkflowDAO.findByAppIdAndStatus(
					claimApplication.getClaimApplicationId(), PayAsiaConstants.CLAIM_STATUS_COMPLETED);
			if (applicationCompletedWorkflow != null) {
				String paidDate = getPaidDate(claimBatchMasterList, applicationCompletedWorkflow.getCreatedDate());
				paidDate = claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED) ? paidDate : "";

				addClaimForm.setPaidDate(paidDate);
			} else {
				addClaimForm.setPaidDate("");
			}

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
//			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
//					+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>" + claimApplication.getTotalItems() + "</a>");
			claimTemplateItemCount.append(claimApplication.getTotalItems());

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
//			claimTemplate.append("<br>");
//			String claimStatusMode = ",\"approved\"";
//			claimTemplate
//					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
//							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>[View]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));
			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());

			Collections.sort(claimApplicationReviewers, new ClaimReviewerComp());
			int revCount = 1;
			for (ClaimApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

//					ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
//							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());
					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, claimDTO.getPageContextPath(),
									claimAppReviewer.getEmployee()));

//					ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//							+ applicationWorkflow.getCreatedDate() + "</span>");

					addClaimForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

				}
				if (revCount == 2) {

					ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder ClaimReviewer2 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, claimDTO.getPageContextPath(),
									claimAppReviewer.getEmployee()));

//					if (applicationWorkflow.getCreatedDate() != null) {
//						ClaimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ applicationWorkflow.getCreatedDate() + "</span>");
//					}

					addClaimForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

				}
				if (revCount == 3) {

					ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder ClaimReviewer3 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, claimDTO.getPageContextPath(),
									claimAppReviewer.getEmployee()));

//					ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//							+ applicationWorkflow.getCreatedDate() + "</span>");

					addClaimForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

				}
				revCount++;
			}

			addClaimFormList.add(addClaimForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

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
	public AddClaimFormResponse getRejectedClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setCreatedDate(claimDTO.getSearchText().trim());
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setClaimGroup("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isBlank(claimDTO.getSearchText())){
			}
			else if(claimDTO.getSearchText().matches("^[0-9]+$")) {
				conditionDTO.setClaimNumber(Long.parseLong(claimDTO.getSearchText().trim()));
			}
			else{
				return null;
			}
		}

		conditionDTO.setEmployeeId(claimDTO.getEmployeeId());
		conditionDTO.setVisibleToEmployee(true);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
		conditionDTO.setClaimStatus(claimStatus);

		if (StringUtils.isNotBlank(claimDTO.getFromDate())) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(claimDTO.getFromDate()));
		}

		if (StringUtils.isNotBlank(claimDTO.getToDate())) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(claimDTO.getToDate()));
		}

		int recordSize = (claimApplicationDAO.getCountForCondition(conditionDTO)).intValue();

		Employee employeeVO = employeeDAO.findById(claimDTO.getEmployeeId());
		Long companyId = employeeVO.getCompany().getCompanyId();
		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		conditionDTO.setCreatedDateSortOrder(claimPreferenceForm.getCreatedDateSortOrder());
		conditionDTO.setClaimNumberSortOrder(claimPreferenceForm.getClaimNumberSortOrder());
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByCondition(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddClaimForm addClaimForm = new AddClaimForm();

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
//			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
//					+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>" + claimApplication.getTotalItems() + "</a>");
			claimTemplateItemCount.append(claimApplication.getTotalItems());

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
//			claimTemplate.append("<br>");
//			String claimStatusMode = ",\"rejected\"";
//			claimTemplate
//					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
//							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>[View]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));

			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());

			Collections.sort(claimApplicationReviewers, new ClaimReviewerComp());

			Boolean reviewStatus = false;
			int revCount = 1;
			for (ClaimApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (reviewStatus) {
					continue;
				}

				if (revCount == 1) {

					ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());

					if (applicationWorkflow == null) {

						addClaimForm.setClaimReviewer1(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, claimDTO.getPageContextPath(),
										claimAppReviewer.getEmployee()));

//						ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ applicationWorkflow.getCreatedDate() + "</span>");

						addClaimForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
						reviewStatus = true;
						StringBuilder ClaimReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED, claimDTO.getPageContextPath(),
										claimAppReviewer.getEmployee()));

//						ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ applicationWorkflow.getCreatedDate() + "</span>");

						addClaimForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

						reviewer1Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

					}
				}
				if (revCount == 2) {

					ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());

					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
							|| reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addClaimForm.setClaimReviewer2(
								getStatusImage("NA", claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							addClaimForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
									claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));
							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
						} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder ClaimReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
											claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

//							ClaimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//									+ applicationWorkflow.getCreatedDate() + "</span>");

							addClaimForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

						} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
							reviewStatus = true;
							addClaimForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED,
									claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

							reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

						}

					}

				}
				if (revCount == 3) {
					ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
							claimApplication.getClaimApplicationId(), claimAppReviewer.getEmployee().getEmployeeId());
					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
							|| reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addClaimForm.setClaimReviewer3(
								getStatusImage("NA", claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							addClaimForm.setClaimReviewer3(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
									claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));
						} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

							StringBuilder ClaimReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
											claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

//							ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//									+ applicationWorkflow.getCreatedDate() + "</span>");

							addClaimForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

						} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

							StringBuilder ClaimReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED,
											claimDTO.getPageContextPath(), claimAppReviewer.getEmployee()));

//							ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//									+ applicationWorkflow.getCreatedDate() + "</span>");

							addClaimForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

						}

					}

				}
				revCount++;
			}

			addClaimFormList.add(addClaimForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

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
	public AddClaimFormResponse getWithdrawnClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setCreatedDate(claimDTO.getSearchText().trim());
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setClaimGroup("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isBlank(claimDTO.getSearchText())){
			}
			else if(claimDTO.getSearchText().matches("^[0-9]+$")) {
				conditionDTO.setClaimNumber(Long.parseLong(claimDTO.getSearchText().trim()));
			}
			else{
				return null;
			}
		}
		conditionDTO.setEmployeeId(claimDTO.getEmployeeId());
		conditionDTO.setVisibleToEmployee(true);
		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN);
		conditionDTO.setClaimStatus(claimStatus);

		if (StringUtils.isNotBlank(claimDTO.getFromDate())) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(claimDTO.getFromDate()));
		}

		if (StringUtils.isNotBlank(claimDTO.getToDate())) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(claimDTO.getToDate()));
		}

		int recordSize = (claimApplicationDAO.getCountForCondition(conditionDTO)).intValue();

		Employee employeeVO = employeeDAO.findById(claimDTO.getEmployeeId());
		Long companyId = employeeVO.getCompany().getCompanyId();
		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		conditionDTO.setCreatedDateSortOrder(claimPreferenceForm.getCreatedDateSortOrder());
		conditionDTO.setClaimNumberSortOrder(claimPreferenceForm.getClaimNumberSortOrder());
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByCondition(conditionDTO, pageDTO, sortDTO);

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {
			AddClaimForm addClaimForm = new AddClaimForm();

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
//			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
//					+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>" + claimApplication.getTotalItems() + "</a>");
			claimTemplateItemCount.append(claimApplication.getTotalItems());

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
//			claimTemplate.append("<br>");
//			String claimStatusMode = ",\"withdrawn\"";
//			claimTemplate
//					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
//							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>[View]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));

			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());

			Collections.sort(claimApplicationReviewers, new ClaimReviewerComp());
			int revCount = 1;
			for (ClaimApplicationReviewer claimAppReviewer : claimApplicationReviewers) {
				if (revCount == 1) {

					addClaimForm.setClaimReviewer1(getEmployeeName(claimAppReviewer.getEmployee()));

				}
				if (revCount == 2) {

					addClaimForm.setClaimReviewer2(getEmployeeName(claimAppReviewer.getEmployee()));

				}
				if (revCount == 3) {

					addClaimForm.setClaimReviewer3(getEmployeeName(claimAppReviewer.getEmployee()));

				}
				revCount++;
			}

			addClaimFormList.add(addClaimForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

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

	@Override
	public AddClaimFormResponse getAllClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {

		List<String> claimStatus = new ArrayList<>();
		AddClaimConditionDTO conditionDTO = new AddClaimConditionDTO();

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setCreatedDate(claimDTO.getSearchText().trim());
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_GROUP)) {
			if (StringUtils.isNotBlank(claimDTO.getSearchText())) {
				conditionDTO.setClaimGroup("%" + claimDTO.getSearchText().trim() + "%");
			}
		}

		if (claimDTO.getSearchCondition().equals(PayAsiaConstants.MY_REQUEST_CLAIM_NUMBER)) {
			if (StringUtils.isBlank(claimDTO.getSearchText())){
			}
			else if(claimDTO.getSearchText().matches("^[0-9]+$")) {
				conditionDTO.setClaimNumber(Long.parseLong(claimDTO.getSearchText().trim()));
			}
			else{
				return null;
			}
		}

		List<ClaimBatchMaster> claimBatchMasterList = claimBatchMasterDAO.getClaimBatchMastersByDateRange(
				claimDTO.getCompanyId(), null, null, null, PayAsiaConstants.CLAIM_PAID_STATUS_ALL);

		int recordSize = claimApplicationDAO.getCountByConditionForEmployee(claimDTO.getEmployeeId(), claimStatus,
				claimDTO.getFromDate(), claimDTO.getToDate(), conditionDTO);

		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(claimDTO.getCompanyId());
		Boolean visibleToEmployee = true;
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByConditionForEmployee(pageDTO, sortDTO,
				claimDTO.getEmployeeId(), claimStatus, claimDTO.getFromDate(), claimDTO.getToDate(), visibleToEmployee,
				conditionDTO, claimPreferenceForm.getClaimNumberSortOrder(),
				claimPreferenceForm.getCreatedDateSortOrder());

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {
			AddClaimForm addClaimForm = new AddClaimForm();

			ClaimApplicationWorkflow applicationCompletedWorkflow = claimApplicationWorkflowDAO.findByAppIdAndStatus(
					claimApplication.getClaimApplicationId(), PayAsiaConstants.CLAIM_STATUS_COMPLETED);
			if (applicationCompletedWorkflow != null) {
				String paidDate = getPaidDate(claimBatchMasterList, applicationCompletedWorkflow.getCreatedDate());
				paidDate = claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED) ? paidDate : "";

				addClaimForm.setPaidDate(paidDate);
			} else {
				addClaimForm.setPaidDate("");
			}

			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {

				StringBuilder claimTemplateEdit = new StringBuilder();
				claimTemplateEdit
						.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());

				addClaimForm.setClaimTemplateName(String.valueOf(claimTemplateEdit));
				StringBuilder claimWithDrawLink = new StringBuilder();

				addClaimForm.setAction(String.valueOf(claimWithDrawLink));
			} else {
				StringBuilder claimTemplateView = new StringBuilder();
				claimTemplateView
						.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());

				if (claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {

				} else if (claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
						|| claimApplication.getClaimStatusMaster().getClaimStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				} else if (claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				} else if (claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED)) {
				}

				addClaimForm.setClaimTemplateName(String.valueOf(claimTemplateView));
			}

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| claimApplication.getClaimStatusMaster().getClaimStatusName()
							.equals(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {

				if (claimApplication.getCreatedBy() != null) {
					if (claimApplication.getCreatedBy().equals(String.valueOf(claimDTO.getEmployeeId()))) {
						StringBuilder claimWithDrawLink = new StringBuilder();
						addClaimForm.setAction(String.valueOf(claimWithDrawLink));
					}
				}
			}
			addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()));
			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount.append(claimApplication.getTotalItems());

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			List<ClaimApplicationReviewer> claimAppReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());

			List<EmployeeClaimReviewer> empClaimReviewers = new ArrayList<>(
					claimApplication.getEmployeeClaimTemplate().getEmployeeClaimReviewers());

			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
				employeeClaimsLogic.getPendingClaimReviewers(empClaimReviewers, addClaimForm);
			}
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
				employeeClaimsLogic.getApprovedClaimReviewers(claimAppReviewers, addClaimForm,
						claimDTO.getPageContextPath(), claimApplication);
			}
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| claimApplication.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				employeeClaimsLogic.getSubmittedClaimReviewers(claimAppReviewers, addClaimForm,
						claimDTO.getPageContextPath(), claimApplication);
			}
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
				employeeClaimsLogic.getWithdrawClaimReviewers(claimAppReviewers, addClaimForm);
			}
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
				employeeClaimsLogic.getRejectedClaimReviewers(claimAppReviewers, addClaimForm,
						claimDTO.getPageContextPath(), claimApplication);
			}

			addClaimFormList.add(addClaimForm);
		}

		AddClaimFormResponse response = new AddClaimFormResponse();
		response.setAddClaimFormList(addClaimFormList);

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
	public AddClaimFormResponse searchClaimTemplateItems(AddClaimDTO claimDTO, PageRequest pageDTO,
			SortCondition sortDTO, MessageSource messageSource) {
		String approve = "payasia.myRequest.approved";
		String rejected = "payasia.myRequest.rejected";
		AddClaimFormResponse response = new AddClaimFormResponse();
		Integer recordSize = claimApplicationItemDAO.getCountForCondition(claimDTO.getClaimApplicationId(), pageDTO, sortDTO);
		List<ClaimApplicationItem> claimApplicationItems = claimApplicationItemDAO.findByCondition(claimDTO.getClaimApplicationId(),
				pageDTO, sortDTO);
		List<ClaimApplicationItemForm> claimAppItems = new ArrayList<>();
		for (ClaimApplicationItem claimApplicationItem : claimApplicationItems) {
			ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
			claimApplicationItemForm.setClaimItemName(claimApplicationItem.getEmployeeClaimTemplateItem()
					.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
			claimApplicationItemForm.setReceiptNumber(claimApplicationItem.getReceiptNumber());
			claimApplicationItemForm.setClaimAmount(claimApplicationItem.getClaimAmount());
			claimApplicationItemForm.setClaimDate(DateUtils.timeStampToString(claimApplicationItem.getClaimDate()));

			if (claimApplicationItem.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				if (claimApplicationItem.isActive()) {
					/*
					 * claimApplicationItemForm
					 * .setClaimItemStatus(PayAsiaConstants
					 * .CLAIM_LIST_STATUS_APPROVED);
					 */
					claimApplicationItemForm
							.setClaimItemStatus(messageSource.getMessage(approve, new Object[] {}, claimDTO.getLocale()));
				} else {
					/*
					 * claimApplicationItemForm
					 * .setClaimItemStatus(PayAsiaConstants
					 * .CLAIM_LIST_STATUS_REJECTED);
					 */
					claimApplicationItemForm
							.setClaimItemStatus(messageSource.getMessage(rejected, new Object[] {}, claimDTO.getLocale()));
				}
			} else if (claimApplicationItem.getClaimApplication().getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				/*
				 * claimApplicationItemForm
				 * .setClaimItemStatus(PayAsiaConstants.
				 * CLAIM_LIST_STATUS_REJECTED);
				 */
				claimApplicationItemForm
						.setClaimItemStatus(messageSource.getMessage(rejected, new Object[] {}, claimDTO.getLocale()));
			} else {
				claimApplicationItemForm.setClaimItemStatus("");
			}

			claimAppItems.add(claimApplicationItemForm);
		}
		response.setClaimApplicationItemForm(claimAppItems);
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
	public ClaimFormPdfDTO generateClaimFormPrintPDF(AddClaimDTO claimDTO) {
		ClaimFormPdfDTO claimFormPdfDTO = new ClaimFormPdfDTO();
		ClaimApplication claimApplicationVO = claimApplicationDAO.findByClaimApplicationID(claimDTO);
		
		if(claimApplicationVO==null){
			return null;
		}
		
		claimFormPdfDTO.setClaimTemplateName(
				claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
		claimFormPdfDTO.setEmployeeNumber(claimApplicationVO.getEmployee().getEmployeeNumber());
		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				claimFormPdfDTO.setClaimFormPdfByteFile(
						generateClaimFormPDF(claimDTO));
				return claimFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			try {
				claimFormPdfDTO.setClaimFormPdfByteFile(
						generateClaimFormPDF(claimDTO));
				return claimFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

	}

	private byte[] generateClaimFormPDF(AddClaimDTO claimDTO) throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(claimDTO.getEmployeeId(), PAYASIA_TEMP_PATH, "ClaimForm");
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

			PdfPTable claimReportPdfTable = claimFormPrintPDFLogic.createClaimReportPdf(document, writer, 1, claimDTO.getCompanyId(),
					claimDTO.getClaimApplicationId(), claimDTO.isHasLundinTimesheetModule());

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
	public String getSubmittedOnDate(Long claimApplicationId) {
		String claimAppSubmittedDate = "";
		ClaimApplication claimApplication = claimApplicationDAO.findByID(claimApplicationId);
		ClaimApplicationWorkflow empClaimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
				claimApplication.getClaimApplicationId(), claimApplication.getEmployee().getEmployeeId());
		claimAppSubmittedDate = empClaimApplicationWorkflow == null ? ""
				: DateUtils.timeStampToString(empClaimApplicationWorkflow.getCreatedDate());
		return claimAppSubmittedDate;
	}

	@Override
	public ClaimPreferenceForm getClaimPreferences(Long companyId) {
		ClaimPreferenceForm claimPreferenceForm = new ClaimPreferenceForm();
		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreferenceVO == null) {
			claimPreferenceForm.setDefaultEmailCC(false);
			return claimPreferenceForm;
		}
		claimPreferenceForm.setDefaultEmailCC(claimPreferenceVO.isDefaultEmailCC());
		claimPreferenceForm.setAdditionalBalanceFrom(claimPreferenceVO.getAdditionalBalanceFrom());
		claimPreferenceForm.setAdminCanAmendDataBeforeApproval(claimPreferenceVO.isAdminCanAmendDataBeforeApproval());
		claimPreferenceForm.setShowPaidStatusForClaimBatch(claimPreferenceVO.isShowPaidStatusForClaimBatch());
		return claimPreferenceForm;
	}

	private String getPaidDate(List<ClaimBatchMaster> claimMasters, Timestamp updatedDate) {
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
		return "";
	}

	@Override
	public ClaimApplicationItemAttach viewAttachment(AddClaimDTO addClaimDTO) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		ClaimApplicationItemAttachment claimApplicationItemAttachment = claimApplicationItemAttachmentDAO
				.findByID(addClaimDTO.getClaimApplicationItemAttachmentId());
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
	
	@Override
	public AddClaimForm getClaimAppDataMsgSource(AddClaimDTO claimDTO, MessageSource messageSource) {
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
		response.setIsApproved(isApproved);
		response.setVisibleToEmployee(claimApplicationVO.getVisibleToEmployee());
		response.setEmployeeClaimTemplateId(claimApplicationVO.getEmployeeClaimTemplate().getEmployeeClaimTemplateId());
		setClaimTemplateItemListMsgSource(response, claimApplicationVO, claimDTO, messageSource);
		response.setClaimTemplateName(claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
		response.setEmailCC(claimApplicationVO.getEmailCC());

		return response;
	}
	
	private void setClaimTemplateItemListMsgSource(AddClaimForm response, ClaimApplication claimApplication, AddClaimDTO claimDTO, MessageSource messageSource) {
		String user = "payasia.user";
		byte [] empImage= null;
		List<AddClaimForm> claimTemplateItemList = new ArrayList<AddClaimForm>();
		EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO
				.findByID(claimApplication.getEmployeeClaimTemplate().getEmployeeClaimTemplateId());

		claimDTO.setEmployeeClaimTemplateId(employeeClaimTemplate.getEmployeeClaimTemplateId());
		claimDTO.setAdmin(false);

		List<EmployeeClaimTemplateItem> employeeClaimTemplateItems = employeeClaimTemplateItemDAO
				.findByEmployeeClaimTemplateId(claimDTO);

		response.setEmployeeClaimTemplateItemId(employeeClaimTemplate.getEmployeeClaimTemplateId());
		response.setClaimTemplateName(employeeClaimTemplate.getClaimTemplate().getTemplateName());
		HashMap<Long, ClaimApplicationItemDTO> claimApplicationItems = new HashMap<>();
		List<ClaimApplicationItemDTO> claimApplicationItemsList = new ArrayList<>();
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
				claimItemDTO.setEmployeeClaimTemplateItemId(FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateItem.getEmployeeClaimTemplateItemId()));
				claimItemDTO.setClaimItemId(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemId());
				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO
						.findByItemId("" + employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());
				if (claimTemplateItemGeneralVO != null
						&& StringUtils.isNotEmpty(claimTemplateItemGeneralVO.getHelpText())) {

					claimItemDTO.setHelpText(claimTemplateItemGeneralVO.getHelpText());
				}

				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
				claimItems.add(claimItemDTO);
				claimApplicationItemDTO.setClaimItems(claimItems);
				claimApplicationItemDTO.setCategoryId(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryId());
				claimApplicationItemDTO.setCategoryName(employeeClaimTemplateItem.getClaimTemplateItem()
						.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
				claimApplicationItems.put(claimItemCategoryId, claimApplicationItemDTO);
				claimApplicationItemsList.add(claimApplicationItemDTO);

			} else {
				ClaimItemDTO claimItemDTO = new ClaimItemDTO();
				claimItemDTO.setEmployeeClaimTemplateItemId(FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateItem.getEmployeeClaimTemplateItemId()));
				claimItemDTO.setClaimItemId(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemId());
				claimItemDTO.setClaimItemName(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getClaimItemName());
				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO
						.findByItemId("" + employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId());
				if (claimTemplateItemGeneralVO != null
						&& StringUtils.isNotEmpty(claimTemplateItemGeneralVO.getHelpText())) {

					claimItemDTO.setHelpText(claimTemplateItemGeneralVO.getHelpText());
				}
				claimApplicationItems.get(claimItemCategoryId).getClaimItems().add(claimItemDTO);

			}

		}
//		response.setClaimApplicationItems(claimApplicationItems);
		response.setClaimApplicationItemsList(claimApplicationItemsList);
		response.setClaimTemplateItemList(claimTemplateItemList);
		List<EmployeeClaimReviewer> claimReviewers = new ArrayList<>(
				claimApplication.getEmployeeClaimTemplate().getEmployeeClaimReviewers());

		int totalNoOfReviewers = 0;
		if (claimApplication.getClaimApplicationReviewers().size() == 0) {

			for (EmployeeClaimReviewer employeeClaimReviewer : claimReviewers) {

				totalNoOfReviewers++;
				if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					response.setClaimReviewer1(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setApplyTo(employeeDetailLogic.getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setApplyToId(employeeClaimReviewer.getEmployee2().getEmployeeId());

				} else if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					response.setClaimReviewer2(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setClaimReviewer2Id(employeeClaimReviewer.getEmployee2().getEmployeeId());

				} else if (employeeClaimReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					response.setClaimReviewer3(getEmployeeName(employeeClaimReviewer.getEmployee2()));
					response.setClaimReviewer3Id(employeeClaimReviewer.getEmployee2().getEmployeeId());
				}

			}
		} else {

			for (ClaimApplicationReviewer claimApplicationReviewer : claimApplication.getClaimApplicationReviewers()) {

				totalNoOfReviewers++;
				if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					response.setClaimReviewer1(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setApplyTo(employeeDetailLogic.getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setApplyToId(claimApplicationReviewer.getEmployee().getEmployeeId());

				} else if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					response.setClaimReviewer2(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setClaimReviewer2Id(claimApplicationReviewer.getEmployee().getEmployeeId());

				} else if (claimApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					response.setClaimReviewer3(getEmployeeName(claimApplicationReviewer.getEmployee()));
					response.setClaimReviewer3Id(claimApplicationReviewer.getEmployee().getEmployeeId());
				}

			}
		}

		Timestamp claimAppSubmittedDate = null;
		HashMap<Long, ClaimApplicationWorkflow> workFlow = new HashMap<>();
		for (ClaimApplicationWorkflow claimApplicationWorkflow : claimApplication.getClaimApplicationWorkflows()) {

			if (claimApplicationWorkflow.getEmployee().getEmployeeId() == claimApplication.getEmployee()
					.getEmployeeId()) {
				claimAppSubmittedDate = claimApplicationWorkflow.getCreatedDate();
			}

			workFlow.put(claimApplicationWorkflow.getEmployee().getEmployeeId(), claimApplicationWorkflow);

		}
		Set<ClaimApplicationReviewer> claimApplicationReviewerVOs = claimApplication.getClaimApplicationReviewers();
		List<ClaimApplicationWorkflowForm> claimWorkflows = new ArrayList<>();

		ClaimApplicationWorkflowForm appClaimWorkFlowForm = new ClaimApplicationWorkflowForm();

		appClaimWorkFlowForm.setRemarks(claimApplication.getRemarks());

		boolean previousStatus = false;
		String previousStatusName = "name";
		
		if (claimApplication.getClaimStatusMaster().getClaimStatusName()
				.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
			appClaimWorkFlowForm.setStatusName(claimApplication.getClaimStatusMaster().getClaimStatusName());
			previousStatus = true;
			if (StringUtils.isNotBlank(claimApplication.getClaimStatusMaster().getLabelKey())) {
				appClaimWorkFlowForm.setStatusNameLocale(messageSource.getMessage(
						claimApplication.getClaimStatusMaster().getLabelKey(), new Object[] {}, claimDTO.getLocale()));
			} else {

				appClaimWorkFlowForm.setStatusNameLocale(claimApplication.getClaimStatusMaster().getClaimStatusName());
			}
		} else {
			appClaimWorkFlowForm.setStatusName(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
			if (StringUtils.isNotBlank(claimApplication.getClaimStatusMaster().getLabelKey())) {
				ClaimStatusMaster claimStatusMaster = claimstatusMasterDAO
						.findByCondition(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				
				//String msg = messageSource.getMessage(claimStatusMaster.getLabelKey(),new Object[] {}, UserContext.getLocale());
				appClaimWorkFlowForm.setStatusNameLocale(claimStatusMaster.getClaimStatusName());
			} else {
				appClaimWorkFlowForm.setStatusNameLocale(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
			}

		}
	

		appClaimWorkFlowForm.setCreatedDate(
				claimAppSubmittedDate == null ? DateUtils.timeStampToString(claimApplication.getCreatedDate())
						: DateUtils.timeStampToString(claimAppSubmittedDate));
		appClaimWorkFlowForm.setCreateDateM(claimAppSubmittedDate);
		if (StringUtils.isNotBlank(UserContext.getDevice()) && !UserContext.getDevice().equalsIgnoreCase("WEB BROWSER")) {
			try {
			
				empImage = employeeDetailLogic.getEmployeeImage(claimApplication.getEmployee().getEmployeeId(), null, employeeImageWidth, employeeImageHeight);
	
			} catch (IOException e) {
				e.printStackTrace();
			}
			appClaimWorkFlowForm.setEmpImage(empImage);
		}
		appClaimWorkFlowForm.setWorkflowRule(messageSource.getMessage(user, new Object[] {}, claimDTO.getLocale()));
		appClaimWorkFlowForm.setRemarks(claimApplication.getRemarks());
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
				previousStatusName = claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName();
				
				if(previousStatus){
					claimApplicationWorkflowForm.setStatusName("");
				}
				
				if(StringUtils.equalsIgnoreCase(previousStatusName, "Rejected") || StringUtils.equalsIgnoreCase(previousStatusName, "Withdrawn") || StringUtils.equalsIgnoreCase(previousStatusName, "Completed")){
					claimApplicationWorkflowForm.setStatusName(claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName());
					previousStatus = true;
				}
				
			   if(previousStatus == false){
				   
				    String status =  claimApplicationWorkflow.getClaimStatusMaster()!=null && claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName()!=null ? claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName():PayAsiaConstants.CLAIM_STATUS_PENDING;
				    claimApplicationWorkflowForm.setStatusName(status);
				}
			   

				if (StringUtils.isNotBlank(claimApplicationWorkflow.getClaimStatusMaster().getLabelKey())) {
					claimApplicationWorkflowForm.setStatusNameLocale(
							messageSource.getMessage(claimApplicationWorkflow.getClaimStatusMaster().getLabelKey(),
									new Object[] {}, claimDTO.getLocale()));
				} else {
					claimApplicationWorkflowForm
							.setStatusNameLocale(claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName());
				}

				claimApplicationWorkflowForm
						.setCreatedDate(DateUtils.timeStampToStringWithTime(claimApplicationWorkflow.getCreatedDate()));
				claimApplicationWorkflowForm.setCreateDateM(claimApplicationWorkflow.getCreatedDate());
			}
			else{
				if (previousStatus == true) {
					claimApplicationWorkflowForm.setStatusName("");
				}
				else{
					claimApplicationWorkflowForm.setStatusName(PayAsiaConstants.CLAIM_STATUS_PENDING);
				}
			}

			if (StringUtils.isNotBlank(claimApplicationReviewer.getWorkFlowRuleMaster().getLabelKey())) {
				claimApplicationWorkflowForm.setWorkflowRule(
						messageSource.getMessage(claimApplicationReviewer.getWorkFlowRuleMaster().getLabelKey(),
								new Object[] {}, claimDTO.getLocale()) + revCount);
			} else {
				claimApplicationWorkflowForm
						.setWorkflowRule(claimApplicationReviewer.getWorkFlowRuleMaster().getRuleName() + revCount);
			}
			claimApplicationWorkflowForm.setEmpName(getEmployeeName(claimApplicationReviewer.getEmployee()));
		
			if (StringUtils.isNotBlank(UserContext.getDevice()) && !UserContext.getDevice().equalsIgnoreCase("WEB BROWSER")) {
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
	
	
	@Override
	public ClaimDetailsReportDTO showClaimTransactionReport(Long companyId, ClaimReportsForm claimReportsForm,
			Long employeeId, String[] dataDictionaryIds, boolean hasLundinTimesheetModule, Locale locale, Boolean isCheckedFromCreatedDate) {
		
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
		claimApprovededForwardDate.setsTitle(messageSource.getMessage(claimApprovedForwardDateLocale, new Object[] {}, locale));
		
		ClaimReportHeaderDTO claimApprovedForwarded2Date = new ClaimReportHeaderDTO();
		claimApprovedForwarded2Date.setmDataProp("claimApprovedForwarded2Date");
		claimApprovedForwarded2Date.setsTitle(messageSource.getMessage(claimApprovedForward2DateLocale, new Object[] {}, locale));
		
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
	
		
		if(claimReportsForm.isIncludeSubordinateEmployees()){
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
						claimReportsForm.getGroupByName(), employeeId,claimReportsForm.isIncludeResignedEmployees(),claimReportsForm.isIncludeSubordinateEmployees()
						,isCheckedFromCreatedDate);
			} else if (StringUtils.isNotBlank(claimReportsForm.getStatusName())
					&& claimReportsForm.getStatusName().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByClaimReviewerCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), employeeId, claimReportsForm.isIncludeResignedEmployees(),claimReportsForm.isIncludeSubordinateEmployees()
						,isCheckedFromCreatedDate);
			} else if (StringUtils.isNotBlank(claimReportsForm.getStatusName())
					&& claimReportsForm.getStatusName().equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
				claimApplicationItemVOList = claimApplicationItemDAO.findByClaimReviewerCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), employeeId,claimReportsForm.isIncludeResignedEmployees(),claimReportsForm.isIncludeSubordinateEmployees()
						,isCheckedFromCreatedDate);
			} else {
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
				claimStatusList.add(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);

				List<ClaimApplicationItem> claimApplicationItemVO1List = new ArrayList<ClaimApplicationItem>();
				claimApplicationItemVO1List = claimApplicationItemDAO.findByClaimReviewerCondition(companyId,
						claimReportsForm.getStartDate(), claimReportsForm.getEndDate(), multipleClaimTemplateIdList,
						claimReportsForm.getClaimCategoryId(), multipleClaimItemIdList, claimStatusList,
						claimReportsForm.getGroupByName(), employeeId,claimReportsForm.isIncludeResignedEmployees(),claimReportsForm.isIncludeSubordinateEmployees()
						,isCheckedFromCreatedDate);

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
		
		//DecimalFormat thousandSeperator2 = new DecimalFormat("###,###.00000000");

		for (ClaimApplicationItem ClaimAppItemsVO : claimApplicationItemVOList) {

			List<ClaimApplicationWorkflow> claimApplicationWorkflowList = claimApplicationWorkflowDAO.findWorkFlowByClaimAppId(
					ClaimAppItemsVO.getClaimApplication().getClaimApplicationId());

		
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

			if(claimApplicationWorkflowList != null){
				int counter = 0;
				for(ClaimApplicationWorkflow claimAppWorkflow : claimApplicationWorkflowList){
					if(claimAppWorkflow.getClaimStatusMaster() != null){
						
						switch(claimAppWorkflow.getClaimStatusMaster().getClaimStatusName()){
						
								case PayAsiaConstants.CLAIM_STATUS_COMPLETED:
									 	claimReportDataDTO.setClaimApprovedDate(DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
									 	break;
								case PayAsiaConstants.CLAIM_STATUS_REJECTED:
										claimReportDataDTO.setRejectedDate(DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
										break;
								case PayAsiaConstants.CLAIM_STATUS_SUBMITTED:
										claimReportDataDTO.setClaimSubmittedDate(DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
										break;
								case PayAsiaConstants.CLAIM_STATUS_APPROVED:
									
									if(counter==0){
										claimReportDataDTO.setClaimApprovedForwardedDate(DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
									}else{
										claimReportDataDTO.setClaimApprovedForwarded2Date(DateUtils.timeStampToString(claimAppWorkflow.getCreatedDate()));
									}
									counter++;
									break;
								default :
									LOGGER.error("There is no claim transaction..");
						}
					}
				}
			}
			
			claimReportDataDTO.setClaimCreatedDate(DateUtils.timeStampToString(ClaimAppItemsVO.getClaimApplication().getCreatedDate()));
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
			if(claimReportsForm.isIncludeSubordinateEmployees()){
				claimReportDataDTO.setCompanyCodes(ClaimAppItemsVO.getClaimApplication().getCompany().getCompanyCode());
			}

			if (ClaimAppItemsVO.getExchangeRate() != null) {
				claimReportDataDTO.setExchangeRate(ClaimAppItemsVO.getExchangeRate().toString());
				
			} else {
				claimReportDataDTO.setExchangeRate("");
			}
			
			if (ClaimAppItemsVO.getForexReceiptAmount() != null) {
				claimReportDataDTO.setReceiptAmount(thousandSeperator.format(ClaimAppItemsVO.getForexReceiptAmount().doubleValue()));
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
		BeanComparator beanComparator = new BeanComparator("employeeNo");
		Collections.sort(claimDetailsDataDTOs, beanComparator);
		claimDetailsReportDTO.setClaimDetailsDataDTOs(claimDetailsDataDTOs);
		claimDetailsReportDTO.setClaimHeaderDTOs(claimHeaderDTOs);
		claimDetailsReportDTO.setClaimDetailsCustomDataDTOs(customDataDTOs);
		if(claimReportsForm.isIncludeSubordinateEmployees()){
			claimDetailsReportDTO.setSubordinateCompanyEmployee(claimReportsForm.isIncludeSubordinateEmployees());
		}
		return claimDetailsReportDTO;
	}

	private void Switch(String claimStatusName) {
		// TODO Auto-generated method stub
		
	}
	
}
