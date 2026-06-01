package com.smarthospital.hardware.driver;

import com.smarthospital.hardware.driver.CardReadResult;

public interface CardReaderDriver {
    CardReadResult readIdCard();
    CardReadResult readMedicareCard();
}
