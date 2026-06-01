package com.smarthospital.web.controller.kiosk;

import com.smarthospital.common.response.R;
import com.smarthospital.model.entity.*;
import com.smarthospital.service.drug.DrugService;
import com.smarthospital.service.inpatient.InpatientService;
import com.smarthospital.service.outpatient.OutpatientService;
import com.smarthospital.service.payment.PaymentOrderService;
import com.smarthospital.service.registration.QueueService;
import com.smarthospital.service.registration.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kiosk/query")
@RequiredArgsConstructor
public class KioskQueryController {

    private final OutpatientService outpatientService;
    private final InpatientService inpatientService;
    private final RegistrationService registrationService;
    private final PaymentOrderService paymentOrderService;
    private final DrugService drugService;
    private final QueueService queueService;

    @GetMapping("/outpatient-orders")
    public R<List<OutpatientOrder>> queryOutpatientOrders(@RequestParam Long patientId) {
        return R.ok(outpatientService.queryByPatientId(patientId));
    }

    @GetMapping("/outpatient-items")
    public R<List<OutpatientOrderItem>> queryOutpatientItems(@RequestParam Long orderId) {
        return R.ok(outpatientService.queryItems(orderId));
    }

    @GetMapping("/inpatient-records")
    public R<List<InpatientDeposit>> queryInpatientRecords(@RequestParam Long patientId) {
        return R.ok(inpatientService.queryByPatientId(patientId));
    }

    @GetMapping("/registration-records")
    public R<List<Registration>> queryRegistrationRecords(@RequestParam Long patientId) {
        return R.ok(registrationService.queryByPatientId(patientId));
    }

    @GetMapping("/payment-details")
    public R<List<PaymentDetail>> queryPaymentDetails(@RequestParam Long paymentOrderId) {
        return R.ok(paymentOrderService.findDetailsByOrderId(paymentOrderId));
    }

    @GetMapping("/drugs")
    public R<List<DrugInfo>> searchDrugs(@RequestParam String keyword) {
        return R.ok(drugService.search(keyword));
    }

    @GetMapping("/queue")
    public R<List<Queue>> queryQueue(@RequestParam Long departmentId, @RequestParam Long doctorId) {
        return R.ok(queueService.queryByDepartmentAndDoctor(departmentId, doctorId));
    }
}