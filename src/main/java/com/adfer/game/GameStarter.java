package com.adfer.game;

import com.adfer.configuration.PrepareEnvironment;
import com.adfer.level.AbstractLevelHandler;
import com.adfer.level.Level1;
import com.pi4j.io.gpio.GpioFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by adrianferenc on 29.04.2017.
 */
@Component
public class GameStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameStarter.class);
    private GamePicker gamePicker;

    @Autowired
    public GameStarter(GamePicker gamePicker) {
        this.gamePicker = gamePicker;
    }

    public void start() {
        LOGGER.info("Starting game...");
        PrepareEnvironment.LCD.clear();
        PrepareEnvironment.LCD.writeLineA("Hello! :)", false);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int gameNumber = pickGame();
        Game game = prepareGame(gameNumber);
        verifyResult(game.run());
        terminateGame();
    }

    private int pickGame() {
        LOGGER.info("Pick a game...");
        PrepareEnvironment.LCD.clear();
        PrepareEnvironment.LCD.writeLineA("Pick game number...", true);
        int gameNumber = gamePicker.pickGame();
        LOGGER.info("Picked game number {}", gameNumber);
        PrepareEnvironment.LCD.clear();
        PrepareEnvironment.LCD.writeLineB(String.format("Game %s selected", gameNumber), true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gameNumber;
    }

    private Game prepareGame(int gameNumber) {
        LOGGER.debug("Preparing game number {}", gameNumber);
        AbstractLevelHandler level1 = new Level1();
        return new Game.GameBuilder()
                .addLevel(level1)
                .build();
    }

    private void verifyResult(boolean gameResult) {
        LOGGER.info("Game finished with result {}", String.valueOf(gameResult).toUpperCase());
        PrepareEnvironment.LCD.clear();
        PrepareEnvironment.LCD.writeLineA(String.format("Game finished with result %s", String.valueOf(gameResult).toUpperCase()), true);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void terminateGame() {
        GpioFactory.getInstance().shutdown();
    }
}
