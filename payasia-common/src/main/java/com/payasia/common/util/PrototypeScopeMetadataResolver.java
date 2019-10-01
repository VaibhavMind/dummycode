/*
 * <h4>Description</h4>
 * Adds prototype scope to beans
 *
 * @author tarungupta
 */
package com.payasia.common.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

/**
 * The Class PrototypeScopeMetadataResolver.
 */
public class PrototypeScopeMetadataResolver implements ScopeMetadataResolver {
	
	List<String> prototypeBeans = Arrays.asList("EmployeePaySlipControllerImpl","EmployeeDocumentControllerImpl");

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.context.annotation.ScopeMetadataResolver#
	 * resolveScopeMetadata
	 * (org.springframework.beans.factory.config.BeanDefinition)
	 */
	@Override
	public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
		ScopeMetadata scopeMetadata = new ScopeMetadata();
		String beanName = definition.getBeanClassName().substring(definition.getBeanClassName().lastIndexOf(".") + 1);
		if(prototypeBeans.contains(beanName) || !beanName.endsWith("ApiImpl"))
		{
			scopeMetadata.setScopeName(BeanDefinition.SCOPE_PROTOTYPE);	
		}
		else{
			definition.setLazyInit(true);
			scopeMetadata.setScopeName(BeanDefinition.SCOPE_SINGLETON);
		}
		
		return scopeMetadata;
	}

}
