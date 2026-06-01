package com.smarthospital.service.drug;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.dal.mapper.DrugInfoMapper;
import com.smarthospital.model.entity.DrugInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrugService {

    private final DrugInfoMapper drugInfoMapper;

    public List<DrugInfo> search(String keyword) {
        return drugInfoMapper.selectList(
                new LambdaQueryWrapper<DrugInfo>()
                        .eq(DrugInfo::getStatus, 1)
                        .and(w -> w.like(DrugInfo::getName, keyword)
                                .or().like(DrugInfo::getPinYin, keyword))
                        .last("LIMIT 50"));
    }
}