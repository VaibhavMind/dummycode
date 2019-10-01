package com.payasia.common.util;

public class RandomNumberGenerator {

	/**
	 * This method returns a random number between 0 and maxLimit.
	 * 
	 * @param maxLimit
	 *            the max limit
	 * @return the random number
	 */
	public static int getRandomNumber(int maxLimit) {
		return (int) (Math.random() * maxLimit);
	}

	/**
	 * This method returns a random number between smallest n digit number and
	 * largest n digit number. For example if n=4, then a random number would be
	 * generated between 1000 and 9999.
	 * 
	 * @param n
	 *            the n
	 * @return the n digit random number
	 */
	public static String getNDigitRandomNumber(int n) {
		int upperLimit = getMaxNDigitNumber(n);
		int lowerLimit = getMinNDigitNumber(n);
		int s = getRandomNumber(upperLimit);
		if (s < lowerLimit) {
			s += lowerLimit;
		}
		return String.valueOf(s);
	}

	/**
	 * This method returns a random number between lowerLimit and upperLimit.
	 * 
	 * @param lowerLimit
	 *            the lower limit
	 * @param upperLimit
	 *            the upper limit
	 * @return the random number
	 */
	public static int getRandomNumber(int lowerLimit, int upperLimit) {
		int v = (int) (Math.random() * upperLimit);
		if (v < lowerLimit) {
			v += lowerLimit;
		}
		return v;
	}

	/**
	 * This method returns a largest n digit number. The value returned depends
	 * on integer limit.
	 * 
	 * @param n
	 *            the n
	 * @return the max n digit number
	 */
	private static int getMaxNDigitNumber(int n) {
		int s = 0;
		int j = 10;
		for (int i = 0; i < n; i++) {
			int m = 9;
			s = (s * j) + m;
		}
		return s;
	}

	/**
	 * This method returns a lowest n digit number. The value returned depends
	 * on integer limit.
	 * 
	 * @param n
	 *            the n
	 * @return the min n digit number
	 */
	private static int getMinNDigitNumber(int n) {
		int s = 0;
		int j = 10;
		for (int i = 0; i < n - 1; i++) {
			int m = 9;
			s = (s * j) + m;
		}
		return s + 1;
	}

}
