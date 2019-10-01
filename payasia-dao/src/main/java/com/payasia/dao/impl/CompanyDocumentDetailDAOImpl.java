package com.payasia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.CompanyDocumentConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyDocumentDetailDAO;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyDocumentDetail;
import com.payasia.dao.bean.CompanyDocumentDetail_;
import com.payasia.dao.bean.CompanyDocument_;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.DocumentCategoryMaster_;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.MonthMaster_;

/**
 * The Class CompanyDocumentDetailDAOImpl.
 */
@Repository
public class CompanyDocumentDetailDAOImpl extends BaseDAO implements CompanyDocumentDetailDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CompanyDocumentDetail companyDocumentDetail = new CompanyDocumentDetail();
		return companyDocumentDetail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDocumentDetailDAO#save(com.payasia.dao.bean.
	 * CompanyDocumentDetail)
	 */
	@Override
	public void save(CompanyDocumentDetail companyDocumentDetail) {
		super.save(companyDocumentDetail);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDetailDAO#findByCompanyDocumentId(java
	 * .lang.Long, java.lang.String, java.lang.Integer, java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public CompanyDocumentDetail findByCompanyDocumentId(Long companyId, String fileName, Integer year,
			String categoryName, Long categoryId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocumentDetail> criteriaQuery = cb.createQuery(CompanyDocumentDetail.class);
		Root<CompanyDocumentDetail> companyDocumentDetailRoot = criteriaQuery.from(CompanyDocumentDetail.class);
		criteriaQuery.select(companyDocumentDetailRoot);

		Join<CompanyDocumentDetail, CompanyDocument> companyDocumentJoin = companyDocumentDetailRoot
				.join(CompanyDocumentDetail_.companyDocument);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)), fileName));

		restriction = cb.and(restriction,
				cb.equal(companyDocumentJoin.get(CompanyDocument_.company).get("companyId").as(Long.class), companyId));

		restriction = cb.and(restriction, cb.equal(companyDocumentJoin.get(CompanyDocument_.documentCategoryMaster)
				.get("documentCategoryId").as(Long.class), categoryId));

		if (categoryName.equals(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)
				|| categoryName.equals(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {

			restriction = cb.and(restriction, cb.equal(companyDocumentJoin.get(CompanyDocument_.year), year));
		} else {

			restriction = cb.and(restriction, cb.isNull(companyDocumentJoin.get(CompanyDocument_.year)));
		}

		criteriaQuery.where(restriction);

		TypedQuery<CompanyDocumentDetail> maxCompanyDocumentDetailQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyDocumentDetail> companyDocumentDetailList = maxCompanyDocumentDetailQuery.getResultList();
		if (companyDocumentDetailList != null && !companyDocumentDetailList.isEmpty()) {
			return companyDocumentDetailList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDetailDAO#update(com.payasia.dao.bean.
	 * CompanyDocumentDetail)
	 */
	@Override
	public void update(CompanyDocumentDetail companyDocumentDetail) {
		super.update(companyDocumentDetail);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDetailDAO#findByCondition(com.payasia.
	 * common.dto.CompanyDocumentConditionDTO,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public List<CompanyDocumentDetail> findByCondition(CompanyDocumentConditionDTO companyDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocumentDetail> criteriaQuery = cb.createQuery(CompanyDocumentDetail.class);
		Root<CompanyDocumentDetail> companyDocumentDetailRoot = criteriaQuery.from(CompanyDocumentDetail.class);
		criteriaQuery.select(companyDocumentDetailRoot);

		Join<CompanyDocumentDetail, CompanyDocument> companyDocumentJoin = companyDocumentDetailRoot
				.join(CompanyDocumentDetail_.companyDocument);

		Path<Long> documentID = companyDocumentJoin.get(CompanyDocument_.documentId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyDocumentJoin.get(CompanyDocument_.company).get("companyId").as(Long.class), companyId));

		if (companyDocumentConditionDTO.getCompanyDocumentId() != null) {

			restriction = cb.and(restriction, cb.equal(documentID, companyDocumentConditionDTO.getCompanyDocumentId()));
		}

		if (companyDocumentConditionDTO.getYear() != null) {
			restriction = cb.and(restriction,
					cb.equal(companyDocumentJoin.get(CompanyDocument_.year), companyDocumentConditionDTO.getYear()));
		}

		if (companyDocumentConditionDTO.getCategoryId()!=null) {

			restriction = cb
					.and(restriction,
							cb.equal(
									companyDocumentJoin.get(CompanyDocument_.documentCategoryMaster)
											.get("documentCategoryId").as(Long.class),
									companyDocumentConditionDTO.getCategoryId()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDocumentName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)),
							companyDocumentConditionDTO.getDocumentName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(companyDocumentJoin.get(CompanyDocument_.description)),
					companyDocumentConditionDTO.getDescription().toUpperCase() + "%"));
		}
		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getType())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileType)),
							companyDocumentConditionDTO.getType().toUpperCase()));
		}

		if (companyDocumentConditionDTO.getCategoryName().equals(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)
				|| companyDocumentConditionDTO.getCategoryName().equals(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)
				|| companyDocumentConditionDTO.getCategoryName()
						.equals(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)) {
			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)),
							companyDocumentConditionDTO.getEmployeeNumber().toUpperCase()));
		}
		restriction = cb.and(restriction, cb.equal(companyDocumentJoin.get(CompanyDocument_.source),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_DOCUMENT));
		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, companyDocumentDetailRoot,
					companyDocumentJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}
			if (sortPath == null) {
				criteriaQuery.orderBy(cb.desc(companyDocumentDetailRoot.get(CompanyDocumentDetail_.companyDocumentDetailID)));
			}
		}

	
		
		TypedQuery<CompanyDocumentDetail> companyDocumentQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyDocumentQuery.setFirstResult(getStartPosition(pageDTO));
			companyDocumentQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<CompanyDocumentDetail> companyDocumentDetailList = companyDocumentQuery.getResultList();

		return companyDocumentDetailList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDocumentDetailDAO#findById(long)
	 */
	@Override
	public CompanyDocumentDetail findById(long docId) {

		CompanyDocumentDetail companyDocumentDetail = super.findById(CompanyDocumentDetail.class, docId);
		return companyDocumentDetail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDetailDAO#delete(com.payasia.dao.bean.
	 * CompanyDocumentDetail)
	 */
	@Override
	public void delete(CompanyDocumentDetail companyDocumentDetail) {
		super.delete(companyDocumentDetail);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDetailDAO#getCountForCondition(com.payasia
	 * .common.dto.CompanyDocumentConditionDTO, java.lang.Long)
	 */
	@Override
	public Integer getCountForCondition(CompanyDocumentConditionDTO companyDocumentConditionDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyDocumentDetail> companyDocumentDetailRoot = criteriaQuery.from(CompanyDocumentDetail.class);
		criteriaQuery.select(cb.count(companyDocumentDetailRoot).as(Integer.class));

		Join<CompanyDocumentDetail, CompanyDocument> companyDocumentJoin = companyDocumentDetailRoot
				.join(CompanyDocumentDetail_.companyDocument);

		Path<Long> documentID = companyDocumentJoin.get(CompanyDocument_.documentId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyDocumentJoin.get(CompanyDocument_.company).get("companyId").as(Long.class), companyId));

		if (companyDocumentConditionDTO.getCompanyDocumentId() != null) {

			restriction = cb.and(restriction, cb.equal(documentID, companyDocumentConditionDTO.getCompanyDocumentId()));
		}

		if (companyDocumentConditionDTO.getYear() != null) {
			restriction = cb.and(restriction,
					cb.equal(companyDocumentJoin.get(CompanyDocument_.year), companyDocumentConditionDTO.getYear()));
		}

		if (companyDocumentConditionDTO.getCategoryId()!=null) {

			restriction = cb
					.and(restriction,
							cb.equal(
									companyDocumentJoin.get(CompanyDocument_.documentCategoryMaster)
											.get("documentCategoryId").as(Long.class),
									companyDocumentConditionDTO.getCategoryId()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDocumentName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)),
							companyDocumentConditionDTO.getDocumentName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(companyDocumentJoin.get(CompanyDocument_.description)),
					companyDocumentConditionDTO.getDescription().toUpperCase()));
		}
		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getType())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileType)),
							companyDocumentConditionDTO.getType().toUpperCase()));
		}

		if (companyDocumentConditionDTO.getCategoryName().equals(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)
				|| companyDocumentConditionDTO.getCategoryName()
						.equals(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)
						|| companyDocumentConditionDTO.getCategoryName()
						.equals(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)) {
			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)),
							companyDocumentConditionDTO.getEmployeeNumber().toUpperCase() + "%"));
		}
		
		// New
		restriction = cb.and(restriction, cb.equal(companyDocumentJoin.get(CompanyDocument_.source),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_DOCUMENT));
		criteriaQuery.where(restriction);

		TypedQuery<Integer> companyDocumentQuery = entityManagerFactory.createQuery(criteriaQuery);

		return companyDocumentQuery.getSingleResult();
	}

	/**
	 * Gets the sort path for search employee.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyDocumentDetailRoot
	 *            the company document detail root
	 * @param companyDocumentJoin
	 *            the company document join
	 * @return the sort path for search employee
	 */
	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<CompanyDocumentDetail> companyDocumentDetailRoot,
			Join<CompanyDocumentDetail, CompanyDocument> companyDocumentJoin) {

		List<String> companyDocumentList = new ArrayList<String>();
		companyDocumentList.add(SortConstants.COMPANY_DOCUMENT_DETAIL_YEAR);
		companyDocumentList.add(SortConstants.COMPANY_DOCUMENT_DETAIL_DESCRIPTION);

		List<String> companyDocumentDetailList = new ArrayList<String>();

		companyDocumentDetailList.add(SortConstants.COMPANY_DOCUMENT_DETAIL_DOCUMENT_NAME);
		companyDocumentDetailList.add(SortConstants.COMPANY_DOCUMENT_DETAIL_DOCTYPE);

		Path<String> sortPath = null;
		if (companyDocumentDetailList.contains(sortDTO.getColumnName())) {
			sortPath = companyDocumentDetailRoot.get(colMap.get(CompanyDocumentDetail.class + sortDTO.getColumnName()));
		}

		if (companyDocumentList.contains(sortDTO.getColumnName())) {
			sortPath = companyDocumentJoin.get(colMap.get(CompanyDocument.class + sortDTO.getColumnName()));
		}

		Join<CompanyDocument, DocumentCategoryMaster> documentCategoryMasterJoin = companyDocumentJoin
				.join(CompanyDocument_.documentCategoryMaster);
		List<String> companyDocumentCategoryList = new ArrayList<String>();
		companyDocumentCategoryList.add(SortConstants.COMPANY_DOCUMENT_DETAIL_CATEGORY);
		if (companyDocumentCategoryList.contains(sortDTO.getColumnName())) {
			sortPath = documentCategoryMasterJoin
					.get(colMap.get(DocumentCategoryMaster.class + sortDTO.getColumnName()));
		}

		return sortPath;
	}


	@Override
	public int getCountForConditionCmp(CompanyDocumentConditionDTO companyDocumentConditionDTO, Long companyId) {
		return findByConditionCmp(companyDocumentConditionDTO, null, null, companyId).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyDocumentDetailDAO#findByCondition(com.payasia.
	 * common.dto.CompanyDocumentConditionDTO,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public List<CompanyDocumentDetail> findByConditionCmp(CompanyDocumentConditionDTO companyDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocumentDetail> criteriaQuery = cb.createQuery(CompanyDocumentDetail.class);
		Root<CompanyDocumentDetail> companyDocumentDetailRoot = criteriaQuery.from(CompanyDocumentDetail.class);
		criteriaQuery.select(companyDocumentDetailRoot);

		Join<CompanyDocumentDetail, CompanyDocument> companyDocumentJoin = companyDocumentDetailRoot
				.join(CompanyDocumentDetail_.companyDocument);

		Path<Long> documentID = companyDocumentJoin.get(CompanyDocument_.documentId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyDocumentJoin.get(CompanyDocument_.company).get("companyId").as(Long.class), companyId));

		if (companyDocumentConditionDTO.getCompanyDocumentId() != null) {

			restriction = cb.and(restriction, cb.equal(documentID, companyDocumentConditionDTO.getCompanyDocumentId()));
		}
		boolean noResult = false;
		if (companyDocumentConditionDTO.getCompanyDocumentIdList() != null) {

			if (companyDocumentConditionDTO.getCompanyDocumentIdList().size() > 0) {
				restriction = cb.and(restriction,
						documentID.in(companyDocumentConditionDTO.getCompanyDocumentIdList()));
			} else {
				noResult = true;
			}

		}

		if (companyDocumentConditionDTO.getYear() != null) {
			restriction = cb.and(restriction,
					cb.equal(companyDocumentJoin.get(CompanyDocument_.year), companyDocumentConditionDTO.getYear()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getCategory())) {

			restriction = cb
					.and(restriction,
							cb.equal(
									companyDocumentJoin.get(CompanyDocument_.documentCategoryMaster)
											.get("documentCategoryId").as(Long.class),
									companyDocumentConditionDTO.getCategoryId()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDocumentName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)),
							companyDocumentConditionDTO.getDocumentName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(companyDocumentJoin.get(CompanyDocument_.description)),
					companyDocumentConditionDTO.getDescription().toUpperCase()));
		}
		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getType())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileType)),
							companyDocumentConditionDTO.getType().toUpperCase()));
		}

		restriction = cb.and(restriction, cb.equal(companyDocumentJoin.get(CompanyDocument_.source),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_DOCUMENT));

		if (noResult) {
			List<CompanyDocumentDetail> companyDocumentDetailList = new ArrayList<>();
			return companyDocumentDetailList;
		} else {
			criteriaQuery.where(restriction);
		}

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, companyDocumentDetailRoot,
					companyDocumentJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<CompanyDocumentDetail> companyDocumentQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyDocumentQuery.setFirstResult(getStartPosition(pageDTO));
			companyDocumentQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<CompanyDocumentDetail> companyDocumentDetailList = companyDocumentQuery.getResultList();

		return companyDocumentDetailList;
	}

	@Override
	public int getCountForTuplesbyConditionCmp(CompanyDocumentConditionDTO companyDocumentConditionDTO,
			Long companyId) {
		return findTuplesByConditionCmp(companyDocumentConditionDTO, null, null, companyId).size();
	}

	@Override
	public List<Tuple> findTuplesByConditionCmp(CompanyDocumentConditionDTO companyDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);
		Root<CompanyDocumentDetail> companyDocumentDetailRoot = criteriaQuery.from(CompanyDocumentDetail.class);

		Join<CompanyDocumentDetail, CompanyDocument> companyDocumentJoin = companyDocumentDetailRoot
				.join(CompanyDocumentDetail_.companyDocument);

		Join<CompanyDocument, DocumentCategoryMaster> documentCategoryMasterJoin = companyDocumentJoin
				.join(CompanyDocument_.documentCategoryMaster);
		List<Selection<?>> selectionItems = new ArrayList<Selection<?>>();

		selectionItems
				.add(companyDocumentJoin.get(CompanyDocument_.documentId).alias(getAlias(CompanyDocument_.documentId)));
		selectionItems.add(
				companyDocumentJoin.get(CompanyDocument_.description).alias(getAlias(CompanyDocument_.description)));
		selectionItems.add(companyDocumentJoin.get(CompanyDocument_.year).alias(getAlias(CompanyDocument_.year)));
		selectionItems.add(documentCategoryMasterJoin.get(DocumentCategoryMaster_.categoryName)
				.alias(getAlias(DocumentCategoryMaster_.categoryName)));
		selectionItems.add(companyDocumentDetailRoot.get(CompanyDocumentDetail_.companyDocumentDetailID)
				.alias(getAlias(CompanyDocumentDetail_.companyDocumentDetailID)));
		selectionItems.add(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)
				.alias(getAlias(CompanyDocumentDetail_.fileName)));
		selectionItems.add(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileType)
				.alias(getAlias(CompanyDocumentDetail_.fileType)));
		criteriaQuery.multiselect(selectionItems);

		Predicate restriction = cb.conjunction();
		Path<Long> documentID = companyDocumentJoin.get(CompanyDocument_.documentId);
		restriction = cb.and(restriction,
				cb.equal(companyDocumentJoin.get(CompanyDocument_.company).get("companyId").as(Long.class), companyId));

		if (companyDocumentConditionDTO.getCompanyDocumentId() != null) {

			restriction = cb.and(restriction, cb.equal(documentID, companyDocumentConditionDTO.getCompanyDocumentId()));
		}
		boolean noResult = false;
		if (companyDocumentConditionDTO.getCompanyDocumentIdList() != null) {

			if (companyDocumentConditionDTO.getCompanyDocumentIdList().size() > 0) {
				restriction = cb.and(restriction,
						documentID.in(companyDocumentConditionDTO.getCompanyDocumentIdList()));
			} else {
				noResult = true;
			}

		}

		if (companyDocumentConditionDTO.getYear() != null) {
			restriction = cb.and(restriction,
					cb.equal(companyDocumentJoin.get(CompanyDocument_.year), companyDocumentConditionDTO.getYear()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getCategory())) {

			restriction = cb
					.and(restriction,
							cb.equal(
									companyDocumentJoin.get(CompanyDocument_.documentCategoryMaster)
											.get("documentCategoryId").as(Long.class),
									companyDocumentConditionDTO.getCategoryId()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDocumentName())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)),
							companyDocumentConditionDTO.getDocumentName().toUpperCase()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(companyDocumentJoin.get(CompanyDocument_.description)),
					companyDocumentConditionDTO.getDescription().toUpperCase()));
		}
		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getType())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileType)),
							companyDocumentConditionDTO.getType().toUpperCase()));
		}

		restriction = cb.and(restriction, cb.equal(companyDocumentJoin.get(CompanyDocument_.source),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_DOCUMENT));

		if (noResult) {
			List<Tuple> companyDocumentDetailList = new ArrayList<>();
			return companyDocumentDetailList;
		} else {
			criteriaQuery.where(restriction);
		}

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, companyDocumentDetailRoot,
					companyDocumentJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}
		
		if(sortDTO!=null && StringUtils.isBlank(sortDTO.getColumnName())){
		criteriaQuery.orderBy(cb.desc(companyDocumentJoin.get(CompanyDocument_.updatedDate)));
		}
		
		TypedQuery<Tuple> companyDocumentQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyDocumentQuery.setFirstResult(getStartPosition(pageDTO));
			companyDocumentQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<Tuple> companyDocumentDetailList = companyDocumentQuery.getResultList();

		return companyDocumentDetailList;
	}

	@Override
	public CompanyDocumentDetail findByFileNameAndCondition(Long companyId, String fileName, int year, long monthId,
			int part, String companyDocumentUploadSourcePdf) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocumentDetail> criteriaQuery = cb.createQuery(CompanyDocumentDetail.class);
		Root<CompanyDocumentDetail> companyDocumentDetailRoot = criteriaQuery.from(CompanyDocumentDetail.class);
		criteriaQuery.select(companyDocumentDetailRoot);

		Join<CompanyDocumentDetail, CompanyDocument> companyDocumentJoin = companyDocumentDetailRoot
				.join(CompanyDocumentDetail_.companyDocument);
		Join<CompanyDocument, MonthMaster> compDocMonthJoin = companyDocumentJoin.join(CompanyDocument_.month);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocumentDetail_.fileName)), fileName));

		restriction = cb.and(restriction,
				cb.equal(companyDocumentJoin.get(CompanyDocument_.company).get("companyId").as(Long.class), companyId));

		restriction = cb.and(restriction, cb.equal(compDocMonthJoin.get(MonthMaster_.monthId), monthId));

		restriction = cb.and(restriction, cb.equal(companyDocumentJoin.get(CompanyDocument_.year), year));

		restriction = cb.and(restriction, cb.equal(companyDocumentJoin.get(CompanyDocument_.part), part));
		restriction = cb.and(restriction,
				cb.equal(companyDocumentJoin.get(CompanyDocument_.source), companyDocumentUploadSourcePdf));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyDocumentDetail> maxCompanyDocumentDetailQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyDocumentDetail> companyDocumentDetailList = maxCompanyDocumentDetailQuery.getResultList();
		if (companyDocumentDetailList != null && !companyDocumentDetailList.isEmpty()) {
			return companyDocumentDetailList.get(0);
		}
		return null;

	}

	/*
	 * NEW METHOD ADDED AFTER DEVELOPED API
	 */
		
	@Override
	public List<CompanyDocument> findByConditionPdf(CompanyDocumentConditionDTO companyDocumentConditionDTO,
			Long monthId, PageRequest pageDTO, SortCondition sortDTO, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyDocument> criteriaQuery = cb.createQuery(CompanyDocument.class);
		Root<CompanyDocument> companyDocumentDetailRoot = criteriaQuery.from(CompanyDocument.class);
		criteriaQuery.select(companyDocumentDetailRoot);

		Join<CompanyDocument, MonthMaster> compDocMonthJoin = companyDocumentDetailRoot.join(CompanyDocument_.month);

		Path<Long> documentID = companyDocumentDetailRoot.get(CompanyDocument_.documentId);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(companyDocumentDetailRoot.get(CompanyDocument_.company).get("companyId").as(Long.class), companyId));

		if (companyDocumentConditionDTO.getCompanyDocumentId() != null) {
			restriction = cb.and(restriction, cb.equal(documentID, companyDocumentConditionDTO.getCompanyDocumentId()));
		}

		if (companyDocumentConditionDTO.getYear() != null) {
			restriction = cb.and(restriction,
					cb.equal(companyDocumentDetailRoot.get(CompanyDocument_.year), companyDocumentConditionDTO.getYear()));
		}

		if (monthId != null) {
			restriction = cb.and(restriction, cb.equal(compDocMonthJoin.get(MonthMaster_.monthId), monthId));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getCategory())) {

			restriction = cb
					.and(restriction,
							cb.equal(
									companyDocumentDetailRoot.get(CompanyDocument_.documentCategoryMaster)
											.get("documentCategoryId").as(Long.class),
									companyDocumentConditionDTO.getCategoryId()));
		}
		if (companyDocumentConditionDTO.getCategoryName().equals(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)
				|| companyDocumentConditionDTO.getCategoryName().equals(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)
				|| companyDocumentConditionDTO.getCategoryName()
						.equals(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)) {
			restriction = cb.and(restriction,
					cb.like(cb.upper(companyDocumentDetailRoot.get(CompanyDocument_.description)),
							companyDocumentConditionDTO.getEmployeeNumber().toUpperCase() + "%"));
		}

		/*restriction = cb.and(restriction, cb.equal(companyDocumentDetailRoot.get(CompanyDocument_.source),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF));*/
		

		restriction = cb.and(restriction,
				cb.equal(companyDocumentDetailRoot.get(CompanyDocument_.released), true));
		
		criteriaQuery.where(restriction);

//		if (sortDTO != null) {
//
//			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, companyDocumentDetailRoot,
//					companyDocumentDetailRoot);
//			if (sortPath != null) {
//				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
//					criteriaQuery.orderBy(cb.asc(sortPath));
//				}
//				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
//					criteriaQuery.orderBy(cb.desc(sortPath));
//				}
//			}
//
//		}

		TypedQuery<CompanyDocument> companyDocumentQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyDocumentQuery.setFirstResult(getStartPosition(pageDTO));
			companyDocumentQuery.setMaxResults(pageDTO.getPageSize());
		}
		List<CompanyDocument> companyDocumentDetailList = companyDocumentQuery.getResultList();
		return companyDocumentDetailList;
	}
	
	
	@Override
	public Integer getCountForConditionPdf(CompanyDocumentConditionDTO companyDocumentConditionDTO, Long companyId,
			Long monthId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyDocument> companyDocumentRoot = criteriaQuery.from(CompanyDocument.class);
		criteriaQuery.select(cb.count(companyDocumentRoot).as(Integer.class));
		Join<CompanyDocument, MonthMaster> compDocMonthJoin = companyDocumentRoot.join(CompanyDocument_.month);
		Path<Long> documentID = companyDocumentRoot.get(CompanyDocument_.documentId);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyDocumentRoot.get(CompanyDocument_.company).get("companyId").as(Long.class), companyId));

		if (companyDocumentConditionDTO.getCompanyDocumentId() != null) {

			restriction = cb.and(restriction, cb.equal(documentID, companyDocumentConditionDTO.getCompanyDocumentId()));
		}

		if (companyDocumentConditionDTO.getYear() != null) {
			restriction = cb.and(restriction,
					cb.equal(companyDocumentRoot.get(CompanyDocument_.year), companyDocumentConditionDTO.getYear()));
		}

		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getCategory())) {

			restriction = cb
					.and(restriction,
							cb.equal(
									companyDocumentRoot.get(CompanyDocument_.documentCategoryMaster)
											.get("documentCategoryId").as(Long.class),
									companyDocumentConditionDTO.getCategoryId()));
		}

	
		if (StringUtils.isNotBlank(companyDocumentConditionDTO.getDescription())) {

			restriction = cb.and(restriction, cb.like(cb.upper(companyDocumentRoot.get(CompanyDocument_.description)),
					//companyDocumentConditionDTO.getDescription().toUpperCase()));
					companyDocumentConditionDTO.getEmployeeNumber().toUpperCase() + "%"));
		}
		
		restriction = cb.and(restriction,
				cb.equal(companyDocumentRoot.get(CompanyDocument_.released), true));

		if (monthId != null) {
			restriction = cb.and(restriction, cb.equal(compDocMonthJoin.get(MonthMaster_.monthId), monthId));
		}
		criteriaQuery.where(restriction);
		criteriaQuery.where(restriction);
		TypedQuery<Integer> companyDocumentQuery = entityManagerFactory.createQuery(criteriaQuery);
		return companyDocumentQuery.getSingleResult();
	}

}
