package com.allinpal.twodfireservice.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.demo.domain.City;
import com.allinpal.twodfireservice.demo.mapper.DemoMapper;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;

@Service("demoService")
// @Transactional(propagation = Propagation.REQUIRED,isolation =
// Isolation.DEFAULT,timeout=36000,readOnly=false,rollbackFor=RuntimeException.class)
public class DemoService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	public DemoMapper demoMapper;

	@Autowired
	public AppConfig appConfig;

	public City getCityById(String id) {
		logger.info("this service in on {}", appConfig.getAppIp());
		logger.info("generateId {}", GenerateIdsUtil.generateId(appConfig.getAppIp()));
		return demoMapper.findByState(id);
	}

	// 方法的@Transactional会覆盖类上面声明的事务;rollbackFor可定义异常回滚类型,默认是RuntimeException或者Error
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveCity(City city) {
		int cnt = demoMapper.addCity(city);
		throw new RuntimeException("插入数据(cnt: " + cnt + ")将回滚!");
	}
}
