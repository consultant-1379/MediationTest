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
import com.ericsson.cifwk.taf.handlers.*;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;

public class MediationSameNodeDifferentDirectories extends MediationNodeNamesData {

    Logger logger = Logger.getLogger(this.getClass());
    private SshRemoteCommandExecutor sshRemoteCommandExecutor;
    private RemoteFileHandler remoteFileHandler;

    public List<MediationServiceNode> wantedNodes() { 

        String node = "LTE01";
        String nodeName = "LTE01ERBS00002";

        Host netsimHost = DataHandler.getHostByName("Netsim");

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(DataHandler.getHostByName("ms1"));

        logger.info("Running sameNodeDifferentDirectories script: cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./sameNodeDifferentDirectories.sh "
                + node + " " + nodeName + " " + netsimHost.getOriginalIp());

        String runScript = "cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./sameNodeDifferentDirectories.sh " + node + " " + nodeName + " "
                + netsimHost.getOriginalIp();

        sshRemoteCommandExecutor.simplExec("bash -c \"" + runScript + "\"");

        logger.info("Finished running: cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./sameNodeDifferentDirectories.sh " + node + " " + nodeName
                + " " + netsimHost.getOriginalIp());

        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        String[] customDirs = { "5MB/", "10MB/", "100MB/", "700MB/" };
        //        String[] customDirs = { "5MB/", "10MB/", "100MB/" };

        try {
            String destDir = getDestDir();

            String simulation = getSimulationName(node);
            if (node.isEmpty()) {
                logger.info("This netsim does not contain any simulation for the node: " + node);
            } else {
                String transformNodeName = MediationTransformHandlerWrapper.findTransformNodeName(nodeName);
                result.add(new MediationServiceNode(transformNodeName, destDir + customDirs[0], " counters", ".txt", "Collected.txt", "secret", "ftp/"
                        + customDirs[0]));
                result.add(new MediationServiceNode(transformNodeName, destDir + customDirs[1], " counters", ".txt", "Collected.txt", "secret", "ftp/"
                        + customDirs[1]));
                result.add(new MediationServiceNode(transformNodeName, destDir + customDirs[2], " counters", ".txt", "Collected.txt", "secret", "ftp/"
                        + customDirs[2]));
                result.add(new MediationServiceNode(transformNodeName, destDir + customDirs[3], " counters", ".txt", "Collected.txt", "secret", "ftp/"
                        + customDirs[3]));
                NodeSimulationList.addSimulationToNodeName(nodeName, simulation);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
