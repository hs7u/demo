package com.batch.demo.service;

import java.util.List;

import com.batch.demo.vo.vo.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PubSubService {
    public Mono<List<Message>> getMessages();
    public Mono<Message> createMessage(String empName, String message);
    public Flux<Message> getMessageFlux();
}
