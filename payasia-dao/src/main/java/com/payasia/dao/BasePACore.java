/*******************************************************************************
 * Copyright (c) 2010 *All rights reserved.
 * The information contained here in is confidential.
 * 
 * Created by:	gurmeetsinghvirdi
 * Created on:	July 20, 2011
 * Description:	Base class for DAO & Logic
 * 
 * @author gurmeetsinghvirdi
 ******************************************************************************/
package com.payasia.dao;

import javax.persistence.metamodel.SingularAttribute;

/**
 * @author gurmeetsinghvirdi
 * 
 */
public abstract class BasePACore {

	/**
	 * Gets the alias for column name.
	 * 
	 * @param attribute
	 *            SingularAttribute of entity field
	 * @return the alias
	 */
	protected String getAlias(SingularAttribute<?, ?> attribute) {
		String alias = attribute.getDeclaringType().getJavaType()
				.getSimpleName()
				+ "_" + attribute.getName();
		return alias;
	}
}
