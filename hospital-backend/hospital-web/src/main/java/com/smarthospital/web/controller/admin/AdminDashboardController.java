package com.smarthospital.web.controller.admin;

import com.smarthospital.common.response.R;
import com.smarthospital.model.dto.DashboardSummary;
import com.smarthospital.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public R<DashboardSummary> summary() {
        return R.ok(dashboardService.getSummary());
    }
}
