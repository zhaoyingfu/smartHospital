package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.ScheduleLock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleLockMapper extends BaseMapper<ScheduleLock> {
}
