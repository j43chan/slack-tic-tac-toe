package com.jermaine.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorController;

@SpringBootApplication
public class App{
    public static void main( String[] args ){
        SpringApplication.run( App.class, args);
    }
}
