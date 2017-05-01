package com.adfer.configuration;

import com.adfer.component.LcdDisplay;
import com.pi4j.io.gpio.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Created by adrianferenc on 29.04.2017.
 */
@Configuration
public class PrepareEnvironment implements ApplicationRunner {

    public static final Pin YELLOW_LED = RaspiPin.GPIO_01;
    public static final Pin GREEN_LED = RaspiPin.GPIO_05;
    public static final Pin RED_LED = RaspiPin.GPIO_06;
    public static final Pin BUTTON = RaspiPin.GPIO_04;
    public static LcdDisplay LCD;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final GpioController gpio = GpioFactory.getInstance();
        gpio.provisionDigitalInputPin(BUTTON, "Button", PinPullResistance.PULL_UP);
        gpio.provisionDigitalOutputPin(RED_LED, "RedLED", PinState.LOW);
        gpio.provisionDigitalOutputPin(YELLOW_LED, "YellowLED", PinState.LOW);
        gpio.provisionDigitalOutputPin(GREEN_LED, "GreenLED", PinState.LOW);
        LCD = initLcd();
        Thread lcdThread = new Thread(LCD, "LcdDisplay_thread");
        lcdThread.setDaemon(true);
        lcdThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gpio.setState(PinState.LOW, GpioFactory.getInstance().getProvisionedPins().stream()
                    .filter(p -> p.getMode() == PinMode.DIGITAL_OUTPUT)
                    .toArray(GpioPinDigitalOutput[]::new)
            );
            LCD.clear();
            gpio.unexportAll();
            gpio.shutdown();
        }));
    }

    private LcdDisplay initLcd() {
        // initialize LCD
        GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_26, "TurnLcdOn", PinState.HIGH);
        final LcdDisplay lcd = new LcdDisplay(
                RaspiPin.GPIO_25,  // LCD RS pin
                RaspiPin.GPIO_24,  // LCD strobe pin
                RaspiPin.GPIO_23,  // LCD data bit 5
                RaspiPin.GPIO_22,  // LCD data bit 6
                RaspiPin.GPIO_21,  // LCD data bit 7
                RaspiPin.GPIO_02); // LCD data bit 8
        lcd.clear();
        return lcd;
    }

}
