package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.WorkdayFtpImportHistoryDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.WorkdayFtpImportHistoryDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.WorkdayFtpImportHistory;
import com.payasia.dao.bean.WorkdayFtpImportHistory_;

@Repository
public class WorkdayFtpImportHistoryDAOImpl extends BaseDAO implements WorkdayFtpImportHistoryDAO {

	@Override
	protected Object getBaseEntity() {

		return new WorkdayFtpImportHistory();
	}

	@Override
	public void save(WorkdayFtpImportHistory ftpImportHistory) {
		super.save(ftpImportHistory);
	}

	@Override
	public WorkdayFtpImportHistory saveHistory(WorkdayFtpImportHistory ftpImportHistory) {
		WorkdayFtpImportHistory persistObj = ftpImportHistory;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (WorkdayFtpImportHistory) getBaseEntity();
			beanUtil.copyProperties(persistObj, ftpImportHistory);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
		return persistObj;
	}

	@Override
	public List<WorkdayFtpImportHistory> findByCondition(Long companyId, WorkdayFtpImportHistoryDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, String importType) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFtpImportHistory> criteriaQuery = cb.createQuery(WorkdayFtpImportHistory.class);
		Root<WorkdayFtpImportHistory> importHistoryRoot = criteriaQuery.from(WorkdayFtpImportHistory.class);
		criteriaQuery.select(importHistoryRoot);

		Join<WorkdayFtpImportHistory, Company> impHisRootRootJoin = importHistoryRoot
				.join(WorkdayFtpImportHistory_.company);
		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(conditionDTO.getImportFileName())) {
			restriction = cb.and(restriction,
					cb.like(cb.upper(importHistoryRoot.get(WorkdayFtpImportHistory_.importFileName)),
							conditionDTO.getImportFileName().toUpperCase()));
		}
		if (conditionDTO.getFromDate() != null) {
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					(importHistoryRoot.get(WorkdayFtpImportHistory_.createdDate)), conditionDTO.getFromDate()));
		}
		if (conditionDTO.getToDate() != null) {
			restriction = cb.and(restriction, cb.lessThanOrEqualTo(
					(importHistoryRoot.get(WorkdayFtpImportHistory_.createdDate)), conditionDTO.getToDate()));
		}
		if (StringUtils.isNotBlank(conditionDTO.getImportStatus())) {
			if ("visible".equals(conditionDTO.getImportStatus())) {
				restriction = cb.and(restriction,
						cb.equal((importHistoryRoot.get(WorkdayFtpImportHistory_.importStatus)), true));
			}
			if ("hidden".equals(conditionDTO.getImportStatus())) {
				restriction = cb.and(restriction,
						cb.equal((importHistoryRoot.get(WorkdayFtpImportHistory_.importStatus)), false));
			}
		}
		if ("payrolldata".equalsIgnoreCase(importType)) {
			restriction = cb.and(restriction,
					cb.equal(importHistoryRoot.get(WorkdayFtpImportHistory_.importType), "Payroll"));
		} else {
			restriction = cb.and(restriction,
					cb.equal(importHistoryRoot.get(WorkdayFtpImportHistory_.importType), "Employee"));
		}
		restriction = cb.and(restriction, cb.equal((impHisRootRootJoin.get(Company_.companyId)), companyId));
		criteriaQuery.where(restriction);
		if (sortDTO != null) {
			Path<String> sortPath = getSortPathForFtpImportHistory(sortDTO, importHistoryRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
			}
		}
		TypedQuery<WorkdayFtpImportHistory> impHisTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			impHisTypedQuery.setFirstResult(getStartPosition(pageDTO));
			impHisTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<WorkdayFtpImportHistory> ftpImportHistoryList = impHisTypedQuery.getResultList();
		return ftpImportHistoryList;
	}

	private Path<String> getSortPathForFtpImportHistory(SortCondition sortDTO, Root<WorkdayFtpImportHistory> importHistoryRoot) {
		
		List<String> impHisIsColList = new ArrayList<String>();
		
		impHisIsColList.add(SortConstants.FTP_IMPORT_HISTORY_CREATED_DATE);
		impHisIsColList.add(SortConstants.FTP_IMPORT_HISTORY_FILE_NAME);
		impHisIsColList.add(SortConstants.FTP_IMPORT_HISTORY_STATUS);
		impHisIsColList.add(SortConstants.FTP_IMPORT_HISTORY_TO_NO_RECORDS);
		 
		Path<String> sortPath = null;
		if (impHisIsColList.contains(sortDTO.getColumnName())) {
			sortPath = importHistoryRoot.get(colMap.get(WorkdayFtpImportHistory.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public int getCountForImportHistory(Long companyId, WorkdayFtpImportHistoryDTO conditionDTO, String importType) {

		return findByCondition(companyId, conditionDTO, null, null, importType).size();
	}
	
	@Override
	public WorkdayFtpImportHistory findById(Long importHistoryId) {
		return super.findById(WorkdayFtpImportHistory.class, importHistoryId);
	}

}
