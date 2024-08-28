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
import com.ericsson.cifwk.taf.handlers.MediationTransformHandlerWrapper;
import com.ericsson.cifwk.taf.handlers.NodeSimulationList;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;

public class MediationLargeFilesData extends MediationNodeNames {
    Logger logger = Logger.getLogger(this.getClass());
    private SshRemoteCommandExecutor sshRemoteCommandExecutor;

    public List<MediationServiceNode> wantedNodes() { 
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();

        String node = "LTE01";
        String nodeName = "LTE01ERBS00003";
        Host netsimHost = DataHandler.getHostByName("Netsim");

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(DataHandler.getHostByName("ms1"));

        logger.info("Running largefile script: cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./largeFile.sh " + node + " " + nodeName + " "
                + netsimHost.getOriginalIp());

        String runLargeFileScript = "cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./largeFile.sh " + node + " " + nodeName + " "
                + netsimHost.getOriginalIp();

        sshRemoteCommandExecutor.simplExec("bash -c \"" + runLargeFileScript + "\"");

        logger.info("Finished running: cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./largeFile.sh " + node + " " + nodeName + " "
                + netsimHost.getOriginalIp());

        String simulation = getSimulationName(node);
        if (node.isEmpty()) {
            logger.info("This netsim does not contain any simulation for the node: " + node);
        } else {
            try {
                String destDir = getDestDir();

                String transformedNodeName = MediationTransformHandlerWrapper.findTransformNodeName(nodeName);

                result.add(new MediationServiceNode(transformedNodeName, destDir + "700MB/", " counters", ".txt", "Collected.txt", "secret", "ftp/700MB/"));
                NodeSimulationList.addSimulationToNodeName(nodeName, simulation);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
}
