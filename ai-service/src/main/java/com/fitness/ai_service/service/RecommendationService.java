package com.fitness.ai_service.service;

import com.fitness.ai_service.model.Recommendation;
import com.fitness.ai_service.repo.RecommendationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepo recommendationRepo;

    public List<Recommendation> getUserRecommendations(String userId) {
      return recommendationRepo.findByUserId(userId);
    }

    public Recommendation getActivityRecommendations(String activityId) {
     return recommendationRepo.findByActivityId(activityId).orElseThrow(()-> new RuntimeException("Recommendation not found.."));
    }
}
