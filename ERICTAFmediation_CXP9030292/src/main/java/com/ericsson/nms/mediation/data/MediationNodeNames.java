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

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.DataProvider;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.handlers.NetsimHandlerWrapper;

/** Super Class for Mediation Data Sets */

public abstract class MediationNodeNames implements DataProvider{

    Logger logger = Logger.getLogger(this.getClass());
    static Logger logger2 = Logger.getLogger(MediationNodeNames.class);
    public int numberOfLTENodes = 0;
    public int numberOfRNC36Nodes = 0;
    public int numberOfrnc37Nodes = 0;
    public int numberOfSFTPNodes = 0;

    public abstract List<MediationServiceNode> wantedNodes(); 

    public String getDestDir() throws Exception {
        /**
         * Read properties from file inside jar, top level
         */
//    	 String destDir = DataHandler.getAttribute("mediation.destDirectory").toString();      
//         return destDir;
        return "/cluster/collect_files/";
    }

    static String getSimulationName(String nodeName) {
        //        NetsimHandlerWrapper nhw = new NetsimHandlerWrapper();
        List<String> simulationList = NetsimHandlerWrapper.getListOfSimulations();
        logger2.info("Here is the list of simulations returned by netsimHandler: " + simulationList);

        for (String sim : simulationList) {
            logger2.info("Comparing the node " + nodeName + " to the simulation " + sim + " to see if they match.");
            if (sim.contains(nodeName)) {
                logger2.info("The node " + nodeName + " matches the simulation " + sim + ", return simulation");
                return sim;
            }
        }
        return "";
    }
}
