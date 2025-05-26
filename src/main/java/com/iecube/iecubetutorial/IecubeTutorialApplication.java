package com.iecube.iecubetutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IecubeTutorialApplication {

	public static void main(String[] args) {
		SpringApplication.run(IecubeTutorialApplication.class, args);
	}

}
