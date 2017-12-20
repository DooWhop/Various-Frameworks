package com.allinpal.twodfireservice.notify.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.allinpal.twodfireservice.notify.domain.DeductChargeReg;
import com.allinpal.twodfireservice.notify.domain.DeductLimit;
import com.allinpal.twodfireservice.notify.domain.DeductLoanUseReq;
import com.allinpal.twodfireservice.notify.domain.DeductSplit;

@Mapper
public interface DeductMapper {

	List<DeductLoanUseReq> queryLoanUseReq(DeductLoanUseReq deductLoanUseReq);
	
	List<DeductChargeReg> queryDeductChargeReg(DeductChargeReg deductChargeReg);
	
	List<DeductLimit> queryDeductLimit(DeductLimit deductLimit);
	
	int updateLoanUseReq(DeductLoanUseReq deductLoanUseReq);
	
	int updateChargeReg(DeductChargeReg deductChargeReg);
	
	int insertDeductSplit(DeductSplit deductSplit);
	
	int updateDeductSplit(DeductSplit deductSplit);
	
	List<DeductSplit> queryDeductSplitByState(DeductSplit deductSplit);
	
	List<DeductChargeReg> queryDeductChargeRegByUseRecNo(DeductChargeReg deductChargeReg);
	
	int insertChargeReg(DeductChargeReg deductChargeReg);
	
	List<DeductChargeReg> queryChargeRegByProcState(DeductChargeReg deductChargeReg);
	
}
