package com.smarthospital.web.controller.admin;

import com.smarthospital.common.response.R;
import com.smarthospital.model.entity.Queue;
import com.smarthospital.model.vo.queue.QueuePanelVO;
import com.smarthospital.service.registration.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/queue")
@RequiredArgsConstructor
public class AdminQueueController {

    private final QueueService queueService;

    @PostMapping("/call-next")
    public R<Queue> callNext(@RequestParam Long departmentId, @RequestParam Long doctorId) {
        return R.ok(queueService.callNext(departmentId, doctorId));
    }

    @PostMapping("/recall/{id}")
    public R<Queue> recall(@PathVariable Long id) {
        return R.ok(queueService.recall(id));
    }

    @PostMapping("/skip/{id}")
    public R<Queue> skip(@PathVariable Long id) {
        return R.ok(queueService.skip(id));
    }

    @PostMapping("/start/{id}")
    public R<Queue> startConsultation(@PathVariable Long id) {
        return R.ok(queueService.startConsultation(id));
    }

    @PostMapping("/complete/{id}")
    public R<Queue> complete(@PathVariable Long id) {
        return R.ok(queueService.complete(id));
    }

    @GetMapping("/panel")
    public R<QueuePanelVO> panel(@RequestParam Long departmentId, @RequestParam Long doctorId) {
        return R.ok(queueService.getPanelData(departmentId, doctorId));
    }
}
