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

public class MediationIntegration extends MediationNodeNames {
    /**
	 * 
	 */
	private static final String SUB_NETWORK_ONRM_ROOT_MO_SUB_NETWORK_FDD_GROUP_ME_CONTEXT = "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=";
	Logger logger = Logger.getLogger(this.getClass());

    public List<MediationServiceNode> wantedNodes() { 
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        try {
            String destDir = getDestDir();
            String node = "LTE01";
            String simulation = "LTEC1110x160-ST-FDD-5K-" + node;
            String transformNodeName ;
            MediationServiceNode nextObject;
            String nodeName;
            
            for ( int i=11;i<=20;i++ )
            {            
	            transformNodeName = SUB_NETWORK_ONRM_ROOT_MO_SUB_NETWORK_FDD_GROUP_ME_CONTEXT + node + "ERBS000" + i;
	            nextObject = new MediationServiceNode(transformNodeName, destDir);
	            result.add(nextObject);
	            nodeName = transformNodeName.substring(transformNodeName.lastIndexOf('=') + 1);
	            NodeSimulationList.addSimulationToNodeName(nodeName, simulation);             
            }                     
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
