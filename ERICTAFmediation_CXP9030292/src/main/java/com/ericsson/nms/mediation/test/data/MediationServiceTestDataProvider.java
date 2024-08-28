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
package com.ericsson.nms.mediation.test.data;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;

import com.ericsson.cifwk.taf.TestData;
import com.ericsson.nms.mediation.data.*;

/**
 * //TODO Add class javadoc
 */
public class MediationServiceTestDataProvider implements TestData { 

    @DataProvider(name = "MediationServiceTestData")
    public static Object[][] mediationServiceTestDataBuilder() {
        MediationNodeNamesData nodeData = new MediationNodeNamesData();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationIntegrationData")
    public static Object[][] mediationIntegrationDataBuilder() {
        MediationIntegration nodeData = new MediationIntegration();
        return (testDataLoader(nodeData));
    }
    
    @DataProvider(name = "MediationIPV6Data")
    public static Object[][] mediationIPV6builder() {
        MediationIPV6Nodes nodeData = new MediationIPV6Nodes();
        return (testDataLoader(nodeData));
    }
    
    @DataProvider(name = "MediationServiceTestDataAllNodes")
    public static Object[][] mediationServiceTestDataBuilderAllNodes() {
        MediationNodeNamesDataAllNodes nodeData = new MediationNodeNamesDataAllNodes();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataFtpNodes")
    public static Object[][] mediationServiceTestDataBuilderFtpNodes() {
        MediationNodeNamesDataFtpNodes nodeData = new MediationNodeNamesDataFtpNodes();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataDeltaComposite")
    public static Object[][] mediationServiceTestDataBuilderCompositeNodes() {
        MediationServiceTestDataDeltaComposite nodeData = new MediationServiceTestDataDeltaComposite("ftp/");
        nodeData.setTimeStamp("1361368800000"); // Default time of 20/Feb/2013
                                                // 14:00 GMT for composite jobs
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataDeltaComposite2Nodes")
    public static Object[][] mediationServiceTestDataBuilderComposite2Nodes() {
        MediationServiceTestDataDeltaCompositeMultipleNodes nodeData = new MediationServiceTestDataDeltaCompositeMultipleNodes(2, "ftp/");
        nodeData.setTimeStamp("1361368800000"); // Default time of 20/Feb/2013
                                                // 14:00 GMT for composite jobs
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataDeltaComposite50Nodes")
    public static Object[][] mediationServiceTestDataBuilderComposite50Nodes() {
        MediationServiceTestDataDeltaCompositeMultipleNodes nodeData = new MediationServiceTestDataDeltaCompositeMultipleNodes(50, "ftp/");
        nodeData.setTimeStamp("1361368800000"); // Default time of 20/Feb/2013
                                                // 14:00 GMT for composite jobs
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataDeltaCompositeWithFutureTimeStamp")
    public static Object[][] mediationServiceTestDataBuilderCompositeWithFutureTimeStamp() {
        MediationServiceTestDataDeltaComposite nodeData = new MediationServiceTestDataDeltaComposite("ftp/");
        nodeData.setTimeStamp("1363752000000"); // 20/03/2013 04:00 GMT -expect
                                                // 0 files

        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataDeltaCompositeWithHalfExpiredTimeStamp")
    public static Object[][] mediationServiceTestDataBuilderCompositeWithHalfExpiredTimeStamp() {
        MediationServiceTestDataDeltaComposite nodeData = new MediationServiceTestDataDeltaComposite("ftp/");
        nodeData.setTimeStamp("1361455200000"); // 21/02/2013 14:00 -expect 41
                                                // files to be collected
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataDeltaCompositeWithAlreadyExpiredTimeStamp")
    public static Object[][] mediationServiceTestDataBuilderCompositeWithAlreadyExpiredTimeStamp() {
        MediationServiceTestDataDeltaComposite nodeData = new MediationServiceTestDataDeltaComposite("ftp/");
        nodeData.setTimeStamp("1358690400000");
        // 20/01/2013 14:00 GMT -expect 96
        // files to be collected, might be a bug these files will not exist
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataDeltaCompositeWithInvalidJobIds")
    public static Object[][] mediationServiceTestDataBuilderCompositeWithInvalidFormatJobId() {
        MediationServiceTestDataDeltaComposite nodeData = new MediationServiceTestDataDeltaComposite("ftp/");
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataCellTraceComposite")
    public static Object[][] mediationServiceTestDataBuilderCompositeNodesCellTrace() {
        MediationServiceTestDataDeltaComposite nodeData = new MediationServiceTestDataDeltaComposite("/c/pm_data/");
        nodeData.setTimeStamp("1361961900000"); // Default time of 20/Feb/2013
                                                // 14:00 GMT for composite jobs
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataCellTraceComposite50Nodes")
    public static Object[][] mediationServiceTestDataBuilderCompositeNodesCellTrace50Nodes() {
        MediationServiceTestDataDeltaCompositeMultipleNodes nodeData = new MediationServiceTestDataDeltaCompositeMultipleNodes(50, "/c/pm_data/");
        nodeData.setTimeStamp("1361961900000"); // Default time of 20/Feb/2013
                                                // 14:00 GMT for composite jobs
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataUETrace")
    public static Object[][] mediationServiceTestDataBuilderCompositeNodesUeTrace() {
        MediationServiceTestDataDeltaComposite nodeData = new MediationServiceTestDataDeltaComposite("/c/pm_data/");
        nodeData.setTimeStamp("1361961900000"); // Default time of 20/Feb/2013
                                                // 14:00 GMT for composite jobs
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataCompositeUeTrace50Nodes")
    public static Object[][] mediationServiceTestDataBuilderCompositeNodesUeTraceMultiNode() {
        MediationServiceTestDataDeltaCompositeMultipleNodes nodeData = new MediationServiceTestDataDeltaCompositeMultipleNodes(50, "/c/pm_data/");
        nodeData.setTimeStamp("1361961900000"); // Default time of 20/Feb/2013
                                                // 14:00 GMT for composite jobs
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceGzipDataProvider")
    public static Object[][] mediationServiceTestDataGzipFileTransfer() {
        MediationGzipDataProvider nodeData = new MediationGzipDataProvider();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceUnSupportedFileFormatGzipDataProvider")
    public static Object[][] mediationServiceGzipUnsupportedFileFormatTestData() {
        MediationGzipUnSupportedFileFormatDataProvider nodeData = new MediationGzipUnSupportedFileFormatDataProvider();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataFiveFtpNodes")
    public static Object[][] mediationServiceTestDataBuilderFiveFtpNodes() {
        MediationNodeNamesDataFtpNodes nodeData = new MediationNodeNamesDataFtpNodes();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestDataSftpNodes")
    public static Object[][] mediationServiceTestDataBuilderSftpNodes() {
        MediationNodeNamesDataSftpNodes nodeData = new MediationNodeNamesDataSftpNodes();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationSameNodeDifferentDirectories")
    public static Object[][] mediationServiceTestDataBuildDirs() {
        MediationSameNodeDifferentDirectories nodeData = new MediationSameNodeDifferentDirectories();
        return (testDataLoader(nodeData));
    }

    // @DataProvider(name = "MediationUnsuccessfulRead")
    // public static Object[][] mediationServiceTestDataBuildUnsuccesful() {
    // MediationUnsuccessfulReadData nodeData = new
    // MediationUnsuccessfulReadData();
    // return (testDataLoader(nodeData));
    // }

    @DataProvider(name = "MediationUnsuccessfulReadSFTP")
    public static Object[][] mediationServiceTestDataBuildUnsuccesfulSFTP() {
        MediationUnsuccessfulReadDataSFTP nodeData = new MediationUnsuccessfulReadDataSFTP();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationUnsuccessfulReadFTP")
    public static Object[][] mediationServiceTestDataBuildUnsuccesfulFTP() {
        MediationUnsuccessfulReadDataFTP nodeData = new MediationUnsuccessfulReadDataFTP();
        return (testDataLoader(nodeData));
    }

    // @DataProvider(name = "MediationUnsuccessfulWrite")
    // public static Object[][] mediationServiceTestDataWriteUnsuccesful() {
    // MediationUnsuccessfulWriteData nodeData = new
    // MediationUnsuccessfulWriteData();
    // return (testDataLoader(nodeData));
    //
    // }

    @DataProvider(name = "MediationUnsuccessfulWriteFTP")
    public static Object[][] mediationServiceTestDataWriteUnsuccesfulFTP() {
        MediationUnsuccessfulWriteDataFTP nodeData = new MediationUnsuccessfulWriteDataFTP();
        return (testDataLoader(nodeData));

    }

    @DataProvider(name = "MediationUnsuccessfulWriteSFTP")
    public static Object[][] mediationServiceTestDataWriteUnsuccesfulSFTP() {
        MediationUnsuccessfulWriteDataSFTP nodeData = new MediationUnsuccessfulWriteDataSFTP();
        return (testDataLoader(nodeData));

    }

    @DataProvider(name = "MediationUnsuccessfulReWrite")
    public static Object[][] mediationServiceTestDataReWriteUnsuccesful() {
        MediationUnsuccessfulReWriteData nodeData = new MediationUnsuccessfulReWriteData();
        return (testDataLoader(nodeData));

    }

    @DataProvider(name = "MediationFtpConnectionDropped")
    public static Object[][] mediationServiceTestDataFtpConnectionDropped() {
        MediationFtpTransferException nodeData = new MediationFtpTransferException();
        return (testDataLoader(nodeData));

    }

    @DataProvider(name = "MediationServiceTestDataLargeFiles")
    public static Object[][] mediationServiceTestDataLargeFiles() {

        MediationLargeFilesData nodeData = new MediationLargeFilesData();
        return (testDataLoader(nodeData));

    }

    @DataProvider(name = "MediationConnectionDropped")
    public static Object[][] MediationConnectionDropped() {

        MediationFtpTransferException nodeData = new MediationFtpTransferException();
        return (testDataLoader(nodeData));
    }

    @DataProvider(name = "MediationServiceTestBuilderNodeInfo")
    public static Object[][] MediationServiceTestBuilderNodeInfo() {
        MediationNodeNamesDataFtpNodes nodeData = new MediationNodeNamesDataFtpNodes();
        return (testDataLoader(nodeData));
    }

    /** testDataLoader */
    public static Object[][] testDataLoader(MediationNodeNames nodeData) {

        List<Object[]> testData = new ArrayList<Object[]>();
        for (MediationServiceNode currentNode : nodeData.wantedNodes()) {
            Object[] testDataEntry = new Object[1];
            testDataEntry[0] = currentNode;
            testData.add(testDataEntry);
        }
        return testData.toArray(new Object[testData.size()][1]);
    }
}