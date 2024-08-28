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

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;

public class MediationUnsuccessfulReadDataFTP extends
        MediationUnsuccessfulReadData {
    Logger logger = Logger.getLogger(this.getClass());
    private SshRemoteCommandExecutor sshRemoteCommandExecutor;

    //    public List<MediationServiceNode> wantedNodes() {
    //        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
    //
    //        String nodeName = "LTE01ERBS00136";
    //        String sourceDirectory = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/LTEC1110x160-ST-FDD-5K-LTE01/" + nodeName + "/fs/";
    //        String password = "netsim";
    //
    //        result = (ArrayList<MediationServiceNode>) wantedNodes(nodeName, sourceDirectory, password);
    //        return result;
    //    }

    @Override
    public List<MediationServiceNode> wantedNodes() { 
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();

        try {
            String destDir = getDestDir();

            String node = "LTE01";
            String nodeName = "LTE01ERBS00137";
            Host netsimHost = DataHandler.getHostByName("Netsim");

            sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
            sshRemoteCommandExecutor.setHost(DataHandler.getHostByName("ms1"));

            logger.info("Running readError script: cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./readError.sh "
                    + node + " " + nodeName + " " + netsimHost.getOriginalIp());

            String runScript = "cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./readError.sh "
                    + node + " " + nodeName + " " + netsimHost.getOriginalIp();

            sshRemoteCommandExecutor.simplExec("bash -c \"" + runScript + "\"");
            logger.info("Finished running: cd /opt/ericsson/nms/litp/PmMediationTest/target/scripts; ./readError.sh "
                    + node + " " + nodeName + " " + netsimHost.getOriginalIp());

            String sourceDirectory = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/LTEC1110x160-ST-FDD-5K-LTE01/"
                    + nodeName + "/fs/";
            String password = "netsim";
            String errorMessage = "550 Failed to open file.";
            result.add(new MediationServiceNode(
                    "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="
                            + nodeName, destDir + "src_file_no_perms/",
                    " counters", ".txt", "Collected.txt", password,
                    sourceDirectory + "ftp/file_no_perms/", 10, errorMessage)); // File does not have read permissions
            result.add(new MediationServiceNode(
                    "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=LTE01ERBS00155",
                    destDir + "src_node_not_accessible/",
                    " counters",
                    ".txt",
                    "Collected.txt",
                    password,
                    sourceDirectory + "ftp/",
                    0,
                    "Connection could not be established with Network Element due to remote host not avaialble for sftp connection")); // Node does not exist
            result.add(new MediationServiceNode(
                    "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="
                            + nodeName, destDir + "src_empty_file/",
                    " counters", ".txt", "Collected.txt", password,
                    sourceDirectory + "ftp/empty_file/", 7,
                    "Empty file (zero bytes) was transferred")); // Empty file transfer

            //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_wrong_password/",
            //                    " counters", ".txt", "Collected.txt", "secretly", sourceDirectory + "ftp/", 0,
            //                    "Could not establish sftp connection with the Network Element")); // wrong password    
            //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=Banana", destDir + "src_no_dp/", " counters", ".txt",
            //                    "Collected.txt", "secret", sourceDirectory + "ftp/", 11, "Cannot perform requested task, as its data path cannot be instantiated")); // Node is not available
            String sourceDir = sourceDirectory + "ftp/no_perms/";
            result.add(new MediationServiceNode(
                    "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="
                            + nodeName, destDir + "src_dir_no_perms/",
                    " counters", ".txt", "Collected.txt", password, sourceDir,
                    10, errorMessage)); // Directory does not have read permissions
            sourceDir = sourceDirectory + "ftp/no_perms2/";
            result.add(new MediationServiceNode(
                    "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="
                            + nodeName, destDir + "src_dir_not_exist/",
                    " counters", ".txt", "Collected.txt", password, sourceDir,
                    10, errorMessage)); // Directory does not exist
            sourceDir = sourceDirectory + "ftp/empty_dir/";
            result.add(new MediationServiceNode(
                    "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="
                            + nodeName, destDir + "src_empty_dir/",
                    " counters", ".txt", "Collected.txt", password, sourceDir,
                    10, errorMessage)); // Empty Directory
            sourceDir = sourceDirectory + "ftp/file_does_not_exist/";
            result.add(new MediationServiceNode(
                    "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="
                            + nodeName, destDir + "src_file_unavailable/",
                    " counters", ".txt", "Collected.txt", password, sourceDir,
                    10, errorMessage)); // Non empty directory in which file does not exist
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
