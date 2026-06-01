package com.smarthospital.hardware.mock;

import com.smarthospital.hardware.driver.ScannerDriver;
import org.springframework.stereotype.Component;

@Component
public class MockScanner implements ScannerDriver {

    @Override
    public String scanQrCode() {
        return "https://pay.example.com/mock-qr-" + System.currentTimeMillis();
    }

    @Override
    public String scanBarCode() {
        return "BAR" + System.currentTimeMillis();
    }
}
