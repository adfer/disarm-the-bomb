package com.adfer.level;

import com.adfer.configuration.RaspPiConfiguration;
import com.adfer.game.Game;
import com.adfer.game.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractLevelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLevelHandler.class);

    public abstract String getLevelName();

    protected abstract Result getLevelLogic();

    public Result run(Game game) {
        Result result = null;
        LOGGER.info("Starting level {}", getLevelName());
        RaspPiConfiguration.LCD.clear();
        RaspPiConfiguration.LCD.writeLineA(String.format("Starting %s", getLevelName()), true);

        ExecutorService executorService = prepareExecutorService();
        Future<Result> levelLogicThread = executorService.submit(() -> getLevelLogic());

        while (!game.isTimeout() && !levelLogicThread.isDone()) {
            //level logic running
        }
        if (game.isTimeout()) {
            LOGGER.error("Level " + getLevelName() + " terminated because of game timeout.");
            levelLogicThread.cancel(true);
            return new Result(false, "Game timeout. Level " + getLevelName() + " terminated.");
        }
        if (levelLogicThread.isDone()) {
            try {
                result = levelLogicThread.get();
                LOGGER.info("Level {} done with result {}", getLevelName(), result.isSuccess());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("Level {} completed with result {}", getLevelName(), String.valueOf(result.isSuccess()).toUpperCase());
        RaspPiConfiguration.LCD.clear();
        RaspPiConfiguration.LCD.writeLineA(String.format("%s completed!", getLevelName()), true);
        return result;
    }

    private ExecutorService prepareExecutorService() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "Level-" + getLevelName());
            thread.setDaemon(true);
            return thread;
        });
    }

}
