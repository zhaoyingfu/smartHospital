package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.Queue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QueueMapper extends BaseMapper<Queue> {
}
