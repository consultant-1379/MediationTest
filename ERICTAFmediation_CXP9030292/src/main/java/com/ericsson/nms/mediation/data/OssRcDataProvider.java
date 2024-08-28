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

import com.ericsson.cifwk.taf.DataProvider;
import com.ericsson.cifwk.taf.handlers.OssrcPmProxy;

public class OssRcDataProvider implements DataProvider{

    OssrcPmProxy oss = new OssrcPmProxy();

    public String getNextJobId() {
        return oss.provideFileCollectionJobId();
    }
}
