package com.payasia.dao;

import com.payasia.common.dto.ColumnPropertyDTO;

/**
 * The Interface GeneralDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface GeneralDAO {

	/**
	 * Gets the column type of Database Table by tableName and fieldName .
	 * 
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @return the column type
	 * @throws Exception
	 *             the exception
	 */
	String getColumnType(String tableName, String fieldName);

	/**
	 * Gets the column size of Database Table by tableName and fieldName .
	 * 
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @return the column size
	 * @throws Exception
	 *             the exception
	 */
	int getColumnSize(String tableName, String fieldName);

	/**
	 * Gets the column is Nullable or not.
	 * 
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @return the column nullable
	 * @throws Exception
	 *             the exception
	 */
	int getColumnNullable(String tableName, String fieldName);

	/**
	 * Gets the column properties of Database Table by tableName and fieldName .
	 * 
	 * @param tableName
	 *            the table name
	 * @param fieldName
	 *            the field name
	 * @return the column properties DTO
	 * @throws Exception
	 *             the exception
	 */
	ColumnPropertyDTO getColumnProperties(String tableName, String fieldName);
	
	 boolean isTemplateExistForCom(Long templateId,Long companyId);

}
