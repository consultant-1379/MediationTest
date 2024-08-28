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

import com.ericsson.cifwk.taf.handlers.*;
import com.ericsson.cifwk.taf.handlers.netsim.implementation.SshNetsimHandler;

public class MediationNodeNamesDataAllNodes extends MediationNodeNames {

    public MediationNodeNamesDataAllNodes() {
        numberOfLTENodes = 1000; // This means there is actually 5 nodes as this means 1 node from each simulation
    }

    public List<MediationServiceNode> wantedNodes() {

        String destDir = "";
        try {
            destDir = getDestDir();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SshNetsimHandler netsimHandler = NetsimHandlerWrapper.generateSshNetsimHandler();
        List<String> simulationList = netsimHandler.getListOfSimulations();
        logger.info("Here is the list of simulations returned from netsim: " + simulationList);

        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>(); 
        NetsimHandlerWrapper netsimHost = new NetsimHandlerWrapper();

        for (String simulation : simulationList) {
            String fdnNode = "";
            int nodesFromThisSim = numberOfLTENodes;
            int numOfNodesForThisSimulation = 0;
            for (String nodeName : netsimHost.getNodeNames(nodesFromThisSim, simulation)) {
                numOfNodesForThisSimulation++;
                fdnNode = MediationTransformHandlerWrapper.findTransformNodeName(nodeName);
                logger.info("Preparing for Node Transfer for Node : " + fdnNode);
                NodeSimulationList.addSimulationToNodeName(nodeName, simulation);
                result.add(new MediationServiceNode(fdnNode, destDir));
            }
            logger.info("The total number of nodes used from simulation " + simulation + " = " + numOfNodesForThisSimulation);
        }
        return result;
    }
}
