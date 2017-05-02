package com.adfer.level;

import com.adfer.configuration.RaspPiConfiguration;
import com.adfer.game.Result;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static com.adfer.util.GameUtil.sleep;

public class Level3 extends AbstractLevelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Level3.class);

    GpioController gpio = GpioFactory.getInstance();
    GpioPinDigitalOutput greenLed = (GpioPinDigitalOutput) gpio.getProvisionedPin(RaspPiConfiguration.GREEN_LED);
    GpioPinDigitalOutput yellowLed = (GpioPinDigitalOutput) gpio.getProvisionedPin(RaspPiConfiguration.YELLOW_LED);
    GpioPinDigitalOutput redLed = (GpioPinDigitalOutput) gpio.getProvisionedPin(RaspPiConfiguration.RED_LED);
    GpioPinDigitalOutput sensorTriggerPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(RaspPiConfiguration.ULTRASONIC_TRIGGER);
    GpioPinDigitalOutput sensorEchoPin = (GpioPinDigitalOutput) gpio.getProvisionedPin(RaspPiConfiguration.ULTRASONIC_ECHO);

    private final static int DISTANCE_TO_SET = 20;
    private final static float ACCEPTABLE_DISTANCE_MARGIN = 0.5f;
    private final static float CLOSE_DISTANCE_MARGIN = 1f;

    @Override
    public String getLevelName() {
        return Level.LEVEL3.name();
    }

    @Override
    protected Result getLevelLogic() {
        double distance;
        AtomicInteger count = new AtomicInteger();
        while (true) {
            sleep(500);
            sensorTriggerPin.high(); // Make trigger pin HIGH
            sleep((int) 0.01);// Delay for 10 microseconds
            sensorTriggerPin.low(); //Make trigger pin LOW

            while (sensorEchoPin.isLow()) {
                //Wait until the ECHO pin gets HIGH
            }
            long startTime = System.nanoTime(); // Store the surrent time to calculate ECHO pin HIGH time.
            while (sensorEchoPin.isHigh()) { //Wait until the ECHO pin gets LOW

            }
            long endTime = System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.

            distance = (((endTime - startTime) / 1e3) / 2) / 29.1;
            LOGGER.debug("Distance : {} cm", distance);
            if (isDistanceInRange(distance, ACCEPTABLE_DISTANCE_MARGIN)) {
                turnOnLed(Led.GREEN);
                LOGGER.debug("\tcounting -> {}", count.get());
                if (count.incrementAndGet() > 3) {
                    break;
                }
                sleep(500);
            } else if (isDistanceInRange(distance, CLOSE_DISTANCE_MARGIN)) {
                turnOnLed(Led.YELLOW);
                count.set(0);
            } else {
                turnOnLed(Led.RED);
                count.set(0);
            }
        }
        LOGGER.info("SUCCESS: Your distance was {}", distance);
        RaspPiConfiguration.LCD.clear();
        RaspPiConfiguration.LCD.writeLineA(String.format("Level %s finished", getLevelName()), true);
        RaspPiConfiguration.LCD.writeLineB(String.format("Your distance was %.2f", distance), true);
        return new Result(true, String.format("Your distance was %.2f", distance));
    }

    private boolean isDistanceInRange(double distance, float acceptableDistanceMargin) {
        return distance >= DISTANCE_TO_SET - acceptableDistanceMargin && distance <= DISTANCE_TO_SET + acceptableDistanceMargin;
    }

    private enum Led {
        RED,
        GREEN,
        YELLOW
    }

    private void turnOnLed(Led led) {
        redLed.setState(PinState.LOW);
        greenLed.setState(PinState.LOW);
        yellowLed.setState(PinState.LOW);
        switch (led) {
            case RED:
                redLed.setState(PinState.HIGH);
                break;
            case GREEN:
                greenLed.setState(PinState.HIGH);
                break;
            case YELLOW:
                yellowLed.setState(PinState.HIGH);
                break;

        }
    }
}
