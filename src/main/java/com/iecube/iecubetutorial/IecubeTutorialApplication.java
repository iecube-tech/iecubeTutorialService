package com.iecube.iecubetutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableTransactionManagement // 启用事务管理
public class IecubeTutorialApplication {

	public static void main(String[] args) {
		SpringApplication.run(IecubeTutorialApplication.class, args);
	}

}
