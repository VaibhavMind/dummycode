package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.ValidationClaimItemDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.ImportEmployeeClaimForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.ImportEmployeeClaimDTO;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimApplicationItemCustomFieldDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.ClaimApplicationItemLundinDetailDAO;
import com.payasia.dao.ClaimApplicationReviewerDAO;
import com.payasia.dao.ClaimApplicationWorkflowDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CurrencyMasterDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.LundinAFEDAO;
import com.payasia.dao.LundinBlockDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemCustomField;
import com.payasia.dao.bean.ClaimApplicationItemLundinDetail;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.ClaimStatusMaster;
import com.payasia.dao.bean.ClaimTemplateItemClaimType;
import com.payasia.dao.bean.ClaimTemplateItemCustomField;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CurrencyMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LundinAFE;
import com.payasia.dao.bean.LundinBlock;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.ClaimFormPrintPDFLogic;
import com.payasia.logic.EmployeeClaimsLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class EmployeeClaimsLogicImpl extends BaseLogic implements EmployeeClaimsLogic {
	private static final Logger LOGGER = Logger.getLogger(EmployeeClaimsLogicImpl.class);
	@Autowired
	private MessageSource messageSource;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	ClaimApplicationDAO claimApplicationDAO;
	@Resource
	ClaimApplicationWorkflowDAO claimApplicationWorkflowDAO;
	@Resource
	ClaimApplicationReviewerDAO claimApplicationReviewerDAO;
	@Resource
	ClaimStatusMasterDAO claimStatusMasterDAO;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	@Resource
	ClaimFormPrintPDFLogic claimFormPrintPDFLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;

	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;

	@Resource
	ClaimApplicationItemDAO claimApplicationItemDAO;

	@Resource
	ClaimApplicationItemCustomFieldDAO claimApplicationItemCustomFieldDAO;

	@Resource
	CurrencyMasterDAO currencyMasterDAO;

	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	LundinBlockDAO lundinBlockDAO;
	@Resource
	LundinAFEDAO lundinAFEDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	ClaimApplicationItemLundinDetailDAO claimApplicationItemLundinDetailDAO;

	@Override
	public List<EmployeeListForm> getEmployeeId(Long companyId, String searchString, Long employeeId) {
		List<EmployeeListForm> empListFormList = new ArrayList<EmployeeListForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(searchString.trim(), companyId,
				employeeShortListDTO);

		for (Employee employee : employeeNumberList) {
			EmployeeListForm empListForm = new EmployeeListForm();
			empListForm.setEmployeeNumber(employee.getEmployeeNumber());
			String empName = "";
			try {
				empName += URLEncoder.encode(employee.getFirstName(), "UTF-8") + " ";
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			if (StringUtils.isNotBlank(employee.getLastName())) {
				try {
					empName += URLEncoder.encode(employee.getLastName(), "UTF-8") + " ";
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}

			}
			empListForm.setEmployeeName(empName);
			empListForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			empListFormList.add(empListForm);

		}
		return empListFormList;
	}

	@Override
	public AddClaimFormResponse getPendingClaims(String fromDate, String toDate, Long sessionEmpId, PageRequest pageDTO,
			SortCondition sortDTO, String transactionType, String employeeNumber, Long companyId) {
		AddClaimFormResponse response = new AddClaimFormResponse();
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		if (employeeVO == null) {
			return response;
		}

		/* Check employee shortlist */
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmpId, companyId);
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		if (employeeShortListDTO.getEmployeeShortList()
				&& !companyShortListEmployeeIds.contains(BigInteger.valueOf(employeeVO.getEmployeeId()))) {
			return response;
		}

		ArrayList<String> roleList = new ArrayList<>();
		ArrayList<String> claimTemplatePrivilegeList = new ArrayList<>();
		if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
			// ADD Claim Template Roles
			getGlencoresgRolesList(roleList);
			// ADD Claim Template Privilege
			getClaimTemplatePrivilegeList(roleList, claimTemplatePrivilegeList);

		}

		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_DRAFT);

		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		Boolean visibleToEmployee = null;
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByConditionForAdmin(pageDTO, sortDTO,
				employeeVO.getEmployeeId(), claimStatus, fromDate, toDate, visibleToEmployee,
				claimPreferenceForm.getClaimNumberSortOrder(), claimPreferenceForm.getCreatedDateSortOrder());

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {

			// For Glencoresg Company
			if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
				if (!claimTemplatePrivilegeList.isEmpty() && !claimTemplatePrivilegeList.isEmpty()
						&& !claimTemplatePrivilegeList.contains(
								claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName())) {
					continue;
				}

				if (!roleList.isEmpty()
						&& !claimApplication.getCreatedBy().equalsIgnoreCase(String.valueOf(sessionEmpId))) {
					continue;
				}
			}

			AddClaimForm addClaimForm = new AddClaimForm();

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			claimTemplate.append("<br>");
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editClaimApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>[Edit]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));

			StringBuilder claimWithDrawLink = new StringBuilder();
			claimWithDrawLink.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplicationById("
					+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>Delete</a>");

			addClaimForm.setAction(String.valueOf(claimWithDrawLink));

			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(claimApplication.getClaimApplicationId());

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
					+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>" + claimApplication.getTotalItems() + "</a>");

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			List<EmployeeClaimReviewer> empApplicationReviewers = new ArrayList<>(
					claimApplication.getEmployeeClaimTemplate().getEmployeeClaimReviewers());
			getPendingClaimReviewers(empApplicationReviewers, addClaimForm);
			addClaimFormList.add(addClaimForm);
		}

		response.setAddClaimFormList(addClaimFormList);

		return response;
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

	private void getGlencoresgRolesList(ArrayList<String> roleList) {
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
			if (grantedAuthority.getAuthority().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_SECRETARY)) {
				roleList.add(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_SECRETARY);
			}
		}
	}

	private void getClaimTemplatePrivilegeList(ArrayList<String> roleList,
			ArrayList<String> claimTemplatePrivilegeList) {
		// ADD Claim Template Privilege
		if (roleList.contains(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_MEDICAL_AND_AD_HOC)) {
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_MEDICAL_CLAIMS);
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_Ad_Hoc);
		}
		if (roleList.contains(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_AD_HOC)) {
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_Ad_Hoc);
		}
		if (roleList.contains(PayAsiaConstants.GLENCORESG_COMPANY_ROLE_SECRETARY)) {
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_LOCAL_EXPENSES);
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_OVERSEAS_EXPENSES);
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_LOCAL_EXPENSES_2LR);
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_OVERSEAS_EXPENSES_2LR);
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_LOCAL_EXPENSES_3LR);
			claimTemplatePrivilegeList.add(PayAsiaConstants.GLENCORESG_COMPANY_CLAIM_TEMPLATE_OVERSEAS_EXPENSES_3LR);
		}
	}

	@Override
	public void getPendingClaimReviewers(List<EmployeeClaimReviewer> empClaimReviewers, AddClaimForm addClaimForm) {
		Collections.sort(empClaimReviewers, new EmployeeClaimReviewerComp());
		for (EmployeeClaimReviewer employeeClaimReviewer : empClaimReviewers) {
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
	}

	/**
	 * Comparator Class for Ordering claimApplicationWorkflow List
	 */
	private class EmployeeReviewerComp implements Comparator<ClaimApplicationReviewer> {
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

	private class EmployeeClaimReviewerComp implements Comparator<EmployeeClaimReviewer> {
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
	public AddClaimFormResponse getApprovedClaims(String fromDate, String toDate, Long sessionEmpId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath, String transactionType,
			String employeeNumber, Long companyId) {
		AddClaimFormResponse response = new AddClaimFormResponse();
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		if (employeeVO == null) {
			return response;
		}

		/* Check employee shortlist */
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmpId, companyId);
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		if (employeeShortListDTO.getEmployeeShortList()
				&& !companyShortListEmployeeIds.contains(BigInteger.valueOf(employeeVO.getEmployeeId()))) {
			return response;
		}

		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);

		ArrayList<String> roleList = new ArrayList<>();
		ArrayList<String> claimTemplatePrivilegeList = new ArrayList<>();
		if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
			// ADD Claim Template Roles
			getGlencoresgRolesList(roleList);

			// ADD Claim Template Privilege
			getClaimTemplatePrivilegeList(roleList, claimTemplatePrivilegeList);
		}

		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		Boolean visibleToEmployee = null;
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByConditionForAdmin(pageDTO, sortDTO,
				employeeVO.getEmployeeId(), claimStatus, fromDate, toDate, visibleToEmployee,
				claimPreferenceForm.getClaimNumberSortOrder(), claimPreferenceForm.getCreatedDateSortOrder());

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {

			// For Glencoresg Company
			if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
				if (!claimTemplatePrivilegeList.isEmpty() && !claimTemplatePrivilegeList
						.contains(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName())) {
					continue;
				}

				if (!roleList.isEmpty()
						&& !claimApplication.getCreatedBy().equalsIgnoreCase(String.valueOf(sessionEmpId))) {
					continue;
				}
			}

			AddClaimForm addClaimForm = new AddClaimForm();

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
					+ claimApplication.getClaimApplicationId() + ");'>" + claimApplication.getTotalItems() + "</a>");

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"approved\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>[View]</a></span>");

			if (claimApplication.getVisibleToEmployee() != null && !claimApplication.getVisibleToEmployee()) {
				claimTemplate.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplicationById("
						+ claimApplication.getClaimApplicationId() + ");'> | [Delete]</a>");
			}

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(claimApplication.getClaimApplicationId());

			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());
			getApprovedClaimReviewers(claimApplicationReviewers, addClaimForm, pageContextPath, claimApplication);
			addClaimFormList.add(addClaimForm);
		}

		response.setAddClaimFormList(addClaimFormList);

		return response;
	}

	@Override
	public void getApprovedClaimReviewers(List<ClaimApplicationReviewer> claimApplicationReviewers,
			AddClaimForm addClaimForm, String pageContextPath, ClaimApplication claimApplication) {
		Collections.sort(claimApplicationReviewers, new EmployeeReviewerComp());
		int revCount = 1;
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			if (revCount == 1) {

				ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());
				StringBuilder ClaimReviewer1 = new StringBuilder(getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
						pageContextPath, claimApplicationReviewer.getEmployee()));

//				ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//						+ applicationWorkflow.getCreatedDate() + "</span>");

				addClaimForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

			}
			if (revCount == 2) {

				ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());
				if (applicationWorkflow == null) {
					continue;
				}

				StringBuilder ClaimReviewer2 = new StringBuilder(getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
						pageContextPath, claimApplicationReviewer.getEmployee()));

//				if (applicationWorkflow.getCreatedDate() != null) {
//					ClaimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//							+ applicationWorkflow.getCreatedDate() + "</span>");
//				}

				addClaimForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

			}
			if (revCount == 3) {

				ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());
				if (applicationWorkflow == null) {
					continue;
				}

				StringBuilder ClaimReviewer3 = new StringBuilder(getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED,
						pageContextPath, claimApplicationReviewer.getEmployee()));

//				ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//						+ applicationWorkflow.getCreatedDate() + "</span>");

				addClaimForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

			}
			revCount++;
		}
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
	public AddClaimFormResponse getRejectedClaims(String fromDate, String toDate, Long sessionEmpId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath, String transactionType,
			String employeeNumber, Long companyId) {
		AddClaimFormResponse response = new AddClaimFormResponse();
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		if (employeeVO == null) {
			return response;
		}

		/* Check employee shortlist */
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmpId, companyId);
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		if (employeeShortListDTO.getEmployeeShortList()
				&& !companyShortListEmployeeIds.contains(BigInteger.valueOf(employeeVO.getEmployeeId()))) {
			return response;
		}

		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
		ArrayList<String> roleList = new ArrayList<>();
		ArrayList<String> claimTemplatePrivilegeList = new ArrayList<>();
		if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {

			// ADD Claim Template Roles
			getGlencoresgRolesList(roleList);

			// ADD Claim Template Privilege
			getClaimTemplatePrivilegeList(roleList, claimTemplatePrivilegeList);
		}

		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		Boolean visibleToEmployee = null;
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByConditionForAdmin(pageDTO, sortDTO,
				employeeVO.getEmployeeId(), claimStatus, fromDate, toDate, visibleToEmployee,
				claimPreferenceForm.getClaimNumberSortOrder(), claimPreferenceForm.getCreatedDateSortOrder());

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {

			// For Glencoresg Company
			if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
				if (!claimTemplatePrivilegeList.isEmpty() && !claimTemplatePrivilegeList
						.contains(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName())) {
					continue;
				}

				if (!roleList.isEmpty()
						&& !claimApplication.getCreatedBy().equalsIgnoreCase(String.valueOf(sessionEmpId))) {
					continue;
				}
			}

			AddClaimForm addClaimForm = new AddClaimForm();

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
					+ claimApplication.getClaimApplicationId() + ");'>" + claimApplication.getTotalItems() + "</a>");

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"rejected\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>[View]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(claimApplication.getClaimApplicationId());
			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());

			getRejectedClaimReviewers(claimApplicationReviewers, addClaimForm, pageContextPath, claimApplication);

			addClaimFormList.add(addClaimForm);
		}

		response.setAddClaimFormList(addClaimFormList);

		return response;
	}

	@Override
	public void getRejectedClaimReviewers(List<ClaimApplicationReviewer> claimApplicationReviewers,
			AddClaimForm addClaimForm, String pageContextPath, ClaimApplication claimApplication) {

		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(claimApplicationReviewers, new EmployeeReviewerComp());
		int revCount = 1;
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			if (revCount == 1) {

				ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());

				if (applicationWorkflow == null) {

					addClaimForm.setClaimReviewer1(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
							pageContextPath, claimApplicationReviewer.getEmployee()));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
									claimApplicationReviewer.getEmployee()));

//					ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//							+ applicationWorkflow.getCreatedDate() + "</span>");

					addClaimForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					StringBuilder ClaimReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED, pageContextPath,
									claimApplicationReviewer.getEmployee()));

//					ClaimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//							+ applicationWorkflow.getCreatedDate() + "</span>");

					addClaimForm.setClaimReviewer1(String.valueOf(ClaimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

				}
			}
			if (revCount == 2) {

				ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());

				if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addClaimForm.setClaimReviewer2(
							getStatusImage("NA", pageContextPath, claimApplicationReviewer.getEmployee()));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {

						addClaimForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, claimApplicationReviewer.getEmployee()));
						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer2 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										claimApplicationReviewer.getEmployee()));

//						ClaimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ applicationWorkflow.getCreatedDate() + "</span>");

						addClaimForm.setClaimReviewer2(String.valueOf(ClaimReviewer2));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						addClaimForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED,
								pageContextPath, claimApplicationReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_REJECTED;

					}

				}

			}
			if (revCount == 3) {
				ClaimApplicationWorkflow applicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());
				if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)
						|| reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

					addClaimForm.setClaimReviewer3(
							getStatusImage("NA", pageContextPath, claimApplicationReviewer.getEmployee()));

				} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (applicationWorkflow == null) {
						addClaimForm.setClaimReviewer3(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, claimApplicationReviewer.getEmployee()));
					} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										claimApplicationReviewer.getEmployee()));

//						ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ applicationWorkflow.getCreatedDate() + "</span>");

						addClaimForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

					} else if (applicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {

						StringBuilder ClaimReviewer3 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_REJECTED, pageContextPath,
										claimApplicationReviewer.getEmployee()));

//						ClaimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ applicationWorkflow.getCreatedDate() + "</span>");

						addClaimForm.setClaimReviewer3(String.valueOf(ClaimReviewer3));

					}

				}

			}
			revCount++;
		}
	}

	@Override
	public AddClaimFormResponse getWithdrawnClaims(String fromDate, String toDate, Long sessionEmpId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath, String transactionType,
			String employeeNumber, Long companyId) {
		AddClaimFormResponse response = new AddClaimFormResponse();
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		if (employeeVO == null) {
			return response;
		}

		/* Check employee shortlist */
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmpId, companyId);
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		if (employeeShortListDTO.getEmployeeShortList()
				&& !companyShortListEmployeeIds.contains(BigInteger.valueOf(employeeVO.getEmployeeId()))) {
			return response;
		}

		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN);
		ArrayList<String> roleList = new ArrayList<>();
		ArrayList<String> claimTemplatePrivilegeList = new ArrayList<>();
		if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
			// ADD Claim Template Roles
			getGlencoresgRolesList(roleList);

			// ADD Claim Template Privilege
			getClaimTemplatePrivilegeList(roleList, claimTemplatePrivilegeList);
		}

		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		Boolean visibleToEmployee = null;
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByConditionForAdmin(pageDTO, sortDTO,
				employeeVO.getEmployeeId(), claimStatus, fromDate, toDate, visibleToEmployee,
				claimPreferenceForm.getClaimNumberSortOrder(), claimPreferenceForm.getCreatedDateSortOrder());

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {

			// For Glencoresg Company
			if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
				if (!claimTemplatePrivilegeList.isEmpty() && !claimTemplatePrivilegeList
						.contains(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName())) {
					continue;
				}
				if (!roleList.isEmpty()
						&& !claimApplication.getCreatedBy().equalsIgnoreCase(String.valueOf(sessionEmpId))) {
					continue;
				}
			}

			AddClaimForm addClaimForm = new AddClaimForm();

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
					+ claimApplication.getClaimApplicationId() + ");'>" + claimApplication.getTotalItems() + "</a>");

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"withdrawn\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>[View]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(claimApplication.getClaimApplicationId());

			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());

			getWithdrawClaimReviewers(claimApplicationReviewers, addClaimForm);

			addClaimFormList.add(addClaimForm);
		}

		response.setAddClaimFormList(addClaimFormList);

		return response;
	}

	@Override
	public void getWithdrawClaimReviewers(List<ClaimApplicationReviewer> claimApplicationReviewers,
			AddClaimForm addClaimForm) {
		Collections.sort(claimApplicationReviewers, new EmployeeReviewerComp());
		int revCount = 1;
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			if (revCount == 1) {

				addClaimForm.setClaimReviewer1(getEmployeeName(claimApplicationReviewer.getEmployee()));

			}
			if (revCount == 2) {

				addClaimForm.setClaimReviewer2(getEmployeeName(claimApplicationReviewer.getEmployee()));

			}
			if (revCount == 3) {

				addClaimForm.setClaimReviewer3(getEmployeeName(claimApplicationReviewer.getEmployee()));

			}
			revCount++;
		}
	}

	@Override
	public AddClaimFormResponse getSubmittedClaims(String fromDate, String toDate, Long sessionEmpId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath, String transactionType,
			String employeeNumber, Long companyId) {
		AddClaimFormResponse response = new AddClaimFormResponse();
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		if (employeeVO == null) {
			return response;
		}

		/* Check employee shortlist */
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmpId, companyId);
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		if (employeeShortListDTO.getEmployeeShortList()
				&& !companyShortListEmployeeIds.contains(BigInteger.valueOf(employeeVO.getEmployeeId()))) {
			return response;
		}

		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);

		ArrayList<String> roleList = new ArrayList<>();
		ArrayList<String> claimTemplatePrivilegeList = new ArrayList<>();
		if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
			// ADD Claim Template Roles
			getGlencoresgRolesList(roleList);

			// ADD Claim Template Privilege
			getClaimTemplatePrivilegeList(roleList, claimTemplatePrivilegeList);
		}

		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		Boolean visibleToEmployee = null;
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByConditionForAdmin(pageDTO, sortDTO,
				employeeVO.getEmployeeId(), claimStatus, fromDate, toDate, visibleToEmployee,
				claimPreferenceForm.getClaimNumberSortOrder(), claimPreferenceForm.getCreatedDateSortOrder());

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {

			// For Glencoresg Company
			if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
				if (!claimTemplatePrivilegeList.isEmpty() && !claimTemplatePrivilegeList
						.contains(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName())) {
					continue;
				}
				if (!roleList.isEmpty()
						&& !claimApplication.getCreatedBy().equalsIgnoreCase(String.valueOf(sessionEmpId))) {
					continue;
				}
			}

			AddClaimForm addClaimForm = new AddClaimForm();

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
					+ claimApplication.getClaimApplicationId() + ");'>" + claimApplication.getTotalItems() + "</a>");

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);

			StringBuilder claimTemplate = new StringBuilder();
			claimTemplate.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			claimTemplate.append("<br>");
			String claimStatusMode = ",\"submitted\"";
			claimTemplate
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>[View]</a></span>");

			addClaimForm.setClaimTemplateName(String.valueOf(claimTemplate));
			if (claimApplication.getCreatedBy() != null) {
				if (claimApplication.getCreatedBy().equals(String.valueOf(sessionEmpId))) {
					StringBuilder claimWithDrawLink = new StringBuilder();
					claimWithDrawLink.append("<a class='alink' href='#' onClick='javascipt:withDrawClaimApplication("
							+ claimApplication.getClaimApplicationId() + ");'>Withdraw</a>");

					addClaimForm.setAction(String.valueOf(claimWithDrawLink));
				}
			}

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));

			addClaimForm.setClaimApplicationId(claimApplication.getClaimApplicationId());

			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());

			getSubmittedClaimReviewers(claimApplicationReviewers, addClaimForm, pageContextPath, claimApplication);

			addClaimFormList.add(addClaimForm);
		}

		response.setAddClaimFormList(addClaimFormList);

		return response;
	}

	@Override
	public void getSubmittedClaimReviewers(List<ClaimApplicationReviewer> claimApplicationReviewers,
			AddClaimForm addClaimForm, String pageContextPath, ClaimApplication claimApplication) {
		String reviewer1Status = "";
		String reviewer2Status = "";
		Collections.sort(claimApplicationReviewers, new EmployeeReviewerComp());
		int revCount = 1;
		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			if (revCount == 1) {

				ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());

				if (claimApplicationWorkflow == null) {

					addClaimForm.setClaimReviewer1(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
							pageContextPath, claimApplicationReviewer.getEmployee()));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName()
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					StringBuilder claimReviewer1 = new StringBuilder(
							getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
									claimApplicationReviewer.getEmployee()));

//					claimReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//							+ claimApplicationWorkflow.getCreatedDate() + "</span>");

					addClaimForm.setClaimReviewer1(String.valueOf(claimReviewer1));

					reviewer1Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

				}
			}
			if (revCount == 2) {

				ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());

				if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addClaimForm.setClaimReviewer2(
							getStatusImage("NA", pageContextPath, claimApplicationReviewer.getEmployee()));

					reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
				} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (claimApplicationWorkflow == null) {

						addClaimForm.setClaimReviewer2(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, claimApplicationReviewer.getEmployee()));
						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_PENDING;
					} else if (claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer2 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										claimApplicationReviewer.getEmployee()));

//						claimReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ claimApplicationWorkflow.getCreatedDate() + "</span>");

						addClaimForm.setClaimReviewer2(String.valueOf(claimReviewer2));

						reviewer2Status = PayAsiaConstants.CLAIM_STATUS_APPROVED;

					}

				}

			}
			if (revCount == 3) {
				ClaimApplicationWorkflow claimApplicationWorkflow = claimApplicationWorkflowDAO.findByCondition(
						claimApplication.getClaimApplicationId(),
						claimApplicationReviewer.getEmployee().getEmployeeId());
				if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_PENDING)) {

					addClaimForm.setClaimReviewer3(
							getStatusImage("NA", pageContextPath, claimApplicationReviewer.getEmployee()));

				} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

					if (claimApplicationWorkflow == null) {
						addClaimForm.setClaimReviewer3(getStatusImage(PayAsiaConstants.CLAIM_STATUS_PENDING,
								pageContextPath, claimApplicationReviewer.getEmployee()));
					} else if (claimApplicationWorkflow.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {

						StringBuilder claimReviewer3 = new StringBuilder(
								getStatusImage(PayAsiaConstants.CLAIM_STATUS_APPROVED, pageContextPath,
										claimApplicationReviewer.getEmployee()));

//						claimReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
//								+ claimApplicationWorkflow.getCreatedDate() + "</span>");

						addClaimForm.setClaimReviewer3(String.valueOf(claimReviewer3));

					}

				}

			}
			revCount++;
		}
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
	public AddClaimFormResponse getAllClaims(String fromDate, String toDate, Long sessionEmpId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String transactionType, String employeeNumber,
			Long companyId, Locale locale) {
		AddClaimFormResponse response = new AddClaimFormResponse();
		String edit = "payasia.edit";
		String delete = "payasia.delete";
		String view = "payasia.view";
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		if (employeeVO == null) {
			return response;
		}

		/* Check employee shortlist */
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmpId, companyId);
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();
		if (employeeShortListDTO.getEmployeeShortList()
				&& !companyShortListEmployeeIds.contains(BigInteger.valueOf(employeeVO.getEmployeeId()))) {
			return response;
		}

		ArrayList<String> roleList = new ArrayList<>();
		ArrayList<String> claimTemplatePrivilegeList = new ArrayList<>();
		if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
			// ADD Claim Template Roles
			getGlencoresgRolesList(roleList);

			// ADD Claim Template Privilege
			getClaimTemplatePrivilegeList(roleList, claimTemplatePrivilegeList);
		}
		List<String> claimStatus = new ArrayList<>();

		ClaimPreferenceForm claimPreferenceForm = getClaimGridSortOrder(companyId);
		Boolean visibleToEmployee = null;
		List<ClaimApplication> pendingClaims = claimApplicationDAO.findByConditionForAdmin(pageDTO, sortDTO,
				employeeVO.getEmployeeId(), claimStatus, fromDate, toDate, visibleToEmployee,
				claimPreferenceForm.getClaimNumberSortOrder(), claimPreferenceForm.getCreatedDateSortOrder());

		List<AddClaimForm> addClaimFormList = new ArrayList<AddClaimForm>();
		DecimalFormat df = new DecimalFormat("##.00");
		for (ClaimApplication claimApplication : pendingClaims) {

			// For Glencoresg Company
			if (employeeVO.getCompany().getCompanyCode().equalsIgnoreCase(PayAsiaConstants.GLENCORESG_COMPANY_CODE)) {
				if (!claimTemplatePrivilegeList.isEmpty() && !claimTemplatePrivilegeList
						.contains(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName())) {
					continue;
				}
				if (!roleList.isEmpty()
						&& !claimApplication.getCreatedBy().equalsIgnoreCase(String.valueOf(sessionEmpId))) {
					continue;
				}
			}

			AddClaimForm addClaimForm = new AddClaimForm();

			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				StringBuilder claimTemplateEdit = new StringBuilder();
				claimTemplateEdit
						.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
				claimTemplateEdit.append("<br>");
				claimTemplateEdit
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:editClaimApplication("
								+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>["
								+ messageSource.getMessage(edit, new Object[] {}, locale) + "]</a></span>");

				addClaimForm.setClaimTemplateName(String.valueOf(claimTemplateEdit));
				StringBuilder claimWithDrawLink = new StringBuilder();
				claimWithDrawLink.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplicationById("
						+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>"
						+ messageSource.getMessage(delete, new Object[] {}, locale) + "</a>");

				addClaimForm.setAction(String.valueOf(claimWithDrawLink));

				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_DRAFT);
			} else {
				StringBuilder claimTemplateView = new StringBuilder();
				claimTemplateView
						.append(claimApplication.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
				claimTemplateView.append("<br>");
				String claimStatusMode = ",\"submitted\"";

				if (claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {

					claimStatusMode = ",\"approved\"";
				} else if (claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
						|| claimApplication.getClaimStatusMaster().getClaimStatusName()
								.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
					claimStatusMode = ",\"submitted\"";
				} else if (claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
					claimStatusMode = ",\"Withdrawn\"";
				} else if (claimApplication.getClaimStatusMaster().getClaimStatusName()
						.equals(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED)) {
					claimStatusMode = ",\"Rejected\"";
				}

				claimTemplateView
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:viewClaimApplication("
								+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + claimStatusMode + ");'>["
								+ messageSource.getMessage(view, new Object[] {}, locale) + "]</a></span>");

				if (claimApplication.getVisibleToEmployee() != null && !claimApplication.getVisibleToEmployee()) {
					claimTemplateView.append("<a class='alink' href='#' onClick='javascipt:deleteClaimApplicationById("
							+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'> | ["
							+ messageSource.getMessage(delete, new Object[] {}, locale) + "]</a>");
				}

				addClaimForm.setClaimTemplateName(String.valueOf(claimTemplateView));
			}

			addClaimForm.setCreateDate(DateUtils.timeStampToString(claimApplication.getCreatedDate()));
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| claimApplication.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_SUBMITTED);
				if (claimApplication.getCreatedBy() != null) {
					if (claimApplication.getCreatedBy().equals(String.valueOf(sessionEmpId))) {
						StringBuilder claimWithDrawLink = new StringBuilder();
						claimWithDrawLink
								.append("<a class='alink' href='#' onClick='javascipt:withDrawClaimApplication("
										+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>Withdraw</a>");

						addClaimForm.setAction(String.valueOf(claimWithDrawLink));
					}
				}
			}
			addClaimForm.setClaimApplicationId(claimApplication.getClaimApplicationId());

			BigDecimal totalApplicableAmount = BigDecimal.ZERO;
			totalApplicableAmount = getTotalAmountApplicable(claimApplication, totalApplicableAmount);
			addClaimForm.setClaimAmount(new BigDecimal(df.format(totalApplicableAmount)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			claimTemplateItemCount.append("<a class='alink' href='#' onClick='javascipt:viewClaimTemplateItems("
					+ FormatPreserveCryptoUtil.encrypt(claimApplication.getClaimApplicationId()) + ");'>" + claimApplication.getTotalItems() + "</a>");

			addClaimForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			addClaimForm.setClaimNumber(claimApplication.getClaimNumber());
			List<ClaimApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
					claimApplication.getClaimApplicationReviewers());
			List<EmployeeClaimReviewer> empClaimReviewers = new ArrayList<>(
					claimApplication.getEmployeeClaimTemplate().getEmployeeClaimReviewers());

			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_DRAFT)) {
				getPendingClaimReviewers(empClaimReviewers, addClaimForm);
			}
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_APPROVED);
				getApprovedClaimReviewers(claimApplicationReviewers, addClaimForm, pageContextPath, claimApplication);
			}
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_APPROVED)
					|| claimApplication.getClaimStatusMaster().getClaimStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
				getSubmittedClaimReviewers(claimApplicationReviewers, addClaimForm, pageContextPath, claimApplication);
			}
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equals(PayAsiaConstants.CLAIM_STATUS_WITHDRAWN)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_WITHDRAWN);
				getWithdrawClaimReviewers(claimApplicationReviewers, addClaimForm);
			}
			if (claimApplication.getClaimStatusMaster().getClaimStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_REJECTED)) {
				addClaimForm.setStatus(PayAsiaConstants.CLAIM_LIST_STATUS_REJECTED);
				getRejectedClaimReviewers(claimApplicationReviewers, addClaimForm, pageContextPath, claimApplication);
			}

			addClaimFormList.add(addClaimForm);
		}

		response.setAddClaimFormList(addClaimFormList);

		return response;
	}

	public AddClaimForm rejectClaim(AddClaimForm addClaimForm, Long employeeId) {
		AddClaimForm response = new AddClaimForm();
		Employee loggedInEmp = employeeDAO.findById(employeeId);
		Date date = new Date();
		ClaimApplication claimApplicationVO = claimApplicationDAO.findByID(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));
		ClaimStatusMaster claimStatusMaster = claimStatusMasterDAO
				.findByCondition(PayAsiaConstants.CLAIM_STATUS_REJECTED);
		for (ClaimApplicationReviewer claimReviewer : claimApplicationVO.getClaimApplicationReviewers()) {

			if (claimReviewer.getPending()) {
				claimReviewer.setPending(false);
				claimReviewer.setEmployee(loggedInEmp);
				claimApplicationReviewerDAO.update(claimReviewer);
			}

		}

		claimApplicationVO.setClaimStatusMaster(claimStatusMaster);
		claimApplicationVO.setUpdatedDate(new Timestamp(date.getTime()));
		claimApplicationDAO.update(claimApplicationVO);

		Set<ClaimApplicationItem> claimApplicationItems = claimApplicationVO.getClaimApplicationItems();
		for (ClaimApplicationItem claimApplicationItem : claimApplicationItems) {
			ClaimApplicationItem applicationItem = claimApplicationItem;
			applicationItem.setActive(false);
			claimApplicationItemDAO.update(applicationItem);
		}

		ClaimApplicationWorkflow applicationWorkflow = new ClaimApplicationWorkflow();
		applicationWorkflow.setEmployee(loggedInEmp);
		applicationWorkflow.setClaimApplication(claimApplicationVO);
		applicationWorkflow.setClaimStatusMaster(claimStatusMaster);
		applicationWorkflow.setRemarks(addClaimForm.getRemarks());

		applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		claimApplicationWorkflowDAO.saveReturn(applicationWorkflow);

		return response;

	}

	@Override
	public AddClaimForm acceptClaim(AddClaimForm addClaimForm, Long employeeId) {

		AddClaimForm response = new AddClaimForm();

		Employee loggedInEmp = employeeDAO.findById(employeeId);
		Date date = new Date();
		ClaimApplication claimApplicationVO = claimApplicationDAO.findByID(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));

		// Validating claim application item.
		Boolean isAdmin = true;
		Boolean isManager = false;
		for (ClaimApplicationItem claimApplicationItem : claimApplicationVO.getClaimApplicationItems()) {
			ValidationClaimItemDTO validateClaimItemDTO = claimApplicationItemDAO
					.validateClaimItem(claimApplicationItem, isAdmin, isManager);
			if (validateClaimItemDTO.getErrorCode() == 1) {
				response.setValidationClaimItemDTO(validateClaimItemDTO);
				return response;

			}
		}

		ClaimStatusMaster claimStatusCompleted = claimStatusMasterDAO
				.findByCondition(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		for (ClaimApplicationReviewer claimReviewer : claimApplicationVO.getClaimApplicationReviewers()) {

			if (claimReviewer.getPending()) {
				claimReviewer.setPending(false);
				claimReviewer.setEmployee(loggedInEmp);
				claimApplicationReviewerDAO.update(claimReviewer);
			}

		}

		claimApplicationVO.setClaimStatusMaster(claimStatusCompleted);

		claimApplicationVO.setUpdatedDate(new Timestamp(date.getTime()));
		claimApplicationDAO.update(claimApplicationVO);

		ClaimApplicationWorkflow claimApplicationWorkflow = new ClaimApplicationWorkflow();
		claimApplicationWorkflow.setEmployee(loggedInEmp);
		claimApplicationWorkflow.setClaimApplication(claimApplicationVO);
		claimApplicationWorkflow.setClaimStatusMaster(claimStatusCompleted);
		claimApplicationWorkflow.setTotalAmount(claimApplicationVO.getTotalAmount());
		if (StringUtils.isNotBlank(addClaimForm.getRemarks())) {
			try {
				claimApplicationWorkflow.setRemarks(URLDecoder.decode(addClaimForm.getRemarks(), "UTF-8"));
			} catch (UnsupportedEncodingException exception) {
				claimApplicationWorkflow.setRemarks("");
				LOGGER.error(exception.getMessage(), exception);
			}
		}
		claimApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		claimApplicationWorkflowDAO.saveReturn(claimApplicationWorkflow);

		return response;

	}

	@Override
	public String isPayAsiaAdminCanApprove(Long companyId) {
		ClaimPreference claimPreference = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreference != null) {
			if (claimPreference.isPayAsiaAdminCanApprove()) {
				return "true";
			} else {
				return "false";
			}
		}
		return "false";

	}

	private class ClaimAppWorkFlowComp implements Comparator<ClaimApplicationWorkflow> {

		@Override
		public int compare(ClaimApplicationWorkflow templateField, ClaimApplicationWorkflow compWithTemplateField) {
			if (templateField.getClaimApplicationWorkflowId() > compWithTemplateField.getClaimApplicationWorkflowId()) {
				return 1;
			} else if (templateField.getClaimApplicationWorkflowId() < compWithTemplateField
					.getClaimApplicationWorkflowId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public String isAdminCanEditClaimBeforeApproval(Long claimApplicationId, Long companyId) {
		boolean isAdminCanAmendDataBeforeApproval = false;
		ClaimPreference claimPreference = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreference != null && claimPreference.isAdminCanAmendDataBeforeApproval()) {
			isAdminCanAmendDataBeforeApproval = true;
		}

		if (isAdminCanAmendDataBeforeApproval) {
			return PayAsiaConstants.TRUE;
		}
		return PayAsiaConstants.FALSE;
	}

	@Override
	public String getEmployeeName(String employeeNumber, Long companyId) {
		Employee employeeVO = employeeDAO.findByNumber(employeeNumber, companyId);
		if (employeeVO != null) {
			String employeeName = employeeVO.getFirstName();
			if (StringUtils.isNotBlank(employeeVO.getLastName())) {
				employeeName += " " + employeeVO.getLastName();
			}
			employeeName += " [" + employeeVO.getEmployeeNumber() + "]";
			return employeeName;
		} else {
			return "";
		}
	}

	@Override
	public ImportEmployeeClaimForm importEmployeeClaim(ImportEmployeeClaimForm importEmployeeClaimForm, Long companyId,
			Long employeeId, boolean hasLundinTimesheetModule) {
		ImportEmployeeClaimForm response = new ImportEmployeeClaimForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();
		try {

			importEmployeeClaimForm = readEmployeeClaimImportData(importEmployeeClaimForm.getFileUpload(), companyId,
					dataImportLogDTOs);

			validateEmployeeClaims(importEmployeeClaimForm, companyId, dataImportLogDTOs, employeeId,
					hasLundinTimesheetModule);

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaRollBackDataException(dataImportLogDTOs);

		}
		response.setDataImportLogDTOs(dataImportLogDTOs);
		return response;

	}

	private ImportEmployeeClaimForm validateEmployeeClaims(ImportEmployeeClaimForm importEmployeeClaimForm,
			Long companyId, List<DataImportLogDTO> dataImportLogDTOs, Long loggedInEmployeeId,
			boolean hasLundinTimesheetModule) {
		Boolean isAdmin = true;
		Boolean isManager = false;
		ImportEmployeeClaimForm importEmployeeClaimFrm = new ImportEmployeeClaimForm();
		validateImpotedData(dataImportLogDTOs, importEmployeeClaimForm.getEmployeeClaims(), companyId,
				hasLundinTimesheetModule);

		if (!dataImportLogDTOs.isEmpty()) {
			importEmployeeClaimFrm.setDataValid(false);
			importEmployeeClaimFrm.setDataImportLogDTOs(dataImportLogDTOs);
			return importEmployeeClaimFrm;
		}

		Company companyVO = companyDAO.findById(companyId);
		Employee loggedInEmployeeVO = employeeDAO.findById(loggedInEmployeeId);
		int rowCount = 2;
		for (ImportEmployeeClaimDTO importClaimDTO : importEmployeeClaimForm.getEmployeeClaims()) {
			Employee claimantEmp = employeeDAO.findByNumber(importClaimDTO.getEmployeeNumber(), companyId);

			if (claimantEmp == null) {

				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(rowCount);
				dataImportLogDTO.setErrorKey("payasia.employee.claim.import.employee.not.found");
				dataImportLogDTO.setColName(importClaimDTO.getEmployeeNumber());
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);

			}

			EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO.checkEmployeeClaimTemplateyName(
					importClaimDTO.getClaimTemplate(), claimantEmp.getEmployeeId(),
					DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime()),
					companyVO.getDateFormat());

			if (employeeClaimTemplate == null) {

				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(rowCount);
				dataImportLogDTO.setErrorKey("payasia.employee.claim.import.claim.template.not.assigned.to.employee");
				dataImportLogDTO.setColName(importClaimDTO.getEmployeeNumber());
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);

			}
			EmployeeClaimTemplateItem employeeClaimTemplateItem = null;
			if (StringUtils.isNotBlank(importClaimDTO.getClaimCategory())) {
				employeeClaimTemplateItem = employeeClaimTemplateItemDAO.findItemByTemplateIdandItemName(
						employeeClaimTemplate.getEmployeeClaimTemplateId(), importClaimDTO.getClaimItem(),
						importClaimDTO.getClaimCategory().trim());
			} else {
				employeeClaimTemplateItem = employeeClaimTemplateItemDAO.findItemByTemplateIdandItemName(
						employeeClaimTemplate.getEmployeeClaimTemplateId(), importClaimDTO.getClaimItem(), null);
			}

			if (employeeClaimTemplateItem == null) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(rowCount);
				dataImportLogDTO
						.setErrorKey("payasia.employee.claim.import.claim.template.item.not.assigned.to.employee");
				dataImportLogDTO.setColName(importClaimDTO.getEmployeeNumber());
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);
			}

			ClaimApplication claimApplication = new ClaimApplication();
			claimApplication.setCompany(companyVO);
			claimApplication.setEmployee(employeeClaimTemplate.getEmployee());
			claimApplication.setTotalItems(1);
			ClaimStatusMaster claimStatusMaster = claimStatusMasterDAO
					.findByCondition(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
			claimApplication.setClaimStatusMaster(claimStatusMaster);
			claimApplication.setRemarks(importClaimDTO.getClaimRemarks());
			claimApplication.setEmployeeClaimTemplate(employeeClaimTemplate);
			claimApplication.setClaimNumber(claimApplicationDAO.getMaxClaimNumber() + 1);
			claimApplication.setTotalAmount(new BigDecimal(importClaimDTO.getAmount()));
			if (StringUtils.isNotBlank(importClaimDTO.getVisibleToEmployee())) {
				if (importClaimDTO.getVisibleToEmployee().equalsIgnoreCase(PayAsiaConstants.TRUE) || importClaimDTO
						.getVisibleToEmployee().equalsIgnoreCase(PayAsiaConstants.PAYASIA_REQUIRED_YES)) {
					claimApplication.setVisibleToEmployee(true);
				} else if (importClaimDTO.getVisibleToEmployee().equalsIgnoreCase(PayAsiaConstants.FALSE)
						|| importClaimDTO.getVisibleToEmployee()
								.equalsIgnoreCase(PayAsiaConstants.PAYASIA_REQUIRED_NO)) {
					claimApplication.setVisibleToEmployee(false);
				} else {
					claimApplication.setVisibleToEmployee(false);
				}

			} else {
				claimApplication.setVisibleToEmployee(false);
			}

			ClaimApplicationItem claimApplicationItem = new ClaimApplicationItem();
			if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals().iterator().next()
					.getReceiptNoMandatory() && StringUtils.isBlank(importClaimDTO.getReceipt())) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(rowCount);
				dataImportLogDTO.setErrorKey("payasia.empty");
				dataImportLogDTO.setColName("Receipt");
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);
			}
			claimApplicationItem.setReceiptNumber(importClaimDTO.getReceipt());
			if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals().iterator().next()
					.getRemarksMandatory() && StringUtils.isBlank(importClaimDTO.getClaimItemRemarks())) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(rowCount);
				dataImportLogDTO.setErrorKey("payasia.empty");
				dataImportLogDTO.setColName("Claim Item Remarks");
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);
			}
			String remarks = importClaimDTO.getClaimItemRemarks();
			if (StringUtils.isNotBlank(remarks) && remarks.length() > 2999) {
				remarks = remarks.substring(0, 2999);
			}
			claimApplicationItem.setRemarks(remarks);
			claimApplicationItem.setClaimAmount(new BigDecimal(importClaimDTO.getClaimAmount()));
			if (StringUtils.isBlank(importClaimDTO.getTaxAmount())) {
				claimApplicationItem.setTaxAmount(new BigDecimal(0));
			} else {
				claimApplicationItem.setTaxAmount(new BigDecimal(importClaimDTO.getTaxAmount()));
			}

			// Calculate Applicable Amount If Applicable vivek
			ClaimTemplateItemClaimType claimTemplateItemClaimType = employeeClaimTemplateItem.getClaimTemplateItem()
					.getClaimTemplateItemClaimTypes().iterator().next();

			if (claimTemplateItemClaimType.getClaimType().getCodeDesc()
					.equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED)) {
				if (claimTemplateItemClaimType.getReceiptAmtPercentApplicable() != null && claimTemplateItemClaimType
						.getReceiptAmtPercentApplicable() <= PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED_AMOUNT_APPLICABLE_PERCENT) {
					Integer amtApplicablePercent = claimTemplateItemClaimType.getReceiptAmtPercentApplicable();
					if (amtApplicablePercent == null || amtApplicablePercent == 0) {
					} else {
						Double applicableAmt = (Double.valueOf(importClaimDTO.getClaimAmount()) * amtApplicablePercent)
								/ 100;
						claimApplicationItem.setApplicableClaimAmount(new BigDecimal(applicableAmt));
					}
				}
			}

			claimApplicationItem.setAmountBeforeTax(
					claimApplicationItem.getClaimAmount().subtract(claimApplicationItem.getTaxAmount()));
			claimApplicationItem.setEmployeeClaimTemplateItem(employeeClaimTemplateItem);
			if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemGenerals().iterator().next()
					.getClaimDateMandatory() && importClaimDTO.getClaimDate() == null) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(rowCount);
				dataImportLogDTO.setErrorKey("payasia.empty");
				dataImportLogDTO.setColName("Claim Date");
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);
			}
			if (importClaimDTO.getClaimDate() != null) {
				claimApplicationItem.setClaimDate(DateUtils.convertDateToTimeStamp(importClaimDTO.getClaimDate()));
			}

			if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemClaimTypes().iterator().next()
					.getClaimType().getCodeDesc().equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_QUANTITY_BASED)) {
				claimApplicationItem.setQuantity(new Float(importClaimDTO.getQuantity()));
				claimApplicationItem.setUnitPrice(new BigDecimal(importClaimDTO.getPerUnitPrice()));

			} else if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemClaimTypes().iterator()
					.next().getClaimType().getCodeDesc().equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED)) {

			} else if (employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemClaimTypes().iterator()
					.next().getClaimType().getCodeDesc().equals(PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED)) {
				claimApplicationItem.setExchangeRate(new BigDecimal(importClaimDTO.getForexRate()));
				claimApplicationItem.setForexReceiptAmount(claimApplicationItem.getClaimAmount());
				claimApplicationItem.setClaimAmount(
						claimApplicationItem.getClaimAmount().multiply(claimApplicationItem.getExchangeRate()));

				CurrencyMaster currencyMaster = currencyMasterDAO.findByCurrencyCode(importClaimDTO.getCurrency());
				if (currencyMaster == null) {

					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO.setRowNumber(rowCount);
					dataImportLogDTO.setErrorKey("payasia.employee.claim.currency.code.not.found");
					dataImportLogDTO.setColName(importClaimDTO.getEmployeeNumber());
					dataImportLogDTO.setErrorValue("");
					dataImportLogDTOs.add(dataImportLogDTO);
					throw new PayAsiaRollBackDataException(dataImportLogDTOs);

				}

				claimApplicationItem.setCurrencyMaster(currencyMaster);

			}

			claimApplication.setTotalAmount(claimApplicationItem.getClaimAmount());
			ClaimApplication persistClaimAplication = claimApplicationDAO.saveReturn(claimApplication);

			claimApplicationItem.setClaimApplication(persistClaimAplication);

			ValidationClaimItemDTO validateClaimItemDTO = claimApplicationItemDAO
					.validateClaimItem(claimApplicationItem, isAdmin, isManager);
			ClaimApplicationItem persistClaimApplicationItem = null;
			if (validateClaimItemDTO.getErrorCode() == 1) {

				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(rowCount);
				dataImportLogDTO.setErrorKey(validateClaimItemDTO.getErrorKey());
				dataImportLogDTO.setColName(importClaimDTO.getEmployeeNumber());
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);

			} else {
				persistClaimApplicationItem = claimApplicationItemDAO.saveReturn(claimApplicationItem);

			}

			ClaimApplicationWorkflow claimApplicationWorkflow = new ClaimApplicationWorkflow();

			claimApplicationWorkflow.setClaimApplication(persistClaimAplication);
			claimApplicationWorkflow.setClaimStatusMaster(claimStatusMaster);

			claimApplicationWorkflow.setRemarks(importClaimDTO.getClaimRemarks());
			claimApplicationWorkflow.setEmployee(loggedInEmployeeVO);

			if (importClaimDTO.getApprovedDate() != null) {
				claimApplicationWorkflow
						.setCreatedDate(DateUtils.convertDateToTimeStamp(importClaimDTO.getApprovedDate()));
			} else {
				claimApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestamp());
			}

			claimApplicationWorkflow.setTotalAmount(persistClaimAplication.getTotalAmount());

			claimApplicationWorkflow = claimApplicationWorkflowDAO.saveReturn(claimApplicationWorkflow);

			ClaimApplicationReviewer claimApplicationReviewer = new ClaimApplicationReviewer();

			claimApplicationReviewer.setEmployee(loggedInEmployeeVO);
			WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
					.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, "1");
			claimApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
			claimApplicationReviewer.setClaimApplication(persistClaimAplication);
			claimApplicationReviewer.setPending(false);
			claimApplicationReviewerDAO.save(claimApplicationReviewer);

			if (hasLundinTimesheetModule) {
				String accountCodeStartWith = "";
				if (StringUtils.isNotBlank(
						employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster().getAccountCode())) {
					accountCodeStartWith = employeeClaimTemplateItem.getClaimTemplateItem().getClaimItemMaster()
							.getAccountCode().substring(0, 1);
				}

				ClaimApplicationItemLundinDetail claimApplicationItemLundinDetail = new ClaimApplicationItemLundinDetail();
				if (importClaimDTO.getBlockId() != null) {
					LundinBlock lundinBlock = lundinBlockDAO.findById(importClaimDTO.getBlockId());
					if (PayAsiaConstants.PAYASIA_LUNDIN_CLAIM_ITEM_ACCOUNT_CODE_START_WITH
							.equalsIgnoreCase(accountCodeStartWith)
							&& (lundinBlock.getBlockCode().startsWith("C") || lundinBlock.getBlockCode().startsWith("c")
									|| lundinBlock.getBlockCode().equalsIgnoreCase("COM")
									|| lundinBlock.getBlockCode().startsWith("NV")
									|| lundinBlock.getBlockCode().startsWith("nv"))) {
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO.setRowNumber(rowCount);
						dataImportLogDTO.setErrorKey("payasia.lundin.block.invalid");
						dataImportLogDTO.setColName(importClaimDTO.getBlock());
						dataImportLogDTO.setErrorValue("");
						dataImportLogDTOs.add(dataImportLogDTO);
						throw new PayAsiaRollBackDataException(dataImportLogDTOs);
					} else {
						claimApplicationItemLundinDetail.setLundinBlock(lundinBlock);
					}
				}
				if (importClaimDTO.getAFEId() != null) {
					LundinAFE lundinAFE = lundinAFEDAO.findById(importClaimDTO.getAFEId());
					if (PayAsiaConstants.PAYASIA_LUNDIN_CLAIM_ITEM_ACCOUNT_CODE_START_WITH
							.equalsIgnoreCase(accountCodeStartWith)
							&& lundinAFE.getAfeCode()
									.equalsIgnoreCase(PayAsiaConstants.PAYASIA_LUNDIN_AFE_NOT_ANALYZED_CODE)) {
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO.setRowNumber(rowCount);
						dataImportLogDTO.setErrorKey("payasia.lundin.afe.invalid");
						dataImportLogDTO.setColName(importClaimDTO.getAFE());
						dataImportLogDTO.setErrorValue("");
						dataImportLogDTOs.add(dataImportLogDTO);
						throw new PayAsiaRollBackDataException(dataImportLogDTOs);
					} else {
						claimApplicationItemLundinDetail.setLundinAFE(lundinAFE);
					}

				}
				claimApplicationItemLundinDetail.setClaimApplicationItem(persistClaimApplicationItem);
				claimApplicationItemLundinDetailDAO.save(claimApplicationItemLundinDetail);
			}

			Integer customFieldCount = 1;
			for (ClaimTemplateItemCustomField claimTemplateItemCustomField : employeeClaimTemplateItem
					.getClaimTemplateItem().getClaimTemplateItemCustomFields()) {
				ClaimApplicationItemCustomField claimApplicationItemCustomField = new ClaimApplicationItemCustomField();
				claimApplicationItemCustomField.setClaimApplicationItem(persistClaimApplicationItem);
				claimApplicationItemCustomField.setClaimTemplateItemCustomField(claimTemplateItemCustomField);
				claimApplicationItemCustomField.setValue(getCustomFieldValue(customFieldCount, importClaimDTO));
				ClaimApplicationItemCustomField abc = claimApplicationItemCustomFieldDAO
						.saveReturn(claimApplicationItemCustomField);
				customFieldCount++;
			}
			rowCount++;
		}

		return importEmployeeClaimFrm;

	}

	private void validateImpotedData(List<DataImportLogDTO> dataImportLogDTOs,
			List<ImportEmployeeClaimDTO> employeeClaimDTOList, Long companyId, boolean hasLundinTimesheetModule) {

		List<String> employeeNameList = new ArrayList<>();
		List<Tuple> employeeNameListVO = employeeDAO.getEmployeeNameTupleList(companyId);
		for (Tuple empTuple : employeeNameListVO) {
			String employeeName = (String) empTuple.get(getAlias(Employee_.employeeNumber), String.class);
			employeeNameList.add(employeeName.toUpperCase());
		}

		// Get Lundin Block
		List<String> lundinBlockCodeList = new ArrayList<String>();
		List<LundinBlock> lundinBlocks = null;
		List<AppCodeMaster> appCodeMasterList = null;
		if (hasLundinTimesheetModule) {
			lundinBlocks = lundinBlockDAO.findByCondition(companyId);
			for (LundinBlock lundinBlock : lundinBlocks) {
				lundinBlockCodeList.add(lundinBlock.getBlockCode());
			}
		}

		int rowCount = 1;
		for (ImportEmployeeClaimDTO employeeClaimDTO : employeeClaimDTOList) {
			String rowNumber = String.valueOf(rowCount);
			if (StringUtils.isBlank(employeeClaimDTO.getEmployeeNumber())) {
				setClaimImportLogs(dataImportLogDTOs, "Employee No", "payasia.empty", Long.parseLong(rowNumber));
			}
			if (StringUtils.isNotBlank(employeeClaimDTO.getEmployeeNumber())) {
				if (!employeeNameList.contains(employeeClaimDTO.getEmployeeNumber().toUpperCase())) {
					setClaimImportLogs(dataImportLogDTOs, "Employee No", "payasia.invalid.employee.number",
							Long.parseLong(rowNumber));
				}
			}

			if (StringUtils.isBlank(employeeClaimDTO.getClaimTemplate())) {
				setClaimImportLogs(dataImportLogDTOs, "Claim Template", "payasia.empty", Long.parseLong(rowNumber));
			}
			if (StringUtils.isBlank(employeeClaimDTO.getClaimItem())) {
				setClaimImportLogs(dataImportLogDTOs, "Claim Item", "payasia.empty", Long.parseLong(rowNumber));
			}

			if (StringUtils.isBlank(employeeClaimDTO.getAmount())) {
				setClaimImportLogs(dataImportLogDTOs, "Amount", "payasia.empty", Long.parseLong(rowNumber));
			}
			// if (StringUtils.isBlank(employeeClaimDTO.getReceipt())) {
			// setClaimImportLogs(dataImportLogDTOs, "Receipt",
			// "payasia.empty", Long.parseLong(rowNumber));
			// }
			if (StringUtils.isBlank(employeeClaimDTO.getClaimAmount())) {
				setClaimImportLogs(dataImportLogDTOs, "Claim Amount", "payasia.empty", Long.parseLong(rowNumber));
			}
			// if (StringUtils.isBlank(employeeClaimDTO.getClaimItemRemarks()))
			// {
			// setClaimImportLogs(dataImportLogDTOs, "Claim Item Remarks",
			// "payasia.empty", Long.parseLong(rowNumber));
			// }
			// if (employeeClaimDTO.getClaimDate() == null) {
			//
			// setClaimImportLogs(dataImportLogDTOs, "Claim Date",
			// "payasia.empty", Long.parseLong(rowNumber));
			//
			// }

			if (hasLundinTimesheetModule) {
				// Block
				if (StringUtils.isBlank(employeeClaimDTO.getBlock())) {
					setClaimImportLogs(dataImportLogDTOs, "Block", "payasia.empty", Long.parseLong(rowNumber));
				} else {
					if (!lundinBlockCodeList.isEmpty() && !lundinBlockCodeList.contains(employeeClaimDTO.getBlock())) {
						setClaimImportLogs(dataImportLogDTOs, "Block", "payasia.lundin.block.invalid",
								Long.parseLong(rowNumber));
					} else {
						// AFE
						if (StringUtils.isBlank(employeeClaimDTO.getAFE())) {
							setClaimImportLogs(dataImportLogDTOs, "AFE", "payasia.empty", Long.parseLong(rowNumber));
						} else {
							List<LundinAFE> lundinAFEs = null;
							List<String> lundinAFEList = new ArrayList<String>();
							for (LundinBlock lundinBlock : lundinBlocks) {
								if (employeeClaimDTO.getBlock().equalsIgnoreCase(lundinBlock.getBlockCode())) {
									employeeClaimDTO.setBlockId(lundinBlock.getBlockId());
									lundinAFEs = lundinBlock.getLundinAfes();
									for (LundinAFE lundinAFE : lundinAFEs) {
										lundinAFEList.add(lundinAFE.getAfeCode());
									}

								}
							}
							if (!lundinAFEList.isEmpty() && !lundinAFEList.contains(employeeClaimDTO.getAFE())) {
								setClaimImportLogs(dataImportLogDTOs, "AFE", "payasia.lundin.afe.invalid",
										Long.parseLong(rowNumber));
							} else {
								for (LundinAFE lundinAFE : lundinAFEs) {
									if (lundinAFE.getAfeCode().equalsIgnoreCase(employeeClaimDTO.getAFE())) {
										employeeClaimDTO.setAFEId(lundinAFE.getAfeId());
									}
								}
							}

						}
					}
				}
			}

			rowCount++;
		}
	}

	public void setClaimImportLogs(List<DataImportLogDTO> dataImportLogDTOs, String key, String remarks,
			Long rowNumber) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setColName(key);
		dataImportLogDTO.setRemarks(remarks);
		dataImportLogDTO.setRowNumber(rowNumber + 1);
		dataImportLogDTOs.add(dataImportLogDTO);
	}

	private String getCustomFieldValue(Integer customFieldCount, ImportEmployeeClaimDTO importClaimDTO) {
		String methodName = "getCustomField" + String.valueOf(customFieldCount);
		Method customFieldMethod = null;
		try {
			customFieldMethod = ImportEmployeeClaimDTO.class.getMethod(methodName);
		} catch (NoSuchMethodException | SecurityException exception) {
			LOGGER.error(exception.getMessage(), exception);

		}

		String customFieldValue = null;
		try {
			customFieldValue = (String) customFieldMethod.invoke(importClaimDTO);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return customFieldValue;
	}

	private ImportEmployeeClaimForm readEmployeeClaimImportData(CommonsMultipartFile fileUpload, Long companyId,
			List<DataImportLogDTO> dataImportLogDTOs) {
		ImportEmployeeClaimForm importEmployeeClaimForm = new ImportEmployeeClaimForm();
		Company companyVO = companyDAO.findById(companyId);
		String fileName = fileUpload.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		List<ImportEmployeeClaimDTO> employeeClaims = new ArrayList<>();
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			employeeClaims = ExcelUtils.getEmployeeClaimFromXLS(fileUpload, companyVO.getDateFormat(),
					dataImportLogDTOs);
		} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			employeeClaims = ExcelUtils.getEmployeeClaimFromXLSX(fileUpload, companyVO.getDateFormat(),
					dataImportLogDTOs);
		}

		importEmployeeClaimForm.setEmployeeClaims(employeeClaims);

		return importEmployeeClaimForm;
	}

	@Override
	public ClaimFormPdfDTO generateClaimFormPrintPDF(Long companyId, Long employeeId, Long claimApplicationId,
			boolean hasLundinTimesheetModule) {

		ClaimFormPdfDTO claimFormPdfDTO = new ClaimFormPdfDTO();
		ClaimApplication claimApplicationVO = claimApplicationDAO.findByID(claimApplicationId);
		claimFormPdfDTO.setClaimTemplateName(
				claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
		claimFormPdfDTO.setEmployeeNumber(claimApplicationVO.getEmployee().getEmployeeNumber());

		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				claimFormPdfDTO.setClaimFormPdfByteFile(
						generateClaimFormPDF(companyId, employeeId, claimApplicationId, hasLundinTimesheetModule));
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
						generateClaimFormPDF(companyId, employeeId, claimApplicationId, hasLundinTimesheetModule));
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
	public Long getEmployeeId(Long companyId, String employeeNumber) {
		Employee employee = employeeDAO.findByNumber(employeeNumber, companyId);
		return employee.getEmployeeId();
	}

}
