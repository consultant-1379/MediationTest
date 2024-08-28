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

public class MediationFtpTransferException extends MediationNodeNamesData {
    Logger logger = Logger.getLogger(this.getClass());
    public int numberOfNodes = 9;

    public List<MediationServiceNode> wantedNodes() {
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();        
        result.add(new MediationServiceNode("FILETRANSFEREXP", "/cluster/collect_files/", " counters", ".txt", "Collected.txt", "secret", "ftp/", 5, "File FILETRANSFEREXPCollected.txt already exists in /collect_files/reWriteDirectory/FILETRANSFEREXP"));
        return result;
    }    
};