/*
 * <h4>Description</h4>
 * This class intercepts save, update and delete operations,
 * to override userId with  logged in userid
 *
 * @author tarungupta
 */
package com.payasia.web.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.springframework.security.core.context.SecurityContextHolder;

import com.payasia.web.security.PayAsiaUserDetails;

/**
 * The Class EntityInterceptor.
 */
public class EntityInterceptor implements PreInsertEventListener,
		PreUpdateEventListener {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger(EntityInterceptor.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6532904727005971593L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.event.PreUpdateEventListener#onPreUpdate(org.hibernate.
	 * event.PreUpdateEvent)
	 */
	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {

		Object entityObject = event.getEntity();
		Timestamp currentDate = new Timestamp(new Date().getTime());
		overrideField(entityObject, "setUpdatedDate", currentDate);
		overrideField(entityObject, "setUpdatedBy", getEmployeeID());
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.event.PreInsertEventListener#onPreInsert(org.hibernate.
	 * event.PreInsertEvent)
	 */
	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		Timestamp currentDate = new Timestamp(new Date().getTime());
		Object entityObject = event.getEntity();
		overrideField(entityObject, "setCreatedDate", currentDate);
		overrideField(entityObject, "setCreatedBy", getEmployeeID());
		overrideField(entityObject, "setUpdatedDate", currentDate);
		overrideField(entityObject, "setUpdatedBy", getEmployeeID());
		return false;
	}

	/**
	 * Gets the user name from security context.
	 * 
	 * @return the user name
	 */
	private String getEmployeeID() {

		String empId = "TESTUSER";

		try {
			Object userDetails = SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();

			if (userDetails instanceof PayAsiaUserDetails) {
				empId = String.valueOf(((PayAsiaUserDetails) userDetails)
						.getUserId());
			} else {
				empId = (String) userDetails;
			}

			return empId;
		} catch (Exception e) {
			LOGGER.error(
					"Not able to locate user from security context. Reason: "
							+ e.getMessage(), e);
			return "TESTUSER";
		}
	}

	/**
	 * Overrides field value in entity.
	 * 
	 * @param entity
	 *            the entity
	 * @param methodName
	 *            the method name
	 * @param data
	 *            the data
	 */
	private void overrideField(Object entity, String methodName, Object data) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("overrideField(Object, String, Object) - start");
		}

		Class[] parameterTypes = { data.getClass() };

		Method entityMethod;
		try {
			entityMethod = entity.getClass().getDeclaredMethod(methodName,
					parameterTypes);

			if (entityMethod != null) {
				entityMethod.invoke(entity, data);
			}
		} catch (SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error("Error while setting method:" + methodName
					+ " in class:" + entity.getClass().getName());
			LOGGER.error("Error while setting method: " + e.getMessage(), e);
		}

	}
}
