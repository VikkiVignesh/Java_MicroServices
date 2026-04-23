package com.fitness.activityservice.service;

import com.fitness.activityservice.dtos.ActivityRequest;
import com.fitness.activityservice.dtos.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {


    private final ActivityRepo activityRepo;
    private final UserValidationService uservalidation;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routing;
    private final RabbitTemplate rabbitTemplate;
    public Mono<ActivityResponse> createActivity(ActivityRequest request) {

        return uservalidation.validateUser(request.getUserId())
                .flatMap(isValidUser -> {

                    if (!isValidUser) {
                        return Mono.error(new RuntimeException("User Not Found"));
                    }

                    Activity activity = Activity.builder()
                            .userId(request.getUserId())
                            .type(request.getType())
                            .duration(request.getDuration())
                            .caloriesBurned(request.getCaloriesBurned())
                            .startTime(request.getStartTime())
                            .additionalMetrics(request.getAdditionalMetrics())
                            .build();

                    Activity saved = activityRepo.save(activity); // ⚠ still blocking

                    //after cratin of activity pusblish that data to rabbit mq
                    try{
                        rabbitTemplate.convertAndSend(exchange,routing,saved);
                    }
                    catch (Exception e)
                    {
                        log.error("Failed to Push activity to Rabbit MQ");
                    }

                    return Mono.just(convertToDto(saved));
                });
    }

    private ActivityResponse convertToDto(Activity act) {

        if (act == null) return null;

        ActivityResponse response = new ActivityResponse();

        response.setId(act.getId());
        response.setUserId(act.getUserId());
        response.setType(act.getType());
        response.setDuration(act.getDuration());
        response.setCaloriesBurned(act.getCaloriesBurned());
        response.setStartTime(act.getStartTime());
        response.setAdditionalMetrics(act.getAdditionalMetrics());
        response.setCreatedAt(act.getCreatedAt());
        response.setUpdatedAt(act.getUpdatedAt());

        return response;
    }

    public ActivityResponse getActivityById(String activityId) {
     Activity existing=activityRepo.findById(activityId).orElseThrow(()-> new RuntimeException("Activity Not Found"));

     return convertToDto(existing);
    }

    public List<ActivityResponse> getActivityByUserId(String userId) {
     List<Activity> activities=activityRepo.findAllByUserId(userId);
     return activities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
