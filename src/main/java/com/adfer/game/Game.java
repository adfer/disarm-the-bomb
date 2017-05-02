package com.adfer.game;

import com.adfer.configuration.RaspPiConfiguration;
import com.adfer.level.AbstractLevelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private List<AbstractLevelHandler> levels = new CopyOnWriteArrayList<>();
    private AtomicBoolean timeout = new AtomicBoolean(false);

    public boolean isTimeout() {
        return timeout.get();
    }

    private Game(GameBuilder gameBuilder) {
        this.levels.addAll(gameBuilder.levels);
    }

    public Result run() {
        Result result = null;
        for (AbstractLevelHandler level : levels) {
            result = level.run(this);
            LOGGER.info("Level {} finished with result {} and comment {}", level.getLevelName(), result.isSuccess(), result.getComment());
            if (result.isSuccess() == false) {
                break;
            }
        }
        return result;
    }

    public void timeExpired() {
        LOGGER.error("Game time expired!");
        RaspPiConfiguration.LCD.clear();
        RaspPiConfiguration.LCD.writeLineA("Game time expired!", true);
        this.timeout.set(true);
    }

    public static class GameBuilder {
        private List<AbstractLevelHandler> levels = new CopyOnWriteArrayList<>();

        public GameBuilder addLevel(AbstractLevelHandler level) {
            levels.add(level);
            return this;
        }

        public Game build() {
            return new Game(this);
        }

    }

}
