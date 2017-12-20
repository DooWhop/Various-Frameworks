package com.allinpal.twodfireservice.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.allinpal.twodfireservice.demo.domain.City;
import com.allinpal.twodfireservice.demo.mapper.DemoMapper;
import com.allinpal.twodfireservice.demo.service.DemoService;
import com.thfund.commonService.file.hessian.FileService;


@RestController
@RequestMapping("demo")
public class DemoController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DemoMapper demoMapper;
	
	@Autowired
	private FileService fileService;

	@Autowired
	private DemoService demoService;

	@Value("${credit.factory.url}")
	private String creditFactoryUrl;

	@Value("${mas.send.url}")
	private String masSendUrl;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/")
	String home() {
		logger.debug("home method {}", "debug");
		logger.info("home method {}", "info");
		logger.warn("home method {}", "warn");
		logger.error("home method {}", "error");
		return "Hello World!         ~~~~~~~";
	}

	@RequestMapping("/mapper")
	String mapper() {
		logger.debug("mapper method {}", "debug");
		logger.info("mapper method {}", "info");
		logger.warn("mapper method {}", "warn");
		logger.error("mapper method {}", "error");
		City city = demoMapper.findByState("CA");
		logger.debug("findBystate {}", city);
		City city2 = demoMapper.selectCityByXmlId("1");
		logger.debug("selectCityByXmlId {}", city2);
		return city.toString();
	}

	@RequestMapping("/service")
	String service() {
		logger.debug("service method {}", "debug");
		logger.info("service method {}", "info");
		logger.warn("service method {}", "warn");
		logger.error("service method {}", "error");
		City city = demoService.getCityById("CA");
		logger.debug("getCityById {}", city);
		return city.toString();
	}

	@RequestMapping(value = "/city", method = RequestMethod.POST)
	String addCity(City city) {
		logger.info("add city param: {}", city);
		demoService.saveCity(city);
		return "addCity success!";
	}


	/**
	 * 通过mas调用二代服务
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/foo", method = RequestMethod.POST)
	public String foo(HttpServletRequest request) {
		MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
		uriVariables.add("topicName", "loan.queryUseReq");
		uriVariables.add("prodCode", "8004000001");
		uriVariables.add("loancertno", "513227199203189769");
		String result = restTemplate.postForObject("http://10.56.201.11/mas/bizHandle", uriVariables, String.class);
		return result;
	}
	
	@RequestMapping("/open")
	public String open(HttpServletRequest request) throws UnsupportedEncodingException {
		
		
		Map<String, String> P001Map = new TreeMap<String,String>();
		P001Map.put("interfacecode", "P001");
		P001Map.put("usercode", "1001");//OPEN_ID
		P001Map.put("reqtype", "00");//请求类型     00:开户
		P001Map.put("phoneno", "13813999999");//用户手机号
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		P001Map.put("terminalno", "sdfds");//接入方
		P001Map.put("accesscode", "weixin");
		P001Map.put("usertype", "P");
		P001Map.put("channel", "W");
		P001Map.put("signType", "MD5");
		P001Map.put("version", "V1.0");
		P001Map.put("partner", "uap");
		P001Map.put("inputCharset", "UTF-8");
		P001Map.put("timestamp", sdf.format(new Date()));

		 StringBuilder sb = new StringBuilder();
	        Iterator<String> iterator = P001Map.keySet().iterator();
	        while (iterator.hasNext())
	        {
	            sb.append(P001Map.get(iterator.next()) + "|");
	        }
	        String source = sb.toString();
	        if (!StringUtils.isEmpty(source))
	        {
	            source = source + "1234567890";
	        }
	        
	        String sourceSign = DigestUtils.md5Hex((source).getBytes("UTF-8"));
	        
	        P001Map.put("sign", sourceSign);
	    
	    MultiValueMap<String, String> P001Map1 = new LinkedMultiValueMap<String, String>();
	    for(Map.Entry<String,String> entry :P001Map.entrySet()) {
	    	P001Map1.add(entry.getKey(), entry.getValue());
	    }
	        
		String result = restTemplate.postForObject("http://10.56.201.161:8280/open/tlb/route/route/json", P001Map1, String.class);
		return result;
	}
	
	@RequestMapping("/upload")
	public String upload() throws FileNotFoundException {
		File f = new File("E:/test.txt");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id","test");
		param.put("fileType","test");//
		param.put("fileName","test");
		param.put("serviceName","2dfire");
		InputStream is = new FileInputStream(f);
		Map<String, Object>  result = fileService.upload(param, is);
		String fileId = "";
		for (Entry<String, Object> entry : result.entrySet()) {
			logger.info(entry.getKey() + " : " + entry.getValue());
			if ("fileId".equalsIgnoreCase(entry.getKey())) {
				fileId = (String) entry.getValue();
			}
		}
		return fileId;
	}
}
