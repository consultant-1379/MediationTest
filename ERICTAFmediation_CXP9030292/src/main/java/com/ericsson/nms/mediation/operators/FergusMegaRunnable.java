package com.ericsson.nms.mediation.operators;

import java.util.List;
import java.util.concurrent.Callable;

import com.ericsson.nms.mediation.data.MediationServiceNode;

public class FergusMegaRunnable implements Callable<Boolean> {

    MediationServiceOperator apiOperator = new MediationServiceOperator();

    @Override
    public Boolean call() throws Exception { 

    List<MediationServiceNode> collectionNodes = apiOperator.run400FtpJobs(2000L);
    return apiOperator.processSetofResults(collectionNodes, 2000L);
        
    }

}
