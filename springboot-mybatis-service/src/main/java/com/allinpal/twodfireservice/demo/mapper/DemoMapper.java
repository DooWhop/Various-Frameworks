package com.allinpal.twodfireservice.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.allinpal.twodfireservice.demo.domain.City;

@Mapper
public interface DemoMapper {
	@Select("select * from city where state = #{state}")
	City findByState(@Param("state") String state);
	
	City selectCityByXmlId(String id);
	
	int addCity(City city);
}
