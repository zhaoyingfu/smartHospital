package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {
}
