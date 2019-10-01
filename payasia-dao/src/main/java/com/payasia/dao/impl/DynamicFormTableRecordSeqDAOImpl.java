package com.payasia.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.DynamicFormTableRecordSeqDAO;
import com.payasia.dao.bean.DynamicFormTableRecordSeq;

@Repository
public class DynamicFormTableRecordSeqDAOImpl extends BaseDAO implements
		DynamicFormTableRecordSeqDAO {

	@Override
	protected Object getBaseEntity() {
		DynamicFormTableRecordSeq dynamicFormTableRecordSeq = new DynamicFormTableRecordSeq();
		return dynamicFormTableRecordSeq;
	}

	@Override
	public void save(DynamicFormTableRecordSeq dynamicFormTableRecordSeq) {
		super.save(dynamicFormTableRecordSeq);
	}

	@Override
	public void update(DynamicFormTableRecordSeq dynamicFormTableRecordSeq) {
		super.update(dynamicFormTableRecordSeq);
	}

	@Override
	public void delete(DynamicFormTableRecordSeq dynamicFormTableRecordSeq) {
		super.delete(dynamicFormTableRecordSeq);
	}

	@Override
	public Long getNextVal() {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DynamicFormTableRecordSeq> criteriaQuery = cb
				.createQuery(DynamicFormTableRecordSeq.class);
		Root<DynamicFormTableRecordSeq> DynamicFormTableRecordSeqRoot = criteriaQuery
				.from(DynamicFormTableRecordSeq.class);
		criteriaQuery.select(DynamicFormTableRecordSeqRoot);

		TypedQuery<DynamicFormTableRecordSeq> maxDynamicTableRecordSeqQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		DynamicFormTableRecordSeq dynamicFormTableRecordSeq = maxDynamicTableRecordSeqQuery
				.getSingleResult();
		long maxTableRecordId = dynamicFormTableRecordSeq.getNextVal();
		
		getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					CallableStatement cstmt = null;
					cstmt = connection
							.prepareCall("{call Get_Dynamic_Form_Table_Record_Seq()}");

					ResultSet rs = cstmt.executeQuery();
				} catch (SQLException e) {
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
		});

		return maxTableRecordId;
	}
}
