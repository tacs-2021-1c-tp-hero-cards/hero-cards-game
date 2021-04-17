package ar.edu.utn.frba.tacs.tp.api.herocardsgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("ar.edu.utn.frba.tacs.tp.api.herocardsgame")
@SpringBootApplication
public class HeroCardsGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeroCardsGameApplication.class, args);
	}

}
