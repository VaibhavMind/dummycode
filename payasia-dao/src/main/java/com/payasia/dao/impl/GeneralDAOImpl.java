package com.payasia.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.bean.EmpDataExportTemplate;
import com.payasia.dao.bean.EmpDataExportTemplate_;

/**
 * The Class GeneralDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class GeneralDAOImpl extends BaseDAO implements GeneralDAO {

	private static final Logger LOGGER = Logger.getLogger(GeneralDAOImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.GeneralDAO#getColumnType(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getColumnType(final String tableName, final String fieldName) {

		final List<String> columnTypeList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					DatabaseMetaData metadata = connection.getMetaData();
					ResultSet resultSet = metadata.getColumns(null, null,
							tableName, fieldName);
					while (resultSet.next()) {
						columnTypeList.add(resultSet.getString("TYPE_NAME"));
					}
				} catch (SQLException sqlException) {
					LOGGER.error(sqlException.getMessage(), sqlException);
					throw new PayAsiaSystemException(sqlException.getMessage(),
							sqlException);
				}

			}
		});

		return columnTypeList.get(0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.GeneralDAO#getColumnSize(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int getColumnSize(final String tableName, final String fieldName) {

		final List<Integer> columnSizeList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					DatabaseMetaData metadata = connection.getMetaData();
					ResultSet resultSet = metadata.getColumns(null, null,
							tableName, fieldName);
					while (resultSet.next()) {
						columnSizeList.add(resultSet.getInt("COLUMN_SIZE"));
					}
				} catch (SQLException sqlException) {
					LOGGER.error(sqlException.getMessage(), sqlException);
					throw new PayAsiaSystemException(sqlException.getMessage(),
							sqlException);
				}

			}
		});

		return columnSizeList.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.GeneralDAO#getColumnNullable(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int getColumnNullable(final String tableName, final String fieldName) {
		final List<Integer> columnNullableList = new ArrayList<>();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					DatabaseMetaData metadata = connection.getMetaData();
					ResultSet resultSet = metadata.getColumns(null, null,
							tableName, fieldName);
					while (resultSet.next()) {
						columnNullableList.add(resultSet.getInt("NULLABLE"));
					}
				} catch (SQLException sqlException) {
					LOGGER.error(sqlException.getMessage(), sqlException);
					throw new PayAsiaSystemException(sqlException.getMessage(),
							sqlException);
				}

			}
		});

		return columnNullableList.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.GeneralDAO#getColumnProperties(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ColumnPropertyDTO getColumnProperties(final String tableName,
			final String fieldName) {

		final ColumnPropertyDTO columnPropertyDTO = new ColumnPropertyDTO();

		getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				try {
					DatabaseMetaData metadata = connection.getMetaData();
					ResultSet resultSet = metadata.getColumns(null, null,
							tableName, fieldName);
					while (resultSet.next()) {
						columnPropertyDTO.setColumnType(resultSet
								.getString("TYPE_NAME"));
						columnPropertyDTO.setColumnLength(resultSet
								.getInt("COLUMN_SIZE"));
						columnPropertyDTO.setColumnNullable(resultSet
								.getInt("NULLABLE"));
					}
				} catch (SQLException sqlException) {
					LOGGER.error(sqlException.getMessage(), sqlException);
					throw new PayAsiaSystemException(sqlException.getMessage(),
							sqlException);
				}

			}
		});

		return columnPropertyDTO;

	}
	@Override
	public boolean isTemplateExistForCom(Long templateId,Long companyId)
	{
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmpDataExportTemplate> criteriaQuery = cb.createQuery(EmpDataExportTemplate.class);
		Root<EmpDataExportTemplate> templateRoot = criteriaQuery.from(EmpDataExportTemplate.class);

		criteriaQuery.select(templateRoot);

		Predicate restriction = cb.conjunction();
		
		restriction = cb.and(restriction,cb.equal(templateRoot.get(EmpDataExportTemplate_.exportTemplateId), templateId));
		
		restriction = cb.and(restriction,cb.equal(templateRoot.get(EmpDataExportTemplate_.company), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<EmpDataExportTemplate> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		
		List<EmpDataExportTemplate> list = typedQuery.getResultList();
		return list!=null && !list.isEmpty()  ? true :false;
	}

}
