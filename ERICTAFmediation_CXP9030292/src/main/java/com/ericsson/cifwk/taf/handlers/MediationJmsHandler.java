package com.ericsson.cifwk.taf.handlers;

import java.io.InputStream;
import java.util.Properties;

import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;

public class MediationJmsHandler {

    private static final Logger logger = Logger.getLogger(MediationJmsHandler.class);
    public static JmsHandler jmsHandler;
    Session session = null;
    private static String destinationName;
    private static int number_of_jmsHandlers = 0;

    private static Host myHost;

    public MediationJmsHandler() {
        readQueueName();
        logger.info("Destination "+destinationName.toString()); 
        myHost = DataHandler.getHostByName("PMServ_si_0_jee_instance");
    }

    public static JmsHandler getJmsHandler() {
        if (jmsHandler == null) {
            try {
                logger.info("JMSHandler does not exist, creating a new one!");
                logger.debug("Just checking again, host: " + myHost.getIp());
                logger.info("Using host:" + myHost + " and dest:" + destinationName);
                jmsHandler = new JmsHandler(myHost, destinationName, false, Session.AUTO_ACKNOWLEDGE, true, false, 2000L);
                logger.debug("Number of JMSHandler instances created: " + ++number_of_jmsHandlers);
            } catch (Exception e) {
                logger.error("Cannot instantiate jms handler", e);
            }
        } else {
            logger.info("JMSHandler already exists, return this one!");
            logger.debug("Number of JMSHandler instances created: " + number_of_jmsHandlers);
        }
        logger.info("Returning jmsHandler" + jmsHandler);
        return jmsHandler;
    }

    private void readQueueName() {
        try {
            final Properties jmsProperties = new Properties();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("mediation.properties");
            jmsProperties.load(is);
            destinationName = jmsProperties.getProperty("jms.response.queue.name");
            is.close();
        } catch (Exception e) {
            logger.error("Initial data fail" + e.getMessage());
        }

    }

    public Message getMessageFromJmsQueue(final String jobId, final Long timeOut) throws Exception {
        Message response = null;
        try {
            JmsHandler myJmsHandler = getJmsHandler();
            logger.info("getMessageFromJmsQueue: Here is the value of the job id we are looking for " + jobId);
            logger.debug("getMessageFromJmsQueue: Here is the value of my jms handler " + myJmsHandler.toString());
            logger.debug("Here are all of the messages on the queue: " + myJmsHandler.getAllMessages());
            response = myJmsHandler.getMessageFromJms(jobId, timeOut);

            return response;
        } catch (final Exception e) {
            throw e;
        }
    }

    public void closeJmsHandler() throws Exception {
        try {
            JmsHandler myJmsHandler = getJmsHandler();
            myJmsHandler.getUndeliveredMessages().clear();
            logger.debug("Here are all of the messages on the queue: " + myJmsHandler.getAllMessages());
            myJmsHandler.close();
            logger.info("JMSHandler has been closed. Value of myJmsHandler: " + myJmsHandler);

        } catch (final Exception e) {
            throw e;
        }
    }
}
