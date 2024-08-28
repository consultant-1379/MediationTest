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

public class RbsNodeForReWritingError extends MediationNodeNames {
    Logger logger = Logger.getLogger(this.getClass());

    public List<MediationServiceNode> wantedNodes() { 
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        
        String destDir = "";

        try {
            destDir = getDestDir();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            result.add(new MediationServiceNode("RNC36RBS02", destDir + "/reWriteDirectory"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }    
}
