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

import java.util.HashMap;

import org.apache.log4j.Logger;

public class NodeSimulationList {
    static Logger logger2 = Logger.getLogger(NetsimHandlerWrapper.class);
    static HashMap<String, String> mapOfNodeNamesToSims = new HashMap<String, String>(1000);

    public static void addSimulationToNodeName(String nodeName, String simulation) {
        logger2.debug("Here is the HashMap from getSimulationFromNodeName of values for nodeName and sim: " + mapOfNodeNamesToSims.keySet());
        mapOfNodeNamesToSims.put(nodeName, simulation);
        logger2.debug("Added the node: " + nodeName + " to the HashMap with simulation name : " + simulation);
    }

    public static String getSimulationFromNodeName(String nodeName) {
        logger2.debug("Here is the HashMap from getSimulationFromNodeName of values for nodeName and sim: " + mapOfNodeNamesToSims.keySet());
        if (mapOfNodeNamesToSims.containsKey(nodeName)) {
            String sim = mapOfNodeNamesToSims.get(nodeName);
            return sim;
        } else {
            return null;
        }
    }
}
