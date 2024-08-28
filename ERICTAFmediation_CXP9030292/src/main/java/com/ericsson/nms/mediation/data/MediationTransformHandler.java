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

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.DataProvider;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;

public class MediationTransformHandler implements DataProvider{

	private static final Logger logger = Logger
			.getLogger(MediationTransformHandler.class);
	private static String[] listOfOSSNodeNames = null;

	public String[] getListofNodeNamesFromOSS() {
                
		SshRemoteCommandExecutor sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
		sshRemoteCommandExecutor.setHost(DataHandler.getHostByName("OssMaster"));
		String cstestCommand = "/opt/ericsson/nms_cif_cs/etc/unsupported/bin/cstest -s ONRM_CS lt MeContext ";

		String listOfFdns = sshRemoteCommandExecutor.simplExec("bash -c \""
				+ cstestCommand + "\"");

		String[] result = listOfFdns.split("\\n");
		for (String nodename : result) {
			logger.debug("Node Name : " + nodename);
		}
		return result;
	}

	public String[] getListofNodeNames() {
		if (listOfOSSNodeNames == null) {
			try {
				listOfOSSNodeNames = getListofNodeNamesFromOSS();
				logger.info("FDN Node Strings retrieved from OSS");
			} catch (final Exception e) {
				logger.error("Problem retrieving FDN Node Strings from OSS", e);
				return null;
			}
		}
		return listOfOSSNodeNames;
	}

	public String transformNodeName(String nodeName) {

		for (String searchNode : getListofNodeNames()) {
			if (searchNode.contains(nodeName)) {
				return searchNode;
			}
		}
		logger.error("Node name could not be resolved " + nodeName);
		return null;
	}
};