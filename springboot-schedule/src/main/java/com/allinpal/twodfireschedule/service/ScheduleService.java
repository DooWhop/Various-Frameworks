package com.allinpal.twodfireschedule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ScheduleService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Runnable initReq(final String url, final String method){
		
		return new Runnable() {			
			@Override
			public void run() {
				if("get".equals(method)){
					reqForGet(url);
				}else{
					reqForPost(url);
				}				
			}
		};
		
	}
	
	
	public Boolean reqForPost(String url){
		
		try {
			//MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			logger.info("-----定时任务{}开始-----", url);
			Object obj = restTemplate.postForObject(url, null, String.class);
			logger.info("-----定时任务{}结束, result:{}-----", url, obj);
			return true;
		} catch (Exception e) {		
			logger.error("-----定时任务{}发生异常-----", url);
			e.printStackTrace();			
			return false;	
		}			
	}
	
	
	public Boolean reqForGet(String url){
		
		try {
			logger.info("-----定时任务{}开始-----", url);
			Object obj = restTemplate.getForObject(url, String.class);
			logger.info("-----定时任务{}结束, result:{}-----", url, obj);
			return true;
		} catch (Exception e) {			
			logger.error("-----定时任务{}发生异常-----", url);
			e.printStackTrace();			
			return false;	
		}
	}
}
