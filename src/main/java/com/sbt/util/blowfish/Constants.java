/*
 * @(#)Constants.java	2017年5月24日
 *
 * Copyright (c) 2017. All Rights Reserved.
 *
 */

package com.sbt.util.blowfish;


/**
 * Constants
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: Constants.java 2017年5月24日 11:00:19 Exp $
 */
public class Constants {
	
	/**
	 * The newline character，it is "\r\n" in windows，and "\n" in linux 
	 */
	public static final String LINE_SEPARATER = Systems.getSystemProperty("line.separator", "\r\n");
	
	public static final String PREFERED_ENCODING = "UTF-8";
	public static final String ENCODING_UTF8 = "UTF-8";
	
	public static final String WEB_EXCEPTION_SIGN = "exceptionSign";
	
	/**
	 * 系统核心数 
	 */
	public static final int CPU_CORES = Runtime.getRuntime().availableProcessors();
	
	public static final String[] PARAMS_TO_HIDDEN = new String[] {
		"pwd", "password", "secret", "secretKey"
	};
	
	public static final String[] DEF_ENCRYPT_TEXT = new String[] { 
		"XXs3wfEnetf5qgGaV7mLKxx5NUqPulaRXXs3wfEnetemXFmQlWz1HGXgfkrXkYt5XXs3wfEneteeQtCjeZrvMQ==",
		"IvsyXE3us39aabipfa3Pc9NLWVZNf4jPIvsyXE3us38WCaguR5XnkdKShZLLefE/IvsyXE3us39uzXqp2gIiJQ==",
		"0lBtqrhFIyCVFbU/w/NoCeDQc1dH/Ug10lBtqrhFIyB05M+2qXyh69c4UPgO3xDM0lBtqrhFIyAxAkto/BSwMw==",
	};
	
	public static final String DEF_ORGINAL_TEXT = "！！！我是中国人！！！毛主席万岁！！！";
	
}
