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

import com.ericsson.cifwk.taf.handlers.MediationTransformHandlerWrapper;
import com.ericsson.cifwk.taf.handlers.NodeSimulationList;

public class MediationIPV6Nodes extends MediationNodeNames {
    Logger logger = Logger.getLogger(this.getClass());

    public List<MediationServiceNode> wantedNodes() {
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        try {
            String destDir = getDestDir();
            String simulation = "LTED125-V2x160-ST-FDD-LTE10";
            String erbsName = "LTE10ERBS0015";
            // IPV6 Nodes current defined on Netsimlin527 , LTE10 node 150 to 159 
            //String transformNodeName = "SubNetwork=ONRM_ROOT_MO,SubNetwork=ERBS-SUBNW-1,MeContext=LTE10ERBS00150";
            
            for(int nodeIndex = 0; nodeIndex <= 9; nodeIndex++) {                                    
                String transformedNodeName = MediationTransformHandlerWrapper.findTransformNodeName(erbsName+nodeIndex);
                logger.info("Preparing for Node Transfer for Node : "+transformedNodeName); 
                result.add(new MediationServiceNode(transformedNodeName, destDir));
                NodeSimulationList.addSimulationToNodeName(erbsName+nodeIndex, simulation);
            }
          
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result; 
    }
}
