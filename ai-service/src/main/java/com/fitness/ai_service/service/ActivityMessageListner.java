package com.fitness.ai_service.service;

import com.fitness.ai_service.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListner {

    private final  ActivityAIService aiService;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity)
    {
        log.info("Recieved activity from teh Rabbit MQ : {}",activity);

        log.info("Generated Recommendations {}",aiService.generateRecommendation(activity));
        System.out.println("");
    }
}
