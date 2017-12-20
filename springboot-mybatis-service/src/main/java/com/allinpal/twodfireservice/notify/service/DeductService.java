package com.allinpal.twodfireservice.notify.service;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.AppConfig;
import com.allinpal.twodfireservice.notify.domain.DeductChargeReg;
import com.allinpal.twodfireservice.notify.domain.DeductLimit;
import com.allinpal.twodfireservice.notify.domain.DeductLoanUseReq;
import com.allinpal.twodfireservice.notify.domain.DeductSplit;
import com.allinpal.twodfireservice.notify.domain.Notify;
import com.allinpal.twodfireservice.notify.mapper.DeductMapper;
import com.allinpal.twodfireservice.util.GenerateIdsUtil;

/**
 * @author admin
 * 扣款service
 */
@Service("deductService")
public class DeductService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//状态,1 - 审批中2 - 审批失败3 - 审批通过4 - 已放款5 - 已结清6 - 逾期7 - 审批作废8 - 贷款作废9 - 已签订10 - 通华代偿09-通华不准入11 - 放款失败（张家口银行卡放款失败、超级网银放款全部失败） 12 -放款状态未知
	private static final String LOAN_USE_REQ_STATUS_SIGN = "9";
	
	private static final String LOAN_USE_REQ_STATUS_LOAN = "4";
	
	private static final String LOAN_USE_REQ_STATUS_FAILURE= "11";
	
	private static final String LOAN_USE_REQ_STATUS_UNKNOW= "12";
	
	private static final String CHARGE_REG_PROC_STATE_INIT= "00";
	
	private static final String CHARGE_REG_PROC_STATE_SUCCESS = "10";
	
	private static final String CHARGE_REG_PROC_STATE_FAILURE= "20";
	
	private static final String CHARGE_REG_PROC_STATE_UNKNOW= "30";
	
	private static final String OVERALL_STATUS_SUCCESS = "01";//01：成功
	
	private static final String OVERALL_STATUS_FAILURE = "03";//03：失败
	
	private static final String OVERALL_STATUS_UNKNOW = "02";//02：处理中
	
	private static final String IPP_VERSION = "v1.0";
	private static final String IPP_BUSINESS_TYPE = "02";
	private static final String IPP_PAY_TYPE = "09";
	private static final String IPP_TRANS_CODE = "2001";//
	private static final String IPP_CERT_TYPE = "01";
	private final String PROD_CODE_2FIRE = "9002000003";
	
	private static final String DEDUCT_RESULT_SUCCESS = "000000";
	private static final String DEDUCT_RESULT_WAIT_STILL = "100000";//
	private static final String DEDUCT_RESULT_PROCESSING = "200000";//交易进行中
	private static final String DEDUCT_RESULT_WAIT = "42003"; //交易机构交易结果未知
	private static final String DEDUCT_RESULT_UNKNOW_TRADE_STATE = "00002"; //未知的交易处理异常
	private static final String DEDUCT_RESULT_ERROR_DEAL_MSG = "00003"; //创建交易结果报文异常
	private static final String DEDUCT_RESULT_NUM_UNMATCH = "42001"; //交易机构应答报文中订单号与原交易不匹配
	private static final String DEDUCT_RESULT_AMT_UNMATCH = "42002"; //交易机构应答报文中金额与原交易不匹配
	private static final String DEDUCT_RESULT_CHECK_FAILED = "42005"; //交易机构应答报文验证签名失败
	private static final String DEDUCT_RESULT_TIMEOUT = "40014"; //交易未完成 银行通讯未超时响应受理状态
	
	private static final SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat formatTime = new SimpleDateFormat("HHmmss");
	
	@Autowired
	private DeductMapper deductMapper;
	
	@Autowired
	public AppConfig appConfig;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${notify.tpp.agent.id}")
	private String agentId;
	
	@Value("${notify.tpp.merchantno.2dfire}")
	private String merchantNo;
	
	@Value("${notify.tpp.withholding.notice.url}")
	private String withholdingNoticeUrl;
	
	@Value("${notify.tpp.withholding.query.url}")
	private String withholdingQueryUrl;
	
	public void deductCharge(Notify notify){
		logger.info("DeductService method deductCharge param is  {}", JSON.toJSONString(notify));
		try{
			BigDecimal payment_amount = new BigDecimal(notify.getPayment_amount()).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//查询支用信息和扣款信息
			DeductLoanUseReq deductLoanParam = new DeductLoanUseReq();
			deductLoanParam.setRecordNo(notify.getApp_seq());
			List<DeductLoanUseReq> loanUseReqList = deductMapper.queryLoanUseReq(deductLoanParam);
			
			if(loanUseReqList == null || loanUseReqList.size() == 0){
				logger.info("DeductService method deductCharge, loanUseReqList is null or empty!");
				return;
			}
			
			DeductLoanUseReq loanUseReq = loanUseReqList.get(0);
			String loan_status = loanUseReq.getStatus();
			//判断支用状态,只有为只用成功或者放款状态未知时，才能操作
			if(!(LOAN_USE_REQ_STATUS_SIGN.equals(loan_status) || LOAN_USE_REQ_STATUS_UNKNOW.equals(loan_status))){
				logger.info("DeductService method deductCharge, loanUseReq'status is not 9 or 12, loan_status is {}!",loan_status);
				return;
			}
		
			DeductChargeReg chargeParam = new DeductChargeReg();
			chargeParam.setUseRecordNo(loanUseReq.getRecordNo());
			chargeParam.setCardStatus("1");//生效
			List<DeductChargeReg> chargeList = deductMapper.queryDeductChargeReg(chargeParam);
			DeductChargeReg chargeReg = null;
			if(chargeList == null || chargeList.size() == 0){
				logger.info("chargeList is null or empty! UseRecordNo is {}! start to insert chargeRegInfo",notify.getApp_seq());
				return;
//				chargeReg = new DeductChargeReg();
//				chargeReg.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
//				chargeReg.setUseRecordNo(loanUseReq.getRecordNo());//支用流水号
//				chargeReg.setAcctId(loanUseReq.getAcctId());//贷款账户id
//				chargeReg.setCurDate(formatDate.format(new Date()));//日期
//				chargeReg.setBaseAmount(loanUseReq.getAppAmt());
//				if ("6M".equals(loanUseReq.getApplTerm().toUpperCase())) {
//					chargeReg.setServiceRate(new BigDecimal(0.02));
//					chargeReg.setManagerRate(new BigDecimal(0.02));
//				} else {
//					chargeReg.setServiceRate(new BigDecimal(0.03));
//					chargeReg.setManagerRate(new BigDecimal(0.03));
//				}
//				chargeReg.setServiceFee(loanUseReq.getServiceFee());
//				chargeReg.setManagerFee(loanUseReq.getManagerFee());
//				BigDecimal tolamount = loanUseReq.getServiceFee().add(loanUseReq.getManagerFee());
//				chargeReg.setTolamount(tolamount);
//				chargeReg.setProcState("00");
//				chargeReg.setCreateUid("system");
//				chargeReg.setCreateTime(System.currentTimeMillis());
//				chargeReg.setLastModifyUid("system");
//				chargeReg.setLastModifyTime(System.currentTimeMillis());
//				
//				//新增费用登记表
//				this.insertChargeReg(chargeReg);
			}
			chargeReg = chargeList.get(0);
			
			//更新支用信息表，保存放款状态
			if(OVERALL_STATUS_SUCCESS.equals(notify.getOverall_status())){
				//放款成功
				loanUseReq.setNewStatus(LOAN_USE_REQ_STATUS_LOAN);
				String loanDate = loanUseReq.getLoanDate();
				if(loanDate == null || "".equals(loanDate)){
					loanUseReq.setLoanDate(sdf.format(new Date()));
				}
				//比较下放款金额是否与支用金额一致
				if(payment_amount.compareTo(loanUseReq.getAppAmt()) != 0){
					loanUseReq.setDescrible("放款通知中返回的放款金额与支用金额不一致！ 本次放款通知中本步骤放款结果金额为：" + payment_amount);
				}
				loanUseReq.setOldLastModifyTime(loanUseReq.getLastModifyTime());
				loanUseReq.setLastModifyUid("system");
				loanUseReq.setLastModifyTime(System.currentTimeMillis());
				this.updateLoanUseReq(loanUseReq);
				
				
				//拆分扣款
				//更新手续费表为扣款中
//				chargeReg.setNewProcState("30");//扣款中
//				chargeReg.setLastModifyUid("system");
//				chargeReg.setLastModifyTime(System.currentTimeMillis());
//				this.updateChargeReg(chargeReg);
				
				//生成扣款明细
				List<DeductSplit> deductSplitList = this.createDeductSplit(chargeReg);
				this.insertDeductSplitList(deductSplitList);
				
				//请求TPP扣款
				boolean deductRet = this.deduct(deductSplitList);
				if(deductRet){
					chargeReg.setNewProcState("10");//扣款成功 00 - 初始状态 10 - 成功 20 - 失败
				}else{
					chargeReg.setNewProcState("20");
				}
				//更新费用登记表
				int deductCount = chargeReg.getDeductCount();
				chargeReg.setDeductCount(deductCount + 1);
				chargeReg.setLastModifyTime(chargeReg.getLastModifyTime());
				chargeReg.setLastModifyUid("system");
				chargeReg.setLastModifyTime(System.currentTimeMillis());
				
				this.updateChargeReg(chargeReg);
				
			}else if(OVERALL_STATUS_FAILURE.equals(notify.getOverall_status())){
				//放款失败
				loanUseReq.setNewStatus(LOAN_USE_REQ_STATUS_FAILURE);
				loanUseReq.setDescrible(notify.getResponse_message());
				loanUseReq.setLastModifyUid("system");
				loanUseReq.setLastModifyTime(System.currentTimeMillis());
				this.updateLoanUseReq(loanUseReq);
			}else if(OVERALL_STATUS_UNKNOW.equals(notify.getOverall_status())){
				//放款未知状态
				loanUseReq.setNewStatus(LOAN_USE_REQ_STATUS_UNKNOW);
				loanUseReq.setDescrible(notify.getResponse_message());
				loanUseReq.setLastModifyUid("system");
				loanUseReq.setLastModifyTime(System.currentTimeMillis());
				this.updateLoanUseReq(loanUseReq);
			}
		}catch(Exception e){
			// 扣款失败
			logger.error("DeductService method deductCharge has an exception! creditRecordNo is {}, Exception is {}",notify.getApp_seq(),e);
		}
	}
	
//	public void dealUnkownDeductState(){
//		//未知状态扣款处理
//		//查询未知状态的
//		DeductSplit deductSplitParam = new DeductSplit();
//		deductSplitParam.setProcState("70");//未知状态
//		List<DeductSplit> deductSplitList = deductMapper.queryDeductSplitByState(deductSplitParam);
//		
//		if(deductSplitList == null || deductSplitList.size() == 0){
//			logger.info("DeductService method [dealUnkownDeductState]  deductSplitList is null or size = 0 !");
//			return;
//		}
//		List<DeductSplit> list = null;
//		DeductChargeReg paramter = null;
//		List<DeductChargeReg> chargeRegList = null;
//		DeductChargeReg chargeReg = null;
//		for(DeductSplit deductSplit : deductSplitList){
//			//查询拆分代扣明细
//			deductSplitParam = new DeductSplit();
//			deductSplitParam.setOrigRecordNo(deductSplit.getOrigRecordNo());
//			deductSplitParam.setNotProcState("99");
//			deductSplitParam.setProdCode(deductSplit.getProdCode());
//			deductSplitParam.setSortField("split_num");
//			list = deductMapper.queryDeductSplitByState(deductSplit);
//			if(list == null || list.size() == 0){
//				continue;
//			}
//			boolean result = this.doDealDeductState(list);
//			
//			if(result){
//				//所有扣款明细都扣款成功
//				paramter = new DeductChargeReg();
//				paramter.setRecordNo(deductSplit.getOrigRecordNo());
//				chargeRegList = deductMapper.queryDeductChargeRegByUseRecNo(paramter);
//				
//				if(chargeRegList == null || chargeRegList.size() == 0){
//					logger.info("chargeReg info is null , deductSplit's recordNo is {}, deductSplit's origRecordNo is {}", deductSplit.getRecordNo(),deductSplit.getOrigRecordNo());
//					continue;
//				}
//				chargeReg = chargeRegList.get(0);
//				chargeReg.setNewProcState("10");
//				chargeReg.setLastModifyTime(chargeReg.getLastModifyTime());
//				chargeReg.setLastModifyUid("system");
//				chargeReg.setLastModifyTime(System.currentTimeMillis());
//				
//				this.updateChargeReg(chargeReg);
//	            logger.info("chargeReg result is success , chargeReg's recordNo is {}:", chargeReg.getRecordNo());
//			}
//		}
//	}
	
	/**
	 * 信托代扣，收到放款通知，更新支用表/服务费管理费费用表(t_charge_reg)状态
	 * @param notify
	 */
	public boolean updateLoanResult(Notify notify){
		logger.info("DeductService method updateLoanResult param is  {}", JSON.toJSONString(notify));
		boolean ret = false;
		try{
			String 	payment_no = notify.getPayment_no();
			BigDecimal payment_amount = new BigDecimal(notify.getPayment_amount()).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
			if("1".equals(payment_no)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				//查询支用信息和扣款信息
				DeductLoanUseReq deductLoanParam = new DeductLoanUseReq();
				deductLoanParam.setRecordNo(notify.getApp_seq());
				List<DeductLoanUseReq> loanUseReqList = deductMapper.queryLoanUseReq(deductLoanParam);
				
				if(loanUseReqList == null || loanUseReqList.size() == 0){
					logger.info("DeductService method updateLoanResult, loanUseReqList is null or empty!");
					return ret;
				}
				
				DeductLoanUseReq loanUseReq = loanUseReqList.get(0);
				if(OVERALL_STATUS_SUCCESS.equals(notify.getOverall_status())){
					//放款成功
					loanUseReq.setNewStatus(LOAN_USE_REQ_STATUS_LOAN);
					String loanDate = loanUseReq.getLoanDate();
					if(loanDate == null || "".equals(loanDate)){
						loanUseReq.setLoanDate(sdf.format(new Date()));
					}
					//比较下放款金额是否与支用金额一致
					if(payment_amount.compareTo(loanUseReq.getAppAmt()) != 0){
						loanUseReq.setDescrible("放款通知中返回的放款金额与支用金额不一致！ 本次放款通知中本步骤放款结果金额为：" + notify.getPayment_amount());
					}
				}else if(OVERALL_STATUS_FAILURE.equals(notify.getOverall_status())){
					//放款失败
					loanUseReq.setNewStatus(LOAN_USE_REQ_STATUS_FAILURE);
				}else if(OVERALL_STATUS_UNKNOW.equals(notify.getOverall_status())){
					loanUseReq.setNewStatus(LOAN_USE_REQ_STATUS_UNKNOW);
				}
				
				loanUseReq.setLastModifyUid("system");
				loanUseReq.setLastModifyTime(System.currentTimeMillis());
				this.updateLoanUseReq(loanUseReq);
				ret = true;
			}else if("2".equals(payment_no)){
				//对比费用登记表(t_charge_reg)记录
				//查询费用信息
				DeductChargeReg chargeParam = new DeductChargeReg();
				chargeParam.setUseRecordNo(notify.getApp_seq());
				chargeParam.setCardStatus("1");//生效
				List<DeductChargeReg> chargeList = deductMapper.queryDeductChargeReg(chargeParam);
				DeductChargeReg chargeReg = null;
				BigDecimal tolamount = null;
				if(chargeList == null || chargeList.size() == 0){
					logger.info("chargeList is null or empty! UseRecordNo is {}! start to insert chargeRegInfo",notify.getApp_seq());
					
					DeductLoanUseReq deductLoanParam = new DeductLoanUseReq();
					deductLoanParam.setRecordNo(notify.getApp_seq());
					List<DeductLoanUseReq> loanUseReqList = deductMapper.queryLoanUseReq(deductLoanParam);
					
					if(loanUseReqList == null || loanUseReqList.size() == 0){
						logger.info("DeductService method updateLoanResult, loanUseReqList is null or empty!");
						return ret;
					}
					
					DeductLoanUseReq loanUseReq = loanUseReqList.get(0);
					
					
					chargeReg = new DeductChargeReg();
					chargeReg.setRecordNo(GenerateIdsUtil.generateId(appConfig.getAppIp()));
					chargeReg.setUseRecordNo(loanUseReq.getRecordNo());//支用流水号
					chargeReg.setAcctId(loanUseReq.getAcctId());//贷款账户id
					chargeReg.setCurDate(formatDate.format(new Date()));//日期
					chargeReg.setBaseAmount(loanUseReq.getAppAmt());
					if ("6M".equals(loanUseReq.getApplTerm().toUpperCase())) {
						chargeReg.setServiceRate(new BigDecimal(0.02));
						chargeReg.setManagerRate(new BigDecimal(0.02));
					} else {
						chargeReg.setServiceRate(new BigDecimal(0.03));
						chargeReg.setManagerRate(new BigDecimal(0.03));
					}
					chargeReg.setServiceFee(loanUseReq.getServiceFee());
					chargeReg.setManagerFee(loanUseReq.getManagerFee());
					tolamount = loanUseReq.getServiceFee().add(loanUseReq.getManagerFee());
					chargeReg.setTolamount(tolamount);
					chargeReg.setCreateUid("system");
					chargeReg.setCreateTime(System.currentTimeMillis());
					chargeReg.setLastModifyUid("system");
					chargeReg.setLastModifyTime(System.currentTimeMillis());
					
					
					if(OVERALL_STATUS_SUCCESS.equals(notify.getOverall_status())){
						//放款成功
						chargeReg.setProcState(CHARGE_REG_PROC_STATE_SUCCESS);
						//比较下放款金额是否与手续费+服务费金额一致
						if(payment_amount.compareTo(tolamount) != 0){
							chargeReg.setRemark("放款通知中返回的服务费管理费扣款金额与费用表中的服务费管理费总金额不一致！ 本次放款通知中本步骤放款结果金额为：" + notify.getPayment_amount());
						}
					}else if(OVERALL_STATUS_FAILURE.equals(notify.getOverall_status())){
						//放款失败
						chargeReg.setProcState(CHARGE_REG_PROC_STATE_FAILURE);
					}else if(OVERALL_STATUS_UNKNOW.equals(notify.getOverall_status())){
						chargeReg.setProcState(CHARGE_REG_PROC_STATE_UNKNOW);
					}else{
						chargeReg.setProcState(CHARGE_REG_PROC_STATE_INIT);
					}
					
					//新增费用登记表
					this.insertChargeReg(chargeReg);
				}else{
					chargeReg = chargeList.get(0);
					tolamount = chargeReg.getTolamount();
					
					if(OVERALL_STATUS_SUCCESS.equals(notify.getOverall_status())){
						//放款成功
						chargeReg.setNewProcState(CHARGE_REG_PROC_STATE_SUCCESS);
						//比较下放款金额是否与手续费+服务费金额一致
						if(payment_amount.compareTo(tolamount) != 0){
							chargeReg.setRemark("放款通知中返回的服务费管理费扣款金额与费用表中的服务费管理费总金额不一致！ 本次放款通知中本步骤放款结果金额为：" + notify.getPayment_amount());
						}
					}else if(OVERALL_STATUS_FAILURE.equals(notify.getOverall_status())){
						//放款失败
						chargeReg.setNewProcState(CHARGE_REG_PROC_STATE_FAILURE);
					}else if(OVERALL_STATUS_UNKNOW.equals(notify.getOverall_status())){
						chargeReg.setNewProcState(CHARGE_REG_PROC_STATE_UNKNOW);
					}
					chargeReg.setOldLastModifyTime(chargeReg.getLastModifyTime());
					chargeReg.setLastModifyUid("system");
					chargeReg.setLastModifyTime(System.currentTimeMillis());
					
					//更新费用登记表
					this.updateChargeReg(chargeReg);
				}
				ret = true;
			}
		}catch(Exception e){
			// 扣款失败
			logger.error("DeductService method deductCharge has an exception! creditRecordNo is {}, Exception is {}",notify.getApp_seq(),e);
		}
		return ret;
	}
	
	
	public void dealUnkownAndFailedDeductState(){
		logger.info("NotifyController method dealUnkownAndFailedDeductState start!");
		//手续费扣款失败补扣
		//查询chargeReg表，代扣次数<=2,代扣状态为失败的
		DeductChargeReg chargeRegParamter = new DeductChargeReg();
		chargeRegParamter.setProcState("20");//扣款失败
		chargeRegParamter.setDeductCount(2);
		List<DeductChargeReg> chargeList = deductMapper.queryChargeRegByProcState(chargeRegParamter);
		DeductSplit deductSplitParam = null;
		List<DeductSplit> list = null;
//		List<DeductSplit> deductSplitList;
		String splitProcState = null;
//		DeductSplit deductSplit = null;
		boolean result = false;
		int deductCount = 0;
		for(DeductChargeReg chargeReg: chargeList){
			try{
				//查询拆分代扣明细
				deductSplitParam = new DeductSplit();
				deductSplitParam.setOrigRecordNo(chargeReg.getRecordNo());
				deductSplitParam.setNotProcState("99");
				deductSplitParam.setProdCode(chargeReg.getProdCode());
				deductSplitParam.setSortField("split_num");
				list = deductMapper.queryDeductSplitByState(deductSplitParam);
				if(list == null || list.size() == 0){
					logger.info("DeductService method [dealUnkownAndFailedDeductState]  list is null or size == 1 ! chargeReg's record_no is {}",chargeReg.getUseRecordNo());
					continue;
				}
				
				for(DeductSplit bean : list){
					if("70".equals(bean.getProcState()) || "90".equals(bean.getProcState())){
						splitProcState = bean.getProcState();
						break;//只会存在一条未知状态或者扣款失败的信息
					}
				}
			
				//更新charge_reg表为扣款中状态
				chargeReg.setNewProcState("40");//扣款中状态
				chargeReg.setLastModifyTime(chargeReg.getLastModifyTime());
				chargeReg.setLastModifyUid("system");
				chargeReg.setLastModifyTime(System.currentTimeMillis());
				this.updateChargeReg(chargeReg);
				//开始扣款
				result = this.doDealDeductState(list);
				if("90".equals(splitProcState)){
					//扣款失败的时候，记录扣款次数+1
					deductCount = chargeReg.getDeductCount();
					chargeReg.setDeductCount(deductCount + 1);
				}
				if(result){
					//所有扣款明细都扣款成功
					chargeReg.setNewProcState("10");
				}else{
					chargeReg.setNewProcState("20");	
				}
				chargeReg.setProcState("40");//扣款处理中
				chargeReg.setLastModifyTime(chargeReg.getLastModifyTime());
				chargeReg.setLastModifyUid("system");
				chargeReg.setLastModifyTime(System.currentTimeMillis());
				
				this.updateChargeReg(chargeReg);
	            logger.info("chargeReg result is {} , chargeReg's recordNo is {}:",result?"success":"false", chargeReg.getRecordNo());
			}catch(Exception e){
				// 扣款失败
				logger.error("DeductService method dealUnkownAndFailedDeductState has an exception! chargeReg's record_no is {}, Exception is {}",chargeReg.getRecordNo(),e);
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateLoanUseReq(DeductLoanUseReq loanUseReq) {
		logger.info("DeductService method updateLoanUseReq start!");
		int cnt = deductMapper.updateLoanUseReq(loanUseReq);
		if(cnt != 1){
			throw new RuntimeException("更新LoanUseReq数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateChargeReg(DeductChargeReg chargeReg) {
		logger.info("DeductService method updateChargeReg start!");
		int cnt = deductMapper.updateChargeReg(chargeReg);
		if(cnt != 1){
			throw new RuntimeException("更新ChargeReg数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertChargeReg(DeductChargeReg chargeReg) {
		logger.info("DeductService method insertChargeReg start!");
		int cnt = deductMapper.insertChargeReg(chargeReg);
		if(cnt != 1){
			throw new RuntimeException("插入ChargeReg数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertDeductSplitList(List<DeductSplit> list) {
		logger.info("DeductService method updateChargeReg start!");
		int cnt = 0;
		for(int i = 0 ; i < list.size(); i++){
			cnt = deductMapper.insertDeductSplit(list.get(i));
			if(cnt != 1){
				throw new RuntimeException("新增DeductSplit数据(cnt: "+cnt+")将回滚!");
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertDeductSplit(DeductSplit deductSplit) {
		logger.info("DeductService method updateChargeReg start!");
		int cnt = deductMapper.insertDeductSplit(deductSplit);
		if(cnt != 1){
			throw new RuntimeException("新增DeductSplit数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateDeductSplit(DeductSplit deductSplit) {
		logger.info("DeductService method updateChargeReg is start!");
		int cnt = deductMapper.updateDeductSplit(deductSplit);
		if(cnt != 1){
			throw new RuntimeException("更新DeductSplit数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	private boolean deduct(List<DeductSplit> list){
		logger.info("DeductService method doInitDeduct start!");
		//调用IPP扣款
		boolean deductRet = false;
		for(int i=0; i < list.size(); i++){
			DeductSplit deductSplit = null;
			try{
				deductSplit = list.get(i);
				deductRet = this.doIPPDeduct(deductSplit);
				if(!deductRet){
					break;
				}
				deductRet = true;
			}catch(Exception e){
				// 扣款失败
				logger.error("DeductService method doInitDeduct has an exception! DeductSplit' recordNo is {},and Exception is {}",deductSplit.getRecordNo(),e);
				break;
			}
			
		}
		return deductRet;
	}
	
	@SuppressWarnings("unchecked")
	private boolean doIPPDeduct(DeductSplit deductSplit) throws Exception{
		logger.info("DeductService method doIPPDeduct start ! deductSplit's recordNo is {} !",deductSplit.getRecordNo());
		String errorCode = null;
		String rtnCode = null;
		String rtnMsg = null;
		boolean deductRet = false;
		MultiValueMap<String, String> multiValueMap  = this.getParamLess(deductSplit);
		//调用IPP扣款
		String jsonStr = URLDecoder.decode(restTemplate.postForObject(withholdingNoticeUrl, multiValueMap, String.class), "UTF-8");
		
		logger.info("ipp deductSplit recordNo is {}, result is {}",deductSplit.getRecordNo() , jsonStr);
		
		Map<String, Object> resultMap = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
		
		if(resultMap == null  || !"true".equals(resultMap.get("isSuccess"))){
			logger.info("ipp deductSplit recordNo is {}, resultMap is null!",deductSplit.getRecordNo());
			return deductRet;
		}
		Map<String, Object> responseMap = (Map<String, Object>)resultMap.get("response");
		errorCode = (String) responseMap.get("ERROR_CODE") == null ? "" : (String) responseMap.get("ERROR_CODE");
		// IPP错误码变更为6位，截取后5位按原逻辑处理
		if (!"".equals(errorCode) && errorCode.length() > 5) {
			errorCode = errorCode.substring(errorCode.length() - 5, errorCode.length());
		}
		rtnCode = (String) responseMap.get("RET_CODE") == null ? "" : (String) responseMap.get("RET_CODE");
		rtnMsg = (String) responseMap.get("RET_MSG") == null ? "" : (String) responseMap.get("RET_MSG");
		
		if (!"".equals(rtnCode)){
			if (DEDUCT_RESULT_SUCCESS.equals(rtnCode)) {
				deductSplit.setNewProcState("80");//扣款成功
				deductSplit.setRtnCode(rtnCode);
				deductSplit.setRtnMsg(rtnMsg);
				deductRet = true;
			}else if (DEDUCT_RESULT_WAIT_STILL.equals(rtnCode) || DEDUCT_RESULT_PROCESSING.equals(rtnCode)) {
				logger.info("Waiting withholding process result for chargeDeduct.  recordNo is {} " + deductSplit.getRecordNo());
				deductSplit.setNewProcState("70");
				deductSplit.setRtnCode(rtnCode);
				deductSplit.setRtnMsg(rtnMsg);
			}else {
				deductSplit.setNewProcState("90");
				deductSplit.setRtnCode(rtnCode);
				deductSplit.setRtnMsg(rtnMsg);
			}
		}else{
			if (DEDUCT_RESULT_WAIT.equals(errorCode) || DEDUCT_RESULT_UNKNOW_TRADE_STATE.equals(errorCode)
					|| DEDUCT_RESULT_ERROR_DEAL_MSG.equals(errorCode) || DEDUCT_RESULT_NUM_UNMATCH.equals(errorCode)
					|| DEDUCT_RESULT_AMT_UNMATCH.equals(errorCode) || DEDUCT_RESULT_CHECK_FAILED.equals(errorCode)
					|| DEDUCT_RESULT_TIMEOUT.equals(errorCode)) {
				logger.info("Waiting withholding process result for chargeDeduct. recordNo is {} " + deductSplit.getRecordNo());
				deductSplit.setNewProcState("70");
			}else{
				deductSplit.setNewProcState("90");
			}
			deductSplit.setRtnCode(errorCode);
		}
		deductSplit.setRtnMsg(rtnMsg);
		deductSplit.setOldLastModifyTime(deductSplit.getLastModifyTime());
		deductSplit.setLastModifyUid("system");
		deductSplit.setLastModifyTime(System.currentTimeMillis());
		//更新扣款表
		this.updateDeductSplit(deductSplit);
		return deductRet;
	}
	
	private boolean doDealDeductState(List<DeductSplit> list){
		logger.info("DeductService method doDealDeductState is start!");
		//处理未知状态
		String procState = null;
//		String origRecordNo = null;
		for(DeductSplit deductSplit : list){
			procState = deductSplit.getProcState();
//			origRecordNo = deductSplit.getOrigRecordNo();
			if("80".equals(procState)){
				continue;
			}
			
			//70未知状态
			if("70".equals(procState)){
				try {
					if(this.doUnKnowSplitDeduct(deductSplit)){
						logger.info("doDealDeductState origRecordNo is {}, procState is 70, query ipp result is true! recordNo is {}" ,deductSplit.getRecordNo());
						continue;
					}else{
						logger.info("doDealDeductState 70 is false,the DeductDetailSplit's recordNo is {}" ,deductSplit.getRecordNo());
						return false;
					}
				} catch (Exception e) {
					logger.info("doDealDeductState 70 has an exception , the DeductDetailSplit's recordNo is {} , exception is {}", deductSplit.getRecordNo(),e);
					e.printStackTrace();
					return false;
				}
			}
			
			//90扣款失败状态
			if("90".equals(procState)){
				try {
					if(this.doFailedSplitDeduct(deductSplit)){
						logger.info("doDealDeductState origRecordNo is {}, procState is 90, deduct is true! recordNo is {}" ,deductSplit.getRecordNo());
						continue;
					}else{
						logger.info("doDealDeductState 90 is false,the DeductDetailSplit's recordNo is {}" ,deductSplit.getRecordNo());
						return false;
					}
				} catch (Exception e) {
					logger.info("doDealDeductState 90 has an exception , the DeductDetailSplit's recordNo is {} , exception is {}", deductSplit.getRecordNo(),e);
					e.printStackTrace();
					return false;
				}
			}
			
			if("00".equals(procState)){
				try {
					if(this.doIPPDeduct(deductSplit)){
						continue;
					}else{
						return false;
					}
				} catch (Exception e) {
					logger.info("doDealDeductState deal 00 has an exception, deductsplit' recordNo is {}, exception is {}" , deductSplit.getRecordNo(),e);
					e.printStackTrace();					
					return false;
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean doUnKnowSplitDeduct(DeductSplit deductSplit) throws Exception{
		logger.info("DeductService method doUnKnowSplitDeduct start to query TPP result! deductSplit's recordNo is {} !",deductSplit.getRecordNo());
		String rtnCode = null;
		String rtnMsg = null;
		boolean deductRet = false;
		
		MultiValueMap<String, String> multiValueMap = getQueryParamLess(deductSplit);
		
		//调用IPP扣款
		String jsonStr = URLDecoder.decode(restTemplate.postForObject(withholdingQueryUrl, multiValueMap, String.class), "UTF-8");
		
		logger.info("ipp doUnKnowSplitDeduct recordNo is {}, result is {}",deductSplit.getRecordNo() , jsonStr);
		
		Map<String, Object> resultMap = (Map<String, Object>)JSONObject.parseObject(jsonStr,Map.class);
		
		if(resultMap == null || !"true".equals(resultMap.get("isSuccess"))){
			logger.info("ipp doUnKnowSplitDeduct recordNo is {}, resultMap is null!",deductSplit.getRecordNo());
			return deductRet;
		}
		Map<String, Object> responseMap = (Map<String, Object>)resultMap.get("response");
		rtnCode = (String) responseMap.get("RET_CODE") == null ? "" : (String) responseMap.get("RET_CODE");
		rtnMsg = (String) responseMap.get("RET_MSG") == null ? "" : (String) responseMap.get("RET_MSG");
		
		if (DEDUCT_RESULT_SUCCESS.equals(rtnCode)) {
			//扣款成功
			deductSplit.setNewProcState("80");//扣款成功
			deductRet = true;
		} else if (DEDUCT_RESULT_WAIT_STILL.equals(rtnCode) || DEDUCT_RESULT_PROCESSING.equals(rtnCode)) {
			// 结果未知的不做任何操作
			logger.info("Waiting doUnKnowSplitDeduct process result.recordNo is {}, SERIAL_NO is {}, rtnCode is {}",deductSplit.getRecordNo(),deductSplit.getSerialNo(),rtnCode);
		} else {
			deductSplit.setNewProcState("90");
		}
		deductSplit.setRtnCode(rtnCode);
		deductSplit.setRtnMsg(rtnMsg);
		deductSplit.setOldLastModifyTime(deductSplit.getLastModifyTime());
		deductSplit.setLastModifyUid("system");
		deductSplit.setLastModifyTime(System.currentTimeMillis());
		//更新扣款表
		this.updateDeductSplit(deductSplit);
		return deductRet;
	}
	
	private boolean doFailedSplitDeduct(DeductSplit deductSplit) throws Exception{
		logger.info("DeductService method doFailedSplitDeduct start to query TPP result! deductSplit's recordNo is {} !",deductSplit.getRecordNo());
		boolean deductRet = false;
		
		//复制bean
		String recordNo = GenerateIdsUtil.generateId(appConfig.getAppIp());
		String serialNo = gserialsNo();
		DeductSplit newDeductSplit = new DeductSplit();
		BeanUtils.copyProperties(deductSplit, newDeductSplit);
		
		//将90状态的扣款明细置为失效
		deductSplit.setNewProcState("99");//失效
		deductSplit.setRemark("扣款失败处理，置为失效,新的扣款记录为："+recordNo);
		deductSplit.setOldLastModifyTime(deductSplit.getLastModifyTime());
		deductSplit.setLastModifyUid("system");
		deductSplit.setLastModifyTime(System.currentTimeMillis());
		this.updateDeductSplit(deductSplit);
		
		//插入新的扣款明细
		newDeductSplit.setRecordNo(recordNo);
		newDeductSplit.setProcState("00");//初始扣款状态
		newDeductSplit.setSerialNo(serialNo);
		newDeductSplit.setCreateUid("system");
		newDeductSplit.setCreateTime(System.currentTimeMillis());
		newDeductSplit.setLastModifyUid("system");
		newDeductSplit.setLastModifyTime(System.currentTimeMillis());
		this.insertDeductSplit(newDeductSplit);
		
		deductRet = this.doIPPDeduct(newDeductSplit);
		
		return deductRet;
	}
	
	//init TPP请求参数
	private MultiValueMap<String, String> getParamLess(DeductSplit deductSplit)
    {
		String serialNo = deductSplit.getSerialNo();
		String serialsNoLast12 = serialNo.substring(serialNo.length()-12,serialNo.length());
		String remark = null;
		MultiValueMap<String, String> multiValueMap =  new LinkedMultiValueMap<String, String>();
		multiValueMap.add("VERSION", IPP_VERSION);// 版本
		multiValueMap.add("BUSINESS_TYPE", IPP_BUSINESS_TYPE);// 业务类型_基金
		multiValueMap.add("PAY_TYPE", IPP_PAY_TYPE);// 单笔代收付
		multiValueMap.add("TRANS_CODE", IPP_TRANS_CODE);// 扣款
		multiValueMap.add("AGENT_ID", agentId);// 代理号
		multiValueMap.add("TRACE_NUM", serialNo);// 流水号
		Date transDate =new Date(Long.parseLong(deductSplit.getTransDate().toString()));
		multiValueMap.add("TRANS_DATE", formatDate.format(transDate));// 交易日期
		multiValueMap.add("TRANS_TIME", formatTime.format(transDate));// 交易时间       
		multiValueMap.add("BG_NOTIFY_URL", "");// 后台通知地址
		multiValueMap.add("MERCHANT_NO", merchantNo);// 商户号
		multiValueMap.add("TRANS_AMOUNT", String.valueOf(deductSplit.getSplitAmount().multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_HALF_UP)));// 交易金额(单位分)
		multiValueMap.add("CURRENCY", "156");// 币种（156人民币）
		multiValueMap.add("BANK_CODE", deductSplit.getBankCode());// 银行代码
		multiValueMap.add("ACCT_NAME", deductSplit.getAcctName());// 账户名称
		multiValueMap.add("ACCT_CAT", deductSplit.getAcctCat());// 账户类型
		multiValueMap.add("ACCT_NO", deductSplit.getAcctNo());// 银行卡号
		multiValueMap.add("ID_TYPE", IPP_CERT_TYPE);// 证件类型:01 身份证
		multiValueMap.add("ID_NO", deductSplit.getIdNo());// 证件号码
		if(PROD_CODE_2FIRE.equals(deductSplit.getProdCode())){
			remark = ("2df-" + deductSplit.getBusiType() + "-" + serialsNoLast12).toString();
		}
		multiValueMap.add("REMARK", remark);// 备注，用户代收付等资金类交易。POS扣款交易，资金类备注以方便对账
		logger.info("multiValueMap is : {}",multiValueMap);
        return multiValueMap;
    }
	
	//init TPP查询请求参数
	private MultiValueMap<String, String> getQueryParamLess(DeductSplit deductSplit)
    {
		String serialNo= gserialsNo();
		//String serialsNoLast12 = serialNo.substring(serialNo.length()-12,serialNo.length());
		MultiValueMap<String, String> multiValueMap =  new LinkedMultiValueMap<String, String>();
		multiValueMap.add("VERSION", IPP_VERSION);// 版本
		multiValueMap.add("BUSINESS_TYPE", "03");// 业务类型_理财
		multiValueMap.add("PAY_TYPE", "00");// 单笔代收付
		multiValueMap.add("TRANS_CODE", "4001");// 查询
		multiValueMap.add("AGENT_ID", agentId);// 代理号
		multiValueMap.add("TRACE_NUM", serialNo);// 流水号
		multiValueMap.add("TRANS_DATE", formatDate.format(new Date()));// 交易日期
		multiValueMap.add("TRANS_TIME", formatTime.format(new Date()));// 交易时间       
		multiValueMap.add("BG_NOTIFY_URL", "");// 后台通知地址
		multiValueMap.add("MERCHANT_NO", merchantNo);// 商户号
		multiValueMap.add("ORI_TRANS_DATE", formatDate.format(deductSplit.getTransDate()));// 原交易日期
		multiValueMap.add("ORI_TRANS_TIME", formatTime.format(deductSplit.getTransDate()));// 原交易时间
		multiValueMap.add("ORI_TRACE_NUM", deductSplit.getSerialNo());// 原交易流水号
		logger.info("multiValueMap is : {}",multiValueMap);
        return multiValueMap;
    }
	
	//生成扣款明细表
	private List<DeductSplit> createDeductSplit(DeductChargeReg chargeReg){
		List<DeductSplit> deductSplitList = new ArrayList<DeductSplit>();
		DeductSplit deductSplit = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date curDate= new Date();
		//查询扣款银行限额表
		DeductLimit limitParam = new DeductLimit();
		limitParam.setDeductChannel("TPP");// 支付渠道
		limitParam.setBusiType("1");//业务类型 1 - 对私代扣
		limitParam.setBankCode(chargeReg.getBankIdenCode());
		limitParam.setStatus("1");//状态 0-无效 1-有效
		List<DeductLimit> limitList = deductMapper.queryDeductLimit(limitParam);
		String recordNo = null;
		BigDecimal tolamount = chargeReg.getTolamount();
		String serialNo= null;
		if(limitList == null || limitList.size() == 0){
			//不需要拆分
			logger.info("chargeReg[{}] needn't split !",chargeReg.getRecordNo());
			deductSplit = new DeductSplit();
			recordNo = GenerateIdsUtil.generateId(appConfig.getAppIp());
			logger.info("DeductService method createDeductSplit recordNo is {}",recordNo);
			deductSplit.setRecordNo(recordNo);
			deductSplit.setOrigRecordNo(chargeReg.getRecordNo());
			deductSplit.setProdCode(chargeReg.getProdCode());
			deductSplit.setDeductDate(sdf.format(curDate));
			deductSplit.setCurDate(sdf.format(curDate));
			deductSplit.setBusiType("2");//0 - 到期逾期代扣 1 - 提前还款代扣 2 - 手续费服务费代扣 3 - 代偿代扣
			serialNo= gserialsNo();
			deductSplit.setSerialNo(serialNo);
			deductSplit.setTransAmount(tolamount);
			deductSplit.setSplitAmount(tolamount);
			deductSplit.setSplitCount(1);
			deductSplit.setSplitNum(0);
			deductSplit.setTransDate(System.currentTimeMillis());
			deductSplit.setBankCode(chargeReg.getBankIdenCode());
			deductSplit.setAcctName(chargeReg.getAcctName());
			deductSplit.setAcctNo(chargeReg.getAcctNo());
			deductSplit.setAcctCat("01");//账户类别
			deductSplit.setIdNo(chargeReg.getCertNo());
			deductSplit.setProcState("00");//处理状态 ：00 - 初始状态  70 - 未知 80 - 代扣成功 90 - 代扣失败 99 - 代扣作废 95 - 差错作废
			deductSplit.setCreateUid("system");
			deductSplit.setCreateTime(System.currentTimeMillis());
			deductSplit.setLastModifyUid("system");
			deductSplit.setLastModifyTime(System.currentTimeMillis());
			deductSplitList.add(deductSplit);
			
		}else{
			//需要拆分扣款
			logger.info("chargeReg[{}] is start to split! tolamount is {}",chargeReg.getRecordNo(),tolamount);
			DeductLimit limit = limitList.get(0);
			BigDecimal onceDeductAmt = limit.getOnceDeductAmt();
			int count = tolamount.divideToIntegralValue(onceDeductAmt).intValue();
			BigDecimal lastOnceAmt = tolamount.remainder(onceDeductAmt);
			logger.info("chargeReg[{}]'s last once amt is : {}" ,chargeReg.getRecordNo() ,lastOnceAmt);
		    int splitCount = new BigDecimal(0.00).compareTo(lastOnceAmt) == 0 ? count : (count + 1);
		    for(int i = 0; i < splitCount; i++){
		    	deductSplit = new DeductSplit();
		    	recordNo = GenerateIdsUtil.generateId(appConfig.getAppIp());
				logger.info("DeductService method createDeductSplit recordNo is {}",recordNo);
				deductSplit.setRecordNo(recordNo);
				deductSplit.setOrigRecordNo(chargeReg.getRecordNo());
				deductSplit.setProdCode(chargeReg.getProdCode());
				deductSplit.setDeductDate(sdf.format(curDate));
				deductSplit.setCurDate(sdf.format(curDate));
				serialNo= gserialsNo();
				deductSplit.setSerialNo(serialNo);
				deductSplit.setBusiType("2");//0 - 到期逾期代扣 1 - 提前还款代扣 2 - 手续费服务费代扣 3 - 代偿代扣
				deductSplit.setTransAmount(tolamount);
				
				if(i == splitCount - 1){
					deductSplit.setSplitAmount(lastOnceAmt);
				}else{
					deductSplit.setSplitAmount(onceDeductAmt);
				}
				deductSplit.setSplitCount(splitCount);
				deductSplit.setSplitNum(i);
				deductSplit.setTransDate(System.currentTimeMillis());
				deductSplit.setBankCode(chargeReg.getBankIdenCode());
				deductSplit.setAcctName(chargeReg.getAcctName());
				deductSplit.setAcctNo(chargeReg.getAcctNo());
				deductSplit.setAcctCat("01");//账户类别
				deductSplit.setIdNo(chargeReg.getCertNo());
				deductSplit.setProcState("00");//处理状态 ：00 - 初始状态  70 - 未知 80 - 代扣成功 90 - 代扣失败 99 - 代扣作废 95 - 差错作废
				deductSplit.setCreateUid("system");
				deductSplit.setCreateTime(System.currentTimeMillis());
				deductSplit.setLastModifyUid("system");
				deductSplit.setLastModifyTime(System.currentTimeMillis());
				deductSplitList.add(deductSplit);
				
		    }
		}
		return deductSplitList;
	}
	
	public String gserialsNo(){
		//2DF-MMDDHHMMSS+毫秒3位
		//SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
		//SimpleDateFormat formatTime = new SimpleDateFormat("HHmmss");
		Date datestr= new Date();
        String yymmdd=formatDate.format(datestr);// 交易日期
        String hhmmss=formatTime.format(datestr);// 交易时间       
        String mmm=System.currentTimeMillis()+"";
        String str = "2DF-"+ yymmdd.substring(yymmdd.length()-4)+hhmmss.substring(0,hhmmss.length()-2)+mmm.substring(mmm.length()-3)+new Random().nextInt(1000);
		return str;
	}
}
