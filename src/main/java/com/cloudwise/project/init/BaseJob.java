package com.cloudwise.project.init;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class BaseJob implements Job {
	
	public abstract void collectorData();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		collectorData();
	}

}
