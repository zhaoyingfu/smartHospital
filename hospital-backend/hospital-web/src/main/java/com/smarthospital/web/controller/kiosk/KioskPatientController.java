package com.smarthospital.web.controller.kiosk;

import com.smarthospital.common.response.R;
import com.smarthospital.model.dto.IdentityVerifyReq;
import com.smarthospital.model.entity.Patient;
import com.smarthospital.service.patient.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kiosk/patient")
@RequiredArgsConstructor
public class KioskPatientController {

    private final PatientService patientService;

    @PostMapping("/verify")
    public R<Patient> verify(@RequestBody IdentityVerifyReq req) {
        Patient patient = patientService.verifyOrRegister(req.getIdCard(), req.getMedicareCard(), req.getPhone(), req.getMedicalNo());
        return R.ok(patient);
    }

    @GetMapping("/{id}")
    public R<Patient> getInfo(@PathVariable Long id) {
        return R.ok(patientService.findById(id));
    }

    @GetMapping("/{id}/blacklist")
    public R<Boolean> checkBlacklist(@PathVariable Long id) {
        return R.ok(patientService.isBlacklisted(id));
    }
}