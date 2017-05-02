package com.adfer.game;

import com.adfer.component.GameTimeoutHandler;
import com.adfer.level.AbstractLevelHandler;
import com.adfer.level.Level1;
import com.adfer.level.Level2;
import com.adfer.level.Level3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Timer;

@Component
public class GameStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameStarter.class);

    @Value("${game.time.limit.seconds}")
    public int GAME_TIME_LIMIT_IN_SECONDS;

    private GamePicker gamePicker;

    @Autowired
    public GameStarter(GamePicker gamePicker) {
        this.gamePicker = gamePicker;
    }

    public void start() {
        int gameNumber = gamePicker.pickGame();
        Game game = prepareGame(gameNumber);
        runGame(game);
    }

    private Game prepareGame(int gameNumber) {
        LOGGER.debug("Preparing game number {}", gameNumber);
        AbstractLevelHandler level1 = new Level1();
        AbstractLevelHandler level2 = new Level2();
        AbstractLevelHandler level3 = new Level3();
        return new Game.GameBuilder()
                .addLevel(level1)
                .addLevel(level2)
                .addLevel(level3)
                .build();
    }

    private void runGame(Game game) {
        Timer gameTimer = new Timer();
        gameTimer.schedule(new GameTimeoutHandler(game), GAME_TIME_LIMIT_IN_SECONDS * 1000);
        game.run();
        gameTimer.cancel();
    }
}
