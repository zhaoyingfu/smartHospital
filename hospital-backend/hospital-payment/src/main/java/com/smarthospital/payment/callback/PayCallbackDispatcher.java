package com.smarthospital.payment.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayCallbackDispatcher {

    public void onPaySuccess(String outTradeNo, String payMethod, String tradeNo) {
        log.info("[支付回调] 支付成功: outTradeNo={}, payMethod={}, tradeNo={}", outTradeNo, payMethod, tradeNo);
    }

    public void onPayFail(String outTradeNo, String payMethod, String failReason) {
        log.info("[支付回调] 支付失败: outTradeNo={}, payMethod={}, failReason={}", outTradeNo, payMethod, failReason);
    }

    public void onRefundSuccess(String refundNo, String payMethod, String refundTradeNo) {
        log.info("[退款回调] 退款成功: refundNo={}, payMethod={}, refundTradeNo={}", refundNo, payMethod, refundTradeNo);
    }

    public void onRefundFail(String refundNo, String payMethod, String failReason) {
        log.info("[退款回调] 退款失败: refundNo={}, payMethod={}, failReason={}", refundNo, payMethod, failReason);
    }
}
