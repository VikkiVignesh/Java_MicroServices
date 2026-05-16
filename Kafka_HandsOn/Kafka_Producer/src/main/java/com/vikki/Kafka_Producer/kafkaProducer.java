package com.vikki.Kafka_Producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class kafkaProducer {

    private final KafkaTemplate<String,RiderLocation> kafkaTemplate;

    public kafkaProducer(KafkaTemplate<String, RiderLocation> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message)
    {
        RiderLocation location=new RiderLocation("rider123",23.56,77.89);
        kafkaTemplate.send("ride-topic",location);
        return "Message Sent : "+ location.getRiderId();
    }
}
