package com.payasia.dao.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
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

import com.payasia.common.dto.HrisPendingItemsConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.HRISChangeRequestReviewerDAO;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DataDictionary_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequestReviewer;
import com.payasia.dao.bean.HRISChangeRequestReviewer_;
import com.payasia.dao.bean.HRISChangeRequest_;
import com.payasia.dao.bean.HRISStatusMaster;
import com.payasia.dao.bean.HRISStatusMaster_;

@Repository
public class HRISChangeRequestReviewerDAOImpl extends BaseDAO implements
		HRISChangeRequestReviewerDAO {

	@Override
	protected Object getBaseEntity() {
		HRISChangeRequestReviewer hrisChangeRequestReviewer = new HRISChangeRequestReviewer();
		return hrisChangeRequestReviewer;
	}

	@Override
	public void update(HRISChangeRequestReviewer hrisChangeRequestReviewer) {
		this.entityManagerFactory.merge(hrisChangeRequestReviewer);
	}

	@Override
	public void delete(HRISChangeRequestReviewer hrisChangeRequestReviewer) {
		this.entityManagerFactory.remove(hrisChangeRequestReviewer);
	}

	@Override
	public void save(HRISChangeRequestReviewer hrisChangeRequestReviewer) {
		super.save(hrisChangeRequestReviewer);
	}

	@Override
	public HRISChangeRequestReviewer findById(Long hrisChangeRequestReviewerId) {
		return super.findById(HRISChangeRequestReviewer.class,
				hrisChangeRequestReviewerId);

	}

	@Override
	public List<HRISChangeRequestReviewer> findHRISChangeRequestReviewers(
			Long employeeId, PageRequest pageDTO, SortCondition sortDTO,
			HrisPendingItemsConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequestReviewer> criteriaQuery = cb
				.createQuery(HRISChangeRequestReviewer.class);
		Root<HRISChangeRequestReviewer> hRISChangeRequestReviewerRoot = criteriaQuery
				.from(HRISChangeRequestReviewer.class);
		criteriaQuery.select(hRISChangeRequestReviewerRoot);

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequestReviewer, Employee> employeeJoin = hRISChangeRequestReviewerRoot
				.join(HRISChangeRequestReviewer_.employeeReviewer);

		Join<HRISChangeRequestReviewer, HRISChangeRequest> hRISChangeRequestJoin = hRISChangeRequestReviewerRoot
				.join(HRISChangeRequestReviewer_.hrisChangeRequest);
		Join<HRISChangeRequest, DataDictionary> dataDictJoin = hRISChangeRequestJoin
				.join(HRISChangeRequest_.dataDictionary);
		Join<HRISChangeRequest, Employee> changeReqEmployeeJoin = hRISChangeRequestJoin
				.join(HRISChangeRequest_.employee);
		 
		 
		 
		if (employeeId != null) {
			restriction = cb.and(restriction, cb.equal(
					employeeJoin.get(Employee_.employeeId), employeeId));
		}
		if (StringUtils.isNotBlank(conditionDTO.getField())) {
			restriction = cb.and(restriction, cb.like(
					dataDictJoin.get(DataDictionary_.label),
					'%' + conditionDTO.getField() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getCreatedBy())) {
			restriction = cb.and(restriction, cb.or(cb.like(
					cb.upper(changeReqEmployeeJoin.get(Employee_.firstName)),
					conditionDTO.getCreatedBy().toUpperCase() + '%'), cb.like(
					cb.upper(changeReqEmployeeJoin.get(Employee_.lastName)),
					conditionDTO.getCreatedBy().toUpperCase() + '%'), cb.like(
					cb.upper(changeReqEmployeeJoin
							.get(Employee_.employeeNumber)), conditionDTO
							.getCreatedBy().toUpperCase() + '%')));
		}
		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb
					.and(restriction, cb.equal(
							hRISChangeRequestJoin.get(
									HRISChangeRequest_.createdDate).as(
									Date.class), DateUtils
									.stringToTimestamp(conditionDTO
											.getCreatedDate())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getNewValue())) {
			restriction = cb.and(restriction, cb.like(
					hRISChangeRequestJoin.get(HRISChangeRequest_.newValue),
					'%' + conditionDTO.getNewValue() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getOldValue())) {
			restriction = cb.and(restriction, cb.like(
					hRISChangeRequestJoin.get(HRISChangeRequest_.oldValue),
					'%' + conditionDTO.getOldValue() + '%'));
		}

		restriction = cb.and(restriction, cb.equal(
				hRISChangeRequestReviewerRoot
						.get(HRISChangeRequestReviewer_.pending), true));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO,
					hRISChangeRequestReviewerRoot);
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

		TypedQuery<HRISChangeRequestReviewer> hRISChangeRequestReviewerQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			hRISChangeRequestReviewerQuery
					.setFirstResult(getStartPosition(pageDTO));
			hRISChangeRequestReviewerQuery.setMaxResults(pageDTO.getPageSize());
		}

		return hRISChangeRequestReviewerQuery.getResultList();
	}

	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<HRISChangeRequestReviewer> root) {

		Join<HRISChangeRequestReviewer, HRISChangeRequest> changeReqJoin = root
				.join(HRISChangeRequestReviewer_.hrisChangeRequest);
		Join<HRISChangeRequest, DataDictionary> dataDictJoin = changeReqJoin
				.join(HRISChangeRequest_.dataDictionary);
		Join<HRISChangeRequest, Employee> employeeJoin = changeReqJoin
				.join(HRISChangeRequest_.employee);

		List<String> employeeIsColList = new ArrayList<String>();
		employeeIsColList.add(SortConstants.HRIS_CHANGE_REQUEST_CREATEDBY);

		List<String> dataDictColList = new ArrayList<String>();
		dataDictColList.add(SortConstants.HRIS_CHANGE_REQUEST_FIELD);

		List<String> hrisChangeReqColList = new ArrayList<String>();
		hrisChangeReqColList.add(SortConstants.HRIS_CHANGE_REQUEST_OLD_VALUE);
		hrisChangeReqColList.add(SortConstants.HRIS_CHANGE_REQUEST_NEW_VALUE);
		hrisChangeReqColList
				.add(SortConstants.HRIS_CHANGE_REQUEST_CREATED_DATE);

		Path<String> sortPath = null;

		if (employeeIsColList.contains(sortDTO.getColumnName())) {
			sortPath = employeeJoin.get(colMap.get(Employee.class
					+ sortDTO.getColumnName()));
		}
		if (dataDictColList.contains(sortDTO.getColumnName())) {
			sortPath = dataDictJoin.get(colMap.get(DataDictionary.class
					+ sortDTO.getColumnName()));
		}
		if (hrisChangeReqColList.contains(sortDTO.getColumnName())) {
			sortPath = changeReqJoin.get(colMap.get(HRISChangeRequest.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Integer getCountHRISChangeRequestReviewers(
			HrisPendingItemsConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<HRISChangeRequestReviewer> hRISChangeRequestReviewerRoot = criteriaQuery
				.from(HRISChangeRequestReviewer.class);
		criteriaQuery.select(cb.count(hRISChangeRequestReviewerRoot).as(
				Integer.class));

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequestReviewer, Employee> employeeJoin = hRISChangeRequestReviewerRoot
				.join(HRISChangeRequestReviewer_.employeeReviewer);

		 if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction, cb.equal(
					employeeJoin.get(Employee_.employeeId),
					conditionDTO.getEmployeeId()));
		}

		restriction = cb.and(restriction, cb.equal(
				hRISChangeRequestReviewerRoot
						.get(HRISChangeRequestReviewer_.pending), true));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> hRISChangeRequestReviewerQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return hRISChangeRequestReviewerQuery.getSingleResult();
	}

	@Override
	public List<HRISChangeRequestReviewer> checkHRISEmployeeReviewer(
			long employeeId, List<String> hrisStatusList) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequestReviewer> criteriaQuery = cb
				.createQuery(HRISChangeRequestReviewer.class);
		Root<HRISChangeRequestReviewer> root = criteriaQuery
				.from(HRISChangeRequestReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequestReviewer, Employee> empHRISJoin = root
				.join(HRISChangeRequestReviewer_.employeeReviewer);

		Join<HRISChangeRequestReviewer, HRISChangeRequest> hrisChangeReqJoin = root
				.join(HRISChangeRequestReviewer_.hrisChangeRequest);
		Join<HRISChangeRequest, HRISStatusMaster> hrisStatusJoin = hrisChangeReqJoin
				.join(HRISChangeRequest_.hrisStatusMaster);

		restriction = cb.and(restriction,
				cb.equal(empHRISJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(
				restriction,
				hrisStatusJoin.get(HRISStatusMaster_.hrisStatusName).in(
						hrisStatusList));
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequestReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			return typedQuery.getResultList();
		} else {
			return null;
		}
	}

	@Override
	public List<HRISChangeRequestReviewer> findByCondition(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequestReviewer> criteriaQuery = cb
				.createQuery(HRISChangeRequestReviewer.class);
		Root<HRISChangeRequestReviewer> root = criteriaQuery
				.from(HRISChangeRequestReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequestReviewer, Employee> empJoin = root
				.join(HRISChangeRequestReviewer_.employeeReviewer);
		Join<HRISChangeRequestReviewer, HRISChangeRequest> hrisChangeRequestJoin = root
				.join(HRISChangeRequestReviewer_.hrisChangeRequest);

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(root.get(HRISChangeRequestReviewer_.pending), true));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(hrisChangeRequestJoin
				.get(HRISChangeRequest_.updatedDate)));
		TypedQuery<HRISChangeRequestReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		final List<HRISChangeRequestReviewer> list = typedQuery.getResultList();
		if(list!=null && !list.isEmpty()){
			return list ;
		}		
		return Collections.emptyList();
	}
	
	@Override
	public List<HRISChangeRequestReviewer> findByConditionPosition(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, int position) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequestReviewer> criteriaQuery = cb
				.createQuery(HRISChangeRequestReviewer.class);
		Root<HRISChangeRequestReviewer> root = criteriaQuery
				.from(HRISChangeRequestReviewer.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequestReviewer, Employee> empJoin = root
				.join(HRISChangeRequestReviewer_.employeeReviewer);
		Join<HRISChangeRequestReviewer, HRISChangeRequest> hrisChangeRequestJoin = root
				.join(HRISChangeRequestReviewer_.hrisChangeRequest);

		restriction = cb.and(restriction,
				cb.equal(empJoin.get(Employee_.employeeId), employeeId));

		restriction = cb.and(restriction,
				cb.equal(root.get(HRISChangeRequestReviewer_.pending), true));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(hrisChangeRequestJoin
				.get(HRISChangeRequest_.updatedDate)));
		TypedQuery<HRISChangeRequestReviewer> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(position);
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		List<HRISChangeRequestReviewer> list = typedQuery.getResultList();
		if(list!=null && !list.isEmpty()){
			return list ;
		}		
		return Collections.emptyList();
	}

	
}
