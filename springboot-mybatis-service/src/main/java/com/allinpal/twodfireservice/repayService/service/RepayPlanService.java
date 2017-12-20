package com.allinpal.twodfireservice.repayService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allinpal.twodfireservice.repayService.domain.RepayPlan;
import com.allinpal.twodfireservice.repayService.mapper.RepayPlanMapper;

@Service("repayService")
public class RepayPlanService {

	@Autowired
	private RepayPlanMapper repayPlanMapper;
	
	public List<RepayPlan> getRepayList(RepayPlan repayInfo) {
		return repayPlanMapper.getRepayList(repayInfo);
	}

}
