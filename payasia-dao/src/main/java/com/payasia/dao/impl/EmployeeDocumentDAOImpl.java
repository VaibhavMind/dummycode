package com.payasia.dao.impl;

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

import com.payasia.common.dto.EmployeeDocumentConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeeDocumentDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeDocument;
import com.payasia.dao.bean.EmployeeDocument_;
import com.payasia.dao.bean.Employee_;

 
/**
 * The Class EmployeeDocumentDAOImpl.
 */
@Repository
public class EmployeeDocumentDAOImpl extends BaseDAO implements
		EmployeeDocumentDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmployeeDocument employeeDocument = new EmployeeDocument();
		return employeeDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDocumentDAO#save(com.payasia.dao.bean.
	 * EmployeeDocument)
	 */
	@Override
	public void save(EmployeeDocument employeeDocument) {
		super.save(employeeDocument);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDocumentDAO#findByCondition(com.payasia.common
	 * .dto.EmployeeDocumentConditionDTO, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public List<EmployeeDocument> findByCondition(
			EmployeeDocumentConditionDTO employeeDocumentConditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long empId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeDocument> criteriaQuery = cb
				.createQuery(EmployeeDocument.class);
		Root<EmployeeDocument> employeeDocumentRoot = criteriaQuery
				.from(EmployeeDocument.class);
		criteriaQuery.select(employeeDocumentRoot);

		Join<EmployeeDocument, Employee> employeeDocumentJoin = employeeDocumentRoot
				.join(EmployeeDocument_.employee);

		Path<Long> employeeId = employeeDocumentJoin.get(Employee_.employeeId);

		Predicate restriction = cb.conjunction();

		if (employeeDocumentConditionDTO.getYear() != null) {
			restriction = cb.and(restriction, cb.equal(
					employeeDocumentRoot.get(EmployeeDocument_.year),
					employeeDocumentConditionDTO.getYear()));
		}

		if (StringUtils.isNotBlank(employeeDocumentConditionDTO
				.getDocumentName())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeDocumentRoot
							.get(EmployeeDocument_.fileName)),
					employeeDocumentConditionDTO.getDocumentName()
							.toUpperCase()));
		}

		if (StringUtils.isNotBlank(employeeDocumentConditionDTO
				.getDescription())) {

			restriction = cb.and(restriction,
					cb.like(cb.upper(employeeDocumentRoot
							.get(EmployeeDocument_.description)),
							employeeDocumentConditionDTO.getDescription()
									.toUpperCase()));
		}

		if (StringUtils.isNotBlank(employeeDocumentConditionDTO.getType())) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(employeeDocumentRoot
							.get(EmployeeDocument_.fileType)),
					employeeDocumentConditionDTO.getType().toUpperCase()));
		}

		restriction = cb.and(restriction, cb.equal(employeeId, empId));

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForAllEmployeeDocument(sortDTO,
					employeeDocumentRoot);
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

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeDocument> employeeDocumentQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			employeeDocumentQuery.setFirstResult(getStartPosition(pageDTO));
			employeeDocumentQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<EmployeeDocument> employeeDocumentDetailList = employeeDocumentQuery
				.getResultList();

		return employeeDocumentDetailList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDocumentDAO#getCountForCondition(com.payasia.
	 * common.dto.EmployeeDocumentConditionDTO, java.lang.Long)
	 */
	@Override
	public int getCountForCondition(
			EmployeeDocumentConditionDTO employeeDocumentConditionDTO,
			Long employeeId) {
		return findByCondition(employeeDocumentConditionDTO, null, null,
				employeeId).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDocumentDAO#findById(long)
	 */
	@Override
	public EmployeeDocument findById(long docId) {

		EmployeeDocument employeeDocument = super.findById(
				EmployeeDocument.class, docId);
		return employeeDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDocumentDAO#update(com.payasia.dao.bean.
	 * EmployeeDocument)
	 */
	@Override
	public void update(EmployeeDocument employeeDocument) {
		super.update(employeeDocument);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmployeeDocumentDAO#delete(com.payasia.dao.bean.
	 * EmployeeDocument)
	 */
	@Override
	public void delete(EmployeeDocument employeeDocument) {
		super.delete(employeeDocument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDocumentDAO#findDocumentByYearEmIDocNameDocId
	 * (java.lang.Long, java.lang.Integer, java.lang.String, java.lang.Long)
	 */
	@Override
	public EmployeeDocument findDocumentByYearEmIDocNameDocId(Long employeeId,
			Integer year, String fileName, Long documentId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeeDocument> criteriaQuery = cb
				.createQuery(EmployeeDocument.class);
		Root<EmployeeDocument> employeeDocumentRoot = criteriaQuery
				.from(EmployeeDocument.class);

		criteriaQuery.select(employeeDocumentRoot);

		Join<EmployeeDocument, Employee> employeeJoin = employeeDocumentRoot
				.join(EmployeeDocument_.employee);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction, cb.equal(
				employeeDocumentRoot.get(EmployeeDocument_.year), year));

		restriction = cb
				.and(restriction, cb.equal(
						employeeDocumentRoot.get(EmployeeDocument_.fileName),
						fileName));

		if (documentId != null) {

			restriction = cb.and(restriction, cb.notEqual(
					employeeDocumentRoot.get(EmployeeDocument_.empDocumentId),
					documentId));
		}

		criteriaQuery.where(restriction);

		TypedQuery<EmployeeDocument> employeeDocumentTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmployeeDocument> employeeDocList = employeeDocumentTypedQuery
				.getResultList();
		if (employeeDocList != null &&  !employeeDocList.isEmpty()) {
			return employeeDocList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeeDocumentDAO#getSortPathForAllEmployeeDocument
	 * (com.payasia.common.form.SortCondition, javax.persistence.criteria.Root)
	 */
	@Override
	public Path<String> getSortPathForAllEmployeeDocument(
			SortCondition sortDTO, Root<EmployeeDocument> employeeDocumentRoot) {

		List<String> employeeDocumentsColList = new ArrayList<String>();
		employeeDocumentsColList
				.add(SortConstants.EMPLOYEE_DOCUMENT_CENTER_DOCNAME);
		employeeDocumentsColList
				.add(SortConstants.EMPLOYEE_DOCUMENT_CENTER_DESCRIPTION);
		employeeDocumentsColList
				.add(SortConstants.EMPLOYEE_DOCUMENT_CENTER_DOCTYPE);
		employeeDocumentsColList
				.add(SortConstants.EMPLOYEE_DOCUMENT_CENTER_YEAR);

		Path<String> sortPath = null;

		if (employeeDocumentsColList.contains(sortDTO.getColumnName())) {
			sortPath = employeeDocumentRoot.get(colMap
					.get(EmployeeDocument.class + sortDTO.getColumnName()));
		}

		return sortPath;
	}

}
