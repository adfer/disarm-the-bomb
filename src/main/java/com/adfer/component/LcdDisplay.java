package com.adfer.component;

import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.Pin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by adrianferenc on 30.04.2017.
 */
public class LcdDisplay implements Runnable {

    public final static int LCD_ROWS = 2;
    public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;
    public final static int LCD_COLUMNS = 16;
    private static final String SPACE_AFTER_TEXT = "   ";
    private static final String SPACE_BEFORE_TEXT = "  ";

    public final boolean DEBUG = false;

    public final int SCROLL_SPEED = 500;

    private GpioLcdDisplay lcd;

    private String lineA;

    private String lineB;

    private boolean scrollA = false;

    private boolean scrollB = false;

    private static final Logger log = LoggerFactory.getLogger(LcdDisplay.class);

    public LcdDisplay(Pin rs, Pin e, Pin d1, Pin d2, Pin d3, Pin d4) {
        if (!DEBUG) {
            lcd = new GpioLcdDisplay(LCD_ROWS, LCD_COLUMNS, rs, e, d1, d2, d3,
                    d4);
        }
    }

    public void clear() {
        if (!DEBUG) {
            scrollA = false;
            scrollB = false;
            lineA = "";
            lineB = "";
            lcd.clear();
        }
    }

    public void writeLineA(String text, boolean scroll) {
        this.lineA = SPACE_BEFORE_TEXT.concat(text.concat(SPACE_AFTER_TEXT));
        this.scrollA = scroll;
        writeA(this.lineA);
    }

    public void writeLineB(String text, boolean scroll) {
        this.lineB = SPACE_BEFORE_TEXT.concat(text.concat(SPACE_AFTER_TEXT));
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
            log.error("Lcd Display shutdown.", e);
        }
    }

    private void writeA(String text) {

        int length = Math.min(LCD_COLUMNS, text.length());

        String output = text.subSequence(0, length - 1).toString();

        if (!DEBUG) {
            lcd.writeln(LCD_ROW_1, output);
        } else {
            log.debug(output);
        }

    }

    private void writeB(String text) {

        int length = Math.min(LCD_COLUMNS, text.length());
        String output = text.subSequence(0, length - 1).toString();

        if (!DEBUG) {
            lcd.writeln(LCD_ROW_2, output);
        } else {
            log.debug(output);
        }

    }

}