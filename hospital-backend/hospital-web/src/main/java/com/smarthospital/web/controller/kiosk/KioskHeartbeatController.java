package com.smarthospital.web.controller.kiosk;

import com.smarthospital.common.response.R;
import com.smarthospital.service.kiosk.KioskMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kiosk/heartbeat")
@RequiredArgsConstructor
public class KioskHeartbeatController {

    private final KioskMonitorService kioskMonitorService;

    @PostMapping
    public R<Void> heartbeat(@RequestParam Long kioskId,
                             @RequestParam(required = false) Integer paperRemaining) {
        kioskMonitorService.heartbeat(kioskId, paperRemaining);
        return R.ok();
    }
}