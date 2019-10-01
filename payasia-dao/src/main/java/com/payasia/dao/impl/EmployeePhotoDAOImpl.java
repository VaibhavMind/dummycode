package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmployeePhotoDAO;
import com.payasia.dao.bean.EmployeePhoto;
import com.payasia.dao.bean.EmployeePhoto_;

/**
 * The Class EmployeePhotoDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmployeePhotoDAOImpl extends BaseDAO implements EmployeePhotoDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {

		EmployeePhoto empPhoto = new EmployeePhoto();
		return empPhoto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeePhotoDAO#save(com.payasia.dao.bean.EmployeePhoto)
	 */
	@Override
	public void save(EmployeePhoto employeePhoto) {
		super.save(employeePhoto);
	}

	@Override
	public EmployeePhoto saveReturn(EmployeePhoto employeePhoto) {
		EmployeePhoto persistObj = employeePhoto;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmployeePhoto) getBaseEntity();
			beanUtil.copyProperties(persistObj, employeePhoto);
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
	 * @see com.payasia.dao.EmployeePhotoDAO#findByEmployeeId(java.lang.Long)
	 */
	@Override
	public EmployeePhoto findByEmployeeId(Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeePhoto> criteriaQuery = cb
				.createQuery(EmployeePhoto.class);
		Root<EmployeePhoto> employeePhotoRoot = criteriaQuery
				.from(EmployeePhoto.class);

		criteriaQuery.select(employeePhotoRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				employeePhotoRoot.get(EmployeePhoto_.employee_ID), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeePhoto> employeePhotoTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeePhoto> employeePhotoList = employeePhotoTypedQuery
				.getResultList();
		if (employeePhotoList != null && !employeePhotoList.isEmpty()) {
			return employeePhotoList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmployeePhotoDAO#update(com.payasia.dao.bean.EmployeePhoto
	 * )
	 */
	@Override
	public void update(EmployeePhoto employeePhoto) {
		super.update(employeePhoto);

	}

	@Override
	public void delete(EmployeePhoto employeePhoto) {
		super.delete(employeePhoto);

	}

	@Override
	public EmployeePhoto findByEmployeeAndCompanyId(Long employeeId, Long companyid) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmployeePhoto> criteriaQuery = cb
				.createQuery(EmployeePhoto.class);
		Root<EmployeePhoto> employeePhotoRoot = criteriaQuery
				.from(EmployeePhoto.class);

		criteriaQuery.select(employeePhotoRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				employeePhotoRoot.get(EmployeePhoto_.employee_ID), employeeId));
		
		restriction = cb.and(restriction, cb.equal(
				employeePhotoRoot.get(EmployeePhoto_.companyId), companyid));

		criteriaQuery.where(restriction);

		TypedQuery<EmployeePhoto> employeePhotoTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<EmployeePhoto> employeePhotoList = employeePhotoTypedQuery
				.getResultList();
		if (employeePhotoList != null && !employeePhotoList.isEmpty()) {
			return employeePhotoList.get(0);
		}
		return null;
	}

}
