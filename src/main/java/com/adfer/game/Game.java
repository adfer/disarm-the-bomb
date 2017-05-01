package com.adfer.game;

import com.adfer.level.AbstractLevelHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by adrianferenc on 29.04.2017.
 */
public class Game {
    private List<AbstractLevelHandler> levels = new CopyOnWriteArrayList<>();

    private Game(GameBuilder gameBuilder) {
        this.levels.addAll(gameBuilder.levels);
    }

    public boolean run() {
        boolean isFalseResult = this.levels.stream()
                .map(AbstractLevelHandler::runLevel)
                .filter(r -> r.isSuccess() == false)
                .findAny()
                .isPresent();
        return !isFalseResult;
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
