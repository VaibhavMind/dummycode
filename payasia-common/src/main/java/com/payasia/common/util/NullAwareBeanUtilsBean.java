/*
 * <h4>Description</h4>
 * Methods of this class do not copy null values from source to destination.
 * @author tarungupta
 */
package com.payasia.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;

 
/**
 * The Class NullAwareBeanUtilsBean.
 */
public class NullAwareBeanUtilsBean extends BeanUtilsBean {
	private static final Logger LOGGER = Logger
			.getLogger(NullAwareBeanUtilsBean.class);
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
	
	/**
	 * Copy all bean objects in a list.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param destClass
	 *            type of destination class
	 * @param srcList
	 *            the source list
	 * @return the destinationList
	 */
	public <T> List<T> copyList(Class<T> destClass, List<?> srcList) {
		List<T> destList = new ArrayList<T>();
		for (Object orig : srcList) {
			T dest = null;
			try {
				dest = destClass.newInstance();
				super.copyProperties(dest, orig);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			destList.add(dest);
		}

		return destList;
	}

}
