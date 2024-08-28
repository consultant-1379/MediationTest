package com.ericsson.cifwk.taf.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.tools.RestTool;
import com.ericsson.nms.mediation.data.PropertyFileLoader;

public class RestCallHandler {

	static Logger logger = Logger.getLogger(RestCallHandler.class);

	public static String getResultFromRest(final String urlString) {
		logger.info("Called getResultFromRest with arg " + urlString);

		Host upgradeHost = DataHandler.getHostByName("PMMed_si_0_jee_instance");
		
		logger.info("host ip = " + upgradeHost.getIp());
		RestTool restCaller = new RestTool(upgradeHost);

		List<String> responseList = restCaller.get(urlString);
		logger.info("ResponseList " + responseList.toString());
		String response = responseList.get(0);
		logger.info("Got response " + response);
		return response;
	}
}