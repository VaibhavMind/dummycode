package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.form.EmployeeNumberSrForm;
import com.payasia.common.form.EmployeeNumberSrFormResponse;
import com.payasia.common.form.EmployeeTypeForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmpNoSeriesMasterDAO;
import com.payasia.dao.EmployeeTypeMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmpNoSeriesMaster;
import com.payasia.dao.bean.EmployeeTypeMaster;
import com.payasia.logic.EmployeeNumberSrLogic;

/**
 * The Class EmployeeNumberSrLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class EmployeeNumberSrLogicImpl implements EmployeeNumberSrLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeNumberSrLogicImpl.class);

	/** The employee type master dao. */
	@Resource
	EmployeeTypeMasterDAO employeeTypeMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The emp no series master dao. */
	@Resource
	EmpNoSeriesMasterDAO empNoSeriesMasterDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeNumberSrLogic#viewEmpNoSr(java.lang.Long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public EmployeeNumberSrFormResponse viewEmpNoSr(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		List<EmpNoSeriesMaster> empNoSeriesList = empNoSeriesMasterDAO
				.findByConditionCompany(companyId, pageDTO, sortDTO);

		List<EmployeeNumberSrForm> employeeNumberSrFormList = new ArrayList<EmployeeNumberSrForm>();

		for (EmpNoSeriesMaster empNoSeries : empNoSeriesList) {

			EmployeeNumberSrForm employeeNumberSrForm = new EmployeeNumberSrForm();
			employeeNumberSrForm.setCompanyId(empNoSeries.getCompany()
					.getCompanyId());
			/*ID ENCRYPT*/
			employeeNumberSrForm.setEmpNoSeriesId(FormatPreserveCryptoUtil.encrypt(empNoSeries
					.getEmpNoSeriesId()));

			employeeNumberSrForm.setDescription(empNoSeries.getSeriesDesc());

			employeeNumberSrForm.setPrefix(empNoSeries.getPrefix());
			employeeNumberSrForm.setSuffix(empNoSeries.getSuffix());
			employeeNumberSrForm.setActive(empNoSeries.getActive());

			employeeNumberSrFormList.add(employeeNumberSrForm);

		}
		EmployeeNumberSrFormResponse response = new EmployeeNumberSrFormResponse();
		int recordSize = empNoSeriesMasterDAO.getCountForEmpNoSeries(companyId);
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
		response.setEmployeeNoSrList(employeeNumberSrFormList);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeNumberSrLogic#saveNewSeries(com.payasia.common
	 * .form.EmployeeNumberSrForm, java.lang.Long)
	 */
	@Override
	public String saveNewSeries(EmployeeNumberSrForm employeeNumberSrForm,
			Long companyId) {
		boolean status = true;
		EmpNoSeriesMaster empNoSeriesMaster = new EmpNoSeriesMaster();
		String description = "";
		try {
			empNoSeriesMaster.setSeriesDesc(URLDecoder.decode(
					employeeNumberSrForm.getDescription(), "UTF-8"));
			description = URLDecoder.decode(
					employeeNumberSrForm.getDescription(), "UTF-8");
			empNoSeriesMaster.setPrefix(URLDecoder.decode(
					employeeNumberSrForm.getPrefix(), "UTF-8"));
			empNoSeriesMaster.setSuffix(URLDecoder.decode(
					employeeNumberSrForm.getSuffix(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		empNoSeriesMaster.setActive(employeeNumberSrForm.isActive());

		Company company = companyDAO.findById(companyId);

		empNoSeriesMaster.setCompany(company);

		List<EmpNoSeriesMaster> empNoSeriesMasterList = empNoSeriesMasterDAO
				.findByConditionCompany(companyId, null, null);
		if (empNoSeriesMasterList != null) {
			for (EmpNoSeriesMaster empNoSeriesMasterVO : empNoSeriesMasterList) {
				if (empNoSeriesMasterVO.getSeriesDesc().toUpperCase()
						.equals(description)) {
					status = false;
				}
			}
		}
		if (status) {
			empNoSeriesMasterDAO.save(empNoSeriesMaster);
			return "employee.number.series.are.successfully.saved";
		} else {
			return "duplicate.number.series.please.add.other.number.series";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeNumberSrLogic#editSeries(java.lang.Long,
	 * com.payasia.common.form.EmployeeNumberSrForm)
	 */
	@Override
	public String editSeries(Long companyId,
			EmployeeNumberSrForm employeeNumberSrForm) {
		EmpNoSeriesMaster empNoSeriesMaster = new EmpNoSeriesMaster();

		empNoSeriesMaster.setEmpNoSeriesId(employeeNumberSrForm
				.getEmpNoSeriesId());
		String description = "";
		try {
			empNoSeriesMaster.setSeriesDesc(URLDecoder.decode(
					employeeNumberSrForm.getDescription(), "UTF-8"));
			description = URLDecoder.decode(
					employeeNumberSrForm.getDescription(), "UTF-8");
			empNoSeriesMaster.setPrefix(URLDecoder.decode(
					employeeNumberSrForm.getPrefix(), "UTF-8"));
			empNoSeriesMaster.setSuffix(URLDecoder.decode(
					employeeNumberSrForm.getSuffix(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		empNoSeriesMaster.setActive(employeeNumberSrForm.isActive());

		Company company = companyDAO.findById(companyId);

		empNoSeriesMaster.setCompany(company);

		List<EmpNoSeriesMaster> empNoSeriesMasterList = empNoSeriesMasterDAO
				.findByConditionCompany(companyId, null, null);
		boolean status = true;
		if (empNoSeriesMasterList != null) {
			for (EmpNoSeriesMaster empNoSeriesMasterVO : empNoSeriesMasterList) {
				if (empNoSeriesMasterVO.getSeriesDesc().toUpperCase()
						.equals(description)) {
					if (empNoSeriesMasterVO.getEmpNoSeriesId() != employeeNumberSrForm
							.getEmpNoSeriesId()) {
						status = false;
					}

				}
			}
		}
		if (status) {
			empNoSeriesMasterDAO.update(empNoSeriesMaster);
			return "employee.number.series.are.successfully.updated";
		} else {
			return "duplicate.number.series.please.add.other.number.series";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeNumberSrLogic#deleteSeries(long)
	 */
	@Override
	public String deleteSeries(long empNoSeriesId) {

		EmpNoSeriesMaster empNoSeriesMaster = empNoSeriesMasterDAO
				.findById(empNoSeriesId);
		try {
			empNoSeriesMasterDAO.delete(empNoSeriesMaster);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return "payasia.emp.series.cannot.be.deleted";
		}
		return "employee.number.series.are.successfully.deleted";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeNumberSrLogic#getEmployeeTypeList()
	 */
	@Override
	public List<EmployeeTypeForm> getEmployeeTypeList(Long companyId) {

		List<EmployeeTypeMaster> employeeTypeMasterList = employeeTypeMasterDAO
				.findByConditionCompany(companyId);

		List<EmployeeTypeForm> employeeTypeFormList = new ArrayList<EmployeeTypeForm>();

		for (EmployeeTypeMaster employeeTypeMaster : employeeTypeMasterList) {

			EmployeeTypeForm employeeTypeForm = new EmployeeTypeForm();

			employeeTypeForm.setCompanyId(employeeTypeMaster.getCompany()
					.getCompanyId());
			employeeTypeForm.setEmpTypeId(employeeTypeMaster.getEmpTypeId());
			employeeTypeForm.setEmpType(employeeTypeMaster.getEmpType());
			employeeTypeForm
					.setEmpTypeDesc(employeeTypeMaster.getEmpTypeDesc());

			employeeTypeFormList.add(employeeTypeForm);

		}

		return employeeTypeFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeNumberSrLogic#getDataForSeries(long)
	 */
	@Override
	public EmployeeNumberSrForm getDataForSeries(long empNoSeriesId) {

		EmpNoSeriesMaster empNoSeries = empNoSeriesMasterDAO
				.findById(empNoSeriesId);

		EmployeeNumberSrForm employeeNumberSrForm = new EmployeeNumberSrForm();
		employeeNumberSrForm.setCompanyId(empNoSeries.getCompany()
				.getCompanyId());
		employeeNumberSrForm.setEmpNoSeriesId(empNoSeries.getEmpNoSeriesId());
		employeeNumberSrForm.setDescription(empNoSeries.getSeriesDesc());
		employeeNumberSrForm.setPrefix(empNoSeries.getPrefix());
		employeeNumberSrForm.setSuffix(empNoSeries.getSuffix());
		employeeNumberSrForm.setActive(empNoSeries.getActive());

		return employeeNumberSrForm;
	}

}
