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
package com.ericsson.nms.mediation.test.cases;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.VUsers;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;
import com.ericsson.cifwk.taf.tools.cli.*;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.nms.mediation.data.MediationServiceNode;
import com.ericsson.nms.mediation.operators.FergusMegaRunnable;
import com.ericsson.nms.mediation.operators.MediationServiceOperator;
import com.ericsson.nms.mediation.test.data.MediationServiceTestDataProvider;

/**
 * //TODO Add class javadoc
 */
public class MediationService extends TorTestCaseHelper implements TestCase {

    MediationServiceOperator mediationService;
    private boolean firstRun;
    
    private RemoteFileHandler remoteFileHandler;
    private static final Logger logger = Logger.getLogger(MediationService.class);
    private final long shortDelay = 6000L;
    private final long mediumDelay = 7000L;
    private final long longDelay = 25000L;

    @BeforeSuite
    public void copyScriptsDirectoryToMs1() {

        String createScriptsDir = "mkdir -p /opt/ericsson/nms/litp/PmMediationTest/target/scripts";
        String deleteFilesOnNode = "rm -rf /cluster/collect_files/*";

        logger.info("Here is the ip address gathered from the MediationHost.properties: "
                + DataHandler.getHostByName("ms1").getIp());
               
        
        CLI cliToMs1 = new CLI(DataHandler.getHostByName("ms1"));
               
        Shell shell = cliToMs1.executeCommand(createScriptsDir);
        shell.writeln("exit"); 
        // Check command exit code
        shell.expectClose();
        assertTrue(shell.isClosed());     
        
        CLI cliToSc1 = new CLI(DataHandler.getHostByName("sc1"));
        logger.debug("Here is the ip address gathered from the MediationHost.properties: "
        			+ DataHandler.getHostByName("sc1").getIp());
        logger.debug("Here is the password  address gathered from the MediationHost.properties: "
    			+ DataHandler.getHostByName("sc1").getPass());
        
       
        shell = cliToSc1.executeCommand(deleteFilesOnNode);
        shell.writeln("exit");  
        // Check command exit code
        shell.expectClose();
        assertTrue(shell.isClosed());                     

        File myCurrentDir = new File(".");
        String myCurrentDirPath = myCurrentDir.getAbsolutePath();
        logger.info("Here is the current absolute path  "
    			+ myCurrentDirPath);         

        String localScriptsDir = myCurrentDirPath
                + "/ERICTAFmediation_CXP9030292/src/main/resources/scripts/";

        final File localFolder = new File(localScriptsDir);
        String jenkinsScriptsDir = "/var/jenkins/workspace/PM_TafMediationTest_Execute_Regression_13B/target/scripts/";
        final File jenkinsFolder = new File(jenkinsScriptsDir);
        String scriptsDir = null;

        if (localFolder.exists()) {
            logger.info("You are on a local machine, will use this folder: " + localScriptsDir);
            scriptsDir = localScriptsDir;
            File folder = new File(scriptsDir);
            ArrayList<String> folderContents = listFilesForFolder(folder);
            remoteFileHandler = new RemoteFileHandler(DataHandler.getHostByName("ms1"));
            logger.info("remoteFileHandler created, now sending file copying job");

            Host ms1host = DataHandler.getHostByName("ms1");

            for (String fileName : folderContents) {
                logger.info("Sending file: " + scriptsDir + fileName);
                String remoteFileLocation = "/opt/ericsson/nms/litp/PmMediationTest/target/scripts/";    
                remoteFileHandler.copyLocalFileToRemote(fileName ,remoteFileLocation);                              
                logger.info("File sent: " + fileName);
            }
        } else if (jenkinsFolder.exists()) {
            logger.info("You are on the jenkins machine, use this folder: " + jenkinsScriptsDir);
            scriptsDir = jenkinsScriptsDir;
        } else {
            logger.error("You are using a new environment, please contact Kerill or Fergus to add the necessary directory here!");
        }
    }

    @BeforeSuite
    public void verifyRequiredServicesAreDeployed() {

        if (!(new MediationServiceOperator().isFileCollectionServiceDeployed())) {
            fail("Cannot locate PM Service on the Jboss Server");
        }
        mediationService = new MediationServiceOperator();
        firstRun = true;
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataFtpNodes", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulNotificationOfFileCollectionRequestsForFtpNodes(MediationServiceNode node) {

        setTestcase("TOR_TT60_Func_1", "Verify that User receives Successful notification of simple Ftp FileCollectionRequests");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, longDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataSftpNodes", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulNotificationOfFileCollectionRequestsForSftpNodes(MediationServiceNode node) {

        setTestcase("TOR_TT60_Func_2", "Verify that User receives Successful notification of secure Ftp FileCollectionRequests");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, longDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestData", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulNotificationOfFileCollectionRequestsForFtpNodesandSftpNodes(MediationServiceNode node) {

        setTestcase("TOR_TT60_Func_3", "Verify that User receives Successful notification of mixture of ftp and sftp FileCollectionRequests");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, mediumDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataLargeFiles", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulNotificationOfFileCollectionRequestsTransferofLargeFile(MediationServiceNode node) {

        setTestcase("TOR_TT60_Func_4", "Verify that large files can be transferred");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, longDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationIntegrationData", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulNotificationOfFileUsingFdn(MediationServiceNode node) {

        setTestcase("TOR_TT782_Func_1", "Verify that fdn can be passed");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, shortDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationIPV6Data", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulNotificationOfIPV6Nodes(MediationServiceNode node) {

        setTestcase("TOR_TT782_Func_2", "Verify IPV6 Sftp Transfer is acceptable");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, shortDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationSameNodeDifferentDirectories", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulNotificationOfFileCollectionRequestsWhereLargeNumberofFilesInDirectory(MediationServiceNode node) {
        setTestcase("TOR_TT63_Func_1", "Ftp Test Case selection for 5MB,10MB,100MB,700MB Files in directory. This node is " + node.getSourceDir());
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, longDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationUnsuccessfulReadSFTP", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyUnsuccessfulNotificationReadErrorSFTP(MediationServiceNode node) {

        setTestcase("TOR-TT61-001", "SFtp Failure Notification, Invoke Read Error Situation  ");
        setTestStep("Call PM Service FileCollectionServiceRemote and Invoke collectFiles, Timed");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedUnSuccessfullResultOfCollectFiles(node, longDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationUnsuccessfulReadFTP", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyUnsuccessfulNotificationReadErrorFTP(MediationServiceNode node) {

        setTestcase("TOR-TT61-001", "Ftp Failure Notification, Invoke Read Error Situation  ");
        setTestStep("Call PM Service FileCollectionServiceRemote and Invoke collectFiles, Timed");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedUnSuccessfullResultOfCollectFiles(node, longDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationUnsuccessfulWriteFTP", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyUnsuccessfulNotificationWriteErrorFTP(MediationServiceNode node) {

        setTestcase("TOR-TT350-001", "Ftp Failure Notification, Invoke Write Error Situation  ");
        setTestStep("Call PM Service FileCollectionServiceRemote and Invoke collectFiles");
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedUnSuccessfullResultOfCollectFiles(node, longDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationUnsuccessfulWriteSFTP", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyUnsuccessfulNotificationWriteErrorSFTP(MediationServiceNode node) {

        setTestcase("TOR-TT350-001", "SFtp Failure Notification, Invoke Write Error Situation  ");
        setTestStep("Call PM Service FileCollectionServiceRemote and Invoke collectFiles");
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedUnSuccessfullResultOfCollectFiles(node, longDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationUnsuccessfulReWrite", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyUnsuccessfulNotificationReWriteError(MediationServiceNode node) {

        setTestcase("TOR-TT350-002", "Ftp Failure Notification, Invoke Write Error Situation  ");
        setTestStep("Call PM Service FileCollectionServiceRemote and Invoke collectFiles");
        // assertTrue(mediationService.clearDirectories(node.getName()));
        // assertTrue(mediationService.runOneSuccessfullTransfer());
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        if (firstRun) {
            assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, mediumDelay));
            firstRun = false;
        } else {
            assertTrue(mediationService.expectedUnSuccessfullResultOfCollectFiles(node, longDelay));
        }
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationConnectionDropped", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFtpTransferError(MediationServiceNode node) {

        setTestcase("TOR-TT478-001", "Ftp Failure during transfer  ");
        setTestStep("Call PM Service FileCollectionServiceRemote and Invoke collectFiles");
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedUnSuccessfullResultOfCollectFiles(node, shortDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceGzipDataProvider", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulNotificationAndDecompressionOfGzipFile(MediationServiceNode node) {
        setTestCase("TOR_TT406_Func_1", "Verify that a gzipped compressed file is transferred and decompressed");

        setTestStep("Clearing Directories ");
        assertTrue(mediationService.clearDirectories());
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedSuccessfullResultOfCollectFiles(node, shortDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceUnSupportedFileFormatGzipDataProvider", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifySuccessfulErrorNotificationOfTextFilewithGZIPCompressor(MediationServiceNode node) {
        setTestCase("TOR_TT406_Func_2", "Verify that an  unsupported file format  is handled as expected by Gzip Compressor");

        setTestStep("Clearing Directories ");
        assertTrue(mediationService.clearDirectories());
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Files from node");
        assertTrue(mediationService.collectFiles(node));

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.expectedUnSuccessfullResultOfCollectFiles(node, shortDelay));

    }

    @Test(groups = { "acceptance" })
    public void VerifyMediationPreUpgrade() {

        setTestcase(
                "TOR-TT488-001",
                "Mediation Service Version shall be able to listen to UpgradeEvent events, and perform the necessary operations to prepare for the upgrade, accept the upgrade");

        setTestStep("Tell Mediation Service to get ready for Upgrade");
        assertTrue(mediationService.serviceReadyForUpgrade());

        setTestStep("Restoring Mediation Registry");
        assertTrue(mediationService.restoreMediationRegistry());
    }

    @Test(groups = { "acceptance" })
    public void VerifyMediationRealUpgradeNoMediationTasksRanBefore() {
        setTestcase("TOR-TT489-001", "Actual Upgrade Test, no Mediation Tasks Ran Before ");

        setTestStep("Performing Upgrade");
        assertTrue(mediationService.servicePerformUpgrade());

        setTestStep("Checking service ready for upgrade");
        assertTrue(mediationService.serviceReadyForUpgrade());

        setTestStep("Checking Task state is Idle");
        assertTrue(mediationService.serviceTaskStateisIdle());

        setTestStep("Performing Upgrade");
        assertTrue(mediationService.servicePerformUpgrade());
    }

    @Test(groups = { "acceptance" })
    public void VerifyMediationRealUpgradeSomeMediationTasksRanBefore() {
        setTestcase("TOR-TT489-002", "Actual Upgrade Test, some Mediation Tasks Ran Before ");

        //        setTestStep("Running 12 Ftp Jobs");
        //        assertTrue(mediationService.run12FtpJobs());

        setTestStep("Checking service ready for upgrade");
        assertTrue(mediationService.serviceReadyForUpgrade());
        //
        //        setTestStep("Checking Task state is Idle");
        //        assertTrue(mediationService.serviceTaskStateisIdle());
        //
        //        setTestStep("Performing Upgrade");
        //        assertTrue(mediationService.servicePerformUpgrade());
    }

    @Test(groups = { "acceptance" })
    public void VerifyMediationRealUpgradeSeveralMediationTasksInProgress5ActiveJobs() {
        setTestcase("TOR-TT489-003", "Actual Upgrade Test, 5 Mediation Tasks in progress");

        setTestStep("Running 5 Ftp 100Mb Jobs");
        List<MediationServiceNode> collectionNodes = mediationService.run5Ftp100MbJobs();

        setTestStep("Checking service ready for upgrade");
        assertTrue(mediationService.serviceReadyForUpgrade());

        setTestStep("Checking Task state is Idle");
        assertTrue(mediationService.serviceTaskStateisIdle());

        setTestStep("Performing Upgrade");
        assertTrue(mediationService.servicePerformUpgrade());
    }

    @Test(groups = { "acceptance" })
    public void VerifyMediationRealUpgradeSeveralMediationTasksInProgress10ActiveJobs() {
        setTestcase("TOR-TT489-004", "Actual Upgrade Test, 10 Mediation Tasks in progress, 9 average Size, one Very Large Size");

        setTestStep("Running 10 Ftp 100Mb Jobs");
        List<MediationServiceNode> collectionNodes = mediationService.run10Ftp100MbJobs();

        setTestStep("Checking service ready for upgrade");
        assertTrue(mediationService.serviceReadyForUpgrade());

        setTestStep("Checking Task state is Idle");
        assertTrue(mediationService.serviceTaskStateisIdle());

        setTestStep("Verifying Successful Collection of Files");
        assertTrue(mediationService.processSetofResults(collectionNodes, shortDelay));

        setTestStep("Performing Upgrade");
        assertTrue(mediationService.servicePerformUpgrade());
    }

    @Test(groups = { "acceptance" })
    public void VerifyMediationRun5Jobs() {
        setTestcase("TOR-TT489-005", "5 Jobs");

        Integer requiredIteration = 1;
        List<MediationServiceNode> collectionNodes;
        for (int i = 0; i < requiredIteration; i++) {
            setTestStep("Execute and Verifying 5 jobs run");
            collectionNodes = mediationService.run5Ftp100MbJobs();
            assertTrue(mediationService.processSetofResults(collectionNodes, shortDelay));
        }
    }

    @Test(groups = { "acceptance" })
    public void VerifyMediationRun400ActiveJobsConcurrently() {
        setTestcase("TOR-TT489-006", "400 Nearly Concurrent jobs");
        Integer requiredIteration = 1;
        List<MediationServiceNode> collectionNodes;
        for (int i = 0; i < requiredIteration; i++) {
            setTestStep("Execute and Verifying 400 jobs run");
            collectionNodes = mediationService.run400FtpJobs(shortDelay);
            assertTrue(mediationService.processSetofResults(collectionNodes, shortDelay));
        }
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataDeltaComposite", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeDelta(MediationServiceNode node) {

        setTestcase("TOR_TT1148_Func_1", "Verify that User receives Successful notification of delta jobs for 1 node");
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Composite files from node");
        assertTrue(mediationService.collectComposite(node, node.getDeltaJobId().toString(), node.getDeltaPlugin().toString()));
        sleep(20);

        setTestStep("Checking what files are available for Delta Transfer");
        assertTrue(mediationService.checkDeltaDirectory(node));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataDeltaComposite2Nodes", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeDelta2Nodes(MediationServiceNode node) {

        setTestcase("TOR_TT1148_Func_2", "Verify that User receives Successful notification of delta jobs for 2 nodes");
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Composite files from node");
        assertTrue(mediationService.collectComposite(node, node.getDeltaJobId().toString(), node.getDeltaPlugin().toString()));
        sleep(20);

        setTestStep("Checking what files are available for Delta Transfer");
        assertTrue(mediationService.checkDeltaDirectory(node));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataDeltaComposite50Nodes", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeDelta50Nodes(MediationServiceNode node) {

        setTestcase("TOR_TT1148_Func_3", "Verify that User receives Successful notification of delta jobs for 50 nodes");
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Composite files from node");
        assertTrue(mediationService.collectComposite(node, node.getDeltaJobId().toString(), node.getDeltaPlugin().toString()));
        sleep(30); // 8 secs is required here to work best,its not great but
        // will do

        setTestStep("Checking what files are available for Delta Transfer");
        assertTrue(mediationService.checkDeltaDirectory(node));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataDeltaCompositeWithAlreadyExpiredTimeStamp", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeDeltaWithAlreadyExpiredTimeStamp(MediationServiceNode node) {

        setTestcase("TOR_TT1148_Func_4", "Verify that files are collected for delta jobs with already expired time stamp");
        setAdditionalResultInfo("Target Node= " + node.getName());
        mediationService.clearDirectories();
        String nodeName = node.toString();
        String currentJobId = node.getDeltaJobId().toString();
        String plugin = node.getDeltaPlugin().toString();

        setTestStep("Collecting Composite files from node");
        assertTrue(mediationService.collectComposite(node, currentJobId, plugin));
        sleep(55);

        setTestStep("Checking what files are available for Delta Transfer");
        assertEquals(0, mediationService.getNumberOfFileCollectedForNode(node.getBaseName()).size());
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataDeltaCompositeWithFutureTimeStamp", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeDeltaWithFutureTimeStamp(MediationServiceNode node) {

        setTestcase("TOR_TT1148_Func_5", "Verify that no files are collected for delta jobs with future time stamp");
        setAdditionalResultInfo("Target Node= " + node.getName());
        mediationService.clearDirectories();

        setTestStep("Collecting Composite files from node");
        assertTrue(mediationService.collectComposite(node, node.getDeltaJobId().toString(), node.getDeltaPlugin().toString()));
        sleep(1);

        setTestStep("Checking what files are available for Delta Transfer");
        assertEquals(0, mediationService.getNumberOfFileCollectedForNode(node.getBaseName()).size());
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataDeltaCompositeWithHalfExpiredTimeStamp", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeDeltaWithHalfExpiredTimeStamp(MediationServiceNode node) {

        setTestcase("TOR_TT1148_Func_6", "Verify that some files are collected for delta jobs with half expired time stamp");
        setAdditionalResultInfo("Target Node= " + node.getName());
        mediationService.clearDirectories();

        setTestStep("Collecting Composite files from node");
        assertTrue(mediationService.collectComposite(node, node.getDeltaJobId().toString(), node.getDeltaPlugin().toString()));
        sleep(20);

        setTestStep("Checking what files are available for Delta Transfer");
        assertEquals(40, mediationService.getNumberOfFileCollectedForNode(node.getBaseName()).size());
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataDeltaCompositeWithInvalidJobIds", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeDeltaWithInvalidFormatJobId(MediationServiceNode node) {

        setTestcase("TOR_TT1148_Func_7", "Verify that no files are collected for delta jobs with invalid format job id");
        setAdditionalResultInfo("Target Node= " + node.getName());
        mediationService.clearDirectories();

        setTestStep("Collecting Composite files from node");
        assertTrue(mediationService.collectComposite(node, node.getInvalidDeltaJobId("::DELTA_SYNC::1::ZZZZZZ").toString(), node.getDeltaPlugin()
                .toString()));
        sleep(1);

        setTestStep("Checking what files are available for Delta Transfer");
        assertEquals(0, mediationService.getNumberOfFileCollectedForNode(node.getBaseName()).size());
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataDeltaCompositeWithInvalidJobIds", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeDeltaWithIncompleteJobId(MediationServiceNode node) {

        setTestcase("TOR_TT1148_Func_8", "Verify that no files are collected for delta jobs with incomplete job id");
        setAdditionalResultInfo("Target Node= " + node.getName());
        mediationService.clearDirectories();

        setTestStep("Collecting Composite files from node");
        assertTrue(mediationService.collectComposite(node, node.getInvalidDeltaJobId("::DELTA_SYNC").toString(), node.getDeltaPlugin().toString()));
        sleep(1);

        setTestStep("Checking what files are available for Delta Transfer");
        assertEquals(0, mediationService.getNumberOfFileCollectedForNode(node.getBaseName()).size());
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataCellTraceComposite", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeCellTrace(MediationServiceNode node) {

        setTestcase("TOR_TT1089_Func_1", "Verify that User receives Successful notification of cellTrace jobs for 1 node");
        setAdditionalResultInfo("Target Node= " + node.getName());

        setTestStep("Collecting Composite Cell Trace from node");
        mediationService.collectCompositeCellTrace(node, node.getCellTraceJobId().toString(), node.getCellTracePlugin().toString());
        sleep(6);

        setTestStep("Checking Cell Trace Directory for what files are available for CellTrace Transfer");
        assertTrue(mediationService.checkCellTraceDirectory(node));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataCellTraceComposite50Nodes", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeCellTrace50Nodes(MediationServiceNode node) {

        setTestcase("TOR_TT1089_Func_2", "Verify that User receives Successful notification of cellTrace jobs for 50 node with Compression True");
        setAdditionalResultInfo("Target Node= " + node.getName());

        node.setDecompressionRequired(true);

        setTestStep("Collecting Composite Cell Trace from node");
        mediationService.collectCompositeCellTrace(node, node.getCellTraceJobId().toString(), node.getCellTracePlugin().toString());
        sleep(6);

        setTestStep("Checking Cell Trace Directory for what files are available for CellTrace Transfer");
        assertTrue(mediationService.checkCellTraceDirectory(node));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataUETrace", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeUETrace(MediationServiceNode node) {

        setTestcase("TOR_TT1090_Func_1", "Verify that User receives Successful notification of UE Trace jobs for 1 node");
        setAdditionalResultInfo("Target Node= " + node.getName());

        mediationService.collectCompositeUeTrace(node, node.getUETraceJobId().toString(), node.getUETracePlugin().toString());
        sleep(6);
        assertTrue(mediationService.checkUETraceDirectory(node));
    }

    @VUsers(vusers = { 2 })
    @Test(groups = { "acceptance" })
    public void VerifySuccessfulNotificationOfFileCollectionRequestsForAllNodes() {
        setTestcase("TOR_TT60_Func_1", "Concurrent jobs run on all available nodes on the netsim");

        Integer requiredIteration = 1;
        List<MediationServiceNode> collectionNodes;
        setTestStep("Collecting all Files and Verfiying they have been collected");
        for (int i = 0; i < requiredIteration; i++) {
            collectionNodes = mediationService.runAllFtpJobs();
            assertTrue(mediationService.processSetofResults(collectionNodes, shortDelay));
        }
    }

    @VUsers(vusers = { 2 })
    @Test(groups = { "acceptance" })
    public void VerifyMediationRun500ActiveJobsConcurrently() {
        setTestcase("TOR-TT489-007", "500 Nearly Concurrent jobs");

        Integer requiredIteration = 1;
        List<MediationServiceNode> collectionNodes;
        for (int i = 0; i < requiredIteration; i++) {
            setTestStep("Executing and Verifying 500 jobs running");
            collectionNodes = mediationService.run500FtpJobs();
            assertTrue(mediationService.processSetofResults(collectionNodes, shortDelay));
        }
    }

    @Context(context = { Context.API })
    @VUsers(vusers = { 20 })
    @Test(groups = { "acceptance" })
    public void verifyAllIsDying() {
        setTestcase("TOR-TTXXX-001", "Performance Test Task");
        assertTrue(mediationService.clearDirectories());
        List<Callable> tasks = new ArrayList<Callable>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new FergusMegaRunnable());
        }
    }

    @VUsers(vusers = { 2 })
    @Test(groups = { "acceptance" })
    public void VerifyDeltaComposite50NodesInParalell() {
        mediationService = new MediationServiceOperator();
        setTestcase("TOR_TT1148_Func_3", "Verify that User receives Successful notification of delta jobs for 50 nodes");

        //        Integer requiredIteration = 1;
        List<MediationServiceNode> collectionNodes;
        //        for (int i = 0; i < requiredIteration; i++) {
        setTestStep("Executing and Verifying 500 Composite Delta jobs running");
        collectionNodes = mediationService.run50CompositeDeltaJobs(2000L);
        sleep(50);
        assertTrue(mediationService.processSetofCompositeDeltaResults(collectionNodes, shortDelay));
        //        }
    }

    @VUsers(vusers = { 2 })
    @Test(groups = { "acceptance" })
    public void VerifyCellTraceComposite50NodesInParalell() {
        mediationService = new MediationServiceOperator();
        setTestcase("TOR_TT1089_Func_2", "Verify that User receives Successful notification of cellTrace jobs for 50 node with Compression True");

        List<MediationServiceNode> collectionNodes;
        setTestStep("Executing and Verifying 500 Composite Cell Trace jobs running");
        collectionNodes = mediationService.run50CompositeCellTraceJobs(2000L);
        sleep(30);
        assertTrue(mediationService.processSetofCompositeCellTraceResults(collectionNodes, shortDelay));
    }

    @VUsers(vusers = { 2 })
    @Test(groups = { "acceptance" })
    public void VerifyUeTraceComposite50NodesInParalell() {
        mediationService = new MediationServiceOperator();
        setTestcase("TOR_TT1090_Func_2", "Verify that User receives Successful notification of UE Trace jobs for 50 node");

        List<MediationServiceNode> collectionNodes;
        setTestStep("Executing and Verifying 500 Composite UE Trace jobs running");
        collectionNodes = mediationService.run50CompositeUeTraceJobs(2000L);
        sleep(35);
        assertTrue(mediationService.processSetofCompositeUeTraceResults(collectionNodes, shortDelay));
    }

    @Test(groups = { "acceptance" }, dataProvider = "MediationServiceTestDataCompositeUeTrace50Nodes", dataProviderClass = MediationServiceTestDataProvider.class)
    public void VerifyFileCollectionRequestsForCompositeUETrace50Nodes(MediationServiceNode node) {

        setTestcase("TOR_TT1090_Func_2", "Verify that User receives Successful notification of UE Trace jobs for 50 node");
        setAdditionalResultInfo("Target Node= " + node.getName());

        mediationService.collectCompositeUeTrace(node, node.getUETraceJobId().toString(), node.getUETracePlugin().toString());
        sleep(30);
        assertTrue(mediationService.checkUETraceDirectory(node));
    }
    
    
    @AfterSuite
    public void verifyRequiredServicesClosedDown() {

        mediationService.closeFileCollectionService();
           
    }

    public ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> folderContents = new ArrayList<String>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                folderContents.add(fileEntry.getName());
            }
        }
        return folderContents;
    }

    public void deleteFilesInFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                deleteFilesInFolder(fileEntry);
            } else {
                if (fileEntry.delete()) {
                    System.out.println(fileEntry.getName() + " is deleted!");
                } else {
                    System.out.println("Delete operation is failed.");
                }
            }
        }
    }
}
