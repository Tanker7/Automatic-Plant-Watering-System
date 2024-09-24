package eecsminor;

import org.firmata4j.Pin;
import org.firmata4j.ssd1306.MonochromeCanvas;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.TimerTask;
import java.util.HashMap;

public class moisture extends TimerTask {

    private final SSD1306 display;
    private final Pin pumpPin;
    private final Pin soilMoisturePin;
    private HashMap<Integer, Integer> sensorValues;  // HashMap to store sensor values for each pin

    private final int dryMoistureValue = 728;
    private final int minMoistureThreshold = 660;
    private final int wetMoistureValue = 550;
    private final int displayHeight = 60;

    moisture(SSD1306 display, Pin pumpPin, Pin soilMoisturePin) {
        this.display = display;
        this.pumpPin = pumpPin;
        this.soilMoisturePin = soilMoisturePin;
    }

    @Override
    public void run() {

        int soilMoistureValue = (int) this.soilMoisturePin.getValue();
        int moisturePercentage = ((soilMoistureValue - wetMoistureValue) * 100) / (dryMoistureValue - wetMoistureValue);

        if (soilMoistureValue >= dryMoistureValue) {
            try {
                pumpPin.setValue(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawDisplay("Moisture: Dry", "Pump is ON and watering the soil!", soilMoistureValue, moisturePercentage);
        } else if (soilMoistureValue > minMoistureThreshold) {
            try {
                pumpPin.setValue(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawDisplay("Moisture: Moderate", "Pump is ON and watering the soil!", soilMoistureValue, moisturePercentage);
        } else if (soilMoistureValue <= wetMoistureValue) {
            try {
                pumpPin.setValue(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawDisplay("Moisture: Wet", "Pump is OFF, soil is watered enough.", soilMoistureValue, moisturePercentage);
        }
    }

    private void drawDisplay(String moistureLevel, String pumpStatus, int soilMoistureValue, int moisturePercentage) {
        display.getCanvas().clear();
        display.getCanvas().drawString(0, 0, moistureLevel);
        display.getCanvas().drawString(0, 10, pumpStatus);
        display.getCanvas().drawString(0, 30, "Moisture Value: " + String.valueOf(soilMoistureValue));

        int moistureBarHeight = displayHeight * moisturePercentage / 100;
        display.getCanvas().drawHorizontalLine(0, 55, displayHeight, MonochromeCanvas.Color.DARK);
        display.getCanvas().drawHorizontalLine(0, 55, moistureBarHeight, MonochromeCanvas.Color.BRIGHT);
        display.display();
    }
}