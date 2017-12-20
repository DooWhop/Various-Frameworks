package com.allinpal.twodfireservice.idenverify.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.idenverify.domain.BankCard;
import com.allinpal.twodfireservice.idenverify.domain.IdenFile;
import com.allinpal.twodfireservice.idenverify.domain.P020TLBUser;
import com.allinpal.twodfireservice.idenverify.rcservice.C010QueryBankAcctService;
import com.allinpal.twodfireservice.idenverify.rcservice.C012BindCardService;
import com.allinpal.twodfireservice.idenverify.rcservice.C014QuickAuthService;
import com.allinpal.twodfireservice.idenverify.rcservice.G2ChangeCardService;
import com.allinpal.twodfireservice.idenverify.rcservice.P001SendVerifyCodeService;
import com.allinpal.twodfireservice.idenverify.rcservice.P017CheckVerifyCodeService;
import com.allinpal.twodfireservice.idenverify.rcservice.P020QueryOpenIdService;
import com.allinpal.twodfireservice.idenverify.rcservice.TwoDFireMutexCheckService;
import com.allinpal.twodfireservice.idenverify.rcservice.TwoDFireUserProdAcctService;
import com.allinpal.twodfireservice.idenverify.service.IdenVerifyService;

/**绑卡服务
 * 
 * @author liudp
 *
 */
@RestController
@RequestMapping("idenVerifyService")
public class IdenVerifyController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IdenVerifyService idenverifyService;
	
	@Autowired
	private C010QueryBankAcctService c010QueryBankAcctService;
	
	@Autowired
	private C014QuickAuthService c014QuickAuthService;
		
	@Autowired
	private C012BindCardService c012BindCardService;
	
	@Autowired
	private P017CheckVerifyCodeService p017CheckVerifyCodeService;
	
	@Autowired
	private G2ChangeCardService g2ChangeCardService;
	
	@Autowired
	private P001SendVerifyCodeService p001SendVerifyCodeService;
	
	@Autowired
	private P020QueryOpenIdService p020QueryOpenIdService;
	
	@Autowired
	private TwoDFireUserProdAcctService twoDFireUserProdAcctService;
	
	@Autowired
	private TwoDFireMutexCheckService twoDFireMutexCheckService;
	
				
	/**上传身份证
	 * 
	 * @param file
	 * @param idenFile
	 * @return
	 */
	@RequestMapping(value="/ocrVerify", method=RequestMethod.POST)
	String ocrVerify(String file, IdenFile idenFile) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			//参数校验
			logger.info("ocrVerify param idenFile:{}", idenFile);
			Map<String, String> verifyResult = checkParamsOfOcrVerify(file, idenFile);
			if(!MapUtils.isEmpty(verifyResult)){
				return JSONObject.toJSONString(verifyResult);
			}	
			//上传并保存到本地			
			boolean result = idenverifyService.idenFileUpload(file, idenFile);
			if(result){
				logger.info("上传文件并保存信息到本地成功");
				retMap.put("isSuccess", "true");
			}else{
				logger.info("上传文件并保存信息到本地失败");
				retMap.put("isSuccess", "false");
				retMap.put("errorMessage", "系统异常");
			}
			
		} catch (Exception e) {
			logger.error("上传文件保存信息到本地发生异常! Exception is :{}", e);	
			retMap.put("isSuccess", "false");
			retMap.put("errorMessage", "系统发生异常");			
		}	
		return JSONObject.toJSONString(retMap);		
	}
	
	
	/**发送手机验证码
	 * 
	 * @param bankCard
	 * @return
	 */
	@RequestMapping(value="/sendVerifyCode", method=RequestMethod.POST)
	String sendVerifyCode(BankCard bankCard) {		
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			//1.参数校验
			logger.info("sendVerifyCode param: {}", bankCard);
			Map<String, String> verifyResult = checkParamsOfSendVerifyCode(bankCard);
			if(!MapUtils.isEmpty(verifyResult)){
				return JSONObject.toJSONString(verifyResult);
			}					
			String openId = "";
			//2.通过userId查询tlbuseroid
			String tlbUserOid = idenverifyService.getTlbUserOidByUserId(bankCard.getUserId());
			if(StringUtils.isEmpty(tlbUserOid)){
				logger.info("TlbUserOid不能为空");
				retMap.put("isSuccess", "false");
				retMap.put("errorMessage", "系统异常");			
				return JSONObject.toJSONString(retMap);
			}	
			//3.通过tlbuseroid查询openid
			P020TLBUser tlbUser = p020QueryOpenIdService.queryTlbUserInfo(tlbUserOid);
			if(tlbUser==null){
				logger.info("P020返回异常");
				retMap.put("isSuccess", "false");
				retMap.put("errorMessage", "系统异常");
				return JSONObject.toJSONString(retMap);
			}else{
				openId = tlbUser.getOpenid();
			}			
			//4.校验openId
			if(StringUtils.isEmpty(openId)){
				logger.info("openId不能为空");
				retMap.put("isSuccess", "false");
				retMap.put("errorMessage", "系统异常");
				return JSONObject.toJSONString(retMap);
			}
			//5.P001发送验证码									
			String res = p001SendVerifyCodeService.sendVerifyCode(bankCard, openId);
			logger.info("P001 sendVerifyCode result: {}", res);
			return res;	
		} catch (Exception e) {
			logger.error("发送手机验证码发生异常! Exception is :{}", e);	
			retMap.put("isSuccess", "false");
			retMap.put("errorMessage", "系统发生异常");
			String res = JSONObject.toJSONString(retMap);
			return res;
		}	
	}
	
	/**检验手机验证码
	 * 
	 * @param bankCard
	 * @return
	 */
	@RequestMapping(value="/checkVerifyCode", method=RequestMethod.POST)
	String checkVerifyCode(BankCard bankCard) {
				
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			//1.//参数校验
			logger.info("checkVerifyCode param: {}", bankCard);			
			Map<String, String> verifyResult = checkParamsOfCheckVerifyCode(bankCard);
			if(!MapUtils.isEmpty(verifyResult)){
				return JSONObject.toJSONString(verifyResult);
			}
			//2.P017校验验证码
			String res = p017CheckVerifyCodeService.checkVerifyCode(bankCard);
			logger.info("checkVerifyCode result: {}", res);
			return res;	
		} catch (Exception e) {
			logger.error("校验手机验证码发生异常! Exception is :{}", e); 		
			resultMap.put("isSuccess", "false");
			resultMap.put("errorMessage", "系统发生异常");
			String res = JSONObject.toJSONString(resultMap);
			return res;
		}		
	}
	
    /**快捷绑卡信息提交
     * 
     * @param bankCard
     * @param merchantNo
     * @param mobile
     * @return
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value="/bindCard", method=RequestMethod.POST)
	String bindCard(BankCard bankCard, String merchantNo, String mobile) {
    	
    	Map<String, Object> retMap = new HashMap<String, Object>();	
		Map<String, Object> responseBody = new HashMap<String, Object>();
    	try {
    		logger.info("bindCard param: {}", bankCard);
    		//1.参数校验
        	Map<String, String> verifyResult = checkParamsOfAddBankCard(bankCard, merchantNo, mobile);
        	if(!MapUtils.isEmpty(verifyResult)){
    			return JSONObject.toJSONString(verifyResult);
    		}  		
    		//2.身份证x大写
			bankCard.setCertNo(bankCard.getCertNo().toUpperCase());
    		//3.是否重复绑卡
    		int repCnt = idenverifyService.getCountOfRepeatCard(bankCard);
    		if(repCnt>0) {
    			retMap.put("isSuccess", "false");
    			retMap.put("errorMessage", "您已添加过此银行卡");
    			return JSONObject.toJSONString(retMap);
    		}
    		
			String openId ="";
			String ispassidenauth = "";
			String idenno = "";				
			//4.通过userId查询tlbuseroid
			String tlbUserOid = idenverifyService.getTlbUserOidByUserId(bankCard.getUserId());
			logger.info("getTlbUserOidByUserId result is tlbUserOid: {}", tlbUserOid);
			if(StringUtils.isEmpty(tlbUserOid)){
				logger.info("TlbUserOid不能为空");
				retMap.put("isSuccess", "false");
				retMap.put("errorMessage", "系统异常");
				return JSONObject.toJSONString(retMap);
			}			
			//5.P020通过tlbuseroid查询openid,身份证号
			P020TLBUser tlbUser = p020QueryOpenIdService.queryTlbUserInfo(tlbUserOid);
			if(tlbUser==null){
				logger.info("P020返回异常");
				retMap.put("isSuccess", "false");
				retMap.put("errorMessage", "系统异常");
				return JSONObject.toJSONString(retMap);
			}else{
				openId = tlbUser.getOpenid();
				ispassidenauth = tlbUser.getIspassidenauth();
				idenno = tlbUser.getIdenno()==null? "":tlbUser.getIdenno();
				logger.info("p020QueryOpenIdService result: openId: {}, ispassidenauth: {}, idenno: {}.",
						openId, ispassidenauth, idenno);
			}				
			//6.如果一代已经实名验证,且身份证号和填写的身份证号不相同,结束绑定
			if(!StringUtils.isEmpty(ispassidenauth) && "Y".equals(ispassidenauth)){
				if(!idenno.equals(bankCard.getCertNo())){
					logger.info("若已实名验证,身份证号和填写的身份证号不相同,结束绑定");
					retMap.put("isSuccess", "false");
					retMap.put("errorMessage", "非常抱歉,银行卡认证失败");
					return JSONObject.toJSONString(retMap);
				}
			}	
			//7.校验openId
			if(StringUtils.isEmpty(openId)){
				logger.info("openId不能为空");
				retMap.put("isSuccess", "false");
				retMap.put("errorMessage", "系统异常");
				return JSONObject.toJSONString(retMap);
			}					
			//8.互斥校验
			String checkRes = twoDFireMutexCheckService.mutexCheck(openId);
			logger.info("twoDFireMutexCheckService result is {}", checkRes);	
			if(StringUtils.isEmpty(checkRes)){
				retMap.put("isSuccess", "false");
				retMap.put("errorMessage", "调用互斥校验结果异常");
				return JSONObject.toJSONString(retMap);
			}
			Map<String, Object> checkMap = JSONObject.parseObject(checkRes, Map.class);
			String checkCode = (String) (checkMap.get("code")==null?"":checkMap.get("code"));
			//000000:互斥校验通过;000001:异常;其它:失败
			if(!"000000".equals(checkCode)){
				if("000001".equals(checkCode)){
					retMap.put("isSuccess", "false");
					retMap.put("errorMessage", "系统异常");
					return JSONObject.toJSONString(retMap);
				}else{
					logger.info("互斥校验失败");
					retMap.put("isSuccess", "false");
					retMap.put("errorMessage", "非常抱歉,银行卡认证失败");
					return JSONObject.toJSONString(retMap);
				}			
			}			  		  			
    		//9.C010银行卡查询
			String c010Res = c010QueryBankAcctService.queryBankAcct(openId);
			logger.info("c010 QueryBankAcctService result: {}", c010Res);  
			Map<String, Object> c010ResMap = JSONObject.parseObject(c010Res, Map.class);
			responseBody = (Map<String, Object>) c010ResMap.get("response");
			String c010IsSuccess = (String) c010ResMap.get("isSuccess");
			String c010Code = responseBody==null? null:(String)responseBody.get("code");
			List<Map<String, Object>> posBankList = responseBody==null? null:(List<Map<String, Object>>) responseBody.get("posbanklist");
			
			//10.C010: 1.若有银行卡->C014新卡快捷认证->loan.changeCard换卡      2.若无银行卡->C012;  
			if("true".equals(c010IsSuccess) && "W-Y000000".equals(c010Code)){		
				//1.有银行卡
				if(!CollectionUtils.isEmpty(posBankList)){
					//10.1.1 C014新卡快捷认证
					String c014Res = c014QuickAuthService.quickAuth(bankCard, openId);
					logger.info("c014QuickAuthService result: {}", c014Res);  
					Map<String, Object> c014ResMap = JSONObject.parseObject(c014Res, Map.class);
					responseBody = (Map<String, Object>) c014ResMap.get("response");
					String c014IsSuccess = (String) c014ResMap.get("isSuccess");
					String c014Code = responseBody==null? null:(String)responseBody.get("code");
					//新卡通过C014快捷认证
					if("true".equals(c014IsSuccess) && "W-Y000000".equals(c014Code)){
						//10.1.2二代服务loan.changeCard换卡
						Map<String, Object> oldPosBankCard = new HashMap<String, Object>();
						if(!CollectionUtils.isEmpty(posBankList)){
							oldPosBankCard = posBankList.get(0);
						}else{
							logger.info("oldPosBankCard不能为空!"); 
							retMap.put("isSuccess", "false");
							retMap.put("errorMessage", "系统异常");
							return JSONObject.toJSONString(retMap);	
						}
						String changeCardRes = g2ChangeCardService.changeCard(oldPosBankCard, bankCard);
						logger.info("g2ChangeCardService result: {}", changeCardRes);  
						Map<String, Object> changeCardResMap = JSONObject.parseObject(changeCardRes, Map.class);
						String resultCode = (String) changeCardResMap.get("resultCode");				
						if("W-Y000000".equals(resultCode)){		
							String result = (String) changeCardResMap.get("result");
							if ("0".equals(result)) {// 0,换卡通过；1，不通过
								logger.info("loan.changeCard 换卡成功!"); 
								responseBody.clear();
								responseBody.put("code", "W-Y000000");
								responseBody.put("message", "添加银行卡成功");
								retMap.put("response", responseBody);
								retMap.put("isSuccess", "true");
								//10.1.3保存绑卡信息
								logger.info("新银行卡绑定成功--开始保存绑卡信息到本地"); 
								saveBankCard(bankCard);
								//10.1.4保存账户信息
								logger.info("新银行卡绑定成功--开始保存账户信息到本地");
								saveProdAcct(bankCard, merchantNo, mobile);														
							}else{
								logger.info("loan.changeCard 换卡失败!"); 
								responseBody.clear();
								retMap.put("isSuccess", "false");
								responseBody.put("errorMessage", "非常抱歉,银行卡认证失败");								
							}
							return JSONObject.toJSONString(retMap);	
						} else {
							logger.info("masSend loan.changeCard 失败!"); 
							retMap.put("isSuccess", "false");
							retMap.put("errorMessage", "系统异常");
							return JSONObject.toJSONString(retMap);												
						}
					//新卡快捷认证未通过
					}else{
						return c014Res;
					}
				//2.无银行卡	
				}else{				
					//10.2.1 C012Pos快捷绑卡   
		    		String c012Res = c012BindCardService.bindCard(bankCard, openId);
		    		logger.info("c012BindCardService result: {}", c012Res);    				       		
		    		Map<String, Object> c012ResMap = JSONObject.parseObject(c012Res, Map.class);
		    		responseBody = (Map<String, Object>) c012ResMap.get("response");
		    		String c012IsSuccess = (String) c012ResMap.get("isSuccess");
		    		String c012Code = responseBody==null? null:(String)responseBody.get("code");
		    		//C012绑卡成功
					if("true".equals(c012IsSuccess) && "W-Y000000".equals(c012Code)){
						logger.info("新银行卡绑定成功--开始保存绑卡信息到本地"); 
						saveBankCard(bankCard);
						logger.info("新银行卡绑定成功--开始保存账户信息到本地");
						saveProdAcct(bankCard, merchantNo, mobile);
					}	    		
					return c012Res;
				}				
			//查询银行卡异常									
			}else{
				return c010Res;
			}
			    		    							
		} catch (Exception e) {
			logger.error("绑卡服务发生异常! Exception is :{}", e);  			
			retMap.put("isSuccess", "false");
			retMap.put("errorMessage", "系统发生异常");		
			String res = JSONObject.toJSONString(retMap);
			return res;
		}    	   	
	 }
    
    /**查询支持的银行卡列表
	 * 
	 * @param prodCode
	 * @return
	 */
	@RequestMapping(value="/supportCard", method=RequestMethod.POST)
	String querySupportCard(String prodCode) {	
				
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> responseBody = new HashMap<String, Object>();
		try {
			logger.info("querySupportCard param: {}", prodCode);
			//参数校验
			Map<String, String> verifyResult = checkParamsOfGetBankList(prodCode);
			if(!MapUtils.isEmpty(verifyResult)){
				return JSONObject.toJSONString(verifyResult);
			}	
			//根据产品code查询bank
			Map<String,Object> bankMap = idenverifyService.querySupportCard(prodCode);			
			if(bankMap!=null && !bankMap.isEmpty()){
				logger.info("查询支持的银行列表成功!");
				responseBody.put("supportBanks", bankMap);
				resultMap.put("isSuccess", "true");				
			}else{
				logger.error("查询支持的银行列表为空!");
				resultMap.put("isSuccess", "false");
				resultMap.put("errorMessage", "系统发生异常");
			}
			resultMap.put("response",responseBody);			
		} catch (Exception e) {
			logger.error("查询银行列表发生异常! Exception is :{}", e);		
			resultMap.put("errorMessage", "系统发生异常");
			resultMap.put("isSuccess", "false");
		}
		String res = JSONObject.toJSONString(resultMap);
		logger.info("querySupportCard result: {}", res);
		return res;
		
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
	 * @param bankCard
	 * @return
	 */
	private Map<String, String> checkParamsOfSendVerifyCode(BankCard bankCard){
		
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isEmpty(bankCard.getUserId())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "用户标识不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getHpNo())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "银行预留手机号不能为空");
		}
		if(StringUtils.isEmpty(bankCard.getReqType())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "验证码请求类型不能为空");
		}
		return checkMap;
	}
	
	/**校验验证码参数校验
	 * 
	 * @param bankCard
	 * @return
	 */
	private Map<String, String> checkParamsOfCheckVerifyCode(BankCard bankCard){
		
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isEmpty(bankCard.getHpNo())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "银行预留手机号不能为空");
		}		
		if(StringUtils.isEmpty(bankCard.getReqType())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "验证码请求类型不能为空");
		}		
		if(StringUtils.isEmpty(bankCard.getVerifyCode())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "验证码不能为空");
		}		
		if(StringUtils.isEmpty(bankCard.getVerifyCodeId())){
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
	private Map<String, String> checkParamsOfAddBankCard(BankCard bankCard, String merchantNo, String mobile){
		
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
	
	
	/**上传参数检查
	 * @param file
	 * @param idenFile
	 * @return
	 */
	private Map<String, String> checkParamsOfOcrVerify(String file, IdenFile idenFile){
		
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isEmpty(file)){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "文件上传不能为空");
		}
		if(StringUtils.isEmpty(idenFile.getUserId())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "用户标识不能为空");
		}
		if(StringUtils.isEmpty(idenFile.getProdCode())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "产品代码不能为空");
		}
		if(StringUtils.isEmpty(idenFile.getFileType())){
			checkMap.put("isSuccess", "false");
			checkMap.put("errorMessage", "文件类型不能为空");
		}
		if("1".equals(idenFile.getFileType())){
			if(StringUtils.isEmpty(idenFile.getIdenFace())){
				checkMap.put("isSuccess", "false");
				checkMap.put("errorMessage", "身份证正反面类型不能为空");
			}
		}				
		return checkMap;
	}
	
	/**保存绑卡信息到本地
	 * 
	 * @param bankCard
	 * @return
	 */
	private boolean saveBankCard(BankCard bankCard){	
		
		logger.info("-----saveBankCard start-----");
		logger.info("saveBankCard param: {}", bankCard);
		int cnt = idenverifyService.saveCard(bankCard);	        		
		logger.info("saveBankCard result(cnt): {}", cnt);      		   		
		if(cnt == 1){
			logger.info("保存绑卡信息到本地成功!");  
			return true;
		}else{
			logger.warn("保存绑卡信息到本地失败!");  
			return false;
		}		
	}
	
	/**保存账户信息到本地
	 * 
	 * @param bankCard
	 * @param merchantNo
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	private boolean saveProdAcct(BankCard bankCard, String merchantNo, String mobile) throws Exception{
		
		logger.info("-----saveProdAcct start-----");
		logger.info("saveProdAcct param:{},merchantNo:{},mobile:{}", bankCard, merchantNo, mobile);
		String saveProdRes = twoDFireUserProdAcctService.saveProdBank(bankCard, merchantNo, mobile);  		
		logger.info("saveProdAcct result: {}", saveProdRes);    		
		if(saveProdRes != null && !"".equals(saveProdRes) 
				&& "000000".equals(JSONObject.parseObject(saveProdRes, Map.class).get("code"))){
			logger.info("保存账户信息到本地成功!");  
			return true;
		}else{
			logger.info("保存账户信息到本地失败!"); 
			return false;
		}
		
	}
              
}
