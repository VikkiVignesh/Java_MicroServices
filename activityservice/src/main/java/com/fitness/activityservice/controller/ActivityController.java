package com.fitness.activityservice.controller;

import com.fitness.activityservice.dtos.ActivityRequest;
import com.fitness.activityservice.dtos.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    @PostMapping("/create")
    public Mono<ActivityResponse> trackActivity(@RequestBody ActivityRequest request) {
        return activityService.createActivity(request);
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable String activityId)
    {
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityResponse>> getActivityByUserIdId(@PathVariable String userId)
    {
        return ResponseEntity.ok(activityService.getActivityByUserId(userId));
    }

}
