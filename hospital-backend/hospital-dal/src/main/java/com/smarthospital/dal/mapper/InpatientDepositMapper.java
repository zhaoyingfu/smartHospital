package com.smarthospital.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smarthospital.model.entity.InpatientDeposit;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InpatientDepositMapper extends BaseMapper<InpatientDeposit> {
}
