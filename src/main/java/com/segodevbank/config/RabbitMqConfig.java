package com.segodevbank.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${sample.rabbitmq.exchange}")
    private String exchange;

    @Value("${sample.rabbitmq.queue}")
    private String queueName;

    @Value("${sample.rabbitmq.routingKey}")
    private String routingKey;

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    Queue firstStepQueue() {
        return new Queue(queueName, false);
    }

    @Bean
    Queue secondStepQueue() {
        return new Queue("secondStepQueue", false);
    }

    @Bean
    Queue thirdStepQueue() {
        return new Queue("thirdStepQueue", false);
    }

    @Bean
    Binding firstBinding(Queue firstStepQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(firstStepQueue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    Binding secondBinding(Queue secondStepQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(secondStepQueue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    Binding thirdBinding(Queue thirdStepQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(thirdStepQueue)
                .to(exchange)
                .with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

  //asagidaki kod opsiyonel mi degil mi daha kod calisirken anlasilacaktir!!!
  /*  @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }*/

}
