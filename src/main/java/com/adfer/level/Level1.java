package com.adfer.level;

import com.adfer.configuration.PrepareEnvironment;
import com.adfer.game.Result;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by adrianferenc on 30.04.2017.
 */
public class Level1 extends AbstractLevelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Level1.class);
    private AtomicInteger count = new AtomicInteger();

    @Override
    String getLevelName() {
        return Level.LEVEL1.name();
    }

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    @Override
    long getLevelTimeInSeconds() {
        return 5;
    }


    @Override
    protected Result runLevelLogic() {
        GpioPin button = GpioFactory.getInstance().getProvisionedPin(PrepareEnvironment.BUTTON);
        GpioPinListenerDigital pressButton = event -> {
            if (event.getState().isLow()) {
                GpioPinDigitalOutput led = (GpioPinDigitalOutput) GpioFactory.getInstance().getProvisionedPin(PrepareEnvironment.RED_LED);
                led.blink(500, 500);
                LOGGER.info("Count is: {}", count.incrementAndGet());
            }
        };
        button.addListener(pressButton);
        while (!isTimeout() || count.get() < 4) {
            //wait for game selection
        }

        if (count.get() == 4) {
            return new Result(true, "Count == 4");
        }
        return new Result(false, String.format("Count != 4. Count is %s", count));
    }
}
