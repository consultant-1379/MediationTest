/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.mediation.data;


import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;

public class PropertyFileLoader {
	static Logger logger = Logger
			.getLogger(PropertyFileLoader.class.getClass());

	public String getProperty(final String propsFile) {
		logger.info("Getting Information from DataHandler for " + propsFile);
		String property = DataHandler.getAttribute(propsFile).toString();
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return property;

	}
	
	public Host getHostByName(String hostname)
	{
		Host host = DataHandler.getHostByName(hostname);
		return host;
	}

	/**
	 * @param props
	 */
	private void printProperties(Properties props) {
		Enumeration e = props.propertyNames();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			logger.info(key + " -- " + props.getProperty(key));
		}
	}

}
