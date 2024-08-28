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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;

public class MediationUnsuccessfulReadDataSFTP extends MediationUnsuccessfulReadData {
    Logger logger = Logger.getLogger(this.getClass());
    private SshRemoteCommandExecutor sshRemoteCommandExecutor;

    public List<MediationServiceNode> wantedNodes() { 
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();

        String node = "LTE01";
        String nodeName = "LTE01ERBS00112";
        String sourceDirectory = "";
        String password = "secret";
        Host netsimHost = DataHandler.getHostByName("Netsim");

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(DataHandler.getHostByName("ms1"));

        logger.info("Running sameNodeDifferentDirectories script: cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./readError.sh " + node + " "
                + nodeName + " " + netsimHost.getOriginalIp());

        String runScript = "cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./readError.sh " + node + " " + nodeName + " "
                + netsimHost.getOriginalIp();

        sshRemoteCommandExecutor.simplExec("bash -c \"" + runScript + "\"");
        logger.info("Finished running: cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./readError.sh " + node + " " + nodeName + " "
                + netsimHost.getOriginalIp());

        result = (ArrayList<MediationServiceNode>) wantedNodes(nodeName, sourceDirectory, password);
        return result;
    }
}
