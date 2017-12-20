package com.allinpal.twodfireweb.controller;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class DemoController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;

	@Value("${application.message:Hello World}")
	private String message = "Hello World";

	@Value("${demo.url}")
	private String demoUrl;

	@GetMapping("/")
	public String welcome(Map<String, Object> model) {
		String restResult = restTemplate.getForObject(demoUrl, String.class);
		logger.debug("request from {} result is {}",demoUrl,restResult);
		model.put("time", new Date());
//		model.put("message", this.message);
		model.put("message", restResult);
		return "welcome";
	}

	@RequestMapping("/foo")
	public String foo(Map<String, Object> model) {
		throw new RuntimeException("Foo");
	}
}
