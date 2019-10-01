package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmpNoSeriesMaster;

/**
 * The Interface EmpNoSeriesMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmpNoSeriesMasterDAO {

	/**
	 * Save EmpNoSeriesMaster Object.
	 * 
	 * @param empNoSeriesMaster
	 *            the emp no series master
	 */
	void save(EmpNoSeriesMaster empNoSeriesMaster);

	/**
	 * Find EmpNoSeriesMaster Objects List by condition companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<EmpNoSeriesMaster> findByConditionCompany(long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Gets the sort path for emp no series.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param empNoSrRoot
	 *            the emp no sr root
	 * @param empNoSrRootRootJoin
	 *            the emp no sr root root join
	 * @return the sort path for emp no series
	 */
	Path<String> getSortPathForEmpNoSeries(SortCondition sortDTO,
			Root<EmpNoSeriesMaster> empNoSrRoot,
			Join<EmpNoSeriesMaster, Company> empNoSrRootRootJoin);

	/**
	 * Delete EmpNoSeriesMaster Object.
	 * 
	 * @param empNoSeriesMaster
	 *            the emp no series master
	 */
	void delete(EmpNoSeriesMaster empNoSeriesMaster);

	/**
	 * Find EmpNoSeriesMaster Object by empNoSeriesId.
	 * 
	 * @param empNoSeriesId
	 *            the emp no series id
	 * @return the emp no series master
	 */
	EmpNoSeriesMaster findById(long empNoSeriesId);

	/**
	 * Update EmpNoSeriesMaster Object.
	 * 
	 * @param empNoSeriesMaster
	 *            the emp no series master
	 */
	void update(EmpNoSeriesMaster empNoSeriesMaster);

	/**
	 * Gets the count for emp no series By companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the count for emp no series
	 */
	int getCountForEmpNoSeries(Long companyId);

	/**
	 * Find active series by companyId.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<EmpNoSeriesMaster> findActiveSeriesByCompany(long companyId);
	
	EmpNoSeriesMaster findById(long empNoSeriesId,Long companyId);
}
