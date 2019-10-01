package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmailDAO;
import com.payasia.dao.bean.Email;
import com.payasia.dao.bean.Email_;

@Repository
public class EmailDAOImpl extends BaseDAO implements EmailDAO {

	@Override
	protected Object getBaseEntity() {
		Email email = new Email();
		return email;
	}

	@Override
	public Email saveReturn(Email email) {
		email.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		Email persistObj = email;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (Email) getBaseEntity();
			beanUtil.copyProperties(persistObj, email);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void update(Email email) {
		super.update(email);
	}

	@Override
	public void delete(Email email) {
		super.delete(email);
	}

	@Override
	public Email findById(long emailId) {

		Email email = super.findById(Email.class, emailId);
		return email;
	}

	@Override
	public List<Email> findAllEmailsBySentDate() {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Email> criteriaQuery = cb.createQuery(Email.class);
		Root<Email> emailRoot = criteriaQuery.from(Email.class);
		criteriaQuery.select(emailRoot);

		criteriaQuery.where(cb.isNull(emailRoot.get(Email_.sentDate)));

		TypedQuery<Email> emailTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<Email> emails = emailTypedQuery.getResultList();

		return emails;
	}

}
