package com.cloudwise.project.conf;

import com.cloudwise.project.init.AutowiringSpringBeanJobFactory;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wayne
 */
@Configuration
public class SchedulerConfig {

	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();

		jobFactory.setApplicationContext(applicationContext);

		return jobFactory;
	}


	@Bean(name = "schedulerFactoryBean")
	public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) throws IOException {

		// 定义属性文件
		Properties quartzProperties = new Properties();
		FileInputStream in = new FileInputStream("conf/quartz.properties");
		quartzProperties.load(in);
		in.close();

		SchedulerFactoryBean factory = new SchedulerFactoryBean();

		//设置quartz配置文件
		factory.setQuartzProperties(quartzProperties);
		
		factory.setJobFactory(jobFactory);

		return factory;

	}

	@Bean(name = "scheduler")
	public Scheduler createScheduler(SchedulerFactoryBean factory) throws IOException {
		Scheduler scheduler = factory.getScheduler();

		return scheduler;
	}

}