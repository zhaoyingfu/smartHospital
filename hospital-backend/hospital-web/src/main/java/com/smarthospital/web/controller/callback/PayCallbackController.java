package com.smarthospital.web.controller.callback;

import com.smarthospital.payment.callback.PayCallbackDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/callback/pay")
@RequiredArgsConstructor
public class PayCallbackController {

    private final PayCallbackDispatcher dispatcher;

    @PostMapping("/wechat")
    public String wechatCallback(@RequestBody String body) {
        log.info("[微信支付回调] body={}", body);
        dispatcher.onPaySuccess("MOCK_OUT_TRADE_NO", "WECHAT", "WX_MOCK_TRADE_NO");
        return "SUCCESS";
    }

    @PostMapping("/alipay")
    public String alipayCallback(@RequestBody String body) {
        log.info("[支付宝支付回调] body={}", body);
        dispatcher.onPaySuccess("MOCK_OUT_TRADE_NO", "ALIPAY", "ALI_MOCK_TRADE_NO");
        return "success";
    }

    @PostMapping("/bank")
    public String bankCallback(@RequestBody String body) {
        log.info("[银行卡支付回调] body={}", body);
        dispatcher.onPaySuccess("MOCK_OUT_TRADE_NO", "BANK_CARD", "BANK_MOCK_TRADE_NO");
        return "OK";
    }

    @PostMapping("/medicare")
    public String medicareCallback(@RequestBody String body) {
        log.info("[医保支付回调] body={}", body);
        dispatcher.onPaySuccess("MOCK_OUT_TRADE_NO", "MEDICARE", "MED_MOCK_TRADE_NO");
        return "OK";
    }
}