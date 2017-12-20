package com.allinpal.twodfireweb.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireweb.request.BankCardDto;
import com.allinpal.twodfireweb.request.VerifyCodeDto;
import com.allinpal.twodfireweb.util.RedisUtils;

/**绑卡
 *  
 * @author liudp
 *
 */
@Controller
public class BindCardController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private RedisUtils redisUtils;

	@Value("${idenVerifyService.supportCard.url}")
	private String SUPPORT_CARD_URL;//查询支持的银行列表服务url
	
	@Value("${idenVerifyService.sendVerifyCode.url}")
	private String SEND_VERIFY_CODE_URL;//发送验证码服务url
	
	@Value("${idenVerifyService.checkVerifyCode.url}")
	private String CHECK_VERIFY_CODE_URL;//检查验证码服务url
	
	@Value("${idenVerifyService.bindCard.url}")
	private String BIND_CARD_URL;//添加银行卡服务url

	/**测试
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/bindCardTest")
	public String home(Map<String, Object> model) {
		model.put("userId", "1001");
		model.put("merchantNo", "1001");
		return "idenVerify/bindCard";
	}
	
	/**ajax调用支持的银行列表
	 * 
	 * @param prodCode
	 * @return
	 */
	@RequestMapping(value="/ajaxGetBankList", produces="application/json; charset=utf-8")
	@ResponseBody
	public String ajaxGetBankList(String prodCode) {		
		Map<String, String> verifyResult = checkParamsOfGetBankList(prodCode);
		if(!MapUtils.isEmpty(verifyResult)){
			return JSONObject.toJSONString(verifyResult);
		}
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("prodCode", prodCode);
		logger.info("ajaxGetBankList param {}",paramMap);
        String result = restTemplate.postForObject(SUPPORT_CARD_URL, paramMap, String.class);
        logger.info("ajaxGetBankList result {}",result);
        return result;
	}
	
	/**ajax调用发送验证码
	 * 
	 * @param verifyCode
	 * @return
	 */
	@RequestMapping(value="/ajaxSendVerifyCode", produces="application/json; charset=utf-8")
	@ResponseBody
	public String ajaxSendVerifyCode(VerifyCodeDto verifyCode) {
		Map<String, String> verifyResult = checkParamsOfSendVerifyCode(verifyCode);
		if(!MapUtils.isEmpty(verifyResult)){
			return JSONObject.toJSONString(verifyResult);
		}
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("userId", verifyCode.getUserId());
		paramMap.add("hpNo", verifyCode.getHpNo());
		paramMap.add("reqType",verifyCode.getReqType());
		logger.info("ajaxSendVerifyCode param {}",paramMap);
        String result = restTemplate.postForObject(SEND_VERIFY_CODE_URL, paramMap, String.class);
        logger.info("ajaxSendVerifyCode result {}",result);
        return result;
	}
	
	/**ajax调用校验验证码
	 * 
	 * @param verifyCode
	 * @return
	 */
	@RequestMapping(value="/ajaxCheckVerifyCode",produces="application/json; charset=utf-8")
	@ResponseBody
	public String ajaxCheckVerifyCode(VerifyCodeDto verifyCode) {
		Map<String, String> verifyResult = checkParamsOfCheckVerifyCode(verifyCode);
		if(!MapUtils.isEmpty(verifyResult)){
			return JSONObject.toJSONString(verifyResult);
		}
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("hpNo", verifyCode.getHpNo());
		paramMap.add("reqType", verifyCode.getReqType());
		paramMap.add("verifyCode", verifyCode.getVerifyCode());
		paramMap.add("verifyCodeId", verifyCode.getVerifyCodeId());
		logger.info("ajaxCheckVerifyCode param {}",paramMap);
        String result = restTemplate.postForObject(CHECK_VERIFY_CODE_URL, paramMap, String.class);
        logger.info("ajaxCheckVerifyCode result {}",result);
        return result;
	}
	
	/**ajax调用添加银行卡
	 * 
	 * @param bankCard
	 * @param merchantNo
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value="/ajaxAddBankCard",produces="application/json; charset=utf-8")
	@ResponseBody
	public String ajaxAddBankCard(BankCardDto bankCard, String merchantNo, String mobile) {
		Map<String, String> verifyResult = checkParamsOfAddBankCard(bankCard, merchantNo, mobile);
		if(!MapUtils.isEmpty(verifyResult)){
			return JSONObject.toJSONString(verifyResult);
		}
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
		paramMap.add("userId", bankCard.getUserId());
		paramMap.add("bankIdenName", bankCard.getBankIdenName());
		paramMap.add("bankIdenCode", bankCard.getBankIdenCode());
		paramMap.add("hpNo", bankCard.getHpNo());
		paramMap.add("acctNo", bankCard.getAcctNo());
		paramMap.add("certNo", bankCard.getCertNo());
		paramMap.add("acctName", bankCard.getAcctName());
		paramMap.add("verifyMode", bankCard.getVerifyMode());
		paramMap.add("merchantNo", merchantNo);
		paramMap.add("mobile", mobile);
		logger.info("ajaxAddBankCard param {}",paramMap);
        String result = restTemplate.postForObject(BIND_CARD_URL, paramMap, String.class);
        logger.info("ajaxAddBankCard result {}",result);
        return result;
	}
	
	/*@GetMapping("/goToPreApply")
	public String goToPreApply(String merchantNo, String hpNo, String prodCode,Map<String, Object> model) {
		model.put("merchantNo", merchantNo);
		model.put("mobile", hpNo);
		model.put("prodCode", prodCode);
		return "loanApply/applyFirst";
	}*/
	
	/**绑卡成功controller
	 *
	 * @param merchantNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/bindCardSuccess")
	public String bindCardSuccess(String merchantNo, Map<String, Object> model) {
		model.put("merchantNo", merchantNo);
        return "idenVerify/bindCardSucc";
	}
	
	/**绑卡成功回到首页controller
	 * 
	 * @param merchantNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/bindSuccBackHome")
	public String bindSuccBackHome(String merchantNo, Map<String, Object> model) {
		model.put("merchantNo", merchantNo);
        return "home/2dfireHome";
	}
	
	/**查询银行列表参数校验
	 * 
	 * @param prodCode
	 * @return
	 */
	private Map<String, String> checkParamsOfGetBankList(String prodCode){
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isEmpty(prodCode)){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "产品代码不能为空");
		}
		return checkMap;
	}
	
	/**发送验证码参数校验
	 * 
	 * @param verifyCode
	 * @return
	 */
	private Map<String, String> checkParamsOfSendVerifyCode(VerifyCodeDto verifyCode){
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isEmpty(verifyCode.getUserId())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "用户标识不能为空");
		}
		if(StringUtils.isEmpty(verifyCode.getHpNo())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "银行预留手机号不能为空");
		}
		if(StringUtils.isEmpty(verifyCode.getReqType())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "验证码请求类型不能为空");
		}
		return checkMap;
	}
	
	/**校验验证码参数校验
	 * 
	 * @param verifyCode
	 * @return
	 */
	private Map<String, String> checkParamsOfCheckVerifyCode(VerifyCodeDto verifyCode){
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isEmpty(verifyCode.getHpNo())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "银行预留手机号不能为空");
		}		
		if(StringUtils.isEmpty(verifyCode.getReqType())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "验证码请求类型不能为空");
		}		
		if(StringUtils.isEmpty(verifyCode.getVerifyCode())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "验证码不能为空");
		}		
		if(StringUtils.isEmpty(verifyCode.getVerifyCodeId())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "验证码标识不能为空");
		}
		return checkMap;
	}
	
	/**添加银行卡参数校验
	 * 
	 * @param bankCard
	 * @param merchantNo
	 * @param mobile
	 * @return
	 */
	private Map<String, String> checkParamsOfAddBankCard(BankCardDto bankCard, String merchantNo, String mobile){
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isEmpty(bankCard.getAcctName())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "账户名不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getAcctNo())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "账户号不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getBankIdenCode())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "银行代码不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getBankIdenName())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "银行名不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getCertNo())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "身份证号不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getHpNo())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "银行预留手机号不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getUserId())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "用户标识不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getVerifyMode())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "验证方式不能为空");
		}
		if(StringUtils.isEmpty(merchantNo)){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "商户号不能为空");
		}
		if(StringUtils.isEmpty(mobile)){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "注册手机号不能为空");
		}
							
		return checkMap;
	}
	
}
