package com.smarthospital.hardware.mock;

import com.smarthospital.hardware.driver.CardReadResult;
import com.smarthospital.hardware.driver.CardReaderDriver;
import org.springframework.stereotype.Component;

@Component
public class MockCardReader implements CardReaderDriver {

    @Override
    public CardReadResult readIdCard() {
        return CardReadResult.ok("110101199001011234", "模拟患者");
    }

    @Override
    public CardReadResult readMedicareCard() {
        return CardReadResult.ok("YB20260001", "模拟患者");
    }
}
