package com.adfer.component;

import com.adfer.game.Game;

import java.util.TimerTask;

public class GameTimeoutHandler extends TimerTask {

    Game callbackClass;

    public GameTimeoutHandler(Game callbackClass) {
        this.callbackClass = callbackClass;
    }

    public void run() {
        callbackClass.timeExpired();
    }
}