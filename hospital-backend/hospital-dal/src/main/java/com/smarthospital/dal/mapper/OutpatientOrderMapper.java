package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.OutpatientOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutpatientOrderMapper extends BaseMapper<OutpatientOrder> {
}
