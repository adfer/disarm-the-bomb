package com.adfer.level;

import com.adfer.configuration.RaspPiConfiguration;
import com.adfer.game.Result;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class Level1 extends AbstractLevelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Level1.class);
    private AtomicInteger count = new AtomicInteger();

    @Override
    public String getLevelName() {
        return Level.LEVEL1.name();
    }

    @Override
    protected Result getLevelLogic() {
        Result result = null;
        GpioPin button = GpioFactory.getInstance().getProvisionedPin(RaspPiConfiguration.BUTTON);
        GpioPinListenerDigital pressButton = event -> {
            if (event.getState().isLow()) {
                GpioPinDigitalOutput led = (GpioPinDigitalOutput) GpioFactory.getInstance().getProvisionedPin(RaspPiConfiguration.RED_LED);
                led.blink(500, 300);
                LOGGER.info("Count is: {}", count.incrementAndGet());
            }
        };
        button.addListener(pressButton);
        while (!Thread.interrupted()) {
            //wait for game selection
            if (count.get() == 4) {
                result = new Result(true, "Count == 4");
                break;
            }
        }
        button.removeAllListeners();
        if (result == null) {
            result = new Result(false, String.format("Count != 4. Count is %s", count));
        }
        return result;
    }
}
