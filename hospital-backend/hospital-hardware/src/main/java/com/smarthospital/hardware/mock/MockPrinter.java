package com.smarthospital.hardware.mock;

import com.smarthospital.hardware.driver.PrinterDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockPrinter implements PrinterDriver {

    @Override
    public boolean print(String content) {
        log.info("[模拟打印] 内容:\n{}", content);
        return true;
    }

    @Override
    public boolean checkPaperStatus() {
        return true;
    }
}
