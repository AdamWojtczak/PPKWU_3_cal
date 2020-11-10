package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Ppkwu3CalApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ppkwu3CalApplication.class, args);
		try {
			EventsGetter.getMonthEvents();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
