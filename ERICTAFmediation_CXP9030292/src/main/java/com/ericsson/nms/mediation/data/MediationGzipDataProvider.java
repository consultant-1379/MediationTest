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

import com.ericsson.cifwk.taf.handlers.NodeSimulationList;

public class MediationGzipDataProvider extends MediationNodeNames {

    Logger logger = Logger.getLogger(this.getClass());

    @Override 
    public List<MediationServiceNode> wantedNodes() {
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        try {
            String node = "LTE02";

            String simulation = getSimulationName(node);
            if (node.isEmpty()) {
                logger.info("This netsim does not contain any simulation for the node: " + node);
            } else {
                String destDir = getDestDir();
                short nodeType = 1;
                String erbsName = node + "ERBS00002";
                MediationServiceNode nextObject = new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + erbsName, destDir,
                        " counters", ".txt", "Collected.txt", "secret", "ftp/", 0, "", "", "GMT", nodeType, false);
                result.add(nextObject);
                NodeSimulationList.addSimulationToNodeName(erbsName, simulation);
                erbsName = node + "ERBS00004";
                nextObject = new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + erbsName, destDir, " counters", ".txt.gz",
                        "Collected.txt", "secret", "ftp/", 0, "", "", "GMT", nodeType, true);
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
