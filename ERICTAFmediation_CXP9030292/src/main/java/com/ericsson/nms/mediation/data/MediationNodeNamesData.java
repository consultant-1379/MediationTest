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

import java.util.*;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.handlers.*;

public class MediationNodeNamesData extends MediationNodeNames {

    static Logger logger2 = Logger.getLogger(MediationNodeNamesData.class);

    public MediationNodeNamesData() {
        numberOfLTENodes = 5; // This means there is actually 5 nodes as this means 1 node from each simulation
    }

    public MediationNodeNamesData(int numberOfNodes) {
        numberOfLTENodes = numberOfNodes / 5;
    }

    public List<MediationServiceNode> wantedNodes() { 
        String destDir = null;
        try {
            destDir = getDestDir();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int remainderOfNodes = 0;
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        NetsimHandlerWrapper netsimHost = new NetsimHandlerWrapper();

        String[] nodeArray = { "LTE02", "LTE03", "LTE04", "LTE05", "LTE06" };
        List<String> nodeList = Arrays.asList(nodeArray);

        for (String node : nodeList) {
            String simulation = getSimulationName(node);
            if (node.isEmpty()) {
                logger.info("This netsim does not contain any simulation for the node: " + node);
            } else {
                String fdnNode = "";
                int nodesFromThisSim = numberOfLTENodes + remainderOfNodes; // if previous simulation did not have enough nodes, find extra nodes in this simulation
                remainderOfNodes = 0;
                logger.info("After including the remainder from previous sims, the number of nodes to be collected from this sim = " + nodesFromThisSim);
                int numOfNodesForThisSimulation = 0;
                for (String nodeName : netsimHost.getNodeNames(nodesFromThisSim, simulation)) {
                    numOfNodesForThisSimulation++;
                    fdnNode = MediationTransformHandlerWrapper.findTransformNodeName(nodeName);
                    logger.info("Preparing for Node Transfer for Node : " + fdnNode);
                    NodeSimulationList.addSimulationToNodeName(nodeName, simulation);
                    result.add(new MediationServiceNode(fdnNode, destDir));
                }
                // if there were not enough nodes in this simulation, add the remainder on to the next simulation
                remainderOfNodes = nodesFromThisSim - numOfNodesForThisSimulation;
                logger.info("There are " + remainderOfNodes + " nodes left over that need to be taken care of by the next simulation.");
            }
        }
        return result;
    }
}