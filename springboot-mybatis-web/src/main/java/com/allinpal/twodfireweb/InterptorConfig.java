package com.allinpal.twodfireweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.allinpal.twodfireweb.interceptor.SessionInterptor;

@Configuration
@EnableWebMvc
public class InterptorConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private SessionInterptor sessionInterptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册监控拦截器 
	    registry.addInterceptor(sessionInterptor) 
	        .addPathPatterns("/**") 
	        .excludePathPatterns("/home")
	        .excludePathPatterns("/getCaptcha")
	        .excludePathPatterns("/userValidate")
	        .excludePathPatterns("/showProtocol"); 
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("favicon.ico").addResourceLocations("classpath:/static/favicon.ico");
		registry.addResourceHandler("css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("vendor/**").addResourceLocations("classpath:/static/vendor/");
		registry.addResourceHandler("images/**").addResourceLocations("classpath:/static/images/");
		registry.addResourceHandler("js/**").addResourceLocations("classpath:/static/js/");
		registry.addResourceHandler("fonts/**").addResourceLocations("classpath:/static/fonts/");
		registry.addResourceHandler("pdfjs/**").addResourceLocations("classpath:/static/pdfjs/");
	}	
	
}
