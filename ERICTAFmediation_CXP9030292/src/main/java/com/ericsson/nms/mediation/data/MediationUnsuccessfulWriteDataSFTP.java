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

import com.ericsson.cifwk.taf.handlers.RemoteCommandExecutor;

public class MediationUnsuccessfulWriteDataSFTP extends MediationUnsuccessfulWriteData {
    Logger logger = Logger.getLogger(this.getClass());
    public int numberOfNodes = 9;
    RemoteCommandExecutor sshRemoteCommandExecutor;

    public List<MediationServiceNode> wantedNodes() { 
        String nodeName = "LTE01ERBS00115";
        String sourceDirectory = "";
        String password = "secret";

        ArrayList<MediationServiceNode> result = (ArrayList<MediationServiceNode>) wantedNodes(nodeName, sourceDirectory, password);
        return result;
    }
}
