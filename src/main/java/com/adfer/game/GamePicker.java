package com.adfer.game;

import com.adfer.configuration.RaspPiConfiguration;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.adfer.util.GameUtil.sleep;

@Service
public class GamePicker {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePicker.class);

    private AtomicInteger gameNumber = new AtomicInteger(1);
    private AtomicBoolean gameSelected = new AtomicBoolean();

    public int pickGame() {
        LOGGER.info("Picking game...");
        RaspPiConfiguration.LCD.clear();
        RaspPiConfiguration.LCD.writeLineA("Please select a game. Long press to accept", true);
        RaspPiConfiguration.LCD.writeLineB(String.format("Selected %s", gameNumber.get()), false);

        AtomicLong startTime = new AtomicLong();
        GpioPin button = GpioFactory.getInstance().getProvisionedPin(RaspPiConfiguration.BUTTON);
        GpioPinListenerDigital pickGame = event -> {
            if (event.getState().isLow()) {
                startTime.set(System.currentTimeMillis());
            } else {
                long pressTime = System.currentTimeMillis() - startTime.get();
                if (pressTime > 2000) {
                    LOGGER.debug("Pressed for at least 2 seconds. Exact press time {}", pressTime);
                    gameSelected.set(true);
                } else {
                    if (gameNumber.incrementAndGet() > 8) {
                        gameNumber.set(1);
                    }
                    RaspPiConfiguration.LCD.clearLineB();
                    RaspPiConfiguration.LCD.writeLineB(String.format("Selected %s", gameNumber.get()), false);
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
        RaspPiConfiguration.LCD.clear();
        RaspPiConfiguration.LCD.writeLineA(String.format("Game %s selected", gameNumber.get()), true);
        sleep(3000);
        return gameNumber.get();
    }
}
