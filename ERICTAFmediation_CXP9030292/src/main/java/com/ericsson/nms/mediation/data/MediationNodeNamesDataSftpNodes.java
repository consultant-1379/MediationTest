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

import com.ericsson.cifwk.taf.handlers.NetsimHandlerWrapper;

public class MediationNodeNamesDataSftpNodes extends MediationNodeNamesData {

    public MediationNodeNamesDataSftpNodes() {
        numberOfLTENodes = 10;
        numberOfRNC36Nodes = 0;
        numberOfrnc37Nodes = 0;
        numberOfSFTPNodes = 0;
    }

    @Override
    public List<MediationServiceNode> wantedNodes() { 

        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        NetsimHandlerWrapper netsimHost = new NetsimHandlerWrapper();
        try {
            String destDir = getDestDir();

            String node = "LTE04";
            String simulation = getSimulationName(node);
            if (node.isEmpty()) {
                logger.info("This netsim does not contain any simulation for the node: " + node);
            } else {
                for (String fdnNode : netsimHost.getNodeNames(numberOfLTENodes, simulation)) {
                    result.add(new MediationServiceNode(fdnNode, destDir));
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
