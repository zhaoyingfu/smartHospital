package com.smarthospital.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.response.R;
import com.smarthospital.model.dto.ReconHandleReq;
import com.smarthospital.model.entity.ReconciliationDetail;
import com.smarthospital.model.entity.ReconciliationRecord;
import com.smarthospital.model.entity.ReconciliationTicket;
import com.smarthospital.service.recon.ReconService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/recon")
@RequiredArgsConstructor
public class AdminReconController {

    private final ReconService reconService;

    @PostMapping("/execute")
    public R<ReconciliationRecord> execute(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reconDate,
            @RequestParam String channel) {
        return R.ok(reconService.executeRecon(reconDate, channel));
    }

    @GetMapping("/records")
    public R<Page<ReconciliationRecord>> records(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(reconService.pageQuery(pageNum, pageSize));
    }

    @GetMapping("/details")
    public R<List<ReconciliationDetail>> details(@RequestParam Long recordId) {
        return R.ok(reconService.queryDetails(recordId));
    }

    @PostMapping("/handle")
    public R<Void> handleDiff(@Valid @RequestBody ReconHandleReq req,
                              @RequestHeader("X-User-Id") Long handlerId) {
        reconService.handleDiff(req, handlerId);
        return R.ok();
    }

    @GetMapping("/tickets")
    public R<Page<ReconciliationTicket>> tickets(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(reconService.pageTickets(pageNum, pageSize));
    }
}