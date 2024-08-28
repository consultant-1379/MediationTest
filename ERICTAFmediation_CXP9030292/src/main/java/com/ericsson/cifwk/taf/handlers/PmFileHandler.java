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
package com.ericsson.cifwk.taf.handlers;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;

public class PmFileHandler {

    static RemoteCommandExecutor sshRemoteCommandExecutor;
    static Logger logger = Logger.getLogger(PmFileHandler.class);

    // static Logger logger = Logger.getLogger(MediationServiceOperator.class);

    /**
     * @param host1
     * @param filePath1
     * @param host2
     * @param filePath2
     * @return
     */
    public static boolean compareRemoteFiles(Host host1, String filePath1, Host host2, String filePath2) {

        logger.info("Comparing " + filePath1 + " on host " + host1 + " with " + filePath2 + " on host " + host2);

        if (!checkRemoteFileExists(host1, filePath1))
            // throw new RemoteFileNotFoundException(host1, filePath1);
            return false;
        if (!checkRemoteFileExists(host2, filePath2))
            // throw new RemoteFileNotFoundException(host2, filePath2);
            return false;
        boolean resultOfCompareRemoteFiles = (getRemoteFileInformation(host1, filePath1).md5.equals(getRemoteFileInformation(host2, filePath2).md5))
                && (getRemoteFileInformation(host1, filePath1).filesize.equals(getRemoteFileInformation(host2, filePath2).filesize));
        logger.debug("Returning " + resultOfCompareRemoteFiles + " from compareRemoteFiles() method");
        return resultOfCompareRemoteFiles;

    }

    public static boolean checkRemoteFileExists(Host host, String filePath) {

        logger.info("Checking does " + filePath + " on host " + host + " exist...");

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(host);

        String cmd = "bash -c \"test -f " + filePath + " && echo true\"";
        logger.info("just before sshRemoteCommandExecutor, cmd =" + cmd);
        String cmdOutput1 = sshRemoteCommandExecutor.simplExec(cmd);
        // logger.info("Output of checkRemoteFilesCommand = " +
        // sshRemoteCommandExecutor.simplExec(cmd));
        if (cmdOutput1.equals("true"))
            return true; // explain logic here it specific to stderr or stdout
                         // from the shell ?
        else {
            logger.info("output was =" + cmdOutput1);
            return false;
        }

    }

    public static String checkRemoteFileListings(Host host, String filePath) {

        logger.info("Checking what files list at  " + filePath + " on host " + host);

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(host);

        logger.info("Received what files list at  " + filePath + " on host " + host);

        // list files only in file path
        String cmd = "bash -c \"ls -p " + filePath + " | grep -v /\"";
        logger.info("just before sshRemoteCommandExecutor " + cmd);

        String cmdOutput1 = sshRemoteCommandExecutor.simplExec(cmd);
        return cmdOutput1;
    }

    public static String checkRemoteDirectoryListings(Host host, String filePath) {

        logger.info("Checking what Directory list at  " + filePath + " on host " + host);

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(host);

        // list directories in file path
        String cmd = "bash -c \"ls -p " + filePath + " | grep /\"";
        logger.info("just before sshRemoteCommandExecutor " + cmd);

        String cmdOutput1 = sshRemoteCommandExecutor.simplExec(cmd);
        return cmdOutput1;
    }

    public static boolean createRemoteFile(Host host, String filePath, String fileSize, String sizeType) {

        logger.debug("Creating remote file " + filePath + " on host " + host);

        // TODO
        // temporary method, would not release this....as is....

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(host);

        // example dd if=/dev/zero of=/a/b/c.txt bs=1M count=1 1Mb file

        sshRemoteCommandExecutor.simplExec("bash -c \"dd if=/dev/zero of=" + filePath + " bs=" + fileSize + sizeType + " count=1\"");

        // TODO check stderr on dd command rather than another call to
        // checkfileexists

        return checkRemoteFileExists(host, filePath);

    }

    public static boolean deleteRemoteFile(Host host, String filePath) {

        logger.info("Deleting remote file " + filePath + " on host " + host);

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(host);

        String cmdOutput1 = sshRemoteCommandExecutor.simplExec("bash -c \"rm -rf " + filePath + "\"");

        // TODO
        // check if file deleted by checking it exists and reversing the boolean
        // returned or check stderr on rm command

        if (cmdOutput1.length() > 0)
            return true; // explain logic here it specific to stderr or stdout
                         // from the shell ?
        else
            return false;

    }

    public static boolean copyRemoteFile(Host host1, String filePath1, String filePath2) {

        logger.debug("Copying " + filePath1 + " on host " + host1 + " to " + filePath2);

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(host1);

        String cmdOutput1 = sshRemoteCommandExecutor.simplExec("bash -c \"cp " + filePath1 + " " + filePath2 + "\"");

        if (cmdOutput1.length() > 0)
            return true;
        else
            return false;
    }

    public static FileStructure getRemoteFileInformation(Host host, String filePath) {

        //        logger.info("Getting remote file information for " + filePath + " on host " + host);

        sshRemoteCommandExecutor = new SshRemoteCommandExecutor();
        sshRemoteCommandExecutor.setHost(host);

        //        logger.info("Received remote file information for " + filePath + " on host " + host);

        FileStructure filestructure = new FileStructure();

        int SIZE_POSITION = 4;
        int MD5_POSITION = 0;

        //        logger.info("Getting list of files for " + filePath + " on host " + host);

        String cmdOutput1 = sshRemoteCommandExecutor.simplExec("bash -c \"ls -l " + filePath + "\"");
        filestructure.filesize = cmdOutput1.split(" ")[SIZE_POSITION];

        //        logger.info("Received list of files for " + filePath + " on host " + host);

        String cmdOutput2 = sshRemoteCommandExecutor.simplExec("bash -c \"md5sum " + filePath + "\"");
        filestructure.md5 = cmdOutput2.split(" ")[MD5_POSITION];

        //        logger.info("Performed checksum on " + filePath + " on host " + host);

        return filestructure;
    }

}
