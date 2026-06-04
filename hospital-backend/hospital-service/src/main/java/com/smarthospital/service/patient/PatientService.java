package com.smarthospital.service.patient;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.dal.mapper.PatientBlacklistMapper;
import com.smarthospital.dal.mapper.PatientMapper;
import com.smarthospital.his.client.HisClient;
import com.smarthospital.model.entity.Patient;
import com.smarthospital.model.entity.PatientBlacklist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientMapper patientMapper;
    private final PatientBlacklistMapper blacklistMapper;
    private final HisClient hisClient;

    public Patient findById(Long id) {
        return patientMapper.selectById(id);
    }

    public Patient findByIdCard(String idCard) {
        return patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>().eq(Patient::getIdCard, idCard));
    }

    public Patient findByMedicareCard(String medicareCard) {
        return patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>().eq(Patient::getMedicareCard, medicareCard));
    }

    public Patient findByPhone(String phone) {
        return patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>().eq(Patient::getPhone, phone));
    }

    public Patient findByMedicalNo(String medicalNo) {
        return patientMapper.selectOne(
                new LambdaQueryWrapper<Patient>().eq(Patient::getMedicalNo, medicalNo));
    }

    public Patient verifyOrRegister(String idCard, String medicareCard, String phone, String medicalNo) {
        Patient patient = null;
        if (idCard != null) {
            patient = findByIdCard(idCard);
        } else if (medicareCard != null) {
            patient = findByMedicareCard(medicareCard);
        } else if (phone != null) {
            patient = findByPhone(phone);
        } else if (medicalNo != null) {
            patient = findByMedicalNo(medicalNo);
        }
        if (patient == null) {
            var hisPatient = hisClient.queryPatient(idCard, medicareCard, phone, medicalNo);
            patient = new Patient();
            if (hisPatient != null && hisPatient.getExists()) {
                patient.setName(hisPatient.getName());
                patient.setIdCard(hisPatient.getIdCard() != null ? hisPatient.getIdCard() : medicalNo);
                patient.setMedicareCard(hisPatient.getMedicareCard());
                patient.setPhone(hisPatient.getPhone());
                patient.setMedicalNo(hisPatient.getMedicalNo());
            } else {
                patient.setName(hisPatient != null ? hisPatient.getName() : "新患者");
                patient.setIdCard(idCard != null ? idCard : medicalNo);
                patient.setMedicareCard(medicareCard);
                patient.setPhone(phone);
                patient.setMedicalNo(medicalNo != null ? medicalNo : "MZ" + System.currentTimeMillis());
            }
            patient.setIsBlacklist(false);
            patientMapper.insert(patient);
        }
        return patient;
    }

    public boolean isBlacklisted(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient != null && patient.getIsBlacklist()) {
            return true;
        }
        return blacklistMapper.selectCount(
                new LambdaQueryWrapper<PatientBlacklist>().eq(PatientBlacklist::getPatientId, patientId)) > 0L;
    }

    public void addToBlacklist(Long patientId, String reason, Long operatorId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BizException("患者不存在");
        }
        patient.setIsBlacklist(true);
        patientMapper.updateById(patient);
        PatientBlacklist bl = new PatientBlacklist();
        bl.setPatientId(patientId);
        bl.setReason(reason);
        bl.setOperatorId(operatorId);
        blacklistMapper.insert(bl);
    }
}