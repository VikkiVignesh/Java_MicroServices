package com.fitness.activityservice.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    //Below code si for registring the queue in the rabbit mq server with durable true condition  means it should stay in the  rabbit mq even if restarts
    @Bean
    public Queue activityQueue()
    {
        return new Queue("activity.queue",true);
    }

    @Bean
    public DirectExchange activityExchange()
    {
        return new DirectExchange("fitness.exchange");
    }

    @Bean
    public Binding activityBinding(Queue activityQueue,DirectExchange activityExchange)
    {
        return BindingBuilder.bind(activityQueue).to(activityExchange).with("activity.tracking");
    }

    //jackson is for convert message into  json
    @Bean
    public MessageConverter jsonMessageConverter()
    {
        return new JacksonJsonMessageConverter();
    }
}
