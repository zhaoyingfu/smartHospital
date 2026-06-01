package com.smarthospital.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.response.R;
import com.smarthospital.model.entity.DailySettlement;
import com.smarthospital.service.recon.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/settlement")
@RequiredArgsConstructor
public class AdminSettlementController {

    private final SettlementService settlementService;

    @PostMapping("/generate")
    public R<DailySettlement> generate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate settleDate) {
        return R.ok(settlementService.generateDaily(settleDate));
    }

    @GetMapping("/list")
    public R<Page<DailySettlement>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(settlementService.pageQuery(pageNum, pageSize));
    }

    @PostMapping("/{id}/confirm")
    public R<Void> confirm(@PathVariable Long id,
                           @RequestHeader("X-User-Id") Long confirmerId) {
        settlementService.confirm(id, confirmerId);
        return R.ok();
    }

    @PostMapping("/{id}/lock")
    public R<Void> lock(@PathVariable Long id) {
        settlementService.lock(id);
        return R.ok();
    }
}