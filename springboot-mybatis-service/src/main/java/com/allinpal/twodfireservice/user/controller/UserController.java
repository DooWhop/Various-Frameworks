package com.allinpal.twodfireservice.user.controller;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.creditService.domain.ProdAcct;
import com.allinpal.twodfireservice.user.domain.LoanCreditInfo;
import com.allinpal.twodfireservice.user.domain.User;
import com.allinpal.twodfireservice.user.service.UserService;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;
import com.allinpay.base.holder.CommonParameterHolder;
import com.allinpay.base.util.RadmonUtil;
import com.allinpay.fmp.extintfc.sms.service.SMSSendService;
import com.allinpay.fmp.sms.domain.SmsParam;

/**
 * @author admin
 * 用户服务
 */
@RestController
@RequestMapping("userService")
public class UserController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String md5Key = "1234567890";
	
//	private final String PROD_CODE_2FIRE = "9002000003";
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	public AppConfig appConfig;
	
	@Value("${userService.open.url}")
	private String openUrl;
	
	
	@Autowired
	private SMSSendService smsSendService;
	
	@RequestMapping(value="/query", method=RequestMethod.POST)
	@ResponseBody
	String query(User userParam) {
		logger.info("UserController method query userParam is {}", JSON.toJSONString(userParam));
		JSONObject result = new JSONObject(); 
		//参数校验
		String merchantNo = userParam.getMerchantNo();
		if(merchantNo == null || "".equals(merchantNo)){
			result.put("code", "000001");
			result.put("message", "二维火商户ID为空！");
			logger.info("UserController method query merchantNo is null or empty!");
			return JSON.toJSONString(result);
		}
		userParam.setStatus("1");
		List<User> userList = userService.queryUserInfo(userParam);
		if(userList != null && userList.size() > 0){
			result.put("user", userList.get(0));
		}
		result.put("code", "000000");
		return JSON.toJSONString(result);
	}
	
	@RequestMapping(value="/queryUser", method=RequestMethod.POST)
	@ResponseBody
	String queryUser(User userParam) {
		JSONObject result = new JSONObject(); 
		logger.info("UserController method queryUser userParam is {}", JSON.toJSONString(userParam));
		if(userParam == null){
			result.put("code", "000002");
			result.put("message", "userParam is null or empty!");
			return JSON.toJSONString(result);
		}
		userParam.setStatus("1");
		List<ProdAcct> acctList = userService.queryUserCardInfo(userParam);
		result.put("code", "000000");
		result.put("message", "处理成功");
		result.put("userList", acctList);
		logger.info("UserController method queryUser result is {}", JSON.toJSONString(result));
		return JSON.toJSONString(result);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getCaptcha", method=RequestMethod.POST)
	@ResponseBody
	String getCaptcha(User userParam) {
		logger.info("UserController method getCaptcha user is {}", JSON.toJSONString(userParam));
		Map<String,Object> result = new HashMap<String,Object>();
		String interfaceretcode = null;
		MultiValueMap<String, String> multiValueMap = null;
		String validateCode = null;
		try{
			String mobile = userParam.getMobile();
			String merchantNo = userParam.getMerchantNo();
			String type = userParam.getType();
			logger.info("UserController method getCaptcha , mobile is null or empty !");
			if(mobile == null || "".equals(mobile)){
				result.put("code", "000004");
				result.put("message", "手机号为空！");
				
				return JSONObject.toJSONString(result);
			}
//			if(merchantNo == null || "".equals(merchantNo)){
//				result.put("code", "000004");
//				logger.info("getCaptcha , merchantNo is null or empty! mobile is {}", mobile);
//				return JSONObject.toJSONString(result);
//			}
			if(type == null || "".equals(type)){
				result.put("code", "000004");
				logger.info("getCaptcha , type is null or empty! mobile is {}, merchantNo is {}", mobile,merchantNo);
				return JSONObject.toJSONString(result);
			}
			
			//查询该手机号是否注册绑定过二维火商户ID	
			if("validateMobile".equals(userParam.getType())){
				// 验证手机
				// 新注册用户
				// 查询一代数据库，该手机号是否已经注册用户信息, P001 验证码获取
				Map<String, String> P001Map =  new TreeMap<String,String>();
				
				P001Map.put("interfacecode", "P001");
				P001Map.put("usercode", "1001");//OPEN_ID
				P001Map.put("reqtype", "00");//请求类型     00:开户
				P001Map.put("phoneno", userParam.getMobile());//用户手机号
				
				multiValueMap = this.getParamLess(P001Map);
				
				String jsonStr = URLDecoder.decode(restTemplate.postForObject(openUrl, multiValueMap, String.class), "UTF-8");
				
				logger.info("P001's result is {}", jsonStr);
				
				if(jsonStr == null || "".equals(jsonStr)){
					result.put("code", "000002");
					result.put("message", "P001's return jsonStr is null or empty!");
					logger.info("P001's return jsonStr is null or empty!");
					return JSON.toJSONString(result);
				}
				
				Map<String, Object> ret = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
				
				if(ret == null || !"true".equals(ret.get("isSuccess"))){
					result.put("code", "000002");
					result.put("message", "P001's retMap is faliure!");
					logger.info("P001's retMap is faliure!");
					return JSON.toJSONString(result);
				}
				
				Map<String, String> retMap = (Map<String, String>)ret.get("response");
				
				interfaceretcode = retMap.get("code");
				
				if(!"W-Y000000".equals(interfaceretcode) && !"W-B000005".equals(interfaceretcode)){
					result.put("code", "000002");
					result.put("message", "P001 is faliure! interfaceretcode is "+interfaceretcode);
					logger.info("P001 is faliure! interfaceretcode is {} , message is {}", interfaceretcode,  retMap.get("message"));
					return JSON.toJSONString(result);
				}
				
				if("W-Y000000".equals(interfaceretcode)){
					//发送成功
					result.put("code", "000000");
					result.put("isExist", "N");
					result.put("verifycodeid", retMap.get("verifycodeid"));
					return JSON.toJSONString(result);
				}else if("W-B000005".equals(interfaceretcode)){
					//随机生成短信验证码发送
					validateCode = RadmonUtil.radmonPassword(4);
					result.put("isExist", "Y");
					logger.info("validateCode :" + validateCode);
					result.put("validateCode", validateCode);
				}
			}else{
				//随机生成短信验证码发送
				validateCode = RadmonUtil.radmonPassword(4);
				logger.info("validateCode :" + validateCode);
				result.put("isExist", "Y");
				result.put("validateCode", validateCode);
			}
			//发送短信
			String smsContent = "短信验证码："+validateCode;
			logger.info("smsContent :" + smsContent);
			SmsParam smsParam = new SmsParam();
			smsParam.setInstCode("allinpay");
			smsParam.setContent(smsContent);
			smsParam.setMobiles(userParam.getMobile());
			
			CommonParameterHolder commonParam = new CommonParameterHolder();
			commonParam.setValue("ACTOR_ID", "SYSTEM");
			
			boolean blnSendRlt = smsSendService.sendSMS(smsParam, commonParam);
			
			if(blnSendRlt){
				result.put("code", "000000");
			}else{
				result.put("code", "000003");
			}
			
		}catch (Exception e) {
			logger.error("UserController's method register has an exception {},",e);
			result.put("code", "000001");
			result.put("message", "system error!");
		}
		return JSON.toJSONString(result);
	}
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/register", method=RequestMethod.POST)
	@ResponseBody
	String register(User userParam) {
		logger.info("UserController method register userParam is {}", JSON.toJSONString(userParam));
		Map<String,Object> result = new HashMap<String,Object>();
		String interfaceretcode = null;
		MultiValueMap<String, String> multiValueMap = null;
		String userId = null;
		String openId = null;
		try {
			//校验参数
			String  mobile = userParam.getMobile();
			String merchantNo = userParam.getMerchantNo();
			String type = userParam.getType();
			if(mobile == null || "".equals(mobile)){
				result.put("code", "000003");
				result.put("message", "请输入手机号码!");
				logger.info("UserController method register, mobile is null or empty!");
				return JSONObject.toJSONString(result);
			}
			if(merchantNo == null || "".equals(merchantNo)){
				result.put("code", "000003");
				logger.info("getCaptcha , merchantNo is null or empty! mobile is {}", mobile);
				return JSONObject.toJSONString(result);
			}
			if(type == null || "".equals(type)){
				result.put("code", "000003");
				logger.info("getCaptcha , type is null or empty! mobile is {}, merchantNo is {}", mobile,merchantNo);
				return JSONObject.toJSONString(result);
			}
			
			//查询该手机号是否注册绑定过二维火商户ID
			userParam.setStatus("1");//生效
			List<User> userList = userService.queryUserInfo(userParam);
			
			if("validateMobile".equals(userParam.getType())){
				if(userList == null || userList.size() == 0){
					userId = GenerateIdsUtil.generateId(appConfig.getAppIp());
					logger.info("UserController method register, user's userId is {}",userId);
					if("N".equals(userParam.getIsExist())){
						String loginpwd = "000000";//默认初始000000
						//调用C002
						Map<String, String> C002Map = new TreeMap<String, String>();
						
						C002Map.put("interfacecode", "C002");
						C002Map.put("usercode", "H5_".concat(userId));//OPEN_ID: H5_+user_id
						C002Map.put("phoneno", mobile);//用户手机号
						C002Map.put("loginpwd", loginpwd);//登录密码
						C002Map.put("loginpwdcfm", loginpwd);//确认密码
						C002Map.put("verifycodeid", userParam.getVerifycodeid());//验证码id
						C002Map.put("verifycode", userParam.getVerifycode());//验证码
						
						multiValueMap = this.getParamLess(C002Map);
						
						String jsonStr = URLDecoder.decode(restTemplate.postForObject(openUrl, multiValueMap, String.class), "UTF-8");
						logger.info("C002's result is {}", jsonStr);
						
						if(jsonStr == null || "".equals(jsonStr)){
							result.put("code", "000002");
							result.put("message", "C002's return jsonStr is null or empty!");
							logger.info("C002's return jsonStr is null or empty!");
							return JSON.toJSONString(result);
						}
						
						Map<String, Object> ret = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
						if(ret == null || !"true".equals(ret.get("isSuccess"))){
							result.put("code", "000002");
							result.put("message", "C002 is failure !");
							logger.info("C002 is failure !");
							return JSON.toJSONString(result);
						}
						Map<String, String> retMap = (Map<String, String>)ret.get("response");
						interfaceretcode = retMap.get("code");
						
						if(!"W-Y000000".equals(interfaceretcode) && !"W-B000005".equals(interfaceretcode)){
							result.put("code", "000002");
							result.put("message", "C002 is faliure! interfaceretcode is "+interfaceretcode);
							logger.info("C002 is faliure! interfaceretcode is {}, message is {}", interfaceretcode,  retMap.get("message"));
							return JSON.toJSONString(result);
						}
						
					}
					//查询一代fmp的useroid和investorid
					Map<String, String> P020Map =  new TreeMap<String,String>();
					P020Map.put("interfacecode", "P020");
					P020Map.put("hpno", userParam.getMobile());//用户手机号
					multiValueMap = this.getParamLess(P020Map);
					
					String jsonStr = URLDecoder.decode(restTemplate.postForObject(openUrl, multiValueMap, String.class), "UTF-8");
					logger.info("P020's result is {}", jsonStr);
					
					if(jsonStr == null || "".equals(jsonStr)){
						result.put("code", "000002");
						result.put("message", "P020's return jsonStr is null or empty!");
						logger.info("P020's return jsonStr is null or empty!");
						return JSON.toJSONString(result);
					}
					
					Map<String, Object> ret = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
					if(ret == null || !"true".equals(ret.get("isSuccess"))){
						result.put("code", "000002");
						result.put("message", "P020 is failure !");
						logger.info("P020 is failure !");
						return JSON.toJSONString(result);
					}
					Map<String, Object> retMap = (Map<String, Object>)ret.get("response");
					interfaceretcode = (String)retMap.get("code");
					
					if(!"W-Y000000".equals(interfaceretcode)){
						result.put("code", "000002");
						result.put("message", "P020 is faliure! interfaceretcode is "+interfaceretcode);
						logger.info("P020 is faliure! interfaceretcode is {}, message is {}", interfaceretcode,  retMap.get("message"));
						return JSON.toJSONString(result);
					}
					
					if(retMap.get("userid") == null){
						result.put("code", "000002");
						result.put("message", "P020's return userid is null!");
						logger.info("P020's return userid is null!");
						return JSON.toJSONString(result);
					}
					
					if(retMap.get("investorid") == null){
						result.put("code", "000002");
						result.put("message", "P020's return investorid is null!");
						logger.info("P020's return investorid is null!");
						return JSON.toJSONString(result);
					}
					
					
					openId = (String)retMap.get("openid");
					
					if(openId == null || "".equals(openId)){
						//关联一代用户信息表的openId为用户表主键
//						// 调用P006
						Map<String, String> P006Map = new TreeMap<String, String>();
						P006Map.put("interfacecode", "P006");
						P006Map.put("usercode", mobile);//用户代码(登录手机号)
						P006Map.put("reqtype", "4");//绑定openId功能
						P006Map.put("openid", "H5_".concat(userId));
						
						multiValueMap = this.getParamLess(P006Map);
						
						jsonStr = URLDecoder.decode(restTemplate.postForObject(openUrl, multiValueMap, String.class), "UTF-8");
						logger.info("P006's result is {}", jsonStr);
						
						if(jsonStr == null || "".equals(jsonStr)){
							result.put("code", "000002");
							result.put("message", "P006's return jsonStr is null or empty!");
							logger.info("P006's return jsonStr is null or empty!");
							return JSON.toJSONString(result);
						}
						
						ret = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
						if(ret == null || !"true".equals(ret.get("isSuccess"))){
							result.put("code", "000002");
							result.put("message", "P006 is failure !");
							logger.info("P006 is failure !");
							return JSON.toJSONString(result);
						}
						
						Map<String, Object> P006RetMap =  (Map<String, Object>)ret.get("response");
						interfaceretcode = (String)P006RetMap.get("code");
						
						if(!"W-Y000000".equals(interfaceretcode)){
							result.put("code", "000002");
							result.put("message", "P006 is faliure! interfaceretcode is "+interfaceretcode);
							logger.info("P006 is faliure! interfaceretcode is {}, message is {}", interfaceretcode,  P006RetMap.get("message"));
							return JSON.toJSONString(result);
						}
						
					}
					
					// 保存用户信息
					userParam.setUserId(userId);
					userParam.setStatus("1"); //状态 1：生效 0：失效
					userParam.setMerchantNo(merchantNo);
					userParam.setTlbUserOid(new BigDecimal(retMap.get("userid").toString()));
					userParam.setInvestorOid(new BigDecimal(retMap.get("investorid").toString()));
					userParam.setCreateUid(userParam.getUserId());
					userParam.setCreateTime(System.currentTimeMillis());
					userParam.setLastModifyUid(userParam.getUserId());
					userParam.setLastModifyTime(System.currentTimeMillis());
					userService.saveUser(userParam);
					result.put("userId", userId);
					result.put("tlbOpenId", openId);
				}else{
					User user = userList.get(0);
					
					boolean bindRet = this.setUpOpenId(user);
					
					if(!bindRet){
						result.put("code", "000005");
						result.put("message", "set up fmp.user's openId is failure! userId is "+user.getUserId());
						return JSON.toJSONString(result);
					}
					
					result.put("userId", user.getUserId());
				}
			}else{
				//登录验证 ，需要查看openId是否绑定，若通联宝账户fmp.user表中openId为空，绑定二维火的H5_userId为openId
				User user = userList.get(0);
				boolean bindRet = this.setUpOpenId(user);
				if(!bindRet){
					result.put("code", "000005");
					result.put("message", "set up fmp.user's openId is failure! userId is "+user.getUserId());
					return JSON.toJSONString(result);
				}
				result.put("userId", user.getUserId());
			}
			result.put("code", "000000");
			result.put("message", "success!");
		} catch (Exception e) {
			logger.error("UserController's method register has an exception {},",e);
			result.put("code", "000004");
			result.put("message", "system error!");
		}
		return JSON.toJSONString(result);
	}

	@RequestMapping(value="/redirect", method=RequestMethod.POST)
	@ResponseBody
	String initRedirect(User userParam) {
		logger.info("UserController method initRedirect userParam is {}", JSON.toJSONString(userParam));
		Map<String,Object> result = new HashMap<String,Object>();
		int count = 0;
		String mobile = userParam.getMobile();
		String userId = userParam.getUserId();
		try {
			//校验参数
			if(userId == null || "".equals(userId)){
				result.put("code", "000002");
				logger.info("UserController method initRedirect, userId is null or empty!");
				return JSONObject.toJSONString(result);
			}
			if(mobile == null || "".equals(mobile)){
				//查询手机号
				userParam.setStatus("1");//生效
				List<User> list = userService.queryUserInfo(userParam);
				
				if(list == null || list.size() == 0){
					logger.error("UserController's method initRedirect UserInfo is null! userId is {}",userParam.getUserId());
					result.put("code", "000002");
					return JSON.toJSONString(result);
				}
				result.put("mobile", list.get(0).getMobile());
			}else{
				result.put("mobile", userParam.getMobile());
			}
			//查询是否有授信信息
			List<LoanCreditInfo> creditList= userService.queryCreditCount(userParam);
			
			if(creditList == null || creditList.size() == 0){
				//查询身份证是否上传
				userParam.setOcrState("10");
				count = userService.queryUserIdenFile(userParam);
				if(count != 2){
					//上传身份证页面
					result.put("pageCode", "400000");
				}else{
					//查询银行卡信息
					userParam.setCardStatus("1");//生效
					count = userService.queryCardCount(userParam);
					if(count == 0){
						//未绑定银行卡页面
						result.put("pageCode", "100000");
					}else{
						//已绑定银行卡页面，未做授信，请求准入验证
						result.put("pageCode", "200000");
					}
				}
			}else{
				//已有授信
				LoanCreditInfo bean = creditList.get(0);
				result.put("prodCode", bean.getProdCode());
				result.put("pageCode", "300000");
				result.put("creditStatus", bean.getStatus());
			}
			result.put("code", "000000");
		}catch (Exception e) {
			logger.error("UserController's method initRedirect has an exception {},",e);
			result.put("code", "000001");
			if(mobile != null && !"".equals(mobile)){
				result.put("mobile", userParam.getMobile());
			}
			result.put("message", "system error!");
		}
		return JSON.toJSONString(result);
	}
	
	@SuppressWarnings("unchecked")
	private boolean setUpOpenId(User user) throws Exception{
		String openId = null;
		//查询一代fmp的useroid和investorid
		Map<String, String> P020Map =  new TreeMap<String,String>();
		P020Map.put("interfacecode", "P020");
		P020Map.put("userid", user.getTlbUserOid().toString());//用户手机号
		MultiValueMap<String, String> multiValueMap = this.getParamLess(P020Map);
		
		String jsonStr = URLDecoder.decode(restTemplate.postForObject(openUrl, multiValueMap, String.class), "UTF-8");
		logger.info("P020's result is {}", jsonStr);
		
		if(jsonStr == null || "".equals(jsonStr)){
			logger.info("P020's return jsonStr is null or empty!");
			return false;
		}
		
		Map<String, Object> ret = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
		if(ret == null || !"true".equals(ret.get("isSuccess"))){
			logger.info("P020 is failure !");
			return false;
		}
		Map<String, Object> retMap = (Map<String, Object>)ret.get("response");
		String interfaceretcode  = (String)retMap.get("code");
		
		if(!"W-Y000000".equals(interfaceretcode)){
			logger.info("P020 is faliure! interfaceretcode is {}, message is {}", interfaceretcode,  retMap.get("message"));
			return false;
		}
		
		openId = (String)retMap.get("openid");
		
		if(openId == null || "".equals(openId)){
			//关联一代用户信息表的openId为用户表主键
//			// 调用P006
			Map<String, String> P006Map = new TreeMap<String, String>();
			P006Map.put("interfacecode", "P006");
			P006Map.put("usercode", user.getMobile());//用户代码(登录手机号)
			P006Map.put("reqtype", "4");//绑定openId
			P006Map.put("openid", "H5_".concat(user.getUserId()));
			
			multiValueMap = this.getParamLess(P006Map);
			
			jsonStr = URLDecoder.decode(restTemplate.postForObject(openUrl, multiValueMap, String.class), "UTF-8");
			logger.info("P006's result is {}", jsonStr);
			
			if(jsonStr == null || "".equals(jsonStr)){
				logger.info("P006's return jsonStr is null or empty!");
				return false;
			}
			
			ret = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
			if(ret == null || !"true".equals(ret.get("isSuccess"))){
				logger.info("P006 is failure !");
				return false;
			}
			
			Map<String, Object> P006RetMap =  (Map<String, Object>)ret.get("response");
			interfaceretcode = (String)P006RetMap.get("code");
			
			if(!"W-Y000000".equals(interfaceretcode)){
				logger.info("P006 is faliure! interfaceretcode is {}, message is {}", interfaceretcode,  P006RetMap.get("message"));
				return false;
			}
		}
		return true;
	}
	
	private MultiValueMap<String, String> getParamLess(Map<String, String> paramsMap) throws Exception
    {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		paramsMap.put("terminalno", "sdfds");//接入方
		paramsMap.put("accesscode", "weixin");
		paramsMap.put("usertype", "P");
		paramsMap.put("channel", "W");
		paramsMap.put("signType", "MD5");
		paramsMap.put("version", "V1.0");
		paramsMap.put("partner", "uap");
		paramsMap.put("inputCharset", "UTF-8");
		paramsMap.put("timestamp", sdf.format(new Date()));
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = paramsMap.keySet().iterator();
        while (iterator.hasNext())
        {
            sb.append(paramsMap.get(iterator.next()) + "|");
        }
        String source = sb.toString();
        if (!StringUtils.isEmpty(source))
        {
            source = source + md5Key;
        }
        
        String sourceSign = DigestUtils.md5Hex((source).getBytes("UTF-8"));
        
        paramsMap.put("sign", sourceSign);
         
        MultiValueMap<String, String> multiValueMap =  new LinkedMultiValueMap<String, String>();
        
        for(Map.Entry<String, String> entry : paramsMap.entrySet()){
        	multiValueMap.add(entry.getKey(), entry.getValue());
        }

        return multiValueMap;
    }
}
