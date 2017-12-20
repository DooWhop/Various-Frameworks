package com.allinpal.twodfireservice.notify.service;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.notify.domain.DeductLoanUseReq;
import com.allinpal.twodfireservice.notify.domain.LoanRate;
import com.allinpal.twodfireservice.notify.domain.Notify;
import com.allinpal.twodfireservice.notify.domain.ProdAcctInfo;
import com.allinpal.twodfireservice.notify.domain.TouKongInfo;
import com.allinpal.twodfireservice.notify.domain.TouKongMessage;
import com.allinpal.twodfireservice.notify.domain.TouKongRepayPlan;
import com.allinpal.twodfireservice.notify.enums.MjmiId;
import com.allinpal.twodfireservice.notify.mapper.DeductMapper;
import com.allinpal.twodfireservice.notify.mapper.TouKongMapper;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;

/**
 * @author admin
 * 
 */
@RestController
@RequestMapping("toukongService")
public class TouKongService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String OVERALL_STATUS_SUCCESS = "01";//01：成功
	
	private static final String OVERALL_STATUS_FAILURE = "03";//03：失败
	
	private static final String RET_CODE_SUCCESS = "000000000000";
	
	private static final String ID_TYP_CD= "0";//0-身份证
	
	private static final String LOAN_USE_REQ_STATUS_LOAN = "4";
	
	private static final String LOAN_USE_REQ_STATUS_FAILURE= "11";
	
	@Autowired
	private TouKongMapper touKongMapper;
	
	@Autowired
	private DeductMapper deductMapper;
	
	@Autowired
	public AppConfig appConfig;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${mas.sendV2.url}")
	private String masSendUrl;
	
	public void doToukong(Notify notify){
		logger.info("ToukongService method doToukong param is  {}", JSON.toJSONString(notify));
	
		if(OVERALL_STATUS_SUCCESS.equals(notify.getOverall_status())){
			
			List<TouKongInfo> list = touKongMapper.queryCreditInfo(notify);
			
			if(list == null || list.size() == 0){
				logger.info("loanCreditReq is null or empty!");
				return;
			}
			
			TouKongInfo bean = list.get(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String workDate = sdf.format(new Date());
			bean.setWorkDate(workDate);
			//用户信息上送
//			boolean ret = false;
			if(bean.getClientNo() == null || "".equals(bean.getClientNo())){
				this.toukong1001(bean);	
			}
//			if((bean.getClientNo()!=null && !"".equals(bean.getClientNo())) && (bean.getBillNo() == null || "".equals(bean.getBillNo()))){
//				this.toukong2001(bean);
//			}
			if(bean.getClientNo()!=null && !"".equals(bean.getClientNo())){
				this.toukong2001(bean);
			}
			if(bean.getBillNo() !=null && !"".equals(bean.getBillNo())){
				this.toukong2002(bean);
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertToukongMessage(TouKongMessage toukongMessage) {
		logger.info("ToukongService method insertToukongMessage is start!");
		int cnt = touKongMapper.insertToukongMessage(toukongMessage);
		if(cnt != 1){
			throw new RuntimeException("更新t_toukong_message数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProdAcctInfo(ProdAcctInfo prodAcctInfo) {
		logger.info("ToukongService method updateProdAcctInfo is start!");
		int cnt = touKongMapper.updateProdAcctInfo(prodAcctInfo);
		if(cnt != 1){
			throw new RuntimeException("更新t_prod_acct数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertRepayPlan(List<TouKongRepayPlan> list, TouKongInfo bean) {
		logger.info("ToukongService method insertRepayPlan is start!");
		//先删除当前支用的还款计划
		TouKongRepayPlan paramter = new TouKongRepayPlan();
		paramter.setUseRecordNo(bean.getUseRecordNo());
		paramter.setProdCode(bean.getProdCode());
		
		int count = touKongMapper.queryRepayPlanCount(paramter);
		
		if(count > 0){
			touKongMapper.deleteRepayPlan(paramter);
		}
		
		int cnt = 0;
		for(int i = 0 ; i < list.size(); i++){
			cnt = touKongMapper.insertRepayPlan(list.get(i));
			if(cnt != 1){
				throw new RuntimeException("更新t_loan_repay_plan数据(cnt: "+cnt+")将回滚!");
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateLoanUseReq(DeductLoanUseReq deductLoanUseReq) {
		logger.info("ToukongService method updateLoanUseReq is start!");
		int cnt  = touKongMapper.updateLoanUseReq(deductLoanUseReq);
		if(cnt != 1){
			throw new RuntimeException("更新t_loan_repay_plan数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	/**
	 * 用户信息上送
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	private boolean toukong1001(TouKongInfo bean){
		logger.info("ToukongService method toukong1001 is start! param is  {}", JSON.toJSONString(bean));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		boolean ret = false;
		MultiValueMap<String, String> multiValueMap =  new LinkedMultiValueMap<String, String>();
		JSONObject json = new JSONObject();
		String retCode = null;
		String retMsg = null;
		try{
			
			String transSn = GenerateIdsUtil.generateId(appConfig.getAppIp());
			String transCode = "1001";
			multiValueMap.add("topicName", "coAgency.tonghuaTouKong");
			json.put("TRANS_CODE", transCode);
			json.put("TRANS_SN", transSn);
			json.put("CO_ORG_CODE", "903");
			
			json.put("WorkDate", bean.getWorkDate());
			json.put("ClientName", bean.getCustName());
			json.put("IdTpCd", "0");
			json.put("IdNum", bean.getCertNo().toUpperCase());
			json.put("MobileNum", bean.getMobile());
//			json.put("CountryTpCd", "");//国别代码
//			json.put("ResidentTpCd", "");//居民类型
//			int sex = Integer.parseInt(bean.getCertNo().substring(16,17));
//			json.put("GenderTpCd", sex % 2 == 0 ? "2":"1");//性别 1-男 2-女 0-未知  9-未说明
			String birthday = bean.getCertNo().substring(6,14);
			json.put("BirthDt",  sdf.format(sdf1.parse(birthday)));//出生日期
//			json.put("IdExpiryDt", "");//证件有效期限
//			json.put("IssueZoneTpCd", "");//发证机关地点
//			json.put("AdrUsageTpCd", "");//地址类型
//			json.put("Address", bean.getLiveCity().concat(bean.getLiveAddr()));//详细地址
//			json.put("PostCode", "");//邮政编码
//			json.put("CtcmthTpCd", "");//联系类型
//			json.put("CtcmthNum", "");//联系信息
//			json.put("CreateInst", "");//建立机构
//			json.put("BelongToInst", "");//归属机构
			multiValueMap.add("messgeJsonBody", json.toJSONString());
			String jsonStr = URLDecoder.decode(restTemplate.postForObject(masSendUrl, multiValueMap, String.class), "UTF-8");;
			logger.info("coAgency.tonghuaTouKong's  1001 result is {}", jsonStr);
			if(jsonStr == null || "".equals(jsonStr)){
				logger.info("ToukongService method toukong1001 coAgency.tonghuaTouKong 's return result is null or empty");
			}else{
				Map<String,Object> retMap =  (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
				if("0000".equals((String)retMap.get("RET_CODE"))){
					Map<String,Object> resultMap = (Map<String, Object>)retMap.get("RESULT_DATA");
					retCode = (String)resultMap.get("RetCode");
					retMsg = (String)resultMap.get("ReturnMsg");
					
					//判断返回结果
					if(RET_CODE_SUCCESS.equals(retCode)){
						ret = true;
						String clientNo = (String)resultMap.get("ClientNo");
						bean.setClientNo(clientNo);
						//查询贷款账户表
						List<ProdAcctInfo> prodAcctList = touKongMapper.queryProdAcctInfo(bean);
						if(prodAcctList == null || prodAcctList.size() == 0){
							logger.info("ToukongService method toukong1001 prodAcctList is null! acctId is {}",bean.getAcctId());
						}else{
							ProdAcctInfo prodAcctInfo = prodAcctList.get(0);
							prodAcctInfo.setClientNo(clientNo);
							prodAcctInfo.setLastModifyUid("system");
							prodAcctInfo.setLastModifyTime(System.currentTimeMillis());
							//更新贷款账户表
							this.updateProdAcctInfo(prodAcctInfo);
						}
					}
				}
			}
			
			//记录投控交互信息
			TouKongMessage toukongMessage = new TouKongMessage();
			toukongMessage.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
			toukongMessage.setOrigRecordNo(bean.getRecordNo());//授信编号
			toukongMessage.setTransCode(transCode);
			toukongMessage.setTransSn(transSn);
			toukongMessage.setRetCode(retCode);
			toukongMessage.setRetMsg(retMsg);
			toukongMessage.setReqContent(json.toJSONString());
			toukongMessage.setRespContent(jsonStr);
			toukongMessage.setCreateUid("system");
			toukongMessage.setCreateTime(System.currentTimeMillis());
			toukongMessage.setLastModifyUid("system");
			toukongMessage.setLastModifyTime(System.currentTimeMillis());
			
			this.insertToukongMessage(toukongMessage);
			
		}catch(Exception e){
			logger.error("ToukongService method toukong1001 has an exception! creditRecordNo is {}, Exception is {}",bean.getRecordNo(),e);
		}
		return ret;
	}
	
	/**
	 * 生成还款计划
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	private boolean toukong2002(TouKongInfo bean){
		logger.info("ToukongService method toukong2002 (query repayPlan) is start! param is  {}", bean);
		boolean ret = false;
		MultiValueMap<String, String> multiValueMap =  new LinkedMultiValueMap<String, String>();
		JSONObject json = new JSONObject();
		String retCode = null;
		String retMsg = null;
		try{
			String transSn = GenerateIdsUtil.generateId(appConfig.getAppIp());
			String transCode = "2002";
			multiValueMap.add("topicName", "coAgency.tonghuaTouKong");
			json.put("TRANS_CODE", transCode);
			json.put("TRANS_SN", transSn);
			json.put("CO_ORG_CODE", "903");
			
			json.put("WorkDate", bean.getWorkDate());//渠道工作日期yyyy-MM-dd
			json.put("IouID", bean.getBillNo());//借据号
			multiValueMap.add("messgeJsonBody", json.toJSONString());
			
			String jsonStr = URLDecoder.decode(restTemplate.postForObject(masSendUrl, multiValueMap, String.class), "UTF-8");;
			logger.info("coAgency.tonghuaTouKong's 2002 result is {}", jsonStr);
			if(jsonStr == null || "".equals(jsonStr)){
				logger.info("ToukongService method toukong2002 coAgency.tonghuaTouKong 's return result is null or empty");
			}else{
				Map<String,Object> retMap =  (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
				if("0000".equals((String)retMap.get("RET_CODE"))){
					Map<String,Object> resultMap = (Map<String, Object>)retMap.get("RESULT_DATA");
					retCode = (String)resultMap.get("RetCode");
					retMsg = (String)resultMap.get("ReturnMsg");

					if(RET_CODE_SUCCESS.equals(retCode)){
						ret = true;
						//保存还款计划
						List<TouKongRepayPlan> list = new ArrayList<TouKongRepayPlan>();
						
						TouKongRepayPlan repayPlan = null;
						
						List<Map<String,Object>> amortSchedList = (List<Map<String,Object>>)resultMap.get("AmortSchedList");
						BigDecimal principalAmt = null;
						BigDecimal interestAmt = null;
						if(amortSchedList != null && amortSchedList.size() > 0){
							for(Map<String,Object> amortSched : amortSchedList){
								repayPlan = new TouKongRepayPlan();
								
								repayPlan.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
								repayPlan.setProdCode(bean.getProdCode());
								repayPlan.setUseRecordNo(bean.getUseRecordNo());//支用流水号
								repayPlan.setStartDate((String)amortSched.get("StartDate"));//起始日期
								repayPlan.setPayDate((String)amortSched.get("DueDate"));//还款日
								repayPlan.setTpnum(Integer.parseInt((String)amortSched.get("CurrTerm")));//CurrTerm
								principalAmt = amortSched.get("PrincipalAmt") == null ? new BigDecimal(0.00): new BigDecimal((String)amortSched.get("PrincipalAmt"));
								repayPlan.setPayPrinamt(principalAmt);//本金金额,2位小数
								interestAmt = amortSched.get("InterestAmt") == null ? new BigDecimal(0.00): new BigDecimal((String)amortSched.get("InterestAmt"));
								repayPlan.setPayInteamt(interestAmt);//利息金额,2位小数
								repayPlan.setStartBalamt(new BigDecimal((String)amortSched.get("StartBalAmt")));//期初余额,2位小数
								repayPlan.setEndBalamt(new BigDecimal((String)amortSched.get("StartBalAmt")));//期末余额,2位小数
								repayPlan.setChargeAmt(new BigDecimal((String)amortSched.get("ChargeAmt")));//应收费用,2位小数
								repayPlan.setPayTotamt(principalAmt.add(interestAmt));
								repayPlan.setCreateUid("system");
								repayPlan.setCreateTime(System.currentTimeMillis());
								repayPlan.setLastModifyUid("system");
								repayPlan.setLastModifyTime(System.currentTimeMillis());
								
								list.add(repayPlan);
							}
							
							//insert
							this.insertRepayPlan(list,bean);
						}
					}
				}
			}
			
			//记录投控交互信息
			TouKongMessage toukongMessage = new TouKongMessage();
			toukongMessage.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
			toukongMessage.setOrigRecordNo(bean.getRecordNo());//授信编号
			toukongMessage.setTransCode(transCode);
			toukongMessage.setTransSn(transSn);
			toukongMessage.setRetCode(retCode);
			toukongMessage.setRetMsg(retMsg);
			toukongMessage.setReqContent(json.toJSONString());
			toukongMessage.setRespContent(jsonStr);
			toukongMessage.setCreateUid("system");
			toukongMessage.setCreateTime(System.currentTimeMillis());
			toukongMessage.setLastModifyUid("system");
			toukongMessage.setLastModifyTime(System.currentTimeMillis());
			
			this.insertToukongMessage(toukongMessage);
			
		}catch(Exception e){
			logger.error("ToukongService method toukong2002 has an exception! creditRecordNo is {}, Exception is {}",bean.getRecordNo(),e);
		}
		return ret;
	}
	
	/**
	 * 贷款信息上送
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	private boolean toukong2001(TouKongInfo bean){
		logger.info("ToukongService method toukong2001 is start! param is  {}", bean);
		boolean ret = false;
		MultiValueMap<String, Object> multiValueMap =  new LinkedMultiValueMap<String, Object>();
		JSONObject json = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String retCode = null;
		String retMsg = null;
		try{
			
			//查询年利率
			LoanRate loanRateParam = new LoanRate();
			loanRateParam.setProdCode(bean.getProdCode());
			loanRateParam.setApplTerm(bean.getApplTerm().toUpperCase());
			loanRateParam.setStatus("1");//生效
			List<LoanRate> loanRateList = touKongMapper.queryLoanRate(loanRateParam);
			if(loanRateList == null || loanRateList.size()==0){
				logger.info("ToukongService method toukong2001,query loanRateList is null! credit recordNo is {}", bean.getRecordNo());
				return ret;
			}
			LoanRate loanRateInfo = loanRateList.get(0);
			
			String transSn = GenerateIdsUtil.generateId(appConfig.getAppIp());
			String transCode = "2001";
			multiValueMap.add("topicName", "coAgency.tonghuaTouKong");
			json.put("TRANS_CODE", transCode);
			json.put("TRANS_SN", transSn);
			json.put("CO_ORG_CODE", "903");
			
			String ReqTxnid = GenerateIdsUtil.generateId(appConfig.getAppIp());
			json.put("WorkDate", bean.getWorkDate());//渠道工作日期yyyy-MM-dd
			json.put("ReqTxnid", ReqTxnid);//请求流水,随机生成
			json.put("CustomerNbr", bean.getClientNo());//客户号
			json.put("CustomerName", bean.getCustName());//客户姓名
			json.put("IdTypcd", ID_TYP_CD);//证件类型
			json.put("Id", bean.getCertNo());//证件号
//			json.put("MgtMainBranchNbr", "");//主办行法人号
//			json.put("GlSeqNo", "");//记账批次号
//			json.put("ContractId", "");//合同号
//			json.put("IouID", "");//外部借据号
//			json.put("OrderID", "");//订单号
			json.put("Ccy", "156");//币种
//			json.put("ContractAmt", bean.getApplAmt());//合同额度
//			json.put("ContractStartDate", "");//合同开始日期
//			json.put("ContractEndDate", "");//合同结束日期
//			json.put("DisbEndDate", "");//发放截止日期
//			json.put("RevolveLimitYN", "");//是否循环
			json.put("TranAmt", bean.getAppAmt().toString());//放款金额
			json.put("WaveDays", "0");//免息天数
			Date openDate =new Date(bean.getOpenDate());
			json.put("OpenDate", sdf.format(openDate));//开户日期
//			json.put("OpenDate", "2017-12-15");//开户日期
//			json.put("DateMat", "");//放款到期日期
			String applTerm = bean.getApplTerm().toUpperCase();
			json.put("Term", applTerm);//期数
//			json.put("FirstDueDate", "");//首次还本付息日期
//			json.put("PmtDueDay", "");//固定还本付息日
			json.put("ChanDisbDate", sdf.format(new Date()));//渠道放款日期:当前时间
//			json.put("ChanDisbDate", "2017-12-15");//渠道放款日期:当前时间
			BigDecimal loanRate = loanRateInfo.getLoanRate();
			json.put("LoanRate", loanRate.toString());//执行年利率
			BigDecimal odRate = loanRate.multiply(new BigDecimal(0.15)).setScale(4,BigDecimal.ROUND_HALF_UP);
			json.put("OdpRate", odRate.toString());//罚息年利率
			json.put("OdiRate", odRate.toString());//复利年利率
//			json.put("MjAcctTypCd", "");//产品大类代码
//			json.put("MiAcctTypCd", "");//产品小类代码
			json.put("MjmiId", MjmiId.getRetValue(bean.getApplTerm().toUpperCase()));//产品编号
			json.put("PmtSched", "EFBI");//还款方式   EFB-等额本金  EFBI-等额本息  SISP-一次性还本付息 MISP-多次付息一次还本
			json.put("PrinCalperiodCd", "1M");//本金还款频率。贷款连续两次还款时间的间隔。
			json.put("IntCalperiodCd", "1M");//利息还款频率。贷款连续两次还款时间的间隔。
//			json.put("GraceDays", "");//宽限天数
			json.put("DayMethCd", "30DM");//计息天数代码
//			json.put("DepAcctNbr", "");//放款入账账号
//			json.put("WthAcctNbr", "");//还款账号
//			json.put("HoldMediId", "");//质押介质号
//			json.put("HoldAcctNbr", "");//质押账号
//			json.put("HoldSeqNbr", "");//冻结编号
//			json.put("ThirdpartMediid", "");//第三方账户介质号
//			json.put("SoldProdId", "");//可售产品编号
			json.put("ChannalID", "zc10001");//渠道号
//			json.put("RtxnNote", "");//客户备注
//			json.put("PostScript", "");//交易附言
			json.put("CollateralTypCD", "4");//担保方式 4-信用
			json.put("LowRiskYn", "1");//是否低风险0否、1是
//			json.put("SystemId", "");//系统编号
			
			//PartInfo
//			List<Map<String,Object>> partInfoList = new ArrayList<Map<String,Object>>();
//			Map<String,Object> partInfo = new HashMap<String,Object>();
//			
//			partInfoList.add(partInfo);
//			json.put("PartInfo", partInfoList);
			multiValueMap.add("messgeJsonBody", json.toJSONString());
			
			String jsonStr = URLDecoder.decode(restTemplate.postForObject(masSendUrl, multiValueMap, String.class), "UTF-8");;
			logger.info("coAgency.tonghuaTouKong's  2001 result is {}", jsonStr);
			if(jsonStr == null || "".equals(jsonStr)){
				logger.info("ToukongService method toukong2001 coAgency.tonghuaTouKong 's return result is null or empty");
			}else{
				Map<String,Object> retMap =  (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
				if("0000".equals((String)retMap.get("RET_CODE"))){
					Map<String,Object> resultMap = (Map<String, Object>)retMap.get("RESULT_DATA");
					retCode = (String)resultMap.get("RetCode");
					retMsg = (String)resultMap.get("ReturnMsg");
					
					//判断返回结果
					if(RET_CODE_SUCCESS.equals(retCode)){
						ret = true;
						
						String iouId = (String)resultMap.get("IouId");
						bean.setBillNo(iouId);
						//更新支用信息 --借据编号
						DeductLoanUseReq deductLoanUseReq = new DeductLoanUseReq();
						deductLoanUseReq.setRecordNo(bean.getUseRecordNo());
						deductLoanUseReq.setBillNo(iouId);
						deductLoanUseReq.setLastModifyUid("system");
						deductLoanUseReq.setLastModifyTime(System.currentTimeMillis());
						
						this.updateLoanUseReq(deductLoanUseReq);
					}
				}
			}
			
			//记录投控交互信息
			TouKongMessage toukongMessage = new TouKongMessage();
			toukongMessage.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
			toukongMessage.setOrigRecordNo(bean.getRecordNo());//授信编号
			toukongMessage.setTransCode(transCode);
			toukongMessage.setTransSn(transSn);
			toukongMessage.setRetCode(retCode);
			toukongMessage.setRetMsg(retMsg);
			toukongMessage.setReqContent(json.toJSONString());
			toukongMessage.setRespContent(jsonStr);
			toukongMessage.setCreateUid("system");
			toukongMessage.setCreateTime(System.currentTimeMillis());
			toukongMessage.setLastModifyUid("system");
			toukongMessage.setLastModifyTime(System.currentTimeMillis());
			
			this.insertToukongMessage(toukongMessage);
		}catch(Exception e){
			logger.error("ToukongService method toukong2001 has an exception! creditRecordNo is {}, Exception is {}",bean.getRecordNo(),e);
		}
		return ret;
	}
}
