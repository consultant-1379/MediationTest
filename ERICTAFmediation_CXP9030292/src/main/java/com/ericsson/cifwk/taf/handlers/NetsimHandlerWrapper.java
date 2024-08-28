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
package com.ericsson.cifwk.taf.handlers;

import java.util.*;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.netsim.NetsimHandler;
import com.ericsson.cifwk.taf.handlers.netsim.implementation.NetsimNE;
import com.ericsson.cifwk.taf.handlers.netsim.implementation.SshNetsimHandler;

public class NetsimHandlerWrapper {

    private static NetsimHandler netsimHandler;
    Logger logger = Logger.getLogger(this.getClass());
    List<NetsimNE> netsimNodes;
    List<String> netsimNodeNames;
    private ArrayList<String> listOfTransformedNodeNames;
    static HashMap<String, List<NetsimNE>> mapOfNodeNamesToSims = new HashMap<String, List<NetsimNE>>(20);

    static Logger logger2 = Logger.getLogger(NetsimHandlerWrapper.class);
    private static Host netsimHost;
    private static SshNetsimHandler sshNetsimHandler;
    private static List<String> listOfSims;

    public List<String> getNodeNames(int numberOfLTENodes, String simName) {

        String netsimSimulation = simName;
        logger.debug("Here are the netsimSimulation: " + netsimSimulation);
        int numOfNodesLimit = numberOfLTENodes;

        netsimHandler = generateNetsimHandler();
        netsimNodeNames = new ArrayList<String>();
        listOfTransformedNodeNames = new ArrayList<String>();

        SshNetsimHandler sshNetsimHandler = generateSshNetsimHandler();
        if (mapOfNodeNamesToSims.containsKey(netsimSimulation)) {
            netsimNodes = mapOfNodeNamesToSims.get(netsimSimulation);
        } else {
            netsimNodes = getAllStartedNEs(sshNetsimHandler, netsimSimulation);
            mapOfNodeNamesToSims.put(netsimSimulation, netsimNodes);
        }
        logger.debug("Here is the number of nodes connected to this simulation: " + netsimNodes.size());
        Iterator<NetsimNE> netsimNodesIterator = netsimNodes.iterator();

        for (int i = 0; i < numOfNodesLimit; i++) {
            if (netsimNodesIterator.hasNext()) {
                NetsimNE netsimNode = netsimNodesIterator.next();
                String nodeName = netsimNode.getName();
                logger.debug("This is the netsimhandlerwrapper for loop number: " + i + ". Using the node: " + nodeName);
                if ((nodeName.contains("RBS")) || !nodeName.equals("LTE01ERBS00001")) {
                    logger.debug("Checking if this netsim node is available on the server: " + nodeName);
                    String transformedNodeName = MediationTransformHandlerWrapper.findTransformNodeName(nodeName);
                    if (transformedNodeName != null) {
                        logger.debug("Adding node to list as it is available on both the netsim and the oss: " + nodeName);
                        listOfTransformedNodeNames.add(transformedNodeName);
                        NodeSimulationList.addSimulationToNodeName(nodeName, netsimSimulation);
                    } else {
                        numOfNodesLimit++;
                        logger.debug("Discarding this node at it is available on the netsim, but not available on the oss: " + nodeName
                                + ". Target node count is now: " + numOfNodesLimit);
                    }
                } else {
                    numOfNodesLimit++;
                    logger.debug("Discarding this node at it is available on the netsim, but not available on the oss: " + nodeName
                            + ". Target node count is now: " + numOfNodesLimit);
                }
            } else {
                logger.debug("There are no more nodes left on this netsim.");
                return (listOfTransformedNodeNames);
            }
        }
        logger.debug("Here is the list NetsimHandlerWrapper is returning: " + listOfTransformedNodeNames);
        return (listOfTransformedNodeNames);
    }

    private static NetsimHandler generateNetsimHandler() {
        if (netsimHandler == null) {
            logger2.info("NetsimHandler does not exist, creating a new one!");
            netsimHandler = new NetsimHandler(DataHandler.getHostByName("Netsim"));
        } else {
            logger2.info("NetsimHandler already exists, return this one!");
        }
        return netsimHandler;
    }

    public static SshNetsimHandler generateSshNetsimHandler() {
        if (sshNetsimHandler == null) {
            logger2.info("SshNetsimHandler does not exist, creating a new one!");
            Host netsimHost = DataHandler.getHostByName("Netsim");
            logger2.debug("Here is the netsimHost: " + netsimHost.getIp());
            sshNetsimHandler = new SshNetsimHandler(netsimHost);
        } else {
            logger2.info("SshNetsimHandler already exists, return this one!");
        }
        return sshNetsimHandler;
    }

    public static List<String> getListOfSimulations() {
        if (listOfSims == null) {
            logger2.info("List of simulations does not exist, creating a new one!");
            SshNetsimHandler netsimHandler = NetsimHandlerWrapper.generateSshNetsimHandler();
            listOfSims = netsimHandler.getListOfSimulations();
        } else {
            logger2.info("List of simulations already exists, return this one!");
        }
        return listOfSims;
    }

    //    public static Host generateNetsimHost() {      
    //            Host netSimHost = DataHandler.getHostByName("Netsim");           
    //            logger2.debug("NetsimHost: " + netsimHost);
    //        return netSimHost;
    //    }

    // This is a wrapper class for getAllNEs to provide a list of started NEs only
    public List<NetsimNE> getAllStartedNEs(SshNetsimHandler sshNetsimHandler, String simulation) {
        List<NetsimNE> neList = new ArrayList<NetsimNE>();
        List<NetsimNE> startedNeList = new ArrayList<NetsimNE>();

        if (simulation == "") {
            return neList;
        }
        String cmdResponse = sshNetsimHandler.executeCommandWithSimulation(simulation, ".show started");
        neList = sshNetsimHandler.getAllNEs(simulation);

        for (NetsimNE ne : neList) {
            String nodeName = ne.getName();
            if (cmdResponse.contains(nodeName)) {
                startedNeList.add(ne);
            }
        }
        return startedNeList;
    }
}
