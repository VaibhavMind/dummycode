/*
 * <h4>Description</h4>
 * Methods of this class do not copy null values from source to destination.
 * @author tarungupta
 */
package com.payasia.web.controller.impl;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
 
/**
 * The Class NullAwareBeanUtilsBean.
 */
public class NullAwareBeanUtilsBean extends BeanUtilsBean {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.beanutils.BeanUtilsBean#copyProperty(java.lang.Object,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public void copyProperty(Object dest, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {
		if (value == null)
			return;
		super.copyProperty(dest, name, value);
	}

}
