package com.smarthospital.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.response.R;
import com.smarthospital.model.entity.KioskMachine;
import com.smarthospital.service.kiosk.KioskMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/kiosk")
@RequiredArgsConstructor
public class AdminKioskController {

    private final KioskMonitorService kioskMonitorService;

    @GetMapping("/list")
    public R<Page<KioskMachine>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(kioskMonitorService.pageQuery(pageNum, pageSize));
    }

    @GetMapping("/all")
    public R<List<KioskMachine>> listAll() {
        return R.ok(kioskMonitorService.listAll());
    }

    @GetMapping("/{id}")
    public R<KioskMachine> detail(@PathVariable Long id) {
        return R.ok(kioskMonitorService.findById(id));
    }

    @PutMapping("/{id}/features")
    public R<Void> updateFeatures(@PathVariable Long id, @RequestBody String features) {
        kioskMonitorService.updateFeatures(id, features);
        return R.ok();
    }
}