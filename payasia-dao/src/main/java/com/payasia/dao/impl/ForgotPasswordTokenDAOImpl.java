package com.payasia.dao.impl;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ForgotPasswordTokenDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.ForgotPasswordToken;
import com.payasia.dao.bean.ForgotPasswordToken_;

@Repository
public class ForgotPasswordTokenDAOImpl extends BaseDAO implements
		ForgotPasswordTokenDAO {

	@Override
	protected Object getBaseEntity() {
		return new ForgotPasswordToken();
	}

	@Override
	public void update(ForgotPasswordToken forgotPasswordToken) {
		super.update(forgotPasswordToken);

	}

	@Override
	public void save(ForgotPasswordToken forgotPasswordToken) {
		super.save(forgotPasswordToken);
	}

	@Override
	public void delete(ForgotPasswordToken forgotPasswordToken) {
		super.delete(forgotPasswordToken);

	}

	@Override
	public ForgotPasswordToken findByID(long forgotPasswordTokenId) {
		return super.findById(ForgotPasswordToken.class, forgotPasswordTokenId);
	}

	@Override
	public void updateByCondition() {
		String queryString = "UPDATE ForgotPasswordToken SET Active = 0 WHERE Active = 1 AND DATEDIFF(MINUTE,Created_Date,GETDATE()) >= 120";
		Query q = entityManagerFactory.createQuery(queryString);
		q.executeUpdate();
	}

	@Override
	public ForgotPasswordToken findByCondition(String token, Long employeeId,
			Long companyId, boolean active) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ForgotPasswordToken> criteriaQuery = cb
				.createQuery(ForgotPasswordToken.class);
		Root<ForgotPasswordToken> forgotPasswordTokenRoot = criteriaQuery
				.from(ForgotPasswordToken.class);
		Join<ForgotPasswordToken, Employee> employeeJoin = forgotPasswordTokenRoot
				.join(ForgotPasswordToken_.employee);

		criteriaQuery.select(forgotPasswordTokenRoot);
		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction,
						cb.equal(forgotPasswordTokenRoot
								.get(ForgotPasswordToken_.token), token));

		restriction = cb.and(restriction, cb.equal(
				forgotPasswordTokenRoot.get(ForgotPasswordToken_.active),
				active));

		restriction = cb.and(restriction,
				cb.equal(employeeJoin.get(Employee_.employeeId), employeeId));

		criteriaQuery.where(restriction);

		TypedQuery<ForgotPasswordToken> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (typedQuery.getResultList().size() > 0) {
			ForgotPasswordToken forgotPasswordToken = typedQuery
					.getSingleResult();

			return forgotPasswordToken;
		}
		return null;

	}

}
