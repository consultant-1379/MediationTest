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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.handlers.RemoteCommandExecutor;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;

public class MediationUnsuccessfulWriteData extends MediationNodeNamesData {
    Logger logger = Logger.getLogger(this.getClass());
    public int numberOfNodes = 9;
    RemoteCommandExecutor sshRemoteCommandExecutor; 

    public List<MediationServiceNode> wantedNodes(String nodeName, String sourceDirectory, String password) {
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();

        /**
         * on the target blade do the equivalent of
         * 
         * chmod 444 /unWriteableDirectory
         */

        String destDir = "/cluster/collect_files/";
        //        String nodeName = "LTE01ERBS00115";

        try {
            destDir = getDestDir();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
           
            sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
            sshRemoteCommandExecutor.setHost(DataHandler.getHostByName("sc1"));

            String createUnWriteableDirectory = "rm -rf " + destDir + "/unWriteableDirectory;mkdir " + destDir + "/unWriteableDirectory;chmod 444 " + destDir + "/unWriteableDirectory";
            String createUnWriteableDirectoryWithExistingDir = "rm -rf " + destDir + "/unWriteableDirectoryWithExistingDir/" + nodeName + ";mkdir -p " + destDir + "/unWriteableDirectoryWithExistingDir/" + nodeName + ";chmod 444 " + destDir
                    + "/unWriteableDirectoryWithExistingDir/" + nodeName;
            String createReWriteDirectory = "rm -rf " + destDir + "/reWriteDirectory;mkdir " + destDir + "/reWriteDirectory;chmod 777 " + destDir + "/reWriteDirectory";

            sshRemoteCommandExecutor.simplExec("bash -c \"" + createUnWriteableDirectory + "\"");
            sshRemoteCommandExecutor.simplExec("bash -c \"" + createUnWriteableDirectoryWithExistingDir + "\"");
            sshRemoteCommandExecutor.simplExec("bash -c \"" + createReWriteDirectory + "\"");
            
            sshRemoteCommandExecutor.setHost(DataHandler.getHostByName("ms1"));
            sshRemoteCommandExecutor.simplExec("bash -c \" cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./create_full_fs.sh fullFileSystem/");
            //Runtime.getRuntime().exec(new String[] { "bash", "-c", "cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./create_full_fs.sh fullFileSystem/" });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "unWriteableDirectory/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/", 2, "Cannot store file: "
                + destDir + "unWriteableDirectory/" + nodeName + "/" + nodeName + "Collected.txt", ""));
        result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "unWriteableDirectoryWithExistingDir/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/", 2,
                "Cannot store file: " + destDir + "unWriteableDirectoryWithExistingDir/" + nodeName + "/" + nodeName + "Collected.txt", ""));
        result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, "/fullFileSystem/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/", 3, "Cannot store file: /fullFileSystem/"
                + nodeName + "/" + nodeName + "Collected.txt", ""));
        return result;
    }

   

}
