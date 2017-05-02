package com.adfer;

import com.adfer.game.Game;
import com.adfer.game.Result;
import com.adfer.level.Level;
import com.adfer.level.AbstractLevelHandler;
import com.adfer.level.Level1;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by adrianferenc on 29.04.2017.
 */
public class AbstractLevelHandlerTest {

    @Test
    public void shouldFinishLevelSuccessfully() throws Exception {
        //given
        AbstractLevelHandler AbstractLevelHandler = new Level1();
        Game game = new Game.GameBuilder().build();

        //when
        Result levelResult = AbstractLevelHandler.run(game);

        //then
        assertTrue(levelResult.isSuccess());
        assertEquals("Level LEVEL1 finished", levelResult.getComment());
    }

}