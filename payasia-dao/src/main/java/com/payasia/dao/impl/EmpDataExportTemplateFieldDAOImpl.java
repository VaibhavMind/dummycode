package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmpDataExportTemplateFieldDAO;
import com.payasia.dao.bean.EmpDataExportTemplateField;

@Repository
public class EmpDataExportTemplateFieldDAOImpl extends BaseDAO implements
		EmpDataExportTemplateFieldDAO {

	@Override
	public EmpDataExportTemplateField save(
			EmpDataExportTemplateField empDataExportTemplateField) {
		EmpDataExportTemplateField persistObj = empDataExportTemplateField;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (EmpDataExportTemplateField) getBaseEntity();
			beanUtil.copyProperties(persistObj, empDataExportTemplateField);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	protected Object getBaseEntity() {
		EmpDataExportTemplateField empDataExportTemplateField = new EmpDataExportTemplateField();
		return empDataExportTemplateField;
	}

	@Override
	public void update(EmpDataExportTemplateField empDataExportTemplateField) {
		super.update(empDataExportTemplateField);
	}

	@Override
	public void delete(EmpDataExportTemplateField empDataExportTemplateField) {
		super.delete(empDataExportTemplateField);
	}

	@Override
	public EmpDataExportTemplateField findById(Long templateId) {
		EmpDataExportTemplateField empDataExportTemplateField = super.findById(
				EmpDataExportTemplateField.class, templateId);
		return empDataExportTemplateField;
	}

	@Override
	public void deleteByCondition(Long exportTemplateId) {

		String queryString = "DELETE FROM EmpDataExportTemplateField d WHERE d.empDataExportTemplate.exportTemplateId = :exportTemplateId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("exportTemplateId", exportTemplateId);

		q.executeUpdate();
	}

}
