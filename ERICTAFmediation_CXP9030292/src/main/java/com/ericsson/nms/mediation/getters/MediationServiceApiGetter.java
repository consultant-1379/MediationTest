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
package com.ericsson.nms.mediation.getters;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.ApiGetter;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.AsRmiHandler;
import com.ericsson.nms.mediation.data.PropertyFileLoader;
import com.ericsson.oss.services.pm.service.api.FileCollectionServiceRemote;

public class MediationServiceApiGetter implements ApiGetter {

    Logger logger = Logger.getLogger(this.getClass());
    private FileCollectionServiceRemote fileCollectionService;
    PropertyFileLoader propertyFileLoader = new PropertyFileLoader();
    private AsRmiHandler myEjbHandler;

    public FileCollectionServiceRemote getFileCollectionService() {
        if (fileCollectionService == null) {
            try {
                Host pmservicejboss = propertyFileLoader
                        .getHostByName("PMServ_si_0_jee_instance");
                logger.info("IP address pmservicejboss is: "
                        + pmservicejboss.getIp());
                myEjbHandler = new AsRmiHandler(pmservicejboss);

                String pmServiceVersion = propertyFileLoader
                        .getProperty("pmserviceversion");
                logger.info("pmServiceVersion  is " + pmServiceVersion);

                String ejbLookup = "pmservice/pmservice-ejb-"
                        + pmServiceVersion
                        + "/FileCollectionServiceImpl!com.ericsson.oss.services.pm.service.api.FileCollectionServiceRemote";

                logger.info("PmServiceEJB lookup is " + ejbLookup);

                fileCollectionService = (FileCollectionServiceRemote) myEjbHandler
                        .getServiceViaJndiLookup(ejbLookup);
                logger.info("PmServiceEJB FileCollectionService is  :"
                        + fileCollectionService);
                
            } catch (final Exception e) {
                logger.error(
                        "PmServiceEJB FileCollectionService cannot be found", e);
                return null;
            }
        }
        return fileCollectionService;
    }

    public Boolean closeFileCollectionService() {
        if (myEjbHandler != null) {
            myEjbHandler.close();
            return true;
        }
        else {
        	logger.debug("There was no service to close");
        	return false; 
        }
    }

}
