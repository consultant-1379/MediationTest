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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.handlers.NodeSimulationList;
import com.ericsson.cifwk.taf.handlers.RemoteCommandExecutor;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;

public class MediationUnsuccessfulReWriteData extends MediationNodeNamesData {
    Logger logger = Logger.getLogger(this.getClass());
    RemoteCommandExecutor sshRemoteCommandExecutor;

    //    public int numberOfNodes = 9;

    public List<MediationServiceNode> wantedNodes() { 
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();

        String destDir = "/cluster/collect_files/";

        try {
            destDir = getDestDir();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
             sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
             sshRemoteCommandExecutor.setHost(DataHandler.getHostByName("sc1"));

            String createReWriteDirectory = "rm -rf " + destDir + "/reWriteDirectory;mkdir " + destDir + "/reWriteDirectory;chmod 777 " + destDir + "/reWriteDirectory";

            sshRemoteCommandExecutor.simplExec("bash -c \"" + createReWriteDirectory + "\"");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String node = "LTE01";
        String simulation = getSimulationName(node);
        if (node.isEmpty()) {
            logger.info("This netsim does not contain any simulation for the node: " + node);
        } else {
            String transformNodeName = "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=LTE01ERBS00115";
            //        MediationServiceNode nextObject = new MediationServiceNode(transformNodeName, destDir);
            result.add(new MediationServiceNode(transformNodeName, destDir + "reWriteDirectory/", " counters", ".txt", "Collected.txt", "secret", "ftp/", -1, "", ""));
            String nodeName = transformNodeName.substring(transformNodeName.lastIndexOf('=') + 1);
            NodeSimulationList.addSimulationToNodeName(nodeName, simulation);
            logger.info("I have created a node with name : " + transformNodeName + " and destination directory: " + destDir);
            result.add(new MediationServiceNode(transformNodeName, destDir + "reWriteDirectory/", " counters", ".txt", "Collected.txt", "secret", "ftp/", 5, "File LTE01ERBS00115Collected.txt already exists in " + destDir
                    + "reWriteDirectory/LTE01ERBS00115", ""));
        }

        return result;
    }
};