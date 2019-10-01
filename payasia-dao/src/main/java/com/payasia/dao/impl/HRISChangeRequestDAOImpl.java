package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
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

import com.payasia.common.dto.HrisMyRequestConditionDTO;
import com.payasia.common.dto.HrisPendingItemsConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.HRISChangeRequestDAO;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DataDictionary_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequest_;
import com.payasia.dao.bean.HRISStatusMaster;
import com.payasia.dao.bean.HRISStatusMaster_;

@Repository
public class HRISChangeRequestDAOImpl extends BaseDAO implements HRISChangeRequestDAO {

	@Override
	public void update(HRISChangeRequest hrisChangeRequest) {
		this.entityManagerFactory.merge(hrisChangeRequest);
	}

	@Override
	public void delete(HRISChangeRequest hrisChangeRequest) {
		this.entityManagerFactory.remove(hrisChangeRequest);
	}

	@Override
	public void save(HRISChangeRequest hrisChangeRequest) {
		super.save(hrisChangeRequest);
	}

	@Override
	public HRISChangeRequest savePersist(HRISChangeRequest hrisChangeRequest) {
		HRISChangeRequest persistObj = hrisChangeRequest;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (HRISChangeRequest) getBaseEntity();
			beanUtil.copyProperties(persistObj, hrisChangeRequest);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public HRISChangeRequest findById(Long hrisChangeRequestId) {
		return super.findById(HRISChangeRequest.class, hrisChangeRequestId);

	}

	@Override
	protected Object getBaseEntity() {
		HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
		return hrisChangeRequest;
	}
	
	@Override
	public HRISChangeRequest findByCondition(Long dictionaryId, Long employeeId, List<String> hrisStatusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequest, Employee> empJoin = root.join(HRISChangeRequest_.employee);
		Join<HRISChangeRequest, DataDictionary> dataDictionaryJoin = root.join(HRISChangeRequest_.dataDictionary);
		Join<HRISChangeRequest, HRISStatusMaster> statusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(dataDictionaryJoin.get(DataDictionary_.dataDictionaryId), dictionaryId));
		restriction = cb.and(restriction, statusJoin.get(HRISStatusMaster_.hrisStatusName).in(hrisStatusList));
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public HRISChangeRequest findByCondition(Long dictionaryId,Integer tableId, Long employeeId, List<String> hrisStatusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequest, Employee> empJoin = root.join(HRISChangeRequest_.employee);
		Join<HRISChangeRequest, DataDictionary> dataDictionaryJoin = root.join(HRISChangeRequest_.dataDictionary);
		Join<HRISChangeRequest, HRISStatusMaster> statusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(dataDictionaryJoin.get(DataDictionary_.dataDictionaryId), dictionaryId));
		restriction = cb.and(restriction, statusJoin.get(HRISStatusMaster_.hrisStatusName).in(hrisStatusList));
		if(tableId!=null){
			restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.tableRecordSequence),tableId));			
		}
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public List<HRISChangeRequest> findByConditionChangeRequests(HrisMyRequestConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequest, Employee> empLeaveJoin = root.join(HRISChangeRequest_.employee);

		Join<HRISChangeRequest, HRISStatusMaster> hrisStatusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);
		Join<HRISChangeRequest, DataDictionary> dataDictJoin = root.join(HRISChangeRequest_.dataDictionary);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getHrisStatusNames().size() > 0) {
			restriction = cb.and(restriction,
					hrisStatusJoin.get(HRISStatusMaster_.hrisStatusName).in(conditionDTO.getHrisStatusNames()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getField())) {
			restriction = cb.and(restriction,
					cb.like(dataDictJoin.get(DataDictionary_.label), '%' + conditionDTO.getField() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.createdDate).as(Date.class),
					DateUtils.stringToTimestamp(conditionDTO.getCreatedDate())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getNewValue())) {
			restriction = cb.and(restriction,
					cb.like(root.get(HRISChangeRequest_.newValue), '%' + conditionDTO.getNewValue() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getOldValue())) {
			restriction = cb.and(restriction,
					cb.like(root.get(HRISChangeRequest_.oldValue), '%' + conditionDTO.getOldValue() + '%'));
		}

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForSearchEmployee(sortDTO, root);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

	public Path<String> getSortPathForSearchEmployee(SortCondition sortDTO, Root<HRISChangeRequest> root) {

		Join<HRISChangeRequest, DataDictionary> dataDictJoin = root.join(HRISChangeRequest_.dataDictionary);

		List<String> dataDictColList = new ArrayList<String>();
		dataDictColList.add(SortConstants.HRIS_CHANGE_REQUEST_FIELD);

		List<String> hrisChangeReqColList = new ArrayList<String>();
		hrisChangeReqColList.add(SortConstants.HRIS_CHANGE_REQUEST_OLD_VALUE);
		hrisChangeReqColList.add(SortConstants.HRIS_CHANGE_REQUEST_NEW_VALUE);
		hrisChangeReqColList.add(SortConstants.HRIS_CHANGE_REQUEST_CREATED_DATE);

		Path<String> sortPath = null;

		if (dataDictColList.contains(sortDTO.getColumnName())) {
			sortPath = dataDictJoin.get(colMap.get(DataDictionary.class + sortDTO.getColumnName()));
		}
		if (hrisChangeReqColList.contains(sortDTO.getColumnName())) {
			sortPath = root.get(colMap.get(HRISChangeRequest.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	@Override
	public Integer getCountChangeRequests(HrisMyRequestConditionDTO conditionDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(cb.count(root).as(Integer.class));

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequest, Employee> empLeaveJoin = root.join(HRISChangeRequest_.employee);

		Join<HRISChangeRequest, HRISStatusMaster> hrisStatusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);
		Join<HRISChangeRequest, DataDictionary> dataDictJoin = root.join(HRISChangeRequest_.dataDictionary);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empLeaveJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getHrisStatusNames().size() > 0) {
			restriction = cb.and(restriction,
					hrisStatusJoin.get(HRISStatusMaster_.hrisStatusName).in(conditionDTO.getHrisStatusNames()));
		}

		if (StringUtils.isNotBlank(conditionDTO.getField())) {
			restriction = cb.and(restriction,
					cb.like(dataDictJoin.get(DataDictionary_.label), '%' + conditionDTO.getField() + '%'));
		}

		if (StringUtils.isNotBlank(conditionDTO.getCreatedDate())) {
			restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.createdDate).as(Date.class),
					DateUtils.stringToTimestamp(conditionDTO.getCreatedDate())));
		}
		if (StringUtils.isNotBlank(conditionDTO.getNewValue())) {
			restriction = cb.and(restriction,
					cb.like(root.get(HRISChangeRequest_.newValue), '%' + conditionDTO.getNewValue() + '%'));
		}
		if (StringUtils.isNotBlank(conditionDTO.getOldValue())) {
			restriction = cb.and(restriction,
					cb.like(root.get(HRISChangeRequest_.oldValue), '%' + conditionDTO.getOldValue() + '%'));
		}
		criteriaQuery.where(restriction);

		TypedQuery<Integer> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		return typedQuery.getSingleResult();
	}

	@Override
	public List<HRISChangeRequest> findByConditionSubmitted(HrisPendingItemsConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequest, Employee> empChangeReqJoin = root.join(HRISChangeRequest_.employee);

		Join<HRISChangeRequest, HRISStatusMaster> hrisStatusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empChangeReqJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getHrisStatusNames().size() > 0) {
			restriction = cb.and(restriction,
					hrisStatusJoin.get(HRISStatusMaster_.hrisStatusName).in(conditionDTO.getHrisStatusNames()));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(root.get(HRISChangeRequest_.updatedDate)));

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		// Added by Gaurav for Pagination
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(getStartPosition(pageDTO));
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}

	@Override
	public HRISChangeRequest findByConditionTableSeq(Long dictionaryId, Long employeeId, List<String> hrisStatusList,
			int tableSeq) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequest, Employee> empJoin = root.join(HRISChangeRequest_.employee);
		Join<HRISChangeRequest, DataDictionary> dataDictionaryJoin = root.join(HRISChangeRequest_.dataDictionary);
		Join<HRISChangeRequest, HRISStatusMaster> statusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(dataDictionaryJoin.get(DataDictionary_.dataDictionaryId), dictionaryId));
		restriction = cb.and(restriction, statusJoin.get(HRISStatusMaster_.hrisStatusName).in(hrisStatusList));
		restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.tableRecordSequence), tableSeq));
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public HRISChangeRequest findByIdAndTableSeq(Long hrisChangeReqId, int tableSeq) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.hrisChangeRequestId), hrisChangeReqId));
		restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.tableRecordSequence), tableSeq));
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public List<HRISChangeRequest> findByDataDictIdAndSeq(Long companyId, Long employeeId, Integer seqNo,
			List<String> hrisStatusList, List<String> fieldDictIds) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequest, Employee> empJoin = root.join(HRISChangeRequest_.employee);
		Join<HRISChangeRequest, DataDictionary> dataDictionaryJoin = root.join(HRISChangeRequest_.dataDictionary);
		Join<HRISChangeRequest, HRISStatusMaster> statusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.tableRecordSequence), seqNo));

		restriction = cb.and(restriction, statusJoin.get(HRISStatusMaster_.hrisStatusName).in(hrisStatusList));
		restriction = cb.and(restriction, dataDictionaryJoin.get(DataDictionary_.dataDictionaryId).in(fieldDictIds));
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList;
		}
		return null;
	}

	@Override
	public HRISChangeRequest findByDataDictAndSeq(Long dictionaryId, Long employeeId, Integer seqNo,
			List<String> hrisStatusList) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequest, Employee> empJoin = root.join(HRISChangeRequest_.employee);
		Join<HRISChangeRequest, DataDictionary> dataDictionaryJoin = root.join(HRISChangeRequest_.dataDictionary);
		Join<HRISChangeRequest, HRISStatusMaster> statusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(dataDictionaryJoin.get(DataDictionary_.dataDictionaryId), dictionaryId));
		restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.tableRecordSequence), seqNo));
		restriction = cb.and(restriction, statusJoin.get(HRISStatusMaster_.hrisStatusName).in(hrisStatusList));
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public HRISChangeRequest findByIdAndEmployeeId(Long hrisChangeRequestId, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequest, Employee> empJoin = root.join(HRISChangeRequest_.employee);
		restriction = cb.and(restriction,
				cb.equal(root.get(HRISChangeRequest_.hrisChangeRequestId), hrisChangeRequestId));

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		criteriaQuery.where(restriction);
		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public HRISChangeRequest findByIdAndCompanyId(Long hrisChangeRequestId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(root.get(HRISChangeRequest_.hrisChangeRequestId), hrisChangeRequestId));

		restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.companyId), companyId));

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}

	@Override
	public HRISChangeRequest findByDataDictAndSeq(Long dictionaryId, Long employeeId, Integer seqNo,
			List<String> hrisStatusList, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeRequest, Employee> empJoin = root.join(HRISChangeRequest_.employee);
		Join<HRISChangeRequest, DataDictionary> dataDictionaryJoin = root.join(HRISChangeRequest_.dataDictionary);
		Join<HRISChangeRequest, HRISStatusMaster> statusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);

		restriction = cb.and(restriction, cb.equal(empJoin.get(Employee_.employeeId), employeeId));
		restriction = cb.and(restriction,
				cb.equal(dataDictionaryJoin.get(DataDictionary_.dataDictionaryId), dictionaryId));
		restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.tableRecordSequence), seqNo));
		restriction = cb.and(restriction, statusJoin.get(HRISStatusMaster_.hrisStatusName).in(hrisStatusList));
		restriction = cb.and(restriction, cb.equal(root.get(HRISChangeRequest_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<HRISChangeRequest> changeRequestList = typedQuery.getResultList();
		if (changeRequestList != null && !changeRequestList.isEmpty()) {
			return changeRequestList.get(0);
		}
		return null;
	}
	
	@Override
	public List<HRISChangeRequest> findByConditionSubmitted2(HrisPendingItemsConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, int position) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeRequest> criteriaQuery = cb.createQuery(HRISChangeRequest.class);
		Root<HRISChangeRequest> root = criteriaQuery.from(HRISChangeRequest.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<HRISChangeRequest, Employee> empChangeReqJoin = root.join(HRISChangeRequest_.employee);

		Join<HRISChangeRequest, HRISStatusMaster> hrisStatusJoin = root.join(HRISChangeRequest_.hrisStatusMaster);

		if (conditionDTO.getEmployeeId() != null) {
			restriction = cb.and(restriction,
					cb.equal(empChangeReqJoin.get(Employee_.employeeId), conditionDTO.getEmployeeId()));
		}

		if (conditionDTO.getHrisStatusNames().size() > 0) {
			restriction = cb.and(restriction,
					hrisStatusJoin.get(HRISStatusMaster_.hrisStatusName).in(conditionDTO.getHrisStatusNames()));
		}

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.desc(root.get(HRISChangeRequest_.updatedDate)));

		TypedQuery<HRISChangeRequest> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

		// Added by Gaurav for Pagination
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			typedQuery.setFirstResult(position);
			typedQuery.setMaxResults(pageDTO.getPageSize());
		}

		return typedQuery.getResultList();
	}
	
}
