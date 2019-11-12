package com.cloudwise.project.init;

import com.cloudwise.project.init.clean.DeleteFile;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(value = 1)
@Slf4j
public class SchedulerRunner implements CommandLineRunner {

	@Autowired
	private BauschScheduler scheduler;

	private void schedeleTask() {

//		// 采集任务
//		String collectorpackage2 = "com.cloudwise.project.init.clean";
//		// 目的是实例化com.cloudwise.project.init.clean路径下的所有类（通过反射）
//		ConfigurationBuilder configBuilder2 = new ConfigurationBuilder()
//				.filterInputsBy(new FilterBuilder().includePackage(collectorpackage2))
//				.setUrls(ClasspathHelper.forPackage(collectorpackage2))
//				.setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner(), new SubTypesScanner(false));
//		Reflections reflections2 = new Reflections(configBuilder2);
//		Set<Class<? extends BaseJob>> clazz2 = reflections2.getSubTypesOf(BaseJob.class);
//		for (Class<? extends BaseJob> clazz : clazz2) {
//
//			scheduler.collectDataByInterval(clazz, 1);
//
//		}
		//定时删除数据
		scheduler.startJobByCron(DeleteFile.class, "0 0 0 1/15 * ? ");
		
	}

	@Override
	public void run(String... paramVarArgs) throws Exception {

		try {
			scheduler.start();// 开启调度器
			schedeleTask();
		} catch (Exception e) {
			log.error("start scheduler error", e);
		}
	}
}
