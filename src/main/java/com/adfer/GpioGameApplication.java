package com.adfer;

import com.adfer.game.GameStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GpioGameApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(GpioGameApplication.class, args);
        GameStarter gameStarter = app.getBean(GameStarter.class);
        gameStarter.start();
        SpringApplication.exit(app);
    }
}