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

import com.ericsson.cifwk.taf.DataProvider;
import com.ericsson.cifwk.taf.handlers.MediationTransformHandlerWrapper;

public class MediationFiveLargeFilesData extends MediationNodeNames implements DataProvider{
    
	Logger logger = Logger.getLogger(this.getClass());
	String fdnNode = "";
	public String destDir;

	public MediationFiveLargeFilesData() {
		try {
			this.destDir = getDestDir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<MediationServiceNode> wantedNodes() {
		ArrayList<MediationServiceNode> result = new ArrayList<MediationServiceNode>();
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00103"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00104"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00105"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00106"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00107"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00108"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00109"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00110"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00111"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00112"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00103"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00104"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00105"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00106"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00107"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00108"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00109"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00110"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00111"), destDir));
		result.add(new MediationServiceNode(MediationTransformHandlerWrapper
				.findTransformNodeName("LTE01ERBS00112"), destDir));
		return result;
	}

}
