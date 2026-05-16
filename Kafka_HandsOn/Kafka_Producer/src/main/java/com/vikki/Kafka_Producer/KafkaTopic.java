package com.vikki.Kafka_Producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic createMyTopic()
    {
        return new NewTopic("ride-topic",3,(short) 1);
    }
}