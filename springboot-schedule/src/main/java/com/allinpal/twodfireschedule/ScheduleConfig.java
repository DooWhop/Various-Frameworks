package com.allinpal.twodfireschedule;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.allinpal.twodfireschedule.service.ScheduleService;
import com.allinpal.twodfireschedule.util.PropertiesUtil;

@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {
	
	@Autowired
	ScheduleService scheduleService;
	
	@Autowired
	PropertiesUtil propertiesUtil;
	
	@Value("${schedule.threadpool.size}")
	private int size;
	
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {      
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(size));
        List<Map<String, String>> list = propertiesUtil.readProperties();
        for (Map<String, String> map : list) {
        	 taskRegistrar.addCronTask(scheduleService.initReq(map.get("url"), map.get("method")), map.get("cron"));
		}         
    }
}