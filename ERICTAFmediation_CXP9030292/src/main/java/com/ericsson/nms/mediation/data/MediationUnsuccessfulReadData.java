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

public class MediationUnsuccessfulReadData extends MediationNodeNamesData { 
    Logger logger = Logger.getLogger(this.getClass());

    //    public List<MediationServiceNode> wantedNodes(String nodeName, String sourceDirectory, String password) {
    //        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
    //
    //        try {
    //            String destDir = getDestDir();
    //
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_empty_file/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/empty_file/", 7,
    //                    "Empty file (zero bytes) was transferred")); // Empty file transfer
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_file_no_perms/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/file_no_perms/", 10,
    //                    "An SftpException  occured trying to download the file { ftp/file_no_perms//" + nodeName + ".txt} to a output stream")); // File does not have read permissions
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=LTE01ERBS00155", destDir + "src_node_not_accessible/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/", 0,
    //                    "Connection could not be established with Network Element due to remote host not avaialble for sftp connection")); // Node does not exist
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_wrong_password/", " counters", ".txt", "Collected.txt", "secretly", sourceDirectory + "ftp/", 0,
    //                    "Could not establish sftp connection with the Network Element")); // wrong password    
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=Banana", destDir + "src_no_dp/", " counters", ".txt", "Collected.txt", "secret", sourceDirectory + "ftp/", 11,
    //                    "Cannot perform requested task, as its data path cannot be instantiated")); // Node is not available
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_dir_no_perms/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/no_perms/", 10,
    //                    "An SftpException  occured trying to download the file { ftp/no_perms//" + nodeName + ".txt} to a output stream")); // Directory does not have read permissions
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_dir_not_exist/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/no_perms2/", 10,
    //                    "An SftpException  occured trying to download the file { ftp/no_perms2//" + nodeName + ".txt} to a output stream")); // Directory does not exist
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_empty_dir/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/empty_dir/", 10,
    //                    "An SftpException  occured trying to download the file { ftp/empty_dir//" + nodeName + ".txt} to a output stream")); // Empty Directory
    //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_file_unavailable/", " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/file_does_not_exist/", 10,
    //                    "An SftpException  occured trying to download the file { ftp/file_does_not_exist//" + nodeName + ".txt} to a output stream")); // Non empty directory in which file does not exist
    //        } catch (Exception e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //        return result;
    //    }

    public List<MediationServiceNode> wantedNodes(String nodeName, String sourceDirectory, String password) {
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();

        try {
            String destDir = getDestDir();

            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_file_no_perms/",
                    " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/file_no_perms/", 10,
                    "An SftpException  occured trying to download the file {ftp/file_no_perms/" + nodeName + ".txt} to an output stream")); // File does not have read permissions
            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_empty_file/", " counters",
                    ".txt", "Collected.txt", password, sourceDirectory + "ftp/empty_file/", 7, "Empty file (zero bytes) was transferred")); // Empty file transfer
            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=LTE01ERBS00155", destDir + "src_node_not_accessible/",
                    " counters", ".txt", "Collected.txt", password, sourceDirectory + "ftp/", 0,
                    "Connection could not be established with Network Element due to remote host not avaialble for sftp connection")); // Node does not exist
            //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_wrong_password/",
            //                    " counters", ".txt", "Collected.txt", "secretly", sourceDirectory + "ftp/", 0,
            //                    "Could not establish sftp connection with the Network Element")); // wrong password    
            //            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=Banana", destDir + "src_no_dp/", " counters", ".txt",
            //                    "Collected.txt", "secret", sourceDirectory + "ftp/", 11, "Cannot perform requested task, as its data path cannot be instantiated")); // Node is not available
            String sourceDir = sourceDirectory + "ftp/no_perms/";
            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_dir_no_perms/",
                    " counters", ".txt", "Collected.txt", password, sourceDir, 10, "An SftpException  occured trying to download the file {" + sourceDir
                            + nodeName + ".txt} to an output stream")); // Directory does not have read permissions
            sourceDir = sourceDirectory + "ftp/no_perms2/";
            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_dir_not_exist/",
                    " counters", ".txt", "Collected.txt", password, sourceDir, 10, "An SftpException  occured trying to download the file {" + sourceDir
                            + nodeName + ".txt} to an output stream")); // Directory does not exist
            sourceDir = sourceDirectory + "ftp/empty_dir/";
            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_empty_dir/", " counters",
                    ".txt", "Collected.txt", password, sourceDir, 10, "An SftpException  occured trying to download the file {" + sourceDir + nodeName
                            + ".txt} to an output stream")); // Empty Directory
            sourceDir = sourceDirectory + "ftp/file_does_not_exist/";
            result.add(new MediationServiceNode("SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext=" + nodeName, destDir + "src_file_unavailable/",
                    " counters", ".txt", "Collected.txt", password, sourceDir, 10, "An SftpException  occured trying to download the file {" + sourceDir
                            + nodeName + ".txt} to an output stream")); // Non empty directory in which file does not exist
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
