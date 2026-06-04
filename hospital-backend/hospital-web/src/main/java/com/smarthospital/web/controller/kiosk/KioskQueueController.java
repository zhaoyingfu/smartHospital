package com.smarthospital.web.controller.kiosk;

import com.smarthospital.common.response.R;
import com.smarthospital.model.dto.SignInReq;
import com.smarthospital.model.entity.Queue;
import com.smarthospital.model.vo.queue.QueuePanelVO;
import com.smarthospital.service.registration.QueueService;
import com.smarthospital.service.registration.SignInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kiosk/queue")
@RequiredArgsConstructor
public class KioskQueueController {

    private final SignInService signInService;
    private final QueueService queueService;

    @PostMapping("/signin")
    public R<Queue> signIn(@RequestBody SignInReq req) {
        return R.ok(signInService.signIn(req.getPatientId(), req.getRegNo()));
    }

    @PostMapping("/reenter")
    public R<Queue> reenter(@RequestBody SignInReq req) {
        return R.ok(signInService.reenterSkip(req.getPatientId(), req.getRegNo()));
    }

    @GetMapping("/status")
    public R<QueuePanelVO> status(@RequestParam Long departmentId, @RequestParam Long doctorId) {
        return R.ok(queueService.getPanelData(departmentId, doctorId));
    }

    @GetMapping("/wait-count")
    public R<Long> waitCount(@RequestParam Long departmentId, @RequestParam Long doctorId) {
        return R.ok(queueService.getWaitCount(departmentId, doctorId));
    }
}
