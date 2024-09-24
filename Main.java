package eecsminor;

import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.Timer;

public class Minor {

    private static final String port = "COM3";
    private static final byte I2Caddress = (byte) 0x3C;
    private static final int sensorPinNumber = 14;
    private static final int pumpPinNumber = 2;
    private static final int updateIntervalMS = 1000;

    public static void main(String[] args) throws IOException, InterruptedException {
        FirmataDevice arduino = new FirmataDevice(port);
        try {
            arduino.start();
            System.out.println("Board started.");
            arduino.ensureInitializationIsDone();
        } catch (Exception ex) {
            System.out.println("Couldn't connect to board.");
            return;
        }

        I2CDevice i2cDevice = arduino.getI2CDevice(I2Caddress);
        SSD1306 OLED = new SSD1306(i2cDevice, SSD1306.Size.SSD1306_128_64);
        OLED.init();

        Pin moisturePin = arduino.getPin(sensorPinNumber);
        Pin pumpPin = arduino.getPin(pumpPinNumber);

        moisturePin.setMode(Pin.Mode.ANALOG);
        pumpPin.setMode(Pin.Mode.OUTPUT);

        moisture mValue = new moisture(OLED, pumpPin, moisturePin);
        new Timer().schedule(mValue, 0, updateIntervalMS);
    }
}