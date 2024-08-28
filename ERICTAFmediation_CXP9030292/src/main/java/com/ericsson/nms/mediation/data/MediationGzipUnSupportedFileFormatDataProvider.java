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

import com.ericsson.cifwk.taf.handlers.NodeSimulationList;

public class MediationGzipUnSupportedFileFormatDataProvider extends MediationNodeNames {

    @Override
    public List<MediationServiceNode> wantedNodes() {
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        String node = "LTE01";
        String simulation = getSimulationName(node);
        if (node.isEmpty()) {
            logger.info("This netsim does not contain any simulation for the node: " + node);
        } else {
            try {
                String destDir = getDestDir();
                String erbsName = node + "ERBS00003";
                short x = 1;
                MediationServiceNode nextObject = new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + erbsName, destDir, " counters", ".txt", "Collected.txt", "secret", "ftp/", 6, "Not in GZIP format", "", "GMT", x,
                        true );
                result.add(nextObject);
                NodeSimulationList.addSimulationToNodeName(erbsName, simulation);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result; 
    }

}
