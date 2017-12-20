package com.allinpal.twodfireservice.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.allinpal.twodfireservice.creditService.domain.ProdAcct;
import com.allinpal.twodfireservice.user.domain.LoanCreditInfo;
import com.allinpal.twodfireservice.user.domain.User;
import com.allinpal.twodfireservice.user.mapper.UserMapper;

/**
 * @author admin
 * 用户信息service
 */
@Service("userService")
public class UserService {
	@Autowired
	public UserMapper userMapper;
	
	
	public List<User> queryUserInfo(User user) {
		return userMapper.queryUserInfo(user);
	}
	
	public List<ProdAcct> queryUserCardInfo(User user){
		return userMapper.queryUserCardInfo(user);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveUser(User user) {
		int cnt = userMapper.addUserInfo(user);	
		if(cnt != 1){
			throw new RuntimeException("插入数据(cnt: "+cnt+")将回滚!");
		}
	}
	
	public List<LoanCreditInfo> queryCreditCount(User user) {
		return userMapper.queryCreditCount(user);
	}
	
	public int queryCardCount(User user) {
		return userMapper.queryCardCount(user);
	}
	
	public int queryUserIdenFile(User user){
		return userMapper.queryUserIdenFile(user);
	}
}
