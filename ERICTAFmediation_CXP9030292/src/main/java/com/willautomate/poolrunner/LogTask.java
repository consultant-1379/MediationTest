package com.willautomate.poolrunner;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

public class LogTask implements Callable{

	private int i ;
	private Logger logger = Logger.getLogger(this.getClass());
	public Object call() throws Exception {
		logger.info("Running " + i);
		Thread.sleep(1000);
		logger.info("Finidhed " + i);
		return null;
	}

	
	LogTask(int i){
		this.i = i;
	}
	
}
