package com.adfer.component;

import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.Pin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LcdDisplay implements Runnable {

    public final static int LCD_ROWS = 2;
    public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;
    public final static int LCD_COLUMNS = 16;
    private static final String SPACE_AFTER_TEXT = "   ";
    private static final String SPACE_BEFORE_TEXT = "  ";

    public final boolean DEBUG = false;

    public final int SCROLL_SPEED = 300;

    private GpioLcdDisplay lcd;

    private String lineA;

    private String lineB;

    private boolean scrollA = false;

    private boolean scrollB = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(LcdDisplay.class);

    public LcdDisplay(Pin rs, Pin e, Pin d1, Pin d2, Pin d3, Pin d4) {
        if (!DEBUG) {
            lcd = new GpioLcdDisplay(LCD_ROWS, LCD_COLUMNS, rs, e, d1, d2, d3,
                    d4);
        }
    }

    public void clear() {
        clearLineA();
        clearLineB();
    }

    public void clearLineA() {
        if (!DEBUG) {
            scrollA = false;
            lineA = "";
            lcd.clear(LCD_ROW_1);
        }
    }

    public void clearLineB() {
        if (!DEBUG) {
            scrollB = false;
            lineB = "";
            lcd.clear(LCD_ROW_2);
        }
    }

    public void writeLineA(String text, boolean scroll) {
        this.lineA = text.concat(SPACE_AFTER_TEXT);
        if (scroll) {
            this.lineA = SPACE_BEFORE_TEXT.concat(this.lineA);
        }
        this.scrollA = scroll;
        writeA(this.lineA);
    }

    public void writeLineB(String text, boolean scroll) {
        this.lineB = text.concat(SPACE_AFTER_TEXT);
        if (scroll) {
            this.lineB = SPACE_BEFORE_TEXT.concat(this.lineB);
        }
        this.scrollB = scroll;
        writeB(this.lineB);
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (scrollA && !lineA.isEmpty()) {
                    lineA = lineA.substring(1) + lineA.substring(0, 1);
                    writeA(lineA);
                }
                if (scrollB && !lineB.isEmpty()) {
                    lineB = lineB.substring(1) + lineB.substring(0, 1);
                    writeB(lineB);
                }

                Thread.sleep(SCROLL_SPEED);

            }
        } catch (InterruptedException e) {
            LOGGER.error("Lcd Display shutdown.", e);
        }
    }

    private void writeA(String text) {

        int length = Math.min(LCD_COLUMNS, text.length());

        String output = text.subSequence(0, length - 1).toString();

        if (!DEBUG) {
            lcd.writeln(LCD_ROW_1, output);
        } else {
            LOGGER.debug(output);
        }

    }

    private void writeB(String text) {

        int length = Math.min(LCD_COLUMNS, text.length());
        String output = text.subSequence(0, length - 1).toString();

        if (!DEBUG) {
            lcd.writeln(LCD_ROW_2, output);
        } else {
            LOGGER.debug(output);
        }

    }

}