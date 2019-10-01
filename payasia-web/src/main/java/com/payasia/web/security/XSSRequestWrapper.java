package com.payasia.web.security;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.payasia.common.exception.PayAsiaSystemException;

public class XSSRequestWrapper extends HttpServletRequestWrapper {
	private String apostrophe = "&#39;";

	public XSSRequestWrapper(HttpServletRequest paramHttpServletRequest) {
		super(paramHttpServletRequest);
	}

	public XSSRequestWrapper(HttpServletRequest paramHttpServletRequest, String paramString) {
		super(paramHttpServletRequest);
		apostrophe = paramString;
	}

	public String[] getParameterValues(String paramString) {
		String[] arrayOfString1 = super.getParameterValues(paramString);
		if (arrayOfString1 == null) {
			return null;
		}
		int i = arrayOfString1.length;
		String[] arrayOfString2 = new String[i];
		for (int j = 0; j < i; j++) {
			
			boolean isValid = isValidString(arrayOfString1[j]);
			if(!isValid) {
	            throw new PayAsiaSystemException("payasia.record.invalid.data");
			}
			arrayOfString2[j] = cleanXSS(arrayOfString1[j]);
		}
		return arrayOfString2;
	}

	public String getParameter(String paramString) {
		String str = super.getParameter(paramString);
		if (str == null) {
			return null;
		}
		return cleanXSS(str);
	}

	public String getHeader(String paramString) {
		String str = super.getHeader(paramString);
		if (str == null) {
			return null;
		}
		return cleanXSS(str);
	}

	private String cleanXSS(String paramString) {
		if (paramString == null) {
			return "";
		}
		String str = paramString;
		str = str.replaceAll("\000", "");
		Pattern localPattern = Pattern.compile("<script>(.*?)</script>", 2);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", 42);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", 42);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("</script>", 2);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("<script(.*?)>", 42);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("eval\\((.*?)\\)", 42);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("expression\\((.*?)\\)", 42);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("javascript:", 2);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("vbscript:", 2);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("onload(.*?)=", 42);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("%3Cscript%3(.*?)%3C%2Fscript%3E", 2);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("%3Cscript%3(.*?)%3C/script%3E", 2);
		str = localPattern.matcher(str).replaceAll("");
		localPattern = Pattern.compile("%3Cscript%3Ealert%28%27", 42);
		str = localPattern.matcher(str).replaceAll("");
	
		//commented to prevent special characters enabled 
		/*str = str.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		str = str.replaceAll("'", apostrophe);*/
		str = str.replaceAll("<", "&lt 3;").replaceAll(">", "&gt 4;");

		return str;
	}
	
	
	public static boolean isValidString(String value) {

		if (value != null) {

			Pattern scriptPatternWithoutEncoding = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);

			Pattern scriptPatternWithEncoding = Pattern.compile("%3Cscript%3(.*?)%3C%2Fscript%3E",
					Pattern.CASE_INSENSITIVE);

			Pattern scriptPatternWithEncodingAndSlash = Pattern.compile("%3Cscript%3(.*?)%3C/script%3E",
					Pattern.CASE_INSENSITIVE);
	
			Pattern patternWithEncoding = Pattern.compile("%3Cimg%20src%3Dx%20onerror%3Dalert(.*?)%3B%3E");
			Pattern patternWithOutEncoding = Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingImg = Pattern.compile("%3Cimg%20src%3D%220x%22%20onerror%3D%22alert(.*?)%3B%22%3E", Pattern.CASE_INSENSITIVE);
			Pattern patternWithOutEncodingImg = Pattern.compile("<img src='0x' onerror='alert(.*?);'>", Pattern.CASE_INSENSITIVE);
			Pattern patternWithOutEncodingdoc = Pattern.compile("<img src='0x' onerror='alert(document.cookie)';>", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingdoc = Pattern.compile("%3Cimg+src%3D%220x%22+onerror%3D%22alert%28document.cookie%29%3B%22%3E", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingT = Pattern.compile("%3Cimg+src%3D%220x%22+onerror%3D%22alert%28.%2A%3F%29%3B%22%3E", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingR = Pattern.compile("%3Cimg%20src%3Dx%20onerror%3Dalert%26%2340%3B%22hii%22%26%2341%3B%20%2F%3E", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingX = Pattern.compile("%3Cimg%20src%3Dx%20onerror%3Dalert(document.cookie)%2F%3E", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingY = Pattern.compile("%3Cimg%20src%3Dx%20onerror%3Dalert(.*?)%2F%3E", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingXml = Pattern.compile("%3D%22%253Cimg%2520src%253D%25220x%2522%2520onerror%253D%2522alert(document.cookie)%253B%2522%253E", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingXml1 = Pattern.compile("%3D%22%253Cimg%2520src%253D%25220x%2522%2520onerror%253D%2522alert(.*?)%253B%2522%253E", Pattern.CASE_INSENSITIVE);
			
			/*remove this pattern after security fix*/
			
			/*Pattern patternWithEncodingImg1 = Pattern.compile("img", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingAll = Pattern.compile("alert(.*?)", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingA = Pattern.compile("alert", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingS = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingSc = Pattern.compile("script", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingSr = Pattern.compile("src", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingE = Pattern.compile("eval", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingL = Pattern.compile("onload", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingV = Pattern.compile("vbscript", Pattern.CASE_INSENSITIVE);
			Pattern patternWithEncodingEx = Pattern.compile("expression", Pattern.CASE_INSENSITIVE);*/
			
			
			if (scriptPatternWithoutEncoding.matcher(value).find() || scriptPatternWithEncoding.matcher(value).find()
					|| scriptPatternWithEncodingAndSlash.matcher(value).find() || patternWithEncoding.matcher(value).find() || patternWithOutEncoding.matcher(value).find()
					|| patternWithEncodingImg.matcher(value).find() || patternWithOutEncodingImg.matcher(value).find() || patternWithOutEncodingdoc.matcher(value).find()
					|| patternWithEncodingdoc.matcher(value).find() || patternWithEncodingT.matcher(value).find() || patternWithEncodingR.matcher(value).find()
					|| patternWithEncodingX.matcher(value).find() || patternWithEncodingY.matcher(value).find()
					|| patternWithEncodingXml.matcher(value).find() || patternWithEncodingXml1.matcher(value).find()
					/*|| patternWithEncodingImg1.matcher(value).find() || patternWithEncodingAll.matcher(value).find()
					|| patternWithEncodingA.matcher(value).find() || patternWithEncodingS.matcher(value).find()
					|| patternWithEncodingA.matcher(value).find() || patternWithEncodingAll.matcher(value).find()
					|| patternWithEncodingSc.matcher(value).find() || patternWithEncodingSr.matcher(value).find()
					|| patternWithEncodingE.matcher(value).find() || patternWithEncodingL.matcher(value).find()
					|| patternWithEncodingV.matcher(value).find() || patternWithEncodingEx.matcher(value).find()*/
					) {
				return false;
			}
			return true;
		}
		return false;
	}

}
