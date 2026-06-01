package com.smarthospital.hardware.driver;

public interface PrinterDriver {
    boolean print(String content);
    boolean checkPaperStatus();
}
