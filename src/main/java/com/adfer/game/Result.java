package com.adfer.game;

public class Result {

    private boolean success;
    private String comment;

    public Result(boolean success, String comment) {
        this.success = success;
        this.comment = comment;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getComment() {
        return comment;
    }
}
