package com.allinpal.twodfireservice.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.allinpal.twodfireservice.creditService.domain.ProdAcct;
import com.allinpal.twodfireservice.user.domain.LoanCreditInfo;
import com.allinpal.twodfireservice.user.domain.User;

@Mapper
public interface UserMapper {

	List<User> queryUserInfo(User user);
	
	List<ProdAcct> queryUserCardInfo(User user);
	
	int addUserInfo(User user);
	
	List<LoanCreditInfo> queryCreditCount(User user);
	
	int queryCardCount(User user);
	
	int queryUserIdenFile(User user);
}
