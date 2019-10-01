package com.payasia.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.EmpDataExportTemplateFilterDAO;
import com.payasia.dao.bean.EmpDataExportTemplateFilter;

@Repository
public class EmpDataExportTemplateFilterDAOImpl extends BaseDAO implements
		EmpDataExportTemplateFilterDAO {

	@Override
	public void save(EmpDataExportTemplateFilter empDataExportTemplateFilter) {
		super.save(empDataExportTemplateFilter);

	}

	@Override
	protected Object getBaseEntity() {
		EmpDataExportTemplateFilter empDataExportTemplateFilter = new EmpDataExportTemplateFilter();
		return empDataExportTemplateFilter;
	}

	@Override
	public void update(EmpDataExportTemplateFilter empDataExportTemplateFilter) {
		super.update(empDataExportTemplateFilter);

	}

	@Override
	public void delete(EmpDataExportTemplateFilter empDataExportTemplateFilter) {
		super.delete(empDataExportTemplateFilter);

	}

	@Override
	public EmpDataExportTemplateFilter findById(Long filterId) {
		EmpDataExportTemplateFilter empDataExportTemplateFilter = super
				.findById(EmpDataExportTemplateFilter.class, filterId);
		return empDataExportTemplateFilter;
	}

	@Override
	public void deleteByCondition(Long exportTemplateId) {

		String queryString = "DELETE FROM EmpDataExportTemplateFilter d WHERE d.empDataExportTemplate.exportTemplateId = :exportTemplateId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("exportTemplateId", exportTemplateId);

		q.executeUpdate();
	}

}
