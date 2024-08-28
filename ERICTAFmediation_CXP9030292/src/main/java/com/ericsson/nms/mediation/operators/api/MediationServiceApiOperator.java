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
package com.ericsson.nms.mediation.operators.api;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.ApiOperator;
import com.ericsson.nms.mediation.data.MediationServiceNode;
import com.ericsson.nms.mediation.getters.MediationServiceApiGetter;
import com.ericsson.oss.services.pm.service.api.FileCollectionDetails;

public class MediationServiceApiOperator implements ApiOperator {

    /**
	 * 
	 */
    MediationServiceApiGetter service; 
    Logger logger = Logger.getLogger(this.getClass());

    public MediationServiceApiOperator() {
        service = new MediationServiceApiGetter();
    }

    public boolean isFileCollectionServiceDeployed() {
        logger.info("Trying to instantiate FileCollectionService");
        return service.getFileCollectionService() != null;
    }
    
    public boolean closeFileCollectionService() {
        logger.info("Trying to close FileCollectionService");
        return service.closeFileCollectionService();
    }
    

    public boolean collectFiles(MediationServiceNode node, String jobId) {
        try {
            FileCollectionDetails jobToSubmit = node.getFileCollectionDetails(jobId, node.getTimeZone(), node.getNodeType(), null);
            service.getFileCollectionService().collectFiles(jobToSubmit);
            return true;
        } catch (Exception e) {
            logger.error("Could not submit collectFiles method  FileCollectionService");
            service = new MediationServiceApiGetter();
            collectFiles(node, jobId);
            return false;
        }
    }

    public boolean collectCompositeFiles(MediationServiceNode node, String jobId, String plugIn) {
        try {
            FileCollectionDetails jobToSubmit = node.getCompositeCollectionDetails(jobId, node.getTimeZone(), node.getNodeType(), plugIn);
            service.getFileCollectionService().collectFiles(jobToSubmit);
            return true;
        } catch (Exception e) {
            logger.error("Could not submit collectFiles method  FileCollectionService");
            service = new MediationServiceApiGetter();
            collectCompositeFiles(node, jobId, plugIn);
            return false;
        }
    }

    public boolean collectCompositeFilesCellTrace(MediationServiceNode node, String jobId, String plugIn) {
        try {
            FileCollectionDetails jobToSubmit = node.getCompositeCellTraceCollectionDetails(jobId, node.getTimeZone(), node.getNodeType(), plugIn);
            service.getFileCollectionService().collectFiles(jobToSubmit);
            return true;
        } catch (Exception e) {
            logger.error("Could not submit collectFiles method  FileCollectionService");
            service = new MediationServiceApiGetter();
            collectCompositeFilesCellTrace(node, jobId, plugIn);
            return false;
        }
    }

    public boolean collectCompositeFilesUeTrace(MediationServiceNode node, String jobId, String plugIn) {
        try {
            FileCollectionDetails jobToSubmit = node.getCompositeUETraceCollectionDetails(node.getUETraceSourceFile().toString(), jobId, node.getTimeZone(),
                    node.getNodeType(), plugIn);
            logger.info("Here is jobToSubmit object: " + jobToSubmit);
            logger.info("Here is service object: " + service);
            logger.info("Here is service.getFileCollectionService( object: " + service.getFileCollectionService());
            service.getFileCollectionService().collectFiles(jobToSubmit);
            return true;
        } catch (Exception e) {
            logger.error("Could not submit collectFiles method  FileCollectionService");
            service = new MediationServiceApiGetter();
            collectCompositeFilesUeTrace(node, jobId, plugIn);
            return false;
        }
    }
}