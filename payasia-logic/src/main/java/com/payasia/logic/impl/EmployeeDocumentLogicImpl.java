/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.CompanyDocumentConditionDTO;
import com.payasia.common.dto.CompanyDocumentDetailDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeTaxDocumentForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.CompanyDocumentDetailDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyDocumentDetail;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.EmployeeDocumentLogic;
import com.payasia.logic.GeneralLogic;

/**
 * The Class EmployeeDocumentLogicImpl.
 */
@Component
public class EmployeeDocumentLogicImpl implements EmployeeDocumentLogic {

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The document category master dao. */
	@Resource
	DocumentCategoryMasterDAO documentCategoryMasterDAO;

	/** The company document dao. */
	@Resource
	CompanyDocumentDAO companyDocumentDAO;

	/** The company document detail dao. */
	@Resource
	CompanyDocumentDetailDAO companyDocumentDetailDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	FileUtils fileUtils;

	/** The document separator. */
	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String documentSeparator;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDocumentLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDocumentLogic#generateTaxDocument(java.lang
	 * .Long)
	 */
	@Override
	public String generateTaxDocument(Long documentId) {
		CompanyDocumentDetail companyDocumentDetail = companyDocumentDetailDAO
				.findById(documentId);
		String filePath;

		/*
		 * filePath = "/company/" +
		 * companyDocumentDetail.getCompanyDocument().getCompany()
		 * .getCompanyId() + "/" +
		 * PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT + "/" +
		 * companyDocumentDetail.getCompanyDocument().getYear() + "/" +
		 * getValidEmployeeNumber(companyDocumentDetail.getFileName(),
		 * companyDocumentDetail.getCompanyDocument().getCompany()
		 * .getCompanyId()) + "/" + companyDocumentDetail.getFileName();
		 */

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(
				downloadPath,
				rootDirectoryName,
				companyDocumentDetail.getCompanyDocument().getCompany()
						.getCompanyId(),
				PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT,
				companyDocumentDetail.getFileName(),
				String.valueOf(companyDocumentDetail.getCompanyDocument()
						.getYear()),
				getValidEmployeeNumber(companyDocumentDetail.getFileName(),
						companyDocumentDetail.getCompanyDocument().getCompany()
								.getCompanyId()), null,
				PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

		filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		return filePath;
	}

	private String getValidEmployeeNumber(String fileName, Long companyId) {
		String fileNameArr[];
		String employeeNumber = null;

		fileNameArr = fileName.split(documentSeparator);
		Employee emp = employeeDAO.findByNumber(fileNameArr[0], companyId);
		if (emp != null) {
			employeeNumber = emp.getEmployeeNumber();
		}

		return employeeNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDocumentLogic#getPaySlipFrequencyDetails(java
	 * .lang.Long)
	 */
	@Override
	public AdminPaySlipForm getPaySlipFrequencyDetails(Long companyId) {
		AdminPaySlipForm adminPaySlipForm = new AdminPaySlipForm();

		Company company = companyDAO.findById(companyId);
		PayslipFrequency paySlipFrequency = company.getPayslipFrequency();
		adminPaySlipForm.setPaySlipFrequency(paySlipFrequency.getFrequency());
		adminPaySlipForm.setPayslipPart(company.getPart());

		return adminPaySlipForm;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDocumentLogic#getEmployeeId(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public List<AdminPaySlipForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId) {
		List<AdminPaySlipForm> adminPaySlipFormFormList = new ArrayList<AdminPaySlipForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(
				searchString.trim(), companyId, employeeShortListDTO);
		for (Employee employee : employeeNumberList) {
			AdminPaySlipForm adminPaySlipFormForm = new AdminPaySlipForm();

			try {
				adminPaySlipFormForm.setEmployeeNumber(URLEncoder.encode(
						employee.getEmployeeNumber(), "UTF-8"));

				String employeeName = employee.getFirstName();
				if (employee.getLastName() != null) {
					employeeName = employeeName
							+ " "
							+ employee.getLastName();
				}
				adminPaySlipFormForm.setEmployeeName(employeeName);

			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}

			adminPaySlipFormForm.setEmployeeId(employee.getEmployeeId());
			adminPaySlipFormFormList.add(adminPaySlipFormForm);

		}
		return adminPaySlipFormFormList;
	}

	@Override
	public WorkFlowDelegateResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (StringUtils.isNotBlank(empName)) {
			try {
				conditionDTO.setEmployeeName("%"
						+ URLDecoder.decode(empName.trim(), "UTF-8") + "%");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}

		if (StringUtils.isNotBlank(empNumber)) {
			try {
				conditionDTO.setEmployeeNumber("%"
						+ URLDecoder.decode(empNumber.trim(), "UTF-8") + "%");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		int recordSize = employeeDAO.getCountForCondition(conditionDTO,
				companyId);

		List<Employee> employeeVOList = employeeDAO.findEmployeesByCondition(
				conditionDTO, pageDTO, sortDTO, companyId);
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		WorkFlowDelegateResponse response = new WorkFlowDelegateResponse();
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

		response.setSearchEmployeeList(employeeListFormList);
		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDocumentLogic#getYearList(java.lang.Long)
	 */
	@Override
	public List<Integer> getYearList(Long companyId) {

		DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT);
		List<Integer> yearList = companyDocumentDAO.getDistinctYearList(
				companyId, categoryMaster.getDocumentCategoryId());

		if (yearList == null) {
			yearList = new ArrayList<Integer>();
			yearList.add(Calendar.getInstance().get(Calendar.YEAR));
		}
		return yearList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDocumentLogic#getTaxDocumentList(java.lang.
	 * String, int, java.lang.Long)
	 */
	@Override
	public EmployeeTaxDocumentForm getTaxDocumentList(String employeeNumber,
			int year, Long companyId, Long employeeId) {
		try {
			employeeNumber = URLDecoder.decode(employeeNumber, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		EmployeeTaxDocumentForm employeeTaxDocumentForm = new EmployeeTaxDocumentForm();
		Employee employee = employeeDAO.findByNumber(employeeNumber, companyId);
		List<Long> employeeIds = new ArrayList<Long>();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		List<Long> assignedEmployees = new ArrayList(
				employeeShortListDTO.getShortListEmployeeIds());

		if (employee == null) {
			employeeTaxDocumentForm.setDocumentExist(false);
			employeeTaxDocumentForm
					.setVerifyResponse("payasia.employee.not.found");
			return employeeTaxDocumentForm;
		}

		if (employeeShortListDTO.getEmployeeShortList()
				&& employeeShortListDTO.getShortListEmployeeIds().size() == 0) {
			employeeTaxDocumentForm.setDocumentExist(false);
			employeeTaxDocumentForm
					.setVerifyResponse("paysia.tax.document.not.authorized");

			return employeeTaxDocumentForm;
		}

		if (employeeShortListDTO.getEmployeeShortList()
				&& employeeShortListDTO.getShortListEmployeeIds().size() > 0) {
			for (Object existEmployee : assignedEmployees) {
				employeeIds.add(Long.valueOf(existEmployee.toString()));
			}
			if (!employeeIds.contains(employee.getEmployeeId())) {
				employeeTaxDocumentForm.setDocumentExist(false);
				employeeTaxDocumentForm
						.setVerifyResponse("paysia.tax.document.not.authorized");

				return employeeTaxDocumentForm;
			}
		}

		DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT);
		CompanyDocumentConditionDTO companyDocumentConditionDTO = new CompanyDocumentConditionDTO();
		companyDocumentConditionDTO.setCategoryId(categoryMaster
				.getDocumentCategoryId());
		companyDocumentConditionDTO.setYear(year);
		companyDocumentConditionDTO.setDocumentName(employeeNumber
				+ documentSeparator + "%");
		List<CompanyDocumentDetail> documentList = companyDocumentDetailDAO
				.findByConditionCmp(companyDocumentConditionDTO, null, null,
						companyId);

		if (documentList == null || documentList.size() == 0) {
			employeeTaxDocumentForm.setDocumentExist(false);
			employeeTaxDocumentForm
					.setVerifyResponse("paysia.tax.document.no.document.error");

			employeeTaxDocumentForm
					.setMessageParam(new Object[] {
							employee.getFirstName() + "[" + employeeNumber
									+ "]", year });

			return employeeTaxDocumentForm;
		}

		List<CompanyDocumentDetailDTO> taxDocumentList = new ArrayList<CompanyDocumentDetailDTO>();
		for (CompanyDocumentDetail documentDetail : documentList) {
			CompanyDocumentDetailDTO companyDocumentDetailDTO = new CompanyDocumentDetailDTO();

			String fileName = documentDetail.getFileName();
			companyDocumentDetailDTO.setDocumentId(FormatPreserveCryptoUtil.encrypt(documentDetail.getCompanyDocumentDetailID()));
			companyDocumentDetailDTO.setDocumentName(fileName.substring(fileName.indexOf(documentSeparator) + 1,fileName.length()));
			taxDocumentList.add(companyDocumentDetailDTO);
		}
		employeeTaxDocumentForm.setDocumentExist(true);
		employeeTaxDocumentForm.setTaxDocumentList(taxDocumentList);
		return employeeTaxDocumentForm;
	}

}
