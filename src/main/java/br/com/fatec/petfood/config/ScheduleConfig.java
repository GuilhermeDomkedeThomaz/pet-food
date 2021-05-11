package br.com.fatec.petfood.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("schedule.request")
public class ScheduleConfig {

    private Integer minutesQuery;

    private Integer page;

    private Integer size;
}
