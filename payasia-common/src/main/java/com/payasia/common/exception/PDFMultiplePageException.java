package com.payasia.common.exception;

/**
 * PDFMultiplePageException is used to indicate that pdf contains more than one
 * page. This can be used in cases like display page numbers only if number of
 * pages are more than one.
 */
public class PDFMultiplePageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
