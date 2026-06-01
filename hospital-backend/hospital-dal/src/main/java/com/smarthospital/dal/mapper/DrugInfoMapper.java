package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.DrugInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DrugInfoMapper extends BaseMapper<DrugInfo> {
}
