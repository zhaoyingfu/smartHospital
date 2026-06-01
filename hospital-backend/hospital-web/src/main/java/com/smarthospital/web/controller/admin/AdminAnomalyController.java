package com.smarthospital.web.controller.admin;

import com.smarthospital.common.response.R;
import com.smarthospital.service.anomaly.AnomalyDetectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/anomaly")
@RequiredArgsConstructor
public class AdminAnomalyController {

    private final AnomalyDetectService anomalyDetectService;

    @GetMapping("/detect")
    public R<List<String>> detect() {
        return R.ok(anomalyDetectService.detectAnomalies());
    }
}