package com.adfer.game;

import com.adfer.configuration.PrepareEnvironment;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by adrianferenc on 29.04.2017.
 */
@Service
public class GamePicker {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePicker.class);

    private static long startTime;
    private AtomicInteger gameNumber = new AtomicInteger(1);
    private AtomicBoolean gameSelected = new AtomicBoolean();

    public int pickGame() {
        GpioPin button = GpioFactory.getInstance().getProvisionedPin(PrepareEnvironment.BUTTON);

        GpioPinListenerDigital pickGame = (GpioPinListenerDigital) event -> {
            if (event.getState().isLow()) {
                startTime = System.currentTimeMillis();
            } else {
                long pressTime = System.currentTimeMillis() - startTime;
                if (pressTime > 2000) {
                    LOGGER.debug("Pressed for at least 2 seconds. Exact press time {}", pressTime);
                    gameSelected.set(true);
                } else {
                    if (gameNumber.incrementAndGet() > 8) {
                        gameNumber.set(0);
                    }
                    LOGGER.debug("Current game number {}", gameNumber.get());
                }
            }
        };
        button.addListener(pickGame);
        while (!gameSelected.get()) {
            //wait for game selection
        }
        button.removeListener(pickGame);
        LOGGER.info("Selected game number {}", gameNumber.get());
        return gameNumber.get();
    }
}
