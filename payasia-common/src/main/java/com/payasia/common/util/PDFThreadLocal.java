package com.payasia.common.util;

/**
 * PDFThreadLocal class is used to store values that are required across
 * classes, but cannot be made static. In case these values are made static PDF
 * generation might fail when concurrent users start working on same PDF. Thread
 * local values ensure that these values are unique for each request.
 */
public class PDFThreadLocal {

	/** Stores whether page numbers should be displayed or not. */
	public static ThreadLocal<Boolean> pageNumbers = new ThreadLocal<Boolean>() {

		@Override
		protected synchronized Boolean initialValue() {
			return false;
		}
	};
}
