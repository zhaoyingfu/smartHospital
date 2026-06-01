package com.smarthospital.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class TradeNoUtil {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generate(String prefix) {
        String timestamp = LocalDateTime.now().format(FMT);
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return prefix + timestamp + random;
    }

    public static String regNo() {
        return generate("RG");
    }

    public static String paymentTradeNo() {
        return generate("PAY");
    }

    public static String depositNo() {
        return generate("DP");
    }

    public static String refundNo() {
        return generate("RF");
    }

    public static String orderNo() {
        return generate("OD");
    }
}
