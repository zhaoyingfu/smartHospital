package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
