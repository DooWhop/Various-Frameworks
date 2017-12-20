package com.allinpal.twodfireweb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireweb.request.IdentityDto;
import com.allinpal.twodfireweb.request.User;

/**上传身份证
 *  
 * @author liudp
 *
 */
@Controller
public class IdentityController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${idenVerifyService.ocrVerify.url}")
	private String OCR_VERIFY_URL;
	
	@RequestMapping("/identityTest")
	public String identityTest(Map<String, Object> model){
		model.put("merchantNo", "821000000000000");
		model.put("userId", "1001");
		model.put("mobile", "12345678901");
		return "idenVerify/uploadIdentity";
	}
		
	@ResponseBody
	@RequestMapping(value="/uploadIdentity", produces="application/json; charset=utf-8")
	public String uploadIdentity(@RequestBody List<IdentityDto> identityList) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isSuccess", "true");
		for (IdentityDto identityDto : identityList) {				
			logger.info("uploadIdentity IdentityDto param idenFace:{},userId:{}",
					identityDto.getIdenFace(),identityDto.getUserId());						 		
			MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
			String photo = identityDto.getPhoto();
			if(StringUtils.isEmpty(photo)){
				map.put("isSuccess", "false");
	        	map.put("errorMessage", "请继续上传");
	        	break;
			}
			paramMap.add("userId", identityDto.getUserId());
			paramMap.add("idenFace", identityDto.getIdenFace());
			paramMap.add("file", photo);
			paramMap.add("fileType", "1");//1-身份证
			paramMap.add("prodCode", "9002000003");//二维火产品code
	        String result = restTemplate.postForObject(OCR_VERIFY_URL, paramMap, String.class);
	        logger.info("uploadIdentity param result:{}",result);
	        Map<String, Object> res = (Map<String, Object>) JSONObject.parse(result);
	        if(!"true".equals(res.get("isSuccess"))){
	        	map.put("isSuccess", "false");
	        	map.put("errorMessage", "上传失败,请重试");
	        }								
		}		
		
        return JSONObject.toJSONString(map);
	}
	

}
