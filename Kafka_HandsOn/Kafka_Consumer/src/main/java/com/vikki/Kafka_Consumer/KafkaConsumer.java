package com.vikki.Kafka_Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "my-topic", groupId = "my-group")
    public void listen(String message)
    {
        System.out.println("Recieved message --- "+message);
    }

    @KafkaListener(topics = "ride-topic", groupId = "rider-group")
    public void listen1(RiderLocation location)
    {
        System.out.println("Recieved Rider Info --- "+location.toString());
    }
}
