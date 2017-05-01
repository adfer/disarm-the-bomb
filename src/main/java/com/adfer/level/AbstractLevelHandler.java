package com.adfer.level;

import com.adfer.configuration.PrepareEnvironment;
import com.adfer.game.Result;
import org.slf4j.Logger;

/**
 * Created by adrianferenc on 30.04.2017.
 */
public abstract class AbstractLevelHandler {

    long startTime;

    abstract String getLevelName();

    abstract Logger getLogger();

    abstract long getLevelTimeInSeconds();

    protected abstract Result runLevelLogic();

    public final boolean isTimeout() {
        return (System.currentTimeMillis() - startTime) / 1000 > getLevelTimeInSeconds();
    }

    public Result runLevel() {
        Result result = null;
        startTime = System.currentTimeMillis();
        PrepareEnvironment.LCD.clear();
        PrepareEnvironment.LCD.writeLineA(String.format("Starting %s", getLevelName()), true);
        getLogger().info("Starting level {}", getLevelName());
        while (!isTimeout() && result == null) {
            result = runLevelLogic();
            if (result != null) {
                break;
            }
        }
        getLogger().info("Level {} completed with result {}", getLevelName(), String.valueOf(result.isSuccess()).toUpperCase());
        PrepareEnvironment.LCD.clear();
        PrepareEnvironment.LCD.writeLineB(String.format("%s finished", getLevelName()), true);
        try {
            //TODO change it
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

}
