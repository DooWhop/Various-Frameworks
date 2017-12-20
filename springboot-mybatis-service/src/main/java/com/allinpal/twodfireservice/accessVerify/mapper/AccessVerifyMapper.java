package com.allinpal.twodfireservice.accessVerify.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.allinpal.twodfireservice.accessVerify.domain.AccessInfo;
import com.allinpal.twodfireservice.accessVerify.domain.BasicInfo;
import com.allinpal.twodfireservice.accessVerify.domain.TApplyNotaccess;

@Mapper
public interface AccessVerifyMapper {
	AccessInfo getAcctInfo(AccessInfo pai);
	public int recordErrorInfor(TApplyNotaccess parameter);
	BasicInfo getBasicInfo(String merchantNo);
	String getTlbUserOid(String userId);
}
