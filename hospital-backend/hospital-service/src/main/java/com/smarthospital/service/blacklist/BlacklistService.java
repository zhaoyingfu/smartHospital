package com.smarthospital.service.blacklist;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.dal.mapper.PatientBlacklistMapper;
import com.smarthospital.dal.mapper.PatientMapper;
import com.smarthospital.model.entity.Patient;
import com.smarthospital.model.entity.PatientBlacklist;
import com.smarthospital.service.patient.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final PatientBlacklistMapper blacklistMapper;
    private final PatientMapper patientMapper;
    private final PatientService patientService;

    public void add(Long patientId, String reason, Long operatorId) {
        patientService.addToBlacklist(patientId, reason, operatorId);
    }

    public void remove(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient != null) {
            patient.setIsBlacklist(false);
            patientMapper.updateById(patient);
        }
    }

    public Page<PatientBlacklist> pageQuery(int pageNum, int pageSize) {
        Page<PatientBlacklist> page = new Page<>(pageNum, pageSize);
        return blacklistMapper.selectPage(page,
                new LambdaQueryWrapper<PatientBlacklist>().orderByDesc(PatientBlacklist::getCreateTime));
    }
}