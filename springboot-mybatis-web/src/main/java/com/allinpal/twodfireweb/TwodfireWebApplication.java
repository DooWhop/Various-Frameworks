package com.allinpal.twodfireweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@PropertySource(value = {
		"file:/app/thfd/conf/2dfire-web.properties"
		}, ignoreResourceNotFound = true)
public class TwodfireWebApplication extends SpringBootServletInitializer {
	// 启动的时候要注意，由于我们在controller中注入了RestTemplate，所以启动的时候需要实例化该类的一个实例
		@Autowired
		private RestTemplateBuilder builder;

		// 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
		@Bean
		public RestTemplate restTemplate() {
			return builder.build();
		}

		@Override
		protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
			return application.sources(TwodfireWebApplication.class);
		}
	public static void main(String[] args) {
		SpringApplication.run(TwodfireWebApplication.class, args);
	}
}
