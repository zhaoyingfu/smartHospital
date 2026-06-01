package com.smarthospital.web.controller.kiosk;

import com.smarthospital.common.response.R;
import com.smarthospital.model.dto.InpatientDepositReq;
import com.smarthospital.model.dto.OutpatientPayReq;
import com.smarthospital.model.dto.PayReq;
import com.smarthospital.model.entity.InpatientDeposit;
import com.smarthospital.model.entity.OutpatientOrder;
import com.smarthospital.model.entity.PaymentDetail;
import com.smarthospital.his.dto.InpatientInfoDTO;
import com.smarthospital.service.inpatient.InpatientService;
import com.smarthospital.service.outpatient.OutpatientService;
import com.smarthospital.service.payment.PaymentOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kiosk/payment")
@RequiredArgsConstructor
public class KioskPaymentController {

    private final PaymentOrderService paymentOrderService;
    private final OutpatientService outpatientService;
    private final InpatientService inpatientService;

    @PostMapping("/pay")
    public R<PaymentDetail> pay(@Valid @RequestBody PayReq req) {
        return R.ok(paymentOrderService.processPayment(req));
    }

    @PostMapping("/outpatient/create")
    public R<OutpatientOrder> createOutpatientOrder(@Valid @RequestBody OutpatientPayReq req) {
        return R.ok(outpatientService.createFromHis(req.getPatientId(), req.getVisitNo(), req.getKioskId()));
    }

    @PostMapping("/inpatient/deposit")
    public R<InpatientDeposit> createInpatientDeposit(@Valid @RequestBody InpatientDepositReq req) {
        return R.ok(inpatientService.createDeposit(req));
    }

    @GetMapping("/inpatient/info")
    public R<InpatientInfoDTO> getInpatientInfo(@RequestParam String inpatientNo) {
        return R.ok(inpatientService.queryInpatientInfo(inpatientNo));
    }
}