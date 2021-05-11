package br.com.fatec.petfood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan("br.com.fatec.petfood.config")
public class PetFoodApplication {

    private static final Logger logger = LoggerFactory.getLogger(PetFoodApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PetFoodApplication.class, args);

        logger.info("\n----------------------------------------------------------\n\t" +
                "Application Pet Food is running!\n\t" +
                "Local: http://localhost:8080\n\t" +
                "Swagger: http://localhost:8080/swagger-ui.html" +
                "\n----------------------------------------------------------");
    }
}
