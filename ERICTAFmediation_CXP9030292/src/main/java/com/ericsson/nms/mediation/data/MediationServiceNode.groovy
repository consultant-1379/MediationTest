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
package com.ericsson.nms.mediation.data

import org.apache.log4j.Logger

import com.ericsson.cifwk.taf.DataProvider
import com.ericsson.cifwk.taf.handlers.*
import com.ericsson.oss.services.pm.service.api.FileCollectionDetails
import com.ericsson.oss.services.pm.service.api.SingleFileTransferDetails

class MediationServiceNode implements DataProvider{

	String templateDirectory
	String templateFileContents
	String templateFileType
	String templateDestinationFile
	String sourcePassword
	String sourceDir
	String jobId
	String name
	String baseName

	Integer errorCode
	String errorDescription

	String timeZone
	String timeStamp

	short nodeType
	boolean decompressionRequired

	Logger logger = Logger.getLogger(this.class)

	MediationServiceNode(String name, String templateDirectory="/cluster/collect_files/",String templateFileContents = " counters",String templateFileType = ".txt",String templateDestinationFile = ".txt",String sourcePassword = "secret",String sourceDir = "ftp/", Integer errorCode = 0, String errorDescription = "", String timeStamp = (new Date()).time.toString(), String timeZone = "GMT", short nodeType = 1, boolean decompressionRequired = false ){
		this.name = name
		if(name.contains("=")){
			this.baseName = name.substring(name.lastIndexOf('=') + 1)
		}else{
			this.baseName = name;
		}
		this.jobId=  new OssRcDataProvider().getNextJobId()
		this.templateDirectory = templateDirectory
		this.templateDestinationFile = templateDestinationFile
		this.templateFileType = templateFileType
		this.sourceDir = sourceDir
		this.sourcePassword = sourcePassword
		this.templateFileContents = templateFileContents
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.timeZone = timeZone;
		this.nodeType = nodeType;
		this.decompressionRequired = decompressionRequired;
		//                def date= new Date()
		this.timeStamp = timeStamp;

		logger.debug "Node created: $this"


	}

	public getJobId(){
		return jobId.toString()
	}

	public getDeltaJobId() {
		String deltaJobId=this.name.toString()+ "::DELTA_SYNC::1::" + getTimeStamp();
		//String deltaJobId=this.name.toString()+"::DELTA_SYNC::1::"; // invalid job id
		this.jobId=deltaJobId
		return deltaJobId
	}


	public getInvalidDeltaJobId(String invalidJobId){
		return this.name.toString()+ invalidJobId
	}

	public setTimeStamp(String timesstamp){
		this.timeStamp = timesstamp
	}


	public getDeltaPlugin() {
		return "com.ericsson.nms.umts.ranos.pms.collectionplugins.deltasync.DeltaSyncJobPlugin"
	}


	public getCellTracePlugin() {
		return "com.ericsson.nms.umts.ranos.pms.collectionplugins.celltrace.CellTraceJobPlugin"
	}

        public getUETracePlugin() {
        return "com.ericsson.nms.umts.ranos.pms.collectionplugins.uetrace.UeTraceJobPlugin"
        }
	public getCellTraceJobId() {
		String cellTraceJobId=this.name.toString()+"::CELL_TRACE::2::1361961900000";
		this.jobId=cellTraceJobId
		return cellTraceJobId
	}

        public getUETraceJobId() {
                String cellTraceJobId=this.name.toString()+"::UE_TRACE::1::1361961900000";
                this.jobId=cellTraceJobId
                return cellTraceJobId
        }

        public getUETraceSourceFile() {
                return "UeTraceFilesLocation"
        }

	public getSourceFile(){
		return baseName + templateFileType
	}



	public getSourceContents(){
		return baseName + templateFileContents
	}

	public getDestinationDir(){
		return templateDirectory + baseName
	}

	public getDestinationFile(){
		return timeStamp + baseName + templateDestinationFile
	}
	public FileCollectionDetails getFileCollectionDetails(String jobId, String ftpDir=this.sourceDir, String timeZone, Short nodeType, String plugin){

		final List<SingleFileTransferDetails> files = []
		logger.info ( "src  "+sourceFile+" ftpDir "+ ftpDir + "dest file" + destinationFile + " dest dir "+destinationDir+ " job "+jobId+" timezone "+ timeZone + " nodeType "+ nodeType )
		files << new SingleFileTransferDetails(sourceFile, ftpDir, destinationFile, destinationDir, jobId, decompressionRequired, timeZone, nodeType, plugin )

		logger.debug "Transfer details prepared ${files.dump()}"
		return new FileCollectionDetails(name,files)
	}


	public FileCollectionDetails getCompositeCollectionDetails(String jobId, String ftpDir=this.sourceDir, String timeZone, Short nodeType, String plugin){

		final List<SingleFileTransferDetails> files = []
		logger.info ( " ftpDir "+ ftpDir + " dest dir "+destinationDir+ " job "+jobId+" timezone "+ timeZone + " nodeType "+ nodeType + " plugin "+ plugin )
		files << new SingleFileTransferDetails("", ftpDir, "", destinationDir, jobId, decompressionRequired, timeZone, nodeType, plugin )

		logger.debug "Transfer details prepared ${files.dump()}"
		return new FileCollectionDetails(name,files)
	}


	public FileCollectionDetails getCompositeCellTraceCollectionDetails(String jobId, String ftpDir=this.sourceDir, String timeZone, Short nodeType, String plugin){

		final List<SingleFileTransferDetails> files = []
		logger.info ( "CellTraceFilesLocation" + " ftpDir "+ ftpDir + " dest dir "+destinationDir+ " job "+jobId + " timezone " + timeZone + " nodeType "+ nodeType + " plugin "+ plugin )
		files << new SingleFileTransferDetails("CellTraceFilesLocation", ftpDir, "A20130227.1030-1045_SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="+baseName+"_CellTraceFilesLocation", destinationDir, jobId, decompressionRequired, timeZone, nodeType, plugin )

		logger.debug "Transfer details prepared ${files.dump()}"
		return new FileCollectionDetails(name,files)
	}

        public FileCollectionDetails getCompositeUETraceCollectionDetails(String srcFile, String jobId, String ftpDir=this.sourceDir, String timeZone, Short nodeType, String plugin){

                 final List<SingleFileTransferDetails> files = []
                 logger.info ( "src " + srcFile + " ftpDir "+ ftpDir + " dest dir "+destinationDir+ " job "+jobId+" timezone "+ timeZone + " nodeType "+ nodeType + " plugin "+ plugin )
                 files << new SingleFileTransferDetails(srcFile, ftpDir, "A20130227.1030-1045_SubNetwork=ONRM_ROOT_MO,SubNetwork=FDDGroup,MeContext="+baseName+"_UeTraceFilesLocation", destinationDir, jobId, decompressionRequired, timeZone, nodeType, plugin )
                 logger.debug "Transfer details prepared ${files.dump()}"
                 return new FileCollectionDetails(name,files)
        }

	String toString(){
		return "name:$name templateDirectory:$templateDirectory templateFileType:$templateFileType templateDestinationFile:$templateDestinationFile sourceDir:$sourceDir sourcePassword:$sourcePassword"
	}
}
