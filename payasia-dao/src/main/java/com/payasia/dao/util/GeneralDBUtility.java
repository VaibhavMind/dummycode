package com.payasia.dao.util;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.payasia.common.bean.util.UserContext;

public class GeneralDBUtility {

	@PersistenceContext
	protected EntityManager entityManagerFactory;

	/*
	 * 
	 * For Audit Delete Operation: set User Id in Session Context that can be
	 * persisted across multiple batches on the same session .
	 * 
	 * @return returns the new value in batches that are started after the batch
	 * that set the value is completed.
	 */
	public void setSessionContextInfo() {
		String queryString = "DECLARE @Ctx varbinary(128) SELECT @Ctx = CONVERT(varbinary(128), CONVERT(varchar(128),:employeeId)) SET CONTEXT_INFO @Ctx";
		Query query = entityManagerFactory.createNativeQuery(queryString);
		query.setParameter("employeeId", UserContext.getUserId());
		query.executeUpdate();
	}
}
