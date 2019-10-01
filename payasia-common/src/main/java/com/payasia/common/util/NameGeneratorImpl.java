/*
 * <h4>Description</h4>
 *
 * @author tarungupta
 */
package com.payasia.common.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.util.ClassUtils;

/**
 * The Class NameGeneratorImpl.
 */
public class NameGeneratorImpl implements BeanNameGenerator {
	private static final Logger LOGGER = Logger
			.getLogger(NameGeneratorImpl.class);
	/**
	 * Suffix of entity class name.
	 */
	private static final String IMPL_SUFFIX = "Impl";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.support.BeanNameGenerator#generateBeanName
	 * (org.springframework.beans.factory.config.BeanDefinition,
	 * org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@Override
	public String generateBeanName(BeanDefinition definition,
			BeanDefinitionRegistry registry) {
		try {
			String propertyName = ClassUtils.getShortNameAsProperty(Class
					.forName(definition.getBeanClassName()));

			int pos = propertyName.indexOf(IMPL_SUFFIX);
			if (pos == -1)
				return propertyName;
			
			return propertyName.substring(0, pos);

		} catch (ClassNotFoundException e) {
			 
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Class "
					+ definition.getBeanClassName() + " not found.");
		}
	}

}
