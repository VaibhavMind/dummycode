package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.EmployeeClaimAdjustmentDTO;
import com.payasia.common.dto.EmployeeClaimAdjustmentReportDTO;
import com.payasia.common.dto.EmployeeEntitlementDTO;
import com.payasia.common.form.EmployeeClaimAdjustmentForm;
import com.payasia.common.form.EmployeeClaimAdjustments;
import com.payasia.common.form.EmployeeEntitlements;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeClaimAdjustmentComparison;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeClaimAdjustmentDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimAdjustment;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.logic.EmployeeEntitlementsLogic;

@Component
public class EmployeeEntitlementsLogicImpl implements EmployeeEntitlementsLogic {
	private static final Logger LOGGER = Logger.getLogger(EmployeeEntitlementsLogicImpl.class);

	@Resource
	EmployeeClaimAdjustmentDAO employeeClaimAdjustmentDAO;

	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeDAO employeeDAO;
	@Autowired
	private MessageSource messageSource;

	@Override
	public EmployeeEntitlements getEmployeeEntitlements(Long empId, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, String employeeNumber, long claimTemplateId, int year, Locale locale) {
		String details = "payasia.claim.details";
		String edit = "payasia.edit";
		EmployeeEntitlements employeeEntitlements = new EmployeeEntitlements();

		List<EmployeeEntitlementDTO> employeeEntitlementDTOList = employeeClaimAdjustmentDAO
				.callEmployeeClaimEntitlementSummary(companyId, employeeNumber, claimTemplateId, year);

		for (EmployeeEntitlementDTO employeeEntitlementDTO : employeeEntitlementDTOList) {
			long employeeClaimTemplateId=FormatPreserveCryptoUtil.encrypt(employeeEntitlementDTO.getEmployeeClaimTemplateId());
			employeeEntitlementDTO.setEmployeeClaimTemplateId(employeeClaimTemplateId);

			StringBuilder entitlements = new StringBuilder();
			entitlements.append("<span style='float:left;margin-left:40%;margin-right:2%'>"
					+ employeeEntitlementDTO.getEntitlement() + "</span>");
			entitlements
					.append("<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'viewEmpEntitlementDetails("
							+ employeeClaimTemplateId + ")'>["
							+ messageSource.getMessage(details, new Object[] {}, locale) + "]</a></span>");
			employeeEntitlementDTO.setEntitlementDetails(String.valueOf(entitlements));

			StringBuilder adjustments = new StringBuilder();
			adjustments.append("<span style='float:left;margin-left:40%;margin-right:2%'>"
					+ employeeEntitlementDTO.getAdjustments() + "</span>");
			adjustments
					.append("<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'viewEmployeeEntitlementAdjustments("
							+ employeeClaimTemplateId + ")'>["
							+ messageSource.getMessage(edit, new Object[] {}, locale) + "]</a></span>");
			employeeEntitlementDTO.setAdjustments(String.valueOf(adjustments));
			employeeEntitlementDTO.setFirstName(
					employeeEntitlementDTO.getFirstName() + " [" + employeeEntitlementDTO.getEmployeeNumber() + "]");
		}

		employeeEntitlements.setEmployeeEntitlements(employeeEntitlementDTOList);
		return employeeEntitlements;
	}

	@Override
	public String saveClaimAdjustment(EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm, Long companyId) {
		
		EmployeeClaimAdjustment employeeClaimAdjustment = new EmployeeClaimAdjustment();
		
		Long employeeClaimTemplateId = FormatPreserveCryptoUtil.decrypt(employeeClaimAdjustmentForm.getEmployeeClaimTemplateId());
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setAdmin(true);
		addClaimDTO.setEmployeeClaimTemplateId(employeeClaimTemplateId);
		addClaimDTO.setCompanyId(companyId);
		
		EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO.findByEmployeeClaimTemplateID(addClaimDTO);
		
		Company company = companyDAO.findById(companyId);
		employeeClaimAdjustment.setEmployeeClaimTemplate(employeeClaimTemplate);
		employeeClaimAdjustment.setCompany(company);
		employeeClaimAdjustment.setAmount(employeeClaimAdjustmentForm.getAmount());
		employeeClaimAdjustment
				.setEffectiveDate(DateUtils.stringToTimestamp(employeeClaimAdjustmentForm.getEffectiveDate()));
		if (StringUtils.isNotBlank(employeeClaimAdjustmentForm.getRemarks())) {
			try {
				employeeClaimAdjustment
						.setRemarks(URLDecoder.decode(employeeClaimAdjustmentForm.getRemarks(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else {
			employeeClaimAdjustment.setRemarks("");
		}
		employeeClaimAdjustmentDAO.save(employeeClaimAdjustment);
		return "true";
	}

	@Override
	public EmployeeClaimAdjustments getEmployeeClaimAdjustments(AddClaimDTO claimDTO) {
		
		EmployeeClaimAdjustments employeeClaimAdjustmentRes = new EmployeeClaimAdjustments();
		List<EmployeeClaimAdjustment> employeeClaimAdjustments = employeeClaimAdjustmentDAO.findByEmployeeClaimTemplateId(claimDTO);
		List<EmployeeClaimAdjustmentDTO> empoAdjustmentDTOs = new ArrayList<>();
		for (EmployeeClaimAdjustment employeeClaimAdjustment : employeeClaimAdjustments) {
			EmployeeClaimAdjustmentDTO employeeClaimAdjustmentDTO = new EmployeeClaimAdjustmentDTO();
			employeeClaimAdjustmentDTO.setAmount(employeeClaimAdjustment.getAmount());
			employeeClaimAdjustmentDTO
					.setEffectiveDate(DateUtils.timeStampToString(employeeClaimAdjustment.getEffectiveDate()));
			employeeClaimAdjustmentDTO.setRemarks(employeeClaimAdjustment.getRemarks());
			employeeClaimAdjustmentDTO.setClaimTemplateName(
					employeeClaimAdjustment.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			employeeClaimAdjustmentDTO
					.setEmployeeClaimAdjustmentID(FormatPreserveCryptoUtil.encrypt(employeeClaimAdjustment.getEmployeeClaimAdjustmentID()));
			empoAdjustmentDTOs.add(employeeClaimAdjustmentDTO);
		}

		employeeClaimAdjustmentRes.setRows(empoAdjustmentDTOs);
		return employeeClaimAdjustmentRes;
	}

	@Override
	public String updateClaimAdjustment(EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm, Long companyId) {
		
		EmployeeClaimAdjustment employeeClaimAdjustment = employeeClaimAdjustmentDAO
				.findByID(FormatPreserveCryptoUtil.decrypt(employeeClaimAdjustmentForm.getEmployeeClaimAdjustmentId()));
		employeeClaimAdjustment.setAmount(employeeClaimAdjustmentForm.getAmount());
		employeeClaimAdjustment
				.setEffectiveDate(DateUtils.stringToTimestamp(employeeClaimAdjustmentForm.getEffectiveDate()));
		if (StringUtils.isNotBlank(employeeClaimAdjustmentForm.getRemarks())) {
			try {
				employeeClaimAdjustment
						.setRemarks(URLDecoder.decode(employeeClaimAdjustmentForm.getRemarks(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else {
			employeeClaimAdjustment.setRemarks("");
		}
		return "true";
	}

	@Override
	public String deleteClaimAdjustment(Long employeeClaimAdjustmentId,Long companyId) {
		Integer employeClaimAdjustmentId = (int) (long) FormatPreserveCryptoUtil.decrypt(new Long(employeeClaimAdjustmentId));
		EmployeeClaimAdjustment employeeClaimAdjustment = employeeClaimAdjustmentDAO
				.findByID(employeClaimAdjustmentId);
		if(companyId.equals(employeeClaimAdjustment.getCompany().getCompanyId()))
		{
			employeeClaimAdjustmentDAO.delete(employeeClaimAdjustment);
			return "true";
		}
		return "false";
	}

	private String getEmployeeName(Long employeeId) {

		Employee employeeVO = employeeDAO.findById(employeeId);
		if (employeeVO != null) {
			String employeeName = employeeVO.getFirstName();
			if (StringUtils.isNotBlank(employeeVO.getLastName())) {
				employeeName += " " + employeeVO.getLastName();
			}

			return employeeName;
		} else {
			return null;
		}
	}

	@Override
	public EmployeeClaimAdjustmentReportDTO exportAdjustmentsExcelFile(Long companyId,
			EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm, Long employeeId) {

		EmployeeClaimAdjustmentReportDTO empClaimAdjustmentDataDTO = new EmployeeClaimAdjustmentReportDTO();

		List<EmployeeClaimAdjustment> employeeClaimAdjustments = employeeClaimAdjustmentDAO.findByEffectiveDate(
				companyId, DateUtils.stringToTimestamp(employeeClaimAdjustmentForm.getStartDate()),
				DateUtils.stringToTimestamp(employeeClaimAdjustmentForm.getEndDate()));
		List<EmployeeClaimAdjustmentDTO> empoAdjustmentDTOs = new ArrayList<>();
		for (EmployeeClaimAdjustment employeeClaimAdjustment : employeeClaimAdjustments) {
			EmployeeClaimAdjustmentDTO employeeClaimAdjustmentDTO = new EmployeeClaimAdjustmentDTO();
			employeeClaimAdjustmentDTO.setAmount(employeeClaimAdjustment.getAmount());
			employeeClaimAdjustmentDTO
					.setEffectiveDate(DateUtils.timeStampToString(employeeClaimAdjustment.getEffectiveDate()));
			employeeClaimAdjustmentDTO.setRemarks(employeeClaimAdjustment.getRemarks());
			employeeClaimAdjustmentDTO.setClaimTemplateName(
					employeeClaimAdjustment.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			employeeClaimAdjustmentDTO
					.setEmployeeClaimAdjustmentID(employeeClaimAdjustment.getEmployeeClaimAdjustmentID());
			employeeClaimAdjustmentDTO.setEmployeeNumber(
					employeeClaimAdjustment.getEmployeeClaimTemplate().getEmployee().getEmployeeNumber());
			employeeClaimAdjustmentDTO.setEmployeeName(
					getEmployeeName(employeeClaimAdjustment.getEmployeeClaimTemplate().getEmployee().getEmployeeId()));
			empoAdjustmentDTOs.add(employeeClaimAdjustmentDTO);
		}

		empClaimAdjustmentDataDTO.setEmployeeClaimAdjustmentDTOs(empoAdjustmentDTOs);

		return empClaimAdjustmentDataDTO;
	}
	
	/*
	 * 	NEW METHOD TO IMPLEMENT PAGINATION AND SORTING IN CLAIM ADJUSTMENTS SUMMARY
	 */
	@Override
	public EmployeeClaimAdjustments getEmployeeClaimAdjustmentsNew(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO) {
		
		EmployeeClaimAdjustments employeeClaimAdjustmentRes = new EmployeeClaimAdjustments();
		List<EmployeeClaimAdjustment> employeeClaimAdjustments = employeeClaimAdjustmentDAO.findByEmployeeClaimTemplateId(claimDTO);
		List<EmployeeClaimAdjustmentDTO> empoAdjustmentDTOs = new ArrayList<>();
		for (EmployeeClaimAdjustment employeeClaimAdjustment : employeeClaimAdjustments) {
			EmployeeClaimAdjustmentDTO employeeClaimAdjustmentDTO = new EmployeeClaimAdjustmentDTO();
			employeeClaimAdjustmentDTO.setAmount(employeeClaimAdjustment.getAmount());
			employeeClaimAdjustmentDTO.setEffectiveDate(DateUtils.timeStampToString(employeeClaimAdjustment.getEffectiveDate()));
			employeeClaimAdjustmentDTO.setRemarks(employeeClaimAdjustment.getRemarks());
			employeeClaimAdjustmentDTO.setClaimTemplateName(employeeClaimAdjustment.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
			employeeClaimAdjustmentDTO.setEmployeeClaimAdjustmentID(FormatPreserveCryptoUtil.encrypt(employeeClaimAdjustment.getEmployeeClaimAdjustmentID()));
			empoAdjustmentDTOs.add(employeeClaimAdjustmentDTO);
		}
		
		if (sortDTO != null && !empoAdjustmentDTOs.isEmpty()) {
			Collections.sort(empoAdjustmentDTOs, new EmployeeClaimAdjustmentComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		List<EmployeeClaimAdjustmentDTO> finalEmpClaimAdjustmentList = new ArrayList<>();
		
		/*	PAGINATION	*/
		if (pageDTO != null && !empoAdjustmentDTOs.isEmpty()) {
			int recordSizeFinal = empoAdjustmentDTOs.size();
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;

			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal){
				endPos = startPos + pageSize;
			} else if (startPos <= recordSizeFinal) {
				endPos = recordSizeFinal;
			}

			finalEmpClaimAdjustmentList = empoAdjustmentDTOs.subList(startPos, endPos);
			employeeClaimAdjustmentRes.setTotal(totalPages);
		}
		employeeClaimAdjustmentRes.setPage(pageDTO.getPageNumber());
		employeeClaimAdjustmentRes.setRows(finalEmpClaimAdjustmentList);
		employeeClaimAdjustmentRes.setRecords(empoAdjustmentDTOs.size());
		return employeeClaimAdjustmentRes;
	}
}
