package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.PatientBlacklist;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientBlacklistMapper extends BaseMapper<PatientBlacklist> {
}
