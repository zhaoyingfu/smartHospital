package com.smarthospital.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.response.R;
import com.smarthospital.model.entity.PatientBlacklist;
import com.smarthospital.service.blacklist.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/blacklist")
@RequiredArgsConstructor
public class AdminBlacklistController {

    private final BlacklistService blacklistService;

    @GetMapping("/list")
    public R<Page<PatientBlacklist>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(blacklistService.pageQuery(pageNum, pageSize));
    }

    @PostMapping("/add")
    public R<Void> add(@RequestParam Long patientId,
                       @RequestParam String reason,
                       @RequestHeader("X-User-Id") Long operatorId) {
        blacklistService.add(patientId, reason, operatorId);
        return R.ok();
    }

    @DeleteMapping("/{patientId}")
    public R<Void> remove(@PathVariable Long patientId) {
        blacklistService.remove(patientId);
        return R.ok();
    }
}