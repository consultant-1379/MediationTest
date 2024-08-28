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

import com.ericsson.cifwk.taf.handlers.NodeSimulationList;

public class MediationNodeNamesDataFtpNodes extends MediationNodeNamesData {

    public MediationNodeNamesDataFtpNodes() {
        numberOfLTENodes = 1;
        numberOfRNC36Nodes = 0;
        numberOfrnc37Nodes = 0;
        numberOfSFTPNodes = 0;
    }

    @Override
    public List<MediationServiceNode> wantedNodes() {

        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        String destDir;
        try {
            destDir = getDestDir();

            String node = "LTE01";
            String simulation = getSimulationName(node);
            if (node.isEmpty()) {
                logger.info("This netsim does not contain any simulation for the node: " + node);
            } else {
                String erbsName = node + "ERBS00136";
                String transformNodeName = "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + erbsName;
                String sourceDir = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + simulation + "/" + erbsName + "/fs/ftp/";
                MediationServiceNode nextObject = new MediationServiceNode(transformNodeName, destDir, " counters", ".txt", "Collected.txt", "netsim",
                        sourceDir);
                result.add(nextObject);
                NodeSimulationList.addSimulationToNodeName(erbsName, simulation);
                erbsName = node + "ERBS00137";
                transformNodeName = "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + erbsName;
                sourceDir = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + simulation + "/" + erbsName + "/fs/ftp/";
                nextObject = new MediationServiceNode(transformNodeName, destDir, " counters", ".txt", "Collected.txt", "netsim", sourceDir);
                result.add(nextObject);
                NodeSimulationList.addSimulationToNodeName(erbsName, simulation);
                erbsName = node + "ERBS00138";
                transformNodeName = "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + erbsName;
                sourceDir = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + simulation + "/" + erbsName + "/fs/ftp/";
                nextObject = new MediationServiceNode(transformNodeName, destDir, " counters", ".txt", "Collected.txt", "netsim", sourceDir);
                result.add(nextObject);
                NodeSimulationList.addSimulationToNodeName(erbsName, simulation);
                erbsName = node + "ERBS00139";
                transformNodeName = "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + erbsName;
                sourceDir = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + simulation + "/" + erbsName + "/fs/ftp/";
                nextObject = new MediationServiceNode(transformNodeName, destDir, " counters", ".txt", "Collected.txt", "netsim", sourceDir);
                result.add(nextObject);
                NodeSimulationList.addSimulationToNodeName(erbsName, simulation);
                erbsName = node + "ERBS00140";
                transformNodeName = "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + erbsName;
                sourceDir = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + simulation + "/" + erbsName + "/fs/ftp/";
                nextObject = new MediationServiceNode(transformNodeName, destDir, " counters", ".txt", "Collected.txt", "netsim", sourceDir);
                result.add(nextObject);
                NodeSimulationList.addSimulationToNodeName(erbsName, simulation);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }
}