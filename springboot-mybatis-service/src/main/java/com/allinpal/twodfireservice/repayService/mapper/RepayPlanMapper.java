package com.allinpal.twodfireservice.repayService.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.allinpal.twodfireservice.repayService.domain.RepayPlan;

@Mapper
public interface RepayPlanMapper {

	List<RepayPlan> getRepayList(RepayPlan repayInfo);

}
