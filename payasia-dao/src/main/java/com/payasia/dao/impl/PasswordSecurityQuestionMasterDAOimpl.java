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

import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.PasswordSecurityQuestionMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.PasswordSecurityQuestionMaster;
import com.payasia.dao.bean.PasswordSecurityQuestionMaster_;

/**
 * The Class PasswordSecurityQuestionMasterDAOimpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class PasswordSecurityQuestionMasterDAOimpl extends BaseDAO implements
		PasswordSecurityQuestionMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordSecurityQuestionMasterDAO#save(com.payasia.dao
	 * .bean.PasswordSecurityQuestionMaster)
	 */
	@Override
	public void save(
			PasswordSecurityQuestionMaster passwordSecurityQuestionMaster) {
		super.save(passwordSecurityQuestionMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordSecurityQuestionMasterDAO#update(com.payasia.
	 * dao.bean.PasswordSecurityQuestionMaster)
	 */
	@Override
	public void update(
			PasswordSecurityQuestionMaster passwordSecurityQuestionMaster) {
		super.update(passwordSecurityQuestionMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		PasswordSecurityQuestionMaster passwordSecurityQuestionMaster = new PasswordSecurityQuestionMaster();
		return passwordSecurityQuestionMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordSecurityQuestionMasterDAO#findByConditionCompany
	 * (java.lang.Long, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<PasswordSecurityQuestionMaster> findByConditionCompany(
			Long companyId, PageRequest pageDTO, SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PasswordSecurityQuestionMaster> criteriaQuery = cb
				.createQuery(PasswordSecurityQuestionMaster.class);
		Root<PasswordSecurityQuestionMaster> passTypeRoot = criteriaQuery
				.from(PasswordSecurityQuestionMaster.class);

		criteriaQuery.select(passTypeRoot);

		Join<PasswordSecurityQuestionMaster, Company> passTypeRootJoin = passTypeRoot
				.join(PasswordSecurityQuestionMaster_.company);

		Path<Long> companyID = passTypeRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForPasswordSecurityQuestion(
					sortDTO, passTypeRoot, passTypeRootJoin);
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

		 
		 

		TypedQuery<PasswordSecurityQuestionMaster> passTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			passTypedQuery.setFirstResult(getStartPosition(pageDTO));
			passTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		passTypedQuery.getResultList();

		return passTypedQuery.getResultList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.PasswordSecurityQuestionMasterDAO#
	 * getSortPathForPasswordSecurityQuestion
	 * (com.payasia.common.form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForPasswordSecurityQuestion(
			SortCondition sortDTO,
			Root<PasswordSecurityQuestionMaster> passTypeRoot,
			Join<PasswordSecurityQuestionMaster, Company> passTypeRootJoin) {

		List<String> passQuesIsIdList = new ArrayList<String>();
		passQuesIsIdList.add(SortConstants.PASS_SECURITY_QUESTION_ID);

		List<String> passQuesIsColList = new ArrayList<String>();
		passQuesIsColList.add(SortConstants.SECURITY_QUESTION);

		Path<String> sortPath = null;

		if (passQuesIsColList.contains(sortDTO.getColumnName())) {
			sortPath = passTypeRoot.get(colMap
					.get(PasswordSecurityQuestionMaster.class
							+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordSecurityQuestionMasterDAO#getQuestionForEdit(
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public PasswordSecurityQuestionMaster getQuestionForEdit(Long companyId,
			Long pwdSecurityQuestionId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PasswordSecurityQuestionMaster> criteriaQuery = cb
				.createQuery(PasswordSecurityQuestionMaster.class);
		Root<PasswordSecurityQuestionMaster> passTypeRoot = criteriaQuery
				.from(PasswordSecurityQuestionMaster.class);

		criteriaQuery.select(passTypeRoot);

		Join<PasswordSecurityQuestionMaster, Company> passTypeRootJoin = passTypeRoot
				.join(PasswordSecurityQuestionMaster_.company);

		Path<Long> companyID = passTypeRootJoin.get(Company_.companyId);
		Path<Long> pwdSecurityQuestionID = passTypeRoot
				.get(PasswordSecurityQuestionMaster_.pwdSecurityQuestionId);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyID, companyId));

		restriction = cb.and(restriction,
				cb.equal(pwdSecurityQuestionID, pwdSecurityQuestionId));

		criteriaQuery.where(restriction);

		TypedQuery<PasswordSecurityQuestionMaster> passQuesTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<PasswordSecurityQuestionMaster> passwordList = passQuesTypedQuery
				.getResultList();
		if (passwordList != null &&  !passwordList.isEmpty()) {
			return passwordList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordSecurityQuestionMasterDAO#delete(com.payasia.
	 * dao.bean.PasswordSecurityQuestionMaster)
	 */
	@Override
	public void delete(
			PasswordSecurityQuestionMaster passwordSecurityQuestionMaster) {
		super.delete(passwordSecurityQuestionMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordSecurityQuestionMasterDAO#findById(java.lang.
	 * Long)
	 */
	@Override
	public PasswordSecurityQuestionMaster findById(Long pwdSecurityQuestionId) {
		return super.findById(PasswordSecurityQuestionMaster.class,
				pwdSecurityQuestionId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordSecurityQuestionMasterDAO#getCountForConditionCompany
	 * (java.lang.Long)
	 */
	@Override
	public int getCountForConditionCompany(Long companyId) {
		return findByConditionCompany(companyId, null, null).size();
	}

}
