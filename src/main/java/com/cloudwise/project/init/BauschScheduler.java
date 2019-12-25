//package com.cloudwise.project.init;
//
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//
//@Service
//@Slf4j
//public class BauschScheduler {
//
//	private final String collectorJobGroup = "collectorJobGroup";
//	private final String collectorTriggerGroup = "collectorTriggerGroup";
//	@Autowired
//	@Qualifier("scheduler")
//	private Scheduler scheduler;
//
//	public synchronized void start() throws SchedulerException, IOException {
//		log.info("quartz schedule is starting...");
//		if (!scheduler.isStarted()) {
//			scheduler.start();
//		}
//		log.info("quartz schedule has started...");
//	}
//	public void removeTrigger(String name) {
//		try {
//				removeTrigger(name, collectorTriggerGroup);
//		} catch (SchedulerException e) {
//			log.error("removeTrigger error. trigger=" + name, e);
//		}
//	}
//
//	private void removeTrigger(String name, String group)
//			throws SchedulerException {
//		TriggerKey triggerKey = new TriggerKey(name, group);
//		boolean istriggerExists = scheduler.checkExists(triggerKey);
//		if (istriggerExists) {
//			scheduler.resumeTrigger(triggerKey);// 停止触发器
//			scheduler.unscheduleJob(triggerKey);// 移除触发器
//		}
//	}
//	public void addTrigger(String name, JobDetail collectorjobDetail,int intervaleMin) {
//		try {
//
//			addTrigger(collectorTriggerGroup, collectorjobDetail, name, intervaleMin);
//		} catch (SchedulerException e) {
//			log.error("addTrigger error. trigger=" + name, e);
//		}
//	}
//
//	private void addTrigger(String group, JobDetail jobDetail, String name,
//			int intervaleMin) throws SchedulerException {
//		TriggerKey triggerKey = new TriggerKey(name, group);
//		boolean istriggerExists = scheduler.checkExists(triggerKey);
//		if (istriggerExists) {
//			removeTrigger(name, group);
//		}
//		Trigger trigger = TriggerBuilder.newTrigger()
//				.withIdentity(triggerKey)
//				.withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(intervaleMin)).startNow()
//				.forJob(jobDetail).build();
//		scheduler.scheduleJob(trigger);
//	}
//
//	public void startCollectorJob(Class<? extends BaseJob> clszz,int intervalMin){
//		try {
//			JobDetail collectorjobDetail = JobBuilder.newJob(clszz)
//					.withIdentity(new JobKey(clszz.getSimpleName(), collectorJobGroup)).storeDurably().build();
//			scheduler.addJob(collectorjobDetail, true);
//			addTrigger(clszz.getSimpleName(),collectorjobDetail, intervalMin);
//		} catch (SchedulerException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void startCollectorJob_Single(Class<? extends BaseJob> clszz,int intervalMin,String name){
//		try {
//			JobDetail collectorjobDetail = JobBuilder.newJob(clszz)
//					.withIdentity(new JobKey(name, collectorJobGroup)).storeDurably().build();
//			scheduler.addJob(collectorjobDetail, true);
//			addTrigger(name,collectorjobDetail, intervalMin);
//		} catch (SchedulerException e) {
//			e.printStackTrace();
//		}
//	}
//
//
//	public void collectDataByInterval(Class<? extends BaseJob> clszz,int intervalMin){
//		try {
//			JobDetail senddatajobDetail = JobBuilder.newJob(clszz)
//					.withIdentity(new JobKey(clszz.getName(), collectorJobGroup)).storeDurably().build();
//
//			scheduler.addJob(senddatajobDetail, true);
//			addTrigger(clszz.getName(),senddatajobDetail, intervalMin);
//		} catch (SchedulerException e) {
//			log.error("start send data job error,jobname:{}",clszz.getName());
//			log.error("start send data job error",e);
//			e.printStackTrace();
//		}
//	}
//
//
//
//	//Cron表达式设置调度时间的Trigger
//	public void addTriggerByCron(String name, JobDetail collectorjobDetail,String cron) {
//		try {
//
//			addTriggerByCron(collectorTriggerGroup, collectorjobDetail, name, cron);
//		} catch (SchedulerException e) {
//			log.error("addTrigger error. trigger=" + name, e);
//		}
//	}
//
//	private void addTriggerByCron(String group, JobDetail jobDetail, String name,
//			String corn) throws SchedulerException {
//		TriggerKey triggerKey = new TriggerKey(name, group);
//		boolean istriggerExists = scheduler.checkExists(triggerKey);
//		if (istriggerExists) {
//			removeTrigger(name, group);
//		}
//		Trigger trigger = TriggerBuilder.newTrigger()
//				.withIdentity(triggerKey)
//				.withSchedule(CronScheduleBuilder.cronSchedule(corn)).startNow()
//				.forJob(jobDetail).build();
//		scheduler.scheduleJob(trigger);
//	}
//
//	public void startJobByCron(Class<? extends BaseJob> clszz,String cron){
//		try {
//			JobDetail collectorjobDetail = JobBuilder.newJob(clszz)
//					.withIdentity(new JobKey(clszz.getSimpleName(), collectorJobGroup)).storeDurably().build();
//			scheduler.addJob(collectorjobDetail, true);
//			addTriggerByCron(clszz.getSimpleName(),collectorjobDetail, cron);
//		} catch (SchedulerException e) {
//			e.printStackTrace();
//		}
//	}
//}
