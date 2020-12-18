/*
 * @(#)Systems.java	2017-03-22
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.sbt.util.blowfish;



/**
 * Utils for System.
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Systems.java 2017-03-22 2017-03-22 00:17:12 Exp $
 */
public final class Systems {

	private Systems() {
		throw new InstantiationError("Must not instantiate this class");
	}

	public static String getSystemProperty(String key, String valIfNull) {
		String val = getSystemProperty(key);
		return val == null ? valIfNull : val;
	}

	/**
	 * <p>
	 * Gets a System property, defaulting to <code>null</code> if the property
	 * cannot be read.
	 * </p>
	 * <p>
	 * <p>
	 * If a <code>SecurityException</code> is caught, the return value is
	 * <code>null</code> and a message is written to <code>System.err</code>.
	 * </p>
	 *
	 * @param property the system property name
	 * @return the system property value or <code>null</code> if a security
	 * problem occurs
	 */
	public static String getSystemProperty(String property) {
		try {
			return System.getProperty(property);
		} catch (SecurityException ex) {
			// we are not allowed to look at this property
			System.err.println("Caught a SecurityException reading the system property '"
					+ property
					+ "'; the SystemUtils property value will default to null.");
			return null;
		}
	}

	/**
	 * LogA messages on system console.
	 *
	 * @param format message pattern format
	 * @param params parameter array
	 */
	public static void out(String format, Object... params) {
		String msg = String.format(format, params);
		System.out.println(msg);
	}

	/**
	 * 一次性输出多行
	 */
	public static void out(String... lines) {
		for (String line : lines) {
			System.out.println(line);
		}
	}

	/**
	 * Get current machine's cup core num.
	 *
	 * @return
	 */
	public static int cupNum() {
		return Constants.CPU_CORES;
	}

	//public static void main(String[] args) {
	//	Systems.out("cpu_num={}", cupNum());
	//}

}
