package com.payasia.logic;

import javax.persistence.metamodel.SingularAttribute;

public abstract class BaseLogic {

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
