package com.adfer.level;

import com.adfer.game.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by adrianferenc on 02.05.2017.
 */
public class Level2 extends AbstractLevelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Level2.class);


    @Override
    public String getLevelName() {
        return Level.LEVEL2.name();
    }

    @Override
    protected Result getLevelLogic() {
        int i = 0;
        while (!Thread.interrupted() && i < 10) {
            System.out.println(i++);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new Result(true, "some fake comment from level 2");
    }
}
