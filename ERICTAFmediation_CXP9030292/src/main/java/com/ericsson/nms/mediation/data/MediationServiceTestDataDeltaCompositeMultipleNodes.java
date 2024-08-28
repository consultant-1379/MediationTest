/*------------------------------------------------------------------------------
 *******************************************************************************

 * COPYRIGHT Ericsson 2012

 *

 * The copyright to the computer program(S) herein is the property of

 * Ericsson Inc. The programs may be used and/or copied only with written

 * permission from Ericsson Inc. or in accordance with the terms and

 * conditions stipulated in the agreement/contract under which the

 * program(S) have been supplied.

 *******************************************************************************

 *----------------------------------------------------------------------------*/

package com.ericsson.nms.mediation.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.handlers.NodeSimulationList;

public class MediationServiceTestDataDeltaCompositeMultipleNodes extends MediationNodeNames {

    private String timeStamp = "";
    Logger logger = Logger.getLogger(this.getClass());

    int numberOfNodes = 1;
    private String sourceDirectory;

    public MediationServiceTestDataDeltaCompositeMultipleNodes(int numberOfDeltaNodes, String sourceDir) {
        numberOfNodes = numberOfDeltaNodes;
        sourceDirectory = sourceDir;
    }

    public List<MediationServiceNode> wantedNodes() { 

        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();

        try {
            String fdnNode = "";
            String destDir = getDestDir();

            for (int i = 1; i <= numberOfNodes; i++) {

                String prefix = "00000" + i;
                prefix = prefix.substring(prefix.length() - 5);
                logger.info("Prepared nodeNamePart : " + prefix);

                String nodeName = "LTE01ERBS" + prefix;
                String simulation = getSimulationName(nodeName);
                MediationTransformHandler mediationTransform = new MediationTransformHandler();
                fdnNode = mediationTransform.transformNodeName(nodeName);
                logger.info("Preparing for Node Transfer for Node : " + fdnNode);
                MediationServiceNode nextObject = new MediationServiceNode(fdnNode, destDir);
                nextObject.setSourceDir(sourceDirectory);
                if (timeStamp != "") {
                    nextObject.setTimeStamp(timeStamp);
                }
                result.add(nextObject);
                NodeSimulationList.addSimulationToNodeName(nodeName, simulation);
            }

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        return result;

    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
