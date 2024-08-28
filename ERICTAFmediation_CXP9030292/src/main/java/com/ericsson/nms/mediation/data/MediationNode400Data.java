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

public class MediationNode400Data extends MediationNodeNames{


	public MediationNode400Data(){
		numberOfLTENodes = 100;
    }
       
    public List<MediationServiceNode> wantedNodes() { 

        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        NetsimHandlerWrapper netsimHost = new NetsimHandlerWrapper();
        try {
            String destDir = "";
            destDir = getDestDir();
            String fdnNode="";
            MediationTransformHandler mediationTransform = new MediationTransformHandler();
//            for (String nodeName : netsimHost.getNodeNames(numberOfLTENodes, "LTEC1110x160-ST-FDD-5K-LTE01")) {
//                fdnNode = mediationTransform.transformNodeName(nodeName);
//                logger.info("Preparing for Node Transfer for Node : "+fdnNode); 
//                result.add(new MediationServiceNode(fdnNode, destDir));
//            }
           for (String nodeName2 : netsimHost.getNodeNames(numberOfLTENodes, "LTEC1110x160-ST-FDD-5K-LTE02")) {
                fdnNode = mediationTransform.transformNodeName(nodeName2);
                logger.info("Preparing for Node Transfer for Node : "+fdnNode); 
                result.add(new MediationServiceNode(fdnNode, destDir));    
            }     
//            for (String nodeName3 : netsimHost.getNodeNames(numberOfLTENodes, "LTEC1110x160-ST-FDD-5K-LTE03")) {               
//                fdnNode = mediationTransform.transformNodeName(nodeName3);
//                logger.info("Preparing for Node Transfer for Node : "+fdnNode); 
//                result.add(new MediationServiceNode(fdnNode, destDir));
//            }
//            for (String nodeName4 : netsimHost.getNodeNames(numberOfLTENodes, "LTEC1110x160-ST-FDD-5K-LTE04")) {
//                fdnNode = mediationTransform.transformNodeName(nodeName4);
//                logger.info("Preparing for Node Transfer for Node : "+fdnNode); 
//                result.add(new MediationServiceNode(fdnNode, destDir));    
//            }  
//            for (String nodeName5 : netsimHost.getNodeNames(numberOfLTENodes, "LTEC1110x160-ST-FDD-5K-LTE05")) {
//                fdnNode = mediationTransform.transformNodeName(nodeName5);
//                logger.info("Preparing for Node Transfer for Node : "+fdnNode); 
//                result.add(new MediationServiceNode(fdnNode, destDir));    
//            }                     
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }    
}