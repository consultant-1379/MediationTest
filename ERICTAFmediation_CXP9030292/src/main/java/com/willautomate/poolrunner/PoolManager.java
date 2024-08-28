package com.willautomate.poolrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public class PoolManager {

	private ExecutorService pool;
	public ExecutorService getPool(int vusers){
		if (pool==null)
			pool = Executors.newCachedThreadPool(new VusersThreadFactory(vusers));
		return pool;
	}
	private void clearPool(){
		pool = null;
	}
	private List<Future> currentVusers;
	private List<Future> getCurrentVusers(){
		if (currentVusers == null)
			currentVusers = new ArrayList<Future>();
		return currentVusers;
	}
	public void executeWithVusers(List<Callable> tasks, int vusers){
		while (! tasks.isEmpty()){ 
		try {
			Callable task = tasks.get(0);
			getCurrentVusers().add(getPool(vusers).submit(task));
			tasks.remove(0);
		} catch (RejectedExecutionException allVusersOccupied){
			try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
	}
		
		for (Future runTask : getCurrentVusers()){
			try {
				runTask.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		pool.shutdown();
		clearPool();
	}
}
