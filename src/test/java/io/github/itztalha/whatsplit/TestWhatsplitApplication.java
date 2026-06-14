package io.github.itztalha.whatsplit;

import org.springframework.boot.SpringApplication;

public class TestWhatsplitApplication {

	public static void main(String[] args) {
		SpringApplication.from(WhatsplitApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
