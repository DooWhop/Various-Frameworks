package com.allinpal.twodfireweb.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireweb.request.CreditReq;
import com.allinpal.twodfireweb.request.User;
import com.allinpal.twodfireweb.util.RedisUtils;

/**
 * @author admin
 * 账户-验证手机
 */
@Controller
public class UserController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String PROD_CODE_2FIRE = "9002000003";

	//0全部 1申请未完成 2申请失效 3审批中 4审批失败 5审批通过 6额度生效 66额度到期 7额度失效 8审批作废 9额度暂停
	private static String EFFECTIVE_STATUS="3,5,6,9";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${userService.url}")
	private String userServiceUrl;
	
	@Value("${userService.creditService.url}")
	private String creditServiceUrl;
	
	@Value("${contract.url}")
	private String contractUrl;
	
    @Autowired
    private RedisUtils redisUtils;
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/initUser")
	public String initUser(Map<String, Object> model, HttpServletRequest request) {
		logger.info("initUser is start!");
		String merchantNo = request.getParameter("merchantNo");
    	if(StringUtils.isEmpty(merchantNo)){
    		logger.info("merchantNo 为空");
    		model.put("merchantNo", merchantNo);
    		return "common/error";
    	}else{
	        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
			paramMap.add("merchantNo", merchantNo);
			String userQueryUrl = userServiceUrl.concat("/query");
	        String jsonStr = restTemplate.postForObject(userQueryUrl, paramMap, String.class);
			logger.info("request from {} result is {}",userQueryUrl,jsonStr);
			model.put("enteraction", request.getAttribute("enteraction"));
			if(jsonStr == null || "".equals(jsonStr)){
				model.put("merchantNo", merchantNo);
				return "common/error";
			}
			Map<String,Object> result = (Map<String,Object>)JSONObject.parseObject(jsonStr,Map.class);
			String code = (String)result.get("code");
			if("000000".equals(code)){
				User user = (User)JSONObject.parseObject(JSON.toJSONString(result.get("user")),User.class);
				if(user == null){
					model.put("merchantNo", merchantNo);
					return "user/validateMobile";
				}
				model.put("user", user);
				return "user/idConfirmation";
			}
			model.put("merchantNo", merchantNo);
			return "common/error";
    	}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/initRedirect")
	public String initRedirect(User user, Model model, HttpServletRequest request) {
		String openId = getOpenId(request);
		logger.info("initRedirect is start! userId is {}, merchantNo is {}, mobile is {} ",openId,user.getMerchantNo(),user.getMobile());
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("userId", openId);
		paramMap.add("merchantNo", user.getMerchantNo());
		paramMap.add("prodCode", PROD_CODE_2FIRE);
		String jsonStr = restTemplate.postForObject(userServiceUrl.concat("/redirect"), paramMap, String.class);
		logger.info("initRedirect' result is {}", jsonStr);
		
		Map<String,Object> result = (Map<String,Object>)JSONObject.parseObject(jsonStr,Map.class);
		String code = (String)result.get("code");
		if(!"000000".equals(code)){
			model.addAttribute("message", "系统异常，请稍后再试！");
			model.addAttribute("merchantNo", user.getMerchantNo());
			model.addAttribute("mobile", result.get("mobile"));
			model.addAttribute("prodCode", PROD_CODE_2FIRE);
			return"myCredit/fail";
		}
		String pageCode = (String)result.get("pageCode");
		
		if("100000".equals(pageCode)){
			//跳转绑卡页面
			model.addAttribute("userId", openId);
			model.addAttribute("merchantNo", user.getMerchantNo());
			model.addAttribute("mobile", result.get("mobile"));
			return "idenVerify/bindCard";
		}else if("200000".equals(pageCode)){
			//调准入校验url
			MultiValueMap<String, String> multiValueMap =  new LinkedMultiValueMap<String, String>();
			multiValueMap.add("merchantNo", user.getMerchantNo());
			multiValueMap.add("prodCode", PROD_CODE_2FIRE);
			multiValueMap.add("mobile", (String)result.get("mobile"));
			String creditPreapplyUrl = creditServiceUrl.concat("/preapply");
			jsonStr = restTemplate.postForObject(creditPreapplyUrl, multiValueMap, String.class);
			logger.info("{} return info is {}",creditPreapplyUrl, jsonStr);
	        JSONObject initJson = JSONObject.parseObject(jsonStr);
			if("000000".equals(initJson.getString("code"))){
				CreditReq creditReq = JSONObject.parseObject(initJson.toString(), CreditReq.class);
		    	String creditReqStr = JSON.toJSONString(creditReq);
		    	model.addAttribute("creditReqStr", creditReqStr);
		    	model.addAttribute("creditReq", creditReq);
		    	model.addAttribute("mobilestr", getMobilestr(creditReq.getMobile()));
		    	return "loanApply/applyFirst";
			}else{
				model.addAttribute("message", initJson.getString("message"));
			}
			model.addAttribute("merchantNo", user.getMerchantNo());
			model.addAttribute("mobile", result.get("mobile"));
			model.addAttribute("prodCode", PROD_CODE_2FIRE);
			return "loanApply/applyFail"; 
		}else if("300000".equals(pageCode)){
			model.addAttribute("merchantNo", user.getMerchantNo());
	    	model.addAttribute("prodCode", (String)result.get("prodCode"));
	    	model.addAttribute("mobile", (String)result.get("mobile"));
	    	if(!EFFECTIVE_STATUS.contains((String)result.get("creditStatus"))){
		    	return "redirect:/preapply"; 
	    	}else{
	    		return "redirect:/queryCreditDetail"; 
	    	}
		}else if("400000".equals(pageCode)){
			//跳转身份证上传页面
			model.addAttribute("userId", openId);
			model.addAttribute("merchantNo", user.getMerchantNo());
			model.addAttribute("mobile", result.get("mobile"));
			return "idenVerify/uploadIdentity";
		}else{
			model.addAttribute("message", "系统异常，请稍后再试！");
			model.addAttribute("merchantNo", user.getMerchantNo());
			model.addAttribute("mobile", result.get("mobile"));
			model.addAttribute("prodCode", PROD_CODE_2FIRE);
			return"myCredit/fail";
		}
	}
	
	@RequestMapping("/initChangeUser")
	public String initChangeUser(Map<String, Object> model) {
		logger.info("initChangeUser is start!");
		
		return "user/idConfirmation";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getCaptcha", method = RequestMethod.POST)
    @ResponseBody
	public String getCaptcha(User user, String type, Map<String, Object> model) {
		String mobile = user.getMobile();
		String merchantNo = user.getMerchantNo();
		logger.info("getCaptcha is start! mobile is {}", mobile);
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(mobile == null || "".equals(mobile)){
			retMap.put("code", "000001");
			retMap.put("message", "请输入手机号码!");
			return JSONObject.toJSONString(retMap);
		}
//		if(merchantNo == null || "".equals(merchantNo)){
//			retMap.put("code", "000001");
//			logger.info("getCaptcha , merchantNo is null or empty! mobile is {}", mobile);
//			return JSONObject.toJSONString(retMap);
//		}
		if(type == null || "".equals(type)){
			retMap.put("code", "000001");
			logger.info("getCaptcha , type is null or empty! mobile is {}, merchantNo is {}", mobile,merchantNo);
			return JSONObject.toJSONString(retMap);
		}
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("mobile", mobile);
		paramMap.add("merchantNo", user.getMerchantNo());
		paramMap.add("type", type);//发送验证码
		String getCaptchaUrl = userServiceUrl.concat("/getCaptcha");
		String jsonStr = restTemplate.postForObject(getCaptchaUrl, paramMap, String.class);
		logger.info("request from {} result is {}",getCaptchaUrl,jsonStr);
		
		Map<String,Object> map = (Map<String,Object>)JSONObject.parseObject(jsonStr,Map.class);
		String code = (String)map.get("code");
		if("000000".equals(code)){
			String isExist = (String)map.get("isExist");
			redisUtils.set("captcha_"+mobile, "N".equals(isExist) ? map.get("verifycodeid"):map.get("validateCode"),new Long(300));
			retMap.put("isExist", isExist);
		}
		retMap.put("code", code);
		
		return JSONObject.toJSONString(retMap);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/userValidate", method = RequestMethod.POST)
    @ResponseBody
	public String userValidate(User user, String captcha, String type, String isExist,
				Map<String, Object> model, HttpServletRequest request) {
		logger.info("userValidate is start! mobile is {}, captcha is {}",user.getMobile(),captcha);
		Map<String,Object> retMap = new HashMap<String,Object>();
		String  mobile = user.getMobile();
		String merchantNo = user.getMerchantNo();
		if(mobile == null || "".equals(mobile)){
			retMap.put("code", "000001");
			retMap.put("message", "请输入手机号码!");
			logger.info("userValidate mobile is null or empty");
			return JSONObject.toJSONString(retMap);
		}
		if(captcha == null || "".equals(captcha)){
			retMap.put("code", "000001");
			retMap.put("message", "请输入4位手机验证码!");
			logger.info("userValidate captcha is null or empty");
			return JSONObject.toJSONString(retMap);
		}
		
		if(isExist == null || "".equals(isExist)){
			retMap.put("code", "000001");
			retMap.put("message", "验证失败!");
			logger.info("userValidate isExist is null or empty");
			return JSONObject.toJSONString(retMap);
		}
		
		if(redisUtils.get("captcha_"+mobile) == null){
			retMap.put("code", "000001");
			retMap.put("message", "短信验证码未发送成功!");
			logger.info("userValidate captcha_{} is null or empty",mobile);
			return JSONObject.toJSONString(retMap);
		}
		String validateCode = redisUtils.get("captcha_"+mobile).toString();
		if(!"validateMobile".equals(type) || "Y".equals(isExist)){
			
			if(!captcha.equals(validateCode)){
				retMap.put("code", "000001");
				retMap.put("message", "短信验证码错误!");
				return JSONObject.toJSONString(retMap);
			}
			//短信验证码通过
//			if(!"validateMobile".equals(type)){
//				redisUtils.remove("captcha_"+mobile);
//				setOpenId(user.getUserId(), request);
//				retMap.put("code", "000000");
//				retMap.put("message", "验证码校验通过!");
//				return JSONObject.toJSONString(retMap);
//			}
		}
		
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("mobile", mobile);
        paramMap.add("merchantNo", merchantNo);
        paramMap.add("type", type);
        paramMap.add("isExist", isExist);
        paramMap.add("verifycode", captcha);
        if("N".equals(isExist)){
        	paramMap.add("verifycodeid", validateCode);
        }
        String userRegisterUrl = userServiceUrl.concat("/register");
        String jsonStr = restTemplate.postForObject(userRegisterUrl, paramMap, String.class);
		logger.info("request from {} result is {}",userRegisterUrl,jsonStr);
		
		Map<String,Object> map = (Map<String,Object>)JSONObject.parseObject(jsonStr,Map.class);
		String code = (String)map.get("code");
		if("000000".equals(code)){
			redisUtils.remove("captcha_"+mobile);
			String userId = (String)map.get("userId");
			setOpenId(userId, request);
			String tlbOpenId = (String)map.get("tlbOpenId");
			
			if(tlbOpenId !=null && !"".equals(tlbOpenId)){
				redisUtils.set("tlbOpenId_"+userId, tlbOpenId, new Long(600));
			}
			// 生成协议
	        Map<String, Object> protocolParams = new HashMap<String, Object>();
	        protocolParams.put("creditRecordNo", "0");
	        List<Map<String, Object>> protocolList = new ArrayList<Map<String, Object>>();
	        Map<String, Object> YHZCmap = new HashMap<String, Object>();
	        YHZCmap.put("protocolType", "5");
	        protocolList.add(YHZCmap);
	        protocolParams.put("protocols", protocolList);
	        protocolParams.put("useRecordNo", "0");
	        String signAgreeUrl = contractUrl + "/signAgreement";
	        logger.info("signAgree signAgreeUrl=====>{}", signAgreeUrl);
	        logger.info("signAgree param=====>{}", protocolParams);
	        String signAgreeResult = restTemplate.postForObject(signAgreeUrl,
	            protocolParams, String.class);
	        logger.info("signAgree response=====>{}", signAgreeResult);
		}
		
		retMap.put("code", code);
		retMap.put("userId", (String)map.get("userId"));
		retMap.put("message", "success");
		return JSONObject.toJSONString(retMap);
	}
	
	@RequestMapping(value = "/showProtocol", method = RequestMethod.GET)
	public String showProtocol(Map<String, Object> model) {
		return "user/yhzcxy";
	}
	
	private String getMobilestr(String mobile) {
		return mobile.substring(0,3)+"****"+mobile.substring(7, 11);
	}  
}
