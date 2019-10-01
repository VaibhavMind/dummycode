/**
 * @author ragulapraveen
 *
 */
package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.CompanyDocumentConditionDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyDocument_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.DocumentCategoryMaster_;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.MonthMaster_;

/**
 * The Class CompanyDocumentDAOImpl.
 * 
 * 
 */

@Repository
public class CompanyDocumentDAOImpl extends BaseDAO implements
		CompanyDocumentDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CompanyDocument companyDocument = new CompanyDocument();
		return companyDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDAO#save(com.payasia.dao.bean.CompanyDocument
	 * )
	 */
	@Override
	public CompanyDocument save(CompanyDocument companyDocument) {
		CompanyDocument persistObj = companyDocument;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CompanyDocument) getBaseEntity();
			beanUtil.copyProperties(persistObj, companyDocument);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDAO#findByCondition(com.payasia.common
	 * .dto.CompanyDocumentConditionDTO, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<CompanyDocument> findByCondition(
			CompanyDocumentConditionDTO companyDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocument> criteriaQuery = cb
				.createQuery(CompanyDocument.class);
		Root<CompanyDocument> empRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(empRoot);

		Join<CompanyDocument, DocumentCategoryMaster> companyDocCategoryJoin = empRoot
				.join(CompanyDocument_.documentCategoryMaster);

		Predicate restriction = cb.conjunction();

		if (companyDocumentConditionDTO.getUploadedDate() != null) {
			restriction = cb.and(restriction, cb.equal(
					empRoot.get(CompanyDocument_.uploadedDate),
					companyDocumentConditionDTO.getUploadedDate()));

		}
		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getCategory())) {

			restriction = cb.and(restriction, cb.equal(companyDocCategoryJoin
					.get(DocumentCategoryMaster_.documentCategoryId),
					companyDocumentConditionDTO.getCategoryId()));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllCompanyDocument(sortDTO,
					empRoot);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<CompanyDocument> companyDocumentQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyDocumentQuery.setFirstResult(getStartPosition(pageDTO));
			companyDocumentQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<CompanyDocument> companyDocumentList = companyDocumentQuery
				.getResultList();

		return companyDocumentList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDAO#getSortPathForAllCompanyDocument(com
	 * .payasia.common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForAllCompanyDocument(SortCondition sortDTO,
			Root<CompanyDocument> empRoot) {

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.COMPANY_DOCUMENT_NAME);
		employeeIsColList.add(SortConstants.COMPANY_DOCUMENT_TYPE);
		employeeIsColList.add(SortConstants.COMPANY_DOCUMENT_UPLOADED_DATE);
		employeeIsColList.add(SortConstants.COMPANY_DOCUMENT_DESCRIPTION);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = empRoot.get(colMap.get(CompanyDocument.class
					+ sortDTO.getColumnName()));
		}

		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDocumentDAO#findByID(long)
	 */
	@Override
	public CompanyDocument findByID(long docId) {
		CompanyDocument companyDocument = super.findById(CompanyDocument.class,
				docId);
		return companyDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDocumentDAO#delete(com.payasia.dao.bean.
	 * CompanyDocument)
	 */
	@Override
	public void delete(CompanyDocument companyDocument) {
		super.delete(companyDocument);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDocumentDAO#update(com.payasia.dao.bean.
	 * CompanyDocument)
	 */
	@Override
	public void update(CompanyDocument companyDocument) {
		super.update(companyDocument);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDAO#getCountForAll(com.payasia.common.
	 * dto.CompanyDocumentConditionDTO)
	 */
	@Override
	public int getCountForAll(
			CompanyDocumentConditionDTO companyDocumentConditionDTO) {

		return findByCondition(companyDocumentConditionDTO, null, null).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDAO#findByCategoryCompanyAndDocumentName
	 * (java.lang.Long, java.lang.String, java.lang.Long)
	 */
	@Override
	public CompanyDocument findByCategoryCompanyAndDocumentName(
			Long categoryId, String documentName, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocument> criteriaQuery = cb
				.createQuery(CompanyDocument.class);
		Root<CompanyDocument> cmpDocRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(cmpDocRoot);

		Join<CompanyDocument, DocumentCategoryMaster> companyDocCategoryJoin = cmpDocRoot
				.join(CompanyDocument_.documentCategoryMaster);

		Join<CompanyDocument, Company> companyDocJoin = cmpDocRoot
				.join(CompanyDocument_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyDocCategoryJoin
				.get(DocumentCategoryMaster_.documentCategoryId), categoryId));

		restriction = cb.and(restriction,
				cb.equal(companyDocJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyDocument> companyDocumentTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyDocument> companyDocList = companyDocumentTypedQuery
				.getResultList();
		if (companyDocList != null && !companyDocList.isEmpty()) {
			return companyDocList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDAO#getDistinctYearList(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public List<Integer> getDistinctYearList(Long companyId, Long categoryId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyDocument> compDocRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(compDocRoot.get(CompanyDocument_.year)).distinct(
				true);
		Join<CompanyDocument, Company> companyDocJoin = compDocRoot
				.join(CompanyDocument_.company);

		Join<CompanyDocument, DocumentCategoryMaster> companyDocCategoryJoin = compDocRoot
				.join(CompanyDocument_.documentCategoryMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyDocCategoryJoin
				.get(DocumentCategoryMaster_.documentCategoryId), categoryId));

		restriction = cb.and(restriction,
				cb.equal(companyDocJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> companyDocumentTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return companyDocumentTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDocumentDAO#findMaxCompanyDocumentId()
	 */
	@Override
	public Long findMaxCompanyDocumentId() {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<CompanyDocument> companyDocumentRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(cb.max(companyDocumentRoot
				.get(CompanyDocument_.documentId)));

		TypedQuery<Long> maxCompanyDocumentQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		Long maxEmpId = maxCompanyDocumentQuery.getSingleResult();
		if (maxEmpId == null) {
			maxEmpId = (long) 0;
		}
		return maxEmpId;

	}

	@Override
	public void updatePayslipReleaseStatus(Long companyId, Long monthId,
			int year, int part, Long documentCategoryId, Boolean released) {

		String queryString = "UPDATE  CompanyDocument cd SET cd.released =:released WHERE cd.month.monthId = :monthId AND cd.year = :year";
		queryString += " AND cd.part = :part  AND cd.company.companyId = :companyId AND cd.documentCategoryMaster.documentCategoryId = :documentCategoryId AND cd.released <>:released";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("released", released);
		q.setParameter("monthId", monthId);
		q.setParameter("year", year);
		q.setParameter("part", part);
		q.setParameter("companyId", companyId);
		q.setParameter("documentCategoryId", documentCategoryId);

		q.executeUpdate();

	}

	@Override
	public CompanyDocument findByConditionSourceTextAndDesc(Long companyId,
			Long documentCategoryId, int year, int part, Long monthId,
			String source, String employeeNumber) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocument> criteriaQuery = cb
				.createQuery(CompanyDocument.class);
		Root<CompanyDocument> cmpDocRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(cmpDocRoot);

		Join<CompanyDocument, DocumentCategoryMaster> companyDocCategoryJoin = cmpDocRoot
				.join(CompanyDocument_.documentCategoryMaster);

		Join<CompanyDocument, Company> companyDocJoin = cmpDocRoot
				.join(CompanyDocument_.company);
		Join<CompanyDocument, MonthMaster> monthDocJoin = cmpDocRoot
				.join(CompanyDocument_.month);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyDocCategoryJoin
				.get(DocumentCategoryMaster_.documentCategoryId),
				documentCategoryId));
		restriction = cb.and(restriction,
				cb.equal(companyDocJoin.get(Company_.companyId), companyId));
		if(monthId!=null){
		restriction = cb.and(restriction,
				cb.equal(monthDocJoin.get(MonthMaster_.monthId), monthId));
		}
		restriction = cb.and(restriction,
				cb.equal(cmpDocRoot.get(CompanyDocument_.part), part));
		restriction = cb.and(restriction,
				cb.equal(cmpDocRoot.get(CompanyDocument_.year), year));
		restriction = cb.and(restriction,
				cb.equal(cmpDocRoot.get(CompanyDocument_.source), source));
		restriction = cb.and(restriction, cb.like(
				cmpDocRoot.get(CompanyDocument_.description), employeeNumber
						+ "[_]%"));
		
		restriction = cb.and(restriction,
				cb.equal(cmpDocRoot.get(CompanyDocument_.released), true));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyDocument> companyDocumentTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyDocument> companyDocList = companyDocumentTypedQuery
				.getResultList();
		if (companyDocList != null && !companyDocList.isEmpty()) {
			return companyDocList.get(0);
		}
		return null;

	}

	@Override
	public List<CompanyDocument> findByConditionSourceTextAndDesc(
			Long companyId, Long documentCategoryId, int year, int part,
			Long monthId, String source) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocument> criteriaQuery = cb
				.createQuery(CompanyDocument.class);
		Root<CompanyDocument> cmpDocRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(cmpDocRoot);

		Join<CompanyDocument, DocumentCategoryMaster> companyDocCategoryJoin = cmpDocRoot
				.join(CompanyDocument_.documentCategoryMaster);

		Join<CompanyDocument, Company> companyDocJoin = cmpDocRoot
				.join(CompanyDocument_.company);
		Join<CompanyDocument, MonthMaster> monthDocJoin = cmpDocRoot
				.join(CompanyDocument_.month);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyDocCategoryJoin
				.get(DocumentCategoryMaster_.documentCategoryId),
				documentCategoryId));
		restriction = cb.and(restriction,
				cb.equal(companyDocJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(monthDocJoin.get(MonthMaster_.monthId), monthId));
		restriction = cb.and(restriction,
				cb.equal(cmpDocRoot.get(CompanyDocument_.part), part));
		restriction = cb.and(restriction,
				cb.equal(cmpDocRoot.get(CompanyDocument_.year), year));
		restriction = cb.and(restriction,
				cb.equal(cmpDocRoot.get(CompanyDocument_.source), source));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyDocument> companyDocumentTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyDocument> companyDocList = companyDocumentTypedQuery
				.getResultList();
		return companyDocList;

	}

	@Override
	public List<Integer> findByConditionSourceTextAndDescWithoutPart(
			Long companyId, Long documentCategoryId, int year, Long monthId,
			String source, String employeeNumber) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyDocument> cmpDocRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(
				cmpDocRoot.get(CompanyDocument_.part).as(Integer.class))
				.distinct(true);

		Join<CompanyDocument, DocumentCategoryMaster> companyDocCategoryJoin = cmpDocRoot
				.join(CompanyDocument_.documentCategoryMaster);

		Join<CompanyDocument, Company> companyDocJoin = cmpDocRoot
				.join(CompanyDocument_.company);
		Join<CompanyDocument, MonthMaster> monthDocJoin = cmpDocRoot
				.join(CompanyDocument_.month);
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyDocCategoryJoin
				.get(DocumentCategoryMaster_.documentCategoryId),
				documentCategoryId));
		restriction = cb.and(restriction,
				cb.equal(companyDocJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(monthDocJoin.get(MonthMaster_.monthId), monthId));
		restriction = cb.and(restriction,
				cb.equal(cmpDocRoot.get(CompanyDocument_.year), year));
		restriction = cb.and(restriction, cb.or(cb.equal(
				cmpDocRoot.get(CompanyDocument_.source),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT), cb
				.equal(cmpDocRoot.get(CompanyDocument_.source),
						PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF)));
		restriction = cb.and(restriction, cb.like(
				cmpDocRoot.get(CompanyDocument_.description), employeeNumber
						+ "[_]%"));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> companyDocumentTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<Integer> companyDocList = companyDocumentTypedQuery
				.getResultList();
		if (companyDocList != null && !companyDocList.isEmpty()) {
			return companyDocList;
		}
		return null;

	}

	@Override
	public List<CompanyDocument> getPayslips(
			PayslipConditionDTO payslipConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocument> criteriaQuery = cb
				.createQuery(CompanyDocument.class);
		Root<CompanyDocument> companyDocumentRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(companyDocumentRoot);

		Join<CompanyDocument, Company> companyJoin = companyDocumentRoot
				.join(CompanyDocument_.company);

		Join<CompanyDocument, MonthMaster> monthJoin = companyDocumentRoot
				.join(CompanyDocument_.month);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				companyJoin.get(Company_.companyId),
				payslipConditionDTO.getCompanyId()));

		restriction = cb.and(restriction, cb.like(
				companyDocumentRoot.get(CompanyDocument_.description),
				payslipConditionDTO.getEmployeeNumber()));

		restriction = cb.and(restriction, cb.or(cb.equal(
				companyDocumentRoot.get(CompanyDocument_.source),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT), cb
				.equal(companyDocumentRoot.get(CompanyDocument_.source),
						PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF)));

		restriction = cb.and(restriction, cb.equal(
				companyDocumentRoot.get(CompanyDocument_.released), true));

		criteriaQuery.where(restriction);

		List<Order> orderByList = new ArrayList<>();
		orderByList
				.add(cb.desc(companyDocumentRoot.get(CompanyDocument_.year)));
		orderByList.add(cb.desc(monthJoin.get(MonthMaster_.monthId)));
		orderByList.add(cb.asc(companyDocumentRoot.get(CompanyDocument_.part)));
		criteriaQuery.orderBy(orderByList);

		TypedQuery<CompanyDocument> companyDocumentQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		companyDocumentQuery.setMaxResults(PayAsiaConstants.NO_OF_PAYSLIPS);

		List<CompanyDocument> companyDocumentList = companyDocumentQuery
				.getResultList();

		return companyDocumentList;
	}

	@Override
	public List<Integer> getParts(PayslipConditionDTO payslipConditionDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyDocument> companyDocumentRoot = criteriaQuery
				.from(CompanyDocument.class);
		criteriaQuery.select(
				companyDocumentRoot.get(CompanyDocument_.part)
						.as(Integer.class)).distinct(true);

		Join<CompanyDocument, MonthMaster> monthMasterJoin = companyDocumentRoot
				.join(CompanyDocument_.month);
		Join<CompanyDocument, Company> companyJoin = companyDocumentRoot
				.join(CompanyDocument_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				monthMasterJoin.get(MonthMaster_.monthId),
				payslipConditionDTO.getMonthMasterId()));

		restriction = cb.and(restriction, cb.equal(
				companyDocumentRoot.get(CompanyDocument_.year),
				payslipConditionDTO.getYear()));

		restriction = cb.and(restriction, cb.equal(
				companyJoin.get(Company_.companyId),
				payslipConditionDTO.getCompanyId()));

		restriction = cb.and(restriction, cb.like(
				companyDocumentRoot.get(CompanyDocument_.description),
				payslipConditionDTO.getEmployeeNumber()));

		restriction = cb.and(restriction, cb.equal(
				companyDocumentRoot.get(CompanyDocument_.released), true));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(companyDocumentRoot
				.get(CompanyDocument_.part)));

		TypedQuery<Integer> companyDocumentQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		companyDocumentQuery.setMaxResults(PayAsiaConstants.NO_OF_PAYSLIPS);

		List<Integer> companyDocumentList = companyDocumentQuery
				.getResultList();

		return companyDocumentList;
	}

}
