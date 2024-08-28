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

import java.util.HashMap;

import com.ericsson.nms.mediation.data.MediationTransformHandler;

public class MediationTransformHandlerWrapper {
    //    private static final Logger logger = Logger.getLogger(MediationTransformHandlerWrapper.class);
    static MediationTransformHandler mth = new MediationTransformHandler();
    static HashMap<String, String> mapOfTransformsToNodeNames = new HashMap<String, String>(2000);

    public static String findTransformNodeName(String nodeName) {
        String transformedNodeName = "";
        HashMap<String, String> mapOfTransformsToNodeNames = getHashMapOfTransforms();
        if (mapOfTransformsToNodeNames.containsKey(nodeName)) {
            transformedNodeName = mapOfTransformsToNodeNames.get(nodeName);
        } else {
            transformedNodeName = mth.transformNodeName(nodeName);
            mapOfTransformsToNodeNames.put(nodeName, transformedNodeName);
        }
        return transformedNodeName;
    }

    public static HashMap<String, String> getHashMapOfTransforms() {
        return mapOfTransformsToNodeNames;
    }

}
