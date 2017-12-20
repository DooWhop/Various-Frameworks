package com.allinpal.twodfireservice;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import com.allinpay.fmp.extintfc.sms.service.SMSSendService;
import com.thfund.commonService.file.hessian.FileService;
/**
 * spring boot 应用启动类
 * 
 * @author Shawn
 *
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.allinpal.twodfireservice.*.mapper")
@PropertySource(value = {
		"file:/app/thfd/conf/2dfire-service.properties"
		}, ignoreResourceNotFound = true)
public class TwodfireServiceApplication {
	@Value("${fileService.url}")
	private String fileServiceUrl;
	
	@Value("${smsSendService.url}")
	private String smsSendServiceUrl;
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return new org.apache.tomcat.jdbc.pool.DataSource();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/com/allinpal/twodfireservice/*/mapper/*.xml"));

		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean(value="fileService")
    public HessianProxyFactoryBean fileServiceClient() {
        HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
        factory.setServiceUrl(fileServiceUrl);
        factory.setServiceInterface(FileService.class);
        return factory;
    }
	
	@Bean(value="smsSendService")
    public HessianProxyFactoryBean smsSendServiceClient() {
        HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
        factory.setServiceUrl(smsSendServiceUrl);
        factory.setServiceInterface(SMSSendService.class);
        return factory;
    }
	// 启动的时候要注意，由于我们在controller中注入了RestTemplate，所以启动的时候需要实例化该类的一个实例
	@Autowired
	private RestTemplateBuilder builder;

	// 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
	@Bean
	public RestTemplate restTemplate() {
		return builder.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(TwodfireServiceApplication.class, args);
	}
}
