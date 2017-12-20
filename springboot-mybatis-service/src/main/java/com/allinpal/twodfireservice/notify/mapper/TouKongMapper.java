package com.allinpal.twodfireservice.notify.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.allinpal.twodfireservice.notify.domain.DeductLoanUseReq;
import com.allinpal.twodfireservice.notify.domain.LoanRate;
import com.allinpal.twodfireservice.notify.domain.Notify;
import com.allinpal.twodfireservice.notify.domain.ProdAcctInfo;
import com.allinpal.twodfireservice.notify.domain.TouKongInfo;
import com.allinpal.twodfireservice.notify.domain.TouKongMessage;
import com.allinpal.twodfireservice.notify.domain.TouKongRepayPlan;

@Mapper
public interface TouKongMapper {
	
	List<TouKongInfo> queryCreditInfo(Notify notify);
	
	int insertToukongMessage(TouKongMessage toukongMessage);
	
	int insertRepayPlan(TouKongRepayPlan toukongRepayPlan);
	
	List<ProdAcctInfo> queryProdAcctInfo(TouKongInfo toukongInfo);
	
	int updateProdAcctInfo(ProdAcctInfo prodAcctInfo);
	
	List<LoanRate> queryLoanRate(LoanRate loanRate);
	
	int updateLoanUseReq(DeductLoanUseReq deductLoanUseReq);
	
	int deleteRepayPlan(TouKongRepayPlan toukongRepayPlan);
	
	int queryRepayPlanCount(TouKongRepayPlan toukongRepayPlan);
}
