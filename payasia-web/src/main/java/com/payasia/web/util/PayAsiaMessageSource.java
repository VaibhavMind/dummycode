package com.payasia.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class PayAsiaMessageSource extends ReloadableResourceBundleMessageSource {

	/**
	 * Calculate the filenames for the given bundle basename and Locale,
	 * appending language code, country code, and variant,script code. E.g.:
	 * basename "messages", Locale "de_AT_oo" -> "messages_de_AT_OO",
	 * "messages_de_AT", "messages_de".
	 * <p>
	 * Follows the rules defined by {@link java.util.Locale#toString()}.
	 * 
	 * @param basename
	 *            the basename of the bundle
	 * @param locale
	 *            the locale
	 * @return the List of filenames to check
	 */
	@Override
	protected List<String> calculateFilenamesForLocale(String basename,
			Locale locale) {
		List<String> result = new ArrayList<String>(3);
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();
		String script = locale.getScript();
		StringBuilder temp = new StringBuilder(basename);
		StringBuilder tempWithScript = new StringBuilder(basename);

		if (language.length() > 0) {
			temp.append('_');
			temp.append(language);
			result.add(0, temp.toString());
		}

		if (language.length() > 0 && script.length() > 0) {
			tempWithScript.append('_');
			tempWithScript.append(language);
		}

		if (script.length() > 0 && (language.length() > 0)
				&& script.length() > 0) {
			tempWithScript.append('_');
			tempWithScript.append(script);
			result.add(0, tempWithScript.toString());
		}

		if (country.length() > 0) {
			temp.append('_');
			temp.append(country);
			result.add(0, temp.toString());
		}

		if (country.length() > 0 && script.length() > 0) {
			tempWithScript.append('_');
			tempWithScript.append(country);
			result.add(0, tempWithScript.toString());
		}

		if (variant.length() > 0
				&& (language.length() > 0 || country.length() > 0)) {
			temp.append('_');
			temp.append(variant);
			result.add(0, temp.toString());
		}

		if (variant.length() > 0
				&& (language.length() > 0 || country.length() > 0)
				&& script.length() > 0) {
			tempWithScript.append('_');
			tempWithScript.append(variant);
			result.add(0, tempWithScript.toString());
		}

		return result;
	}
}
