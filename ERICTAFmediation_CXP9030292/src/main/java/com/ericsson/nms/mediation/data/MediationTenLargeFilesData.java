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

import com.ericsson.cifwk.taf.handlers.MediationTransformHandlerWrapper;

public class MediationTenLargeFilesData extends MediationNodeNames {
    Logger logger = Logger.getLogger(this.getClass());

    public List<MediationServiceNode> wantedNodes() { 
        ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00103")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00104")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00105")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00106")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00107")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00103")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00111")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00112")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00113")));
        result.add(new MediationServiceNode(MediationTransformHandlerWrapper.findTransformNodeName("LTE01ERBS00114")));
        return result;
    }
}