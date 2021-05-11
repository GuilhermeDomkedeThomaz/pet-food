package br.com.fatec.petfood.schedule;

import br.com.fatec.petfood.config.ScheduleConfig;
import br.com.fatec.petfood.service.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RequestSchedule {

    private final RequestService requestService;
    private final ScheduleConfig scheduleConfig;
    private static final Logger logger = LoggerFactory.getLogger(RequestSchedule.class);

    public RequestSchedule(RequestService requestService, ScheduleConfig scheduleConfig) {
        this.requestService = requestService;
        this.scheduleConfig = scheduleConfig;
    }

    @Scheduled(cron = "${schedule.request}")
    public void cancelRequestSchedule() {
        logger.info("Starting schedule for cancel request on status created.");

        try {
            requestService.cancelRequestSchedule(scheduleConfig.getMinutesQuery(), scheduleConfig.getPage(), scheduleConfig.getSize());
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getCause());
        }

        logger.info("Finishing schedule for cancel request on status created.");
    }
}
