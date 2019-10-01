/**
 * 
 */
package com.payasia.web.util;

import java.util.Arrays;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.tiles2.TilesViewResolver;

/**
 * @author tarungupta
 * 
 */
public class PayAsiaTilesViewResolver extends TilesViewResolver {

	private Boolean exposeContextBeansAsAttributes;
	private String[] exposedContextBeanNames;

	public void setExposeContextBeansAsAttributes(
			boolean exposeContextBeansAsAttributes) {
		this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
	}

	public void setExposedContextBeanNames(String[] exposedContextBeanNames) {
		if (exposedContextBeanNames != null) {
			this.exposedContextBeanNames = Arrays.copyOf(
					exposedContextBeanNames, exposedContextBeanNames.length);
		}
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		AbstractUrlBasedView superView = super.buildView(viewName);
		if (superView instanceof PayAsiaTilesView) {
			PayAsiaTilesView view = (PayAsiaTilesView) superView;
			if (this.exposeContextBeansAsAttributes != null)
				view.setExposeContextBeansAsAttributes(this.exposeContextBeansAsAttributes);
			if (this.exposedContextBeanNames != null)
				view.setExposedContextBeanNames(this.exposedContextBeanNames);
		}
		return superView;
	}

}
