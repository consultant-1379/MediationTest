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
package com.ericsson.nms.mediation.operators;

import groovy.util.GroovyMBean;

import java.io.File;
import java.util.*;

import javax.jms.ObjectMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.GenericOperator;
import com.ericsson.cifwk.taf.data.*;
import com.ericsson.cifwk.taf.handlers.*;
import com.ericsson.nms.mediation.data.*;
import com.ericsson.nms.mediation.operators.api.MediationServiceApiOperator;
import com.ericsson.oss.services.pm.service.api.FileCollectionDetails;
import com.ericsson.oss.services.pm.service.events.FileCollectionResponse;

public class MediationServiceOperator implements GenericOperator {

    Logger logger = Logger.getLogger(this.getClass());
    static Logger logger2 = Logger.getLogger(MediationServiceOperator.class);
    public Long eventTimeOut = 10000L;
    private String currentJobId;
    private MediationServiceNode currentNode;
    MediationServiceApiOperator apiOperator = new MediationServiceApiOperator();
    private float totalProcessTime = 0;
    static int number_of_mediationJmsHandlers = 0;
    static MediationJmsHandler mediationJmsResponseHandler;
    private String upgradeProperties;
    private final PropertyFileLoader propertyFileLoader = new PropertyFileLoader();

    public static MediationJmsHandler getJmsResponseHandler() {
        if (mediationJmsResponseHandler == null) {
            logger2.info("MediationJMSHandler does not exist, creating a new one!");
            logger2.debug("Number of MediationJMSHandler instances created: " + ++number_of_mediationJmsHandlers);
            mediationJmsResponseHandler = new MediationJmsHandler();
        } else {
            logger2.info("MediationJMSHandler already exists, return this one!");
            logger2.debug("Number of MediationJMSHandler instances created: " + number_of_mediationJmsHandlers);
        }
        return mediationJmsResponseHandler;
    }

    /**
     * @return
     */
    public boolean isFileCollectionServiceDeployed() {
        return apiOperator.isFileCollectionServiceDeployed();
    }
    
    /**
     * @return
     */
    public boolean closeFileCollectionService() {
        return apiOperator.closeFileCollectionService();
    }

    

    public boolean collectFiles(MediationServiceNode node) {
        currentJobId = (String) node.getJobId();
        currentNode = node;
        return (apiOperator.collectFiles(node, currentJobId));
    }

    public boolean collectComposite(MediationServiceNode node, String currentJobId, String plugin) {
        this.currentJobId = currentJobId;
        this.currentNode = node;
        return (apiOperator.collectCompositeFiles(node, currentJobId, plugin));
    }

    public boolean collectCompositeCellTrace(MediationServiceNode node, String currentJobId, String plugin) {
        this.currentJobId = currentJobId;
        this.currentNode = node;
        return (apiOperator.collectCompositeFilesCellTrace(node, currentJobId, plugin));
    }

    public boolean collectCompositeUeTrace(MediationServiceNode node, String currentJobId, String plugin) {
        this.currentJobId = currentJobId;
        this.currentNode = node;
        return (apiOperator.collectCompositeFilesUeTrace(node, currentJobId, plugin));
    }

    public List<MediationServiceNode> run50CompositeDeltaJobs(long shortDelay) {
        MediationServiceTestDataDeltaCompositeMultipleNodes my50CompositeDeltaNodes = new MediationServiceTestDataDeltaCompositeMultipleNodes(50,
                "ftp/");
        my50CompositeDeltaNodes.setTimeStamp("1361404800000");
        MediationServiceApiOperator apiOperator = new MediationServiceApiOperator();
        List<MediationServiceNode> collectionNodes = my50CompositeDeltaNodes.wantedNodes();

        for (MediationServiceNode currentNode : collectionNodes) {
            apiOperator.collectCompositeFiles(currentNode, currentNode.getDeltaJobId().toString(), currentNode.getDeltaPlugin().toString());
        }
        return collectionNodes;
    }

    public List<MediationServiceNode> run50CompositeCellTraceJobs(long shortDelay) {
        MediationServiceTestDataDeltaCompositeMultipleNodes my50CompositeDeltaNodes = new MediationServiceTestDataDeltaCompositeMultipleNodes(50,
                "/c/pm_data/");
        my50CompositeDeltaNodes.setTimeStamp("1361961900000");
        MediationServiceApiOperator apiOperator = new MediationServiceApiOperator();
        List<MediationServiceNode> collectionNodes = my50CompositeDeltaNodes.wantedNodes();

        for (MediationServiceNode currentNode : collectionNodes) {
            apiOperator.collectCompositeFilesCellTrace(currentNode, currentNode.getCellTraceJobId().toString(), currentNode.getCellTracePlugin()
                    .toString());
        }
        return collectionNodes;
    }

    public List<MediationServiceNode> run50CompositeUeTraceJobs(long shortDelay) {
        MediationServiceTestDataDeltaCompositeMultipleNodes my50CompositeDeltaNodes = new MediationServiceTestDataDeltaCompositeMultipleNodes(50,
                "/c/pm_data/");
        my50CompositeDeltaNodes.setTimeStamp("1361961900000");
        MediationServiceApiOperator apiOperator = new MediationServiceApiOperator();
        List<MediationServiceNode> collectionNodes = my50CompositeDeltaNodes.wantedNodes();

        for (MediationServiceNode currentNode : collectionNodes) {
            apiOperator.collectCompositeFilesCellTrace(currentNode, currentNode.getUETraceJobId().toString(), currentNode.getUETracePlugin()
                    .toString());
        }
        return collectionNodes;
    }

    public boolean expectedSuccessfullResultOfCompositeRequest(MediationServiceNode currentNode, final Long timeOut) {
        boolean result = false;

        try {
            FileCollectionResponse fileCollectionResponse = expectedResultOfCollectFiles(currentNode, timeOut);
            if (fileCollectionResponse != null) {
                if (fileCollectionResponse.isSuccessful()) {
                    logger.info("Job Id received from notification:" + fileCollectionResponse.getJobId());
                    verifyMediationSuccessfulNotificationIsReceived(currentNode, fileCollectionResponse);

                    result = checkDeltaDirectory(currentNode);
                    return result;
                } else {
                    logger.error("Error Code received where Successfull Result Expected " + fileCollectionResponse.getErrorCode());
                }
                logger.error("Error Description received where Successfull Result Expected " + fileCollectionResponse.getErrorDescription());
                return false;
            } else {
                logger.error("Response for Job Id never received " + currentNode.getJobId());
                return result;
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public boolean checkDeltaDirectory(MediationServiceNode node) {

        boolean result = false;
        try {
            logger.info("Result of checking what files are available for Delta Transfer ");

            String transformNodeName = node.getName();
            String sourceDir = node.getSourceDir();
            String nodeName = transformNodeName.substring(transformNodeName.lastIndexOf('=') + 1);
            logger.info("Here is the value of nodeName: " + transformNodeName);
            logger.info("Here is the value of baseName: " + nodeName);

            // TODO hardcoded to use this simulation for deltas

            String nodeSimulation = "LTEC1110x160-ST-FDD-5K-LTE01";
            logger.info("Here is the returned value of nodeSimulation: " + nodeSimulation);
            String netsimFilePath = null;
            if (sourceDir.contains("/netsim/netsim_dbdir/simdir/netsim/netsimdir/")) {
                logger.info("The source directory for this node already uses the absolute value, does not change");
                netsimFilePath = sourceDir;
            } else {
                logger.info("Add the absolute filepath to the source directory!");
                netsimFilePath = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + nodeSimulation + "/" + nodeName + "/fs/" + sourceDir;
            }

            result = verifyDeltaFilesExist(netsimFilePath, nodeName);
        } catch (Exception e) {
            logger.error("Exception thrown: " + e.getMessage());
        }
        logger.info("The result of verifyDeltaFilesExist: " + result);
        return result;
    }

    public boolean checkCellTraceDirectory(MediationServiceNode node) {

        boolean result = false;
        try {
            logger.info("Result of checking what files are available for CellTrace Transfer ");

            String transformNodeName = node.getName();
            String sourceDir = node.getSourceDir();
            String nodeName = transformNodeName.substring(transformNodeName.lastIndexOf('=') + 1);
            logger.info("Here is the value of nodeName: " + transformNodeName);
            logger.info("Here is the value of baseName: " + nodeName);

            // TODO hardcoded to use this simulation for deltas

            String nodeSimulation = "LTEC1110x160-ST-FDD-5K-LTE01";
            logger.info("Here is the returned value of nodeSimulation: " + nodeSimulation);

            List<String> netsimFilePaths = new ArrayList<String>();
            if (sourceDir.contains("/netsim/netsim_dbdir/simdir/netsim/netsimdir/")) {
                logger.info("The source directory for this node already uses the absolute value, does not change");
                netsimFilePaths.add(sourceDir);
            } else {
                logger.info("Add the absolute filepath to the source directory!");
                netsimFilePaths.add("/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + nodeSimulation + "/" + nodeName + "/fs/" + "c/pm_data1");
                netsimFilePaths.add("/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + nodeSimulation + "/" + nodeName + "/fs/" + "c/pm_data2");

                netsimFilePaths.add("/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + nodeSimulation + "/" + nodeName + "/fs/" + "c/pm_data3");
            }

            result = verifyCellTraceFilesExist(netsimFilePaths, nodeName);
        } catch (Exception e) {
            logger.error("Exception thrown: " + e.getMessage());
        }
        logger.info("The result of checkCellTraceDirectory: " + result);
        return result;
    }

    private boolean verifyCellTraceFilesExist(final List<String> netsimFilePathArray, final String nodeName) {

        List<String> netSimList = getNetsimFiles(netsimFilePathArray);

        List<String> collectedFilesList = getNumberOfFileCollectedForNodeCellTrace(nodeName);

        logger.info("There are [" + netSimList.size() + "] number of files on netsim");
        logger.info("There are [" + collectedFilesList.size() + "] number of files collected");
        if (netSimList.size() != collectedFilesList.size()) {
            return false;
        }

        boolean result = false;
        Host fileHandlerNodeOnLitpMachine = getHost("sc1");

        for (String collectedFile : collectedFilesList) {
            logger.info("collectedFile = " + collectedFile);
            String collectedFilePrefix = collectedFile.substring(0, collectedFile.indexOf("_") + 1);
            String sub = collectedFile.substring(collectedFile.lastIndexOf("celltracefile"));
            String collectedFileSuffix = sub.substring(sub.indexOf("_"), sub.indexOf(".gz"));

            for (String netsimFile : netSimList) {
                logger.info("netsimFile = " + netsimFile + " ,collectedFilePrefix = " + collectedFilePrefix + " ,collectedFileSuffix = "
                        + collectedFileSuffix);
                if (netsimFile.contains(collectedFilePrefix)) {
                    if (netsimFile.contains(collectedFileSuffix)) {
                        FileStructure fileStructure = PmFileHandler.getRemoteFileInformation(fileHandlerNodeOnLitpMachine, "/cluster/collect_files/"
                                + nodeName + "/" + collectedFile);
                        if (!fileStructure.filesize.equals("0")) {
                            logger.info("The size of the collect files is greater than 0");
                            result = true;
                            netSimList.remove(netsimFile);
                            break;
                        } else {
                            logger.info("The size of the collect files is 0");
                            return false;
                        }
                    }
                    break;
                }
                break;

            }
        }

        return result;
    }

    public boolean checkUETraceDirectory(MediationServiceNode node) {

        boolean result = false;
        try {
            logger.info("Result of checking what files are available for UETrace Transfer ");

            String transformNodeName = node.getName();
            String sourceDir = node.getSourceDir();
            String nodeName = transformNodeName.substring(transformNodeName.lastIndexOf('=') + 1);
            logger.info("Here is the value of nodeName: " + transformNodeName);
            logger.info("Here is the value of baseName: " + nodeName);

            // TODO hardcoded to use this simulation for deltas

            String nodeSimulation = "LTEC1110x160-ST-FDD-5K-LTE01";
            logger.info("Here is the returned value of nodeSimulation: " + nodeSimulation);
            String netsimFilePath = null;
            if (sourceDir.contains("/netsim/netsim_dbdir/simdir/netsim/netsimdir/")) {
                logger.info("The source directory for this node already uses the absolute value, does not change");
                netsimFilePath = sourceDir;
            } else {
                logger.info("Add the absolute filepath to the source directory!");

                // All these folder locations are specified in UeTraceFilesLocation file
                String[] netsimFolders = { "c/pm_data1", "c/pm_data2", "c/pm_data3" };

                String[] netsimAllFiles = null;
                List<String> netSimUETraceFilesList = new ArrayList<String>();
                for (String folder : netsimFolders) {
                    netsimFilePath = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + nodeSimulation + "/" + nodeName + "/fs/" + folder;
                    String netsimListofFiles = PmFileHandler.checkRemoteFileListings(getHost("Netsim"), netsimFilePath);
                    netsimAllFiles = netsimListofFiles.split("\\n+");
                    // Filter out the UeTraceFiles from each of the NetSim Directory.
                    for (String s : netsimAllFiles) {
                        if (s.contains("_uetrace_")) {

                            netSimUETraceFilesList.add(s);
                        }
                    }
                }
                result = verifyUETraceFilesExist(netSimUETraceFilesList, nodeName);
            }
        } catch (Exception e) {
            logger.error("Exception thrown: " + e.getMessage());
        }
        logger.info("The result of verifyUETraceFilesExist: " + result);
        return result;
    }

    private boolean verifyUETraceFilesExist(List<String> netSimUETraceFilesList, String nodeName) {

        boolean result = false;
        Host fileHandlerNodeOnLitpMachine = getHost("sc1");

        List<String> tokenizedNetsimFiles = tokenizeNetsimFiles(netSimUETraceFilesList);

        if (VerifyUETraceFileCollectionDirectories(tokenizedNetsimFiles, nodeName)) {
            List<String> collectedFilesList = getNumberOfFileCollectedForNodeUETrace(nodeName, tokenizedNetsimFiles);

            logger.info("There are [" + netSimUETraceFilesList.size() + "] number of files on netsim");
            logger.info("There are [" + collectedFilesList.size() + "] number of files collected");
            if (netSimUETraceFilesList.size() != collectedFilesList.size()) {
                logger.info(" The number of files collected do NOT match the number of files on Netsim");
                return false;
            }

            for (String netsimFile : netSimUETraceFilesList) {

                String tokenStart = netsimFile.substring(0, netsimFile.indexOf("-"));
                String endTimeToken = netsimFile.substring(15, 19);
                String sub = netsimFile.substring(netsimFile.lastIndexOf("uetrace"));
                String tokenEnd = sub.substring(sub.indexOf("_") + 1, sub.indexOf(".bin.gz"));

                for (String collectedFile : collectedFilesList) {

                    if ((collectedFile.contains(tokenEnd)) && (collectedFile.contains(tokenStart)) && (collectedFile.contains(endTimeToken))) {
                        String collectedfilePath = "/cluster/collect_files/" + nodeName + "/" + tokenEnd + "/" + "SubNetwork=FDDGroup" + "/"
                                + "MeContext=" + nodeName + "/";
                        FileStructure collectedfileStructure = PmFileHandler
                                .getRemoteFileInformation(fileHandlerNodeOnLitpMachine, collectedfilePath);
                        if (!collectedfileStructure.filesize.equals("0")) {
                            result = true;
                            collectedFilesList.remove(collectedFile);
                            break;
                        } else {
                            logger.info("Collected UETrace File Size is ZERO");
                            return false;
                        }
                    } else {
                        logger.info("collectedFile do NOT match NetSim file" + collectedFile);
                        return false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * This Method checks the Directories created in SC. These Directories should match the IMSI number in UETrace files.
     * 
     * @param tokenizedNetsimFileNames
     *            - IMSI tokens from UETrace Files.
     * @param nodeName
     * @return
     */
    private boolean VerifyUETraceFileCollectionDirectories(List<String> tokenizedNetsimFileNames, String nodeName) {

        boolean dirNameMatch = false;
        Host fileHandlerNodeOnLitpMachine = getHost("sc1");
        String[] collectedDirectories = PmFileHandler.checkRemoteDirectoryListings(fileHandlerNodeOnLitpMachine,
                "/cluster/collect_files/" + nodeName + "/").split("\\n+");
        logger.info("There are [" + tokenizedNetsimFileNames.size() + "] number of files on netsim");
        logger.info("There are [" + collectedDirectories.length + "] number of directories created");
        if (tokenizedNetsimFileNames.size() == collectedDirectories.length) {
            dirNameMatch = true;
            for (String dir : collectedDirectories) {
                //Trimming the dirName (like 45604501000D/) to get rid of "/"
                String dirTrimmed = dir.substring(0, dir.indexOf("/"));
                logger.info("collectedDirectory name is " + dirTrimmed);
                if (!tokenizedNetsimFileNames.contains(dirTrimmed)) {
                    dirNameMatch = false;
                    logger.info("netSim File tokens does NOT contain the directory of the collected UETrace file - " + dirTrimmed);
                }
            }
        } else {
            logger.info(" The number of directories created do NOT match the number of files in NetSim");
        }
        return dirNameMatch;
    }

    /**
     * This method will generate the IMSI (International mobile subscriber identity) tokens from the UETrace files available in NetSim
     * 
     * @param netSimAllFilesList
     *            - UeTrace Files available in all the Directories mentioned in UeTraceFileLocation file.
     * @return IMSI tokens
     */
    private List<String> tokenizeNetsimFiles(List<String> netSimAllFilesList) {
        List<String> netsimFileTokens = new ArrayList<String>();

        for (String netsimFile : netSimAllFilesList) {

            String tokenStart = netsimFile.substring(0, netsimFile.indexOf("_") + 1);
            String sub = netsimFile.substring(netsimFile.lastIndexOf("uetrace"));
            String tokenEnd = sub.substring(sub.indexOf("_") + 1, sub.indexOf(".bin.gz"));
            netsimFileTokens.add(tokenEnd);
        }
        return netsimFileTokens;
    }

    public List<String> getNumberOfFileCollectedForNodeUETrace(final String nodeName, List<String> tokenizedNetsimFiles) {
        Host fileHandlerNodeOnLitpMachine = getHost("sc1");

        List<String> collectedFilesList = new ArrayList<String>();
        for (String netSimToken : tokenizedNetsimFiles) {
            String[] collectedFiles = PmFileHandler.checkRemoteFileListings(fileHandlerNodeOnLitpMachine,
                    "/cluster/collect_files/" + nodeName + "/" + netSimToken + "/" + "SubNetwork=FDDGroup" + "/" + "MeContext=" + nodeName + "/")
                    .split("\\n+");

            for (String s : collectedFiles) {
                if (s.contains("_uetrace")) { // TODO hardcoded
                    collectedFilesList.add(s);
                }
            }
        }
        return collectedFilesList;
    }

    /**
     * @param netsimFilePathArray
     * @return
     */
    private List<String> getNetsimFiles(final List<String> netsimFilePathArray) {

        List<String> netSimList = new ArrayList<String>();
        for (String netsimFilePath : netsimFilePathArray) {

            String netsimListofFiles = PmFileHandler.checkRemoteFileListings(getHost("Netsim"), netsimFilePath);
            String[] netsimFilesForPath = netsimListofFiles.split("\\n+");
            for (String s : netsimFilesForPath) {
                if (s.contains("_CellTrace_")) { // this is the filter used by
                                                 // the
                    // plugins jar
                    netSimList.add(s);
                }
            }

        }
        return netSimList;
    }

    private boolean verifyDeltaFilesExist(final String filePath, final String nodeName) {

        String netsimListofFiles = PmFileHandler.checkRemoteFileListings(getHost("Netsim"), filePath);
        //        logger.info("Returned netsim from checkRemoteFileListings");
        String[] netsimFiles = netsimListofFiles.split("\\n+");
        List<String> netSimList = new ArrayList<String>();
        for (String s : netsimFiles) {
            if (s.contains("1.xml.gz")) { // this is the filter used by the
                                          // plugins jar
                netSimList.add(s);
            }
        }

        List<String> collectedFilesList = getNumberOfFileCollectedForNode(nodeName);

        logger.info("There are [" + netSimList.size() + "] number of files on netsim");
        logger.info("There are [" + collectedFilesList.size() + "] number of files collected");
        if (netSimList.size() != collectedFilesList.size()) {
            return false;
        }

        boolean result = false;
        Host fileHandlerNodeOnLitpMachine = getHost("sc1");
        for (String collectedFile : collectedFilesList) {
            String token = collectedFile.substring(0, collectedFile.indexOf("+"));

            for (String netsimFile : netSimList) {
                if (netsimFile.contains(token)) {

                    FileStructure fileStructure = PmFileHandler.getRemoteFileInformation(fileHandlerNodeOnLitpMachine, "/cluster/collect_files/"
                            + nodeName + "/" + collectedFile);
                    if (!fileStructure.filesize.equals("0")) {
                        result = true;
                        netSimList.remove(netsimFile);
                        break;
                    } else {
                        return false;
                    }

                } else {
                    return false;
                }
            }

        }

        return result;
    }

    public List<String> getNumberOfFileCollectedForNodeCellTrace(final String nodeName) {
        Host fileHandlerNodeOnLitpMachine = getHost("sc1");
        String[] collectedFiles = PmFileHandler.checkRemoteFileListings(fileHandlerNodeOnLitpMachine, "/cluster/collect_files/" + nodeName + "/")
                .split("\\n+");
        List<String> collectedFilesList = new ArrayList<String>();
        for (String s : collectedFiles) {
            if (s.contains("_celltrace")) { // TODO hardcoded
                collectedFilesList.add(s);
            }
        }

        return collectedFilesList;
    }

    public List<String> getNumberOfFileCollectedForNode(final String nodeName) {
        Host fileHandlerNodeOnLitpMachine = getHost("sc1");
        String[] collectedFiles = PmFileHandler.checkRemoteFileListings(fileHandlerNodeOnLitpMachine, "/cluster/collect_files/" + nodeName + "/")
                .split("\\n+");
        List<String> collectedFilesList = new ArrayList<String>();
        for (String s : collectedFiles) {
            if (s.contains(".xml")) {
                collectedFilesList.add(s);
            }
        }

        return collectedFilesList;
    }

    private Host getHost(String hostName) {
        Host fileHandlerNodeOnLitpMachine = propertyFileLoader.getHostByName(hostName);
        return fileHandlerNodeOnLitpMachine;
    }

    public boolean clearDirectories() {
        try {
            String deleteFile = getDestDir() + "*";
            PmFileHandler.deleteRemoteFile(getHost("sc1"), deleteFile);
            return true;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public FileCollectionResponse expectedResultOfCollectFiles(MediationServiceNode testNode, final Long timeOut) {
        logger.debug("In expectedResultOfCollectFiles");
        try {

            logger.info("Before getting message for queue, job id wanted is  " + testNode.getJobId());

            MediationJmsHandler jmsHand = getJmsResponseHandler();
            logger.debug("The value of the jms instance being used: " + jmsHand.toString());

            ObjectMessage message = (ObjectMessage) jmsHand.getMessageFromJmsQueue(currentJobId, timeOut);

            FileCollectionResponse response = (FileCollectionResponse) message.getObject();

            if (!currentJobId.equals(response.getJobId())) {
                logger.error("Job Ids not Matching " + testNode.getJobId() + " and " + response.getJobId());
                return null;
            }

            jmsHand.closeJmsHandler();

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception during expectedResultOfCollectFiles");
            return null;
        }
    }

    // public FileCollectionResponse expectedResultOfCollectFiles(
    // MediationServiceNode testNode) {
    // logger.info("In expectedResultOfCollectFiles");
    // try {
    //
    // logger.info("Before getting message for queue, job id wanted is  "
    // + testNode.getJobId());
    //
    // MediationJmsHandler jmsHand = getJmsResponseHandler();
    // logger.info("The value of the jms instance being used: "
    // + jmsHand.toString());
    //
    // currentJobId = "SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="
    // + testNode.getBaseName()
    // + "::DELTA_SYNC::96::Wed Feb 20 13:45:00 GMT 2013";
    // logger.info("Getting message from jmsQueue for job id:  "
    // + currentJobId);
    // ObjectMessage message = (ObjectMessage) jmsHand
    // .getMessageFromJmsQueue(currentJobId);
    //
    // logger.info("Got message : " + message.toString());
    // FileCollectionResponse response = (FileCollectionResponse) message
    // .getObject();
    // logger.info("Got Response : " + response.toString());
    //
    // if (!currentJobId.equals(response.getJobId())) {
    // logger.error("Job Ids not Matching " + testNode.getJobId()
    // + " and " + response.getJobId());
    // return null;
    // }
    //
    // jmsHand.closeJmsHandler();
    //
    // return response;
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.info("Exception during expectedResultOfCollectFiles "
    // + e.toString());
    // return null;
    // }
    // }

    public boolean expectedSuccessfullResultOfCollectFiles(MediationServiceNode currentNode, final Long timeOut) {
        boolean result = false;

        try {
            FileCollectionResponse fileCollectionResponse = expectedResultOfCollectFiles(currentNode, timeOut);
            if (fileCollectionResponse != null) {
                if (fileCollectionResponse.getErrorCode() == -1) {
                    logger.info("Job Id received from notification:" + fileCollectionResponse.getJobId());
                    verifyMediationSuccessfulNotificationIsReceived(currentNode, fileCollectionResponse);
                    result = compareFiles(currentNode);
                    return result;
                } else {
                    logger.error("Error Code received where Successfull Result Expected " + fileCollectionResponse.getErrorCode());
                }
                logger.error("Error Description received where Successfull Result Expected " + fileCollectionResponse.getErrorDescription());
                return false;
            } else {
                logger.error("Response for Job Id never received " + currentNode.getJobId());
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public boolean expectedUnSuccessfullResultOfCollectFiles(MediationServiceNode currentNode, final Long TimeOut) {

        try {
            FileCollectionResponse response = expectedResultOfCollectFiles(currentNode, TimeOut);
            if (response != null) {
                if (response.getErrorCode() != -1) {
                    return verifyMediationUnSuccessfulNotificationIsReceived(currentNode, response);
                    // return true;
                } else {
                    logger.error("Successfull Result was NOT Expected" + response.getErrorCode());
                }
                return false;
            } else {
                logger.error("Response for Job Id never received " + currentNode.getJobId());
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean compareFiles(MediationServiceNode node) throws Exception {
        Boolean fileTransferredCorrectly = false;
        logger.info("Confirm File has been transferred");
        String destinationDir = (String) node.getDestinationDir();
        String destinationFile = (String) node.getDestinationFile();
        // String sourceContents = (String) node.getSourceContents();
        logger.info("DESTINATION DIRECTORY: " + "/" + destinationDir + "/");
        logger.info("DESTINATION FILE: " + destinationFile);
        fileTransferredCorrectly = verifyFileBeenTransferred("/" + destinationDir + "/", destinationFile);
        logger.info("Result of checking if file is available: " + fileTransferredCorrectly);
        logger.info("Confirm File contents is correct");
        if (!fileTransferredCorrectly) {
            return fileTransferredCorrectly;
        }

        fileTransferredCorrectly = false;

        Host netsimHost = getHost("Netsim");

        String transformNodeName = node.getName();
        String sourceDir = node.getSourceDir();
        String nodeName = transformNodeName.substring(transformNodeName.lastIndexOf('=') + 1);
        logger.info("Here is the value of nodeName: " + transformNodeName);
        logger.info("Here is the value of baseName: " + nodeName);
        String nodeSimulation = NodeSimulationList.getSimulationFromNodeName(nodeName);
        logger.info("Here is the returned value of nodeSimulation: " + nodeSimulation);
        String netsimFilePath = null;
        if (sourceDir.contains("/netsim/netsim_dbdir/simdir/netsim/netsimdir/")) {
            logger.info("The source directory for this node already uses the absolute value, does not change");
            netsimFilePath = sourceDir + nodeName + ".txt";
        } else {
            logger.info("Add the absolute filepath to the source directory!");
            netsimFilePath = "/netsim/netsim_dbdir/simdir/netsim/netsimdir/" + nodeSimulation + "/" + nodeName + "/fs/" + sourceDir + nodeName
                    + ".txt";
        }

        logger.info("The value of netsimFilePath is: " + netsimFilePath);
        // String netsimFilePath = "/var/tmp/fileToCompare.txt";

        String nodeFilePath = "/" + destinationDir + "/" + destinationFile;
        boolean intermediate_boolean = PmFileHandler.compareRemoteFiles(netsimHost, netsimFilePath, getHost("sc1"), nodeFilePath);

        logger.info("Here is the output of compareRemoteFiles: " + intermediate_boolean);

        fileTransferredCorrectly = intermediate_boolean;

        return fileTransferredCorrectly;
    }

    private boolean verifyFileBeenTransferred(String expectedTargetDir, String expectedFileName) {
        String filePath = expectedTargetDir + expectedFileName;

        boolean fileExists = PmFileHandler.checkRemoteFileExists(getHost("sc1"), filePath);

        if (!fileExists) {
            logger.error(expectedTargetDir + expectedFileName + " file NOT transferred successfully");
        }
        return fileExists;
    }

    private boolean verifyMediationUnSuccessfulNotificationIsReceived(MediationServiceNode testNode, FileCollectionResponse response) {

        boolean result = false;
        logger.info("Job Id received from notification:" + response.getJobId());
        float measuredJobTime = response.getJobEndTime() - response.getJobStartTime();
        logger.info("Time for Process Job Id " + currentJobId + " is " + measuredJobTime);
        String errorDescription = StringUtils.chomp(response.getErrorDescription());
        logger.info("Here is response.getErrorDescription: " + errorDescription);
        logger.info("Here is response.getErrorCode: " + response.getErrorCode());

        if (response.getErrorCode() != currentNode.getErrorCode()) {
            logger.error("Wrong Error Code Returned: " + response.getErrorCode() + " when we expected: " + currentNode.getErrorCode());
            return result;
        } else {
            if ((errorDescription != null) && !(errorDescription.equals(currentNode.getErrorDescription()))) {
                logger.error("Wrong Error Description has been returned Returned description: " + errorDescription + " when we expected: "
                        + currentNode.getErrorDescription());
                return result;
            }
        }

        if (response.getErrorDescription() != null) {
            logger.error("Bytes Transferred  " + response.getBytesTransferred());
            if (response.getBytesTransferred() != -1) {
                logger.error("Bytes Transferred not Matching ");
                return result;
            }
            if (response.getBytesStored() != -1) {
                logger.error("Bytes Stored not Matching ");
                return result;
            }
            result = true;
        }
        return result;

    }

    private boolean verifyMediationSuccessfulNotificationIsReceived(MediationServiceNode testNode, FileCollectionResponse response) {

        FileCollectionDetails currentJobDetails = testNode.getFileCollectionDetails(currentJobId, testNode.getTimeZone(), testNode.getNodeType(),
                null);
        logger.info("Job Details" + currentJobDetails.getListOfFiles());
        /**
         * Transaction Overhead of one Byte added on, should be JIRAed for investigation
         */
        // long byteSize = sourceContents.getBytes().length + 1;
        logger.info("Bytes transferred value received from notification:" + response.getBytesTransferred());
        logger.info("Bytes stored value received from notification:" + response.getBytesStored());
        logger.info("Decompression required Value from notification: " + testNode.getDecompressionRequired());
        logger.info("Job Details Source Directory from notification:" + response.getSourceDir());
        logger.info("Job Details Source File from notification:" + response.getSourceFile());
        logger.info("Job Details Destination Directory from notification:" + response.getDestinationDir());
        logger.info("Job Details Destination File from notification:" + response.getDestinationFile());
        float measuredJobTime = response.getJobEndTime() - response.getJobStartTime();
        logger.info("Time for Process Job Id " + currentJobId + " is " + measuredJobTime / 1000 + " secs");
        logger.info("Job Details time zone notification:" + response.getTimeZone());
        logger.info("Job Details node type notification:" + response.getNodeType());
        logger.info("Job Details isCreatedWithInterceptor value: " + response.isCreatedWithInterceptor());

        totalProcessTime += measuredJobTime;
        logger.info("Total time to process jobs is " + totalProcessTime / 1000 + " secs");

        // if (!(byteSize == response.getBytesTransferred())) {
        // logger.error("Bytes Transferred not Matching " + byteSize
        // + " and " + response.getBytesTransferred());
        // return false;
        // }
        // if (!(byteSize == response.getBytesStored())) {
        // logger.error("Bytes Stored not Matching " + byteSize + " and "
        // + response.getBytesStored());
        // return false;
        // }
        return true;
    }

    public String getCurrentNumberofRegisteredMSs() {
        return RestCallHandler.getResultFromRest(getUpgradeProperties("upgrade.medservices.deployed.count.url"));
    }

    /**
     * @return
     */
    private String getUpgradeProperties(String prop) {
        upgradeProperties = propertyFileLoader.getProperty(prop);
        return upgradeProperties;
    }

    public boolean UpgradeEventSent() {
        /** Fire Upgrade Trigger user rest Call towards Upgrade Manager */
        String result = null;
        try {
            logger.info("About to send Upgrade Event sent towards MediationService1");
            result = RestCallHandler.getResultFromRest(getUpgradeProperties("upgrade.manager.url.prepare.upgrade.med.serv"));
            logger.info("result is -" + result);

            if (result != null) {
                logger.info("Upgrade Event sent towards MediationService1");
                return true;
            } else {
                logger.info("Upgrade Event is NOT sent towards MediationService1");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // public boolean UpgradeEventSentwithActiveTasks() {
    // /** Fire Upgrade Trigger user rest Call towards Upgrade Manager */
    // Process result = null;
    // try {
    // logger.info("About to send Upgrade Event sent towards MediationService1");
    // result = Runtime
    // .getRuntime()
    // .exec(new String[] { "/bin/bash", "-c",
    // "/opt/ericsson/nms/litp/PmMediationTest/target/scripts/upgradeTrigger.sh"
    // });
    // result.waitFor();
    // if (result.exitValue() == 0) {
    // logger.info("Upgrade Event sent towards MediationService1");
    // return true;
    // } else {
    // return false;
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // return false;
    // }
    // }

    public boolean serviceReadyForUpgrade() {
        try {

            String currentNumberofRegisteredMSs = getCurrentNumberofRegisteredMSs();
            logger.info("MediationService instance current value :" + currentNumberofRegisteredMSs);

            if (!UpgradeEventSent()) {
                logger.error("Upgrade Event was never sent towards MediationService1");
            }

            if (currentNumberofRegisteredMSs != null && !"0".equalsIgnoreCase(currentNumberofRegisteredMSs)) {
                Integer intValueOfCurrentNumberofRegisteredMSs = Integer.valueOf(currentNumberofRegisteredMSs.replaceAll("[^A-Za-z0-9]", ""));
                Integer deployments = intValueOfCurrentNumberofRegisteredMSs;
                Thread.sleep(Long.parseLong(getUpgradeProperties("upgrade.thread.delay.timeout")));
                logger.info("Verify from MediationCore that MediationService instance has been removed from MediationServiceConsumer cache");

                /** If at least one MSAddress remains */
                if (deployments > 1) {
                    logger.info("Multiple MediationService instances found");
                    logger.info("Multiple MediationService instances reduced to :" + getCurrentNumberofRegisteredMSs());
                    Integer result = deployments - 1;
                    return (getCurrentNumberofRegisteredMSs().equals(result.toString()));
                } else {
                    /** No MSAddress remain, registry is empty */
                    logger.info("MediationService instance current value :" + getCurrentNumberofRegisteredMSs());
                    return (getCurrentNumberofRegisteredMSs().equalsIgnoreCase("0"));
                }
            } else {
                logger.error("PreUpgrade Event could not be processed");
                return false;
            }
        } catch (Exception e) {
            logger.info("Exception Thrown during Pre Upgrade Event processsing");
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreMediationRegistry() {

        try {
            JbossHandler jbossBlade = initiateHost("jboss");
            String mediationEarVersion = getUpgradeProperties("upgrade.mediation.service");
            jbossBlade.undeployFile(mediationEarVersion, true);

            File mediationServiceEar = new File(getUpgradeProperties("upgrade.deploy.location") + mediationEarVersion);

            jbossBlade.deployFile(mediationServiceEar);

            if (!checkIsDeployed("deployment-info --name=" + mediationEarVersion, jbossBlade)) {
                logger.error("Could not Determine if Mediation Service was Deployed, please check your active Jboss Instances");
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean servicePerformUpgrade() {
        // TODO Auto-generated method stub
        try {

            JbossHandler jbossBlade = initiateHost("jboss");
            String mediationEarVersion = getUpgradeProperties("upgrade.mediation.service");
            logger.info("Current Status of MediationService1 according to Jboss Management: "
                    + checkIsDeployed("deployment-info --name=" + mediationEarVersion, jbossBlade));

            logger.info("Trying to Undeploy " + getUpgradeProperties("upgrade.deploy.location") + mediationEarVersion);

            jbossBlade.undeployFile(mediationEarVersion, true);

            logger.info("Check Deployment State ");

            Thread.sleep(10000L);

            File mediationServiceEar = new File(getUpgradeProperties("upgrade.deploy.location") + mediationEarVersion);

            jbossBlade.deployFile(mediationServiceEar);

            logger.info("Trying to Deploy " + getUpgradeProperties("upgrade.deploy.location") + mediationEarVersion);

            logger.info("Current Status of MediationService1 according to Jboss Management: "
                    + checkIsDeployed("deployment-info --name=" + mediationEarVersion, jbossBlade));

            String recheckIsDeployed = RestCallHandler.getResultFromRest(getUpgradeProperties("upgrade.medservices.deployed.count.url"));
            logger.info("MediationService instance current value :" + recheckIsDeployed);
            // this is a hardcoded value ("1") so may need to change depending
            // on the actual deployment e.g. if its in single node or multinode
            // with multiple Med Serv instances
            return (recheckIsDeployed.equalsIgnoreCase("2"));

        } catch (Exception e) {
            logger.info("Exception Thrown during Upgrade");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return
     */
    // public boolean runOneSuccessfullTransfer() {
    // Boolean result = Boolean.FALSE;
    // RbsNodeForReWritingError myOneRbsNode = new RbsNodeForReWritingError();
    // MediationServiceOperator mediationService = new
    // MediationServiceOperator();
    // for (MediationServiceNode currentNode : myOneRbsNode.wantedNodes()) {
    // result = (mediationService.collectFiles(currentNode) &&
    // mediationService.expectedSuccessfullResultOfCollectFiles(currentNode));
    // if (result.equals(Boolean.FALSE)) {
    // logger.error("File for Node " + currentNode.getName() +
    // " could not be transferred ");
    // break;
    // }
    // }
    // return result;
    // }

    public boolean run12FtpJobs() {
        Boolean result = Boolean.FALSE;
        MediationNodeNamesDataFtpNodes myTweleveNodes = new MediationNodeNamesDataFtpNodes();
        MediationServiceOperator mediationService = new MediationServiceOperator();
        for (MediationServiceNode currentNode : myTweleveNodes.wantedNodes()) {
            result = (mediationService.collectFiles(currentNode) && mediationService.expectedSuccessfullResultOfCollectFiles(currentNode, 5000L));
            if (result.equals(Boolean.FALSE)) {
                logger.error("File for Node " + currentNode.getName() + " could not be transferred ");
                break;
            }
        }
        return result;
    }

    public List<MediationServiceNode> run5Ftp100MbJobs() {
        boolean result = Boolean.TRUE;
        MediationFiveLargeFilesData myFiveNodes = new MediationFiveLargeFilesData();
        MediationServiceOperator mediationService = new MediationServiceOperator();
        List<MediationServiceNode> collectionNodes = myFiveNodes.wantedNodes();
        for (MediationServiceNode currentNode : collectionNodes) {
            result = mediationService.collectFiles(currentNode);
            if (!result) {
                logger.error("File for Node " + currentNode.getName() + " could not be transferred");
                break;
            } else {
                logger.info("Node " + currentNode.getName() + " JobId " + currentNode.getJobId());
            }
        }
        return collectionNodes;
    }

    public List<MediationServiceNode> runAllFtpJobs() {
        MediationNodeNamesDataAllNodes myNodes = new MediationNodeNamesDataAllNodes();
        List<MediationServiceNode> collectionNodes = myNodes.wantedNodes();
        collectionNodes = runCollectFilesForJobs(collectionNodes);
        return collectionNodes;
    }

    public List<MediationServiceNode> runCollectFilesForJobs(List<MediationServiceNode> collectionNodes) {
        boolean result = Boolean.TRUE;
        MediationServiceOperator mediationService = new MediationServiceOperator();

        for (MediationServiceNode currentNode : collectionNodes) {
            result = mediationService.collectFiles(currentNode);
            if (!result) {
                logger.error("File for Node " + currentNode.getName() + " could not be transferred");
                break;
            } else {
                logger.info("Node " + currentNode.getName() + " JobId " + currentNode.getJobId());
            }
        }
        return collectionNodes;
    }

    public List<MediationServiceNode> runNumberOfFtpJobs(int numberOfJobs) {
        MediationNodeNamesData myNodes = new MediationNodeNamesData(numberOfJobs);
        List<MediationServiceNode> collectionNodes = myNodes.wantedNodes();
        collectionNodes = runCollectFilesForJobs(collectionNodes);
        return collectionNodes;
    }

    public List<MediationServiceNode> run100FtpJobs() {
        List<MediationServiceNode> collectionNodes = runNumberOfFtpJobs(100);
        return collectionNodes;
    }

    public List<MediationServiceNode> run400sftpJobs() {
        boolean result = Boolean.TRUE;
        MediationNode400Data my400Nodes = new MediationNode400Data();
        MediationServiceOperator mediationService = new MediationServiceOperator();
        List<MediationServiceNode> collectionNodes = my400Nodes.wantedNodes();

        for (MediationServiceNode currentNode : collectionNodes) {
            result = mediationService.collectFiles(currentNode);
            if (!result) {
                logger.error("File for Node " + currentNode.getName() + " could not be transferred");
                break;
            } else {
                logger.info("Node " + currentNode.getName() + " JobId " + currentNode.getJobId());
            }
        }
        return collectionNodes;
    }

    public List<MediationServiceNode> run400FtpJobs(long shortDelay) {
        boolean result = Boolean.TRUE;
        MediationNode400Data my200Nodes = new MediationNode400Data();
        MediationServiceOperator mediationService = new MediationServiceOperator();
        List<MediationServiceNode> collectionNodes = my200Nodes.wantedNodes();

        for (MediationServiceNode currentNode : collectionNodes) {
            result = mediationService.collectFiles(currentNode);
            if (!result) {
                logger.error("File for Node " + currentNode.getName() + " could not be transferred");
                break;
            } else {
                logger.info("Node " + currentNode.getName() + " JobId " + currentNode.getJobId());
            }
        }
        return collectionNodes;
    }

    public List<MediationServiceNode> run500FtpJobs() {
        List<MediationServiceNode> collectionNodes = runNumberOfFtpJobs(500);
        return collectionNodes;
    }

    public List<MediationServiceNode> run10Ftp100MbJobs() {
        MediationTenLargeFilesData myTenNodes = new MediationTenLargeFilesData();
        List<MediationServiceNode> collectionNodes = myTenNodes.wantedNodes();
        collectionNodes = runCollectFilesForJobs(collectionNodes);
        return collectionNodes;
    }

    /**
     * @param collectionNodes
     * @param shortDelay
     * @return
     */
    public boolean processSetofCompositeDeltaResults(List<MediationServiceNode> collectionNodes, long shortDelay) {

        boolean result = true;

        for (MediationServiceNode currentNode : collectionNodes) {
            boolean areFilesCollected = checkDeltaDirectory(currentNode);

            if (areFilesCollected == false) {
                logger.info("Composite delta files have failed for this node: " + currentNode);
                result = false;
            } else {
                logger.info("Composite delta files have been collected succesfully for this node: " + currentNode);
            }
        }
        // TODO Auto-generated method stub
        return result;
    }

    /**
     * @param collectionNodes
     * @param shortDelay
     * @return
     */
    public boolean processSetofCompositeCellTraceResults(List<MediationServiceNode> collectionNodes, long shortDelay) {

        boolean result = true;

        for (MediationServiceNode currentNode : collectionNodes) {
            boolean areFilesCollected = checkCellTraceDirectory(currentNode);

            if (areFilesCollected == false) {
                logger.info("Composite delta files have failed for this node: " + currentNode);
                result = false;
            } else {
                logger.info("Composite delta files have been collected succesfully for this node: " + currentNode);
            }
        }
        // TODO Auto-generated method stub
        return result;
    }

    /**
     * @param collectionNodes
     * @param shortDelay
     * @return
     */
    public boolean processSetofCompositeUeTraceResults(List<MediationServiceNode> collectionNodes, long shortDelay) {

        boolean result = true;

        for (MediationServiceNode currentNode : collectionNodes) {
            boolean areFilesCollected = checkUETraceDirectory(currentNode);

            if (areFilesCollected == false) {
                logger.info("Composite UE files have failed for this node: " + currentNode);
                result = false;
            } else {
                logger.info("Composite UE files have been collected succesfully for this node: " + currentNode);
            }
        }
        // TODO Auto-generated method stub
        return result;
    }

    public boolean processSetofResults(List<MediationServiceNode> collectionNodes, long timeOut) {

        ArrayList<FileCollectionResponse> allResponses = new ArrayList<FileCollectionResponse>();

        ArrayList<FileCollectionResponse> successResponses = new ArrayList<FileCollectionResponse>();
        ArrayList<FileCollectionResponse> unsuccessfullResponses = new ArrayList<FileCollectionResponse>();

        Integer totalResponseCounter = 0;

        for (MediationServiceNode currentNode : collectionNodes) {
            allResponses.add(getNextResponse(currentNode.getName(), currentNode.getJobId().toString(), timeOut));
            totalResponseCounter++;
        }

        Integer numberSuccessFullJobs = 0;
        Integer numberUnSuccessFullJobs = 0;
        Integer numberLostJobs = 0;
        Integer totalProcessedJobs = 0;

        logger.info("AllResponses: " + allResponses.size());
        for (Integer currentResponse = 0; currentResponse < totalResponseCounter; currentResponse++) {
            if (allResponses != null) {
                FileCollectionResponse fileCollectionResponse = allResponses.get(currentResponse);
                if (fileCollectionResponse == null) {
                    numberLostJobs++;
                } else if (fileCollectionResponse.isSuccessful() && fileCollectionResponse.getErrorCode() == -1) {
                    successResponses.add(fileCollectionResponse);
                    numberSuccessFullJobs++;
                } else {
                    unsuccessfullResponses.add(fileCollectionResponse);
                    numberUnSuccessFullJobs++;
                }
            } else {
                logger.info((" I have no valid responses to process "));
            }
        }

        logger.info("******************************************************");
        logger.info(" Test Case Results :  ");
        logger.info("******************************************************");

        logger.info("Attempted to process  " + totalResponseCounter + " Jobs  ");
        logger.info("Number of Successfull   Jobs is " + numberSuccessFullJobs);
        logger.info("Number of UnSuccessfull Jobs is " + numberUnSuccessFullJobs);
        logger.info("Number of Lost Jobs is          " + numberLostJobs);
        logger.info("******************************************************");
        totalProcessedJobs = numberSuccessFullJobs + numberUnSuccessFullJobs + numberLostJobs;

        logger.info("Total Number of         Jobs is " + totalProcessedJobs);

        logger.info(" SuccessFull List  ");
        for (FileCollectionResponse currentResponse : successResponses) {
            logger.info(currentResponse.toString());
        }

        logger.info(" UnSuccessFull List  ");
        for (FileCollectionResponse currentResponse : unsuccessfullResponses) {
            logger.info(currentResponse.toString());
        }

        return true;
    }

    public FileCollectionResponse getNextResponse(String currentNode, String currentJobId, Long timeout) {
        ObjectMessage message;
        try {
            /** TAF code */
            logger.info(" NODE   is " + currentNode);
            logger.info(" JOB id is " + currentJobId);

            MediationJmsHandler jmsResponseHandler = getJmsResponseHandler();
            message = (ObjectMessage) jmsResponseHandler.getMessageFromJmsQueue(currentJobId, timeout);

            logger.info("got message " + message);
            // System.out.println("got message " + message);

            // jmsResponseHandler.closeJmsHandler();
            return (FileCollectionResponse) message.getObject();
        } catch (Exception e) {
            logger.info("Cannot find the message!!!!!!! with content " + currentJobId);

            // TAF DEBUG CODE
            // logger.info( " stuff ");
            // for (Message msg :
            // getJmsResponseHandler().getJmsHandler().getAllMessages()){
            // logger.info("MSG: " + msg + " details");
            // try {
            // logger.info("getting message content");
            // FileCollectionResponse resp = (FileCollectionResponse)
            // ((ObjectMessage) msg).getObject();
            // logger.info("ID: " + resp.getJobId());
            // }catch (Exception err){
            // logger.info("incorrect message");
            // }
            // }
            // logger.info( " stuff undelivered");
            // for (Message msg :
            // getJmsResponseHandler().getJmsHandler().getUndeliveredMessages()){
            // logger.info("MSG: " + msg );
            // }

            e.printStackTrace();
            return null;
        }
    }

    public boolean get5Ftp100MbResults(List<MediationServiceNode> collectionNodes) {
        Boolean result = Boolean.FALSE;
        MediationServiceOperator mediationService = new MediationServiceOperator();
        for (MediationServiceNode currentNode : collectionNodes) {
            result = mediationService.expectedSuccessfullResultOfCollectFiles(currentNode, 15000L);
            if (result.equals(Boolean.FALSE)) {
                logger.error("Result for Node " + currentNode.getName() + " not found");
            }
        }
        return result;
    }

    /**
     * @return
     */
    public boolean serviceTaskStateisIdle() {
        boolean result = Boolean.FALSE;
        try {

            logger.debug("Creating hosts");
            Host litpMachine = new Host();
            List<User> users = new ArrayList<User>();
            User user1 = new User("root", getUpgradeProperties("upgrade.sc1.node.jboss1.user.root.pass"), UserType.ADMIN);
            users.add(user1);
            litpMachine.setUsers(users);
            Host jbossNodeOnLitpMachine = new Host();
            jbossNodeOnLitpMachine.setUsers(users);
            /** info for ports */
            Map<Ports, String> jbossPorts = new HashMap<Ports, String>();
            jbossPorts.put(Ports.JMX, "9999");
            jbossPorts.put(Ports.SSH, "22");

            /** info for litp host */
            litpMachine.setType(HostType.SC1);
            litpMachine.setIp(getUpgradeProperties("upgrade.sc1.node.jboss1.ip"));
            litpMachine.setPort(jbossPorts);
            litpMachine.setUser("root");
            litpMachine.setPass(getUpgradeProperties("upgrade.sc1.node.jboss1.user.root.pass"));
            litpMachine.setUser(UserType.ADMIN.toString());

            /** info for jboss */
            jbossNodeOnLitpMachine.setType(HostType.JBOSS);
            jbossNodeOnLitpMachine.setIp(getUpgradeProperties("upgrade.sc1.node.jboss1.ip"));
            jbossNodeOnLitpMachine.setPort(jbossPorts);
            jbossNodeOnLitpMachine.setUser("root");
            jbossNodeOnLitpMachine.setPass(getUpgradeProperties("upgrade.sc1.node.jboss1.user.root.pass"));

            // JbossHandler rafal = initiateHost("jmx");
            logger.debug("Creating JbossHandler");

            JbossHandler JbossHandler = new JbossHandler(jbossNodeOnLitpMachine, litpMachine);
            logger.info("JbossHandler is " + JbossHandler.getServer().toString() + "," + JbossHandler.getJbossNode().toString());

            Integer activeJobs = null;
            Integer counter = 0;
            do {
                Thread.sleep(10000);
                System.out.println();
                activeJobs = getActiveJobsCounter(JbossHandler);
                logger.info("Active jobs: " + activeJobs + "; Test Iteration Count: " + counter);
                counter++;
            } while (activeJobs > 0 && counter < 12);
            logger.info("Number of Mediation Tasks to complete:" + activeJobs);
            if (activeJobs == 0) {
                result = Boolean.TRUE;
            }
        } catch (Exception e) {
            logger.info("Exception Thrown during Mediation Tasks Checker " + e.getMessage());
            e.printStackTrace();
            return result;
        }
        logger.info("Mediation Service has no Active Tasks : " + result);
        return result;
    }

    /**
     * @param jbossBlade
     * @return
     */
    private Integer getActiveJobsCounter(JbossHandler jmxhost) {
        JmxHandler mediationJmx = jmxhost.getJmxService();
        // GroovyMBean mediationTracker = mediationJmx
        // .getMBean("com.ericsson.nms.mediation.service.tasks.impl.MediationService1:type=MediationTaskTracker");
        // com.ericsson.oss.mediation.service.tasks.impl.mediationservice
        GroovyMBean mediationTracker = mediationJmx
                .getMBean("com.ericsson.oss.mediation.service.tasks.impl.mediationservice:type=MediationTaskTracker");
        Integer activeJobs = (Integer) mediationTracker.getProperty("numberOfActiveTasks");
        return activeJobs;
    }

    public JbossHandler initiateHost(String hostType) throws Exception {

        Host litpMachine = getHost("sc1");
        Host jbossNodeOnLitpMachine = new Host();
        Host jmxNodeOnLitpMachine = new Host();

        if (hostType == "jboss") {

            /** info for jboss */
            Map<Ports, String> jbossPorts = new HashMap<Ports, String>();
            jbossPorts.put(Ports.JBOSS_MANAGEMENT, "9999");
            jbossPorts.put(Ports.SSH, "22");
            jbossNodeOnLitpMachine.setPort(jbossPorts);
            jbossNodeOnLitpMachine.setUser(getUpgradeProperties("upgrade.ms.user.name"));
            jbossNodeOnLitpMachine.setPass(getUpgradeProperties("upgrade.ms.user.password"));
            litpMachine.setPort(jbossPorts);
            return new JbossHandler(jbossNodeOnLitpMachine, litpMachine);
        } else if (hostType == "jmx") {
            Map<Ports, String> jmxPorts = new HashMap<Ports, String>();
            /** info for jmx */
            jmxPorts.put(Ports.SSH, "22");
            jmxPorts.put(Ports.JMX, "9999");
            litpMachine.setPort(jmxPorts);
            jmxNodeOnLitpMachine.setPort(jmxPorts);
            jmxNodeOnLitpMachine.setUser(getUpgradeProperties("upgrade.jboss.admin.user"));
            jmxNodeOnLitpMachine.setPass(getUpgradeProperties("upgrade.jboss.admin.password"));
            return new JbossHandler(jmxNodeOnLitpMachine, litpMachine);
        } else {
            logger.error("Jboss Contructor could not be formed correctly");
        }
        return null;
    }

    public boolean checkIsDeployed(final String deploymentName, final JbossHandler jbossBlade) {
        try {
            String mediationEar = getUpgradeProperties("upgrade.mediation.service");
            jbossBlade.getCommandService().execute("deployment-info --name=" + mediationEar);//
        } catch (Error ae) {
            logger.info("Assertion error caught");
            return false;
        }
        return (jbossBlade.getCommandService().getResponse().contains("OK"));
    }

    public String getDestDir() throws Exception {
        /**
         * Read properties from file inside jar, top level
         */
        return propertyFileLoader.getProperty("mediation.destDirectory");

    }
}
