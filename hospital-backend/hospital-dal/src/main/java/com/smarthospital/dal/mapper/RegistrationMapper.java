package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.Registration;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
}
