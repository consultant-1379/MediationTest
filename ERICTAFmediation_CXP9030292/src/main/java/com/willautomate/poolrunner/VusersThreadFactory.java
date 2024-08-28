package com.willautomate.poolrunner;

import java.util.List;
import java.util.concurrent.ThreadFactory;

public class VusersThreadFactory implements ThreadFactory{

	public int vusers = 5;
	private int threadRegistry = 0;
	public Thread newThread(Runnable arg0) {
		Thread result = null;
		if (threadRegistry < vusers){
			result = new Thread(arg0,"vuser-"+threadRegistry);
			threadRegistry++;
		}
		return result;
	}
	public VusersThreadFactory(int vusers) {
		this.vusers = vusers;
	}
}
