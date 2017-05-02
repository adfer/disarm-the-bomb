package com.adfer.configuration;

import com.adfer.component.LcdDisplay;
import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class RaspPiConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RaspPiConfiguration.class);


    public static final Pin YELLOW_LED = RaspiPin.GPIO_01;
    public static final Pin GREEN_LED = RaspiPin.GPIO_05;
    public static final Pin RED_LED = RaspiPin.GPIO_06;
    public static final Pin BUTTON = RaspiPin.GPIO_04;
    public static final Pin ULTRASONIC_TRIGGER = RaspiPin.GPIO_03;
    public static final Pin ULTRASONIC_ECHO = RaspiPin.GPIO_07;
    public static LcdDisplay LCD;

    @PostConstruct
    public void setUp() {
        initButtons();
        initLED();
        initLCD();
        initUltrasonicDistanceSensor();
    }

    private void initUltrasonicDistanceSensor() {
        LOGGER.info("Initializing ultrasonic sensor...");
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput ss = gpio.provisionDigitalOutputPin(ULTRASONIC_TRIGGER); // Trigger pin as OUTPUT
        gpio.provisionDigitalInputPin(ULTRASONIC_ECHO, PinPullResistance.PULL_DOWN); // Echo pin as INPUT
    }

    @PreDestroy
    public void cleanUp() {
        LOGGER.info("Cleaning up before shutdown...");
        LCD.clear();
        GpioController gpio = GpioFactory.getInstance();
        gpio.setState(PinState.LOW, GpioFactory.getInstance().getProvisionedPins().stream()
                .filter(p -> p.getMode() == PinMode.DIGITAL_OUTPUT)
                .toArray(GpioPinDigitalOutput[]::new)
        );
        gpio.unexportAll();
        gpio.shutdown();
    }

    private void initButtons() {
        LOGGER.info("Initializing buttons...");
        GpioController gpio = GpioFactory.getInstance();
        gpio.provisionDigitalInputPin(BUTTON, "Button", PinPullResistance.PULL_UP);
    }

    private void initLED() {
        LOGGER.info("Initializing LEDs...");
        GpioController gpio = GpioFactory.getInstance();
        gpio.provisionDigitalOutputPin(RED_LED, "RedLED", PinState.LOW);
        gpio.provisionDigitalOutputPin(YELLOW_LED, "YellowLED", PinState.LOW);
        gpio.provisionDigitalOutputPin(GREEN_LED, "GreenLED", PinState.LOW);
    }

    private void initLCD() {
        LOGGER.info("Initializing LCD display...");
        // turn LCD on
        GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_26, "TurnLcdOn", PinState.HIGH);
        LCD = new LcdDisplay(
                RaspiPin.GPIO_25,  // LCD RS pin
                RaspiPin.GPIO_24,  // LCD strobe pin
                RaspiPin.GPIO_23,  // LCD data bit 5
                RaspiPin.GPIO_22,  // LCD data bit 6
                RaspiPin.GPIO_21,  // LCD data bit 7
                RaspiPin.GPIO_02); // LCD data bit 8
        LCD.clear();
        Thread lcdThread = new Thread(LCD, "LcdDisplay_thread");
        lcdThread.setDaemon(true);
        lcdThread.start();
    }

}
