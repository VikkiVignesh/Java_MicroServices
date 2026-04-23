package com.fitness.ai_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.ai_service.model.Activity;
import com.fitness.ai_service.model.Recommendation;
import com.fitness.ai_service.repo.RecommendationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;
    private final RecommendationRepo recRepo;

    public String generateRecommendation(Activity activity)
    {
        String prompt=createPromptForActivity(activity);

        String aiResponse= geminiService.getAnswer(prompt);
        log.info("Response from the AI : {} ",aiResponse);
        processAIResponse(activity,aiResponse);
        return aiResponse;
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
Analyze this fitness activity and generate a structured JSON response.

### Current Activity:
- Activity ID: %s
- User ID: %s
- Activity Type: %s
- Duration: %d minutes
- Calories Burned: %d
- Additional Metrics: %s

### Previous Activities (User History):

Instructions:
- Compare the current activity with previous activities.
- Identify trends (improvement, decline, consistency).
- Adjust recommendations based on past performance.
- If no history is available, rely only on current activity.

Output Requirements:
- Return ONLY valid JSON (no explanation, no extra text, no markdown).
- Follow the EXACT structure below.
- Do NOT include null values.

### JSON Structure:
{
  "activityId": "string",
  "userId": "string",
  "activityType": "string",
  "analysis": {
    "overall": "string",
    "pace": "string",
    "heartRate": "string",
    "caloriesBurned": "string",
    "trend": "string"
  },
  "improvements": [
    {
      "area": "string",
      "recommendation": "string"
    }
  ],
  "suggestions": ["string"],
  "safety": ["string"]
}

### Rules:

1. Identity Mapping:
- activityId = current Activity ID
- userId = current User ID
- activityType = current Activity Type

2. Trend Analysis (IMPORTANT):
- Compare with previous activities
- Mention if performance is:
  - Improving
  - Declining
  - Consistent
- Use metrics like duration, calories, distance

3. Analysis:
- overall: 1–2 line summary including trend
- pace: compare speed/duration with past
- heartRate: infer intensity or compare if available
- caloriesBurned: efficiency vs past
- trend: one-line conclusion (e.g., "Performance improving steadily")

4. Improvements:
- Provide exactly 3–5 items
- Must address weaknesses observed from comparison
- Each item:
  - area (Endurance, Speed, Consistency, etc.)
  - recommendation (specific actionable advice)

5. Suggestions:
- Provide exactly 3–5 items
- Each must:
  - Start with action verb
  - Be specific and based on trend
  - Not repeat improvements

6. Safety:
- Provide exactly 2–4 items
- Must consider:
  - Increased intensity risks
  - Fatigue patterns
  - Overtraining possibility

Strict Constraints:
- Output must be valid JSON only
- No extra text or formatting
- No additional fields

Now generate the JSON response.
""",
                activity.getId(),
                activity.getUserId(),
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }

    private void processAIResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // ✅ Step 1: Parse outer response
            JsonNode rootNode = mapper.readTree(aiResponse);

            // ✅ Step 2: Extract actual JSON string
            String jsonText = rootNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // ✅ Clean markdown if present
            jsonText = jsonText
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

            log.info("Extracted JSON: {}", jsonText);

            // ✅ Step 3: Parse actual JSON
            JsonNode root = mapper.readTree(jsonText);

            // ✅ Extract fields
            String activityId = root.path("activityId").asText();
            String userId = root.path("userId").asText();
            String activityType = root.path("activityType").asText();

            JsonNode analysisNode = root.path("analysis");

            String analysisText =
                    "Overall: " + analysisNode.path("overall").asText() + "\n" +
                            "Pace: " + analysisNode.path("pace").asText() + "\n" +
                            "Heart Rate: " + analysisNode.path("heartRate").asText() + "\n" +
                            "Calories: " + analysisNode.path("caloriesBurned").asText() + "\n" +
                            "Trend: " + analysisNode.path("trend").asText();

            List<String> improvements = new ArrayList<>();
            for (JsonNode node : root.path("improvements")) {
                improvements.add(
                        node.path("area").asText() + ": " +
                                node.path("recommendation").asText()
                );
            }

            List<String> suggestions = new ArrayList<>();
            for (JsonNode node : root.path("suggestions")) {
                suggestions.add(node.asText());
            }

            List<String> safety = new ArrayList<>();
            for (JsonNode node : root.path("safety")) {
                safety.add(node.asText());
            }

            Recommendation recommendation = Recommendation.builder()
                    .activityId(activityId)
                    .userId(userId)
                    .activityType(activityType)
                    .recommendation(analysisText)
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .build();

            log.info("Before saving recommendation...");

            Recommendation saved = recRepo.save(recommendation);

            log.info("Saved Recommendation: {}", saved);

        } catch (Exception e) {
            log.error("Error processing AI response", e);
        }
    }


}
