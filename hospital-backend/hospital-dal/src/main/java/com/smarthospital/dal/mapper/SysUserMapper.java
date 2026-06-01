package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
